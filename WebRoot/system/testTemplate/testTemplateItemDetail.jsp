<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="" id="form" modelAttribute="frmtestTemplateItem" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">题型：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmtestTemplateItem.testType}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">排序ID：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmtestTemplateItem.count}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">题型关键字：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmtestTemplateItem.testTypeKey}</p>
				</div>
			</div>
			<div class="form-group">
				<div class="col-xs-offset-5">
				<div class="form-wrap">
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