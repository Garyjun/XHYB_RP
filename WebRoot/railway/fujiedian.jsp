<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>演示主页</title>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.exhide-3.5.min.js"></script>
	<script type="text/javascript" src="${path}/system/book/bookUtil.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
		.by-main-page-body-side {
		  background-color: #E5E5E5;
		}
	</style>
	<script type="text/javascript">
	var fieldName = '${param.fieldName}';
	var isMain = '${param.isMain}';
	var type = '${param.typeId}';
	
	var zTree;
    var demoIframe;
    var zNodes;
    var rootCode;
    
    function addHoverDom(treeId, treeNode) {
		var sObj = $("#" + treeNode.tId + "_span");
		var zTree = $.fn.zTree.getZTreeObj("tree");
	} 
     function removeHoverDom(treeId, treeNode) {
	        $("#removeBtn_"+treeNode.tId).unbind().remove();
	        $("#addBtn_"+treeNode.tId).unbind().remove();
	        $("#editBtn_"+treeNode.tId).unbind().remove();
	        $("#detailBtn_" + treeNode.tId).unbind().remove();
	        $("#changeVersionBtn_" + treeNode.tId).unbind().remove();
	        $("#importBtn_" + treeNode.tId).unbind().remove();
	        $("#exportVersionBtn_" + treeNode.tId).unbind().remove();
		};
    
    
    var setting = {
    		async: {
				enable: true,
				url: getUrl
			},
			view : {
				/* addHoverDom : addHoverDom,
				removeHoverDom : removeHoverDom, */
				dblClickExpand : false,
				showLine : true,
				selectedMulti : false
			},
			check: {
				enable: true,
				chkStyle: "radio",
				chkboxType: { "Y": "", "N": "" },
				radioType: "all"
			},
			data : {
				simpleData : {
					enable : true,
					idKey : "nodeId",
					pIdKey : "pid",
					rootPId : "0"
				}
			},
			callback: {
				onClick: zTreeOnClick	
			}
		};
    
	$(document).ready(function() {
		var t = $("#tree");
		$.get("${path}/system/FLTX/listContent.action?path=0&type=" +type,
				function(data) {
					var zNodes = eval("("+data+")");
					$.fn.zTree.init(t, setting, zNodes);
			});
	});
	function loadReady() {
		var bodyH = demoIframe.contents().find("body").get(0).scrollHeight, htmlH = demoIframe
				.contents().find("html").get(0).scrollHeight, maxH = Math
				.max(bodyH, htmlH), minH = Math.min(bodyH, htmlH), h = demoIframe
				.height() >= maxH ? minH : maxH;
		if (h < 400)
			h = 400;
		demoIframe.height(h);
	}
	
	function getUrl(treeId, treeNode) {
		var param = "";
		if(treeNode == undefined){
			param = '0';
		}else{
			param = treeNode.id;
		}
		return "${path}/system/FLTX/listContent.action?path=" + param+"&type=" +type;
	}
	
	function zTreeOnClick(event, treeId, treeNode) {
		var xpath = treeNode.xpath;
		var name = treeNode.name;
		var url =treeNode.uon;
// 		var myArgs = new Array("refresh",xpath,name,url);
// 		window.returnValue = myArgs;
	};
	function queding(){
		var paths = "";
		var nodeNames = "";
		var ids = "";
		var zTree = $.fn.zTree.getZTreeObj("tree");
		var nodes = zTree.getCheckedNodes(true);
		var checkCount = nodes.length;
		//paths = nodes[0].xpath+","+nodes[0].code;
		if(checkCount == 0 || checkCount == '0'){
			top.layer.msg("未选择分类", {icon: 3}); 
			//top.layer.alert("未选择分类");
		}else{
			paths = nodes[0].code;
			nodeNames = nodes[0].name;
			ids = nodes[0].id;
			top.layer.msg("您选择了" + checkCount + "个项【" + nodeNames +"】", {icon: 6}); 
			//top.layer.alert("您选择了" + checkCount + "个项【" + nodeNames +"】");
			var parentWin= "";
			if(isMain == '1'){
				parentWin=  top.index_frame;
			}else{
				parentWin = parent;
			}
			parentWin.$('#xpath').val(paths);
			parentWin.$('#sortname').val(nodeNames);
			parentWin.$('#categoryId').val(ids);
			parent.layer.closeAll();
		}
		
	}
	
	</script>
</head>
<body>
	<div style="float: left;width: 350px;">
		<ul id="tree" class="ztree form-wrap"></ul>
	</div>
	<input type="button" value="确定" onclick="queding();"/>
</body>
</html>