<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>HTML网页在线预览</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript">  

function isNotNull(_paramValue){
	if(typeof(_paramValue) != undefined && typeof(_paramValue) != 'undefined' && _paramValue != undefined && _paramValue != 'undefined' && _paramValue != null && _paramValue != ""){
		return true;
	}
	return false;
}
	var filePath = '<%=request.getParameter("filePath")%>'; //相对路径
	var id ='<%=request.getParameter("id")%>';
	var url = httpBasePath + "htmlviewer.action"; //action
  	$(document).ready(function(){
  		if(isNotNull(filePath) || isNotNull(id)){
  			var param = "?filePath=" +  filePath + "&id=" + id + "&math=" + Math.random();
  			//第一步校验文件是否存在
  			var data =  ajaxGet(gotoCheckFileExitUrl + param);//执行ajax
  			var jsonArray = eval('(' + data + ')'); 
			if(jsonArray.noteNum != 2 && jsonArray.noteNum != 4){
  				$('#errorHtml').html(errorHtml(jsonArray.noteInfo));
  			}else{
				//获取文件路径---暂时只实现了单个html预览
  	 			data =  ajaxGet(url + param);//执行ajax
  	        	if(isNotNull(data)){
  	        		$('#byPageContent').attr("src",getRootPath() + "/" + data.trim());
  	        	}
			}
  		}else{
  			$("#byPageContent").html("<font color=red>NOTE：【路径不存在，无法预览】</font>");
  		}
  	});
</script>
</head>
<body id="errorHtml">
<iframe id="byPageContent" width="100%" height="100%" frameborder="0" src=""><p><b>加载中,请稍后......</b></p></iframe>
</body>
</html>