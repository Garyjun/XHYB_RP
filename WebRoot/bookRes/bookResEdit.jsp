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
	</style>
	<script type="text/javascript">
		var cFileType = '';
		var objectId = '-1';
		var md5 = '';//刚上传文件的md5值
		var data='';
		var fileCompleteNum = 0;//完成总数
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
			setModuleAndType(currentModule,currentType);
			objectId = $('#objectId').val();
			var zNodes =[{ nodeId:1, pid:-1, name:"目录结构", open:true,xpath:'1'}];
			if(objectId!='-1'){
				var data = '${resourceDetail}';
				data = eval('('+data+')');
				initData(data);
				var organizations=data.organizations;
				if(organizations!=null&&organizations.length>0){
					organizationItems=organizations[0].organizationItems;
					$("#ogId").val(organizations[0].objectId);
					var firstPath=organizationItems[0].path;
					var bookPath=firstPath.substring(0,firstPath.lastIndexOf('/'));
					$("#bookPath").val(bookPath);
					if(organizationItems.length>0){
						zNodes=organizationItems;
					}
				}
				$.fn.zTree.init($("#directoryTree"), directorySetting, zNodes);
			}else{
				$("#ratingShow").rating();
			}
// 			else{
// 				top.index_frame.setModuleAndType();
				
// 			}
			
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
			
		  //初始化上传组件
		    $('#fileData').uploadify({
		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
		        'uploader' : '${path}/uploadFileToTemp.action;jsessionid=<%=session.getId()%>',
		        'debug' : false,
		        //是否自动上传
		        'auto':false,
		        'multi':false,
		        'queueSizeLimit':1,
		        'fileTypeExts':'*.zip;*.rar',
		        'fileTypeDesc':'支持的文件格式',
		        'buttonText':'选择文件',
		        'removeCompleted':false,
		        'onUploadStart': function (file) {
		        	uploadFileWaiting = $.waitPanel('正在上传文件...',false);
            	},
		        'onSelect':function(file){
		        	cFileType = file.type;
		        	
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
		        		$('#uploadFile').val(data.uploadFile);
		        		info = "上传成功";
		        		fileCompleteNum=fileCompleteNum+1;
		        		checkRepeat();
		        	}
		        	$('#' + file.id).find('.data').html(' - ' + info);
		        	uploadFileWaiting.close();
            	}
		    });
			
		})
		
		
		//保存表单数据
		function saveForm(){
			if(fileCompleteNum==0){
				$.alert('请选择上传的文件！');
				return ;
			}
			if(objectId == '' || objectId == '-1'){
    			checkRepeat();
    		}else{
    			saveRes();
    		}
		}
		//查重
		function checkRepeat(){
			$.post('${path}/bookRes/checkRepeat.action',{module:$('#module').val(),source:$('#source').val(),type:$('#type').val(),title:$('#title').val(),creator:$('#creator').val(),isbn:$('#ISBN').val()},function(data){
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
				if(typeof(obj.objectId) != 'undefined' && obj.objectId != ''){
					msg+= "<a class=\"btn hover-red\" href=\"javascript:detailOres('"+obj.objectId+"');\"><i class=\"fa fa-sign-out\"></i>详细</a>";
				}
				msg+= '</td></tr>';
				resVersions.push(obj.commonMetaData.commonMetaDatas.res_version);
			}
			
			msg += '</table>';
			msg +='<script type=\"text/javascript\">function detailOres(objectId){$.openWindow(\"${path}\/bookRes\/detail.action?libType=${param.libType}&flag=flag&objectId=\"+objectId,\"资源详细\",800,450);}<\/script>';
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
				        	$('#objectId').val('-1');
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
				        	saveRes(3);
				        }},
				        {name:'忽略',callback:function(){}}
				]
			});
		}
		function saveRes(repeatType){
			if(typeof(repeatType) == 'undefined'){
				repeatType = 1;
			}
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
			objectId = $('#objectId').val();
			if(objectId !=-1 && repeatType !=2){
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
			}
			saveResWaiting = $.waitPanel('正在保存资源...',false);
			if($("#saveAndApply").val()==1){
				url = "${path}/res/wf/doSaveAndSubmit.action?repeatType="+repeatType;
			}else{
				url = "${path}/bookRes/saveRes.action?repeatType="+repeatType;
			}
			$('#coreData').ajaxSubmit({
				url: url,//表单的action
 				method: 'post',//方式
 				success:(function(response){
 					saveResWaiting.close();
 					if(response == ''){
						parent.queryForm();
						
 					}else{
						$.showTips(response,5,'');
 					}
 					
           		})
 			});
		}
		function updateForm(){
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
			$('#unit').val('');
			$('#region').val($('#provinces').val()+','+$('#city').val()+','+$('#area').val());
			saveResWaiting = $.waitPanel('正在保存资源...',false);
			$('#coreData').ajaxSubmit({
				url: '${path}/bookRes/updateRes.action',//表单的action
 				method: 'post',//方式
 				success:(function(response){
 					saveResWaiting.close();
 					if('${targetBook}'!=''){
 						window.location.href = '${path}/res/wf/gotoCheck.action?libType='+$("#libType").val()+'&objectId='+ objectId + '&operateFrom=MANAGE_PAGE'+'&goBackTask='+$('#goBackTask').val();
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
			$('#subject').val(commonMetaDatas['subject']);
			changeSubject();
			$('#grade').val(commonMetaDatas['grade']);
			changeGrade();
			$('#unit').val(commonMetaDatas['unit']);
		//	$('#fascicule').val(commonMetaDatas['fascicule']);
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
					alert("节点名称不能为空.");
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
			function addHoverDom(treeId, treeNode) {
				var zTree = $.fn.zTree.getZTreeObj("directoryTree");
				var sObj = $("#" + treeNode.tId + "_span");
				if(treeNode.files!=''){
					if (treeNode.nodeId!=1&&treeNode.editNameFlag==false && $("#infoBtn_"+treeNode.tId).length==0){
							var infoStr = "<span class='button preLook' id='infoBtn_" + treeNode.tId
						+ "' title='预览' onfocus='this.blur();'></span>";
					    sObj.after(infoStr);
						var btnRes = $("#infoBtn_"+treeNode.tId);
						if (btnRes) btnRes.bind("click", function(){
							var filePath=treeNode.files[0].path;
							readFileOnline(filePath,objectId); //相对路径	
						});
					}
					
					if (treeNode.nodeId!=1&&treeNode.editNameFlag==false && $("#remFileBtn_"+treeNode.tId).length==0){
						var remFileStr = "<span class='button remove' id='remFileBtn_" + treeNode.tId
					+ "' title='删除' onfocus='this.blur();'></span>";
				    sObj.after(remFileStr);
					var remFileRes = $("#remFileBtn_"+treeNode.tId);
					if (remFileRes) remFileRes.bind("click", function(){
						nodes = zTree.getSelectedNodes(),
						treeNode = nodes[0];
						var nodeId=treeNode.nodeId;
						var parentNode = treeNode.getParentNode();
						var path = parentNode.path+"/"+treeNode.name;
						var objectId = $('#objectId').val();
						$.post("${path}/bookRes/deleteNode.action",{ caId: objectId,nodeId: nodeId,path:path },
								function(data){
									if(data=='-1'){
										alert('删除节点异常!');
									}
					    });
						zTree.removeNode(treeNode);
					});
				}
				}
				if (treeNode.files==''&&treeNode.nodeId!=1&&treeNode.editNameFlag==false && $("#addResBtn_"+treeNode.tId).length==0){
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
						var url="${path}/bookRes/toSelFile.action?nodeId="+nodeId+'&parentName='+parentName+'&parentPath='+parentPath;
						$.openWindow(url,'添加文件',1000,300);
						
					});
				}
				
				if (treeNode.files==''&&treeNode.editNameFlag==false && $("#addBtn_"+treeNode.tId).length==0){
					var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
					+ "' title='增加' onfocus='this.blur();'></span>";
					//alert(addStr);
					
					if(treeNode.nodeId!=1){
						addStr += "<span class='button remove' id='removeBtn_" + treeNode.tId+ "' title='删除' onfocus='this.blur();'></span>";
	                    addStr += "<span class='button edit' title='编辑' id='editBtn_" + treeNode.tId + "'></span>";
					 
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
							var path = treeNode.path;
							var objectId = $('#objectId').val();
							$.post("${path}/bookRes/deleteNode.action",{ caId: objectId,nodeId: nodeId,path:path },
									function(data){
										if(data=='-1'){
											alert('删除节点异常!');
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
				$("#editBtn_"+treeNode.tId).unbind().remove();
				$("#readBtn_"+treeNode.tId).unbind().remove();
			};
			
			function setNodeFile(nodeId,fileName,filePath){
				//alert('execute setNodeFile'+'nodeId : '+nodeId+"fileName :"+fileName+" filePath: "+filePath);
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var treeNode = treeObj.getNodeByParam("nodeId", nodeId, null);
				var id=treeNode.pid + new Date().getTime();
				var xpath=treeNode.xpath+','+id;
				var file={name:fileName,path:filePath};
				var files=[file];
				treeObj.addNodes(treeNode, {nodeId:id,xpath:xpath,pid:treeNode.nodeId,name:fileName,files:files});
				updateNode(id);
			}
			
			function updateNode(nodeId,oldPath){
				objectId = $('#objectId').val();
				ogId=$('#ogId').val();
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var node = treeObj.getNodeByParam("nodeId", nodeId, null);
				node.caId=objectId;
				node.ogId=ogId;
				var nodeJson=JSON.stringify(node);
				treeObj.updateNode(node);
				if(objectId=='-1'){
					closeWindow();
					return;
				}
				var title=$("#title").val();
				$.post("${path}/bookRes/updateNode.action",{ nodeJson: nodeJson,title:title,oldPath:oldPath},
				function(data){
					if(data=='-1'){
						alert('更新节点异常!');
					}else{
						node.objectId=data;
						treeObj.updateNode(node);
					}
					closeWindow();
			    });
				
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
				treeObj.addNodes(treeNode, {nodeId:id,xpath:xpath, pid:treeNode.nodeId, name:$('#labelName').val(),path:path,files:[]});
				updateNode(id);
				
			}
			function optNode(){
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
			function closeWindow(){
				$("#versionText").modal('hide');
			}
			
	</script>
	
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap">
    <ul class="page-breadcrumb breadcrumb">
        <li>
            <a href="###">资源管理</a>
            <i class="fa fa-angle-right"></i>
        </li>
        <li>
            <a href="###">图书资源</a>
            <i class="fa fa-angle-right"></i>
        </li>
        <li><a href="###">编辑资源</a> </li>
    </ul>
	<form action="#" id="coreData" class="form-horizontal" method="post">
	<input type="hidden" id="commonMetaDataObjectId"  name="commonMetaData.objectId" value=""/>
	<input type="hidden" id="extendMetaDataObjectId" name="extendMetaData.objectId" value=""/>
	<input type="hidden" id="identifier" name="commonMetaData.commonMetaDatas['identifier']" value=""/>
	<input type="hidden" id="libType" name="commonMetaData.commonMetaDatas['libType']" value="${param.libType}"/>
	<input type="hidden" id="xpaths" name="xpaths[0]" value=""/>
	<input type="hidden" id="type" name="commonMetaData.commonMetaDatas['type']"/>
    <input type="hidden" id="module" name="commonMetaData.commonMetaDatas['module']"/>
    <input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
    <input type="hidden" id="queryNodeId" name="queryNodeId" value="-1"/>
    <input type="hidden" id="nodeAsset" name="nodeAsset" value=""/>
    <input type="hidden" id="jsonTree" name="jsonTree" value="${jsonTree}"/>
	<input type="hidden" id="ogId" name="ogId"/>
	<input type="hidden" id="bookPath" name="bookPath" />
	<input type="hidden" id="operateFrom" name="operateFrom" value="${operateFrom}"/>
	<input type="hidden" id="goBackTask" name="goBackTask" value="${goBackTask}"/>
    <input type="hidden" id="wfTaskId" name="wfTaskId" value="${wfTaskId}"/>
    <input type="hidden" name="libType" value="${libType}"/>
    <input type="hidden" id="saveAndApply" name="saveAndApply" value=""/>
	<input type="hidden" id="opt" name="opt" value=""/>
	<input type="hidden" id="nodeOpt" name="nodeOpt" value=""/>
	<input type="hidden" id="resVersion" name="commonMetaData.commonMetaDatas['res_version']"/>
	<div class="by-tab">
    <%@ include file="/bres/metaData.jsp" %>
       	 <c:if test="${objectId=='-1'}">
       	  <div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">图书文件</div>
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
	            <div class="caption">图书信息  
	            <button class="fa fa-plus" onclick="addRootOpt()" title="增加目录"></button>
	            </div> 
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
        </c:if>
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
  					    <button class="btn btn-primary"  onclick="optNode();">确定</button>
						<button class="btn btn-primary" onclick="closeWindow()">关闭</button>				
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
	}else if('${targetBook}'!='')
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