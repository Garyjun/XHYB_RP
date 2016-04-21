<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/appframe/common.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>编辑资源</title>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	
	<script type="text/javascript">
	
	$(function() {
		var src = $('#src').val();
		$("#img1").attr("src",src);
	});
	</script>
</head>
<body>
<div class="form-wrap">
<input type="hidden" id="src" name="src" value="${param.src}"/>
   <div class="carousel-inner" id="showImg" align="center">
   <img src="" alt="" id="img1"/>
   </div>
</div>
</body>
</html>