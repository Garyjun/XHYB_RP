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
			data: {
				simpleData: {
					enable: true
				}
			},
			view: {
	            addHoverDom: addHoverDom,
	            removeHoverDom: removeHoverDom,
				expandSpeed: ""
			},
			callback: {
				beforeExpand: beforeExpand,
				onAsyncSuccess: onAsyncSuccess,
				onAsyncError: onAsyncError,
				onClick: zTreeOnClick
			}
		};
	
	    function addHoverDom(treeId, treeNode) {
	        var sObj = $("#" + treeNode.tId + "_span");
	        if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
	        var addStr = "<sec:authorize url='/system/dataManagement/classification/addZTFLNode.action'><span class='button add' id='addBtn_" + treeNode.tId
	        		+ "'title='创建节点' onfocus='this.blur();'></span></sec:authorize>";
	        addStr += "<sec:authorize url='/system/dataManagement/classification/delZTFLNode.action'><span class='button remove' id='removeBtn_" + treeNode.tId
	                + "' title='删除节点' onfocus='this.blur();'></span></sec:authorize>";
	        sObj.after(addStr);
	        var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	        var add = $("#addBtn_"+treeNode.tId);
	        if (add) add.bind("click", function(){
	            var newNode = {name:"newNode",pid:treeNode.nodeId,nodeId:_getRandomString(8)};
	            newNode = zTree.addNodes(treeNode, newNode);
	            return false;
	        });
	        
	        var del = $("#removeBtn_"+treeNode.tId);
	        if (del) del.bind("click", function(){
            	$.confirm('确定要删除所选数据吗？', function(){
		        	$.post("delZTFLNode.action",
		            		{
		            			objectId:treeNode.nodeId
		            		},
		            		function(data){
		    		        	zTree.removeNode(treeNode);
		            		} 
		            );
            	});
	        });
	    };
	
	    function removeHoverDom(treeId, treeNode) {
	        $("#removeBtn_"+treeNode.tId).unbind().remove();
	        $("#addBtn_"+treeNode.tId).unbind().remove();
	    };
	    
		//修改节点
		function editNode() {
			if(!$("#nodeLabel").val()||$("#nodeLabel").val()=="newNode"){
				alert("请修改节点信息后再保存");
				return;
			}
			var nodeId = $("#nodeNumber").val();
			var t = $.fn.zTree.getZTreeObj("treeDemo");
			var node = t.getNodesByParam("nodeId",nodeId)[0];
			node.name = $("#nodeLabel").val();
			node.code = $("#code").val();
			t.updateNode(node);
			var ZTFLTree = {"node":JSON.stringify(node)};
			//提交修改
			$.post("addZTFLNode.action", ZTFLTree,
				function(data) {
					alert(data);
					var node = t.getNodesByParam("nodeId",nodeId)[0];
					node.nodeId = data;
					t.updateNode(node);
			});
		}

		function getUrl(treeId, treeNode) {
			var param = treeNode.nodeId;
			return "listZTFL.action?path=" + param;
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
			$("#nodeLabel").val(treeNode.name);
			$("#code").val(treeNode.code);
			$("#nodeNumber").val(treeNode.nodeId);
		}

		$(document).ready(function(){
			var t = $("#treeDemo");
			$.get("listZTFL.action?path=root",
					function(data) {
						var zNodes = eval("("+data+")");
						$.fn.zTree.init(t, setting, zNodes);
						var root = t.getNodes()[0];
						if(root){
							t.expandNode(root,true,false,true);
						}
				});
		});
	</script>	
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
	    <ul class="page-breadcrumb breadcrumb">
	        <li>
	            <a href="###">分类体系管理</a>
	            <i class="fa fa-angle-right"></i>
	        </li>
	        <li>
	            <a href="###">中图分类</a>
	        </li>
    	</ul>
		<div class="col-sm-5" style="height: 100%;">
			<ul id="treeDemo" class="ztree form-wrap" style="overflow:auto;"></ul>
		</div>
		<div class="col-sm-5" style="float: left;">
			<div class="row">
				<div class ="form-wrap">
					<label class="control-label col-md-3">节点名称：</label>
					<div class="col-md-7">
						<input id="nodeLabel" type="text" class="form-control"/>
					</div>
				</div>
			</div>
			<div class="row">
				<div class ="form-wrap">
					<label class="control-label col-md-3">节点编码：</label>
					<div class="col-md-7">
						<input id="code" type="text" class="form-control"/>
					</div>
				</div>
			</div>			
			<div class="row">
				<div class ="form-wrap">
					<label class="control-label col-md-3"></label>
					<div class="col-md-4">
						<sec:authorize url='/system/dataManagement/classification/addZTFLNode.action'>
			           	<input id="submit" type="button" value="保存修改" 
			           				class="btn btn-primary" onclick="javascript:editNode();"/> 
			           	</sec:authorize>
			        </div>
		        </div>
			</div>			
		</div>
	</div>
	<input id="nodeNumber" type="hidden"/>
	<input id="fromId" type="hidden"/>
</body>
</html>