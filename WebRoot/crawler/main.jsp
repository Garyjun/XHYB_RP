<!doctype html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="viewport" content="width=device-width, user-scalable=no">
	<script type="text/javascript" src="${path}/appframe/plugin/jiuGongGe/js/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/jiuGongGe/css/font-awesome.min.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/jiuGongGe/js/jquery.popmenu.js"></script>
	<title>数据采集</title>
	<style type="text/css">
		html, body {height: 100%;}
		.fa{ font-size: 20px;line-height: 30px;} 
	</style>
	<style type="text/css">
		div#rMenu {
			position:absolute; 
			visibility:hidden; 
			top:0; 
			background-color: #555;
			text-align: left;
			padding: 2px;
			z-index:5555;
		}
		div#rMenu ul li{
			margin: 1px 0;
			padding: 0 5px;
			cursor: pointer;
			list-style: none outside none;
			background-color: #DFDFDF;
		}
	</style>
	
	
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript">
		
		var zTree, rMenu,firstChannelNode;
		
		$(function() {
			rMenu = $("#rMenu");
			$.ajax({
				type : "post",
				async : false,
				url : "${path}/crawler/channelTree.action",
				success : function(data) {
					jsonTree = eval('(' + data + ')')
					$.fn.zTree.init($("#ztree"), setting,jsonTree);
					zTree = $.fn.zTree.getZTreeObj("ztree")
					//zTree.expandAll(true);
					// 右侧iframe 默认显示 第一个站点下的第一个频道爬取信息
				}	
			});
		});
	
		var setting = {
				view : {
					selectedMulti : false
				},
				edit : {
					enable : false,
					drag: { prev: true, next: true,inner: false},
					showRemoveBtn : false,
					showRenameBtn : false
				},
				data : {
					simpleData : {
						enable : true
					}
				},
				callback : {
					onClick: onClick,
					onRightClick: OnRightClick
				}
	
		};
		
		function onClick(event, treeId, treeNode) {
			if(treeNode.level == 0) {
				event.stopPropagation();
			} else if(treeNode.level > 0) {
				$('#work_main')[0].contentWindow.query(treeNode.id);
			} 
		}	
		var currNode;
		function OnRightClick(event, treeId, treeNode) {
			currNode = treeNode;
			if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
				zTree.cancelSelectedNode();
				showRMenu("root",treeId, event.clientX, event.clientY);
			} else if (treeNode && !treeNode.noR) {
				zTree.selectNode(treeNode);
				showRMenu("node", treeId,event.clientX, event.clientY);
			}
		}
		function showRMenu(type,treeId, x, y) {
			$("#rMenu ul").show();
			if (type=="root") {
				$("#start").hide();
				$("#stop").hide();
				$("#add_channel").show();
				$("#refresh").hide();
				$("#prop_config").hide();
			} else {
				$("#start").show();
				$("#stop").show();
				$("#add_channel").hide();
				$("#refresh").show();
				$("#prop_config").show();
			}
			rMenu.css({"top":y+"px", "left":x+"px", "visibility":"visible"});

			$("body").bind("mousedown", onBodyMouseDown);
		}
		function hideRMenu() {
			if (rMenu) rMenu.css({"visibility": "hidden"});
			$("body").unbind("mousedown", onBodyMouseDown);
		}
		function onBodyMouseDown(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
				rMenu.css({"visibility" : "hidden"});
			}
		}

		function start() {
			hideRMenu();
			$.ajax({
				type : "post",
				async : false,
				url : "${path}/crawler/start.action?channelId="+currNode.id,
				success : function(data) {
					//result = eval('(' + data + ')');
				}	
			});
		}
		function stop() {
			hideRMenu();
			var nodes = zTree.getSelectedNodes();
			if (nodes && nodes.length>0) {
				if (nodes[0].children && nodes[0].children.length > 0) {
					var msg = "要删除的节点是父节点，如果删除将连同子节点一起删掉。\n\n请确认！";
					if (confirm(msg)==true){
						zTree.removeNode(nodes[0]);
					}
				} else {
					zTree.removeNode(nodes[0]);
				}
			}
		}
		function addChannel(checked) {
			var nodes = zTree.getSelectedNodes();
			if (nodes && nodes.length>0) {
				zTree.checkNode(nodes[0], checked, true);
			}
			hideRMenu();
		}
		function refresh() {
			hideRMenu();
			$.fn.zTree.init($("#ztree"), setting, zNodes);
		}

		
		
	</script>
</head>
<body style="overflow-x: hidden">
	<div class="by-main-page-body-side" id="byMainPageBodySide">
		<div class="by-tool-box">
		    <div class="page-sidebar" style="height:600px;">
		        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
		            <i class="fa fa-angle-left"></i>
		        </div>
		        <div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">频道导航</label></div>
				    <div class="by-form-val"></div>
				</div>
				<div class="zztreeBackground left">
					<ul id="ztree" class="ztree"></ul>
				</div>
		    </div>
	    </div>
	</div>
	
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="${path}/crawler/list.jsp"></iframe>
	</div>
	
	<div id="rMenu">
		<ul>
			<li id="start" onclick="start();">启动</li>
			<li id="stop" onclick="stop();">停止</li>
			<li id="refresh" onclick="refresh();">刷新</li>
			<li id="add_channel" onclick="addChannel(true);">添加频道</li>
			<li id="prop_config" onclick="propConfig(true);">属性配置</li>
		</ul>
	</div>
</body>
</html>