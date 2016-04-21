<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>修改</title>
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
							parent.returnMain();
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
		<form:form action="${path}/system/inDefinition/updAction.action" id="form" target="_parent" modelAttribute="frmDefinition" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-4 control-label text-right"><span class="required">*</span>名称：</label>
				<div class="col-xs-5">
					<form:input path="name"  class="form-control text-input validate[required, maxSize[40]]"/>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-4 control-label text-right">简称：</label>
				<div class="col-xs-5">
					<form:input path="nameAbbr" class="form-control text-input" />
				</div>
			</div>
			<div class="form-group">
				<label  class="col-xs-4 control-label text-right">状态：</label>
				<div class="col-xs-5">
					<form:radiobutton path="status" value="1" checked="true"/>可用
					<form:radiobutton path="status" value="0"/>禁用
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-4 control-label text-right">url：</label>
				<div class="col-xs-5">
					<form:input path="url" class="form-control text-input" />
				</div>
			</div>	
			
			<div class="form-group">
				<div class="col-xs-offset-5 col-xs-7">
	           		<form:hidden path="id" id="inDefinitionId"/>
	           		<input type="hidden" name="token" value="${token}" />
	           		<input id="tijiao" type="submit" value="提交" class="btn btn-primary"/>
	            	<input class="btn btn-primary" type="button" value="返回" onclick="parent.returnMain();"/>
	            </div>
			</div>
		</form:form>
		</div>	
	</div>
</body>
</html>