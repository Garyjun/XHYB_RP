/**
 * 此备份为高级检索功能显示正常版本 2015-06-25 16:48
 */
//设置数据库的类型
var defaultQueryModel = "solr";
var globalContactWord = "AND";

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
		var contact = createContact(lineCount);
		var delOpt = $("<a class=\"btn hover-red\" href=\"javascript:deleteQueryCondition('condition"+count+"')\"><i class=\"fa fa-trash-o\"></i>删除</a>");
		
		field.appendTo(divRow);
		opt.appendTo(divRow);
		val.appendTo(divRow);
		contact.appendTo(divRow);
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

//根据传过来的数据组装好查询条件
//TODO
function directCreateQueryCondition(resType, flag){
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
								"<button type='button' class='btn btn-default red' id='queryCondtionButton' style='margin-left:30px;'>查询</button>" +
								"&nbsp;&nbsp;&nbsp;&nbsp;" +
								"<button type='button' class='btn btn-default blue' id='clearCondtionButton' onclick='clearQueryCondition();'>清空</button>" +
//								"&nbsp;&nbsp;&nbsp;&nbsp;" +
//								"<button type='button' class='btn btn-primary' id='exportCondtionButton' >导出</button>" +
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

//获得查询字段和值
function getAllQueryConditions(){
	var params = "";
	var conditonNum = $("#queryCondition").children();
	//alert("查询条件的个数为：" + conditonNum.length);
	var contactWord = "OR";
	for(var count=1;count<=conditonNum.length;count++){
		var i = "";
		if(count<10){
			i = "0" + count;
		}
		var operator = "";
		var fieldEnName = $("select[id=queryField"+i+"] option:selected").val();
		var fieldType = $("select[id=queryField"+i+"] option:selected").attr("fieldType");
		
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
		$("select[name=multiSelEle]").multipleSelect("uncheckAll");//清空数据字典下拉框
	}
	
	var params = getAllQueryConditions();
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

function dirCreateOperator(fieldType, lineNumber){
	if(lineNumber<10){
		lineNumber = "0" + lineNumber;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 50px;'></div>");
	var div2 = $("<div class='form-group'></div>");
	var label = $("<label for='operator' class='control-label col-md-4' style='margin-top:8px;'>操作符：</label>");
	//TODO 修改onclick事件
	//var sel = $("<select class='selectpicker bla bla bli operator"+lineNumber+"' id='operator"+lineNumber+"' name='operator' onchange=\"operateDirChangeEvent('"+lineNumber+"');\" style='border:solid 1px #ffcc00;'></select>");
	var sel = $("<select class='selectpicker bla bla bli operator"+lineNumber+"' id='operator"+lineNumber+"' name='operator' onchange=\"operateDirChangeEvent('"+lineNumber+"', '"+fieldType+"');\" style='border:solid 1px #ffcc00;'></select>");
	label.appendTo(div2);
	sel.appendTo(div2);
	div2.appendTo(div1);
	var noWd = $("<option></option>");
	var equ = $("<option value='equ' id='equ"+lineNumber+"'"+">等于</option>");
	var likeMd = $("<option value='likeMd' id='likeMd"+lineNumber+"'"+">模糊</option>");
	var gt = $("<option value='gt' id='gt"+lineNumber+"'"+">大于</option>");
	var lt = $("<option value='lt' id='lt"+lineNumber+"'"+">小于</option>");
	var gte = $("<option value='gte' id='gte"+lineNumber+"'"+">大于等于</option>");
	var lte = $("<option value='lte' id='lte"+lineNumber+"'"+">小于等于</option>");
	var inMd = $("<option value='inMd' id='inMd"+lineNumber+"'"+">包含</option>");
	var notIn = $("<option value='notIn' id='notIn"+lineNumber+"'"+">不包含</option>");
	var between = $("<option value='betMd' id='betMd"+lineNumber+"'"+">区间</option>");
	noWd.appendTo(sel);
	if(fieldType==1||fieldType==5||fieldType==9){
		equ.appendTo(sel);
		likeMd.appendTo(sel);
		gt.appendTo(sel);
		lt.appendTo(sel);
		gte.appendTo(sel);
		lte.appendTo(sel);
		inMd.appendTo(sel);
		notIn.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
	}else if(fieldType==2||fieldType==3||fieldType==4||fieldType==6){
		inMd.appendTo(sel);
		notIn.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
	}else if(fieldType==7){ //日期
		equ.appendTo(sel);
		gt.appendTo(sel);
		lt.appendTo(sel);
		gte.appendTo(sel);
		lte.appendTo(sel);
		between.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
	}else if(fieldType==8){ //树形结构
		inMd.appendTo(sel);
		notIn.appendTo(sel);
		$('select[name=operator]').selectpicker('refresh');
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
	var div1 = $("<div class='col-md-2' style='margin-right: 50px;'></div>");
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
    	var optGroup = $("<optgroup label='"+groupId+"' data-subtext='' data-icon='icon-ok'></optgroup>");
    	if(map.get(groupId)==null){
    		optGroup.appendTo(sel);
    		map.put(groupId, optGroup);
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
        	var group = $("optgroup[label="+groupId+"]");
        	if(map.get(groupId)){
        		var opt;
            	if(fieldValues){
            		opt = $("<option fieldValues='"+fieldValues+"' value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}else{
            		opt = $("<option value='"+fieldEnName+"' fieldType='"+ fieldType +"'>"+fieldZhName+"</option>");
            	}
                opt.appendTo(map.get(groupId));
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
	var div1 = $("<div class='col-md-2' style='margin-right: 50px;'></div>");
	var div2 = $("<div class='form-group'></div>");
	var label = $("<label for='operator' class='control-label col-md-4' style='margin-top:8px;'>操作符：</label>");
	var sel = $("<select class='selectpicker bla bla bli operator"+counter+"' id='operator"+counter+"' name='operator' onchange=\"operateChangeEvent('"+counter+"');\" style='border:solid 1px #ffcc00;'></select>");
	label.appendTo(div2);
	sel.appendTo(div2);
	div2.appendTo(div1);
	var noWd = $("<option></option>");
	var equ = $("<option value='equ' id='equ"+counter+"'"+">等于</option>");
	var likeMd = $("<option value='likeMd' id='likeMd"+counter+"'"+">模糊</option>");
	var gt = $("<option value='gt' id='gt"+counter+"'"+">大于</option>");
	var lt = $("<option value='lt' id='lt"+counter+"'"+">小于</option>");
	var gte = $("<option value='gte' id='gte"+counter+"'"+">大于等于</option>");
	var lte = $("<option value='lte' id='lte"+counter+"'"+">小于等于</option>");
	var inMd = $("<option value='inMd' id='inMd"+counter+"'"+">包含</option>");
	var notIn = $("<option value='notIn' id='notIn"+counter+"'"+">不包含</option>");
	var between = $("<option value='betMd' id='betMd"+counter+"'"+">区间</option>");
	noWd.appendTo(sel);
	equ.appendTo(sel);
	likeMd.appendTo(sel);
	gt.appendTo(sel);
	lt.appendTo(sel);
	gte.appendTo(sel);
	lte.appendTo(sel);
	inMd.appendTo(sel);
	notIn.appendTo(sel);
	between.appendTo(sel);
	return div1;
}

//生成字段值
function createVal(counter, fieldType){
	if(counter<10){
		counter = "0" + counter;
	}
	var div1 = $("<div class='col-md-2' style='margin-right: 50px; width:350px'>");
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
	//生成连接符结束
	return div1;
}

//获得元数据定义字段
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
	var gt = $('select[id='+idVal+']').find('[value=gt]');
	var lt = $('select[id='+idVal+']').find('[value=lt]');
	var gte = $('select[id='+idVal+']').find('[value=gte]');
	var lte = $('select[id='+idVal+']').find('[value=lte]');
	var inMd = $('select[id='+idVal+']').find('[value=inMd]');
	var notIn = $('select[id='+idVal+']').find('[value=notIn]');
	var betMd = $('select[id='+idVal+']').find('[value=betMd]');
	if(fieldType==1||fieldType==5||fieldType==9){
		equ.show();
		likeMd.show();
		gt.show();
		lt.show();
		gte.show();
		lte.show();
		inMd.show();
		notIn.show();
		betMd.hide();
	}else if(fieldType==2||fieldType==3||fieldType==4||fieldType==6){
		equ.hide();
		likeMd.hide();
		gt.hide();
		lt.hide();
		gte.hide();
		lte.hide();
		inMd.show();
		notIn.show();
		betMd.hide();
	}else if(fieldType==7){ //日期
		equ.show();
		likeMd.hide();
		gt.show();
		lt.show();
		gte.show();
		lte.show();
		inMd.hide();
		notIn.hide();
		betMd.show();
	}else if(fieldType==8){ //树形结构
		equ.hide();
		likeMd.hide();
		gt.hide();
		lt.hide();
		gte.hide();
		lte.hide();
		inMd.show();
		notIn.show();
		betMd.hide();
	}
	$('select[name=operator]').selectpicker('refresh');
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
	var optEnName = $("select[id=queryField"+counter+"] option:selected").val();
	//alert("当前操作符英文名是：" + optEnName);
	if(operateValues){
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
		}else{
			if(operateValues.trim()!=""){
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
	}else{
		//1:text 文本     5:textarea 文本域      6:byte[] 图片附件     9:url  
		if(fieldType==1||fieldType==5||fieldType==6||fieldType==9){
			//alert("jinruwenbenyu");
			var inp = $("<div class='inputVal'>"+
					"<input class='form-control' type='text' id='inputValue"+counter+"' style='width:250px; margin-left:87px;'/>"+
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
	}
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