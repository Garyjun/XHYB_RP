<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<title>修改密码</title>
<script type="text/javascript">
	$(function(){
/* 		$.validationEngineLanguage.allRules["checkLoginPassword"] = {
           	  "url":"${path}/user/checkLoginPassword.action",
        	  "alertText": "* 原密码错误！"
        };
		
		$.validationEngineLanguage.allRules["checkNewPassword"] = {
	           "url":"${path}/user/checkNewPassword.action",
	        	"alertText": "* 新密码与原密码重复！"
	    }; */
		
		$('#form1').validationEngine('attach', {
			relative: true,
			overflownDIV:"#divOverflown",
			promptPosition:"centerRight",//验证提示信息的位置，可设置为："topRight", "bottomLeft", "centerRight", "bottomRight"
			maxErrorsPerField:1,
			onValidationComplete:function(form,status){
				if(status){
					update();	
				}
				
			}
		});
	});
	function update(){
		$.get("${path}/user/checkPassword.action?oldPassword="+$("#oldPassword").val()
				+"&newPassword1="+$("#newPassword1").val(),function(data){
			if(data=="-1"){
				parent.parent.layer.alert('原密码错误！'); 
				//$.alert("原密码错误！");
				return;
			}else if(data=="-2"){
				parent.parent.layer.alert('新密码与原密码不能相同！'); 
				//$.alert("新密码与原密码不能相同！");
				return;				
			}else{
			    	$('#form1').ajaxSubmit({
						method: 'post',
						success:(function(msg){
							if(msg =='密码修改成功！'){
								parent.location = "${path}/system/login.jsp";
							}
							//$.closeFromInner();
			      		})
					});
				    	//$.openWindow("${path}/system/user/update_password.jsp?iscolseBtn=1","修改密码");
				    
				
				
				/* $.confirm('确定要修改密码吗？', function(){
				//$.blockUI({ message: '<h5>正在保存...</h5>' });
					
				}); */
			}
		});
	}
	
	function returnLoginPage(){
		parent.location = "${path}/system/login.jsp";
		$.closeFromInner();
	}

</script>
</head>
<body>
	<div class="form-wrap">
		<form:form action="${path}/user/updatePassword.action" name="form1" id="form1" method="POST" class="form-horizontal">
			<div class="form-group">
				<br />
				<input type="hidden" name="id" value="${sessionScope.APP_USER_SESSION_KEY.userId }">
				<label for="loginName" class="col-sm-4 control-label text-right">旧登录密码：</label>
				<div class="col-sm-4">
					<input type="password" id="oldPassword" name="oldPassword" class="form-control validate[required,minSize[8]] text-input" />
				</div>
			</div>

			<div class="form-group">
				<label for="userName" class="col-sm-4 control-label text-right">新登录密码：</label>
				<div class="col-sm-4">
					<input type="password" id="newPassword1" name="newPassword"  class="form-control validate[required,minSize[8],custom[password]] text-input" />
				</div>
			</div>

			<div class="form-group">
				<label for="phone" class="col-sm-4 control-label text-right">确认密码：</label>
				<div class="col-sm-4">
					<input type="password" id="newPassword2" name="newPassword2" class="form-control validate[required,equals[newPassword1]] text-input" />
				</div>
			</div>

			<div class="form-group">
				<div class="col-sm-offset-4 col-sm-4">
					<input type="submit" class="btn btn-primary" value="修改">
					&nbsp;
					<button type="reset" class="btn btn-primary">重置</button>
					<script type="text/javascript">
					<% String  iscolseBtnStr = request.getParameter("iscolseBtn");%>
					  var iscolseBtn = '<%=iscolseBtnStr%>';
					  if(iscolseBtn == null || iscolseBtn == "null" || iscolseBtn == ""){
						  document.write("&nbsp; <input class=\"btn btn-primary\" type=\"button\" value=\"关闭\" onclick=\"javascript:$.closeFromInner();\" />");
					  }else{
						  document.write("&nbsp; <input class=\"btn btn-primary\" type=\"button\" value=\"返回登录页\" onclick=\"javascript:returnLoginPage();\" />");
					  }
					</script>
				</div>
			</div>
		</form:form>
	</div>
</body>
</html>