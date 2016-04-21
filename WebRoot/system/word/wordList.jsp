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
	   		var _url = 'list.action';
	   		var _pk = 'id';
	   		var _conditions = ['name','similarWords'];
	   		var _sortName = 'id';
	   		var _sortOrder = 'desc';
			var _colums = [{field:'name',title:'敏感词',width:fillsize(0.15),sortable:true},
							{field:'similarWords',title:'相似词',width:fillsize(0.40),sortable:true},
							{field:'status',title:'状态',width:fillsize(0.1),formatter:$statusDesc},
							{field:'level',title:'等级',width:fillsize(0.1),formatter:$levelDesc},
							{field:'opt1',title:'操作',width:fillsize(0.22),align:'center',formatter:$operate}];
	   		
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	   		$grid.query();
		});
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<sec:authorize url='/system/word/detail.action'><a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
			opt += "<sec:authorize url='/system/word/upd.action'><a class=\"btn hover-red\" href=\"javascript:upd("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 修改</a></sec:authorize>";
			opt += "<sec:authorize url='/system/word/delete.action'><a class=\"btn hover-blue\" href=\"javascript:deleteRecord("+rec.id+")\" ><i class=\"fa fa-trash-o\"></i> 删除</a></sec:authorize>";
			return opt;
					
		};
		
		$statusDesc = function(value,rec){
			if(rec.status==1)
				return "可用";
			else
				return "禁用";
		}
		
		$levelDesc = function(value,rec){
			if(rec.level==1)
				return "高";
			else if(rec.level==2)
				return "中";
			else
				return "低";
		}
		/***添加***/
		function add(){
			$.openWindow("${path}/system/word/upd.action",'添加',600,350);
//			location.href = "upd.action";
		}
		
		/***修改***/
		function upd(id){
			$.openWindow("${path}/system/word/upd.action?id="+id,'编辑',600,350);
//			location.href = "upd.action?id="+id;
		}
		
		function detail(id){
			$.openWindow("${path}/system/word/detail.action?id="+id,'查看',600,350);
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
				$.ajax({
					url:'delete.action?ids=' + ids,
				    type: 'post',
				    datatype: 'text',
				    success: function (returnValue) {
				    	$grid.query();
				    }
				});
			});
		}
		function query(){
			$grid.query();
		}
		
		function importWord(){
			$.openWindow("system/word/importWord.jsp", '导入敏感词', 470, 250);
		}
		
		function formReset(){
			$('#queryForm')[0].reset();
			query();
		}
		
		function updateMany(){
			var wordIds = getChecked('data_div','id').join(',');
			if(wordIds==''){
				$.alert("请选择要修改的敏感词");
			}else{
				$.openWindow("${path}/system/word/updMany.action?ids="+wordIds,'修改',600,350);
			}
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">系统管理</a></li>
					<li class="active">敏感词管理</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
			<form action="#" id="queryForm">
					<sec:authorize url="/system/word/upd.action">
						<input class="btn btn-primary" type="button" value="添加" onclick="javascript:add();"/>
					</sec:authorize>
					<sec:authorize url="/system/word/importWord.jsp">
						<input class="btn btn-primary" type="button" value="批量导入" onclick="importWord();"/>
					</sec:authorize>
					<sec:authorize url="/system/word/delete.action">
						<input class="btn btn-primary" type="button" value="批量删除" onclick="del();"/>
					</sec:authorize>
					<sec:authorize url="/system/word/updMany.action">
						<input class="btn btn-primary" type="button" value="批量修改" onclick="updateMany();"/>	
					</sec:authorize>
					<div class="form-inline" style="float:right;">
						<div class="form-group">
							<label class="sr-only">名称:</label> 
							<input type="text" class="form-control" id="name" name="name" qMatch="like" placeholder="输入敏感词" />
							<input type="text" class="form-control" id="similarWords" name="similarWords" qMatch="like" placeholder="输入相似词" />
						</div>
						<sec:authorize url="/system/word/list.action">						
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<button type="button" class="btn btn-primary" onclick="formReset();">清空</button>
						</sec:authorize>
					</div>
					</form>
					<p></p>
					<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
				</div>
		</div>
		</div>
</body>
</html>