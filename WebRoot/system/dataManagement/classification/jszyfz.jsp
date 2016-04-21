<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/system/book/bookUtil.js"></script>
	<script>
	function importExcel(){
		$.openWindow("${path}/system/dataManagement/classification/importClassicExcel.jsp?moudleName=jszyfz",'导入分类体系',450,200);
	}
	
    var zTree;
    var demoIframe;
    var zNodes;
    function addHoverDom(treeId, treeNode) {
        var sObj = $("#" + treeNode.tId + "_span");
        var zTree = $.fn.zTree.getZTreeObj("tree");
        
        if ($("#removeBtn_"+treeNode.tId).length==0){
        	var removeStr = "<sec:authorize url='/system/dataManagement/classification/jszyfz/deleteNode.action'><span class='button remove' id='removeBtn_" + treeNode.tId
    			+ "' title='删除节点' onfocus='this.blur();'></span></sec:authorize>";
        	sObj.after(removeStr);
            var del = $("#removeBtn_"+treeNode.tId);
            if (del) del.bind("click", function(){
            	$.confirm('确定要删除所选数据吗？', function(){
	            	$.post("tzzy/deleteNode.action",
	                		{
	                			objectId:treeNode.objectId,
	                			domainType:"domainType=1"
	                		},
	                		function(data){
	        		        	zTree.removeNode(treeNode);
	                		} 
	                );
            	});
            });
        }
        
        
        if ($("#addBtn_"+treeNode.tId).length==0){
			var addStr = "<sec:authorize url='/system/book/jszyfz/getSelectValues.action'><span class='button add' id='addBtn_"
				+ treeNode.tId + "' title='添加节点' onfocus='this.blur();'></span></sec:authorize>";
			sObj.after(addStr);
	        var add = $("#addBtn_"+treeNode.tId);
	        if (add) add.bind("click", function(){
	        	$("#operation").val("add");
	        	var nodeType = 6;
	        	$("#fromId").val(treeNode.objectId);
				$("#nodeType").val(nodeType);
				$("#nodeParentId").val(treeNode.nodeId);
				$("#labelName").val("");
				$("#versionText").modal('show');
	        });
        }
        
        if ($("#editBtn_"+treeNode.tId).length==0&&treeNode.level>0){
			var editStr = "<span class='button edit' id='editBtn_"
				+ treeNode.tId + "' title='编辑节点' onfocus='this.blur();'></span>";
			sObj.after(editStr);
			var edit = $("#editBtn_" + treeNode.tId);
			if (edit)
				edit.bind("click",function() {
					$("#operation").val("edit");
					$("#fromId").val(treeNode.getParentNode().objectId);
					$("#nodeParentId").val(treeNode.pid);
					$("#nodeNumber").val(treeNode.nodeId);
					$("#labelName").val("");
					$("#versionText").modal('show');
				});
        }
    };

    function removeHoverDom(treeId, treeNode) {
        $("#removeBtn_"+treeNode.tId).unbind().remove();
        $("#addBtn_"+treeNode.tId).unbind().remove();
        $("#editBtn_"+treeNode.tId).unbind().remove();
    };

    var setting = {
        view: {
            addHoverDom: addHoverDom,
            removeHoverDom: removeHoverDom,
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

    $(document).ready(function(){
        var t = $("#tree");
		$.get("listClassification.action?moudleName=JS",
			function(data) {
				var books = eval("("+data+")");
				zNodes = books.domains;
				for(var i=0;i<zNodes.length;i++){
					zNodes[i]["name"] = zNodes[i].label;
				}
				t = $.fn.zTree.init(t, setting, zNodes);
				var root = t.getNodes()[0];
				if(root){
					t.expandNode(root,true,false,true);
				}
			    demoIframe = $("#testIframe");
			    demoIframe.bind("load", loadReady);
		});
    });

    function loadReady() {
        var bodyH = demoIframe.contents().find("body").get(0).scrollHeight,
                htmlH = demoIframe.contents().find("html").get(0).scrollHeight,
                maxH = Math.max(bodyH, htmlH), minH = Math.min(bodyH, htmlH),
                h = demoIframe.height() >= maxH ? minH:maxH ;
        if (h < 400) h = 400;
        demoIframe.height(h);
    }

	//生成单个节点json数据
	function getNodeJson(node) {
		var books = {
				"@type" : "domainList",
				"domainType" : 0
			};
			var domains = [];
			var book = {
				"label" : node.name,
				"nodeId" : node.nodeId,
				"nodeType" : node.nodeType,
				"pid" : node.pid,
				"code" : node.code,
				"level" : node.level
			};
			if (node.objectId)
				book["objectId"] = node.objectId;
			if (node.xpath)
				book["xpath"] = node.xpath;
			if (node.pcode)
				book["pcode"] = node.pcode;
			if (node.domainType === 1)
				book["domainType"] = node.domainType;
			if (node.broaderNames)
				book["broaderNames"] = node.broaderNames;
			if (node.broaders)
				book["broaders"] = node.broaders;
			domains.push(book);
			var domainStr = JSON.stringify(domains);
			books["domains"] = domainStr;
			return books;
	}
	
	
	//保存新节点
	function saveNode() {
		if($("#labelName").val().length==0){
			alert("请输入节点名称");
			return;
		}
		var t = $.fn.zTree.getZTreeObj("tree");
		var node = {};
		var parentNode = t.getNodesByParam("nodeId", $("#nodeParentId").val())[0];
		//修改节点
		if($("#operation").val()=='edit'){
			node = t.getNodesByParam("nodeId", $("#nodeNumber").val())[0];
			node.name = $("#labelName").val();
		}else{
			//添加节点
			//输入的节点
			node = {
					name : $("#labelName").val(),
					code : _getRandomString(8),
					nodeType : 6,
					pid : $("#nodeParentId").val(),
					nodeId : _getRandomString(8),
					level: parentNode.level + 1,
					pcode: parentNode.code,
					broaderNames: getBroaderNames(parentNode,$("#labelName").val()),
					domainType:1,
					broaders: [parentNode.objectId]
				};
			if(parentNode.nodeType === 7)
				node.xpath = parentNode.code;
			else
				node.xpath = parentNode.xpath + "," + parentNode.code;
		}
		//提交修改
		var books = getNodeJson(node);
		$.post("createClassification.action?fromId=" + $("#fromId").val()+"&type=classification", books,
				function(data) {
					var books = eval("(" + data + ")");
					if (books.state != 0) {
						alert("保存失败");
						return;
					}
					if($("#operation").val()=='edit'){
						var treeNode = t.getNodesByParam("nodeId", $("#nodeNumber").val())[0];
						treeNode.name = $("#labelName").val();
						t.updateNode(treeNode);
						$("#versionText").modal('hide');
					}else{
						var treeNode = t.getNodesByParam("nodeId", $(
									"#nodeParentId").val())[0];
						node.objectId = books.objectId;
						newNode = t.addNodes(treeNode, node);
						$("#versionText").modal('hide');
					}
				});
	}
	
	function getBroaderNames(parentNode,name){
		var names = [];
		names.push(name);
		while(parentNode){
			if(parentNode.nodeType==0){
				names.push(parentNode.label);
				break;
			}else{
				names.push(parentNode.label);
			}
			parentNode = parentNode.getParentNode();
		}
		return names.reverse().join(",");
	}
	
	function dojob(){
		if(checkJobStatus()=="0"){
			$.confirm('优化分类树会影响系统性能，仅在导入新的教材版本后执行', function(){
				$.get("${path}/system/book/dojob.action?type=1",function(data){
					
				});
			});
		}else{
			$.alert("正在建立级联及相关关系，请稍后！");
		}
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
	        <li>
	            	资源模块
	            <i class="fa fa-angle-right"></i>
	        </li>
	        <li>
	          		  教师专业发展
	            <i class="fa fa-angle-right"></i>
	        </li>	        
	        <li style="float:right;">
	        	<sec:authorize url="/system/dataManagement/classification/importExcel.jsp">
	        		<span class="glyphicon glyphicon-import"></span>
	        		<a href="###" onclick="importExcel();">批量导入</a>
	        	</sec:authorize>
	        </li>	
	        <li style="float:right;" class="col-xs-2">
	        	<span class="glyphicon glyphicon-refresh"></span>
	        	<a href="###" onclick="dojob();">优化分类树</a>
	        </li>	                
    	</ul>
		<div class="col-sm-5" style="height: 100%;">
			<ul id="tree" class="ztree form-wrap" style="overflow:auto;"></ul>
		</div>
		<div class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" 
			aria-labelledby="mySmallModalLabel" aria-hidden="true" id="versionText">
  			<div class="modal-dialog modal-sm">  			
  				<div class="modal-content"> 
  					<div class="modal-body">				
	  					<div class="col-xs-9">
							<input id="labelName" type="text" class="form-control" placeholder="输入名称"/>
						</div>						
						<div>
							<button class="btn btn-primary" onclick="saveNode()">确定</button>															
						</div>
					</div>
				</div>
  			</div>
		</div>
	</div>
	<input id="nodeNumber" type="hidden"/>
	<input id="nodeType" type="hidden"/>
	<input id="nodeParentId" type="hidden"/>
	<input id="fromId" type="hidden"/>
	<input id="operation" type="hidden"/>
	<input id="moudleName" type="hidden" value="JS"/>
</body>
</html>