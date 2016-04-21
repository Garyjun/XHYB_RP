<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.brainsoon.system.support.SystemConstants.ResourceStatus"%>
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
		dt.setURL('${path}/statistics/sourceProcessNum/queryList.action?taskName='+$("#taskName").val()+"&processName="+$("#processName").val()+"&startTime="+$("#startTime").val()+"&endTime="+$("#endTime").val()+"&status="+$("#status").val()+"&resName="+$("#resName").val());
		/* dt.setOnLoadSuccess('onLoadSuccess'); */
		dt.setColums([{field:'resName',title:'资源名称',width:100, align:'center' ,sortable:true},
						/* {field:'sourceType',title:'资源类型',width:100, align:'center' ,sortable:true}, */
						{field:'taskDetail.makerName',title:'所属加工人',width:100, align:'center'},
						{field:'taskDetail.taskProcess.taskName',title:'所属加工任务',width:100, align:'center'},
						{field:'taskDetail.status',title:'资源状态',width:100, align:'center' ,formatter:$statusDesc},
						{field:'taskDetail.startTime',title:'开始时间',width:100, align:'center' ,formatter:$formatDate},
						{field:'taskDetail.endTime',title:'结束时间',width:100, align:'center' ,formatter:$formatDate}]);
		accountHeight();
		dt.initDt();
		//queryId();
	});
	
	/* var onLoadSuccess = function(data){
		data = JSON.parse(data);
		if(data.total==0){
			$("#export").attr("disabled",true); 
		}
	}; */
	$statusDesc = function(value,rec){
		if(rec.taskDetail.status==0)
			return "未加工";
		else if(rec.taskDetail.status==1)
			 return "未加工";
		 else if(rec.taskDetail.status == 2)
			   return "已完成";
		   else 
			  return "";
	}
	
	function query(){
		dt.query();
	}
	
	function queryId() {
		var idList="";
		$.ajax({
			type:"post",
			url:"${path}/statistics/sourceProcessNum/queryId.action",
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
	
	//采用Post方式提交
	function exportRes() {
		var codes = getChecked('data_div','id').join(',');
		var pageSize ="";
		if(codes != ''){
			//location.href='${path}/statistics/sourceProcessNum/exportRes.action?ids='+codes;
			downLoading = $.waitPanel('下载中请稍候...',false);	
			 $.ajax({
				url:'${path}/statistics/sourceProcessNum/exportRes.action?ids='+codes,
			    type: 'post',
			    datatype: 'json',
			    success: function (returnValue) {
			    	if(returnValue!=""){
			    		downLoading.close();
			    		window.location.href = '${path}/statistics/sourceProcessNum/getExportExcelDown.action?excelFilePath='+returnValue;
			    	}else{
			    		$.alert("文件下载数量超过数据字典定义大小，不能下载");
			    		downLoading.close();
			    	}
			    }
			}); 
		}else{
			//没有选中数据按页数导出数据
			
			$('#page').val(1); //设置默认为：第一页开始
			$("#myModal").modal('show');
		}
	}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">资源加工情况统计</a>
					</li>
				</ol>
			</div>
			
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
					<input type="hidden" id="resName" name="resName" value="${param.resName}" />
					<input type="hidden" id="taskIdList"/>
					<input type="hidden" id="taskName" name="taskName" value="${param.taskName}" />
					<input type="hidden" id="status" name="status" value="${param.processStatus}" />
					<input type="hidden" id="processName" name="processName" value="${param.processName}"/>
					<input type="hidden" id="endTime" name="endTime" value="${param.endTime}" />
					<input type="hidden" id="startTime" name="startTime" value="${param.startTime}" />
					<sec:authorize url="/statistics/sourceProcessNum/exportRes.action"><input type="button" value="批量导出" id="export" class="btn btn-primary" onclick="exportRes()"/></sec:authorize>
					<!-- <div style="width: 30%;float: right;text-align: right;">
						总数量：<span id="tnum">0</span>
					</div> -->
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
				<%@ include file="downloadProcess.jsp" %>
			</div>
			
		</div>
	</div>
</body>
</html>