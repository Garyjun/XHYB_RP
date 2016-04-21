<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<%@ page import="com.brainsoon.resrelease.support.ResReleaseConstant"%>
<%@ page import="com.brainsoon.resrelease.support.ResReleaseConstant.OrderStatus"%>
<%@ page import="com.brainsoon.resrelease.support.SysParamsTemplateConstants"%>
<%@ page import="com.brainsoon.resrelease.support.SysParamsTemplateConstants.PublishType"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="${path}/resRelease/resOrder/resOrder_operate.js"></script>
	<script type="text/javascript">
	     $(function(){
	    	initQueryCond();
 	   		var _divId = 'data_div';
 	   		var _url = '${path}/resOrder/query.action';
 	   		var _pk = 'orderId';
 	   		var _conditions = [/*'publishType',*/'startDate','endDate','status','channelName','createUser'];
 	   		var _sortName = 'id';
 	   		var _sortOrder = 'desc';
 	   		var _check=true;
 	   		var _colums = [ 
				{ title:'下单日期', field:'orderDate' ,width:100, align:'center', formatter:$orderDateDesc, sortable:true},
				{ title:'客户名称', field:'channelName' ,width:100, align:'center'},
				{ title:'状态', field:'statusDesc' ,width:80, align:'center'},
				{ title:'资源类型', field:'resTypeDesc' ,width:90, align:'center'},
				{ title:'创建者', field:'createUser.userName' ,width:100, align:'center' },
				{ title:'创建时间', field:'createTime' ,width:120, align:'center',formatter:$dateDesc, sortable:true},
				{title:'操作',field:'opt1',width:fillsize(0.2),align:'center',formatter:$operate}
			];
 			accountHeight();
 	   		$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
 		});
	     $orderDateDesc = function(value,rec){
			var date = rec.orderDate;
			return date.substring(0,10);
		}
	     
	     $dateDesc = function(value,rec){
			var date = rec.createTime;
			return date.substring(0,10);
		}
	     
	    var platformId = '${platformId}';
			/***操作列***/
	 		$operate = function(value,rec){
	 			var posttype = $('#posttype').val();
	 			var opt = "";
	 		    var viewUrl= '<sec:authorize url="/resOrder/detail.action"><a class=\"btn hover-red\"  href="javascript:view('+rec.orderId+')"><i class=\"fa fa-sign-out\"></i>详细</a>&nbsp;</sec:authorize> ';		
			    var editUrl= '<sec:authorize url="/resOrder/add/edit.action"><a  class=\"btn hover-red\" href="javascript:edit('+rec.orderId+')"><i class=\"fa fa-edit\"></i>修改</a>&nbsp;</sec:authorize> ';	
			    var delUrl= '<sec:authorize url="/resOrder/delete.action"><a class=\"btn hover-red\" href="javascript:deleteRecord('+rec.orderId+')"><i class=\"fa fa-trash-o\"></i>删除</a>&nbsp;</sec:authorize>';
			    var toAuditrl = '<sec:authorize url="/resOrder/apply.action"><a  class=\"btn hover-red\" href="javascript:doApply('+rec.orderId+','+posttype+')"><i class=\"fa fa-mail-reply-all\"></i>上报</a>&nbsp;</sec:authorize> ';	
			    var auditUrl = '<sec:authorize url="/resOrder/check.action"><a  class=\"btn hover-red\" href="javascript:gotoCheck('+rec.orderId+','+rec.platformId+','+posttype+')"><i class=\"fa fa-check-square-o\"></i>审核</a>&nbsp;</sec:authorize>';
			    var publishUrl = '<sec:authorize url="/resOrder/publish.action"><a  class=\"btn hover-red\" href="javascript:publish('+rec.orderId+')"><i class=\"fa fa-send-o\"></i>加工发布</a>&nbsp;</sec:authorize> ';
			    var warningUrl = '<sec:authorize url="/resOrder/warnings.action"><a  class=\"btn hover-red\" href="javascript:warnings('+rec.orderId+')"><i class=\"fa fa-send-o\"></i>通知</a>&nbsp;</sec:authorize> ';
			    if(rec.status==<%=OrderStatus.CREATED %>){//未提交
			    	opt = viewUrl + editUrl + delUrl + toAuditrl;
			    }else
			    if(rec.status==<%=OrderStatus.TO_AUDIT %>){//待审核
			    	opt = viewUrl + auditUrl;
			    }else
			    if(rec.status==<%=OrderStatus.AUDITED %>){//审核通过
			    	opt = viewUrl + publishUrl;
			    }else
			    if(rec.status==<%=OrderStatus.PROCESSED %>){//加工成功
			    	opt = viewUrl;
			    }else
				if(rec.status==<%=OrderStatus.PROCESSEDWRONG %>){//加工失败
				    opt = viewUrl + publishUrl;
				}else
			    if(rec.status==<%=OrderStatus.PUBLISHED %>){//已发布
			    	opt = viewUrl;
			    }else
			    if(rec.status==<%=OrderStatus.AUDIT_REFUSE %>){//审核驳回
			    	opt= editUrl + viewUrl+delUrl;
			    }else 
				if(rec.status==<%=OrderStatus.WAITING_PROCESS %>){//待加工
					opt= viewUrl;
				}else 
			    if(rec.status==<%=OrderStatus.PROCESSING %>){//加工中
			    	opt= viewUrl;
			    }else 
			    if(rec.status==<%=OrderStatus.PUBLISHING %>){//发布中
			    	opt= viewUrl + warningUrl;
			    }else 
				if(rec.status==<%=OrderStatus.ORDERADD %>){//可追加
				    opt= viewUrl + editUrl + publishUrl;
				}
	 			return opt;
	 		};
	 		function warnings(ids){
	 			$.ajax({
	 				url: '${path}/resOrder/querywarnings.action?ids='+ids,
	 				success: function(data){
	 					if(data=="1"){
	 						$.confirm('确定要通知所选数据吗？', function(){
		 						$.ajax({
		 			 				url: '${path}/resOrder/warnings.action?ids='+ids,
		 			 				success: function(data){
		 			 					if(data=="1"){
		 			 						$.alert("通知成功!");
			 			 					location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
		 			 					}else{
		 			 						$.alert("通知失败!请检查");
			 			 					location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
		 			 					}
		 			 				}
		 			 			});
	 						});
	 					}else{
	 						$.alert("请选择线上发布的需求单!");
	 					}
	 				}
	 			});
	 			
	 		}
	 		function batchwarnings(){
	 			var ids = getChecked('data_div','orderId').join(',');
	 			if (ids == '') {
					$.alert('请选择需要通知的需求单！');
					return;
				}else{
					var canDel = true;
				 	var rows = $('#data_div').datagrid('getChecked');
				 	for ( var i = 0; i < rows.length; i++) {
				 		var status = eval('rows[i]' + '.' + 'status');
				 		if(status == '9'){
				 			continue;
				 		}else{
				 			$.alert('只能通知发布中的需求单！');
				 			canDel = false;
				 			break;
				 		}
				 	}
				 	if(canDel){
				 		warnings(ids);
				 	}
				}
	 		}
	 		function publish(ids){
	 			var _url = "${path}/resRelease/batchProcess.action?orderIds=" + ids+"&posttype="+$('#posttype').val();
	 			//$.alert("资源加工已经触发，请稍后！");
	 			$.ajax({
	 				url: _url,
	 				success: function(data){
	 					$.alert(data);
	 					//$grid.query();
	 					//window.location.href = "${path}/resOrder/view.action?id="+id;
	 					//location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
	 					refreshResOrderList();
	 				}
	 			});
	 		}
	 		
	 		$statusDesc = function(value,rec){
				if(rec.status==<%=OrderStatus.CREATED %>)
					return "未提交";
				else if(rec.status==<%=OrderStatus.TO_AUDIT %>)
					return "待审核";
				else if(rec.status==<%=OrderStatus.AUDITED %>)
					return "审核通过";
				else if(rec.status==<%=OrderStatus.AUDIT_REFUSE %>)
					return "审核驳回";
				else if(rec.status==<%=OrderStatus.WAITING_PROCESS %>)
					return  "待加工";
				else if(rec.status==<%=OrderStatus.PROCESSING %>)
					return  "加工中";
				else if(rec.status==<%=OrderStatus.PROCESSED %>)
					return "加工成功";
				else if(rec.status==<%=OrderStatus.PROCESSEDWRONG %>)
					return "加工失败";
				else if(rec.status==<%=OrderStatus.WAITING_PUBLISH %>)
					return "待发布";
				else if(rec.status==<%=OrderStatus.PUBLISHING %>)
					return "发布中";
				else if(rec.status==<%=OrderStatus.PUBLISHED %>)
					return "已发布";

	   		};
	 		
			function query(){
				$("#startDate").val($('#modifiedStartTime').val());
		    	$("#endDate").val($('#modifiedEndTime').val());
				$grid.query();
			}
		     
		     /***添加***/
			function edit(id){
		    	var posttype=$('#posttype').val();
			  	window.location.href = "${path}/resOrder/toEdit.action?id="+id +"&posttype="+posttype;
			}
		     
		  	/***详细***/
			function view(id){
				var posttype=$('#posttype').val();
			  	window.location.href = "${path}/resOrder/view.action?id="+id +"&posttype="+posttype;
			}
			  
			function batchDel(){
				var ids = getChecked('data_div','orderId').join(',');
				if (ids == '') {
					$.alert('请选择要删除的需求单！');
					return;
				} else {
					var canDel = true;
				 	var rows = $('#data_div').datagrid('getChecked');
				 	for ( var i = 0; i < rows.length; i++) {
				 		var status = eval('rows[i]' + '.' + 'status');
				 		if(status == '0'||status == '3'){
				 			continue;
				 		}else{
				 			$.alert('只能删除未提交或审核驳回状态的需求单！');
				 			canDel = false;
				 			break;
				 		}
				 	}
				 	if(canDel){
				 		deleteRecord(ids);
				 	}
				}
			 }
			
			function deleteRecord(ids){
				$.confirm('确定要删除所选数据吗？', function(){
					$.post('${path}/resOrder/batchDeleteResOrder.action?ids=' + ids+'&posttype='+$('#posttype').val(),function(data){
						if(data == "0"){
							$.alert('删除失败');
						}else{
							$.alert('删除成功');
						}
						location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
					});
				});
			}
			
			function batchApply(){
				var posttype=$('#posttype').val();
				var ids = getChecked('data_div','orderId').join(',');
				if (ids == '') {
					$.alert('请选择要上报的需求单！');
				} else {
					var canApply = true;
				 	var rows = $('#data_div').datagrid('getChecked');
				 	for ( var i = 0; i < rows.length; i++) {
				 		var status = eval('rows[i]' + '.' + 'status');
				 		if(status == '0'||status == '3'){
				 			continue;
				 		}else{
				 			$.alert('只能上报未提交和驳回状态的需求单！');
				 			canApply = false;
				 			break;
				 		}
				 	}
				 	if(canApply){
					 	doApply(ids,posttype);
				 	}
				}
			}
			
			function batchCheck(){
				var ids = getChecked('data_div','orderId').join(',');
				if (ids == '') {
					$.alert('请选择要审核的需求单！');
				} else {
					var canCheck = true;
				 	var rows = $('#data_div').datagrid('getChecked');
				 	for ( var i = 0; i < rows.length; i++) {
				 		var status = eval('rows[i]' + '.' + 'status');
				 		if(status == '1'){
				 			continue;
				 		}else{
				 			$.alert('只能审核待审核状态的需求单！');
				 			canCheck = false;
				 			break;
				 		}
				 	}
				 	if(canCheck){
					 	$("#versionText").modal('show');
				 	}
				}
			}
			
			//查看发布日志
			function publishLog(id){
				location.href = "${path}/resOrder/publishLog.action?id="+id;
			}
			
			
			function batchProcess(){
				var ids = getChecked('data_div','orderId').join(',');
				if (ids == '') {
					$.alert('请选择要加工的需求单！');
				} else {
					$.ajax({
						url: "${path}/resRelease/canBatchProcess.action?orderIds=" + ids,
						type: "get",
						success: function(data){
							if(data==0){
								$.alert("只能加工审核通过或加工失败的需求单！");
								return;
							}else if(data==1){
								$.confirm('请确定是否加工！', function(){
									$.ajax({
										url:'${path}/resRelease/batchProcess.action?orderIds=' + ids+'&posttype='+$('#posttype').val(),
									    type: 'get',
									    datatype: 'text',
									    beforeSend: function(XMLHttpRequest){
									    	$.alert("资源加工已经触发，请稍后！");
									    },
									    success: function (returnValue) {
											//$grid.query();
									    	location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
									    }
									});
								});
							}
						}
					});
				}
			}
			
			function batchUpdateStatus(){
				var ids = getChecked('data_div','orderId').join(',');
				if (ids == '') {
					$.alert('请选择要审核的需求单！');
				} else {
					$.confirm('请确定是否要修改需求单状态！', function(){
						$.ajax({
							url:'${path}/resOrder/updateStatus.action?orderIds=' + ids,
						    type: 'get',
						    datatype: 'text',
						    success: function () {
								$grid.query();
						    }
						});
					});
				}
			}
			
			
			function updateStatus(id){
				$.confirm('请确定是否要修改需求单状态！', function(){
					$.ajax({
						url:'${path}/resOrder/updateStatus.action?orderIds=' + id,
					    type: 'get',
					    datatype: 'text',
					    success: function () {
							$grid.query();
					    }
					});
				});
			}
			
			function refreshResOrderList(){
				location.href = "${path}/resOrder/list.action";
			}
			
			function initQueryCond(){
				var name = $("#channelName", window.parent.document);
			 	var startDate = $("#modifiedStartTime", window.parent.document);
				var endDate = $("#modifiedEndTime", window.parent.document);
				var status = $("#status", window.parent.document);
				var createUser = $("#createUser", window.parent.document);
				var publishType = $("#publishType", window.parent.document);
				 $('#startDate').val(startDate.val());
				 $('#endDate').val(endDate.val());
				 $('#status').val(status.val()); 
				 $('#channelName').val(name.val())
				 $('#createUser').val(createUser.val());
				 $('#publishType').val(publishType.val());
			}
			
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><span>资源发布</span></li>
					<li class="active">需求单</li>
					<li class="active">需求单列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
				     <sec:authorize url="/resOrder/add/edit.action">
				     	<input type="button" value="添加" class="btn btn-primary" onclick="edit(-1)"/>
				   	</sec:authorize> 
				     <sec:authorize url="/resOrder/apply.action">
				     	<input type="button" value="批量上报" class="btn btn-primary" onclick="batchApply()"/>
				   	</sec:authorize> 
				     <sec:authorize url="/resOrder/check.action"> 
				     	<input type="button" value="批量审核" class="btn btn-primary" onclick="batchCheck()"/>
				     </sec:authorize>
				     <sec:authorize url="/resOrder/delete.action"> 
				     	<input type="button" value="批量删除" class="btn btn-primary" onclick="batchDel()"/>
				     </sec:authorize>
				     
				     <sec:authorize url="/resOrder/publish.action"> 
				     	<input type="button" value="批量加工发布" class="btn btn-primary" onclick="batchProcess()"/>
				     </sec:authorize>
				     <sec:authorize url="/resOrder/warnings.action"> 
				     	<input type="button" value="批量通知" class="btn btn-primary" onclick="batchwarnings()"/>
				     </sec:authorize>
				     
				     <%-- <sec:authorize url="/resOrder/updateStatus.action"> 
				     	<input type="button" value="批量修改状态" class="btn btn-primary" onclick="batchUpdateStatus()"/>
				     </sec:authorize>  --%>
					<%-- <div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form">
						   <div class="form-group">
							      <input placeholder=" 开始时间" class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})" id="modifiedStartTime" name="modifiedStartTime" />
							</div>
							<div class="form-group">
							      <input placeholder="结束时间" class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})" id="modifiedEndTime" name="modifiedEndTime" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="reset" value="清空" class="btn btn-primary"/>
						</form>
						<input type="hidden" id="startDate" name="startDate" value="${param.startDate }" />
						<input type="hidden" id="endDate" name="endDate" value="${param.endDate }" />
						<input type="hidden" id="status" name="status" value="${param.status }" />
						<input type="hidden" id="createUser" name="createUser" value="${param.createUser }" />
						<input type="hidden" id="channelName" name="channelName" value="${param.channelName }" />
					</div> --%>
					<input type="hidden" id="startDate" name="startDate" value="<%=request.getParameter("startDate") %>" />
					<input type="hidden" id="endDate" name="endDate" value="<%=request.getParameter("startDate") %>" />
					<input type="hidden" id="status" name="status" value="<%=request.getParameter("startDate") %>" />
					<input type="hidden" id="createUser" name="createUser" value="<%=request.getParameter("startDate") %>" />
					<input type="hidden" id="channelName" name="channelName" value="<%=request.getParameter("startDate") %>" />
					<input type="hidden" id="publishType" name="publishType" value="<%=request.getParameter("publishType") %>" />
					<input type="hidden" id="posttype" name="posttype" value="1" />
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
				<%@ include file="/resRelease/resOrder/checkWindow.jsp" %>
			</div>				
		</div>
	</div>
</body>
</html>