<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>

<html>
	<head>
    	<title>设置字段属性范围</title>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
		<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>        
        <script type="text/javascript"
	src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
<script type="text/javascript"
	src="${path}/search/js/bootstrap-select.js"></script>
	<script type="text/javascript"
	src="${path}/appframe/util/appDataGrid.js"></script>
<link rel="stylesheet" type="text/css"
	href="${path}/search/css/bootstrap-select.css" />
	<script type="text/javascript" src="${path}/search/js/map.js"></script>
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
			dt.setColums([ <app:QueryListColumnTag/>
			{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}]);
			accountHeight();
			dt.initDt();
			dt.query();
		});
		
		
	
        </script>
    </head>
	<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            <a href="###">系统管理</a>
			        </li>
			        <li>
			            <a href="###">用户管理</a>
			        </li>
			        <li>
			            <a href="###">设置字段属性范围</a>
			        </li>					
				</ul>
			</div>
			<div class="portlet">
				<div class="portlet-title" style="width: 100%">
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
    </body>
</html>