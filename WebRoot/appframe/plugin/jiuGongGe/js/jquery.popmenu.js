/*
jQuery Pop Menu
Version: beta
Author: Guc. http://www.gucheen.pro
Based on jQuery 2.0.3
*/

(function ($) {

    $.fn.popmenu = function (options) {

        var settings = $.extend({
            'controller': true,
            'width': '300px',
            'background': '#f39921',
            'focusColor': '#d47900',
            'borderRadius': '10px',
            'top': '50',
            'left': '0',
            'iconWidth': '75px',
			'iconHeight': '60px'
        }, options);
        if (settings.controller === true) {
            var temp_display = 'none';
        } else {
            var temp_display = 'block';
        }
        var tar = $(this);
        var tar_body = tar.children('ul');
        var tar_list = tar_body.children('li');
        var tar_a = tar_list.children('a');
        var tar_ctrl = tar.children('.pop_ctrl');

        function setIt() {
            tar_body.css({
                'display': temp_display,
                'position': 'absolute',
                'margin-top': -settings.top,
                'margin-left': -settings.left,
                'background': settings.background,
                'width': settings.width,
                'float': 'left',
                'padding': '0',
                'border-radius': settings.borderRadius
            });
            tar_list.css({
                'display': 'block',
                'color': '#fff',
                'float': 'left',
                'width': settings.iconWidth,
                'height': settings.iconHeight,
                'text-align': 'center',
                'border-radius': settings.borderRadius
            });
            tar_a.css({
                'text-decoration': 'none',
                'color': '#fff'
            });
            tar_ctrl.hover(function () {
                tar_ctrl.css('cursor', 'pointer');
            }, function () {
                tar_ctrl.css('cursor', 'default')
            });
            tar_ctrl.click(function (e) {
                e.preventDefault();
                tar_body.show('fast');
                $(document).mouseup(function (e) {
                    var _con = tar_body;
                    if (!_con.is(e.target) && _con.has(e.target).length === 0) {
                        _con.hide();
                    }
                    //_con.hide(); some functions you want
                });
				$(tar_body).mouseleave(function (e) {
                    tar_body.hide('fast');
                });
            });
            tar_list.hover(function () {
                $(this).css({
                    'background': settings.focusColor,
                    'cursor': 'pointer'
                });
            }, function () {
                $(this).css({
                    'background': settings.background,
                    'cursor': 'default'
                });
            });
        }
        return setIt();

    };

}(jQuery));