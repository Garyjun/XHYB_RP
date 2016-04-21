<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>


<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	var targetName = '${param.targetName}';
		$(function(){
			if(targetName!=""){
				targetName = decodeURIComponent($('#targetName').val());
				$('#targetName').val(targetName);
			}
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = '${path}/target/list.action';
	   		var _pk = 'id';
	   		var _conditions = ['targetName','module','status','targetType'];
	   		var _sortName = 'id';
	   		var _sortOrder = 'desc';
			var _colums = [{field:'targetName',title:'名称',width:fillsize(0.1),align:'center' ,sortable:true},
			               {field:'targetNum',title:'使用次数',width:fillsize(0.1),align:'center'},
// 							{field:'targetStatus',title:'状态',width:fillsize(0.1),align:'center' ,formatter:$statusDesc},
							{field:'moduleType',title:'类型',width:fillsize(0.1),align:'center'},
							{field:'targetCb',title:'标签分类',width:fillsize(0.1),align:'center'},
							{field:'opt1',title:'操作',width:fillsize(0.17),align:'center',formatter:$operate}
							];
			
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
// 			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
//	   		$grid.query();
		});
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<sec:authorize url='/target/detail.action'><a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
			opt += "<sec:authorize url='${path}/target/upd.action'><a class=\"btn hover-red\" href=\"javascript:upd("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 修改</a></sec:authorize>";
			opt += "<sec:authorize url='${path}/target/delete.action'><a class=\"btn hover-blue\" href=\"javascript:deleteRecord('"+rec.id+"','"+rec.module+"','"+rec.targetName+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a></sec:authorize>";
			return opt;
					
		};
		
		$statusDesc = function(value,rec){
			if(rec.targetStatus==1)
				return "启用";
			else
				return "禁用";
		}
		
		/***添加***/
		function add(){
			$.openWindow("${path}/target/upd.action?typeTarget="+$('#typeTarget').val(),'添加',600,400);
//			location.href = "upd.action";
		}
		
		/***修改***/
		/* function upd(id){
			$.ajax({
				url:'${path}/target/deleteYes.action?ids='+id,
			    type: 'post',
			    async : true,
			    success: function (Data) {
			    	data = eval('('+Data+')');
			    	if(data.status==1){
						$.confirm('该标签有关联数据！！如果修改标签类型会清空标签关联数据！',
								function(){
							$.openWindow("${path}/target/upd.action?id="+id+"&typeTarget="+$('#typeTarget').val(),'编辑',600,350);
						});
			    	}else{
							$.openWindow("${path}/target/upd.action?id="+id+"&typeTarget="+$('#typeTarget').val(),'编辑',600,350);
			    	}
			    	$grid.query();
			    }
			});
		} */
		/***修改***/
		function upd(id){
			$.openWindow("${path}/target/upd.action?id="+id+"&typeTarget="+$('#typeTarget').val(),'编辑',600,400);
		}
		function detail(id){
			$.openWindow("${path}/target/detail.action?id="+id,'详细',600,400);
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
		function deleteRecord(ids,publishType,targetName){
			var name = targetName;
			$.ajax({
				url : "${path}/tagHtml/getMainPageTargetTag.action?publishType="+publishType,
				type : 'post',
				datatype : 'text',
				async:false,
				success : function(returnValue) {
					if (returnValue != undefined || returnValue != '') {
							$('#targetField').val(returnValue);
					}
				}
			});
			targetName = encodeURI(encodeURI(targetName));
			$.ajax({
				url:'${path}/target/deleteTarget.action?ids='+ids+"&targetField="+$('#targetField').val()+"&targetName="+targetName+"&publishType="+publishType,
			    type: 'post',
			    async : false,
			    success: function (Data) {
			    	data = eval('('+Data+')');
			    	if(data.status==1){
						$.confirm('所选标签['+name+']有数据关联！！确定要删除吗？',
								function(){
							$.ajax({
								url:'${path}/target/delete.action?ids=' + ids+"&publishType="+publishType,
							    type: 'post',
							    datatype: 'text',
							    success: function (returnValue) {
							    	$grid.query();
							    }
							});
						});
			    	}else{
						$.confirm('确定要删除所选标签吗？',
								function(){
							$.ajax({
								url:'${path}/target/delete.action?ids=' + ids+"&publishType="+publishType,
							    type: 'post',
							    datatype: 'text',
							    success: function (returnValue) {
							    	$grid.query();
							    }
							});
						});
			    	}
			    	$grid.query();
			    }
			});
		}
		function query(){
			$grid.query();
		}
		
		function importWord(){
			$.openWindow("system/word/importWord.jsp", '导入标签', 450, 200);
		}
	</script>
</head>
<body>
   <div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">资源管理</a></li>
					<li class="active">标签管理</li>
					<li class="active">标签列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				    <sec:authorize url="${path}/target/upd.action">
						<input class="btn btn-primary" type="button" value="添加" onclick="javascript:add();"/>
					</sec:authorize>
					<sec:authorize url="${path}/target/delete.action">
						<input class="btn btn-primary" type="button" value="批量删除" onclick="del();"/>
					</sec:authorize>
					<div style="float: right;">
					   <input type="hidden" id="userId" name="userId" value=""/>
					   <input type="hidden" id="targetField" name="targetField" value=""/>
					   <input type="hidden" id="targetName" name="targetName" value="${param.targetName }" qMatch="like"/>
					   <input type="hidden" id="module" name="module" value="${param.module }" qType="String"/>
					   <input type="hidden" id="status" name="status" value="${param.status }" qType="int"/>
					   <input type="hidden" id="typeTarget" name="typeTarget" value="${param.typeTarget}"/>
					   <input type="hidden" id="targetType" name="targetType" value="${param.targetType}"/>
			        </div>
				</div>
			   <div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
		  </div>
		</div>
	</div>
</body>
</html>