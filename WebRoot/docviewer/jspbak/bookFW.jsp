<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>图书在线阅读平台</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/appframe/jquery/js/jquery-1.4.4.min.js"></script>
<%

    String appPath = request.getContextPath();
    String readActionUrl = request.getParameter("readActionUrl");
    String readResType = request.getParameter("readResType");
    
%>
<script type="text/javascript">
   
	//阅读类型
    //原始资源：ores
    //基础资源：fres
    //标准资源：bres
    //应用资源：prod
	var readResType = '<%=readResType%>';
	//URL
    var readActionUrl = '<%=readActionUrl%>';
    
    
	if(readResType != null  && readResType != ""){
		if(readResType == 'ores'){
			readResType = "原始资源";
		}else if(readResType == 'fres'){
			readResType = "基础资源";
		}else if(readResType == 'bres'){
			readResType = "标准资源";
		}else if(readResType == 'prod'){
			readResType = "应用资源";
		}else{
			readResType = "";
		}
		
	}
	
	
	//设置Iframe 阅读  URL
	$(document).ready(function(){
		 if(readActionUrl == null  || readActionUrl == ""){
			alert("提示：资源文件不存在!");
			return false;
		 }else{
	   	 	readActionUrl = replaceAllString(readActionUrl,"@","?");
	   		readActionUrl = replaceAllString(readActionUrl,"*","&");
	     }
		 //alert("readActionUrl " + readActionUrl);
		 var iframe = document.getElementById('readJspUrl');
		 iframe.src = readActionUrl;
		 showWaitPanel();
		 //判断iframe是否加载完成
   		 if (iframe.attachEvent){
   	        iframe.attachEvent("onload", function(){
   	        	hiddWaitPanel();
   	        });
   	    } else {
   	        iframe.onload = function(){
   	        	hiddWaitPanel();
   	        };
   	    }
	}); 

  
    //显示
    function showWaitPanel() {
		waitPanel = new AppFrame.WaitPanel("正在努力加载，请稍候...");
		waitPanel.show();
	}
    
    //隐藏
    function hiddWaitPanel() {
    	waitPanel.hide();
	}
   

    
</script>


<base target="_self" />
</head>
<body class="yui-skin-sam" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" style="overflow: hidden;">
	<div class="con_crumbs" id="bt" style="height: 2%;">在线阅读平台&nbsp;>&nbsp; <% request.getParameter("readResType");%>在线阅读</div>
	    <!-- 加载... -->
		<div class="con_R" id="canRight" >
			<iframe id="readJspUrl" style="height: 650px;width:1020px"  scrolling="no" frameborder="0">正在努力加载，请稍候...</iframe>
		</div>
</body>
<script>

/**
 * 替换
 * @param str 源字符串
 * @param sptr 要替换的字符串
 * @param nstr 替换后的字符串
 * @returns
 */
function replaceAllString(str,sptr,nstr){
	if(str != null && str != ""){
	  while (str.indexOf(sptr) != -1){
		   str = str.replace(sptr, nstr);
		}
	}
	return str;
}

/**
 * 判断字符串是否为空
 * @param _paramValue
 * @returns {Boolean}
 */
function isNotNull(_paramValue){
	if(_paramValue != undefined && _paramValue != null && _paramValue != ""){
		return true;
	}
	return false;
}

</script>
</html>