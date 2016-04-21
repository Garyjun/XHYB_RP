<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>专题主页</title>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
	function formclear(){
		$('#name').val('');
		$('#status').val('');
		$('#storeType').val('');
		$('#subject').val('');
		$('#trade').val('');
		$('#collectionStart').val('');
		$('#collectionEnd').val('');
	}
		function query(){
			var url = '${path}/subject/subjectList.jsp';
			var name = $('#name').val();
			var status = $('#status').val();
			var storeType = $('#storeType').val();
			var subject = $('#subject').val();
			var trade = $('#trade').val();
			var collectionStart = $('#collectionStart').val();
			var collectionEnd = $('#collectionEnd').val();
			if(name!=null&&name!=undefined){
				name = encodeURI(encodeURI(name));
			}
			url += '?name=' + name + '&status=' + status + '&storeType=' + storeType + '&subject=' + subject + '&trade=' + trade +'&collectionStart=' +collectionStart +'&collectionEnd=' +collectionEnd;
			$('#work_main1').attr('src',url);
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
				    <div class="by-form-title"><label for="abc">主题库查询</label></div>
				</div>
			</div>
			<div class="by-tool-box">
			<div style="padding-left: 0px;padding-right: 0px;border: 0px;">
					<div class="tab-content" style="border: 0px;">
						<div class="tab-pane active">
							<form id="queryForm1" action="" target="work_main" method="post">
								<div class="by-form-row clearfix">
									<div class="by-form-title" style="height: 10%">
										<label for="abc">名称：</label>
									</div>
									<div class="by-form-val" style="height: 10%">
										<input class="form-control" id="name" name="name"/>
									</div>
									<div class="by-form-title" style="height: 10%">
										<label for="abc">状态：</label>
									</div>
									<div class="by-form-val" style="height: 10%">
										<app:constants name="status" repository="com.brainsoon.resrelease.support.ResReleaseConstant" className="OrderStatus" inputType="select" headerValue="全部" cssType="form-control"></app:constants>
									</div>
									<div class="by-form-title" style="height: 10%">
										<label for="abc">库类别：</label>
									</div>
									<div class="by-form-val" style="height: 10%">
										<app:select name="storeType" id="storeType" indexTag="kuCategory" headName="全部" headValue=""></app:select>
									</div>
									<div class="by-form-title" style="height: 10%">
										<label for="abc">学科：</label>
									</div>
									<div class="by-form-val" style="height: 10%">
										<app:select name="subject" id="subject" indexTag="subject" headName="全部" headValue=""></app:select>
									</div>
									<div class="by-form-title" style="height: 10%">
										<label for="abc">行业：</label>
									</div>
									<div class="by-form-val" style="height: 10%">
										<app:select name="trade" id="trade" indexTag="trade" headName="全部" headValue=""></app:select>
									</div>
									<div class="by-form-title" style="height: 10%">
										<label for="abc">开始时间：</label>
									</div>
									<div class="by-form-val" style="height: 10%">
										<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy',maxDate:'#F{$dp.$D(\'collectionEnd\')}'})" id="collectionStart" name="collectionStart" />
									</div>
									<div class="by-form-title" style="height: 10%">
										<label for="abc">结束时间：</label>
									</div>
									<div class="by-form-val" style="height: 10%">
										<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy',minDate:'#F{$dp.$D(\'collectionStart\')}'})" id="collectionEnd" name="collectionEnd" />
									</div>
								</div>
								<div class="by-tab" style="margin-top:1%;background-color: #e5e5e5;">
									<div class="tab-content" style="margin-top:1%;background-color: #e5e5e5;border-width: 0px;">
										<div class="by-form-row by-form-action">
											<a class="btn btn-default red" href="###" onclick="query();">查询</a>
											<a class="btn btn-default blue" href="###" onclick="formclear();query();">清空</a>
										</div>
									</div>
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
    	<iframe id="work_main1" name="work_main1" width="100%" height="100%" frameborder="0" src="subjectList.jsp"></iframe>
	</div>
</body>
</html>