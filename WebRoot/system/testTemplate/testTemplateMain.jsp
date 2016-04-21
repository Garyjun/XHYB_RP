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
	   		var _url = '${path}/system/testTemplate/list.action';
	   		var _pk = 'id';
	   		var _conditions = ['name'];
	   		var _sortName = 'id';
	   		var _sortOrder = 'desc';
			var _colums = [{field:'name',title:'模板名称',width:100, align:'center' ,sortable:true},
							{field:'description',title:'描述',width:100, align:'center' },
							{field:'opt1',title:'操作',width:fillsize(0.19),align:'center',formatter:$operate}];
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<sec:authorize url='/system/testTemplate/testTemplateDetail.action'><a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
			opt += "<sec:authorize url='/system/testTemplate/upd.action'><a class=\"btn hover-red\" href=\"javascript:upd("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 修改</a></sec:authorize>";
			opt += "<sec:authorize url='/system/testTemplate/delAction.action'><a class=\"btn hover-blue\" href=\"javascript:deleteRecord("+rec.id+")\" ><i class=\"fa fa-trash-o\"></i> 删除</a></sec:authorize>";
			return opt;
					
		};
		
		/***添加***/
		function add(){
			$.openWindow("${path}/system/testTemplate/add.action",'添加试题模板',1000,600);
		}
		
		/***修改***/
		function upd(id){
			$.openWindow("${path}/system/testTemplate/upd.action?id="+id,'修改试题模板',1000,600);
		}
		
		function detail(id){
			$.openWindow("${path}/system/testTemplate/testTemplateDetail.action?id="+id,'试题模板详细页',1000,600);
		}
		
		/***删除***/
		function del() {
			var codes = getChecked('data_div','id').join(',');
			if (codes == '') {
			    $.alert('请选择要删除的数据！');
			} else {
				deleteRecord(codes);
			};
		}
		function deleteRecord(ids){
			$.confirm('确定要删除所选数据吗？', function(){
				$.ajax({
					url:'${path}/system/testTemplate/delAction.action?ids=' + ids,
				    type: 'post',
				    datatype: 'text',
				    success: function (returnValue) {
				    	$.alert(returnValue,function(){freshDataTable('data_div');});
				    }
				});
			});
		}
		function query(){
			$grid.query();
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="###">系统管理</a></li>
					<li class="active">系统设置</li>
					<li class="active">试卷模板</li>
				</ol>
			</div>
			
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				  <sec:authorize url="/system/testTemplate/upd.action">
						<input class="btn btn-primary" type="button" value="添加" onclick="add();"/>
					</sec:authorize>
					<sec:authorize url="/system/testTemplate/delAction.action">
						<input class="btn btn-primary" type="button" value="批量删除" onclick="del();"/>
					</sec:authorize>
				         
					<div style="float: right;">
					<form action="#">
						<div class="form-inline" style="float:right;">
						<div class="form-group">
							<input type="text" class="form-control" id="name" name="name" qMatch="like" placeholder="输入名称" />
						</div>
						<sec:authorize url="/system/testTemplate/list.action">
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="reset" value="清空" class="btn btn-primary"/>
						</sec:authorize>
					</div>
					</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width:100%;"></div>
			</div>
			
		</div>
	</div>
</body>
</html>