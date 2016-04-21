<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<style type="text/css">
	#publishType{width: 261px;}
	</style>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
		function returnList(){
			location.href = "sysDirList.jsp";
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
			var title = document.getElementById('lifeCycle');
			var nameValue = "${sysDir.lifeCycle}";
			for(var j=0;j<title.options.length;j++){
				if(title.options[j].value == nameValue){
				title.options[j].selected = true;
				break;
				}
			}
			$('#life').val(nameValue);
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
			top.index_frame.work_main.freshDataTable('data_div');
			$.closeFromInner();
		}
		
		function clean(){
			$('#form1')[0].reset();
			$('#dirEnName').val("");
			$('#dirCnName').val("");
			$('#fileTypes').val("");
			$('#dirDesc').text("");
		}
		
		
		function updateClean(){
			var statusValue = $('#statusValue').val();
			$('select[id=processName] option[value='+statusValue+']').attr('selected',true);
			var types = $('#newType').val();
			$('select[id=resType] option[value='+types+']').attr('selected',true);
			var dirEnName = $('#Ename').val();
			var dirCnName = $('#Cname').val();
			var fileTypes = $('#types').val();
			var dirDesc = $('#Desc').val();
			$('#dirEnName').val(dirEnName);
			$('#dirCnName').val(dirCnName);
			$('#fileTypes').val(fileTypes);
			$('#dirDesc').val(dirDesc);
			
			var life = $('#life').val();
			$('select[id=lifeCycle] option[value='+life+']').attr('selected',true);
			
		}
	</script>
</head>
<body>
	<div class="form-wrap">
	      		<form:form action="" modelAttribute="sysDir" name="form1" id="form1" method="POST"  class="form-horizontal">
		      		<form:hidden path="id"  id="dirId"/>
		      		<input id="statusValue" value="${statusValue}" type="hidden"/>
		      		<div class="form-group">
					    <div class="by-form-title"><label for="status" class="col-sm-4 control-label text-right">资源类型：</label>
					    <div class="col-xs-5">
					    	<input id="newType" value="${sysDir.resType}" type="hidden"/>
					        <app:selectResType name="resType" id="resType"  headName=""  headValue="" selectedVal="${sysDir.resType}"/>
					    </div>
					</div>
					</div>
					<div class="form-group">
						<div class="by-form-title"><label for="status" class="col-sm-4 control-label text-right">生命周期：</label></div>
						<div class="col-xs-5">
							<input id="life" value="" type="hidden"/>
							<app:constants name="lifeCycle" repository="com.brainsoon.system.support.SystemConstants" className="LifeCycle" inputType="select" headerValue="--请选择--" cssType="form-control"></app:constants>
						</div>
					</div>
					<div class="form-group">
						<label for="dirEnName" class="col-sm-4 control-label text-right"><span class="required">*</span>目录关键字：</label>
						<div class="col-xs-5">
							  <c:if test="${id>-1}">
							  <input id="Ename" value="${sysDir.dirEnName}" type="hidden"/>
							  <input id="dirEnName" name="dirEnName" value="${sysDir.dirEnName}" class="form-control validate[required,maxSize[50],ajax[validateDirName]] text-input"/>
							 <%-- <form:input  path="dirEnName" id="dirEnName"  class="form-control validate[required,maxSize[50],ajax[validateDirName]] text-input" /> --%>
							  </c:if>
							  <c:if test="${id==-1}">
							 <form:input  path="dirEnName" id="dirEnName"  class="form-control validate[required,maxSize[50],ajax[validateDirNameAdd]] text-input" />
							  </c:if>
						</div>
					</div>
					<div class="form-group">
						<label for="dirCnName" class="col-sm-4 control-label text-right">目录值：</label>
						<div class="col-xs-5">
						    <input id="Cname" value="${sysDir.dirCnName}" type="hidden"/>
							<input id="dirCnName" name="dirCnName" value="${sysDir.dirCnName}" class="form-control"/>
							 <%-- <form:input  path="dirCnName" id="dirCnName"  class="form-control" /> --%>
						</div>
					</div>
					<div class="form-group">
						<label for="fileTypes" class="col-sm-4 control-label text-right">目录下文件类型：</label>
						<div class="col-xs-5">
							<input id="types" value="${sysDir.fileTypes}" type="hidden"/>
							<input id="fileTypes" name="fileTypes" value="${sysDir.fileTypes}" class="form-control"/>
							 <%-- <form:input  path="fileTypes" id="fileTypes"  class="form-control" /> --%>
						</div>
					</div>
					
					<div class="form-group">
						<label for="status" class="col-sm-4 control-label text-right">状态：</label>
						<div class="col-xs-5">
							 <form:select path="status"  id="processName" items="${status}" class="form-control" />
						</div>
					</div>
					
			      <div class="form-group">
						<label for="dirDesc" class="col-sm-4 control-label text-right">描述：</label>
						<div class="col-xs-5">
							<input id="Desc" value="${sysDir.dirDesc}" type="hidden"/>
							<textarea rows="3" cols="" id="dirDesc" name="dirDesc" class="form-control validate[maxSize[50]]">${sysDir.dirDesc}</textarea>
						     <%-- <form:textarea path="dirDesc" rows="3" class="form-control validate[maxSize[50]]"/>  --%>
						</div>
				</div>
				<div class="form-group">
						<div class="col-sm-offset-4 col-sm-6">
			           		<input type="hidden" name="token" value="${token}" />
			           		<c:if test="${id>-1}">
   					  			 <button type="button"  class="btn btn-primary" onclick="edit('${path}/sysDir/update.action')">保存</button>
   							 </c:if>
   							
   							  <c:if test="${id==-1}">
   							   <button type="button"  class="btn btn-primary" onclick="edit('${path}/sysDir/add.action')">保存</button>
   							 </c:if>
   							  &nbsp;
   							  <c:if test="${id==-1}">
   							  	<input type="button" class="btn btn-primary" value="重置" onclick="clean()"/>
   							  </c:if>
   							  <c:if test="${id>-1}">
      							<input onclick="updateClean()" class="btn btn-primary" type="button" value="重置"/>
      						  </c:if> 
   							   &nbsp; 
			            	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
			            </div>
				</div>
					
			</form:form>   	
  		  </div>
</body>
</html>