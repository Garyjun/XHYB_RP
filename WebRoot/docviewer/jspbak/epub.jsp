<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>EPUB在线预览</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge"></meta>
<meta name="viewport" content="width=device-width, initial-scale=1"></meta>
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css" />
<script type="text/javascript" src="${path}/appframe/plugin/jqueryJson/jquery.json-2.4.min.js"></script>

<script type="text/javascript">
//查询树形结构路径
var treeNodeUrl = "${path}/docviewer/epubviewer.action";
//初始化树
var setting = {
	view: {
		dblClickExpand: false,
        showLine: true,
        selectedMulti: false
	},
	data: {
		simpleData: {
			enable: true
		}
	},
	callback: {
		beforeClick: beforeClickZtree
		
	}
};



$(document).ready(function(){
	var filePath = '<%=request.getParameter("filePath")%>'; //绝对路径
	var fileType ='<%=request.getParameter("fileType")%>'; 
	var id ='<%=request.getParameter("id")%>';
	var param = "?filePath=" + filePath + "&id=" + id + "&fileType=" + fileType;
	var zNodes = new Array();
	$.ajax({               
        type: "get",      
        async:false,//同步
        dataType: "json",               
        url: treeNodeUrl + param,   
        success: function(treeData) {
        	if(treeData != null){
        		for(var i = 0; i < treeData.length; i++){
        			var odata = {
							name:treeData[i].name,
							id:treeData[i].id,
							fileUrl:treeData[i].fileUrl,
							style:treeData[i].css,
							pId:treeData[i].pId
						};
        			zNodes.push(odata);
        			var sons = treeData[i].sons;
        			if(sons != null && sons != ""){
        				zNodes = drawBranch(zNodes,sons);
    				}
        		}
    		}
		}
	});
	$.fn.zTree.init($("#tree"), setting, zNodes);
	$('#tree').height($('#byMainPageBodySide').height() - 10);
});



function drawBranch(zNodes, treeData){
	for(var i = 0; i < treeData.length; i++){
		var odata = {
				name:treeData[i].name,
				id:treeData[i].id,
				fileUrl:treeData[i].fileUrl,
				style:treeData[i].css,
				pId:treeData[i].pId
			};
		zNodes.push(odata);
		var sons = treeData[i].sons;
		if(sons != null && sons != ""){
			zNodes = drawBranch(zNodes,sons);
		}
	}
	return zNodes;
}	



//单击事件
function beforeClickZtree(treeId, treeNode){
	//alert(treeNode.id + ","+ treeNode.name +  ","+treeNode.fileUrl);
	var url = treeNode.fileUrl;
	if(url != null && url !=""){
		$('#byPageContent').attr("src","${basePath}" + url);
	}
}


</script>
</head>
<body>
	<div class="by-main-page-body-side" id="byMainPageBodySide">
		<div class="page-sidebar">
			<div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn"
				onclick="toggleSideBar()">
				<i class="fa fa-angle-left"></i>
			</div>
			<div id="sideWrap">
				<div class="by-tool-box">
					<ul id="tree" class="ztree" style="overflow: auto;"></ul>
				</div>
			</div>
		</div>
	</div>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
		<iframe id="byPageContent" width="100%" height="100%" frameborder="0"
			src=""></iframe>
	</div>
</body>
</html>

