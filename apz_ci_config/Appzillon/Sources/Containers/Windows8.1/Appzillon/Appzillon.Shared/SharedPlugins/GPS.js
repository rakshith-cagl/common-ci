//--------GPS Plugin file-----------
WinContainer.GPS = (function () {
    var _device = Windows.Devices.Geolocation.Geolocator(),
        _gpsRunning=false,
        _id = null;
    _keepAlive = false;
    var _Start = function (req) {
        _device =  Windows.Devices.Geolocation.Geolocator();
        if (_gpsRunning)
            return;
        WinContainer.removeAlertUI();

        _id = req.id;
        var periodicity = req.periodicity;
        var distanceInterval = req.distanceInterval;
        var timeInterval = req.timeInterval;
        if (timeInterval == null || timeInterval == undefined)
            timeInterval = req.interval;
        if (_device == null) {
            WinContainer.failureCallback(req.id, "APZ-CNT-059");
            WinContainer.Log.fatal("GPS Device Not Present Or Access Denied by user");
            return;
        }
        try {
           
                _device.desiredAccuracy = Windows.Devices.Geolocation.PositionAccuracy.default;
                if (periodicity == "onChange") {
                    _keepAlive = true;
                    _device.movementThreshold = distanceInterval > 0 ? distanceInterval : _device.movementThreshold;
                    _device.reportInterval = 10;
                }
                else if (periodicity == "intervalBased" || periodicity == "timed") {
                    _keepAlive = true;
                    _device.reportInterval = timeInterval > 0 ? timeInterval : 1;
                }
                else if (periodicity == 'none') {
                    _device.getGeopositionAsync().then(function (cord) {
                        var r = cord.coordinate;
                        callback(_id, false, r);
                        _device = null;
                        _gpsRunning = false;
                        return;
                    }, function (e)
                    {
                        return;
                    });                  
                }
                if (_keepAlive) {
                    _gpsRunning = true;
                    _device.addEventListener("positionchanged", onPositionChanged);
                }
           
        }
        catch (e) {
            WinContainer.failureCallback(req.id, "APZ-CNT-082");
        }

    }
    function callback(id, kpAlive, r) {
        var res = {};
        res.id = id;
        res.keepAlive = kpAlive;
        res.latitude = r.latitude;
        res.longitude = r.longitude;

        WinContainer.successCallback(res);
    }
    
    function onPositionChanged(e) {     
            var r = e.position.coordinate;
            if (_gpsRunning) {
                callback(_id, true, r);
            }
       
    }
    var _Stop = function (req) {
        try {
            if (_device == null) {
                WinContainer.failureCallback(req.id, "APZ-CNT-059");
                WinContainer.Log.fatal("GPS Device Not found in use");
                return;
            }

            if (_device != null && _gpsRunning) {
                _device.removeEventListener("positionchanged", onPositionChanged);
                _device.reportInterval = 0;
                _device = null;
                _gpsRunning = false;
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
