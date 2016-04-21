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
		
		#fakeFrame{
		  position: absolute;
		  width: 400px;
		  background-color: #E5E5E5;
		  border:1px solid #FFFFFF;
		}
		.by-main-page-body-center{
		  
		}
		.abc{
		background-attachment: scroll;
		background-clip:border-box;
		background-color: transparent;
		background-position-x:left;
		background-position-y:top;
		background-repeat: no-repeat;
		background-size:auto;
		float: right;
		font-size: 12px;
		height: 17px;
		width: 183px;    
		
		}
		.ab{
			background-attachment: scroll;
			background-image:url("/image/homeStartSearch.png"); 
			cursor: pointer;
			height: 14px;
			line-height: 15px;
			position: absolute;
			right: 5px;
			top: -1px;
			width: 14px;
		}
		.contain{
                position:relative;
                float: right; 
        }
        .search{
                background-image:url("image/homeStartSearch.png");
                position:absolute;
                width:30px;
                height:30px;
                top:6px;
                left:140px;
				background-repeat:  no-repeat;
        }
		
	</style>
	<script type="text/javascript">
	function save(){
		var data = $("#data_div").datagrid("getSelected");
		var name = data.name;
		var url = data.uuid;
		$('input').value = name;
		
		//wordType 1:表示是上位词	2:下位词	3:相关词
		var wordType = parent.$('#wordTypes').val();
   		var wordSize = parent.$('#wordSize').val();
   		var parentWin=  top.index_frame;
   		if(wordType == '1'){	//为上位词的name和url设置值
   			parentWin.$('#Sname'+wordSize).val(name);
   			parentWin.$('#Surl'+wordSize).val(url);
   		}else if(wordType == '2'){	//设置下位词的name和url的值
   			parentWin.$('#Xname'+wordSize).val(name);
   			parentWin.$('#Xurl'+wordSize).val(url);
   		}else if(wordType == '3'){	//设置相关词的name和url的值
   			parentWin.$('#Gname'+wordSize).val(name);
   			parentWin.$('#Gurl'+wordSize).val(url);
   		}
   		
		var myArgs = new Array("refresh",name,url);
		window.parent.returnValue = myArgs;
	}
	
	$(function(){
   		//定义一datagrid
   		var _divId = 'data_div';
   		var _url = '${path}/railway/list.action';
   		var _pk = 'id';
   		var _conditions = ['xpath','name1','page'];
   		var _sortName = 'id';
   		var _sortOrder = 'id';
   		var _check=false;
		var _colums = [	{field:'name',title:'',width:fillsize(0.4),align:'center',formatter:$opera},
		               	{field:'uuid',title:'',width:fillsize(0.4),align:'center',hidden:true}
						];
   		
		accountHeight();
   		$grid = $.datagridSimple(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
   		
	});
	/***操作列***/
	$opera = function(value,rec){
		var optArray = new Array();
		optArray.push("<a style=\"color:#4F5D73\" href=\"javascript:save()\" target=\"work_main\">"+rec.name+"</a>");
		return createOpt(optArray);
	};
	
	/***操作列***/
	/* $operate = function(value,rec){
		var optArray = new Array();
		optArray.push("<sec:authorize url='${path}/railway/goto.action'><a class=\"btn hover-red\" href=\"javascript:edit('"+rec.uuid+"')\"><i class=\"fa fa-edit\"></i></a>&nbsp;</sec:authorize>");	
		return createOpt(optArray);
	}; */
	function detail(uuid){
		$('#work_main').attr('src',"${path}/railway/gotoKnowledgePreDetail.action?uuid="+uuid);
	} 
	function edit(uuid){
		$.openWindow("${path}/railway/goto.action?name=1&uuid="+uuid, '详细', 1200, 500);
	}
	function query(){
		var namename=$("#name").val();
		 if(namename==null || namename==""){
			alert("请输入关键字");
			return;
		}else{
			namename = encodeURI(encodeURI(namename));
		}
		//this.location.href="${path}/railway/list.action?page=1&&namename="+namename; 
		this.location.href="${path}/railway/tanxiangxi.jsp?page=1&name1="+namename;
		//$('#work_main').attr('src',"${path}/railway/mainCenter.jsp?page=1&name1="+namename);
	} 
	
	
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 85%;width:80%;">
		<div class="panel panel-default" style="height: 100%;">
 			<div class="panel-heading" id="div_head_t">
 			<!-- <table border="1" width="200px;">
 				<tr>
 					<td style="width: 50%;">
 						<font style="font-size: 14px;margin-top: 0;color: #ffffff;">概念词列表</font>
 					</td>
 					<td>
 						<div class="container">
        					<input id="name"  name="name" type="text" style="margin-left: 130px;"/> 
        					<div class="search" onclick="query()"></div>
					</div>
 					</td>
 				</tr>
 			</table> -->
					<div>
					<span style="width: 20px;">
					<font style="font-size: 14px;color: #ffffff;">概念词列表</font></span>
					<!-- <input id="name" name="name" type="text"></input>&nbsp;&nbsp;&nbsp;&nbsp;<a href="###" class="btn btn-default red" onclick="query()"style="height: 50%">查询</a> -->
					<span class="contain">
        					<input id="name"  name="name" type="text"/> 
        					<div class="search" onclick="query()"></div>
					</span>
					</div>
			</div>
			<div class="panel-body height_remain" >
				<div id="data_div" class="data_div height_remain" style="width: 100%;"></div>
			</div>
		</div>
	</div>
	
		<input type="hidden" id="xpath" name="xpath" value="${param.xpath }" />
		<input type="hidden" id="name1" name="name1" value="${param.name1 }" />
		<input type="hidden" id="page" name="page" value="${param.page }"/>
</body>
</html>