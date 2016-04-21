<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.brainsoon.system.support.SystemConstants.ResourceStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>jQuery实现站长之家网站大事记时间轴代码 - 素材家园（www.sucaijiayuan.com）</title>
<link rel="stylesheet" href="css/style.css">
<script type="text/javascript">
	function searchMonthKey(month){
// 		alert(month);
// 		var liLength = $("#eventYear li").length;
// 		 for(var i = 0; i < liLength; i++){
//              var current = $("#eventYear li").eq(i).attr("class");
//              if(current=='current'){
//             	 alert( $("#eventYear label").eq(i).html());
//              }
//          }
		 //隐藏元素
		    $('#month'+month).prevAll('li').slideUp(800);
		 //显示被隐藏元素
			$('#month'+month).slideDown(800).nextAll('li').slideDown(800);
	}
</script>
</head>
<body>
<div class="page">
	<div class="form-inline" align="center" id="searchMonth" style="margin-top: 5px">
		<button type="button" class="btn btn-default" onclick="searchMonthKey(1);"><font color="blue">1月</font></button>
		<button type="button" class="btn btn-default" onclick="searchMonthKey(2);"><font color="blue">2月</font></button>
		<button type="button" class="btn btn-default" onclick="searchMonthKey(3);"><font color="blue">3月</font></button>
		<button type="button" class="btn btn-default" onclick="searchMonthKey(4);"><font color="blue">4月</font></button>
		<button type="button" class="btn btn-default" onclick="searchMonthKey(5);"><font color="blue">5月</font></button>
		<button type="button" class="btn btn-default" onclick="searchMonthKey(6);"><font color="blue">6月</font></button>
		<button type="button" class="btn btn-default" onclick="searchMonthKey(7);"><font color="blue">7月</font></button>
		<button type="button" class="btn btn-default" onclick="searchMonthKey(8);"><font color="blue">8月</font></button>
		<button type="button" class="btn btn-default" onclick="searchMonthKey(9);"><font color="blue">9月</font></button>
		<button type="button" class="btn btn-default" onclick="searchMonthKey(10);"><font color="blue">10月</font></button>
		<button type="button" class="btn btn-default" onclick="searchMonthKey(11);"><font color="blue">11月</font></button>
		<button type="button" class="btn btn-default" onclick="searchMonthKey(12);"><font color="blue">12月</font></button>
	</div>
	<div class="box">
		<ul class="event_year" id="eventYear">
			<li class="current">
			<label for="2013">2013</label></li>
			<li><label id="flag" for="2012">2012</label></li>
			<li><label for="2011">2011</label></li>
			<li><label for="2010">2010</label></li>
			<li><label for="2009">2009</label></li>
			<li><label for="2008">2008</label></li>
			<li><label for="2007">2007</label></li>
			<li><label for="2006">2006</label></li>
			<li><label for="2005">2005</label></li>
			<li><label for="2004">2004</label></li>
			<li><label for="2003">2003</label></li>
			<li><label for="2002">2002</label></li>
		</ul>
		<ul class="event_list" id="eventList">
<!-- 			<div> -->
<!-- 				<h3 id="2013">2013</h3> -->
<!-- 				<li><span>5月</span><p><span>站长之家专栏改版上线</span></p></li> -->
<!-- 				<li><span>4月</span><p><span>站长工具旗下产品，超级监控上线</span></p></li> -->
<!-- 				<li><span>3月</span><p><span>站长之家创业栏目上线</span></p></li> -->
<!-- 			</div> -->
			
<!-- 			<div> -->
<!-- 				<h3 id="2012">2012</h3> -->
<!-- 				<li> -->
<!-- 				<span>9æ</span> -->
<!-- 				<p><span>站长之家北京分公司成立</span></p> -->
<!-- 				</li> -->
<!-- 			</div> -->
			
<!-- 			<div> -->
<!-- 				<h3 id="2011">2011</h3> -->
<!-- 				<li><span>3月13日</span><p><span>建站大师上线</span></p></li> -->
<!-- 				<li><span>3月26日</span><p><span>站长论坛荣获2010年第二届中文论坛100强称号（名列第50位）</span></p></li> -->
<!-- 			</div>	 -->
				
<!-- 			<div> -->
<!-- 			<h3 id="2010">2010</h3> -->
<!-- 				<li><span id="month11">11月1日</span><p><span>第六次全国人 口普查标准时点在今 日零时开始 ， 全国 --> 
<!--  				600 多万名普查 员和普查指导 员走 进 4 亿 多户住 户 ， 查 清查实全 --> 
<!--  				国人 口状况 。 这 次人 口普 查采用按现住地登记的原则。</span></p></li> --> 
<!--  				<li><span id="month11">11月2日</span><p><span>第六次全国人 口普 查工作正在全国展 开。胡锦涛 、吴邦 国、 --> 
<!-- 				温 家宝 、 贾庆林 、 李长春 、 习近 平 、 李克强 、 贺 国强 、 周永康等 --> 
<!-- 				党和国家领导人以普通公民的身份 ， 在北京中南海亲 自参加人 口 --> 
<!-- 				普查登记 ， 或委托家人申报人口普查信息 。</span></p></li>				 --> 
<!-- 				<li><span id="month11">11月3日</span><p><span>第六届藏传佛教高级学衔授予活动在中国藏语系高级佛 -->
<!-- 				学院举行 。</span></p></li> -->
<!-- 				<ul id="month1">1月</ul> -->
<!-- 				<li id="month1"><span>1日</span><p><span>国务院法制办公室发布关于公布修 改 《 中华 人民共和国 -->
<!-- 				职业病防治法》 诊断鉴定制度条文 （ 草案） 公开征求意见的通知 。 -->
<!-- 				△河 北省正式启动西柏坡革命旧址修复工程 。</span></p></li> -->
<!-- 				<li id="month1"><span>2日</span><p><span>国务院法制办公室发布关于公布修 改 《 中华 人民共和国 -->
<!-- 				职业病防治法》 诊断鉴定制度条文 （ 草案） 公开征求意见的通知 。 -->
<!-- 				△河 北省正式启动西柏坡革命旧址修复工程 。</span></p></li> -->
<!-- 				<li id="month1"><span>2-3日</span><p><span>国务院法制办公室发布关于公布修 改 《 中华 人民共和国 -->
<!-- 				职业病防治法》 诊断鉴定制度条文 （ 草案） 公开征求意见的通知 。 -->
<!-- 				△河 北省正式启动西柏坡革命旧址修复工程 。</span></p></li> -->
<!-- 				<ul id="month2">2月</ul> -->
<!-- 				<li id="month2"><span>1日</span><p><span>国务院法制办公室发布关于公布修 改 《 中华 人民共和国 -->
<!-- 				职业病防治法》 诊断鉴定制度条文 （ 草案） 公开征求意见的通知 。 -->
<!-- 				△河 北省正式启动西柏坡革命旧址修复工程 。</span></p></li> -->
<!-- 				<li id="month2"><span>2日</span><p><span>国务院法制办公室发布关于公布修 改 《 中华 人民共和国 -->
<!-- 				职业病防治法》 诊断鉴定制度条文 （ 草案） 公开征求意见的通知 。 -->
<!-- 				△河 北省正式启动西柏坡革命旧址修复工程 。</span></p></li> -->
<!-- 				<li id="month2"><span>2-3日</span><p><span>国务院法制办公室发布关于公布修 改 《 中华 人民共和国 -->
<!-- 				职业病防治法》 诊断鉴定制度条文 （ 草案） 公开征求意见的通知 。 -->
<!-- 				△河 北省正式启动西柏坡革命旧址修复工程 。</span></p></li> -->
<!-- 				<ul id="month3">3月</ul> -->
<!-- 				<li id="month3"><span>1-2日</span><p><span>国务院法制办公室发布关于公布修 改 《 中华 人民共和国 -->
<!-- 				职业病防治法》 诊断鉴定制度条文 （ 草案） 公开征求意见的通知 。 -->
<!-- 				△河 北省正式启动西柏坡革命旧址修复工程 。</span></p></li> -->
<!-- 				<li id="month3"><span>2日</span><p><span>国务院法制办公室发布关于公布修 改 《 中华 人民共和国 -->
<!-- 				职业病防治法》 诊断鉴定制度条文 （ 草案） 公开征求意见的通知 。 -->
<!-- 				△河 北省正式启动西柏坡革命旧址修复工程 。</span></p></li> -->
<!-- 				<li id="month3"><span>2-3日</span><p><span>国务院法制办公室发布关于公布修 改 《 中华 人民共和国 -->
<!-- 				职业病防治法》 诊断鉴定制度条文 （ 草案） 公开征求意见的通知 。 -->
<!-- 				△河 北省正式启动西柏坡革命旧址修复工程 。</span></p></li> -->
<!-- 				<ul id="month11">11月</ul> -->
<!-- 				<li id="month11"><span>4-5日</span><p><span>李克强来到北京市东城区 ， 深入社区和居民家中 ， 考察 -->
<!-- 				人 口普 查登记工作 。</span></p></li>	 -->
<!-- 				<li id="month11"><span>4-6日</span><p><span>国务院发布 《 国务院关于加强法治政府建设的意见》 。</span></p></li>	 -->
<!-- 				<li id="month11"><span>5日</span><p><span>国务院发布的 《 国务院关于加强法治政府建设的意见》 -->
<!-- 				单行本 ， 由人民出版社出版发行 。</span></p></li>	 -->
<!-- 				<ul>12月</ul>	 -->
<!-- 				<li id="month12"><span>1日</span><p><span>国务院发布的 《 国务院关于加强法治政府建设的意见》 -->
<!-- 				单行本 ， 由人民出版社出版发行 。</span></p></li>	 -->
<!-- 				<li id="month12"><span>11日</span><p><span>国务院发布的 《 国务院关于加强法治政府建设的意见》 -->
<!-- 				单行本 ， 由人民出版社出版发行 。</span></p></li>	 -->
<!-- 				<li id="month12"><span>12日</span><p><span>国务院发布的 《 国务院关于加强法治政府建设的意见》 -->
<!-- 				单行本 ， 由人民出版社出版发行 。</span></p></li>					 -->
<!-- 			</div> -->

<!-- 			<div> -->
<!-- 				<h3 id="2009">2009</h3> -->
<!-- 				<li><span>3月20日</span><p><span>举办中国站长站7周年站长调查活动</span></p></li>				 -->
<!-- 				<li><span>3月23日</span><p><span>正式推出IDC行业的B2C平台 - 主机网！</span></p></li> -->
<!-- 				<li><span>11月26日</span><p><span>我的工具发布</span></p></li> -->
<!-- 			</div> -->
		
<!-- 			<div> -->
<!-- 				<h3 id="2008">2008</h3> -->
<!-- 				<li><span>1月8日</span><p><span>站长统计用户数突破60万！</span></p></li> -->
<!-- 				<li><span>4月23日</span><p><span>站长资讯推出v2008新版</span></p></li> -->
<!-- 				<li><span>4月28日</span><p><span>推出软件下载子站-精品软件站</span></p></li> -->
<!-- 			</div> -->

<!-- 			<div> -->
<!-- 				<h3 id="2007">2007</h3> -->
<!-- 				<li><span>3月31日</span><p><span>五周年聚会-千位站长齐聚广州</span></p></li> -->
<!-- 				<li><span>4月7日</span><p><span>五周年聚会安徽合肥站顺利召开</span></p></li> -->
<!-- 				<li><span>9月14日</span><p><span>站长下载栏目改版完成</span></p></li> -->
<!-- 			</div> -->
		
<!-- 			<div> -->
<!-- 				<h3 id="2006">2006</h3> -->
				
<!-- 				<li><span>4月8日</span><p><span>中国站长联盟的免费统计分析服务当日统计量突破4亿次！</span></p></li> -->
<!-- 				<li><span>5月25日</span><p><span>推出素材下载栏目！</span></p></li> -->
<!-- 				<li><span>8月8日</span><p><span>中国站长联盟的免费统计分析服务当日统计量突破5亿次！</span></p></li> -->
<!-- 			</div> -->

<!-- 			<div> -->
<!-- 				<h3 id="2005">2005</h3> -->
<!-- 				<li><span>1月23日</span><p><span>网站论坛帖子突破200万！</span></p></li> -->
<!-- 				<li><span>2月20日</span><p><span>免费域名注册量突破20万！</span></p></li> -->
<!-- 				<li><span>3月10日</span><p><span>站长学院栏目正式开通！</span></p></li> -->
<!-- 			</div> -->
		
<!-- 			<div> -->
<!-- 				<h3 id="2004">2004</h3> -->
<!-- 				<li><span>3月2日</span><p><span>网站论坛帖子突破100万！</span></p></li> -->
<!-- 				<li><span>4月18日</span><p><span>下载栏目提供下载次数突破2000万！</span></p></li> -->
<!-- 				<li><span>4月20日</span><p><span>9126.com免费转向域名系统为用户提供转向次数突破1亿次！</span></p></li> -->
<!-- 			</div> -->

<!-- 			<div> -->
<!-- 				<h3 id="2003">2003</h3> -->
<!-- 				<li><span>3月22日</span><p><span>网站注册用户达到50000</span></p></li> -->
<!-- 				<li><span>4月12日</span><p><span>9126.com免费转向域名系统为站长们提供的转向次数突破2000万！</span></p></li> -->
<!-- 				<li><span>5月13日</span><p><span>下载栏目提供下载次数突破1000万！</span></p></li> -->
<!-- 			</div> -->

<!-- 			<div> -->
<!-- 				<h3 id="2002">2002</h3>				 -->
<!-- 				<li><span>12月28</span><p><span>网站主域名更改为www.sucaijiayuan.com</span></p></li>				 -->
<!-- 				<li><span>12月27日</span><p><span>网站注册用户达到20000</span></p></li> -->
<!-- 				<li><span>11月20日</span><p><span>网站日访问量突破10万!</span></p></li> -->
<!-- 				<li><span>10月21日</span><p><span>免费转向域名栏目开始提供服务</span></p></li> -->
<!-- 			</div> -->
		</ul>	
		<div class="clearfix"></div>
	</div>
</div>
<!-- <script src="./timeLine/js/jquery.1.7.1min.js" type="text/javascript"></script> -->
<script type="text/javascript">
$(function(){
// 	$("#flag").attr("for","180");
	$.ajax({
		url:'${path}/publishRes/test.action',
		type : 'post',
		datatype : 'text',
		async:false,
		success : function(returnValue) {
			if (returnValue != undefined || returnValue != '') {
				var json = eval("("+returnValue+")");
				 $(json).each(function(index) {
				    	var metaData = json[index];
				    	var key = metaData.key;
				    	var value = metaData.value;
				    	 $(value).each(function(index) {
				    		 var deepMetaData = value[index];
				    		 var deepKey = deepMetaData.key;
						     var deepValue = deepMetaData.value;
				    	 });
// 				    	var li = $("<li style=\"width:110px\" rel=\""+key+"\">"+value+"</li>");
// 				    	li.appendTo(ul);
				 });
			}
		}
	});
	$('label').click(function(){
		$('.event_year>li').removeClass('current');
		$(this).parent('li').addClass('current');
		var year = $(this).attr('for');
		//隐藏
		$('#'+year).parent().prevAll('div').slideUp(800);
		//显示
		$('#'+year).parent().slideDown(800).nextAll('div').slideDown(800);
	});
});
</script>
</body>
</html>