/**
 * Created by tangzk on 14-3-28.
 * 首页使用，其它页面不需要引用
 */

$G = $G || {};

(function ($, $G) {

    var loadIframeUrl = function() {
        var iframeUrl = $.jStorage.get("url")
        if(iframeUrl) {
            App.loadPage(iframeUrl);
        }
    }

    var App = $G.App = {
        id: "byMainPage",
        $iframe: $("#index_frame"),
        init: function () {
            this._init();
        },
        _init: function () {
            this.setPageSize();
        },
        setPageSize: function () {
            var me = this,
                w = $(window).width(),
                h = $(window).height();

            $("#" + me.id).width(w).height(h);

            var bh = h - $("#" + me.id + "Head").outerHeight() - $("#" + me.id + "Foot").outerHeight();
            $("#" + me.id + "Body").width(w).height(bh);
        },
        loadPage: function(url) {
            this.$iframe.attr("src", url);
            $.jStorage.set("url", url);
        }
    };

    $(function(){
        App.init();

        window.onresize = function() {
            App.setPageSize();
        }
    });
})(jQuery, $G);

var App = $G.App;
