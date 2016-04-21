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
			location.href = "upd.action?id="+$("#dictNameId").val();
		}
		
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="updDictValue.action" id="form" modelAttribute="frmDictValue" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">参数名称：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmDictValue.name}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">参数简称：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmDictValue.shortname}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">参数值：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmDictValue.indexTag}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">描述：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmDictValue.description}</p>
				</div>
			</div>
			<div class="form-group">
				<div class="col-xs-offset-5">
				<div class="form-wrap">
					<form:hidden path="id" />
	           		<form:hidden path="pid" id="dictNameId"/>
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