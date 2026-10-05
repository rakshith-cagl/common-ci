/*!
 * jquery.tooltip.js 0.0.1 - https://github.com/yckart/jquery.tooltip.js
 * The tooltip to use, ready for mobile!
 *
 * Copyright (c) 2013 Yannick Albert (http://yckart.com)
 * Licensed under the MIT license (http://www.opensource.org/licenses/mit-license.php).
 * 2013/02/09
*/
(function ($, window, document) {
    'use strict';

    var pluginName = 'tooltip',
        defaults = {
            fade: false,
            fallback: '',
            align: 'autoTop',
            html: false,
            attr: 'title',
            trigger: {
                show: 'ontouchend' in document ? 'touchstart' : 'mouseenter',
                hide: 'ontouchend' in document ? 'touchend' : 'mouseleave'
            },
            delay: {
                show: 0,
                hide: 0
            }
        };

    function Plugin(el, options) {

        options = $.extend({}, defaults, options);

        var elem = $(el),
            timeout;

        elem.on('tooltip:show ' + options.trigger.show, function(){
               var that = this;
            if($(window).width() < 960 )
                setTimeout(function(){
                    $.data(that, 'cancel.tooltip', true);

                var tip = $.data(that, 'active.tooltip');
                if (!tip) {
                    tip = $('<div class="tooltip"><div class="tooltip-inner"/></div>').css({position:'absolute', zIndex:100000});
                    $.data(that, 'active.tooltip', tip);
                }

                if (elem.attr('title') || typeof (elem.attr('original-title')) !== 'string') {
                    elem.attr('original-title', elem.attr('title') || '').removeAttr('title');
                }

                var title;
                if (typeof options.attr === 'string') {
                    title = elem.attr(options.attr === 'title' ? 'original-title' : options.attr);
                } else if (typeof options.attr == 'function') {
                    title = options.attr.call(that);
                }

                tip.find('.tooltip-inner')[options.html ? 'html' : 'text'](title || options.fallback);

                var pos = $.extend({}, elem.offset(), {width: that.offsetWidth, height: that.offsetHeight});
                if(that.offsetWidth == undefined)
                {
                    pos.width=that.getClientRects()[0].width;
                    pos.height=that.getClientRects()[0].height;
                }
                tip[0].className = 'tooltip';
                tip.remove().css({
                    top: 0,
                    left: 0,
                    opacity: 0,
                    display:"block"
                }).appendTo(document.body);

                var actualWidth = tip[0].offsetWidth,
                    actualHeight = tip[0].offsetHeight,
                    dir = options.align === 'autoTop' ?
                          pos.top > ($(document).scrollTop() + $(window).height() / 2) ? 't' : 'b' :
                          pos.left > ($(document).scrollLeft() + $(window).width() / 2) ? 'l' : 'r';

                switch (options.align.charAt(0) === 'a' ? dir : options.align.charAt(0)) {
                    case 'b':
                        tip.css({top: pos.top + pos.height, left: pos.left + pos.width / 2 - actualWidth / 2}).addClass('tooltip-bottom');
                        break;
                    case 't':
                        tip.css({top: pos.top - actualHeight, left: pos.left + pos.width / 2 - actualWidth / 2}).addClass('tooltip-top');
                        ///Adjusting height
                        
                            var tooltip = $(".tooltip");
                            if($(".tooltip").length>1){
                                tooltip = $(".tooltip")[1]; /// Propertiess of latest tooltip
                            }
                           
                            if(tip.css('left').slice(0, -2) * $(tooltip).height() >9600){
                                var ll = pos.top -$(tooltip).height()
                                tip.css('top',ll+'px')  
                            }
                        
                        break;
                    case 'l':
                        tip.css({top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left - actualWidth}).addClass('tooltip-left');
                        break;
                    case 'r':
                        tip.css({top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left + pos.width}).addClass('tooltip-right');
                        break;
                }
                if(tip.css('left')[0]=='-')
                    tip.css('left','0px')


                clearTimeout(timeout);
                tip.stop().delay(options.delay.show).fadeTo(options.fade ? options.fade.duration : 0, options.fade.opacity || 0.8, options.fade.complete);
                },300);

            else

                $.data(this, 'cancel.tooltip', true);

                var tip = $.data(this, 'active.tooltip');
                if (!tip) {
                    tip = $('<div class="tooltip"><div class="tooltip-inner"/></div>').css({position:'absolute', zIndex:100000});
                    $.data(this, 'active.tooltip', tip);
                }

                if (elem.attr('title') || typeof (elem.attr('original-title')) !== 'string') {
                    elem.attr('original-title', elem.attr('title') || '').removeAttr('title');
                }

                var title;
                if (typeof options.attr === 'string') {
                    title = elem.attr(options.attr === 'title' ? 'original-title' : options.attr);
                } else if (typeof options.attr == 'function') {
                    title = options.attr.call(this);
                }

                tip.find('.tooltip-inner')[options.html ? 'html' : 'text'](title || options.fallback);

                var pos = $.extend({}, elem.offset(), {width: this.offsetWidth, height: this.offsetHeight});
                if(this.offsetWidth == undefined)
                {
                    pos.width=this.getClientRects()[0].width;
                    pos.height=this.getClientRects()[0].height;
                }
                tip[0].className = 'tooltip';
                tip.remove().css({
                    top: 0,
                    left: 0,
                    opacity: 0,
                    display:"block"
                }).appendTo(document.body);

                var actualWidth = tip[0].offsetWidth,
                    actualHeight = tip[0].offsetHeight,
                    dir = options.align === 'autoTop' ?
                          pos.top > ($(document).scrollTop() + $(window).height() / 2) ? 't' : 'b' :
                          pos.left > ($(document).scrollLeft() + $(window).width() / 2) ? 'l' : 'r';

                switch (options.align.charAt(0) === 'a' ? dir : options.align.charAt(0)) {
                    case 'b':
                        tip.css({top: pos.top + pos.height, left: pos.left + pos.width / 2 - actualWidth / 2}).addClass('tooltip-bottom');
                        break;
                    case 't':
                        tip.css({top: pos.top - actualHeight, left: pos.left + pos.width / 2 - actualWidth / 2}).addClass('tooltip-top');
                        ///Adjusting height
                        
                            var tooltip = $(".tooltip");
                            if($(".tooltip").length>1){
                                tooltip = $(".tooltip")[1]; /// Propertiess of latest tooltip
                            }
                           
                            if(tip.css('left').slice(0, -2) * $(tooltip).height() >9600){
                                var ll = pos.top -$(tooltip).height()
                                tip.css('top',ll+'px')  
                            }
                        
                        break;
                    case 'l':
                        tip.css({top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left - actualWidth}).addClass('tooltip-left');
                        break;
                    case 'r':
                        tip.css({top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left + pos.width}).addClass('tooltip-right');
                        break;
                }
                if(tip.css('left')[0]=='-')
                    tip.css('left','0px')


                clearTimeout(timeout);
                tip.stop().delay(options.delay.show).fadeTo(options.fade ? options.fade.duration : 0, options.fade.opacity || 0.8, options.fade.complete);
            
           
        });

        elem.on('tooltip:hide ' + options.trigger.hide, function(){
            $.data(this, 'cancel.tooltip', false);
            var self = this;
            timeout = setTimeout(function () {
                if ($.data(self, 'cancel.tooltip')) return;
                var tip = $.data(self, 'active.tooltip');
                if (options.fade) {
                    tip.stop().fadeTo(options.fade.duration, 0, function () {
                        tip.remove();
                        if(options.fade.complete) options.fade.complete(true);
                    });
                } else {
                    if($(window).width() > 960 || $('.tooltip').length>1)
                    tip.remove();
                }
            }, options.delay.hide);
        });

    }

    $.fn[pluginName] = function (options) {
        return this.each(function () {
            if (!$.data(this, "plugin_" + pluginName)) {
                $.data(this, "plugin_" + pluginName, new Plugin(this, options));
            }
        });
    };

}(jQuery, window, document));