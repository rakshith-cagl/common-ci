WinContainer.Gesture = (function () {

    var req_id;

    var tapped = null;
    var firstEvent = true;
    var gr;
    var singletapG = false;
    var doubletapG = false;
    var longpressG = false;
    var pinchG = false;
    var swipeG = false;
    var alreadyStarted = false;


    var startGesture = function (json) {

        req_id = json.id;
        var applicationData = Windows.Storage.ApplicationData.current;
        var localSettings = applicationData.localSettings;


        singletapG = gestureStartValidation(json.singletap, singletapG);
        doubletapG = gestureStartValidation(json.doubletap, doubletapG);
        longpressG = gestureStartValidation(json.longpress, longpressG);
        pinchG = gestureStartValidation(json.pinch, pinchG);
        swipeG = gestureStartValidation(json.swipe, swipeG);

/*
        if (swipeG && (localSettings.values["SwipeGestureRecognitionStart"] == null))
            appzillon.plugin.sendAuditLog("SwipeGestureRecognitionStart", "STARTEX");
        if (singletapG && (localSettings.values["SingleTapGestureRecognitionStart"] == null))
            appzillon.plugin.sendAuditLog("SingleTapGestureRecognitionStart", "STARTEX");
        if (pinchG && (localSettings.values["PinchGestureRecognitionStart"] == null))
            appzillon.plugin.sendAuditLog("PinchGestureRecognitionStart", "STARTEX");
        if (doubletapG && (localSettings.values["DoubleTapGestureRecognitionStart"] == null))
            appzillon.plugin.sendAuditLog("DoubleTapGestureRecognitionStart", "STARTEX");
        if (longpressG && (localSettings.values["LongPressGestureRecognitionStart"] == null))
            appzillon.plugin.sendAuditLog("LongPressGestureRecognitionStart", "STARTEX");

            */

        if (alreadyStarted)
            return;




        function gestureStartValidation(action, gesture) {
            if (gesture)
                return true;
            if (action == "Y")
                return true;
            else
                return false;
        }

        try {
            registerGestureEvents();
            alreadyStarted = true;
            $(window).bind("scroll", function () {
                deRegisterGestureEvents();
                registerGestureEvents();

            });

            //appzillon.plugin.storeLog("Gesture Recognition Started", "D");
            var data = JSON.stringify({
                successMessage: "Gesture Recognition Started"
            }, null, " ");
            var js = JSON.parse(data);
            js.event = "started";
            js.id = json.id;
            js.keepAlive = true;
            WinContainer.successCallback(js);
        } catch (e) {         
            WinContainer.failureCallback(json.id, "APZ-CNT-082");   //gesture start fail
            WinContainer.Log.error(e.description);
        }


    }



    function processDown (evt) {
        try {

            var pp = evt.getCurrentPoint(document.body);
            evt.stopImmediatePropagation = true;

            // Feed the PointerPoint to GestureRecognizer
            gr.processDownEvent(pp);
        }
        catch (e) {
            WinContainer.Log.warn(e.description);
            var ab = 6;
        }
    };

    function tappedHandler  (evt) {
        if (!tapped) {

            tapped = setTimeout(function () {
                tapped = null;
                if (singletapG)
                    gestureS("singleTap");
            }, 400);


        } else {

            clearTimeout(tapped);
            tapped = null;
            if (doubletapG)
                gestureS("doubleTap");


        }



    };


    function longPressHandler (evt) {
        if (longpressG) {
            var value = Windows.UI.Input.HoldingState.completed;

            if (value == 1) {
                gestureS("longPress");

            }
        }

    };


    function processMove(evt) {
        // Get intermediate PointerPoints
        try {
            evt.stopImmediatePropagation = true;
            var pps = evt.getIntermediatePoints(document.body);

            // processMoveEvents takes an array of intermediate PointerPoints
            gr.processMoveEvents(pps);
        }
        catch (e) {

        }

    };

    function processUp(evt) {
        try {
            evt.stopImmediatePropagation = true;
            // Get the current PointerPoint
            var pp = evt.getCurrentPoint(document.body);

            // Feed GestureRecognizer
            gr.processUpEvent(pp);
        }
        catch (e) { }

    };

    function processMouse(evt) {
        try {
            evt.stopImmediatePropagation = true;
            var pp = evt.getCurrentPoint(document.body);


            gr.processMouseWheelEvent(pp, evt.shiftKey, evt.ctrlKey);
        }
        catch (e) { }
    };

    // The following functions are registered to handle GestureRecognizer gesture events




    function manipulationCompletedHandler(evt) {

        if (evt.cumulative) {
            if (evt.cumulative.expansion > 0 && pinchG) {

                gestureS("zoomIn");
            } else if (evt.cumulative.expansion < 0 && pinchG) {

                gestureS("zoomOut");
            } else {
                if (Math.abs(evt.cumulative.translation.x) > Math.abs(evt.cumulative.translation.y) && swipeG) {
                    if (evt.cumulative.translation.x < 0)
                        gestureS("swipeLeft");
                    else
                        gestureS("swipeRight");
                } else if (swipeG && evt.cumulative.expansion == 0) {
                    if (evt.cumulative.translation.y < 0)
                        gestureS("swipeUp");
                    else
                        gestureS("swipeDown");
                }
            }

        }

    };

    function gestureS(event) {
        var res = {};
        res.id = req_id;
        res.event = event;
        res.keepAlive = true;
        if (event != "singleTap" && event != "doubleTap") {
            if (firstEvent) {
                firstEvent = false;               
                WinContainer.successCallback(res);
                setTimeout(function () {
                    firstEvent = true;

                }, 400);
            }
        } else
            WinContainer.successCallback(res);
    }
    function deRegisterGestureEvents() {
        document.body.removeEventListener('pointerdown', processDown, false);
        document.body.removeEventListener('pointermove', processMove, false);
        document.body.removeEventListener('pointerup', processUp, false);
        document.body.removeEventListener('pointercancel', processUp, false);

        document.body.removeEventListener('wheel', processMouse, false);


        gr.removeEventListener('manipulationcompleted', manipulationCompletedHandler);
        gr.removeEventListener("holding", longPressHandler);


        gr.removeEventListener('tapped', tappedHandler);
        document.body.style.msTouchAction = "auto";
        gr = null;

    }
    function registerGestureEvents() {
        gr = new Windows.UI.Input.GestureRecognizer();

        gr.gestureSettings =
            Windows.UI.Input.GestureSettings.manipulationRotate |
            Windows.UI.Input.GestureSettings.manipulationTranslateX |
            Windows.UI.Input.GestureSettings.manipulationTranslateY |
            Windows.UI.Input.GestureSettings.manipulationScale |
            Windows.UI.Input.GestureSettings.manipulationRotateInertia |
            Windows.UI.Input.GestureSettings.manipulationScaleInertia |
            Windows.UI.Input.GestureSettings.manipulationTranslateInertia |
            Windows.UI.Input.GestureSettings.hold |
            Windows.UI.Input.GestureSettings.holdWithMouse |

            Windows.UI.Input.GestureSettings.tap;
        gr.showGestureFeedback = false;

        document.body.addEventListener('pointerdown', processDown, false);
        document.body.addEventListener('pointermove', processMove, false);
        document.body.addEventListener('pointerup', processUp, false);
        document.body.addEventListener('pointercancel', processUp, false);

        document.body.addEventListener('wheel', processMouse, false);


        /* if (swipeG && !pinchG)
             document.body.style.msTouchAction = "pinch-zoom";
         else if (!swipeG && pinchG) {*/
        if (!swipeG)
            document.body.style.msTouchAction = 'pan-y';
        else
            document.body.style.msTouchAction = 'none';

        // gr.addEventListener('manipulationstarted', manipulationStartedHandler);
        gr.addEventListener('manipulationcompleted', manipulationCompletedHandler);
        gr.addEventListener("holding", longPressHandler);

        //gr.addEventListener('manipulationcompleted', manipulationEndHandler);
        gr.addEventListener('tapped', tappedHandler);
    }

    var stopGesture = function (req) {
        try {
           
            deRegisterGestureEvents();
            swipeG = singletapG = doubletapG = longpressG = pinchG = alreadyStarted = false;
            $(window).unbind('scroll');
            req.event = "stopped";
            WinContainer.successCallback(req);

        } catch (e) {
            WinContainer.failureCallback(id, "APZ-CNT-082", e.message);
            WinContainer.Log.error(e.description);
        }
    }

    return {
        start: startGesture,
        stop: stopGesture
    }
})();