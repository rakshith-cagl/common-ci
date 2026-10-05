//--------Accelerometer Plugin files ---------------
WinContainer.AcceleroMeter = (function () {
    var _device = Windows.Devices.Sensors.Accelerometer.getDefault(),
        _deviceInterval = null,
        _id = null;

    var _Start = function (req) {
        _id = req.id;
        var periodicity = req.periodicity;
        var interval = req.interval;
       // _device = getDevice();

        if (_device == null) {
            WinContainer.Log.error("No Accelerometer Device Found Or Access Denied by user");
            WinContainer.failureCallback(req.id, ErrorCode.DEVICE_NOT_FOUND);
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
            WinContainer.Log.fatal("Accelerometer "+e.message);
            WinContainer.failureCallback(req.id, ErrorCode.RUNTIME_EXCEPTION);
        }

    }
    function callback(id, kpAlive, r) {
        var res = {};
        res.id = id;
        res.keepAlive = kpAlive;
        res.xCord = r.accelerationX;
        res.yCord = r.accelerationY;
        res.zCord = r.accelerationZ;
        WinContainer.successCallback(res);
    }
    function getData(keepAlive) {
        var r = _device.getCurrentReading();
        callback(_id, keepAlive, r);
    }
    
    var _Stop = function (req) {
        try {
            var id = req.id;
            if (_device == null) {
                WinContainer.Log.error("No Accelerometer Device Found in use");
                WinContainer.failureCallback(req.id, ErrorCode.DEVICE_NOT_FOUND);
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
        start: _Start,
        stop: _Stop
    }


})();
//-------------