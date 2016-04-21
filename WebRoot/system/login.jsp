<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>用户登录-学研平台</title>
    <link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/font-awesome.css"/>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/style-metronic.css"/>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/login.css"/>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/jquery.min.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/loadmask/jquery.loadmask.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/loadmask/jquery.loadmask.min.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/blockui-master/jquery.blockUI.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/utils/jquery.cookie.js"></script>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/base.css"/>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/common.css"/>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/newlogin.css"/>
	<!--[if lt IE 9]>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/html5shiv.min.js"></script>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/respond.min.js"></script>
	<![endif]-->
	<script type="text/javascript">
	
	$(document).ready(function(){
		 if ($.cookie("rmbUser") == "true") { 
			 $("#_spring_security_remember_me").prop("checked", true); 
			 $("#j_username").val($.cookie("j_username")); 
			 $("#j_password").remove(); 
			 $("#j_password_div").html("<input id=\"j_password\" name=\"j_password\" class=\"passwd\" type=\"password\" onfocus=\"this.type='password'\"  autocomplete=\"off\" placeholder=\"密码\" value=\"\"/>"); 
			 $("#j_password").val($.cookie("j_password")); 
		 } 
	 }); 

	$("#j_username").on('input',function(e){  
		 checkUname();
	 }); 
	 
	 $("#j_password").on('input',function(e){  
		 checkPword();
	 });  
	 
	 
	 
	 function checkUname(){
		 var b= true;
		 var j_username = $("#j_username").val(); 
		 if(j_username == "" || j_username == "账号"){ 
			$("#tip").text("请输入账号!"); 
		 	$("#j_username").focus(); 
		 	b = false;
		 }else{
			$("#tip").text(""); 
		 }
		 return b;
	 }
	 
	 
	 function checkPword(){
		 var b= true;
		 var j_password = $("#j_password").val(); 
		 if(j_password == "" || j_password == "密码"){ 
			 $("#tip").text("请输入密码!"); 
			 $("#j_password").focus(); 
			 b = false;
		 }else{
			 $("#tip").text(""); 
		 }
		 return b;
	 }
	 
	 
		  
		  
	 //记住用户名密码 
	 function saveUserInfo() { 
		 var b = $("#_spring_security_remember_me").prop("checked");
		 if(check(b)){
			 if (b) { 
				 var j_username = $("#j_username").val(); 
				 var j_password = $("#j_password").val(); 
				 $.cookie("rmbUser", "true", { expires: 7 }); //存储一个带7天期限的cookie 
				 $.cookie("j_username", j_username, { expires: 7 }); 
				 $.cookie("j_password", j_password, { expires: 7 }); 
			 }else{ 
				 $.cookie("rmbUser", "false", { expire: -1 }); 
				 $.cookie("j_username", "", { expires: -1 }); 
				 $.cookie("j_password", "", { expires: -1 }); 
			 } 
		 }
	 }
	  
	 function check(b){ 
		 var boo = true;
		 if (b == 1 || b == true) { 
			 boo = checkUname();
			 if(boo){
				boo = checkPword();
			 }
		 }else{
			 $("#tip").text(""); 
		 }
		 return boo; 
	} 
		 
	
	function toSubmit(){
		saveUserInfo();
		$.blockUI({ message: '<div style="height:25px;" align="center"><img src="${path}/appframe/plugin/EasyUI/themes/default/images/loading.gif" /> &nbsp;正在登陆，请稍待。。。</div>',
			css: {
                border: 'none',
                padding: '20px',
                backgroundColor: 'white',
                textAligh:'center',
            //    top:"50%",  
                width:"300px",
                opacity: .7
               }
		});
		//$.post("${path}/j_spring_security_check");
	}
	
	function checkSubmit(){
		var record = check(1);
		if(record){
			toSubmit();
		}else{
			return record;
		}
	}
	</script>
  </head>
<body>
<div class="top">
	<div class="left"></div>
	<!-- <div class="right"></div> -->
	<div class="login">
		<p class="title" style="color:#666666;font-weight:blod;font-size:18px">学研平台</p>
		<form action="${path}/j_spring_security_check" method="post" onsubmit="return checkSubmit();">
			 <div class="form-group" style="display:none;">
				<div class="radio-list col-xs-offset-1">
					<label class="radio-inline form-group">
						<input type="radio" name="platformId" id="platformId" value="1" checked="checked"/>
					</label>
					<label class="radio-inline form-group">
						<input type="radio" name="platformId" id="platformId" value="2"/>
					</label>
				</div>
            </div>
                <div id="j_username_div">
                	<input class="admin" type="text" name="j_username" id="j_username" autocomplete="off" placeholder="账号"/>
                </div>
		 		<div id="j_password_div">
					<input class="passwd" name="j_password" id="j_password" onfocus="this.type='password'"  autocomplete="off" placeholder="密码" value=""/>
				</div>
				<input type="submit" class="smt smtbtn" style="color:white;"  value="登录"/>
	<!-- 		<div class="form-actions"> -->
	<!-- 			   <button class="submit btn btn-login red" id="submitId"> -->
	<!--                   	  登陆<i class="fa fa-arrow-circle-o-right"></i> -->
	<!--                 </button> -->
			<label class="remember_pass"> 
				<input id="_spring_security_remember_me" name="_spring_security_remember_me" type="checkbox" onclick="saveUserInfo();"/> 记住密码
			</label>
			<div class="center">
				<label id="tip" style="color:red;"></label>
			</div>
<!-- 			</div> -->
		</form>
	</div>
	<div class="input-admin"></div>
	<div class="input-passwd"></div>
</div>
<div class="bottom">
	<p style="padding-top:15px;font-size:15px;font-family:宋体;">学研平台&nbsp;|&nbsp;北京博云易讯科技有限公司</p>
</div>
</body>

<script>
//  function cleanpwd(){
// 	 document.getElementById('j_password').value=''; 
//  }
 
//  function immediately(){ 
// 	 var element = document.getElementById("j_username"); 
// 	 if("\v"=="v") { 
// 	 	element.onpropertychange = check; 
// 	 }else{ 
// 		 element.addEventListener("input",check,false); 
// 	 }
//  } 

//  	document.onkeydown=function(e){
// 		 var isie = (document.all) ? true:false;
// 		 var key;
// 		 var ev;
// 		 if(isie){//IE浏览器
// 			 key = window.event.keyCode;
// 			 ev = window.event;
// 		 }else{//火狐浏览器
// 			 key = e.which;
// 			 ev = e;
// 		 }
// 		 if(key==9){//IE浏览器
// 			 if(isie){
// 				 ev.keyCode=0;
// 				 ev.returnValue=false;
// 			 }else{//火狐浏览器
// 				 ev.which=0;
// 				 ev.preventDefault();
// 			 }
// 		 }
// 	 }; 

</script>
</html>