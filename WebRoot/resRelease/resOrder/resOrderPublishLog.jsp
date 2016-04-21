<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page
	import="org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<%@ page import="com.brainsoon.resrelease.support.ResReleaseConstant"%>
<%@ page import="com.brainsoon.resrelease.support.ResReleaseConstant.*"%>
<%@ page import="com.brainsoon.system.support.SystemConstants"%>
<%@ page import="com.brainsoon.system.support.SystemConstants"%>
<html>
<head>
<title>发布明细</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript" src="${path}/resRelease/resOrder/templeteSelect.js"></script>
<script type="text/javascript">
	$(function() {
		showTab();
	});
	function showTab() {
		//定义一datagrid
		var _divId = 'data_div';
		var _url = '${path}/resOrder/publishLogQuery.action?orderId=${resOrder.orderId}';
		var _pk = 'id';
		var _conditions = [ 'module', 'type', 'status', 'eduPhase', 'version',
				'subject', 'grade', 'fascicule', 'title', 'creator',
				'description', 'keywords', 'modifiedStartTime',
				'modifiedEndTime', 'searchText', 'queryType', 'libType' ];
		var _sortName = 'createTime';
		var _sortOrder = 'desc';
		var _colums = [ 
			{field : 'resId', title : '资源编号', width : fillsize(0.2),sortable : true}, 
			{field : 'resTypeDesc',title : '资源类型', width : fillsize(0.1),sortable : true}, 
			{field : 'createUser.userName', title : '创建者', width : fillsize(0.1),sortable : true}, 
			{field : 'createTime', title : '创建时间', width : fillsize(0.18), sortable : true},
			{field : 'status', title : '状态', width : fillsize(0.1), sortable : true, formatter:$statusDesc},
			{title:'操作',field:'opt1',width:fillsize(0.17),align:'center',formatter:$operate}
		];
		$grid1 = $.datagrid(_divId, _url, _pk, _colums, _conditions, _sortName,
				_sortOrder, false, true, true);
	}
	
	/***操作列***/
	$operate = function(value,rec){
		var opt = "";
		var viewUrl= '<sec:authorize url="/role/view/*"><a class=\"btn hover-red\"  href="javascript:view('+rec.orderId+')"><i class=\"fa fa-sign-out\"></i>查看</a>&nbsp;</sec:authorize> ';		
		var publishUrl = '<sec:authorize url="/role/update"><a  class=\"btn hover-red\" href="javascript:publish('+rec.orderId+')"><i class=\"fa fa-edit\"></i>发布</a>&nbsp;</sec:authorize> ';	
		var publishLogUrl = '<sec:authorize url="/role/update"><a  class=\"btn hover-red\" href="javascript:publishLog('+rec.orderId+')"><i class=\"fa fa-edit\"></i>发布日志</a>&nbsp;</sec:authorize> ';	
		if(rec.status==<%=ProcessStatus.PROCESSED %>){
		   opt=viewUrl;
		 }
		if(rec.status==<%=ProcessStatus.FAILED %>){
		    opt=viewUrl;
		}
		return opt;
	};
	
	$statusDesc = function(value,rec){
		if(rec.status=="0")
			return "失败";
		else
			return "成功";
		};
	
	function returnList() {
		location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
	}

</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap ">
		<div class="panel panel-default">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">发布单明细</a></li>
				</ol>
			</div>
			<form action="${path}/resOrder/saveResOrderAndResource.action"
				id="form1" class="form-horizontal">
				<div class="tab-pane" id="tab_1_1_2">
					<div class="portlet">
						<div class="portlet-title">
							<div class="caption">
								需求单信息 <a href="javascript:;" onclick="togglePortlet(this)">
<!-- 								<i class="fa fa-angle-up"></i> -->
								</a>
							</div>
						</div>
						<div class="portlet-body">
							<div class="row">
								<div class="container-fluid">
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">单号：</label>

												<div class="col-md-7">
													<p class="form-control-static">${resOrder.orderId}</p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">状态：</label>

												<div class="col-md-7">
													<p class="form-control-static">${resOrder.statusDesc}</p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">下单日期：</label>

												<div class="col-md-7">
													<p class="form-control-static">
														<fmt:formatDate value="${resOrder.orderDate}" pattern="yyyy-MM-dd" />
													</p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">创建日期：</label>

												<div class="col-md-7">
													<p class="form-control-static">
<%-- 													${resOrder.createTime} --%>
<%-- 														<fmt:formatDate value="${resOrder.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /> --%>
														<fmt:formatDate value="${resOrder.createTime}" pattern="yyyy-MM-dd" />
													
													</p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4"><i
													class="must">*</i>客户名称：</label>

												<div class="col-md-7">
<!-- 													<input type="text" id="channelName" -->
<%-- 														value="${resOrder.channelName }" name="channelName" --%>
<!-- 														class="form-control" /> -->
												<p class="form-control-static">${resOrder.channelName}</p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4"><i
													class="must">*</i>模板名称：</label>
													<div class="col-md-4">
													<div class="input-group">
														<p class="form-control-static">${resOrder.template.name}</p>
<!-- 														<div class="input-group"> -->
															<span class="input-group-btn"> <a
																	onclick="javascript:modelDetail('detail');"
																	class="btn btn-primary" role="button"
																	style="margin-left: 3px;">详细</a>
																</span>
															<input type="hidden" name="template.id"
																value="${resOrder.template.id}" id="templateId" /> <input
																type="hidden" name="template.type"
																value="${resOrder.template.type}" id="templateType" />
	
																
														</div>
													</div>
											</div>
										</div>
										<div class="col-md-12">
											<div class="form-group">
												<label class="control-label col-md-2"><i
													class="must">*</i>描述信息：</label>

												<div class="col-xs-5">
													<p class="form-control-static">${resOrder.description}</p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>

							<table width="98%" border="0" align="center" cellpadding="0"
								cellspacing="0">
								<tr>
									<td><br />
										<div align="center">
											<button type="button" class="btn btn-primary"
												onclick="returnList();">返回</button>
										</div> <br />
										<div id="dt"></div>
										<div id="dt-pag-nav"></div></td>
								</tr>
							</table>

							<div class="by-tab">
								<div class="tab-content">
									<div class="tab-pane active" id="tab_1_1_2_tab">
										<div class="portlet">
											<div id="data_div" class="data_div"
												style="width: 1300px; height: 300px;"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>