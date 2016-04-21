<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>基础资源管理</title>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
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
			$('#module').change(function(){
				changeMyInputValues();
				queryForm();
			});
			$('#type').change(function(){
				changeMyInputValues();
				queryForm();
			});
			$('#status').change(function(){
				queryForm();
			});
			changeMyInputValues();
			queryForm();
			$('#bresTab a').click(function (e) {
	            e.preventDefault();
	            queryType = $(this).context.id;
	        });
			if(module == 'ZT'){
				$("#unitShow").text("专题：");
			}else{
				$("#unitShow").text("单元：");
			}
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
			if(module == 'ZT'){
				$("#unitShow").text("专题：");
			}else{
				$("#unitShow").text("单元：");
			}
		}
		function setModuleAndType(){
			try{top.index_frame.work_main.setModuleAndType($('#module').val(),$('#type').val());}catch(e){}
		}
		
		function queryForm(){
			
			var url = 'bookResList.action?module='+$('#module').val()+'&type='+$('#type').val()+'&status='+$('#status').val()+'&queryType='+queryType;
			if(queryType == 0){
				var eduPhase = getEducational();
				url += '&eduPhase='+eduPhase+'&version='+$('#version').val()+'&subject='+$('#subject').val()+'&grade='+$('#grade').val()+'&fascicule='+$('#fascicule').val();
			}else if(queryType == 1){
				url += '&title='+$('#title').val()+'&batchNum='+$('#batchNum').val()+'&creator='+$('#creator').val()+'&keywords='+$('#keywords').val()+'&description='+$('#description').val()+'&modifiedStartTime='+$('#modifiedStartTime').val()+'&modifiedEndTime='+$('#modifiedEndTime').val();
			}else{
				url += '&searchText='+$('#searchText').val();
			}
			$('#work_main').attr('src',url);
		}
		
		function getModuleAndType(){
			return $('#module').val()+','+$('#type').val();
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
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">资源模块：</label></div>
				    <div class="by-form-val">
				    	<app:constants name="module" repository="com.brainsoon.system.support.SystemConstants" className="ResourceMoudle" inputType="select" selected="1" cssType="form-control"></app:constants>
				    </div>
				</div>
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">资源类型：</label></div>
				    <div class="by-form-val">
				    	<app:constants name="type" repository="com.brainsoon.system.support.SystemConstants" className="ResourceCaType" inputType="select" selected="1" cssType="form-control"></app:constants>
				    </div>
				</div>
				<div class="by-form-row clearfix">
				    <div class="by-form-title"><label for="abc">资源状态：</label></div>
				    <div class="by-form-val">
				    	<app:constants name="status" repository="com.brainsoon.system.support.SystemConstants" className="ResourceStatus" inputType="select" selected="0" cssType="form-control"></app:constants>
				    </div>
				</div>
				<div class="by-tab">
                       <ul class="nav nav-tabs nav-justified" id="bresTab">
                           <li class="active"><a id="0" href="#tab_1_1_1" data-toggle="tab">分类检索</a></li>
                           <li><a id="1" href="#tab_1_1_2" data-toggle="tab">条件检索</a></li>
                           <li><a id="2" href="#tab_1_1_3" data-toggle="tab">全文检索</a></li>
                       </ul>
                       <div class="tab-content">
                           <div class="tab-pane active" id="tab_1_1_1">
							<form id="queryForm" action="" target="work_main" method="post">
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
												<a id="menuBtn" onclick="showUnit(); return false;" href="###" class="btn btn-primary" role="button">选择</a>
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