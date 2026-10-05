/*takes json array containing location details*/
var locationInfo = {};
var posMarker = [];
var marker;
var map;
/*******Creating location button********/
function CenterControl(controlDiv, map) {
    // Create a div to hold the control.
    controlDiv.style.right = '11px';
    // Set CSS for the control border
    var controlUI = document.createElement('div');
    controlUI.style.backgroundColor = '#fff';
    controlUI.style.border = '2px solid #fff';
    controlUI.style.cursor = 'pointer';
    controlUI.style.height = '28px';
    controlUI.style.width = '27px';
    controlUI.title = 'Click to recenter the map';
    controlDiv.appendChild(controlUI);

    // Set CSS for the control interior
    var controlText = document.createElement('div');
    controlText.style.cursor = 'pointer';
    controlText.style.backgroundImage = "url(https://maps.gstatic.com/tactile/mylocation/mylocation-sprite-cookieless-v2-1x.png)";
    controlText.style.height = '18px';
    controlText.style.width = '18px';
    controlText.style.top = '7px';
    controlText.style.left = '5px';
    controlText.style.position = 'absolute';
    controlText.title = 'Click to recenter the map';
    //controlText.setAttribute("onclick","currLoc();");
    controlUI.appendChild(controlText);
    //alert("createBtn called");
}

function initializeMaps(markers) {

    var myOptions = {
        zoom: 12,
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        navigationControlOptions: {
            style: google.maps.NavigationControlStyle.SMALL
        },
        streetViewControl: false,
        mapTypeControl: false,
        myLocation: true,
        myLocationButton: false
    };
    map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

    var infowindow = new google.maps.InfoWindow();
    var i;

    var bounds = new google.maps.LatLngBounds();
    /**************Overlay on map to notify the add new marker action******************/
    var overlayDiv = document.createElement('overlay');
    overlayDiv.style.display = 'block';
    overlayDiv.style.width = '100%';
    overlayDiv.style.height = '100%';
    overlayDiv.style.backgroundColor = 'rgba(0,0,0,0.5)';
    overlayDiv.style.cursor = 'pointer';
    overlayDiv.style.zIndex = '2';
    var myTextDiv = document.createElement('h2');
    myTextDiv.innerHTML = 'Double tap on the map to capture the location';
    myTextDiv.style.top = "50%";
    myTextDiv.style.left = "50%";
    myTextDiv.style.color = "ghostwhite";
    myTextDiv.style.transform = "translate(-50%,-100%)";
    myTextDiv.style.msTransform = "translate(-50%,-50%)";
    myTextDiv.style.textAlign = "center";
    myTextDiv.style.position = "absolute";

    overlayDiv.appendChild(myTextDiv);
    map.controls[google.maps.ControlPosition.CENTER].push(overlayDiv);
    var centerControlDiv = document.createElement('div');
    centerControlDiv.setAttribute("id", "locBtn");
    centerControlDiv.style.paddingRight = "9px";
    CenterControl(centerControlDiv, map);

    centerControlDiv.index = 1;
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(centerControlDiv);
    overlayDiv.addEventListener('click', function() {
        overlayDiv.style.display = 'none';
    });

    for (i = 0; i < markers.length; i++) {

        var pos = new google.maps.LatLng(markers[i].locationLatitude, markers[i].locationLongitude);
        //new google.maps.LatLng("12.939255849502914","77.62524754797596");
        //new google.maps.LatLng(markers[i].locationLatitude, markers[i].locationLongitude);
        bounds.extend(pos);
        if (markers.length - 1 == i) {
            marker = new google.maps.Marker({
                position: pos,
                map: map,
                icon: '../scripts/container/blue-dot.png'
            });
        } else {
            marker = new google.maps.Marker({
                position: pos,
                map: map,
                icon: '../scripts/container/red-dot.png'
            });
        }
        posMarker.push(marker); //markers object used to remove marker
        google.maps.event.addListener(marker, 'click', function(event) {
            return function() {
                infowindow.setContent(event.latLng.lat() + "," + event.latLng.lng());
                infowindow.open(map, marker);
            }
        });
        /*************Recenter map to current location onclick of Loc button**************/
                centerControlDiv.addEventListener('click', function() {
                    markerLocation.getCurrLoc();
                });
        // Create new marker on double click event on the map
        var newMarker = [];


        google.maps.event.addListener(map, 'dblclick', function(event) {
            for (var i = 0; i < newMarker.length; i++) {
                newMarker[i].setMap(null);
            }
            marker = new google.maps.Marker({
                position: event.latLng,
                map: map,
                draggable: true,
                title: event.latLng.lat() + ', ' + event.latLng.lng()
            });
            locationInfo = {
                "latitude": event.latLng.lat(),
                "longitude": event.latLng.lng()
            }
            infowindow.setContent('<p>Latitude: ' + event.latLng.lat() + '</p>' +
                '<p>Longitude: ' + event.latLng.lng() + '</p>' +

                '<button onclick="return fetchLocation();">Get Location</button>');

            infowindow.open(map, marker);

            newMarker.push(marker);
            google.maps.event.addListener(marker, 'dragend', function(event) {

                locationInfo = {
                    "latitude": event.latLng.lat(),
                    "longitude": event.latLng.lng()
                }
                infowindow.setContent('<p>Latitude: ' + event.latLng.lat() + '</p>' +
                    '<p>Longitude: ' + event.latLng.lng() + '</p>' +
                    '<button onclick="return fetchLocation();">Get Location</button>');

                infowindow.open(map, marker);

            });

        });

        google.maps.event.addListener(marker, 'dragend', function(event) {

            locationInfo = {
                "latitude": event.latLng.lat(),
                "longitude": event.latLng.lng()
            }
            infowindow.setContent(event.latLng.lat() + "," + event.latLng.lng());
            infowindow.open(map, marker);

        });

    }

    map.fitBounds(bounds);
    // set zoom here
    zoomChangeBoundsListener =
        google.maps.event.addListenerOnce(map, 'bounds_changed', function(event) {
            if (this.getZoom()) {
                this.setZoom(16); // set zoom here
            }
        });

}
/***********native call to get current location*************/
function getCurrentPosition(loc) {
   // alert(JSON.stringify(loc));
    for (var i = 0; i < posMarker.length; i++) {
        posMarker[i].setMap(null);
    }
    var pos1 = new google.maps.LatLng(loc.locationLatitude, loc.locationLongitude);
    marker = new google.maps.Marker({
        position: pos1,
        map: map,
        icon: '../scripts/container/blue-dot.png'
    });
    posMarker.push(marker);
    map.setCenter(pos1);
}

function showPosition(markerobj) {
    var markers = JSON.parse(markerobj);
    initializeMaps(markers.markerInfo);
}

//Driving Direction

var directionsDisplay;
var directionsService;
var rendererOptions = {
    draggable: true
};
var haight;
var oceanBeach;
var initializmap = {};

initializmap.route = function(routeobj) {
    directionsService = new google.maps.DirectionsService();
    directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);
    var mapOptions = {
        zoom: 10,
        center: haight
    };
    var map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
    directionsDisplay.setMap(map);
    calcRoute(routeobj);
}

function calcRoute(routeobj) {
    var robj = JSON.parse(routeobj);
    var startlatlong = (robj.fromLocation).split(",");
    var endlatlong = (robj.toLocation).split(",");
    haight = new google.maps.LatLng(startlatlong[0], startlatlong[1]);
    oceanBeach = new google.maps.LatLng(endlatlong[0], endlatlong[1]);
    var request = {
        origin: haight,
        destination: oceanBeach,
        travelMode: google.maps.TravelMode.DRIVING
    };
    directionsService.route(request, function(response, status) {
        if (status == google.maps.DirectionsStatus.OK) {
            directionsDisplay.setDirections(response);
        } else {
            alert("No Route found");
            //			 var jsonobj = {
            //					    "locationLatitude":startlatlong[0],
            //						"locationLongitude":startlatlong[1]
            //					  };
            //			initializeMaps(jsonobj);
        }
    });
}

//Selection

var markers = [];
var radius_circle;
var circle;
var cityCircle;
var placeNameval;
var marker;
var srchLocations;

initializmap.select = function(selobj, cuurjson) {
    srchLocations = JSON.parse(selobj);
    geocoder = new google.maps.Geocoder();
    var curobj = JSON.parse(cuurjson);
    sellat = curobj.locationlatitude;
    sellong = curobj.locationlongitude;
    var center = new google.maps.LatLng(sellat, sellong);

    radius_circle = srchLocations.radius; // 30km

    // draw map
    var mapOptions = {
        center: center,
        zoom: 15,
    };

    map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
    //Abhishek 13 March 2015 Added to show for the present location also
    //	var myMarker=new google.maps.Marker({
    //	  position:center,
    //	  });
    //
    //myMarker.setMap(map);
    //
    //circle = drawCircle(center);
    //circle.center = center;
    //cityCircle.setMap(null);
    //
    //map.fitBounds(circle.getBounds());

    showNearByLocations(center);

    google.maps.event.addListener(map, 'click', function(e) {
        clickPos = new google.maps.LatLng(e.latLng.lat(), e.latLng.lng());
        //		clearMarkers();
        //		createCurrentMarker(clickPos);
        //		checkNearByPlaces();

        showNearByLocations(clickPos);

    });

}

function showNearByLocations(loc) {

    clearMarkers();
    createCurrentMarker(loc);
    checkNearByPlaces();
}

function clearMarkers() {
    setAllMap(null);

}

function setAllMap(map) {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(map);
    }
    markers = [];
}

function checkNearByPlaces() {
        //	circle = drawCircle();
        //	cityCircle.setMap(null);
        var flag;
        for (var i = 0; i < srchLocations.nearbyplaces.length; i++) {
            var searchLat = parseFloat(srchLocations.nearbyplaces[i].locationLatitude);
            var searchLong = parseFloat(srchLocations.nearbyplaces[i].locationLongitude);
            var Latlng = new google.maps.LatLng(searchLat, searchLong);

            ceateMarkers(Latlng);
            flag = 1;
        }

        if (flag == 1) {
            call();
        } else {}
    }
    //Abhishek 27 Jan 2015 : showing InfoWindow for every marker START
function ceateMarkers(Latlng) {
    var infowindow = new google.maps.InfoWindow();
    var marks = new google.maps.Marker({
        position: Latlng,
        map: map
    });
    markers.push(marks);

    var markerNameValue = "Retriving details, Please wait !!!";

    geocoder.geocode({
        'latLng': Latlng
    }, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            if (results[1]) {
                markerNameValue = results[1].formatted_address;
            }
        } else {
            markerNameValue = "Lat long : " + Latlng;
        }
    });

    google.maps.event.addListener(marks, 'click', (function() {
        //		var placeNameval1;
        //		var Latlng1 = new google.maps.LatLng(this.getPosition().k, this.getPosition().B);
        //
        //		geocoder.geocode({
        //			'latLng' : Latlng1
        //		}, function(results, status) {
        //			if (status == google.maps.GeocoderStatus.OK) {
        //				if (results[1]) {
        //					placeNameval1 = results[1].formatted_address;
        //					infowindow.setContent(placeNameval1);
        //				}
        //			}
        //		});
        infowindow.setContent("" + markerNameValue);
        infowindow.open(map, this);
    }));
}

function createCurrentMarker(currentPos) {
        var currentMarkerValue = "Retriving details, Please wait !!!";
        geocoder.geocode({
            'latLng': currentPos
        }, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                if (results[1]) {
                    currentMarkerValue = results[1].formatted_address;
                } else {}
            } else {
                currentMarkerValue = "Lat long : " + currentPos;
            }
        });
        var infowindow = new google.maps.InfoWindow();
        marker = new google.maps.Marker({
            map: map,
            //		zoom : 12,
            animation: google.maps.Animation.DROP,
            position: currentPos
        });

        markers.push(marker);
        //Abhishek Bug ID 4262 Start
        circle = drawCircle(currentPos);
        cityCircle.setMap(null);

        map.fitBounds(circle.getBounds());

        //	map.setZoom(12); // Back to default zoom
        //	map.panTo(currentPos); // Pan map to that position

        //Abhishek Bug ID 4262 END

        google.maps.event.addListener(marker, 'click', function() {
            infowindow.setContent("" + currentMarkerValue);
            infowindow.open(map, this);
        });
    }
    //Abhishek 27 Jan 2015 : showing InfoWindow for every marker END

function drawCircle(centeralPosition) {
    var populate = {
        //strokeColor: '#0000FF',
        //strokeOpacity: 0.8,
        strokeWeight: 1,
        // fillColor: '#0000FF',
        //fillOpacity: 0.35,
        map: map,
        center: centeralPosition,
        radius: parseInt(radius_circle)
    };
    cityCircle = new google.maps.Circle(populate);
    return cityCircle;
}

//Abhishek 27 Jan 2015 : showing InfoWindow for every marker START
function call() {

        for (var i = 0; i < srchLocations.nearbyplaces.length + 1; i++) {

            var distance = calculateDistance(markers[i].getPosition().lat(),
                markers[i].getPosition().lng(), circle.getCenter().lat(),
                circle.getCenter().lng(), "K");

            if (distance * 1000 < radius_circle) {
                var nearby = new google.maps.LatLng(markers[i].getPosition().lat(), markers[i].getPosition().lng());
                markers[i].setIcon('http://maps.gstatic.com/mapfiles/icon_green.png');
                markers[0].setIcon('../scripts/container/red-dot.png');

            } else {
                markers[i].setMap(null);

            }
        }
    }
    //Abhishek 27 Jan 2015 : showing InfoWindow for every marker END

function calculateDistance(lat1, lon1, lat2, lon2, unit) {

    var radlat1 = Math.PI * lat1 / 180;
    var radlat2 = Math.PI * lat2 / 180;
    var radlon1 = Math.PI * lon1 / 180;
    var radlon2 = Math.PI * lon2 / 180;
    var theta = lon1 - lon2;
    var radtheta = Math.PI * theta / 180;
    var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
    dist = Math.acos(dist);
    dist = dist * 180 / Math.PI;
    dist = dist * 60 * 1.1515;
    if (unit == "K") {
        dist = dist * 1.609344;
    }
    if (unit == "N") {
        dist = dist * 0.8684;
    }
    return dist;
}

function fetchLocation() {
    markerLocation.fetchloc(JSON.stringify(locationInfo));
}
