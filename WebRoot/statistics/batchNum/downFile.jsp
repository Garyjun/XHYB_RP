<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>列表</title>
<script type="text/javascript"
	src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/jQueryValidationEngine/js/languages/jquery.validationEngine-zh_CN.js"
	charset="utf-8"></script>
<style>
#flag {
	margin-bottom: 5px;
}
</style>
<script type="text/javascript"> 
//元数据提交
 $(function() {
	 $('#downSucess').hide();
	 $('#downFalse').hide();
	 $('#showClose').hide();
     $('#form').validationEngine('attach', {
		relative: true,
		overflownDIV:"#divOverflown",
		promptPosition:"bottomLeft",
		validateNonVisibleFields:true,
		validateDisplayNoneFields:false,
		showOneMessage:true,
		maxErrorsPerField:1,
		binded:true,
		onValidationComplete:function(form,status){
			if(status){
				var encry = $('#encryptPwd').val();
				if(encry.length>6){
					$.alert("密码长度必须小于或等于6位");
				}else{
					save();	
				}
			}
		}
	});
     //根据传过来的ftpFlag设置http还是ftp下载默认选中
     if($('#ftpFlag').val()!=""){
         $("input[name='downloadType']").eq(0).attr("checked","checked");
        // $("input[name='downloadType']").eq(0).removeAttr("checked");
         $('#downloadType2').disabled =true;
     }
     
     
     $('input[name="compress"]').change(function(){
			//1.表示压缩   2.表示不压缩
			var compareType = $('input[name="compress"]:checked').val();
			if(compareType == '1'){
				$('#encryptPwd').attr("disabled",false);
			}else if(compareType == '2'){
				$('#encryptPwd').val("");
				$('#encryptPwd').attr("disabled",true);
			}
		});
 });
 
 	function save(){
 		//获取下载方式
		var downloadType = $('input:radio[name="downloadType"]:checked').val();
 		//获取是否压缩
		var isComplete = $('input:radio[name="compress"]:checked').val();
 		
		
		 //获取选中的资源id
		 var codes = $('#ids').val();
         var ftpHttpDate = {
         		ids:codes,         						//选中的资源的id
         		isComplete:isComplete,   				//是否压缩
         		ftpFlag:$('#ftpFlag').val(),			//下载方式，2：ftp下载
         		encryptPwd:$('#encryptPwd').val()   	//获取加密密匙
 	    };
         
 		var ftpHttp = encodeURI(JSON.stringify(ftpHttpDate));
 		if(downloadType=="2"){
 			downLoading = $.waitPanel('下载中请稍候...',false);	
 		}
 		$.ajax({
			url :'${path}/publishRes/downloadPublishRes.action',
			type : 'post',
			async : true,
			data:"gotoFtpHttp=" + ftpHttp,
			success : function(data) {
				if(downloadType=="2"){
					downLoading.close();
				}
				callback(data);
			}
		});
	}
 	
	function callback(data){
		//更新节点名称
		var isType = "zip";
		var fileType = data.substring(data.lastIndexOf("/")+1,data.length);
		var flagDown = data.split(",");
// 		alert(data);
// 		alert(flagDown[0]);
		if(flagDown.length==2){
			if(flagDown.length>1){
				$('#zipName').val(flagDown[0]);
			}else{
				$('#zipName').val(data);
			}
			if(fileType.indexOf(isType)>=0){
				$('#show').hide();
				$('#downSucess').show();
				$('#showClose').show();
			}else if(flagDown[0]=="1"){
				$('#show').hide();
				$('#downSucess').show();
				$('#showClose').show();
			}else{
				$('#show').hide();
				$('#downFalse').show();
				$('#showClose').show();
			}
		}else{
			var resourceType = $('#publishType').val();
			 $.openWindow("${path}/publishRes/import/downloadFileDetail.jsp?publishType="+resourceType,'文件下载',300,200);
		}
	}
	function gotoDownSucess(){
		zipName = $('#zipName').val();
		zipName = encodeURI(encodeURI(zipName));
		window.location.href = "${path}/publishRes/downloadFile.action?zipName="+zipName;
// 		$.closeFromInner();
	}
	function gotoDownFalse(){
		$.alert($('#zipName').val());
	}
	
</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap ">
	<form:form action="${path}/publishRes/downloadPublishRes.action" id="form" class="form-horizontal" name="form"  modelAttribute="frmWord" method="post">
	<input type="hidden" id="ids" name="ids" value="${param.ids}"/>
	<input type="hidden" id="zipName" name="zipName" value=""/>
	<input type="hidden" id="ftpFlag" name="ftpFlag" value="${param.ftpFlag}"/>
	<input type="hidden" id="publishType" name="publishType" value="${param.publishType}"/>
	<a id="downSucess" href="javascript:void(0)" onclick="gotoDownSucess()" style='text-decoration:underline;'>附件打包成功,请下载</a>
	<a id="downFalse" href="javascript:void(0)" onclick="gotoDownFalse()" style='text-decoration:underline;'>附件打包失败,请查看失败原因</a>
	<div style="margin:5% 0 0 0">
	<input class="btn btn-primary" id="showClose" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	</div>
	<div id="show">
	<div class="row">
		<div class="col-md-3">
			 <div class="form-group">
			 <div class="col-md-5">
				<label>下载方式：</label>
			</div>
			</div>
		</div>
		<div class="col-md-6">
			 <div class="form-group">
			 <div class="col-md-8">
<!-- 			 	<input id="downloadType1" type="radio" name="downloadType" value="1" checked="checked"/> Http下载&nbsp;&nbsp;&nbsp;&nbsp; -->
				<input id="downloadType2" type="radio" name="downloadType" value="2" /> Ftp下载
			</div>
			</div>
		</div>
		<br></br>
		<div class="col-md-3">
			 <div class="form-group">
			 <div class="col-md-5">
				<label>是否压缩：</label>
			</div>
			</div>
		</div>
		<div class="col-md-6">
		    <div class="form-group">
				 <div class="col-md-8">
					<input id="compress1" type="radio" name="compress" value="1" checked="checked"/>是&nbsp;&nbsp;&nbsp;&nbsp;
					<input id="compress2" type="radio" name="compress" value="2" />否
				</div>
			</div>
		</div>
		<br></br>
		<div class="col-md-6">
		    <div class="form-group">
			 	<label class="control-label col-md-4">加密密钥：</label>
				 <div class="col-md-8">
<!-- 				  validate[required,custom[cNOrEng]] -->
					<input type="password" name="encryptPwd" id="encryptPwd"  class="form-control"/>
				</div>
			</div>
		</div>
		<div class="col-md-3">
		    <div class="form-group">
				 <div class="col-md-5">
					<label class="control-label">(密码只支持7位以内的字母或数字)</label>
				</div>
			</div>
		</div>
	</div>
		<div align="center">
           	<input  type="submit" value="确定" class="btn btn-primary red"/>
           	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
    	</div>
    </div>
	</form:form>
</div>
</body>
</html>