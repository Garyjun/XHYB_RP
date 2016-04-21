<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	
 	$(function(){
 	var codeName = document.getElementById("codeName");
 	var codeDefault = document.getElementById("codeDefault");
	if($('#type').val()==3)
	{
		codeName.innerHTML="无";
		codeDefault.innerHTML="无";
	}
 	});
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="updDictValue.action" id="form" modelAttribute="frmWord" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">字典类型：</label>
				<input type="hidden" name="type" id="type" value="${frmWord.adapterType}"></input>
				<div class="col-xs-5">
					<c:if test="${frmWord.adapterType=='00'}">
						<p class="form-control-static">资源类型</p>
					</c:if>
					<c:if test="${frmWord.adapterType=='01'}">
						<p class="form-control-static">分册</p>
					</c:if>
					<c:if test="${frmWord.adapterType=='02'}">
						<p class="form-control-static">学科</p>
					</c:if>
					<c:if test="${frmWord.adapterType=='03'}">
						<p class="form-control-static">年级</p>
					</c:if>								
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">集成方名称：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.adapterName}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">集成方编码：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.adapterCode}</p>
				</div>
			</div>
			<div class="form-group" id="localCodeName">
				<label class="col-xs-5 control-label text-right">本系统名称：</label>
				<div class="col-xs-5">
					<p id="codeName" class="form-control-static">${frmWord.codeName}</p>
				</div>
			</div>
			<div class="form-group" id="localCode">
				<label class="col-xs-5 control-label text-right">本系统编码：</label>
				<div class="col-xs-5">
					<p id="codeDefault" class="form-control-static">${frmWord.codeDefault}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">状态：</label>
				<div class="col-xs-5">
					<c:if test="${frmWord.codeStatus=='1'}">
						<p class="form-control-static">启用</p>
					</c:if>
					<c:if test="${frmWord.codeStatus=='0'}">
						<p class="form-control-static">禁用</p>
					</c:if>					
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">版本：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.adapterVer}</p>
				</div>
			</div>
			<div class="form-group">
				<div class="col-xs-offset-5">
					<div class="form-wrap">
					<form:hidden path="codeId" />
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