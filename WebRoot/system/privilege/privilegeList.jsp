<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>权限管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript">
		 $(function(){
	 	   		//定义一datagrid
	 	   		var _divId = 'data_div';
	 	   		var _url = '${path}/privilege/query.action';
	 	   		var _pk = 'id';
	 	   		var _conditions = ['privilegeName','moduleId'];
	 	   		var _sortName = 'id';
	 	   		var _sortOrder = 'desc';
	 	   		var _check=true;
	 			var _colums = [ 
	 							{ title:'权限菜单', field:'module.moduleName' ,width:100, align:'center' },
	 						    { title:'权限名称', field:'privilegeName' ,width:100, align:'center' ,sortable:true},
	 						    { title:'显示顺序', field:'displayOrder' ,width:100, align:'center' },
	 						    { title:'权限状态', field:'statusDesc' ,width:100, align:'center',hidden:true },
	 							{title:'操作',field:'opt1',width:fillsize(0.17),align:'center',formatter:$operate}
	 							
	 						];
	 	   		
	 			accountHeight();
	 			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
	 		});
		 /***操作列***/
	 		$operate = function(value,rec){
	 			var opt = "";
	 		    var viewUrl='<sec:authorize url="/privilege/view/*"><a class=\"btn hover-red\"  href="javascript:view('+rec.id+')"><i class=\"fa fa-sign-out\"></i>详细</a>&nbsp;</sec:authorize> ';		
			    var editUrl='<sec:authorize url="/privilege/update"><a  class=\"btn hover-red\" href="javascript:edit('+rec.id+')"><i class=\"fa fa-edit\"></i>修改</a>&nbsp;</sec:authorize> ';	
			    var delUrl='<sec:authorize url="/privilege/delete/*"><a class=\"btn hover-red\" href="javascript:del('+rec.id+');"><i class=\"fa fa-trash-o\"></i>删除</a>&nbsp;</sec:authorize>';	
	 			opt=viewUrl+editUrl+delUrl;
	 			//opt=viewUrl;
	 			return opt;
	 					
	 		};
	 	 function del(id){
	 		var url='${path}/privilege/delete/'+id+'.action';
	 		$.confirm('确定要删除该权限吗？', function(){
		 			$.get(url,function(data){
		 				if(data=="0"){
		 					query();
		 				}else{
		 					$.alert("该权限有相关的菜单和角色数据，不能删除！");
		 				}
		 			});				 
			});
	 	 }
		 function batchDel(){
			 var ids=getChecked('data_div','id').join(',');
			 if(ids==''){
				 $.alert('请选择要删除的权限！');
				 return;
			 }

			$.confirm('确定要删除所选数据吗？', function() {
				$.get("${path}/privilege/batchDelete/" + ids + ".action",
					function(data) {
						if (data == "0") {
								query();
						} else {
								$.alert("该权限有相关的菜单和角色数据，不能删除！");
							}
						});
				});

			}
			function query() {
				$grid.query();
			}

			/***添加***/
			function edit(id) {
				$.openWindow("${path}/privilege/toEdit.action?id=" + id,
						'编辑权限', 560, 500);
			}

			/***查看***/
			function view(id) {
				$.openWindow("${path}/privilege/view.action?id=" + id, '查看权限',
						560, 400);
			}
			function formReset(){
		 		$grid.clean();
// 				$('#moduleId').val("");
// 				$('#privilegeName').val("");
 				$grid.query();
			}
		</script>
    </head>
    <body>
	    <div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
			<div class="panel panel-default" style="height: 100%;">
				<div class="panel-heading" id="div_head_t">
					<ul class="breadcrumb">
				        <li>
				            <a href="###">系统管理</a>
				        </li>
				        <li>
				            <a href="###">权限管理</a>
				        </li>
				        <li>
				            <a href="###">权限列表</a>
				        </li>
					</ul>
				</div>
				<div class="panel-body height_remain" id="999">
					<div style="margin: 0px 10px 10px 0px;height:34px">
						<sec:authorize url="/privilege/update">
						    <button  class="btn btn-primary" onclick="edit(-1)">添加</button>
						</sec:authorize>
						<sec:authorize url="/privilege/delete/*">
		                    <button class="btn btn-primary"  onclick="batchDel();">批量删除</button>
						</sec:authorize>
						 <button  class="btn btn-primary" style="background:white;width:1px;">&nbsp;</button>	
						 <span>&nbsp;</span>
						<div style="float: right;">
							<form action="#" class="form-inline no-btm-form" >
							   <div class="form-group">
								 <label for="command.module.id" class="control-label" >所属菜单:</label>
							       <form:select path="command.module.id"  id="moduleId"  class="form-control">  
							                <form:option value="" label="---请选择---"/>  
	    						 			<form:options items="${modules}"  itemLabel="moduleName" itemValue="id" />  
	    						  </form:select>  
	    						
							 </div> 
							  <div class="form-group">
								      <input type="text" class="form-control" id="privilegeName" name="privilegeName" qMatch="like"  placeholder="输入权限名" />
								</div>
								 
								<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
								<input type="reset" value="清空" class="btn btn-primary" onclick="formReset();"/>
							</form>
						</div>
					</div>
					<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
				</div>
			</div>
	</div>
	   
				

    </body>
</html>
