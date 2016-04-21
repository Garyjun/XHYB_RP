<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>编辑</title>
<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/ueditor/ueditor.config.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/ueditor/ueditor.all.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/ueditor/third-party/codemirror/codemirror.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/ueditor/third-party/zeroclipboard/ZeroClipboard.js"></script>
<script type="text/javascript" charset="utf-8" src="${path}/appframe/plugin/ueditor/kityformula-plugin/addKityFormulaDialog.js"></script>
<script type="text/javascript" charset="utf-8" src="${path}/appframe/plugin/ueditor/kityformula-plugin/getKfContent.js"></script>
<script type="text/javascript" charset="utf-8" src="${path}/appframe/plugin/ueditor/kityformula-plugin/defaultFilterFix.js"></script>    
<script type="text/javascript" charset="utf-8" src="${path}/appframe/plugin/ueditor/lang/zh-cn/zh-cn.js"></script>
<script type="text/javascript">
//保存数据
function saveExamination() {
	var questionValue = top.index_frame.work_main.UE.getEditor('content_json');
//	questionValue.value = questionsUE.getContent();
	//var c = UE.getEditor(questionValue); 
	//c.ready(function() {
		questionValue.setContent(questionsUE.getContent());
	//top.index_frame.work_main.document.getElementById("questionHtml").setContent(questionsUE.getContentTxt());
    	top.index_frame.work_main.UE.getEditor('analysis_description').setContent(analysisDescriptionUE.getContent());
    	top.index_frame.work_main.UE.getEditor('answer_description').setContent(answer_descriptionUE.getContent());
    	top.index_frame.work_main.UE.getEditor('answer_json').setContent(answer_jsonUE.getContent());
    	$.closeFromInner();
}
$(function(){
	alert($('#content_json').val());
	$('#questions').val($('#content_json').val());
	 $('#answer_json').val($('#answer_json1').val());
	 $('#answer_description').val($('#answer_description1').val());
	 $('#analysis_description').val($('#analysis_description1').val());
  });
</script>
</head>
<body>
<div id="product_shift_out_{m}"> </div>
<div id="fakeFrame" class="container-fluid by-frame-wrap">
	<ul class="page-breadcrumb breadcrumb">
        <li>
            <a href="###">创建试题</a>
            <i class="fa fa-angle-right"></i>
        </li>
    </ul>
</div>
		<input type="hidden" id="questionType" name="questionType" value="${param.questionType}"/>
		<textarea name="content_json" style="display:none" id="content_json" class="form-control">${param.content_json}</textarea>
		<textarea name="analysis_description1" style="display:none" id="analysis_description1" class="form-control">${param.analysis_description}</textarea>
		<textarea name="answer_description1" style="display:none" id="answer_description1" class="form-control">${param.answer_description}</textarea>
		<textarea name="answer_json1" style="display:none" id="answer_json1" class="form-control">${param.answer_json}</textarea>
<div >
		<div class="row">
<!-- 			<div ><label class="control-label col-md-1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;材料：</label></div> -->
<!-- 					<div style="width:915px;display: inline-block;" > -->
<!-- 					<textarea id="material" name="material" style="width: 98%;height:150px;"></textarea> -->
<!-- 			</div> -->
					<div style="margin-top:10px;"><label class="control-label col-md-1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;问题：</label></div>
					<div style="width:900px;display: inline-block;" >
					<textarea id="questions" name="questions" style="width: 98%;height:150px;"></textarea>
					</div>
<!-- 				<div  id="main"> -->
				
<!-- 				</div> -->
					<div style="margin-top:10px;"><label class="control-label col-md-1">&nbsp;&nbsp;答案内容：</label></div>
					<div style="width:900px;display: inline-block;" >
					<textarea id="answer_json" name="answer_json" style="width: 98%;height:150px;"></textarea>
					</div>
					<div style="margin-top:10px;"><label class="control-label col-md-1">&nbsp;&nbsp;答案描述：</label></div>
					<div style="width:900px;display: inline-block;" >
					<textarea id="answer_description" name="answer_description" style="width: 98%;height:150px;"></textarea>
					</div>
					<div style="margin-top:10px;"><label class="control-label col-md-1">&nbsp;&nbsp;解析描述：</label></div>
					<div style="width:900px;display: inline-block;" >
					<textarea id="analysis_description" name="analysis_description" style="width: 98%;height:150px;"></textarea>
					</div>
			<div align="center">
			<input type="button" value="保存"class="btn btn-primary" onclick="saveExamination();" />
				<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
			</div>
		</div>
	</div>
</body>
 <script type="text/javascript"> 
 //var materialUE = UE.getEditor('material');
 var analysisDescriptionUE = UE.getEditor('analysis_description');
 var answer_descriptionUE = UE.getEditor('answer_description');
 var answer_jsonUE = UE.getEditor('answer_json');
 var questionsUE = UE.getEditor('questions');
 </script> 
</html>