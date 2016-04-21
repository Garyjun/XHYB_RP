<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
	<head>
		<title>加工任务管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
		<script type="text/javascript">
			$(function(){
	 	   		//定义一datagrid
	 	   		var _divId = 'data_div';
	 	   		var _url = '${path}/taskProcess/query.action';
	 	   		var _pk = 'id';
	 	   		var _conditions = ['taskName','createUser', 'startTime', 'endTime'];
	 	   		var _sortName = 'id';
	 	   		var _sortOrder = 'desc';
	 	   		var _check=true;
	 			var _colums = [
	 							{ title:'名称', field:'taskName' ,width:fillsize(0.15), align:'center' ,sortable:true},
	 						    { title:'批次', field:'batchNumber' ,width:fillsize(0.05), align:'center' },
	 						    { title:'数量', field:'processNumber' ,width:fillsize(0.05), align:'center'},
	 						    { title:'建议人数', field:'personNumber' ,width:fillsize(0.05), align:'center' },
	 						    { title:'预计开始时间', field:'startTime' ,width:fillsize(0.1), align:'center',formatter:$formatDate },
	 						    { title:'要求完成时间', field:'endTime' ,width:fillsize(0.1), align:'center',formatter:$formatDate },
	 						    { title:'创建人', field:'createUser.loginName' ,width:fillsize(0.1), align:'center' },
	 							{title:'操作',field:'opt1',width:fillsize(0.25),align:'center',formatter:$operate}

	 						];

	 			accountHeight();
	 			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);

	 		});

			/***操作列***/
	 		$operate = function(value,rec){
	 			var optArray = new Array();
	 		    var viewUrl='<sec:authorize url="/taskProcess/view.action"><a class=\"btn hover-red\"  href="javascript:view('+rec.id+')"><i class=\"fa fa-sign-out\"></i>详细</a></sec:authorize> ';
			    var editUrl='<sec:authorize url="/taskProcess/toEdit.action"><a  class=\"btn hover-red\" href="javascript:edit('+rec.id+')"><i class=\"fa fa-edit\"></i>修改</a></sec:authorize> ';
			    var delUrl='<sec:authorize url="/taskProcess/delete.action"><a class=\"btn hover-red\" href="javascript:deleteRecord('+rec.id+')"><i class=\"fa fa-trash-o\"></i>删除</a></sec:authorize>';
			    var asignResource='<sec:authorize url="/taskProcess/asignResource.action"><a class=\"btn hover-red\" href="javascript:addResource('+rec.id+')"><i class=\"glyphicon glyphicon-plus-sign\"></i>分配资源&加工员</a></sec:authorize>';
			    optArray.push(viewUrl);
			    optArray.push(editUrl);
			    optArray.push(delUrl);
			    optArray.push(asignResource);
			    //optArray.push(asignUser);
//			    optArray.push(viewResource);
	 			return createOpt(optArray);

	 		};

			//另外一种实现方式，暂时没有采用
/* 	 		$statusDesc = function(value,rec){
	 			return doMapStr('${status2Desc}',value);
	   		}; */

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
						url:'${path}/taskProcess/delete.action?id=' + ids,
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
						url:"${path}/taskProcess/batchDelete/"+ids+".action",
					    datatype: 'text',
					    success: function () {
					    	freshDataTable('data_div');
					    }
					});
				});
			}

			 function query(){
				$grid.query();
			}

		     /***添加、编辑***/
			function edit(id){
		    	 if(id==-1) {
		    		 $.openWindow("${path}/taskProcess/toEdit.action?id="+id,'添加加工任务',800,600);
		    	 } else if(id>-1) {
		    		 $.openWindow("${path}/taskProcess/toEdit.action?id="+id,'编辑加工任务',800,600);
		    	 }
			}

			  /***查看***/
			function view(id){
			  $.openWindow("${path}/taskProcess/view.action?id="+id,'查看加工任务',800,600);
			}
			  
			function addResource(id){
				location.href = "${path}/taskProcess/addResource.action?id="+id;
			}
			
/* 			function viewResource(id){
				location.href = "${path}/taskprocess/addResource.action?id="+id;
			} */
			
			function addMaker(id){
/* 				$("#taskProcessId").val(id);
				$.get("",function(data){
					$("#myModal").modal("show");
				}); */
				
				$.openWindow("${path}/taskProcess/addMakers.action?id="
						+id,'添加加工员',600,250);
			}

			function closeModal(){
				$("[name='makers']").removeAttr("checked");
				$("#taskProcessId").val("");
				$("#myModal").modal("hide");
			}
			
			function saveMaker(){
				$.post("${path}/taskProcess/saveMaker.action",{
					makers:			getMakers(),
					taskProcessId:	$("#taskProcessId").val()
				},function(data){
					$("[name='makers']").removeAttr("checked");
					$("#taskProcessId").val("");
					$("#myModal").modal("hide");					
					if(data!=-1)
						$.alert("分配成功！");
					else
						$.alert("分配失败");
				});
			}
			
			function getMakers(){
				var makers = "";
				 $("input[name='makers']").each(function(){
					 if($(this).attr("checked") == "checked"){
						 makers += $(this).val() + ",";
					 }
				 });
				 return makers;
			}
		</script>
	</head>
	<body>
		<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">加工管理</a></li>
					<li class="active">加工任务管理</li>
					<li class="active">加工任务列表</li>

				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				     <sec:authorize url="/taskProcess/toEdit.action"><a class="btn btn-primary" onclick="edit(-1)">添加</a></sec:authorize>
				     <sec:authorize url="/taskProcess/batchDeleteTaskProcess.action"> <a class="btn btn-primary" onclick="del();">批量删除</a></sec:authorize>
					<div style="float: right;">
						<!-- <form action="#" class="form-inline no-btm-form" role="form">
						   <div class="form-group">
							      <input type="text" class="form-control" id="taskName" name="taskName" placeholder="名称" />
							</div>
							<div class="form-group">
							      <input type="text" class="form-control" id="createUser.loginName" name="createUser.loginName" placeholder="创建人" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="reset" value="清空" class="btn btn-primary"/>
						</form> -->
						<!-- <form action="#" class="form-inline no-btm-form" role="form">
						   <div class="form-group">
							      <input placeholder=" 预计开始时间" class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'})" id="startTime" name="startTime" />
							</div>
							<div class="form-group">
							      <input placeholder="要求完成时间" class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startTime\')}'})" id="endTime" name="endTime" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="reset" value="清空" class="btn btn-primary"/>
						</form> -->
						
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title">分配加工员</h4>
	      </div>
	      <div class="modal-body">
	        	<c:forEach items="${makers}" var="maker" varStatus="status">
	        		<label class="checkbox-inline">
	        			<input type="checkbox" name="makers" value="${maker.id}"/>${maker.userName}
	        		</label>
	        		<c:if test="${status.index !=0 && status.index % 10 == 0}">
	        			<br />
	        		</c:if>
	        		<c:if test="${status.isLast()}">
	        			<br />
	        		</c:if>
	        	</c:forEach>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary" onclick="saveMaker();">保存</button>
	        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div>
	<input type="hidden" id="taskProcessId" value=""/>	
	<input type="hidden" id="taskName" name="taskName" value="${param.taskName }" />
	<input type="hidden" id="createUser" name="createUser" value="${param.createUser }" />
	</body>
</html>