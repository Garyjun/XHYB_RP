<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>全文检索</title>
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
		//getStatusMap();
		var _divId = 'data_div';
		var _pk = 'id';
		var _url ="${path}/search/pubres/fullsearch/list.action";
		var _conditions = ['keyword'];
		var _sortName = 'modified_time';
		var _sortOrder = 'desc';
		var _check = false;
		var _colums = [ 
		];
		$grid = datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false);
		
		var dataDiv = $("#searchNamePlace");
		//调用ajax后台返回名称项
		$.ajax({
			url: "${path}/search/pubres/fullsearch/searchKeys.action",
		    type: 'post',
		    datatype: 'text',
		    async:false,
		    success: function (returnValue) {
		    	var data = eval('('+returnValue+')');
		    	 $(data).each(function(index) {
				    	var metaData = data[index];
				    	var key = metaData.name;
				    	var value = metaData.num;
				    	var divRow = $("<button type=\"button\" class=\"btn btn-default\" onclick=\"searchWithKey('"+key+"','"+value+"');\"><font color=\"blue\">"+key+"</font></button>");
				    	divRow.appendTo(dataDiv);
				  });
		    }
		});
	});
	function searchWithKey(key,value){
		$('#keyword').val(key);
		queryForm();
	}
	function clearForm(key,value){
		$('#keyword').val("");
		queryForm();
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	var flatview = $.extend({}, $.fn.datagrid.defaults.view, {  
	    renderRow: function(target, fields, frozen, rowIndex, rowData){
	        var view = [];  
	        if (!frozen){
	       		view.push('<td colspan=' + fields.length + ' style="padding:5px;border:0;">');  
				 if(rowData.genre){
					view.push("<img src='http://upload.cciph.com.cn/"+rowData.genre+"' width='95' height='130' style='float:left'/>");
		            //view.push("<img src='${path}/appframe/main/images/cover_min.png' class='resImg' width='110' height='80' style='height:100px;float:left'>");  
				}else{
		            view.push("<img src='${path}/appframe/main/images/cover_min.png' class='resImg' width='95' height='130' style='height:100px;float:left'>");  
				} 
	            view.push('<div style="width:100%;">');
        		view.push('&nbsp;&nbsp;&nbsp;<span style=\'font-hight:20px;font-size:14px; font-weight:600;\'>'+rowData.title+'</span></br>');
        		//view.push('&nbsp;&nbsp;书名：'+rowData.title);
        		view.push('<span style=\'font-hight:20px;\'>&nbsp;&nbsp; 作者：'+rowData.authorname);
        		view.push('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ISBN：'+rowData.isbn);
        		view.push("</span><a class=\"btn hover-red\" href=\"javascript:detail('"+rowData.uuid+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>");
	            view.push('<p>&nbsp;&nbsp;&nbsp;&nbsp;'+rowData.matchContents+'....<p>');
        		/* view.push("<a class=\"btn hover-red\" href=\"javascript:readRes('"+rowData.uuid+"')\" ><i class=\"fa fa-eye\"></i> 预览</a>"); */
        		view.push('</div>');  
	        	view.push('</td>'); 
	        }  
	        $(".datagrid-btable").attr("width","100%");
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
			pageList: [10, 20, 50, 100],
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
		$.openWindow("${path}/publishRes/openDetail.action?objectId="+objectId+"&searchFlag=close"+"&flagSta=1",'资源详细页',1000,550);
		/* var openUrl = "${path}/publishRes/toDetail.action?objectId="+objectId+"&flagSta=1";
		$.openWindow(openUrl,'详细信息',1024,500); */
	}
	
	function readRes(objectId){
		var paths = '';
		$.ajax({
			type:"post",
			async:false,
			url:_appPath+"/bres/getResFilePaths.action?objectId="+objectId,
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
		//查询前清空数据
		//$grid.clean();
		$('#data_div').datagrid('loadData',{total:0,rows:[]});
		
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
								<div class="col-md-16">
									<label class="control-label col-md-4">关键词：</label>
									<div class="col-md-8">
										<input type="text" name="keyword" id="keyword" class="form-control"  style="width: 250px;display:inline;" />
										<input type="button" value="查询" class="btn btn-primary blue " onclick="queryForm();"/>
										<input type="button" value="清空" class="btn btn-primary blue " onclick="clearForm();"/>
									</div>
								</div>
							</div>
					    </div>
					    <div class="form-inline" align="center" id="searchNamePlace"></div>
				</div>
				<br/>
				<div id="data_div" class="data_div height_remain" style="height: 450px;"></div>
			</div>
		</div>
	</div>
	<iframe id="downFile" name="downFile"  style="display: none;"></iframe>
</body>
</html>