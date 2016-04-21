var ztreeSetting = {
	async : {
		enable : true,
		url : _appPath + "/index/generateNavTree.action",
		autoParam : [ "id", "pValue", "value" ]
	},
	callback : {
		onClick : onClick
	}
};

function onClick(event, treeId, treeNode, clickFlag) {
	var nodes = treeNode.getPath();
	var path = "";
	$(nodes).each(function(i,e){
		path += "|" + e.name + "(" + e.type + ")";
	})
	   var searchParam;
	   if(path.indexOf("期刊")!=-1){
		   searchParam = {resType:"0",columns:[//期刊
       			{key:"magazine",value:"期刊名"},
       			{key:"localSerialNumber",value:"国内刊号"},
       			{key:"overseasSerialNumber",value:"国际刊号"},
       			{key:"magazineYear",value:"期刊年份"},
       			{key:"numOfYear",value:"当前期数"},
       			{key:"magazineNum",value:"总期数"}]
       		};
	   }else  if(path.indexOf("爬虫")!=-1){
		   searchParam = {resType:"4",channel:"2",columns:[//网页爬虫
	   			{key:"sn",value:"编号"},
	   			{key:"title",value:"标题"},
	   			{key:"source",value:"来源"},
	   			{key:"postime",value:"发布时间"},
	   			{key:"status",value:"状态"},
	   			{key:"createTime",value:"采集时间"}]
	   	   };
	   }else  if(path.indexOf("文章")!=-1){
		   searchParam = {resType:"1",columns:[//文章
	               {key:"title",value:"文章标题"},
	               {key:"wzJournalClass",value:"来源"},
	               {key:"keywords",value:"关键词"},
	               {key:"source",value:"所属期次分类"},
	               {key:"articleOfJul",value:"所属期次期号"}]
	   		};
	   }else  if(path.indexOf("大事辑览")!=-1){
		   searchParam = {resType:"2",columns:[//大事辑览
   			  {key:"magazineTime",value:"日期"},
   			  {key:"content",value:"条目内容"},
   			  {key:"magazineNum",value:"所属期刊"}]
   		   };
	   }
	initTable(searchParam);
	
	console.log(path);
}