<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
 <link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
 <script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>  
<script type="text/javascript">
	$(function(){
		var orgStr=decodeURIComponent('${param.orgStr}');
		var orgNodes = jQuery.parseJSON(orgStr);
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
	});
	function returnCheckedNodeIds() {
			var paths = ",";
			var nodeNames = "";
			var ids = "";
			var zTree = $.fn.zTree.getZTreeObj("organizationTree");
			var nodes = zTree.getCheckedNodes(true);
			var checkCount = nodes.length;
			for(var i = 1; i < nodes.length; i++){
				paths += nodes[i].id +",-,";
				ids += nodes[i].id +",";
				nodeNames += nodes[i].name +",";
			}
			nodeNames = nodeNames.substring(0,nodeNames.length-1);
			ids = ids.substring(0,ids.length-1);
			parentWin=  top.index_frame.work_main;
			parentWin.$('#orgName').val(nodeNames);
			parentWin.$('#orgId').val(ids);
			$.closeFromInner();
	}
	function checkflag(){
		var orgId='${param.orgId}';
		var zTree = $.fn.zTree.getZTreeObj("organizationTree");
			var nodes = zTree.getSelectedNodes();
			treeNode = nodes[0];
			if(treeNode.children && treeNode.children.length>0){
				for(var i =0;i<treeNode.children.length;i++){
					if(orgId.indexOf(treeNode.children[i].id)>=0){
						treeNode.children[i].checked = true;
					}
				}
			}
	}
</script>
<html>
    <head>
    <title>查看</title>     
    </head>
     <body>
       <div class="form-group">
				<div class="col-sm-offset-3 col-sm-6">
					<button type="button" class="btn btn-primary"
						onclick="returnCheckedNodeIds()">确认</button>
					&nbsp; <input class="btn btn-primary" type="button" value="关闭 "
						onclick="javascript:$.closeFromInner();" />
				</div>
	   </div>

      	<div class="form-group">
			<div class="col-sm-9">
				<div class="zTreeBackground" >
					<ul id="organizationTree"  class="ztree"></ul>
				</div>
			</div>
   		</div>
   		
    </body>
</html>