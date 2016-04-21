<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>需求单添加</title>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/resRelease/resOrder/templeteSelect.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#form1').validationEngine('attach', {
				relative: true,
				overflownDIV:"#divOverflown",
				promptPosition:"topRight",//验证提示信息的位置，可设置为："topRight", "bottomLeft", "centerRight", "bottomRight"
				maxErrorsPerField:1,
				onValidationComplete:function(form,status){
					if(status&&checkDate()){
// 						add();
						add("${path}/resOrder/add.action?posttype="+$('#posttype').val());
					}
				}
			});
			
		});
		
		function checkDate(){
			var date = $("#orderDate").val();
			if(date!=""){
				return true;
			}
			return false;
		}
		function resty(){}
		function add(act){
			document.form1.action=act; 
			jQuery('#form1').submit();
		}
		/* function add(){
			//document.form1.action=act; 
			jQuery('#form1').submit();
		} */
		function returnList(){
			location.href = "${path}/resRelease/resOrder/resOrderList.jsp";
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
					<li class="active">需求单编辑</li>
				</ol>
			</div>
			<p></p>
<%-- 	      		<form:form modelAttribute="resOrder" name="form1" id="form1" method="POST"  class="form-horizontal"> --%>
	      		<form:form modelAttribute="resOrder" name="form1" id="form1" method="POST"  class="form-horizontal">
		      		<form:hidden path="orderId"  /> 
		      		<input type="hidden" name="posttype" id="posttype" value="${posttype }"/>
					<div class="form-group">
						<label for="orderDate" class="col-sm-4 control-label text-right"><span class="required">*</span>下单日期：</label>
						<div class="col-sm-2">
<%--                               <input type="text" disabled="disabled" name="orderDate" id="orderDate" value="${resOrder.orderDate}" placeholder="<%=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())%>" class="form-control Wdate" onClick="WdatePicker({readOnly:true})"/> --%>
                              <input class="form-control validate[required] Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="orderDate" name="orderDate" class="form-control validate[required] text-input" />
                         </div>
					</div>
					
					<div class="form-group">
						<label for="channelName" class="col-sm-4 control-label text-right"><span class="required">*</span>客户名称：</label>
						<div class="col-sm-4">
							 <form:input  path="channelName" id="channelName" value="${resOrder.channelName}" class="form-control validate[required] text-input" />
						</div>
					</div>
				  <div class="form-group">
					<label for="template.id" class="col-sm-4 control-label text-right"><span class="required">*</span>模板名称：</label>
						<div class="col-sm-4">
	                      	<div class="input-group">
	                      		<input type="text" class="form-control validate[required]" value="${resOrder.template.name }" name="template.name" id="templateName" readonly="readonly"/>
	                      		<input type="hidden" name="template.id" value="${resOrder.template.id}" id="templateId"/>
	                      		<input type="hidden" name="template.type" value="${resOrder.template.type}" id="templateType"/>
								<span class="input-group-btn">
								<!-- <a id="menuBtn2" onclick="javascript:modelSelect(-1);" class="btn btn-primary" role="button">模板选择</a> -->
								<img src="${path }/appframe/main/images/select.png" alt="选择模板" title="选择模板" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelSelect(-1);"/>
								<img src="${path }/appframe/main/images/detail.png" alt="详细" title="详细" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelDetail('detail');"/>
								<img src="${path }/appframe/main/images/clean.png" alt="清空" title="清空" style="cursor:pointer;margin-left: 3px;" onclick="javascript:clearModelValue();"/>
								<!-- <a onclick="javascript:modelDetail('detail');"  class="btn btn-primary" role="button" style="margin-left: 3px;">详细</a>
								<a id="clearModel" onclick="javascript:clearModelValue();"  class="btn btn-primary" role="button" style="margin-left: 3px;">清空</a> -->
								</span>
	                         </div>
						</div>
				  </div>
			      <div class="form-group">
						<label for="description" class="col-sm-4 control-label text-right">描述信息：</label>
						<div class="col-sm-6">
						     <form:textarea path="description" value="encodeURI(${resOrder.description})" rows="3" class="form-control"/> 
						</div>
				</div>
				<div class="form-group">
						<div class="col-sm-offset-4 col-sm-6">
			           		<input type="hidden" name="token" value="${token}" />
   							   <button type="submit"  class="btn btn-primary">下一步</button>
<!--    							   <button type="submit"  class="btn btn-primary" onclick="checkDate();">下一步</button> -->
   							  &nbsp;
   							  <button type="reset" class="btn btn-primary">重置</button>
   							   &nbsp; 
			            	<input class="btn btn-primary" type="button" value="取消" onclick="returnList()"/>
			            </div>
				</div>
			</form:form>   	
  		  </div>
  		  </div>
</body>
</html>