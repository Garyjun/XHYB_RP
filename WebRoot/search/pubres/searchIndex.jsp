<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>高级检索</title>
<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
<script type="text/javascript" src="${path}/bres/classtype.js"></script>
<script type="text/javascript" src="${path}/bres/res_operate.js"></script>
<script type="text/javascript">
	$(function(){
		getStatusMap();
		var _divId = 'data_div'; 
		var _pk = 'id';
		var _url ="${path}/search/pubres/list.action";
		var _conditions = ['publishType','status','title','creator','description','res_version','keywords','modifiedStartTime','modifiedEndTime','searchText','queryType','publishStartDate','publishEndDate','cbclass'];
		var _sortName = 'modified_time';
		var _sortOrder = 'desc';
		var _check = false;
		var _colums = [ 
				{field:'commonMetaData.commonMetaDatas.title',title:'书名',width:fillsize(0.17)},
				{field:'commonMetaData.commonMetaDatas.creator',title:'作者',width:fillsize(0.07)},
				{field:'commonMetaData.commonMetaDatas.keywords',title:'关键字',width:fillsize(0.1)},
				{field:'commonMetaData.commonMetaDatas.status',title:'状态',width:fillsize(0.07),formatter:$statusDesc},
				{field:'commonMetaData.commonMetaDatas.modified_time',title:'发布时间',width:fillsize(0.18)},
			    {field:'opt1',title:'操作',width:fillsize(0.20),align:'center',formatter:$operate}
		];
		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false);
	});
	
	
	function doDown(codes){
		down4Encrypt('${path}/publishRes/downloadPublishRes.action?ids='+codes);
	}
	
	$operate = function(value,rec){
		var optArray = new Array();
		optArray.push("<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>");
		optArray.push("<a class=\"btn hover-red\" href=\"javascript:readRes('"+rec.path+"','"+rec.objectId+"')\" ><i class=\"fa fa-eye\"></i> 预览</a>");
		optArray.push("<a class=\"btn hover-red\" href=\"javascript:doDown('"+rec.objectId+"')\" ><i class=\"fa fa-download\"></i> 下载</a>");
		return createOpt(optArray);
	};
	
	function detail(objectId){
		var openUrl = "${path}/publishRes/detail.action?objectId="+objectId+"&flagSta=1";
		$.openWindow(openUrl,'详细信息',1024,500);
		//ndow.location.href = "${path}/publishRes/detail.action?objectId="+objectId;
	}
	
	function readRes(path,objectId){
		readFileOnline(path,objectId,"tszy");
	}
	
	function queryForm(){
		$grid.query();
	}
	
	function downAllRes(){
		var initWaiting = $.waitPanel('正在下载请稍后...',false);
		$.get('${path}/search/pubres/initExportRes.action',function(data){
			$('#downFile').attr('src',"${path}/search/pubres/exportRes.action?filePath="+data);
			initWaiting.close();
		});
	}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="width: 85%">
		<input type="hidden" id="queryType" name="queryType" value="0"/>
		<input type="hidden" id="module" name="module" value="TB"/>
		<input type="hidden" id="libType" name="libType" value="2"/>
		<input type="hidden" id="type" name="type" value=""/>
		
		<div class="panel panel-default">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">高级查询</a></li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div class="container-fluid form-horizontal">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label col-md-4">书名：</label>
								<div class="col-md-8">
									<input type="text" name="title" id="title" class="form-control" style="width: 250px;display:inline;" />
								</div>
							</div>
						</div>
						
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label col-md-4">作者：</label>
								<div class="col-md-8">
									<input type="text" name="creator" id="creator" class="form-control" style="width: 250px;display:inline;" />
								</div>
							</div>
						</div>
						
						<div class="col-md-6 filed">
							<div class="form-group">
								<label class="control-label col-md-4">关键词：</label>
								<div class="col-md-8">
									<input type="text" name="keywords" id="keywords" class="form-control" style="width:250px; display: inline;" />
								</div>
							</div>
						</div>
						
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label col-md-4">中图分类：</label>
								<div class="col-md-8">
									<input type="text" name="cbclass" id="cbclass" class="form-control" style="width: 250px;display:inline;" />
								</div>
							</div>
						</div>
						
						<div class="col-md-6 filed">
							<div class="form-group">
								<label class="control-label col-md-4">发布时间：</label>
								<div class="col-md-8">
										<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})" id="modifiedStartTime" name="modifiedStartTime" style="width: 119px;display: inline;"/> -
										<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})" id="modifiedEndTime" name="modifiedEndTime" style="width: 119px;display: inline;"/>
								</div>
							</div>
						</div>
						
						<div class="col-md-6 filed">
							<div class="form-group">
								<label class="control-label col-md-4">出版时间：</label>
								<div class="col-md-8">
										<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'publishEndDate\')}'})" id="publishStartDate" name="publishStartDate" style="width: 119px;display: inline;"/> -
										<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'publishStartDate\')}'})" id="publishEndDate" name="publishEndDate" style="width: 119px;display: inline;"/>
								</div>
							</div>
						</div>
						
						<div class="col-md-6">
							<div class="form-actions">
								<input type="button" value="查询" class="btn btn-primary blue "  style="width:100px;" onclick="queryForm();" />
								<input id="downTemplate"  type="button" value="全部导出" class="btn btn-primary" onclick="downAllRes()"/>
							</div>
					    </div>
					    
					    <div class="col-md-6">
							<div class="form-group">
								<label class="control-label col-md-4">资源状态：</label>
								<div class="col-md-6">
									<app:constants name="status" repository="com.brainsoon.system.support.SystemConstants" className="PublishResourceStatus" inputType="select" headerValue="全部" cssType="form-control" ></app:constants>
								</div>
							</div>
						</div>
					    
				</div>
				<div id="data_div" class="data_div height_remain" style="height: 450px;"></div>
			</div>
		</div>
	</div>
	<iframe id="downFile" name="downFile"  style="display: none;"></iframe>
</body>
</html>