<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>敏感词统计详细</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript"
	src="${path}/appframe/main/js/libs/jquery.messager.js"></script>
<script type="text/javascript">
	$(function() {
		var resource = $('#resource').val();
		//定义一datagrid
		var _divId = 'data_div';
		var _url = '${path}/resource/queryDetail.action?resource=' + resource;
		var _pk = 'id';
		var _conditions = [ 'resource' ];
		var _sortName = 'id';
		var _sortOrder = 'desc';
		var _check = false;
		var _colums = [ {title : '文件名称',   field : 'filePath',width : 50,align : 'center',sortable : true}, 
		                {title : '包含敏感词',  field : 'word',		width : 30,align : 'center',sortable : true},
		                {title:	 '操作',      field:'opt1',		width:fillsize(0.12),align:'center',formatter:$operate}

					];

		accountHeight();
		$grid = $.datagridSimple(_divId, _url, _pk, _colums, _conditions,
				_sortName, _sortOrder, _check);
	});

	
	$operate = function(value,rec){
		var optArray = new Array();
		var viewUrl = ("<a class=\"btn hover-red\" href=\"javascript:view('"+rec.fileRealPath+"','"+rec.fileId+"')\" ><i class=\"fa fa-eye\"></i> 预览</a>");
		optArray.push(viewUrl);
	    return createOpt(optArray);
		};
	
	function query() {
		$grid.query();
	}

	function formReset() {
		$('#queryForm')[0].reset();
		$grid.query();
	}
	
	function view(filePath,fileId){
		readFileOnline(filePath, fileId, null,null, null, null, '1');
	}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 345px;">
		<div id="data_div" class="data_div height_remain" style="width: 620px;"></div>
	</div>
			<input id="resource" value="${resourId}" type="hidden" />
</body>
</html>
