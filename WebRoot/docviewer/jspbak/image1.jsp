<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>图片在线预览</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsPlug-in/slideshow/js/supersized.3.2.7.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsPlug-in/slideshow/js/jquery.easing.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jsPlug-in/slideshow/theme/supersized.shutter.min.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/jsPlug-in/slideshow/theme/supersized.shutter.css" media="screen" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/jsPlug-in/slideshow/css/supersized.css" media="screen" />
<script type="text/javascript">  

var arrObjs = window.parent.arrObjs;
var startSlide = window.parent.startSlide;
if(arrObjs != null && arrObjs != "undefined" && arrObjs.length > 0){
	if(arrObjs.length==1){
		startSlide = 1;
	}
}else{
	var imgUrl = '<%=request.getParameter("filePath") %>';
	var imgName = '<%=request.getParameter("imgName") %>';
	if(imgUrl != null && imgUrl != ""){
		arrObjs = new Array();
		arrObjs.push({image:imgUrl, title:imgName=="null"?"":imgName, thumb:'', url:imgUrl});
	}
    
	if(arrObjs == null || arrObjs == "undefined" || arrObjs.length <= 0){
		arrObjs = new Array();
	}else{
		startSlide = 1;
	}
}

        jQuery(function($){
			$.supersized({
				// Functionality
				slideshow               :   1,			// Slideshow on/off    幻灯片开/关。禁用导航和转换。
				autoplay				:	0,			    // Slideshow starts playing automatically  决定是否开始播放幻灯片,当页面加载
				start_slide             :   startSlide,			    // Start slide (0 is random)  幻灯片开始  它控制哪些映像被加载,0会产生一个随机的形象要加载的每一次。
				stop_loop				:	0,			    // Pauses slideshow on last slide  到最后一张幻灯片暂停幻灯片播放
				random					: 	0,			// Randomize slide order (Ignores start slide) 幻灯片是随机的顺序所示。start_slide不顾。
				slide_interval          :   3000,		// Length between transitions  滑动的变化的时间（毫秒）
				
				//transition 控制,这一效果是用于幻灯片之间的过渡。
				//0或' none ' -没有过渡效果
				//1或“褪色”——渐变效果(默认)
				//2或“slideTop”——从上下滑
				//3或“slideRight”——从右下滑
				//4或“slideBottom”——从底部下滑
				//5或“slideLeft”——从左下滑
				//6或“carouselRight”——从右到左旋转木马
				//7或“carouselLeft”——从左到右旋转木马
				transition              :   6, 			    // 0-None, 1-Fade, 2-Slide Top, 3-Slide Right, 4-Slide Bottom, 5-Slide Left, 6-Carousel Right, 7-Carousel Left
				transition_speed		:	1000,	// Speed of transition  以毫秒为单位的速度转换。
				new_window				:	1,			// Image links open in new window/tab  幻灯片的链接打开一个新窗口。
				pause_hover             :   0,			// Pause slideshow on hover  暂停当前图像幻灯片
				keyboard_nav            :   1,			// Keyboard navigation on/off  　　允许控制通过键盘: 空格键——暂停或开始    右箭头或向上箭头-下一张幻灯片   左箭头或向下箭头——先前的幻灯片

				
				//performance  使用图像显示选项在Firefox和ie调整图像质量。这可以加速/减速过渡。Webkit还不支持这些选项。
				//0 -没有调整
				//1 -混合,降低图像质量在转换和恢复完成后。(默认)
				//2 -图像质量更高
				//3 -更快的过渡的速度,更低的图像质量
				performance				:	3,		// 0-Normal, 1-Hybrid speed/quality, 2-Optimizes image quality, 3-Optimizes transition speed // (Only works for Firefox/IE, not Webkit)
				image_protect			:	1,		// Disables image dragging and right click with Javascript 禁用右击和图像拖使用Javascript。
														   
				// Size & Position						   
				min_width		        :   0,			// Min width allowed (in pixels) 最小宽度图像是允许的。如果它满足时,图像大小而不会进一步缩小
				min_height		        :   0,		// Min height allowed (in pixels)  最低身高图像是允许的。如果它满足时,图像大小而不会进一步缩小
				vertical_center         :   1,		// Vertically center background  中心图像垂直。当关闭,图像调整  /显示从页面的顶部。
				horizontal_center       :   1,		// Horizontally center background 中心横贯画面。当关闭,图像调整  /显示从左边的页面。
				fit_always				:	0,			// Image will never exceed browser width or height (Ignores min. dimensions) 防止从图片曾被裁。忽略了最小宽度和高度。
				fit_portrait         	:   1,			// Portrait images will not exceed browser height  防止被剪裁图像通过锁定在100%的高度。
				fit_landscape			:   1,			// Landscape images will not exceed browser width 防止被剪裁图像通过锁定在100%宽度
														   				
				//slide_links 生成的链接的列表跳转到相应的幻灯片。
				//0或假	禁用幻灯片链接
				//数字	数(默认)
				//name	头衔的下滑
				//空 ——链接是空的。允许背景精灵。
				slide_links				:	'blank',	     // Individual links for each slide (Options: false, 'num', 'name', 'blank')  链接为每个幻灯片(选项:为假,“num”、“名”、“空白”)
				thumb_links				:	1,			 // Individual thumb links for each slide
				
				//thumbnail_navigation 切换后退/前进缩略图导航。
				//打开,当缩略图从下一个/以前的职位是生成的,可以通过点击来导航。
				//如果“小样”字段的幻灯片是空的,它会简单地缩减完整大小的图像。
				thumbnail_navigation    :   0,		 // Thumbnail navigation
				slides 					: arrObjs,      //该数组包含所有你的照片与图片,标题、url和任何定制字段。可以删除任何字段你留下空

				// Theme Options	  小样的控制	   
				progress_bar			:	1,			// Timer for each slide							
				mouse_scrub				:	0
					
				});
		    });
        
        
        $(document).ready(function(){
        	if(arrObjs.length!=1){
        		$("#nextthumb").after("<a id='prevslide' class='load-item'></a><br><a id='nextslide' class='load-item'></a>"); 
        	}  
         });
</script>
<style type="text/css">
		ul#demo-block{ margin:0 15px 15px 15px; }
			ul#demo-block li{ margin:0 0 10px 0; padding:10px; display:inline; float:left; clear:both; color:#aaa; background:url('img/bg-black.png'); font:11px Helvetica, Arial, sans-serif; }
			ul#demo-block li a{ color:#eee; font-weight:bold; }
	</style>
</head>
<body>
 <!-- <div class="con_crumbs" id="bt">图片SHOW&nbsp;</div> -->
 
 <ul id="demo-block">
		<li><font size="2">提示：点击图片查看原图</font></li>
	</ul>
	
 <!--Thumbnail Navigation-->
	<div id="prevthumb"></div>
	<div id="nextthumb"></div>
	
	<!--Arrow Navigation
	<a id="prevslide" class="load-item"></a>
	<a id="nextslide" class="load-item"></a>
	-->
	
	<div id="thumb-tray" class="load-item">
		<div id="thumb-back"></div>
		<div id="thumb-forward"></div>
	</div>

	 
	<!--Time Bar-->
	<div id="progress-back" class="load-item">
		<div id="progress-bar"></div>
	</div>
	
	<!--Control Bar-->
	<div id="controls-wrapper" class="load-item">
		<div id="controls">
			
			<a id="play-button"><img id="pauseplay" src="<%=request.getContextPath()%>/jsPlug-in/slideshow/img/pause.png"/></a>
		
			<!--Slide counter-->
			<div id="slidecounter">
				<span class="slidenumber"></span> / <span class="totalslides"></span>
			</div>
			
			<!--Slide captions displayed here-->
			<div id="slidecaption"></div>
			
			<!--Thumb Tray button-->
			<a id="tray-button"><img id="tray-arrow" src="<%=request.getContextPath()%>/jsPlug-in/slideshow/img/button-tray-up.png"/></a>
			
		    <!--Navigation   <ul id="slide-list"></ul>-->
			
		</div>
	</div>
</body>
</html>