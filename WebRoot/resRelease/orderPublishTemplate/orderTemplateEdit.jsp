<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/jscolor/jscolor.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript">
	function returnList(){
		location.href = "${path}/resRelease/orderPublishTemplate/orderTemplateList.jsp";
	}
	//资源平台类型
	var platformId = ${platformId};
	var resType = '${resType}';
	$(document).ready(function(){
		 $('#hi').css("display", "none");
		 
		 $('#metaDatasCode').css("display", "none");
		$("[data-toggle='popover']").popover();
		$('#form1').validationEngine('attach', {
			relative: true,
			overflownDIV:"#divOverflown",
			promptPosition:"topRight",//验证提示信息的位置，可设置为："topRight", "bottomLeft", "centerRight", "bottomRight"
			maxErrorsPerField:1,
			onVaidationComplete:function(form,status){
				if(status){
					checkName();
				}
			}
		});
		
		/* $.get("${path}/resRelease/orderPublishTemplate/getSysDir.action?id="+$("#templateId").val(),function(data){
			var json =  eval("(" + data + ")");
			if(platformId==1){
				var fileTypeList = $("#fileTypeList");
				var fileList = json.list.split(",");
				var listValueArray;
				if(json.value)
					listValueArray = json.value.split(",");
				for(var i=0; i<fileList.length; i++){
					var label = $("<label></label>");
					label.text(fileList[i]).addClass("checkbox-inline form-group");
					label.appendTo(fileTypeList);
					var checkbox = $("<input type='checkbox' name='fileType' class='validate[required]'></input>");
					checkbox.val(fileList[i]);
					if(listValueArray&&jQuery.inArray(fileList[i], listValueArray)!=-1)
						checkbox.attr("checked",true);
					checkbox.appendTo(label);
				}
			}
			if(platformId==2){
				var flag = '${flag}';
				var publishTypeList = $("#publishTypeList");
				if(json.publishType==""){
					$.alert("未配置资源类型，请到数据字典中进行配置！");
						return false;
				}
				var publishList = json.publishType.split(",");
				
				var listValueArray;
				if(json.value)
					listValueArray = json.checkedType.split(",");
				for(var i=0; i<publishList.length; i++){
					var label = $("<label></label>");
					label.text(publishList[i]).addClass("radio-inline form-group");
					label.appendTo(publishTypeList);
					if(flag==false){
						if(i==0){
							var radio = $("<input type='radio' checked='true' path='resourceType' class='resourceType' name='resourceType' value='"+publishList[i]+"'></input>");
						}else{
							var radio = $("<input type='radio' path='resourceType' class='resourceType' name='resourceType' value='"+publishList[i]+"'></input>");
						}
					}else{
						var radio = $("<input type='radio' path='resourceType' class='resourceType' name='resourceType' value='"+publishList[i]+"'></input>");
					}
					radio.val(publishList[i]);
					if(listValueArray&&jQuery.inArray(publishList[i], listValueArray)!=-1){
						radio.attr("checked",true);
					}
					radio.appendTo(label);
				}
				createCheckBox();
				
				$(".resourceType"). change(function(){
					createCheckBox();
				});	
			}
		}); */
		
		//资源类型	
		var metadatasDesc = '${metadatasDesc}';
		//alert("元数据项:" + metadatasDesc);
		if(metadatasDesc){
			$("#metadatas").val(metadatasDesc);
		}
		//发布内容
		 $.get("${path}/resRelease/orderPublishTemplate/getAppInfo.action",function(data){
			var result = JSON.parse(data);
			var supplies = result.supplies;
			var metaInfo = result.metaInfo;
			var metaArray = "${paramsTempEntity.metaInfo}";
			//var count = 0;
		 for(var meta in metaInfo){
			var label = $("<label class='checkbox-inline form-group'></label>");
			label.appendTo($("#publistContentList"));
			var checkbox = $("<input type='checkbox' name='metaInfo' class='validate[required]' value="
					+ meta + ">" + meta + "</input>");
			if(metaArray&&metaArray.indexOf(meta)!=-1){
				checkbox.attr("checked","true");
			}
			checkbox.appendTo(label);
		} 
		$("[value='资源文件']").attr("checked",true).attr("readonly",true).attr("onclick","return false;");
		$("[value='元数据Excel']").attr("checked",true).attr("readonly",true).attr("onclick","return false;");
		//$("[value='文件清单Xml']").attr("checked",true).attr("readonly",true).attr("onclick","return false;");
		createResourceType();
			
			if($('input:radio[name="publishType"]:checked').val().indexOf('Line')){
				metaArray='元数据Excel';
				$("#metadataGroup").css("display", "block");
			} 
		}); 
		//查询发布途径
		$.get("${path}/resRelease/orderPublishTemplate/getposttypes.action",function(data){
			var result = JSON.parse(data);
			var posttype = result.posttype;
			var posttypes = "${paramsTempEntity.posttype}";
			//var count = 0;
		 for(var meta in posttype){
			var label = $("<label class='radio-inline form-group'></label>");
			label.appendTo($("#posttypess"));
			var checkbox = $("<input type='radio' name='posttype' class='validate[required]' value="
					+ meta + ">" + posttype[meta] + "</input>");
			if(posttypes&&posttypes.indexOf(meta)!=-1){
				checkbox.attr("checked","true");
			}
			if(posttypes=="2"){
				$('#offlineShow').hide();
			}
			checkbox.appendTo(label);
		} 
		
		});
		
		offlineShow();
		$("#posttypess").change(function(){
			offlineShow();
		});
		
		function createCheckBox(){
			var resourceType = $("input[name='resourceType']:checked").val();
			if($(".label2")){
				$(".label2").remove();
			}
			$.ajax({
 				url: '${path}/resRelease/orderPublishTemplate/getResourType.action?name='+ encodeURI(resourceType) +"&id="+$("#templateId").val(),
 				type: 'get',
 				datatype: 'text',
 				success: function(data){
 					var json =  eval("(" + data + ")");
 					if(json.list==""){
 						$.alert("未配置["+resourceType+"]的资源目录，请到资源目录中进行配置！");
 						return false;
 					}
 						var fileList = json.list.split(",");
 						if(json.value)
 							var listValueArray = json.value.split(",");
 						for(var i=0; i<fileList.length; i++){
 							var label = $("<label class='label2'></label>");
 							label.text(fileList[i]).addClass("checkbox-inline form-group");
 							label.appendTo(fileTypeList);
 							var checkbox = $("<input type='checkbox' class='fileType validate[required]' value='"+fileList[i]+"' name='fileType'></input>");
 							checkbox.val(fileList[i]);
 							if(listValueArray&&jQuery.inArray(fileList[i], listValueArray)!=-1)
 								checkbox.attr("checked",true);
 							checkbox.appendTo(label);
 						}
 				}
				
 			});
		}
		
		//图片水印
	    $('#fileData').uploadify({
	        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
	        'uploader' : '${path}/resRelease/orderPublishTemplate/uploadImage.action?name='+$("#templateName").val(),
	        'debug' : false,
	        //是否自动上传
	        'auto':true,
	        'buttonText':'上传文件',
	        'uploadLimit':1,
	        'removeCompleted':false,
	        'fileTypeExts':'*.jpg;*.png',
	        'onUploadStart': function (file) { 
	        	$("#fileData").uploadify("settings", "formData");  
        	},
	        'onUploadSuccess': function(file, data, response) {  
				if(data!="error"){
					$("#imgWaterMarkURL").val(data);
				}else{
					$.alert("上传失败");
				}
        	}
	    });
		if(platformId==1){
			if($("input[name='resourceType']:checked").val()=="book")
				$("#bookType").show();
			else
				$("#bookType").hide();
			$(".changeResouce").change(function(){
				var resourceType = $("input[name='resourceType']:checked").val();
				if(resourceType=="book"){
					$("#bookType").show();
				}else{
					$("#bookType").hide();
				}
			});	
		}else{
		}
		//图片水印类型
		if($("input[name='waterMarkType']:checked").val()=="文字"){
			$("#imgTextWaterMark").show();
			$("#imgWaterMark").hide();
		}else if($("input[name='waterMarkType']:checked").val()=="图片"){
			$("#imgTextWaterMark").hide();
			$("#imgWaterMark").show();
		}else{
			$("input[name='waterMarkType']:eq(1)").attr("checked","checked"); 
			$("#imgTextWaterMark").show();
			$("#imgWaterMark").hide();
		}
		$(".imgWMType").change(function(){
			var imgWMType = $("input[name='waterMarkType']:checked").val();
			if(imgWMType=="文字"){
				$("#imgTextWaterMark").show();
				$("#imgWaterMark").hide();
			}else{
				$("#imgTextWaterMark").hide();
				$("#imgWaterMark").show();
			}
		});
		
		//加水印文件
		
		
		$("#imageExtension").change(function(){
			if($(this).attr("checked")){
				$("#imgTypeDiv").css("display","block");
				$("#imgInfo").css("display","block");
				$("#showImageButton").css("display","block");
			}
			else{
				$("#imgTypeDiv").css("display","none");
				$("#imgInfo").css("display","none");
				$("#showImageButton").css("display","none");
			}
		});
		
		$("#videoExtension").change(function(){
			if($(this).attr("checked"))
				$("#videoTypeDiv").css("display","block");
			else
				$("#videoTypeDiv").css("display","none");
		});
		
		$("#textExtension").change(function(){
			if($(this).attr("checked"))
				$("#textTypeDiv").css("display","block");
			else
				$("#textTypeDiv").css("display","none");
		});
		
		//加水印文件类型
		var waterMarkFileType = $("#waterMarkFileType").val();
		if(waterMarkFileType.indexOf("image")!=-1)
			$("#imageExtension").attr("checked","checked");
		else{
			$("#imgTypeDiv").css("display","none");
			$("#imgInfo").css("display","none");
			$("#showImageButton").css("display","none");
		}
		if(waterMarkFileType.indexOf("video")!=-1)
			$("#videoExtension").attr("checked","checked");
		else
			$("#videoTypeDiv").css("display","none");
		if(waterMarkFileType.indexOf("text")!=-1)
			$("#textExtension").attr("checked","checked");
		else
			$("#textTypeDiv").css("display","none");
		
		//水印类型
		var waterMarkType = "${paramsTempEntity.waterMarkType}";
		if(waterMarkType.indexOf("文字")!=-1){
			$('input:radio[value="文字"]').attr("checked","checked");
			$("#imgTextWaterMark").show();
			$("#imgWaterMark").hide();
		}
		else if(waterMarkType.indexOf("图片")!=-1){
			$('input:radio[value="图片"]').attr("checked","checked");
			$("#imgTextWaterMark").hide();
			$("#imgWaterMark").show();
		}
		//集成方列表和发布内容
		//getAppInfo();
	});
	
	
	//图片文字水印预览
	function showImage(type){
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
		var waterType = $(".imgWMType:checked").val();
		//图片水印
		var waterPos = "";
		var fileType = "";
		if(type=="image"){
			waterPos = $("#imgWaterMarkPos").val();
			fileType = "image";
		}else if(type=="video"){
			waterPos = $("#videoWaterMarkPos").val();
			fileType = "video";
		}else if(type=="word"){
			waterPos = $("#wordWaterMarkPos").val();
			fileType = "image";
		}
		//对水印图片路径编码
		var imgWaterMarkURL = encodeURI($("#imgWaterMarkURL").val());
		if(waterType=="图片"){
			if($("#imgWaterMarkURL").val().length==0){
				$.alert("请上传水印图片后再点预览!");
				$.unblockUI();
				return;
			}else{
				$.get("${path}/resRelease/orderPublishTemplate/showImage.action",{
					waterMarkPos:waterPos,
					waterMarkTextSize:$("#waterMarkTextSize").val(),
					waterMarkColor:$("#waterMarkColor").val(),
					imgWaterMarkURL:imgWaterMarkURL,
					waterMarkTextFont:$("#waterMarkTextFont").val(),
					waterMarkOpacity:$("#imgWaterMarkOpacity").val(),
					waterMarkTextBold:$("input[name='waterMarkTextBold']:checked").val(),
					hight:$("#imgHeight").val(),
					width:$("#imgWidth").val(),
					waterType:"img",
					waterFileType:fileType
				},function(data){
					$.unblockUI();
					if(type=="image"){
						var url = "${path}/docviewer/jsp/image.jsp?mark=1&filePath="+ data + "&math=" + Math.random();
					}else if(type=="video"){
						var url = "${path}/docviewer/jsp/video.jsp?mark=1&filePath="+ data + "&math=" + Math.random();
					}else if(type=="word"){
						//未实现
					}
					$.openWindow(url,"预览",1000,600);
				});
			}
		}else{//文字水印
			if($("#waterMarkText").val().length==0){
				$.alert("请输入水印文字后再点预览!");
				$.unblockUI();
				return;
			}else{
				$.get("${path}/resRelease/orderPublishTemplate/showImage.action",{
					waterMarkPos:waterPos,
					waterMarkTextSize:$("#waterMarkTextSize").val(),
					waterMarkColor:$("#waterMarkColor").val(),
					waterMarkText:encodeURI($("#waterMarkText").val()),
					waterMarkTextFont:$("#waterMarkTextFont").val(),
					waterMarkOpacity:$("#wordWaterMarkOpacity").val(),
					waterMarkTextBold:$("input[name='waterMarkTextBold']:checked").val(),
					hight:$("#imgHeight").val(),
					width:$("#imgWidth").val(),
					waterType:"word",
					waterFileType:fileType
				},function(data){
					$.unblockUI();
					if(type=="image"){
						var url = "${path}/docviewer/jsp/image.jsp?mark=1&filePath="+ data + "&math=" + Math.random();
					}else if(type=="video"){
						var url = "${path}/docviewer/jsp/video.jsp?mark=1&filePath="+ data + "&math=" + Math.random();
					}else if(type=="word"){
						//未实现
					}
					$.openWindow(url,"预览",1000,600);
				});
			}
		}
		
	}
	
	function save(){
		var waterMarkFileType = "";
		if($("#imageExtension").attr("checked")){
			waterMarkFileType += "image,";
		}
		if($("#videoExtension").attr("checked")){
			waterMarkFileType += "video,";
		}
		if($("#textExtension").attr("checked")){
			waterMarkFileType += "text";
		}
		$("#waterMarkFileType").val(waterMarkFileType);
		var waterType = $(".imgWMType:checked").val();
		if(waterType=="图片"){
			$("#imgWaterMarkPos").val($("#imgWaterMarkPos").val());
			$("#imgWaterMarkOpacity").val($("#imgWaterMarkOpacity").val());
		}else{
			$("#wordWaterMarkPos").val($("#wordWaterMarkPos").val());
			$("#wordWaterMarkOpacity").val($("#wordWaterMarkOpacity").val());
		}
		
		
		$("#form1").submit();
	}
	
		function checkName(){
			var templateName = $("#templateName").val();
			//先判断选了 加水印文件类型
			var waterFile = $('input[name="waterFile"]:checked').val();
			if(waterFile == '图片' || waterFile == '视频'){
				//然后判断图片和文字水印类型的必填项  因为校验有限制 所以自己判断  不用validation
				var waterType = $('input[name="waterMarkType"]:checked').val();
				if(waterType == '图片'){
					var imgWaterMarkURL = $('#imgWaterMarkURL').val();
					if(imgWaterMarkURL == ''){
						$.alert("水印类型为图片，请上传图片");
						return;
					}
				}
				
				if(waterType == '文字'){
					var waterMarkText = $('#waterMarkText').val();
					if(waterMarkText == ''){
						$.alert("水印类型为文字，请填写水印文字");
						return;
					}
				}
			}
			
			
			
			$.ajax({
				url: '${path}/resRelease/orderPublishTemplate/checkTemplateName.action?templateName='+templateName+"&templateId="+'${templateId}',
				type: 'post',
				datatype: 'text',
				success: function(data){
					if(data=="1"){
						$.alert("已经存在名称为["+templateName+"]的模板，请修改！");
						return;
					}else{
						save();
					}
// 					return true; 
				}
			});
	}
	
	function getAppInfo(){
		$.get("${path}/resRelease/orderPublishTemplate/getAppInfo.action",function(data){
			var result = JSON.parse(data);
			var supplies = result.supplies;
			var metaInfo = result.metaInfo;
			if(Object.keys(supplies).length==0){
				$.alert("请先配置集成方数据！");
				return;
			}
			if(Object.keys(metaInfo).length==0){
				$.alert("请先配置发布内容数据！");
				return;
			}			
			for(var supply in supplies){
				var option = $("<option value=" + supply + ">" + supplies[supply] + "</option>");
				option.appendTo($("#supplier"));
			};
			var supplyId = "${paramsTempEntity.supplier}";
			if(supplyId)
				$("#supplier").val(supplyId);
			
			var metaArray = "${paramsTempEntity.metaInfo}";
			for(var meta in metaInfo){
				var label = $("<label class='checkbox-inline form-group'></label>");
				label.appendTo($("#publistContentList"));
				var checkbox = $("<input type='checkbox' name='metaInfo' class='validate[required]' value="
						+ meta + ">" + meta + "</input>");
				if(metaArray&&metaArray.indexOf(meta)!=-1)
					checkbox.attr("checked","true");
				checkbox.appendTo(label);
			};
		});
	}
	
	
	function showMetadatasTree(){
		//var resType = $("#resType").find("option selected").val();
		
		//var resTypeId = $(".resourceType:checked").val();
		
		var resTypeId =''; 
		$("input[name='resourceType']:checkbox").each(function(){
			if($(this).attr("checked")){
				resTypeId += $(this).val()+","
            }
		})
		//alert(resTypeId);
		//alert(id);
		if(resTypeId){
			$.openWindow("${path}/resRelease/orderPublishTemplate/getMetaDatasTree.action?resTypeId=" + resTypeId, '元数据项选择', 600, 400);
		}else{
			$.alert("请选择资源类型");
			return;
		}
	}
	
	
	function createResourceType(){
		var resTypeRadio = $("#resourceType");
		var checkedResType = '${checkedResType}';
		if(resType!=undefined&&resType!=""){
			var types =  resType.split(",");
			//resTypeRadio.addClass("radio-inline form-group");
			var count = 0;
			for(var i=0;i<types.length;i++){
				//alert(types[i]);
				var label = $("<label class='checkbox-inline form-group'></label>");
				label.appendTo(resTypeRadio);
				var type = types[i].split(":");
				//alert("type0:" + type[0] +"   type1:" + type[1]);
				var radio = $("<input type='checkbox' path='resourceType' onclick='isCleanMetadatas()' class='resourceType' name='resourceType' value='"+type[0]+"'>"+type[1]+"</input>");
				/* if(i==0){
					radio.attr("checked","true");
				} */
				//var str = type[0] + ":" + type[1];
				
				/*  var val=$('input:radio[name="resourceType"]:checked').val();
		            if(val==null){
		                if(count==0){
		    				radio.attr("checked","true");
		    			} 
		            } */
		        var checkedResTypes =  checkedResType.split(",");
		        for(var j=0;j<checkedResTypes.length;j++){
		        	if(checkedResTypes[j]&&type[0]==checkedResTypes[j]){
						radio.attr("checked",true);
					}	
		        }
				
				count++;
				radio.appendTo(label);
			}
		}
		//将选中的资源类型保存起来
		$('#selectedVal').val($("input[name='resourceType']:checked").val());
	}
	
	function offlineShow(){
		var publishType = $('input:radio[name="posttype"]:checked').val();
		if(publishType=="1"){
			$("#offlineShow").show();
		}else if(publishType=="2"){
			$("#offlineShow").hide();
			$("input:radio[name='publishType']").eq(0).attr("checked","checked");
		}
	}
	
	function isCleanMetadatas(){
		var thisVal = $("input[name='resourceType']:checked").val();
		var beforeVal = $('#selectedVal').val();
		if(thisVal == beforeVal){
			return;
		}else{
			$("#metadatas").val("");//将数据项清空
			$("#selectedVal").val(thisVal);//将本次选择的值保存
		}
	}

	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<ul class="page-breadcrumb breadcrumb">
			<li><span>发布模板编辑</span></li>
		</ul>
		<form:form action="${path}/resRelease/orderPublishTemplate/save.action" name="form1" id="form1" modelAttribute="paramsTempEntity" method="post" class="form-horizontal" role="form">
		<div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">模板信息</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                	<div class="form-group">
	                    <div class="row">
							<label class="col-xs-1 control-label text-right"><span class="required">*</span>模板名称：</label>
							<div class="col-xs-5">
								<form:input path="name" id="templateName" class="form-control validate[required] text-input"/>
								<input type="hidden" id="templateId" value="${templateId}" />
							</div>
							<label class="col-xs-1 control-label text-right"><span class="required">*</span>启用状态：</label>
							<div class="col-xs-2 radio-list">
								<label class="radio-inline form-group">
									<form:radiobutton path="status" value="1" checked="true"/>启用
								</label>
								<label class="radio-inline form-group">
									<form:radiobutton path="status" value="0"/>禁用
								</label>
							</div>	
	                     </div>
                     </div>
                    <div class="form-group">
	                    <div class="row">
	                    	<label class="col-xs-1 control-label text-right"><span class="required">*</span>发布途径：</label>
		                    <div class="col-xs-4">
		                    	<div class="radio-list" id="posttypess" class='form-control validate[required]'></div> 
						 	</div>
					 	</div> 
 					</div>
                    <div class="form-group">
	                    <div class="row">
 							<label class="col-xs-1 control-label text-right"><span class="required">*</span>发布方式：</label>
			                    <div class="col-xs-2 radio-list" id="publishType">
									<label class="radio-inline form-group">
										<form:radiobutton path="publishType" value="onLine" checked="true"/>线上发布
									</label>
									<label class="radio-inline form-group" id="offlineShow">
										<form:radiobutton path="publishType" value="offLine"/>线下发布
									</label>
								</div>	
								<%-- <div id="urlShow" style="display:none;">
			                     <label class="col-xs-1 control-label text-right"><span class="required">*</span>url：</label>
			                     <div class="col-xs-4">
			                    	<form:input path="url" id="url"  class="form-control validate[required] text-input"/>
	 					 	 	 </div> 
 					 	 	 </div> --%>
				 	 	 </div>
			 	 	</div>
                    <div class="form-group">
	                    <div class="row">
	                    	<label class="col-xs-1 control-label text-right"><span class="required">*</span>资源类型：</label>
		                    <div class="col-xs-4">
		                    	<div class="checkbox-list" id="resourceType" class='form-control validate[required]'></div> 
						 	</div>
					 	</div> 
 					</div>
 					
 					
                     
                	<div class="form-group" id="hi">
	                    <div class="row">
	                    	<label class="col-xs-1 control-label text-right"><span class="required">*</span>发布内容：</label>
	                    	<div class="col-xs-11">
								<div class="checkbox-list" id="publistContentList" class='form-control validate[required]'>
	                            </div>			
							</div>
	                    </div>
	                </div>
	                <div class="form-group" id="metadataGroup" style="display:none;">
                     <div class="row" id="metadataRowDiv">
						<label class="col-xs-1 control-label text-right"><span class="required">*</span>定制元数据项：</label>
						<div class="col-xs-8">
						<textarea id="metadatas" readonly="true" class="form-control validate[required]" style="left;" rows="5" cols="3" class="form-control" style="left;"></textarea>
							<%-- <input type="hidden" name="metaDatasCode" id="metaDatasCode" value="${paramsTempEntity.metaDatasCode}"/> --%>
							<textarea name="metaDatasCode" id="metaDatasCode" rows="1" cols="1">${paramsTempEntity.metaDatasCode}</textarea>
							<span class="input-group-btn"> 
								<a id="menuBtn2" onclick="javascript:showMetadatasTree();" class="btn btn-primary" role="button">选择</a>
							</span>
						</div>												
                     </div>
                     </div>     
                                         
                     <div class="form-group">
                     <div class="row">
						<label class="col-xs-1 control-label text-right">备注：</label>
						<div class="col-xs-8">
							<form:textarea path="remark" rows="3" class="form-control"/>
						</div>												
                     </div>
                     </div>
                </div>
            </div>
        </div>
		<div class="portlet portlet-border" id="imageInfo">
	        <div class="portlet-title">
	            <div class="caption">水印信息 <font color="red">【注：未选择文件类型则不会给文件添加水印】</font></div>
	        </div>
            <div class="portlet-body">
	            <div class="container-fluid"> 
	            	 <div class="form-group">
	                  	<div class="row">
		                     <label class="col-xs-1 control-label text-right">加水印文件类型：</label>
		                        <div class="col-xs-2 checkbox-list" id="waterMarkFileList" class='form-control' style='margin-left:40px;'>
									<label class="checkbox-inline form-group">
										<input type="checkbox" id="imageExtension" name="waterFile" value="图片"/>图片
									</label>
									<label class="checkbox-inline form-group">
										<input type="checkbox" id="videoExtension" name="waterFile" value="视频"/>视频
									</label>
<!-- 									<label class="checkbox-inline form-group"> -->
<!-- 										<input type="checkbox" id="textExtension" name="waterFile" value="文本"/>文本(pdf) -->
<!-- 									</label> -->
									<form:hidden path="waterMarkFileType" />								
		                        </div>
                        </div>
                       </div>
	                          
	            	<div class="form-group">
		        		<div class="row">
							<label class="col-xs-1 control-label text-right">水印类型：</label>
							<div class="col-xs-2 radio-list">
								<label class="radio-inline form-group">
									<input type="radio"  id="waterMarkType" class="imgWMType" name="waterMarkType" value="图片"/>图片
								</label>
								<label class="radio-inline form-group">
									<input type="radio"  id="waterMarkType" class="imgWMType" name="waterMarkType" value="文字"/>文字
								</label>
							</div>																									
		        		</div>
	            	</div>
                    
            		<div id="imgWaterMark">
				        <div class="col-md-12">
					    	<fieldset>
					        	<legend id="wordLegendPosition">水印参数</legend>
					                	<div class="form-group">
					                		<div class="row">
				                    			<div class="col-md-6">
													<label class="col-xs-3 control-label text-right">水印透明度：</label>
													<div class="col-xs-4">
														<app:constants id="imgWaterMarkOpacity" name="waterMarkOpacity" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants"
															className="WmAlpha" inputType="select" selected="${paramsTempEntity.waterMarkOpacity}" cssType="form-control"></app:constants>
													</div>	
												</div>
												<div class="col-md-6">
													<label class="col-xs-3 control-label text-right"><span style="color:red">*</span>&nbsp;图片URL：</label>
													<div class="col-xs-5">
														<input type="file" id="fileData"/>
														</ br>
														${paramsTempEntity.imgWaterMarkURL}
													</div>								
													<input type="hidden" name="imgWaterMarkURL" id="imgWaterMarkURL" value="${paramsTempEntity.imgWaterMarkURL}" class="form-control text-input"/>								
												</div>
					                		</div>
			                    		</div>
							</fieldset>
			            </div>
			        </div>
                   
                 <div id="imgTextWaterMark">
					<div class="col-md-12">
	            		<fieldset>
	            			<legend id="wordLegendPosition">水印参数</legend>
	            			<div class="form-group">
		        				<div class="row">
	                                 <div class="col-md-6">
									 	<label class="col-xs-3 control-label text-right">水印透明度：</label>
											<div class="col-xs-4">
												<app:constants id="wordWaterMarkOpacity" name="waterMarkOpacity" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants"
													className="WmAlpha" inputType="select" selected="${paramsTempEntity.waterMarkOpacity}" cssType="form-control"></app:constants>
											</div>	
								     </div>
								     
								     <div class="col-md-6">
										<label class="col-xs-3 control-label text-right">文字加粗：</label>
											<div class="col-xs-4">
												<app:constants id="waterMarkTextBold" name="waterMarkTextBold" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants"
													className="JudgeWhether" inputType="select" selected="${paramsTempEntity.waterMarkTextBold}" cssType="form-control"></app:constants>
											</div>
	                    		 	</div>
								   </div>
								</div>
	                        	
								<div class="form-group">
		                     		<div class="row">
										<div class="col-md-6">
											<label class="col-xs-3 control-label text-right"><span style="color:red">*</span>&nbsp;水印文字：</label>
											<div class="col-xs-4">
												<form:input path="waterMarkText" id="waterMarkText" class="form-control text-input"/>
											</div>   
										</div>
										<div class="col-md-6">
											<label class="col-xs-3 control-label text-right">字体颜色：</label>
											<div class="col-xs-4">
												<form:input path="waterMarkColor" id="waterMarkColor" class="form-control color"/>
											</div>
										</div>
										
									</div>
								</div>
								<div class="form-group">
		                    		 <div class="row">
										<div class="col-md-6">
											<label class="col-xs-3 control-label text-right">字体大小：</label>
											<div class="col-xs-4">
												<app:constants name="waterMarkTextSize" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants"
											 		className="TextSize" inputType="select" selected="${paramsTempEntity.waterMarkTextSize}" cssType="form-control"></app:constants>
											</div>
										</div>   
										<div class="col-md-6">
											<label class="col-xs-3 control-label text-right">文字字体：</label>
											<div class="col-xs-4">
												<app:constants name="waterMarkTextFont" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants"
													className="WmFont" inputType="select" selected="${paramsTempEntity.waterMarkTextFont}" cssType="form-control"></app:constants>
											</div>	
										</div>	
									</div>
								</div>
							</fieldset>
                     	</div>  
                     </div>
                     
                  <div id="imgTypeDiv">
                    <div class="col-md-12">
	                    <fieldset>
	                        <legend id="wordLegendPosition">图片文件参数</legend>  
                			<div class="form-group">
	                    		<div class="row">
	                     			<div class="col-md-6">
										<label class="col-xs-3 control-label text-right">图片限高：</label>
										<div class="col-xs-4">
											<form:input path="imgHeight" id="imgHeight"  class="form-control text-input"/>
										</div>   
									</div>     
									<div class="col-md-6">             
										<label class="col-xs-3 control-label text-right">图片限宽：</label>
										<div class="col-xs-4">
											<form:input path="imgWidth" id="imgWidth" class="form-control text-input"/>
										</div>	 
									</div>
								</div>                    
                     		</div> 
                     
                   			<div class="form-group">
                   				<div class="row">
                    				<div class="col-md-6">
									<label class="col-xs-3 control-label text-right">图片格式：</label>
									<div class="col-xs-4">
										<app:constants name="imgType" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants" 
												className="ImgFormat" inputType="select" selected="${paramsTempEntity.imgType}" cssType="form-control"></app:constants> 
									</div>	
								</div>
								
								
								<div class="col-md-6">
									<label class="col-xs-3 control-label text-right">水印位置：</label>
									<div class="col-xs-4">
										<app:constants id="imgWaterMarkPos" name="imgWaterMarkPos" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants" 
											className="WmPosition" inputType="select" selected="${paramsTempEntity.imgWaterMarkPos}" cssType="form-control"></app:constants>
									</div>
								</div>
						</div>
						</div>
						
						<div class="form-group">
							<div class="row">
								<div class="col-md-6">
                 		 					<label class="col-xs-3 control-label text-right">
										<input class="btn btn-primary" type="button" value="预览" onclick="showImage('image');"/>
									</label>
								</div>
                 		 			</div>
						</div>
                     	</fieldset>
                     </div>  
                    </div>
                     
                   <div id="videoTypeDiv">
                	<div class="col-md-12">
		                <fieldset>
		                	<legend id="wordLegendPosition">视频文件参数</legend>
							<div class="form-group">
								<div class="row">
									<div class="col-md-6">
										<label class="col-xs-3 control-label text-right">视频格式：</label>
										<div class="col-xs-5">
											<app:constants name="videoType" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants" 
												className="VideoFormat" inputType="select" selected="${paramsTempEntity.videoType}"  cssType="form-control"></app:constants>
										</div>																
									</div>
									
									
									<div class="col-md-6">
										<label class="col-xs-3 control-label text-right">水印位置：</label>
											<div class="col-xs-4">
												<app:constants id="videoWaterMarkPos" name="videoWaterMarkPos" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants" 
													className="WmPosition" inputType="select" selected="${paramsTempEntity.videoWaterMarkPos}" cssType="form-control"></app:constants>
											</div>
									</div>
								</div>
							</div>
							<div class="form-group">
								<div class="row">
									<div class="col-md-6">
	                    		 		<label class="col-xs-3 control-label text-right">
											<input class="btn btn-primary" type="button" value="预览" onclick="showImage('video');"/>
										</label>
									</div>
	                    		 </div>
							</div> 
						</fieldset>
                    </div>
                 </div>
                
<%--                   <div id="textTypeDiv">
                     <div class="col-md-12">
	                    	<fieldset>
	                        	<legend id="wordLegendPosition">文本文件参数</legend>
	                        	
						<div class="form-group">
							<div class="row">
								<div class="col-md-6">
										<label class="col-xs-3 control-label text-right">文本格式：</label>
								<div class="col-xs-5">
									<app:constants name="textType" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants" 
										className="TextFormat" inputType="select" selected="${paramsTempEntity.textType}" cssType="form-control"></app:constants>
								</div>																
							</div>
							
							<div class="col-md-6">
								<label class="col-xs-3 control-label text-right">水印位置：</label>
									<div class="col-xs-4">
										<app:constants id="wordWaterMarkPos" name="wordWaterMarkPos" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants" 
											className="WmPosition" inputType="select" selected="${paramsTempEntity.wordWaterMarkPos}" cssType="form-control"></app:constants>
									</div>
							</div>
						</div>
						</div>
<!-- 						<div class="form-group"> -->
<!-- 							<div class="row"> -->
<!-- 								<div class="col-md-6"> -->
<!--                  		 					<label class="col-xs-3 control-label text-right"> -->
<!-- 										<input class="btn btn-primary" type="button" value="预览" onclick="showImage('word');"/> -->
<!-- 									</label> -->
<!-- 								</div> -->
<!--                  		 	</div> -->
<!-- 						</div> --> 
						</fieldset>
                     </div>
                </div>   --%>   
              </div>
           </div>    
		</div>                    
		<div class="form-group">
			<div class="col-xs-offset-5 col-xs-7">
	           	<input type="button" class="btn btn-primary" value="提交" onclick="checkName();" name="token" value="${token}" />
	            <input class="btn btn-primary" type="button" value="返回" onclick="returnList();"/>
	           </div>
		</div>
		<form:input path="id" id="id" class="hidden"/>
		</form:form>
	</div>
	<input type="text" style="display:none;" id="selectedVal" value=""/>
</body>
</html>