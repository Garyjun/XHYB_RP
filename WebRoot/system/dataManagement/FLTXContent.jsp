<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>演示主页</title>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>	
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		var treeSetting = {
				async: {
					enable: true,
					url: getUrl
				},				
		        view: {
		            addHoverDom: addHoverDom,
		            removeHoverDom: removeHoverDom,
					expandSpeed: ""
		        },
				data: {
					simpleData: {
						enable: true,
						idKey: "id",
						pIdKey: "pid"
					}
				},
				callback: {
					beforeExpand: beforeExpand,
					onAsyncSuccess: onAsyncSuccess,
					onAsyncError: onAsyncError,
					//onClick: zTreeOnClick
				}					
		};
		
		var dt = new DataGrid();
		$(function(){
			dt.setConditions([]);
	   		dt.setSortName('id');
			dt.setSortOrder('desc');
			dt.setURL('${path}/system/FLTX/queryRelationList.action?centerKey=-1');
			/* dt.setSelectBox('id'); */
			dt.setColums([ {field:'centerName',title:'节点名称',width:fillsize(0.2),sortable:true,align:'center'},
			               {field:'centerTypeName',title:'节点分类',width:fillsize(0.15),sortable:true,align:'center'},
			               {field:'relativeName',title:'引用节点名称',width:fillsize(0.2),sortable:true,align:'center'},
							{field:'relativeType',title:'引用分类',width:fillsize(0.15),sortable:true,align:'center'},
							{field:'opt1',title:'操作',width:fillsize(0.15),align:'center',formatter:$operate}]);
			accountHeight();
			dt.initDt();
			dt.query();
		});
		
	$(document.ready).ready(function(){
		//教材版本列表
		$("#nodeInfo").hide();
		$("#hideList").hide();
		$("#addRelation").hide();
		$.ajax({
			url:"${path}/system/FLTX/listContent.action?type="
						+ $("#treeType").val()+"&path=0",
			success:function(data){
				var content = jQuery.parseJSON(data);
//				if($("#treeType").val()!=1){
					var root = {"id":0,"pid":-1,"name":decodeURIComponent("${param.name}"),"xpath":"0", open:true};
					content.unshift(root);
//				}
				var ztree = $.fn.zTree.init($("#tree"), treeSetting,content);
//				var root = ztree.getNodes()[0];
//				if(root){
//					ztree.expandNode(root,true,false,true);
//				}
			}
		});	
		
		$("#pageTitle").text(decodeURIComponent("${param.name}"));
	});
	
	function getUrl(treeId, treeNode) {
		var param = treeNode.id;
		return "${path}/system/FLTX/listContent.action?path="
				+ param + "&type=" + $("#treeType").val();
	}
	
	function beforeExpand(treeId, treeNode) {
		if (!treeNode.isAjaxing) {
			treeNode.times = 1;
			ajaxGetNodes(treeNode, "refresh");
			return true;
		} else {
			//alert("zTree 正在下载数据中，请稍后展开节点。。。");
			notice("提示信息","zTree 正在下载数据中，请稍后展开节点。。。","3");
			return false;
		}
	}
	
	function ajaxGetNodes(treeNode, reloadType) {
		var zTree = $.fn.zTree.getZTreeObj("tree");
		if (reloadType == "refresh") {
			treeNode.icon = "../../../css/zTreeStyle/img/loading.gif";
			zTree.updateNode(treeNode);
		}
		zTree.reAsyncChildNodes(treeNode, reloadType, true);
	}
	
	function onAsyncSuccess(event, treeId, treeNode, msg) {
		if (!msg || msg.length == 0) {
			return;
		}
		var zTree = $.fn.zTree.getZTreeObj("tree");
		if(treeNode != null){
			treeNode.icon = "";
			zTree.updateNode(treeNode);
			zTree.selectNode(treeNode);
		}
	}
	
	function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
		var zTree = $.fn.zTree.getZTreeObj("tree");
		//alert("异步获取数据出现异常。");
		notice("提示信息","异步获取数据出现异常。","3");
		treeNode.icon = "";
		zTree.updateNode(treeNode);
	}
	
	//当点击树节点的内容，则显示右侧进行编辑
	function zTreeOnClick(event,treeId, treeNode){
		  if (treeNode.level>0){
			$("#name").val(treeNode.name.substring(treeNode.name.indexOf("]")+1));
			$("#code").val(treeNode.code);
			$("#treeNodeId").val(treeNode.id);
			$("#treeNodeName").val(treeNode.name);
			$("#treeNodePid").val(treeNode.pid);
			$("#nodeInfo").show();
			queryForm();
			$("#hideList").show();
			$("#addRelation").show();
		}  
	}
	
	function addHoverDom(treeId, treeNode){
		var zTree = $.fn.zTree.getZTreeObj("tree");
		var sObj = $("#" + treeNode.tId + "_span");
		if ($("#removeNodeBtn_"+treeNode.tId).length==0&&treeNode.level>0){
			var removeStr = "<span class='button remove' id='removeNodeBtn_"
				+ treeNode.tId + "' title='删除节点' onfocus='this.blur();'></span>";
			sObj.after(removeStr);
			var del = $("#removeNodeBtn_"+treeNode.tId);
			if(del){
				del.bind("click",function(){
					$.confirm('确定要删除所选数据吗？', function(){
						$.post("${path}/system/FLTX/deleteNode.action?id="+treeNode.id,
								function(data){
							if(data==0){
								zTree.removeNode(treeNode);
							}else{
								//$.alert("删除出错！");
								notice("提示信息","删除出错！","3");
							}
						});
					});
				});
			}
		}
		
		if ($("#addNodeBtn_"+treeNode.tId).length==0){
			var addNodeStr = "<span class='button add' id='addNodeBtn_" + treeNode.tId+ 
							"' title='添加节点' onfocus='this.blur();'></span>";
			sObj.after(addNodeStr);
			var addNode = $("#addNodeBtn_"+treeNode.tId);
			if (addNode) addNode.bind("click", function(){
				$("#treeNodeId").val("");
				$("#treeNodePid").val(treeNode.id);
				$("#name").val("");
				if($("#treeType").val()=='1'){
					$("#code").val("");
				}else if ($("#treeType").val()=='6'){
					$("#code").val("");
					$("#xcode").val(treeNode.xcode);
				}else{
					$("#code").val(_getRandomString(4));
				}
				$("#xpath").val(treeNode.xpath);
				$("#nodeInfo").show();
				if($("#treeType").val()!='1'){
					$("#bianma").hide();
				}
				$("#hideList").hide();
				$("#addRelation").hide();
			});
		}
		
		//点击编辑按钮进行节点编辑
 		if ($("#editNodeBtn_"+treeNode.tId).length==0&&treeNode.level>0){
 			var editStr = "<span class='button edit' id='editNodeBtn_"
 				+ treeNode.tId + "' title='编辑节点' onfocus='this.blur();'></span>";
 			sObj.after(editStr);
 			var edit = $("#editNodeBtn_" + treeNode.tId);
 			if(edit){
 				edit.bind("click",function(){
 					$("#name").val(treeNode.name.substring(treeNode.name.indexOf("]")+1));
 					$("#code").val(treeNode.code);
 					$("#treeNodeId").val(treeNode.id);
 					$("#treeNodeName").val(treeNode.name);
 					$("#treeNodePid").val(treeNode.pid);
 					$("#nodeInfo").show();
 					if($("#treeType").val()!='1'){
 						$("#bianma").hide();
 					}
 					queryForm();
 					$("#hideList").show();
 					$("#addRelation").show();
 				});
 			}
 		} 
		
		
	}
	
	function removeHoverDom(treeId, treeNode) {
		$("#addNodeBtn_"+treeNode.tId).unbind().remove();
		$("#editNodeBtn_"+treeNode.tId).unbind().remove();
		$("#removeNodeBtn_"+treeNode.tId).unbind().remove();
	}
	
	function closeWindow(){
		$("#treeNodeId").val("");
		$("#treeNodePid").val("");
		$("#xpath").val("");
		$("#xcode").val("");
		$("#nodeInfo").hide();
	}
	
	function saveNode(){
		var t = $.fn.zTree.getZTreeObj("tree");
		var nodes = t.getSelectedNodes();
		var name = $("#name").val();
		var code = "";
		if($("#treeType").val()!='6'){
			code = $("#code").val();
		}else {
			code = $("#name").val();
		}
		var id = $("#treeNodeId").val();
		var pid = $("#treeNodePid").val();
		if(nodes[0].parentTId==null){
			$("#xpath").val("");
		}
		var treeRelationType = $("#treeRelationType").val();
		var treeRelationId = $("#treeRelationId").val();
		if(name.length==0||code.length==0){
			$.alert("请输入完整信息！");
			return;
		}
		var url = "";
		if(id.length==0){
			url += "${path}/system/FLTX/addNode.action";
		}else{
			url += "${path}/system/FLTX/updateNode.action?id="+id;
		}
		$.post(url,{
			name:name,
			code:code,
			pid:pid,
			type:$("#treeType").val(),
			xcode:$("#xcode").val(),
			xpath:$("#xpath").val()
		},function(data){
			var node = jQuery.parseJSON(data);
			if(id.length==0){
				var parentNode = t.getNodesByParam("id",pid)[0];
				node.name = "[" + node.code + "]" + node.name;
				t.addNodes(parentNode, node);
			}else{
				var editNode = t.getNodesByParam("id",node.id)[0];
				editNode.name = "[" + node.code + "]" + node.name;
				editNode.code = node.code;
				t.updateNode(editNode);
			}
			$("#treeNodeId").val("");
			$("#treeNodePid").val("");
			$("#xpath").val("");
			
			//重新异步加载当前选中的第一个节点
			if (nodes.length>0) {
				t.reAsyncChildNodes(nodes[0], "refresh", true);
			}
			
			notice("提示信息","保存成功！！","3");
			
			
			//$("#nodeInfo").hide();
		});
	}
	
	function exportExcel(){
		location.href = "${path}/system/FLTX/downloadExportExcel.action?id="
				+$("#treeType").val()+"&name="+$("#treeName").val();
	}

	
	
	$operate = function(value,rec){
		var opt= "<a class=\"btn hover-red\" href=\"javascript:del('"+rec.id+"','"+rec.centerName+"','"+rec.relativeName+"')\" ><i class=\"fa fa-edit\"></i>删除</a>";
		return opt;
	};
	function del(id,centerName,relativeName) {
		$.ajax({
			type : "post",
			async : false,
			url : "${path}/system/FLTX/delRelation.action?id="+id+"&centerName="+centerName+"&relativeName="+relativeName,
			success : function(data) {
				queryForm();
				//$.alert("删除成功");
				notice("提示信息","删除成功！","3");
			}
		});
	}
	function queryForm() {
		dt.dataGridObj.datagrid({
					url : "${path}/system/FLTX/queryRelationList.action?centerKey="+$("#treeNodeId").val()
		}); 
	} 
	function showRelationTree(){
		$.openWindow("${path}/system/FLTX/showRelationTree.action?type="+"${param.type}"+"&typeName="+"${param.name}"+"&treeId="+$("#treeNodeId").val()+"&treeNodeName="+$("#treeNodeName").val(), "分类树关联", 700, 600);
	}
	function fun(){
		notice("提示信息title","定义消息图标。可定义“skins/icons/”目录下的图标","3");
	}
	function fun2(){
		var d = art.dialog({
		    content: '对话框将在两秒内关闭',
		    time:2,
		    left: '80%',   
	        top: '100%'
		});
		d.show();
	}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
	    <ul class="page-breadcrumb breadcrumb">
	        <li>
	            	分类体系管理
	            <i class="fa fa-angle-right"></i>
	        </li>
	        <li id="pageTitle">
	            <i class="fa fa-angle-right"></i>
	        </li>
	        
<!-- 	        <li style="float:right;" class="col-xs-2">
	        	<span class="glyphicon glyphicon-refresh"></span>
	        	<a href="###" onclick="exportExcel();">导出分类树</a>
	        </li> -->	        	        	        	        	        
    	</ul>
		<div class="row">
			<div class="col-md-6" style="height: 400px;overflow: auto;">
				<ul id="tree" class="ztree form-wrap" ></ul>
			</div>
			
			<div class="col-xs-offset-2 col-md-6" id="nodeInfo" style="margin-left:-30px;">
				<div class="row">
					<div class="form-wrap">
						<label for="name" class="col-xs-3 control-label text-right">节点名称：</label>
						<input type="text" id="name" class="col-xs-6 text-input" value="" />
					</div>
				</div>
				<div class="row" id="bianma">
					<div class="form-wrap">
						<label for="name" class="col-xs-3 control-label text-right">节点编码：</label>
						<input type="text" id="code" class="col-xs-6 text-input" value="" />
					</div>
				</div>
				<div class="row" style="margin-left:2px;margin-bottom:15px">
					<div class="form-wrap">
						<input type="button" class="btn btn-primary col-xs-offset-3" onclick="saveNode()"
							value="保存" /> 
						<!-- <input type="button" class="btn btn-primary"
							onclick="closeWindow()" value="关闭" /> -->
							<input type="button" id="addRelation" class="btn btn-primary" onclick="showRelationTree()"
							value="增加关联"/> 
					
					</div>
				</div>
			</div>
			<div id="hideList" align="center">
				<div class="col-md-6" style="margin-left:-5px;" id="headshow">
					<div class="by-tab">
						<div class="portlet">
							<div class="portlet-title" style="width:100%">
							    <span style="color:red">注：(本节点仅支持与其他任何一种分类树的一个节点关联)</span>
								<div class="caption" style="margin-left: -20px" align="left">
									增加关联
								</div>
							</div>
							
						</div>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width:500px"></div>
			</div>
		</div>
		<input type="hidden" id="treeType" value="${param.type}"/>
		<input type="hidden" id="treeName" value="${param.name}"/>
		<input type="hidden" id="treeRelationType"/>
		<input type="hidden" id="treeRelationId"/>
		<input type="hidden" id="treeNodeId" value="" />
		<input type="hidden" id="treeNodeName" value="" />
		<input type="hidden" id="treeNodePid" value="" />
		<input type="hidden" id="xpath" value="" />
		<input type="hidden" id="xcode" value="" />
	</div>
</body>
</html>