<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>基础资源管理</title>
	<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>	    
	<style type="text/css">
		html, body {height: 100%;}
		#tree{
/*      position:absolute; */
/*     margin-left:10px; */
/*     margin-top:4px; */
       width:180px;
/*        height:435px;  */
/*     z-index:10; */
}
/*   .container{ */
/*       position:relative; */
/*         	} */
/*   .search{ */
/*           background-image:url("${path}/docviewer/css/search.png); */
/*           position:absolute; */
/*           width:50px; */
/*           height:50px; */
/*           top:4px; */
/*           left:100px; */
/*           z-index:99; */
/*   	} */
	</style>
	<script type="text/javascript">
	
		var queryType = 0;
		var treeSetting = "";
		//获得左侧查询条件
		$(function(){
			treeSetting = {
					check: {
				        enable: true
//				        chkboxType : { "Y" : "", "N" : "" }
				    },
					callback: {
						//点击
// 						onClick: zTreeOnClick
						//选中事件
						onCheck: onChecks
					},
			        view: {
			            addHoverDom: addHoverDom,
// 			            removeHoverDom: removeHoverDom,
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
				url : "${path}/tagHtml/getMainPageConditionTag.action?publishType="+$('#publishType').val(),
				type : 'post',
				datatype : 'text',
				success : function(returnValue) {
					if (returnValue != undefined || returnValue != '') {
							$('#mainPageCondition').html(returnValue);
					} else {
							$('#mainPageCondition').html("");
					}
				}
			});
			//获得标签字段
			$.ajax({
				url : "${path}/tagHtml/getMainPageTargetTag.action?publishType="+$('#publishType').val(),
				type : 'post',
				datatype : 'text',
				async:false,
				success : function(returnValue) {
					if (returnValue != undefined || returnValue != '') {
							$('#targetField').val(returnValue);
					}
				}
			});
// 			searchTarget(1);
			$("#targetType").hide();
			$('#lc').hide();
// 			$("#picture").hide();
// 			showTarget();
			$("#resetSearch").hide();
			$('#publishType').change(function(){
				location.href = "${path}/publishRes/publishResMain.jsp?publishType="+$('#publishType').val();
	//			queryForm();
			});
			$('#status').change(function(){
				queryForm();
			});
			$("select[type=multiSelEle]").multipleSelect({
				multiple: true,
				multipleWidth: 140,
				width: "90%",
				//onOpen: dyAddOpts,
				onClick: optCheckChangeListener
			});
			queryForm();
			$('#bresTab a').click(function (e) {
	            e.preventDefault();
	            queryType = $(this).context.id;
	        });
			
			lifeCycle();
			
			$('#lifeCycle').change(function(){
// 				alert($('#lifeCycle').val());
			});
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
			
			$("#targetType").change(function() {
			    //执行事件
				var targetType = $('#targetType').val();
				targetType = encodeURI(encodeURI(targetType));
				 $.ajax({
						url:"${path}/target/mainTargetSearch.action?publishType="+$('#publishType').val()+"&targetType="+targetType,
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
		});
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
// 		function removeHoverDom(treeId, treeNode) {
// 			$("#addNodeBtn_"+treeNode.tId).unbind().remove();
// 			$("#editNodeBtn_"+treeNode.tId).unbind().remove();
// 			$("#removeNodeBtn_"+treeNode.tId).unbind().remove();		
// 		}
		//标签索引
		var statusMap;
			function showTarget(flag){
				
// 				if(flag=="1"){
// 					 $("#tab_1_1_4").empty();
// 						var div = $('<label class=\"control-label col-md-4\"></label>'+'<div align=\"center\">'+'<div class=\"form-inline\">'+'<div class=\"form-group\">'+'<input type=\"text\" class=\"form-control\" id=\"searchName\" name=\"searchName\" onfocus=\"showHide();\" placeholder=\"输入标签名称\" qMatch=\"like\" style=\"width:62%\"/>'+'<input id=\"search\" type=\"button\" value=\"搜索\" class=\"btn btn-primary red\" onclick=\"searchTarget();\"/>'+'<input id=\"resetSearch\" type=\"button\" value=\"清空\" class=\"btn btn-primary red\" onclick=\"searchTarget(1);\"/>'+'</div>'+'</div>'+'</div>');
// 						div.appendTo($("#tab_1_1_4"));
// 						$("#resetSearch").hide();
// 				}
				$.ajax({
					url :'${path}/target/getAllMainTargets.action?module='+$('#publishType').val()+"&targetField="+$('#targetField').val(),
					type : 'post',
					async : true,
					success : function(data) {
						var publishTypeList = $("#tab_1_1_4");
						var divRow = $("<div  class='row' style='width:220px;margin-top:15px;margin-left:3px;' id='showFlag1'></div>"+
					                   "</div><div class='col-md-4'><div class='form-wrap'><div style='width:220px;margin-top:15px;margin-left:3px;' class='col-md-44' id='showFlag2'></div></div></div></div>");
						divRow.appendTo(publishTypeList);
						typeTarget = eval('(' + data + ')');
						for(var i = 0; i< typeTarget.length;i++){
							var newName = typeTarget[i].name;
							if(typeTarget[i].name.length>6){
								 newName = 	typeTarget[i].name.substring(0,7)+"...";
							}
							var checkbox = $("<input type='checkbox'  name='typeTarget' value='"+typeTarget[i].name+"' onClick='selectTargetRes();'><span data-toggle='tooltip' data-placement='top' title='"+typeTarget[i].name+"'>"+newName+"【"+typeTarget[i].num+"】</span></input>");
							if(i%1==0) {
								checkbox.appendTo($("#showFlag1"));
								var br = $("<br>");
								br.appendTo($("#showFlag1"));
							
							}else{
								checkbox.appendTo($("#showFlag2"));
								var br = $("<br>");
								br.appendTo($("#showFlag2"));
							}
					    }
					}
				});
			}
			function selectTargetRes() {
				//alert(11);
				$("#resetSearch").show();
				$("#search").hide();
				var targetName = document.getElementsByName("typeTarget");
				var targetNames="";
			    for (var i = 0; i < targetName.length; i++){  
				    if (targetName[i].checked){  
				    	targetNames= targetNames+","+targetName[i].value;
				    	}
			    }
			    if(targetNames.length>0){
			    	targetNames=targetNames.substr(1);
			    }
			    targetNames = encodeURI(encodeURI(targetNames));
				document.getElementById("queryForm").action = '${path}/target/gotoTargetList.action?publishType='+$('#publishType').val()+'&targetNames='+targetNames+"&targetField="+$('#targetField').val()+'&isTarget=1&status='+$('#status').val();
				document.getElementById("queryForm").submit();
			}
		function queryForm(){
// 			<app:MainCheckboxTag publishType="${param.publishType}"/>
			var url = 'publishResList.action?publishType='+$('#publishType').val()+'&status='+$('#status').val()+"&taskFlagAddFile=1";
 			url +=<app:MainPageParameterTag publishType="${param.publishType}" />+"&targetField="+$('#targetField').val();
			$('#work_main').attr('src',url);
		}
		function queryClass(){
// 			searchTarget(1);
			var html = "";
			var firstID = "";
			var firstName = "";
			$.ajax({
				type : "post",
				url : "${path}/statistics/sourceNum/queryGetDefiniton.action?pid="
						+ $('#publishType').val(),
				success : function(data) {
					var json = JSON.parse(data);
					var fieldName = "";
					firstID = json[0].id;
					firstName = json[0].fieldName;
					for (var i = 0; i < json.length; i++) {
						var jsonObj = json[i];
						var name = jsonObj.name;
						var id = jsonObj.id;
						fieldName = jsonObj.fieldName;
						var idFieldName = id + "-" + fieldName;
						html += "<option value='"+idFieldName+"'>"
								+ name + "</option>";
						$('#metadataClass').html(html);
					}
					$('#classPage').attr('src',"${path}/publishRes/class.jsp?fieldName="+firstName+"&typeId="+firstID);
				}
			});
		}
		
		function showClass() {
			var idFieldName = $("#metadataClass").val();
			var typeIds = idFieldName.split("-");
			var typeId = typeIds[0];
			var fieldName = typeIds[1];
			$('#classPage').attr('src',"${path}/publishRes/class.jsp?fieldName="+fieldName+"&typeId="+typeId);
		}
		
		function lifeCycle(){
			$.ajax({
				type : "post",
				url : "${path}/publishRes/lifeCycle.action",
				success : function(data) {
					var json ="";
					if(data!=""){
						$('#lc').show();
						json = JSON.parse(data);
					}
					var fieldName = "";
					var content = "";
					for(var i in json){
			 			var info = json[i];
				 		content +="<option value = '"+i+"'>"+info+"</option>";
			 		}
					$("#lifeCycle").append(content);
				}
			});
		}
		function searchTarget(num){
			var targetName = encodeURI(encodeURI($('#searchName').val()));
			$.ajax({
				url :'${path}/target/getTargetsForName.action?targetName='+targetName+"&publishType="+$('#publishType').val()+"&flag=target",
				type : 'post',
				async : true,
				success : function(data) {
					var publishTypeList = $("#tab_1_1_4");
					typeTarget = eval('(' + data + ')');
					var j =0;
					if(typeTarget!=null){
						 $("#tab_1_1_4").empty();
						var div = $('<label class=\"control-label col-md-4\"></label>'+'<div align=\"center\">'+'<div class=\"form-inline\">'+'<div class=\"form-group\">'+'<input type=\"text\" class=\"form-control\" id=\"searchName\" name=\"searchName\" onfocus=\"showHide();\" placeholder=\"输入标签名称\" qMatch=\"like\" style=\"width:62%\"/>'+'<input id=\"search\" type=\"button\" value=\"搜索\" class=\"btn btn-primary red\" onclick=\"searchTarget();\"/>'+'<input id=\"resetSearch\" type=\"button\" value=\"清空\" class=\"btn btn-primary red\" onclick=\"searchTarget(1);\"/>'+'</div>'+'</div>'+'</div>');
						div.appendTo($("#tab_1_1_4"));
						var divRow = $("<div  class='row' style='width:220px;margin-top:15px;margin-left:3px;' id='showFlag1'></div>"+
		                   "</div><div class='col-md-4'><div class='form-wrap'><div style='width:220px;margin-top:15px;margin-left:3px;' class='col-md-44' id='showFlag2'></div></div></div></div>");
						divRow.appendTo($("#tab_1_1_4"));
					}
					$("#resetSearch").hide();
// 					var targetNum = 0;
// 					if(typeTarget.length<=20){
// 						targetNum = typeTarget.length;
// 					}else{
// 						targetNum =20;
// 					}
					for(var i = 0; i< typeTarget.length;i++){
						var newName = typeTarget[i].name;
						if(typeTarget[i].name.length>6){
							 newName = 	typeTarget[i].name.substring(0,7)+"...";
						}
						var checkbox = $("<input type='checkbox'  name='typeTarget' value='"+typeTarget[i].name+"' onClick='selectTargetRes();'><span data-toggle='tooltip' data-placement='top' title='"+typeTarget[i].name+"'>"+newName+"【"+typeTarget[i].num+"】</span></input>");
						if(i%1==0) {
							checkbox.appendTo($("#showFlag1"));
							var br = $("<br>");
							br.appendTo($("#showFlag1"));
						
						}else{
							checkbox.appendTo($("#showFlag2"));
							var br = $("<br>");
							br.appendTo($("#showFlag2"));
						}
				    }
					if(num!=undefined){
			 			var targetNames ="";
			 			document.getElementById("queryForm").action = '${path}/target/gotoTargetList.action?publishType='+$('#publishType').val()+'&targetNames='+targetNames+"&targetField="+$('#targetField').val()+'&isTarget=1&status='+$('#status').val();
			 			document.getElementById("queryForm").submit();
					}
				}
			});
			
		}
		function showHide(){
			$("#resetSearch").hide();
			$("#search").show();
// 			var targetNames ="";
// 			document.getElementById("queryForm").action = '${path}/target/gotoTargetList.action?publishType='+$('#publishType').val()+'&targetNames='+targetNames+"&targetField="+$('#targetField').val()+'&isTarget=1';
// 			document.getElementById("queryForm").submit();
		}
		function showTargetType(en){
			if(en==undefined){
				$("#picture").hide();	
				$("#targetType").empty();
					$.ajax({
						type : "post",
						url : "${path}/publishRes/targetType.action?publishType="+$('#publishType').val(),
						success : function(data) {
							var json = JSON.parse(data);
							var fieldName = "";
							var content = "";
							content ="<option value = ''>全部</option>";
							for(var i in json){
					 			var info = json[i];
						 		content +="<option value = '"+i+"'>"+info+"</option>";
					 		}
							content +="<option value = '通用标签'>通用标签</option>";
							$("#searchName").hide();
							$("#targetType").show();
							$("#targetType").append(content);
						}
				});
			}else{
				$("#picture").show();
				$("#searchName").show();
				$("#targetType").hide();
// 				$("#picture").show();
// // 				$("#tree").empty();
// 				var targetName = $('#searchName').val();
// 				 targetName = encodeURI(encodeURI(targetName));
// 				 $.ajax({
// 						url:"${path}/target/mainTargetSearch.action?publishType="+$('#publishType').val()+"&targetName="+targetName,
// 						async:false,
// 						success:function(data){
// 							var content = jQuery.parseJSON(data);
// 							var ztree = $.fn.zTree.init($("#tree"), treeSetting,content);
// 							var root = ztree.getNodes()[0];
// 							if(root){
// 								ztree.expandNode(root,true,false,true);
// 							}

// 						}
// 					});
// 				 var t = $.fn.zTree.getZTreeObj("tree");
// 					var checkedNode = t.getCheckedNodes();
// 					var targetNames="";
// 					var tar = "";
// 				    for (var i = 0; i < checkedNode.length; i++){ 
// 				    	if(checkedNode[i].isParent!=true){
// 				    	tar = checkedNode[i].name.substring(0,checkedNode[i].name.indexOf('('));
// 					    	targetNames= targetNames+tar+",";
// 				    	}
// 				    }
// 				    targetNames = targetNames.substring(0,targetNames.length-1);
// 				    targetNames = encodeURI(encodeURI(targetNames));
// 				    document.getElementById("queryForm").action = '${path}/target/gotoTargetList.action?publishType='+$('#publishType').val()+'&targetNames='+targetNames+"&targetField="+$('#targetField').val()+'&isTarget=1&status='+$('#status').val();
// 					document.getElementById("queryForm").submit();
			}
		}
		function goSearchTarget(){
			$("#searchName").show();
			$("#targetType").hide();
			$("#picture").show();
//				$("#tree").empty();
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
			    targetNames = encodeURI(encodeURI(targetNames));
			    if(targetNames==''){
			    	targetNames = targetName;
			    }
			    document.getElementById("queryForm").action = '${path}/target/gotoTargetList.action?publishType='+$('#publishType').val()+'&targetNames='+targetNames+"&targetField="+$('#targetField').val()+'&isTarget=1&status='+$('#status').val();
				document.getElementById("queryForm").submit();
		}
	</script>
</head>
<body class="by-frame-body">
<div class="by-main-page-body-side" id="byMainPageBodySide">
    <div class="page-sidebar">
<%--     <app:MainPageTargetTag publishType="${param.publishType}"/> --%>
    <input type="hidden" id="targetField" name="targetField" value=""/>
        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
            <i class="fa fa-angle-left"></i>
        </div>
		<div id="sideWrap">
			<div class="by-tool-box">
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">资源类型：</label></div>
				    <div class="by-form-val">
				        <app:selectResType name="publishType" id="publishType" selectedVal="${param.publishType}"  headName=""  headValue=""  readonly =""/>
				    </div>
				</div>
				<div class="by-form-row clearfix" id="lc">
					<div class="by-form-title"><label for="abc">生命周期：</label></div>
					<div class="by-form-val">
						<select  id="lifeCycle" class="form-control text-input"></select>
					</div>
				</div>
				<div class="by-tab">
                       <ul class="nav nav-tabs nav-justified" id="bresTab">
                       		<li class="active"><a id="0" href="#tab_1_1_2" data-toggle="tab">条件</a></li>
                           <li onclick="queryClass()"><a id="1" href="#tab_1_1_1" data-toggle="tab">分类</a></li>
                           <li ><a id="1" href="#tab_1_1_4" data-toggle="tab">标签</a></li>
<!--                            <li><a id="2" href="#tab_1_1_3" data-toggle="tab">全文检索</a></li> -->
                       </ul>
                       <div class="tab-content">
                        <div class="tab-pane active" id="tab_1_1_2">
                        <div class="by-form-row clearfix">
						    <div class="by-form-title"><label for="abc">资源状态：</label></div>
						    <div class="by-form-val">
						    	<app:constants name="status" repository="com.brainsoon.system.support.SystemConstants" className="PublishResourceStatus" inputType="select" headerValue="全部" cssType="form-control" ignoreKeys="4"></app:constants>
						    </div>
						</div>
                           	<form id="queryForm1" action="" target="work_main" method="post">
                           	<div id="mainPageCondition">
<%--                            		<app:MainPageConditionTag publishType="${param.publishType}" /> --%>
                           	</div>
                               <div class="by-form-row by-form-action">
                                   <a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
                                   <a class="btn btn-default blue" href="###" onclick="$('#queryForm1')[0].reset();$('#status').val('');queryForm();">清空</a>
                               </div>
                              </form>
                           </div>
                           <div class="tab-pane" id="tab_1_1_1">
							<form id="queryForm" action="" target="work_main" method="post">
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="title">分类：</label></div>
                                   <div class="by-form-val">
	                                   	<select class="form-control" name="metadataClass" id="metadataClass"
											onchange="showClass()">
										</select>
                                   </div>
                               </div>
                                <div class="by-form-row clearfix">
                                  <iframe id="classPage" width="100%;" height="345px;" frameborder="0" scrolling="no" src="" ></iframe>
                                </div>
                            </form>
                           </div>
                          
                           <div class="tab-pane" id="tab_1_1_3">
                               <div class="by-form-row clearfix">
                                   <div class="">
                                       <input class="form-control" id="searchText" name="searchText"/>
                                   </div>
                               </div>
                              	<div class="by-form-row by-form-action">
                                   <a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
                                   <a class="btn btn-default blue" href="###" onclick="$('#searchText').val('');queryForm();">清空</a>
                               </div>
                           </div>
                            <div class="tab-pane col-md-offset-1 " id="tab_1_1_4" style="margin-left: 4px">
                            <label class="control-label col-md-4"></label>
                            <div align="center">
								<div class="form-inline">
									<div class="form-group">
									<div class="dropdown">
									  <button id="butn1" type="button" class="btn btn-primary red btn-sm" id="dropdownMenu1" data-toggle="dropdown">选择<span class="caret"></span>
									   </button>
									    <select  id="targetType" name="targetType" class="form-control" style="display:none;width:112px">
										</select>
									  <input type="text" class="form-control" id="searchName" name="searchName" onfocus="showHide();" placeholder="输入标签名称" qMatch="like" style="width:50%"/>
									   <a href='###' onclick="javascript:goSearchTarget()"><span><img id="picture" alt="暂无图片" width="26" height="26" src="${path}/appframe/main/images/detail.png"></span></a>
									   <ul class="dropdown-menu text-left" role="menu" aria-labelledby="dropdownMenu1">
									   	  <li role="presentation">
									         <a role="menuitem" tabindex="-1" onclick="showTargetType(1);return false;" href="javascript:void(0);">标签查询</a>
									      </li>
									      <li role="presentation" class="divider"></li>
									      <li role="presentation">
									         <a role="menuitem" tabindex="-1"  onclick="showTargetType();return false;" href="javascript:void(0);">分类查询</a>
									      </li>
									   </ul>
									</div>
									</div>
<!-- 									<input id="resetSearch" type="button" value="清空" class="btn btn-primary red" onclick="searchTarget(1);"/> -->
<!-- 								<div class="container"> -->
<!-- 								        <input type="text" value="aaa" class="sousuo"  />  -->
<!-- 								        <div class="search"></div> -->
<!-- 								</div> -->
								</div>
							</div>
							<div id="trees">
								<div class="panel-body height_remain" style="width:200px;overflow:auto;" id="999">
									<ul id="tree" class="ztree form-wrap" style="overflow:auto;"></ul>
									</div>
							</div>
							</div>
                           </div>
                       </div>
                   </div>
           	</div>
		</div>
   	</div>
</div>
<div class="by-main-page-body-center" id="byMainPageBodyCenter">
    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src=""></iframe>
</div>
</body>
</html>