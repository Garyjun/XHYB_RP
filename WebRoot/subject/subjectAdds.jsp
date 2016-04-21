<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<title>添加专题</title>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/filterWordsResult.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
	<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css" />
<script type="text/javascript">
		function adds(){
			jQuery('#form1').submit();
		}
		$(function(){
			$('#form1').validationEngine('attach', {
				relative: true,
				overflownDIV:"#divOverflown",
				promptPosition:"bottomLeft",
				maxErrorsPerField:1,
				onValidationComplete:function(form,status){
					if(status){
						//addadd();
						if($("#fileData").data('uploadify') !=undefined){
							var len1 = $("#fileData").data('uploadify').queueData.queueLength;
							if(len1 > 0){
								$('#fileData').uploadify('upload','*');
								
							}else{
								$.alert('请选择上传的文件！');
								return;
							}
						}else{
						}
					}
				}
			});
			upload();
		});
		function upload(){
			var cFileType = '';
			var fileStatus = 0;
				//初始化上传组件
			    $('#fileData').uploadify({
			        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
			        'uploader' : '${path}/subject/uploadFileToTemp.action?logo='+$('#logo').val(),
			        'debug' : false,
			        //是否自动上传
			        'auto':false,
			        'multi':false,
			        'queueSizeLimit':1,
			        'fileTypeExts':'*.jpg;*.png',
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
			                    $.alert("文件 ["+file.name+"] 大小超出系统限制的"+$('#fileData').uploadify('settings','fileSizeLimit')+"KB大小！");
			                    break;
			                case -120:
			                    $.alert("文件 ["+file.name+"] 大小异常！");
			                    break;
			                case -130:
			                    $.alert("文件 ["+file.name+"] 类型不正确,系统只允许jpg和png格式的文件！");
			                    break;
			            }
			        },
			        'onUploadSuccess': function(file, data, response) {  
			        	var da = JSON.parse(data);
			        	if(da.typeStatus==0){
			        		if(da.status == 0){
			        			$('#logo').val(da.uploadFile);
			        			info = "上传成功";
			        			addadd();
			        		}
			        	}else{
			        		info = "请确认图片格式!";
			        		$('#' + file.id).find('.data').html(' - ' + info);
			        		uploadFileWaiting.close();
			        		return;
			        	}
			        	$('#' + file.id).find('.data').html(' - ' + info);
			        	uploadFileWaiting.close();
	            	}
			    });
		}
	 function addadd() {
		$('#form1').ajaxSubmit({
			url: '${path}/subject/subjectAddSub.action',
			method: 'post',
			success:(function(data){
				window.location.href = "${path}/subject/subjectEdit.action?id="+data+"&posttype="+$('#posttype').val();
			})
		});
	} 
	 $(function(){
			$("select[type=multiSelEle]").multipleSelect({
				multiple: true,
				multipleWidth: 140,
				width: "100%"
			});
			$("select[type=multiSelEle]").multipleSelect("refresh");
		});
	 function modelSelect(id,operateFrom){
		$.openWindow("${path}/subject/modelSelect.action?id="+id+"&operateFrom="+operateFrom+"&posttype="+$('#posttype').val(),'模板选择',800,500);
	}
	 function modelDetail(opFrom){
		var templateId = document.getElementById("templateId").value;
		if(templateId==""||templateId==null){
			$.alert("请选择模板后再查看详细信息！");
			return false;
		}
		$.openWindow(_appPath+"/resRelease/orderPublishTemplate/detail.action?id="+templateId+"&opFrom=resTemp",'模板详细信息',800,500);
			
	}
	function clearModelValue(){
		$("#templateName").attr('value','');
		$("#templateId").attr('value','');
		$("#templateType").attr('value','');
		return false;
	}
	function golist(){
		location.href = "${path}/subject/subjectList.jsp";
	}
	function resty(){}
</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap ">
	<div class="panel panel-default">
    	<div class="panel-heading" id="div_head_t">
			<ol class="breadcrumb">
				<li class="active">资源发布</li>
				<li class="active">主题库</li>
				<li class="active">主题库添加</li>
			</ol>
		</div>
 	</div>
<form:form action="#" id="form1" class="form-horizontal" name="form1"  modelAttribute="stores" method="post">
	<div class="portlet">
		<div class="portlet-title">
			<div class="caption">基本信息<a href="javascript:;" onclick="togglePortlet(this)">         
				<i class="fa fa-angle-up"></i></a>
			</div>
		</div>
		<input type="hidden" name="token" value="${token}" />
		<input type="hidden" name="posttype" id="posttype" value="${posttype}" />
		<div class="portlet-body">
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>名称 :</label>
							<div class="col-md-9">                               
								<input type="text" name="name" id="name" class="form-control validate[required,ajax[validatesubjectNameAdd]] text-input" />
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3">父主题 :</label>
							<div class="col-md-9">
								<select name="pid" id='pid' class='form-control'>
								<option value='0'>----请选择----</option>
								<c:forEach items="${lists}" var="lists">
									<option value='${lists.id}'>${lists.name }</option>
								</c:forEach>
								</select>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>名称(英文) :</label>
							<div class="col-md-9">                               
								<input type="text" name="nameEn" id="nameEn" class="form-control text-input validate[required,custom[onlyLetterSp]]" />
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>行业:</label>
							<div class="col-md-9">
								<app:select name="trade" id="trade" indexTag="trade" headName="----请选择----" headValue=""  requiredCheck="true"></app:select>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3">学科 :</label>
							<div class="col-md-9">
								<app:select name="subject" id="subject" indexTag="subject" headName="----请选择----" headValue=""></app:select>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3">出版商 :</label>
							<div class="col-md-9">
								<app:select name="bookman" id="bookman" indexTag="presshose" headName="----请选择----" headValue=""></app:select>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3">资源类别 :</label>
							<div class="col-md-9">
								<app:select name="storeType" id="storeType" indexTag="kuCategory" headName="----请选择----" headValue=""></app:select>                             
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>语言类型 :</label>
							<div class="col-md-9">
								<app:select name="language" id="language" indexTag="relation_language" headName="----请选择----" headValue="" requiredCheck="true"></app:select>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label for="template.id" class="control-label col-md-3"><i class="must">*</i>模板名称：</label>
							<div class="col-sm-9">
		                      	<div class="input-group">
		                      		<input type="text" class="form-control validate[required]" name="template.name" id="templateName" readonly="readonly"/>
									<input type="hidden" name="template.id" id="templateId"/>
		                      		<input type="hidden" name="template.type"id="templateType"/>
									<span class="input-group-btn">
									<img src="${path }/appframe/main/images/select.png" alt="选择模板" title="选择模板" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelSelect(-1);"/>
									<img src="${path }/appframe/main/images/detail.png" alt="详细" title="详细" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelDetail('detail');"/>
									<img src="${path }/appframe/main/images/clean.png" alt="清空" title="清空" style="cursor:pointer;margin-left: 3px;" onclick="javascript:clearModelValue();"/>
									</span>
		                         </div>
							</div>
				  		</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>受众类型 :</label>
							<div class="col-md-9">
								<app:select name="audience" id="audience" indexTag="audience" headName="----请选择----" headValue="" requiredCheck="true"></app:select>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>主题库分类 :</label>
							<div class="col-md-9">
								<app:select name="subLibclassify" id="subLibclassify" indexTag="ZTKtypes" headName="----请选择----" headValue="" requiredCheck="true"></app:select>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>收录开始年限 :</label>
							<div class="col-md-9">
								<input type="text" name="collectionStart" id="collectionStart" class="form-control Wdate validate[required]" onClick="WdatePicker({dateFmt:'yyyy',maxDate:'#F{$dp.$D(\'collectionEnd\')}',readOnly:true})"/>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>收录结束年限 :</label>
							<div class="col-md-9">
								<input type="text" name="collectionEnd" id="collectionEnd" class="form-control Wdate validate[required]" onClick="WdatePicker({dateFmt:'yyyy',minDate:'#F{$dp.$D(\'collectionStart\')}',readOnly:true})"/>
							</div>
						</div>
					</div>
				</div>
		         <div id="fakeFrame" class="container-fluid by-frame-wrap">
    				<div class="by-tab">
         				<div class="portlet portlet-border">
	       					<div class="portlet-title">
	            				<div class="caption">LOGO信息</div>
	        				</div>
            				<div class="portlet-body">
                				<div class="container-fluid">
                    				<div class="row" id="fileDiv">
                        				<div class="col-md-6">
                            				<div class="form-group">
                                				<label class="control-label col-md-4"><i class="must">*</i>上传LOGO：</label>
                                				<div class="col-md-8">
                                					<input type="hidden" id="format" name="commonMetaData.commonMetaDatas['format']"/>
                                    				<input type="file" name="fileData" id="fileData" class="form-control validate[required]"/>
                                					<input type="hidden" id="logo" name="logo">
                                				</div>
                            				</div>
                        				</div>
                    				</div>
                				</div>
            				</div>
        				</div>
    				</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3">关键词 :</label>
							<div class="col-md-9">
								<input type="text" name="keyword" id="keyword"  style="width: 170%;display: inline;" class='form-control validate[maxSize[1000]]'/>(多个标签按英文','分割)
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3">主题库简介 :</label>
							<div class="col-md-9">
								<textarea rows="3" cols="100" name="synopsis" id="synopsis"  class='form-control validate[maxSize[1000]]' style="width: 170%;display: inline;"></textarea>(字数不能大于1000个)
							</div>
						</div>
					</div>
				</div>
			</div>
				<div style="text-align: center;">
					<input type="button" class="btn btn-lg red" value="下一步" onclick="adds();">
					<input type="reset" class="btn btn-lg blue" value="重置">
					<input type="button" class="btn btn-lg blue" onclick="golist();" value="返回">
				</div>
		</div>
	</div>
</form:form>
 	
</div>
</body>
</html>