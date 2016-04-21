<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>

<html>
    <head>
    <title>用户组详细</title>
    
          <script type="text/javascript">
			var roleNames = '${roleNames}';
 			$(document).ready(function(){
 				
 		   		//定义一datagrid
 		   		var _divId = 'data_div1';
 		   		var _url = '${path}/system/group/listUser.action?groupId='+$("#groupId").val()+"&userIds=";
 		   		var _pk = 'id';
 		   		var _conditions = ['userName'];
 		   		var _sortName = 'id';
 		   		var _sortOrder = 'desc';
 				var _colums = [{field:'loginName',title:'登录名',width:fillsize(0.2),sortable:true},
 								{field:'userName',title:'姓名',width:fillsize(0.2),sortable:true},
 								{field:'rolesDesc',title:'分配角色',width:fillsize(0.2), formatter:$getRolesDesc}];
 		   		
 		   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
				
				typeTarget = eval('(' + roleNames + ')');
				var div = $('#roleRow');
				var  roleName = "";
				for(var i=1;i<typeTarget.length+1;i++){
					if(i==typeTarget.length){
						roleName += typeTarget[i-1].name;
					}else{
						roleName += typeTarget[i-1].name+"，";
					}
					if(i%8==0){
						roleName +="<br/>";
					}
				}
				divRow = $("<p class=\"form-control-static\">"+roleName+"</p>");
				divRow.appendTo(div);
				$('#form1').validationEngine('attach', {
					relative: true,
					overflownDIV:"#divOverflown",
					promptPosition:"centerRight",//验证提示信息的位置，可设置为："topRight", "bottomLeft", "centerRight", "bottomRight"
					maxErrorsPerField:1,
					onValidationComplete:function(form,status){
						if(status){
							var params = "?addUserIds=" + $("#addUserIds").val();
							$("#form1").attr("action","${path}/system/group/update.action" + params);
							$('#form1').ajaxSubmit({
								method: 'post',//方式
								success:(function(response){
									returnList();
				       			})
							});
							//$("#form1").submit();
						}
					}
				});
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
			
			function returnList(){
				location.href = "${path}/system/group/groupList.jsp";
			}
			
		</script>

    </head>
     <body>
     	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
     	<div class="panel panel-default">
     	<div class="panel-heading" id="div_head_t">
     		<ol class="breadcrumb">
				<li><a href="javascript:;">用户组</a></li>
				<li class="active">用户组详细</li>
			</ol>
     	</div>
     	<div class="panel-body height_remain" id="999">
     		<div class="form-wrap">
	      		<form:form action="${path}/system/group/update.action"  name="form1" id="form1" method="POST" modelAttribute="group" role="form">
		      		<form:hidden path="id"  id="groupId"/>
		      		<div class="form-horizontal">
		      		<div class="form-group">
						<label for="name" class="col-sm-3 control-label text-right">名称：</label>
						<div class="col-sm-6">
							<p class="form-control-static">${group.name}</p>
						</div>
					</div>
					 <div class="form-group">
		                <label for="roleIds" class="col-sm-3 control-label text-right" >分配角色：</label>
		                 <div class="col-sm-6" id="roleRow">
		                 </div>
			      </div>	
			      <div class="form-group">
						<div class="col-sm-offset-5 col-sm-6">
			            	<input class="btn btn-primary" type="button" value="返回" onclick="returnList();"/>
			            </div>
					</div>				
					</div>
					
					<div id="data_div1" class="data_div height_remain" style="width: 100%; height: 400px;"></div>				
					
			</form:form> 
			</div>  	
  		</div>
		</div>	
		</div>
    </body>
</html>