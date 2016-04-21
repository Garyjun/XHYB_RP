<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>文档分类</title>
</head>
<body>
	<form action="">
		<table width="30%" style="margin-top: 20px;" align="center">
			<tr height="30px;">
				<td>所属分类</td>
				<td>条数</td>
			</tr>
			<c:forEach items="${wfenleilist}" var="wfenleilist">
			<tr height="30px;">
				<td>${wfenleilist.wname}</td>
				<td>${wfenleilist.size}</td>
				</tr>
			</c:forEach>
		</table>
		<br>
	<center>
	<p>韶山9型电力机车轻大修规程（试行）&nbsp;&nbsp;分类为：
	<c:forEach items="${wfenleilist2}" var="wfenleilist2">
				${wfenleilist2.wname}&nbsp;&nbsp;
			</c:forEach><br></p></center>
	</form>
</body>
</html>