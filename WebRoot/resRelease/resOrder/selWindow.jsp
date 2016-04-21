<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
	<head>
		<title>资源选择策略</title>
		<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
		<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
		<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
		<script type="text/javascript">
			$(function() {
				//部门树
				var fileTree='${fileTree}';
				var zNodes = jQuery.parseJSON(fileTree);
				var setting = {
					check: {
						enable: true
					},
					data: {
						simpleData: {
							enable: true
						}
					}
				};
				$.fn.zTree.init($("#fileTree"), setting,zNodes);
				$("#btnVal").attr("value", "全选");
				checkOperate();
			});
			
			/* function getCheckedNodes(){
				var treeObj = $.fn.zTree.getZTreeObj("fileTree");
				var nodes = treeObj.getCheckedNodes(true);	
				var names = "";
				var len = nodes.length;
				for(var i=0;i<len;i++){
					names += nodes[i].name;
				}
			} */
			
			function checkOperate(){
				$("#btnVal").bind("click",function(){
					var treeObj = $.fn.zTree.getZTreeObj("fileTree");
					var checkedNodes = treeObj.getCheckedNodes(true);	//获得被选中的节点
					var allNodes = treeObj.getNodes();   //获得所有节点
					if(checkedNodes!=null&&allNodes!=null&&checkedNodes.length==getAllChildrenNodes(allNodes)){
						$("#btnVal").attr("value", "全选");
						treeObj.checkAllNodes(false);
					}else{
						$("#btnVal").attr("value", "重置");
						treeObj.checkAllNodes(true);
					}
	            });
			}
			
			
			function getAllChildrenNodes(treeNodes){
				var result = "";
				var len = 0;
				for(var count= 0;count<treeNodes.length;count++){
					var treeNode = treeNodes[count];
					if (treeNode.isParent) {
						var childrenNodes = treeNode.children;
						result += treeNode.name + ",";
						if (!childrenNodes.isParent) {
							for (var i = 0; i < childrenNodes.length; i++) {
								result += childrenNodes[i].name + ",";
							}
						}else{
							getAllChildrenNodes(treeNode);
						}
					}else{
						result += treeNode.name + ",";
					}
					if(result!=null&&result.length>1){
						var arr = result.substring(0,result.length-1).split(",");
						len = arr.length;
					}
				}
				return len;
			}
		</script>
		<style type="text/css">
    		ul.ztree {border: 1px solid #617775;width:100%;height:70%;overflow-x:auto;}
		</style>
	</head>
	<body>
		<div class="form-wrap">
			<div class="zTreeBackground">
				<ul id="fileTree" class="ztree"></ul>
			</div>
			<div class="portlet">
				<table width="98%" border="0" align="center" cellpadding="0"
					cellspacing="0">
					<tr>
						<td><br />
							<div align="left">
								<input type="hidden" name="token" value="${token}" />
								<button type="button" class="btn btn-primary"
									onclick="">跨页全选所有资源</button>
								&nbsp;
								<button type="button" class="btn btn-primary" 
									onclick="">添加当前选中资源</button>
									
								<input type="button" class="btn btn-primary" 
									onclick="" id="btnVal"></input>
							</div> <br />
						</td>
					</tr>
				</table>
			</div>
		</div>
	</body>
</html>