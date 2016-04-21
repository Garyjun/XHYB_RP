<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>用户组管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
		<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>	        
		<script type="text/javascript">
	$(document).ready(function(){
		var _divId = 'data_div';
	   		var _url = '${path}/system/group/list.action';
	   		var _pk = 'id';
	   		var _conditions = ['name'];
	   		var _sortName = 'id';
	   		var _sortOrder = 'desc';
	   		var _check=true;
			var _colums = [ 
							{ title:'名称', field:'name' ,width:100, align:'center',sortable:true },
						    { title:'创建时间', field:'createdTime' ,width:100, align:'center' },
						    { title:'修改时间', field:'modifiedTime' ,width:100, align:'center' }
						];
	   		
			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
		var treeSetting = {
		        view: {
		            addHoverDom: addHoverDom,
		            removeHoverDom: removeHoverDom,
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
		
		$.ajax({
			url:"${path}/system/organization/list.action",
			async:false,
			success:function(data){
				var content = jQuery.parseJSON(data);
				var ztree = $.fn.zTree.init($("#tree"), treeSetting,content);
				var root = ztree.getNodes()[0];
				if(root){
					ztree.expandNode(root,true,false,true);
				}
			}
		});	
	});
	
	function addHoverDom(treeId, treeNode){
		var zTree = $.fn.zTree.getZTreeObj("tree");
		var sObj = $("#" + treeNode.tId + "_span");
		if ($("#removeNodeBtn_"+treeNode.tId).length==0&&treeNode.level>0){
			var removeStr = "<sec:authorize url='/organization/delete.action'><span class='button remove' id='removeNodeBtn_"
			+ treeNode.tId + "' title='删除节点' onfocus='this.blur();'></span></sec:authorize>";
			sObj.after(removeStr);
			var del = $("#removeNodeBtn_"+treeNode.tId);
			if(del){
				del.bind("click",function(){
					$.confirm('确定要删除所选数据吗？', function(){
						$.post("${path}/system/organization/deleteNode.action?id="+treeNode.id,
								function(data){
							if(data==0){
								zTree.removeNode(treeNode);
							}else{
								$.alert("删除出错！");
							}
						});
					});
				});
			}
		}
		
		if ($("#editNodeBtn_"+treeNode.tId).length==0&&treeNode.level>0){
			var editStr = "<sec:authorize url='/organization/upd.action'><span class='button edit' id='editNodeBtn_"
				+ treeNode.tId + "' title='编辑节点' onfocus='this.blur();'></span></sec:authorize>";
			sObj.after(editStr);
			var edit = $("#editNodeBtn_" + treeNode.tId);
			if(edit){
				edit.bind("click",function(){
					$("#name").val(treeNode.name);
					$("#treeNodeId").val(treeNode.id);
					$("#treeNodePid").val(treeNode.pid);
					$("#myModal").modal("show");
				});
			}
		}
		
		if ($("#addNodeBtn_"+treeNode.tId).length==0){
			var addNodeStr = "<sec:authorize url='/organization/add.action'><span class='button add' id='addNodeBtn_" + treeNode.tId+ 
							"' title='添加节点' onfocus='this.blur();'></span></sec:authorize>";
			sObj.after(addNodeStr);
			var addNode = $("#addNodeBtn_"+treeNode.tId);
			if (addNode) addNode.bind("click", function(){
				$("#treeNodeId").val("");
				$("#name").val("");
				$("#treeNodePid").val(treeNode.id);
				$("#xpath").val(treeNode.xpath);
				$("#myModal").modal("show");
			});
		}
	}
	
	function save(){
		var t = $.fn.zTree.getZTreeObj("tree");
		var name = $("#name").val();
		var id = $("#treeNodeId").val();
		var pid = $("#treeNodePid").val();
		var xpath = $("#xpath").val();
		if(name.length==0){
			$.alert("请输入完整信息！");
			return;
		}
		var url = "";
		if(id.length==0){
			url += "${path}/system/organization/addNode.action?xpath="+xpath;
		}else{
			url += "${path}/system/organization/updateNode.action?id="+id;
		}
		$.post(url,{
			name:name,
			pid:pid
		},function(data){
			var node = jQuery.parseJSON(data);
			if(id.length==0){
				var parentNode = t.getNodesByParam("id",pid)[0];
				t.addNodes(parentNode, node);
			}else{
				var editNode = t.getNodesByParam("id",node.id)[0];
				editNode.name = node.name;
				t.updateNode(editNode);
			}
			$("#treeNodeId").val("");
			$("#treeNodePid").val("");
			$("#xpath").val("");
			$('#myModal').modal('hide');
		});
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
		$('#myModal').modal('hide');
	}
		</script>
    </head>
   <body data-spy="scroll" >
     <div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            <a href="###">系统管理</a>
			        </li>
			        <li>
			            <a href="###">部门管理</a>
			        </li>
				</ul>
			</div>
			<div class="panel-body height_remain" id="999">
				<div class="col-sm-5" style="height: 100%;">
					<ul id="tree" class="ztree form-wrap" style="overflow:auto;"></ul>
				</div>
			</div>
		</div>
	</div>
	<div id="myModal" class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" 
			aria-labelledby="mySmallModalLabel" aria-hidden="true">
	  <div class="modal-dialog modal-sm">
	    <div class="modal-content">
	    	<div class="modal-header">
	    		<h4 class="modal-title">机构名称</h4>
	    	</div>
	    	<div class="modal-body">
		    	<input type="text" id="name" class="form-control" value=""/>
		    	<div class="form-group" style="margin-top:10px;">
			    	<button class="btn btn-primary" onclick="save();">保存</button>
			    	<button class="btn btn-primary" onclick="closeWindow();">关闭</button>
		    	</div>
	    	</div>
	    </div>
	  </div>
	</div>	
	<input type="hidden" id="treeNodeId" value="" />
	<input type="hidden" id="treeNodePid" value="" />
	<input type="hidden" id="xpath" value="" />	
	<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
  </body>
</html>
