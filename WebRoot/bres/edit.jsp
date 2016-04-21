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
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/filterWordsResult.js"></script>
	<script type="text/javascript">
		var initWaiting = $.waitPanel('正在加载请稍后...',false);
		var currentModule = '${module}';
		var currentType = '${type}';
		var cFileType = '';
		var objectId = '';
		var md5 = '';//刚上传文件的md5值
		var hasFile = 0;
		var fileStatus = 0;
		var imgStatus = 0;
		var repeatCancel = 0;
		$(function() {
			objectId = $('#objectId').val();			
			//初始化上传组件
		    $('#fileData').uploadify({
		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
		        'uploader' : '${path}/uploadFileToTemp.action;jsessionid=<%=session.getId()%>',
		        'debug' : false,
		        //是否自动上传
		        'auto':false,
		        'multi':false,
		        'queueSizeLimit':1,
		        'buttonText':'选择文件',
		        'removeCompleted':false,
		        'onUploadStart': function (file) { 
		        	uploadFileWaiting = $.waitPanel('正在上传文件...',false);
            	},
		        'onSelect':function(file){
		       		cFileType = file.type;
		       		getExtentMeta();
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
		        'onUploadSuccess': function(file, data, response) {  
		        	var info = '<font color="red">上传失败</font>';
		        	data = eval('('+data+')');
		        	if(data.status == 0){
		        		//path保存
		        		md5 = data.md5;
		        		$('#uploadFile').val(data.uploadFile);
		        		hasFile = 0;
		        		info = "上传成功";
		        		fileStatus = 0;
		        		saveResWithFile(fileStatus,imgStatus);
		        	}
		        	$('#' + file.id).find('.data').html(' - ' + info);
		        	uploadFileWaiting.close();
            	}
		    });
		    $('#thumbFileData').uploadify({
		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
		        'uploader' : '${path}/uploadFileToTemp.action',
		        'debug' : false,
		        //是否自动上传
		        'auto':false,
		        'multi':false,
		        'queueSizeLimit':1,
		        'fileSizeLimit':'2MB',
		        'queueID':'thumbFileDataDiv',
		        'fileTypeExts':'*.jpg;*.png',
		        'buttonText':'选择图片',
		        'removeCompleted':false,
		        'onUploadStart': function (file) { 
		        	uploadThumbFileWaiting = $.waitPanel('正在上传缩略图文件...',false);
            	},
		        'onSelect':function(file){
		        },
		      //返回一个错误，选择文件的时候触发
		        'onSelectError':function(file, errorCode, errorMsg){
		            switch(errorCode) {
		                case -100:
		                    $.alert("上传的文件数量已经超出系统限制的"+$('#thumbFileData').uploadify('settings','queueSizeLimit')+"个文件！");
		                    break;
		                case -110:
		                    $.alert("文件 ["+file.name+"] 大小超出系统限制的"+$('#thumbFileData').uploadify('settings','fileSizeLimit')+"大小！");
		                    break;
		                case -120:
		                    $.alert("文件 ["+file.name+"] 大小异常！");
		                    break;
		                case -130:
		                    $.alert("文件 ["+file.name+"] 类型不正确,系统只允许ipg和RAR格式的文件！");
		                    break;
		            }
		        },
		        onUploadProgress: function (file, bytesUploaded, bytesTotal, totalBytesUploaded, totalBytesTotal) {
		        	imgStatus = 1;
	            },
		        'onUploadSuccess': function(file, data, response) {  
		        	var info = '<font color="red">上传失败</font>';
		        	data = eval('('+data+')');
		        	if(data.status == 0){
		        		//path保存
		        		$('#thumbFile').val(data.uploadFile);
		        		info = "上传成功";
		        		imgStatus = 0;
		        		saveResWithFile(fileStatus,imgStatus);
		        	}
		        	$('#' + file.id).find('.data').html(' - ' + info);
		        	uploadThumbFileWaiting.close();
            	}
		    });
			
			//元数据提交
		    $('#coreData').validationEngine('attach', {
				relative: true,
				overflownDIV:"#divOverflown",
				promptPosition:"bottomLeft",
				validateNonVisibleFields:true,
				validateDisplayNoneFields:false,
				showOneMessage:true,
				maxErrorsPerField:1,
				binded:true,
				onValidationComplete:function(form,status){
					if(status){
			    			//修改逻辑不查重
			    			var len1 = $("#fileData").data('uploadify').queueData.queueLength;
							var len2 = $("#thumbFileData").data('uploadify').queueData.queueLength;
							if(len2 > 0){
								$('#thumbFileData').uploadify('upload','*');
							}
							if(len1 > 0){
								$('#fileData').uploadify('upload','*');
							}
							if(len1 == 0){
								if(len2 == 0){
									var filePath = $('#uploadFile').val();
									if(repeatCancel ==1 && filePath !=''){
										checkRepeat();
									}else{
										if(objectId != ''){
											if(hasFile == 1){
				 								$.alert('请选择上传的文件！');
				 							}else{
				 								updateForm();
				 							}
										}else{
											$.alert('请选择上传的文件！');
										}
									}
								}else{
									if(objectId == ''){
										$.alert('请选择上传的文件！');
									}else if(hasFile == 1 && objectId != ''){
										$.alert('请选择上传的文件！');
									}
								}
							}
					}
				}
			});
			if(objectId!=''){
				initData();
				//initFile();
			}else{
				setModuleAndType(currentModule,currentType);
				$("#ratingShow").rating();
				$('#coverDiv').hide();
			}
			
		});
		
		var fileNum = 0; //文件上传总数
		var fileCompleteNum = 0;//完成总数
		function saveResWithFile(fileStatus,imgStatus){
			if(fileStatus == 0 && imgStatus == 0){
				if(objectId == ''){
					checkRepeat();
	    		}else{
	    			//修改逻辑不查重
	    			updateForm();
	    		}
			}
		}
		function saveRes(){
			saveResWaiting = $.waitPanel('正在保存资源...',false);
// 			if(typeof(repeatType) == 'undefined'){
// 				repeatType = 1;
// 				$('#relationIds').val(relationIds);
// 			}
			$('#repeatType').val();
	//		$('#format').val(cFileType);
			$('#region').val($('#provinces').val()+','+$('#city').val()+','+$('#area').val());
			//获取最终的xpaths
			var xpaths = '';
			if(type == 'T12'){
				//获取学段的xpath
				xpaths = module + ',' + type + ',' + getEducational();
			}else if(module != 'TB'){
				xpaths = $('#subject').find('option:selected').attr('xpath');
			}
			$('#xpaths').val(xpaths);
			var url = "";
			if($("#saveAndApply").val()==1){
				url = "${path}/res/wf/doSaveAndSubmit.action?libType=${libType}";
			}else{
				url = "${path}/bres/saveRes.action";
			}
			$('#coreData').ajaxSubmit({
				url: url,//表单的action
 				type: 'post',//方式
 				success:(function(response){
 					saveResWaiting.close();
 					if('${operateFrom}'!=''){
							window.location.href = "${path}"+response;
					}else{
						if(response == ''){
							parent.queryForm();
	 					}else{
							$.showTips(response,5,'');
	 					}
					}
           		})
 			});
		}
		//查重
		function checkRepeat(){
			$.post('${path}/bres/checkRepeat.action',{module:$('#module').val(),source:$('#source').val(),type:$('#type').val(),title:$('#title').val(),creator:$('#creator').val(),md5:md5},function(data){
				data = eval('('+data+')');
				if(data.status == 1){
					confirmRepeat(data.res);
				}else{
					saveRes();
				}
			});
		}

		var relationIds = '';
		function confirmRepeat(res){
			var msg = '<table border="0" cellpadding="0" cellspacing="0" >';
			var resVersions = new Array();
			for(var rs in res){
				var obj = res[rs];
				msg+= '<tr style="white-space:nowrap;"><td><input type="radio" id="repeatResId" name="repeatResId" value="'+obj.objectId+'='+obj.commonMetaData.commonMetaDatas.res_version+'"/></td><td><b>标题：</b>'
				+obj.commonMetaData.commonMetaDatas.title;
				
				if(typeof(obj.commonMetaData.commonMetaDatas.keywords) != 'undefined' && obj.commonMetaData.commonMetaDatas.keywords != ''){
					msg+= '<b>关键字：</b>'+obj.commonMetaData.commonMetaDatas.keywords;
				}
				if(typeof(obj.commonMetaData.commonMetaDatas.res_version) != 'undefined' && obj.commonMetaData.commonMetaDatas.res_version != ''){
					msg+= '<b>资源版本：</b>V'+obj.commonMetaData.commonMetaDatas.res_version+'.0';
				}
				if(typeof(obj.objectId) != 'undefined' && obj.objectId != ''){
					msg+= "<a class=\"btn hover-red\" href=\"javascript:detailOres('"+obj.objectId+"');\"><i class=\"fa fa-sign-out\"></i>详细</a>";
				}
				resVersions.push(obj.commonMetaData.commonMetaDatas.res_version);
				msg+= '</td></tr>';
				if(relationIds == ''){
					relationIds += obj.objectId;
				}else{
					relationIds += ',' + obj.objectId;
				}
			}
			
			msg += '</table>';
			msg +='<script type=\"text/javascript\">function detailOres(objectId){$.openWindow(\"${path}\/bres\/openDetail.action?libType=${param.libType}&objectId=\"+objectId,\"资源详细\",800,450);}<\/script>';
			$.simpleDialog({
				title:'存在重复版本', 
				content:'<font color=\"red\">系统检测到重复资源，请检查！</font>'+msg, 
				button:[{name:'覆盖',callback:function(){
							var checkedObjectId = top.$('input:radio[name="repeatResId"]:checked').val();
							if(typeof(checkedObjectId) != 'undefined' && checkedObjectId != ''){
								var checkedObjectIdAndVersion = checkedObjectId.split("=");
								$('#objectId').val(checkedObjectIdAndVersion[0]);
								$('#resVersion').val(checkedObjectIdAndVersion[1]);
								$('#repeatType').val("2");
								saveRes();
							}else{
								$.alert('请选择要覆盖的资源');
								return false;
							}
						}},
				        {name:'创建新版本',callback:function(){
				        	$('#objectId').val('');
				        	if(resVersions.length>0){
				        		var maxValue = resVersions[0];
				        		for(var i=1;i<resVersions.length;i++){
				        			if(maxValue<resVersions[i]){
				        				maxValue = resVersions[i];
				        			}
					        	}
				        	}
				        	var newResVersion = parseInt(maxValue)+1;
				        	$('#resVersion').val(newResVersion);
				        	$('#relationIds').val(relationIds);
				        	saveRes();
				        }},
// 				        {name:'详细',callback:function(){
// 				        	alert(relationIds);
// 				        	$.openWindow("${path}/bres/openDetail.action?libType=${param.libType}&objectId="+relationIds,'资源详细',800,450);
// 				        }},
				        {name:'忽略',callback:function()
				        	{
				        	repeatCancel =1;
				        	}
				        }
				]
			});
		}
		//修改表单数据
		function updateForm(){
			saveResWaiting = $.waitPanel('正在保存资源...',false);
			$('#relationIds').val(relationIds);
		//	$('#format').val(cFileType);
			$('#region').val($('#provinces').val()+','+$('#city').val()+','+$('#area').val());
			//获取最终的xpaths
			var xpaths = '';
			if(type == 'T12'){
				//获取学段的xpath
				xpaths = module + ',' + type + ',' + getEducational();
			}else if(module != 'TB'){
				xpaths = $('#subject').find('option:selected').attr('xpath');
			}
			$('#xpaths').val(xpaths);
			var url = "";
			if($("#saveAndApply").val()==1){
				url = "${path}/res/wf/doSaveAndSubmit.action?libType=${libType}";
			}else{
				url = "${path}/bres/updateRes.action";
			}
			$('#coreData').ajaxSubmit({
				url: url,//表单的action
 				type: 'post',//方式
 				success:(function(response){
 					saveResWaiting.close();
 					var taskId = $("#wfTaskId").val();
 					if('${operateFrom}'!=''){
 						if(taskId!=null){
 							window.location.href = "${path}/TaskAction/toList.action";
 						}else{
 							window.location.href = "${path}"+response;
 						}
					}else if('${target}'!=''){
						window.location.href = '${path}/res/wf/gotoCheck.action?libType='+$("#libType").val()+'&objectId='+ objectId + '&operateFrom=MANAGE_PAGE'+'&goBackTask='+$('#goBackTask').val();
					}else{
						if(response == ''){
							parent.queryForm();
	 					}else{
	 						$.showTips(response,5,'');
	 					}
					}
           		})
 			});
		}
		//初始化数据
		function initData(){
			var data = '${resourceDetail}';
	//	var data = '{"@type":"asset","objectId":"urn:asset-c58d9f5b-dfdc-4163-a18b-f48c0a7a4ce6","commonMetaData":{"commonMetaDatas":{"fascicule":"B1","chapter":"qDtn0mpl","copyright":"华师京城","keywords":"说“屏”,屏风","libType":"2","subject":"S01","chapter_name":"第三单元","language":"ZH","source":"中央电教馆","title":"由日本名画文物展谈幕府屏风艺术","type":"T01","versionName":"人教版","fasciculeName":"上册","modified_time":"2014-11-28 15:09:43:471","create_date":"20141128","subjectName":"语文","identifier":"hsjc.TB.M.V01.G81-B1.S01.T01.F10.29018436","gradeName":"八年级","audience":"QT","creator":"华师京城","unitName":"15 说\\"屏\\"","module":"TB","educational_phase":"M","format":"doc","node_name":"15 说","res_version":"1","version":"V01","educational_phase_name":"初中","node":"f8LbQjEJ","unit":"urn:domain-1603ba7f-ec52-4a5b-9dd3-45112332f364","grade":"G81","status":"0"},"objectId":"urn:comMeta-436f5b38-1e57-4ad5-8cee-bce1fd3c88a2"},"domains":["urn:domain-1603ba7f-ec52-4a5b-9dd3-45112332f364"],"importXpath":"V01,M,S01,G81,B1,qDtn0mpl,f8LbQjEJ","importXpathName":"人教版,初中,语文,八年级,上册,第三单元","resType":"F10","xpathNames":["人教版,初中,语文,八年级,上册,第三单元"],"xpaths":["V01,M,S01,G81,B1,qDtn0mpl,f8LbQjEJ"],"extendMetaData":{"extendMetaDatas":{},"objectId":"urn:extMeta-9999bffb-4565-4818-9972-29b57c180ba5"},"files":[{"name":"由日本名画文物展谈幕府屏风艺术.doc","objectId":"urn:file-ab6be981-a134-44da-8d1b-38f3ec6a4d07","fileByte":"44032","format":"doc","md5":"183b1632e4ac9e14b0bc09f224b80198","modified_time":"2014-11-28 15:09:43:471","path":"TB/T01/G55/hsjc_TB_M_V01_G81-B1_S01_T01_F10_29018436/由日本名画文物展谈幕府屏风艺术.doc","publisher":""}]}';
			if(data == ''){
				initWaiting.close();
				$.alert('未找到数据');
				parent.queryForm();
				return;
			}
			data = data.replace(/[\r\n]/g,"");
			data = eval('('+data+')');
			//核心和通用元数据
			var commonMetaDatas = data.commonMetaData.commonMetaDatas;
			$('#commonMetaDataObjectId').val(data.commonMetaData.objectId);
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
			var coverPath  =  commonMetaDatas['path'];
			if(coverPath !=null && coverPath!=''){
				$('#coverPath').val(commonMetaDatas['path']);
			}else{
				$('#coverDiv').hide();			
			}
			$('#batchNum').val(data.batchNum);		
			var public_or_not = commonMetaDatas['public_or_not'];
			var release_scope = commonMetaDatas['release_scope'];
			$('#resVersion').val(commonMetaDatas['res_version']);
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
				if(key=='rating'){
					$("#ratingShow").val(commonMetaDatas[key]);
				}else{
					$('#'+key).val(commonMetaDatas[key]);
				}
			}
			$("#ratingShow").rating();
			var files = data.files;
			if(files != undefined){
				if(files.length>0){
					$('#fileDiv').hide();
					var uploadFile = data.files[0].path;
					if(typeof(uploadFile) != 'undefined'){
						$('#resFilePath').val(uploadFile);
						cFileType = uploadFile.substring(uploadFile.lastIndexOf('.'));
						var _divId = "data_div";
				   		var _pk = 'path';
						var _colums =[ {field : 'name',title : '文件名',width : fillsize(0.12),sortable : false}, 
						               {field : 'format',title : '文件类型',width : fillsize(0.12),sortable : false}, 
						               {field : 'fileByte',title : '文件大小',width : fillsize(0.12),sortable : false}, 
						               {field : 'publisher',title : '发布者',width : fillsize(0.12),sortable : false}, 
						               {field : 'modified_time',title : '更新时间',width : fillsize(0.12),sortable : false}, 
						               {field : 'opt1',title : '操作',width : fillsize(0.3),align : 'center',formatter : $operate} ];
						$grid = $.datagridJsObject(_divId,files,_pk,_colums,null,null,true,false);
					}
				}
			}else{
				hasFile =1;
			}
 			getExtentMeta();
			//处理扩展元数据
			var extendMetaData = data.extendMetaData;
			if(extendMetaData!= undefined){
				var extendMetaDatas = data.extendMetaData.extendMetaDatas;
				$('#extendMetaDataObjectId').val(data.extendMetaData.objectId);
				for(var key in extendMetaDatas){
					$('#'+key).val(extendMetaDatas[key]);
				}
			}
			var copyrightMetadata = data.copyRightMetaData;
			if(copyrightMetadata != undefined){
				if(typeof(copyrightMetadata) != 'undefined' && copyrightMetadata != ''){
					var copyrightMetadatas  = data.copyRightMetaData.copyRightMetaDatas;
					var copyrightObjectId = data.copyRightMetaData.objectId;
					
					if(typeof(copyrightObjectId) != 'undefined' && copyrightObjectId != ''){
						$('#copyrightObjectId').val(copyrightObjectId);
					}
					if(typeof(copyrightMetadatas) != 'undefined' && copyrightMetadatas != ''){
						for(var key in copyrightMetadatas){
							$('#'+key).val(copyrightMetadatas[key]);           
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
			               {field:'opt1',title:'操作',width:fillsize(0.3),align:'center',formatter:$operate}];
	   		
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false,false,true);
		} 
		
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<a class=\"btn hover-red\" href=\"javascript:readFileOnline('"+rec.path+"','"+objectId+"')\" ><i class=\"fa fa-eye\"></i> 预览</a>";
			opt += "<a class=\"btn hover-blue\" href=\"javascript:downRes('"+rec.path+"')\" ><i class=\"fa fa-download\"></i> 下载</a>";
			opt += "<a class=\"btn hover-blue\" href=\"javascript:delFile('"+rec.objectId+"','"+rec.path+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a>";
			opt += "<a class=\"btn hover-red\" href=\"javascript:filterWord('"+rec.path+"')\" ><i class=\"fa fa-filter\"></i> 过滤</a>";
			return opt;
					
		};
		
		//删除资源对应的文件
		function delFile(objectId,paths){
			if(objectId != ''){
				var resId = $('#objectId').val();
				$.confirm('确定要删除此文件吗？', function(){
					$.post('${path}/bres/delFile.action',{ids:objectId,paths:paths,resId:resId},function(rtn){
				//		$grid.query();
						hasFile = 1;
						$('#fileDiv').show();
						$('#fileList').hide();
					});
				});
			}
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
		function closeWt(){
			initWaiting.close();
		}
		function showCover(){
			var cover = $('#coverPath').val();
			readFileOnline(cover);
		}
		function downRes(filePath){
			window.location.href = "${path}/bres/downloadFile.action?filePath="+filePath;
		}
	</script>
</head>
<body onload="closeWt()">
<div id="fakeFrame" class="container-fluid by-frame-wrap">
    <ul class="page-breadcrumb breadcrumb">
        <li>
            <a href="###">资源管理</a>
            <i class="fa fa-angle-right"></i>
        </li>
        <li>
            <a href="###">
            <c:choose>
            	<c:when test="${libType eq 1}">标准资源</c:when>
            	<c:otherwise>原始资源</c:otherwise>
            </c:choose>
            </a>
            <i class="fa fa-angle-right"></i>
        </li>
        <li><a href="###">编辑资源</a></li>
    </ul>
	<form action="#" id="coreData" class="form-horizontal">
    <div class="by-tab">
		<%@ include file="metaData.jsp" %>
         <div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">文件信息</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row" id="fileDiv">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="control-label col-md-4"><i class="must">*</i>文件：</label>
                                <div class="col-md-8">
                                	<input type="hidden" id="format" name="commonMetaData.commonMetaDatas['format']" />
                                    <input type="file" name="fileData" id="fileData" class="validate[required]" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <c:if test="${not empty objectId}">
                     <div class="row" id="fileList">
                            <div class="form-group">
                            	<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
                            </div>
                     </div>
                    </c:if>
                    <div id="coverDiv">
                   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:showCover();"  >查看封面</a>
                   </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="control-label col-md-4">缩略图：</label>
                                <div class="col-md-8">
                                    <input type="file" name="thumbFileData" id="thumbFileData"  />
									图片尺寸150*120px，2M以内，仅支持JPG、PNG
									<div id="thumbFileDataDiv"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <c:if test="${not empty objectId}">
			<%@ include file="relationRes.jsp" %>
        </c:if>
        <div class="form-actions">
        	<input type="hidden" id="type" name="commonMetaData.commonMetaDatas['type']"/>
        	<input type="hidden" id="module" name="commonMetaData.commonMetaDatas['module']"/>
<!--         	<input type="hidden" id="resFilePath" name="files[0].path" value=""/> -->
			<input type="hidden" id="status" name="commonMetaData.commonMetaDatas['status']"/>
        	<input type="hidden" id="uploadFile" name="uploadFile" value=""/>
        	<input type="hidden" id="thumbFile" name="thumbFile" value=""/>
        	<input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
        	<input type="hidden" id="repeatType" name="repeatType" value="1"/>
        	<input type="hidden" id="libType" name="commonMetaData.commonMetaDatas['libType']" value="${libType}"/>
        	<input type="hidden" id="relationIds" name="relationIds" value=""/>
        	<input type="hidden" id="saveAndApply" name="saveAndApply" value=""/>
        	<input type="hidden" id="operateFrom" name="operateFrom" value="${operateFrom}"/>
        	<input type="hidden" id="wfTaskId" name="wfTaskId" value="${wfTaskId}"/>
        	<input type="hidden" id="goBackTask" name="goBackTask" value="${goBackTask}"/>
        	<input type="hidden" id="xpaths" name="xpaths[0]" value=""/>
        	<input type="hidden" id="commonMetaDataObjectId" name="commonMetaData.objectId" value=""/>
        	<input type="hidden" id="extendMetaDataObjectId" name="extendMetaData.objectId" value=""/>
        	<input type="hidden" id="resVersion" name="commonMetaData.commonMetaDatas['res_version']"/>
        	<input type="hidden" id="coverPath" name="commonMetaData.commonMetaDatas['path']" value=""/>
            <button type="submit" class="btn btn-lg red">提交</button>
            <c:if test="${status=='5'}">
        		<button type="button" class="btn btn-lg red" onclick="doApply();">提交并上报</button>
        	</c:if>
            <button type="button" class="btn btn-lg blue" onclick="goBack();">取消</button>
        </div>
    </div>
    </form>
</div>
<script type="text/javascript">
	function goBack(){
		if('${operateFrom}'=='TASK_LIST_PAGE'&&'${target}'==''){
			window.location.href = "${path}/TaskAction/toList.action"; 
		}else if('${target}'!='')
		{
			window.location.href = '${path}/res/wf/gotoCheck.action?libType='+$("#libType").val()+'&objectId='+ objectId + '&operateFrom=MANAGE_PAGE'+'&goBackTask='+$("#goBackTask").val();
		}else{
			parent.queryForm();
		}
	}
	
	function doApply(){
		$("#saveAndApply").val("1");
		$("#coreData").submit();
	}
</script>
<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
<script type="text/javascript" src="${path}/bres/knowledgeTree.js"></script>
</body>
</html>