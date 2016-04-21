<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>需求单管理</title>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		var libType = '${param.libType}';
		var queryType = 0;
		$(document).ready(function(){
			queryForm();
			
			$('#publishType').change(function(){
				var url = "${path}/resRelease/resOrder/resOrderList.jsp";
				$('#work_main1').attr('src',url);
	//			queryForm();
			});
		});
		
		function queryForm(){
			var url = '${path}/resRelease/resOrder/resOrderList.jsp';
			var chlName = $('#channelName').val();
			if(chlName!=null&&chlName!=undefined){
				chlName = encodeURI(chlName);
			}
			url += '?startDate='+$('#modifiedStartTime').val()+'&endDate='+$('#modifiedEndTime').val()+'&status='+$('#status').val()+'&channelName='+chlName+'&createUser='+$('#createUser').val();
			$('#work_main1').attr('src',url);
		}
		
		function formClear() {
			$('#modifiedStartTime').val('');
			$('#modifiedEndTime').val('');
			$('#channelName').val('');
			$('#status').val('');
			$('#createUser').val('');
		}
	</script>
</head>
<body class="by-frame-body">
	<div class="by-main-page-body-side" id="byMainPageBodySide">
		<div class="page-sidebar">
			<div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn"
				onclick="toggleSideBar(resizeFrameDt)">
				<i class="fa fa-angle-left"></i>
			</div>
			<div id="sideWrap">
				<div class="by-tool-box">
				
					<%-- <div class="by-form-row clearfix">
					    <div class="by-form-title"><label for="abc">资源类型：</label></div>
					    <div class="by-form-val">
					    														<!-- 默认查询的资源类型为图书资源 -->
					        <app:selectResType name="publishType" id="publishType" selectedVal=""  headName="全部"  headValue=""  readonly =""/>
					    </div>
					</div> --%>
				
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="channelName">客户名称：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control" name="channelName" id="channelName" />
						</div>
					</div>
					
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="status">状态：</label>
						</div>
						<div class="by-form-val">
<!-- 							<input class="form-control" name="status" id="status" /> -->
<%-- 							<form:select path="status"  id="processName" items="${statusMap}" class="form-control" />  --%>
							<app:constants name="status" repository="com.brainsoon.resrelease.support.ResReleaseConstant" className="OrderStatus" inputType="select" headerValue="全部" cssType="form-control"></app:constants>
						</div>
					</div>
					
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="createUser">创建者：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control" name="createUser" id="createUser" />
						</div>
					</div>
					
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="modifiedStartTime">开始时间：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control Wdate"
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})"
								id="modifiedStartTime" name="startDate" />
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="modifiedEndTime">结束时间：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control Wdate"
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})"
								id="modifiedEndTime" name="endDate" />
						</div>
					</div>
					<div class="by-tab" style="margin-top:1%;background-color: #e5e5e5;">
						<div class="tab-content" style="margin-top:1%;background-color: #e5e5e5;border-width: 0px;">
							<div class="by-form-row by-form-action">
								<a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
								<a class="btn btn-default blue" href="###" onclick="formClear();queryForm();">清空</a>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
		<iframe id="work_main1" name="work_main1" width="100%" height="100%"
			frameborder="0" src="resOrderList.jsp"></iframe>
	</div>
</body>
</html>