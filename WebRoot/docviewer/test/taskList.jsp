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
		
		// 表格数据源  
	    var data = [];  
	    // 用代码造30条数据  
	    for (var i = 1; i < 31; ++i) {  
	        data.push({  
	        	"taskId":i,  
	            "processNameDesc":i,  
	            "taskName":"taskName" + i,
	            "desc":"Student1" + i, 
	            "applyUser":"applyUser" + i,
	            "createDate":"createDate" + i,
	            "opt1":"Student1" + i
	        })  
	    }; 
	    
	    var obj = [
                 {taskId:'1',processNameDesc:'1',taskName:'一',desc:'111',applyUser:'2222',createDate:'222'},
                 {taskId:'2',processNameDesc:'2',taskName:'一',desc:'111',applyUser:'2222',createDate:'222'},
                 {taskId:'3',processNameDesc:'3',taskName:'一',desc:'111',applyUser:'2222',createDate:'222'},
                 {taskId:'4',processNameDesc:'4',taskName:'一',desc:'111',applyUser:'2222',createDate:'222'},
                 {taskId:'5',processNameDesc:'5',taskName:'一',desc:'111',applyUser:'2222',createDate:'222'},
                 {taskId:'6',processNameDesc:'6',taskName:'一',desc:'111',applyUser:'2222',createDate:'222'},
                 {taskId:'7',processNameDesc:'7',taskName:'一',desc:'111',applyUser:'2222',createDate:'222'},
                 {taskId:'8',processNameDesc:'8',taskName:'一',desc:'111',applyUser:'2222',createDate:'222'},
                 {taskId:'9',processNameDesc:'9',taskName:'一',desc:'111',applyUser:'2222',createDate:'222'},
                 {taskId:'0',processNameDesc:'10',taskName:'一',desc:'111',applyUser:'2222',createDate:'222'},
                 {taskId:'11',processNameDesc:'11',taskName:'一',desc:'111',applyUser:'2222',createDate:'222'},
                 {taskId:'12',processNameDesc:'12',taskName:'一',desc:'222',applyUser:'2222',createDate:'222'}
		];  
	    
	    var _divId = "data_div";
   		var _pk = 'taskId';
		var _colums =[ {field : 'processNameDesc',title : '类型',width : fillsize(0.1),sortable : false,align : 'center'}, 
		               {field : 'taskName',title : '任务名称',width : fillsize(0.2),sortable : false,align : 'center'}, 
		               {field : 'desc',title : '描述',width : fillsize(0.3),sortable : false,align : 'center'}, 
		               {field : 'applyUser',title : '提交人',width : fillsize(0.1),sortable : false,align : 'center'}, 
		               {field : 'createDate',title : '日期',width : fillsize(0.15),sortable : false,align : 'center'}, 
		               {field : 'opt1',title : '操作',width : fillsize(0.15),align : 'center',formatter : $operate} ];
		$grid = $.datagridJsObject(_divId,obj,_pk,_colums,null,null,true);
	});
	/***操作列***/
	$operate = function(value, rec) {
		var opt = "";
		var doUrl = "${path}" + rec.taskUrl + "&execuId=" + rec.execuId+ "&operateFrom=TASK_LIST_PAGE&wfTaskId=" + rec.taskId;
		var optName = '执行';
		var taskName = rec.taskName;
		if (taskName.indexOf("审") != -1) {
			optName = "审核";
		}
		var doOpt = "<a class=\"btn hover-red\" href=\"javascript:doTask('"
				+ doUrl + "')\" >" + optName + "</a>";
		var viewImgOpt = "<a class=\"btn hover-red\" href=\"javascript:viewImage('"
				+ rec.execuId + "')\" >进度</a>";
		opt = opt + doOpt + viewImgOpt;
		return opt;

	};

	function query() {
		$grid.query();
	}
	/***执行任务***/
	function doTask(doUrl) {
		window.location.href = doUrl
		//$.openWindow(doUrl,'执行任务',1024,500);
	}
	/***查看流程图***/
	function viewImage(execuId) {
		$.openWindow("${path}/jbpm/processImage.jsp?execuId=" + execuId,'查看流程图', 800, 500);
	}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">待办任务</a></li>
					<li class="active">待办列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				 <div style="float: right;margin-bottom:10px;">
						<form action="#" class="form-inline no-btm-form" role="form">
							<div class="form-group">
								<label class="control-label" for="processName">类型:</label>
								<form:select path="command.processName" id="processName" class="form-control" qMatch="=">
									<form:option value="" label="全部" />
									<form:options items="${wfTypes}" />
								</form:select>
							</div>
							<div class="form-group">
								<input type="text" class="form-control" id="taskName" name="taskName" qMatch="like" placeholder="输入任务名称" />
							</div>
							<div class="form-group">
								<input type="text" class="form-control" id="busiDesc" name="busiDesc" qMatch="like" placeholder="任务描述" />
							</div>
							<div class="form-group">
								<input type="text" class="form-control" id="applyUser" name="applyUser" qMatch="like" placeholder="提交人" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()" /> 
							<input type="reset" value="清除" class="btn btn-primary" />
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