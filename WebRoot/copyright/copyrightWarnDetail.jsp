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
<!-- 				<label class="col-xs-5 control-label text-right">合同编号：</label> -->
<!-- 				<div class="col-xs-5"> -->
<%-- 					<p class="form-control-static">${frmWord.language}</p> --%>
<!-- 				</div> -->
<!-- 			</div> -->
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">版权人：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.ownership}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">授权人：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.autherName}</p>
				</div>
			</div>
<!-- 			<div class="form-group"> -->
<!-- 				<label class="col-xs-5 control-label text-right">版权人：</label> -->
<!-- 				<div class="col-xs-5"> -->
<%-- 					<p class="form-control-static">${frmWord.licensArear}</p> --%>
<!-- 				</div> -->
<!-- 			</div>	 -->
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">授权区域：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.licensArear}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">语种：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.language}</p>
				</div>
			</div>	
<!-- 			<div class="form-group"> -->
<!-- 				<label class="col-xs-5 control-label text-right">授权人：</label> -->
<!-- 				<div class="col-xs-5"> -->
<%-- 					<p class="form-control-static">${frmWord.authorizer}</p> --%>
<!-- 				</div> -->
<!-- 			</div>	 -->
<!-- 			<div class="form-group"> -->
<!-- 				<label class="col-xs-5 control-label text-right">授权开始时间：</label> -->
<!-- 				<div class="col-xs-5"> -->
<%-- 					<p class="form-control-static">${frmWord.authStartDate}</p> --%>
<!-- 				</div> -->
<!-- 			</div>	 -->
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">授权结束时间：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.licenEndTime}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">授权范围：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.licensRange}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">contractCode：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.contractCode}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">授权开始时间：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.licenStartTime}</p>
				</div>
			</div>
						<div class="form-group">
				<label class="col-xs-5 control-label text-right">授权结束时间：</label>
				<div class="col-xs-5">
					<p class="form-control-static">${frmWord.licenEndTime}</p>
				</div>
			</div>
			<div class="form-group">
				<div class="col-xs-offset-5">
					<div class="form-wrap">
<%-- 					<form:hidden path="id" /> --%>
	           		<input type="hidden" name="token" value="${token}" />
	           		<c:if test="${main!=1}">
	            	<input class="btn btn-primary" type="button" value="返回" onclick="parent.gotoCopyrightWarning();"/>
	            	</c:if>
	            	<c:if test="${main==1}">
	            	<input class="btn btn-primary" type="button" value="返回" onclick="parent.queryForm();"/>
	            	</c:if>
	            	</div>
	            </div>
			</div>
		</form:form>
		</div>	
	</div>
</body>
</html>