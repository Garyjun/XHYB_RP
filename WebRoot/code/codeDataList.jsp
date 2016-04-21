<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>编码管理</title>
        	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
			<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
			<script type="text/javascript" src="${path}/bres/res_operate.js"></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <script>
         var inDefName='${inDefName}';
         $(function(){
        	 $('#name').val(inDefName);
 	   		//定义一datagrid
 	   		var _divId = 'data_div';
 	   		var _url = '${path}/code/query.action';
 	   		var _pk = 'codeId';
 	   		var _conditions = ['adapterType','adapterName','codeStatus','name'];
 	   		var _sortName = 'codeId';
 	   		var _sortOrder = 'desc';
 	   		var _check=true;
 			var _colums = [ 
 						    {title:'应用集成方名称', field:'name' ,width:100, align:'center',sortable:true},
 						    {title:'编码类型', field:'adapterType' ,width:90, align:'center',formatter:$getType,sortable:true},
 						    {title:'集成方编码名称', field:'adapterName' ,width:90, align:'center',sortable:true},
 						    {title:'集成方编码', field:'adapterCode' ,width:75, align:'center',sortable:true},
 						    {title:'本系统名称', field:'codeName' ,width:90, align:'center',sortable:true},
 						    {title:'本系统编码', field:'codeDefault' ,width:90, align:'center',sortable:true},
  							{title:'状态', field:'codeStatus' ,width:75, align:'center', formatter:$getStatusDesc },
 							{title:'操作',field:'opt1',width:fillsize(0.18),align:'center',formatter:$operate}];
 			accountHeight();
 	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
 		});
         /***操作列***/
 		$operate = function(value,rec){
 			var opt = "";
 		    var viewUrl='<sec:authorize url="/user/view/*"><a class=\"btn hover-red\"  href="javascript:view('+rec.codeId+')"><i class=\"fa fa-sign-out\"></i>详细</a>&nbsp;</sec:authorize> ';		
		    var editUrl='<sec:authorize url="/user/update.action*"><a  class=\"btn hover-red\" href="javascript:upd('+rec.codeId+')"><i class=\"fa fa-edit\"></i>修改</a>&nbsp;</sec:authorize> ';	
		    var delUrl='<sec:authorize url="/user/delete/*"><a class=\"btn hover-red\" href="javascript:del('+rec.codeId+')"><i class=\"fa fa-trash-o\"></i>删除</a>&nbsp;</sec:authorize>';
 			opt=viewUrl+editUrl+delUrl;
 			return opt;
 					
 		};
 		
 		/**获取角色信息**/
 		$getRolesDesc = function(value,rec){
 		var rolesDesc = "";
 			$.ajax({
 				url:'${path}/user/getRolesDesc.action?id='+rec.id,
 				async: false,
 				success:function (data){
 					rolesDesc = data;
 				}
 			});
 			return rolesDesc;
 		};
 		
 		$getStatusDesc = function(value,rec){
 			var statusDesc = "";
 			if(rec.codeStatus==0)
 				statusDesc = "禁用";
 			else
 				statusDesc = "启用";
 			return statusDesc;
 		};
 		$getType = function(value,rec){
 			var type = "";
 			if(rec.adapterType=='00'){
 				type = "资源类型";
 			}else if(rec.adapterType=='01')
 			{
 				type = "分册";
 			}else if
 			(rec.adapterType=='02')
 			{
 				type = "学科";
 			}else if
 			(rec.adapterType=='03'){
 				type = "年级";
 			}else{
 				type = "学段";
 			}
 			return type;
 		};
 		
 		 function del(id){
 	 		var url='${path}/user/delete/'+id+'.action';
 	 		$.confirm('确定要删除该用户吗？', function(){
 				 window.location=url;
 			});
 	 	 }
 		 
        function batchDel(){
		 var ids=getChecked('data_div','id').join(',');
		 if(ids==''){
			 $.alert('请选择要删除的用户！');
			 return;
		 }
		 $.confirm('确定要删除所选数据吗？', function(){
			 window.location="${path}/user/batchDelete/"+ids+".action";
		});
	 }
        
		function disabled(id){
			window.location="${path}/user/batchDisabled/"+id+".action";
		} 
		
		function enabled(id){
			window.location="${path}/user/batchEnabled/"+id+".action";
		}
		
		function resetPassword(id){
			window.location="${path}/user/resetPassword.action?id=" + id;
		}
        
		 function batchDisabled(){
			 var ids=getChecked('data_div','id').join(',');
			 if(ids==''){
				 $.alert('请选择要禁用的用户！');
				 return;
			 }
			window.location="${path}/user/batchDisabled/"+ids+".action";
		 }
		 
		 function batchEnabled(){
			 var ids=getChecked('data_div','id').join(',');
			 if(ids==''){
				 $.alert('请选择要启用的用户！');
				 return;
			 }
			window.location="${path}/user/batchEnabled/"+ids+".action";
		 }
     function query(){
			$grid.query();
	}
     
     /***添加修改***/
	function edit(id){

			$.openWindow("${path}/code/upd.action?id="+id,'添加编码',800,500);

	}
	function upd(id){
		  	$.openWindow("${path}/code/upd.action?id="+id,'修改编码',600,500);

	}
	function view(codeId){
		$.openWindow("${path}/code/detail.action?codeId="+codeId,'详细',700,400);
	}
	/***删除***/
	function dels() {
		var codes = getChecked('data_div','codeId').join(',');
		if (codes == '') {
		    $.alert('请选择要删除的数据！');
		    return;
		} else { 
			$.confirm('确定要删除所选数据吗？', function(){
				window.location.href = "${path}/code/delete.action?ids="+codes;
		});
			
		};
	}			
	function del(id){
		$.confirm('确定要删除所选数据吗？', function(){
			window.location.href = "${path}/code/delete.action?libType=${param.libType}&ids="+id;	});
	}		

	$(function(){
		
		$('#adapterType').change(function(){
			query();
		});
		$('#codeStatus').change(function(){
			query();
		});
	});
</script>
    </head>
    <div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
		<input type="hidden" id="name" name="name" value=""/>
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            <a href="###">系统管理</a>
			        </li>
			        <li>
			            <a href="###">编码管理</a>
			        </li>
			        <li>
			            <a href="###">编码列表</a>
			        </li>
				</ul>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				   <!--  <a class="btn btn-primary" href="#" onclick="edit(-1)">新增</a>   -->
				     <sec:authorize url="/user/add.action">
				        <button  class="btn btn-primary" onclick="edit(-1)">添加</button>
				     </sec:authorize> 
				     <sec:authorize url="/user/delete/*"> 
				      <button  class="btn btn-primary" onclick="dels();">批量删除</button>
				     </sec:authorize>
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form">
							<div class="form-group">
							      <input type="text" class="form-control" id="adapterName" name="adapterName"  placeholder="输入集成方编码名称" qMatch="like"/>
							</div>
							<div class="form-group">
							      <select name="adapterType" id="adapterType" class="form-control">
							      	<option value="">字典类型</option>
									<option value="00">资源类型</option>
									<option value="01">分册</option>
									<option value="02">学科</option>
									<option value="03">年级</option>
									<option value="04">学段</option>
								</select>
							</div>
							<div class="form-group">
								<select name="codeStatus" id="codeStatus" class="form-control">
									<option value="">状态</option>
									<option value="1">启用</option>
									<option value="0">禁用</option>
								</select>
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="reset" value="清空" class="btn btn-primary"/>
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width:100%;"></div>
			</div>
		</div>
	</div>
</html>
