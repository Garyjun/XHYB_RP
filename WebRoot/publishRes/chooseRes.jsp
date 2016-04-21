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
	<script type="text/javascript" src="${path}/appframe/plugin/jxmenu/js/jquery-powerFloatEdit-min.js"></script>
	<script type="text/javascript"
	src="${path}/appframe/util/appDataGrid.js"></script>
	<script type="text/javascript">
	var dt = new DataGrid();
	var width = '${param.divWidth}';
	var objectIds = '${param.objectIds}';
	var dt = new DataGrid();
	$(function() {
		initializes();
		queryForm1();
	});
	function typeinit(){
		initializes();
		queryForm1();
	}
	function initializes(){
		$('#queryCondition').children().remove();
		xmenuNum="";
		var publishType= $('#publishType').val();
		directCreateQueryConditionAndRes(publishType);
		onLoadDict();   //加载数据字典项
		modifyCss();
		if(xmenuNum != "" && xmenuNum != null){
			xmenuNum = xmenuNum.substring(0,xmenuNum.length-1);
			var menuList = xmenuNum.split(",");
			for(var i=0;i<menuList.length;i++){
				var menu = menuList[i]+"()";
				eval(menu);
			}
		}
	}
	function queryForm1() {
		$('#data_div').width(width);
		var publishType= $('#publishType').val();
		dt.setConditions([]);
		dt.setPK('objectId');
		dt.setSortName('objectId');
		dt.setURL('${path}/search/queryFormList.action?publishType='+$("#publishType").val()+"&params=");
		dt.setSortOrder('desc');
		dt.setSelectBox('objectId');
		dt.setColums([ <app:QueryListColumnTag/>
		{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}]);
		accountHeight();
		dt.initDt();
		dt.query();
		//为查询按钮添加点击事件
		if($("#queryCondtionButton").length>0){
			$('#queryCondtionButton').on('click', function() {
				//获取页面上输入的查询条件组合,和在查询条件中输入的值
				//var params = getAllQueryConditions();
				var params = getAllQueryFieldConditions();
				params = encodeURI(params);
				queryForm(params);
				$('#params').val(params);
			});
		/* if($("#queryCondtionButton").length>0){
			$('#queryCondtionButton').on('click', function() {
				//var params = getAllQueryConditions();
				var params = getAllQueryFieldConditions();
				queryForm(params);
				$('#params').val(params);
			});
		} */
		}
	}
/* 	
	$(function() {
		$('#data_div').width(width);
		var publishType = $("#publishType").val();
		initializes();
		
		$("#queryFrame").selectpicker('refresh');

		dt.setConditions([]);
		dt.setPK('objectId');
		dt.setSortName('objectId');
		dt.setURL('${path}/search/queryFormList.action?publishType='+$("#publishType").val()+"&params=");
		dt.setSortOrder('desc');
		dt.setSelectBox('objectId');
		dt.setColums([ <app:QueryListColumnTag/>
		{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}]);
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
	}); */
	
	$operate = function(value,rec){
		var opt= "<sec:authorize url="/publishRes/detail.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
		return opt;
	};
	
	function detail(objectId){
		$.openWindow("${path}/publishRes/openDetail.action?objectId="+objectId+"&searchFlag=close"+"&flagSta=1",'资源详细页',1000,550);
	}
	
	function queryForm(params) {
		dt.dataGridObj.datagrid({
					url : "${path}/search/queryFormList.action?publishType="+$("#publishType").val()+"&params="+params
				}); 
	}
	
	function onLoadDict(){
		var publishType = $("#publishType").val();
		var flag = "";
		// 获得元数据定义字段,包括数据字典和数据字典包括的项
		var queryFields = creatFieldOpt(publishType, flag);
		var lineCount = 0;
		var newValues = "";
		var newCount = "";
		if(queryFields){
			var obj = JSON.parse(queryFields);
			$(obj).each(function(index) {
				++lineCount;
				var count = "";
				if(lineCount<10){
					count += "0" + lineCount;
				}
				var fieldValues = obj[index].fieldValues;
				var fieldType = obj[index].fieldType;
				if(fieldType == 3 || fieldType == 2 || fieldType == 4){
					newValues = newValues+",";
					newValues = newValues+fieldValues;
					newCount = newCount +",";
					newCount = newCount+count;
					//dictAddOpts(fieldValues,'search', null, "multiSelEle"+count);
				}
			});  
		}
		if(newValues.length>0 && newCount.length>0){
			var values = newValues.split(",");
			var counts = newCount.split(",");
			for(var i=0;i<values.length;i++){
				if(values[i]!="" && counts[i]!=""){
					//加载数据字典项，填充multiSelEle插件，原来所运用插件
					//dictAddOpts(values[i],'search', null, "multiSelEle"+counts[i]);
					//运用Xmenu插件
					dictQueryAddOpts(values[i],'search', null, "select_Value"+counts[i]);
				}
			}
		}
	}
		/***添加关联***/
		function add() {
			var codes = getChecked('data_div','objectId').join(',');
			if (codes == '') {
			    $.alert('请选择要添加的资源！');
			} else {
				if($('#reriveRes').val()=="1"){
					addReriveRes(codes);
				}else if($('#reriveRes').val()=="2"){
					addSourceRes(codes);
				}else{
					addRelations(codes);
				}
			}
		}
		function addRelations(ids){
			var resId =$('#objectIds').val();
			$.post('addRelationRes.action',{id:resId,relationIds:ids},function(data){
				if(data==""){
					top.index_frame.work_main.$grid.query();
					$.alert("添加成功");
					$.closeFromInner();
				}
			});
		}
		function addReriveRes(ids){
			var resId =$('#objectIds').val();
			$.post('addReriveRes.action',{id:resId,relationIds:ids},function(data){
				if(data==""){
					top.index_frame.work_main.$grid.query();
					$.alert("添加成功");
					$.closeFromInner();
				}
			});
		}
		function addSourceRes(ids){
			var resId =$('#objectIds').val();
			$.post('addSourceRes.action',{id:resId,relationIds:ids},function(data){
				if(data==""){
					top.index_frame.work_main.$grid.query();
					$.alert("添加成功");
					$.closeFromInner();
				}
			});
		}
// 		function getQueryCondition(){
// 			var publishType = $("#publishType").val();
// 			directCreateQueryCondition(publishType);
// 		};
			var winWidth = 0;
			var winHeight = 0; 
			function findDimensions(){  //函数：获取尺寸
				//获取窗口宽度 
				if (window.innerWidth){
					winWidth = window.innerWidth; 
				} else if ((document.body) && (document.body.clientWidth)){
					winWidth = document.body.clientWidth; 
				} 
				//获取窗口高度 
				if (window.innerHeight){ 
					winHeight = window.innerHeight; 
				} else if ((document.body) && (document.body.clientHeight)){ 
					winHeight = document.body.clientHeight; 
				}
				//通过深入Document内部对body进行检测，获取窗口大小 
				if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth){ 
					winHeight = document.documentElement.clientHeight; 
					winWidth = document.documentElement.clientWidth; 
				} 
				var valueWidth = winWidth.toString();
//		 		alert($('#data_div')+"   "+winWidth);
				$('#data_div').width(valueWidth);
			} 
			findDimensions(); 
			window.onresize=findDimensions;
	</script>
</head>
<body class="win-dialog">
	<div class="form-wrap">
			<div class="row">
				<div class="col-md-4">
					<label class="control-label col-md-2">资源类型：</label>
					<div class="col-md-10">
						<app:selectResType name="publishType" id="publishType" headName="" headValue="" 
							onchange="typeinit()" />
					</div>
				</div>
			</div>
			<div class="portlet">
				<div class="portlet-title" style="width: 100%">
					<div class="caption" style="margin-left: -15px">
						隐藏查询条件 <a href="javascript:;" onclick="togglePortlet(this)"><i
							class="fa fa-angle-up"></i></a>
					</div>
				</div>
				<form id="myForm" action="" target="work_main" method="post">
				<div class="portlet-body" id="queryFrame">
					<div class="container-fluid" id="queryCondition"></div>
				</div>
					</form>
			</div>
		<div class="panel-body height_remain">
			<div style="margin: 0px 10px 10px 0px;">
				<input type="hidden" id="objectId" name="objectId"
					value="${param.objectId}" />
				<textarea name="objectIds" style="display: none" id="objectIds"
					class="form-control">${param.objectIds}</textarea>
				<input type="hidden" id="publishType" name="publishType"
					value="${param.publishType}" /> <input type="hidden"
					id="reriveRes" name="reriveRes" value="${param.reriveRes}" /> <input
					type="hidden" id="queryType" name="queryType"
					value="${param.queryType}" /> <input type="button" value="选择"
					class="btn btn-primary" onclick="add()" /> &nbsp;
				<!-- 		<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>   -->
			</div>
			<div id="data_div" class="data_div height_remain"></div>
		</div>
	</div>
</body>
</html>