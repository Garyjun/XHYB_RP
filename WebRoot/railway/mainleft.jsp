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
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.exhide-3.5.min.js"></script>
	<script type="text/javascript" src="${path}/system/book/bookUtil.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
		.by-main-page-body-side {
		  background-color: #E5E5E5;
		}
	</style>
	<script type="text/javascript">
	var zTree;
    var demoIframe;
    var zNodes;
    var rootCode;
    var changeVersionTree;
    
    function addHoverDom(treeId, treeNode) {
		var sObj = $("#" + treeNode.tId + "_span");
		var zTree = $.fn.zTree.getZTreeObj("tree");
	
	//教材改版
    	if($("#changeVersionBtn_"+treeNode.tId).length==0&&treeNode.nodeType==0){
    		var changeVersionStr = "<span class='button republish' id='changeVersionBtn_" + treeNode.tId
			+ "' title='教材改版' onfocus='this.blur();'></span>";
    		sObj.after(changeVersionStr);
    		var changeVersion = $("#changeVersionBtn_"+treeNode.tId);
    		if (changeVersion) changeVersion.bind("click",function(){
    			$("#objectId").val(treeNode.objectId);
    			$.get("${path}/system/dataManagement/classification/changeVersionInfoList.action?objectId=" + treeNode.objectId,function(data){
    				var books = eval("("+data+")");
    				var subjects = $("#subjects");
    				var subjectsNodes = books.domains;
    				subjectsNodes.shift();
    				subjectsNodes.shift();
    				subjectsNodes.shift();
    				for(var i=0;i<subjectsNodes.length;i++){
    					subjectsNodes[i].name = books.domains[i].label;
    					if(subjectsNodes[i].nodeType == 2)
    						subjectsNodes[i].isHidden = true;
    				}
    		    	var subjectsSetting = {
    						check: {
    							enable: true
    						},
    		            	view: {
    		                	dblClickExpand: false,
    		                	showLine: true,
    		                	selectedMulti: false
    		            	},
    		            	data: {
    		                	simpleData: {
    		                    	enable:true,
    		                    	idKey: "nodeId",
    		                    	pIdKey: "pid",
    		                    	rootPId: "0"
    		                	}
    		            	}
    		        	};
    		    	changeVersionTree = $.fn.zTree.init(subjects, subjectsSetting, subjectsNodes);
					var root = changeVersionTree.getNodes()[0];
					if(root){
						changeVersionTree.expandNode(root,true,false,true);
					}
        			$("#changeVersion").modal('show');
    			});
    		});
    	}
    	//导入教材版本
    	if ($("#importBtn_" + treeNode.tId).length == 0 && (treeNode.nodeType == 1)) {
			var importStr = "<sec:authorize url='/system/book/importExcel.jsp'><span class='button import' id='importBtn_" + treeNode.tId
				+ "' title='导入' onfocus='this.blur();'></span></sec:authorize>";
			sObj.after(importStr);
			var importBtn = $("#importBtn_" + treeNode.tId);
			if (importBtn)
				importBtn.bind("click",function(){
					var xpath = "";
					if(treeNode.nodeType == 0)
						xpath = treeNode.code;
					else
						xpath = treeNode.xpath + "," + treeNode.code;
					$.openWindow("${path}/system/book/importExcel.jsp?pid=" + treeNode.nodeId
						+ "&id="+treeNode.objectId+"&xpath=" + xpath 
						+ "&bookVersion="+$("#bookVersion").val(), '导入教材', 550, 270);
			
				});
		}
	
		//删除节点
    	if ($("#removeBtn_"+treeNode.tId).length==0){
    		var removeStr = "<sec:authorize url='/system/dataManagement/classification/tbzy/deleteNode.action'><span class='button remove' id='removeBtn_" + treeNode.tId
				+ "' title='删除节点' onfocus='this.blur();'></span></sec:authorize>";
    		sObj.after(removeStr);
        	var del = $("#removeBtn_"+treeNode.tId);
        	if (del) del.bind("click", function(){
        		$.confirm('确定要删除所选数据吗？', function(){
        			$.blockUI({ message: '<h5>正在删除...</h5>' });
            		$.post("${path}/system/dataManagement/classification/tbzy/deleteNode.action",
                			{
                				objectId:treeNode.objectId,
                				domainType:"domainType=0"
                			},
                			function(data){
        		        		zTree.removeNode(treeNode);
        		        		parent.location.href="${path}/system/dataManagement/main.jsp";
                			} 
                	);
        		});
        	});
    	}
		//添加节点
    	if ($("#addBtn_"+treeNode.tId).length==0/* &&treeNode.nodeType!=4 */){
			var addStr = "<sec:authorize url='/system/book/tbzy/getSelectValues.action'><span class='button add' id='addBtn_"
				+ treeNode.tId + "' title='添加节点' onfocus='this.blur();'></span></sec:authorize>";
			sObj.after(addStr);
        	var add = $("#addBtn_"+treeNode.tId);
        	if (add) add.bind("click", function(){
        		var nodeType = getNodeType(treeNode);
        		$("#operation").val("add");
        		$("#fromId").val(treeNode.objectId);
				$("#nodeType").val(nodeType);
				$("#nodeParentId").val(treeNode.nodeId);
				var selectDiv = $("#selectDiv");
				selectDiv.empty();
				$.post("${path}/system/book/tbzy/getSelectValues.action", {
					nodeType : nodeType,
					code : treeNode.code
				}, function(data) {
					alert(data);
					if(data != null && data !=""){
						var jsonArray = eval("(" + data + ")");
						if (!jsonArray)
							return;
						createSelectList(jsonArray, null);
						$('#myModal').modal('show');
					}else{
						return;
					}
				});
				return false;
        	});
    	}
	
		//编辑节点
    	if ($("#editBtn_"+treeNode.tId).length==0&&treeNode.level>0){
			var editStr = "<sec:authorize url='/system/book/tbzy/getSelectValues.action'><span class='button edit' id='editBtn_"
				+ treeNode.tId + "' title='编辑节点' onfocus='this.blur();'></span></sec:authorize>";
			sObj.after(editStr);
			var edit = $("#editBtn_" + treeNode.tId);
			if (edit)
				edit.bind("click",function() {
					$.ajax({
						url:"${path}/system/book/tbzy/hasResource.action",
						async:false,
						data:{"objectId":treeNode.objectId,"domainType":treeNode.domainType},
						success:function(data){
							if(data=="hasResource"){
								$.alert("该节点已关联资源，不可修改！");
								return;
							}
						}
					});
					$("#operation").val("edit");
					$("#fromId").val(treeNode.getParentNode().objectId);
					$("#nodeParentId").val(treeNode.pid);
					$("#nodeNumber").val(treeNode.nodeId);
					$("#nodeType").val(treeNode.nodeType);
					var selectDiv = $("#selectDiv");
					selectDiv.empty();
					$.post("${path}/system/book/tbzy/getSelectValues.action", {
							nodeType : treeNode.nodeType,
							code : treeNode.getParentNode().code
					}, function(data) {
							var jsonArray = eval("(" + data + ")");
							if (!jsonArray)
								return;
							createSelectList(jsonArray, treeNode.code);
							$('#myModal').modal('show');
					});
				});
    	}

		//查看单元
		if ($("#detailBtn_" + treeNode.tId).length == 0 /* && treeNode.nodeType == 4 */) {
			var detailStr = "<sec:authorize url='/system/book/bookDetail.jsp'><span class='button ico_open' id='detailBtn_" + treeNode.tId
				+ "' title='查看目录' onfocus='this.blur();'></span></sec:authorize>";
			sObj.after(detailStr);
			var detail = $("#detailBtn_" + treeNode.tId);
			if (detail)
				detail.bind("click",function() {
					var fromId = treeNode.objectId;
					var version = treeNode.version;
					var pid = treeNode.nodeId;
					var xpath = treeNode.xpath + "," + treeNode.code;
					var pcode = treeNode.code;					
					var nameArray = new Array();
					Array.push(treeNode.name);
					while (treeNode.getParentNode()) {
						treeNode = treeNode.getParentNode();
						nameArray.push(treeNode.name);
					}
					var name = "";
					while (nameArray.length) {
						name += nameArray.pop() + "/";
					}
					name = name.substring(0, name.length - 1);
					location.href = "${path}/system/book/bookDetail.jsp?fromId=" + fromId + "&version=" + version
						+ "&pid=" + pid + "&name=" + encodeURI(encodeURI(name)) + "&pcode=" + pcode
						+ "&xpath=" + xpath;
			});
		}
	} 
     function removeHoverDom(treeId, treeNode) {
	        $("#removeBtn_"+treeNode.tId).unbind().remove();
	        $("#addBtn_"+treeNode.tId).unbind().remove();
	        $("#editBtn_"+treeNode.tId).unbind().remove();
	        $("#detailBtn_" + treeNode.tId).unbind().remove();
	        $("#changeVersionBtn_" + treeNode.tId).unbind().remove();
	        $("#importBtn_" + treeNode.tId).unbind().remove();
	        $("#exportVersionBtn_" + treeNode.tId).unbind().remove();
		};
    
    
    var setting = {
    		async: {
    			enable: true,
				url: getUrl
			},
			view : {
				/* addHoverDom : addHoverDom,
				removeHoverDom : removeHoverDom, */
				dblClickExpand : false,
				showLine : true,
				selectedMulti : false
			},
			data : {
				simpleData : {
					enable : true,
					idKey : "nodeId",
					pIdKey : "pid",
					rootPId : "0"
				}
			},
			callback: {
				onClick: zTreeOnClick	
			}
		};
    
    function getUrl(treeId, treeNode) {
		var param = "";
		if(treeNode == undefined){
			param = '0';
		}else{
			param = treeNode.id;
		}
		return "${path}/system/FLTX/listContent.action?path=" + param+"&type=1";
	}
    
	$(document).ready(function() {
		var t = $("#tree");
		$.get("${path}/system/FLTX/listContent.action?path=0&type=1",
			function(data) {
				var zNodes = eval("("+data+")");
				$.fn.zTree.init(t, setting, zNodes);
			});
		
// 		var t = $("#tree");
// 		$.get("${path}/railway/listRailwayInfo.action", function(data) {
// 			var books = eval("(" + data + ")");
// 			zNodes = books.domains;
// 			for (var i = 0; i < zNodes.length; i++) {
// 				if (i == 0)
// 					rootCode = zNodes[i].code;
// 				zNodes[i]["name"] = zNodes[i].label;
// 				zNodes[i]["uon"] = zNodes[i].objectId;
// 				zNodes[i]["xpath"]=zNodes[i].xpath+","+zNodes[i].code;
// 			}
// 			t = $.fn.zTree.init(t, setting, zNodes);
// 			var root = t.getNodes()[0];
// 			if(root){
// 				t.expandNode(root,true,false,true);
// 			}
// 			demoIframe = $("#testIframe");
// 			demoIframe.bind("load", loadReady);
// 		});
	});
	function loadReady() {
		var bodyH = demoIframe.contents().find("body").get(0).scrollHeight, htmlH = demoIframe
				.contents().find("html").get(0).scrollHeight, maxH = Math
				.max(bodyH, htmlH), minH = Math.min(bodyH, htmlH), h = demoIframe
				.height() >= maxH ? minH : maxH;
		if (h < 400)
			h = 400;
		demoIframe.height(h);
	}
	
	function zTreeOnClick(event, treeId, treeNode) {
	    var url = '${path}/railway/mainCenter.jsp';
	    //var xpath = treeNode.xpath+","+treeNode.code;
	    var xpath = treeNode.code;
	    var nodeName = treeNode.name;
	    var nodeId = treeNode.id;
		if(xpath!=null&&xpath!=undefined){
			xpath = encodeURI(xpath);
		}
		if(nodeName !=null && nodeName!=undefined){
			nodeName = encodeURI(encodeURI(nodeName));
		}
		
		url += '?xpath=' + xpath+'&nodeName=' + nodeName +'&nodeId=' +nodeId;
		$('#work_main').attr('src',url);
	    
	};
	
	</script>
</head>
<body>
	<div class="by-main-page-body-side" id="byMainPageBodySide">
	<div style="overflow-y:auto; width:100%; height:100%;">
			<ul id="tree" class="ztree form-wrap"></ul>
		</div>
	</div>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="mainCenter.jsp"></iframe>
	</div>
</body>
</html>