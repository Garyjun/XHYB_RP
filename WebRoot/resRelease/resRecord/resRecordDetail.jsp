<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
	<head>
		<title>发布记录管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
		<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript" src="${path}/search/js/bootstrap-select.js"></script>
    	<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css"/>
		<script type="text/javascript" src="${path}/appframe/util/zxappDataGrid.js"></script>
		<script type="text/javascript">
			$(function() {
				 $('.selectpicker').selectpicker({
					'hideDisabled': true, //不显示选中勾选图标
	                'selectedText': 'cat'
	            }); 
				 var map = '${map}';
				var da = JSON.parse(map);
				var keys='';
				var div1=$('#meta_tab');
				var num1 = 0;
				for(var key in da){
					++num1;
					if(num1==1){
						div1.append("<li class=\"active\" onclick=\"showTab("+key+")\"><a href=\"\" data-toggle=\"tab\">"+da[key]+" </a></li>");
						keys =key;
					}else{
						div1.append("<li onclick=\"showTab("+key+")\"><a href=\"\" data-toggle=\"tab\">"+da[key]+" </a></li>");
					}
				}
				showTab(keys);
			});
			var dt = new DataGrids();
			function showTab(key) {
				//定义一datagrid
				$('#restype').val(key);
				window.onresize=findDimensions();
				var sizewidth=$('#relation').width();
				$('#data_div').width(sizewidth);
				dt.setConditions(['objectId']);
				dt.setPK('objectId');
				dt.setSortName('detailId');
				dt.setURL("${path}/resRelease/resReleaseDetailQuery.action?id=${resRelease.id}&restype="+key);
				dt.setSortOrder('desc');
				dt.setColums([ 
					{ title:'资源名称', field:'resName' ,width:fillsize(0.15), align:'center'},
					{title:'文件加工</br>成功数/失败数/总数', field:'processMessage' ,width:fillsize(0.2), align:'center'},
					//{title:'文件发布</br>成功数/失败数/总数', field:'publishMessage' ,width:fillsize(0.2), align:'center', sortable:true},
					{title:'创建时间', field:'createTime' ,width:fillsize(0.2), align:'center', sortable:true}, 
// 					{title:'备注',field:'remark',width:fillsize(0.1),align:'center'},
					{title:'操作',field:'opt1',width:fillsize(0.15),align:'center',formatter:$operate}
				]);
				accountHeight();
				dt.initDt();
				dt.query();
			}
				
			/***操作列***/
	 		$operate = function(value,rec){
		 			var detailUrl= "<sec:authorize url='/role/detail/*'> <a class=\"btn hover-red\" href=\"javascript:detail('"+rec.resId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a></sec:authorize>"
		 			var opt = detailUrl;
	 			return opt;
	 		};
	   		
	   		function detail(resId){
	   			var orderId = ${resRelease.orderId};
	   			//$.openWindow("${path}/resRelease/resRecord/publishResDetail.jsp?orderId=" + orderId + "&resId=" + resId, '发布资源文件信息', 900, 500);
	   			parent.parent.layer.open({
	   			    type: 2,
	   			    title: '发布资源文件信息',
	   			    closeBtn: true,
	   			    maxmin: true, //开启最大化最小化按钮
	   			    area: ['900px', '500px'],
	   			    shift: 2,
	   			    content: "${path}/resRelease/resRecord/publishResDetail.jsp?orderId=" + orderId + "&resId=" + resId +"&posttype=" +$('#posttype').val()
	   			});
	   		}
	   		
	   		function downloadFile(resId){
				if(resId.indexOf("urn：asset")){
					down4Encrypt('${path}/bres/downloadRes/${param.libType}.action?ids='+resId);
				}else{
					down4Encrypt('${path}/bookRes/downloadBookRes/${param.libType}.action?ids='+resId);
				}
	   			
	   		}
	   		
			function queryForm(){
				dt.dataGridObj.datagrid({
					url : "${path}/resRelease/queryDetailByProAndPubStatus.action?processStatus=" + 
							$("#processStatus option:selected").val() + 
							"&status=" + $("#status  option:selected").val() +
							"&relId=${resRelease.id}"+
							"&restype="+$('#restype').val()
				}); 
			}
			
			function closess(){
				var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
				parent.layer.close(index); //再执行关闭  
			}
			/* function returnList(){
				location.href = "${path}/resRelease/resRecord/resRecordList.jsp";
			} */
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
				$('#data_div').width(valueWidth);
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
						<li><span>资源发布</span></li>
						<li><span>发布记录</span></li>
						<li><span>发布记录明细</span></li>
					</ol>
				</div>
				<form id="form1" class="form-horizontal">
					<div class="tab-pane" id="tab_1_1_2">
					<p></p>
					<input type="hidden" id="posttype" name="posttype" value="${resRelease.posttype }"/>
					<input type="hidden" id="restype" name="restype"/>
						<div class="portlet">
							<div class="portlet-title">
								<div class="caption">
									查询条件 <a href="javascript:;" onclick="togglePortlet(this)">
									</a>
								</div>
							</div>
							<div class="portlet-body">
								<div class="row">
									<div class="container-fluid">
										<div class="row">
											<div class="col-md-6">
												<div class="form-group">
													<label class="control-label col-md-4" style="margin-right:-50px;">加工状态：</label>
													<div class="col-md-7">
														<select class="selectpicker bla bla bli processStatus" name="processStatus" id="processStatus" data-live-search="false" style="border:solid 1px #ffcc00; margin-left:-10px;">
													        <option value="">全部</option>
													        <option value="proSuccess">成功</option>
													        <option value="procFailed">失败</option>
												   		</select>
													</div>
												</div>
											</div>
											
											<div class="col-md-6">
												<div class="form-group">
													<label class="control-label col-md-4" style="margin-right:-50px;">发布状态：</label>
													<div class="col-md-7">
														<select class="selectpicker bla bla bli publishStatus" name="status" id="status" data-live-search="false" style="border:solid 1px #ffcc00; margin-left:-10px;">
													        <option value="">全部</option>
													        <option value="pubSuccess">成功</option>
													        <option value="pubFailed">失败</option>
												   		</select>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							
							<table width="98%" border="0" align="center" cellpadding="0"
								cellspacing="0">
								<tr>
									<td><br />
										<div align="center">
											<button type="button" class="btn btn-primary"
												onclick="queryForm();">查询</button>
												&nbsp;&nbsp;&nbsp;&nbsp;
												<c:choose>
													<c:when test="${datetype==1 }">
													<button type="button" class="btn btn-primary"
													onclick="closess();">关闭</button>
													</c:when>
													<c:otherwise>
													<button type="button" class="btn btn-primary"
												onclick="history.go(-1);">返回</button>
													</c:otherwise>
												</c:choose>
												
										</div> <br />
										<div id="dt"></div>
										<div id="dt-pag-nav"></div></td>
								</tr>
							</table>
							<div class="portlet">
	  							<div class="portlet-title">
	      							<div class="caption">资源文件明细 <font color="red">【文件加工成功数：即拷贝成功的数量；水印添加失败的不计算在内，可在<详细>中查看添加水印情况】</font></div>
	  							</div>
	  							<!-- <div class="panel-body" id="999" style="width:99%;">
		   							<div class="portlet">
		   							<ul class="nav nav-tabs nav-justified" id="meta_tab"></ul>
									<div class="tab-content">
        								<div class="tab-pane active" id="tab_1_1_1" style="width:100%">
											<div class="portlet" id="relation">
												<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
											</div>
										</div>
									</div>
									</div>
	  							</div> -->
	  							
	  						</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="by-tab">
							<div class="portlet">
								<div class="panel-body">
								<ul class="nav nav-tabs nav-justified" id="meta_tab"></ul>
									<div class="tab-content">
				        				<div class="tab-pane active" id="tab_1_1_1" style="width:100%">
											<div class="portlet" id="relation">
												<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</body>
</html> 