<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>用户管理</title>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		var libType = '${target.libType}';
		var queryType = 0;
		$(function(){
			//var title = "${APP_USER_SESSION_KEY.platformId}";
			$('#module').change(function(){
				queryForm();
			});
			$('#status').change(function(){
				queryForm();
			});
			queryForm();
			typeChange();
		});
		
		function queryForm(){
			/* var title = "${APP_USER_SESSION_KEY.platformId}";
			var targetName = $('#targetName').val();
			var	module = $('#module').val(); */
			//var url ='targetList.jsp?status='+$('#status').val()+'&module='+module+'&targetName='+targetName+"&typeTarget="+$('#typeTarget').val();
			var loginName = $('#loginName').val();
			var userName = $('#userName').val();
			var orgnameByOrgId = $('#orgnameByOrgId').val();
			var useridByGroup = $('#useridByGroup').val();
			var status = $('#status').val();
			var url = 'userList.jsp?loginName='+loginName+'&userName='+userName+'&orgnameByOrgId='+orgnameByOrgId+'&useridByGroup='+useridByGroup+'&status='+status;
			$('#work_main').attr('src',url);
		}
		
		function typeChange() {
			var title = "${APP_USER_SESSION_KEY.platformId}";
			if(title==1)
			{
				$('#div2').hide();
			}else
			{
				$('#div1').hide();
			}
		}
	
	</script>
</head>
<body class="by-frame-body">
<input type="hidden" id="typeTarget" name="typeTarget" value="${param.pMenuId}"/>
<div class="by-main-page-body-side" id="byMainPageBodySide">
    <div class="page-sidebar">
        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
            <i class="fa fa-angle-left"></i>
        </div>
		<div id="sideWrap">
			<div class="by-tool-box">
				<form id="queryForm1" action="" target="work_main" method="post">
                 <div class="by-form-row clearfix">
                    <div class="by-form-title"><label for="title">登录名：</label></div>
                    <div class="by-form-val">
                    	<input type="text" class="form-control" id="loginName" name="loginName" qMatch="like"  placeholder="" />
                    </div>
                </div>
                <div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">用户名：</label></div>
				    <div class="by-form-val">
				        <input type="text" class="form-control" id="userName" name="userName"  qMatch="like" placeholder="" />
				    </div>
				</div>
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">组织部门：</label></div>
				    <div class="by-form-val">
				    	<input type="text" class="form-control" id="orgnameByOrgId" name="orgnameByOrgId"  qMatch="like" placeholder="" />
				    </div>
				</div>
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">用户组：</label></div>
				    <div class="by-form-val">
				    	 <input type="text" class="form-control" id="useridByGroup" name="useridByGroup"  qMatch="like" placeholder="" />
				    </div>
				</div>
				 <div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">用户状态：</label></div>
				    <div class="by-form-val">
				    	<app:constants name="status" id="status" repository="com.brainsoon.resrelease.support.SysParamsTemplateConstants" className="userStatus" inputType="select" cssType="form-control"></app:constants>
				    </div>
				</div>
				
				<!-- <div class="by-form-row clearfix">
				    <select name="status" id="status" class="form-control">
						<option value="-1">全部</option>
						<option value="1">启用</option>
						<option value="0">禁用</option>
					</select>
				</div> -->
					 <div class="by-tab" style="margin-top:1%;background-color: #e5e5e5;">
						<div class="tab-content" style="margin-top:1%;background-color: #e5e5e5;border-width: 0px;">
							<div class="by-form-row by-form-action">
								<a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
								<a class="btn btn-default blue" href="###" onclick="$('#queryForm1')[0].reset();queryForm();">清空</a>
							</div>
						</div>
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