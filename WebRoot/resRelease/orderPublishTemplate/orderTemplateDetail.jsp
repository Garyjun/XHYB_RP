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
/* 	function returnList(){
		location.href = "orderTemplateList.jsp";
	} */
	
	$(document).ready(function(){
		var waterMarkFileTypeDesc = '${paramsTempEntity.waterMarkFileTypeDesc}';
		if(waterMarkFileTypeDesc.indexOf("图片")== -1)
			$("#showImageButton").css("display","none");
			$("#showImageButton").css("display","none");
			
		if('${waterMarkFlag}' == "0"){
			$("#imageInfo").css("display","none");
		}
	});
	
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
		 
		var waterType = "${paramsTempEntity.waterMarkType}";
		if(waterType=="文字"){
			waterType = "word";
		}else{
			waterType = "img";
		}
		var waterPos = "";
		var fileType = "";
		if(type=="image"){
			waterPos = '${paramsTempEntity.imgWaterMarkPos}';
			fileType = "image";
		}else if(type=="video"){
			waterPos = '${paramsTempEntity.videoWaterMarkPos}';
			fileType = "video";
		}else if(type=="word"){
			waterPos = '${paramsTempEntity.wordWaterMarkPos}';
			fileType = "image";
		}
		//对水印图片路径编码
		var imgWaterMarkURL = encodeURI($("#imgWaterMarkURL").val());
		
		if(type=="image"||type=="video"){
			$.get("showImage.action",{
				waterMarkPos:waterPos,
				waterMarkTextSize:'${paramsTempEntity.waterMarkTextSize}',
				waterMarkColor:'${paramsTempEntity.waterMarkColor}',
				waterMarkText:encodeURI('${paramsTempEntity.waterMarkText}'),
				waterMarkTextFont:'${paramsTempEntity.waterMarkTextFont}',
				waterMarkOpacity:'${paramsTempEntity.waterMarkOpacity}',
				waterMarkTextBold:'${paramsTempEntity.waterMarkTextBold}',
				hight:'${paramsTempEntity.imgHeight}',
				width:'${paramsTempEntity.imgWidth}',
				waterType:waterType,
				waterFileType:fileType,
				imgWaterMarkURL:imgWaterMarkURL
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
		}else{
			$.get("showImage.action",{
				waterMarkPos:waterPos,
				waterMarkTextSize:'${paramsTempEntity.waterMarkTextSize}',
				waterMarkColor:'${paramsTempEntity.waterMarkColor}',
				waterMarkText:encodeURI('${paramsTempEntity.waterMarkText}'),
				waterMarkTextFont:'${paramsTempEntity.waterMarkTextFont}',
				waterMarkOpacity:'${paramsTempEntity.waterMarkOpacity}',
				waterMarkTextBold:'${paramsTempEntity.waterMarkTextBold}',
				hight:'${paramsTempEntity.imgHeight}',
				width:'${paramsTempEntity.imgWidth}',
				waterType:waterType,
				waterFileType:fileType,
				imgWaterMarkURL:imgWaterMarkURL
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
	
	</script>
	<style type="text/css">
		pre { 
			margin-left: -2px;
			white-space: pre-wrap; /* css-3 */ 
			white-space: -moz-pre-wrap; /* Mozilla, since 1999 */ 
			white-space: -pre-wrap; /* Opera 4-6 */ 
			white-space: -o-pre-wrap; /* Opera 7 */ 
			word-wrap: break-word; /* Internet Explorer 5.5+ */ 
			background-color: #FFF;
			border: 0px thin #FFF;
			padding-top: -32px;
			margin-top: -6px;
		} 
	</style>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<ul class="page-breadcrumb breadcrumb">
				<li><span>发布模板详细</span></li>
		</ul>
		<div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">模板信息</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                	<div class="form-group">
	                    <div class="row">
							<label class="col-xs-1 control-label text-right">模板名称：</label>
							<div class="col-xs-4">
								<p class="control-label col-md-10" 
									style="white-space:nowrap; width:300px; overflow:hidden;  text-overflow:ellipsis;" 
									title="${paramsTempEntity.name}">
										${paramsTempEntity.name}
								</p>
							</div>	 
							<label class="col-xs-1 control-label text-right">启用状态：</label>
							<div class="col-xs-2 radio-list">
								<p class="control-label col-md-4">${paramsTempEntity.statusDesc}</p>
							</div>							                   
	                     </div>
                     </div>
                     <div class="form-group">
	                    <div class="row">
	                    	<label class="col-xs-1 control-label text-right">发布途径：</label>
							<div class="col-xs-2 radio-list">
								<p class="control-label col-md-4">${paramsTempEntity.posttypeDesc}</p>
							</div>
				 	 	</div>
 					 </div>
                     <div class="form-group">
	                    <div class="row">
	                    	<label class="col-xs-1 control-label text-right">发布方式：</label>
							<div class="col-xs-2 radio-list">
								<p class="control-label col-md-4">${paramsTempEntity.publishTypeDesc}</p>
							</div>
				 	 	</div>
 					 </div>
                     
                      <div class="form-group">
	                    <div class="row">
		                     <label class="col-xs-1 control-label text-right">资源类型：</label>
		                     <div class="col-xs-2  radio-list">
		                    	<p class="control-label col-md-4">${resTypeDesc}</p>
	 					 	 	</div> 
					 	</div> 
				 	</div>
                     <c:if test="${metaInofFlag==1}">
                     	 <div class="form-group">
		                     <div class="row">
								<label class="col-xs-1 control-label text-right" style="margin-right:18px;">元数据项：</label>
								<div class="col-xs-8">
									<%-- <pre>${metadatasDesc}</pre> --%>
									<textarea id="metadatas" readonly="true" class="form-control" style="left;" rows="5" cols="3" class="form-control">${metadatasDesc}</textarea>
								</div>												
		                     </div>
	                     </div>
                     </c:if>
                         
                     <div class="form-group">
                     <div class="row">
						<label class="col-xs-1 control-label text-right" style="margin-right:18px;">备注：</label>
						<div class="col-xs-8">
						<c:if test="${remarkFlag ==1}">
							<pre>${paramsTempEntity.remark}</pre>
						</c:if>
						</div>												
                       </div>
                   </div>
                </div>
            </div>
        </div>
		<div class="portlet portlet-border" id="imageInfo">
	        <div class="portlet-title">
	            <div class="caption">水印信息</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                	<div class="form-group">
                     	<div class="row">
							<label class="col-xs-1 control-label text-right">加水印文件：</label>
							<div class="col-xs-2 radio-list">
								<p class="control-label col-md-4" id="waterMarkFileTypeDesc">
									${paramsTempEntity.waterMarkFileTypeDesc}
								</p>
							</div>																	
                     	</div>
                     </div>
                	
                	<div class="form-group">
	                     <div class="row">
							<label class="col-xs-1 control-label text-right">水印类型：</label>
							<div class="col-xs-2 radio-list">
								<p class="control-label col-md-4" id="imgWaterMarkTypeshow">${paramsTempEntity.waterMarkType}</p>
								<input type="hidden" name="waterMarkType" id="waterMarkType" value="${paramsTempEntity.waterMarkType}"/>
							</div>						
	                     </div>
                     </div>
                     
                     <!-- 图片水印 -->
                     <c:if test="${markType=='img'}">
                     <div class="col-md-12">
	                    <fieldset>
	                        <legend id="wordLegendPosition">水印参数</legend>
	                     	 <div class="form-group">
			                     <div class="row">
									<div class="col-md-6">
										<label class="col-xs-3 control-label text-right">水印透明度：</label>
										<div class="col-xs-4">
											<p class="control-label col-md-4" id="imgWMTextOpacityDesc">${paramsTempEntity.waterMarkOpacityDesc}</p>
											<input type="hidden" name="waterMarkOpacity" id="waterMarkOpacity" value="${paramsTempEntity.waterMarkOpacity}"/>
										</div>	
									</div>
									<div class="col-md-6">
										<label class="col-xs-3 control-label text-right">&nbsp;图片URL：</label>
										<div class="col-xs-2 radio-list">
											${paramsTempEntity.imgWaterMarkURL}
											<input type="hidden" name="imgWaterMarkURL" id="imgWaterMarkURL" value="${paramsTempEntity.imgWaterMarkURL}"/>
										</div>
                   		 	  	    </div>
								</div>
					  	  </div>
					</fieldset>
					</div>
					</c:if>
                     <!-- 文字水印 -->
                     <c:if test="${markType=='word'}">
                     <div class="col-md-12">
	                    <fieldset>
	                        <legend id="wordLegendPosition">水印参数</legend>
                     		 <div class="form-group">
		        				<div class="row">
	                                 <div class="col-md-6">
									 	<label class="col-xs-3 control-label text-right">水印透明度：</label>
											<div class="col-xs-4">
												<p class="control-label col-md-4" id="imgWMTextOpacityDesc">${paramsTempEntity.waterMarkOpacityDesc}</p>
												<input type="hidden" name="waterMarkOpacity" id="waterMarkOpacity" value="${paramsTempEntity.waterMarkOpacity}"/>
											</div>	
								     </div>
								     <div class="col-md-6">
										<label class="col-xs-3 control-label text-right">文字加粗：</label>
										<div class="col-xs-2 radio-list">
											<p class="control-label col-md-4" id="waterMarkTextBoldStr">${paramsTempEntity.waterMarkTextBoldDesc}</p>
											<input type="hidden" name="waterMarkTextBold" id="waterMarkTextBold" value="${paramsTempEntity.waterMarkTextBold}"/>
										</div>
                    		 		</div>
								   </div>
								</div>
	                        	
								<div class="form-group">
		                     		<div class="row">
										<div class="col-md-6">
											<label class="col-xs-3 control-label text-right">水印文字：</label>
											<div class="col-xs-4">
												<p class="control-label col-md-4" id="waterMarkTextStr">${paramsTempEntity.waterMarkText}</p>
												<input type="hidden" name="waterMarkText" id="waterMarkText" value="${paramsTempEntity.waterMarkText}"/>
											</div>   
										</div>
										<div class="col-md-6">
											<label class="col-xs-3 control-label text-right">字体颜色：</label>
											<div class="col-xs-4">
												<p class="control-label col-md-4" id="waterMarkColorStr">${paramsTempEntity.waterMarkColor}</p>
												<input type="hidden" name="waterMarkColor" id="waterMarkColor" value="${paramsTempEntity.waterMarkColor}"/>
											</div>
										</div>
										
									</div>
								</div>
								<div class="form-group">
		                    		 <div class="row">
										<div class="col-md-6">
											<label class="col-xs-3 control-label text-right">字体大小：</label>
											<div class="col-xs-4">
												<p class="control-label col-md-4" id="waterMarkTextSizeDesc">${paramsTempEntity.waterMarkTextSizeDesc}</p>
												<input type="hidden" name="waterMarkTextSize" id="waterMarkTextSize" value="${paramsTempEntity.waterMarkTextSize}"/>
											</div>
										</div>   
										<div class="col-md-6">
											<label class="col-xs-3 control-label text-right">文字字体：</label>
											<div class="col-xs-4">
												<p class="control-label col-md-4" id="waterMarkTextFontDesc">${paramsTempEntity.waterMarkTextFontDesc}</p>
												<input type="hidden" name="waterMarkTextFont" id="waterMarkTextFont" value="${paramsTempEntity.waterMarkTextFont}"/>
											</div>	
										</div>	
									</div>
								</div>
                     </fieldset>
                     </div>
                     </c:if>
                    
                    <!-- 图片-->  
                    <c:if test="${image!=''}"> 
                    <div class="col-md-12">
	                    <fieldset>
	                        <legend id="wordLegendPosition">图片文件参数</legend>
					<div class="form-group">
	                     <div class="row">
							<div class="col-md-6">
											<label class="col-xs-3 control-label text-right">图片限高：</label>
							<div class="col-xs-4">
								<input type="hidden" name="imgHeight" id="imgHeight" value="${paramsTempEntity.imgHeight}"/>
								<p class="control-label col-md-4" id="imgHeightStr">${(paramsTempEntity.imgHeight eq '' || paramsTempEntity.imgHeight eq null)?'默认': paramsTempEntity.imgHeight}</p>
							</div>  
							</div>                   
							<div class="col-md-6">
											<label class="col-xs-3 control-label text-right">图片限宽：</label>
							<div class="col-xs-4">
							 	<input type="hidden" name="imgWidth" id="imgWidth" value="${paramsTempEntity.imgWidth}"/>
								<p class="control-label col-md-4" id="imgWidthStr">${(paramsTempEntity.imgWidth eq '' || paramsTempEntity.imgWidth eq null)?'默认': paramsTempEntity.imgWidth}</p>
							</div>
							</div>
	                     </div>
                     </div>
                     
                     <div class="form-group">
	                     <div class="row">
							<div class="col-md-6">
											<label class="col-xs-3 control-label text-right">图片格式：</label>
							<div class="col-xs-4">
								<p class="control-label col-md-4">${(paramsTempEntity.imgTypeDesc eq '' || paramsTempEntity.imgTypeDesc eq null)?'默认': paramsTempEntity.imgTypeDesc}</p>
								<input type="hidden" name="imgType" id="imgType" value="${paramsTempEntity.imgType}"/> 
							</div>
							</div>
							
							<div id="showImageButton">
								<div class="col-md-6">
								</div>
							</div>
							<div class="col-md-6">
								<label class="col-xs-3 control-label text-right">水印位置：</label>
								<div class="col-xs-4">
									<p class="control-label col-md-4" id="imgWMTextPosDesc">${paramsTempEntity.imgWaterMarkPosDesc}</p>
								   <input type="hidden" name="imgWaterMarkPos" id="imgWaterMarkPos" value="${paramsTempEntity.imgWaterMarkPos}"/>
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
                     </c:if>
                     
                     <!-- 视频 -->
                      <c:if test="${video!=''}"> 
                      <div class="col-md-12">
	                    <fieldset>
	                        <legend id="wordLegendPosition">视频文件参数</legend>
                      <div class="form-group">
	                     <div class="row">
							<div class="col-md-6">
											<label class="col-xs-3 control-label text-right">视频格式：</label>
							<div class="col-xs-4">
								<p class="control-label col-md-4">${(paramsTempEntity.videoTypeDesc eq '' || paramsTempEntity.videoTypeDesc eq null)?'默认': paramsTempEntity.videoTypeDesc}</p>
								<input type="hidden" name="videoType" id="videoType" value="${paramsTempEntity.videoType}"/> 
							</div>
							</div>
							<div class="col-md-6">
								<label class="col-xs-3 control-label text-right">水印位置：</label>
									<div class="col-xs-4">
										<p class="control-label col-md-4" id="videoWaterMarkPosDesc">${paramsTempEntity.videoWaterMarkPosDesc}</p>
										<input type="hidden" name="videoWaterMarkPos" id="videoWaterMarkPos" value="${paramsTempEntity.videoWaterMarkPos}"/> 
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
                      </c:if>
                      
                      <!-- 文本 -->
                      <c:if test="${text!=''}">  
<!-- 	                      <div class="col-md-12"> -->
<!-- 		                    <fieldset> -->
<!-- 		                        <legend id="wordLegendPosition">文本文件参数</legend> -->
<!-- 	                      <div class="form-group"> -->
<!-- 		                     <div class="row"> -->
<!-- 								<div class="col-md-6"> -->
<!-- 												<label class="col-xs-3 control-label text-right">文本格式：</label> -->
<!-- 								<div class="col-xs-4"> -->
<%-- 									<p class="control-label col-md-4">${paramsTempEntity.textTypeDesc}</p> --%>
<%-- 									<input type="hidden" name="textType" id="textType" value="${paramsTempEntity.textType}"/> --%>
<!-- 								</div> -->
<!-- 								</div> -->
<!-- 								<div class="col-md-6"> -->
<!-- 									<label class="col-xs-3 control-label text-right">水印位置：</label> -->
<!-- 									<div class="col-xs-4"> -->
<%-- 										<p class="control-label col-md-4" id="wordWaterMarkPosDesc">${paramsTempEntity.wordWaterMarkPosDesc}</p> --%>
<%-- 										<input type="hidden" name="wordWaterMarkPos" id="wordWaterMarkPos" value="${paramsTempEntity.wordWaterMarkPos}"/> --%>
<!-- 									</div> -->
<!-- 								</div> -->
<!-- 							 </div> -->
<!-- 						</div>  -->
<!-- 						<div class="form-group"> -->
<!-- 							<div class="row"> -->
<!-- 								<div class="col-md-6"> -->
<!-- 	                		 					<label class="col-xs-3 control-label text-right"> -->
<!-- 										<input class="btn btn-primary" type="button" value="预览" onclick="showImage('word');"/> -->
<!-- 									</label> -->
<!-- 								</div> -->
<!-- 	                		 </div> -->
<!-- 						</div>  -->
<!-- 						</fieldset> -->
<!-- 						</div>  -->
                      </c:if>
                     </div>
                </div>
            </div>
			<div class="form-group">
				<div class="col-xs-offset-5 col-xs-7">
		            <input class="btn btn-primary" type="button" value="返回" onclick="history.go(-1);"/>
		        </div>
			</div>
		</div>   
</body>
</html>
