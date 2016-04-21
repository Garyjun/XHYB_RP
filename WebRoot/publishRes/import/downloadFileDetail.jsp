<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>列表</title>
<script type="text/javascript"
	src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/jQueryValidationEngine/js/languages/jquery.validationEngine-zh_CN.js"
	charset="utf-8"></script>
<style>
#flag {
	margin-bottom: 5px;
}
</style>
<script type="text/javascript"> 
	$(function() {

	});
	function codes(){
		var codes = top.index_frame.work_main.getChecked('data_div','objectId').join(',');
		    return codes;
	}
	//查看
	function lookDetail(id){
// 		excelPath = encodeURI(encodeURI(excelPath));
 		$.openWindow("${path}/system/ftpHttpDetail/ftpHttpList.jsp?id="+id+"&publishType="+$('#libType').val()+"&divWidth=1020px",'文件列表',1080,500);
// 		window.location.href = "${path}/system/ftpHttpDetail/ftpHttpList.jsp?id="+id+"&publishType="+$('#libType').val();
	}
	
	</script>

<style type="text/css">
.form-control1 {
	display: block;
	width: 50%;
	height: 34px;
	padding: 6px 12px;
	font-size: 14px;
	line-height: 1.428571429;
	color: #555555;
	background-color: #ffffff;
	background-image: none;
	border: 1px solid #cccccc;
	border-radius: 4px;
	-webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
	box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
	-webkit-transition: border-color ease-in-out .15s, box-shadow
		ease-in-out .15s;
	transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
}

background-image
:url
("selRes
.png
")
 
;
margin-right
:
0px;
 
vertical-align
:top
;
 
*
vertical-align
:middle
}
.target-checkbox {
	
}
</style>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap "
		style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<input type="hidden" id="resIds" name="resIds"
				value="${param.resIds }" /> <input type="hidden" id="publishType"
				name="publishType" value="${param.publishType }" /> <input
				type="hidden" id="targetField" name="targetField"
				value="${param.targetField }" />
				<div class="form-group" align="center"
					style="margin: 20px 10px 10px 0px;">
					<label>已添加到Ftp队列：</label>
<!-- 					<input type="text" class="form-control1" id="searchName" -->
<!-- 						style="display: inline;" name="searchName" placeholder="输入标签名称" -->
<!-- 						qMatch="like"/>  -->
<!-- 						onkeyup="searchComplete()" -->
<!-- 						<input id="search" type="button" value="搜索" -->
<!-- 						style="display: inline;" class="btn btn-primary red" onclick="searchTarget();"/> -->
				</div>
				<div id="showFlag" style="margin-left:70px">
				<div class="row">
					<div class="col-md-5">
						<div class="form-wrap">
							<div class="col-md-3" id="showFlag1"></div>
						</div>
					</div>
					<div class="col-md-5">
						<div class="form-wrap">
							<div class="col-md-3" id="showFlag2"></div>
						</div>
					</div>
				</div>
				</div>
				<div align="center" style="margin-top:10px">
					<button type="button" class="btn btn-default blue"
							onclick="javascript:$.closeFromInner();">关闭</button>
					<button type="button" class="btn btn-default red" onclick="lookDetail();">查看</button>
				</div>
		</div>
	</div>
</body>
</html>