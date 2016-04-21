<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>修改界面</title>
<script type="text/javascript">
		/*
			上位词操作
		*/
		/**
		 * 根据table的id将datagrid选中的值添加到对应的表格中
		 */
		function openkaichuang(size) {
			var resDlg = $.openWindow("${path}/railway/tanchuangshu.jsp?type=1&size="+size,'选择词条', 900, 550);
// 			layer.open({
// 				title:'选择词条',
// 				type:2,
// 				area: ['900px', '550px'],
// 				content:'${path}/railway/tanchuangshu.jsp?type=1&size='+size
// 			});
			//var resDlg= window.showModalDialog('${path}/railway/tanchuangshu.jsp','dialogWidth=1200px;dialogHeight=550px;center=1');
		}
		
		function addtable() {
			var table = document.getElementById("Stable");
			var size= table.insertRow(table.rows.length);
			size.style.border.bottom="1px #000000 solid;";
			var length=table.rows.length;
			var rowID =  length-1;
			//添加序列号
			var newPrefixTD1=size.insertCell(0);
			newPrefixTD1.align="center"; 
		    var prefix = "<font color='red' size='3'>*</font>&nbsp;&nbsp;<input  name='Sname" + rowID + "' id='Sname" + rowID + "' readonly='readonly'/>";
		    newPrefixTD1.innerHTML = prefix;
		    
		    var newPrefixTD2=size.insertCell(1);
		    newPrefixTD2.align="center";
		    var prefixs = "<input size='45px;' name='Surl" + rowID + "' id='Surl" + rowID + "' readonly='readonly'//>";
		    newPrefixTD2.innerHTML = prefixs;
			 
		    var newPrefixTD=size.insertCell(2);
			newPrefixTD.align="center"; 
			var insert = "<img alt='选择' title='选择' src='${path }/railway/image/search.gif' onclick='openkaichuang("+rowID+");'>&nbsp;<img alt='清除' title='清除' src='${path }/railway/image/lookupClear.gif' onclick='deltable(this);'>";
			
			newPrefixTD.innerHTML = insert;
		}
		function deltable(r){
			  var i=r.parentNode.parentNode.rowIndex;
			  document.getElementById('Stable').deleteRow(i);
		}
		function addtable2() {
			var table = document.getElementById("Xtable");
			var size= table.insertRow(table.rows.length);
			size.style.border.bottom="1px #000000 solid;";
			var length=table.rows.length;
			var rowID =  length-1;
			//添加序列号
			var newPrefixTD1=size.insertCell(0);
			newPrefixTD1.align="center"; 
		    var prefix = "<font color='red' size='3'>*</font>&nbsp;&nbsp;<input  name='Xname" + rowID + "' id='Xname" + rowID + "' readonly='readonly'/>";
		    newPrefixTD1.innerHTML = prefix;
		    
		    var newPrefixTD2=size.insertCell(1);
		    newPrefixTD2.align="center"; 
		    var prefixs = "<input size='45px;' name='Xurl" + rowID + "' id='Xurl" + rowID + "' readonly='readonly'//>";
		    newPrefixTD2.innerHTML = prefixs;
			 
		    var newPrefixTD=size.insertCell(2);
			newPrefixTD.align="center"; 
			var insert = "<img alt='选择' title='选择' src='${path }/railway/image/search.gif' onclick='openkaichuang2("+rowID+");'>&nbsp;<img alt='清除' title='清除' src='${path }/railway/image/lookupClear.gif' onclick='deltable2(this);'>";
			
			newPrefixTD.innerHTML = insert;
		}
		function openkaichuang2(size) {
			//var resDlg= window.showModalDialog('${path}/railway/tanchuangshu.jsp','dialogWidth=1200px;dialogHeight=550px;center=1');
			var resDlg = $.openWindow("${path}/railway/tanchuangshu.jsp?type=2&size="+size,'选择词条', 900, 550);
			if (resDlg != null && resDlg.length > 0 && resDlg[0].toString() == "refresh") {
		        if (resDlg[1] == undefined) {
		            return;
		        }
		        document.getElementById("Xname"+size).value = resDlg[1].toString();
		        document.getElementById("Xurl"+size).value = resDlg[2].toString();
		    }
		}
		function deltable2(r){
			  var i=r.parentNode.parentNode.rowIndex;
			  document.getElementById('Xtable').deleteRow(i);
		}
		
		
		/*
			相关词操作
		*/
		function openkaichuang1(size) {
			//var resDlg= window.showModalDialog('${path}/railway/tanchuangshu.jsp','dialogWidth=1500px;dialogHeight=550px;center=1');
			var resDlg = $.openWindow("${path}/railway/tanchuangshu.jsp?type=3&size="+size,'选择词条', 900, 550);
			if (resDlg != null && resDlg.length > 0 && resDlg[0].toString() == "refresh") {
		        if (resDlg[1] == undefined) {
		            return;
		        }
		        document.getElementById("Gname"+size).value = resDlg[1].toString();
		        document.getElementById("Gurl"+size).value = resDlg[2].toString();
		    }
		}
		
		function addtable1() {
			var table = document.getElementById("Gtable");
			var size= table.insertRow(table.rows.length);
			size.style.border.bottom="1px #000000 solid;";
			var length=table.rows.length;
			var rowID =  length-1;
			//添加序列号
			var newPrefixTD=size.insertCell(0);
			newPrefixTD.align="center"; 
		    var prefix = "<font color='red' size='3'>*</font>&nbsp;&nbsp;<input name='Gname" + rowID + "' id='Gname" + rowID + "' readonly='readonly'/>";
		    newPrefixTD.innerHTML = prefix;
		    
		    var newPrefixTD1=size.insertCell(1);
		    newPrefixTD1.align="center"; 
		    var prefixs = "<input size='45px;' name='Gurl" + rowID + "' id='Gurl" + rowID + "' readonly='readonly'//>";
		    newPrefixTD1.innerHTML = prefixs;
		    
		    var newPrefixTD2=size.insertCell(2);
			newPrefixTD2.align="center";
			var prefi="<select name='Gtype" + rowID + "' id='Gtype" + rowID + "'>";
			prefi += "<option value='contain'>事故</option>";
			prefi += "<option value='depend'>预防</option>";
			prefi +="</select>";
			newPrefixTD2.innerHTML = prefi;
			 
		    var newPrefixTD3=size.insertCell(3);
			newPrefixTD3.align="center"; 
			var insert = "<img alt='选择' title='选择' src='${path}/railway/image/search.gif' onclick='openkaichuang1("+rowID+");'>&nbsp;<img alt='清除' title='清除' src='${path }/railway/image/lookupClear.gif' onclick='deltable1(this);'>";
			newPrefixTD3.innerHTML = insert;
			
		}
		
		function deltable1(r){
			  var i=r.parentNode.parentNode.rowIndex;
			  document.getElementById('Gtable').deleteRow(i);
		}
		function addjiedian(){
			//var resDlg= window.showModalDialog("${path}/railway/fujiedian.jsp","dialogWidth=500px;dialogHeight=300px");
			//var resDlg=$.openWindow("${path}/railway/fujiedian.jsp?fieldName=cbclassSearch&isMain=2&typeId=1",'选择分类', 500, 450);
			layer.open({
				title:'选择分类',
				type:2,
				area: ['500px', '450px'],
				content:'${path}/railway/fujiedian.jsp?fieldName=cbclassSearch&isMain=2&typeId=1'
			});
		}
		
		function shanchu(){
			document.getElementById("sortname").value = '';           
		}
		function editupdate(){
			if($("#sortname").val()=="" || $("#sortname").val()==null || $("#sortname").val()==undefined || $("#categoryId").val()=="" || $("#categoryId").val()==null || $("#categoryId").val()==undefined || $("#xpath").val()=="" || $("#xpath").val()==null || $("#xpath").val()==undefined){
				alert("所属节点不正确,请重新填写!");
				return;
			}
			var table1 = document.getElementById("Stable");
			var length1=table1.rows.length;
			for(var i=1;i<length1;i++){
				if($("#Surl"+i).val()=="" ||$("#Surl"+i).val()==null || $("#Surl"+i).val().size<1 || $("#Surl"+i).val()==undefined){
					alert("第"+i+"行上位词不能为空,请选择！");
					return;
				}
			}
			var table2 = document.getElementById("Xtable");
			var length2=table2.rows.length;
			for(var i=1;i<length2;i++){
				if($("#Xurl"+i).val()=="" ||$("#Xurl"+i).val()==null || $("#Xurl"+i).val().size<1 || $("#Xurl"+i).val()==undefined){
					alert("第"+i+"行下位词不能为空,请选择！");
					return;
				}
			}
			var table3 = document.getElementById("Gtable");
			var length3=table3.rows.length;
			for(var i=1;i<length3;i++){
				if($("#Gurl"+i).val()=="" ||$("#Gurl"+i).val()==null || $("#Gurl"+i).val().size<1 || $("#Gurl"+i).val()==undefined){
					alert("第"+i+"行相关词不能为空,请选择！");
					return;
				}
			}
			//获取上位词的url
			var Shangwei = "";
			for(var i=1;i<length1;i++){
				var shang =$("#Surl"+i).val(); 
				Shangwei += shang + ",";
			}
			if(Shangwei!=''){
				Shangwei = Shangwei.substring(0,Shangwei.length-1);
			}
			//获取下位词的url
			var Xiawei = "";
			for(var i=1;i<length2;i++){
				var xia = $("#Xurl"+i).val();
				Xiawei += xia + ",";
			}
			if(Xiawei!=''){
				Xiawei = Xiawei.substring(0,Xiawei.length-1);
			}
			//获取相关词的url
			var Guanwei = "";
			for(var i=1;i<length3;i++){
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
				url: '${path}/railway/editupdate.action?Surl='+Shangwei+'&Xurl='+Xiawei+'&Gurl='+Guanwei,
				method: 'post',
				success:(function(data){
					if(data=="1"){
						top.layer.msg("修改成功"); 
						$.closeFromInner();
					}
				})
			});
			
		}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="" id="form1" modelAttribute="knowledge" method="post" class="form-horizontal" role="form">
			<div  style="margin-top: 10px;">
			<input type="hidden" name="objectid" value="${knowledge.objectid}" style="width:400px;"/>
			<table width="90%" border="0" cellpadding="5px" cellspacing="10" align="center">
				<tr>
					<td style="height: 30px;font-size: 14px;" >首选词中文：</td>
					<td><input type="text" name="prefLabelZH" value="${knowledge.prefLabelZH}" style="width:400px;h"/></td>
				</tr>
				<tr>
					<td style="height: 30px;font-size: 14px;" >首选词英文：</td>
					<td><input type="text" name="prefLabelEN" value="${knowledge.prefLabelEN}"  style="width:400px;"/></td>
				</tr>
				<tr>
					<td style="height: 30px; font-size: 14px;" >备选词中文：</td>
					<td><input type="text" name="altLabelZH" value="${knowledge.altLabelZH}"  style="width:400px;"/></td>
				</tr>
				<tr>
					<td style="height: 30px; font-size: 14px;" >所属节点：</td>
					<td>
					<input type="hidden" readonly="readonly" name="xpath" id="xpath" value="${knowledge.xpath}"  size="50px;">
					<input type="hidden" readonly="readonly" name="categoryId" id="categoryId" value="${knowledge.categoryId}"  size="50px;">
					<font color="red" size="3">*</font>&nbsp;&nbsp;<input type="text" readonly="readonly" name="sortname" id="sortname" value="${knowledge.sortname}" size="50px;">
					<!-- <input type="text" value="${knowledge.sortname}" style="width:400px;"/> -->
					<img alt="选择" title="选择" src="${path }/railway/image/search.gif" onclick="addjiedian();">
				    <img alt="清除" title="清除" src="${path }/railway/image/lookupClear.gif" onclick="shanchu();">
				    </td>
				</tr>
			</table>
		</div>
		<div style="width: 100%;">
			<label class="con_L_searchA">上位词</label>&nbsp;&nbsp;
			<a class="glyphicon glyphicon-plus" title="添加信息" onclick="addtable();"></a>
			&nbsp;&nbsp;&nbsp;<font size="1" color="red">带*号为必填项</font>
		</div>
		<div class="form-group">
			<table width="90%" border="1" cellpadding="5" cellspacing="0" align="center" id="Stable">
				<tr align="center">
					<td bgcolor="#DEDEDE" height="26px" width="30%">词条名</td>
					<td align="center" width="60%" bgcolor="#DEDEDE" height="26px">词条URL</td>
					<td align="center" width="14%" bgcolor="#DEDEDE" height="26px" colspan="2">操作</td>
				</tr>
					<c:forEach items="${broadersList}" var="broadersList"  varStatus="stat">
						    <tr align="center">
						    	<td height="22px"><font color="red" size="3">*</font>&nbsp;&nbsp;<input name="Sname${stat.index+1}" id="Sname${stat.index+1}" value="${broadersList.name }" readonly="readonly"></td>
						    	<td height="22px"><input size='45px;' name="Surl${stat.index+1}" id="Surl${stat.index+1}" value="${broadersList.url }" readonly="readonly"></td>
						    	<td><img alt='选择' title="选择" src='${path }/railway/image/search.gif' onclick='openkaichuang(${stat.index+1});'>
						    	<img alt='清除' title="清除" src='${path }/railway/image/lookupClear.gif' onclick='deltable(this);'></td>
						    </tr>
				</c:forEach>
			</table>
		</div>
		<div style="width: 14%;">
			<label class="con_L_searchA">下位词</label>&nbsp;&nbsp;
			<a class="glyphicon glyphicon-plus" title="添加信息" onclick="addtable2();"></a>
		</div>
		<div class="form-group">
			<table width="90%" border="1" cellpadding="5" cellspacing="0" align="center" id="Xtable">
				<tr align="center">
					<td bgcolor="#DEDEDE" height="26px" width="30%">词条名</td>
					<td align="center" width="60%" bgcolor="#DEDEDE" height="26px">词条URL</td>
					<td align="center" width="14%" bgcolor="#DEDEDE" height="26px" colspan="2">操作</td>
				</tr>
					<c:forEach items="${narrowersList}" var="narrowersList"  varStatus="stat">
						    <tr align="center">
						    	<td height="22px"><font color="red" size="3">*</font>&nbsp;&nbsp;<input name="Xname${stat.index+1}" id="Xname${stat.index+1}" value="${narrowersList.name }" readonly="readonly"></td>
						    	<td height="22px"><input size='45px;' name="Xurl${stat.index+1}" id="Xurl${stat.index+1}" value="${narrowersList.url }" readonly="readonly"></td>
						    	<td><img alt='选择' title="选择" src='${path }/railway/image/search.gif' onclick='openkaichuang2(${stat.index+1});'>
						    	<img alt='清除' title="清除" src='${path }/railway/image/lookupClear.gif' onclick='deltable2(this);'></td>
						    </tr>
				</c:forEach>
			</table>
		</div>
		<div style="width: 14%;">
			<label class="con_L_searchA">相关词</label>&nbsp;&nbsp;
			<a class="glyphicon glyphicon-plus" title="添加信息" onclick="addtable1();"></a>
		</div>
		<div class="form-group">
			<table width="90%" border="1" cellpadding="5" cellspacing="0" align="center" id="Gtable">
				<tr align="center">
					<td bgcolor="#DEDEDE" height="26px" width="30%">词条名</td>
					<td align="center" width="50%" bgcolor="#DEDEDE" height="26px">词条URL</td>
					<td align="center" width="10%" bgcolor="#DEDEDE" height="26px">关系</td>
					<td align="center" width="14%" bgcolor="#DEDEDE" height="26px" colspan="2">操作</td>
				</tr>
				<c:forEach items="${relatedsList}" var="relatedsList"  varStatus="stat">
			    <tr align="center">
			    	<td height="22px"><font color="red" size="3">*</font>&nbsp;&nbsp;<input name="Gname${stat.index+1}" id="Gname${stat.index+1}" value="${relatedsList.name }" readonly="readonly"></td>
						    	<td height="22px"><input size='45px;'  name="Gurl${stat.index+1}" id="Gurl${stat.index+1}" value="${relatedsList.url }" readonly="readonly"></td>
						    	<td height="22px">
						    	<c:choose>
						    		<c:when test="${relatedsList.sort eq'contain' }">
						    			<select id="Gtype${stat.index+1}" name="Gtype${stat.index+1}">
						    				<option selected="selected"  value="contain">事故</option>
						    				<option value="depend">预防</option>
						    			</select>
						    		</c:when>
						    		<c:otherwise>
						    			<select id="Gtype${stat.index+1}" name="Gtype${stat.index+1}">
						    				<option  value="contain">事故</option>
						    				<option  selected="selected"  value="depend">预防</option>
						    			</select>
						    		</c:otherwise>
						    	</c:choose>
						    	
						    	</td>
						    	<td>
						    	<img alt='选择' title="选择" src='${path }/railway/image/search.gif' onclick='openkaichuang1(${stat.index+1});'>
						    	<img alt='清除' title="清除" src='${path }/railway/image/lookupClear.gif' onclick='deltable1(this);'></td>
			    </tr>
			</c:forEach>
			</table>
		</div>
		<div style="text-align: center;"><a class="btn btn-default red" onclick="editupdate();">确定</a></div>
		</form:form>
		</div>	
	</div>
</body>
</html>