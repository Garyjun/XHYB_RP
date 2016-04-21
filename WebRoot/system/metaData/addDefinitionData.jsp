<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
<script type="text/javascript">
    var uid = "${uuid}";
	function save() {
		var typeName = encodeURI(encodeURI(parent.name));
		var groupName = encodeURI(encodeURI(parent.groupTwoname));
		if("metaDefinition"=="${flag}") {
			if("6"==$("#fieldType").val()){
				$("#form").attr(
						"action",
						"${path}/system/metaData/addSysDefinitionAction.action?typeId="
								+ parent.id + "&groupId=" + parent.groupTwoId + "&typeName="
								+ typeName + "&groupName=" +groupName+"&valueRange="+$("#valueRangeSelect").val()+"&flag=metaDefinition");
			}else if("9"==$("#fieldType").val()){
				$("#form").attr(
						"action",
						"${path}/system/metaData/addSysDefinitionAction.action?typeId="
								+ parent.id + "&groupId=" + parent.groupTwoId + "&typeName="
								+ typeName + "&groupName=" +groupName+"&valueRange="+$("#dictName").val()+"&flag=metaDefinition");
			}else if("7"==$("#fieldType").val()){
				$("#form").attr(
						"action",
						"${path}/system/metaData/addSysDefinitionAction.action?typeId="
								+ parent.id + "&groupId=" + parent.groupTwoId + "&typeName="
								+ typeName + "&groupName=" +groupName+"&valueRange="+$("#dateFormat").val()+"&flag=metaDefinition");
			}else if("2"==$("#fieldType").val()||"3"==$("#fieldType").val()||"4"==$("#fieldType").val()){
// 				$("#form").attr(
// 						"action",
// 						"${path}/system/metaData/addSysDefinitionAction.action?typeId="
// 								+ parent.id + "&groupId=" + parent.groupTwoId + "&typeName="
// 								+ typeName + "&groupName=" +groupName+"&valueRange="+$("#textareaRange").val()+"&flag=metaDefinition");
				$("#form").attr(
						"action",
						"${path}/system/metaData/addSysDefinitionAction.action?typeId="
								+ parent.id + "&groupId=" + parent.groupTwoId + "&typeName="
								+ typeName + "&groupName=" +groupName+"&valueRange="+$("#dictName").val()+"&flag=metaDefinition");
			}else{
				$("#form").attr(
						"action",
						"${path}/system/metaData/addSysDefinitionAction.action?typeId="
								+ parent.id + "&groupId=" + parent.groupTwoId + "&typeName="
								+ typeName + "&groupName=" + groupName+"&valueRange="+$("#valueRange").val()+"&flag=metaDefinition");
			}
		}else if("centerMeta"=="${flag}"){
			if("6"==$("#fieldType").val()){
				$("#form").attr("action",
				"${path}/system/metaData/addCenterMetaDataAction.action?valueRange="+$("#valueRangeSelect").val()+"&flag=centerMeta");
			}else if("9"==$("#fieldType").val()){
				$("#form").attr("action",
						"${path}/system/metaData/addCenterMetaDataAction.action?valueRange="+$("#dictName").val()+"&flag=centerMeta");
			}else if("7"==$("#fieldType").val()){
				$("#form").attr("action",
						"${path}/system/metaData/addCenterMetaDataAction.action?valueRange="+$("#dateFormat").val()+"&flag=centerMeta");
			}else if("2"==$("#fieldType").val()||"3"==$("#fieldType").val()||"4"==$("#fieldType").val()){
// 				$("#form").attr("action",
// 						"${path}/system/metaData/addCenterMetaDataAction.action?valueRange="+$("#textareaRange").val()+"&flag=centerMeta");
				$("#form").attr("action",
						"${path}/system/metaData/addCenterMetaDataAction.action?valueRange="+$("#dictName").val()+"&flag=centerMeta");
			}else{
				$("#form").attr("action",
						"${path}/system/metaData/addCenterMetaDataAction.action?valueRange="+$("#valueRange").val()+"&flag=centerMeta");
			}
		}else if("addFileMetaData"=="${flag}"){
			if("6"==$("#fieldType").val()){
				$("#form").attr("action",
				"${path}/system/metaData/addFileDefinitionDataAction.action?valueRange="+$("#valueRangeSelect").val()+"&typeId="+parent.id+"&typeName="+typeName+"&flag=addFileMetaData");
			}else if("9"==$("#fieldType").val()){
				$("#form").attr("action",
						"${path}/system/metaData/addFileDefinitionDataAction.action?valueRange="+$("#dictName").val()+"&typeId="+parent.id+"&typeName="+typeName+"&flag=addFileMetaData");
			}else if("7"==$("#fieldType").val()){
				$("#form").attr("action",
						"${path}/system/metaData/addFileDefinitionDataAction.action?valueRange="+$("#dateFormat").val()+"&typeId="+parent.id+"&typeName="+typeName+"&flag=addFileMetaData");
			}else if("2"==$("#fieldType").val()||"3"==$("#fieldType").val()||"4"==$("#fieldType").val()){
				$("#form").attr("action",
						"${path}/system/metaData/addFileDefinitionDataAction.action?valueRange="+$("#textareaRange").val()+"&typeId="+parent.id+"&typeName="+typeName+"&flag=addFileMetaData");
			}else{
				$("#form").attr("action",
						"${path}/system/metaData/addFileDefinitionDataAction.action?valueRange="+$("#valueRange").val()+"&typeId="+parent.id+"&typeName="+typeName+"&flag=addFileMetaData");
			}
		}
		$("#form").submit();
	}
    
	function checkSave() {
    	var fieldZhName = $("#fieldZhName").val();
    	var fieldName = $("#fieldName").val();
    	if(fieldZhName==""||fieldName=="") {
    		$.alert("请填写必填信息后再提交");
    	}else{
    		save();
    		//$.alert("元数据保存成功!");
    		
    		/* var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
			var nodes = treeObj.getSelectedNodes();
			//重新异步加载当前选中的第一个节点
			if (nodes.length>0) {
				t.reAsyncChildNodes(nodes[0], "refresh", true);
			} */
    		notice("提示信息","元数据保存成功!","3");
    	}
    }
	
	function addTreeValue(uid) {
		var treeObj = parent.treeObjToChild;
		var treeNode = parent.parentToChild;
        treeObj.addNodes(treeNode, {id:uid,pId:"${groupId}",name:"${treeName}"});
	}
	
	function addCenterTreeValue(uid) {
		var treeObj = parent.treeObjToChild;
		var treeNode = parent.parentToChild;
		treeObj.addNodes(treeNode, {id:uid,pId:"a1",name:"${treeCenterName}"});
	}
	function addFileTreeValue(uid) {
		var treeObj = parent.treeObjToChild;
		var treeNode = parent.parentToChild;
        treeObj.addNodes(treeNode, {id:uid,pId:"a${id}",name:"${treeFileName}"});
	}
	
	function getTreeByFieldType() {
		if("6"==$("#fieldType").val()){
			$("#valueRangeDiv").hide();
			$("#dictNameDiv").hide();
			$("#dateFormatDiv").hide();
			 $("#textareaDiv").hide();
			$("#valueRangeSelectDiv").show();
			$("#valueRangeRow").show();
			$("#selectDec").hide();
			$("#dec").show();
			$("#lengthValue").show();
		}else if("9"==$("#fieldType").val()){
			$("#dictNameDiv").show();
			$("#valueRangeDiv").hide();
			$("#dateFormatDiv").hide();
			 $("#textareaDiv").hide();
			$("#valueRangeSelectDiv").hide();
			$("#valueRangeRow").show();
			$("#selectDec").hide();
			$("#dec").show();
			$("#lengthValue").show();
		}else if("7"==$("#fieldType").val()){
			$("#valueRangeDiv").hide();
			$("#valueRangeSelectDiv").hide();
			$("#dictNameDiv").hide();
			 $("#textareaDiv").hide();
			$("#dateFormatDiv").show();
			$("#valueRangeRow").show();
			$("#selectDec").hide();
			$("#dec").show();
			$("#lengthValue").show();
		}else if("3"==$("#fieldType").val()||"4"==$("#fieldType").val()){
// 			$("#valueRangeDiv").hide();
// 			$("#dictNameDiv").hide();
// 			 $("#textareaDiv").show();
// 			$("#dateFormatDiv").hide();
// 			$("#valueRangeSelectDiv").hide();
			$("#dictNameDiv").show();
			$("#valueRangeDiv").hide();
			$("#dateFormatDiv").hide();
			 $("#textareaDiv").hide();
			$("#valueRangeSelectDiv").hide();
			$("#valueRangeRow").show();
			$("#selectDec").show();
			$("#dec").hide();
			$("#lengthValue").show();
		}else if("10"==$("#fieldType").val()||"11"==$("#fieldType").val()){
			$("#valueRangeRow").hide();
			$("#selectDec").hide();
			$("#dec").show();
			$("#lengthValue").show();
		}else if("2"==$("#fieldType").val()){
			$("#lengthValue").hide();
		}else{
			$("#valueRangeDiv").show();
			$("#dictNameDiv").hide();
			 $("#textareaDiv").hide();
			$("#dateFormatDiv").hide();
			$("#valueRangeSelectDiv").hide();
			$("#valueRangeRow").show();
			$("#selectDec").hide();
			$("#dec").show();
			$("#lengthValue").show();
		}
		
	}
	
	
	function getvalueRangeSelect(){
		$.ajax({
			type:"post",
			url:"${path}/system/metaData/getTreeByFieldType.action",
			success:function(data){
				 var list = JSON.parse(data);
				 var select = $("<select class='form-control' name='valueRangeSelect' id='valueRangeSelect'></select>");
				 select.appendTo($("#valueRangeSelectDiv"));
				 for(var i=0;i<list.length;i++){
						var listSon = list[i];
						var name = listSon.name;
						var id = listSon.id;
						var option = $("<option value="+ id +">" + name + "</option>");
						option.appendTo(select);
				}
			}
		});
		getTreeByFieldType();
	}
	
	function getDictName(){
		$.ajax({
			type:"post",
			url:"${path}/system/metaData/getDictName.action",
			success:function(data){
				 var list = JSON.parse(data);
				 var select = $("<select class='form-control' name='dictName' id='dictName'></select>");
				 select.appendTo($("#dictNameDiv"));
				 for(var i=0;i<list.length;i++){
						var listSon = list[i];
						var name = listSon.name;
						var indexTag = listSon.indexTag;
						var option = $("<option value="+ indexTag +">" + name + "</option>");
						option.appendTo(select);
				}
			}
		});
		getTreeByFieldType();
	}
	
	
	function reBack() {
		if("metaDefinition"=="${flag}") {
    		parent.reBack(1);
    	}else if("centerMeta"=="${flag}") {
    		parent.reBack(1);
    	}else if("addFileMetaData"=="${flag}") {
    		parent.reBack(1);
    	}
	}
	
	
	$(function(){
		    if(uid!=""){
		    	if("metaDefinition"=="${flag}") {
		    		addTreeValue(uid);
		    	}else if("centerMeta"=="${flag}") {
		    		addCenterTreeValue(uid);
		    	}else if("addFileMetaData"=="${flag}") {
		    		addFileTreeValue(uid);
		    	}
		    }
		    $("#dateFormatDiv").hide();
		    $("#textareaDiv").hide();
		    getvalueRangeSelect();
		    getDictName();
	});
</script>
</head>
<body>
	<div class="panel panel-default" style="width:750px">
				       <label class="control-label">注:<span class="required">*</span>为必填项目
			           </label>
				       <div class="panel-body" id="999">
				           <form action="" id="form" method="post">
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right"><span
									class="required">*</span>元数据名称：</label>
								<div class="col-md-7">
									<input name="fieldZhName" id="fieldZhName" type="text"
										class="form-control text-input" />
								</div>
							</div>
						</div>

						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right"><span
									class="required">*</span>元数据英文名称：</label>
								<div class="col-md-7">
									<input name="fieldName" id="fieldName" type="text"
										class="form-control text-input" />
								</div>
							</div>
						</div>
					</div>
					<div class='row' >
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">输入类型：</label>
								<div class="col-md-7">
									<app:select name="fieldType" indexTag="fieldType"
										id="fieldType" selectedVal="${fieldType}" headName=""
										headValue="" onchange="getTreeByFieldType()"/>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">值格式校验模式：</label>
								<div class="col-md-7">
									<app:select name="validateModel" indexTag="validateModel"
										id="validateModel" selectedVal="${validateModel}" headName=""
										headValue="" />
								</div>
							</div>
						</div>

					</div>
					<div class='row' id="lengthValue">
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">值长度范围：</label>
								<div class="col-md-7">
									<input type="text" id="valueLength" name="valueLength"
										class="form-control text-input" />
								</div>
							</div>
						</div>
						<div class="col-md-6" id="dec">
							<div class="form-wrap">
								<div class="col-md-12">
									<span style="font-size: 12px">以双闭区间限定值长度范围如:0,10表示长度在0和10之间;</span><br /> <span
										style="font-size: 12px">5,表示长度必须大于5;10表示长度必须小于10</span>
								</div>
							</div>
						</div>
						<div class="col-md-6" id="selectDec">
							<div class="form-wrap">
								<div class="col-md-12">
									<span style="font-size: 12px">以数字限定值长度范围如:1表示只能选择1个;5,表示长</span><br /> <span
										style="font-size: 12px">度只能选择5个;选择单选不用填写值长度范围,默认单选</span>
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<div class="col-md-6" id="valueRangeRow">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">取值范围：</label>
								<div class="col-md-7" id="valueRangeDiv">
									<input type='text' id='valueRange' name='valueRange' class='form-control text-input' />
								</div>
								<div class="col-md-7" id="valueRangeSelectDiv">
									
								</div>
								<div class="col-md-7" id="dictNameDiv">
									
								</div>
								<div class="col-md-7" id="textareaDiv">
									<textarea class="form-control text-input" cols="10" rows="3"
										name="textareaRange" id="textareaRange"></textarea>
								</div>
								<div class="col-md-7" id="dateFormatDiv">
									<app:select name="dateFormat" indexTag="dateFormat"
										id="dateFormat" selectedVal="${dateFormat}" headName=""
										headValue=""/>
								</div>
							</div>
						</div>
						<!-- <div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">属性分类：</label>
								<div class="col-md-7">
									<input type="text" id="attriType" name="attriType"
										class="form-control text-input" />
								</div>
							</div>
						</div> -->
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">是否显示：</label>
								<div class="col-md-7">
									<input type="radio" id="showField" name="showField"
										value="false" class="radio-inline" /> 不显示 <input
										type="radio" id="showField" name="showField" value="true"
										class="radio-inline"  checked="checked"/> 显示
								</div>
							</div>
						</div>

					</div>
					
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">显示优先级：</label>
								<div class="col-md-7">
									<input id="viewPriority" name="viewPriority" value="1"
										type="checkbox" checked="checked">详细页显示</input> <input id="viewPriority"
										name="viewPriority" value="2" type="checkbox">列表显示</input> <input
										id="viewPriority" name="viewPriority" value="3"
										type="checkbox" checked="checked">编辑页显示</input>
									<!-- <br/>
									<input id="viewPriority" name="viewPriority" value="4" type="checkbox">查询页显示</input> -->
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">查重：</label>
								<div class="col-md-7">
									<input type="radio" id="duplicateCheck" name="duplicateCheck"
										value="false" checked="checked" class="radio-inline" /> 否 <input
										type="radio" id="duplicateCheck" name="duplicateCheck"
										value="true" class="radio-inline" /> 是
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
							<div class="col-md-6">
								<div class="form-wrap">
									<label class="control-label col-md-5 text-right">是否允许为空：</label>
									<div class="col-md-7">
										<input type="radio" id="allowNull" name="allowNull" value="0"
											class="radio-inline" /> 不允许 <input
											type="radio" id="allowNull" name="allowNull" value="1"
											class="radio-inline" checked="checked" /> 允许
									</div>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-wrap">
									<label class="control-label col-md-5 text-right">是否只读：</label>
									<div class="col-md-7">
										<input type="radio" id="readOnly" name="readOnly" value="false"
											checked="checked" class="radio-inline" /> 否 <input
											type="radio" id="readOnly" name="readOnly" value="true"
											class="radio-inline" /> 是
									</div>
								</div>
							</div>
					</div>
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">查询模式：</label>
								<div class="col-md-7">
									<app:select name="queryModel" indexTag="queryModel"
										id="queryModel" selectedVal="${queryModel}" headName=""
										headValue="" />
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">是否高级查询：</label>
								<div class="col-md-7">
									<input type="radio" id="allowAdvancedQuery"
										name="allowAdvancedQuery" value="false" checked="checked"
										class="radio-inline" /> 否 <input type="radio"
										id="allowAdvancedQuery" name="allowAdvancedQuery" value="true"
										class="radio-inline" /> 是
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">是否批量检索：</label>
								<div class="col-md-7">
									<input type="radio" id="openQuery" name="openQuery" value="1"
										class="radio-inline"  checked="checked" />否<input
										type="radio" id="openQuery" name="openQuery" value="0"
										class="radio-inline"/>是
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">是否二次查询：</label>
								<div class="col-md-7">
									<input type="radio" id="secondSearch" name="secondSearch" value="1"
										class="radio-inline"  checked="checked" />否<input
										type="radio" id="secondSearch" name="secondSearch" value="0"
										class="radio-inline"/>是
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">特殊标识：</label>
								<div class="col-md-7">
									<app:select name="identifier" indexTag="identifier"
										id="identifier" selectedVal="${identifier}" headName=""
										headValue="" />
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">自动完成功能：</label>
								<div class="col-md-7">
									<input type="radio" id="openAutoComple" name="openAutoComple"
										value="false" checked="checked" /> 不开启 <input type="radio"
										id="openAutoComple" name="openAutoComple" value="true"
										class="radio-inline" /> 开启
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<%-- <div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">资源生命周期：</label>
								<div class="col-md-7">
									<app:constants name="resLifeCycle" id="resLifeCycle"
										repository="com.brainsoon.system.support.SystemConstants"
										className="ResLifeCycle" inputType="select" selected="1"
										cssType="form-control"></app:constants>
								</div>
							</div>
						</div> --%>
						
						<%-- <div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">元数据类别：</label>
								<div class="col-md-7">
									<app:select name="type" indexTag="type" id="type"
										selectedVal="${type}" headName="" headValue="" />
								</div>
							</div>
						</div> --%>
						<%-- <div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">查询级别：</label>
								<div class="col-md-7">
									<app:select name="openQuery" indexTag="openQuery"
										id="openQuery" selectedVal="${openQuery}" headName=""
										headValue="" />
								</div>
							</div>
						</div> --%>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">导出等级：</label>
								<div class="col-md-7">
									<input id="exportLevel" name="exportLevel" value="1"
										type="checkbox" checked="checked">一级</input> <input id="exportLevel"
										name="exportLevel" value="2" type="checkbox">二级</input> <input
										id="exportLevel" name="exportLevel" value="3" type="checkbox">三级</input>
									<input id="exportLevel" name="exportLevel" value="4"
										type="checkbox">四级</input>  <br /><input id="exportLevel"
										name="exportLevel" value="5" type="checkbox">五级</input>
									<input id="exportLevel" name="exportLevel" value="6"
										type="checkbox">六级</input> <input id="exportLevel"
										name="exportLevel" value="7" type="checkbox">七级</input>
									<%-- <app:constants name="exportLevel" id="exportLevel"
										repository="com.brainsoon.system.support.SystemConstants"
										className="ExportLevel" inputType="select" selected="1"
										cssType="form-control"></app:constants> --%>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">默认值：</label>
								<div class="col-md-7">
									<input type="text" id="defaultValue" name="defaultValue"
										class="form-control text-input" />
								</div>
							</div>
						</div>
					</div>


<!-- 					<div class='row'> -->
<!-- 						<div class="col-md-6"> -->
<!-- 							<div class="form-wrap"> -->
<!-- 								<label class="control-label col-md-5 text-right">LOM属性：</label> -->
<!-- 								<div class="col-md-7"> -->
<!-- 									<input type="text" id="lomAttri" name="lomAttri" -->
<!-- 										class="form-control text-input" /> -->
<!-- 								</div> -->
<!-- 							</div> -->
<!-- 						</div> -->
<!-- 						<div class="col-md-6"> -->
<!-- 							<div class="form-wrap"> -->
<!-- 								<label class="control-label col-md-5 text-right">是否创建索引：</label> -->
<!-- 								<div class="col-md-7"> -->
<!-- 									<input type="radio" id="createIndex" name="createIndex" -->
<!-- 										value="false" checked="checked" class="radio-inline" /> 否 <input -->
<!-- 										type="radio" id="createIndex" name="createIndex" value="true" -->
<!-- 										class="radio-inline" /> 是 -->
<!-- 								</div> -->
<!-- 							</div> -->
<!-- 						</div> -->
<!-- 					</div> -->
					<%-- <div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">导出等级：</label>
								<div class="col-md-7">
									<input id="exportLevel" name="exportLevel" value="1"
										type="checkbox" checked="checked">一级</input> <input id="exportLevel"
										name="exportLevel" value="2" type="checkbox">二级</input> <input
										id="exportLevel" name="exportLevel" value="3" type="checkbox">三级</input>
									<input id="exportLevel" name="exportLevel" value="4"
										type="checkbox">四级</input> <input id="exportLevel"
										name="exportLevel" value="5" type="checkbox">五级</input> <br />
									<input id="exportLevel" name="exportLevel" value="6"
										type="checkbox">六级</input> <input id="exportLevel"
										name="exportLevel" value="7" type="checkbox">七级</input>
									<app:constants name="exportLevel" id="exportLevel"
										repository="com.brainsoon.system.support.SystemConstants"
										className="ExportLevel" inputType="select" selected="1"
										cssType="form-control"></app:constants>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">查询级别：</label>
								<div class="col-md-7">
									<app:select name="openQuery" indexTag="openQuery"
										id="openQuery" selectedVal="${openQuery}" headName=""
										headValue="" />
								</div>
							</div>
						</div>
					</div> --%>
					<%-- <div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right"><span
									class="required">*</span>查询模式：</label>
								<div class="col-md-7">
									<app:constants name="queryModel" id="queryModel"
										repository="com.brainsoon.system.support.SystemConstants"
										className="QueryModel" inputType="select" selected="1"
										cssType="form-control"></app:constants>
								</div>
							</div>
						</div>
					</div> --%>
					<div class='row'>
						<div class="col-md-12">
							<div class="form-wrap" style="margin-left: -140px">
								<label class="control-label col-md-4 text-right">相关词：</label>
								<div class="col-md-8">
									<textarea class="form-control" cols="30" rows="3"
										name="relatedWords" id="relatedWords"></textarea>
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<div class="col-md-12">
							<div class="form-wrap" style="margin-left: -140px">
								<label class="control-label col-md-4 text-right">备注：</label>
								<div class="col-md-8">
									<textarea class="form-control" cols="30" rows="3"
										name="description" id="description"></textarea>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-6" style="margin-left:300px;margin-top:10px">
						<!-- <div class="form-actions"> -->
								<input type="button" value="保存" class="btn btn-primary "
									style="width: 100px;" onclick="checkSave();"/>
								<!-- <input type="button" value="返回" class="btn btn-primary "
								style="width: 100px;" onclick="reBack();" /> -->		<!-- 不需要返回按钮 -->
						<!-- </div> -->
					</div>
				</form>
					</div>
				</div>
</body>
</html>