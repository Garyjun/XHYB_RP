//设置数据库的类型
var defaultQueryModel = "solr";
//设置各查询字段连接符
var globalContactWord = "AND";

//定义全局变量 方便生成不重复的标签id值
var lineCount = 0;
//页面加载次数
var countTimes = 0;


var queryFieldZhName = "查询字段：";
var operatorZhName = "操作符：";
var fieldValZhName = "字段值：";

var optEnVal = '{"", "", "", ""}';
var optCnVal = '{"", "", "", ""}';

var _metaDatasUrl = "";
var _queryUrl = "";
var resType = "";
var isCrossDB = false;

;(function ( $, window, document, undefined ) {
	var AdvancedSearch = function(element, options, e) {
        if (e) {
            e.stopPropagation();
            e.preventDefault();
        }
        this.$element = $(element);
        this.$newElement = null;
        this.$button = null;
        this.$menu = null;
        this.$lis = null;

        //Merge defaults, options and data-attributes to make our options
        this.options = $.extend({}, $.fn.selectpicker.defaults, this.$element.data(), typeof options == 'object' && options);

        //If we have no title yet, check the attribute 'title' (this is missed by jq as its not a data-attribute
        if (this.options.title === null) {
            this.options.title = this.$element.attr('title');
        }

        //Expose public methods
        this.val = Selectpicker.prototype.val;
        this.render = Selectpicker.prototype.render;
        this.refresh = Selectpicker.prototype.refresh;
        this.setStyle = Selectpicker.prototype.setStyle;
        this.selectAll = Selectpicker.prototype.selectAll;
        this.deselectAll = Selectpicker.prototype.deselectAll;
        this.init();
    };
	
    $.fn.advancedSearch = function(option, event) {
        //get the args of the outer function..
        var args = arguments;
        var value;
        var chain = this.each(function() {
             if ($(this).is('select')) {
                 var $this = $(this),
                     data = $this.data('selectpicker'),
                     options = typeof option == 'object' && option;

                 if (!data) {
                     $this.data('selectpicker', (data = new Selectpicker(this, options, event)));
                 } else if (options) {
                     for(var i in options) {
                        data.options[i] = options[i];
                     }
                 }

                 if (typeof option == 'string') {
                     //Copy the value of option, as once we shift the arguments
                     //it also shifts the value of option.
                     var property = option;
                     if (data[property] instanceof Function) {
                         [].shift.apply(args);
                         value = data[property].apply(data, args);
                     } else {
                         value = data.options[property];
                     }
                 }
             }
         });

         if (value !== undefined) {
             return value;
         } else {
             return chain;
         }
     };

     $.fn.advancedSearch.defaults = {
         style: 'btn-default',
         size: 'auto',
         title: null,
         selectedTextFormat : 'values',
         noneSelectedText : '',//设置未选中值时列表框显示提示信息
         noneResultsText : 'No results match',
         countSelectedText: '{0} of {1} selected',
         isCrossDB: false,
     };
	
	
})(jQuery, window, document);
