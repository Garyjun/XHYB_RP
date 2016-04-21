<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>模板中间的元数据树</title>
<script type="text/javascript"
	src="${path}/appframe/util/accountHeight.js"></script>
<link rel="stylesheet"
	href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" />
<script type="text/javascript"
	src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
<script type="text/javascript">
	var setting = {
		view : {
			fontCss: getFont,
			nameIsHTML: true,
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
	
	function getFont(treeId, node) {
		return node.font ? node.font : {};
	}

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
	
	/* 
	treeId 		String 		目标节点 targetNode所在 zTree 的 treeId，便于用户操控
	treeNodes 	Array(JSON) 被拖拽的节点 JSON数据集合（无论拖拽操作为 复制 还是 移动，treeNodes 都是当前被拖拽节点的数据集合。）
	targetNode 	JSON 		treeNodes被拖拽放开的目标节点 JSON 数据对象。如果拖拽成为根节点，则 targetNode = null
	moveType	String		指定移动到目标节点的相对位置	"inner"：成为子节点，"prev"：成为同级前一个节点，"next"：成为同级后一个节点
	isCopy		Boolean		拖拽节点操作是 复制 或 移动true：复制；false：移动 
	*/
	function zTreeBeforeDrop(treeId, treeNodes, targetNode, moveType) {
		var indexTreeId = treeId;
		var id1 = treeNodes[0].id;
		var id2 = targetNode.id;
		 if(targetNode.pId == dragId){  
             var confirmVal = false;  
             $.ajax({  
                  type: "post", 
                  async : false,
                  url:"${path}/system/metaData/ztreeOrder.action?targetId="+targetNode.id+"&groupId="+dragId+"&delType=2"+"&uri="+uri+"&moveType="+moveType,  
                  success: function(json){  
                        if(json=="success" ){  
                             confirmVal = true;  
                            // $.alert('操作成功!'); 
                             notice("提示信息","操作成功!","3");
                        } else{  
                             //$.alert('亲，操作失败');  
                             notice("提示信息","亲，操作失败！","3");
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
	  /*   var id = treeNode.id
		className = (className === "dark" ? "" : "dark");
		var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		zTree.selectNode(treeNode);
		var flag = confirm("确认删除 节点 -- " + treeNode.name + " 吗？");
		if(flag==true) {
			if(treeNode.level<2) {
				$.ajax({
					type : "post",
					async : false,
					url : "${path}/system/metaData/delMetaDefinition.action?id="+id,
					success : function(data) {
						if(data=="0") {
							flag=false;
						}
					}
				});
			} else if(treeNode.level<3){
				$.ajax({
					type : "post",
					async : false,
					url : "${path}/system/metaData/delDefinition.action?id="+id+"&delType=2",
					success : function(data) {
						if(data=="0") {
							flag=false;
						}
					}
				});
			}
		}
		return flag; */
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
	
	/* var idDefinition="";
	var groupId="";
	var haveEditNode=""; */
	function onClickNode(event, treeId, treeNode) {
		idDefinition = treeNode.id;
		groupId= treeNode.pId;
		  if(treeNode.level<1) {
			event.stopPropagation();
		} else if(treeNode.level<2) {
			$.ajax({
				type : "post",
				async : false,
				url : "${path}/system/metaData/detailMetaDefinition.action?id="+idDefinition,
				success : function(data) {
					var json = eval('(' + data + ')');
					var fieldName = json.fieldName;
					var fieldZhName = json.fieldZhName;
					$("#addChild").hide();
					$("#addDefinitionDiv").hide();
					$("#editDefinitionDiv").hide();
					$("#editAndDetail").show();
					$("#eName").val(fieldZhName);
					$("#eNameAbbr").val(fieldName);
					
				}
			});
		} else if(treeNode.level<3) {
			$("#editAndDetail").hide();
			$("#addChild").hide();
			$("#addDefinitionDiv").hide();
			haveEditNode = treeNode;
			$('#editFrame').attr('src',"${path}/system/metaData/editDefinition.action?id="+idDefinition+"&groupId="+groupId+"&sysResMetadataTypeId="+"${id}"+"&typeName="+"${typeName}"+"&delType=2");
			//$("#editDefinitionDiv").load("${path}/system/metaData/editDefinition.action?id="+idDefinition+"&groupId="+groupId+"&sysResMetadataTypeId="+"${id}"+"&typeName="+"${typeName}"+"&delType=2");
			$("#editDefinitionDiv").show();
		} 
		
	}	
	
	
	 var newCount = 1;
	 var groupTwoId ="";
	 var groupTwoname = "";
	 var id =  "${id}";
	 var name = "${typeName}";
	 var parentToChild = "";
	 var treeObjToChild = "";
	 var idDefinition="";
	 var groupId="";
	function addHoverDom(treeId, treeNode) {
		 idDefinition = treeNode.id;
		 var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		 var sObj = $("#" + treeNode.tId + "_span");
		if (treeNode.level<1) {
			//var sObj = $("#" + treeNode.tId + "_span");
			if (treeNode.editNameFlag|| $("#addBtn_" + treeNode.tId).length > 0){
				return;
			}
			var addStr = "<span class='button add' id='addBtn_" + treeNode.tId+ "' title='添加节点' onfocus='this.blur();'></span>";
			sObj.after(addStr);
			var btn = $("#addBtn_" + treeNode.tId);
			if (btn) {
			  btn.bind("click", function() {
				    $("#addChild").show();
					$("#editAndDetail").hide();
					$("#addDefinitionDiv").hide();
					$("#editDefinitionDiv").hide();
					return false;
				});
			}
				
		 }else if(treeNode.level<2) {
			 groupTwoId = treeNode.id;
			 groupTwoname = treeNode.name;
			 parentToChild = treeNode;
			 //var sObj = $("#" + treeNode.tId + "_span");
				if (treeNode.editNameFlag|| $("#addBtn_" + treeNode.tId).length > 0){
					return;
				}
				var addStr = "<span class='button add' id='addBtn_" + treeNode.tId+ "' title='添加元素' onfocus='this.blur();'></span>";
				sObj.after(addStr);
				var btn = $("#addBtn_" + treeNode.tId);
				if (btn) {
				  btn.bind("click", function() {
					    $("#addChild").hide();
						$("#editAndDetail").hide();
						$("#editDefinitionDiv").hide();
						$('#addFrame').attr('src',"${path}/system/metaData/addSysDefinition.action?groupId="+groupTwoId+"&id="+"${id}"+"&name="+"${typeName}");
						//$("#addDefinitionDiv").load("${path}/system/metaData/addSysDefinition.action");
						$("#addDefinitionDiv").show();
					    return false;
					 
					});
				}
				
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
								url : "${path}/system/metaData/delMetaDefinition.action?id="+treeNode.id+"&groupName="+treeNode.name,
								success : function(data) {
									if(data=="0") {
										$.alert('删除节点异常!');
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
				  
				var editStr = "<span class='button edit' id='editNodeBtn_"
					+ treeNode.tId + "' title='编辑节点' onfocus='this.blur();'></span>";
				sObj.after(editStr);
				var edit = $("#editNodeBtn_" + treeNode.tId);
				if(edit){
					edit.bind("click",function(){
						$.ajax({
							type : "post",
							async : false,
							url : "${path}/system/metaData/detailMetaDefinition.action?id="+treeNode.id,
							success : function(data) {
								var json = eval('(' + data + ')');
								var fieldName = json.fieldName;
								var fieldZhName = json.fieldZhName;
								$("#addChild").hide();
								$("#addDefinitionDiv").hide();
								$("#editDefinitionDiv").hide();
								$("#editAndDetail").show();
								$("#eName").val(fieldZhName);
								$("#eNameAbbr").val(fieldName);
								
							}
						});
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
							url : "${path}/system/metaData/delDefinition.action?id="+treeNode.id+"&delType=2"+"&name="+ treeNode.name,
							success : function(data) {
								if(data=="0") {
									$.alert('删除节点异常!');
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
					$("#editAndDetail").hide();
					$("#addChild").hide();
					$("#addDefinitionDiv").hide();
					haveEditNode = treeNode;
					$('#editFrame').attr('src',"${path}/system/metaData/editDefinition.action?id="+treeNode.id+"&groupId="+treeNode.pId+"&sysResMetadataTypeId="+"${id}"+"&typeName="+"${typeName}"+"&delType=2");
					//$("#editDefinitionDiv").load("${path}/system/metaData/editDefinition.action?id="+idDefinition+"&groupId="+groupId+"&sysResMetadataTypeId="+"${id}"+"&typeName="+"${typeName}"+"&delType=2");
					$("#editDefinitionDiv").show();;
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
			url : "${path}/system/metaData/queryMetaType.action?sysResMetadataTypeId="+"${id}",
			success : function(data) {
				t = eval('(' + data + ')');
				var root = { id:"a${id}", pId:0, name:"${typeName}", open:true};
				t.push(root);
				//字体处理
				if(t != null){
					for (var i = 0; i < t.length; i++) {
						var name = t[i].name;
						if(t[i].allowNull != undefined && t[i].allowNull == 0){ //不允许为空
							name += "<span style='color:red;margin-left:5px;font-weight:bold;'>N</span>";
						}
// 						else if(t[i].allowNull != undefined && t[i].allowNull == 1){
// 							name += "<span style='color:red;margin-left:5px;font-weight:bold;'>Y</span>";
// 						}
						
						if(t[i].duplicateCheck != undefined && t[i].duplicateCheck == "true"){ //是查重项
							name += "<span style='color:blue;margin-left:5px;font-weight:bold;'>Q</span>";
						}
						t[i].name = name;
					}
				}
				
				$.fn.zTree.init($("#treeDemo"), setting, t);
				 var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
				 treeObjToChild = $.fn.zTree.getZTreeObj("treeDemo");
		         var treenode = treeObj.getNodeByParam("id", "${groupId}", null);
		         treeObj.expandNode(treenode, true, true, true);
		         treeObj.selectNode(treenode);
			}
		});
	}
	
	function editNodes() {
		$("#addChild").hide();
		$("#addDefinitionDiv").hide();
		$("#editDefinitionDiv").hide();
		var eName = $("#eName").val();
		eName = encodeURI(encodeURI(eName));
		var eNameAbbr = $("#eNameAbbr").val();
		if(eName=="") {
			$.alert("请填写必填信息后再提交");
		}else {
			$.ajax({
				type : "post",
				async : false,
				url : "${path}/system/metaData/updMetaGroupAction.action?sysResMetadataTypeId="+"${id}"+"&fieldName="+eNameAbbr+"&fieldZhName="+eName+"&id="+idDefinition,
				success : function(data) {
					getTreeValue();
					$("#editAndDetail").hide();
				}
			});
		}
	}
	
	function addNodes() {
		$("#addDefinitionDiv").hide();
		$("#editDefinitionDiv").hide();
		$("#editAndDetail").hide();
		var name = $("#name").val();
		var nameAbbr = $("#nameAbbr").val();
		if(name=="") {
			$.alert("请填写必填信息后再提交");
		}else {
			$.ajax({
				type : "post",
				async : false,
				url : "${path}/system/metaData/addMetaGroupAction.action?sysResMetadataTypeId="+"${id}"+"&fieldName="+nameAbbr+"&fieldZhName="+name,
				success : function(data) {
					getTreeValue();
					$("#name").val("");
					$("#nameAbbr").val("");
					$("#addChild").hide();
				}
			});
		}
	}
	
	function reBack(flag) {
		if(flag==1) {
			$("#addDefinitionDiv").hide();
		}else if (flag==2){
			 $("#editDefinitionDiv").hide();
		}
	}
	
	$(function() {
		$("#addChild").hide();
		$("#editAndDetail").hide();
		 getTreeValue();
	});
	
</script>
</head>
<body style="overflow-x: hidden">
 <div id="fakeFrame" class="container-fluid by-frame-wrap"
		style="height: 100%">
		<div class="form-wrap">
<!-- 		<span style='color:red;margin-left:5px;font-weight:bold;'>Y</span> - 空 -->
		<span style='color:red;margin-left:5px;font-weight:bold;'>N</span> - 非空    <span style='color:blue;margin-left:5px;font-weight:bold;'>Q</span> - 查重
			<div class="row">
				<div  class="col-md-3" style="width: 29%;border:1px solid;border-color:#dddddd;height:700px;overflow:auto;"><!-- style="width: 28%" -->
					<div class="zTreeDemoBackground left">
						<ul id="treeDemo" class="ztree"></ul>
					</div>
				</div>
	 			<div id="addChild" class="col-md-8">
					
						<div class="form-group">
							<label class="col-xs-4 control-label text-right"><span class="required">*</span>名称：</label> 
							<input class="col-xs-5" id="name" type="text"></input>
							<br/>
						</div>
						
						<div class="form-group">
							<label class="col-xs-4 control-label text-right">简称：</label> 
							<input class="col-xs-5" id="nameAbbr" type="text"></input>
							<br/>
						</div>
						<div class="form-group" style="margin-left:410px">
							<input class="btn btn-primary btn-sm" type="button" value="确定" onclick="addNodes()"/>
						</div>
				</div>
				
				<div id="editAndDetail" class="col-md-8">
				
				   <div class="form-group">
							<label class="col-xs-4 control-label text-right"><span class="required">*</span>名称：</label> 
							<input class="col-xs-5" id="eName" type="text"></input>
							<br/>
						</div>
						
						<div class="form-group">
							<label class="col-xs-4 control-label text-right">简称：</label> 
							<input class="col-xs-5" id="eNameAbbr" type="text"></input>
							<br/>
						</div>
						<div class="form-group" style="margin-left:410px">
							<input class="btn btn-primary btn-sm" type="button" value="确定" onclick="editNodes()"/>
						</div>
				</div>
				
				          
				<div id="addDefinitionDiv" class="col-md-8" style="display:none;">
				   <iframe id="addFrame" name="addFrame" width="1000px" height="750px" frameborder="0"></iframe>
			    </div>
			    <div id="editDefinitionDiv" class="col-md-8" style="display:none;">
				   <iframe id="editFrame" name="editFrame" width="1000px" height="750px" frameborder="0"></iframe>
			    </div>
		</div>
	</div>
</div>
</body>
</html>


