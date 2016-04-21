function modelSelect(orderId, operateFrom){
	$.openWindow(_appPath+"/resOrder/modelSelect.action?orderId="+orderId+"&operateFrom="+operateFrom,'模板选择',800,500);
}
function modelDetail(opFrom){
	var templateId = document.getElementById("templateId").value;
	if(templateId==""||templateId==null){
		$.alert("请选择模板后再查看详细信息！");
		return false;
	}
//	alert(opFrom);
	//$.openWindow(_appPath+"/resRelease/select.action?id="+templateId+"&opFrom="+opFrom,'模板详细信息',800,500);
	$.openWindow(_appPath+"/resRelease/orderPublishTemplate/detail.action?id="+templateId+"&opFrom=resTemp",'模板详细信息',800,500);
	
}

function clearModelValue(){
	$("#templateName").attr('value','');
	$("#templateId").attr('value','');
	$("#templateType").attr('value','');
	return false;
}