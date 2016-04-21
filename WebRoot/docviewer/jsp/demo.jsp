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
  		    //===================调用示例说明======start===============
  		    // 直接调用 readFileOnline(relPath) 方法即可
  		    // @param relPath:文件相对路径（相对于根目录的路径，根目录在web-config.xml中配置的"FILE_ROOT"）
  		    // 
  		    // 注：
  		    // 1.针对传入ID，暂未实现 
  		    // 2.office系列要实现预览，请参考 "../Common/com/brainsoon/common/util/dofile/conver/offce转pdf说明.txt"说明文档
  		    // 3.readFileOnline 详细请查看【 方法在 ../docviewer/js/fileviewer.js 】中的说明,里面有文件类型的支持说明
  		    /**
			 * @方法说明 在线预览总方法入口，系统根据以下的参数判断并自动调用预览方法
			 * @param filePath 文件url 可选 （和filePath两者必须其一）
			 * @param id  资源id（文件id） 可选（和filePath两者必须其一）
			 * @param fileType 文件类型 可选（如果只传id的情况下，fileType必选）
			 * @param fileSize 文件大小 可选（用于控制是否支持在线预览）
			 * @param readType 预览方式（0-打开新窗口预览 1-弹窗预览 默认为打开新窗口预览） 可选
			 * @param readWidth 弹窗的宽 （默认为'900'） 可选
			 * @param readHigth 弹窗的高 （默认为'600'） 可选
			 */
			//===================调用示例说明========end=============
				
  			//如果不是传ID，则后面的参数都不用传递
  			readFileOnline("TB/T01/G00001/hsjc_TB_K_V00_XB-_SL5_T01_F10_10264958/失控全人 类的最终命运和结局凯文 凯利着影印版.pdf"); //相对路径
  		}else if(type == 2){
  			//如果要传ID，则必须要跟文件扩展名
  			readFileOnline("pdf/java详解 .docx","doc"); //相对路径
  		}else if(type == 3){
  			readFileOnline("pdf/博云科技通讯录.xls","","xls"); //相对路径
  		}else if(type == 4){
  			readFileOnline("pdf/展会宣讲PPT：出版行业成典型案例_博云科技_马清华_内部可编辑版.pptx","","ppt"); //相对路径
  		}else if(type == 5){
  			readFileOnline("TB/T01/G00001/hsjc_TB_K_V00_XB-_SL5_T01_F10_10264958/配置.txt","","txt"); //相对路径
  		}else if(type == 6){
  			readFileOnline("pdf/toc.xml","","xml"); //相对路径
  		}else if(type == 7){
  			readFileOnline("pdf/1-你好.flv","","mp4"); //相对路径
  		}else if(type == 8){
  			readFileOnline("pdf/1-你好.mp3","","mp3"); //相对路径
  		}else if(type == 9){ //图片支持多张预览，多张以逗号分隔
  			readFileOnline("pdf/1.jpg,pdf/2-你好.jpg","","jpg"); //相对路径
  		}else if(type == 10){
  			readFileOnline("pdf/老外超爱说的工作英语300句.epub","","epub"); //相对路径
  		}else if(type == 11){
  			readFileOnline("TB","","zip"); //相对路径
  		}else if(type == 12){
  			readFileOnline("pdf/你好啊.html","","html"); //相对路径
  		}else if(type == 13){
  			readFileOnline("","2222","tszy"); //相对路径--图书资源预览
  		}else if(type == 14){
  			readFileOnline("","2223","jhzy"); //相对路径--聚合资源预览
  		}else if(type == 15){
  			readFileOnline("","","");
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
	      <p><b><a class="btn hover-red" href="javascript:read(11)"><i class="fa"></i>zip</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(12)"><i class="fa"></i>html</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(13)"><i class="fa"></i>图书资源</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(14)"><i class="fa"></i>聚合资源</a></b></p>
	      <p><b><a class="btn hover-red" href="javascript:read(15)"><i class="fa"></i>不支持的格式</a></b></p>
	    </div>
	  </div> 
	</div>
</div>
</body>
</html>