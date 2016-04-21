<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>列表</title>
<script type="text/javascript"
	src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/jQueryValidationEngine/js/languages/jquery.validationEngine-zh_CN.js"
	charset="utf-8"></script>
<style>
#flag {
	margin-bottom: 5px;
}
</style>
<script type="text/javascript"> 
//元数据提交
 $(function() {
    $('#form').validationEngine('attach', {
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
				save();
			}
		}
	});
 });
 	function save(){
		$('#form').ajaxSubmit({
			method: 'post',//方式
			success:(function(response){
				callback(response);
    		})
		});
	}
	function callback(data){
		//更新节点名称
		data = eval('(' + data + ')');
		var win=  top.index_frame.work_main;
		var treeObj = win.$.fn.zTree.getZTreeObj("directoryTree");
		var nodes = treeObj.getSelectedNodes();
		var treeNode=nodes[0];
		treeNode.name= decodeURIComponent(data.newName);
		treeObj.updateNode(treeNode);
		$.closeFromInner();
	}
</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap ">
	<form:form action="${path}/publishRes/saveFileMetadata.action" id="form" class="form-horizontal" name="form"  modelAttribute="frmWord" method="post">
	<input type="hidden"  id="fileObjectId"  name="fileObjectId" value="${fileObjectId}"/>
	<input type="hidden"  id="fileType"  name="fileType" value="${fileType}"/>
		<app:FiletMetadataCreateTag object="${fileMetadata}" fileType="${fileType}" flag="${flag}" publishType="${publishType}" fileName="${fileName}" fileSize="${fileSize}"/>
		<div style="margin:5% 0 0 0">
		    <div class="form-group">
				<div class="col-xs-offset-5">
			           	<input  type="submit" value="提交" class="btn btn-primary" /> 
			           	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
			    </div>
			</div>
		</div>
	</form:form>
</div>
</body>
</html>