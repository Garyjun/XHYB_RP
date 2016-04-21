<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<head>
<title>pdf在线阅读</title>
<script type="text/javascript">  

<%
    String filePath = request.getParameter("filePath");   //文件相对路径
	
    String path = "http://"+ request.getServerName() +":" + request.getServerPort() + request.getContextPath()+"/";
    
	//filePath  =  "common/showIV/1.doc"; //测试
	
	filePath = path+filePath;
	
%>

//判断reader是否存在，不存在就弹出下载框
function isAcrobatPluginInstall() { 
 var flag = false; 
 // 如果是firefox浏览器 
 if (navigator.plugins && navigator.plugins.length) { 
    for (x = 0; x < navigator.plugins.length; x++) {
	if (navigator.plugins[x].name == 'Adobe Acrobat') 
		flag = true; 
	 } 
 } 
 // 下面代码都是处理IE浏览器的情况 
 else if (window.ActiveXObject) { 
	 for (x = 2; x < 10; x++) { 
	 try { 
		 oAcro = eval("new ActiveXObject('PDF.PdfCtrl." + x + "');"); 
		 if (oAcro) { 
			flag = true; 
		 } 
	 } catch (e) { 
		flag = false; 
	 } 
 } 
 try { 
	 oAcro4 = new ActiveXObject('PDF.PdfCtrl.1'); 
	 if (oAcro4) 
		flag = true; 
	 } catch (e) { 
		flag = false; 
 } 
 try { 
	 oAcro7 = new ActiveXObject('AcroPDF.PDF.1'); 
	 if (oAcro7) 
		flag = true; 
	 } catch (e) { 
		flag = false; 
	 } 
 } 
 if (flag) { 
	return true;

 } else { 
		alert("对不起,您还没有安装PDF阅读器软件呢,为了方便预览PDF文档,请选择安装！"); 
		location = 'http://ardownload.adobe.com/pub/adobe/reader/win/10.x/10.1.0/zh_CN/AdbeRdr1010_zh_CN.exe'; 
 } 
	return flag; 
 }
 
</script>
<style type="text/css">
	
</style>
</head>
<body>
<div style="align:center"> 
<object id="adobeReaderInfo" classid="clsid:CA8A9780-280D-11CF-A24D-444553540000"  
    codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab"  name="PDF1"  width="100%"  height="550"  border="0">
	<param name="_Version" value="65539">   
	<param name="_ExtentX" value="20108">   
	<param name="_ExtentY" value="10866">   
	<param name="_StockProps" value="0">        
	<param name="SRC" value="<%=filePath%>"/>     
</object>     
</div>  
<script  language="JavaScript" type="text/javascript">  
	isAcrobatPluginInstall();
</script>     
</body>  
</html>