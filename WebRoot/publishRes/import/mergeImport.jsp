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
		var i =0;
		$(function() {
			//初始化上传组件	BatchImportPublishAction.java
		    $('#fileData').uploadify({
		    	//‘uploadify.swf’	uploadify.swf 文件的相对路径
		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
		        //后台处理程序的相对路径
		        'uploader' : '${path}/publishRes/import/saveAndAnalyzeExcel.action?random='+parseInt(1000*Math.random())+"&publishType=${param.publishType}",
		        //如果设置为True则表示启用SWFUpload的调试模式
		        'debug' : false,
		        //是否自动上传
		        'auto':false,
		        //设置为true时，则可以上传多个文件
		        'multi':true,
		      	//队列最多显示的任务数量，如果选择的文件数量超出此限制，将会出发onSelectError事件。
				//注意此项并非最大文件上传数量，如果要限制最大上传文件数量，应设置uploadLimit。
		        'queueSizeLimit':3,
		        'uploadLimit':3,
				'simUploadLimit':3,
		        'queueID':'fileQueue',
		      	//浏览按钮的文本
		        'buttonText':'请选择三个excel',
		        //是否自动将已完成任务从队列中删除，如果设置为false则会一直保留此任务显示
		        'removeCompleted':false,
// 		        'fileDesc': "请选择zip或rar文件",
		        'fileTypeExts':'*.xls;*.xlsx',
		      	//当文件即将开始上传时立即触发
		        'onUploadStart': function (file) { 
		        	$("#file_upload").uploadify("settings", "formData", {});
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
		                    $.alert("文件 ["+file.name+"] 类型不正确,系统只允许jpg和png格式的文件！");
		                    break;
		            }
		        },
		      	//当文件上传成功时触发
		        'onUploadSuccess': function(file, data, response) {
		        	var info = '<font color="red">上传成功</font>';
	        		$('#' + file.id).find('.data').html(' - ' + info);
		        	i++;
		        	if(i==3){
		        		if(data!=""){
		        			window.location.href = '${path}/publishRes/import/downAnalyzeExcel.action?excelPath='+data;
		        		}else{
		        			var info = '<font color="red">上传失败请重新上传</font>';
		        			$('#' + file.id).find('.data').html(' - ' + info);
		        		}
		        	}
            	}
		    });
		});
		    	
		    	
// 		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf?random=' + (new Date()).getTime(),
// 		        'uploader' : '${path}/publishRes/import/saveAndAnalyzeExcel.action',
// 		        'debug' : false,
// 		        //是否自动上传
// 		        'auto':false,
// 		        'buttonText':'上传文件',
// // 		        'uploadLimit':2,
// 				'simUploadLimit':2,
// 		        //'method' : 'post',
// 		        'queueID':'fileQueue',
// 		        'queueSizeLimit':2,
// 		        //'preventCaching'  : true,
// 				'fileObjName':'file', 
// 		        'multi': true,
// 		        //'removeCompleted':false,
// 		        'fileTypeExts':'*.xls;*.xlsx',
// 		        'onUploadStart': function (file) {
// 		        	$("#fileData").uploadify("settings", "formData", {remark:$('#remark').val(),repeatType:$('input:radio[name="repeatType"]:checked').val()});  
//             	},
//             	  //返回一个错误，选择文件的时候触发
// 		        'onSelectError':function(file, errorCode, errorMsg){
// 		            switch(errorCode) {
// 		                case -100:
// 		                    $.alert("上传的文件数量已经超出系统限制的"+$('#fileData').uploadify('settings','queueSizeLimit')+"个文件！");
// 		                    break;
// 		                case -110:
// 		                    $.alert("文件 ["+file.name+"] 大小超出系统限制的"+$('#thumbFileData').uploadify('settings','fileSizeLimit')+"大小！");
// 		                    break;
// 		                case -120:
// 		                    $.alert("文件 ["+file.name+"] 大小异常！");
// 		                    break;
// 		                case -130:
// 		                    $.alert("文件 ["+file.name+"] 类型不正确,系统只允许ipg和RAR格式的文件！");
// 		                    break;
// 		            }
// 		        },
// 		        'onUploadSuccess': function(file, data, response) { 
// 		        	saveResWaiting.close();
// 		        	var info = '<font color="red">上传失败</font>';
// 		        	if(data == "0"){
// 		        		info = "上传成功，请稍后查询结果";
// 		        		$('#' + file.id).find('.data').html(' - ' + info);
// 		        		top.index_frame.work_main.freshDataTable('data_div');
// 		        		$.closeFromInner();
// 		        	}else{
// 		        		$.closeFromInner();
// 		        		$.alert('导入数据出错!');
		        		
// 		        	}
//             	},
//                 'onClearQueue' : function(queueItemCount) {  
//                     //alert(queueItemCount + ' file(s) were removed from the queue');  
//                 }, 
//                 'onInit' : function(instance){  
//                     //alert('The queue ID is ' + instance.settings.queueID);  
//                 },
//                 //上传完成  
//                 'onUploadComplete' : function(file) {  
//                     //alert('The file ' + file.name + ' finished processing.');  
//                 }, 
// 		    });
			
			//元数据提交
// 		    $('#coreData').validationEngine('attach', {
// 				relative: true,
// 				overflownDIV:"#divOverflown",
// 				promptPosition:"bottomLeft",
// 				maxErrorsPerField:1,
// 				binded:true,
// 				onValidationComplete:function(form,status){
// 					if(status){
// 						//表单验证通过,开始上传
// 						var files = $("#fileData").data('uploadify').queueData.files;
// 						var fileId = '';
// 						for (var n in files) {
// 							fileId = n.id;
// 						}
// 						if(fileId != ''){
// 							$('#fileData').uploadify('upload','*');
// 						}else{
// 							$.alert('请选择上传的文件！');
// 							return ;
// 						}
// 						saveResWaiting = $.waitPanel('正在导入资源...',false);
// 					}
// 				}
// 			});
// 		});
		
		
		 //表单验证通过,开始上传
		 function uploadFile(){
			var files = $("#fileData").data('uploadify').queueData.files;
			var fileId = '';
			var lengthTotal = $("#fileData").data('uploadify').queueData.queueLength;
			console.info(files);
			for (var n in files) {
				fileId = files.id;
			}
			if(fileId != '' && lengthTotal ==3){
				$('#fileData').uploadify('upload','*')
			}else{
				$.alert('请你继续选择上传文件！');
			}
		} 
		
		
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
                <input type="file" name="fileData" id="fileData"  class="validate[required]" />
                <div id="fileQueue"></div>
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-3 control-label text-right">备注：</label>
			<div class="col-sm-9">
				<textarea rows="2" class="form-control" name="remark" id="remark" style="width: 50%"></textarea>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-3 col-sm-9">
				<input type="hidden" id="module" name="module" />
               	<input id="tijiao" id="tijiao" type="button" value="提交" onclick="uploadFile();" class="btn btn-primary"/> 
               	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
            </div>
        </div>
	</form>
</div>
</body>
</html>