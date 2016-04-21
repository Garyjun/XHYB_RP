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
	   		var _url = 'detailList.action';
	   		var _pk = 'id';
	   		var _conditions = ['taskId','status'];
	   		var _sortName = 'excelNum';
	   		var _sortOrder = 'asc';
// 	   		,hidden : true
			var _colums = [{field:'remark',title:'备注',width:fillsize(0.5),sortable:true,formatter:$remarkDesc},
//			               {field:'excelNum',title:'导入数',width:fillsize(0.5),sortable:true,hidden : true},
			               {field:'statusZh',title:'状态',width:fillsize(0.07),align:'center',sortable:true},
// 			               {field:'checkRepeatField',title:'查重字段',width:fillsize(0.6),sortable:true},
							{field:'createTime',title:'导入时间',width:fillsize(0.12),align:'center',sortable:true},
							{field:'paths',title:'路径',width:fillsize(0.2),align:'center'},
							{field:'opt1',title:'操作',width:fillsize(0.07),align:'center',align:'center',formatter:$operate}];
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,false,true,false);
		});
		$operate = function(value,rec){
			var opt = "";
			if(rec.status==5){
				opt += "&nbsp;&nbsp;&nbsp;&nbsp;<a class=\"btn hover-blue\" href=\"javascript:checkRepeat('"+rec.id+"')\" ><i class=\"fa fa-eye\"></i> 查重</a>";
			}
			/* if(rec.status==4){
				opt += "&nbsp;&nbsp;&nbsp;&nbsp;<a class=\"btn hover-blue\" href=\"javascript:reImport('"+rec.id+"')\" ><i class=\"fa fa-repeat\"></i> 查重</a>";
			} */
			return opt;
		};
		$remarkDesc = function(value,rec){
			var remark = "";
			if(rec.task.filetype==2){//是xml方式导入
				remark = rec.remark.replace("第【0】行", "");
			}else{
				remark = rec.remark;
			}
			return remark;
		};
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
				msg+= '<tr style="white-space:nowrap;"><td><input type="radio" id="repeatResId" name="repeatResId" value="'
				+obj.objectId+'='+obj.res_version+'"/></td><td><b>标题：</b>'+obj.metadataMap.title;
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
				var filetype = $('#filetype').val();
				var status = $('#status').val();
				if(filetype==1){
					var excelPath = $('#excelPath').val();
					excelPath = encodeURI(encodeURI(excelPath));
					window.location.href = '${path}/publishRes/beachDetaillLogExportExcel.action?status='+status+"&excelPath="+excelPath+"&id="+$('#taskId').val();
				}
				if(filetype==2){
					window.location.href = '${path}/publishRes/beachDetaillLogExportXml.action?status='+status+"&id="+$('#taskId').val();
				}
			}
			function reImport(id){
				$.ajax({
					url: '${path}/publishRes/reImport.action?id='+id,
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
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
			<input type="hidden" id="taskId" name="task.id" value="${task.id }" qType="long" />
			<input type="hidden" id="taskIdAgain" name="taskIdAgain" value="${taskIdAgain}"/>
			<input type="hidden" id="relationIds" name="relationIds" value=""/>
			<input type="hidden" id="repeatType" name="repeatType" value=""/>
<!-- 			<input type="hidden" id="status" name="status" value="" qType="int"/> -->
			<input type="hidden" id="publishType" name="publishType" value="${param.publishType}"/>
			<input type="hidden" id="resVersion" name="resVersion" value=""/>
			<input type="hidden" id="objectId" name="objectId" value=""/>
			<input type="hidden" id="excelPath" name="excelPath" value="${excelPath}"/>
			<input type="hidden" id="filetype" name="task.filetype" value="${task.filetype }" qType="long" />
			<div class="panel panel-default" style="height: 100%;">
				<div class="panel-heading" id="div_head_t">
					<ol class="breadcrumb">
						<li><a href="###">资源管理</a></li>
						<li class="active">批量导入日志</li>
						<li class="active">详细列表</li>
					</ol>
				</div>
				<div class="panel-body height_remain" >
					<div style="float: left;margin-bottom:10px;">
						<div class="form-inline" style="float:left;">
							<div class="form-group">
								<div class="form-inline">
              						<input class="btn btn-primary red" type="button" value="返回" onclick="goBack();"/>
           						</div>
        					</div>
        				</div>
        				</div>
        				<div style="float: right;margin-bottom:10px;">
        					<div class="form-inline" style="float:right;">
        					<div class="form-group">
        					<label class="control-label" for="processName">类型:</label>
							      <select name="status" id="status" class="form-control" qType="int" style="width: 99px;">
							      	<option value="">全部</option>
									<option value="3">成功</option>
									<option value="4">失败</option>
									<option value="5">重复</option>
								</select>
							</div>
<!-- 							<div class="form-group"> -->
<!-- 									<input type="text" class="form-control" id="status1" name="status1" qMatch="like" placeholder="输入状态" /> -->
<!-- 							</div> -->
							<sec:authorize url="/system/dataManagement/dataDict/list.action">
									<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
<!-- 									<input type="reset" value="清空" class="btn btn-primary"/> -->
							</sec:authorize>
							<div class="form-group">
              						<input class="btn btn-primary" type="button" value="导出" onclick="exportMetedata();"/>
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