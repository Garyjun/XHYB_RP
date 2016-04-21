<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>资源视图-预览</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1"></meta>
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css" />
<script type="text/javascript" src="${path}/appframe/plugin/jqueryJson/jquery.json-2.4.min.js"></script>
<style>
	.by-main-page-body-side {
	  position: absolute;
	  top: 0;
	  left: 0;
	  z-index: 99;
	  width: 300px;
	  background-color: #fff;
	}
	.by-main-page-body-side .page-sidebar {
	  height: 100%;
	  padding-left: 0;
	  padding-right: 0;
	  width: 300px;
	  background-color: #e5e5e5;
	}
	.sidebar-toggler {
	  cursor: pointer;
	  width: 20px;
	  height: 20px;
	  position: absolute;
	  top: 10px;
	  left: 280px;
	  background-color: #ffffff;
	  text-align: center;
	  z-index: 9999;
	}
	#sideWrap {
	  width: 300px;
	  background-color: #e5e5e5;
	}
</style>
<script type="text/javascript">
	var directorySetting = {
		view: {
			addHoverDom: addHoverDom,
			removeHoverDom: removeHoverDom,
			selectedMulti: false
		},
		edit: {
			enable: true,
			showRemoveBtn: false,
			showRenameBtn: false
		},
		data: {
			simpleData: {
				enable: true,
				idKey: "nodeId",
				pIdKey: "pid"
			}
		},
		callback: {
			beforeDrag: beforeDrag,
			beforeRemove: beforeRemove,
			onClick: onClickNode
		}
	};

	$(function() {
		var datas = '${ztreeJson}';
		if(datas!=""){
			zNodes = eval('('+datas+')');
			$.fn.zTree.init($("#directoryTree"), directorySetting, zNodes);
			$.fn.zTree.getZTreeObj("directoryTree").expandAll(true);//展开树
		}else{
			$.fn.zTree.init($("#directoryTree"), directorySetting, null);
			$.fn.zTree.getZTreeObj("directoryTree").expandAll(true);//展开树
		}
	});

	function removeHoverDom(treeId, treeNode) {
		//添加预览、下载按钮
		$("#previewFile_" + treeNode.tId).unbind().remove();
		$("#downloadFile_" + treeNode.tId).unbind().remove();
	};
	function beforeDrag(treeId, treeNodes) {
		return false;
	}
	function beforeRemove(treeId, treeNode) {
		className = (className === "dark" ? "" : "dark");
		var zTree = $.fn.zTree.getZTreeObj("directoryTree");
		zTree.selectNode(treeNode);
		return true;
	}
	function onClickNode(event, treeId, treeNode, clickFlag) {
		var zTree = $.fn.zTree.getZTreeObj("directoryTree");
		var sObj = $("#" + treeNode.tId + "_span");
		if (treeNode.editNameFlag == false && treeNode.isDir == "2" ) { 
			var filePath = treeNode.path + ",";
			var filePaths = "";
			var isImageType = "jpgpngbmptiftiffjpeggif";
			var objectId = $('#objectId').val();
			var fileObjectId = treeNode.object;
			var fileZhName = treeNode.name;
			filePath = replaceAllString(filePath, "\\", "/");
			fileName = filePath.substring(filePath.lastIndexOf("/") + 1,
					filePath.lastIndexOf("."));
			var flag = false;
			if (treeNode.children != undefined && treeNode.children.length > 0) {
				flag = true;
				for (var y = 0; y < treeNode.children.length; y++) {
					var imageType = treeNode.children[y].path;
					imageType = imageType.substring(imageType.lastIndexOf(".") + 1,imageType.length);
					if (isImageType.indexOf(imageType) >= 0) {
						filePaths = filePaths+ treeNode.children[y].path+ ",";
					}
				}
			}
			if (filePaths != "") {
				filePath = filePaths;
			}
			filePath = filePath.substring(0,filePath.length - 1);
			if (flag) {
				//readFileOnline(filePath, objectId,fileType, fileZhName);
				$("#previewDiv").css("display","");
				readFileOnline(filePath, fileObjectId, fileType, fileZhName, null, null, '2');
			} else {
				var fileType = filePath.substring(
						filePath.lastIndexOf(".") + 1,
						filePath.length);
				if (fileType.indexOf("/") >= 0) {
					//$.alert("此文件不支持预览");
					notice("提示","此文件不支持预览","3");
					return;
				}
				//readFileOnline(filePath, objectId, fileType, fileZhName, fileName);
				$("#previewDiv").css("display","");
				readFileOnline(filePath, fileObjectId, fileType, fileZhName, fileName, null, '2');
			}
			//相对路径
		}
	}
	function addHoverDom(treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj("directoryTree");
		var sObj = $("#" + treeNode.tId + "_span");
		if (treeNode.editNameFlag == false && treeNode.isDir == "2" ) { 
			
			/* //预览按钮
			if ($("#previewFile_"+treeNode.tId).length==0){ 
				var previewStr = "<span class='button preLook' id='previewFile_"
						+ treeNode.tId
						+ "' title='预览' onfocus='this.blur();'></span>";
				sObj.after(previewStr);
				var btnRes = $("#previewFile_" + treeNode.tId);//预览按钮
				var filePath = treeNode.path + ",";
				var filePaths = "";
				var isImageType = "jpgpngbmptiftiffjpeggif";
				var objectId = $('#objectId').val();
				var fileObjectId = treeNode.object;
				var fileZhName = treeNode.name;
				filePath = replaceAllString(filePath, "\\", "/");
				fileName = filePath.substring(filePath.lastIndexOf("/") + 1,
						filePath.lastIndexOf("."));
				var flag = false;
				if (btnRes){
					btnRes.bind(
								"click",
								function() {
									if (treeNode.children != undefined && treeNode.children.length > 0) {
										flag = true;
										for (var y = 0; y < treeNode.children.length; y++) {
											var imageType = treeNode.children[y].path;
											imageType = imageType.substring(imageType.lastIndexOf(".") + 1,imageType.length);
											if (isImageType.indexOf(imageType) >= 0) {
												filePaths = filePaths+ treeNode.children[y].path+ ",";
											}
										}
									}
									if (filePaths != "") {
										filePath = filePaths;
									}
									filePath = filePath.substring(0,filePath.length - 1);
									if (flag) {
										//readFileOnline(filePath, objectId,fileType, fileZhName);
										$("#previewDiv").css("display","");
										readFileOnline(filePath, fileObjectId, fileType, fileZhName, null, null, '2');
									} else {
										var fileType = filePath.substring(
												filePath.lastIndexOf(".") + 1,
												filePath.length);
										if (fileType.indexOf("/") >= 0) {
											//$.alert("此文件不支持预览");
											notice("提示","此文件不支持预览","3");
											return;
										}
										//readFileOnline(filePath, objectId, fileType, fileZhName, fileName);
										$("#previewDiv").css("display","");
										readFileOnline(filePath, fileObjectId, fileType, fileZhName, fileName, null, '2');
									}
									//相对路径
								});
				}
			} */
			
			//下载按钮事件
			if ($("#downloadFile_"+treeNode.tId).length==0){
				
				var downloadFileStr = "<span class='button download' id='downloadFile_"
						+ treeNode.tId
						+ "' title='下载' onfocus='this.blur();'></span>";
				sObj.after(downloadFileStr);
				var downloadFileBtn = $("#downloadFile_" + treeNode.tId);//下载按钮
				if (downloadFileBtn)
					downloadFileBtn.bind("click", function() {
						filePath = encodeURI(encodeURI(treeNode.path));
						var zhName = encodeURI(encodeURI(treeNode.name));
						window.location.href = "${path}/publishRes/download.action?filePath="+filePath+"&name="+zhName;
					}); 
			}
			
		}
		
		
		
	};
	function shrinkDiv(){
		var value = $("#stateBut").html();
		if(value=="左侧收缩"){
			$("#stateBut").html("左侧展开");
			$("#ztreeDiv").css("display","none");
			$("#previewDiv").removeClass("col-md-8");  
			$("#previewDiv").addClass("col-md-12");  
		}else if(value=="左侧展开"){
			$("#stateBut").html("左侧收缩");
			$("#ztreeDiv").css("display","block");
			$("#previewDiv").removeClass("col-md-12");  
			$("#previewDiv").addClass("col-md-8");
		}
	}
</script>
</head>
<body>
 	<input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
	<!-- <div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%">
		<div class="form-wrap">
			<label id="stateBut" onclick="shrinkDiv()" style="align:center;">左侧收缩</label>
			<div class="row">
				<div id="ztreeDiv" class="col-md-4" style=" border: 1px solid; border-color: #dddddd; height: 545px; overflow: auto;">
					<div class="zTreeDemoBackground left">
						<ul id="directoryTree" class="ztree form-wrap"></ul>
					</div>
				</div>
				<div id="previewDiv" class="col-md-8" style="display:none;">
				   <iframe id="byPageContent" name="previewFrame" width="890px" height="545px" frameborder="0"></iframe>
			    </div>
			</div>

		</div>
	</div> -->
	
	<div class="by-main-page-body-side" id="byMainPageBodySide"  >
		<div class="page-sidebar"> <!-- style="" -->
			<div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBarForPreview()" >
				<i class="fa fa-angle-left"></i>
			</div>
			<div id="sideWrap">
				<div class="by-tool-box" style="height:587px;width:300px;overflow: auto;background:#E5E5E5;">
					<ul id="directoryTree" class="ztree" style=""></ul>
				</div>
			</div>
		</div>
	</div>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter" style="margin-left:300px;">
		<iframe id="byPageContent" name="previewFrame" width="100%" height="100%" frameborder="0" src=""></iframe>
	</div>
</body>
</html>