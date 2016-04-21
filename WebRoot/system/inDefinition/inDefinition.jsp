<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<title>应用集成方定义</title>
	<style>
	li{list-style-type:none;}
	</style>
	<script type="text/javascript">
	 $(function(){
		 query();
	 });
	function showAdd(){
		$('#work_main').attr('src',"${path}/system/inDefinition/add.action");
	}
	function deleteRecord(id){
		/* location.href='${path}/system/inDefinition/delAction.action?id='+id; */
	}
	function returnMain(){
		location.href='${path}/system/inDefinition/inDefinition.jsp';
	} 
	function query(){
		$.ajax({
			type:"post",
			url:"${path}/system/inDefinition/query.action",
			success:function(data){
				var list = JSON.parse(data);
				for(var i=0;i<list.length;i++){
					var listSon = list[i];
					var name = listSon.name;
					var id = listSon.id;
					if(id==71){
						var li = $("<li class='list-group-item'></li>");
						li.appendTo($("#inDefinitionlist"));
						var inform = $("<a style='color:black'></a>");
						inform.attr({
							"href":"${path}/system/inDefinition/detail.action?id="+id,
							"target":"work_main",
						});
						inform.text(name);
						inform.appendTo(li);
					} else {
						var li = $("<li class='list-group-item'></li>");
						li.appendTo($("#inDefinitionlist"));
						var inform = $("<a style='color:black'></a>");
						inform.attr({
							"href":"${path}/system/inDefinition/detail.action?id="+id,
							"target":"work_main",
						});
						inform.text(name);
						//var detail = $("<sec:authorize url='/system/inDefinition/detail.action'></sec:authorize>");
						//inform.appendTo(detail);
						inform.appendTo(li);
						var div = $("<div style='float:right;'></div>");
						var del = $("<a class='btn btn-danger btn-xs'>删除</a>");
						del.attr({
							"href":"${path}/system/inDefinition/delAction.action?id="+id,
						    "targer":"_self",
						});
						//var rights = $("<sec:authorize url='/system/inDefinition/delAction.action'></sec:authorize>");
						//del.appendTo(rights);
						del.appendTo(div);
						div.appendTo(li);
					}
					
					/* var div = $("<div style='float:right;'></div>");
					var div1 = $("<div class='badge' style='background:#F4F5F3'></div>");
					var div2 = $("<div class='badge' style='background:#F4F5F3'></div>");
					var button = $("<button class='btn btn-danger btn-xs' onclick='deleteRecord("+id+")'></button>");
					var span = $("<span></span>");
					span.text("删除");
					span.appendTo(button);
					var href = $("<a></a>"); 
					href.text("元数据");
					href.attr({
						"href":"",
						"target":"work_main"
					});
				    href.appendTo(div1);
					var href1 = $("<a></a>");
					href1.text("数据字典");
					href.attr({
						"href":"",
						"target":"work_main"
					});
				    href1.appendTo(div2);
					div1.appendTo(div);
					div2.appendTo(div);
					button.appendTo(div);
					div.appendTo(li); */ 
				}
			}
		});
	}
	</script>
</head>
<body class="by-frame-body">
<div class="by-main-page-body-side" id="byMainPageBodySide">
    <div class="page-sidebar">
		<div id="sideWrap">
			<div class="by-tool-box">
							<form target="work_main" method="post" id="form">
							   <div class="by-form-row by-form-action">
							   <sec:authorize url="/system/inDefinition/add.action">
							   <button type="button" class="btn btn-info btn-block" onclick="showAdd()">
                                        <span class="glyphicon glyphicon-plus"></span>添加
                               </button>
                               </sec:authorize> 
                               </div>
                               <ul id="inDefinitionlist" class="list-group" style="height:500px;overflow:auto">
                               </ul>
                              
                            </form>
           	</div>
		</div>
   	</div>
</div>
<div class="by-main-page-body-center" id="byMainPageBodyCenter">
    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src=""></iframe>
</div>
</body>
</html>