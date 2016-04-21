<%@page import="com.brainsoon.system.support.SystemConstants.ResourceStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>素材资源统计</title>
<script type="text/javascript" src="${path}/appframe/plugin/Highcharts/js/highcharts.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/Highcharts/js/modules/exporting.js"></script>
	<script type="text/javascript" src="${path}/appframe/plugin/Highcharts/js/highcharts-more.js"></script>
<script type="text/javascript">
$(function () {
	getPieData();
	var div = $('#data_div');
	$.ajax({
		url:'${path}/statistics/bookNum/sucaiNumTab.action',
		dataType:'text',
		success:function(data){
			var tableList = $(data);
			tableList.appendTo(div);
		}
	});
  /*   $('#container').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: '素材资源分类统计',
            y:10
        },
        tooltip: {
    	    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                }
            }
        },
        series: [{
            type: 'pie',
            name: '制作分类',
            data: [
                ['二维',   45.0],
                ['三维',       25.8],
                {
                    name: '实拍',
                    y: 16.4,
                    sliced: true,
                    selected: true
                },
                ['虚拟',    8.5]
            ]
        }]
    }); */
});


function getPieData() {
	//饼状图定义
	$.ajax({
		url:'${path}/statistics/bookNum/sucaiNum.action',
		dataType: "json",
		success: function(data){
			var d = [];
			$(data).each(function(n,item){
		        d.push([item.names,item.counts]);
		       })
		       if(d.length!=0){
				chart = new Highcharts.Chart(
					{
						//常规图表选项设置
						chart : {
							renderTo : 'test', //在哪个区域呈现，对应HTML中的一个元素ID
							plotBackgroundColor : null, //绘图区的背景颜色
							plotBorderWidth : null, //绘图区边框宽度
							plotShadow : false
						//绘图区是否显示阴影            
						},

						 //图表的主标题
						title : {
							text: '',
				            y:10
						}, 
						//当鼠标经过时的提示设置
						tooltip : {
							//pointFormat : '<b>{point.name}</b>: {point.percentage:.1f} %',
							/*  formatter: function() {
   							 return '<b>'+ this.point.name +'</b>: ('+
                 				Highcharts.numberFormat(this.y, 0, ',') +' 个)';
   							 }, */
							percentageDecimals : 1
						},
						//每种图表类型属性设置
						plotOptions : {
							//饼状图
							pie : {
								size:200,
								allowPointSelect : true,
								cursor : 'pointer',
								dataLabels : {
									enabled : true,
									color : '#000000',
									connectorColor : '#000000',
									//format:'<b>{point.name}</b>: {point.percentage:.1f} %'
									formatter:  function() {
	           							 return '<b>'+ this.point.name +'</b>: ('+
	                         				Highcharts.numberFormat(this.y, 0, ',') +' 个)';
	           							 }
								},
								showInLegend: true			//显示图标
							}
						},
						//图表要展现的数据
						series : [ {
							type : 'pie',
							name : '数量',
							data :''
						} ]
					});
				chart.series[0].setData(d);
		       }else{
				$('#test').append("<center style=\"margin-top: 30%;\">无数据</center>");
			} 
		  }
	});
  }
</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="width: 85%">
		<div class="panel panel-default" style="height: 100%;">
			<div class="panel-heading" id="div_head_t">
				<ol class="breadcrumb">
					<li><a href="javascript:;">素材资源统计</a></li>
				</ol>
			</div>
			<div class="col-md-12" style="height: 85%">
         		<div class="row" style="height: 100%">
          			<div class="col-md-6" style="height: 100%">
              			<div id="test" style="height: 100%"></div>
              		</div>
              		<div class="col-md-6" style="height: 100%;">
       					<div id="data_div" class="data_div height_remain" style="width: 100%;height: 100%; margin-top: 27%; margin-left: 20%" ></div>
  					</div>
          	</div>
		</div>	
	</div>
</div> 
</body>
</html>