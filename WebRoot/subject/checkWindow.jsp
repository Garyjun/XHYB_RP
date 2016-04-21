<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">
function goBack(){
	$("#versionText").modal('hide');
}

function doCheck(decision){
	var libType=$('#libType').val();
	var codes = getChecked('data_div','id').join(',');
	if(codes==""){
		$.alert("请选择要审核的主题库！");
	}
	var pre_url = "";
	if(decision=="reject"&&$('#auditMsg').val()==""){
		$("#requiredAuditRemark").show();
		$.alert("审核驳回，请填写审核意见！");
		return;
	}
		var auditMsg = $('#auditMsg').val();
		if(auditMsg!=null&&auditMsg!=undefined){
			auditMsg = encodeURI(encodeURI(auditMsg));
		}
		
	pre_url = '${path}/subject/doCheck.action';
	var request_url = pre_url + '?objectId='+codes
			+'&decision='+decision
			+'&auditMsg='+auditMsg
			+'&libType='+libType
			+"&operateFrom=MANAGE_PAGE";
	$("#versionText").modal('hide');
	$.confirm('确定审核'+(decision=='approve'?'通过':'驳回')+'吗？', function(){
		$.ajax({
			url: request_url,
		    type: 'post',
		    datatype: 'text',
		    contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
		    success: function (destination) {
		    	$('#auditMsg').val('');
		    	location.href = "${path}"+destination;
		    }
		});
	});
		
}
</script>
<div class="modal fade" id="versionText" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog ">
    <div class="modal-content col-xs-9"  >
			<div class="modal-body ">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		        <h4 class="modal-title" id="myModalLabel">
		        	<i id="requiredAuditRemark" style="display:none;" class="must">*</i>审核意见：</h4>
				<textarea id="auditMsg" name="auditMsg"
					class="form-control" rows="5" ></textarea>
					<div align="center"><br />
				<button type="button" class="btn btn-lg red" onclick="doCheck('approve');">通过</button>
				<button type="button" class="btn btn-lg red" onclick="doCheck('reject');">驳回</button>
				<button type="button" class="btn btn-lg blue" onclick="goBack();">关闭</button></div>
			</div>
		</div>
	</div>
</div>