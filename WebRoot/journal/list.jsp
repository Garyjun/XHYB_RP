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
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/jQueryValidationEngine/js/languages/jquery.validationEngine-zh_CN.js"
	charset="utf-8"></script>
	<style>
		#flag {
			margin-bottom: 5px;
		}
	</style>
	<script type="text/javascript">
	var publishType='${publishType}';
	var status='${param.status}';
		$(function(){
			//定义一datagrid
			if(publishType!=""){
				$('#publishType').val(publishType);
			}
			if($('#publishType').val()==""){
				$('#publishType').val('${param.publishType}');
			}
	   		var _divId = 'data_div';
	   		var _url = '${path}/journal/journalList.action?publishType='+publishType;
	   		var _pk = 'objectId';
	   		var _conditions = ['magazineYear','numOfYear','magazineNum'];
	   		var _sortName = 'createTime';
	   		var _sortOrder = 'desc';
	   		var _colums = [ 
							{ title:'封面', field:'metadataMap.cover' ,width:100, align:'center',formatter:$coverDesc },
						    { title:'期刊名', field:'metadataMap.magazine' ,width:100, align:'center' },
						    { title:'国内刊号', field:'metadataMap.localSerialNumber' ,width:100, align:'center' },
						    { title:'国际刊号', field:'metadataMap.overseasSerialNumber' ,width:100, align:'center' },
						    { title:'期刊年份', field:'metadataMap.magazineYear' ,width:100, align:'center' },
						    { title:'当前期数', field:'metadataMap.numOfYear' ,width:100, align:'center' },
						    { title:'总期数', field:'metadataMap.magazineNum' ,width:100, align:'center' },
						    { title:'创建时间', field:'metadataMap.createTime' ,width:100, align:'center' },
							{title:'操作',field:'opt1',width:fillsize(0.17),align:'center',formatter:$operate}
						];
			accountHeight();
	   		$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		/***操作列***/
		$operate = function(value,rec){
			console.info(rec);
			var optArray = new Array();
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>");
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:upd('"+rec.objectId+"')\" ><i class=\"fa fa-edit\"></i> 修改</a>");
			return createOpt(optArray);
		};
		
		$coverDesc = function (value,rec){
			var cover = "";
			if(rec.metadataMap.cover != "" && rec.metadataMap.cover != "undefined" && rec.metadataMap.cover != undefined){
				//console.info(rec.metadataMap.cover);
				cover="<img src='${path}/fileDir/"+rec.metadataMap.cover+"' class='Img' width='60' height='80' alt='封面不存在' />";
			}else{
				cover="<img src='${path}/appframe/main/images/cover_min.png' class='Img' width='60' height='80' >";  
			}
			return cover;
		}
		
		function openImg(src){
			src = ''; 
			parent.parent.layer.open({
			    title: false,
			    shadeClose: true,
			    closeBtn: false,
			    btn: '关闭',
			    zIndex: 25,
			    area: ['470px', '600px'],
			    content: '<img width=\'400\' height=\'520\' src='+src+' onclick="layer.closeAll();">'
			});

		}
		
		/***修改***/
		function upd(objectId){
			window.location.replace("${path}/publishRes/toEdit.action?publishType=${param.publishType}&objectId="+objectId+"&cbclassFieldValue="+$('#cbclassFieldValue').val()+"&cbclassField="+$('#cbclassField').val()+"&isTarget="+$('#isTarget').val()+"&taskFlagAddFile="+$('#taskFlagAddFile').val());
		}
	
		function query(){
			$grid.query();
		}
		function queryList(){
			$('#magazineYear').val("");
			$('#numOfYear').val("");
			$grid.query();
		}
		/***查看***/
		function detail(objectId){
//			window.location.href = "${path}/journal/toDetail.action?objectId="+objectId+"&publishType=${param.publishType}&status="+$('#status').val();
			window.location.href = "${path}/journal/toDetail.action?objectId="+objectId+"&&publishType=${param.publishType}&status=0";
		}

		function closelayer(){
			layer.closeAll();
		}
		
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<input type="hidden" id="status" name="status" value="${status}"/>
		<input type="hidden" id="creator" name="creator" value="${param.creator }"/>
		<input type="hidden" id="publishType" name="publishType" value="${param.publishType }"/>
		
		<input type="hidden" id="magazineYear" name="magazineYear" value="${param.magazineYear }"/>
     	<input type="hidden" id="numOfYear" name="numOfYear" value="${param.numOfYear }"/>
	  
		<div class="portlet portlet-default" style="height: 100%;">
			<div class="portlet-title" style="padding: 0px;background-color: #e7e1d3;">
				<div class="caption" style="background-color:#e7e1d3; ">查询条件 <div style="float:right;"><a href="javascript:;" onclick="togglePortlet(this)">         
					<i class="fa fa-angle-up"></i></a></div>
				</div>
			</div>
			<div class="portlet-body">
				<div class="panel-body height_remain" id="999" style="background-color: #eeebe4;">
					<form action="#" class="form-inline no-btm-form"  role="form" id="queryForm">
						<div style="float: left;">
							<div class="form-group">
								<input type="text" class="form-control" id="magazineNum" name="magazineNum" placeholder="总期数" />
							</div>
						</div>
						<div style="float: right;">
							<button type="button" class="btn btn-primary" style="background-color: #e0d1bc;font-size: 14;font-style:#7a521e;" onclick="queryList();">查询</button>
							<button type="button" class="btn btn-primary" style="background-color: #e0d1bc;font-size: 14;font-style:#7a521e;" onclick="formReset();">清空</button>
						</div>
					</form>
				</div>
			</div>
			<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			<%@ include file="/publishRes/resCheck.jsp" %>
			<%@ include file="/publishRes/publishResFilePage.jsp" %>
			<%@ include file="/beachReplaceMetadata/beachReplace.jsp" %>
		</div>
	</div>
</body>
</html>