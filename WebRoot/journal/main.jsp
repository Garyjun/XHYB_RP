<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>期刊管理</title>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.exhide-3.5.min.js"></script>   
	<script type="text/javascript">
		//获得左侧查询条件
		$(function(){
			var setting = {
		    		async: {
		    			enable: true,
						url: getUrl
					},
					view : {
						/* addHoverDom : addHoverDom,
						removeHoverDom : removeHoverDom, */
						dblClickExpand : false,
						showLine : true,
						selectedMulti : false
					},
					data : {
						simpleData : {
							enable : true,
							idKey : "id",
							pIdKey : "pid",
						}
					},
					callback: {
						onClick: zTreeOnClick,
						onAsyncSuccess: onAsyncSuccess
					}
				};
		    
		   
			$.ajax({
				url:"${path}/journal/getTimeTree.action?type=6&nodeId=0",
				async:false,
				success:function(data){
					var content = jQuery.parseJSON(data);
					var ztree = $.fn.zTree.init($("#tree"), setting, content);
					var nodeD = ztree.getNodeByParam("name", "10年代", null);
					if(nodeD!=null){
						ztree.expandNode(nodeD, true, false, true);
					}
				}
			});	
		});
		
		function getUrl(treeId, treeNode) {
			var nodeId = "";
			var level = "";
			var name = "";
			if(treeNode == undefined){
				nodeId = '0';
			}else{
				nodeId = treeNode.id;
				level = treeNode.level;
				name = treeNode.name;
				xcode = treeNode.xcode;
			}
			return "${path}/journal/getTimeTree.action?nodeId="+nodeId+"&type=6"+"&level="+level+"&xcode="+xcode+"&name="+encodeURI(encodeURI(name));
		}
		
		function zTreeOnClick(event, treeId, treeNode) {
		    var level = treeNode.level;
		    var code = treeNode.code;
		    var name = treeNode.name;
		    var xcode = treeNode.xcode;
		    if(level=="0"){//期刊
		    	
		    }else if(level=="1"){//年代
		    	
		    }else if(level=="2"){//年份
		    	var codes = xcode.split(",");
		    	var magazineYear = codes[1];
				$('#work_main').attr('src','${path}/journal/list.jsp?magazineYear='+magazineYear+"&publishType="+$('#publishType').val());
		    }else if(level=="3"){//某期
		    	var codes = xcode.split(",");
		    	var magazineYear = codes[1];
		    	var numOfYear = codes[2];
				$('#work_main').attr('src','${path}/journal/list.jsp?magazineYear='+magazineYear+ '&numOfYear='+numOfYear+"&publishType="+$('#publishType').val());
		    }
		}
		
		function onAsyncSuccess(event, treeId, treeNode, msg) {
			var ztree = $.fn.zTree.getZTreeObj("tree");
			var nodeY = ztree.getNodeByParam("name", "2016年", null);
			if(nodeY!=null){
				ztree.expandNode(nodeY, true, false, true);
			}
		}
</script>
</head>
<body class="by-frame-body">
<div class="by-main-page-body-side" id="byMainPageBodySide">
	<input type="hidden" id="publishType" name="publishType" value="66"/>
    <div class="page-sidebar">
    	<input type="hidden" id="targetField" name="targetField" value=""/>
        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
            <i class="fa fa-angle-left"></i>
        </div>
		<div id="sideWrap">
			<div class="by-tool-box">
				<div id="trees">
					<div class="panel-body height_remain" style="width:236px;height:500px;overflow:auto;">
						<ul id="tree" class="ztree form-wrap" ></ul>
					</div>
				</div>
           	</div>
		</div>
   	</div>
</div>
<div class="by-main-page-body-center" id="byMainPageBodyCenter">
    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="${path}/journal/list.jsp"></iframe>
</div>
</body>
</html>