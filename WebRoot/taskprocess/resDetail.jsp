<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>需求单明细</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript" src="${path}/resRelease/resOrder/templeteSelect.js"></script>
<script type="text/javascript">
	$(function() {
		showTab();
	});
	
	//
	function showTab() {
		var taskId = $("#taskId").val();
		var resId = $("#resId").val();
		//alert("orderId:" + orderId + "   resId:" + resId);
		//定义一datagrid
		var _divId = 'data_div';
		var _url = '${path}/taskProcess/resourceDetail.action?taskId=' + taskId + "&resId=" + resId;
		var _pk = 'id';
		var _conditions = [];
		var _sortName = 'modifiedTime';
		var _sortOrder = 'desc';
		var _colums = [ 
			{field : 'name',title : '文件名称',width : fillsize(0.2),sortable : true, align:'center'}, 
			{field : 'fileType',title : '文件类型',width : fillsize(0.17),sortable : true, align:'center'}, 
			{field : 'creator',title : '制作者',width : fillsize(0.07),sortable : true, formatter:$creatorNameDesc, align:'center'}, 
			{field : 'create_time',title : '创建时间',width : fillsize(0.1),sortable : true, align:'center'}
		];
		$grid1 = $.datagrid(_divId, _url, _pk, _colums, _conditions, _sortName,
				_sortOrder, false, false, true);
	}
	
	$creatorNameDesc = function(value, rec){
		var userId = rec.creator;
		var creatorName = "";
		if(userId==null||userId==""||userId==undefined){
			
		}else{
			var _url = "${path}/resOrder/getCreatorName.action?creatorId=" + userId;
			$.ajax({
				url: _url,
				type: 'get',
				async: false,
				success: function(data){
					creatorName = data;
				}
			});
		}
		return creatorName;
	}
	
	function returnList() {
		//location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
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
	<input id="resId" type="hidden" value="<%=request.getParameter("resId")%>"/>
</body>
</html>