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
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
		<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
	<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css" />
<script type="text/javascript">
	$(function(){
		$('#form1').validationEngine('attach', {
			relative: true,
			overflownDIV:"#divOverflown",
			promptPosition:"bottomLeft",
			maxErrorsPerField:1,
			onValidationComplete:function(form,status){
				if(status){
					//updates();
					if($("#fileData").data('uploadify') !=undefined){
						var len1 = $("#fileData").data('uploadify').queueData.queueLength;
						if(len1 > 0){
							$('#fileData').uploadify('upload','*');
							
						}else if($('#logo').val()==null || $('#logo').val()==undefined){
							$.alert('请选择上传的文件！');
							return;
						}else{
							denglis();
						}
					}else{
					}
				}
			}
		});
		$("select[type=multiSelEle]").multipleSelect({
			multiple: true,
			multipleWidth: 140,
			width: "100%"
		});
		$("select[type=multiSelEle]").multipleSelect("refresh");
			
		
		 resty();
		 upload();
	});
	function resty(){
		var restype = $('#templateType').val();
		var div1=$('#meta_tab');
		div1.empty();
		var id = $("#id").val();
		var num1 = 0;
		 $('#form1').ajaxSubmit({
			url: '${path}/subject/subjectRes.action?restype='+restype,
			method: 'post',
			async:false,
			success:(function(data){
				var da = JSON.parse(data);
				var keys='';
				for(var key in da){
					++num1;
					if(num1==1){
						div1.append("<li class=\"active\" onclick=\"initRalations("+key+")\"><a href=\"\" data-toggle=\"tab\">"+da[key]+" </a></li>");
						keys =key;
					}else{
						div1.append("<li onclick=\"initRalations("+key+")\"><a href=\"\" data-toggle=\"tab\">"+da[key]+" </a></li>");
					}
				}
				 initRalations(keys);
			})
		});
	}
	function denglis(){
		$('#form1').ajaxSubmit({/* <input type="hidden" name="templateId" value="${subjectStore.template.id}" id="templateId"/> */
			url:'${path}/subject/subjectUpdSub.action?templateId='+$('#templateId').val(),
			method: 'post',
			success:(function(data){
				location.href = "${path}"+data;
			})
		});
	}
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
		        			denglis();
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
	
	function gotoSelect() {
		var posttype = $('#posttype').val();
		var id = $('#id').val();
		var type= document.getElementById("templateType").value;
		var operateFrom = '${operateFrom}';
		//$.openWindow("${path}/resOrder/addResource.action?orderId="+id+"&type="+type+"&operateFrom="+operateFrom+"&posttype="+posttype, '选择资源', 1200, 600);
		//$.openWindow("${path}/resOrder/addResourceonline.action?orderId="+id+"&type="+type+"&operateFrom="+operateFrom+"&posttype="+posttype, '选择资源', 1200, 500);
		  parent.parent.layer.open({
		    type: 2,
		    title: '选择资源',
		    closeBtn: true,
		    maxmin: true, //开启最大化最小化按钮
		    area: ['1200px', '600px'],
		    shift: 2,
		    content: "${path}/resOrder/addResourceonline.action?orderId="+id+"&type="+type+"&operateFrom="+operateFrom+"&posttype="+posttype  //iframe的url，no代表不显示滚动条
		});
	
	}
	
	function initRalations(key){
		var id = $("#id").val();
		window.onresize=findDimensions();
		var sizewidth=$('#relation').width();
		$('#data_div').width(sizewidth);
		//定义一datagrid
   		var _divId = 'data_div';
   		var _url = '${path}/resOrder/resourceList.action?orderId=' + id+'&restype='+key+'&posttype='+$('#posttype').val();
   		var _pk = 'objectId';
   		var _conditions = [];
   		var _sortName = '';
   		var _sortOrder = 'desc';
		var _colums = [
						<app:QueryListColumnTag />
						{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}
						];
		
		accountHeight();
   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	}
	
	$operate = function(value,rec){
		var opt= "<sec:authorize url="/subject/view.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
		return opt;
	};
	function detail(resId){
		var orderId = '${subjectStore.id}';
		//$.openWindow("${path}/resOrder/resourceDetail.action?orderId="+orderId+"&resId="+resId, '资源详细信息', 1050, 530);
		$.openWindow("${path}/resRelease/resOrder/resFileDetail.jsp?orderId="+orderId+"&resId="+resId+"&restype="+$('#res').val()+"&posttype="+$('#posttype').val(), '资源详细信息', 800, 530);
	}
	function clearResList(type) {
		var id = $('#id').val();
		var operateFrom = "${operateFrom}";
		if(type=='part'){
			var	resId = getChecked('data_div','objectId').join(',');
			if(resId == ""){
				$.alert('请选择要删除的资源！');
				return;
			}
			$.confirm('确定要删除所选资源吗？', function() {
				location.href = "${path}/subject/deleteResourceByDeleteType.action?orderId="
						+ id + "&resIds=" + resId + "&deleteType=" + "part" + "&operateFrom=" + operateFrom + "&posttype=" + $('#posttype').val();
			});
		}else{
			$.confirm('确定要清空资源吗？', function() {
				location.href = "${path}/subject/deleteResourceByDeleteType.action?orderId="
					+ id + "&resIds=" + "" + "&deleteType=" + "all" + "&operateFrom=" + operateFrom + "&posttype=" + $('#posttype').val();
			});
		}
	}
	
	function tolist(){
		this.location.href='${path}/subject/subjectList.jsp';
	}
	
	function importDir(){
		var id = $("#id").val();
		parent.parent.layer.open({
		    type: 2,
		    title: '资源快速挑选',
		    closeBtn: true,
		    maxmin: true, //开启最大化最小化按钮
		    area: ['1200px', '550px'],
		    shift: 2,
		    content: '${path}/subject/ksshaixuan.action?id='+id+'&posttype='+$('#posttype').val()  //iframe的url，no代表不显示滚动条
		    
		});
	}
	function modelSelect(id,operateFrom){
		var id = $('#id').val();
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
		return false;
	}
	//保存并上报
	function doSaveAnaSubmit(){
		var orderId = '${subjectStore.id }';
		var operateFrom = '${operateFrom}';
		var posttype = $('#posttype').val();
		var templateId = document.getElementById("templateId").value;
		$.ajax({
			url : "${path}/resOrder/canApply.action?orderId="+orderId+"&posttype="+posttype,
			type : "post",
			datatype : "text",
			success : function(data){
				if(data=="N"){
					$.alert("该主题库没有添加资源，无法上报！");
				}else{
					$.confirm('确定要提交审核吗？', function() {
						$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;正在处理中，请稍待。。。</div>',
							css: {
				                border: 'none',
				                padding: '20px',
				                backgroundColor: 'white',
				                textAligh:'center',
				            //    top:"50%",  
				                width:"300px",
				                opacity: .7
				               }
						});
						$('#form1').ajaxSubmit({
							url : _appPath + '/subject/resOrderWorkFlow/doSaveAndSubmit.action?wfTaskId=${wfTaskId}&posttypes='+$('#posttype').val()+'&operateFrom1='+operateFrom,
							type : 'post',
							datatype : 'text',
							success : function(returnValue) {
								if(returnValue == 'error'){
									$.alert("保存并提交失败");
								}else{
									$.alert("保存并提交成功");
								}
								$.unblockUI();
								if('${operateFrom}'=='TASK_LIST_PAGE'){
									window.location.href = "${path}/TaskAction/toList.action"; 
								}else{
									location.href = "${path}/subject/subjectList.jsp";
								}
								
							}
						});
					});
		
				}
			}
		});
		
	}
	//上报
	function toApply() {
		var objectId = '${subjectStore.id }';
		var posttype = $('#posttype').val();
		$.ajax({
			url : "${path}/resOrder/canApply.action?orderId="+objectId+"&posttype="+posttype,
			type : "post",
			datatype : "text",
			success : function(data){
				if(data=="N"){
					$.alert("该主题库没有添加资源，无法上报！");
				}else{
					$.confirm('确定要上报吗？', function() {
						$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;正在处理中，请稍待。。。</div>',
							css: {
				                border: 'none',
				                padding: '20px',
				                backgroundColor: 'white',
				                textAligh:'center',
				            //    top:"50%",  
				                width:"300px",
				                opacity: .7
				               }
						});
						$.ajax({
							url : _appPath + '/subject/doApply.action?objectId='+ objectId+'&posttype='+posttype,
							type : 'post',
							datatype : 'text',
							success : function(returnValue) {
								$.alert(returnValue);
								$.unblockUI();
								if('${operateFrom}'=='TASK_LIST_PAGE'){
									window.location.href = "${path}/TaskAction/toList.action"; 
								}else{
									location.href = "${path}/subject/subjectList.jsp";
								}
							}
						});
					});
				}
			}
		});
	}
	function query(logopath){
		$.openWindow(logopath);
	}
	var winWidth = 0;
	var winHeight = 0; 
	function findDimensions(){  //函数：获取尺寸
		//获取窗口宽度 
		if (window.innerWidth){
			winWidth = window.innerWidth; 
		} else if ((document.body) && (document.body.clientWidth)){
			winWidth = document.body.clientWidth; 
		} 
		//获取窗口高度 
		if (window.innerHeight){ 
			winHeight = window.innerHeight; 
		} else if ((document.body) && (document.body.clientHeight)){ 
			winHeight = document.body.clientHeight; 
		}
		//通过深入Document内部对body进行检测，获取窗口大小 
		if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth){ 
			winHeight = document.documentElement.clientHeight; 
			winWidth = document.documentElement.clientWidth; 
		} 
		var valueWidth = winWidth.toString();
// 		alert($('#data_div')+"   "+winWidth);
		$('#data_div').width(valueWidth);
	} 
	findDimensions(); 
	window.onresize=findDimensions; 
</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap ">
	<div class="panel panel-default">
    	<div class="panel-heading" id="div_head_t">
			<ol class="breadcrumb">
				<li class="active">资源发布</li>
				<li class="active">主题库</li>
				<li class="active">主题库修改</li>
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
		<input type="hidden" name="id" id="id" value="${subjectStore.id }"/>
		<input type="hidden" id="posttype" name="posttype" value="${posttype }">
		<div class="portlet-body">
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>名称 :</label>
							<div class="col-md-9">                               
								<input type="text" name="name" id="name" value="${subjectStore.name }" class="form-control text-input validate[required,ajax[validatesubjectNameAdd]]" />
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3">父主题 :</label>
							<div class="col-md-9">
								<select name="pid" id='pid' class='form-control'>
									<option value='0'>请选择</option>
									<c:forEach items="${lists }" var="lists">
										<option value="${lists.id }" <c:if test="${subjectStore.pid eq lists.id}">selected="selected"</c:if>>${lists.name}</option>
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
								<input type="text" name="nameEn" id="nameEn" value="${subjectStore.nameEn }"  class="form-control text-input validate[required,custom[onlyLetterSp]]" />
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>行业:</label>
							<div class="col-md-9">
								<app:select name="trade" id="trade" indexTag="trade" headName="----请选择----" headValue="" selectedVal="${subjectStore.trade}" requiredCheck="true"></app:select>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3">学科 :</label>
							<div class="col-md-9">
								<app:select name="subject" id="subject" indexTag="subject" headName="----请选择----" headValue="" selectedVal="${subjectStore.subject}"></app:select>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3">出版商 :</label>
							<div class="col-md-9">
								<app:select name="bookman" id="bookman" indexTag="presshose" headName="----请选择----" headValue="" selectedVal="${subjectStore.bookman}"></app:select>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3">资源类别 :</label>
							<div class="col-md-9">
								<app:select name="storeType" id="storeType" indexTag="kuCategory" headName="----请选择----" headValue="" selectedVal="${subjectStore.storeType}"></app:select>                             
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>语言类型 :</label>
							<div class="col-md-9">
								<app:select name="language" id="language" indexTag="relation_language" headName="----请选择----" headValue="" selectedVal="${subjectStore.language}" requiredCheck="true"></app:select>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label for="template.id" class="control-label col-md-3"><i class="must">*</i>模板名称：</label>
							<div class="col-sm-9">
		                      	<div class="input-group" style="width: 100%">
		                      		<input type="text" class="form-control validate[required]" value="${subjectStore.template.name }" name="template.name" id="templateName" readonly="readonly"/>
		                      		<input type="hidden" name="template.id" value="${subjectStore.template.id}" id="templateId"/>
		                      		<input type="hidden" name="template.type" value="${subjectStore.template.type}" id="templateType"/>
									<c:if test="${flag=='0' }">
									<span class="input-group-btn">
										<!-- <a id="menuBtn2" onclick="javascript:modelSelect();" class="btn btn-primary" role="button">模板选择</a>
										<a onclick="javascript:modelDetail('detail');"  class="btn btn-primary" role="button" style="margin-left: 3px;">详细</a>
										<a id="clearModel" onclick="javascript:clearModelValue();"  class="btn btn-primary" role="button" style="margin-left: 3px;">清空</a> -->
										<img src="${path }/appframe/main/images/select.png" alt="选择模板" title="选择模板" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelSelect();"/>
										<img src="${path }/appframe/main/images/detail.png" alt="详细" title="详细" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelDetail('detail');"/>
										<img src="${path }/appframe/main/images/clean.png" alt="清空" title="清空" style="cursor:pointer;margin-left: 3px;" onclick="javascript:clearModelValue();"/>
									</span>
									</c:if>
									<c:if test="${flag=='1'}">
									<span class="input-group-btn">
											<img src="${path }/appframe/main/images/detail.png" alt="详细" title="详细" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelDetail('detail');"/>
									</span>
									</c:if>
		                         </div>
							</div>
				  		</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>受众类型 :</label>
							<div class="col-md-9">
								<app:select name="audience" id="audience" indexTag="audience" headName="----请选择----" headValue="" selectedVal="${subjectStore.audience}" requiredCheck="true"></app:select>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>主题库分类 :</label>
							<div class="col-md-9">
								<app:select name="subLibclassify" id="subLibclassify" indexTag="ZTKtypes" headName="----请选择----" headValue="" selectedVal="${subjectStore.subLibclassify}" requiredCheck="true"></app:select>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>收录开始年限 :</label>
							<div class="col-md-9">
								<input type="text" name="collectionStart" id="collectionStart" class="form-control Wdate validate[required]"  value="${subjectStore.collectionStart }" onClick="WdatePicker({dateFmt:'yyyy',maxDate:'#F{$dp.$D(\'collectionEnd\')}',readOnly:true})"/>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3"><i class="must">*</i>收录结束年限 :</label>
							<div class="col-md-9">
								<input type="text" name="collectionEnd" id="collectionEnd" class="form-control Wdate validate[required]" value="${subjectStore.collectionEnd }" onClick="WdatePicker({dateFmt:'yyyy',minDate:'#F{$dp.$D(\'collectionStart\')}',readOnly:true})"/>
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
                                <div class="col-md-8" style="float: left;">
                                	<input type="hidden" id="format" name="commonMetaData.commonMetaDatas['format']" />
                                	<input type="hidden" id="logo" name="logo" value="${subjectStore.logo}">
                                    <input type="file" name="fileData" id="fileData"  class="form-control validate[required]"/>
                                    <c:if test="${subjectStore.logo !=null}">
                    					<a style="cursor:pointer;" onclick="query('${path}/${subjectStore.logo }');">预览</a>
               						</c:if> 
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
								<input type="text" name="keyword" id="keyword" value="${subjectStore.keyword }" style="width: 170%;display: inline;" class='form-control validate[maxSize[1000]]'/>(多个标签按','分割)
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-3">主题库简介 :</label>
							<div class="col-md-9">
								<textarea rows="3" cols="100" name="synopsis" id="synopsis"  class='form-control validate[maxSize[1000]]' style="width: 170%;display: inline;">${subjectStore.synopsis }</textarea>(字数不能大于1000个)
							</div>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-xs-offset-5 col-xs-7">
	            		<input id="tijiao" type="submit" value="提交" class="btn btn-primary"/> 
	            		<c:if test="${subjectStore.status=='3'}">
							<sec:authorize url="/subject/apply.action">
								<button type="button" class="btn btn-primary" onclick="doSaveAnaSubmit();">提交并上报</button>
							</sec:authorize>
						</c:if>
	            		<c:if test="${subjectStore.status=='0'}">
							<sec:authorize url="/subject/apply.action">
								<button type="button" class="btn btn-primary" onclick="toApply();">上报</button>
							</sec:authorize>
						</c:if>
	            		<input class="btn btn-primary" type="button" value="关闭" onclick="tolist();"/>
	            	</div>
				</div>
				
			</div>
			
		</div>
	</div>
	<div class="panel panel-default" style="height: 100%;">
		<div class="by-tab">
			<div class="form-group col-xs-10" style="width: 100%">
 				<a id="added" class="btn btn-primary" onclick="gotoSelect();" target=_self>常规挑选</a>
				<input class="btn btn-primary" type="button" value="快速挑选" onclick="importDir();"/>
				<input class="btn btn-primary" type="button" value="批量删除" onclick="clearResList('part');"/>
				<input class="btn btn-primary" type="button" value="清空资源" onclick="clearResList('all');"/>
				
			</div>
			<ul class="nav nav-tabs nav-justified" id="meta_tab"></ul>
			<div class="portlet">
				<div class="panel-body">
					<div class="tab-content">
        				<div class="tab-pane active" id="tab_1_1_1" style="width:100%">
							<div class="portlet" id="relation">
								<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form:form>
 	
</div>
			
</body>
</html>