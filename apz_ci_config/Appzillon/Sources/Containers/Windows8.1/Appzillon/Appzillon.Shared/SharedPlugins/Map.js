//--------Plugin files-----------
WinContainer.plugin.loadMap = function (jsonObj) {
    var res = {};
    res.id = jsonObj.id;
    WinContainer.removeAlertUI();
    try {

        var locator = Windows.Devices.Geolocation.Geolocator();
        locator.desiredAccuracy = Windows.Devices.Geolocation.PositionAccuracy.default;
        var curLatitude;
        var curLongitude;

        // Get the geoposition, capturing the request in a 'promise' object.
        var promise = locator.getGeopositionAsync().then(function (pos) {
            // Get the coordinates of the current location.
            var coord = pos.coordinate;
            //location = new Microsoft.Maps.Location(coord.latitude, coord.longitude);
            curLatitude = coord.latitude;
            curLongitude = coord.longitude;

            var mapOptions = {
                credentials: WinContainer.Settings.containerProperties[JsonKey.BING_MAPS_KEY],
                center: new Microsoft.Maps.Location(curLatitude, curLongitude),
                mapTypeId: Microsoft.Maps.MapTypeId.road,
                zoom: 4
            };
            var map;

            var locationName = new Array;
            var locationDescription = new Array;
            var locationLatitude = new Array;
            var locationLongitude = new Array;
            var markerInfo = new Array;
            markerInfo = jsonObj.markerInfo;
            for (var i = 0; i < markerInfo.length; i++) {

                locationName[i] = jsonObj.markerInfo[i].locationName;
                locationDescription[i] = jsonObj.markerInfo[i].locationDescription;
                locationLatitude[i] = jsonObj.markerInfo[i].locationLatitude;
                locationLongitude[i] = jsonObj.markerInfo[i].locationLongitude;

            }
            Microsoft.Maps.loadModule('Microsoft.Maps.Map', { callback: initMap });
            function initMap() {
                var divMap = document.createElement('div');
                divMap.id = "mapDiv";

                divMap.style.position = "absolute";
                divMap.style.top = 0;
                divMap.style.left = 0;
                divMap.style.zIndex = 9999999999;
                divMap.style.width = "100%";
                divMap.style.height = "100%";
                if (WinContainer.isPhone()) {
                    WinJS.Application.onbackclick = function () {
                        try {
                            var elem = document.getElementById('mapDiv');
                            elem.parentNode.removeChild(elem);
                            document.body.style.overflowX = "visible";
                            document.body.style.overflowY = "visible";
                        } catch (e) { }
                        return true;
                    }
                } else {
                    var divButton = document.createElement('div');
                    var button = document.createElement('input');
                    button.type = "submit";
                    button.value = "X";
                    button.style.position = "absolute";
                    button.style.top = "5px";
                    button.style.right = "5px";
                    button.style.border = "2px solid #c2c2c2";
                    button.style.padding = "1px 5px";
                    button.style.backgroundColor = "#605F61";
                    button.style.zIndex = 99999999999;
                    button.style.height = "38px";
                    button.style.width = "38px";
                    button.style.borderRadius = "150px";
                    button.addEventListener("click", function () {
                        var elem = document.getElementById('mapDiv');
                        elem.parentNode.removeChild(elem);
                        document.body.style.overflowX = "visible";
                        document.body.style.overflowY = "visible";

                    }, false);

                    divButton.appendChild(button);
                    divMap.appendChild(divButton);
                }

                map = new Microsoft.Maps.Map(divMap, mapOptions);
                document.body.appendChild(divMap);
                document.getElementById("mapDiv").focus();
                document.body.style.overflowX = "hidden";
                document.body.style.overflowY = "hidden";
                var loc1;
                var loc2;
                var pin = new Array;
                var pinInfoBox;
                var pinLayer = new Microsoft.Maps.EntityCollection();


                for (var i = 0; i < markerInfo.length; i++) {

                    loc1 = new Microsoft.Maps.Location(locationLatitude[i], locationLongitude[i]);
                    pin[i] = new Microsoft.Maps.Pushpin(loc1);
                    pin[i].title = locationName[i];
                    pin[i].description = locationDescription[i];
                    pinLayer.push(pin[i]);
                    Microsoft.Maps.Events.addHandler(pin[i], 'click', displayInfo);
                }
                // Current location pin
                loc2 = new Microsoft.Maps.Location(curLatitude, curLongitude);
                pin[i] = new Microsoft.Maps.Pushpin(loc2);
                pin[i].title = "Current Location";
                pin[i].description = " Your Current Position";
                pinLayer.push(pin[i]);
                Microsoft.Maps.Events.addHandler(pin[i], 'click', displayInfo);
                map.entities.push(pinLayer);
                Microsoft.Maps.Events.addHandler(pin, 'click', displayInfo);
                var arrLocations = [];
                arrLocations.push(loc1);
                arrLocations.push(loc2);
                var rect = Microsoft.Maps.LocationRect.fromLocations(arrLocations);
                setTimeout((function () {
                    map.setView({ bounds: rect });
                }).bind(this), 1500);
                WinContainer.successCallback(res);
            }

            function displayInfo(e) {
                if (e.targetType == "pushpin") {
                    showInfobox(e.target);
                }
            }

            var infoBoxlayer = new Microsoft.Maps.EntityCollection();
            var infoBoxpresent;

            function showInfobox(shape) {
                if (infoBoxpresent) {
                    infoBoxlayer.clear();
                    infoBoxpresent = false;
                }
                var infoboxOptions = {
                    width: 170,
                    height: 80,
                    showCloseButton: true,
                    zIndex: 10,
                    offset: new Microsoft.Maps.Point(0, 25),
                    showPointer: true,
                    title: shape.title,
                    description: shape.description
                };
                infoBoxpresent = true;
                var defInfobox = new Microsoft.Maps.Infobox(shape.getLocation(), infoboxOptions);
                infoBoxlayer.push(defInfobox);
                map.entities.push(infoBoxlayer);
            }

        }, function (e) {
            WinContainer.failureCallback(res.id, "APZ-CNT-107", e.message);
            WinContainer.Log.error(e.message);
        });
    }
    catch (e) {
        WinContainer.failureCallback(res.id, "APZ-CNT-107", e.message);
        WinContainer.Log.error(e.description);
    }
}

var currentLocation;
WinContainer.plugin.drivingDirection = function (json) {
    WinContainer.removeAlertUI();
    if (json.fromLocation == null || json.fromLocation == "") {
        currentLocation = new Windows.Devices.Geolocation.Geolocator();
        currentLocation.desiredAccuracy = Windows.Devices.Geolocation.PositionAccuracy.default;
        currentLocation.desiredAccuracyInMeters = 50;
        currentLocation.getGeopositionAsync(12000, 10000000000).then(function (loc) {
            json.fromLocation = loc.coordinate.latitude + "," + loc.coordinate.longitude;
            WinContainer.plugin.displayMapRoute(json);
        }, function (e) {
            WinContainer.failureCallback(json.id, "", "APZ-CNT-092" + e.message);
            WinContainer.Log.warn(e.message);
        });
    }
    else
        WinContainer.plugin.displayMapRoute(json);
}

WinContainer.plugin.displayMapRoute = function (json) {

    //if (!WinContainer.plugin.validaterouteplugin(json))
    //    return;
    var res = {};
    res.id = json.id;
    var fromLocation = json.fromLocation;
    var toLocation = json.toLocation;

    try {
        var fromCoordinates = fromLocation.split(",");
        var toCoordinates = toLocation.split(",");
        var mapOptions = {
            credentials: WinContainer.Settings.containerProperties[JsonKey.BING_MAPS_KEY],//"AvOW5Fz4QTsubTTdmaVnseeZnAQ0JYwbx_6zdMdgHk6iF-pnoTE7vojUFJ1kXFTP",
            center: new Microsoft.Maps.Location(fromCoordinates[0], fromCoordinates[1]),
            mapTypeId: Microsoft.Maps.MapTypeId.road,
            zoom: 5
        };
        var map;

        Microsoft.Maps.loadModule('Microsoft.Maps.Map', { callback: initMap });

        function initMap() {
            var divMap = document.createElement('div');
            divMap.id = "mapDiv";

            divMap.style.position = "absolute";
            divMap.style.top = 0;
            divMap.style.left = 0;
            divMap.style.zIndex = 9999999999;
            divMap.style.width = "100%";
            divMap.style.height = "100%";
            if (WinContainer.isPhone()) {
                WinJS.Application.onbackclick = function () {
                    try {
                        var elem = document.getElementById('mapDiv');
                        elem.parentNode.removeChild(elem);
                        document.body.style.overflowX = "visible";
                        document.body.style.overflowY = "visible";
                    } catch (e) { }
                    return true;
                }
            } else {
                var divButton = document.createElement('div');
                var button = document.createElement('input');
                button.type = "submit";
                button.value = "X";
                button.style.position = "absolute";
                button.style.top = "5px";
                button.style.right = "5px";
                button.style.border = "2px solid #c2c2c2";
                button.style.padding = "1px 5px";
                button.style.backgroundColor = "#605F61";
                button.style.zIndex = 99999999999;
                button.style.height = "38px";
                button.style.width = "38px";
                button.style.borderRadius = "150px";
                button.addEventListener("click", function () {
                    var elem = document.getElementById('mapDiv');
                    elem.parentNode.removeChild(elem);
                    document.body.style.overflowX = "visible";
                    document.body.style.overflowY = "visible";

                }, false);

                divButton.appendChild(button);
                divMap.appendChild(divButton);
            }
            map = new Microsoft.Maps.Map(divMap, mapOptions);
            document.body.appendChild(divMap);

            document.getElementById("mapDiv").focus();
            document.body.style.overflowX = "hidden";
            document.body.style.overflowY = "hidden";
            var loc1, loc2;
            var map, searchManager, mapdirectionsManager;
            loc1 = new Microsoft.Maps.Location(fromCoordinates[0], fromCoordinates[1]);
            loc2 = new Microsoft.Maps.Location(toCoordinates[0], toCoordinates[1]);

            var arrLocations = [];
            arrLocations.push(loc1);
            arrLocations.push(loc2);

            var rect = Microsoft.Maps.LocationRect.fromLocations(arrLocations);
            map.entities.clear();
            mapdirectionsManager = new Microsoft.Maps.Directions.DirectionsManager(map);
            if (mapdirectionsManager) {
                mapdirectionsManager.resetDirections();
            }
            if (mapdirectionsManager) {

                mapdirectionsManager.setRequestOptions({ routeMode: Microsoft.Maps.Directions.RouteMode.driving });
                var initialLocation = new Microsoft.Maps.Directions.Waypoint({ location: loc1 });
                var finalLocation = new Microsoft.Maps.Directions.Waypoint({ location: loc2 });
                mapdirectionsManager.addWaypoint(initialLocation);
                mapdirectionsManager.addWaypoint(finalLocation);

                mapdirectionsManager.calculateDirections();
            }

            setTimeout((function () {
                map.setView({ bounds: rect });
            }).bind(this), 1500);

        }
        WinContainer.successCallback(res);
    }
    catch (e) {
        WinContainer.failureCallback(res.id, "APZ-CNT-107", e.message);
        WinContainer.Log.error(e.message);
    }
};




(function () {
    var pos;
    var infoboxLayer;
    var infoboxLayerfixed;
    var searchPins;
    var radius;
    var searchLocations;
    var pinLayer1;
    var map;
    var infoBoxPresent;

    WinContainer.plugin.locationSelector = function (json) {
        var res = {};
        res.id = json.id;
        WinContainer.removeAlertUI();
        //if (!WinContainer.plugin.validateareaselector(json))
        //    return;

        searchLocations = json.nearbyplaces;
        radius = parseInt(json.radius);
        try {
            searchPins = new Microsoft.Maps.EntityCollection();
            infoboxLayer = new Microsoft.Maps.EntityCollection();
            infoboxLayerfixed = new Microsoft.Maps.EntityCollection();
            pinLayer1 = new Microsoft.Maps.EntityCollection();

            var positionLocator = new Windows.Devices.Geolocation.Geolocator();
            positionLocator.desiredAccuracy = Windows.Devices.Geolocation.PositionAccuracy.default;
            positionLocator.desiredAccuracyInMeters = 50;

            var mapOptions;

            var latitude = "";
            var longitude = "";

            positionLocator.getGeopositionAsync(12000, 10000000000).then(posSuccessHandler, posErrorHandler);

            function posSuccessHandler(evt) {
                latitude = evt.coordinate.latitude;
                longitude = evt.coordinate.longitude;
                mapSelectEntry();
            }
            function posErrorHandler(evt) {
                alert("Failed to detect current location");
                WinContainer.Log.error("Failed to detect current location"+evt.description);
                mapSelectEntry();
            }
            function mapSelectEntry() {
                mapOptions = {
                    credentials: WinContainer.Settings.containerProperties[JsonKey.BING_MAPS_KEY],
                    mapTypeId: Microsoft.Maps.MapTypeId.road,

                };
                if (latitude != "" || longitude != "")
                    mapOptions.center = new Microsoft.Maps.Location(latitude, longitude);
                Microsoft.Maps.loadModule('Microsoft.Maps.Map', {
                    callback: initMap
                });
            }
            function initMap() {
                var divMap = document.createElement('div');
                divMap.id = "mapDiv";
                divMap.style.position = "absolute";
                divMap.style.top = 0;
                divMap.style.left = 0;
                divMap.style.zIndex = 9999999999;
                divMap.style.width = "100%";
                divMap.style.height = "100%";
                if (WinContainer.isPhone()) {
                    WinJS.Application.onbackclick = function () {
                        try {
                            var elem = document.getElementById('mapDiv');
                            elem.parentNode.removeChild(elem);
                            document.body.style.overflowX = "visible";
                            document.body.style.overflowY = "visible";
                        } catch (e) { }
                        return true;
                    }
                } else {
                    var divButton = document.createElement('div');
                    var button = document.createElement('input');
                    button.type = "submit";
                    button.value = "X";
                    button.style.position = "absolute";
                    button.style.top = "5px";
                    button.style.right = "5px";
                    button.style.border = "2px solid #c2c2c2";
                    button.style.padding = "1px 5px";
                    button.style.backgroundColor = "#605F61";
                    button.style.zIndex = 99999999999;
                    button.style.height = "38px";
                    button.style.width = "38px";
                    button.style.borderRadius = "150px";
                    button.addEventListener("click", function () {
                        var elem = document.getElementById('mapDiv');
                        elem.parentNode.removeChild(elem);
                        document.body.style.overflowX = "visible";
                        document.body.style.overflowY = "visible";

                    }, false);

                    divButton.appendChild(button);
                    divMap.appendChild(divButton);
                }
                document.body.appendChild(divMap);

                document.getElementById("mapDiv").focus();
                document.body.style.overflowX = "hidden";
                document.body.style.overflowY = "hidden";

                map = new Microsoft.Maps.Map(divMap, mapOptions);
                var rect1 = getViewRadius(latitude, longitude, radius);
                var firstPin = new Microsoft.Maps.Pushpin({
                    latitude: latitude,
                    longitude: longitude
                });
                searchPins.push(firstPin);
                map.entities.push(searchPins);
                Microsoft.Maps.Events.addHandler(firstPin, 'click', showCurrentLocation);

                setTimeout((function () {
                    map.setView({ bounds: rect1 });
                }).bind(this), 1500);
                setTargetPoints(searchLocations, latitude, longitude, radius);
                Microsoft.Maps.Events.addHandler(map, 'click', getLocation);
                WinContainer.successCallback(res);
            }

        }
        catch (e) {
            WinContainer.failureCallback(res.id, "APZ-CNT-082", e.message);
            WinContainer.Log.error(e.description);
        }
    }
    function showCurrentLocation(location) {
        infoboxLayerfixed.clear();
        if (location.targetType == 'pushpin') {
            findLocation(location);
        }
    }
    function findLocation(location) {

        var lat = location.target._location.latitude;
        var long = location.target._location.longitude;
        WinJS.xhr({
            type: "get",
            url: "http://dev.virtualearth.net/REST/v1/Locations/" + lat + "," + long + "?key=" + WinContainer.Settings.containerProperties[JsonKey.BING_MAPS_KEY]//AvOW5Fz4QTsubTTdmaVnseeZnAQ0JYwbx_6zdMdgHk6iF-pnoTE7vojUFJ1kXFTP"
        }).then(function (result) {
            var serverResponse = result.response;
            var addr = JSON.parse(serverResponse);
            var address = addr.resourceSets[0].resources[0].name;
            showInfoboxCurrent(location.target, address);
        });
    }
    function getLocation(location) {

        if (location.targetType == 'map') {
            if (location.originalEvent.target.className == "infobox-close") {
                infoboxLayer.clear();
                infoboxLayerfixed.clear();
                return;
            }
            pinLayer1.clear();
            infoboxLayer.clear();
            infoboxLayerfixed.clear();
            searchPins.clear();

            var mapCenter = map.getCenter();
            pos = map.tryPixelToLocation(new Microsoft.Maps.Point(location.pageX, location.pageY),
                Microsoft.Maps.PixelReference.control);

            var rect = getViewRadius(pos.latitude, pos.longitude, radius);
            map.setView({
                bounds: rect
            });

            var pin = new Microsoft.Maps.Pushpin(new Microsoft.Maps.Location(pos.latitude, pos.longitude), {
                width: 36,
                height: 56,
            });
            Microsoft.Maps.Events.addHandler(pin, 'click', showCurrentLocation);
            searchPins.push(pin);
            map.entities.push(searchPins);
            setTargetPoints(searchLocations, pos.latitude, pos.longitude, radius);

        }

    };
    function showInfoboxCurrent(location, address) {
        infoboxLayer.clear();
        if (infoBoxPresent == true) {
            infoboxLayer.clear();
            infoBoxPresent = false;
        }
        var infoboxOptions = {
            width: 170,
            height: 80,
            showCloseButton: true,
            zIndex: 10,
            offset: new Microsoft.Maps.Point(-5, 35),
            showPointer: true,
            description: JSON.stringify(address),
            visible: true

        };
        infoboxPresent = true;
        var defInfoboxCurrent = new Microsoft.Maps.Infobox(location.getLocation(), infoboxOptions);
        infoboxLayer.push(defInfoboxCurrent);
        map.entities.push(infoboxLayer);

    }
    function setTargetPoints(searchLocations, latitude, longitude, radius) {
        var targetLatitude = new Array;
        var targetLongitude = new Array;
        var desc = new Array;
        var name = new Array;
        var mapCenter = map.getCenter();
        var locx;
        var targetPin = new Array;
        map.entities.push(pinLayer1);
        var arrLocations1 = [];

        for (var i = 0; i < searchLocations.length; i++) {

            targetLatitude[i] = searchLocations[i].locationLatitude;
            targetLongitude[i] = searchLocations[i].locationLongitude;
            desc[i] = searchLocations[i].locationDescription;
            name[i] = searchLocations[i].locationName;
        }

        for (var i = 0; i < searchLocations.length; i++) {
            var distance = getDistanceBetweenPoints(latitude, longitude, targetLatitude[i], targetLongitude[i]);
            if (distance <= radius) {
                locx = new Microsoft.Maps.Location(targetLatitude[i], targetLongitude[i]);
                targetPin[i] = new Microsoft.Maps.Pushpin(locx, {
                    icon: "ms-appx:///images/green.png",
                    width: 36,
                    height: 56
                });
                targetPin[i].name = name[i];
                targetPin[i].desc = desc[i];
                Microsoft.Maps.Events.addHandler(targetPin[i], 'click', showCurrentLocation);
                pinLayer1.push(targetPin[i]);
            }
        }

    }
    function getViewRadius(latitude, longitude, radius) {
        var north = -90.0;
        var west = 180.0;
        var south = 90.0;
        var east = -180.0;
        var lat = toRadian(latitude); // radians
        var lng = toRadian(longitude); // radians
        var d = radius / (6378137); //earth's mean radius in meters 
        for (var i = 0; i <= 270; i = i + 90) {
            var brng = toRadian(i);
            var latRadians = Math.asin((Math.sin(lat) * Math.cos(d)) + (Math.cos(lat) * Math.sin(d) * Math.cos(brng)));
            var lngRadians = lng
                                + Math.atan2(
                                    Math.sin(brng) * Math.sin(d) * Math.cos(lat),
                                    Math.cos(d) - (Math.sin(lat) * Math.sin(latRadians)));
            var latDegree = toDegrees(latRadians);
            var lngDegree = toDegrees(lngRadians);

            if (latDegree > north)
                north = latDegree;
            if (latDegree < south)
                south = latDegree;
            if (lngDegree > east)
                east = lngDegree;
            if (lngDegree < west)
                west = lngDegree;
        }
        var rect = Microsoft.Maps.LocationRect.fromEdges(north, west, south, east);
        return rect;
    }
    function toRadian(degrees) {
        return degrees * (Math.PI / 180);
    }
    function toDegrees(radians) {
        return radians * (180 / Math.PI);
    }
    function getDistanceBetweenPoints(p1Lat, p1Long, p2Lat, p2Long) {
        p2Lat = parseFloat(p2Lat);
        p2Long = parseFloat(p2Long);
        var R = 6378137; // Earth’s mean radius in meter
        var dLat = toRadian(p2Lat - p1Lat);
        var dLong = toRadian(p2Long - p1Long);
        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(toRadian(p1Lat)) * Math.cos(toRadian(p2Long)) *
            Math.sin(dLong / 2) * Math.sin(dLong / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var d = (R * c);
        return d; // returns the distance in meter
    }
})();