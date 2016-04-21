<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<%@ page import="com.brainsoon.resrelease.support.ResReleaseConstant"%>
<%@ page import="com.brainsoon.resrelease.support.ResReleaseConstant.OrderStatus"%>
<%@ page import="com.brainsoon.resrelease.support.SysParamsTemplateConstants"%>
<%@ page import="com.brainsoon.resrelease.support.SysParamsTemplateConstants.PublishType"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>专题列表</title>
<script type="text/javascript">
		$(function(){
			var _divId = 'data_div';
			var	_url = '${path}/subject/queryList.action';
			var _pk = 'id';
			var _conditions = ['name','status','storeType','subject','trade','collectionStart','collectionEnd'];
			var _sortName = 'id';
			var _sortOrder = 'desc';
			var _check=true;
			var _colums = [
					{field:'name',title:'主题库名称',width:fillsize(0.10),align:'center'},
					{field:'tradeDesc',title:'行业',width:fillsize(0.10),align:'center'},
					{field:'languageDesc',title:'语言',width:fillsize(0.10),align:'center'},
					{field:'audienceDesc',title:'受众类别',width:fillsize(0.10),align:'center'},
					{field:'statusDesc',title:'状态',width:fillsize(0.10),align:'center'},
					{field:'collectionStart',title:'收录开始年限',width:fillsize(0.10),align:'center'},
					{field:'collectionEnd',title:'收录结束年限',width:fillsize(0.10),align:'center'},
					{field:'createUser.userName',title:'创建人',width:fillsize(0.10),align:'center'},
					{field:'opt1',title:'操作',width:fillsize(0.2),align:'center',formatter:$operate}];
			accountHeight();
			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
		});
		/***操作列***/
 		$operate = function(value,rec){
 			var posttype = $('#posttype').val();
 			var opt='';
 		    var viewUrl='<sec:authorize url="/subject/view.action"><a class=\"btn hover-red\"  href="javascript:view('+rec.id+')"><i class=\"fa fa-sign-out\"></i>详细</a></sec:authorize> ';
		    var editUrl='<sec:authorize url="/subject/upd.action"><a  class=\"btn hover-red\" href="javascript:upd('+rec.id+')"><i class=\"fa fa-edit\"></i>修改</a></sec:authorize> ';
		    var delUrl='<sec:authorize url="/subject/del.action"><a class=\"btn hover-red\" href="javascript:del('+rec.id+')"><i class=\"fa fa-trash-o\"></i>删除</a></sec:authorize>';
		    var toAuditrl = '<sec:authorize url="/subject/apply.action"><a  class=\"btn hover-red\" href="javascript:doApply('+rec.id+','+posttype+')"><i class=\"fa fa-mail-reply-all\"></i>上报</a>&nbsp;</sec:authorize> ';	
		    var auditUrl = '<sec:authorize url="/subject/check.action"><a  class=\"btn hover-red\" href="javascript:gotoCheck('+rec.id+','+rec.platformId+','+posttype+')"><i class=\"fa fa-check-square-o\"></i>审核</a>&nbsp;</sec:authorize>';
		    var publishUrl = '<sec:authorize url="/subject/publish.action"><a  class=\"btn hover-red\" href="javascript:publish('+rec.id+')"><i class=\"fa fa-send-o\"></i>加工发布</a>&nbsp;</sec:authorize> ';
		    var warningUrl = '<sec:authorize url="/subject/warnings.action"><a  class=\"btn hover-red\" href="javascript:warnings('+rec.id+')"><i class=\"fa fa-send-o\"></i>通知</a>&nbsp;</sec:authorize> ';
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
		    }
		    
 			return opt;
 		};
 		function warnings(ids){
			$.confirm('确定要通知所选数据吗？', function(){
				$.ajax({
	 				url: '${path}/subject/warnings.action?ids='+ids,
	 				success: function(data){
	 					if(data=="1"){
	 						$.alert("通知成功!");
		 					location.href = "${path}/subject/subjectList.jsp";
	 					}else{
	 						$.alert("通知失败!请检查");
		 					location.href = "${path}/subject/subjectList.jsp";
	 					}
	 				}
		 		});
			});
 		}
 		function batchwarnings(){
 			var ids = getChecked('data_div','id').join(',');
 			if (ids == '') {
				$.alert('请选择需要通知的主题库！');
				return;
			}else{
				var canDel = true;
			 	var rows = $('#data_div').datagrid('getChecked');
			 	for ( var i = 0; i < rows.length; i++) {
			 		var status = eval('rows[i]' + '.' + 'status');
			 		if(status == '9'){
			 			continue;
			 		}else{
			 			$.alert('只能通知发布中的主题库！');
			 			canDel = false;
			 			break;
			 		}
			 	}
			 	if(canDel){
			 		warnings(ids);
			 	}
			}
 		}
 		
 		function view(id){
 			this.location.href="${path}/subject/subjectQuery.action?id="+id+"&posttype="+$('#posttype').val();
 		}
 		function add(){
 			upd(-1);
 		}
 		function upd(id){
 			if(id==-1){
 				this.location.href="${path}/subject/subjectAdd.action?id="+id+"&posttype="+$('#posttype').val();
 			}else{
 				this.location.href="${path}/subject/subjectEdit.action?id="+id+"&posttype="+$('#posttype').val();
 			}
 		}
 		//批量删除
 		function batchDel(){
			var ids = getChecked('data_div','id').join(',');
			if (ids == '') {
				$.alert('请选择要删除的主题库！');
				return;
			} else {
				var canDel = true;
			 	var rows = $('#data_div').datagrid('getChecked');
			 	for ( var i = 0; i < rows.length; i++) {
			 		var status = eval('rows[i]' + '.' + 'status');
			 		if(status == '0'||status == '3'){
			 			continue;
			 		}else{
			 			$.alert('只能删除未提交或审核驳回状态的主题库！');
			 			canDel = false;
			 			break;
			 		}
			 	}
			 	if(canDel){
			 		del(ids);
			 	}
			}
		 }
 		
 		function del(id){
			$.confirm('确定要删除所选数据吗？', function(){
				$.post('${path}/subject/batchDeletesubject.action?ids=' + id+'&posttype='+$('#posttype').val(),function(data){
					if(data == "0"){
						$.alert('删除失败');
					}else{
						$.alert('删除成功');
					}
					location.href = "${path}/subject/subjectList.jsp";
				});
			});
 		}
 		/*
 		批量上报
 		*/
 		function batchApply(){
			var posttype=$('#posttype').val();
			var ids = getChecked('data_div','id').join(',');
			if (ids == '') {
				$.alert('请选择要上报的主题库！');
			} else {
				var canApply = true;
			 	var rows = $('#data_div').datagrid('getChecked');
			 	for ( var i = 0; i < rows.length; i++) {
			 		var status = eval('rows[i]' + '.' + 'status');
			 		if(status == '0'||status == '3'){
			 			continue;
			 		}else{
			 			$.alert('只能上报未提交和驳回状态的主题库！');
			 			canApply = false;
			 			break;
			 		}
			 	}
			 	if(canApply){
				 	doApply(ids,posttype);
			 	}
			}
		}
 		/**
 		 * 资源：上报
 		 * @param objectId
 		 */
 		function doApply(objectId,posttype) {
 			$.ajax({
 				url : _appPath +"/resOrder/canApply.action?orderId="+objectId+"&posttype="+posttype,
 				type : "post",
 				datatype : "text",
 				success : function(data){
 					if(data=="N"){
 						$.alert("该主题库没有添加资源，无法上报！");
 					}else{
 						$.confirm('确定要上报吗？', function() {
 							var _url = _appPath + '/subject/doApply.action?objectId=' + objectId +"&posttype="+posttype;
 							$.ajax({
 								url : _url,
 								type : 'post',
 								datatype : 'text',
 								success : function(returnValue) {
 									$.alert(returnValue);
 									$grid.query();
 								}
 							});
 						});
 					}
 				}
 				
 			});
 		}
 		/*
 		批量审核
 		*/
 		function batchCheck(){
			var ids = getChecked('data_div','id').join(',');
			if (ids == '') {
				$.alert('请选择要审核的主题库！');
			} else {
				var canCheck = true;
			 	var rows = $('#data_div').datagrid('getChecked');
			 	for ( var i = 0; i < rows.length; i++) {
			 		var status = eval('rows[i]' + '.' + 'status');
			 		if(status == '1'){
			 			continue;
			 		}else{
			 			$.alert('只能审核待审核状态的主题库！');
			 			canCheck = false;
			 			break;
			 		}
			 	}
			 	if(canCheck){
				 	$("#versionText").modal('show');
			 	}
			}
		}
 		
 		/**
 		 * 资源：审核
 		 */
 		function gotoCheck(objectId,platformId,posttype) {
 			var _url=  _appPath + '/subject/gotoCheck.action?objectId='+ objectId + '&operateFrom=MANAGE_PAGE&posttype='+posttype;
 			window.location.href = _url;
 		}
 		//加工
 		function publish(ids){
 			var _url = "${path}/resRelease/batchProcess.action?orderIds=" + ids+"&posttype="+$('#posttype').val();
 			$.alert("资源加工已经触发，请稍后！");
 			$.ajax({
 				url: _url,
 				type: 'get',
 				async: false,
 				success: function(){
 					location.href = "${path}/subject/subjectList.action";
 				}
 			});
 		}
 		//批量加工
 		function batchOneCheck(){
			var ids = getChecked('data_div','id').join(',');
			if (ids == '') {
				$.alert('请选择要加工的主题库！');
			} else {
				$.ajax({
					url: "${path}/subject/canBatchProcess.action?orderIds=" + ids,
					type: "get",
					success: function(data){
						if(data==0){
							$.alert("只能加工审核通过或加工失败的主题库！");
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
								    	location.href = "${path}/subject/subjectList.jsp";
								    }
								});
							});
						}
					}
				});
			}
		}
</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
<form action="#" id="coreData" class="form-horizontal" method="post">
	<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li class="active">资源发布</li>
					<li class="active">主题库</li>
					<li class="active">主题库列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
					<sec:authorize url='/subject/subjectAdd.action'>
					<input type="button" value="添加" class="btn btn-primary" onclick="add()"/>  
					</sec:authorize>
					<sec:authorize url='/subject/del.action'>
					<input type="button" value="批量删除" class="btn btn-primary" onclick="batchDel()"/>
					</sec:authorize>
					<sec:authorize url='/subject/apply.action'>
					<input type="button" value="批量上报" class="btn btn-primary" onclick="batchApply()"/>
					</sec:authorize>
					<sec:authorize url='/subject/check.action'>
					<input type="button" value="批量审核" class="btn btn-primary" onclick="batchCheck()"/>
					</sec:authorize>
					<sec:authorize url='/subject/publish.action'>
					<input type="button" value="批量加工发布" class="btn btn-primary" onclick="batchOneCheck()"/>
					</sec:authorize>
					<sec:authorize url="/subject/warnings.action"> 
				    <input type="button" value="批量通知" class="btn btn-primary" onclick="batchwarnings()"/>
				    </sec:authorize>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
				<%@ include file="/subject/checkWindow.jsp" %>
			</div>
	</div>
	<input type="hidden" id="name" name="name" value="${param.name }" />
	<input type="hidden" id="status" name="status" value="${param.status }" />
	<input type="hidden" id="storeType" name="storeType" value="${param.storeType }" />
	<input type="hidden" id="subject" name="subject" value="${param.subject }" />
	<input type="hidden" id="trade" name="trade" value="${param.trade }" />
	<input type="hidden" id="collectionStart" name="collectionStart" value="${param.collectionStart }" />
	<input type="hidden" id="collectionEnd" name="collectionEnd" value="${param.collectionEnd }" />
	<input type="hidden" name="posttype" id="posttype" value="2">
</form>
</div>
</body>
</html>