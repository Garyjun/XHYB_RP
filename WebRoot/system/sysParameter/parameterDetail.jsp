<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
    <title>查看</title>  
    </head>
     <body>
      	<div class="form-wrap">
	      		<form:form action="${path}/sysParameter/view.action"  name="form1" id="form1" method="POST" modelAttribute="sysParameter"  class="form-horizontal">
		      		<div class="form-group">
						<label for="paraKey" class="col-sm-4 control-label text-right">参数key：</label>
						<div class="col-sm-5">
						             <p class="form-control-static">
		               					${sysParameter.paraKey}
		               				 </p>
						</div>
					</div>
					
					<div class="form-group">
						<label for="paraValue" class="col-sm-4 control-label text-right">参数值：</label>
						<div class="col-sm-5">
							  <p class="form-control-static">
		               					${sysParameter.paraValue}
		               		 </p>
						</div>
					</div>
					
					<div class="form-group">
						<label for="paraType" class="col-sm-4 control-label text-right">类型：</label>
						<div class="col-sm-5">
							 <p class="form-control-static">
		               					${sysParameter.paraTypeDesc}
		               		 </p>
						</div>
					</div>
					<div class="form-group">
						<label for="paraStatus" class="col-sm-4 control-label text-right">状态：</label>
						<div class="col-sm-5">
		               					${sysParameter.paraStatusDesc}
		               		 
						</div>
					</div>
					
			    <div class="form-group">
						<label for="paraDesc" class="col-sm-4 control-label text-right">描述：</label>
						<div class="col-sm-5">
						     <textarea rows="3"  disabled="disabled" name="desc" class="form-control">${sysParameter.paraDesc}</textarea>
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