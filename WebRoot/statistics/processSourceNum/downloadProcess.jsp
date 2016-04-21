<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">
	function exportExcel(){
		
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
		
		//获取dataGrid每页显示的条数
		var dataDiv = $('#data_div').datagrid('getData');
		var pageSize = dataDiv.rows.length;
		
		var ids = "";				
		var resName = $('#resName').val();
		var status = $('#status').val();
		var taskName = $('#taskName').val();
		var processName = $('#processName').val();
		var startTime = $('#startTime').val();
		var endTime = $('#endTime').val();
		var total = dataDiv.total;   //获取dataGrid总条数
		var sumPage = "";
		if(total%pageSize==0){
			sumPage = total/pageSize;
		}else{
			sumPage = total/pageSize + 1;
		}
		
		if(page1>sumPage){
			$.alert("结束页已经超过总页数，请重新输入");
		}else{
			downLoading = $.waitPanel('下载中请稍候...',false);
			$.ajax({
				url:'${path}/statistics/sourceProcessNum/getExportByPage.action',
			    type: 'post',
			    datatype: 'json',
			    data: {
			    	resName:resName,
			    	status:status,
			    	taskName:taskName,
			    	processName:processName,
			    	startTime:startTime,
			    	endTime:endTime,
			    	pageSize : pageSize,
			    	page:page,
			    	page1:page1
			    },
			     success: function (returnValue) {
			    	if(returnValue!=""){
			    		downLoading.close();
			    		window.location.href = '${path}/statistics/sourceProcessNum/getExportExcelDown.action?excelFilePath='+returnValue;
			    	}else{
			    		$.alert("文件下载数量超过数据字典定义大小，不能下载");
			    		downLoading.close();
			    	}
			    } 
			}); 
		    $("#myModal").modal('hide');
		}  
	}
	 
  

	function totalRes(){
		var pageSize = 0;
		var page = $('#page').val();
		var page1 = $('#page1').val();
		//获取dataGrid每页显示的条数
		var dataDiv = $('#data_div').datagrid('getData');
		var pageSize = dataDiv.rows.length;
		
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
