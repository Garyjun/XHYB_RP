<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>选择列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript" src="${path}/appframe/util/download.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript">
		var publishType = '${param.publishType}';
		var path = '${param.path}';
		var fieldName = '${param.fieldName}';
		var startTime = '${param.startTime}';
		var endTime = '${param.endTime}';
		$(function(){
			//定义一datagrid
	   		var _divId = 'data_div';
	   		var _url = '${path}/publishRes/openList.action?sysResMetadataTypeId='+publishType+'&path='+path+'&fieldName='+fieldName+'&startTime='+startTime+'&endTime='+endTime;
	   		var _pk = 'objectId';
	   		var _conditions = [<app:QueryConditionTag   />,'status','publishType'];
	   		var _sortName = 'createTime';
	   		var _sortOrder = 'desc';
			var _colums = [
							<app:QueryListColumnTag   />
							{field:'createTime',title:'创建日期',width:fillsize(0.10),align:'center',formatter:$operated},
							{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}];
			accountHeight();
	   		$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder);
		});
		/***操作列***/
		$operate = function(value,rec){
			var optArray = new Array();
			optArray.push("<a class=\"btn hover-red\" href=\"javascript:detail('"+rec.objectId+"')\" ><i class=\"fa fa-sign-out\"></i> 详细</a>");
			return createOpt(optArray);
		}
		
		$operated = function(value){
			var ddate = new Date();
			ddate.setTime(value);
			var newTime = ddate.format('yyyy-MM-dd HH:mm:ss');
			return newTime;
		}
		
		/***查看***/
		function detail(objectId){
			window.location.href = "${path}/publishRes/openDetail.action?objectId="+objectId+"&publishType=${param.publishType}"+"&searchFlag=close"+"&flagSta=1"+'&path='+path+'&fieldName='+fieldName+'&startTime='+startTime+'&endTime='+endTime+"&returnBack=1";
	//		$.openWindow("${path}/bres/openDetail.action?libType=${param.libType}&objectId="+objectId,'资源详细',800,450);
		}
		
		Date.prototype.format=function(fmt) {        
		    var o = {        
		    "M+" : this.getMonth()+1, //月份        
		    "d+" : this.getDate(), //日        
		    "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //小时        
		    "H+" : this.getHours(), //小时        
		    "m+" : this.getMinutes(), //分        
		    "s+" : this.getSeconds(), //秒        
		    "q+" : Math.floor((this.getMonth()+3)/3), //季度        
		    "S" : this.getMilliseconds() //毫秒        
		    };        
		    var week = {        
		    "0" : "\u65e5",        
		    "1" : "\u4e00",        
		    "2" : "\u4e8c",        
		    "3" : "\u4e09",        
		    "4" : "\u56db",        

		    "5" : "\u4e94",        
		    "6" : "\u516d"       
		    };        
		    if(/(y+)/.test(fmt)){        
		        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));        
		    }        
		    if(/(E+)/.test(fmt)){        
		        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "\u661f\u671f" : "\u5468") : "")+week[this.getDay()+""]);        
		    }        
		    for(var k in o){        
		        if(new RegExp("("+ k +")").test(fmt)){        
		            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));        
		        }        
		    }        
		    return fmt;        
		} 
	</script>
</head>
<body class="win-dialog">
<div class="form-wrap">
<div class="panel-body height_remain">
	<div id="data_div" class="data_div height_remain" style="width: 965px;"></div>
</div>
</div>
</body>
</html>