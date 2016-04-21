String.prototype.trim=function(){
　　    return this.replace(/(^\s*)|(\s*$)/g, "");
}

String.prototype.ltrim=function(){
　　    return this.replace(/(^\s*)/g,"");
}

String.prototype.rtrim=function(){
return this.replace(/(\s*$)/g,"");
}


String.prototype.endWith=function(str){
	if(str==null||str==""||this.length==0||str.length>this.length)
	  return false;
	if(this.substring(this.length-str.length)==str)
	  return true;
	else
	  return false;
	return true;
}

String.prototype.startWith=function(str){
	if(str==null||str==""||this.length==0||str.length>this.length)
	  return false;
	if(this.substr(0,str.length)==str)
	  return true;
	else
	  return false;
	return true;
}

/**
 * 判断字符串不为空
 * @param _paramValue
 * @returns {Boolean}
 */
function isNotNull(_paramValue){
	if(typeof(_paramValue) != undefined && typeof(_paramValue) != 'undefined' && _paramValue != undefined && _paramValue != 'undefined' && _paramValue != null && _paramValue != ""){
		return true;
	}
	return false;
}


/**判断是否为数组*/
isArray = function (o) {
    return '[object Array]' == Object.prototype.toString.call(o);
};

/**判断是否为日期对象*/
isDate = function(o) {
    // return o instanceof Date;
    return {}.toString.call(o) === "[object Date]" && o.toString() !== 'Invalid Date' && !isNaN(o);
};

/**判断是否为Element对象*/
isElement = function (source) {
    return !!(source && source.nodeName && source.nodeType == 1);
};

/**判断目标参数是否为function或Function实例*/
isFunction = function (source) {
    // chrome下,'function' == typeof /a/ 为true.
    return '[object Function]' == Object.prototype.toString.call(source);
};

/**判断目标参数是否number类型或Number对象*/
isNumber = function (source) {
    return '[object Number]' == Object.prototype.toString.call(source) && isFinite(source);
};

/** 判断目标参数是否为Object对象*/
isObject = function (source) {
    return 'function' == typeof source || !!(source && 'object' == typeof source);
};

/**判断目标参数是否string类型或String对象*/
isString = function (source) {
    return '[object String]' == Object.prototype.toString.call(source);
};

/**判断目标参数是否Boolean对象*/
isBoolean = function(o) {
    return typeof o === 'boolean';
};
