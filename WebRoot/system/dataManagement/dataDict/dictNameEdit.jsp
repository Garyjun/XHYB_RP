<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>


<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
	
	$(function(){
   		//定义一datagrid
   		var _divId = 'data_div1';
   		var _url = '${path}/system/dataManagement/dataDict/listValue.action?dictNameId='+$("#dictNameId").val();
   		var _pk = 'id';
   		var _conditions = ['pid'];
   		var _sortName = 'id';
   		var _sortOrder = 'desc';
		var _colums = [{field:'name',title:'参数名称',width:fillsize(0.2),sortable:true},
		               {field:'shortname',title:'参数简称',width:fillsize(0.2),sortable:true},
						{field:'indexTag',title:'参数值',width:fillsize(0.2),sortable:true},
						{field:'description',title:'描述',width:fillsize(0.2)},
						{field:'opt1',title:'操作',width:fillsize(0.22),align:'center',formatter:$operate}];
   		
   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
   		$grid.query();
		$('#form').validationEngine('attach', {
			relative: true,
			overflownDIV:"#divOverflown",
			promptPosition:"centerRight",
			maxErrorsPerField:1,
			onValidationComplete:function(form,status){
				if(status){
					$('#form').ajaxSubmit({
						method: 'post',//方式
						success:(function(response){
							top.index_frame.work_main.freshDataTable('data_div');
							$.closeFromInner();
		       			})
					});
				}
			}
		});
		$("#added").attr("href","${path}/system/dataManagement/dataDict/addDictValue.action?pid="+$("#dictNameId").val());

	});
	
	/***操作列***/
	$operate = function(value,rec){
		var opt = "";
		opt += "<a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
		opt += "<a class=\"btn hover-red\" href=\"${path}/system/dataManagement/dataDict/dictValueUpd.action?id="+rec.id+"\" target= \"_self\"><i class=\"fa fa-edit\"></i> 修改</a>";
		/* opt += "<a class=\"btn hover-red\" href=\"javascript:upd("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 修改</a>"; */
		opt += "<a class=\"btn hover-blue\" href=\"javascript:deleteRecord("+rec.id+")\" ><i class=\"fa fa-trash-o\"></i> 删除</a>";
		return opt;
				
	};
	
	/* function upd(id){
		location.href = "${path}/system/dataManagement/dataDict/dictValueUpd.action?id="+id;
	} */
	
	function detail(id){
		$.openWindow("${path}/system/dataManagement/dataDict/dictValueDetail.action?id="+id,'查看',600,320);
		//location.href = "dictValueDetail.action?id="+id;
	}
	function query(){
		$grid.query();
	}
	/* function addDictValue(){
		$.openWindow("${path}/system/dataManagement/dataDict/addDictValue.action?pid="+$("#dictNameId").val(),'添加',600,320);
	}
 */
	/***删除***/
	function del() {
		var codes = getChecked('data_div1','id').join(',');
		if (codes == '') {
		    $.alert('请选择要删除的数据！');
		} else {
			deleteRecord(codes);
		};
	}
	
	function deleteRecord(ids){
		$.confirm('确定要删除所选数据吗？', function(){
			$.ajax({
				url:'delDictValueAction.action?ids=' + ids,
			    type: 'post',
			    datatype: 'text',
			    success: function (returnValue) {
			    	$.alert(returnValue,function(){freshDataTable('data_div1');}); 
			    }
			});
		});
	}
	function addRelationFlag() {
		var relationTree = $("#relationTree").val();
		$("#indexTag").val(relationTree);
	}
	
	//批量导入数据字典的详细项
	function importDir(){
		var d = art.dialog.open("system/dataManagement/dataDict/importDir.jsp?pid="+$("#dictNameId").val(),
         {
		   lock:true,
           title: "导入数据字典项",
           width: "450px",
           height: "200px",
           close: function () {
               query();
               //window.location.reload(true);
           }
       });
		d.showModal();
		//$.openWindow("system/dataManagement/dataDict/importDir.jsp?pid="+$("#dictNameId").val()+"&iframeName="+iframeName, '导入数据字典项',450,200);
	}
	
		
	</script>
</head>
<body>
	<div id="fakeFrameDict" class="container-fluid by-frame-wrap" style="width:100%;">
	<div class="panel panel-default">
		<!-- <div class="panel-heading" id="div_head_t">
			<ol class="breadcrumb">
				<li><a href="javascript:;">数据字典</a></li>
				<li class="active">字典信息</li>
			</ol>
		</div> -->
		<div class="form-wrap">
		<form:form action="updAction.action" id="form" modelAttribute="frmDictName" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-sm-5 control-label text-right"><span class="required">*</span>字典名称：</label>
				<div class="col-sm-3">
					<form:input path="name"  class="form-control validate[required, maxSize[200],ajax[validateDictName]] text-input" id="dictName"/>
				</div>
			</div>
			<div class="form-group">
				<label class="col-sm-5 control-label text-right"><span class="required">*</span>索引名称：</label>
				<div class="col-sm-3">
					<form:input path="indexTag" id="indexTag" class="form-control validate[required, maxSize[200],custom[indexTag]] text-input"/>
				</div>
				<input class="btn btn-primary btn-sm col-xs-1" type="button" value="添加前缀" onclick="addRelationFlag();"/>
				<span class="col-xs-1" style="font-size:15px;color:red">*(添加前缀仅用于元<br/>数据输入类型中的<br/>数据字典取值范围)</span>
			</div>
			<div class="form-group">
				<label  class="col-sm-5 control-label">状态：</label>
				<div class="col-sm-3">
					<form:radiobutton path="status" value="1"/>可用
					<form:radiobutton path="status" value="0"/>禁用
				</div>
			</div>
			<div class="form-group">
				<label class="col-sm-5 control-label text-right">描述：</label>
				<div class="col-sm-3">
					<form:textarea path="description" rows="5" class="form-control validate[maxSize[40]]"/>
				</div>
			</div>			
			<div class="form-group">
				<div class="col-xs-offset-5 col-xs-7">
	           		<form:hidden path="id" id="dictNameId"/>
	           		<input type="hidden" name="token" value="${token}" />
	           		<input type="hidden" id ="relationTree" value="relation_"/>
	            	<input id="tijiao" type="submit" value="提交" class="btn btn-primary"/> 
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            </div>
			</div>
			<div class="form-group col-xs-10">
			    <a id="added" class="btn btn-primary" target=_self>添加</a>
				<!-- <input class="btn btn-primary" type="button" value="添加" onclick="javascript:addDictValue();"/>  -->
				<input class="btn btn-primary" type="button" value="批量删除" onclick="del();"/>
				<input class="btn btn-primary" type="button" value="批量添加" onclick="importDir();"/>
			</div>
			<div id="data_div1" class="data_div height_remain col-sm-12" style="width:950px;"></div>
		</form:form>
		</div>
	</div>
  </div>
</body>
</html>