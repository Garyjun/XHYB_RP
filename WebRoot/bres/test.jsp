<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>编辑</title>
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/ueditor/themes/default/css/ueditor.css"/>
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/ueditor/themes/iframe.css"/>
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/ueditor/third-party/codemirror/codemirror.css"/>
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
	var i=0;
	function addSmallQuestion(){
		 $(" <div class='col-md-12' id ='ccc1'><div class='form-group'><label class='control-label col-md-2'>问题2：</label><div class='col-md-10'><input type='text' name='content_json1' id='content_json1' class='form-control' style='width: 60%;'/></div></div></div>").appendTo("#ccc");
	}
	function addChoice(){
	//	alert();
// 		 $("<div class='col-md-12'>选项标号<input type='text' id='optionFlag"+i+"'>选项内容<input type='text'  id='optionContext"+i+"'><br></div>").appendTo("#ccc");
// 		 i++;	
	}
	function getInputContext(){
		var smallQuestion = new Object();
		smallQuestion.questionDesc = "dsfds";
		smallQuestion.optionCount = "3";
		var questionOption = new Array();
		var oneOption = new Object();
		oneOption.index= "A";
		oneOption.desc = "eeeee";
		questionOption[0] = oneOption;
		var oneOption1 = new Object();
		oneOption1.index= "B";
		oneOption1.desc = "rrrrrr";
		questionOption[1] = oneOption1;
		smallQuestion.questionOption = questionOption;
		var json = JSON.stringify(smallQuestion); 
		alert(json);
// 		for(var a=0;a<=i;a++){
// 			alert(document.getElementById("optionFlag"+a).value);
// 		}
		
	}
</script>
</head>
<body>

					<div  style="width: 83.33333333%;display: inline-block">
							 <div id="editor" style="width:100%;height:200px;"></div>
					</div>
			
 <script type="text/javascript"> 
 var ue = UE.getEditor('editor');
 ue.ready(function() {
	//    ue.setContent('<p>我企鹅网球场<img class="kfformula" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAKEAAAArCAYAAAAQeugIAAADL0lEQVR4Xu2aPa8NURSG7/0rFH6AgkIjKiFBhRCNj5aamkqB0kdBCCoKOhIRBT9B4Rdo9bxvsrdMTs6Zmcxda691z34nWbk398y8e308e689+9zdHV3KQHAGdoPH1/DKwI4gFAThGRCE4SWQA4JQDIRnQBCGl0AOCEIxEJ4BQRheAjkgCMVAeAYEYXgJ5IAgFAPhGRCE4SWQA4JQDIRnQBCGl0AOtIbwKlL+FHYH9hX2zagEXrpG7klmLAOtIaQvp2DXYGdhT2AvjGD00hVBzhmIgLCGRGjOwK7D3pcV8qNBvF66Bq5JYl0GIiGs/hzDLydhtwuMH/DzmUG5LHRrm1/qzr2y9Vj3vKf2Un9DnssA4TBwgsji/Ia9hj00yoqXrpF7bjIXoMytz4kywueS16lJ/hL3XXbzakU4G4TVPYLINn2krCRcUSwuL10L36w1CCAnMrc6X2B/YMdhl2A/YA9gb9YMyg5yH3bU2qFNelkhrP5yf/cIdgB2GmaxZ6S2l26rus0Z51eZwKugEbIrsLoXfzuAkZOUXYMr5o05g1jckxnCmwjwIsx6NfTStaiHpcb3idVseJowHJcnFs0A5MAZIeRMvAtjy2BCpvYvcwu3RJfAsm0tvcZeTDy162rPzsGV71AJYFMuuQLy+gnj2S0Bteo6k7nLAuFYi5gMYuQGL929+NTy2ccYjG13eM05DptaRU1jiIaQM44tl5vlVzBupC1moJeuafKdxepKO1yNazfg0Mz7pheT5/jsoLN//+WjIOSb23mY9bcmXrqt6mE5zieIcUvDr0iHF7sDIePLHj9bPXl4V24+Z+nMmFZrCAnJLZj1y4aXbqs6eIzzF6Kb6luPYViHeoRTV0f+zfIkYjK21hAy+MMwq0PoGqCXrue3Gp7azMucA2f6wLY8PMzmP5isa9OTMC29oTWES/3Uc1ucAUG4xcXdL6EJwv1SqS32szWEe9kHjW2WvXS3uPR5QmsNYZ7I5UmaDAjCNKXo1xFB2G/t00QuCNOUol9HBGG/tU8TuSBMU4p+HRGE/dY+TeSCME0p+nVEEPZb+zSRC8I0pejXEUHYb+3TRP4PslKJLG69m9AAAAAASUVORK5CYII=" data-latex="\geq \geq \equiv \equiv \approx "/><img src="/bsfw0112/ueditor/jsp/upload/image/20150115/1421314183812092555.jpg" title="1421314183812092555.jpg" alt="1_130606082414_1.jpg"/></p>');
	//    ue.setHide(); //隐藏编辑器
	//    ue.setShow(); //显示编辑器
 });
 function addChoice1(){
		alert(ue.getContent());
//		 $("<div class='col-md-12'>选项标号<input type='text' id='optionFlag"+i+"'>选项内容<input type='text'  id='optionContext"+i+"'><br></div>").appendTo("#ccc");
//		 i++;	
	}
 </script> 
</body>
</html>