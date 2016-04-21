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
	<style type="text/css">
		pre { 
			margin-left: -2px;
			white-space: pre-wrap; /* css-3 */ 
			white-space: -moz-pre-wrap; /* Mozilla, since 1999 */ 
			white-space: -pre-wrap; /* Opera 4-6 */ 
			white-space: -o-pre-wrap; /* Opera 7 */ 
			word-wrap: break-word; /* Internet Explorer 5.5+ */ 
			background-color: #FFF;
			border: 0px thin #FFF;
			padding-top: -32px;
			margin-top: -6px;
		} 
	</style>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="updDictValue.action" id="form" modelAttribute="frmWord" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">敏感词：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.name}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">相似词：</label>
				<div class="col-xs-5" id="showword">
				   <c:if test="${frmWord.similarWords!=null}">
				      <pre>${frmWord.similarWords}</pre>
				   </c:if>
					<%-- <p class="form-control-static">${frmWord.similarWords}</p> --%>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">状态：</label>
				<div class="col-xs-5">
					<c:if test="${frmWord.status=='1'}">
						<p class="form-control-static">可用</p>
					</c:if>
					<c:if test="${frmWord.status=='0'}">
						<p class="form-control-static">禁用</p>
					</c:if>					
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">等级：</label>
				<div class="col-xs-5">
					<c:if test="${frmWord.level=='1'}">
						<p class="form-control-static">高</p>
					</c:if>
					<c:if test="${frmWord.level=='2'}">
						<p class="form-control-static">中</p>
					</c:if>
					<c:if test="${frmWord.level=='3'}">
						<p class="form-control-static">低</p>
					</c:if>										
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