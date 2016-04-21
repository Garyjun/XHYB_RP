<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<style type="text/css">

</style>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="" id="form" modelAttribute="knowledge" method="post" class="form-horizontal" role="form">
		<fieldset  style="text-align: right;border:1px solid #009;-moz-border-radius: 5px;-webkit-border-radius: 5px;border-radius: 5px;margin-top:10px;margin-left: 27%;width:600px;">
		<legend>详细信息</legend>
			<div  style="margin-top: 10px;">
			<table width="90%" border="0" cellpadding="5px" cellspacing="10" align="center">
				<tr>
					<td style="height: 30px;font-size: 14px;" >首选词中文：</td>
					<td><input type="text" value="${knowledge.prefLabelZH}" readonly="readonly" style="width:400px;h"/></td>
				</tr>
				<tr>
					<td style="height: 30px;font-size: 14px;" >首选词英文：</td>
					<td><input type="text" value="${knowledge.prefLabelEN}" readonly="readonly" style="width:400px;"/></td>
				</tr>
				<tr>
					<td style="height: 30px; font-size: 14px;" >备选词中文：</td>
					<td><input type="text" value="${knowledge.altLabelZH}" readonly="readonly" style="width:400px;"/></td>
				</tr>
				<tr>
					<td style="height: 30px; font-size: 14px;" >分类：</td>
					<td><input type="text" value="${knowledge.sortname}" readonly="readonly" style="width:400px;"/></td>
				</tr>
			</table>
		</div>
		
		</fieldset>
		 <%-- <div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right" style="margin-top: 50px;">首选词中文:</label>
				<div class="col-xs-5" style="margin-top: 50px;">
					<input type="text" value="${knowledge.prefLabelZH}" readonly="readonly" style="width:400px;"/>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">首选词英文:</label>
				<div class="col-xs-5">
					<input type="text" value="${knowledge.prefLabelEN}" readonly="readonly" style="width:400px;"/>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">备选词中文:</label>
				<div class="col-xs-5">
					<input type="text" value="${knowledge.prefLabelPY}" readonly="readonly" style="width:400px;"/>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">分类:</label>
				<div class="col-xs-5">
					<input type="text" value="${knowledge.prefLabelPY}" readonly="readonly" style="width:400px;"/>
				</div>
			</div>
		</div>  --%>
		<fieldset  style="text-align: center;border:1px solid #009;-moz-border-radius: 5px;-webkit-border-radius: 5px;border-radius: 5px;margin-top:20px;margin-left: 27%;width:600px;">
		<legend>详细描述</legend>
		<textarea  rows="3" cols="80" readonly="readonly">${knowledge.description}</textarea>
		</fieldset>
		<fieldset  style="text-align: right;border:1px solid #009;-moz-border-radius: 5px;-webkit-border-radius: 5px;border-radius: 5px;margin-top:20px;margin-left: 27%;width:600px;">
		<legend>属性关系</legend>
		
		
		<div style="width: 10%;text-align: right;">
			<label class="con_L_searchA">上位词</label>
		</div>
		<div class="form-group">
			<table width="90%" border="1" cellpadding="5" cellspacing="0" align="right" style="margin-right: 30px;">
				<tr align="center">
					<td bgcolor="#DEDEDE" height="26px" width="30%">词条名</td>
					<td align="center" width="60%" bgcolor="#DEDEDE" height="26px">词条URL</td>
				</tr>
				<c:forEach items="${broadersList}" var="broadersList">
				<c:if test="broadersList==null">
				<tr align="center">
			    	<td height="22px">没有数据</td>
			    	<td height="22px">${broadersList.url }</td>
			    </tr>
				</c:if>
			    <tr align="center">
			    	<td height="22px">${broadersList.name }</td>
			    	<td height="22px">${broadersList.url }</td>
			    </tr>
			</c:forEach>
			</table>
		</div>
		 <div style="width: 10%;text-align: right;">
			<label class="con_L_searchA">下位词</label>
		</div>
		<div class="form-group">
			<table width="90%" border="1" cellpadding="5" cellspacing="0" align="right" style="margin-right: 30px;">
				<tr align="center">
					<td bgcolor="#DEDEDE" height="26px" width="30%">词条名</td>
					<td align="center" width="60%" bgcolor="#DEDEDE" height="26px">词条URL</td>
				</tr>
				<c:forEach items="${narrowersList}" var="narrowersList">
			    <tr align="center">
			    	<td height="22px">${narrowersList.name }</td>
			    	<td height="22px">${narrowersList.url }</td>
			    </tr>
			</c:forEach>
			</table>
		</div>
		<div style="width: 10%;text-align: right;">
			<label class="con_L_searchA">相关词</label>
		</div>
		<div class="form-group">
			<table width="90%" border="1" cellpadding="5" cellspacing="0" align="right" style="margin-right: 30px;">
				<tr align="center">
					<td bgcolor="#DEDEDE" height="26px" width="18%">词条名</td>
					<td bgcolor="#DEDEDE" height="26px" width="15%">类型</td>
					<td width="67%" bgcolor="#DEDEDE" height="26px">词条URL
					</td>
				</tr>
				<c:forEach items="${relatedsList}" var="relatedsList">
			    <tr align="center">
			    	<td height="22px">${relatedsList.name }</td>
			    	<td height="22px">
			    	<c:choose>
			    	<c:when test="${relatedsList.sort eq'contain' }">事故
			    	</c:when>
			    	<c:otherwise>预防</c:otherwise>
			    	</c:choose>
			    	
			    	<%-- <c:if test="">${relatedsList.sort }></c:if> --%>
			    	</td>
			    	<td height="22px">${relatedsList.url }</td>
			    </tr>
			</c:forEach>
			</table>
		</div>
		</fieldset>
		</form:form>
		</div>	
	</div>
</body>
</html>