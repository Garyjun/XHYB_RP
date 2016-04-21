<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>列表</title>
<script type="text/javascript" src="${path}/appframe/plugin/autocomplete/jquery.autocomplete.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/autocomplete/jquery.bigautocomplete.js"></script>
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/autocomplete/jquery.bigautocomplete.css"/>
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
	$("#tt").bigAutocomplete({data:[{title:"book"},
	                                {title:"blue"},
	                                {title:"fool"},
	                                {title:"bus"}]});
// 	var url_ = "${path}/target/autoCom.action";
// 	$("#tt").bigAutocomplete({data:[{title:"book",result:"booooook"},
// 	                                    {title:"blue",result:"bluuuuue"},
// 	                                    {title:"fool"},
// 	                                    {title:"bus",result:[1,2,3]}]
});
	</script>

</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap "
		style="height: 100%;">
		<div class="regist" >
						下拉补全框的数据为：["book","blue","fool","bus"] <br />
						<input type="text" id="tt" style="width:300px;" />
		</div>
		
		<div class="panel panel-default" style="height: 100%;">
		</div>
	</div>
</body>
</html>