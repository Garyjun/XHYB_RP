<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript">
    var zTree;
    var demoIframe;
    function addHoverDom(treeId, treeNode) {
        var sObj = $("#" + treeNode.tId + "_span");
        if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
        var addStr = "";
        addStr += "<span class='button add' id='addBtn_" + treeNode.tId + "' title='添加节点'></span>";
        if(treeNode.getParentNode())
	        addStr += "<span class='button remove' id='removeBtn_" + treeNode.tId
	                + "' title='添加节点' onfocus='this.blur();'></span>";
        sObj.after(addStr);
        var zTree = $.fn.zTree.getZTreeObj("tree");
        var add = $("#addBtn_"+treeNode.tId);
        if (add) add.bind("click", function(){
            var newNode = {name:"newNode"};
            
			newNode.nodeType = 6;
           	newNode.code = _getRandomString(8);
            newNode.nodeId = _getRandomString(8);
            newNode.version = $("#version").val();
        	//节点（章）
            if(treeNode.level == 0){
				newNode.pid = $("#pid").val();
				newNode.xpath = $("#xpath").val();
				newNode.pcode = $("#pcode").val();
				newNode.broaders = $("#fromId").val();
            }else{
            	newNode.pid = treeNode.nodeId;
            	newNode.xpath = treeNode.xpath + "," + treeNode.code;
            	newNode.pcode = treeNode.code;
	            newNode.broaders = [treeNode.objectId];
            }
            var newNodes = zTree.addNodes(treeNode, newNode);
            zTree.selectNode(newNodes[0]);//选择点
            zTree.setting.callback.onClick(null, newNodes[0].tId, newNodes[0]);//调用事件
            
            $("#nodeNumber").val(newNodes[0].nodeId);
            return false;
        });
        
        var del = $("#removeBtn_"+treeNode.tId);
        if (del) del.bind("click", function(){
        	$.confirm('确定要删除所选数据吗？', function(){
	        	var parent = treeNode.getParentNode();
	        	$.post("deleteNode.action",
	            	{
	            		objectId:treeNode.objectId
	            	},
	            	function(data){
	    		        zTree.removeNode(treeNode);
	    		        if(!parent.isParent){
	    		            //父节点nodetype改为6
	    					parent.nodeType = 6;
	    		            var books = getNodeJson(parent);
	    		        	$.post("createBook.action",books,
	    		                	function(data){
	//    		        		        alert(data);
	    		            });
	    		    }
	            });
        	});
        });
    };

    function removeHoverDom(treeId, treeNode) {
        $("#removeBtn_"+treeNode.tId).unbind().remove();
        $("#addBtn_"+treeNode.tId).unbind().remove();
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
        },
        callback: {
        	onClick: zTreeOnClick
        }
    };

    $(document).ready(function(){
    	var name = $("#name").val();
    	$("#name").val(decodeURIComponent(name));
    	$("#versionTitle").text(decodeURIComponent(name));
    	var t = $("#tree");
		$.get("listBookCatalog.action",
				{
					version : $("#version").val(),
					fromId: $("#fromId").val()
				}, 
				function(data) {
					var books = eval("("+data+")");
					zNodes = books.domains;
					if(zNodes.length>0){
						for(var i=0;i<zNodes.length;i++){
							zNodes[i]["name"] = zNodes[i].label;
						}
						var node = {"name":$("#name").val(),"nodeType":4,"nodeId":$("#pid").val(),"pid":"-1"};
						zNodes.unshift(node);
					}else{
						zNodes = [{"name":$("#name").val(),"nodeType":4,"nodeId":$("#pid").val(),"pid":"-1"}];
					}
					t = $.fn.zTree.init(t, setting, zNodes);
					var root = t.getNodes()[0];
					if(root){
						t.expandNode(root,true,false,true);
					}					
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
    
    //单击节点
    function zTreeOnClick(event, treeId, treeNode){
		$("#nodeLabel").val(treeNode.name);
		$("#code").val(treeNode.code);
		$("#nodeNumber").val(treeNode.nodeId);
		//相关节点-版本
		if(!treeNode.isParent){
			$("#catalogName").text(treeNode.name);
			$("#versionList").empty();
			$("#relativeNode").empty();
			$("<option></option>").text("请选择版本").appendTo($("#versionList"));
			$.get("selectRelative.action?type=version",function(data){
				var books = eval("("+data+")");
				for(var i=0;i<books.domains.length;i++){
					var book = books.domains[i];
					$("<option></option>").val(book.code).text(
							book.label).appendTo($("#versionList"));
				}
			});
			$.get("listRelative.action?objectId="+treeNode.objectId,function(data){
				var books = eval("("+data+")");
				for(var i=0;i<books.domains.length;i++){
					var book = books.domains[i];
					if(book.objectId!=treeNode.objectId){
						var xpath = book.xpathName.split(",");
						//var peroid = xpath.splice(0,1);
						//xpath.splice(1,0,peroid);
						var li = $("<li></li>");
						li.text(xpath.join("/")).appendTo($("#relativeNode"));
						var delButton = $("<span class='button remove glyphicon glyphicon-remove'></span>");
						delButton.appendTo(li);
						delButton.click(function(){
							delRelativeNode(book.objectId);
							$(this).parent().remove();
						});
					}
				}
			});
		}
	}
	//修改节点
	function editNode() {
		var nodeId = $("#nodeNumber").val();
		var t = $.fn.zTree.getZTreeObj("tree");
		var node = t.getNodesByParam("nodeId",nodeId)[0];
		node.name = $("#nodeLabel").val();
		node.broaderNames = getBroaderNames(node);
		t.updateNode(node);
		//提交修改
		var books = getNodeJson(node);
		$.post("createBook.action?fromId="+$("#fromId").val(), books, 
			function(data) {
				var books = eval("("+data+")");
				if(books.state!=0){
					alert("保存失败");
					return;						
				}
				node["objectId"] = books.objectId;
				t.updateNode(node);
				var parent = node.getParentNode();
				if(parent.nodeType==6){
			    	//父节点nodetype改为5
					parent.nodeType = 5;
					var books = getNodeJson(parent);
			        $.post("createBook.action",books,
			        	function(data){
//			        		alert(data);
					});
				}
		});
	}
	
	//生成单个节点json数据
	function getNodeJson(node){
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
				"level" : node.level + 6,
				"domainType" : 0
		};
		if (node.objectId)
			book["objectId"] = node.objectId;
		if (node.version)
			book["version"] = node.version;
		if (node.xpath)
			book["xpath"] = node.xpath;
		if (node.pcode)
			book["pcode"] = node.pcode;
		if (node.broaders)
			book["broaders"] = node.broaders;		
		if (node.broaderNames)
			book["broaderNames"] = node.broaderNames;		
		domains.push(book);
		var domainStr=JSON.stringify(domains);
		books["domains"] = domainStr;
		return books;
	}

	function importCatalogExcel(){
		$.openWindow("system/book/importCatalogExcel.jsp?fromId=" + 
				$("#fromId").val()+"&pid="+$("#pid").val(),'导入教材目录',450,200);
	}
	
	//选择学段
	function changeVersion(){
		var code = $("#versionList").val();
		var selectList = $("#peroidList");
		$("#peroidList option[index!='0']").remove();
		getNodeList(selectList,code,"请选择学段");
	}
	//选择学科
	function changePeroid(){
		var code = $("#versionList").val() + "," + $("#peroidList").val();
		var selectList = $("#subjectList");
		$("#subjectList option[index!='0']").remove();
		getNodeList(selectList,code,"请选择学科");
	}
	//选择年级
	function changeSubject(){
		var code = $("#versionList").val() + "," + $("#peroidList").val()
			+ "," + $("#subjectList").val();
		var selectList = $("#gradeList");
		$("#gradeList option[index!='0']").remove();
		getNodeList(selectList,code,"请选择年级");
	}
	//选择分册
	function changeGrade(){
		var code = $("#versionList").val() + "," + $("#peroidList").val()
		+ "," + $("#subjectList").val() + "," + $("#gradeList").val();
		var selectList = $("#volumeList");
		$("#volumeList option[index!='0']").remove();
		getNodeList(selectList,code,"请选择分册");
	}
	//相关节点树
	function changeVolume(){
		var fromId = $("#relativeId").val();
		$.get("listBookCatalog.action?fromId="+fromId+"&version=",function(data){
			var books = eval("("+data+")");
			var relativeTree = $("#relativeTree");
			var relativeNodes = books.domains;
			for(var i=0;i<relativeNodes.length;i++){
				relativeNodes[i].name = books.domains[i].label;
			}
		    var relativeSetting = {
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
		            },
		            callback: {
		            	onClick: relativeTreeOnClick
		            }
		        };
			$.fn.zTree.init(relativeTree, relativeSetting, relativeNodes);
		});
	}
	//单击相关节点
	function relativeTreeOnClick(event, treeId, treeNode){
		
	}
	//删除相关节点
	function delRelativeNode(relativeId){
		var treeObj = $.fn.zTree.getZTreeObj("tree");
		var node = treeObj.getSelectedNodes()[0];
		$.confirm('确定要删除所选数据吗？', function(){
/* 			$.post("delRelativeNode.action",{
				id:node.objectId,
				relativeId:relativeId
			},function(data){
				if(data!=0)
					alert("删除失败");
			}); */
			$.ajax({
				url:'delRelativeNode.action',
			    type: 'post',
			    data: {"id":node.objectId,"relativeId":relativeId},
			    success: function (data) {
					if(data!=0)
						alert("删除失败");
			    }
			});			
		});
	}
	//添加相关节点
	function createRelation(){
		var treeObj = $.fn.zTree.getZTreeObj("tree");
		var node = treeObj.getSelectedNodes()[0];
		var relativeTree = $.fn.zTree.getZTreeObj("relativeTree");
		var relativeNode = relativeTree.getSelectedNodes()[0];
		$.post("createRelation.action",{
			id:node.objectId,
			relativeId:relativeNode.objectId
		},function (data){
			if(data!=0)
				alert("关联失败");
			var names = [];
			while(relativeNode){
				names.push(relativeNode.name);
				relativeNode = relativeNode.getParentNode();
			}
			names.push($("#volumeList option:selected").text());
			names.push($("#gradeList option:selected").text());
			names.push($("#subjectList option:selected").text());
			names.push($("#peroidList option:selected").text());
			names.push($("#versionList option:selected").text());
			var name = names.reverse().join("/");
			var li = $("<li></li>");
			li.text(name).appendTo($("#relativeNode"));
			var delButton = $("<span class='button remove glyphicon glyphicon-remove'></span>");
			delButton.appendTo(li);
			delButton.click(function(relativeNode){
				delRelativeNode(relativeNode.objectId);
				$(this).parent().remove();
			});
		});
	}
	
	function getNodeList(selectList,code,name){
		selectList.empty();
		$("<option></option>").text(name).appendTo(selectList);
		$.get("selectRelative.action?code="+code,function(data){
			var books = eval("("+data+")");
			for(var i=0;i<books.domains.length;i++){
				var book = books.domains[i];
				$("<option></option>").val(book.code).text(
						book.label).appendTo(selectList);
				if(book.nodeType==4){
					$("#relativeId").val(book.objectId);
				}
			}
		});
	}
	
	function returnVersion(){
		location.href = "${path}/system/book/bookList.jsp?version="+$("#version").val();
	}
	
	function getBroaderNames(node){
		var names = [];
		var name = "";
		if(node.xpathName){
			name = node.xpathName;
		}else{
			names.push(node.name);
			while(node.getParentNode()){
				if(node.getParentNode().label)
					names.push(node.getParentNode().label);
				node = node.getParentNode();
			}
			var preName = $("#name").val();
			name = preName.split("/").join(",") + "," + names.reverse().join(",");
		}
		return name;
	}
	</script>	
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
	    <ul class="page-breadcrumb breadcrumb">
	        <li>
	            <a href="###">系统管理</a>
	            <i class="fa fa-angle-right"></i>
	        </li>
	        <li>
	            <a href="###">同步资源</a>
	            <i class="fa fa-angle-right"></i>
	        </li>
	        <li>
	            <a href="###">教材目录</a>
	        </li>
	        <li style="float:right;">
	        	<a href="###" onclick="returnVersion();">返回教材版本</a>
	        </li>	        	        
    	</ul>
    	<div class="row row-pct-4">
    	<div class="col-xs-6 row-pct-10">
			<div class="panel panel-default row-pct-10" style="overflow:auto;height:442px">
				<div class="panel-heading"  style="color:white;font-weight:bold;">
	    			<span class="title" id="versionTitle"><%=request.getParameter("name")%></span>
	  			</div>
	  			<div class="panel-body">
					<ul id="tree" class="ztree form-wrap"></ul>
				</div>
			</div>
		</div>
		<div class="by-tab col-xs-6">
			<ul class="nav nav-tabs nav-justified">
				<li class="active"><a href="#tab_1_1_1" data-toggle="tab">节点属性</a></li>
				<li><a href="#tab_1_1_2" data-toggle="tab">相关节点</a></li>
			</ul>			
			<div class="tab-content" style="height:400px;">
				<div class="tab-pane active" id="tab_1_1_1">
					<div class="row">
						<div class ="form-wrap">
							<label class="col-xs-2">节点名称：</label>
							<div class="col-xs-6">
								<input id="nodeLabel" type="text" class="form-control"/>
							</div>
						</div>
					</div>
					<input id="code" type="hidden" class="form-control"/>
					<div class="row">
						<div class ="form-wrap">
							<label class="control-label col-xs-2"></label>
							<div class="col-xs-3">
			           			<input id="submit" type="button" value="保存修改" 
			           				class="btn btn-primary" onclick="javascript:editNode();"/> 
			        		</div>
		        		</div>
					</div>				
				</div>
				<div class="tab-pane" id="tab_1_1_2">
				<div class="container-fluid">
					<div class="form-wrap">
						<div class="row">
							<label class="col-xs-2">节点名称：</label>
							<label class="col-xs-2" id="catalogName"></label>
						</div>
						<div class="row">
							<label class="col-xs-2">相关节点：</label>
							<ul class="col-xs-10 list-group" id="relativeNode">
							</ul>
						</div>
					</div>
				</div>
				<div class="container-fluid" style="padding:10px">
					<div class="container-fluid" style="background-color:#eee;height:273px;overflow:auto;">
						<div class="row">
							<div class="form-wrap">
							<div class="col-xs-4">
								<select class="form-control" onchange="changeVersion()" id="versionList">
									<option value="">请选择版本</option>
								</select>
							</div>
							<div class="col-xs-4">
								<select class="form-control" onchange="changePeroid()" id="peroidList">
									<option value="">请选择学段</option>
								</select>
							</div>
							<div class="col-xs-4">
								<select class="form-control" onchange="changeSubject()" id="subjectList">
									<option value="">请选择学科</option>
								</select>
							</div>
							</div>
						</div>
						<div class="row">
							<div class="form-wrap">
							<div class="col-xs-4">
								<select class="form-control" onchange="changeGrade()" id="gradeList">
									<option value="">请选择年级</option>
								</select>
							</div>	
							<div class="col-xs-4">
								<select class="form-control" onchange="changeVolume()" id="volumeList">
									<option value="">请选择分册</option>
								</select>
							</div>	
							</div>																
						</div>
						<div class="row">
							<div class="form-wrap">
								<ul id="relativeTree" class="ztree" style="background-color:#fff;margin:0 15px 15px;"></ul>
							</div>
						</div>
						<div class="form-wrap">
							<button class="btn btn-primary" onclick="createRelation();">添加关联</button>
						</div>
					</div>
				</div>
	            </div>
			</div>
		</div>
		</div>
	</div>
	<input id="nodeNumber" type="hidden"/>
	<input id="fromId" type="hidden" value="<%=request.getParameter("fromId")%>"/>
	<input id="version" type="hidden" value="<%=request.getParameter("version")%>"/>
	<input id="pid" type="hidden" value="<%=request.getParameter("pid")%>"/>
	<input id="name" type="hidden" value="<%=request.getParameter("name")%>"/>
	<input id="pcode" type="hidden" value="<%=request.getParameter("pcode")%>"/>
	<input id="xpath" type="hidden" value="<%=request.getParameter("xpath")%>"/>
	<input id="relativeId" type="hidden"></input>
</body>
</html>