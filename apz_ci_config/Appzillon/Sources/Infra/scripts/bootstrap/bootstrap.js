/*!
  * Bootstrap v5.2.2 (https://getbootstrap.com/)
  * Copyright 2011-2022 The Bootstrap Authors (https://github.com/twbs/bootstrap/graphs/contributors)
  * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
  */
 (function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory(require('../appzillon/popper')) :
    (global = typeof globalThis !== 'undefined' ? globalThis : global || self, global.bootstrap = factory(global.Popper));
  })(this, (function (Popper) { 'use strict';
  
    function _interopNamespace(e) {
      if (e && e.__esModule) return e;
      const n = Object.create(null, { [Symbol.toStringTag]: { value: 'Module' } });
      if (e) {
        for (const k in e) {
          if (k !== 'default') {
            const d = Object.getOwnPropertyDescriptor(e, k);
            Object.defineProperty(n, k, d.get ? d : {
              enumerable: true,
              get: () => e[k]
            });
          }
        }
      }
      n.default = e;
      return Object.freeze(n);
    }
  
    const Popper__namespace = /*#__PURE__*/_interopNamespace(Popper);
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): util/index.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    const MAX_UID = 1000000;
    const MILLISECONDS_MULTIPLIER = 1000;
    const TRANSITION_END = 'transitionend'; // Shout-out Angus Croll (https://goo.gl/pxwQGp)
  
    const toType = object => {
      if (object === null || object === undefined) {
        return `${object}`;
      }
  
      return Object.prototype.toString.call(object).match(/\s([a-z]+)/i)[1].toLowerCase();
    };
    /**
     * Public Util API
     */
  
  
    const getUID = prefix => {
      do {
        prefix += Math.floor(Math.random() * MAX_UID);
      } while (document.getElementById(prefix));
  
      return prefix;
    };
  
    const getSelector = element => {
      let selector = element.getAttribute('data-bs-target');
  
      if (!selector || selector === '#') {
        let hrefAttribute = element.getAttribute('href'); // The only valid content that could double as a selector are IDs or classes,
        // so everything starting with `#` or `.`. If a "real" URL is used as the selector,
        // `document.querySelector` will rightfully complain it is invalid.
        // See https://github.com/twbs/bootstrap/issues/32273
  
        if (!hrefAttribute || !hrefAttribute.includes('#') && !hrefAttribute.startsWith('.')) {
          return null;
        } // Just in case some CMS puts out a full URL with the anchor appended
  
  
        if (hrefAttribute.includes('#') && !hrefAttribute.startsWith('#')) {
          hrefAttribute = `#${hrefAttribute.split('#')[1]}`;
        }
  
        selector = hrefAttribute && hrefAttribute !== '#' ? hrefAttribute.trim() : null;
      }
  
      return selector;
    };
  
    const getSelectorFromElement = element => {
      const selector = getSelector(element);
  
      if (selector) {
        return document.querySelector(selector) ? selector : null;
      }
  
      return null;
    };
  
    const getElementFromSelector = element => {
      const selector = getSelector(element);
      return selector ? document.querySelector(selector) : null;
    };
  
    const getTransitionDurationFromElement = element => {
      if (!element) {
        return 0;
      } // Get transition-duration of the element
  
  
      let {
        transitionDuration,
        transitionDelay
      } = window.getComputedStyle(element);
      const floatTransitionDuration = Number.parseFloat(transitionDuration);
      const floatTransitionDelay = Number.parseFloat(transitionDelay); // Return 0 if element or transition duration is not found
  
      if (!floatTransitionDuration && !floatTransitionDelay) {
        return 0;
      } // If multiple durations are defined, take the first
  
  
      transitionDuration = transitionDuration.split(',')[0];
      transitionDelay = transitionDelay.split(',')[0];
      return (Number.parseFloat(transitionDuration) + Number.parseFloat(transitionDelay)) * MILLISECONDS_MULTIPLIER;
    };
  
    const triggerTransitionEnd = element => {
      element.dispatchEvent(new Event(TRANSITION_END));
    };
  
    const isElement = object => {
      if (!object || typeof object !== 'object') {
        return false;
      }
  
      if (typeof object.jquery !== 'undefined') {
        object = object[0];
      }
  
      return typeof object.nodeType !== 'undefined';
    };
  
    const getElement = object => {
      // it's a jQuery object or a node element
      if (isElement(object)) {
        return object.jquery ? object[0] : object;
      }
  
      if (typeof object === 'string' && object.length > 0) {
        return document.querySelector(object);
      }
  
      return null;
    };
  
    const isVisible = element => {
      if (!isElement(element) || element.getClientRects().length === 0) {
        return false;
      }
  
      const elementIsVisible = getComputedStyle(element).getPropertyValue('visibility') === 'visible'; // Handle `details` element as its content may falsie appear visible when it is closed
  
      const closedDetails = element.closest('details:not([open])');
  
      if (!closedDetails) {
        return elementIsVisible;
      }
  
      if (closedDetails !== element) {
        const summary = element.closest('summary');
  
        if (summary && summary.parentNode !== closedDetails) {
          return false;
        }
  
        if (summary === null) {
          return false;
        }
      }
  
      return elementIsVisible;
    };
  
    const isDisabled = element => {
      if (!element || element.nodeType !== Node.ELEMENT_NODE) {
        return true;
      }
  
      if (element.classList.contains('disabled')) {
        return true;
      }
  
      if (typeof element.disabled !== 'undefined') {
        return element.disabled;
      }
  
      return element.hasAttribute('disabled') && element.getAttribute('disabled') !== 'false';
    };
  
    const findShadowRoot = element => {
      if (!document.documentElement.attachShadow) {
        return null;
      } // Can find the shadow root otherwise it'll return the document
  
  
      if (typeof element.getRootNode === 'function') {
        const root = element.getRootNode();
        return root instanceof ShadowRoot ? root : null;
      }
  
      if (element instanceof ShadowRoot) {
        return element;
      } // when we don't find a shadow root
  
  
      if (!element.parentNode) {
        return null;
      }
  
      return findShadowRoot(element.parentNode);
    };
  
    const noop = () => {};
    /**
     * Trick to restart an element's animation
     *
     * @param {HTMLElement} element
     * @return void
     *
     * @see https://www.charistheo.io/blog/2021/02/restart-a-css-animation-with-javascript/#restarting-a-css-animation
     */
  
  
    const reflow = element => {
      element.offsetHeight; // eslint-disable-line no-unused-expressions
    };
  
    const getjQuery = () => {
      if (window.jQuery && !document.body.hasAttribute('data-bs-no-jquery')) {
        return window.jQuery;
      }
  
      return null;
    };
  
    const DOMContentLoadedCallbacks = [];
  
    const onDOMContentLoaded = callback => {
      if (document.readyState === 'loading') {
        // add listener on the first call when the document is in loading state
        if (!DOMContentLoadedCallbacks.length) {
          document.addEventListener('DOMContentLoaded', () => {
            for (const callback of DOMContentLoadedCallbacks) {
              callback();
            }
          });
        }
  
        DOMContentLoadedCallbacks.push(callback);
      } else {
        callback();
      }
    };
  
    const isRTL = () => document.documentElement.dir === 'rtl';
  
    const defineJQueryPlugin = plugin => {
      onDOMContentLoaded(() => {
        const $ = getjQuery();
        /* istanbul ignore if */
  
        if ($) {
          const name = plugin.NAME;
          const JQUERY_NO_CONFLICT = $.fn[name];
          $.fn[name] = plugin.jQueryInterface;
          $.fn[name].Constructor = plugin;
  
          $.fn[name].noConflict = () => {
            $.fn[name] = JQUERY_NO_CONFLICT;
            return plugin.jQueryInterface;
          };
        }
      });
    };
  
    const execute = callback => {
      if (typeof callback === 'function') {
        callback();
      }
    };
  
    const executeAfterTransition = (callback, transitionElement, waitForTransition = true) => {
      if (!waitForTransition) {
        execute(callback);
        return;
      }
  
      const durationPadding = 5;
      const emulatedDuration = getTransitionDurationFromElement(transitionElement) + durationPadding;
      let called = false;
  
      const handler = ({
        target
      }) => {
        if (target !== transitionElement) {
          return;
        }
  
        called = true;
        transitionElement.removeEventListener(TRANSITION_END, handler);
        execute(callback);
      };
  
      transitionElement.addEventListener(TRANSITION_END, handler);
      setTimeout(() => {
        if (!called) {
          triggerTransitionEnd(transitionElement);
        }
      }, emulatedDuration);
    };
    /**
     * Return the previous/next element of a list.
     *
     * @param {array} list    The list of elements
     * @param activeElement   The active element
     * @param shouldGetNext   Choose to get next or previous element
     * @param isCycleAllowed
     * @return {Element|elem} The proper element
     */
  
  
    const getNextActiveElement = (list, activeElement, shouldGetNext, isCycleAllowed) => {
      const listLength = list.length;
      let index = list.indexOf(activeElement); // if the element does not exist in the list return an element
      // depending on the direction and if cycle is allowed
  
      if (index === -1) {
        return !shouldGetNext && isCycleAllowed ? list[listLength - 1] : list[0];
      }
  
      index += shouldGetNext ? 1 : -1;
  
      if (isCycleAllowed) {
        index = (index + listLength) % listLength;
      }
  
      return list[Math.max(0, Math.min(index, listLength - 1))];
    };
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): dom/event-handler.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    /**
     * Constants
     */
  
    const namespaceRegex = /[^.]*(?=\..*)\.|.*/;
    const stripNameRegex = /\..*/;
    const stripUidRegex = /::\d+$/;
    const eventRegistry = {}; // Events storage
  
    let uidEvent = 1;
    const customEvents = {
      mouseenter: 'mouseover',
      mouseleave: 'mouseout'
    };
    const nativeEvents = new Set(['click', 'dblclick', 'mouseup', 'mousedown', 'contextmenu', 'mousewheel', 'DOMMouseScroll', 'mouseover', 'mouseout', 'mousemove', 'selectstart', 'selectend', 'keydown', 'keypress', 'keyup', 'orientationchange', 'touchstart', 'touchmove', 'touchend', 'touchcancel', 'pointerdown', 'pointermove', 'pointerup', 'pointerleave', 'pointercancel', 'gesturestart', 'gesturechange', 'gestureend', 'focus', 'blur', 'change', 'reset', 'select', 'submit', 'focusin', 'focusout', 'load', 'unload', 'beforeunload', 'resize', 'move', 'DOMContentLoaded', 'readystatechange', 'error', 'abort', 'scroll']);
    /**
     * Private methods
     */
  
    function makeEventUid(element, uid) {
      return uid && `${uid}::${uidEvent++}` || element.uidEvent || uidEvent++;
    }
  
    function getElementEvents(element) {
      const uid = makeEventUid(element);
      element.uidEvent = uid;
      eventRegistry[uid] = eventRegistry[uid] || {};
      return eventRegistry[uid];
    }
  
    function bootstrapHandler(element, fn) {
      return function handler(event) {
        hydrateObj(event, {
          delegateTarget: element
        });
  
        if (handler.oneOff) {
          EventHandler.off(element, event.type, fn);
        }
  
        return fn.apply(element, [event]);
      };
    }
  
    function bootstrapDelegationHandler(element, selector, fn) {
      return function handler(event) {
        const domElements = element.querySelectorAll(selector);
  
        for (let {
          target
        } = event; target && target !== this; target = target.parentNode) {
          for (const domElement of domElements) {
            if (domElement !== target) {
              continue;
            }
  
            hydrateObj(event, {
              delegateTarget: target
            });
  
            if (handler.oneOff) {
              EventHandler.off(element, event.type, selector, fn);
            }
  
            return fn.apply(target, [event]);
          }
        }
      };
    }
  
    function findHandler(events, callable, delegationSelector = null) {
      return Object.values(events).find(event => event.callable === callable && event.delegationSelector === delegationSelector);
    }
  
    function normalizeParameters(originalTypeEvent, handler, delegationFunction) {
      const isDelegated = typeof handler === 'string'; // todo: tooltip passes `false` instead of selector, so we need to check
  
      const callable = isDelegated ? delegationFunction : handler || delegationFunction;
      let typeEvent = getTypeEvent(originalTypeEvent);
  
      if (!nativeEvents.has(typeEvent)) {
        typeEvent = originalTypeEvent;
      }
  
      return [isDelegated, callable, typeEvent];
    }
  
    function addHandler(element, originalTypeEvent, handler, delegationFunction, oneOff) {
      if (typeof originalTypeEvent !== 'string' || !element) {
        return;
      }
  
      let [isDelegated, callable, typeEvent] = normalizeParameters(originalTypeEvent, handler, delegationFunction); // in case of mouseenter or mouseleave wrap the handler within a function that checks for its DOM position
      // this prevents the handler from being dispatched the same way as mouseover or mouseout does
  
      if (originalTypeEvent in customEvents) {
        const wrapFunction = fn => {
          return function (event) {
            if (!event.relatedTarget || event.relatedTarget !== event.delegateTarget && !event.delegateTarget.contains(event.relatedTarget)) {
              return fn.call(this, event);
            }
          };
        };
  
        callable = wrapFunction(callable);
      }
  
      const events = getElementEvents(element);
      const handlers = events[typeEvent] || (events[typeEvent] = {});
      const previousFunction = findHandler(handlers, callable, isDelegated ? handler : null);
  
      if (previousFunction) {
        previousFunction.oneOff = previousFunction.oneOff && oneOff;
        return;
      }
  
      const uid = makeEventUid(callable, originalTypeEvent.replace(namespaceRegex, ''));
      const fn = isDelegated ? bootstrapDelegationHandler(element, handler, callable) : bootstrapHandler(element, callable);
      fn.delegationSelector = isDelegated ? handler : null;
      fn.callable = callable;
      fn.oneOff = oneOff;
      fn.uidEvent = uid;
      handlers[uid] = fn;
      element.addEventListener(typeEvent, fn, isDelegated);
    }
  
    function removeHandler(element, events, typeEvent, handler, delegationSelector) {
      const fn = findHandler(events[typeEvent], handler, delegationSelector);
  
      if (!fn) {
        return;
      }
  
      element.removeEventListener(typeEvent, fn, Boolean(delegationSelector));
      delete events[typeEvent][fn.uidEvent];
    }
  
    function removeNamespacedHandlers(element, events, typeEvent, namespace) {
      const storeElementEvent = events[typeEvent] || {};
  
      for (const handlerKey of Object.keys(storeElementEvent)) {
        if (handlerKey.includes(namespace)) {
          const event = storeElementEvent[handlerKey];
          removeHandler(element, events, typeEvent, event.callable, event.delegationSelector);
        }
      }
    }
  
    function getTypeEvent(event) {
      // allow to get the native events from namespaced events ('click.bs.button' --> 'click')
      event = event.replace(stripNameRegex, '');
      return customEvents[event] || event;
    }
  
    const EventHandler = {
      on(element, event, handler, delegationFunction) {
        addHandler(element, event, handler, delegationFunction, false);
      },
  
      one(element, event, handler, delegationFunction) {
        addHandler(element, event, handler, delegationFunction, true);
      },
  
      off(element, originalTypeEvent, handler, delegationFunction) {
        if (typeof originalTypeEvent !== 'string' || !element) {
          return;
        }
  
        const [isDelegated, callable, typeEvent] = normalizeParameters(originalTypeEvent, handler, delegationFunction);
        const inNamespace = typeEvent !== originalTypeEvent;
        const events = getElementEvents(element);
        const storeElementEvent = events[typeEvent] || {};
        const isNamespace = originalTypeEvent.startsWith('.');
  
        if (typeof callable !== 'undefined') {
          // Simplest case: handler is passed, remove that listener ONLY.
          if (!Object.keys(storeElementEvent).length) {
            return;
          }
  
          removeHandler(element, events, typeEvent, callable, isDelegated ? handler : null);
          return;
        }
  
        if (isNamespace) {
          for (const elementEvent of Object.keys(events)) {
            removeNamespacedHandlers(element, events, elementEvent, originalTypeEvent.slice(1));
          }
        }
  
        for (const keyHandlers of Object.keys(storeElementEvent)) {
          const handlerKey = keyHandlers.replace(stripUidRegex, '');
  
          if (!inNamespace || originalTypeEvent.includes(handlerKey)) {
            const event = storeElementEvent[keyHandlers];
            removeHandler(element, events, typeEvent, event.callable, event.delegationSelector);
          }
        }
      },
  
      trigger(element, event, args) {
        if (typeof event !== 'string' || !element) {
          return null;
        }
  
        const $ = getjQuery();
        const typeEvent = getTypeEvent(event);
        const inNamespace = event !== typeEvent;
        let jQueryEvent = null;
        let bubbles = true;
        let nativeDispatch = true;
        let defaultPrevented = false;
  
        if (inNamespace && $) {
          jQueryEvent = $.Event(event, args);
          $(element).trigger(jQueryEvent);
          bubbles = !jQueryEvent.isPropagationStopped();
          nativeDispatch = !jQueryEvent.isImmediatePropagationStopped();
          defaultPrevented = jQueryEvent.isDefaultPrevented();
        }
  
        let evt = new Event(event, {
          bubbles,
          cancelable: true
        });
        evt = hydrateObj(evt, args);
  
        if (defaultPrevented) {
          evt.preventDefault();
        }
  
        if (nativeDispatch) {
          element.dispatchEvent(evt);
        }
  
        if (evt.defaultPrevented && jQueryEvent) {
          jQueryEvent.preventDefault();
        }
  
        return evt;
      }
  
    };
  
    function hydrateObj(obj, meta) {
      for (const [key, value] of Object.entries(meta || {})) {
        try {
          obj[key] = value;
        } catch (_unused) {
          Object.defineProperty(obj, key, {
            configurable: true,
  
            get() {
              return value;
            }
  
          });
        }
      }
  
      return obj;
    }
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): dom/data.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
  
    /**
     * Constants
     */
    const elementMap = new Map();
    const Data = {
      set(element, key, instance) {
        if (!elementMap.has(element)) {
          elementMap.set(element, new Map());
        }
  
        const instanceMap = elementMap.get(element); // make it clear we only want one instance per element
        // can be removed later when multiple key/instances are fine to be used
  
        if (!instanceMap.has(key) && instanceMap.size !== 0) {
          // eslint-disable-next-line no-console
          console.error(`Bootstrap doesn't allow more than one instance per element. Bound instance: ${Array.from(instanceMap.keys())[0]}.`);
          return;
        }
  
        instanceMap.set(key, instance);
      },
  
      get(element, key) {
        if (elementMap.has(element)) {
          return elementMap.get(element).get(key) || null;
        }
  
        return null;
      },
  
      remove(element, key) {
        if (!elementMap.has(element)) {
          return;
        }
  
        const instanceMap = elementMap.get(element);
        instanceMap.delete(key); // free up element references if there are no instances left for an element
  
        if (instanceMap.size === 0) {
          elementMap.delete(element);
        }
      }
  
    };
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): dom/manipulator.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    function normalizeData(value) {
      if (value === 'true') {
        return true;
      }
  
      if (value === 'false') {
        return false;
      }
  
      if (value === Number(value).toString()) {
        return Number(value);
      }
  
      if (value === '' || value === 'null') {
        return null;
      }
  
      if (typeof value !== 'string') {
        return value;
      }
  
      try {
        return JSON.parse(decodeURIComponent(value));
      } catch (_unused) {
        return value;
      }
    }
  
    function normalizeDataKey(key) {
      return key.replace(/[A-Z]/g, chr => `-${chr.toLowerCase()}`);
    }
  
    const Manipulator = {
      setDataAttribute(element, key, value) {
        element.setAttribute(`data-bs-${normalizeDataKey(key)}`, value);
      },
  
      removeDataAttribute(element, key) {
        element.removeAttribute(`data-bs-${normalizeDataKey(key)}`);
      },
  
      getDataAttributes(element) {
        if (!element) {
          return {};
        }
  
        const attributes = {};
        const bsKeys = Object.keys(element.dataset).filter(key => key.startsWith('bs') && !key.startsWith('bsConfig'));
  
        for (const key of bsKeys) {
          let pureKey = key.replace(/^bs/, '');
          pureKey = pureKey.charAt(0).toLowerCase() + pureKey.slice(1, pureKey.length);
          attributes[pureKey] = normalizeData(element.dataset[key]);
        }
  
        return attributes;
      },
  
      getDataAttribute(element, key) {
        return normalizeData(element.getAttribute(`data-bs-${normalizeDataKey(key)}`));
      }
  
    };
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): util/config.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    /**
     * Class definition
     */
  
    class Config {
      // Getters
      static get Default() {
        return {};
      }
  
      static get DefaultType() {
        return {};
      }
  
      static get NAME() {
        throw new Error('You have to implement the static method "NAME", for each component!');
      }
  
      _getConfig(config) {
        config = this._mergeConfigObj(config);
        config = this._configAfterMerge(config);
  
        this._typeCheckConfig(config);
  
        return config;
      }
  
      _configAfterMerge(config) {
        return config;
      }
  
      _mergeConfigObj(config, element) {
        const jsonConfig = isElement(element) ? Manipulator.getDataAttribute(element, 'config') : {}; // try to parse
  
        return { ...this.constructor.Default,
          ...(typeof jsonConfig === 'object' ? jsonConfig : {}),
          ...(isElement(element) ? Manipulator.getDataAttributes(element) : {}),
          ...(typeof config === 'object' ? config : {})
        };
      }
  
      _typeCheckConfig(config, configTypes = this.constructor.DefaultType) {
        for (const property of Object.keys(configTypes)) {
          const expectedTypes = configTypes[property];
          const value = config[property];
          const valueType = isElement(value) ? 'element' : toType(value);
  
          if (!new RegExp(expectedTypes).test(valueType)) {
            throw new TypeError(`${this.constructor.NAME.toUpperCase()}: Option "${property}" provided type "${valueType}" but expected type "${expectedTypes}".`);
          }
        }
      }
  
    }
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): base-component.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    /**
     * Constants
     */
  
    const VERSION = '5.2.2';
    /**
     * Class definition
     */
  
    class BaseComponent extends Config {
      constructor(element, config) {
        super();
        element = getElement(element);
  
        if (!element) {
          return;
        }
  
        this._element = element;
        this._config = this._getConfig(config);
        Data.set(this._element, this.constructor.DATA_KEY, this);
      } // Public
  
  
      dispose() {
        Data.remove(this._element, this.constructor.DATA_KEY);
        EventHandler.off(this._element, this.constructor.EVENT_KEY);
  
        for (const propertyName of Object.getOwnPropertyNames(this)) {
          this[propertyName] = null;
        }
      }
  
      _queueCallback(callback, element, isAnimated = true) {
        executeAfterTransition(callback, element, isAnimated);
      }
  
      _getConfig(config) {
        config = this._mergeConfigObj(config, this._element);
        config = this._configAfterMerge(config);
  
        this._typeCheckConfig(config);
  
        return config;
      } // Static
  
  
      static getInstance(element) {
        return Data.get(getElement(element), this.DATA_KEY);
      }
  
      static getOrCreateInstance(element, config = {}) {
        return this.getInstance(element) || new this(element, typeof config === 'object' ? config : null);
      }
  
      static get VERSION() {
        return VERSION;
      }
  
      static get DATA_KEY() {
        return `bs.${this.NAME}`;
      }
  
      static get EVENT_KEY() {
        return `.${this.DATA_KEY}`;
      }
  
      static eventName(name) {
        return `${name}${this.EVENT_KEY}`;
      }
  
    }
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): util/component-functions.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
  
    const enableDismissTrigger = (component, method = 'hide') => {
      const clickEvent = `click.dismiss${component.EVENT_KEY}`;
      const name = component.NAME;
      EventHandler.on(document, clickEvent, `[data-bs-dismiss="${name}"]`, function (event) {
        if (['A', 'AREA'].includes(this.tagName)) {
          event.preventDefault();
        }
  
        if (isDisabled(this)) {
          return;
        }
  
        const target = getElementFromSelector(this) || this.closest(`.${name}`);
        const instance = component.getOrCreateInstance(target); // Method argument is left, for Alert and only, as it doesn't implement the 'hide' method
  
        instance[method]();
      });
    };
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): dom/selector-engine.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    /**
     * Constants
     */
  
    const SelectorEngine = {
      find(selector, element = document.documentElement) {
        return [].concat(...Element.prototype.querySelectorAll.call(element, selector));
      },
  
      findOne(selector, element = document.documentElement) {
        return Element.prototype.querySelector.call(element, selector);
      },
  
      children(element, selector) {
        return [].concat(...element.children).filter(child => child.matches(selector));
      },
  
      parents(element, selector) {
        const parents = [];
        let ancestor = element.parentNode.closest(selector);
  
        while (ancestor) {
          parents.push(ancestor);
          ancestor = ancestor.parentNode.closest(selector);
        }
  
        return parents;
      },
  
      prev(element, selector) {
        let previous = element.previousElementSibling;
  
        while (previous) {
          if (previous.matches(selector)) {
            return [previous];
          }
  
          previous = previous.previousElementSibling;
        }
  
        return [];
      },
  
      // TODO: this is now unused; remove later along with prev()
      next(element, selector) {
        let next = element.nextElementSibling;
  
        while (next) {
          if (next.matches(selector)) {
            return [next];
          }
  
          next = next.nextElementSibling;
        }
  
        return [];
      },
  
      focusableChildren(element) {
        const focusables = ['a', 'button', 'input', 'textarea', 'select', 'details', '[tabindex]', '[contenteditable="true"]'].map(selector => `${selector}:not([tabindex^="-"])`).join(',');
        return this.find(focusables, element).filter(el => !isDisabled(el) && isVisible(el));
      }
  
    };
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): util/scrollBar.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    /**
     * Constants
     */
  
    const SELECTOR_FIXED_CONTENT = '.fixed-top, .fixed-bottom, .is-fixed, .sticky-top';
    const SELECTOR_STICKY_CONTENT = '.sticky-top';
    const PROPERTY_PADDING = 'padding-right';
    const PROPERTY_MARGIN = 'margin-right';
    /**
     * Class definition
     */
  
    class ScrollBarHelper {
      constructor() {
        this._element = document.body;
      } // Public
  
  
      getWidth() {
        // https://developer.mozilla.org/en-US/docs/Web/API/Window/innerWidth#usage_notes
        const documentWidth = document.documentElement.clientWidth;
        return Math.abs(window.innerWidth - documentWidth);
      }
  
      hide() {
        const width = this.getWidth();
  
        this._disableOverFlow(); // give padding to element to balance the hidden scrollbar width
  
  
        this._setElementAttributes(this._element, PROPERTY_PADDING, calculatedValue => calculatedValue + width); // trick: We adjust positive paddingRight and negative marginRight to sticky-top elements to keep showing fullwidth
  
  
        this._setElementAttributes(SELECTOR_FIXED_CONTENT, PROPERTY_PADDING, calculatedValue => calculatedValue + width);
  
        this._setElementAttributes(SELECTOR_STICKY_CONTENT, PROPERTY_MARGIN, calculatedValue => calculatedValue - width);
      }
  
      reset() {
        this._resetElementAttributes(this._element, 'overflow');
  
        this._resetElementAttributes(this._element, PROPERTY_PADDING);
  
        this._resetElementAttributes(SELECTOR_FIXED_CONTENT, PROPERTY_PADDING);
  
        this._resetElementAttributes(SELECTOR_STICKY_CONTENT, PROPERTY_MARGIN);
      }
  
      isOverflowing() {
        return this.getWidth() > 0;
      } // Private
  
  
      _disableOverFlow() {
        this._saveInitialAttribute(this._element, 'overflow');
  
        this._element.style.overflow = 'hidden';
      }
  
      _setElementAttributes(selector, styleProperty, callback) {
        const scrollbarWidth = this.getWidth();
  
        const manipulationCallBack = element => {
          if (element !== this._element && window.innerWidth > element.clientWidth + scrollbarWidth) {
            return;
          }
  
          this._saveInitialAttribute(element, styleProperty);
  
          const calculatedValue = window.getComputedStyle(element).getPropertyValue(styleProperty);
          element.style.setProperty(styleProperty, `${callback(Number.parseFloat(calculatedValue))}px`);
        };
  
        this._applyManipulationCallback(selector, manipulationCallBack);
      }
  
      _saveInitialAttribute(element, styleProperty) {
        const actualValue = element.style.getPropertyValue(styleProperty);
  
        if (actualValue) {
          Manipulator.setDataAttribute(element, styleProperty, actualValue);
        }
      }
  
      _resetElementAttributes(selector, styleProperty) {
        const manipulationCallBack = element => {
          const value = Manipulator.getDataAttribute(element, styleProperty); // We only want to remove the property if the value is `null`; the value can also be zero
  
          if (value === null) {
            element.style.removeProperty(styleProperty);
            return;
          }
  
          Manipulator.removeDataAttribute(element, styleProperty);
          element.style.setProperty(styleProperty, value);
        };
  
        this._applyManipulationCallback(selector, manipulationCallBack);
      }
  
      _applyManipulationCallback(selector, callBack) {
        if (isElement(selector)) {
          callBack(selector);
          return;
        }
  
        for (const sel of SelectorEngine.find(selector, this._element)) {
          callBack(sel);
        }
      }
  
    }
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): util/backdrop.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    /**
     * Constants
     */
  
    const NAME$9 = 'backdrop';
    const CLASS_NAME_FADE$4 = 'fade';
    const CLASS_NAME_SHOW$5 = 'show';
    const EVENT_MOUSEDOWN = `mousedown.bs.${NAME$9}`;
    const Default$8 = {
      className: 'modal-backdrop',
      clickCallback: null,
      isAnimated: false,
      isVisible: true,
      // if false, we use the backdrop helper without adding any element to the dom
      rootElement: 'body' // give the choice to place backdrop under different elements
  
    };
    const DefaultType$8 = {
      className: 'string',
      clickCallback: '(function|null)',
      isAnimated: 'boolean',
      isVisible: 'boolean',
      rootElement: '(element|string)'
    };
    /**
     * Class definition
     */
  
    class Backdrop extends Config {
      constructor(config) {
        super();
        this._config = this._getConfig(config);
        this._isAppended = false;
        this._element = null;
      } // Getters
  
  
      static get Default() {
        return Default$8;
      }
  
      static get DefaultType() {
        return DefaultType$8;
      }
  
      static get NAME() {
        return NAME$9;
      } // Public
  
  
      show(callback) {
        if (!this._config.isVisible) {
          execute(callback);
          return;
        }
  
        this._append();
  
        const element = this._getElement();
  
        if (this._config.isAnimated) {
          reflow(element);
        }
  
        element.classList.add(CLASS_NAME_SHOW$5);
  
        this._emulateAnimation(() => {
          execute(callback);
        });
      }
  
      hide(callback) {
        if (!this._config.isVisible) {
          execute(callback);
          return;
        }
  
        this._getElement().classList.remove(CLASS_NAME_SHOW$5);
  
        this._emulateAnimation(() => {
          this.dispose();
          execute(callback);
        });
      }
  
      dispose() {
        if (!this._isAppended) {
          return;
        }
  
        EventHandler.off(this._element, EVENT_MOUSEDOWN);
  
        this._element.remove();
  
        this._isAppended = false;
      } // Private
  
  
      _getElement() {
        if (!this._element) {
          const backdrop = document.createElement('div');
          backdrop.className = this._config.className;
  
          if (this._config.isAnimated) {
            backdrop.classList.add(CLASS_NAME_FADE$4);
          }
  
          this._element = backdrop;
        }
  
        return this._element;
      }
  
      _configAfterMerge(config) {
        // use getElement() with the default "body" to get a fresh Element on each instantiation
        config.rootElement = getElement(config.rootElement);
        return config;
      }
  
      _append() {
        if (this._isAppended) {
          return;
        }
  
        const element = this._getElement();
  
        this._config.rootElement.append(element);
  
        EventHandler.on(element, EVENT_MOUSEDOWN, () => {
          execute(this._config.clickCallback);
        });
        this._isAppended = true;
      }
  
      _emulateAnimation(callback) {
        executeAfterTransition(callback, this._getElement(), this._config.isAnimated);
      }
  
    }
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): util/focustrap.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    /**
     * Constants
     */
  
    const NAME$8 = 'focustrap';
    const DATA_KEY$5 = 'bs.focustrap';
    const EVENT_KEY$5 = `.${DATA_KEY$5}`;
    const EVENT_FOCUSIN$2 = `focusin${EVENT_KEY$5}`;
    const EVENT_KEYDOWN_TAB = `keydown.tab${EVENT_KEY$5}`;
    const TAB_KEY = 'Tab';
    const TAB_NAV_FORWARD = 'forward';
    const TAB_NAV_BACKWARD = 'backward';
    const Default$7 = {
      autofocus: true,
      trapElement: null // The element to trap focus inside of
  
    };
    const DefaultType$7 = {
      autofocus: 'boolean',
      trapElement: 'element'
    };
    /**
     * Class definition
     */
  
    class FocusTrap extends Config {
      constructor(config) {
        super();
        this._config = this._getConfig(config);
        this._isActive = false;
        this._lastTabNavDirection = null;
      } // Getters
  
  
      static get Default() {
        return Default$7;
      }
  
      static get DefaultType() {
        return DefaultType$7;
      }
  
      static get NAME() {
        return NAME$8;
      } // Public
  
  
      activate() {
        if (this._isActive) {
          return;
        }
  
        if (this._config.autofocus) {
          this._config.trapElement.focus();
        }
  
        EventHandler.off(document, EVENT_KEY$5); // guard against infinite focus loop
  
        EventHandler.on(document, EVENT_FOCUSIN$2, event => this._handleFocusin(event));
        EventHandler.on(document, EVENT_KEYDOWN_TAB, event => this._handleKeydown(event));
        this._isActive = true;
      }
  
      deactivate() {
        if (!this._isActive) {
          return;
        }
  
        this._isActive = false;
        EventHandler.off(document, EVENT_KEY$5);
      } // Private
  
  
      _handleFocusin(event) {
        const {
          trapElement
        } = this._config;
  
        if (event.target === document || event.target === trapElement || trapElement.contains(event.target)) {
          return;
        }
  
        const elements = SelectorEngine.focusableChildren(trapElement);
  
        if (elements.length === 0) {
          trapElement.focus();
        } else if (this._lastTabNavDirection === TAB_NAV_BACKWARD) {
          elements[elements.length - 1].focus();
        } else {
          elements[0].focus();
        }
      }
  
      _handleKeydown(event) {
        if (event.key !== TAB_KEY) {
          return;
        }
  
        this._lastTabNavDirection = event.shiftKey ? TAB_NAV_BACKWARD : TAB_NAV_FORWARD;
      }
  
    }
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): modal.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    /**
     * Constants
     */
  
    const NAME$7 = 'modal';
    const DATA_KEY$4 = 'bs.modal';
    const EVENT_KEY$4 = `.${DATA_KEY$4}`;
    const DATA_API_KEY$2 = '.data-api';
    const ESCAPE_KEY$1 = 'Escape';
    const EVENT_HIDE$4 = `hide${EVENT_KEY$4}`;
    const EVENT_HIDE_PREVENTED$1 = `hidePrevented${EVENT_KEY$4}`;
    const EVENT_HIDDEN$4 = `hidden${EVENT_KEY$4}`;
    const EVENT_SHOW$4 = `show${EVENT_KEY$4}`;
    const EVENT_SHOWN$4 = `shown${EVENT_KEY$4}`;
    const EVENT_RESIZE$1 = `resize${EVENT_KEY$4}`;
    const EVENT_CLICK_DISMISS = `click.dismiss${EVENT_KEY$4}`;
    const EVENT_MOUSEDOWN_DISMISS = `mousedown.dismiss${EVENT_KEY$4}`;
    const EVENT_KEYDOWN_DISMISS$1 = `keydown.dismiss${EVENT_KEY$4}`;
    const EVENT_CLICK_DATA_API$2 = `click${EVENT_KEY$4}${DATA_API_KEY$2}`;
    const CLASS_NAME_OPEN = 'modal-open';
    const CLASS_NAME_FADE$3 = 'fade';
    const CLASS_NAME_SHOW$4 = 'show';
    const CLASS_NAME_STATIC = 'modal-static';
    const OPEN_SELECTOR$1 = '.modal.show';
    const SELECTOR_DIALOG = '.modal-dialog';
    const SELECTOR_MODAL_BODY = '.modal-body';
    const SELECTOR_DATA_TOGGLE$2 = '[data-bs-toggle="modal"]';
    const Default$6 = {
      backdrop: true,
      focus: true,
      keyboard: true
    };
    const DefaultType$6 = {
      backdrop: '(boolean|string)',
      focus: 'boolean',
      keyboard: 'boolean'
    };
    /**
     * Class definition
     */
  
    class Modal extends BaseComponent {
      constructor(element, config) {
        super(element, config);
        this._dialog = SelectorEngine.findOne(SELECTOR_DIALOG, this._element);
        this._backdrop = this._initializeBackDrop();
        this._focustrap = this._initializeFocusTrap();
        this._isShown = false;
        this._isTransitioning = false;
        this._scrollBar = new ScrollBarHelper();
  
        this._addEventListeners();
      } // Getters
  
  
      static get Default() {
        return Default$6;
      }
  
      static get DefaultType() {
        return DefaultType$6;
      }
  
      static get NAME() {
        return NAME$7;
      } // Public
  
  
      toggle(relatedTarget) {
        return this._isShown ? this.hide() : this.show(relatedTarget);
      }
  
      show(relatedTarget) {
        if (this._isShown || this._isTransitioning) {
          return;
        }
  
        const showEvent = EventHandler.trigger(this._element, EVENT_SHOW$4, {
          relatedTarget
        });
  
        if (showEvent.defaultPrevented) {
          return;
        }
  
        this._isShown = true;
        this._isTransitioning = true;
  
        this._scrollBar.hide();
  
        document.body.classList.add(CLASS_NAME_OPEN);
  
        this._adjustDialog();
  
        this._backdrop.show(() => this._showElement(relatedTarget));
      }
  
      hide() {
        if (!this._isShown || this._isTransitioning) {
          return;
        }
  
        const hideEvent = EventHandler.trigger(this._element, EVENT_HIDE$4);
  
        if (hideEvent.defaultPrevented) {
          return;
        }
  
        this._isShown = false;
        this._isTransitioning = true;
  
        this._focustrap.deactivate();
  
        this._element.classList.remove(CLASS_NAME_SHOW$4);
  
        this._queueCallback(() => this._hideModal(), this._element, this._isAnimated());
      }
  
      dispose() {
        for (const htmlElement of [window, this._dialog]) {
          EventHandler.off(htmlElement, EVENT_KEY$4);
        }
  
        this._backdrop.dispose();
  
        this._focustrap.deactivate();
  
        super.dispose();
      }
  
      handleUpdate() {
        this._adjustDialog();
      } // Private
  
  
      _initializeBackDrop() {
        return new Backdrop({
          isVisible: Boolean(this._config.backdrop),
          // 'static' option will be translated to true, and booleans will keep their value,
          isAnimated: this._isAnimated()
        });
      }
  
      _initializeFocusTrap() {
        return new FocusTrap({
          trapElement: this._element
        });
      }
  
      _showElement(relatedTarget) {
        // try to append dynamic modal
        if (!document.body.contains(this._element)) {
          document.body.append(this._element);
        }
  
        this._element.style.display = 'block';
  
        this._element.removeAttribute('aria-hidden');
  
        this._element.setAttribute('aria-modal', true);
  
        this._element.setAttribute('role', 'dialog');
  
        this._element.scrollTop = 0;
        const modalBody = SelectorEngine.findOne(SELECTOR_MODAL_BODY, this._dialog);
  
        if (modalBody) {
          modalBody.scrollTop = 0;
        }
  
        reflow(this._element);
  
        this._element.classList.add(CLASS_NAME_SHOW$4);
  
        const transitionComplete = () => {
          if (this._config.focus) {
            this._focustrap.activate();
          }
  
          this._isTransitioning = false;
          EventHandler.trigger(this._element, EVENT_SHOWN$4, {
            relatedTarget
          });
        };
  
        this._queueCallback(transitionComplete, this._dialog, this._isAnimated());
      }
  
      _addEventListeners() {
        EventHandler.on(this._element, EVENT_KEYDOWN_DISMISS$1, event => {
          if (event.key !== ESCAPE_KEY$1) {
            return;
          }
  
          if (this._config.keyboard) {
            event.preventDefault();
            this.hide();
            return;
          }
  
          this._triggerBackdropTransition();
        });
        EventHandler.on(window, EVENT_RESIZE$1, () => {
          if (this._isShown && !this._isTransitioning) {
            this._adjustDialog();
          }
        });
        EventHandler.on(this._element, EVENT_MOUSEDOWN_DISMISS, event => {
          // a bad trick to segregate clicks that may start inside dialog but end outside, and avoid listen to scrollbar clicks
          EventHandler.one(this._element, EVENT_CLICK_DISMISS, event2 => {
            if (this._element !== event.target || this._element !== event2.target) {
              return;
            }
  
            if (this._config.backdrop === 'static') {
              this._triggerBackdropTransition();
  
              return;
            }
  
            if (this._config.backdrop) {
              this.hide();
            }
          });
        });
      }
  
      _hideModal() {
        this._element.style.display = 'none';
  
        this._element.setAttribute('aria-hidden', true);
  
        this._element.removeAttribute('aria-modal');
  
        this._element.removeAttribute('role');
  
        this._isTransitioning = false;
  
        this._backdrop.hide(() => {
          document.body.classList.remove(CLASS_NAME_OPEN);
  
          this._resetAdjustments();
  
          this._scrollBar.reset();
  
          EventHandler.trigger(this._element, EVENT_HIDDEN$4);
        });
      }
  
      _isAnimated() {
        return this._element.classList.contains(CLASS_NAME_FADE$3);
      }
  
      _triggerBackdropTransition() {
        const hideEvent = EventHandler.trigger(this._element, EVENT_HIDE_PREVENTED$1);
  
        if (hideEvent.defaultPrevented) {
          return;
        }
  
        const isModalOverflowing = this._element.scrollHeight > document.documentElement.clientHeight;
        const initialOverflowY = this._element.style.overflowY; // return if the following background transition hasn't yet completed
  
        if (initialOverflowY === 'hidden' || this._element.classList.contains(CLASS_NAME_STATIC)) {
          return;
        }
  
        if (!isModalOverflowing) {
          this._element.style.overflowY = 'hidden';
        }
  
        this._element.classList.add(CLASS_NAME_STATIC);
  
        this._queueCallback(() => {
          this._element.classList.remove(CLASS_NAME_STATIC);
  
          this._queueCallback(() => {
            this._element.style.overflowY = initialOverflowY;
          }, this._dialog);
        }, this._dialog);
  
        this._element.focus();
      }
      /**
       * The following methods are used to handle overflowing modals
       */
  
  
      _adjustDialog() {
        const isModalOverflowing = this._element.scrollHeight > document.documentElement.clientHeight;
  
        const scrollbarWidth = this._scrollBar.getWidth();
  
        const isBodyOverflowing = scrollbarWidth > 0;
  
        if (isBodyOverflowing && !isModalOverflowing) {
          const property = isRTL() ? 'paddingLeft' : 'paddingRight';
          this._element.style[property] = `${scrollbarWidth}px`;
        }
  
        if (!isBodyOverflowing && isModalOverflowing) {
          const property = isRTL() ? 'paddingRight' : 'paddingLeft';
          this._element.style[property] = `${scrollbarWidth}px`;
        }
      }
  
      _resetAdjustments() {
        this._element.style.paddingLeft = '';
        this._element.style.paddingRight = '';
      } // Static
  
  
      static jQueryInterface(config, relatedTarget) {
        return this.each(function () {
          const data = Modal.getOrCreateInstance(this, config);
  
          if (typeof config !== 'string') {
            return;
          }
  
          if (typeof data[config] === 'undefined') {
            throw new TypeError(`No method named "${config}"`);
          }
  
          data[config](relatedTarget);
        });
      }
  
    }
    /**
     * Data API implementation
     */
  
  
    EventHandler.on(document, EVENT_CLICK_DATA_API$2, SELECTOR_DATA_TOGGLE$2, function (event) {
      const target = getElementFromSelector(this);
  
      if (['A', 'AREA'].includes(this.tagName)) {
        event.preventDefault();
      }
  
      EventHandler.one(target, EVENT_SHOW$4, showEvent => {
        if (showEvent.defaultPrevented) {
          // only register focus restorer if modal will actually get shown
          return;
        }
  
        EventHandler.one(target, EVENT_HIDDEN$4, () => {
          if (isVisible(this)) {
            this.focus();
          }
        });
      }); // avoid conflict when clicking modal toggler while another one is open
  
      const alreadyOpen = SelectorEngine.findOne(OPEN_SELECTOR$1);
  
      if (alreadyOpen) {
        Modal.getInstance(alreadyOpen).hide();
      }
  
      const data = Modal.getOrCreateInstance(target);
      data.toggle(this);
    });
    enableDismissTrigger(Modal);
    /**
     * jQuery
     */
  
    defineJQueryPlugin(Modal);
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): util/sanitizer.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    const uriAttributes = new Set(['background', 'cite', 'href', 'itemtype', 'longdesc', 'poster', 'src', 'xlink:href']);
    const ARIA_ATTRIBUTE_PATTERN = /^aria-[\w-]*$/i;
    /**
     * A pattern that recognizes a commonly useful subset of URLs that are safe.
     *
     * Shout-out to Angular https://github.com/angular/angular/blob/12.2.x/packages/core/src/sanitization/url_sanitizer.ts
     */
  
    const SAFE_URL_PATTERN = /^(?:(?:https?|mailto|ftp|tel|file|sms):|[^#&/:?]*(?:[#/?]|$))/i;
    /**
     * A pattern that matches safe data URLs. Only matches image, video and audio types.
     *
     * Shout-out to Angular https://github.com/angular/angular/blob/12.2.x/packages/core/src/sanitization/url_sanitizer.ts
     */
  
    const DATA_URL_PATTERN = /^data:(?:image\/(?:bmp|gif|jpeg|jpg|png|tiff|webp)|video\/(?:mpeg|mp4|ogg|webm)|audio\/(?:mp3|oga|ogg|opus));base64,[\d+/a-z]+=*$/i;
  
    const allowedAttribute = (attribute, allowedAttributeList) => {
      const attributeName = attribute.nodeName.toLowerCase();
  
      if (allowedAttributeList.includes(attributeName)) {
        if (uriAttributes.has(attributeName)) {
          return Boolean(SAFE_URL_PATTERN.test(attribute.nodeValue) || DATA_URL_PATTERN.test(attribute.nodeValue));
        }
  
        return true;
      } // Check if a regular expression validates the attribute.
  
  
      return allowedAttributeList.filter(attributeRegex => attributeRegex instanceof RegExp).some(regex => regex.test(attributeName));
    };
  
    const DefaultAllowlist = {
      // Global attributes allowed on any supplied element below.
      '*': ['class', 'dir', 'id', 'lang', 'role', ARIA_ATTRIBUTE_PATTERN],
      a: ['target', 'href', 'title', 'rel'],
      area: [],
      b: [],
      br: [],
      col: [],
      code: [],
      div: [],
      em: [],
      hr: [],
      h1: [],
      h2: [],
      h3: [],
      h4: [],
      h5: [],
      h6: [],
      i: [],
      img: ['src', 'srcset', 'alt', 'title', 'width', 'height'],
      li: [],
      ol: [],
      p: [],
      pre: [],
      s: [],
      small: [],
      span: [],
      sub: [],
      sup: [],
      strong: [],
      u: [],
      ul: []
    };
    function sanitizeHtml(unsafeHtml, allowList, sanitizeFunction) {
      if (!unsafeHtml.length) {
        return unsafeHtml;
      }
  
      if (sanitizeFunction && typeof sanitizeFunction === 'function') {
        return sanitizeFunction(unsafeHtml);
      }
  
      const domParser = new window.DOMParser();
      const createdDocument = domParser.parseFromString(unsafeHtml, 'text/html');
      const elements = [].concat(...createdDocument.body.querySelectorAll('*'));
  
      for (const element of elements) {
        const elementName = element.nodeName.toLowerCase();
  
        if (!Object.keys(allowList).includes(elementName)) {
          element.remove();
          continue;
        }
  
        const attributeList = [].concat(...element.attributes);
        const allowedAttributes = [].concat(allowList['*'] || [], allowList[elementName] || []);
  
        for (const attribute of attributeList) {
          if (!allowedAttribute(attribute, allowedAttributes)) {
            element.removeAttribute(attribute.nodeName);
          }
        }
      }
  
      return createdDocument.body.innerHTML;
    }
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): util/template-factory.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    /**
     * Constants
     */
  
    const NAME$5 = 'TemplateFactory';
    const Default$4 = {
      allowList: DefaultAllowlist,
      content: {},
      // { selector : text ,  selector2 : text2 , }
      extraClass: '',
      html: false,
      sanitize: true,
      sanitizeFn: null,
      template: '<div></div>'
    };
    const DefaultType$4 = {
      allowList: 'object',
      content: 'object',
      extraClass: '(string|function)',
      html: 'boolean',
      sanitize: 'boolean',
      sanitizeFn: '(null|function)',
      template: 'string'
    };
    const DefaultContentType = {
      entry: '(string|element|function|null)',
      selector: '(string|element)'
    };
    /**
     * Class definition
     */
  
    class TemplateFactory extends Config {
      constructor(config) {
        super();
        this._config = this._getConfig(config);
      } // Getters
  
  
      static get Default() {
        return Default$4;
      }
  
      static get DefaultType() {
        return DefaultType$4;
      }
  
      static get NAME() {
        return NAME$5;
      } // Public
  
  
      getContent() {
        return Object.values(this._config.content).map(config => this._resolvePossibleFunction(config)).filter(Boolean);
      }
  
      hasContent() {
        return this.getContent().length > 0;
      }
  
      changeContent(content) {
        this._checkContent(content);
  
        this._config.content = { ...this._config.content,
          ...content
        };
        return this;
      }
  
      toHtml() {
        const templateWrapper = document.createElement('div');
        templateWrapper.innerHTML = this._maybeSanitize(this._config.template);
  
        for (const [selector, text] of Object.entries(this._config.content)) {
          this._setContent(templateWrapper, text, selector);
        }
  
        const template = templateWrapper.children[0];
  
        const extraClass = this._resolvePossibleFunction(this._config.extraClass);
  
        if (extraClass) {
          template.classList.add(...extraClass.split(' '));
        }
  
        return template;
      } // Private
  
  
      _typeCheckConfig(config) {
        super._typeCheckConfig(config);
  
        this._checkContent(config.content);
      }
  
      _checkContent(arg) {
        for (const [selector, content] of Object.entries(arg)) {
          super._typeCheckConfig({
            selector,
            entry: content
          }, DefaultContentType);
        }
      }
  
      _setContent(template, content, selector) {
        const templateElement = SelectorEngine.findOne(selector, template);
  
        if (!templateElement) {
          return;
        }
  
        content = this._resolvePossibleFunction(content);
  
        if (!content) {
          templateElement.remove();
          return;
        }
  
        if (isElement(content)) {
          this._putElementInTemplate(getElement(content), templateElement);
  
          return;
        }
  
        if (this._config.html) {
          templateElement.innerHTML = this._maybeSanitize(content);
          return;
        }
  
        templateElement.textContent = content;
      }
  
      _maybeSanitize(arg) {
        return this._config.sanitize ? sanitizeHtml(arg, this._config.allowList, this._config.sanitizeFn) : arg;
      }
  
      _resolvePossibleFunction(arg) {
        return typeof arg === 'function' ? arg(this) : arg;
      }
  
      _putElementInTemplate(element, templateElement) {
        if (this._config.html) {
          templateElement.innerHTML = '';
          templateElement.append(element);
          return;
        }
  
        templateElement.textContent = element.textContent;
      }
  
    }
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): tooltip.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    /**
     * Constants
     */
  
    const NAME$4 = 'tooltip';
    const DISALLOWED_ATTRIBUTES = new Set(['sanitize', 'allowList', 'sanitizeFn']);
    const CLASS_NAME_FADE$2 = 'fade';
    const CLASS_NAME_MODAL = 'modal';
    const CLASS_NAME_SHOW$2 = 'show';
    const SELECTOR_TOOLTIP_INNER = '.tooltip-inner';
    const SELECTOR_MODAL = `.${CLASS_NAME_MODAL}`;
    const EVENT_MODAL_HIDE = 'hide.bs.modal';
    const TRIGGER_HOVER = 'hover';
    const TRIGGER_FOCUS = 'focus';
    const TRIGGER_CLICK = 'click';
    const TRIGGER_MANUAL = 'manual';
    const EVENT_HIDE$2 = 'hide';
    const EVENT_HIDDEN$2 = 'hidden';
    const EVENT_SHOW$2 = 'show';
    const EVENT_SHOWN$2 = 'shown';
    const EVENT_INSERTED = 'inserted';
    const EVENT_CLICK$1 = 'click';
    const EVENT_FOCUSIN$1 = 'focusin';
    const EVENT_FOCUSOUT$1 = 'focusout';
    const EVENT_MOUSEENTER = 'mouseenter';
    const EVENT_MOUSELEAVE = 'mouseleave';
    const AttachmentMap = {
      AUTO: 'auto',
      TOP: 'top',
      RIGHT: isRTL() ? 'left' : 'right',
      BOTTOM: 'bottom',
      LEFT: isRTL() ? 'right' : 'left'
    };
    const Default$3 = {
      allowList: DefaultAllowlist,
      animation: true,
      boundary: 'clippingParents',
      container: false,
      customClass: '',
      delay: 0,
      fallbackPlacements: ['top', 'right', 'bottom', 'left'],
      html: false,
      offset: [0, 0],
      placement: 'top',
      popperConfig: null,
      sanitize: true,
      sanitizeFn: null,
      selector: false,
      template: '<div class="tooltip" role="tooltip">' + '<div class="tooltip-arrow"></div>' + '<div class="tooltip-inner"></div>' + '</div>',
      title: '',
      trigger: 'hover focus'
    };
    const DefaultType$3 = {
      allowList: 'object',
      animation: 'boolean',
      boundary: '(string|element)',
      container: '(string|element|boolean)',
      customClass: '(string|function)',
      delay: '(number|object)',
      fallbackPlacements: 'array',
      html: 'boolean',
      offset: '(array|string|function)',
      placement: '(string|function)',
      popperConfig: '(null|object|function)',
      sanitize: 'boolean',
      sanitizeFn: '(null|function)',
      selector: '(string|boolean)',
      template: 'string',
      title: '(string|element|function)',
      trigger: 'string'
    };
    /**
     * Class definition
     */
  
    class Tooltip extends BaseComponent {
      constructor(element, config) {
        if (typeof Popper__namespace === 'undefined') {
          throw new TypeError('Bootstrap\'s tooltips require Popper (https://popper.js.org)');
        }
  
        super(element, config); // Private
  
        this._isEnabled = true;
        this._timeout = 0;
        this._isHovered = null;
        this._activeTrigger = {};
        this._popper = null;
        this._templateFactory = null;
        this._newContent = null; // Protected
  
        this.tip = null;
  
        this._setListeners();
  
        if (!this._config.selector) {
          this._fixTitle();
        }
      } // Getters
  
  
      static get Default() {
        return Default$3;
      }
  
      static get DefaultType() {
        return DefaultType$3;
      }
  
      static get NAME() {
        return NAME$4;
      } // Public
  
  
      enable() {
        this._isEnabled = true;
      }
  
      disable() {
        this._isEnabled = false;
      }
  
      toggleEnabled() {
        this._isEnabled = !this._isEnabled;
      }
  
      toggle() {
        if (!this._isEnabled) {
          return;
        }
  
        this._activeTrigger.click = !this._activeTrigger.click;
  
        if (this._isShown()) {
          this._leave();
  
          return;
        }
  
        this._enter();
      }
  
      dispose() {
        clearTimeout(this._timeout);
        EventHandler.off(this._element.closest(SELECTOR_MODAL), EVENT_MODAL_HIDE, this._hideModalHandler);
  
        if (this.tip) {
          this.tip.remove();
        }
  
        if (this._element.getAttribute('data-bs-original-title')) {
          this._element.setAttribute('title', this._element.getAttribute('data-bs-original-title'));
        }
  
        this._disposePopper();
  
        super.dispose();
      }
  
      show() {
        if (this._element.style.display === 'none') {
          throw new Error('Please use show on visible elements');
        }
  
        if (!(this._isWithContent() && this._isEnabled)) {
          return;
        }
  
        const showEvent = EventHandler.trigger(this._element, this.constructor.eventName(EVENT_SHOW$2));
        const shadowRoot = findShadowRoot(this._element);
  
        const isInTheDom = (shadowRoot || this._element.ownerDocument.documentElement).contains(this._element);
  
        if (showEvent.defaultPrevented || !isInTheDom) {
          return;
        } // todo v6 remove this OR make it optional
  
  
        if (this.tip) {
          this.tip.remove();
          this.tip = null;
        }
  
        const tip = this._getTipElement();
  
        this._element.setAttribute('aria-describedby', tip.getAttribute('id'));
  
        const {
          container
        } = this._config;
  
        if (!this._element.ownerDocument.documentElement.contains(this.tip)) {
          container.append(tip);
          EventHandler.trigger(this._element, this.constructor.eventName(EVENT_INSERTED));
        }
  
        if (this._popper) {
          this._popper.update();
        } else {
          this._popper = this._createPopper(tip);
        }
  
        tip.classList.add(CLASS_NAME_SHOW$2); // If this is a touch-enabled device we add extra
        // empty mouseover listeners to the body's immediate children;
        // only needed because of broken event delegation on iOS
        // https://www.quirksmode.org/blog/archives/2014/02/mouse_event_bub.html
  
        if ('ontouchstart' in document.documentElement) {
          for (const element of [].concat(...document.body.children)) {
            EventHandler.on(element, 'mouseover', noop);
          }
        }
  
        const complete = () => {
          EventHandler.trigger(this._element, this.constructor.eventName(EVENT_SHOWN$2));
  
          if (this._isHovered === false) {
            this._leave();
          }
  
          this._isHovered = false;
        };
  
        this._queueCallback(complete, this.tip, this._isAnimated());
      }
  
      hide() {
        if (!this._isShown()) {
          return;
        }
  
        const hideEvent = EventHandler.trigger(this._element, this.constructor.eventName(EVENT_HIDE$2));
  
        if (hideEvent.defaultPrevented) {
          return;
        }
  
        const tip = this._getTipElement();
  
        tip.classList.remove(CLASS_NAME_SHOW$2); // If this is a touch-enabled device we remove the extra
        // empty mouseover listeners we added for iOS support
  
        if ('ontouchstart' in document.documentElement) {
          for (const element of [].concat(...document.body.children)) {
            EventHandler.off(element, 'mouseover', noop);
          }
        }
  
        this._activeTrigger[TRIGGER_CLICK] = false;
        this._activeTrigger[TRIGGER_FOCUS] = false;
        this._activeTrigger[TRIGGER_HOVER] = false;
        this._isHovered = null; // it is a trick to support manual triggering
  
        const complete = () => {
          if (this._isWithActiveTrigger()) {
            return;
          }
  
          if (!this._isHovered) {
            tip.remove();
          }
  
          this._element.removeAttribute('aria-describedby');
  
          EventHandler.trigger(this._element, this.constructor.eventName(EVENT_HIDDEN$2));
  
          this._disposePopper();
        };
  
        this._queueCallback(complete, this.tip, this._isAnimated());
      }
  
      update() {
        if (this._popper) {
          this._popper.update();
        }
      } // Protected
  
  
      _isWithContent() {
        return Boolean(this._getTitle());
      }
  
      _getTipElement() {
        if (!this.tip) {
          this.tip = this._createTipElement(this._newContent || this._getContentForTemplate());
        }
  
        return this.tip;
      }
  
      _createTipElement(content) {
        const tip = this._getTemplateFactory(content).toHtml(); // todo: remove this check on v6
  
  
        if (!tip) {
          return null;
        }
  
        tip.classList.remove(CLASS_NAME_FADE$2, CLASS_NAME_SHOW$2); // todo: on v6 the following can be achieved with CSS only
  
        tip.classList.add(`bs-${this.constructor.NAME}-auto`);
        const tipId = getUID(this.constructor.NAME).toString();
        tip.setAttribute('id', tipId);
  
        if (this._isAnimated()) {
          tip.classList.add(CLASS_NAME_FADE$2);
        }
  
        return tip;
      }
  
      setContent(content) {
        this._newContent = content;
  
        if (this._isShown()) {
          this._disposePopper();
  
          this.show();
        }
      }
  
      _getTemplateFactory(content) {
        if (this._templateFactory) {
          this._templateFactory.changeContent(content);
        } else {
          this._templateFactory = new TemplateFactory({ ...this._config,
            // the `content` var has to be after `this._config`
            // to override config.content in case of popover
            content,
            extraClass: this._resolvePossibleFunction(this._config.customClass)
          });
        }
  
        return this._templateFactory;
      }
  
      _getContentForTemplate() {
        return {
          [SELECTOR_TOOLTIP_INNER]: this._getTitle()
        };
      }
  
      _getTitle() {
        return this._resolvePossibleFunction(this._config.title) || this._element.getAttribute('data-bs-original-title');
      } // Private
  
  
      _initializeOnDelegatedTarget(event) {
        return this.constructor.getOrCreateInstance(event.delegateTarget, this._getDelegateConfig());
      }
  
      _isAnimated() {
        return this._config.animation || this.tip && this.tip.classList.contains(CLASS_NAME_FADE$2);
      }
  
      _isShown() {
        return this.tip && this.tip.classList.contains(CLASS_NAME_SHOW$2);
      }
  
      _createPopper(tip) {
        const placement = typeof this._config.placement === 'function' ? this._config.placement.call(this, tip, this._element) : this._config.placement;
        const attachment = AttachmentMap[placement.toUpperCase()];
        return Popper__namespace.createPopper(this._element, tip, this._getPopperConfig(attachment));
      }
  
      _getOffset() {
        const {
          offset
        } = this._config;
  
        if (typeof offset === 'string') {
          return offset.split(',').map(value => Number.parseInt(value, 10));
        }
  
        if (typeof offset === 'function') {
          return popperData => offset(popperData, this._element);
        }
  
        return offset;
      }
  
      _resolvePossibleFunction(arg) {
        return typeof arg === 'function' ? arg.call(this._element) : arg;
      }
  
      _getPopperConfig(attachment) {
        const defaultBsPopperConfig = {
          placement: attachment,
          modifiers: [{
            name: 'flip',
            options: {
              fallbackPlacements: this._config.fallbackPlacements
            }
          }, {
            name: 'offset',
            options: {
              offset: this._getOffset()
            }
          }, {
            name: 'preventOverflow',
            options: {
              boundary: this._config.boundary
            }
          }, {
            name: 'arrow',
            options: {
              element: `.${this.constructor.NAME}-arrow`
            }
          }, {
            name: 'preSetPlacement',
            enabled: true,
            phase: 'beforeMain',
            fn: data => {
              // Pre-set Popper's placement attribute in order to read the arrow sizes properly.
              // Otherwise, Popper mixes up the width and height dimensions since the initial arrow style is for top placement
              this._getTipElement().setAttribute('data-popper-placement', data.state.placement);
            }
          }]
        };
        return { ...defaultBsPopperConfig,
          ...(typeof this._config.popperConfig === 'function' ? this._config.popperConfig(defaultBsPopperConfig) : this._config.popperConfig)
        };
      }
  
      _setListeners() {
        const triggers = this._config.trigger.split(' ');
  
        for (const trigger of triggers) {
          if (trigger === 'click') {
            EventHandler.on(this._element, this.constructor.eventName(EVENT_CLICK$1), this._config.selector, event => {
              const context = this._initializeOnDelegatedTarget(event);
  
              context.toggle();
            });
          } else if (trigger !== TRIGGER_MANUAL) {
            const eventIn = trigger === TRIGGER_HOVER ? this.constructor.eventName(EVENT_MOUSEENTER) : this.constructor.eventName(EVENT_FOCUSIN$1);
            const eventOut = trigger === TRIGGER_HOVER ? this.constructor.eventName(EVENT_MOUSELEAVE) : this.constructor.eventName(EVENT_FOCUSOUT$1);
            EventHandler.on(this._element, eventIn, this._config.selector, event => {
              const context = this._initializeOnDelegatedTarget(event);
  
              context._activeTrigger[event.type === 'focusin' ? TRIGGER_FOCUS : TRIGGER_HOVER] = true;
  
              context._enter();
            });
            EventHandler.on(this._element, eventOut, this._config.selector, event => {
              const context = this._initializeOnDelegatedTarget(event);
  
              context._activeTrigger[event.type === 'focusout' ? TRIGGER_FOCUS : TRIGGER_HOVER] = context._element.contains(event.relatedTarget);
  
              context._leave();
            });
          }
        }
  
        this._hideModalHandler = () => {
          if (this._element) {
            this.hide();
          }
        };
  
        EventHandler.on(this._element.closest(SELECTOR_MODAL), EVENT_MODAL_HIDE, this._hideModalHandler);
      }
  
      _fixTitle() {
        const title = this._element.getAttribute('title');
  
        if (!title) {
          return;
        }
  
        if (!this._element.getAttribute('aria-label') && !this._element.textContent.trim()) {
          this._element.setAttribute('aria-label', title);
        }
  
        this._element.setAttribute('data-bs-original-title', title); // DO NOT USE IT. Is only for backwards compatibility
  
  
        this._element.removeAttribute('title');
      }
  
      _enter() {
        if (this._isShown() || this._isHovered) {
          this._isHovered = true;
          return;
        }
  
        this._isHovered = true;
  
        this._setTimeout(() => {
          if (this._isHovered) {
            this.show();
          }
        }, this._config.delay.show);
      }
  
      _leave() {
        if (this._isWithActiveTrigger()) {
          return;
        }
  
        this._isHovered = false;
  
        this._setTimeout(() => {
          if (!this._isHovered) {
            this.hide();
          }
        }, this._config.delay.hide);
      }
  
      _setTimeout(handler, timeout) {
        clearTimeout(this._timeout);
        this._timeout = setTimeout(handler, timeout);
      }
  
      _isWithActiveTrigger() {
        return Object.values(this._activeTrigger).includes(true);
      }
  
      _getConfig(config) {
        const dataAttributes = Manipulator.getDataAttributes(this._element);
  
        for (const dataAttribute of Object.keys(dataAttributes)) {
          if (DISALLOWED_ATTRIBUTES.has(dataAttribute)) {
            delete dataAttributes[dataAttribute];
          }
        }
  
        config = { ...dataAttributes,
          ...(typeof config === 'object' && config ? config : {})
        };
        config = this._mergeConfigObj(config);
        config = this._configAfterMerge(config);
  
        this._typeCheckConfig(config);
  
        return config;
      }
  
      _configAfterMerge(config) {
        config.container = config.container === false ? document.body : getElement(config.container);
  
        if (typeof config.delay === 'number') {
          config.delay = {
            show: config.delay,
            hide: config.delay
          };
        }
  
        if (typeof config.title === 'number') {
          config.title = config.title.toString();
        }
  
        if (typeof config.content === 'number') {
          config.content = config.content.toString();
        }
  
        return config;
      }
  
      _getDelegateConfig() {
        const config = {};
  
        for (const key in this._config) {
          if (this.constructor.Default[key] !== this._config[key]) {
            config[key] = this._config[key];
          }
        }
  
        config.selector = false;
        config.trigger = 'manual'; // In the future can be replaced with:
        // const keysWithDifferentValues = Object.entries(this._config).filter(entry => this.constructor.Default[entry[0]] !== this._config[entry[0]])
        // `Object.fromEntries(keysWithDifferentValues)`
  
        return config;
      }
  
      _disposePopper() {
        if (this._popper) {
          this._popper.destroy();
  
          this._popper = null;
        }
      } // Static
  
  
      static jQueryInterface(config) {
        return this.each(function () {
          const data = Tooltip.getOrCreateInstance(this, config);
  
          if (typeof config !== 'string') {
            return;
          }
  
          if (typeof data[config] === 'undefined') {
            throw new TypeError(`No method named "${config}"`);
          }
  
          data[config]();
        });
      }
  
    }
    /**
     * jQuery
     */
  
  
    defineJQueryPlugin(Tooltip);
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): popover.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    /**
     * Constants
     */
  
    const NAME$3 = 'popover';
    const SELECTOR_TITLE = '.popover-header';
    const SELECTOR_CONTENT = '.popover-body';
    const Default$2 = { ...Tooltip.Default,
      content: '',
      offset: [0, 8],
      placement: 'right',
      template: '<div class="popover" role="tooltip">' + '<div class="popover-arrow"></div>' + '<h3 class="popover-header"></h3>' + '<div class="popover-body"></div>' + '</div>',
      trigger: 'click'
    };
    const DefaultType$2 = { ...Tooltip.DefaultType,
      content: '(null|string|element|function)'
    };
    /**
     * Class definition
     */
  
    class Popover extends Tooltip {
      // Getters
      static get Default() {
        return Default$2;
      }
  
      static get DefaultType() {
        return DefaultType$2;
      }
  
      static get NAME() {
        return NAME$3;
      } // Overrides
  
  
      _isWithContent() {
        return this._getTitle() || this._getContent();
      } // Private
  
  
      _getContentForTemplate() {
        return {
          [SELECTOR_TITLE]: this._getTitle(),
          [SELECTOR_CONTENT]: this._getContent()
        };
      }
  
      _getContent() {
        return this._resolvePossibleFunction(this._config.content);
      } // Static
  
  
      static jQueryInterface(config) {
        return this.each(function () {
          const data = Popover.getOrCreateInstance(this, config);
  
          if (typeof config !== 'string') {
            return;
          }
  
          if (typeof data[config] === 'undefined') {
            throw new TypeError(`No method named "${config}"`);
          }
  
          data[config]();
        });
      }
  
    }
     /**
    * jQuery
      */
  
  
     defineJQueryPlugin(Popover);
  
    /**
     * --------------------------------------------------------------------------
     * Bootstrap (v5.2.2): index.umd.js
     * Licensed under MIT (https://github.com/twbs/bootstrap/blob/main/LICENSE)
     * --------------------------------------------------------------------------
     */
    const index_umd = {
      Modal,
      Popover,
      Tooltip
    };
  
    return index_umd;
  
  }));
  //# sourceMappingURL=bootstrap.js.map
  