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
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
	var detailFlag = '${detailFlag}';
	var data='';
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
// 	     function save(saveType){
// 	    	 $('#coreData').ajaxSubmit({
// 					url: '${path}/publishRes/saveRes.action',//表单的action
// 	 				method: 'post',//方式
// 	 				success:(function(response){
// 	 					alert("ddd");
// 	 					saveResWaiting.close();
// 	 					if('${targetPub}'!=''){
// 	 						window.location.href ='${path}/pubres/wf/gotoCheck.action?objectId='+ objectId + '&operateFrom=MANAGE_PAGE';
// 	 					}else if(response == ''){
// 							parent.queryForm();
// 	 					}else{
// 							$.showTips(response,5,'');
// 	 					}
	 					
// 	           		})
// 	 			});
// 	     }
	     $(function() {
// 	    	 initRalations();
// 	    	 init_grid();
			//元数据提交
// 			var datas = '${ztreeJson}';
// 			if(datas !=''){
// 				zNodes = eval('('+datas+')');
// 				$.fn.zTree.init($("#directoryTree"), directorySetting, zNodes);
// 			}
	     });
			function removeHoverDom(treeId, treeNode) {
				$("#addBtn_"+treeNode.tId).unbind().remove();
				$("#addResBtn_"+treeNode.tId).unbind().remove();
				$("#infoBtn_"+treeNode.tId).unbind().remove();
				$("#removeBtn_"+treeNode.tId).unbind().remove();
				$("#removeFileBtn_"+treeNode.tId).unbind().remove();
				$("#editBtn_"+treeNode.tId).unbind().remove();
				
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
					if(detailFlag!="detail"){
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
				if (detailFlag!="detail"){
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
				
				if (detailFlag!="detail"){
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
				if(oldPath!="1"&&parentId==undefined && $('#nodeOpt').val()!="addRoot"){
					fileObject.pid = nodeId;
				}else if($('#nodeOpt').val()=="addRoot"){
					fileObject.id = nodeId;
					fileObject.pid = "-1";
					fileObject.path=node.path;
					title = "addRoot";
				}
				else{
					//拿title做标记
					title = "addNode";
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
			function addRootNode(){
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var id= new Date().getTime();
				var xpath="-1"+','+id;
				var path=$("#bookPath").val()+'/'+$('#labelName').val();
				treeObj.addNodes(null, {nodeId:id,pid:'-1', name:$('#labelName').val(),path:path,files:[]});
				updateNode(id);
			}
			function setNodeFile(nodeId,fileName,isDir){
				//alert('execute setNodeFile'+'nodeId : '+nodeId+"fileName :"+fileName+" filePath: "+filePath);
				var treeObj = $.fn.zTree.getZTreeObj("directoryTree");
				var treeNode = treeObj.getNodeByParam("nodeId", nodeId, null);
				var id=treeNode.pid + new Date().getTime();
				//var xpath=treeNode.xpath+','+id;
				//var file={name:fileName,path:filePath};
				//var files=[file];
				treeObj.addNodes(treeNode, {nodeId:id,pid:nodeId,name:fileName,isDir:isDir});
				//updateNode(id);
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
			function addRootOpt(){
				$("#labelName").val('');
				$('#nodeOpt').val('addRoot');
				$("#versionText").modal('show');
			}
			function goBackTaskWaring(){
// 				alert($('#copyrightWaring').val());
				if($('#copyrightWaring').val()=='1'){
					parent.gotoCopyrightWarning();
				}else{
					parent.queryForm();
				}
			}
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
					<li class="active">资源详细</li>
				</ol>
	</div>
 </div>	
<form action="#" id="coreData" class="form-horizontal">
 <app:AssetMetadataDetailTag   object="${assetAs}" publishType="${publishType}"/>
 <input type="hidden" id="nodeOpt" name="nodeOpt" value=""/>
 <input type="hidden" id="status" name="status" value="${status}"/>
 <input type="hidden" id="uploadFile" name="uploadFile" value=""/>
 <input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
 <input type="hidden" id="targetField" name="targetField" value="${targetField}"/>
 <input type="hidden" id="targetNames" name="targetNames" value="${targetNames}"/>
 <input type="hidden" id="cbclassFieldValue" name="cbclassFieldValue" value="${cbclassFieldValue}"/>
 <input type="hidden" id="cbclassField" name="cbclassField" value="${cbclassField}"/>
 <input type="hidden" id="copyrightWaring" name="copyrightWaring" value="${copyrightWaring}"/>
 <input type="hidden" id="bookPath" name="bookPath" />
 <input type="hidden" id="editOldName" name="editOldName" value=""/>
 <input type="hidden" id="fileFlag" name="fileFlag"/>
   <c:if test="${param.buttonShow==1}">
    	<div class="form-actions" align="center">
   			<button type="button" class="btn btn-lg blue" onclick="goBackTask();">返回</button>
   		</div>
   </c:if>
 </form>
 <script type="text/javascript">
 	function goBackTask(){
		if('${operateFrom}'=='TASK_LIST_PAGE'){
			window.location.href = "${path}/TaskAction/toList.action"; 
		}else{
			if($('#targetNames').val()!=""){
				parent.selectTargetRes();
			}else{
				history.go(-1);
			}
		}
	}
</script>
</div>
</body>
</html>