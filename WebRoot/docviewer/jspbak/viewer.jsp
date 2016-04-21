<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>SHOW</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<script type="text/javascript">  
//$.openWindow("${path}/docviewer/add.action",'添加订单',600,350);
	var filePath = '${filePath}';
	var id ='${id}';
	alert(filePath + "--------" + id);
	var type = 10;
	var url = "";
  	//function read(type){
  	$(document).ready(function(){
  		var param = "filePath=" + filePath + "&id=" + id;
  		if(type == 1){
  			param = "";
  			url = "${path}/docviewer/pdfviewer.action?" + param;
  		}else if(type == 2){
  			param = "";
  			url = "${path}/docviewer/officeviewer.action?" + param;
  		}else if(type == 3){
  			param = "";
  			url = "${path}/docviewer/officeviewer.action?" + param;
  		}else if(type == 4){
  			param = "";
  			url = "${path}/docviewer/officeviewer.action?" + param;
  		}else if(type == 5){
  			param = "";
  			url = "${path}/docviewer/txtviewer.action?" + param;
  		}else if(type == 6){
  			param = "";
  			url = "${path}/docviewer/txtviewer.action?" + param;
  		}else if(type == 7){
  			param = "";
  			url = "${path}/docviewer/videoviewer.action?" + param;
  		}else if(type == 8){
  			param = "";
  			url = "${path}/docviewer/audioviewer.action?" + param;
  		}else if(type == 9){
  			param = "";
  			url = "${path}/docviewer/imageviewer.action?" + param;
  		}else if(type == 10){
  			param = "";
  			url = "${path}/docviewer/epub.jsp?" + param;
  		}else{
  		   $.alert('格式不支持！');
  		}
  		
  		if(url != ""){
  			$('#byPageContent').attr("src",url);
  		}
  	//}
  	});
</script>
<base target="_blank" />
</head>
<body>
<!-- <div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;"> -->
<!-- 	<div class="panel panel-default"> -->
<!-- 		<div id="strobeMediaPlayback" > -->
<!-- 		<div align="center"> -->
<!-- 	      <p><b><a class="btn hover-red" href="javascript:read(1)"><i class="fa"></i>PDF</a></b></p> -->
<!-- 	      <p><b><a class="btn hover-red" href="javascript:read(2)"><i class="fa"></i>WORD</a></b></p> -->
<!-- 	      <p><b><a class="btn hover-red" href="javascript:read(3)"><i class="fa"></i>EXCEL</a></b></p> -->
<!-- 	      <p><b><a class="btn hover-red" href="javascript:read(4)"><i class="fa"></i>PPT</a></b></p> -->
<!-- 	      <p><b><a class="btn hover-red" href="javascript:read(5)"><i class="fa"></i>TXT</a></b></p> -->
<!-- 	      <p><b><a class="btn hover-red" href="javascript:read(6)"><i class="fa"></i>XML</a></b></p> -->
<!-- 	      <p><b><a class="btn hover-red" href="javascript:read(7)"><i class="fa"></i>视频</a></b></p> -->
<!-- 	      <p><b><a class="btn hover-red" href="javascript:read(8)"><i class="fa"></i>音频</a></b></p> -->
<!-- 	      <p><b><a class="btn hover-red" href="javascript:read(9)"><i class="fa"></i>图片</a></b></p> -->
<!-- 	       <p><b><a class="btn hover-red" href="javascript:read(10)"><i class="fa"></i>EPUB</a></b></p> -->
<!-- 	      <p><b><a class="btn hover-red" href="javascript:read(11)"><i class="fa"></i>不支持的格式</a></b></p> -->
<!-- 	    </div> -->
<!-- 	  </div>  -->
<!-- 	</div> -->
<!-- </div> -->
<iframe id="byPageContent" width="100%" height="100%" frameborder="0" src=""></iframe>
</body>
</html>