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
	$(function(){
		getTreeType();
		getTreeValue();
	});
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
					enable: true,
					idKey: "id",
					pIdKey: "pid"
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
			return "${path}/system/FLTX/listContent.action?path="+param+"&type="+$("#searchFlag").val();
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
			$.get("${path}/system/FLTX/listContent.action?path=0&type="+$("#searchFlag").val(),
					function(data) {
				        var zNodes = jQuery.parseJSON(data);
						var showname = $("#searchFlag").find("option:selected").text();
						if($("#searchFlag").val()!=1){
							var root = {"id":0,"pid":-1,"name":decodeURIComponent(showname),"xpath":"0",open:true};
							zNodes.unshift(root);
						}
						$.fn.zTree.init(t, setting, zNodes);
			});
		}
		
		function getCodeName() {
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			var nodes = treeObj.getCheckedNodes(true);
			if(nodes.length>1) {
				$.alert("请选择一个节点进行关联操作");
			}else{
				var centerType = "${type}";
				var centerId = "${treeId}";
				var treeRelationId ="";
				var treeRelationName = "";
				for(var i=0;i<nodes.length;i++){
					treeRelationId = nodes[i].id;
					treeRelationName =  nodes[i].name;
					
				}
				var parentWin=  top.index_frame.work_main;
				parentWin.$("#treeRelationId").val(treeRelationId);
				parentWin.$("#treeRelationType").val($("#searchFlag").val());
				var relationType = $("#searchFlag").val();
				subRelationType(centerType,centerId,relationType,treeRelationId,treeRelationName);
			}
		}
		
		function subRelationType(centerType,centerId,relationType,relationId,treeRelationName) {
			var showname = $("#searchFlag").find("option:selected").text();
			$.ajax({
				type : "post",
				url : "${path}/system/FLTX/addRelationType.action?centerType="+centerType+"&centerId="+centerId+"&relationType="+relationType+"&relationId="+relationId
						+"&treeRelationName="+treeRelationName+"&relationTypeName="+showname+"&typeName="+"${typeName}"+"&treeNodeName="+"${treeNodeName}",
				success : function(data) {
					if(data=="1") {
						$.alert("节点已经被关联,请重新选择!");
					}else if(data=="0") {
						var parent=  top.index_frame.work_main;
						parent.queryForm();
						$.closeFromInner();
					}
				}
			});
		}
		
		function ajaxGetNodes(treeNode, reloadType) {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			if (reloadType == "refresh") {
				treeNode.icon = "../../../css/zTreeStyle/img/loading.gif";
				zTree.updateNode(treeNode);
			}
			zTree.reAsyncChildNodes(treeNode, reloadType, true);
		}
		
		function getTreeType() {
			$.ajax({
				url:"${path}/system/FLTX/listMenu.action",
				async:false,
				success:function(data){
					var menu = jQuery.parseJSON(data);
					$.each(menu,function(n,node){
						if("${type}"!=node.id) {
							var option = $("<option value='"+node.id+"'>"+ node.name + "</option>");
							option.appendTo($('#searchFlag'));
							
						}
					});
				}
			});		
		}
		
		/* function getTreeType() {
			$.ajax({
				url:"${path}/system/FLTX/queryCheckType.action?centerType="+"${type}",
				async:false,
				success:function(data){
					var menu = jQuery.parseJSON(data);
					$.each(menu,function(n,node){
						var option = $("<option value='"+node.id+"'>"+ node.name + "</option>");
						option.appendTo($('#searchFlag'));
					});
				}
			});	
		} */
	</script>	
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap"
		style="height: 100%;">
		<div class="form-wrap">
				<div class="col-md-6">
				     <div class="row">
							<label class="control-label col-md-3">资源树类型：</label>
							<div class="col-md-6">
							<select class="form-control" name="searchFlag" id="searchFlag"
									onchange="getTreeValue();">
							</select>
							</div>
					</div>
					<div class="zTreeDemoBackground left">
						<ul id="treeDemo" class="ztree"></ul>
					</div>
					<input type="button" value="确定" class="btn btn-primary " style="margin-left:200px;" onclick="getCodeName();" />
					<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
				</div>
		 </div>
	</div>
</body>
</html>