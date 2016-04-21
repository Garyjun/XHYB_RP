<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>EPUB在线预览</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1"></meta>
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css" />
<script type="text/javascript" src="${path}/appframe/plugin/jqueryJson/jquery.json-2.4.min.js"></script>
<script type="text/javascript" src="${path}/docviewer/js/scrollWheel.js"></script>

<script type="text/javascript">
//查询树形结构路径
var gotoUrl = "${path}/docviewer/epubviewer.action";
//初始化树
var setting = {
	view: {
		dblClickExpand: false,
        showLine: true,
        selectedMulti: false,
//         fontCss : {color:"blue"}
	},
	data: {
		simpleData: {
			enable: true
		}
	},
	callback: {
		beforeClick: beforeClickZtree,
// 		onClick: onClick
	}
};



$(document).ready(function(){
	var filePath = '<%=request.getParameter("filePath")%>'; //绝对路径
	var fileType ='<%=request.getParameter("fileType")%>'; 
	var id ='<%=request.getParameter("id")%>';
	var param = "?filePath=" + filePath + "&id=" + id + "&fileType=" + fileType + "&math=" + Math.random();
	var zNodes = new Array();
	$.ajax({               
        type: "get",      
        async:false,//同步
        dataType: "json",               
        url: encodeURI(gotoUrl + param),   
        success: function(treeData) {
        	if(treeData != null){
        		if(isNotNull(treeData.noteInfo)){
        			$('#errorHtml').html(errorHtml(treeData.noteInfo));
        		}else{
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
//         		var jsonArray = eval('(' + treeData + ')'); 
//         		alert(jsonArray);
    		}
		}
	});
// 	 var zTree = $.fn.zTree.getZTreeObj("tree");//获取ztree对象  
//      var node = zTree.getNodeByParam('id', 1);//获取id为1的点  
//      zTree.selectNode(node);//选择点  
//      zTree.setting.callback.onClick(null, zTree.setting.treeId, node);
     
	var ztree = $.fn.zTree.init($("#tree"), setting, zNodes);
	//默认展开一级节点
	var root = ztree.getNodes()[0];
	if(root){
		ztree.expandNode(root,true,false,true);
	}
	var h = $('#byMainPageBodySide').height();
	var w = $('#byMainPageBodySide').width();
	if(w < 900){
		w = 900;
	}
	if(h < 550){
		h = 550;
		$('#byMainPageBodySide').height(h);
		$('#byPageContent').height(h-20);
		$('#byMainPageBodyCenter').width(w-10-230);
		$('#byMainPageBodyCenter').height(h-10);
	}
	$('#tree').height(h-10);
	if(root.isParent==true){
		//父节点默认展开第一节点并选中
		for(var y = 0;y<root.children.length;y++){
			var fileUrl = root.children[y].fileUrl;
	 		if(fileUrl != null && fileUrl !=""){
			ztree.selectNode(root.children[y]);
			beforeClickZtree("", root.children[y]);
// 			$('#byPageContent').attr("src","${basePath}" + url);
			}
			break;
		}
	}else{
		var url = root.fileUrl;
		if(url != null && url !=""){
			ztree.selectNode(root);
			beforeClickZtree("", root);
// 			$('#byPageContent').attr("src","${basePath}" + url);
		}
	}
});

// function onClick(event, treeId, treeNode, clickFlag) {
// 	var url = treeNode.fileUrl;
// 	if(url != null && url !=""){
// 		$('#byPageContent').attr("src","${basePath}" + url);
// 	}
// }

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
<body id="errorHtml">
	<div class="by-main-page-body-side" id="byMainPageBodySide">
		<div class="page-sidebar">
			<div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn"
				onclick="toggleSideBar()">
				<i class="fa fa-angle-left"></i>
			</div>
			<div id="sideWrap"  style="background:#FAFAD2;">
				<div class="by-tool-box">
					<ul id="tree" class="ztree" style="overflow: auto;"></ul>
				</div>
			</div>
		</div>
	</div>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
		<iframe id="byPageContent" width="100%" height="100%" frameborder="0" src=""></iframe>
	</div>
</body>
</html>