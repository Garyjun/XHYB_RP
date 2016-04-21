<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>音视频在线预览</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge"></meta>
<meta name="viewport" content="width=device-width, initial-scale=1"></meta>
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css" />
<script type="text/javascript" src="${path}/appframe/plugin/jqueryJson/jquery.json-2.4.min.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/StrobeMediaPlayback/swfobject.js"></script>
<script type="text/javascript">  	
    //获取参数 
	var width = "";
	var height = "";
	var filePath = '<%=request.getParameter("filePath") %>';  //相对路径
	var mark = '<%=request.getParameter("mark") %>';  //标识：用于判断逻辑 1:为不参与校验
	var id ='<%=request.getParameter("id")%>';
	var url = httpBasePath + "videoviewer.action"; //action
	var tempFilePath = "";
	$(document).ready(function(){
  		if(isNotNull(filePath) || isNotNull(id)){
  			//var dirPath = viewerDirPath(filePath); //如果不用获取视频的宽高的话，则不用执行ajax,同时需要加上fileDir/fileRoot or converFlieRoot 目录
  			var param = "?filePath=" +  filePath + "&id=" + id + "&math=" + Math.random();
  			//第一步校验文件是否存在
  			var data =  ajaxGet(gotoCheckFileExitUrl + param);//执行ajax
  			var jsonArray = eval('(' + data + ')'); 
			if(jsonArray.noteNum != 2 && jsonArray.noteNum != 4){
  				$('#errorHtml').html(errorHtml(jsonArray.noteInfo));
  			}else{
  				if(isNotNull(mark) && mark == 1){
  					tempFilePath = filePath;
	  			}else{
	  				tempFilePath =  ajaxGet(url + param);//执行ajax、获取文件路径---暂时只实现了单个视频预览
	  			}
	        	if(isNotNull(tempFilePath)){
	        		var reVal0 = tempFilePath.split("|");
	        		tempFilePath = "${basePath}" + reVal0[0];
	        		if(isNotNull(reVal0[1])){
	        			var reVal1 = reVal0[1].split(",");
	        			width = reVal1[0];
	        			height = reVal1[1];
	        		}
	        		//console.log("tempFilePath:" + tempFilePath + "   width:" +width + "   height:" + height);
	        		setWH();//设置宽高
	        		//console.log("tempFilePath:" + tempFilePath + "   width:" +width + "   height:" + height);
	        		//初始化参数
	        		var parameters ={	
	        				//视频地址
	        	    		src: tempFilePath,
	        	    		//是否自动隐藏工具栏,
	        	    		controlBarAutoHide: false,
	        	    		//是否显示播放结束按钮
	        	    		playButtonOverlay: true,
	        	    		//控制栏位置$$$
	        	        	controlBarPosition: "bottom",
	        	        	//是否循环播放
	        				loop: false,
	        				//是否自动播放
	        				autoPlay:true,
	        				//其他样式:  none/stretch/zoom/letterbox
	        				scaleMode: "stretch",
	        				enableStageVideo :"false",
	        				autoRewind:"true",
	        				//其他样式:  docked/none
	        				controlBarMode:"docked",  
	        				//其他流格式:   live/recorded/dvr
	        				streamType:"liveOrRecorded", 
	        				//不显示详细信息$$$
	        				verbose : "true",
	        				//是否显示缓冲中字样$$$$
	        				bufferingOverlay : "true"
	        			};
	        		var params = {
	        				quality : "high",
	        				bgcolor : "#000000",
	        				allowscriptaccess : "sameDomain",
	        				allowfullscreen	: "true",
	        				wmode:"Opaque",
	        				
	        		};
	        		var attributes = {
	        				id : "strobeMediaPlayback",
	        				name : "strobeMediaPlayback",
	        				align : "middle"
	        		};
	        		//===================视频参数设置=======================
	        		swfobject.embedSWF(
	        			"${path}/appframe/plugin/StrobeMediaPlayback/StrobeMediaPlayback.swf",
	        			"strobeMediaPlayback",
	        			width,
	        			height,
	        			"10.1.0",
	        			{},
	        			parameters,
	        			params,
	        			attributes
	        	  );
	        		//swfobject.createCSS("#strobeMediaPlayback", "display:block;text-align:left;");
	        	}else{
	        		$("#strobeMediaPlayback").html("<font color=red>NOTE：【路径不存在，无法预览】</font>");
	        		return;
	        	}
  			}
  		}else{
  			$("#strobeMediaPlayback").html("<font color=red>NOTE：【路径不存在，无法预览】</font>");
  			return;
  		}   
	})
	 	
	
	
	//计算视频的宽度和高度
	function setWH(){
 		/* var per = "";//压缩比例
 		var proNum = 33; //默认大小
 		var  windowWidth = $(window).width(); //浏览器当前窗口可视区域宽度   
 		var  windowHeight = $(window).height(); //浏览器当前窗口可视区域高度  
		
 		if(windowWidth == 0){//如果没有获取到宽度，则为880
 			windowWidth = 880;
 		}
		
 		if(windowHeight == 0){//如果没有获取到高度，则为530
 			windowHeight = 530;
 		}
 		height= parseInt(height) + parseInt(proNum);
 		 //计算宽高比例
         if(width > height){//如果大出的宽比大出的高要大
 	   		per =height/width;  //计算出宽的压缩比例
 		}else{//否则要小
 			per =width/height;  //计算出宽的压缩比例
 		}
		
         if(width != null && width != "" && width!= "null"&& width != "undefined" && width !="0"){
 				if(height != null && height != "" && height!= "null"&& height != "undefined" && height !="0"){
 					if(width > windowWidth && height > windowHeight ){//如果两者都比现窗口大
 						if((parseInt(width)-parseInt(windowWidth)) > (parseInt(height)-parseInt(windowHeight))){//如果大出的宽比大出的高要大
 								width = windowWidth;  //最后的宽度
 								height = parseInt(height)-parseInt((parseInt(width)-parseInt(windowWidth))*per);  //最后的高度
 							}else{//否则要小
 								width =  parseInt(width)-parseInt((parseInt(height)-parseInt(windowHeight))*per);  //最后的宽度
 								height = windowHeight; //最后的高度
 							}
 						}else if(width > windowWidth && height < windowHeight ){//如果宽比较大，高比较小
 							width = windowWidth;  //最后的宽度
 							height = parseInt(height)-parseInt((parseInt(width)-parseInt(windowWidth))*per);  //最后的高度
 						}else if(width < windowWidth && height > windowHeight ){//如果宽比较小，高比较大
 							width =  parseInt(width)-parseInt((parseInt(height)-parseInt(windowHeight))*per);  //最后的宽度
 							height = windowHeight; //最后的高度
 						}
 					}else{
 						height = windowHeight;
 						width = windowWidth;
 					}
 			}else{
 				height = windowHeight;
 				width = windowWidth;
 			}  */
		height = 540;
		width = 890;
		}
	
	
</script>
<style>
html,body {
 margin:0;
 text-align:center;
 height:100%;
} 
content {
  position:absolute;
  top:50%;
  writing-mode: tb-rl;
  vertical-align: middle;
}
</style>
</head>
<body id="errorHtml">
		<div id="strobeMediaPlayback" >
	      <p><b>加载中,请稍后......</b></p>
	    </div>
</body>
</html>