<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>演示主页</title>
	<style type="text/css">
		html, body {height: 100%;}
	</style>
	<script type="text/javascript">
	
	$(document.ready).ready(function(){
		//教材版本列表
		$.ajax({
			url:"${path}/system/FLTX/listMenu.action",
			async:false,
			success:function(data){
				var menu = jQuery.parseJSON(data);
				$.each(menu,function(n,node){
					var li = $("<li></li>");
					li.appendTo($("#sideMenu"));
					var a = $("<a></a>");
					a.text(node.name);
					a.attr({
						"href": "FLTXContent.jsp?type="+node.id+"&name="+encodeURI(encodeURI(node.name)),
						"target": "work_main",
						"id":"menu_"+node.id
					});
					a.appendTo(li);
					addDeleteButtonTo(a,node.id,n);
					addEditButtonTo(a,node,n);
				});
				if(menu.length>0){
					$("#work_main").attr("src","FLTXContent.jsp?type="+menu[0].id+"&name="+encodeURI(encodeURI(menu[0].name)));
				}
			}
		});	
		
		//第一个加底色（其实是第二个）
		$("#sideMenu li:eq(1)").addClass("open");
		
		var li = $("#sideMenu li");
		li.click(function(event){
			li.each(function(){
				$(this).removeClass("open");
			});
			$(this).addClass("open");
		});
		
	});

	function addEditButtonTo(a,node,n){
		var space = $("<span class='pull-right'>&nbsp&nbsp</span>");
		space.appendTo(a);		
		var span = $("<span class='glyphicon glyphicon-pencil pull-right'></span>");
		span.appendTo(a);
		span.click(function(event){
			$("#name").val(node.name);
			$("#indexTag").val(node.indexTag);
			$("#updateIndex").val(n);
			$("#updateId").val(node.id);
			$('#updateModal').modal('show');
			event.stopPropagation();
			event.preventDefault(); 			
		});
	}
	
	function addDeleteButtonTo(a,id,n){
		var span = $("<span class='glyphicon glyphicon-minus pull-right'></span>");
		span.appendTo(a);
		span.click(function(event){
			$.confirm('确定要删除所选数据吗？', function(){
				deleteTable(id,n);
			});
			event.stopPropagation();
			event.preventDefault(); 
		});
	}
	
	function saveTable(){
		var tableName = $("#name").val();
		var tableIndex = $("#indexTag").val();
		if(tableName.length==0||tableIndex.length==0){
			$.alert("请填完整信息！");
			return;
		}
		$.post("${path}/system/FLTX/add.action",{
			name:tableName,
			indexTag:tableIndex
		},function(data){
			if(data>0){
				var li = $("<li></li>");
				li.appendTo($("#sideMenu"));				
				var a = $("<a></a>");
				a.text(tableName);
				a.attr({
					"href": "FLTXContent.jsp?type="+data+"&name="+tableName,
					"target": "work_main",
					"id":"menu_"+data
				});
				a.appendTo(li);
				var node = {
						"id":data,
						"name":tableName,
						"indexTag":tableIndex
				};
				addDeleteButtonTo(a,data,$("#sideMenu").length);
				addEditButtonTo(a,node,$("#sideMenu").length);
				$('#updateModal').modal('hide');
			}
		});
	}
	
	function add(){
		$("#updateIndex").val("");
		$("#updateId").val("");	
		$("#name").val("");
		$("#indexTag").val("");			
		$('#updateModal').modal('show');
	}
	
	function closeWindow(){
		$("#updateIndex").val("");
		$("#updateId").val("");
		$("#name").val("");
		$("#indexTag").val("");			
		$('#updateModal').modal('hide');
	}
	
	function deleteTable(id,index){
		$.post("${path}/system/FLTX/delete.action?id="+id,function(data){
			if(data==0){
//				$("#sideMenu").find("li")[index].remove();
				location.reload();		
			}
		});
	}
	
	function updateTable(){
		var tableName = $("#name").val();
		var tableIndex = $("#indexTag").val();
		if(tableName.length==0||tableIndex.length==0){
			$.alert("请填完整信息！");
			return;
		}	
		$.post("${path}/system/FLTX/update.action?id="+$("#updateId").val(),{
			name:tableName,
			indexTag:tableIndex			
		},function(data){
			var index = parseInt($("#updateIndex").val());
//			$("#sideMenu").children()[index].find("a").text(data);
			var id = $("#updateId").val();
			$("#menu_"+id).text(data);
			var node = {
					"id":id,
					"name":tableName,
					"indexTag":tableIndex
			};
			var a = $("#menu_"+id);
			addDeleteButtonTo(a,id,$("#sideMenu").length);
			addEditButtonTo(a,node,$("#sideMenu").length);			
			$("#updateIndex").val("");
			$("#updateId").val("");
			$("#name").val("");
			$("#indexTag").val("");					
			$('#updateModal').modal('hide');			
		});
	}

	function saveResult(){
		if($("#updateId").val().length>0){
			updateTable();
		}else{
			saveTable();
		}
	}
	</script>
</head>
<body>
	<form id="queryForm" action="">
		<div class="by-main-page-body-side" id="byMainPageBodySide">
		    <div class="page-sidebar">
		        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
		            <i class="fa fa-angle-left"></i>
		        </div>
				<div id="sideWrap">
					<ul class="page-sidebar-menu"  id="sideMenu">
						<li class="by-item-first"></li>
					</ul>
				</div>
				<div class="form-wrap">
					<div class="form-group">
						<input type="button" class="btn btn-primary" onclick="add()" value="添加分类"/>
					</div>
				</div>
		    </div>
		</div>
		<div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title">添加/修改分类</h4>
					</div>
					<div class="modal-body">
						<div class="row">
							<div class="form-wrap">
								<label for="name" class="col-xs-3 control-label text-right">分类名称：</label>
								<input type="text" id="name" class="col-xs-6 text-input" value=""/>
							</div>
						</div>
						<div class="row">
							<div class="form-wrap">
								<label for="name" class="col-xs-3 control-label text-right">索引名称：</label>
								<input type="text" id="indexTag" class="col-xs-6 text-input" value=""/>	
							</div>						
						</div>
					</div>
					<div class="modal-footer">
						<input type="button" class="btn btn-primary" onclick="saveResult()" value="保存"/>
						<input type="button" class="btn btn-primary" onclick="closeWindow()" value="关闭"/>
					</div>
				</div>
			</div>
		</div>				
	</form>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src=""></iframe>
	</div>
	<input type="hidden" id="updateId" value=""></input>
	<input type="hidden" id="updateIndex" value=""/>
</body>
</html>