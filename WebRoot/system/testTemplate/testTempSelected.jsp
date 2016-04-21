<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
    <title>列表</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
		$(function(){
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = '${path}/system/testTemplate/list.action';
	   		var _pk = 'id';
	   		var _conditions = ['name'];
	   		var _sortName = 'id';
	   		var _sortOrder = 'desc';
			var _colums = [
			               {field:'name',title:'模板名称',width:60, align:'center' ,sortable:true},
							{field:'description',title:'描述',width:60, align:'center' },
							{field:'opt1',title:'操作',width:fillsize(0.19),align:'center',formatter:$operate}];
	//		accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<sec:authorize url='/system/testTemplate/testTemplateDetail.action'><a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
			opt += "<a class=\"btn hover-red\" href=\"javascript:selectTemp("+rec.id+','+"'"+rec.name+"')\" ><i class=\"fa fa-edit\"></i>选中</a>";
			return opt;
					
		};
		
		/***选中***/
		function selectTemp(ids,name){
			 var win=  top.index_frame.work_main;
			 var treeObj = win.$.fn.zTree.getZTreeObj("directoryTree");
			 var nodes = treeObj.getNodes();
			 var rootNode = nodes[0];
			$.ajax({
				type:"post",
				async:false,
				url:"${path}/system/testTemplate/paperArray.action?paperId="+ids,
				success:function(data){
					data = eval('('+data+')');
					if(data.itemList!=""){
						var itemList = eval('('+data.itemList+')');
						 for(var i=0;i<itemList.length;i++) {
 								//var assetsArray = new Array();
 								var id=i + new Date().getTime();
 								var xpath=rootNode.xpath+','+id;
 								//assetsArray.push(ids);
							 	treeObj.addNodes(rootNode, {nodeId:id,xpath:xpath, pid:rootNode.nodeId, name:itemList[i].testType});
						 }
					}
					
					top.index_frame.work_main.document.getElementById("templateName").value=name;
					top.index_frame.work_main.document.getElementById("templet_id").value=ids;
					$.closeFromInner();
					//win.$.fn.zTree.init($("#directoryTree"), directorySetting, zNodes);
				}
			});

// 			top.index_frame.work_main.document.getElementById("paperId").value=id;
		}
		//详细
		function detail(id){
			$.openWindow("${path}/system/testTemplate/testTemplateDetail.action?id="+id,'试题模板详细页',1000,600);
		}
		function query(){
			$grid.query();
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " >
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="###">系统管理</a></li>
					<li class="active">系统设置</li>
					<li class="active">试卷模板</li>
				</ol>
			</div>			
	<div id="data_div" class="data_div height_remain" style="width: 750px;"></div>
		</div>
	</div>
</body>
</html>