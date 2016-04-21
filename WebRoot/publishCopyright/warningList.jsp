<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
	<script type="text/javascript">
		$(function(){
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = 'warningList.action';
	   		var _pk = 'id';
	   		var _conditions = ['publishType','contractCode','crtPerson','authorizer','authStartDateBegin','authStartDateEnd','authEndDateBegin','authEndDateEnd'];
	   		var _sortName = 'authEndDate';
	   		var _sortOrder = 'asc';
	   		var _check=false;
			var _colums = [
							{field:'title',title:'资源标题',width:fillsize(0.2),align:'center',sortable:true},
			                {field:'contractCode',title:'合同编号',width:fillsize(0.2),sortable:true},
			               	{field:'crtPerson',title:'版权人',width:fillsize(0.2),sortable:true},
			               	{field:'authorizer',title:'授权人',width:fillsize(0.2),sortable:true},
			               	{field:'authStartDate',title:'授权开始时间',width:fillsize(0.2),sortable:true},
							{field:'authEndDate',title:'授权结束时间',width:fillsize(0.2),sortable:true},
							{field:'opt1',title:'操作',width:fillsize(0.47),align:'center',formatter:$operate}];
			accountHeight();
	   		//$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	   		$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
		});
		
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
			return opt;
					
		};
		//详细
		function detail(id){
			window.location.href = "${path}/publishRes/detail.action?objectId="+id;
//			location.href = "detail.action?id="+id;
		}
		function query(){
			$grid.query();
		}
		
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<input type="hidden" id="publishType" name="publishType" value="${param.publishType}" />
			<input type="hidden" id="authStartDateBegin" name="authStartDateBegin" value="${param.authStartDateBegin }" />
			<input type="hidden" id="authStartDateEnd" name="authStartDateEnd" value="${param.authStartDateEnd }" />
			<input type="hidden" id="authEndDateBegin" name="authEndDateBegin" value="${param.authEndDateBegin }" />
			<input type="hidden" id="authEndDateEnd" name="authEndDateEnd" value="${param.authEndDateEnd }" />
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li class="active">
					版权预警
					</li>
					<li class="active">预警资源列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
					<button  class="btn btn-primary" onclick="edit(-1)" style="visibility:hidden;">新增</button>
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form">
							<div class="form-group">
							      <input type="text" class="form-control" id="contractCode" name="contractCode"   placeholder="输入合同编号" />
							</div>
						   <div class="form-group">
							      <input type="text" class="form-control" id="crtPerson" name="crtPerson"   placeholder="输入版权人" />
							</div>
							<div class="form-group">
							      <input type="text" class="form-control" id="authorizer" name="authorizer"   placeholder="输入授权人" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="reset" value="清除" class="btn btn-primary"/>
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
</body>
</html>