<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
	<head>
		<title>加工任务管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
		<script type="text/javascript">
			$(function(){
	 	   		//定义一datagrid
	 	   		var _divId = 'data_div';
	 	   		var _url = '${path}/taskProcess/queryTaskResource.action';
	 	   		var _pk = 'id';
	 	   		var _conditions = ['resName','userName','taskName', 'publishType'];
	 	   		var _sortName = 'id';
	 	   		var _sortOrder = 'desc';
	 	   		var _check=true;
	 			var _colums = [
	 						    { title:'资源名称', field:'resName' ,width:200, align:'center' },
	 						    { title:'加工员', field:'taskDetail.makerName' ,width:100, align:'center' ,sortable:true},
	 						    { title:'加工单名称', field:'taskDetail.taskProcess.taskName' ,width:200, align:'center'},
	 						    { title:'状态', field:'status' ,width:50, align:'center',formatter:$statusDesc },
	 						    { title:'预计开始时间', field:'taskDetail.startTime' ,width:100, align:'center',formatter:$formatDate },
	 						    { title:'要求完成时间', field:'taskDetail.endTime' ,width:100, align:'center',formatter:$formatDate }
	 						   	//{ title:'操作',field:'opt1',width:100,align:'center',formatter:$operate}
	 						];

	 			accountHeight();
	 			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
	 			
	 			$("#publishType"). change(function(){
	 				$grid.query();
	 			});
	 		});

	 	   	$operate = function(value,rec){
	 	   		var optArray = new Array();
	 	   		if(rec.taskDetail.status==0){
		 	   		var delUrl='<sec:authorize url="/taskProcess/deleteTaskResource.action"><a class=\"btn hover-red\" href="javascript:deleteRecord('+rec.id+')"><i class=\"fa fa-trash-o\"></i>删除</a></sec:authorize>';
		 	   		optArray.push(delUrl);
	 	   		}
	 	   		return createOpt(optArray);
	 	   	};
	 	   	
			function deleteRecord(id){
				$.confirm('确定要删除所选数据吗？', function(){
					$.ajax({
						url:'${path}/taskProcess/deleteTaskResource.action?id=' + id,
					    datatype: 'text',
					    success: function () {
					    	freshDataTable('data_div');
					    }
					});
				});
			}
			
			function del(){
				var ids = getChecked('data_div','id').join(',');
				if(ids.length==0){
					$.alert("请选择要删除的加工任务单！");
					return;					
				}
				var rows = $('#data_div').datagrid('getChecked');
				for(var i=0; i<rows.length; i++){
					if(rows[i].taskDetail.status!=1){
						$.alert("只能删除未分配的加工任务单！");
						return;
					}
				}
				$.confirm('确定要删除所选数据吗？', function(){
					$.ajax({
						url:'${path}/taskProcess/batchDeleteTaskResource.action?ids=' + ids,
					    datatype: 'text',
					    success: function () {
					    	freshDataTable('data_div');
					    }
					});
				});				
			}
	 	   	
	 	   $statusDesc = function(value, rec) {
	 		   /* if(rec.taskDetail.status == 1)
	 			   return "未分配";
	 		   else if(rec.taskDetail.status == 2)
	 			   return "已分配";
	 		   else if(rec.taskDetail.status == 3)
	 			   return "已完成";
	 		   else 
	 			  return ""; */
	 		 /*  if(rec.taskDetail.status == 0){
	 			  return "未加工";
	 		  }else if */if(rec.status == 0){
	 			  return "未加工";
	 	   	  }else if(rec.status == 1){
	 			  return "已加工";
	 		  }else if(rec.status == 2){
	 			  return "已完成";
	 		  }
	 	   };
	 	function query(){
	 		$grid.query();
	 	}	
	 	
	 	function exportResExcel(){
	 		var dataDiv = $('#data_div').datagrid('getData');
	 		//alert('总数据量:' + dataDiv.total)//注意你的数据源一定要定义了total，要不会为undefined，datagrid分页就是靠这个total定义
	 		//alert('当前页数据量:' + dataDiv.rows.length)
	 		if(dataDiv.total == null || dataDiv.total == ""){
	 			$.alert("当前没有数据！");
	 			return;
	 		}
	 		
	 		//获得选中的ids;
	 		var ids = getChecked('data_div','id').join(',');
	 		var publishType = $("#publishType option:selected").val();
	 		var publishTypeDesc = $("#publishType option:selected").text();
			
	 		//如果没有选中数据，，则按页数导出数据
	 		if(ids == null || ids == ""){
	 			$('#publishType').val(publishType);
	 			$('#page').val(1); //设置默认为：第一页开始
				$("#myModal").modal('show');
			
			//如果点选了数据，则按点选的数据导出
	 		}else{
	 			$.get("${path}/taskProcess/getMetaDataIds.action?ids=" + ids + "&publishType=" + publishType,function(data){
		 			if(data&&data.length>0){
		 				//$.alert("本次导出的为【" + publishTypeDesc + "】类型的元数据！");
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
		 				$.alert("当前没有数据！");
		 			}
		 		});
	 		}
	 	}
	 	
	 	function clearQueryCondition(){
	 		$("input[role=taskName]").val("");
	 		$("input[role=resName]").val("");
	 		$("input[role=userName]").val("");
	 	}
		</script>
	</head>
	<body>
		<input type="hidden" id="pageSize" name="pageSize" value=""/>
		<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">加工管理</a></li>
					<li class="active">加工任务管理</li>
					<li class="active">加工任务列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				   <%--  <sec:authorize url="/taskProcess/batchDelete/*"> <a class="btn btn-primary" onclick="del();">批量删除</a></sec:authorize> --%>
			     	<input type="button" value="hidden" style="background:white; border-color: white;" class="btn btn-primary" />
				    <sec:authorize url="/taskProcessRecord/exportMetadata.action"> <a class="btn btn-primary" onclick="exportResExcel();">导出元数据</a></sec:authorize>
					<div  style="float: left; margin-right:3px;">
						<div class="row" >
						<label class="control-label col-md-4 text-right"
							>资源类型：</label>
						<div class="col-md-8">
							 <app:selectResType name="publishType" id="publishType" selectedVal="${param.publishType}"  headName=""  headValue=""  readonly =""/>
						</div>
					</div>
					</div>
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form">
						   <div class="form-group">
							      <input type="text" role="taskName" class="form-control" id="taskName" name="taskName" qMatch="like"  placeholder="加工单名称" />
							</div>						
						   <div class="form-group">
							      <input type="text" role="resName" class="form-control" id="resName" name="resName" qMatch="like"  placeholder="资源名称" />
							</div>
							<div class="form-group">
							      <input type="text" role="userName" class="form-control" id="userName" name="userName" qMatch="like" placeholder="加工员" />
							</div>							
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="button" value="清空" onclick="clearQueryCondition();query();" class="btn btn-primary"/>
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
				<%@ include file="download.jsp" %>
			</div>
		</div>
	</div>	
	</body>
</html>