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
				if(setCheckedNodes()){
					jQuery('#form1').submit();
				}
				
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
				waitPanel = $.waitPanel('正在保存...',false);
				$('#form1').ajaxSubmit({
	 				method: 'post',//方式
	 				success:(function(response){
	 					callback(response);
	           		})
	 			});
			}
			function callback(data){
			    top.index_frame.work_main.freshDataTable('data_div');
				//top.index_frame.freshDataTable('data_div');
				waitPanel.close();
				$.closeFromInner();
			}
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
				$.fn.zTree.init($("#privilegeTree"), setting,zNodes);
			});
			
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
					alert('请选择权限!');
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
			
			function setCheckedResNodes(){
				if($("#resTree").length){
					var checkedCodes='';
					var checkedIds='';
					var treeObj = $.fn.zTree.getZTreeObj("resTree");
					var nodes = treeObj.getCheckedNodes(true);
					var reomveNodeIds=getReomveNodeIds(nodes,treeObj);
					var checkedNames='';
					var textBook=$("#textBook").val();
					for (var i=0, l=nodes.length; i < l; i++) {
						var node=nodes[i];
						if(reomveNodeIds.indexOf(node.id+",")!=-1){
							continue;
						}
						var xpath=node.xpath;
						var code=node.code;
						if(xpath!=''){
							code=node.xpath+","+node.code;
						}
						checkedCodes=checkedCodes+code+';';
	                    if(code=='TB'||code=='TB,T00'){
	                    	checkedCodes=checkedCodes+textBook+";";
						}
						checkedNames=checkedNames+node.name+';';
						checkedIds=checkedIds+node.id+',';
					}
					if(checkedCodes!=''){
						checkedCodes=checkedCodes.slice(0,checkedCodes.length-1);
						checkedNames=checkedNames.slice(0,checkedNames.length-1);
						checkedIds=checkedIds.slice(0,checkedIds.length-1);
					}
					$("#resCodes").val(checkedCodes);
					$("#resIds").val(checkedIds);
					return true;
				}

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
      		<form:hidden path="resCodes"  />
      		<form:hidden path="roleKey"  />
      	    <input type="hidden" name="textBook" id="textBook" value="${textBook}" />
      		<input type="hidden" name="token" value="${token}" />
        	<div class="form-group">
		                <label for="roleName" class="col-sm-3 control-label text-right" ><i class="must">*</i>角色名</label>
		                 <div class="col-sm-5">
		                 <form:input  path="roleName" id="roleName"  class="form-control validate[required,maxSize[20]] text-input" />
		                 </div>  
			</div>	
			
			<div class="form-group">
		                <label for="urls" class="col-sm-3 control-label text-right" >描述</label>
		                  <div class="col-sm-5">
		                 <form:textarea rows="3"  path="description" class="form-control validate[required,maxSize[200]]"></form:textarea>
		                 </div>
			</div>	
			<div class="form-group">
		                <label for="urls" class="col-sm-3 control-label text-right" ><i class="must">*</i>状态</label>
		                  <div class="col-sm-5">
		                   <form:select path="status"  id="processName" items="${statusMap}" class="form-control" /> 
		                 </div>
			</div>	
			
			<div class="form-group">
				<label for="privilegeTree" class="col-sm-3 control-label text-right" >权限设置</label>
				<div class="zTreeBackground col-sm-5" >
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
      					<button type="reset" class="btn btn-primary">重置</button>
      					  &nbsp;
      					<input type="button" class="btn btn-primary"  value="关闭 " onclick="javascript:$.closeFromInner();"/>
   					 </div>
  		  </div>
			
        </form:form>   
      	</div>
        </div>
    </body>
</html>