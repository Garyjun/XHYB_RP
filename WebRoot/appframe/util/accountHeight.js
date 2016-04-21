/**
 * 计算剩余高度，为设置 height_remain 的元素计算高度，必须包含在fakeFrame中
 */
function accountHeight(){
	$('#fakeFrame').find('.height_remain').each(function(){
		//先获取父元素高度，然后减去之前的所有同辈元素
		//prevAll()
		var pHeight = $(this).parent().height();
		var prevHt = 0;
		$(this).prevAll().each(function(){
			prevHt += $(this).outerHeight();
		});
		
		//算出padding
		var padt = $(this).css('padding-top');
		padt = padt.replace('PX','');
		padt = padt.replace('px','');
		var padb = $(this).css('padding-bottom');
		padb = padb.replace('PX','');
		padb = padb.replace('px','');
		
		var padding = Number(padt) + Number(padb);
		//设置高度
		$(this).height(pHeight - prevHt - padding);
	});
}
