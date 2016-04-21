<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>文章资源详细</title>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
	function goBackTask(){
		parent.$('#work_main1').attr('src','${path}/article/articleList.jsp');
	}
	</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap ">
 <div class="panel panel-default">
    <div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li class="active">资源管理
					</li>
					<li class="active">文章管理</li>
					<li class="active">文章详细</li>
				</ol>
	</div>
 </div>	
<form action="#" id="coreData" class="form-horizontal">
	<app:ArticleMetadataDetailTag   object="${scoEntity}" publishType="${publishType}"/>
	<input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
	
	<div class="form-actions" align="center">
        <button type="button" class="btn btn-lg blue" onclick="goBackTask();">返回</button>
	</div>
 </form>
</div>
</body>
</html>