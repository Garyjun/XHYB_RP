<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>取消分配资源</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
<script type="text/javascript">
var dt = new DataGrid();
$(function(){
	var taskId = $("#taskId").val();
	var processorId = $("#processorId").val();
	dt.setConditions([]);
	dt.setDivId('data_div');
	dt.setPK('id');
	dt.setSortName('id');
	dt.setURL('${path}/taskProcess/queryRelationByTaskIdAndProcessor.action?taskId=' + taskId + "&processorId=" + processorId);
	dt.setSortOrder('desc');
	dt.setSelectBox('id');
	dt.setColums([ 
	      { title:'资源名称', field:'resName', width:fillsize(0.4), align:'center', sortable:true},
		  { title:'状态', field:'taskDetail.status', width:fillsize(0.15), align:'center', formatter:$statusDesc}
		]
	);
	accountHeight();
	dt.initDt();
	dt.query();

	});
	
	$statusDesc = function(value, rec){
		if(rec.status==0){
			return "未分配";
		}else if(rec.status==1){
			return "已分配";
		}else if(rec.status==2){
			return "已完成";
		}
	}
	
	function removeSelectedResToMaker(){
		var status = getChecked('data_div','status').join(',');
		if(status.indexOf("2") != -1){
			$.alert('选中资源包含已完成资源，请重新选择！');
			return;
		}
		
		var processorId = $("#processorId").val();
		var taskId = $("#taskId").val();
		var resIds = getChecked('data_div','sysResDirectoryId').join(',');
		if (resIds == '') {
			$.alert('请选择要取消分配的资源！');
			return;
		} else{
			var _url = "${path}/taskProcess/removeResToProcessor.action?taskId=" + taskId + "&processorId=" + processorId + "&resIds=" + resIds;
			$.ajax({
				url: _url,
				type: "get",
				success: function(data){
					if(data == "SUCCESS"){
						queryForm();
						var parentWin =  top.index_frame.work_main;
						parentWin.freshDataTable('data_div_user');
					}
				}
			});
		}
	}

	function queryForm() {
		var taskId = $("#taskId").val();
		var processorId = $("#processorId").val();
		dt.dataGridObj.datagrid({
			url : "${path}/taskProcess/queryRelationByTaskIdAndProcessor.action?taskId=" + taskId + "&processorId="+processorId
		});
	}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap ">
		<div style="margin-left:-10px;">
			<button type="button" class="btn btn-primary"
				onclick="removeSelectedResToMaker();" style="float:left;">取消分配</button>
			</div>
			 <br />
			<div id="dt"></div>
			<div id="dt-pag-nav"></div>
		</div>
	</div>
	<div class="panel-body" id="999" style="width:765px;">
		<div id="data_div" class="data_div" ></div>
	</div>
	<input id="taskId" type="hidden" value="<%=request.getParameter("taskId")%>"/>
	<input id="processorId" type="hidden" value="<%=request.getParameter("processorId")%>"/>
</body>
</html>