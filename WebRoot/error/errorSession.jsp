<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>error page</title>
	<script type="text/javascript">
		$(function(){
			top.location.href="${path}/index.jsp";
		})
	</script>
</head>
<body style="margin: 0;padding: 0;background-color: #f5f5f5;">
	
</body>
</html>