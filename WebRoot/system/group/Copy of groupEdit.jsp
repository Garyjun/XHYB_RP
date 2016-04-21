<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>

<html>
    <head>
    <title>编辑</title>
    
          <script type="text/javascript">
          	var $grid = null;
 			$(document).ready(function(){
 				
 		   		//定义一datagrid
 		   		var _divId = 'data_div';
 		   		var _url = '${path}/system/group/listUser.action?groupId='+$("#groupId").val()+"&userIds="+$("#addUserIds").val();
 		   		var _pk = 'id';
 		   		var _conditions = ['userName'];
 		   		var _sortName = 'id';
 		   		var _sortOrder = 'desc';
 		   		var _check=true;
 				var _colums = [{field:'loginName',title:'登录名',width:fillsize(0.2),sortable:true},
 								{field:'userName',title:'姓名',width:fillsize(0.2),sortable:true},
 								{field:'rolesDesc',title:'分配角色',width:fillsize(0.2), formatter:$getRolesDesc}];
 		   		
 				$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
				
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
			
			function addUser(){
				$("#addUserIds").val("");
				$.openWindow("${path}/system/group/addUser.jsp?groupId=${group.id}",'添加用户',900,600);
				//location.href = "${path}/system/group/addUser.jsp";
			}
			
			function delUser(){
				var ids = getChecked('data_div','id').join(',');
				if(ids.length==0){
					$.alert("请选择要删除的用户!");
					return;
				}
				$.get("${path}/system/group/delUser.action?ids="
						+ids+"&groupId="+"${group.id}",function(data){
	 				if(data=="0"){
	 					$grid.query();
	 				}else{
	 					$.alert("删除用户组出错！");
	 				}					
				});
			}
			
			function returnList(){
				location.href = "${path}/system/group/groupList.jsp";
			}
		     function query(){
		    	 var groupId = $("#groupId").val();
		    	 var userIds = $("#addUserIds").val();
		    	 //$grid.setUrl('${path}/system/group/listUser.action?groupId='+groupId+'&userIds='+userIds);
		    	 $grid.setUrl();
		    	 $grid.query();
			}			
			
		</script>

    </head>
     <body>
     	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
     	<div class="panel panel-default">
     	<div class="panel-heading" id="div_head_t">
     		<ol class="breadcrumb">
				<li><a href="javascript:;">用户组</a></li>
				<li class="active">编辑用户组</li>
			</ol>
     	</div>
     	<div class="panel-body height_remain" id="999">
	      		<form:form action="${path}/system/group/update.action" id="form1" method="POST" modelAttribute="group">
		      		<form:hidden path="id"  id="groupId"/>
		      		<div class="form-horizontal">
		      		<div class="form-group">
						<label for="name" class="col-sm-3 control-label text-right"><i class="must">*</i>名称：</label>
						<div class="col-sm-6">
							<form:input  path="name" id="name"  class="col-sm-6 form-control validate[required,maxSize[50]] text-input" />
						</div>
					</div>
						<div class="form-group">
							<label for="roleIds" class="col-sm-3 control-label text-right">分配角色：</label>
							<div class="col-sm-6">
								<c:forEach items="${roles}" var="role" varStatus="status">
									<c:if test="${role.status==1 }">
										<label class="checkbox-inline"> 
											<form:checkbox path="roleIds" value="${role.id}" /> ${role.roleName}
										</label>
						        		<c:if test="${status.index !=0 && status.index % 4 == 0}">
						        			<br />
						        		</c:if>	
						        		<c:if test="${status.isLast()}">
						        			<br />
						        		</c:if>						        											
									</c:if>
								</c:forEach>
							</div>
						</div>
						<div class="form-group">
						<div class="form-wrap">
							<input class="btn btn-primary" type="button" onclick="addUser();" value="添加用户" />
							<input class="btn btn-primary" type="button" onclick="delUser();" value="移除用户" />
						</div>						
					</div>
					</div>
					<div id="data_div" class="data_div height_remain" style="width: 100%; height: 400px;"></div>				
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-6">
			           		<input type="hidden" name="token" value="${token}" />
   					  		<input id="tijiao" type="submit" value="提交" class="btn btn-primary"/>
   							  &nbsp;
			            	<input class="btn btn-primary" type="button" value="返回" onclick="returnList();"/>
			            </div>
					</div>
					<form:hidden path="createdTime"/>
					<form:hidden path="modifiedTime"/>
					<form:hidden path="userIds"/>
					<input type="hidden" id="addUserIds" value="">
			</form:form> 
  		</div>
		</div>	
		</div>
    </body>
</html>