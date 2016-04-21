<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>用户管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <script type="text/javascript">
         $(function(){
 	   		//定义一datagrid
 	   		var _divId = 'data_div';
 	   		var _url = '${path}/user/query.action';
 	   		var _pk = 'id';
 	   		var _conditions = ['userName','loginName','orgnameByOrgId','useridByGroup','status'];
 	   		var _sortName = 'id';
 	   		var _sortOrder = 'desc';
 	   		var _check=true;
 			var _colums = [ 
 							{ title:'登录名', field:'loginName' ,width:100, align:'center' ,sortable:true},
 						    { title:'姓名', field:'userName' ,width:100, align:'center' ,sortable:true},
 						    { title:'所属部门', field:'orgnameByOrgId' ,width:100, align:'center'},
 						    { title:'已分配角色', field:'rolesDesc' ,width:200, align:'center', formatter:$getRolesDesc},
						    { title:'用户组', field:'useridByGroup' ,width:100, align:'center'},
 						    { title:'状态', field:'statusDesc' ,width:100, align:'center',formatter:$getStatusDesc },
 						   /* 	{ title:'手机', field:'mobile' ,width:100, align:'center'}, */
 							{title:'操作',field:'opt1',width:fillsize(0.12),align:'center',formatter:$operate}
 							
 						];
 	   		
 			accountHeight();
 			
 			/* var _buttons = [{  
				text:'二次查询' ,  
		        iconCls:'icon-search',    
		        handler:function(){ 
		           //alert("----"); 
		          layer.open({
   						type: 1,
   						title :'二次查询',
						area: ['500px', '300px'],
						btn: ['查询', '关闭'],
						fix: false,
						yes: function(index, layero){
							query();
							layer.closeAll();
						},
						scrollbar: false,
   						content: '<div class="col-md-6"> '+
		           				' <div class="form-group"> <label class="control-label col-md-4">姓名：</label>'+
		           				' <div class="col-md-8"> <input type="text" class="form-control" id="userName" qMatch="like"/>'+
		           				' </div> </div> </div>'
					});
		        }    
		    }]; */
 			//$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check,"","",true,_buttons);
 			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
 		});
         
         
         /***操作列***/
 		$operate = function(value,rec){
 			var optArray = new Array();
 		    var viewUrl = '<sec:authorize url="/user/view"><a class=\"btn hover-red\"  href="javascript:view('+rec.id+')"><i class=\"fa fa-sign-out\"></i>详细</a></sec:authorize> ';		
		    var editUrl = '<sec:authorize url="/user/update*"><a  class=\"btn hover-red\" href="javascript:edit('+rec.id+')"><i class=\"fa fa-edit\"></i>修改</a></sec:authorize> ';	
		    var delUrl = '<sec:authorize url="/user/delete"><a class=\"btn hover-red\" href="javascript:del('+rec.id+')"><i class=\"fa fa-trash-o\"></i>删除</a></sec:authorize>';
		    var resetPassword = '<sec:authorize url="/user/resetPassword"><a class=\"btn hover-red\" href="javascript:resetPassword('+rec.id+');"><i class=\"fa fa-refresh\"></i>重置</a></sec:authorize>';
		    var setStatus = "";
		    if(rec.status == 1)
		    	setStatus = '<a class=\"btn hover-red\" href="javascript:disabled('+rec.id+');"><i class=\"fa fa-lock\"></i>禁用</a>';
		    else
		    	setStatus = '<a class=\"btn hover-red\" href="javascript:enabled('+rec.id+');"><i class=\"fa fa-unlock\"></i>启用</a>';
		    var dataPreRangeUrl = '<a class=\"btn hover-red\" href="javascript:openDataRangePreWindow('+rec.id+');"><i class=\"fa fa-unlock\"></i>权限字段属性</a>';
		    <sec:authorize url="/user/viewUser">
		   	 optArray.push(viewUrl);
		    </sec:authorize>
		    <sec:authorize url="/user/addUser.action">
		    	optArray.push(editUrl);
		    </sec:authorize>
		    
		    <sec:authorize url="/user/delete">
		    if(rec.loginName != 'admin'){
		    	optArray.push(delUrl);
		    }
		    </sec:authorize>
		    
		    <sec:authorize url="/user/batchEabled.action">
		    if(rec.loginName != 'admin'){
		    	optArray.push(setStatus);
		    }
		    </sec:authorize>
		    
		    <sec:authorize url="/user/resetPassword.action">
		    	optArray.push(resetPassword);
		    </sec:authorize>
//		    optArray.push(dataPreRangeUrl);
		    return createOpt(optArray);
 		};
 		
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
 		
 		 function del(id){
 			$.ajax({
				url:"${path}/user/checkDelete.action?id="+id,
			    type: 'post',
			    datatype: 'text',
			    success: function (data) {
			    	if(data!=0){
			    		$.alert("该用户不能删除!");
			    	}else{
			    		 $.confirm('确定要删除该用户吗？', function(){
			    			 var url='${path}/user/delete/'+id+'.action';
				    		 $.ajax({
				    			 url:url,
				    			 type:'post',
				    			 datatype:'text',
				    			 success:function(data){
				    				 if(data == 'success'){
				    					 parent.queryForm();
				    				 }else{
				    					 $.alert("删除失败!");
				    				 }
				    			 }
				    		 });
			 			});
			    		 
			    		 
			 	 		/*  $.confirm('确定要删除该用户吗？', function(){
			 				 location.href =url; 
			 			}); */
			    	}
			    }
			});
 	 	 }
 		 
 		/* $.confirm('确定要删除该用户吗？', function(){
 		$.post('${path}/user/delete/'+id+'.action',function(data){
				if(data=="ok")
					//$grid.query();
					history.back(0);
				else
					$.alert(data);
					location.href = '${path}/system/user/userList.jsp';
			}); */
 		
		//批量删除
        function batchDel(){
		 var ids=getChecked('data_div','id').join(',');
		 if(ids==''){
			 $.alert('请选择要删除的用户！');
			 return;
		 }
		 $.ajax({
				url:"${path}/user/checkDelete.action?id="+ids,
			    type: 'post',
			    datatype: 'text',
			    success: function (data) {
			    	if(data!=0){
// 			    		$.alert("所选用户"+data+"不能删除，已分配为加工员");
			    		$.alert("所选用户不能删除!");
			    	}else{
			    		 $.confirm('确定要删除所选数据吗？', function(){
			    			 url = '${path}/user/batchDelete/'+ids+'.action';
			    			 $.ajax({
				    			 url:url,
				    			 type:'post',
				    			 datatype:'text',
				    			 success:function(data){
				    				 if(data == 'success'){
				    					 parent.queryForm();
				    				 }else{
				    					 $.alert("删除失败");
				    				 }
				    			 }
				    		 });
			    			 //window.location="${path}/user/batchDelete/"+ids+".action";
			    		});
			    	}
			    }
			});
	 }
        
		//启用用户
		function disabled(id){
			//window.location="${path}/user/batchDisabled/"+id+".action";
			url = '${path}/user/batchDisabled/'+id+'.action';
			 $.ajax({
    			 url:url,
    			 type:'post',
    			 datatype:'text',
    			 success:function(data){
    				 if(data == 'success'){
    					 parent.queryForm();
    				 }else{
    					 $.alert("启用失败");
    				 }
    			 }
    		 });
		} 
		
		//禁用用户
		function enabled(id){
			//window.location="${path}/user/batchEnabled/"+id+".action";
			url = '${path}/user/batchEnabled/'+id+'.action';
			$.ajax({
   			 url:url,
   			 type:'post',
   			 datatype:'text',
   			 success:function(data){
   				 if(data == 'success'){
   					 parent.queryForm();
   				 }else{
   					 $.alert("禁用失败");
   				 }
   			 }
   		 });
		}
		
		
		//重置密码
		function resetPassword(id){
			//window.location="${path}/user/resetPassword.action?id=" + id;
			$.ajax({
	   			 url:"${path}/user/resetPassword.action?id=" + id,
	   			 type:'post',
	   			 datatype:'text',
	   			 success:function(data){
	   				 if(data == 'success'){
	   					 parent.queryForm();
	   				 }else{
	   					 $.alert("重置失败");
	   				 }
	   			 }
	   		 });
		}
        
		 //批量禁用
		 function batchDisabled(){
			 var ids=getChecked('data_div','id').join(',');
			 if(ids==''){
			     /* $.messager.lays(170, 120);
	    		 $.messager.anim('slide', 800);
	    		 $.messager.show("提示信息", "请选择要禁用的用户！",3000);  */
	    		 //notice("提示信息", "请选择要禁用的用户！",2);
				 $.alert('请选择要禁用的用户！');
				 return;
			 }
			//window.location="${path}/user/batchDisabled/"+ids+".action";
			$.ajax({
	   			 url:'${path}/user/batchDisabled/'+ids+'.action',
	   			 type:'post',
	   			 datatype:'text',
	   			 success:function(data){
	   				 if(data == 'success'){
	   					 parent.queryForm();
	   				 }else{
	   					 $.alert("禁用失败");
	   				 }
	   			 }
	   		 });
		 }
		 
		 //批量启用
		 function batchEnabled(){
			 var ids=getChecked('data_div','id').join(',');
			 if(ids==''){
				 $.alert('请选择要启用的用户！');
				 return;
			 }
			//window.location="${path}/user/batchEnabled/"+ids+".action";
			$.ajax({
	   			 url:'${path}/user/batchEnabled/'+ids+'.action',
	   			 type:'post',
	   			 datatype:'text',
	   			 success:function(data){
	   				 if(data == 'success'){
	   					 parent.queryForm();
	   				 }else{
	   					 $.alert("启用失败");
	   				 }
	   			 }
	   		 });
		 }
     function query(){
			$grid.query();
	}
     
     /***添加***/
	function edit(id){
/* 		if(id==-1) {
			$.openWindow("${path}/user/toEdit.action?id="+id,'添加用户',600,550);
		}else{
		  	$.openWindow("${path}/user/toEdit.action?id="+id,'修改用户',600,550);
		} */
		location.href = "${path}/user/toEdit.action?id="+id;
	}
    
	  /***详细***/
	function view(id){
		location.href = "${path}/user/view.action?id="+id;
	//  $.openWindow("${path}/user/view.action?id="+id,'查看用户',600,550);
	}
				
	function formReset(){
// 		$grid.clean();
		$('#loginName').val("");
		$('#userName').val("");
		$('#status').val("");
		$('#orgnameByOrgId').val("");
		$('#useridByGroup').val("");
		$grid.query();
	}				
				
	function setDataRangePre(){
		var ids = getChecked('data_div','id').join(',');
		if(ids==''){
			$.alert('请选择要启用的用户！');
			return;
		}	
	}
	
	function openDataRangePreWindow(id){
		location.href = "${path}/system/user/userDataPreRange.jsp?id="+id;
	}
</script>
</head>
	<body>
    <div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            	系统管理
			        </li>
			        <li>
			            	用户管理
			        </li>
			        <li>
			            	用户列表
			        </li>
				</ul>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				   <!--  <a class="btn btn-primary" href="#" onclick="edit(-1)">新增</a>   -->
				     <sec:authorize url="/user/update">
				        <button  class="btn btn-primary" onclick="edit(-1)">添加</button>
				     </sec:authorize> 
				     <sec:authorize url="/user/delete"> 
				      <button  class="btn btn-primary" onclick="batchDel();">批量删除</button>
				     </sec:authorize>
				     <sec:authorize url="/user/batchEabled.action"> 
				      <button  class="btn btn-primary" onclick="batchEnabled();">批量启用</button>
				     </sec:authorize>				     
				     <sec:authorize url="/user/batchEabled.action"> 
				      <button  class="btn btn-primary" onclick="batchDisabled();">批量禁用</button>
				     </sec:authorize>
						<!--解决列表表上面的按钮与查询条件间隙问题 -->
				      <button  class="btn btn-primary" style="background:white;width:1px;">&nbsp;</button>	
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form" id="queryForm">
						   <div class="form-group">
								 <!--  <label for="userName" class="control-label" >登录名:</label> -->
							      <input type="hidden" class="form-control" id="loginName" name="loginName" value="${param.loginName}" qMatch="like"  placeholder="输入登录名"/>
							</div>
							<div class="form-group">
								  <!-- <label for="userName" class="control-label" >用户名:</label> -->
							      <input type="hidden" class="form-control" id="userName" name="userName"  value="${param.userName}" qMatch="like" placeholder="输入姓名" />
							</div>
							<div class="form-group">
								  <!-- <label for="userName" class="control-label" >组织部门:</label> -->
							      <input type="hidden" class="form-control" id="orgnameByOrgId" name="orgnameByOrgId"  value="${param.orgnameByOrgId}" qMatch="like" placeholder="输入所属部门" />
							</div>
							<div class="form-group">
								  <!-- <label for="userName" class="control-label" >用户组:</label> -->
							      <input type="hidden" class="form-control" id="useridByGroup" name="useridByGroup"  value="${param.useridByGroup}" qMatch="like" placeholder="输入用户组" />
							</div>
							<div class="form-group">
								  <!-- <label for="userName" class="control-label" >状态:</label> -->
								 <%--  <c:if test="${record != null || record != ''}"> --%>
								  	<input type="hidden" class="form-control" id="status" name="status" value="${param.status}"/>
								  <%-- </c:if> --%>
								  <%-- <c:if test="${record == 'update'}">
							      	<input type="text" class="form-control" id="status" name="status" value="${status}"/>
								  </c:if> --%>
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
