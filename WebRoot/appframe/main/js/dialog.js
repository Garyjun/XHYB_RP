/**
 * 基于Easy UI
 * Created by tangzk on 2014/4/1.
 */
$G = $G || {};
(function($, $G) {
    $G.Dialog = {
        /**
         * 提示信息，不会阻断事件执行
         * @param msg 提示信息
         * @param fn 点击确定事件
         * @param title 窗口标题
         * @param icon 提示信息前的图标
         */
        alert: function(msg, fn, title) {
            title = title || "";
//            $.messager.alert(title, msg, icon, fn);
            art.dialog({
                lock: true,
                content: msg,
                resize: false,
                width: 300,
                ok: function () {
                    fn && fn();
                    return true;
                }
            });
        },
        /**
         * 确认信息
         * @param msg 需要确认的信息
         * @param fnOk 点击确定事件
         * @param fnCanel 点击取消事件
         * @param title 窗口标题
         */
        confirm: function(msg, fnOk, fnCanel, title) {
            title = title || "确认对话框";
//            $.messager.confirm(title, msg, function(b){
//                if(b) {
//                    fnOk && fnOk();
//                } else {
//                    fnCanel && fnCanel();
//                }
//            });
            art.dialog({
                title: title,
                lock: true,
                content: msg,
                resize: false,
                width: 300,
                ok: function () {
                    fnOk && fnOk();
                    return true;
                },
                cancel: function() {
                    fnCanel && fnCanel();
                    return true;
                }
            });
        },
        /**
         * 提示信息，不会阻断事件执行
         * @param msg 提示信息
         * @param fn 点击确定事件
         * @param title 窗口标题
         * @param icon 提示信息前的图标
         */
        progress: function(percent, fn, title, width) {
            title = title || "正在加载中...";
            percent = percent || 0;
            width = width || 300;

            var cnt = '<div class="progress progress-striped active" style="width: ' + width + 'px"><div class="progress-bar" role="progressbar" aria-valuenow="' + percent + '" aria-valuemin="0" aria-valuemax="100" style="width: ' + percent + '%"><span class="sr-only">' + percent + '%</span></div></div>';
            var pWin = art.dialog({
                title: title,
                lock: true,
                content: cnt,
                resize: false,
                width: 300,
                close: function() {
                    fn && fn();
                }
            });

            pWin.changePercent = function(intval) {
                pWin.DOM.content.find(".progress-bar").css("width", intval + "%").attr("aria-valuenow", intval);
                pWin.DOM.content.find(".sr-only").html(intval + "%");
            }
            return pWin;
        },
        /**
         * 弹出窗口
         * @param URL
         * @param titleOrOptions 类型是字符串时代表窗口的标题，是对象是代表的参数，如果是对象后面的参数将会被忽略
         * @param width
         * @param height
         * @param modal
         * @param fullScreen
         * @param resizable
         */
        openWindow: function(URL, titleOrOptions, width, height, modal, fullScreen, resizable) {
            if(URL) {
                if($.isPlainObject(titleOrOptions)) {
                    //可参考artDialog官方API进行传参
                    /**
                     * button的定义方式
                     * button: [
                     * {
                     *     name: '同意',
                     *     callback: function () {
                     *       this.content('你同意了').time(2);
                     *       return false;
                     *     },
                     *     focus: true
                     * },
                     * {
                     *     name: '不同意',
                     *     callback: function () {
                     *       alert('你不同意')
                     *     }
                     * },
                     * {
                     *     name: '无效按钮',
                     *     disabled: true
                     * },
                     * {
                     *     name: '关闭我'
                     * }
                     * ]
                     */
                    var options = {title: '窗口', resize: true, width: 640, height: 320, lock: true};
                    options = $.extend(options, titleOrOptions);
                    art.dialog.open(URL, options);
                } else {
                    var title = titleOrOptions || '窗口';
                    width = width || 640;
                    height = height || 320;
                    resizable = resizable === false ? false : true;
                    modal = modal === false ? false : true;
                    var options = {title: title, resize: resizable, width: width, height: height, lock: modal};
                    art.dialog.open(URL, options);
                }
            } else {
                this.alert('打开窗口的链接错误！');
            }
        },
        /**
         * 三个参数必须
         * @param titleOrOptions 字符串为标题，对象时使用artDialog API中的参数，并且后两个参数将忽略
         * @param content 提示内容
         * @param button 按钮
         */
        simpleDialog: function(titleOrOptions, content, button) {
            var options = {};
            if($.isPlainObject(titleOrOptions)){
                options = {resize: true, width: 300, lock: true};
                options = $.extend(options, titleOrOptions);
            } else {
                options = {title: titleOrOptions, content: content, resize: true, width: 300, lock: true, button: button};
            }
            art.dialog(options);
        }
    };
})(jQuery, $G);

var Dialog = $G.Dialog;
