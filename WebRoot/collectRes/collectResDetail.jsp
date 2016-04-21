<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/appframe/common.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>编辑资源</title>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	
	<script type="text/javascript">
	function showTreeSecond(treeId){
	    var treeObj = $.fn.zTree.getZTreeObj(treeId);
		var root = treeObj.getNodeByParam("nodeId", "1", null);
		if(root){
			treeObj.expandNode(root,true,false,true);
		}
     }
	var cFileType = '';
	var directorySetting = {
			view: {
				addHoverDom: addHoverDom,
				removeHoverDom: removeHoverDom,
				selectedMulti: false
			},
			
			data: {
				simpleData: {
					enable: true,
					idKey: "nodeId",
					pIdKey: "pid"
				}
			}
		};	
	function addHoverDom(treeId, treeNode) {
		var sObj = $("#" + treeNode.tId + "_span");
		if(treeNode.assets!=null){
			if (treeNode.nodeId!=1&&treeNode.editNameFlag==false){
				
				//预览
				if($("#readBtn_"+treeNode.tId).length==0){
					var readStr = "<span class='button preLook' id='readBtn_" + treeNode.tId
					+ "' title='预览' onfocus='this.blur();'></span>";
				    sObj.after(readStr);
					var btnRead = $("#readBtn_"+treeNode.tId);
					if (btnRead) btnRead.bind("click", function(){
						if(treeNode.files != undefined){
							var filePath=treeNode.files[0].path;
							var objectId=treeNode.files[0].objectId;
							readFileOnline(filePath,objectId); //相对路径	
						}else{
							$.alert("无法预览，文件不存在！");
						}
					});
				}
				
				//资源信息
				if($("#infoBtn_"+treeNode.tId).length==0){
					//var infoStr = "<span class='button ico_open' id='infoBtn_" + treeNode.tId
					//+ "' title='资源信息' onfocus='this.blur();'></span>";
					var infoStr = "<span class='button ico_list' id='infoBtn_" + treeNode.tId
					+ "' title='资源信息' onfocus='this.blur();'></span>";
					//alert(infoStr);
				    sObj.after(infoStr);
					var btnRes = $("#infoBtn_"+treeNode.tId);
					if (btnRes) btnRes.bind("click", function(){
						var objectId=treeNode.assets[0];
						$.openWindow("${path}/collectRes/detail.action?objectId="+objectId,'资源信息',1000,500);
						
					});
				}
			}
		}
	};
	
	function removeHoverDom(treeId, treeNode) {
		$("#infoBtn_"+treeNode.tId).unbind().remove();
		$("#readBtn_"+treeNode.tId).unbind().remove();
	};
	//把select，radio 等换成text，然后边框隐藏
	function setReadOnlyAndNoborder(){
		//处理select
		$("select").each(function(i){
		   var cText = $(this).find("option:selected").text();
		   if(cText == '请选择') cText = '';
		   $(this).parent().html('<input type="text" class="form-control" readonly="readonly" value="'+cText+'"/>');
		});
		//处理radio
		parseRadio('public_or_not');
		parseRadio('release_scope');
		parseRadio('sex');
		
		//最后边框设置为none
		$('input[type=text]').attr("readonly","readonly").addClass('no_border');
	}
	
	function parseRadio(radioId){
		var radioObj = $('input[id='+radioId+']:checked');
		var cCheckedValue = radioObj.attr('valueDesc');
		radioObj.parent().parent().html('<input type="text" class="form-control" readonly="readonly" value="'+cCheckedValue+'"/>');
	}
	
	//初始化数据
	function initData(data){
		var data = '${resourceDetail}';
		if(data == ''){
			initWaiting.close();
			$.alert('未找到数据');
			parent.queryForm();
			return;
		}
		data = data.replace(/[\r\n]/g,"");
		data = eval('('+data+')');
		$('#commonMetaDataObjectId').val(data.commonMetaData.objectId);
		$('#extendMetaDataObjectId').val(data.extendMetaData.objectId);
		$('#batchNum').val(data.batchNum);		
		//核心和通用元数据
		var commonMetaDatas = data.commonMetaData.commonMetaDatas;
		$('#identifier').val(commonMetaDatas['identifier']);
		//处理特殊
		var m = commonMetaDatas['module'];
		var type = commonMetaDatas['type'];
		setModuleAndType(m,type);
		$('#educational_phase').val(commonMetaDatas['educational_phase']);
		changeEducational();
		$('#version').val(commonMetaDatas['version']);
		changeVersion();
		$('#subject').val(commonMetaDatas['subject']);
		changeSubject();
		$('#grade').val(commonMetaDatas['grade']);
		changeGrade();
		$('#fascicule').val(commonMetaDatas['fascicule']);
		
		
		var public_or_not = commonMetaDatas['public_or_not'];
		var release_scope = commonMetaDatas['release_scope'];
		$("input[id=public_or_not][value="+public_or_not+"]").attr("checked",true);
		$("input[id=release_scope][value="+release_scope+"]").attr("checked",true);
		var region = commonMetaDatas['region'];
		if(typeof(region) != 'undefined' && region != ''){
			var regionArray = region.split(',');
			$('#provinces').val(regionArray[0]);
			setCity();
			$('#city').val(regionArray[1]);
			setArea();
			$('#area').val(regionArray[2]);
		}
		
		for(var key in commonMetaDatas){
			if(key=='rating'){
				$("#ratingShow").val(commonMetaDatas[key]);
			}else{
				$('#'+key).val(commonMetaDatas[key]);
			}
		}
		$("#ratingShow").rating();
		
        getExtentMeta();
		
		//处理扩展元数据
		var extendMetaDatas = data.extendMetaData.extendMetaDatas;
		for(var key in extendMetaDatas){
			$('#'+key).val(extendMetaDatas[key]);
		}
		var copyrightMetadata = data.copyRightMetaData;
		if(typeof(copyrightMetadata) != 'undefined' && copyrightMetadata != ''){
			var copyrightMetadatas  = data.copyRightMetaData.copyRightMetaDatas;
			if(typeof(copyrightMetadatas) != 'undefined' && copyrightMetadatas != ''){
				for(var key in copyrightMetadatas){
					$('#'+key).val(copyrightMetadatas[key]);
				}
			}
		}
	}
	function setModuleAndType(module,type){
		$('#module').val(module);
		$('#type').val(type);
			
		educationalPhaseShow();
		
		var educational_phase = getEducational();
		
		if(educational_phase != ''){
			changeEducational();
		}
	}
	/***修改***/
	function upd(objectId){
		window.location.replace("${path}/collectRes/edit.action?module=${param.module}&type=${param.type}&objectId="+objectId+"&targetCol=1"+"&goBackTask="+$('#goBackTask').val());
	}
	$(function() {
 		$('#flagButton').hide();
 		var data = '${date1}';
 		data=new Date(data); 
 		var today = new Date(); 
 		if(data>today){
 			$('#flagButton').show();	
 		}
		setModuleAndType('${module}','${type}');
		var data = '${resourceDetail}';
		data = eval('('+data+')');
		initData(data);
		var zNodes =[{ nodeId:1, pid:-1, name:"目录结构", open:true,xpath:'1'}];
		var organizations=data.organizations;
		if(organizations!=null&&organizations.length>0){
			organizationItems=organizations[0].organizationItems;
			$("#ogId").val(organizations[0].objectId);
			if(organizationItems.length>0){
				zNodes=organizationItems;
			}
		};
		$.fn.zTree.init($("#directoryTree"), directorySetting, zNodes);
		showTreeSecond("directoryTree");
		setReadOnlyAndNoborder();
	 }
	)
	
	</script>
	
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap">
    <ul class="page-breadcrumb breadcrumb">
        <li>
            <a href="###">资源管理</a>
            <i class="fa fa-angle-right"></i>
        </li>
        <li>
            <a href="###">聚合资源</a>
            <i class="fa fa-angle-right"></i>
        </li>
        <li><a href="###">资源信息</a> </li>
    </ul>
	<form action="#" id="coreData" class="form-horizontal" method="post">
	<input type="hidden" id="type" name="commonMetaData.commonMetaDatas['type']" value="${type}"/>
    <input type="hidden" id="module" name="commonMetaData.commonMetaDatas['module']" value="${module}"/>
    <input type="hidden" id="goBackTask" name="goBackTask" value="${goBackTask}"/>
    <input type="hidden" id="queryType" name="queryType" value="${queryType}"/>
    <div class="by-tab">
         <%@ include file="/bres/metaData.jsp" %>
         <div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">目录结构</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <div class="col-md-12 zTreeBackground">
                                	 <ul id="directoryTree"  class="ztree"></ul>
                                </div>
                                
                            </div>
                        </div>
                       
                    </div>
                   
                </div>
            </div>
        </div>
        
        <div class="form-actions">
        	<c:if test="${empty wfTaskId}">
        	  <button type="button" class="btn btn-lg blue" onclick="parent.queryForm();">返回</button>
            </c:if>
        </div>
        <!-- 工作流操作页面 -->
        <c:if test="${not empty execuId}">
			<%@ include file="/bres/WFOperate.jsp" %>
        </c:if>
    </div>
    </form>
</div>
<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
<script type="text/javascript" src="${path}/bres/knowledgeTree.js"></script>
</body>
</html>