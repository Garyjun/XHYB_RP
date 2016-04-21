<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>文件在线预览</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge"></meta>
<meta name="viewport" content="width=device-width, initial-scale=1"></meta>
<script type="text/javascript" src="${path}/appframe/plugin/flexPaper/flexpaper_flash.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css" />
<script type="text/javascript" src="${path}/appframe/plugin/jqueryJson/jquery.json-2.4.min.js"></script>

<script type="text/javascript">
	//查询树形结构路径
	var treeNodeUrl = "${path}/docviewer/pdfViewer2nav.action"; //获取pdf导航菜单
	var filePath = '<%=request.getParameter("filePath")%>'; //绝对路径
	var fileType ='<%=request.getParameter("fileType")%>';
	var id ='<%=request.getParameter("id")%>';
	//filePath = "/23423你说地方/cvsd/sdfs/岁的斯 蒂芬斯   蒂芬电风扇地方sdfsdfsdfasfs你好毒阿斯蒂芬 诉讼费斯蒂芬斯蒂芬森的.jsdf";
	var dirChar = id;   //生成随机字符串,用于生成目录
	if (!isNotNull(id)) {
		dirChar = CC2PY(getFileNameNoExt(filePath));  //生成随机字符串,用于生成目录
		if(dirChar != null && dirChar.length > 100){
			dirChar = dirChar.substring(0,100);
		}
	}
	
	
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
			
		}
	};
	
	
	
	$(document).ready(function(){
		var param = "?dirChar=" + dirChar + "&filePath=" + filePath + "&id=" + id;
		var zNodes = new Array();
		$.ajax({               
	        type: "get",      
	        async:false,//同步
	        dataType: "json",               
	        url: treeNodeUrl + param,   
	        success: function(treeData) {
	        	//console.log(treeData);
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
		<a id="viewerPlaceHolder"
			style="width: 100%; height: 100%; display: block"></a>
	</div>
</body>
<script type="text/javascript">
	//加载swf控件
	var fp = new FlexPaperViewer(	
	   "${path}/appframe/plugin/flexPaper/FlexPaperViewer",
	   "viewerPlaceHolder", { config : {
	   SwfFile : "{${path}/tempfile/viewer/pdf/" + dirChar +"/" + dirChar +"_[*,0].swf}",
			Scale : 1.0,
			ZoomTransition : 'easeOut',
			ZoomTime : 0.5,
			ZoomInterval : 0.1,
			FitPageOnLoad : false,
			FitWidthOnLoad : true,
			PrintEnabled : false,
			FullScreenAsMaxWindow : false,
			ProgressiveLoading : true,
			MinZoomSize : 0.2,
			MaxZoomSize : 5,
			SearchMatchAll : true,
			InitViewMode : 'Portrait',
			ViewModeToolsVisible : true,
			ZoomToolsVisible : true,
			NavToolsVisible : true,
			CursorToolsVisible : true,
			SearchToolsVisible : true,
			localeChain : 'en_US'
		}
	});
	
	
	
	
	function onDocumentLoadedError(errorMessage) {
		$.alert(errorMessage);
	}
	
	
	function onCurrentPageChanged(pagenum,maxPages) {
		waitPanel = new AppFrame.WaitPanel("正在加载资源文件，请稍候...");
		waitPanel.show();
		doRequest(pagenum,maxPages);
		waitPanel.hide();
	}

	
	function doRequest(maxPages,pagenum) {
		var reval = ajaxJs("${path}/docviewer/pdfviewer2swf.action?dirChar=" + dirChar + "&filePath=" + filePath + "&maxPages=" + maxPages +"&thisIndex=" + pagenum);
		if(reval != 1){
			$.alert("转换SWF文件时出错.");
		}
	}
	
	
	function gotoPage(num){
		getDocViewer().gotoPage(num);
	}

	//test
	//doRequest(147,4);
</script>
</html>

