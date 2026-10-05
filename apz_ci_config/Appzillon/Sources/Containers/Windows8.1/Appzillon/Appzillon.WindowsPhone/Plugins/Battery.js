//-------------------------------------------------------------------
(function () {
    var battery = Windows.Phone.Devices.Power.Battery.getDefault(),
        prevTime = battery.remainingDischargeTime,
        prevPercent = battery.remainingChargePercent,
        thresholdValue = false,
        levelInterval = null,
        timeInterval = null,
        isLevel = false;
    id = "";
    function battery_Event(v) {
        //var newTime = battery.remainingDischargeTime;
        var newPercent = battery.remainingChargePercent;
        var j = {};
        j.state = null;
        j.level = newPercent;
        if (newPercent != prevPercent) {
            prevPercent = newPercent;
            if (thresholdValue && thresholdValue == (newPercent)) {
                j.event = "threshold";
                try {
                    j.id = id;
                    j.keepAlive = true;
                    WinContainer.successCallback(j);
                } catch (e) {
                    console.log(e.message);
                }
            }
            j.event = "level";
            try {
                if (isLevel) {
                    j.id = id;
                    j.keepAlive = true;
                    WinContainer.successCallback(j);
                }
            } catch (e) {
                console.log(e.message);
            }
        }

        if (v == 'time') {
            j.event = "time";
            try {
                j.id = id;
                j.keepAlive = true;
                WinContainer.successCallback(j);                                                           //-------------- appzillon.app.batteryStatus(j);
            } catch (e) {
                console.log(e.message);
            }
        }
    }


    function stopLevelInterval() {
        clearInterval(levelInterval);
    }
    function startLevelInterval() {
        try { clearInterval(levelInterval); } catch (e) { }
        levelInterval = setInterval(function () { battery_Event(); }, 10000);
    }
    function stopTimeInterval() {
        clearInterval(timeInterval);
    }
    function startTimeInterval(t) {
        timeInterval = setInterval(function () { battery_Event("time"); }, t);
    }

    WinContainer.plugin.startBatteryMonitor = function (json) {
        try {
            id = json.id;
            if (json.level.toUpperCase() == "Y") {
                isLevel = true;
            }
            if (!(json.time == undefined || json.time == "null" || json.time == null || json.time == "0")) {
                stopTimeInterval();
                var time = parseInt(json.time);
                time = time > 0 ? time : false;
                if (time) {
                    time = time * 1000;
                    startTimeInterval(time);
                }
            }
            if (!(json.threshold == undefined || json.threshold == "null" || json.threshold == null || json.threshold == "0")) {
                var v = parseInt(json.threshold);
                thresholdValue = v > 0 && v < 100 ? v : false;
            }
            if (isLevel || thresholdValue) {
                startLevelInterval();
            }
            var j = { "event": "started" };
            if (levelInterval || timeInterval) {
                j.id = id;
                j.state = null;
                j.level = battery.remainingChargePercent;
                j.keepAlive = true;
                WinContainer.successCallback(j);
            }
            else
                throw "no event";
        } catch (e) {
            var j = { "errorCode": "", "errorDescription": "Invalid json" };
            WinContainer.failureCallback(id, "APZ-CNT-241");
            WinContainer.Log.error(e.description);
        }
    }


    WinContainer.plugin.stopBatteryMonitor = function (json) {
        isLevel = false;
        stopTimeInterval();
        thresholdValue = false;
        stopLevelInterval();
        var j = { "event": "stopped" }
        j.id = json.id;
        j.keepAlive = true;
        WinContainer.successCallback(j);
    }
    // start();
})();