<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>列表</title>
<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
<script type="text/javascript">
	$(function() {
		var _divId = 'data_div';
   		var _url = '${path}/TaskAction/list.action';
   		var _pk = 'taskId';
   		var _conditions = [ 'taskName', 'processName', 'busiDesc', 'applyUser' ];
   		var _sortName = 'taskId';
   		var _sortOrder = 'desc';
		var _colums =[ 
		               {field : 'processNameDesc',title : '类型',width : fillsize(0.1),sortable : true,align : 'center'}, 
		               {field : 'taskName',title : '任务名称',width : fillsize(0.2),sortable : true,align : 'center'}, 
		               {field : 'processReturnName',title : '描述',width : fillsize(0.3),sortable : true,align : 'center'},
		               {field : 'userName',title : '提交人',width : fillsize(0.1),sortable : true,align : 'center'}, 
		               {field : 'createDate',title : '日期',width : fillsize(0.15),sortable : true,align : 'center'}, 
		               {field : 'opt1',title : '操作',width : fillsize(0.15),align : 'center',formatter : $operate} ];
		accountHeight();
		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false);
	});
	/***操作列***/
	$operate = function(value, rec) {
		var opt = "";
		var doUrl = "${path}" + rec.taskUrl + "&execuId=" + rec.execuId+ "&operateFrom=TASK_LIST_PAGE&wfTaskId=" + rec.taskId+"&goBackTask=1&taskFlag="+$('#taskFlag').val();
		var optName = '执行';
		var taskName = rec.taskName;
		if (taskName.indexOf("审") != -1) {
			optName = "审核";
		}
		if(taskName.indexOf("主") != -1){
			doUrl = doUrl+"&posttype=2";
		}
		if(taskName.indexOf("需") != -1){
			doUrl = doUrl+"&posttype=1";
		}
// 		if(optName =='执行'){
// 			alert(doUrl);
// 		}
		var doOpt = "<a class=\"btn hover-red\" href=\"javascript:doTask('"
				+ doUrl + "')\" >" + optName + "</a>";
		var viewImgOpt = "<a class=\"btn hover-red\" href=\"javascript:viewImage('"
				+ rec.execuId + "')\" >进度</a>";
		opt = opt + doOpt + viewImgOpt;
		return opt;

	};

	function queryForm() {
		$grid.query();
	}
	/***执行任务***/
	function doTask(doUrl) {
		window.location.href = doUrl;
		//$.openWindow(doUrl,'执行任务',1024,500);
	}
	/***查看流程图***/
	function viewImage(execuId) {
		$.openWindow("${path}/jbpm/processImage.jsp?execuId=" + execuId,'查看流程图', 870, 555);
	}
	function formReset(){
		$('#queryForm')[0].reset();
	//	$grid.clean();
		queryForm();
	}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
	<input type="hidden" id="taskFlag" name="taskFlag" value="${taskFlag}"/>
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">待办任务</a></li>
					<li class="active">待办列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				 <div style="float: right;margin-bottom:10px;">
						<form action="#" class="form-inline no-btm-form" role="form" id="queryForm">
							<div class="form-group">
								<label class="control-label" for="processName">类型:</label>
								<form:select path="command.processName" id="processName" class="form-control" qMatch="=">
									<form:option value="" label="全部" />
									<form:options items="${wfTypes}" />
								</form:select>
							</div>
							<div class="form-group">
								<input type="text" class="form-control" id="taskName" name="taskName" qMatch="like" placeholder="任务名称" />
							</div>
							<div class="form-group">
								<input type="text" class="form-control" id="busiDesc" name="busiDesc" qMatch="like" placeholder="描述" />
							</div>
							<div class="form-group">
								<input type="text" class="form-control" id="applyUser" name="applyUser" qMatch="like" placeholder="提交人" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="queryForm();" /> 
							<button type="button" class="btn btn-primary" onclick="formReset();">清空</button>
						</form>
				</div>

				<input type="hidden" id="taskName" name="taskName" qMatch="like" /> 
				<input type="hidden" id="status" name="status" qMatch="=" /> 
				<input type="hidden" id="processName" name="processName" qMatch="=" />

				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
</body>
</html>