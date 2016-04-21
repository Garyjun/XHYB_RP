<!doctype html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="viewport" content="width=device-width, user-scalable=no">
	<script type="text/javascript" src="${path}/appframe/plugin/jiuGongGe/js/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/jiuGongGe/css/font-awesome.min.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/jiuGongGe/js/jquery.popmenu.js"></script>
	<title>演示主页</title>
	<style type="text/css">
		html, body {height: 100%;}
		.fa{
            font-size: 20px;
            line-height: 30px;
        } 
	</style>
	<script type="text/javascript">
	$(function(){
		queryGroup();
		queryFileGroup();
		
		$("#sideMenu li:eq(1)").addClass("open");
		
		var li = $("#sideMenu li");
		li.click(function(event){
			li.each(function(){
				$(this).removeClass("open");
			});
			$(this).addClass("open");
		});
		
	 });
	function queryGroup(){
		$.ajax({
			type:"post",
			url:"${path}/system/metaData/queryGroup.action",
			success:function(data){
				var list = JSON.parse(data);
				for(var i=0;i<list.length;i++){
					var listSon = list[i];
					var name = listSon.name;
					var id = listSon.id;
					
					//var li = $("<li class='row'><a class='col-md-5' style='padding-left:15px!important' onclick='showMetaDataType(\""+name+"\",\""+id+"\")'>"+name+"</a><a style='padding-left:10px!important' onclick='showDoiSubMeta(\""+id+"\")' class='col-md-2'><font color='#0000FF'>DOI</font> </a><sec:authorize url='/system/metaData/editMetaDataType.action'><a style='padding-left:10px!important' onclick='fileOrSourceEditData(\"1\",\""+id+"\")' class='col-md-2'><font color='#0000FF'>编辑</font></a></sec:authorize><sec:authorize url='/system/metaData/delSourceMetaDataType.action'><a style='padding-left:10px!important' onclick='fileOrSourceDeleteData(\"2\",\""+name+"\",\""+id+"\")' class='col-md-2'><font color='#0000FF'>删除</font> </a></sec:authorize><a style='padding-left:10px!important' onclick='sysDirList(\""+name+"\",\""+id+"\")' class='col-md-2'><font color='#0000FF'>11</font> </a></li>");
					var li = $("<li class='row' id='li_"+id+"'>"+
							"<a class='col-md-5' style='padding-left:15px!important'  onclick='showMetaDataType(\""+name+"\",\""+id+"\")'>"+name+"</a>"+
							"<div id='popmenu_box_"+id+"' style='width: 35px;height: 35x;margin-left: 30px;float:left; '>"+
						            "<div class='pop_ctrl'><i class='fa fa-bars' style='color: #f39921;'></i></div>"+
						            "<ul>"+
						                "<li><a onclick='showDoiSubMeta(\""+id+"\")'><div><i class='fa fa-calendar-o'></i></div><div>DOI</div></a></li>"+
										"<sec:authorize url='/system/metaData/editMetaDataType.action'>"+
						                "<li><a onclick='fileOrSourceEditData(\"1\",\""+id+"\")'><div><i class='fa fa-pencil'></i></div><div>编辑</div></a></li>"+
										"</sec:authorize>"+
										"<sec:authorize url='/system/metaData/delSourceMetaDataType.action'>"+
						                "<li><a onclick='fileOrSourceDeleteData(\"2\",\""+name+"\",\""+id+"\")'><div><i class='fa fa-trash-o'></i></div><div>删除</div></a></li>"+
										"</sec:authorize>"+
						                "<li><a onclick='sysDirList(\""+name+"\",\""+id+"\")'><div><i class='fa fa-folder-open'></i></div><div>资源目录</div></a></li>"+
						            "</ul>"+
						    "</div>"+
						"</li>");
					li.appendTo($("#metaManager"));
					$("#popmenu_box_"+id).popmenu({});
				}
			}
		});
	}
	

	function queryFileGroup(){
		$.ajax({
			type:"post",
			url:"${path}/system/metaData/queryFileGroup.action",
			success:function(data){
				var list = JSON.parse(data);
				for(var i=0;i<list.length;i++){
					var listSon = list[i];
					var name = listSon.name;
					var id = listSon.id;
					//var li = $("<li class='row'><a class='col-md-5' style='padding-left:15px!important' onclick='showFileMetaDataType(\""+name+"\",\""+id+"\")'>"+name+"</a><sec:authorize url='/system/metaData/editFileMetaDataType.action'><a style='padding-left:15px!important' onclick='fileOrSourceEditData(\"2\",\""+id+"\")' class='col-md-3'><font color='#0000FF'>编辑</font></a></sec:authorize ><sec:authorize url='/system/metaData/delFileMetaDataType.action'><a style='padding-left:10px!important' onclick='fileOrSourceDeleteData(\"3\",\""+name+"\",\""+id+"\")' class='col-md-2'><font color='#0000FF'>删除</font> </a></sec:authorize></li>");
					var li = $("<li class='row' id='li_"+id+"'>"+
							"<a class='col-md-5' style='padding-left:15px!important'  onclick='showFileMetaDataType(\""+name+"\",\""+id+"\")'>"+name+"</a>"+
							"<div id='popmenu_box_"+id+"' style='width: 35px;height: 35x;margin-left: 30px;float:left; '>"+
						            "<div class='pop_ctrl'><i class='fa fa-bars' style='color: #f39921;'></i></div>"+
						            "<ul>"+
										"<sec:authorize url='/system/metaData/editFileMetaDataType.action'>"+
						                "<li><a onclick='fileOrSourceEditData(\"2\",\""+id+"\")'><div><i class='fa fa-pencil'></i></div><div>编辑</div></a></li>"+
										"</sec:authorize>"+
										"<sec:authorize url='/system/metaData/delFileMetaDataType.action'>"+
						                "<li><a onclick='fileOrSourceDeleteData(\"3\",\""+name+"\",\""+id+"\")'><div><i class='fa fa-trash-o'></i></div><div>删除</div></a></li>"+
										"</sec:authorize>"+
						            "</ul>"+
						    "</div>"+
						"</li>");
					li.appendTo($("#fileManager"));
					$("#popmenu_box_"+id).popmenu({'width': '150px'});
				}
			}
		});
	}
	function showMetaDataType(name,id){
		//删除选中背景
		$("#metaManager li").removeClass('active');
		$("#fileManager li").removeClass('active');
		$("#sideMenu li:eq(1)").removeClass("open");
		name = encodeURI(encodeURI(name));
		$("#li_"+id).addClass("active");
		$('#work_main').attr('src',"${path}/system/metaData/detailMetaDataType.action?typeName="+name+"&id="+id);
	}
	
	function reback(name,id,flag){
		if(flag=="1") {
			$('#work_main').attr('src',"${path}/system/metaData/centerMetaData.jsp");
		}else if(flag=="2"){
			$('#work_main').attr('src',"${path}/system/metaData/detailMetaDataType.action?typeName="+name+"&id="+id);
		}else if(flag=="3"){
			$('#work_main').attr('src',"${path}/system/metaData/detailFileMetaDataType.action?typeName="+name+"&id="+id);
		}
	}
	
	function showFileMetaDataType(name,id){
		//删除选中背景
		$("#fileManager li").removeClass('active');
		$("#metaManager li").removeClass('active');
		$("#sideMenu li:eq(1)").removeClass("open");
		
		$("#li_"+id).addClass("active");
		$('#work_main').attr('src',"${path}/system/metaData/detailFileMetaDataType.action?typeName="+name+"&id="+id);
	}
	
	function showSourceAdd(event){
		$('#work_main').attr('src',"${path}/system/metaData/add.action");
		event.stopPropagation();
	}
	
	function fileOrSourceEditData(flag,id){
		if("2"==flag){
			$('#work_main').attr('src',"${path}/system/metaData/editFileMetaDataType.action?id="+id);
		}else if("1"==flag) {
			$('#work_main').attr('src',"${path}/system/metaData/editMetaDataType.action?id="+id);
		}
		
	}
	function sysDirList(name,id){
		//notice("数据展示","资源--name:"+name+"--id:"+id,"5");
		$('#work_main').attr('src',"${path}/system/sysDir/sysDirList.jsp?id="+id);
	}
	
	function fileOrSourceDeleteData(flagType,name,id){
		// 因为$.confirm 该方法在iframe.js中 不起作用   所以在apputil.js中重写了方法confirmNew（）
		confirmNew("确认删除 节点 -- " + name + " 吗？", function(){
		   $.ajax({
					type : "post",
					async : false,
					url : "${path}/system/metaData/delFileOrSourceMetaDataType.action?id="+id+"&typeName="+name+"&flagType="+flagType,
					success : function(data) {
						if("2"==data) {
							$("#metaManager").empty();
							queryGroup();
						}else if("3"==data) {
							$("#fileManager").empty();
							 queryFileGroup();
						}
						 returnMain();
					}
				}); 
	    });
	}
	function returnMain() {
		location.href="${path}/system/metaData/returnMain.action";
	}
	
	function showFileAdd(event){
		$('#work_main').attr('src',"${path}/system/metaData/addFile.action");
		event.stopPropagation();
	}
	function subMenu(event){
		$('#work_main').attr('src',"${path}/system/sysDoi/toUpd.action");
		event.stopPropagation();
	}
	function showDoiSubMeta(publishType){
		$('#work_main').attr('src',"${path}/system/sysDoi/toUpd.action?publishType="+publishType);
		event.stopPropagation();
	}
	function clickMetaData(event){
		//删除选中背景
		$("#fileManager li").removeClass('active');
		$("#metaManager li").removeClass('active');
		$("li").removeClass("open");
		
		$("#sideMenu li:eq(1)").addClass("open");
		$('#work_main').attr('src',"${path}/system/metaData/centerMetaData.jsp");
		event.stopPropagation();
	}
	</script>
</head>
<body style="overflow-x: hidden">
	<form id="queryForm" action="" target="work_main" method="post">
		<div class="by-main-page-body-side" id="byMainPageBodySide">
		    <div class="page-sidebar" style="height:600px;">
		        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
		            <i class="fa fa-angle-left"></i>
		        </div>
				<ul class="page-sidebar-menu"  id="sideMenu">
						<li class="by-item-first"></li>					
						<li class="active">
							<a onclick="clickMetaData(event);">
								<i class="fa fa-tasks"></i>
								<span class="title">通用元数据</span>
							</a>
						</li>
						
						<li class="active" >
							<a class="button">
								<i class="fa fa-tasks"></i>
								<span class="title">分类元数据</span>
									<sec:authorize url='/system/metaData/add.action'><span class="glyphicon glyphicon-plus" onclick="showSourceAdd(event)" style="flow:right;"></span></sec:authorize>
								<span class="arrow open"></span>
							</a>
							<ul class="sub-menu" id="metaManager">
							</ul>
						</li>
						
						<li class="active">
							<a class="button">
								<i class="fa fa-tasks"></i>
								<span class="title">文件元数据</span>
									<sec:authorize url='/system/metaData/addFile.action'><span class="glyphicon glyphicon-plus" onclick="showFileAdd(event)" style="flow:right;"></span></sec:authorize>
								<span class="arrow open"></span>
							</a>
							<ul class="sub-menu" id="fileManager">
							</ul>
						</li>
						
					</ul>
		    </div>
		</div>
		
	</form>
	<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="${path}/system/metaData/centerMetaData.jsp"></iframe>
	</div>
</body>
</html>