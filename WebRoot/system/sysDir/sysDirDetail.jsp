<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
    <title>查看</title>     
    </head>
     <body>
      	<div class="form-wrap">
	      		<form:form action=""  name="form1" modelAttribute="sysDir" id="form1" method="POST"  class="form-horizontal">
		      		<div class="form-group">
						<label for="resTypeNum" class="col-sm-4 control-label text-right">资源类型：</label>
						<div class="col-sm-5">
						             <p class="form-control-static">
		               					${sysDir.resTypeNum}
		               				 </p>
								 
						</div>
					</div>
					<div class="form-group">
						<label for="lifeCycleDesc" class="col-sm-4 control-label text-right">生命周期：</label>
						<div class="col-sm-5">
						             <p class="form-control-static">
		               					${sysDir.lifeCycleDesc}
		               				 </p>
								 
						</div>
					</div>
		      		<div class="form-group">
						<label for="dirEnName" class="col-sm-4 control-label text-right">目录关键字：</label>
						<div class="col-sm-5">
						             <p class="form-control-static">
		               					${sysDir.dirEnName}
		               				 </p>
								 
						</div>
					</div>
					
					<div class="form-group">
						<label for="dirCnName" class="col-sm-4 control-label text-right">目录值：</label>
						<div class="col-sm-5">
							  <p class="form-control-static">
		               					${sysDir.dirCnName}
		               		 </p>
						</div>
					</div>
					
					<div class="form-group">
						<label for="fileTypes" class="col-sm-4 control-label text-right">目录下文件类型：</label>
						<div class="col-sm-5">
							 <p class="form-control-static">
		               					${sysDir.fileTypes}
		               		 </p>
						</div>
					</div>
					<div class="form-group">
						<label for="status" class="col-sm-4 control-label text-right">状态：</label>
						<div class="col-sm-5">
							  <p class="form-control-static">
		               					${sysDir.statusDesc}
		               		 </p>
						</div>
					</div>
					
			    <div class="form-group">
						<label for="dirDesc" class="col-sm-4 control-label text-right">描述：</label>
						<div class="col-sm-5">
						     <textarea rows="3"  disabled="disabled" name="dirDesc" class="form-control">${sysDir.dirDesc}</textarea>
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