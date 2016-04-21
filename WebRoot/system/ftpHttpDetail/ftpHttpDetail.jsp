<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>详细</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
		$(function(){
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = 'ftpDetailList.action';
	   		var _pk = 'id';
	   		var _conditions = ['taskId','status'];
	   		var _sortName = 'createTime';
	   		var _sortOrder = 'asc';
// 	   		,hidden : true
			var _colums = [{field:'resName',title:'资源名称',width:fillsize(0.2),align:'center'},
			               {field:'status',title:'压缩状态',width:fillsize(0.1),align:'center',formatter:$status},
							{field:'totalFileSize',title:'文件大小',width:fillsize(0.1),align:'center',sortable:true},
							{field:'ftpPath',title:'文件路径',width:fillsize(0.1),align:'center'},
							{field:'createTime',title:'完成时间',width:fillsize(0.13),align:'center',sortable:true},
							{field:'pwd',title:'是否加密',width:fillsize(0.13),align:'center',formatter:$pwd},
							];
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false,true,false);
		});
		$operate = function(value,rec){
			var opt = "";
			if(rec.status==0){
				opt += "&nbsp;&nbsp;&nbsp;&nbsp;<a class=\"btn hover-blue\" href=\"javascript:checkRepeat('"+rec.id+"')\" ><i class=\"fa fa-eye\"></i> 查重</a>";
			}
			return opt;
		};
		$status = function(value,rec){
			if(rec.status=='3'){
				return "待处理";
			}else if(rec.status=='2'){
				return "处理中";
			}else{
				return "已完成";
			}
		}
		$pwd = function(value,rec){
			if(rec.pwd=="" ||rec.pwd== null)
				return "否";
			else
				return "是";
		}
		//查重
		function checkRepeat(id){
			var excelPath = $('#excelPath').val();
				excelPath = encodeURI(encodeURI(excelPath));
				$.ajax({
					url:"${path}/publishRes/importDetailcheckRepeat.action?id="+id+"&excelPath="+excelPath+"&publishType="+$('#publishType').val(),
				    type: 'post',
				    datatype: 'text',
				    success: function (data) {
				    	data = eval('('+data+')');
						if(data.status == 1){
							confirmRepeat(data.res,id);
						}else{
							$.alert("不重复");
						}
				    }
				});
		}
		var relationIds = '';
		function confirmRepeat(res,id){
			var msg = '<table border="0" cellpadding="0" cellspacing="0" >';
			var resVersions = new Array();
				for(var i = 0;i<res.cas.length;i++){
				var obj = res.cas[i];
				msg+= '<tr style="white-space:nowrap;"><td><input type="radio" id="repeatResId" name="repeatResId" value="'+obj.objectId+'='+obj.res_version+'"/></td><td><b>标题：</b>'
				+obj.metadataMap.title;
				if(typeof(obj.objectId) != 'undefined' && obj.objectId != ''){
					msg+= "<a class=\"btn hover-red\" href=\"javascript:detailOres('"+obj.objectId+"');\"><i class=\"fa fa-sign-out\"></i>详细</a>";
				}
				resVersions.push(obj.resVersion);
				msg+= '</td></tr>';
				if(relationIds == ''){
					relationIds += obj.objectId;
				}else{
					relationIds += ',' + obj.objectId;
				}
			}
			
			msg +='<script type=\"text/javascript\">function detailOres(objectId){$.openWindow(\"${path}\/publishRes\/toDetail.action?publishType=${param.publishType}&flagSta=3&objectId=\"+objectId,\"资源详细\",1170,500);}<\/script>';
			$.simpleDialog({
				title:'存在重复版本', 
				content:'<font color=\"red\">系统检测到重复资源，请检查！</font>'+msg, 
				button:[{name:'忽略',callback:function()
			        	{
			        	repeatCancel =1;
			        	}
			       		},
			       		{name:'新版本',callback:function(){
				        	$('#objectId').val('');
				        	if(resVersions.length>0){
				        		var maxValue = resVersions[0];
				        		for(var i=1;i<resVersions.length;i++){
				        			if(maxValue<resVersions[i]){
				        				maxValue = resVersions[i];
				        			}
					        	}
				        	}
				        	var newResVersion = parseInt(maxValue)+1;
				        	$('#resVersion').val(newResVersion);
				        	$('#relationIds').val(relationIds);
				        	$('#repeatType').val("");
				        	saveRes(id);
				        }},
				        {name:'元数据增量(文件增量)',callback:function(){
							var checkedObjectId = top.$('input:radio[name="repeatResId"]:checked').val();
							if(typeof(checkedObjectId) != 'undefined' && checkedObjectId != ''){
								var checkedObjectIdAndVersion = checkedObjectId.split("=");
								$('#objectId').val(checkedObjectIdAndVersion[0]);
								$('#resVersion').val(checkedObjectIdAndVersion[1]);
								$('#repeatType').val("2");
								saveRes(id);
							}else{
								$.alert('请选择要覆盖的资源');
								return false;
							}
						}},
				        {name:'元数据覆盖(文件增量)',callback:function(){
				        	var checkedObjectId = top.$('input:radio[name="repeatResId"]:checked').val();
							if(typeof(checkedObjectId) != 'undefined' && checkedObjectId != ''){
								var checkedObjectIdAndVersion = checkedObjectId.split("=");
								$('#objectId').val(checkedObjectIdAndVersion[0]);
								$('#resVersion').val(checkedObjectIdAndVersion[1]);
								$('#repeatType').val("4");
								saveRes(id);
							}else{
								$.alert('请选择要覆盖的资源');
								return false;
							}
						}}
				]
			});
		}
// 	     $(function() {
//  	    	 $('#id').val('${taskId}');
// 	     });
			function saveRes(id){
				var excelPath = $('#excelPath').val();
				excelPath = encodeURI(encodeURI(excelPath));
				saveResWaiting = $.waitPanel('正在保存资源...',false);
				var repeatType = $('#repeatType').val();
				var url = "";
				if($("#saveAndApply").val()==1){
					url = "${path}/res/wf/doSaveAndSubmit.action?libType=${libType}";
				}else{
					url = "${path}/publishRes/beachDetailCheckrepeatSave.action?id="+id+"&excelPath="+excelPath+"&resVersion="+$('#resVersion').val()+"&repeatType="+repeatType+"&publishType="+$('#publishType').val()+"&objectId="+$('#objectId').val()+"&taskId="+$('#taskId').val();
				}
					$.ajax({
						url:url,
					    type: 'post',
					    datatype: 'text',
					    success: function (data) {
							if(data==""){
								var excelPath = $('#excelPath').val();
								excelPath = encodeURI(encodeURI(excelPath));
								window.location.href = "${path}/publishRes/import/detail.action?id="+$('#taskIdAgain').val()+"&excelPath="+excelPath+"&publishType="+$('#publishType').val();
							}else{
								$.alert("操作失败"+data);
							}
							saveResWaiting.close();
					    }
				});
			}
			function query(){
				$grid.query();
			}
			function exportMetedata(){
				var status = $('#status').val();
				var excelPath = $('#excelPath').val();
				excelPath = encodeURI(encodeURI(excelPath));
				window.location.href = '${path}/publishRes/beachDetaillLogExportExcel.action?status='+status+"&excelPath="+excelPath+"&id="+$('#taskId').val();
			}
			function lookDetail(id){
//		 		excelPath = encodeURI(encodeURI(excelPath));
		 		$.openWindow("${path}/system/ftpHttpDetail/ftpHttpList.jsp?id="+id+"&publishType="+$('#libType').val()+"&divWidth=1020px",'文件列表',1080,500);
//		 		window.location.href = "${path}/system/ftpHttpDetail/ftpHttpList.jsp?id="+id+"&publishType="+$('#libType').val();
			}
			function goFtp(){
				window.location.href = '${path}/system/ftpHttpDetail/ftpHttpList.jsp?flag=1';
			}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
			<input type="hidden" id="taskId" name="task.id" value="${task.id }" qType="long" />
			<input type="hidden" id="taskIdAgain" name="taskIdAgain" value="${taskIdAgain}"/>
			<input type="hidden" id="relationIds" name="relationIds" value=""/>
			<input type="hidden" id="flag" name="flag" value="${flag}"/>
			<input type="hidden" id="repeatType" name="repeatType" value=""/>
<!-- 			<input type="hidden" id="status" name="status" value="" qType="int"/> -->
			<input type="hidden" id="publishType" name="publishType" value="${param.publishType}"/>
			<input type="hidden" id="resVersion" name="resVersion" value=""/>
			<input type="hidden" id="objectId" name="objectId" value=""/>
			<input type="hidden" id="excelPath" name="excelPath" value="${excelPath}"/>
			<div class="panel panel-default" style="height: 100%;">
<!-- 				<div class="panel-heading" id="div_head_t"> -->
<!-- 					<ol class="breadcrumb"> -->
<!-- 						<li class="active">详细列表</li> -->
<!-- 					</ol> -->
<!-- 				</div> -->
				<div class="panel-body height_remain" >
					<div style="float: left;margin-bottom:10px;">
						<div class="form-inline" style="float:left;">
							<div class="form-group">
								<div class="form-inline">
								<c:if test="${flag==undefined}">
              						<input class="btn btn-primary red" type="button" value="返回" onclick="lookDetail();$.closeFromInner();"/>
              					</c:if>
              					<c:if test="${flag!=undefined}">
              						<input class="btn btn-primary red" type="button" value="返回" onclick="goFtp();$.closeFromInner();"/>
              					</c:if>
           						</div>
        					</div>
        				</div>
        				</div>
        				<div style="float: right;margin-bottom:10px;">
        					<div class="form-inline" style="float:right;">
<!--         					<div class="form-group"> -->
<!--         					<label class="control-label" for="processName">类型:</label> -->
<!-- 							      <select name="status" id="status" class="form-control" qType="int" style="width: 99px;"> -->
<!-- 							      	<option value="">全部</option> -->
<!-- 									<option value="1">成功</option> -->
<!-- 									<option value="2">失败</option> -->
<!-- 									<option value="0">重复</option> -->
<!-- 								</select> -->
<!-- 							</div> -->
<!-- 							<div class="form-group"> -->
<!-- 									<input type="text" class="form-control" id="status1" name="status1" qMatch="like" placeholder="输入状态" /> -->
<!-- 							</div> -->
							<sec:authorize url="/system/dataManagement/dataDict/list.action">
									<input type="button" value="" class="btn btn-default" onclick="query()"/>
<!-- 									<input type="reset" value="清空" class="btn btn-primary"/> -->
							</sec:authorize>
							<div class="form-group">
              						<input class="btn btn-default" type="button" value="" onclick="exportMetedata();"/>
           					</div>
						</div>
					</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
				</div>
			</div>
	</div>
<script type="text/javascript">
	function goBack(){
		window.location.href = '${path}/publishRes/import/importResultList.jsp?publishType='+$('#publishType').val();
		}
</script>
</body>
</html>