<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>选择列表</title>
	<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
	<script type="text/javascript" src="${path}/search/js/bootstrap-select.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css" />
	<script type="text/javascript" src="${path}/search/js/map.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/search/css/modify.css" />
	<style type="text/css">
	.by-panel-body {
	  background-color: #ffffff;
	  color: #000000;
	  font-size: 12px;
	  margin-left: 16px;
	}
	</style>
	<script type="text/javascript"
	src="${path}/appframe/util/appDataGrid.js"></script>
	<script type="text/javascript">
	var dt = new DataGrid();
	var width = '${param.divWidth}';
	var fieldName = '${param.fieldName}';
	var valueLength = '${param.valueLength}';
	var check = '${param.check}';
// 	var objectIds = '${param.objectIds}';
	var dt = new DataGrid();
	$(function() {
		$('#data_div').width(width);
		var publishType = $("#publishType").val();
		modifyCss();
		$("#queryFrame").selectpicker('refresh');

		dt.setConditions(["name","address"]);
		dt.setPK('id');
		dt.setSortName('id');
		dt.setURL('${path}/staff/query.action');
		dt.setSortOrder('desc');
		dt.setSelectBox('id');
		dt.setColums([
		{field:'name',title:'姓名',width:fillsize(0.27),align:'left'},
// 		{field:'age',title:'年龄',width:fillsize(0.27),align:'center'},
		{field:'sex',title:'性别',width:fillsize(0.27),align:'center'},
		{field:'adress',title:'地址',width:fillsize(0.27),align:'center'},
		{field:'telephone',title:'联系电话',width:fillsize(0.27),align:'center'},
// 		{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}
		]);
		accountHeight();
		dt.initDt();
		dt.query();
		
		if($("#queryCondtionButton").length>0){
			$('#queryCondtionButton').on('click', function() {
				var params = getAllQueryConditions();
				queryForm(params);
			});
		}
		$("select[type=multiSelEle]").multipleSelect({
			multiple: true,
			multipleWidth: 80,
			width: "90%",
			//onOpen: dyAddOpts,
			onClick: optCheckChangeListener
		});
		$("label[name=multiSelEleName]").css("margin-right", "-1px");
	});
	$operate = function(value,rec){
		var opt= "<sec:authorize url="/publishRes/detail.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
		return opt;
	};
	
	function detail(objectId){
		$.openWindow("${path}/publishRes/openDetail.action?objectId="+objectId+"&searchFlag=close"+"&flagSta=1",'资源详细页',1000,550);
	}
	
	function queryForm(params) {
		var name = $("#name").val();
		name = encodeURI(encodeURI(name));
		var address = $("#address").val();
		address = encodeURI(encodeURI(address));
		dt.dataGridObj.datagrid({
					url : "${path}/staff/query.action?name="+name+"&address="+address
				}); 
	}
	function query() {
		$('#name').val("");
		$('#address').val("");
		var name = "";
		var address = "";
		dt.dataGridObj.datagrid({
					url : "${path}/staff/query.action?name="+name+"&address="+address
				}); 
	}
	
	 layer.open({
		type: 1,
		title :'二次查询',
		area: ['500px', '300px'],
		btn: ['查询', '关闭'],
		fix: false,
		yes: function(index, layero){
			query();
			layer.closeAll();
		},
		scrollbar: false,
	});
// 	function getQueryCondition(){
// 		/* var publishType = $("#publishType").val();
// 		directCreateQueryCondition(publishType); */
//         var conditionSize = $("select[name=queryField]").size();
// 		var publishType = $("#publishType").val();
// 		//alert("查询条件个数： "+publishType + "   "+ flag);
// 		var flag = $("#flag").val();
// 		for(var i=1;i<=conditionSize;i++){
// 			var count = "";
// 			if(i<10){
// 				count = "0" + i;
// 			}
// 			//lineCount--;
// 			$("#condition"+count).remove();
// 		}
// 		lineCount = 0;
// 		directCreateQueryCondition(publishType,flag);
// 		modifyCss();
// 		//var params = getAllQueryConditions();
// 		var params = "";
// 		queryForm(params);
// 		dt.query();
// 		$('#queryCondtionButton').on('click', function() {
// 			var params = getAllQueryConditions();
// 			//var params = "";
// 			queryForm(params);
// 		});
// 		dictCounttimes++;
// 		$("select[type=multiSelEle]").multipleSelect({
// 			multiple: true,
// 			multipleWidth: 80,
// 			width: "90%",
// 			onClick: optCheckChangeListener,
// 			onOpen: dyAddOpts
// 		});
// 		$("label[name=multiSelEleName]").css("margin-right", "-1px");
// 		$("label[name=timeShowLabel]").css("margin-left", "30px");
// 		$("label[name=timeShowLabel]").css("margin-right", "-65px");
// 		$("select[type=multiSelEle]").multipleSelect("refresh");
// 		//$("select[name=multiSelEle]").css("display", "block");
// 	};
	
// 		var width = '${param.divWidth}';
// 		$(function(){
// 			//定义一datagrid
// 			$('#data_div').width(width);
// 			directCreateQueryCondition();
// 	   		var _divId = 'data_div';
// 	   		var _url = '${path}/publishRes/query.action';
// 	   		var _pk = 'objectId';
// 	   		var _conditions = [<app:QueryConditionTag   />,'objectIds','publishType','queryType'];
// 	   		var _sortName = 'status';
// 	   		var _sortOrder = 'desc';
// 			var _colums = [<app:QueryListColumnTag   />
// 							{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate2,align:'center'}];
// 	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
// 	   		if($("#queryCondtionButton").length>0){
// 				$('#queryCondtionButton').on('click', function() {
// 					var params = getAllQueryConditions();
// 					queryForm(params);//点击查询调用的方法
// 				});
// 			}
// 		});
		function choice() {
			var ids = getChecked('data_div','id').join(',');
			if (ids == '') {
				if(check == '2'){
					layer.alert('请选择要添加的人员名称！');
				}else{ 
					$.alert('请选择要添加的人员名称！');
				}
			} else {
				addRelations(ids);
			}
		}
		function addRelations(ids){
// 			var resId =$('#objectIds').val();
			var arr = ids.split(",");
			if(valueLength!=""){
				if(arr.length>valueLength){
					if(check == '2'){
						layer.alert("所选人员数量必须小于等于"+valueLength+"个");
					}else{
						$.alert("所选人员数量必须小于等于"+valueLength+"个");
					}
					return;
				}
			}
			$.post('${path}/staff/searchName.action',{ids:ids},function(data){
				if(data!=""){
					if(check == '1'){
						top.index_frame.$("#"+fieldName+"Name").val(data);
						top.index_frame.$("#"+fieldName+"").val(ids);
					}else if(check == '2'){
						parent.$("#"+fieldName+"Name").val(data);
						parent.$("#"+fieldName+"").val(ids);
						var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
						parent.layer.close(index);
					}else{
						top.index_frame.work_main.document.getElementById(fieldName+"Name").value = data;
						top.index_frame.work_main.document.getElementById(fieldName).value = ids;
					}
					//$.alert("添加成功");
					$.closeFromInner();
				}
			});
		}
		 /***添加***/
		function edit(id){
	    	 if(id>-1){
// 	    		 $.openWindow("${path}/staff/toEdit.action?id="+id,'修改人员信息',800,600);
	    		 var d = art.dialog.open("${path}/staff/toEdit.action?id="+id,
	    			     {
	    				   lock:true,
	    			       title: "修改人员信息",
	    			       width: "800px",
	    			       height: "600px",
	    			       close: function () {
	    			           query();
	    			           //window.location.reload(true);
	    			       }
	    			   });
	    	 }else{
// 	    		 $.openWindow("${path}/staff/toEdit.action?id="+id+"&fromPeopleUnit=1",'添加人员信息',800,600);
	    		 var d = art.dialog.open("${path}/staff/toEdit.action?id="+id+"&fromPeopleUnit=1",
	    			     {
	    				   lock:true,
	    			       title: "添加人员信息",
	    			       width: "800px",
	    			       height: "600px",
	    			       close: function () {
	    			           query();
	    			           //window.location.reload(true);
	    			       }
	    			   });
	    	 }
			
		}
// 		function peopleUnit(id){
// 			var d = art.dialog.open("${path}/staff/toEdit.action?id="+id,
// 	     {
// 		   lock:true,
// 	       title: "导入数据字典项",
// 	       width: "450px",
// 	       height: "200px",
// 	       close: function () {
// 	           query();
// 	           //window.location.reload(true);
// 	       }
// 	   });
// 			d.showModal();
// 			//$.openWindow("system/dataManagement/dataDict/importDir.jsp?pid="+$("#dictNameId").val()+"&iframeName="+iframeName, '导入数据字典项',450,200);
// 		}
// 		function addReriveRes(ids){
// 			var resId =$('#objectIds').val();
// 			$.post('addReriveRes.action',{id:resId,relationIds:ids},function(data){
// 				if(data==""){
// 					top.index_frame.work_main.$grid.query();
// 					$.alert("添加成功");
// 					$.closeFromInner();
// 				}
// 			});
// 		}
// 		function addSourceRes(ids){
// 			var resId =$('#objectIds').val();
// 			$.post('addSourceRes.action',{id:resId,relationIds:ids},function(data){
// 				if(data==""){
// 					top.index_frame.work_main.$grid.query();
// 					$.alert("添加成功");
// 					$.closeFromInner();
// 				}
// 			});
// 		}
// 		function getQueryCondition(){
// 			var publishType = $("#publishType").val();
// 			directCreateQueryCondition(publishType);
// 		};
	</script>
</head>
<body class="win-dialog" style="overflow-x:auto">
	<div class="form-wrap">
			<div class="portlet">
				<form id="myForm" action="" target="work_main" method="post">
				<div class="portlet-body" id="queryFrame">
					<div class="container-fluid" id="queryCondition"></div>
				</div>
					</form>
			</div>
		<div class="panel-body height_remain form-inline" id="999">
			<div class="form-group">
					  <label for="userName" class="control-label" >姓名：</label>
				      <input type="text" class="form-control" id="name" name="name" qMatch="like"  placeholder="输入姓名" />
			</div>
			<div class="form-group">
					  <label for="userName" class="control-label" >地址：</label>
				      <input type="text" class="form-control" id="address" name="address"  qMatch="like" placeholder="输入地址" />
			</div>
			<div class="form-group">
				  <input type="button" value="查询" class="btn btn-primary" onclick="queryForm('')"/>
			</div>
			<div class="form-group">
<!-- 				<div style="margin: 0px 10px 10px 0px;"> -->
						 <input type="button" value="清空"
						class="btn btn-primary" onclick="query('')" />
			</div>
			<c:if test="${param.check !='2'}">
			<div class="form-group">
					<input type="button" value="添加" class="btn btn-primary blue" onclick="edit(-1);" />
			</div>
			</c:if>
			<div class="form-group">
					<input type="button" value="选择" class="btn btn-primary red" onclick="choice();" />
			</div>
		</div>
			<div id="data_div" class="data_div height_remain"></div>
	</div>
</body>
</html>