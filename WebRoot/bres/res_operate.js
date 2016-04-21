/**
 * 资源：上报
 * 
 * @param objectId
 * @param libType
 */
function doApply(objectId, libType) {
	var url = _appPath + '/pubres/wf/goDoApply.action';
	if (libType) {
		url = _appPath + '/res/wf/doApply.action?objectId=' + objectId+ '&libType=' + libType;
	}
	
	var apply = {
			ids:objectId,
    };
	var doApply = encodeURI(JSON.stringify(apply));
	$.confirm('确定要上报吗？', function() {
		doApplying = $.waitPanel('上报中请稍候...',false);
		$.ajax({
			url : url,
			type : 'post',
			datatype : 'text',
			data:"doApply=" + doApply,
			success : function(msg) {
				doApplying.close();
				query();
			}
		});
	});
}

/**
 * 资源：审核
 * 
 * @param objectId
 */
function gotoCheck(objectId,libType) {
	var url = _appPath+ '/res/wf/gotoCheck.action?libType='+$("#libType").val()+'&objectId='+ objectId + '&operateFrom=MANAGE_PAGE'+'&queryType='+$('#queryType').val()+"&status="+$('#status').val();
	if (!libType) {
		url = _appPath+ '/pubres/wf/gotoCheck.action?objectId='+ objectId + '&operateFrom=MANAGE_PAGE'+"&status="+$('#status').val();
	}
	window.location.href = url;
}

/**
 * 资源：状态描述
 */
$statusDesc = function(value, rec) {
	return statusMap[rec.status];
};

var statusMap;
function getStatusMap() {
	$.ajax({
		url : _appPath + '/res/wf/getStatusMap.action',
		type : 'post',
		async : false,
		success : function(data) {
			statusMap = eval('(' + data + ')');
		}
	});
}
/**
 * 资源下线
 * 
 * @param objectId
 */
function offlineRes(objectId,libType) {
	$.confirm('确定要下线所选资源吗？', function() {
		$.ajax({
			url : _appPath + '/res/wf/offlineRes.action?objectId=' + objectId,
			type : 'post',
			success : function(success){
				 $.alert("下线成功！");
				query();
			}
		});
	});
}


/**
 * 出版资源下线
 * 
 * @param objectId
 */
function PubOfflineRes(objectId,libType) {
	$.confirm('确定要下线所选资源吗？', function() {
		$.ajax({
			url : _appPath + '/pubres/wf/PubOfflineRes.action?objectId=' + objectId,
			type : 'post',
			success : function(success){
				 $.alert("下线成功！");
				query();
			}
		});
	});
}
