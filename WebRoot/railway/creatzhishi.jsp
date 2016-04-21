<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>创建知识点</title>
<style type="text/css">
	
	.con_L_search {
		background-color: rgb(242, 245, 248);
		border-bottom-width: 1px;
		border-bottom-style: solid;
		border-bottom-color: #9acccd;
		border-left-color: #9acccd;
		border-left-style: solid;
		border-left-width: 1px;
		border-right-color: #9acccd;
		border-right-style: solid;
		border-right-width: 1px;
		border-top-color: #9acccd;
		border-top-style: solid;
		border-top-width: 1px;
	}

	.yui-skin-sam {
		background-color: rgb(250, 250, 250);
		background-position-x: 0px;
		background-position-y: 0px;
		background-repeat: repeat-x;
		border-bottom-style: none;
		border-bottom-width: medium;
		border-left-style: none;
		border-left-width: medium;
		border-right-style: none;
		border-right-width: medium;
		border-top-style: none;
		border-top-width: medium;
		color: #000;
		font-family: 宋体;
		font-size: 12px;
		font-style: normal;
		font-variant: normal;
		font-weight: normal;
		line-height: normal;
		overflow: auto;
	}
</style>
<script type="text/javascript">
	function addtable1() {
		var table = document.getElementById("table1");
		var size= table.insertRow(table.rows.length);
		var length=table.rows.length;
		var rowID = "";
		if(length>3){
			var lastInput = $("#table1").find("input:last").attr("id");
			var lastInputId = lastInput.substring(4,lastInput.length);
			rowID = parseInt(lastInputId) + 1;
		}else{
			rowID = length-1;
		}
		 
		//添加序列号
		var newPrefixTD=size.insertCell(0);
		newPrefixTD.align="center"; 
		newPrefixTD.height="30px";
		var checkbox = "<input size='30px;' type='text' readonly='readonly' name='Sname" + rowID + "' id='Sname"+rowID+"'/>";
		newPrefixTD.innerHTML = checkbox;
		var newPrefixTD1=size.insertCell(1);
		newPrefixTD1.align="center";
		//newPrefixTD.innerHTML = "关键字："+rowID;
		 var prefix = "<input size='70px;' name='Surl" + rowID + "' id='Surl" + rowID + "' type='text' readonly='readonly'/>";
		 newPrefixTD1.innerHTML = prefix;
		 var newPrefixTD2=size.insertCell(2);
		 newPrefixTD2.align="center";
		 var prefixs = "<img alt='选择' title='选择' src='${path }/railway/image/search.gif' onclick='openkaichuang1("+rowID+");'>&nbsp;<img alt='清除' title='清除' src='${path }/railway/image/lookupClear.gif' onclick='deltable1(this);'>";
		 newPrefixTD2.innerHTML = prefixs;
	}
	function addtable2() {
		var table = document.getElementById("table2");
		var size= table.insertRow(table.rows.length);
		var length=table.rows.length;
		var rowID = "";
		if(length>3){
			var lastInput = $("#table2").find("input:last").attr("id");
			var lastInputId = lastInput.substring(4,lastInput.length);
			rowID = parseInt(lastInputId) + 1;
		}else{
			rowID = length-1;
		}
		//添加序列号
		var newPrefixTD=size.insertCell(0);
		newPrefixTD.align="center"; 
		newPrefixTD.height="30px"; 
		var checkbox = "<input size='30px;' type='text' readonly='readonly' name='Xname" + rowID + "' id='Xname"+rowID+"'  />";
		newPrefixTD.innerHTML = checkbox;
		var newPrefixTD1=size.insertCell(1);
		newPrefixTD1.align="center";
		//newPrefixTD.innerHTML = "关键字："+rowID;
		 var prefix = "<input size='70px;' name='Xurl" + rowID + "' id='Xurl" + rowID + "' type='text' readonly='readonly'/>";
		 newPrefixTD1.innerHTML = prefix;
		 var newPrefixTD2=size.insertCell(2);
		 newPrefixTD2.align="center";
		 var prefixs = "<img alt='选择' title='选择' src='${path }/railway/image/search.gif' onclick='openkaichuang2("+rowID+");'>&nbsp;<img alt='清除' title='清除' src='${path }/railway/image/lookupClear.gif' onclick='deltable2(this);'>";
		 newPrefixTD2.innerHTML = prefixs;
	}
	function addtable3() {
		var table = document.getElementById("table3");
		var size= table.insertRow(table.rows.length);
		var length=table.rows.length;
		var rowID = "";
		if(length>3){
			var lastInput = $("#table3").find("input:last").attr("id");
			var lastInputId = lastInput.substring(4,lastInput.length);
			rowID = parseInt(lastInputId) + 1;
		}else{
			rowID = length-1;
		}
		//添加序列号
		var newPrefixTD=size.insertCell(0);
		newPrefixTD.align="center"; 
		newPrefixTD.height="30px";
		var checkbox = "<input size='30px;' type='text' readonly='readonly' name='Gname" + rowID + "' id='Gname"+rowID+"'/>";
		newPrefixTD.innerHTML = checkbox;
		
		var newPrefixTD1=size.insertCell(1);
		newPrefixTD1.align="center";
		var prefix = "<input size='50px;' name='Gurl" + rowID + "' id='Gurl" + rowID + "' type='text' readonly='readonly'/>";
		newPrefixTD1.innerHTML = prefix;
		
		var newPrefixTD2=size.insertCell(2);
		newPrefixTD2.align="center";
		var prefi="<select name='Gtype" + rowID + "' id='Gtype" + rowID + "'>";
		prefi += "<option value='contain'>包含</option>";
		prefi += "<option value='depend'>依赖</option>";
		prefi +="</select>";
		newPrefixTD2.innerHTML = prefi;
		
		var newPrefixTD3=size.insertCell(3);
		newPrefixTD3.align="center";
		var prefixs = "<img alt='选择' title='选择' src='${path }/railway/image/search.gif' onclick='openkaichuang3("+rowID+");'>&nbsp;<img alt='清除' title='清除' src='${path }/railway/image/lookupClear.gif' onclick='deltable3(this);'>";
		newPrefixTD3.innerHTML = prefixs;
	}
	function deltable1(r){
		  var i=r.parentNode.parentNode.rowIndex;
		  document.getElementById('table1').deleteRow(i);
	}
	function deltable2(r){
		  var i=r.parentNode.parentNode.rowIndex;
		  document.getElementById('table2').deleteRow(i);
	}
	function deltable3(r){
		  var i=r.parentNode.parentNode.rowIndex;
		  document.getElementById('table3').deleteRow(i);
	}
	function clean() {
		var r=confirm("确定清空以上所有信息吗？");
		  if (r==true)
		    {
		    	$('#form1').submit();
		    }else{
		    	return;
		    }
				
	}
	function shanchu(){
		document.getElementById("sortname").value = '';           
	}
	function openkaichuang1(size) {
		var resDlg = $.openWindow("${path}/railway/tanchuangshu.jsp?type=1&size="+size,'选择词条', 900, 550);
		//var resDlg= window.showModalDialog("${path}/railway/tanchuangshu.jsp","dialogWidth=760px;dialogHeight=500px");
		 if (resDlg != null && resDlg.length > 0 && resDlg[0].toString() == "refresh") {
	        if (resDlg[1] == undefined) {
	            return;
	        }
	        document.getElementById("Sname"+size).value = resDlg[1].toString();
	        document.getElementById("Surl"+size).value = resDlg[2].toString();
	    }
	    
	}
	function openkaichuang2(size) {
		var resDlg = $.openWindow("${path}/railway/tanchuangshu.jsp?type=2&size="+size,'选择词条', 900, 550);
		//var resDlg= window.showModalDialog("${path}/railway/tanchuangshu.jsp","dialogWidth=900px;dialogHeight=550px");
		 if (resDlg != null && resDlg.length > 0 && resDlg[0].toString() == "refresh") {
	        if (resDlg[1] == undefined) {
	            return;
	        }
	        document.getElementById("Xname"+size).value = resDlg[1].toString();
	        document.getElementById("Xurl"+size).value = resDlg[2].toString();
	    }
	    
	}
	function openkaichuang3(size) {
		var resDlg = $.openWindow("${path}/railway/tanchuangshu.jsp?type=3&size="+size,'选择词条', 900, 550);
		//var resDlg= window.showModalDialog("${path}/railway/tanchuangshu.jsp","dialogWidth=900px;dialogHeight=550px");
		 if (resDlg != null && resDlg.length > 0 && resDlg[0].toString() == "refresh") {
	        if (resDlg[1] == undefined) {
	            return;
	        }
	        document.getElementById("Gname"+size).value = resDlg[1].toString();
	        document.getElementById("Gurl"+size).value = resDlg[2].toString();
	    }
	    
	}
	function addjiedian(){
		var resDlg=$.openWindow("railway/treeNode.jsp?fieldName=cbclassSearch&isMain=1&typeId=1",'选择分类', 500, 450);
		//var resDlg= window.showModalDialog("${path}/railway/fujiedian.jsp","dialogWidth=500px;dialogHeight=300px");
		if (resDlg != null && resDlg.length > 0 && resDlg[0].toString() == "refresh") {
	        if (resDlg[1] == undefined) {
	            return;
	        }
// 	        document.getElementById("xpath").value = resDlg[1].toString();
// 	        document.getElementById("categoryId").value = resDlg[3].toString();
// 	        document.getElementById("sortname").value = resDlg[2].toString();
	    }
	}
	function onClickSave(){
		//获取上位词的url
		var table1 = document.getElementById("table1");
		var length1=table1.rows.length;
		var Shangwei = "";
		for(var i=2;i<length1;i++){
			var shang =$("#Surl"+i).val(); 
			Shangwei += shang + ",";
		}
		if(Shangwei!=''){
			Shangwei = Shangwei.substring(0,Shangwei.length-1);
		}
		//获取下位词的url
		var table2 = document.getElementById("table2");
		var length2=table2.rows.length;
		var Xiawei = "";
		for(var i=2;i<length2;i++){
			var xia = $("#Xurl"+i).val();
			Xiawei += xia + ",";
		}
		if(Xiawei!=''){
			Xiawei = Xiawei.substring(0,Xiawei.length-1);
		}
		//获取相关词的url
		var table3 = document.getElementById("table3");
		var length3=table3.rows.length;
		var Guanwei = "";
		for(var i=2;i<length3;i++){
			var guan = $("#Gurl"+i).val();
			var ty = $("#Gtype"+i).val();
			var gt = guan + "|" + ty;
			Guanwei += gt + ",";
		}
		if(Guanwei!=''){
			Guanwei = Guanwei.substring(0,Guanwei.length-1);
		}
		//this.location.href='${path}/railway/createzhishi.action?Surl='+Shangwei+'&Xurl='+Xiawei+'&Gurl'+Guanwei;
		/* $.post("${path}/railway/createzhishi.action",{Surl:Shangwei,Xurl:Xiawei,Gurl:Guanwei},function(data){
			alert(data);
		}); */
		$('#form1').ajaxSubmit({
			url: '${path}/railway/createzhishi.action?Surl='+Shangwei+'&Xurl='+Xiawei+'&Gurl='+Guanwei,
			method: 'post',
			success:(function(data){
				alert(data);
			})
		});
	}
	
	function importExcel(){
		$.ajax({
			url:'${path}/railway/excelData.action'
		});
	}
</script>
</head>
<body  class="yui-skin-sam" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="overflow: auto;" scroll="auto" >
<div class="con_L_search" style="margin-top: 25px;"  align="left">
	<form:form action="" id="form1" name="form1" method="post" modelAttribute="knowledge">
	<div class="form-group">
		<table border="1" width="80%" align="center">
			<tr>
				<td rowspan="3" style="text-align: center;width: 20%;">首选词：</td>
				<td height="30px;">&nbsp;&nbsp;&nbsp;&nbsp;中文:&nbsp;&nbsp;<input name="prefLabelZH" type="text"/></td>
				<td rowspan="3" style="text-align: center;width: 20%;">备选词：</td>
				<td height="30px;">&nbsp;&nbsp;&nbsp;&nbsp;中文:&nbsp;&nbsp;<input name="altLabelZH" type="text"/></td>
			</tr>
			<tr>
				<td height="30px;">&nbsp;&nbsp;&nbsp;&nbsp;英文:&nbsp;&nbsp;<input name="prefLabelEN" type="text"/></td>
				<td height="30px;">&nbsp;&nbsp;&nbsp;&nbsp;英文:&nbsp;&nbsp;<input name="altLabelEN" type="text"/></td>
			</tr>
			<tr>
				<td height="30px;">&nbsp;&nbsp;&nbsp;&nbsp;拼音:&nbsp;&nbsp;<input name="prefLabelPY" type="text"/></td>
				<td height="30px;">&nbsp;&nbsp;&nbsp;&nbsp;拼音:&nbsp;&nbsp;<input name="altLabelPY" type="text"/></td>
			</tr>
			<tr height="30px;">
				<td align="center">所属节点:</td>
				<td colspan="3"><div>&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="hidden" readonly="readonly" name="xpath" id="xpath" size="50px;">
				<input type="hidden" readonly="readonly" name="categoryId" id="categoryId" size="50px;">
				<input type="text" readonly="readonly" name="sortname" id="sortname" size="50px;">
				<img alt="选择" title="选择" src="${path }/railway/image/search.gif" onclick="addjiedian();">
				<img alt="清除" title="清除" src="${path }/railway/image/lookupClear.gif" onclick="shanchu();"></div>
				</td>
			</tr>
			<tr height="75px;">
				<td align="center">描述：</td>
				<td colspan="3">&nbsp;
					<textarea rows="3" cols="100" name="description"></textarea>
				</td>
			</tr>
		</table>
		<table width="80%" border="1" align="center" id="table1">
			<tr height="30px;">
				<td colspan="3">
					&nbsp;&nbsp;上位词：<a class="glyphicon glyphicon-plus" title="添加信息" onclick="addtable1();"></a>
				</td>
			</tr>
			<tr height="30px;" align="center" bgcolor="#DEDEDE">
					<td width="25%">词条名</td>
					<td>词条URL</td>
					<td width="20%">操作</td>					
			</tr>
		</table>
		<table width="80%" border="1" align="center" id="table2">
			<tr height="30px;">
				<td colspan="3">
					&nbsp;&nbsp;下位词：<a class="glyphicon glyphicon-plus" title="添加信息" onclick="addtable2();"></a>
				</td>
			</tr>
			<tr height="30px;" align="center" bgcolor="#DEDEDE">
					<td width="25%">词条名</td>
					<td>词条URL</td>
					<td width="20%">操作</td>					
			</tr>
		</table>
		<table width="80%" border="1" align="center" id="table3">
			<tr height="30px;">
				<td colspan="3">
					&nbsp;&nbsp;相关词：<a class="glyphicon glyphicon-plus" title="添加信息" onclick="addtable3();"></a>
				</td>
			</tr>
			<tr height="30px;" align="center" bgcolor="#DEDEDE">
					<td width="25%">词条名</td>
					<td width="40%">词条URL</td>
					<td>关系</td>
					<td width="20%">操作</td>					
			</tr>
		</table>
		</div>
		<div align="center">
		<input type="button" class="btn btn-default blue" value="确定" onclick="onClickSave();"/>
		<input type="button" class="btn btn-default red" value="重置" onclick="clean();"/>
<!-- 		<input type="button" class="btn btn-default red" value="导入" onclick="importExcel();"/> -->
		</div>
	</form:form>>
</div>
</body>
</html>