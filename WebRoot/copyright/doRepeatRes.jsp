<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript">
	$(function(){
   		var _divId = 'data_div';
   		var _url = '${path}/copyright/getRepeatRes.action?source=${copyrightRepeat.source}&type=${copyrightRepeat.type}&title=${copyrightRepeat.title}&creator=${copyrightRepeat.creator}';
   		var _pk = 'objectId';
   		var _conditions = ['module','type','status','eduPhase','version','subject','grade','fascicule','title','creator','description','keywords','modifiedStartTime','modifiedEndTime','searchText','queryType','libType'];
   		var _sortName = 'modified_time';
   		var _sortOrder = 'desc';
   		var _showCheck =false;
   		var _colums = [
						{field:'commonMetaData.commonMetaDatas.title',title:'资源标题',width:fillsize(0.17),sortable:true},
						{field:'commonMetaData.commonMetaDatas.keywords',title:'关键字',width:fillsize(0.1),sortable:true},
						{field:'commonMetaData.commonMetaDatas.description',title:'摘要',width:fillsize(0.4),sortable:true},
						{field:'commonMetaData.commonMetaDatas.modified_time',title:'更新时间',width:fillsize(0.18),sortable:true,formatter:$modifiedTime},
						{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate}];
		accountHeight();
   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_showCheck);
	});
	/***操作列***/
	$modifiedTime = function(value,rec){
		value = value.substring(0,11);
		return value;
	};
	$operate = function(value,rec){
		var opt = "";
		opt += "<a class=\"btn hover-red\" href=\"javascript:doRelate('"+rec.objectId+"')\" ><i class=\"fa fa-edit\"></i> 关联</a>";
		opt += "<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
		return opt;
	};
	function doRelate(objectId){
		var id = ${param.id};
		initWaiting = $.waitPanel('正在关联版权请稍后...',false);
		$.post('${path}/copyright/doRelateCopyright.action',{id:id,objectId:objectId},function(data){
			data = eval('('+data+')');
			initWaiting.close();
			if(data.state == 0){
				top.index_frame.work_main.freshDataTable('data_div');
				$.closeFromInner();
			}else{
				alert("关联失败，请联系系统管理员");
				$.closeFromInner();
			}
		});
	//	window.location.href = '${path}/copyright/doRelateCopyright.action?id='+${param.id}+'&objectId='+objectId;
	}
	//状态信息
	$statusDesc = function(value, rec) {
	};
	
	</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
	<div class="portlet">
	<input type="hidden" id="crtType" name="crtType" value="${param.crtType }" />
	                    <div class="portlet-title">
	                        <div class="caption">版权信息 <a href="javascript:;" onclick="togglePortlet(this)"><i
	                                class="fa fa-angle-up"></i></a></div>
	                    </div>
	                    <div class="portlet-body">
	                        <div class="container-fluid">
	                                            <span style="float:left;width:12%;"><label >版权类型：</label></span>
	                                            <span style="float:left;width:38%;">${copyrightRepeat.crtTypeDesc}&nbsp;</span>
	                                            <span style="float:left;width:12%;"><label >授权地区：</label></span>
	                                        	<span style="float:left;width:38%;">${copyrightRepeat.authAreaDesc}&nbsp;</span>
	                        </div>
	                        <div class="container-fluid">
	                                            <span style="float:left;width:12%;"><label >授权渠道：</label></span>
	                                            <span style="float:left;width:38%;">${copyrightRepeat.authChannelDesc}&nbsp;</span>
	                                            <span style="float:left;width:12%;"><label >授权语言：</label></span>
	                                        	<span style="float:left;width:38%;">${copyrightRepeat.authLanguageDesc}&nbsp;</span>
	                        </div>
	                        <div class="container-fluid">
	                                            <span style="float:left;width:16%;"><label >许可权利种类：</label></span>
	                                            <span style="float:left;width:34%;">${copyrightRepeat.permitRightDesc}&nbsp;</span>
	                                            <span style="float:left;width:12%;"><label >合作模式：</label></span>
	                                        	<span style="float:left;width:38%;">${copyrightRepeat.collaPatternDesc}&nbsp;</span>
	                        </div>
	                        <div class="container-fluid">
	                                            <span style="float:left;width:12%;"><label >授权时限：</label></span>
	                                            <span style="float:left;width:38%;">${copyrightRepeat.authTimeLimit}&nbsp;</span>
	                                            <span style="float:left;width:12%;"><label >授权时间：</label></span>
	                                        	<span style="float:left;width:38%;">${copyrightRepeat.authStartDate}&nbsp;-&nbsp;${copyrightRepeat.authEndDate}</span>
	                        </div>
	                        <div class="container-fluid">
	                                            <span style="float:left;width:12%;"><label >版权人：</label></span>
	                                            <span style="float:left;width:38%;">${copyrightRepeat.crtPerson}&nbsp;</span>
	                                            <span style="float:left;width:14%;"><label >版权授权人：</label></span>
	                                        	<span style="float:left;width:36%;">${copyrightRepeat.authorizer}&nbsp;</span>
	                        </div>
	                        <div class="container-fluid">
	                                            <span style="float:left;width:12%;"><label >合同编号：</label></span>
	                                            <span style="float:left;width:38%;">${copyrightRepeat.contractCode}&nbsp;</span>
	                        </div>
	                    </div>
	                </div>
		
			<div class="panel-body height_remain" id="999">
				<div id="data_div" class="data_div height_remain" style="width: 750px;"></div>
			</div>
	</div>
</body>
</html>