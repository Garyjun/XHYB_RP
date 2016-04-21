<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
	<head>
		<title>发布记录管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
		<script type="text/javascript">
			$(function() {
				showTab();
			});
			
			function showTab() {
				//定义一datagrid
				var orderId = $("#orderId").val();
				var resId = $("#resId").val();
				//alert(orderId + "  " + resId);
				var _divId = 'data_div';
				var _url = "${path}/resRelease/publishResFileDetailQuery.action?resId=" + resId + "&orderId=" + orderId +"&posttype="+$('#posttype').val();
				var _pk = 'id';
				var _conditions = [];
				var _sortName = '';
				var _sortOrder = 'desc';
				var _colums = [ 
					{ title:'资源名称', field:'fileName' ,width:fillsize(0.3), align:'center'},
				  	{title:'加工状态（拷贝）', field:'processStatusDesc' ,width:fillsize(0.3), align:'center'},
				  	{title:'加工备注（拷贝&水印）',field:'processRemark',width:fillsize(0.4),align:'center'}
				];
				$grid1 = $.datagrid(_divId, _url, _pk, _colums, _conditions, _sortName,
						_sortOrder, false, true, true);
			}
		</script>
	</head>
	<body>
		<div class="panel-body" id="999" style="width:900px;">
			<div id="data_div" class="data_div" ></div>
		</div>
		<input type="hidden" id="posttype" value="<%= request.getParameter("posttype") %>" />
		<input type="hidden" id="resId" value="<%= request.getParameter("resId") %>" />
		<input type="hidden" id="orderId" value="<%= request.getParameter("orderId")%>" />
	</body>
</html> 