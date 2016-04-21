<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<script type="text/javascript"
	src="${path}/appframe/plugin/jquery/jquery-1.9.1.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/Highcharts/js/highcharts.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/Highcharts/js/highcharts-more.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/Highcharts/js/highcharts-3d.js"></script>
<script type="text/javascript"
	src="${path}/appframe/plugin/Highcharts/js/modules/exporting.js"></script>
<script>
	$(document).ready(function() {
		//异步请求数据
		$.ajax({
			type : "post",
			url : '${path}/system/sysMonitor/queryServerInfo.action?linkInfo='+$('#linkInfo').val(),//提供数据的Servlet
			beforeSend: function () {
		        $("#loading").show();
		    },		
			success : function(data) {
				getPieData(data);
			},
			complete: function () {
		        $("#loading").hide();
		    },
			error : function() {
				//alert("请求超时，请重试！");
			}
		});
	});
	
	function getPieData(data) {
		if(data == "" || data == null || data == undefined){
			$.alert("请在数据字典中配置服务器信息，并在服务器指定位置放置执行脚本！");
			return;
		}
		var json = JSON.parse(data);
		
		var i=0;
		var title="";
		for(var key in json){
			var value = json[key];
			if(key=="cpu_us"){
				title="CPU使用率";
			}else if(key=="disk_sda3"){
				title="硬盘使用率";
			}else if(key=="load"){
				title="负载";
			}else if(key=="mem"){
				title="内存使用率";
			}else if(key=="swap"){
				title="Swap使用率";
			}else{
				continue;
			}
				
			var container = $("<div class='col-md-6' id='container"+i+"' style='min-width:450px;height:300px;float:left;'></div>");
			container.appendTo($("#pieHome"));
			
			$('#container'+i+'').highcharts({
				
			    chart: {
			        type: 'gauge',
			        plotBackgroundColor: null,
			        plotBackgroundImage: null,
			        plotBorderWidth: 0,
			        plotShadow: false
			    },
			    
			    title: {
			        text: title
			    },
			    
			    pane: {
			        startAngle: -150,
			        endAngle: 150,
			        background: [{
			            backgroundColor: {
			                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
			                stops: [
			                    [0, '#FFF'],
			                    [1, '#333']
			                ]
			            },
			            borderWidth: 0,
			            outerRadius: '109%'
			        }, {
			            backgroundColor: {
			                linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
			                stops: [
			                    [0, '#333'],
			                    [1, '#FFF']
			                ]
			            },
			            borderWidth: 1,
			            outerRadius: '107%'
			        }, {
			            // default background
			        }, {
			            backgroundColor: '#DDD',
			            borderWidth: 0,
			            outerRadius: '105%',
			            innerRadius: '103%'
			        }]
			    },
			       
			    // the value axis
			    yAxis: {
			        min: 0,
			        max: 100,
			        
			        minorTickInterval: 'auto',
			        minorTickWidth: 1,
			        minorTickLength: 10,
			        minorTickPosition: 'inside',
			        minorTickColor: '#666',
			
			        tickPixelInterval: 30,
			        tickWidth: 2,
			        tickPosition: 'inside',
			        tickLength: 10,
			        tickColor: '#666',
			        labels: {
			            step: 2,
			            rotation: 'auto'
			        },
			        title: {
			            text: '百分比'
			        },
			        plotBands: [{
			            from: 0,
			            to: 60,
			            color: '#55BF3B' // green
			        }, {
			            from: 60,
			            to: 80,
			            color: '#DDDF0D' // yellow
			        }, {
			            from: 80,
			            to: 100,
			            color: '#DF5353' // red
			        }]        
			    },
			
			    series: [{
			        name: '使用率',
			        data: [value*1],
			        tooltip: {
			            valueSuffix: ' %'
			        }
			    }]
			
			});
			i++;
		}
		
		
	}
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap " style="height: 100%;">
		<div class="panel panel-default">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">服务器信息统计</a></li>
				</ol>
			</div>
			<div class='row' id="pieHome"></div>
		</div>
	</div>
<input id="linkInfo" type="hidden" value="<%=request.getParameter("linkInfo")%>"/>
</body>

</html>
