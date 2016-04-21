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
   		var _url = '${path}/system/testTemplate/testTemplateItemList.action?pid='+$("#testTemplateId").val();
   		var _pk = 'id';
   		var _conditions = ['pid'];
   		var _sortName = 'id';
   		var _sortOrder = 'desc';
		var _colums = [{field:'testType',title:'题型',width:fillsize(0.2),sortable:true},
						{field:'testTypeKey',title:'题型关键字',width:fillsize(0.2),sortable:true},
						{field:'opt1',title:'操作',width:fillsize(0.22),align:'center',formatter:$operate}];
   		
   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
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
		$("#added").attr("href","${path}/system/testTemplate/addtestTemplateItem.action?pid="+$("#testTemplateId").val());

	});
	
	/***操作列***/
	$operate = function(value,rec){
		var opt = "";
		opt += "<a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 详细</a>";
		/* opt += "<a class=\"btn hover-red\" href=\"${path}/system/testTemplate/testTemplateItemUpd.action?id="+rec.id+"\" target= \"_self\"><i class=\"fa fa-edit\"></i> 修改</a>"; */
		opt += "<a class=\"btn hover-blue\" href=\"javascript:deleteRecord("+rec.id+")\" ><i class=\"fa fa-trash-o\"></i> 删除</a>";
		return opt;
				
	};
	
	function detail(id){
		$.openWindow("${path}/system/testTemplate/testTemplateItemDetail.action?id="+id,'查看',600,320);
	}
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
				url:'delTestTempItemAction.action?ids=' + ids,
			    type: 'post',
			    datatype: 'text',
			    success: function (returnValue) {
			    	$.alert(returnValue,function(){freshDataTable('data_div1');}); 
			    }
			});
		});
	}
	
	</script>
</head>
<body>
	<div id="fakeFrameDict" class="container-fluid by-frame-wrap" style="width:100%;">
	<div class="panel panel-default">
		<div class="form-wrap">
		<form:form action="updAction.action" id="form" modelAttribute="frmtestTemplate" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-sm-5 control-label text-right"><span class="required">*</span>试题模板名称：</label>
				<div class="col-sm-3">
					<form:input path="name"  class="form-control validate[required, maxSize[20]] text-input" id="testTemplateName"/>
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
	           		<form:hidden path="id" id="testTemplateId"/>
	           		<input type="hidden" name="token" value="${token}" />
	            	<input id="tijiao" type="submit" value="提交" class="btn btn-primary"/> 
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            </div>
			</div>
			<div class="form-group col-xs-10">
			    <a id="added" class="btn btn-primary" target=_self>添加</a>
				<input class="btn btn-primary" type="button" value="批量删除" onclick="del();"/>
			</div>
			<div id="data_div1" class="data_div height_remain col-sm-12" style="width:950px;"></div>
		</form:form>
		</div>
	</div>
  </div>
</body>
</html>