<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="/appframe/common.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<style type="text/css">
.tab_list{}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title>主页</title>
<!-- pdf -->
<script type="text/javascript" src="../appframe/plugin/flexPaper/flexpaper_flash.js"></script>
<!-- JQuery -->
<script type="text/javascript" src="../appframe/plugin/layout/lib/js/jquery-latest.js"></script>
<!-- Jquery.ui -->
<link rel="stylesheet" type="text/css" href="../appframe/plugin/layout/lib/css/themes/base/jquery.ui.all.css" />
<script type="text/javascript" src="../appframe/plugin/layout/lib/js/jquery-ui-latest.js"></script>
<!-- Layout -->
<script type="text/javascript" src="../appframe/plugin/layout/lib/js/jquery.layout-latest.js"></script>
<link type="text/css" rel="stylesheet" href="../appframe/plugin/layout/lib/css/layout-default-latest.css" />
<!-- Ztree -->
<link rel="stylesheet" href="../appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css" />
<script type="text/javascript" src="../appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<!-- Datatables -->
<link rel="stylesheet" type="text/css" href="../appframe/plugin/DataTables/media/css/jquery.dataTables.css" />
<script type="text/javascript" src="../appframe/plugin/DataTables/media/js/jquery.dataTables.js"></script>

<!-- Annotator  -->
<script type="text/javascript" src="../appframe/plugin/annotator/annotator-full.min.js"></script>
<link rel="stylesheet" href="../appframe/plugin/annotator/annotator.min.css">

<link rel="stylesheet" type="text/css" href="css/style.css"/>

<script type="text/javascript" src="js/datatable.js"></script>
<script type="text/javascript" src="js/layout.js"></script>
<script type="text/javascript" src="js/ztree.js"></script>
<script type="text/javascript" src="js/tabsUtils.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$('body').layout(layoutSetting);
		$.fn.zTree.init($("#treePane"), ztreeSetting);
		//var searchParam = {resType:"5",channel:"2"};
		var searchParam = {resType:"2"};
		initTable(searchParam);
		$("#center_area").tabs();
		tabsclose();
		//$('#tabsdiv').annotator().annotator('addPlugin', 'Store',annotatorSetting);
	});
	function test1(){
		var searchParam = {resType:"5",channel:"2",conditions:{}};
		initTable(searchParam);
	}
	function test2(){
		var searchParam = {resType:"3"};
		initTable(searchParam);
	}
	function test3(){
		var searchParam = {resType:"2"};
		initTable(searchParam);
	}
	function test4(){
		var table = $("#table_list").DataTable();
		if(table){
			table.destory(true);
		}
		//$("#table_list").empty();
	}
</script>
</head>
<body>
	<!-- 顶部  -->
	<div class="ui-layout-north">
		<img alt="logo" id="logo" src="${pageContext.request.contextPath}/fileDir/sysUpLoadFile/logo/pubLogo3.png"/>
 		<div style="float: right;">
			<button type="button" onclick="test1();">网页爬虫</button>
			<button type="button" onclick="test2();">大事辑览</button>
			<button type="button" onclick="test3();">文章</button>
			<button type="button" onclick="test4();">table_list</button>
		</div>
		
<!-- 		<select id="selectmenu" style="color: blue; font-weight: normal; float: right;"> -->
<!-- 			<option selected="selected">设置</option> -->
<!-- 			<option>重新登录</option> -->
<!-- 			<option>修改密码</option> -->
<!-- 		</select> -->
	</div>

	<!-- 导航区域  -->
	<div class="ui-layout-west">
		<div class="ui-layout-center" style="height:400px;overflow:auto;">
			<div class="zTreeDemoBackground right">
				<ul id="treePane" class="ztree"></ul>
			</div>
		</div>
		<div class="ui-layout-south">
			<table id="table_list" class="display"  width="100%"></table>
			<!-- <table id="journal_list" class="display" style="diapaly:none" width="100%"></table>
			<table id="article_list" class="display" style="diapaly:none" width="100%"></table>
			<table id="event_list" class="display" style="diapaly:none" width="100%"></table>
			<table id="crawl_list" class="display" style="diapaly:none" width="100%"></table> -->
		</div>
	</div>

	<!-- 内容区域-->
	<div class="ui-layout-center tab_wrap" id="center_area">
	<!-- 删除所有工具栏  -->
		<ul class="tab_list" id="tabsul">
			<!-- jQuery ui tabs 选项卡位置-->
		</ul>
		<!-- add wrapper that layout will auto-size to 'fill space' -->
		<div class="ui-layout-content ui-widget-content" id="tabsdiv" style="padding: 0px;">
			
		</div>
	</div>
	
	<!-- 操作区域 -->
	<div class="ui-layout-east">
		<div class="ui-layout-center"></div>
	</div>

	<!-- 底部 -->
	<div class="ui-layout-south">
		欢迎使用学研平台！
	</div>

</body>
</html>