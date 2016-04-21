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
	<script type="text/javascript" src="${path}/appframe/plugin/jxmenu/js/jquery-powerFloatEdit-min.js"></script>
	<script type="text/javascript"
	src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript"
	src="${path}/appframe/plugin/jQueryValidationEngine/js/languages/jquery.validationEngine-zh_CN.js"
	charset="utf-8"></script>
	<style>
		#flag {
			margin-bottom: 5px;
		}
	</style>
	<script type="text/javascript">
	var resTargetId = '${resTargetId}';
	var isTarget = '${isTarget}';
	var publishType='${publishType}';
	var status='${param.status}';
	var targetField='${param.targetField}';
	var targetNames = encodeURI('${param.targetNames}');
	var _conditions = "";
		$(function(){
			$('#isTarget').val(isTarget);
			//定义一datagrid
			if(publishType!=""){
				$('#publishType').val(publishType);
			}
			if($('#publishType').val()==""){
				$('#publishType').val('${param.publishType}');
			}
			if(status!=""&&status!="NaN"){
				$('#status').val(status);
			}
			getStatusMap();
	   		var _divId = 'data_div';
	   		var _url = '';
	   		if(isTarget=='1'){
	   			_url = '${path}/publishRes/queryListTargetRes.action?publishType=${param.publishType}&targetNames='+targetNames+"&targetField="+targetField+'&status='+status;
	   		}else{
	   			_url = '${path}/publishRes/query.action?publishType='+$('#publishType').val();
	   		}
	   		
	   		var _pk = 'objectId';
	   		_conditions = [<app:QueryConditionTag   publishType="${param.publishType}"/>,"publishType","status"];
	   		var _sortName = 'create_time';
	   		var _sortOrder = 'desc';
			var _colums = [
							{field:'cover',title:'封面',width:fillsize(0.15),align:'center',formatter:$coverDesc},
							<app:QueryListColumnTag   publishType="${param.publishType}"/>
							{field:'status',title:'状态',width:fillsize(0.10),align:'center',formatter:$statusDesc},
							{field:'createName',title:'创建人',width:fillsize(0.11),align:'center'},
							{field:'createTime',title:'创建日期',width:fillsize(0.14),align:'center'},
							{field:'opt1',title:'操作',width:fillsize(0.22),align:'center',formatter:$operate,align:'center'}];
			accountHeight();
	   	//	$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	   		
	   		
	   		var _buttons = [{  
				text:'二次查询' ,  
		        iconCls:'icon-search',    
		        handler:function(){ 
		           //alert("----"); 
		          layer.open({
   						type: 1,
   						title :'二次查询',
						area: ['450px', '430px'],
						btn: [],
						fix: false,
						shadeClose: true,
						closeBtn: 0,
						yes: function(index, layero){
						},
						scrollbar: false,
						content: '<app:SecondQueryConditionTag   publishType="${param.publishType}"/>'
							
							
		          });
		           //每个人要加查询条件自己拼好固定格式,加一个input的hidden的隐藏把二次
		           	//查询的查询条件放到指定的input的value中，zaijs中直接获取指定的input的value值，连接到原来查询条件的后面
		           	//此处要注意的是，datagrid加上_buttons参数之后，还要加入true,true,true,"","","",参数，详细看一下，详细说明
		        }    
		    }];
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,true,true,true,"","","",_buttons);
	   		//图片放大插件
	   		//$("a[id^='imgbox_']").imgbox();
	   		
	   		
	   		//获取封装查询条件的dataGrid
// 	   		var dataDiv = $('#data_div').datagrid('options');
// 	   		var param = dataDiv.queryParams;   //获取输入的查询条件
// 	   		//拼装查询条件的格式
// 	   		var queryContion = "\"";
// 		   	 for (var key in param)
// 		     {
// 		   		 queryContion += "&"+"'"+key+"'"+":"+"[";
// 		   		 var queryData = param[key];
// 		   		 for(var i =0;i<queryData.length;i++){
// 		   			queryContion += "'" + queryData[i]+"'"+","
// 		   		 }
// 		   		queryContion = queryContion.substring(0,queryContion.length-1);
// 		   		queryContion += "]"
// 		     }
// 		   	//queryContion += "\""    为了在二次查询时能够直接连接加入新的查询条件，所以此处特意少写了个结束的”
// 		   	console.info(queryContion);
// 	   		$('#secondQuery').val(queryContion);
	   		
		});
		/***操作列***/
		$operate = function(value,rec){
			var optArray = new Array();
			var status = rec.status;
			<sec:authorize url='/publishRes/preview.action'>
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:preview('"+rec.objectId+"')\" ><i class=\"fa fa-eye\"></i> 预览</a>");
			</sec:authorize>
			<sec:authorize url='/publishRes/detail.action'>
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>");
			</sec:authorize>
			if(status==<%=ResourceStatus.STATUS3 %>){
				<sec:authorize url="/publishRes/gotoLock.action">
				optArray.push("<a class=\"btn hover-red\" href=\"javascript:gotoUnLock('"+rec.objectId+"','"+status+"')\" ><i class=\"fa fa-unlock\"></i> 解锁</a>");
				</sec:authorize>
				}
			if(status==<%=ResourceStatus.STATUS0 %>||status==<%=ResourceStatus.STATUS5 %>||status==<%=ResourceStatus.STATUS8 %>){
			<sec:authorize url="/publishRes/2add.action">
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:upd('"+rec.objectId+"')\" ><i class=\"fa fa-edit\"></i> 修改</a>");
			</sec:authorize>
			}
			if(status==<%=ResourceStatus.STATUS8 %>){
				<sec:authorize url="/publishRes/gotoUnLock.action">
				optArray.push("<a class=\"btn hover-red\" href=\"javascript:gotoUnLock('"+rec.objectId+"','"+status+"')\" ><i class=\"fa fa-lock\"></i> 加锁</a>");
				</sec:authorize>
				}
			// 草稿、下线、已驳回
			if(status==<%=ResourceStatus.STATUS0 %>||status==<%=ResourceStatus.STATUS4 %>||status==<%=ResourceStatus.STATUS5 %>){
				<sec:authorize url="/publishRes/delete.action">
				optArray.push("<a class=\"btn hover-blue\" href=\"javascript:enforceDelete('"+rec.objectId+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a>");
				</sec:authorize>
			}
			if('${enforceDelete}'==1 && status==<%=ResourceStatus.STATUS1 %>||'${enforceDelete}'==1 && status==<%=ResourceStatus.STATUS2%>||'${enforceDelete}'==1 && status==<%=ResourceStatus.STATUS3%>||'${enforceDelete}'==1 && status==<%=ResourceStatus.STATUS6%>||'${enforceDelete}'==1 && status==<%=ResourceStatus.STATUS8 %>)
			{
				<sec:authorize url="/publishRes/forceDelete.action">
				optArray.push("<a class=\"btn hover-blue\" href=\"javascript:enforceDelete('"+rec.objectId+"')\" ><i class=\"fa fa-trash-o\"></i> 强制删除</a>");
				</sec:authorize>
			}
// 			<sec:authorize url="/publishRes/read.action">
// 			optArray.push("<a class=\"btn hover-red\" href=\"javascript:readRes('"+rec.objectId+"')\" ><i class=\"fa fa-eye\"></i> 预览</a>");
// 			</sec:authorize>
			<sec:authorize url="/publishRes/download.action">
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:doDown('"+rec.objectId+"')\" ><i class=\"fa fa-download\"></i> 下载</a>");
			</sec:authorize>
			// 草稿，驳回状态
			if(status==<%=ResourceStatus.STATUS0 %>||status==<%=ResourceStatus.STATUS5 %>){
				<sec:authorize url="/publishRes/doApply.action">
				optArray.push("<a class=\"btn hover-red\" href=\"javascript:doApply('"+rec.objectId+"')\" ><i class=\"fa fa-mail-reply-all\"></i> 上报</a>");
				</sec:authorize>
			}
			// 待审核
			if(status==<%=ResourceStatus.STATUS1 %>||status==<%=ResourceStatus.STATUS2%>||status==<%=ResourceStatus.STATUS6%>){
				<sec:authorize url="WF_pubresCheck">
				optArray.push("<a class=\"btn hover-red\" href=\"javascript:gotoCheck('"+rec.objectId+"')\" ><i class=\"fa fa-check-square-o\"></i> 审核</a>");
				</sec:authorize>
			}
// 			<sec:authorize url="/bres/toChange/2.action">
// 			optArray.push("<a class=\"btn hover-red\" href=\"javascript:change('"+rec.objectId+"')\" ><i class=\"fa fa-download\"></i> 转换</a>");
// 			</sec:authorize>
			// 审核已经通过
			if(status==<%=ResourceStatus.STATUS3 %>){
			    <sec:authorize url="/publishRes/PubOfflineRes.action">
				optArray.push("<a class=\"btn hover-red\" href=\"javascript:PubOfflineRes('"+rec.objectId+"')\" ><i class=\"fa  fa-level-down\"></i> 下线</a>");
				</sec:authorize>
 			}
			
			return createOpt(optArray);
		};
		
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
		
		function openImg(src){
			//var imgSrc = ''; 
			parent.parent.layer.open({
			    title: false,
			    shadeClose: true,
			    closeBtn: false,
			    btn: '关闭',
			    area: ['470px', '600px'],
			    content: '<img width=\'400\' height=\'520\' src='+src+' onclick="layer.closeAll();">'
			});

		}
		
		//预览页面
		function preview(objectId){
			//notice("提示",objectId,"5");
			//iframe窗
			parent.parent.layer.open({
			    type: 2,
			    title: '资源文件预览',
			    closeBtn: true,
			    shadeClose: true,
			    maxmin: true, //开启最大化最小化按钮
			    area: ['1280px', '630px'],
			    shift: 2,
			    content: '${path}/publishRes/preview.action?objectId='+objectId  //iframe的url，no代表不显示滚动条
			    
			});
			//$.openWindow('${path}/publishRes/preview.action?objectId='+objectId,'资源文件预览',1200,550);
		}
		function readRes(path,objectId){
			readFileOnline(path,objectId,"tszy");
		}
		//标签点击事件
// 		function clickTarget(obj){
// 			var codes = getChecked('data_div','objectId').join(',');
// 			 var ids=document.getElementsByName("objectId");
// 			 var resIds = '';
// 			    for(var i=0;i<ids.length;i++){
// 			         if(ids[i].checked){
// 			        	 resIds+=(ids[i].value+",");
// 			       }
// 			    }
// 			if(obj!=null){
// 				//alert(11);
// 			$.post('${path}/target/selectTarget.action?resId='+obj+'&libType=120&module='+$('#module').val()+'&type='+$('#type').val(),function(data){
// 				data = eval('('+data+')');
// 				if(data.status == 1){
// 					confirmRepeat(data,obj);
// 				}else{
// 					 $.alert('您还没有创建标签！');
// 					    return;
// 				}
// 			});
// 		}else if(codes == ''){
// 		    $.alert('请选择要添加标签的资源！');
// 		    return;
// 		}else {
// 			    $('#resId').val(resIds);
			    
// 			$.post('${path}/target/selectTarget.action?resId='+resIds+'&libType=120&module='+$('#module').val()+'&type='+$('#type').val(),function(data){
// 				data = eval('('+data+')');
// 				if(data.status == 1){
// 					confirmRepeat(data,resIds);
// 				}else{
// 					 $.alert('您还没有创建标签！');
// 					    return;
// 				}
// 			});
			
// 		}
// 		}
		//批量标签
// 		function clickTarget(){
// 			var codes = getChecked('data_div','objectId').join(',');
// 			 var ids=document.getElementsByName("objectId");
// 			 var resIds = '';
// 			    for(var i=0;i<ids.length;i++){
// 			         if(ids[i].checked){
// 			        	 resIds+=(ids[i].value+",");
// 			       }
// 			    }
// 			    alert(codes+"codes");
//  			    alert(resIds+"resID");
//  			    resIds = resIds.substring(0,resIds.length-1);
// 			    if(codes==""){
// 			    $.alert('请选择要添加标签的资源！');	
// 			    }else{
// 			    	var BatchGoto = {
// 			    			resIds:codes,
// 			    			publishType:'${param.publishType}',
// 			    			targetField:$('#targetField').val()
// 				    };
// 					var batchTarget = encodeURI(JSON.stringify(BatchGoto));
// 					$.ajax({
// 						url: "${path}/system/target/bachTarget.action",
// 					    type: 'post',
// 					    datatype: 'text',
// 					    data:"batchTarget=" + batchTarget,
// 					    success: function (returnValue) {
//  					    	if(returnValue==1){
//  					    		$.alert("操作成功!");
//  					    		initWaiting.close();
//  					    		query();
//  					    	}else{
//  					    		$.alert("操作失败!");
//  					    		initWaiting.close();
//  					    	}
// 					    }
// 					});
//  			   $.openWindow("${path}/system/target/bachTarget.jsp?publishType=${param.publishType}&resIds="+codes+"&targetField="+$('#targetField').val(),'批量标签',500,400);
// 			    }  
// 		}
		//批量标签
		function clickTarget(){
			var codes = top.index_frame.work_main.getChecked('data_div','objectId').join(',');
			    if(codes==""){
			    $.alert('请选择要添加标签的资源！');	
			    }else{
			    $.openWindow("${path}/system/target/bachTarget.jsp?publishType=${param.publishType}&targetField="+$('#targetField').val(),'批量标签',500,400);
			    }  
		}
		//加解锁
		function gotoUnLock(id,status){
			var initWaiting = $.waitPanel('正在操作请稍后...',false);
			var BatchGoto = {
					ids:id,
					status:status
		    };
			var batchGotoUn = encodeURI(JSON.stringify(BatchGoto));
			$.ajax({
				url:"${path}/pubres/wf/gotoUnLock.action",
			    type: 'post',
			    datatype: 'text',
			    data:"batchGoto=" + batchGotoUn,
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
			var status = getChecked('data_div','status').join(',');
			 if(ids==""){
				    $.alert('请选择需要解锁的资源！');
			 }else{
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
				}else if(status[i]=="1"){
					$.alert("只有状态为已通过的资源才能加解锁");
					break;
				}else{
					$.alert("只有状态为已通过的资源才能加解锁");
					break;
				}
			}
			if(objectId!=""){
			var initWaiting = $.waitPanel('正在操作请稍后...',false);
			var BatchGoto = {
					ids:objectId,
					status:"3"
		    };
			var batchGotoUn = encodeURI(JSON.stringify(BatchGoto));
			$.ajax({
				url:"${path}/pubres/wf/gotoUnLock.action",
			    type: 'post',
			    datatype: 'text',
			    data:"batchGoto=" + batchGotoUn,
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
		}
			//initWaiting.close();
	}
		//批量加锁
		function BatchGotoLock(){
			var ids = getChecked('data_div','objectId').join(',');
			var status = getChecked('data_div','status').join(',');
			 if(ids==""){
				    $.alert('请选择需要加锁的资源！');
			 }else{
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
				}else if(status[i]=="1"){
					$.alert("只有状态为已通过的资源才能加解锁");
					break;
				}else{
					$.alert("只有状态为已通过的资源才能加解锁");
					break;
				}
			}
			if(objectId!=""){
			var initWaiting = $.waitPanel('正在操作请稍后...',false);
			var gotoLock = {
					ids:objectId,
					status:"8"
		    };
			var batchGoto = encodeURI(JSON.stringify(gotoLock));
			$.ajax({
				url:"${path}/pubres/wf/gotoUnLock.action",
			    type: 'post',
			    datatype: 'text',
			    data:"batchGoto=" + batchGoto,
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
		}
			//initWaiting.close();
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
		
		/***添加***/
		function add(){
			upd('-1');
		}
		
		/***修改***/
		function upd(objectId){
			window.location.replace("${path}/publishRes/toEdit.action?publishType=${param.publishType}&objectId="+objectId+"&cbclassFieldValue="+$('#cbclassFieldValue').val()+"&cbclassField="+$('#cbclassField').val()+"&isTarget="+$('#isTarget').val()+"&taskFlagAddFile="+$('#taskFlagAddFile').val());
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
			 		var status = eval('rows[i]'+'.'+'status');
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
		//强制删除
		function enforceDelete(ids){
// 			ids = "urn:publish-d11dc3ba-4141-4545-bc8b-94734a26be03";
			var del = {
					ids:ids,
		    };
			var enforceDel = encodeURI(JSON.stringify(del));
			$.confirm('确定要删除所选数据吗？', function(){
				enforceDelete = $.waitPanel('正在删除...',false);
				$.ajax({
					url:'${path}/publishRes/enforceDeletes.action',
				    type: 'post',
				    datatype: 'text',
				    data:"enforceDel=" + enforceDel,
				    success: function (returnValue) {
				    	data = eval('('+returnValue+')');
				    	if(data.status==1){
				    		$.alert("删除成功!");
				    		enforceDelete.close();
				    		parent.queryForm();
				    	}else{
				    		$.alert("该资源已发布不能删除!");
				    		enforceDelete.close();
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
			window.location.href = "${path}/publishRes/toDetail.action?objectId="+objectId+"&publishType=${param.publishType}&status="+$('#status').val()+"&targetField="+$('#targetField').val()+"&targetNames="+$('#targetNames').val()+"&cbclassFieldValue="+$('#cbclassFieldValue').val()+"&cbclassField="+$('#cbclassField').val();
		}
		/* function importRes(){
			$.openWindow("${path}/publishRes/import/publishImport.jsp?publishType=${param.publishType}",'批量导入',600,400);
		} */
		//下载资源文件
		//1.若选中页面资源则根据选中资源id进行下载
		//2.若没在页面上直接选中资源，则按照输入的页码统计资源进行下载
		function batchDown(){
			var codes = getChecked('data_div','objectId').join(',');
			var dataDiv = $('#data_div').datagrid('getData');
			var pageSize = dataDiv.rows.length;   //获取页面显示的条数
			if (codes == '') {
				if(pageSize >0){
					if(codes == null || codes == ""){
						$("#Modal").modal('show');
					}
				}else{
					$.alert("资源为空，不能导出");
				}
			} else {
				doDown(codes);
			}
		}
		function doDown(codes){
			//down4Encrypt('${path}/publishRes/downloadPublishRes.action?ids='+codes);
			openIng = $.waitPanel('打开中请稍候...',false);
			$.ajax({
				url:'${path}/publishRes/downFileSize/${param.libType}.action?ids='+codes,
			    type: 'post',
			    datatype: 'text',
			    success: function (returnValue) {
			    	data = eval('('+returnValue+')');
			    	if(data.boo==1){
			    		openIng.close();
			    		//文件大小超出系统规定大小，就会默认选择FTP下载，并且被压缩
			    		$.openWindow("${path}/publishRes/import/downFileEncryptPwd.jsp?publishType=${param.publishType}"+"&ids="+codes+"&ftpFlag=2",'文件下载',630,280);
			    	}else{
			    		openIng.close();
			    		$.openWindow("${path}/publishRes/import/downFileEncryptPwd.jsp?publishType=${param.publishType}"+"&ids="+codes,'文件下载',630,280);
			    	}
			    }
			});
		}
		function batchOneCheck(){
			var codes = getChecked('data_div','objectId').join(',');
			if (codes == '') {
			    $.alert('请选择要批量审核的资源！');
			} else {
				var canApply = true;
			 	var rows = $('#data_div').datagrid('getChecked');
			 	for ( var i = 0; i < rows.length; i++) {
			 		var status = eval('rows[i]' + '.' + 'status');
			 		if(status == '1'){
			 		}else{
			 			$.alert('状态为非待审核的资源审核！');
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
			 		var status = eval('rows[i]' + '.' + 'status');
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
			 		var status = eval('rows[i]' + '.' + 'status');
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
			$.openWindow('${path}/appframe/plugin/timeLine/index.jsp','测试',1200,900);
		}
		function test1(){
			$.openWindow("${path}/system/target/test123.jsp",'测试',800,500);
		}
		function create(){
			window.location.href = '${path}/publishRes/create.jsp';
		}
		function exportMetedata(){
			//获得选中的ids;
			var ids = getChecked('data_div','objectId').join(',');
			$('#objectids').val(ids);
			$('#page').val("");
			$('#page1').val("");
			$('#total').html("");
			var dataDiv = $('#data_div').datagrid('getData');
			var pageSize = dataDiv.rows.length;   //获取页面显示的条数
			$('#pageSize').val(pageSize);
			if(ids == null || ids == ""){
				if(pageSize>0){
					$("#myModal").modal('show');
				}else{
					$.alert("数据为空，不能导出");
				}
				return;
			}
			
			/* //通过class属性获得分页数据大小
			var foo= $("select[class='pagination-page-list']"); 
			$(foo).each(function() {
				$('#pageSize').val(this.value);
			}) */
			
			
			//若直接选择指定的几条数据
			var searchParamCa = {
					ids:ids,
					batch:"1",
					publishType:'${param.publishType}'
					
		        };
			
			if (ids!= '') {
				var paramCa  = encodeURI(JSON.stringify(searchParamCa));
				downLoading = $.waitPanel('下载中请稍候...',false);	
				$.ajax({
					url:'${path}/publishRes/getExportExcel.action',
				    type: 'post',
				    datatype: 'json',
				    data:"searchParamCa=" + paramCa,
				    success: function (returnValue) {
				    	if(returnValue!=""){
				    		downLoading.close();
				    		window.location.href = '${path}/publishRes/getExportExcelDown.action?excelFilePath='+returnValue;
				    	}else{
				    		$.alert("下载数量超过数据字典定义大小，不能下载");
				    		downLoading.close();
				    	}
				    }
				});
			}
			
			
//			$.openWindow("${path}/publishRes/import/exportLevel.jsp?ids="+ids,'导出级别',350,130);
// 			if(ids!=""){
// 				window.location.href = '${path}/publishRes/getExportExcel.action?ids='+ids;	
// 			}else{
// 				window.location.href = '${path}/publishRes/getExportExcel.action?publishType=${param.publishType}'+<app:ListPageParameterTag />;
// 			}
		}
		
		//生成二次查询输入内容的隐藏域
		function checkSecond(){
			$('#secondCore').children().remove();
			$.ajax({
				async : false,
				type: 'post',
				dataType :'text',
				url : '${path}/publishRes/getSecondQuery.action?publishType='+$('#publishType').val(),
				success:function(data){
					//data拼接格式元数据英文名称+文本框类型+查询模式
					var coreDate = $("#secondCore");
					var second = data.split(",");
					for(var i=0;i<second.length;i++){
						var query = second[i].split(":");
						var coreFieldType = query[1];   //文本框类型  1:单文本   2,3,4:数据字典Xmenu插件  7:日期  6:树形
						var coreQueryModel = query[2];	//查询模式：1:查询不用于  2：完全匹配查询 3：模糊匹配查询  4:区间
						var secondValue = "";
						var startTime = "";
						var endTime = "";
						var treeValue = "";
						var querySecond = "";
						var endSecond = ""
						if(coreFieldType != 7 && coreFieldType != 6){
							secondValue = $('#'+query[0]).val();
						}else if(coreFieldType == 7){
							startTime = $('#'+query[0]+"_metaField_StartDate").val();
							startTime = startTime.substring(1,startTime.length-1);
							endTime = $('#'+query[0]+"_metaField_EndDate").val();
							endTime = endTime.substring(1,endTime.length-1);
						}else{
							treeValue = $('#'+query[0]).val();
						}
							
						
						if(coreFieldType != 7 && coreFieldType != 6){
							if(secondValue != "" && secondValue != null){
								secondValue = encodeURI(secondValue);
								if(coreQueryModel != 1){
									if(coreQueryModel == 2){
										querySecond = $("<input type=\"hidden\" id="+query[0]+"_metaField"+" value ="+secondValue+" name="+query[0]+"_metaField  qMatch=\"=\">");
									}else if(coreQueryModel == 3){
									    querySecond = $("<input type=\"hidden\" id="+query[0]+"_metaField"+" value ="+secondValue+" name="+query[0]+"_metaField qMatch=\"like\">" );
									}
								}else{
									 querySecond = $("<input type=\"hidden\" id="+query[0]+"_metaField"+" value ="+secondValue+" name="+query[0]+"_metaField qMatch=\"like\">" );
								}
							}
						}else if(coreFieldType == 7){
							if(startTime != "" && startTime != null){
								 querySecond = $("<input type=\"hidden\" id="+query[0]+"_metaField_StartDate"+" name="+query[0]+"_metaField_StartDate  value ="+startTime+" qMatch=\"=\">");
							}
							if(endTime != "" && endTime != null){
								endSecond = $("<input type=\"hidden\" id="+query[0]+"_metaField_EndDate"+" name="+query[0]+"_metaField_EndDate  value ="+endTime+" qMatch=\"=\">");
							}
						}else{
							if(treeValue != "" && treeValue != null){
							     querySecond = $("<input type=\"\hidden\" id="+query[0]+"_metaField value="+treeValue+" name="+query[0]+"_metaField qMatch=\"=\">");
							}
						}
						if(querySecond != "" && querySecond != null){
							querySecond.appendTo(coreDate);
							if(endSecond != "" && endSecond != null){
								endSecond.appendTo(coreDate);
							}
						}
						
					}
				}
			}); 
			query();   //为查询按钮绑定的方法
			layer.closeAll();
		}
		
		
		function closelayer(){
			layer.closeAll();
		}
		function beachReplace(){
// 			$('#beachReplace').modal().css({
// 			    width: 'auto',
// 			    'margin-center': function () {
// 			       return -($(this).width() / 2);
// 			   }
// 			});
			var dataDiv = $('#data_div').datagrid('getData');
			var pageSize = dataDiv.rows.length;   //获取页面显示的条数
				if(pageSize>0){
					$("#beachReplace").modal('show');
				}else{
					$.alert("数据为空，不能执行此操作");
					return;
				}
		}
		
		
		
		function importTM(){
			parent.parent.layer.open({
			    type: 2,
			    title: '添加条目资源',
			    closeBtn: true,
			    shadeClose: true,
			    maxmin: true, //开启最大化最小化按钮
			    area: ['500px', '400px'],
			    shift: 2,
			    content: '${path}/publishRes/import/importTM.jsp?publishType=${param.publishType}' //iframe的url，no代表不显示滚动条
			    
			});
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
	  <input type="hidden" id="isTarget" name="isTarget" value=""/>
	  <input type="hidden" id="status" name="status" value="${status}"/>
	  <input type="hidden" id="taskFlagAddFile" name="taskFlagAddFile" value="${taskFlagAddFile}"/>
	  <input type="hidden" id="cbclassFieldValue" name="cbclassFieldValue"value="${cbclassFieldValue}"/>
	  <input type="hidden" id="cbclassField" name="cbclassField"value="${cbclassField}"/>
	  <input type="hidden" id="creator" name="creator" value="${param.creator }"/>
	  <input type="hidden" id="targetField" name="targetField" value="${param.targetField }"/>
	  <input type="hidden" id="targetNames" name="targetNames" value="${param.targetNames}"/>
	  <input type="hidden" id="modifiedStartTime" name="modifiedStartTime" value="${param.modifiedStartTime }" qMatch="=" />
	  <input type="hidden" id="modifiedEndTime" name="modifiedEndTime" value="${param.modifiedEndTime }"  qMatch="="/>
	  <input type="hidden" id="libType" name="libType" value="${param.libType }" />
	  <input type="hidden" id="queryType" name="queryType" value="${param.queryType }"/>
	  <input type="hidden" id="publishType" name="publishType" value="${publishType}"/>
	  <input type="hidden" id="pageSize" name="pageSize" value=""/>
<%-- 	   <app:ListPageParameterTag /> --%>
	<!-- 提交标签表单    -->
<form action="#" id="coreData" class="form-horizontal" method="post">
	<div id="secondCore"></div>
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
					<li class="active">管理资源</li>
					<li class="active">资源列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
					<sec:authorize url='/publishRes/2add.action'>
					<input type="button" value="添加" class="btn btn-primary" onclick="add()"/>  
					</sec:authorize>
<%-- 					<sec:authorize url='/publishRes/import.action'>
					<input type="button" value="批量导入" class="btn btn-primary" onclick="importRes()"/>
					</sec:authorize> --%>
					<c:if test="${isTM=='1'}">
						<sec:authorize url='/publishRes/importTM.action'>
						<input type="button" value="条目导入" class="btn btn-primary" onclick="importTM()"/>
						</sec:authorize>
					</c:if>
					<c:if test="${isTM=='0'}">
						<sec:authorize url='/publishRes/importResult.action'>
						<input type="button" value="批量导入" class="btn btn-primary" onclick="lookBatchResult()"/>
						</sec:authorize>
					</c:if>
					<sec:authorize url='/publishRes/gotoUnLock.action'>
					<input type="button" value="批量加锁" class="btn btn-primary" onclick="BatchGotoLock()"/>
					</sec:authorize>
					<sec:authorize url='/publishRes/gotoLock.action'>
					<input type="button" value="批量解锁" class="btn btn-primary" onclick="BatchGotoUnLock()"/>
					</sec:authorize>
					<sec:authorize url='/publishRes/delete.action'>
					<input type="button" value="批量删除" class="btn btn-primary" onclick="del()"/>
					</sec:authorize>
					<sec:authorize url='/publishRes/download.action'>
					<input type="button" value="批量下载" class="btn btn-primary" onclick="batchDown()"/>
					</sec:authorize>
					<sec:authorize url='/publishRes/doApply.action'>
						<input type="button" value="批量上报" class="btn btn-primary" onclick="batchDoApply()"/>
					</sec:authorize>
					<sec:authorize url='/publishRes/pubTarget.action'>
						<input type="button" value="批量标签" class="btn btn-primary" onclick="clickTarget()"/>
					</sec:authorize>
					<sec:authorize url='WF_pubresCheck'>
						<input type="button" value="批量审核" class="btn btn-primary" onclick="batchOneCheck()"/>
					</sec:authorize>
					<sec:authorize url='WF_pubresCheck'>
						<input type="button" value="导出元数据" class="btn btn-primary" onclick="exportMetedata()"/>
					</sec:authorize>
					<sec:authorize url='/publishRes/beachReplace.action'>
						<input type="button" value="批量替换" class="btn btn-primary" onclick="beachReplace()"/>
					</sec:authorize>
					
					<input type="button" value="测试页面" class="btn btn-primary" onclick="test()"/>
<!-- 					<input type="button" value="创建页面" class="btn btn-primary" onclick="add()"/> -->
						<input type="hidden" id="secondQuery" name="secondQuery" value=""/>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
				<%@ include file="/publishRes/resCheck.jsp" %>
				<%@ include file="/publishRes/publishResFilePage.jsp" %>
				<%@ include file="/beachReplaceMetadata/beachReplace.jsp" %>
			</div>
		</div>
	</div>
<%-- 	<app:MainPageTargetTag /> --%>
</body>
</html>