<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>资源管理平台</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/appframe/jquery/js/jquery.ztree.core-3.0.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/appframe/jquery/js/jquery.ztree.exedit-3.0.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/appframe/jquery/js/jquery.ztree.excheck-3.0.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/common/showIV/readType.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/appframe/jquery/css/zTreeStyle/zTreeStyle.css" type="text/css" />
<%
    String appPath1 = request.getContextPath();
%>
<script type="text/javascript">
var videoJspUrl = '<%=request.getContextPath()%>/common/showIV/video.jsp';    //视频
var imageJspUrl = '<%=request.getContextPath()%>/common/showIV/image.jsp';   //图片

    var setting = {
		data: {
			simpleData: {
				enable: true
			}
		}
	};
    
    
	var zNodes = new Array();
	var arrObjs = new Array(); //定义总的图片路径数组
	var startSlide = "1"; //定义导航树的图片索引,默认图片展示从 1 开始
	//初始化
	var initSrcUrl = "";
	var initWidth = "";
	var initHeight = "";
	var initPanel= "0";
	var initNum = "0";
	
	var id ='<%=request.getAttribute("id") %>';
	if(id == null || id =="" || id=="null")
		id = '<%=request.getParameter("id") %>';
	
    var type ='<%=request.getAttribute("type") %>';
    if(type == null || type =="" || type=="null")
		type = '<%=request.getParameter("type") %>';
		
	var filePath ='<%=request.getAttribute("filePath") %>';
	
	if(filePath == null || filePath =="" || filePath=="null")
		filePath = '<%=request.getParameter("filePath") %>';
		
	 var url="";	
      //如果Id不为空
	  if(id!= null && id!="" && id!="null"){
		  if(type != null && type != ""){
				if(type.substr(0,1) == "o"){
					url="<%=request.getContextPath()%>/comic/showOresVideoInfo.action?type=" + type  + "&id=" + id + "&math=" + Math.random();
				}else if(type.substr(0,1) == "b"){
					url="<%=request.getContextPath()%>/comic/showBresVideoInfo.action?type=" + type  + "&id=" + id + "&math=" + Math.random();
				}else if(type.substr(0,1) == "p"){
					url="<%=request.getContextPath()%>/comic/showProVideoInfo.action?type=" + type  + "&id=" + id + "&math=" + Math.random();
				}else if(type.substr(0,1) == "f"){
					url="<%=request.getContextPath()%>/comic/showFresVideoInfo.action?type=" + type  + "&id=" + id + "&math=" + Math.random();
				}
			}
	  }else if(filePath!=null){ 
			 this.location="<%=request.getContextPath()%>/common/showIV/video.jsp?filePath=" + filePath + "&math=" + Math.random();
	  }else{
			  alert("提示：资源文件不存在!");
	  }
	  
      
      
	  //组装image对象数组和树节点对象数组
		function doPostAjax(){  
		      		   
		    $.ajax({  
		         url: url,  
		         type:'get',  
		         async: false ,  
		       	 datatype:"json",
	  	         beforeSend: function(XMLHttpRequest){
	  	    		 //
	  	    	 },
		         success: function(data){  

		        	 var jsonArray = eval('(' + data.substring(0,data.length-2) + ')');  
		     		
		         	initNum = jsonArray.length; 
		         	
		         	var indexCount = -1;  //定义视频的索引
		            var indexImgCount = -1; //定义图片的索引
		         	
		         	for(var index =0, len = jsonArray.length;index<len; index++) { 
		         		
		         		var eventUrl = "";
		         		
		         		 //拼装视频播放点击事件
		         		 indexCount++;
		         		if(jsonArray[index].url!=null && jsonArray[index].url!=""){
		         			 
		         			    //视频URL
		         			    var videoUrl = "";
		         			    
		         			   var videoUrl0 = "";
		         			    
		       				 	if(jsonArray[index].url != null || jsonArray[index].url != ""){
		       				 		
		       				 	    videoUrl = "<%=request.getContextPath()%>/" + jsonArray[index].url; 
		       				 	    
		       				 	    videoUrl0 = jsonArray[index].url; 
		       				 	     alert(videoUrl);
			       				 	 if(jsonArray[index].isTxt=='txt'){
			       				 		eventUrl = "onloadVideoUrl('txt','" +  jsonArray[index].url + "'," + jsonArray[index].width + "," + jsonArray[index].height +",'" + jsonArray[index].isConverSuccess +"')";
			       				 	 }else{
			       				 		eventUrl = "onloadVideoUrl('single','" +  jsonArray[index].url + "'," + jsonArray[index].width + "," + jsonArray[index].height +",'" + jsonArray[index].isConverSuccess +"')";
			       				 	 }
		       				 	 
		       				 	     
		       				 	}
		       				 	     
			  		         		 
			  		         		 
			  		         	       //第一次初始化视频自动播放
			  		         	       if((initSrcUrl == null || initSrcUrl=="")  && (videoUrl != null && videoUrl !="")){
			  		         	    	    initSrcUrl = videoUrl;
			  		         	    	    if(!isImg(videoUrl)){
				  		         	    		$.ajax({  
				  		           		         url: '<%=request.getContextPath()%>/comic/getVideoWidthAndHeight?videoPath='+ videoUrl0  +'&math=' + Math.random(),  
				  		           		         type:'get',  
				  		           		         async: false ,  
				  		           		       	 datatype:"json",
				  		           		         success: function(data){ 
				  		           		        	var widthAndHeight = eval('(' + data.substring(0,data.length-2) + ')');
				  		           		     	  initWidth = widthAndHeight[0].width;
				  		           		 		  initHeight = widthAndHeight[1].height;
				  		 	   	       		     },
				  		 	   	 	          	 error: function(){
				  		 	   	 	          		alert("获取数据出错！");
				  		 	   	 	          	 }
				  		    	 		     	}); 
			  		         	    	    }
			  		         	       }
			  		         	
			  		         	 //如果是图片，设置图片的数据结构
			  		         	 if(isImg(videoUrl)){
			  		         		    indexImgCount ++;
			  		         			arrObjs.push({image:videoUrl, title:jsonArray[index].title, thumb:'', url:videoUrl});
			  		         			eventUrl = "loadImageUrl('single'," + (indexImgCount + 1) + ",'" + videoUrl + "','" + jsonArray[index].title +"')";
			  		         	 }
		       				    
		  		         	}else{
		  		         		
		         				indexCount--;
		         				
		         			}
		         			
		         			//树节点数组
		         			if(jsonArray[index].id == "0"){
	  		         	    	zNodes.push({id:jsonArray[index].id, pId:jsonArray[index].pId, name:jsonArray[index].title,open:jsonArray[index].isOpen,iconSkin:jsonArray[index].iconSkin,click:eventUrl}); 
		         			}else{
		         				zNodes.push({id:jsonArray[index].id, pId:jsonArray[index].pId, name:jsonArray[index].title,open:jsonArray[index].isOpen,iconSkin:jsonArray[index].iconSkin,click:eventUrl}); 
		         			}
		         	}
		         	
		         	$(".box").show();
		         	
			     	if(initNum == "0"){
			     		$(".con_L_box").hide();
			     		$("#canMiddle").hide();
			     		alert("提示：资源文件不存在!");
			     		window.close();
			     	}else if(initNum == "1"){
			     		 $(".con_L_box").hide();
			     		 $("#canMiddle").hide();
			     	}else{
			     		$.fn.zTree.init($("#treeView"), setting, zNodes);
			     	}
			   
			     	//如果第一张是图片，那么默认进入图片浏览的控件
		         	 if(isImg(initSrcUrl)){
		         			document.getElementById("jspUrl").src= imageJspUrl; 
		         	 }else{//否则进入视频浏览
		         	 	if(jsonArray[0].isConverSuccess =='2'){
		        			alert('正在处理视频，请稍后重试！');
		        			closeWin(initNum);
		         		}else if(jsonArray[0].isConverSuccess =='4'){
		         			alert('正在处理视频，请稍后重试！');
		         			closeWin(initNum);
		         		}else{
		         			document.getElementById("jspUrl").src= videoJspUrl + "?filePath=" + initSrcUrl + "&width=" + initWidth+ "&height=" + initHeight;
		         		}
		         	}
			      	waitPanel.hide();
		         },
	  		     complete: function(XMLHttpRequest, textStatus){
	  	        	 //
	  	         },
	          	error: function(){
	          		alert("获取数据出错！");
	          	}
		     });  
	      }
	  
  	      
  	//初始化树结构
	 $(document).ready(function(){
		 showWaitPanel();
		 if(url != null && url !=""){
	    		setTimeout('doPostAjax()', 1);
	    	}
     });
	
	
	//点击树节点事件:展示视频
	function onloadVideoUrl(type,srcUrl,width,height,isConverSuccess){
		//如果是图片，那么默认进入图片浏览的控件
     	 if(isImg(srcUrl)){
     			document.getElementById("jspUrl").src= imageJspUrl; 
     	 }else{//否则进入视频浏览
     		 
     		if(type=='txt'){
     			document.getElementById("jspUrl").src='<%=request.getContextPath()%>/common/showIV/txtOrXml.jsp?filePath='+srcUrl;
        	}else{
        		if(isConverSuccess =='2'){
        			alert('正在处理视频，请稍后重试！');
        			closeWin(initNum);
         		}else if(isConverSuccess =='4'){
         			alert('正在处理视频，请稍后重试！');
         			closeWin(initNum);
         		}else{
         			$.ajax({  
          		         url: '<%=request.getContextPath()%>/comic/getVideoWidthAndHeight?videoPath='+srcUrl+'&math=' + Math.random(),  
          		         type:'get',  
          		         async: false ,  
          		       	 datatype:"json",
          		         success: function(data){ 
          		        	var widthAndHeight = eval('(' + data.substring(0,data.length-2) + ')');
          		        	width = widthAndHeight[0].width;
          		        	height = widthAndHeight[1].height;
	   	       		     },
	   	 	          	 error: function(){
	   	 	          		alert("获取数据出错！");
	   	 	          	 }
   	 		     	}); 
            		document.getElementById("jspUrl").src = videoJspUrl + "?filePath=" + "<%=request.getContextPath()%>/"+srcUrl + "&width=" + width+ "&height=" + height;
         		}
        	}
     	  }
	}
	
	 //点击树节点事件:展示图片
    function loadImageUrl(type,sortIndex,url,title){
		document.getElementById("jspUrl").src= imageJspUrl + "?filePath=" + url + "&imgName=" + title;
		startSlide = sortIndex;
   }
	
	function showWaitPanel() {
		waitPanel = new AppFrame.WaitPanel("正在加载，请稍候...");
		waitPanel.show();
	}
	
	function closeWin(initNum){
		if(initNum == "" || initNum == "1"){
			window.close();
		}
	}
	
</script>

 <style type="text/css">
	   ul.ztree {margin-top: 10px;border: 1px solid #617775;background: #f0f6e4;width:220px;height:550px;overflow-y:scroll;overflow-x:auto;}
</style>
<base target="_self" />
</head>
<body class="yui-skin-sam" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	  <div id="box" class="box" style="height: 580px;">
		  <div class="con_L_box" align="left" id="canLeft" style="height:560px;float: left;">
		        <div class="con_L" style="height: 30px;">
					<div class="con_L_title">菜单导航</div>
				</div>
				     <ul id="treeView" class="ztree"></ul>
		  </div>
		<s:action name="gotoBar" namespace="/resclass" executeResult="true" />
		<div class="con_R" id="canRight" >
			<iframe id="jspUrl" style="height: 560px;width:100%"  scrolling="no" frameborder="0"></iframe>
		</div>
	</div>
</body>
<script>
$("#box").hide();
if(url == null || url ==""){
	window.close();
}
</script>
</html>