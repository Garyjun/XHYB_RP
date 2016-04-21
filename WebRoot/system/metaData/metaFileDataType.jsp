<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>列表</title>
<script type="text/javascript"
	src="${path}/appframe/util/accountHeight.js"></script>
<link rel="stylesheet"
	href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" />
<script type="text/javascript"
	src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
<script type="text/javascript">
	var setting = {
		view : {
			addHoverDom : addHoverDom,
			removeHoverDom : removeHoverDom,
			selectedMulti : false
		},
		/* async: {
			enable: true,
			url: "${path}/system/metaData/queryGroupChild.action",
			autoParam: ["groupId='${groupId}'"]
		},  */
		edit : {
			enable : true,
			drag: { prev: true, next: true,inner: false},
			showRemoveBtn : false,
			showRenameBtn : showRenameBtn
		},
		data : {
			simpleData : {
				enable : true
			}
		},
		callback : {
			beforeDrag : beforeDrag,
			beforeDrop: zTreeBeforeDrop,
			beforeRemove : beforeRemove,
			onClick: onClickNode
		}

	};

	var className = "dark";
	var dragId;
	var uri;
	function beforeDrag(treeId, treeNodes) {
		for (var i=0,l=treeNodes.length; i<l; i++) {  
               dragId = treeNodes[i].pId;
               uri = treeNodes[i].id
          if (treeNodes[i].drag === false) {  
               return false;  
         }  
        }  
         return true;  
	}
	
	function zTreeBeforeDrop(treeId, treeNodes, targetNode, moveType) {
		 if(targetNode.pId == dragId){  
             var confirmVal = false;  
             $.ajax({  
                  type: "post", 
                  async : false,
                  url:"${path}/system/metaData/ztreeOrder.action?targetId="+targetNode.id+"&delType=3"+"&uri="+uri+"&typeId="+"${id}"+"&moveType="+moveType,  
                  success: function(json){  
                        if(json=="success" ){  
                             confirmVal = true;  
                             //$.alert('操作成功!');
                             notice("提示信息","操作成功!","3");
                        } else{  
                             //$.alert('亲，操作失败');
                             notice("提示信息","亲，操作失败!","3");
                        }  
                  }
             });  
            return confirmVal;  
        } else{  
           //$.alert('亲,只能进行同级排序！');
            notice("提示信息","亲,只能进行同级排序！","3");
            return false;  
        }  
    }  
	function beforeRemove(treeId, treeNode) {
	    //var id = treeNode.id-1;
	    //var id = treeNode.id
		className = (className === "dark" ? "" : "dark");
		var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		zTree.selectNode(treeNode);
		/* var flag =confirm("确认删除 节点 -- " + treeNode.name + " 吗？");
		if(flag==true) {
			if(treeNode.level<2) {
				$.ajax({
					type : "post",
					async : false,
					url : "${path}/system/metaData/delDefinition.action?id="+id+"&delType=3",
					success : function(data) {
						if(data=="0") {
							flag=false;
						}
					}
				});
			}
		} */
		return true;
	}
	
	function showRemoveBtn(treeId, treeNode) {
		if (treeNode.pId ==null) {
			return false;
		} else {
			return true;
		}
	}
	function showRenameBtn(treeId, treeNode) {
			return false;
	}
	
	var idDefinition="";
	var groupId="";
	var haveEditNode="";
	function onClickNode(event, treeId, treeNode) {
	 	idDefinition = treeNode.id;
		if(treeNode.level<1) {
			event.stopPropagation();
		} else if(treeNode.level<2) {
			haveEditNode = treeNode;
			$("#addDefinitionDiv").hide();
			$('#editFrame').attr('src',"${path}/system/metaData/editDefinition.action?id="+idDefinition+"&delType=3"+"&sysResMetadataTypeId="+"${id}"+"&typeName="+"${typeName}");
			//$("#editDefinitionDiv").load("${path}/system/metaData/editDefinition.action?id="+idDefinition+"&delType=3"+"&sysResMetadataTypeId="+"${id}"+"&typeName="+"${typeName}");
			$("#editDefinitionDiv").show();
		} 
		
	}	
	
	
	var newCount = 1;
	 var id =  "${id}";
	 var name = "${typeName}";
	 var parentToChild = "";
	 var treeObjToChild ="";
	function addHoverDom(treeId, treeNode) {
		 var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		 var sObj = $("#" + treeNode.tId + "_span");
		if (treeNode.level<1) {
			parentToChild = treeNode;
			//var sObj = $("#" + treeNode.tId + "_span");
			if (treeNode.editNameFlag|| $("#addBtn_" + treeNode.tId).length > 0){
				return;
			}
			var addStr = "<span class='button add' id='addBtn_" + treeNode.tId+ "' title='添加文件元数据' onfocus='this.blur();'></span>";
			sObj.after(addStr);
			var btn = $("#addBtn_" + treeNode.tId);
			if (btn) {
			  btn.bind("click", function() {
				  $("#editDefinitionDiv").hide();
				  $('#addFrame').attr('src',"${path}/system/metaData/addFileDefinitionData.action?id="+"${id}"+"&typeName="+"${typeName}");
				 // $("#addDefinitionDiv").load("${path}/system/metaData/addFileDefinitionData.action?id="+"${id}"+"&typeName="+"${typeName}");
				  $("#addDefinitionDiv").show();
				  return false;
				});
			}
				
		 }else {
			 if (treeNode.editNameFlag|| $("#removeFileBtn_" + treeNode.tId).length > 0){
					return;
			 }
			 var removeFileStr = "<span class='button remove' id='removeFileBtn_" + treeNode.tId+ "' title='删除' onfocus='this.blur();'></span>";
			 sObj.after(removeFileStr);
			 var removeFileBtn = $("#removeFileBtn_"+treeNode.tId);
			  if (removeFileBtn) 
			  removeFileBtn.bind("click", function(){
				  $.confirm("确认删除 节点 -- " + treeNode.name + " 吗？", function(){
					  $.ajax({
							type : "post",
							async : false,
							url : "${path}/system/metaData/delDefinition.action?id="+treeNode.id+"&delType=3"+"&name="+treeNode.name,
							success : function(data) {
								if(data=="0") {
									flag=false;
								}else{
									 zTree.removeNode(treeNode);
								}
							}
						});
				  });
			  });
			  if (treeNode.editNameFlag|| $("#editNodeBtn_" + treeNode.tId).length > 0){
					return;
			 }
			  
			/* var editStr = "<span class='button edit' id='editNodeBtn_"
				+ treeNode.tId + "' title='编辑节点' onfocus='this.blur();'></span>";
			sObj.after(editStr);
			var edit = $("#editNodeBtn_" + treeNode.tId);
			if(edit){
				edit.bind("click",function(){
					haveEditNode = treeNode;
					$("#addDefinitionDiv").hide();
					$('#editFrame').attr('src',"${path}/system/metaData/editDefinition.action?id="+treeNode.id+"&delType=3"+"&sysResMetadataTypeId="+"${id}"+"&typeName="+"${typeName}");
					//$("#editDefinitionDiv").load("${path}/system/metaData/editDefinition.action?id="+idDefinition+"&delType=3"+"&sysResMetadataTypeId="+"${id}"+"&typeName="+"${typeName}");
					$("#editDefinitionDiv").show();
				});
			} */
		 }
	};
	function removeHoverDom(treeId, treeNode) {
		$("#addBtn_" + treeNode.tId).unbind().remove();
		$("#editNodeBtn_"+treeNode.tId).unbind().remove();
		$("#removeFileBtn_"+treeNode.tId).unbind().remove();
	};
	
	function getTreeValue() {
		var t = [];
		$.ajax({
			type : "post",
			async : false,
			url : "${path}/system/metaData/queryFileAllData.action?sysResMetadataTypeId="+"${id}",
			success : function(data) {
				t = eval('(' + data + ')');
				var root = { id:"a${id}", pId:0, name:"${typeName}", open:true};
				t.push(root);
				$.fn.zTree.init($("#treeDemo"), setting, t);
				treeObjToChild = $.fn.zTree.getZTreeObj("treeDemo");
			}
		});
	}
	
	function reBack(flag) {
		if(flag==1) {
			$("#addDefinitionDiv").hide();
		}else if (flag==2){
			 $("#editDefinitionDiv").hide();
		}
	}
	
	$(function() {
		getTreeValue();
	});
	
</script>
</head>
<body style="overflow-x: hidden">
	<div id="fakeFrame" class="container-fluid by-frame-wrap"
		style="height: 100%;">
		<div class="form-wrap">
			<div class="row">
				<div class="col-md-3" style="width: 29%;border:1px solid;border-color:#dddddd;height:700px;overflow:auto;">
					<div class="zTreeDemoBackground left">
						<ul id="treeDemo" class="ztree"></ul>
					</div>
				</div>
				<div id="addDefinitionDiv" class="col-md-8" style="display:none;">
				   <iframe id="addFrame" name="addFrame" width="1000px" height="700px" frameborder="0"></iframe>
			    </div>
			    <div id="editDefinitionDiv" class="col-md-8" style="display:none;">
				   <iframe id="editFrame" name="editFrame" width="1000px" height="700px" frameborder="0"></iframe>
			    </div>
			</div>
		</div>
	</div>
</body>
</html>


