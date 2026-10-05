WinContainer.GeoFencing = (function () {
    var res = {};
    res.event = "GEOFENCE";
    var region,
     countryList,
     coordinates,
     radius,
     count,
     loc;


    function activateGeoFencing(req) {
        WinContainer.removeAlertUI();
        res.id = req.id;
        var region = req.region;
        var countryList = req.countryList;
        var coordinates = req.coordinates;
        var radius = req.radius;

        try {
            loc = new Windows.Devices.Geolocation.Geolocator();
            loc.desiredAccuracy = Windows.Devices.Geolocation.PositionAccuracy.default;
            loc.desiredAccuracyInMeters = 50;
            var getLocationDetails = WinContainer.isPhone() ? getLocationDetailsPhone : getLocationDetailsDesktop;

            loc.getGeopositionAsync(12000, 10000000000).then(function (pos) {
                count = 0;
                if (region == "COUNTRY")
                    getLocationDetails(pos, function (s, r) {
                        if (s) {
                            res.allowLocation = r;
                            WinContainer.successCallback(res);
                        } else {
                            WinContainer.failureCallback(res.id, "APZ-CNT-092");
                        }
                    });

                else {
                    var pos2 = coordinates.split(",");
                    var distance = getDistanceBetweenPoints(pos.coordinate.latitude, pos.coordinate.longitude, pos2[0], pos2[1]);
                    if (distance > radius) {
                        res.allowLocation = true;
                        WinContainer.successCallback(res);
                    } else {
                        res.allowLocation = false;
                        WinContainer.successCallback(res);
                    }
                }
            }, function (e) {
                WinContainer.failureCallback(req_id, "APZ-CNT-082", e.message);
                WinContainer.Log.error(e.message);
            });
        } catch (e) {
            WinContainer.failureCallback(req_id, "APZ-CNT-082", e.message);
            WinContainer.Log.error(e.description);
        }
    }

    function errorHandler(e) {
        WinContainer.failureCallback(req_id, "APZ-CNT-082", e.message);
        WinContainer.Log.error(e.message);
    }
    function getPositionHandlerDesktop(pos) {
        WinContainer.removeAlertUI();
    }
    function getLocationDetailsDesktop(pos, callback) {

        var query = pos.coordinate.latitude + "," + pos.coordinate.longitude;

        var searchRequest = 'http://dev.virtualearth.net/REST/v1/Locations/' + query
            + '?incl=ciso2&o=json&key='
            + WinContainer.Settings.containerProperties[JsonKey.BING_MAPS_KEY];//'AvOW5Fz4QTsubTTdmaVnseeZnAQ0JYwbx_6zdMdgHk6iF-pnoTE7vojUFJ1kXFTP';

        WinJS.xhr({
            url: searchRequest
        }).done(function (response) {
            try {
                var json = JSON.parse(response.responseText);

                var countryCode = json.resourceSets[0].resources[0].address.countryRegionIso2; // address is stored here
                for (i = 0; i < countryList.length; i++) {
                    if (countryCode == countryList[i]) {
                        callback(true, true);
                        return;
                    }
                }
                callback(true, false);
            } catch (e) {
                if (e.message == "Unable to get property 'address' of undefined or null reference") {
                    if (count <= 10) {
                        getLocationDetails(pos, callback);
                        count++;
                    }
                    else {
                        callback(false, "", e.message);
                    }

                }
                else {
                    callback(false, "", e.message);
                }
            }
        }, function (e) {
            // handle error here...
            if (count <= 10) {
                getLocationDetails(pos, callback);
                count++;
            }
            else {
                WinContainer.failureCallback(req_id, "APZ-CNT-092", e.message);
            }
        });
    }
    function getLocationDetailsPhone(pos, callback) {
        try {

            var basicGeoposition = {
                altitude: pos.coordinate.altitude,
                latitude: pos.coordinate.latitude,
                longitude: pos.coordinate.longitude
            };
            var geopoint = new Windows.Devices.Geolocation.Geopoint(basicGeoposition);
            Windows.Services.Maps.MapLocationFinder.findLocationsAtAsync(geopoint).done(function (result) {
                for (i = 0; i < countryList.length; i++) {
                    if (result.locations[0].address.countryCode == countryList[i]) {
                        callback(true, false);
                        return;
                    }
                }
                callback(true, true);
            });
        } catch (e) {
            callback(false, "APZ-CNT-082");
            WinContainer.Log.error(e.message);
        }
    }

    return {
        activate:activateGeoFencing
    }
})();