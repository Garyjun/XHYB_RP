<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>加工任务添加/编辑</title>
	<script type="text/javascript">
		function returnList(){
			location.href = "${path}/taskProcess/query.action";
		}

		function edit(act){
			document.form1.action=act;
 			jQuery('#form1').submit();
		}

		$(function(){
			$('#form1').validationEngine('attach', {
				relative: true,
				overflownDIV:"#divOverflown",
				promptPosition:"centerRight",
				maxErrorsPerField:1,
				onValidationComplete:function(form,status){
					if(status){
						save();
					}
				}
			});
		});

		function save(){
			$('#form1').ajaxSubmit({
 				method: 'post',//方式
 				success:(function(response){
 					callback(response);
           		})
 			});
		}

		function callback(data){
// 			top.index_frame.freshDataTable('data_div'); 没有main时用这种方式
			top.index_frame.work_main.freshDataTable('data_div');
			$.closeFromInner();
		}
	</script>
</head>
<body>
	<div class="form-wrap">
	      		<form:form action="" modelAttribute="taskProcess" name="form1" id="form1" method="POST"  class="form-horizontal">
		      		<form:hidden path="id"  id="id"/>
		      		<form:hidden path="createUser.id"  id="createUser.id"/>
		      		<form:hidden path="createTime"  id="createTime"/>
					<div class="form-group">
						<label for="taskName" class="col-sm-4 control-label text-right"><span class="required">*</span>名称：</label>
						<div class="col-xs-5">
							<form:input  path="taskName" id="taskName"  class="form-control validate[required, maxSize[200],ajax[validateTaskProcessName]] text-input"/>
						</div>
					</div>

					<div class="form-group">
						<label for="batchNumber" class="col-sm-4 control-label text-right">批次：</label>
						<div class="col-xs-5">
							 <form:input  path="batchNumber" id="batchNumber"  items="${batchNumber}" class="form-control" />
						</div>
					</div>


					<div class="form-group">
						<label for="processNumber" class="col-sm-4 control-label text-right">数量：</label>
						<div class="col-xs-5">
								<form:input path="processNumber"  id="processNumber" items="${processNumber}" class="form-control" />
							</div>
					</div>

					<div class="form-group">
						<label for="personNumber" class="col-sm-4 control-label text-right">建议人数：</label>
						<div class="col-xs-5">
								<form:input path="personNumber"  id="personNumber" items="${personNumber}" class="form-control" />
							</div>
					</div>

					<div class="form-group">
						<label for="startTime" class="col-sm-4 control-label text-right"><span class="required">*</span>预计开始时间：</label>
						<div class="col-xs-5">
							<input class="form-control validate[required] Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'})" id="startTime" name="startTime" value="<fmt:formatDate value="${taskProcess.startTime}" pattern="yyyy-MM-dd" />" />
						</div>
					</div>

					<div class="form-group">
						<label for="endTime" class="col-sm-4 control-label text-right"><span class="required">*</span>要求完成时间：</label>
						<div class="col-xs-5">
								<input class="form-control validate[required] Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startTime\')}'})" id="endTime" name="endTime" value="<fmt:formatDate value="${taskProcess.endTime}" pattern="yyyy-MM-dd" />" />
						</div>
					</div>

					<div class="form-group">
						<label for="priority" class="col-sm-4 control-label text-right">优先级：</label>
						<div class="col-xs-5">
							 <form:select path="priority" id="priority"  items="${priority2Desc}" class="form-control" />
						</div>
					</div>

					<%-- <div class="form-group">
						<label for="status" class="col-sm-4 control-label text-right">状态：</label>
						<div class="col-xs-5">
							<p class="form-control-static">
								${taskProcess.statusDesc}
							</p>
						</div>
				 	</div> --%>
				 	
				 	<div class="form-group">
						<label for="priority" class="col-sm-4 control-label text-right">资源类型：</label>
						<div class="col-xs-5">
							 <app:selectResType name="resType" id="resType" headName="" headValue="" selectedVal="${taskProcess.resType}"/>
						</div>
					</div>

			      <div class="form-group">
						<label for="description" class="col-sm-4 control-label text-right">描述：</label>
						<div class="col-xs-5">
						     <form:textarea path="description" id="description" rows="3" class="form-control validate[maxSize[10000]]"/>
						</div>
				 </div>

				<div class="form-group">
						<div class="col-sm-offset-4 col-sm-6">
			           		<input type="hidden" name="token" value="${token}" />
			           		<c:if test="${id>-1}">
   					  			 <button type="button"  class="btn btn-primary" onclick="edit('${path}/taskProcess/update.action')">保存</button>
   							</c:if>
   							<c:if test="${id eq null || id eq ''}">
   							   <button type="button"  class="btn btn-primary" onclick="edit('${path}/taskProcess/add.action')">保存</button>
   							</c:if>
   							  &nbsp;
   							<button type="reset" class="btn btn-primary">重置</button>
   							   &nbsp;
			            	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
			            </div>
				</div>
			</form:form>
  		  </div>
</body>
</html>