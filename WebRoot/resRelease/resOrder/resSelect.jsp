<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<!-- 
	*根据Action传过来的数据
	*左边的目录树，node.flag属性：//_dir：目录， _file：文件
	*中间扩展名树，node.flag属性：//ExtensionNameDir：扩展名分类 ，ExtensionName：扩展名
	*右边的文件树，node.flag属性：//1：目录， 2：文件
	*
	*huangjun 2015年9月21日15:01:54
 -->
<html>
	<head>
		<title>可选资源列表</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
		<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
		<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
		<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/demo.css"/>
		<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css" />
		<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
		<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
		<script type="text/javascript" src="${path}/resRelease/resOrder/js/map.js"></script>
		<script type="text/javascript" src="${path}/resRelease/resOrder/js/list.js"></script>
		<script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
		<script type="text/javascript" src="${path}/search/js/bootstrap-select.js"></script>
		<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
		<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
		<script type="text/javascript" src="${path}/appframe/plugin/jxmenu/js/jquery-powerFloatEdit-min.js"></script>
		<script type="text/javascript">
			var orderId = "${param.orderId}";
			
			//根据orderId获取资源类型Id，初始化高级查询组件
			/* window.onload = function(){
				$.ajax({
					url : '${path}/resOrder/getResType.action?orderId=' + orderId,
					type : 'post',
					success: function(data){
						alert(data);
						//directCreateQueryCondition();
						directCreateQueryConditionForResSel(data, "");
						modifyCss("resSelect");
						$("#queryFrame").selectpicker('refresh');
						$("select[type=multiSelEle]").multipleSelect({
							multiple: true,
							multipleWidth: 80,
							width: "90%",
							//onOpen: dyAddOpts,
							onClick: optCheckChangeListener
						});
						$("label[name=multiSelEleName]").css("margin-right", "-20px");
					}
				});
				//初始化左侧的资源目录树 页面加载完成是调用
				showSelCategory();
				init();
				showExtensionName();//初始化中间的扩展名DIV
			
				$("input[name='zTreeType']").change(function(){
					var zTreeType = $("input[name='zTreeType']:checked").val();
					if(zTreeType=="dirTree"){
						$("#dirTreeDiv").show();
						$("#extensionNameDiv").hide();
						
						var extensionName = $.fn.zTree.getZTreeObj("extensionName");
						extensionName.checkAllNodes(false);//清空扩展名树
						var fileTree = $.fn.zTree.getZTreeObj("fileTree");
						fileTree.checkAllNodes(false);//清空资源文件树
					}else{
						$("#dirTreeDiv").hide();
						$("#extensionNameDiv").show();
						
						var dirTree = $.fn.zTree.getZTreeObj("dirTree");
						dirTree.checkAllNodes(false);//清空目录树
						var fileTree = $.fn.zTree.getZTreeObj("fileTree");
						fileTree.checkAllNodes(false);//清空资源文件树
					}
				});
			} */
			$(function(){
				//directCreateQueryCondition();
				initializes();
			});
			function typeinit(){  
				initializes();
			}
			function initializes(){
				xmenuNum="";
				directCreateQueryConditionAndRes($('#resType').val(), "");
				onLoadDict();   //加载数据字典项
				modifyCss("resSelect");
				if(xmenuNum != "" && xmenuNum != null){
					xmenuNum = xmenuNum.substring(0,xmenuNum.length-1);
					var menuList = xmenuNum.split(",");
					for(var i=0;i<menuList.length;i++){
						var menu = menuList[i]+"()";
						eval(menu);
					}
				}
				if($("#queryCondtionButton").length>0){
					$('#queryCondtionButton').on('click', function() {
						var params = getAllQueryFieldConditions();
						params = encodeURI(params);
						queryForm(params);
					});
				}
				$("#queryFrame").selectpicker('refresh');
				/* $("select[type=multiSelEle]").multipleSelect({
					multiple: true,
					multipleWidth: 80,
					width: "90%",
					//onOpen: dyAddOpts,
					onClick: optCheckChangeListener
				});
				$("label[name=multiSelEleName]").css("margin-right", "-20px"); */
				//初始化左侧的资源目录树 页面加载完成是调用
				showSelCategory();
				init();
				showExtensionName();//初始化中间的扩展名DIV
			
				$("input[name='zTreeType']").change(function(){
					var zTreeType = $("input[name='zTreeType']:checked").val();
					if(zTreeType=="dirTree"){
						$("#dirTreeDiv").show();
						$("#extensionNameDiv").hide();
						
						var extensionName = $.fn.zTree.getZTreeObj("extensionName");
						extensionName.checkAllNodes(false);//清空扩展名树
						var fileTree = $.fn.zTree.getZTreeObj("fileTree");
						fileTree.checkAllNodes(false);//清空资源文件树
					}else{
						$("#dirTreeDiv").hide();
						$("#extensionNameDiv").show();
						
						var dirTree = $.fn.zTree.getZTreeObj("dirTree");
						dirTree.checkAllNodes(false);//清空目录树
						var fileTree = $.fn.zTree.getZTreeObj("fileTree");
						fileTree.checkAllNodes(false);//清空资源文件树
					}
				});
			}			
			function onLoadDict(){
				var publishType = $("#resType").val();
				var flag = "";
				// 获得元数据定义字段,包括数据字典和数据字典包括的项
				var queryFields = creatFieldOpt(publishType, flag);
				var lineCount = 0;
				var newValues = "";
				var newCount = "";
				if(queryFields){
					var obj = JSON.parse(queryFields);
					$(obj).each(function(index) {
						++lineCount;
						var count = "";
						if(lineCount<10){
							count += "0" + lineCount;
						}
						var fieldValues = obj[index].fieldValues;
						var fieldType = obj[index].fieldType;
						if(fieldType == 3 || fieldType == 2 || fieldType == 4){
							newValues = newValues+",";
							newValues = newValues+fieldValues;
							newCount = newCount +",";
							newCount = newCount+count;
							//dictAddOpts(fieldValues,'search', null, "multiSelEle"+count);
						}
					});  
				}
				if(newValues.length>0 && newCount.length>0){
					var values = newValues.split(",");
					var counts = newCount.split(",");
					for(var i=0;i<values.length;i++){
						if(values[i]!="" && counts[i]!=""){
							//加载数据字典项，填充multiSelEle插件，原来所运用插件
							//dictAddOpts(values[i],'search', null, "multiSelEle"+counts[i]);
							//运用Xmenu插件
							dictQueryAddOpts(values[i],'search', null, "select_Value"+counts[i]);
						}
					}
				}
			}
			
			//初始化右侧的资源文件 点击查询是调用
			function init(){
				$.ajax({
					url : '${path}/resOrder/getFileTreeJson.action?page=1&size=10&param=0&orderId=' + orderId +'&restype='+ $('#resType').val(),
					type : 'post',
					beforeSend:function(XMLHttpRequest){
	                    $(".zTreeBackground ul[id=fileTree]").css({background:"url(${path}/resRelease/resOrder/img/treeLoading.gif)", "background-repeat":"no-repeat", "background-position":"center"});
						//$(".zTreeBackground ul[id=fileTree]").css("display", "block");
	                },
					success : function(fileTree) {
						if(fileTree==""){
							$(".zTreeBackground ul[id=fileTree]").css({background:""});
							$("#fileTree").append("后台查询发生错误，请重试！");
							return false;
						}
						var zNodes = jQuery.parseJSON(fileTree);
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
								onAsyncError: onAsyncError,
								onClick: zTreeOnClick
							}
						};
						$.fn.zTree.init($("#fileTree"), setting,zNodes);
						//$(".zTreeBackground ul[id=fileTree]").css("display", "none");
						$(".zTreeBackground ul[id=fileTree]").css({background:""});
						checkOperate();
					}
				});
			}
			var curPage = 0;
			//var orderId = "${param.orderId}";
			function getUrl(treeId, treeNode) {
				var param = "id="+ treeNode.id +"&page="+treeNode.page +"&size="+treeNode.pageSize,
				aObj = $("#" + treeNode.tId + "_a");
				aObj.attr("title", "当前第 " + treeNode.page + " 页 / 共 " + treeNode.maxPage + " 页");
				return "${path}/resOrder/getFileTreeJson.action?" + param +"&param=1&orderId=" + orderId;
			}
			
			function goPage(treeNode, page) {
				treeNode.page = page;
				if (treeNode.page<1){
					treeNode.page = 1;
				}
				if (treeNode.page>treeNode.maxPage) {
					treeNode.page = treeNode.maxPage;
				}
				if (curPage == treeNode.page) {
					return;
				}
				if (treeNode.maxPage == 1) {
					return;
				}
				curPage = treeNode.page;
				var zTree = $.fn.zTree.getZTreeObj("fileTree");
				zTree.reAsyncChildNodes(treeNode, "refresh");
			}
			function beforeExpand(treeId, treeNode) {
				if (treeNode.page == 0) treeNode.page = 1;
				return !treeNode.isAjaxing;
			}
			function onAsyncSuccess(event, treeId, treeNode, msg) {
				//alert(treeId + "   " + msg);
				checkedFileByContentAndType('update');
			}
			function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
				var zTree = $.fn.zTree.getZTreeObj("fileTree");
				layer.alert("异步获取数据出现异常。");
				treeNode.icon = "";
				zTree.updateNode(treeNode);
			}
			function addDiyDom(treeId, treeNode) {
				if (treeNode.level>0) return;
				var aObj = $("#" + treeNode.tId + "_a");
				if ($("#addBtn_"+treeNode.id).length>0) return;
				var addStr = "<span class='button lastPage' id='lastBtn_" + treeNode.id
					+ "' title='last page' onclick='addListener()' onfocus='this.blur();'></span><span class='button nextPage' id='nextBtn_" + treeNode.id
					+ "' title='next page' onclick='addListener()' onfocus='this.blur();'></span><span class='button prevPage' id='prevBtn_" + treeNode.id
					+ "' title='prev page' onclick='addListener()' onfocus='this.blur();'></span><span class='button firstPage' id='firstBtn_" + treeNode.id
					+ "' title='first page' onclick='addListener()' onfocus='this.blur();'></span>"; 
					
				aObj.after(addStr);
				var first = $("#firstBtn_"+treeNode.id);
				var prev = $("#prevBtn_"+treeNode.id);
				var next = $("#nextBtn_"+treeNode.id);
				var last = $("#lastBtn_"+treeNode.id);
				treeNode.maxPage = Math.round(treeNode.count/treeNode.pageSize - .5) + (treeNode.count%treeNode.pageSize == 0 ? 0:1);
				first.bind("click", function(){
					if (!treeNode.isAjaxing) {
						goPage(treeNode, 1);//首页
					}
				});
				last.bind("click", function(){
					if (!treeNode.isAjaxing) {
						goPage(treeNode, treeNode.maxPage);//尾页
					}
				});
				prev.bind("click", function(){
					if (!treeNode.isAjaxing) {
						goPage(treeNode, treeNode.page-1);//上一页
					}
				});
				next.bind("click", function(){
					if (!treeNode.isAjaxing) {
						goPage(treeNode, treeNode.page+1);//下一页
					}
				});
			};
			
			
			function queryResource(){
				$grid1.query();
			}
			
			function clearCondition(){
				$("#modifiedStartTime").attr('value','');
				$("#modifiedEndTime").attr('value','');
				
			}
			
			
			function showSelCategory(){
				//var orderId = "${param.orderId}";
				//TODO
				//alert("显示树时的orderId:" + orderId);
				$.ajax({
					url : '${path}/resOrder/getDirTreeJson.action?orderId=' + orderId +'&restype=' +$('#resType').val(),
					type : 'post',
					success : function(dirTree) {
						var dirzNodes = jQuery.parseJSON(dirTree);
						var dirsetting = {
							check: {
								//chkboxType: { "Y": "p", "N": "s" },
								enable: true
							},
							data: {
								simpleData: {
									enable: true
								}
							},
							callback: {
								//onClick: dirTreeOnClick
								onCheck: zTreeOnCheck
							}
						};
						$.fn.zTree.init($("#dirTree"), dirsetting,dirzNodes);
						//$("#selCategory").css("display","block");
						//$("#btnVal").attr("value", "全选");
					}
				});
			}
			
			function zTreeOnCheck(){
				//alert("o");
				/* var treeObj = $.fn.zTree.getZTreeObj("dirTree");
				var checkedNodes = treeObj.getCheckedNodes(true);	//获得被选中的节点
				for(var j=0;j<checkedNodes.length;j++){
					if(checkedNodes[j].flag=="_file"){
						list.add(checkedNodes[j].name);
					}
				} */
				var treeObj = $.fn.zTree.getZTreeObj("fileTree");
				treeObj.checkAllNodes(false);
				checkedFileByContentAndType('update');
				
			}
			
			function submitCategory(category){
				$("#selCategory").css("display","none");
				var treeObj = $.fn.zTree.getZTreeObj("dirTree");
				var fileTree = $.fn.zTree.getZTreeObj("fileTree");
				var selMap = getMap("dirTree");
				var chkMap = getMap("fileTree");
				var dirNodes =  treeObj.getNodesByParam("flag", "_dir", null);
				var fileNodes =  treeObj.getNodesByParam("flag", "_file", null);
				treeObj.checkAllNodes(false);
				$("#btnVal").attr("value", "全选");
				if(category==""){ //更新
					sleMap.get();
				}else{ //覆盖 
					var nodes = "";
					
				
					fileTree.checkNode(nodes, true, true);
				}
				
			}
			
			function checkedByParam(map){
				var treeObj = $.fn.zTree.getZTreeObj("fileTree");
				var dirNodes =  treeObj.getNodesByParam("flag", "1", null);
				var fileNodes =  treeObj.getNodesByParam("flag", "2", null);
				var list = new List();
				for(var k=0;k<fileNodes.length;k++){
					list.add(fileNodes[i]);
				}
				var i = 0;
				var j = 0;
				for(i;i<dirNodes.length;i++){
					var type = map.get(dirNodes[i]);
				}
			}
			
			function getMap(treeId){
				var treeObj = $.fn.zTree.getZTreeObj(treeId);
				var nodes = treeObj.getCheckedNodes(true);
				var map = new Map(); 
				for(var i = 1; i < nodes.length; i++){
					if(nodes[i].isParent){
						var children = nodes[i].children;
						var chkNode = "";
						for(var count=0;count<children.length;count++){
							if(children[count].checked){
								chkNode += children[count].name + "*";
							}
						}
						if(chkNode!=null&&chkNode.length>1){
							chkNode = chkNode.substring(0,chkNode.length-1);
						} 
						map.put(nodes[i].name, chkNode); 
					}
					if(nodes[i].getParentNode().name=="资源目录"&&nodes[i].children==null){
						map.put(nodes[i].name, ""); 
					}
					if(nodes[i].getParentNode().name=="资源文件"&&nodes[i].children==null){
						map.put(nodes[i].name, ""); 
					}
				}
				var result = "";
			    result = map.toString();
			    if(result!=null&&result.length>2){
			    	result = result.substring(0,result.length-2) + "}";
				}  
			    return result;
			}
			
			function checkOperate(){
				$("#btnVal").bind("click",function(){
					var treeObj = $.fn.zTree.getZTreeObj("dirTree");
					var checkedNodes = treeObj.getCheckedNodes(true);	//获得被选中的节点
					var allNodes = treeObj.getNodes();   //获得所有节点
					if(checkedNodes!=null&&allNodes!=null&&checkedNodes.length==getAllChildrenNodes(allNodes)){
						$("#btnVal").attr("value", "全选");
						treeObj.checkAllNodes(false);
					}else{
						$("#btnVal").attr("value", "重置");
						treeObj.checkAllNodes(true);
					}
	            });
			}
			
			function getAllChildrenNodes(firstTreeNode){
				var result = "";
				var len = 0;
				var treeNodes = firstTreeNode[0].children;
				result += firstTreeNode[0].name + ",";
				for(var count= 0;count<treeNodes.length;count++){
					var treeNode = treeNodes[count];
					if (treeNode.isParent) {
						var childrenNodes = treeNode.children;
						result += treeNode.name + ",";
						if (!childrenNodes.isParent) {
							for (var i = 0; i < childrenNodes.length; i++) {
								result += childrenNodes[i].name + ",";
							}
						}else{
							getAllChildrenNodes(treeNode);
						}
					}else{
						result += treeNode.name + ",";
					}
					if(result!=null&&result.length>1){
						var arr = result.substring(0,result.length-1).split(",");
						len = arr.length;
					}
				}
				//alert(result);
				var _url = "";
				$.ajax({
					url : _url,
					type : 'post',
					success : function() {
						
					}
					
				});
				return len;
			}
			
			function cancel(){
				$("#selCategory").css("display","none");
				var treeObj = $.fn.zTree.getZTreeObj("dirTree");
				treeObj.checkAllNodes(false);
				$("#btnVal").attr("value", "全选");
			}
			
			function zTreeOnClick(event, treeId, treeNode) {
			    if($("#resDetail")!=null){
			    	$("#resDetail").remove();
			    }
			    var fakeContainer = $("#fakeContainer");
			    var resDetail = $("<div id='resDetail' style='border: 1px solid;width:300px; height:200px; margin-left:15px; float:left; display:none;'></div>");
			    if(treeNode.flag=="_file"){
			   		var table = $("<table></table>");
			   		for(var i=0;i<4;i++){
			   			var tr = $("<tr></tr>");
			   			for(var j=0;j<2;j++){
			   				if(j==0){
			   					var td = $("<td  style='float:left;width:50px;height:50px;border:0 solid #F00; padding-left:3px; font-family:微软雅黑; padding-top:10px; text-align:right;'>"+'标题： '+"</td>");
				   				td.appendTo(tr);
			   				}
			   				if(j==1){
			   					var td = $("<td style='float:right;width:240px;height:50px;border:0 solid #000; padding-left:3px; font-family:微软雅黑; padding-top:10px; text-align:left;'>"+'中华人民共和国简史 '+"</td>");
				   				td.appendTo(tr);
			   				}
			   			}
			   			tr.appendTo(table);
			   		}
			   		table.appendTo(resDetail);
			   		resDetail.appendTo(fakeContainer);
			    	$("#resDetail").css("display","block");
			    }else{
			    	$("#resDetail").css("display","none");
			    }
			};
			
			function coverUpdate(){
				var selNodes = getMap("dirTree");
				var chkedNodes = getMap("fileTree");
			}
			
			function updateSel(){
				var selNodes = getMap("dirTree");
			}
			
			function hideSelCategory(id){
				$("#"+id).css("display","none");
				var treeObj = $.fn.zTree.getZTreeObj(id);
		        var selNodes = treeObj.getCheckedNodes(true);
				treeObj.checkAllNodes(false);
				return selNodes;
			}
			
			function checkedNodes(){
				var treeId = "fileTree";
				var treeObj = $.fn.zTree.getZTreeObj(treeId);
		        var dirNodes = treeObj.treeObj.getNodeByParam("flag", "1", null);
		        var fileNodes = treeObj.treeObj.getNodeByParam("flag", "2", null);
		        var result = getMap("dirTree");
			}
			
			//添加当前页选中资源到数据库
			function  getAllCheckedNodes(){
				var resIds = getAllCheckedFile();
				if(resIds==null||resIds==""){
					layer.alert("请选择需要添加的资源文件！");
				}else{
					$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;正在处理中，请稍待。。。</div>',
						css: {
			                border: 'none',
			                padding: '20px',
			                backgroundColor: 'white',
			                textAligh:'center',
			                width:"300px",
			                opacity: .7
			               }
					});
					$.post("${path}/resOrder/saveSelectedResource.action?posttype="+$('#posttype').val()+"&restype="+$('#resType').val(),
	                		{
								resIds: resIds,
								orderId: orderId
	                		},
	                		function(data){
	                			layer.alert("资源添加成功！");
	                			$.unblockUI();
								var parentWin =  top.index_frame.work_main1;
								
								var operateFrom = '${operateFrom}';
								//alert("添加当前页" + operateForm);
								if(operateFrom=="TASK_LIST_PAGE"){
									parentWin =  top.index_frame.work_main;
								}
								parentWin.freshDataTable('data_div');
	                		} 
	                );

				}
			}
			
			// 根据查询条件和左侧资源目录树 添加全部页资源
			//
			function addRessByConditionAndDir(){
				var zTreeType = $("input[name='zTreeType']:checked").val();
				if(zTreeType=="dirTree"){
					//资源目录树  --添加全部页
					addAllRessByDir();
				}else{
					//扩展名树 --添加全部页
					addAllRessByExtensionName();
				}
				
			}
			
			function addAllRessByDir(){
				//$.alert("添加资源文件操作已经触发，请稍后！");
				var map = getContentAndTypeMap();
				var conditions = getAllQueryFieldConditions();
				//alert("目录map:" + map);
				var jsonDir = "["; 
				map.each(function(key,value,index){   
					var content = key;//目录
			        var type = value;//文件类型
			        if(type!=null){
			        	//alert("list:" + type.toString());
			        	var type = type.toString();
			        	type = type.substring(1, type.length-1);
			        	
					    jsonDir += "{'" + content + "':'" + type +"'},";
			        }
				});
				if(jsonDir.length>1){
					jsonDir = jsonDir.substring(0, jsonDir.length-1);
					jsonDir += "]";
				}
				var _url = "${path}/resOrder/saveAllResource.action?orderId=" + orderId +"&restype=" + $('#resType').val()+"&posttype="+$('#posttype').val();
				if(jsonDir.length>1){
					_url += "&dirs=" + encodeURI(jsonDir);
				}else{
					_url += "&dirs=''";
				}
				if(conditions!=null&&conditions!=""){
					_url +=  "&conditions=" + conditions;
				}else{
					_url +=  "&conditions=''";
				}
				$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;正在处理中，请稍待。。。</div>',
						css: {
			                border: 'none',
			                padding: '20px',
			                backgroundColor: 'white',
			                textAligh:'center',
			                width:"300px",
			                opacity: .7
			               }
					});
				$.ajax({
					url : _url,
					type : 'post',
					success: function(response){
						layer.alert("资源添加成功！");
						$.unblockUI();
						var parentWin =  top.index_frame.work_main1;
						var operateFrom = '${operateFrom}';
						//alert("添加全部页" + operateFrom);
						if(operateFrom=="TASK_LIST_PAGE"){
							parentWin =  top.index_frame.work_main;
						}
						parentWin.freshDataTable('data_div');
					}
				});
			}
			
			function addAllRessByExtensionName(){
				//获取点选的扩展名
				var extensionName = $.fn.zTree.getZTreeObj("extensionName");
				var nodes = extensionName.getCheckedNodes(true);
				var extensionNames = "";
				for(var k=0;k<nodes.length;k++){
					if(nodes[k].flag=="extensionName"){//类型为扩展名
						extensionNames +=nodes[k].name+",";
					}
				}
				if(extensionNames.length>1){
					extensionNames = extensionNames.substring(0, extensionNames.length-1);
				}
				//查询条件
				var conditions = getAllQueryFieldConditions();
				
				var _url = "${path}/resOrder/saveAllResource.action?orderId=" + orderId+"&restype=" + $('#resType').val()+"&posttype="+$('#posttype').val();
				if(extensionNames.length>1){
					_url += "&dirs=" + encodeURI("ext-"+extensionNames);
				}else{
					_url += "&dirs=''";
				}
				if(conditions!=null&&conditions!=""){
					_url +=  "&conditions=" + conditions;
				}else{
					_url +=  "&conditions=''";
				}
				$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;正在处理中，请稍待。。。</div>',
					css: {
		                border: 'none',
		                padding: '20px',
		                backgroundColor: 'white',
		                textAligh:'center',
		                width:"300px",
		                opacity: .7
		               }
				});
				$.ajax({
					url : _url,
					type : 'post',
					success: function(response){
						layer.alert("资源添加成功！");
						$.unblockUI();
						var parentWin =  top.index_frame.work_main1;
						var operateFrom = '${operateFrom}';
						//alert("添加全部页" + operateFrom);
						if(operateFrom=="TASK_LIST_PAGE"){
							parentWin =  top.index_frame.work_main;
						}
						parentWin.freshDataTable('data_div');
					}
				});
				
			}
			
			function getFileType(fileName){
				var index = fileName.lastIndexOf(".");
				var type = fileName.substring(index+1, fileName.length);
				return type;
			}
			
			function checkNodesByType(category){
				var dirObj = $.fn.zTree.getZTreeObj("dirTree");
				var dirCheckedNodes = dirObj.getCheckedNodes(true);
				if(dirCheckedNodes.length==0){
					layer.alert("请选择目录及文件类型！");
					return false;
				}else{
					var fileObj = $.fn.zTree.getZTreeObj("fileTree");
					
					var nodes = fileObj.getNodesByParam("flag", "2", null);
					
					var list = new List();
					for(var j=0;j<dirCheckedNodes.length;j++){
						if(dirCheckedNodes[j].flag=="_file"){
							list.add(dirCheckedNodes[j].name);
						}
					}
					if(category=="cover"){
						fileObj.checkAllNodes(false);
					}
					for(var i=0;i<nodes.length;i++){
						var type = getFileType(nodes[i].name);
						if(list.contains(type)){
							fileObj.checkNode(nodes[i], true, true);
						}
					}
					dirObj.checkAllNodes(false);
					$("#selCategory").css("display","none");
				}
			}
			
			function createContent(){
				
			}
			
			function getContentAndTypeMap(){
				var map = new Map();
				var dirTree = $.fn.zTree.getZTreeObj("dirTree");
				var nodes = dirTree.getCheckedNodes(true);
				var len = nodes.length;
				var list = new List();
				for(var k=0;k<len;k++){
					if(nodes[k].flag=="_dir"){
						list.add(nodes[k]);
					}
				}
				//alert("选中元素个数："+ len);
				for(var index=0;index<list.size();index++){
					var dirNode = list.get(index);
					var listtemp = new List();
					for(var i=0;i<len;i++){
						var nodeName = nodes[i].name;
						var parentNode = nodes[i].getParentNode();
						if(parentNode){
							var parentNodeName = parentNode.name;
							if(dirNode.name==parentNodeName){
								//alert(nodes[i].name + "  " + nodes[i].getParentNode().name);
								listtemp.add(nodeName);
							}
						}
						
					}
					map.put(dirNode.name, listtemp);
				}
				//alert(map.size());
				return map;
			}
			
			//TODO根据目录和文件类型选中文件
			function checkedFileByContentAndType(category){
				var map = getContentAndTypeMap();
				var fileTree = $.fn.zTree.getZTreeObj("fileTree");
				if(category=="cover"){
					fileTree.checkAllNodes(false);
				}
				map.each(function(key,value,index){   
			        var content = key;//目录
			        var type = value;//文件类型
					//alert("type:" + type + "    content:" + content);
					if(type){//存在挑选目录下文件类型
						var size = type.size();
						for(var index=0;index<size;index++){
							var fileTypeNodes = fileTree.getNodesByParam("fileType", type.get(index), null);//获得文件类型
							for(var i=0;i<fileTypeNodes.length;i++){
								//var path = fileTypeNodes[i].path; //获得文件路径
								var path = fileTypeNodes[i].filePathName; //获得文件路径
								//path = path.replaceAll("\\", "/");
								
								if(path == "" || path == "undefined" || path == undefined){
									continue;
								}
								
								var arr = "";
								if(path.indexOf("\\")>-1){
									arr = path.split("\\");
								}else{
									arr = path.split("/");
								}
								
								var list = new List();
								var len = arr.length;
								for(var key=0;key<len;key++){
									list.add(arr[key]);
								}
								if(list.contains(content)){
									fileTree.checkNode(fileTypeNodes[i], true, true);
								}
							}
						}
						
					}else{//直接挑选某目录下的所有文件
						var allFileNodes = fileTree.getNodesByParam("flag", 2, null);//获得树上所有文件
						for(var i=0;i<allFileNodes.length;i++){
							//var path = allFileNodes[i].path; //获得文件路径
							var path = fileTypeNodes[i].filePathName; //获得文件路径
							if(path == "" || path == "undefined"){
								continue;
							}
							var arr = path.split("\\");
							var list = new List();
							var len = arr.length;
							for(var key=0;key<len;key++){
								list.add(arr[key]);
							}
							if(list.contains(content)){
								fileTree.checkNode(allFileNodes[i], true, true);
							}
						}
					}
				});
			}
			
			//获得所有选中文件的资源id
			function getAllCheckedFile(){
				var fileTree = $.fn.zTree.getZTreeObj("fileTree");
				var allCheckedFileNodes = fileTree.getCheckedNodes(true);
				var len = allCheckedFileNodes.length;
				//[{"objectId":"", "fileId":""}]
				var allFiles = "[";
				for(var k=0;k<len;k++){
					var fileId = allCheckedFileNodes[k].objectId;
					var resId = allCheckedFileNodes[k].caId;
					var isFile = allCheckedFileNodes[k].fileType;
					if(isFile){
						allFiles += "{\"objectId\":\"" + resId + "\"," + "\"fileId\":\"" + fileId +"\"},";
					}
				}
				if(allFiles.length>1){
					allFiles = allFiles.substring(0, allFiles.length-1) + "]";
				}else{
					allFiles = "";
				}
				//alert("选中所有的文件：" + allFiles);
				return allFiles;
			}
			
			//给分页按钮添加自动选中文件事件
			function addListener(){
				checkedFileByContentAndType('update');
			}
			
			function closeWindow(){
				var parentWin =  top.index_frame.work_main1;
				
				//parentWin.location.href = "${path}/resOrder/toEdit.action?id=" + orderId;
				//$.closeFromInner();
				var operateFrom = '${operateFrom}';
				//alert("关闭窗口"+ operateFrom);
				if(operateFrom=="TASK_LIST_PAGE"){
					parentWin =  top.index_frame.work_main;
				}
				parentWin.freshDataTable('data_div');
				//$.closeFromInner();
				var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
				parent.layer.close(index); //再执行关闭     
			}
			
			function saveResourceByCategory(category){
				if(category=="current"){ //添加当前页
					var allFilesId = getAllCheckedFile();
					//alert(allFilesId);
				}else{//所有页
					
				}
			}
			
			/* $(function() {
				if($("#queryCondtionButton").length>0){
					$('#queryCondtionButton').on('click', function() {
						var params = getAllQueryFieldConditions();
						alert("查询条件" + params);
						queryForm(params);
					});
				}
			}); */
			var count=0;//刷新资源树
			function queryForm(params) {
				/* zTreeObj.setting.async.url = "";
				reAsyncChildNodes  */
				var fileTree = $.fn.zTree.getZTreeObj("fileTree");
				var allChildNodes = fileTree.getNodesByParam("id", "-3", null);
				if(allChildNodes!=null){
					fileTree.removeChildNodes(allChildNodes[0]);
					var params = getAllQueryFieldConditions();
					//var _queryUrl = '${path}/resOrder/getFileTreeJson.action?page=1&size=50&orderId=' + orderId + "&param=" + params;
					var _queryUrl = '${path}/resOrder/getFileTreeJsonByQueryCondition.action?page=1&size=10&orderId=' + orderId + "&param=" + params;
					$.ajax({
						url : _queryUrl,
						type : 'post',
						beforeSend:function(XMLHttpRequest){
		                    $(".zTreeBackground ul[id=fileTree]").css({background:"url(${path}/resRelease/resOrder/img/treeLoading.gif)", "background-repeat":"no-repeat", "background-position":"center"});
							//$(".zTreeBackground ul[id=fileTree]").css("display", "block");
		                },
						success : function(fileTreeJson) {
							if(fileTree==""){
								$(".zTreeBackground ul[id=fileTree]").css({background:""});
								$("#fileTree").append("后台查询发生错误，请重试！");
								return false;
							}
							var zNodes = jQuery.parseJSON(fileTreeJson);
							/* var setting = {
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
									onAsyncError: onAsyncError,
									onClick: zTreeOnClick
								}
							};
							$.fn.zTree.init($("#fileTree"), setting,zNodes);
							//$(".zTreeBackground ul[id=fileTree]").css("display", "none");
							$(".zTreeBackground ul[id=fileTree]").css({background:""});
							checkOperate(); */
							
							//var newNodes = [{name:"newNode1",id:"ksdafj1",pId:"-3"}, {name:"newNode2",id:"ksdafj2",pId:"-3"}, {name:"newNode3",id:"ksdafj3",pId:"-3"}];
							$(".zTreeBackground ul[id=fileTree]").css({background:""});
							var parentNode = fileTree.transformTozTreeNodes(allChildNodes[0]);
							newNodes = fileTree.addNodes(parentNode[0], zNodes);
						}
					});
					
					
				}
			}
			
			function showExtensionName(){
				
				$.ajax({
					url : '${path}/resOrder/getExtensionName.action',
					type : 'post',
					success : function(data) {
						var extensionNodes = jQuery.parseJSON(data);
						var extensionSetting = {
							check: {
								enable: true
							},
							data: {
								simpleData: {
									enable: true
								}
							},
							callback: {
								onCheck: extensionOnCheck
							}
						};
						$.fn.zTree.init($("#extensionName"), extensionSetting,extensionNodes);
					}
				});
			}
			function extensionOnCheck(){
				var fileTree = $.fn.zTree.getZTreeObj("fileTree");
				fileTree.checkAllNodes(false);//清空资源树
				var extensionTree = $.fn.zTree.getZTreeObj("extensionName");
				var checkedNodes = extensionTree.getCheckedNodes(true);
				for(var j=0;j<checkedNodes.length;j++){
					if(checkedNodes[j].flag=="extensionName"){
						var extensionName = checkedNodes[j].name;//点选的
						var fileTypeNodes = fileTree.getNodesByParam("fileType", extensionName, null);//获得文件类型
						
						for(var i=0;i<fileTypeNodes.length;i++){
							fileTree.checkNode(fileTypeNodes[i], true, true);
						}
					}
				}
			}
			
		</script>
		<style type="text/css">
			.ztree li span.button.firstPage {float:right; margin-left:2px; margin-right: 0; background-image:url('${path}/resRelease/resOrder/img/zTreeStandard.png'); background-repeat:no-repeat; background-position:-144px -16px; vertical-align:top; *vertical-align:middle}
			.ztree li span.button.prevPage {float:right; margin-left:2px; margin-right: 0; background-image:url('${path}/resRelease/resOrder/img/zTreeStandard.png'); background-repeat:no-repeat; background-position:-144px -48px; vertical-align:top; *vertical-align:middle}
			.ztree li span.button.nextPage {float:right; margin-left:2px; margin-right: 0; background-image:url('${path}/resRelease/resOrder/img/zTreeStandard.png'); background-repeat:no-repeat; background-position:-144px -64px; vertical-align:top; *vertical-align:middle}
			.ztree li span.button.lastPage {float:right; margin-left:2px; margin-right: 0; background-image:url('${path}/resRelease/resOrder/img/zTreeStandard.png'); background-repeat:no-repeat; background-position:-144px -32px; vertical-align:top; *vertical-align:middle}
			ul.ztree li span.button {width:16px; height:16px;}
	</style>
	</head>
	<body>
	<div style="overflow-x:auto;">
		<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: auto;">
			<div class="panel panel-default" style="height: auto;">
				<%-- <div class="by-tab">
					<div class="portlet">
					    <div class="portlet-title">
	                        <div class="caption">隐藏查询条件
	                        	<a href="javascript:;" onclick="togglePortlet(this)"><i class="fa fa-angle-up"></i></a>
	                        </div>
                    	</div>
						<div class="portlet-body">
							<div class="container-fluid">
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="title" class="control-label col-md-4">资源标题：</label>
											<div class="col-md-8">
												<input type="text" name="title" id="title" class="form-control" />
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="creator" class="control-label col-md-4">制作者：</label>
											<div class="col-md-8">
												<input type="text" name="creator" id="creator" class="form-control" />
											</div>
										</div>
									</div>
								</div>
								
								<br/>
								
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="title" class="control-label col-md-4">创建时间：</label>
											<div class="col-md-8">
												<input type="text" name="title" id="title" class="form-control" />
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="creator" class="control-label col-md-4">中图分类：</label>
											<div class="col-md-8">
												<input type="text" name="creator" id="creator" class="form-control" />
											</div>
										</div>
									</div>
								</div>
								
								<br/>
								
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="title" class="control-label col-md-4">语种：</label>
											<div class="col-md-8">
												<input type="text" name="title" id="title" class="form-control" />
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="creator" class="control-label col-md-4">ISBN：</label>
											<div class="col-md-8">
												<input type="text" name="creator" id="creator" class="form-control" />
											</div>
										</div>
									</div>
								</div>
								
								<br/>
								
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="title" class="control-label col-md-4">评价等级：</label>
											<div class="col-md-8">
												<input type="text" name="title" id="title" class="form-control" />
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="creator" class="control-label col-md-4">评价描述：</label>
											<div class="col-md-8">
												<input type="text" name="creator" id="creator" class="form-control" />
											</div>
										</div>
									</div>
								</div>
								
								<br/>
								
								<div class="row">
									<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">创建时间：</label>
												<div class="col-sm-4">
													<input type="text" name="modifiedStartTime" id="modifiedStartTime"
														class="form-control Wdate" onselect="new Date()"
														onClick="WdatePicker({readOnly:true})" />
												</div>
												<div class="col-sm-4">
													<input type="text" name="modifiedEndTime" id="modifiedEndTime"
														class="form-control Wdate" onselect="new Date()"
														onClick="WdatePicker({readOnly:true})" />
												</div>
											</div>
										</div>
									
								</div>
								
								<br/>
								
								<div class="row">
									<div align="right">
										<input type="hidden" name="token" value="${token}" />
										<button type="button" class="btn btn-primary" onclick="queryResource()">查询</button>
								        &nbsp;
								     	<button type="reset" class="btn btn-primary" onclick="clearCondition();queryResource();">清空</button>
									</div> 
									<br />
								</div>
							</div>
						</div>
					</div>
				</div> --%>
				 <div class="row" style="margin-top: 1%;">
					<div class="col-md-4">
						<label class="control-label col-md-2">资源类型：</label>
						<div class="col-md-10">
							<select  id="resType"  name="resType" onchange="typeinit();">
								<c:forEach items="${lists }" var="lists">
									<option value="${lists.id}">${lists.typeName}</option>
								</c:forEach>
							</select>
						</div>
					</div>
				</div> 
				<%-- <div class="row">
					<div class="col-md-4">
						<label class="control-label col-md-2">资源类型：</label>
						<div class="col-md-10">
							 <app:selectResType onchange="typeinit();" name="publishType" id="publishType" selectedVal="${type }"  headName=""  headValue=""  readonly =""/>
						</div>
					</div>
				</div> --%>
				
				
				<div class="by-tab">
						<div class="portlet">
							<div class="portlet-title" style="width:100%">
								<div class="caption">
									隐藏查询条件 <a href="javascript:;" onclick="togglePortlet(this)"><i
										class="fa fa-angle-up"></i></a>
								</div>
							</div>
							 <form id="myForm">	
							<div class="portlet-body" id="queryFrame">
								<div class="container-fluid" id="queryCondition"></div>
							</div>
							</form>
						</div>
					</div>
				
			</div>
		</div>
		<div id="fakeFrame1" class="container-fluid by-frame-wrap " style="height: auto;">
			<div align="right">
				<input type="hidden" name="token" value="${token}" />
				<button type="button" class="btn btn-primary"
					onclick="addRessByConditionAndDir();">添加全部页</button>
				&nbsp;
				<button type="button" class="btn btn-primary" id="part"
					onclick="getAllCheckedNodes('part');">添加当前页</button>
				<button type="button" class="btn btn-primary"  
					onclick="closeWindow();" id="cancel">关闭</button>
					
			</div> 
				
			<div class="container-fluid by-frame-wrap">
				<label class="radio-inline form-group">
					<input type="radio"  id="zTreeType"  name="zTreeType" value="dirTree" checked="checked"/>按文件夹目录选择
				</label>
				<label class="radio-inline form-group">
					<input type="radio"  id="zTreeType" name="zTreeType" value="extensionName"/>按文件扩展名选择
				</label>
				<div id="dirTreeDiv" class="zTreeBackground" style="margin-left:20px;">
					<ul id="dirTree" class="ztree" style="border: 1px solid;width:380px;height:500px;overflow-x:auto;float:left;"></ul>
				</div>
				<div id="extensionNameDiv" class="zTreeBackground" style="margin-left:20px;display:none">
					<ul id="extensionName" class="ztree" style="border: 1px solid;width:350px;height:500px;overflow-x:auto;float:left;" ></ul>
				</div>
				<div class="zTreeBackground" style="float:left; margin-left:20px;">
					<ul id="fileTree" class="ztree" style="border: 1px solid;width:680px;height:500px;overflow-x:auto;float:left;"></ul>
				</div>
				
			</div>
		</div>
	</div>
	<input type="hidden" id="posttype" name="posttype" value="${posttype }"/>
	<input type="hidden" id="publishType" value="${param.type}"/>
	</body>
</html>