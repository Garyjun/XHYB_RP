<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="com.brainsoon.system.support.SystemConstants.ResourceStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
	<script type="text/javascript">
	var dt = new DataGrid();
	$(function(){
		dt.setConditions([]);
		dt.setSortName('id');
		dt.setSortOrder('desc');
		dt.setSelectBox('id');
		dt.setURL('${path}/statistics/taskProcessNum/queryList.action?taskName='+$("#taskName").val()+"&processName="+$("#processName").val()+"&startTime="+$("#startTime").val()+"&endTime="+$("#endTime").val()+"&status="+$("#status").val());
		/* dt.setOnLoadSuccess('onLoadSuccess'); */
		dt.setColums([{field:'taskName',title:'任务名称',width:100, align:'center' ,sortable:true},
						{field:'processorNameDesc',title:'加工员名称',width:100, align:'center'},
						{field:'resNumDesc',title:'加工数量',width:100, align:'center'},
						{field:'processStatusDesc',title:'加工状态',width:100, align:'center'},
						{field:'startTime',title:'开始时间',width:100, align:'center',formatter:$formatDate},
						{field:'endTime',title:'结束时间',width:100, align:'center',formatter:$formatDate}]);
		accountHeight();
		dt.initDt(); 
	});
	/* $(function(){
		queryId();
	}); */
	
	function queryId() {
		var idList="";
		$.ajax({
			type:"post",
			url:"${path}/statistics/taskProcessNum/queryId.action",
			success:function(data){
				 var list = JSON.parse(data);
				 for(var i=0;i<list.length;i++){
						var listSon = list[i];
						var id = listSon.id;
						idList+=id+",";
				}
				$("#taskIdList").val(idList);
			}
		});
	}
	
	function exportRes() {
			var codes = getChecked('data_div','id').join(',');
			//导出当前页面被选中的数据
			if(codes !=''){
				location.href='${path}/statistics/taskProcessNum/exportRes.action?ids='+codes;
			//导出当前页面的所有数据
			}else{
				$.alert("请选择要导出的数据");
			}
			
		
		
		/* if (codes =='') {
			location.href='${path}/statistics/taskProcessNum/exportRes.action?ids='+$("#taskIdList").val();
		} else {
            location.href='${path}/statistics/taskProcessNum/exportRes.action?ids='+codes;
		} */
	}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">任务加工情况统计</a>
					</li>
				</ol>
			</div>
			
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
					<input type="hidden" id="taskName" name="taskName" value="${param.taskName}" />
					<input type="hidden" id="taskIdList"/>
					<input type="hidden" id="status" name="status" value="${param.processStatus}" />
					<input type="hidden" id="processName" name="processName" value="${param.processName}"/>
					<input type="hidden" id="endTime" name="endTime" value="${param.endTime}" />
					<input type="hidden" id="startTime" name="startTime" value="${param.startTime}" />
					<sec:authorize url="/statistics/taskProcessNum/exportRes.action"><input type="button" value="批量导出" id="export" class="btn btn-primary" onclick="exportRes()"/></sec:authorize>
					<!-- <div style="width: 30%;float: right;text-align: right;">
						总数量：<span id="tnum">0</span>
					</div> -->
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
			
		</div>
	</div>
</body>
</html>