<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<script type="text/javascript">
	var objectIds = "";
	var count = 0;
	function initRalations(){
		if(count==0){
			//只在初始化的时候执行一次
			$('#divWidth').val($('#relation_div').width());
			$('#relation_data_div').width($('#divWidth').val());
			$('#operate_history_div').width($('#divWidth').val());
		}else{
			$('#relation_data_div').width($('#divWidth').val());
		}
		count++;
		//定义一datagrid
   		var _divId = 'relation_data_div';
   		var _url = '${path}/publishRes/listRelationRes.action';
   		var _pk = 'objectId';
   		var _conditions = ['objectId'];
   		var _sortName = '';
   		var _sortOrder = 'desc';
		var _colums = [
						<app:QueryListColumnTag />
//						{field:'metadataMap.status',title:'状态',width:fillsize(0.10),align:'center'},
// 						{field:'objectId',title:'objectId',width:fillsize(0.10),align:'center',hidden : true},
						{field:'createTimeFormat',title:'创建日期',width:fillsize(0.10),align:'center'},
						{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}
						];
		
		accountHeight();
   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	}
	function initDerives(){ //衍生品
		$('#derive_data_div').width($('#divWidth').val());
		//定义一datagrid
	//	getStatusMap();
   		var _divId = 'derive_data_div';
   		var _url = '${path}/publishRes/listReriveRes.action';
   		var _pk = 'objectId';
   		var _conditions = ['objectId'];
   		var _sortName = '';
   		var _sortOrder = 'desc';
		var _colums = [
						<app:QueryListColumnTag/>
				//		{field:'metadataMap.status',title:'状态',width:fillsize(0.10),align:'center'},
						{field:'createTimeFormat',title:'创建日期',width:fillsize(0.10),align:'center'},
						{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate1,align:'center'}
						];
		accountHeight();
   		$grid1 = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	}
	function initBorder(){ //上位资源
		//定义一datagrid
	//	getStatusMap();
		$('#border_data_div').width($('#divWidth').val());
   		var _divId = 'border_data_div';
   		var _url = '${path}/publishRes/listBorderRes.action';
   		var _pk = 'objectId';
   		var _conditions = ['objectId'];
   		var _sortName = '';
   		var _sortOrder = 'desc';
		var _colums = [
						<app:QueryListColumnTag/>
				//		{field:'metadataMap.status',title:'状态',width:fillsize(0.10),align:'center'},
						{field:'createTimeFormat',title:'创建日期',width:fillsize(0.10),align:'center'},
						{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate2,align:'center'}
						];
		accountHeight();
   		$grid2 = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
	}
	$operate = function(value,rec){
		var opt = "";
		<c:if test="${detailFlag!='detail'}">
		opt += "<a class=\"btn hover-blue\" href=\"javascript:deleteRelations('"+rec.objectId+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a>";
		</c:if>
		opt += "<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
		return opt;
	};
	$operate1 = function(value,rec){
		var opt = "";
		<c:if test="${detailFlag!='detail'}">
		  opt += "<a class=\"btn hover-blue\" href=\"javascript:deleteDerives('"+rec.objectId+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a>";
		  </c:if>
		  opt += "<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
		return opt;
	};
	$operate2 = function(value,rec){
		var opt = "";
		<c:if test="${detailFlag!='detail'}">
		opt += "<a class=\"btn hover-blue\" href=\"javascript:deleteSources('"+rec.objectId+"')\" ><i class=\"fa fa-trash-o\"></i> 删除</a>";
		</c:if>
		opt += "<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>";
		return opt;
	};
	function chooseRes(){
		//先获取选择的资源rds如果有值则加上之前的session值，否则直接引用session值
		//$.openWindow("${path}/publishRes/chooseRes.jsp?objectIds="+$('#objectId').val()+"&queryType=2"+"&publishType="+$('#publishType').val()+"&divWidth=1150px",'添加相关资源',1200,550);
		parent.parent.layer.open({
		    type: 2,
		    title: '添加相关资源',
		    closeBtn: true,
		    shadeClose: true,
		    maxmin: true, //开启最大化最小化按钮
		    area: ['1200px', '550px'],
		    shift: 2,
		    content: "${path}/publishRes/chooseRes.jsp?objectIds="+$('#objectId').val()+"&queryType=2"+"&publishType="+$('#publishType').val()+"&divWidth=1100px"  //iframe的url，no代表不显示滚动条
		    
		});
	
	}
	function chooseReriveRes(){
		//$.openWindow("${path}/publishRes/chooseRes.jsp?objectIds="+$('#objectId').val()+"&queryType=3"+"&publishType="+$('#publishType').val()+"&reriveRes=1&divWidth=1150px",'添加衍生资源',1200,550);
		parent.parent.layer.open({
		    type: 2,
		    title: '添加衍生资源',
		    closeBtn: true,
		    shadeClose: true,
		    maxmin: true, //开启最大化最小化按钮
		    area: ['1200px', '550px'],
		    shift: 2,
		    content: "${path}/publishRes/chooseRes.jsp?objectIds="+$('#objectId').val()+"&queryType=3"+"&publishType="+$('#publishType').val()+"&reriveRes=1&divWidth=1100px"  //iframe的url，no代表不显示滚动条
		    
		});
	}
	function chooseSourceRes(){
		//$.openWindow("${path}/publishRes/chooseRes.jsp?objectIds="+$('#objectId').val()+"&queryType=4"+"&publishType="+$('#publishType').val()+"&reriveRes=2&divWidth=1150px",'添加来源资源',1200,550);
		parent.parent.layer.open({
		    type: 2,
		    title: '添加来源资源',
		    closeBtn: true,
		    shadeClose: true,
		    maxmin: true, //开启最大化最小化按钮
		    area: ['1200px', '550px'],
		    shift: 2,
		    content: "${path}/publishRes/chooseRes.jsp?objectIds="+$('#objectId').val()+"&queryType=4"+"&publishType="+$('#publishType').val()+"&reriveRes=2&divWidth=1100px" //iframe的url，no代表不显示滚动条
		    
		});
	}
	function deleteRelations(cid){
			$.confirm('确定要删除此文件吗？', function(){
				var objectId = $('#objectId').val();
				//先判断选择值的是否为空，不为空加上
				$.post('${path}/publishRes/delRelationRes.action',{id:objectId,relationIds:cid},function(data){
					if(data==0){
						$grid.query();
						$.alert("删除成功");
					}
				});
		 });
	}
	/***查看***/
	function detail(objectId){
		$.openWindow("${path}/publishRes/openDetail.action?objectId="+objectId+"&flagSta=1",'资源详细',1000,550);
	}
	function deleteDerives(cid){
		var objectId = $('#objectId').val();
		$.confirm('确定要删除此文件吗？', function(){
			$.post('${path}/publishRes/delDerivesRes.action',{id:objectId,derivesIds:cid},function(data){
				if(data==0){
					$grid1.query();
					$.alert("删除成功");
				}
			});
		});
	}
	function deleteSources(cid){
		var objectId = $('#objectId').val();
		$.confirm('确定要删除此文件吗？', function(){
			$.post('${path}/publishRes/delSourceRes.action',{id:objectId,sourceIds:cid},function(data){
				if(data==0){
					$grid2.query();
					$.alert("删除成功");
				}
			});
		});
	}
</script>
<input type="hidden" name="relationId" id="relationId"></input>
<input type="hidden" name="objectIds" id="objectIds"></input>
<input type="hidden" name="divWidth" id="divWidth"></input>
<div class="by-tab">
	<ul class="nav nav-tabs nav-justified" id="meta_tab">
		<li class="active"onclick="initRalations()"><a href="#tab_1_1_1" data-toggle="tab">
				相关资源 <c:if test="${detailFlag!='detail'}">
					<button type="button" class="fa fa-plus" onclick="chooseRes()"
						title="添加相关资源"></button>
				</c:if>
		</a></li>
		<li onclick="initDerives()"><a href="#tab_1_1_2" data-toggle="tab">
				衍生资源 <c:if test="${detailFlag!='detail'}">
					<button type="button" class="fa fa-plus" onclick="chooseReriveRes()"
						title="添加衍生资源"></button>
				</c:if>
		</a></li>
		<li onclick="initBorder()"><a href="#tab_1_1_3" data-toggle="tab">
		                      来源资源<c:if test="${detailFlag!='detail'}">
					<button type="button" class="fa fa-plus" onclick="chooseSourceRes()"
						title="添加来源资源"></button>
				</c:if>
		</a></li>
	</ul>
	<div class="portlet">
		<div class="panel-body">
			<div class="tab-content">
			    <c:if test="${flagSta=='1'}">
        		<div class="tab-pane active" id="tab_1_1_1" style="width:930px">
					<div class="portlet" id="relation_div">
						<div id="relation_data_div" class="data_div height_remain"></div>
					</div>
				</div>
        	   </c:if>
        	    <c:if test="${flagSta==null}">
        		<div class="tab-pane active" id="tab_1_1_1">
					<div class="portlet" id="relation_div">
						<div id="relation_data_div" class="data_div height_remain "></div>
					</div>
				</div>
        	   </c:if>
        	    <c:if test="${flagSta==3}">
        		<div class="tab-pane active" id="tab_1_1_1" style="width:930px">
					<div class="portlet" id="relation_div">
						<div id="relation_data_div" class="data_div height_remain "></div>
					</div>
				</div>
        	   </c:if>
        	   <c:if test="${flagSta==4}">
        		<div class="tab-pane active" id="tab_1_1_1" style="width:1200px">
					<div class="portlet" id="relation_div">
						<div id="relation_data_div" class="data_div height_remain "></div>
					</div>
				</div>
        	   </c:if>
				<c:if test="${flagSta!='2'}">
        		<div class="tab-pane active" id="tab_1_1_1" style="width:930px">
					<div class="portlet" id="relation_div">
						<div id="relation_data_div" class="data_div height_remain"></div>
					</div>
				</div>
        	   </c:if>
				<div class="tab-pane" id="tab_1_1_2">
					<div class="portlet">
						<div id="derive_data_div" class="data_div height_remain"></div>
					</div>
				</div>
				<div class="tab-pane" id="tab_1_1_3">
					<div class="portlet">
						<div id="border_data_div" class="data_div height_remain"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>