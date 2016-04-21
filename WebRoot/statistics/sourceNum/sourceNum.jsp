<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>资源统计</title>
<script type="text/javascript"
	src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
<link rel="stylesheet"
	href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" />
<script type="text/javascript"
	src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/Highcharts/js/highcharts.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/Highcharts/js/modules/exporting.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/Highcharts/js/highcharts-more.js"></script>
<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
<script type="text/javascript">

    var dt = new DataGrid();
    var chart;
    var chartPie=[];
	$(function() {
		queryMedata();
		dt.setPK('type');
		dt.setConditions([]);
		dt.setSortName('type');
		dt.setSortOrder('desc');
		dt.setSelectBox('type');
		dt.setURL('${path}/statistics/sourceNum/queryFormList.action?sysResMetadataTypeId=');
		dt.setOnLoadSuccess('onLoadSuccess');
		dt.setColums([{
			title : '资源类型',
			field : 'typeName',
			width : fillsize(0.2),
			align : 'center'
		}, {
			title : '数量',
			field : 'count',
			width : fillsize(0.2),
			align : 'center',
			sortable : true
		},{field:'opt1',title:'操作',width:fillsize(0.2),align:'center',formatter:$operate}]);
		accountHeight();
		dt.initDt();
		dt.query();
		
		

		chart = new Highcharts.Chart({
			chart : {
				renderTo : 'test', //放置图表的容器
			   /*  events : {
				      load : st// 定时器
				}, */
				plotBackgroundColor : null,
				plotBorderWidth : null,
				defaultSeriesType : 'column', //图表类型line, spline, area, areaspline, column, bar, pie , scatter 
			},
			title : {
				text : '资源统计柱状图',
			},
			xAxis : {//X轴数据
				categories : '',
				lineWidth : 2,
				labels : {
					rotation : -45, //字体倾斜
					align : 'right',
					style : {
						font : 'normal 13px 宋体'
					}
				}
			},
			yAxis : {//Y轴显示文字
				lineWidth : 2,
				title : {
					text : '资源统计数量',
				}
			},
			tooltip : {
				formatter : function() {
					return '<b>' + this.x + '</b><br/>' + this.series.name
							+ ': ' + Highcharts.numberFormat(this.y, 0);
				}
			},
			plotOptions : {
				column : {
					dataLabels : {
						enabled : true,
					},
					enableMouseTracking : true,//是否显示title
				}
			},
			series : [ {
				name : '资源数量',
				data : '',
			} ]
			
		});
			
		getPieData();
	});
	
	$operate = function(value,rec){
		var opt= "<sec:authorize url="/publishRes/detail.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.type+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a></sec:authorize>";
		return opt;
	};
	
	function detail(objectId){
		$.openWindow("${path}/publishRes/openList.jsp?publishType="+objectId+"&path="+$("#paths").val()+"&fieldName="+fieldName+
				"&startTime="+ $("#startTime").val()+"&endTime="+$("#endTime").val(),'资源统计详细页',1000,550);
	}
	
	var onLoadSuccess = function(data){
		var Ydata = [];
		var Xdata = [];	
		data = eval('('+data+')');
		var categories = data.rows;
		for(i=0;i<categories.length;i++) {
			var count = categories[i].count;
			if(count!=null) {
				Ydata.push(count);
			}
			
			var name =  categories[i].typeName;
			if(name!=null) {
				Xdata.push(name);
			} 
		} 
		if(data.statisticsNum==0) {
			$("#downTemplate").attr("disabled",true); 
		}else {
			$("#downTemplate").attr("disabled",false); 
		}
		$('#tnum').html(data.statisticsNum);
		getData(Ydata,Xdata);
	};
	
	

	  //动态更新图表数据
	  function getData(Ydata,Xdata) {
	   chart.series[0].setData(Ydata);
	   chart.xAxis[0].setCategories(Xdata);
	  }
	 function getPieChildData(type,data) {
		  $.each(JSON.parse(data), function(i, d) {
				if(d.type==type) {
					var typeInt =parseInt(type);
					browsers[typeInt].push([d.fileType,d.count]);
				}
			});
	  }
	  
	 
	 function qcType(typeCount) {
		 for(var i=0;i<typeCount.length;i++){
			 for(var j=i+1;j<typeCount.length;j++) {
				 if(typeCount[i]==typeCount[j]) {
					 typeCount.splice(j,1);
					 j--;
				 }
			 }
		 }
	 }
	 
	  function getPieData() {
		//异步请求数据
			$.ajax({
				type : "post",
				url : '${path}/statistics/sourceNum/queryPieList.action',//提供数据的Servlet
				success : function(data) {
					//定义一个数组
					//迭代，把异步获取的数据放到数组中
					browsers = [];
					typeCount = [];
					$.each(JSON.parse(data), function(i, d) {
						typeCount.push(d.type);
					});
					 qcType(typeCount);
					 for(var i=0;i<typeCount.length;i++) {
						var index =parseInt(typeCount[i]);
						browsers[index] = [];
						getPieChildData(typeCount[i],data);
					}
					var json = JSON.parse(data);
					var number = typeCount.length;
					var count = 0;
					if(number%2==0){
						count = number/2;
					}else{
						count = parseInt(number/2+1);
					}
					for(var i=0;i<count;i++) {
						var num = i*2;
						var numT = i*2+1;
						var divRow = $("<div class='row'> <div class='col-md-6' id='container"+num+"' style='width: 600px; height: 350px'></div>"
									+"<div class='col-md-6' id='container"+numT+"' style='width: 600px; height: 320px;'></div></div>");
								divRow.appendTo($("#pieHome"));
					}
					for(var i=0;i<number;i++) {
						//饼状图定义
						chartPie[i] = new Highcharts.Chart(
								{
									//常规图表选项设置
									chart : {
										renderTo : 'container'+i+'', //在哪个区域呈现，对应HTML中的一个元素ID
										plotBackgroundColor : null, //绘图区的背景颜色
										plotBorderWidth : null, //绘图区边框宽度
										plotShadow : false
									//绘图区是否显示阴影            
									},

									//图表的主标题
									title : {
										text : ''+$("#"+typeCount[i]).attr("pieTitle")+''
									},
									//当鼠标经过时的提示设置
									tooltip : {
										//pointFormat : '{series.name}: <b>{point.percentage:.0f}</b>',
										pointFormat : '{series.name}: <b>{point.y}</b>',
										percentageDecimals : 1
									},
									//每种图表类型属性设置
									plotOptions : {
										//饼状图
										pie : {
											allowPointSelect : true,
											cursor : 'pointer',
											dataLabels : {
												enabled : true,
												color : '#000000',
												connectorColor : '#000000',
												formatter : function() {
													//Highcharts.numberFormat(this.percentage,2)格式化数字，保留2位精度
													//return '<b>'+ this.point.name+ '</b>: '+ Highcharts.numberFormat(this.percentage,0); //显示的是百分比
													return '<b>'+ this.point.name +'</b>: '+ Highcharts.numberFormat(this.y, 0, ',');//显示的是实际数目
												}
											},
									       /*  events: {
							                    click: function(e) {
							                    	alert(e.point.name);
							                    	alert(Highcharts.numberFormat(e.point.percentage,0));
							                    }
							                } */

										}
									},
									//图表要展现的数据
									series : [ {
										type : 'pie',
										name : '数量',
										data : ''
									} ]
								});
						//设置数据
						chartPie[i].series[0].setData(browsers[typeCount[i]]);
					};
				},
				error : function() {
					alert("请求超时，请重试！");
				}
			});
		
		
			
	  }
	
	function queryForm() {
		var idStr="";
		$('input[name="source"]:checked').each(function(){  
			idStr+= $(this).val()+","; 
		});
		dt.dataGridObj.datagrid({
					url : "${path}/statistics/sourceNum/queryFormList.action?sysResMetadataTypeId="
							+ idStr
							+ "&path="
							+ $("#paths").val()
							+ "&fieldName="
							+ fieldName
							+ "&startTime="
							+ $("#startTime").val()
							+ "&endTime="
							+ $("#endTime").val()
				}); 
	} 


	function queryMedata() {
		$.ajax({
					type : "post",
					url : "${path}/statistics/sourceNum/queryType.action",
					success : function(data) {
						var json = JSON.parse(data);
						pieName = JSON.parse(data);
					    var count = 0;
						for (var i = 0; i < json.length; i++) {
							count++;
							var jsonObj = json[i];
							var name = jsonObj.name;
							var id = jsonObj.id;
							var label = $("<label class='checkbox-inline form-group'></label>");
							label.appendTo($("#sourceType"));
							var br = $("<br/>");
							if (count % 5 == 0) {
								br.appendTo($("#sourceType"));
							}
							var input = $("<input name='source' value='"
									+ id
									+ "' type='checkbox' id='"+id+"' pieTitle='"+name+"' onclick='getTypeNum()'>"
									+ name + "</input>");
							input.appendTo(label);

						}
					}
				});
	}

	var typeValue = "";
	function getTypeNum() {
		var num = $('input[name="source"]:checked').length;
		typeValue = $('input[name="source"]:checked').val();
		var  newHtml = "<input type='radio' value=''/>无"
		$('#zdy').html(newHtml).show();;
		if (num != 1) {
			
		} else {
			$.ajax({
						type : "post",
						url : "${path}/statistics/sourceNum/queryGetDefiniton.action?pid="
								+ typeValue,
						success : function(data) {
							var json = JSON.parse(data);
							newHtml ='';
								for (var i = 0; i < json.length; i++) {
									var jsonObj = json[i];
									var name = jsonObj.name;
									showname = name;
									var id = jsonObj.id;
									var fieldName = jsonObj.fieldName;
									var idFieldName = id + "-" + fieldName;
									newHtml +="<input type='radio' value='"+ idFieldName +"' onclick='showTree()' name='node' title='"+name+"'/>"+name;
									//$('#searchFlag').html(html).show();
									$('#zdy').html(newHtml).show();
								}
						}
					});

		}
		
	}

	var fieldName = "";
	function showTree() {
		var idFieldName = $("input:radio:checked").val();
		var typeIds = idFieldName.split("-");
		var shownname = $("input:radio:checked").attr('title');
		var typeId = typeIds[0];
		fieldName = typeIds[1];
		$.openWindow("${path}/statistics/sourceNum/showTree.action?typeId="
				+ typeId, shownname, 700, 600);
		
	}
	
	function exportRes() {
		var codes = getCheckedIndex('data_div').join(',');
		location.href='${path}/statistics/sourceNum/exportRes.action?ids='+codes;
		/* if (codes == '') {
		    $.alert('请选择要导出的资源！');
		} else {
        location.href='${path}/statistics/sourceNum/exportRes.action?ids='+codes;
		} */
	}
	
	//清空资源统计柱状图根据查询条件查询出的信息
	function resetAndQuery() {
		$('input[name="source"]').attr("checked",false);
		
		//将自定义列表下拉框置为初始状态
		$("#zdy").html("<input type='radio' value=''/>无");
		
		$("#paths").val("");
		$("#codeName").val("");
		$("#startTime").val("");
		$("#endTime").val("");
		fieldName="";
		queryForm();
	}
	
</script>

</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap"
		style="width:100%">


		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">资源统计</a></li>
				</ol>
			</div>
			<div class="row" style="margin-left: 148px">
				<div class="form-group">
					<label class="control-label col-md-1">资源分类：</label>
					<div class="col-md-11" id="sourceType"></div>
				</div>
			</div>

			<div class="panel-body" id="999" >
				<div class="container-fluid form-horizontal">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">自定义列表：</label>
							<div class="col-md-8">
								<div id="zdy"></div>
								<textarea class="form-control" cols="80" rows="5" id="codeName"></textarea>
								<input type="hidden" id="paths" />
							</div>
						</div>
					</div>

					<div class="col-md-6" style="margin-left: 105px">
<!-- 						<div class="form-group"> -->
<!-- 							<div class="col-md-6"> -->
<!-- 								<div class="form-group"> -->
<!-- 									<label class="control-label col-md-4">开始时间：</label> -->
<!-- 									<div class="col-md-8"> -->
<!-- 										<input class="form-control Wdate" style="width: 150px" -->
<!-- 											onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'endTime\')}'})" -->
<!-- 											id="startTime" name="startDate" /> -->
<!-- 									</div> -->
<!-- 								</div> -->
<!-- 							</div> -->

<!-- 							<div class="col-md-6"> -->
<!-- 								<div class="form-group"> -->
<!-- 									<label class="control-label col-md-4">结束时间：</label> -->
<!-- 									<div class="col-md-8"> -->
<!-- 										<input class="form-control Wdate" style="width: 150px" -->
<!-- 											onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'startTime\')}'})" -->
<!-- 											id="endTime" name="endDate" /> -->
<!-- 									</div> -->
<!-- 								</div> -->
<!-- 							</div> -->
<!-- 						</div> -->
					</div>


					<div class="col-md-6" style="margin-left: 260px">
						<div class="form-actions">
							<input type="button" value="查询" class="btn btn-default red"
								style="width: 100px;" onclick="queryForm();" /> 
								<input type="button" value="清空" class="btn btn-default blue"
								style="width: 100px;" onclick="resetAndQuery();" /> 
								<sec:authorize url="/statistics/sourceNum/exportRes.action"><input
								id="downTemplate" style="width: 100px;" type="button"
								value="批量导出" class="btn btn-default blue"
								onclick="exportRes()" /></sec:authorize>
						</div>
					</div>
					<div class="col-md-12">
                          <div class="alert alert-info" role="alert"><span style="font-size:10px"><b>概览</b></span></div>
                          <div class="row">
                              <div class="col-md-6">
                                    <div id="test" style="height: 400px;"></div>
                              </div>
                              <div class="col-md-6">
                                   <div style="width: 30%;float: right;text-align: right;">
						                                                   总数量：<span id="tnum">0</span>
				                   </div>
				                   <div id="data_div" class="data_div height_remain" style="height: 400px;"></div>
				              </div>
                           </div>
					</div>
					<div id="pieHome" class="col-md-12">
                          <div class="alert alert-info" role="alert"><span style="font-size:10px"><b>分表统计</b></span></div>
                          <input id="numPie" type="hidden"/>
					</div>
					
				</div>
				<div >
				</div>
			</div>

		</div>

	</div>
</body>

</html>