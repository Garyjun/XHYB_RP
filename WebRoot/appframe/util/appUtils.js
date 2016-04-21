function getRootPath(){
    //获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
    var curWwwPath=window.document.location.href;
    //获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
    var pathName=window.document.location.pathname;
    var pos=curWwwPath.indexOf(pathName);
    //获取主机地址，如： http://localhost:8083
    var localhostPaht=curWwwPath.substring(0,pos);
    //获取带"/"的项目名，如：/uimcardprj
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    return(localhostPaht+projectName);
}
(function($) {
	$.extend({
		/**
		 *  计算字符串的长度，中文算二个
		 * @param str=计算字符
		 * @return 字符串的长度
		 */
		strLen:function(str){
			if(str !=null && str!=""){
				return str.replace(/[^\x00-\xff]/g,"xx").length;
			}else{
				return "";
			}
		},
		/**
		* 浮动DIV定时显示提示信息,如操作成功, 失败等
		* @param string tips (提示的内容)
		* @param int time 显示的时间(按秒算), time > 0
		* @sample <a href="javascript:void(0);" onclick="showTips( '操作成功', 3 );">点击</a>
		* @sample 上面代码表示点击后显示操作成功3秒钟, 距离顶部0px
		*/
		showTips:function(tips,time,code){
			if(tips != null && tips != "" && typeof(tips) != 'undefined'){
				var windowWidth = window.document.documentElement.clientWidth;
				var tipsDiv = '<div class="tipsClass">' + tips + '</div>';
				$('body').append( tipsDiv );
				var offsetTop =  $(window).scrollTop() +"px";
				var bgColor = '#8FBC8F';
				if(typeof(code) != 'undefined' && (code == '2014' || code == '')){
					bgColor = 'yellow';
				}
				$('div.tipsClass').css({
					'top' : offsetTop,
					'left' : ( windowWidth / 2 ) - ( $.strLen(tips) * 4 / 2 ) + 'px',
					'position' : 'absolute',
					'padding' : '3px 5px',
					'background': bgColor,
					'font-size' : 12 + 'px',
					'margin' : '0 auto',
					'text-align': 'center',
					'width' : 'auto',
					'color' : 'black',
					'opacity' : '0.8',
					'min-width' : '200px',
					'z-index' : '9999999999999'
				}).show();
				var isRemove = true;
				$('div.tipsClass').mouseover(function(){
					$('div.tipsClass').show();
					isRemove = false;
				}).mouseout(function(){$('div.tipsClass').fadeOut();$('div.tipsClass').remove();});
				$("div.tipsClass").pulsate({color:"#09f"});
				$("div.tipsClass").center();
				//$("div.tipsClass").corner();
				setTimeout( function(){if(isRemove){$('div.tipsClass').fadeOut();$('div.tipsClass').remove();}}, ( time * 1000 ) );
			}
		},
		isEmpty:function(value){
			if(typeof(value) == 'undefined')
				return true;
			value = value.replace(' ','');
			if(value == '')
				return true;
			return false;
		},
		/**
		 * 列表组件，封装查询
		 * @param _divId
		 * @param _url 查询地址
		 * @param _pk 主键
		 * @param _colums 查询列
		 * @param _conditions [] 查询条件 ,指的的input的id，拼接的时候获取此元素的name
		 * @param _sortName 排序列
		 * @param _sortOrder 排序顺序 asc desc
		 * @param _showCheck 是否显示勾选
		 * @param _pagination 是否分页
		 * @param _autoRowHeight 自动高度
		 * @param _onLoadSuccess 加载成功回调事件
		 * @param _frozenColumns 确定列的数量被冻结在数据表视图中的表
		 * @param _showMoreMenu 是否显示更多菜单
		 * @param _buttons 自定义工具栏： array
		 */
		datagrid:function(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_showCheck,_pagination,_autoRowHeight,_onLoadSuccess,_frozenColumns,_showMoreMenu,_buttons){
			if(_divId == null || _divId == "" || typeof(_divId) == 'undefined'){
				_divId = "data_div";
			}
			$data = $('#'+_divId);
			//首先给此div加一个标识
			$data.addClass('datagrid_mark');
			accountHeight();
			if(typeof(_pagination) == 'undefined'){
				_pagination = true;
			}

			//不传入本参数 或者 =true
			if(_showCheck == undefined || _showCheck == true){
				if(typeof(_frozenColumns) == 'undefined' || _frozenColumns == '' ||  _frozenColumns){
					_frozenColumns = [{field:_pk,checkbox:true,sortable:true}];
				}
			}else if(_showCheck == false){
				_frozenColumns = [];
			}else{
				$.alert("ERROR：表格参数<_showCheck>传入有误，只能为【true】 or 【false】");
			}

			if(typeof(_autoRowHeight) == 'undefined'){
				_autoRowHeight = false;
			}
			if(typeof(_showMoreMenu) == 'undefined'){
				_showMoreMenu = true;
			}
			//获取div的宽度
			var _width = $data.width();

			var _this = $data.datagrid({
				title:'', 
				width:_width,
				collapsible:true,//是否可折叠的 
				fitColumns: true,
				autoRowHeight:_autoRowHeight,
				nowrap: false,striped: true,
				url:_url,
				idField:_pk,
				sortName:_sortName, //排序
				queryParams:getQueryParams(_conditions),
	 			sortOrder:_sortOrder,
				onLoadSuccess: function (data) {
					//setTimeout(_onLoadSuccess(), 10);
					if(typeof(_onLoadSuccess) != 'undefined' && _onLoadSuccess != ''){
						var call = new selfCallBack(_onLoadSuccess);
						call.func();
					}
					if(data.length == 0){
						alert('未找到数据');
					}
				},
				pageList: [10, 20, 50, 100],
				pageSize: 10,
				pagination:_pagination, //添加分页
				rownumbers:false,//显示序号
				singleSelect:true,
//				toolbar:[{  
//                    text:'二次查询' ,  
//                    iconCls:'icon-add' ,  
//                    handler:function(){  
//                        alert('add' )  
//                    }  
//                },{  
//                    text:'Cut' ,  
//                    iconCls:'icon-cut' ,  
//                    handler:function(){  
//                        alert('cut' )  
//                    }  
//                },'-' ,{  
//                    text:'Save' ,  
//                    iconCls:'icon-save' ,  
//                    handler:function(){  
//                        alert('save' )  
//                    }  
//                }],  
				selectOnCheck:false,
				checkOnSelect:false,
				showMoreMenu:_showMoreMenu,
				frozenColumns:[_frozenColumns],
				columns:[_colums]
			});
			
			//设置分页控件 
		    var p = $('#' + _divId).datagrid('getPager'); 
		    $(p).pagination({ 
//		        pageSize: 10,//每页显示的记录条数，默认为10 
//		        pageList: [10, 20, 50, 100],,//可以设置每页记录条数的列表 
		    	buttons:_buttons
//		        [{    
//			        iconCls:'icon-search',    
//			        handler:function(){    
//			            eval(add());    
//			        }    
//			    }
//		        ,{    
//			        iconCls:'icon-add',    
//			        handler:function(){    
//			            alert('add');    
//			        }    
//			    },{    
//			        iconCls:'icon-edit',    
//			        handler:function(){    
//			            alert('edit');    
//			        }    
//			    }
//			    ]
//		        beforePageText: '第',//页数文本框前显示的汉字 
//		        afterPageText: '页    共 {pages} 页', 
//		        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录', 
//		        onBeforeRefresh:function(){
//		            $(this).pagination('正在处理，请稍待。。。');
//		            $(this).pagination('loaded');
//		        }
		    }); 
			//resizeDt(_divId);
			//获取div高度
//			var _height = $data.height();
//			if(_height != 0){
//				$data.datagrid('resize',{height:200});
//			}
			_this.query = function(){
				//清空全选
				$data.datagrid('clearSelections');
				$data.datagrid('uncheckAll');
				$data.datagrid('options').queryParams = getQueryParams(_conditions);
				//查询第一页数据
				$data.datagrid('options').pageNumber = 1;
				//分页栏跳转到第一页
				$data.datagrid('getPager').pagination({pageNumber: 1});
				$data.datagrid('reload');
			};
			_this.clean = function(){
				cleanQueryParams(_conditions);
			};
			return _this;
		},
		/**
		 * 列表组件，封装查询
		 * @param _divId
		 * @param _url 查询地址
		 * @param _pk 主键
		 * @param _colums 查询列
		 * @param _conditions [] 查询条件 ,指的的input的id，拼接的时候获取此元素的name
		 * @param _sortName 排序列
		 * @param _sortOrder 排序顺序 asc desc
		 * @param _check 是否多选
		 * @param _width 宽 百分比写（0-1）
		 * @param _height 高
		 * @param _onLoadSuccess 加载成功回调事件
		 * @param _frozenColumns 冻结列
		 * @param _pagination 是否分页
		 * @param _buttons _buttons 自定义工具栏： array数组
		 */
		datagridSimple:function(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check,_onLoadSuccess,_frozenColumns,_pagination,_buttons){
			if(_divId == null || _divId == "" || typeof(_divId) == 'undefined'){
				_divId = "data_div";
			}
			$data = $('#'+_divId);
			//首先给此div加一个标识
			$data.addClass('datagrid_mark');
			accountHeight();
			if(typeof(_frozenColumns) == 'undefined' || _frozenColumns == '' ||  _frozenColumns){
				if(_check){
					_frozenColumns = [{field:_pk,checkbox:true,sortable:true}];
				}else{
					_frozenColumns = [];
				}

			}
			if(typeof(_pagination) == 'undefined' && _pagination != ''){
				_pagination = true;
			}
			//获取div的宽度
			var _width = $data.width();
			//获取div高度
			var _height = $data.height();
			//设置参数
			var queryParams = getSimpleQueryParams(_conditions);
			//组装datagrid
			var _this = $data.datagrid({
				width:_width,
				height:_height,
				fitColumns: true,
				autoRowHeight:false,
				nowrap: false,striped: true,
				url:_url,
				idField:_pk,
				sortName:_sortName, //排序
	 			sortOrder:_sortOrder,
				onLoadSuccess: function (data) {
					//setTimeout(_onLoadSuccess(), 10);
					if(typeof(_onLoadSuccess) != 'undefined' && _onLoadSuccess != ''){
						var call = new selfCallBack(_onLoadSuccess);
						call.func();
					}
					if(data.length == 0){
						alert('未找到数据');
					}
				},
				pageList: [10, 20, 50, 100],
				pageSize: 10,
				pagination:true, //添加分页
				rownumbers:false,//显示序号
				singleSelect:true,
				selectOnCheck:false,
				checkOnSelect:false,
				frozenColumns:[_frozenColumns],
				columns:[_colums],
				queryParams:queryParams
			});
			//设置分页控件 
		    var p = $('#' + _divId).datagrid('getPager'); 
		    $(p).pagination({ 
		    	buttons:_buttons
		    }); 
			_this.query = function(){
				//设置参数
				$data.datagrid('options').queryParams = getSimpleQueryParams(_conditions);
				//查询第一页数据
				$data.datagrid('options').pageNumber = 1;
				//分页栏跳转到第一页
				$data.datagrid('getPager').pagination({pageNumber: 1});
				$data.datagrid('reload');
			};
			_this.clean = function(){
				cleanQueryParams(_conditions);
			};
			return _this;
		},
		/**
		 * 列表组件，封装查询
		 * @param _divId
		 * @param _pk 主键
		 * @param _colums 查询列
		 * @param _conditions [] 查询条件 ,指的的input的id，拼接的时候获取此元素的name
		 * @param _sortName 排序列
		 * @param _sortOrder 排序顺序 asc desc
		 * @param _showCheck 是否显示勾选
		 * @param _pagination 是否分页
		 * @param _autoRowHeight 自动高度
		 * @param _onLoadSuccess 加载成功回调事件
		 */
		datagridJsObject:function(_divId,_dataObject,_pk,_colums,_sortName,_sortOrder,_showCheck,_pagination,_autoRowHeight,_onLoadSuccess,_frozenColumns,_showMoreMenu){
			if(_divId == null || _divId == "" || typeof(_divId) == 'undefined'){
				_divId = "data_div";
			}
			$data = $('#'+_divId);
			//首先给此div加一个标识
			$data.addClass('datagrid_mark');

			accountHeight();

			if(typeof(_pagination) == 'undefined'){
				_pagination = true;
			}

			//不传入本参数 或者 =true
			if(_showCheck == undefined || _showCheck == true){
				if(typeof(_frozenColumns) == 'undefined' || _frozenColumns == '' ||  _frozenColumns){
					_frozenColumns = [{field:_pk,checkbox:true,sortable:true}];
				}
			}else if(_showCheck == false){
				_frozenColumns = [];
			}else{
				$.alert("ERROR：表格参数<_showCheck>传入有误，只能为【true】 or 【false】");
			}

			if(typeof(_autoRowHeight) == 'undefined'){
				_autoRowHeight = false;
			}
			if(typeof(_showMoreMenu) == 'undefined'){
				_showMoreMenu = true;
			}
			//获取div的宽度
			var _width = $data.width();

			var _this = $data.datagrid({
				url:null,
				width:_width,
				fitColumns: true,
				autoRowHeight:_autoRowHeight,
				nowrap: false,
				striped: true,
				idField:_pk,
				fit:false,//不全屏平铺
				sortName:_sortName, //排序
				sortOrder:'desc',
				onLoadSuccess: function (data) {
					if(typeof(_onLoadSuccess) != 'undefined' && _onLoadSuccess != ''){
						var call = new selfCallBack(_onLoadSuccess);
						call.func();
					}
					if(data.length == 0){
						alert('未找到数据');
					}
				},
				pageList: [10, 20, 50, 100],
				pageSize: 10,
				data:_dataObject.slice(0,10),
				pagination:_pagination, //添加分页
				rownumbers:false,//显示序号
				singleSelect:true,
				selectOnCheck:false,
				checkOnSelect:false,
				showMoreMenu:_showMoreMenu,
				collapsible: true,//显示可折叠按钮
				frozenColumns:[_frozenColumns],
				columns:[_colums]
			});
			var pager = $data.datagrid("getPager");
		    pager.pagination({
		           total:_dataObject.length,
		           onSelectPage:function (pageNo, pageSize) {
		               var start = (pageNo - 1) * pageSize;
		               var end = start + pageSize;
		               $data.datagrid("loadData", _dataObject.slice(start, end));
		               pager.pagination('refresh', {
		                   total:_dataObject.length,
		                   pageNumber:pageNo
		               });
		           }
		      });
		     //resizeDt(_divId);
		},
		sortUp:function(url,displayNo,flag){
			$.post(url,{displayNo:displayNo,flag:flag},function(data){
				top.frmright.window.freshDataTable('data_div');
			});
		}
	});
	/**
	 * 设置全局ajax方法，捕获后台异常
	 */
	$.ajaxSetup({
	   error:function(xhr,status,err){
		   var wating = $("div[IDFLAG=progresswaiting]",window.top.document);
		   if(wating.length == 1){
			   top.Dialog.close();
		   }
		   var errMsg="";
		   var msg="";
    	   if(xhr.status == 2013 || xhr.status == 2014){
    		   //自定义的异常
    		   errMsg = eval("(" + decodeURIComponent(xhr.getResponseHeader('ErrorJson')) + ")");
    		   if(errMsg.msg=='denied'){
    			   parent.location.href=getRootPath()+"/security/denied.jsp";
    			   $.closeFromInner();
    		   }
    	   }else{
    		   $('#APP_MSG').html(xhr.responseText);
    		   msg = $('#APP_MSG').find('p').eq(1).find('u').html();
    		   if(msg != null && msg !=""){
    			   var msgArr = msg.split(':');
        		   var temp = msgArr[msgArr.length-1];
        		   errMsg = $.parseJSON("{\"code\":\"\",\"msg\":\""+temp+"\"}");
    		   }
    	   }
    	   //显示异常消息
    	   $.showTips(errMsg.msg,5,errMsg.code);
	   }
	});


    $.fn.center = function () {
        this.css("position","absolute");
        this.css("top", ( $(window).height() - this.height() ) / 2+$(window).scrollTop() + "px");
        this.css("left", ( $(window).width() - this.width() ) / 2+$(window).scrollLeft() + "px");
        return this;
    };
})(jQuery);
function getQueryParams(_conditions){
	var condition = '';
	//{'name':['test','',''],'id':['1','=','int']}     value的组成为 值,匹配符,类型  ，类型包含 int date string
	
	for(var i = 0;i < _conditions.length;i ++){
		var filed = _conditions[i];
		var filedName = '';
		if(!$.isEmpty(filed)){
			//默认后台匹配为 =
			var qMatch = $('#'+filed).attr('qMatch');
			var qType = $('#'+filed).attr('qType');
			if($.isEmpty(qMatch))
				qMatch = '=';
			if($.isEmpty(qType))
				qType = 'string';
			var filedValue = $('#'+filed).val();
			if(!$.isEmpty(filedValue)){
				filedName = $('#'+filed).attr('name');
				condition += ',\''+filedName+'_myfd'+'\':['+'\''+ encodeURIComponent(filedValue) +'\',\''+qMatch+'\',\''+qType+'\']';
			}
		}
	}
	
	if(!$.isEmpty(condition)){
		condition = condition.substring(1);
	}
	condition = eval('('+'{'+condition+'}'+')');
	
	
	return condition;
}
/**
 * 通过&传参，注意参数前需要有？开头
 * @param _conditions
 * @returns {String}
 */
function getQueryParamString(_conditions){
	var condition = '';
	//{'name':['test','',''],'id':['1','=','int']}     value的组成为 值,匹配符,类型  ，类型包含 int date string
	for(var i = 0;i < _conditions.length;i ++){
		var filed = _conditions[i];
		var filedName = '';
		if(!$.isEmpty(filed)){
			//默认后台匹配为 =
			var qMatch = $('#'+filed).attr('qMatch');
			var qType = $('#'+filed).attr('qType');
			if($.isEmpty(qMatch))
				qMatch = '=';
			if($.isEmpty(qType))
				qType = 'string';
			var filedValue = $('#'+filed).val();
			if(!$.isEmpty(filedValue)){
				filedName = $('#'+filed).attr('name');
				condition += '&' + filedName+'_myfd[]' + '==[' + encodeURIComponent(filedValue) +',' + qMatch + ',' + qType + ']';
			}
		}
	}
	if(!$.isEmpty(condition)){
		condition = condition.substring(1);
	}
	return encodeURI(condition);
}
function getSimpleQueryParams(_conditions){
	var condition = '';
	for(var i = 0;i < _conditions.length;i ++){
		var filed = _conditions[i];
		var filedName = '';
		var filedValue= '';
		if(!$.isEmpty(filed)){
			if(document.getElementById(filed)!=null && document.getElementById(filed)!=undefined){
				//filedValue = document.getElementById(filed).value;
				filedValue = $('#'+filed).val();
				if(filedValue != null && filedValue != ""){
					filedName = document.getElementById(filed).name;
				}
			}
//			filedName = $('#'+filed).attr('name');
//			if(filedValue != null && filedValue != ""){
//				filedName = getFiledName(filed,false);
//			}else{//如果filedValue为空,则调用父窗口的document来获取
//				filedValue = getFiledValue(filed,true);
//				if(filedValue != null && filedValue != ""){
//					filedName = getFiledName(filed,true);
//				}
//			}
			if(!$.isEmpty(filedValue) && !$.isEmpty(filedName)){
				condition += ',"'+ filedName +'":"'+ encodeURIComponent(filedValue) +'"';
			}
			//alert("最后的JSON串为：" + condition);
		}
	}
	
	if(!$.isEmpty(condition)){
		condition = condition.substring(1);
	}
	condition = eval('('+'{'+condition+'}'+')');
	return condition;
}


/**
 * 清空查询参数值
 * @param _conditions 查询参数
 */
function cleanQueryParams(_conditions){
	for(var i = 0;i < _conditions.length;i ++){
		var filed = _conditions[i];
		if(!$.isEmpty(filed)){
			var filedId = $('#'+filed);
			var filedType = document.getElementById(filed).type;
			//alert(filedType);
			var placeholderVal = filedId.attr('placeholder');
			//alert(filedId.attr('placeholder'));
			if(filedType == "text" || filedType == "textarea" || filedType == "hidden"){
				var isIe = checkIsIE();
				if(isIe == 1){
					filedId.addClass('placeholder');
					filedId.attr("value",placeholderVal);
				}else{
					filedId.attr("value","");
				}
			}else if(filedType == "radio" || filedType == "checkbox"){
				filedId.attr("checked",'');//不打勾 
			}else if(filedType == "select-one"){
				var a = document.getElementById(filed);
				a.options[0].selected = true;
			}
		}
	}
}


function checkIsIE() {
	if ((navigator.userAgent.indexOf('MSIE') >= 0)
			&& (navigator.userAgent.indexOf('Opera') < 0)) {
		return 1;
	} else if (navigator.userAgent.indexOf('Firefox') >= 0) {
		//alert('你是使用Firefox')
		return 2;
	} else if (navigator.userAgent.indexOf('Opera') >= 0) {
//		alert('你是使用Opera')
		return 3;
	} else if( window.navigator.userAgent.indexOf("Chrome") !== -1 ){
//		alert('谷歌')
		return 4;
	}else{
		return 5;
	}
}


function getFiledValue(filed,isParent){
	var filedValue = "";
	var fieldObj = null;
	if(isParent == true){
		fieldObj = parent.document.getElementById(filed); //$('#'+filed, parent.document);
	}else{
		fieldObj = document.getElementById(filed); //$('#'+filed, parent.document);
	}
	if(fieldObj != null && fieldObj != "undefined"){
		filedValue = fieldObj.value;
	}
	return filedValue;
}

function getFiledName(filed,isParent){
	var filedName = "";
	var fieldObj = null;
	if(isParent == true){
		fieldObj = parent.document.getElementById(filed); //$('#'+filed, parent.document);
	}else{
		fieldObj = document.getElementById(filed); //$('#'+filed, parent.document);
	}
	if(fieldObj != null && fieldObj !== "undefined"){
		filedName = fieldObj.name;
	}
	return filedName;
}

//将日期格式化为：yyyy-MM-dd
$formatDate = function(value,rec,index) {
	if(value != null && value != '' && value.length > 10){
		return value.substring(0,10);
	}else{
		return "";
	}
}


/**
 * 方法说明：通过参数key，匹配map中的值，返回明细值
 * 处理形如：
 * {1=新建,2=待审核,3=加工中}的字符串
 * param separator 分隔符,默认分隔为'='，支持自定义分隔符"
 *
 */
function doMapStr(val,key,separator){
	var re = "";
	if(val != null && val != "" && key != null && key != ""){
		if(separator == null || separator == "" || typeOf(separator) == "undefined"){separator = "="};
		val = val.substring(1,val.length-1);
		var vals = val.split(",");
		for(var i = 0;i < vals.length;i ++){
 			var valIndex = vals[i];
 			if(valIndex != null && valIndex != ""){
 				var valIndexs = valIndex.split(separator);
 				if(valIndexs[0] == key){
					re = valIndexs[1];
					break;
				}
 			}
 		}
	}
	return re;
}

//普通方法
function openWindow(url,name,iWidth,iHeight){
    var url;                             //转向网页的地址;
    var name;                            //网页名称，可为空;
    var iWidth;                          //弹出窗口的宽度;
    var iHeight;                         //弹出窗口的高度;
    //获得窗口的垂直位置
    var iTop = (window.screen.availHeight-30-iHeight)/2;
    //获得窗口的水平位置
    var iLeft = (window.screen.availWidth-10-iWidth)/2;
    window.open(url,name,'height='+iHeight+',,innerHeight='+iHeight+',width='+iWidth+',innerWidth='+iWidth+',top='+iTop+',left='+iLeft+',status=no,toolbar=no,menubar=no,location=no,resizable=no,scrollbars=0,titlebar=no');
}
String.prototype.endWith = function(s) {
	if (s == null || s == "" || this.length == 0 || s.length > this.length)
		return false;
	if (this.substring(this.length - s.length) == s)
		return true;
	else
		return false;
	return true;
};

String.prototype.startWith = function(s) {
	if (s == null || s == "" || this.length == 0 || s.length > this.length)
		return false;
	if (this.substr(0, s.length) == s)
		return true;
	else
		return false;
	return true;
};

//获取长度为len的随机字符串
function _getRandomString(len) {
    len = len || 32;
    var $chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefGhijklmnopqrstuvwxyz0123456789'; // 默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1
    var maxPos = $chars.length;
    var pwd = '';
    for (var i = 0; i < len; i++) {
        pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
    }
    return pwd;
}

//+---------------------------------------------------
//| 日期计算
//+---------------------------------------------------
function DateAdd(strInterval, Number,dtTmpStr) {
	var dtTmp;
	var ua = navigator.userAgent.toLowerCase();
	if (typeof dtTmpStr == 'string')//如果是字符串转换为日期型
	{
		dtTmp = new Date(dtTmpStr.replace("-","/"));
	}
	if(ua.indexOf("firefox")!=-1) {
		if (typeof dtTmpStr == 'string'){//如果是字符串转换为日期型
			var arrayValue = dtTmpStr.split("-");
			dtTmp = new Date(arrayValue[0],arrayValue[1]-1,arrayValue[2],"","","");
		}
	}
	switch (strInterval) {
		case 's' :return new Date(Date.parse(dtTmp) + (1000 * parseInt(Number)));
		case 'n' :return new Date(Date.parse(dtTmp) + (60000 * parseInt(Number)));
		case 'h' :return new Date(Date.parse(dtTmp) + (3600000 * parseInt(Number)));
		case 'd' :return new Date(Date.parse(dtTmp) + (86400000 * parseInt(Number)));
		case 'w' :return new Date(Date.parse(dtTmp) + ((86400000 * 7) * parseInt(Number)));
		case 'q' :return new Date(parseInt(dtTmp.getFullYear()), (dtTmp.getMonth()) + parseInt(Number)*3, dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());
		case 'm' :return new Date(parseInt(dtTmp.getFullYear()), (dtTmp.getMonth()) + parseInt(Number), dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());
		case 'y' :return trans(((parseInt(dtTmp.getFullYear()) + parseInt(Number)) + "-" + (parseInt(dtTmp.getMonth())+1) + "-" + dtTmp.getDate()),'0');
	}

}

//实现加减f：'1'加1，'0'减1
function trans(o, f) {
	var ma = [['1','3','5','7','8','10'],['4','6','9','11']];
	var d = o.split('-');
	var l = isLeap(d[0]);
	if (f == '1') {
		if ((check(d[1], ma[0]) && (d[2] == '31'))
				|| (check(d[1], ma[1]) && (d[2] == '30'))
				|| (d[1] == '2' && d[2] == '28' && !l)
				|| (d[1] == '2' && d[2] == '29' && l)) {
			return d[0] + '-' + (d[1] * 1 + 1) + '-' + '01';
		} else if (d[1] == '12' && d[2] == '31') {
			return (d[0] * 1 + 1) + '-' + '1-01';
		} else {
			var d2 = (d[2] * 1 + 1);
			if(d2 != null && d2 != "" && len(d2+'') == 1){
				d2 = "0" + d2;
			}
			return d[0] + '-' + d[1] + '-' + d2;
		}
	} else if (f == '0') {
		if (check(d[1] - 1, ma[0]) && (d[2] == '1')) {
			return d[0] + '-' + (d[1] - 1) + '-' + '31';
		} else if (check(d[1] - 1, ma[1]) && (d[2] == '1')) {
			return d[0] + '-' + (d[1] - 1) + '-' + '30';
		} else if (d[1] == '1' && d[2] == '1') {
			return (d[0] - 1) + '-' + '12-31';
		} else if (d[1] == '3' && d[2] == '1' && !l) {
			return d[0] + '-' + '2-28';
		} else if (d[1] == '3' && d[2] == '1' && l) {
			return d[0] + '-' + '2-29';
		} else {
			var d2 = (d[2] - 1);
			if(d2 != null && d2 != "" && len(d2+'') == 1){
				d2 = "0" + d2;
			}
			return d[0] + '-' + d[1] + '-' + d2;
		}
	} else {
		return;
	}
}
//转化日期函数
function pasDate(da) {
	var yp = da.indexOf('年'), mp = da.indexOf('月'), dp = da.indexOf('日');
	var y = da.substr(0, yp), m = da.substr(yp + 1, mp - yp - 1), d = da
			.substr(mp + 1, dp - mp - 1);
	return [ y, m, d ];
}


//判断数组a是否存在在元素n
function check(n, a) {
	for ( var i = 0, len = a.length; i < len; i++) {
		if (a[i] == n) {
			return true;
		}
	}
	return false;
}

// 闰?年?
function isLeap(y) {
	return ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) ? true : false;
}
function notice(title,msg,time){
	 var opt = {},   
		        api, aConfig, hide, wrap, top,   
		        duration = 800;   
			var d = art.dialog({
			    title: title,
			    content: msg,
			    id: 'Notice',   
		        left: '100%',   
		        top: '100%',
		        width:320,
		        fixed: true,   //开启静止定位。静止定位是css2.1的一个属性，它静止在浏览器某个地方不动，也不受滚动条拖动影响。（artDialog支持IE6 fixed）
		        drag: true,   //是否允许用户拖动位置
		        resize: true,   //是否允许用户调节尺寸
		        follow: null,   //让对话框吸附到指定元素附近
		        lock: false,
		        quickClose: true,
		        //icon: 'face-sad',//定义消息图标。可定义“skins/icons/”目录下的图标名作为参数名（不包含后缀名）
		        init: function(here){   
		            api = this;   
		            aConfig = api.config;   
		            wrap = api.DOM.wrap;   
		            top = parseInt(wrap[0].style.top);   
		            hide = top + wrap[0].offsetHeight;   
		            wrap.css('top', hide + 'px')   
		                .animate({top: top + 'px'}, duration, function () {   
		                    opt.init && opt.init.call(api, here);   
		                });   
		        },   
		        close: function(here){   
		            wrap.animate({top: hide + 'px'}, duration, function () {   
		                opt.close && opt.close.call(this, here);   
		                aConfig.close = $.noop;   
		                api.close();   
		            });   
		               
		            return false;   
		        },
		        time: time 
			});
}
/**
 * 由于元数据模板管理中的添加修改元数据是一个嵌套页面，位置获取不准确，随新定义一个方法，主要是位置与上一个方法不同
 * @param title
 * @param msg
 * @param time
 */
function noticeForIframe(title,msg,time){
	 var opt = {},   
		        api, aConfig, hide, wrap, top,   
		        duration = 800;   
			var d = art.dialog({
			    title: title,
			    content: msg,
			    id: 'Notice',   
		        left: '65%',   
		        top: '100%',
		        width:320,
		        fixed: true,   //开启静止定位。静止定位是css2.1的一个属性，它静止在浏览器某个地方不动，也不受滚动条拖动影响。（artDialog支持IE6 fixed）
		        drag: true,   //是否允许用户拖动位置
		        resize: true,   //是否允许用户调节尺寸
		        follow: null,   //让对话框吸附到指定元素附近
		        lock: false,
		        quickClose: true,
		        //icon: 'face-sad',//定义消息图标。可定义“skins/icons/”目录下的图标名作为参数名（不包含后缀名）
		        init: function(here){   
		            api = this;   
		            aConfig = api.config;   
		            wrap = api.DOM.wrap;   
		            top = parseInt(wrap[0].style.top);   
		            hide = top + wrap[0].offsetHeight;   
		            wrap.css('top', hide + 'px')   
		                .animate({top: top + 'px'}, duration, function () {   
		                    opt.init && opt.init.call(api, here);   
		                });   
		        },   
		        close: function(here){   
		            wrap.animate({top: hide + 'px'}, duration, function () {   
		                opt.close && opt.close.call(this, here);   
		                aConfig.close = $.noop;   
		                api.close();   
		            });   
		               
		            return false;   
		        },
		        time: time 
			});
}
function confirmNew(msg, fnOk, fnCanel, title){
	top.Dialog.confirm(msg, fnOk, fnCanel, title);
}
function len(s) {
	var l = 0;
	var a = s.split("");
	for ( var i = 0; i < a.length; i++) {
		if (a[i].charCodeAt(0) < 299) {
			l++;
		} else {
			l += 2;
		}
	}
	return l;
}

function trim(str){ //删除左右两端的空格  
    return str.replace(/(^\s*)|(\s*$)/g, "");  
}  
function ltrim(str){ //删除左边的空格  
    return str.replace(/(^\s*)/g,"");  
}  
function rtrim(str){ //删除右边的空格  
    return str.replace(/(\s*$)/g,"");  
}  

Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}