<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>编辑</title>
<script type="text/javascript"
	src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/ueditor/ueditor.config.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/ueditor/ueditor.all.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/ueditor/third-party/codemirror/codemirror.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/ueditor/third-party/zeroclipboard/ZeroClipboard.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${path}/appframe/plugin/ueditor/kityformula-plugin/addKityFormulaDialog.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${path}/appframe/plugin/ueditor/kityformula-plugin/getKfContent.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${path}/appframe/plugin/ueditor/kityformula-plugin/defaultFilterFix.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${path}/appframe/plugin/ueditor/lang/zh-cn/zh-cn.js"></script>
<script type="text/javascript">
	var i = 0;
	var j = 0;
	var index = 0;
	var o = 0;

	function addButton() {
		if (j == 0) {
			var num = $('#main span.num').length;
			if (num == 1) {
				num++;
			}
			j = num;
		}
		var childdiv = $('<span></span>');
		childdiv.attr('id', 'child' + j);
		childdiv.attr('class', 'num');
		childdiv.appendTo($("#main"));
		childdiv
				.append('<label class=\"control-label col-md-1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;问题：<\/label><div name=\"question'+j+'\" id=\"question'+j+'\" style=\"width: 600px;height:50px;display: inline-block;float: left;\"><\/div><div style=\"display: inline-block;float: left;height:190px; \">&nbsp;&nbsp;<input type=\"button\" value=\"添加选项\"  class=\"btn btn-primary\" onclick=\"addChoice('
						+ j
						+ ')\"/>&nbsp;&nbsp;<input type=\"button\" value=\"添加问题\" id=\"addQuestion\" class=\"btn btn-primary\" onclick=\"addButton();\"/>&nbsp;&nbsp;<input type=\"button\" value=\"删除问题\" class=\"btn btn-primary red\" onclick=\"deleteRow('
						+ j
						+ ');\"/><\/div><script type=\"text\/javascript\">var child'
						+ j
						+ 'UE = UE.getEditor(\"question'
						+ j
						+ '\");<\/script> ');
		j++;
	}
	function deleteRow(j) {
		var targ = $("#child" + j);
		targ.remove();
	}
	function addChoice(j) {
		if (o == 0) {
			o++;
			index = $('#child' + j + ' div.row').length;
			index++;
		}
		//alert(index);
		index++;
		$('#child' + j + '')
				.append(
						'<div class="row" id=\"optionRow'+j+index+'\"><div id=\"choiceFlag'+j+index+'\" class=\"control-label col-md-1 \"  style=\"display: inline-block;float: left;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;选项标号:&nbsp;<input type=\"text\" class=\"form-control\" id=\"optionFlag'+j+index+'\" style=\"width: 35px;height:32px;display: inline-block;"\/>&nbsp;&nbsp;&nbsp;&nbsp;内容:&nbsp;</div><div id=\"optionContext'+j+index+'\" name=\"optionContext'+j+index+'\" style=\"width: 488px;height:32px;display: inline-block;margin-left:123px;float:left;"></div><div style=\"display: inline-block;float: left;height:50px;width:300px; \">&nbsp;&nbsp;<input type=\"button\" value=\"删除选项\" class=\"btn btn-primary red\" onclick=\"deleteChoice(\''
								+ j
								+ index
								+ '\');\"/></div><script type=\"text\/javascript\">var optionContext'
								+ j
								+ index
								+ 'UE = UE.getEditor(\"optionContext'
								+ j
								+ index
								+ '\",{toolbars: [[\'fullscreen\', \'source\', \'undo\', \'redo\', \'insertimage\']]});<\/script>');
	}
	function deleteChoice(y) {
		var targChoiceFlag = $("#optionRow" + y);
		targChoiceFlag.remove();
		//index++;
	}
	$(function() {
		var content_json = decodeURIComponent('${content_json}');
		if (content_json != null && content_json != '') {
			editQuestion(content_json);
		}
		var analysis_description = decodeURIComponent('${analysis_description}');
		var answer_description = decodeURIComponent('${answer_description}');
		var answer_json = decodeURIComponent('${answer_json}');
		$('#answer_json').val(answer_json);
		$('#answer_description').val(answer_description);
		$('#analysis_description').val(analysis_description);
		//清空session
		$.ajax({
			url : '${path}/bres/removeSession.action',
			type : 'post',
			datatype : 'text',
			success : function(returnValue) {
			}
		});

	});
	//将json转换为数据
	function editQuestion(json) {
		json = eval('(' + json + ')');
		var material = "";
		if (json.material != "" || json.material != undefined) {
			 material = json.material;
		}
		for ( var i = 0; i < json.samllQuesions.length; i++) {
			var jsonObj = json.samllQuesions[i];
			var questions = jsonObj.questionDesc;
			var childdiv = $('<span></span>');
			childdiv.attr('id', 'child' + i + '');
			childdiv.attr('class', 'num');
			childdiv.appendTo($("#main"));
			childdiv
					.append('<label class=\"control-label col-md-1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;问题:</label><textarea name=\"question'+i+'\" id=\"question'+i+'\" style=\"width: 600px;height:50px;display: inline-block;float: left;\"><\/textarea><div style=\"display: inline-block;float: left;height:190px; \">&nbsp;&nbsp;<input type=\"button\" value=\"添加选项\"  class=\"btn btn-primary\" onclick=\"addChoice('
							+ i
							+ ')\"/>&nbsp;&nbsp;<input type=\"button\" value=\"添加问题\" id=\"addQuestion\" class=\"btn btn-primary\" onclick=\"addButton();\"/>&nbsp;&nbsp;<input type=\"button\" value=\"删除问题\" class=\"btn btn-primary red\" onclick=\"deleteRow('
							+ i
							+ ');\"/><\/div><script type=\"text\/javascript\"> $(\'#question'
							+ i
							+ '\').val(\''
							+ questions
							+ '\');var c'
							+ i
							+ ' = UE.getEditor(\"question'
							+ i
							+ '\");<\/script> ');
			var option = jsonObj.questionOption;
			for ( var j = 1; j < option.length + 1; j++) {
				var jsonOption = option[j - 1];
				var desc = jsonOption.desc;
				var index = jsonOption.index;
				$('#child'+i).append('<div class="row" id=\"optionRow'+i+j+'\"><div id=\"choiceFlag'+i+j+'\" class=\"control-label col-md-1 \" style=\"display: inline-block;float: left;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;选项标号:&nbsp;<input type=\"text\" class=\"form-control\" id=\"optionFlag'+i+j+'\" value=\"'+index+'\" style=\"width: 35px;height:32px;display: inline-block;"\/>&nbsp;&nbsp;&nbsp;&nbsp;内容:&nbsp;</div><textarea id=\"optionContext'+i+j+'\" name=\"optionContext'+i+j+'\" style=\"width: 488px;height:32px;display: inline-block;margin-left:123px;float:left;"></textarea><div style=\"display: inline-block;float: left;height:50px;width:300px; \">&nbsp;&nbsp;<input type=\"button\" value=\"删除选项\" class=\"btn btn-primary red\" onclick=\"deleteChoice(\''+i+j+'\');\"/></div><script type=\"text\/javascript\">$(\'#optionContext'+i+j+ '\').val(\''+desc+ '\');var d'+i+j+ ' = UE.getEditor(\"optionContext'+i+j+ '\",{toolbars: [[\'fullscreen\', \'source\', \'undo\', \'redo\', \'insertimage\']]});<\/script>');
			}
		}
		$('#material').val(material);
	}
	//保存数据
	function saveExamination() {
		var examination = new Object();
		var smallQuestions = new Array();
		var num = $('#main span.num').length;
		//材料
		examination.material = materialUE.getContent();
		for ( var u = 0; u < num; u++) {
			//alert(u);
			var nameValue = $('#main span.num').eq(u).attr('id');
			//alert(nameValue);
			if (nameValue != undefined) {
				var name = nameValue.substring(5, nameValue.length);
				//alert("name"+name);
				var smallQuestion = new Object();
				var questionOption = new Array();
				var numDiv = $('#child' + name + ' div.row').length;
				//smallQuestion.material1 = materialUE.getContentTxt();
				//alert("1"+materialUE.getContentTxt());
				//小问题描述UE.getEditor('question' + name).getContent();
				smallQuestion.questionDesc = UE.getEditor('question' + name)
						.getContent();
				//smallQuestion.questionDesc1 = UE.getEditor('question'+name).getContentTxt();
				//alert("2"+UE.getEditor('question'+name).getContentTxt());
				//alert(UE.getEditor('question'+name).getContentTxt());
				smallQuestion.optionCount = num;
				//alert(num);
				//alert(numDiv);
				for ( var y = 0; y < numDiv; y++) {
					//alert("第二个for");
					var divId = $('#child' + name + ' div.row').eq(y)
							.attr('id');
					var index = divId.substring(9, divId.length);
					//alert("index"+index);
					var oneOption = new Object();
					//alert(index);
					//alert(UE.getEditor('optionContext'+index).getContent());
					oneOption.index = $('#optionFlag' + index).val();
					oneOption.desc = UE.getEditor('optionContext' + index)
							.getContent();
					//oneOption.txt = UE.getEditor('optionContext'+index).getContentTxt();
					questionOption[y] = oneOption;
				}
				smallQuestion.questionOption = questionOption;
				smallQuestions[u] = smallQuestion;
			}
		}
		examination.samllQuesions = smallQuestions;
		var json = JSON.stringify(examination);
		//alert(json);
		top.index_frame.work_main.document.getElementById("content_json").value = encodeURIComponent(json);
		queryTestQuestions(json);
		//$.closeFromInner();
	}
	//关闭本页面传值到父页面转换为文本
	function queryTestQuestions(json) {
		//var json = {"samllQuesions":[{"material":"1111","questionDesc":"qqqq","optionCount":3,"questionOption":[{"index":"a","desc":"111"},{"index":"b","desc":"222"}]},{"material":"1111","questionDesc":"aaaaaa","optionCount":3,"questionOption":[{"index":"a","desc":"333"},{"index":"b","desc":"444"},{"index":"c","desc":"555"}]},{"material":"1111","questionDesc":"wwwww","optionCount":3,"questionOption":[{"index":"a","desc":"666"},{"index":"b","desc":"777"}]}]};
		json = eval('(' + json + ')');
		var material = "";
		if (json.material != "" && json.material != undefined) {
			material = "材料：" + json.material;
		}
		var questionText = '';
		for ( var i = 0; i < json.samllQuesions.length; i++) {
			var jsonObj = json.samllQuesions[i];
			var questions = jsonObj.questionDesc;
			questionText = questionText + questions;
			var option = jsonObj.questionOption;
			for ( var j = 0; j < option.length; j++) {
				var jsonOption = option[j];
				var index = jsonOption.index;
				var desc = jsonOption.desc;
				desc = desc.replace('p', 'span');
				desc = desc.replace('/p', '/span');
				var txt = index + "." + desc + '<br>';
				questionText = questionText + txt;
			}
		}
		top.index_frame.work_main.UE.getEditor('questionJson').setContent(
				material + questionText);
		top.index_frame.work_main.document
				.getElementById("analysis_description").value = analysisDescriptionUE
				.getContent();
		top.index_frame.work_main.document.getElementById("answer_description").value = answer_descriptionUE
				.getContent();
		top.index_frame.work_main.document.getElementById("answer_json").value = answer_jsonUE
				.getContent();
		art.dialog.close();//关闭弹出框
	}
	// 	function getInputContext() {
	// 		var smallQuestion = new Object();
	// 		smallQuestion.questionDesc = "dsfds";
	// 		smallQuestion.optionCount = "3";
	// 		var questionOption = new Array();
	// 		var oneOption = new Object();
	// 		oneOption.index = "A";
	// 		oneOption.desc = "eeeee";
	// 		questionOption[0] = oneOption;
	// 		var oneOption = new Object();
	// 		oneOption.index = "B";
	// 		oneOption.desc = "rrrrrr";
	// 		questionOption[1] = oneOption;
	// 		smallQuestion.questionOption = questionOption;
	// 		var json = JSON.stringify(smallQuestion);
	// 		alert(json);
	// 		// 		for(var a=0;a<=i;a++){
	// 		// 			alert(document.getElementById("optionFlag"+a).value);
	// 		// 		}

	// 	}
</script>

</head>
<body>
	<div id="product_shift_out_{m}"></div>
	<div id="fakeFrame" class="container-fluid by-frame-wrap">
		<ul class="page-breadcrumb breadcrumb">
			<li><a href="###">创建试题</a> <i class="fa fa-angle-right"></i></li>
		</ul>
	</div>
	<!-- <div class="panel-body height_remain" > -->
	<!-- 			<div align="left"> -->
	<%-- 					<sec:authorize url="${path}/target/upd.action"> --%>
	<!-- 						<input class="btn btn-primary" type="button" value="填空题" onclick="javascript:batchRetry();"/> -->
	<%-- 					</sec:authorize> --%>
	<%-- 					<sec:authorize url="${path}/target/upd.action"> --%>
	<!-- 						<input class="btn btn-primary" type="button" value="选择题" onclick="javascript:allotHost();"/> -->
	<%-- 					</sec:authorize> --%>
	<%-- 					<sec:authorize url="${path}/target/upd.action"> --%>
	<!-- 						<input class="btn btn-primary" type="button" value="判断题" onclick="javascript:pageAll();"/> -->
	<%-- 					</sec:authorize> --%>
	<%-- 					<sec:authorize url="${path}/target/upd.action"> --%>
	<!-- 						<input class="btn btn-primary" type="button" value="测试值" onclick="javascript:test();"/> -->
	<%-- 					</sec:authorize> --%>
	<!-- 			</div> -->
	<!-- </div> -->
	<%-- 		<input type="hidden" id="questionJsons" name="questionJsons" value="${param.questionJsons}"/> --%>
	<input type="hidden" id="questionType" name="questionType"
		value="${param.questionType}" />
	<textarea name="content_json" style="display: none" id="content_json"
		class="form-control">${param.content_json}</textarea>
	<textarea name="analysis_description1" style="display: none"
		id="analysis_description1" class="form-control">${param.analysis_description}</textarea>
	<textarea name="answer_description1" style="display: none"
		id="answer_description1" class="form-control">${param.answer_description}</textarea>
	<textarea name="answer_json1" style="display: none" id="answer_json1"
		class="form-control">${param.answer_json}</textarea>
	<div>
		<div class="row">
			<div>
				<label class="control-label col-md-1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;材料：</label>
			</div>
			<div style="width: 915px; display: inline-block;">
				<textarea id="material" name="material"
					style="width: 98%; height: 150px;"></textarea>
			</div>
			<div class="col-md-12">
				<div class="control-label col-md-3"
					style="margin-top: 10px; margin-bottom: 10px;">
					<input type="button" value="添加问题" id="addQuestion"
						class="btn btn-primary" onclick="addButton();" />
				</div>
			</div>
			<div id="main"></div>
			<div style="margin-top: 10px;">
				<label class="control-label col-md-1">&nbsp;&nbsp;答案内容：</label>
			</div>
			<div style="width: 900px; display: inline-block;">
				<textarea id="answer_json" name="answer_json"
					style="width: 98%; height: 150px;"></textarea>
			</div>
			<div style="margin-top: 10px;">
				<label class="control-label col-md-1">&nbsp;&nbsp;答案描述：</label>
			</div>
			<div style="width: 900px; display: inline-block;">
				<textarea id="answer_description" name="answer_description"
					style="width: 98%; height: 150px;"></textarea>
			</div>
			<div style="margin-top: 10px;">
				<label class="control-label col-md-1">&nbsp;&nbsp;解析描述：</label>
			</div>
			<div style="width: 900px; display: inline-block;">
				<textarea id="analysis_description" name="analysis_description"
					style="width: 98%; height: 150px;"></textarea>
			</div>
			<div align="center">
				<input type="button" value="保存" class="btn btn-primary"
					onclick="saveExamination();" /> <input class="btn btn-primary"
					type="button" value="关闭" onclick="javascript:$.closeFromInner();" />
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
	var materialUE = UE.getEditor('material');
	var analysisDescriptionUE = UE.getEditor('analysis_description');
	var answer_descriptionUE = UE.getEditor('answer_description');
	var answer_jsonUE = UE.getEditor('answer_json');
</script>
</html>