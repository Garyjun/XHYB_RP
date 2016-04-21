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
			{field:'operateDesc',title:'操作信息',width:fillsize(0.1),sortable:true},
			{field:'remark',title:'备注',width:fillsize(0.18),sortable:true}
		];
   		$grid_operate_history = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false,true,true,"",false,false);
	}
	
	
	function doCheck(decision){
		//区分教育、出版
		var module_url = "${path}/res/wf/";
		var wfType  = $("#beanId").val().split(".")[0];
		if(wfType=='pubresCheck'){
			module_url = "${path}/pubres/wf/";
		}
		var request_url = module_url+'doCheck.action?objectId=${objectId}'
				+'&wfTaskId=${wfTaskId}&decision='+decision
				+'&libType=${libType}'
				+'&publishType=${publishType}'
				+'&operateFrom=${operateFrom}'
				+'&type='+$('#type').val()+'&queryType='+$('#queryType').val()+'&module='+$('#module').val()+'&goBackTask='+$('#goBackTask').val();
		
		if(decision=='reject' && $("#checkOpinion").val()==''){
			$.alert('审核意见不能为空');
			return;
		}
		var user = {checkOpinion:$('#checkOpinion').val()};
		var aMenu  = encodeURI(JSON.stringify(user));
		$.confirm('确定审核'+(decision=='approve'?'通过':'驳回')+'吗？', function(){
			$.ajax({
				url: encodeURI(request_url),
			    type: 'post',
			    data:"checkOpinion=" + aMenu,
			    datatype: 'text',
			    contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
			    success: function (destination) {
			    	window.location.href = '${path}'+destination;
			    }
			});
		});
	}
	
	function goBackTaskList(){
		if('${goBackTask}'==''){
			parent.queryForm();
		}else{
			window.location.href = "${path}/TaskAction/toList.action"; 
		}
		
	}
</script>

	<div class="form-actions" style="text-align:center; ">
	   	<input type="hidden" id="beanId" name="beanId" value="${execuId}" qMatch="="/>
	 	<c:if test="${not empty wfTaskId}">
	   	  <div class="row">
	          <div class="col-md-12">
	              <div class="form-group">
	                  <label class="control-label col-md-2">审核意见：</label>
	                  <div class="col-md-10">
	                  		<textarea id="checkOpinion" name="checkOpinion" class="form-control" rows="5"></textarea>
	                  </div>
	              </div>
	          </div>
	      </div>
	   	 <button type="button" class="btn btn-lg red" onclick="doCheck('approve');">通过</button>
	     <button type="button" class="btn btn-lg red" onclick="doCheck('reject');">驳回</button>
	     <button type="button" id="flagButton" class="btn btn-lg red" onclick="upd('${objectId}');">修改</button>
	     <button type="button" class="btn btn-lg blue" onclick="goBackTaskList();">返回</button>
	   </c:if>
	  
	</div>

	<div class="portlet portlet-border">
	  <div class="portlet-title">
	      <div class="caption">操作历史</div>
	  </div>
	  <div class="panel-body" id="999">
		   <div id="operate_history_div" class="data_div height_remain"></div>
	  </div>
	</div>