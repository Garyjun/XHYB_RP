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
			location.href = "wordList.jsp";
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="updDictValue.action" id="form" modelAttribute="frmWord" method="post" class="form-horizontal" role="form">
<!-- 			<div class="form-group"> -->
<!-- 				<label class="col-xs-5 control-label text-right">状态：</label> -->
<!-- 				<div class="col-xs-5"> -->
<%-- 					<c:if test="${frmWord.status=='1'}"> --%>
<!-- 						<p class="form-control-static">启用</p> -->
<%-- 					</c:if> --%>
<%-- 					<c:if test="${frmWord.status=='2'}"> --%>
<!-- 						<p class="form-control-static">禁用</p> -->
<%-- 					</c:if>					 --%>
<!-- 				</div> -->
<!-- 			</div> -->
			<div class="form-group">
				    <label class="col-xs-5 control-label text-right">资源类型：</label>
				   <div class="col-xs-5">
				        <app:selectResType name="module" id="module" selectedVal="${module}"  headName="全部"  headValue="" readonly="1"/>
				    </div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">标签名称：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.targetName}</p>
				</div>
			</div>
			<div class="form-group">
				    <label  class="col-xs-5 control-label text-right">使用次数：</label>
				   <div class="col-xs-5">
				   <p class="form-control-static">${frmWord.targetNum}</p>
				    </div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">描述：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.description}</p>
				</div>
			</div>			
			<div class="form-group">
				<div class="col-xs-offset-5">
					<div class="form-wrap">
					<form:hidden path="id" />
	           		<input type="hidden" name="token" value="${token}" />
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            	</div>
	            </div>
			</div>
		</form:form>
		</div>	
	</div>
</body>
</html>