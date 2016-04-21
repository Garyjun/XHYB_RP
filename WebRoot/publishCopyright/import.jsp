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
	
	<script type="text/javascript">
		var cFileType = '';
		$(function() {
			console.dir(top.index_frame);
			//初始化上传组件
		    $('#fileData').uploadify({
		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
		        'uploader' : '${path}/copyright/importCopyright.action',
		        'debug' : false,
		        //是否自动上传
		        'auto':false,
		        'buttonText':'上传Excel文件',
		        'uploadLimit':1,
		        'removeCompleted':false,
		        'fileTypeExts':'*.xls',
		        'onUploadSuccess': function(file, data, response) {  
		        	var info = '<font color="red">上传失败</font>';
		        	data = eval('('+data+')');
		        	if(data.status == 0){
		        		info = "上传成功，请稍后查询结果";
		        		$('#' + file.id).find('.data').html(' - ' + info);
		        		$.closeFromInner();
		        	}else{
		        		$('#' + file.id).find('.data').html(' - ' + info);
		        	}
            	}
		    });
		});
		function doImport(){
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
		function importCopyright(){
			window.location.href = "${path}/copyright/downloadCopyrightExcel.action";
		}
	</script>
</head>
<body>
<div class="form-wrap">
	<form action="#" id="coreData" class="form-horizontal">
		<div class="form-group">
			<label class="col-sm-3 control-label text-right">文件：<span class="required">*</span></label>
			<div class="col-sm-9">
				<input type="hidden" id="format" name="commonMetaDatas['format']" />
                <input type="file" name="fileData" id="fileData" class="validate[required]" />
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-3 col-sm-9">
               	<input id="tijiao" type="button" onclick="doImport();" value="提交" class="btn btn-primary"/> 
               	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
               	<input id="downTemplate"  type="button" value="下载Excel模版" class="btn btn-primary" onclick="importCopyright()"/> 
            </div>
        </div>
	</form>
</div>
</body>
</html>