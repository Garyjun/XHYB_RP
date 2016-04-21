<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>

<html>
    <head>
    <title>查看</title>   
    <link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
        <script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>    
    </head>
     <script type="text/javascript">
     jQuery(document).ready(function(){
			var groupName = '${groupName}';
			$('#orgName').val(groupName);
    		//部门树
			var orgStr='${orgJson}';
			var orgNodes = jQuery.parseJSON(orgStr);
			setOrgCheckedNodes(orgNodes);
			var orgTreeSetting = {
					check: {
						enable: true
					},
					data: {
						simpleData: {
							enable: true,
							idKey: "id",
							pIdKey: "pid",
							rootPId: -1
						}
					}					
			};
			var tree = $.fn.zTree.init($("#organizationTree"), orgTreeSetting,orgNodes);
			var root = tree.getNodes()[0];
			if(root){
				tree.expandNode(root,true,false,true);
			}
			var resGroups = ${resTypeObj};
			
			for(var type in resGroups){
				addDivToDataPri(resGroups[type].resTypeName,resGroups[type].resTypeId,resGroups[type].groupArray);
			}
			
			//fengda2015年10月23日添加
	   		//根据所选择的资源类型，加载相对应的资源目录
			for(var type in resGroups){
				addDivResourcesTree(resGroups[type].resTypeName,resGroups[type].resTypeId);
			}
			
 		}); 
     
     	//fengda2015年10月23日添加
   		//根据所选择的资源类型，加载相对应的资源目录
		function addDivResourcesTree(name,value){
			var outerDiv = $("<div id='" + value + "_divRes" + "' ></div>");
			outerDiv.appendTo($("#tab_1_1_5"));
			//资源库名
			var title = $("<label for='email' class='col-sm-2 control-label' style='font-size:15px;'>"+name + "：</label>");
			title.appendTo(outerDiv);
			var hr = $("<hr/>");
			hr.appendTo(outerDiv);
			
			
			$.get("${path}/user/getResourcesDirectory.action?typeId="+value,function(data){
					var group = jQuery.parseJSON(data);
	 				//组名
	 				var div = $("<div class='form-group' name='checkboxGroupRes'></div>");
	 				div.appendTo(outerDiv);
	 				//字段
	 				var checkboxDiv = $("<div class='col-sm-9'></div>");
	 				checkboxDiv.appendTo(div);
	 				
	 				//已选择的id
	 				var ids = "";
	 				var dataArray = [];
	 				if($("#resourceDataJson").val()){
	 					dataArray = jQuery.parseJSON($("#resourceDataJson").val());
	 				}
	 				
	 				 $.each(dataArray,function(i,data){
	 					if(data.id == value){
	 						ids = data.field;
	 					}
	 				}); 
	 				
	 				if(group != "" && group != null){
	 					//回调函数拥有两个参数：第一个为对象的成员或数组的索引，第二个为对应变量或内容
		 				$.each(group,function(index,filed){
		 					var checkboxLabel = $("<label class='checkbox-inline'></label>");
		 					checkboxLabel.appendTo(checkboxDiv);
		 					checkboxLabel.text(filed.resType);
		 					
		 					var checkbox = $("<input type='checkbox' name='" + "Resource_"+value + "' readonly='readonly' onclick='return false'/>");
		 					checkbox.attr("value",filed.resType);
		 					//比较该资源下数据库中存放的资源目录，和根据资源库id查询出的所有资源目录
		 					//相同的证明以选中显示在页面上
		 					 if(ids.indexOf(filed.resType)!=-1){
		 						checkbox.attr("checked",true);
		 					} 
		 					checkbox.appendTo(checkboxLabel);
		 					
		 					var br = $("<br />");
		 					if(index!=0&&index%5==0){
		 						br.appendTo(checkboxDiv);
		 					}
// 		 					 if(index == group.length-1){
// 		 						br.appendTo(checkboxDiv);
// 		 						//全选
// 		 						var allCheckLabel = $("<label class='checkbox-inline'></label>");
// 		 						allCheckLabel.appendTo(checkboxDiv);
// 		 						allCheckLabel.text("全选"); 						
// 		 						var allCheck = $("<input type='checkbox' id='allCheck_"+ value +"' />");
// 		 						allCheck.appendTo(allCheckLabel);
// 		 						allCheck.click(function(){
// 		 							if(this.checked){
// 			 							$("input[name='Resource_"+ value +"']").attr("checked",true);
// 		 							}else{
// 		 								$("input[name='Resource_"+ value +"']").attr("checked",false);
// 		 							}
// 		 						});		 						
// 		 					} 
		 				});
			   		}
				});
			}
     
     
     function setOrgCheckedNodes(orgNodes){
			var resCodes = $("#organization").val();
			if(resCodes){
				var checkedNodes = resCodes.split(",");
				$.each(orgNodes,function(n,node){
					if($.inArray(node.id+"",checkedNodes)!=-1)
						node.checked = true;
					else
						node.checked = false;
				});
			}
		}
     function addDivToDataPri(name,value,groups){
			var outerDiv = $("<div id='" + value + "_div" + "' ></div>");
			outerDiv.appendTo($("#tab_1_1_2"));
			//资源库名
			var title = $("<h4>" + name + "</h4>");
			title.appendTo(outerDiv);
			var hr = $("<hr/>");
			hr.appendTo(outerDiv);
			for(var group in groups){
				var groupDiv = $("<div class='form-group'></div>");
				var lableDiv = $("<label for='mobile' class='col-sm-3 control-label'>"+groups[group].groupName+"：</label>");
				lableDiv.appendTo(groupDiv);
				var contentDiv =  $("<div class='col-sm-9'></div>");
				var p = $("<p class='form-control-static'>"+groups[group].selectField+"</p>");
				p.appendTo(contentDiv);
				contentDiv.appendTo(groupDiv);
				groupDiv.appendTo(outerDiv);
			}
     }
   	function returnList(){
			location.href = "${path}/system/user/userList.jsp";
		}
    	</script>
     <body>
     <div id="fakeFrame" class="container-fluid by-frame-wrap">
		<div class="panel panel-default">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            	系统管理
			        </li>
			        <li>
			            	用户管理
			        </li>
			        <li>
			            	用户详细
			        </li>
				</ul>
			</div>
			<p><small><font color="red">*注意：如果修改了【角色权限】和【用户组权限】系统重新登录之后才生效。</font></small></p>
      	<div class="form-wrap">
	      		<form:form action=""  name="form1" id="form1" method="POST"  class="form-horizontal">
		      		<div class="form-group">
						<label for="loginName" class="col-sm-4 control-label text-right">登录名：</label>
						<div class="col-sm-5">
						             <p class="form-control-static">
		               					${command.loginName}
		               				 </p>
								 
						</div>
					</div>
					
					<div class="form-group">
						<label for="userName" class="col-sm-4 control-label text-right">姓名：</label>
						<div class="col-sm-5">
							  <p class="form-control-static">
		               					${command.userName}
		               		 </p>
						</div>
					</div>
					
					<div class="form-group">
						<label for="userName" class="col-sm-4 control-label text-right">所属部门：</label>
						<div class="col-sm-6 input-group">
						<span class="col-sm-6 input-group-btn">
						<input type="text" class="col-sm-10 form-control validate[required]" id="orgName" name="orgName" readonly="readonly"/>
						</span>
						</div>
					</div>
					
					<div class="form-group">
						<label for="phone" class="col-sm-4 control-label text-right">电话：</label>
						<div class="col-sm-5">
							 <p class="form-control-static">
		               					${command.phone}
		               		 </p>
						</div>
					</div>
					<div class="form-group">
						<label for="mobile" class="col-sm-4 control-label text-right">手机：</label>
						<div class="col-sm-5">
							  <p class="form-control-static">
		               					${command.mobile}
		               		 </p>
						</div>
					</div>
					
					<div class="form-group">
						<label for="email" class="col-sm-4 control-label text-right">email：</label>
						<div class="col-sm-5">
							  <p class="form-control-static">
		               					${command.email}
		               		 </p>
						</div>
					</div>
			      
			     <div class="form-group">
			     <label for="privilegeTree" class="col-sm-3 control-label text-right" >权限设置</label>
			      	<div class="by-tab col-sm-7">
			      		<ul class="nav nav-tabs" style="width: 520px;">
			      			<li class="active" style="width: 125px;"><a href="#tab_1_1_4" data-toggle="tab">角色权限</a></li>
			      			<li style="width: 125px;"><a href="#tab_1_1_1" data-toggle="tab">数据权限</a></li>
			      			<li style="width: 125px;"><a href="#tab_1_1_2" data-toggle="tab">字段权限</a></li>
			      			<!-- <li><a href="#tab_1_1_3" data-toggle="tab">字段属性权限</a></li> -->
			      			<li style="width: 125px;"><a href="#tab_1_1_5" data-toggle="tab">资源目录权限</a></li>
			      		</ul>
			      	</div>
			      </div>
			       <div class="form-group">
			      	<label for="privilegeTree" class="col-sm-3 control-label text-right" ></label>
			      	<div class="tab-content col-sm-7">
			      		<div class="tab-pane" id="tab_1_1_1" >
			      		<p><small><font color="red">*注意：<br>按组织部门授权：该用户能看到已授权的组织部门下的所有人上传的资源。<br>按个人用户授权：该用户能看到自己上传的资源。<br>同时授权：则该用户能看到已授权的组织部门下的所有人上传的资源和自己上传的资源。<br>都不授权：该用户看不到任何资源。<br>资源库授权：该用户能看到已授权的资源库，否则无权限。</font></small></p>
			      			<div class="form-group">
								<label for="roleIds" class="col-sm-3 control-label text-right" >组织部门：</label>
								<div class="col-sm-9">
									<div class="zTreeBackground" >
										<ul id="organizationTree"  class="ztree"></ul>
									</div>
								</div>
			      			</div>
							<div class="form-group">
								<label class="col-sm-3 control-label text-right" >个人用户授权：</label>
								<div class="col-sm-9">
									<p class="form-control-static">${isPrivateName} </p>
								</div>							
							</div>	
							<div class="form-group">
						      	<label for="roleIds" class="col-sm-3 control-label text-right" >资源库：</label>
						      	<div class="col-sm-9">
						      		 <p class="form-control-static">${(resTypeName eq '' || resTypeName eq null)?'未授权':resTypeName} </p>
						      	</div>
						     </div>						
						</div>
						<div class="tab-pane" id="tab_1_1_2">
						<p><small><font color="red">*注意：不勾选元数据项，则无元数据权限。</font></small></p>
						<div class="form-group">
							<label for="roleIds" class="col-sm-3 control-label text-right" >核心元数据：</label>
					      	<div class="col-sm-9">
					      		 <p class="form-control-static">${(coreMetadata eq '' || coreMetadata eq null)?'未授权':coreMetadata} </p>
					      	</div>
					      	</div>	
						</div>
 						<%-- <div class="tab-pane" id="tab_1_1_3">
  							<div class="portlet-body" id="queryFrame">
								<div class="container-fluid" id="queryCondition"></div>
								<table width="98%" border="0" align="center" cellpadding="0" cellspacing="0" style="margin-left:16px;">
									<tr>
	 									<td>
											<div align="center"> --> -->
												<input type="hidden" name="token" value="${token}" /> 
	 											<button type="button" class="btn btn-primary" id="addCondtion"> 
													onclick="addQueryCondition();" style="margin-left:30px;">添加查询条件</button>
											</div>
	 									</td>
									</tr>
								</table>
						</div>				
			      	</div> --%>
			      	
			      	<div class="tab-pane active" id="tab_1_1_4" >
			      		<div class="form-group">
		                <label for="roleIds" class="col-sm-2 control-label text-right" >分配角色:</label>
		                 <div class="col-sm-5">
		                      <table>
		                          <c:forEach items="${roles}" var="role" varStatus="status">
		                         <c:if test="${(status.index+1)%3 == 1}"><tr></c:if>
		                           <td class="form-control-static">&nbsp;${role.roleName}</td>
		                           <td>&nbsp;&nbsp;</td>
		                          <c:if test="${(status.index+1)%3 == 0}"></tr></c:if>
		                  
		                       </c:forEach>
		                 
		                     </table>
		                 
		                 </div>
		                
			    </div>	
			     	<div class="form-group">
		                <label for="groupIds" class="col-sm-2 control-label text-right" >分配用户组:</label>
		                 <div class="col-sm-5">
		                      <table>
		                          <c:forEach items="${groups}" var="group" varStatus="status">
		                         <c:if test="${(status.index+1)%3 == 1}"><tr></c:if>
		                           <td class="form-control-static">&nbsp;${group.name}</td>
		                           <td>&nbsp;&nbsp;</td>
		                          <c:if test="${(status.index+1)%3 == 0}"></tr></c:if>
		                  
		                       </c:forEach>
		                     </table>
		                 </div>
			    	</div>	
			     </div>
			      	<div class="tab-pane" id="tab_1_1_5"></div>
			      </div>
			      </div>
			    <div class="form-group">
						<label for="description" class="col-sm-4 control-label text-right">描述：</label>
						<div class="col-sm-5">
						     <textarea rows="3"  disabled="disabled" name="description" class="form-control">${command.description}</textarea>
						</div>
				</div>
			      	
					
				  <div class="form-group">
						<div class="col-sm-offset-4 col-sm-5">
			            	<input class="btn btn-primary" type="button" value="返回 " onclick="history.go(-1);"/>
			            </div>
				 </div>
					<form:hidden path="organization" id="organization" />	
					<form:hidden path="resourceDataJson" id="resourceDataJson"/>
			</form:form>   	
  		  </div>
			 </div></div>
    </body>
</html>