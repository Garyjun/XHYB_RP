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
	var chart;
	var chart1;
	var chart2;
	var chart3;
	var array = [];
	$(function() {
		$(document)
				.ready(
						function() {
							chart = new Highcharts.Chart(
									{
										colors : [ "#7cb5ec", "#f7a35c",
												"#90ee7e", "#7798BF",
												"#aaeeee", "#ff0066",
												"#eeaaee", "#55BF3B",
												"#DF5353", "#7798BF", "#aaeeee" ],

										//常规图表选项设置
										chart : {
											renderTo : 'container', //在哪个区域呈现，对应HTML中的一个元素ID
											type : 'pie',
											plotBackgroundColor : null, //绘图区的背景颜色
											plotBorderWidth : null, //绘图区边框宽度
											plotShadow : false, //绘图区是否显示阴影       
											options3d : {
												enabled : true,
												alpha : 60,
												beta : 0
											}

										},

										//图表的主标题
										title : {
											text : '西方佛界实例排名'
										},
										//当鼠标经过时的提示设置
										tooltip : {
											pointFormat : '{series.name}: <b>{point.percentage}%</b>',
											percentageDecimals : 1
										},
										//每种图表类型属性设置
										plotOptions : {
											//饼状图
											pie : {
												allowPointSelect : true,
												cursor : 'pointer',
												dataLabels : {
													enabled : true,
													color : '#000000',
													connectorColor : '#000000',
													depth : 35,
													formatter : function() {
														//Highcharts.numberFormat(this.percentage,2)格式化数字，保留2位精度
														return '<b>'
																+ this.point.name
																+ '</b>: '
																+ this.percentage
																+ ' %';

													}
												}
											}
										},
										//图表要展现的数据
										series : [ {
											type : 'pie',
											name : '西方佛界',
											data : ''
										} ]
									});
						});

		//异步请求数据
		$.ajax({
			type : "post",
			url : '${path}/system/sysMonitor/query.action',//提供数据的Servlet
			success : function(data) {
				//定义一个数组
				//迭代，把异步获取的数据放到数组中
				browsers = [];
				$.each(JSON.parse(data), function(i, d) {
					browsers.push([ d.name, Number(d.pecent) ]);
				});
				var json = JSON.parse(data);
				for(var i=0;i<json.length;i++) {
				   var jstr = json[i];
				   var spn = $("<span></span>");
				   spn.text(jstr.name+":"+jstr.pecent+"%");
				   spn.appendTo($("#content"));
				   var br = $("<br/>");
				   br.appendTo($("#content"));
				};
				//设置数据
				chart.series[0].setData(browsers);
				chart1.series[0].setData(browsers);
				chart2.series[0].setData(browsers);
				//chart3.series[0].setData(browsers);
			},
			error : function() {
				alert("请求超时，请重试！");
			}
		});

		$(document)
				.ready(
						function() {
							chart1 = new Highcharts.Chart(
									{
										//常规图表选项设置
										chart : {
											renderTo : 'container1', //在哪个区域呈现，对应HTML中的一个元素ID
											plotBackgroundColor : null, //绘图区的背景颜色
											plotBorderWidth : null, //绘图区边框宽度
											plotShadow : false
										//绘图区是否显示阴影            
										},

										//图表的主标题
										title : {
											text : '西方佛界实例排名'
										},
										//当鼠标经过时的提示设置
										tooltip : {
											pointFormat : '{series.name}: <b>{point.percentage}%</b>',
											percentageDecimals : 1
										},
										//每种图表类型属性设置
										plotOptions : {
											//饼状图
											pie : {
												allowPointSelect : true,
												cursor : 'pointer',
												dataLabels : {
													enabled : true,
													color : '#000000',
													connectorColor : '#000000',
													formatter : function() {
														//Highcharts.numberFormat(this.percentage,2)格式化数字，保留2位精度
														return '<b>'
																+ this.point.name
																+ '</b>: '
																+ this.percentage
																+ ' %';

													}
												}
											}
										},
										//图表要展现的数据
										series : [ {
											type : 'pie',
											name : '西方佛界',
											data : ''
										} ]
									});
						});

		$(document)
				.ready(
						function() {
							chart2 = new Highcharts.Chart(
									{
										//常规图表选项设置
										chart : {
											renderTo : 'container2', //在哪个区域呈现，对应HTML中的一个元素ID
											plotBackgroundColor : null, //绘图区的背景颜色
											plotBorderWidth : null, //绘图区边框宽度
											plotShadow : false
										//绘图区是否显示阴影            
										},

										//图表的主标题
										title : {
											text : '西方佛界实例排名',
											style : {
												color : '#FF00FF',
												fontWeight : 'bold'
											}

										},
										//当鼠标经过时的提示设置
										tooltip : {
											pointFormat : '{series.name}: <b>{point.percentage}%</b>',
											percentageDecimals : 1
										},
										//每种图表类型属性设置
										plotOptions : {
											//饼状图
											pie : {
												allowPointSelect : true,
												cursor : 'pointer',
												dataLabels : {
													enabled : true,
													color : '#000000',
													connectorColor : '#000000',
													formatter : function() {
														//Highcharts.numberFormat(this.percentage,2)格式化数字，保留2位精度
														return '<b>'
																+ this.point.name
																+ '</b>: '
																+ this.percentage
																+ ' %';

													}
												}
											}
										},
										//图表要展现的数据
										series : [ {
											type : 'pie',
											name : '西方佛界',
											data : ''
										} ]
									});
						});

/* 		$(document)
				.ready(
						function() {
							chart3 = new Highcharts.Chart(
									{
										//常规图表选项设置
										chart : {
											renderTo : 'container3', //在哪个区域呈现，对应HTML中的一个元素ID
											plotBackgroundColor : null, //绘图区的背景颜色
											plotBorderWidth : null, //绘图区边框宽度
											plotShadow : false
										//绘图区是否显示阴影            
										},

										//图表的主标题
										title : {
											text : '西方佛界实例排名'
										},
										//当鼠标经过时的提示设置
										tooltip : {
											pointFormat : '{series.name}: <b>{point.percentage}%</b>',
											percentageDecimals : 1
										},
										//每种图表类型属性设置
										plotOptions : {
											//饼状图
											pie : {
												allowPointSelect : true,
												cursor : 'pointer',
												dataLabels : {
													enabled : true,
													color : '#000000',
													connectorColor : '#000000',
													formatter : function() {
														//Highcharts.numberFormat(this.percentage,2)格式化数字，保留2位精度
														return '<b>'
																+ this.point.name
																+ '</b>: '
																+ this.percentage
																+ ' %';

													}
												}
											}
										},
										//图表要展现的数据
										series : [ {
											type : 'pie',
											name : '西方佛界',
											data : ''
										} ]
									});
						}); */
						
						
						
						
		$(document)
		.ready(
				function() {
					chart3 = new Highcharts.Chart(
							{
								  chart: {
								        type: 'gauge',
								        renderTo : 'container3',
								        plotBackgroundColor: null,
								        plotBackgroundImage: null,
								        plotBorderWidth: 0,
								        plotShadow: false
								    },
								    
								    title: {
								        text: 'Cpu'
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
								        max: 200,
								        
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
								            text: '%'
								        },
								        plotBands: [{
								            from: 0,
								            to: 120,
								            color: '#55BF3B' // green
								        }, {
								            from: 120,
								            to: 160,
								            color: '#DDDF0D' // yellow
								        }, {
								            from: 160,
								            to: 200,
								            color: '#DF5353' // red
								        }]        
								    },
								
								    series: [{
								        name: 'Cpu%',
								        data: [80],
								        tooltip: {
								            valueSuffix: '%'
								        }
								    }]
							},	function (chart3) {
								if (!chart3.renderer.forExport) {
								    setInterval(function () {
								        var point = chart3.series[0].points[0],
								            newVal,
								            inc = Math.round((Math.random() - 0.5) * 20);
								        
								        newVal = point.y + inc;
								        if (newVal < 0 || newVal > 200) {
								            newVal = point.y - inc;
								        }
								        
								        point.update(newVal);
								        
								    }, 3000);
								};
							});
				}); 
				
						
							

	});
	 
	
	
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap "
		style="height: 100%;">
		<div class="row">
			<div class="col-md-3" >
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title"><b>系统详细信息情况如下:</b></h3>
					</div>
					<div id="content" class="panel-body"></div>
				</div>
			</div>
			
			
			
			
			
			
			<div class="col-md-9">
				<div class="row">
					<div class="col-md-4" id="container"
						style="width: 400px; height: 200px"></div>
					<div class="col-md-4" id="container1"
						style="width: 400px; height: 200px"></div>

				</div>
				<div class="row">
					<div class="col-md-4" id="container2"
						style="width: 400px; height: 200px"></div>
					<div class="col-md-6" id="container3"
						style="width: 400px; height: 200px"></div>
				</div>
			</div>
		</div>
	</div>

</body>

</html>
