<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>基础资源管理</title>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 120%;}
		#tree{
        width:200px;
		}
	.by-tool-box .by-form-row .by-form-title {
	    width: 0px;
	    height: 35px;
	    line-height: 35px;
	    float: left;
	}
	</style>
	<script type="text/javascript">
		var queryType = 0;
		var treeSetting = "";
		$(function(){
			treeSetting = {
					check: {
				        enable: true
				    },
					callback: {
						//选中事件
// 						onCheck: onChecks
					},
			        view: {
// 			            addHoverDom: addHoverDom,
			            dblClickExpand: false,
			            showLine: true,
			            selectedMulti: false
			        },
					data: {
						simpleData: {
							enable: true,
							idKey: "id",
							pIdKey: "pid"
						}
					}					
			};
			//加载分类
			queryClass();
// 			queryForm();
		});
		//点击事件
// 		function onChecks(event, treeId, treeNode) {
// 			var t = $.fn.zTree.getZTreeObj("tree");
// 			var checkedNode = t.getCheckedNodes();
// 			var targetNames="";
// 			var tar = "";
// 		    for (var i = 1; i < checkedNode.length; i++){
// 		    		tar = checkedNode[i].name.substring(0,checkedNode[i].name.indexOf('('));
// 			    	targetNames= targetNames+tar+",";
// 		    }
// 		    targetNames = targetNames.substring(0,targetNames.length-1);
// 		    targetNames = encodeURI(encodeURI(targetNames));
// 		    document.getElementById("queryForm").action = '${path}/target/gotoTargetList.action?publishType=1&targetNames='+targetNames+"&targetField="+$('#targetField').val()+'&isTarget=1&offline=1';
// 			document.getElementById("queryForm").submit();
// 		};
// 		function addHoverDom(treeId, treeNode){
// 		}
// 		function queryForm(){
// // 			<app:MainCheckboxTag publishType="1"/>
// 			var url = '${path}/articleResource/articleResourceList.jsp?publishType=1';
// //  			url +=<app:MainPageParameterTag />+"&targetField=";
// 			$('#work_main').attr('src',url);
// 		}
		function queryClass(){
			var html = "";
			var firstID = "";
			var fieldName = "";
			$.ajax({
				type : "post",
				url : "${path}/statistics/sourceNum/queryTimeClass.action?pid=1",
				async:false,
				success : function(data) {
					var json = JSON.parse(data);
					firstID = json[0].id;
					fieldName = json[0].fieldName;
					$('#fieldName').val(fieldName);
					$('#classPage').attr('src',"${path}/articleResource/mainClass.jsp?typeId="+firstID+"&flag=1");
				}
			});
		}
		
// 		function showClass() {
// 			var idFieldName = $("#metadataClass").val();
// 			var typeIds = idFieldName.split("-");
// 			var typeId = typeIds[0];
// 			var fieldName = typeIds[1];
// 			$('#classPage').attr('src',"${path}/publishRes/class.jsp?fieldName="+fieldName+"&typeId="+typeId+"&flag=1");
// 		}
	</script>
</head>
<body class="by-frame-body">
<div class="by-main-page-body-side" id="byMainPageBodySide">
<%--     <app:MainPageTargetTag publishType="1"/> --%>
    <input type="hidden" id="targetField" name="targetField" value=""/>
    <input type="hidden" id="fieldName" name="fieldName" value=""/>
       <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
           <i class="fa fa-angle-left"></i>
       </div>
	<div id="sideWrap">
		<div class="by-tab">
			<div class="by-form-row clearfix">
                    <iframe id="classPage" width="100%;" height="599px;" frameborder="0" scrolling="no" src="" ></iframe>
			</div>
           </div>
	</div>
</div>
<div class="by-main-page-body-center" id="byMainPageBodyCenter">
    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src=""></iframe>
</div>
</body>
</html>