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
<style type="text/css">
.keyword {
	color: #950f10;
	font-weight: bold;
}

.recorddiv {
	HEIGHT: 94px
}

.recorddiv .h4 {
	LINE-HEIGHT: 20px;
	MARGIN-BOTTOM: 6px;
	border-bottom: 0px;
}

.resImg {
	height: 118px;
	width: 84px;
	float: left;
	margin-right: 10px;
}

.typespan {
	COLOR: #fff;
	PADDING-BOTTOM: 1px;
	BACKGROUND-COLOR: #941010;
	PADDING-LEFT: 3px;
	PADDING-RIGHT: 0px;
	PADDING-TOP: 1px;
	MARGIN-RIGHT: 10px;
}

.unDisplayMore {
	margin-left: 95px;
	display: none;
}

.displayMore {
	margin-left: 95px;
}

em {
	font-style: normal;
	COLOR: #f00;
}

/* .datagrid-header { */
/* 	position: absolute; */
/* 	visibility: hidden; */
/* } */
</style>
<script type="text/javascript">
	$(function(){
		getStatusMap();
		var _divId = 'data_div';
		var _pk = 'id';
		var _url ="${path}/search/pubres/fullsearch/list.action";
		var _conditions = ['keyword'];
		var _sortName = 'modified_time';
		var _sortOrder = 'desc';
		var _check = false;
		var _colums = [ 
				/* {field:'cover',title:'封面',width:fillsize(0.17)},
				{field:'title',title:'书名',width:fillsize(0.17)},
				{field:'creator',title:'作者',width:fillsize(0.07)},
				{field:'matchContents',title:'内容',width:fillsize(0.1)},
				{field:'status',title:'状态',width:fillsize(0.07),formatter:$statusDesc},
				{field:'modified_time',title:'更新时间',width:fillsize(0.18)},
			    {field:'opt1',title:'操作',width:fillsize(0.20),align:'center',formatter:$operate} */
		];
		$grid = datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false);
	});
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	var flatview = $.extend({}, $.fn.datagrid.defaults.view, {  
	    renderRow: function(target, fields, frozen, rowIndex, rowData){  
	        var view = [];  
	        view.push('<td colspan=' + fields.length + ' style="padding:5px;border:0;">');  
	        if (!frozen){
				if(rowData.cover){
					view.push("<img src='${path}/${fileRoot}/"+rowData.cover+"', width='110' height='80'/>");
				}else{
		            view.push("<img src='${path}/appframe/main/images/nopic.jpg' class='resImg' alt='${path}/appframe/main/images/nopic.jpg' width='110' height='80' style='height:100px;float:left'>");  
				}
	            view.push('<div style="width:100%;">');
	            view.push('<p>&nbsp;&nbsp;&nbsp;&nbsp;'+rowData.matchContents[0]+'....<p>');
        		view.push('&nbsp;&nbsp;书名   '+rowData.title+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;作者   '+rowData.creator);
        		view.push("<a class=\"btn hover-red\" href=\"javascript:detail('"+rowData.uuid+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>");
	            view.push('</div>');  
	        }  
	        view.push('</td>');  
	        return view.join('');  
	    }  
	});  
	
	function datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_showCheck,_pagination,_autoRowHeight,_onLoadSuccess,_frozenColumns){
		$data = $('#'+_divId);
		$data.addClass('datagrid_mark');
		if(typeof(_pagination) == 'undefined'){
			_pagination = true;
		}
		if(_showCheck == undefined || _showCheck == true){
			if(typeof(_frozenColumns) == 'undefined' || _frozenColumns == '' ||  _frozenColumns){
				_frozenColumns = [{field:_pk,checkbox:true,sortable:true}];
			}
		}else if(_showCheck == false){
			_frozenColumns = [];
		}else{
			$.alert("ERROR：表格参数<_showCheck>传入有误，只能为【true】 or 【false】");
		}
		
		if(typeof(_autoRowHeight) == 'undefined'){
			_autoRowHeight = false;
		}
		var _width = $data.width();
		var _this = $data.datagrid({
			width:_width,
			fitColumns: true,
			autoRowHeight:_autoRowHeight,
			nowrap: false,
			striped: true,
			url:_url,
			view: flatview,
			idField:_pk,
			sortName:_sortName,
			queryParams:getQueryParams(_conditions),
 			sortOrder:_sortOrder,
			onLoadSuccess: function (data) {
				if(typeof(_onLoadSuccess) != 'undefined' && _onLoadSuccess != ''){
					var call = new selfCallBack(_onLoadSuccess);
					call.func();
				}
				if(data.length == 0){  
					alert('未找到数据');
				}
			},
			pageList: [10, 15, 20, 25, 30, 35, 40, 45, 50],
			pageSize: 10,
			pagination:_pagination,
			rownumbers:false, 
			singleSelect:true,
			selectOnCheck:false,
			checkOnSelect:false,
			frozenColumns:[_frozenColumns],
			columns:[_colums]
		});
		
		_this.query = function(){
			$data.datagrid('clearSelections');
			$data.datagrid('uncheckAll');
			$data.datagrid('options').queryParams = getQueryParams(_conditions);   
			$data.datagrid('options').pageNumber = 1;     
			$data.datagrid('getPager').pagination({pageNumber: 1});     
			$data.datagrid('reload');  
		};
		return _this;
	} 
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	function doDown(codes){
		down4Encrypt('${path}/publishRes/downloadPublishRes.action?ids='+codes);
	}
	
	$operate = function(value,rec){
		var optArray = new Array();
		optArray.push("<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>");
		optArray.push("<a class=\"btn hover-red\" href=\"javascript:readRes('"+rec.objectId+"')\" ><i class=\"fa fa-eye\"></i> 预览</a>");
		optArray.push("<a class=\"btn hover-red\" href=\"javascript:doDown('"+rec.objectId+"')\" ><i class=\"fa fa-download\"></i> 下载</a>");
		return createOpt(optArray);
	};
	
	function detail(objectId){
		var openUrl = "${path}/publishRes/detail.action?objectId="+objectId+"&flagSta=1";
		$.openWindow(openUrl,'详细信息',1024,500);
	}
	
	function readRes(objectId){
		var paths = '';
		$.ajax({
			type:"post",
			async:false,
			url:_appPath+"/getResFilePaths.action?objectId="+objectId,
			success:function(data){
				data = eval('('+data+')');
				if(data.length > 0){
					paths = data[0].path;
				}
			}
		});
		readFileOnline(paths);//需要该接口 获取ppath 和objectId
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
		<div class="panel panel-default">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">全文检索</a></li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div class="container-fluid form-horizontal">
						<div class="col-md-12">
							<div class="form-group">
								<div class="col-md-10">
									<label class="control-label col-md-4">关键词：</label>
									<div class="col-md-8">
										<input type="text" name="keyword" id="keyword" class="form-control"  style="width: 250px;display:inline;" />
										<input type="button" value="查询" class="btn btn-primary blue " onclick="queryForm();"/>
									</div>
								</div>
							</div>
					    </div>
				</div>
				<br/>
				<br/>
				<div id="data_div" class="data_div height_remain" style="height: 450px;"></div>
			</div>
		</div>
	</div>
	<iframe id="downFile" name="downFile"  style="display: none;"></iframe>
</body>
</html>