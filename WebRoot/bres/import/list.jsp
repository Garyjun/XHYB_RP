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
	   		var _url = 'list.action';
	   		var _pk = 'id';
	   		var _conditions = ['libType','type','module',''];
	   		var _sortName = 'id';
	   		var _sortOrder = 'desc';
			var _colums = [{field:'excelName',title:'Excel名称',width:fillsize(0.15),sortable:true},
							{field:'batchNum',title:'批次号',width:fillsize(0.1),sortable:true},
							{field:"okNum",title:"成功/失败/总记录",width:fillsize(0.12),formatter:$result,sortable:false},
							{field:'statusDesc',title:'状态',width:fillsize(0.1),sortable:true},
							{field:'createTime',title:'上传时间',width:fillsize(0.15),sortable:true},
							{field:'finishTime',title:'完成时间',width:fillsize(0.15),sortable:true},
							{field:'opt1',title:'操作',width:fillsize(0.33),align:'center',formatter:$operate}];
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<a class=\"btn hover-red\" href=\"javascript:lookDetail('"+rec.id+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
			if(rec.failExcelPath  != undefined && rec.failExcelPath  != ''){
				opt += "<a class=\"btn hover-red\" href=\"javascript:downFailLog('"+rec.failExcelPath+"')\" ><i class=\"fa fa-download\"></i> 失败日志</a>";
			}
			if( rec.fileNotExistPath  != undefined && rec.fileNotExistPath  != '' ){
				opt += "<a class=\"btn hover-red\" href=\"javascript:downFailLog('"+rec.fileNotExistPath+"')\" ><i class=\"fa fa-download\"></i> 文件不存在日志</a>";
			}
// 			opt += "&nbsp;&nbsp;&nbsp;&nbsp;<a class=\"btn hover-blue\" href=\"javascript:deleteRecord('"+rec.id+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a>";
			return opt;
					
		};
		$result = function(value,rec) {
			var okNum = rec.succNum==null?0:rec.succNum;
			var failNum = rec.failNum==null?0:rec.failNum;
			var allNum = rec.allNum==null?0:rec.allNum;
			return "<font color=\"green\">" + okNum + "</font>/<font color=\"red\">" + failNum + "</font>/" + allNum;
		}  
		function downFailLog(path){
			window.location.href = "${path}/bres/downloadAbsFile.action?filePath="+path;
		}
		//查看doExcelData
		function lookDetail(id){
			$.openWindow("${path}/bres/import/detail.action?id="+id,'查看详细',800,550);
		}
		function doExcelData(){
			window.location.href = "${path}/bres/import/doExcelData.action";
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
			var checkValue =document.getElementsByName('autoRefresh')[0];
			if(timerId != '') {
				clearInterval(timerId);
			}
			if(checkValue.checked) {
				//首先判断是不是数字
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
				url:"${path}/bres/import/lookQueueExcel.action",
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
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
	<input type="hidden" id="module" name="module" value="${param.module }" />
<%-- 					<input type="hidden" id="type" name="type" value="${param.type }" /> --%>
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">资源管理</a>
					</li>
					<li class="active">原始资源</li>
					<li class="active">批量导入日志列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" >
				<div style="margin: 0px 10px 10px 0px;">
					<input type="hidden" id="libType" name="libType" value="${param.libType }"/>
<!-- 					<input type="button" value="批量删除" class="btn btn-primary" onclick="del()"/> -->
<!-- 					<input type="button" value="处理日志" class="btn btn-primary" onclick="doExcelData()"/> -->
					<input type="button" value="返回" class="btn btn-primary red" onclick="parent.queryForm()"/>
					<input type="button" value="查看未处理EXCEL" class="btn btn-primary red" onclick="lookExcel()"/>
					<input type="button" value="刷新" class="btn btn-primary red" onclick="query();"/>
		      	时间间隔：<input type="text" name="autoFrequence" id="autoFrequence" onblur="doAutoCheck();" size="4" class="validate[custom[integer]]"/>秒
		        	<input onclick="doAutoCheck();" type="checkbox" name="autoRefresh" id="autoRefresh" />自动刷新
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
</body>
</html>