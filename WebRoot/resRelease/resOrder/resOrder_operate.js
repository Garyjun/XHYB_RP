/**
 * 资源：上报
 * 
 * @param objectId
 */
function doApply(objectId,posttype) {
	$.ajax({
		url : _appPath +"/resOrder/canApply.action?orderId="+objectId+"&posttype="+posttype,
		type : "post",
		datatype : "text",
		success : function(data){
			if(data=="N"){
				$.alert("该需求单没有添加资源，无法上报！");
			}else{
				$.confirm('确定要上报吗？', function() {
					var _url = "";
					//if(platformId==1)
						//_url = _appPath + '/resOrder/doApply.action?objectId=' + objectId;
					//else
						//_url = _appPath + '/pubresOrder/pubresOrderWorkFlow/doApply.action?objectId=' + objectId;
					_url = _appPath + '/resOrder/workFlowDes.action?objectId=' + objectId +"&posttype="+posttype;
					$.ajax({
						url : _url,
						type : 'post',
						datatype : 'text',
						success : function(returnValue) {
							$.alert(returnValue);
							query();
						}
					});
				});
			}
		}
		
	});
}

/**
 * 资源：审核
 * 
 * @param objectId
 */
function gotoCheck(objectId,platformId,posttype) {
//	alert(_appPath
//			+ '/resOrder/resOrderWorkFlow/gotoCheck.action?objectId='+ objectId);
	var _url = "";
	//if(platformId==1)
		_url =  _appPath + '/resOrder/resOrderWorkFlow/gotoCheck.action?objectId='+ objectId + '&operateFrom=MANAGE_PAGE&posttype='+posttype;
	/*else
		_url =  _appPath + '/pubresOrder/pubresOrderWorkFlow/gotoCheck.action?objectId='+ objectId + '&operateFrom=MANAGE_PAGE';*/
	window.location.href = _url;
//	window.location.href = _appPath
//			+ '/resOrder/resOrderWorkFlow/gotoCheck.action?objectId='+ objectId;
}

/**
 * 资源：状态描述
 */
$statusDesc = function(value, rec) {
	return statusMap[rec.commonMetaData.commonMetaDatas.status];
};

var statusMap;
function getStatusMap() {
	$.ajax({
		url : _appPath + '/resOrder/getStatusMap.action',
		type : 'post',
		async : false,
		success : function(data) {
			statusMap = eval('(' + data + ')');
		}
	});
}
/**
 * 资源发布
 * 
 * @param objectId
 */
function publish(objectId,platformId) {
	$.confirm('确定要所发布吗？', function() {
		var _url = "";
		if(platformId==1)
			_url = _appPath + '/resOrder/publish.action?objectId=' + objectId;
		else
			_url = _appPath + '/resOrder/publish.action?objectId=' + objectId;
		$.ajax({
			//url : _appPath + '/resOrder/publish.action?objectId=' + objectId,
			url : _url,
			type : 'post',
			datatype : 'text',
			success : function(returnValue) {
				query();
			}
		});
	});
}
