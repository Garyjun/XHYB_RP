<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>编辑</title>
<script type="text/javascript"
	src="${path}/appframe/util/accountHeight.js"></script>
<script type="text/javascript">
var codeId='${codeId}';
var id='${id}';
			function edit(act){
				var nameValue=$("#codeName2").find('option:selected').text();
				var act="";
				var codeName2 = {code:nameValue};
				var codeName1  = encodeURI(JSON.stringify(codeName2));
				act = '${path}/code/toUpdCode.action?dicName='+id+'&codeId='+codeId+'&codeName='+codeName1;
				document.form1.action=act; 
				jQuery('#form1').submit();
			}
			jQuery(document).ready(function(){
				 /**
	             * 返回数据内容：[String,Boolean,String] 
	             * 第一个值类型为 String，是接收到 fieldId 的值；
				 * 第二个值类型为 Boolean，验证通过返回 true，不通过返回 false
				 * 第三个值类型为 String，自定义的错误提示,如果没有采用下面默认的alertText
	             */
	             //也可以写成: $.validationEngineLanguage.allRules.checkLoginName
				$.validationEngineLanguage.allRules["checkLoginName"] = {
				 "url":"${path}/user/checkLoginName.action",
		         //传递附加参数
		         "extraData": "name=loginName&userId=1",
		         "alertText": "* 该登录名被使用"
		       //  "alertTextOk":"* 帐号可以使用.", 
		        // "alertTextLoad": "* 正在检查登录名，请稍候..."
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
				$.ajax({
					    type:'post',
					    async:false,
						url:'${path}/code/codeType.action?adapterType='+"${frmWord.adapterType}",
						success:function(data){
							var json = JSON.parse(data);
							var content = "";
					 		for(var i in json){
					 			var info = json[i];
						 		content +="<option value = '"+i+"'>"+info+"</option>";
					 		}
					 		    content += "<option value = '无'>"+'无'+"</option>";
					 		$("#codeName2").append(content);
						}
					});
				title = document.getElementById('codeName2');
				var name = "${frmWord.codeName}";
				var select = false;
				
				for(var j=0;j<title.options.length;j++){
					if(title.options[j].innerHTML == name){
					title.options[j].selected = true;
					select = true;
					break;
					}
				}
				if($('#adapterType').val()=="01"||$('#adapterType').val()=="00"||$('#adapterType').val()=="02"){
				if(!select){
					$("#codeName2").append(content);
					var title = document.getElementById('codeName2');
					for(var i=0;i<title.options.length;i++){
						if(title.options[i].innerHTML == '无'){
						title.options[i].selected = true;
						break;
						}
					}
				}
				}else{
					var cou = $('#adapterName').val();
					var content="";
					//将年级和名称的两个字拼起来
					if($('#codeDefault').val()!=''){
					content +="<option value = '"+cou+"'>"+cou+"</option>";
					$("#codeName2").append(content);
					var title = document.getElementById('codeName2');
					for(var i=0;i<title.options.length;i++){
						if(title.options[i].innerHTML == cou){
						title.options[i].selected = true;
						break;
						}
					}
				}else{
					var content = "<option value = '无'>"+'请选择'+"</option>";
					$("#codeName2").append(content);
					var title = document.getElementById('codeName2');
					for(var i=0;i<title.options.length;i++){
						if(title.options[i].innerHTML == '请选择'){
						title.options[i].selected = true;
						break;
						}
					}
				}
				}
				if($('#adapterType').val()=="03"){
					$.ajax({
					    type:'post',
					    async:false,
						url:'${path}/code/codeType.action?adapterType='+$('#adapterType').val(),
						success:function(data){
							//alert(data);
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
				$('#codeGrade').change(function(){
					$('#codeName2').empty();
					$('#codetype2').hide();
					$('#codetype1').show();
					var tt=$("#codeStage").find('option:selected').text();
					var bb=$("#codeGrade").find('option:selected').text();
					var content="";
					$('#codeDefault').val($("#codeStage").val()+$("#codeGrade").val());
					//将年级和名称的两个字拼起来
					content +="<option value = '"+tt+bb+"'>"+tt+bb+"</option>";
					$("#codeName2").append(content);
				});
			 if($('#adapterType').val()=="01"||$('#adapterType').val()=="02"||$('#adapterType').val()=="00"||$('#adapterType').val()=="04")
				{
					$('#codetype2').hide();
					$('#codeStage1').hide();
					$('#codeGrade2').hide();
				}else
				{
					$('#codetype2').hide();
					$('#codeStage1').show();
					$('#codeGrade2').show();
				}
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
			function query(){
				var adapterName = $('#adapterName').val();
				$("#codeName2").empty();
				$.ajax({
				    type:'post',
				    async:false,
					url:'${path}/code/codeType.action?adapterType='+"${frmWord.adapterType}",
					success:function(data){
						var json = JSON.parse(data);
						var content = "";
				 		for(var i in json){
				 			var info = json[i];
					 		content +="<option value = '"+i+"'>"+info+"</option>";
				 		}
				 		$("#codeName2").append(content);
					}
				});
			var title = document.getElementById('codeName2');
			var select=false;
			//搜索页面是否与本地名称相等，相等把默认编码放入输入框内
			for(var i=0;i<title.options.length;i++){
				if(title.options[i].innerHTML == adapterName){
				title.options[i].selected = true;
				select = true;
				$('#codeDefault').val($('#codeName2').val());
				break;
				}
			}
			if(!select){
				//alert("无");
				var content = "<option value = '无'>"+'无'+"</option>";
				$("#codeName2").append(content);
				var title = document.getElementById('codeName2');
				for(var i=0;i<title.options.length;i++){
					if(title.options[i].innerHTML == '无'){
					title.options[i].selected = true;
					break;
					}
				}
				$("#codeDefault").val("");
			}
			}
		</script>

</head>
<body>
	<div class="form-wrap">
		<form:form action="${path}/code/updtarget.action" id="form1"
			name="form1" modelAttribute="frmWord" method="post"
			class="form-horizontal" role="form">
			<div class="form-group">
			<input type="hidden" name="adapterType" id="adapterType" value="${frmWord.adapterType}"></input>
				<label class="col-xs-5 control-label text-right"><span
					class="required"></span>字典类型：</label>
					<div class="col-xs-5">
					<c:if test="${frmWord.adapterType=='00'}">
						<p class="form-control-static">资源类型</p>
					</c:if>
					<c:if test="${frmWord.adapterType=='01'}">
						<p class="form-control-static">分册</p>
					</c:if>
					<c:if test="${frmWord.adapterType=='02'}">
						<p class="form-control-static">学科</p>
					</c:if>
					<c:if test="${frmWord.adapterType=='03'}">
						<p class="form-control-static">年级</p>
					</c:if>	<c:if test="${frmWord.adapterType=='04'}">
						<p class="form-control-static">学段</p>
					</c:if>								
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right"><span
					class="required">*</span>集成方名称：</label>
				<div class="col-xs-5">
					<form:input path="adapterName" id="adapterName"
						class="form-control validate[required] text-input" onblur="query()"/>
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
					class="required">*</span>本系统名称：</label>
				<div class="col-xs-5">
					<form:select path="codeName" id="codeName2" itemLabel=" "
						itemValue="null"
						class="form-control validate[required] text-input"></form:select>
				</div>
			</div>


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
					<label class="col-xs-5 control-label text-right">状&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;态：</label>
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
						onclick="edit('${path}/code/updtarget.action')">保存</button>&nbsp;&nbsp;&nbsp;
					<input class="btn btn-primary" type="button" value="关闭 "
						onclick="javascript:$.closeFromInner();" />
				</div>
			</div>
		</form:form>
	</div>

</body>
</html>