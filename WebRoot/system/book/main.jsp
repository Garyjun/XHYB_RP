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
	$(document.ready).ready(function(){
		$.get("listBook.action", function(data){
			var books = eval("("+data+")");
			for(var i=0;i<books.domains.length;i++){
				var book = books.domains[i];
				var li = $("<li></li>");
				if(i==0)
					li.addClass("active");
				var href = $("<a></a>");
				href.text(book.label);
				var version = book.objectId.substring(book.objectId.indexOf("#")+1,book.objectId.length);
				href.attr({
					"href":"bookList.jsp?version="+version,
					"target":"work_main"
				});
				href.appendTo(li);
				li.appendTo($("#book"));
			}
			$("#book li").click(function(){
				$("#book li").each(function(){
					$(this).removeClass("active");
				});
				$(this).addClass("active");
			});
			if(books.domains[0]){
				var firstBookId = books.domains[0].objectId;
				var version = firstBookId.substring(firstBookId.indexOf("#")+1,firstBookId.length);
				$("#work_main").attr("src","bookList.jsp?version="+version);
			}
		});
	});
	
	function addBook(evt){
	    $.openWindow("system/book/addBook.jsp",'添加教材',400,200);
	    evt.stopPropagation();
	    return false;
	}
	</script>
</head>
<body>
	<form id="queryForm" action="dictNameList.jsp" target="work_main" method="post">
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
		                <li class="active open">
		                    <a href="javascript:;">
		                        <i class="fa fa-tasks"></i>
		                        <span class="title">教材版本</span>
		                        <span class="glyphicon glyphicon-plus" 
		                        	onclick="addBook(event)" style="flow:right;"></span>
		                        <span class="arrow open"></span>
		                    </a>
							<ul class="sub-menu" id="book">
		                    </ul>
		                </li>
		                <li>
		                	<a href="stopWordList.jsp" target="work_main">
		                	<i class="fa fa-tasks"></i>
		                	<span class="title">停用词管理</span>
		                	</a>
		                </li>
		            </ul>
		        </div>
		    </div>
		</div>
	</form>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0"></iframe>
	</div>
</body>
</html>