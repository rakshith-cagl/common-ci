//---IDLETIMEOUT
WinContainer.AppIdleTimeOut = (function () {
    var timerG;

    var _Start = function (req) {
        try {
            var count = 0;
            var maxTime = WinContainer.Settings.appProperties.idleTimeOut;
            if (maxTime == 0) {
                WinContainer.failureCallback(req.id, "APZ-CNT-153");
                return;
            }
            

            function setTimer() {
                if (count == maxTime) {
                    document.body.style.msTouchAction = "auto";
                    if (window.navigator.PointerEnabled) {
                        document.body.removeEventListener('pointerdown', resetTimer, false);
                        document.body.removeEventListener('pointermove', resetTimer, false);
                        document.body.removeEventListener('pointerup', resetTimer, false);
                        document.body.removeEventListener('pointerenter', resetTimer, false);
                        document.body.removeEventListener('pointerleave', resetTimer, false);
                        document.body.removeEventListener('pointerout', resetTimer, false);
                        document.body.removeEventListener('pointerover', resetTimer, false);

                    }
                    else if (window.navigator.msPointerEnabled) {
                        document.body.removeEventListener('MSPointerDown', resetTimer, false);
                        document.body.removeEventListener('MSPointerMove', resetTimer, false);
                        document.body.removeEventListener('MSPointerUp', resetTimer, false);
                        document.body.removeEventListener('MSPointerEnter', resetTimer, false);
                        document.body.removeEventListener('MSPointerLeave', resetTimer, false);
                        document.body.removeEventListener('MSPointerOut', resetTimer, false);
                        document.body.removeEventListener('MSPointerOver', resetTimer, false);

                    }
                    document.body.removeEventListener('MouseMove', resetTimer, false);
                    req.event = "timerExceeds";
                    WinContainer.successCallback(req);
                    WinContainer.Log.info("function timeOut call back is called");
                } else {
                    count = count + 1;
                    timerG = setTimeout(setTimer, 1000);
                }

            }

            function resetTimer(evt) {

                clearTimeout(timerG);
                count = 0;
                setTimer();

            }


            document.body.style.msTouchAction = "auto";
            if (window.navigator.PointerEnabled) {
                document.body.addEventListener('pointerdown', resetTimer, false);
                document.body.addEventListener('pointermove', resetTimer, false);
                document.body.addEventListener('pointerup', resetTimer, false);
                document.body.addEventListener('pointerenter', resetTimer, false);
                document.body.addEventListener('pointerleave', resetTimer, false);
                document.body.addEventListener('pointerout', resetTimer, false);
                document.body.addEventListener('pointerover', resetTimer, false);

            }
            else if (window.navigator.msPointerEnabled) {
                document.body.addEventListener('MSPointerDown', resetTimer, false);
                document.body.addEventListener('MSPointerMove', resetTimer, false);
                document.body.addEventListener('MSPointerUp', resetTimer, false);
                document.body.addEventListener('MSPointerEnter', resetTimer, false);
                document.body.addEventListener('MSPointerLeave', resetTimer, false);
                document.body.addEventListener('MSPointerOut', resetTimer, false);
                document.body.addEventListener('MSPointerOver', resetTimer, false);

            }
            document.body.addEventListener('MouseMove', resetTimer, false);
            var res = {};
            res.id = req.id;
            res.keepAlive = true;
            res.status = true;
            res.event = "started";
            WinContainer.successCallback(res);
            setTimer();

        } catch (e) {
            WinContainer.Log.fatal(e.message);
            WinContainer.failureCallback(req.id,"APZ-CNT-200");
        }
    }
    return {
        Start:_Start
    }
})();