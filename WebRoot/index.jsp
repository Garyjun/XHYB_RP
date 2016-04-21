<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html lang="zh-CN">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>学研平台</title>
	<script type="text/javascript">
		$(function(){
			var title = "${APP_USER_SESSION_KEY.platformId}";
			if(title == 2)
				document.title = "教育资源管理系统";
			else if(title == 1)
				document.title = "学研平台";
			else
				location.href = "${path}/system/login.jsp";
				
			initWelcome();
			informMessage();
			getLogo();
			
			
			var name = "${APP_USER_SESSION_KEY.name}";
			var updatePassword = "${APP_USER_SESSION_KEY.passwordLastestModifiedTime}"
			dayTime = new Date();
			//获取当前时间
			var currentTime = dayTime.getFullYear()+"-"+(today.getMonth() + 1)+"-"+today.getDate();
			
			var record = 0;   //0 不提示修改密码  1.提示修改密码(首次登陆,或距离上次修改已超过90天)
			var message = ""; //记录弹出信息
			if(updatePassword == null || updatePassword ==''){
				record = 1;
				message = name +",你好! 系统检测到你为第一次登录,请确认修改密码.";
			}else{
				//若当前时间距离上次修改密码时间已经超过90天,要提示修改密码
				updatePassword = updatePassword.substring(0,10);
				var interval = dateDiff(updatePassword,currentTime);
				if((interval+1)>90){
					record = 1;
					message = name +",你好! 系统检测到你距上次修改密码超过90天,请确认修改密码.";
				}
			}
			if(record == '1'){
				layer.confirm(message, {
				    btn: ['确认'],
				    title:"警告 !",
				    area: ['320px', '200px'],
				    scrollbar:false,
				    shift:5,
				    closeBtn: 0,
				    skin:'layui-layer-molv',
				    yes: function(index, layero){
				    	layer.closeAll(); 
				    	 layer.open({
		   						type: 2,
		   						title :'修改密码',
		   						area: ['650px', '430px'],
								btn: [],
								fix: false,
								closeBtn: 0,
								yes: function(index, layero){
								},
								scrollbar: false,
								content: '${path}/system/user/update_password.jsp?iscolseBtn=1'
									
									
				          });
				    	//$.openWindow("${path}/system/user/update_password.jsp?iscolseBtn=1","修改密码");
				    },
				});
			}
			
		//	setInterval(informMessage, 3000);
		});
		function initWelcome(){
			today = new Date();
			var week;
			if (today.getDay() == 0)
				week = "星期日";
			if (today.getDay() == 1)
				week = "星期一";
			if (today.getDay() == 2)
				week = "星期二";
			if (today.getDay() == 3)
				week = "星期三";
			if (today.getDay() == 4)
				week = "星期四";
			if (today.getDay() == 5)
				week = "星期五";
			if (today.getDay() == 6)
				week = "星期六";
			$('#welcomeInfo').html('<span class="by-ymd">'+ today.getFullYear() + '年' + (today.getMonth() + 1) + '月' + today.getDate() + '日</span> <span class="by-week">'+week+'</span>');
		}
		
		
		//判断是否为闰年
		function isLeapYear(year){
			if(year % 4 == 0 && ((year % 100 != 0) || (year % 400 == 0)))
			{
		     	return true;
			}
				return false;
		}
		
		//判断前后两个日期
		function validatePeriod(fyear,fmonth,fday,byear,bmonth,bday){
			if(fyear < byear){
				return true;
			}else if(fyear == byear){
			if(fmonth < bmonth){
		   		return true;
			} else if (fmonth == bmonth){
		   	if(fday <= bday){
		    	return true;
		   	}else {
		    	return false;
		   	}
			}else {
		   		return false;
			}
			}else {
				return false;
			}
		}
		//计算两个日期的差值
		function dateDiff(d1,d2){
		    var disNum=compareDate(d1,d2);
		    return disNum;
		}
		
		//比较两个日期差
		function compareDate(date1,date2)
		{
		    var regexp=/^(\d{1,4})[-|\.]{1}(\d{1,2})[-|\.]{1}(\d{1,2})$/;
		    var monthDays=[0,3,0,1,0,1,0,0,1,0,0,1];
		    regexp.test(date1);
		    var date1Year=RegExp.$1;
		    var date1Month=RegExp.$2;
		    var date1Day=RegExp.$3;
		    regexp.test(date2);
		    var date2Year=RegExp.$1;
		    var date2Month=RegExp.$2;
		    var date2Day=RegExp.$3;

			if(validatePeriod(date1Year,date1Month,date1Day,date2Year,date2Month,date2Day)){
				firstDate=new Date(date1Year,date1Month,date1Day);
			     secondDate=new Date(date2Year,date2Month,date2Day);
			     result=Math.floor((secondDate.getTime()-firstDate.getTime())/(1000*3600*24));
			     for(j=date1Year;j<=date2Year;j++){
			  		if(isLeapYear(j)){
			      		monthDays[1]=2;
			  		}else{
			      		monthDays[1]=3;
			  		}
			  		for(i=date1Month-1;i<date2Month;i++){
			      		result=result-monthDays[i];
			  		}
			     }
			     		return result;
			}else{
			    alert('对不起第一个时间必须小于第二个时间，谢谢！');
			    exit;
			}
		}
		
		
		
		
		function showMenu(obj,subId){
			//alert(obj+subId);
			$('#top_menu').children().removeClass('active');
			$('#sub_menu').children().css("display","none");
			
			$(obj).parent().addClass('active');
			$subMenu = $('#sub_menu').children('#sub_'+subId);
			$subMenu.css("display","block");
			//查找第一个
			$firstA = $subMenu.children().first().children().first();
			openMenu($firstA);
		}
		//默认打开第一个
		function openMenu(obj){
			$(obj).parent().parent().children().children().removeClass('active');
			var menuId=$(obj).attr('id');
			$(obj).addClass('active');
			var url=$(obj).attr('url');
			if(url.indexOf("?")!=-1){
				url=url+'&pMenuId='+menuId;
			}else{
				url=url+'?pMenuId='+menuId;
			}
			//alert(url);
			$.loadPage(url);
		}
		function updatePassword(id){
			$.openWindow("${path}/system/user/update_password.jsp",'修改密码',650,400);
		}
		
		function gotoTaskList(){
			var url = '${path}/TaskAction/toList.action?taskFlag=1';
			$('#index_frame').attr('src',url);
		}
		 
		function gotoCopyrightWarning(){
			var url = "${path}/copyright/warningList.jsp";
			$('#index_frame').attr('src',url);
		}
		
		/**
		* 系统消息条目数
		*/
		function informMessage(){
			$.ajax({
	             type: "post",
	             url: "${path}/system/getInformMessages.action",
	             dataType: "json",
	             async:false,
	             success: function(data){
	            	 if(data!='' && data!=null){
		            	 $("#total").text(data.total);
		            	 $("#copyrightNum").text(data.copyrightNum);
		            	 $("#taskNum").text(data.taskNum);
		            	 $("#displayNum").text(data.total);
	            	 }
	             }
	         });
		}
		
		
		/**
		* get logo
		*/
		function getLogo(){
			var platformId = '${APP_USER_SESSION_KEY.platformId}';
			$.ajax({
	             type: "post",
	             url: "${path}/logo/queryIndexLogo.action?platformId=" + platformId,
	            // dataType: "json",
	             success: function(data){
	            	 var data = JSON.parse(data);
	            	 var name;
	            	 for(var i=0;i<data.length;i++){
	 					var listSon =data[i];
	 					 name = listSon.logoName;
	 				}
	            	 if(name == null || name == ''){
							$('#logo').attr("src","${path}/appframe/main/images/pubLogo.png"); 	            		
	            	 }else{
	            	 	$('#logo').attr("src","${path}/fileDir/sysUpLoadFile/logo/"+ name);
	            	 }
	             }
	         });
		}
		
		
		function openAccountSetting(){
			$.openWindow("${path}/user/gotoAccountSetting.action",'账户设置',600,400);
		}
		
		
	</script>
</head>
<body class="by-main-page" id="byMainPage">
<div class="by-main-page-head navbar navbar-fixed-top" id="byMainPageHead" role="navigation">
    <div class="container-fluid by-nav-wrap">
        <div class="navbar-header by-logo-wraper">
           <a class="navbar-brand" href="#"> 
             	<img alt="logo" id="logo" src="${pageContext.request.contextPath}/appframe/main/images/input-spinner.gif"/>
           </a> 
        </div>
        <div class="hidden-sm hidden-xs">
			<ul class="nav navbar-nav" id="top_menu">
			    <li class="active">
					<a href="${pageContext.request.contextPath}/index.jsp"/> 首页</a>
					<span class="selected"></span>
				</li>
				<c:forEach var="m" items="${modules}">
					<sec:authorize ifAnyGranted="${m.roles}">
						<c:if test="${m.roles!=''}">
							<c:if test="${m.displayOrder==1}">
							<li class="active">
								<a href="#"	onclick="showMenu(this,${m.id})"> ${m.moduleName} </a> 
								<span class="selected"></span>
							</li>
							</c:if>
							<c:if test="${m.displayOrder!=1}">
								<li><a class="dropdown-toggle" data-toggle="dropdown" href="#" onclick="showMenu(this,${m.id})">${m.moduleName}</a><span class="selected"></span></li>
							</c:if>
						</c:if>
						<c:if test="${m.roles==''}">
							<li><a href="#">${m.moduleName}</a></li>
						</c:if>
					</sec:authorize>
				</c:forEach>
				</ul>
		</div>

        <ul class="by-tool-menu nav navbar-nav pull-right">
             <li class="dropdown by-head-notification-item">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown"
                   data-close-others="true">
                    <i class="fa fa-volume-up"></i>
                    <span class="badge" id="displayNum">0</span>
                </a>
                <ul class="dropdown-menu extended notification">
                    <li>
                        <p>您有<span class="by-num" id="total">0</span>条未读消息</p>
                    </li>
                    <li>
                        <a href="javascript:;" onclick="gotoTaskList();">
							<span class="by-num" id="taskNum">0</span> 项待办任务
                        </a>
                    </li>
                    <li>
                        <a href="javascript:;" onclick="gotoCopyrightWarning();">
                           	<span class="by-num" id="copyrightNum">0</span> 条版权预警
                        </a>
                    </li>
                </ul>
            </li>
            <li class="dropdown user">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown"
                   data-close-others="true" >
                    <span class="username">${sessionScope.APP_USER_SESSION_KEY.username }</span>
                    <i class="fa fa-angle-down"></i>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="#" onclick="openAccountSetting();"><i class="fa fa-user"></i> 帐户设置</a></li>
                    <li><a href="#" onclick="updatePassword();"><i class="fa fa-key"></i> 修改密码</a></li>
                    <li class="divider"></li>
                    <li><a href="${path}/j_spring_security_logout"><i class="fa fa-power-off"></i> 退出登录</a></li>
                </ul>
            </li>
            <li class="by-date" id="welcomeInfo">
            </li>
        </ul>
    </div>
    <div class="container-fluid by-sub-nav-wrap" id="sub_menu">
		<c:forEach var="m" items="${modules}"> 
			<sec:authorize ifAnyGranted="${m.roles}">
				<c:if test="${m.dir=='1'}">
					<c:if test="${m.displayOrder==1}">
						<ul class="by-sub-nav clearfix" id="sub_${m.id}">
					</c:if>
					<c:if test="${m.displayOrder!=1}">
						<ul class="by-sub-nav clearfix" id="sub_${m.id}" style="display: none;">
					</c:if>
					<c:forEach var="child" items="${m.children}">
						<sec:authorize ifAnyGranted="${child.roles}">
							<li><a href="#"
								onclick="openMenu(this)" id="${child.id}" url="<c:url value="${child.url}"/>">${child.moduleName}</a>
							</li>
						</sec:authorize>
					</c:forEach>
					</ul>
				</c:if>
			</sec:authorize>
		</c:forEach>   
    </div>
</div>
<div class="by-main-page-body" id="byMainPageBody">
	<iframe id="index_frame" name="index_frame" width="100%" height="100%" frameborder="0" src="main.jsp"></iframe>
</div>

<div class="by-main-page-foot" id="byMainPageFoot">
<!-- 		 <p class="pull-right"><a href="#">回到页顶</a></p> -->
	 <div style="text-align:center">
	     <p>学研平台 | 北京博云易讯科技有限公司</p>
	</div>
</div>
<div class="by-fixed-layer" id="fixedLayer"></div>
<script type="text/javascript" src="${path}/appframe/main/js/main.js"></script>
<script>

</script>
</body>
</html>