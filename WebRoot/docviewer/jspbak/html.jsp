<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>SHOW</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<script type="text/javascript">  
	var filePath = '<%=request.getParameter("filePath")%>'; //相对路径
	var id ='<%=request.getParameter("id")%>';
	filePath = "appframe/plugin/fancyBox/demo/index.html";
	//alert(filePath + "--------" + id);
	var type = 10;
	var url = "";
  	$(document).ready(function(){
  		//var param = "filePath=" + filePath + "&id=" + id;
  		if(filePath != null && filePath != ""){
  			$('#byPageContent').attr("src","${path}/" + filePath);
  		}
  	});
</script>
<base target="_blank" />
</head>
<body>
<iframe id="byPageContent" width="100%" height="100%" frameborder="0" src=""></iframe>
</body>
</html>