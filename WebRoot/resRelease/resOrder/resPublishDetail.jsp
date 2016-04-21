<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<%@ page import="com.brainsoon.resrelease.support.ResReleaseConstant"%>
<%@ page import="com.brainsoon.resrelease.support.ResReleaseConstant.OrderStatus"%>
<%@ page import="com.brainsoon.system.support.SystemConstants"%>
<%@ page import="com.brainsoon.system.support.SystemConstants"%>
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
		var orderId = $("#orderId").val();
		var resId = $("#resId").val();
		//alert("orderId:" + orderId + "   resId:" + resId);
		//定义一datagrid
		var _divId = 'data_div';
		var _url = '${path}/resOrder/resourceDetail.action?orderId=' + orderId + "&resId=" + resId;
		var _pk = 'id';
		var _conditions = [];
		var _sortName = 'modifiedTime';
		var _sortOrder = 'desc';
		var _colums = [ 
			{field : 'name',title : '文件名称',width : fillsize(0.2),sortable : true, align:'center'}, 
			{field : 'fileType',title : '文件类型',width : fillsize(0.17),sortable : true, align:'center'}, 
			{field : 'creator',title : '制作者',width : fillsize(0.07),sortable : true, align:'center'}, 
			{field : 'create_time',title : '创建时间',width : fillsize(0.1),sortable : true, align:'center'}
		];
		$grid1 = $.datagrid(_divId, _url, _pk, _colums, _conditions, _sortName,
				_sortOrder, false, false, true);
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
	<input id="orderId" type="hidden" value="<%=request.getParameter("orderId")%>"/>
	<input id="resId" type="hidden" value="<%=request.getParameter("resId")%>"/>
</body>
</html>