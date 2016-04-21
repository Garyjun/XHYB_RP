<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.brainsoon.resrelease.support.SysParamsTemplateConstants.PublishType"%>
<%@page import="com.brainsoon.resrelease.support.ResReleaseConstant.ResReleaseStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<%@ page import="com.brainsoon.resrelease.support.ResReleaseConstant"%>
<%@ page import="com.brainsoon.resrelease.support.ResReleaseConstant.OrderStatus"%>
<html>
	<head>
		<title>发布记录管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
		<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript">
			$(function(){
	 	   		//定义一datagrid
	 	   		var _divId = 'data_div';
	 	   		var _url = '${path}/resRelease/resOrderRecordList.action';
	 	   		var _pk = 'id';
	 	   		var _conditions = ['posttype','startDate','endDate','channelName','status','createUser'];
	 	   		var _sortName = 'id';
	 	   		var _sortOrder = 'desc';
	 	        var	_showCheck = false;
	 			var _colums = [ 
	 							{ title:'单据名称', field:'channelName' ,width:fillsize(0.12), align:'center'},
 	 						    { title:'单据编号', field:'orderId' ,width:fillsize(0.1), align:'center', sortable:true},
	 						  	{title:'资源加工</br>成功数/失败数/总数', field:'processMessage' ,width:fillsize(0.13), align:'center'},
	 						  	{title:'资源发布</br>成功数/失败数/总数', field:'publishMessage' ,width:fillsize(0.13), align:'center', sortable:true},
	 						    {title:'资源类型', field:'restypeMessage' ,width:fillsize(0.1), align:'center'},
	 						   	{title:'发布路径', field:'publishUrl' ,width:fillsize(0.18), align:'center'}, 
	 						   	{title:'发布途径', field:'posttype' ,width:fillsize(0.12), align:'center', sortable:true,formatter:$posttypeDesc},
	 						   	{title:'创建时间', field:'createTime' ,width:fillsize(0.12), align:'center', sortable:true},
	 							{title:'操作',field:'opt1',width:fillsize(0.1),align:'center',formatter:$operate}
	 						];
	 	   		
	 			accountHeight();
	 			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_showCheck);
	 	     	
	 		});
			$posttypeDesc = function(value,rec){
				if(rec.posttype=="1")
					return "需求单";
				else
					return "主题库";
			}
			/***操作列***/
	 		$operate = function(value,rec){
	 			var opt = "";
	 			var viewUrl='<sec:authorize url="/resRecord/detail.action"><a class=\"btn hover-red\"  href="javascript:view('+rec.id+')"><i class=\"fa fa-sign-out\"></i>详细</a>&nbsp;</sec:authorize> ';
					opt = viewUrl;
	 			return opt;
	 		};

	 		function view(orderId){
	   			window.location.href = "${path}/resRelease/view.action?id="+orderId;
	   		}
	 		
	 		<%-- function publish(orderId){
	 			var _url = "${path}/resOrder/getPublishType.action?orderId=" + orderId;
	 			$.ajax({
	 				url: _url,
	 				type: 'get',
	 				success: function(data){
	 					var _metaInfoUrl = "${path}/resOrder/getMetaInfo.action?orderId=" + orderId;
	 					if(data=="<%=PublishType.ON_LINE%>"){   //线上发布
	 						$.ajax({
	 							url: _metaInfoUrl,
	 							type: 'get',//本次为【线上发布】，发布操作是否立即执行？  发布操作已执行，请 等待回执结果！操作取消！
	 							success: function(data){
	 								if(data=="0"){//不发布资源文件
	 									$.confirm('本次为【线上发布】，此次不会发布【资源文件】！发布操作是否立即执行？', function(){
	 		 				 				$.alert("发布操作已执行，请等待回执结果！");	
	 		 				 				var _publishUrl = "${path}/resRelease/publish.action?orderId=" + orderId;
	 		 				 				$.ajax({
	 		 					 				url: _publishUrl,
	 		 					 				type: 'get',
	 		 					 				async: false,
	 		 					 				success: function(){
	 		 					 					//$grid.query();
	 		 					 					//location.href = "${path}/resRelease/resRecord/resRecordList.jsp";
	 		 					 				}
	 		 					 			});
	 			 						}, function(){
	 			 							$.alert("发布操作取消！");
	 			 						});
	 								}else{//发布资源文件
	 									$.confirm('本次为【线上发布】，此次将会发布【资源文件】！发布操作是否立即执行？', function(){
	 		 				 				$.alert("发布操作已执行，请等待回执结果！");	
	 		 				 				var _publishUrl = "${path}/resRelease/publish.action?orderId=" + orderId;
	 		 				 				$.ajax({
	 		 					 				url: _publishUrl,
	 		 					 				type: 'get',
	 		 					 				async: false,
	 		 					 				success: function(){
	 		 					 					//$grid.query();
	 		 					 					//location.href = "${path}/resRelease/resRecord/resRecordList.jsp";
	 		 					 				}
	 		 					 			});
	 			 						}, function(){
	 			 							$.alert("发布操作取消！");
	 			 						});
	 								}
	 							}
	 						});
	 						
	 						
	 					}else if(data=="<%=PublishType.OFF_LINE%>"){//线下发布
	 						$.ajax({
	 							url: _metaInfoUrl,
	 							type: 'get',
	 							success: function(data){
	 								if(data=="0"){//不发布资源文件
	 									$.confirm('本次为【线下发布】，此次不会发布【资源文件】！', function(){
	 		 				 				$.alert("发布操作已执行，请确定客户是否已经收到资源 ！");	
	 		 				 				var _publishUrl = "${path}/resRelease/publish.action?orderId=" + orderId;
	 		 				 				$.ajax({
	 		 					 				url: _publishUrl,
	 		 					 				type: 'get',
	 		 					 				async: false,
	 		 					 				success: function(){
	 		 					 					refreshRecordList();
	 		 					 					//location.href = "${path}/resRelease/resRecord/resRecordList.jsp";
	 		 					 					//setTimeout("location.href = '${path}/resRelease/resRecord/resRecordList.jsp;'" ,3000);
	 		 					 					//location.href = "${path}/resRelease/refreshList.action";
	 		 					 					//$grid.query();
	 		 					 					//setTimeout(refreshRecordList(), 3000);
	 		 					 				}
	 		 					 			});
	 			 						}, function(){
	 			 							$.alert("发布操作取消！");
	 			 						});
	 								}else{//发布资源文件
	 									$.confirm('本次为【线下发布】，此次将会发布【资源文件】！', function(){
	 		 				 				$.alert("发布操作已执行，请确定客户是否已经收到资源 ！");	
	 		 				 				var _publishUrl = "${path}/resRelease/publish.action?orderId=" + orderId;
	 		 				 				$.ajax({
	 		 					 				url: _publishUrl,
	 		 					 				type: 'get',
	 		 					 				async: false,
	 		 					 				success: function(){
	 		 					 					refreshRecordList();
	 		 					 					//location.href = "${path}/resRelease/resRecord/resRecordList.jsp";
	 		 					 					///location.href = "${path}/resRelease/refreshList.action";
	 		 					 					//setTimeout(refreshRecordList(), 3000);
	 		 					 				}
	 		 					 			});
	 			 						}, function(){
	 			 							$.alert("发布操作取消！");
	 			 						});
	 								}
	 							}
	 						});
	 					}
	 				}
	 			});
	 		} --%>
	 		
	 		function refreshRecordList(){
	 			location.href = "${path}/resRelease/refreshList.action";
	 			//location.href = "${path}/resRelease/resRecord/resRecordList.jsp";
	 		}
	   		
	   		function httpDownload(id){
	   			location.href = "${path}/resRelease/httpDownload.action?id="+id;
	   		}
	   		
	   		function formClear(){
		    	$('#modifiedStartTime').val('');
		    	$('#modifiedEndTime').val('');
	   		}
			function query(){
				$("#startDate").val($('#modifiedStartTime').val());
				$("#endDate").val($('#modifiedEndTime').val());
				$grid.query();
			}
		     
		</script>
	</head>
	<body>
		<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><span>资源发布</span></li>
					<li class="active">发布记录</li>
					<li class="active">发布记录列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;float: right;">
					<!-- <div style="float: right;"> -->
						<form action="#" class="form-inline no-btm-form" role="form">
						   <div class="form-group">
							      <input placeholder=" 开始时间" class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})" id="modifiedStartTime" name="modifiedStartTime" />
							</div>
							<div class="form-group">
							      <input placeholder="结束时间" class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})" id="modifiedEndTime" name="modifiedEndTime" />
							</div>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<input type="button" value="清空" class="btn btn-primary" onclick="formClear();query();"/>
						</form>
						<input type="hidden" id="startDate" name="startDate" value="${param.startDate }" />
						<input type="hidden" id="endDate" name="endDate" value="${param.endDate }" />
						<input type="hidden" id="status" name="status" value="${param.status }" />
						<input type="hidden" id="channelName" name="channelName" value="${param.channelName }" />
						<input type="hidden" id="posttype" name="posttype" value="${param.posttype }" />
						<%-- <input type="hidden" id="createUser" name="createUser" value="${param.createUser }" /> --%>
					<!-- </div> -->
				</div>
				<div id="data_div" class="data_div height_remain"></div>
			</div>			
		</div>
	</div>
	</body>
</html>