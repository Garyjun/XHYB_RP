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
<html>
<head>
<title>需求单修改</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${path}/resRelease/resOrder/templeteSelect.js"></script>
<script type="text/javascript" src="${path}/appframe/util/zxappDataGrid.js"></script>
<script type="text/javascript">
	$(function() {
		if('${resOrder.statusDesc}'=="审核驳回"){
			$("#auditRemark").show();
		}else{
			$("#auditRemark").hide();
		}
		resty();
		var date = '${resOrder.orderDate}';
		$("#orderDate").attr("value",date.substring(0,10));
		$('#form1').validationEngine('attach', {
			relative: true,
			overflownDIV:"#divOverflown",
			promptPosition:"topRight",//验证提示信息的位置，可设置为："topRight", "bottomLeft", "centerRight", "bottomRight"
			maxErrorsPerField:1,
			onValidationComplete:function(form,status){
				if(status&&checkDate()){
					save();
				}
			}
		});

	});
	function resty(){
		var div1=$('#meta_tab');
		div1.empty();
		var orderId = $('#orderId').val();
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
			$.ajax({
				url : _appPath + '/resOrder/querypublishtype.action?objectId='+ orderId,
				type : 'post',
				success : function(data) {
					$('#publish').val(data);
					showTab(keys);
				}
			});
			
		});
	}
	function checkDate(){
		var date = $("#orderDate").val();
		if(date!=""){
			return true;
		}
		return false;
	}
	var dt = new DataGrids();
	function showTab(keys) {
		var orderId = '${resOrder.orderId}';
		window.onresize=findDimensions();
		var sizewidth=$('#relation').width();
		$('#data_div').width(sizewidth);
		$('#res').val(keys);
		//alert(orderId);
		dt.setConditions([]);
		dt.setPK('objectId');
		dt.setSortName('create_time');
		dt.setURL('${path}/resOrder/resourceList.action?orderId=' + orderId+"&restype="+keys+"&posttype="+$('#posttype').val());
		dt.setSortOrder('desc');
		dt.setSelectBox('objectId');
		dt.setColums([ <app:QueryListColumnTag />
		{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}]);
		accountHeight();
		dt.initDt();
		dt.query();
	}
	function returnList() {
		if('${operateFrom}'=='TASK_LIST_PAGE'){
			window.location.href = "${path}/TaskAction/toList.action"; 
		}else{
			parent.queryForm();
		}
	}
	$operate = function(value,rec){
		var opt= "<sec:authorize url="/publishRes/detail.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
		return opt;
	};
	
	function detail(resId){
		var orderId = '${resOrder.orderId}';
		//$.openWindow("${path}/resOrder/resourceDetail.action?orderId="+orderId+"&resId="+resId, '资源详细信息', 1050, 530);
		$.openWindow("${path}/resRelease/resOrder/resFileDetail.jsp?orderId="+orderId+"&resId="+resId+"&restype="+$('#res').val()+"&posttype="+$('#posttype').val(), '资源详细信息', 800, 530);
	}
	
	$libTypeDesc = function(libType){
		if(libType==1){
			return "标准资源";
		}else if(libType==2){
			return "原始资源";
		}else{
			return "聚合资源";
		}
	}

	function save() {
		var orderId = '${resOrder.orderId}';
		var operateFrom = '${operateFrom}';
		var channelName= document.getElementById("channelName").value;
		var templateId = document.getElementById("templateId").value;
		var description= document.getElementById("description").value;
		var orderDate= document.getElementById("orderDate").value;
		if(channelName!=null&&channelName.trim().length>0){
			channelName = encodeURI(encodeURI(channelName));
		}
		if(description!=null&&description.trim().length>0){
			description = encodeURI(description);
		}
// 		if('${operateFrom}'=='TASK_LIST_PAGE'){
// 			window.location.href = "${path}/TaskAction/toList.action"; 
// 		}else{
// 			parent.queryForm();
// 		}
		//var operateFrom = '${operateFrom}';
		//alert(operateFrom+"      operateFrom");
		window.location.href = "${path}/resOrder/update.action?orderId="+orderId+"&operateFrom="+operateFrom+"&channelName="+
			channelName+"&templateId="+templateId+"&description="+description+"&orderDate="+orderDate;
	}

	function gotoSelect() {
		var orderId = ${resOrder.orderId};
		var posttype = $('#posttype').val();
		var type= document.getElementById("templateType").value;
		var publish = $('#publish').val();
		/* if(type=="education"){
			type = "T00,T01,T02,T03,T04,T05,T07,T08,T09,T10,T11,T12";
		}else{
			type = "T06";
		} */
		var operateFrom = '${operateFrom}';
		if(publish=="offLine"){
			parent.parent.layer.open({
			    type: 2,
			    title: '选择资源',
			    closeBtn: true,
			    maxmin: true, //开启最大化最小化按钮
			    area: ['1200px', '600px'],
			    shift: 2,
			    content: "${path}/resOrder/addResource.action?orderId="+orderId+"&type="+type+"&operateFrom="+operateFrom+"&posttype="+posttype  //iframe的url，no代表不显示滚动条
			    
			});
			
			
			//$.openWindow("${path}/resOrder/addResource.action?orderId="+orderId+"&type="+type+"&operateFrom="+operateFrom+"&posttype="+posttype, '选择资源', 1200, 600);
		}else{
			parent.parent.layer.open({
			    type: 2,
			    title: '选择资源',
			    closeBtn: true,
			    maxmin: true, //开启最大化最小化按钮
			    area: ['1200px', '600px'],
			    shift: 2,
			    content: "${path}/resOrder/addResourceonline.action?orderId="+orderId+"&type="+type+"&operateFrom="+operateFrom+"&posttype="+posttype  //iframe的url，no代表不显示滚动条
			    
			});
			
			//$.openWindow("${path}/resOrder/addResourceonline.action?orderId="+orderId+"&type="+type+"&operateFrom="+operateFrom+"&posttype="+posttype, '选择资源', 1200, 600);
		}
		
	}
	function toApply() {
		var objectId = '${resOrder.orderId}';
		var posttype = $('#posttype').val();
		$.ajax({
			url : "${path}/resOrder/canApply.action?orderId="+objectId+"&posttype="+posttype,
			type : "post",
			datatype : "text",
			success : function(data){
				if(data=="N"){
					$.alert("该需求单没有添加资源，无法上报！");
				}else{
					$.confirm('确定要上报吗？', function() {
						$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;正在处理中，请稍待。。。</div>',
							css: {
				                border: 'none',
				                padding: '20px',
				                backgroundColor: 'white',
				                textAligh:'center',
				            //    top:"50%",  
				                width:"300px",
				                opacity: .7
				               }
						});
						$.ajax({
							url : _appPath + '/resOrder/doApply.action?objectId='+ objectId+'&posttype='+posttype,
							type : 'post',
							datatype : 'text',
							success : function(returnValue) {
								$.alert(returnValue);
								$.unblockUI();
								if('${operateFrom}'=='TASK_LIST_PAGE'){
									window.location.href = "${path}/TaskAction/toList.action"; 
								}else{
									parent.queryForm();
								}
							}
						});
					});
				}
			}
			
		});
		
	}

	// function query(){
	// 	$grid.query();
	// }

	function clearResList(type) {
		var id = ${resOrder.orderId};
		var operateFrom = "${operateFrom}";
		var resIds = "";
		var resId = "";
		if(type=='part'){
			resId = getChecked('data_div', 'objectId').join(',');
			//alert(resId);
			if(resId!=""){
				resIds += resId + ",";
				resIds = resIds.substr(0,resIds.length-1);
			}
			
			if(resIds == ""){
				$.alert('请选择要删除的资源！');
				return;
			}
			$.confirm('确定要删除所选资源吗？', function() {
				location.href = "${path}/resOrder/deleteResourceByDeleteType.action?orderId="
						+ id + "&resIds=" + resIds + "&deleteType=" + "part" + "&operateFrom=" + operateFrom + "&posttype=" + $('#posttype').val();
			});
		}else{
			$.confirm('确定要清空资源吗？', function() {
				location.href = "${path}/resOrder/deleteResourceByDeleteType.action?orderId="
					+ id + "&resIds=" + "" + "&deleteType=" + "all" + "&operateFrom=" + operateFrom + "&posttype=" + $('#posttype').val();
			});
		}
	}
	
	//保存并上报
	function doSaveAnaSubmit(){
		var orderId = ${resOrder.orderId};
		var operateFrom = '${operateFrom}';
		var channelName = $("#channelName").val();
		var templateId = document.getElementById("templateId").value;
		var description= document.getElementById("description").value;
		var orderDate= document.getElementById("orderDate").value;
		if(channelName!=null){
			channelName = encodeURI(channelName);
		}
		if(description!=null&&description.trim().length>0){
			description = encodeURI(description);
		}
		
		//没有资源无法上报
		var data=$('#data_div').datagrid('getData');
		if(data.total <= 0){
			$.alert('该需求单没有添加资源，无法提交并上报!');
			return;
		}
		
		$.confirm('确定要提交审核吗？', function() {
			$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;正在处理中，请稍待。。。</div>',
				css: {
	                border: 'none',
	                padding: '20px',
	                backgroundColor: 'white',
	                textAligh:'center',
	                width:"300px",
	                opacity: .7
	               }
			});
			$.ajax({
				url : _appPath + '/resOrder/resOrderWorkFlow/doSaveAndSubmit.action?orderId='
						+ orderId +"&operateFrom="+operateFrom+"&wfTaskId=${wfTaskId}&channelName=" + channelName
						+"&templateId="+templateId+"&description="+description+"&orderDate="+orderDate+"&posttype="+$('#posttype').val(),
				type : 'post',
				datatype : 'text',
				success : function(returnValue) {
					if(returnValue == 'error'){
						$.alert("保存并提交失败");
					}else{
						$.alert("保存并提交成功");
					}
					$.unblockUI();
					if('${operateFrom}'=='TASK_LIST_PAGE'){
						window.location.href = "${path}/TaskAction/toList.action"; 
					}else{
						//parent.queryForm();
						location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
					}
					
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
// 		alert($('#data_div')+"   "+winWidth);
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
					<li class="active">需求单编辑</li>
				</ol>
			</div>
			<form action="${path}/resOrder/add.action"
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
						<input type="hidden" name="posttype" id="posttype" value="${posttype}"/>
						<input type="hidden" name="publish" id="publish" value="${resOrder.template.publishType}"/>
						<div class="portlet-body">
							<div class="row">
								<div class="container-fluid">
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">单号：</label>

												<div class="col-md-7">
													<input type="hidden" name="orderId" id="orderId" value="${resOrder.orderId}"/>
													<p class="form-control-static">${resOrder.orderId}</p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">状态：</label>

												<div class="col-md-7">
													<input type="hidden" name="staus" id="status" value="${resOrder.status}"/>
													<p class="form-control-static" name="status" id="status">${resOrder.statusDesc}</p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">创建日期：</label>

												<div class="col-md-7">
													<p class="form-control-static">
														<input type="hidden" name="createTime" id="createTime" value="${resOrder.createTime}"/>
														<fmt:formatDate value="${resOrder.createTime}" pattern="yyyy-MM-dd" />
													
													</p>
												</div>
											</div>
										</div>
										
											<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">下单日期：</label>
												<div class="col-md-4">
													<p class="form-control-static">
													<c:choose>
														<c:when test="${resOrder.status eq status }">
															<fmt:formatDate value="${resOrder.orderDate}" pattern="yyyy-MM-dd" />
														</c:when>
														<c:otherwise>
															<input class="form-control validate[required] Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" value="${resOrder.orderDate}" id="orderDate" name="orderDate"/>
														</c:otherwise>
													</c:choose>
													</p>
												</div>
											</div>
										</div>
										
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4"><i
													class="must">*</i>客户名称：</label>

												<div class="col-md-5">
												<c:choose>
														<c:when test="${resOrder.status eq status}">
															<p class="form-control-static">${resOrder.channelName}</p>
														</c:when>
														<c:otherwise>
															<input type="text" id="channelName" value="${resOrder.channelName }" name="channelName" class="form-control validate[required]" />
														</c:otherwise>		
												</c:choose>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4"><i
													class="must">*</i>模板名称：</label>
												<div class="col-md-4">
													<div class="input-group">
														<input type="hidden" name="template.id" value="${resOrder.template.id}" id="templateId" /> 
														<input type="hidden" name="template.type" value="${resOrder.template.type}" id="templateType" />
														<input type="text" class="form-control validate[required]"  value="${resOrder.template.name }" name="template.name" id="templateName" readonly="readonly" /> 
														<c:if test="${flag=='0'}">
															<span class="input-group-btn"> 
															<img src="${path }/appframe/main/images/select.png" alt="选择模板" title="选择模板" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelSelect('${resOrder.orderId}', '${operateFrom}');"/>
															<img src="${path }/appframe/main/images/detail.png" alt="详细" title="详细" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelDetail('detail');"/>
															<img src="${path }/appframe/main/images/clean.png" alt="清空" title="清空" style="cursor:pointer;margin-left: 3px;" onclick="javascript:clearModelValue();"/>
															</span>
														</c:if>
														<c:if test="${flag=='1'}">
															<span class="input-group-btn">
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
												
												<div class="col-md-8">
												<c:choose>
													<c:when test="${resOrder.status eq status }">
														<textarea rows="3" cols="" readonly="readonly" class="form-control">${resOrder.description}</textarea>
													</c:when>
													<c:otherwise>
														<textarea rows="3" cols="" class="form-control" id="description" name="description">${resOrder.description}</textarea>
													</c:otherwise>
												</c:choose>
												</div>
											</div>
										</div>
										
										<div class="col-md-12" id="auditRemark">
											<div class="form-group">
												<label class="control-label col-md-2">审核意见：</label>

												<div class="col-md-8">
													<textarea rows="3" cols="" class="form-control" id="auditRemark" name="auditRemark">${resOrder.auditRemark}</textarea>
												</div>
												<%-- <div class="col-xs-5">
													 <p class="form-control-static">${resOrder.auditRemark}</p> 
											    </div> --%> 
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
										<input type="hidden" id="wfTaskId" name="wfTaskId" value="${wfTaskId}"/>
											<input type="hidden" name="token" value="${token}" />
											<c:choose>
												<c:when test="${resOrder.status eq status }">
													<button type="button" class="btn btn-primary" onclick="returnList();">保存</button>
												</c:when>
												<c:otherwise>
													<button type="submit" class="btn btn-primary" onclick="chcekDate();">保存</button>
												</c:otherwise>
											</c:choose>
											&nbsp;
											
											<c:if test="${resOrder.status=='3'}">
											<sec:authorize url="/resOrder/apply.action">
												<button type="button" class="btn btn-primary"
													onclick="doSaveAnaSubmit();">提交并上报</button></sec:authorize>
											&nbsp;
											</c:if>
											
											<c:if test="${resOrder.status=='0'}">
											<sec:authorize url="/resOrder/apply.action">
												<button type="button" class="btn btn-primary"
													onclick="toApply();">上报</button>
											</sec:authorize>
											&nbsp;
											</c:if>
											
											<button type="button" class="btn btn-primary"
												onclick="returnList();">取消</button>
										</div> <br />
										<div id="dt"></div>
										<div id="dt-pag-nav"></div></td>
								</tr>
							</table>

							<table width="98%" border="0" align="center" cellpadding="0"
								cellspacing="0">
								<tr>
									<td><br />
										<div align="left">
											<input type="hidden" name="token" value="${token}" />
											<button type="button" class="btn btn-primary"
												onclick="gotoSelect()">添加可选资源</button>
											&nbsp;
											<button type="button" class="btn btn-primary"
												onclick="clearResList('part');">批量删除</button>
											&nbsp;
											<button type="button" class="btn btn-primary"
												onclick="clearResList('all');">清空资源</button>
										</div> <br />
										<div id="dt"></div>
										<div id="dt-pag-nav"></div></td>
								</tr>
							</table>
<!-- 							<div class="by-tab"> -->
								<!-- <ul class="nav nav-tabs nav-justified" id="meta_tab">
									<li class="active"><a  id="tab_1_1_2" data-toggle="tab"
										onclick="initTable(2);">原始资源</a></li>
									<li><a  id="tab_1_1_1" data-toggle="tab"
										onclick="initTable(1);">标准资源</a></li>
									<li><a  id="tab_1_1_3" data-toggle="tab"
										onclick="initTable(3);">聚合资源</a></li>
								</ul> -->
<!-- 								<div class="tab-content"> -->
<!-- 									<div class="tab-pane active"> -->
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
	  							
	  							<!-- <div class="panel panel-default" style="height: 100%;">-->
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
										<!-- </div>-->
									</div>
								</div> 
	  							
	  						</div>
						</div>
					</div>
				</div>
			</form>
			<!-- 工作流操作页面 -->
        <c:if test="${not empty execuId}">
			<%@ include file="/resRelease/resOrder/operationHistory.jsp" %>
        </c:if>
		</div>
	</div>
	<input type="hidden" id="operateFrom" value="${operateFrom}"/>
</body>
</html>