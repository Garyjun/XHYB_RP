<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>知识点详细</title>
	<script type="text/javascript">
	function chakan(url){
		//this.location.href='${path}/railway/gotoKnowledgePreDetail.action?uuid='+url;
		
		$.openWindow("${path}/railway/goto.action?uuid="+url, '详细', 800, 400);
		//$("#createVersion").modal("show");
	}
	
	</script>

</head>
<body  class="yui-skin-sam" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="overflow: auto;" scroll="auto" >
<form:form action="" id="form" modelAttribute="markDe" method="post" class="form-horizontal" role="form">
	<table  width="100%" border="0" cellspacing="1" cellpadding="3"  id="tjjgDiv" >
		<tr>
			<td align="left" class="con_crumbs" style="height: 24px;line-height: 24px;" colspan="2">标引详细 &nbsp;
			</td>
		</tr>
		<tr>
			<td style="border-left:solid 1px #D7D7D7;border-right:solid 1px #D7D7D7;border-bottom:solid 1px #D7D7D7;border-top:solid 1px #D7D7D7;">
				<div style="width:100%">
					<c:choose>
						<c:when test="${markDe != null}">
							<ul>
							<c:forEach items="${markDe}" var="markDe">
								<li id="li" style="MARGIN-BOTTOM: 10px;margin-left:15px;">
									<div class="recorddiv">
									<hr></hr>
									<p>描述：<span style="color: #000;">${markDe.sentence}</span></p>
									<c:choose>
										<c:when test="${markDe.wtype==1}">
											<p>引用主题词:</p>
										</c:when>
										<c:otherwise>
											<p>引用主题词:&nbsp;&nbsp;<a href="javascript:chakan('${markDe.entityReference }')" >${markDe.name}</a></p>
										</c:otherwise>
									</c:choose>
									
									<c:choose>
										<c:when test="${markDe.wtype==1}">
										<p>主题词分类:&nbsp;&nbsp;默认</p>
										</c:when>
										<c:otherwise>
											<p>主题词分类:&nbsp;&nbsp;${markDe.domain }</p>
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${markDe.etype==1}">
											<p>类型：解释型</p>
										</c:when>
										<c:when test="${markDe.etype==0 && markDe.wtype==0}">
											<p>类型：主题词</p>
										</c:when>
										<c:otherwise>
											<p>类型：新词</p>
										</c:otherwise>
									
									</c:choose>
									<c:choose>
										<c:when test="${markDe.extractedFromName==null}">
										</c:when>
										<c:otherwise>
										<p>引用<span style="color: #000;">${markDe.extractedFromName}</span></p>
										</c:otherwise>
									</c:choose>
									<p>开始位置：<span style="color:#000;">${markDe.start}</span>&nbsp;&nbsp;&nbsp;&nbsp;
							     	  结束位置：<span style="color:#000;">${markDe.end}</span>
									</p>
									<p class="unDisplayMore"><br/>...${markDe.selectedText}...</p>
									</div>
								</li>
							</c:forEach>
							</ul>
						</c:when>
						<c:otherwise>
						没有数据
						</c:otherwise>
					</c:choose>
				</div>
			</td>
		</tr>
	</table>
</form:form>
</body>
</html>