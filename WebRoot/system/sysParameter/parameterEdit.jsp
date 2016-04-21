<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	
		function returnList(){
			location.href = "parameterList.jsp";
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
		//添加时重置
		function clean(){
			$('#form1')[0].reset();
			$('#paraKey').val("");
			$('#paraValue').val("");
			$('#paraDesc').text("");
		}
		
		//修改时重置
		function updateClean(){
			var status = $('#statusValue').val();
			var type= $('#typeValue').val();
			$('select[id=paraStatus] option[value='+status+']').attr('selected',true);
			$('select[id=paraType] option[value='+type+']').attr('selected',true);
			var paraKey = $('#newpara').val();
			var paraValue = $('#newValue').val();
			var paraDesc = $('#newDesc').val();
			$('#paraKey').val(paraKey);
			$('#paraValue').val(paraValue);
			$('#paraDesc').val(paraDesc);
		}
	</script>
</head>
<body>
	<div class="form-wrap">
	      		<form:form action="" modelAttribute="sysParameter" name="form1" id="form1" method="POST"  class="form-horizontal">
		      		<form:hidden path="id"  id="paraKeyId"/>
		      		<input  id="statusValue" value="${statusInt}" type="hidden"/>
		      		<input id="typeValue" value="${typeValue}" type="hidden"/>
					<div class="form-group">
						<label for="paraKey" class="col-sm-4 control-label text-right"><span class="required">*</span>参数key：</label>
						<div class="col-xs-5">
						     <c:if test="${id>-1}">
						     <input id="newpara" value="${sysParameter.paraKey}" type="hidden"/>
						     <input id="paraKey" name="paraKey" class="form-control validate[required, maxSize[50],ajax[validateParamKey]] text-input" value="${sysParameter.paraKey}"/>
							<%-- <form:input  path="paraKey" id="paraKey"  class="form-control validate[required, maxSize[50],ajax[validateParamKey]] text-input"/> --%>
							  </c:if>
							  <c:if test="${id==-1}">
							<form:input  path="paraKey" id="paraKey"  class="form-control validate[required, maxSize[200],ajax[validateParamKeyAdd]] text-input"/>
							  </c:if>
							 
						</div>
					</div>
					
					<div class="form-group">
						<label for="paraValue" class="col-sm-4 control-label text-right">参数值：</label>
						<div class="col-xs-5">
							<input id="newValue" type="hidden" value="${sysParameter.paraValue}"/>
							<input id="paraValue" name="paraValue" value="${sysParameter.paraValue}" class="form-control  validate[maxSize[200],custom[paraValue]] text-input"/>
							 <%-- <form:input  path="paraValue" id="paraValue"  class="form-control  validate[maxSize[50],custom[paraValue]] text-input" /> --%>
						</div>
					</div>
					<div class="form-group">
						<label  class="col-sm-4 control-label text-right">类型：</label>
						<div class="col-xs-5">
								<form:select path="paraType"  id="paraType" items="${type}" class="form-control" /> 
							</div>
					</div>
					
					<div class="form-group">
						<label for="paraStatus" class="col-sm-4 control-label text-right">状态：</label>
						<div class="col-xs-5">
							<form:select path="paraStatus"  id="paraStatus" items="${status}" class="form-control" />
						</div>
					</div>
					
			      <div class="form-group">
						<label for="paraDesc" class="col-sm-4 control-label text-right">描述：</label>
						<div class="col-xs-5">
							<input type="hidden" id="newDesc" value="${sysParameter.paraDesc}"/>
							<textarea rows="3" cols="" id="paraDesc" name="paraDesc" class="form-control validate[maxSize[50]]">${sysParameter.paraDesc}</textarea>
						     <%-- <form:textarea path="paraDesc" rows="3" class="form-control validate[maxSize[50]]"/>  --%>
						</div>
				</div>
				<div class="form-group">
						<div class="col-sm-offset-4 col-sm-6">
			           		<input type="hidden" name="token" value="${token}" />
			           		<c:if test="${id>-1}">
   					  			 <button type="button"  class="btn btn-primary" onclick="edit('${path}/sysParameter/update.action')">保存</button>
   							 </c:if>
   							
   							  <c:if test="${id==-1}">
   							   <button type="button"  class="btn btn-primary" onclick="edit('${path}/sysParameter/add.action')">保存</button>
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