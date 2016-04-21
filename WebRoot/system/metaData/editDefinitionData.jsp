<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>编辑页面</title>
<script type="text/javascript">
    $(function(){
	    getSelected();
	    getViewPriority();
	    getvalueRangeSelect();
	    getDictName();
    });
    
     function getSelected() {
		var exportLevel = "${metadataDefinition.exportLevel}";
		var exportLevelSelect = exportLevel.split(",");
		var allexportLevel = ['1','2','3','4','5','6','7'];
		var allexportLevelName = ['一级','二级','三级','四级','五级','六级','七级'];
		var count =0;
		for(var i=0;i<7;i++) {
			count++;
			var _exist=$.inArray(allexportLevel[i],exportLevelSelect); 
			if(_exist>=0) {
				var input = $("<input id='exportLevel' name='exportLevel' value='"+allexportLevel[i]+"' type='checkbox' checked='checked'>"+allexportLevelName[i]+"</input>");
				input.appendTo($("#exportLevelAll"));
			}else {
				var input = $("<input id='exportLevel' name='exportLevel' value='"+allexportLevel[i]+"' type='checkbox'>"+allexportLevelName[i]+"</input>");
				input.appendTo($("#exportLevelAll"));
			}
			var br =$("<br/>");
			if(count%4==0) {
				br.appendTo($("#exportLevelAll"));
			}
		}
	}
     
   /*   function reBack() {
 		$("#editDefinitionDiv").hide();
 	} */
     
     function getViewPriority() {
 		var viewPriority = "${metadataDefinition.viewPriority}";
 		var viewPrioritySelect = viewPriority.split(",");
 		var allviewPriority = ['1','2','3'];
 		var allviewPriorityName = ['详细页显示','列表显示','编辑页显示'];
 		var count =0;
 		for(var i=0;i<3;i++) {
 			count++;
 			var _exist=$.inArray(allviewPriority[i],viewPrioritySelect); 
 			if(_exist>=0) {
 				var input = $("<input id='viewPriority' name='viewPriority' value='"+allviewPriority[i]+"' type='checkbox' checked='checked'>"+allviewPriorityName[i]+"</input>");
 				input.appendTo($("#viewPriorityAll"));
 			}else {
 				var input = $("<input id='viewPriority' name='viewPriority' value='"+allviewPriority[i]+"' type='checkbox'>"+allviewPriorityName[i]+"</input>");
 				input.appendTo($("#viewPriorityAll"));
 			}
 			var br =$("<br/>");
 			if(count%3==0) {
 				br.appendTo($("#viewPriorityAll"));
 			}
 		}
 	}
	function save() {
		var typeId = "${id}";
		var groupId = "${groupId}";
		var typeName = encodeURI(encodeURI("${typeName}"));
		var uuid = "${metadataDefinition.uri}";
		var fieldName = "${metadataDefinition.fieldName}";
		if("6"==$("#fieldType").val()){
			$("#form").attr(
					"action",
					"${path}/system/metaData/editDefinitionAction.action?typeId="
							+ typeId + "&groupId=" + groupId+"&typeName="+typeName+"&uri="+uuid+"&fieldName="+fieldName+"&delType="+"${delType}"+"&valueRange="+$("#valueRangeSelect").val());
		}else if("9"==$("#fieldType").val()){
			$("#form").attr(
					"action",
					"${path}/system/metaData/editDefinitionAction.action?typeId="
							+ typeId + "&groupId=" + groupId+"&typeName="+typeName+"&uri="+uuid+"&fieldName="+fieldName+"&delType="+"${delType}"+"&valueRange="+$("#dictName").val());
		}else if("7"==$("#fieldType").val()){
			$("#form").attr(
					"action",
					"${path}/system/metaData/editDefinitionAction.action?typeId="
							+ typeId + "&groupId=" + groupId+"&typeName="+typeName+"&uri="+uuid+"&fieldName="+fieldName+"&delType="+"${delType}"+"&valueRange="+$("#dateFormat").val());
		}else if("2"==$("#fieldType").val()||"3"==$("#fieldType").val()||"4"==$("#fieldType").val()){
// 			$("#form").attr(
// 					"action",
// 					"${path}/system/metaData/editDefinitionAction.action?typeId="
// 							+ typeId + "&groupId=" + groupId+"&typeName="+typeName+"&uri="+uuid+"&fieldName="+fieldName+"&delType="+"${delType}"+"&valueRange="+$("#textareaRange").val());
			$("#form").attr(
					"action",
					"${path}/system/metaData/editDefinitionAction.action?typeId="
							+ typeId + "&groupId=" + groupId+"&typeName="+typeName+"&uri="+uuid+"&fieldName="+fieldName+"&delType="+"${delType}"+"&valueRange="+$("#dictName").val());
		}else{
			$("#form").attr(
					"action",
					"${path}/system/metaData/editDefinitionAction.action?typeId="
							+ typeId + "&groupId=" + groupId+"&typeName="+typeName+"&uri="+uuid+"&fieldName="+fieldName+"&delType="+"${delType}"+"&valueRange="+$("#valueRange").val());
		}
		
		$("#form").submit();
	}
	
	function checkSave() {
    	var fieldZhName = $("#fieldZhName").val();
    	var fieldName = $("#fieldName").val();
    	if(fieldZhName==""||fieldName=="") {
    		$.alert("请填写必填信息后再提交");
    		
    	}else{
    		editTreeValue();
    		save();
    		//$.alert("元数据修改成功!");
    		noticeForIframe("提示信息","元数据修改成功!","3");
    	}
    }
	
	
	function editTreeValue() {
		var treeObj = parent.treeObjToChild;
		var treeNode = parent.haveEditNode;
		var treeNodName = $("#fieldZhName").val();
//		var allowNull = $("input[name='allowNull']:checked").val();// $("#allowNull").val();
// 		if(allowNull != undefined && allowNull != null && allowNull == 0){ //不允许为空
// 			alert(allowNull);
// 			treeNodName += "<span style='color:red;margin-left:5px;font-weight:bold;'>N</span>";
// 		}
// //		else if(allowNull != undefined && allowNull != null && treeObj.allowNull == 1){
// //			treeNodName += "<span style='color:red;margin-left:5px;font-weight:bold;'>Y</span>";
// //		}
// 		var duplicateCheck = $("input[name='duplicateCheck']:checked").val(); //$("#duplicateCheck").val();
// 		alert(duplicateCheck);
// 		if(duplicateCheck != undefined && duplicateCheck != null && duplicateCheck == "true"){ //是查重项
// 			alert(duplicateCheck);
// 			treeNodName += "<span style='color:blue;margin-left:5px;font-weight:bold;'>Q</span>";
// 		}
		treeNode.name = treeNodName;
		treeObj.updateNode(treeNode);
		//treeObj.editName(treeNode);
	}
	
	function edit() {
		var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
		nodes = zTree.getSelectedNodes(),
		treeNode = nodes[0];
		if (nodes.length == 0) {
			alert("请先选择一个节点");
			return;
		}
		zTree.editName(treeNode);
	};
	
	
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
			//修改
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
	function getDictName(){
		$.ajax({
			type:"post",
			url:"${path}/system/metaData/getDictName.action",
			success:function(data){
				 var list = JSON.parse(data);
				 var select = $("<select class='form-control' name='dictName' id='dictName'><option value=\"\">---请选择---</option></select>");
				 select.appendTo($("#dictNameDiv"));
				 for(var i=0;i<list.length;i++){
						var listSon = list[i];
						var name = listSon.name;
						var indexTag = listSon.indexTag;
						var option = $("<option value="+ indexTag +">" + name + "</option>");
						if(indexTag=="${metadataDefinition.valueRange}"){
							option.attr("selected", "selected");
						}
						option.appendTo(select);
				}
			}
		});
		getTreeByFieldType();
	}
	function getvalueRangeSelect(){
		$.ajax({
			type:"post",
			url:"${path}/system/metaData/getTreeByFieldType.action",
			success:function(data){
				 var list = JSON.parse(data);
				 var select = $("<select class='form-control' name='valueRangeSelect' id='valueRangeSelect'><option value=\"\">---请选择---</option></select>");
				 select.appendTo($("#valueRangeSelectDiv"));
				 for(var i=0;i<list.length;i++){
						var listSon = list[i];
						var name = listSon.name;
						var id = listSon.id;
						var option = $("<option value="+ id +">" + name + "</option>");
						if(id=="${metadataDefinition.valueRange}"){
							option.attr("selected", "selected");
						}
						option.appendTo(select);
				}
			}
		});
		getTreeByFieldType();
	}
	
</script>
</head>
<body>
		<div class="panel panel-default" style="width:750px">
			<label class="control-label">注:<span class="required">*</span>为必填项目
			</label>
			<div class="panel-body height_remain" id="999">
				<form action="" id="form" method="post">
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right"><span
									class="required">*</span>元数据名称：</label>
								<div class="col-md-7">
									<input name="fieldZhName" id="fieldZhName" type="text"
										class="form-control text-input" value="${metadataDefinition.fieldZhName}"/>
								</div>
							</div>
						</div>

						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right"><span
									class="required">*</span>元数据英文名称：</label>
								<div class="col-md-7">
									<input name="fieldName" id="fieldName" type="text" disabled="disabled"
										class="form-control text-input" value="${metadataDefinition.fieldName}"/>
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">输入类型：</label>
								<div class="col-md-7">
								    <app:select name="fieldType" indexTag="fieldType" id="fieldType"  selectedVal="${metadataDefinition.fieldType}" onchange="getTreeByFieldType()" headName="---请选择---"  headValue=""  />
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">值格式校验模式：</label>
								<div class="col-md-7">
									<app:select name="validateModel" indexTag="validateModel" id="validateModel"  selectedVal="${metadataDefinition.validateModel}" headName="---请选择---"  headValue=""  />
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
										    class="form-control text-input" value="${metadataDefinition.valueLength}"/>
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
								       <input type="text" id="valueRange" name="valueRange"
										class="form-control text-input" value="${metadataDefinition.valueRange}"/>
									   
								    </div>
								    <div class="col-md-7" id="valueRangeSelectDiv">
								    </div>
									<div class="col-md-7" id="dictNameDiv">
								    </div>
								    <div class="col-md-7" id="dateFormatDiv">
									<app:select name="dateFormat" indexTag="dateFormat"
										id="dateFormat" selectedVal="${metadataDefinition.valueRange}" headName="---请选择---"
										headValue=""/>
								    </div>
								    <div class="col-md-7" id="textareaDiv">
									<textarea class="form-control text-input" cols="10" rows="3"
										name="textareaRange" id="textareaRange">${metadataDefinition.valueRange}</textarea>
								    </div>
								
							</div>
						</div>
						<%-- <div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">属性分类：</label>
								<div class="col-md-7">
									<input type="text" id="attriType" name="attriType"
										class="form-control text-input" value="${metadataDefinition.attriType}"/>
								</div>
							</div>
						</div> --%>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">是否显示：</label>
								<div class="col-md-7">
								    <c:if test="${metadataDefinition.showField=='false'}">
						              <input type="radio" id="showField" name="showField"
										value="false" checked="checked" class="radio-inline" /> 不显示 <input
										type="radio" id="showField" name="showField" value="true"
										class="radio-inline" /> 显示
					                </c:if>
					                <c:if test="${metadataDefinition.showField=='true'}">
						            <input type="radio" id="showField" name="showField"
										value="false"  class="radio-inline" /> 不显示 <input
										type="radio" id="showField" name="showField" value="true"
										class="radio-inline" checked="checked" /> 显示
					                </c:if>
									<c:if test="${metadataDefinition.showField==null}">
						              <input type="radio" id="showField" name="showField"
										value="false" checked="checked" class="radio-inline" /> 不显示 <input
										type="radio" id="showField" name="showField" value="true"
										class="radio-inline" /> 显示
					                </c:if>
								</div>
							</div>
						</div>

					</div>
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">显示优先级：</label>
								<div class="col-md-7" id="viewPriorityAll">
								   
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">查重：</label>
								<div class="col-md-7">
								    <c:if test="${metadataDefinition.duplicateCheck=='false'}">
						              <input type="radio" id="duplicateCheck" name="duplicateCheck"
										value="false" checked="checked" class="radio-inline" /> 否 <input
										type="radio" id="duplicateCheck" name="duplicateCheck"
										value="true" class="radio-inline" /> 是
					                </c:if>
					                <c:if test="${metadataDefinition.duplicateCheck=='true'}">
						             <input type="radio" id="duplicateCheck" name="duplicateCheck"
										value="false"  class="radio-inline" /> 否 <input
										type="radio" id="duplicateCheck" name="duplicateCheck"
										value="true" class="radio-inline" checked="checked"/> 是
					                </c:if>
									<c:if test="${metadataDefinition.duplicateCheck==null}">
						              <input type="radio" id="duplicateCheck" name="duplicateCheck"
										value="false" checked="checked" class="radio-inline" /> 否 <input
										type="radio" id="duplicateCheck" name="duplicateCheck"
										value="true" class="radio-inline" /> 是
					                </c:if>
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
<!-- 						<div class="col-md-6"> -->
<!-- 							<div class="form-wrap"> -->
<!-- 								<label class="control-label col-md-5 text-right">是否创建索引：</label> -->
<!-- 								<div class="col-md-7"> -->
<%-- 								     <c:if test="${metadataDefinition.createIndex=='false'}"> --%>
<!-- 						               <input type="radio" id="createIndex" name="createIndex" -->
<!-- 										value="false" checked="checked" class="radio-inline" /> 否 <input -->
<!-- 										type="radio" id="createIndex" name="createIndex" value="true" -->
<!-- 										class="radio-inline" /> 是 -->
<%-- 					                </c:if> --%>
<%-- 					                <c:if test="${metadataDefinition.createIndex=='true'}"> --%>
<!-- 						             <input type="radio" id="createIndex" name="createIndex" -->
<!-- 										value="false" class="radio-inline" /> 否 <input -->
<!-- 										type="radio" id="createIndex" name="createIndex" value="true" -->
<!-- 										class="radio-inline"  checked="checked" /> 是 -->
<%-- 					                </c:if> --%>
					                
<%-- 					                <c:if test="${metadataDefinition.createIndex==null}"> --%>
<!-- 						               <input type="radio" id="createIndex" name="createIndex" -->
<!-- 										value="false" checked="checked" class="radio-inline" /> 否 <input -->
<!-- 										type="radio" id="createIndex" name="createIndex" value="true" -->
<!-- 										class="radio-inline" /> 是 -->
<%-- 					                </c:if> --%>
<!-- 								</div> -->
<!-- 							</div> -->
<!-- 						</div> -->
						<!-- 						<div class="col-md-6"> -->
<!-- 							<div class="form-wrap"> -->
<!-- 								<label class="control-label col-md-5 text-right">LOM属性：</label> -->
<!-- 								<div class="col-md-7"> -->
<!-- 									<input type="text" id="lomAttri" name="lomAttri" -->
<%-- 										class="form-control text-input" value="${metadataDefinition.lomAttri}"/> --%>
<!-- 								</div> -->
<!-- 							</div> -->
<!-- 						</div> -->
					</div>
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">是否允许为空：</label>
								<div class="col-md-7">
								    <c:if test="${metadataDefinition.allowNull=='1'}">
						               <input type="radio" id="allowNull" name="allowNull" value="0"
										class="radio-inline" /> 不允许 <input type="radio"
										id="allowNull" name="allowNull" value="1"
										class="radio-inline" checked="checked" /> 允许
					                </c:if>
					                <c:if test="${metadataDefinition.allowNull=='0'}">
						              <input type="radio" id="allowNull" name="allowNull" value="0"
										class="radio-inline" checked="checked" /> 不允许 <input type="radio"
										id="allowNull" name="allowNull" value="1" 
										class="radio-inline" /> 允许
					                </c:if>
					                
					                <c:if test="${metadataDefinition.allowNull==null}">
						               <input type="radio" id="allowNull" name="allowNull" value="0"
										class="radio-inline" /> 不允许 <input type="radio"
										id="allowNull" name="allowNull" value="1"
										class="radio-inline"  checked="checked"/> 允许
					                </c:if>
									
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">是否只读：</label>
								<div class="col-md-7">
								    <c:if test="${metadataDefinition.readOnly==false}">
						                <input type="radio" id="readOnly" name="readOnly" value="false"
										checked="checked" class="radio-inline" /> 否 <input
										type="radio" id="readOnly" name="readOnly" value="true"
										class="radio-inline" /> 是
					                </c:if>
					                <c:if test="${metadataDefinition.readOnly==true}">
						              <input type="radio" id="readOnly" name="readOnly" value="false"
									     class="radio-inline" /> 否 <input
										type="radio" id="readOnly" name="readOnly" value="true"
										class="radio-inline" checked="checked" /> 是
					                </c:if>
					                
					                <c:if test="${metadataDefinition.readOnly==null}">
						                <input type="radio" id="readOnly" name="readOnly" value="false"
										checked="checked" class="radio-inline" /> 否 <input
										type="radio" id="readOnly" name="readOnly" value="true"
										class="radio-inline" /> 是
					                </c:if>
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<%-- <div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">资源生命周期：</label>
								<div class="col-md-7">
								    <app:select name="resLifeCycle" indexTag="resLifeCycle" id="resLifeCycle"  selectedVal="${metadataDefinition.resLifeCycle}" headName=""  headValue=""  />
								</div>
							</div>
						</div> --%>
						
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">查询模式：</label>
								<div class="col-md-7">
								<app:select name="queryModel" indexTag="queryModel"
										id="queryModel" selectedVal="${metadataDefinition.queryModel}" headName="---请选择---"
										headValue="" />
									<%-- <app:constants name="queryModel" id="queryModel"
										repository="com.brainsoon.system.support.SystemConstants"
										className="QueryModel" inputType="select" selected="${metadataDefinition.queryModel}"
										cssType="form-control"></app:constants> --%>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">是否高级查询：</label>
								<div class="col-md-7">
								    <c:if test="${metadataDefinition.allowAdvancedQuery=='false'}">
						              <input type="radio" id="allowAdvancedQuery"
										name="allowAdvancedQuery" value="false" checked="checked"
										class="radio-inline" /> 否 <input type="radio"
										id="allowAdvancedQuery" name="allowAdvancedQuery" value="true"
										class="radio-inline" /> 是
					                </c:if>
					                <c:if test="${metadataDefinition.allowAdvancedQuery=='true'}">
						            <input type="radio" id="allowAdvancedQuery"
										name="allowAdvancedQuery" value="false" 
										class="radio-inline" /> 否 <input type="radio"
										id="allowAdvancedQuery" name="allowAdvancedQuery" value="true"
										class="radio-inline" checked="checked" /> 是
					                </c:if>
									<c:if test="${metadataDefinition.allowAdvancedQuery==null}">
						              <input type="radio" id="allowAdvancedQuery"
										name="allowAdvancedQuery" value="false" checked="checked"
										class="radio-inline" /> 否 <input type="radio"
										id="allowAdvancedQuery" name="allowAdvancedQuery" value="true"
										class="radio-inline" /> 是
					                </c:if>
								</div>
							</div>
						</div>
						<%-- <div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">查询级别：</label>
								<div class="col-md-7">
									<app:constants name="openQuery" id="openQuery"
										repository="com.brainsoon.system.support.SystemConstants"
										className="OpenQuery" inputType="select" selected="${metadataDefinition.openQuery}"
										cssType="form-control"></app:constants>
								</div>
							</div>
						</div> --%>
						<%-- <div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right"><span
									class="required">*</span>元数据类别：</label>
								<div class="col-md-7">
								    <app:select name="type" indexTag="type" id="type"  selectedVal="${metadataDefinition.type}" headName=""  headValue=""  />
								</div>
							</div>
						</div> --%>
					</div>

					<%-- <div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">导出等级：</label>
								<div class="col-md-7" id="exportLevelAll">
						               
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right"><span
									class="required">*</span>查询级别：</label>
								<div class="col-md-7">
									<app:constants name="openQuery" id="openQuery"
										repository="com.brainsoon.system.support.SystemConstants"
										className="OpenQuery" inputType="select" selected="${metadataDefinition.openQuery}"
										cssType="form-control"></app:constants>
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
										className="QueryModel" inputType="select" selected="${metadataDefinition.queryModel}"
										cssType="form-control"></app:constants>
								</div>
							</div>
						</div>
					</div> --%>
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">是否批量检索：</label>
								<div class="col-md-7">
								    <c:if test="${metadataDefinition.openQuery=='1'}">
						               <input type="radio" id="openQuery" name="openQuery" value="1"
										class="radio-inline" checked="checked" /> 否 <input type="radio"
										id="openQuery" name="openQuery" value="0"
										class="radio-inline" /> 是
					                </c:if>
					                <c:if test="${metadataDefinition.openQuery=='0'}">
						              <input type="radio" id="openQuery" name="openQuery" value="1"
										class="radio-inline"  /> 否<input type="radio"
										id="openQuery" name="openQuery" value="0" 
										class="radio-inline" checked="checked"/> 是
					                </c:if>
					                
					                <c:if test="${metadataDefinition.openQuery==null}">
						               <input type="radio" id="openQuery" name="openQuery" value="1"
										class="radio-inline" checked="checked"/> 否 <input type="radio"
										id="openQuery" name="openQuery" value="0"
										class="radio-inline"  /> 是
					                </c:if>
									
								</div>
							</div>
						</div>
												<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">是否二次查询：</label>
								<div class="col-md-7">
								    <c:if test="${metadataDefinition.secondSearch!=null&&metadataDefinition.secondSearch=='1'}">
						               <input type="radio" id="secondSearch" name="secondSearch" value="1"
										class="radio-inline" checked="checked" /> 否 <input type="radio"
										id="secondSearch" name="secondSearch" value="0"
										class="radio-inline" /> 是
					                </c:if>
					                <c:if test="${metadataDefinition.secondSearch!=null&&metadataDefinition.secondSearch=='0'}">
						              <input type="radio" id="secondSearch" name="secondSearch" value="1"
										class="radio-inline"  /> 否<input type="radio"
										id="secondSearch" name="secondSearch" value="0" 
										class="radio-inline" checked="checked"/> 是
					                </c:if>
					                
					                <c:if test="${metadataDefinition.secondSearch==null}">
						               <input type="radio" id="secondSearch" name="secondSearch" value="1"
										class="radio-inline" checked="checked"/> 否 <input type="radio"
										id="secondSearch" name="secondSearch" value="0"
										class="radio-inline"  /> 是
					                </c:if>
									
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">特殊标识：</label>
								<div class="col-md-7">
									<app:select name="identifier" indexTag="identifier" id="identifier"  selectedVal="${metadataDefinition.identifier}" headName="---请选择---"  headValue=""  />
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">自动完成功能：</label>
								<div class="col-md-7">
								     <c:if test="${metadataDefinition.openAutoComple=='false'}">
						                <input type="radio" id="openAutoComple" name="openAutoComple"
										value="false" checked="checked" /> 不开启 <input type="radio"
										id="openAutoComple" name="openAutoComple" value="true"
										class="radio-inline" /> 开启
					                </c:if>
					                <c:if test="${metadataDefinition.openAutoComple=='true'}">
						               <input type="radio" id="openAutoComple" name="openAutoComple"
										value="false" /> 不开启 <input type="radio"
										id="openAutoComple" name="openAutoComple" value="true"
										class="radio-inline" checked="checked"/> 开启
					                </c:if>
					                
									 <c:if test="${metadataDefinition.openAutoComple==null}">
						                <input type="radio" id="openAutoComple" name="openAutoComple"
										value="false" checked="checked" /> 不开启 <input type="radio"
										id="openAutoComple" name="openAutoComple" value="true"
										class="radio-inline" /> 开启
					                </c:if>
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">导出等级：</label>
								<div class="col-md-7" id="exportLevelAll">
						               
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-wrap">
								<label class="control-label col-md-5 text-right">默认值：</label>
								<div class="col-md-7">
									<input type="text" id="defaultValue" name="defaultValue"
										class="form-control text-input" value="${metadataDefinition.defaultValue}"/>
								</div>
							</div>
						</div>
					</div>
					
					<div class='row'>
						<div class="col-md-12">
							<div class="form-wrap" style="margin-left: -140px">
								<label class="control-label col-md-4 text-right">相关词：</label>
								<div class="col-md-8">
									<textarea class="form-control" cols="30" rows="3"
										name="relatedWords" id="relatedWords">${metadataDefinition.relatedWords}</textarea>
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
										name="description" id="description">${metadataDefinition.description}</textarea>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-6" style="margin-left:300px;margin-top:10px">
						<!-- <div class="form-actions"> -->
							<input type="button" value="保存" class="btn btn-primary "
								style="width: 100px;" onclick="checkSave();" />
							<!-- <input type="button" value="返回" class="btn btn-primary "
								style="width: 100px;" onclick="parent.reBack(2);" /> -->
						<!-- </div> -->
					</div>
				</form>
			</div>
		</div>
</body>
</html>