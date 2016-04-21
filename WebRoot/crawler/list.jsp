<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
    <head>
        <title>采集结果管理</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<script type="text/javascript">
			$(function(){
	 	   		var _divId = 'data_div';
	 	   		var _url = '${path}/crawler/query.action';
	 	   		var _pk = 'id';
	 	   		var _conditions = ['title','channelId'];
	 	   		var _sortName = 'id';
	 	   		var _sortOrder = 'desc';
	 	   		var _check=true;
	 			var _colums = [ 
					{ title:'编号', field:'sn' ,width:150, align:'center',sortable:true },
					{ title:'标题', field:'title' ,width:350, align:'left',sortable:true,formatter:$link },
					{ title:'来源', field:'source' ,width:80, align:'left',sortable:false,formatter:$source},
				    { title:'发布时间', field:'postime' ,width:120, align:'center'},
				    //{ title:'点击数', field:'clicks' ,width:80, align:'center' },
				    //{ title:'回复数', field:'replys' ,width:80, align:'center' },
				    { title:'状态', field:'status' ,width:80, align:'center'},
				    { title:'采集时间', field:'createTime' ,width:125, align:'center'},
					{ title:'操作',field:'opt',width:fillsize(0.1),align:'center',formatter:$operate}
	 			];
	 			accountHeight();
	 			$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
	 		});
			
			$time = function(value,row){
				return row.crateTime.substring(0,14);
			}
			
			$link = function(value,row){
				return "<a href=\"javascript:view('"+row.url+"')\">"+value+"</a>";
			}
			
			$source = function(value,row){
				if(row.source){
					return row.source.replace(/来源：/g,"");	
				}else{
					return row.source	
				}
				
			}
			
			/***操作列***/
	 		$operate = function(value,rec){
	 			var opt = "";
	 			var viewUrl='<sec:authorize url="/role/view/*"><a class=\"btn hover-red\" href="javascript:view(\''+rec.url+'\')" ><i class=\"fa fa-sign-out\" ></i>查看</a>&nbsp;</sec:authorize> ';
				opt = viewUrl;
				return opt;
			};
			
			function query(channelId) {
				$("#channelId").val(channelId);
				$grid.query();
				setInterval("$grid.query()",10000);
			}
			
			// 启动
			function view(url){
				$.openWindow(url,'结果页',1000,600);
			}
			
			function formReset() {
				$grid.clean();
				query();
			}
		</script>
    </head>
   <body data-spy="scroll" >
     <div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            <a href="###">数据采集</a>
			        </li>
			        <li>
			            <a href="###">采集结果</a>
			        </li>
				</ul>
			</div>
			<div class="panel-body height_remain" id="999">
				<div style="margin: 0px 10px 10px 0px;">
					<button  class="btn btn-primary" style="background:white;width:1px;">&nbsp;</button>	
					
					<div style="float: right;">
						<form action="#" class="form-inline no-btm-form" role="form" id="queryForm">
						   <input type="hidden" id="channelId" name="channelId"/>
						   <div class="form-group">
							      <input type="text" class="form-control" id="title" name="title"  placeholder="输入文章标题"/>
							</div>							
							<input type="button" value="查询" class="btn btn-primary" onclick="query()"/>
							<button type="button" class="btn btn-primary" onclick="formReset();">清空</button>
						</form>
					</div>
					
				</div>
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
  </body>
</html>
