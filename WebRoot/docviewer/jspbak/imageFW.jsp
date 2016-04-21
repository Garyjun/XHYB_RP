<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>资源管理平台</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/appframe/jquery/js/jquery.ztree.core-3.0.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/appframe/jquery/js/jquery.ztree.exedit-3.0.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/appframe/jquery/js/jquery.ztree.excheck-3.0.min.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/appframe/jquery/css/zTreeStyle/zTreeStyle.css" type="text/css" />
<%
    String appPath1 = request.getContextPath();
%>
<script type="text/javascript">
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
	var initPanel= "0";
	var initNum = "0";
	
	var id ='<%=request.getAttribute("id") %>';
	if(id == null || id =="" || id=="null")
		id = '<%=request.getParameter("id") %>';
		
    var type ='<%=request.getAttribute("type") %>';
    var filePath ='<%=request.getParameter("filePath") %>';
    if(type == null || type =="" || type=="null")
		type = '<%=request.getParameter("type") %>';
	var url="";		
	//如果Id不为空
	 if(id!= null && id!="" && id!="null"){
		if(type != null && type != ""){
			if(type.substr(0,1) == "o"){
				url="<%=request.getContextPath()%>/comic/showOresImageInfo.action?type=" + type  + "&id=" + id + "&math=" + Math.random();
			}else if(type.substr(0,1) == "b"){
				url="<%=request.getContextPath()%>/comic/showBresImageInfo.action?type=" + type  + "&id=" + id + "&math=" + Math.random();
			}else if(type.substr(0,1) == "p"){
				url="<%=request.getContextPath()%>/comic/showProImageInfo.action?type=" + type  + "&id=" + id + "&math=" + Math.random();
			}else if(type.substr(0,1) == "f"){
				url="<%=request.getContextPath()%>/comic/showFresImageInfo.action?type=" + type  + "&id=" + id + "&math=" + Math.random();
			}			
		}
	 }else if(filePath!=null){ 
		 filePath="<%=path%>"+filePath;
		 this.location="<%=request.getContextPath()%>/common/showIV/image.jsp?filePath=" + filePath + "&math=" + Math.random();
	 }else{
		  alert("提示：资源文件不存在!");
	  }
	
	
	
	
	//组装image对象数组和树节点对象数组
	function doPostAjax(){
		$.ajax({ 
	         type:'get', 
	         url: url,  
	         async: false,
	         datatype:"json",
	         beforeSend: function(XMLHttpRequest){
	    		 //
	    	 },
	         success: function(data){  
	         	var jsonArray = eval('(' + data.substring(0,data.length-2) + ')'); 
	         	
	         	initNum = jsonArray.length;
	         	
	         	var indexCount = -1;
	         	
	         	for(var index =0, len = jsonArray.length;index<len; index++) { 
	         		
	         		//点击轮换图片事件
	         		var eventUrl ="";
	         		
        		    indexCount++;
        		    
        			if(jsonArray[index].url!=null && jsonArray[index].url!=""){
        				
	      				 	//图片URL
	      				 	var filePath = "";
	      				 	if(jsonArray[index].isTxt == "txt"){
	      				 		eventUrl = "loadImageUrl('txt'," + (indexCount + 1) + ",'" + jsonArray[index].url + "','" + jsonArray[index].id +"')";
		       				   	
	      				 	}else{
	      				 	   filePath = "<%=request.getContextPath()%>/" + jsonArray[index].url;
	      				 	eventUrl = "loadImageUrl('single'," + (indexCount + 1) + ",'" + filePath + "','" + jsonArray[index].title +"')";
	      				 	}
		       				 arrObjs.push({image:filePath, title:jsonArray[index].title, thumb:'', url:filePath});
		       				   
        			    }else{
        			    	
        					indexCount--;
        					
        			}
	         			
        	        //树节点数组
        	        if(jsonArray[index].id == "0"){
        	        	zNodes.push({id:jsonArray[index].id, pId:jsonArray[index].pId, name:jsonArray[index].title,open:jsonArray[index].isOpen,iconSkin:jsonArray[index].iconSkin, click:eventUrl});
        	        }else{
        	        	zNodes.push({id:jsonArray[index].id, pId:jsonArray[index].pId, name:jsonArray[index].title,open:jsonArray[index].isOpen,iconSkin:jsonArray[index].iconSkin, click:eventUrl});
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
			      	document.getElementById("jspUrl").src=imageJspUrl; 
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
	
	         
     $(document.body).ready(function(){
    	showWaitPanel();
    	if(url != null && url !=""){
    		setTimeout('doPostAjax()', 1);
    	}
     });
      
     
    //点击树节点事件:展示图片
    function loadImageUrl(type,sortIndex,url,title){
    	if(type=='txt'){
 //   		document.getElementById("jspUrl").src='<%=appPath1%>'+'/bres/readSingleFile.action?bresFile.bresFileId='+title;
  //  		document.getElementById("jspUrl").src='<%=appPath1%>'+'/comic/readSingleFile.action?filePath='+url;
    		document.getElementById("jspUrl").src='<%=request.getContextPath()%>/common/showIV/txtOrXml.jsp?filePath='+url;
    	}else{
    		document.getElementById("jspUrl").src= imageJspUrl + "?filePath=" + url + "&imgName=" + title;
    		startSlide = sortIndex;
    	}
		
   }
     
    function showWaitPanel() {
		waitPanel = new AppFrame.WaitPanel("正在加载，请稍候...");
		waitPanel.show();
	}
    
</script>

 <style type="text/css">
	   ul.ztree {margin-top: 10px;border: 1px solid #617775;background: #f0f6e4;width:220px;height:550px;overflow-y:scroll;overflow-x:auto;}
</style>
<base target="_self" />
</head>
<body class="yui-skin-sam" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	  <div id="box" class="box" style="height: 580px;" id="image">
		  <div class="con_L_box" align="left" id="canLeft" style="height:560px;float: left;">
		        <div class="con_L" style="height: 30px;">
					<div class="con_L_title">菜单导航</div>
				</div>
				     <ul id="treeView" class="ztree"></ul>
		  </div>
		<s:action name="gotoBar" namespace="/resclass" executeResult="true" />
		<div class="con_R" id="canRight" >
			<iframe id="jspUrl" style="height: 590px;width:100%"  scrolling="no" frameborder="0"></iframe>
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