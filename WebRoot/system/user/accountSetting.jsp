<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>

<html>
    <head>
    <title>查看</title>    
    <script type="text/javascript">
    jQuery(document).ready(function(){
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
    });
    
	function save(){
		$('#form1').ajaxSubmit({
				method: 'post',//方式
				success:(function(response){
					$.closeFromInner();
       		})
		});
	}

	function setInfo(){
		jQuery('#form1').submit();
	}
    </script>
    </head>
     <body>
      	<div class="form-wrap">
	      		<form:form action="${path}/user/updateAccountInfo.action"  name="form1" id="form1" method="POST"  class="form-horizontal">
		      		<div class="form-group">
						<label for="loginName" class="col-sm-4 control-label text-right">登录名：</label>
						<div class="col-sm-5">
						          	 <p class="form-control-static">
		               					${command.loginName}
		               				 </p>
								 
						</div>
					</div>
					
					<div class="form-group">
						<label for="userName" class="col-sm-4 control-label text-right">姓名：</label>
						<div class="col-sm-5">
							  <p class="form-control-static">
		               			<form:input  path="userName" id="userName"  class="form-control validate[required] text-input" />
		               		 </p>
						</div>
					</div>
					
					<div class="form-group">
						<label for="phone" class="col-sm-4 control-label text-right">电话：</label>
						<div class="col-sm-5">
							 <p class="form-control-static">
		               			<form:input  path="phone" id="phone"  class="form-control validate[custom[guding]] text-input" />
		               		 </p>
						</div>
					</div>
					<div class="form-group">
						<label for="mobile" class="col-sm-4 control-label text-right">手机：</label>
						<div class="col-sm-5">
							  <p class="form-control-static">
		               			<form:input  path="mobile" id="mobile"  class="form-control validate[custom[mobile]] text-input" />
		               		 </p>
						</div>
					</div>
					
					<div class="form-group">
						<label for="email" class="col-sm-4 control-label text-right">email：</label>
						<div class="col-sm-5">
							  <p class="form-control-static">
		               			<form:input  path="email" id="email"  class="form-control validate[custom[newemail]] text-input" />
		               		 </p>
						</div>
					</div>	
			      	
					
				  <div class="form-group">
						<div class="col-sm-offset-4 col-sm-5">
							<input class="btn btn-primary" type="button" value="保存 " onclick="setInfo();"/>
			            	<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
			            </div>
				 </div>
					
			</form:form>   	
  		  </div>
			
    </body>
</html>