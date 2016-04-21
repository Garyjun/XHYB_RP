<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
		$(function(){
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = 'listStopWord.action';
	   		var _pk = 'id';
	   		var _conditions = ['name'];
	   		var _sortName = 'id';
	   		var _sortOrder = 'desc';
			var _colums = [{field:'name',title:'名称',width:fillsize(0.3),sortable:true},
							{field:'status',title:'状态',width:fillsize(0.1),formatter:$statusDesc},
							{field:'desc',title:'描述',width:fillsize(0.3)},
							{field:'opt1',title:'操作',width:fillsize(0.22),align:'center',formatter:$operate}];
	   		
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<sec:authorize url='/system/dataManagement/stopWord/detail.action'><a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
			opt += "<sec:authorize url='/system/dataManagement/stopWord/stopWordUpd.action'><a class=\"btn hover-red\" href=\"javascript:upd("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 修改</a></sec:authorize>";
			opt += "<sec:authorize url='/system/dataManagement/stopWord/delAction.action'><a class=\"btn hover-blue\" href=\"javascript:deleteRecord("+rec.id+")\" ><i class=\"fa fa-trash-o\"></i> 删除</a></sec:authorize>";
			return opt;
		};
		
		$statusDesc = function(value,rec){
			if(rec.status==1)
				return "可用";
			else
				return "禁用";
		}
		
		/***添加***/
		function add(){
			$.openWindow("${path}/system/dataManagement/stopWord/stopWordUpd.action",'添加',600,300);
//			location.href = "stopWordUpd.action";
		}
		
		/***修改***/
		function upd(id){
			$.openWindow("${path}/system/dataManagement/stopWord/stopWordUpd.action?id="+id,'编辑',600,300);
//			location.href = "stopWordUpd.action?id="+id;
		}
		
		function detail(id){
			$.openWindow("${path}/system/dataManagement/stopWord/detail.action?id="+id,'查看',600,250);
//			location.href = "detail.action?id="+id;
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
				location.href = '${path}/system/dataManagement/stopWord/delAction.action?ids=' + ids;
			});
		}
		function query(){
			$grid.query();
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap ">
		<div class="panel panel-default" style="height: 94%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">分类体系管理</a></li>
					<li class="active">停用词管理</li>
				</ol>
			</div>
		<div class="panel-body height_remain" id="999">
				<div class="form-group">
					<sec:authorize url="/system/dataManagement/stopWord/stopWordUpd.action">
						<input class="btn btn-primary" type="button" value="添加" onclick="javascript:add();"/>
					</sec:authorize>
					<sec:authorize url="/system/dataManagement/stopWord/delAction.action">
						<input class="btn btn-primary" type="button" value="批量删除" onclick="del();"/>
					</sec:authorize>					
				</div>
			<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
		</div>
		</div>
	</div>
</body>
</html>