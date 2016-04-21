<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>	
<title>碎片</title>
<script type="text/javascript">
		var treeSetting = {
			async: {
				enable: true,
				url: getUrl
			},				
	        view: {
	            //addHoverDom: addHoverDom,
	           // removeHoverDom: removeHoverDom,
				expandSpeed: ""
	        },
			data: {
				simpleData: {
					enable: true,
					idKey: "id",
					pIdKey: "pid"
				}
			},
			callback: {
				beforeExpand: beforeExpand,
				onAsyncSuccess: onAsyncSuccess,
				onAsyncError: onAsyncError,
				//onClick: zTreeOnClick
			}					
		};
		$(document.ready).ready(function(){
			$.ajax({
				url:"${path}/system/FLTX/listContent.action?type=6&path=0",
				success:function(data){
					var content = jQuery.parseJSON(data);
					var root = {"id":0,"pid":-1,"name":"时间分类","xpath":"0", open:true};
					content.unshift(root);
					var ztree = $.fn.zTree.init($("#tree"), treeSetting,content);
					$('#work_main1').attr('src','${path}/fragment/fragmentAsImgList.jsp');
				}
			});	
		});
		function getUrl(treeId, treeNode) {
			var param = treeNode.id;
			return "${path}/system/FLTX/listContent.action?path="+ param + "&type=6";
		}
		function beforeExpand(treeId, treeNode) {
			if (!treeNode.isAjaxing) {
				treeNode.times = 1;
				ajaxGetNodes(treeNode, "refresh");
				return true;
			} else {
				//alert("zTree 正在下载数据中，请稍后展开节点。。。");
				notice("提示信息","zTree 正在下载数据中，请稍后展开节点。。。","3");
				return false;
			}
		}
		function ajaxGetNodes(treeNode, reloadType) {
			var zTree = $.fn.zTree.getZTreeObj("tree");
			if (reloadType == "refresh") {
				treeNode.icon = "../../../css/zTreeStyle/img/loading.gif";
				zTree.updateNode(treeNode);
			}
			zTree.reAsyncChildNodes(treeNode, reloadType, true);
		}
		function onAsyncSuccess(event, treeId, treeNode, msg) {
			if (!msg || msg.length == 0) {
				return;
			}
			var zTree = $.fn.zTree.getZTreeObj("tree");
			if(treeNode != null){
				treeNode.icon = "";
				zTree.updateNode(treeNode);
				zTree.selectNode(treeNode);
			}
		}
		function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
			var zTree = $.fn.zTree.getZTreeObj("tree");
			//alert("异步获取数据出现异常。");
			notice("提示信息","异步获取数据出现异常。","3");
			treeNode.icon = "";
			zTree.updateNode(treeNode);
		}
		//Ztree树的点击事件
		function zTreeOnClick(event, treeId, treeNode) {
		    alert(treeNode.tId + ", " + treeNode.name);
		    var url = '${path}/fragment/fragmentAsImgList.jsp?treeId'+"";
		    //$('#work_main1').attr('src',url);
		};
</script>
</head>
<body class="by-frame-body" style="height: 100%">
<div class="by-main-page-body-side" id="byMainPageBodySide">
	<div class="page-sidebar" style="overflow:auto;">
		<div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
			<i class="fa fa-angle-left"></i>
		</div>
		<div id="sideWrap">
        	<ul id="tree" class="ztree form-wrap" ></ul>
		</div>
	</div>
</div>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
    	<iframe id="work_main1" name="work_main1" width="100%" height="100%" frameborder="0" src=""></iframe>
	</div>
</body>
</html>