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
	<script type="text/javascript">
	$(document).ready(function(){
		
		//第一个加底色（其实是第二个）
		$("#sideMenu li:eq(1)").addClass("open");
		
		var li = $("#sideMenu li");
		li.click(function(event){
			li.each(function(){
				$(this).removeClass("open");
			});
			$(this).addClass("open");
		});
	});

	</script>
</head>
<body>
	<form id="queryForm" action="dictNameList.jsp" target="work_main" method="post">
		<div class="by-main-page-body-side" id="byMainPageBodySide">
		    <div class="page-sidebar">
		        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
		            <i class="fa fa-angle-left"></i>
		        </div>
				<jsp:include page="/appframe/subMenu.jsp?pMenuId=${param.pMenuId}" />
		    </div>
		</div>
	</form>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="${path}/system/dataManagement/dataDict/dictNameList.jsp"></iframe>
	</div>
</body>
</html>