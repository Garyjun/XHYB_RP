<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
<title>铁路资源标引列表</title>
<script type="text/javascript">

		$(function(){
			var	markName = encodeURI(encodeURI("机车"));
			$("#work_main").attr('src',"${path}/railway/biaoyinmain.action?markName="+markName);
			
		})
		function gotoMarkDetail(markName){
			if(markName!=null&&markName!=undefined){
				markName = encodeURI(encodeURI(markName));
			}
			$("#work_main").attr('src',"${path}/railway/biaoyinmain.action?markName="+markName);
		}
		function queryform(){
			var markName=$("#name").val();
			if(markName!=null&&markName!=undefined){
				markName=encodeURI(encodeURI(markName));
			}
			$("#work_main").attr('src',"${path}/railway/biaoyinmain.action?markName="+markName);
		}

</script>
</head>
<body>
<div class="by-main-page-body-side" id="byMainPageBodySide" style="height: 100%;background-color:#e5e5e5;">
		<form:form action="" id="form" modelAttribute="gnc" method="post"  role="form">
		<div class="form-group">
		 <form action="" id="form" role="form" style="height: 100%;">
				<div id="sideWrap">
					<ul class="page-sidebar-menu"  id="sideMenu">				
						<li class="active">
							<a>
								<span class="title">标引列表</span>
							</a>
						</li>
					</ul>
					&nbsp;&nbsp;&nbsp;&nbsp;<input id="name" type="text" name="name"/><a href="###" class="btn btn-default red" onclick="queryform()">搜索</a>
				<table width="90%" border="0" cellpadding="5" cellspacing="0">
				<c:forEach items="${gnc }" var="gnc">
			    <tr align="center" style="height: 30px;">
			    	<td>
			    	<a href="###" onclick="gotoMarkDetail('${gnc.name}')"><font size="3">${gnc.name}</font></a>

			    	</td>
			    </tr>
			</c:forEach>
			</table>
			</div>
		</form> 
		</div>
		</form:form>
	</div> 
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="biaoyinxinxi.jsp"></iframe>
	</div>

</body>
</html>