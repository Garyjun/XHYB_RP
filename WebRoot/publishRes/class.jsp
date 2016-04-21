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
	var setting = {
			async: {
				enable: true,
				url: getUrl
			},
			check: {
				enable: false,
				chkStyle: "checkbox",
				chkboxType: { "Y": "", "N": "" },
				radioType: "all"
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			view: {
				expandSpeed: ""
			},
			callback: {
				beforeExpand: beforeExpand,
				onAsyncSuccess: onAsyncSuccess,
				onAsyncError: onAsyncError,
				onClick: zTreeOnClick
			}
		};
	
	function getUrl(treeId, treeNode) {
		var typeId = '${param.typeId}';
		var param = "";
		if(treeNode == undefined){
			param = '0';
		}else{
			param = treeNode.id;
		}
		return "${path}/system/FLTX/listContent.action?path="+param+"&type="+typeId;
	}
		
	function beforeExpand(treeId, treeNode) {
		if (!treeNode.isAjaxing) {
			treeNode.times = 1;
			ajaxGetNodes(treeNode, "refresh");
			return true;
		} else {
			alert("zTree 正在下载数据中，请稍后展开节点。。。");
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
		alert("异步获取数据出现异常。");
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
			var fieldName = '${param.fieldName}';
			var fieldValue = ","+treeNode.id+",";
			var cbclassFieldValue = fieldName+'_metaField';
			var url = '${path}/publishRes/publishResList.action?publishType='+parent.$('#publishType').val()+'&status='+parent.$('#status').val()+'&offline=${param.offline}';
	 		url +='&'+cbclassFieldValue+'='+fieldValue+"&cbclassField="+cbclassFieldValue;
			parent.$('#work_main').attr('src',url);
		}
		$(document).ready(function(){
			var typeId = '${param.typeId}';
			var url = '${path}/publishRes/publishResList.action?publishType=1'+'&status='+parent.$('#status').val()+'&offline=${param.offline}';
			parent.$('#work_main').attr('src',url);
			var t = $("#treeDemo");
			$.get("${path}/system/FLTX/listContent.action?path=0&type="+typeId,
					function(data) {
						var zNodes = eval("("+data+")");
						$.fn.zTree.init(t, setting, zNodes);
			});
		});
	</script>	
</head>
<body style="overflow:hidden">
<div style="border:1px solid #DDD">
			<ul id="treeDemo" class="ztree" style="overflow-y: scroll;overflow-x:auto;height:345px;"></ul>
</div>
</body>
</html>