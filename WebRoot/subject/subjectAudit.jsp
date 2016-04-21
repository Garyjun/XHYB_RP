<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>主题详细</title>
<script type="text/javascript">
	$(function(){
		var div1=$('#meta_tab');
		var id = $("#id").val();
		var restype = $("#restype").val();
		var num1 = 0;
		var res='';
		
		 $('#form1').ajaxSubmit({
			url: '${path}/subject/subjectRes.action?restype='+restype,
			method: 'post',
			async:false,
			success:(function(data){
				var da = JSON.parse(data);
				var keys='';
				for(var key in da){
					res +=da[key]+',';
					++num1;
					if(num1==1){
						div1.append("<li class=\"active\" onclick=\"initRalations("+key+")\"><a href=\"\" data-toggle=\"tab\">"+da[key]+" </a></li>");
						keys =key;
					}else{
						div1.append("<li onclick=\"initRalations("+key+")\"><a href=\"\" data-toggle=\"tab\">"+da[key]+" </a></li>");
					}
				}
				res=res.substring(0,res.length-1);
				 $('#res').append(res);
				 initRalations(keys);
			})
		});
		
	});
	function initRalations(key){
		var id = $("#id").val();
		//$('#relation_data').width('1047px');
		window.onresize=findDimensions();
		var sizewidth=$('#relation').width();
		$('#relation_data').width(sizewidth);
		//定义一datagrid
   		var _divId = 'relation_data';
   		var _url = '${path}/resOrder/resourceList.action?orderId=' + id+'&restype='+key+'&posttype='+$('#posttype').val();
   		var _pk = 'id';
   		var _conditions = [];
   		var _sortName = '';
   		var _sortOrder = 'desc';
		var _colums = [
						<app:QueryListColumnTag />
						{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}
						];
		
		accountHeight();
   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	}
	$operate = function(value,rec){
		var opt= "<sec:authorize url="/subject/view.action"><a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-edit\"></i> 详细</a></sec:authorize>";
		return opt;
	};
	function detail(resId){
		var orderId = '${subjectStore.id}';
		$.openWindow("${path}/resRelease/resOrder/resFileDetail.jsp?orderId="+orderId+"&resId="+resId+"&restype="+$('#res').val()+"&posttype="+$('#posttype').val(), '资源详细信息', 800, 530);
	}
	function returnList() {
		if('${operateFrom}'=='TASK_LIST_PAGE'){
			window.location.href = "${path}/TaskAction/toList.action"; 
		}else{
			window.location.href = "${path}/subject/subjectList.jsp"; 
		}
	}
 	function modelDetail(opFrom){
		var templateId = document.getElementById("templateId").value;
		if(templateId==""||templateId==null){
			$.alert("请选择模板后再查看详细信息！");
			return false;
		}
		$.openWindow(_appPath+"/resRelease/orderPublishTemplate/detail.action?id="+templateId+"&opFrom=resTemp",'模板详细信息',800,500);
			
	}
	
	function doCheck(decision){
		var opFrom = '${operateFrom}';
		var auditMsg = $('#auditMsg').val();
		if(decision=="reject"){
			$("#requiredAuditRemark").show();
			if(auditMsg==""){
				$.alert("审核驳回，请填写审核意见！");
				return;
			}
		}
		if(auditMsg!=null){
			auditMsg = encodeURI(encodeURI(auditMsg));
		}
		var request_url = '${path}/subject/doCheck.action?objectId='+$('#id').val()
				+'&wfTaskId=${wfTaskId}&decision='+decision
				+'&auditMsg=' + auditMsg
				+"&operateFrom=${operateFrom}";
		var operateFrom = '${operateFrom}';
		$.confirm('确定审核'+(decision=='approve'?'通过':'驳回')+'吗？', function(){
			$.ajax({
				url: request_url,
			    type: 'get',
			    datatype: 'text',
			    contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
			    success: function (destination) {
			    	window.location.href = '${path}'+destination;
			    }
			});
		});
	}
	
	var winWidth = 0;
	var winHeight = 0; 
	function findDimensions(){  //函数：获取尺寸
		//获取窗口宽度 
		if (window.innerWidth){
			winWidth = window.innerWidth; 
		} else if ((document.body) && (document.body.clientWidth)){
			winWidth = document.body.clientWidth; 
		} 
		//获取窗口高度 
		if (window.innerHeight){ 
			winHeight = window.innerHeight; 
		} else if ((document.body) && (document.body.clientHeight)){ 
			winHeight = document.body.clientHeight; 
		}
		//通过深入Document内部对body进行检测，获取窗口大小 
		if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth){ 
			winHeight = document.documentElement.clientHeight; 
			winWidth = document.documentElement.clientWidth; 
		} 
		var valueWidth = winWidth.toString();
// 		alert($('#data_div')+"   "+winWidth);
		$('#relation_data').width(valueWidth);
	} 
	findDimensions(); 
	window.onresize=findDimensions; 
</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap ">
	<div class="panel panel-default">
    	<div class="panel-heading" id="div_head_t">
			<ol class="breadcrumb">
				<li class="active">资源发布</li>
				<li class="active">主题库</li>
				<li class="active">主题库审核</li>
			</ol>
		</div>
 	</div>
<form:form action="#" id="form1" class="form-horizontal" name="form1"  modelAttribute="stores" method="post">
	<div class="portlet">
		<div class="portlet-title">
			<div class="caption">通用元数据<a href="javascript:;" onclick="togglePortlet(this)">         
				<i class="fa fa-angle-up"></i></a>
			</div>
		</div>
		<input type="hidden" name="id" id="id" value="${subjectStore.id }"/>
		<input type="hidden" name="status" id="status" value="${subjectStore.status }"/>
		<input type="hidden" name="restype" id="restype" value="${subjectStore.restype }"/>
		<input type="hidden" name="operateFrom" id="operateFrom" value="${operateFrom}">
		<input type="hidden" name="posttype" id="posttype" value="${posttype }">
		<input type="hidden" id="wfTaskId" name="wfTaskId" value="${wfTaskId }">
		<div class="portlet-body">
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">名称 :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.name }
		               		 	</p>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">父专题 :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStores.name }
		               		 	</p>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">名称(英文) :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.nameEn }
		               		 	</p>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">行业:</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.tradeDesc}
		               		 	</p>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">学科 :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.subjectDesc }
		               		 	</p>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">出版商 :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.bookmanDesc }
		               		 	</p>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">资源类别 :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.storetypeDesc }
		               		 	</p>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">语言类型 :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.languageDesc }
		               		 	</p>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">模板名称：</label>
							<div class="col-md-4">
								<div class="input-group">
									<p class="form-control-static" style="white-space:nowrap; width:200px; overflow:hidden;  text-overflow:ellipsis;" title="${subjectStore.template.name}">${subjectStore.template.name}</p>
									<span class="input-group-btn">
										<img src="${path }/appframe/main/images/detail.png" alt="详细" title="详细" style="cursor:pointer;margin-left: 3px;" onclick="javascript:modelDetail('detail');"/>
									</span>
									<input type="hidden" name="template.id" value="${subjectStore.template.id}" id="templateId" />
									<input type="hidden" name="template.type" value="${subjectStore.template.type}" id="templateType" />
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">受众类型 :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.audienceDesc }
		               		 	</p>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">收录开始年限 :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.collectionStart }
		               		 	</p>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">收录结束年限 :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.collectionEnd }
		               		 	</p>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">关键词 :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.keyword }
		               		 	</p>
							</div>
						</div>
					</div>
				</div>
				
				
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4">主题库简介 :</label>
							<div class="col-sm-5">
							  	<p class="form-control-static">
		               				${subjectStore.synopsis }
		               		 	</p>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label col-md-4"><i id="requiredAuditRemark" style="display:none;" class="must">*</i>审核意见 :</label>
							<div class="col-sm-8">
		               				<textarea rows="3" cols="" name="auditMsg" id="auditMsg"  class='form-control' style="width: 170%;display: inline;"></textarea>
							</div>
						</div>
					</div>
				</div>
			</div>
							<table width="98%" border="0" align="center" cellpadding="0"cellspacing="0">
								<tr>
									<td><br/>
										<div align="center">
											<input type="hidden" name="token" value="${token}" />
											<button type="button" class="btn btn-primary"
												onclick="doCheck('approve')">通过</button>
											&nbsp;
											
											<button type="button" class="btn btn-primary"
												onclick="doCheck('reject');">驳回</button>
											&nbsp;
											
											<button type="button" class="btn btn-primary"
												onclick="returnList();">返回</button>
										</div> <br />
										<div id="dt"></div>
										<div id="dt-pag-nav"></div></td>
								</tr>
							</table>
		<div class="panel panel-default">
		<div class="by-tab">
			<ul class="nav nav-tabs nav-justified" id="meta_tab"></ul>
			<div class="portlet">
				<div class="panel-body">
					<div class="tab-content">
        				<div class="tab-pane active" id="tab_1_1_1" style="width:100%">
							<div class="portlet" id="relation">
								<div id="relation_data" class="data_div height_remain" style="width: 100%;"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
		
		</div>
	</div>
</form:form>
 	
</div>
</body>
</html>