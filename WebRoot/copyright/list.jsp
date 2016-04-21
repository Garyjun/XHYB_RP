<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<%@page import="com.brainsoon.system.support.SystemConstants.ResourceStatus"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
	<script type="text/javascript">
		$(function(){
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = 'list.action?type=${param.type}';
	   		var _pk = 'objectId';
	   		var _conditions = ['module','type','contractCode','crtPerson','authorizer','crtType','authArea','authStartDateBegin','authStartDateEnd','authEndDateBegin','authEndDateEnd','queryType'];
	   		var _sortName = 'modified_time';
	   		var _sortOrder = 'desc';
			var _colums = [	{field:'commonMetaData.commonMetaDatas.title',title:'资源标题',width:fillsize(0.17),align:'center',sortable:true},
			               	{field:'copyRightMetaData.copyRightMetaDatas.contractCode',title:'合同编号',width:fillsize(0.2),align:'center',sortable:true},
			               	{field:'copyRightMetaData.copyRightMetaDatas.crtPerson',title:'版权人',width:fillsize(0.2),align:'center',sortable:true},
			               	{field:'copyRightMetaData.copyRightMetaDatas.authorizer',title:'授权人',width:fillsize(0.2),align:'center',sortable:true},
			               	{field:'copyRightMetaData.copyRightMetaDatas.authStartDate',title:'授权开始时间',width:fillsize(0.2),align:'center',sortable:true},
							{field:'copyRightMetaData.copyRightMetaDatas.authEndDate',title:'授权结束时间',width:fillsize(0.2),align:'center',sortable:true},
							{field:'opt1',title:'操作',width:fillsize(0.47),align:'center',formatter:$operate}];
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			var status = rec.commonMetaData.commonMetaDatas.status;
			if(status==<%=ResourceStatus.STATUS0 %>||status==<%=ResourceStatus.STATUS5 %>){
			opt += "<sec:authorize url="/copyright/update"><a class=\"btn hover-red\" href=\"javascript:upd('"+rec.objectId+"')\" ><i class=\"fa fa-edit\"></i> 修改</a></sec:authorize>";
			}
			opt += "<sec:authorize url="/copyright/detail"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a></sec:authorize>";
		//	opt += "<a class=\"btn hover-blue\" href=\"javascript:deleteRecord('"+rec.objectId+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a>";
	//		opt += "<a class=\"btn hover-red\" href=\"javascript:doDown('"+rec.objectId+"')\" ><i class=\"fa fa-download\"></i> 下载</a>";
			return opt;
					
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
			readFileOnline(paths);//接口要改  预览需要获取文件path 和objectId
		}
		
		/***添加***/
		function add(){
			upd('');
		}
		
		/***查看***/
		function detail(objectId){
			var type = '${param.type}';
			if(type =='T06'){
				window.location.href = "${path}/bookRes/detail.action?module=${param.module}&isCopyright=1&type=${param.type}&objectId="+objectId;
			}else{
				window.location.href = "${path}/bres/detail.action?module=${param.module}&isCopyright=1&objectId="+objectId;
			}
		}
		
		/***修改***/
		function upd(objectId){
			var type = '${param.type}';
			if(type =='T06'){
				window.location.href = "${path}/bookRes/edit.action?module=${param.module}&type=${param.type}&isCopyright=1&objectId="+objectId;
			}else{
				window.location.href = "${path}/bres/edit.action?module=${param.module}&isCopyright=1&objectId="+objectId;
			}
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
					url:'${path}/bres/delRes.action?ids=' + ids,
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
		
		function importCopyright(){
			$.openWindow("${path}/copyright/import.jsp",'批量导入版权',600,200);
		}
		function lookBatchResult(){
			window.location.href = '${path}/bres/import/list.jsp';
		}
		function gotoCopyrightRepeat(){
			window.location.href = '${path}/copyright/repeatList.jsp';
		}
		function gotoCopyrightResult(){
			window.location.href = '${path}/copyright/resultList.jsp';
	//		$.openWindow("${path}/copyright/doRepeatRes.jsp",'批量导入',800,800);
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
					<li><a href="javascript:;">资源管理</a>
					</li>
					<li class="active">版权列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
					<input type="hidden" id="module" name="module" value="${param.module }"/>
					<input type="hidden" id="type" name="type" value="${param.type }" />
					<input type="hidden" id="contractCode" name="contractCode" value="${param.contractCode }" />
					<input type="hidden" id="crtPerson" name="crtPerson" value="${param.crtPerson }" />
					<input type="hidden" id="authorizer" name="authorizer" value="${param.authorizer }" />
					<input type="hidden" id="crtType" name="crtType" value="${param.crtType }" />
					<input type="hidden" id="authArea" name="authArea" value="${param.authArea }" />
					<input type="hidden" id="authStartDateBegin" name="authStartDateBegin" value="${param.authStartDateBegin }" />
					
					<input type="hidden" id="authStartDateEnd" name="authStartDateEnd" value="${param.authStartDateEnd }" />
					<input type="hidden" id="authEndDateBegin" name="authEndDateBegin" value="${param.authEndDateBegin }" />
					<input type="hidden" id="authEndDateEnd" name="authEndDateEnd" value="${param.authEndDateEnd }" />
					<input type="hidden" id="keywords" name="keywords" value="${param.keywords }" />
<%-- 					<input type="hidden" id="modifiedStartTime" name="modifiedStartTime" value="${param.modifiedStartTime }" /> --%>
<%-- 					<input type="hidden" id="modifiedEndTime" name="modifiedEndTime" value="${param.modifiedEndTime }" /> --%>
					
<%-- 					<input type="hidden" id="searchText" name="searchText" value="${param.searchText }" /> --%>
					
					<input type="hidden" id="queryType" name="queryType" value="${param.queryType }" />
<%-- 					<sec:authorize url="/copyright/import"> --%>
<!-- 					<input type="button" value="批量导入版权" class="btn btn-primary" onclick="importCopyright()"/> -->
<%-- 					</sec:authorize> --%>
<%-- 					<sec:authorize url="/copyright/resultList"> --%>
<!-- 					<input type="button" value="批量导入版权日志" class="btn btn-primary" onclick="gotoCopyrightResult()"/> -->
<%-- 					</sec:authorize> --%>
<%-- 					<sec:authorize url="/copyright/repeatList"> --%>
<!-- 					<input type="button" value="资源重复" class="btn btn-primary" onclick="gotoCopyrightRepeat()"/> -->
<%-- 					</sec:authorize> --%>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
</body>
</html>