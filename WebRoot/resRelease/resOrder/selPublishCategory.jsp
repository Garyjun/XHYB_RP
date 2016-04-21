<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
	<head>
		<title>发布方式</title>
	</head>
	<body class="form-wrap">
		<!-- 发布方式：
		<select>
			<option value='offline' selected='true'>线下发布</option>
			<option value='online'>线上发布</option> 
		</select> -->
		<form action="#" id="coreData" class="form-horizontal">
		<div class="form-group" style="margin-top:20px;">
			<label class="col-sm-3 control-label text-right">发布方式：</label>
			<div class="col-sm-9">
				<label class="radio-inline">
					<input type="radio" name="publishType" value="offline" checked="checked"/> 线下发布
				</label>
				<label class="radio-inline">
					<input type="radio" name="publishType" value="online"/> 线上发布
				</label>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-3 col-sm-9">
               	<input id="tijiao" id="tijiao" type="submit" value="确定" class="btn btn-primary"/> 
               	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
            </div>
        </div>
		</form>
	</body>
</html>