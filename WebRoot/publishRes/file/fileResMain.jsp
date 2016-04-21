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
		$(function(){
			function reinitIframe()
			{
			var iframe = document.getElementById("work_main");
			try{
			var bHeight = iframe.contentWindow.document.body.scrollHeight;
			var dHeight = iframe.contentWindow.document.documentElement.scrollHeight;
			var height = Math.max(bHeight, dHeight);
			iframe.height = height;
			}catch (ex){}
			}
			window.setInterval("reinitIframe()", 200);
			queryForm();
		});
		
		function queryForm(en){
			var url = '${path}/publishRes/file/fileResList.jsp';
			var resName = $('#resName').val();
			if(resName!=null&&resName!=undefined){
				resName = encodeURI(resName);
			}
			//清空
			if(en!=undefined){
				$('#fileExtensionName').val('');
				$('#publishType').val('');
				$('#modifiedStartTime').val('');
				$('#modifiedEndTime').val('');
				$('#resName').val('');
				resName='';
			}
			var createUser = $('#createUser').val();
			if(createUser!=null&&createUser!=undefined){
				createUser = encodeURI(createUser);
			}
			//startDate='+$('#modifiedStartTime').val()+'&endDate='+$('#modifiedEndTime').val()+'&createUser='+createUser+'
			url += '?resName='+resName+'&publishType='+$('#publishType').val()+'&fileExtensionName='+$('#fileExtensionName').val()+'&startDate='+$('#modifiedStartTime').val()+'&endDate='+$('#modifiedEndTime').val();
			$('#work_main').attr('src',url);
		}
		
		function formClear() {
			$('#modifiedStartTime').val('');
			$('#modifiedEndTime').val('');
			$('#resName').val('');
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
				
					<div class="by-form-row clearfix">
					    <div class="by-form-title"><label for="abc">资源类型：</label></div>
					    <div class="by-form-val">
					    														<!-- 默认查询的资源类型为图书资源 -->
					        <app:selectResType name="publishType" id="publishType" selectedVal=""  headName="全部"  headValue=""  readonly =""/>
					    </div>
					</div>
				
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="channelName">资源名称：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control" name="resName" id="resName" />
						</div>
					</div>
					
<!-- 					<div class="by-form-row clearfix"> -->
<!-- 						<div class="by-form-title"> -->
<!-- 							<label for="createUser">创建者：</label> -->
<!-- 						</div> -->
<!-- 						<div class="by-form-val"> -->
<!-- 							<input class="form-control" name="createUser" id="createUser" /> -->
<!-- 						</div> -->
<!-- 					</div> -->
					
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="createUser">扩展名：</label>
						</div>
						<div class="by-form-val">
							<app:select name="fileExtensionName" indexTag="fileExtensionName" id="fileExtensionName"   headName="全部"  headValue=""  />
						</div>
					</div>
					
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="modifiedStartTime">开始时间：</label>
						</div>
						<div class="by-form-val">
<!-- 						 HH:mm:ss -->
							<input class="form-control Wdate"
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})"
								id="modifiedStartTime" name="startDate" />
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="modifiedEndTime">结束时间：</label>
						</div>
						<div class="by-form-val">
<!-- 						 HH:mm:ss -->
							<input class="form-control Wdate"
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})"
								id="modifiedEndTime" name="endDate" />
						</div>
					</div>
 					<div class="tab-pane" id="tab_1_1_3">
						<div class="by-form-row by-form-action">
							<a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
							<a class="btn btn-default blue" href="###" onclick="queryForm('1');">清空</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
		<iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="fileResList.jsp"></iframe>
	</div>
</body>
</html>