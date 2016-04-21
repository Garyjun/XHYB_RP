<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>基础资源管理</title>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		var libType = '${target.libType}';
		var queryType = 0;
		$(function(){
			//var title = "${APP_USER_SESSION_KEY.platformId}";
			$('#module').change(function(){
				queryForm();
			});
			$('#status').change(function(){
				queryForm();
			});
			queryForm();
			typeChange();
			targetType();
			$('#targetType').change(function(){
				queryForm();
			});
		});
		function targetType(){
			$.ajax({
				type : "post",
				url : "${path}/publishRes/targetType.action",
				success : function(data) {
					var json = JSON.parse(data);
					var fieldName = "";
					var content = "";
					content ="<option value = ''>全部</option>";
					for(var i in json){
			 			var info = json[i];
				 		content +="<option value = '"+i+"'>"+info+"</option>";
			 		}
					content+="<option value = '通用标签'>通用标签</option>";
					$("#targetType").append(content);
				}
			});
		}
		function queryForm(){
			var targetType ="";
			if($('#targetType').val()!=""){
				targetType = encodeURI(encodeURI($('#targetType').val()));
			}
			var title = "${APP_USER_SESSION_KEY.platformId}";
			var targetName = $('#targetName').val();
			targetName = encodeURI(encodeURI(targetName));
			var	module = $('#module').val();
			var url ='targetList.jsp?status='+$('#status').val()+'&module='+module+'&targetName='+targetName+"&typeTarget="+$('#typeTarget').val()+"&targetType="+targetType;
			$('#work_main').attr('src',url);
		}
		
		function typeChange() {
			var title = "${APP_USER_SESSION_KEY.platformId}";
			if(title==1)
			{
				$('#div2').hide();
			}else
			{
				$('#div1').hide();
			}
		}
	
	</script>
</head>
<body class="by-frame-body">
<input type="hidden" id="typeTarget" name="typeTarget" value="${param.pMenuId}"/>
<input type="hidden" id="status" name="status" value="1"/>
<div class="by-main-page-body-side" id="byMainPageBodySide">
    <div class="page-sidebar">
        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
            <i class="fa fa-angle-left"></i>
        </div>
		<div id="sideWrap">
			<div class="by-tool-box">
                      	<form id="queryForm1" action="" target="work_main" method="post">
                         
                           <div class="by-form-row clearfix">
                              <div class="by-form-title"><label for="title">标签名称：</label></div>
                              <div class="by-form-val">
                              	<input class="form-control" id="targetName" name="targetName"/>
                              </div>
                          </div>
                <div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">资源类型：</label></div>
				    <div class="by-form-val">
				        <app:selectResType name="module" id="module" selectedVal="${param.publishType}"  headName="全部"  headValue="" readonly=""/>
				    </div>
				</div>
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">标签分类：</label></div>
				    <div class="by-form-val">
				        <select  id="targetType" name="targetType" class="form-control validate[required] text-input"></select>
				    </div>
				</div>
<!-- 				<div class="by-form-row clearfix"> -->
<!-- 				    <div class="by-form-title"><label for="abc">标签状态：</label></div> -->
<!-- 				    <div class="by-form-val"> -->
<%-- 				    <app:constants name="status" repository="com.brainsoon.system.support.SystemConstants" className="targetStatus" inputType="select" cssType="form-control"></app:constants> --%>
<!-- 				    </div> -->
<!-- 				</div> -->
                <div class="by-form-row by-form-action">
                    <a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
                    <a class="btn btn-default blue" href="###" onclick="$('#queryForm1')[0].reset();queryForm();">清空</a>
                </div>
               </form>
                     
           	</div>
		</div>
   	</div>
</div>
<div class="by-main-page-body-center" id="byMainPageBodyCenter">
    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src=""></iframe>
</div>
</body>
</html>