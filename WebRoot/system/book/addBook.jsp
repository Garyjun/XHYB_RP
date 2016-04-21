<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/appframe/common.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>添加订单</title>
	<script type="text/javascript">
		function save(){
			var versionNum = $("#versionNum").val();
			var code = "";
			if(versionNum>8)
				code =  "V" + (parseInt(versionNum)+1);
			else
				code =  "V0" + (parseInt(versionNum)+1);
			var books = {
					"@type" : "domainList",
					"domainType" : 0
			};
			var domains = [];
			var book = {
					"label" : $("#version").val(),
					"nodeId" :  _getRandomString(8),
					"nodeType" : 0,
					"pid" : "-1",
					"code" : code,
					"level" : 2
			};
			domains.push(book);
			var domainStr = JSON.stringify(domains);
			books["domains"] = domainStr;
			$.post("addBook.action",books,
				function(data) {
					$.closeFromInner();
					location.href="main.jsp";
			});
		}
	</script>
</head>
<body>
	<div class="form-wrap">
			<div class="row form-wrap">
				<label class="col-sm-4 control-label text-right">教材版本*</label>
				<div class="col-sm-5">
					<input type="text" id="version"/>
				</div>
			</div>			
			<div class="form-wrap">
				<div class="col-sm-offset-4 col-sm-9">
                	<input type="hidden" name="token" value="${token}" />
                	<input id="tijiao" type="button" value="提交" class="btn btn-primary" onclick="save();"/> 
                	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
                </div>
            </div>
            <input type="hidden" id="versionNum" value="<%=request.getParameter("versionNum")%>"/>
	</div>
</body>
</html>
