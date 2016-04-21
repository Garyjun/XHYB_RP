<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<!-- 
***###
 -->
<html>
    <head>
    <title>编辑</title>
        <link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
        <script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.min.js"></script>  
		<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
		<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css" />
		<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
		<script type="text/javascript" src="${path}/resRelease/resOrder/js/map.js"></script>
		<script type="text/javascript" src="${path}/resRelease/resOrder/js/list.js"></script>
		<script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
		<script type="text/javascript" src="${path}/search/js/bootstrap-select.js"></script>
		<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
		<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
          <script type="text/javascript">
          var fieldId = "";
			function edit(act){
				document.form1.action=act; 
				jQuery('#form1').submit();
			}
			
 			jQuery(document).ready(function(){
				var groupName = '${groupName}';
				var groupValue = '${groupValue}';
				$('#orgName').val(groupName);
				$('#orgId').val(groupValue);
 				$('.selectpicker').selectpicker({
	                'selectedText': 'cat'
	            });
 				addCoreMetaData();//添加字段权限-》元数据选项
 				//addQueryCondition();
				$('#form1').validationEngine('attach', {
					relative: true,
					overflownDIV:"#divOverflown",
					promptPosition:"topRight",//验证提示信息的位置，可设置为："topRight", "bottomLeft", "centerRight", "bottomRight"
					maxErrorsPerField:1,
					onValidationComplete:function(form,status){
						if(status){
							save();
						}
					}
				});
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
				//部门树
				var orgStr='${orgJson}';
				var orgNodes = jQuery.parseJSON(orgStr);
				setOrgCheckedNodes(orgNodes);
				var orgTreeSetting = {
						check: {
							enable: true
						},
						data: {
							simpleData: {
								enable: true,
								idKey: "id",
								pIdKey: "pid",
								rootPId: -1
							}
						}
				};
				var tree = $.fn.zTree.init($("#organizationTree"), orgTreeSetting,orgNodes);
				var root = tree.getNodes()[0];
				if(root){
					tree.expandNode(root,true,false,true);
				}
				
				//选择资源库
				$("input[name='resTypeIds']").click(function(){
					if(this.checked){
						addDivToDataPri($(this).parent().text(),$(this).val());
						addQueryConditionDiv($(this).val(),$(this).parent().text());
						//根据所选择的资源库加载资源库下的资源目录
						addDivResourcesTree($(this).parent().text(),$(this).val());
					}else{
						removeDivFromDataPri($(this).val());
						removeQueryCondition($(this).val());
						//删除勾选掉对勾的资源库展示的资源目录的div
						removeResDataDiv($(this).val());
					}
				});
				
				$("input[name='resTypeIds']").each(function(){
					if(this.checked){
						addDivToDataPri($(this).parent().text(),$(this).val());
						addQueryConditionDiv($(this).val(),$(this).parent().text());
						//根据所选择的资源库加载资源库下的资源目录
						addDivResourcesTree($(this).parent().text(),$(this).val());
					}
				});
				
			}); 
 			
 			function addCoreMetaData(){
 				//核心元数据
 				var coreDiv = $("<div class='form-group' name='checkboxGroup'></div>");
 				coreDiv.appendTo($("#tab_1_1_2"));
 				var coreName = $("<label for='email' class='col-sm-2 control-label'>核心元数据：</label>"); 				
 				coreName.appendTo(coreDiv);
 				var coreCheckboxDiv = $("<div class='col-sm-10'></div>");
 				coreCheckboxDiv.appendTo(coreDiv);
 				var coreMetaDataArray = ${coreMetadata}; 
 				$.each(coreMetaDataArray,function(index,coreMetadata){
 					var checkboxLabel = $("<label class='checkbox-inline'></label>");
 					checkboxLabel.appendTo(coreCheckboxDiv);
 					checkboxLabel.text(coreMetadata.name); 	
 					
 					var checkbox = $("<input type='checkbox' name='group_1'/>");
 					checkbox.attr("value",coreMetadata.enName);
 					if(coreMetadata.checked){
 						checkbox.attr("checked",true);
 					}
 					checkbox.appendTo(checkboxLabel);
 					
 					var rowsTo = $("<br/>");
 					if(index!=0 && index%5==0){
 						rowsTo.appendTo(coreCheckboxDiv);
 					}
 					//全选
 					if(index == coreMetaDataArray.length-1){
 						var allCheckLabel = $("<label class='checkbox-inline'></label>");
 						allCheckLabel.appendTo(coreCheckboxDiv);
 						allCheckLabel.text("全选"); 						
 						var allCheck = $("<input type='checkbox' id='allCheck_1'/>");
 						allCheck.appendTo(allCheckLabel);
 						allCheck.click(function(){
 							if(this.checked){
	 							$("input[name='group_1']").attr("checked",true);
 							}else{
 								$("input[name='group_1']").attr("checked",false);
 							}
 						});
 					}
 				});
 				var coreHr = $("<hr/>");
 				coreHr.appendTo($("#tab_1_1_2")); 
 				
 				//选中显示的所有元数据和核心元数据
 				//var allCheckLabelAll = $("<label class='btn btn-primary'></label>");
				//allCheckLabelAll.appendTo($("#zhuyi"));
				//allCheckLabelAll.text("全选"); 	
				var allCheckAll = $("<input type='button' id='allCheck_1All' class='btn btn-primary' value='全选' />");
				var allDisablekAll = $("<input type='button' id='allCheck_2All' class='btn btn-primary' value='清空' />");
				//allCheckAll.appendTo(allCheckLabelAll);
				var splits = $("<a>&nbsp;&nbsp;</a>");
				allCheckAll.appendTo($("#zhuyi"));
				splits.appendTo($("#zhuyi"));
				allDisablekAll.appendTo($("#zhuyi"));
				allCheckAll.click(function(){
					$("input[name^='group_']").attr("checked",true);
				});
				allDisablekAll.click(function(){
					$("input[name^='group_']").attr("checked",false);
				});
 			};
 			
 			
 			//根据所选择的资源类型，加载相对应的资源目录
 			function addDivResourcesTree(name,value){
 				var outerDiv = $("<div id='" + value + "_divRes" + "' ></div>");
 				outerDiv.appendTo($("#tab_1_1_5"));
 				//资源库名
 				var title = $("<label for='email' class='col-sm-2 control-label' style='font-size:15px;'>"+name + "：</label>");
 				title.appendTo(outerDiv);
 				var hr = $("<hr/>");
 				hr.appendTo(outerDiv);
 				
 				
 				$.get("${path}/user/getResourcesDirectory.action?typeId="+value,function(data){
 						var group = jQuery.parseJSON(data);
		 				//组名
		 				var div = $("<div class='form-group' name='checkboxGroupRes'></div>");
		 				div.appendTo(outerDiv);
		 				//字段
		 				var checkboxDiv = $("<div class='col-sm-9'></div>");
		 				checkboxDiv.appendTo(div);
		 				
		 				//已选择的id
		 				var ids = "";
		 				var dataArray = [];
		 				if($("#resourceDataJson").val()){
		 					dataArray = jQuery.parseJSON($("#resourceDataJson").val());
		 				}
		 				
		 				 $.each(dataArray,function(i,data){
		 					if(data.id == value){
		 						ids = data.field;
		 					}
		 				}); 
		 				
		 				if(group != "" && group != null){
		 					//回调函数拥有两个参数：第一个为对象的成员或数组的索引，第二个为对应变量或内容
			 				$.each(group,function(index,filed){
			 					var checkboxLabel = $("<label class='checkbox-inline'></label>");
			 					checkboxLabel.appendTo(checkboxDiv);
			 					checkboxLabel.text(filed.resType);
			 					
			 					var checkbox = $("<input type='checkbox' name='" + "Resource_"+value + "'/>");
			 					checkbox.attr("value",filed.resType);
			 					//比较该资源下数据库中存放的资源目录，和根据资源库id查询出的所有资源目录
			 					//相同的证明以选中显示在页面上
			 					 if(ids.indexOf(filed.resType)!=-1){
			 						checkbox.attr("checked",true);
			 					} 
			 					checkbox.appendTo(checkboxLabel);
			 					
			 					var br = $("<br />");
			 					if(index!=0&&index%5==0){
			 						br.appendTo(checkboxDiv);
			 					}
			 					 if(index == group.length-1){
			 						br.appendTo(checkboxDiv);
			 						//全选
			 						var allCheckLabel = $("<label class='checkbox-inline'></label>");
			 						allCheckLabel.appendTo(checkboxDiv);
			 						allCheckLabel.text("全选"); 						
			 						var allCheck = $("<input type='checkbox' id='allCheck_"+ value +"'/>");
			 						allCheck.appendTo(allCheckLabel);
			 						allCheck.click(function(){
			 							if(this.checked){
				 							$("input[name='Resource_"+ value +"']").attr("checked",true);
			 							}else{
			 								$("input[name='Resource_"+ value +"']").attr("checked",false);
			 							}
			 						});		 						
			 					} 
			 				});
 				   		}
 					});
 				}
 			
 			
 			
 			
 			
 			//向字段权限中根据所选中的资源库添加对应的元数据
 			function addDivToDataPri(name,value){
 				var outerDiv = $("<div id='" + value + "_div" + "' ></div>");
 				//资源库对应的元数据名
 				outerDiv.appendTo($("#tab_1_1_2"));

 				
 				//资源库名
 				var title = $("<h4>" + name + "</h4>");
 				title.appendTo(outerDiv);
 				var hr = $("<hr/>");
 				hr.appendTo(outerDiv);
 				
 				$.get("${path}/user/getDataPrivilge.action?typeId="+value,function(data){
 					var group = jQuery.parseJSON(data);
 					$.each(group,function(n,node){
		 				//组名
		 				var div = $("<div class='form-group' name='checkboxGroup'></div>");
		 				div.appendTo(outerDiv);
		 				//字段
		 				var groupName = $("<label for='email' class='col-sm-2 control-label'>"
		 								+ node.name + "：</label>");
		 				groupName.appendTo(div);
		 				
		 				var checkboxDiv = $("<div class='col-sm-10'></div>");
		 				checkboxDiv.appendTo(div);
		 				
		 				//已选择的id
		 				var ids = "";
		 				var dataArray = [];
		 				if($("#dataPreJson").val()){
		 					dataArray = jQuery.parseJSON($("#dataPreJson").val());
		 				}
		 				
		 				//遍历已经选中的资源库对应的元数据id,将选中的元数据的中英文名名称记录在ids中
		 				$.each(dataArray,function(i,data){
		 					if(data.id == node.id){
		 						ids = data.field;
		 					}
		 				});
		 				
		 				var fileds = node.field;
		 				$.each(fileds,function(index,filed){
		 					var checkboxLabel = $("<label class='checkbox-inline'></label>");
		 					checkboxLabel.appendTo(checkboxDiv);
		 					checkboxLabel.text(filed.name);
		 					
		 					var checkbox = $("<input type='checkbox' name='" + "group_"+node.id + "'/>");
		 					checkbox.attr("value",filed.enName);
		 					if(ids.indexOf(filed.enName)!=-1){
		 						checkbox.attr("checked",true);
		 					}
		 					checkbox.appendTo(checkboxLabel);
		 					
		 					var br = $("<br />");
		 					if(index!=0&&index%5==0){
		 						br.appendTo(checkboxDiv);
		 					}
		 					if(index == fileds.length-1){
		 						br.appendTo(checkboxDiv);
		 						//全选
		 						var allCheckLabel = $("<label class='checkbox-inline'></label>");
		 						allCheckLabel.appendTo(checkboxDiv);
		 						allCheckLabel.text("全选"); 						
		 						var allCheck = $("<input type='checkbox' id='allCheck_'"+ node.id +"/>");
		 						allCheck.appendTo(allCheckLabel);
		 						allCheck.click(function(){
		 							if(this.checked){
			 							$("input[name='group_"+ node.id +"']").attr("checked",true);
		 							}else{
		 								$("input[name='group_"+ node.id +"']").attr("checked",false);
		 							}
		 						});		 						
		 					}
		 				});
 					});
 				});
 			}
			
 			function removeDivFromDataPri(id){
 				var divId = "#" + id + "_div";
 				$(divId).remove();
 			}
 			
 			//删除勾选掉对勾的资源库展示的资源目录的div
 			function removeResDataDiv(id){
 				var divId = "#"+ id + "_divRes";
 				$(divId).remove();
 			}
 			
			function save(){
				$.get("${path}/user/checkLoginName.action?fieldValue="
						+$("#loginName").val(),function(data){
					if(data=="-1"){
						$.alert("该用户名已存在！");
						return;
					}
					setResCodes();
					setDataPreJson();
					setDataPreRangeArray();
					setResDataJson();
					$.blockUI({ message: '<h5>正在保存...</h5>' });
					$('#form1').ajaxSubmit({
		 				method: 'post',//方式
		 				success:(function(response){
		 					callback(response);
		           		})
		 			});
				});
			}
			function callback(data){
/* 				top.index_frame.freshDataTable('data_div');
				$.closeFromInner(); */
				//location.href = "${path}/system/user/userMain.jsp";
				history.go(-1);
			}
			
			/* function returnList(){
				location.href = "${path}/system/user/userMain.jsp";
			} */
			
			function setOrgCheckedNodes(orgNodes){
				var resCodes = $("#organization").val();
				if(resCodes){
					var checkedNodes = resCodes.split(",");
					$.each(orgNodes,function(n,node){
						if($.inArray(node.id+"",checkedNodes)!=-1)
							node.checked = true;
						else
							node.checked = false;
					});
				}
			}
			
			function setResCodes(){
				var treeObj = $.fn.zTree.getZTreeObj("organizationTree");
				var nodes = treeObj.getCheckedNodes(true);
				var codes = "";
				$.each(nodes,function(n,node){
					codes += node.id + ",";
				});
				$("#organization").val(codes);
			}
			
			function setDataPreJson(){
				var dataJson = [];
				var array = $("div[name='checkboxGroup']");
				$.each(array,function(index,group){
					var json = {};
					var group = $(this).find("input[type='checkbox']");
					if(group.length>0){
						//组id
						json.id = group[0].name.substring(6);
						json.field = "";
						$.each(group,function(groupIndex,field){
							if(this.checked==true){
								json.field += $(this).val() + ",";
							}
						});
						if(json.field)
							dataJson.push(json);
					}
				});
				$("#dataPreJson").val(JSON.stringify(dataJson));
			}
			
			
			//设置用户被授权的资源目录
			function setResDataJson(){
				var dataJson = [];
				//选中所有的选择的资源类型展示的资源目录的组别
				var array = $("div[name='checkboxGroupRes']");
				$.each(array,function(index,group){
					var json = {};
					//循环每个组时，选中每组下的文件目录
					var group = $(this).find("input[type='checkbox']");
					if(group.length>0){
						//组id,组名称组成Resource_组id
						json.id = group[0].name.substring(9);
						json.field = "";
						//循环每种资源下的资源目录，获取被选中的项
						$.each(group,function(groupIndex,field){
							if(this.checked==true){
								json.field += $(this).val() + ",";
							}
						});
						if(json.field){
							json.field = json.field.replace(",on","");
							dataJson.push(json);
						}
					}
				});
				$("#resourceDataJson").val(JSON.stringify(dataJson));
			}
			
			function setDataPreRangeArray(){
// 				alert(dataValue);
				var sql = getAllQueryConditionsByResType();
				if(sql&&sql.length>0){
					$("#dataPreRangeArray").val(sql);
				};
			}
			
			//向 字段属性权限 中添加查询条件
			function addQueryConditionDiv(id,name){
// 				var resTypeIds = "";
// 				获得泛型id
// 				var checkedData = $("input[id^=resTypeIds]");
// 				for(var i=0;i<checkedData.length;i++){
// 					if(checkedData[i].checked==true){
// 						resTypeIds = dataValue+checkedData[i].value+",";	
// 					}
// 				}
				var dataPreRangeArray = '${dataPreRangeArray}';
				//alert(dataPreRangeArray);
				showPrivilegeConditionForEdit(id, dataPreRangeArray);
				fieldId = fieldId+id+",";
				var div = $("#queryFrame");
				var conditionDiv = $("<div id='queryCondition"+ id +"'></div>");
				conditionDiv.appendTo(div);
				var form = $("<form id='myForm"+id +"'> </form>");
 				var title = $("<h4>" + name + "</h4>");
 				title.appendTo(conditionDiv);
 				var hr = $("<hr/>");
 				hr.appendTo(conditionDiv);
 				form.appendTo(conditionDiv);
 				var div1 = $("<table width='98%' border='0' align='center' cellpadding='0' cellspacing='0' style='margin-left:16px;'> <tr><td><div align='center'> <input type='hidden' name='token' value='${token}'/> <button type='button' class='btn btn-primary' id='addCondtion"+ id +"'' onclick='addQueryConditionByResType("+ id +");' style='margin-left:30px;'>添加查询条件</button> </div></td></tr></table>");
 				div1.appendTo(form);
 				
 				
			}
			
			function removeQueryCondition(id){
				var id = "queryCondition"+id;
				$("#"+id).remove();
			}
			
			function chooseGroup(){
				var orgJson='${orgJson}';
				orgJson =  encodeURI(encodeURI(orgJson));
				$.openWindow("${path}/system/user/chooseGroup.jsp?orgStr="+orgJson,'所属部门',300,400);
				
			}
			function clearUnit(){
				$('#orgName').val("");
			}
			//添加时重置
			function resetForm(){
				
				$('input[name="resTypeIds"]:checked').each(function(){ 
					//删除字段权限中动态生成的生成的元数据项
					$("#"+$(this).val()+"_div").remove();
					//删除资源目录中动态生成的生成的资源对应的目录
					$("#"+$(this).val()+"_divRes").remove();
				}); 
				
				//重置核心元数据选中的项，由于jQuery('#form1')[0].reset();对于核心元数据重置时不生效
				$("input[name='group_1']").attr("checked",false);
				
				jQuery('#form1')[0].reset();
				var treeObj = $.fn.zTree.getZTreeObj("organizationTree");
				nodes = treeObj.getCheckedNodes(true);
				for (var i=0, l=nodes.length; i < l; i++) {
					treeObj.checkNode(nodes[i], false, true);
				}
				var del = [];
				del = $("div[id^='condition']");
				for(var i= 0;i<del.length;i++){
					deleteQueryCondition('condition0'+(i+1));
				}
			}
			
			//修改时重置
			function resetForm1(name){
				jQuery('#form1')[0].reset();
				var groupName = name;
				$('#orgName').val(groupName);
				var treeObj = $.fn.zTree.getZTreeObj("organizationTree");
				nodes = treeObj.getCheckedNodes(true);
				for (var i=0, l=nodes.length; i < l; i++) {
					treeObj.checkNode(nodes[i], true, true);
				} 
			}
		</script>

    </head>
     <body>
        <div id="fakeFrame" class="container-fluid by-frame-wrap">
		<div class="panel panel-default">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            	系统管理
			        </li>
			        <li>
			            	用户管理
			        </li>
			        <li>
			         <c:if test="${id>-1}">
			         	用户编辑
			         </c:if>
			          <c:if test="${id==-1}">
			         	用户添加
			         </c:if> 	
			        </li>
				</ul>
			</div>
			<p><small><font color="red">*注意：如果修改了【角色权限】和【用户组权限】系统重新登录之后才生效。</font></small></p>
      	<div class="form-wrap">
	      		<form:form action=""  name="form1" id="form1" method="POST"  class="form-horizontal"  role="form">
		      		<form:hidden path="id"  />
		      		<form:hidden path="password"  />
		      		
		      		<div class="form-group">
						<label for="loginName" class="col-sm-3 control-label text-right"><i class="must">*</i>登录名：</label>
						<div class="col-sm-6">
								  <c:if test="${id==-1}">
				                         <form:input  path="loginName" id="loginName"  class="form-control validate[required,maxSize[20],custom[onlyLetterNumber]] text-input" /> 
				                  </c:if>
				                  <c:if test="${id>-1}">
				                       <input type="text" name="loginName" readonly value="${command.loginName}" class="form-control text-input"/>
				                      
				                  </c:if>
						</div>
					</div>
					
					<div class="form-group">
						<label for="userName" class="col-sm-3 control-label text-right"><i class="must">*</i>姓名：</label>
						<div class="col-sm-6">
							 <form:input  path="userName" id="userName"  class="form-control validate[required,maxSize[20]] text-input" />
						</div>
					</div>
					
					<div class="form-group">
						<label for="userName" class="col-sm-3 control-label text-right"><i class="must">*</i>所属部门：</label>
						<div class="col-sm-6 input-group">
						<span class="col-sm-6 input-group-btn">
						<input type="hidden" name="orgId" id="orgId"/>
						<input type="text" class="col-sm-10 form-control validate[required]" id="orgName" name="orgName" readonly="readonly"/>
							<a id="menuBtn" onclick="chooseGroup();return false;" href="###" class="btn btn-primary">选择</a>
							<a onclick="clearUnit();return false;" href="###" class="btn btn-primary" style="margin-left: 3px;">清除</a>
						</span>
						</div>
					</div>
					
					<div class="form-group">
						<label for="status" class="col-sm-3 control-label text-right"><i class="must">*</i>状态：</label>
						<div class="col-sm-6">
							 <c:if test="${command.loginName eq 'admin'}">
			                 	启用
			                 	<input type="hidden" name="status" id="status" value="1"/>
			                 </c:if>
			                 <c:if test="${command.loginName ne 'admin'}">
			                 <input type="radio" name="status" id="status" value="1" checked="checked"/>启用
			                 <input type="radio" name="status" id="status" value="0"/>禁用
<%-- 			                	 <form:radiobutton path="status" value="1" checked="true"/>启用 --%>
<%-- 							     <form:radiobutton path="status" value="0" checked="false"/>禁用 --%>
			                 </c:if>
						</div>
					</div>					
					
					<div class="form-group">
						<label for="phone" class="col-sm-3 control-label text-right">电话：</label>
						<div class="col-sm-6">
							 <form:input  path="phone" id="phone"  class="form-control validate[custom[phone]] text-input" />
						</div>
					</div>
					<div class="form-group">
						<label for="mobile" class="col-sm-3 control-label text-right">手机：</label>
						<div class="col-sm-6">
							 <form:input  path="mobile" id="mobile"  class="form-control validate[custom[mobile]] text-input" />
						</div>
					</div>
					
					<div class="form-group">
						<label for="email" class="col-sm-3 control-label text-right">email：</label>
						<div class="col-sm-6">
							 <form:input  path="email" id="email"  class="form-control validate[custom[email]] text-input" />
						</div>
					</div>
					
				<%-- 	 <div class="form-group">
		                <label for="roleIds" class="col-sm-3 control-label text-right" >分配角色：</label>
		                 <div class="col-sm-9">
		                      <c:forEach items="${roles}" var="role" varStatus="status">
		                      	<c:if test="${role.status==1 }">
						                    <label class="checkbox-inline">
				  								<form:checkbox path="roleIds" value="${role.id}" /> ${role.roleName}
											</label>
									 <c:if test="${status.index !=0 && status.index % 7 == 0}">
		                               <br/>
		                            </c:if>
		                            <c:if test="${status.isLast()}">
		                            	<br/>
		                            </c:if>
		                        </c:if>
							 </c:forEach>
		                 </div>
			      </div>
			      <div class="form-group">
			      	<label for="roleIds" class="col-sm-3 control-label text-right" >分配用户组：</label>
			      	<div class="col-sm-9">
			      		<c:forEach items="${groups}" var="group" varStatus="status">
							<label class="checkbox-inline">
								<form:checkbox path="groupIds" value="${group.id}" /><p class="form-control-static"
										style="white-space:nowrap; font-size:14px;width:70px;overflow:hidden;margin-top:-5px;text-overflow:ellipsis;" title="${group.name}">${group.name}</p>
							</label>
							<c:if test="${status.index !=0 && status.index % 7 == 0}">
								<br/>
							</c:if>
							<c:if test="${status.isLast()}">
								<br/>
							</c:if>										      		
			      		</c:forEach>
			      	</div>
			      </div> --%>
			      <div class="form-group">
			      	<label for="privilegeTree" class="col-sm-3 control-label text-right" >权限设置</label>
			      	<div class="by-tab col-sm-7">
			      		<ul class="nav nav-tabs" style="width: 520px;">
			      			<li class="active" style="width: 125px;"><a href="#tab_1_1_4" data-toggle="tab">角色权限</a></li>
			      			<li style="width: 125px;"><a href="#tab_1_1_1" data-toggle="tab">数据权限</a></li>
			      			<li style="width: 125px;"><a href="#tab_1_1_2" data-toggle="tab">字段权限</a></li>
			      			<!-- <li><a href="#tab_1_1_3" data-toggle="tab">字段属性权限</a></li> -->
			      			<li style="width: 125px;"><a href="#tab_1_1_5" data-toggle="tab">资源目录权限</a></li>
			      		</ul>
			      	</div>
			      </div>
			      <div class="form-group">
			      	<label for="privilegeTree" class="col-sm-3 control-label text-right" ></label>
			      	<div class="tab-content col-sm-7">
			      		<div class="tab-pane" id="tab_1_1_1" >
			      		<p><small><font color="red">*注意：<br>按组织部门授权：该用户能看到已授权的组织部门下的所有人上传的资源。<br>按个人用户授权：该用户能看到自己上传的资源。<br>同时授权：则该用户能看到已授权的组织部门下的所有人上传的资源和自己上传的资源。<br>都不授权：该用户看不到任何资源。<br>资源库授权：该用户能看到已授权的资源库，否则无权限。</font></small></p>
			      			<div class="form-group">
								<label for="roleIds" class="col-sm-3 control-label text-right" >组织部门：</label>
								<div class="col-sm-9">
									<div class="zTreeBackground" >
										<ul id="organizationTree"  class="ztree"></ul>
									</div>
								</div>
			      			</div>
			      			<div class="form-group">
								<label class="col-sm-3 control-label text-right" >个人用户授权：</label>
								<div class="col-sm-9">
									<form:radiobutton path="isPrivate" value="1"/>是
									<form:radiobutton path="isPrivate" value="0"/>否
								</div>							
							</div>	
					      <div class="form-group">
					      	<label for="roleIds" class="col-sm-3 control-label text-right" >资源库：</label>
					      	<div class="col-sm-9">
					      		<c:forEach items="${resType}" var="type" varStatus="status">
					      			<label class="checkbox-inline">
					      				<form:checkbox path="resTypeIds" name="resTypeIds" value="${type.id}" /> ${type.typeName}
					      			</label>
									<c:if test="${status.index !=0 && status.index % 7 == 0}">
										<br/>
									</c:if>
									<c:if test="${status.isLast()}">
										<br/>
									</c:if>
					      		</c:forEach>
					      	</div>
					      </div>
						</div>
						<div class="tab-pane" id="tab_1_1_2">
							<p id="zhuyi"><small><font color="red">*注意：不勾选元数据项，则无元数据权限。&nbsp;&nbsp;&nbsp;&nbsp;
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font></small></p>
							<br/>
						</div>
						<%-- <div class="tab-pane" id="tab_1_1_3">
 							<div class="portlet-body" id="queryFrame">
							<div id="queryConditioncommon">
								<h4>核心元数据</h4>
								<hr>
								<table width="98%" border="0" align="center" cellpadding="0" cellspacing="0" style="margin-left:16px;">
									<tr>
										<td>
											<div align="center">
												<input type="hidden" name="token" value="${token}" />
												<button type="button" class="btn btn-primary" id="addCondtioncommon"
													onclick="addQueryConditionByResType('common');" style="margin-left:30px;">添加查询条件</button>
											</div> 
										</td>
									</tr>
								</table>
							</div>
						</div>						
			      	</div>
			      	 --%>
			      	<div class="tab-pane active" id="tab_1_1_4">
				      	<div class="form-group">
			                <label for="roleIds" class="col-sm-1 control-label text-left" >分配角色：</label>
			                 <div class="col-sm-9">
			                      <c:forEach items="${roles}" var="role" varStatus="status">
			                      	<c:if test="${role.status==1 }">
							                    <label class="checkbox-inline">
					  								<form:checkbox path="roleIds" value="${role.id}" /> ${role.roleName}
												</label>
										 <c:if test="${status.index !=0 && status.index % 7 == 0}">
			                               <br/>
			                            </c:if>
			                            <c:if test="${status.isLast()}">
			                            	<br/>
			                            </c:if>
			                        </c:if>
								 </c:forEach>
			                 </div>
				      </div>
				      <div class="form-group">
				      	<label for="roleIds" class="col-sm-1 control-label text-left" style="width: 70px">分配用户组：</label>
				      	<div class="col-sm-9">
				      		<c:forEach items="${groups}" var="group" varStatus="status">
								<label class="checkbox-inline">
									<form:checkbox path="groupIds" value="${group.id}" /><p class="form-control-static"
											style="white-space:nowrap; font-size:14px;width:70px;overflow:hidden;margin-top:-5px;text-overflow:ellipsis;" title="${group.name}">${group.name}</p>
								</label>
								<c:if test="${status.index !=0 && status.index % 7 == 0}">
									<br/>
								</c:if>
								<c:if test="${status.isLast()}">
									<br/>
								</c:if>										      		
				      		</c:forEach>
				      	</div>
				      </div>
				     </div>
				     
				     <div class="tab-pane" id="tab_1_1_5"></div>
			      </div>
			    </div>
			    <div class="form-group">
						<label for="description" class="col-sm-3 control-label text-right">描述：</label>
						<div class="col-sm-6">
						     <form:textarea path="description" rows="3" class="form-control validate[maxSize[200]]"/> 
						</div>
				</div>
				<div class="form-group">
						<div class="col-sm-offset-3 col-sm-6">
			           		<input type="hidden" name="token" value="${token}" />
			           		<c:if test="${id>-1}">
   					  			 <button type="button"  class="btn btn-primary" onclick="edit('${path}/user/update.action')">保存</button>
   							 </c:if>
   							
   							  <c:if test="${id==-1}">
   							   <button type="button"  class="btn btn-primary" onclick="edit('${path}/user/add.action')">保存</button>
   							 </c:if>
   							  &nbsp;
   							  <c:if test="${id==-1}">
   							  		<button type="button" class="btn btn-primary" onclick="resetForm();">重置</button>
   							   </c:if>
   							   <c:if test="${id>-1}">
   							   		<button type="button" class="btn btn-primary" onclick="resetForm1('${groupName}');">重置</button>
   							   </c:if>
   							   &nbsp; 
			            	<input class="btn btn-primary" type="button" value="返回 " onclick="history.go(-1);"/>
			            </div>
				</div>
				<!-- <button type="button"  class="btn btn-primary" onclick="getAllQueryConditionsByResType();">获得添加权限数据</button> -->
				<form:hidden path="organization" id="organization" />	
				<form:hidden path="dataPreJson" id="dataPreJson"/>
				<form:hidden path="resourceDataJson" id="resourceDataJson"/>
				<form:hidden path="dataPreRangeArray" id="dataPreRangeArray"/>
			</form:form>   	
  		  </div>
		</div>	
	</div>
    </body>
</html>