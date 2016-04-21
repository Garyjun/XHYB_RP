<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>Zip文件阅读</title>
<%
    String appPath1 = request.getContextPath();
%>
<script type="text/javascript" src="<%=appPath1%>/appframe/jquery/js/jquery.ztree.core-3.0.js"></script>
<script type="text/javascript" src="<%=appPath1%>/appframe/jquery/js/jquery.ztree.exedit-3.0.min.js"></script>
<script type="text/javascript" src="<%=appPath1%>/appframe/jquery/js/jquery.ztree.excheck-3.0.min.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/appframe/jquery/css/zTreeStyle/zTreeStyle.css" type="text/css" />

<style type="text/css">
body{ margin: 0; padding: 0px;margin-left: 0;margin-right: 0;overflow: hidden; }
</style>
<script language='javascript'>
var $j = $.noConflict();//使jquery $ 失效  单独页面调试打开
var setting = {
	view: {
		addDiyDom: addDiyDom,
		selectedMulti: false,
		showTitle: false
	},
	data: {
		simpleData: {
			enable: true
		}
	},
	callback: {
		onExpand: zTreeOnExpand,
		onCollapse:zTreeOnExpand
	}
};
function zTreeOnExpand(event, treeId, treeNode) {
	window.parent.setIframeHeight(document.body.scrollHeight,false);
}
var treeObj;
//自定义树链接
function addDiyDom(treeId, treeNode) {
	if(treeNode.isParent){
		return;
	}
	var aObj = $j("#" + treeNode.tId + "_a");
	var readStr = "<a id='diyBtn1_" +treeNode.id+ "' onclick=\"onClickReadSingleFile('" +treeNode.relPath+ "','"+treeNode.absPath+"','" +treeNode.fileType+"');return false;\" style='text-decoration:underline;'>阅读</a>";
	var downStr = "<a id='diyBtn2_" +treeNode.id+ "' onclick=\"onClickDownSingleFile('" +treeNode.relPath+ "','"+treeNode.absPath+ "');return false;\" style='text-decoration:underline;'>下载</a>";
	aObj.after(readStr);
	aObj.after(downStr);
	
}

/*单击阅读*/
function onClickReadSingleFile(relPath,absPath,fileType){
	if(fileType==""){
		//对于没有文件后缀的文件 默认为txt文件
		fileType="txt";
	}
	var pathType=getFilePathTypeByFileType(fileType);
	var filePath="";
	if(pathType==1){ //采用绝对路径
		filePath=replaceAllString(absPath,"|","/");
		readFileOnline(null, fileType,null,'o',"1",filePath,null,"readObjIfrm",null,null);
	}else if(pathType==2){//采用相对路径
		filePath=replaceAllString(relPath,"|","/");
		readFileOnline("", fileType,null,'o',"1",filePath,null,"readObjIfrm",null,null);
	}else if(pathType==3){
		alert("无法处理【"+pathType+"】格式 !");
		return 0;
	}else{
		alert("无法处理【"+pathType+"】格式 !");
		return 0;
	}
}

function onClickDownSingleFile(relPath,absPath){
	absPath=replaceAllString(absPath,"|","/");
	this.location='<%=appPath1%>/downSingleFile.action?filePath='+absPath;
}
var id ='<%=request.getAttribute("id") %>';
if(id == null || id =="" || id=="null")
	id = '<%=request.getParameter("id") %>';

var filePath ='<%=request.getAttribute("filePath") %>';
	if(filePath == null || filePath =="" || filePath=="null")
		filePath = '<%=request.getParameter("filePath") %>';
		
var type ='<%=request.getAttribute("type") %>';
	if(type == null || type =="" || type=="null")
		type = '<%=request.getParameter("type") %>';
//初始化树结构;
 var zTree
 $j(document).ready(function(){
	 $j.post('${pageContext.request.contextPath}/commOnlineRead/readZipFile.action?id='+id+'&filePath='+filePath+'&type='+type + "&math=" + Math.random(),'',function(data){
		 zNodes = eval('(' + data + ')');
			treeObj = $j.fn.zTree.init($j("#directory"), setting, zNodes);
	 });

// 	var url='${pageContext.request.contextPath}/commOnlineRead/readZipFile.action?id='+id+'&filePath='+filePath+'&type='+type + "&math=" + Math.random();
// 	  //异步加载文件 txt/xml
// 	  $j.ajax({  
// 	         url: url,  //访问的服务器资源---送请求的地址，可以是服务器页面也可以是WebService动作。
// 	         type:'get',  //请求的类型---GET本身是作为请求URL的一部分进行发送的;而POST请求是作为请求体的一部分进行发送的，这是HTTP协议来决定的  
// 	        // data:"",//发送请求时，携带的参数---是key:value对形式，如：{name:"grayworm",sex:"male"}，如果是数组{works:["work1","work2"]}
// 	         async: true ,  //异步
// 	       	 datatype:"json",//可以是xml、json、html等，默认为html 
// 	        //发送ajax请求前被触发，如果返回false则取消本次请求。如果异步请求需要显示gif动画，那应当在这里设置相应<img>的可见。
// 	         beforeSend: function(XMLHttpRequest){
// 	    		 //
// 	    	   },
// 	    	//请求调用完成后的回调函数（请求成功或失败时均调用），如果异步请求显示gif动画，那应当在这里设置相应的<img>不可见。
// 	    	 complete: function(XMLHttpRequest, textStatus){ 
// 	    		   //textStatus是描述返回状态的字符串
// 	         },
// 	        //请求执行成功时的回调函数
//            success: function(data,textStatus){ 
//         		   alert("1");

//          },
//         //请求执行失败时的回调函数 
//       	error: function(XMLHttpRequest,textStatus,errorThrown){
//       		   //data是服务端返回的数据可以是xml、json、text等格式
//       		   //textStatus,errorThrown是描述返回状态的信息
//       			alert("加载文件出错！");
//       		 	waitPanel.hide();
//       	}
// 	  });



 });

</script>
</head>
<body class="yui-skin-sam">
	 <div id="box" class="box" style="height:600px;">
		  <div class="con_L_box" align="left" id="canLeft" style="height:600px;float: left;">
		        <div class="con_L" style="height: 30px;width:350px;">
					<div class="con_L_title">文件导航</div>
				</div>
				<table class="yui-skin-sam" width="350px" border="0" align="center" cellpadding="0" cellspacing="0">
					<tr>
						<td>
							<ul id="directory" class="ztree"></ul>
						</td>
					</tr>
				</table>
		  </div>
		  <s:action name="gotoBar" namespace="/resclass" executeResult="true" />
		  <div class="con_R" id="canRight" >
		  	<iframe id="readObjIfrm" style="height: 600px;width:100%"  scrolling="no" frameborder="0"></iframe>
		  </div>
	 </div>
</body>
</html>
