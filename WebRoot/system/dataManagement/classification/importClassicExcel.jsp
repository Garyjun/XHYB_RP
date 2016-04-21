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
		var url = "importClassicExcel.action";
/* 		if($("#objectId").val().length>0)
			url = "importKnowledgeExcel.action?objectId="+$("#objectId").val()+'&pid='+$("#pid").val()+"&xpath="+$("#xpath").val(); */
		//初始化上传组件
	    $('#fileData').uploadify({
	        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
	        'uploader' : url,
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
	        	info = "上传成功，请稍后查询结果";
	        	alert(info);
	        	$.closeFromInner();
	        	parent.location.href = "tbzy.jsp";
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
			$.blockUI({ message: '<h5>正在导入...</h5>' });
		}else{
			$.alert('请选择上传的文件！');
		}
	}
	
	function downTemplete(){
		window.location.href = "downloadTemplete.action?moudleName="+$("#moudleName").val();
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
                <input class="btn btn-primary" type="button" value="下载模板" onclick="javascript:downTemplete();"/>
                <input id="tijiao" type="button" value="提交" class="btn btn-primary" onclick="save();"/> 
                <input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
            </div>
            <input type="hidden" id="moudleName" value="<%=request.getParameter("moudleName") %>" />
            <input type="hidden" id="objectId" value="<%=request.getParameter("id")%>" />
			<input type="hidden" id="pid" value="<%=request.getParameter("pid")%>" />
			<input type="hidden" id="xpath" value="<%=request.getParameter("xpath")%>" />
        </div>
	</div>
</body>
</html>
