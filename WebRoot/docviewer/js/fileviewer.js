/**初始化文件类型数组*/
//图片支持的类型
var imgTypes = ["jpg","png","bmp","tif","tiff","jpeg","gif"]; 

//视频支持的类型
var videoTypes = ["avi","rmvb","asf","mov","rm","mp4","3gp","flv","mpg","wmv","ogv","mpeg","mpeg2","f4v","f5v","f6v","swf"];

//音频支持的类型
var audioTypes = ["mp3","wav","ape","flac","ac3","ogg","wma"];

//视频的ogg，ogv，webm格式
//var html5VideoTypes = ["ogg","ogv","webm"];

//txt or  xml
var txtTypes = ["txt","xml","opf","ncx","css","js","php","java","properties","dtd","log","tld","jsp","sql","c","ini","bat","py","cgi","asp"];

//HTML
var htmlTypes = ["html","htm"];

//office格式
var officeTypes = ["doc","docx", "docm","dotx","dotm", "mpp","xls","xlsx","xlsm","xltx","xltm","xlsb","xlam","pptx","ppt","pptm","ppsx","ppsm","potx","potm","ppam","vsd"];

//pdf
var pdfTypes = ["pdf"];

//自定义类型(封装类型)
var epubTypes = ["cdp",'rmrb','tdp','epub'];

//Marc
var marcTypes = ["iso"];

//zip
var zipTypes = ["zip"];

//扩展资源类型(图书资源<tszy>、聚合资源<jhzy>)
var expandTypes = ["tszy","jhzy"];


/**初始化文件类型预览URL变量*/
//视频
var videoUrl =getRootPath() +  '/docviewer/jsp/video.jsp';   

//图片
var imageUrl =getRootPath() +  '/docviewer/jsp/image.jsp'; 

//视频ogg,ogv,webm预览框架
//var html5VideoUrl = getRootPath() + '/docviewer/jsp/html5Video.jsp'; 

//txt和xml
var txtUrl =getRootPath() + '/docviewer/jsp/txtOrXml.jsp';

//html
var htmlUrl = getRootPath() +  '/docviewer/jsp/html.jsp';

//office系列（word、excel、ppt等）
var officeUrl =getRootPath() +  '/docviewer/jsp/office.jsp';

//PDF
var pdfUrl = getRootPath() + '/docviewer/jsp/pdf.jsp';

//zip预览器
var zipUrl = getRootPath() + '/docviewer/jsp/zip.jsp';

//epub预览
var epubUrl =  getRootPath() +  '/docviewer/jsp/epub.jsp';
// getRootPath() + 

//扩展资源类型(图书资源<tszy>、聚合资源<jhzy>)预览器
var expandUrl =getRootPath() + '/docviewer/jsp/expand.jsp';




//显示错误提示信息
var showErrInfo = "无法预览!";
//定义预览时默认标题的名称
var readTitle = "在线预览";  
//http请求根路径
var httpBasePath = getRootPath() + "/docviewer/";
//异步校验文件是否存在url
var gotoCheckFileExitUrl = httpBasePath + "checkFileIsOk.action";

/**
 * @方法说明 在线预览总方法入口，系统根据以下的参数判断并自动调用预览方法
 * @param filePath 文件path 必填（相对目录，即file.path） 	
 * @param id  文件id 必填（objectId，即file.objectId）
 * @param fileType 文件类型 可选（如果只传id的情况下，fileType必选）
 * @param fileSize 文件大小 可选（用于控制是否支持在线预览）
 * @param readType 预览方式（0-打开新窗口预览 1-弹窗预览 2-iframe预览 默认为打开新窗口预览） 可选
 * @param iframeName iframe名称 可选（用于控制是否支持在线预览）
 * @param readWidth 弹窗的宽 （默认为'900'） 可选
 * @param readHigth 弹窗的高 （默认为'600'） 可选
 * 【filePath】路径说明：
 * txt or xml 文件<ncx/opf>：文件绝对路径 如:D://txt/1.txt
 * office 文件<.doc/.excel/.ppt等>：带有http的文件相对路径 如:http://localhost:8089/bsrcm/swf/doc/1.doc
 * 音视频 文件<.mp4/.flv> ：带有http的文件相对路径 如:http://localhost:8089/bsrcm/swf/vedio/1.mp4
 * PDF 文件： 带有http的文件相对路径 如:http://localhost:8089/bsrcm/swf/pdf/1.pdf
 * 图片格式 文件<jpg/gif>：文件相对路径  如: http://localhost:8089/bsrcm/swf/img/1.jpg
 */
function readFileOnline(filePath,id,fileType,fileZhName,fileName,fileSize,readType,readWidth,readHigth) {
	var iframeId = "byPageContent"; //默认iframe的id属性值
	/**给默认项赋值*/
	if(!isNotNull(readType)){ readType = "1";} //预览方式（0-打开新窗口预览 1-弹窗预览 默认为打开新窗口预览） 可选
	if(!isNotNull(readWidth)){ readWidth = 900;} //弹窗的宽 （默认为'900'） 可选
	if(!isNotNull(readHigth)){ readHigth = 550;} //弹窗的高 （默认为'600'） 可选
	//如果文件大小大于2G，则不支持预览
	if(isNotNull(fileSize)){ 
		if(fileSize > 2048000){
		$.alert(showErrInfo + "【文件太大，暂不支持在线预览！】");
		return;
		}
	} 
	/**第一步：校验必选的项是否存在*/
	//1.判断文件id或文件路径两者都为空
	if(!isNotNull(id) && !isNotNull(filePath)){
		$.alert(showErrInfo + "【路径不存在，请检查！】");
		return;
	}
	//2.检查文件类型是否存在
	//如果filePath为空，id不为空，则文件类型是必须的传入的
	var fileNoExitInfo = showErrInfo + "【无法识别文件类型，请检查！】";
	if(!isNotNull(filePath) && isNotNull(id)){//获取文件类型
		if(!isNotNull(fileType)){
			$.alert(fileNoExitInfo);
			return;
		}
	}
	
	
	
	//如果filePath不为空，则获取文件类型
	if(!isNotNull(fileType)){
		if(isNotNull(filePath)){
			fileType = getFileExt(filePath);
			if(!isNotNull(fileType)){
				$.alert(fileNoExitInfo);
				return;
			}
		}
	}
	
	
	
	//特殊逻辑:如果是zip预览，则重新设置宽高
//	for (var nowType in zipTypes) {
//		if (zipTypes[nowType] == fileType) {
//			 readWidth = 350;
//			 readHigth = 450;
//		}
//	}
	
	/**第二步：校验是否支持该类型的文件预览*/
	var readUrl = "";
	//调用 【checkFileTypeId】 方法：判断该文件类型用什么方法进行预览
	
	//特殊处理----检查文件类型（图片）
//	if(checkFileType(fileType,imgTypes)){
//		viewerImg(id,filePath); //直接执行查看图片
//	}else{
		readUrl = getReadFileUrlByFileType(fileType.toLowerCase(),id,filePath);
		/**第三步：判断文件【fileUrl】是否为空：
		 * (1) 如果【fileUrl】存在，则直接使用【fileUrl】进行预览;
		 * (2) 否则【fileUrl】不存在，则调用文件【id】查询【fileUrl】预览;
		 * (3) 否则如果以上两者都不存在，直接提示文件无法预览*/
		if(isNotNull(readUrl)){
			filePath = encodeURI(encodeURI(filePath));
			readUrl += "&id=" + id + "&filePath=" + filePath + "&fileType=" + fileType+"&fileZhName="+fileZhName+"&fileName="+fileName;
			//执行在线预览方法
			if(readType == "0"){ //0-打开新窗口预览 1-弹窗预览   默认为打开新窗口预览
//				$.openWindow(readUrl,readTitle,readWidth,readHigth);
				parent.parent.layer.open({
				    type: 2,
				    title: readTitle,
				    closeBtn: true,
				    shadeClose: true,
//				    maxmin: true, //开启最大化最小化按钮
				    area: ['1200px', '650px'],
				    shift: 2,
				    content: readUrl  //iframe的url，no代表不显示滚动条
				    
				});
				//$.openWindow('${path}/publishRes/preview.action?objectId='+objectId,'资源文件预览',1200,550);

			}else if(readType == "2"){ //2-右侧iframe预览
				if(isNotNull(document.getElementById(iframeId))){
					document.getElementById(iframeId).src = readUrl;
				}else{
					$.alert(showErrInfo + "【系统内部错误，请检查！】");
					return;
				}
			}else{
				//$.openWindow(readUrl, {title:readTitle, button:[{name:'关闭',callback:function(){}}]});
				parent.parent.layer.open({
				    type: 2,
				    title: readTitle,
				    closeBtn: true,
				    shadeClose: true,
//				    maxmin: true, //开启最大化最小化按钮
				    area: ['1200px', '650px'],
				    shift: 2,
				    content: readUrl,  //iframe的url，no代表不显示滚动条
//				    full: function(layero){
//				    	parent.parent.layer.full(layero.context);
//				    }
//				    
				});
				//$.openWindow('${path}/publishRes/preview.action?objectId='+objectId,'资源文件预览',1200,550);

//				$.openWindow(readUrl,readTitle,readWidth,readHigth);
			}
//		}
	}
}	
	



/**
 * @方法说明 判断该文件类型用什么方法进行预览
 * @param fileType 文件类型
 * @param id 文件id
 * @returns 返回预览URL
 */
function getReadFileUrlByFileType(fileType,id,filePath){
	var readUrl = ""; //返回预览url的路径
	/**是否是图片类型*/
	for (var nowType in imgTypes) {
		if (imgTypes[nowType] == fileType) {
			readUrl = imageUrl;
		}
	}
	
	/**是否是视频类型*/
	for (var nowType in videoTypes) {
		if (videoTypes[nowType] == fileType) {
			readUrl = videoUrl;
		}
	}
	
	/**是否是音频类型*/
	for (var nowType in audioTypes) {
		if (audioTypes[nowType] == fileType) {
			readUrl = videoUrl;
		}
	}
	
	/**是否是ogg,ogv,webm视频类型*/
//	for (var nowType in html5VideoTypes) {
//		if (html5VideoTypes[nowType] == fileType) {
//			readUrl = videoUrl;
//		}
//	}
	
	/**是否是txt文本类型---暂时是将txt文本转换成pdf再预览，因此调用的函数跟pdf一致 */
	for (var nowType in txtTypes) {
		if (txtTypes[nowType] == fileType) {
			readUrl = pdfUrl;
		}
	}
	
	/**是否是html类型*/
	for (var nowType in htmlTypes) {
		if (htmlTypes[nowType] == fileType) {
			readUrl = htmlUrl;
		}
	}
	
	/**是否是Office类型 ---暂时是将office转换成pdf再预览，因此调用的函数跟pdf一致 */
	for (var nowType in officeTypes) {
		if (officeTypes[nowType] == fileType) {
			readUrl = pdfUrl;
		}
	}
	
	/**是否是pdf类型*/
	for (var nowType in pdfTypes) {
		if (pdfTypes[nowType] == fileType) {
			readUrl = pdfUrl;
		}
	}
	
	/**是否是marc类型*/
	for (var nowType in marcTypes) {
		if (marcTypes[nowType] == fileType) {
			readUrl = getUrl(fileType,id,filePath);
		}
	}

	/**是否是zip类型*/
	for (var nowType in zipTypes) {
		if (zipTypes[nowType] == fileType) {
			readUrl = zipUrl;
		}
	}
	
	/**是否是epub类型*/
	for (var nowType in epubTypes) {
		if (epubTypes[nowType] == fileType) {
			readUrl = epubUrl;
		}
	}
	
	
	/**扩展资源类型(图书资源<tszy>、聚合资源<jhzy>)*/
	for (var nowType in expandTypes) {
		if (expandTypes[nowType] == fileType) {
			readUrl = expandUrl;
		}
	}
	
	/**如果readUrl为空，表示不支持该格式的文件在线预览*/
	if(!isNotNull(readUrl)){
		//$.alert(showErrInfo + "【系统不支持该文件类型<." + fileType + ">在线预览】");
		notice("提示",showErrInfo + "【系统不支持该文件类型<." + fileType + ">在线预览】","3");
		return;
	}
	//return readUrl + "?math=" + Math.random();
	return readUrl + "?";
}


/**
 * @方法说明 图片在线预览
 * @param id
 * @param filePath
 * @return void
 */
function viewerImg(id,filePath){
	var param = "?filePath=" +  filePath + "&id=" + id + "&math=" + Math.random();
	var url = httpBasePath + "imgviewer.action"; //action
	filePath = ajaxGet(url + param);//执行ajax
	//如果文件路径不为空，则执行，否则打印异常提示
	if(!isNotNull(filePath)){
		//$.alert(showErrInfo + "【未找到要预览的图片】");
		notice("提示",showErrInfo + "【未找到要预览的图片】","3");
	}else{
		//处理成json串
		var filePaths = filePath.split(",");
		var imgJsonArr = "[";
		var b = false;
		for(var i = 0; i < filePaths.length; i++){
			var path = filePaths[i].trim(); //去掉前后空格
			if(isNotNull(path)){
				b = true;
				imgJsonArr += "{href:'" + getRootPath() + "/" + path + "',title:'" + path.substring(path.lastIndexOf("/")+1,path.lastIndexOf(".")) +"'},";
			}
		}
		
		if(b){
			if(imgJsonArr !="["){
				imgJsonArr = imgJsonArr.substring(0,imgJsonArr.length-1);
			}
			imgJsonArr += "]";
			//alert(imgJsonArr);
			//调用打开函数
			top.$.fancybox.open(
			  eval(imgJsonArr),
			  {
				helpers : {
					thumbs : {
						width: 75,
						height: 50
					}
				}
			 });
		}else{
			//$.alert(showErrInfo + "【未找到要预览的图片】");
			notice("提示",showErrInfo + "【未找到要预览的图片】","3");
		}
	}
}



/**
 * @方法说明 对预览的URL判断（针对：自定义文件格式预览(封装类型)）
 * @param fileType 文件类型
 * @param type 资源类型
 * @param id 文件id
 * @returns 返回预览URL
 */
function getUrl(fileType,id,filePath){
	var url = "";
	if(fileType == "iso"){
		url = httpBasePath + "marcviewer.action?filePath=" + filePath + "&id=" + id + "&math=" + Math.random();
	}
	return url;
}


/**
 * @方法说明 对预览的URL判断（针对：自定义文件格式预览(封装类型)）
 * @param fileType 文件类型
 * @param type 资源类型
 * @param id 文件id
 * @returns 返回预览URL
 */
function getEncodeURIUrl(url){
	var newUrl = "";
	url = replaceAllString(url,"\\","/");
	if (isNotNull(url)) {
		var arrUrl = url.split("/");
		for(var i=0;i< arrUrl.length;i++){
			var param = arrUrl[i];
			if (isNotNull(param)) {
				newUrl += encodeURIComponent(param.trim()) + "/";
			}
		}
	 }
	if (isNotNull(newUrl)) {
		newUrl = newUrl.substring(0,newUrl.length-1);
	}
	return newUrl;
}





/**
 * @方法说明 替换特殊字符
 * @param readUrl url字符串
 */
function replaceAllOtherStr(readUrl){
	readUrl = replaceAllString(readUrl,"?","@");
	readUrl = replaceAllString(readUrl,"&","*");
	return readUrl;
}



/**
 * @方法说明 替换字符串
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
 * @方法说明 判断字符串是否为空
 * @param _paramValue
 * @returns {Boolean}
 */
function isNotNull(_paramValue){
	if(_paramValue != undefined && _paramValue != null && _paramValue != "" && _paramValue != "undefined"){
		return true;
	}
	return false;
}


/**
 * @方法说明 判断是否包含某种类型的文件
 * @param fileType 文件类型
 * @param filetypeArr 文件类型数组
 */
function checkFileType(fileType,filetypeArr){
	var b = false;
	if(filetypeArr != null){
		for (var nowType in filetypeArr) {
			if (filetypeArr[nowType] == fileType) {
				b = true;
				break;
			}
		}
	}
	return b;
}


/**
 * @方法说明 得到文件后缀名
 * @param filePath
 * @returns
 */
function getFileExt(filePath){
	 var reVal = ""; 
	 if (isNotNull(filePath)) {
		 if(filePath.indexOf("/") != -1){
			 filePath =  filePath.substring(filePath.lastIndexOf("/")+1,filePath.length)
		 }
		 if(filePath.indexOf(".") != -1){
			 filePath = replaceAllString(filePath, "\\\\", "/");
			 reVal =  filePath.substring(filePath.lastIndexOf(".")+1,filePath.length); 
		 }
	 }
	 return reVal;
}


/**
 * @方法说明 取文件名不带后缀
 * @param filePath
 * @returns
 */
function getFileNameNoExt(filePath) {
    var fileName = getFileName(filePath);
    if (isNotNull(fileName)) {
    	return fileName.substring(0,(fileName.length - getFileExt(fileName).length -1));
    }else{
		return "";
	}
}


/**
 * @方法说明 取文件全名名称
 * @param filePath
 * @returns
 */
function getFileName(filePath) {
	var reVal = ""; 
	if (isNotNull(filePath)) {
		 filePath = filePath.trim();
		 filePath = replaceAllString(filePath, "\\\\", "/");
		 if(filePath.indexOf("/") != -1){
			 reVal =  filePath.substring(filePath.lastIndexOf("/")+1,filePath.length);
		 }
    }
	return reVal;
}


    

/**
 * @功能描述：AJAX GET请求
 * @return 
 */
function ajaxGet(url){ 
	var response ="";
	var request = new XMLHttpRequest(); 
	if (window.ActiveXObject) {
		request = new ActiveXObject("Microsoft.XMLHTTP");
	}
	if(request == null){
		//$.alert('您的浏览器不支持AJAX！');
		notice("提示",'您的浏览器不支持AJAX！',"3");
		return; 
	}
  request.open("GET",encodeURI(url), false); 
  request.onreadystatechange = function(){ 
    if(request.readyState == 4 && request.status == 200){ 
      if (request.responseText){         
    	  response = request.responseText; 
      } 
    } 
  }; 
  request.send(null); 
  return response;
}

/**
 * @功能描述：AJAX POST请求
 * @param url 请求url
 * @param vars send()
 * @return 
 */
function ajaxPost(url, vars){ 
	var response ="";
	var request = new XMLHttpRequest(); 
	if (window.ActiveXObject) {
		request = new ActiveXObject("Microsoft.XMLHTTP");
	}
	if(request == null){
		//$.alert('您的浏览器不支持AJAX！');
		notice("提示","您的浏览器不支持AJAX！","3");
		return; 
	}
    request.open('POST', encodeURI(url),true); 
    request.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
    request.onreadystatechange = function(){ 
        if(request.readyState == 4 && request.status == 200){ 
          if (request.responseText){         
        	  response = request.responseText; 
          } 
        } 
      };  
     request.send(vars);
     return response;
} 

/**
 * @功能描述：生成随机数，默认为15位字符串
 * @returns {String}
 */
function randomChar(){
   var x="0123456789qwertyuioplkjhgfdsazxcvbnm";
   var tmp="";
   for(var i=0;i<15;i++){
	   tmp += x.charAt(Math.ceil(Math.random()*100000000)%x.length);
   }
   return tmp;
}


/**
 * @功能描述：获取文件相对路径(用于支持pdf、office等在线阅读的路径)
 * @returns {String}
 */
function viewerDirPath(filePath,fileType){
	//文件类型
	if (!isNotNull(fileType)) {
		if (isNotNull(filePath)) {
			fileType = getFileExt(filePath);

		}
	}
	//Path
	if(isNotNull(filePath)) {
		filePath = filePath.substring(0,filePath.lastIndexOf("/")+1);
		filePath = filePath + "/";
    }
   return filePath;
}



/**
 * 去前后空格
 */
String.prototype.trim = function() {    
	return this.replace(/(^\s*)|(\s*$)/g, "");    
} 

/**
 * 去前空格
 */
String.prototype.ltrim = function(){    
	return this.replace(/(^\s*)/g, "");    
} 

/**
 * 去后空格
 */
String.prototype.rtrim = function(){    
	return this.replace(/(\s*$)/g, "");    
} 


/**
 * 去字符串中的所有空格，不仅仅包含前后空格
 */
String.prototype.trimAll = function(){  
	return this.replace(/\s/ig, "");    
} 

function errorHtml(val){
	return "<div style=\"align:center\"><font color=\"red\">"+ val +"</font></div>";
}


/**
 * 重试操作
 * @param srcPath
 */
function doTaskHistoryByPath(srcPath,id){
	var param = "?filePath=" +  filePath + "&id=" + id + "&math=" + Math.random();
	var url = getRootPath()  + "/publishRes/doTaskHistoryByPath.action"; //action
	var reVal = ajaxGet(url + param);//执行ajax
	//如果文件路径不为空，则执行，否则打印异常提示
	if(reVal == 0){
		//$.alert("重试操作失败，请稍后重试。");
		notice("提示","重试操作失败，请稍后重试。","3");
	}else{
		//$.alert("重试操作成功，已进入待转换队列。");
		notice("提示","重试操作成功，已进入待转换队列。","3");
	}
}
