<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>office在线阅读</title>
<script type="text/javascript">  

function showWaitPanel() {
	waitPanel = new AppFrame.WaitPanel("正在加载，请稍候...");
	waitPanel.show();
}

/**
 * 判断字符串是否为空
 * @param _paramValue
 * @returns {Boolean}
 */
function isNotNull(_paramValue){
	if(_paramValue != "undefined" && _paramValue != null && _paramValue != ""){
		return true;
	}
	return false;
}
 
showWaitPanel();
//服务器路径
var basePath = "http://<%=request.getServerName() %>:<%=request.getServerPort()%>${pageContext.request.contextPath}/";
var sysMetaDataTypeId = '<%=request.getParameter("sysMetaDataTypeId") %>'; //元数据id
var filePath = '<%=request.getParameter("filePath") %>'; //office 文件<.doc/.excel/.ppt等>：带有http的文件相对路径 如:http://localhost:8089/bsrcm/swf/doc/1.doc
var type = '<%=request.getParameter("type") %>'; //类型
var id = '<%=request.getParameter("id") %>'; //主键id
if(!isNotNull(filePath)){filePath = "";}
//filePath = "http://localhost:8091/bsrcm_base/common/showIV/出版图书原始资源模板.xls";
//alert("sysMetaDataTypeId：" + sysMetaDataTypeId + " filePath： " + filePath + " type：" +  type +" id：" + id);
var url = "<%=request.getContextPath()%>/commOnlineRead/queryFileUrl.action?sysMetaDataTypeId=" + sysMetaDataTypeId  + "&filePath=" + filePath  + "&id=" + id + "&type=" + type + "&math=" + Math.random();
if(isNotNull(filePath)){
	 document.getElementById('framerControl').Open(filePath, true);
}else{
	if(isNotNull(id) && isNotNull(type)){
	  //异步加载文件 txt/xml
	  $.ajax({  
	         url: url,  //访问的服务器资源---送请求的地址，可以是服务器页面也可以是WebService动作。
	         type:'get',  //请求的类型---GET本身是作为请求URL的一部分进行发送的;而POST请求是作为请求体的一部分进行发送的，这是HTTP协议来决定的  
	        // data:"",//发送请求时，携带的参数---是key:value对形式，如：{name:"grayworm",sex:"male"}，如果是数组{works:["work1","work2"]}
	         async: true ,  //异步
	       	 datatype:"json",//可以是xml、json、html等，默认为html 
	        //发送ajax请求前被触发，如果返回false则取消本次请求。如果异步请求需要显示gif动画，那应当在这里设置相应<img>的可见。
	         beforeSend: function(XMLHttpRequest){
	    		 //
	    	   },
	    	//请求调用完成后的回调函数（请求成功或失败时均调用），如果异步请求显示gif动画，那应当在这里设置相应的<img>不可见。
	    	 complete: function(XMLHttpRequest, textStatus){ 
	    		   //textStatus是描述返回状态的字符串
	         },
	        //请求执行成功时的回调函数
            success: function(data,textStatus){ 
         		   //data是服务端返回的数据可以是xml、json、text等格式
         		   //textStatus是描述返回状态的字符串
         		   if(data != ""){
         			   data=data.substring(0,data.length-2);
         		   }
         		   
         		    var jsonArray = eval(data); 
         		 //   alert(basePath + jsonArray[0].relativePath );
         		    document.getElementById('framerControl').Open( basePath + jsonArray[0].relativePath , true);
         		   waitPanel.hide();

          },
         //请求执行失败时的回调函数 
       	error: function(XMLHttpRequest,textStatus,errorThrown){
       		   //data是服务端返回的数据可以是xml、json、text等格式
       		   //textStatus,errorThrown是描述返回状态的信息
       			alert("加载文件出错！");
       		 	waitPanel.hide();
       	}
	  }); 
   }
}
	 		
</script>
<style>

</style>
</head>
<body>
<div class="con_crumbs" id="bt"  />
<div id="content"> 
	<object id="framerControl" codebase="<%=basePath %>/docviewer/tool/dsoframer.ocx#Version=2.3.0.0" height="550" 
		classid="clsid:00460182-9E5E-11D5-B7C8-B8269041DD57" width="100%">
		<param name="_ExtentX" value="16960">
		<param name="_ExtentY" value="13600">
		<param name="BorderColor" value="-2147483632">
		<param name="BackColor" value="-2147483643">
		<param name="ForeColor" value="-2147483640">
		<param name="TitlebarColor" value="-2147483635">
		<param name="TitlebarTextColor" value="-2147483634">
		<param name="BorderStyle" value="1">
		<param name="Titlebar" value="0">
		<param name="Toolbars" value="0">
		<param name="Menubar" value="1">
		<param name="Caption" value="正在加载中,请稍后.....">
	</object>
</div>
</body>
</html>