<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript">
	var fieldName = '${param.fieldName}';
	var isMain = '${param.isMain}';;
	var type = '${param.typeId}';
	var setting = {
			async: {
				enable: true,
				url: getUrl
			},
			check: {
				enable: true,
				chkStyle: "radio",
				chkboxType: { "Y": "", "N": "" },
				radioType: "all"
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			view: {
				expandSpeed: "",
				dblClickExpand : false,
				showLine : true,
				selectedMulti : false		//不允许同时选中多个
			},
			callback: {
				beforeExpand: beforeExpand,
				onAsyncSuccess: onAsyncSuccess,
				onAsyncError: onAsyncError,
				onClick: zTreeOnClick
			}
		};
	
		function getUrl(treeId, treeNode) {
			var param = "";
			if(treeNode == undefined){
				param = '0';
			}else{
				param = treeNode.id;
			}
			return "${path}/system/FLTX/listContent.action?path=" + param+"&type=" +type;
		}
		
		function beforeExpand(treeId, treeNode) {
			if (!treeNode.isAjaxing) {
				treeNode.times = 1;
				ajaxGetNodes(treeNode, "refresh");
				return true;
			} else {
				$.alert("zTree 正在下载数据中，请稍后展开节点。。。");
				return false;
			}
		}
		function onAsyncSuccess(event, treeId, treeNode, msg) {
			if (!msg || msg.length == 0) {
				return;
			}
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			if(treeNode != null){
				treeNode.icon = "";
				zTree.updateNode(treeNode);
				zTree.selectNode(treeNode);
			}
		}
		function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			$.alert("异步获取数据出现异常。");
			treeNode.icon = "";
			zTree.updateNode(treeNode);
		}
		function ajaxGetNodes(treeNode, reloadType) {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			if (reloadType == "refresh") {
				treeNode.icon = "../../../css/zTreeStyle/img/loading.gif";
				zTree.updateNode(treeNode);
			}
			zTree.reAsyncChildNodes(treeNode, reloadType, true);
		}
		
		//单击节点
		function zTreeOnClick(event, treeId, treeNode) {
			$("#nodeLabel").val(treeNode.name);
			$("#code").val(treeNode.code);
			$("#nodeNumber").val(treeNode.nodeId);
		}
		function returnCheckedNodeIds() {
			var paths = "";
			var nodeNames = "";
			var ids = "";
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			var nodes = zTree.getCheckedNodes(true);
			var checkCount = nodes.length;
			//paths = nodes[0].xpath+","+nodes[0].code;
			paths = nodes[0].code;
			nodeNames = nodes[0].name;
			ids = nodes[0].id;
			if(checkCount == 0){
				$.alert("未选择分类");
			}else{
				$.alert("您选择了" + checkCount + "个项【" + nodeNames +"】");
				var parentWin= "";
				if(isMain == '1'){
					parentWin=  top.index_frame;
				}else{
					parentWin=  top.index_frame.work_main;
				}
				parentWin.$('#xpath').val(paths);
				parentWin.$('#sortname').val(nodeNames);
				parentWin.$('#categoryId').val(ids);
			}
			$.closeFromInner();
		}
		$(document).ready(function(){
			var t = $("#treeDemo");
			$.get("${path}/system/FLTX/listContent.action?path=0&type=" +type,
					function(data) {
						var zNodes = eval("("+data+")");
						$.fn.zTree.init(t, setting, zNodes);
				});
		});
	</script>	
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap "
		style="height: 100%;">
		<div class="col-sm-5" style="height: 100%;">
			<div class="form-group">
				<div class="col-sm-offset-3 col-sm-6">
					<button type="button" class="btn btn-primary"
						onclick="returnCheckedNodeIds()">确认</button>
					&nbsp; <input class="btn btn-primary" type="button" value="关闭 "
						onclick="javascript:$.closeFromInner();" />
				</div>
			</div>
			<ul id="treeDemo" class="ztree form-wrap" style="overflow: auto;"></ul>
		</div>
	</div>
</body>
</html>