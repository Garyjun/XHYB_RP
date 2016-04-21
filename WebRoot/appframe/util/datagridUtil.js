/**
 *自适应表格的宽度处理(适用于Jquery Easy Ui中的dataGrid的列宽),
 *注：可以实现列表的各列宽度跟着浏览宽度的变化而变化，即采用该方法来设置DataGrid
 *的列宽可以在不同分辨率的浏览器下自动伸缩从而满足不同分辨率浏览器的要求
 *使用方法：(如:{field:'ymName',title:'编号',width:fillsize(0.08),align:'center'},)
 *
 *@param 
 *percent当前列的列宽所占整个窗口宽度的百分比(以小数形式出现，如0.3代表30%)
 *borderSize 宽度要减去冻结列（序号和checkbox列，暂时这两列宽度为54px,如果有值，则采用传入值）
 *@return通过当前窗口和对应的百分比计算出来的具体宽度
 */
function fillsize(percent, borderSize) {
	var bodyWidth = document.body.clientWidth;
	if (typeof (borderSize) == 'undefined') {
		borderSize = 54 + 3;//多减去 3 px 边
	}
	bodyWidth = bodyWidth - borderSize;
	return Math.floor((bodyWidth) * percent);//为了不出现小数像素值
}
/**
 * @param percent 当前列的窗口所占整个窗口高度的百分比(以小数形式出现，如0.3代表30%)
 * @returns 高度像素值
 */
function fillHeight(percent) {
	var bodyHeight = document.body.clientHeight;
	return bodyHeight * percent;
}
/**
 *刷新DataGrid列表(适用于Jquery Easy Ui中的dataGrid)
 *注：建议采用此方法来刷新DataGrid列表数据(也即重新加载数据)
 *@param dataTableId将要刷新数据的DataGrid依赖的table列表id
 */
function freshDataTable(dataTableId) {
	var _pageNumber = $('#' + dataTableId + '').datagrid('options').pageNumber;
	$('#' + dataTableId + '').datagrid('getPager').pagination({
		pageNumber : _pageNumber
	});
	$('#' + dataTableId + '').datagrid('reload');
	//刷新后清空所有选中
	clearSelect(dataTableId);
}
function linkClick(linkObject) {
	var formObject = document.createElement('form');
	document.body.appendChild(formObject);
	formObject.setAttribute('method', 'post');
	var url = linkObject.href;
	var uri = '';
	var i = url.indexOf('?');

	if (i == -1) {
		formObject.action = url;
	} else {
		formObject.action = url.substring(0, i);
	}

	if (i >= 0 && url.length >= i + 1) {
		uri = url.substring(i + 1, url.length);
	}

	var sa = uri.split('&');

	for ( var i = 0; i < sa.length; i++) {
		var isa = sa[i].split('=');
		var inputObject = document.createElement('input');
		inputObject.setAttribute('type', 'hidden');
		inputObject.setAttribute('name', isa[0]);
		inputObject.setAttribute('value', isa[1]);
		formObject.appendChild(inputObject);
	}

	formObject.submit();

	return false;
}
/**
 *取消DataGrid中的行选择(适用于Jquery Easy Ui中的dataGrid)
 *注意：解决了无法取消"全选checkbox"的选择,不过，前提是必须将列表展示
 *数据的DataGrid所依赖的Table放入html文档的最全面，至少该table前没有
 *其他checkbox组件。
 *
 *@paramdataTableId将要取消所选数据记录的目标table列表id
 */
function clearSelect(dataTableId) {
	$('#' + dataTableId).datagrid('uncheckAll');
}
/**
 *获取被选中的行
 *cid 为 容器的id，及div的id
 *id 为要获取的字段的id
 *返回 array ，如果需要字符串，请自行调用join(',')
 */
function getChecked(cid, id) {
	var ids = new Array();
	var rows = $('#' + cid + '').datagrid('getChecked');
	for ( var i = 0; i < rows.length; i++) {
		ids.push(eval('rows[i]' + '.' + id));
	}
	return ids;
}

/**
 * fengda2015年8月20日
 * 获取被选中行的下标
 * @param cid
 * @returns {Array}
 */
function getCheckedIndex(cid) {
	var ids = new Array();
	var rows = $('#' + cid + '').datagrid('getChecked');
	for ( var i = 0; i < rows.length; i++) {
		var s = $('#'+cid).datagrid('getRowIndex',rows[i]);
		ids.push(s);
	}
	return ids;
}


function resizeDt(dataTableId) {
	if(typeof(dataTableId) != 'undefined'){
		$t = $('#'+dataTableId).parent().parent().parent();
	}else{
		$t = $('.datagrid_mark').parent().parent().parent();
	}
	$p = $t.parent();
	//获取div高度
	var _width = $p.width();
	//计算高度
	var prevHt = 0;
	
	$t.prevAll().each(function(){
		prevHt += $(this).outerHeight();
	});
	
	//算出padding
	var padt = $t.css('padding-top');
	padt = padt.replace('PX','');
	padt = padt.replace('px','');
	var padb = $t.css('padding-bottom');
	padb = padb.replace('PX','');
	padb = padb.replace('px','');
	
	var padding = Number(padt) + Number(padb);
	
	var _height = $p.height() - prevHt - padding;
	if(typeof(dataTableId) != 'undefined'){
		$('#' + dataTableId).datagrid('resize', {
			width :_width,
			height : _height
		});
	}else{
		$('.datagrid_mark').datagrid('resize', {
			width :_width,
			height : _height
		});
	}
}
function resizeFrameDt(status){
	document.getElementById('work_main').contentWindow.resizeDt();
}
function selfCallBack(methodName,data) {
	try {
		if(typeof(data) != 'undefined'){
			data = JSON.stringify(data);
		}
		this.func = new Function(methodName + "('"+data+"');");
	} catch (e) {
		alert("找不到方法");
	}
}
//格式化操作列，超过3个操作显示更多
function createOpt(optArray){
	var opts = '';
	var menus = '';
	var len = optArray.length - 1;
	if(len <= 2){
		opts = optArray.join('');
	}else{
		var tempId = Math.uuid();
		opts = optArray[0];
		opts += '<div class="dropdown" style="display:inline;">';
		opts += '	<a href="javascript:void(0);" onclick="showDropDownMenu(\''+tempId+'\',this)" class="dropdown-toggle" data-toggle="dropdown">更多操作<b class="caret"></b></a>';
		menus += '<ul class="dropdown-menu" style="min-width:90px;" id="'+tempId+'">';
		for(var i = 1;i <= len;i ++){
			menus += '	<li>'+optArray[i]+'</li>';
		}
		menus += '</ul>';
		opts += '</div>';
	}
	$('body').append(menus);
	return opts;
}
function showDropDownMenu(id,obj){
	$('.dropdown-menu:visible').hide();
	var ulObj = $('#'+id);
	ulObj.css('top',$(obj).offset().top+15);
	ulObj.css('left',$(obj).offset().left-17);
	ulObj.show();
	ulObj.bind('mouseleave',function(){ulObj.hide();});
	ulObj.bind('mouseover',function(){ulObj.show();});
	//$(obj).bind('mouseout',function(){ulObj.hide(2000);});
}