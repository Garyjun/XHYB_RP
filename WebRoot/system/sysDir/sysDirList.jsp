<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>图书目录</title>
        <style type="text/css">
		#resType{width: 100px;}
		</style>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
         <script>
         $(function(){
 	   		//定义一datagrid
 	   		var _divId = 'data_div';
 	   		var _url = '${path}/sysDir/query.action';
 	   		var _pk = 'id';
 	   		var _conditions = ['resType','dirCnName'];
 	   		var _sortName = 'id';
 	   		var _sortOrder = 'desc';
 	   		var _check=true;
 			var _colums = [ 
 							{ title:'资源类型', field:'resTypeNum' ,width:100, align:'center'},
 							{ title:'生命周期', field:'lifeCycleDesc' ,width:100, align:'center' },
 						    { title:'目录关键字', field:'dirEnName' ,width:100, align:'center' ,sortable:true},
 						    { title:'目录值', field:'dirCnName' ,width:100, align:'center' },
 						    { title:'状态', field:'statusDesc' ,width:100, align:'center' ,formatter:$statusDesc },
 						    { title:'目录下文件类型', field:'fileTypes' ,width:100, align:'center' },
 						    { title:'描述', field:'dirDesc' ,width:100, align:'center' },
 							{ title:'操作',field:'opt1',width:fillsize(0.17),align:'center',formatter:$operate }
 							
 						];
 	   		
 			accountHeight();
 			$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
 		});
         
        $statusDesc = function(value,rec){
        	if(rec.status==1){
        		return "启用";
        	}else{
        		return "禁用";
        	}
        };
        $statusDesc = function(value,rec){
        	if(rec.status==1){
        		return "启用";
        	}else{
        		return "禁用";
        	}
        };
        $res = function(value,rec){
        	alert(rec.resType);

        };
         /***操作列***/
 		$operate = function(value,rec){
 			var opt = "";
 		    var viewUrl='<sec:authorize url="/sysDir/view.action"><a class=\"btn hover-red\"  href="javascript:view('+rec.id+')"><i class=\"fa fa-sign-out\"></i>详细</a>&nbsp;</sec:authorize> ';		
		    var editUrl='<sec:authorize url="/sysDir/toEdit.action"><a  class=\"btn hover-red\" href="javascript:edit('+rec.id+')"><i class=\"fa fa-edit\"></i>修改</a>&nbsp;</sec:authorize> ';	
		    //var delUrl='<sec:authorize url="/user/delete.action*"><a class=\"btn hover-red\" href="${path}/sysDir/delete.action?id='+rec.id+'"><i class=\"fa fa-trash-o\"></i>删除</a>&nbsp;</sec:authorize>';
		    var delUrl='<sec:authorize url="/sysDir/delete.action"><a class=\"btn hover-red\" href="javascript:deleteRecord('+rec.id+')"><i class=\"fa fa-trash-o\"></i>删除</a>&nbsp;</sec:authorize>';	
 			opt=viewUrl+editUrl+delUrl;
 			return opt;
 					
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
					url:'${path}/sysDir/delete.action?id=' + ids,
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
					url:"${path}/sysDir/batchDelete/"+ids+".action",
				    datatype: 'text',
				    success: function () {
				    	freshDataTable('data_div');
				    }
				});
			});
		}
              
        /* function batchDel(){
		 var ids=getChecked('data_div','id').join(',');
		 if(ids==''){
			 alert('请选择要删除的元数据！');
			 return;
		 }
		 window.location="${path}/sysDir/batchDelete/"+ids+".action";
		 
	 } */
     function query(){
			$grid.query();
	}
     
     /***添加***/
	function edit(id){
    	 if(id>-1) {
    		 $.openWindow("${path}/sysDir/toEdit.action?id="+id,'编辑目录',700,600);
    	 } else if(id==-1) {
    		 $.openWindow("${path}/sysDir/toEdit.action?id="+id,'添加目录',700,600);
    	 }
	}
    
	  /***添加***/
	function view(id){
	  $.openWindow("${path}/sysDir/view.action?id="+id,'查看目录',700,600);
	}
				
	function formReset(){
		$grid.clean();
		$grid.query();
		//$('#queryForm')[0].reset();
//			query();
	}			
				

</script>
</head>
   <div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">系统管理</a>
					<li class="active">系统设置</li>
					<li class="active">资源目录</li>
					<li class="active">资源目录列表</li>
					</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				     <sec:authorize url="/sysDir/toEdit.action"><a class="btn btn-primary" onclick="edit(-1)">添加</a></sec:authorize> 
				     <sec:authorize url="/sysDir/batchDelete/*"> <a class="btn btn-primary" onclick="del();">批量删除</a></sec:authorize>
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form">
						  <%--  <div class="form-group">
							<div class="by-form-title">
							<label for="status" class="control-label">目录资源类型：</label>
					         <app:selectResType name="resType" id="resType"  headName="全部"  headValue="" selectedVal="${param.id}"  readonly="true"/>
							</div>
							</div> --%>
							<input type="hidden" name="resType" id="resType" value="${param.id}"/>
							<div class="form-group">
								  <label for="userName" class="control-label" >目录值:</label>
							      <input type="text" class="form-control" id="dirCnName" name="dirCnName" qMatch="like" placeholder="输入目录值" />
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
</html>
