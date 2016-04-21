<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/appframe/common.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>资源详细</title>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/filterWordsResult.js"></script>
	<style type="text/css">
		.no_border{border:none;background-color: white !important;cursor: default !important;}
	</style>
	<script type="text/javascript">
		initWaiting = $.waitPanel('正在加载请稍后...',false);
		var cFileType = '';
		var objectId = '';
		var md5 = '';//刚上传文件的md5值
		$(function() {
			objectId = $('#objectId').val();
			
			if(objectId!=''){
				initData();
				initFile();
			}else{
				setModuleAndType('${module}','${type}');
			}
			//所有表单变为只读，变成详细页面
			setReadOnlyAndNoborder(); 
		});
		//把select，radio 等换成text，然后边框隐藏
		function setReadOnlyAndNoborder(){
			//处理select
			$("select").each(function(i){
			   var cText = $(this).find("option:selected").text();
			   if(cText == '请选择') cText = '';
			   $(this).parent().html('<input type="text" class="form-control" readonly="readonly" value="'+cText+'"/>');
			});
			//处理radio
			parseRadio('public_or_not');
			parseRadio('release_scope');
			parseRadio('sex');
			
			//最后边框设置为none
			$('input[type=text]').attr("readonly","readonly").addClass('no_border');
		}
		function parseRadio(radioId){
			var radioObj = $('input[id='+radioId+']:checked');
			var cCheckedValue = radioObj.attr('valueDesc');
			radioObj.parent().parent().html('<input type="text" class="form-control" readonly="readonly" value="'+cCheckedValue+'"/>');
		}
		
		//初始化数据
		function initData(){
			var data = '${resourceDetail}';
			if(data == ''){
				$.alert('未找到数据');
				return;
			}
			data = eval('('+data+')');
			//核心和通用元数据
			var commonMetaDatas = data.commonMetaData.commonMetaDatas;
			//处理特殊
			var m = commonMetaDatas['module'];
			var type = commonMetaDatas['type'];
			setModuleAndType(m,type);
			//处理学段
			$('#educational_phase').val(commonMetaDatas['educational_phase']);
			changeEducational();
			$('#version').val(commonMetaDatas['version']);
			changeVersion();
			$('#subject').val(commonMetaDatas['subject']);
			changeSubject();
			$('#grade').val(commonMetaDatas['grade']);
			changeGrade();
			$('#fascicule').val(commonMetaDatas['fascicule']);
			
			var public_or_not = commonMetaDatas['public_or_not'];
			var release_scope = commonMetaDatas['release_scope'];
			$("input[id=public_or_not][value="+public_or_not+"]").attr("checked",true);
			$("input[id=release_scope][value="+release_scope+"]").attr("checked",true);
			var region = commonMetaDatas['region'];
			if(typeof(region) != 'undefined' && region != ''){
				var regionArray = region.split(',');
				$('#provinces').val(regionArray[0]);
				setCity();
				$('#city').val(regionArray[1]);
				setArea();
				$('#area').val(regionArray[2]);
			}
			
			for(var key in commonMetaDatas){
				//alert(key+"="+commonMetaDatas[key]);
				$('#'+key).val(commonMetaDatas[key]);
			}
			
			var uploadFile = data.files[0].path;
			if(typeof(uploadFile) != 'undefined'){
				$('#resFilePath').val(uploadFile);
				cFileType = uploadFile.substring(uploadFile.lastIndexOf('.'));
			}
			getExtentMeta();
			
			//处理扩展元数据
			var extendMetaDatas = data.extendMetaData.extendMetaDatas;
			for(var key in extendMetaDatas){
				$('#'+key).val(extendMetaDatas[key]);
			}
			var copyrightMetadata = data.copyRightMetaData;
			if(typeof(copyrightMetadata) != 'undefined' && copyrightMetadata != ''){
				var copyrightMetadatas  = data.copyRightMetaData.copyRightMetaDatas;
				if(typeof(copyrightMetadatas) != 'undefined' && copyrightMetadatas != ''){
					for(var key in copyrightMetadatas){
						$('#'+key).val(copyrightMetadatas[key]);
						var cText = $('#'+key).find("option:selected").text();
						if(cText == 'undefine' || cText==''){
						}else{
							$('#'+key).hide();
							$('#'+key+'Detail').val(cText);
						}
					}
				}
			}	
		}
		function initFile(){
			//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = '${path}/getResourceFilesById.action?objectId='+$('#objectId').val();
	   		var _pk = 'path';
	   		var _conditions = [];
	   		var _sortName = 'modified_time';
	   		var _sortOrder = 'desc';
	   		var _colums = [{field:'name',title:'文件名',width:fillsize(0.12)},
			               {field:'format',title:'文件类型',width:fillsize(0.12),sortable:true},
			               {field:'fileByte',title:'文件大小',width:fillsize(0.12),sortable:true},
			               /* {field:'publisher',title:'发布者',width:fillsize(0.12),sortable:true}, */
			               {field:'modified_time',title:'更新时间',width:fillsize(0.12),sortable:true},
			               {field:'opt1',title:'操作',width:fillsize(0.2),align:'center',formatter:$operate}];
	   		
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false,false,true);
		}
		
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<a class=\"btn hover-red\" href=\"javascript:readFileOnline('"+rec.path+"\',\'"+rec.objectId+"')\" ><i class=\"fa fa-eye\"></i> 预览</a>";
			opt += "<a class=\"btn hover-blue\" href=\"javascript:downRes('"+rec.path+"')\" ><i class=\"fa fa-trash-o\"></i> 下载</a>";
			opt += "<a class=\"btn hover-red\" href=\"javascript:filterWord('"+rec.path+"')\" ><i class=\"fa fa-eye\"></i> 过滤</a>";
			return opt;
					
		};
		
		function getExtentMeta(){
			//获取扩展元数据
			$.ajax({
				type:"post",
				async:false,
				url:"${path}/getExtendMetaHtml.action?objectId=${objectId}&type="+$('#type').val()+"&ext="+cFileType,
				success:function(data){
					data = eval('('+data+')');
					$('#tab_1_1_3').html(data.outputResult);
				}
			});
		}
		
		
		function setCity(){
			var provinceCode = $('#provinces').val();
			//获取市列表
			$.ajax({
				type:"post",
				async:false,
				url:"${path}/getPlaceList.action?type=2&code="+provinceCode,
				success:function(data){
					$("#city").empty();
					$("#city").append(createOption(data));
				}
			});
			setArea();
		}
		function setArea(){
			var cityCode = $('#city').val();
			//获取市列表
			$.ajax({
				type:"post",
				async:false,
				url:"${path}/getPlaceList.action?type=3&code="+cityCode,
				success:function(data){
					$("#area").empty();
					$("#area").append(createOption(data));
				}
			});
		}
		function createOption(data){
			var options = '';
			data = eval('('+data+')');
			var lt = data.length;
			for(var i = 0;i <lt;i ++){
				options += '<option value="'+data[i].code+'">'+data[i].name+'</option>';
			}
			return options;
		}
		
		function setModuleAndType(module,type){
			$('#module').val(module);
			$('#type').val(type);
				
			educationalPhaseShow();
			
			var educational_phase = getEducational();
			
			if(educational_phase != ''){
				changeEducational();
			}else if(objectId==''){
				changeDomainType();
			}
			if(objectId==''){
				getExtentMeta();
			}
		}
		function downRes(filePath){
			window.location.href = "${path}/bres/downloadFile.action?filePath="+filePath;
		}
		
		function returnList(){
			location.href = "orphanList.jsp";
		}
	</script>
</head>
<body onload="initWaiting.close()">
<div id="fakeFrame" class="container-fluid by-frame-wrap">
	<c:choose>
    	<c:when test="${empty libType}"></c:when>
    	<c:otherwise>
    		<ul class="page-breadcrumb breadcrumb">
		        <li>
		            <a href="###">资源管理</a>
		            <i class="fa fa-angle-right"></i>
		        </li>
		        <li>
		            <a href="###">
		            <c:choose>
		            	<c:when test="${libType eq 1}">标准资源管理</c:when>
		            	<c:otherwise>原始资源管理</c:otherwise>
		            </c:choose>
		            </a>
		            <i class="fa fa-angle-right"></i>
		        </li>
		        <li><a href="###">资源详细</a></li>
		    </ul>
    	</c:otherwise>
    </c:choose>
	<form action="#" id="coreData" class="form-horizontal">
    <div class="by-tab">
        <ul class="nav nav-tabs nav-justified">
            <li class="active"><a href="#tab_1_1_1" data-toggle="tab">核心元数据</a></li>
            <li><a href="#tab_1_1_2" data-toggle="tab">通用元数据</a></li>
            <li><a href="#tab_1_1_3" data-toggle="tab">扩展元数据</a></li>
            <li><a href="#tab_1_1_4" data-toggle="tab">版权元数据</a></li>
        </ul>
        <div class="tab-content">
            <div class="tab-pane active" id="tab_1_1_1">
                    <div class="portlet">
                        <div class="portlet-title">
                            <div class="caption">资源信息 <a href="javascript:;" onclick="togglePortlet(this)"><i
                                    class="fa fa-angle-up"></i></a></div>
                        </div>
                        <div class="portlet-body">
                            <div class="container-fluid">
                                <div class="row">
                                	<%--以下内容不需要动态判断显示逻辑 --%>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4"><i class="must">*</i>来源：</label>

                                            <div class="col-md-8">
                                            	<input type="text" name="commonMetaDatas['source']" id="source" class="form-control validate[required]" style=""/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4"><i class="must">*</i>标题：</label>

                                            <div class="col-md-8">
                                                <input type="text" name="commonMetaDatas['title']" id="title" class="form-control validate[required]"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4"><i class="must">*</i>关键字：</label>

                                            <div class="col-md-8">
                                            	<input type="text" name="commonMetaDatas['keywords']" id="keywords" class="form-control validate[required]"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4"><i class="must">*</i>摘要：</label>

                                            <div class="col-md-8">
                                                <input type="text" name="commonMetaDatas['description']" id="description" class="form-control validate[required]"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4"><i class="must">*</i>适用对象：</label>

                                            <div class="col-md-8">
                                            	<app:constants id="audience" name="commonMetaDatas['audience']" 
                                                	repository="com.brainsoon.system.support.SystemConstants" 
                                                	className="Audience" 
                                                	inputType="select" cssType="form-control validate[required]" headerValue="请选择"></app:constants>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4"><i class="must">*</i>语种：</label>

                                            <div class="col-md-8">
                                            	<app:constants id="language" name="commonMetaDatas['language']" 
                                                	repository="com.brainsoon.system.support.SystemConstants" 
                                                	className="Language" 
                                                	inputType="select" cssType="form-control validate[required]" headerValue="请选择"></app:constants>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4"><i class="must">*</i>原作者是否公开：</label>

                                            <div class="col-md-8">
											    <app:constants id="public_or_not" name="commonMetaDatas['public_or_not']" 
                                                	repository="com.brainsoon.system.support.SystemConstants" 
                                                	className="OpeningRate" 
                                                	inputType="radio"  selected="QJ"></app:constants>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4"><i class="must">*</i>平台发布是否公开：</label>

                                            <div class="col-md-8">
                                            	<app:constants id="release_scope" name="commonMetaDatas['release_scope']" 
                                                	repository="com.brainsoon.system.support.SystemConstants" 
                                                	className="ReleaseScope" 
                                                	inputType="radio"  selected="1"></app:constants>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <div class="col-md-12">
                                                <fieldset>
                                                    <legend>阅读消费方式设置</legend>
                                                    <div class="col-md-7">
                                                        <label class="control-label col-md-6">消费类型：</label>

                                                        <div class="col-md-6">
                                                            <app:constants id="view_type" name="extendMetaDatas['view_type']" 
                                                				repository="com.brainsoon.system.support.SystemConstants" 
                                                				className="ConsumeType" 
                                                				inputType="select" cssType="form-control" headerValue="请选择"></app:constants>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-5">
                                                        <label class="control-label col-md-6">消费值：</label>

                                                        <div class="col-md-6">
                                                            <input type="text" id="view_value" name="extendMetaDatas['view_value']" class="form-control" placeholder="0"/>
                                                        </div>
                                                    </div>
                                                </fieldset>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <div class="col-md-12">
                                                <fieldset>
                                                    <legend>下载消费方式设置</legend>
                                                    <div class="col-md-7">
                                                        <label class="control-label col-md-6">消费类型：</label>

                                                        <div class="col-md-6">
                                                            <app:constants id="down_type" name="extendMetaDatas['down_type']" 
                                                				repository="com.brainsoon.system.support.SystemConstants" 
                                                				className="ConsumeType" 
                                                				inputType="select" cssType="form-control" headerValue="请选择"></app:constants>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-5">
                                                        <label class="control-label col-md-6">消费值：</label>

                                                        <div class="col-md-6">
                                                            <input type="text" id="down_value" name="extendMetaDatas['down_value']" class="form-control" placeholder="0"/>
                                                        </div>
                                                    </div>
                                                </fieldset>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="portlet">
                        <div class="portlet-title">
                            <div class="caption">制作者信息 <a href="javascript:;" onclick="togglePortlet(this)"><i
                                    class="fa fa-angle-up"></i></a></div>
                        </div>
                        <div class="portlet-body">
                            <div class="container-fluid">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4">所在地区：</label>

                                            <div class="col-md-8">
												<select id="provinces" class="form-control" onchange="setCity()" style="width: 33%;display:inline;">
													<option value="">请选择</option>
													<c:forEach items="${provinces}" var="item">
														<option value="${item.code }">${item.name }</option>
													</c:forEach>
												</select>
												<select class="form-control" id="city" name="city" onchange="setArea()" style="min-width: 80px;width: 32%;display:inline;">
													<option value="">请选择</option>
												</select>
												<input type="hidden" id="region" name="commonMetaDatas['region']" /><%--存储省市县代码 --%>
												<select class="form-control" id="area" name="area" style="min-width: 80px;width: 33%;display:inline;"><option value="">请选择</option></select>
											</div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4"><i class="must">*</i>性别：</label>

                                            <div class="col-md-8">
                                            	<app:constants id="sex" name="commonMetaDatas['sex']" 
                                                	repository="com.brainsoon.system.support.SystemConstants" 
                                                	className="Gender" 
                                                	inputType="radio"  selected="M"></app:constants>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4">所在学校：</label>

                                            <div class="col-md-8">
                                            	<input type="text" name="commonMetaDatas['school']" id="school" class="form-control"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4">学校地址：</label>

                                            <div class="col-md-8">
                                                <input type="text" name="commonMetaDatas['address']" id="address" class="form-control"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4"><i class="must">*</i>制作者姓名：</label>

                                            <div class="col-md-8">
                                            	<input type="text" name="commonMetaDatas['creator']" id="creator" class="form-control validate[required]"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4">其他作者姓名：</label>

                                            <div class="col-md-8">
                                                <input type="text" name="commonMetaDatas['contributor']" id="contributor" class="form-control"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4">出生日期：</label>

                                            <div class="col-md-8">
                                            	<input type="text" name="commonMetaDatas['birthdate']" id="birthdate" class="form-control"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4">Email：</label>

                                            <div class="col-md-8">
                                                <input type="text" name="commonMetaDatas['email']" id="email" class="form-control"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4">手机：</label>

                                            <div class="col-md-8">
                                            	<input type="text" name="commonMetaDatas['cellphone_number']" id="cellphone_number" class="form-control"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label class="control-label col-md-4">职称：</label>

                                            <div class="col-md-8">
                                                <input type="text" name="commonMetaDatas['professional_title']" id="professional_title" class="form-control"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

            </div>
            <%-- 选项卡2 --%>
            <div class="tab-pane" id="tab_1_1_2">
            	<div class="portlet">
                    <div class="portlet-title">
                        <div class="caption">资源信息 <a href="javascript:;" onclick="togglePortlet(this)"><i
                                class="fa fa-angle-up"></i></a></div>
                    </div>
                    <div class="portlet-body">
                        <div class="container-fluid">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label class="control-label col-md-4"><i class="must">*</i>难易程度：</label>

                                        <div class="col-md-8">
                                            <input type="text" id="difficulty_level" name="commonMetaDatas['difficulty_level']" class="form-control" style="width: 70px;"/>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label class="control-label col-md-4"><i class="must">*</i>版权所属：</label>

                                        <div class="col-md-8">
                                            <input type="text" id="copyright" name="commonMetaDatas['copyright']" class="form-control"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label class="control-label col-md-4"><i class="must">*</i>知识点：</label>

                                        <div class="col-md-8">
                                           	<div class="input-group">
                                           		<input type="text" class="form-control" name="commonMetaDatas['knowledge_point_name']" id="knowledge_point_name" readonly="readonly"/>
                                           	</div>
										</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="portlet">
                    <div class="portlet-title">
                        <div class="caption">评价与反馈 <a href="javascript:;" onclick="togglePortlet(this)"><i
                                class="fa fa-angle-up"></i></a></div>
                    </div>
                    <div class="portlet-body">
                        <div class="container-fluid">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label class="control-label col-md-2">评价等级：</label>

                                        <div class="col-md-10">
                                            <input type="number" name="commonMetaDatas['rating']" id="rating" class="rating"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label class="control-label col-md-2">评价者：</label>

                                        <div class="col-md-10">
                                            <input type="text" name="commonMetaDatas['reviewer']" id="reviewer" class="form-control" style="width: 40%;"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label class="control-label col-md-2">评价描述：</label>

                                        <div class="col-md-10">
                                        	<textarea name="commonMetaDatas['review']" class="form-control" rows="3"></textarea>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="tab-pane" id="tab_1_1_3">
            	
			</div>
			<div class="tab-pane" id="tab_1_1_4">
				<%@ include file="/bres/copyrightDetail.jsp" %>
			</div>
        </div>
         <div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">文件信息</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <c:if test="${not empty objectId}">
                     <div class="row">
                        <div class="col-md-12">
                            <div class="form-group">
                            	<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
                            </div>
                         </div>
                     </div>
                     </c:if>
                </div>
            </div>
        </div>
        <c:if test="${not empty objectId}">
			<%@ include file="/bres/relationRes.jsp" %>
        </c:if>
        <div class="form-actions">
        	<input type="hidden" id="type" name="commonMetaDatas['type']"/>
        	<input type="hidden" id="module" name="commonMetaDatas['module']"/>
        	<input type="hidden" id="resFilePath" name="files[0].path" value=""/>
        	<input type="hidden" id="uploadFile" name="uploadFile" value=""/>
        	<input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
        	<input type="hidden" id="repeatType" name="repeatType" value="1"/>
        	<input type="hidden" id="libType" name="commonMetaDatas['libType']" value="${libType}"/>
        	<input type="hidden" id="relationIds" name="relationIds" value=""/>
			<c:if test="${empty wfTaskId}">
            <button type="button" class="btn btn-lg blue" onclick="returnList();">返回</button>
            </c:if>
        </div>
        <!-- 工作流操作页面 -->
        <c:if test="${not empty execuId}">
			<%@ include file="/bres/WFOperate.jsp" %>
        </c:if>
    </div>
    </form>
</div>
<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
<script type="text/javascript" src="${path}/bres/knowledgeTree.js"></script>
</body>
</html>