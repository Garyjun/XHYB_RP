<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<style type="text/css">
div{border-top:0px solid #000} 
 .colo2,.colo1{ 
	float:left;
	font-size:14px; 
/* 	color:#f00; */ 
	width:200px; 
	height:200px; 
} 
.colo2{
border: #EEEEEE solid 1px;
position:relative; 
top:10px; 
height:500px;
width:800px; 
margin-left: 70px;
margin-right: auto;
border-radius:10px;
/* background-color: white; */
}
.colo1{
margin-left: 50px;
margin-right: auto;
}
    #sticky_footer
{
    width: 100%;
    background-color: #222222;    
        opacity:.8;
    filter:alpha(opacity=80);
    -moz-opacity: 0.8;
    position: fixed;
    _position:absolute;
    overflow: hidden;
    height:38px;
    bottom: 0;
    z-index: 99999;
    line-height:38px;
    float:left;
    _top:expression(eval(document.documentElement.scrollTop+document.documentElement.clientHeight-this.offsetHeight-(parseInt(this.currentStyle.marginTop,10)||0)-(parseInt(this.currentStyle.marginBottom,10)||0)));
}
#wrapperfooter{width:960px;margin:0 auto;border-top:0px #333333 solid;padding:0;color:red}
</style>
<title>试卷模板</title>
<script type="text/javascript">
	$(function(){
		$.ajax({
			type:"post",
			async:false,
			url:"${path}/system/testTemplate/paperArray.action?paperId="+$('#paperId').val(),
			success:function(data){
				data = eval('('+data+')');
				if(data.itemList!=""){
					var itemList = eval('('+data.itemList+')');
					 for(var i=0;i<itemList.length;i++) {
 						 var divmain = $("<div class='form-group'></div>");
 						 divmain.appendTo($("#testTypeList"));
 						 var label = $("<label class='col-xs-4 control-label text-right'>试题类型:</label>");
  						 $(divmain).append(label);
 						 var select = $("<div id='myId"+i+"' class='col-md-1'><select id='mySelect"+i+"'name='mySelect' class='form-control' style='width:120px;' ><div id='paperDiv"+i+"'class='col-md-1'></div></div>");
 						 divmain.append(select);
  						 var div = "<option value='"+itemList[i].testTypeKey+"'>"+itemList[i].testType+"</option>";
						 $("#mySelect"+i).append(div);
						  var inputs =  '<input type=\"button\" value=\"选择\" class=\"btn btn-primary blue\" onclick=\"addPaper(\''+itemList[i].testTypeKey+"','"+i+'\');\"/>';
						 $("#myId"+i).append(inputs);
					 }
				}
			}
		});
	});
// 		var divmain = $("<div class='form-group'></div>");
// 		divmain.appendTo($("#testTypeList"));
// 		var label = $("<label class='col-xs-4 control-label text-right'>试题类型:</label>");
// 		label.appendTo(divmain);
// 		var div = $("<div class='col-xs-3'><app:select name='testItem' indexTag='testType' id='testItem' selectedVal='${testItem}' headName='' headValue='' /></div>");
// 		div.appendTo(divmain);
// 		var buttonadd = $("<button type='button' class='btn btn-info' onclick='showAdd()'></button>");
// 		buttonadd.appendTo(divmain);
// 		var span = $("<span class='glyphicon glyphicon-plus'></span>");
// 		span.appendTo(buttonadd);
// 		var buttondel = $("<button type='button' class='btn btn-warning' onclick='del(this)'></button>");
// 		buttondel.appendTo(divmain);
// 		var spandel = $("<span class='glyphicon glyphicon-minus'></span>");
// 		spandel.appendTo(buttondel);
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
	function addPaper(id,placeFlag){
		var url = "${path}/system/testTemplate/questionRes.jsp?paperId="+$('#paperId').val()+"&queryType=0&libType=2&type=T14&module=ST&placeFlag="+placeFlag;
		$('#work_main2').attr('src',url);
		//$.openWindow("${path}/system/testTemplate/questionRes.jsp?paperId="+$('#paperId').val()+"&queryType=0&libType=2&type=T14&module=ST&placeFlag="+placeFlag,'试题选择',900,500);
	}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap "
		style="height: 100%;">
			<form:form action=""
				id="form" modelAttribute="frmTestTemplateItem"
				method="post" class="form-horizontal" role="form">
<!-- 				<div class="form-group"> -->
<!-- 					<label class="col-xs-4 control-label text-right">试题类型:</label> -->
<!-- 					<button type="button" class="btn btn-info" onclick="showAdd()"> -->
<!-- 						<span class="glyphicon glyphicon-plus"></span> -->
<!-- 					</button> -->
<!-- 					<button type="button" class="btn btn-warning" onclick="del(this)"> -->
<!-- 						<span class="glyphicon glyphicon-minus"></span> -->
<!-- 					</button> -->
<!-- 					<div class="col-xs-3"> -->
<%-- 						<app:select name="testItem" indexTag="testType" id="testItem" --%>
<%-- 							selectedVal="${testItem}" headName="" headValue="" /> --%>
<!-- 					</div> -->
<!-- 				</div> -->
				 <input type="hidden" id="paperId" name="paperId" value="${param.paperId }" />
				 <input type="hidden" id="paperName" name="paperName" value="${param.paperName }" />
				 <input type="hidden" id="module" name="module" value="${param.module }" />
				 <input type="hidden" id="libType" name="libType" value="${param.libType }" />
				 <input type="hidden" id="queryType" name="queryType" value="${param.queryType }" />
				 <input type="hidden" id="type" name="queryType" value="${param.type }" />
				<div id="testTypeList" class="colo1">

				</div>
				<div class="colo2">
						<iframe id="work_main2" name="work_main2" width="100%" height="100%" frameborder="0" src=""></iframe>
				</div>
				   <div id="sticky_footer">
					<div id="wrapperfooter">
					</div>
					</div>
				<input id="tijiao" type="button" style="margin-left:400px" value="保存" class="btn btn-primary" onclick="save();"/>
			</form:form>
	</div>
</body>
</html>