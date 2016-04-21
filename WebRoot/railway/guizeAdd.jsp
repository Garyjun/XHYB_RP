<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>创建规则</title>
<style type="text/css">
.con_L_search{
	border: 0px solid #9ACCCD;
	border-bottom-width: 0px;
	border-bottom-style: solid;
	border-bottom-color: #024363;
	line-height: 25px;
	margin-bottom: 10px;
	font-size: 15px;
	height: 20px;
	}
	fieldset{
	background-color: #ccddff;
	}
</style>
<script type="text/javascript">
		
		/* function onClickSave(isCreate){
			alert(document.getElementsByName("knowledge.broaders"));
			if(isCreate == 1){
				$("saveKnowledge").action='${pageContext.request.contextPath}/knowledge/addKnowledge';
			}else{
				$("saveKnowledge").action='${pageContext.request.contextPath}/knowledge/updateKnowledge';
			}
			$("saveKnowledge").submit();
		}
		
		function gotoSelectType(fileName) {
		    var url = "${pageContext.request.contextPath}/showAllKnowledges.action";
		    var iWidth = 400;
		    var iHeight = 500;
		    var strWinName = "选择知识点";
		    var resDlg = AppFrame.Util.openModalDialog(url, iWidth, iHeight, strWinName);
		    if (resDlg != null && resDlg.length > 0 && resDlg[0].toString() == "refresh") {
		        if (resDlg[1] == undefined) {
		            return;
		        }
		        document.getElementById("knowledge."+fileName).value = resDlg[1].toString();
		        document.getElementById(fileName+"Name").value = resDlg[2].toString();
		    }
		}
		function clearType(fileName){ 
			document.getElementById("knowledge."+fileName).value = '';  
			document.getElementById(fileName+"Name").value  ='';
		}  */
		function addTr(){
			var table = document.getElementById("regularTable");
			var newTR = table.insertRow(table.rows.length);
			var length=table.rows.length;
			var rowID =  length-1;
			//添加列:序号
			 var newPrefixTD=newTR.insertCell(0);
			 newPrefixTD.align="center"; 
			 newPrefixTD.style.height="25px";
			 newPrefixTD.style.width="10%";
			 newPrefixTD.style.fontWeight="bold";
			 newPrefixTD.innerHTML = "主语"+rowID;
			 
			 var newPrefixTD1=newTR.insertCell(1);
			 newPrefixTD1.style.height="25px";
			 newPrefixTD1.style.width="23%";
			 var prefix = "<select name='regularPrefix" + rowID + "' id='regularPrefix" + rowID + "' >";
			 prefix += "<option value='?a'>?a</option>";
			 prefix += "<option value='?b'>?b</option>";
			 prefix += "<option value='?c'>?c</option>";
			 prefix += "<option value='?d'>?d</option>";
			 prefix += "<option value='?e'>?e</option>";
			 prefix += "<option value='?f'>?f</option>";
			 prefix += "<option value='?g'>?g</option>";
			 prefix += "<option value='0'>0</option>";
			 prefix += "<option value='1'>1</option>";
			 prefix += "<option value='2'>2</option>";
			 prefix += "<option value='3'>3</option>";
			 prefix += "<option value='4'>4</option>";
			 prefix += "<option value='5'>5</option>";
			 prefix += "<option value='6'>6</option>";
			 prefix += "<option value='7'>7</option>";
			 prefix += "<option value='8'>8</option>";
			 prefix += "<option value='9'>9</option>";
			 newPrefixTD1.innerHTML = prefix;
			 var newContentTD=newTR.insertCell(2);
			 newContentTD.align="center"; 
			 newContentTD.style.height="25px";
			 newContentTD.style.width="10%";
			 newContentTD.style.fontWeight="bold";
			 newContentTD.innerHTML = "谓语"+rowID;
			 var newContentTD1=newTR.insertCell(3);
			 newContentTD1.style.height="25px";
			 newContentTD1.style.width="23%";
			 var content = "<select name='regularContent" + rowID + "' id='regularContent" + rowID + "' >";
			 content += "<option value='http://www.w3.org/2004/02/skos/core#broader'>上位</option>";
			 content += "<option value='http://www.w3.org/2004/02/skos/core#narrower'>下位</option>";
			 content += "<option value='http://www.w3.org/2004/02/skos/core#related'>相关</option>";
			 content += "<option value='http://www.brainsoon.com/structure#isPartOf'>部分</option>";
			 content += "<option value='http://www.brainsoon.com/structure#contain'>包含</option>";
			 content += "<option value='http://www.brainsoon.com/structure#context'>上下文</option>";
			 content += "<option value='http://www.brainsoon.com/structure#type'>类型</option>";
			 content += "</select>";
			 newContentTD1.innerHTML = content;
			 var newSuffixTD=newTR.insertCell(4);
			 newSuffixTD.align="center"; 
			 newSuffixTD.style.height="25px";
			 newSuffixTD.style.width="10%";
			 newSuffixTD.style.fontWeight="bold";
			 newSuffixTD.innerHTML = "宾语"+rowID;
			 var newSuffixTD1=newTR.insertCell(5);
			 newSuffixTD1.style.height="25px";
			 newSuffixTD1.style.width="23%";
			 var suffix = "<select name='regularSuffix" + rowID + "' id='regularSuffix" + rowID + "' >";
			 suffix += "<option value='?a'>?a</option>";
			 suffix += "<option value='?b'>?b</option>";
			 suffix += "<option value='?c'>?c</option>";
			 suffix += "<option value='?d'>?d</option>";
			 suffix += "<option value='?e'>?e</option>";
			 suffix += "<option value='?f'>?f</option>";
			 suffix += "<option value='?g'>?g</option>";
			 suffix += "<option value='0'>0</option>";
			 suffix += "<option value='1'>1</option>";
			 suffix += "<option value='2'>2</option>";
			 suffix += "<option value='3'>3</option>";
			 suffix += "<option value='4'>4</option>";
			 suffix += "<option value='5'>5</option>";
			 suffix += "<option value='6'>6</option>";
			 suffix += "<option value='7'>7</option>";
			 suffix += "<option value='8'>8</option>";
			 suffix += "<option value='9'>9</option>";
			 newSuffixTD1.innerHTML = suffix;

		}
		function onClickSave(){
			var table = document.getElementById("regularTable");
			var length=table.rows.length;
			var regulars = "";
			for(var i=1;i<length;i++){
				var regularPrefix = document.getElementById("regularPrefix"+i).value;
				var regularContent = document.getElementById("regularContent"+i).value;
				var regularSuffix = document.getElementById("regularSuffix"+i).value;
				if(regularPrefix == '' || regularContent == '' || regularSuffix == ''){
					continue;
				}
				regulars += regularPrefix+"|"+ regularContent+"|"+ regularSuffix+",";
			}
			if(regulars!=''){
				regulars = regulars.substring(0,regulars.length-1);
			}
			regulars =  regulars.replace(/#/g,"-cmp");
			var result = document.getElementById("regularPrefix").value+"|"+document.getElementById("regularContent").value+"|"+document.getElementById("regularSuffix").value;
			result =  result.replace("#","-cmp");
			this.location.href='${path}/railway/guizeAdd.action?contents='+regulars+'&result='+result;
		}
		function onClickReturn(){
			this.location.href='${path}/railway/queryTuili.action';
		}
	</script>

</head>
<body  class="yui-skin-sam" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="overflow: auto;">
<div class="con_L_search" style="margin-top: 8px;"  align="left">
<fieldset>
<legend>增加规则</legend>

		<table width="99%" border="1"  id="regularTable">
				<tr >
				<td colspan="6"><input type="button" onclick="addTr();" class="btn btn-default red" value="添加规则" /></td>
				</tr>
				<tr>
				<td width="10%" align="center"><strong>主语1</strong></td><td width="23%">
				<select id="regularPrefix1" name="regularPrefix1">
					<option value="?a">?a</option>
					<option value="?b">?b</option>
					<option value="?c">?c</option>
					<option value="?d">?d</option>
					<option value="?e">?e</option>
					<option value="?f">?f</option>
					<option value="?g">?g</option>
					<option value="0">0</option>
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
					<option value="7">7</option>
					<option value="8">8</option>
					<option value="9">9</option>
				</select>
				</td>
				<td width="10%" align="center"><strong>谓语1</strong></td><td>
				<select id="regularContent1" name="regularContent1">
					<option value="http://www.w3.org/2004/02/skos/core#broader">上位</option>
					<option value="http://www.w3.org/2004/02/skos/core#narrower">下位</option>
					<option value="http://www.w3.org/2004/02/skos/core#related">相关</option>
					<option value="http://www.brainsoon.com/structure#isPartOf">部分</option>
					<option value="http://www.brainsoon.com/structure#contain">包含</option>
					<option value='http://www.brainsoon.com/structure#context'>上下文</option>
			        <option value='http://www.brainsoon.com/structure#type'>类型</option>
				</select>
				</td>
				<td width="10%" align="center"><strong>宾语1</strong></td>
				<td width="23%">
				<select id="regularSuffix1" name="regularSuffix1">
					<option value="?a">?a</option>
					<option value="?b">?b</option>
					<option value="?c">?c</option>
					<option value="?d">?d</option>
					<option value="?e">?e</option>
					<option value="?f">?f</option>
					<option value="?g">?g</option>
					<option value="0">0</option>
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
					<option value="7">7</option>
					<option value="8">8</option>
					<option value="9">9</option>
				</select>
				</td>
				</tr>
		</table>
		<br>
		<div   align="left" ><strong>推出规则</strong></div>
		<table width="99%" border="1"  id="regularTable1">
			<tr>
				<td width="10%" align="center"><strong>主语</strong></td><td width="23%">
				<select id="regularPrefix" name="regularPrefix">
					<option value="?a">?a</option>
					<option value="?b">?b</option>
					<option value="?c">?c</option>
					<option value="?d">?d</option>
					<option value="?e">?e</option>
					<option value="?f">?f</option>
					<option value="?g">?g</option>
					<option value="0">0</option>
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
					<option value="7">7</option>
					<option value="8">8</option>
					<option value="9">9</option>
				</select>
				</td>
				<td width="10%" align="center"><strong>谓语</strong></td><td width="23%">
				<select id="regularContent" name="regularContent">
					<option value="http://www.w3.org/2004/02/skos/core#broader">上位</option>
					<option value="http://www.w3.org/2004/02/skos/core#narrower">下位</option>
					<option value="http://www.w3.org/2004/02/skos/core#related">相关</option>
					<option value="http://www.brainsoon.com/structure#isPartOf">部分</option>
					<option value="http://www.brainsoon.com/structure#contain">包含</option>
					<option value='http://www.brainsoon.com/structure#context'>上下文</option>
			        <option value='http://www.brainsoon.com/structure#type'>类型</option>
				</select>
				</td>
				<td width="10%" align="center"><strong>宾语</strong></td><td width="23%">
				<select id="regularSuffix" name="regularSuffix">
					<option value="?a">?a</option>
					<option value="?b">?b</option>
					<option value="?c">?c</option>
					<option value="?d">?d</option>
					<option value="?e">?e</option>
					<option value="?f">?f</option>
					<option value="?g">?g</option>
					<option value="0">0</option>
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
					<option value="7">7</option>
					<option value="8">8</option>
					<option value="9">9</option>
				</select>
				</td>
				</tr>
		</table>
		<div align="center">
		<input type="button" onclick="onClickSave();" class="btn btn-default red" value="创建" />
		<input type="button" onclick="onClickReturn();" class="btn btn-default blue" value="返回" />
		</div>
		</fieldset>
</div>
</body>
</html>