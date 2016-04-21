<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript"> 
	function edit(act){
		document.form.action=act; 
		jQuery('#form').submit();
	}

	function returnList(){
			location.href = "targetmain.jsp";
		}
		
		jQuery(document).ready(function(){
			$('#form').validationEngine('attach', {
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
			$('#form').ajaxSubmit({
 				method: 'post',//方式
 				success:(function(response){
 					callback(response);
           		})
 			});
		}
		function callback(data){
		    top.index_frame.work_main.freshDataTable('data_div');
			$.closeFromInner();
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="${path}/target/updtarget.action" id="form" name="form"  modelAttribute="frmWord" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-5 control-label text-right"><span class="required">*</span>名称：</label>
				<div class="col-xs-5">
					<form:input path="name"  class="form-control validate[required] text-input"/>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right"><span class="required">*</span>类型：</label>
				<div class="col-xs-5">
				    	<app:constants name="type" repository="com.brainsoon.system.support.SystemConstants" className="resTargetType" inputType="select" ignoreKeys="T10,T11,T00" selected="1" cssType="form-control" ></app:constants>
				    </div>
			</div>
			<div class="form-group">
				<label  class="col-xs-5 control-label text-right">状态：</label>
				<div class="col-xs-5">
					<app:constants name="status" repository="com.brainsoon.system.support.SystemConstants" className="TargetStatus" inputType="select" ignoreKeys="T10,T11,T00" selected="1" cssType="form-control" ></app:constants>
				</div>
			</div>
			<%-- <div class="form-group">
			<label  class="col-xs-5 control-label text-right"></label>
				<div class="col-xs-5">
					<form:radiobutton path="platformId" value="1"/>教育资源平台 
					<form:radiobutton path="platformId" value="2"/>出版资源平台 
				</div>
			</div> --%>
			<div class="form-group">
		               <label  class="col-xs-5 control-label text-right">描述：</label>
		                  <div class="col-sm-5">
		                 <form:textarea rows="3"  path="description" class="form-control"></form:textarea>
		                 </div>
			</div>
			<div class="form-group">
				<div class="col-xs-offset-5">
					<div class="form-wrap">
					<form:hidden path="id" />
	           		<input type="hidden" name="token" value="${token}" />
	            	<input id="tijiao" type="button" value="提交" class="btn btn-primary" onclick="edit('${path}/target/updtarget.action')"/> 
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            	</div>
	            </div>
			</div>
		</form:form>
		</div>	
	</div>
</body>
</html>