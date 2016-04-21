<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>SHOW</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript">  
  	function read(type){
  		if(type == 1){
  			readFileOnline(11,"","pdf"); //绝对路径
  		}else if(type == 2){
  			readFileOnline(22,"","doc"); //绝对路径
  		}else if(type == 3){
  			readFileOnline(33,"","xls"); //绝对路径
  		}else if(type == 4){
  			readFileOnline(44,"","ppt"); //绝对路径
  		}else if(type == 5){
  			readFileOnline(55,"","txt"); //绝对路径
  		}else if(type == 6){
  			readFileOnline(66,"","xml"); //绝对路径
  		}else if(type == 7){
  			readFileOnline(77,"","mp4");
  		}else if(type == 8){
  			readFileOnline(88,"","mp3");
  		}else if(type == 9){
  			readFileOnline(99,"","jpg"); //相对路径
  		}else if(type == 10){
  			readFileOnline(110,"","epub"); //绝对路径
  		}else if(type == 11){
  			readFileOnline(120,"","html"); //相对路径
  		}else if(type == 12){
  			readFileOnline(120,"","");
  		}
  	}
</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
	<div class="panel panel-default">
		<div id="strobeMediaPlayback" >
		<div align="center">
	      <p><b><a class="btn hover-red" href="javascript:read(1)"><i class="fa"></i>PDF</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(2)"><i class="fa"></i>WORD</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(3)"><i class="fa"></i>EXCEL</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(4)"><i class="fa"></i>PPT</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(5)"><i class="fa"></i>TXT</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(6)"><i class="fa"></i>XML</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(7)"><i class="fa"></i>视频</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(8)"><i class="fa"></i>音频</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(9)"><i class="fa"></i>图片</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(10)"><i class="fa"></i>EPUB</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(11)"><i class="fa"></i>html</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(12)"><i class="fa"></i>不支持的格式</a></b></p>
	    </div>
	  </div> 
	</div>
</div>
</body>
</html>