<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>批量列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
		$(function(){
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		//list方法url 路径：/BSFW/Resource/com/brainsoon/resource/action/BatchImportPublishAction.java 
	   		var _url = 'list.action';
	   		var _pk = 'id';
	   		var _conditions = ['libType'];
	   		var _sortName = 'id';
	   		var _sortOrder = 'desc';
			var _colums = [{field:'name',title:'名称',width:fillsize(0.2),align:'center',sortable:true},
// 							{field:'batchNum',title:'批次号',width:fillsize(0.2),sortable:true},
							{field:"okNum",title:"成功/失败/总记录",width:fillsize(0.15),align:'center',formatter:$result,sortable:false},
							{field:'remark',title:'备注',width:fillsize(0.1),align:'center',sortable:true},
							{field:'createTime',title:'上传时间',width:fillsize(0.15),align:'center',sortable:true},
							{field:'finishTime',title:'完成时间',width:fillsize(0.15),align:'center',sortable:true},
							{field:'statusDesc',title:'状态',width:fillsize(0.12),align:'center',sortable:true},
							{field:'opt1',title:'操作',width:fillsize(0.17),align:'center',formatter:$operate}];
	   		
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<a class=\"btn hover-red\" href=\"javascript:lookDetail('"+rec.id+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
			/* if(rec.excelPath  != undefined && rec.excelPath  != ''){
// 				'"+rec.failExcelPath+"',
				opt += "<a class=\"btn hover-red\" href=\"javascript:downFailLog('"+rec.id+"','"+rec.failExcelPath+"','"+rec.excelPath+"','"+rec.allNum+"')\" ><i class=\"fa fa-download\"></i> 下载</a>";
			} */
			opt += "&nbsp;&nbsp;&nbsp;&nbsp;<a class=\"btn hover-blue\" href=\"javascript:deleteRecord('"+rec.id+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a>";
			return opt;
					
		};
		$result = function(value,rec) {
			var okNum = rec.succNum==null?0:rec.succNum;
			var failNum = rec.failNum==null?0:rec.failNum;
			var allNum = rec.allNum==null?0:rec.allNum;
			return "<font color=\"green\">" + okNum + "</font>/<font color=\"red\">" + failNum + "</font>/" + allNum;
		}     
		//查看
		function lookDetail(id){
			//excelPath = encodeURI(encodeURI(excelPath));+"&excelPath="+excelPath
			window.location.href = "${path}/publishRes/import/detail.action?id="+id+"&publishType="+$('#libType').val();
		}
		function downFailLog(id,failExcelPath,excelPath,allNum){
// 			alert(failExcelPath);
// 			alert(excelPath);
			failExcelPath = encodeURI(encodeURI(failExcelPath));
			excelPath = encodeURI(encodeURI(excelPath));
			window.location.href = "${path}/publishRes/downloadAbsFile.action?id="+id+"&failExcelPath="+failExcelPath+"&filePath="+excelPath+"&allNum="+allNum;
		}
		/*EXCEL批量导入*/
		function importResEXCEL(){
			parent.parent.layer.open({
			    type: 2,
			    title: 'EXCEL导入',
			    closeBtn: true,
			    shadeClose: true,
			    maxmin: true, //开启最大化最小化按钮
			    area: ['600px', '400px'],
			    shift: 2,
			    content: '${path}/publishRes/import/publishImport.jsp?publishType='+$('#libType').val()  //iframe的url，no代表不显示滚动条
			    
			});
			//$.openWindow("${path}/publishRes/import/publishImport.jsp?publishType="+$('#libType').val(),'EXCEL导入',600,400);
		}
		/*XML批量导入*/
		function importResXML(){
			parent.parent.layer.open({
			    type: 2,
			    title: 'XML导入',
			    closeBtn: true,
			    shadeClose: true,
			    maxmin: true, //开启最大化最小化按钮
			    area: ['600px', '570px'],
			    shift: 2,
			    content: '${path}/publishRes/import/publishImportXML.jsp?publishType='+$('#libType').val()  //iframe的url，no代表不显示滚动条
			    
			});
			//$.openWindow("${path}/publishRes/import/publishImportXML.jsp?publishType="+$('#libType').val(),'XML导入',600,570);
		}
		/***删除***/
		function del() {
			var codes = getChecked('data_div','id').join(',');
			if (codes == '') {
			    $.alert('请选择要删除的数据！');
			} else {
				deleteRecord(codes);
			};
		}
		function deleteRecord(ids){
			$.confirm('确定要删除所选数据吗？', function(){
				$.ajax({
					url:'delTaskInfo.action?ids=' + ids,
				    type: 'post',
				    datatype: 'text',
				    success: function (returnValue) {
				    	query();
				    }
				});
			});
		}
		function query(){
			$grid.query();
		}
		var timerId;
		// 自动刷新
		function doAutoCheck(){
			var seconds = $('#autoFrequence').val();
			var checkValue =document.getElementsByName('autoRefresh')[0];  ;
			if(timerId != '') {
				clearInterval(timerId);
			}
			if(checkValue.checked) {
				if(check_num(seconds)){
					if( seconds >= 5) {
						timerId = setInterval("query()",seconds * 1000);
					} else {
						$.alert("请输入大于等于5秒的时间间隔！");
						checkValue.checked = false;
						return;
					}
				}else{
					$.alert("请输入大于等于5秒的时间间隔！");
					$('#autoFrequence').val('');
					checkValue.checked = false;
					return;
				}
			}
		}
		function check_num(value){    
			//定义正则表达式部分    
			var reg = /^\d+$/;    
			if( value.constructor === String ){   
				var re = value.match( reg );   
				if(re == null){
					return false;
				}
				return true;    
			}    
			return false;
		}
		function lookExcel(){
			$.ajax({
				type:"post",
				async:false,
				url:"${path}/publishRes/import/lookQueueExcel.action",
				success:function(data){
					if(data!=''){
						var msg = '<table border="0" cellpadding="0" cellspacing="0" >';
						var excelNames = data.split(",");
						for(var excelName in excelNames){
							msg+= '<tr style="white-space:nowrap;"><td>'+excelNames[excelName]+'</td></tr>';
						}
						msg += '</table>';
						$.simpleDialog({
							title:'存在未处理的EXCEL', 
							content:'<font color=\"red\"> 未处理的EXCEL：</font>'+msg, 
							button:[
							        {name:'确定',callback:function(){}
							        }
							]
						})
					}else{
						$.alert("不存在未处理的Excel");
					}
				}
			});
		}
		function mergeExcel(){
			$.openWindow("${path}/publishRes/import/mergeImport.jsp?publishType=${param.publishType}",'合并Excel',500,400);
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">资源管理</a>
					</li>
					<li class="active">资源视图</li>
					<li class="active">批量导入日志列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" >
				<div style="margin: 0px 10px 10px 0px;">
					<input type="hidden" id="libType" name="libType" value="${param.publishType }"/>
					<div class="form-inline">
						<div class="dropdown">
							<button type="button" class="btn btn-primary" id="dropdownMenu1" data-toggle="dropdown">资源导入<span class="caret"></span></button>
							<input type="button" value="批量删除" class="btn btn-primary" onclick="del()"/>
							<sec:authorize url='/publishRes/beachReplace.action'>
								<input type="button" value="合并Excel" class="btn btn-primary" onclick="mergeExcel()"/>
							</sec:authorize>
			      			时间间隔：<input type="text" name="autoFrequence" id="autoFrequence" onblur="doAutoCheck();" size="4" class="validate[custom[integer]]"/>秒
			        		<input onclick="doAutoCheck();" type="checkbox" name="autoRefresh" id="autoRefresh" />自动刷新
					   		<ul class="dropdown-menu text-left" role="menu" aria-labelledby="dropdownMenu1">
					      		<li role="presentation">
					         		<a role="menuitem" tabindex="-1"  onclick="importResXML();return false;" href="javascript:void(0);">XML导入</a>
					      		</li>
					      		<li role="presentation" class="divider"></li>
					   	  		<li role="presentation">
					         		<a role="menuitem" tabindex="-1" onclick="importResEXCEL();return false;" href="javascript:void(0);">EXCEL导入</a>
					      		</li>
<!-- 					      		<li role="presentation" class="divider"></li>
					     		<li role="presentation">
					         		<a role="menuitem" tabindex="-1"  onclick="importRes();return false;" href="javascript:void(0);">文件导入</a>
					      		</li>
 -->					   		</ul>
							<!-- <input type="button" value="查看未处理EXCEL" class="btn btn-primary red" onclick="lookExcel()"/> -->
							<input type="button" value="刷新" class="btn btn-primary red" onclick="query();"/>
					   		<input type="button" value="返回" class="btn btn-primary red" onclick="parent.queryForm()"/>
						</div>
					
<!-- 					<input type="button" value="EXCEL导入" class="btn btn-primary" onclick="importResEXCEL()"/> -->
<!-- 					<input type="button" value="XML导入" class="btn btn-primary" onclick="importResXML()"/> -->
<!-- 					<input type="button" value="文件导入" class="btn btn-primary" onclick="importRes()"/> -->
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
</body>
</html>