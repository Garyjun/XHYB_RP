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
				enable: true,
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
			}
		};
	
		function getUrl(treeId, treeNode) {
			var param = "";
			if(treeNode == undefined){
				param = '0';
			}else{
				param = treeNode.id;
			}
			return "${path}/system/FLTX/listContent.action?path="+param+"&type="+"${typeId}";
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
		
		
		function getTreeValue() {
			var t = $("#treeDemo");
			$.get("${path}/system/FLTX/listContent.action?path=0&type="+"${typeId}",
					function(data) {
						var zNodes = eval("("+data+")");
						$.fn.zTree.init(t, setting, zNodes);
			});
		}
		
		//修改点选规则   选中第一个分类后  再选第二个分类  会以追加的形式  而不是以覆盖的形式
		function getCodeName() {
			var parentWin=  top.index_frame;
			
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			var nodes = treeObj.getCheckedNodes(true);
			//获取父页面id为codeName的值
			var codeName = parentWin.$("#codeName").val();
			var path = parentWin.$("#paths").val();
			if(codeName == "" || codeName == undefined ){
				codeName = "";
			}
			if(path == "" || path == undefined ){
				path = ",";
			}else{
				path += "-,";
			}
			for(var i=0;i<nodes.length;i++){
				var name = nodes[i].name;
				var code = nodes[i].code;
				path += nodes[i].id+",-,";
				codeName += code+"|"+name+"\n";
			}
			//去掉最后面的-,
			path = path.substr(0,path.length-2);
			
			parentWin.$("#codeName").val(codeName);
			parentWin.$("#paths").val(path);
			$.closeFromInner();
		}
		
		function ajaxGetNodes(treeNode, reloadType) {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			if (reloadType == "refresh") {
				treeNode.icon = "../../../css/zTreeStyle/img/loading.gif";
				zTree.updateNode(treeNode);
			}
			zTree.reAsyncChildNodes(treeNode, reloadType, true);
		}
		$(function() {
			getTreeValue();
		});
	</script>	
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap"
		style="height: 100%;">
		<div class="form-wrap">
				<div class="col-md-6">
					<div class="zTreeDemoBackground left">
						<ul id="treeDemo" class="ztree"></ul>
					</div>
					<input type="button" value="确定" class="btn btn-primary " style="width: 100px;margin-left:200px;" onclick="getCodeName();" />
				</div>
		 </div>
	</div>
</body>
</html>