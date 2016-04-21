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
		   queryMedata();
	   });
	   
	   
	   function queryMedata(){
			$.ajax({
				type:"post",
				url:"${path}/system/inDefinition/haveMetaData.action",
				success:function(data){
						var json = JSON.parse(data);
						var jsonList = json.customPropertys;
						var countMust = 0;
						var countMay = 0;
						var nameMustCN = "${frmMetaDataTerm.nameMustCN}";
						var mCN = nameMustCN.split(",");
						var nameMayCN = "${frmMetaDataTerm.nameMayCN}";
						var yCN = nameMayCN.split(",");
						for(var i=0;i<jsonList.length;i++){
							var jsonObj = jsonList[i];
							var name = jsonObj.name;
							var nameCN = jsonObj.nameCN;
							var refer = jsonObj.refer;
							var necessary = jsonObj.necessary;
							if(refer==2) {
								countMust++;
								var label = $("<label class='checkbox-inline form-group'></label>");
								label.appendTo($("#meDataMust"));
								var br =$("<br/>");
								if(countMust%5==0) {
									br.appendTo($("#meDataMust"));
								}
								var _exist=$.inArray(nameCN,mCN); 
								if(_exist>=0){ 
									var input = $("<input name='must' value='"+name+"' type='checkbox' checked='checked'/>"+nameCN+"</input>");
									input.appendTo(label);
								} else {
									var input = $("<input name='must' value='"+name+"' type='checkbox'/>"+nameCN+"</input>");
									input.appendTo(label);
								}
								
							} else if(refer==3) {
								countMay++;
								var label = $("<label class='checkbox-inline form-group'></label>");
								label.appendTo($("#meDataMay"));
								var br =$("<br/>");
								if(countMay%5==0) {
									br.appendTo($("#meDataMay"));
								}
								var _exist=$.inArray(nameCN,yCN); 
								if(_exist>=0){
									var input = $("<input name='may' value='"+name+"' type='checkbox' checked='checked'/>"+nameCN+"</input>");
									input.appendTo(label);
								} else{
									var input = $("<input name='may' value='"+name+"' type='checkbox'/>"+nameCN+"</input>");
									input.appendTo(label);
								}
							}
						}
				}
			});
	     }
			
			function save(){
				   var metaDataTermId = $("#metaDataTermId").val();
				   var id = $("#id").val();
				   var must =''; 
				   var mustcn ='';
				   $('input[name="must"]:checked').each(function(){  
					   must+=$(this).val()+","; 
					   mustcn+=$(this).parent().text()+",";
				   });
				   var may ='';  
				   var maycn ='';
				   $('input[name="may"]:checked').each(function(){  
					   may+=$(this).val()+","; 
					   maycn+=$(this).parent().text()+",";
				   });
				   $.post('${path}/system/inDefinition/upMetaAction.action',
						  {nameMust:must,nameMay:may,nameMustCN:mustcn,nameMayCN:maycn,pid:metaDataTermId,id:id},
						  function(data){
							  toMetaDataList(metaDataTermId);
							  $.closeFromInner();
					     }); 
			
		   }
			
			function toMetaDataList(metaDataTermId){
				parent.index_frame.work_main.location.href='${path}/system/inDefinition/metaDataList.action?pid='+ metaDataTermId;
			}
			
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
	            <input id="tijiao" type="button" value="提交" class="btn btn-primary" onclick="save();"/>
	            <input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	         </div>
		</div>
		<form:input path="definition.id" id="metaDataTermId" class="hidden" qType="long"/>
		<form:input path="id" id="id" class="hidden" qType="long"/>
		</div>
		</form:form>
	</div>
</body>
</html>