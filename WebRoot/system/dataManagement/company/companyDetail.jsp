<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<html>
    <head>
    <title>单位信息查看</title>
    </head>
     <body>
      	<div class="form-wrap">
	      		<form:form action="${path}/company/view.action"  name="form1" id="form1" method="POST" modelAttribute="company"  class="form-horizontal">
		      		<div class="form-group">
						<label for="name" class="col-sm-4 control-label text-right"><span class="required">*</span>单位名称：</label>
						<p class="form-control-static">
		               		${company.name}
		               	</p>
					</div>
					<div class="form-group">
						<label for="shortName" class="col-sm-4 control-label text-right">单位简称：</label>
						<p class="form-control-static">
		               		${company.shortName}
		               	</p>
					</div>
					
					<div class="form-group">
						<label for="address" class="col-sm-4 control-label text-right">单位地址：</label>
						<p class="form-control-static">
		               		${company.address}
		               	</p>
					</div>

					<div class="form-group">
						<label for="code" class="col-sm-4 control-label text-right">邮政编码：</label>
						<p class="form-control-static">
		               		${company.code}
		               	</p>
					</div>
					<div class="form-group">
						<label for="email" class="col-sm-4 control-label text-right">电子邮件：</label>
						<p class="form-control-static">
		               		${company.email}
		               	</p>
					</div>
					
					<div class="form-group">
						<label for="companyType" class="col-sm-4 control-label text-right">单位类型：</label>
						<p class="form-control-static">
		               		${company.companyType}
		               	</p>
					</div>
					<div class="form-group">
						<label for="contactFirst" class="col-sm-4 control-label text-right">联系人1：</label>
						<p class="form-control-static">
		               		${company.contactFirst}
		               	</p>
					</div>
					<div class="form-group">
						<label for="telephoneFirst" class="col-sm-4 control-label text-right">联系人1电话：</label>
						<p class="form-control-static">
		               		${company.telephoneFirst}
		               	</p>
					</div>
					<div class="form-group">
						<label for="contactSecond" class="col-sm-4 control-label text-right">联系人2：</label>
						<p class="form-control-static">
		               		${company.contactSecond}
		               	</p>
					</div>
					<div class="form-group">
						<label for="telephoneSecond" class="col-sm-4 control-label text-right">联系人2电话：</label>
						<p class="form-control-static">
		               		${company.telephoneSecond}
		               	</p>
					</div>
			      <div class="form-group">
						<label for="description" class="col-sm-4 control-label text-right">简介：</label>
						<div class="col-xs-5">
							<textarea rows="3"  disabled="disabled" name="description" class="form-control" style="width:400px;" title="${company.description}">${company.description}</textarea>
						</div>
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