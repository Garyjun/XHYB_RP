<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>列表</title>
<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
<script type="text/javascript">
	function preview() {
		var filePath = $("#filePath").val();
		var uuid = $("#uuid").val();
		var fileType = getFileExt(filePath);
		var fileName = getFileNameNoExt(filePath);
		readFileOnline(filePath,uuid,fileType,fileName,fileName);
	}
</script>
</head>
<body>
	<div align="center" style="height:500px;margin:200px;">
		<input type="text" id="filePath" name="filePath" value="" style="width:500px;"/>filePath
		<input type="text" id="uuid" name="uuid" value="" style="width:500px;"/>uuid
		<br />
		<input type="button" id="preview" name="preview" value="预览" onclick="preview();"/>
	</div>
</body>
</html>