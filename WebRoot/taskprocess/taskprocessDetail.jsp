<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<html>
    <head>
    <title>加工任务查看</title>
    </head>
     <body>
      	<div class="form-wrap">
	      		<form:form action="${path}/taskProcess/view.action"  name="form1" id="form1" method="POST" modelAttribute="taskProcess"  class="form-horizontal">
		      		<div class="form-group">
						<label for="taskName" class="col-sm-4 control-label text-right">名称：</label>
						<div class="col-sm-5">
				             <p class="form-control-static" style="white-space:nowrap; width:400px; overflow:hidden;  text-overflow:ellipsis;" title="${taskProcess.taskName}">
               							${taskProcess.taskName}
               				 </p>
						</div>
					</div>

					<div class="form-group">
						<label for="batchNumber" class="col-sm-4 control-label text-right">批次：</label>
						<div class="col-sm-5">
							  <p class="form-control-static">
		               					${taskProcess.batchNumber}
		               		 </p>
						</div>
					</div>

					<div class="form-group">
						<label for="paraType" class="col-sm-4 control-label text-right">数量：</label>
						<div class="col-sm-5">
							 <p class="form-control-static">
		               					${taskProcess.processNumber}
		               		 </p>
						</div>
					</div>

					<div class="form-group">
						<label for="paraType" class="col-sm-4 control-label text-right">建议人数：</label>
						<div class="col-sm-5">
							 <p class="form-control-static">
		               					${taskProcess.personNumber}
		               		 </p>
						</div>
					</div>

					<div class="form-group">
						<label for="paraType" class="col-sm-4 control-label text-right">预计开始时间：</label>
						<div class="col-sm-5">
							 <p class="form-control-static">
		               					<fmt:formatDate value="${taskProcess.startTime}" pattern="yyyy-MM-dd" />
		               		 </p>
						</div>
					</div>

					<div class="form-group">
						<label for="paraType" class="col-sm-4 control-label text-right">要求完成时间：</label>
						<div class="col-sm-5">
							 <p class="form-control-static">
		               					<fmt:formatDate value="${taskProcess.endTime}" pattern="yyyy-MM-dd" />
		               		 </p>
						</div>
					</div>

					<div class="form-group">
						<label for="paraStatus" class="col-sm-4 control-label text-right">优先级：</label>
						<div class="col-sm-5">
							<p class="form-control-static">
		               					${taskProcess.priorityDesc}
		               		</p>
						</div>
					</div>

					<%-- <div class="form-group">
						<label for="paraStatus" class="col-sm-4 control-label text-right">状态：</label>
						<div class="col-sm-5">
							<p class="form-control-static">
		               					${taskProcess.statusDesc}
		               		</p>

						</div>
					</div> --%>
					
					<div class="form-group">
						<label for="paraStatus" class="col-sm-4 control-label text-right">资源类型：</label>
						<div class="col-sm-5">
							<p class="form-control-static">
		               					${taskProcess.resTypeDesc}
		               		</p>

						</div>
					</div>

			    <div class="form-group">
						<label for="description" class="col-sm-4 control-label text-right">描述：</label>
						<div class="col-sm-5">
						     <textarea rows="3"  disabled="disabled" name="description" class="form-control" style="width:400px;" title="${taskProcess.description}">${taskProcess.description}</textarea>
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