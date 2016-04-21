<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>铁路资源</title>
<script type="text/javascript">
	$(document.ready).ready(function(){
		//教材版本列表
		$.ajax({
			url:"${path}/railway/listRailway.action",
			async:false,
			success:function(data){
				var books = eval("("+data+")");
				var maxVersionNum = 0, tempNum = 0;
				for(var i=0;i<books.domains.length;i++){
					var book = books.domains[i];
					tempNum = parseInt(book.code.substring(1));
					if(maxVersionNum<tempNum)
						maxVersionNum = tempNum;
					var li = $("<li></li>");
					var href = $("<a><i class='fa fa-tasks'></i></a>");
					href.text(book.label);
					var version = book.objectId.substring(book.objectId.indexOf("#")+1,book.objectId.length);
					href.attr({
						"href":"${path}/railway/railwaySortList.jsp?version="+version,
						"target":"work_main"
					});
					href.appendTo(li);
					li.appendTo($("#book"));
				}
				$("#lastVersionNum").val(maxVersionNum);
				if(books.domains[0]){
					var firstBookId = books.domains[0].objectId;
					var version = "";
					if($("#bookVersion").val()!="null")
						version = $("#bookVersion").val();
					else
						version = firstBookId.substring(firstBookId.indexOf("#")+1,firstBookId.length);
					$("#work_main").attr("src","${path}/railway/railwaySortList.jsp?version="+version);
				}
			}
		});
	
		var subLi = $("#sideMenu li");
		subLi.click(function(event){
				subLi.each(function(){
					$(this).removeClass("open");
					if($(this).find(".sub-menu li")){
						var children = $(this).find(".sub-menu li");
						children.each(function(){
							$(this).removeClass("active");
						});
					}
				});
				$(this).addClass("open");
		});
	
		var li = $(".sub-menu li");
		li.click(function(event){
			li.each(function(){
				$(this).removeClass("active");
			});
		
			$(this).addClass("active");
		});

	
	});

	function addBook(evt){
		$("#createVersion").modal("show");
    	evt.stopPropagation();
	}

	function save(){
		if($("#newVersionName").val().length==0){
			$.alert("请输入教材版本名称");
			return;
		}
		var versionNum = $("#lastVersionNum").val();
		var code = "";
		if(versionNum>8)
			code =  "V" + (parseInt(versionNum)+1);
		else
			code =  "V0" + (parseInt(versionNum)+1);
		var railway = {
				"label" : $("#newVersionName").val(),
				"nodeId" :  _getRandomString(8),
				"nodeType" : 0,
				"pid" : "-1",
				"code" : code,
				"level" : 2
		};
		var railways = {
				"@type" : "domainList",
				"domainType" : 0
		};
		var domains = [];
		domains.push(railway);
		var domainStr = JSON.stringify(domains);
		railways["domains"] = domainStr;
		$.post("${path}/railway/addRail.action",railways,
			function(data) {
				$("#createVersion").modal("hide");
				$("#lastVersionNum").val(code);
				location.href="railwaySortMain.jsp";
		});
	}
</script>
</head>
<body>
<form id="queryForm" action="" target="work_main" method="post">
		<div class="by-main-page-body-side" id="byMainPageBodySide">
		    <div class="page-sidebar">
		        <div class="sidebar-toggler hidden-phone" id="sideBarToggleBtn" onclick="toggleSideBar(resizeFrameDt)">
		            <i class="fa fa-angle-left"></i>
		        </div>
				<div id="sideWrap">
					<ul class="page-sidebar-menu"  id="sideMenu">
						<li class="by-item-first"></li>					
						<li class="active">
							<a class="button">
								<i class="fa fa-tasks"></i>
								<span class="title">铁路资源</span>
								<sec:authorize url='/system/book/tbzy/getSelectValues.action'>
									<span class="glyphicon glyphicon-plus" 
			                        	onclick="addBook(event)" style="flow:right;"></span>
								</sec:authorize>
								<span class="arrow open"></span>
							</a>
							<ul class="sub-menu" id="book">
							</ul>
						</li>																					
					</ul>
				</div>
		    </div>
		</div>
		<div class="modal fade" id="createVersion" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		        <h4 class="modal-title" id="myModalLabel">添加分类名称</h4>
		      </div>
		      <div class="modal-body">
		      	<div class="form-group">
			        <div class="row">
			        	<label class="col-xs-offset-2 col-xs-2 control-label">分类名称</label>
			        	<div class="col-xs-4">
							<input type="text" id="newVersionName"  class="form-control text-input"/>
						</div>
			        </div>
			    </div>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" onclick="save();">保存</button>
		        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		      </div>
		    </div>
		  </div>
		</div>		
	</form>
<div class="by-main-page-body-center" id="byMainPageBodyCenter">
	    <iframe id="work_main" name="work_main" width="100%" height="100%" frameborder="0" src="railwaySortList.jsp"></iframe>
	</div>
		<input type="hidden" id="lastVersionNum" value=""/>
	<input type="hidden" id="bookVersion" value="<%=request.getParameter("bookVersion")%>" />
</body>
</html>