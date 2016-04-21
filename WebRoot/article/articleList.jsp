<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
<title>碎片列表</title>
<script type="text/javascript">
	$(function(){
		var _divId = 'data_div';
		var	_url = '${path}/article/queryList.action';
		var _pk = 'id';
		var _conditions = ['joulName','wordName','fragType','treeId'];
		var _sortName = 'id';
		var _sortOrder = 'desc';
		var _check=true;
		var _colums = [
				{ title:'文章标题', field:'metadataMap.title' ,width:fillsize(0.25), align:'center',formatter:$title},
				{ title:'所属期刊分类', field:'metadataMap.wzJournalClass' ,width:fillsize(0.06), align:'center' },
				{ title:'所属期刊名称', field:'metadataMap.magazine' ,width:fillsize(0.06), align:'center' },
				{ title:'来源',field:'metadataMap.source',width:fillsize(0.10), align:'center'},
				{ title:'所属期刊期号', field:'metadataMap.articleOfJul' ,width:fillsize(0.06), align:'center' },
				{title:'操作',field:'opt1',width:fillsize(0.10),align:'center',formatter:$operates}];
		accountHeight();
		$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
	});
	$operates = function(value,rec){
		var optArray = new Array();
		var optDetail = "<a class=\"btn hover-red\" href=\"javascript:view('"+rec.objectId+"')\"><i class=\"fa fa-sign-out\"></i>详细</a>";
		var optUpdate = "<a class=\"btn hover-red\" href=\"javascript:edit('"+rec.objectId+"')\"><i class=\"fa fa-edit\"></i>修改</a>";
		optArray.push(optDetail);
		optArray.push(optUpdate);
		return optArray;
	}
	
	//内容
	$title = function(value,rec){
		var contents=rec.metadataMap.title;
		var tempValue = '';
		if(contents.length>30){
			tempValue = contents.substring(0,30)+"...";
		}else{
			tempValue = contents;
		}
		contents = "<span data-toggle='tooltip' data-placement='top' title='"+contents+"' >"+tempValue+"</span>";
		return 	contents;
	}

	
	function queryList(){
		$grid.query();
	}
	
	//查询详细
	function view(id){
		window.location.href = "${path}/article/Detail.action?objectId="+id;
	}
	
	//修改
	function edit(id){
		window.location.href = "${path}/article/Edit.action?objectId="+id;
	}
	
	//清空
	function formReset(){
		$('#queryForm')[0].reset();
		queryList();
	}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="portlet" style="height: 100%;">
			<div class="portlet-title" style="padding: 0px;background-color: #e7e1d3;">
				<div class="caption" style="background-color:#e7e1d3; ">查询条件 <div style="float:right;"><a href="javascript:;" onclick="togglePortlet(this)">         
					<i class="fa fa-angle-up"></i></a></div>
				</div>
			</div>
			<div class="portlet-body">
				<div class="panel-body height_remain" id="999" style="background-color: #eeebe4;">
					<form action="#" class="form-inline no-btm-form"  role="form" id="queryForm">
						<div style="float: left;">
							<div class="form-group">
								<span>所属期刊名称：</span>
							  	<input type="text" class="form-control" id="joulName" name="joulName"/>
							</div>
						</div>
						<div style="float: right;">
							<button type="button" class="btn btn-primary" style="background-color: #e0d1bc;font-size: 14;font-style:#7a521e;" onclick="queryList();">查询</button>
							<button type="button" class="btn btn-primary" style="background-color: #e0d1bc;font-size: 14;font-style:#7a521e;" onclick="formReset();">清空</button>
						</div>
					</form>
				</div>
			</div>
			<div class="panel-body height_remain" id="999" style="margin-top: 5px;">
				<div id="data_div" class="data_div height_remain" style="width:100%;"></div>
			</div>
		</div>
	</div>
	<input type="hidden" id="treeId" name="treeId" value="${param.treeId }"/>
</body>
</html>