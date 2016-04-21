<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>敏感词统计列表</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <script type="text/javascript" src="${path}/appframe/main/js/libs/jquery.messager.js"></script>
        <script type="text/javascript">
         $(function(){
 	   		//定义一datagrid
 	   		var _divId = 'data_div';
 	   		var _url = '${path}/resource/queryList.action?word_type=包含';
 	   		var _pk = 'id';
 	   		var _conditions = ['publishType','resourceName','wordName'];
 	   		var _sortName = 'id';
 	   		var _sortOrder = 'asc';
 	   		var _check=true;
 			var _colums = [ 
							//{ title:'id', field:'id' ,width:100, align:'center',hidden:true },
 						    { title:'资源名称', field:'resourceName' ,width:100, align:'center' ,sortable:true},
 							{ title:'资源id', field:'resourceId' ,width:150, align:'center' },
 						    { title:'状态', field:'status' ,width:100, align:'center'},
 						    { title:'是否包含敏感词', field:'haveWord' ,width:100, align:'center'},
 							{title:'操作',field:'opt1',width:100,align:'center',formatter:$operate}
 							
 						];
 	   		
 			accountHeight();
 			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
 		});
         
         
         /***操作列***/
 		$operate = function(value,rec){
 			var optArray = new Array();
 			var viewUrl= "<sec:authorize url='/resource/view'><a class=\"btn hover-red\" href=\"javascript:view('"+rec.resourceId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a></sec:authorize>";
 		   // var viewUrl = "<a class=\"btn hover-red\"  href="javascript:view('"+rec.resourceId+"')\"><i class=\"fa fa-sign-out\"></i>详细</a> ";		
		   // var editUrl = '<a  class=\"btn hover-red\" href="javascript:filtered('+rec.id+')"><i class=\"fa fa-edit\"></i>过滤</a>';	
		    var setStatus = "";
		   	 optArray.push(viewUrl);
		     //optArray.push(editUrl);
		    return createOpt(optArray);
 		};
        
  	 function query(){
		$grid.query();
 	 }
    
	  /***详细***/
	function view(id){
		$.openWindow("${path}/resource/view.action?id="+id,'资源敏感词详细信息',650,350);
	}
	
	/***过滤***/
	function filtered(){
		downLoading = $.waitPanel('过滤中请稍候...',false);	
		$.ajax({
			url:'${path}/resource/queryResource.action',
		    datatype: 'text',
		    success: function (data) {
		    	if(data == 'ok'){
		    		downLoading.close();
			    	query();
			    	notice("提示信息", "过滤成功！",3);
		    	}else{
		    		notice("提示信息", "过滤失败！",3);
		    	}
		    	
		    }
		});
	}  
	function formReset(){
		$('#queryForm')[0].reset();
		$grid.query();
	}				
				
</script>
</head>
	<body>
    <div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            	查询统计
			        </li>
			        <li>
			            	资源敏感词统计
			        </li>
			        <li>
			            	资源敏感词统计列表
			        </li>
				</ul>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
						<!--解决列表表上面的按钮与查询条件间隙问题 -->
				      <button  class="btn btn-primary" style="background:white;width:1px;">&nbsp;</button>	
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form" id="queryForm">
						   <div class="form-group">
						    	<input type="text" class="form-control" id="resourceName" name="resourceName"  qMatch="like" placeholder="输入资源名称" />
						    </div>
						    <div class="form-group">
						    	<input type="text" class="form-control" id="wordName" name="wordName"  qMatch="like" placeholder="输入敏感词" />
						    </div>
						   <div class="form-group">
						   		<label for="abc">资源类型：</label>
				       		 	<app:selectResType name="publishType" id="publishType" selectedVal=""  headName="请选择"  headValue=""  readonly =""/>
						    </div>
							<%-- <div class="form-group">
								<label for="abc">敏感词：</label>
								<app:select name="word_type" indexTag="word_type" id="word_type"  selectedVal="" headName="请选择"  headValue=""  />
							</div> --%>
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<button type="button" class="btn btn-primary" onclick="formReset();">清空</button>
						</form>
					</div>
				</div>
				<div id="data_div" class="data_div height_remain" style="width:100%;"></div>
			</div>
		</div>
	</div>
	</body>
</html>
