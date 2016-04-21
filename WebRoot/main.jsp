<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>主页</title>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
	function gotoCopyrightWarning(){
		var url = "${path}/copyright/warningList.jsp?copyrightWaring=1";
		$('#work_main').attr('src',url);
	}
	
	function gotoTaskListByStatus(statusType){
		var url = '${path}/TaskAction/toList.action';
		$('#work_main').attr('src',url);
	}
	
	function gotoLogQueryByself(workLog){
		var url = '${path}/system/workLog/list.action?workLog='+workLog;
		$('#work_main').attr('src',url);
	}
	function gotoFileDown(){
		var url = '${path}/system/ftpHttpDetail/ftpHttpList.jsp?flag=1';
		$('#work_main').attr('src',url);
	}
	$(function(){
		var li = $("#sideWrap li");
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
	<form id="queryForm" action="test/list.jsp" target="work_main" method="post">
		<div class="by-main-page-body-side" id="byMainPageBodySide">
		    <div class="page-sidebar">
		        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
		            <i class="fa fa-angle-left"></i>
		        </div>
		        <div id="sideWrap">
		            <div class="by-tool-box">
		
		            </div>
		            <ul class="page-sidebar-menu">
		                <li class="by-item-first"></li>
		                <li class="open">
		                    <a href="javascript:;" onclick="gotoTaskListByStatus('1');">
		                        <i class="fa fa-tasks"></i>
		                        <span class="title">待办任务</span>
		                        <span class="arrow"></span>
		                    </a>
<!-- 		                    <ul class="sub-menu"> -->
<!-- 		                        <li> -->
<!-- 		                            <a href="javascript:;" onclick="gotoTaskListByStatus('1');">待审核</a> -->
<!-- 		                        </li> -->
<!-- 		                    </ul> -->
		                </li>
		                
		                 <li>
		                    <a href="javascript:;" onclick="gotoFileDown();">
		                        <i class="fa fa-download"></i>
		                        <span class="title" id="workLog">下载管理</span>
		                        <span class="arrow"></span>
		                    </a>
		                </li>
		                
		                <li >
		                <sec:authorize url='/copyrightwarn/copyrightwarning.action'>
		                    <a href="javascript:;" id ="title1" onclick="gotoCopyrightWarning();">
		                        <i class="fa fa-upload"></i>
		                        <span class="title">版权预警</span>
		                        <span class="arrow"></span>
		                    </a>
		                  </sec:authorize>
		                </li>
		                <li>
		                    <a href="javascript:;" onclick="gotoLogQueryByself('1');">
		                        <i class="fa fa-wrench"></i>
		                        <span class="title" id="workLog">工作日志</span>
		                        <span class="arrow"></span>
		                    </a>
		                   
		                </li>
		            </ul>
		        </div>
		    </div>
		</div>
	</form>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="${path}/TaskAction/toList.action"></iframe>
	</div>
</body>
</html>