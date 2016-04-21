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



<link rel="stylesheet" type="text/css" href="css/style.css"/>

<script type="text/javascript" src="js/datatable.js"></script>
<script type="text/javascript" src="js/layout.js"></script>
<script type="text/javascript" src="js/ztree.js"></script>
<script type="text/javascript" src="js/tabsUtils.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$('body').layout(layoutSetting);
		$.fn.zTree.init($("#treePane"), ztreeSetting);

		/* var searchParam = {resType:"0",columns:[//期刊
			{key:"magazine",value:"期刊名"},
			{key:"localSerialNumber",value:"国内刊号"},
			{key:"overseasSerialNumber",value:"国际刊号"},
			{key:"magazineYear",value:"期刊年份"},
			{key:"numOfYear",value:"当前期数"},
			{key:"magazineNum",value:"总期数"}]
		}; */
		var searchParam = {resType:"1",columns:[//文章
            {key:"title",value:"文章标题"},
            {key:"wzJournalClass",value:"来源"},
            {key:"keywords",value:"关键词"},
            {key:"source",value:"所属期次分类"},
            {key:"articleOfJul",value:"所属期次期号"}]
		};
		/* var searchParam = {resType:"2",columns:[//大事辑览
			{key:"magazineTime",value:"日期"},
			{key:"content",value:"条目内容"},
			{key:"magazineNum",value:"所属期刊"}]
		}; */
		/* var searchParam = {resType:"4",columns:[//网页爬虫
			{key:"sn",value:"编号"},
			{key:"title",value:"标题"},
			{key:"source",value:"来源"},
			{key:"postime",value:"发布时间"},
			{key:"status",value:"状态"},
			{key:"createTime",value:"采集时间"}]
		}; */

		initTable(searchParam);
		$("#center_area").tabs();
		tabsclose();
		onkeyclosetabs();
	});
	
</script>
</head>
<body>
	<!-- 顶部  -->
	<div class="ui-layout-north">
		<img alt="logo" id="logo" src="${pageContext.request.contextPath}/fileDir/sysUpLoadFile/logo/pubLogo3.png"/>
<!-- 		<div style="float: right;"> -->
 			<!-- <button type="button" onclick="openTabs('1','5','3','4');">测试</button> -->
<!-- 			<button type="button" onclick="initTable('article');">文章</button> -->
<!-- 			<button type="button" onclick="initTable('event');">大事记</button> -->
<!-- 		</div> -->
		
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
			<!-- <table id="table_list" class="display" ></table> -->
			<table id="journal_list" class="display" style="diapaly:none" width="100%"></table>
			<table id="article_list" class="display" style="diapaly:none" width="100%"></table>
			<table id="event_list" class="display" style="diapaly:none" width="100%"></table>
			<table id="crawl_list" class="display" style="diapaly:none" width="100%"></table>
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