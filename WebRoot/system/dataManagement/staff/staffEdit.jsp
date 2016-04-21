<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>人员管理添加/编辑</title>
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
	      		<form:form action="" modelAttribute="staff" name="form1" id="form1" method="POST"  class="form-horizontal">
		      		<form:hidden path="id"  id="id"/>
		      		<input type="hidden"  name="fromPeopleUnit" id="fromPeopleUnit" value="${fromPeopleUnit}"/>
					<div class="form-group">
						<label for="name" class="col-sm-4 control-label text-right"><span class="required">*</span>姓名：</label>
						<div class="col-xs-5">
								<form:input  path="name" id="name"  class="form-control text-input validate[required]"/>
						</div>
					</div>
					
					<div class="form-group">
						<label for="sex" class="col-sm-4 control-label text-right">性别：</label>
						<div class="col-xs-5">
								<app:select name="sex" indexTag="sex" id="sex"  selectedVal="${staff.sex}" headName="请选择"  headValue=""  />
						</div>
					</div>

					<div class="form-group">
						<label for="age" class="col-sm-4 control-label text-right">年龄：</label>
						<div class="col-xs-5">
								<form:input path="age"  id="age" class="form-control validate[custom[onlyNumberSp]]" />
						</div>
					</div>
					<div class="form-group">
						<label for="workName" class="col-sm-4 control-label text-right">团体或组织单位名称：</label>
						<div class="col-xs-5">
								<form:input path="workName"  id="workName" class="form-control" />
						</div>
					</div>
					<div class="form-group">
						<label for="identity" class="col-sm-4 control-label text-right">职位：</label>
						<div class="col-xs-5">
								<form:input path="identity"  id="identity" class="form-control" />
						</div>
					</div>
					<div class="form-group">
						<label for="telephone" class="col-sm-4 control-label text-right">联系电话：</label>
						<div class="col-xs-5">
								<form:input path="telephone"  id="telephone" class="form-control" />
						</div>
					</div>
					
					<div class="form-group">
						<label for="birthday" class="col-sm-4 control-label text-right">出生日期：</label>
						<div class="col-xs-5">
								<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" id="birthday" name="birthday" value="${staff.birthday}" />
						</div>
					</div>
					
					<div class="form-group">
						<label for="cardNumber" class="col-sm-4 control-label text-right">身份证号：</label>
						<div class="col-xs-5">
								<form:input path="cardNumber"  id="cardNumber" class="form-control validate[custom[card]]" />
						</div>
					</div>
					<div class="form-group">
						<label for="userType" class="col-sm-4 control-label text-right">人员类型：</label>
						<div class="col-xs-5">
								<app:select name="userType" indexTag="staff_type" id="userType"  selectedVal="${staff.userType}" headName="请选择"  headValue=""  />
						</div>
					</div>
					<div class="form-group">
						<label for="address" class="col-sm-4 control-label text-right">家庭住址：</label>
						<div class="col-xs-5">
								<form:input path="address"  id="address" class="form-control text-input" />
						</div>
					</div>
					
					<div class="form-group">
						<label for="code" class="col-sm-4 control-label text-right">邮政编码：</label>
						<div class="col-xs-5">
								<form:input path="code"  id="code" class="form-control validate[custom[code]]" />
						</div>
					</div>
					
					
					

				<div class="form-group">
						<div class="col-sm-offset-4 col-sm-6">
			           		<input type="hidden" name="token" value="${token}" />
			           		<c:if test="${id>-1}">
   					  			 <button type="button"  class="btn btn-primary" onclick="edit('${path}/staff/update.action')">保存</button>
   							</c:if>
   							<c:if test="${id eq null || id eq ''}">
   							   <button type="button"  class="btn btn-primary" onclick="edit('${path}/staff/add.action')">保存</button>
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