<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript">
		$(function(){
			init();
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = '${path}/bres/query.action';
	   		var _pk = 'objectId';
	   		var _conditions = ['module','type','eduPhase','version','subject','grade','fascicule','unit','title','creator','description','keywords','modifiedStartTime','modifiedEndTime','searchText','queryType','libType','batchNum'];
	   		var _sortName = 'modified_time';
	   		var _sortOrder = 'desc';
	   		var _showCheck=false;
			var _colums = [{field:'commonMetaData.commonMetaDatas.unit',title:'内容单元',width:fillsize(0.2),sortable:true},
							{field:'commonMetaData.commonMetaDatas.title',title:'资源标题',width:fillsize(0.17),sortable:true},
							{field:'commonMetaData.commonMetaDatas.creator',title:'制作者',width:fillsize(0.07),sortable:true},
							{field:'commonMetaData.commonMetaDatas.keywords',title:'关键字',width:fillsize(0.2),sortable:true},
							{field:'commonMetaData.commonMetaDatas.modified_time',title:'更新时间',width:fillsize(0.16),sortable:true},
							{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate}
						  ];
			
	   	   $grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_showCheck);
	   		
		});
		
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			var title = rec.commonMetaData.commonMetaDatas.title;
			var source = rec.commonMetaData.commonMetaDatas.source;
			opt += "<a class=\"btn hover-red\" href=\"javascript:selRes('"+rec.objectId+"','"+title+"','"+source+"')\" ><i class=\"fa fa-check-square-o\"></i> 选择</a>";
			opt += "<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
			return opt;
		};
		function filter(node) {
		    return (node.assets !=undefined);
		}
		function selRes(resId,title,source){
			var win=  top.index_frame.work_main;
			var treeObj = win.$.fn.zTree.getZTreeObj("directoryTree");
			var nodes = treeObj.getSelectedNodes();
			//判断是不是第一个资源，取第一个资源的来源
// 			var allNodes = treeObj.getNodesByFilter(filter); 
// 			if(allNodes.length == 0 ){
// 				win.$('#source').val(source);
// 			}
			var treeNode=nodes[0];
			var id=treeNode.nodeId + new Date().getTime();
			var xpath=treeNode.xpath+','+id;
			var assetsArray = new Array();
			assetsArray.push(resId);
			treeObj.addNodes(treeNode, {nodeId:id,xpath:xpath,pid:treeNode.nodeId, name:title,assets:assetsArray});
			var objectId = win.$('#objectId').val();
			var ogId=win.$('#ogId').val();
			var node = treeObj.getNodeByParam("nodeId", id, null);
			node.caId=objectId;
			node.ogId=ogId;
			var nodeJson=JSON.stringify(node);
			if(objectId=='-1'){
				art.dialog.close();//关闭弹出框
			}
			var nodeAsset = win.$('#nodeAsset').val();
			$.post("${path}/collectRes/updateNode.action",{ nodeAsset: nodeAsset, nodeJson: nodeJson,title:title },
					function(data){
						if(data=='-1'){
							$.alert('更新节点异常!');
						}else{
							var nodeTemp = treeObj.getNodeByParam("nodeId", id, null);
							nodeTemp.objectId=data;
							treeObj.updateNode(nodeTemp);
						}
						art.dialog.close();//关闭弹出框
			         }
			);
			var nodeId=$("#nodeId").val();
			win.setNodeRes(nodeId,resId);
			$.closeFromInner();
		}
		
		function detail(objectId){
		//	$.openWindow("${path}/bres/detail.action?libType=0&objectId="+objectId,'资源信息',1024,768);
			$.openWindow("${path}/bres/openDetail.action?libType=${param.libType}&objectId="+objectId,'资源详细',800,450);
		}
		
		function query(){
			$grid.query();
		}
		
		//初始化学段
		function init(){
			$.ajax({
				type:"post",
				async:false,
				url:"${path}/search/getDic.action?random="+Math.random(),
				success:function(data){
					 var da = (eval('('+data+')'));
					 var html ="<option value=''>请选择</option>";
					 for(var i = 0 , len = da.length;i < len;i++){  
		                 html += "<option value="+ da[i].code +">" + da[i].name + "</option>";  
		         	 }
					 $('#eduPhase').html(html).show(); 
				}
			});
			
			//并绑定事件：加载学科
			$("#eduPhase").change(function(){
				$("#eduPhase option").each(function(i,o){
					if($(this).attr("selected")){
						 $.post("${path}/search/getDic.action?random="+Math.random()+"&type=1&xPath=TB&eduPhase="+o.value,'', function(data){
							 var da = (eval('('+data+')'));
							 var html ="<option value=''>请选择</option>";
							 for(var i = 0 , len = da.length;i < len;i++){  
				                 html += "<option value="+ da[i].code +">" + da[i].name+ "</option>";  
				         	 }
							 $('#subject').html(html).show(); 
				         });  
					}
				})
			 })
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
					onClick: onClick2,
					onDblClick:hideMenu2
				}
			};

			var zNodes2 =[];
		/**
		 * 获取知识点树形结构
		*/
		document.write('<div id="knowledgeContent" class="knowledgeContent" style="display:none; position: absolute;z-index: 999999999;background-color: white;border: 1px solid #e5e5e5;">');
		document.write('	<ul id="knowledgeTree" class="ztree" style="margin-top:0; width:215px;max-height: 230px;overflow:auto;"></ul>');
		document.write('</div>');
		var preCode2,pCode2 = '';
		function getKnowledgeTree(){
			pCode2 = $('#eduPhase').val() + ',' + $('#subject').val();
			if(preCode2 != pCode2){
				preCode2 = pCode2;
				$.ajax({
					type:"post",
					async:false,
					url:"${path}/search/getDic.action?type=2&eduPhase="+$('#eduPhase').val()+"&subject="+$('#subject').val(),
					success:function(data){
						zNodes2 = eval('('+data+')');
						$.fn.zTree.init($("#knowledgeTree"), setting2, zNodes2);
					}
				});
			}
		}

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
	</script>
	<style type="text/css">
	.edit-control{width:150px}
	</style>
</head>
<body class="win-dialog">
<div class="form-wrap">
<div class="container-fluid">
<form id="queryForm" action="" target="work_main" method="post">
<input type="hidden" id="module" name="module" value="${param.module }" />
<input type="hidden" id="libType" name="libType" value="${param.libType }" />
<input type="hidden" id="queryType" name="queryType" value="${param.queryType }" />
<input type="hidden" id="type" name="type" value="${param.type }" />
<input type="hidden" id="placeFlag" name="placeFlag" value="${param.placeFlag }" />
<input type="hidden" id="nodeId" name="nodeId" value="${nodeId }" />
<input type="hidden" id="filterIds" name="filterIds" value="${filterIds}" />
<input type="hidden" id="status" name="status" value="3" />
	<div class="row">
		<div class="col-xs-6">
            <div class="form-group">
                <label class="control-label col-xs-4">学段：</label>
                <div class="col-xs-8">
                	<select class="form-control" name="eduPhase" id="eduPhase">
							<option value="">请选择</option>
					</select>
                </div>
            </div>
        </div>
		<div class="col-xs-6" >
            <div class="form-group">
                <label class="control-label col-xs-4">学科：</label>

                <div class="col-xs-8">
                	<select class="form-control" name="subject" id="subject">
							<option value="">请选择</option>
					</select>
                </div>
            </div>
        </div>
        </div><div class="row">
		<div class="col-xs-6" style="margin:6px 0px 0px 0px;">
            <div class="form-group">
                <label class="control-label col-xs-4">资源类型：</label>

                <div class="col-xs-8">
                	<app:constants name="type" repository="com.brainsoon.system.support.SystemConstants" className="ResourceType" inputType="select" ignoreKeys="T10,T11,T00" headerKey="" headerValue="全部" selected="1" cssType="form-control"></app:constants>
                </div>
            </div>
        </div>
		<div class="col-xs-6" style="margin: 6px 0px 0px 0px;">
            <div class="form-group">
                <label class="control-label col-xs-4">发布时间：</label>

                <div class="col-xs-8">
                	<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})" id="modifiedStartTime" name="modifiedStartTime" style="width: 115px;display: inline;"/> -
					<input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})" id="modifiedEndTime" name="modifiedEndTime" style="width: 115px;display: inline;"/>
                </div>
            </div>
        </div>
        <div class="col-xs-6" style="margin: 6px 0px 0px 0px;" >
            <div class="form-group">
                <label class="control-label col-xs-4">知识点：</label>

                <div class="col-xs-8" >
                	<input type="text" class="form-control" name="knowledge_point_name" id="knowledge_point_name" readonly="readonly" style="width:117px;display: inline;"/>
	                <input type="hidden" name="knowledge_point" id="knowledge_point" style="width:1px;"/>
					<a id="menuBtn2" onclick="showKnowledge(); return false;" href="###" class="btn btn-primary" role="button">选择</a>
				    <a onclick="clearKnowledge();return false;" href="###" class="btn btn-primary" role="button" style="margin-left: 1px;">清空</a>
                </div>
            </div>
        </div>
        <div class="col-xs-6" style="margin: 6px 0px 0px 0px;">
            <div class="form-group">
                <label class="control-label col-xs-4">标题：</label>

                <div class="col-xs-8">
                	<input type="text" name="title" id="title" class="form-control"/>
                </div>
            </div>
        </div>
        <div class="col-xs-6" style="margin: 6px 0px 0px 0px;">
            <div class="form-group">
                <label class="control-label col-xs-4">关键字：</label>

                <div class="col-xs-8">
                	<input type="text" name="keywords" id="keywords" class="form-control"/>
                </div>
            </div>
        </div>
        <div class="col-xs-6" style="margin: 6px 0px 0px 0px;">
            <div class="form-group">
                <label class="control-label col-xs-4">制作者：</label>

                <div class="col-xs-8">
                	<input type="text" name="creator" id="creator" class="form-control"/>
                </div>
            </div>
        </div>
        <div class="col-xs-12" align="right" style="padding-right: 10px;padding-top: 6px;">
        	<input type="button" value="查询" class="btn btn-primary red" onclick="query()"/>  
        	<input type="button" value="清空" class="btn btn-primary" onclick="$('#queryForm')[0].reset();"/>  
        </div>
    </div>
</form>
</div>
<div class="panel-body height_remain">
	<div id="data_div" class="data_div height_remain" style="width: 750px;"></div>
</div>
</div>
</body>
</html>