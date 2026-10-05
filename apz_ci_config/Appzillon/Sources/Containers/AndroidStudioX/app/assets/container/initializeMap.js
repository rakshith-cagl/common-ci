/*takes json array containing location details*/
function initializeMaps(markers) {
	var myOptions = {
		zoom : 12,
		mapTypeId : google.maps.MapTypeId.ROADMAP,
		navigationControlOptions : {
			style : google.maps.NavigationControlStyle.SMALL
		},
		mapTypeControl : false
	};
	var map = new google.maps.Map(document.getElementById("map_canvas"),myOptions);
	var infowindow = new google.maps.InfoWindow();
	var marker, i;
	var bounds = new google.maps.LatLngBounds();
	for (i = 0; i < markers.length; i++) {
		var pos = new google.maps.LatLng(markers[i].locationLatitude,markers[i].locationLongitude);
		bounds.extend(pos);
		if (markers.length - 1 == i) {
			marker = new google.maps.Marker({
				position : pos,
				map : map,
				icon : '../scripts/container/blue-dot.png'
			});
		} else {
			marker = new google.maps.Marker({
				position : pos,
				map : map,
				icon : '../scripts/container/red-dot.png'
			});
		}
		google.maps.event.addListener(marker, 'click', (function(marker, i) {
			return function() {
				infowindow.setContent(markers[i].locationName + ","	+ markers[i].locationDescription);
				infowindow.open(map, marker);
			}
		})(marker, i));

	}
	map.fitBounds(bounds);
}

function showPosition(markerobj) {
	var markers = JSON.parse(markerobj);
	initializeMaps(markers.markerInfo);
}

//Driving Direction

var directionsDisplay;
var directionsService;
var rendererOptions = {
	draggable : true
};
var haight;
var oceanBeach;
var initializmap = {};

initializmap.route = function(routeobj) {
        directionsService = new google.maps.DirectionsService();
	directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);
	var mapOptions = {
		zoom : 10,
		center : haight
	};
	var map = new google.maps.Map(document.getElementById("map_canvas"),mapOptions);
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
		origin : haight,
		destination : oceanBeach,
		travelMode : google.maps.TravelMode.DRIVING
	};
	directionsService.route(request, function(response, status) {
		if (status == google.maps.DirectionsStatus.OK) {			
			directionsDisplay.setDirections(response);
		}else{
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
		center : center,
		zoom : 15,
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

function showNearByLocations(loc){
	
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
	} else {
	}
}
//Abhishek 27 Jan 2015 : showing InfoWindow for every marker START
function ceateMarkers(Latlng) {	
	var infowindow = new google.maps.InfoWindow();
	var marks = new google.maps.Marker({
		position : Latlng,
		map : map
	});
	markers.push(marks);
	
	var markerNameValue = "Retriving details, Please wait !!!";
	
	geocoder.geocode({
		'latLng' : Latlng
	}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			if (results[1]) {
				markerNameValue = results[1].formatted_address;				
			}
		}else{
			markerNameValue = "Lat long : "+Latlng;
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
		infowindow.setContent(""+markerNameValue);
		infowindow.open(map, this);
	}));
}

function createCurrentMarker(currentPos) {
	var currentMarkerValue ="Retriving details, Please wait !!!";
	geocoder.geocode({
		'latLng' : currentPos
	}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			if (results[1]) {
				currentMarkerValue = results[1].formatted_address;
			}else{ }
		}else{
			currentMarkerValue = "Lat long : "+currentPos;
		}
	});
	var infowindow = new google.maps.InfoWindow();
	marker = new google.maps.Marker({
		map : map,
//		zoom : 12,
		animation: google.maps.Animation.DROP,
		position : currentPos
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
		infowindow.setContent(""+currentMarkerValue);
		infowindow.open(map, this);
	});
}
//Abhishek 27 Jan 2015 : showing InfoWindow for every marker END

function drawCircle(centeralPosition) {
	var populate = {
		//strokeColor: '#0000FF',
		//strokeOpacity: 0.8,
		strokeWeight : 1,
		// fillColor: '#0000FF',
		//fillOpacity: 0.35,
		map : map,
		center : centeralPosition,
		radius : parseInt(radius_circle)
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
			var nearby = new google.maps.LatLng(markers[i].getPosition().lat(),	markers[i].getPosition().lng());
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
	var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1)
			* Math.cos(radlat2) * Math.cos(radtheta);
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
