<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<html>
    <head>
        <title>查看</title>
        <link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
        <script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>
		<SCRIPT type="text/javascript">
		function getChildren(nodes,treeNode){
			nodes.push(treeNode);
			if (treeNode.isParent){
				for(var obj in treeNode.children){
				  getChildren(nodes,treeNode.children[obj]);

				}
		   }
		  return nodes;
	    }
		function getParent(node,allCheckedIds){
			while(node.getParentNode()!=null){
				if(allCheckedIds.indexOf(node.getParentNode().id+",")==-1){
					allCheckedIds=allCheckedIds+node.getParentNode().id+",";
				}
				node=node.getParentNode();
			}
			return allCheckedIds;
			
		}
		var setting = {
				data: {
					simpleData: {
						enable: true
					}
				}
			};
		var treeJsonStr='${treeJson}';
		var zNodes =eval( treeJsonStr ); 
		var chooseNodes = [];
		for(var i=0; i<zNodes.length; i++){
			if(zNodes[i].checked){
				chooseNodes.push(zNodes[i]);
			}
		}
		for(var j=0; j<chooseNodes.length;j++){
			addParents(chooseNodes[j]);
		}
		
		function addParents(node){
			var parentNode = getParent(node);
			while(parentNode&&!containsNode(parentNode)){
				chooseNodes.push(parentNode);
				parentNode = getParent(parentNode);
			}
		}
		
		function getParent(node){
			for(var i=0; i<zNodes.length; i++){
				if(node.pId==zNodes[i].id)
					return zNodes[i];
			}
			return null;
		}
		
		function containsNode(node){
			for(var i=0; i<chooseNodes.length; i++){
				if(node.id==chooseNodes[i].id)
					return true;
			}
			return false;
		}
		var allCheckedIds='';

		$(document).ready(function(){
			$.fn.zTree.init($("#privilegeTree"), setting, chooseNodes);
		});
	</SCRIPT>
	<style type="text/css">
	div.zTreeBackground {width:270px;height:260px;text-align:left;}

    ul.ztree {margin-top: 10px;border: 1px solid #617775;background: #f0f6e4;width:270px;height:260px;overflow-y:scroll;overflow-x:auto;}
	
	</style>

    </head>
     <body>
      	<div class="form-wrap">
      	<div>
      		<form action="" name="form1" method="POST" class="form-horizontal">
      		<input type="hidden" name="resIds" id="resIds" value="${command.resIds}" />
      		<div class="form-group">
		                <label for="roleName" class="col-sm-4 control-label text-right" >角色名</label>
		                 <div class="col-sm-5">
		                  <p class="form-control-static">
		                      ${command.roleName}
		                  </p>
		                 </div>  
			</div>
			<div class="form-group">
		                <label for="roleName" class="col-sm-4 control-label text-right" >状态</label>
		                 <div class="col-sm-5">
		                  <p class="form-control-static">
		                     ${command.statusDesc} 
		                  </p>
		                 </div>  
		                 
			</div>	
			                               
			<div class="form-group">
		                <label for="urls" class="col-sm-4 control-label text-right" >描述</label>
		                  <div class="col-sm-5">
		                  <p class="form-control-static"
								style="white-space:nowrap;width:100px;overflow:hidden;text-overflow:ellipsis;" title="${command.description}">${command.description}</p>
		                 </div>
		                 
			</div>	
			
			 <div class="form-group">
		                 
		                  <label for="privilegeTree" class="col-sm-4 control-label text-right" >权限设置</label>
							<div class="form-group">
							             <label for="privilegeTree" class="col-sm-4 control-label text-right" ></label>
							             <div class="tab-content col-sm-5">
						                         <div class="tab-pane active" id="tab_1_1_1" >
						                            <div class="zTreeBackground" >
							                           <ul id="privilegeTree"  class="ztree"></ul>
							                        </div>
						                         </div>
						                 </div>
						    </div>
		                 
			</div>	
		  <div class="form-group">
   					 <div class="col-sm-offset-4 col-sm-5" align="center">
      					<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
   					 </div>
  		  </div>
        	  
        </form>   
      	</div>
        
        
        </div>
    </body>
</html>