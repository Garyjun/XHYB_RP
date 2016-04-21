<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<%
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    String message = (String) request.getAttribute("javax.servlet.error.message");
    String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
    String uri = (String) request.getAttribute("javax.servlet.error.request_uri");
    Throwable t = (Throwable) request.getAttribute("javax.servlet.error.exception");
    if(t == null) {
        t = (Throwable) request.getAttribute("exception");
    }
    Class<?> exception = (Class<?>) request.getAttribute("javax.servlet.error.exception_type");
    String exceptionName = "";
    if (exception != null) {
        exceptionName = exception.getName();
    }
    if (statusCode == null) {
        statusCode = 0;
    }
    if (statusCode == 500) {
        LOGGER.error(statusCode + "|" + message + "|" + servletName + "|" + uri + "|" + exceptionName + "|" + request.getRemoteAddr(), t);
    }
    else if (statusCode == 404) {
        LOGGER.error(statusCode + "|" + message + "|" + servletName + "|" + uri + "|" + request.getRemoteAddr());
    }

    String queryString = request.getQueryString();
    String url = uri + (queryString == null || queryString.length() == 0 ? "" : "?" + queryString);
    url = url.replaceAll("&amp;", "&").replaceAll("&", "&amp;");

    if(t != null) {
        LOGGER.error("error", t);
    }
    
    Exception ex=(Exception)request.getAttribute("ex");
%>
<html>
    <head>
        <title>页面<%=statusCode%>错误</title>
    </head>
    <body data-spy="scroll" data-target=".subnav" data-offset="50">
	   	<div class="container">
	   	<H3>Exception: <%= ex.getMessage()%></H3>
	   	异常详细信息：<br/>
	    <%
	        if (statusCode == 403) {
	            out.println("该资源您没权限访问.<br/><br/>");
	            out.println("<a href=\"" + url + "\">刷新,看看是否能访问了</a><br/>");
	        }if (statusCode == 404) {
	            out.println("该资源不存在.<br/><br/>");
	            out.println("<a href=\"" + url + "\">刷新,看看是否能访问了</a><br/>");
	        }if (statusCode == 405) {
	            out.println("发生未知错误,请联系管理员.<br/><br/>");
	            out.println("<a href=\"" + url + "\">刷新,看看是否能访问了</a><br/>");
	        }if (statusCode == 500) {
	            out.println("服务器内部错误,请稍候再试!!!.<br/><br/>");
	            out.println("<a href=\"" + url + "\">刷新,看看是否能访问了</a><br/>");
	        }else {
	        	ex.printStackTrace(new java.io.PrintWriter(out));
	        	out.println("\n");
	        	out.println("对不起,您访问的页面出了一点内部小问题,请<a href=\"" + url + "\">刷新一下</a>重新访问,或者先去别的页面转转,过会再来吧~<br/><br/>");
	        }
	    %>
	    <a href="javascript:void(0);" onclick="history.go(-1)">返回刚才页面</a><br/><br/>
	    </div>
    </body>
</html>
<%!
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("DAILY_ALL");
%>