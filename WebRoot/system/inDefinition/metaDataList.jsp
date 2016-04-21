<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
    <title>列表</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
		$(function(){
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = '${path}/system/inDefinition/queryMetaData.action?pid='+$("#pid").val();
	   		var _pk = 'id';
	   		var _conditions = ['pid','version'];
	   		var _sortName = 'id';
	   		var _sortOrder = 'desc';
	   		var _check = false;
			var _colums = [{field:'definition.name',title:'应用集成方名称',width:100, align:'center' ,sortable:true},
							{field:'version',title:'版本',width:100, align:'center' ,sortable:true},
							{field:'opt1',title:'操作',width:fillsize(0.19),align:'center',formatter:$operate}];
	   		
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
		});
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<sec:authorize url='/system/inDefinition/metaDataTermdetail.action'><a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
			opt += "<sec:authorize url='/system/inDefinition/upmetaDataTerm.action'><a class=\"btn hover-red\" href=\"javascript:upd("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 修改</a>	</sec:authorize>";
			opt += "<sec:authorize url='/system/inDefinition/delmetaDataTerm.action'><a class=\"btn hover-blue\" href=\"javascript:deleteRecord("+rec.id+")\" ><i class=\"fa fa-trash-o\"></i> 删除</a></sec:authorize>";
			return opt;
					
		};
		function query(){
			$grid.query();
		}
		function add(){
			var ppid = $("#pid").val();
			$.openWindow("${path}/system/inDefinition/metaDataTerm.action?pid="+ppid,'添加元数据项',1000,600);
		}
		function detail(id){
			$.openWindow("${path}/system/inDefinition/metaDataTermdetail.action?id="+id,'详细元数据项',1000,600);
		}
		function deleteRecord(id){
			var ppid = $("#pid").val();
			$.confirm('确定要删除所选数据吗？', function(){
				location.href='${path}/system/inDefinition/delmetaDataTerm.action?id='+id+'&pid='+ppid;
			});
			
		}
		function upd(id){
			$.openWindow("${path}/system/inDefinition/upmetaDataTerm.action?id="+id,'修改元数据项',1000,600);
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="###">系统管理</a></li>
					<li class="active">应用集成方定义</li>
					<li class="active">元数据列表</li>
				</ol>
			</div>
			
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				<sec:authorize url='/system/inDefinition/metaDataTerm.action'>
						<input class="btn btn-primary" type="button" value="添加" onclick="add();"/>
				</sec:authorize>
					<div style="float: right;">
					<form action="#" id="queryForm">
						<div class="form-inline" style="float:right;">
						<div class="form-group">
							<input type="text" class="form-control" id="version" name="version" qType="long" placeholder="输入版本" />
						</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							 <a class="btn btn-default blue" href="###" onclick="$('#queryForm')[0].reset();query();">清空</a>
					    </div>
					</form>
					</div>
				</div>
				<input type="hidden" id="pid" name="pid" value="${pid}" qType="long"/>
				<div id="data_div" class="data_div height_remain" style="width:100%;"></div>
			</div>
			
		</div>
	</div>
</body>
</html>