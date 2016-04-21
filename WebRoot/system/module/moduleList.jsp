<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>菜单管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
         <script>
         $(function(){
  	   		//定义一datagrid
  	   		var _divId = 'data_div';
  	   		var _url ='${path}/module/query.action';
  	   		var _pk = 'id';
  	   		var _conditions = ['moduleName'];
  	   		var _sortName = 'id';
  	   		var _sortOrder = 'desc';
  	   		var _check=true;
  			var _colums = [ 
  							{ title:'菜单名', field:'moduleName' ,width:100, align:'center' },
  						    { title:'url', field:'url' ,width:100, align:'center' },
  							{title:'操作',field:'opt1',width:fillsize(0.17),align:'center',formatter:$operate}
  							
  						];
  	   		
  			accountHeight();
  	   		$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
  		});
          /***操作列***/
   		$operate = function(value,rec){
   			var opt = "";
   		    var viewUrl='<sec:authorize url="/module/view"><a class=\"btn hover-red\"  href="javascript:view('+rec.id+')"><i class=\"fa fa-sign-out\"></i>详细</a>&nbsp;</sec:authorize> ';		
  		    var editUrl='<sec:authorize url="/module/update"><a  class=\"btn hover-red\" href="javascript:edit('+rec.id+')"><i class=\"fa fa-edit\"></i>修改</a>&nbsp;</sec:authorize> ';	
  		    var delUrl='<sec:authorize url="/module/delete/*"><a class=\"btn hover-red\" href="javascript:del('+rec.id+');"><i class=\"fa fa-trash-o\"></i>删除</a>&nbsp;</sec:authorize>';	
   			opt=viewUrl+editUrl+delUrl;
   			//opt=viewUrl;
   			return opt;
   					
   		};
   		
   	     function del(id){
	 		var url='${path}/module/delete/'+id+'.action';
	 		$.confirm('确定要删除该权限吗？', function(){
	 			$.get(url,function(data){
	 				if(data=="0"){
	 					query();
	 				}else{
	 					$.alert("该菜单有相关的权限和角色数据，不能删除！");
	 				}
	 			});
			});
	 	 }
   	 
        function batchDel(){
         var ids=getChecked('data_div','id').join(',');
		 if(ids==''){
			 $.alert('请选择要删除的菜单！');
			 return;
		 }
		 $.confirm('确定要删除所选数据吗？', function(){
			 $.get("${path}/module/batchDelete/"+ids+".action",function(data){
	 				if(data=="0"){
	 					query();
	 				}else{
	 					$.alert("该菜单有相关的权限和角色数据，不能删除！");
	 				}
			 });
		});
	
		 
	 }
     
    function query(){
	   $grid.query();
	}
     
     /***添加***/
	function edit(id){
	  $.openWindow("${path}/module/toEdit.action?id="+id,'编辑菜单',600,450);
	}
    
	  /***添加***/
	function view(id){
	  $.openWindow("${path}/module/view.action?id="+id,'查看菜单',600,350);
	}
	function formReset(){
 			$grid.clean();
//			$('#moduleId').val("");
//			$('#privilegeName').val("");
			$grid.query();
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
			            <a href="###">菜单管理</a>
			        </li>
			        <li>
			            <a href="###">菜单列表</a>
			        </li>
				</ul>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;height:34px">
					<sec:authorize url="/module/update">
					    <button  class="btn btn-primary" onclick="edit(-1)">添加</button>
					</sec:authorize>
					<sec:authorize url="/module/delete/*">
		                <button class="btn btn-primary"  onclick="batchDel();">批量删除</button>
					</sec:authorize>
					 <span>&nbsp;</span>
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" >
						  <div class="form-group">
								 <!--  <label for="moduleName" class="control-label" >登录名:</label> -->
							      <input type="text" class="form-control" id="moduleName" name="moduleName"  placeholder="输入菜单名" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="reset" value="清空" class="btn btn-primary" onclick="formReset();"/>
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
    </body>
</html>
