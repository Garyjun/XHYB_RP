<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
	<script type="text/javascript" src="${path}/orphanResource/unitTree.js"></script>
	<script type="text/javascript">
		$(function(){
	   		//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = 'list.action?type=${param.type}';
	   		var _pk = 'objectId';
	   		var _conditions = ['title','creator','keywords','description','modifiedStartTime','modifiedEndTime','status','type'];
	   		var _sortName = 'objectId';
	   		var _sortOrder = 'desc';
			var _colums = [{field:'commonMetaData.commonMetaDatas.title',title:'资源标题',width:fillsize(0.17),sortable:true},
							{field:'commonMetaData.commonMetaDatas.creator',title:'制作者',width:fillsize(0.07),sortable:true},
							{field:'commonMetaData.commonMetaDatas.keywords',title:'关键字',width:fillsize(0.1),sortable:true},
							{field:'commonMetaData.commonMetaDatas.status',title:'状态',width:fillsize(0.1),sortable:true,formatter:$statusDesc},
							{field:'commonMetaData.commonMetaDatas.modified_time',title:'更新时间',width:fillsize(0.18),sortable:true},
							{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate}];
	   		
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		
		var statusMap;
		function getStatusMap() {
			$.ajax({
				url : '${path}/res/wf/getStatusMap.action',
				type : 'post',
				async : false,
				success : function(data) {
					statusMap = eval('(' + data + ')');
				}
			});
		}
		
		 //资源：状态描述
		$statusDesc = function(value, rec) {
			getStatusMap();
			return statusMap[rec.commonMetaData.commonMetaDatas.status];
		};
		/***操作列***/
		$operate = function(value,rec){
			var opt = "";
			opt += "<sec:authorize url='/orphanResource/mountResource.action'><a class=\"btn hover-blue\" href=\"javascript:mountResource('"+rec.objectId+"')\" ><i class=\"fa fa-anchor\"></i> 挂载</a></sec:authorize>";
			opt += "<sec:authorize url='/orphanResource/detail.action'><a class=\"btn hover-blue\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a></sec:authorize>";
			if(rec.commonMetaData.commonMetaDatas.status==3)
				opt += "<sec:authorize url='/orphanResource/offlineResource.action'><a class=\"btn hover-blue\" href=\"javascript:offlineResource('"+rec.objectId+"')\" ><i class=\"fa fa-trash-o\"></i> 下线</a></sec:authorize>";
			opt += "<sec:authorize url='/orphanResource/delete.action'><a class=\"btn hover-blue\" href=\"javascript:deleteResource('"+rec.objectId+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a></sec:authorize>";
			return opt;
					
		};
		
		/***删除***/
		function del() {
			var codes = getChecked('data_div','id').join(',');
			if (codes == '') {
			    $.alert('请选择要删除的数据！');
			} else {
				deleteRecord(codes);
			};
		}
		function deleteResource(ids){
			$.confirm('确定要删除所选数据吗？', function(){
				$.ajax({
					url:'delete.action?ids=' + ids,
				    type: 'post',
				    datatype: 'text',
				    success: function (returnValue) {
				    	$grid.query();
				    }
				});
			});
		}
		function query(){
			$grid.query();
		}
		
		function mountResource(id){
			$("#resourceId").val(id);
			//教材版本列表
			$("#versionList").empty();
			$("#peroidList").empty();
			$("#subjectList").empty();
			$("#gradeList").empty();
			$("#volumeList").empty();
			$("#unitName").val("");
			$("#unit").val("");
			$("<option></option>").text("请选择版本").appendTo($("#versionList"));
			$.get("${path}/system/book/selectRelative.action?type=version",function(data){
				var books = eval("("+data+")");
				for(var i=0;i<books.domains.length;i++){
					var book = books.domains[i];
					$("<option></option>").val(book.code).text(
							book.label).appendTo($("#versionList"));
				}
				$("#mountResource").modal('show');
			});
		}
		
		//选择学段
		function changeVersion(){
			var code = $("#versionList").val();
			var selectList = $("#peroidList");
			$("#peroidList option[index!='0']").remove();
			getNodeList(selectList,code,"请选择学段");
		}
		//选择学科
		function changePeroid(){
			var code = $("#versionList").val() + "," + $("#peroidList").val();
			var selectList = $("#subjectList");
			$("#subjectList option[index!='0']").remove();
			getNodeList(selectList,code,"请选择学科");
		}
		//选择年级
		function changeSubject(){
			var code = $("#versionList").val() + "," + $("#peroidList").val()
				+ "," + $("#subjectList").val();
			var selectList = $("#gradeList");
			$("#gradeList option[index!='0']").remove();
			getNodeList(selectList,code,"请选择年级");
		}
		//选择分册
		function changeGrade(){
			var code = $("#versionList").val() + "," + $("#peroidList").val()
			+ "," + $("#subjectList").val() + "," + $("#gradeList").val();
			var selectList = $("#volumeList");
			$("#volumeList option[index!='0']").remove();
			getNodeList(selectList,code,"请选择分册");
		}
		
		function getNodeList(selectList,code,name){
			selectList.empty();
			$("<option></option>").text(name).appendTo(selectList);
			$.get("${path}/system/book/selectRelative.action?code="+code,function(data){
				var books = eval("("+data+")");
				for(var i=0;i<books.domains.length;i++){
					var book = books.domains[i];
					$("<option></option>").val(book.code).text(
							book.label).appendTo(selectList);
				}
			});
		}
		
		function save(){
			if($("#versionList").val()=="请选择版本"){
				$.alert("请选择教材版本");
				return;
			}
			if(!$("#peroidList").val()||$("#peroidList").val()=="请选择学段"){
				$.alert("请选择学段");
				return;
			}
			if(!$("#subjectList").val()||$("#subjectList").val()=="请选择学科"){
				$.alert("请选择学科");
				return;
			}
			if(!$("#gradeList").val()||$("#gradeList").val()=="请选择年级"){
				$.alert("请选择年级");
				return;
			}
			if(!$("#volumeList").val()||$("#volumeList").val()=="请选择分册"){
				$.alert("请选择分册");
				return;
			}
			if(!$("#unit").val()||$("#unit").val()=="请选择单元"){
				$.alert("请选择单元");
				return;
			}			
			$.post("mountResource.action",{
				version:	$("#versionList").val(),
				peroid:		$("#peroidList").val(),
				subject:	$("#subjectList").val(),
				grade:		$("#gradeList").val(),
				volume:		$("#volumeList").val(),
				unit:		$("#unit").val(),
				unitName:	$("#unitName").val(),
				resourceId:	$("#resourceId").val()
			},function (data){
				$.alert("挂载成功！");
				query();
				$("#mountResource").modal('hide');
			});
		}
		
		/***查看***/
		function detail(objectId){
			window.location.href = "${path}/orphanResource/detail.action?libType=2&objectId="+objectId;
		}
		
		/***资源下线***/
		function offlineResource(objectId){
			$.confirm('确定要下线所选资源吗？', function() {
				$.ajax({
					url : _appPath + '/orphanResource/offlineResource.action?resourceId=' + objectId,
					type : 'post',
					success : function(returnValue) {
						query();
					}
				});
			});
		}

	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">资源管理</a></li>
					<li class="active">孤儿资源列表</li>
				</ol>
			</div>
			<div class="panel-body height_remain" id="999">
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
		<div class="modal fade" id="mountResource" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		        <h4 class="modal-title" id="myModalLabel">挂载图书目录</h4>
		      </div>
		      <div class="modal-body">
		      	<div class="form-group">
			        <div class="row">
			        	<label class="col-xs-1 control-label">版本</label>
			        	<div class="col-xs-5">
							<select class="form-control" onchange="changeVersion()" id="versionList">
								<option value="">请选择版本</option>
							</select>
						</div>			        
			        	<label class="col-xs-1 control-label">学段</label>
			        	<div class="col-xs-5">
							<select class="form-control" onchange="changePeroid()" id="peroidList">
								<option value="">请选择学段</option>
							</select>
						</div>
			        </div>
				</div>
				<div class="form-group">
			        <div class="row">
			        	<label class="col-xs-1 control-label">学科</label>
			        	<div class="col-xs-5">
							<select class="form-control" onchange="changeSubject()" id="subjectList">
								<option value="">请选择学科</option>
							</select>
						</div>
			        	<label class="col-xs-1 control-label">年级</label>
			        	<div class="col-xs-5">
							<select class="form-control" onchange="changeGrade()" id="gradeList">
								<option value="">请选择年级</option>
							</select>
						</div>						
			        </div>
				</div>
				<div class="form-group">
			        <div class="row">
			        	<label class="col-xs-1 control-label">分册</label>
			        	<div class="col-xs-5">
							<select class="form-control" id="volumeList">
								<option value="">请选择分册</option>
							</select>
						</div>
			        	<label class="col-xs-1 control-label">单元</label>
			        	<div class="col-xs-5">
							<div class="input-group">
								<input type="text" class="form-control" id="unitName" readonly="readonly"/>
								<input type="hidden" id="unit"/>
								<span class="input-group-btn">
									<a id="menuBtn" onclick="showUnit(); return false;" href="###" class="btn btn-primary">选择</a>
								</span>
							</div>
						</div>						
			        </div>			        			        
			    </div>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" onclick="save();">保存</button>
		        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		      </div>
		    </div>
		  </div>
		</div>			
		<input type="hidden" id="description" name="description" value="${param.description }" />
		<input type="hidden" id="type" name=type value="${param.type }" />		
		<input type="hidden" id="status" name="status" value="${param.status }" />		
		<input type="hidden" id="title" name="title" value="${param.title }" />
		<input type="hidden" id="creator" name="creator" value="${param.creator }" />
		<input type="hidden" id="keywords" name="keywords" value="${param.keywords }" />
		<input type="hidden" id="modifiedStartTime" name="modifiedStartTime" value="${param.modifiedStartTime }" />
		<input type="hidden" id="modifiedEndTime" name="modifiedEndTime" value="${param.modifiedEndTime }" />	
		<input type="hidden" id="resourceId"/>	
	</div>
</body>
</html>