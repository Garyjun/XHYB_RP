<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>

<html>
	<head>
    	<title>设置字段属性范围</title>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
		<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>        
        <script type="text/javascript">
	$(document).ready(function(){
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
		
		$.get("${path}/user/getDataRangePreJson.action?id="+$("#userId").val(),function(data){
			var result = jQuery.parseJSON(data);
			var ztree = $.fn.zTree.init($("#tree"), treeSetting,result.nodeArray);
			$("#dataPreRangeArray").val(JSON.stringify(result.dataPreRangeArray));
			var root = ztree.getNodes()[0];
			if(root){
				ztree.expandNode(root,true,false,true);
			}			
		});
		
		function addHoverDom(treeId, treeNode){
			var zTree = $.fn.zTree.getZTreeObj("tree");
			var sObj = $("#" + treeNode.tId + "_span");
			if ($("#addNodeBtn_"+treeNode.tId).length==0&&treeNode.level==2){
				var addNodeStr = "<span class='button add' id='addNodeBtn_" + treeNode.tId+ 
					"' title='设置属性范围' onfocus='this.blur();'></span>";
				sObj.after(addNodeStr);
				var addNode = $("#addNodeBtn_"+treeNode.tId);
				if (addNode) addNode.bind("click", function(){
					$("#labelName").val(treeNode.enName);
					var div = $("#content");
					
					var label = $("<label for='name' class='col-sm-3'>"
							+ treeNode.name + "</label>");
					label.appendTo(div);
					
					var select = $("<select id='operator' class='col-sm-2 form-control'></select>");
					select.appendTo(div);
					var option1 = $("<option value='=' class='form-control'>=</option>");
					var option2 = $("<option value='>=' class='form-control'>&gt;=</option>");
					var option3 = $("<option value='<=' class='form-control'>&lt;=</option>");
					option1.appendTo(select);
					option2.appendTo(select);
					option3.appendTo(select);
					
					createContent(treeNode);
					$("#myModal").modal("show");
				});
			}
		}
		
		function removeHoverDom(treeId, treeNode){
			$("#addNodeBtn_"+treeNode.tId).unbind().remove();
		}
	});
	
	function closeWindow(){
		$("#content").empty();
		$("#myModal").modal("hide");
	}
	
	function save(){
		var text = "";
		if($("#text").length>0){
			text = $("#text").val();
		}
		if($("input[type='radio']").length>0){
			text = $("input[name='text']:checked").val();
		}
		if($("input[type='checkbox']").length>0){
			var array = $("input[type='checkbox']");
			for(var i=0; i<array.size();i++){
				if(array[i].checked){
					text += array[i].value + ",";
				}
			}
		}
		var conditions = jQuery.parseJSON($("#dataPreRangeArray").val());
		if(!conditions){
			conditions = [];
		}
		var condition = {
			"name": $("#labelName").val(),
			"operator": $("#operator").val(),
			"text": text
		};
		addToArray(condition,conditions);
	}
	
	function addToArray(condition,conditions){
		for(var i=0; i<conditions.length; i++){
			if(condition.name == conditions[i].name){
				conditions.splice(i,1);
			}
		}
		if(condition.text){
			conditions.push(condition);
		}
		$.post("${path}/user/setDataRangePreJson.action",{
			userId: $("#userId").val(),
			conditions:	JSON.stringify(conditions)
		},function(data){
			if(data==-1){
				$.alert("添加错误！");
			}
			$("#content").empty();
			$("#dataPreRangeArray").val(JSON.stringify(conditions));
			$("#myModal").modal("hide");
		});
	}
	
	function createContent(treeNode){
		switch(getTreeNodeType(treeNode)){
		case "text":
			createText(treeNode);
			break;
		case "radio":
			createRadio(treeNode);
			break;
		case "checkbox":
			createCheckBox(treeNode);
			break;
		case "date":
			createDate(treeNode);
			break;
		}
	}
	
	function createText(treeNode){
		var condition = findConditionByName(treeNode.enName);
		var text = $("<input type='text' class='col-sm-offset-1 col-sm-6 form-control' id='text'></input>");
		text.appendTo($("#content"));
		if(condition){
			text.val(condition.text);
		}
	}
	
	function createRadio(treeNode){
		var checkedOption = findConditionByName(treeNode.enName);
		var values = treeNode.valueRange.split(",");
		var border = $("<div class='col-sm-offset-1 col-sm-6'></div>");
		border.appendTo($("#content"));
		for(var i=0; i<values.length; i++){
			var label = $("<label class='radio-inline'>" + values[i] + "</label>");
			label.appendTo(border);
			var radio = $("<input type='radio' name='text' value=" + values[i] + " />");
			radio.appendTo(label);
			if(checkedOption && values[i]==checkedOption.text){
				radio.attr("checked",true);
			}
			if(i!=0&&i%3==0){
				var br = $("<br />");
				br.appendTo(border);
			}
		}
	}
	
	function createCheckBox(treeNode){
		var checkedOption = findConditionByName(treeNode.enName);
		var values = treeNode.valueRange.split(",");
		var border = $("<div class='col-sm-offset-1 col-sm-6'></div>");
		border.appendTo($("#content"));
		for(var i=0; i<values.length; i++){
			var label = $("<label class='checkbox-inline'>" + values[i] + "</label>");
			label.appendTo(border);
			var checkbox = $("<input type='checkbox' name='text' value=" + values[i] + " />");
			checkbox.appendTo(label);
			if(checkedOption && checkedOption.text.indexOf(values[i])!=-1){
				checkbox.attr("checked",true);
			}
			if(i!=0&&i%3==0){
				var br = $("<br />");
				br.appendTo(border);
			}
		}
	}
	
	function createDate(treeNode){
		var condition = findConditionByName(treeNode.enName);
		var date = $("<input class='datepicker' data-date-format='mm/dd/yyyy'>");
		date.appendTo($("#content"));
		$('.datepicker').datepicker();
/* 		if(condition.text){
			$('.datepicker').datepicker('update', condition.text);
		} */
	}
	
	function findConditionByName(name){
		var conditions = jQuery.parseJSON($("#dataPreRangeArray").val());
		if(!conditions){
			conditions = [];
		}		
		for(var i=0; i<conditions.length; i++){
			if(conditions[i].name == name){
				return conditions[i];
			}
		}
		return null;
	}
	
	function getTreeNodeType(node){
		var type = "";
		switch(node.fieldType){
		case 1:
			type = "text";
			break;
		case 2:
			type = "radio";
			break;
		case 3:
			type = "checkbox";
			break;
		case 4:
			type = "radio";
			break;
		case 5:
			type = "text";
			break;
		case 6:
			type = "text";
			break;
		case 7:
			type = "date";
			break;
		case 8:
			type = "text";
			break;
		case 9:
			type = "text";
			break;		
		}
		return type;
	}
        </script>
    </head>
	<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            <a href="###">系统管理</a>
			        </li>
			        <li>
			            <a href="###">用户管理</a>
			        </li>
			        <li>
			            <a href="###">设置字段属性范围</a>
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
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title" id="myModalLabel">设置字段权限范围属性</h4>
				</div>
				<div class="modal-body">
					<div class="row"><div class="form-wrap" id="content"></div></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" onclick="save();">保存</button>
					<button type="button" class="btn btn-default" onclick="closeWindow();">关闭</button>
				</div>
			</div>
		</div>
	</div>	
	<input type="hidden" id="userId" value="${ param.id }">
	<input type="hidden" id="dataPreRangeArray" value="">	
	<input type="hidden" id="labelName" value="">	
    </body>
</html>