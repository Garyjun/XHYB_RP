<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">
	function exportExcel(){
		var dataDiv = $('#data_div').datagrid('getData');
		var page = $('#page').val();
		var page1 = $('#page1').val();
		if(page == ""){
			$.alert("请输入下载起始页！");
			return;
		}
		if(page1 == ""){
			$.alert("请输入下载结束页！");
			return;
		}
		var pageSize = dataDiv.rows.length;
		var resName = $('#resName').val();
		var userName = $('#userName').val();
		var taskName = $('#taskName').val();
		var publishType = $('#publishType').val();
		$.get("${path}/taskProcess/getMetaDataIdsByPage.action?page="+page+"&page1="+page1+"&pageSize="+pageSize +"&resName=" + resName 
					+ "&userName=" +userName + "&taskName=" + taskName + "&publishType=" + publishType,function(data){
 			if(data&&data.length>0){
 				var searchParamCa = {
 					ids:data,
		        };
				
				var paramCa  = encodeURI(JSON.stringify(searchParamCa));
			 				
 				downLoading = $.waitPanel('下载中请稍候...',false);	
 				$.ajax({
 					url:'${path}/publishRes/getExportExcel.action',
 				    type: 'post',
 				    datatype: 'json',
 				    data:"searchParamCa=" + paramCa,
 				    success: function (returnValue) {
 				    	if(returnValue!=""){
 				    		downLoading.close();
 				    		window.location.href = '${path}/publishRes/getExportExcelDown.action?excelFilePath='+returnValue;
 				    	}else{
 				    		$.alert("文件下载数量超过数据字典定义大小，不能下载");
 				    		downLoading.close();
 				    	}
 				    }
 				});
 				
 				
 			}else{
 				$.alert("当前没有数据！");
 			}
 		});
		
	    $("#myModal").modal('hide');
	}   
  

	function totalRes(){
		var pageSize = 0;
		var page = $('#page').val();
		var page1 = $('#page1').val();
		var foo= $("select[class='pagination-page-list']"); 
		$(foo).each(function() {
			pageSize = this.value;
		})	
		
		if(page!=""&&page1!=""&&pageSize!=null){
			var totalValue = (parseInt(page1)-parseInt(page)+1)*parseInt(pageSize);
			totalValue = "已选"+totalValue +"条数据";
			$('#total').html(totalValue);
		}else if(page!=""&&page1==""&&pageSize!=null){
			$('#total').html("");
		}
	}
	
</script>


<!-- Button trigger modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-body">
       <input type="hidden" id="objectids" name="objectids" value=""/>
       <input type="hidden" id="publishType" name="publishType" value=""/>
		<div class="form-group" align="center">
		<label class="radio-inline">页数：<input type="text" name="page" id="page" value="" size="4" onkeyup="totalRes();"/> 到：<input type="text" name="page1" id="page1" value="" size="4" onkeyup="totalRes();"/>
		<label id="total" class="radio-inline"></label>
		</label>
		</div>
      </div>
      <div class="modal-footer">
      <button type="button" class="btn btn-primary" onclick="exportExcel();">确定</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>
