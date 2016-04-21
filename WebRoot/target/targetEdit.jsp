<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/jQueryValidationEngine/js/languages/jquery.validationEngine-zh_CN.js" charset="utf-8"></script>
	
	<script type="text/javascript"> 
	var num =1;
	function edit(act){
		document.form.action=act;
		var status = checkTarget();
		if(status=='1'){
			return;
		}else{
			jQuery('#form').submit();
		}
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
						//防止提交两次
						if(num==1){
							num++;
							save();
						}
					}
				}
			});
			$('#module').change(function(){
				checkTarget();
			});	
		});
		function checkTarget(){
			var targetName = $('#targetName').val();
			targetName = encodeURI(encodeURI(targetName));
			var status = "0";
			 $.ajax({
					url:"${path}/target/checkTarget.action?publishType="+$('#module').val()+"&targetName="+targetName,
					async:false,
					success:function(data){
						if(data=='1'){
							$.alert("标签重复不能添加");
							status = "1";
						}
					}
				});
			 return status;
		}
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
		//查重
// 		function checkRepeat(){
// 			alert($('#name').val());
// 			$.post('${path}/target/checkRepeat.action?name='+$('#name').val(),
// 			function(data){
// 				data = eval('('+data+')');
// 				alert(data.status);
// 				if(data.status == 1){
// 					confirmRepeat(data.res);
// 				}else{
// 					saveForm();
// 				}
// 			});
// 		}
		//标签分类
		$(function(){
			targetType();
		});
		function targetType(){
			$.ajax({
				type : "post",
				url : "${path}/publishRes/targetType.action",
				success : function(data) {
					var json = JSON.parse(data);
					var fieldName = "";
					var content = "";
					content ="<option value = '通用标签'>通用标签</option>";
					for(var i in json){
			 			var info = json[i];
				 		content +="<option value = '"+i+"'>"+info+"</option>";
			 		}
					$("#targetType").append(content);
				}
			});
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="" id="form" name="form"  modelAttribute="frmWord" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				    <label  class="col-xs-5 control-label text-right">资源类型：</label>
				   <div class="col-xs-5">
				        <app:selectResType name="module" id="module" selectedVal="${module}"  headName=""  headValue="" />
				    </div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right"><span class="required">*</span>标签名称：</label>
				<div class="col-xs-5">
					<form:input path="targetName"  class="form-control validate[required, maxSize[50],ajax[validateTargetName]] text-input" />
				</div>
			</div>
			<div class="form-group">
					<label class="col-xs-5 control-label text-right"><span class="required">*</span>标签分类：</label>
					<div class="col-xs-5">
						<select  id="targetType" name="targetType" class="form-control validate[required] text-input"></select>
					</div>
			</div>
<!-- 			<div class="form-group"> -->
<!-- 				    <label  class="col-xs-5 control-label text-right">使用次数：</label> -->
<!-- 				   <div class="col-xs-5"> -->
<%-- 				       <form:input path="targetNum"  class="form-control text-input "/> --%>
<!-- 				    </div> -->
<!-- 			</div> -->
<!-- 			<div class="form-group"> -->
<!-- 				<label  class="col-xs-5 control-label text-right">状态：</label> -->
<!-- 				<div class="col-xs-5"> -->
<%-- 					<app:constants name="targetStatus" repository="com.brainsoon.system.support.SystemConstants" className="targetStatus" inputType="select" ignoreKeys="T10,T11,T00" selected="${statas}" cssType="form-control" ></app:constants> --%>
<!-- 				</div> -->
<!-- 			</div> -->
			<div class="form-group">
		               <label  class="col-xs-5 control-label text-right">描述：</label>
		                  <div class="col-sm-5">
		                 <form:textarea rows="3"  path="description" class="form-control"></form:textarea>
		                 </div>
			</div>
			<div class="form-group">
				<div class="col-xs-offset-5">
					<div class="form-wrap">
					<form:hidden path="id" id="targetId"/>
	           		<input type="hidden" name="token" value="${token}" />
	            	<input type="button" value="提交" class="btn btn-primary" onclick="edit('${path}/target/updtarget.action')"/> 
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            	</div>
	            </div>
			</div>
		</form:form>
		</div>	
	</div>
</body>
</html>