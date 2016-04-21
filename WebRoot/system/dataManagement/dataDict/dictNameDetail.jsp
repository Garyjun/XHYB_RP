<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>


<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	$(function(){
   		//定义一datagrid
   		var _divId = 'data_div';
   		var _url = '${path}/system/dataManagement/dataDict/listValue.action?dictNameId='+$("#dictNameId").val();
   		var _pk = 'id';
   		var _conditions = ['pid'];
   		var _sortName = 'id';
   		var _sortOrder = 'desc';
		var _colums = [{field:'name',title:'参数名称',width:fillsize(0.2),sortable:true},
		               {field:'shortname',title:'参数简称',width:fillsize(0.2),sortable:true},
						{field:'indexTag',title:'参数值',width:fillsize(0.2),sortable:true},
						{field:'description',title:'描述',width:fillsize(0.2)},
						{field:'opt1',title:'操作',width:fillsize(0.22),align:'center',formatter:$operate}];
		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false);
	});
	
	/***操作列***/
	$operate = function(value,rec){
		var opt = "";
		opt += "<a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 详细</a>";
		return opt;
				
	};
	
	function detail(id){
		 $.openWindow("${path}//system/dataManagement/dataDict/dictValueDetail.action?id="+id,'字典项详细页',600,320);
		//location.href = "dictValueDetail.action?id="+id;
	}
	
	function returnList(){
		location.href = "dictNameList.jsp";
	}

	</script>
</head>
<body>
	<div id="fakeFrameDict" class="container-fluid by-frame-wrap" style="height: 100%;">
 	<div class="panel panel-default">
		<!-- <div class="panel-heading" id="div_head_t">
			<ol class="breadcrumb">
				<li><a href="javascript:;">数据字典</a></li>
				<li class="active">字典信息</li>
			</ol>
		</div> -->
		<div class="form-wrap">
		<form:form action="updAction.action" id="form" modelAttribute="frmDictName" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">字典名称：</label>
				<div class="col-xs-3">
					<p class="form-control-static">${frmDictName.name}</p>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">索引名称：</label>
				<div class="col-xs-3">
					<p class="form-control-static">${frmDictName.indexTag}</p>
				</div>
			</div>
			<div class="form-group">
				<label  class="col-xs-5 control-label">状态：</label>
				<div class="col-xs-3">
					<c:if test="${frmDictName.status=='1'}">
						<p class="form-control-static">可用</p>
					</c:if>
					<c:if test="${frmDictName.status=='0'}">
						<p class="form-control-static">禁用</p>
					</c:if>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">描述：</label>
				<div class="col-xs-3">
					<p class="form-control-static">${frmDictName.description}</p>
				</div>
			</div>	
			<div class="form-group">
				<div class="col-xs-offset-5 col-xs-7">
	           		<form:hidden path="id" id="dictNameId"/>
	           		<input type="hidden" name="token" value="${token}" />
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            </div>
			</div>
			<div id="data_div" class="data_div height_remain col-xs-12" style="width:950px;"></div>
		</form:form>
		</div>	
	</div> 
	</div>
</body>
</html>