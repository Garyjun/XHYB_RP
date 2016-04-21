<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<html>
    <head>
        <title>编辑</title>
        <link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
        <script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>
         
		 <script type="text/javascript">
		 
		 	var waitPanel;
		
	        function edit(act){
				document.form1.action=act; 
				jQuery('#form1').submit();
			}
			jQuery(document).ready(function(){
				$('#form1').validationEngine('attach', {
					relative: true,
					overflownDIV:"#divOverflown",
					promptPosition:"centerRight",
					maxErrorsPerField:1,
					onValidationComplete:function(form,status){
						if(status){
							save();
						}
					}
				});
				
			});
			function save(){
				if(setCheckedNodes()){
					$('#form1').ajaxSubmit({
		 				method: 'post',//方式
		 				success:(function(response){
		 					location.href = "${path}/system/role/roleList.jsp";
		           		})
		 			});
				}
			}

			//权限树
			var treeJsonStr='${treeJson}';
			var zNodes =eval( treeJsonStr ); 
			//去掉首页节点
			zNodes.shift();
			var setting = {
					check: {
						enable: true
					},
					data: {
						simpleData: {
							enable: true,
							idKey: "id",
							pIdKey: "pId",
							rootPId: -1
						}
					}
					
			};
			
			$(document).ready(function(){
				refsh();
			});
			
			function refsh(){
				$.fn.zTree.init($("#privilegeTree"), setting,zNodes);
			}
			
			function setCheckedNodes(){
				var checkedIds='';
				var treeObj = $.fn.zTree.getZTreeObj("privilegeTree");
				var nodes = treeObj.getCheckedNodes(true);
				for (var i=0, l=nodes.length; i < l; i++) {
					var node=nodes[i];
					if(!node.isParent){
						checkedIds=checkedIds+node.id+',';
					}
					
				}
				if(checkedIds==''){
					$.alert('请选择权限!');
					return false;
				}else{
					checkedIds=checkedIds.slice(0,checkedIds.length-1);
				}
				
				$("#privilegeIds").val(checkedIds);
				return true;

			}
			function getChildren(nodes,treeNode){
				nodes.push(treeNode);
				if (treeNode.isParent){
					for(var obj in treeNode.children){
					  getChildren(nodes,treeNode.children[obj]);
	
					}
			   }
			  return nodes;
		   }
			
			function getReomveNodeIds(nodes,treeObj){
				var removeIds="";
				var removeNames="";
				for (var i=0, l=nodes.length; i < l; i++) {
					var node=nodes[i];
					if(node.isParent){
						var allChildChecked=true;
						var children=[];
						children = getChildren(children,node);
						for(var j=0;j<children.length;j++){
							var child=children[j];
							if(child.checked==false){
								allChildChecked=false;
								break;
							}
						}
						if(allChildChecked==false){
							if(removeIds.indexOf(node.id+",")==-1){
								removeIds=removeIds+node.id+",";
								removeNames=removeNames+node.name+",";
							}
						}else{
							for(var j=1;j<children.length;j++){
								var child=children[j];
								if(removeIds.indexOf(child.id+",")==-1)
								{
									removeIds=removeIds+child.id+",";
									removeNames=removeNames+child.name+",";
								}
								
							}
						}
						
					}
					
				}
				
				return removeIds;
			}

			function checkedParent(node,treeObj){
				while(node.getParentNode()!=null){
					treeObj.checkNode(node.getParentNode(), true, false);
					node=node.getParentNode();
				}
			}
			
			//修改
			function formReset(){
				$('#form1')[0].reset();
				refsh();
			}
			
			
			function formReset1(){
				$('#form1')[0].reset();
				var treeObj = $.fn.zTree.getZTreeObj("privilegeTree");
				nodes = treeObj.getCheckedNodes(true);
				for (var i=0, l=nodes.length; i < l; i++) {
					treeObj.checkNode(nodes[i], false, true);
				}  
			}
			
			function returnList(){
				location.href = "${path}/system/role/roleList.jsp";
			}
		</script>
	<style type="text/css">
	div.zTreeBackground {width:290px;height:220px;text-align:left;}

    ul.ztree {margin-top: 10px;border: 1px solid #617775;background: #f0f6e4;width:290px;height:220px;overflow-y:scroll;overflow-x:auto;}
	
	</style>

    </head>
     <body>
      	<div class="form-wrap">
      	<div>
      		<form:form action="${path}/role/add.action" name="form1" id="form1" method="POST" class="form-horizontal">
      		<form:hidden path="id"  />
      		<form:hidden path="privilegeIds"  />
      		<form:hidden path="resIds"  />
      		<form:hidden path="resCodes" id="resCodes" />
      		<form:hidden path="roleKey" id="roleKey" />
      	    <input type="hidden" name="textBook" id="textBook" value="${textBook}" />
      		<input type="hidden" name="token" value="${token}" />
        	<div class="form-group">
		                <label for="roleName" class="col-sm-3 control-label text-right" ><i class="must">*</i>角色名</label>
		                 <div class="col-sm-5">
			                 <c:if test="${command.roleName eq '加工员' or command.roleName eq '总库管理员'}">
			                 	<input type="text" name="roleName" readonly  id="roleName"  class="form-control text-input" value="${command.roleName}"/>
			                 </c:if>
			                 <c:if test="${command.roleName ne '加工员' and command.roleName ne '总库管理员'}">
			                	 <form:input  path="roleName" id="roleName"  class="form-control validate[required,maxSize[20]] text-input" />
			                 </c:if>
		                 </div>  
			</div>	
			
			<div class="form-group">
		                <label for="urls" class="col-sm-3 control-label text-right" >描述</label>
		                  <div class="col-sm-5">
		                 <form:textarea rows="3"  path="description" class="form-control"></form:textarea>
		                 </div>
			</div>	
			<div class="form-group">
		                <label for="urls" class="col-sm-3 control-label text-right" ><i class="must">*</i>状态</label>
		                  <div class="col-sm-5">
		                  	 <c:if test="${command.roleName eq '加工员' or command.roleName eq '总库管理员'}">
			                 	启用
			                 </c:if>
			                 <c:if test="${command.roleName ne '加工员' and command.roleName ne '总库管理员'}">
			                	 <form:select path="status"  id="processName" items="${statusMap}" class="form-control" /> 
			                 </c:if>
		                 </div>
			</div>	
				
			
			<div class="form-group">
				<label for="privilegeTree" class="col-sm-3 control-label text-right" ><i class="must">*</i>权限设置</label>
				<div class="col-sm-offset-4" >
					<ul id="privilegeTree"  class="ztree"></ul>
				</div>				
			</div>
			
			<div class="form-group">
   					 <div class="col-sm-offset-3 col-sm-5" >
   					 <c:if test="${id>-1}">
   					   <button type="button"  class="btn btn-primary" onclick="edit('${path}/role/update.action')">保存</button>
   					 </c:if>
   					  <c:if test="${id==-1}">
   					   <button type="button"  class="btn btn-primary" onclick="edit('${path}/role/add.action')">保存</button>
   					 </c:if>
   					   &nbsp;
   					    <c:if test="${id>-1}">
      						<button type="button" class="btn btn-primary" onclick="formReset();">重置</button>
      					 </c:if>
      					 <c:if test="${id==-1}">
      					 	<button type="button" class="btn btn-primary" onclick="formReset1();">重置</button>
      					 </c:if>
      					  &nbsp;
      					<input type="button" class="btn btn-primary"  value="返回" onclick="returnList();"/>
   					 </div>
  		  </div>
			
        </form:form>   
      	</div>
        </div>
    </body>
</html>