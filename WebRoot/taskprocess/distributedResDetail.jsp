<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>分配资源明细</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
<script type="text/javascript">
	$(function() {
		showTab();
	});
	
	var dt = new DataGrid();
	function showTab() {
		var taskId = $("#taskId").val();
		var userId = $("#userId").val();
		dt.setConditions([]);
		dt.setPK('objectId');
		dt.setSortName('create_time');
		dt.setURL('${path}/taskProcess/queryResourcesByTaskIdAndProcessor.action?taskId=' + taskId + "&userId=" + userId);
		dt.setSortOrder('desc');
		dt.setSelectBox('objectId');
		dt.setColums([ 
		      <app:QueryListColumnTag />
			//{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}
		]
		);
		accountHeight();
		dt.initDt();
		dt.query();
	}
	
	$creatorNameDesc = function(value, rec){
		var userId = rec.creator;
		var _url = "${path}/resOrder/getCreatorName.action?creatorId=" + userId;
		var creatorName = "";
		$.ajax({
			url: _url,
			type: 'get',
			async: false,
			success: function(data){
				creatorName = data;
			}
		});
		return creatorName;
	}
	
	function returnList() {
		location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
	}

</script>
</head>
<body>
<!-- 	<div id="fakeFrame" class="container-fluid by-frame-wrap "> -->
<!-- 		<div class="panel panel-default"> -->
<!-- 			<div class="tab-pane" id="tab_1_1_2"> -->
<!-- 				<div class="portlet"> -->
<!-- 					<div class="portlet-title"> -->
<!-- 						<div class="caption">资源明细</div> -->
<!-- 					</div> -->
					<div class="panel-body" id="999" style="width:765px;">
						<div id="data_div" class="data_div" ></div>
					</div>
<!-- 				</div> -->
<!-- 			</div> -->
<!-- 		</div> -->
<!-- 	</div> -->
	<input id="taskId" type="hidden" value="<%=request.getParameter("taskId")%>"/>
	<input id="userId" type="hidden" value="<%=request.getParameter("processorId")%>"/>
</body>
</html>