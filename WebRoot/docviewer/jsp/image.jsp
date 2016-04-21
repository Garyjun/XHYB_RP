<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>HTML网页在线预览</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<style>
html,body {
	height: 100%;
	/*非IE的主流浏览器识别的垂直居中的方法*/
	vertical-align: middle;
	/*设置水平居中*/
	text-align: center;
	/* 针对IE的Hack */
	*display: block;
	*font-size: 175px; /*约为高度的0.873，200*0.873 约为175*/
	*font-family: Arial; /*防止非utf-8引起的hack失效问题，如gbk编码*/
	border: 1px solid #eee;
}

</style>
<script type="text/javascript">  
	var filePath = '<%=request.getParameter("filePath")%>'; //相对路径
	var id ='<%=request.getParameter("id")%>';
	var mark = '<%=request.getParameter("mark") %>';  //标识：用于判断逻辑 1:为不参与校验
	var url = httpBasePath + "imgviewer.action"; //action
	var igwidth = "";
	var igheight = "";
	$(document).ready(
			function() {
				if (isNotNull(filePath) || isNotNull(id)) {
					var param = "?filePath=" + filePath + "&id=" + id
							+ "&math=" + Math.random();
					if(isNotNull(mark) && mark == 1){
						//
		  			}else{
		  				filePath = ajaxGet(url + param);//执行ajax
		  			}
					//如果文件路径不为空，则执行，否则打印异常提示
					if (!isNotNull(filePath)) {
						$.alert(showErrInfo + "【未找到要预览的图片】");
					} else {
						//处理成json串
						var filePaths = filePath.split(",");
						var imgJsonArr = "[";
						var b = false;
						var imgHtml = "";
						var imgName = "";
						var imgHtmls = "";
						var num = 0;
						for ( var i = 0; i < filePaths.length; i++) {
							var path = filePaths[i].trim(); //去掉前后空格
							var imgType = path.substring(path.lastIndexOf("."),path.length);
 							    imgName = path.substring(path.lastIndexOf("/") + 1,path.lastIndexOf("."));
							    path = path.substring(0,path.lastIndexOf("/") + 1)+imgName+imgType;
							    if(num<1){
								imgHtmls = "<div class=\"item active\">";
								num++;
							    }else{
							    imgHtmls = "<div class=\"item\">";
							    }
							var src = getRootPath() + "/" + path;
								igwidth = 898;
								igheight = 548;
							var pathName = path.substring(path.lastIndexOf("/") + 1,
									path.lastIndexOf("."));
							//<a href=\"\" onclick=\"path('"+src+"');\">
							// onclick=\"path('"+pathSrc+"');\"
							var imgurl = "<a href='"+src +"' target=\"_blank\"><img src=\""+src+"\" height=\""+igheight+"\"  width=\""+igwidth+"\" onload=\"autoResizeImage("+ igwidth +"," + igheight +",this)\"  alt=\"点击查看原图\">"+"</div>";
								imgHtml += imgHtmls + imgurl + "";
						}
						$('#showImg').html(imgHtml);
					}
				} else {
					$("#showImg").html("<font color=red>NOTE：【路径不存在，无法预览】</font>");
				}
			});

	function isNotNull(_paramValue) {
		if (typeof (_paramValue) != undefined
				&& typeof (_paramValue) != 'undefined'
				&& _paramValue != undefined && _paramValue != 'undefined'
				&& _paramValue != null && _paramValue != "") {
			return true;
		}
		return false;
	}
// 	function path(src){
// 		$.openWindow("${path}/publishRes/showFileDetail.jsp?src="+src,'查看原图',1200,700);
// 	}
	
</script>
</head>
<body id="errorHtml">
<div id="myCarousel" class="carousel slide">
   <!-- 轮播（Carousel）指标 -->
<!--    <ol class="carousel-indicators"> -->
<!--       <li data-target="#myCarousel" data-slide-to="0" class="active"></li> -->
<!--       <li data-target="#myCarousel" data-slide-to="1"></li> -->
<!--       <li data-target="#myCarousel" data-slide-to="2"></li> -->
<!--    </ol>    -->
   <!-- 轮播（Carousel）项目 -->
   <div class="carousel-inner" id="showImg" align="center">
   
   </div>
   <!-- 轮播（Carousel）导航 -->
   <a class="carousel-control left" href="#myCarousel" 
      data-slide="prev">&lsaquo;</a>
      <!--    <img src="pre.png" width="20" height="30" border="0" alt="上一页" /> -->
   <a class="carousel-control right" href="#myCarousel" 
      data-slide="next">&rsaquo;</a>
</div> 
</body>
</html>