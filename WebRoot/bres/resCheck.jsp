<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">
function goBack(){
	$("#versionText").modal('hide');
}

function doCheck(decision){
	var libType=$('#libType').val();
	var codes = getChecked('data_div','objectId').join(',');
	var url =  '${path}/pubres/wf/doCheck.action?objectId=' + codes
				+'&decision='+decision
				+'&checkOpinion='+$('#checkOpinion').val()
				+"&operateFrom=MANAGE_PAGE";
	
	if (libType==1||libType==2||libType==3) {
		url = '${path}/res/wf/doCheck.action?objectId='+codes
		+'&decision='+decision
		+'&libType='+libType
		+"&operateFrom=MANAGE_PAGE";
	}
	var user = {checkOpinion:$('#checkOpinion').val()};
	var aMenu  = encodeURI(JSON.stringify(user));
	if(decision=='reject' && $("#checkOpinion").val()==''){
		$.alert('审核意见不能为空');
		return;
	}
	
	$("#versionText").modal('hide');
	$("#exportLeve").modal('hide');
	$.confirm('确定审核'+(decision=='approve'?'通过':'驳回')+'吗？', function(){
		$.ajax({
			url: url,
		    type: 'post',
		    data:"checkOpinion=" + aMenu,
		    datatype: 'text',
		    contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
		    success: function (destination) {
		    	$('#checkOpinion').val('');
		    	query();
		    }
		});
	});
}

function edit(){
	ids = $('#objectids').val();
	if(ids!=""){
		window.location.href = '${path}/publishRes/getExportExcel.action?ids='+ids+"&level="+$('input:radio[name="level"]:checked').val();
	}else{
		window.location.href = '${path}/publishRes/getExportExcel.action?publishType=${param.publishType}'+"&level="+$('input:radio[name="level"]:checked').val()+<app:ListPageParameterTag />;
	}
	$("#myModal").modal('hide');
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
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">导出级别:</h4>
      </div>
      <div class="modal-body">
       <input type="hidden" id="objectids" name="objectids" value=""/>
		<div class="form-group" >
			<div align="center">
				<label class="radio-inline">
					<input type="radio" name="level" value="1" checked="checked"/> 1级
				</label>
				<label class="radio-inline">
					<input type="radio" name="level" value="2"/> 2级
				</label>
				<label class="radio-inline">
					<input type="radio" name="level" value="3"/> 3级
				</label>
								<label class="radio-inline">
					<input type="radio" name="level" value="4"/> 4级
				</label>
								<label class="radio-inline">
					<input type="radio" name="level" value="5"/> 5级
				</label>
								<label class="radio-inline">
					<input type="radio" name="level" value="6"/> 6级
				</label>
								<label class="radio-inline">
					<input type="radio" name="level" value="7"/> 7级
				</label>
			</div>
		</div>
      </div>
      <div class="modal-footer">
      <button type="button" class="btn btn-primary" onclick="edit();">确定</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>
