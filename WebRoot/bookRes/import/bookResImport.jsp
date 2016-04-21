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
		var saveResWaiting;
		$(function() {
			var libType = '${param.libType}';
			$('#module').val('${param.module}');
			$('#type').val('${param.type}');
			//初始化上传组件
		    $('#fileData').uploadify({
		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
		        'uploader' : '${path}/bres/import/saveRes.action',
		        'debug' : false,
		        //是否自动上传
		        'auto':false,
		        'buttonText':'上传文件',
		        'uploadLimit':1,
		        'removeCompleted':false,
		        'fileTypeExts':'*.xls',
		        'onUploadStart': function (file) { 
		        	var repeatRelevanceType = $('input:checkbox[name="repeatRelevanceType"]:checked').val();
		        	if(typeof(repeatRelevanceType) == 'undefined'){
		        		repeatRelevanceType = 0;
		        	}
		        	$("#fileData").uploadify("settings", "formData", {libType:libType,module:$('#module').val(),remark:$('#remark').val(),repeatType:$('input:radio[name="repeatType"]:checked').val(),repeatRelevanceType:repeatRelevanceType});  
            	},
		        'onUploadSuccess': function(file, data, response) { 
		        	saveResWaiting.close();
		        	var info = '<font color="red">上传失败</font>';
		        	data = eval('('+data+')');
		        	if(data.status == 0){
		        		info = "上传成功，请稍后查询结果";
		        		$('#' + file.id).find('.data').html(' - ' + info);
		        		top.index_frame.work_main.freshDataTable('data_div');
		        		$.closeFromInner();
		        	}else{
		        		$.closeFromInner();
		        		$.alert('导入数据出错!');
		        		
		        	}
            	}
		    });
			
			//元数据提交
		    $('#coreData').validationEngine('attach', {
				relative: true,
				overflownDIV:"#divOverflown",
				promptPosition:"bottomLeft",
				maxErrorsPerField:1,
				binded:true,
				onValidationComplete:function(form,status){
					if(status){
						//表单验证通过,开始上传
						var files = $("#fileData").data('uploadify').queueData.files;
						var fileId = '';
						for (var n in files) {
							fileId = files.id;
						}
						if(fileId != ''){$('#fileData').uploadify('upload','*');
						}else{
							$.alert('请选择上传的文件！');
							return;
						}
						saveResWaiting = $.waitPanel('正在导入资源...',false);
					}
				}
			});
		});
		function downTemplete(){
	//		window.location.href = "${path}/bookRes/import/downBookTemplete.action";
			window.location.href = "${path}/bres/downloadTempleteBook/book.action";
		}
	</script>
</head>
<body>
<div class="form-wrap">
	<form action="#" id="coreData" class="form-horizontal">
		<div class="form-group">
			<label class="col-sm-3 control-label text-right">文件：<span class="required">*</span></label>
			<div class="col-sm-9">
				<input type="hidden" id="format" name="commonMetaData.commonMetaDatas['format']" />
                <input type="file" name="fileData" id="fileData" class="validate[required]" />
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-3 control-label text-right">重复策略：</label>
			<div class="col-sm-9">
				<label class="radio-inline">
					<input type="radio" name="repeatType" value="1" checked="checked"/> 新建
				</label>
				<label class="radio-inline">
					<input type="radio" name="repeatType" value="2"/> 覆盖
				</label>
				<label class="radio-inline">
					<input type="radio" name="repeatType" value="3"/> 忽略
				</label>
			</div>
		</div>
<!-- 		<div class="form-group"> -->
<!-- 			<label class="col-sm-3 control-label text-right">重复关联策略：</label> -->
<!-- 			<div class="col-sm-9"> -->
<!-- 				<label class="checkbox-inline"> -->
<!-- 					<input type="checkbox" name="repeatRelevanceType" value="1" checked="checked"/> 自动关联 -->
<!-- 				</label> -->
<!-- 			</div> -->
<!-- 		</div> -->
		<div class="form-group">
			<label class="col-sm-3 control-label text-right">备注：</label>
			<div class="col-sm-9">
				<textarea rows="2" class="form-control" name="remark" id="remark"></textarea>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-3 col-sm-9">
				<input type="hidden" id="module" name="module" />
				<input id="downTemplate"  type="button" value="下载Excel模版" class="btn btn-primary" onclick="downTemplete()"/> 
               	<input id="tijiao" id="tijiao" type="submit" value="提交" class="btn btn-primary"/> 
               	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
            </div>
        </div>
	</form>
</div>
</body>
</html>