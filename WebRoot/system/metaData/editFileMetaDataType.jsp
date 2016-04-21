<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
    <link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
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
							returnMain();
		       			})
					});
				}
			}
		});
	});
	
	
	
	
	function returnMain(){
		parent.location.href='${path}/system/metaData/main.jsp';
	} 
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="${path}/system/metaData/editFileMetaDataTypeAction.action" id="form" modelAttribute="frmFileGroup" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-4 control-label text-right"><span class="required">*</span>分类中文名称：</label>
				<div class="col-xs-5">
					<form:input path="typeName"  class="form-control text-input validate[required, maxSize[50]]"/>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-4 control-label text-right"><span class="required">*</span>分类简称：</label>
				<div class="col-xs-5">
					<form:input path="shortName" class="form-control text-input validate[required, maxSize[6]]" />
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-4 control-label text-right"><span class="required">*</span>分类编码：</label>
				<div class="col-xs-5">
					<form:input path="typeCode" class="form-control text-input validate[required, minSize[4],maxSize[6]]" />
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-4 control-label text-right">存储路径：</label>
				<div class="col-xs-5">
					<form:input path="storePath" class="form-control text-input" />
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-4 control-label text-right">文件格式：</label>
				<div class="col-xs-5">
				    <form:textarea path="formats" rows="4" class="form-control"/>
				</div>
			</div>
			
			
			<div class="form-group">
				<label  class="col-xs-4 control-label text-right">状态：</label>
				<div class="col-xs-5">
					<form:radiobutton path="status" value="1"/>启用
					<form:radiobutton path="status" value="0"/>禁用
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
					<form:hidden path="id"/>
	           		<input type="hidden" name="token" value="${token}" />
	            	<input id="tijiao" type="submit" value="提交" class="btn btn-primary"/>
	                 <input class="btn btn-primary" type="button" value="返回" onclick="returnMain();"/>
	            	</div>
	            </div>
			</div>
			
		</form:form>
		</div>	
	</div>
</body>
</html>