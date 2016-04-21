<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>批量检索</title>
<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
<script type="text/javascript">
	$(function(){
	//定义一datagrid
		initDatagrid();
	});
	
	function initDatagrid(){
		var publishType = $("#publishType").val();
		var queryColumn = "";
		 $.ajax({
			url: '${path}/publishRes/initQueryColumn.action',
			type: 'post',
	    	datatype: 'text',
	    	async:	false,
	    	data: "publishType=" +publishType,
	    	success :function(data){
	    		queryColumn = data;
	    	}
		})
		queryColumn=eval('('+queryColumn+')');
		changeRows();
		init();
		var _divId = 'data_div';
		var _url = '${path}/publishRes/query.action';
		var _pk = 'objectId';
		var _conditions = [<app:QueryConditionBatchTag/>,'publishType','status'];
		var _sortName = 'create_time';
		var _sortOrder = 'desc';
		var _colums = queryColumn;
	   /*  var _colums = [
					<app:QueryListColumnTag/>
					{field:'createTime',title:'创建日期',width:fillsize(0.10),align:'center'},
					{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}]; */
	    accountHeight();
		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	}
	
	$operate = function(value,rec){
		var opt = "<sec:authorize url="/publishRes/batch/openDetail.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a></sec:authorize>";
		return opt;
	};
	
	function changeRows() {
		$.fn.pasteEvents = function( delay ) {
		     if (delay == undefined) delay = 10;
		     return $(this).each(function() {
		         var $el = $(this);
		         $el.on("paste", function() {
		             $el.trigger("prepaste");
		             setTimeout(function() { $el.trigger("postpaste"); }, delay);
		         });
		     });
		 };
		 var num = 0;
		 $("#contentDatas").keydown(function(event) {
			 num = $("#contentDatas").val().split("\n").length;
			 $("#num").text(num+" 行");
		}); 
		// 使用
		$("#contentDatas").on("postpaste", function() { 
		    var content = $("#contentDatas").val();
		    num = content.split("\n").length;
		    $("#num").text(num+" 行");
		 }).pasteEvents();

	}
	
	function queryForm(){
		var searchFlag = $("#searchFlag").val();  //获取选择的查询项
		var searchFileType = $('#searchFlag option:selected').attr("name");  //获取查询字段在元数据中配置的fileType
		//var searchDict = $('#searchFlag option:selected').attr("label");
		var fileTypeAndDictKey =  searchFileType.split("-");
		var fileType = "";
		var dictKey = "";
		if(fileTypeAndDictKey.length>0){
			fileType = fileTypeAndDictKey[0]; //元数据配置的文本框类型
			dictKey = fileTypeAndDictKey[1];  //数据字典的索引
		}
		
		var id = searchFlag+"_metaField";
		var centent = $("#contentDatas").val();   //获取输入查询内容框中的内容
		centent = centent.replace(/\n/g,",-");
		var queryCondit = "";
		
		//1单文本 5文本域 4单选 3多选 2下拉选择 7日期 8URL 6树形 11单位 10人员
		if(fileType == '2' || fileType == '3' || fileType == '4' || fileType == '6' 
				|| fileType == '10' || fileType == '11'){
			
			$.ajax({
				url:'${path}/publishRes/changeMetadata.action?fileType='+fileType+'&condition='+centent+'&dictKey='+dictKey,
				type: 'post',
				datatype:'text',
				async:false,
				success :function(data){
					queryCondit = data;
				}
			})
		}
		if(fileType == '7'){
			var dateLongType = centent.split(",-");
			for(var i = 0;i<dateLongType.length;i++){
				var firstDate = new Date(dateLongType[i]);
				var secondDate = new Date(firstDate.valueOf() - 8*60*60*1000);
				queryCondit += new Date(secondDate).getTime()+",-";
			}
			if(queryCondit != "" && queryCondit != ''){
				queryCondit = queryCondit.substring(0,queryCondit.length-2);
			}
		}
 		<app:RsetHiddenBatchFlagTag />
 		if(queryCondit != "" && queryCondit != '' && queryCondit != null){
 			centent = queryCondit;
 		} 
		$("#"+id).val(centent);
		$grid.query();
	}
	
	function detail(objectId){
		$.openWindow("${path}/publishRes/openDetail.action?objectId="+objectId+"&searchFlag=close"+"&flagSta=1",'资源详细页',1000,550);
	}
	
	//导出元数据，若没在页面上选取记录则根据输入的页数，进行导出，若选中了记录，则导出选中的记录
	//fengda 2015年11月3日
	function exportResDown() {
		var codes = getChecked('data_div','objectId').join(',');
		//获取dataGrid每页显示的条数,getData参数为固定值
		var dataDiv = $('#data_div').datagrid('getData');
		var pageSize = dataDiv.rows.length;   //获取页面显示的条数
		if(codes == null || codes == ""){
			if(pageSize > 0){
				$("#myModal").modal('show');
			}else{
				$.alert("资源为空，不能导出");
			}
			return;
		}
		
		//若直接选择指定的几条数据
		var publishType = $("#publishType").val();
		var searchParamCa = {
				ids:codes,
				batch:"1",
				publishType:publishType
				
	        };
		
		if (codes!= '') {
			var paramCa  = encodeURI(JSON.stringify(searchParamCa));
			downLoading = $.waitPanel('下载中请稍候...',false);	
			$.ajax({
				url:'${path}/publishRes/getExportExcel.action',
			    type: 'post',
			    datatype: 'json',
			    data:"searchParamCa=" + paramCa,
			    success: function (returnValue) {
			    	if(returnValue!=""){
			    		downLoading.close();
			    		window.location.href = '${path}/publishRes/getExportExcelDown.action?excelFilePath='+returnValue;
			    	}else{
			    		$.alert("下载数量超过数据字典定义大小，不能下载");
			    		downLoading.close();
			    	}
			    }
			});
			
//           location.href='${path}/publishRes/getExportExcel.action?ids='+codes+"&publishType="+$("#publishType").val()+"&batch=batch";
          
		}
	}
	
	//初始化
	function init(){
		$('#searchFlag').empty();
		$.ajax({
			type:"post",
			url:"${path}/system/metaData/queryBatchFlag.action?publishType="+$("#publishType").val(),
			success:function(data){
				 var list = JSON.parse(data);
				 for(var i=0;i<list.length;i++){
						var listSon = list[i];
						var name = listSon.name;
						var id = listSon.id;
						var dictKey = listSon.dictKey; //获取数据字典的索引
						//1单文本 5文本域 4单选 3多选 2下拉选择 7日期 8URL 6树形 11单位 10人员
						//获取该字段在元数据中配置的文本框类型
						var fileType = listSon.fileType;   
						var html = $("<option value="+ id +" name="+fileType+"-"+dictKey+">" + name + "</option>");
						html.appendTo($('#searchFlag')); 
					}
			}
		});
		//将数据清空 huagnjun 2015年8月7日 13:53:02
		$("#contentDatas").val("");
		<app:RsetHiddenBatchFlagTag />
		$("#num").text(0+" 行");
	}
	
	
	function resetQueryForm() {
		$("#publishType").val("");
		init();
		$("#contentDatas").val("");
		<app:RsetHiddenBatchFlagTag />
		$("#num").text(0+" 行");
		$grid.query();
	}
	
	//下载资源文件  fengda 2015年11月3日
	function batchDown(){
		var codes = getChecked('data_div','objectId').join(',');
		if(codes != ''){
			//获取资源类型
			var resourceType= $('#publishType').val();
			$.openWindow("${path}/statistics/batchNum/downFile.jsp?publishType="+resourceType+"&ids="+codes+"&ftpFlag=2",'文件下载',630,280);
		}else{
			
			var codes = getChecked('data_div','objectId').join(',');
			var dataDiv = $('#data_div').datagrid('getData');
			var pageSize = dataDiv.rows.length;   //获取页面显示的条数
			if(pageSize > 0){
				if(codes == null || codes == ""){
					$("#Resource").modal('show');
				}
			}else{
				$.alert("资源为空，不能导出");
			}
			
			//$.openWindow("${path}/statistics/batchNum/downFilePage.jsp?publishType="+resourceType+"&ids="+codes+"&ftpFlag=2",'文件下载',630,300);
		}
	}
	
	//当资源类型改变时，
	function refshSearch(){
		location.href = "${path}/publishRes/gotoBatchNum.action?publishType="+$("#publishType").val();
	}
		
// 		$.ajax({
// 			url:'${path}/publishRes/downFileSize/${param.libType}.action?ids='+codes,
// 		    type: 'post',
// 		    datatype: 'text',
// 		    success: function (returnValue) {
// 		    	data = eval('('+returnValue+')');
// 		    	if(data.boo==1){
// 		    		//文件大小超出系统规定大小，就会默认选择FTP下载，并且被压缩
// 		    		$.openWindow("${path}/statistics/batchNum/downFile.jsp?publishType=${param.publishType}"+"&ids="+codes+"&ftpFlag=2",'文件下载',630,280);
// 		    	}else{
// 		    		$.openWindow("${path}/statistics/batchNum/downFile.jsp?publishType=${param.publishType}"+"&ids="+codes,'文件下载',630,280);
// 		    	}
// 		    }
// 		});

</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="width: 85%">
	<app:HiddenBatchFlagTag />  <!-- 生成元数据中配置为批量查询的元数据，对应的隐藏域的input文本框 -->
	<input type="hidden" id="pageSize" name="pageSize" value=""/>
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">批量检索</a></li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div class="container-fluid form-horizontal">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label col-md-4">资源类型：</label>
								<div class="col-md-8">
								    <app:selectResType name="publishType" id="publishType"  selectedVal="${publishType}" headName=""  headValue="" onchange="refshSearch();"/>
								<!-- 
										<select class="form-control" name="sourceType" id="sourceType">
											<option value="">请选择</option>
										</select> -->
								</div>
							</div>
						</div>
						
						<div class="col-md-6 filed">
							<div class="form-group">
								<label class="control-label col-md-4">查询标识：</label>
								<div class="col-md-8">
									<select class="form-control" name="searchFlag" id="searchFlag">
										<!-- <option id="isbn" value="isbn">ISBN</option>
										<option value="title">资源名称</option> -->
									</select>
								</div>
							</div>
						</div>
						
						<div class="col-md-6 filed">
							<div class="form-group">
								<div class="col-md-8" style="margin-left:82px">
									<textarea class="form-control" cols="65" rows="15" name="contentDatas" id="contentDatas"></textarea>
									<label class="control-label">已输入：<span id="num">0行</span></label>
								</div>
							</div>
						</div>
						
						<div class="col-md-6">
							<div class="form-group">
							   <div class="col-md-8" style="margin-left:175px">
								<div class="panel panel-warning">
  									<div class="panel-heading">*备注说明：</div>
  										<ul class="list-group">
    										<li class="list-group-item">*最大输入行数为500行</li>
    										<li class="list-group-item">*一行为一个如:ISBN、DOI、ISSN</li>
   											<!-- <li class="list-group-item">*DOI必须由字母或数字类型组成</li> -->
 									 	</ul>
						   		</div>
						   		</div>
							</div>
						</div>
						<div class="col-md-6" style="margin-left:160px">
							<div class="form-actions">
								<input type="button" value="查询" class="btn btn-default red"  style="width:100px;" onclick="queryForm();" />
								<input type="button" value="清空" class="btn btn-default blue"  style="width:100px;" onclick="resetQueryForm();" />
							   <sec:authorize url="/publishRes/getExportExcel.action"><input id="downTemplate"  style="width:100px;" type="button" value="导出元数据" class="btn btn-default blue" onclick="exportResDown()"/></sec:authorize>
								<input id="downTemplate"  style="width:100px;" type="button" value="下载资源文件" class="btn btn-default blue" onclick="batchDown()"/>
						  </div>
			   			</div>
					</div>
					<div id="data_div" class="data_div height_remain" style="height: 450px;"></div>
				</div>
				<%@ include file="/taskprocess/resCheck.jsp" %>
				<%@ include file="/statistics/batchNum/downFilePage.jsp"%>
			</div>
		</div>
</body>
</html>