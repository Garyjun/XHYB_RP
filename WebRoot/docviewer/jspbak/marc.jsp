<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>MARC数据信息</title>
<%
	String appPath = request.getContextPath();
%>
<style type="text/css" media="screen">
body{ margin: 0; padding: 0px;margin-left: 0;margin-right: 0;overflow: hidden; }
.dis {
	display: block;
}

.undis {
	display: none;
}
</style>
<script type="text/javascript">
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
<body class="yui-skin-sam" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" style="overflow: auto;" scroll="auto">
	<div class="con_crumbs" id="bt" style="height: 2%;">MARC数据信息</div>
	<div id="tab-container" class="yui-navset" style="margin-top:5px;width:100%;">
		<ul class="yui-nav">
			<li id="tb_1" class="selected" onclick="HoverLi(1);"><a href="###"><em>表单视图</em> </a></li>
			<li id="tb_2" onclick="HoverLi(2);"><a href="###"><em>字段视图</em></a></li>
			<li id="tb_3" onclick="HoverLi(3);"><a href="###"><em>文件视图</em></a></li>
		</ul>
	</div>
	<div class="dis" id="tbc_01">
		<div style="width: 100%; height:96%; overflow: auto;">
			<table border="0" cellspacing="1" cellpadding="3" align="center" class="FORM" style="width: 100%;">
				<tbody>
					<tr>
					<s:iterator value="marcMap" status="st" var="outMap">
						<s:if test="key == 1">
							<s:iterator value="#outMap.value" status="status" var="mp">
								<s:iterator value="#mp" status="status" var="marc">
									<s:if test="#status.odd">
										</tr><tr>
									</s:if>
									<td class="label" style="width:10%;">
										<s:if test="key.length() > 4">
											<s:set var="marc" value="key.substring(3)"></s:set>
										</s:if>
										<s:else>
											<s:set var="marc" value="key"></s:set>
										</s:else>
										<s:if test="#marc.indexOf('--') > -1">
											<s:property value="#marc.substring(0,#marc.indexOf('--'))" />
										</s:if>
										<s:else>
											<s:property value="marc" />
										</s:else>
										
									</td>
									<td class="content"><s:property value="value" />&nbsp;</td>
								</s:iterator>
							</s:iterator>
						</s:if>
					</s:iterator>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	<div class="undis" id="tbc_02">
		<div style="width: 100%; height:96%; overflow: auto;">
			<table border="0" cellspacing="1" cellpadding="3" align="center" class="FORM" style="width: 100%;">
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
									out.print("<td class=\"label\" style=\"width:10%;\">");
									out.print(key);
									out.print("</td>");
									out.print("<td class=\"content\">");
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
		</div>
	</div>
	<div class="undis" id="tbc_03">
		<div style="width: 100%; height:96%; overflow: auto;">
			<s:iterator value="marcMap" status="st" var="outMap">
				<s:if test="key == 3">
					<s:iterator value="#outMap.value" status="status" var="mp">
							<textarea name="marc" rows="20"  style= "width:100%;"><s:property value="mp" /></textarea>
					</s:iterator>
				</s:if>
			</s:iterator>
		</div>
	</div>
</body>
</html>

