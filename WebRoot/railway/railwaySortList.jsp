<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/appframe/common.jsp"%>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.exhide-3.5.min.js"></script>
	<script type="text/javascript" src="${path}/system/book/bookUtil.js"></script>
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
	        /* if($("#changeVersionBtn_"+treeNode.tId).length==0&&treeNode.nodeType==0){
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
	        } */
	        //导入教材版本
	     /*    if ($("#importBtn_" + treeNode.tId).length == 0 && (treeNode.nodeType == 1)) {
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
		} */
			
			//删除节点
	        if ($("#removeBtn_"+treeNode.tId).length==0){
	        	 var removeStr = "<sec:authorize url='/system/dataManagement/classification/tbzy/deleteNode.action'><span class='button remove' id='removeBtn_" + treeNode.tId
	    			+ "' title='删除节点' onfocus='this.blur();'></span></sec:authorize>";
	        	sObj.after(removeStr);
	            var del = $("#removeBtn_"+treeNode.tId);
	            if (del) del.bind("click", function(){
	            	$.confirm('确定要删除所选数据吗？', function(){
	            		$.blockUI({ message: '<h5>正在删除...</h5>' });
		            	$.post("${path}/railway/deleteNode.action",
		                		{
		                			objectId:treeNode.objectId,
		                			domainType:"domainType=0"
		                		},
		                		function(data){
		        		        	zTree.removeNode(treeNode);
		        		        	parent.location.href="${path}/railway/railwaySortMain.jsp";
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
					/* $.post("${path}/system/book/tbzy/getSelectValues.action", {
						nodeType : nodeType,
						code : treeNode.code
					}, function(data) {
						alert(data);
						if(data != null && data !=""){
							var jsonArray = eval("(" + data + ")");
							if (!jsonArray)
								return; */
							createSelectList();
							$('#myModal').modal('show');
						/* }else{
							return;
						}
					}); 
					return false;*/
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
						$("#operation").val("edit");
						$("#fromId").val(treeNode.getParentNode().objectId);
						$("#nodeParentId").val(treeNode.pid);
						$("#nodeNumber").val(treeNode.nodeId);
						$("#nodeType").val(treeNode.nodeType);
						var selectDiv = $("#selectDiv");
						selectDiv.empty();  
						createSelectList(treeNode.tId);
						alert(treeNode.tId);
						$('#myModal').modal('show');

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
			view : {
				addHoverDom : addHoverDom,
				removeHoverDom : removeHoverDom,
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
			}
		};

		$(document).ready(function() {
			var t = $("#tree");
			$.get("listRailInfo.action", {
				version : $("#bookVersion").val()
			}, function(data) {
				var books = eval("(" + data + ")");
				var maxVersionNum = 0, tempNum = 0;
				zNodes = books.domains;
				for (var i = 0; i < zNodes.length; i++) {
					if (i == 0){
						rootCode = zNodes[i].code;
						}
					 tempNum = parseInt(zNodes[i].code.substring(1));
						maxVersionNum = tempNum;
					zNodes[i]["name"] = zNodes[i].label;
				}
				$("#lastVersionNum").val(maxVersionNum);
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
			var bodyH = demoIframe.contents().find("body").get(0).scrollHeight, htmlH = demoIframe
					.contents().find("html").get(0).scrollHeight, maxH = Math
					.max(bodyH, htmlH), minH = Math.min(bodyH, htmlH), h = demoIframe
					.height() >= maxH ? minH : maxH;
			if (h < 400)
				h = 400;
			demoIframe.height(h);
		}

	//保存新节点
	 function saveNode() {
		var t = $.fn.zTree.getZTreeObj("tree");
		var node = {};
		var nodeType = $("#nodeType").val();
		//修改节点
		if($("#operation").val()=='edit'){
			//选择的节点
			node = t.getNodesByParam("nodeId", $("#nodeNumber").val())[0];
			var text = "["+$("#nodeLabelid").val()+$("#nodeLabel").val()+"]";
			var check = t.getNodesByParam("name", text, node.getParentNode());
			if(node.name!=text&&check.length>0){
				alert("[" + check[0].name + "]节点已存在，请选择其他节点");
				return;
			}
			node.name = text;
			//node.code = $("input:radio[name='nodeLabel']:checked").val();
		} else if($("#operation").val()=='add'){
			//添加节点
			//选择的节点
			var parentNode = t.getNodesByParam("nodeId", $("#nodeParentId").val())[0];
			var versionNum = $("#lastVersionNum").val();
			var code = "";
			/* var versionNums = "";
			if(versionNum.substring(0,1)=='S'){
				versionNum= versionNum.substring(1);
			}
			//versionNum= versionNums.substring(1);
				code ='S'+ (parseInt(versionNum)+1); */
			node = {
					name : "["+$("#nodeLabelid").val()+"]"+$("#nodeLabel").val(),
					code : "["+$("#nodeLabelid").val()+"]",
					nodeType : $("#nodeType").val(),
					pid : $("#nodeParentId").val(),
					nodeId : _getRandomString(8),
					level: parentNode.level + 1,
					xpath: parentNode.xpath + "," + parentNode.code,
					pcode: parentNode.code,
					version: parentNode.version,
					broaderNames: getBroaderNames(parentNode,$("#nodeLabel").val()),
					domainType:0,
					broaders: [parentNode.objectId]
				};
			$("#lastVersionNum").val(code);
			if(parentNode.nodeType==0){
				node.xpath = parentNode.code;
				node.version = parentNode.objectId;
			}
			 if(!"text"){
				alert("请选择节点信息");
				return;
			}
			var check = t.getNodesByParam("name", node.code+node.name, parentNode);
			if (check.length > 0) {
				alert("[" + node.code+node.name + "]节点已存在，请选择其他节点");
				return;
			} 
		} 
		//提交修改
		var books = getNodeJson(node,0);
		var parentNode = t.getNodesByParam("nodeId", $("#nodeParentId").val())[0];
		//修改教材版本树
		$.post("${path}/railway/updateRail.action?fromId=" + $("#fromId").val(), books,
			function(data){
				var books = eval("(" + data + ")");
				if (books.state != 0) {
					alert("保存失败");
					return;
				}
				if($("#operation").val()=='edit'){
					var treeNode = t.getNodesByParam("nodeId", $("#nodeNumber").val())[0];
					alert("treeNode"+treeNode);
					treeNode.name = $("#nodeLabelid").val()+$("#nodeLabel").val();
					alert("treeNode.name"+treeNode.name);
					t.updateNode(treeNode);
					$('#myModal').modal('hide');
				} else{
					var treeNode = t.getNodesByParam("nodeId", $("#nodeParentId").val())[0];
					node.objectId = books.objectId;
					newNode = t.addNodes(treeNode, node);
					$('#myModal').modal('hide');
				}
		});
	}

		//导入excel文件
		function importExcel() {
				$.openWindow("system/book/importExcel.jsp?version="
						+$("#bookVersion").val(), '导入教材', 450, 200);
		} 
		//创建列表
		 function createSelectList() {
				var div = $("<div class='radio form-wrap'></div>");
				div.appendTo(selectDiv);
				var label = $("<label class='control-label col-xs-6 text-left'style='margin-left: 5px'>节点名称</label>");
				label.appendTo(div);
				var label1 = $("<input type='text' id='nodeLabel' name='nodeLabel'/>");
				label1.appendTo(label);
				var div1 = $("<div class='radio form-wrap'></div>");
				div1.appendTo(selectDiv);
				var text =  $("<label class='control-label col-xs-6 text-left'style='margin-left: 5px'>节点编号</label>");
				text.appendTo(div1);
				var label2 = $("<input type='text' id='nodeLabelid' name='nodeLabelid'/>");
				label2.appendTo(text);
		}
		 
	 	function closeWindow(){
			$("#versionText").modal('hide');
			$('#myModal').modal('hide');
		} 
		
		 function changeVersion(){
			if($("#newVersionName").val().length==0){
				$.alert("请输入教材版本名称！");
				return;
			}
			var nodes =  changeVersionTree.getCheckedNodes(true);
			var books = {};
			var domainStr = JSON.stringify(getChangeVersionNodes(nodes));
			books["domains"] = domainStr;
			books["objectId"] = $("#objectId").val();
			$("#fakeFrame").mask("改版中...");
			$.post("${path}/system/dataManagement/classification/changeVersion.action",books,function(data){
				if(data == "success"){
					$.alert("教材改版成功，请稍候查看信息！");
					$("#changeVersion").modal('hide');
					parent.location.reload();
				}
				else
					$.alert("改版失败！");
			});
		} 
		 function dojob(){
				if(checkJobStatus()=="0"){
					$.confirm('优化分类树会影响系统性能，仅在导入新的教材版本后执行', function(){
						$.get("${path}/system/book/dojob.action?type=0",function(data){
							
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
	        <li style="margin-left:180px;" id="jobTime"></li>
	        <li style="float:right;">
	        	<sec:authorize url="/system/dataManagement/classification/importExcel.jsp">
	        		<span class="glyphicon glyphicon-import"></span>
	        		<a href="###" onclick="dojob();">优化分类树</a>
	        	</sec:authorize>
	        </li>	              	        
    	</ul>
		<div class="col-xs-5" style="height: 100%;">
			<ul id="tree" class="ztree form-wrap" style="overflow:auto;"></ul>
		</div>
		 <div class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" 
			aria-labelledby="mySmallModalLabel" aria-hidden="true" id="myModal">
  			<div class="modal-dialog modal-sm">
  				<div class="modal-content">
  					<div class="form-group" id="selectDiv">
    				</div>
					<div class="form-wrap">
						<button class="btn btn-primary col-xs-offset-1" onclick="saveNode()">确定</button>
						<button class="btn btn-primary" onclick="closeWindow()">关闭</button>																
					</div>
					<label class=""></label>
				</div>
  			</div>
		</div>
		 <div class="modal fade" id="changeVersion" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		        <h4 class="modal-title" id="myModalLabel">教材改版</h4>
		      </div>
		      <div class="modal-body">
		      	<div class="form-group">
			        <div class="row">
			        	<label class="col-xs-offset-2 col-xs-2 control-label">教材版本名称</label>
			        	<div class="col-xs-4">
							<input type="text" id="newVersionName"  class="form-control text-input"/>
						</div>
			        </div>
			    </div>
			    <div class="form-group">    
			        <div class="row">
			        	<label class="col-xs-offset-2 col-xs-2 control-label">不参与改版学科</label>
						<div style="height:150px;width:200px;overflow:auto; float:left;">
						<ul id="subjects" class="ztree" style="background-color:#fff;margin:0 15px 15px;"></ul>
						</div>
			        </div>	
		        </div>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" onclick="changeVersion();">保存</button>
		        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		      </div>
		    </div>
		  </div>
		</div>		
	</div>
	<input id="nodeNumber" type="hidden"/>
	<input id="nodeType" type="hidden"/>
	<input id="nodeParentId" type="hidden"/>
	<input id="fromId" type="hidden"/>
	<input id="objectId" type="hidden"/>
	<input id="operation" type="hidden"/>
	<input type="hidden" id="lastVersionNum" value=""/>
	<input id="bookVersion" type="hidden" value="<%=request.getParameter("version")%>"/>
</body>
</html>