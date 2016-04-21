<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>详细页面</title>
<style type="text/css">
.con_crumbs{
color: #000;
padding-left: 18px;
font-weight: bold;
background-color: #cceeff;
}
.con_L_searchA{
background-color: rgb(230,228,228);
background-position-x:8px;
background-position-y:3px;
background-repeat: no-repeat;
color: #000;
font-family: 宋体;
font-size: 12px;
font-style: normal;
font-weight: bold;
font-variant: normal;
height: 22px;
line-height: 22px;
}
.con_L_search_box{
color: #000;
font-family: 宋体;
font-size: 12px;
font-style: normal;
font-variant: normal;
font-weight: normal;
line-height: normal;
padding-bottom: 8px;
padding-left: 40px;
padding-right: 40px;
padding-top: 8px;
width: 99%;

}

</style>
</head>
<body  class="yui-skin-sam" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="overflow: auto;" scroll="auto" >
	<table  width="100%" border="0" cellspacing="1" cellpadding="3"  id="tjjgDiv" >
				<tr>
					<td width="80%" style="border-left:solid 1px #D7D7D7;border-right:solid 1px #D7D7D7;border-bottom:solid 1px #D7D7D7;border-top:solid 1px #D7D7D7;">
						<table width="100%">
						<tr>
							<td align="left" class="con_crumbs" style="height: 24px;line-height: 24px;width:99%;" colspan="2">
								&nbsp;&nbsp;&nbsp;&nbsp;${medicineName} &nbsp;
							</td>
						</tr>
							<c:choose>
								<c:when test="${disease.indication==null && disease.principle==null && disease.sideEffect==null && disease.restriction==null}">
									<tr align="left"><td height="23px">
										<div class="con_L_search_box" align="left" style="width:99%;"><hr></hr><strong>自动摘要:</strong><br/></div></td></tr>
									<tr align="left"><td height="23px">
										<div class="con_L_search_box" align="left"><hr></hr><strong>临床表现:</strong><br/>&nbsp;&nbsp;&nbsp;&nbsp;${disease.clinicalFeature}</div></td></tr>
									<tr align="left"><td height="23px">
										<div class="con_L_search_box" align="left"><hr></hr><strong>治疗:</strong><br/>&nbsp;&nbsp;&nbsp;&nbsp;${disease.treatment}</div></td></tr>
									<tr align="left"><td height="23px">
										<div class="con_L_search_box" align="left"><hr></hr><strong>描述:</strong><br/>&nbsp;&nbsp;&nbsp;&nbsp;${disease.text}</div></td></tr>
								</c:when>
								<c:otherwise>
									<tr align="left"><td height="23px">
										<div class="con_L_search_box" align="left" style="width:99%;"><hr></hr><strong>适应症:</strong><br/>&nbsp;&nbsp;&nbsp;&nbsp;${disease.indication}</div></td></tr>
									<tr align="left"><td height="23px">
										<div class="con_L_search_box" align="left"><hr></hr><strong>原理:</strong><br/>&nbsp;&nbsp;&nbsp;&nbsp;${disease.principle}</div></td></tr>
									<tr align="left"><td height="23px">
										<div class="con_L_search_box" align="left"><hr></hr><strong>副作用:</strong><br/>&nbsp;&nbsp;&nbsp;&nbsp;${disease.sideEffect}</div></td></tr>
									<tr align="left"><td height="23px">
										<div class="con_L_search_box" align="left"><hr></hr><strong>禁忌:</strong><br/>&nbsp;&nbsp;&nbsp;&nbsp;${disease.restriction}</div></td></tr>
								</c:otherwise>
							</c:choose>

							</table>
					</td>
					<td valign="top">
					<c:choose>
						<c:when test="${disease.indication==null && disease.principle==null && disease.sideEffect==null && disease.restriction==null}">
							<div class="con_L_searchA" align="left">治疗药物&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="###" >更多</a></div>
								<div  style="padding: 5px 5px;">
									<table width="100%" border="0" cellpadding="5" cellspacing="0" >
									<c:choose>
									<c:when test="${disease.medicines == null}">
										<tr>	
											<td bgcolor="#ffffff" height="22px">
											数据不存在！
											</td>
										</tr>
									</c:when>
									<c:otherwise>
										<c:forEach  items="${disease.medicines}" var="d">
										<tr>
											<td align="left" height="22px"><a href="###">${d }</a></td></tr>
										</c:forEach>
									</c:otherwise>
									</c:choose>
									</table>
								</div>
								<div class="con_L_searchA" align="left">相关症状体征&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="###">更多</a></div>
									<div  style="padding: 5px 5px;">
									<table width="100%" border="0" cellpadding="5" cellspacing="0" >
									<c:choose>
										<c:when test="${disease.semiotics == null}">
											<tr>
												<td bgcolor="#ffffff" height="22px">
												数据不存在！
												</td>
											</tr>
										</c:when>
										<c:otherwise>
											<c:forEach  items="${disease.semiotics }" var="a">
											<tr>
												<td align="left" height="22px"><a href="###">${a }</a></td></tr>
											</c:forEach>
										</c:otherwise>
									</c:choose>
									</table>
									</div>
								<div class="con_L_searchA" align="left">相关手术操作&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="###">更多</a></div>
									<div  style="padding: 5px 5px;">
									<table width="100%" border="0" cellpadding="5" cellspacing="0" >
									<c:choose>
										<c:when test="${disease.operations == null}">
											<tr>
												<td bgcolor="#ffffff" height="22px">
												数据不存在！
												</td>
											</tr>
										</c:when>
										<c:otherwise>
											<c:forEach items="${disease.operations}" var="opera">
											<tr>
												<td align="left" height="22px"><a href="###">${opera}</a></td></tr>
											</c:forEach>
										</c:otherwise>
									</c:choose>
									</table>
									</div>
								<div class="con_L_searchA" align="left">相关实验检查&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="###">更多</a></div>
									<div  style="padding: 5px 5px;">
									<table width="100%" border="0" cellpadding="5" cellspacing="0" >
									<c:choose>
										<c:when test="${disease.examines == null}">
											<tr>
												<td bgcolor="#ffffff" height="22px">
												数据不存在！
												</td>
											</tr>
										</c:when>
										<c:otherwise>
											<c:forEach  items="${disease.examines}" var="exa">
											<tr>
												<td align="left" height="22px"><a href="###">${exa }</a></td></tr>
											</c:forEach>
										</c:otherwise>
									</c:choose>
									</table>
									</div>
								<div class="con_L_searchA" align="left">相关疾病&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="###">更多</a></div>
									<div  style="padding: 5px 5px;">
									<table width="100%" border="0" cellpadding="5" cellspacing="0" >
									<c:choose>
										<c:when test="${disease.relateds == null}">
											<tr>
												<td bgcolor="#ffffff" height="22px">
												数据不存在！
												</td>
											</tr>
										</c:when>
										<c:otherwise>
											<c:forEach  items="${disease.relateds}" var="rela">
											<tr>
												<td align="left" height="22px"><a href="###">${rela}</a></td></tr>
											</c:forEach>
										</c:otherwise>
									</c:choose>
									</table>
									</div>
						</c:when>
						<c:otherwise>
							<div class="con_L_searchA" align="left">相关药物&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="###" >更多</a></div>
							<div  style="padding: 5px 5px;">
								<table width="100%" border="0" cellpadding="5" cellspacing="0" >
									<c:choose>
									<c:when test="${disease.relateds == null}">
										<tr>	
											<td bgcolor="#ffffff" height="22px">
											数据不存在！
											</td>
										</tr>
									</c:when>
									<c:otherwise>
										<c:forEach  items="${disease.relateds}" var="d">
										<tr>
											<td align="left" height="22px"><a href="###">${d }</a></td></tr>
										</c:forEach>
									</c:otherwise>
									</c:choose>
									</table>
								</div>
						</c:otherwise>
					</c:choose>
					</td>
				</tr>
	</table>
</body>
</html>