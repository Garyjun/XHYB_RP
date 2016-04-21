<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
	<script type="text/javascript">
		$(function(){
	   		var _divId = 'data_div';
	   		var _url = '${path}/bres/list.action?flag=1';
	   		var _pk = 'objectId';
	   		var _conditions = ['module','type','status','unit','eduPhase','version','subject','grade','fascicule','title','creator','description','keywords','modifiedStartTime','modifiedEndTime','searchText','queryType','libType'];
	   		var _sortName = 'modified_time';
	   		var _sortOrder = 'desc';
			var _colums = [{field:'commonMetaData.commonMetaDatas.unit',title:'内容单元',width:fillsize(0.2),sortable:true},
							{field:'commonMetaData.commonMetaDatas.title',title:'资源标题',width:fillsize(0.17),sortable:true},
							{field:'commonMetaData.commonMetaDatas.creator',title:'制作者',width:fillsize(0.07),sortable:true},
							{field:'commonMetaData.commonMetaDatas.keywords',title:'关键字',width:fillsize(0.1),sortable:true},
							{field:'commonMetaData.commonMetaDatas.modified_time',title:'更新时间',width:fillsize(0.18),sortable:true},
							{field:'opt1',title:'操作',width:fillsize(0.35),align:'center',formatter:$operate}];
	   		
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		/***操作列***/
		$operate = function(value,rec){
			var optArray = new Array();
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:restoreRes('"+rec.objectId+"')\" ><i class=\"fa  fa-level-up\"></i> 恢复</a>");
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>");
			optArray.push("<a class=\"btn hover-blue\" href=\"javascript:deleteRecord('"+rec.objectId+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a>");
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:doDown('"+rec.objectId+"')\" ><i class=\"fa fa-download\"></i>下载</a>");
			return createOpt(optArray);
					
		};
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
			readFileOnline(paths);
		}
		
		/***添加***/
		function add(){
			upd('');
		}
		
		/***查看***/
		function detail(objectId){
			window.location.href = "${path}/bres/detail.action?libType=${param.libType}&objectId="+objectId;
		}
		
		/***修改***/
		function upd(objectId){
			window.location.href = "${path}/bres/edit.action?libType=${param.libType}&objectId="+objectId;
		}
		
		/***删除***/
		function del() {
			var codes = getChecked('data_div','objectId').join(',');
			if (codes == '') {
			    $.alert('请选择要删除的资源！');
			} else {
				deleteRecord(codes);
			}
		}
		function deleteRecord(ids){
			$.confirm('确定要删除所选数据吗？', function(){
				$.ajax({
					url:'delRes.action?ids=' + ids,
				    type: 'post',
				    datatype: 'text',
				    success: function (returnValue) {
				    	query();
				    }
				});
			});
		}
	
		function restoreRes(objectId){
			$.confirm('确定要恢复所选资源吗？', function(){
				$.ajax({
					url:'${path}/res/wf/restoreRes.action?objectId=' + objectId,
				    type: 'post',
				    datatype: 'text',
				    success: function (returnValue) {
				    	query();
				    }
				});
			});
		}
		function query(){
			$grid.query();
		}
		
		function importRes(){
			$.openWindow("${path}/bres/import/create.jsp",'批量导入',600,350);
		}
		function lookBatchResult(){
			window.location.href = '${path}/bres/import/list.jsp';
		}
		//下载资源
		function batchDown(){
			var codes = getChecked('data_div','objectId').join(',');
			if (codes == '') {
			    $.alert('请选择要下载的资源！');
			} else {
				doDown(codes);
			}
		}
		function doDown(codes){
			down4Encrypt('${path}/bres/downloadRes.action?ids='+codes);
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">下线资源管理</a>
					</li>
					<li class="active">资源列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
					<input type="hidden" id="module" name="module" value="${param.module }" />
					<input type="hidden" id="type" name="type" value="${param.type }" />
					<input type="hidden" id="status" name="status" value="${param.status }" />
					<input type="hidden" id="eduPhase" name="eduPhase" value="${param.eduPhase }" />
					<input type="hidden" id="version" name="version" value="${param.version }" />
					<input type="hidden" id="subject" name="subject" value="${param.subject }" />
					<input type="hidden" id="grade" name="grade" value="${param.grade }" />
					<input type="hidden" id="fascicule" name="fascicule" value="${param.fascicule }" />
					<input type="hidden" id="unit" name="unit" value="${param.unit }" />
					<input type="hidden" id="title" name="title" value="${param.title }" />
					<input type="hidden" id="creator" name="creator" value="${param.creator }" />
					<input type="hidden" id="description" name="description" value="${param.description }" />
					<input type="hidden" id="keywords" name="keywords" value="${param.keywords }" />
					<input type="hidden" id="modifiedStartTime" name="modifiedStartTime" value="${param.modifiedStartTime }" />
					<input type="hidden" id="modifiedEndTime" name="modifiedEndTime" value="${param.modifiedEndTime }" />
					
					<input type="hidden" id="searchText" name="searchText" value="${param.searchText }" />
					
					<input type="hidden" id="libType" name="libType" value="${param.libType }" />
					<input type="hidden" id="queryType" name="queryType" value="${param.queryType }" />
					<!-- 
					<input type="button" value="批量恢复" class="btn btn-primary" onclick="del()"/>
					<input type="button" value="批量下载" class="btn btn-primary" onclick="batchDown()"/>
					-->
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
</body>
</html>