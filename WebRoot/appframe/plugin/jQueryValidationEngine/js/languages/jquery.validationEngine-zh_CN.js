(function($){
    $.fn.validationEngineLanguage = function(){
    };
    $.validationEngineLanguage = {
        newLang: function(){
            $.validationEngineLanguage.allRules = {
                "required": { // Add your regex rules here, you can take telephone as an example
                    "regex": "none",
                    "alertText": "* 此处不可空白",
                    "alertTextCheckboxMultiple": "* 请选择一个项目",
                    "alertTextCheckboxe": "* 您必须钩选此栏",
                    "alertTextDateRange": "* 日期范围不可空白"
                },
                "requiredInFunction": {
                    "func": function(field, rules, i, options){
                        return (field.val() == "test") ? true : false;
                    },
                    "alertText": "* Field must equal test"
                },
                "dateRange": {
                    "regex": "none",
                    "alertText": "* 无效的 ",
                    "alertText2": " 日期范围"
                },
                "validateDictName":{
	            	"url": _appPath+"/system/dataManagement/dataDict/validateDictName.action",
                    // error
	            	"extraDataDynamic": ['#dictNameId'],
                    "alertTextOk":"字典名称可以使用",
                    "alertText":"字典名称已存在",
	                "alertTextLoad": "字典名称正在验证，请稍后"
	            },
	            "validateDictValue":{
	            	"extraDataDynamic": ['#dictNameId'],
	            	"url": _appPath+"/target/validateDictValue.action",
                    "alertTextOk":"该分类值可以使用",
                    "alertText":"此分类已存在该值",
	                "alertTextLoad":"数据字典名称正在验证，请稍后"
	            },
	            "validateDictValueOne":{
	            	"extraDataDynamic": ['#dictNameId'],
	            	"url": _appPath+"/target/validateDictValueOne.action",
                    "alertTextOk":"该分类值可以使用",
                    "alertText":"此分类已存在该值",
	                "alertTextLoad":"数据字典名称正在验证，请稍后"
	            },
	            "validateDictNameAdd":{
	            	"url": _appPath+"/system/dataManagement/dataDict/validateDictNameAdd.action",
                    // error
                    "alertTextOk":"字典名称可以使用",
                    "alertText":"字典名称已存在",
	                "alertTextLoad": "字典名称正在验证，请稍后"
	            },
	            "validateTargetNameEdit":{
	            	"extraDataDynamic": ['#targetId'],
	            	"url": _appPath+"/target/validateTargetNameEdit.action",
                    "alertTextOk":"标签名称可以使用",
                    "alertText":"标签名称已存在",
	                "alertTextLoad":"标签名称正在验证，请稍后"
	            },
	            "validateTargetName":{
	            	"url": _appPath+"/target/validateTargetName.action",
                    "alertTextOk":"标签名称可以使用",
                    "alertText":"标签名称已存在",
	                "alertTextLoad":"标签名称正在验证，请稍后"
	            },
	            "validateDirName":{
	            	"url": _appPath+"/sysDir/validateDirName.action",
                    // error
	            	"extraDataDynamic": ['#dirId'],
                    "alertTextOk":"目录关键字可以使用",
                    "alertText":"目录关键字已存在",
	                "alertTextLoad": "目录关键字正在验证，请稍后"
	            },
	            "validatesubjectNameAdd":{
	            	"url": _appPath+"/subject/validatesubjectNameAdd.action",
                    // error
	            	"extraDataDynamic": ['#name','#id'],
                    "alertTextOk":"主题库名称可以使用",
                    "alertText":"主题库名称已存在",
	                "alertTextLoad": "主题库名称正在验证，请稍后"
	            },
	            "validateDirNameAdd":{
	            	"url": _appPath+"/sysDir/validateDirNameAdd.action",
                    // error
                    "alertTextOk":"目录关键字可以使用",
                    "alertText":"目录关键字已存在",
	                "alertTextLoad": "目录关键字正在验证，请稍后"
	            },
	            "validateParamKey":{
	            	"url": _appPath+"/sysParameter/validateParamKey.action",
                    // error
	                "extraDataDynamic": ['#paraKeyId'],
                    "alertTextOk":"参数名称可以使用",
                    "alertText":"参数名称已存在",
	                "alertTextLoad": "参数名称正在验证，请稍后"
	            },
	            "validateParamKeyAdd":{
	            	"url": _appPath+"/sysParameter/validateParamKeyAdd.action",
                    // error
                    "alertTextOk":"参数名称可以使用",
                    "alertText":"参数名称已存在",
	                "alertTextLoad": "参数名称正在验证，请稍后"
	            },
	            "isbn":{
	            	"url": _appPath+"/publishRes/checkISBN.action",
                    // error
                    "alertTextOk":"ISBN符合规范",
                    "alertText":"ISBN不符合规范",
	                "alertTextLoad": "ISBN正在验证，请稍后"
	            },
                "dateTimeRange": {
                    "regex": "none",
                    "alertText": "* 无效的 ",
                    "alertText2": " 时间范围"
                },
                "minSize": {
                    "regex": "none",
                    "alertText": "* 最少 ",
                    "alertText2": " 个字符"
                },
                "maxSize": {
                    "regex": "none",
                    "alertText": "* 最多 ",
                    "alertText2": " 个字符"
                },
				"groupRequired": {
                    "regex": "none",
                    "alertText": "* 你必需选填其中一个栏位"
                },
                "min": {
                    "regex": "none",
                    "alertText": "* 最小值為 "
                },
                "max": {
                    "regex": "none",
                    "alertText": "* 最大值为 "
                },
                "past": {
                    "regex": "none",
                    "alertText": "* 日期必需早于 "
                },
                "future": {
                    "regex": "none",
                    "alertText": "* 日期必需晚于 "
                },
                "maxCheckbox": {
                    "regex": "none",
                    "alertText": "* 最多选取 ",
                    "alertText2": " 个项目"
                },
                "minCheckbox": {
                    "regex": "none",
                    "alertText": "* 请选择 ",
                    "alertText2": " 个项目"
                },
                "equals": {
                    "regex": "none",
                    "alertText": "* 请输入与上面相同的密码"
                },
                "creditCard": {
                    "regex": "none",
                    "alertText": "* 无效的信用卡号码"
                },
                "phone": {
                    // credit: jquery.h5validate.js / orefalo
                    "regex": /^([\+][0-9]{1,3}([ \.\-])?)?([\(][0-9]{1,6}[\)])?([0-9 \.\-]{1,32})(([A-Za-z \:]{1,11})?[0-9]{1,4}?)$/,
                    "alertText": "* 无效的电话号码"
                },
                "code":{
                	"regex":/^[0-9]\d{5}(?!\d)$/,
                	"alertText":"* 邮政编码无效"
                },
                "card":{
                	"regex":/^(\d{15}$|^\d{18}$|^\d{17}(\d|X|x))$/,
                	"alertText":"无效的身份证号"
                },
                "guding":{
                	"regex": /^([0-9]{3,4}-)?[0-9]{7,8}$/,
                	"alertText":"* 电话号码无效"
                },
                "mobile": {
                    // credit: jquery.h5validate.js / orefalo
                    "regex": /^0?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/,
                    "alertText": "* 无效的手机号码"
                },
                "email": {
                    // Shamelessly lifted from Scott Gonzalez via the Bassistance Validation plugin http://projects.scottsplayground.com/email_address_validation/
                    "regex": /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i,
                    "alertText": "* 邮件地址无效"
                },
                "newemail":{
                	"regex": /^([a-zA-Z0-9_\.\-])+\@(([A-Za-z0-9\-])+\.)+([A-Za-z0-9]{2,4})+$/,
                	"alertText":"* 邮件地址无效"
                },
                "integer": {
                    "regex": /^[\-\+]?\d+$/,
                    "alertText": "* 不是有效的整数"
                },
                "number": {
                    // Number, including positive, negative, and floating decimal. credit: orefalo
                    "regex": /^[\-\+]?((([0-9]{1,3})([,][0-9]{3})*)|([0-9]+))?([\.]([0-9]+))?$/,
                    "alertText": "* 无效的数字"
                },
                "date": {
                    "regex": /^\d{4}[\/\-](0?[1-9]|1[012])[\/\-](0?[1-9]|[12][0-9]|3[01])$/,
                    "alertText": "* 无效的日期，格式必需为 YYYY-MM-DD"
                },
                "ipv4": {
                    "regex": /^((([01]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))[.]){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))$/,
                    "alertText": "* 无效的 IP 地址"
                },
                "url": {
                    "regex": /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i,
                    "alertText": "* Invalid URL"
                },
                "onlyNumberSp": {
                    "regex": /^[0-9\ ]+$/,
                    "alertText": "* 只能填数字"
                },
                "onlyLetterSp": {
                    "regex": /^[a-zA-Z\ \']+$/,
                    "alertText": "* 只接受英文字母大小写"
                },
                "onlyLetterNumber": {
                    "regex": /^[0-9a-zA-Z]+$/,
                    "alertText": "* 不接受特殊字符"
                },
                "numberOrPoint": {
                	"regex":/^(\d+\.\d{1,2}|\d+)$/,
                	"alertText": "* 只能填写数字或带两位小数"
                },
                // --- CUSTOM RULES -- Those are specific to the demos, they can be removed or changed to your likings
                "ajaxUserCall": {
                    "url": "ajaxValidateFieldUser",
                    // you may want to pass extra data on the ajax call
                    "extraData": "name=eric",
                    "alertText": "* 此名称已被其他人使用",
                    "alertTextLoad": "* 正在确认名称是否有其他人使用，请稍等。"
                },
				"ajaxUserCallPhp": {
                    "url": "phpajax/ajaxValidateFieldUser.php",
                    // you may want to pass extra data on the ajax call
                    "extraData": "name=eric",
                    // if you provide an "alertTextOk", it will show as a green prompt when the field validates
                    "alertTextOk": "* 此帐号名称可以使用",
                    "alertText": "* 此名称已被其他人使用",
                    "alertTextLoad": "* 正在确认帐号名称是否有其他人使用，请稍等。"
                },
                "ajaxNameCall": {
                    // remote json service location
                    "url": "ajaxValidateFieldName",
                    // error
                    "alertText": "* 此名称可以使用",
                    // if you provide an "alertTextOk", it will show as a green prompt when the field validates
                    "alertTextOk": "* 此名称已被其他人使用",
                    // speaks by itself
                    "alertTextLoad": "* 正在确认名称是否有其他人使用，请稍等。"
                },
				 "ajaxNameCallPhp": {
	                    // remote json service location
	                    "url": "phpajax/ajaxValidateFieldName.php",
	                    // error
	                    "alertText": "* 此名称已被其他人使用",
	                    // speaks by itself
	                    "alertTextLoad": "* 正在确认名称是否有其他人使用，请稍等。"
	                },

                "validate2fields": {
                    "alertText": "* 请输入 HELLO"
                },
	            //tls warning:homegrown not fielded
                "dateFormat":{
                    "regex": /^\d{4}[\/\-](0?[1-9]|1[012])[\/\-](0?[1-9]|[12][0-9]|3[01])$|^(?:(?:(?:0?[13578]|1[02])(\/|-)31)|(?:(?:0?[1,3-9]|1[0-2])(\/|-)(?:29|30)))(\/|-)(?:[1-9]\d\d\d|\d[1-9]\d\d|\d\d[1-9]\d|\d\d\d[1-9])$|^(?:(?:0?[1-9]|1[0-2])(\/|-)(?:0?[1-9]|1\d|2[0-8]))(\/|-)(?:[1-9]\d\d\d|\d[1-9]\d\d|\d\d[1-9]\d|\d\d\d[1-9])$|^(0?2(\/|-)29)(\/|-)(?:(?:0[48]00|[13579][26]00|[2468][048]00)|(?:\d\d)?(?:0[48]|[2468][048]|[13579][26]))$/,
                    "alertText": "* 无效的日期格式"
                },
                //tls warning:homegrown not fielded
				"dateTimeFormat": {
	                "regex": /^\d{4}[\/\-](0?[1-9]|1[012])[\/\-](0?[1-9]|[12][0-9]|3[01])\s+(1[012]|0?[1-9]){1}:(0?[1-5]|[0-6][0-9]){1}:(0?[0-6]|[0-6][0-9]){1}\s+(am|pm|AM|PM){1}$|^(?:(?:(?:0?[13578]|1[02])(\/|-)31)|(?:(?:0?[1,3-9]|1[0-2])(\/|-)(?:29|30)))(\/|-)(?:[1-9]\d\d\d|\d[1-9]\d\d|\d\d[1-9]\d|\d\d\d[1-9])$|^((1[012]|0?[1-9]){1}\/(0?[1-9]|[12][0-9]|3[01]){1}\/\d{2,4}\s+(1[012]|0?[1-9]){1}:(0?[1-5]|[0-6][0-9]){1}:(0?[0-6]|[0-6][0-9]){1}\s+(am|pm|AM|PM){1})$/,
                    "alertText": "* 无效的日期或时间格式",
                    "alertText2": "可接受的格式： ",
                    "alertText3": "mm/dd/yyyy hh:mm:ss AM|PM 或 ",
                    "alertText4": "yyyy-mm-dd hh:mm:ss AM|PM"
	            },
	            "onlyNumberOne": {
	            	"regex": /^0\.[0-9]{1,2}$/,
                    "alertText": "* 只能填0-1之间两位小数的数字"
                },
	            "indexTag": {
	            	"regex":/([A-Za-z][A-za-z0-9\W])+/,
                    "alertText":"*无效索引值"
                },
                "cNOrEng": {
                  	 "regex": /^[0-9a-zA-Z]+$/,
                      "alertText":"只能是数字和字母"
                  },
                "checkISBN": {
                	 "regex": /^[0-9a-zA-Z\-]+$/,
                    "alertText":"ISBN只能是数字、字母、和【-】"
                },
                "paraValue": {
                	"regex":/([A-Za-z][A-za-z0-9\W]|[0-9]|[A-Za-z])+/,
                    "alertText":"*无效参数值"
                },
                "validateTaskProcessName":{
	            	"url": _appPath+"/taskProcess/validateTaskProcessName.action",
                    // error
	                "extraDataDynamic": ['#taskName'],
                    "alertTextOk":"加工任务名称可以使用",
                    "alertText":"加工任务名称已存在",
	                "alertTextLoad": "加工任务名称正在验证，请稍后"
	            },
	            "isCN": {
	            	"regex":/^[0-9\u4e00-\u9faf]+$/,
                    "alertText":"*只能是汉字"
                },
                "password":{
                	"regex":/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,10}$/,
                	"alertText":"*必须包含数字和字母且长度8-10位"
                },
            };

        }
    };
    $.validationEngineLanguage.newLang();
})(jQuery);
