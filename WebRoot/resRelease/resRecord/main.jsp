<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>发布记录管理</title>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		$(document).ready(function(){
			queryForm();
			$('#posttype').change(function(){
				var url = "${path}/resRelease/resRecord/resRecordList.jsp?posttype="+$('#posttype').val();
				$('#work_main').attr('src',url);
	//			queryForm();
			});
		});
		
		function queryForm(){
			var url = '${path}/resRelease/resRecord/resRecordList.jsp';
			var chlName = $('#channelName').val();
			if(chlName!=null&&chlName!=undefined){
				chlName = encodeURI(encodeURI(chlName));
			}
			/* var createUserName = $('#createUser').val();
			if(createUserName!=null&&createUserName!=undefined){
				createUserName = encodeURI(createUserName);
			}
			url += '?startDate='+$('#modifiedStartTime').val()+'&endDate='+$('#modifiedEndTime').val()+'&status='+$('#status').val()+'&channelName='+chlName+'&createUser='+createUserName; */
			url += '?startDate='+$('#modifiedStartTime').val()+'&endDate='+$('#modifiedEndTime').val()+'&status='+$('#status').val()+'&channelName='+chlName+'&posttype='+$('#posttype').val();
			$('#work_main').attr('src',url);
		}
		
		function formClear() {
			$('#modifiedStartTime').val('');
			$('#modifiedEndTime').val('');
			$('#channelName').val('');
 			$('#status').val('');
			$('#posttype').val('');
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
					    <div class="by-form-title"><label for="abc">发布途径：</label></div>
					    <div class="by-form-val">
					    											<!-- 默认查询的需求单 -->
					        <%-- <app:selectResType name="publishType" id="publishType" selectedVal=""  headName="全部"  headValue=""  readonly =""/> --%>
					    	 <app:select name="posttype" id="posttype" indexTag="XqdZtk" headName="全部" headValue=""></app:select>
					    </div>
					</div>
				
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="channelName">单据名称：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control" name="channelName" id="channelName" />
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="channelName">单据状态：</label>
						</div>
						<div class="by-form-val">
						<app:constants id="status" name="status" repository="com.brainsoon.resrelease.support.ResReleaseConstant" className="ResReleaseStatus" inputType="select" headerValue="全部" cssType="form-control"></app:constants>
						</div>
					</div>
					
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="modifiedStartTime">开始时间：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control Wdate"
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})"
								id="modifiedStartTime" name="orderDate" />
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="modifiedEndTime">结束时间：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control Wdate"
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})"
								id="modifiedEndTime" name="orderDate" />
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
		<iframe id="work_main" name="work_main" width="100%" height="100%"
			frameborder="0" src="resRecordList.jsp"></iframe>
	</div>
</body>
</html>