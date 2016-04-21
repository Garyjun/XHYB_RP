<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link rel="stylesheet" href="${path}/publishRes/file/fileList.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/laypage/laypage/laypage.js"></script>
	<title>文件视图列表</title>
	<style type="text/css">
		html, body {margin: 0 auto;width:100%;height:100%;}
		#fileList{width:100%;height:100%;}
	</style>
	<script type="text/javascript">
	$(function(){
		paging(1);//分页方法
	});
	//以下将以jquery.ajax为例，演示一个异步分页
	function paging(curr){
		var screenHeight = window.screen.height;
		var screenWidth = window.screen.width;
		var size = 10;
		var num = Math.round(screenHeight/screenWidth*100)/100;
		if(num>0.7){
			size = 8;
		}
// 		var show = $.waitPanel('加载中...',false);
		$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;加载中，请稍待。。。</div>',
			css: {
                border: 'none',
                padding: '20px',
                backgroundColor: 'white',
                textAligh:'center',
            //    top:"50%",  
                width:"300px",
                opacity: .7
               }
		});
	    $.post('${path}/publishRes/file/fileResList.action', {
	        page: curr || 1, //向服务端传的参数，此处只是演示
	        size: size, //每页显示的数据数 默认为10条
	        startDate: $("#startDate").val(), 
	        endDate: $("#endDate").val(), 
	        createUser: $("#createUser").val(), 
	        resName: $("#resName").val(), 
	        publishType: $("#publishType").val(),
	        fileExtensionName: $("#fileExtensionName").val()
	    }, function(datas){
	    	if(datas!=null && datas!=''){
			var res = eval('(' + datas + ')');
			
			//创建展示列表
			try {
				var files = res.rows;
				var fileList = $("#fileList");
				var url = httpBasePath + "imgviewer.action";
				var isImageType = "jpgpngbmpjpeggif";
				var ul = $("<ul class='imglist'></ui>");
				fileList.empty();
				ul.appendTo(fileList);
				$(files).each(function(index) {
			    	var file = files[index];
			    	var title = file.title;
			    	var newName = "";
	    			if(title.length>6){
	    				 newName = 	title.substring(0,6)+"...";
	    			}else{
	    				newName = title;
	    			}
	    			var filename="";
	    			var names=file.name;
	    			if(names.length>6){
	    				filename = 	names.substring(0,6)+"...";
	    			}else{
	    				filename = names;
	    			}
	    			var param = "?filePath=" + file.coverPath + "&id=" + file.objectId
					+ "&math=" + Math.random();
	    			if(isImageType.indexOf(file.fileType)>=0){
	    				var coverFilePath = ajaxGet(url + param);
	    			}else{
	    				coverFilePath = file.coverPath;
	    			}
			    	var li = $("<li style='margin-top: 10px;margin-left:30px'>"+
					    			"<a href='###' onclick=\"javascript:preview('"+file.path+"','"+file.objectId+"','"+file.fileType+"','"+file.name+"','"+file.name+"')\"><span><img id='picture' alt='暂无图片' src=${path}/"+coverFilePath+" width='168' height='126'></span></a>"+
					    			"<h2><label ></label><a title='"+names+"'>"+filename+"</a></h2>"+
					    			"<h2><a href='javascript:resDetail(\""+file.caId+"\")'>所属资源：<font color='red'><label data-toggle='tooltip' data-placement='top' title='"+title+"'>"+newName+"</label></font></a></h2>"+
					    			"<p><a href='javascript:download(\""+file.path+"\",\""+file.aliasName+"\")'>下载</a>&nbsp;&nbsp;</p>"+
				    			"</li>");
			    	li.appendTo(ul);
			    });
				
				var pages = Math.floor(res.total/10)+1;
				//显示分页
				laypage({
				    cont: 'data_div_layerpage', //容器。值支持id名、原生dom对象，jquery对象。【如该容器为】：<div id="page1"></div>
				    pages: pages, //通过后台拿到的总页数
				    skin: 'yahei', //加载内置皮肤，也可以直接赋值16进制颜色值，如:#c00
				    skip: true, //是否开启跳页
				    curr: curr || 1, //当前页
				    jump: function(obj, first){ //触发分页后的回调
				        if(!first){ //点击跳页触发函数自身，并传递当前页：obj.curr
				        	paging(obj.curr);
				        }
				    }
				});
			} catch (e) {
				var fileList = $("#fileList");
				var isEmpty = $("<span style='margin-top:10%'><img alt='暂无文件'src=${path}/appframe/main/images/nodata.png  width='368' height='326')\"></span>");
				isEmpty.appendTo(fileList);
			}
	    	}
	    	$.unblockUI();
	    });
	};
	function download(path,aliasName){
// 		path = path.substring(18);
		path = encodeURI(encodeURI(path));
		window.location.href = "${path}/publishRes/downloadFile.action?zipName="+path+"&name="+aliasName+"&flag=1";
	}
	
	function preview(filePath, objectId, fileType, aliasName,aliasName){
// 		filePath = filePath.substring(17);
// 		objectId = "urn:file-70053548-f580-490b-84c4-6e13e1552419";
		if(fileType=="zip"){
			$.alert("zip格式不支持预览");
			return;
		}
		readFileOnline(filePath, objectId, fileType, aliasName,aliasName);
	}
	function openImg(src){
		//var imgSrc = ''; 
		parent.parent.layer.open({
		    title: false,
		    shadeClose: true,
		    closeBtn: false,
		    btn: '关闭',
		    area: ['470px', '600px'],
		    content: '<img width=\'400\' height=\'520\' src='+src+' onclick="layer.closeAll();">'
		});

	}
	function resDetail(objectId){
		//$.openWindow("${path}/publishRes/openDetail.action?objectId="+objectId+"&flagSta=1",'资源详细',1000,550);
// 		var publishType = "1";//$("#publishType").val()
// 		objectId = "urn:publish-63c6f491-6ecb-4e88-a98f-9e94c4b69bcb";//objectId
 		$.openWindow('${path}/publishRes/openDetail.action?objectId='+objectId+'&searchFlag=close&flagSta=3','资源详细',1100,550);
// 		parent.parent.layer.open({
// 		    type: 2,
// 		    title: '资源详细',
// 		    closeBtn: true,
// 		    shadeClose: true,
// // 		    maxmin: true, //开启最大化最小化按钮
// 		    area: ['1100px', '550px'],
// 		    shift: 2,
// 		    content: '${path}/publishRes/openDetail.action?objectId='+objectId+'&searchFlag=close&flagSta=3'  //iframe的url，no代表不显示滚动条
		    
// 		});
	}
	</script>
	<style type="">
	li{
		font-size: 14px;
		font-family:"Helvetica Neue", Helvetica, Arial, sans-serif;
		line-height:1.428571429;
	}
	</style>
</head>
<body>
	<input type="hidden" id="startDate" name="startDate" value="${param.startDate }" />
	<input type="hidden" id="endDate" name="endDate" value="${param.endDate }" />
	<input type="hidden" id="createUser" name="createUser" value="${param.createUser }" />
	<input type="hidden" id="resName" name="resName" value="${param.resName }" />
	<input type="hidden" id="publishType" name="publishType" value="${param.publishType }" />
	<input type="hidden" id="fileExtensionName" name="fileExtensionName" value="${param.fileExtensionName }" />
	
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
	
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li class="active">资源管理</li>
					<li class="active">文件视图</li>
					<li class="active">文件列表</li>
				</ol>
			</div>
			<div align="center" id="999" >
				<div class="rightinfo" id="fileList"></div>
				<div id="data_div_layerpage" align="center"></div>
			</div>				
		</div>
	</div>
</body>
</html>