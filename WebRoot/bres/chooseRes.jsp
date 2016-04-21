<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>选择列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript">
		var resId = '${param.id}';
		$(function(){
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = 'list.action';
	   		var _pk = 'objectId';
	   		var _conditions = ['module','type','status','eduPhase','version','subject','grade','fascicule','title','creator','description','keywords','modifiedStartTime','modifiedEndTime','searchText','queryType','libType'];
	   		var _sortName = 'commonMetaDatas.modified_time';
	   		var _sortOrder = 'desc';
			var _colums = [{field:'commonMetaData.commonMetaDatas.unit',title:'内容单元',width:fillsize(0.2),sortable:true},
							{field:'commonMetaData.commonMetaDatas.title',title:'资源标题',width:fillsize(0.17),sortable:true},
							{field:'commonMetaData.commonMetaDatas.creator',title:'制作者',width:fillsize(0.07),sortable:true},
							{field:'commonMetaData.commonMetaDatas.keywords',title:'关键字',width:fillsize(0.1),sortable:true},
							{field:'commonMetaData.commonMetaDatas.modified_time',title:'更新时间',width:fillsize(0.18),sortable:true},
							{field:'opt1',title:'操作',width:fillsize(0.1),align:'center',formatter:$operate}];
	   		
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
			return opt;
					
		};
		
		
		/***查看***/
		function detail(objectId){
			$.openWindow("${path}/bres/openDetail.action?libType=${param.libType}&objectId="+objectId,'资源详细',800,450);
		//	window.location.href = "${path}/bres/detail.action?libType=${param.libType}&objectId="+objectId;
		}
		function query(){
			$grid.query();
		}
		
		/***添加关联***/
		function add() {
			var codes = getChecked('data_div','objectId').join(',');
			if (codes == '') {
			    $.alert('请选择要添加的资源！');
			} else {
				addRelations(codes);
			}
		}
		function addRelations(ids){
			$.post('addRelationRes.action',{id:resId,relationIds:ids},function(data){
				top.index_frame.work_main.$grid2.query();
				$.closeFromInner();
			});
		}
	</script>
</head>
<body class="win-dialog">
<div class="form-wrap">
<div class="container-fluid">
<form id="queryForm" action="" target="work_main" method="post">
	<div class="row">
		<div class="col-xs-6">
            <div class="form-group">
                <label class="control-label col-xs-4">资源标题：</label>

                <div class="col-xs-8">
                	<input type="text" name="title" id="title" class="form-control"/>
                </div>
            </div>
        </div>
		<div class="col-xs-6" >
            <div class="form-group">
                <label class="control-label col-xs-4">制作者：</label>

                <div class="col-xs-8">
                	<input type="text" name="creator" id="creator" class="form-control"/>
                </div>
            </div>
        </div>
        </div><div class="row">
		<div class="col-xs-6" style="margin:6px 0px 0px 0px;">
            <div class="form-group">
                <label class="control-label col-xs-4">关键字：</label>

                <div class="col-xs-8">
                	<input type="text" name="keywords" id="keywords" class="form-control"/>
                </div>
            </div>
        </div>
		<div class="col-xs-6" style="margin: 6px 0px 0px 0px;">
            <div class="form-group">
                <label class="control-label col-xs-4">摘要：</label>

                <div class="col-xs-8">
                	<input type="text" name="description" id="description" class="form-control"/>
                </div>
            </div>
        </div>
         </div><div class="row">
		<div class="col-xs-6" style="margin: 6px 0px 0px 0px;">
            <div class="form-group">
                <label class="control-label col-xs-4">开始时间：</label>

                <div class="col-xs-8">
                	<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})" id="modifiedStartTime" name="modifiedStartTime"/>
                </div>
            </div>
        </div>
		<div class="col-xs-6" style="margin: 6px 0px 0px 0px;">
            <div class="form-group">
                <label class="control-label col-xs-4">结束时间：</label>

                <div class="col-xs-8">
                	<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})" id="modifiedEndTime" name="modifiedEndTime"/>
                </div>
            </div>
        </div>
        <div class="col-xs-12" align="right" style="padding-right: 10px;padding-top: 6px;">
        	<input type="button" value="查询" class="btn btn-primary red" onclick="query()"/>  
        	<input type="button" value="清空" class="btn btn-primary" onclick="$('#queryForm')[0].reset();"/>  
        </div>
    </div>
</form>
</div>
<div class="panel-body height_remain">
	<div style="margin: 0px 10px 10px 0px;">
		<input type="hidden" id="module" name="module" value="${param.module }" />
		<input type="hidden" id="type" name="type" value="${param.type }" />
		<input type="hidden" id="status" name="status" value="" />
		<input type="hidden" id="eduPhase" name="eduPhase" value="${param.eduPhase }" />
		
		<input type="hidden" id="libType" name="libType" value="${param.libType }" />
		<input type="hidden" id="queryType" name="queryType" value="1" />
		<input type="button" value="选择" class="btn btn-primary" onclick="add()"/>  
	</div>
	<div id="data_div" class="data_div height_remain" style="width: 750px;"></div>
</div>
</div>
</body>
</html>