<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>发布模板列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript">
	$(function(){
		refsh();
   		//定义一datagrid
   		var _divId = 'data_div';
   		// PublishTempAction.java list方法
   		var _url = '${path}/resRelease/orderPublishTemplate/list.action';
   		var _pk = 'id';
   		var _conditions = ['posttype','publishType','name','status','startDate','endDate','createUser'];
   		var _sortName = 'id';
   		var _sortOrder = 'desc';
   		var _check=true;
		var _colums = [{field:'status',title:'状态',width:fillsize(0.1),align:'center',sortable:true,formatter:$statusDesc},
		               {field:'name',title:'模板名称',width:fillsize(0.15),align:'center',sortable:true},
						{field:'typeDesc',title:'资源类型',align:'center',width:fillsize(0.1)},
						{field:'posttypeDesc',title:'发布途径',align:'center',width:fillsize(0.1)},
						{field:'publishType',title:'发布方式',align:'center',width:fillsize(0.1),formatter:$typeDesc},
						{field:'createUser.userName',title:'创建者',align:'center',width:fillsize(0.1)},
						{field:'creatTime',title:'创建时间',width:fillsize(0.15),sortable:true,align:'center'},
						{field:'opt1',title:'操作',width:fillsize(0.3),align:'center',formatter:$operate}];
   		
		accountHeight();
   		$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
   		
	});
	/***操作列***/
	$operate = function(value,rec){
		/* var opt = "";
		opt += "<sec:authorize url='/orderPublishTemplate/detail.action'><a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 查看</a></sec:authorize>";
		opt += "<sec:authorize url='/orderPublishTemplate/add/edit.action'><a class=\"btn hover-red\" href=\"javascript:upd("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 修改</a></sec:authorize>";
		opt += "<sec:authorize url='/orderPublishTemplate/delete.action'><a class=\"btn hover-blue\" href=\"javascript:deleteRecord("+rec.id+")\" ><i class=\"fa fa-trash-o\"></i> 删除</a></sec:authorize>";
		
		opt += "<sec:authorize url='/orderPublishTemplate/disable.action'><a class=\"btn hover-red\" href=\"javascript:disabled("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 禁用</a></sec:authorize>";
		opt += "<sec:authorize url='/orderPublishTemplate/able.action'><a class=\"btn hover-red\" href=\"javascript:enabled("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 启用</a></sec:authorize>"; 
		return opt;*/
		var optArray = new Array();
		optArray.push("<sec:authorize url='/orderPublishTemplate/detail.action'><a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-sign-out\"></i>详细</a></sec:authorize>");
		optArray.push("<sec:authorize url='/orderPublishTemplate/add/edit.action'><a class=\"btn hover-red\" href=\"javascript:upd("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 修改</a></sec:authorize>");
		optArray.push("<sec:authorize url='/orderPublishTemplate/delete.action'><a class=\"btn hover-blue\" href=\"javascript:deleteRecord("+rec.id+")\" ><i class=\"fa fa-trash-o\"></i> 删除</a></sec:authorize>");
		if(rec.status==1){
			optArray.push("<sec:authorize url='/orderPublishTemplate/disable.action'><a class=\"btn hover-red\" href=\"javascript:disabled("+rec.id+")\" ><i class=\"fa fa-lock\"></i> 禁用</a></sec:authorize>");
		}else{
			optArray.push("<sec:authorize url='/orderPublishTemplate/able.action'><a class=\"btn hover-red\" href=\"javascript:enabled("+rec.id+")\" ><i class=\"fa fa-unlock\"></i> 启用</a></sec:authorize>");
		}
		return createOpt(optArray);
	};
	
	$statusDesc = function(value,rec){
		if(rec.status==1)
			return "启用";
		else
			return "禁用";
	}
	$typeDesc = function(value,rec){
		if(rec.publishType=='offLine')
			return "线下发布";
		else
			return "线上发布";
	}
	
	/***添加***/
	 function add(){
		var posttype=$('#posttype').val();
		location.href = "${path}/resRelease/orderPublishTemplate/add/edit.action?posttype="+posttype;
	} 
	
	function refsh(){
		var name = $("#name", window.parent.document);
	 	var startDate = $("#modifiedStartTime", window.parent.document);
		var endDate = $("#modifiedEndTime", window.parent.document);
		var status = $("#status", window.parent.document);
		var createUser = $("#createUser", window.parent.document);
		var publishType = $("#publishType", window.parent.document);
		var posttype = $("#posttype", window.parent.document);
		 $('#name').val(name.val());
		 $('#startDate').val(startDate.val());
		 $('#endDate').val(endDate.val()); 
		 $('#status').val(status.val())
		 $('#createUser').val(createUser.val());
		 $('#publishType').val(publishType.val());
		 $('#posttype').val(posttype.val());
	}
	
	/***修改***/
	 function upd(id){
		var posttype=$('#posttype').val();
		location.href = "${path}/resRelease/orderPublishTemplate/update/edit.action?id="+id +"&posttype="+posttype;
	} 
	
	function detail(id){
		location.href = "${path}/resRelease/orderPublishTemplate/detail.action?id="+id;
	}
	
	/***删除***/
	function del() {
		var codes = getChecked('data_div','id').join(',');
		if (codes == '') {
		    $.alert('请选择要删除的数据！');
		} else {
			deleteRecord(codes);
		};
	}
	function deleteRecord(ids){
		$.confirm('确定要删除所选数据吗？', function(){
			$.post('${path}/resRelease/orderPublishTemplate/delete.action?ids=' + ids,function(data){
				if(data=="ok")
					//$grid.query();
					location.href = '${path}/resRelease/orderPublishTemplate/orderTemplateList.jsp';
				else
					$.alert(data);
					location.href = '${path}/resRelease/orderPublishTemplate/orderTemplateList.jsp';
			});
		});
	}
	
	//禁用
	function disabled(ids){
		$.confirm('确定要禁用所选数据吗？', function(){
			$.post('${path}/resRelease/orderPublishTemplate/batchDisabled.action?ids=' + ids,function(data){
				if(data=="ok")
					$grid.query();
				else
					$.alert(data);
				$('#data_div').datagrid('uncheckAll');
			});
		});
	}
	
	//批量禁用
	function batchDisabled(){
		var status = getChecked('data_div','status').join(',');
		if(status.indexOf('0') != -1){
			$.alert('选中模板中包含已启用模板，请重新选择！');
		    return;
		}
		var ids = getChecked('data_div','id').join(',');
		if (ids == '') {
		    $.alert('请选择要禁用的数据！');
		    return;
		} 
		$.confirm('确定要禁用所选数据吗？', function(){
			$.post('${path}/resRelease/orderPublishTemplate/batchDisabled.action?ids=' + ids,function(data){
				if(data=="ok")
					$grid.query();
				else
					$.alert(data);
				$('#data_div').datagrid('uncheckAll');
			});
		});
	}
	
	//启用
	function enabled(ids){
		$.confirm('确定要启用所选数据吗？', function(){
			$.post('${path}/resRelease/orderPublishTemplate/batchEnabled.action?ids=' + ids,function(data){
				if(data=="ok")
					$grid.query();
				else
					$.alert(data);
				$('#data_div').datagrid('uncheckAll');
			});
		});
	}
	
	//批量启用
	function batchEnabled(){
		var status = getChecked('data_div','status').join(',');
		if(status.indexOf('1') != -1){
			$.alert('选中模板中包含已启用模板，请重新选择！');
		    return;
		}
		
		var ids = getChecked('data_div','id').join(',');
		if (ids == '') {
		    $.alert('请选择要启用的数据！');
		    return;
		}
		$.confirm('确定要启用所选数据吗？', function(){
			$.post('${path}/resRelease/orderPublishTemplate/batchEnabled.action?ids=' + ids,function(data){
				if(data=="ok")
					$grid.query();
				else
					$.alert(data);
				$('#data_div').datagrid('uncheckAll');
			});
		});
	}
	
	function formClear(){
    	$('#modifiedStartTime').val('');
    	$('#modifiedEndTime').val('');
	}
	function query(){
		$("#startDate").val($('#modifiedStartTime').val());
    	$("#endDate").val($('#modifiedEndTime').val());
		$grid.query();
	}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><span>资源发布</span></li>
					<li class="active">发布模板</li>
					<li class="active">发布模板列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				     <sec:authorize url="/orderPublishTemplate/add/edit.action">
						<input class="btn btn-primary" type="button" value="添加" onclick="javascript:add();"/>
					</sec:authorize>
					<sec:authorize url="/orderPublishTemplate/delete.action">
						<input class="btn btn-primary" type="button" value="批量删除" onclick="del();"/>
					</sec:authorize>
					<sec:authorize url="/orderPublishTemplate/disable.action">
						<input class="btn btn-primary" type="button" value="批量禁用" onclick="batchDisabled();"/>
					</sec:authorize>	
					<sec:authorize url="/orderPublishTemplate/able.action">
						<input class="btn btn-primary" type="button" value="批量启用" onclick="batchEnabled();"/>
					</sec:authorize>
					<div style="float: right;">
						<!-- <form action="#" class="form-inline no-btm-form" role="form">
						   <div class="form-group">
							      <input placeholder=" 开始时间" class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})" id="modifiedStartTime" name="modifiedStartTime" />
							</div>
							<div class="form-group">
							      <input placeholder="结束时间" class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})" id="modifiedEndTime" name="modifiedEndTime" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="button" value="清空" class="btn btn-primary" onclick="formClear();query();"/>
						</form> -->
						<input type="hidden" id="startDate" name="startDate" value="${param.startDate }" />
						<input type="hidden" id="endDate" name="endDate" value="${param.endDate }" />
						<input type="hidden" id="status" name="status" value="${param.status }" />
					  	<input type="hidden" id="name" name="name" value="${param.name }" />
						<input type="hidden" id="createUser" name="createUser" value="${param.createUser }" />
						<input type="hidden" id="publishType" name="publishType" value="${param.publishType }" />
						<input type="hidden" id="posttype" name="posttype" value="${param.posttype }" />
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>				
		</div>
	</div>
</body>
</html>