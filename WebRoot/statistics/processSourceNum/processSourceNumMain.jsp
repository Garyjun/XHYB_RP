<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>资源加工数量统计</title>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
    <script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		$(function(){
			queryForm();
		});
		
		function queryForm(){
			var resName = encodeURI(encodeURI($('#resName').val()));
			var taskName = encodeURI(encodeURI($('#taskName').val()));
			var processStatus = encodeURI(encodeURI($('#processStatus').val()));
			var processName = encodeURI(encodeURI($('#processName').val()));
			var url = 'list.jsp?taskName='+taskName+"&resName="+resName+"&processStatus="+processStatus+"&processName="+processName
					+"&startTime="+$("#startTime").val()+"&endTime="+$("#endTime").val();
			$('#work_main').attr('src',url);
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
				<div class="by-tab">
                       <div class="tab-content">
                           <div class="tab-pane active" id="tab_1_1_1">
							<form id="queryForm" action="" target="work_main" method="post">
							   <div class="by-form-row clearfix">
								    <div class="by-form-title"><label for="abc">资源名称：</label></div>
								    <div class="by-form-val">
								    	<input class="form-control" id="resName" name="resName"/>
								    </div>
								</div>
								<div class="by-form-row clearfix">
								    <div class="by-form-title"><label for="abc">资源状态：</label></div>
								    <div class="by-form-val">
								    	<app:constants name="processStatus" repository="com.brainsoon.system.support.SystemConstants" className="SourceStatus" inputType="select" headerValue="全部" cssType="form-control"></app:constants>
								    </div>
								</div>
								<div class="by-form-row clearfix">
								    <div class="by-form-title"><label for="abc">任务名称：</label></div>
								    <div class="by-form-val">
								    	<input class="form-control" id="taskName" name="taskName"/>
								    </div>
								</div>
								<div class="by-form-row clearfix">
								    <div class="by-form-title"><label for="abc">加工员：</label></div>
								    <div class="by-form-val">
								    	<input class="form-control" id="processName" name="processName"/>
								    </div>
								</div>
								
								<div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="startTime">开始时间：</label></div>
                                   <div class="by-form-val">
                                      <input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'})" id="startTime" name="startDate" />
                                   </div>
                               </div>
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="endTime">结束时间：</label></div>
                                   <div class="by-form-val">
    								 <input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startTime\')}'})" id="endTime" name="endDate" />
                                   </div>
                               </div>
								
                               <div class="by-form-row by-form-action">
                                   <a class="btn btn-default red" href="###" onclick="queryForm();">查询</a>
                                   <a class="btn btn-default blue" href="###" onclick="$('#queryForm')[0].reset();queryForm();">清空</a>
                               </div>
                            </form>
                           </div>
                       </div>
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