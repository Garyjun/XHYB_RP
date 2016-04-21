<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<script type="text/javascript">
	$(function() {
		initRalations();
	});
	function initRalations(){
		//定义一datagrid
   		var _divId = 'relation_data_div';
   		var _url = '${path}/bres/listRelationRes.action';
   		var _pk = 'objectId';
   		var _conditions = ['id'];
   		var _sortName = 'modified_time';
   		var _sortOrder = 'desc';
   		var _colums = [{field:'commonMetaData.commonMetaDatas.unitName',title:'内容单元',width:fillsize(0.2),sortable:true},
						{field:'commonMetaData.commonMetaDatas.title',title:'资源标题',width:fillsize(0.17),sortable:true},
						{field:'commonMetaData.commonMetaDatas.creator',title:'制作者',width:fillsize(0.07),sortable:true},
						{field:'commonMetaData.commonMetaDatas.keywords',title:'关键字',width:fillsize(0.1),sortable:true},
						{field:'commonMetaData.commonMetaDatas.modified_time',title:'更新时间',width:fillsize(0.18),sortable:true}];
   		
   		$grid2 = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false,true,true,"",false,false);
	}
</script>

<div class="portlet portlet-border">
  <div class="portlet-title">
      <div class="caption">相关资源</div>
  </div>
  <div class="panel-body" id="999">
		<div style="margin: 0px 10px 10px 0px;">
			<input type="hidden" id="id" name="id" value="${objectId}"/>
			 <c:if test="${empty execuId}">
				<input type="button" value="添加" class="btn btn-primary" onclick="chooseRes()"/>
			 </c:if> 
		</div>
		<div id="relation_data_div" class="data_div" style="width: 100%;"></div>
	</div>
</div>