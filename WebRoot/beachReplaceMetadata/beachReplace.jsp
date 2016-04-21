<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- <%@ include file="/appframe/common.jsp"%> --%>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>批量替换</title>
	<script type="text/javascript" src="${path}/appframe/plugin/jxmenu/js/jquery-powerFloatEdit-min.js"></script>
<%-- 	<script type="text/javascript" src="${path}/appframe/plugin/jxmenu/js/jquery-powerFloat-min.js"></script> --%>
	<script type="text/javascript" src="${path}/appframe/plugin/jxmenu/js/jquery-xmenu.js"></script>
	<link type="text/css" rel="stylesheet" 	href="${path}/appframe/plugin/jxmenu/css/xmenu.css" />
	<style type="text/css">
         #maxDiv{ 
            width: 100%; 
            height: 450px;
         } 
	</style>
	<script type="text/javascript">
		function edit(act){
			document.form1.action=act;
 			jQuery('#form1').submit();
		}

		$(function(){
			$('#form1').validationEngine('attach', {
				relative: true,
				overflownDIV:"#divOverflown",
				promptPosition:"centerRight",
				maxErrorsPerField:1,
				onValidationComplete:function(form,status){
					if(status){
						goBeachReplace();
					}
				}
			});
			var ul = $("#ulValue");
			$.ajax({
				url:"${path}/publishRes/returnZtreeMetadata.action?publishType="+$('#publishType').val(),
			    type: 'post',
			    async:false,
			    datatype: 'text',
			    success: function (data) {
			    	var json = eval("("+data+")");
					 $(json).each(function(index) {
					    	var metaData = json[index];
					    	var key = metaData.key;
					    	var value = metaData.value;
					    	var li = $("<li style=\"width:110px\" rel=\""+key+"\">"+value+"</li>");
					    	li.appendTo(ul);
					 });
			    }
			});
			
			$("#selectpos").xMenu({
				width :600,
				eventType: "click", //事件类型 支持focus click hover
				dropmenu:"#m2",//弹出层
				hiddenID : "selectposhidden"//隐藏域ID
			});
		});
		function save(){
			$('#form1').ajaxSubmit({
 				method: 'post',//方式
 				success:(function(response){
 					callback(response);
           		})
 			});
		}

		function callback(data){
		}
		function showMetadata(){
// 			 var d = art.dialog.open("${path}/beachReplaceMetadata/selectMetadata.jsp",
//     			{
// 				 lock:true,
//  			       title: "选择属性",
//  			       width: "500px",
//  			       height: "600px",
//     			       close: function () {
//     			           //window.location.reload(true);
//     			       }
//     			 });
// 			$.openWindow("${path}/beachReplaceMetadata/selectMetadata.jsp",'批量替换',400,600);
		}
		function goBeachReplace(){
			var page = "";
			var page1 = "";
			var ids = "";
			var pageSize = "";
			var startRow = "";
			var dataDiv = $('#data_div').datagrid('getData');
			//通过class属性获得分页数据大小
			var foo= $("select[class='pagination-page-list']"); 
			$(foo).each(function() {
				pageSize = this.value;
			})
			var va = $("input[name='newPage']:checked").val();
			//计算第几页开始
			startRow = dataDiv.startRow;
			page = parseInt(startRow/pageSize)+1;
			if(va==1){
				//计算是全部页
				var num = dataDiv.total;
				if(parseInt(num)%parseInt(pageSize)==0){
					page1 = parseInt(num/pageSize)+1;
				}else{
					page1 = parseInt(num/pageSize)+2;
				}
			}else{
				page1 = page+1;
			}
			var searchParamCa = {
					ids:ids,
					fieldValue:$('#replaceField').val(),
					publishType:'${param.publishType}',
					page:page,
					field:$('#selectposhidden').val(),
					page1:page1,
					size:parseInt(pageSize),
					queryModel:encodeURIComponent(getQueryParamString(_conditions))
		        };
			var paramCa  = encodeURI(JSON.stringify(searchParamCa));
			downLoading = $.waitPanel('替换中请稍候...',false);
			$.ajax({
				url:'${path}/publishRes/beachReplace.action',
			    type: 'post',
			    datatype: 'json',
			    data:"beachReplaceCa=" + paramCa,
			    success: function (returnValue) {
			    	var statusValue = eval("("+returnValue+")");
			    		$.alert(statusValue.status);
			    	$("#beachReplace").modal('hide');
			    	query();
			    	downLoading.close();
			    }
			});
		
		}
	</script>
</head>
<body>
<div class="modal fade" id="beachReplace" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog ">
    <div class="modal-content col-xs-9"  >
			<div class="modal-body ">
			<div id="maxDiv">
				<div class="form-wrap">
		      		<form action="" name="form1" id="form1" class="form-horizontal">
					<div class="form-group">
						<div class="topnav">
							<label for="name" class="col-sm-4 control-label text-right"><span class="required">*</span>选择资源属性：</label>
							<div class="col-xs-5">
								<div id="lead" class="card" >
									<a id="selectpos" href="javascript:void(0);" class="as" >
										<span  title="请选择">请选择</span>
										<input type="hidden" value="1" id="selectposhiddenMaxNumber"/>
									</a>
								</div>
							</div>
								<input type="hidden" value="" id="selectposhidden"/>
	<%-- 						<a onclick="showMetadata()" href="###"  style="margin-left: 1%;display: inline;"><img src="${path}/appframe/main/images/select.png" alt="选择"/></a> --%>
	<%-- 						<a onclick="clearMetadata()" href="###"  style="margin-left: 1%;display: inline;"><img src="${path}/appframe/main/images/clean.png" alt="选择"/></a> --%>
						</div>
					</div>
					<div class="form-group">
						<label for="name" class="col-sm-4 control-label text-right"><span class="required">*</span>批量替换的值：</label>
						<div class="col-xs-5">
							<input type="text" name="replaceField" id="replaceField" style="width:84%" class="form-control text-input validate[required]" />
						</div>
					</div>
					<div class="form-group">
						<label for="name" class="col-sm-4 control-label text-right">页码范围：</label>
		      			<div class="col-md-6">
						    <div class="form-group">
								 <div class="col-md-8">
								 		<input type="radio" name="newPage" id="newPage" value="0" checked="checked"/>当前页&nbsp;&nbsp;&nbsp;
										<input type="radio" name="newPage" id="newPage" value="1"/>全部页
								 </div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-offset-4 col-sm-6">
   					  			 <button type="submit"  class="btn btn-primary">替换</button>
   							  &nbsp;
			            	<button type="button" class="btn btn-primary" data-dismiss="modal">关闭</button>
			            </div>
					</div>
					</form>
					<div id="m2" class="xmenu" style="display: none;">
						<div class="select-info">	
							<label class="top-label">已选元数据：</label>
							<ul>
							
							</ul>
							<a  name="menu-confirm" href="javascript:void(0);" class="a-btn">
								<span class="a-btn-text">确定</span>
							</a> 
						</div>			
						<dl>
							<dt class="open">选择元数据</dt>
								<dd>       
									<ul id="ulValue">
									</ul>						
								</dd>
						</dl>			
					</div>
		  	 	</div>
	  		</div>
	  	</div>
	  </div>
	 </div>
 </div>
</body>
</html>