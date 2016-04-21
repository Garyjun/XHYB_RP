<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
	<head>
		<title>系统参数管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
		<script type="text/javascript">
			$(function(){
	 	   		//定义一datagrid
	 	   		var _divId = 'data_div';
	 	   		var _url = '${path}/sysParameter/query.action';
	 	   		var _pk = 'id';
	 	   		var _conditions = ['paraKey','paraValue'];
	 	   		var _sortName = 'id';
	 	   		var _sortOrder = 'desc';
	 	   		var _check=true;
	 			var _colums = [ 
	 							{ title:'参数key', field:'paraKey' ,width:100, align:'center' ,sortable:true},
	 						    { title:'参数值', field:'paraValue' ,width:100, align:'center' },
	 						    { title:'类型', field:'paraType' ,width:100, align:'center',formatter:$typeDesc },
	 						    { title:'状态', field:'paraStatus' ,width:100, align:'center',formatter:$statusDesc },
	 						    { title:'描述', field:'paraDesc' ,width:100, align:'center' },
	 							{title:'操作',field:'opt1',width:fillsize(0.17),align:'center',formatter:$operate}
	 							
	 						];
	 	   		
	 			accountHeight();
	 			$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	 	     	
	 		});
			
			/***操作列***/
	 		$operate = function(value,rec){
	 			var opt = "";
	 		    var viewUrl='<sec:authorize url="/sysParameter/view.action"><a class=\"btn hover-red\"  href="javascript:view('+rec.id+')"><i class=\"fa fa-sign-out\"></i>详细</a>&nbsp;</sec:authorize> ';		
			    var editUrl='<sec:authorize url="/sysParameter/toEdit.action"><a  class=\"btn hover-red\" href="javascript:edit('+rec.id+')"><i class=\"fa fa-edit\"></i>修改</a>&nbsp;</sec:authorize> ';	
			    var delUrl='<sec:authorize url="/sysParameter/delete.action"><a class=\"btn hover-red\" href="javascript:deleteRecord('+rec.id+')"><i class=\"fa fa-trash-o\"></i>删除</a>&nbsp;</sec:authorize>';	
			   // var delUrl='<sec:authorize url="/role/delete/*"><a class=\"btn hover-red\" href="${path}/sysParameter/delete.action?id='+rec.id+'"><i class=\"fa fa-trash-o\"></i>删除</a>&nbsp;</sec:authorize>';	
			    opt=viewUrl+editUrl+delUrl;
	 			return opt;
	 					
	 		};
	 		
	 		$statusDesc = function(value,rec){
				if(rec.paraStatus==1)
					return "启用";
				else
					return "禁用";
	   		};
	 		
	 		$typeDesc = function(value,rec){
				if(rec.paraType==0)
					return "系统参数";
				else if(rec.paraType==1)
					return "配置参数";
				else if(rec.paraType==2)
					return "业务参数";
				else if(rec.paraType==3)
					return "其他";
	   		};
	   		
	   		
	   		/***删除***/
			 function del() {
				var codes = getChecked('data_div','id').join(',');
				if (codes == '') {
				    $.alert('请选择要删除的数据！');
				} else {
					batchDel(codes);
				};
			}
			function deleteRecord(ids){
				$.confirm('确定要删除所选数据吗？', function(){
					$.ajax({
						url:'${path}/sysParameter/delete.action?id=' + ids,
					    datatype: 'text',
					    success: function () {
					    	freshDataTable('data_div');
					    }
					});
				});
			}
			
			function batchDel(ids){
				$.confirm('确定要删除所选数据吗？', function(){
					$.ajax({
						url:"${path}/sysParameter/batchDelete/"+ids+".action",
					    datatype: 'text',
					    success: function () {
					    	freshDataTable('data_div');
					    }
					});
				});
			}
	   		 
	   		
	   		
			
		      /*  function batchDel(){
				 var ids=getChecked('data_div','id').join(',');
				 if(ids==''){
					 alert('请选择要删除的系统参数！');
					 return;
				 }
				 window.location="${path}/sysParameter/batchDelete/"+ids+".action";
				 
			 }  */
			 function query(){
					$grid.query();
			}
		     
		     /***添加***/
			function edit(id){
		    	 if(id==-1) {
		    		 $.openWindow("${path}/sysParameter/toEdit.action?id="+id,'添加系统参数',800,500);
		    	 } else if(id>-1) {
		    		 $.openWindow("${path}/sysParameter/toEdit.action?id="+id,'编辑系统参数',800,500);
		    	 }
			}
		    
			  /***查看***/
			function view(id){
			  $.openWindow("${path}/sysParameter/view.action?id="+id,'查看系统参数',800,500);
			}
			function formReset(){
				$grid.clean();
				$grid.query();
				//$('#queryForm')[0].reset();
//					query();
			}	
		</script>
	</head>
	<body>
		<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">系统管理</a></li>
					<li class="active">系统设置</li>
					<li class="active">系统参数</li>
					<li class="active">系统参数列表</li>
					
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				     <sec:authorize url="/sysParameter/toEdit.action"><a class="btn btn-primary" onclick="edit(-1)">添加</a></sec:authorize> 
				     <sec:authorize url="/sysParameter/batchDelete/*"> <a class="btn btn-primary" onclick="del();">批量删除</a></sec:authorize>
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form">
						   <div class="form-group">
							      <input type="text" class="form-control" id="paraKey" name="paraKey" qMatch="like" placeholder="输入参数key" />
							</div>
							<div class="form-group">
							      <input type="text" class="form-control" id="paraValue" name="paraValue" qMatch="like" placeholder="输入参数值" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="button" value="清空" class="btn btn-primary" onclick="formReset();"/>
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
	</body>
</html>