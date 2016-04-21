<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<html>
    <head>
    <title>人员信息查看</title>
    </head>
     <body>
      	<div class="form-wrap">
	      		<form:form action="${path}/staff/view.action"  name="form1" id="form1" method="POST" modelAttribute="company"  class="form-horizontal">
		      		<div class="form-group">
						<label for="name" class="col-sm-4 control-label text-right"><span class="required">*</span>姓名：</label>
						<p class="form-control-static">
		               		${staff.name}
		               	</p>
					</div>
					<div class="form-group">
						<label for="sex" class="col-sm-4 control-label text-right">性别：</label>
						<p class="form-control-static">
		               		${staff.sex}
		               	</p>
					</div>
					
					<div class="form-group">
						<label for="age" class="col-sm-4 control-label text-right">年龄：</label>
						<p class="form-control-static">
		               		${staff.age}
		               	</p>
					</div>
					
					<div class="form-group">
						<label for="workName" class="col-sm-4 control-label text-right">团体或组织单位名称：</label>
						<p class="form-control-static">
							${staff.workName}
						</p>
					</div>
					
					<div class="form-group">
						<label for="identity" class="col-sm-4 control-label text-right">职位：</label>
						<p class="form-control-static">
		               		${staff.identity}
		               	</p>
					</div>
					<div class="form-group">
						<label for="birthday" class="col-sm-4 control-label text-right">出生日期：</label>
						<p class="form-control-static">
		               		${staff.birthday}
		               	</p>
					</div>
					
					<div class="form-group">
						<label for="address" class="col-sm-4 control-label text-right">家庭住址：</label>
						<p class="form-control-static">
		               		${staff.address}
		               	</p>
					</div>
					<div class="form-group">
						<label for="telephone" class="col-sm-4 control-label text-right">联系电话：</label>
						<p class="form-control-static">
		               		${staff.telephone}
		               	</p>
					</div>
					<div class="form-group">
						<label for="userType" class="col-sm-4 control-label text-right">人员类型：</label>
						<p class="form-control-static">
		               		${staff.userType}
		               	</p>
					</div>
					<div class="form-group">
						<label for="cardNumber" class="col-sm-4 control-label text-right">身份证号：</label>
						<p class="form-control-static">
		               		${staff.cardNumber}
		               	</p>
					</div>
					<div class="form-group">
						<label for="code" class="col-sm-4 control-label text-right">邮政编码：</label>
						<p class="form-control-static">
		               		${staff.code}
		               	</p>
					</div>
					
				  <div class="form-group">
						<div class="col-sm-offset-4 col-sm-5">
			            	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
			            </div>
				 </div>

			</form:form>
  		  </div>

    </body>
</html>