<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.net.URLEncoder" import="java.net.URLDecoder"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>PDF在线预览</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1"></meta>
<script type="text/javascript" src="${path}/appframe/plugin/flexPaper/flexpaper_flash.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css" />
<script type="text/javascript" src="${path}/appframe/plugin/jqueryJson/jquery.json-2.4.min.js"></script>

<script type="text/javascript">
	//含中文URL
	var filePath = '<%=request.getParameter("filePath")%>';
	//转义后的URL
	<% String filePathDecode = URLDecoder.decode(request.getParameter("filePath"), "UTF-8"); %>
	var filePathDec = '<%=filePathDecode%>';
	//alert(filePathDec);
	var fileType ='<%=request.getParameter("fileType")%>';
	var id ='<%=request.getParameter("id")%>';
	//加载swf控件
	var titleZh ='<%=request.getParameter("fileZhName")%>';
	var title ='<%=request.getParameter("fileName")%>';
	var dirPath = viewerDirPath(filePathDec);
	var basePath ="${basePath}";
	//alert(basePath);
	//异步检查文件是否存在
	$(document).ready(function(){
		var param = "?filePath=" + filePath + "&math=" + Math.random()+"&id="+id;
		$.ajax({               
	        type: "get",      
	        async:false,//同步
	        dataType: "json",               
	        url: encodeURI(gotoCheckFileExitUrl + param), 
	        success: function(data) {
	        	if(data != null){
	        		if(isNotNull(data.noteNum) && data.noteNum != 2 && data.noteNum != 4){
	        			$('#errorHtml').html(errorHtml(data.noteInfo));
	        		}else{
	        			var fp = new FlexPaperViewer(	
	    					   "${path}/appframe/plugin/flexPaper/WebReader",
	    					   "viewerPlaceHolder", { config : {
	    					   		SwfFile : basePath  + "fileDir/converFileRoot/" + data.relfilePath + "/[*,0].swf",
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
	    							rootUrl:basePath,
	    							title:titleZh,
	    							bookid:id,
	    							filePath:filePathDec,
	    							fileType:fileType,
	    							dirChar:"",
	    							localeChain : 'en_US'
	    						  }
	    					 });
	    		        }
			       }
	           }
		  });
	 });
		
	function onDocumentLoadedError(errorMessage) {
		$.alert(errorMessage);
	}
	
	function onCurrentPageChanged(maxPages,pagenum) {
		doRequest(maxPages,pagenum);
	}
	
	function doRequest(maxPages,pagenum) {
		
		var reval = ajaxGet("${path}/docviewer/pdfviewer2swf.action?dirChar=11&filePath=" + filePath + "&maxPages=" + maxPages +"&thisIndex=" + pagenum);
		if(reval != 1){
			//$.alert("转换SWF文件时出错.");
			return;
		}
	}
	
	function gotoPage(pagenum){
		getDocViewer().gotoPage(pagenum);
	}
	
</script>
</head>
<body id="errorHtml">
	<a id="viewerPlaceHolder" style="width: 100%; height: 100%; display: block"></a>
</body>
</html>

