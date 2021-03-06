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
	<style type="text/css">
	/*  div.zTreeBackground {width:530px;height:400px;text-align:left;} */
    ul.ztree {width:500px;height:360px;overflow-y:scroll;overflow-x:auto;}
 	ul.ztree li span.button.ico_res{ background-image:url("selFile.png") ;margin-right:0px; vertical-align:top; *vertical-align:middle}
	</style>

	<script type="text/javascript">
	var cFileType = '';
	var objectId = '';
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
		if(treeNode.files!=''){
			if (treeNode.nodeId!=1&&treeNode.editNameFlag==false && $("#infoBtn_"+treeNode.tId).length==0){
					var infoStr = "<span class='button preLook' id='infoBtn_" + treeNode.tId
				+ "' title='预览' onfocus='this.blur();'></span>";
			    sObj.after(infoStr);
				var btnRes = $("#infoBtn_"+treeNode.tId);
				if (btnRes) btnRes.bind("click", function(){
					var filePath=treeNode.files[0].path;
					var object=treeNode.object;
					readFileOnline(filePath,object); //相对路径	
				});
			}
		}
	};
	
	function removeHoverDom(treeId, treeNode) {
		$("#infoBtn_"+treeNode.tId).unbind().remove();
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
//		parseRadio('release_scope');
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
		//核心和通用元数据
		var commonMetaDatas = data.commonMetaData.commonMetaDatas;
		$('#identifier').val(commonMetaDatas['identifier']);
		$('#batchNum').val(data.batchNum);		
		
		var public_or_not = commonMetaDatas['public_or_not'];
	//	var release_scope = commonMetaDatas['release_scope'];
		$("input[id=public_or_not][value="+public_or_not+"]").attr("checked",true);
	//	$("input[id=release_scope][value="+release_scope+"]").attr("checked",true);
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
			//alert(key+"="+commonMetaDatas[key]);
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
	
	/***修改***/
	function upd(objectId){
		window.location.replace("${path}/publishRes/edit.action?publishType=${param.publishType}&objectId="+objectId+"&targetPub=1"+"&goBackTask="+$('#goBackTask').val());
	}
	$(function() {
 		$('#flagButton').hide();
 		var data = '${date1}';
 		data=new Date(data); 
 		var today = new Date(); 
 		if(data>today){
 			$('#flagButton').show();	
 		}
		var data = '${resourceDetail}';
		data = eval('('+data+')');
		objectId = $('#objectId').val();
		initData(data);
		var zNodes =[{ nodeId:1, pid:-1, name:"资源文件", open:true,xpath:'1'}];
		var organizations=data.organizations;
		if(organizations!=null&&organizations.length>0){
			organizationItems=organizations[0].organizationItems;
			$("#ogId").val(organizations[0].objectId);
			if(organizationItems.length>0){
				zNodes=organizationItems;
			}
		};
		$.fn.zTree.init($("#directoryTree"), directorySetting, zNodes);
		setReadOnlyAndNoborder(); 
	}
	);
	
	</script>
	<style type="text/css">
	div.zTreeBackground {width:500px;height:400px;text-align:left;}

    ul.ztree {margin-top: 10px;border: 1px solid #617775;background: #f0f6e4;width:470px;height:360px;overflow-y:scroll;overflow-x:auto;}
	
	</style>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap">
    <ul class="page-breadcrumb breadcrumb">
        <li>
            <a href="###">资源管理</a>
            <i class="fa fa-angle-right"></i>
        </li>
        <li>
            <a href="###">资源管理</a>
            <i class="fa fa-angle-right"></i>
        </li>
        <li><a href="###">资源详细</a> </li>
    </ul>
	<form action="#" id="coreData" class="form-horizontal" method="post">
	<input type="hidden" id="publishType" name="commonMetaData.commonMetaDatas['publishType']" value="${publishType}"/>
	<input type="hidden" id="goBackTask" name="goBackTask" value="${goBackTask}"/>
    <input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
    <input type="hidden" id="flag" name="flag" value="${flag}"/>
    <div class="by-tab">
        <%@ include file="/publishRes/publishMetaData.jsp" %>
         <div class="portlet portlet-border">
	        <div class="portlet-title">
	            <div class="caption">资源文件</div>
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <div class="col-md-12 zTreeBackground">
                                	 <ul id="directoryTree"  class="ztree" style="overflow:auto;"></ul>
                                </div>
                                
                            </div>
                        </div>
                       
                    </div>
                   
                </div>
            </div>
        </div>
        <div class="form-actions" >
        <c:if test="${empty wfTaskId}">
          <c:choose>
            <c:when test="${flagSta=='1'}">
             <button type="button" class="btn btn-lg blue" onclick="javascript:$.closeFromInner();">关闭</button>
            </c:when>
            <c:otherwise>
              <button type="button" class="btn btn-lg blue" onclick="goBack();">返回</button>
            </c:otherwise>
          </c:choose>
          
        </c:if>
        </div>
        <!-- 工作流操作页面 -->
        <c:if test="${not empty execuId}">
         <c:choose>
            <c:when test="${flagSta=='1'}">
             <sec:authorize url='WF_pubresCheck'>
			    <%@ include file="/bres/PubWFOperate.jsp" %>
			 </sec:authorize>
            </c:when>
            <c:otherwise>
              <sec:authorize url='WF_pubresCheck'>
			   <%@ include file="/bres/WFOperate.jsp" %>
			  </sec:authorize>
            </c:otherwise>
          </c:choose>
        </c:if>
       
    </div>
    </form>
</div>
<script type="text/javascript">
	function goBack(){
		if('${flag}'=='flag'){
			$.closeFromInner();
		}else{
			parent.queryForm();
		}
	}
	</script>
<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
<script type="text/javascript" src="${path}/bres/knowledgeTree.js"></script>
</body>
</html>