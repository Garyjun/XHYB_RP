<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!-- 
	--DictSelect模板页面
	--2015年10月10日15:49:45
	--huagnjuun
	--注意事项：
		1、引入文件
		<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css" />
		<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
		<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
		<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
		
		2、引入控件
		$("select[type=multiSelEle]").multipleSelect({
				multiple: true,
				multipleWidth: 140,//该选项指没一个option宽度
				width: "100%"
		});
		$("select[type=multiSelEle]").multipleSelect("refresh");
		
		这段代码在文档加载完成时执行,即$(function(){ }或较早的版本的$(document).ready(function(){ ......  })中
		
		3、选择框的宽度为width: 66.66666667%;
		
 -->
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css" />
	<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
	<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
	<title>DictSelect模板页面</title>
	<script type="text/javascript">
		$(function(){
			$("select[type=multiSelEle]").multipleSelect({
				multiple: true,
				multipleWidth: 140,
				width: "100%",
				minimumCountSelected: 2
			});
			$("select[type=multiSelEle]").multipleSelect("refresh");
		});
		
	</script>
</head>
<body>
	<div class="form-wrap">

		<div class="form-group">
	    	<label class="col-md-4 control-label text-right">国籍：</label>
	    	<app:dictSelect id="Country" name="Country" indexTag="relation_country" />
		</div>
		
		<div class="form-group">
	    	<label class="col-md-4 control-label text-right">国籍2：</label>
		    <app:dictSelect id="Country2" name="Country2" indexTag="relation_country" selectedVal="1010,1011,1015"/>
		</div>
		
		<div class="form-group">
	    	<label class="col-md-4 control-label text-right">朝代：</label>
		    <app:dictSelect id="chaodai" name="chaodai" indexTag="relation_chaodai" />
		</div>
		
		<div class="form-group">
	    	<label class="col-md-4 control-label text-right">出版社：</label>
	    	<app:dictSelect id="house" name="house" indexTag="relation_house" selectedVal="1009,1401,1402" />
		</div>
   </div>
</body>
</html>