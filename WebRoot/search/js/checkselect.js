
/**
 * @author xiehewei hwshea@163.com
 * @desc 本高级查询用到的插件有bootstrap-multiselect.js 和jquery.multiple.select.js
 * bootstrap-multiselect.js的api文档可参见 http://silviomoreto.gidirectCreateQueryConditionForResSelthub.io/bootstrap-select/
 * jquery.multiple.select.js的api文档可参见 http://wenzhixin.net.cn/p/multiple-select/docs/#multiple-select
 * 
 * 使用该插件需要引用的js有：
 *		<script type="text/javascript" src="${path}/resRelease/resOrder/js/map.js"></script>
 *		<script type="text/javascript" src="${path}/resRelease/resOrder/js/list.js"></script>
 *		<script type="text/javascript" src="${path}/search/js/checkselect.js"></script>
 *		<script type="text/javascript" src="${path}/search/js/bootstrap-select.js"></script>
 *		<script type="text/javascript" src="${path}/resRelease/js/dictSelect.js"></script>
 *		<script type="text/javascript" src="${path}/resRelease/js/jquery.multiple.select.js"></script>
 * 使用该插件需要引用的css有：
 *		<link rel="stylesheet" type="text/css" href="${path}/search/css/bootstrap-select.css" />
 *		<link rel="stylesheet" type="text/css" href="${path}/resRelease/css/multiple-select.css" />
 * 
 * 本插件分为四个不同功能：1，直接生成高级查询查询项  2，手动添加高级查询  3，专门为用户权限的组件 4，用户权限修改写的展示组件
 * 下面主要介绍三种功能的实现代码，设计细节在此不做具体阐述
 * 1，直接生成高级查询项的入口方法为 directCreateQueryCondition()
 * 2，手动添加高级查询的入口方法为 addQueryCondition()   注意：本方法由于长期没有维护，所以有些功能和样式不完善
 * 3，专门为用户权限的入口方法为 addQueryConditionByResType()
 * 4，为用户权限修改的方法入口为 showPrivilegeConditionForEdit()
 * 5.directCreateQueryConditionAndRes();//资源发布中添加资源
 * 6.dirCreateQueryValAndRes//资源发布中添加资源
 * */

//设置数据库的类型
var defaultQueryModel = "solr";
var globalContactWord = "AND";

var xmenuNum = "";  //记录xmenu要初始化的方法名称，解决当资源切换时xmenu初始化就加载导致无效
//定义全局变量 方便生成不重复的标签id值
var lineCount = 0;
var countTimes = 0;
function addQueryCondition(){
	var vals = creatFieldOpt();
	var flag = true;
	//TODO 需要判断完善在非顺序删除的情况
	/*var i = "";
	if(lineCount==1){
		var contactVal = $("#contactWord01 option:selected").val();
		if(contactVal==undefined||contactVal.trim().length==0){
			flag = false;
		}
	}else{
		for(var count=1;count<=lineCount;count++){
			if(count<10){
				i = "0" + count;
			}
			var contactVal = $("#contactWord"+i+ " option:selected").val();
			if(contactVal==undefined||contactVal.trim().length==0){
				flag = false;
				break;
			}
		}
	}*/
	if(flag){
		++lineCount;
		var queryCondition = $("#queryCondition");
		var count = "";
		if(lineCount<10){
			count = "0" + lineCount;
		}
		var divRow = $("<div class='row' id='condition"+count+"'></div>");
		var field = createField(lineCount, vals);
		var opt = createOperator(lineCount);
		var val = createVal(lineCount, "");
		//var contact = createContact(lineCount);
		var delOpt = $("<a class=\"btn hover-red\" href=\"javascript:deleteQueryCondition('condition"+count+"')\"><i class=\"fa fa-trash-o\"></i>删除</a>");
		
		field.appendTo(divRow);
		opt.appendTo(divRow);
		val.appendTo(divRow);
		//contact.appendTo(divRow);
		delOpt.appendTo(divRow);
		divRow.appendTo(queryCondition);
		$('.selectpicker').selectpicker({
	        'selectedText': 'cat1'
	    });
	}else{
		$.alert("请选择连接符");
		return;
	}
}

/**
 * @desc 为用户权限定制
 * @param resType 资源类型
 * 
 */
function addQueryConditionByResType(resType){
	var resTypeIds = "";
//	获得泛型id
	var checkedData = $("input[id^=resTypeIds]");
	for(var i=0;i<checkedData.length;i++){
		if(checkedData[i].checked==true){
			resTypeIds = resTypeIds+checkedData[i].value+",";	
		}
	}
	if(resTypeIds!=""){
		resTypeIds = resTypeIds.substring(0,resTypeIds.length-1);
	}
	var vals = creatDefineMetaDataByResType(resType);
	//alert(resTypeIds);
	var flag = true;
	if(flag){
		++lineCount;
		var queryCondition = $("#queryCondition" + resType);
		var addCondtion = $("#addCondtion" + resType);
		var count = "";
		if(lineCount<10){
			count = "0" + lineCount;
		}
		var divRow = $("<div class='row' id='condition"+count+"'></div>");
		var field = createFieldByResType(lineCount, vals, resType);
		var opt = createOperatorByResType(lineCount, resType);
		var val = createValByResType(lineCount, "", resType);
		var delOpt = $("<a class=\"btn hover-red\" href=\"javascript:deleteQueryCondition('condition"+count+"')\"><i class=\"fa fa-trash-o\"></i>删除</a>");
		
		field.appendTo(divRow);
		opt.appendTo(divRow);
		val.appendTo(divRow);
		//contact.appendTo(divRow);
		delOpt.appendTo(divRow);
		addCondtion.before(divRow);
//		divRow.appendTo(queryCondition);
		$('.selectpicker').selectpicker({
	        'selectedText': 'cat1'
	    });
	}else{
		$.alert("请选择连接符");
		return;
	}
}

function createFieldByResType(counter, queryFields, resType){
	if(counter<10){
		counter = "0" + counter;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 90px;'></div>");
	var div2 = $("<div class='form-group'></div>");
	var label = $("<label for='queryField' class='control-label col-md-4' style='margin-left:-16px; margin-right:10px; margin-top:8px;'>查询字段：</label>");
	//var sel = $("<select class='selectpicker bla bla bli queryField"+counter+"'"+" id='queryField"+counter+"'"+" name='queryField' data-live-search='true' style='border:solid 1px #ffcc00;'></select>");
	var sel =  $("<select class='selectpicker bla bla bli queryField"+counter+"' id='queryField"+counter+"' role='"+resType+"' name='queryField' onchange=\"fieldChangeEvent('"+counter+"');\" data-live-search='true' style='border:solid 1px #ffcc00;'>");
    //var obj = eval("("+queryFields+")");
	var optNone = $("<option value='' fieldType='-1'></option>");
	optNone.appendTo(sel);
    var obj = JSON.parse(queryFields);
    var map = new Map();
    $(obj).each(function(index) {
    	var metaData = obj[index];
    	var groupId = metaData.groupId;
    	var groupName = metaData.groupName;
    	var optGroup = $("<optgroup label='"+groupName+"' data-subtext='' data-icon='icon-ok'></optgroup>");
    	if(map.get(groupName)==null){
    		optGroup.appendTo(sel);
    		map.put(groupName, optGroup);
    	}
    });
    
    $(obj).each(function(index) {
        var metaData = obj[index];
        var fieldType = metaData.fieldType;
        var groupId = metaData.groupId;
        var fieldEnName = metaData.fieldEnName;
        var fieldZhName = metaData.fieldZhName;
        var fieldValues = metaData.fieldValues;
        //alert("查询字段对应的操作值fieldValues是： " + fieldValues);
        if(groupId){//有分组的
        	var groupName = metaData.groupName;
        	var group = $("optgroup[label="+groupName+"]");
        	if(map.get(groupName)){
        		var opt;
            	if(fieldValues){
            		opt = $("<option fieldValues='"+fieldValues+"' role='"+resType+"' value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}else{
            		opt = $("<option value='"+fieldEnName+"' role='"+resType+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}
                opt.appendTo(map.get(groupName));
        	}else{  //不存在该分组
        		var optGroup = $("<optgroup label='"+groupName+"' data-subtext='another test' data-icon='icon-ok'></optgroup>");
        		var opt;
            	if(fieldValues){
            		opt = $("<option fieldValues='"+fieldValues+"'  role='"+resType+"' value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}else{
            		opt = $("<option value='"+fieldEnName+"' role='"+resType+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}
                opt.appendTo(optGroup);
        		optGroup.appendTo(sel);
        	}
        }else{
        	var opt;
        	if(fieldValues){
        		opt = $("<option fieldValues='"+fieldValues+"' role='"+resType+"' value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
        	}else{
        		opt = $("<option value='"+fieldEnName+"' role='"+resType+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
        	}
            opt.appendTo(sel);
        }
    });
	
	label.appendTo(div2);
	sel.appendTo(div2);
	div2.appendTo(div1);
	return div1;
}

function createOperatorByResType(counter, resType){
	if(counter<10){
		counter = "0" + counter;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 90px;'></div>");
	var div2 = $("<div class='form-group'></div>");
	var label = $("<label for='operator' class='control-label col-md-4' style='margin-top:8px;'>操作符：</label>");
	var sel = $("<select class='selectpicker bla bla bli operator"+counter+"' id='operator"+counter+"' name='operator' onchange=\"operateChangeEventForUser('"+counter+"');\" style='border:solid 1px #ffcc00;'></select>");
	label.appendTo(div2);
	sel.appendTo(div2);
	div2.appendTo(div1);
	var noWd = $("<option></option>");
	var equ = $("<option value='equ' id='equ"+counter+"'"+">等于</option>");
	var likeMd = $("<option value='likeMd' id='likeMd"+counter+"'"+">模糊</option>");
//	var gt = $("<option value='gt' id='gt"+counter+"'"+">大于</option>");
//	var lt = $("<option value='lt' id='lt"+counter+"'"+">小于</option>");
//	var gte = $("<option value='gte' id='gte"+counter+"'"+">大于等于</option>");
//	var lte = $("<option value='lte' id='lte"+counter+"'"+">小于等于</option>");
	var inMd = $("<option value='inMd' id='inMd"+counter+"'"+">包含</option>");
	//var notIn = $("<option value='notIn' id='notIn"+counter+"'"+">不包含</option>");//暂不支持
	var between = $("<option value='betMd' id='betMd"+counter+"'"+">区间</option>");
	noWd.appendTo(sel);
	equ.appendTo(sel);
	likeMd.appendTo(sel);
//	gt.appendTo(sel);
//	lt.appendTo(sel);
//	gte.appendTo(sel);
//	lte.appendTo(sel);
	inMd.appendTo(sel);
	//notIn.appendTo(sel);
	between.appendTo(sel);
	return div1;
}

function createVal(counter, fieldType, resType){
	if(counter<10){
		counter = "0" + counter;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 90px; width:200px'>");
	var div2 = $("<div class='form-group' id='formGroupFieldValue"+counter+"'></div>");
	var label = $("<label for='fieldValue' class='control-label col-md-4' style='margin-top:8px;margin-right:-50px;'>字段值：</label>");
	label.appendTo(div2);
	div2.appendTo(div1);
	return div1;
}

/**
 * @desc 获得查询字段和值
 * @return json 
 * 数据结构[{"resType":"common","fieldName":"title","operator":"EQUAL","value":"通用资源名称"},
 * 		{"resType":"36","fieldName":"client","operator":"EQUAL","value":"资讯图片委托者"}]
 * 该数据结构可以直接使用JSONArray转化，然后循环得到String对象，然后使用Gson将该对象序列化成QueryConditionItem对象
 * 具体操作可参见QueryConditionItem.java中的示例
 * 
 * */
function getAllQueryConditionsByResType(){
	var resTypeIds = "";
	var checkedData = $("input[id^=resTypeIds]");
	for(var i=0;i<checkedData.length;i++){
		if(checkedData[i].checked==true){
			resTypeIds = resTypeIds+checkedData[i].value+",";	
		}
	}
	if(resTypeIds!=""){
		resTypeIds = "common,"+resTypeIds.substring(0,resTypeIds.length-1);
	}else{
		resTypeIds = "common";
	}
	//alert("选中的id为："+resTypeIds);
	var allParams = "[";
	if(resTypeIds!=null){
		var types = resTypeIds.split(",");
		var typesLen = types.length;
		for(var countNum=0;countNum<typesLen;countNum++){
			//var conditonNum = $("select[role="+types[countNum]+"]");
			var type = types[countNum];
			var tempType = type;
			/*if(type=="1"){
				tempType = "common";
			}*/
			var conditonField = $("select[role="+tempType+"]");
			//alert("查询条件的个数为：" + conditonNum.length);
			var contactWord = "OR";
			//for(var count=1;count<=conditonNum.length;count++){
			$(conditonField).each(function(){
				var params = "{";
				var fieldId = $(this).attr("id");
				var i= fieldId.substring(10, fieldId.length);
				var operator = "";
				var fieldEnName = $("select[id=queryField"+i+"] option:selected").val();
				var fieldType = $("select[id=queryField"+i+"] option:selected").attr("fieldType");
				var resType = $("select[id=queryField"+i+"] option:selected").attr("role");
				
				if( $("#contactWord" + i).length>0){
					contactWord = $("#contactWord" + i + " option:selected").val().toUpperCase(); //获得连接符
				}
				var operatorValue =  $("#operator" + i + " option:selected").val();
				var val = "";
				if(fieldType==2||fieldType==3||fieldType==4){
					$("select[id=fieldValue"+i+"] option:selected").each(function(){
						val += $(this).val()+",";
					});
					if(val.length>1){
						val = val.substring(0, val.length-1);
					}
					//alert("多选值为："+ val); 
				}else if(fieldType==1||fieldType==5){
					val = $("#inputValue" + i).val();
				}else if(fieldType==7){
					if(operatorValue=="betMd"){
						val = $("#modifiedStartTime" + i).val() + "," + $("#modifiedEndTime" + i).val();
					}else{
						val = $("#modifiedStartTime" + i).val();
					}
				}else if(fieldType==6){
					//TODO
					var val = $("#inputValueHideCode" + i).val();
					if(val.length>0){
						val = val.substring(1, val.length-1);
					}
					//alert("获得的中图分类code:" + val);
				}else if(fieldType==9){
					var checkedVals = $('select[name=multiSelEle]').multipleSelect('getSelects', 'value');
					if(checkedVals!=null){
						val = checkedVals.toString();
					}
				}
				//LIKE("like"), IN("in"), EQUAL("="),NOTEQUAL("!="), LT("<"), GT(">"),LTE("<="), GTE(">=");
				if(operatorValue=="equ"){
					operatorValue = "EQUAL";
				}else if(operatorValue=="likeMd"){
					operatorValue = "LIKE";
				}else if(operatorValue=="gt"){
					operatorValue = "GT";
				}else if(operatorValue=="lt"){
					operatorValue = "LT";
				}else if(operatorValue=="gte"){
					operatorValue = "GTE";
				}else if(operatorValue=="lte"){
					operatorValue = "LTE";
				}else if(operatorValue=="inMd"){
					operatorValue = "IN";
				}else if(operatorValue=="betMd"){
					operatorValue = "BETWEEN";
				}
				//alert("第" + i + "个查询字段为：" + fieldEnName + "操作符为：" + operatorValue + "  字段值为：" + val);
				params += "\"resType\"" + ":" + "\"" + type + "\"," + "\"fieldName\"" + ":" + "\"" + fieldEnName + "\"," +
						"\"operator\"" + ":" + "\"" + operatorValue + "\"," + "\"value\"" + ":" + "\"" + val +"\"},";
				//alert(params);
				allParams += params;
			});
		}
	}
	if(allParams!=""&&allParams.length>1){
		allParams = allParams.substring(0, allParams.length-1) + "]";
	}
	
	/* solr拼接方式
	 * if(resTypeIds!=null){
		var allParams = "";
		var types = resTypeIds.split(",");
		var typesLen = types.length;
		for(var countNum=0;countNum<typesLen;countNum++){
			//var conditonNum = $("select[role="+types[countNum]+"]");
			var type = types[countNum];
			if(type=="1"){
				type = "common";
			}
			var conditonField = $("select[role="+type+"]");
			//alert("查询条件的个数为：" + conditonNum.length);
			var contactWord = "OR";
			var params = "";
			//for(var count=1;count<=conditonNum.length;count++){
			$(conditonField).each(function(){
				//params = "";
				var fieldId = $(this).attr("id");
				var i= fieldId.substring(10, fieldId.length);
				var operator = "";
				var fieldEnName = $("select[id=queryField"+i+"] option:selected").val();
				var fieldType = $("select[id=queryField"+i+"] option:selected").attr("fieldType");
				var resType = $("select[id=queryField"+i+"] option:selected").attr("role");
				
				if( $("#contactWord" + i).length>0){
					contactWord = $("#contactWord" + i + " option:selected").val().toUpperCase(); //获得连接符
				}
				var operatorValue =  $("#operator" + i + " option:selected").val();
				var val = "";
				if(fieldType==2||fieldType==3||fieldType==4){
					$("select[id=fieldValue"+i+"] option:selected").each(function(){
						val += $(this).val()+",";
					});
					if(val.length>1){
						val = val.substring(0, val.length-1);
					}
					//alert("多选值为："+ val); 
				}else if(fieldType==1||fieldType==5){
					val = $("#inputValue" + i).val();
				}else if(fieldType==7){
					if(operatorValue=="betMd"){
						val = $("#modifiedStartTime" + i).val() + "," + $("#modifiedEndTime" + i).val();
					}else{
						val = $("#modifiedStartTime" + i).val();
					}
				}else if(fieldType==6){
					//TODO
					var val = $("#inputValueHideCode" + i).val();
					if(val.length>0){
						val = val.substring(1, val.length-1);
					}
					//alert("获得的中图分类code:" + val);
				}else if(fieldType==9){
					var checkedVals = $('select[name=multiSelEle]').multipleSelect('getSelects', 'value');
					if(checkedVals!=null){
						val = checkedVals.toString();
					}
				}
				//alert("第" + i + "个查询字段为：" + fieldEnName + "操作符为：" + operatorValue + "  字段值为：" + val);
				var fieldVals = val.split(",");
				var size = fieldVals.length;
				var iCount = 1;
				$(fieldVals).each(function(index){
					var fieldval = fieldVals[index];
					if(operatorValue=="equ"){
						if(size==1){
							params += " (" + fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
						}else{
							if(iCount==size){
								params += fieldval + ")" + " " + contactWord + " ";
							}else if(iCount==1){
								params += fieldEnName + ":(" + fieldval + ",";
							}else if(iCount<size){
								params += fieldval + ",";
							}
						}
					}else if(operatorValue=="likeMd"){
						if(size==1){
							params += " (" + fieldEnName + ":*" + fieldval + "*)" + " " + contactWord + " ";
						}else{
							if(iCount==size){
								params += fieldEnName + ":*" + fieldval + "*)" + " " + contactWord + " ";
							}else if(iCount==1){
								params += " (" + fieldEnName + ":*" + fieldval + " OR ";
							}else{
								params += fieldEnName + ":*" + fieldval + " OR ";
							}
						}
					}else if(operatorValue=="gt"){
						params += " " + fieldEnName + ":{" + fieldval +" TO * }" + " " + contactWord + " ";
					}else if(operatorValue=="lt"){
						params += " " + fieldEnName + ":{ * TO " + fieldval + "}" + " " + contactWord + " ";
					}else if(operatorValue=="gte"){
						params += " " + fieldEnName + ":[" + fieldval +" TO * ]" + " " + contactWord + " ";
					}else if(operatorValue=="lte"){
						params += " " + fieldEnName + ":[ * TO " + fieldval + "]" + " " + contactWord + " ";
					}else if(operatorValue=="inMd"){
						if(size==1){
							params += " (" + fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
						}else{
							if(iCount==size){
								params += fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
							}else if(iCount==1){
								params += " (" + fieldEnName + ":" + fieldval + " OR ";
							}else{
								params += fieldEnName + ":" + fieldval + " OR ";
							}
						}
					}else if(operatorValue=="notIn"){
						if(size==1){
							params += " (" + fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
						}else{
							if(iCount==size){
								params += fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
							}else if(iCount==1){
								params += " (" + fieldEnName + ":" + fieldval + " AND ";
							}else{
								params += fieldEnName + ":" + fieldval + " AND ";
							}
						}
					}else if(operatorValue=="betMd"){
						if(iCount==size){
							params += " TO " +fieldval+ "]" + " " + contactWord + " ";
						}else{
							params += " " + fieldEnName + ":[" + fieldval;
						}
					}
					iCount++;
				});
				if(params.trim().length>0){
					params = params.trim();
					var contact = params.substring(params.length-2, params.length);
					if(contact=="ND"){
						params = params.substring(0, params.length-3);
					}else if(contact=="OR"){
						params = params.substring(0, params.length-2);
					}
				}
			});
			if(params!=""){
				allParams += types[countNum] + "=" + params + ",";
			}
		}
	}*/
	//alert("拼接后查询条件为：" + allParams);
	return allParams;
}

/**
 * 为用户权限编辑定制
 * json数据格式 [{resType:"40",fieldName:"author",operator:"GTE",value:"漫画资源"},
 * {resType:"41",fieldName:"dautherCode",operator:"GT",value:"作者编码"}]
 **/
function showPrivilegeConditionForEdit(id, jsonAll){
	if(jsonAll!=null&&jsonAll!=""){
		var queryCondition = $("#queryCondition" + id);
		var queryFrame = $("#queryFrame");
		var objAll = JSON.parse(jsonAll);
		var json = "";
		var jsonArr = "[";
		var tempJson = "{";
		$(objAll).each(function(index) {
			var resTypeTemp = objAll[index].resType;
			if(id==resTypeTemp){
				var fieldNameTemp = objAll[index].fieldName;
				var operatorTemp = objAll[index].operator;
				var valueTemp = objAll[index].value;
				tempJson += "\"resType\"" + ":" + "\"" + resTypeTemp + "\"," + "\"fieldName\"" + ":" + "\"" + fieldNameTemp + "\"," +
				"\"operator\"" + ":" + "\"" + operatorTemp + "\"," + "\"value\"" + ":" + "\"" + valueTemp +"\"}," 
			}
		});
		if(tempJson!=null&&tempJson.length>1){
			jsonArr += tempJson.substring(0, tempJson.length-1) +"]";
		}
		//alert("资源id为" + id + "的元数据项：" +jsonArr);
		if(jsonArr!="["){
		var obj = JSON.parse(jsonArr);
		var counter = 0;
		$(obj).each(function(index) {
			var resType = obj[index].resType;
			//alert("资源类型： "+resType);
			var fieldName = obj[index].fieldName;
			var operator = obj[index].operator;
			var value = obj[index].value;
			var metaDatas = creatDefineMetaDataByResType(resType);
			var metaData = getFieldTypeByEnName(fieldName);
			var metaDataJson = JSON.parse(metaData);
			var fieldType = metaDataJson.fieldType;
			var fieldValues = metaDataJson.valueRange;
			//alert("字段类型：" + fieldType + "    字段值：" + fieldValues);
			var field = showSelectedQueryField(resType, metaDatas, fieldName, counter, null);
			var opt = showSelectedOperator(resType, operator, counter, fieldType, null);
			var val = showFieldValue(resType, value, fieldValues, fieldType, counter, null);
			var count = "";
			if(counter<10){
				count += "0" +counter;
			}else{
				count = counter;
			}
			var div = $("<div class='row' id='condition"+count+"'>");
			field.appendTo(div);
			opt.appendTo(div);
			val.appendTo(div);
			var delOpt = $("<a class=\"btn hover-red\" href=\"javascript:deleteQueryCondition('condition"+count+"')\"><i class=\"fa fa-trash-o\"></i>删除</a>");
			delOpt.appendTo(div);
			div.appendTo(queryCondition);
			counter++;
			//$('select').selectpicker('refresh');
			$('.selectpicker').selectpicker({
                'selectedText': 'cat'
            });
			//alert(resType + "   " + fieldName + "   " + operator + "   " + value + "   ");
		});
		}
	}else{
		//$.alert("获取权限数据异常！");
	}
}

/**
 * @desc 显示选择的字段值
 * @param resType 资源类型
 * @param queryFields 对应资源类型元数据字段
 * @param selectedVal 选中值
 * @param lineNumber 标识符
 * @param source 扩展参数（暂时未用）
 * */
function showSelectedQueryField(resType, queryFields, selectedVal, counter, source){
	if(counter<10){
		counter = "0" + counter;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 90px;'></div>");
	var div2 = $("<div class='form-group'></div>");
	var label = $("<label for='queryField' class='control-label col-md-4' style='margin-left:-16px; margin-right:10px; margin-top:8px;'>查询字段：</label>");
	//var sel = $("<select class='selectpicker bla bla bli queryField"+counter+"'"+" id='queryField"+counter+"'"+" name='queryField' data-live-search='true' style='border:solid 1px #ffcc00;'></select>");
	var sel =  $("<select class='selectpicker bla bla bli queryField"+counter+"' id='queryField"+counter+"' role='"+resType+"' name='queryField' onchange=\"fieldChangeEvent('"+counter+"');\" data-live-search='true' style='border:solid 1px #ffcc00;'>");
    //var obj = eval("("+queryFields+")");
	var optNone = $("<option value='' fieldType='-1'></option>");
	optNone.appendTo(sel);
    var obj = JSON.parse(queryFields);
    var map = new Map();
    $(obj).each(function(index) {
    	var metaData = obj[index];
    	var groupId = metaData.groupId;
    	var groupName = metaData.groupName;
    	var optGroup = $("<optgroup label='"+groupName+"' data-subtext='' data-icon='icon-ok'></optgroup>");
    	if(map.get(groupName)==null){
    		optGroup.appendTo(sel);
    		map.put(groupName, optGroup);
    	}
    });
    
    $(obj).each(function(index) {
        var metaData = obj[index];
        var fieldType = metaData.fieldType;
        var groupId = metaData.groupId;
        var fieldEnName = metaData.fieldEnName;
        var fieldZhName = metaData.fieldZhName;
        var fieldValues = metaData.fieldValues;
        //alert("查询字段对应的操作值fieldValues是： " + fieldValues);
        if(groupId){//有分组的
        	var groupName = metaData.groupName;
        	var group = $("optgroup[label="+groupName+"]");
        	if(map.get(groupName)){
        		var opt;
            	if(fieldValues){
            		opt = $("<option fieldValues='"+fieldValues+"' role='"+resType+"' value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}else{
            		opt = $("<option value='"+fieldEnName+"' role='"+resType+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}
            	if(fieldEnName==selectedVal){
            		opt.attr("selected", "selected");
            	}
                opt.appendTo(map.get(groupName));
        	}else{  //不存在该分组
        		var optGroup = $("<optgroup label='"+groupName+"' data-subtext='another test' data-icon='icon-ok'></optgroup>");
        		var opt;
            	if(fieldValues){
            		opt = $("<option fieldValues='"+fieldValues+"'  role='"+resType+"' value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}else{
            		opt = $("<option value='"+fieldEnName+"' role='"+resType+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}
                opt.appendTo(optGroup);
        		optGroup.appendTo(sel);
        	}
        }else{
        	var opt;
        	if(fieldValues){
        		opt = $("<option fieldValues='"+fieldValues+"' role='"+resType+"' value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
        	}else{
        		opt = $("<option value='"+fieldEnName+"' role='"+resType+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
        	}
            opt.appendTo(sel);
        }
    });
    //$('select[id=queryField' + counter + ']').selectpicker('val', selectedVal);
    $('.selectpicker').selectpicker('refresh');
	label.appendTo(div2);
	sel.appendTo(div2);
	div2.appendTo(div1);
	return div1;
}


/**
 * @desc 显示操作符
 * @param resType 资源类型
 * @param selectedOperator 选择操作符
 * @param lineNumber 标识符
 * @param fieldType 字段类型 
 * @param source 扩展参数（暂时未用）
 * 
 * */
function showSelectedOperator(resType, selectedOperator, lineNumber, fieldType, source){
	if(lineNumber<10){
		lineNumber = "0" + lineNumber;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 70px;'></div>");
	var div2 = $("<div class='form-group'></div>");
	var label = $("<label for='operator' class='control-label col-md-4' style='margin-top:8px;'>操作符：</label>");
	//TODO 修改onclick事件
	//var sel = $("<select class='selectpicker bla bla bli operator"+lineNumber+"' id='operator"+lineNumber+"' name='operator' onchange=\"operateDirChangeEvent('"+lineNumber+"');\" style='border:solid 1px #ffcc00;'></select>");
	var sel = $("<select class='selectpicker bla bla bli operator"+lineNumber+"' role='"+resType+"' id='operator"+lineNumber+"' name='operator' onchange=\"operateDirChangeEvent('"+lineNumber+"', '"+fieldType+"');\" style='border:solid 1px #ffcc00;'></select>");
	label.appendTo(div2);
	sel.appendTo(div2);
	div2.appendTo(div1);
	var noWd = $("<option></option>");
	var equ = $("<option value='equ' id='equ"+lineNumber+"'"+">等于</option>");
	var likeMd = $("<option value='likeMd' id='likeMd"+lineNumber+"'"+">模糊</option>");
//	var gt = $("<option value='gt' id='gt"+lineNumber+"'"+">大于</option>");
//	var lt = $("<option value='lt' id='lt"+lineNumber+"'"+">小于</option>");
//	var gte = $("<option value='gte' id='gte"+lineNumber+"'"+">大于等于</option>");
//	var lte = $("<option value='lte' id='lte"+lineNumber+"'"+">小于等于</option>");
	var inMd = $("<option value='inMd' id='inMd"+lineNumber+"'"+">包含</option>");
	//var notIn = $("<option value='notIn' id='notIn"+lineNumber+"'"+">不包含</option>"); 暂不支持
	var between = $("<option value='betMd' id='betMd"+lineNumber+"'"+">区间</option>");
	if(selectedOperator=="EQUAL"){
		//selectedOperator = "equ";
		equ.attr("selected", "selected");
	}else if(selectedOperator=="LIKE"){
		//selectedOperator = "likeMd";
		likeMd.attr("selected", "selected");
	}else if(selectedOperator=="GT"){
		//selectedOperator = "gt";
		gt.attr("selected", "selected");
	}else if(selectedOperator=="LT"){
		//selectedOperator = "lt";
		lt.attr("selected", "selected");
	}else if(selectedOperator=="GTE"){
		//selectedOperator = "gte";
		gte.attr("selected", "selected");
	}else if(selectedOperator=="LTE"){
		//selectedOperator = "lte";
		lte.attr("selected", "selected");
	}else if(selectedOperator=="IN"){
		//selectedOperator = "inMd";
		inMd.attr("selected", "selected");
	}else if(selectedOperator=="BETWEEN"){
		//selectedOperator = "betMd";
		between.attr("selected", "selected");
	}
	noWd.appendTo(sel);
	if(fieldType==1||fieldType==5||fieldType==9){
		equ.appendTo(sel);
		likeMd.appendTo(sel);
//		gt.appendTo(sel);
//		lt.appendTo(sel);
//		gte.appendTo(sel);
//		lte.appendTo(sel);
		inMd.appendTo(sel);
		//notIn.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
	}else if(fieldType==2||fieldType==3||fieldType==4||fieldType==6){
		inMd.appendTo(sel);
		//notIn.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
	}else if(fieldType==7){ //日期
		equ.appendTo(sel);
//		gt.appendTo(sel);
//		lt.appendTo(sel);
//		gte.appendTo(sel);
//		lte.appendTo(sel);
		between.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
	}else if(fieldType==8){ //树形结构
		inMd.appendTo(sel);
		//notIn.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
	}
	/*if(selectedOperator=="EQUAL"){
		selectedOperator = "equ";
	}else if(selectedOperator=="LIKE"){
		selectedOperator = "likeMd";
	}else if(selectedOperator=="GT"){
		selectedOperator = "gt";
	}else if(selectedOperator=="LT"){
		selectedOperator = "lt";
	}else if(selectedOperator=="GTE"){
		selectedOperator = "gte";
	}else if(selectedOperator=="LTE"){
		selectedOperator = "lte";
	}else if(selectedOperator=="IN"){
		selectedOperator = "inMd";
	}else if(selectedOperator=="BETWEEN"){
		selectedOperator = "betMd";
	}*/
	//$('.selectpicker').selectpicker('val', selectedOperator);
	return div1;
}

/**
 * @desc 显示字段值
 * @param resType 资源类型
 * @param selectedVal 选择的值
 * @param fieldValues 字段值
 * @param fieldType 字段类型
 * @param lineNumber 标识符
 * @param source 扩展字段（暂时未用）
 * 
 * */
function showFieldValue(resType, selectedVal, fieldValues, fieldType, lineNumber, source){
	//TODO
	var queryField = $("select[id=queryField"+lineNumber+"] option:selected");
	if(lineNumber<10){
		lineNumber = "0" + lineNumber;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 50px; width:300px'>");
	var div2 = $("<div class='form-group' id='formGroupFieldValue"+lineNumber+"'" +
			"" +
			"" +
			"" +
			"></div>");
	var label = $("<label for='fieldValue' class='control-label col-md-4' style='margin-top:8px;margin-right:-50px;'>字段值：</label>");
	label.appendTo(div2);
	if(fieldValues!=null||fieldValues!=undefined){
		if(fieldType==1){
			var category = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+lineNumber+"' value='" + selectedVal + "' style='width:250px; margin-left:87px;'/>"+
				"</div>");
			category.appendTo(div2);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==6){
			///<input type=\"hidden\" name=\"metadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName()).append("\" value=\"").append(value).append("\" />");
			
			var category = $("<div class='inputVal'>" +
					"<input class='form-control' type='text' id='inputValue"+lineNumber+"' onfocus=\"getFLTX('"+lineNumber+"', '"+fieldType+"');\" style='width:250px; margin-left:87px;'/>"+
					 "<input type='hidden' name='inputValueHideCode' id='inputValueHideCode" + lineNumber +	"'" +
			"</div>");
			category.appendTo(div2);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==7){
			var optVal = $("select[id=operator"+lineNumber+"] option:selected").val();
			label.attr("name", "timeShowLabel");
			div1.attr("name", "timeShowDiv");
			var timeDiv = $(
					"<div class='time' id='time"+lineNumber+"' style='margin-left:-100px;'>"+
						"<div>"+
							"<div class='col-sm-4 start' style='width:130px;float:left;'>"+
								"<input type='text' name='modifiedStartTime"+lineNumber+"' id='modifiedStartTime"+lineNumber+"'"+
									" class='form-control Wdate' onselect='new Date()'"+
										" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime"+lineNumber+"\')}'})'"+
											" onClick='WdatePicker({readOnly:true})'/>"+
							"</div>"+
							"<div class='col-sm-4 end' style='width:130px;float:left;'>"+
								"<input type='text' name='modifiedEndTime"+lineNumber+"' style='display:none;' id='modifiedEndTime"+lineNumber+"'"+
									" class='form-control Wdate' onselect='new Date()'"+
									" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'modifiedStartTime"+lineNumber+"\')}'})'"+
									" onClick='WdatePicker({readOnly:true})'/>"+
							"</div>"+
						"</div>");
			timeDiv.appendTo(div2);
		
		}else if(fieldType==2||fieldType==3||fieldType==4){
			var arrs = fieldValues.split(",");
			var divSel = $("<div class='selVal' id='selVal"+lineNumber+"'></div>");
			var selSingle = $("<select class='selectpicker bla bla bli' name='fieldValue' id='fieldValue"+lineNumber+"' style='width:180px; height:35px; margin-left:30px; border:solid 1px'>");
			var selMulti = $("<select class='selectpicker bla bla bli' name='fieldValue' multiple id='fieldValue"+lineNumber+"' style='width:180px; height:35px; margin-left:30px; border:solid 1px'>");
			$(arrs).each(function(index) {
				var value = arrs[index];
				//2:select 选择框     3:checkBox 复选框     4:radio 单选框
				if(fieldType==2||fieldType==3){
					var opt = $("<option class='get-class'>"+value+"</option>");
					opt.appendTo(selMulti);
				}else if(fieldType==4){
					var opt = $("<option class='get-class'>"+value+"</option>");
					opt.appendTo(selSingle);
				}
			});
			if(fieldType==2||fieldType==3){
				selMulti.appendTo(divSel);
				divSel.appendTo(div2);
			}else if(fieldType==4){
				selSingle.appendTo(divSel);
				divSel.appendTo(div2);
			}
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==9){//数据字典
			label.attr("name", "multiSelEleName");//为修正资源选配页面对齐问题为label添加的属性
			label.css("margin-right", "-9px");
			div2.attr("onclick", "dyAddOpts('" + fieldValues + "','search', null, 'multiSelEle"+lineNumber+"');");
			var mulSelLayer = $("<select multiple='multiple' type='multiSelEle' name='multiSelEle' id='multiSelEle" + lineNumber + "'></select>");
			mulSelLayer.appendTo(div2);
		}
		
	}else{
		var inp = $("<div class='inputVal'>"+
				"<input class='form-control'  value='" + selectedVal + "' type='text' id='inputValue"+lineNumber+"' style='width:250px; margin-left:87px;'/>"+
			"</div>");
		inp.appendTo(div2);
		$('select[name=fieldValue]').selectpicker('refresh');
	}
	div2.appendTo(div1);
	return div1;
}

/**
 * @desc 为用户权限定制
 * 根据资源类型id获得资源类型名称
 * @param 资源类型id
 * @return 资源类型中文名
 * 
 * */
function getResTypeDescByTypeIds(typeIds){
	var typeDesc = "";
	$.ajax({
		url: _appPath + '/user/getResTypeDescByTypeIds.action?publishTypeIds=' + typeIds,
		type: 'get',
		async: false,
		success: function(data){
			typeDesc = data;
		}
	});
	return typeDesc;
}


/**
 * @desc 根据字段英文名称获得字段类型
 * @param eName 英文名称 
 * @return 字段类型
 * */
function getFieldTypeByEnName(enName){
	var fieldType = "";
	$.ajax({
		url: _appPath + '/user/getMetadataDefinitionByName.action?enName=' + enName,
		type: 'get',
		async: false,
		success: function(data){
			fieldType = data;
		}
	});
	return fieldType;
}

/**
 * @desc 根据传过来的数据组装好查询条件
 * @param resType 资源类型
 * @param flag 是否是夸库
 * 高级查询组件前台数据结构
[
	{"groupName":"","fieldEnName":"res_type","groupId":null,"pattern":null,"queryModel":2,"fieldValues":"中文,英文,其他","fieldZhName":"语种","fieldType":2}
	,
	{"groupName":null,"fieldEnName":"cbclass","groupId":"16","pattern":null,"queryModel":2,"fieldValues":"1","fieldZhName":"中图分类","fieldType":6},
	{"groupName":null,"fieldEnName":"review","groupId":"16","pattern":null,"queryModel":2,"fieldValues":"1","fieldZhName":"评价描述","fieldType":1},
	{"groupName":"","fieldEnName":"isbn","groupId":null,"pattern":null,"queryModel":3,"fieldValues":null,"fieldZhName":"ISBN","fieldType":1}
	,
	{"groupName":null,"fieldEnName":"rating","groupId":"16","pattern":null,"queryModel":2,"fieldValues":"1","fieldZhName":"评价等级","fieldType":1},
	{"groupName":null,"fieldEnName":"publishdate","groupId":"16","pattern":null,"queryModel":4,"fieldValues":null,"fieldZhName":"出版日期","fieldType":7},
	{"groupName":null,"fieldEnName":"source","groupId":"17","pattern":null,"queryModel":2,"fieldValues":"flv,mp4,rmvb","fieldZhName":"格式","fieldType":3},
	{"groupName":null,"fieldEnName":"title","groupId":"17","pattern":null,"queryModel":2,"fieldValues":null,"fieldZhName":"标题","fieldType":1}
]


//字段类型 1:text 文本、2:select 选择框、3:checkBox 复选框、4:radio 单选框 5：textarea 文本域  6:byte[] 图片附件 7 date 日期 8 分类lookup 9 数据字典

选择框 单选 复选全部采用bootstrap-multiselect组件实现 checkselect.js

数据字典采用jquery-multiselect组件实现 dictSelect.js
	
用户权限
{"resType":"1","fieldName":"doi","operator":"LIKE", "value":"value"}
 * 
 */
function directCreateQueryCondition(resType, flag){
	/* fengda 2015年11月4日
	 * resType 资源类型
	 * flag 标识符 主要解决是否是夸库
	 * 返回设置的查询条件，获得元数据定义字段
	 */
	var queryFields = creatFieldOpt(resType, flag);
	/*if($("#condition00")){
		$("#condition00").remove();
	}*/
	if(countTimes>0){
		if($("#queryCondition").length>0){
			$("#queryCondition").empty();
		}
		if($("#buttonsFrame").length>0){
			$("#buttonsFrame").remove();
		}
	}
	if(queryFields){
		var obj = JSON.parse(queryFields);
		//获取searchIndex页面中放查询条件的div的id
		var queryCondition = $("#queryCondition");
		//获取searchIndex页面中放查询条件的上一层的div的id
		var queryFrame = $("#queryFrame");
		countTimes++;
		$(obj).each(function(index) {
			++lineCount;
			var count = "";
			if(lineCount<10){
				count += "0" + lineCount;
			}
			var fieldType = obj[index].fieldType;    //元数据中设置的输入类型   1.单文本 2.下拉选择 3.多选 4.单选 5.文本域 6树形 7.日期 8.URL
			var fieldEnName = obj[index].fieldEnName;  //元数据的英文名
			var fieldZhName = obj[index].fieldZhName;  //元数据项的中文名
			var fieldValues = obj[index].fieldValues;  //
			var fieldMaxValue = obj[index].valueLen;   //允许选择的最多的个数
			//var field = dirCreateField(obj[index], lineCount);//生成查询字段(原来的高级查询)
			//var val = dirCreateVal(fieldType, fieldValues, lineCount);//生成字段值(原来的高级查询)
			var field = dirCreateQueryField(obj[index], lineCount);//生成查询字段   fengda 2015年11月17日去除了原来的查询字段的声明
			var opt = dirCreateOperator(fieldType, lineCount);//生成操作符
			var val = dirCreateQueryVal(fieldType, fieldValues,fieldEnName,fieldZhName, lineCount,fieldMaxValue);//fengda 2015年11月19日 生成字段值   去除了字段值：<label>在页面上的显示
			
			var div = $("<div class='row' id='condition"+count+"'>");
			field.appendTo(div);
			
			val.appendTo(div);
			opt.appendTo(div);
			div.appendTo(queryCondition);
		});
		
		if(flag == 'getAcrossStoreCondition'){
			var queryBtn = $("" +
					"<table width='98%' border='0' align='center' id='buttonsFrame' cellpadding='0' cellspacing='0' style='margin-left:16px;'>" +
						"<tr>" +
							"<td>" +
								"<div align='center'>" +
									"<input type='hidden' name='token' value='${token}' />" +
									"<button type='button' class='btn btn-default red' id='queryCondtionButton' style='margin-left:30px;'>查询</button>" +
									"&nbsp;&nbsp;&nbsp;&nbsp;" +
									"<button type='button' class='btn btn-default blue' id='clearCondtionButton' onclick='clearQueryCondition();'>清空</button>" +
								"</div>" +
							"</td>" +
						"</tr>" +
					"</table>");
		}else{
			var queryBtn = $("" +
					"<table width='98%' border='0' align='center' id='buttonsFrame' cellpadding='0' cellspacing='0' style='margin-left:16px;'>" +
						"<tr>" +
							"<td>" +
								"<div align='center'>" +
									"<input type='hidden' name='token' value='${token}' />" +
									"<button type='button' class='btn btn-default red' id='queryCondtionButton' style='margin-left:30px;'>查询</button>" +
									"&nbsp;&nbsp;&nbsp;&nbsp;" +
									"<button type='button' class='btn btn-default blue' id='clearCondtionButton' onclick='clearQueryCondition();'>清空</button>" +
									"&nbsp;&nbsp;&nbsp;&nbsp;" +
									"<button type='button' class='btn btn-default blue' id='exportCondtionButton' onclick='downMetada();'>导出元数据</button>" +
									"&nbsp;&nbsp;&nbsp;&nbsp;" +
									"<button type='button' class='btn btn-default blue' id='exportCondtionButton' onclick='downFileResource();'>下载资源文件</button>" +
								"</div>" +
							"</td>" +
						"</tr>" +
					"</table>");
		}
		
			
		queryBtn.appendTo(queryFrame);
		$('select').selectpicker('refresh');
		return "queryCondtionButton";
	}
}
function directCreateQueryConditionAndRes(resType, flag){
	var resLineCount=0;
	
	var queryFields = creatFieldOpt(resType, flag);
	/*if($("#condition00")){
		$("#condition00").remove();
	}*/
	if(countTimes>0){
		if($("#queryCondition").length>0){
			$("#queryCondition").empty();
		}
		if($("#buttonsFrame").length>0){
			$("#buttonsFrame").remove();
		}
	}
	if(queryFields){
		var obj = JSON.parse(queryFields);
		//获取searchIndex页面中放查询条件的div的id
		var queryCondition = $("#queryCondition");
		//获取searchIndex页面中放查询条件的上一层的div的id
		var queryFrame = $("#queryFrame");
		countTimes++;
		$(obj).each(function(index) {
			++resLineCount;
			var count = "";
			if(resLineCount<10){
				count += "0" + resLineCount;
			}
			var fieldType = obj[index].fieldType;    //元数据中设置的输入类型   1.单文本 2.下拉选择 3.多选 4.单选 5.文本域 6树形 7.日期 8.URL
			var fieldEnName = obj[index].fieldEnName;  //元数据的英文名
			var fieldZhName = obj[index].fieldZhName;  //元数据项的中文名
			var fieldValues = obj[index].fieldValues;  //
			var fieldMaxValue = obj[index].valueLen;   //允许选择的最多的个数
			//var field = dirCreateField(obj[index], resLineCount);//生成查询字段(原来的高级查询)
			//var val = dirCreateVal(fieldType, fieldValues, resLineCount);//生成字段值(原来的高级查询)
			var field = dirCreateQueryField(obj[index], resLineCount);//生成查询字段   fengda 2015年11月17日去除了原来的查询字段的声明
			var opt = dirCreateOperator(fieldType, resLineCount);//生成操作符
			var val = dirCreateQueryValAndRes(fieldType, fieldValues,fieldEnName,fieldZhName, resLineCount,fieldMaxValue);// 2015年12月2日 生成字段值   去除了字段值：<label>在页面上的显示详见该方法注释
			
			var div = $("<div class='row' id='condition"+count+"'>");
			field.appendTo(div);
			
			val.appendTo(div);
			opt.appendTo(div);
			div.appendTo(queryCondition);
		});  
		var queryBtn = $("" +
				"<table width='98%' border='0' align='center' id='buttonsFrame' cellpadding='0' cellspacing='0' style='margin-left:16px;'>" +
					"<tr>" +
						"<td>" +
							"<div align='center'>" +
								"<input type='hidden' name='token' value='${token}' />" +
								"<button type='button' class='btn btn-default red' id='queryCondtionButton' style='margin-left:30px;'>查询</button>" +
								"&nbsp;&nbsp;&nbsp;&nbsp;" +
								"<button type='button' class='btn btn-default blue' id='clearCondtionButton' onclick='clearQueryCondition();'>清空</button>" +
							"</div>" +
						"</td>" +
					"</tr>" +
				"</table>");
			
		queryBtn.appendTo(queryFrame);
		$('select').selectpicker('refresh');
		return "queryCondtionButton";
	}
}


function queryByCondition(queryMethod){
	var getLineCondition = getAllQueryConditions();
	var param = encodeURI(getLineCondition);
	var _url = _appPath + "/search/advSearch.action?conditionParam=" + param;
	//alert("查询条件：" + _url);
	$.ajax({
		url: _url,
		success:function(data){
			//alert(data);
		}
	})
}

//获得输入的查询条件组合
function getAllQueryConditions(){
	var params = "";
	//获取id为queryCondition的div下的所有的子元素，即，所有的查询条件和在查询条件中输入的值
	var conditonNum = $("#queryCondition").children();
//	var contactWord = "OR";
	var contactWord = globalContactWord;//将连接符换为AND huangjun 2015年8月6日 12:12:09
	for(var count=1;count<=conditonNum.length;count++){
		var i = "";
		if(count<10){
			i = "0" + count;
		}
		var operator = "";
		//获取查询字段值
		//var fieldEnName = $("select[id=queryField"+i+"] option:selected").val();
		//获取查询字段的类型，例如input  fieldType =1
		//var fieldType = $("select[id=queryField"+i+"] option:selected").attr("fieldType");
		//由于改变显示方式所以获取方式发生改变
		var fieldEnName = $('#queryField'+i).attr('value');
		//获取查询字段的类型，例如input  fieldType =1
		var fieldType = $('#queryField'+i).attr('fieldType');
		
		if( $("#contactWord" + i).length>0){
			contactWord = $("#contactWord" + i + " option:selected").val().toUpperCase(); //获得连接符
		}
		var operatorValue =  $("#operator" + i + " option:selected").val();
		var val = "";
		if(fieldType==2||fieldType==3||fieldType==4){
			$("select[id=fieldValue"+i+"] option:selected").each(function(){
				val += $(this).val()+",";
			});
			if(val.length>1){
				val = val.substring(0, val.length-1);
			}
			//alert("多选值为："+ val); 
		}else if(fieldType==1||fieldType==5){
			val = $("#inputValue" + i).val();
		}else if(fieldType==7){
			if(operatorValue=="betMd"){
				val = $("#modifiedStartTime" + i).val() + "," + $("#modifiedEndTime" + i).val();
			}else{
				val = $("#modifiedStartTime" + i).val();
			}
		}else if(fieldType==6){
			//TODO
			var val = $("#inputValueHideCode" + i).val();
			if(val.length>0){
				val = val.substring(1, val.length-1);
			}
		//获取查询字段类型为数据字典的被选中的项
		}else if(fieldType==9){
			var checkedVals = $('select[name=multiSelEle]').multipleSelect('getSelects', 'value');
			if(checkedVals!=null){
				val = checkedVals.toString();
			}
			
		}
		
		//alert("第" + i + "个查询字段为：" + fieldEnName + "操作符为：" + operatorValue + "  字段值为：" + val);
		var fieldVals = val.split(",");
		var size = fieldVals.length;
		var iCount = 1;
		$(fieldVals).each(function(index){
			var fieldval = fieldVals[index];
			if(operatorValue=="equ"){
				if(size==1){
					params += " (" + fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
				}else{
					if(iCount==size){
						params += fieldval + ")" + " " + contactWord + " ";
					}else if(iCount==1){
						params += fieldEnName + ":(" + fieldval + ",";
					}else if(iCount<size){
						params += fieldval + ",";
					}
				}
			}else if(operatorValue=="likeMd"){
				if(size==1){
					params += " (" + fieldEnName + ":*" + fieldval + "*)" + " " + contactWord + " ";
				}else{
					if(iCount==size){
						params += fieldEnName + ":*" + fieldval + "*)" + " " + contactWord + " ";
					}else if(iCount==1){
						params += " (" + fieldEnName + ":*" + fieldval + " OR ";
					}else{
						params += fieldEnName + ":*" + fieldval + " OR ";
					}
				}
			}else if(operatorValue=="gt"){
				params += " " + fieldEnName + ":{" + fieldval +" TO * }" + " " + contactWord + " ";
			}else if(operatorValue=="lt"){
				params += " " + fieldEnName + ":{ * TO " + fieldval + "}" + " " + contactWord + " ";
			}else if(operatorValue=="gte"){
				params += " " + fieldEnName + ":[" + fieldval +" TO * ]" + " " + contactWord + " ";
			}else if(operatorValue=="lte"){
				params += " " + fieldEnName + ":[ * TO " + fieldval + "]" + " " + contactWord + " ";
			}else if(operatorValue=="inMd"){
				if(size==1){
					params += " (" + fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
				}else{
					if(iCount==size){
						params += fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
					}else if(iCount==1){
						params += " (" + fieldEnName + ":" + fieldval + " OR ";
					}else{
						params += fieldEnName + ":" + fieldval + " OR ";
					}
				}
			}else if(operatorValue=="notIn"){
				if(size==1){
					params += " (" + fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
				}else{
					if(iCount==size){
						params += fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
					}else if(iCount==1){
						params += " (" + fieldEnName + ":" + fieldval + " AND ";
					}else{
						params += fieldEnName + ":" + fieldval + " AND ";
					}
				}
			}else if(operatorValue=="betMd"){
				if(iCount==size){
					params += " TO " +fieldval+ "]" + " " + contactWord + " ";
				}else{
					params += " " + fieldEnName + ":[" + fieldval;
				}
			}
			iCount++;
		});
	}
	if(params.trim().length>0){
		params = params.trim();
		var contact = params.substring(params.length-2, params.length);
		if(contact=="ND"){
			params = params.substring(0, params.length-3);
		}else if(contact=="OR"){
			params = params.substring(0, params.length-2);
		}
	}
	//alert("拼接后查询条件为：" + params);
	return params;
}

//清空查询条件@Deprecated已废弃 此种清空按照重新加载页面实现的，此种会带来再次加载点击查询 查询按钮会失效的问题
function clearQueryCondition_bak(){
	var conditionSize = $("select[name=queryField]").size();
	//alert("查询条件个数： "+conditionSize);
	var publishType = $("#publishType").val();
	var flag = $("#flag").val();
	for(var i=1;i<=conditionSize;i++){
		var count = "";
		if(i<10){
			count = "0" + i;
		}
		lineCount--;
		$("#condition"+count).remove();
	}
	directCreateQueryCondition(publishType,flag);
	var params = getAllQueryConditions();
	queryForm(params);
}

//新版清空实现，采用bootstrap-select.js中自带方法
function clearQueryCondition(){
	var conditionSize = $("select[name=queryField]").size();
	//alert("查询条件个数： "+conditionSize);
	var publishType = $("#publishType").val();
	var flag = $("#flag").val();
	for(var i=1;i<=conditionSize;i++){
		var count = "";
		if(i<10){
			count = "0" + i;
		}
		lineCount--;
	}
	document.getElementById("myForm").reset();//input输入框采用form的reset方法
	$('.selectpicker').selectpicker('deselectAll');//清空bootstrap-multiple-select选项值，不能用传统jquery
	if(publishType!=null&&publishType!=undefined){
		var seletcCheck = $("span[id^='spanValue']").text('请选择');	//将选择该写成出事的请选择
		$("ul[id^='MenuSelectVal']").empty();//移除已选中项中展示的所有li
		
		$("input[id^='Menu']").val('');	//清空隐藏域中的值
		
		$("ul[id^='select_Value']").children("li").each(function(){
			$(this).removeAttr("class");		//删除下拉展示的被选中项的样式
		});
		
		
		$('input[name^="people_dir"]').val('');	//制空人员隐藏域中的值
		$('input[name^="compary_dir"]').val('');//制空部门隐藏域中的值
		$("select[name=multiSelEle]").multipleSelect("uncheckAll");//清空数据字典下拉框
	}
	
	var params = getAllQueryFieldConditions();
	queryForm(params);
}

//TODO 保存查询条件
function saveConditionAndQuery(){
	
}


function dirCreateField(val, lineNumber){
	/*<select class="selectpicker" disabled>
    	<option>Mustard</option>
    </select>*/
	if(lineNumber<10){
		lineNumber = "0" + lineNumber;
	}
	var fieldEnName = val.fieldEnName;
	var fieldZhName = val.fieldZhName;
	var fieldValues = val.fieldValues;
	//alert("查询字段的值是：" + fieldValues + fieldEnName);
	var fieldType = val.fieldType;
    var div1 = $("<div class='col-md-2' style='margin-right: 50px;'></div>");
	var div2 = $("<div class='form-group'></div>");
	var label = $("<label for='queryField' class='control-label col-md-4' style='margin-left:-16px; margin-right:10px; margin-top:8px;'>查询字段：</label>");
	var sel =  $("<select class='selectpicker bla bla bli queryField"+lineNumber+"' id='queryField"+lineNumber+"' name='queryField' disabled data-live-search='true' style='border:solid 1px #ffcc00;'>");
	var opt = $("<option fieldValues='"+fieldValues+"' value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
	opt.appendTo(sel);
	label.appendTo(div2);
	sel.appendTo(div2);
	div2.appendTo(div1);
	$('select[name=queryField]').selectpicker('refresh');
	return div1;
}



function dirCreateQueryField(val, lineNumber){
	if(lineNumber<10){
		lineNumber = "0" + lineNumber;
	}
	var fieldEnName = val.fieldEnName;
	var fieldZhName = val.fieldZhName;
	var fieldValues = val.fieldValues;
	var fieldType = val.fieldType;
    var div1 = $("<div class='col-md-1' style='margin-right: 140px;'></div>");
	var label = $("<label for='queryField' id='queryField"+lineNumber+"' name='queryField' value='"+fieldEnName+"' fieldType='"+ fieldType +"' class='control-label col-md-4' style='margin-left:210px; margin-right:10px; margin-top:8px;'>"+fieldZhName+"：</label>");
	label.appendTo(div1);
	return div1;
}


function dirCreateOperator(fieldType, lineNumber){
	if(lineNumber<10){
		lineNumber = "0" + lineNumber;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 50px;'></div>");
	var div2 = $("<div class='form-group'></div>");
	//var label = $("<label for='operator' class='control-label col-md-4' style='margin-top:8px;'>操作符：</label>");
	var sel = $("<select class='selectpicker bla bla bli operator"+lineNumber+"' id='operator"+lineNumber+"' name='operator' onchange=\"operateDirChangeEvent('"+lineNumber+"', '"+fieldType+"');\" style='border:solid 1px #ffcc00;width:350px'></select>");
	//label.appendTo(div2);
	sel.appendTo(div2);
	div2.appendTo(div1);
	//var noWd = $("<option></option>");
	var equ = $("<option value='equ' id='equ"+lineNumber+"'"+">精确</option>");
	var likeMd = $("<option value='likeMd' id='likeMd"+lineNumber+"'"+">模糊</option>");
	var inMd = $("<option value='inMd' id='inMd"+lineNumber+"'"+">包含</option>");
	var between = $("<option value='betMd' id='betMd"+lineNumber+"'"+">区间</option>");
	//noWd.appendTo(sel);
	if(fieldType==1||fieldType==5||fieldType==9){
		likeMd.appendTo(sel);
		equ.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
	}else if(fieldType==2||fieldType==3||fieldType==4||fieldType==6){
		inMd.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
	}else if(fieldType==7){ //日期
		//equ.appendTo(sel);
		between.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
	}else if(fieldType==8){ //树形结构
		inMd.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
	}else if(fieldType == 10 || fieldType == 11){
		inMd.appendTo(sel);
	}
	return div1;
}

//TODO生成查询字段值
function dirCreateVal(fieldType, fieldValues, lineNumber){
	var queryField = $("select[id=queryField"+lineNumber+"] option:selected");
	if(lineNumber<10){
		lineNumber = "0" + lineNumber;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 50px; width:350px'>");
	var div2 = $("<div class='form-group' id='formGroupFieldValue"+lineNumber+"'" +
			"" +
			"" +
			"" +
			"></div>");
	var label = $("<label for='fieldValue' class='control-label col-md-4' style='margin-top:8px;margin-right:-50px;'>字段值：</label>");
	label.appendTo(div2);
	
	if(fieldValues!=null||fieldValues!=undefined){
		if(fieldType==1){
			var category = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+lineNumber+"' style='width:250px; margin-left:87px;'/>"+
				"</div>");
			category.appendTo(div2);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==6){
			var category = $("<div class='inputVal'>" +
					"<input class='form-control' type='text' id='inputValue"+lineNumber+"' onfocus=\"getFLTX('"+lineNumber+"', '"+fieldType+"');\" style='width:250px; margin-left:87px;'/>"+
					 "<input type='hidden' name='inputValueHideCode' id='inputValueHideCode" + lineNumber +	"'" +
			"</div>");
			category.appendTo(div2);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==7){
			var optVal = $("select[id=operator"+lineNumber+"] option:selected").val();
			label.attr("name", "timeShowLabel");
			div1.attr("name", "timeShowDiv");
			var timeDiv = $(
					"<div class='time' id='time"+lineNumber+"' style='margin-left:-100px;'>"+
						"<div>"+
							"<div class='col-sm-4 start' style='width:130px;float:left;'>"+
								"<input type='text' name='modifiedStartTime"+lineNumber+"' id='modifiedStartTime"+lineNumber+"'"+
									" class='form-control Wdate' onselect='new Date()'"+
										" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime"+lineNumber+"\')}'})'"+
											" onClick='WdatePicker({readOnly:true})'/>"+
							"</div>"+
							"<div class='col-sm-4 end' style='width:130px;float:left;'>"+
								"<input type='text' name='modifiedEndTime"+lineNumber+"' style='display:none;' id='modifiedEndTime"+lineNumber+"'"+
									" class='form-control Wdate' onselect='new Date()'"+
									" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'modifiedStartTime"+lineNumber+"\')}'})'"+
									" onClick='WdatePicker({readOnly:true})'/>"+
							"</div>"+
						"</div>");
			timeDiv.appendTo(div2);
		
		}else if(fieldType==2||fieldType==3||fieldType==4){
			var arrs = fieldValues.split(",");
			var divSel = $("<div class='selVal' id='selVal"+lineNumber+"'></div>");
			var selSingle = $("<select class='selectpicker bla bla bli' name='fieldValue' id='fieldValue"+lineNumber+"' style='width:180px; height:35px; margin-left:30px; border:solid 1px'>");
			var selMulti = $("<select class='selectpicker bla bla bli' name='fieldValue' multiple id='fieldValue"+lineNumber+"' style='width:180px; height:35px; margin-left:30px; border:solid 1px'>");
			$(arrs).each(function(index) {
				var value = arrs[index];
				if(fieldType==2||fieldType==3){
					var opt = $("<option class='get-class'>"+value+"</option>");
					opt.appendTo(selMulti);
				}else if(fieldType==4){
					var opt = $("<option class='get-class'>"+value+"</option>");
					opt.appendTo(selSingle);
				}
			});
			if(fieldType==2||fieldType==3){
				selMulti.appendTo(divSel);
				divSel.appendTo(div2);
			}else if(fieldType==4){
				selSingle.appendTo(divSel);
				divSel.appendTo(div2);
			}
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==9){//数据字典
			label.attr("name", "multiSelEleName");//为修正资源选配页面对齐问题为label添加的属性
			label.css("margin-right", "-9px");
			//div2.attr("onClick", "dyAddOpts('" + fieldValues + "','search', null, 'multiSelEle"+lineNumber+"');");
			var mulSelLayer = $("<select multiple='multiple' type='multiSelEle' name='multiSelEle' id='multiSelEle" + lineNumber + "'></select>");
			//mulSelLayer.attr("open","dyAddOpts('" + fieldValues + "','search', null, 'multiSelEle"+lineNumber+"');");
			mulSelLayer.appendTo(div2);
		}
		
	}else{
		////}else{
			var inp = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+lineNumber+"' style='width:250px; margin-left:87px;'/>"+
				"</div>");
			inp.appendTo(div2);
			$('select[name=fieldValue]').selectpicker('refresh');
		//}
	}
	div2.appendTo(div1);
	return div1;
}

//生成中图分类树
var treeSetting = {
	view: {
	    dblClickExpand: false,
	    showLine: true,
	    selectedMulti: false
	},
	data: {
		simpleData: {
			enable: true,
			idKey: "id",
			pIdKey: "pid"
		}
	}					
};
//教材版本列表
/*$.ajax({
	url:"${path}/system/FLTX/listContent.action?type=1",
	async:false,
	success:function(data){
		var content = jQuery.parseJSON(data);
		var root = {"id":0,"pid":-1,name:"${param.name}"};
		content.unshift(root);
		var ztree = $.fn.zTree.init($("#tree"), treeSetting,content);
		var root = ztree.getNodes()[0];
		if(root){
			ztree.expandNode(root,true,false,true);
		}
	}
});*/	

//删除查询字段
function deleteQueryCondition(divNumber){
	lineCount--;
	$("#"+divNumber).remove();
}

//生成查询字段
function createField(counter, queryFields){
	if(counter<10){
		counter = "0" + counter;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 90px;'></div>");
	var div2 = $("<div class='form-group'></div>");
	var label = $("<label for='queryField' class='control-label col-md-4' style='margin-left:-16px; margin-right:10px; margin-top:8px;'>查询字段：</label>");
	//var sel = $("<select class='selectpicker bla bla bli queryField"+counter+"'"+" id='queryField"+counter+"'"+" name='queryField' data-live-search='true' style='border:solid 1px #ffcc00;'></select>");
	var sel =  $("<select class='selectpicker bla bla bli queryField"+counter+"' id='queryField"+counter+"' name='queryField' onchange=\"fieldChangeEvent('"+counter+"');\" data-live-search='true' style='border:solid 1px #ffcc00;'>");
    //var obj = eval("("+queryFields+")");
	var optNone = $("<option value='' fieldType='-1'></option>");
	optNone.appendTo(sel);
    var obj = JSON.parse(queryFields);
    var map = new Map();
    $(obj).each(function(index) {
    	var metaData = obj[index];
    	var groupId = metaData.groupId;
    	var groupName = metaData.groupName;
    	var optGroup = $("<optgroup label='"+groupName+"' data-subtext='' data-icon='icon-ok'></optgroup>");
    	if(map.get(groupId)==null){
    		optGroup.appendTo(sel);
    		map.put(groupName, optGroup);
    	}
    });
    
    $(obj).each(function(index) {
        var metaData = obj[index];
        var fieldType = metaData.fieldType;
        var groupId = metaData.groupId;
        var fieldEnName = metaData.fieldEnName;
        var fieldZhName = metaData.fieldZhName;
        var fieldValues = metaData.fieldValues;
        //alert("查询字段对应的操作值fieldValues是： " + fieldValues);
        if(groupId){//有分组的
        	var groupName = metaData.groupName;
        	var group = $("optgroup[label="+groupName+"]");
        	if(map.get(groupName)){
        		var opt;
            	if(fieldValues){
            		opt = $("<option fieldValues='"+fieldValues+"' value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}else{
            		opt = $("<option value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}
                opt.appendTo(map.get(groupName));
        	}else{  //不存在该分组
        		var optGroup = $("<optgroup label='"+groupId+"' data-subtext='another test' data-icon='icon-ok'></optgroup>");
        		var opt;
            	if(fieldValues){
            		opt = $("<option fieldValues='"+fieldValues+"' value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}else{
            		opt = $("<option value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}
                opt.appendTo(optGroup);
        		optGroup.appendTo(sel);
        	}
        }else{
        	var opt;
        	if(fieldValues){
        		opt = $("<option fieldValues='"+fieldValues+"' value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
        	}else{
        		opt = $("<option value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
        	}
            opt.appendTo(sel);
        }
    });
	
	label.appendTo(div2);
	sel.appendTo(div2);
	div2.appendTo(div1);
	return div1;
}

//生成操作符
function createOperator(counter){
	if(counter<10){
		counter = "0" + counter;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 90px;'></div>");
	var div2 = $("<div class='form-group'></div>");
	var label = $("<label for='operator' class='control-label col-md-4' style='margin-top:8px;'>操作符：</label>");
	var sel = $("<select class='selectpicker bla bla bli operator"+counter+"' id='operator"+counter+"' name='operator' onchange=\"operateChangeEvent('"+counter+"');\" style='border:solid 1px #ffcc00;'></select>");
	label.appendTo(div2);
	sel.appendTo(div2);
	div2.appendTo(div1);
	var noWd = $("<option></option>");
	var equ = $("<option value='equ' id='equ"+counter+"'"+">等于</option>");
	var likeMd = $("<option value='likeMd' id='likeMd"+counter+"'"+">模糊</option>");
//	var gt = $("<option value='gt' id='gt"+counter+"'"+">大于</option>");
//	var lt = $("<option value='lt' id='lt"+counter+"'"+">小于</option>");
//	var gte = $("<option value='gte' id='gte"+counter+"'"+">大于等于</option>");
//	var lte = $("<option value='lte' id='lte"+counter+"'"+">小于等于</option>");
	var inMd = $("<option value='inMd' id='inMd"+counter+"'"+">包含</option>");
	//var notIn = $("<option value='notIn' id='notIn"+counter+"'"+">不包含</option>");//暂不支持
	var between = $("<option value='betMd' id='betMd"+counter+"'"+">区间</option>");
	noWd.appendTo(sel);
	equ.appendTo(sel);
	likeMd.appendTo(sel);
//	gt.appendTo(sel);
//	lt.appendTo(sel);
//	gte.appendTo(sel);
//	lte.appendTo(sel);
	inMd.appendTo(sel);
	//notIn.appendTo(sel);
	between.appendTo(sel);
	return div1;
}

//生成字段值
function createVal(counter, fieldType){
	if(counter<10){
		counter = "0" + counter;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 90px; width:200px'>");
	var div2 = $("<div class='form-group' id='formGroupFieldValue"+counter+"'></div>");
	var label = $("<label for='fieldValue' class='control-label col-md-4' style='margin-top:8px;margin-right:-50px;'>字段值：</label>");
	label.appendTo(div2);
	div2.appendTo(div1);
	return div1;
}

//生成连接符
function createContact(counter){
	if(counter<10){
		counter = "0" + counter;
	}
	//生成连接符开始
	var div1 = $("<div class='col-md-2' style='margin-right: 50px;'>");
	var div2 = $("<div class='form-group'></div>");
	var label = $("<label for='contactWord' class='control-label col-md-4' style='margin-top:8px;'>连接符：</label>");
	var sel = $("<select class='selectpicker bla bla bli' name='contactWord' id='contactWord"+counter+"' data-live-search='false' style='border:solid 1px #ffcc00;font-weight: normal;font-family: inherit;font-size: 14px;'>");
	var noCon = $("<option value=''></option>");
	var andCon = $("<option value='and'>并且</option>");
	var orCon = $("<option value='or'>或者</option>");
	noCon.appendTo(sel);
	andCon.appendTo(sel);
	orCon.appendTo(sel);
	label.appendTo(div2);
	sel.appendTo(div2);
	div2.appendTo(div1);
//	生成连接符结束
	return div1;
}

/**
 * @desc 获得元数据定义字段
 * @param resType 资源类型
 * @param flag 标识符 主要解决是否是夸库
 * @return json 
 *	[
 *  	{"resType":"1","groupName":"","fieldEnName":"res_type","groupId":null,"pattern":null,"queryModel":2,"fieldValues":"中文,英文,其他","fieldZhName":"语种","fieldType":2},
 *		{"resType":"36","groupName":null,"fieldEnName":"cbclass","groupId":"16","pattern":null,"queryModel":2,"fieldValues":"1","fieldZhName":"中图分类","fieldType":6}
 *	]
 * 
 * */
function creatFieldOpt(resType, flag){
	if(resType==undefined||resType==""){
		resType = "all";
	}
	var _url = _appPath + "/search/getAllDefineMetaData.action?resType="+resType + "&flag=" + flag;
	var fields = "";
	$.ajax({
		async: false, //这里使用同步的Ajax请求  
		url: _url,
		type: "get",
		success: function(result){
			fields = result;
		}
	});
	return fields;
}

/**
 * @desc 获得元数据定义字段
 * @param resType 资源类型
 * @return json 
 *	[
 *  	{"resType":"1","groupName":"","fieldEnName":"res_type","groupId":null,"pattern":null,"queryModel":2,"fieldValues":"中文,英文,其他","fieldZhName":"语种","fieldType":2},
 *		{"resType":"36","groupName":null,"fieldEnName":"cbclass","groupId":"16","pattern":null,"queryModel":2,"fieldValues":"1","fieldZhName":"中图分类","fieldType":6}
 *	]
 * 
 * */
function creatDefineMetaDataByResType(resType){
	var _url = _appPath + "/search/getDefineMetaDataByResType.action?resType=" + resType;
	var fields = "";
	$.ajax({
		async: false, //这里使用同步的Ajax请求  
		url: _url,
		type: "get",
		success: function(result){
			fields = result;
		}
	});
	return fields;
}

//获得某一个查询字段的值
function getUniqueFieldValueById(id){
	var val = $("#"+id).val();
	return val;
}

function addListenerToField(){
	var field = $("select[name=queryField]").val(); //获得选中查询字段的值
	var fieldType = $("select[name=queryField] option:selected").attr("fieldType"); //获得选中查询字段的属性
	var pattern = $("select[name=queryField] option:selected").attr("pattern"); //获得选中查询字段的属性
	//$("select[name=operator]").remove();
	$("select[name=operator]").attr("value","");
	//$("select[name=contactWord]").empty();
}

//根据当前操作的查询字段和类型控制操作符的显示
function createOptByType(selElement, fieldType){
	var idVal = "";
	if(selElement instanceof jQuery){
		var id = selElement.attr("id");
		if(id){
			idVal = "operator"+id.substring(id.length-2,id.length);
		}
	}else{
		idVal = "operator"+selElement;
	} 
	var equ = $('select[id='+idVal+']').find('[value=equ]');
	var likeMd = $('select[id='+idVal+']').find('[value=likeMd]');
//	var gt = $('select[id='+idVal+']').find('[value=gt]');
//	var lt = $('select[id='+idVal+']').find('[value=lt]');
//	var gte = $('select[id='+idVal+']').find('[value=gte]');
//	var lte = $('select[id='+idVal+']').find('[value=lte]');
	var inMd = $('select[id='+idVal+']').find('[value=inMd]');
	//var notIn = $('select[id='+idVal+']').find('[value=notIn]');  //暂不支持
	var betMd = $('select[id='+idVal+']').find('[value=betMd]');
	if(fieldType==1||fieldType==5){
		equ.show();
		likeMd.show();
//		gt.show();
//		lt.show();
//		gte.show();
//		lte.show();
//		inMd.show();
		//notIn.show();
		betMd.hide();
	}else if(fieldType==9){
		equ.show();
		likeMd.hide();
		inMd.hide();
		betMd.hide();
	}else if(fieldType==2||fieldType==3||fieldType==4||fieldType==6){
		equ.hide();
		likeMd.hide();
//		gt.hide();
//		lt.hide();
//		gte.hide();
//		lte.hide();
//		inMd.show();
		//notIn.show();
		betMd.hide();
	}else if(fieldType==7){ //日期
		equ.show();
		likeMd.hide();
//		gt.show();
//		lt.show();
//		gte.show();
//		lte.show();
		inMd.hide();
		//notIn.hide();
		betMd.show();
	}else if(fieldType==8){ //树形结构
		equ.hide();
		likeMd.hide();
//		gt.hide();
//		lt.hide();
//		gte.hide();
//		lte.hide();
		inMd.show();
		//notIn.show();
		betMd.hide();
	}
	$('select[name=operator]').selectpicker('refresh');
}


function createValByResType(counter, fileType, operator){
	if(counter<10){
		counter = "0" + counter;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 90px; width:150px'>");
	var div2 = $("<div class='form-group' id='formGroupFieldValue"+counter+"'></div>");
	var label = $("<label for='fieldValue' id='fieldValue" + counter + "' class='control-label col-md-4' style='margin-top:8px;'>字段值：</label>");
	label.appendTo(div2);
	div2.appendTo(div1);
	return div1;
}

function createValByType(counter, fileType, operator){
	$(".selVal").css("display", "none");
	$(".time").css("display", "none");
	$(".inputVal input[id=inputValue"+counter+"]").css("display", "none");
	if(fileType==7){
		if(operator=="betMd"){
			$(".time").css("display", "block");
			$(".start").css("display", "block");
			$(".end").css("display", "block");
		}else{
			$(".time").css("display", "block");
			$(".start").css("display", "block");
			$(".end").css("display", "none");
		}
	}else if(fileType==1){
		$(".selVal").css("display", "block");
	}else if(fileType==2){
		$(".inputVal input[id=inputValue"+counter+"]").css("display", "block");
	}else if(fileType==3){
		
	}else if(fileType==4){
		
	}else if(fileType==5){
		
	}else if(fileType==6){
		
	}else if(fileType==8){
		
	}else if(fileType==9){
		
	}
}
//给动态生成的查询字段绑定change事件    onchange='fieldChangeEvent();'
//$('select[name=queryField]').on('click', function() {
/*$(document).on("click", "select[name=queryField]", function(){ 
	alert("bindevent");
	var fieldType = $("select[name=queryField] option:selected").attr("fieldType"); //获得选中查询字段的属性
	var currentEle = $(this); //获得当前选中查询字段的操作符
	createOptByType(currentEle, fieldType);
	createValByType(7, "betMd");
}); */

//
function fieldChangeEvent(counter){
	//alert("field event");
	var inpDiv = $(".inputVal input[id=inputValue"+counter+"]");
	var selDiv = $("select[id=fieldValue"+counter+"]");
	var timeDiv = $("[id=time"+counter+"]");
	if(inpDiv.length>0){
		inpDiv.remove();
	}
	if(selDiv.length>0){
		selDiv.remove();
	}
	if(timeDiv.length>0){
		timeDiv.remove();
	}
	var fieldType = $("select[id=queryField"+counter+"] option:selected").attr("fieldType"); //获得选中查询字段的类型
	//var currentEle = $(this); //获得当前选中查询字段的操作符
	createOptByType(counter, fieldType);
}

//为手动添加查询条件的操作符添加事件  需要根据查询条件和选择的值进行字段值输入框的生成
function operateChangeEvent(counter){
	var valDiv = $("#formGroupFieldValue"+counter);
	var label = $("label[id=fieldValue"+counter+"]");
	var inpDiv = $(".inputVal input[id=inputValue"+counter+"]");
	var selDiv = $("div [id=selVal"+counter+"]");
	var timeDiv = $("[id=time"+counter+"]");
	if(inpDiv.length>0){
		inpDiv.remove();
	}
	if(selDiv.length>0){
		selDiv.remove();
	}
	if(timeDiv.length>0){
		timeDiv.remove();
	}
	//alert("operate event");
	var fieldVal = $("#queryField"+counter +" option:selected").val();
	if(!fieldVal){
		$.alert("请选择查询字段！");
		return;
	}
	var fieldType = $("select[id=queryField"+counter+"] option:selected").attr("fieldType"); //获得当前操作符对应查询字段的类型
	//alert("当前操作符对应的fieldType是：" + fieldType);
	var operateValues = $("select[id=queryField"+counter+"] option:selected").attr("fieldValues"); //获得选中查询字段对应的字段值
	var role = $("select[id=queryField"+counter+"] option:selected").attr("role"); //获得选中查询字段对应的字段值
	var optEnName = $("select[id=queryField"+counter+"] option:selected").val();
	//alert("当前操作符英文名是：" + optEnName);
	//alert("111：" + optEnName);
	operateValues = getMetadataValueByEnName(optEnName); 
	//alert("222：" + operateValues);
	//alert("字段类型：" + fieldType + "   字段值：" + operateValues);
	//if(operateValues){
		//alert("当前操作符对应的字段值是：" + operateValues);
		//var valDiv = $("#formGroupFieldValue"+counter);
		if(fieldType==6){
			//TODO 生成中图分类树
			//alert("生成中图分类树");
			var inp = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+counter+"' onfocus=\"getFLTX('"+counter+"', '"+fieldType+"');\" style='width:250px; margin-left:87px;'/>"+
					"<input type='hidden' name='inputValueHideCode' id='inputValueHideCode" + counter +	"'" +
				"</div>");
			inp.appendTo(valDiv);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==2||fieldType==3||fieldType==4){
			//if(operateValues.trim()!=""){
				var arrs = operateValues.split(",");
				var divSel = $("<div class='selVal' id='selVal"+counter+"'></div>");
				var selSingle = $("<select class='selectpicker bla bla bli' name='fieldValue' id='fieldValue"+counter+"' style='width:180px; height:35px; margin-left:30px; border:solid 1px'>");
				var selMulti = $("<select class='selectpicker bla bla bli' name='fieldValue' multiple id='fieldValue"+counter+"' style='width:180px; height:35px; margin-left:30px; border:solid 1px'>");
				$(arrs).each(function(index) {
					var value = arrs[index];
					//2:select 选择框     3:checkBox 复选框     4:radio 单选框
					if(fieldType==2||fieldType==3){
						var opt = $("<option class='get-class'>"+value+"</option>");
						opt.appendTo(selMulti);
					}else if(fieldType==4){
						var opt = $("<option class='get-class'>"+value+"</option>");
						opt.appendTo(selSingle);
					}
				});
				/*var sel = $("<select class='selectpicker bla bla bli fieldValue' style='width:180px;height:35px;margin-left:30px;' name='fieldValue' id='fieldValue110' " +
						"data-live-search='true' style='border:solid 1px #ffcc00;'><option></option>" +
						"<optgroup label='test' data-subtext='another test' data-icon='icon-ok'>" +
						"<option>ASD</option><option>Bla</option><option>Ble</option>" +
						"</optgroup>" +
						"<option>bull</option>" +
						"<option class='get-class' disabled>ox</option></select>");*/
				if(fieldType==2||fieldType==3||fieldType==4){
					label.css("margin-right","-12px");
					if(fieldType==2||fieldType==3){
						selMulti.appendTo(divSel);
						divSel.appendTo(valDiv);
					}else if(fieldType==4){
						selSingle.appendTo(divSel);
						divSel.appendTo(valDiv);
					}
					$('select[name=fieldValue]').selectpicker('refresh');
				}else if(fieldType==9){//数据字典
					//label.attr("name", "multiSelEleName");//为修正资源选配页面对齐问题为label添加的属性
					//label.css("margin-right", "-9px");
					//valDiv.attr("onclick", "dyAddOpts('" + operateValues + "','edit', null, 'multiSelEle"+counter+"');");
					valDiv.attr("onclick", "dyAddOpts('relation_chdai','edit','null', 'dynasty');");
					var mulSelLayer = $("<select multiple='multiple' type='multiSelEle' name='multiSelEle' id='multiSelEle" + counter + "'></select>");
					mulSelLayer.appendTo(valDiv);
				}
			//}
		}
	//}else{
		//1:text 文本     5:textarea 文本域      6:byte[] 图片附件     9:url  
		else if(fieldType==1||fieldType==5||fieldType==6){
			//alert("jinruwenbenyu");
			label.css("margin-right","-50px");
			var inp = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+counter+"' style='width:180px; margin-left:67px;'/>"+
				"</div>");
			inp.appendTo(valDiv);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==7){
			var optVal = $("select[id=operator"+counter+"] option:selected").val();
			if(optVal=="betMd"){
				var timeDiv = $(
						"<div class='time' id='time"+counter+"' style='margin-left:-100px;'>"+
							"<div>"+
								"<div class='col-sm-4 start' style='width:130px;float:left;'>"+
									"<input type='text' name='modifiedStartTime"+counter+"' id='modifiedStartTime"+counter+"'"+
										" class='form-control Wdate' onselect='new Date()'"+
											" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime"+counter+"\')}'})'"+
												" onClick='WdatePicker({readOnly:true})'/>"+
								"</div>"+
								"<div class='col-sm-4 end' style='width:130px;float:left;'>"+
									"<input type='text' name='modifiedEndTime"+counter+"' id='modifiedEndTime"+counter+"'"+
										" class='form-control Wdate' onselect='new Date()'"+
										" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'modifiedStartTime"+counter+"\')}'})'"+
										" onClick='WdatePicker({readOnly:true})'/>"+
								"</div>"+
							"</div>"+
						"</div>");
				timeDiv.appendTo(valDiv);
			}else{
				var timeDiv = $(
						"<div class='time' id='time"+counter+"' style='margin-left:-100px;'>"+
							"<div>"+
								"<div class='col-sm-4 start' style='width:130px;float:left;'>"+
									"<input type='text' name='modifiedStartTime"+counter+"' id='modifiedStartTime"+counter+"'"+
										" class='form-control Wdate' onselect='new Date()'"+
											" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime"+counter+"\')}'})'"+
												" onClick='WdatePicker({readOnly:true})'/>"+
								"</div>"+
							"</div>"+
						"</div>");
				timeDiv.appendTo(valDiv);
			}
			$('select[name=fieldValue]').selectpicker('refresh');
		}
	//}
}

//为数据字典项填充数据
function fun(operateValues,counter){
	$("select[id=multiSelEle"+counter+"]").multipleSelect({
		multiple: true,
		multipleWidth: 50,
		width: "100%"
	});
	
	
	var arrs = [];
	if(operateValues!=null){
		arrs = operateValues.split(",");
	}
	if(arrs[0]==""&&arrs.length==1){
		return;
	}
	var len = arrs.length;
		$(arrs).each(function(count) {
			var values = arrs[count].split("=");
			
			var $select = $("select[id=multiSelEle"+counter+"]"),
				$opt = $("<option />", {
					title: values[1],
					value: values[0],
					text: values[1]
				});
			$select.append($opt);
			count++;					
		});
	
	$("select[id=multiSelEle"+counter+"]").multipleSelect("refresh");
}
/*
 * 为手动添加查询条件的操作符添加事件  需要根据查询条件和选择的值进行字段值输入框的生成
 * 菜单位置：系统管理-》用户管理-》添加 修改 2015年8月13日 10:30:36 黄俊
 */
function operateChangeEventForUser(counter){
	var valDiv = $("#formGroupFieldValue"+counter);
	var label = $("label[id=fieldValue"+counter+"]");
	var inpDiv = $(".inputVal input[id=inputValue"+counter+"]");
	var selDiv = $("div [id=selVal"+counter+"]");
	var timeDiv = $("[id=time"+counter+"]");
	if(inpDiv.length>0){
		inpDiv.remove();
	}
	if(selDiv.length>0){
		selDiv.remove();
	}
	if(timeDiv.length>0){
		timeDiv.remove();
	}
	var fieldVal = $("#queryField"+counter +" option:selected").val();
	if(!fieldVal){
		$.alert("请选择查询字段！");
		return;
	}
	var fieldType = $("select[id=queryField"+counter+"] option:selected").attr("fieldType"); //获得当前操作符对应查询字段的类型
	var operateValues = $("select[id=queryField"+counter+"] option:selected").attr("fieldValues"); //获得选中查询字段对应的字段值
	var role = $("select[id=queryField"+counter+"] option:selected").attr("role"); //获得选中查询字段对应的字段值
	var optEnName = $("select[id=queryField"+counter+"] option:selected").val();   
	operateValues = getMetadataValueByEnName(optEnName);   //获取被选中值在系统配置中的具体参数值，例如若是数据字典，将查询到该数据字典下的所有项
		if(fieldType==6){
			//TODO 生成中图分类树
			var inp = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+counter+"' onfocus=\"getFLTX('"+counter+"', '"+fieldType+"');\" style='width:250px; margin-left:87px;'/>"+
					"<input type='hidden' name='inputValueHideCode' id='inputValueHideCode" + counter +	"'" +
				"</div>");
			inp.appendTo(valDiv);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==2||fieldType==3||fieldType==4){
				var arrs = operateValues.split(",");
				var divSel = $("<div class='selVal' id='selVal"+counter+"'></div>");
				var selSingle = $("<select class='selectpicker bla bla bli' name='fieldValue' id='fieldValue"+counter+"' style='width:180px; height:35px; margin-left:30px; border:solid 1px'>");
				var selMulti = $("<select class='selectpicker bla bla bli' name='fieldValue' multiple id='fieldValue"+counter+"' style='width:180px; height:35px; margin-left:30px; border:solid 1px'>");
				$(arrs).each(function(index) {
					var value = arrs[index];
					//2:select 选择框     3:checkBox 复选框     4:radio 单选框
					if(fieldType==2||fieldType==3){
						var opt = $("<option class='get-class'>"+value+"</option>");
						opt.appendTo(selMulti);
					}else if(fieldType==4){
						var opt = $("<option class='get-class'>"+value+"</option>");
						opt.appendTo(selSingle);
					}
				});
				if(fieldType==2||fieldType==3||fieldType==4){
					label.css("margin-right","-12px");
					if(fieldType==2||fieldType==3){
						selMulti.appendTo(divSel);
						divSel.appendTo(valDiv);
					}else if(fieldType==4){
						selSingle.appendTo(divSel);
						divSel.appendTo(valDiv);
					}
					$('select[name=fieldValue]').selectpicker('refresh');
				}
		}
		else if(fieldType==9){//数据字典
			label.attr("name", "multiSelEleName");//为修正资源选配页面对齐问题为label添加的属性
			label.css("margin-right", "24px");
			var mulSelLayer = $("<select multiple='multiple' type='multiSelEle' name='multiSelEle' id='multiSelEle" + counter + "' style='width:200px;height:24px'></select>");
			mulSelLayer.appendTo(valDiv);
			fun(operateValues,counter);
		}
		//1:text 文本     5:textarea 文本域      6:byte[] 图片附件     9:url  
		else if(fieldType==1||fieldType==5||fieldType==6){
			//alert("jinruwenbenyu");
			label.css("margin-right","-50px");
			var inp = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+counter+"' style='width:180px; margin-left:67px;'/>"+
				"</div>");
			inp.appendTo(valDiv);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==7){
			var optVal = $("select[id=operator"+counter+"] option:selected").val();
			if(optVal=="betMd"){
				var timeDiv = $(
						"<div class='time' id='time"+counter+"' style='margin-left:-100px;'>"+
							"<div style='width:200px; margin-left:152px;'>"+
								"<div class='col-sm-4 start' style='width:50%;float:left;'>"+
									"<input type='text' name='modifiedStartTime"+counter+"' id='modifiedStartTime"+counter+"'"+
										" class='form-control Wdate' onselect='new Date()'"+
											" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime"+counter+"\')}'})'"+
												" onClick='WdatePicker({readOnly:true})'/>"+
								"</div>"+
								"<div class='col-sm-4 end' style='width:50%;float:left;'>"+
									"<input type='text' name='modifiedEndTime"+counter+"' id='modifiedEndTime"+counter+"'"+
										" class='form-control Wdate' onselect='new Date()'"+
										" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'modifiedStartTime"+counter+"\')}'})'"+
										" onClick='WdatePicker({readOnly:true})'/>"+
								"</div>"+
							"</div>"+
						"</div>");
				timeDiv.appendTo(valDiv);
			}else{
				var timeDiv = $(
						"<div class='time' id='time"+counter+"' style='margin-left:-100px;'>"+
							"<div style='margin-left:152px;'>"+
								"<div class='col-sm-4 start' style='width:180px;float:left;'>"+
									"<input type='text' name='modifiedStartTime"+counter+"' id='modifiedStartTime"+counter+"'"+
										" class='form-control Wdate' onselect='new Date()'"+
											" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime"+counter+"\')}'})'"+
												" onClick='WdatePicker({readOnly:true})'/>"+
								"</div>"+
							"</div>"+
						"</div>");
				timeDiv.appendTo(valDiv);
			}
			$('select[name=fieldValue]').selectpicker('refresh');
		}
	//}
}

function getMetadataValueByEnName(enName){
	var fields = "";
	if(enName!=undefined||enName!=""){
		var _url = _appPath + "/search/getMetaDataValueByEnName.action?enName="+enName;
		
		$.ajax({
			async: false, //这里使用同步的Ajax请求  
			url: _url,
			type: "get",
			success: function(result){
				fields = result;
			}
		});
	}
	return fields;
}

//为直接生成查询条件的操作符添加时间    这里只 针对时间类型 fieldType==7
function operateDirChangeEvent(counter, fieldType){
	if(fieldType==7){
		var optVal = $("select[id=operator"+counter+"] option:selected").val();
		if(optVal=="betMd"){
			$("#modifiedEndTime"+counter).css("display", "block");
		}else{
			$("#modifiedEndTime"+counter).css("display", "none");
		}
		$('select[name=fieldValue]').selectpicker('refresh');
	}
}

//中图分类弹出层
function getFLTX(lineNumber, fieldType){
	if(fieldType==6){
		$.openWindow("system/dataManagement/classification/ztflSelect.jsp?fieldName=cbclassSearch&isMain=1&lineNumber=" + lineNumber,'选择分类', 500, 450);
	}
}


function directCreateQueryConditionBySource(resType, flag, source){
	//function directCreateQueryCondition(){
		var queryFields = creatFieldOpt(resType, flag);
		/*if($("#condition00")){
			$("#condition00").remove();
		}*/
		if(countTimes>0){
			if($("#queryCondition").length>0){
				$("#queryCondition").empty();
			}
			if($("#buttonsFrame").length>0){
				$("#buttonsFrame").remove();
			}
		}
		if(queryFields){
			var obj = JSON.parse(queryFields);
			var queryCondition = $("#queryCondition");
			var queryFrame = $("#queryFrame");
			countTimes++;
			$(obj).each(function(index) {
				++lineCount;
				var count = "";
				if(lineCount<10){
					count += "0" + lineCount;
				}
				var fieldType = obj[index].fieldType;
				var fieldEnName = obj[index].fieldEnName;
				var fieldZhName = obj[index].fieldZhName; 
				var fieldValues = obj[index].fieldValues;
				var field = dirCreateField(obj[index], lineCount);
				var opt = dirCreateOperator(fieldType, lineCount);
				var val = dirCreateVal(fieldType, fieldValues, lineCount);
				var div = $("<div class='row' id='condition"+count+"'>");
				field.appendTo(div);
				opt.appendTo(div);
				val.appendTo(div);
				div.appendTo(queryCondition);
			}); 
			var queryBtn = "";
			if(source=="resSelect"){
				queryBtn = $("" +
						"<table width='98%' border='0' align='center' id='buttonsFrame' cellpadding='0' cellspacing='0' style='margin-left:16px;'>" +
							"<tr>" +
								"<td>" +
									"<div align='center'>" +
										"<input type='hidden' name='token' value='${token}' />" +
										"<button type='button' class='btn btn-primary' id='queryCondtionButton' style='margin-left:30px;'>查询</button>" +
										"&nbsp;&nbsp;&nbsp;&nbsp;" +
										"<button type='button' class='btn btn-primary' id='clearCondtionButton' onclick='clearQueryCondition();'>清空</button>" +
									"</div>" +
								"</td>" +
							"</tr>" +
						"</table>");
			}
			
				
			queryBtn.appendTo(queryFrame);
			$('select').selectpicker('refresh');
			return "queryCondtionButton";
		}
	}
function createDivLayer(count, enName, dict, type, opt){
	var showInput = "";
	var hideInput = "";
	if(type=="2"){
		showInput = $("<select id='condition"+count+"'>");
		hideInput = $("<div class='row' id='condition"+count+"'>");
		/*<select multiple='multiple'>
        <optgroup label=''Group 1'>
        <option value='1'>北京</option>*/
	}else if(type=="3"){
		showInput = $("<select id='condition"+count+"'>");
		hideInput = $("<div class='row' id='condition"+count+"'>");
	}else if(type=="4"){
		showInput = $("<select id='condition"+count+"'>");
		hideInput = $("<div class='row' id='condition"+count+"'>");
	}else if(type=="9"){
		showInput = $("<select id='condition"+count+"'>");
		hideInput = $("<div class='row' id='condition"+count+"'>");
	}
	
	var valNum = 20;
	if(fieldValues!=null){
		var arrs = fieldValues.split(",");
		var divSel = $("<div class='selVal' id='selVal"+lineNumber+"'></div>");
		var selSingle = $("<select class='selectpicker bla bla bli' name='fieldValue' id='fieldValue"+lineNumber+"' style='width:180px; height:35px; margin-left:30px; border:solid 1px'>");
		var selMulti = $("<select class='selectpicker bla bla bli' name='fieldValue' multiple id='fieldValue"+lineNumber+"' style='width:180px; height:35px; margin-left:30px; border:solid 1px'>");
		if(arrs==null){
			return;
		}else{
			var arrSize = arrs.length;
			if(arrSize<valNum){
				$(arrs).each(function(index) {
					var value = arrs[index];
					//2:select 选择框     3:checkBox 复选框     4:radio 单选框
					if(fieldType==2||fieldType==3){
						var opt = $("<option class='get-class'>"+value+"</option>");
						opt.appendTo(selMulti);
					}else if(fieldType==4){
						var opt = $("<option class='get-class'>"+value+"</option>");
						opt.appendTo(selSingle);
					}
				});
				if(fieldType==2||fieldType==3){
					selMulti.appendTo(divSel);
					divSel.appendTo(div2);
				}else if(fieldType==4){
					selSingle.appendTo(divSel);
					divSel.appendTo(div2);
				}
			}else{
				
			}
			
		}
	}
}

function divLayerListener(){
	$("select").multipleSelect({
		multiple: true,
		multipleWidth: 155,
		width: '40%'
	});
}

function directCreateQueryConditionForResSel(resType, flag){
//function directCreateQueryCondition(){
	var queryFields = creatFieldOpt(resType, flag);
	/*if($("#condition00")){
		$("#condition00").remove();
	}*/
	if(countTimes>0){
		if($("#queryCondition").length>0){
			$("#queryCondition").empty();
		}
		if($("#buttonsFrame").length>0){
			$("#buttonsFrame").remove();
		}
	}
	if(queryFields){
		var obj = JSON.parse(queryFields);
		var queryCondition = $("#queryCondition");
		var queryFrame = $("#queryFrame");
		countTimes++;
		$(obj).each(function(index) {
			++lineCount;
			var count = "";
			if(lineCount<10){
				count += "0" + lineCount;
			}
			var fieldType = obj[index].fieldType;
			var fieldEnName = obj[index].fieldEnName;
			var fieldZhName = obj[index].fieldZhName; 
			var fieldValues = obj[index].fieldValues;
			var field = dirCreateField(obj[index], lineCount);
			var opt = dirCreateOperator(fieldType, lineCount);
			var val = dirCreateVal(fieldType, fieldValues, lineCount);
			var div = $("<div class='row' id='condition"+count+"'>");
			field.appendTo(div);
			opt.appendTo(div);
			val.appendTo(div);
			div.appendTo(queryCondition);
		});  
		var queryBtn = $("" +
				"<table width='98%' border='0' align='center' id='buttonsFrame' cellpadding='0' cellspacing='0' style='margin-left:16px;'>" +
					"<tr>" +
						"<td>" +
							"<div align='center'>" +
								"<input type='hidden' name='token' value='${token}' />" +
								"<button type='button' class='btn btn-primary' id='queryCondtionButton' onclick='queryForm();' style='margin-left:30px;'>查询</button>" +
								"&nbsp;&nbsp;&nbsp;&nbsp;" +
								"<button type='button' class='btn btn-primary' id='clearCondtionButton' onclick='clearQueryCondition();'>清空</button>" +
//									"&nbsp;&nbsp;&nbsp;&nbsp;" +
//									"<button type='button' class='btn btn-primary' id='exportCondtionButton' >导出</button>" +
							"</div>" +
						"</td>" +
					"</tr>" +
				"</table>");
			
		queryBtn.appendTo(queryFrame);
		$('select').selectpicker('refresh');
		return "queryCondtionButton";
	}
} 

function modifyCss(source){
	if(source=="resSelect"){
		$("div[name=timeShowDiv]").css("width", "450px");
		$("label[name=timeShowLabel]").css("margin-right", "-68px");
	}else{
		$("div[name=timeShowDiv]").css({
			"width": "450px",
			"margin-left":"-32px"
		});
		$("label[name=timeShowLabel]").css("margin-right", "-45px");
	}
	
}


/*
 * fengda 2015年11月19日
 * 生成输入查询字段内容框根据不同的类型（文本框，下拉，数据字典）
 * fieldType  元数据中设置的输入类型   1.单文本 2.下拉选择 3.多选 4.单选 5.文本域 6树形 7.日期 8.URL
 * fieldValues 
 * fieldEnName 元数据的英文名称
 * fieldZhName 元数据的中文名称
 * fieldMaxValue 允许输入的最大值
 */

function dirCreateQueryVal(fieldType, fieldValues,fieldEnName,fieldZhName, lineNumber,fieldMaxValue){
	var queryField = $("select[id=queryField"+lineNumber+"] option:selected");
	if(lineNumber<10){
		lineNumber = "0" + lineNumber;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 50px; width:350px'>");
	var div2 = $("<div class='form-group' id='formGroupFieldValue"+lineNumber+"'" +
			"" +
			"" +
			"" +
			"></div>");
	//去除了字段值：<label>在页面上的显示
	if(fieldValues!=null||fieldValues!=undefined){
		if(fieldType==1){
			var category = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+lineNumber+"' style='width:250px; margin-left:87px;'/>"+
				"</div>");
			category.appendTo(div2);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==6){
			///<input type=\"hidden\" name=\"metadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName()).append("\" value=\"").append(value).append("\" />");
			
			var category = $("<div class='inputVal'>" +
					"<input class='form-control' type='text' id='inputValue"+lineNumber+"' onfocus=\"getFLTX('"+lineNumber+"', '"+fieldType+"');\" style='width:250px; margin-left:87px;'/>"+
					 "<input type='hidden' name='inputValueHideCode' id='inputValueHideCode" + lineNumber +	"'" +
			"</div>");
			category.appendTo(div2);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==7){
		//	var optVal = $("select[id=operator"+lineNumber+"] option:selected").val();
			//label.attr("name", "timeShowLabel");
			//div1.attr("name", "timeShowDiv");
			var timeDiv = $(
					"<div class='time' id='time"+lineNumber+"' style='width:250px; margin-left:87px;'>"+
						"<div>"+
							"<div class='col-sm-4 start' style='width:110px;float:left;'>"+
								"<input type='text' name='modifiedStartTime"+lineNumber+"' id='modifiedStartTime"+lineNumber+"'"+
									" class='form-control Wdate' onselect='new Date()'"+
										" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime"+lineNumber+"\')}'})'"+
											" onClick='WdatePicker({readOnly:true})'/>"+
							"</div>"+
							"<div class='col-sm-4 end' style='width:110px;float:left;'>"+
								"<input type='text' name='modifiedEndTime"+lineNumber+"' id='modifiedEndTime"+lineNumber+"'"+
									" class='form-control Wdate' onselect='new Date()'"+
									" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'modifiedStartTime"+lineNumber+"\')}'})'"+
									" onClick='WdatePicker({readOnly:true})'/>"+
							"</div>"+
						"</div>"+
					"</div>");
			timeDiv.appendTo(div2);
		
		}else if(fieldType==3||fieldType==4 || fieldType==2){
			var dir = $("<div class='inputVal'> <div id='lead' class='card'>");
			var selet = $("<div class='topnav' style='width:300px; margin-left:85px;'>" + "<a id='Div_"+lineNumber+"'  href='javascript:void(0);' class='as'>" +
								"<span id='spanValue"+lineNumber+"' title=''>请选择</span>"+"</a>"+
						  "</div>"+
						  "<input type='hidden' value='' id= 'Menu"+lineNumber+"' name='Menu"+lineNumber+"'/>"+
						  "<input type='hidden' value='"+fieldMaxValue+"' id='MaxNumber"+lineNumber+"'/>"+"</div>"+
						  "<div id='m2"+lineNumber+"' class='xmenu' style='display: none;'>"+
						  		"<div class='select-info'>"+"<label class='top-label'>已选"+fieldZhName+"：</label> <ul id='MenuSelectVal"+lineNumber+"'> </ul>"+
						  			"<a name='menu-confirm' href='javascript:void(0);' class='a-btn'> <span class='a-btn-text'>确定</span> </a> " +
						  		"</div>" +
						  		"<dl> <dt class=\"open\">选择"+fieldZhName+"</dt> <dd><ul style='width:380px;overflow-y:scroll;' id='select_Value"+lineNumber+"'> </ul></dd> </dl> " +
						 "</div></div> </div>");
			var script = $("<script type='text/javascript'>function initMenu"+ lineNumber +"(){$('#Div_"+lineNumber+"').xMenu({width :400,eventType:'click',dropmenu:'#m2"+lineNumber+"',hiddenID : 'Menu"+lineNumber+"' });if($('#m2"+lineNumber+"').height()>300){ $('#select_Value"+lineNumber+"').height('300px');}}</script>");
			xmenuNum += "initMenu"+lineNumber+",";
			selet.appendTo(dir);
			script.appendTo(dir);
			dir.appendTo(div2);
		}else if(fieldType==9){//数据字典
			var dir = $("<div id='lead' class='card'>");
			var selet = $("<div class='topnav'>" + "<a id='Div_"+lineNumber+"'  href='javascript:void(0);' class='as'>" +
								"<span id='spanValue"+lineNumber+"' title=''>请选择</span>"+"</a>"+
						  "</div>"+
					"<input type='text' value='' id= 'Menu"+lineNumber+"' name='Menu"+lineNumber+"'/>"+
					"<input type='hidden' value='"+fieldMaxValue+"' id='MaxNumber"+lineNumber+"'/>"+"</div>"+
					"<div id='m2"+lineNumber+"' class='xmenu' style='display: none;'>"+
						"<div class='select-info'>"+"<label class='top-label'>已选"+fieldZhName+"：</label> <ul> </ul>"+
							"<a name='menu-confirm' href='javascript:void(0);' class='a-btn'> <span class='a-btn-text'>确定</span> </a> " +
						"</div>" +
						"<dl> <dt class=\"open\">选择"+fieldZhName+"</dt> <dd><ul id='select_Value"+lineNumber+"'> </ul></dd> </dl> " +
					"</div>"+
					"<script type='text/javascript'>"+
					"$(document).ready(function(){"+
					"$('Div_"+lineNumber+"').xMenu({"+
					"width :600,"+
					"eventType:'click',"+
					"dropmenu:'#m2"+lineNumber+"',"+
					"hiddenID : 'MaxNumber"+lineNumber+"' });"+
					" }); "+
					"</script>");
			selet.appendTo(dir);
			dir.appendTo(div2);
		}else if(fieldType == 10){  //人员
			var compary = $("<script type=\'text/javascript\'>"+
								"function clear"+fieldEnName+"Category() {"+
									"$('#"+fieldEnName+"Name').val('');"+
									"$('#"+fieldEnName+"').val('');"+
								"}"+
								"function show"+fieldEnName+"Category() {"+
									"$.openWindow('"+_appPath+"/system/peopleUnit/peopleUnit.jsp?divWidth=1150px&fieldName="+fieldEnName+
									"&valueLength="+fieldMaxValue+"&check=1','"+fieldZhName+"','1200px','530px')"+
								"}"+
							"</script>"+
							"<div class='inputVal'>"+
										 	"<input type='text' name="+fieldEnName+"Name id="+fieldEnName+"Name style=\'width:150px; margin-left:87px;display: inline;\' readonly=\'readonly\'  class='form-control' value=''>"+
										 	"<input type='hidden' name='people_dir"+lineNumber+"' id="+fieldEnName+" value=''>"+
										 	"<a onclick='show"+fieldEnName+"Category();'href=\"###\" class='btn-primary' role='button' style='margin-left: 1%;display: inline;'><img src=\""+_appPath+"/appframe/main/images/select.png\" alt=\"选择\"></a>"+
										 	"<a onclick='clear"+fieldEnName+"Category();'href=\"###\" class='btn-primary' role='button' style='margin-left: 1%;display: inline;'><img src=\""+_appPath+"/appframe/main/images/clean.png\" alt=\"清除\"></a>"+
							  "</div>");
			compary.appendTo(div2);
		}else if(fieldType == 11){  //单位
			var people = $("<script type=\'text/javascript\'>"+
					"function clear"+fieldEnName+"Category() {"+
						"$('#"+fieldEnName+"Name').val('');"+
						"$('#"+fieldEnName+"').val('');"+
					"}"+
					"function show"+fieldEnName+"Category() {"+
						"$.openWindow('"+_appPath+"/system/peopleUnit/companyUnit.jsp?divWidth=1150px&fieldName="+fieldEnName+
						"&valueLength="+fieldMaxValue+"&check=1','"+fieldZhName+"','1200px','530px')"+
					"}"+
				"</script>"+
				"<div class='inputVal'>"+
					"<div class='form-group'>"+
							 	"<input type='text' name="+fieldEnName+"Name id="+fieldEnName+"Name style=\'width:150px; margin-left:100px;display: inline;\' readonly=\'readonly\'  class='form-control' value=''>"+
							 	"<input type='hidden' name='compary_dir"+lineNumber+"' id="+fieldEnName+" value=''>"+
							 	"<a onclick='show"+fieldEnName+"Category();'href=\"###\" class='btn-primary' role='button' style='margin-left: 1%;display: inline;'><img src=\""+_appPath+"/appframe/main/images/select.png\" alt=\"选择\"></a>"+
							 	"<a onclick='clear"+fieldEnName+"Category();'href=\"###\" class='btn-primary' role='button' style='margin-left: 1%;display: inline;'><img src=\""+_appPath+"/appframe/main/images/clean.png\" alt=\"清除\"></a>"+
				  "</div>");
			people.appendTo(div2);
		}
		
	}else{
		////}else{
			var inp = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+lineNumber+"' style='width:250px; margin-left:87px;'/>"+
				"</div>");
			inp.appendTo(div2);
			$('select[name=fieldValue]').selectpicker('refresh');
		//}
	}
	div2.appendTo(div1);
	return div1;
}
//2015年12月2日添加资源中的高级查询中的弹窗将openwindow改成layer打开   （10,11）类型修改
function dirCreateQueryValAndRes(fieldType, fieldValues,fieldEnName,fieldZhName, lineNumber,fieldMaxValue){
	var queryField = $("select[id=queryField"+lineNumber+"] option:selected");
	if(lineNumber<10){
		lineNumber = "0" + lineNumber;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 50px; width:350px'>");
	var div2 = $("<div class='form-group' id='formGroupFieldValue"+lineNumber+"'" +
			"" +
			"" +
			"" +
			"></div>");
	//去除了字段值：<label>在页面上的显示
	if(fieldValues!=null||fieldValues!=undefined){
		if(fieldType==1){
			var category = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+lineNumber+"' style='width:250px; margin-left:87px;'/>"+
				"</div>");
			category.appendTo(div2);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==6){
			///<input type=\"hidden\" name=\"metadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName()).append("\" value=\"").append(value).append("\" />");
			
			var category = $("<div class='inputVal'>" +
					"<input class='form-control' type='text' id='inputValue"+lineNumber+"' onfocus=\"getFLTX('"+lineNumber+"', '"+fieldType+"');\" style='width:250px; margin-left:87px;'/>"+
					 "<input type='hidden' name='inputValueHideCode' id='inputValueHideCode" + lineNumber +	"'" +
			"</div>");
			category.appendTo(div2);
			$('select[name=fieldValue]').selectpicker('refresh');
		}else if(fieldType==7){
		//	var optVal = $("select[id=operator"+lineNumber+"] option:selected").val();
			//label.attr("name", "timeShowLabel");
			//div1.attr("name", "timeShowDiv");
			var timeDiv = $(
					"<div class='time' id='time"+lineNumber+"' style='width:250px; margin-left:87px;'>"+
						"<div>"+
							"<div class='col-sm-4 start' style='width:110px;float:left;'>"+
								"<input type='text' name='modifiedStartTime"+lineNumber+"' id='modifiedStartTime"+lineNumber+"'"+
									" class='form-control Wdate' onselect='new Date()'"+
										" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'modifiedEndTime"+lineNumber+"\')}'})'"+
											" onClick='WdatePicker({readOnly:true})'/>"+
							"</div>"+
							"<div class='col-sm-4 end' style='width:110px;float:left;'>"+
								"<input type='text' name='modifiedEndTime"+lineNumber+"' id='modifiedEndTime"+lineNumber+"'"+
									" class='form-control Wdate' onselect='new Date()'"+
									" onfocus='WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'modifiedStartTime"+lineNumber+"\')}'})'"+
									" onClick='WdatePicker({readOnly:true})'/>"+
							"</div>"+
						"</div>"+
					"</div>");
			timeDiv.appendTo(div2);
		
		}else if(fieldType==3||fieldType==4 || fieldType==2){
			var dir = $("<div class='inputVal'> <div id='lead' class='card'>");
			var selet = $("<div class='topnav' style='width:300px; margin-left:85px;'>" + "<a id='Div_"+lineNumber+"'  href='javascript:void(0);' class='as'>" +
								"<span id='spanValue"+lineNumber+"' title=''>请选择</span>"+"</a>"+
						  "</div>"+
						  "<input type='hidden' value='' id= 'Menu"+lineNumber+"' name='Menu"+lineNumber+"'/>"+
						  "<input type='hidden' value='"+fieldMaxValue+"' id='MaxNumber"+lineNumber+"'/>"+"</div>"+
						  "<div id='m2"+lineNumber+"' class='xmenu' style='display: none;'>"+
						  "<div class='select-info'>"+"<label class='top-label'>已选"+fieldZhName+"：</label> <ul id='MenuSelectVal"+lineNumber+"'> </ul>"+
						  			"<a name='menu-confirm' href='javascript:void(0);' class='a-btn'> <span class='a-btn-text'>确定</span> </a> " +
						  		"</div>" +
						  		"<dl> <dt class=\"open\">选择"+fieldZhName+"</dt> <dd><ul style='width:380px;overflow-y:scroll;' id='select_Value"+lineNumber+"'> </ul></dd> </dl> " +
						 "</div></div> </div>");
			var script = $("<script type='text/javascript'>function initMenu"+ lineNumber +"(){$('#Div_"+lineNumber+"').xMenu({width :400,eventType:'click',dropmenu:'#m2"+lineNumber+"',hiddenID : 'Menu"+lineNumber+"' });if($('#m2"+lineNumber+"').height()>300){ $('#select_Value"+lineNumber+"').height('300px');}}</script>");
			xmenuNum += "initMenu"+lineNumber+",";
			selet.appendTo(dir);
			script.appendTo(dir);
			dir.appendTo(div2);
		}else if(fieldType==9){//数据字典
			var dir = $("<div id='lead' class='card'>");
			var selet = $("<div class='topnav'>" + "<a id='Div_"+lineNumber+"'  href='javascript:void(0);' class='as'>" +
								"<span id='spanValue"+lineNumber+"' title=''>请选择</span>"+"</a>"+
						  "</div>"+
					"<input type='text' value='' id= 'Menu"+lineNumber+"' name='Menu"+lineNumber+"'/>"+
					"<input type='hidden' value='"+fieldMaxValue+"' id='MaxNumber"+lineNumber+"'/>"+"</div>"+
					"<div id='m2"+lineNumber+"' class='xmenu' style='display: none;'>"+
						"<div class='select-info'>"+"<label class='top-label'>已选"+fieldZhName+"：</label> <ul> </ul>"+
							"<a name='menu-confirm' href='javascript:void(0);' class='a-btn'> <span class='a-btn-text'>确定</span> </a> " +
						"</div>" +
						"<dl> <dt class=\"open\">选择"+fieldZhName+"</dt> <dd><ul id='select_Value"+lineNumber+"'> </ul></dd> </dl> " +
					"</div>"+
					"<script type='text/javascript'>"+
					"$(document).ready(function(){"+
					"$('Div_"+lineNumber+"').xMenu({"+
					"width :600,"+
					"eventType:'click',"+
					"dropmenu:'#m2"+lineNumber+"',"+
					"hiddenID : 'MaxNumber"+lineNumber+"' });"+
					" }); "+
					"</script>");
			selet.appendTo(dir);
			dir.appendTo(div2);
		}else if(fieldType == 10){  //人员
			var compary = $("<script type=\'text/javascript\'>"+
								"function clear"+fieldEnName+"Category() {"+
									"$('#"+fieldEnName+"Name').val('');"+
									"$('#"+fieldEnName+"').val('');"+
								"}"+
								"function show"+fieldEnName+"Category() {"+
									"layer.open({"+
								    "type: 2,"+
								    "title: '"+fieldZhName+"',"+
								    "closeBtn: true,"+
								    "maxmin: true,"+
								    "area: ['1200px', '470px'],"+
								    "shift: 2,"+
								    "content:'"+_appPath+"/system/peopleUnit/peopleUnit.jsp?divWidth=1150px&fieldName="+fieldEnName+
									"&valueLength="+fieldMaxValue+"&check=2'"+  //iframe的url，no代表不显示滚动条
								"});"+
								
								
									/*"$.openWindow('"+_appPath+"/system/peopleUnit/peopleUnit.jsp?divWidth=1150px&fieldName="+fieldEnName+
									"&valueLength="+fieldMaxValue+"&check=1','"+fieldZhName+"','1200px','470px')"+*/
								"}"+
							"</script>"+
							"<div class='inputVal'>"+
										 	"<input type='text' name="+fieldEnName+"Name id="+fieldEnName+"Name style=\'width:150px; margin-left:87px;display: inline;\' readonly=\'readonly\'  class='form-control' value=''>"+
										 	"<input type='hidden' name='people_dir"+lineNumber+"' id="+fieldEnName+" value=''>"+
										 	"<a onclick='show"+fieldEnName+"Category();'href=\"###\" class='btn-primary' role='button' style='margin-left: 1%;display: inline;'><img src=\""+_appPath+"/appframe/main/images/select.png\" alt=\"选择\"></a>"+
										 	"<a onclick='clear"+fieldEnName+"Category();'href=\"###\" class='btn-primary' role='button' style='margin-left: 1%;display: inline;'><img src=\""+_appPath+"/appframe/main/images/clean.png\" alt=\"清除\"></a>"+
							  "</div>");
			compary.appendTo(div2);
		}else if(fieldType == 11){  //单位
			var people = $("<script type=\'text/javascript\'>"+
					"function clear"+fieldEnName+"Category() {"+
						"$('#"+fieldEnName+"Name').val('');"+
						"$('#"+fieldEnName+"').val('');"+
					"}"+
					"function show"+fieldEnName+"Category() {"+
							"layer.open({"+
						    "type: 2,"+
						    "title: '"+fieldZhName+"',"+
						    "closeBtn: true,"+
						    "maxmin: true,"+
						    "area: ['1200px', '470px'],"+
						    "shift: 2,"+
						    "content:'"+_appPath+"/system/peopleUnit/companyUnit.jsp?divWidth=1150px&fieldName="+fieldEnName+
						"&valueLength="+fieldMaxValue+"&check=2'"+  //iframe的url，no代表不显示滚动条
						"});"+
					
					
						/*"$.openWindow('"+_appPath+"/system/peopleUnit/companyUnit.jsp?divWidth=1150px&fieldName="+fieldEnName+
						"&valueLength="+fieldMaxValue+"&check=1','"+fieldZhName+"','1200px','470px')"+*/
					"}"+
				"</script>"+
				"<div class='inputVal'>"+
					"<div class='form-group'>"+
							 	"<input type='text' name="+fieldEnName+"Name id="+fieldEnName+"Name style=\'width:150px; margin-left:100px;display: inline;\' readonly=\'readonly\'  class='form-control' value=''>"+
							 	"<input type='hidden' name='compary_dir"+lineNumber+"' id="+fieldEnName+" value=''>"+
							 	"<a onclick='show"+fieldEnName+"Category();'href=\"###\" class='btn-primary' role='button' style='margin-left: 1%;display: inline;'><img src=\""+_appPath+"/appframe/main/images/select.png\" alt=\"选择\"></a>"+
							 	"<a onclick='clear"+fieldEnName+"Category();'href=\"###\" class='btn-primary' role='button' style='margin-left: 1%;display: inline;'><img src=\""+_appPath+"/appframe/main/images/clean.png\" alt=\"清除\"></a>"+
				  "</div>");
			people.appendTo(div2);
		}
		
	}else{
		////}else{
			var inp = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+lineNumber+"' style='width:250px; margin-left:87px;'/>"+
				"</div>");
			inp.appendTo(div2);
			$('select[name=fieldValue]').selectpicker('refresh');
		//}
	}
	div2.appendTo(div1);
	return div1;
}


//获得输入的查询条件组合(根据新的高级查询条件组件)
//fengda 2015年11月18日
function getAllQueryFieldConditions(){
	var params = "";
	//获取id为queryCondition的div下的所有的子元素，即，所有的查询条件和在查询条件中输入的值
	var conditonNum = $("#queryCondition").children();
	var contactWord = globalContactWord;//将连接符换为AND 
	for(var count=1;count<=conditonNum.length;count++){
		var i = "";
		if(count<10){
			i = "0" + count;
		}
		var operator = "";
		//获取查询字段值  例如：印次(英文名称)
		var fieldEnName = $('#queryField'+i).attr('value');
		//获取查询字段的类型，例如input  fieldType =1
		var fieldType = $('#queryField'+i).attr('fieldType');
		if( $("#contactWord" + i).length>0){
			contactWord = $("#contactWord" + i + " option:selected").val().toUpperCase(); //获得连接符
		}
		var operatorValue =  $("#operator" + i + " option:selected").val();
		var val = "";
		if(fieldType==3||fieldType==4 || fieldType == 2){
			
			val = $('#Menu'+i).val();
			
			/*$("select[id=fieldValue"+i+"] option:selected").each(function(){
				val += $(this).val()+",";
			});
			if(val.length>1){
				val = val.substring(0, val.length-1);
			}*/
		}else if(fieldType==1||fieldType==5){
			val = $("#inputValue" + i).val();
		}else if(fieldType==7){
			if(operatorValue=="betMd"){
				var start = $("#modifiedStartTime" + i).val();
				var end = $("#modifiedEndTime" + i).val();
				if(start != "" && start != '' && end != "" && end != ''){
					//资源中存储的时间均是按long类型存储的，所以查询条件要将时间转成long类型
					//由于时间控件转换成long类型比正常时间多了八小时，所以要减少八小时
					var firstDate = new Date(start);
					var secondDate = new Date(firstDate.valueOf() - 8*60*60*1000);
					start = new Date(secondDate).getTime()+"";
					
					var endDate = new Date(end);
					var secondEndDate = new Date(endDate.valueOf() - 8*60*60*1000);
					end = new Date(secondEndDate).getTime()+"";
					val = start + "," + end;
				}
				//val = $("#modifiedStartTime" + i).val() + "," + $("#modifiedEndTime" + i).val();
			}else{
				val = $("#modifiedStartTime" + i).val();
				if(val != "" && val != null && val != ''){
					var firstDate = new Date(val);
					var secondDate = new Date(firstDate.valueOf() - 8*60*60*1000);
					val = new Date(secondDate).getTime()+"";
				}
			}
		}else if(fieldType==6){
			//TODO
			var val = $("#inputValueHideCode" + i).val();
			if(val.length>0){
				val = val.substring(1, val.length-1);
			}
		//获取查询字段类型为数据字典的被选中的项
		}else if(fieldType==9){
			var checkedVals = $('select[name=multiSelEle]').multipleSelect('getSelects', 'value');
			if(checkedVals!=null){
				val = checkedVals.toString();
			}
			
		}else if(fieldType == 10){     //人员
			val = $("input[name='people_dir"+i+"']").val();
		}else if(fieldType == 11){    //单位
			val = $("input[name='compary_dir"+i+"']").val();
		}
		
		//alert("第" + i + "个查询字段为：" + fieldEnName + "操作符为：" + operatorValue + "  字段值为：" + val);
		if(val != '' && val != "" && val != null){
			var fieldVals = val.split(","); 
			var size = fieldVals.length;  
			var iCount = 1;
			$(fieldVals).each(function(index){
				var fieldval = fieldVals[index];
				if(operatorValue=="equ"){
					if(size==1){
						params += " (" + fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
					}else{
						if(iCount==size){
							params += fieldval + ")" + " " + contactWord + " ";
						}else if(iCount==1){
							params += fieldEnName + ":(" + fieldval + ",";
						}else if(iCount<size){
							params += fieldval + ",";
						}
					}
				}else if(operatorValue=="likeMd" || operatorValue=="inMd"){
					if(size==1){
						params += " (" + fieldEnName + ":*" + fieldval + "*)" + " " + contactWord + " ";
					}else{
						if(iCount==size){
							params += fieldEnName + ":*" + fieldval + "*)" + " " + contactWord + " ";
						}else if(iCount==1){
							params += " (" + fieldEnName + ":*" + fieldval + " OR ";
						}else{
							params += fieldEnName + ":*" + fieldval + " OR ";
						}
					}
				}else if(operatorValue=="gt"){
					params += " " + fieldEnName + ":{" + fieldval +" TO * }" + " " + contactWord + " ";
				}else if(operatorValue=="lt"){
					params += " " + fieldEnName + ":{ * TO " + fieldval + "}" + " " + contactWord + " ";
				}else if(operatorValue=="gte"){
					params += " " + fieldEnName + ":[" + fieldval +" TO * ]" + " " + contactWord + " ";
				}else if(operatorValue=="lte"){
					params += " " + fieldEnName + ":[ * TO " + fieldval + "]" + " " + contactWord + " ";
				}
	//			else if(operatorValue=="inMd"){
	//				if(size==1){
	//					params += " (" + fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
	//				}else{
	//					if(iCount==size){
	//						params += fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
	//					}else if(iCount==1){
	//						params += " (" + fieldEnName + ":" + fieldval + " OR ";
	//					}else{
	//						params += fieldEnName + ":" + fieldval + " OR ";
	//					}
	//				}
	//			}
				else if(operatorValue=="notIn"){
					if(size==1){
						params += " (" + fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
					}else{
						if(iCount==size){
							params += fieldEnName + ":" + fieldval + ")" + " " + contactWord + " ";
						}else if(iCount==1){
							params += " (" + fieldEnName + ":" + fieldval + " AND ";
						}else{
							params += fieldEnName + ":" + fieldval + " AND ";
						}
					}
				}else if(operatorValue=="betMd"){
					if(iCount==size){
						params += " TO " +fieldval+ "]" + " " + contactWord + " ";
					}else{
						params += " " + fieldEnName + ":[" + fieldval;
					}
				}
				iCount++;
			});
		}
	}
	if(params.trim().length>0){
		params = params.trim();
		var contact = params.substring(params.length-2, params.length);
		if(contact=="ND"){
			params = params.substring(0, params.length-3);
		}else if(contact=="OR"){
			params = params.substring(0, params.length-2);
		}
	}
	//alert("拼接后查询条件为：" + params);
	return params;
}







//资源管理二次查询组件总入口
//fengda 2015年11月20日
function creatQueryCondition(resType, flag){
	/* fengda 2015年11月4日
	 * resType 资源类型
	 * flag 标识符 主要解决是否是夸库
	 * 返回设置的查询条件，获得元数据定义字段
	 */
	var queryFields = creatFieldOpt(resType, flag);
	/*if($("#condition00")){
		$("#condition00").remove();
	}*/
	if(countTimes>0){
		if($("#queryCondition").length>0){
			$("#queryCondition").empty();
		}
		if($("#buttonsFrame").length>0){
			$("#buttonsFrame").remove();
		}
	}
	if(queryFields){
		var obj = JSON.parse(queryFields);
		//获取searchIndex页面中放查询条件的div的id
		var queryCondition = $("#queryCondition");
		//获取searchIndex页面中放查询条件的上一层的div的id
		var queryFrame = $("#queryFrame");
		countTimes++;
		$(obj).each(function(index) {
			++lineCount;
			var count = "";
			if(lineCount<10){
				count += "0" + lineCount;
			}
			var fieldType = obj[index].fieldType;    //元数据中设置的输入类型   1.单文本 2.下拉选择 3.多选 4.单选 5.文本域 6树形 7.日期 8.URL
			var fieldEnName = obj[index].fieldEnName;  //元数据的英文名
			var fieldZhName = obj[index].fieldZhName;  //元数据项的中文名
			var fieldValues = obj[index].fieldValues;  //
			var fieldMaxValue = obj[index].valueLen;   //允许选择的最多的个数
			//var field = dirCreateField(obj[index], lineCount);//生成查询字段(原来的高级查询)
			//var val = dirCreateVal(fieldType, fieldValues, lineCount);//生成字段值(原来的高级查询)
			var field = dirCreateQueryField(obj[index], lineCount);//生成查询字段   fengda 2015年11月17日去除了原来的查询字段的声明
			var opt = dirCreateOperator(fieldType, lineCount);//生成操作符
			var val = dirCreateQueryVal(fieldType, fieldValues,fieldEnName,fieldZhName, lineCount,fieldMaxValue);//fengda 2015年11月19日 生成字段值   去除了字段值：<label>在页面上的显示
			
			var div = $("<div class='row' id='condition"+count+"'>");
			field.appendTo(div);
			
			val.appendTo(div);
			opt.appendTo(div);
			div.appendTo(queryCondition);
		});  
		$('select').selectpicker('refresh');
		return "queryCondtionButton";
	}
}


//用于资源管理二次查询,获取二次查询输入的查询条件及内容
function getSecondQueryFieldConditions(){
	var params = '';
	//获取所有的查询条件框
	var conditionNum = $('#queryCondition').children();
	for(var count =1; count<conditionNum.length;count++){
		var i = '';
		if(count<10){
			i = "0"+count;
		}
		var operator = "";
		//获取查询字段值  例如：印次(英文名称)
		var fieldEnName = $('#queryField'+i).attr('value');
		//获取查询字段的类型，例如input  fieldType =1
		var fieldType = $('#queryField'+i).attr('fieldType');
		//获取选择的操作符（精确或者模糊或包含或区间）
		var operatorValue =  $("#operator" + i + " option:selected").val();
		var val = "";
		//根据不同的文本框分别获取输入的查询条件的内容
		if(fieldType==3||fieldType==4 || fieldType == 2){
			val = $('#Menu'+i).val();
		}else if(fieldType==1||fieldType==5){
			val = $("#inputValue" + i).val();
		}else if(fieldType==7){
			if(operatorValue=="betMd"){
				val = $("#modifiedStartTime" + i).val() + "," + $("#modifiedEndTime" + i).val();
			}else{
				val = $("#modifiedStartTime" + i).val();
			}
		}else if(fieldType==6){
			var val = $("#inputValueHideCode" + i).val();
			if(val.length>0){
				val = val.substring(1, val.length-1);
			}
		//获取查询字段类型为数据字典的被选中的项
		}else if(fieldType==9){
			var checkedVals = $('select[name=multiSelEle]').multipleSelect('getSelects', 'value');
			if(checkedVals!=null){
				val = checkedVals.toString();
			}
		}
		if(operatorValue == 'inMd'){
			params += "\'"+fieldEnName+"_metaField_myfd\':[\'"+val+"\',\'in\',\'string\'],";
		}else if(operatorValue == 'betMd'){
			var newDate = val.split(",");
			params += "\'"+fieldEnName+"_metaField_StartDate_myfd\':[\'"+newDate[0]+"\',\'=\',\'string\'],";
			params += "\'"+fieldEnName+"_metaField_EndDate_myfd\':[\'"+newDate[1]+"\',\'=\',\'string\'],";
		}
		
		
	}
}

