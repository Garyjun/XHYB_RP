<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>选择列表</title>
	<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
	<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
	<script type="text/javascript" src="${path}/search/js/bootstrap-select.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css" />
	<script type="text/javascript" src="${path}/search/js/map.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/search/css/modify.css" />
	<script type="text/javascript" src="${path}/appframe/util/appDataGrid.js"></script>
	<script type="text/javascript">
	var dt = new DataGrid();
	$(function() {
		$("select[type=multiSelEle]").multipleSelect({
			multiple: true,
			multipleWidth: 80,
			width: "70%",
			//onOpen: dyAddOpts,
			onClick: optCheckChangeListener
		});
		queryss();
		$("select[type=multiSelEle]").multipleSelect("refresh");
		
	});
	function queryss(){
		$('#data_div').width("1170px");
		dt.setConditions([]);
		dt.setPK('id');
		dt.setSortName('id');
		dt.setURL('${path}/subject/tobztx.action?id='+$('#id').val()+'&publishType='+$('#publishType').val()+'&name='+encodeURI(encodeURI($('#name').val()))+'&storeType='+$('#storeType').val()+'&subLibclassify='+$('#subLibclassify').val());
		dt.setSortOrder('desc');
		dt.setSelectBox('id');
		dt.setColums([ {field:'name',title:'主题库名称',width:fillsize(0.10),align:'center'},
						{field:'tradeDesc',title:'行业',width:fillsize(0.10),align:'center'},
						{field:'collectionStart',title:'收录开始年限',width:fillsize(0.10),align:'center'},
						{field:'collectionEnd',title:'收录结束年限',width:fillsize(0.10),align:'center'},
						{field:'languageDesc',title:'语言',width:fillsize(0.10),align:'center'},
						{field:'opt1',title:'操作',width:fillsize(0.1),align:'center',formatter:$operate,align:'center'}]);
		accountHeight();
		dt.initDt();
		dt.query();
	}
	$operate = function(value,rec){
		var opt= "<sec:authorize url="/subject/view.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.id+"')\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
		return opt;
	};
	function detail(ids){
		parent.parent.layer.open({
		    type: 2,
		    title: '资源详细',
		    closeBtn: true,
		    maxmin: true, //开启最大化最小化按钮
		    area: ['1200px', '550px'],
		    shift: 2,
		    content: "${path}/subject/subjectQuery.action?id="+ids+"&posttype="+$('#posttype').val()+"&datatype=1"  //iframe的url，no代表不显示滚动条
		    
		});
	}
	
	function clears(){
		$('#name').val('');
		$('#storeType').val('');
		$('#subLibclassify').val('');
	}
	function querys(){
		queryss();
	}
	function addsaveres(){
		var codes = getChecked('data_div','id').join(',');
		if (codes == '') {
			layer.alert('请选择要挑选的专题库!');
		}else{
			$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;正在处理中，请稍待。。。</div>',
				css: {
	                border: 'none',
	                padding: '20px',
	                backgroundColor: 'white',
	                textAligh:'center',
	            //    top:"50%",  
	                width:"300px",
	                opacity: .7
	               }
			});
			$('#myForm').ajaxSubmit({
				url: '${path}/subject/addsaveres.action?resid='+codes+'&id='+$('#id').val()+'&publishType='+$('#publishType').val()+'&posttype='+$('#posttype').val(),
				method: 'post',
				success:(function(data){
					layer.alert('资源添加成功!');
					$.unblockUI();
					var parentWin =  top.index_frame.work_main1;
					parentWin.freshDataTable('data_div');
				})
			});
		}
		
	}
	</script>
</head>
<body class="win-dialog">
	<div class="form-wrap">
	<input type="hidden" name="id" id="id" value="${id}"/>
	<input type="hidden" name="posttype" id="posttype" value="${posttype }"/>
			<div class="row">
				<div class="col-md-4">
					<label class="control-label col-md-2">资源类型：</label>
					<div class="col-md-10">
						<select type="multiSelEle" multiple="multiple" id="publishType"  name="publishType">
							<c:forEach items="${lists }" var="lists">
								<option value="${lists.id}" selected = "selected">${lists.typeName}</option>
							</c:forEach>
						</select>
					</div>
				</div>
			</div>
			<div class="portlet">
				<div class="portlet-title" style="width: 100%">
					<div class="caption" style="margin-left: -15px">
						隐藏查询条件 <a href="javascript:;" onclick="togglePortlet(this)"><i class="fa fa-angle-up"></i></a>
					</div>
				</div>
				<form id="myForm" action="" method="post">
					<div id="" class="portlet-body">
						<div id="" class="container-fluid">
							<div class="row">
								<div class='col-md-4'>
									<div class='form-group'>
										<label class='control-label col-md-2'>主题库名称：</label>
										<div class='col-md-6'>
											<input class='form-control' type='text' id='name' name="name"/>
										</div>
									</div>
								</div>
								<div class='col-md-4'>
									<div class='form-group'>
										<label class='control-label col-md-2'>资源类别：</label>
										<div class='col-md-6'>
											<app:select name="storeType" id="storeType" indexTag="kuCategory" headName="--请选择--" headValue=""></app:select>
										</div>
									</div>
								</div>
								<div class='col-md-4'>
									<div class='form-group'>
										<label class='control-label col-md-2'>主题库分类：</label>
										<div class='col-md-6'>
											<app:select name="subLibclassify" id="subLibclassify" indexTag="ZTKtypes" headName="--请选择--" headValue=""></app:select>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
				<table width='98%' border='0' align='center' id='buttonsFrame' cellpadding='0' cellspacing='0' style='margin-top: 1%;'>
					<tr>
						<td>
							<div align='center'>
								<button type='button' class='btn btn-default red' id='query' style='margin-left:30px;' onclick="querys();">查询</button>
								&nbsp;&nbsp;&nbsp;&nbsp;
								<button type='button' class='btn btn-default blue' id='qingkong' onclick='clears();querys();'>清空</button>
							</div>
						</td>
					</tr>
				</table>
				
			<div class="panel-body height_remain">
				<div style="margin: 0px 10px 10px 0px;">
					<input type="button" value="选择" class="btn btn-primary" onclick="addsaveres()" /> &nbsp;
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 99%;"></div>
			</div>
		</div>
</body>
</html>