<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>单位管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <script type="text/javascript" src="${path}/appframe/main/js/libs/jquery.messager.js"></script>
        <script type="text/javascript">
         $(function(){
 	   		//定义一datagrid
 	   		var _divId = 'data_div';
 	   		var _url = '${path}/company/query.action';
 	   		var _pk = 'id';
 	   		var _conditions = ['name','companyType'];
 	   		var _sortName = 'id';
 	   		var _sortOrder = 'desc';
 	   		var _check=true;
 			var _colums = [ 
							//{ title:'id', field:'id' ,width:100, align:'center',hidden:true },
 							{ title:'单位名称', field:'name' ,width:120, align:'center' },
 						    { title:'单位地址', field:'address' ,width:150, align:'center' ,sortable:true},
 						    { title:'联系人1', field:'contactFirst' ,width:100, align:'center' ,sortable:true},
						    { title:'联系人1联系电话', field:'telephoneFirst' ,width:110, align:'center' ,sortable:true},
 						    { title:'联系人2', field:'contactSecond' ,width:100, align:'center'},
 						   	{ title:'联系人2联系电话', field:'telephoneSecond' ,width:110, align:'center' },
 						    { title:'单位类型', field:'companyType' ,width:100, align:'center' },
 							{title:'操作',field:'opt1',width:170,align:'center',formatter:$operate}
 							
 						];
 	   		
 			accountHeight();
 			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
 		});
         
         
         /***操作列***/
 		$operate = function(value,rec){
 			var optArray = new Array();
 		    var viewUrl = '<sec:authorize url="/company/view"><a class=\"btn hover-red\"  href="javascript:view('+rec.id+')"><i class=\"fa fa-sign-out\"></i>详细</a></sec:authorize>';		
		    var editUrl = '<sec:authorize url="/company/toEdit"><a  class=\"btn hover-red\" href="javascript:edit('+rec.id+')"><i class=\"fa fa-edit\"></i>修改</a></sec:authorize>';	
		    var delUrl = '<sec:authorize url="/company/delete"><a class=\"btn hover-red\" href="javascript:del('+rec.id+')"><i class=\"fa fa-trash-o\"></i>删除</a></sec:authorize>';
		    var setStatus = "";
		   	 optArray.push(viewUrl);
		     optArray.push(editUrl);
		     optArray.push(delUrl);
		    return createOpt(optArray);
 		};
 		
 		
 		 function del(id){
 			$.confirm('确定要删除该单位吗？', function(){
				$.ajax({
					url:'${path}/company/delete.action?id=' + id,
				    datatype: 'text',
				    success: function (data) {
				    	if(data == 'ok'){
				    		query();
					    	notice("提示信息", "删除成功！",4);
				    	}else{
				    		$.alert("删除失败");
				    	}
				    	
				    }
				});
			});
 	 	 }
 		 
 		//批量删除
        function batchDel(){
		 var ids=getChecked('data_div','id').join(',');
		 if(ids==''){
			 $.alert('请选择要删除的单位！');
			 return;
		 }
		 $.confirm('确定要删除所选数据吗？', function(){
			$.ajax({
				url:'${path}/company/batchDelete.action?ids='+ ids,
			    datatype: 'text',
			    success: function (data) {
			    	if(data == 'ok'){
			    		query();
				    	notice("提示信息", "批量删除成功！",4);
			    	}else{
			    		$.alert("批量删除失败");
			    	}
			    }
			});
		});
	 }
        
     function query(){
			$grid.query();
	}
     
     /***添加***/
	function edit(id){
    	 if(id>-1){
    		 $.openWindow("${path}/company/toEdit.action?id="+id,'修改单位信息',800,600);
    	 }else{
    		 $.openWindow("${path}/company/toEdit.action?id="+id,'添加单位信息',800,600);
    	 }
		
	}
    
	  /***详细***/
	function view(id){
		$.openWindow("${path}/company/view.action?id="+id,'单位详细信息',800,600);
	}
				
	function formReset(){
		$('#queryForm')[0].reset();
		$grid.query();
	}				
				
</script>
</head>
	<body>
    <div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            	系统管理
			        </li>
			        <li>
			            	系统设置
			        </li>
			        <li>
			            	单位列表
			        </li>
				</ul>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				<sec:authorize url="/company/toEdit">
				      <button  class="btn btn-primary" onclick="edit(-1)">添加</button>
				</sec:authorize>
				<sec:authorize url="/company/batchDelete"> 
				      <button  class="btn btn-primary" onclick="batchDel();">批量删除</button>
				</sec:authorize>
						<!--解决列表表上面的按钮与查询条件间隙问题 -->
				      <button  class="btn btn-primary" style="background:white;width:1px;">&nbsp;</button>	
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form" id="queryForm">
							<div class="form-group">
								<app:select name="companyType" indexTag="companyType" id="companyType"  selectedVal="" headName="请选择"  headValue=""  />
							</div>
						   <div class="form-group">
							      <input type="text" class="form-control" id="name" name="name" qMatch="like"  placeholder="输入单位名称" />
							</div>
							
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<button type="button" class="btn btn-primary" onclick="formReset();">清空</button>
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width:100%;"></div>
			</div>
		</div>
	</div>
	</body>
</html>
