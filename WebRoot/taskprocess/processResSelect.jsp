<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
	<head>
		<title>加工任务管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <link rel="stylesheet" href="${path}/search/css/bootstrap-select.css"/>
        <link rel="stylesheet" href="${path}/search/css/modify.css"/>
        <link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
        <script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
        <script type="text/javascript" src="${path}/bres/res_operate.js"></script>
        <script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
        <script type="text/javascript" src="${path}/search/js/bootstrap-select.js"></script>
        <script type="text/javascript" src="${path}/search/js/map.js"></script>
        <script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
        <script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
		<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
		<script type="text/javascript">
		var dt = new DataGrid();
		$(document).ready(function(){
			var publishType = $("#resType").val();
			directCreateQueryCondition(publishType);
			modifyCss("resSelect");
			$("#queryFrame").selectpicker('refresh');
			dt.setConditions([]);
			dt.setPK('objectId');
			dt.setSortName('objectId');
			dt.setURL('${path}/search/queryFormList.action?publishType=' + publishType +"&params=");
			dt.setSortOrder('desc');
			dt.setSelectBox('objectId');
			dt.setColums([ <app:QueryListColumnTag/>]);
			accountHeight();
			dt.initDt();
			dt.query();
			
			if($("#queryCondtionButton").length>0){
				$('#queryCondtionButton').on('click', function() {
					var params = getAllQueryConditions();
					//params = encodeURI(params);
					queryForm(params);
				});
			};
			$("select[type=multiSelEle]").multipleSelect({
				multiple: true,
				multipleWidth: 80,
				width: "90%",
				//onBlur: function(){alert("onBlur");},
				onClick: optCheckChangeListener
			});
			
			$("label[name=multiSelEleName]").css("margin-right", "0px");
			$("label[name=timeShowLabel]").css("margin-right", "-68px");
			$(".inputVal .form-control").css({
				"width":"150px"
			}); 
			$(".inputVal").css({
				"margin-right":"20px;"
			}); 
	});
	
	function addRes(param){
		var queryConditions = getAllQueryConditions();
		if(param=="all"){
			//异步请求数据
			$.ajax({
				type : "post",
				url : '${path}/taskProcess/saveAllResByCondition.action?taskId='+$("#taskId").val()+"&conditions="+queryConditions,//提供数据的Servlet
				beforeSend: function () {
					saveResWaiting = $.waitPanel("数据正在处理中，请稍候！",false);
			    },		
				success : function(data) {
					if(data!=-1){
						saveResWaiting.close();
						var parentWin =  top.index_frame.work_main;
						parentWin.freshDataTable('data_div_res');
						$.closeFromInner();
					}
				},
				error : function() {
					//alert("请求超时，请重试！");
				}
			});
			
/*			$.post("${path}/taskProcess/saveAllResByCondition.action", {
				conditions: queryConditions,
				taskId:	$("#taskId").val()
			},function(data){
				if(data!=-1){
					var parentWin =  top.index_frame.work_main;
					parentWin.freshDataTable('data_div_res');
					$.closeFromInner();
				}
				//location.href = "${path}/taskprocess/taskprocesslist.jsp";
			}); */
		}else{
			var resources = getChecked('data_div','objectId').join(',');
			if(resources==""){
				$.alert("请选择要添加的资源！");
				return;
			}else{
				//alert(resources);
				$.post("${path}/taskProcess/saveResource.action", {
					resources:		resources,
					taskProcessId:	$("#taskId").val()
				},function(data){
					if(data!=-1){
						var parentWin =  top.index_frame.work_main;
						parentWin.freshDataTable('data_div_res');
						$.closeFromInner();
					}
					//location.href = "${path}/taskprocess/taskprocesslist.jsp";
				}); 
			}
		}
		/* var resources = getChecked('data_div','objectId').join(',');
		$.post("${path}/taskProcess/saveResource.action",{
			resources:		resources,
			taskProcessId:	$("#taskProcessId").val()
		},function(data){
			if(data!=-1)
				location.href = "${path}/taskprocess/taskprocesslist.jsp";
		}); */
		
	}
	
	/* function queryForm(params) {
		dt.dataGridObj.datagrid({
					url : "${path}/search/queryFormList.action?publishType=1&params="+params
				}); 
	}
	
	function getQueryCondition(){
        var conditionSize = $("select[name=queryField]").size();
		var publishType = $("#publishType").val();
		var flag = $("#flag").val();
		for(var i=1;i<=conditionSize;i++){
			var count = "";
			if(i<10){
				count = "0" + i;
			}
			lineCount--;
			$("#condition"+count).remove();
		}
		directCreateQueryCondition(publishType,flag);
		var params = getAllQueryConditions();
		queryForm(params);
  
	}; */
	
	/* function returnList(){
		location.href = "${path}/taskprocess/taskprocesslist.jsp";
	} */
	
	function queryForm(params) {
		//alert(params);
		dt.dataGridObj.datagrid({
			//该方法在 SearchIndexAction 中
			url : "${path}/search/queryFormList.action?publishType="+$("#resType").val()+"&params="+encodeURIComponent(params)
		}); 
	}
	
	function closeWindow(){
		var parentWin =  top.index_frame.work_main;
		parentWin.freshDataTable('data_div_res');
		$.closeFromInner();
	}
	</script>
	<style type="text/css">
		.inputVal {
			width: 200px;
		}
	
	</style>
	</head>
	<body>
		<%-- <div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<ul class="page-breadcrumb breadcrumb">
	        <li>
	            <a href="###">加工管理</a>
	            <i class="fa fa-angle-right"></i>
	        </li>		
	        <li>
	            <a href="###">加工单管理</a>
	            <i class="fa fa-angle-right"></i>
	        </li>
	        <li>
	            <a href="###">添加资源</a>
	            <i class="fa fa-angle-right"></i>
	        </li>	        			
		</ul>
			<div class="portlet portlet-border">
				<div class="portlet-title">
					<div class="caption">选择资源</div>
				</div>
				<div class="portlet-body">
					<form id="myForm">
					<div class="portlet-body" id="queryFrame">
					<div class="container-fluid" id="queryCondition"></div>	
					</div>		
					</form>	
					<p></p>
					<div class="portlet portlet-border" style="margin-left:10px;margin-right:10px;">
										<div class="panel-body">
					<div id="data_div" class="data_div height_remain" 
						style="margin-left:200px; width: 1100px; height:auto;"></div>
						</div>
						</div>
				</div>					
			</div>
			<div class="form-actions col-xs-offset-3">
				<button type="button"  class="btn btn-lg red" onclick="saveResource()" >提交</button>
				<button type="button"  class="btn btn-lg blue" onclick="returnList()" >返回</button>
			</div>	
			<input type="hidden" id="taskProcessId" value="${param.taskId}"/>
	</div> --%>
	
		<div style="overflow-x:auto;">
		<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: auto;">
			<div class="panel panel-default" style="height: auto;">
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
			<div align="left">
				<input type="hidden" name="token" value="${token}" />
				<button type="button" class="btn btn-primary"
					onclick="addRes('all');">添加全部页</button>
				&nbsp;
				<button type="button" class="btn btn-primary" id="part"
					onclick="addRes('current');">添加当前页</button>
				&nbsp;
				<button type="button" class="btn btn-primary"  
					onclick="closeWindow();" id="cancel">关闭</button>
					
			</div> 
			<br/>
			<div class="portlet portlet-border" >
										<div class="panel-body">
					<div id="data_div" class="data_div height_remain" 
						style="margin-left:200px; width: 1120px; height:auto;"></div>
						</div>
						</div>
		</div>
	</div>
	<input type="hidden" id="taskId" value="${param.taskId}"/>
	<input type="hidden" id="resType" value="${param.resType}"/>
	</body>
</html>