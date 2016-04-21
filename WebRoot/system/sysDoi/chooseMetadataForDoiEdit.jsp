<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/appframe/common.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>选择DOI元素</title>
<style type="text/css">
.tdLabel {text-align:center;} 
/* #publishType { border:1px solid #ccc; height:30px; font-size:12px; position:relative; zoom:1; margin:-1px; } */
</style>
<script language='javascript'>
		function save(){
			var extendMainOne = $('#mainOne').val();
			var secondOptionalOne = $('#secondOptionalOne1').val();
			if(extendMainOne!="" && secondOptionalOne!=""){
				$('#secondOptionalOne').val(secondOptionalOne+"/"+extendMainOne);
			}else if(secondOptionalOne!=""){
				$('#secondOptionalOne').val(secondOptionalOne+"/"+secondOptionalOne.substring(0,4));
			}else{
				$('#secondOptionalOne').val();
			}
			var extendMainTwo = $('#mainTwo').val();
			var secondOptionalTwo = $('#secondOptionalTwo2').val();
			if(extendMainTwo!="" && secondOptionalTwo!=""){
				$('#secondOptionalTwo').val(secondOptionalTwo+"/"+extendMainTwo);
			}else if(secondOptionalTwo!=""){
				$('#secondOptionalTwo').val(secondOptionalTwo+"/"+secondOptionalTwo.substring(0,4));
			}else{
				$('#secondOptionalTwo').val();
			}
			
			var extendMainThree = $('#mainThree').val();
			var secondOptionalThree = $('#secondOptionalThree3').val();
			if(extendMainThree!="" && secondOptionalThree!=""){
				$('#secondOptionalThree').val(secondOptionalThree+"/"+extendMainThree);
			}else if(secondOptionalThree!=""){
				$('#secondOptionalThree').val(secondOptionalThree+"/"+secondOptionalThree.substring(0,4));
			}else{
				$('#secondOptionalThree').val();
			}
			
			var extendMainFour = $('#mainFour').val();
			var secondOptionalFour = $('#secondOptionalFour4').val();
			if(extendMainFour!="" && secondOptionalFour!=""){
				$('#secondOptionalFour').val(secondOptionalFour+"/"+extendMainFour);
			}else if(secondOptionalFour!=""){
				$('#secondOptionalFour').val(secondOptionalFour+"/"+secondOptionalFour.substring(0,4));
			}else{
				$('#secondOptionalFour').val();
			}
			var url='${path}/system/sysDoi/updDoi.action?id='+$('#doiId').val();
			$('#form').ajaxSubmit({
				url: url,//表单的action
				type: 'post',//方式
 				success:(function(data){
 					if(data=='1'){
 						$.alert("提交成功");
 					}else{
 						$.alert("提交失败");
 					}
           		})
 			});
		}
		jQuery(document).ready(function(){
			fieldAttr();
			$('#secondOptionalOne1').change(function(){
				var tt=$("#secondOptionalOne1").find('option:selected').val();
				$('#secondOptionalOne').val(tt);
				tt=tt.substring(0,4);
				var mainShort = $('#mainOne').val();
				if(mainShort!=""){
					tt = mainShort;
				}
				var exampleValue = $('#firstPartOne').val()+"."+$('#firstPartTwo').val()+$('#separator').val()+$('#firstPartThree').val()+"."+tt+"."+"9808789875431"+"."+"00"+"."+"00000001";
				$('#example').html(exampleValue);
			});
			$('#secondOptionalTwo2').change(function(){
				var tt=$("#secondOptionalTwo2").find('option:selected').val();
				$('#secondOptionalTwo').val(tt);
			});
			
			$('#secondOptionalThree3').change(function(){
				var tt=$("#secondOptionalThree3").find('option:selected').val();
				$('#secondOptionalThree').val(tt);
			});
			
			$('#secondOptionalFour4').change(function(){
				var tt=$("#secondOptionalFour4").find('option:selected').val();
				$('#secondOptionalFour').val(tt);
			});
			
			$('#separator').change(function(){
				var tt=$("#secondOptionalOne1").find('option:selected').val();
				tt=tt.substring(0,4);
				var mainShort = $('#mainOne').val();
				if(mainShort!=""){
					tt = mainShort;
				}
				var exampleValue = $('#firstPartOne').val()+"."+$('#firstPartTwo').val()+$('#separator').val()+$('#firstPartThree').val()+"."+tt+"."+"9808789875431"+"."+"00"+"."+"00000001";
				$('#example').html(exampleValue);
			});
		});
// 		function callback(data){
// 		    top.index_frame.work_main.freshDataTable('data_div');
// 			$.closeFromInner();
// 		}
			function fieldAttr(){
				$.ajax({
					type : "post",
					url : "${path}/system/sysDoi/doiField.action?publishType="+$('#publishType').val(),
					success : function(data) {
						var json = JSON.parse(data);
						if(json!=null&&json!=undefined){
							$('#lc').show();
						}
						var fieldName = "";
						var content = "";
						for(var i in json){
				 			var info = json[i];
					 		content +="<option value = '"+i+"'>"+info+"</option>";
				 		}
						$("#secondOptionalOne1").append(content);
						$("#secondOptionalTwo2").append(content);
						$("#firstPartTwo").append(content);
						$("#secondOptionalThree3").append(content);
						$("#secondOptionalFour4").append(content);
						$("#thirdExtendOne").append(content);
						$("#thirdExtendTwo").append(content);
						$("#thirdExtendThree").append(content);
						$("#thirdExtendFour").append(content);
						$("#thirdExtendFive").append(content);
						
						var title = document.getElementById('secondOptionalOne1');
						var nameValue = "${frmWord.secondOptionalOne}";
						var extendShort = nameValue;
			 			if(nameValue.indexOf("/")){
			 				nameValue = nameValue.substring(0,nameValue.indexOf('/'));
			 				extendShort = extendShort.substring(extendShort.indexOf('/')+1,extendShort.length);
			 				$('#mainOne').val(extendShort);
			 			}
						for(var j=0;j<title.options.length;j++){
							if(title.options[j].value == nameValue){
							title.options[j].selected = true;
							break;
							}
						}
						
						var title = document.getElementById('secondOptionalTwo2');
						var nameValue = "${frmWord.secondOptionalTwo}";
						var extendShort = nameValue;
			 			if(nameValue.indexOf("/")){
			 				nameValue = nameValue.substring(0,nameValue.indexOf('/'));
			 				extendShort = extendShort.substring(extendShort.indexOf('/')+1,extendShort.length);
			 				$('#mainTwo').val(extendShort);
			 			}
						for(var j=0;j<title.options.length;j++){
							if(title.options[j].value == nameValue){
							title.options[j].selected = true;
							break;
							}
						}
						var title = document.getElementById('secondOptionalThree3');
						var nameValue = "${frmWord.secondOptionalThree}";
						var extendShort = nameValue;
			 			if(nameValue.indexOf("/")){
			 				nameValue = nameValue.substring(0,nameValue.indexOf('/'));
			 				extendShort = extendShort.substring(extendShort.indexOf('/')+1,extendShort.length);
			 				$('#mainThree').val(extendShort);
			 			}
						for(var j=0;j<title.options.length;j++){
							if(title.options[j].value == nameValue){
							title.options[j].selected = true;
							break;
							}
						}
						var title = document.getElementById('secondOptionalFour4');
						var nameValue = "${frmWord.secondOptionalFour}";
						var extendShort = nameValue;
			 			if(nameValue.indexOf("/")){
			 				nameValue = nameValue.substring(0,nameValue.indexOf('/'));
			 				extendShort = extendShort.substring(extendShort.indexOf('/')+1,extendShort.length);
			 				$('#mainFour').val(extendShort);
			 			}
						for(var j=0;j<title.options.length;j++){
							if(title.options[j].value == nameValue){
							title.options[j].selected = true;
							break;
							}
						}
					//	-----------------------------------
						var title = document.getElementById('firstPartTwo');
						var nameValue = "${frmWord.firstPartTwo}";
						for(var j=0;j<title.options.length;j++){
							if(title.options[j].value == nameValue){
							title.options[j].selected = true;
							break;
							}
						}
						
						var title = document.getElementById('separator');
						var nameValue = "${frmWord.separator}";
						for(var j=0;j<title.options.length;j++){
							if(title.options[j].value == nameValue){
							title.options[j].selected = true;
							break;
							}
						}
						
						var title = document.getElementById('thirdExtendOne');
						var nameValue = "${frmWord.thirdExtendOne}";
						for(var j=0;j<title.options.length;j++){
							if(title.options[j].value == nameValue){
							title.options[j].selected = true;
							break;
							}
						}
						var title = document.getElementById('thirdExtendTwo');
						var nameValue = "${frmWord.thirdExtendTwo}";
						for(var j=0;j<title.options.length;j++){
							if(title.options[j].value == nameValue){
							title.options[j].selected = true;
							break;
							}
						}
						var title = document.getElementById('thirdExtendThree');
						var nameValue = "${frmWord.thirdExtendThree}";
						for(var j=0;j<title.options.length;j++){
							if(title.options[j].value == nameValue){
							title.options[j].selected = true;
							break;
							}
						}
						var title = document.getElementById('thirdExtendFour');
						var nameValue = "${frmWord.thirdExtendFour}";
						for(var j=0;j<title.options.length;j++){
							if(title.options[j].value == nameValue){
							title.options[j].selected = true;
							break;
							}
						}
						var title = document.getElementById('thirdExtendFive');
						var nameValue = "${frmWord.thirdExtendFive}";
						for(var j=0;j<title.options.length;j++){
							if(title.options[j].value == nameValue){
							title.options[j].selected = true;
							break;
							}
						}
					}
				});
				
			}
</script>
</head>
<body>
<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
	<div class="panel panel-default">
	    <div class="panel-heading" id="div_head_t">
			<ol class="breadcrumb">
				<li><a href="javascript:;">系统管理</a></li>
				<li class="active">系统设置</li>
				<li class="active">添加Doi</li>
			</ol>
		</div>
	 </div>	
	 <div>
		<table width="99%" border="0" >
			<tbody >
				<form:form action="#" id="form" class="form-horizontal" name="form"  modelAttribute="frmWord" method="post" role="form">
				<input type="hidden" id="publishType" name="publishType" value="${publishType}"/>
				<input type="hidden" id="doiId" name="doiId" value="${doiId}"/>
				<input type="hidden" id="secondOptionalOne" name="secondOptionalOne" value=""/>
				<input type="hidden" id="secondOptionalTwo" name="secondOptionalTwo" value=""/>
				<input type="hidden" id="secondOptionalThree" name="secondOptionalThree" value=""/>
				<input type="hidden" id="secondOptionalFour" name="secondOptionalFour" value=""/>
				<table border="0" style="margin:2% 0 0 30%">
					<tr>
                        <td width="50px">
<!--                         <i class="must">*</i> -->
                             <label class="control-label col-md-4">前缀部分1：</label>
                        </td>
                         <td>
                           <div class="col-md-15">
                               <form:input type="text" path="firstPartOne" class="form-control validate[required]" style="width:100%" />
                            </div>
                         </td>
                     </tr>
                     <tr>
                        <td>
                             <label class="control-label col-md-4">前缀部分2：</label>
                        </td>
                         <td>
                           <div class="col-md-15">
                            <select class="form-control" name="firstPartTwo" id="firstPartTwo" style="width:100%">
	                             <option value="">请选择</option>
	                         </select>
<%--                                 <form:input type="text" path="firstPartTwo" class="form-control" style="width:100%"/> --%>
                            </div>
                         </td>
                     </tr>
                      <tr>
                        <td width="50px">
<!--                         <i class="must">*</i> -->
                             <label class="control-label col-md-4">分隔符：</label>
                        </td>
                         <td>
                           <div class="col-md-15">
                               <app:constants name="separator" repository="com.brainsoon.system.support.SystemConstants" className="PublishSeparator" inputType="select" headerValue="全部" cssType="form-control"></app:constants>
                            </div>
                         </td>
                     </tr>
                      <tr>
                        <td>
                             <label class="control-label col-md-4">前缀部分3：</label>
                        </td>
                         <td>
                           <div class="col-md-15">
                                <form:input type="text" path="firstPartThree" class="form-control" style="width:100%"/>
                            </div>
                         </td>
                     </tr>
               
				<tr>
					<td width="50px" rowspan="4">
					<label class="control-label col-md-4"><i class="must">*</i>主要属性</label>
<!-- 					<input type="text" name="commonMeta" id="commonMeta" class="form-control validate[required]"/> -->
					</td>
					<td  width="10px"><label class="control-label col-md-4">主要属性可选1:</label>
					</td>
					<td width="70px"><label class="control-label">可选值1：</label>
					</td>
					<td width="100px">
					<select id="secondOptionalOne1" name="secondOptionalOne1" class="form-control validate[required]" >
                     <option value="">请选择</option>
                    </select>
	                 </td>
	                 <td width="60px"><label class="control-label">英文名：</label></td>
                     <td width="60px" align="center"><label class="control-label">简写：</label></td>
 					<td width="70px"> <input type="text" name="mainOne" id="mainOne" class="form-control" style="width:100%"/>
					</td>
					<td align="center"><label class="control-label">4位</label>
					</td>
				</tr>
				<tr>
					<td  width="10px"><label class="control-label col-md-4">主要属性可选2:</label>
					</td>
					<td width="50px"><label class="control-label">可选值2：</label>
					</td>
					<td width="100px"><select class="form-control" name="secondOptionalTwo2" id="secondOptionalTwo2"style="width:100%">
	                 <option value="">请选择</option>
	                 </select>
	                 </td>
	                 <td width="60px"><label class="control-label">英文名：</label>
                     </td>
                     <td width="60px" align="center"><label class="control-label">简写：</label>
 					</td>
 					<td width="70px"><input type="text" name="mainTwo" id="mainTwo" class="form-control" style="width:100%"/>
					</td>
					<td align="center"><label class="control-label">4位</label>
					</td>
				</tr>
				<tr>
					<td  width="10px"><label class="control-label col-md-4">主要属性可选3:</label>
					</td>
					<td width="50px"><label class="control-label">可选值3：</label>
					</td>
					<td width="100px"><select class="form-control" name="secondOptionalThree3" id="secondOptionalThree3"style="width:100%">
	                 <option value="">请选择</option>
	                 </select>
	                 </td>
	                 <td width="60px"><label class="control-label">英文名：</label>
                     </td>
                     <td width="60px" align="center"><label class="control-label">简写：</label>
 					</td>
 					<td width="70px"><input type="text" name="mainThree" id="mainThree" class="form-control" style="width:100%"/>
					</td>
					<td align="center"><label class="control-label">4位</label>
					</td>
				</tr>
				<tr>
					<td  width="10px"><label class="control-label col-md-4">主要属性可选4:</label>
					</td>
					<td width="50px"><label class="control-label">可选值4：</label>
					</td>
					<td width="100px"><select class="form-control" name="secondOptionalFour4" id="secondOptionalFour4"style="width:100%">
	                 <option value="">请选择</option>
	                 </select>
	                 </td>
	                 <td width="60px"><label class="control-label">英文名：</label>
                     </td>
                     <td width="60px" align="center"><label class="control-label">简写：</label>
 					</td>
 					<td width="70px"><input type="text" name="mainFour" id="mainFour" class="form-control" style="width:100%"/>
					</td>
					<td width="50px" align="center"><label class="control-label">4位</label>
					</td>
				</tr>
				<tr>
					<td><label class="control-label col-md-4">扩展属性1:</label></td>
					<td>
						<div class="col-md-15">
	                         <select class="form-control" name="thirdExtendOne" id="thirdExtendOne" style="width:100%">
	                             <option value="">请选择</option>
	                         </select>
                     	</div>
						<label id="thirdPartSymbolEn"></label>
					</td>
				</tr>
				<tr>
					<td ><label class="control-label col-md-4">扩展属性2:</label></td>
					<td>
						<div class="col-md-15">
	                         <select class="form-control" name="thirdExtendTwo" id="thirdExtendTwo" style="width:100%">
	                             <option value="">请选择</option>
	                         </select>
                     	</div>
						<label id="thirdPartValue1En"></label>
					</td>
					</tr>
					<tr>
					<td ><label class="control-label col-md-4">扩展属性3:</label></td>
					<td>
						<div class="col-md-15">
	                         <select class="form-control" name="thirdExtendThree" id="thirdExtendThree" style="width:100%">
	                             <option value="">请选择</option>
	                         </select>
                     	</div>
						<label id="thirdPartValue2En"></label>
					</td>
					</tr>
					<tr>
					<td ><label class="control-label col-md-4">扩展属性4:</label></td>
					<td>
						<div class="col-md-15">
	                         <select class="form-control" name="thirdExtendFour" id="thirdExtendFour" style="width:100%">
	                             <option value="">请选择</option>
	                         </select>
                     	</div>
						<label id="thirdPartValue3En"></label>
					</td>
					</tr>
					<tr>
						<td ><label class="control-label col-md-4">扩展属性5:</label></td>
						<td>
							<div class="col-md-15">
		                         <select class="form-control" name="thirdExtendFive" id="thirdExtendFive" style="width:100%">
		                             <option value="">请选择</option>
		                         </select>
	                     	</div>
						</td>				
					</tr>
				<tr>
					<td ><label class="control-label col-md-4">流水号:</label></td>
					<td>
						<div class="col-md-15">
					八位
                     	</div>
					</td>
				</tr>
				<tr>
					<td align="center">例:</td>
					<td colspan="5"><label id="example" class="control-label col-md-2"></label></td>
				</tr>
				</table>
				<label class="control-label"></label>
				<div style="margin:1% 0 0 40%">
						<input id="tijiao" type="button" value="保存" class="btn btn-primary" onclick="save();"/> &nbsp;&nbsp;
	            </div>
				</form:form>
			</tbody>
		</table>
	</div>
</div>
</body>
</html>

