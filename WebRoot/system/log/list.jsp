<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript">
	     $(function(){
	    	 $('#operator').val(decodeURIComponent("${param.operator }"));
 	   		var _divId = 'data_div';
 	   		var _url = '${path}/log/list.action';
 	   		if($('#workLog').val()!=""){
 	   			_url = _url+"?workLog=1";
 	   		}
 	   		var _pk = 'id';
 	   		var _conditions = ['startDate','endDate','moduleId','optype','operator'];
 	   		var _sortName = 'id';
 	   		var _sortOrder = 'desc';
 	   		var _check = false;
 			var _colums = [ 
				{ title:'操作人', field:'operator' ,width:100, align:'center' },
			    { title:'功能模块', field:'sysOperateType.module.moduleName' ,width:100, align:'center' ,sortable:true},
			    { title:'登陆IP', field:'userIp' ,width:100, align:'center' },
			    { title:'操作时间', field:'operateTime' ,width:100, align:'center' },
			    { title:'操作说明', field:'operateDesc' ,width:100, align:'center' }
 			];
 			accountHeight();
 	   		$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
 		});
	     
	    function query() {
	    	$("#startDate").val($('#modifiedStartTime').val());
	    	$("#endDate").val($('#modifiedEndTime').val());
	 		$grid.query();
	 	}
	    function formReset(){
			$('#queryForm')[0].reset();
			query();
		}		
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
			<c:choose>
			  <c:when test="${workLog!='1'}">
			     <ol class="breadcrumb">
					<li>
					  <a href="javascript:;">系统管理</a>
					</li>
					<li class="active">系统日志</li>
					<li class="active">系统日志列表</li>
				 </ol>
			  </c:when>
			  <c:otherwise>
			    <ol class="breadcrumb">
					<li>
				      <a href="javascript:;">工作日志</a>
					</li>
					<li class="active">工作日志列表</li>
				</ol>
			  </c:otherwise>
			</c:choose>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="float: right;margin-bottom:10px;">
					<form action="#" class="form-inline no-btm-form" role="form" id="queryForm">
						<div class="form-group">
							开始时间:
							<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})" id="modifiedStartTime" name="modifiedStartTime" />
						</div>
						<div class="form-group">
							结束时间:
							<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})" id="modifiedEndTime" name="modifiedEndTime" />
						</div>
						<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
						<button type="button" class="btn btn-primary" onclick="formReset();">清空</button>
					</form>
					<input type="hidden" id="moduleId" name="moduleId" value="${param.moduleId }" />
					<input type="hidden" id="optype" name="optype" value="${param.optype }" />
					<input type="hidden" id="startDate" name="startDate" value="${param.startDate }" />
					<input type="hidden" id="endDate" name="endDate" value="${param.endDate }" />
					<input type="hidden" id="operator" name="operator" value="${param.operator }" />
					<input type="hidden" id="workLog" name="workLog" value="${param.workLog }" />
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
</body>
</html>