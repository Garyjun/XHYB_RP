<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page
	import="org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<%@ page import="com.brainsoon.resrelease.support.ResReleaseConstant"%>
<%@ page
	import="com.brainsoon.resrelease.support.ResReleaseConstant.OrderStatus"%>
<%@ page import="com.brainsoon.system.support.SystemConstants"%>
<%@ page import="com.brainsoon.system.support.SystemConstants"%>
<html>
<head>
<title>需求单审核</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript" src="${path}/resRelease/resOrder/templeteSelect.js"></script>
<script type="text/javascript" src="${path}/appframe/util/zxappDataGrid.js"></script>
<script type="text/javascript">
	$(function() {
		var div1=$('#meta_tab');
		div1.empty();
		var orderId = '${resOrder.orderId}';
		var num1 = 0;
		
		$.post("${path}/resOrder/queryrestype.action", {orderId: orderId},function(data){
			var da = JSON.parse(data);
			var keys='';
			for(var key in da){
				++num1;
				if(num1==1){
					div1.append("<li class=\"active\" onclick=\"showTab("+key+")\"><a href=\"\" data-toggle=\"tab\">"+da[key]+" </a></li>");
					keys =key;
				}else{
					div1.append("<li onclick=\"showTab("+key+")\"><a href=\"\" data-toggle=\"tab\">"+da[key]+" </a></li>");
				}
			}
			 showTab(keys);
		});
		
	});
	/* function showTab() {
		//定义一datagrid
		var _divId = 'data_div';
		var _url = '${path}/resOrder/resourceList.action?libType=2&id=${resOrder.orderId}';
		var _pk = 'objectId';
		var _conditions = [ 'module', 'type', 'status', 'eduPhase', 'version',
				'subject', 'grade', 'fascicule', 'title', 'creator',
				'description', 'keywords', 'modifiedStartTime',
				'modifiedEndTime', 'searchText', 'queryType', 'libType' ];
		var _sortName = 'modified_time';
		var _sortOrder = 'desc';
		var _colums = [ 
		   	{field : 'commonMetaData.commonMetaDatas.unit',title : '内容单元',width : fillsize(0.2),sortable : true, align:'center'}, 
		   	{field : 'commonMetaData.commonMetaDatas.title',title : '资源标题',width : fillsize(0.17),sortable : true, align:'center'}, 
		   	{field : 'commonMetaData.commonMetaDatas.creator',title : '制作者',width : fillsize(0.07),sortable : true, align:'center'}, 
		   	{field : 'commonMetaData.commonMetaDatas.keywords',title : '关键字',width : fillsize(0.1),sortable : true, align:'center'}, 
		   	{field : 'commonMetaData.commonMetaDatas.libType',title : '资源库类型',width : fillsize(0.1),sortable : true, align:'center',formatter:$libTypeDesc}, 
		   	{field : 'commonMetaData.commonMetaDatas.module',title : '资源模块',width : fillsize(0.1),sortable : true, align:'center'}, 
		   	{field : 'type',title : '资源类型',width : fillsize(0.1),sortable : true, align:'center'}, 
		   	{field : 'commonMetaData.commonMetaDatas.modified_time',title : '创建时间',width : fillsize(0.18),sortable : true, align:'center'} 
		];
		$grid1 = $.datagrid(_divId, _url, _pk, _colums, _conditions, _sortName,
				_sortOrder, false, true, true);
	} */
	
	var dt = new DataGrids();
	function showTab(keys) {
		var orderId = '${resOrder.orderId}';
		window.onresize=findDimensions();
		var sizewidth=$('#relation').width();
		$('#data_div').width(sizewidth);
		$('#res').val(keys);
		dt.setConditions([]);
		dt.setPK('objectId');
		dt.setSortName('create_time');
		dt.setURL('${path}/resOrder/resourceList.action?'+"&orderId=" + orderId+"&restype="+keys+"&posttype="+$('#posttype').val());
		dt.setSortOrder('desc');
		dt.setSelectBox('objectId');
		dt.setColums([ <app:QueryListColumnTag />
		{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}]);
		accountHeight();
		dt.initDt();
		dt.query();
	}
	
	$operate = function(value,rec){
		var opt= "<sec:authorize url="/publishRes/detail.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
		return opt;
	};
	
	function detail(resId){
		var orderId = '${resOrder.orderId}';
		$.openWindow("${path}/resRelease/resOrder/resFileDetail.jsp?orderId="+orderId+"&resId="+resId+"&restype="+$('#res').val()+"&posttype="+$('#posttype').val(), '资源详细信息', 800, 530);
	}
	
	$libTypeDesc = function(value,rec){
		var libType = rec.commonMetaData.commonMetaDatas.libType;
		if(libType==1){
			return "标准资源";
		}else if(libType==2){
			return "原始资源";
		}else{
			return "聚合资源";
		}
	}
	function returnList() {
		//location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
		if('${operateFrom}'=='TASK_LIST_PAGE'){
			window.location.href = "${path}/TaskAction/toList.action"; 
		}else{
			parent.queryForm();
		}
	}
	
	function doCheck(decision){
		var opFrom = '${operateFrom}';
		var auditRemark = $('#auditRemark').val();
		if(decision=="reject"){
			$("#requiredAuditRemark").show();
			if(auditRemark==""){
				$.alert("审核驳回，请填写审核意见！");
				return;
			}
		}
		if(auditRemark!=null){
			auditRemark = encodeURI(auditRemark);
		}
		
		//alert(auditRemark+"    审核意见");
		var request_url = '${path}/resOrder/resOrderWorkFlow/doCheck.action?objectId=${resOrder.orderId}'
				+'&wfTaskId=${wfTaskId}&decision='+decision
				+'&checkOpinion=' + auditRemark
				+"&operateFrom=${operateFrom}";
		var operateFrom = '${operateFrom}';
		$.confirm('确定审核'+(decision=='approve'?'通过':'驳回')+'吗？', function(){
			$.ajax({
				url: request_url,
			    type: 'get',
			    datatype: 'text',
			    contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
			    success: function (destination) {
			    	window.location.href = '${path}'+destination;
// 			    	if(operateFrom=="TASK_LIST_PAGE"){
// 			    		location.href = "${path}/TaskAction/list.action";
// 			    	}else{
// 			    		location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
// 			    	}
			    	
			    }
			});
		});
	}
	var winWidth = 0;
	var winHeight = 0; 
	function findDimensions(){  //函数：获取尺寸
		//获取窗口宽度 
		if (window.innerWidth){
			winWidth = window.innerWidth; 
		} else if ((document.body) && (document.body.clientWidth)){
			winWidth = document.body.clientWidth; 
		} 
		//获取窗口高度 
		if (window.innerHeight){ 
			winHeight = window.innerHeight; 
		} else if ((document.body) && (document.body.clientHeight)){ 
			winHeight = document.body.clientHeight; 
		}
		//通过深入Document内部对body进行检测，获取窗口大小 
		if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth){ 
			winHeight = document.documentElement.clientHeight; 
			winWidth = document.documentElement.clientWidth; 
		} 
		var valueWidth = winWidth.toString();
		$('#data_div').width(valueWidth);
	} 
	findDimensions(); 
	window.onresize=findDimensions; 

</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap ">
		<div class="panel panel-default">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">需求单审核</a></li>
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
						<input type="hidden" name="res" id="res"/>
						<input type="hidden" id="posttype" name="posttype" value="${posttype }"/>
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
												<label class="control-label col-md-4">客户名称：</label>

<!-- 												<div class="col-md-7"> -->
<!-- 													<input type="text" id="channelName" -->
<%-- 														value="${resOrder.channelName }" name="channelName" --%>
<!-- 														class="form-control" /> -->
<!-- 												</div> -->
												<div class="col-md-7">
													<p class="form-control-static">${resOrder.channelName}</p>
												</div>
											</div>
										</div>
<!-- 										<div class="col-md-6"> -->
<!-- 											<div class="form-group"> -->
<!-- 												<label class="control-label col-md-4">模板名称：</label> -->
<!-- 													<div class="col-md-7"> -->
<%-- 														<p class="form-control-static">${resOrder.template.name}</p> --%>
<!-- 													</div> -->
<!-- 											</div> -->
<!-- 										</div> -->
										
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">模板名称：</label>
												<div class="col-md-4">
													<div class="input-group">
														<input type="text" class="form-control validate[required]"
															value="${resOrder.template.name }" name="template.name"
															id="templateName" readonly="readonly" /> <input
															type="hidden" name="template.id"
															value="${resOrder.template.id}" id="templateId" /> <input
															type="hidden" name="template.type"
															value="${resOrder.template.type}" id="templateType" />

														<c:if test="${flag=='0'}">
															<span class="input-group-btn"> 
															<%-- <a id="menuBtn2" onclick="javascript:modelSelect('${resOrder.orderId}');" class="btn btn-primary" role="button">模板选择</a>
															<a onclick="javascript:modelDetail('edit');"class="btn btn-primary" role="button" style="margin-left: 3px;">详细</a> 
															<a id="clearModel" onclick="javascript:clearModelValue();"class="btn btn-primary" role="button" style="margin-left: 3px;">清空</a> --%>
															<img src="${path }/appframe/main/images/select.png" alt="选择模板" title="选择模板" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelSelect('${resOrder.orderId}');"/>
															<img src="${path }/appframe/main/images/detail.png" alt="详细" title="详细" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelDetail('detail');"/>
															<img src="${path }/appframe/main/images/clean.png" alt="清空" title="清空" style="cursor:pointer;margin-left: 3px;" onclick="javascript:clearModelValue();"/>
															</span>
														</c:if>



														<c:if test="${flag=='1'}">
															<span class="input-group-btn"> 
															<!-- <a onclick="javascript:modelDetail('detail');" class="btn btn-primary" role="button" style="margin-left: 3px;">详细</a> -->
															<img src="${path }/appframe/main/images/detail.png" alt="详细" title="详细" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelDetail('detail');"/>
															</span>
														</c:if>
													</div>
												</div>
											</div>
										</div>
										
										
										<div class="col-md-12">
											<div class="form-group">
												<label class="control-label col-md-2">描述信息：</label>

<!-- 												<div class="col-md-8"> -->
<%-- 													<textarea rows="3" cols="" class="form-control" disabled="disabled" name="description">${resOrder.description}</textarea> --%>
<!-- 												</div> -->
												<div class="col-xs-5">
													<p class="form-control-static"
														style="white-space:nowrap; width:600px; overflow:hidden;  text-overflow:ellipsis;" title="${resOrder.description}">${resOrder.description}</p>
												</div>
											</div>
										</div>
										
										<div class="col-md-12">
											<div class="form-group">
												<label class="control-label col-md-2"><i id="requiredAuditRemark" style="display:none;" 
													class="must">*</i>审核意见：</label>

												<div class="col-md-8">
													<textarea rows="3" cols="" class="form-control" id="auditRemark" name="auditRemark"></textarea>
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
											<input type="hidden" name="token" value="${token}" />
											<button type="button" class="btn btn-primary"
												onclick="doCheck('approve')">通过</button>
											&nbsp;
											
											<button type="button" class="btn btn-primary"
												onclick="doCheck('reject');">驳回</button>
											&nbsp;
											
											<button type="button" class="btn btn-primary"
												onclick="returnList();">返回</button>
										</div> <br />
										<div id="dt"></div>
										<div id="dt-pag-nav"></div></td>
								</tr>
							</table>

<!-- 							<div class="by-tab"> -->
<!-- 								<div class="tab-content"> -->
<!-- 									<div class="tab-pane active" id="tab_1_1_2_tab"> -->
<!-- 										<div class="portlet"> -->
<!-- 											<div id="data_div" class="data_div" -->
<!-- 												style="width: 1050px; height: 300px;"></div> -->
<!-- 										</div> -->
<!-- 									</div> -->
<!-- 								</div> -->
<!-- 							</div> -->
							
							<div class="portlet">
	  							<div class="portlet-title">
	      							<div class="caption">资源明细</div>
	  							</div>
	  							<!-- <div class="panel-body" id="999" style="width:100%;">
		   							<div id="data_div" class="data_div" ></div>
	  							</div> -->
	  							<div class="by-tab">
										
										<div class="portlet"> 
											<div class="panel-body">
											<ul class="nav nav-tabs nav-justified" id="meta_tab"></ul>
												<div class="tab-content">
        											<div class="tab-pane active" id="tab_1_1_1" style="width:100%">
														<div class="portlet" id="relation">
															<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
														</div>
													</div>
												</div>
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