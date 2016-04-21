document.write('<div id="knowledgeContent" class="knowledgeContent" style="display:none; position: absolute;z-index: 999999999;background-color: white;border: 1px solid #e5e5e5;">');
document.write('	<ul id="knowledgeTree" class="ztree" style="margin-top:0; width:215px;max-height: 230px;overflow:auto;"></ul>');
document.write('</div>');
var preCode2,pCode2 = '';
function getKnowledgeTree(){
	pCode2 = getEducational() + ',' + $('#subject').val();
	if(preCode2 != pCode2){
		preCode2 = pCode2;
		//重新获取知识点
		$.ajax({
			type:"post",
			async:false,
			dataType:"json",
			url:_appPath+"/getKnowledgeTree.action?code="+pCode2,
			success:function(data){
			//	alert(data);
			//	zNodes2 = eval('('+data+')');
				$.fn.zTree.init($("#knowledgeTree"), setting2, data);
			}
		});
	}
}
window.onerror=reportError;
function reportError(msg,url,line) {
      var str = "You have found an error as below: \n\n";
      str += "Err: " + msg + " on line: " + line;
     return true;
}
var setting2 = {
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
		onClick: onClick2
	}
};

var zNodes2 =[];

function onClick2(e, treeId, treeNode) {
	var zTree = $.fn.zTree.getZTreeObj("knowledgeTree"),
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
	$('#knowledge_point').attr('value',vid);
	$("#knowledge_point_name").attr("value", v);
}
function clearKnowledge(){
	$('#knowledge_point').attr('value','');
	$('#knowledge_point_name').attr('value','');
}
function showKnowledge() {
	getKnowledgeTree();
	var obj = $("#knowledge_point_name");
	var objOffset = obj.offset();
	$("#knowledgeContent").css({left:objOffset.left + "px", top:objOffset.top + obj.outerHeight() + "px"}).slideDown("fast");

	$("body").bind("mousedown", onBodyDown2);
}
function hideMenu2() {
	$("#knowledgeContent").fadeOut("fast");
	$("body").unbind("mousedown", onBodyDown2);
}
function onBodyDown2(event) {
	if (!(event.target.id == "menuBtn2" || event.target.id == "knowledgeContent" || $(event.target).parents("#knowledgeContent").length>0)) {
		hideMenu2();
	}
}