<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>MARC数据信息</title>
<%
	String appPath = request.getContextPath();
%>
<script type="text/javascript">
	function isNotNull(_paramValue) {
		if (typeof (_paramValue) != undefined
				&& typeof (_paramValue) != 'undefined'
				&& _paramValue != undefined && _paramValue != 'undefined'
				&& _paramValue != null && _paramValue != "") {
			return true;
		}
		return false;
	}


	function g(o) {
		return document.getElementById(o);
	}
	function HoverLi(n) {
		//如果有N个标签,就将i<=N;   
		for ( var i = 1; i <= 3; i++) {
			g('tb_' + i).className = '';
			g('tbc_0' + i).className = 'undis';
		}
		g('tbc_0' + n).className = 'dis';
		g('tb_' + n).className = 'selected';
		
		window.parent.setIframeHeight(document.body.scrollHeight,false);
	}
</script>

</head>
<body class="container-fluid by-frame-wrap">
  <div class="by-tab" >          
		<ul class="nav nav-tabs nav-justified">
			<li  class="active" ><a href="#tab_1_1_1" data-toggle="tab"><em>表单视图</em></a></li>
			<li><a href="#tab_1_1_2" data-toggle="tab"><em>字段视图</em></a></li>
			<li><a href="#tab_1_1_3" data-toggle="tab"><em>文件视图</em></a></li>
		</ul>
		 <div class="tab-content">
	<div  class="tab-pane active" id="tab_1_1_1">
			<table border="1"  align="center"  style="width: 100%;">
				<tbody>
					<c:forEach var="outMap" items="${marcMap['1']}">
						   <c:forEach items="${outMap}" var="mp" varStatus="status">
						   				<tr>
						   		<td class="form-control" style="width:15%;text-align:right;">
						   				<c:choose>
						   					<c:when test="${mp.key.length()>4}">
						   					  <c:set var="marc" value="${mp.key.substring(3)}"></c:set>
						   					</c:when>
						   					<c:otherwise>
						   					   <c:set var="marc" value="${mp.key}"></c:set>
						   					</c:otherwise>
						   				</c:choose>
						   				<c:choose>
						   					<c:when test="${marc.indexOf('--') > -1}">
						   					  <c:set var="marc" value="${mp.key.substring(3)}"></c:set>
						   					  <label class="control-label"  ><c:out value="${marc.substring(0,marc.indexOf('--'))}"></c:out></label>
						   					</c:when>
						   					<c:otherwise>
						   					   <label class="control-label"  ><c:out value="${marc}"></c:out></label>
						   					</c:otherwise>
						   				</c:choose>
<!-- 										<s:if test="key.length() > 4"> -->
<!-- 											<s:set var="marc" value="key.substring(3)"></s:set> -->
<!-- 										</s:if> -->
<!-- 										<s:else> -->
<!-- 											<s:set var="marc" value="key"></s:set> -->
<!-- 										</s:else> -->
<!-- 										<s:if test="#marc.indexOf('--') > -1"> -->
<!-- 											<s:property value="#marc.substring(0,#marc.indexOf('--'))" /> -->
<!-- 										</s:if> -->
<!-- 										<s:else> -->
<!-- 											<s:property value="marc" /> -->
<!-- 										</s:else> -->
										 
									</td>
									<td class="form-control"><c:out value="${mp.value}"></c:out>&nbsp;</td>
									</tr>
						   </c:forEach>
					</c:forEach>
<!-- 					<s:iterator value="marcMap" status="st" var="outMap"> -->
<!-- 						<s:if test="key == 1"> -->
<!-- 							<s:iterator value="#outMap.value" status="status" var="mp"> -->
<!-- 								<s:iterator value="#mp" status="status" var="marc"> -->
<!-- 									<s:if test="#status.odd"> -->
<!-- 										</tr><tr> -->
<!-- 									</s:if> -->
<!-- 									<td class="label" style="width:10%;"> -->
<!-- 										<s:if test="key.length() > 4"> -->
<!-- 											<s:set var="marc" value="key.substring(3)"></s:set> -->
<!-- 										</s:if> -->
<!-- 										<s:else> -->
<!-- 											<s:set var="marc" value="key"></s:set> -->
<!-- 										</s:else> -->
<!-- 										<s:if test="#marc.indexOf('--') > -1"> -->
<!-- 											<s:property value="#marc.substring(0,#marc.indexOf('--'))" /> -->
<!-- 										</s:if> -->
<!-- 										<s:else> -->
<!-- 											<s:property value="marc" /> -->
<!-- 										</s:else> -->
										
<!-- 									</td> -->
<!-- 									<td class="content"><s:property value="value" />&nbsp;</td> -->
<!-- 								</s:iterator> -->
<!-- 							</s:iterator> -->
<!-- 						</s:if> -->
<!-- 					</s:iterator> -->
				</tbody>
			</table>
	</div>
	<div  class="tab-pane" id="tab_1_1_2">
<!-- 		<div style="width: 100%; height:96%; overflow: auto;"> -->
			<table border="1" style="width: 100%;">
				<tbody>
					<tr>
					<%
						Map marcMap = (Map)request.getAttribute("marcMap");
						if(marcMap.size() > 2){
							List<Map<String,String>> list = (ArrayList)marcMap.get("2");
							for (Map<String, String> map : list) {
								for(Iterator itr = map.entrySet().iterator();itr.hasNext();){
									Map.Entry entry = (Map.Entry)itr.next();
									String key = entry.getKey().toString();
									key = key.substring(0, 3);
									out.print("</tr><tr>");
									out.print("<td class=\"form-control\" style=\"width:15%;text-align:right;\"><label class=\"control-label\"  >");
									out.print(key);
									out.print("</label></td>");
									out.print("<td class=\"form-control\">");
									String value = entry.getValue().toString();
									if(!key.equals("000") && !key.equals("001") && !key.equals("005")){
										String identicator1 = value.substring(0, 1);
										String identicator2 = value.substring(1, 2);
										if(identicator1.equals(" ")){
											out.print("&nbsp;");
										}else{
											out.print(identicator1);
										}
										if(identicator2.equals(" ")){
											out.print("&nbsp;");
										}else{
											out.print(identicator2);
										}
										value = value.substring(2);
									}
									out.print(value);
									out.print("</td>");
								}
							}
						}
					%>
					</tr>
				</tbody>
			</table>
<!-- 		</div> -->
	</div>
	<div  class="tab-pane" id="tab_1_1_3">
<!-- 		<div style="width: 100%; height:96%; overflow: auto;"> -->
		<c:forEach var="outMap" items="${marcMap['3']}" varStatus="st">
			<c:forEach var="mp" items="${outMap}" varStatus="st" >
				<textarea name="marc" rows="20"  style= "width:100%;"><c:out value="${mp}"></c:out></textarea>
			</c:forEach>
		</c:forEach>
<!-- 			<s:iterator value="marcMap" status="st" var="outMap"> -->
<!-- 				<s:if test="key == 3"> -->
<!-- 					<s:iterator value="#outMap.value" status="status" var="mp"> -->
<!-- 							<textarea name="marc" rows="20"  style= "width:100%;"><s:property value="mp" /></textarea> -->
<!-- 					</s:iterator> -->
<!-- 				</s:if> -->
<!-- 			</s:iterator> -->
<!-- 		</div> -->
	</div>
	</div>
</div>
</body>
</html>

