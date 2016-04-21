<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">
	$(function() {
		init_grid();
	});
	
	function init_grid(){
   		var _divId = 'operate_history_div';
   		var _url = '${path}/operateHistory/list.action';
   		var _pk = 'beanId';
   		var _conditions = ['beanId'];
   		var _sortName = 'operateTime';
   		var _sortOrder = 'desc';
   		var _colums = [
           	{field:'operateTime',title:'时间',width:fillsize(0.1),sortable:true},
			{field:'beanStatus',title:'状态描述',width:fillsize(0.1),sortable:true},
			{field:'operator.userName',title:'操作者',width:fillsize(0.1),sortable:true},
			{field:'operateDesc',title:'操作信息',width:fillsize(0.1)},
			{field:'remark',title:'备注',width:fillsize(0.18)}
		];
   		$grid_operate_history = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false,true,true);
	}
	
	
	function goBack(){
		if('${operateFrom}'=='TASK_LIST_PAGE'){
			window.location.href = "${path}/TaskAction/toList.action"; 
		}else{
			parent.queryForm();
		}
		
	}
</script>

	<div class="form-actions" style="text-align:center; ">
	   	<input type="hidden" id="beanId" name="beanId" value="${execuId}" qMatch="="/>
	</div>
	<div class="portlet portlet-border">
	  <div class="portlet-title">
	      <div class="caption">操作历史</div>
	  </div>
	  <div class="panel-body" id="999" style="width:100%;">
		   <div id="operate_history_div" class="data_div height_remain" ></div>
	  </div>
	</div>