<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">
	function exportExcel(){
		//第一步：获取ids
		var ids = $('#objectids').val();
		var page = "";
		var page1 = "";
		if(ids == null || ids == ""){
			//或者页面
			$('#page').val(1); //设置默认为：第一页开始
			page = $('#page').val();
			page1 = $('#page1').val();
			if(page == null || page == ""){
				$.alert("请输入起始页");
				return;
			}
			if(page1 == null || page1 == ""){
				$.alert("请输入结束页");
				return;
			}
		}
		
		//获取导出级别
		var level="";
	    $("input[name='level']:checkbox").each(function(){ 
	        if($(this).attr("checked")){
	        	level += $(this).val()+","
	        }
	    })
	    if(level.length>0){
	    	level = level.substring(0,level.length-1);
	    }else{
	    	$.alert("请选择导出级别");
	    	return;
	    }
	    
	    
	  //获取dataGrid每页显示的条数,getData参数为固定值
		var dataDiv = $('#data_div').datagrid('getData');
	    var pageSize = dataDiv.rows.length;  //获取每页显示的条数
		var total = dataDiv.total;   //获取dataGrid总条数
	    
		//获取页面上输入的查询条件组合,和在查询条件中输入的值
		var params = getAllQueryConditions();
		params = encodeURI(params);
		
		var searchParamCa = {
			publishType:$('#publishType').val(),
			page:page,
			page1:page1,
			level:level,
			size:parseInt(pageSize),
			metadataMap:params,
			batch:"1"                     //标识为查询统计处的导出，为了区分与资源管理不同处的导出，有逻辑不同
        };
		var paramCa  = encodeURI(JSON.stringify(searchParamCa));
		
			
			
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
		if(page1>sumPage){
			$.alert("结束页已经超过总页数，请重新输入");
		}else{
			downLoading = $.waitPanel('下载中请稍候...',false);	
			$.ajax({
				url:'${path}/acrossStoreSearch/getExportExcel.action',
			    type: 'post',
			    datatype: 'json',
			    data:"searchParamCa=" + paramCa,
			    success: function (returnValue) {
			    	if(returnValue!=""){
			    		downLoading.close();
			    		window.location.href = '${path}/acrossStoreSearch/getExportExcelDown.action?excelFilePath='+returnValue;
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
       		<div class="row">
       			<div class="col-md-3">
					 <div class="form-group">
					 	<div class="col-md-5">
							<label>导出级别：</label>
						</div>
					</div>
				</div>
				<div class="col-md-6">
					 <div class="form-group">
						 <div class="col-md-8">
							<input type="checkbox" name="level" value="1" checked="checked"/> 1级
							<input type="checkbox" name="level" value="2"/> 2级
							<input type="checkbox" name="level" value="3"/> 3级
							<input type="checkbox" name="level" value="4"/> 4级
							<input type="checkbox" name="level" value="5"/> 5级
							<input type="checkbox" name="level" value="6"/> 6级
							<input type="checkbox" name="level" value="7"/> 7级
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
						 		<input type="text" name="page" id="page" value="" size="4" onkeyup="totalRes();"/> 到：
								<input type="text" name="page1" id="page1" value="" size="4" onkeyup="totalRes();"/>
								<label id="total1" class="radio-inline"></label>
						</div>
					</div>
				</div>
         </div>
      	<div class="modal-footer" align="center">
     		 <button type="button" class="btn btn-primary" onclick="exportExcel();">确定</button>
        	 <button type="button" class="btn btn-primary" data-dismiss="modal">关闭</button>
      	</div>
   </div>
 </div>
</div>
</div>
