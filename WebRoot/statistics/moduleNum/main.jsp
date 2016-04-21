<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>资源模块数量分布统计</title>
	<script type="text/javascript" src="${path}/bres/classtype.js"></script>
	<script type="text/javascript" src="${path}/bres/unitTree.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
		var module = '';
		var type = '';
		$(function(){
			module = $('#module').val();
			type = $('#type').val();
			$('#module').change(function(){
				module = $('#module').val();
				changeMyInputValues();
				queryForm();
			});
			$('#type').change(function(){
				type = $('#type').val();
				changeMyInputValues();
				queryForm();
			});
			changeMyInputValues();
			queryForm();
		});
		var domainType = -1; // 0 版权树 1分类树 选择学段后值改变
		
		function changeMyInputValues(){
			var type = $('#type').val();
			var module = $('#module').val();
			if(type == 'T12'){
				$('#educational_phase').empty();
				$('#educational_phaseDiv').hide();
			}else if(module == 'TB' || module == 'ZS'){
				//获取学段
				$.get('${path}/bres/getEducationalPhaseOptions.action?module='+module,'',function(data){
					$('#educational_phase').empty();
					$('#educational_phase').append(data);
				});
				$('#educational_phaseDiv').show();
			}
			var educational_phase = getEducational();
			changeDomainType();
			
			if(educational_phase != ''){
				changeEducational();
			}
		}
		function getEducationalText(){
			var type = $('#type').val();
			return getSelectdText('educational_phase');
		}
		function getSelectdText(id){
			var vl = $('#'+id).find('option:selected').text();
			if(vl != '请选择'){
				return vl;
			}else{
				return '';
			}
		}
		function queryForm(){
			var type = $('#type').val();
			var url = 'list.jsp?module='+$('#module').val()+'&type='+type+'&libType='+$('#libType').val()+'&tmGrade='+$('#grade').val()+'&educationalPhase='+getEducationalText()+'&tmSubject='+getSelectdText('subject');
			var version = getSelectdText('version:visible');
			if(typeof(version) != 'undefined' && version != ''){
				url += '&tmVersion='+version;
			}
			url += '&filingDate_st='+$('#filingDate_st').val() + '&filingDate_et='+$('#filingDate_et').val() + '&queryType='+queryType;
			$('#work_main').attr('src',url);
		}
		var queryType = 1;
		function changeQueryType(){
			queryType = $('input[name="queryType"]:checked').val();
			$('#filingDate_st').val('');
			$('#filingDate_et').val('');
		}
		function startTime(){
			if(queryType == 1){
				return WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'filingDate_et\')}'});
			}else if(queryType == 2){
				return WdatePicker({dateFmt:'yyyy-MM',maxDate:'#F{$dp.$D(\'filingDate_et\')}'});
			}else if(queryType == 3){
				return WdatePicker({dateFmt:'yyyy',maxDate:'#F{$dp.$D(\'filingDate_et\')}'});
			}
		}
		function endTime(){
			if(queryType == 1){
				return WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'filingDate_st\')}'});
			}else if(queryType == 2){
				return WdatePicker({dateFmt:'yyyy-MM',minDate:'#F{$dp.$D(\'filingDate_st\')}'});
			}else if(queryType == 3){
				return WdatePicker({dateFmt:'yyyy',minDate:'#F{$dp.$D(\'filingDate_st\')}'});
			}
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
							    <div class="by-form-row clearfix">
								    <div class="by-form-title"><label for="abc">资源模块：</label></div>
								    <div class="by-form-val">
								    	<app:constants name="module" repository="com.brainsoon.system.support.SystemConstants" className="ResourceMoudle" inputType="select" selected="1" cssType="form-control"></app:constants>
								    </div>
								</div>
								<div class="by-form-row clearfix">
								    <div class="by-form-title"><label for="abc">资源类型：</label></div>
								    <div class="by-form-val">
								    	 <form:select path="resTypeMap" id="type" class="form-control" cssStyle="width: 120px">
											<form:options items="${resTypeMap}"/>
											<form:options items="${resCaTypeMap}"/>
						                 </form:select>
								    </div>
								</div>
								<div class="by-form-row clearfix">
								    <div class="by-form-title"><label for="abc">成熟程度：</label></div>
								    <div class="by-form-val">
								    	<app:constants id="libType" name="libType" repository="com.brainsoon.system.support.SystemConstants" className="LibType" inputType="select" cssType="form-control"></app:constants>
								    </div>
								</div>
                               <div class="by-form-row clearfix">
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
                               
                               
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="subject">查询维度：</label></div>
                                   <div class="by-form-val">
                                    	<label class="radio-inline" style="padding-left:0px;">
                                    	<input checked="checked" name="queryType" type="radio" value="1" valuedesc="1" style="float: inherit; margin-left: 0px;" onchange="changeQueryType()"/>
                                    	日 </label>
                                    	<label class="radio-inline" style="padding-left:0px;margin-left: 0px;">
                                    	<input name="queryType" type="radio" value="2" valuedesc="2"  style="float: inherit; margin-left: 0px;" onchange="changeQueryType()"/>
                                    	月 </label>
                                    	<label class="radio-inline" style="padding-left:0px;margin-left: 0px;">
                                    	<input name="queryType" type="radio" value="3" valuedesc="3" style="float: inherit; margin-left: 0px;" onchange="changeQueryType()"/>
                                    	年 </label>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="filingDateStartTime">开始时间：</label></div>
                                   <div class="by-form-val">
                                       <input class="form-control Wdate" onfocus="startTime()" id="filingDate_st" name="filingDate_st"/>
                                   </div>
                               </div>
                               <div class="by-form-row clearfix">
                                   <div class="by-form-title"><label for="filingDateEndTime">结束时间：</label></div>
                                   <div class="by-form-val">
                                       <input class="form-control Wdate" onfocus="endTime()" id="filingDate_et" name="filingDate_et"/>
                                   </div>
                               </div>
                               <div class="by-form-row by-form-action">
                                   <a class="btn btn-default red" href="###" onclick="queryForm()">查询</a>
                                   <a class="btn btn-default blue" href="###" onclick="$('#queryForm')[0].reset();changeQueryType();queryForm();">清空</a>
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