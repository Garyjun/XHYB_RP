<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>演示主页</title>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
	function search(){
		var name = $("#name").val();
		var search = $("#search");
		if(name.length>0){
			search.attr({"href":"wordList.jsp?name="+name,
						"target":"work_main"});
		}
	}
	</script>
</head>
<body>
	<form id="queryForm"  method="post">
		<div class="by-main-page-body-side" id="byMainPageBodySide">
		    <div class="page-sidebar">
		        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
		            <i class="fa fa-angle-left"></i>
		        </div>
		        <div id="sideWrap">
		        <div class="by-tool-box">
		        	<div class="by-form-row clearfix">
						<div class="by-form-title form-group"><label>敏感词：</label></div>	
						<div class="by-form-val form-group">
				    		<input type="text" id="name" class="form-control" />
				    	</div>
						<div class="by-form-title form-group"><labe>状态：</label></div>	
						<div class="by-form-val form-group">
							<select class="form-control" id="status">
								<option value="-1">全部</option>
								<option value="1">启用</option>
								<option value="0">禁用</option>
							</select>							
				    	</div>	
						<div class="by-form-title form-group"><label>等级：</label></div>	
						<div class="by-form-val form-group">
				    		<select class="form-control" id="level">
				    			<option value="-1">全部</option>
				    			<option value="1">一级</option>
								<option value="1">二级</option>
								<option value="0">三级</option>
							</select>
				    	</div>
				    	<div class="by-form-title form-group"><label></label></div>
				    	<div class="by-form-val form-group">
				    		<a href="" id="search" class="btn btn-primary" onclick="search();">查询</a>
				    		<button class="btn btn-primary">清空</button>
				    	</div>				    				    	
		            </div>
		        </div>
		        </div>
		    </div>
		</div>
	</form>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="wordList.jsp"></iframe>
	</div>
</body>
</html>