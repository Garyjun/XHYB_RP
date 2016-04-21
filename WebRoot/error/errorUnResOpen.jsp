<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ include file="../inc/header.jsp"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>error page</title>
	<style type="text/css">
*{padding:0px; margin:0px;}
#main{width:900px;height:500px;margin:auto;}
.con{width:271px;height:140px;margin: auto;margin-top:110px}
#dv{width:271px;height:100px;}
</style>
</head>
<body>
<div id="main">
 <div class="con">
	<div id="dv1">
	 <img src="<%=appPath%>/appframe/main/images/resNotExist.png" />
	</div>
	<div style="margin-top:28px;" align="center">
	   <button  class="btn btn-primary" style="width:110px;" onclick="javascript:$.closeFromInner();">关闭</button>
	</div>
 </div>
 </div>
</body>
</html>