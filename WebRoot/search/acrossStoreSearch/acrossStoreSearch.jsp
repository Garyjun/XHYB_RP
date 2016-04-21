<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
<title>垮库检索</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript"
	src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
<script type="text/javascript"
	src="${path}/search/js/bootstrap-select.js"></script>
<link rel="stylesheet" type="text/css"
	href="${path}/search/css/bootstrap-select.css" />
<script type="text/javascript"
	src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
<script type="text/javascript" src="${path}/search/js/map.js"></script>
<script type="text/javascript"
	src="${path}/appframe/util/appDataGrid.js"></script>
	<link rel="stylesheet" type="text/css"
	href="${path}/search/css/modify.css" />
<link rel="stylesheet" type="text/css"
	href="${path}/resRelease/css/multiple-select.css" />
<script type="text/javascript"
	src="${path}/resRelease/js/dictSelect.js"></script>
	<script type="text/javascript"
	src="${path}/resRelease/js/jquery.multiple.select.js"></script>
<script type="text/javascript">
	var dt = new DataGrid();
	$(function() {
		directCreateQueryCondition("","getAcrossStoreCondition");
		modifyCss();
		$("#queryFrame").selectpicker('refresh');

		dt.setConditions([]);
		dt.setPK('objectId');
		dt.setSortName('objectId');
		dt.setURL('${path}/acrossStoreSearch/queryFormList.action?params=');
		dt.setSortOrder('desc');
		dt.setSelectBox('objectId');
		dt.setColums([ <app:QueryListColumnTag/>
		{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}]);
		accountHeight();
		dt.initDt();
		dt.query();
		
		$("select[type=multiSelEle]").multipleSelect({
			multiple: true,
			multipleWidth: 80,
			width: "90%",
			//onOpen: dyAddOpts,
			onClick: optCheckChangeListener
		});
		if($("#queryCondtionButton").length>0){
			$('#queryCondtionButton').on('click', function() {
				var params = getAllQueryConditions();
				params = encodeURI(params);
				queryForm(params);
			});
		}
	});
	$operate = function(value,rec){
		var opt= "<sec:authorize url="/publishRes/across/openDetail.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a></sec:authorize>";
		return opt;
	};
	
	function detail(objectId){
		$.openWindow("${path}/publishRes/openDetail.action?objectId="+objectId+"&searchFlag=close"+"&flagSta=1",'资源详细页',1000,550);
	}
	
	function queryForm(params) {
		dt.dataGridObj.datagrid({
					url : "${path}/acrossStoreSearch/queryFormList.action?params="+params
				}); 
	}
	
	
	//导出元数据  fengda 2015年11月5日
	function downMetada(){
		var codes = getChecked('data_div','objectId').join(',');
		//获取dataGrid每页显示的条数,getData参数为固定值
		var dataDiv = $('#data_div').datagrid('getData');
		var pageSize = dataDiv.rows.length;   //获取页面显示的条数
		if(codes == null || codes == ""){
			if(pageSize >0){
				$('#myModal').modal('show');
			}else{
				$.alert("资源为空，不能导出");
			}
			return;
		}
		
		//若直接选择指定的几条数据
		var searchParamCa ={
				ids:codes
		};
		
		if(codes != ''){						  
			var paramCa = encodeURI(JSON.stringify(searchParamCa));
			downLoading = $.waitPanel('下载中请稍候...',false);
			$.ajax({
				url:'${path}/acrossStoreSearch/getExportExcel.action',
			    type: 'post',
			    datatype: 'json',
			    data:"searchParamCa=" + paramCa,
			    success: function (returnValue) {
			    	if(returnValue!=""){
			    		downLoading.close();
			    		window.location.href = '${path}/acrossStoreSearch/getExportExcelDown.action?excelFilePath='+returnValue;
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
			$.openWindow("${path}/search/acrossStoreSearch/acrossDownFile.jsp?ids="+codes+"&ftpFlag=2",'文件下载',630,280);
		}else{
			if(pageSize > 0){
				if(codes == null || codes == ""){
					$("#Resource").modal('show');
				}
			}else{
				$.alert("资源为空，不支持导出");
			}
			
		}
	}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap "
		style="height: auto;">
		<div class="panel panel-default" style="height: auto;">
			<div class="panel-body height_remain" id="999">
				<div class="container-fluid form-horizontal">
					<div class="by-tab">
						<div class="portlet">
							<div class="portlet-title">
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
				<input type="hidden" value="getAcrossStoreCondition" id="flag"/>
				<div id="data_div" class="data_div height_remain" style="height: 450px;"></div>
			</div>
			<%@ include file="/search/acrossStoreSearch/acrossDownMetada.jsp"%>
			<%@ include file="/search/acrossStoreSearch/acrossStoreFilePage.jsp"%>
		</div>
	</div>
	<input type="hidden" id="publishType" value="getAcrossStoreCondition"/>
</body>
</html>