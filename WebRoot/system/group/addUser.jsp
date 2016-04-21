<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>用户管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
         <script>
         $(function(){
 	   		//定义一datagrid
 	   		var _divId = 'data_div';
 	   		var _url = '${path}/user/query.action';
 	   		var _pk = 'id';
 	   		var _conditions = ['userName','status'];
 	   		var _sortName = 'id';
 	   		var _sortOrder = 'desc';
 	   		var _check=true;
 			var _colums = [ 
 							{ title:'登录名', field:'loginName' ,width:100, align:'center' },
 						    { title:'姓名', field:'userName' ,width:100, align:'center' ,sortable:true},
 						    { title:'状态', field:'statusDesc' ,width:100, align:'center', formatter:$getStatusDesc },
 						    { title:'分配角色', field:'rolesDesc' ,width:200, align:'center', formatter:$getRolesDesc}
 						];
 	   		
 			accountHeight();
 	   		$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
 		});
 		/**获取角色信息**/
 		$getRolesDesc = function(value,rec){
 		var rolesDesc = "";
 			$.ajax({
 				url:'${path}/user/getRolesDesc.action?id='+rec.id,
 				async: false,
 				success:function (data){
 					rolesDesc = data;
 				}
 			});
 			return rolesDesc;
 		};
 		
 		$getStatusDesc = function(value,rec){
 			var statusDesc = "";
 			if(rec.status==0)
 				statusDesc = "禁用";
 			else
 				statusDesc = "启用";
 			return statusDesc;
 		};

     function query(){
			$grid.query();
	}
				
	function formReset(){
		$('#queryForm')[0].reset();
		query();
	}				
				
	function addUser(){
		var ids=getChecked('data_div','id').join(',');
		if(ids.length==0){
			$.alert("请选择要添加的用户!");
			return;
		}
		if($("#groupId").val().length>0){
			$.get("${path}/system/group/addUser.action?groupId="
					+$("#groupId").val()+"&userIds="+ids,function(data){
				top.index_frame.work_main.query();
				$.closeFromInner();
			});
		}else{
			top.index_frame.work_main.document.getElementById("addUserIds").value = ids;
			top.index_frame.work_main.query();
			$.closeFromInner();			
		}
	}
</script>
    </head>
<body>
    <div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 600px;">
    	<div class="panel panel-default" style="height: 100%;">
			<div class="panel-body height_remain" id="999" style="height: 80%;">
				<div class="form-group">
				    <button  class="btn btn-primary" onclick="addUser();">添加用户</button>
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form" id="queryForm">
						<input type="hidden" name="status" id="status" value="1"></input>
							<div class="form-group">
							      <input type="text" class="form-control" id="userName" name="userName"  placeholder="输入姓名" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<button type="button" class="btn btn-primary" onclick="formReset();">清空</button>
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width:830px;"></div>
			</div>
		</div>
	</div>
	<input type="hidden" id="groupId" value="${param.groupId}"/>
</body>
</html>
