<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>列表</title>
<script type="text/javascript"
	src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/jQueryValidationEngine/js/languages/jquery.validationEngine-zh_CN.js" charset="utf-8"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	
<style>
#flag {
	margin-bottom: 5px;
}
</style>
<script type="text/javascript"> 
var treeSetting ="";
	$(function() {
		treeSetting = {
				check: {
			        enable: true
//			        chkboxType : { "Y" : "", "N" : "" }
			    },
				callback: {
					//点击
// 						onClick: zTreeOnClick
					//选中事件
					onCheck: onChecks
				},
		        view: {
// 		            addHoverDom: addHoverDom,
//			            removeHoverDom: removeHoverDom,
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
		
		$.ajax({
			url:"${path}/target/mainTargetSearch.action?publishType="+$('#publishType').val(),
			async:false,
			success:function(data){
				var content = jQuery.parseJSON(data);
				var ztree = $.fn.zTree.init($("#tree"), treeSetting,content);
				var root = ztree.getNodes()[0];
				if(root){
					ztree.expandNode(root,true,false,true);
				}
			}
		});	
	});
// 	function edit(){
// 		document.form.action=act; 
// 		jQuery('#form').submit();
// 	}
		//点击事件
		function onChecks(event, treeId, treeNode) {
			var t = $.fn.zTree.getZTreeObj("tree");
			var checkedNode = t.getCheckedNodes();
			var targetNames="";
			var tar = "";
		    for (var i = 1; i < checkedNode.length; i++){
		    		tar = checkedNode[i].name.substring(0,checkedNode[i].name.indexOf('('));
			    	targetNames= targetNames+tar+",";
		    }
		    targetNames = targetNames.substring(0,targetNames.length-1);
		    targetNames = encodeURI(encodeURI(targetNames));
		    document.getElementById("queryForm").action = '${path}/target/gotoTargetList.action?publishType='+$('#publishType').val()+'&targetNames='+targetNames+"&targetField="+$('#targetField').val()+'&isTarget=1&status='+$('#status').val();
			document.getElementById("queryForm").submit();
		};
		function addHoverDom(treeId, treeNode){
		}
	function returnList(){
			location.href = "targetmain.jsp";
		}
// 	function searchTarget(){
// 		var targetName = encodeURI(encodeURI($('#searchName').val()));
// 		$.ajax({
// 			url :'${path}/target/getTargetsForName.action?targetName='+targetName+"&publishType="+$('#publishType').val()+"&flag=target",
// 			type : 'post',
// 			async : true,
// 			success : function(data) {
// 				var j =0;
// 				var publishTypeList = $("#showFlag");
// 				typeTarget = eval('(' + data + ')');
// 				var j =0;
// 				if(typeTarget!=null){
// 					$("#showFlag1").empty();
// 					$("#showFlag2").empty();
// 				}
// 				for(var i = 0; i< typeTarget.length;i++){
// 					var newName = typeTarget[i].name;
// 					if(typeTarget[i].name.length>6){
// 						 newName = 	typeTarget[i].name.substring(0,6)+"...";
// 					}
// 					var checkbox = $("<input type='checkbox'  name='typeTarget' value='"+typeTarget[i].name+"' onClick='selectTargetRes();'><span data-toggle='tooltip' data-placement='top' title='"+typeTarget[i].name+"'>"+newName+"【"+typeTarget[i].num+"】</span></input>");
// 					if(i%2==0) {
// 						checkbox.appendTo($("#showFlag1"));
// 						var br = $("<br>");
// 						br.appendTo($("#showFlag1"));
					
// 					}else{
// 						checkbox.appendTo($("#showFlag2"));
// 						var br = $("<br>");
// 						br.appendTo($("#showFlag2"));
// 					}
// 			    }
// 			}
// 		});
		
// 	}
	function edit(){
		var targetName="";
//         $("input[name='typeTarget']:checkbox").each(function(){ 
//             if($(this).attr("checked")){
//             	targetName += $(this).val()+","
//             }
//         })
        
		var t = $.fn.zTree.getZTreeObj("tree");
		var checkedNode = t.getCheckedNodes();
		var targetNames="";
		var tar = "";
	    for (var i = 0; i < checkedNode.length; i++){
	    	if(checkedNode[i].isParent!=true){
	    		tar = checkedNode[i].name.substring(0,checkedNode[i].name.indexOf('('));
		    	targetNames= targetNames+tar+",";
	    	}
	    }
	    targetNames = targetNames.substring(0,targetNames.length-1);
// 	    targetNames = encodeURI(encodeURI(targetNames));
        if(targetNames==""){
        	$.alert("请选择要添加的标签");
        	return;
        }
//         targetName = encodeURI(encodeURI(targetName.substring(0,targetName.length-1)));
        var initWaiting = $.waitPanel('正在操作请稍后...',false);
        var targetFields = $('#targetField').val();
        var target = {
        		ids:codes(),
        		targetNames:targetNames,
        		targetField:targetFields,
	    };
        var gotoTarget = encodeURI(JSON.stringify(target));
		$.ajax({
			url :'${path}/publishRes/beachTargetSave.action',
			type : 'post',
			async : true,
			data:"gotoTarget=" + gotoTarget,
			success : function(data) {
				var publishTypeList = $("#showFlag");
				var typeTarget = eval('(' + data + ')');
				if(typeTarget =="1"){
					initWaiting.close();
					$.alert("操作成功");
					top.index_frame.work_main.$grid.query();
					$.closeFromInner();
				}
			}
		});
	}
	function codes(){
		var codes = top.index_frame.work_main.getChecked('data_div','objectId').join(',');
		    return codes;
	}
	function searchTarget(){
		var targetName = $('#searchName').val();
		 targetName = encodeURI(encodeURI(targetName));
		 $.ajax({
				url:"${path}/target/mainTargetSearch.action?publishType="+$('#publishType').val()+"&targetName="+targetName,
				async:false,
				success:function(data){
					var content = jQuery.parseJSON(data);
					var ztree = $.fn.zTree.init($("#tree"), treeSetting,content);
					var root = ztree.getNodes()[0];
					if(root){
						ztree.expandNode(root,true,false,true);
					}
	
				}
			});
	}
// 	function searchComplete(){
// 			var targetName = encodeURI(encodeURI($('#searchName').val()));
// 	    	var url = "${path}/target/autoCom.action?searchName="+targetName+"&flag=target"+"&publishType="+$('#publishType').val();
// 	    	$("#searchName").bigAutocomplete({url:url});
// 	}
	</script>

<style type="text/css">
.form-control1 {
	display: block;
	width: 50%;
	height: 34px;
	padding: 6px 12px;
	font-size: 14px;
	line-height: 1.428571429;
	color: #555555;
	background-color: #ffffff;
	background-image: none;
	border: 1px solid #cccccc;
	border-radius: 4px;
	-webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
	box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
	-webkit-transition: border-color ease-in-out .15s, box-shadow
		ease-in-out .15s;
	transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
}

background-image
:url
("selRes
.png
")
 
;
margin-right
:
0px;
 
vertical-align
:top
;
 
*
vertical-align
:middle
}
.target-checkbox {
	
}
</style>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap "
		style="height: 100%;">
			<input type="hidden" id="resIds" name="resIds"
				value="${param.resIds }" /> <input type="hidden" id="publishType"
				name="publishType" value="${param.publishType }" /> <input
				type="hidden" id="targetField" name="targetField"
				value="${param.targetField }" />
				<div class="form-group" align="center"
					style="margin: 20px 10px 10px 0px;">
					<input type="text" class="form-control1" id="searchName"
						style="display: inline;" name="searchName" placeholder="输入标签名称"
						qMatch="like"/> 
<!-- 						onkeyup="searchComplete()" -->
						<input id="search" type="button" value="搜索"
						style="display: inline;" class="btn btn-primary red" onclick="searchTarget();"/>
				</div>
				<div id="trees" align="center">
					<div class="panel-body height_remain" id="999">
						<div class="col-sm-5" style="height: 50%;width:100%">
						<ul id="tree" class="ztree form-wrap" style="overflow:auto;"></ul>
						<div align="center" style="margin-top:1px">
						<button type="button" class="btn btn-default red" onclick="edit();">提交</button>
						<button type="button" class="btn btn-default blue"
						onclick="javascript:$.closeFromInner();">返回</button>
					</div>
						</div>
					</div>
				</div>
		</div>
</body>
</html>