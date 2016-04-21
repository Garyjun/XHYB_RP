<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<% int count =0; %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>知识点详细</title>
<style type="text/css">
#regulars .even {
	background-color: #DEDEf9; /* pale blue for even rows */
}

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

body {
	background-color: #ccddff;
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
	$(document).ready(function() {
		$('#regulars tr:even').addClass('even'); //奇偶变色，添加样式 
	});
	function onClickSave() {
		this.location.href = '${path}/railway/guizeAdd.jsp';
	}
	function onClickRegular(type) {
		//		this.location.href='${pageContext.request.contextPath}/doRegular.action?type='+type;
		var url = '${pageContext.request.contextPath}/doRegular.action?type='
				+ type;
		$.ajax({
			url : url,
			type : 'get',
			async : false,
			datatype : "json",
			beforeSend : function(XMLHttpRequest) {
			},
			success : function(data) {
				if (data.substring(11, 12) == 1) {
					alert("推理成功！");
				} else {
					alert("推理失败！");
				}
			},
			complete : function(XMLHttpRequest, textStatus) {
				//
			},
			error : function() {
				alert("获取数据出错！");
			}
		});
	}
	function tuili(){
		$.ajax({
			url:'${path}/railway/tuiliguize.action',
			type : 'get',
			success : function(data) {
				if(data==0){
					alert("推理成功！");
				}
			}
		});
		
		
//		this.location.href="${path}/railway/tuiliguize.action";
	}
</script>

</head>
<body class="yui-skin-sam" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0" style="overflow: auto;">
	<div class="con_L_search" align="left">
		<table id="regulars" width="100%" border="0" cellpadding="5"
			cellspacing="0">
			<tr style="height: 26px;">
				<th style="width: 70%; text-align: center;">条件</th>
				<th style="width: 30%; text-align: center;">结论</th>
			</tr>
		<c:choose>
			<c:when test="${rules!=null }">
				<c:forEach items="${rules }" var="rules" varStatus="stat">

					<tr>
						<!-- 数据显示 -->

					<td width="40%">
					<table border="0">
						<tr align="center">
							<c:forEach items="${rules.conditions}" var="conditions" varStatus="status">
								<c:choose>
									<c:when test="${status.index ==5}">
										<tr align="center">
									</c:when>
									<c:otherwise>
										<td>
											<input name="predicate" value="${conditions.objectDesc }"readonly="readonly" size="4" /> 
											<input name="predicate"value="${conditions.predicateDesc }" readonly="readonly"size="5" />
											<input name="subject"value="${conditions.subjectDesc }" readonly="readonly" size="4" />
											&nbsp;<a></a>
											<c:if test="${!status.last}"><a>且</a></c:if>
											&nbsp;<a></a>
										</td>
										&nbsp;<a></a>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</tr>
					</table>
				</td>
				<!-- 结论 -->
				<td align="center" height="22px" width="60%" >
					<input name="object" value=" ${rules.result.objectDesc }" disabled="disabled" size="6" /> 
					<input name="predicate" value="${rules.result.predicateDesc }" disabled="disabled" size="7" /> 
					<input name="subject" value="${rules.result.subjectDesc }" disabled="disabled" size="6" />
				</td>
		</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td bgcolor="#ffffff" height="22px">数据不存在！</td>
				</tr>

			</c:otherwise>
		</c:choose>
		</table>
		<div align="center">
			<input type="button" class="btn btn-default red" value="创建规则"
				onclick="onClickSave();" />
				<input type="button" class="btn btn-default red" value="推理规则"
				onclick="tuili();" />
		</div>
	</div>
</body>
</html>