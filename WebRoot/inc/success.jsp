<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../inc/header.jsp"%>
<html>
    <head>
        <title>操作成功</title>
        <meta http-equiv="refresh" content="3;url=<c:url value='/user'/>"/>
    </head>
    <body data-spy="scroll" data-target=".subnav" data-offset="50">
      	<div class="container">
		    =============<br/>
		        操作成功,3秒后返回！<br/>
		     <a href="<c:url value='/user'/>">返回列表-ok?</a><br/>
		    =============<br/>
	    </div>
    </body>
</html>
