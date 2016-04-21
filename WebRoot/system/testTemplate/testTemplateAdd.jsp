<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	
	$(function(){
		$('#form').validationEngine('attach', {
			relative: true,
			overflownDIV:"#divOverflown",
			promptPosition:"centerRight",
			maxErrorsPerField:1,
			onValidationComplete:function(form,status){
				if(status){
					 $('#form').ajaxSubmit({
						method: 'post',//方式
						success:(function(response){
						    top.index_frame.work_main.freshDataTable('data_div');
						    self.location.href = "${path}/system/testTemplate/upd.action?id="+response;
		       			})
					});
				}
			}
		});
	});
	
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="${path}/system/testTemplate/addTemplateAction.action" id="form" modelAttribute="frmtestTemplate" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-4 control-label text-right"><span class="required">*</span>试题模板名称：</label>
				<div class="col-xs-5">
					<form:input path="name"  class="form-control validate[required, maxSize[20]] text-input" id="testTemplateName"/>
				</div>
			</div>
			
			<div class="form-group">
				<label class="col-xs-4 control-label text-right">描述：</label>
				<div class="col-xs-5">
					<form:textarea path="description" rows="5" class="form-control validate[maxSize[40]]"/>
				</div>
			</div>			
			<div class="form-group">
				<div class="col-xs-offset-4">
					<div class="form-wrap">
	           		<input type="hidden" name="token" value="${token}" />
	            	<input id="tijiao" type="submit" value="下一步" class="btn btn-primary"/>
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            	</div>
	            </div>
			</div>
		</form:form>
		</div>	
	</div>
</body>
</html>