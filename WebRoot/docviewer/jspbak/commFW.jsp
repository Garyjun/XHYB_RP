<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<head>
<title>在线阅读</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/common/showIV/readType.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/appframe/jquery/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript">  
var videoJspUrl = '<%=request.getContextPath()%>/common/showIV/video.jsp';    //视频
var imageJspUrl = '<%=request.getContextPath()%>/common/showIV/image.jsp';   //图片
var officeJspUrl = '<%=request.getContextPath()%>/common/showIV/office.jsp';   //office
var pdfJspUrl = '<%=request.getContextPath()%>/common/showIV/pdf.jsp';    //pdf
var txtOrXmlJspUrl = '<%=request.getContextPath()%>/common/showIV/txtOrXml.jsp';    //txt/xml

<%
    //String path = request.getParameter("filePath");   //文件相对路径
	String path = "http://" + request.getServerName()  + ":" + request.getServerPort() +  request.getContextPath() + "/"; 
    
	//filePath  =  "common/showIV/1.doc"; //测试
	
	//path += filePath;
	
	//path = "D:/project/BSRDPS/WebRoot/common/showIV/1.txt";   //测试--- txt/xml

%>
	
	var taskId ='<%=request.getParameter("taskId") %>'; //任务Id 
	var url = "<%=request.getContextPath()%>/commOnlineRead/showAllOptionsByTaskId.action?taskId=" + taskId;
	if(taskId != null && taskId != ""){   		   
		    $.ajax({  
		         url: url,  
		         type:'get',  
		         async: true,  //异步
		         datatype:"json", //可以是xml、json、html等，默认为html 
	  	         beforeSend: function(XMLHttpRequest){
	  	        	//showWaitPanel();
	  	    	 },
		         success: function(data){
		        	  if(data != ""){
		        		  data=data.substring(0,data.length-2);
		        		  if(data.indexOf("||||") != -1){
		        			  var zy = data.split("||||");
		        			  $("#hiddenSczy").val(zy[0]);
		             		  $("#hiddenZjzy").val(zy[1]);
		             		  data = replaceAll(data,"||||","");
		             		  $("#hiddenAllZy").val(data);
		        		  }
           		      }
           		   alert(data);
           		   //$("#optionItem").text(data);
           		    $('#fileUrl').append(data);
           		    $("#fileUrl ").get(0).selectedIndex=0;  //设置Select索引值为0的项选中
           		   //document.getElementById("optionItem").innerHTML =data;
           		  
           		   
		           //waitPanel.hide();
		         },
	            //请求执行失败时的回调函数 
	          	error: function(XMLHttpRequest,textStatus,errorThrown){
	          		   //data是服务端返回的数据可以是xml、json、text等格式
	          		   //textStatus,errorThrown是描述返回状态的信息
	          			alert("加载出错！");
	          	}
			  }); 
	      }else{
	    	  alert("任务Id不能为空！");
	      }
   
   
   //根据文件名称跳转到相应的jsp阅读页面
   function gotoJsp(filePath){
	 if(filePath != null && filePath != ""){
         if(isPdf(filePath)){     //如果是pdf     
        	 document.getElementById("commJspUrl").src= pdfJspUrl + "?filePath=<%=path%>"+filePath;  //文件相对路径
		 }else if(isTxtOrXml(filePath)){ //如果是txt/xml
			 document.getElementById("commJspUrl").src= txtOrXmlJspUrl + "?filePath="+filePath;  //文件绝对路径
		 }else if(isOffice(filePath)){    //如果是office
			 document.getElementById("commJspUrl").src= officeJspUrl + "?filePath=<%=path%>"+filePath;  //文件相对路径
		 }else if(isImg(filePath)){    //如果是图片
			 document.getElementById("commJspUrl").src= imageJspUrl + "?filePath=<%=path%>"+filePath;  //文件相对路径
		 }else if(isVedio(filePath)){    //如果是视频
			 document.getElementById("commJspUrl").src= videoJspUrl + "?filePath=<%=path%>"+filePath;   //文件相对路径
		 }else{  
			 alert("SORRY! 暂不支持该格式！");
		 }
	  }
   }
	
   
   //选择资源
   function selectZy(){
	   var selectZy = $("#selectZy").val();
	   var hiddenSczy = $("#hiddenSczy").val();
	   var hiddenZjzy = $("#hiddenZjzy").val();
	   var hiddenAllZy = $("#hiddenAllZy").val();
	   if(hiddenSczy == ""){
		   hiddenSczy = "暂无数据";
	   }
	   if(hiddenZjzy == ""){
		   hiddenZjzy = "暂无数据";
	   }
	   if(hiddenAllZy == ""){
		   hiddenAllZy = "暂无数据";
	   }
	   var options=$('#fileUrl optgroup').remove();
	   var options=$('#fileUrl option').remove();
	   if(selectZy=="0"){  //所在资源
		   $('#fileUrl').append(hiddenAllZy);
	   }else  if(selectZy=="1"){ //素材资源
		   $('#fileUrl').append(hiddenSczy);
	   }else  if(selectZy=="2"){  //中间资源
		   $('#fileUrl').append(hiddenZjzy);
	   }
	   $("#fileUrl ").get(0).selectedIndex=0;  //设置Select索引值为0的项选中
   }
   
   function openFileByUrl(){
	   var filePath = $("#fileUrl").val();
	   filePath = replaceAll(filePath,"\\","/");
	   if(filePath != null && filePath !=""){
		   gotoJsp(filePath);
	   }else{
		  alert("请点击要查看的项！");
	   }
   }
 //正在加载，请稍候..
</script>
<style type="text/css">
	
</style>
</head>
<body>
<input type="hidden"  id="hiddenSczy" name="hiddenSczy" />
<input type="hidden"  id="hiddenZjzy" name="hiddenZjzy" />
<input type="hidden"  id="hiddenAllZy" name="hiddenAllZy" />
<div style="align:center">
      <table style="height: 570px;width:100%">
          <tr>
               <td>
	                <iframe id="commJspUrl"  style="height: 560px;width:100%"  scrolling="no" frameborder="0" >加载中...</iframe>
               </td>
               <td>
                      文件列表：<br />
                      <select name="selectZy"  id="selectZy" style="width:150px;" onclick="selectZy()">
                          <option value="0">---全部---</option>
	                      <option value="1">素材资源</option>
	                      <option value="2">中间资源</option>
                      </select><br />
	                  <select name="fileUrl"  id="fileUrl" size="8"  style="width:150px;height:500px;" onclick="openFileByUrl()" />
               </td>
          </tr>
      </table>
  	 
</div>  
<script  language="JavaScript" type="text/javascript">  
    
</script>     
</body>  
</html>