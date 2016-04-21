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
		var	_url = '${path}/fragment/queryList.action';
		var _pk = 'id';
		var _conditions = ['resourceName','wordName','fragType','treeId'];
		var _sortName = 'id';
		var _sortOrder = 'desc';
		var _check=true;
		var _colums = [
				/* { title:'序号', field:'duanId' ,width:fillsize(0.10), align:'center'}, */
				{ title:'类型', field:'fragType' ,width:fillsize(0.05), align:'center',formatter:$operate },
				{ title:'图片标题', field:'metadataMap.title' ,width:fillsize(0.1), align:'center' },
				{ title:'内容', field:'metadataMap.description' ,width:fillsize(0.5),formatter:$content},
				{ title:'所属期刊',field:'magazineYear', width:fillsize(0.2), align:'center',formatter:$qiname },
				{title:'操作',field:'opt1',width:fillsize(0.10),align:'center',formatter:$operates}];
		accountHeight();
		$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
	});
	$operate = function(value,rec){
		var optArray = "";
		if(rec.fragType=='1'){
			optArray = "<img name='types' width='20' hight='25' id='types' src='${path}/fragment/image/text.png'></img>";
		}else{
			optArray = "<img name='types' width='20' hight='25' id='types' src='${path}/fragment/image/bmp.png'></img>";
		}
		return optArray;
	}
	$operates = function(value,rec){
		var opt = "";
		opt += "<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
// 		opt += "<a class=\"btn hover-red\" href=\"javascript:edit('"+rec.objectId+"')\" ><i class=\"fa fa-edit\"></i> 修改</a>";
		return opt;
	}
	$qiname = function(value,rec){
		var magazineYear = rec.metadataMap.magazineYear;//年份
		var numOfYear = rec.metadataMap.numOfYear;//所在的期刊第几刊
		var title = rec.metadataMap.title;
		var strs= new Array();
		strs = title.split("-");
		var str = magazineYear+"年第"+numOfYear+"期第"+strs[0]+"页第"+strs[1]+"张图";
		var s = "";
		if(str.length>10){
			s = str.substring(0,10)+"...";
		}
		wenname = "<span data-toggle='tooltip' data-placement='top' title='"+str+"' >"+s+"</span>";
		return 	wenname;
	}
	$content = function(value,rec){
		var contents='';
		contents = "<img name='types' width='20' hight='25' id='types' src='${path}/fileDir/fileRoot/期刊/"+rec.metadataMap.description+"'></img>";
		return 	contents;
	}
	/***查看***/
	function detail(objectId){
		window.location.href = "${path}/fragment/toFragmentImageDetail.action?objectId="+objectId+"&flagSta=4&publishType=76"+"&buttonShow=1";
	}
	function queryList(){
		$grid.query();
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
								<span>期刊号</span>
								<select>
									<option>2016</option>
									<option>2015</option>
									<option>2014</option>
									<option>2013</option>
									<option>2012</option>
								</select>
								<span>年</span>
								<select>
									<option>1</option>
									<option>2</option>
									<option>3</option>
									<option>4</option>
									<option>5</option>
									<option>6</option>
									<option>7</option>
									<option>8</option>
									<option>9</option>
									<option>10</option>
									<option>11</option>
									<option>12</option>
								</select>
								<span>月</span>
							</div>
							<div class="form-group">
								<span>文章名称</span><!-- qMatch="like" placeholder="输入文章名称" -->
							  	<input type="text" class="form-control" id="resourceName" name="resourceName"/>
							</div>
							<div class="form-group">
								<span>关键字</span>
								<input type="text" class="form-control" id="wordName" name="wordName"/>
							</div>
							<div class="form-group" style="margin-left: 15px;display: none;">
								<span>类型</span>
								<select id="fragType" name="fragType">
									<option value="2">图片</option>
								</select>
							</div>
						</div>
						<div style="float: right;">
							<button type="button" class="btn btn-primary" style="background-color: #e0d1bc;font-size: 14;font-style:#7a521e;" onclick="queryList();">查询</button>
							<button type="button" class="btn btn-primary" style="background-color: #e0d1bc;font-size: 14;font-style:#7a521e;" onclick="formReset();">清空</button>
						</div>
					</form>
				</div>
			</div>
			<!-- <div class="panel panel-default" style="margin-top: 5px;"> -->
			<div class="panel-body height_remain" id="999" style="margin-top: 5px;">
				<div id="data_div" class="data_div height_remain" style="width:100%;"></div>
			</div>
		<!-- </div> -->
		</div>
	</div>
	<input type="hidden" id="treeId" name="treeId" value="${param.treeId }"/>
</body>
</html>