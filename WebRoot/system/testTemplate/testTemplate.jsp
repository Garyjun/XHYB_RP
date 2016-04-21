<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>试卷模板</title>
<script type="text/javascript">
	function showAdd() {
		var divmain = $("<div class='form-group'></div>");
		divmain.appendTo($("#testTypeList"));
		var label = $("<label class='col-xs-4 control-label text-right'>试题类型:</label>");
		label.appendTo(divmain);
		var div = $("<div class='col-xs-3'><app:select name='testItem' indexTag='testType' id='testItem' selectedVal='${testItem}' headName='' headValue='' /></div>");
		div.appendTo(divmain);
		var buttonadd = $("<button type='button' class='btn btn-info' onclick='showAdd()'></button>");
		buttonadd.appendTo(divmain);
		var span = $("<span class='glyphicon glyphicon-plus'></span>");
		span.appendTo(buttonadd);
		var buttondel = $("<button type='button' class='btn btn-warning' onclick='del(this)'></button>");
		buttondel.appendTo(divmain);
		var spandel = $("<span class='glyphicon glyphicon-minus'></span>");
		spandel.appendTo(buttondel);
	}
	function del(del){
		$(del).parent().remove();
	}
	
	function save(){
		   var testTypeKey = '';
		   var testType = '';
		   var pid = $("#testTemplateId").val();
 		   $('select[name="testItem"]').each(function(){
 			  testTypeKey+=$(this).val()+","; 
 			  testType+= $(this).find("option:selected").text()+",";
		   });
		   $.post('${path}/system/testTemplate/addAction.action',
				  {testTypeKey:testTypeKey,testType:testType,pid:pid},
				  function(data){
					  location.href = "${path}/system/testTemplate/upd.action?id="+pid;
			     }); 
	
}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap "
		style="height: 100%;">
			<form:form action=""
				id="form" modelAttribute="frmTestTemplateItem"
				method="post" class="form-horizontal" role="form">
				<div class="form-group">
					<label class="col-xs-4 control-label text-right">试题类型:</label>
					<button type="button" class="btn btn-info" onclick="showAdd()">
						<span class="glyphicon glyphicon-plus"></span>
					</button>
					<button type="button" class="btn btn-warning" onclick="del(this)">
						<span class="glyphicon glyphicon-minus"></span>
					</button>
					<div class="col-xs-3">
						<app:select name="testItem" indexTag="testType" id="testItem"
							selectedVal="${testItem}" headName="" headValue="" />
					</div>
				</div>
				 <input type="hidden" id="paperId" name="paperId" value="${param.paperId }" />
				 <input type="hidden" id="paperName" name="paperName" value="${param.paperName }" />
				<div id="testTypeList">
				
				</div>
				<form:hidden path="testTemplate.id" id="testTemplateId"/>
				<input id="tijiao" type="button" style="margin-left:400px" value="保存" class="btn btn-primary" onclick="save();"/>
			</form:form>
	</div>
</body>
</html>