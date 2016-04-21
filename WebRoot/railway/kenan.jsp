<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>索引规则</title>
<script type="text/javascript">
	function addtable() {
		var table = document.getElementById("table1");
		var size= table.insertRow(table.rows.length);
		size.style.border.bottom="1px #000000 solid;";
		var length=table.rows.length;
		var rowID =  length-1;
		//添加序列号
		var newPrefixTD=size.insertCell(0);
		newPrefixTD.align="center"; 
		var checkbox = "<input type='button' onclick='deltable(this);' value='删除' />";
		newPrefixTD.innerHTML = checkbox;
		var newPrefixTD1=size.insertCell(1);
		newPrefixTD1.align="center"; 
		//newPrefixTD.innerHTML = "关键字："+rowID;
		 var prefix = "<input name='keyword" + rowID + "' id='keyword" + rowID + "' type='text'/>";
		 newPrefixTD1.innerHTML = prefix;
		 var newPrefixTD2=size.insertCell(2);
		 newPrefixTD2.align="center"; 
		 var prefixs = "<input name='logo" + rowID + "' id='logo" + rowID + "' type='text'/>";
		 newPrefixTD2.innerHTML = prefixs;
	}
	function deltable(r){
		  var i=r.parentNode.parentNode.rowIndex;
		  document.getElementById('table1').deleteRow(i);
	}
	function addtable1() {
		var table = document.getElementById("table2");
		var size= table.insertRow(table.rows.length);
		size.style.border.bottom="1px #000000 solid;";
		var length=table.rows.length;
		var rowID =  length-1;
		//添加序列号
		var newPrefixTD=size.insertCell(0);
		newPrefixTD.align="center"; 
		var checkbox = "<input type='button' onclick='deltable1(this);' value='删除' />";
		newPrefixTD.innerHTML = checkbox;
		var newPrefixTD1=size.insertCell(1);
		newPrefixTD1.align="center"; 
		//newPrefixTD.innerHTML = "关键字："+rowID;
		 var prefix = "<input name='keyword" + rowID + "' id='keyword" + rowID + "' type='text'/>";
		 newPrefixTD1.innerHTML = prefix;
		 var newPrefixTD2=size.insertCell(2);
		 newPrefixTD2.align="center"; 
		 var prefixs = "<input name='logo" + rowID + "' id='logo" + rowID + "' type='text'/>";
		 newPrefixTD2.innerHTML = prefixs;
	}
	function deltable1(r){
		  var i=r.parentNode.parentNode.rowIndex;
		  document.getElementById('table2').deleteRow(i);
	}
	

</script>
</head>
<body>
<form action="">
<div  style="text-align: left;color: red;margin-top: 20px;margin-left: 5%"><font size="3">推理一</font></div>
<div>
	<table border="1" width="90%" align="center">
		<tr>
			<td width="20%" align="center">事实型</td>
			<td width="70%">
				<table id="table1" width="100%" align="right" border="1">
					<tr>
					    <td width="7%" align="center"><input type="button" onclick="addtable();" value="增加"/></td>
						<td align="center">关键字</td>
						<td align="center">标识符</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
<div  style="text-align: left;color: red;margin-top: 20px;margin-left: 5%"><font size="3">推理二</font></div>
<div>
	<table border="1" width="90%" align="center">
		<tr>
			<td width="20%" align="center">解释型</td>
			<td width="70%">
				<table id="table2" width="100%" align="right" border="1">
					<tr>
					    <td width="7%" align="center"><input type="button" onclick="addtable1();" value="增加"/></td>
						<td align="center">关键字</td>
						<td align="center">标识符</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
<div align="center"><input type="button" class="btn btn-default red" value="保存"/>
<input type="button" class="btn btn-default blue" value="返回"/></div>
</form>
</body>
</html>