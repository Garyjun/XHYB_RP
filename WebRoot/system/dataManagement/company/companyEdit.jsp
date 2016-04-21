<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>单位管理添加/编辑</title>
	<script type="text/javascript">

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
			if($('#fromPeopleUnit').val()!=""){
				$.closeFromInner();
			}
// 			top.index_frame.freshDataTable('data_div'); 没有main时用这种方式
			top.index_frame.work_main.freshDataTable('data_div');
			$.closeFromInner();
		}
	</script>
</head>
<body>
	<div class="form-wrap">
	      		<form:form action="" modelAttribute="company" name="form1" id="form1" method="POST"  class="form-horizontal">
		      		<form:hidden path="id"  id="id"/>
		      		<form:hidden path="createdTime"  id="createdTime"/>
		      		<form:hidden path="createder"  id="createder"/>
		      		<input type="hidden"  name="fromPeopleUnit" id="fromPeopleUnit" value="${fromPeopleUnit}"/>
					<div class="form-group">
						<label for="name" class="col-sm-4 control-label text-right"><span class="required">*</span>单位名称：</label>
						<div class="col-xs-5">
							<form:input  path="name" id="name"  class="form-control validate[required, maxSize[200]] text-input"/>
						</div>
					</div>
					<div class="form-group">
						<label for="shortName" class="col-sm-4 control-label text-right">单位简称：</label>
						<div class="col-xs-5">
							<form:input  path="shortName" id="shortName"  class="form-control text-input"/>
						</div>
					</div>
					
					<div class="form-group">
						<label for="address" class="col-sm-4 control-label text-right">单位地址：</label>
						<div class="col-xs-5">
							<form:input path="address"  id="address" class="form-control" />
						</div>
					</div>

					<div class="form-group">
						<label for="code" class="col-sm-4 control-label text-right">邮政编码：</label>
						<div class="col-xs-5">
								<form:input path="code"  id="code" class="form-control validate[custom[code]]" />
						</div>
					</div>
					<div class="form-group">
						<label for="email" class="col-sm-4 control-label text-right">电子邮件：</label>
						<div class="col-xs-5">
								<form:input path="email"  id="email" class="form-control validate[custom[newemail]] text-input" />
						</div>
					</div>
					
					<div class="form-group">
						<label for="companyType" class="col-sm-4 control-label text-right">单位类型：</label>
						<div class="col-xs-5">
								<app:select name="companyType" indexTag="companyType" id="companyType"  selectedVal="${company.companyType}" headName="请选择"  headValue=""  />
						</div>
					</div>
					<div class="form-group">
						<label for="contactFirst" class="col-sm-4 control-label text-right">联系人1：</label>
						<div class="col-xs-5">
								<form:input path="contactFirst"  id="contactFirst" class="form-control text-input" />
						</div>
					</div>
					<div class="form-group">
						<label for="telephoneFirst" class="col-sm-4 control-label text-right">联系人1电话：</label>
						<div class="col-xs-5">
								<form:input path="telephoneFirst"  id="telephoneFirst" class="form-control validate[custom[mobile]]" />
						</div>
					</div>
					<div class="form-group">
						<label for="contactSecond" class="col-sm-4 control-label text-right">联系人2：</label>
						<div class="col-xs-5">
								<form:input path="contactSecond"  id="contactSecond" class="form-control" />
						</div>
					</div>
					<div class="form-group">
						<label for="telephoneSecond" class="col-sm-4 control-label text-right">联系人2电话：</label>
						<div class="col-xs-5">
								<form:input path="telephoneSecond"  id="telephoneSecond" class="form-control validate[custom[mobile]]" />
						</div>
					</div>
			      <div class="form-group">
						<label for="description" class="col-sm-4 control-label text-right">简介：</label>
						<div class="col-xs-5">
						     <form:textarea path="description" id="description" rows="3" class="form-control validate[maxSize[500]]"/>
						</div>
				 </div>

				<div class="form-group">
						<div class="col-sm-offset-4 col-sm-6">
			           		<input type="hidden" name="token" value="${token}" />
			           		<c:if test="${id>-1}">
   					  			 <button type="button"  class="btn btn-primary" onclick="edit('${path}/company/update.action')">保存</button>
   							</c:if>
   							<c:if test="${id eq null || id eq ''}">
   							   <button type="button"  class="btn btn-primary" onclick="edit('${path}/company/add.action')">保存</button>
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