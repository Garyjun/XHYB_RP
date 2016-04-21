<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>资源模板列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	$(function(){
   		//定义一datagrid
   		var _divId = 'data_div';
   		//var _url = '${path}/orderPublishTemplate/list.action';
   		var _url = '${path}/collectRes/orderTemplateList.action';
   		var _pk = 'id';
   		var _conditions = ['name'];
   		var _sortName = 'id';
   		var _singleSelect = false;
   		var _sortOrder = 'desc';
		var _colums = [{field:'name',title:'模板名称',width:fillsize(0.15),sortable:true},
						{field:'status',title:'状态',width:fillsize(0.1),formatter:$statusDesc},
						{field:'createTime',title:'创建时间',width:fillsize(0.2)},
						{field:'remark',title:'备注',width:fillsize(0.25)},
						{field:'opt1',title:'操作',width:fillsize(0.22),align:'center',formatter:$operate}];
   		
		accountHeight();
   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder, _singleSelect);
	});
	/***操作列***/
	$operate = function(value,rec){
		var opt = "";
		opt += "<a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 查看</a>";
		//opt += "<a class=\"btn hover-red\" href=\"javascript:select("+rec.id+','+"'"+rec.name+"')\" ><i class=\"fa fa-edit\"></i> 选择</a>";
		opt += "<a class=\"btn hover-red\" href=\"javascript:select("+rec.id+','+"'"+rec.type+"'"+','+"'"+rec.name+"')\" ><i class=\"fa fa-edit\"></i> 选择</a>";
		return opt;
	};
	
	$statusDesc = function(value,rec){
		if(rec.status==1)
			return "可用";
		else
			return "禁用";
	}
	
	function detail(id){
		location.href = "${path}/resRelease/select.action?id="+id+"&opFrom=edit";
	}
	
	function select(id,type,name){
		var orderId = '${param.orderId}';
		$.ajax({
			url: '${path}/resOrder/update.action?orderId='+orderId+'&templateId='+id+'&flag=1',
		    type: 'post',
		    datatype: 'text',
		    success: function (returnValue) {
		    	top.index_frame.work_main1.document.getElementById("templateId").value=id;
				top.index_frame.work_main1.document.getElementById("templateType").value=type;
				top.index_frame.work_main1.document.getElementById("templateName").value=name;
				
				$.closeFromInner();
// 		    	query();
		    }
		});
		
		
		
		//location.href = "${path}/resRelease/select.action?id="+id;
		//$.a
		//$.closeFromInner();
// 		$('#form1').ajax({
// 				url : '${path}/resOrder/toEdit.action?id='+id,
// 				method: 'post',//方式
// 				success:(function(response){
// 					alert();
// 					$.closeFromInner();
//        		})
// 			});
	}
	
	function query(){
		$grid.query();
	}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap ">
		<div class="panel panel-default" style="height: 94%;">
<!-- 			<div class="panel-heading" id="div_head_t"> -->
<!-- 				<ol class="breadcrumb"> -->
<!-- 					<li><a href="javascript:;">资源发布</a></li> -->
<!-- 					<li class="active">资源发布模板管理</li> -->
<!-- 				</ol> -->
<!-- 			</div> -->
			<div class="form-wrap">
<!-- 				<div class="form-group"> -->
<!-- 					<input class="btn btn-primary" type="button" value="选择资源模板" onclick="javascript:add();"/> -->
<!-- 				</div> -->
				<div id="data_div" class="data_div height_remain" style="width: 735px;"></div>
			</div>
		</div>
	</div>
</body>
</html>