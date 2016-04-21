<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/appframe/common.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>编辑资源</title>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript" src="${path}/appframe/util/filterWordsResult.js"></script>
	<script type="text/javascript">
		var currentModule = '${module}';
		var currentType = '${type}';
		var cFileType = '';
		var md5 = '';//刚上传文件的md5值
		var hasFile = 0;
		var fileStatus = 0;
		var imgStatus = 0;
		var repeatCancel = 0;
		$(function() {		
			//初始化上传组件
		    $('#fileData').uploadify({
		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
		        'uploader' : '${path}/railway/uploadFileToTemp.action;jsessionid=<%=session.getId()%>',
		        'debug' : false,
		        //是否自动上传
		        'auto':true,
		        'multi':false,
		        'queueSizeLimit':1,
		        'fileTypeExts':'*.xml',
		        'buttonText':'选择文件',
		        'removeCompleted':false,
		        'onUploadStart': function (file) { 
		        	uploadFileWaiting = $.waitPanel('正在上传文件...',false);
            	},
		        'onSelect':function(file){
		       		cFileType = file.type;
		        },
		        onUploadProgress: function (file, bytesUploaded, bytesTotal, totalBytesUploaded, totalBytesTotal) {
		        	fileStatus = 1;
	            },
		        //返回一个错误，选择文件的时候触发
		        'onSelectError':function(file, errorCode, errorMsg){
		            switch(errorCode) {
		                case -100:
		                    $.alert("上传的文件数量已经超出系统限制的"+$('#fileData').uploadify('settings','queueSizeLimit')+"个文件！");
		                    break;
		                case -110:
		                    $.alert("文件 ["+file.name+"] 大小超出系统限制的"+$('#fileData').uploadify('settings','fileSizeLimit')+"大小！");
		                    break;
		                case -120:
		                    $.alert("文件 ["+file.name+"] 大小异常！");
		                    break;
		                case -130:
		                    $.alert("文件 ["+file.name+"] 类型不正确,系统只允许xml格式的文件！");
		                    break;
		            }
		        },
		        'onUploadSuccess': function(file, data, response) {  
		        	var info = '<font color="red">上传失败</font>';
		        	data = eval('('+data+')');
		        	if(data.status == 0){
		        		//path保存
		        		md5 = data.md5;
		        		$('#uploadFile').val(data.uploadFile);
		        		hasFile = 0;
		        		info = "解析成功";
		        	}
		        	$('#' + file.id).find('.data').html(' - ' + info);
		        	uploadFileWaiting.close();
            	}
		    });
			
		});
	</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap">
    <div class="by-tab">
         <div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">文件信息</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row" id="fileDiv">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="control-label col-md-4"><i class="must">*</i>文件：</label>
                                <div class="col-md-8">
                                	<input type="hidden" id="format" name="commonMetaData.commonMetaDatas['format']" />
                                    <input type="file" name="fileData" id="fileData" class="validate[required]" />
                                </div>
                            </div>
                        </div>
                    </div>
                   
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
<script type="text/javascript" src="${path}/bres/knowledgeTree.js"></script>
</body>
</html>