<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>基础资源管理</title>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		function queryForm(){
			$("#work_main").attr("src", $("#work_main").attr("src"));
		}
	</script>
</head>
<body>
	<form id="queryForm" action="${path}/system/role/roleList.jsp" target="work_main" method="post">
		<div class="by-main-page-body-side" id="byMainPageBodySide">
		    <div class="page-sidebar">
		        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
		            <i class="fa fa-angle-left"></i>
		        </div>
	        <div id="sideWrap">
            <div class="by-tool-box">
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">角色名：</label></div>
				    <div class="by-form-val">
				    	<input type="text" name="roleName" id="roleName" class="form-control" />
				    </div>
				</div>
                 <div class="by-form-row by-form-action">
                                    <input type="button" value="查询" class="btn btn-primary" onclick="queryForm()"/>
							       <input type="reset" value="清空" class="btn btn-primary"/>
                  </div>
             </div>
                          
			</div>
    	</div>
	</div>
	</form>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="${path}/system/role/roleList.jsp"></iframe>
	</div>
</body>
</html>