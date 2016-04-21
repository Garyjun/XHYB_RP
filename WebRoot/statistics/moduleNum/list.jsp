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
			dt.setURL('list.action?queryType=${param.queryType}');
			dt.setConditions(['module','type','libType','educationalPhase','tmVersion','tmGrade','tmSubject','filingDate_st','filingDate_et']);
			dt.setSortName('id');
			dt.setSortOrder('desc');
			//dt.setSelectBox('id','Single');
			dt.setSelectBox('id');
			dt.setOnLoadSuccess('onLoadSuccess');
			dt.setColums([{field:'maturityNameDesc',title:'成熟度名称',width:fillsize(0.1)},
							{field:'moduleNameDesc',title:'模块名称',width:fillsize(0.1)},
							{field:'resTypeDesc',title:'资源类型',width:fillsize(0.12)},
							{field:'tmSubject',title:'学科',width:fillsize(0.1),sortable:true},
							{field:'tmGrade',title:'年级',width:fillsize(0.1),sortable:true},
							{field:'educationalPhase',title:'学段',width:fillsize(0.12),sortable:true},
							{field:'tmVersion',title:'版本',width:fillsize(0.12),sortable:true},
							{field:'filingDate2',title:'归档日期',width:fillsize(0.15),sortable:true},
							{field:'num',title:'数量',width:fillsize(0.1),sortable:true,align:'center'}]);
			accountHeight();
			dt.initDt();
		});
		var onLoadSuccess = function(data){
			data = eval('('+data+')');
			$('#tnum').html(data.statisticsNum);
		};
		
		/***查看***/
		function detail(objectId){
			window.location.href = "${path}/bres/detail.action?libType=${param.libType}&objectId="+objectId;
		}
		
		/***导出***/
		function exportRes() {
			var codes = getChecked('data_div','id').join(',');
			if (codes == '') {
			    $.alert('请选择要导出的资源！');
			} else {
				location.href='${path}/statistics/moduleNum/exportRes.action?ids='+codes;
				/* down4Encrypt('${path}/statistics/moduleNum/exportRes.action?ids='+codes); */
			}
		}
		function exportAllRes(){
			down4Encrypt('${path}/statistics/moduleNum/exportRes.action?ids='+codes);
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
					<li><a href="javascript:;">资源模块查询统计</a>
					</li>
				</ol>
			</div>
			<div class="panel-body height_remain" >
				<div style="margin: 0px 10px 10px 0px;min-height: 25px;">
					<input type="hidden" id="module" name="moduleName" value="${param.module }" />
					<input type="hidden" id="type" name="resType" value="${param.type }" />
					<input type="hidden" id="libType" name="maturityName" value="${param.libType }" qType="int" />
					<input type="hidden" id="tmGrade" name="tmGrade" value="${param.tmGrade }" />
					<input type="hidden" id="educationalPhase" name="educationalPhase" value="${param.educationalPhase }" />
					<input type="hidden" id="tmVersion" name="tmVersion" value="${param.tmVersion }" />
					<input type="hidden" id="tmSubject" name="tmSubject" value="${param.tmSubject }" />
					<input type="hidden" id="filingDate_st" name="filingDate_st" value="${param.filingDate_st }" />
					<input type="hidden" id="filingDate_et" name="filingDate_et" value="${param.filingDate_et }" />
					<sec:authorize url="/statistics/moduleNum/exportRes.action">
						<input type="button" value="批量导出" class="btn btn-primary" onclick="exportRes()"/>
					</sec:authorize>
					<%-- <input type="button" value="全部导出" class="btn btn-primary" onclick="exportAllRes()"/> --%>
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