<%@ page language="java" contentType="text/html; charset=UTF-8" 
pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.exhide-3.5.min.js"></script>
<title>文章</title>

<script type="text/javascript">

	//初始化期刊分类树
	$(function(){
		var t = $("#perTree");
		$.get("${path}/system/FLTX/listContent.action?path=0&type=5",
			function(data) {
				var date = $.parseJSON(data);	//将json串转换成object对象数组
				var root = {"id":0,"pid":-1,"name":"期刊分类","xpath":"0", open:true};
				date.unshift(root);	//在数组的开头加上一条或多条数据
				$.fn.zTree.init(t, setting, date);
		});
		$('#work_main1').attr('src','${path}/article/articleList.jsp');
	});

	var setting = {
		async: {
			enable: true,	//是否开启异步加载
			url: getUrl		//异步加载时调用的url
		},
		view : {
			dblClickExpand : false,	//双击节点时，是否自动展开父节点的标识
			showLine : true,		//设置 zTree 是否显示节点之间的连线
			selectedMulti : false	//设置是否允许同时选中多个节点。
		},
		data : {
			simpleData : {
				enable : true,		//确定 zTree 初始化时的节点数据、异步加载时的节点数据、或 addNodes 方法中输入的 newNodes 数据是否采用简单数据模式 (Array)
									//不需要用户再把数据库中取出的 List 强行转换为复杂的 JSON 嵌套格式。
									//如果设置为 true，请务必设置 setting.data.simpleData 内的其他参数: idKey / pIdKey / rootPId，并且让数据满足父子关系。
				idKey : "id",		//唯一标识的属性名称
				pIdKey : "pid"		//父节点唯一标识的属性名称
			}
		},
		callback: {
			onClick: zTreeOnClick	
		}
	};
	
	
	//Ajax 获取数据的 URL 地址，调用的方法
	function getUrl(treeId, treeNode) {
		var param = "";
		if(treeNode == undefined){
			param = '0';
		}else{
			param = treeNode.id;
		}
		return "${path}/system/FLTX/listContent.action?path=" + param+"&type=5";
	}
	
	
	function zTreeOnClick(event, treeId, treeNode) {
	    var url = '${path}/article/articleList.jsp?treeId='+encodeURI(encodeURI(treeNode.name));
	    $('#work_main1').attr('src',url);
	};
	
	
	
</script>
</head>
<body class="by-frame-body" style="height: 100%">
<div class="by-main-page-body-side" id="byMainPageBodySide">
	<div class="page-sidebar" style="overflow:auto;">
		<div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
			<i class="fa fa-angle-left"></i>
		</div>
		<div id="sideWrap">
        	<ul id="perTree" class="ztree form-wrap" ></ul>
		</div>
	</div>
</div>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
    	<iframe id="work_main1" name="work_main1" width="100%" height="100%" frameborder="0" src=""></iframe>
	</div>
</body>
</html>