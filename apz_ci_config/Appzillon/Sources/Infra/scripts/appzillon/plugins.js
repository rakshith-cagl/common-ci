"use strict";

function _instanceof(left, right) { if (right != null && typeof Symbol !== "undefined" && right[Symbol.hasInstance]) { return !!right[Symbol.hasInstance](left); } else { return left instanceof right; } }

function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

function _classCallCheck(instance, Constructor) { if (!_instanceof(instance, Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

/**
 * --------------------------------------------------------------------------
 * Bootstrap (v4.5.3): modal.js
 * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
 * --------------------------------------------------------------------------
 */
/////////////////////UTIL START//////////////
var transition = false;
var TRANSITION_END = 'transitionend';
var MAX_UID = 1000000;
var MILLISECONDS_MULTIPLIER = 1000; // Shoutout AngusCroll (https://goo.gl/pxwQGp)

function toType(obj) {
  return {}.toString.call(obj).match(/\s([a-zA-Z]+)/)[1].toLowerCase();
}

function getSpecialTransitionEndEvent() {
  return {
    bindType: transition.end,
    delegateType: transition.end,
    handle: function handle(event) {
      if ($(event.target).is(this)) {
        return event.handleObj.handler.apply(this, arguments); // eslint-disable-line prefer-rest-params
      }

      return undefined; // eslint-disable-line no-undefined
    }
  };
}

function transitionEndTest() {
  if (typeof window !== 'undefined' && window.QUnit) {
    return false;
  }

  return {
    end: 'transitionend'
  };
}

function transitionEndEmulator(duration) {
  var _this = this;

  var called = false;
  $(this).one(Util.TRANSITION_END, function () {
    called = true;
  });
  setTimeout(function () {
    if (!called) {
      Util.triggerTransitionEnd(_this);
    }
  }, duration);
  return this;
}

function setTransitionEndSupport() {
  transition = transitionEndTest();
  $.fn.emulateTransitionEnd = transitionEndEmulator;

  if (Util.supportsTransitionEnd()) {
    $.event.special[Util.TRANSITION_END] = getSpecialTransitionEndEvent();
  }
}

function escapeId(selector) {
  // We escape IDs in case of special selectors (selector = '#myId:something')
  // $.escapeSelector does not exist in jQuery < 3
  selector = typeof $.escapeSelector === 'function' ? $.escapeSelector(selector).substr(1) : selector.replace(/(:|\.|\[|\]|,|=|@)/g, '\\$1');
  return selector;
}

var Util = {
  TRANSITION_END: 'bsTransitionEnd',
  getUID: function getUID(prefix) {
    do {
      // eslint-disable-next-line no-bitwise
      prefix += ~~(Math.random() * MAX_UID); // "~~" acts like a faster Math.floor() here
    } while (document.getElementById(prefix));

    return prefix;
  },
  getSelectorFromElement: function getSelectorFromElement(element) {
    var selector = element.getAttribute('data-target');

    if (!selector || selector === '#') {
      selector = element.getAttribute('href') || '';
    } // If it's an ID


    if (selector.charAt(0) === '#') {
      selector = escapeId(selector);
    }

    try {
      var $selector = $(document).find(selector);
      return $selector.length > 0 ? selector : null;
    } catch (err) {
      return null;
    }
  },
  getTransitionDurationFromElement: function getTransitionDurationFromElement(element) {
    if (!element) {
      return 0;
    } // Get transition-duration of the element


    var transitionDuration = $(element).css('transition-duration');
    var transitionDelay = $(element).css('transition-delay');
    var floatTransitionDuration = parseFloat(transitionDuration);
    var floatTransitionDelay = parseFloat(transitionDelay); // Return 0 if element or transition duration is not found

    if (!floatTransitionDuration && !floatTransitionDelay) {
      return 0;
    } // If multiple durations are defined, take the first


    transitionDuration = transitionDuration.split(',')[0];
    transitionDelay = transitionDelay.split(',')[0];
    return (parseFloat(transitionDuration) + parseFloat(transitionDelay)) * MILLISECONDS_MULTIPLIER;
  },
  reflow: function reflow(element) {
    return element.offsetHeight;
  },
  triggerTransitionEnd: function triggerTransitionEnd(element) {
    $(element).trigger(transition.end);
  },
  supportsTransitionEnd: function supportsTransitionEnd() {
    return Boolean(transition);
  },
  isElement: function isElement(obj) {
    return (obj[0] || obj).nodeType;
  },
  toType: function toType(obj) {
    return {}.toString.call(obj).match(/\s([a-zA-Z]+)/)[1].toLowerCase();
  },
  typeCheckConfig: function typeCheckConfig(componentName, config, configTypes) {
    for (var property in configTypes) {
      if (Object.prototype.hasOwnProperty.call(configTypes, property)) {
        var expectedTypes = configTypes[property];
        var value = config[property];
        var valueType = value && Util.isElement(value) ? 'element' : Util.toType(value);

        if (!new RegExp(expectedTypes).test(valueType)) {
          throw new Error("".concat(componentName.toUpperCase(), ": ") + "Option \"".concat(property, "\" provided type \"").concat(valueType, "\" ") + "but expected type \"".concat(expectedTypes, "\"."));
        }
      }
    }
  }
}; /////////////////////UTILS END///////////////

/**
 * ------------------------------------------------------------------------
 * Constants
 * ------------------------------------------------------------------------
 */

var NAME = 'modal';
var VERSION = '4.5.3';
var DATA_KEY = 'bs.modal';
var EVENT_KEY = ".".concat(DATA_KEY);
var DATA_API_KEY = '.data-api';
var JQUERY_NO_CONFLICT = $.fn[NAME];
var ESCAPE_KEYCODE = 27; // KeyboardEvent.which value for Escape (Esc) key

var Default = {
  backdrop: true,
  keyboard: true,
  focus: true,
  show: true
};
var DefaultType = {
  backdrop: '(boolean|string)',
  keyboard: 'boolean',
  focus: 'boolean',
  show: 'boolean'
};
var EVENT_HIDE = "hide".concat(EVENT_KEY);
var EVENT_HIDE_PREVENTED = "hidePrevented".concat(EVENT_KEY);
var EVENT_HIDDEN = "hidden".concat(EVENT_KEY);
var EVENT_SHOW = "show".concat(EVENT_KEY);
var EVENT_SHOWN = "shown".concat(EVENT_KEY);
var EVENT_FOCUSIN = "focusin".concat(EVENT_KEY);
var EVENT_RESIZE = "resize".concat(EVENT_KEY);
var EVENT_CLICK_DISMISS = "click.dismiss".concat(EVENT_KEY);
var EVENT_KEYDOWN_DISMISS = "keydown.dismiss".concat(EVENT_KEY);
var EVENT_MOUSEUP_DISMISS = "mouseup.dismiss".concat(EVENT_KEY);
var EVENT_MOUSEDOWN_DISMISS = "mousedown.dismiss".concat(EVENT_KEY);
var EVENT_CLICK_DATA_API = "click".concat(EVENT_KEY).concat(DATA_API_KEY);
var CLASS_NAME_SCROLLABLE = 'modal-dialog-scrollable';
var CLASS_NAME_SCROLLBAR_MEASURER = 'modal-scrollbar-measure';
var CLASS_NAME_BACKDROP = 'modal-backdrop';
var CLASS_NAME_OPEN = 'modal-open';
var CLASS_NAME_FADE = 'fade';
var CLASS_NAME_SHOW = 'in';
var CLASS_NAME_STATIC = 'modal-static';
var SELECTOR_DIALOG = '.modal-dialog';
var SELECTOR_MODAL_BODY = '.modal-body';
var SELECTOR_DATA_TOGGLE = '[data-toggle="modal"]';
var SELECTOR_DATA_DISMISS = '[data-dismiss="modal"]';
var SELECTOR_FIXED_CONTENT = '.fixed-top, .fixed-bottom, .is-fixed, .sticky-top';
var SELECTOR_STICKY_CONTENT = '.sticky-top';
/**
 * ------------------------------------------------------------------------
 * Class Definition
 * ------------------------------------------------------------------------
 */

var Modal = /*#__PURE__*/function () {
  function Modal(element, config) {
    _classCallCheck(this, Modal);

    this._config = this._getConfig(config);
    this._element = element;
    this._dialog = element.querySelector(SELECTOR_DIALOG);
    this._backdrop = null;
    this._isShown = false;
    this._isBodyOverflowing = false;
    this._ignoreBackdropClick = false;
    this._isTransitioning = false;
    this._scrollbarWidth = 0;
  } // Getters


  _createClass(Modal, [{
    key: "toggle",
    // Public
    value: function toggle(relatedTarget) {
      return this._isShown ? this.hide() : this.show(relatedTarget);
    }
  }, {
    key: "show",
    value: function show(relatedTarget) {
      var _this2 = this;

      if (this._isShown || this._isTransitioning) {
        return;
      }

      if ($(this._element).hasClass(CLASS_NAME_FADE)) {
        this._isTransitioning = true;
      }

      var showEvent = $.Event(EVENT_SHOW, {
        relatedTarget: relatedTarget
      });
      $(this._element).trigger(showEvent);

      if (this._isShown || showEvent.isDefaultPrevented()) {
        return;
      }

      this._isShown = true;

      this._checkScrollbar();

      this._setScrollbar();

      this._adjustDialog();

      this._setEscapeEvent();

      this._setResizeEvent();

      $(this._element).on(EVENT_CLICK_DISMISS, SELECTOR_DATA_DISMISS, function (event) {
        return _this2.hide(event);
      });
      $(this._dialog).on(EVENT_MOUSEDOWN_DISMISS, function () {
        $(_this2._element).one(EVENT_MOUSEUP_DISMISS, function (event) {
          if ($(event.target).is(_this2._element)) {
            _this2._ignoreBackdropClick = true;
          }
        });
      });

      this._showBackdrop(function () {
        return _this2._showElement(relatedTarget);
      });
    }
  }, {
    key: "hide",
    value: function hide(event) {
      var _this3 = this;

      if (event) {
        event.preventDefault();
      } /// CallBack logic added by Darshan for giving hook to user


      var proceed = true;
      var config = this._config;

      if (apz.isFunction(config.callBack)) {
        if (!event) event = window.event || {};

        if (config.callBackObj) {
          proceed = config.callBack.call(config.callBackObj, event.target);
        } else {
          proceed = config.callBack(event.target);
        }
      }

      if (proceed != false) {
        if (!this._isShown || this._isTransitioning) {
          return;
        }

        var hideEvent = $.Event(EVENT_HIDE);
        $(this._element).trigger(hideEvent);

        if (!this._isShown || hideEvent.isDefaultPrevented()) {
          return;
        }

        this._isShown = false;

        var _transition = Util.supportsTransitionEnd() && $(this._element).hasClass(CLASS_NAME_FADE);

        if (_transition) {
          this._isTransitioning = true;
        }

        this._setEscapeEvent();

        this._setResizeEvent();

        $(document).off(EVENT_FOCUSIN);
        $(this._element).removeClass(CLASS_NAME_SHOW);
        $(this._element).off(EVENT_CLICK_DISMISS);
        $(this._dialog).off(EVENT_MOUSEDOWN_DISMISS);

        if (_transition) {
          var transitionDuration = Util.getTransitionDurationFromElement(this._element);
          $(this._element).one(Util.TRANSITION_END, function (event) {
            return _this3._hideModal(event);
          }).emulateTransitionEnd(transitionDuration);
        } else {
          this._hideModal();
        }
      }
    }
  }, {
    key: "dispose",
    value: function dispose() {
      [window, this._element, this._dialog].forEach(function (htmlElement) {
        return $(htmlElement).off(EVENT_KEY);
      });
      /**
       * `document` has 2 events `EVENT_FOCUSIN` and `EVENT_CLICK_DATA_API`
       * Do not move `document` in `htmlElements` array
       * It will remove `EVENT_CLICK_DATA_API` event that should remain
       */

      $(document).off(EVENT_FOCUSIN);
      $.removeData(this._element, DATA_KEY);
      this._config = null;
      this._element = null;
      this._dialog = null;
      this._backdrop = null;
      this._isShown = null;
      this._isBodyOverflowing = null;
      this._ignoreBackdropClick = null;
      this._isTransitioning = null;
      this._scrollbarWidth = null;
    }
  }, {
    key: "handleUpdate",
    value: function handleUpdate() {
      this._adjustDialog();
    } // Private

  }, {
    key: "_getConfig",
    value: function _getConfig(config) {
      config = _objectSpread(_objectSpread({}, Default), config);
      Util.typeCheckConfig(NAME, config, DefaultType);
      return config;
    }
  }, {
    key: "_triggerBackdropTransition",
    value: function _triggerBackdropTransition() {
      var _this4 = this;

      if (this._config.backdrop === 'static') {
        var hideEventPrevented = $.Event(EVENT_HIDE_PREVENTED);
        $(this._element).trigger(hideEventPrevented);

        if (hideEventPrevented.isDefaultPrevented()) {
          return;
        }

        var isModalOverflowing = this._element.scrollHeight > document.documentElement.clientHeight;

        if (!isModalOverflowing) {
          this._element.style.overflowY = 'hidden';
        }

        this._element.classList.add(CLASS_NAME_STATIC);

        var modalTransitionDuration = Util.getTransitionDurationFromElement(this._dialog);
        $(this._element).off(Util.TRANSITION_END);
        $(this._element).one(Util.TRANSITION_END, function () {
          _this4._element.classList.remove(CLASS_NAME_STATIC);

          if (!isModalOverflowing) {
            $(_this4._element).one(Util.TRANSITION_END, function () {
              _this4._element.style.overflowY = '';
            }).emulateTransitionEnd(_this4._element, modalTransitionDuration);
          }
        }).emulateTransitionEnd(modalTransitionDuration);

        this._element.focus();
      } else {
        this.hide();
      }
    }
  }, {
    key: "_showElement",
    value: function _showElement(relatedTarget) {
      var _this5 = this;

      var transition = Util.supportsTransitionEnd() && $(this._element).hasClass(CLASS_NAME_FADE);
      var modalBody = this._dialog ? this._dialog.querySelector(SELECTOR_MODAL_BODY) : null;

      if (!this._element.parentNode || this._element.parentNode.nodeType !== Node.ELEMENT_NODE && !$(this._element).parent().length) {
        // Don't move modal's DOM position
        // apz-Bug :: Added   '!$(this._element).parent().length'    to ensure proper work flow
        document.body.appendChild(this._element);
      }

      this._element.style.display = 'block';

      this._element.removeAttribute('aria-hidden');

      this._element.setAttribute('aria-modal', true);

      this._element.setAttribute('role', 'dialog');

      if ($(this._dialog).hasClass(CLASS_NAME_SCROLLABLE) && modalBody) {
        modalBody.scrollTop = 0;
      } else {
        this._element.scrollTop = 0;
      }

      if (transition) {
        Util.reflow(this._element);
      }

      $(this._element).addClass(CLASS_NAME_SHOW);

      if (this._config.focus) {
        this._enforceFocus();
      }

      var shownEvent = $.Event(EVENT_SHOWN, {
        relatedTarget: relatedTarget
      });

      var transitionComplete = function transitionComplete() {
        if (_this5._config.focus) {
          _this5._element.focus();
        }

        _this5._isTransitioning = false;
        $(_this5._element).trigger(shownEvent);
      };

      if (transition) {
        var transitionDuration = Util.getTransitionDurationFromElement(this._dialog);
        $(this._dialog).one(Util.TRANSITION_END, transitionComplete).emulateTransitionEnd(transitionDuration);
      } else {
        transitionComplete();
      }
    }
  }, {
    key: "_enforceFocus",
    value: function _enforceFocus() {
      var _this6 = this;

      var toggle = false;
      $(document).off(EVENT_FOCUSIN) // Guard against infinite focus loop
      .on(EVENT_FOCUSIN, function (event) {
        if (document !== event.target && _this6._element !== event.target && $(_this6._element).has(event.target).length === 0) {
          if (!toggle) {
            toggle = true;

            _this6._element.focus();
          }
        }
      });
    }
  }, {
    key: "_setEscapeEvent",
    value: function _setEscapeEvent() {
      var _this7 = this;

      if (this._isShown) {
        $(this._element).on(EVENT_KEYDOWN_DISMISS, function (event) {
          if (_this7._config.keyboard && event.which === ESCAPE_KEYCODE) {
            event.preventDefault();

            _this7.hide();
          } else if (!_this7._config.keyboard && event.which === ESCAPE_KEYCODE) {
            _this7._triggerBackdropTransition();
          }
        });
      } else if (!this._isShown) {
        $(this._element).off(EVENT_KEYDOWN_DISMISS);
      }
    }
  }, {
    key: "_setResizeEvent",
    value: function _setResizeEvent() {
      var _this8 = this;

      if (this._isShown) {
        $(window).on(EVENT_RESIZE, function (event) {
          return _this8.handleUpdate(event);
        });
      } else {
        $(window).off(EVENT_RESIZE);
      }
    }
  }, {
    key: "_hideModal",
    value: function _hideModal() {
      var _this9 = this;

      this._element.style.display = 'none';

      this._element.setAttribute('aria-hidden', true);

      this._element.removeAttribute('aria-modal');

      this._element.removeAttribute('role');

      this._isTransitioning = false;

      this._showBackdrop(function () {
        $(document.body).removeClass(CLASS_NAME_OPEN);

        _this9._resetAdjustments();

        _this9._resetScrollbar();

        $(_this9._element).trigger(EVENT_HIDDEN);
      });
    }
  }, {
    key: "_removeBackdrop",
    value: function _removeBackdrop() {
      if (this._backdrop) {
        $(this._backdrop).remove();
        this._backdrop = null;
      }
    }
  }, {
    key: "_showBackdrop",
    value: function _showBackdrop(callback) {
      var _this10 = this;

      var animate = $(this._element).hasClass(CLASS_NAME_FADE) ? CLASS_NAME_FADE : '';

      if (this._isShown && this._config.backdrop) {
        var doAnimate = Util.supportsTransitionEnd() && animate;
        this._backdrop = document.createElement('div');
        this._backdrop.className = CLASS_NAME_BACKDROP;

        if (animate) {
          this._backdrop.classList.add(animate);
        }

        $(this._backdrop).appendTo(this._config.target);
        $(this._element).on(EVENT_CLICK_DISMISS, function (event) {
          if (_this10._ignoreBackdropClick) {
            _this10._ignoreBackdropClick = false;
            return;
          }

          if (event.target !== event.currentTarget) {
            return;
          }

          if (_this10._config.backdrop === 'static' || _this10._isShown == true) {
            _this10._element.focus();
          } else {
            _this10.hide();
          }

          _this10._triggerBackdropTransition();
        });

        if (doAnimate) {
          Util.reflow(this._backdrop);
        }

        $(this._backdrop).addClass(CLASS_NAME_SHOW);

        if (!callback) {
          return;
        }

        if (!doAnimate) {
          callback();
          return;
        }

        var backdropTransitionDuration = Util.getTransitionDurationFromElement(this._backdrop);
        $(this._backdrop).one(Util.TRANSITION_END, callback).emulateTransitionEnd(backdropTransitionDuration);
      } else if (!this._isShown && this._backdrop) {
        $(this._backdrop).removeClass(CLASS_NAME_SHOW);

        var callbackRemove = function callbackRemove() {
          _this10._removeBackdrop();

          if (callback) {
            callback();
          }
        };

        if (Util.supportsTransitionEnd() && $(this._element).hasClass(CLASS_NAME_FADE)) {
          var _backdropTransitionDuration = Util.getTransitionDurationFromElement(this._backdrop);

          $(this._backdrop).one(Util.TRANSITION_END, callbackRemove).emulateTransitionEnd(_backdropTransitionDuration);
        } else {
          callbackRemove();
        }
      } else if (callback) {
        callback();
      }
    } // ----------------------------------------------------------------------
    // the following methods are used to handle overflowing modals
    // todo (fat): these should probably be refactored out of modal.js
    // ----------------------------------------------------------------------

  }, {
    key: "_adjustDialog",
    value: function _adjustDialog() {
      var isModalOverflowing = this._element.scrollHeight > document.documentElement.clientHeight;

      if (!this._isBodyOverflowing && isModalOverflowing) {
        this._element.style.paddingLeft = "".concat(this._scrollbarWidth, "px");
      }

      if (this._isBodyOverflowing && !isModalOverflowing) {
        this._element.style.paddingRight = "".concat(this._scrollbarWidth, "px");
      }
    }
  }, {
    key: "_resetAdjustments",
    value: function _resetAdjustments() {
      this._element.style.paddingLeft = '';
      this._element.style.paddingRight = '';
    }
  }, {
    key: "_checkScrollbar",
    value: function _checkScrollbar() {
      var rect = document.body.getBoundingClientRect();
      this._isBodyOverflowing = Math.round(rect.left + rect.right) < window.innerWidth;
      this._scrollbarWidth = this._getScrollbarWidth();
    }
  }, {
    key: "_setScrollbar",
    value: function _setScrollbar() {
      var _this11 = this;

      if (this._isBodyOverflowing) {
        // Note: DOMNode.style.paddingRight returns the actual value or '' if not set
        //   while $(DOMNode).css('padding-right') returns the calculated value or 0 if not set
        var fixedContent = [].slice.call(document.querySelectorAll(SELECTOR_FIXED_CONTENT));
        var stickyContent = [].slice.call(document.querySelectorAll(SELECTOR_STICKY_CONTENT)); // Adjust fixed content padding

        $(fixedContent).each(function (index, element) {
          var actualPadding = element.style.paddingRight;
          var calculatedPadding = $(element).css('padding-right');
          $(element).data('padding-right', actualPadding).css('padding-right', "".concat(parseFloat(calculatedPadding) + _this11._scrollbarWidth, "px"));
        }); // Adjust sticky content margin

        $(stickyContent).each(function (index, element) {
          var actualMargin = element.style.marginRight;
          var calculatedMargin = $(element).css('margin-right');
          $(element).data('margin-right', actualMargin).css('margin-right', "".concat(parseFloat(calculatedMargin) - _this11._scrollbarWidth, "px"));
        }); // Adjust body padding

        var actualPadding = document.body.style.paddingRight;
        var calculatedPadding = $(document.body).css('padding-right');
        $(document.body).data('padding-right', actualPadding).css('padding-right', "".concat(parseFloat(calculatedPadding) + this._scrollbarWidth, "px"));
      }

      $(document.body).addClass(CLASS_NAME_OPEN);
    }
  }, {
    key: "_resetScrollbar",
    value: function _resetScrollbar() {
      // Restore fixed content padding
      var fixedContent = [].slice.call(document.querySelectorAll(SELECTOR_FIXED_CONTENT));
      $(fixedContent).each(function (index, element) {
        var padding = $(element).data('padding-right');
        $(element).removeData('padding-right');
        element.style.paddingRight = padding ? padding : '';
      }); // Restore sticky content

      var elements = [].slice.call(document.querySelectorAll("".concat(SELECTOR_STICKY_CONTENT)));
      $(elements).each(function (index, element) {
        var margin = $(element).data('margin-right');

        if (typeof margin !== 'undefined') {
          $(element).css('margin-right', margin).removeData('margin-right');
        }
      }); // Restore body padding

      var padding = $(document.body).data('padding-right');
      $(document.body).removeData('padding-right');
      document.body.style.paddingRight = padding ? padding : '';
    }
  }, {
    key: "_getScrollbarWidth",
    value: function _getScrollbarWidth() {
      // thx d.walsh
      var scrollDiv = document.createElement('div');
      scrollDiv.className = CLASS_NAME_SCROLLBAR_MEASURER;
      document.body.appendChild(scrollDiv);
      var scrollbarWidth = scrollDiv.getBoundingClientRect().width - scrollDiv.clientWidth;
      document.body.removeChild(scrollDiv);
      return scrollbarWidth;
    } // Static

  }], [{
    key: "_jQueryInterface",
    value: function _jQueryInterface(config, relatedTarget) {
      return this.each(function () {
        var data = $(this).data(DATA_KEY);

        var _config = _objectSpread(_objectSpread(_objectSpread({}, Default), $(this).data()), _typeof(config) === 'object' && config ? config : {});

        if (!data) {
          data = new Modal(this, _config);
          $(this).data(DATA_KEY, data);
        }

        if (typeof config === 'string') {
          if (typeof data[config] === 'undefined') {
            throw new TypeError("No method named \"".concat(config, "\""));
          }

          data[config](relatedTarget);
        } else if (_config.show) {
          data.toggle(relatedTarget);
        }
      });
    }
  }, {
    key: "VERSION",
    get: function get() {
      return VERSION;
    }
  }, {
    key: "Default",
    get: function get() {
      return Default;
    }
  }]);

  return Modal;
}();
/**
 * ------------------------------------------------------------------------
 * Data Api implementation
 * ------------------------------------------------------------------------
 */


$(document).on(EVENT_CLICK_DATA_API, SELECTOR_DATA_TOGGLE, function (event) {
  var _this12 = this;

  var target;
  var selector = Util.getSelectorFromElement(this);

  if (selector) {
    target = document.querySelector(selector);
  }

  var config = $(target).data(DATA_KEY) ? 'toggle' : _objectSpread(_objectSpread({}, $(target).data()), $(this).data());

  if (this.tagName === 'A' || this.tagName === 'AREA') {
    event.preventDefault();
  }

  var $target = $(target).one(EVENT_SHOW, function (showEvent) {
    if (showEvent.isDefaultPrevented()) {
      // Only register focus restorer if modal will actually get shown
      return;
    }

    $target.one(EVENT_HIDDEN, function () {
      if ($(_this12).is(':visible')) {
        _this12.focus();
      }
    });
  });

  Modal._jQueryInterface.call($(target), config, this);
});
/**
 * ------------------------------------------------------------------------
 * jQuery
 * ------------------------------------------------------------------------
 */

$.fn[NAME] = Modal._jQueryInterface;
$.fn[NAME].Constructor = Modal;

$.fn[NAME].noConflict = function () {
  $.fn[NAME] = JQUERY_NO_CONFLICT;
  return Modal._jQueryInterface;
};

$('#myDialog').click(function (e) {
  if ($('#modalDialog').is(':visible') && $(e.target).closest('#modalDialog').length === 0) {
    $('#myDialog').hide();
  } else {
    $('#myDialog').show();
  }
});