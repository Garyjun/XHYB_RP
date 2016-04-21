<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
		 <div id="fakeFrame" class="container-fluid" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="" id="form" modelAttribute="knowledge" method="post" class="form-horizontal" role="form">
		<div  class="form-group" style="margin-top: 30px;">
			<table width="90%" border="1" cellpadding="5" cellspacing="0" align="center">
				<tr>
					<td style="height: 26px;width: 33%" >首选词中文：</td>
					<td>${knowledge.prefLabelZH}</td>
				</tr>
				<tr>
					<td style="height: 26px;" >首选词英文：</td>
					<td>${knowledge.prefLabelEN}</td>
				</tr>
				<tr>
					<td style="height: 26px;" >备选词中文：</td>
					<td>${knowledge.prefLabelPY}</td>
				</tr>
			
			</table>
		</div>
		<div style="width: 10%;text-align: center;">
			<label class="con_L_searchA">上位词</label>
		</div>
		<div class="form-group">
			<table width="90%" border="1" cellpadding="5" cellspacing="0" align="center">
				<tr align="center">
					<td bgcolor="#DEDEDE" height="26px" width="30%">词条名</td>
					<td align="center" width="60%" bgcolor="#DEDEDE" height="26px">词条URL</td>
				</tr>
				<c:forEach items="${broadersList}" var="broadersList">
				<c:if test="broadersList==null">
				<tr align="center">
			    	<td height="22px">没有数据</td>
			    	<td height="22px">${broadersList.url }</td>
			    </tr>
				</c:if>
			    <tr align="center">
			    	<td height="22px">${broadersList.name }</td>
			    	<td height="22px">${broadersList.url }</td>
			    </tr>
			</c:forEach>
			</table>
		</div>
		<div style="width: 10%;text-align: center;">
			<label class="con_L_searchA">下位词</label>
		</div>
		<div class="form-group">
			<table width="90%" border="1" cellpadding="5" cellspacing="0" align="center">
				<tr align="center">
					<td bgcolor="#DEDEDE" height="26px" width="30%">词条名</td>
					<td align="center" width="60%" bgcolor="#DEDEDE" height="26px">词条URL</td>
				</tr>
				<c:forEach items="${narrowersList}" var="narrowersList">
			    <tr align="center">
			    	<td height="22px">${narrowersList.name }</td>
			    	<td height="22px">${narrowersList.url }</td>
			    </tr>
			</c:forEach>
			</table>
		</div>
		<div style="width: 10%;text-align: center;">
			<label class="con_L_searchA">相关词</label>
		</div>
		<div class="form-group">
			<table width="90%" border="1" cellpadding="5" cellspacing="0" align="center">
				<tr align="center">
					<td bgcolor="#DEDEDE" height="26px" width="30%">词条名</td>
					<td align="center" width="60%" bgcolor="#DEDEDE" height="26px">词条URL</td>
				</tr>
				<c:forEach items="${relatedsList}" var="relatedsList">
			    <tr align="center">
			    	<td height="22px">${relatedsList.name }</td>
			    	<td height="22px">${relatedsList.url }</td>
			    </tr>
			</c:forEach>
			</table>
		</div>
		</form:form>
		</div>	
	</div>
</body>
</html>