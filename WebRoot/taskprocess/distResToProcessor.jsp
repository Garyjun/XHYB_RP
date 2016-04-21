<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument.Include"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>需求单明细</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
<script type="text/javascript">
	$(function() {
		showTab();
		$("input[name=addBtn]").on("change", function(){
			var checkedRadioVal = this.value;
			//alert(checkedRadioVal);
			$("#resNum").val("");
			if(checkedRadioVal=="curBtn"){
				$("#selCur").css("display", "block");
				$("#inpNum").css("display", "none");
			}else{
				$("#inpNum").css("display", "block");
				$("#selCur").css("display", "none");
			}
		});
	});
	
	//
	var dtRes = new DataGrid();
	function showTab() {
		var taskId = $("#taskId").val();
		dtRes.setDivId("data_div");
		dtRes.setConditions([]);
		dtRes.setPK('id');
		dtRes.setSortName('id');
		dtRes.setURL('${path}/taskProcess/queryTaskResource.action?taskId=' + taskId + "&status=distributing");
		dtRes.setSortOrder('desc');
		dtRes.setSelectBox('id');
		dtRes.setColums([
        	{ title:'资源名称', field:'resName', width:fillsize(0.4), align:'center', sortable:true},
		    { title:'状态', field:'status', width:fillsize(0.15), align:'center', formatter:$statusDesc}//,
		   	//{ title:'操作', field:'opt1', width:fillsize(0.45), align:'center', formatter:$operateRes}
	    ]);
		accountHeight();
		dtRes.initDt();
		dtRes.query(); 
	}
	
	$statusDesc = function(value, rec){
		if(rec.status==0){
			return "未分配";
		}else if(rec.status==1){
			return "已分配";
		}
	}
	
	$operateRes = function(value,rec){
		/* var resDetail= "<sec:authorize url="/taskProcess/detail.action"><a class=\"btn hover-red\" href=\"javascript:resDetail('"+rec.sysResDirectoryId+"')\" ><i class=\"fa-sign-out\"></i> 详细</a></sec:authorize>";
		var selRes= "<sec:authorize url="/taskProcess/detail.action"><a class=\"btn hover-red\" href=\"javascript:delRes('"+rec.taskDetail.id+"')\" ><i class=\"fa-trash-o\"></i> 删除</a></sec:authorize>";
		var opt = resDetail + selRes;
		return opt; */
	};
	
	function returnList() {
		//location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
	}
	
	function addSelectedResToMaker(){
		var processorId = $("#processorId").val();
		var taskId = $("#taskId").val();
		var taskDetailIds = getChecked('data_div','taskDetail.id').join(',');
		//alert("processorId： " + processorId + "   taskId:" + taskId + "   taskDetailIds:" + taskDetailIds);
		if (taskDetailIds == '') {
			$.alert('请选择要分配的资源！');
			return;
		} else{
			var _url = "${path}/taskProcess/distributedResToProcessor.action?taskId=" + taskId +
				"&processorId=" + processorId + "&taskDetailIds=" + taskDetailIds;
			$.ajax({
				url: _url,
				type: "get",
				success: function(){
					queryForm(taskId);
					var parentWin =  top.index_frame.work_main;
					parentWin.freshDataTable('data_div_user');
					//$.closeFromInner();
				}
			});
		}
	}

	function addResNumToMaker(){
		var processorId = $("#processorId").val();
		var taskId = $("#taskId").val();
		var resNum = $("#resNum").val();
		if(resNum==undefined||resNum==null||resNum.trim()==""){
			$("#resNum").val("");
			$.alert("请输入大于0数字！");
			return;
		}else{
			//reg=/^(d{3,4}-)?[1-9]d{6,7}$/;
			reg=/^[0-9]*[1-9][0-9]*$/;
			if(!reg.test(resNum)){
				$("#resNum").val("");
				$.alert("请输入大于0数字！");
			}else{
				var _url = "${path}/taskProcess/distributedResToProcessorByNum.action?taskId=" + taskId +
					"&processorId=" + processorId + "&resNum=" + resNum;
				$.ajax({
					url: _url,
					type: "get",
					success: function(){
						queryForm(taskId);
						var parentWin =  top.index_frame.work_main;
						parentWin.freshDataTable('data_div_user');
					}
				});
			}
		}
		
	}
	
	function queryForm(taskId) {
		dtRes.dataGridObj.datagrid({
			url : "${path}/taskProcess/queryTaskResource.action?taskId=" + taskId + "&status=distributing"
		});
	}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap ">
		<div>
			<label><span>选择添加策略：</span>
				<input checked="checked" type="radio" name="addBtn" id="curBtn" value="curBtn" style="margin-left:10px;"/>
				<span style="font-size:12px;">手动添加 </span> 
			</label>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>
				<input type="radio" name="addBtn" id="numBtn" value="numBtn"/>
				<span style="font-size:12px;">按数量添加 </span> 
			</label>
		</div>
		<div style="margin-left:-10px;">
		<table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
			<tr>
				<td><br />
					<div align="left">
						<input type="hidden" name="token" value="${token}" />
						<div id="selCur" style="display:block;">
						<button type="button" class="btn btn-primary"
							onclick="addSelectedResToMaker();" style="float:left;">确定</button>
						</div>
						<div id="inpNum" style="display:none;">
							<!-- <div class="col-md-4" style="margin-left:-18px;">
								<div class="form-group">
									<label class="control-label col-md-1"><i
										class="must">*</i>请输入添加条数：</label>
									<div class="col-md-1" style="margin-left:-50px;">
										<div class="input-group">
											<input type="text" class="form-control validate[required]" style="" value="" name="resNum" id="resNum"/>
												<span class="input-group-btn">
													<a onclick="addResNumToMaker();" class="btn btn-primary" role="button"
														style="margin-left: 3px;">确定</a>
												</span>
										</div>
									</div>
								</div>
							</div> -->
							<div style="float: left;">
								<form action="#" class="form-inline no-btm-form">
						   			<div class="form-group">
							      		<input placeholder="请输入添加条数" class="form-control" id="resNum" name="resNum" />
							      	</div>
							      	<input type="button" value="确定" class="btn btn-primary" onclick="addResNumToMaker();"/>
						      	</form>
							</div>
							</div>
						</div>
					</div>
					 <br />
					<div id="dt"></div>
					<div id="dt-pag-nav"></div>
				</td>
			</tr>
		</table>
		</div>
	</div>
	<div class="panel-body" id="999" style="width:765px;">
		<div id="data_div" class="data_div" ></div>
	</div>
	<input type="hidden" id="taskId" value="${param.taskId}"/>
	<input type="hidden" id="processorId" value="${param.processorId}"/>
</body>
</html>