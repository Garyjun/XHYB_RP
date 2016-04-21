<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>基础资源管理</title>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
	var detail = '${detail}';
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
	     $(function() {
	    	 //生成资源创建信息
	    	 initRalations();
	    	 //生成 操作历时
	    	 init_grid();
			//元数据提交
			var datas = '${ztreeJson}';
			zNodes = eval('('+datas+')');
			$.fn.zTree.init($("#directoryTree"), directorySetting, zNodes);
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
			};
			
			function returnBack(){
				location.href="${path}/publishRes/openList.jsp?publishType="+"${publishType}"+"&path="+"${pathDetail}"+"&fieldName="+"${fieldName}"+"&startTime="+"${startTime}"+"&endTime="+"${endTime}"
			}
			function closess(){
				var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
				parent.layer.close(index); //再执行关闭  
			}
	</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap ">
<input type="hidden" name="objectId" id="objectId" value="${param.objectId}"></input>
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
 <app:ObjectMetadataDetailTag   object="${bookCa}" publishType="${publishType}"/>
        <div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">资源文件 
	            </div> 
	        </div>
            <div class="portlet-body">
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
        </div>
        <sec:authorize url='WF_pubresCheck'>
		    <%@ include file="/publishRes/PubWFOperate.jsp" %>
		    <div class="form-actions" align="center">
		    	<c:choose>
		    		<c:when test="${datatype=='1' }">
		    			<input class="btn btn-primary" type="button" value="关闭" onclick="closess();"/>
		    		</c:when>
		    		<c:otherwise>
		    			<c:if test="${returnBack==null}">
		          			<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
		     		 	</c:if>
		      			<c:if test="${returnBack=='1'}">
		         			<input class="btn btn-primary" type="button" value="返回" onclick=" returnBack();"/>
		      			</c:if>
		    		</c:otherwise>
		    	</c:choose>
		    
		      
		     </div>
		</sec:authorize>
	          <%@ include file="relationRes.jsp" %>
 </form>
</div>
</body>
</html>