<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.brainsoon.system.support.SystemConstants.ResourceStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
	<script type="text/javascript" src="${path}/bres/res_operate.js"></script>
	<script type="text/javascript">
	var isTarget = '${isTarget}';
	var publishType='${publishType}';
	var targetNames = encodeURI('${param.targetNames}');
		$(function(){
			//定义一datagrid
// 			getStatusMap();
	   		var _divId = 'data_div';
	   		var _url = '';
	   		if(isTarget=='1'){
	   			_url = '${path}/publishRes/queryListTargetRes.action?publishType=${param.publishType}&targetNames='+targetNames+'&targetField='+'${param.targetField}&status=4';
	   		}else{
	   			_url = '${path}/publishRes/query.action';
	   		}
	   		var _pk = 'objectId';
	   		var _conditions = [<app:QueryConditionTag   publishType="${param.publishType}"/>,'status','publishType'];
	   		var _sortName = 'status';
	   		var _sortOrder = 'desc';
			var _colums = [
			                {field:'cover',title:'封面',width:fillsize(0.15),align:'center',formatter:$coverDesc},
							<app:QueryListColumnTag   publishType="${param.publishType}"/>
// 							{field:'status',title:'状态',width:fillsize(0.10),align:'center',formatter:$statusDesc},
							{field:'createName',title:'创建人',width:fillsize(0.11),align:'center'},
							{field:'createTime',title:'创建日期',width:fillsize(0.14),align:'center'},
							{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}];
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		/***版本号***/
		$resVersion = function(value,rec){
			return "V"+value+".0";
		}
		$coverDesc = function (value,rec){
			var cover = "";
			if(rec.metadataMap.cover != "" && rec.metadataMap.cover != "undefined" && rec.metadataMap.cover != undefined){
				var coverPath = rec.metadataMap.cover;
				var minCoverPath = coverPath.replace("cover.jpg","cover_min.jpg");
				cover="<img src='${path}/fileDir/viewer/"+minCoverPath+"' class='Img' width='60' height='80' alt='封面不存在' onclick='openImg(\"${path}/fileDir/fileRoot/"+coverPath+"\");'/>";
			}else{
				//cover="<img src='${path}/${fileRoot}/"+rec.rootPath+"/cover/cover.jpg' class='Img' width='60' height='80' onclick='openImg(\"${path}/${fileRoot}/"+rec.rootPath+"/cover/cover.jpg\");'/>";
				cover="<img src='${path}/appframe/main/images/cover_min.png' class='Img' width='60' height='80' onclick='openImg(\"${path}/appframe/main/images/cover.png\");'>";  
			}
			return cover;
		}
		/***操作列***/
		$operate = function(value,rec){
			var optArray = new Array();
			var status = rec.status;
			if(status==<%=ResourceStatus.STATUS4 %>){
				<sec:authorize url='/publishRes/recover.action'>
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:restoreRes('"+rec.objectId+"')\" ><i class=\"fa  fa-level-up\"></i> 恢复</a>");
			</sec:authorize>
			<sec:authorize url='/publishRes/offlineDetail.action'>
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>");
			</sec:authorize>
// 			optArray.push("<a class=\"btn hover-blue\" href=\"javascript:enforceDelete('"+rec.objectId+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a>");
// 			optArray.push("<a class=\"btn hover-red\" href=\"javascript:doDown('"+rec.objectId+"')\" ><i class=\"fa fa-download\"></i>下载</a>");
			return createOpt(optArray);
			}
		};
		
		function readRes(objectId){
			readFileOnline("",objectId,"tszy");
		}
		//标签点击事件
		function clickTarget(){
			var codes = getChecked('data_div','objectId').join(',');
			 var ids=document.getElementsByName("objectId");
			 var resIds = '';
			    for(var i=0;i<ids.length;i++){
			         if(ids[i].checked){
			        	 resIds+=(ids[i].value+",");
			       }
			    }
			    resIds = resIds.substring(0,resIds.length-1);
			    if(codes==""){
			    $.alert('请选择要添加标签的资源！');	
			    }else{
			    $.openWindow("${path}/system/target/bachTarget.jsp?publishType=${param.publishType}&resIds="+resIds+"&targetField="+$('#targetField').val(),'批量标签',1000,500);
			   }  
		}
		//加解锁
		function gotoUnLock(id,status){
			var initWaiting = $.waitPanel('正在操作请稍后...',false);
			$.ajax({
				url:"${path}/pubres/wf/gotoUnLock.action?objectId="+id+"&status="+status,
			    type: 'post',
			    datatype: 'text',
			    success: function (returnValue) {
			    	if(returnValue==1){
			    		$.alert("操作成功!");
			    		initWaiting.close();
			    		query();
			    	}else{
			    		$.alert("操作失败!");
			    		initWaiting.close();
			    	}
			    }
			});
		}
		//批量解锁
		function BatchGotoUnLock(){
			var ids = getChecked('data_div','objectId').join(',');
			alert(ids);
			var status = getChecked('data_div','metadataMap.status').join(',');
			alert(status);
			ids = ids.split(",");
			status = status.split(",");
			var objectId = "";
			for(var i=0;i<status.length;i++){
				//3是已通过
				if(status[i]=="3"){
					objectId =objectId+ids[i]+",";
				}else if(status[i]=="8"){
					objectId = "";
					$.alert("数据已解锁，请重新选择!");
					break;
				}
			}
			if(objectId!=""){
			var initWaiting = $.waitPanel('正在操作请稍后...',false);
			$.ajax({
				url:"${path}/pubres/wf/gotoUnLock.action?objectId="+objectId+"&status=3",
			    type: 'post',
			    datatype: 'text',
			    success: function (returnValue) {
			    	if(returnValue==1){
			    		$.alert("操作成功!");
			    		initWaiting.close();
			    		query();
			    	}else{
			    		$.alert("操作失败!");
			    		initWaiting.close();
			    	}
			    }
			});
		}
			//initWaiting.close();
	}
		//批量加锁
		function BatchGotoLock(){
			var ids = getChecked('data_div','objectId').join(',');
			var status = getChecked('data_div','metadataMap.status').join(',');
			ids = ids.split(",");
			status = status.split(",");
			var objectId = "";
			for(var i=0;i<status.length;i++){
				//8是已解锁
				if(status[i]=="8"){
					objectId =objectId+ids[i]+",";
				}else if(status[i]=="3"){
					objectId = "";
					$.alert("数据已加锁，请重新选择!");
					break;
				}
			}
			if(objectId!=""){
			var initWaiting = $.waitPanel('正在操作请稍后...',false);
			$.ajax({
				url:"${path}/pubres/wf/gotoUnLock.action?objectId="+objectId+"&status=8",
			    type: 'post',
			    datatype: 'text',
			    success: function (returnValue) {
			    	if(returnValue==1){
			    		$.alert("操作成功!");
			    		initWaiting.close();
			    		query();
			    	}else{
			    		$.alert("操作失败!");
			    		initWaiting.close();
			    	}
			    }
			});
		}
			//initWaiting.close();
	}
		function confirmRepeat(data,resIds){
			$('#selectResId').val(resIds);
			//alert($('#resId').val());
			var msg = '<table border="1" cellpadding="0" width="300px" cellspacing="0" >';
			msg+=data.target;
			msg += '</table>';
			$.simpleDialog({
				title:'添加标签', 
				content:'<font color=\"red\">已使用的标签</font><br>'+msg,
				button:[{name:'添加',callback:function(){
							var canSelectTargetIds ='';
							top.$('input:checkbox[name="canSelectTarget"]:checked').each(function(){
						    	 canSelectTargetIds+=$(this).val()+",";
						    	 
						     });
							canSelectTargetIds = canSelectTargetIds.substring(0,canSelectTargetIds.length-1);
							var hasSelectTargetIds ='';
							top.$('input:checkbox[name="hasSelectTarget"]:checked').each(function(){
								hasSelectTargetIds+=$(this).val()+",";
							     });
							
							hasSelectTargetIds = hasSelectTargetIds.substring(0,hasSelectTargetIds.length-1);
							if(canSelectTargetIds.length>0 || hasSelectTargetIds.length>0){
								saveForm(canSelectTargetIds,hasSelectTargetIds);
							}else{
								$.alert('请选择要添加的标签');
								return false;
							}
							
						}},
				        {name:'取消',callback:function(){}}
				]
			});
			
		}
		/***删除标签***/
		function todelete(canSelectTargetIds,hasSelectTargetIds){
			window.location.href = "${path}/publishRes/todelete.action?objectId="+hasSelectTarget;
		}
		function saveForm(canSelectTargetIds,hasSelectTargetIds){
			saveResWaiting = $.waitPanel('正在保存资源...',false);
			$('#coreData').ajaxSubmit({
				url: '${path}/target/saveTarget.action?canSelectTargetIds='+canSelectTargetIds+'&selectResId='+$('#selectResId').val()+'&hasSelectTargetIds='+hasSelectTargetIds+'&libType='+120+'&resIds='+$('#resId').val()+'&module='+$('#module').val()+'&type='+$('#type').val(),//表单的action
 				method: 'post',//方式
 				success:(function(response){
 					saveResWaiting.close();
					if(response == ''){
						parent.queryForm();
 					}else{
						$.showTips(response,5,'');
 					}
 					
           		})
 			});
		}
		//恢复
		function restoreRes(objectId){
			$.confirm('确定要恢复所选资源吗？', function(){
				$.ajax({
					url:'${path}/pubres/wf/restoreRes.action?objectId=' + objectId,
				    type: 'post',
				    datatype: 'text',
				    success: function (returnValue) {
				    	query();
				    }
				});
			});
		}
		/***添加***/
		function add(){
			upd('-1');
		}
		
		/***修改***/
		function upd(objectId){
			window.location.replace("${path}/publishRes/toEdit.action?publishType=${param.publishType}&objectId="+objectId);
		}
		/***转换***/
		function change(objectId){
			window.location.href = "${path}/publishRes/toChange.action?objectId="+objectId;
		}
		/***删除***/
		function del() {
			var codes = getChecked('data_div','objectId').join(',');
			if (codes == '') {
			    $.alert('请选择要删除的资源！');
			} else {
				var canDel = true;
				var rows = $('#data_div').datagrid('getChecked');
			 	for ( var i = 0; i < rows.length; i++) {
			 		var status = eval('rows[i]' + '.' + 'metadataMap.status');
			 		if(status != '0' && status != '5'){
			 			$.alert('状态为非草稿或者非驳回的资源不允许删除！');
			 			canDel = false;
			 			break;
			 		}
			 	}
			 	if(canDel){
			 		enforceDelete(codes);
			 	}
			}
		}
// 		function deleteRecord(ids){
// 			$.confirm('确定要删除所选数据吗？', function(){
// 				$.ajax({
// 					url:'${path}/publishRes/delRes.action?ids=' + ids,
// 				    type: 'post',
// 				    datatype: 'text',
// 				    success: function (returnValue) {
// 				    	query();
// 				    }
// 				});
// 			});
// 		}
		//强制删除
		function enforceDelete(ids){
			$.confirm('确定要删除所选数据吗？', function(){
				$.ajax({
					url:'${path}/publishRes/enforceDelete.action?ids=' + ids,
				    type: 'post',
				    datatype: 'text',
				    success: function (returnValue) {
				    	data = eval('('+returnValue+')');
				    	if(data.status==1){
				    		$.alert("删除成功!");
				    		query();
				    	}else{
				    		$.alert("该资源已发布不能删除!");
				    	}
				    }
				});
			});
		}
		function query(){
			$grid.query();
		}
		/***查看***/
		function detail(objectId){
			window.location.href = "${path}/publishRes/toDetail.action?objectId="+objectId+"&flagSta=4&publishType=${param.publishType}"+"&buttonShow=1";
		}
		function importRes(){
			$.openWindow("${path}/publishRes/import/publishImport.jsp?publishType=${param.publishType}",'批量导入',600,350);
		}
		//下载资源
		function batchDown(){
			var codes = getChecked('data_div','objectId').join(',');
			if (codes == '') {
			    $.alert('请选择要下载的资源！');
			} else {
				doDown(codes);
			}
		}
		function doDown(codes){
			//down4Encrypt('${path}/publishRes/downloadPublishRes.action?ids='+codes);
			$.ajax({
				url:'${path}/publishRes/downFileSize/${param.libType}.action?ids='+codes,
			    type: 'post',
			    datatype: 'text',
			    success: function (returnValue) {
			    	data = eval('('+returnValue+')');
			    	if(data.boo==1){
			    		$.alert("大小超过文件下载限制!");
			    	}else{
			    		down4Encrypt('${path}/publishRes/downloadPublishRes.action?ids='+codes);
			    	}
			    }
			});
		}
		function batchOneCheck(){
			var codes = getChecked('data_div','objectId').join(',');
			if (codes == '') {
			    $.alert('请选择要批量一审的资源！');
			} else {
				var canApply = true;
			 	var rows = $('#data_div').datagrid('getChecked');
			 	for ( var i = 0; i < rows.length; i++) {
			 		var status = eval('rows[i]' + '.' + 'metadataMap.status');
			 		if(status == '1' || status == '2'){
			 		}else{
			 			$.alert('状态为非待审核或者非待一审的资源不允许一审！');
			 			canApply = false;
			 			break;
			 		}
			 	}
			 	if(canApply){
			 		$("#versionText").modal('show');
			 	}
			}
		}
		function batchSecCheck(){
			var codes = getChecked('data_div','objectId').join(',');
			if (codes == '') {
			    $.alert('请选择要批量二审的资源！');
			} else {
				var canApply = true;
			 	var rows = $('#data_div').datagrid('getChecked');
			 	for ( var i = 0; i < rows.length; i++) {
			 		var status = eval('rows[i]' + '.' + 'metadataMap.status');
			 		if(status == '6'){
			 		}else{
			 			$.alert('状态为非待二审的资源不允许二审！');
			 			canApply = false;
			 			break;
			 		}
			 	}
			 	if(canApply){
			 		$("#versionText").modal('show');
			 	}
			}
		}
		function batchDoApply(){
			var codes = getChecked('data_div','objectId').join(',');
			if (codes == '') {
			    $.alert('请选择要批量上报的资源！');
			} else {
				var canApply = true;
			 	var rows = $('#data_div').datagrid('getChecked');
			 	for ( var i = 0; i < rows.length; i++) {
			 		var status = eval('rows[i]' + '.' + 'metadataMap.status');
			 		if(status == '0'||status == '5'){
			 		}else{
			 			$.alert('状态为非草稿的资源不允许上报！');
			 			canApply = false;
			 			break;
			 		}
			 	}
			 	if(canApply){
		 			doApply(codes);
			 	}
			}
		}
		function lookBatchResult(){
			window.location.href = '${path}/publishRes/import/importResultList.jsp?publishType=${param.publishType}';
		}
		function test(){
			window.location.href = '${path}/publishRes/toDetail.action';
		}
		function create(){
			window.location.href = '${path}/publishRes/create.jsp';
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
	  <input type="hidden" id="status" name="status" value="4"/>
	  <input type="hidden" id="creator" name="creator" value="${param.creator }"/>
	  <input type="hidden" id="modifiedStartTime" name="modifiedStartTime" value="${param.modifiedStartTime }" qMatch="=" />
	  <input type="hidden" id="modifiedEndTime" name="modifiedEndTime" value="${param.modifiedEndTime }"  qMatch="="/>
	  <input type="hidden" id="libType" name="libType" value="${param.libType }" />
	  <input type="hidden" id="queryType" name="queryType" value="${param.queryType }" />
	  <input type="hidden" id="publishType" name="publishType" value="${publishType }"/>
	<!-- 提交标签表单    -->
	<form action="#" id="coreData" class="form-horizontal" method="post">
	<app:HiddenConditionTag  publishType="${param.publishType}" />
	<input type="hidden" id="selectResId"  name="selectResId" value=""/>
	<input type="hidden" id="resId" name="resId" value=""/>
	<input type="hidden" id="resTargetId" name="resTargetId" value=""/>
	<%--<input type="hidden" id="identifier" name="commonMetaData.commonMetaDatas['identifier']" value=""/>
	<input type="hidden" id="xpaths" name="xpaths[0]" value=""/>
	<input type="hidden" id="resTargetId" name="resTargetId" value=""/>
    <input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
    <input type="hidden" id="queryNodeId" name="queryNodeId" value="-1"/>
    <input type="hidden" id="nodeAsset" name="nodeAsset" value=""/>
    <input type="hidden" id="jsonTree" name="jsonTree" value="${jsonTree}"/>
	<input type="hidden" id="ogId" name="ogId"/>
	<input type="hidden" id="bookPath" name="bookPath" />
	<input type="hidden" id="operateFrom" name="operateFrom" value="${operateFrom}"/>
    <input type="hidden" id="wfTaskId" name="wfTaskId" value="${wfTaskId}"/>
    <input type="hidden" id="saveAndApply" name="saveAndApply" value=""/>
	<input type="hidden" id="opt" name="opt" value=""/>
	<input type="hidden" id="nodeOpt" name="nodeOpt" value=""/>
	<input type="hidden" id="publishType" name="commonMetaData.commonMetaDatas['publishType']"/>
	 <input type="hidden" id="batchNum" name="batchNum" value="${param.batchNum }" />
	<input type="hidden" id="resVersion" name="commonMetaData.commonMetaDatas['res_version']"/>  	 --%>
</form>		
					
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li class="active">资源管理</li>
					<li class="active">下线资源</li>
					<li class="active">资源列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
				<%@ include file="/bres/resCheck.jsp" %>
			</div>
		</div>
	</div>
</body>
</html>