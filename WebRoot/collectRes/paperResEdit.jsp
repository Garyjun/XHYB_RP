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
	<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<style type="text/css">
	/*  div.zTreeBackground {width:530px;height:400px;text-align:left;} */
    ul.ztree {width:500px;height:360px;overflow-y:scroll;overflow-x:auto;}
 	ul.ztree li span.button.ico_res{ background-image:url("selRes.png") ;margin-right:0px; vertical-align:top; *vertical-align:middle}
	ul.ztree li span.button.score {margin-left:2px; margin-right: -1px; background-position: -25px -100px; vertical-align:top; *vertical-align:middle}
	</style>
	<script type="text/javascript">
		var initWaiting = $.waitPanel('正在加载请稍后...',false);
		var fileNum = 0; //文件上传总数
		var fileCompleteNum = 0;//完成总数
	   function showTreeSecond(treeId){
		    var treeObj = $.fn.zTree.getZTreeObj(treeId);
			var root = treeObj.getNodeByParam("nodeId", "1", null);
			if(root){
				treeObj.expandNode(root,true,false,true);
			}
	   }
	
		var cFileType = '';
		var objectId = '-1';
		var md5 = '';//刚上传文件的md5值
		var data='';
		var currentModule = '${module}';
		var currentType = '${type}';
		var directorySetting = {
				view: {
					//addHoverDom: addHoverDom,
					removeHoverDom: removeHoverDom,
					selectedMulti: false
				},
				edit: {
					enable: true,
					showRemoveBtn: false,
					showRenameBtn: false
				},
				data: {
					simpleData: {
						enable: true,
						idKey: "nodeId",
						pIdKey: "pid"
					}
				},
				callback: {
					beforeDrag: beforeDrag,
					beforeRemove: beforeRemove,
					onClick: onClickNode
				}
			};
		
		$(function() {
			//alert($('#templet_id').val());
			setModuleAndType(currentModule,currentType);
			objectId = $('#objectId').val();
			//初始化上传组件
// 		   $('#thumbFileData').uploadify({
// 		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
// 		        'uploader' : '${path}/uploadFileToTemp.action',
// 		        'debug' : false,
// 		        //是否自动上传
// 		        'auto':false,
// 		        'multi':false,
// 		        'queueSizeLimit':1,
// 		        'fileSizeLimit':'2MB',
// 		        'queueID':'thumbFileDataDiv',
// 		        'fileTypeExts':'*.jpg;*.png',
// 		        'buttonText':'选择图片',
// 		        'removeCompleted':false,
// 		        'onUploadStart': function (file) { 
// 		        	uploadThumbFileWaiting = $.waitPanel('正在上传封面文件...',false);
//             	},
// 		        'onSelect':function(file){
// 		        },
// 		        'onSelectError':function(file, errorCode, errorMsg){
// 		            switch(errorCode) {
// 		                case -100:
// 		                    $.alert("上传的文件数量已经超出系统限制的"+$('#thumbFileData').uploadify('settings','queueSizeLimit')+"个文件！");
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
// 		        	var info = '<font color="red">上传失败</font>';
// 		        	data = eval('('+data+')');
// 		        	if(data.status == 0){
// 		        		//path保存
// 		        		$('#thumbFile').val(data.uploadFile);
// 		        		info = "上传成功";
// 		        		fileCompleteNum = fileCompleteNum + 1;
// 		        		var opt=$('#opt').val();
// 						if(opt=='save'){
// 							saveForm();
// 						}else{
// 							updateForm();
// 						}
// 		        	}
// 		        	$('#' + file.id).find('.data').html(' - ' + info);
// 		        	uploadThumbFileWaiting.close();
//             	}
// 		    });
			var zNodes =[{ nodeId:-1, pid:-2, name:"试卷", open:true,xpath:'1'}];
			if(objectId!='-1'){
				var data = '${resourceDetail}';
				if(data == ''){
					initWaiting.close();
					$.alert('未找到数据');
					parent.queryForm();
					return;
				}
				data = eval('('+data+')');
				initData(data);
				var organizations=data.organizations;
				if(organizations!=null&&organizations.length>0){
					organizationItems=organizations[0].organizationItems;
					$("#ogId").val(organizations[0].objectId);
// 					var firstPath=organizationItems[0].path;
// 					var bookPath=firstPath.substring(0,firstPath.lastIndexOf('/'));
// 					$("#bookPath").val(bookPath);
					if(organizationItems.length>0){
						var organizationItemTemps = new Array();
						for(var i=0;i<organizationItems.length;i++){
							var organizationItem = organizationItems[i];
							if(organizationItem.score!=undefined){
								organizationItem.name = organizationItem.name+organizationItem.score;
							}
							organizationItemTemps[i] = organizationItem;
						}
						zNodes=organizationItemTemps;
					}
				}
			}else{
				$("#ratingShow").rating();
				top.index_frame.setModuleAndType();
			}
			
			$.fn.zTree.init($("#directoryTree"), directorySetting, zNodes);
			showTreeSecond("directoryTree");
			//元数据提交
		    $('#coreData').validationEngine('attach', {
				relative: true,
				overflownDIV:"#divOverflown",
				validateNonVisibleFields:true,
				validateDisplayNoneFields:false,
				promptPosition:"bottomLeft",
				maxErrorsPerField:1,
				binded:true,
				onValidationComplete:function(form,status){
					if(status){
						var opt=$('#opt').val();
						if(opt=='save'){
								saveForm();
						}
						if(opt=='update'){
// 							var len2 = $("#thumbFileData").data('uploadify').queueData.queueLength;
// 							if(len2 > 0){
// 								$('#thumbFileData').uploadify('upload','*');
// 							}else{
								updateForm();
//							}
						}
					}
				}
			});
			
		});
		
		//保存表单数据
		function saveForm(){
			if(objectId == '' || objectId == '-1'){
    			checkRepeat();
    		}else{
    			saveRes();
    		}
		}
		//查重
		function checkRepeat(){
			$.post('${path}/collectRes/checkRepeat.action',{module:$('#module').val(),source:$('#source').val(),type:$('#type').val(),title:$('#title').val(),creator:$('#creator').val()},function(data){
				data = eval('('+data+')');
				if(data.status == 1){
					confirmRepeat(data.res);
				}else{
					saveRes();
				}
			});
		}
		function confirmRepeat(res){
			var msg = '<table border="0" cellpadding="0" cellspacing="0" >';
			var resVersions = new Array();
			for(var rs in res){
				var obj = res[rs];
				msg+= '<tr style="white-space:nowrap;"><td><input type="radio" id="repeatResId" name="repeatResId" value="'+obj.objectId+'"/></td><td><b>标题：</b>'
				+obj.commonMetaData.commonMetaDatas.title;
				
				if(typeof(obj.commonMetaData.commonMetaDatas.keywords) != 'undefined' && obj.commonMetaData.commonMetaDatas.keywords != ''){
					msg+= '<b>关键字：</b>'+obj.commonMetaData.commonMetaDatas.keywords;
				}
				if(typeof(obj.commonMetaData.commonMetaDatas.res_version) != 'undefined' && obj.commonMetaData.commonMetaDatas.res_version != ''){
					msg+= '<b>版本号：</b>V'+obj.commonMetaData.commonMetaDatas.res_version+'.0';
				}
				msg+= '</td></tr>';
				resVersions.push(obj.commonMetaData.commonMetaDatas.res_version);
			}
			
			msg += '</table>';
			$.simpleDialog({
				title:'存在重复版本', 
				content:'<font color=\"red\">系统检测到重复资源，请检查！</font>'+msg, 
				button:[{name:'覆盖',callback:function(){
							var checkedObjectId = top.$('input:radio[name="repeatResId"]:checked').val();
							if(typeof(checkedObjectId) != 'undefined' && checkedObjectId != ''){
								$('#objectId').val(checkedObjectId);
								saveRes(2);
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
				        	saveRes();
				        }},
				        {name:'忽略',callback:function(){}}
				]
			});
		}
		function saveRes(repeatType){
			if(typeof(repeatType) == 'undefined'){
				repeatType = 1;
			}
			$('#repeatType').val(repeatType);
			$('#format').val(cFileType);
			$('#region').val($('#provinces').val()+','+$('#city').val()+','+$('#area').val());
			//获取最终的xpaths
			var xpaths = '';
			var type=$('#type').val();
			var module=$('#module').val();
			if(type == 'T12'){
				//获取学段的xpath
				xpaths = module + ',' + type + ',' + getEducational();
			}else if(module != 'TB'){
				xpaths = $('#subject').find('option:selected').attr('xpath');
			}else if(type == 'T06'){
				xpaths = $('#grade').find('option:selected').attr('xpath');
			}
			$('#xpaths').val(xpaths);
			var t = $.fn.zTree.getZTreeObj("directoryTree");
			var nodes = t.transformToArray(t.getNodes());
			var treeNodes=[];
			$.each(nodes, function(i, node) {
				var book = {
					"name" : node.name,
					"nodeId" : node.nodeId,
					"pid" : node.pid,
					"xpath" : node.xpath,
					"assets": node.assets
				};
				if (node.objectId)
					book["objectId"] = node.objectId;
				if (node.version)
					book["version"] = node.version;
				if (!node.parentTId)
					book["pid"] = "-1";
				treeNodes.push(book);
			});
			var jsonTree = JSON.stringify(treeNodes);
			$("#jsonTree").val(jsonTree);
			saveResWaiting = $.waitPanel('正在保存资源...',false);
			var url = "";
			if($("#saveAndApply").val()==1){
				url = "${path}/res/wf/doSaveAndSubmit.action?repeatType="+repeatType;
			}else{
				url = "${path}/collectRes/saveRes.action?repeatType="+repeatType;
			}
			$('#coreData').ajaxSubmit({
				url: url,//表单的action
 				method: 'post',//方式
 				success:(function(response){
 					saveResWaiting.close();
 					if('${wfTaskId}'!=''){
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
		function updateForm(){
			$('#format').val(cFileType);
			$('#region').val($('#provinces').val()+','+$('#city').val()+','+$('#area').val());
			saveResWaiting = $.waitPanel('正在保存资源...',false);
			$('#coreData').ajaxSubmit({
				url: '${path}/collectRes/updateRes.action',//表单的action
 				method: 'post',//方式
 				success:(function(response){
 					saveResWaiting.close(); 
 					if('${targetCol}'!=''){
						window.location.href = '${path}/res/wf/gotoCheck.action?libType='+$("#libType").val()+'&objectId='+ objectId + '&operateFrom=MANAGE_PAGE'+'&goBackTask='+$("#goBackTask").val();
					}else if(response == ''){
						parent.queryForm();
 					}else{
						$.showTips(response,5,'');
 					}
           		})
 			});
		}
		//初始化数据
		function initData(data){
			var data = '${resourceDetail}';
			data = data.replace(/[\r\n]/g,"");
			data = eval('('+data+')');
			$('#commonMetaDataObjectId').val(data.commonMetaData.objectId);
			$('#extendMetaDataObjectId').val(data.extendMetaData.objectId);
			//核心和通用元数据
			var commonMetaDatas = data.commonMetaData.commonMetaDatas;
			$('#identifier').val(commonMetaDatas['identifier']);
			//处理特殊
			var m = commonMetaDatas['module'];
			var type = commonMetaDatas['type'];
			setModuleAndType(m,type);
			$('#educational_phase').val(commonMetaDatas['educational_phase']);
			changeEducational();
			$('#version').val(commonMetaDatas['version']);
			changeVersion();
			$('#subject').val(commonMetaDatas['subject']);
			changeSubject();
			$('#grade').val(commonMetaDatas['grade']);
			changeGrade();
			$('#fascicule').val(commonMetaDatas['fascicule']);
			$('#coverPath').val(commonMetaDatas['path']);
			var coverPath  =  commonMetaDatas['path'];
			if(coverPath !=null && coverPath!=''){
				$('#coverPath').val(commonMetaDatas['path']);
			}else{
				$('#coverDiv').hide();			
			}
			$('#resVersion').val(commonMetaDatas['res_version']);
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
			$('#batchNum').val(data.batchNum);		
			for(var key in commonMetaDatas){
				if(key=='rating'){
					$("#ratingShow").val(commonMetaDatas[key]);
				}else{
					$('#'+key).val(commonMetaDatas[key]);
				}
			}
			$("#ratingShow").rating();
			
            getExtentMeta();
			
			//处理扩展元数据
			var extendMetaDatas = data.extendMetaData.extendMetaDatas;
			for(var key in extendMetaDatas){
				$('#'+key).val(extendMetaDatas[key]);
			}
			var copyrightMetadata = data.copyRightMetaData;
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
			$.ajax({
				url:"${path}/system/testTemplate/getTemplatName.action?templet_id="+$('#templet_id').val(),
			    type: 'post',
			    datatype: 'text',
			    success: function (returnValue) {
			    		data = eval('('+returnValue+')');
			    		if(data.itemList!=undefined||data.itemList!=''){
			    		var lastValue = data.itemList;
			    		$('#templateName').val(lastValue);
			    	}else{
			    		$.alert("模板名称为空");
			    	}
			    }
			});
		}
		function setModuleAndType(module,type){
			$('#module').val(module);
			$('#type').val(type);
				
			educationalPhaseShow();
			
			var educational_phase = getEducational();
			
			if(educational_phase != ''){
				changeEducational();
			}else if(objectId=='' || objectId=='-1'){
				changeDomainType();
			}
			if(objectId=='' || objectId=='-1'){
				getExtentMeta();
			}
		}
		
			var className = "dark";
			function beforeDrag(treeId, treeNodes) {
				return false;
			}
			function beforeRemove(treeId, treeNode) {
				className = (className === "dark" ? "":"dark");
				var zTree = $.fn.zTree.getZTreeObj("directoryTree");
				zTree.selectNode(treeNode);
				return true;
			}
			function showRemoveBtn(treeId, treeNode) {
				return true;
			}
			function showRenameBtn(treeId, treeNode) {
				return true;
			}
			
			function onClickNode(event, treeId, treeNode, clickFlag) {
				addHoverDom(treeId, treeNode);
			}	
			function addHoverDom(treeId, treeNode) {
				var zTree = $.fn.zTree.getZTreeObj("directoryTree");
				var sObj = $("#" + treeNode.tId + "_span");
				if(treeNode.assets!=null){
					if (treeNode.nodeId!=-1&&treeNode.editNameFlag==false){
						//预览
						if($("#readBtn_"+treeNode.tId).length==0){
							var readStr = "<span class='button preLook' id='readBtn_" + treeNode.tId
							+ "' title='预览' onfocus='this.blur();'></span>";
						    sObj.after(readStr);
							var btnRead = $("#readBtn_"+treeNode.tId);
							if (btnRead) btnRead.bind("click", function(){
								if(zTree.getSelectedNodes()[0].assets.toString().indexOf("urn")>=0){
									//单个资源预览
								    $.openWindow("${path}/bres/papers.jsp?libType=${param.libType}&module=${param.module}&score="+zTree.getSelectedNodes()[0].score+"&type=${param.type}&objectId="+zTree.getSelectedNodes()[0].assets,'试卷预览',1000,650);
								}else{
// 									 if (treeNode.isParent) {
// 									        var childrenNodes = treeNode.children;
// 									        if (childrenNodes) {
// 									            for (var i = 0; i < childrenNodes.length; i++) {
// 									                result += childrenNodes[i].assets+",";
// 									            }
// 									            //alert(1);
// 									            //各小模块总预览
// 									            $.openWindow("${path}/bres/papers.jsp?libType=${param.libType}&module=${param.module}&type=${param.type}&objectId="+result,'试卷预览',1000,650);
// 									        }
// 									    }
								}
							});
						}
						//资源添加
						if($("#infoBtn_"+treeNode.tId).length==0){
							var infoStr = "<span class='button add' id='infoBtn_" + treeNode.tId
							+ "' title='添加' onfocus='this.blur();'></span>";
						    sObj.after(infoStr);
						    var nodes = zTree.getSelectedNodes();
							var btnRes = $("#infoBtn_"+treeNode.tId);
							if (btnRes) btnRes.bind("click", function(){
								//var objectId=treeNode.assets[0];
								$.openWindow("${path}/system/testTemplate/questionRes.jsp?paperId="+$('#paperId').val()+"&queryType=0&libType=2&type=T14&module=ST",'添加试题',900,460);
							});
						}
						if($("#scoreBtn_"+treeNode.tId).length==0){
							var scoreStr = "<span class='button score' id='scoreBtn_" + treeNode.tId
							+ "' title='设置分数' onfocus='this.blur();'></span>";
						    sObj.after(scoreStr);
						    var btnScore = $("#scoreBtn_"+treeNode.tId);
							  if (btnScore) btnScore.bind("click", function(){
									nodes = zTree.getSelectedNodes(),
									treeNode = nodes[0];
									//alert(treeNode.score);
									$("#scoreDesc").val(treeNode.score);
//									$('#nodeOpt').val('edit');
									$("#scoreText").modal('show');
							 });
						}
						 var deleteStr = "<span class='button remove' id='removeBtn_" + treeNode.tId+ "' title='删除' onfocus='this.blur();'></span>";
						 sObj.after(deleteStr);
						 var removeBtn = $("#removeBtn_"+treeNode.tId);
						  if (removeBtn) removeBtn.bind("click", function(){
								nodes = zTree.getSelectedNodes(),
								treeNode = nodes[0];
								var nodeId=treeNode.nodeId;
								var objectId = $('#objectId').val();
								$.post("${path}/collectRes/deleteNode.action",{ caId: objectId,nodeId: nodeId,title:treeNode.name },
										function(data){
											if(data=='-1'){
												$.alert('删除节点异常!');
											}
							    });
								zTree.removeNode(treeNode);
						  });
					}
				}else{
					if (treeNode.nodeId!=-1&&treeNode.editNameFlag==false && $("#addResBtn_"+treeNode.tId).length==0){
						var addResStr = "<span class='button addFile' id='addResBtn_" + treeNode.tId
						+ "' title='添加资源' onfocus='this.blur();'></span>";
						//alert(addResStr);
					    sObj.after(addResStr);
						var btnRes = $("#addResBtn_"+treeNode.tId);
						var objectId = $('#objectId').val();
						if (btnRes) btnRes.bind("click", function(){
							var nodeId=treeNode.nodeId;
							$.openWindow("${path}/system/testTemplate/questionRes.jsp?paperId="+$('#paperId').val()+"&queryType=0&libType=2&type=T14&module=ST",'模板选择',900,500);
						});
					}
					
					if (treeNode.editNameFlag==false && $("#addBtn_"+treeNode.tId).length==0){
// 						var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
// 						+ "' title='添加' onfocus='this.blur();'></span>";
						//alert(addStr);
						var addStr = "";
						if(treeNode.nodeId!=1){
						//	addStr += "<span class='button remove' id='removeBtn_" + treeNode.tId+ "' title='删除' onfocus='this.blur();'></span>";
		                    addStr += "<span class='button edit' title='编辑' id='editBtn_" + treeNode.tId + "'></span>";
		                    addStr += "<span class='button score' title='设置分数' id='scoreBtn_" + treeNode.tId + "'></span>";
		                    addStr +=  "<span class='button preLook' id='readBtn_" + treeNode.tId
							+ "' title='预览' onfocus='this.blur();'></span>";
						}
						sObj.after(addStr);
					   
						var btn = $("#addBtn_"+treeNode.tId);
						if (btn) btn.bind("click", function(){
							$("#labelName").val('');
							$('#nodeOpt').val('add');
							$("#versionText").modal('show');
							return false;
						});
						var btnRead = $("#readBtn_"+treeNode.tId);
						var allPaperId = "";
						var rootPaperName = "";
						//总的试卷预览
						if (btnRead) btnRead.bind("click", function(){
							if(treeNode.files != undefined){
								var filePath=treeNode.files[0].path;
								var object=treeNode.files[0].objectId;
								readFileOnline(filePath,object); //相对路径	
							}else{
								var nodes = zTree.getNodes();
								var v = new Array();
								var q = new Object();
								if(treeNode.nodeId == '-1'){
									for(var i = 0;i<nodes[0].children.length;i++){
										if(nodes[0].children[i].isParent){
											var p = new Object();
											var u = new Array();
											p.name = nodes[0].children[i].name;
											p.score = nodes[0].children[i].score;
											for(var j = 0;j<nodes[0].children[i].children.length;j++){
											u[j]=nodes[0].children[i].children[j].assets;
											allPaperId = allPaperId + nodes[0].children[i].children[j].assets+",";
											}
											p.data = u;
											v[i]= p;
										}
									}
									q.parse = v;
									var json = JSON.stringify(q);
									json = encodeURIComponent(json);
									$.openWindow("${path}/bres/papers.jsp?libType=${param.libType}&module=${param.module}&type=${param.type}&json="+json,'试卷预览',1000,650);
								}else{
									var result='';
									 if (treeNode.isParent) {
									        var childrenNodes = treeNode.children;
									        if (childrenNodes) {
									            for (var i = 0; i < childrenNodes.length; i++) {
									                result += childrenNodes[i].assets+",";
									            }
									            //各小模块总预览
									           $.openWindow("${path}/bres/papers.jsp?libType=${param.libType}&module=${param.module}&type=${param.type}&objectId="+result,'试卷预览',1000,650);
									        }
									    }
								}
// 								q.parse = v;
// 								var json = JSON.stringify(q);
// 								json = encodeURIComponent(json);
// 								$.openWindow("${path}/bres/papers.jsp?libType=${param.libType}&module=${param.module}&type=${param.type}&json="+json,'试卷预览',1000,650);
// 								$.alert("无法预览，文件不存在！");
							}
						});
						  var btnEdit = $("#editBtn_"+treeNode.tId);
						  if (btnEdit) btnEdit.bind("click", function(){
								nodes = zTree.getSelectedNodes(),
								treeNode = nodes[0];
								$("#labelName").val(treeNode.name);
								$('#nodeOpt').val('edit');
								$("#versionText").modal('show');
						  });
						  var btnScore = $("#scoreBtn_"+treeNode.tId);
						  if (btnScore) btnScore.bind("click", function(){
								nodes = zTree.getSelectedNodes(),
								treeNode = nodes[0];
								alert(treeNode.score);
 								$("#scoreDesc").val(treeNode.score);
// 								$('#nodeOpt').val('edit');
 								$("#scoreText").modal('show');
						  });
						  var removeBtn = $("#removeBtn_"+treeNode.tId);
						  if (removeBtn) removeBtn.bind("click", function(){
								nodes = zTree.getSelectedNodes(),
								treeNode = nodes[0];
								var nodeId=treeNode.nodeId;
								var objectId = $('#objectId').val();
								$.post("${path}/collectRes/deleteNode.action",{ caId: objectId,nodeId: nodeId},
										function(data){
											if(data=='-1'){
												$.alert('删除节点异常!');
											}
							    });
								zTree.removeNode(treeNode);
						  });
						
						
					}
				}
			};
			function removeHoverDom(treeId, treeNode) {
				
				$("#addBtn_"+treeNode.tId).unbind().remove();
				$("#addResBtn_"+treeNode.tId).unbind().remove();
				$("#infoBtn_"+treeNode.tId).unbind().remove();
				$("#readBtn_"+treeNode.tId).unbind().remove();
				$("#removeBtn_"+treeNode.tId).unbind().remove();
				$("#editBtn_"+treeNode.tId).unbind().remove();
				$("#scoreBtn_"+treeNode.tId).unbind().remove();
			};
			
			function setNodeRes(nodeId,resId){
				var tmp=nodeId+','+resId;
				$("#nodeAsset").val($("#nodeAsset").val()+tmp+";");
				updateNode(nodeId,resId);
			}
			
			function updateNode(nodeId,resId){
				objectId = $('#objectId').val();
				ogId=$('#ogId').val();
				var nodeAsset=$("#nodeAsset").val();
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var node = treeObj.getNodeByParam("nodeId", nodeId, null);
				node.caId=objectId;
				node.ogId=ogId;
				if(resId!=''){
					var assets=[resId];
					node.assets=assets;
				}
				var nodeJson=JSON.stringify(node);
				treeObj.updateNode(node);
				if(objectId=='-1'){
					closeWindow();
					return;
				}
				var title=$("#title").val();
				$.post("${path}/collectRes/updateNode.action",{ nodeAsset: nodeAsset, nodeJson: nodeJson,title:title },
						function(data){
							if(data=='-1'){
								$.alert('更新节点异常!');
								
							}else{
								node.objectId=data;
								treeObj.updateNode(node);
							}
							closeWindow();
				         }
				);
				
			}
			/***查看***/
			function detail(objectId){
				window.location.href = "${path}/bres/detail.action?objectId="+objectId;
			}
			function changeOpt(optVal){
				$('#opt').val(optVal);
				jQuery('#coreData').submit();
			}
			
			function editNode(){
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var nodes = treeObj.getSelectedNodes();
				var node=nodes[0];
				node.name=$('#labelName').val();
				treeObj.updateNode(node);
				updateNode(node.nodeId,'');
				
			}
			function addNode(){
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var nodes = treeObj.getSelectedNodes();
				var treeNode=nodes[0];
				var id=treeNode.pid + new Date().getTime();
				var xpath=treeNode.xpath+','+id;
				var path=$("#bookPath").val()+'/'+$('#labelName').val();
				treeObj.addNodes(treeNode, {nodeId:id,xpath:xpath,path:path, pid:treeNode.nodeId, name:$('#labelName').val()});
				updateNode(id,'');
				
			}
			function optNode(){
				var nodeOpt=$('#nodeOpt').val();
				if(nodeOpt=='add'){
					addNode();
				}
				if(nodeOpt=='edit'){
					editNode();
				}
			}
			function optScoreDescNode(){
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var nodes = treeObj.getSelectedNodes();
				var node=nodes[0];
				node.score=$('#scoreDesc').val();
				var tempName = node.name;
				node.name = node.name+$('#scoreDesc').val();
				treeObj.updateNode(node);
				objectId = $('#objectId').val();
				ogId=$('#ogId').val();
				var nodeAsset=$("#nodeAsset").val();
				node.caId=objectId;
				node.ogId=ogId;
				node.name = tempName;
				var nodeJson=JSON.stringify(node);
				var title=$("#title").val();
				$.post("${path}/collectRes/updateNode.action",{ nodeAsset: nodeAsset, nodeJson: nodeJson,title:title },
						function(data){
							if(data=='-1'){
								$.alert('更新节点异常!');
							}else{
								node.objectId=data;
						//		treeObj.updateNode(node);
							}
							closeScoreWindow();
				         }
				);
			}
			function closeWindow(){
				$("#versionText").modal('hide');
			}
			function closeScoreWindow(){
				$("#scoreText").modal('hide');
			}
			function showCover(){
				var cover = $('#coverPath').val();
				readFileOnline(cover);
			}
			function closeWt(){
				initWaiting.close();
			}
			//模板选择
			function paperModelSelect(){
				$.openWindow("${path}/collectRes/paperModelSelect.action",'模板选择',1000,600);
			}
			//添加试卷
			function addPapers(){
				$.openWindow("${path}/system/testTemplate/returnPaperTemplate.jsp?paperId="+$('#paperId').val(),'模板选择',1300,300);
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
            <a href="###">聚合资源</a>
            <i class="fa fa-angle-right"></i>
        </li>
        <li><a href="###">编辑资源</a> </li>
    </ul>
	<form action="#" id="coreData" class="form-horizontal" method="post">
	<input type="hidden" id="commonMetaDataObjectId"  name="commonMetaData.objectId" value=""/>
	<input type="hidden" id="extendMetaDataObjectId" name="extendMetaData.objectId" value=""/>
	<input type="hidden" id="identifier" name="commonMetaData.commonMetaDatas['identifier']" value=""/>
	<input type="hidden" id="path" name="commonMetaData.commonMetaDatas['path']" value=""/>
	<input type="hidden" id="libType" name="commonMetaData.commonMetaDatas['libType']" value="3"/>
	<input type="hidden" id="xpaths" name="xpaths[0]" value=""/>
	<input type="hidden" id="bookPath" name="bookPath" />
	<input type="hidden" id="type" name="commonMetaData.commonMetaDatas['type']"/>
    <input type="hidden" id="module" name="commonMetaData.commonMetaDatas['module']"/>
    <input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
    <input type="hidden" id="queryNodeId" name="queryNodeId" value="-1"/>
    <input type="hidden" id="nodeAsset" name="nodeAsset" value=""/>
    <input type="hidden" id="operateFrom" name="operateFrom" value="${operateFrom}"/>
    <input type="hidden" id="wfTaskId" name="wfTaskId" value="${wfTaskId}"/>
    <input type="hidden" name="libType" value="${libType}"/>
    <input type="hidden" id="saveAndApply" name="saveAndApply" value=""/>
	<input type="hidden" id="jsonTree" name="jsonTree" value="${jsonTree}"/>
	<input type="hidden" id="ogId" name="ogId"/>
	<input type="hidden" id="opt" name="opt" value=""/>
	<input type="hidden" id="nodeOpt" name="nodeOpt" value=""/>
	<input type="hidden" id="thumbFile" name="thumbFile" value=""/>
	<input type="hidden" id="coverPath" name="coverPath" value=""/>
	<input type="hidden" id="repeatType" name="repeatType" value="1"/>
	<input type="hidden" id="goBackTask" name="goBackTask" value="${goBackTask}"/>
	<input type="hidden" id="resVersion" name="commonMetaData.commonMetaDatas['res_version']"/>
	<input type="hidden" id="paperId" name="paperId" value=""/>
	<input type="hidden" id="paperName" name="paperName"/>
	<input type="hidden" id="module" name="module" value="${param.module }" />
	<input type="hidden" id="libType" name="libType" value="${param.libType }" />
	<input type="hidden" id="queryType" name="queryType" value="${param.queryType }" />
	<input type="hidden" id="type" name="queryType" value="${param.type }" />
	<input type="hidden" id="templet_id" name="commonMetaData.commonMetaDatas['templet_id']" />
    <div class="by-tab">
     <%@ include file="/bres/metaData.jsp" %>
         <div class="portlet portlet-border">
<!--          	<div class="portlet-title"> -->
<!-- 	            <div class="caption">封面信息</div> -->
<!-- 	        </div> -->
<!--             <div class="portlet-body"> -->
<!--                 <div class="container-fluid"> -->
<%--                     <c:if test="${objectId!='-1'}"> --%>
<!--                     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:showCover();"  >查看封面</a> -->
<%--                     </c:if> --%>
<!--                     <div class="row"> -->
<!--                         <div class="col-md-6"> -->
<!--                             <div class="form-group"> -->
<!--                                 <div class="col-md-8"> -->
<!--                                     <input type="file" name="thumbFileData" id="thumbFileData"  /> -->
<!-- 									图片尺寸150*120px，2M以内，仅支持JPG、PNG -->
<!-- 									<div id="thumbFileDataDiv"></div> -->
<!--                                 </div> -->
<!--                             </div> -->
<!--                         </div> -->
<!--                     </div> -->
<!--                 </div> -->
<!--             </div> -->
	        <div class="portlet-title">
	            <div class="caption">试卷目录</div>
	        </div>
	             <div align="left">
     <table>
		<tr>
			<td><label class="control-label"><i class="must">*</i></label></td>
     		<td><input type="text" class="form-control validate[required]" 
     		name="template.name"id="templateName"  style="width:100px;height:34px" readonly="readonly" /></td>
     		<td><button type="button" class="btn btn-primary"  onclick="paperModelSelect();">选择模板</button>&nbsp;&nbsp;</td>
<!-- 	 		<td><button type="button" class="btn btn-primary" onclick="addPapers();">添加试卷</button></td> -->
	 	</tr>
	 </table>
	 </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <div class="col-md-12">
                                	 <ul id="directoryTree" class="ztree form-wrap" style="overflow:auto;"></ul>
                                </div>
                                
                            </div>
                        </div>
                       
                    </div>
                   
                </div>
            </div>
        </div>
        <div class="form-actions">
            <c:if test="${objectId!='-1'}">
   					   <button type="button"  class="btn btn-lg blue" onclick="changeOpt('update')" >提交</button>
   			</c:if>
   			<c:if test="${objectId=='-1'}">
   					   <button type="button"  class="btn btn-lg blue" onclick="changeOpt('save')" >提交</button>
   			</c:if>
   		    <c:if test="${status=='5'}">
        		<button type="button" class="btn btn-lg red" onclick="doApply();">保存并上报</button>
        	</c:if>
            <button type="button" class="btn btn-lg red" onclick="goBack();">取消</button>
        </div>
    </div>
    <div class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" 
			aria-labelledby="mySmallModalLabel" aria-hidden="true" id="versionText">
  			<div class="modal-dialog modal-sm">  			
  				<div class="modal-content"> 
  					<div class="modal-body" >	
  					    <div class="col-xs-6">
  					      <input id="labelName" type="text" class="form-control" placeholder="输入名称"/>
  					    </div>
  					    <button type="button" class="btn btn-primary"  onclick="optNode();">确定</button>
						<button type="button" class="btn btn-primary" onclick="closeWindow()">关闭</button>				
					</div>
				</div>
  			</div>
	</div>	
	<div class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" 
			aria-labelledby="myLargeModalLabel" aria-hidden="true" id="scoreText">
  			<div class="modal-dialog modal-lg">  			
  				<div class="modal-content"> 
  					<div class="modal-body" >	
  					    <div class="col-xs-10">
  					      <input id="scoreDesc" type="text" class="form-control" placeholder="输入分数设置说明"/>
  					    </div>
  					    <button type="button" class="btn btn-primary"  onclick="optScoreDescNode();">确定</button>
						<button type="button" class="btn btn-primary" onclick="closeScoreWindow()">关闭</button>				
					</div>
				</div>
  			</div>
	</div>	
	
		
    </form>
</div>
<script type="text/javascript">
function goBack(){
	if('${operateFrom}'=='TASK_LIST_PAGE'){
		window.location.href = "${path}/TaskAction/toList.action"; 
	}else if('${targetCol}'!='')
	{
		window.location.href = '${path}/res/wf/gotoCheck.action?libType='+$("#libType").val()+'&objectId='+ objectId + '&operateFrom=MANAGE_PAGE'+'&goBackTask='+$("#goBackTask").val();
	}else{
		parent.queryForm();
	}
}
	function doApply(){
		$("#saveAndApply").val("1");
		changeOpt('update');
		$("#coreData").submit();
	}
</script>
<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
<script type="text/javascript" src="${path}/bres/knowledgeTree.js"></script>
</body>
</html>