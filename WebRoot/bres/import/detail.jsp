<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>详细</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
		$(function(){
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = 'detailList.action';
	   		var _pk = 'id';
	   		var _conditions = ['taskId'];
	   		var _sortName = 'id';
	   		var _sortOrder = 'asc';
			var _colums = [{field:'remark',title:'备注',width:fillsize(0.6),sortable:true},
							{field:'createTime',title:'导入时间',width:fillsize(0.25),sortable:true}];
	   		
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false,true,false);
		});
	</script>
</head>
<body>
<div class="form-wrap">
	<div class="form-horizontal">
		<div class="form-group">
			<label class="col-sm-3 control-label text-right">名称：</label>
			<div class="col-sm-9">
				${task.name}
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-3 control-label text-right">批次号：</label>
			<div class="col-sm-9">
				${task.batchNum}
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-12">
				<input type="hidden" id="taskId" name="task.id" value="${task.id }" qType="long" />
				<div id="data_div" class="data_div height_remain" style="width: 780px;"></div>
			</div>
		</div>
		
		<div class="form-group">
			<div class="col-sm-offset-3 col-sm-9">
               	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
            </div>
        </div>
	</div>
</div>
</body>
</html>