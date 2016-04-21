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
 	ul.ztree li span.button.ico_res{ background-image:url("selFile.png") ;margin-right:0px; vertical-align:top; *vertical-align:middle}
	 ul.ztree {margin-top: 10px;border: 1px solid #617775;background: #f0f6e4;width:470px;height:360px;overflow-y:scroll;overflow-x:auto;}
	</style>
	<script type="text/javascript">
		var objectId = '-1';
		var md5 = '';//刚上传文件的md5值
		var data='';
		var fileCompleteNum = 0;//完成总数
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
			var publishType = '${publishType}';
			objectId = $('#objectId').val();
			if(objectId!='-1'){
				var modifyLogs = '${modifyLogs}';
				for(var i=0;i<modifyLogs.length;i++){
					var modifyLog = modifyLogs[i];
					$("#modifyContent").val(modifyLog.resId);
				}
				var datas = '${ztreeJson}';
				zNodes = eval('('+datas+')');
				$.fn.zTree.init($("#directoryTree"), directorySetting, zNodes);
			}
			//初始化上传组件
		    $('#fileData').uploadify({
		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
		        'uploader' : '${path}/uploadFileToTemp.action;jsessionid=<%=session.getId()%>',
		        'debug' : false,
		        //是否自动上传
		        'auto':false,
		        'multi':false,
		        'fileTypeExts':'*.zip;*.rar',
		        'fileTypeDesc':'支持的文件格式',
		        'buttonText':'上传文件',
		        'removeCompleted':false,
		        //上传数量
		        'queueSizeLimit' : 1,
		        'onUploadStart': function (file) {
		        	uploadFileWaiting = $.waitPanel('正在上传文件...',false);
            	},
		        'onSelect':function(file){
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
		        //检测FLASH失败调用
		        'onFallback':function(){
		            $.alert("您未安装FLASH控件，无法上传图片！请安装FLASH控件后再试。");
		        },
		        'onUploadSuccess': function(file, data, response) {  
		        	var info = '<font color="red">上传失败</font>';
		        	data = eval('('+data+')');
		        	if(data.status == 0){
		        //		md5 = data.md5;
		        		//path保存
		        		$('#uploadFile').val(data.uploadFile);
		        		info = "上传成功";
		        		fileCompleteNum=fileCompleteNum+1;
		        		checkRepeat();
		        	}
		        	$('#' + file.id).find('.data').html(' - ' + info);
		        	uploadFileWaiting.close();
            	}
		    });
			//元数据提交
		    $('#coreData').validationEngine('attach', {
				relative: true,
				validateNonVisibleFields:true,
				validateDisplayNoneFields:false,
				overflownDIV:"#divOverflown",
				promptPosition:"bottomLeft",
				maxErrorsPerField:1,
				binded:true,
				onValidationComplete:function(form,status){
					if(status){
						if($("#fileData").data('uploadify') !=undefined){
							var len1 = $("#fileData").data('uploadify').queueData.queueLength;
							if(len1 > 0){
								$('#fileData').uploadify('upload','*');
							}else{
								if(objectId == ''){
									$.alert('请选择上传的文件！');
								}else{
									var opt=$('#opt').val();
									if(opt=='update'){
										updateForm();
									}else if(opt=='save'){
										checkRepeat();
									}
								}
							}
						}else{
							if(objectId != '' && objectId != '-1'){
								var opt=$('#opt').val();
								if(opt=='update'){
									updateForm();
								}else if(opt=='save'){
									checkRepeat();
								}
							}
						}
					}
				}
			});
		    var modifyLogsArray = '${modifyLogsArray}';
		    //alert(modifyLogsArray);
		    var count = "";
			if(modifyLogsArray!=""){
				var field = "";
				array = eval('('+modifyLogsArray+')');
				for(var i=0;i<array.length;i++){
					if(array[i].modify_old=="addFile"){
						field = "添加文件:'"+array[i].modify_new+"'\n";
					}else if(array[i].modify_old=="delete"){
						field = "删除节点:'"+array[i].modify_new+"'\n";
					}else if(array[i].modify_old=="addList"){
						field = "添加节点:'"+array[i].modify_new+"'\n";
					}else if(array[i].modify_old=="deleteFile"){
						field = "删除文件:'"+array[i].modify_new+"'\n";
					}else{
						field = "字段:"+array[i].field+":'"+array[i].modify_old+"'修改后→'"+array[i].modify_new+"'\n";
					}
					count = count+field;
				}
				$('#modifyContent').val(count);
			}
		});
		
		 //查重
		function checkRepeat(){
			var publishType = '${publishType}';
			$('#publishType').val(publishType);
			$.post('${path}/publishRes/checkRepeat.action',{source:$('#source').val(),publishType:publishType,title:$('#title').val(),creator:$('#creator').val(),ISBN:$('#ISBN').val()},function(data){
				data = eval('('+data+')');
				if(data.status == 1){
					confirmRepeat(data.res);
				}else{
					saveForm(0);
				}
			});
		}
		 //查重
		function topCheckRepeat(){
			var publishType = '${publishType}';
			$('#publishType').val(publishType);
			$.post('${path}/publishRes/checkRepeat.action',{source:$('#source').val(),publishType:publishType,title:$('#title').val(),creator:$('#creator').val(),ISBN:$('#ISBN').val()},
					function(data){
				data = eval('('+data+')');
				if(data.status == 1){
					topConfirmRepeat(data.res);
				}
			});
		}
		 //顶部判断四项字符是否重复
		function topConfirmRepeat(res){
			var msg = '<table border="0" cellpadding="0" cellspacing="0" >';
			var resVersions = new Array();
			for(var rs in res){
				var obj = res[rs];
				msg+= '<tr style="white-space:nowrap;"><td><input type="radio" id="repeatResId" name="repeatResId" value="'+obj.objectId+'='+obj.commonMetaData.commonMetaDatas.res_version+'"/></td><td><b>标题：</b>'
				+obj.commonMetaData.commonMetaDatas.title+' <b>关键字：</b>'+obj.commonMetaData.commonMetaDatas.keywords+' <b>版本：</b>V'+obj.commonMetaData.commonMetaDatas.res_version+'.0'+"<a class=\"btn hover-red\" href=\"javascript:detailOres('"+obj.objectId+"');\"><i class=\"fa fa-sign-out\"></i>详细</a></td></tr>";
				resVersions.push(obj.commonMetaData.commonMetaDatas.res_version);
			}
			msg += '</table>';
			msg +='<script type=\"text/javascript\">function detailOres(objectId){$.openWindow(\"${path}\/publishRes\/detail.action?libType=${param.libType}&flag=flag&flagSta=2&objectId=\"+objectId,\"资源详细\",800,450);}<\/script>';
			$.simpleDialog({
				title:'存在重复版本', 
				content:'<font color=\"red\">系统检测到重复资源，请检查！</font>'+msg, 
				button:[
				        {name:'关闭',callback:function(){}}
				]
			});
		}
		function confirmRepeat(res){
			var msg = '<table border="0" cellpadding="0" cellspacing="0" >';
			var resVersions = new Array();
			for(var rs in res){
				var obj = res[rs];
				msg+= '<tr style="white-space:nowrap;"><td><input type="radio" id="repeatResId" name="repeatResId" value="'+obj.objectId+'='+obj.commonMetaData.commonMetaDatas.res_version+'"/></td><td><b>标题：</b>'
				+obj.commonMetaData.commonMetaDatas.title+' <b>关键字：</b>'+obj.commonMetaData.commonMetaDatas.keywords+' <b>版本：</b>V'+obj.commonMetaData.commonMetaDatas.res_version+'.0'+"<a class=\"btn hover-red\" href=\"javascript:detailOres('"+obj.objectId+"');\"><i class=\"fa fa-sign-out\"></i>详细</a></td></tr>";
				resVersions.push(obj.commonMetaData.commonMetaDatas.res_version);
			}
			msg += '</table>';
			msg +='<script type=\"text/javascript\">function detailOres(objectId){$.openWindow(\"${path}\/publishRes\/detail.action?libType=${param.libType}&flag=flag&flagSta=2&objectId=\"+objectId,\"资源详细\",800,450);}<\/script>';
			$.simpleDialog({
				title:'存在重复版本', 
				content:'<font color=\"red\">系统检测到重复资源，请检查！</font>'+msg, 
				button:[{name:'覆盖',callback:function(){
							var checkedObjectId = top.$('input:radio[name="repeatResId"]:checked').val();
							if(typeof(checkedObjectId) != 'undefined' && checkedObjectId != ''){
								var checkedObjectIdAndVersion = checkedObjectId.split("=");
								$('#objectId').val(checkedObjectIdAndVersion[0]);
								$('#resVersion').val(checkedObjectIdAndVersion[1]);
								saveForm('2');
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
				        	saveForm('1');
				        }},
				        {name:'忽略',callback:function(){}}
				]
			});
		}
		//保存表单数据
		function saveForm(repeatType){
			if(fileCompleteNum==0){
				$.alert('请选择上传的文件！');
				return ;
			}
			objectId = $('#objectId').val();
			saveResWaiting = $.waitPanel('正在保存资源...',false);
			$('#coreData').ajaxSubmit({
				url: '${path}/publishRes/saveRes.action?repeatType='+repeatType,//表单的action
 				method: 'post',//方式
 				success:(function(response){
 					saveResWaiting.close();
 					if('${targetPub}'!=''){
 						window.location.href ='${path}/pubres/wf/gotoCheck.action?objectId='+ objectId + '&operateFrom=MANAGE_PAGE';
 					}else if(response == ''){
						parent.queryForm();
 					}else{
						$.showTips(response,5,'');
 					}
 					
           		})
 			});
		}
		
		function updateForm(){
			saveResWaiting = $.waitPanel('正在保存资源...',false);
			var url = "";
			if($("#saveAndApply").val()==1){
				url = "${path}/pubres/wf/doSaveAndSubmit.action?operateFrom=${operateFrom}";
			}else{
				url ='${path}/publishRes/updateRes.action';
			}
			$('#coreData').ajaxSubmit({
				url: url,
 				method: 'post',//方式
 				success:(function(response){
 					saveResWaiting.close();
 					if('${operateFrom}'!=''){
						window.location.href = "${path}"+response;
					}else if('${targetPub}'!=''){
 						window.location.href ='${path}/pubres/wf/gotoCheck.action?objectId='+ objectId + '&operateFrom=MANAGE_PAGE'+'&goBackTask='+$('#goBackTask').val();
 					}else{
						if(response == ''){
							parent.queryForm();
	 					}else{
							$.showTips(response,5,'');
	 					}
					}
// 					if(response == ''){
// 						parent.queryForm();
//  					}else{
// 						$.showTips(response,5,'');
//  					}
           		})
 			});
		}
			var className = "dark";
			function beforeDrag(treeId, treeNodes) {
				return false;
			}
			function beforeEditName(treeId, treeNode) {
				className = (className === "dark" ? "":"dark");
				var zTree = $.fn.zTree.getZTreeObj("directoryTree");
				zTree.selectNode(treeNode);
				return true;
			}
			function beforeRemove(treeId, treeNode) {
				className = (className === "dark" ? "":"dark");
				var zTree = $.fn.zTree.getZTreeObj("directoryTree");
				zTree.selectNode(treeNode);
				return true;
			}
			function beforeRename(treeId, treeNode, newName, isCancel) {
				className = (className === "dark" ? "":"dark");
				if (newName.length == 0) {
					$.alert("节点名称不能为空.");
					var zTree = $.fn.zTree.getZTreeObj("directoryTree");
					setTimeout(function(){zTree.editName(treeNode)}, 10);
					return false;
				}
				return true;
			}
			function onRename(e, treeId, treeNode, isCancel) {
				var nodePath=treeNode.path;
				treeNode.path=nodePath.substring(0,nodePath.lastIndexOf('/'))+'/'+treeNode.name;
				updateNode(treeNode.nodeId);
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
			/**
			 * 替换
			 * @param str 源字符串
			 * @param sptr 要替换的字符串
			 * @param nstr 替换后的字符串
			 * @returns
			 */
			function replaceAllString(str, sptr, nstr) {
				if (str != null && str != "") {
					while (str.indexOf(sptr) != -1) {
						str = str.replace(sptr, nstr);
					}
				}
				return str;
			}
			function addHoverDom(treeId, treeNode) {
				var zTree = $.fn.zTree.getZTreeObj("directoryTree");
				var sObj = $("#" + treeNode.tId + "_span");
				if(treeNode.files==undefined){
					if (treeNode.isParent!=true&&treeNode.editNameFlag==false && $("#infoBtn_"+treeNode.tId).length==0){
						var infoStr = "<span class='button preLook' id='infoBtn_" + treeNode.tId
						+ "' title='预览' onfocus='this.blur();'></span>";
						 sObj.after(infoStr);
						var btnRes = $("#infoBtn_"+treeNode.tId);
						var filePath=treeNode.path;
						var fileObjectId = treeNode.object;
						filePath = replaceAllString(filePath,"\\", "/");
						if (btnRes) btnRes.bind("click", function(){
							readFileOnline(filePath,fileObjectId); //相对路径	
						});
					}
					if(treeNode.editNameFlag==false && $("#removeFileBtn_"+treeNode.tId).length==0){
						 var removeFileStr = "<span class='button remove' id='removeFileBtn_" + treeNode.tId+ "' title='删除' onfocus='this.blur();'></span>";
						 sObj.after(removeFileStr);
						 var removeFileBtn = $("#removeFileBtn_"+treeNode.tId);
						  if (removeFileBtn) removeFileBtn.bind("click", function(){
								nodes = zTree.getSelectedNodes(),
								treeNode = nodes[0];
								var nodeId=treeNode.nodeId;
								var parentNode = treeNode.getParentNode();
								var ztreeObjectId = treeNode.object;
								var path = parentNode.path+"/"+treeNode.name;
								var objectId = $('#objectId').val();
								var oldName = nodes[0].name;
								var deleteFile = "deleteFile";
								$.post("${path}/publishRes/deleteNode.action",{ caId: objectId,nodeId: nodeId,path:path,oldName:oldName,deleteFile:deleteFile,ztreeObjectId:ztreeObjectId},
										function(data){
											if(data=='-1'){
												$.alert('删除节点异常!');
											}
							    });
								zTree.removeNode(treeNode);
						  });
					}
				}
				if (treeNode.nodeId!=1&&treeNode.editNameFlag==false && $("#addResBtn_"+treeNode.tId).length==0||treeNode.files.length==0){
					var addResStr = "<span class='button addFile' id='addResBtn_" + treeNode.tId
					+ "' title='添加文件' onfocus='this.blur();'></span>";
					//alert(addResStr);
				    sObj.after(addResStr);
					var btnRes = $("#addResBtn_"+treeNode.tId);
					
					if (btnRes) btnRes.bind("click", function(){
						var nodeId=treeNode.nodeId;
						var parentPath=treeNode.path;
						var parentName=treeNode.name;
						parentPath=escape(encodeURIComponent(parentPath));
						parentName=escape(encodeURIComponent(parentName));
						var url="${path}/publishRes/toSelFile.action?nodeId="+nodeId+'&parentName='+parentName+'&parentPath='+parentPath+"&objectId="+$('#objectId').val();
						$.openWindow(url,'添加文件',1000,400);
					});
				}
				
				if (treeNode.editNameFlag==false && $("#addBtn_"+treeNode.tId).length==0||treeNode.files.length==0){
					var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
					+ "' title='添加目录' onfocus='this.blur();'></span>";
					//alert(addStr);
					
					if(treeNode.nodeId!=1){
						 addStr += "<span class='button edit' title='编辑' id='editBtn_" + treeNode.tId + "'></span>";
						addStr += "<span class='button remove' id='removeBtn_" + treeNode.tId+ "' title='删除' onfocus='this.blur();'></span>";
	                   
					 
					}
					sObj.after(addStr);
				   
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
							nodes = zTree.getSelectedNodes(),
							treeNode = nodes[0];
							var nodeId=treeNode.nodeId;
							var path=treeNode.path;
							var objectId = $('#objectId').val();
							var ztreeObjectId = treeNode.object;
							var oldName = nodes[0].name;
							$.post("${path}/publishRes/deleteNode.action",{ caId: objectId,nodeId: nodeId,path:path,oldName:oldName,ztreeObjectId:ztreeObjectId},
									function(data){
										if(data=='-1'){
											$.alert('删除节点异常!');
										}
						    });
							zTree.removeNode(treeNode);
					  });
					
					
				}
				
			};
			function removeHoverDom(treeId, treeNode) {
				
				$("#addBtn_"+treeNode.tId).unbind().remove();
				$("#addResBtn_"+treeNode.tId).unbind().remove();
				$("#infoBtn_"+treeNode.tId).unbind().remove();
				$("#removeBtn_"+treeNode.tId).unbind().remove();
				$("#removeFileBtn_"+treeNode.tId).unbind().remove();
				$("#editBtn_"+treeNode.tId).unbind().remove();
				
			};
			
			function setNodeFile(nodeId,fileName,filePath){
				//alert('execute setNodeFile'+'nodeId : '+nodeId+"fileName :"+fileName+" filePath: "+filePath);
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var treeNode = treeObj.getNodeByParam("nodeId", nodeId, null);
				var id=treeNode.pid + new Date().getTime();
				var xpath=treeNode.xpath+','+id;
				var file={name:fileName,path:filePath};
				var files=[file];
				treeObj.addNodes(treeNode, {nodeId:id,xpath:xpath,pid:nodeId,name:fileName,files:[]});
				//updateNode(id);
			}
			
			function updateNode(nodeId,oldPath,parentId){
				objectId = $('#objectId').val();
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var node = treeObj.getNodeByParam("nodeId", nodeId, null);
				node.caId=objectId;
				object = node.object;
				treeObj.updateNode(node);
				var fileObjectId = node.object;
				if(objectId=='-1'){
					closeWindow();
					return;
				}
				//var nodeOpt=$('#nodeOpt').val();
				var title=$("#title").val();
				var treeEditOldName=$('#editOldName').val();
				var fileFlag = $('#fileFlag').val();
				var fileObject = new Object();
				fileObject.path = oldPath;
				fileObject.objectId = objectId;
				if(oldPath!="1"){
					fileObject.pid = nodeId;
				}else{
					//拿title做标记
					title = "add";
					fileObject.pid = parentId;
					fileObject.id = nodeId;
				}
				fileObject.name = node.name;
				var newFilePath = node.path;
				var jsonFile = JSON.stringify(fileObject);
				$.post("${path}/publishRes/updateNode.action",{ title:title,jsonFile:jsonFile,treeEditOldName:treeEditOldName,fileFlag:fileFlag,fileObjectId:fileObjectId,newFilePath:newFilePath},
				function(data){
					data = eval('('+data+')');
					if(data.objectId=='-1'){
						$.alert('更新节点异常!');
						
					}else{
						node.objectId=data.objectId;
						treeObj.updateNode(node);
					}
					closeWindow();
			    });
				 $('#fileFlag').val("");
				
			}
			document.onkeydown = function (e) {
	            var e = window.event ? window.event : e;
	            if (e.keyCode == 13) {
	            	$("#versionText").modal('hide');
	            	return;
	            }
	        }
			document.onkeyup = function (e) {
	            var e = window.event ? window.event : e;
	            if (e.keyCode == 13) {
	            	$("#versionText").modal('hide');
	            	return;
	            }
	        }
			function addRootOpt(){
				$("#labelName").val('');
				$('#nodeOpt').val('addRoot');
				$("#versionText").modal('show');
			}
			function addRootNode(){
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var id= new Date().getTime();
				var xpath="-1"+','+id;
				var path=$("#bookPath").val()+'/'+$('#labelName').val();
				treeObj.addNodes(null, {nodeId:id,xpath:xpath, pid:'-1', name:$('#labelName').val(),path:path,files:[]});
				updateNode(id);
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
				$('#editOldName').val(nodes[0].name);
				var node=nodes[0];
				node.name=$('#labelName').val();
				var oldPath = node.path;
				var oldPathTemp = oldPath.substring(0,oldPath.lastIndexOf("/"));
				var newPath = oldPathTemp +"/"+$('#labelName').val();
				node.path = newPath;
				treeObj.updateNode(node);
				updateNode(node.nodeId,oldPath);
				
			}
			function addNode(){
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var nodes = treeObj.getSelectedNodes();
				var treeNode=nodes[0];
				var id=treeNode.pid + new Date().getTime();
				var xpath=treeNode.xpath+','+id;
				var path=treeNode.path+'/'+$('#labelName').val();
				parentId = treeNode.nodeId;
				treeObj.addNodes(treeNode, {nodeId:id,xpath:xpath, pid:treeNode.nodeId, name:$('#labelName').val(),path:path,files:[]});
				updateNode(id,"1",parentId);
				
			}
			function optNode(){
				var name = $('#labelName').val();
				if(name==null || name==''){
					$.alert("输入的名称不能为空");
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
			
	</script>
	
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap">
    <ul class="page-breadcrumb breadcrumb">
        <li>
            <a href="###">出版资源管理</a>
            <i class="fa fa-angle-right"></i>
        </li>
        <li>
            <a href="###">资源管理</a>
            <i class="fa fa-angle-right"></i>
        </li>
        <li><a href="###">修改资源</a> </li>
    </ul>
	<form action="#" id="coreData" class="form-horizontal" method="post">
	<input type="hidden" id="commonMetaDataObjectId"  name="commonMetaData.objectId" value=""/>
	<input type="hidden" id="extendMetaDataObjectId" name="extendMetaData.objectId" value=""/>
	<input type="hidden" id="identifier" name="commonMetaData.commonMetaDatas['identifier']" value=""/>
    <input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
    <input type="hidden" id="queryNodeId" name="queryNodeId" value="-1"/>
    <input type="hidden" id="nodeAsset" name="nodeAsset" value=""/>
    <input type="hidden" id="jsonTree" name="jsonTree" value="${jsonTree}"/>
	<input type="hidden" id="ogId" name="ogId"/>
	<input type="hidden" id="bookPath" name="bookPath" />
	<input type="hidden" id="operateFrom" name="operateFrom" value="${operateFrom}"/>
	<input type="hidden" id="goBackTask" name="goBackTask" value="${goBackTask}"/>
    <input type="hidden" id="wfTaskId" name="wfTaskId" value="${wfTaskId}"/>
    <input type="hidden" id="saveAndApply" name="saveAndApply" value=""/>
	<input type="hidden" id="opt" name="opt" value=""/>
	<input type="hidden" id="nodeOpt" name="nodeOpt" value=""/>
	<input type="hidden" id="editOldName" name="editOldName" value=""/>
	<input type="hidden" id="fileFlag" name="fileFlag"/>
	<input type="hidden" id="publishType" name="commonMetaData.commonMetaDatas['publishType']" value=""/>
	<input type="hidden" id="resVersion" name="commonMetaData.commonMetaDatas['res_version']"/>
	<div class="by-tab">
    <%@ include file="/publishRes/publishMetaData.jsp" %>
       	 <c:if test="${objectId=='-1'}">
       	  <div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">资源文件</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="control-label col-md-4"><i class="must">*</i>文件：</label>
                                <div class="col-md-8">
                                	<input type="hidden" id="uploadFile" name="uploadFile" value=""/>
                                    <input type="file" name="fileData" id="fileData" class="validate[required]" />
                                </div>
                            </div>
                        </div>
                    </div>
                   
                </div>
            </div>
        </div>
        </c:if>
         <c:if test="${objectId!='-1'}">
         <div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">资源文件 
	            <button type="button" class="fa fa-plus" onclick="addRootOpt()" title="添加目录"></button>
	            </div> 
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <div class="col-md-12 zTreeBackground">
                                	 <ul id="directoryTree" class="ztree form-wrap" style="overflow:auto;"></ul>
                                </div>
                                
                            </div>
                        </div>
                       
                    </div>
                   
                </div>
            </div>
        </div>
        </c:if>
        <div class="form-actions">
            <c:if test="${objectId!='-1'}">
   			<button type="button"  class="btn btn-lg blue" onclick="changeOpt('update')" >提交</button>
   			 	<sec:authorize url='/publishRes/doSaveAndApply.action'>
        		<button type="button" class="btn btn-lg red" onclick="doApply();">提交并上报</button>
        		</sec:authorize>
        	</c:if>
   			<c:if test="${objectId=='-1'}">
   					   <button type="button"  class="btn btn-lg blue" onclick="changeOpt('save')" >提交</button>
   			</c:if>
   			
            <button type="button" class="btn btn-lg red" onclick="goBack();">取消</button>
        </div>
    </div>
    <script type="text/javascript">
	function goBack(){
		if('${operateFrom}'=='TASK_LIST_PAGE'){
			window.location.href = "${path}/TaskAction/toList.action"; 
		}else if('${targetPub}'!=''){
			window.location.href ='${path}/pubres/wf/gotoCheck.action?objectId='+ objectId + '&operateFrom=MANAGE_PAGE'+'&goBackTask='+$("#goBackTask").val();
		}else{
			parent.queryForm();
		}
	}
	</script>
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
    </form>
</div>
<script type="text/javascript">
	function doApply(){
		$("#saveAndApply").val("1");
		changeOpt('update');
	}
</script>
<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
<script type="text/javascript" src="${path}/bres/knowledgeTree.js"></script>
</body>
</html>