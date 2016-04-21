<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>版权预警管理</title>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		$(function(){
			$('#publishType').change(function(){
				queryForm();
			});
			queryForm();
		});
		function queryForm(){
			var url = "${path}/publishCopyright/warningList.jsp?publishType="+$('#publishType').val();
			url += '&authStartDateBegin='+$('#authStartDateBegin').val();
			url += '&authStartDateEnd='+$('#authStartDateEnd').val()+'&authEndDateBegin='+$('#authEndDateBegin').val();
			url += '&authEndDateEnd='+$('#authEndDateEnd').val();
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
				<div class="by-form-row clearfix">
				     <div class="by-form-title"><label for="abc">资源类型：</label></div>
				    <div class="by-form-val">
				    	<app:select name="publishType" indexTag="publishType" id="publishType"  selectedVal="${publishType}" headName=""  headValue=""  />
				    </div>
				</div>
                      	<form id="queryForm1" action="" target="work_main" method="post">
                          <div class="by-form-row clearfix">
                              <div class="by-form-title"><label for="modifiedStartTime">开始时间：</label></div>
                          </div>
                           <div class="by-form-val">
                           <app:datetime property="authStartDateBegin" id="authStartDateBegin" onFocus="WdatePicker({maxDate:'#F{$dp.$D(\\'authStartDateEnd\\')}'})" style="width: 40%;display:inline;"  cssClass="form-control Wdate" />&nbsp;-&nbsp;
 				<app:datetime property="authStartDateEnd" id="authStartDateEnd" onFocus="WdatePicker({minDate:'#F{$dp.$D(\\'authStartDateBegin\\')}'})" style="width: 40%;display:inline;" cssClass="form-control Wdate"/>
                          </div>
                          <div class="by-form-row clearfix">
                              <div class="by-form-title"><label for="modifiedEndTime">结束时间：</label></div>
                          </div>
                           <div class="by-form-val">
                          <app:datetime property="authEndDateBegin" id="authEndDateBegin" onFocus="WdatePicker({maxDate:'#F{$dp.$D(\\'authEndDateEnd\\')}'})" style="width: 40%;display:inline;"  cssClass="form-control Wdate" />&nbsp;-&nbsp;
 				<app:datetime property="authEndDateEnd" id="authEndDateEnd" onFocus="WdatePicker({minDate:'#F{$dp.$D(\\'authEndDateBegin\\')}'})" style="width: 40%;display:inline;" cssClass="form-control Wdate"/>
                          </div>
                               <div class="by-form-row by-form-action">
                                   <a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
                                   <a class="btn btn-default blue" href="###" onclick="$('#queryForm1')[0].reset();queryForm();">清空</a>
                               </div>
                              </form>
                     
           	</div>
		</div>
   	</div>
</div>
<div class="by-main-page-body-center" id="byMainPageBodyCenter">
    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="${pageContext.request.contextPath}/copyright/warningList.jsp"></iframe>
</div>
</body>
</html>