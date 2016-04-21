//初始化列表
function initTable(searchParam){
	var resType = searchParam.resType;
	var _columns;//列属性
	
	if(resType!=="" && resType==='1'){//期刊
		_columns = [
			{ "title": "期刊名","data":"metadataMap.magazine"},  
			{ "title": "序号","data":"id"}, 
		    { "title": "国内刊号", "data":'metadataMap.localSerialNumber'},
		    { "title": "国际刊号", "data":'metadataMap.overseasSerialNumber'},
			{ "title": "期刊年份","data":"metadataMap.magazineYear"},  
			{ "title": "当前期数","data":"metadataMap.numOfYear"},  
			{ "title": "总期数","data":"metadataMap.magazineNum"}];
	}else if(resType!=="" && resType==='2'){//文章
		_columns = [
            {"title": "序号","data":"id"}, 
			{"title": "文章标题", "data": "metadataMap.title",
				"render": function(data, type, row) {
					if(data.length>15){
						data = data.substring(0,15)+"...";
					}
                	return data;
            	}
			},
			{"title": "所属期次分类", "data": "metadataMap.wzJournalClass"},
			{"title": "关键词","data": "metadataMap.keywords",
				"render": function(data, type, row) {
					var arr = data.split(",");
					var keywords = "";
					if(arr.length>4){
						for(var i=0; i<4; i++){
							keywords += arr[i]+",";
						}
					}else{
						for(var i=0; i<arr.length; i++){
							keywords += arr[i]+",";
						}
					}
					if(keywords.length > 0){
						keywords = keywords.substring(0,keywords.length)+"..";
					}
                	return data;
            	}
			},
			{"title": "来源","data": "metadataMap.source"},
			{"title": "所属期次期号", "data": "metadataMap.articleOfJul"}];
	}else if(resType!=="" && resType==='3'){//大事记
		_columns = [
            {"title":"序号","data":"id","width":"10%"}, 
    		{"title":"日期","data":"metadataMap.magazineTime","width":"10%"},
    		{"title":"条目内容",
    			"data":"content",
    			"render": function(data, type, row) {
    				if(data.length>60){
    					data = data.substring(0,60)+"...";
    				}
                    return data;
                }
    		},
    		{"title":"所属期刊","data":"metadataMap.magazineNum","width":"20%"}];
	}else if(resType!=="" && resType==='5'){//网页爬虫
		_columns = [
			{ "title":'编号', "data":'sn'},
			{ "title":'标题', "data":'title'},
			{ "title":'来源', "data":'source',
				"render": function(data, type, row) {
					if(row.source){
						return row.source.replace(/来源：/g,"");	
					}else{
						return row.source;	
					}
				}
			},
			{ "title":'发布时间', "data":'postime'},
			{ "title":'状态', "data":'status'},
			{ "title":'采集时间', "data":'createTime'}];
	}
	$.fn.dataTable.ext.errMode = "throw";//出现错误时，抛掉 默认为alert弹出
	var table = queryDataTable(searchParam,_columns);//封装的查询组件
	onclickColumn(table,resType);//列表点击事件
}

//列表点击事件
function onclickColumn(table,resType){
	//点击打开Tab页
	$('.display tbody').on( 'click', 'tr', function (event) {
		//选中样式
		if ( $(this).hasClass('selected') ) {
            $(this).removeClass('selected');
        }else {
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
		//打开tab页面
		if(resType==="5"){
			console.info(table.row( this ).data().url,resType,table.row( this ).data().title,table.row( this ).data().sn);
		}else{
			openTabs(table.row( this ).data().objectId,resType,table.row( this ).data().metadataMap.title);
		}
	} );

}

/**
 * 封装查询组件
 * @param _table
 * @param _ajax
 * @param _columns
 * @returns
 */
function queryDataTable(searchParam,_columns) {
	var table = $("#table_list").DataTable({
		//"retrieve": true,//检索实例
		"destroy": true,//允许重新实例化Datatables
		"language" : {
			"processing" : "正在加载中......",
			"lengthMenu" : "每页 _MENU_ 条",
			"zeroRecords" : "对不起，查询不到相关数据！",
			"emptyTable" : "没数据存在！",
			"info" : "当前显示<font color='green'> _START_ </font>到<font color='green'> _END_ </font>条，共<font color='red'> _TOTAL_ </font>条记录",
			"infoEmpty":"当前显示<font color='red'>0</font>到<font color='red'> 0</font>条，共<font color='red'> 0</font>条记录",
			"infoFiltered" : "数据表中共为 _MAX_ 条记录",
			"search" : "搜索",
			"paginate" : {
				"first" : "首页",
				"previous" : "上一页",
				"next" : "下一页",
				"last" : "末页"
			}
		},
		"processing": true,//显示加载信息
		"serverSide" : true,//开启服务器模式
		"ordering" : false,// 是否使用排序
		"ajax": {
		  	"url": _appPath+"/index/queryResList.action",
		  	"type": "POST",
		  	"data": {"searchParam": JSON.stringify(searchParam)}//额外的需提交到后台的参数
		},
		"columns": _columns
	});
	return table;
}
