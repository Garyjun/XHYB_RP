<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page
	import="org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>加工任务分配</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${path}/resRelease/resOrder/templeteSelect.js"></script>
<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
<script type="text/javascript">
	var taskId = ${taskProcessId};
	$(function() {
		showTabRes("allRes");
		showTabUser();
		$("#addResource").css("display", "block");
		$('#form1').validationEngine('attach', {
			relative: true,
			overflownDIV:"#divOverflown",
			promptPosition:"topRight",//验证提示信息的位置，可设置为："topRight", "bottomLeft", "centerRight", "bottomRight"
			maxErrorsPerField:1,
			onValidationComplete:function(form,status){
				if(status&&checkDate()){
					save();
				}
			}
		});
		$("input[name=res]").on("change", function(){
			var resStatus = this.value;
			var tabId = "tab_1_1_1";
			if(resStatus=="allRes"){
				queryForm(tabId, resStatus);
			}else if(resStatus=="distributed"){
				queryForm(tabId, resStatus);
			}else{
				queryForm(tabId, resStatus);
			}
			$("#data_div_res").datagrid("uncheckAll");
			$("#data_div_user").datagrid("uncheckAll");
		});
	});
	function checkDate(){
		/* var date = $("#orderDate").val();
		if(date!=""){
			return true;
		}
		return false; */
		return true;
	}
	function showItemAndTab(id){
		if(id=="tab_1_1_1"){
			$("#addResource").show();
			$("#distributionResource").hide();
			queryForm(id, "");
		}else if(id=="tab_1_1_2"){
			queryForm(id, "");
			$("#addResource").hide();
			$("#distributionResource").show();
		}
		$("#data_div_res").datagrid("uncheckAll");
		$("#data_div_user").datagrid("uncheckAll");
	}
	var dtRes = new DataGrid();
	function showTabRes(status) {
		dtRes.setDivId("data_div_res");
		dtRes.setConditions([]);
		dtRes.setPK('id');
		dtRes.setSortName('id');
		dtRes.setURL('${path}/taskProcess/queryTaskResource.action?taskId=' + taskId + "&status=" + status);
		dtRes.setSortOrder('desc');
		dtRes.setSelectBox('id');
		dtRes.setColums([ /* <app:QueryListColumnTag /> */
        	{ title:'资源名称', field:'resName', width:fillsize(0.4), align:'center', sortable:true},
		    { title:'状态', field:'status', width:fillsize(0.15), align:'center', formatter:$statusDesc},
		   	{ title:'操作', field:'opt1', width:fillsize(0.45), align:'center', formatter:$operateRes}
	    ]);
		accountHeight();
		dtRes.initDt();
		dtRes.query(); 
	}
	var dtUser  = new DataGrid();
	function showTabUser(){
		dtUser.setDivId("data_div_user");
		dtUser.setConditions(['processor.userName']);
		dtUser.setPK('id');
		dtUser.setSortName('id');
		dtUser.setURL();
		//dtUser.setURL('${path}/taskProcess/queryDistributedProcessors.action?taskId=' + taskId);
		dtUser.setSortOrder('desc');
		dtUser.setSelectBox('id');
		dtUser.setColums([
		    { title:'加工员名称', field:'processor.userName', width:fillsize(0.3), align:'center'},
			{ title:'分配数量', field:'distResNum', width:fillsize(0.1), align:'center'},
			{ title:'导出资源路径', field:'exportResUrl', width:fillsize(0.3), align:'center'},
			{title:'操作', field:'opt1', width:fillsize(0.3), align:'center', formatter:$operateUser}
		]);
		accountHeight();
		dtUser.initDt();
		dtUser.query(); 
	}
	
	$statusDesc = function(value, rec){
		 if(rec.status==0){
			return "未分配";
		}else if(rec.status==1){
			return "已分配";
		}else if(rec.status == 2){
			  return "已完成";
		} /* if(rec.status == 0){
			  return "未加工";
	   	  }else if(rec.status == 1){
			  return "已加工";
		  }else if(rec.status == 2){
			  return "已完成";
		  } */
	}
	
	function queryForm(tabId, param) {
		if(tabId=="tab_1_1_1"){
			dtRes.dataGridObj.datagrid({
				url : "${path}/taskProcess/queryTaskResource.action?taskId=" + taskId +"&status=" + param
			});
		}else{
			dtUser.dataGridObj.datagrid({
				url : "${path}/taskProcess/queryDistributedProcessors.action?taskId=" + taskId
			});
		}
	}
	
	function returnList() {
		parent.queryForm();
		//location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
	}
	
	$operateRes = function(value,rec){
		var resDetail = "<sec:authorize url="/taskProcess/resDetail.action"><a class=\"btn hover-red\" href=\"javascript:resDetail('"+rec.sysResDirectoryId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a></sec:authorize>";
		var delRes = "<sec:authorize url="/taskProcess/deleteRes.action"><a class=\"btn hover-red\" href=\"javascript:delRes('"+rec.taskDetail.id+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a></sec:authorize>";
		var opt = resDetail;
		if(rec.status=="0"){
			opt += delRes;
		}
		return opt;
	};
	
	$operateUser = function(value,rec){
		var distedDetail = "<sec:authorize url="/taskProcess/processorResdetail.action"><a class=\"btn hover-red\" href=\"javascript:distedDetail("+rec.processor.id+")\" ><i class=\"fa fa-sign-out\"></i> 详细</a></sec:authorize>";
		var distResToProcessor = "<sec:authorize url="/taskProcess/distResToProcessor.action"><a class=\"btn hover-red\" href=\"javascript:distResToProcessor("+rec.processor.id+")\" ><i class=\"glyphicon glyphicon-plus-sign\"></i> 分配</a></sec:authorize>";
		var removeResToProcessor = "<sec:authorize url="/taskProcess/removeResToProcessor.action"><a class=\"btn hover-red\" href=\"javascript:removeResToProcessor("+rec.processor.id+")\" ><i class=\"glyphicon glyphicon-minus-sign\"></i> 取消分配</a></sec:authorize>";
		var delProcessor = "<sec:authorize url="/taskProcess/delProcessor.action"><a class=\"btn hover-red\" href=\"javascript:delProcessor("+rec.processor.id+")\" ><i class=\"fa fa-trash-o\"></i> 删除</a></sec:authorize>";
		var exportMetadata = "<sec:authorize url="/taskProcess/exportMetadata.action"><a class=\"btn hover-red\" href=\"javascript:exportMetadata("+rec.processor.id+")\" ><i class=\"fa fa-download\"></i> 导出元数据</a></sec:authorize>";
		var exportFiles = "<sec:authorize url="/taskProcess/exportFiles.action"><a class=\"btn hover-red\" href=\"javascript:exportFiles("+rec.processor.id+")\" ><i class=\"fa fa-download\"></i> 导出素材文件</a></sec:authorize>";
		var opt = distResToProcessor;
		if(rec.distResNum==0){
			opt += delProcessor;
		}else{
			opt += distedDetail  + removeResToProcessor + exportMetadata + exportFiles;
		}
		return opt;
	}

	//导出元数据
	function exportMetadata(processorId){
		var publishType = $("#resType").val();
		
		//---------if 1 ------------资源清单  中的导出元数据
		
		if(processorId == "list"){
			var resIds = getChecked('data_div_res','sysResDirectoryId').join(',');
			if(resIds == ""){
				$.alert("请选择要导出元数据的资源！");
				return;
			}
			var searchParamCa = {
 					ids:resIds,
					publishType:publishType,
		        };
				
			var paramCa  = encodeURI(JSON.stringify(searchParamCa));
			 				
				downLoading = $.waitPanel('下载中请稍候...',false);	
				$.ajax({
					url:'${path}/publishRes/getExportExcel.action',
				    type: 'post',
				    datatype: 'json',
				    data:"searchParamCa=" + paramCa,
				    success: function (returnValue) {
				    	if(returnValue!=""){
				    		downLoading.close();
				    		window.location.href = '${path}/publishRes/getExportExcelDown.action?excelFilePath='+returnValue;
				    	}else{
				    		$.alert("文件下载数量超过数据字典定义大小，不能下载");
				    		downLoading.close();
				    	}
				    }
				});
				
		}else{
		//---------- else if 2 ----------分配加工员/资源  中的导出元数据
		var status = $("input[name=exportMetadataType]:checked").val();
		var _url = "${path}/taskProcess/getTaskResPkByTaskIdAndProcessorIds.action?taskId=" + taskId + "&processorIds=";
		if(processorId!=null){
			_url += processorId;
		}else{
			var processorIds = getChecked('data_div_user','processor.id').join(',');
			_url += processorIds;
			if(processorIds==""){
				$.alert("请选择要导出元数据的加工员！");
				return;
			}
		}
		
		$.ajax({
			url: _url,
			type: "get",
			success: function(ids){
				if(ids&&ids.length>0){
					$.get("${path}/taskProcess/getMetaDataIds.action?ids=" + ids + "&publishType=" + publishType +"&status=" + status,function(data){
			 			if(data&&data.length>0){
			 				
			 				var searchParamCa = {
				 					ids:data,
									publishType:publishType,
						        };
								
							var paramCa  = encodeURI(JSON.stringify(searchParamCa));
							 				
			 				downLoading = $.waitPanel('下载中请稍候...',false);	
			 				$.ajax({
			 					url:'${path}/publishRes/getExportExcel.action',
			 				    type: 'post',
			 				    datatype: 'json',
			 				    data:"searchParamCa=" + paramCa,
			 				    success: function (returnValue) {
			 				    	if(returnValue!=""){
			 				    		downLoading.close();
			 				    		window.location.href = '${path}/publishRes/getExportExcelDown.action?excelFilePath='+returnValue;
			 				    	}else{
			 				    		$.alert("文件下载数量超过数据字典定义大小，不能下载");
			 				    		downLoading.close();
			 				    	}
			 				    }
			 				});
			 			}else{
			 				$.alert("目前没有数据！");
			 			}
			 		});
				}else{
					$.alert("目前没有数据！");
				}
			}
		});
		
		
/* 		
		if(processorId!=null){
			_url += processorId;
			$.ajax({
				url: _url,
				type: "get",
				success: function(ids){
					if(ids&&ids.length>0){
						$.get("${path}/taskProcess/getMetaDataIds.action?ids=" + ids + "&publishType=" + publishType,function(data){
				 			if(data&&data.length>0){
				 				//location.href = "${path}/publishRes/getExportExcel.action?ids="+data + "&publishType=" + publishType;
				 				
				 				var searchParamCa = {
					 					ids:data,
										publishType:publishType,
							        };
									
								var paramCa  = encodeURI(JSON.stringify(searchParamCa));
								 				
				 				downLoading = $.waitPanel('下载中请稍候...',false);	
				 				$.ajax({
				 					url:'${path}/publishRes/getExportExcel.action',
				 				    type: 'post',
				 				    datatype: 'json',
				 				    data:"searchParamCa=" + paramCa,
				 				    success: function (returnValue) {
				 				    	if(returnValue!=""){
				 				    		downLoading.close();
				 				    		window.location.href = '${path}/publishRes/getExportExcelDown.action?excelFilePath='+returnValue;
				 				    	}else{
				 				    		$.alert("文件下载数量超过数据字典定义大小，不能下载");
				 				    		downLoading.close();
				 				    	}
				 				    }
				 				});
				 			}else{
				 				$.alert("目前没有数据！");
				 			}
				 		});
					}else{
						$.alert("目前没有数据！");
					}
				}
			});
		}else{
			var processorIds = getChecked('data_div_user','processor.id').join(',');
			if(processorIds==""){
				$.alert("请选择要导出元数据的加工员！");
			}else{
				_url += processorIds;
				$.ajax({
					url: _url,
					type: "get",
					success: function(ids){
						if(ids&&ids.length>0){
							$.get("${path}/taskProcess/getMetaDataIds.action?ids=" + ids + "&publishType=" + publishType,function(data){
					 			if(data&&data.length>0){
					 				location.href = "${path}/publishRes/getExportExcel.action?ids="+data + "&publishType=" + publishType;
					 			}else{
					 				$.alert("目前没有数据！");
					 			}
					 		});
						}else{
							$.alert("目前没有数据！");
						}
					}
				});
			}
		} */
		
		
		
		}
		
	}
	
	//导出资源文件
	function  exportFiles(processorId){
		var _url = "${path}/taskProcess/copyProcessFiles.action?taskId=" + taskId + "&processorIds=";
		
		if(processorId!=null){
			_url += processorId;
		}else{
			var processorIds = getChecked('data_div_user','processor.id').join(',');
			_url += processorIds;
			if(processorIds==""){
				$.alert("请选择要导出素材文件的加工员！");
				return;
			}
		}
		
		$.ajax({
			url: _url,
			type: "get",
			success: function(data){
				$.alert("导出成功！");
			}
		});
		
		/* if(processorId!=null){
			_url += processorId;
			$.ajax({
				url: _url,
				type: "get",
				success: function(data){
					$.alert("导出成功！");
				}
			});
		}else{
			var processorIds = getChecked('data_div_user','processor.id').join(',');
			if(processorIds==""){
				$.alert("请选择要导出素材文件的加工员！");
			}else{
				_url += processorIds;
				$.ajax({
					url: _url,
					type: "get",
					success: function(data){
						$.alert("导出成功！");
					}
				});
			}
		} */
	}
	
	function delProcessor(proceseId){
		$.confirm('确定要删除该加工员吗？', function(){
			$.post('${path}/taskProcess/delProcessor.action?taskId=' + taskId + '&processorIds=' + proceseId, function(data){
				queryForm("tab_1_1_2", "");
			});
		});
	}
	
	//按策略分配资源
	function distributeResByCategory(proceseId, category){
		if(category==""){
			
		}else{
			
		}
	}
	
	//给加工员分配资源
	function distResToProcessor(processorId){
		$.openWindow("${path}/taskprocess/distResToProcessor.jsp?taskId=" + taskId + "&processorId=" + processorId , '分配资源', 800, 530);
	}
	
	//查看资源详细
	function resDetail(resId){
		$.openWindow("${path}/taskprocess/resDetail.jsp?taskId=" + taskId + "&resId="+resId, '资源详细信息', 800, 530);
	}
	
	//查看加工员已分配资源详细
	function distedDetail(processorId){
		$.openWindow("${path}/taskprocess/distributedResDetail.jsp?taskId=" + taskId + "&processorId=" + processorId, '分配资源信息', 800, 530);
	}
	
	function addResource(){
		var resType = $("#resType").val();
		$.openWindow("${path}/taskprocess/processResSelect.jsp?taskId=" + taskId + "&resType=" + resType, '选择资源', 1200, 600);
	}
	
	function batchDelRes(){
		var ids = getChecked('data_div_res','taskDetail.id').join(',');
		if (ids == '') {
			$.alert('请选择要删除的资源！');
			return;
		} else {
			var canDel = true;
		 	var rows = $('#data_div_res').datagrid('getChecked');
		 	for ( var i = 0; i < rows.length; i++) {
		 		var status = eval('rows[i]' + '.' + 'status');
		 		if(status == '0'){
		 			continue;
		 		}else{
		 			$.alert('只能删除未分配的资源！');
		 			canDel = false;
		 			break;
		 		}
		 	}
		 	if(canDel){
		 		//var _url = '${path}/taskProcess/batchDeleteResourceByTaskIdAndResDetailIds.action?taskId=' + taskId + '&resDetailIds=' + ids;
		 		/* $.ajax({
		 			url: _url,
		 			type: 'post',
		 			success: function(){
		 				$.alert("资源删除成功！");
		 			}
		 		}); */
		 		$.post("${path}/taskProcess/batchDeleteResourceByTaskIdAndResDetailIds.action?taskId=" + taskId + "&resDetailIds=" + ids, function(data){		 			
		 			queryForm("tab_1_1_1", "");
		 			$.alert("资源删除成功！");
				});
		 	}
		}	
	}
	
	function clearRes(){
		$.confirm('本次只清空未分配资源，确定要执行此操作吗？', function(){
			$.post('${path}/taskProcess/deleteAllResByTaskId.action?taskId=' + taskId,function(data){
				queryForm("tab_1_1_1", "");
			});
		});
	}
	
	function delRes(resId){
		$.confirm('确定要删除所选数据吗？', function(){
			$.post('${path}/taskProcess/deleteResourceByTaskIdAndResDetailId.action?taskId=' + taskId + '&resDetailIds=' + resId,function(data){
				queryForm("tab_1_1_1", "");
			});
		});
	}
	
	function addMaker(){
		$.openWindow("${path}/taskProcess/addMakers.action?id=" + taskId,'添加加工员', 600, 250);
	}
	
	function averageDist(){
		var rows = $('#data_div_res').datagrid('getRows');
		if(rows==""){
			$.alert("未添加资源，请添加资源后再进行资源分配！");
			return;
		}
		
		var processorIds = getChecked('data_div_user','processor.id').join(',');
		if(processorIds==""){
			$.alert("请选择加工员！");
			return;
		}
		$.post('${path}/taskProcess/averageDist.action?taskId=' + taskId + "&processorIds=" + processorIds, function(data){
			if(data=="success"){
				queryForm("tab_1_1_2", "");
				$.alert("分配任务成功！");
			}else{
				$.alert("分配任务失败！");
			}
		});
		
		
		/* var _url = "${path}/taskProcess/getProcessorNum.action?taskId=" + taskId;
		$.ajax({
			url: _url,
			type: "get",
			success: function(data){
				alert(data);
				if(data!=0){
					$.post('${path}/taskProcess/averageDist.action?taskId=' + taskId, function(data){
						if(data=="success"){
							queryForm("tab_1_1_2", "");
							$.alert("分配任务成功！");
						}else{
							$.alert("分配任务失败！");
						}
					});
				}else{
					$.alert("请选择加工员！");
				}
			}
		}); */
		
	}
	
	//取消分给该加工员的资源
	function removeResToProcessor(processorId){
		$.openWindow("${path}/taskprocess/removeResToProcessor.jsp?taskId=" + taskId + "&processorId=" + processorId , '取消分配资源', 800, 530);
	}
	
	function save() {
		/* var orderId = '${resOrder.orderId}';
		var operateFrom = '${operateFrom}';
		var channelName= document.getElementById("channelName").value;
		var templateId = document.getElementById("templateId").value;
		var description= document.getElementById("description").value;
		var orderDate= document.getElementById("orderDate").value;
		if(channelName!=null&&channelName.trim().length>0){
			channelName = encodeURI(channelName);
		}
		if(description!=null&&description.trim().length>0){
			description = encodeURI(description);
		}
		window.location.href = "${path}/resOrder/update.action?orderId="+orderId+"&operateFrom="+operateFrom+"&channelName="+
				channelName+"&templateId="+templateId+"&description="+description+"&orderDate="+orderDate; */
	}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap ">
		<div class="panel panel-default">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
<!-- 					<li><a href="javascript:;">需求单编辑</a></li> -->
<!-- 					<li><a href="###">资源发布</a></li> -->
<!-- 					<li class="active">需求单</li> -->
					<li class="active">加工单编辑</li>
				</ol>
			</div>
			<form action="${path}/resOrder/add.action"
				id="form1" class="form-horizontal">
				<div class="tab-pane" id="tab_1_1_2">
					<div class="portlet">
						<div class="portlet-title">
							<div class="caption">
								加工任务信息 <a href="javascript:;" onclick="togglePortlet(this)">
<!-- 								<i class="fa fa-angle-up"></i> -->
								</a>
							</div>
						</div>
						<div class="portlet-body">
							<div class="row">
								<div class="container-fluid">
									<div class="row">
										<input type="hidden" name="orderId" id="orderId" value="${taskProcess.id}"/>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">名称：</label>

												<div class="col-md-7">
													<input type="hidden" name="processId" id="processId" value="${taskProcess.id}"/>
													<p class="form-control-static" style="white-space:nowrap; width:350px; overflow:hidden;  text-overflow:ellipsis;" title="${taskProcess.taskName}">
														${taskProcess.taskName}
													</p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">批次：</label>

												<div class="col-md-7">
													<p class="form-control-static" name="batchNumber" id="batchNumber">
														${taskProcess.batchNumber}
													</p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">数量：</label>

												<div class="col-md-7">
													<p class="form-control-static">
														${taskProcess.processNumber}
													</p>
												</div>
											</div>
										</div>
										
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">建议人数：</label>

												<div class="col-md-4">
													<p class="form-control-static">
														${taskProcess.personNumber}
													</p>
												</div>
											</div>
										</div>
										
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">预计开始时间 ：</label>

												<div class="col-md-5">
													<p class="form-control-static">
														<fmt:formatDate value="${taskProcess.startTime}" pattern="yyyy-MM-dd" />
													</p>
												</div>
											</div>
										</div>
										
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">预计完成时间 ：</label>

												<div class="col-md-5">
													<p class="form-control-static">
														<fmt:formatDate value="${taskProcess.endTime}" pattern="yyyy-MM-dd" />
													</p>
												</div>
											</div>
										</div>
										
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">资源类型 ：</label>

												<div class="col-md-5">
													<p class="form-control-static">
														${taskProcess.resTypeDesc}
													</p>
												</div>
											</div>
										</div>
										
										<div class="col-md-12">
											<div class="form-group">
												<label class="control-label col-md-2">描述信息：</label>

												<%-- <div class="col-md-8">
													${taskProcess.description}
												</div> --%>
												<div class="col-xs-5">
													<p class="form-control-static" style="white-space:nowrap; width:600px; overflow:hidden;  text-overflow:ellipsis;" title="${taskProcess.description}">${taskProcess.description}</p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>

							<table width="98%" border="0" align="center" cellpadding="0"
								cellspacing="0">
								<tr>
									<td><br />
										<div align="center">
											<%-- <input type="hidden" name="token" value="${token}" />
											<button type="submit" class="btn btn-primary"
												onclick="chcekDate();">保存</button>
											&nbsp; --%>
											
											<button type="button" class="btn btn-primary"
												onclick="returnList();">返回</button>
										</div> <br />
										<div id="dt"></div>
										<div id="dt-pag-nav"></div></td>
								</tr>
							</table>

							
 							<div class="by-tab">
								<ul class="nav nav-tabs nav-justified" id="meta_tab">
									<li class="active"><a  id="tab_1_1_1" data-toggle="tab"
										onclick="showItemAndTab('tab_1_1_1');">资源清单</a></li>
									<li><a  id="tab_1_1_2" data-toggle="tab"
										onclick="showItemAndTab('tab_1_1_2');">分配加工员/资源</a></li>
								</ul>
								<div class="" id="addResource" style="display:none;">
									<table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
										<tr>
											<td><br />
												<div align="left">
													<input type="hidden" name="token" value="${token}" />
													<sec:authorize url="/taskProcess/addResource.action">
													<button type="button" class="btn btn-primary" onclick="addResource();">添加资源</button>
													</sec:authorize>
													&nbsp;
													<sec:authorize url="/taskProcess/deleteRes.action">
													<button type="button" class="btn btn-primary" onclick="batchDelRes();">批量删除</button>
													</sec:authorize>
													&nbsp;
													<sec:authorize url="/taskProcess/deleteRes.action">
													<button type="button" class="btn btn-primary" onclick="clearRes();">清空资源</button>
													</sec:authorize>
													
													&nbsp;
													<sec:authorize url="/taskProcess/exportMetadata.action">
														<button type="button" class="btn btn-primary" onclick="exportMetadata('list');">导出元数据</button>
													</sec:authorize>
												</div> <br />
												<div id="dt"></div>
												<div id="dt-pag-nav"></div>
											</td>
										</tr>
									</table>
									<div>
										<label><input name="res"  checked="checked" type="radio" value="allRes" style="margin-left:10px;"/>
											<span style="font-size:12px;">全部 </span> 
										</label>
										&nbsp;&nbsp;&nbsp;&nbsp;
										<label><input name="res" type="radio" value="distributed" />
											<span style="font-size:12px;">已分配 </span> 
										</label>
										&nbsp;&nbsp;&nbsp;&nbsp;
										<label><input name="res" type="radio" value="distributing" />
											<span style="font-size:12px;">未分配 </span> 
										</label>
										&nbsp;&nbsp;&nbsp;&nbsp;
										<label><input name="res" type="radio" value="finished" />
											<span style="font-size:12px;">已完成</span> 
										</label>
									</div>
									<div class="portlet portlet-border" style="margin-left:10px;margin-right:10px;">
										<div class="panel-body">
											<div class="tab-content">
												<div class="tab-pane active">
													<div class="portlet">
														<div id="data_div_res" class="data_div"
															style="width: 1025px; "></div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								
								<div class="" id="distributionResource" style="display:none;">
									<table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
										<tr>
											<td><br />
												<div align="left">
													<input type="hidden" name="token" value="${token}" />
													<sec:authorize url="/taskProcess/addMaker.action">
														<button type="button" class="btn btn-primary"
															onclick="addMaker();">添加加工员</button>
													</sec:authorize>
													&nbsp;
													<sec:authorize url="/taskProcess/avgDist.action">
														<button type="button" class="btn btn-primary"
															onclick="averageDist();">平均分配</button>
													</sec:authorize>
													&nbsp;
													<sec:authorize url="/taskProcess/exportMetadata.action">
														<button type="button" class="btn btn-primary"
															onclick="exportMetadata(null);">导出元数据</button>
													</sec:authorize>
														&nbsp;
													<sec:authorize url="/taskProcess/exportFiles.action">
														<button type="button" class="btn btn-primary"
															onclick="exportFiles(null);">导出素材文件</button>
													</sec:authorize>
													
													&nbsp;&nbsp;&nbsp;&nbsp;
													<label>选择导出元数据类型：</label>
													&nbsp;&nbsp;&nbsp;&nbsp;
													<label><input name="exportMetadataType"  checked="checked" type="radio" value="" style="margin-left:10px;"/>
														<span style="font-size:12px;">全部 </span> 
													</label>
													&nbsp;&nbsp;&nbsp;&nbsp;
													<label><input name="exportMetadataType" type="radio" value="1" />
														<span style="font-size:12px;">已分配 </span> 
													</label>
													&nbsp;&nbsp;&nbsp;&nbsp;
													<label><input name="exportMetadataType" type="radio" value="2" />
														<span style="font-size:12px;">已完成</span> 
													</label>
												</div> <br />
												<div id="dt"></div>
												<div id="dt-pag-nav"></div>
											</td>
										</tr>
									</table>
									<div class="portlet portlet-border" style="margin-left:10px;margin-right:10px;">
										<div class="panel-body">
											<div class="tab-content">
												<div class="tab-pane active">
													<div class="portlet">
														<div id="data_div_user" class="data_div"
															style="width: 1025px; "></div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<!-- <div class="portlet">
	  							<div class="portlet-title">
	      							<div class="caption">资源明细</div>
	  							</div>
	  							<div class="panel-body" id="999" style="width:100%;">
		   							<div id="data_div" class="data_div" ></div>
	  							</div>
	  						</div> -->
						</div>
					</div>
				</div>
				<input type="hidden" id="taskId" value="${taskProcessId}"/>
				<input type="hidden" id="resType" value="${taskProcess.resType}"/>
			</form>
		</div>
	</div>
</body>
</html>