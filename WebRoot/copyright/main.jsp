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
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		var libType = '${param.libType}';
		var queryType = 0;
		$(function(){
			$('#module').change(function(){
				queryForm();
			});
			$('#type').change(function(){
				queryForm();
			});
			queryForm();
		});
		
		function queryForm(){
			var url = 'list.jsp?module='+$('#module').val()+'&type='+$('#type').val()+'&queryType=0&libType=1';
			url += '&contractCode='+$('#contractCode').val()+'&crtPerson='+$('#crtPerson').val()+'&authorizer='+$('#authorizer').val();
			url += '&crtType='+$('#crtType').val()+'&authArea='+$('#authArea').val()+'&authStartDateBegin='+$('#authStartDateBegin').val();
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
				    <div class="by-form-title"><label for="abc">资源模块：</label></div>
				    <div class="by-form-val">
				    	<app:constants name="module" repository="com.brainsoon.system.support.SystemConstants" className="ResourceMoudle" inputType="select" selected="1" cssType="form-control"></app:constants>
				    </div>
				</div>
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">资源类型：</label></div>
				    <div class="by-form-val">
				    	<app:constants name="type" repository="com.brainsoon.system.support.SystemConstants" className="ResourceType" inputType="select" ignoreKeys="T10,T11,T00" selected="1" cssType="form-control"></app:constants>
				    </div>
				</div>
                      	<form id="queryForm1" action="" target="work_main" method="post">
                          <div class="by-form-row clearfix">
                              <div class="by-form-title"><label for="title">合同编号：</label></div>
                              <div class="by-form-val">
                              	<input class="form-control" id="contractCode" name="contractCode"/>
                              </div>
                          </div>
                          <div class="by-form-row clearfix">
                              <div class="by-form-title"><label for="creator">版权人：</label></div>
                              <div class="by-form-val">
                                  <input class="form-control" type="text" id="crtPerson" name="crtPerson"/>
                              </div>
                          </div>
                          <div class="by-form-row clearfix">
                              <div class="by-form-title"><label for="keywords">授权人：</label></div>
                              <div class="by-form-val">
                                  <input class="form-control" type="text" id="authorizer" name="authorizer"/>
                              </div>
                          </div>
                           <div class="by-form-row clearfix">
							    <div class="by-form-title"><label for="abc">版权类型：</label></div>
							    <div class="by-form-val">
							    	<app:select name="copyRightMetaData.crtType" indexTag="crtType" id="crtType"  selectedVal="${crtType}" headName="请选择"  headValue=""  />
							    </div>
							</div>   
							<div class="by-form-row clearfix">
							    <div class="by-form-title"><label for="abc">授权地区：</label></div>
							    <div class="by-form-val">
							    	<app:select name="copyRightMetaData.authArea" indexTag="authArea" id="authArea"  selectedVal="" headName="请选择"  headValue=""  />
							    </div>
							</div>   
                          <div class="by-form-row clearfix">
                              <div class="by-form-title"><label for="modifiedStartTime">开始时间：</label></div>
                          </div>
                          <div>
                          <app:datetime property="authStartDateBegin" id="authStartDateBegin" onFocus="WdatePicker({maxDate:'#F{$dp.$D(\\'authStartDateEnd\\')}'})" style="width: 40%;display:inline;"  cssClass="form-control Wdate" />&nbsp;-&nbsp;
 				<app:datetime property="authStartDateEnd" id="authStartDateEnd" onFocus="WdatePicker({minDate:'#F{$dp.$D(\\'authStartDateBegin\\')}'})" style="width: 40%;display:inline;" cssClass="form-control Wdate"/>
                          </div>
                          <div class="by-form-row clearfix">
                              <div class="by-form-title"><label for="modifiedEndTime">结束时间：</label></div>
                          </div>
                          <div>
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
    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src=""></iframe>
</div>
</body>
</html>