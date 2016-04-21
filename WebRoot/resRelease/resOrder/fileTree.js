var setting = {
	async: {
		enable: true,
		url: getUrl
	},
	check: {
		enable: true
	},
	data: {
		simpleData: {
			enable: true
		}
	},
	view: {
		addDiyDom: addDiyDom
	},
	callback: {
		beforeExpand: beforeExpand,
		onAsyncSuccess: onAsyncSuccess,
		onAsyncError: onAsyncError
	}
};

var zNodes =[
	{name:"分页测试", t:"请点击分页按钮", id:"1", count:2000, page:0, pageSize:100, isParent:true}
];

var curPage = 0;

function getUrl(treeId, treeNode) {
	var param = "id="+ treeNode.id +"_"+treeNode.page +"&count="+treeNode.pageSize,
	aObj = $("#" + treeNode.tId + "_a");
	aObj.attr("title", "当前第 " + treeNode.page + " 页 / 共 " + treeNode.maxPage + " 页")
	return _appPath+"resOrder/queryResforTreePage.action?" + param;
}

function goPage(treeNode, page) {
	treeNode.page = page;
	if (treeNode.page<1) treeNode.page = 1;
	if (treeNode.page>treeNode.maxPage) treeNode.page = treeNode.maxPage;
	if (curPage == treeNode.page) return;
	curPage = treeNode.page;
	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	zTree.reAsyncChildNodes(treeNode, "refresh");
}

function beforeExpand(treeId, treeNode) {
	if (treeNode.page == 0) treeNode.page = 1;
	return !treeNode.isAjaxing;
}

function onAsyncSuccess(event, treeId, treeNode, msg) {
	
}

function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	alert("异步获取数据出现异常。");
	treeNode.icon = "";
	zTree.updateNode(treeNode);
}

function addDiyDom(treeId, treeNode) {
	if (treeNode.level>0) return;
	var aObj = $("#" + treeNode.tId + "_a");
	if ($("#addBtn_"+treeNode.id).length>0) return;
	var addStr = "<span class='button lastPage' id='lastBtn_" + treeNode.id
		+ "' title='last page' onfocus='this.blur();'></span><span class='button nextPage' id='nextBtn_" + treeNode.id
		+ "' title='next page' onfocus='this.blur();'></span><span class='button prevPage' id='prevBtn_" + treeNode.id
		+ "' title='prev page' onfocus='this.blur();'></span><span class='button firstPage' id='firstBtn_" + treeNode.id
		+ "' title='first page' onfocus='this.blur();'></span>";
	aObj.after(addStr);
	var first = $("#firstBtn_"+treeNode.id);
	var prev = $("#prevBtn_"+treeNode.id);
	var next = $("#nextBtn_"+treeNode.id);
	var last = $("#lastBtn_"+treeNode.id);
	treeNode.maxPage = Math.round(treeNode.count/treeNode.pageSize - .5) + (treeNode.count%treeNode.pageSize == 0 ? 0:1);
	first.bind("click", function(){
		if (!treeNode.isAjaxing) {
			goPage(treeNode, 1);
		}
	});
	last.bind("click", function(){
		if (!treeNode.isAjaxing) {
			goPage(treeNode, treeNode.maxPage);
		}
	});
	prev.bind("click", function(){
		if (!treeNode.isAjaxing) {
			goPage(treeNode, treeNode.page-1);
		}
	});
	next.bind("click", function(){
		if (!treeNode.isAjaxing) {
			goPage(treeNode, treeNode.page+1);
		}
	});
};
