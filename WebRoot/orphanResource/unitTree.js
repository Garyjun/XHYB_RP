var unitTreeImport = document.getElementById("unitTreeImport");
var s1 = true;
if(typeof(unitTreeImport) != 'undefined' && unitTreeImport != null){
	var treeFlag = unitTreeImport.getAttribute("data");
	if(treeFlag == 'main'){
		s1 = false;
	}
}
if(s1){
	document.write('<div id="menuContent" class="menuContent" style="display:none; position: absolute;z-index: 999999999;background-color: white;border: 1px solid #e5e5e5;">');
	document.write('	<ul id="unitTree" class="ztree" style="margin-top:0; width:215px;max-height: 240px;overflow:auto;"></ul>');
	document.write('</div>');
}
var preCode,pCode = '-1';
function getUnitTree(){
	//判断必填项 
	var volumeList = $('#volumeList');
	if(!volumeList.val()){
		$.alert('分册不允许为空');
		return false;
	}
	var subjectObj = $('#subjectList');
	if(!subjectObj.val()){
		$.alert('学科不允许为空');
		return false;
	}
	var gradeList = $('#gradeList');
	if(!gradeList.val()){
		$.alert('年级不允许为空');
		return false;
	}
	var versionList = $('#versionList');
	if(!versionList.val()){
		$.alert('版本不允许为空');
		return false;
	}
	var peroidList = $('#peroidList');
	if(!peroidList.val()){
		$.alert('学段不允许为空');
		return false;
	}
	
	pCode = "TB,T01," + peroidList.val() + "," + versionList.val() + "," +
			subjectObj.val() + "," + gradeList.val() + "," + volumeList.val();
	//重新获取单元
	$.ajax({
			type:"post",
			async:false,
			url:_appPath+"/getUnitTree.action?domainType=0&code="+pCode,
			success:function(data){
				zNodes = eval('('+data+')');
				$.fn.zTree.init($("#unitTree"), setting, zNodes);
			}
	});
}
var setting = {
		view: {
			dblClickExpand: false,
			showIcon: false
		},
		data: {
			simpleData: {
				enable: true,
				idKey: "nodeId",
				pIdKey: "pid"
			}
		},
		callback: {
			onClick: onClick
		}
	};

var zNodes =[];

function getSelectedTreeUnitId(){
	var zTree = $.fn.zTree.getZTreeObj("unitTree");
	if(null != zTree){
		var nodes = zTree.getSelectedNodes();
		if(null != nodes && nodes.length > 0)
			return nodes[0].objectId;
	}
	return '';
}
function onClick(e, treeId, treeNode) {
	var zTree = $.fn.zTree.getZTreeObj("unitTree"),
	nodes = zTree.getSelectedNodes();
	var v = "";
	var vid = "";
	nodes.sort(function compare(a,b){return a.id-b.id;});
	for (var i=0, l=nodes.length; i<l; i++) {
		v += nodes[i].name + ",";
		vid += nodes[i].objectId + ",";
	}
	if (v.length > 0 ) v = v.substring(0, v.length-1);
	if (vid.length > 0 ) vid = vid.substring(0, vid.length-1);
	var unitObj = $("#unitName");
	unitObj.attr("value", v);
	$('#unit').attr('value',vid);
}
function clearUnit(){
	//console.dir($('div.filed:hidden'))
	$('#unit').attr('value','');
	$('#unitName').attr('value','');
}
function showUnit() {
	getUnitTree();
	var unitObj = $("#unitName");
	var unitOffset = $("#unitName").offset();
	var bt = $(window).height() - unitOffset.top;
	var menuHeight = $('#unitTree').css('max-height');
	menuHeight = menuHeight.replace('px','');
	if(bt > menuHeight){
		$("#menuContent").css({left:unitOffset.left + "px", top:unitOffset.top + unitObj.outerHeight() + "px"}).slideDown("fast");
	}else{
		$("#menuContent").css({left:unitOffset.left + "px", bottom:bt + "px"}).slideDown("fast");
	}

	$("body").bind("mousedown", onBodyDown);
}
function hideMenu() {
	$("#menuContent").fadeOut("fast");
	$("body").unbind("mousedown", onBodyDown);
}
function onBodyDown(event) {
	if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
		hideMenu();
	}
}