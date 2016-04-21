<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>用户组管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript">
		var $grid = null;
		$(function(){
 	   		//定义一datagrid
 	   		var _divId = 'data_div';
 	   		var _url = '${path}/system/group/list.action';
 	   		var _pk = 'id';
 	   		var _conditions = ['name'];
 	   		var _sortName = 'id';
 	   		var _sortOrder = 'desc';
 	   		var _check=true;
 			var _colums = [ 
 							{ title:'名称', field:'name' ,width:100, align:'center',sortable:true },
 							{ title:'包含角色', field:'findGroupRoles' ,width:100, align:'center' },
 							{ title:'包含用户', field:'findGroupUsers' ,width:100, align:'center' },
 						    { title:'创建时间', field:'createdTime' ,width:100, align:'center' },
 						    { title:'修改时间', field:'modifiedTime' ,width:100, align:'center' },
 							{title:'操作',field:'opt1',width:fillsize(0.17),align:'center',formatter:$operate}
 							
 						];
 	   		
 			accountHeight();
 			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
 	     	
 		});
		
		/***操作列***/
 		$operate = function(value,rec){
 			var opt = "";
 		    var viewUrl='<a class=\"btn hover-red\"  href="javascript:view('+rec.id+')"><i class=\"fa fa-sign-out\"></i>详细</a>&nbsp; ';		
		    var editUrl='<a  class=\"btn hover-red\" href="javascript:edit('+rec.id+')"><i class=\"fa fa-edit\"></i>修改</a>&nbsp; ';	
		    var delUrl='<a class=\"btn hover-red\" href="javascript:del('+rec.id+');"><i class=\"fa fa-trash-o\"></i>删除</a>&nbsp;';			    
 			opt=viewUrl+editUrl+delUrl;
 			return opt;
 		};
 		
 		 function del(id){
 	 		var url='${path}/system/group/delete.action?id='+id;
 	 		$.confirm('确定要删除该用户组吗？', function(){
	 			$.get(url,function(data){
	 				if(data=="0"){
	 					location.reload();
	 				}else{
	 					$.alert("有用户正在使用该用户组，不能删除！");
	 				}
	 			});
 			});
 	 	 }
 		 
		 function batchDel(){
			 var ids=getChecked('data_div','id').join(',');
			 if(ids==''){
				 $.alert('请选择要删除的用户组！');
				 return;
			 }
			 $.confirm('确定要删除所选数据吗？', function(){
				 $.get("${path}/system/group/batchDelete.action?ids="+ids,function(data){
		 				if(data=="0"){
		 					query();
		 				}else{
		 					$.alert("有用户正在使用该用户组，不能删除！");
		 				}
				 });				 
			});
			 
		 }
		 function query(){
			$grid.query();
		}
	     
	     /***添加***/
		function edit(id){
		  	location.href = "${path}/system/group/toEdit.action?id="+id;

		}
	    
		  /***添加***/
		function view(id){
		  	location.href = "${path}/system/group/view.action?id="+id;
		}
		 function query(){
			$grid.query();
		}	

			function formReset(){
				$grid.clean();
			//	$('#name').val("");
				$grid.query();
// 				$grid.clean();
				//$('#queryForm')[0].reset();
// 				query();
			}
		</script>
    </head>
   <body data-spy="scroll" >
     <div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            <a href="###">系统管理</a>
			        </li>
			        <li>
			            <a href="###">用户组管理</a>
			        </li>
			        <li>
			            <a href="###">用户组列表</a>
			        </li>
				</ul>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
					<sec:authorize url="/group/update*">
					    <button  class="btn btn-primary" onclick="edit(-1)">添加</button>
					</sec:authorize>
					<sec:authorize url="/group/delete/*">
		                <button class="btn btn-primary"  onclick="batchDel();">批量删除</button>
					</sec:authorize>
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" id="queryForm">
							<input type="text" class="form-control" id="name" name="name"  placeholder="输入用户组名" />
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<button type="button" class="btn btn-primary" onclick="formReset();">清空</button>
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
  </body>
</html>
