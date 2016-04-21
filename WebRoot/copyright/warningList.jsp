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
	   		var _conditions = ['title','publishType','authStartDate','authEndDate','type'];
	   		var _sortName = 'authEndDate';
	   		var _sortOrder = 'asc';
	   		var _check=false;
			var _colums = [
							{field:'title',title:'资源标题',width:fillsize(0.2),align:'center',sortable:true},
			                {field:'temPublishType',title:'资源类型',width:fillsize(0.2),align:'center',sortable:true},
// 			               	{field:'authStartDate',title:'版权人',width:fillsize(0.2),align:'center',sortable:true},
// 			               	{field:'autherName',title:'授权人',width:fillsize(0.2),align:'center',sortable:true},
			               	{field:'authStartDate',title:'授权开始时间',width:fillsize(0.2),align:'center',sortable:true},
							{field:'authEndDate',title:'授权结束时间',width:fillsize(0.2),align:'center',sortable:true},
							{field:'type',title:'状态',width:fillsize(0.2),align:'center',formatter:$type},
							{field:'opt1',title:'操作',width:fillsize(0.20),align:'center',formatter:$operate}];
			accountHeight();
	   		//$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
		});
		/***状态列***/
		$type = function(value,rec){
			var opt = "";
			if(rec.type=='0'){
				opt = "已过期";
			}else{
				opt = "未过期";
			}
			return opt;
					
		};
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
			return opt;
					
		};
		
		//详细
		function detail(id){
// 			id = "urn:publish-a81651b3-1c97-42fc-b7c0-8bd6d7a4dea5"
				window.location.href = "${path}/publishRes/toDetail.action?objectId="+id+"&copyrightWaring=1";
// 				window.location.href = "${path}/copyright/detail.action?publishType=${param.publishType}&type=${param.type}&identifier="+id+"&main="+$('#main').val();
//			$.openWindow("${path}/copyright/detail.action?id="+id,'详细',600,350);
//			location.href = "detail.action?id="+id;
		}
		function query(){
			$grid.query();
		}
		//重置查询
		function queryReset(){
			$('#publishType').val("");
			$('#title').val("");
			$('#type').val("");
			query();
		}
	</script>
</head>
<body>
<c:if test="${param.copyrightWaring!='1'}">
	<input type="hidden" id="type" name="type" value="${param.type}"/>
	<input type="hidden" id="publishType" name="publishType" value="${param.publishType}"/>
	<input type="hidden" id="main" name="main" value="${param.main}"/>
	<input type="hidden" id="licenStartTime" name="licenStartTime" value="${param.licenStartTime }" />
	<input type="hidden" id="licenEndTime" name="licenEndTime" value="${param.licenEndTime }" />
	<input type="hidden" id="contractCode" name="contractCode" value="${param.contractCode }" />
	<input type="hidden" id="ownership" name="ownership" value="${param.ownership }" />
	<input type="hidden" id="autherName" name="autherName" value="${param.autherName }" />
<!-- 	<input type="hidden" id="copyrightWaring" name="copyrightWaring" value="1" /> -->
</c:if>
<%-- 	<input type="hidden" id="licenEndTimeBegin" name="licenEndTimeBegin" value="${param.licenEndTimeBegin }" /> --%>
<%-- 	<input type="hidden" id="licenEndTimeEnd" name="licenEndTimeEnd" value="${param.licenEndTimeEnd }" /> --%>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li class="active">
					版权预警
					</li>
					<li class="active">预警资源列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
			<c:if test="${param.copyrightWaring=='1'}">
				<div style="margin: 0px 10px 10px 0px;">
					<button  class="btn btn-primary" onclick="edit(-1)" style="visibility:hidden;">新增</button>
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form">
							<div class="form-group">
								<label class="control-label" for="processName">资源类型:</label>
								 <app:selectResType name="publishType" id="publishType" selectedVal=""  headName="全部"  headValue=""  readonly =""/>
<!-- 							      <input type="text" class="form-control" id="contractCode" name="contractCode"   placeholder="输入合同编号" qMatch="like"/> -->
							</div>
							<div class="form-group">
        					<label class="control-label" for="processName">状态:</label>
							      <select name="type" id="type" class="form-control" qType="like" style="width: 99px;">
							      	<option value="">全部</option>
									<option value="0">已过期</option>
									<option value="1">未过期</option>
								</select>
							</div>
							<div class="form-group">
<!-- 							<label class="control-label" for="processName">标题:</label> -->
							      <input type="text" class="form-control" id="title" name="title"   placeholder="输入资源标题" qMatch="like"/>
							</div>
							
<!-- 						   <div class="form-group"> -->
<!-- 							      <input type="text" class="form-control" id="ownership" name="ownership"   placeholder="输入版权人" qMatch="like"/> -->
<!-- 							</div> -->
<!-- 							<div class="form-group"> -->
<!-- 							      <input type="text" class="form-control" id="autherName" name="autherName"   placeholder="输入授权人" qMatch="like"/> -->
<!-- 							</div> -->
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="reset" value="清空" class="btn btn-primary" onclick="queryReset()"/>
						</form>
					</div>
				</div>
			</c:if>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
</body>
</html>