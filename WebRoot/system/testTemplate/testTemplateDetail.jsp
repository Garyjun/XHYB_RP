<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>


<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	
	$(function(){
   		//定义一datagrid
   		var _divId = 'data_div1';
   		var _url = '${path}/system/testTemplate/testTemplateItemList.action?pid='+$("#testTemplateId").val();
   		var _pk = 'id';
   		var _conditions = ['pid'];
   		var _sortName = 'id';
   		var _sortOrder = 'desc';
		var _colums = [{field:'testType',title:'题型',width:fillsize(0.2),sortable:true},
						{field:'testTypeKey',title:'题型关键字',width:fillsize(0.2),sortable:true},
						{field:'opt1',title:'操作',width:fillsize(0.22),align:'center',formatter:$operate}];
   		
   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	});
	
	/***操作列***/
	$operate = function(value,rec){
		var opt = "";
		opt += "<a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 详细</a>";
		return opt;
				
	};
	function detail(id){
		$.openWindow("${path}/system/testTemplate/testTemplateItemDetail.action?id="+id,'查看',600,320);
	}
	</script>
</head>
<body>
	<div id="fakeFrameDict" class="container-fluid by-frame-wrap" style="width:100%;">
	<div class="panel panel-default">
		<div class="form-wrap">
		<form:form action="updAction.action" id="form" modelAttribute="frmtestTemplate" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-sm-5 control-label text-right"><span class="required">*</span>试题模板名称：</label>
				<div class="col-xs-3">
					<p class="form-control-static">${frmtestTemplate.name}</p>
				</div>
			</div>
		
			<div class="form-group">
				<label class="col-sm-5 control-label text-right">描述：</label>
				<div class="col-sm-3">
					<p class="form-control-static">${frmtestTemplate.description}</p>
				</div>
			</div>			
			<div class="form-group">
				<div class="col-xs-offset-5 col-xs-7">
	           		<form:hidden path="id" id="testTemplateId"/>
	           		<input type="hidden" name="token" value="${token}" />
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            </div>
			</div>
			<div id="data_div1" class="data_div height_remain col-sm-12" style="width:950px;"></div>
		</form:form>
		</div>
	</div>
  </div>
</body>
</html>