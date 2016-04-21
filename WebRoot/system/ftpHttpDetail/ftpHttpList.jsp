<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>


<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<style type="text/css">
/* 	#show1 { */
/* 	 position: absolute; */
/* 	  z-index:99999;  */
/* 	  top: 100px; background-color: #F39921; */
/* 	  left: 300px; */
/* 	  width: auto; */
/* 	  height: auto; */
/* 	  /* 火狐浏览器 */ */
/*     -moz-border-radius: 25px; */
/*     /* 谷歌和Safari浏览器 */  */
/*     -webkit-border-radius: 25px;    */
/*     /* W3C 语法支持的浏览器 */ */
/*     border-radius:25px;  */
/*     border-radius:5px 15px 20px 25px; */
/* 	} */
	#maxDiv{top: 100px; background-color: #F39921;}
	</style>
	<script type="text/javascript">
	var targetName = '${param.targetName}';
	var width = '${param.divWidth}';
	function fileDetail(id,name){
		
		$.ajax({
			url:'${path}/publishRes/fileDetailList.action?id='+id+"&name="+name,
		    type: 'post',
		    datatype: 'text',
		    success: function (returnValue) {
		    	//去两边逗号
		    	returnValue = returnValue.substring(1,returnValue.length-1);
		    	$('#maxDiv').html(returnValue);
		    	setTimeout(function () {
		    	$('#show1').modal('show');
		    	 }, 700);
		    }
		});
	}
	function fileHide(){
		$('#show1').hide();
	}
		$(function(){
			$('#show1').hide();
			if(targetName!=""){
				targetName = decodeURIComponent($('#targetName').val());
				$('#targetName').val(targetName);
			}
			$('#data_div').width(width);
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = '${path}/publishRes/LookList.action';
	   		var _pk = 'id';
	   		var _conditions = ['fileSize','status','isComplete'];
	   		var _sortName = 'id';
	   		var _sortOrder = 'desc';
			var _colums = [{field:'resName',title:'批次名称',width:fillsize(0.1),align:'center' ,sortable:true},
			               {field:'downloadUser',title:'下载用户',width:fillsize(0.1),align:'center'},
			               {field:'fileSize',title:'文件大小',width:fillsize(0.1),align:'center' ,sortable:true},
// 			               {field:'downloadNum',title:'下载次数',width:fillsize(0.1),align:'center'},
							{field:'status',title:'状态',width:fillsize(0.1),align:'center' ,formatter:$statusDesc},
							{field:'ftpPath',title:'ftp路径',width:fillsize(0.1),align:'center'},
							{field:'downloadType',title:'下载方式',width:fillsize(0.1),align:'center',sortable:true},
							{field:'isComplete',title:'是否压缩',width:fillsize(0.1),align:'center',formatter:$isComplete},
							{field:'pwd',title:'是否加密',width:fillsize(0.1),align:'center',formatter:$pwd},
							{field:'opt1',title:'操作',width:fillsize(0.15),align:'center',formatter:$operate}
							];
			
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
			//返回路径
	   		$.ajax({
				url:'${path}/publishRes/returnPath.action',
			    type: 'post',
			    datatype: 'text',
			    success: function (returnValue) {
			    	$('#temPath').text(returnValue);
			    }
			});
		});
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
// 			if(rec.downloadType!="HTTP"){
			opt += "<sec:authorize url='/target/detail.action'><a onmouseover=\"fileDetail("+rec.id+","+rec.resName+")\" class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
// 			opt += "<sec:authorize url='${path}/target/upd.action'><a class=\"btn hover-red\" href=\"javascript:upd("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 修改</a></sec:authorize>";
// 			opt += "<sec:authorize url='${path}/target/delete.action'><a class=\"btn hover-blue\" href=\"javascript:deleteRecord("+rec.id+")\" ><i class=\"fa fa-trash-o\"></i> 删除</a></sec:authorize>";
// 			}
			return opt;
					
		};
		
		$statusDesc = function(value,rec){
			if(rec.status=='3'){
				return "待处理";
			}else if(rec.status=='2'){
				return "处理中";
			}else{
				return "已完成";
			}
		}
		
		$isComplete = function(value,rec){
			if(rec.isComplete==1)
				return "是";
			else
				return "否";
		}
		$pwd = function(value,rec){
			if(rec.pwd=="")
				return "否";
			else
				return "是";
		}
		/***添加***/
		function add(){
			$.openWindow("${path}/target/upd.action?typeTarget="+$('#typeTarget').val(),'添加',600,400);
//			location.href = "upd.action";
		}
		
		/***修改***/
		/* function upd(id){
			$.ajax({
				url:'${path}/target/deleteYes.action?ids='+id,
			    type: 'post',
			    async : true,
			    success: function (Data) {
			    	data = eval('('+Data+')');
			    	if(data.status==1){
						$.confirm('该标签有关联数据！！如果修改标签类型会清空标签关联数据！',
								function(){
							$.openWindow("${path}/target/upd.action?id="+id+"&typeTarget="+$('#typeTarget').val(),'编辑',600,350);
						});
			    	}else{
							$.openWindow("${path}/target/upd.action?id="+id+"&typeTarget="+$('#typeTarget').val(),'编辑',600,350);
			    	}
			    	$grid.query();
			    }
			});
		} */
		/***修改***/
		function upd(id){
			$.openWindow("${path}/target/upd.action?id="+id+"&typeTarget="+$('#typeTarget').val(),'编辑',600,400);
		}
		function detail(id){
			window.location.href = "${path}/publishRes/ftpHttpDetail.action?id="+id+"&publishType="+$('#libType').val()+"&flag="+$('#flag').val();
// 			$.openWindow("${path}/target/detail.action?id="+id,'详细',600,400);
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
			$.ajax({
				url:'${path}/target/deleteTarget.action?ids='+ids,
			    type: 'post',
			    async : true,
			    success: function (Data) {
			    	data = eval('('+Data+')');
			    	if(data.status==1){
						$.confirm('所选标签有关联数据！！确定要删除吗？',
								function(){
							$.ajax({
								url:'${path}/target/delete.action?ids=' + ids,
							    type: 'post',
							    datatype: 'text',
							    success: function (returnValue) {
							    	$grid.query();
							    }
							});
						});
			    	}else{
						$.confirm('确定要删除所选标签吗？',
								function(){
							$.ajax({
								url:'${path}/target/delete.action?ids=' + ids,
							    type: 'post',
							    datatype: 'text',
							    success: function (returnValue) {
							    	$grid.query();
							    }
							});
						});
			    	}
			    	$grid.query();
			    }
			});
		}
		function query(){
			$grid.query();
		}
		
		function importWord(){
			$.openWindow("system/word/importWord.jsp", '导入标签', 450, 200);
		}
	</script>
</head>
<body>
   	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
<%--    	<input type="hidden" id="status" name="status" value="${param.status }" qType="int"/> --%>
   	<input type="hidden" id="flag" name="flag" value="${param.flag }" qType="int"/>
   	
			<div class="panel panel-default" style="height: 100%;">
				<div class="panel-heading" id="div_head_t">
					<ol class="breadcrumb">
						<li class="active">资源管理
						</li>
						<li class="active">下载管理</li>
					</ol>
				</div>
				<div class="panel-body height_remain" >
					<c:if test="${param.flag==undefined}">
						<div style="float: left;margin-bottom:10px;">
							<div class="form-inline" style="float:left;">
								<div class="form-group">
										<div class="form-inline">
		              							<input class="btn btn-primary red" type="button" value="关闭" onclick="$.closeFromInner();"/>
		<%--               						<c:if test="${param.flag!=undefined}"> --%>
		<!--               						<div class="panel panel-default" style="height: 100%;"> -->
		<!--               						</div> -->
		<%--               						</c:if> --%>
		           						</div>
	        					</div>
	        				</div>
	        			</div>
        			</c:if>
        			<div class="form-inline" style="float:right;">
        					<div class="form-group">
        					<label class="control-label" for="processName">状态:</label>
							      <select name="status" id="status" class="form-control" qType="like" style="width: 99px;">
							      	<option value="">全部</option>
									<option value="1">已完成</option>
									<option value="2">处理中</option>
									<option value="3">待处理</option>
								</select>
							</div>
							<div class="form-group">
        						<label class="control-label" for="processName">是否压缩:</label>
							      <select name="isComplete" id="isComplete" class="form-control" qType="like" style="width: 99px;">
							      	<option value="">全部</option>
									<option value="1">是</option>
									<option value="2">否</option>
								  </select>
							</div>
<!-- 							<div class="form-group"> -->
<!-- 									<input type="text" class="form-control" id="status1" name="status1" qMatch="like" placeholder="输入状态" /> -->
<!-- 							</div> -->
							<sec:authorize url="/system/dataManagement/dataDict/list.action">
									<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							</sec:authorize>
							
<!-- 							<div class="form-inline"> -->
<!--               						<input class="btn btn-default" type="button" value="" onclick="$.closeFromInner();"/> -->
<!--            					</div> -->
					</div>
						<div >
						<strong>文件绝对路径:</strong><font color="red">[<span id="temPath"></span>]</font>
						<button  class="btn btn-primary" onclick="" style="visibility:hidden;margin-bottom:10px">新增</button>
						</div>
						<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
				</div>
			</div>
	</div>
	<div class="modal fade" id="show1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog ">
	    <div class="modal-content col-xs-8"  >
			<div class="modal-body ">
<!-- 			onmouseout="$('#show1').modal('hide');" -->
				<div id="maxDiv">
				</div>
			</div>
		</div>
	</div>
	</div>
<!-- 	<div id="show1" onmouseout="fileHide();" > -->
<!-- 	<p><a id="demo-basic" title="Hey, there! This is a tooltip.sdfsdfsfsfsdfsfsdfsdfsfsfsf" href="#">Hover for a tooltip</a></p> -->
<!-- 	</div> -->
</body>
</html>