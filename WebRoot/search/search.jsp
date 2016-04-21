<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
	<head>
		<title>高级查询</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
		<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
		<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
		<script type="text/javascript" src="${path}/search/js/bootstrap-select.js"></script>
    	<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css"/>
		<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
		<script type="text/javascript" src="${path}/search/js/map.js"></script>
		<script type="text/javascript" src="${path}/bres/knowledgeTree.js"></script>
		<script type="text/javascript">
			$(function(){
				 $('.selectpicker').selectpicker({
		                'selectedText': 'cat'
		            });
				showTable();
				$("select[name=queryField]").change(function(){
					var fieldType = $("select[name=queryField] option:selected").attr("fieldType"); //获得选中查询字段的属性
					var currentEle = $(this); //获得当前选中查询字段的操作符
					createOptByType(currentEle, fieldType);
				});
				
				$("select[name=operator]").change(function(){
					var id = $(this).attr("id");
					//alert("changeid:" + id);
					var options = $("#"+id+" option:selected").val();
					var fileId = "queryField" + id.substring(id.length-2, id.length);
					var fileType = $("#"+fileId+" option:selected").attr("fieldType");
					//alert("options:"+options + "   fileId:"+fileType);
					var operate = $(this).attr("id");
					createValByType("00", fileType, options);
				});
	 		});
			
			function showTable(){
				var _divId = 'data_div';
		   		var _url = '${path}/search/listQueryHistory.action';
		   		var _pk = 'id';
		   		var _conditions = ['name','createTime'];
		   		var _sortName = 'id';
		   		var _sortOrder = 'desc';
				var _colums = [
	               	{field:'name',title:'名称',width:fillsize(0.2), align:'center' ,sortable:true},
					{field:'createTime',title:'创建时间',width:fillsize(0.2), align:'center' ,sortable:true},
					{field:'description',title:'描述',width:fillsize(0.4), align:'center' },
					{field:'opt1',title:'操作',width:fillsize(0.2),align:'center',formatter:$operate}
				];
		   		
				accountHeight();
		   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
			}
			
			
			$operate = function(value,rec){
	 			var opt = "";
	 		    var searchUrl= '<sec:authorize url="/search/searchList.action"><a class=\"btn hover-red\"  href="javascript:search('+rec.id+')"><i class=\"fa fa-sign-out\"></i>查询</a></sec:authorize> ';		
			    var editUrl= '<sec:authorize url="/search/editSearchCondition.action"><a  class=\"btn hover-red\" href="javascript:edit('+rec.id+')"><i class=\"fa fa-edit\"></i>修改</a></sec:authorize> ';	
			    opt = searchUrl + editUrl;
	 			return opt;
	 		};
	 		
	 		function search(){
	 			
	 		}
	 		
			function edit(id){
				if($("#queryCondition")!=null){
					$("#queryCondition").remove();
			    }
				var queryFrame = $("#queryFrame");
				var queryCondition = $("<div id='queryCondition' style='border: 1px solid;width:97%; height:180px; overflow-x:auto; margin-left:15px; float:left; display:none;'></div>");
				var table = $("<table></table>");
				for(var i=0;i<4;i++){
		   			var tr = $("<tr></tr>");
		   			for(var j=0;j<2;j++){
		   				if(j==0){
		   					var td = $("<td  style='float:left;width:50px;height:50px;border:0 solid #F00; padding-left:3px; font-family:微软雅黑; padding-top:10px; text-align:right;'>"+'标题： '+"</td>");
			   				td.appendTo(tr);
		   				}
		   				if(j==1){
		   					var td = $("<td style='float:right;width:240px;height:50px;border:0 solid #000; padding-left:3px; font-family:微软雅黑; padding-top:10px; text-align:left;'>"+'中华人民共和国简史 '+"</td>");
			   				td.appendTo(tr);
		   				}
		   			}
		   			tr.appendTo(table);
		   		}
		   		table.appendTo(queryCondition);
		   		queryCondition.appendTo(queryFrame);
		   		$("#queryCondition").css("display","block");
	 		}
			
			function saveConditionAndQuery(){
				var querySql = "";
				var _url = "${path}/search/saveQueryHistory.action?querySql=" + querySql;
				$.ajax({
					url: _url,
					type: 'get',
					success: function (result){
						return result;
					}
				});		
			}
						
			function resetCondition(){
				
			}
			
			function cancel(){
				
			}
			
		</script>
	</head>
	<body>
		<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: auto;">
			<div class="panel panel-default" style="height: auto;">
				<div class="by-tab">
					<div class="portlet">
						<div class="portlet-title">
	                       <div class="caption">隐藏查询条件
	                       	<a href="javascript:;" onclick="togglePortlet(this)"><i class="fa fa-angle-up"></i></a>
	                       </div>
                 		</div>
		                 		
                 		<div class="portlet-body" id="queryFrame">
							<div class="container-fluid" id="queryCondition">
								<!-- <div class="row" id="condition00">
									<div class="col-md-2" style="margin-right: 50px;">
										<div class="form-group">
											<label for="queryField" class="control-label col-md-4" >查询字段：</label>
											<div class="col-md-8">
												<input type="text" name="queryField" id="queryField" class="form-control" style="margin-left:10px; width:120px;" readonly onfocus="showMenuTwo();"/>
												<div id="menuContent" class="menuContent" style="display: none; width:130px; height:180px; overflow-x:auto;">
        											<ul id="fieldTree" class="ztree" ></ul>
  										  		</div>
											</div>
										    <label for="queryField" class="control-label col-md-4" style="margin-left:-16px; margin-right:10px; margin-top:8px;">查询字段：</label>
										    <select class="selectpicker bla bla bli queryField00" name="queryField" id="queryField00" data-live-search="true" style="border:solid 1px #ffcc00;">
										        <option fieldType="1"></option>
										        <option fieldType="1">One</option>
										        <option fieldType="2">Two</option>
										        <option fieldType="3">Three</option>
										        <option class="get-class" disabled fieldType="3">ox</option>
										        <optgroup label="test" data-subtext="another test" data-icon="icon-ok">
										            <option fieldType="4">Four</option>
										            <option fieldType="5">Five</option>
										            <option fieldType="6">Six</option>
										            <option fieldType="7">Seven</option>
										            <option fieldType="8">Eight</option>
										             <option fieldType="9">Nine</option>
										        </optgroup>
										    </select>
										</div>
									</div>
									<div class="col-md-2" style="margin-right: 50px;">
										<div class="form-group">
											<label for="operator" class="control-label col-md-4" style="margin-top:8px;">操作符：</label>
											<div class="col-md-8">
												<input type="text" name="operator" id="operator" class="form-control" />
												<select class="form-control" name="operator" style="width:120px;">
												    <option></option>
												    <option>等于</option>
												    <option>大于</option>
												    <option>小于</option>
												    <option>大于等于</option>
												    <option>小于等于</option>
											  	</select>
											</div>
											<label for="operator" class="control-label col-md-4" style="margin-top:8px;">操作符：</label>
										    <select class="selectpicker bla bla bli operator00" id="operator00" name="operator" style="border:solid 1px #ffcc00;">
										      	<option id="sel00"></option> 
											    <option id="equ00" value="equ">等于</option>
											    <option id="gt00" value="gt">大于</option>
											    <option id="lt00" value="lt">小于</option>
											    <option id="gte00" value="gte">大于等于</option>
											    <option id="lte00" value="lte">小于等于</option>
											    <option id="nequ00" value="nequ">不等于</option>
											    <option id="inMd00" value="inMd">in</option>
											    <option id="nin00" value="nin">not in</option>
											    <option id="betMd00" value="betMd">between</option>
											    <option id="likeMd00" value="likeMd">like</option> 
										    </select>
										    <div class="input-prepend" name="1">
	 											<div class="btn-group"  name="2">
												<button class="btn dropdown-toggle" type="button" id="operator" name="operator" data-toggle="dropdown" style="margin-left:28px;width:160px;height:35px;" >  
													<span class="caret" style="float:right;"></span>
												</button>
												<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1" style="margin-left:28px;">
											        <li role="presentation"><a role="menuitem" tabindex="0" href="#" value="01">Action</a></li>
											        <li role="presentation"><a role="menuitem" tabindex="1" href="#" value="02">Another action</a></li>
											        <li role="presentation"><a role="menuitem" tabindex="2" href="#" value="03">Something else here</a></li>
											        <li role="presentation" class="divider"></li>
											        <li role="presentation"><a role="menuitem" tabindex="3" href="#" value="04">Separated link</a></li>
										      	</ul>
										      	</div>
									      	</div>
										</div>
									</div>
									
									<div class="col-md-2" style="margin-right: 50px; width:350px">
										<div class="form-group" id="formGroupFieldValue00">
											<label for="fieldValue" class="control-label col-md-4" style="margin-top:8px;">字段值：</label>
											<div class="col-md-8">
												<input type="text" name="fieldValue" id="fieldValue" class="form-control" style="width:120px;"/>
											</div>
											<label for="fieldValue" class="control-label col-md-4" style="margin-top:8px;margin-right:-50px;">字段值：</label>
										    <div class="selVal" style="display:none;">
											    <select class="selectpicker bla bla bli" multiple name="fieldValue" id='fieldValue00' data-live-search="true" style="border:solid 1px #ffcc00;">
											        <option></option>
											        <option>bull</option>
											        <option class="get-class" disabled>ox</option>
											        <optgroup label="test" data-subtext="another test" data-icon="icon-ok">
											            <option>ASD</option>
											            <option>Bla</option>
											            <option>Ble</option>
											        </optgroup>
											    </select>
										    </div>
										    
										    <div class="inputVal" id="inputVal00" style="display:none;">
											   <input class="form-control" type="text" id="inputValue00" style="width:250px;margin-left:90px;"/>
										    </div>
										    
										    <div class="time" id="time00" style="display:none; margin-left:-100px;">
											    <div>
											   		<div class="col-sm-4 start" style="width:130px;float:left; display:none;">
														<input type="text" name="modifiedStartTime00" id="modifiedStartTime00"
															class="form-control Wdate" onselect="new Date()"
															onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime00\')}'})"
															onClick="WdatePicker({readOnly:true})"/>
													</div>
													onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',minDate:'#F{$dp.$D(\'modifiedStartTime\')}'})"
													<div class="col-sm-4 end" style="width:130px;float:left; display:none;">
														<input type="text" name="modifiedEndTime00" id="modifiedEndTime00"
															class="form-control Wdate" onselect="new Date()"
															onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'modifiedStartTime00\')}'})"
															onClick="WdatePicker({readOnly:true})"/>
													</div>
											    </div>
										    </div>
										    
										</div>
									</div>
									<div class="col-md-2" id="contactWord" style="margin-right: 120px;">
										<div class="form-group">
											<label for="contactWord" class="control-label col-md-4" style="margin-top:8px;">连接符：</label>
											<div class="col-md-8">
												<input type="text" name="contactWord" id="contactWord" class="form-control" />
												<select class="form-control" id="contactWord" style='width:120px;'>
													<option></option>
												    <option>并且</option>
												    <option>或者</option>
												    
											  	</select>
											</div>
										</div>
									</div>
									<div class="col-md-2" style="margin-right: 50px;">
										<div class="form-group">
										    <label for="contactWord" class="control-label col-md-4" style="margin-top:8px;">连接符：</label>
										    <select class="selectpicker bla bla bli" name="contactWord" data-live-search="false" style="border:solid 1px #ffcc00;font-weight: normal;font-family: inherit;font-size: 14px;">
									    	<select class="selectpicker bla bla bli contactWord00" name="contactWord00" id="contactWord00" data-live-search="false" style="border:solid 1px #ffcc00;font-weight: normal;font-family: inherit;font-size: 14px;">
										        <option value=""></option>
											    <option value="and">并且</option>
											    <option value="or">或者</option>
										    </select>
										</div>
									</div>
									<div>
										<button type="button" class="btn btn-primary" id="addCondtion"
										onclick="addQueryCondition();" style="margin-left:30px;">添加查询条件</button>
										&nbsp;&nbsp;
										<button type="button" class="btn btn-primary" id="delCondtion"
										onclick="delQueryCondition();" style="margin-left:10px;">删除</button>
									</div>
									
									<a class="btn hover-red" href="javascript:deleteQueryCondition('+rec.orderId+')"><i class="fa fa-trash-o"></i>删除</a>
								</div> -->
							</div>
							
							<br />
					
							<table width="98%" border="0" align="center" cellpadding="0" cellspacing="0" style="margin-left:16px;">
								<tr>
									<td>
										<div align="center">
											<input type="hidden" name="token" value="${token}" />
											<button type="button" class="btn btn-primary" id="addCondtion"
												onclick="addQueryCondition();" style="margin-left:30px;">添加查询条件</button>
											&nbsp;
											<button type="button" class="btn btn-primary"
												onclick="queryByCondition('');">查询</button>
											&nbsp;
											<button type="button" class="btn btn-primary" id="part"
												onclick="saveConditionAndQuery();">保存并查询</button>
											&nbsp;
											<button type="button" class="btn btn-primary" id="part"
												onclick="clearQueryCondition();">清空</button>
											&nbsp;
											<!-- <button type="button" class="btn btn-primary" id="part"
												onclick="cancel();">取消</button>
											&nbsp; --> 
											<button type="button" class="btn btn-primary" id="part"
												onclick="directCreateQueryCondition('','');">生成</button>
										</div> 
									</td>
								</tr>
							</table>
						</div>
					</div>
				</div>
				
				<div class="portlet" style="overflow-x:auto;">
					<div class="portlet-title">
						<div class="caption">查询历史条件</div>
					</div>
					<div class="panel-body" id="999" style="height:280px;overflow-x:auto;">
						<div id="data_div" class="data_div height_remain" ></div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>