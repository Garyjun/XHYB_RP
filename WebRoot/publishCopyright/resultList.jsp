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
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = '${path}/copyright/importResultList.action';
	   		var _pk = 'id';
	   		var _conditions = ['excelName','status'];
	   		var _sortName = 'importTime';
	   		var _sortOrder = 'desc';
	   		var _check=false;
			var _colums = [
			                {field:'excelName',title:'Excel名称',width:fillsize(0.2),sortable:true},
			               	{field:'statusDesc',title:'导入状态',width:fillsize(0.2),sortable:true},
			               	{field:'importTime',title:'创建时间',width:fillsize(0.2),sortable:true},
							{field:'opt1',title:'操作',width:fillsize(0.47),align:'center',formatter:$operate}];
			accountHeight();
	   		//$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	   		$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
		});
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<sec:authorize url="/copyright/downResult"><a class=\"btn hover-red\" href=\"javascript:downloadCopyrightResult('"+rec.id+"')\" ><i class=\"fa fa-sign-out\"></i>下载结果</a></sec:authorize>";
			opt += "<sec:authorize url="/copyright/deleteResult"><a class=\"btn hover-red\" href=\"javascript:del('"+rec.id+"')\" ><i class=\"fa fa-sign-out\"></i>删除</a></sec:authorize>";
			return opt;
					
		};
		function downloadCopyrightResult(id){
			window.location.href = "${path}/copyright/downloadCopyrightResult.action?id="+id;
		}
		function del(id){
			$.confirm('确定要删除所选数据吗？', function(){
				$.ajax({
					url:'delResult.action?id=' + id,
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
	</script>
</head>
<body data-spy="scroll">
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">批量导入版权结果</a>
					</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				 	<input type="button" value="返回" class="btn btn-primary red" onclick="parent.queryForm()"/>
					<div style="float: right;margin-bottom:10px;">
						<form action="#" class="form-inline no-btm-form" role="form">
							<div class="form-group">
							      <input type="text" class="form-control" id="excelName" name="excelName"   placeholder="输入Excel名称" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="reset" value="清除" class="btn btn-primary"/>
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
</body>
</html>