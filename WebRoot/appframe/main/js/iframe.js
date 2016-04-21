/**
 * Created by tangzk on 2014/4/1.
 */

$G = $G || {};

var toggleSideBar = function(fn) {
    var $sideBar = $("#byMainPageBodySide"),
        $bc = $("#byMainPageBodyCenter"),
        $sideBarBtn = $("#sideBarToggleBtn"),
        sw = $('#byMainPageBodySide').width(),
        w = $(window).width(),
        bcw = w - sw,
        state = 'open';
    if($sideBar.offset().left < 0) {
        $sideBar.css({'overflow-y':'auto','overflow-x':'hidden'}).animate({left:0}, 333);  //.css({'overflow-y':'auto','overflow-x':'hidden'})
        $bc.animate({width: bcw, marginLeft: sw});
        $sideBarBtn.animate({left:216}, 333, "linear", function(){
            fn && fn(state);
        }).css({"background-color": "#fff", "opacity": 1});
        //设置myPlaceholderLabel的宽度
        $("#work_main").contents().find(".myPlaceholderLabel").each(function(){
        	var leftNum = $(this).css('left');
        	leftNum = leftNum.substring(0,leftNum.length-2);
        	//alert(leftNum + " ---- " + sw + " --- " + (parseInt(leftNum) - parseInt(sw)));
        	$(this).css({"left":(parseInt(leftNum) - parseInt(sw))+ 'px'});
        });
    } else {
        state = 'close';
        $sideBar.animate({left:-sw}, 333).css('overflow','visible');
        $bc.animate({width: w, marginLeft: 0});
        $sideBarBtn.animate({left:sw}, 333, "linear", function(){
            fn && fn(state);
        }).css({"background-color": "#e5e5e5", "opacity": "0.8"});
        //设置myPlaceholderLabel的宽度
        $("#work_main").contents().find(".myPlaceholderLabel").each(function(){
        	var leftNum = $(this).css('left');
        	leftNum = leftNum.substring(0,leftNum.length-2);
        	//alert(leftNum + " ---- " + sw + " --- " + (parseInt(leftNum) + parseInt(sw)));
        	$(this).css({"left":(parseInt(leftNum) + parseInt(sw))+ 'px'});
        });
    }
}

var toggleSideBarForPreview = function(fn) {
    var $sideBar = $("#byMainPageBodySide"),
        $bc = $("#byMainPageBodyCenter"),
        $sideBarBtn = $("#sideBarToggleBtn"),
        sw = $('#byMainPageBodySide').width(),
        w = $(window).width(),
        bcw = w - sw,
        state = 'open';
    if($sideBar.offset().left < 0) {
        $sideBar.css({'overflow-y':'auto','overflow-x':'hidden'}).animate({left:0}, 333);  //.css({'overflow-y':'auto','overflow-x':'hidden'})
        $bc.animate({width: bcw, marginLeft: sw});
        $sideBarBtn.animate({left:280}, 333, "linear", function(){
            fn && fn(state);
        }).css({"background-color": "#fff", "opacity": 1});
        //设置myPlaceholderLabel的宽度
        $("#work_main").contents().find(".myPlaceholderLabel").each(function(){
        	var leftNum = $(this).css('left');
        	leftNum = leftNum.substring(0,leftNum.length-2);
        	//alert(leftNum + " ---- " + sw + " --- " + (parseInt(leftNum) - parseInt(sw)));
        	$(this).css({"left":(parseInt(leftNum) - parseInt(sw))+ 'px'});
        });
    } else {
        state = 'close';
        $sideBar.animate({left:-sw}, 333).css('overflow','visible');
        $bc.animate({width: w, marginLeft: 0});
        $sideBarBtn.animate({left:sw}, 333, "linear", function(){
            fn && fn(state);
        }).css({"background-color": "#e5e5e5", "opacity": "0.8"});
        //设置myPlaceholderLabel的宽度
        $("#work_main").contents().find(".myPlaceholderLabel").each(function(){
        	var leftNum = $(this).css('left');
        	leftNum = leftNum.substring(0,leftNum.length-2);
        	//alert(leftNum + " ---- " + sw + " --- " + (parseInt(leftNum) + parseInt(sw)));
        	$(this).css({"left":(parseInt(leftNum) + parseInt(sw))+ 'px'});
        });
    }
}

// 用于判断左侧边栏是否有滚动条
var fixSideBar = function() {
    setTimeout(function(){
        reClacSideSize();
    }, 0);
    //修复tab切换时，内容长高的情况
    $('#byMainPageBodySide').find('[data-toggle="tab"]').on('shown.bs.tab', function(){
        reClacSideSize();
    });
}

var reClacSideSize = function() {
    var wh = parseInt($('#byMainPageBodySide').css('height')),
        ih = $('#sideWrap').height();
    $G.sideBarThin = false;
    if (ih && wh) {
        if(ih > wh) {
            if (!$('body').hasClass('by-overflow-fixed')) {
                $G.sideBarThin = true;
                $('body').addClass('by-overflow-fixed');
                //重新为主内容区设置宽度
                $('#byMainPageBodyCenter').width($('#byMainPageBodyCenter').width() - 20);
            }
        } else if ($('body').hasClass('by-overflow-fixed')) {
            $('body').removeClass('by-overflow-fixed');
            $('#byMainPageBodyCenter').width($('#byMainPageBodyCenter').width() + 20);
        }
    }
}

var togglePortlet = function(me) {
    $portlet = $(me).closest(".portlet");
    $portletBody = $portlet.find(".portlet-body");
    if($portletBody.is(":hidden")) {
        $(me).html("<i class=\"fa fa-angle-up\"></i>");
        $portletBody.slideDown(333);
    } else {
        $(me).html("<i class=\"fa fa-angle-down\"></i>");
        $portletBody.slideUp(333);
    }
}

var winHeight = $(window).height(),
    winWidth = $(window).width();

(function ($, $G) {
    var mainPageBodyId = "byMainPage";
    if (top.location === window.location) {
//        $.jStorage.set("url", location.href);
//        location.href = $G.config.baseUrl;
    }
    var handleSidebarMenu = function () {
        $('.page-sidebar').on('click', 'li > a', function (e) {
            if ($(this).next().hasClass('sub-menu') == false) {
                if ($('.btn-navbar').hasClass('collapsed') == false) {
                    $('.btn-navbar').click();
                }
                return;
            }

            if ($(this).next().hasClass('.sub-menu.always-open')) {
                return;
            }

            var parent = $(this).parent().parent();

            parent.children('li.open').children('a').children('.arrow').removeClass('open');
            parent.children('li.open').children('.sub-menu').slideUp(200);
            parent.children('li.open').removeClass('open');

            var sub = $(this).next();
            var slideSpeed = 200;

            if (sub.is(":visible")) {
                $('.arrow', $(this)).removeClass("open");
                $(this).parent().removeClass("open");
                sub.slideUp(slideSpeed);
            } else {
                $('.arrow', $(this)).addClass("open");
                $(this).parent().addClass("open");
                sub.slideDown(slideSpeed);
            }
            e.preventDefault();
        });
    }

    $(function(){
        fixSideBar();
        handleSidebarMenu();
        var $bodySide = $("#" + mainPageBodyId + "BodySide");
        if($bodySide.length > 0) {
            $bodySide.height(winHeight);
            var bcw = winWidth - $bodySide.outerWidth();
            $("#" + mainPageBodyId + "BodyCenter").width(bcw).height(winHeight);
        }
    });

    $.extend({
        /**
         * 提示信息，不会阻断事件执行
         * @param msg 提示信息
         * @param fn 点击确定事件
         * @param title 窗口标题
         * @param icon 提示信息前的图标
         */
        alert: function (msg, fn, title) {
            top.Dialog.alert(msg, fn, title);
        },
        /**
         * 确认信息
         * @param msg 需要确认的信息
         * @param fnOk 点击确定事件
         * @param fnCanel 点击取消事件
         * @param title 窗口标题
         */
        confirm: function (msg, fnOk, fnCanel, title) {
            top.Dialog.confirm(msg, fnOk, fnCanel, title);
        },
        /**
         * 打开窗口
         * @param url 加载的页面地址
         * @param winId ID，选填 默认为"byWindow"
         * @param title 标题，选填 默认为"窗口"
         * @param width
         * @param height
         * @param modal
         * @param fullScreen
         * @param resizable
         */
        openWindow: function (url, titleOrObj, width, height, modal, fullScreen, resizable) {
            top.Dialog.openWindow(url, titleOrObj, width, height, modal, fullScreen, resizable);
        },
        loadPage: function(url) {
            top.App.loadPage(url);
        },
        closeFromInner: function() {
            art.dialog.close();
        },
        waitPanel: function(title, fn, width) {
            return top.Dialog.progress(100, fn, title, width);
        },
        simpleDialog: function(titleOrObj, content, button) {
            return top.Dialog.simpleDialog(titleOrObj, content, button);
        }
    });
})(jQuery, $G);
