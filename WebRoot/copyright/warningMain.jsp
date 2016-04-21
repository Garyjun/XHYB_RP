<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>版权预警管理</title>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		$(function(){
			$('#publishType').change(function(){
				queryForm();
			});
			$('#type').change(function(){
				queryForm();
			});
			queryForm();
		});
		
		function queryForm(){
			var url = "${path}/copyright/warningList.jsp?publishType="+$('#publishType').val()+"&main=1";
			url += '&licenStartTime='+$('#licenStartTime').val();
			url += '&licenEndTime='+$('#licenEndTime').val()+'&contractCode='+encodeURI(encodeURI($('#contractCode').val()))+'&ownership='+encodeURI(encodeURI($('#ownership').val()))+'&autherName='+encodeURI(encodeURI($('#autherName').val()))+"&copyrightWaring=2";
			$('#work_main').attr('src',url);
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
				<form id="queryForm1" action="" target="work_main" method="post">
					<div class="by-form-row clearfix">
					    <div class="by-form-title"><label for="abc">资源类型：</label></div>
					    <div class="by-form-val">
					        <app:selectResType name="publishType" id="publishType" selectedVal="${param.publishType}"  headName="全部"  headValue=""  readonly =""/>
					    </div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="abc">合同编号：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control" id="contractCode" name="contractCode"/>
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="abc">版权人：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control" id="ownership" name="ownership"/>
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="abc">授权人：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control" id="autherName" name="autherName"/>
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="licenStartTime">开始时间：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control Wdate"
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'licenEndTime\')}'})"
								id="licenStartTime" name="licenStartTime" />
						</div>
					</div>
					<div class="by-form-row clearfix">
						<div class="by-form-title">
							<label for="licenEndTime">结束时间：</label>
						</div>
						<div class="by-form-val">
							<input class="form-control Wdate"
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'licenStartTime\')}'})"
								id="licenEndTime" name="licenEndTime" />
						</div>
					</div>
<!--                       	 <div class="by-form-row clearfix"> -->
<!--                                    <div class="by-form-title"><label for="modifiedStartTime">开始时间：</label></div> -->
<!--                           </div> -->
<!--                            <div class="by-form-val"> -->
<%--                            <app:datetime property="licenStartTime" id="licenStartTime" onFocus="WdatePicker({maxDate:'#F{$dp.$D(\\'licenStartTime\\')}'})" style="width: 40%;display:inline;"  cssClass="form-control Wdate" />&nbsp;-&nbsp; --%>
<%--  							<app:datetime property="licenStartTimeEnd" id="licenStartTimeEnd" onFocus="WdatePicker({minDate:'#F{$dp.$D(\\'licenStartTimeBegin\\')}'})" style="width: 40%;display:inline;" cssClass="form-control Wdate"/> --%> 
<!--                           </div> -->
<!--                                <div class="by-form-row clearfix"> -->
<!--                                    <div class="by-form-title"><label for="modifiedEndTime">结束时间：</label></div> -->
<!--                                </div> -->
<!--                                  <div class="by-form-val"> -->
<%-- 		                          <app:datetime property="licenEndTime" id="licenEndTime" onFocus="WdatePicker({maxDate:'#F{$dp.$D(\\'licenEndTime\\')}'})" style="width: 40%;display:inline;"  cssClass="form-control Wdate" />&nbsp;-&nbsp; --%>
<%-- 		 					<app:datetime property="licenEndTimeEnd" id="licenEndTimeEnd" onFocus="WdatePicker({minDate:'#F{$dp.$D(\\'licenEndTimeBegin\\')}'})" style="width: 40%;display:inline;" cssClass="form-control Wdate"/> --%>
<!-- 		                          </div> -->
                               <div class="by-form-row by-form-action">
                                   <a class="btn btn-default red" href="###" onclick="queryForm();">查询</a>
                                   <a class="btn btn-default blue" href="###" onclick="$('#queryForm1')[0].reset();queryForm();">清空</a>
                               </div>
                      </form>
           	</div>
		</div>
   	</div>
</div>
<div class="by-main-page-body-center" id="byMainPageBodyCenter">
    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="${pageContext.request.contextPath}/copyright/warningList.jsp"></iframe>
</div>
</body>
</html>