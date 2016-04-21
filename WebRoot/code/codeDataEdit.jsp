<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>编辑</title>
<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript">
		var id='${id}';
			function edit(act){
				var nameValue=$("#codeName2").find('option:selected').text();
				var act="";
				var codeName2 = {code:nameValue};
				var codeName1  = encodeURI(JSON.stringify(codeName2));
				act = '${path}/code/toUpdCode.action?dicName='+id+'&codeName='+codeName1;
				document.form1.action=act;
				jQuery('#form1').submit();
			}
			function query(){
				var adapterName = $('#adapterName').val();
				var adapterType = $('#adapterType').val();
				$("#codeName2").empty();
				if(adapterType!='03'){
				$.ajax({
				    type:'post',
				    async:false,
					url:'${path}/code/codeType.action?adapterType='+adapterType,
					success:function(data){
						var json = JSON.parse(data);
						var content = "";
						content += "<option value = '无'>"+'请选择'+"</option>";
				 		for(var i in json){
				 			var info = json[i];
					 		content +="<option value = '"+i+"'>"+info+"</option>";
				 		}
				 		$("#codeName2").append(content);
					}
				});
				}
				//搜索页面是否与本地名称相等，相等把默认编码放入输入框内
			var title = document.getElementById('codeName2');
			var select=false;
			//判断相同则第二个框显示并输入转码
			for(var i=0;i<title.options.length;i++){
				if(title.options[i].innerHTML == adapterName){
				title.options[i].selected = true;
				select = true;
				$('#codeDefault').val($('#codeName2').val());
				break;
				}
			}
			if(!select){
				$("#codeName2").append(content);
				var title = document.getElementById('codeName2');
				for(var i=0;i<title.options.length;i++){
					if(title.options[i].innerHTML == '请选择'){
					title.options[i].selected = true;
					break;
					}
				}
				$("#codeDefault").val("");
			}
			
			}
			jQuery(document).ready(function(){
				$.validationEngineLanguage.allRules["checkLoginName"] = {
				 "url":"${path}/user/checkLoginName.action",
		         //传递附加参数
		         "extraData": "name=loginName&userId=1",
		         "alertText": "* 该登录名被使用"
               };
				$('#form1').validationEngine('attach', {
					relative: true,
					overflownDIV:"#divOverflown",
					promptPosition:"centerRight",//验证提示信息的位置，可设置为："topRight", "bottomLeft", "centerRight", "bottomRight"
					maxErrorsPerField:1,
					onValidationComplete:function(form,status){
						if(status){
							save();
						}
					}
				});
				//编辑页面设置编码类型，编码名称初始值
				$("#codeName2").val("${frmWord.codeName}");
				//$('#codetype2').hide();
				$('#codeStage1').hide();
				$('#codeGrade2').hide();
				//var title = "${APP_USER_SESSION_KEY.platformId}";
				$('#type').change(function(){
				});
				$('#adapterType').change(function(){
					//类型转换，显示相应表单
					var adapterType = $('#adapterType').val();
					$('#codeStage').empty();
					$('#codeDefault').val("");
					 if(adapterType=='01'||adapterType=='02'||adapterType=='00'||adapterType==''||adapterType=='04'){
						$('#codetype1').show();
						$('#codeName2').empty();
						$('#codeStage1').hide();
						$('#codeGrade2').hide();
						$.ajax({
						    type:'post',
						    async:false,
							url:'${path}/code/codeType.action?adapterType='+adapterType,
							success:function(data){
								var json = JSON.parse(data);
								var content = "<option value = '无'>"+'请选择'+"</option>";
						 		for(var i in json){
						 			var info = json[i];
							 		content +="<option value = '"+i+"'>"+info+"</option>";
						 		}
						 		$("#codeName2").append(content);
							}
						});
					}else{
						$('#codeStage1').show();
						$('#codeGrade2').show();
						//年级
						$.ajax({
						    type:'post',
						    async:false,
							url:'${path}/code/codeType.action?adapterType='+adapterType,
							success:function(data){
								var json = JSON.parse(data);
								var content = "<option value = '0'>"+'请选择'+"</option>";
						 		for(var i in json){
						 			var info = json[i];
						 			content +="<option value = '"+i+"'>"+info+"</option>";
						 		}
						 		$("#codeStage").append(content);
							}
						});
					}
				});
				$('#codeName2').change(function(){
					//第一个输入框与第二个框相同则一起动
					if($('#codeName2').val()==$('adapterName').val()){
					$('#codeDefault').val($('#codeName2').val());
					var tt=$("#codeName2").find('option:selected').text();
					$('#adapterName').val(tt);
					}
					else if($('#adapterName').val()==""){
						var tt=$("#codeName2").find('option:selected').text();
						$('#adapterName').val(tt);
						$('#codeDefault').val($('#codeName2').val());
					}else if($('#adapterName').val()==$('#codeName2').val()){
						$('#codeDefault').val($('#codeName2').val());
						var tt=$("#codeName2").find('option:selected').text();
						$('#adapterName').val(tt);
					}else{
						$('#codeDefault').val($('#codeName2').val());
					}
				});
				$('#codeStage').change(function(){
					var adapterType = $('#codeStage').val();
					$('#codeGrade').empty();
					//根据字典类型查询相应的select表单
					$.ajax({
					    type:'post',
					    async:false,
						url:'${path}/code/codeType.action?adapterType='+adapterType,
						success:function(data){
							//alert(data);
							var json = JSON.parse(data);
							var content = "<option value = '0'>"+'请选择'+"</option>";
					 		for(var i in json){
					 			var info = json[i];
					 			content +="<option value = '"+i+"'>"+info+"</option>";
					 		}
					 		$("#codeGrade").append(content);
						}
					});
				});
				//学段改变显示相应的名称
				$('#codeGrade').change(function(){
					$('#codeName2').empty();
					//$('#codetype2').hide();
					$('#codetype1').show();
					var tt=$("#codeStage").find('option:selected').text();
					var bb=$("#codeGrade").find('option:selected').text();
 					var content="";
					$('#codeDefault').val($("#codeStage").val()+$("#codeGrade").val());
					//将年级和名称的两个字拼起来
 					content +="<option value = '"+tt+bb+"'>"+tt+bb+"</option>";
 					$("#codeName2").append(content);
				});
			});
			function save(){
				$('#form1').ajaxSubmit({
	 				method: 'post',//方式
	 				success:(function(response){
	 					callback(response);
	           		})
	 			});
			}
			function callback(data){
			    top.index_frame.work_main.freshDataTable('data_div');
				$.closeFromInner();
			}
		</script>
</head>
<body>
	<div class="form-wrap">
		<form:form action="${path}/code/updtarget.action" id="form1"
			name="form1" modelAttribute="frmWord" method="post"
			class="form-horizontal">
			<div class="form-group">
				<label class="col-xs-5 control-label text-right"><span
					class="required">*</span>字典类型：</label>
				<div class="col-xs-5">
					<app:constants name="adapterType" id="adapterType"
						repository="com.brainsoon.system.support.SystemConstants"
						className="CodeType" inputType="select" ignoreKeys="T10,T11,T00"
					    selected="" cssType="form-control validate[required]" ></app:constants>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right"><span
					class="required">*</span>集成方名称：</label>
				<div class="col-xs-5">
					<form:input path="adapterName" id="adapterName"
						class="form-control validate[required] text-input" onblur="query();"/>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right"><span
					class="required">*</span>集成方编码：</label>
				<div class="col-xs-5">
					<form:input path="adapterCode"
						class="form-control validate[required] text-input" />
				</div>
			</div>
			<div class="form-group" id="codeStage1">
				<label class="col-xs-5 control-label text-right"><span
					class="required"></span>学年：</label>
				<div class="col-xs-5">
					<form:select path="codeStage" id="codeStage" itemLabel=""
						itemValue="null"
						class="form-control text-input"></form:select>
				</div>
			</div>
			<div class="form-group" id="codeGrade2">
				<label class="col-xs-5 control-label text-right"><span
					class="required"></span>年级：</label>
				<div class="col-xs-5">
					<form:select path="codeGrade" id="codeGrade" itemLabel=""
						itemValue="null"
						class="form-control text-input"></form:select>
				</div>
			</div>
			<div class="form-group" id="codetype1">
				<label class="col-xs-5 control-label text-right"><span
					class="required"></span>本系统名称：</label>
				<div class="col-xs-5">
					<form:select path="codeName" id="codeName2" itemLabel=""
						itemValue=""
						class="form-control text-input"><option id="codeName2"></option></form:select>
				</div>
			</div>

<!-- 			<div class="form-group" id="codetype2"> -->
<!-- 				<label class="col-xs-5 control-label text-right"><span -->
<!-- 					class="required">*</span>本系统名称：</label> -->
<!-- 				<div class="col-xs-5"> -->
<%-- 					<app:constants name="codeName" id="codeName1" --%>
<%-- 						repository="com.brainsoon.system.support.SystemConstants" --%>
<%-- 						className="ResourceType" inputType="select" --%>
<%-- 						ignoreKeys="T10,T11,T00" selected="" headerValue="请选择.." --%>
<%-- 						cssType="form-control"></app:constants> --%>
<!-- 				</div> -->
<!-- 			</div> -->
			<div class="form-group">
				<label class="col-xs-5 control-label text-right"><span
					class="required"></span>本系统编码：</label>
				<div class="col-xs-5">
					<form:input path="codeDefault" id="codeDefault"
						class="form-control text-input" readonly="true" />
				</div>
			</div>
			<div class="form-group">
				<div class="by-form-row clearfix">
					<label class="col-xs-5 control-label text-right">状态：</label>
					<!-- 					    <label for="codeStatus" class="col-sm-3 control-label text-right"><i class="must">*</i>状&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;态：</label> -->
					<div class="col-xs-5">
						<form:radiobutton path="codeStatus" value="1" checked="true" />
						启用
						<form:radiobutton path="codeStatus" value="0" />
						禁用
					</div>
				</div>
			</div>

			<div class="form-group">
				<div class="col-sm-offset-3 col-sm-6 text-center">
					<input type="hidden" name="token" value="${token}" />
					<button type="button" class="btn btn-primary"
						onclick="edit()">保存</button>
					<button type="reset" class="btn btn-primary">重置</button>
					<input class="btn btn-primary" type="button" value="关闭 "
						onclick="javascript:$.closeFromInner();" />
				</div>
			</div>
<%--  			<form:hidden path="codeName" value="" id="codeName"/> --%>
		</form:form>
	</div>
</body>
</html>