/**
 * datagrid最新封装
 * zuohl
 * @returns {DataGrid}
 */
DataGrid = function (){
	this._divId = 'data_div';
	this._url = 'list.action';
	this._pk = 'id';
	this._colums = '';
	this._conditions = '';
	this._sortName = '';
	this._sortOrder = '';
	this._check = '';
	this._onLoadSuccess = '';
	this._frozenColumns = [];
	this._pagination = true; //是否分页
	this._autoRowHeight = false;
	this._singleSelect = false;
	this.dataGridIns;
	this.dataGridObj;
	
	this.setDivId = function(field){
		this._divId = field;
	};
	this.setURL = function(field){
		this._url = field;
	};
	this.setPK = function(field){
		this._pk = field;
	};
	this.setColums = function(field){
		this._colums = field;
	};
	this.setConditions = function(field){
		this._conditions = field;
	};
	this.setSortName = function(field){
		this._sortName = field;
	};
	this.setSortOrder = function(field){
		this._sortOrder = field;
	};
	this.setPagination = function(field){
		this._pagination = field;
	};
	
	//设置选择框，不调用此方法，默认无选择列，参数1 为 选择值，参数2 为  Multiple 、Single （多选、单选）
	this.setSelectBox = function(field,flag){
		this._frozenColumns = [{field:field,checkbox:true}];
		if(typeof(flag) != 'undefined' && flag == 'Single'){
			this._singleSelect = true;
		}
	};
	this.setOnLoadSuccess = function(onload){
		this._onLoadSuccess = onload;
	};
	
	//初始化
	this.initDt = function(){
		this.dataGridObj = $('#'+this._divId);
		//获取div的宽度
		var _width = this.dataGridObj.width();
		var onloadFun = this._onLoadSuccess;
		this.dataGridIns = this.dataGridObj.datagrid({
			width:_width,
			fitColumns: true,   //自动收缩或伸展列宽来保证表格正好充满屏幕
			autoRowHeight:this._autoRowHeight,  //定义如果设置行高基于内容的行。设置为false可以提高加载性能。
			nowrap: false,
			striped: true,  //true有条纹的行
			url:this._url,
			idField:this._pk, //指明哪个字段是一个标识字段
			sortName:this._sortName, //排序
			queryParams:getQueryParams(this._conditions),   //当请求远程数据,发送额外的参数。 
 			sortOrder:this._sortOrder,   //定义了列的排序顺序,只能“asc’或‘desc’
			onLoadSuccess: function (data) {
				if(onloadFun != null && onloadFun != '' && typeof(eval(onloadFun)) == 'function'){
					var call = new selfCallBack(onloadFun,data);
					call.func();
				}
				if(data == null || data.length == 0){  
					alert('未找到数据');
				}
			},
			pageList: [10, 20, 50, 100], //当设置分页属性,初始化页面大小选择列表
			pageSize: 10,  //当设置分页属性,初始化页面大小
			pagination:this._pagination, //true显示分页栏datagrid底。  
			rownumbers:false,//显示序号 
			singleSelect:this._singleSelect,  //True允许选择只有一行
			selectOnCheck:true,  //如果设置为true,点击复选框将永远选择行
			checkOnSelect:false, //如果true,复选框checked/ unchecked当用户单击一个行
			frozenColumns:[this._frozenColumns],  //同列的属性,但这些列将被冻结在左。
			columns:[this._colums]   //datagrid列配置对象,见列属性为更多的细节。
		});
	};
	this.query = function(){
		//清空全选
		this.dataGridObj.datagrid('clearSelections');
		this.dataGridObj.datagrid('uncheckAll');
		this.dataGridObj.datagrid('options').queryParams = getQueryParams(this._conditions);   
		//查询第一页数据     
		this.dataGridObj.datagrid('options').pageNumber = 1;     
		//分页栏跳转到第一页   
		this.dataGridObj.datagrid('getPager').pagination({pageNumber: 1});     
		//this.dataGridObj.datagrid('reload');  
	};
};