<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>资源模板列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript">
	$(function(){
   		//定义一datagrid
   		var _divId = 'data_div';
   		//var _url = '${path}/orderPublishTemplate/list.action';
   		var _url = '${path}/resOrder/orderPublishTemplateList.action';
   		var _pk = 'id';
   		var _conditions = ['creatTime_StartTime','creatTime_EndTime','name','posttype'];
   		var _sortName = 'id';
   		//var _singleSelect = false;
   		var _checkbox =false;
   		var _sortOrder = 'desc';
   		var _check = true;
		var _colums = [{field:'name',title:'模板名称',width:fillsize(0.15),align:'center',sortable:true},
						{field:'status',title:'状态',width:fillsize(0.1),align:'center',formatter:$statusDesc},
						{field:'typeDesc',title:'资源类型',width:fillsize(0.2),align:'center'},
						{field:'creatTime',title:'创建时间',width:fillsize(0.2),align:'center'},
						{field:'opt1',title:'操作',width:fillsize(0.3),align:'center',formatter:$operate}];
   		
		accountHeight();
   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
	});
	/***操作列***/
	$operate = function(value,rec){
		var opt = "";
		opt += "<a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 详细</a>";
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
		$.openWindow("${path}/resRelease/orderPublishTemplate/detail.action?id="+id+"&opFrom=resTemp",'模板详细信息',800,500);
	}
	
	function select(id,type,name){
		var orderId = '${param.id}';
		var operateFrom = '${param.operateFrom}';
		$.ajax({
			url: '${path}/subject/subjectUpdSub.action?id='+orderId+'&templateId='+id+'&flag=1',
		    type: 'post',
		    datatype: 'text',
		    success: function (returnValue) {
		    	if(operateFrom=="TASK_LIST_PAGE"){
		    		top.index_frame.work_main.document.getElementById("templateId").value=id;
					top.index_frame.work_main.document.getElementById("templateType").value=type;
					top.index_frame.work_main.document.getElementById("templateName").value=name;
		    	}else{
		    		top.index_frame.work_main1.$("#templateId").val(id);
					top.index_frame.work_main1.$("#templateType").val(type);
					top.index_frame.work_main1.$("#templateName").val(name);
					top.index_frame.work_main1.resty();
		    	}
				
				$.closeFromInner();
		    }
		});
	}
	
	function query(){
		$grid.query();
	}
	
	function formClear(){
		$('#name').val('');
    	$('#creatTime_StartTime').val('');
    	$('#creatTime_EndTime').val('');
	}
	</script>
</head>
<body>
	<div id="fakeFrameQuery" class="container-fluid by-frame-wrap " style="height: auto;">
		<div style="float: left;">
			<form action="#" class="form-inline no-btm-form" role="form">
			<input type="hidden" name="posttype" id="posttype" value="${param.posttype }"/>
				<div class="form-group">
				      <input placeholder=" 模版名称" class="form-control input" id="name" qMatch="like" name="name" />
				</div>
			   <div class="form-group">
				      <input placeholder=" 开始时间" class="form-control Wdate" qMatch=">=" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'creatTime_EndTime\')}'})" id="creatTime_StartTime" name="creatTime_StartTime" />
				</div>
				<div class="form-group">
				      <input placeholder="结束时间" class="form-control Wdate" qMatch="<=" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'creatTime_StartTime\')}'})" id="creatTime_EndTime" name="creatTime_EndTime" />
				</div>
				<input type="button" value="查询" class="btn btn-primary" onclick="query();"/>
				<input type="button" value="清空" class="btn btn-primary" onclick="formClear();query();"/>
			</form>
		</div>
	</div>

	<div id="fakeFrame" class="container-fluid by-frame-wrap ">
		<div class="panel panel-default" style="height: 94%;">
			<div class="form-wrap">
				<div id="data_div" class="data_div height_remain" style="width: 735px;"></div>
			</div>
		</div>
	</div>
</body>
</html>