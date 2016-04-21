<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>基础资源管理</title>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/autocomplete/jquery.bigautocomplete.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/autocomplete/jquery.bigautocomplete.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/jxmenu/js/jquery-powerFloatEdit-min.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
		.topnav a.as {
		  background: #428BCA;
		  padding: 7px 14px 7px;
		  text-decoration: none;
		  font-weight: bold;
		  color: #fff;
		  -webkit-border-radius: 4px;
		  -moz-border-radius: 4px;
		  border-radius: 4px;
		}
	</style>
	<script type="text/javascript">
	var detail = '${detail}';
	var data='';
	var num = 1;
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
	    	 $("select[type=multiSelEle]").multipleSelect({
	 			multiple: true,
	 			multipleWidth: 160,
	 			width: "100%"
	 		});
	    	$("select[type=multiSelEle]").multipleSelect("refresh");
	    	 
	    	if($('#objectId').val() != -1){
	    		initRalations();
	    	}
		    $('#fileData').uploadify({
		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
		        'uploader' : '${path}/uploadFileToTemp.action',
		        'debug' : false,
		        //是否自动上传
		        'auto':false,
		        'multi':false,
		        'queueSizeLimit':1,
		        'buttonText':'选择文件',
		        'removeCompleted':false,
// 		        'fileTypeDesc': "请选择zip或rar文件",
// 		        'fileTypeExts':'*.zip;*.rar',
		        'onUploadStart': function (file) { 
		        	uploadFileWaiting = $.waitPanel('正在上传文件...',false);
            	},
		        'onSelect':function(file){
		       		cFileType = file.type;
// 		       		getExtentMeta();
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
// 		        	if(data.typeStatus=="0"){
		        	if(data.status == 0){
		        		//path保存
		        		md5 = data.md5;
		        		$('#uploadFile').val(data.uploadFile);
		        		hasFile = 0;
		        		info = "上传成功";
		        		fileStatus = 0;
							var filePath = $('#uploadFile').val();
							if(filePath !=''){
								saveRes();
								uploadFileWaiting.close();
							}
		        	}else{
		        		$.alert("文件大小为0KB");
		        	}
// 		        	}else{
// 		        		$.alert("只能添加压缩文件zip或rar");
// 		        	}
		        	$('#' + file.id).find('.data').html(' - ' + info);
		        	uploadFileWaiting.close();
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
			    			var objectId = $('#objectId').val();
			    			if(objectId!="-1" && objectId!=""){
			    				//防止重复提交
			    				if(num ==1){
			    					saveRes();
			    				}
			    			}else{
			    				if(num ==1){
 									checkRepeat();
			    				}
			    			}
			    			num++;
					}
				}
			});
			var datas = '${ztreeJson}';
			if(datas!=""){
				zNodes = eval('('+datas+')');
				initZtree(zNodes);
			}else{
				$.fn.zTree.init($("#directoryTree"), directorySetting, null);
			}
			
			var completeField = $("input[id^=completeField]");
			for(var i=0;i<completeField.length;i++){
				searchComplete(completeField[i].value);
			}
	     });
			function initZtree(zNodes){
				$.fn.zTree.init($("#directoryTree"), directorySetting, zNodes);
			}
			function saveRes(){
				saveResWaiting = $.waitPanel('正在保存资源...',false);
				var url = "";
				if($("#saveAndApply").val()==1){
					url = "${path}/res/wf/doSaveAndSubmit.action?creator="+$('#creator').val()+"&creatTime="+$('#creatTime').val();
				}else{
					url = "${path}/publishRes/saveRes.action?creator="+$('#creator').val()+"&creatTime="+$('#creatTime').val();
				}
				$('#coreData').ajaxSubmit({
					url: url,//表单的action
	 				type: 'post',//方式
	 				success:(function(response){
	 					if('${operateFrom}'=='TASK_LIST_PAGE'){
	 						window.location.href = "${path}/TaskAction/toList.action"; 
						}else{
							if(response == ''){
								if($('#targetNames').val()!=""){
									parent.selectTargetRes();
								}else{
									parent.queryForm();
								}
// 								parent.showTarget('1');
// 								parent.queryForm();
		 					}else{
								$.showTips(response,5,'');
		 					}
						}
	 					saveResWaiting.close();
	           		})
	 			});
			}
			function removeHoverDom(treeId, treeNode) {
				$("#addBtn_"+treeNode.tId).unbind().remove();
				$("#addResBtn_"+treeNode.tId).unbind().remove();
				$("#infoBtn_"+treeNode.tId).unbind().remove();
				$("#removeBtn_"+treeNode.tId).unbind().remove();
				$("#removeFileBtn_"+treeNode.tId).unbind().remove();
				$("#editBtn_"+treeNode.tId).unbind().remove();
				$("#editFileRes_"+treeNode.tId).unbind().remove();
				$("#sensitiveWords_"+treeNode.tId).unbind().remove();
			};
			function beforeDrag(treeId, treeNodes) {
				return false;
			}
			function beforeRemove(treeId, treeNode) {
				className = (className === "dark" ? "":"dark");
				var zTree = $.fn.zTree.getZTreeObj("directoryTree");
				zTree.selectNode(treeNode);
				return true;
			}
			function onClickNode(event, treeId, treeNode, clickFlag) {
				addHoverDom(treeId, treeNode);
			}
			function addHoverDom(treeId, treeNode) {
				var zTree = $.fn.zTree.getZTreeObj("directoryTree");
				var sObj = $("#" + treeNode.tId + "_span");
				if (treeNode.editNameFlag==false && $("#infoBtn_"+treeNode.tId).length==0){ //都有预览
					var infoStr = "<span class='button preLook' id='infoBtn_" + treeNode.tId
					+ "' title='预览' onfocus='this.blur();'></span>";
					 sObj.after(infoStr);
					var btnRes = $("#infoBtn_"+treeNode.tId);
					var filePath=treeNode.path+",";
					var filePaths="";
					var isImageType = "jpgpngbmptiftiffjpeggif";
					var objectId = $('#objectId').val();
					var fileObjectId = treeNode.object;
					var fileZhName = treeNode.name;
					filePath = replaceAllString(filePath,"\\", "/");
					fileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.lastIndexOf("."));
					var flag = false;
					if (btnRes) btnRes.bind("click", function(){
						if(treeNode.children!=undefined&&treeNode.children.length>0){
							flag = true;
							for(var y = 0;y<treeNode.children.length;y++){
								var imageType = treeNode.children[y].path;
								imageType = imageType.substring(imageType.lastIndexOf(".")+1,imageType.length);
								if(isImageType.indexOf(imageType)>=0){
								    filePaths = filePaths+treeNode.children[y].path+",";
								}
							}
						}
						if(filePaths!=""){
							filePath = filePaths;
						}
						filePath = filePath.substring(0,filePath.length-1);
						if(flag){
							readFileOnline(filePath,fileObjectId,fileType,fileZhName);
						}else{
							var fileType = filePath.substring(filePath.lastIndexOf(".")+1,filePath.length);
							if(fileType.indexOf("/")>=0){
								$.alert("此文件不支持预览");
								return;
							}
							readFileOnline(filePath,fileObjectId,fileType,fileZhName,fileName); 
						}
						//相对路径
					});
				}
				if(treeNode.isDir== "2"){ //文件
					 var removeFileStr = "<span class='button remove' id='removeFileBtn_" + treeNode.tId+ "' title='删除' onfocus='this.blur();'></span>";
					 sObj.after(removeFileStr);
					 var removeFileBtn = $("#removeFileBtn_"+treeNode.tId);
					  if (removeFileBtn) removeFileBtn.bind("click", function(){
						  $.confirm('确定要删除该文件吗？', function(){
							  nodes = zTree.getSelectedNodes(),
								treeNode = nodes[0];
								var fileObjectId = treeNode.object;
								var name = treeNode.name;
								var path = treeNode.path;
								var objectId = $('#objectId').val();
								$.post("${path}/publishRes/deleteNode.action",{ caId: objectId,name:name,deleteType:"deleteFile",path:path,fileObjectId:fileObjectId},
										function(data){
											if(data=='-1'){
												$.alert('删除节点异常!');
											}else{
												zTree.removeNode(treeNode);
											}
							    });
						  });
					  });
					  var editFileRes = "<span class='button edit' id='editFileRes_" + treeNode.tId+ "' title='编辑文件元数据' onfocus='this.blur();'></span>";
					  sObj.after(editFileRes);
					  var editFileResBtn = $("#editFileRes_"+treeNode.tId);
					  if (editFileResBtn) editFileResBtn.bind("click", function(){
							nodes = zTree.getSelectedNodes(),
							treeNode = nodes[0];
							var fileObjectId = treeNode.object;
							var name = treeNode.name;
							var path = treeNode.path;
							$.openWindow("${path}/publishRes/toEditFileRes.action?fileObjectId="+fileObjectId+"&publishType="+$('#publishType').val(),'编辑文件元数据',1000,600);
					  });
					  var sensitiveWords = "<span class='button word' id='sensitiveWords_" + treeNode.tId+ "' title='敏感词' onfocus='this.blur();'></span>";
					  sObj.after(sensitiveWords);
					  var sensitiveWordsBtn = $("#sensitiveWords_"+treeNode.tId);
					  if (sensitiveWordsBtn) sensitiveWordsBtn.bind("click", function(){
							  nodes = zTree.getSelectedNodes(),
								treeNode = nodes[0];
								var fileObjectId = treeNode.object;
								var name = treeNode.name;
								var path = treeNode.path;
								name = encodeURI(encodeURI(name));
								filterWord(path,'${path}',name);
					  });
					  
				}
				if (treeNode.isDir =="1"){ //目录
					var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
					+ "' title='添加目录' onfocus='this.blur();'></span>";
					if(treeNode.nodeId!=1){
						addStr += "<span class='button edit' title='编辑' id='editBtn_" + treeNode.tId + "'></span>";
						addStr += "<span class='button remove' id='removeBtn_" + treeNode.tId+ "' title='删除' onfocus='this.blur();'></span>";
					}
					sObj.after(addStr);
					var addResStr = "<span class='button addFile' id='addResBtn_" + treeNode.tId
					+ "' title='添加文件' onfocus='this.blur();'></span>";
				    sObj.after(addResStr);
					var btnRes = $("#addResBtn_"+treeNode.tId);
					if (btnRes) btnRes.bind("click", function(){
						var nodeId=treeNode.nodeId;
						var parentPath=treeNode.path;
						var parentName=treeNode.name;
						var md5 = "";
						if(treeNode.children && treeNode.children.length>0){
							for(var i =0;i<treeNode.children.length;i++){
								if(treeNode.children[i].Md5 && treeNode.children[i].Md5!='undefined' && treeNode.children[i].Md5!=''){
									md5 = md5+treeNode.children[i].Md5+",";
								}
							}
						}
						//+"&taskFlag="+$('#taskFlag').val()
// 						alert($('#taskFlag').val());
// 						 parentName = encodeURI(parentName);
						 parentName = encodeURI(encodeURI(parentName));
						 parentPath = encodeURI(encodeURI(parentPath));
						 nodeId = encodeURI(encodeURI(nodeId));
						var url="${path}/publishRes/selFile.jsp?nodeId="+nodeId+'&parentName='+parentName+'&parentPath='+parentPath+"&objectId="+$('#objectId').val()+"&MD5="+md5+"&taskFlag="+$('#taskFlag').val();
						$.openWindow(url,'添加文件',1000,350);
					});
					var btn = $("#addBtn_"+treeNode.tId);
					if (btn) btn.bind("click", function(){
						$("#labelName").val('');
						$('#nodeOpt').val('add');
						$("#versionText").modal('show');
						return false;
					});
					  var btnEdit = $("#editBtn_"+treeNode.tId);
					  if (btnEdit) btnEdit.bind("click", function(){
						  nodes = zTree.getSelectedNodes(),
							treeNode = nodes[0];
							$("#labelName").val(treeNode.name);
							$('#nodeOpt').val('edit');
							$("#versionText").modal('show');
					  });
					  var removeBtn = $("#removeBtn_"+treeNode.tId);
					  if (removeBtn) removeBtn.bind("click", function(){
						  $.confirm('确定要删除该文件夹吗？', function(){
							  nodes = zTree.getSelectedNodes(),
								treeNode = nodes[0];
								var name=treeNode.name;
								var path=treeNode.path;
								var objectId = $('#objectId').val();
								var fileObjectId = treeNode.object;
								fileObjectId = getChildObjectId(treeNode,fileObjectId);
								$.post("${path}/publishRes/deleteNode.action",{ caId: objectId,path:path,name:name,deleteType:"delete",fileObjectId:fileObjectId},
										function(data){
											if(data=='-1'){
												$.alert('删除节点异常!');
											}else{
												zTree.removeNode(treeNode);
											}
							    });
						  });
					  });
				}
			};
			function getChildObjectId(treeNode,fileObjectId){
				var nodes = treeNode.children;
				if(nodes && nodes.length>0){
					for(var i=0;i<nodes.length;i++){
						var childNode = nodes[i];
						fileObjectId = fileObjectId+","+childNode.object;
						fileObjectId = getChildObjectId(childNode,fileObjectId);
					}
				}
				return fileObjectId;
			}
			function editNode(){
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var nodes = treeObj.getSelectedNodes();
				var newName = $('#labelName').val();
				var node=nodes[0];
				var fileObjectId = node.object;
				var oldName = node.name;
				var caObjectId = $('#objectId').val();
				node.name=$('#labelName').val();
				$.post("${path}/publishRes/renameNode.action",{ newName:newName,caObjectId:caObjectId,oldName:oldName,fileObjectId:fileObjectId},
						function(data){
							if(data=='-1'){
								$.alert('更新节点异常!');
							}else{
								treeObj.updateNode(node);
							}
							closeWindow();
			    });
				
			}
			function addNode(){
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var nodes = treeObj.getSelectedNodes();
				var treeNode=nodes[0];
				var rootPath = '${rootPath}';
				var name = $('#labelName').val();
				var parentPath = treeNode.path;
				var caObjectId = $('#objectId').val();
				$.post("${path}/publishRes/addNode.action",{rootPath:rootPath, parentPath:parentPath,caObjectId:caObjectId,name:name},
						function(data){
							data = eval('('+data+')');
							if(data.status=='-1'){
								$.alert('添加目录出错，请联系系统管理员!');
							}else{
								treeObj.addNodes(treeNode, {nodeId:data.nodeId,pid:data.parentPath, name:name,path:data.path,isDir:"1"});
							}
							closeWindow();
			    });
			}
			function addRootNode(){
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var rootPath = '${rootPath}';
				var name = $('#labelName').val();
				var caObjectId = $('#objectId').val();
				$.ajax({
					url:'${path}/publishRes/addNode.action?rootPath='+rootPath+"&caObjectId="+caObjectId+"&name="+name+"&root=1",
				    type: 'post',
				    datatype: 'text',
				    async:false,
				    success: function (data) {
				    	data = eval('('+data+')');
				    	if(data.status=='1'){
							treeObj.addNodes(null, {nodeId:data.nodeId,pid:'-1', name:name,path:data.path,isDir:"1",object:data.objectId});
							closeWindow();
						}else{
							$.alert('添加目录出错，请联系系统管理员!');
						}
				    }
				});
			}
			function setNodeFile(id,pid,fileName,isDir,path,md5,object){
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var treeNode = treeObj.getNodeByParam("nodeId", pid, null);
				treeObj.addNodes(treeNode, {nodeId:id,pid:pid,name:fileName,isDir:isDir,path:path,Md5:md5,object:object});
			}
			function optNode(){
				var name = $('#labelName').val();
				if(name==null || name==''){
					$.alert("输入的名称不能为空");
				}else if(name.length>40){
					$.alert("输入的名称长度不能大于40");
				}else{
					var nodeOpt=$('#nodeOpt').val();
					if(nodeOpt=='add'){
						addNode();
					}
					if(nodeOpt=='edit'){
						editNode();
					}
					if(nodeOpt=='addRoot'){
						addRootNode();
					}
				}
				
			}
			function closeWindow(){
				$("#versionText").modal('hide');
			}
			function addRootOpt(){
				$("#labelName").val('');
				$('#nodeOpt').val('addRoot');
				$("#versionText").modal('show');
			}
			//查重
			function checkRepeat(){
				var url = "${path}/publishRes/checkRepeat.action?publishType="+$('#publishType').val()+"&status="+$('#status').val();
				$('#coreData').ajaxSubmit({
					url: url,//表单的action
	 				type: 'post',//方式
	 				success:(function(data){
					data = eval('('+data+')');
					if(data.checkField==undefined){
						if(data.status == 1){
							num = 1;
							confirmRepeat(data.res,data.modifly);
						}else{
							var len1 = $("#fileData").data('uploadify').queueData.queueLength;
							if(len1 > 0){
								$('#fileData').uploadify('upload','*');
							}else{
								saveRes();
							}
						}
					}else{
							num = 1;
							$.alert("查重字段"+data.checkField+"为空");
						}
	           		})
	 			});
			}
			var relationIds = '';
			function confirmRepeat(res,modifly){
				var modiFlag = '${modiFlag}';
				var msg = '<table border="0" cellpadding="0" cellspacing="0" >';
				var resVersions = new Array();
					for(var i = 0;i<res.cas.length;i++){
					var obj = res.cas[i];
					msg+= '<tr style="white-space:nowrap;"><td><input type="radio" id="repeatResId" name="repeatResId" value="'+obj.objectId+'='+obj.res_version+'"/></td><td><b>标题：</b>'
					+obj.metadataMap.title;
					if(typeof(obj.objectId) != 'undefined' && obj.objectId != ''){
						msg+= "<a class=\"btn hover-red\" href=\"javascript:detailOres('"+obj.objectId+"');\"><i class=\"fa fa-sign-out\"></i>详细</a>";
					}
					resVersions.push(obj.resVersion);
					msg+= '</td></tr>';
					if(relationIds == ''){
						relationIds += obj.objectId;
					}else{
						relationIds += ',' + obj.objectId;
					}
				}
				
				msg +='<script type=\"text/javascript\">function detailOres(objectId){$.openWindow(\"${path}\/publishRes\/toDetail.action?publishType=${param.publishType}&flagSta=3&objectId=\"+objectId,\"资源详细\",1170,500);}<\/script>';
				$.simpleDialog({
					title:'存在重复版本', 
					content:'<font color=\"red\">系统检测到重复资源，请检查！</font>'+msg, 
					button:[{name:'忽略',callback:function()
			        	{
			        	repeatCancel =1;
			        	}
			       		},
			       		{name:'新版本',callback:function(){
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
				        	$('#repeatType').val("1");
// 				        	if(modifly!=""||modiFlag!=""){
// 				        		saveRes();
// 				        	}else 
				        	if($("#fileData").data('uploadify').queueData.queueLength>0){
				        		$('#fileData').uploadify('upload','*');
				        	}else{
				        		saveRes();
				        	}
				        }},
				        {name:'元数据增量(文件增量)',callback:function(){
							var checkedObjectId = top.$('input:radio[name="repeatResId"]:checked').val();
							if(typeof(checkedObjectId) != 'undefined' && checkedObjectId != ''){
								var checkedObjectIdAndVersion = checkedObjectId.split("=");
								$('#objectId').val(checkedObjectIdAndVersion[0]);
								$('#resVersion').val(checkedObjectIdAndVersion[1]);
								$('#repeatType').val("2");
// 								if(modifly!=""||modiFlag!=""){
// 									saveRes();
// 					        	}else 
					        	if($("#fileData").data('uploadify').queueData.queueLength>0){
					        		$('#fileData').uploadify('upload','*');
					        	}else{
					        		saveRes();
					        	}
							}else{
								$.alert('请选择要覆盖的资源');
								return false;
							}
							num = 0;
						}},
				        {name:'元数据覆盖(文件增量)',callback:function(){
				        	var checkedObjectId = top.$('input:radio[name="repeatResId"]:checked').val();
							if(typeof(checkedObjectId) != 'undefined' && checkedObjectId != ''){
								var checkedObjectIdAndVersion = checkedObjectId.split("=");
								$('#objectId').val(checkedObjectIdAndVersion[0]);
								$('#resVersion').val(checkedObjectIdAndVersion[1]);
								$('#repeatType').val("4");
// 								if(modifly!=""||modiFlag!=""){
// 									saveRes();
// 					        	}else 
					        	if($("#fileData").data('uploadify').queueData.queueLength>0){
					        		$('#fileData').uploadify('upload','*');
					        	}else{
					        		saveRes();
					        	}
							}else{
								$.alert('请选择要覆盖的资源');
								return false;
							}
						}}
					]
				});
			}
			//自动补全功能
			function searchComplete(fileField){
					var fieldName = encodeURI(encodeURI($('#'+fileField).val()));
					var url = "${path}/target/autoCom.action?fileField="+fileField+"&fileFieldValue="+fieldName+"&publishType="+$('#publishType').val();
		    		$('#'+fileField).bigAutocomplete({url:url});
			}
// 			function showHide(fileField){
// 				$('#'+fileField).val("");
// 			}
			function changeOpt(optVal){
				$('#opt').val(optVal);
				jQuery('#coreData').submit();
			}
			//提交并上报
			function doApply(){
				$("#saveAndApply").val("1");
				changeOpt('update');
			}
			//人员，单位
// 			function peopleUnit(url,title){
// 				alert(url);
// 				var d = art.dialog.open("system/dataManagement/dataDict/importDir.jsp?pid="+$("#dictNameId").val(),
// 		         {
// 				   lock:true,
// 		           title: "导入数据字典项",
// 		           width: "450px",
// 		           height: "200px",
// 		           close: function () {
// 		               query();
// 		               //window.location.reload(true);
// 		           }
// 		       });
// 				d.showModal();
// 				//$.openWindow("system/dataManagement/dataDict/importDir.jsp?pid="+$("#dictNameId").val()+"&iframeName="+iframeName, '导入数据字典项',450,200);
// 			}
	</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap ">
 <div class="panel panel-default">
    <div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li class="active">资源管理
					</li>
					<li class="active">资源视图</li>
					  <c:if test="${objectId=='' || objectId=='-1'}">
					  <li class="active">资源添加</li>
					  </c:if>
					  <c:if test="${objectId!='-1'}">
					  <li class="active">资源修改</li>
					  </c:if>
				</ol>
	</div>
 </div>	
<form:form action="#" id="coreData" class="form-horizontal" name="coreData"  modelAttribute="frmWord" method="post">
<%--  <app:bsmenu name="metadataMap['subjectclass']" indexTag="relation_house" cnName="国籍" id="metadataMap['subjectclass']" selectedVal="1,2" maxNumber="1"/> --%>
<%--  <app:bsPersonnelUnit name="metadataMap['subjectclass']" id="metadataMap['subjectclass']" indexTag="relation_house" cnName="国籍"  selectedVal="1,2"/> --%>
 <app:ObjectMetadataCreateTag   object="${bookCa}" publishType="${publishType}"/>
 <input type="hidden" id="nodeOpt" name="nodeOpt" value=""/>
 <input type="hidden" id="status"  name="status" value="${status}"/>
 <input type="hidden" id="taskFlag"  name="taskFlag" value="${taskFlag}"/>
 <input type="hidden" id="uploadFile" name="uploadFile" value=""/>
 <input type="hidden" id="creator" name="creator" value="${creator}"/>
 <input type="hidden" id="creatTime" name="creatTime" value="${creatTime}"/>
 <input type="hidden" id="targetNames" name="targetNames" value="${targetNames}"/>
 <input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
 <input type="hidden" id="repeatType" name="repeatType" value=""/>
 <input type="hidden" id="publishType" name="publishType" value="${publishType}"/>
 <input type="hidden" id="cbclassFieldValue" name="cbclassFieldValue" value="${cbclassFieldValue}"/>
 <input type="hidden" id="cbclassField" name="cbclassField" value="${cbclassField}"/>
 <input type="hidden" id="saveAndApply" name="saveAndApply" value=""/>
 <input type="hidden" id="wfTaskId" name="wfTaskId" value="${wfTaskId}"/>
 <input type="hidden" id="metadataMap['cover']" name="metadataMap['cover']" value="${cover}"/>
 <div class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" 
			aria-labelledby="mySmallModalLabel" aria-hidden="true" id="versionText">
  			<div class="modal-dialog modal-sm">  			
  				<div class="modal-content"> 
  					<div class="modal-body" >	
  					    <div class="col-xs-6">
  					      <input id="labelName" type="text" class="form-control" placeholder="输入名称"/>
  					    </div>
  					    <button type="button" class="btn btn-primary"  onclick="optNode();">确定</button>
						<button type="button" class="btn btn-primary" onclick="closeWindow();">关闭</button>				
																				
					</div>
				</div>
  			</div>
	</div>	
          <div class="portlet portlet-border">
                     <c:if test="${objectId=='-1'}">
	        <div class="portlet-title">
	            <div class="caption">文件信息</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                 <div class="row">
	                 <div class="col-md-12">
						<div class="form-wrap">
							<label class="control-label col-md-2">是否解压：</label>
							<div class="col-md-10">
								<div class="form-group">
									<input type="radio" id="isCompress" name="isCompress"
										value="true" class="radio-inline" /> 是 
										<input type="radio" id="isCompress" name="isCompress" value="false"
										class="radio-inline"  checked="checked"/> 否
								</div>
							</div>
						</div>
					</div>
                 </div>
                    <div class="row" id="fileDiv">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label class="control-label col-md-2">文件：</label>
                                <div class="col-md-10">
                                	<input type="hidden" id="format" name="commonMetaData.commonMetaDatas['format']" />
                                    <input type="file" name="fileData" id="fileData" class="validate[required]" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            </c:if>
            <c:if test="${objectId!='-1'}">
	        <div class="portlet-title">
	            <div class="caption">资源文件 
	            <button type="button" class="fa fa-plus" onclick="addRootOpt()" title="添加目录"></button>
	            </div> 
	        </div>
            <div class="portlet-body" >
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <div class="col-md-12 zTreeBackground" style="width:210%">
                                	 <ul id="directoryTree" class="ztree form-wrap" style="overflow:auto;"></ul>
                                </div>
                                
                            </div>
                        </div>
                       
                    </div>
                   
                </div>
            </div>
            </c:if>
        </div>
          <c:if test="${objectId!='-1'}">
        <%@ include file="relationRes.jsp" %>
        </c:if>
        <div class="form-actions" align="center">
        <button type="submit" class="btn btn-lg red">提交</button>
        <button type="button" class="btn btn-lg blue" onclick="goBackTask();">返回</button>
</div>
 </form:form>
 <script type="text/javascript">
 	function goBackTask(){
		if('${operateFrom}'=='TASK_LIST_PAGE'){
			window.location.href = "${path}/TaskAction/toList.action"; 
		}else{
			if($('#targetNames').val()!=""){
				parent.selectTargetRes();
			}else if($('#cbclassFieldValue').val()!=""){
				var url = '${path}/publishRes/publishResList.action?publishType='+parent.$('#publishType').val()+'&status='+parent.$('#status').val()+'&offline=${param.offline}';
		 		url +='&'+$('#cbclassField').val()+'='+$('#cbclassFieldValue').val();
		 		parent.$('#work_main').attr('src',url);
			}else{
				parent.queryForm();
			}
		}
	}
 </script>
</div>
</body>
</html>