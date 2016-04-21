<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">

	$(function(){
		//当选择是否压缩选为否时，那么密匙项不允许添加，不压缩不支持加密    fengda 2015年12月1日
		$('input[name="compress"]').change(function(){
			var compressType = $('input[name="compress"]:checked').val();
			if(compressType == '1'){
				$('#encryptPwd').attr("disabled",false);
			}else if(compressType == '2'){
				$('#encryptPwd').val("");
				$('#encryptPwd').attr("disabled",true);
			}
		});
	})

	//提交下载，只支持ftp下载
	function save(page,page1,pageSize){
		//获取下载方式
		var downloadType = $('input:radio[name="downloadType"]:checked').val();
		//获取是否压缩
		var isComplete = $('input:radio[name="compress"]:checked').val();
		var publishType= $('#publishType').val();
		
        var ftpHttpDate = {
       			page:page,
    			page1:page1, 
    			publishType:publishType,
        		isComplete:isComplete,   				//是否压缩
        		size:parseInt(pageSize),
        		encryptPwd:$('#encryptPwd').val()   	//获取加密密匙
	    };
        
		var ftpHttp = encodeURI(JSON.stringify(ftpHttpDate));
		if(downloadType=="2"){
 			downLoading = $.waitPanel('下载中请稍候...',false);	
 		}
		$.ajax({
			url :'${path}/publishRes/downloadPageResource.action?ftpFlag=2',
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
//		$.closeFromInner();
	}
	function gotoDownFalse(){
		$.alert($('#zipName').val());
	}
	
	
	function totalRes(){
		var pageSize = 0;
		var starPage = $('#starPage').val();
		var endPage = $('#endPage').val();
//		var foo= $("select[class='pagination-page-list']"); 
//		$(foo).each(function() {
//			pageSize = this.value;
//		})	
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
	
	
	
	function checkSubmit(){
		var encry = $('#encryptPwd').val();
		if(encry.length>6){
			$.alert("密码长度必须小于或等于6位");
		}else{
			//save();
			exportFile();
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
			save(starPage,endPage,pageSize);
		}
} 
</script>


<!-- Button trigger modal -->
<div class="modal fade" id="Resource" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-body">
       	<input type="hidden" id="ids" name="ids" value="${param.ids}"/>
		<input type="hidden" id="zipName" name="zipName" value=""/>
		<input type="hidden" id="ftpFlag" name="ftpFlag" value="${param.ftpFlag}"/>
		<input type="hidden" id="publishType" name="publishType" value="${param.publishType}"/>
<!-- 		<a id="downSucess" href="javascript:void(0)" onclick="gotoDownSucess()" style='text-decoration:underline;'>附件打包成功,请下载</a> -->
<!-- 		<a id="downFalse" href="javascript:void(0)" onclick="gotoDownFalse()" style='text-decoration:underline;'>附件打包失败,请查看失败原因</a> -->
<!-- 		<div style="margin:5% 0 0 0"> -->
<!-- 			<input class="btn btn-primary" id="showClose" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/> -->
<!-- 		</div> -->
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
						<input id="downloadType2" type="radio" name="downloadType" value="2" checked="checked"/> Ftp下载
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
							<input id="compress2" type="radio" name="compress" value="2"/>否
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
				
<!-- 				<div class="col-md-12"> -->
<!-- 			    	<div class="form-group" > -->
<!-- 						<label class="col-md-12">页数： -->
<!-- 						<input type="text" name="page" id="starPage" value="" size="9" onkeyup="totalRes();"/> 到： -->
<!-- 						<input type="text" name="page1" id="endPage" value="" size="9" onkeyup="totalRes();"/> -->
<!-- 						<label id="total1" class="radio-inline"></label> -->
<!-- 						</label> -->
<!-- 					</div> -->
<!-- 			  </div> -->
			</div>
      
      	<div class="modal-footer" align="center">
      		<button type="button" class="btn btn-primary" onclick="checkSubmit();">确定</button>
       		 <button type="button" class="btn btn-primary" data-dismiss="modal">关闭</button>
      	</div>
      </div>
    </div>
  </div>
</div>
</div>
