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
	var parentPath = '${param.parentPath}';
	var nodeId = '${param.nodeId}';
	var objectId = '${param.objectId}';
	var MD5 = '${param.MD5}';
	var parentName = decodeURIComponent('${param.parentName}');
	$(function() {
		$('#parentName').html(parentName);
		parentPath =  encodeURI(parentPath);
		nodeId = encodeURI(nodeId);
		var isCompress = $('input[name="isCompress"]:checked').val();
		var url='${path}/publishRes/addFile.action;jsessionid=<%=session.getId()%>?parentPath='
			+ parentPath
			+ '&nodeId='
			+ nodeId
			+ '&objectId='
			+ objectId + '&MD5=' + MD5+"&title=''";
// 			&isCompress="+isCompress
// 		$('#isCompress').change(function(){
// 			isCompress = $('input[name="isCompress"]:checked').val();
// 			alert(isCompress);
// 		});
			//初始化上传组件
			$('#fileData')
					.uploadify(
							{
								'swf' : '${path}/appframe/plugin/uploadify/uploadify.swf',
								'uploader' : url,
								'debug' : false,
								'method' : 'post',
								//是否自动上传
								'auto' : false,
								'queueSizeLimit':1,
								'multi' : false,
								'buttonText' : '上传文件',
								'formData' : {'isCompress':"",'isCover':""},
								'removeCompleted' : false,
								'onUploadStart' : function(file) {
									uploadFileWaiting = $.waitPanel(
											'正在上传文件...', false);
								},
								'onSelect' : function(file) {
								},
						        onUploadProgress: function (file, bytesUploaded, bytesTotal, totalBytesUploaded, totalBytesTotal) {
						        	fileStatus = 1;
					            },
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
							                    $.alert("文件 ["+file.name+"] 类型不正确,系统只允许ZIP和RAR格式的文件！");
							                    break;
							            }
							        },
								'onUploadSuccess' : function(file, data,
										response) {
									var info = '<font color="red">上传失败</font>';
									data = eval('(' + data + ')');
									uploadFileWaiting.close();
									if (!data.isRepeat) {
										if (data.status == '-1') {
											$.alert("上传失败，请联系系统管理员");
										} else {
											var win = "";
											if($('#taskFlag').val()!=""){
												win = top.index_frame;
											}else{
												win = top.index_frame.work_main;
											}
											if(data.noCompress=='1'){
												if(data.ztreeJson!=""){
	// 												zNodes = eval('('+data.ztreeJson+')');
													win.initZtree(data.ztreeJson);
												}
											}else{
												//编两次，解两次
												var isCover = $('input[name="isCover"]:checked').val();
												if("true" != isCover){
													nodeId = decodeURIComponent(decodeURIComponent(nodeId));
													win.setNodeFile(data.nodeId,nodeId,data.fileName,"2",data.path,data.Md5,data.object);
												}
											}
// 											win.setNodeFile(data.nodeId,nodeId,data.fileName,"2",data.path,data.Md5,data.object);
											art.dialog.close();//关闭弹出框
										}
									} else {
										info = "资源重复，上传失败";
										$('#' + file.id).find('.data').html(' - ' + info);
									}
								}
							});
		});

		function upload() {
			var len1 = $("#fileData").data('uploadify').queueData.queueLength;
			if (len1 < 1) {
				$.alert('请选择上传的文件！');
				return;
			}
			var isCompress = $('input[name="isCompress"]:checked').val();
			var isCover = $('input[name="isCover"]:checked').val();
			$('#fileData').uploadify('upload', '*');
			$("#fileData").uploadify("settings", "formData", {'isCompress':isCompress,'isCover':isCover});
// 			$('#fileData').uploadifySettings('formData',{'isCompress':$('#isCompress').val()});
		}
	</script>
</head>
<body>
  <div class="portlet portlet-border">
  <input type="hidden" id="taskFlag"  name="taskFlag" value="${param.taskFlag}"/>
     <input type="hidden" name="fileName" value=""/> 
     <input type="hidden" name="filePath" value=""/> 
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-12">
	                        <div class="form-wrap">
	                         <label class="control-label col-md-2">父目录：</label>
		                         <div class="col-md-10">
		                            <div class="form-group">
		                                <div  id="parentName">
		                                	${param.parentName}
		                                </div>
		                            </div>
		                          </div>
	                         </div>
                        </div>
                    </div>
	                  <div class="row">
		                 <div class="col-md-12">
							<div class="form-wrap">
								<label class="control-label col-md-2">是否解压：</label>
								<div class="col-md-10">
									<div class="form-group">
									 <div >
										<input type="radio" id="isCompress" name="isCompress"
											value="true" class="radio-inline" /> 是
											<input type="radio" id="isCompress" name="isCompress" value="false"
											class="radio-inline"  checked="checked"/> 否
										</div>
									</div>
								</div>
							</div>
						</div>
	                 </div>
	                 <div class="row">
		                 <div class="col-md-12">
							<div class="form-wrap">
								<label class="control-label col-md-2">是否封面：</label>
								<div class="col-md-10">
									<div class="form-group">
									 <div >
										<input type="radio" id="isCover" name="isCover"
											value="true" class="radio-inline" /> 是
											<input type="radio" id="isCover" name="isCover" value="false"
											class="radio-inline"  checked="checked"/> 否
										</div>
									</div>
								</div>
							</div>
						</div>
	                 </div>
                    <div class="row">
                        <div class="col-md-12">
                          <div class="form-wrap">
                        	<label class="control-label col-md-2"><i class="must">*</i>文件：</label>
	                        <div class="col-md-10">
	                            <div class="form-group">
	                                <div >
	                                	<input type="hidden" id="uploadFile" name="uploadFile" value=""/>
	                                    <input type="file" name="fileData" id="fileData" class="validate[required]" />
	                                </div>
	                            </div>
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