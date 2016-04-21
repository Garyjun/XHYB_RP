<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.brainsoon.system.support.SystemConstants.ResourceStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/Highcharts/js/highcharts.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/Highcharts/js/modules/exporting.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/Highcharts/js/highcharts-more.js"></script>
	<script type="text/javascript">
		var dt = new DataGrid();
		$(function(){
			dt.setConditions([]);
	   		dt.setSortName('id');
			dt.setSortOrder('desc');
			dt.setSelectBox('id');
			dt.setURL('${path}/statistics/releaseNum/list.action?channelName='+$("#channelName").val()+"&posttype="+$("#posttype").val()+"&filingDate_StartTime="+$("#filingDate_StartTime").val()+"&filingDate_EndTime="+$("#filingDate_EndTime").val());
			dt.setOnLoadSuccess('onLoadSuccess');
			dt.setColums([ {field:'channelName',title:'客户名称',width:fillsize(0.1),sortable:true,align:'center'},
							{field:'filingDate',title:'发布日期',width:fillsize(0.15),sortable:true,align:'center'},
							{field:'countNum',title:'数量',width:fillsize(0.1),sortable:true,align:'center'},
							{field:'opt1',title:'操作',width:fillsize(0.2),align:'center',formatter:$operate}]);
			accountHeight();
			dt.initDt();
			dt.query();
		});
		/***操作列***/
 		$operate = function(value,rec){
 			var opt = "";
 			var viewUrl='<sec:authorize url="/resRecord/detail.action"><a class=\"btn hover-red\"  href="javascript:view('+rec.releaseId+')"><i class=\"fa fa-sign-out\"></i>详细</a>&nbsp;</sec:authorize> ';
				opt = viewUrl;
 			return opt;
 		};
 		function view(orderId){
   			//$.openWindow("${path}/resRelease/view.action?id="+orderId, '发布资源信息', 900, 500);
   			parent.parent.layer.open({
   			    type: 2,
   			    title: '发布资源信息',
   			    closeBtn: true,
   			    maxmin: true, //开启最大化最小化按钮
   			    area: ['1200px', '500px'],
   			    shift: 2,
   			    content: "${path}/resRelease/view.action?id="+orderId+"&datetype=1"
   			});
   		}
		var onLoadSuccess = function(data){
			data = eval('('+data+')');
			if(data.statisticsNum==0) {
				$("#export").attr("disabled",true); 
			}
			$('#tnum').html(data.statisticsNum);
			getPieData();
		};
		
		function exportRes() {
			var codes = getChecked('data_div','id').join(',');
			if(codes != ''){
				location.href='${path}/statistics/releaseNum/exportRes.action?ids='+codes;
			}else{
				$.alert("请选择要导出的数据");
			}
		}
		
		function query(){
			dt.query();
		}
		function getPieData() {
			//饼状图定义
			$.ajax({
				url:'${path}/statistics/releaseNum/chart.action',
				dataType: "json",
				success: function(data){
					var d = [];
					$(data).each(function(n,item){
				        d.push([item.names,item.counts]);
				       })
				       if(d.length!=0){
					chart = new Highcharts.Chart(
							{
								//常规图表选项设置
								chart : {
									renderTo : 'test', //在哪个区域呈现，对应HTML中的一个元素ID
									plotBackgroundColor : null, //绘图区的背景颜色
									plotBorderWidth : null, //绘图区边框宽度
									plotShadow : false
								//绘图区是否显示阴影            
								},

								/* //图表的主标题
								title : {
									text :''
								}, */
								//当鼠标经过时的提示设置
								tooltip : {
									//pointFormat : '<b>{point.name}</b>: {point.percentage:.1f} %',
									/*  formatter: function() {
           							 return '<b>'+ this.point.name +'</b>: ('+
                         				Highcharts.numberFormat(this.y, 0, ',') +' 个)';
           							 }, */
									percentageDecimals : 1
								},
								//每种图表类型属性设置
								plotOptions : {
									//饼状图
									pie : {
										size:200,
										allowPointSelect : true,
										cursor : 'pointer',
										dataLabels : {
											enabled : true,
											color : '#000000',
											connectorColor : '#000000',
											//format:'<b>{point.name}</b>: {point.percentage:.1f} %'
											formatter:  function() {
			           							 return '<b>'+ this.point.name +'</b>: ('+
			                         				Highcharts.numberFormat(this.y, 0, ',') +' 个)';
			           							 }
										},
									}
								},
								//图表要展现的数据
								series : [ {
									type : 'pie',
									name : '数量',
									data :''
								} ]
							});
						if($('#posttype').val()=="1"){
							var title = {
							   	 	text:"需求单"
								};
							chart.setTitle(title);
						}else{
							var title = {
							    	text:"主题库"
								};
							chart.setTitle(title);
						}
						chart.series[0].setData(d);
				       }else{
						$('#test').append("<center style=\"margin-top: 30%;\">无数据</center>");
					}
				  }
			});
			

		  }
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">资源发布统计</a>
					</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;height: 10%;">
					<input type="hidden" id="posttype" name="resType" value="${param.posttype}" />
					<input type="hidden" id="channelName" name="channelName" value="${param.channelName}" />
					<input type="hidden" id="filingDate_StartTime" name="filingDate_StartTime" value="${param.filingDate_StartTime }" />
					<input type="hidden" id="filingDate_EndTime" name="filingDate_EndTime" value="${param.filingDate_EndTime }" />
					
					<div style="width: 30%;float: right;text-align: right;">
					<sec:authorize url="/statistics/releaseNum/exportRes.action">
					<input type="button" value="批量导出" id="export" class="btn btn-primary" onclick="exportRes()"/></sec:authorize>
						总数量：<span id="tnum">0</span>
					</div>
				</div>
				<div class="col-md-12" style="height: 85%">
	                <div class="row" style="height: 100%">
	                	<div class="col-md-6" style="height: 100%">
	                    	<div id="test" style="height: 100%"></div>
	                    </div>
	                    <div class="col-md-6" style="height: 100%;">
	             			<div id="data_div" class="data_div height_remain" style="width: 100%;height: 100%"></div>
	        			</div>
	                 </div>
				</div>
<!-- 				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div> -->
			</div>
		</div>
	</div>
</body>
</html>