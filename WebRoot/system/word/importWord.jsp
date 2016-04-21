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
	        'uploader' : 'importWord.action;jsessionid=<%=session.getId()%>',
	        'debug' : false,
	        //是否自动上传
	        'auto':false,
	        'buttonText':'上传文件',
	        'uploadLimit':1,
	        'removeCompleted':false,
	        'fileTypeExts':'*.txt',
	        'onUploadStart': function (file) { 
	        	$("#fileData").uploadify("settings", "formData",{status:$("input:radio[name='status']:checked").val(),level:$("input:radio[name='level']:checked").val()}); 
        	},
	        'onUploadSuccess': function(file, data, response) {  
	        	if(data == "error"){
	        		info = "导入出错，请检查导入文件";
	        	}else{
		        	callback(response);
		        	$.closeFromInner();
	        	}
        	}
	    });
	});
	
	
	function callback(data){
		top.index_frame.freshDataTable('data_div');
		$.closeFromInner();
	}
	
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
	
	function downTemplete(){
		window.location.href = "downloadTemplete.action";
	}
	</script>
	<style type="text/css">
		.newStyle{
			position: relative;
		    min-height: 1px;
		    padding-left: 15px;
		    padding-right: 15px;
		    white-space:nowrap;
		    width: 15%; 
		    float: left;
		}
	</style>
</head>
<body>
	<div class="form-wrap">
			<div class="row form-wrap">
				<label class="col-sm-4 control-label text-right">选择文件</label>
				<div class="col-sm-8">
					<input type="hidden" id="format" name="commonMetaDatas['format']" />
                	<input type="file" name="fileData" id="fileData" class="validate[required]" />
				</div>
				
				<table align="center">
					<tr>
						<td>
							<label  class="col-xs-5 control-label text-right">状态：</label>
						</td>
						<td>
							<input type="radio" value="1" name="status" checked="checked"/>可用
							<input type="radio" value="0" name="status"/>禁用
						</td>
					</tr>		
					<tr>
						<td>
							<label  class="col-xs-5 control-label text-right">等级：</label>
						</td>
						<td>
						    <input type="radio" value="1" name="level" checked="checked"/>高
							<input type="radio" value="2" name="level"/>中
							<input type="radio" value="3" name="level"/>低
						</td>
					
					</tr>
			</table>		
			<div class="form-wrap">
				<div class="col-sm-offset-2 col-sm-8" align="center">
                	<input type="hidden" name="token" value="${token}" />
                	<input class="btn btn-primary" type="button" value="下载模板" onclick="javascript:downTemplete();"/>
                	<input id="tijiao" type="button" value="提交" class="btn btn-primary" onclick="save();"/> 
                	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
                </div>
            </div>
	</div>
</body>
</html>
