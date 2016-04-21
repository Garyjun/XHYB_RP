<%@page import="com.brainsoon.system.support.SystemConstants.ResourceStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>图书分类统计</title>
<script type="text/javascript">
	 $(function(){
		var div = $('#fakeFrame');
		$.ajax({
			dataType:'text',
			url:'${path}/statistics/bookNum/bookNum.action',
			success:function(data){
				var tableList = $(data);
				tableList.appendTo(div);
			}
			
		});
	}); 
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="width: 85%">
		<!-- <div class="panel panel-default">
		<div class="panel-heading" id="div_head_t">
			<ol class="breadcrumb">
				<li><a href="javascript:;">图书分类统计</a></li>
			</ol>
		</div>
			<div align="center" style="margin-top: 40px;">
				<table width="650" border="1" cellspacing="0" cellpadding="0">
					<tr align="center">
						<td height="50px">一级分类</td>
						<td>分类数量</td>
						<td>二级分类</td>
						<td>分类数量</td>
					</tr>
					<tr align="center">
						<td rowspan=6>法律法规</td>
						<td rowspan=6>157</td>
						<td height="35px">安全生产法律法规</td>
						<td>25</td>
					</tr>
					<tr align="center">
						<td height="35px">煤炭行业法律法规</td>
						<td>20</td>
					</tr>
					<tr align="center">
						<td height="35px">煤炭行业标准</td>
						<td>25</td>
					</tr>
					<tr align="center">
						<td height="35px">能源行业标准</td>
						<td>10</td>
					</tr>
					<tr align="center">
						<td height="35px">安全生产行业标准</td>
						<td>40</td>
					</tr>
					<tr align="center">
						<td height="35px">其他</td>
						<td>37</td>
					</tr>
				</table>
			</div>
		</div> -->
	</div>
</body>
</html>