<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>元数据定制</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	   $(function(){
		    var nameMustCN = "${metaDataGroup.nameMustCN}";
			var nameMayCN = "${metaDataGroup.nameMayCN}";
			var nameExpandCN = "${metaDataGroup.nameExpand}";
			var countMust = 0;
			var countMay = 0;
			var mCN = nameMustCN.split(",");
			var yCN = nameMayCN.split(",");
			for (var i=0;i<mCN.length;i++){
				var nameMust = mCN[i];
				countMust++;
				if(nameMust!='') {
					var label = $("<label class='checkbox-inline form-group'></label>");
					label.appendTo($("#meDataMust"));
					var br =$("<br/>");
					if(countMust%5==0){
						br.appendTo($("#meDataMust"));
					}
					var input = $("<input name='must' type='checkbox' checked='checked' disabled='disabled'/>"+nameMust+"</input>");
					input.appendTo(label);
				}
			}
			
			for (var i=0;i<yCN.length;i++){
				var nameMay = yCN[i];
				countMay++;
				if(nameMay!=''){
					var label1 = $("<label class='checkbox-inline form-group'></label>");
					label1.appendTo($("#meDataMay"));
					var br =$("<br/>");
					if(countMay%5==0){
						br.appendTo($("#meDataMay"));
					}
					var input1 = $("<input name='may' type='checkbox' checked='checked' disabled='disabled'/>"+nameMay+"</input>");
					input1.appendTo(label1);
				}
			}
	   });
	   
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<ul class="page-breadcrumb breadcrumb">
				<li><a href="#">应用集成方定义</a><i class="fa fa-angle-right"></i></li>
				<li><a href="#">元数据定制</a></li>
		</ul>
		<form:form action="" id="form" modelAttribute="frmMetaDataTerm" method="post" class="form-horizontal" role="form">
		<div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">必选数据元素</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid" id="meDataMust">
                 </div>
           </div>
		<div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">可选数据元素</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid" id="meDataMay">
                </div>
             </div>  
        </div>    
		            
		<div class="form-group">
			<div class="col-xs-offset-5 col-xs-7">
	           	<input type="hidden" name="token" value="${token}" />
	            <input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	         </div>
		</div>
		</div>
		</form:form>
	</div>
</body>
</html>