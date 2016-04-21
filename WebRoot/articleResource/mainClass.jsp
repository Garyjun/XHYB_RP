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
	function ajaxGetNodes(treeNode, reloadType) {
		var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		if (reloadType == "refresh") {
			treeNode.icon = "../../../css/zTreeStyle/img/loading.gif";
			zTree.updateNode(treeNode);
		}
		zTree.reAsyncChildNodes(treeNode, reloadType, true);
	}
	function getUrl(treeId, treeNode) {
		var typeId = '${param.typeId}';
		var param = "";
		if(treeNode == undefined){
			param = '0';
		}else{
			param = treeNode.id;
		}
		if(treeNode.classDate=='tmdate1'){
			var fieldName= "";
			fieldName = treeNode.name;
			fieldName = fieldName.substring(0,fieldName.length-1);
			//获得月份查询
			return "${path}/system/FLTX/monthJson.action?yearPath="+param+"&type="+typeId+"&year="+fieldName+"&monthField=month";
		}else if(treeNode.classDate=='month'){
			var fieldName= "";
			fieldName = treeNode.name;
			fieldName = fieldName.substring(0,fieldName.length-1);
			return "${path}/system/FLTX/dayJson.action?year="+param+"&type="+typeId+"&month="+fieldName+"&dayField=day";
		}else{
			return "${path}/system/FLTX/getEntryMainTime.action?path="+param+"&type="+typeId;
		}
	}
	function onAsyncSuccess(event, treeId, treeNode, msg) {
		if (!msg || msg.length == 0) {
			return;
		}
		var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		if(treeNode != null){
			treeNode.icon = "";
			var treeNodeNext = "";
			zTree.updateNode(treeNode);
			if(treeNode.isParent==true){
				treeNodeNext = treeNode.children[0];
				if(treeNodeNext.isParent==true){
					zTree.expandNode(treeNodeNext,true,false,true);
				}else{
					zTree.selectNode(treeNode.children[0]);
					zTreeOnClick("", "", treeNode.children[0]);
				}
// 				beforeClickZtree("", root.children[y]);
			}else{
				zTree.selectNode(treeNode);
			}
		}
	}
	function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
		var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		alert("异步获取数据出现异常。");
		treeNode.icon = "";
		zTree.updateNode(treeNode);
	}
		//单击节点
		function zTreeOnClick(event, treeId, treeNode) {
			var classTimeName = treeNode.name;
				classTimeName = encodeURI(encodeURI(classTimeName));
				var classDate = treeNode.classDate;
			var fieldValue = ","+treeNode.id+",";
			var years = "";
			var year = "";
			var month = "";
			var day = "";
			if(classDate =="tmTimeClass"){
				years = treeNode.code;
				years = years.substring(1,years.length);
			}else if(classDate =="tmdate1"){
				year = treeNode.name;
				year = year.substring(0,year.length-1);
// 				month = 
			}else if(classDate =="month"){
				year = treeNode.id;
				month = treeNode.name;
				month = month.substring(0,month.length-1);
			}else if(classDate =="day"){
				year = treeNode.pid;
				month = treeNode.month;
				day = treeNode.name;
				day = day.substring(0,day.length-1);
			}
// 			day = day.substring(0,day.length-1);
// 			var cbclassFieldValue = fieldName+'_metaField';
// 			'+parent.$('#publishType').val()+'    '+parent.$('#status').val()+'
			var url = '${path}/publishRes/entryList.action?publishType=1';
			if(years!=""){
				url = url+'&years='+years;
			}
			if(year!=""){
				url = url+'&year='+year;
			}
			if(month!=""){
				url = url+'&month='+month;
			}
			if(day!=""){
				url = url+'&day='+day;
			}
// 	 		url +='&'+cbclassFieldValue+'='+fieldValue+"&cbclassField="+cbclassFieldValue;
// 			beforeExpand(fieldValue, treeNode);
			parent.$('#work_main').attr('src',url);
		}
		function setNodeFile(id,pid,fileName,isDir,path,md5,object){
			var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			var treeNode = treeObj.getNodeByParam("nodeId", pid, null);
			treeObj.addNodes(treeNode, {nodeId:id,pid:pid,name:fileName,isDir:isDir,path:path,Md5:md5,object:object});
		}
		$(document).ready(function(){
			var typeId = '${param.typeId}';
// 			var url = '${path}/publishRes/entryList.action?publishType=1'+'&status='+parent.$('#status').val()+'&flag=${param.flag}';
// 			parent.$('#work_main').attr('src',url);
			var t = $("#treeDemo");
			$.get("${path}/system/FLTX/getEntryMainTime.action?path=0&type="+typeId,
			function(data) {
				var zNodes = eval("("+data+")");
				var ztree = $.fn.zTree.init(t, setting, zNodes);
				var root = ztree.getNodes()[0];
				if(root){
					ztree.expandNode(root,true,false,true);
				}
				if(root.isParent==true){
					//父节点默认展开第一节点并选中
				 	zTreeOnClick("", "", root);
				}
			});
		});
	</script>	
</head>
<body>
<!-- <div style="border:1px solid #DDD"> -->
			<ul id="treeDemo" class="ztree" style="overflow-y: scroll;overflow-x:auto;height:599px;"></ul>
<!-- </div> -->
</body>
</html>