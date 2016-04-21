<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>操作日志查询</title>
<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" />
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/autocomplete/jquery.bigautocomplete.js"></script>
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/autocomplete/jquery.bigautocomplete.css"/>
<style type="text/css">
	html,body {height: 100%;}
	.container { width: 800px; margin: 0 auto; }
	.autocomplete-suggestions { border: 1px solid #999; background: #FFF; cursor: default; overflow: auto; -webkit-box-shadow: 1px 4px 3px rgba(50, 50, 50, 0.64); -moz-box-shadow: 1px 4px 3px rgba(50, 50, 50, 0.64); box-shadow: 1px 4px 3px rgba(50, 50, 50, 0.64); }
	.autocomplete-suggestion { padding: 2px 5px; white-space: nowrap; overflow: hidden; }
	.autocomplete-no-suggestion { padding: 2px 5px;}
	.autocomplete-selected { background: #F0F0F0; }
	.autocomplete-suggestions strong { font-weight: normal; color: #3399FF; }
</style>
<script type="text/javascript">
	$(document).ready(function(){
		$.ajax({
			url: "${path}/log/getSimilarWords.action?query=",
			type: 'get',
			datatype: 'json',
			success: function(data){
				if(data != null && data !=""){
					var json =  eval("(" + data + ")");
					$("#operator").bigAutocomplete(json);
				}
			}
		});
		
		$("#opModules").change(function(){
			$("#opModules option").each(function(i,o){
				if($(this).attr("selected")){
					 $.post("${path}/log/getOperateTypes.action?random="+Math.random()+"&moduleId="+o.value,'', function(data){
						 var da = (eval('('+data+')'));
						 var html ='';
						 if(da==''){
							 $('#opLabel_div').hide();
							 $('#opValue_div').hide();
						 }else{
							 html = "<option value=''>全部</option>";
							 for(var i = 0 , len = da.length;i < len;i++){  
				                 html += "<option value="+ da[i].id +">" + da[i].operateName + "</option>";  
				         	 }
							 $('#opLabel_div').show();
							 $('#opValue_div').show();
							 $('#operateTypes').html(html).show(); 
						}
			        });  
				 }
			 })
		 })
		 
		$("#opModules").change();
		query();


// 	    $('#autocomplete-ajax').autocomplete({
// 	        serviceUrl: '${path}/log/getSimilarWords.action',
// 	        width: 200,//宽度
//             scrollHeight: 300,   //提示的高度，溢出显示滚动条
//             matchContains: true,    //包含匹配，就是data参数里的数据，是否只要包含文本框里的数据就显示
//             autoFill: false,   //自动填充
//             dataType: 'json',//数据类型
//             deferRequestBy:500,
// 	        onSelect: function(suggestion) {
// 	        	alert(suggestion.value);
// 	            $('#operator').val(suggestion.value);
// 	        },
// 	        onHint: function (hint) {
// 	            $('#autocomplete-ajax-x').val(hint);
// 	        },
// 	        onInvalidateSelection: function() {
// 	            //$('#selction-ajax').html('没有匹配的用户');
// 	        }
// 	    });
		
		
	})

	function query() {
		var url = '${path}/system/log/list.jsp?moduleId=' + $('#opModules').val() 
				+ '&optype='+ $('#operateTypes').val() 
				+ '&startDate='+ $('#modifiedStartTime').val() 
				+ '&operator='+ encodeURI(encodeURI($('#operator').val())); 
				+ '&endDate=' + $('#modifiedEndTime').val();
		$('#work_main').attr('src', url);
	}
	
	function formClear() {
		$('#opModules').val('');
		$('#opLabel_div').hide();
		$('#opValue_div').hide();
		$('#operateTypes').val('');
		$('#operator').val('');
		$('#modifiedStartTime').val('');
		$('#modifiedEndTime').val('');
		query();
	}
	
</script>
</head>
<body class="by-frame-body">
	<div class="by-main-page-body-side" id="byMainPageBodySide">
		<div class="page-sidebar">
			<div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
				<i class="fa fa-angle-left"></i>
			</div>
			<div id="sideWrap">
				<div class="by-tool-box">
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="abc">操作模块：</label>
						</div>
						<div class="by-form-val">
							<form:select path="moduleList" id="opModules" class="form-control" cssStyle="width: 120px">
								<form:option value="" label="全部" />
								<form:options items="${moduleList}"/>
							</form:select>
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div id="opLabel_div" class="by-form-title">
							<label for="abc">操作类型：</label>
						</div>
						<div id="opValue_div" class="by-form-val">
							<select id="operateTypes" name="optype" class="form-control" style="width: 120px">
								<option value="">全部</option>
							</select>
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="modifiedStartTime">开始时间：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})" id="modifiedStartTime" name="startDate" />
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="modifiedEndTime">结束时间：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})" id="modifiedEndTime" name="endDate" />
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="abc">操作人：</label>
						</div>
						<div class="by-form-val">
					        <input type="text" class="form-control" id="operator" name="operator"  style="z-index: 2;"/>
						</div>
					</div>
					<div class="by-form-row by-form-action">
						<a class="btn btn-default red" href="###" onclick="query()">查询</a> 
						<a class="btn btn-default blue" href="###" onclick="formClear()">清空</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
		<iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src=""></iframe>
	</div>
</body>
</html>