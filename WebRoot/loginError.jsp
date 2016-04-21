<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<% String appPath = request.getContextPath();%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>用户登录-学研平台</title>
    <link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/font-awesome.css"/>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/style-metronic.css"/>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/login.css"/>
	<!--[if lt IE 9]>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/html5shiv.min.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/respond.min.js"></script>
	<![endif]-->
  </head>
  <style type="text/css">
*{padding:0px; margin:0px;}
#main{width:900px;height:500px;margin:auto;}
.con{width:249px;height:194px;margin: auto;margin-top:139px;}
#dv1{width:249px;height:154px;margin-top:10px;}
.btn {
  display: inline-block;
  padding: 6px 12px;
  margin-bottom: 0;
  font-size: 14px;
  font-weight: normal;
  line-height: 1.42857143;
  text-align: center;
  white-space: nowrap;
  vertical-align: middle;
  cursor: pointer;
  -webkit-user-select: none;
     -moz-user-select: none;
      -ms-user-select: none;
          user-select: none;
  background-image: none;
  border: 1px solid transparent;
  border-radius: 4px;
}
.btn-primary {
  color: #ffffff;
  background-color: #428bca;
  border-color: #357ebd;
}

</style>
  <script type="text/javascript">
  function relogin(){
	  location.href = "<%=appPath%>/system/login.jsp";
  }
  </script>
  <body>
 <div id="main">
 <div class="con">
	<div id="dv1">
	 <img src="<%=appPath%>/appframe/main/images/logerFail.png" />
	</div>
	<div style="margin-top:28px;" align="center">
	   <button  class="btn btn-primary" onclick="relogin()">重新登录</button>
	</div>
 </div>
 </div>
 
</body>
</html>