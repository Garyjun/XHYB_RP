<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
	<head>
	<title>可选资源列表</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript">
		$(function(){
			showSelCategory();
		});
		
		function showSelCategory(){
			var metadataTree = '${metadataTree}';
			var zNodes = jQuery.parseJSON(metadataTree);
			var setting = {
				check: {
					//chkboxType: { "Y": "p", "N": "s" },
					enable: true
				},
				data: {
					simpleData: {
						enable: true
					}
				},
				callback: {
					
				},
				view: {
					fontCss: setFontCss
				}
			};
			$.fn.zTree.init($("#metaDataTree"), setting, zNodes);
 		}
		function setFontCss(treeId, treeNode) {
			if(treeNode.level == 0){
				return {color:"red"};
			}else if(treeNode.getParentNode().id == -1){
				return {color:"blue"};
			}
			
			//return treeNode.level == 0 ? {color:"red"} : {};
		};
		
		function returnCheckedNodeIds() {
			 var zTree = $.fn.zTree.getZTreeObj("metaDataTree");
			var nodes = zTree.getCheckedNodes(true);
			var typeids = $('#typeid').val();
			var typeid = new Array();
			typeid = typeids.split(',');
			var arr="";
			var names="";
			for (var i = 0; i < typeid.length; i++) {
				var type = typeid[i];
				/*  var node = zTree.getNodeByParam("pId", type, null);
				alert(node.id);  */
				var paths = "";
				var nodeNames = "【"+zTree.getNodeByParam("id", type, null).name + "】\n";
				var checkCount = 0;
				for(var j = 0; j < nodes.length; j++){
					if(nodes[j].flag!="no"){
						if(nodes[j].getParentNode().getParentNode().id==type){
							paths += nodes[j].id +",";
							nodeNames += nodes[j].name +",";
							checkCount++;
						}
					}
				}
				if(checkCount>0){
					paths=paths.substring(0, paths.length-1);
					arr+="{\""+type+"\":\""+paths+"\"}"+"!";
					nodeNames = nodeNames.substring(0, nodeNames.length-1);
					names += nodeNames+"\n";
				}
				if(checkCount==0){
					$.alert(nodeNames+'未选择元数据项!');
					return;
				}
			}if(arr.length>0){
				arr=arr.substring(0, arr.length-1);
			}
			/* var hx = '';
			var checkhx = 0;
			var nodeNames = "核心元数据：";
			for(var j = 0; j < nodes.length; j++){
				if(nodes[j].flag!="no"){
					if(nodes[j].getParentNode().id=='core_a1'){
						hx += nodes[j].id +",";
						nodeNames += nodes[i].name +",";
						checkhx++;
					}
					
				}
			}
			if(checkhx>0){
				hx=hx.substring(0, hx.length-1);
				arr+="{\"core_a1\":\""+hx+"\"}";
				nodeNames = nodeNames.substring(0, nodeNames.length-1);
				names += "【"+nodeNames+"】";
			} */
			if(nodes == 0){
				if(!$.confirm("暂未选择分类，是否离开?")){
					return false;
				}
			}else{
				$.alert(names);
				var parentWin= "";
				parentWin=  top.index_frame.work_main;
				parentWin.$('#metadatas').val("");
				parentWin.$('#metadatas').val(names);
				parentWin.$('#metaDatasCode').val(arr);
				
			}
			$.closeFromInner();
			/*  var paths = "";
			var nodeNames = "";
			var zTree = $.fn.zTree.getZTreeObj("metaDataTree");
			var nodes = zTree.getCheckedNodes(true);
			var checkCount = 0;
			for(var i = 0; i < nodes.length; i++){
				if(nodes[i].flag!="no"){
					paths += nodes[i].id +",";
					nodeNames += nodes[i].name +",";
					checkCount++;
				}
			}
			if(checkCount>0){
				paths = paths.substr(0,paths.length-1);
				//alert(paths);
				nodeNames = nodeNames.substr(0,nodeNames.length-1);
			}
			if(checkCount == 0){
				if(!confirm("暂未选择分类，是否离开?")){
					return false;
				}
			}else{
				$.alert("您选择了" + checkCount + "个元数据项【" + nodeNames +"】");
				var parentWin= "";
				parentWin=  top.index_frame.work_main;
				parentWin.$('#metadatas').val("");
// 				if(fieldName=="cbclass"){
// 					parentWin.$('#'+fieldName).val(paths);
// 					parentWin.$('#'+fieldName+'Name').val(nodeNames);
// 				}else{
// 					var lineNumber = '${param.lineNumber}';
// 					parentWin.$('#inputValue'+lineNumber).val(nodeNames);
// 					parentWin.$('#inputValueHideCode'+lineNumber).val(paths);
// 				}
				parentWin.$('#metadatas').val(nodeNames);
				parentWin.$('#metaDatasCode').val(paths);
			}
			$.closeFromInner();  */
		}
	</script>
	</head>
	<body>
		<div id="fakeFrame" class="container-fluid by-frame-wrap "
		style="height: 100%;">
		<input type="hidden" id="typeid" name='typeid' value="${typeid }"/>
		<div class="col-sm-5" style="height: 100%;">
			<div class="form-group">
				<div class="col-sm-offset-3 col-sm-6">
					<button type="button" class="btn btn-primary"
						onclick="returnCheckedNodeIds()">确认</button>
					&nbsp; <input class="btn btn-primary" type="button" value="关闭 "
						onclick="javascript:$.closeFromInner();" />
				</div>
			</div>
			<ul id="metaDataTree" class="ztree form-wrap" style="overflow: auto;"></ul>
		</div>
	</div>
	</body>
	</html>