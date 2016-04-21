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
				enable: false,
// 				url: getUrl
			},
			check: {
				enable: true,
				chkStyle: "radio",
// 				chkboxType: { "Y": "", "N": "" },
				radioType: "all"
			},
			data: {
				simpleData: {
					enable: true,
					idKey: "nodeId",
					pIdKey: "pid"
				}
			},
			view: {
				expandSpeed: ""
			},
			callback: {
// 				beforeExpand: beforeExpand,
// 				onAsyncSuccess: onAsyncSuccess,
// 				onAsyncError: onAsyncError,
				onClick: zTreeOnClick
// 			}
// 		};
	
// 		function getUrl(treeId, treeNode) {
// 			var param = "";
// 			if(treeNode == undefined){
// 				param = '0';
// 			}else{
// 				param = treeNode.id;
// 			}
// 			return "${path}/system/FLTX/listContent.action?path=" + param+"&type=" +type;
// 		}
		
// 		function beforeExpand(treeId, treeNode) {
// 			if (!treeNode.isAjaxing) {
// 				treeNode.times = 1;
// 				ajaxGetNodes(treeNode, "refresh");
// 				return true;
// 			} else {
// 				$.alert("zTree 正在下载数据中，请稍后展开节点。。。");
// 				return false;
// 			}
// 		}
// 		function onAsyncSuccess(event, treeId, treeNode, msg) {
// 			if (!msg || msg.length == 0) {
// 				return;
// 			}
// 			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
// 			if(treeNode != null){
// 				treeNode.icon = "";
// 				zTree.updateNode(treeNode);
// 				zTree.selectNode(treeNode);
// 			}
// 		}
// 		function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
// 			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
// 			$.alert("异步获取数据出现异常。");
// 			treeNode.icon = "";
// 			zTree.updateNode(treeNode);
// 		}
// 		function ajaxGetNodes(treeNode, reloadType) {
// 			var zTree = $.fn.zTree.getZTreeObj("tree");
// 			if (reloadType == "refresh") {
// 				treeNode.icon = "../../../css/zTreeStyle/img/loading.gif";
// 				zTree.updateNode(treeNode);
// 			}
// 			zTree.reAsyncChildNodes(treeNode, reloadType, true);
// 		}
		
		//单击节点
		function zTreeOnClick(event, treeId, treeNode) {
			$("#nodeLabel").val(treeNode.name);
			$("#code").val(treeNode.code);
			$("#nodeNumber").val(treeNode.nodeId);
		}
		function returnCheckedNodeIds() {
			var fieldName = "";
			var fieldCnName = "";
			var ids = "";
			var zTree = $.fn.zTree.getZTreeObj("tree");
			var nodes = zTree.getCheckedNodes(true);
			var checkCount = nodes.length;
			for(var i = 0; i < nodes.length; i++){
				fieldName += nodes[i].nodeId +",";
				fieldCnName += nodes[i].name +",";
			}
			if(checkCount>0){
				fieldName = fieldName.substr(0,fieldName.length-2);
				fieldCnName = fieldCnName.substr(0,fieldCnName.length-1);
			}
			if(checkCount == 0){
				$.alert("未选择分类");
			}else{
				$.alert("fieldName"+fieldName+"fieldCnName"+fieldCnName);
				window.opener.document.getElementById('fieldValue').value="5";
// 				var parentWin= "";
// 				if(isMain == '1'){
// 					parentWin=  top.index_frame;
// 				}else{
// 					parentWin=  top.index_frame.work_main;
// 				}
// 				$.ajax({ 
// 			       type: "post", 
// 			       url: "${path}/system/FLTX/queryNameAndPath.action?id="+ids, 
// 			       cache:false, 
// 			       async:false, 
// 			       dataType: "json", 
// 			       success: function(data){ 
// 						for(var i=0;i<data.length;i++){
// 							var obj = data[i];
// 							var fieldNameTemp =  obj.filedName;
// 							var nameTemp = obj.name;
// 							var pathTemp = obj.path;
// 							parentWin.$('#'+fieldNameTemp).val(pathTemp);
// 							parentWin.$('#'+fieldNameTemp+'Name').val(nameTemp);
// 						}
// 			       } 
// 				});
// 				parentWin.$('#'+fieldName).val(paths);
// 				parentWin.$('#'+fieldName+'Name').val(nodeNames);
			}
			$.closeFromInner();
		}
		$(document).ready(function(){
			var t = $("#tree");
// 			alert(top.index_frame.work_main.document.getElementById("field").value);
			$.get("${path}/publishRes/returnZtreeMetadata.action",
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
			  <div id="lead" class="card">			

				<h3>查看效果</h3>	
				<p>
				 <div class="topnav">
					<a id="selectpos" href="javascript:void(0);" class="as">
						<span>选择职位</span>			
					</a>				
					
				</div>	
				<br>
				<div class="topnav">
					<a id="selectdept" href="javascript:void(0);" class="as">
						<span>选择部门</span>
					</a>					
					
				</div>				
			</p>
			<h3>隐藏域值</h3>	
			<pre><input type="text" value="" id="selectposhidden" />
			
				<input type="text" value="" id="selectdeptidden" />
			</pre>
		</div>
		<div class="col-sm-5" style="height: 100%;">
			<div class="form-group">
				<div class="col-sm-offset-3 col-sm-6">
					<button type="button" class="btn btn-primary"
						onclick="returnCheckedNodeIds()">确认</button>
					&nbsp; <input class="btn btn-primary" type="button" value="关闭 "
						onclick="javascript:$.closeFromInner();" />
				</div>
			</div>
			<ul id="tree" class="ztree form-wrap" style="overflow: auto;"></ul>
		</div>
	</div>
</body>
</html>