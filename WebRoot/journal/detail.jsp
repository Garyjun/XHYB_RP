<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>基础资源管理</title>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		var detailFlag = '${detailFlag}';
		var data='';
		$(function() {
			init_grid();
			
			var _divId = 'article_div';
	   		var _url = '${path}/journal/articleList.action?publishType='+$('#publishType').val();
	   		var _pk = 'objectId';
	   		var _conditions = ["keyWord","articleTitle","magazineNum"];
	   		var _sortName = 'page';
	   		var _sortOrder = 'desc';
			var _colums = [
							{field:'metadataMap.title',title:'篇名',width:fillsize(0.3),align:'center'},
							{field:'metadataMap.author',title:'作者',width:fillsize(0.1),align:'center'},
							{field:'metadataMap.page',title:'页码',width:fillsize(0.15),align:'center'},
							{field:'opt1',title:'操作',width:fillsize(0.22),align:'center',formatter:$operate,align:'center'}];
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<a class=\"btn hover-red\" href=\"javascript:lookDetail('"+rec.id+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
/* 			if(rec.failExcelPath  != undefined && rec.failExcelPath  != ''){
				opt += "<a class=\"btn hover-red\" href=\"javascript:downFailLog('"+rec.failExcelPath+"')\" ><i class=\"fa fa-download\"></i> 失败日志</a>";
			}
			if( rec.fileNotExistPath  != undefined && rec.fileNotExistPath  != '' ){
				opt += "<a class=\"btn hover-red\" href=\"javascript:downFailLog('"+rec.fileNotExistPath+"')\" ><i class=\"fa fa-download\"></i> 文件不存在日志</a>";
			} */
 			opt += "&nbsp;&nbsp;&nbsp;&nbsp;<a class=\"btn hover-blue\" href=\"javascript:deleteRecord('"+rec.id+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a>";
			return opt;
					
		};
		
		function closeWindow(){
			$("#versionText").modal('hide');
		}
		function goBackTaskWaring(){
			if($('#copyrightWaring').val()=='1'){
				parent.gotoCopyrightWarning();
			}else{
				parent.queryForm();
			}
		}
	</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap ">
 <div class="panel panel-default">
    <div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li class="active">资源管理
					</li>
					<li class="active">资源视图</li>
					<li class="active">资源详细</li>
				</ol>
	</div>
 </div>	
<form action="#" id="coreData" class="form-horizontal">
<app:ObjectMetadataDetailTag   object="${bookCa}" publishType="${publishType}"/>
<!--   <app:ObjectMetadataDetailTag   object="${bookCa}" publishType="66"/> -->
 <input type="hidden" id="nodeOpt" name="nodeOpt" value=""/>
 <input type="hidden" id="status" name="status" value="${status}"/>
 <input type="hidden" id="uploadFile" name="uploadFile" value=""/>
 <input type="hidden" id="objectId" name="objectId" value="${objectId}"/>
 <input type="hidden" id="targetField" name="targetField" value="${targetField}"/>
 <input type="hidden" id="targetNames" name="targetNames" value="${targetNames}"/>
 <input type="hidden" id="cbclassFieldValue" name="cbclassFieldValue" value="${cbclassFieldValue}"/>
 <input type="hidden" id="cbclassField" name="cbclassField" value="${cbclassField}"/>
 <input type="hidden" id="copyrightWaring" name="copyrightWaring" value="${copyrightWaring}"/>
 <input type="hidden" id="bookPath" name="bookPath" />
 <input type="hidden" id="editOldName" name="editOldName" value=""/>
 <input type="hidden" id="fileFlag" name="fileFlag"/>
 
 <input type="hidden" id="publishType" name="magazineYear" value="${param.publishType }"/>
 
 <div class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" 
			aria-labelledby="mySmallModalLabel" aria-hidden="true" id="versionText">
  			<div class="modal-dialog modal-sm">  			
  				<div class="modal-content"> 
  					<div class="modal-body" >	
  					    <div class="col-xs-6">
  					      <input id="labelName" type="text" class="form-control" placeholder="输入名称"/>
  					    </div>
  					    <button type="button" class="btn btn-primary"  onclick="optNode();">确定</button>
						<button type="button" class="btn btn-primary" onclick="closeWindow();">关闭</button>				
					</div>
				</div>
  			</div>
	</div>	
<%--  <c:if test="${!empty ztreeJson}">
        <div class="portlet">
	        <div class="portlet-title">
	            <div class="caption">资源文件</div> 
	        </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <div class="col-md-12 zTreeBackground">
<!--                                  style="width:210%" -->
                                	 <ul id="directoryTree" class="ztree form-wrap" style="overflow:auto;"></ul>
                                </div>
                            </div>
                        </div>
                       
                    </div>
                   
                </div>
            </div>
        </div>
        </c:if> --%>
    <div class="portlet portlet-border">
		<div class="portlet-title">
			<div class="caption">文章列表</div>
		</div>
	  <div class="panel-body" id="999" >
	  	<div style="margin: 0px 10px 10px 0px;">
			<div class="form-inline" style="float:right;">
						<div class="form-group">
							<label class="sr-only">名称:</label> 
							<input type="text" class="form-control" id="name" name="name" qMatch="like" placeholder="输入敏感词" />
							<input type="text" class="form-control" id="similarWords" name="similarWords" qMatch="like" placeholder="输入相似词" />
						</div>
						<sec:authorize url="/system/word/list.action">						
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<button type="button" class="btn btn-primary" onclick="formReset();">清空</button>
						</sec:authorize>
				</div>
			   
			   <div id="article_div" class="data_div height_remain" style="width:930px;"></div>
		  </div>
	  </div>
	</div>
	<sec:authorize url='WF_pubresCheck'>
		<%@ include file="/bres/PubWFOperate.jsp" %>
		<div class="form-actions" align="center">
			<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
			<input class="btn btn-primary" type="button" value="返回" onclick="javascript:goBackTask();"/>
			<!-- <button type="button" class="btn btn-lg orange" onclick="goBackTask();">返回</button> -->
		</div>
	</sec:authorize>
 </form>
 <script type="text/javascript">
 	function goBackTask(){
		if('${operateFrom}'=='TASK_LIST_PAGE'){
			window.location.href = "${path}/TaskAction/toList.action"; 
		}else{
			if($('#targetNames').val()!=""){
				parent.selectTargetRes();
			}else if($('#cbclassFieldValue').val()!=""){
				var url = '${path}/publishRes/publishResList.action?publishType='+parent.$('#publishType').val()+'&status='+parent.$('#status').val()+'&offline=${param.offline}';
		 		url +='&'+$('#cbclassField').val()+'='+$('#cbclassFieldValue').val();
		 		parent.$('#work_main').attr('src',url);
			}else{
				parent.queryForm();
			}
		}
	}
</script>
</div>
</body>
</html>