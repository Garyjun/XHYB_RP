<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>角色管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript">
		$(function(){
 	   		//定义一datagrid
 	   		var _divId = 'data_div';
 	   		var _url = '${path}/role/query.action';
 	   		var _pk = 'id';
 	   		var _conditions = ['roleName'];
 	   		var _sortName = 'id';
 	   		var _sortOrder = 'desc';
 	   		var _check=true;
 			var _colums = [ 
 							{ title:'名称', field:'roleName' ,width:100, align:'center',sortable:true },
 						    { title:'描述', field:'description' ,width:100, align:'center' },
 						    { title:'状态', field:'statusDesc' ,width:100, align:'center' },
 							{title:'操作',field:'opt1',width:fillsize(0.17),align:'center',formatter:$operate}
 							
 						];
 	   		
 			accountHeight();
 			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
 	     	
 		});
		
		/***操作列***/
 		$operate = function(value,rec){
 			var opt = "";
 		    var viewUrl='<sec:authorize url="/role/view/*"><a class=\"btn hover-red\"  href="javascript:view('+rec.id+')"><i class=\"fa fa-sign-out\"></i>详细</a>&nbsp;</sec:authorize> ';		
		    var editUrl='<sec:authorize url="/role/update*"><a  class=\"btn hover-red\" href="javascript:edit('+rec.id+')"><i class=\"fa fa-edit\"></i>修改</a>&nbsp;</sec:authorize> ';	
		    var delUrl='<sec:authorize url="/role/delete/*"><a class=\"btn hover-red\" href="javascript:del('+rec.id+');"><i class=\"fa fa-trash-o\"></i>删除</a>&nbsp;</sec:authorize>';	
		    if(rec.status == 1)
		    	setStatus = '<sec:authorize url="/role/batchEabled.action"><a class=\"btn hover-red\" href="javascript:disabled('+rec.id+');"><i class=\"fa fa-lock\"></i>禁用</a>&nbsp;</sec:authorize>';
		    else
		    	setStatus = '<sec:authorize url="/role/batchEabled.action"><a class=\"btn hover-red\" href="javascript:enabled('+rec.id+');"><i class=\"fa fa-unlock\"></i>启用</a>&nbsp;</sec:authorize>';		    
 			if(rec.roleName == '加工员' || rec.roleName == '总库管理员'){
 				opt=viewUrl+editUrl;
 			}else{
 				opt=viewUrl+editUrl+setStatus+delUrl;
 			}
		    	
 			return opt;
 					
 		};
 		
 		 function del(id){
 	 		var url='${path}/role/delete/'+id+'.action';
 	 		$.confirm('确定要删除该角色吗？', function(){
	 			$.get(url,function(data){
	 				if(data=="0"){
	 					query();
	 				}else{
	 					$.alert("有用户正在使用该角色，不能删除！");
	 				}
	 			});
 			});
 	 	 }
 		 
		 function batchDel(){
			 var ids=getChecked('data_div','id').join(',');
			 if(ids==''){
				 $.alert('请选择要删除的角色！');
				 return;
			 }
			 $.confirm('确定要删除所选数据吗？', function(){
				 $.get("${path}/role/batchDelete/"+ids+".action",function(data){
		 				if(data=="0"){
		 					query();
		 				}else{
		 					$.alert("有用户正在使用该角色，不能删除！");
		 				}
				 });				 
			});
			 
		 }
		 function query(){
				$grid.query();
		}
	     
	     /***添加***/
		function edit(id){
		  //$.openWindow("${path}/role/toEdit.action?id="+id,'修改角色',800,500);
		  location.href = "${path}/role/toEdit.action?id="+id;
		}
	    
		  /***添加***/
		function view(id){
		  $.openWindow("${path}/role/view.action?id="+id,'查看角色',800,500);
		}
		 function query(){
				$grid.query();
		}
		 
		function disabled(id){
			window.location="${path}/role/batchDisabled/"+id+".action";
		} 
			
		function enabled(id){
			window.location="${path}/role/batchEnabled/"+id+".action";
		}
		
		 function batchDisabled(){
			 var ids=getChecked('data_div','id').join(',');
			 if(ids==''){
				 $.alert('请选择要禁用的角色！');
				 return;
			 }
			window.location="${path}/role/batchDisabled/"+ids+".action";
		 }
		 
		 function batchEnabled(){
			 var ids=getChecked('data_div','id').join(',');
			 if(ids==''){
				 $.alert('请选择要启用的角色！');
				 return;
			 }
			window.location="${path}/role/batchEnabled/"+ids+".action";
		 }		

			function formReset(){
				$grid.clean();
		//		$('#queryForm')[0].reset();
				query();
			}
		</script>
    </head>
   <body data-spy="scroll" >
     <div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            <a href="###">系统管理</a>
			        </li>
			        <li>
			            <a href="###">角色管理</a>
			        </li>
			        <li>
			            <a href="###">角色列表</a>
			        </li>
				</ul>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
					<sec:authorize url="/role/update*">
					    <button  class="btn btn-primary" onclick="edit(-1)">添加</button>
					</sec:authorize>
					<sec:authorize url="/role/delete/*">
		                <button class="btn btn-primary"  onclick="batchDel();">批量删除</button>
					</sec:authorize>
					 <button  class="btn btn-primary" style="background:white;width:1px;">&nbsp;</button>	
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form" id="queryForm">
						   <div class="form-group">
							      <input type="text" class="form-control" id="roleName" name="roleName"  placeholder="输入角色名" />
							</div>							
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<button type="button" class="btn btn-primary" onclick="formReset();">清空</button>
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
  </body>
</html>
