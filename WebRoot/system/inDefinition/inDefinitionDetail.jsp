<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>修改</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	function mData(){
		//location.href='metaDataList.jsp?pid='+$("#inDefinitionId").val();	
		location.href='${path}/system/inDefinition/metaDataList.action?pid='+$("#inDefinitionId").val();
	}
	function dictVin(){
		location.href='${path}/code/list.action?id='+$("#inDefinitionId").val();
	}
	function upData(){
		var id = $("#inDefinitionId").val();
		location.href='${path}/system/inDefinition/up.action?id='+id;
	}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form id="form" target="_parent" modelAttribute="frmDefinition" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-4 control-label text-right">名称：</label>
				<div class="col-xs-3">
					<p class="form-control-static">${frmDefinition.name}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-4 control-label text-right">简称：</label>
				<div class="col-xs-3">
					<p class="form-control-static">${frmDefinition.nameAbbr}</p>
				</div>
			</div>
			<div class="form-group">
				<label  class="col-xs-4 control-label text-right">状态：</label>
				<div class="col-xs-3">
					<c:if test="${frmDefinition.status=='1'}">
						<p class="form-control-static">可用</p>
					</c:if>
					<c:if test="${frmDefinition.status=='0'}">
						<p class="form-control-static">禁用</p>
					</c:if>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-4 control-label text-right">url：</label>
				<div class="col-xs-3">
					<p class="form-control-static">${frmDefinition.url}</p>
				</div>
			</div>	
			
			<div class="form-group">
			    <div class="col-xs-offset-4">
					<div class="form-wrap">
					<form:hidden path="id" id="inDefinitionId"/>
	           		<input type="hidden" name="token" value="${token}" />
	           		<input class="btn btn-primary" type="button" value="元数据项定制" onclick="mData();"/>
	           		<input class="btn btn-primary" type="button" value="数据字典维护" onclick="dictVin();"/>
	           		<sec:authorize url="/system/inDefinition/up.action">
	           		<input class="btn btn-primary" type="button" value="修改" onclick="upData();"/>
	           		</sec:authorize> 
	            	<input class="btn btn-primary" type="button" value="返回" onclick="parent.returnMain();"/>
	            	</div>
	            </div>
			</div>
		</form:form>
		</div>	
	</div>
</body>
</html>