<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>演示主页</title>
<style type="text/css">
html, body {
	height: 100%;
}
</style>
<script type="text/javascript">
	$(document).ready(function() {

						//异步请求数据
						$.ajax({type : "post",
								url : '${path}/system/sysMonitor/queryServerLinkInfo.action',//提供数据的Servlet
								success : function(data) {
									createServerLink(data);
								},
								error : function() {
									
								}
							});

						function createServerLink(data) {
							
							if(data == "" || data == null || data == undefined ){
								$.alert("请在数据字典中配置服务器信息，并在服务器指定位置放置执行脚本！");
								return;
							}
							
							var msg = data.split("|");
							var ul = $('#sideMenu');
							for (var i = 0; i < msg.length; i++) {
								var jstr = msg[i].split("+");
								//判断，将第一个服务器置为选中状态，加class="open"
								if(i==0){
									var li = $("<li class='active open' id='li"+i+"' onclick='changeLi("+i+")'>"
											+ "<a href='${path}/system/sysMonitor/monitorList.jsp?linkInfo="
											+ jstr[1]
											+ "' target='work_main' ><span class='title'>"
											+ jstr[0] + "</span></a>" + "</li>");
									li.appendTo(ul);
									linkList(jstr[1]);//初始化的时候传值给主页面，不然初始化的时候主页面没有值，只有点击才刷新主页面
								}else{
									var li = $("<li id='li"+i+"' onclick='changeLi("+i+")'>"
											+ "<a href='${path}/system/sysMonitor/monitorList.jsp?linkInfo="
											+ jstr[1]
											+ "' target='work_main' ><span class='title'>"
											+ jstr[0] + "</span></a>" + "</li>");
									li.appendTo(ul);
								}
								
							}
						}

					});


	function changeLi(i){
		$("li[id^='li']").removeClass("open");//将所有li中所有id以li开头的样式去掉
	    $("#li"+i).addClass("open");//添加点选的li的样式
	}
	
	function linkList(data) {
		$('#work_main').attr('src',
				"${path}/system/sysMonitor/monitorList.jsp?linkInfo=" + data);
	}
</script>
</head>
<body>
	<form id="queryForm" action="dictNameList.jsp" target="work_main"
		method="post">
		<div class="by-main-page-body-side" id="byMainPageBodySide">
			<div class="page-sidebar">
				<div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn"
					onclick="toggleSideBar(resizeFrameDt)">
					<i class="fa fa-angle-left"></i>
				</div>
				<ul class="page-sidebar-menu" id="sideMenu">
					<li class="by-item-first"></li>

				</ul>
			</div>
		</div>
	</form>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
		<iframe id="work_main" name="work_main" width="100%" height="100%"
			frameborder="0" src=""></iframe>
	</div>
</body>
</html>