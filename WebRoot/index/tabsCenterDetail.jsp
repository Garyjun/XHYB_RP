<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.brainsoon.system.support.SystemConstants.ResearchPlatResType" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/appframe/common.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>展示详细页面</title>
<script type="text/javascript" src="../appframe/plugin/flexPaper/flexpaper_flash.js"></script>
<!-- Annotator  -->
<link rel="gettext" type="application/x-po" href="../appframe/plugin/annotator/locale/zh_CN/annotator.po">
<script src="../appframe/plugin/annotator/lib/vendor/gettext.js"></script>

<script type="text/javascript" src="../appframe/plugin/annotator/annotator-full.min.js"></script>
<link rel="stylesheet" href="../appframe/plugin/annotator/annotator.min.css">
<script type="text/javascript" src="js/annotator.js"></script>

<script type="text/javascript">
		$(function(){
			var resId = $("#resId").val();
			var resType = $("#resType").val();
			var url = $("#url").val();
			if(resType!='4'){
				$.ajax({
					type:'GET',
					url:'${path}/resCenterText/detail.action',
					data: "resId="+resId+"&resType="+resType,
					success:function(data){
						var json = JSON.parse(data);
						/* var tabsTitle = "<li style='width:20%;'><a href=\"#tabs-"+resId+"\" >"+json.title+"</a><span style='float:right;' class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";
						var tabsText = "<div id=\"tabs-"+resId+"\" style='height:100%;'><p>"+json.tabsDiv+"</p></div>";
						tabsul.append(tabsTitle);
						tabsdiv.append(tabsText);
						setLiWidth();
						tabs.tabs("refresh"); */
						//立即打开当前添加的选项卡
						/* var idx = $("#center_area a[href=\"#tabs-"+resId+"]").parent().index();
						tabs.tabs( "option", "active" ,idx); */
						//$(".tab_wrap ul.tab_list li a").text(json.title);
						//$("#tabsdiv div p").append(json.tabsDiv);
						$("#div1").append(json.tabsDiv);
						//打开pdf文件
						if(resType==<%=ResearchPlatResType.TYPE0%>){
							readFile(json.filePath, json.fileObjectId, json.fileType, json.fileZhName, json.filePathPdf);
						}
				        
						$('#div1').annotator().annotator('addPlugin', 'Store',{
							prefix : '${path}/api',
							urls : {
								create  :  '/annotations/create.action',
								update  :  '/annotations/update.action',
								destroy :  '/annotations/delete.action?:id',
								search  :  '/annotations/search.action'
							},
							annotationData : {
								'uri'    : resId,
								'resType': resType,
								'resId'  : resId
							},
							loadFromSearch : {
								'limit': 100,
								'uri'  : resId
							}
						});
					}
					 
				});
			}else{
				location.href = url;
			}
			document.onkeydown = function(e){
				var keyCode = e.keyCode || e.which || e.charCode;
			    var altKey = e.altKey;
			    if(altKey && keyCode==81){
			    	parent.chionkeyclosetabs();
			    }
			    //parent.$('#center_area').$.layout.keyDown(e);
			}
		});
		//打开pdf
		function readFile(filePath, fileObjectId, fileType, fileZhName,filePathPdf){
			if(!isNotNull(fileObjectId) && !isNotNull(filePath)){
				$.alert(showErrInfo + "【路径不存在，请检查！】");
				return;
			}
			initPDF(filePath,fileType,fileObjectId,filePathPdf);
		}
		function initPDF(filePath,fileType,id,filePathPdf){
			var basePath ="${basePath}";
			var fp = new FlexPaperViewer(	
		   "${path}/appframe/plugin/flexPaper/WebReader",
		   "viewerPlaceHolder"+id, { config : {
		   		SwfFile : basePath+"fileDir/fileRoot/" + filePath + "/[*,0].swf",
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
				title:'123',
				bookid:id,
				filePath:filePathPdf,
				fileType:fileType,
				dirChar:"",
				localeChain : 'en_US'
			  }
		 });
		}

</script>
</head>
<body>
	<div id="center_area" style="width: 100%;height: 100%;">
		<div  id= "div1" style="height: 100%;padding-left: 10px;padding-right: 10px;">
			
		</div>
	</div>
	<input type="hidden" id="resId" value="${param.resId }"/>
	<input type="hidden" id="resType" value="${param.resType }"/>
	<input type="hidden" id="url" value="${param.url }"/>
</body>
</html>