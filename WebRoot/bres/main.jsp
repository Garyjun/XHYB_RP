<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>基础资源管理</title>
	<script type="text/javascript" src="classtype.js"></script>
	<script type="text/javascript" src="unitTree.js" data="main"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		var libType = '${param.libType}';
		var queryType = 0;
		$(function(){
			showTarget();
			$('#module').change(function(){
				changeMyInputValues();
				if($('#module').val()=="ST"){
					$('#orHide').hide();
				}else{
					$('#orHide').show();
				}
				$('#queryForm')[0].reset();$('#unitName').val('');$('#unit').val('');
				$('#queryForm1')[0].reset();
				$('#searchText').val('');
				queryForm();
			});
			$('#type').change(function(){
				changeMyInputValues();
				$('#queryForm')[0].reset();$('#unitName').val('');$('#unit').val('');
				$('#queryForm1')[0].reset();
				$('#searchText').val('');
				queryForm();
			});
// 			$('#status').change(function(){
// 				queryForm();
// 			});

			changeMyInputValues();
			var module = $("#module").val();
			if(module == 'ZT'){
				$("#unitShow").text("专题：");
			}else{
				$("#unitShow").text("单元：");
			}
			queryForm();
			$('#bresTab a').click(function (e) {
	            e.preventDefault();
	            queryType = $(this).context.id;
	        });
			//getUnitTree()
		});
		var domainType = -1; // 0 版权树 1分类树 选择学段后值改变
		function changeMyInputValues(){
			educationalPhaseShow();
			
			var educational_phase = getEducational();
			
			if(educational_phase != ''){
				changeEducational();
			}else{
				changeDomainType();
			}
			var module = $("#module").val();
			if(module == 'ZT'){
				$("#unitShow").text("专题：");
			}else{
				$("#unitShow").text("单元：");
			}
		}
		//标签索引
		var statusMap;
			function showTarget(){
				$.ajax({
					url :'${path}/target/getAllTargets.action?libType='+libType,
					type : 'post',
					async : true,
					success : function(data) {
						var publishTypeList = $("#tab_1_1_4");
						var typeTargets = eval('(' + data + ')');
						var j=0;
						for(var i = 0; i< typeTargets.length;i++){
							var label = $("<label class=\"checkbox-inline control-label text-left\"></label>");
							label.appendTo(publishTypeList);
							var checkbox = $("<input type='checkbox' name='typeTarget' value='"+typeTargets[i].id+"'onClick='selectTargetRes();'>"+typeTargets[i].name+"&nbsp;</input>");
							checkbox.appendTo(label);
							j++;
							if(j%2==0){
								var br = $("<br>");
								br.appendTo(publishTypeList);
							}
						}
					}
				});
			}
			function selectTargetRes() {
				//alert(11);
				var ids = document.getElementsByName("typeTarget");
				var resTargetId="";
			    for (var i = 0; i < ids.length; i++){  
				    if (ids[i].checked){  
				    	resTargetId= resTargetId+","+ids[i].value;
				    }
			    }
			    //alert(resTargetId);
			    if(resTargetId.length>0){
			    	//截取逗号
			    	resTargetId=resTargetId.substr(1);
			    }
			   // 点击main页面复选框操作
				document.getElementById("queryForm").action = "${path}/target/gotoBresTargetList.action?resTargetId="+resTargetId+"&libType="+libType+""+"&isTarget=1"+"&module="+$('#module').val()+"&type="+$('#type').val();
				document.getElementById("queryForm").submit();
			}
		function queryForm(){
			var type = $('#type').val();
			var module=$('#module').val();
			var url = 'list.action';
			if(type == 'T06'){
				url = '${path}/bookRes/bookResList.action';
			}
			if(module=='ST'){
				url += '?module='+$('#module').val()+'&type=T14'+'&queryType='+queryType+'&libType='+libType;
			}else{
				url += '?module='+$('#module').val()+'&type='+$('#type').val()+'&queryType='+queryType+'&libType='+libType;
			}
			if(queryType == 0){
				var unit = $('#unit').val();
				var eduPhase = getEducational();
				url += '&eduPhase='+eduPhase+'&version='+$('#version').val()+'&status='+$('#status').val()+'&subject='+$('#subject').val()+'&grade='+$('#grade').val()+'&fascicule='+$('#fascicule').val()+'&unit='+unit;
			}else if(queryType == 1){
				url += '&title='+$('#title').val()+'&batchNum='+$('#batchNum').val()+'&creator='+$('#creator').val()+'&keywords='+$('#keywords').val()+'&description='+$('#description').val()+'&modifiedStartTime='+$('#modifiedStartTime').val()+'&modifiedEndTime='+$('#modifiedEndTime').val();
			}else{
				url += '&searchText='+$('#searchText').val();
			}
			$('#work_main').attr('src',url);
		}
	</script>
</head>
<body class="by-frame-body">
<div class="by-main-page-body-side" id="byMainPageBodySide">
    <div class="page-sidebar">
    <!-- <input type="hidden" id="flag" name="flag"/> -->
        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
            <i class="fa fa-angle-left"></i>
        </div>
		<div id="sideWrap">
			<div class="by-tool-box">
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">资源模块：</label></div>
				    <div class="by-form-val">
				    	<app:constants name="module" repository="com.brainsoon.system.support.SystemConstants" className="ResourceMoudle" inputType="select" selected="1" ignoreKeys="SJ" cssType="form-control"></app:constants>
				    </div>
				</div>
				<div id="orHide">
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">资源类型：</label></div>
				    <div class="by-form-val">
				    	<app:constants name="type" repository="com.brainsoon.system.support.SystemConstants" className="ResourceType" inputType="select" ignoreKeys="T10,T11,T00" headerValue="全部" selected="1" cssType="form-control"></app:constants>
				    </div>
				</div>
				</div>
				<div class="by-tab">
                       <ul class="nav nav-tabs nav-justified" id="bresTab">
                           <li class="active"><a id="0" href="#tab_1_1_1" data-toggle="tab">分类</a></li>
                           <li><a id="1" href="#tab_1_1_2" data-toggle="tab">条件</a></li>
                           <li><a id="2" href="#tab_1_1_3" data-toggle="tab">全文</a></li>
                           <li><a id="3" href="#tab_1_1_4" data-toggle="tab">标签</a></li>
                       </ul>
                       <div class="tab-content">
                           <div class="tab-pane active" id="tab_1_1_1">
							<form id="queryForm" action="" target="work_main" method="post">
				 			 <div class="by-form-row clearfix" id="statusDiv">
                                   <div class="by-form-title"><label>资源状态：</label></div>
                                   <div class="by-form-val">
                                      <app:constants name="status" repository="com.brainsoon.system.support.SystemConstants" className="ResourceStatus" inputType="select" headerValue="全部" cssType="form-control"></app:constants>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix" id="educational_phaseDiv">
                                   <div class="by-form-title"><label>学段：</label></div>
                                   <div class="by-form-val">
                                       <select id="educational_phase" name="educational_phase" class="form-control validate[required]" onchange="changeEducational()">
                                    		<option value="">请选择</option>
                                    	</select>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix filed" id="versionDiv">
                                   <div class="by-form-title"><label for="version">版本：</label></div>
                                   <div class="by-form-val">
                                       <select class="form-control" id="version" name="version" onchange="changeVersion()">
                                           <option value="">请选择</option>
                                       </select>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix" id="subjectDiv">
                                   <div class="by-form-title"><label for="subject">学科：</label></div>
                                   <div class="by-form-val">
                                       <select class="form-control" name="subject" id="subject" onchange="changeSubject()">
                                            <option value="">请选择</option>
                                        </select>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix filed" id="gradeDiv">
                                   <div class="by-form-title"><label for="grade">年级：</label></div>
                                   <div class="by-form-val">
                                       <select class="form-control" name="grade" id="grade" onchange="changeGrade();">
                                            <option value="">请选择</option>
                                        </select>
                                   </div>
                               	</div>
								<div class="by-form-row clearfix filed" id="fasciculeDiv">
                                   <div class="by-form-title"><label for="fascicule">分册：</label></div>
                                   <div class="by-form-val">
                                       <select class="form-control" name="fascicule" id="fascicule" >
                                            <option value="">请选择</option>
                                        </select>
                                   </div>
                               	</div>
								<div class="by-form-row clearfix filed" id="unitDivContent">
									<div class="by-form-title"><label for="fascicule"><span id="unitShow"></span></label></div>
                                   <div class="by-form-val">
                                       <div class="input-group">
                                    		<input type="text" class="form-control" id="unitName" readonly="readonly"/>
                                    		<input type="hidden" name="unit" id="unit"/>
											<span class="input-group-btn">
												<a id="menuBtn" onclick="showUnit();" href="###" class="btn btn-primary" role="button">选择</a>
											</span>
                                   		</div>
                                   </div>
                               	</div>
                               
                               <div class="by-form-row by-form-action">
                                   <a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
                                   <a class="btn btn-default blue" href="###" onclick="$('#queryForm')[0].reset();$('#unitName').val('');$('#unit').val('');queryForm();">清空</a>
                               </div>
                            </form>
                           </div>
                           <div class="tab-pane" id="tab_1_1_2">
                           	<form id="queryForm1" action="" target="work_main" method="post">
                           		<div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="title">批次号：</label></div>
                                   <div class="by-form-val">
                                   	<input class="form-control" id="batchNum" name="batchNum"/>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="title">资源标题：</label></div>
                                   <div class="by-form-val">
                                   	<input class="form-control" id="title" name="title"/>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="creator">制作者：</label></div>
                                   <div class="by-form-val">
                                       <input class="form-control" id="creator" name="creator"/>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="keywords">关键字：</label></div>
                                   <div class="by-form-val">
                                       <input class="form-control" id="keywords" name="keywords"/>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="description">摘要：</label></div>
                                   <div class="by-form-val">
                                       <input class="form-control" id="description" name="description"/>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="modifiedStartTime">开始日期：</label></div>
                                   <div class="by-form-val">
                                       <input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime\')}'})" id="modifiedStartTime" name="modifiedStartTime"/>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="modifiedEndTime">结束日期：</label></div>
                                   <div class="by-form-val">
                                       <input class="form-control Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})" id="modifiedEndTime" name="modifiedEndTime"/>
                                   </div>
                               </div>
                               <div class="by-form-row by-form-action">
                                   <a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
                                   <a class="btn btn-default blue" href="###" onclick="$('#queryForm1')[0].reset();queryForm();">清空</a>
                               </div>
                              </form>
                           </div>
                           <div class="tab-pane" id="tab_1_1_3">
                               <div class="by-form-row clearfix">
                                   <div class="">
                                       <input class="form-control" id="searchText" name="searchText"/>
                                   </div>
                               </div>
                              	<div class="by-form-row by-form-action">
                                   <a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
                                   <a class="btn btn-default blue" href="###" onclick="$('#searchText').val('');queryForm();">清空</a>
                               </div>
                           </div>
                            <div class="tab-pane col-md-offset-1 " id="tab_1_1_4">
                           <%--  <c:forEach items="${resTarget}" var="target" varStatus="targetRow">
								<c:if test="#targetRow.Odd">
								<tr>
								</c:if> 
								<td width="50%"><input type="checkbox" name="resTargetIds" id="resTargetIds"  onClick="queryTarget();"/>
								<td><input type="hidden" id="resTargetName"/></td>
 								<c:if test="#targetRow.Even">
									</tr> 
								</c:if>
							</c:forEach> --%>
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