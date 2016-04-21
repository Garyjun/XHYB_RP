<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>基础资源管理</title>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		$(function(){
			queryForm();
		});
		function queryForm(){
			var url = 'orphanList.jsp';
			url += '?type='+$('#type').val()+'&status='+$('#status').val()+'&title='+$('#title').val()
					+'&creator='+$('#creator').val()+'&keywords='+$('#keywords').val()+
					'&modifiedStartTime='+$('#modifiedStartTime').val()+'&modifiedEndTime='+$('#modifiedEndTime').val()+'&description='+$('#description').val();
			$('#work_main').attr('src',url);
		};
		
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
			<form id="queryForm1" action="" target="work_main" method="post">
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">资源类型：</label></div>
				    <div class="by-form-val">
				    	<app:constants name="type" id="type" repository="com.brainsoon.system.support.SystemConstants" className="ResourceType" inputType="select" ignoreKeys="T00" selected="1" cssType="form-control"></app:constants>
				    </div>
				</div>
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">资源状态：</label></div>
				    <div class="by-form-val">
				    	<app:constants name="status" id="status" repository="com.brainsoon.system.support.SystemConstants" className="ResourceStatus" inputType="select" headerValue="全部" cssType="form-control"></app:constants>
				    </div>
				</div>
				<div class="by-form-row clearfix">
					<div class="by-form-title"><label for="title">资源标题：</label></div>
						<div class="by-form-val">
							<input class="form-control" id="title" name="title"/>
						</div>
				</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="creator">制作者：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control" id="creator" name="creator" />
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="keywords">关键字：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control" id="keywords" name="keywords" />
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="description">摘要：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control" id="description" name="description" />
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="modifiedStartTime">开始时间：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control Wdate"
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})"
								id="modifiedStartTime" name="modifiedStartTime" />
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="modifiedEndTime">结束时间：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control Wdate"
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})"
								id="modifiedEndTime" name="modifiedEndTime" />
						</div>
					</div>
<!-- 					<div class="by-form-row clearfix"> -->
						<!-- <div class="by-form-title">
							<label for="button"></label>
						</div> -->
						     <div class="by-form-row by-form-action">
                                   <a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
                                   <a class="btn btn-default blue" href="###" onclick="$('#queryForm1')[0].reset();queryForm();">清空</a>
                               </div>
                               </form>
<!-- 					</div>					 -->
				</div>
		</div>
   	</div>
</div>
<div class="by-main-page-body-center" id="byMainPageBodyCenter">
    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="orphanList.jsp"></iframe>
</div>
</body>
</html>