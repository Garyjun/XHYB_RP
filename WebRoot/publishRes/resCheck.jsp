<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">
function goBack(){
	$("#versionText").modal('hide');
}
function doCheck(decision){
	var codes = getChecked('data_div','objectId').join(',');
	var url =  '${path}/pubres/wf/doCheck.action';
	if(decision=='reject' && $("#checkOpinion").val()==''){
		$.alert('审核意见不能为空');
		return;
	}
	$("#versionText").modal('hide');
	$("#exportLeve").modal('hide');
	$.confirm('确定审核'+(decision=='approve'?'通过':'驳回')+'吗？', function(){
		doChecking = $.waitPanel('正在审核中请稍候...',false);
		var check = {
				ids:codes,
				status:$('#status').val(),
				operateFrom:"MANAGE_PAGE",
				decision:decision,
				checkOpinion: $("#checkOpinion").val()
	    };
		var doCheck  = encodeURI(JSON.stringify(check));
		$.ajax({
			url: url,
		    type: 'post',
		    data:"doCheck=" + doCheck,
		    datatype: 'text',
		    contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
		    success: function (destination) {
		    	$('#checkOpinion').val('');
		    	doChecking.close();
		    	query();
		    }
		});
	});
}

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
	    var pageSize = dataDiv.rows.length;
		var total = dataDiv.total;   //获取dataGrid总条数
    	
		var searchParamCa = {
				ids:ids,
				level:level,
				publishType:'${param.publishType}',
				page:page,
				page1:page1,
				size:parseInt($('#pageSize').val()),
				status:$('#status').val(),
				targetNames:targetNames,
				targetField:targetField,
				queryModel:encodeURIComponent(getQueryParamString(_conditions))
	        };
		
		var paramCa  = encodeURI(JSON.stringify(searchParamCa));
		
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
	    	$("#myModal").modal('hide');
		}
}  
	  
	    
	
	
    
    
   
    
    
// 	if(ids!=""){
// 		var searchParamCa = {
// 				level:level,
// 				ids:ids,
// 				status:status
// 	        };
// 		var paramCa  = encodeURI(JSON.stringify(searchParamCa));
// 		$.ajax({
// 			url:'${path}/publishRes/getExportExcel.action',
// 		    type: 'post',
// 		    datatype: 'json',
// 		    data:"searchParamCa=" + paramCa,
// 		    success: function (returnValue) {
// // 		    	parent.queryForm();
// // 		    	data = eval('('+returnValue+')');
// // 		    	if(data.boo==1){
// // 		    		$.alert("大小超过文件下载限制!");
// // 		    	}else{
// // 		    		$.openWindow("${path}/publishRes/import/downFileEncryptPwd.jsp?publishType=${param.publishType}"+"&ids="+codes,'文件加密',500,150);
// // 		    	}
// 		    }
// 		});
// // 		window.location.href = '${path}/publishRes/getExportExcel.action?publishType=${param.publishType}&ids='+ids+"&level="+level+"&status="+$('#status').val();
// 	}else{
// 		//alert('${path}/publishRes/getExportExcel.action?publishType=${param.publishType}'+"&page="+$('#page').val()+"&page1="+$('#page1').val()+"&pageSize="+$('#pageSize').val()+"&status="+$('#status').val()+"&queryFlag=1&level="+level + "&" + getQueryParamString(_conditions));
// 		//window.location.href = '${path}/publishRes/getExportExcel.action?;
// 	}
// 	$("#myModal").modal('hide');
//  }


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
<div class="modal fade" id="versionText" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog ">
    <div class="modal-content col-xs-9"  >
			<div class="modal-body ">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		        <h4 class="modal-title" id="myModalLabel">审核意见：</h4>
				<textarea id="checkOpinion" name="checkOpinion"
					class="form-control" rows="5" ></textarea>
					<div align="center"><br />
				<button type="button" class="btn btn-lg red" onclick="doCheck('approve');">通过</button>
				<button type="button" class="btn btn-lg red" onclick="doCheck('reject');">驳回</button>
				<button type="button" class="btn btn-lg blue" onclick="goBack();">关闭</button></div>
			</div>
		</div>
	</div>
</div>

<!-- Button trigger modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <!-- <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">导出级别:</h4>
      </div> -->
      <div class="modal-body">
       <input type="hidden" id="objectids" name="objectids" value=""/>
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
			<!-- <div align="center">
				<label class="radio-inline">
					<input type="checkbox" name="level" value="1" checked="checked"/> 1级
				</label>
				<label class="radio-inline">
					<input type="checkbox" name="level" value="2"/> 2级
				</label>
				<label class="radio-inline">
					<input type="checkbox" name="level" value="3"/> 3级
				</label>
								<label class="radio-inline">
					<input type="checkbox" name="level" value="4"/> 4级
				</label>
								<label class="radio-inline">
					<input type="checkbox" name="level" value="5"/> 5级
				</label>
								<label class="radio-inline">
					<input type="checkbox" name="level" value="6"/> 6级
				</label>
								<label class="radio-inline">
					<input type="checkbox" name="level" value="7"/> 7级
				</label>
			</div> -->
			<!-- <div class="form-group" align="center">
				<label class="radio-inline">页数：<input type="text" name="page" id="page" value="" size="4" onkeyup="totalRes();"/> 到：<input type="text" name="page1" id="page1" value="" size="4" onkeyup="totalRes();"/><label id="total" class="radio-inline"></label></label>
			</div> -->
    
	      <div class="modal-footer" align="center">
	      		<button type="button" class="btn btn-primary" onclick="exportExcel();">确定</button>
	       	 <button type="button" class="btn btn-primary" data-dismiss="modal">关闭</button>
	      </div>
        </div>
    </div>
  </div>
</div>
