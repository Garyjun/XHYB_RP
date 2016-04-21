<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	function save(){
		$('#form').ajaxSubmit({
				method: 'post',//方式
				success:(function(response){
					callback(response);
       		})
		});
	}
	
	function callback(data){
		var win=  top.index_frame.work_main;
		win.location.href = "${path}/system/dataManagement/stopWord/stopWordList.jsp";
		$.closeFromInner();
	}
	$(document).ready(function(){
		$('#form').validationEngine('attach', {
			relative: true,
			overflownDIV:"#divOverflown",
			promptPosition:"centerRight",//验证提示信息的位置，可设置为："topRight", "bottomLeft", "centerRight", "bottomRight"
			maxErrorsPerField:1,
			onValidationComplete:function(form,status){
				if(status){
					save();
				}
			}
		});
	});
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="upd.action" id="form" modelAttribute="frmStopWord" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-4 control-label text-right"><span class="required">*</span>名称</label>
				<div class="col-xs-5">
					<form:input path="name"  class="form-control validate[required,minSize[2]] text-input"/>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-4 control-label text-right"><span class="required">*</span>状态</label>
				<div class="col-xs-5">
					<form:radiobutton path="status" value="1" checked="true"/>可用
					<form:radiobutton path="status" value="0"/>禁用
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-4 control-label text-right">备注</label>
				<div class="col-xs-5">
					<form:textarea path="desc" rows="5" class="form-control validate[maxSize[256]]"/>
				</div>
			</div>
			<div class="form-group">
				<div class="col-xs-offset-4">
					<div class="form-wrap">
					<form:hidden path="id"></form:hidden>
	           		<input type="hidden" name="token" value="${token}" />
	            	<input id="tijiao" type="submit" value="提交" class="btn btn-primary"/> 
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            	</div>
	            </div>
			</div>
		</form:form>
		</div>	
	</div>
</body>
</html>