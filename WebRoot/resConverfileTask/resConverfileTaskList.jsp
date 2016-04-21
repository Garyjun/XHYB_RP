<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>转换管理</title>
        	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
			<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
			<script type="text/javascript" src="${path}/bres/res_operate.js"></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <script>
         $(function(){
        	 //?resId='+$('#resId').val()+'&doHasType='+$('#doHasType').val()+'&retryNum='+$('#retryNum').val()+'&status='+$('#status').val()+'&imgStauts='+$('#imgStauts').val()+'&imgDoStauts='+$('#imgDoStauts').val()+'&txtStauts='+$('#txtStauts').val()
 	   		//定义一datagrid
 	   		var _divId = 'data_div';
 	   		var _url = '${path}/resConver/query.action';
 	   		var _pk = 'id';
 	   		var _conditions = ['id','resId','srcPath','doHasType','doResultType','retryNum','status','imgStauts','imgDoStauts','txtStauts','txtDoStauts','txtStr','describes'];
 	   		var _sortName = 'id';
 	   		var _sortOrder = 'asc';
 	   		var _check=true;
 			var _colums = [ 
 						    //{title:'id', field:'id' ,width:30, align:'center',sortable:true},
 						    {title:'资源id', field:'resId' ,width:70, align:'center',sortable:true},
 						    {title:'资源文件路径', field:'srcPath' ,width:90, align:'center',sortable:true},
 						    {title:'转换后文件路径', field:'tarPath' ,width:75, align:'center',sortable:true},
 						    {title:'待处理', field:'doHasType' ,width:40, align:'center',sortable:true,formatter:$doHasType},
						    {title:'处理结果', field:'doResultType' ,width:50, align:'center',sortable:true,formatter:$doResultType},
  							{title:'重试次数', field:'retryNum' ,width:50, align:'center'},
  							{title:'转换状态', field:'status' ,width:50, align:'center',formatter:$status},
   							{title:'抽取封面状态', field:'imgStauts' ,width:50, align:'center',formatter:$imgStauts},
   							{title:'保存封面状态', field:'imgDoStauts' ,width:50, align:'center',formatter:$imgDoStauts},
  							{title:'抽取文本状态', field:'txtStauts' ,width:50, align:'center',formatter:$txtStauts},
   							{title:'保存文本状态', field:'txtDoStauts' ,width:50, align:'center',formatter:$txtDoStauts},
  							{title:'抽取文本内容', field:'txtStr' ,width:70, align:'center',formatter:$txtStr},
  							{title:'描述', field:'describes' ,width:80, align:'center'},
  							{field:'opt1',title:'操作',width:fillsize(0.03),align:'center',formatter:$operate}];
 			accountHeight();
 			$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
 		});
         /***操作列***/
 		$operate = function(value,rec){
 			var opt = "";
 			if(rec.doHasType!=rec.doResultType){
 				if(rec.flag!=parseInt("1")){
		   	 var editUrl='<sec:authorize url="/user/update.action*"><a  class=\"btn hover-red\" href="javascript:clickRetry('+"'"+rec.resId+"'"+')"><i class=\"fa fa-mail-reply-all\"></i>重试</a>&nbsp;</sec:authorize> ';
 			opt=editUrl;
 			}
 			}
 			return opt;
 		};
		$txtStr = function(value,rec){
 			var type = "";
 			if(rec.txtStr!=null){
 			if(rec.txtStr.length>70){
 				type = rec.txtStr.substring(0,70);
 			}else
 			{
 				type = rec.txtStr;
 			}
 			}
 			return type;
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
		$doHasType = function(value,rec){
 			var type = "";
 			if(rec.doHasType!=null){
 			if(rec.doHasType.indexOf('0') != -1){
 				type += "转换";
 			}
			
 			if(rec.doHasType.indexOf('1') != -1)
 			{
 				if(type != ""){
 					type += ",抽封面";
 				}else{
 					type += "抽封面";
 				}
 				
 			}
 			
 			if(rec.doHasType.indexOf('2') != -1)
 			{
 				if(type != ""){
 					type += ",抽文本";
 				}else{
 					type += "抽文本";
 				}
 			}
 			}
 			return type;
 		};
		$doResultType = function(value,rec){
			var type = "";
			if(rec.doResultType!=null){
 			if(rec.doResultType.indexOf('0') != -1){
 				type += "转换";
 			}
			
 			if(rec.doResultType.indexOf('1') != -1)
 			{
 				if(type != ""){
 					type += ",抽封面";
 				}else{
 					type += "抽封面";
 				}
 				
 			}
 			
 			if(rec.doResultType.indexOf('2') != -1)
 			{
 				if(type != ""){
 					type += ",抽文本";
 				}else{
 					type += "抽文本";
 				}
 			}
			}
			return type;
 		};
 		$txtDoStauts = function(value,rec){
 			var type = "";
 			if(rec.txtDoStauts!=null){
 			if(rec.txtDoStauts=='0'){
 				type = "待保存";
 			}else if(rec.txtDoStauts=='2')
 			{
 				type = "保存成功";
 			}else
 			{
 				type = "保存失败";
 			}
 			}
 			return type;
 		};
		$txtStauts = function(value,rec){
 			var type = "";
 			if(rec.txtStauts!=null){
 			if(rec.txtStauts=='0'){
 				type = "待抽取";
 			}else if(rec.txtStauts=='2')
 			{
 				type = "抽取成功";
 			}else
 			{
 				type = "抽取失败";
 			}
 			}
 			return type;
 		};
 		$imgDoStauts = function(value,rec){
 			var type = "";
 			if(rec.imgDoStauts!=null){
 			if(rec.imgDoStauts=='0'){
 				type = "待保存";
 			}else if(rec.imgDoStauts=='2')
 			{
 				type = "保存成功";
 			}else
 			{
 				type = "保存失败";
 			}
 			}
 			return type;
 		};
 		$imgStauts = function(value,rec){
 			var type = "";
 			if(rec.imgStauts!=null){
 			if(rec.imgStauts=='0'){
 				type = "待抽取";
 			}else if(rec.imgStauts=='2')
 			{
 				type = "抽取成功";
 			}else
 			{
 				type = "抽取失败";
 			}
 			}
 			return type;
 		};
 		$status = function(value,rec){
 			var type = "";
 			if(rec.status!=null){
 			if(rec.status=='0'){
 				type = "待转换";
 			}else if(rec.status=='2')
 			{
 				type = "转换成功";
 			}else
 			{
 				type = "转换失败";
 			}
 			}
 			return type;
 		};
 		
		function disabled(id){
			window.location="${path}/user/batchDisabled/"+id+".action";
		} 
		
		function enabled(id){
			window.location="${path}/user/batchEnabled/"+id+".action";
		}
		
		function resetPassword(id){
			window.location="${path}/user/resetPassword.action?id=" + id;
		}
		//change
		function testChange(){
			window.location="${path}/resConver/saveToResConverfileTask.action";
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
	//分配IP
	function allotHost(){
		$.openWindow("${path}/resConver/upd.action",'添加',600,400);
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
	//批量重试
	function batchRetry(){
		var ids = getChecked('data_div','resId').join(',');
		if(ids==''){
			$.alert('请选择要重试的数据！');
		}else{
		$.confirm('确定要重试吗？', function(){
			window.location.href = "${path}/resConver/batchRetry.action?&ids="+ids;});
	}
	}
	//重试
	function clickRetry(resId){
		//alert(resId);
		$.confirm('确定要重试吗？', function(){
			window.location.href = "${path}/resConver/batchRetry.action?&ids="+resId;});
	}
	//跨页全选
// 	function pageAll(){
// 		//alert(resId);
// 		$.confirm('确定要全部重试吗？', function(){
// 			window.location.href = "${path}/resConver/pageAll.action";
// 		});
		
// 	}
	function pageAll(){
		$.confirm('确定重试吗？', function(){
			var initWaiting = $.waitPanel('正在重试请稍后...',false);
			$.ajax({
				url:'${path}/resConver/pageAll.action',
			    type: 'post',
			    datatype: 'text',
			    success: function (returnValue) {
			    	query();
			    	initWaiting.close();
			    	if(returnValue==1){
			    			$.alert("重试成功");
			    		}else{
			    			$.alert("重试失败");
			    		}
			    	
			    	//data = eval('('+returnValue+')');
// 			    	if(data.status==1){
// 			    		$.alert("删除成功!");
// 			    		query();
// 			    	}else{
// 			    		$.alert("该资源已发布不能删除!");
// 			    	}
			    }
			});
		});
	}
// 	function doTaskCheckExitJL(){
// 		$.confirm('确定要检测吗？', function(){window.location.href = "${path}/resConver/doTaskCheckExitJL.action?ischeck=1"});
// 	}
	function doTaskCheckExitJL(ids){
		$.confirm('确定检测吗？', function(){
			var initWaiting = $.waitPanel('正在重试请稍后...',false);
			$.ajax({
				url:'${path}/resConver/doTaskCheckExitJL.action?ischeck=1',
			    type: 'post',
			    datatype: 'text',
			    success: function (returnValue) {
			    	query();
			    	initWaiting.close();
			    	$.alert(returnValue);
			    	//data = eval('('+returnValue+')');
// 			    	if(data.status==1){
// 			    		$.alert("删除成功!");
// 			    		query();
// 			    	}else{
// 			    		$.alert("该资源已发布不能删除!");
// 			    	}
			    }
			});
		});
	}
	$(function(){
		
		$('#doHasType').change(function(){
			query();
		});
		$('#retryNum').change(function(){
			query();
		});
		$('#status').change(function(){
			query();
		});
		$('#imgStauts').change(function(){
			query();
		});
		$('#imgDoStauts').change(function(){
			query();
		});
		$('#txtDoStauts').change(function(){
			query();
		});
		$('#txtStr').change(function(){
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
			            <a href="###">查询统计</a>
			        </li>
			        <li>
			            <a href="###">资源转换情况统计</a>
			        </li>
			        <li>
			            <a href="###">资源转换情况列表</a>
			        </li>
				</ul>
			</div>
			<div class="panel-body height_remain" id="999">
			<div align="left">
					<sec:authorize url="${path}/target/upd.action">
						<input class="btn btn-primary" type="button" value="批量重试" onclick="javascript:batchRetry();"/>
					</sec:authorize>
					<sec:authorize url="${path}/target/upd.action">
						<input class="btn btn-primary" type="button" value="转换主机" onclick="javascript:allotHost();"/>
					</sec:authorize>
					<sec:authorize url="${path}/target/upd.action">
						<input class="btn btn-primary" type="button" value="重试全部" onclick="javascript:pageAll();"/>
					</sec:authorize>
<%-- 					<sec:authorize url="${path}/resConver/doTaskCheckExitJL.action"> --%>
<!-- 						<input class="btn btn-primary" type="button" value="检测" onclick="javascript:doTaskCheckExitJL();"/> -->
<%-- 					</sec:authorize> --%>
			</div>
				<div style="margin: 0px 10px 10px 0px;">
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form">
 							<div class="form-group">
 							      <input type="text" class="form-control" id="id" name="id"  placeholder="输入id" size="4"/>
 							</div>
							<div class="form-group">
							      <input type="text" class="form-control" id="resId" name="resId"  placeholder="输入资源id" qMatch="like" size="27"/>
							</div>
							<div class="form-group">
							      <select name=doHasType id="doHasType" class="form-control" qType="int">
							      	<option value="">待处理&处理结果</option>
									<option value="11">相等</option>
									<option value="10">不相等</option>
									<option value="12">前者为空</option>
									<option value="13">后者为空</option>
								</select>
							</div>
							<div class="form-group">
							      <select name=retryNum id="retryNum" class="form-control" qType="int">
							      	<option value="">重试次数</option>
									<option value="0">0</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
								</select>
							</div>
							<div class="form-group">
							      <select name=status id="status" class="form-control" qType="int">
									<option value="">待转换</option>
									<option value="0">转换历史</option>
									<option value="2">转换成功</option>
									<option value="3">转换失败</option>
								  </select>
							</div>
							<div class="form-group">
							      <select name=imgStauts id="imgStauts" class="form-control" qType="int">
							      	<option value="">抽取封面状态</option>
									<option value="0">待抽取</option>
									<option value="2">抽取成功</option>
									<option value="3">抽取失败</option>
								  </select>
							</div>
							<div class="form-group">
							      <select name=imgDoStauts id="imgDoStauts" class="form-control" qType="int">
							      	<option value="">保存封面状态</option>
									<option value="2">保存成功</option>
									<option value="3">保存失败</option>
								  </select>
							</div>
							<div class="form-group">
							      <select name=txtDoStauts id="txtDoStauts" class="form-control" qType="int">
							      	<option value="">保存文本状态</option>
									<option value="2">保存成功</option>
									<option value="3">保存失败</option>
								  </select>
							</div>
							<div class="form-group">
								<select name="txtStr" id="txtStr" class="form-control" qType="int">
									<option value="">抽取文本内容</option>
									<option value="3">为空</option>
									<option value="2">不为空</option>
								</select>
							</div>
							<div class="form-group">
							      <input type="text" class="form-control" id="describes" name="describes"  placeholder="输入资源描述" qMatch="like" size="11"/>
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query();"/>
							<input type="reset" value="清空" class="btn btn-primary"/>
<!-- 							<input type="button" value="检测" class="btn btn-primary" onclick="testChange();"/> -->
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width:100%;"></div>
			</div>
		</div>
	</div>
</html>
