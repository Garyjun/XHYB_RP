<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">
	$(function() {
		 $('#downSucess').hide();
		 $('#downFalse').hide();
		 $('#showClose').hide();
	    //根据传过来的ftpFlag设置http还是ftp下载默认选中
		//根据传过来的ftpFlag设置http还是ftp下载默认选中
	     if($('#ftpFlag').val()!=""){
	         $("input[name='downloadType']").eq(1).attr("checked","checked");
	         $("input[name='downloadType']").eq(0).removeAttr("checked");
	         document.getElementById("downloadType1").disabled =true;
	     }
	     document.getElementById("compress2").disabled =true;
	     $("#downloadType1").change(function() {
	    	 document.getElementById("compress2").disabled =true;
	    	 $("input[name='compress']").eq(0).attr("checked","checked");
	    	 document.getElementById("encryptPwd").setAttribute("readOnly",true);
	    	 $('#encryptPwd').val("");
	    	 document.getElementById("encryptPwd").readOnly = false;
	     })
	      $("#compress2").change(function() {
	    	 $('#encryptPwd').val("");
	    	 document.getElementById("encryptPwd").readOnly = true;
	     })
	     $("#compress1").change(function() {
	    	 document.getElementById("encryptPwd").readOnly = false;
	     })
	     $("#downloadType2").change(function() {
	    	 $('#encryptPwd').val("");
	    	 document.getElementById("compress2").disabled =false;
	     })
	});
	
	function checkSubmit(){
		var encry = $('#encryptPwd').val();
		if(encry.length>6){
			$.alert("密码长度必须小于或等于6位");
		}else{
			exportFile();
			//save();
		}
	}
	
	function exportFile(){
		//第一步：获取ids
		var starPage = "";
		var endPage = "";
		$('#starPage').val(1); //设置默认为：第一页开始
		starPage = $('#starPage').val();
		endPage = $('#endPage').val();
		if(starPage == null || starPage == ""){
			$.alert("请输入起始页");
			return;
		}
		if(endPage == null || endPage == ""){
			$.alert("请输入结束页");
			return;
		}
		
	  	//获取dataGrid每页显示的条数,getData参数为固定值
		var dataDiv = $('#data_div').datagrid('getData');
	    var pageSize = dataDiv.rows.length;  //获取每页显示的条数
		var total = dataDiv.total;   //获取dataGrid总条数
			
		//获取dataGrid每页显示的条数,getData参数为固定值
		var dataDiv = $('#data_div').datagrid('getData');
		var pageSize = dataDiv.rows.length;
		var total = dataDiv.total;   //获取dataGrid总条数
		var sumPage = "";
		if(total%pageSize==0){
			sumPage = total/pageSize;
		}else{
			sumPage = total/pageSize + 1;
		}
		if(endPage>sumPage){
			$.alert("结束页已经超过总页数，请重新输入");
		}else{
			saveFile(starPage,endPage,pageSize);
		}
	} 
	
 	function saveFile(starPage,endPage,pageSize){
 		var downType = "";
 		//获取下载方式1.http 2.ftp
 		var downloadType = $('input:radio[name="downloadType"]:checked').val();
 		var isComplete = $('input:radio[name="compress"]:checked').val();
 		var ftpHttpDate = {
 				publishType:'${param.publishType}',   								//资源类型
 				page:starPage,														//起始页
 				page1:endPage,														//结束页
 				size:parseInt(pageSize),											//当前页显示的总条数
 				queryModel:encodeURIComponent(getQueryParamString(_conditions)), 	//获取左侧以及二次查询的查询条件
         		isComplete:isComplete,												//是否压缩1.压缩 2.不压缩 
         		ftpFlag:downloadType,												//下载方式 1.http 2.ftp
         		encryptPwd:$('#encryptPwd').val()									//加密密钥
 	    };
 		 var ftpHttp = encodeURI(JSON.stringify(ftpHttpDate));
 		 
 		 //如果选择的是FTP(2)下载则不需要比较文件大小，如果选择的是http(1)下载方式则要比较系统参数与要下载的文件的大小
 		 if(downloadType == '1'){
 			//Loading = $.waitPanel('计算文件大小中，请稍后...',false);
 			//计算要下载的文件是否超过系统参数中设置的大小
 			$.ajax({
 				url:'${path}/publishRes/getFileSizePage.action',
 			    type: 'post',
 			    datatype: 'text',
 			    async : false,
 				data:"gotoFtpHttp=" + ftpHttp,
 			    success: function (returnValue) {
 			    	data = eval('('+returnValue+')');
 			    	downType = data;
 			    }
 			});
 			
 			//如果downType=1,表示要下载的资源文件超过系统参数中设置的大小，所以只能采用ftp下载
 			//Loading.close();
 			if(downType == '1'){
 				$.alert("文件大小超出规定范围，请选择FTP下载方式");
 				return;
 			}
 		 }
 		
 		if(downloadType=="1"){
			downResWaiting = $.waitPanel('正在打包中请稍候...',false);
		}
 		if(downloadType=="2"){
 			downLoading = $.waitPanel('下载中请稍候...',false);	
 		}
 		 $.ajax({
			url :'${path}/publishRes/downloadPublishResPage.action',
			type : 'post',
			async : true,
			data:"gotoFtpHttp=" + ftpHttp,
			success : function(data) {
				callbackFile(data);
				if(downloadType=="1"){
					downResWaiting.close();
				}
				if(downloadType=="2"){
					downLoading.close();
				}
			}
		});
	}
 	
	function callbackFile(data){
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
				//$('#showClose').show();
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
			 $.openWindow("${path}/publishRes/import/downloadFileDetail.jsp?publishType=${param.publishType}",'文件下载',300,200);
		}
	}
	
	function gotoDownSucess(){
		zipName = $('#zipName').val();
		zipName = encodeURI(encodeURI(zipName));
		window.location.href = "${path}/publishRes/downloadFile.action?zipName="+zipName;
	}
	
	function gotoDownFalse(){
		$.alert($('#zipName').val());
	}
	
	function totalRes(){
		var pageSize = 0;
		var starPage = $('#starPage').val();
		var endPage = $('#endPage').val();
		var dataDiv = $('#data_div').datagrid('getData');
	    pageSize = dataDiv.rows.length;  //获取每页显示的条数
		
		if(starPage!=""&&endPage!=""&&pageSize!=null){
			var totalValue = (parseInt(endPage)-parseInt(starPage)+1)*parseInt(pageSize);
			totalValue = "已选"+totalValue +"条数据";
			$('#total1').html(totalValue);
		}else if(page!=""&&page1==""&&pageSize!=null){
			$('#total1').html("");
		}
	}
</script>
<!-- Button trigger modal -->
<div class="modal fade" id="Modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-body">
       		<input type="hidden" id="objectids" name="objectids" value=""/>
       		<input type="hidden" id="ids" name="ids" value="${param.ids}"/>
			<input type="hidden" id="zipName" name="zipName" value=""/>
			<input type="hidden" id="ftpFlag" name="ftpFlag" value="${param.ftpFlag}"/>
			<a id="downSucess" href="javascript:void(0)" onclick="gotoDownSucess()" style='text-decoration:underline;'>附件打包成功,请下载</a>
			<a id="downFalse" href="javascript:void(0)" onclick="gotoDownFalse()" style='text-decoration:underline;'>附件打包失败,请查看失败原因</a>
			<div style="margin:5% 0 0 0">
				<input class="btn btn-primary" id="showClose" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
      		</div>
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
					 	<input id="downloadType1" type="radio" name="downloadType" value="1" checked="checked"/> Http下载&nbsp;&nbsp;&nbsp;&nbsp;
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
				<div class="col-md-3">
					 <div class="form-group">
					 <div class="col-md-5">
						<label>加密密钥：</label>
					</div>
					</div>
				</div>
				<div class="col-md-6">
				    <div class="form-group">
						 <div class="col-md-8">
		<!-- 				  validate[required,custom[cNOrEng]] -->
							<input type="password" name="encryptPwd" id="encryptPwd"/>
						</div>
						<div class="col-md-3">
							<label class="control-label">(密码只支持7位以内的字母或数字)</label>
						</div>
					</div>
				</div>
				<br></br>
				<div class="col-md-3">
					 <div class="form-group">
					 <div class="col-md-5">
						<label>页码范围：</label>
					</div>
					</div>
				</div>
				<div class="col-md-6">
				    <div class="form-group">
						 <div class="col-md-8">
						 		<input type="text" name="page" id="starPage" value="" size="9" onkeyup="totalRes();"/> 到：
								<input type="text" name="page1" id="endPage" value="" size="9" onkeyup="totalRes();"/>
								<label id="total1" class="radio-inline"></label>
						</div>
					</div>
				</div>
        	</div>
			
    
	      	<div class="modal-footer" align="center">
	      		<button type="button" class="btn btn-primary" onclick="checkSubmit();">确定</button>
	       	 	<button type="button" class="btn btn-primary" data-dismiss="modal">关闭</button>
	      	</div>
      </div>
    </div>
  </div>
</div>
