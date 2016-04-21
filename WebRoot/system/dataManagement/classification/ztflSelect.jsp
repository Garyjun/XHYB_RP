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
	var selectType= '${param.selectType}'
	var lineNumber = '${param.lineNumber}'
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
			var paths = ",";
			var nodeNames = "";
			var ids = "";
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			var nodes = zTree.getCheckedNodes(true);
			var checkCount = nodes.length;
			for(var i = 0; i < nodes.length; i++){
				paths += nodes[i].xpath +",-,";
				ids += nodes[i].id +",";
				nodeNames += nodes[i].name +",";
			}
			if(checkCount>0){
				paths = paths.substr(0,paths.length-2);
				nodeNames = nodeNames.substr(0,nodeNames.length-1);
				ids = ids.substr(0,ids.length-1);
			}
			if(checkCount == 0){
				$.alert("未选择分类");
			}else{
				if(isMain == '5'){
					layer.alert("您选择了" + checkCount + "个项【" + nodeNames +"】");    
				}else{
					$.alert("您选择了" + checkCount + "个项【" + nodeNames +"】");
				}
				var parentWin= "";
				if(isMain == '1'){
					parentWin=  top.index_frame;
				}else if(isMain == '5'){
					parentWin = parent;
				}else{
					parentWin=  top.index_frame.work_main;
				}
				$.ajax({ 
			       type: "post", 
			       url: "${path}/system/FLTX/queryNameAndPath.action?id="+ids, 
			       cache:false, 
			       async:false, 
			       dataType: "json", 
			       success: function(data){ 
						for(var i=0;i<data.length;i++){
							var obj = data[i];
							var fieldNameTemp =  obj.filedName;
							var nameTemp = obj.name;
							var pathTemp = obj.path;
							parentWin.$('#'+fieldNameTemp).val(pathTemp);
							parentWin.$('#'+fieldNameTemp+'Name').val(nameTemp);
						}
						
			       } 
				});
				if(selectType == '0'){
					parentWin.$('#inputValue'+lineNumber).val(nodeNames);
					parentWin.$('#inputValueHideCode'+lineNumber).val(paths);
				}else{
					parentWin.$('#'+fieldName).val(paths);
					parentWin.$('#'+fieldName+'Name').val(nodeNames);
				}
				
			}
			
			if(isMain == '5'){
				var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
				parent.layer.close(index); //再执行关闭  
			}else{
				$.closeFromInner();
			}
			
		}
		$(document).ready(function(){
			var t = $("#treeDemo");
			$.get("${path}/system/FLTX/listContent.action?path=0&type=" +type,
					function(data) {
						var zNodes = eval("("+data+")");
						$.fn.zTree.init(t, setting, zNodes);
				});
		});
		
		function closelayer(){
			if(isMain == '5'){
				var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
				parent.layer.close(index); //再执行关闭  
			}else{
				$.closeFromInner();
			}
		}
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
						onclick="closelayer()" />
				</div>
			</div>
			<ul id="treeDemo" class="ztree form-wrap" style="overflow: auto;"></ul>
		</div>
	</div>
</body>
</html>