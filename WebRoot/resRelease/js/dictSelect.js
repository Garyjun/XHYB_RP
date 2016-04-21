var dictCounttimes = 1;
var dictIndexTag = "";
/**
 * 
 * @param indexTag 数据字典索引
 * @param source 来源
 * @param selValue 选中值的数据库主键id
 * @param elId 字段元素属性id
 */
function dyAddOpts(indexTag, source, selValue, elId){
	var count = 0;
	if(source=="edit"){
		$("select[id=" + elId + "]").multipleSelect({
			multiple: true,
			multipleWidth: 160,
			width: "100%"
		});
	}
	var id = getPKByIndextag(indexTag);
	if(id!=null&&id!=undefined){
		var vals = getDictValues(id);
		var arrs = [];
		if(vals!=null){
			arrs = vals.split(",");
		}
		if(arrs[0]==""&&arrs.length==1){
			return;
		}
		if(dictCounttimes>1){
			var len = arrs.length;
			if(len>=count){						
				$(arrs).each(function(count) {
					var values = arrs[count].split("=");
					
					var $select = $("select[id=" + elId + "]"),
						value = arrs[count],
						$opt = $("<option />", {
							title: values[1],
							value: values[0],
							text: values[1]
						});
					$select.append($opt);
					count++;					
				});
				dictCounttimes=1;
			}else{
				len = 0;
				arrs = null;
				return;
			}
			$("select[id=" + elId + "]").multipleSelect("refresh");
		}
		var len = arrs.length;
		if(len>=count){						
			$(arrs).each(function(count) {
				var values = arrs[count].split("=");
				var $select = $("select[id=" + elId + "]"),
					value = arrs[count],
					$opt = $("<option />", {
						title: values[1],
						value: values[0],
						text: values[1]
					});
				$select.append($opt);
				count++;					
			});
		}else{
			len = 0;
			arrs = null;
			return;
		}				
		$("select[id=" + elId + "]").multipleSelect("refresh");
		if(source=="edit"){
			var checkedVal = [];
			if(selValue!=null){
				checkedVal = selValue.toString().split(",");
			}
			$("select[id=" + elId + "]").multipleSelect("setSelects", checkedVal);
		}
		//
	}else{
		$.alert("请检查数据字典配置！");
	}
}


/**
 * 为高级查询数据字典类型的字段值重写的onclick事件
 * 2015年8月27日 10:51:05 huangjun 
 * @param indexTag 数据字典索引
 * @param source 来源
 * @param selValue 选中值的数据库主键id
 * @param elId 字段元素属性id
 */
function dictAddOpts(indexTag, source, selValue, elId){
	var count = 0;
	var id = getPKByIndextag(indexTag);
	if(id!=null&&id!=undefined){
		var vals = getDictValues(id);
		var arrs = [];
		if(vals!=null){
			arrs = vals.split(",");
		}
		if(arrs[0]==""&&arrs.length==1){
			return;
		}
		var selectLen = "-1" ;
		try{
			selectLen = $("id='" + elId+"'");
		}catch(e){
			selectLen = "0";
		}
		if(selectLen =="0"){
			var len = arrs.length;
			if(len>=count){						
				$(arrs).each(function(count) {
					var values = arrs[count].split("=");
					
					var $select = $("select[id=" + elId + "]"),
						value = arrs[count],
						$opt = $("<option />", {
							title: values[1],
							value: values[0],
							text: values[1]
						});
					$select.append($opt);
					count++;					
				});
				dictCounttimes=1;
				dictIndexTag = indexTag;//若dictIndexTag和indexTag不等  则说明是第一次加载
			}else{
				len = 0;
				arrs = null;
				return;
			}
			$("select[id=" + elId + "]").multipleSelect("refresh");
		}
	}else{
		$.alert("请检查数据字典配置！");
	}
}


/*
 *为数据字典项添加数据内容
 * indexTag 数据字典索引值
 */
function dictQueryAddOpts(indexTag, source, selValue, elId){
	var count = 0;
	var id = getPKByIndextag(indexTag);
	if(id!=null&&id!=undefined){
		var vals = getDictValues(id);
		var arrs = [];
		if(vals!=null){
			arrs = vals.split(",");
		}
		if(arrs[0]==""&&arrs.length==1){
			return;
		}
		var selectLen = "-1" ;
		try{
			selectLen = $("id='" + elId+"'");
		}catch(e){
			selectLen = "0";
		}
		if(selectLen =="0"){
			var len = arrs.length;
			if(len>=count){						
				$(arrs).each(function(count) {
					var values = arrs[count].split("=");
					
					var $select = $("ul[id=" + elId + "]"),
						value = arrs[count],
						$opt = $("<li/>", {
							title: values[1],
							value: values[0],
							text:  values[1],
							rel:   values[0]
						});
					$select.append($opt);
					count++;					
				});
				dictCounttimes=1;
				dictIndexTag = indexTag;//若dictIndexTag和indexTag不等  则说明是第一次加载
			}else{
				len = 0;
				arrs = null;
				return;
			}
			$("select[id=" + elId + "]").multipleSelect("refresh");
		}
	}else{
		$.alert("请检查数据字典配置！");
	}
}





function getDictValues(id){
	var _queryUrl = _appPath + "/system/dataManagement/dataDict/getDictValuesByPId.action?pid=" + id;
	var values = "";
	$.ajax({
		url: _queryUrl,
		type: "get",
		async: false,
		success: function(data){
			values = data;
			return data;
		}
	});
	return values;
}

function getPKByIndextag(indexTag){
	var _queryUrl =  _appPath + "/system/dataManagement/dataDict/getPKByIndex.action?indexTag=" + indexTag;
	var id = -1;
	$.ajax({
		url: _queryUrl,
		type: "get",
		async: false,
		success: function(data){
			id =data;
			//alert(data);
			//return data;
		}
	});
	return id;
}

function optCheckChangeListener(){
	var checkedVals = $('select[name=multiSelEle]').multipleSelect('getSelects', 'value');
	//alert(checkedVals);
}