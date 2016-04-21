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
	var score = '${param.score}';
	var returnJson = '${returnJson}';
	var module='${module}';
	$(function() {
		//window.location.href = "${path}/bres/papersRead.action?type=${param.type}&objectId="+$('#objectId').val();
		if(returnJson!=''){
			jsonArray1 = eval('(' + returnJson + ')');
			allPapers(jsonArray1);	
		}else{
			paperRead();
		}
	});
	
	function paperRead() {
		$.ajax({
			url : "${path}/bres/papersRead.action?type=${param.type}&objectId="
					+ $('#objectId').val() + "&json="
					+ encodeURIComponent($('#json').val()),
			type : 'post',
			datatype : 'text',
			success : function(returnValue) {
				if (returnValue != undefined || returnValue != '') {
					var jsonOne = replaceAllString(returnValue, "\\", "");
					data = eval('(' + jsonOne + ')');
					if (data.allPaper == undefined || data.allPaper == '') {
						var lastValue = decodeURIComponent(data.resourceJson);
						$('#content_json').val(lastValue);
						queryTestQuestions();
					} else {
						allPapers(data.allPaper);
					}
				} else {
					$.alert("不能预览");
				}
			}
		});
	}
	/**
	 * 替换
	 * @param str 源字符串
	 * @param sptr 要替换的字符串
	 * @param nstr 替换后的字符串
	 * @returns
	 */
	function replaceAllString(str, sptr, nstr) {
		if (str != null && str != "") {
			while (str.indexOf(sptr) != -1) {
				str = str.replace(sptr, nstr);
			}
		}
		return str;
	}
	//关闭本页面传值到父页面转换为文本

	function queryTestQuestions() {
		//var json = {"samllQuesions":[{"material":"1111","questionDesc":"qqqq","optionCount":3,"questionOption":[{"index":"a","desc":"111"},{"index":"b","desc":"222"}]},{"material":"1111","questionDesc":"aaaaaa","optionCount":3,"questionOption":[{"index":"a","desc":"333"},{"index":"b","desc":"444"},{"index":"c","desc":"555"}]},{"material":"1111","questionDesc":"wwwww","optionCount":3,"questionOption":[{"index":"a","desc":"666"},{"index":"b","desc":"777"}]}]};
		var json = $('#content_json').val();
		var jsons = json.split("+");
		var plusQuestion = '';
		for ( var u = 0; u < jsons.length; u++) {
			var questionText = '';
			json = eval('(' + jsons[u] + ')');
			var material = "";
			if (json.material != undefined) {
				material = (u + 1) + "、" + json.material+"</br>";
			} else {
				material = (u + 1);
			}
			for ( var i = 0; i < json.samllQuesions.length; i++) {
				var jsonObj = json.samllQuesions[i];
				var questions = jsonObj.questionDesc;
				questionText = questionText + questions +score+ "</br>";
				;
				var option = jsonObj.questionOption;
				for ( var j = 0; j < option.length; j++) {
					var jsonOption = option[j];
					var index = jsonOption.index;
					var desc = jsonOption.desc;
					var txt = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + index
							+ ":" + desc;
					questionText = questionText + txt + "</br>";
				}
			}
			plusQuestion = plusQuestion + material + questionText + "</br>";
		}
		plusQuestion = replaceAllString(plusQuestion, "p>", "y0");
		plusQuestion = replaceAllString(plusQuestion, "y0", "span>");
		$('#paperFlag').html(plusQuestion);
	}

	function allPapers(allPaper) {
		var plusQuestion = '';
		var questionName = '';
		var material = '';
		var testType = "";
		var allQuestions = "";
		var score = "";
		//var json = {"samllQuesions":[{"material":"1111","questionDesc":"qqqq","optionCount":3,"questionOption":[{"index":"a","desc":"111"},{"index":"b","desc":"222"}]},{"material":"1111","questionDesc":"aaaaaa","optionCount":3,"questionOption":[{"index":"a","desc":"333"},{"index":"b","desc":"444"},{"index":"c","desc":"555"}]},{"material":"1111","questionDesc":"wwwww","optionCount":3,"questionOption":[{"index":"a","desc":"666"},{"index":"b","desc":"777"}]}]};
		for ( var y = 0; y < allPaper.length; y++) {
			questionName = allPaper[y].name;
			score = allPaper[y].score;
			if(score == undefined){
				score = "";
			}
			plusQuestion = '';
			testType = "<span style=\"font-family:宋体; color:black; font-size:16px; font-weight:bold\">"
					+ questionName +score+ "</span></br>";
			for ( var u = 0; u < allPaper[y].data.length; u++) {
				var jsonNum = decodeURIComponent(allPaper[y].data[u]);
				json = eval('(' + jsonNum + ')');
				var questionText = '';
				if (json.material != undefined && json.material != '') {
					material = (u + 1) + "、" + json.material+"</br>";
				} else {
					material = (u + 1)+ "、";
				}
				for ( var i = 0; i < json.samllQuesions.length; i++) {
					var jsonObj = json.samllQuesions[i];
					var option = jsonObj.questionOption;
					var questions = jsonObj.questionDesc;
					questionText = questionText + questions + "<br/>";
					for ( var j = 0; j < option.length; j++) {
						var jsonOption = option[j];
						var index = jsonOption.index;
						var desc = jsonOption.desc;
						var txt = index + ". " + desc + '<br>';
						questionText = questionText + txt;
					}
				}
				plusQuestion = plusQuestion + material + questionText;
			}
			allQuestions += testType + plusQuestion;
		}
		allQuestions = replaceAllString(allQuestions, "p>", "y0");
		allQuestions = replaceAllString(allQuestions, "y0", "span>");
		$('#paperFlag').html(allQuestions);
		//UE.getEditor('questions').setContent(material+questionText);
	}
	function printPaper() {
		$('#hide1').hide();
		$('#hide').hide();
		window.print();
	}
</script>

</head>
<style type="text/css">
	#paperFlag{
	border: #EEEEEE solid 3px;
	position:relative; 
	top:1px;
	/* height:90%; */
	width:900px; 
	margin-left: auto;
	margin-right: auto;
	background-color: white;
	}
		#hide1{
	position:relative; 
	top:0px;
	height:40px; 
 	width:900px;  
	margin-left: auto;
	margin-right: auto;
	background-color: white;
	}
</style>
<body>
	<div id="fakeFrame">
		<ul class="page-breadcrumb breadcrumb">
		</ul>
	</div>
	<div id="hide1">
		<input class="btn btn-default red" type="button" value="打印"
			onclick="printPaper();" />
	</div>
	<div class="col-md-12" style="display: none">
		<div class="form-group">
			<div class="col-md-8">
				<textarea name="extendMetaData.extendMetaDatas['content_json']"
					id="content_json" class="form-control" rows="5"></textarea>
			</div>
		</div>
	</div>
	<input type="hidden" id="objectId" name="objectId"
		value="${param.objectId }" />
		<textarea name="json" style="display:none" id="json" class="form-control">${param.json}</textarea>
	<textarea name="content_json" style="display: none" id="content_json"
		class="form-control">${param.content_json}</textarea>
<!-- 	<div> -->
<!-- 		<div class="row"> -->
<!-- 			<div> -->
<!-- 				<label class="control-label col-md-1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;试题：</label> -->
<!-- 			</div> -->
<!-- 			<div style="width: 915px; display: inline-block;"> -->
<!-- 				<textarea id="questions" name="questions" -->
<!-- 					style="width: 98%; height: 150px;"></textarea> -->
<!-- 			</div> -->
<!-- 		</div> -->
<!-- 	</div> -->
	<div id="paperFlag">
	
	</div>
	<div align="center" id="hide">
		<input class="btn btn-primary" type="button" value="关闭"
			onclick="javascript:$.closeFromInner();" />
	</div>
</body>
<!-- <script type="text/javascript">  -->
<!--  var questionsUE = UE.getEditor('questions',{toolbars: [['fullscreen']],readonly:true}); -->
<!--  </script> -->
</html>