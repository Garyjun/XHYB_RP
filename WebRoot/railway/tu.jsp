<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/appframe/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>标引详细</title>
<script type="text/javascript" src="${path}/railway/js/esl.js"></script>
<script type="text/javascript" src="${path}/railway/js/echarts.js"></script>
</head>
<body>
	<!--Step:1 为ECharts准备一个具备大小（宽高）的Dom-->
	<div id="main"
		style="height: 100%; border: 1px solid #ccc; margin: 0 auto; text-align: right;"></div>
	<!--  <div id="mainMap" style="height:400px;border:1px solid #ccc;margin:0 auto;"></div>
    <div id="mainPie" style="height:400px;border:1px solid #ccc;margin:0 auto;"></div>
    <div id="mainLine" style="height:400px;border:1px solid #ccc;margin:0 auto;"></div> -->

	<!--Step:2 引入echarts-all.js-->
	<script src="js/echarts-all.js"></script>

	<script type="text/javascript">
		// Step:3 echarts和zrender被echarts-plain.js写入为全局接口
		 $(function(){
			DrawEChart("");
		});  
		 require(
        [
            'echarts',
            'echarts/chart/force' 
        ],
        DrawEChart//异步加载的回调函数绘制图表
        );
		var myChart;
		 function DrawEChart(ec){
			var op;
			 myChart = echarts.init(document.getElementById('main'));

			op = {
				/* title : {
					text: '可视化图表',
					subtext : '数据来自伟大的数据库',
					x : 'right',
					y : 'bottom'
				}, */
				tooltip : {
					trigger : 'item',
					formatter : '{a} : {b}'
				},
				toolbox : {
					show : true,
					feature : {
						restore : {
							show : true
						},
						magicType : {
							show : true,
							type : [ 'force', 'chord' ]
						},
						saveAsImage : {
							show : true
						}
					}
				},
				legend : {
					x : 'left',
					data : [ '包含', '依赖','上位词' ]
				},
				series : [ {
					type : 'force',
					name : "关系",
					ribbonType : false,
					categories : [ {
						name : '人物'
					},{
						name : '包含'
					}, {
						name : '依赖'
					}, {
						name : '上位词'
					}],
					itemStyle : {
						normal : {
							label : {
								show : true,
								textStyle : {
									color : '#333'
								}
							},
							nodeStyle : {
								brushType : 'both',
								borderColor : 'rgba(255,215,0,0.4)',
								borderWidth : 1
							},
							linkStyle : {
								type : 'line'  //line直线  curve曲线
							}
						},
						emphasis : {
							label : {
								show : false
							// textStyle: null      // 默认使用全局文本样式，详见TEXTSTYLE
							},
							nodeStyle : {
							//r: 30
							},
							linkStyle : {}
						}
					},
					useWorker : false,
					minRadius : 15,
					maxRadius : 25,
					gravity : 1.1,
					scaling : 1.1,
					roam : 'move',

					//调用ajax，并循环下面代码，复制，解析json
					nodes : [
					  {category:0, name: '乔布斯', value : 10, label: '乔布斯\n（主要）'},
					 {category:1, name: '丽萨-乔布斯',value : 2},
					 {category:1, name: '保罗-乔布斯',value : 3},
					 {category:1, name: '克拉拉-乔布斯',value : 3},
					 {category:1, name: '劳伦-鲍威尔',value : 7},
					 {category:2, name: '史蒂夫-沃兹尼艾克',value : 5},
					 {category:2, name: '奥巴马',value : 8},
					 {category:2, name: '比尔-盖茨',value : 9},
					 {category:2, name: '乔纳森-艾夫',value : 4},
					 {category:2, name: '蒂姆-库克',value : 4},
					 {category:2, name: '龙-韦恩',value : 1},   
					],
					links : [
					   {source : '丽萨-乔布斯', target : '乔布斯', weight : 1, name: '女儿'},
					{source : '保罗-乔布斯', target : '乔布斯', weight : 2, name: '父亲'},
					{source : '克拉拉-乔布斯', target : '乔布斯', weight : 1, name: '母亲'},
					{source : '劳伦-鲍威尔', target : '乔布斯', weight : 2},
					{source : '史蒂夫-沃兹尼艾克', target : '乔布斯', weight : 3, name: '合伙人'},
					{source : '奥巴马', target : '乔布斯', weight : 1},
					{source : '比尔-盖茨', target : '乔布斯', weight : 6, name: '竞争对手'},
					{source : '乔纳森-艾夫', target : '乔布斯', weight : 1, name: '爱将'},
					{source : '蒂姆-库克', target : '乔布斯', weight : 1},
					{source : '龙-韦恩', target : '乔布斯', weight : 1},
					{source : '克拉拉-乔布斯', target : '保罗-乔布斯', weight : 1},
					{source : '奥巴马', target : '保罗-乔布斯', weight : 1},
					{source : '奥巴马', target : '克拉拉-乔布斯', weight : 1},
					{source : '奥巴马', target : '劳伦-鲍威尔', weight : 1},
					{source : '奥巴马', target : '史蒂夫-沃兹尼艾克', weight : 1},
					{source : '比尔-盖茨', target : '奥巴马', weight : 6},
					{source : '比尔-盖茨', target : '克拉拉-乔布斯', weight : 1},
					{source : '蒂姆-库克', target : '奥巴马', weight : 1}  
					]
				} ]
			};
			myChart.setOption(op);
			query();
			  
		};
		function query(){
			var op = myChart.getOption();
			$.ajax({
				type : "post",
				asyns : false,
				url : '${path}/railway/gotoquery2.action',
				success : function(data) {
					//alert(data);
					//console.info(data);
					var json = $.parseJSON(data);
					//alert(json[0].length);
					   /* for(var i=0;i<json.length;i++){
						for(var j=0;j<json[i].length;j++){
							if(json[i][j].narrower!=null){
								op.series[0].nodes.push({category:json[i][j].size,name:json[i][j].narrower[j].name});
							}else{
								op.series[0].nodes.push({category:json[i][j].size,name:json[i][j].name});
							}
							
							if(json[i][j].narrower!=null){
								op.series[0].links.push({source:0,target :json[i][j].narrower[j].name,weight:6,name:json[i][j].sort});
							}else{
								op.series[0].links.push({source:0,target :json[i][j].name,weight:6,name:json[i][j].sort});
							}
							myChart.setOption(op);
						}
					}     */
					for(var i=0;i<json.length;i++){
						for(var j=0;j<json[i].length;j++){
						op.series[0].nodes.push({category:json[i][j].size,name:json[i][j].sname,value:json[i][j].va});
						op.series[0].links.push({source:json[i][j].zname,target:json[i][j].sname,weight:4,name:json[i][j].sort});
					}
					}
					myChart.setOption(op);
				}
			}); 
		};
	</script>
</body>
</html>