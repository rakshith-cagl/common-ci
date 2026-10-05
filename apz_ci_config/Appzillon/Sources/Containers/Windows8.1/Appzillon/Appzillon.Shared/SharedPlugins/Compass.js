//--------Compass Plugin files-----------
//--------Accelerometer Plugin files ---------------
WinContainer.Compass = (function () {
    var _device = Windows.Devices.Sensors.Compass.getDefault(),
        _deviceInterval = null,
        _id = null;

    var _Start = function (req) {
        _id = req.id;
        var periodicity = req.periodicity;
        var interval = req.interval;

        if (_device == null) {
            WinContainer.Log.error("compass not found OR Access Denied by user");
            WinContainer.failureCallback(req.id, "APZ-CNT-062");
            return;
        }

        try {
            if (!_deviceInterval) {
                if (periodicity != "none") {
                    if (periodicity == "timed") {
                        interval = parseInt(interval);
                        interval = interval > 10 ? interval : 10;
                    }
                    else {
                        interval = 10;
                    }
                    _deviceInterval = setInterval(function () { getData(true); }, interval);
                }
                else {
                    getData(false);
                }
            }
        }
        catch (e) {
            WinContainer.Log.fatal(e.description);
            WinContainer.failureCallback(req.id, "APZ-CNT-063");
        }

    }
    function callback(id, kpAlive, r) {
        var res = {};
        res.id = id;
        res.keepAlive = kpAlive;
        res.magneticNorth = r.headingMagneticNorth;
        res.trueNorth = r.headingTrueNorth;
        WinContainer.successCallback(res);
    }
    function getData(keepAlive) {
        var r = _device.getCurrentReading();
        callback(_id, keepAlive, r);
    }
    
    var _Stop = function (req) {
        try {
            if (_device == null) {
                WinContainer.Log.error("compass not found in use");
                WinContainer.failureCallback(req.id, "APZ-CNT-062");
                return;
            }

            if (_deviceInterval) {
                clearInterval(_deviceInterval);
                _deviceInterval = null;
                if (req.id != _id) {
                    getData(false);
                }
            }
            req.keepAlive = false;
            WinContainer.successCallback(req);
        }
        catch (e) {
        }
    };
    //-----Exposed Method------------
    return {
        Start: _Start,
        Stop: _Stop
    }


})();
