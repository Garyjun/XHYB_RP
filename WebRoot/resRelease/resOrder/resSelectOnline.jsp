<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!-- 
	*根据Action传过来的数据
	*左边的目录树，node.flag属性：//_dir：目录， _file：文件
	*中间扩展名树，node.flag属性：//ExtensionNameDir：扩展名分类 ，ExtensionName：扩展名
	*右边的文件树，node.flag属性：//1：目录， 2：文件
	*
	*huangjun 2015年9月21日15:01:54
 -->
<html>
	<head>
		<title>可选资源列表</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
		<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
		<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css" />
		<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
		<script type="text/javascript" src="${path}/resRelease/resOrder/js/map.js"></script>
		<script type="text/javascript" src="${path}/resRelease/resOrder/js/list.js"></script>
		<script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
		<script type="text/javascript" src="${path}/search/js/bootstrap-select.js"></script>
		<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
		<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
		<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
		<script type="text/javascript" src="${path}/appframe/plugin/jxmenu/js/jquery-powerFloatEdit-min.js"></script>
		<script type="text/javascript">
			var orderId = "${orderId}";
			var dt = new DataGrid();
			$(function() {
				initializes();
				queryForm1();
			});
			function typeinit(){  
				initializes();
				queryForm1();
			}
			function initializes(){
				xmenuNum="";
				var publishType= $('#resType').val();
				directCreateQueryConditionAndRes(publishType);
				onLoadDict();   //加载数据字典项
				modifyCss();
				if(xmenuNum != "" && xmenuNum != null){
					xmenuNum = xmenuNum.substring(0,xmenuNum.length-1);
					var menuList = xmenuNum.split(",");
					for(var i=0;i<menuList.length;i++){
						var menu = menuList[i]+"()";
						eval(menu);
					}
				}
				 $("#queryFrame").selectpicker('refresh');
				/*$("select[type=multiSelEle]").multipleSelect({
					multiple: true,
					multipleWidth: 80,
					width: "90%",
					//onOpen: dyAddOpts,
					onClick: optCheckChangeListener
				});
				$("label[name=multiSelEleName]").css("margin-right", "-1px"); */
			}
			function onLoadDict(){
				var publishType = $("#resType").val();
				var flag = "";
				// 获得元数据定义字段,包括数据字典和数据字典包括的项
				var queryFields = creatFieldOpt(publishType, flag);
				var lineCount = 0;
				var newValues = "";
				var newCount = "";
				if(queryFields){
					var obj = JSON.parse(queryFields);
					$(obj).each(function(index) {
						++lineCount;
						var count = "";
						if(lineCount<10){
							count += "0" + lineCount;
						}
						var fieldValues = obj[index].fieldValues;
						var fieldType = obj[index].fieldType;
						if(fieldType == 3 || fieldType == 2 || fieldType == 4){
							newValues = newValues+",";
							newValues = newValues+fieldValues;
							newCount = newCount +",";
							newCount = newCount+count;
							//dictAddOpts(fieldValues,'search', null, "multiSelEle"+count);
						}
					});  
				}
				if(newValues.length>0 && newCount.length>0){
					var values = newValues.split(",");
					var counts = newCount.split(",");
					for(var i=0;i<values.length;i++){
						if(values[i]!="" && counts[i]!=""){
							//加载数据字典项，填充multiSelEle插件，原来所运用插件
							//dictAddOpts(values[i],'search', null, "multiSelEle"+counts[i]);
							//运用Xmenu插件
							dictQueryAddOpts(values[i],'search', null, "select_Value"+counts[i]);
						}
					}
				}
			}
			
			
			function closeWindow(){
				var parentWin =  top.index_frame.work_main1;
				var operateFrom = '${operateFrom}';
				//alert("关闭窗口"+ operateFrom);
				if(operateFrom=="TASK_LIST_PAGE"){
					parentWin =  top.index_frame.work_main;
				}
				parentWin.freshDataTable('data_div');
				//$.closeFromInner();
				var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
				parent.layer.close(index);
			}
			function queryForm(params) {
				dt.dataGridObj.datagrid({
					url : "${path}/search/queryFormLists.action?publishType="+$('#resType').val()+"&params="+params
				});
			}
			
			function queryForm1() {
				var publishType= $('#resType').val();
				window.onresize=findDimensions();
				var sizewidth=$('#tab_1_1_1').width();
				$('#data_div').width(sizewidth);
				dt.setConditions([]);
				dt.setPK('objectId');
				dt.setSortName('objectId');
				dt.setURL('${path}/search/queryFormLists.action?publishType='+publishType+"&params=");
				dt.setSortOrder('desc');
				dt.setSelectBox('objectId');
				dt.setColums([ <app:QueryListColumnTag/>
				{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}]);
				accountHeight();
				dt.initDt();
				dt.query();
				//为查询按钮添加点击事件
				if($("#queryCondtionButton").length>0){
					$('#queryCondtionButton').on('click', function() {
						//获取页面上输入的查询条件组合,和在查询条件中输入的值
						//var params = getAllQueryConditions();
						var params = getAllQueryFieldConditions();
						params = encodeURI(params);
						queryForm(params);
						$('#params').val(params);
					});
				/* if($("#queryCondtionButton").length>0){
					$('#queryCondtionButton').on('click', function() {
						//var params = getAllQueryConditions();
						var params = getAllQueryFieldConditions();
						queryForm(params);
						$('#params').val(params);
					});
				} */
				}
			}
			$operate = function(value,rec){
				var opt= "<sec:authorize url="/publishRes/openDetail.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a></sec:authorize>";
				return opt;
			};
			function detail(objectId){
				parent.parent.layer.open({
				    type: 2,
				    title: '资源详细',
				    closeBtn: true,
				    maxmin: true, //开启最大化最小化按钮
				    area: ['1200px', '600px'],
				    shift: 2,
				    content: "${path}/publishRes/openDetail.action?objectId="+objectId+"&searchFlag=close"+"&flagSta=3&datatype=1"
				});
			}
			//选择资源按钮
			function checkres(){
				var codes = getChecked('data_div','objectId').join(',');
				if (codes == '') {
					layer.alert('请选择资源！');
				}else{
					$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;正在处理中，请稍待。。。</div>',
						css: {
			                border: 'none',
			                padding: '20px',
			                backgroundColor: 'white',
			                textAligh:'center',
			            //    top:"50%",  
			                width:"300px",
			                opacity: .7
			               }
					});
					$.ajax({
						url:'${path}/resOrder/addres.action?resId=' + codes+'&orderId='+orderId+'&posttype='+$('#posttype').val()+'&restype='+$('#resType').val(),
						type:"post",
					    datatype: 'text',
					    success: function (data) {
					    	layer.alert("资源添加成功！");
					    	$.unblockUI();
					    	var parentWin =  top.index_frame.work_main1;
							var operateFrom = '${operateFrom}';
							if(operateFrom=="TASK_LIST_PAGE"){
								parentWin =  top.index_frame.work_main;
							}
							parentWin.freshDataTable('data_div');
							
					    }
					});
				}
			}
			function addRessByConditionAndDir(){
				var data=$('#data_div').datagrid('getData');
				$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;正在处理中，请稍待。。。</div>',
					css: {
		                border: 'none',
		                padding: '20px',
		                backgroundColor: 'white',
		                textAligh:'center',
		            //    top:"50%",  
		                width:"300px",
		                opacity: .7
		               }
				});
				$.ajax({
					url:'${path}/resOrder/addAllres.action?params='+$('#params').val()+'&total=' + data.total +'&orderId='+orderId+'&posttype='+$('#posttype').val()+'&restype='+$('#resType').val(),
					type:"post",
				    datatype: 'text',
				    success: function (data) {
				    	layer.alert("资源添加成功！");
				    	$.unblockUI();
				    	var parentWin =  top.index_frame.work_main1;
						var operateFrom = '${operateFrom}';
						if(operateFrom=="TASK_LIST_PAGE"){
							parentWin =  top.index_frame.work_main;
						}
						parentWin.freshDataTable('data_div');
				    }
				});
				
			}
			
			var winWidth = 0;
			var winHeight = 0; 
			function findDimensions(){  //函数：获取尺寸
				//获取窗口宽度 
				if (window.innerWidth){
					winWidth = window.innerWidth; 
				} else if ((document.body) && (document.body.clientWidth)){
					winWidth = document.body.clientWidth; 
				} 
				//获取窗口高度 
				if (window.innerHeight){ 
					winHeight = window.innerHeight; 
				} else if ((document.body) && (document.body.clientHeight)){ 
					winHeight = document.body.clientHeight; 
				}
				//通过深入Document内部对body进行检测，获取窗口大小 
				if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth){ 
					winHeight = document.documentElement.clientHeight; 
					winWidth = document.documentElement.clientWidth; 
				} 
				var valueWidth = winWidth.toString();
//		 		alert($('#data_div')+"   "+winWidth);
				$('#data_div').width(valueWidth);
			} 
			findDimensions(); 
			window.onresize=findDimensions; 
		</script>
	</head>
	<body>
	<div style="overflow-y:auto;">
		<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
			<div class="panel panel-default" style="height: 100%;">
			<input type="hidden" id="params" name="params" value=""/>
				 <div class="row" style="margin-top: 1%;">
					<div class="col-md-4">
						<label class="control-label col-md-2">资源类型：</label>
						<div class="col-md-10">
							<select  id="resType"  name="resType" onchange="typeinit();">
								<c:forEach items="${lists }" var="lists">
									<option value="${lists.id}">${lists.typeName}</option>
								</c:forEach>
							</select>
						</div>
					</div>
				</div>
				<div class="by-tab">
					<div class="portlet">
						<div class="portlet-title" style="width:100%">
							<div class="caption">
								隐藏查询条件 <a href="javascript:;" onclick="togglePortlet(this)"><i
									class="fa fa-angle-up"></i></a>
							</div>
						</div>
						 <form id="myForm">	
						<div class="portlet-body" id="queryFrame">
							<div class="container-fluid" id="queryCondition"></div>
						</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<div id="fakeFrame1" class="container-fluid by-frame-wrap " style="height: auto;">
			<div>
				<div align="right">
					<button type="button" class="btn btn-primary" onclick="checkres();">添加选中项</button>
					<input type="hidden" name="token" value="${token}" />
					<button type="button" class="btn btn-primary"
						onclick="addRessByConditionAndDir();">添加全部页</button>
					&nbsp;
					<button type="button" class="btn btn-primary"  
						onclick="closeWindow();" id="cancel">关闭</button>
				</div>  
			</div>
		</div>
		<div id="fakeFrame2" class="container-fluid by-frame-wrap " style="height: 100%;">
				<div class="panel-body">
        		<div class="tab-pane active" id="tab_1_1_1" style="width:100%">
					<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
				</div>
			</div>
		</div>
	</div>
	
	<input type="hidden" id="posttype" name="posttype" value="${posttype }"/>
	<input type="hidden" id="publishType" value="${param.type}"/>
	</body>
</html>