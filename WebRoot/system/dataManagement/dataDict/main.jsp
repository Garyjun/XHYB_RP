<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>演示主页</title>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
</head>
<body>
	<form id="queryForm" action="dictNameList.jsp" target="work_main" method="post">
		<div class="by-main-page-body-side" id="byMainPageBodySide">
		    <div class="page-sidebar navbar-collapse collapse">
		        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
		            <i class="fa fa-angle-left"></i>
		        </div>
		        <div id="sideWrap">
		            <div class="by-tool-box">
		
		            </div>
		            <ul class="page-sidebar-menu">
		                <li class="by-item-first"></li>
		                <li class="active open">
		                    <a href="javascript:;">
		                        <i class="fa fa-tasks"></i>
		                        <span class="title">数据字典</span>
		                        <span class="arrow open"></span>
		                    </a>
		                    <ul class="sub-menu">
		                        <li>
		                            <a href="javascript:;" onclick="$('#queryForm').submit();">资源模块</a>
		                        </li>
		                    </ul>
		                </li>
		            </ul>
		        </div>
		    </div>
		</div>
	</form>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="dictNameList.jsp"></iframe>
	</div>
</body>
</html>