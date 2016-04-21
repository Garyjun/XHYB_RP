<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.brainsoon.system.support.SystemConstants.ResourceStatus"%>
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
		var dt = new DataGrid();
		$(function(){
	   		dt.setPK('objectId');
	   		dt.setConditions(['source','publishType','status','operate']);
	   		dt.setSortName('id');
			dt.setSortOrder('desc');
			dt.setSelectBox('id');
			dt.setOnLoadSuccess('onLoadSuccess');
			dt.setColums([ 
                {field:'source',title:'渠道名称',width:fillsize(0.1),sortable:true},
				{field:'type',title:'资源类型',width:fillsize(0.12),sortable:true},
				{field:'status',title:'资源状态',width:fillsize(0.12),sortable:true},
				{field:'sum',title:'资源购买总价格',width:fillsize(0.12),sortable:true},
				{field:'count',title:'数量',width:fillsize(0.1),sortable:true,align:'center'}
			]);
			accountHeight();
			dt.initDt();
	   		$("#operate").val("");
	   		query();
		});
		var onLoadSuccess = function(data){
			data = eval('('+data+')');
			$('#tnum').html(data.statisticsNum);
		};
		$formate = function(field,rec,index){
			var cell = "";
			return cell;
		};
		
		function exportRes() {
			var codes = getChecked('data_div','objectId').join(',');
			if (codes == '') {
			    $.alert('请选择要导出的资源！');
			} else {
				location.href='${path}/statistics/pubCollectNum/exportRes.action?ids='+codes;
				/* down4Encrypt('${path}/statistics/pubCollectNum/exportRes.action?ids='+codes); */
			}
		}
		
		function query(){
			dt.query();
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">查询统计</a></li>
					<li class="active">出版资源采集情况统计</li>
					<li class="active">出版资源采集情况统计列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
					<input type="hidden" id="publishType" name="publishType" value="${param.publishType}" />
					<input type="hidden" id="status" name="status" value="${param.status}" />
					<input type="hidden" id="source" name="source" value="${param.source}" />
					<input type="hidden" id="operate" name="operate" value="${param.operate}" />
					<sec:authorize url="/statistics/pubCollectNum/exportRes.action">
					<input type="button" value="批量导出" class="btn btn-primary" onclick="exportRes()"/>
					</sec:authorize>
					<div style="width: 30%;float: right;text-align: right;">
						总数量：<span id="tnum">0</span>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
</body>
</html>