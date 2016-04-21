<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    	<meta name="viewport" content="width=device-width, initial-scale=1"/>
		<title>加工单管理</title>
		<style type="text/css">
			html, body {height: 100%;}
		</style>
		<script type="text/javascript">
			function queryForm(){
				var url = '${path}/taskprocess/taskprocesslist.jsp';
				var taskName = $('#taskName').val();
				var createUser = $('#createUser').val();
				if(taskName!=null&&taskName!=undefined){
					taskName = encodeURI(taskName);
				}
				if(createUser!=null&&createUser!=undefined){
					createUser = encodeURI(createUser);
				}
				url += '?taskName=' + taskName + '&createUser=' + createUser;
				//alert(url);
				$('#work_main').attr('src',url);
				
			}
			
			function formClear() {
				$('#taskName').val('');
				$('#createUser').val('');
			}
		</script>
	</head>
	<body>
		<form id="queryForm" method="post">
			<div class="by-main-page-body-side" id="byMainPageBodySide">
			    <div class="page-sidebar">
			        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
			            <i class="fa fa-angle-left"></i>
			        </div>
		        <div id="sideWrap">
	            <div class="by-tool-box">
					<div class="by-form-row clearfix">
					    <div class="by-form-title"><label for="abc">名称：</label></div>
					    <div class="by-form-val">
					    	<input type="text" name="taskName" id="taskName" class="form-control" />
					    </div>
					</div>
					<div class="by-form-row clearfix">
					    <div class="by-form-title"><label for="abc">创建人：</label></div>
					    <div class="by-form-val">
					    	<input type="text" name="createUser" id="createUser" class="form-control" />
					    </div>
					</div>
	                 <div class="by-form-row by-form-action">
                           <input type="button" value="查询" class="btn btn-primary" onclick="queryForm()"/>
					       <input type="button" value="清空" class="btn btn-primary" onclick="formClear();queryForm();"/>
	                  </div>
	             </div>

				</div>
	    		</div>
			</div>
		</form>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="${path}/taskProcess/list.action"></iframe>
	</div>
	</body>
</html>