<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>资源发布数量统计</title>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>  
	<script type="text/javascript">
		$(function(){
			/* querySourceType(); */
			queryForm();
		});
		
		function queryForm(){
			var channelName = $('#channelName').val();
			if(channelName!=null&&channelName!=undefined){
				channelName = encodeURI(encodeURI(encodeURI(channelName)));
			}
			var url = 'list.jsp?'
					/* +'fileType='+$('#fileType').val() */
					+'&posttype='+$('#posttype').val()
					+'&channelName='+channelName
					+'&filingDate_StartTime='+$('#filingDate_StartTime').val() 
					+'&filingDate_EndTime='+$('#filingDate_EndTime').val()
					;	
			$('#work_main').attr('src',url);
		}
		
		function querySourceType() {
			$.ajax({
						type : "post",
						url : "${path}/statistics/releaseNum//querySourceType.action",
						success : function(data) {
							var json = JSON.parse(data);
							for (var i = 0; i < json.length; i++) {
								var jsonObj = json[i];
								var type = jsonObj.type;
								var option = $("<option value='"+type+"'>"+ type + "</option>");
								option.appendTo($("#fileType"));
							}
						}
					});
		}
	</script>
</head>
<body class="by-frame-body">
<div class="by-main-page-body-side" id="byMainPageBodySide">
    <div class="page-sidebar">
        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
            <i class="fa fa-angle-left"></i>
        </div>
		<div id="sideWrap">
			<div class="by-tool-box">
				<div class="by-tab">
                       <div class="tab-content">
                           <div class="tab-pane active" id="tab_1_1_1">
							<form id="queryForm" action="" target="work_main" method="post">
								<%-- <div class="by-form-row clearfix">
								    <div class="by-form-title"><label for="abc">资源分类：</label></div>
								    <div class="by-form-val">
								        <app:selectResType name="publishType" id="publishType"  headName="请选择"  headValue=""/>
								    </div>
								</div> --%>
								<div class="by-form-row clearfix">
								    <div class="by-form-title"><label for="abc">发布途径：</label></div>
								    <div class="by-form-val">
								        <app:select name="posttype" id="posttype" indexTag="XqdZtk"></app:select>
								    </div>
								</div>
							   <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label>客户名称：</label></div>
                                   <div class="by-form-val">
                                   		<input class="form-control" id="channelName" name="channelName"/>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="filingDateStartTime">开始时间：</label></div>
                                   <div class="by-form-val">
                                       <input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'filingDate_EndTime\')}'})" id="filingDate_StartTime" name="filingDate_StartTime"/>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="filingDateEndTime">结束时间：</label></div>
                                   <div class="by-form-val">
                                       <input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'filingDate_StartTime\')}'})" id="filingDate_EndTime" name="filingDate_EndTime"/>
                                   </div>
                               </div>
                               <div class="by-form-row by-form-action">
                                   <a class="btn btn-default red" href="###" onclick="queryForm();">查询</a>
                                   <a class="btn btn-default blue" href="###" onclick="$('#queryForm')[0].reset();queryForm();">清空</a>
                               </div>
                            </form>
                           </div>
                       </div> 
                   </div>
           	</div>
		</div>
   	</div>
</div>
<div class="by-main-page-body-center" id="byMainPageBodyCenter">
    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src=""></iframe>
</div>
</body>
</html>