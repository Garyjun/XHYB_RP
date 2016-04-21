<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="java.net.URLDecoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/appframe/common.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript">
	var fileCompleteNum=0;
	$(function() {
		var parentPath=$("#parentPath").val();
		parentPath=escape(encodeURIComponent(parentPath));
		var url='${path}/bookRes/uploadBookFile.action?parentPath='+parentPath;
		 //初始化上传组件
	    $('#fileData').uploadify({
	        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
	        'uploader' : url,
	        'debug' : false,
	        'method': 'post',
	        //是否自动上传
	        'auto':false,
	        'multi':false,
	        'queueSizeLimit':1,
	        'buttonText':'上传文件',
	        'removeCompleted':false,
	        'onUploadStart': function (file) {
	        	uploadFileWaiting = $.waitPanel('正在上传文件...',false);
        	},
	        'onSelect':function(file){
	           //cFileType = file.type;
	        	
	        },
	        'onUploadSuccess': function(file, data, response) {  
	        	var info = '<font color="red">上传失败</font>';
	        	data = eval('('+data+')');
	        	if(data.status == 0){
	        		//md5 = data.md5;
	        		//path保存
	        		info = "上传成功";
	        		fileCompleteNum=fileCompleteNum+1;
	        		var nodeId=$("#nodeId").val();
	        		callBack(nodeId,data.fileName,data.filePath);
	        		
	        	}
	        	//$('#' + file.id).find('.data').html(' - ' + info);
	        	uploadFileWaiting.close();
        	}
	    });
	 }
	);
	
	function callBack(nodeId,fileName,filePath){
		var win= top.index_frame.work_main;
		win.setNodeFile(nodeId,fileName,filePath);
		art.dialog.close();//关闭弹出框
		
	}
	
	function upload(){
		var len1 = $("#fileData").data('uploadify').queueData.queueLength;
		if(len1<1){
			$.alert('请选择上传的文件！');
			return;
		}
		$('#fileData').uploadify('upload','*');
	}
	
	</script>
</head>
<body>
  <div class="portlet portlet-border">
     <input type="hidden" id="nodeId" name="nodeId" value="${nodeId}" />
     <input type="hidden" name="fileName" value=""/> 
     <input type="hidden" name="filePath" value=""/> 
     <input type="hidden" name="parentPath" id="parentPath" value="${parentPath}"/> 
     
	        <div class="portlet-title">
	            <div class="caption">图书文件</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="control-label col-md-4">父目录：</label>
                                <div class="col-md-8">
                                	${parentName}
                                </div>
                            </div>
                        </div>
                         
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="control-label col-md-4"><i class="must">*</i>文件：</label>
                                <div class="col-md-8">
                                	<input type="hidden" id="uploadFile" name="uploadFile" value=""/>
                                    <input type="file" name="fileData" id="fileData" class="validate[required]" />
                                </div>
                            </div>
                        </div>
                         
                    </div>
                     <div class="row">
                         <div class="col-md-6">
                            <div class="form-group">
                                <div class="col-md-8 col-md-4">
                                	  <button type="button"  class="btn btn-mg blue"" onclick="upload();">提交</button>
                                	  <button type="button" class="btn btn-mg red" onclick="javascript:art.dialog.close();">取消</button>
                                </div>
                            </div>
                        </div>
                     </div>
                   
                   
                </div>
            </div>
    </div>
</body>
</html>