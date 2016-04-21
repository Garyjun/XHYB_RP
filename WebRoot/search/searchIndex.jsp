<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
<title>高级查询</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
<script type="text/javascript"
	src="${path}/search/js/bootstrap-select.js"></script>
<link rel="stylesheet" type="text/css"
	href="${path}/search/css/bootstrap-select.css" />
<link rel="stylesheet" type="text/css"
	href="${path}/resRelease/css/multiple-select.css" />
<script type="text/javascript"
	src="${path}/resRelease/js/dictSelect.js"></script>
	<script type="text/javascript"
	src="${path}/resRelease/js/jquery.multiple.select.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
<script type="text/javascript" src="${path}/search/js/map.js"></script>
<script type="text/javascript"
	src="${path}/appframe/util/appDataGrid.js"></script>
	<link rel="stylesheet" type="text/css"
	href="${path}/search/css/modify.css" />
<script type="text/javascript" src="${path}/appframe/plugin/jxmenu/js/jquery-powerFloatEdit-min.js"></script>
<style type="">
	.bootstrap-select:not([class*="span"]):not([class*="col-"]):not([class*="form-control"]) {
    width: 100px;
    margin-left: 30px;
}
	
</style>
<script type="text/javascript">
	var dt = "";
	$(function() {
		initDatagrid();
	});
	
	function initDatagrid(){
		var publishType = $("#publishType").val();
		var queryColumn = "";
		 $.ajax({
			url: '${path}/search/initQueryColumn.action',
			type: 'post',
	    	datatype: 'text',
	    	async:	false,
	    	data: "publishType=" +publishType,
	    	success :function(data){
	    		queryColumn = data;
	    	}
		})
		queryColumn=eval('('+queryColumn+')');
		dt = new DataGrid();
		xmenuNum="";
		//直接生成高级查询项
		directCreateQueryCondition(publishType);
		modifyCss();
		//$("#queryFrame").selectpicker('refresh');
		dt.setConditions([]);
		dt.setPK('objectId');
		dt.setSortName('objectId');
		dt.setURL('${path}/search/queryFormList.action?publishType='+$("#publishType").val()+"&params=");
		dt.setSortOrder('desc');
		dt.setSelectBox('objectId');
		dt.setColums(queryColumn);
		/* dt.setColums([ <app:QueryListColumnTag publishType="${publishType}"/>
		{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}]); */
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
			});
		}
		
		onLoadDict();   //加载数据字典项
		//xmenuNum 记录xmenu要初始化的方法名称，解决当资源切换时xmenu初始化就加载导致无效
		if(xmenuNum != "" && xmenuNum != null){
			xmenuNum = xmenuNum.substring(0,xmenuNum.length-1);
			var menuList = xmenuNum.split(",");
			for(var i=0;i<menuList.length;i++){
				var menu = menuList[i]+"()";
				eval(menu);
			}
			
		}
	}
	$operate = function(value,rec){
		var opt= "<sec:authorize url="/publishRes/openDetail.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a></sec:authorize>";
		return opt;
	};
	
	function detail(objectId){
		$.openWindow("${path}/publishRes/openDetail.action?objectId="+objectId+"&searchFlag=close"+"&flagSta=3",'资源详细页',1170,500);
	}
	
	//根据资源类型和查询条件，查询结果，
	//params：存储所有的查询条件
	function queryForm(params) {
		dt.dataGridObj.datagrid({
					url : "${path}/search/queryFormList.action?publishType="+$("#publishType").val()+"&params="+params
				}); 
	}
	
	//当资源类型改变时调用此方法，以此生成定义的查询条件的输入框
	function getQueryCondition(){
		refshSearch();
		xmenuNum="";
		$('#queryCondition').children().remove();
        var conditionSize = $("select[name=queryField]").size();
		var publishType = $("#publishType").val();
		var flag = $("#flag").val();
		for(var i=1;i<=conditionSize;i++){
			var count = "";
			if(i<10){
				count = "0" + i;
			}
			
			$("#condition"+count).remove();
		}
		lineCount = 0;
		//生成查询组件
		directCreateQueryCondition(publishType,flag);
		modifyCss();
		
		//params此时为空，则查询该资源类型下的所有资源
		var params = "";
		queryForm(params);
		dt.query();
		
		//为查询按钮添加点击事件（主要是当资源类型改变时，为查询按钮重新添加onclick事件）
 		$('#queryCondtionButton').on('click', function() {
 			//获取页面上输入的查询条件组合,和在查询条件中输入的值
 			//var params = getAllQueryConditions();  原来得样式的方法
 			var params = getAllQueryFieldConditions();
 			queryForm(params);
 		});
		
		
		//为数据字典填充，数据，防止页面没有加载完成后加载数据会无效
		onLoadDict();
		//xmenuNum 记录xmenu要初始化的方法名称，解决当资源切换时xmenu初始化就加载导致无效
		if(xmenuNum != "" && xmenuNum != null){
			xmenuNum = xmenuNum.substring(0,xmenuNum.length-1);
			var menuList = xmenuNum.split(",");
			for(var i=0;i<menuList.length;i++){
				var menu = menuList[i]+"()";
				eval(menu);
			}
			
		}
	};
	
	//为数据字典项添加数据字典的数据
	function onLoadDict(){
		var publishType = $("#publishType").val();
		var flag = $("#flag").val();
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
	
	//导出元数据
	function downMetada(){
		var codes = getChecked('data_div','objectId').join(',');
		//获取dataGrid每页显示的条数,getData参数为固定值
		var dataDiv = $('#data_div').datagrid('getData');
		var pageSize = dataDiv.rows.length;   //获取页面显示的条数
		if(codes == null || codes == ""){
			if(pageSize > 0){
				$('#myModal').modal('show');
			}else{
				$.alert("资源为空，不能导出");
			}
			return;
		}
		
		var publishType = $("#publishType").val();
		//若直接选择指定的几条数据
		var searchParamCa ={
				ids:codes,
				batch:"1",
				publishType:publishType
		};
		
		if(codes != ''){						  
			var paramCa = encodeURI(JSON.stringify(searchParamCa));
			downLoading = $.waitPanel('下载中请稍候...',false);
			$.ajax({
				url:'${path}/search/getExportExcel.action',
			    type: 'post',
			    datatype: 'json',
			    data:"searchParamCa=" + paramCa,
			    success: function (returnValue) {
			    	if(returnValue!=""){
			    		downLoading.close();
			    		window.location.href = '${path}/search/getExportExcelDown.action?excelFilePath='+returnValue;
			    	}else{
			    		$.alert("文件下载数量超过数据字典定义大小，不能下载");
			    		downLoading.close();
			    	}
			    }
			});
		}
	}
	
	
	//下载资源文件，分为两种情况
	//1.若直接选择指定的资源，则下载对应资源下的文件
	//2.若没有选择特定的资源，则可以按照输入开始页结束页进行下载
	function downFileResource(){
		var codes = getChecked('data_div','objectId').join(',');
		
		//获取dataGrid每页显示的条数,getData参数为固定值
		var dataDiv = $('#data_div').datagrid('getData');
		var pageSize = dataDiv.rows.length;   //获取页面显示的条数
		if(codes != ''){
			//获取资源类型
			var resourceType= $('#publishType').val();
			$.openWindow("${path}/search/searchIndexDownFile.jsp?publishType="+resourceType+"&ids="+codes+"&ftpFlag=2",'文件下载',630,280);
		}else{
			if(pageSize >0){
				if(codes == null || codes == ""){
					$("#Resource").modal('show');
				}
			}else{
				$.alert("资源为空，不能导出");
			}
			
		}
	}
	
	//保存类型，重新加载页面,根据不同的资源类型根据在元数据模板中的配置加载显示列
	function refshSearch(){
		location.href = "${path}/search/gotoSearchIndex.action?publishType="+$("#publishType").val();
	}
</script>
</head>
<body id="myBody">
	<div id="fakeFrame" class="container-fluid by-frame-wrap "
		style="height: auto;">
		<div class="panel panel-default">
			<div class="panel-body" id="999">
				<div class="container-fluid form-horizontal">
					<div class="row" style="margin-top: 10px">
						<label class="control-label col-md-4 text-right"
							style="margin-left: -340px">资源类型：</label>
						<div class="col-md-3">
							<app:selectResType name="publishType" id="publishType"
								headName="" headValue="" selectedVal="${publishType}" onchange="getQueryCondition()"/>
						</div>
					</div>
					<div class="by-tab">
						<div class="portlet">
							<div class="portlet-title" style="width:100%">
								<div class="caption" style="margin-left: -20px">
									隐藏查询条件 <a href="javascript:;" onclick="togglePortlet(this)"><i
										class="fa fa-angle-up"></i></a>
								</div>
							</div>
							<form id="myForm">
								<div class="portlet-body" id="queryFrame">
									<div class="container-fluid" id="queryCondition"></div>
								</div>
							</form>
						</div>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="height: 450px;"></div>
			</div>
				<%@ include file="/search/resDownMetada.jsp"%>
				<%@ include file="/search/searchIndexPageFile.jsp"%>
		</div>
	</div>
</body>
</html>