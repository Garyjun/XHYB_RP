<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
	<head>
		<title>高级查询</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
		<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
		<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
		<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>	
		<script type="text/javascript">
		//生成中图分类树
		var treeSetting = {
			view: {
			    dblClickExpand: false,
			    showLine: true,
			    selectedMulti: false
			},
			data: {
				simpleData: {
					enable: true,
					idKey: "id",
					pIdKey: "pid"
				}
			}					
		};
		window.onload = function(){
			//教材版本列表
			$.ajax({
				url:"${path}/system/FLTX/listContent.action?type=1",
				async:false,
				success:function(data){
					var content = jQuery.parseJSON(data);
					var root = {"id":0,"pid":-1,name:"中图分类"};
					content.unshift(root);
					var ztree = $.fn.zTree.init($("#ztflTree"), treeSetting,content);
					var root = ztree.getNodes()[0];
					if(root){
						ztree.expandNode(root,true,false,true);
					} 
				}
			});
		}
		</script>
	</head>
	<body>
		<div id="fakeContainer" class="container-fluid by-frame-wrap">
			<div class="zTreeBackground">
				<ul id="ztflTree" class="ztflTree"></ul>
			</div>
		</div>			
	</body>
</html>