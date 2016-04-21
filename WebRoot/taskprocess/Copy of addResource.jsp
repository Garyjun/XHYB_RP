<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
	<head>
		<title>加工任务管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <link rel="stylesheet" href="${path}/search/css/bootstrap-select.css"/>
        <link rel="stylesheet" href="${path}/search/css/modify.css"/>
        <script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
        <script type="text/javascript" src="${path}/bres/res_operate.js"></script>
        <script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
        <script type="text/javascript" src="${path}/search/js/bootstrap-select.js"></script>
        <script type="text/javascript" src="${path}/search/js/map.js"></script>
        <script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
		<script type="text/javascript">
		var dt = new DataGrid();
		$(document).ready(function(){
		directCreateQueryCondition();
		$("#queryFrame").selectpicker('refresh');

		dt.setConditions([]);
		dt.setPK('objectId');
		dt.setSortName('objectId');
		dt.setURL('${path}/search/queryFormList.action?publishType='+"&params=");
		dt.setSortOrder('desc');
		dt.setSelectBox('objectId');
		dt.setColums([ <app:QueryListColumnTag/>]);
		accountHeight();
		dt.initDt();
		dt.query();
		
		if($("#queryCondtionButton").length>0){
			$('#queryCondtionButton').on('click', function() {
				var params = getAllQueryConditions();
				queryForm(params);
			});
		};
		 
	});
	
	function saveResource(){
/*  		var makers = "";
		$('input[name="makers"]:checked').each(function(){
			makers += $(this).val() + ",";
		});
		$("#makerIds").val(makers);  */
		var resources = getChecked('data_div','objectId').join(',');
		$.post("${path}/taskProcess/saveResource.action",{
			resources:		resources,
			taskProcessId:	$("#taskProcessId").val()
		},function(data){
			if(data!=-1)
				location.href = "${path}/taskprocess/taskprocesslist.jsp";
		});
	}
	
	function queryForm(params) {
		dt.dataGridObj.datagrid({
					url : "${path}/search/queryFormList.action?publishType=1&params="+params
				}); 
	}
	
	function getQueryCondition(){
		/* var publishType = $("#publishType").val();
		directCreateQueryCondition(publishType); */
        var conditionSize = $("select[name=queryField]").size();
		var publishType = $("#publishType").val();
		//alert("查询条件个数： "+publishType + "   "+ flag);
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
  
	};
	
	function returnList(){
		location.href = "${path}/taskprocess/taskprocesslist.jsp";
	}
		</script>
	</head>
	<body>
		<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
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
<!-- 				<div class="portlet-body"> -->
					<from id="myForm">
					<div class="portlet-body" id="queryFrame">
					<div class="container-fluid" id="queryCondition"></div>	
					</div>		
					</from>	
					<div id="data_div" class="data_div height_remain" style="width: 98%;"></div>
<!-- 				</div>					 -->
			</div>
<%-- 			<div class="portlet portlet-border">
				<div class="portlet-title">
					<div class="caption">添加用户</div>
				</div>
				<div class="portlet-body">
					<div class="col-xs-9">
						<c:forEach items="${makers}" var="maker" varStatus="status">
							<label class="checkbox-inline">
								<input type="checkbox" name="makers" value="${maker.id}"/>${maker.userName}
							</label>
							<c:if test="${status.index !=0 && status.index % 9 == 0}">
								<br />
							</c:if>
							<c:if test="${status.isLast()}">
								<br/>
							</c:if>						
						</c:forEach>
					</div>
				</div>					
			</div> --%>	
			<div class="form-actions col-xs-offset-3">
				<button type="button"  class="btn btn-lg red" onclick="saveResource()" >提交</button>
				<button type="button"  class="btn btn-lg blue" onclick="returnList()" >返回</button>
			</div>	
			<input type="hidden" id="taskProcessId" value="${taskProcessId}"/>
	</div>
	</body>
</html>