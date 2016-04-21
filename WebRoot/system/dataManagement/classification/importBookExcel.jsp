<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/appframe/common.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>添加订单</title>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript">
	$(function() {
		console.dir(top.index_frame);
		//初始化上传组件
	    $('#fileData').uploadify({
	        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
	        'uploader' : '${path}/system/book/importExcel.action',
	        'debug' : false,
	        //是否自动上传
	        'auto':false,
	        'buttonText':'上传文件',
	        'uploadLimit':1,
	        'removeCompleted':false,
	        'fileTypeExts':'*.xlsx',
	        'onUploadStart': function (file) { 
	        	$("#fileData").uploadify("settings", "formData");  
        	},
	        'onUploadSuccess': function(file, data, response) {  
	        	var info = '<font color="red">上传失败</font>';
	        	data = eval('('+data+')');
	        	if(data.status == 0){
	        		info = "上传成功，请稍后查询结果";
	        		alert(info);
	        		$.closeFromInner();
	        	}else{
	        		$('#' + file.id).find('.data').html(' - ' + info);
	        	}
        	}
	    });
	});
	
	function save(){
		//表单验证通过,开始上传
		var files = $("#fileData").data('uploadify').queueData.files;
		var fileId = '';
		for (var n in files) {
			fileId = files.id;
		}
		if(fileId != ''){
			$('#fileData').uploadify('upload','*');
		}else{
			$.alert('请选择上传的文件！');
		}
	}
	</script>
</head>
<body>
	<div class="form-wrap">
		<div class="row form-wrap">
			<label class="col-sm-4 control-label text-right">选择文件</label>
			<div class="col-sm-8">
				<input type="hidden" id="format" name="commonMetaDatas['format']" />
                <input type="file" name="fileData" id="fileData" class="validate[required]" />
			</div>
		</div>			
		<div class="form-wrap">
			<div class="col-sm-offset-4 col-sm-8">
                	<input type="hidden" name="token" value="${token}" />
                	<input id="tijiao" type="button" value="提交" class="btn btn-primary" onclick="save();"/> 
                	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
                </div>
            </div>
	</div>
</body>
</html>
