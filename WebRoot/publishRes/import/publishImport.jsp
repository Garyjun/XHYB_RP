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
			//初始化上传组件	BatchImportPublishAction.java
		    $('#fileData').uploadify({
		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
		        'uploader' : '${path}/publishRes/import/saveRes.action;jsessionid=<%=session.getId()%>?publishType='+$('#publishType').val(),
		        'debug' : false,
		        //是否自动上传
		        'auto':false,
		        'buttonText':'上传文件',
		        'uploadLimit':1,
		        'queueSizeLimit':1,
		        'removeCompleted':false,
		        'fileTypeExts':'*.xls',
		        'onUploadStart': function (file) { 
		        	$("#fileData").uploadify("settings", "formData", {remark:$('#remark').val(),repeatType:$('input:radio[name="repeatType"]:checked').val()});  
            	},
            	  //返回一个错误，选择文件的时候触发
		        'onSelectError':function(file, errorCode, errorMsg){
		            switch(errorCode) {
		                case -100:
		                	top.layer.msg("上传的文件数量已经超出系统限制的"+$('#fileData').uploadify('settings','queueSizeLimit')+"个文件！");
		                    break;
		                case -110:
		                	top.layer.msg("文件 ["+file.name+"] 大小超出系统限制的"+$('#thumbFileData').uploadify('settings','fileSizeLimit')+"大小！");
		                    break;
		                case -120:
		                	top.layer.msg("文件 ["+file.name+"] 大小异常！");
		                    break;
		                case -130:
		                	top.layer.msg("文件 ["+file.name+"] 类型不正确,系统只允许ipg和RAR格式的文件！");
		                    break;
		            }
		        },
		        'onUploadSuccess': function(file, data, response) { 
		        	saveResWaiting.close();
		        	var info = '<font color="red">上传失败</font>';
		        	if(data == "SUCCESS"){
		        		top.layer.msg('文件上传成功。数据正在导入中，请稍后！');
		        		top.index_frame.work_main.freshDataTable('data_div');
						top.layer.closeAll("iframe"); //关闭信息框
		        	}else{
		        		top.layer.msg('文件上传出错。请检查导入目录的文件！');
		        		
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
						if(fileId != ''){
							$('#fileData').uploadify('upload','*');
						}else{
							top.layer.msg('请选择上传的文件！');
							return ;
						}
						saveResWaiting = $.waitPanel('正在导入资源...',false);
					}
				}
			});
		});
		function downTemplete(){
			window.location.href = "${path}/publishRes/downloadTemplete.action?publishType="+$('#publishType').val();
		}
	</script>
</head>
<body>
<div class="form-wrap">
	<form action="#" id="coreData" class="form-horizontal">
		<div class="form-group">
			<label class="col-sm-3 control-label text-right">文件：<span class="required">*</span></label>
			<div class="col-sm-9">
			<input type="hidden" id="publishType" name="publishType" value="${param.publishType}"/>
				<input type="hidden" id="format" name="commonMetaData.commonMetaDatas['format']" />
                <input type="file" name="fileData" id="fileData" class="validate[required]" />
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-3 control-label text-right">重复策略：</label>
			<div class="col-sm-9">
				<label class="radio-inline">
					<input type="radio" name="repeatType" value="3" checked="checked"/> 忽略
				</label>
				<label class="radio-inline">
					<input type="radio" name="repeatType" value="1" /> 新版本
				</label>
				<label class="radio-inline">
					<input type="radio" name="repeatType" value="4" /> 元数据覆盖(文件增量)
				</label>
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-3 control-label text-right"></label>
			<div class="col-sm-9">
				<label class="radio-inline">
					<input type="radio" name="repeatType" value="2"/> 元数据增量(文件增量)
				</label>
			</div>
		</div>
<!-- 		<div class="form-group"> -->
<!-- 			<label class="col-sm-3 control-label text-right">来自加工任务：</label> -->
<!-- 			<div class="col-sm-9"> -->
<!-- 				<label class="radio-inline"> -->
<!-- 					<input type="radio" name="processTask" value="1"/> 是 -->
<!-- 				</label> -->
<!-- 				<label class="radio-inline"> -->
<!-- 					<input type="radio" name="processTask" value="0" checked="checked"/> 否 -->
<!-- 				</label> -->
<!-- 			</div> -->
<!-- 		</div> -->
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
				<sec:authorize url='/publishRes/downloadTemplate.action'>
				<input id="downTemplate"  type="button" value="下载Excel模版" class="btn btn-primary" onclick="downTemplete()"/> 
				</sec:authorize>
               	<input id="tijiao" id="tijiao" type="submit" value="提交" class="btn btn-primary"/> 
               	<input class="btn btn-primary" type="button" value="关闭 " onclick="top.layer.closeAll('iframe');"/>
            </div>
        </div>
	</form>
</div>
</body>
</html>