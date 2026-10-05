var mailjsonObj;

var sqlId = 0;

var appzillonappsuccess;

var appzillonappfailure;

var push_message;

var notification_param;

appzillon.plugin.preqinterfaceid;
appzillon.plugin.prespinterfaceid;
appzillon.plugin.preqstr;
appzillon.plugin.ppaintresp;
appzillon.plugin.pcallid;
appzillon.plugin.pasync;
appzillon.plugin.pcallback;

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ START  Plugins calls (alphabetical order )@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

/*...................... A ............................*/

/*Starts accelerometer*/
appzillon.plugin.accelerometerStart = function(jsonObj) {
	
	if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166",null);
	} else{
		Android.startAccelerometer(JSON.stringify(jsonObj));
	}	
}

/*Stops accelerometer*/
appzillon.plugin.accelerometerStop = function(jsonObj) {
	if (typeof jsonObj == "object") {
		if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
			appzillon.util.displayMessage("APZ-CNT-166",null);
		} else if (jsonObj.failureCallback == undefined	|| jsonObj.failureCallback.trim() == "") {
			appzillon.util.displayMessage("APZ-CNT-167", null);
		} else {
			Android.stopAccelerometer(JSON.stringify(jsonObj));
		}
	} else {
		appzillon.util.displayMessage("APZ-CNT-077", null);
	}
}

/*App Idle time out*/
appzillon.plugin.startTimer = function(jsonObj) {
	Android.appIdleTimeout(JSON.stringify(jsonObj)); 
}

/*Audio Plugin*/
appzillon.plugin.audio = function(jsonObj) {
	if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166",null);
	}else{
		Android.startAudioPlugin(JSON.stringify(jsonObj));
	}
	
}

/*Augmented Reality*/

appzillon.plugin.startAugReality = function(jsonObj){
	Android.startAugReality(JSON.stringify(jsonObj));
}

appzillon.plugin.reloadAugReality = function(jsonObj){
	var pjson = {
		"Places" : lbodyobj
	};
	Android.reloadAugReality(JSON.stringify(pjson));
}

/*App Utility Fun*/

/*Returns  debug status(enable or disable)*/
//appzillon.plugin.debugReqd = function() {
//	var debugReqdVal = appzillon.plugin.retrieve('DEBUGREQUIRED');
//	if (debugReqdVal == null || debugReqdVal == "") {
//		debugReqdVal = "N";
//	}
//	return debugReqdVal;
//}

/*Returns  auditlog status(enable or disable)*/
//appzillon.plugin.auditReqd = function() {
//	var auditReqdVal = appzillon.plugin.retrieve('AUDITLOGREQUIRED');
//	if (auditReqdVal == null || auditReqdVal == "") {
//		auditReqdVal = "N";
//	}
//	return auditReqdVal;
//}

/*Returns appId(application package name)*/
//appzillon.plugin.appId = function() {
//	var appid = appzillon.plugin.retrieve('APPID');
//	return appid;
//}

/*Returns IMEI(DEVICEID) no associated with the device*/
//appzillon.plugin.deviceId = function() {
//deviceidchanges starts
//	var devId = "ANDROID"; 
//	var devId = appzillon.plugin.retrieve('DEVICEID');
//deviceidchanges ends
//	return devId;
//}

//App Version
appzillon.plugin.getAppversion = function() {
	return Android.getAppVersion();
}

/*...................... B ............................*/

/*Starts Barcode plugin*/
appzillon.plugin.barcode = function(jsonObj) {
	if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else if (jsonObj.failureCallback == undefined || jsonObj.failureCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-167", null);
	} else {
		Android.barcodeScanner(JSON.stringify(jsonObj));
	}

}

/*BEACON*/
appzillon.plugin.startBeacon = function(jsonObj){
	Android.beaconMonitering("START",JSON.stringify(jsonObj));
}
appzillon.plugin.stopBeacon = function(jsonObj){
	Android.beaconMonitering("STOP",JSON.stringify(jsonObj));
}
/*Bio-meteric Authentication plugin*/
appzillon.plugin.biometricAuth = function(jsonObj){
	appzillon.util.displayMessage("APZ-CNT-142", null);
}

/*...................... C ............................*/

//*****Calender*******
/*For Calendar Plugin(create and edit)*/
appzillon.plugin.calendar = function(jsonObject) {
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
		dateformat = appzillon.user.dateformat;
		Android.calendarPlugin(JSON.stringify(jsonObject), dateformat);
	}
	
}

/*To delete calendar events*/
appzillon.plugin.deleteCalendarEvent = function(jsonObject) {
	dateformat = appzillon.user.dateformat;
	Android.calendarEventDelete(JSON.stringify(jsonObject), dateformat);
}

/*Call*/

/*Makes call to corresponding phNumber */
appzillon.plugin.call = function(phNumber) {
	Android.makePhoneCall(phNumber);
}

/*Camera*/

/*Calls to take picture by passing jsonObject */
appzillon.plugin.camera = function(jsonObj) {
	missingField = "";
	successCallback = jsonObj.successCallback;
	failureCallback = jsonObj.failureCallback;
	action = jsonObj.action;
	if (successCallback == undefined || successCallback == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);

	} else if (failureCallback == undefined || failureCallback == "") {
		appzillon.util.displayMessage("APZ-CNT-167", null);

	} else if (action == undefined || action == "") {
		appzillon.util.displayMessage("APZ-CNT-168", null);

	} else if (action != undefined) {
		if (action == "srcUrl") {
			if (jsonObj.fileName == undefined || jsonObj.fileName == "") {
				appzillon.util.displayMessage("APZ-CNT-169", null);
			} else if (jsonObj.elementId == undefined || jsonObj.elementId == "") {
				appzillon.util.displayMessage("APZ-CNT-170", null);
			} else {
				if (jsonObj.fileOverwrite == undefined) {
					jsonObj.fileOverwrite = "Y";
				}
				Android.startCameraUsingApi(JSON.stringify(jsonObj)); // Native Call
			}
		} else if (action == "save") {
			if (jsonObj.fileName == undefined || jsonObj.fileName == "") {
				appzillon.util.displayMessage("APZ-CNT-169",null);
			} else {
				if (jsonObj.fileOverwrite == undefined) {
					jsonObj.fileOverwrite = "Y";
				}
				Android.startCameraUsingApi(JSON.stringify(jsonObj)); // Native Call
			}
		} else if (action == "srcBase64") {
			if (jsonObj.elementId == undefined || jsonObj.elementId == "") {
				appzillon.util.displayMessage("APZ-CNT-170", null);
			} else {
				Android.startCameraUsingApi(JSON.stringify(jsonObj)); // Native Call
			}
		} else {
			Android.startCameraUsingApi(JSON.stringify(jsonObj)); // Native Call
		}
	} else {
		Android.startCameraUsingApi(JSON.stringify(jsonObj)); // Native Call
	}

}

//Abhishek,Bug id 5391, display srcBase64 Image START
appzillon.plugin.showBase64Image = function(jsonObj) {
	document.getElementById(jsonObj.elementId).src = "data:image/jpg;base64," + jsonObj.encodedImage;
	
}
//Abhishek,Bug id 5391, display srcBase64 Image END

//Siddu, display srcURL Image START
appzillon.plugin.displayNativeUrlImage = function(jsonObj){
	document.getElementById(jsonObj.elementId).src = jsonObj.path;
}
//Siddu, display srcURL Image START

/*Compass*/
/*Starts compass*/
appzillon.plugin.compassStart = function(jsonObj) {
	if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
		Android.startCompassListener(JSON.stringify(jsonObj));
	}
	
}
/*Stops compass*/
appzillon.plugin.compassStop = function(jsonObj) {
	Android.stopCompassListener(JSON.stringify(jsonObj));

}

/*Contacts*/
/*Add Contacts*/
appzillon.plugin.contactAdd = function(jsonObject) {
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else {
		Android.contactAddOperation(JSON.stringify(jsonObject));
	}
}

/* Delete Contacts */
appzillon.plugin.contactDelete = function(jsonObject) {
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else {
		Android.contactDeleteOperation(JSON.stringify(jsonObject));
	}
}

/* Edit Contacts */
appzillon.plugin.contactEdit = function(jsonObject) {
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else {
		Android.contactEditOperation(JSON.stringify(jsonObject));
	}
}

/* Search Contacts */
appzillon.plugin.contactSearch = function(jsonObject) {
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else {
		Android.contactSearchOperation(JSON.stringify(jsonObject));
	}
}

/*Cryptography*/

//Encrypt Data
appzillon.plugin.encryptData = function(json) {
	if (json.successCallback == undefined || json.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else {
		Android.encryptData(JSON.stringify(json));
	}
}
//Decrypt Data
appzillon.plugin.decryptData = function(json) {
	if (json.successCallback == undefined || json.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else {
		Android.decryptData(JSON.stringify(json));
	}
}

/*Encrypt File*/
appzillon.plugin.fileEncrypt = function(json) {

	 if(appzillon.plugin.validateFileEncDesc(json)){		 
		 Android.fileEncrypt(JSON.stringify(json)); 
	 }
}

/*Decrypt File*/
appzillon.plugin.fileDecrypt = function(json) {

	if(appzillon.plugin.validateFileEncDesc(json)){
		 Android.fileDecrypt(JSON.stringify(json)); 
	 }
}

/*Cusror State*/

/*Calls to start the loading spinner*/
appzillon.plugin.setCursorBusy = function() {
//Abhishek 09 September 2015, Native loader is not needed, so commented START
//	Android.startloader();
//Abhishek 09 September 2015, Native loader is not needed, so commented END
}

/*Calls to close the loading spinner*/
appzillon.plugin.setCursorNormal = function() {
//Abhishek 09 September 2015, Native loader is not needed, so commented START
//	Android.endloader();
//Abhishek 09 September 2015, Native loader is not needed, so commented END
}

/*...................... D ............................*/

/*Data Storage*/

/*Stores value in the given key*/
appzillon.plugin.store = function(key, value) {
	key = key.toString();
	localStorage.setItem(key, value);
}

/*Returns value associated with key*/
appzillon.plugin.retrieve = function(key) {
	key = key.toString();
	return localStorage.getItem(key);
}

/*DEVICE*/

/*Retrives device Information*/
appzillon.plugin.device = function(jsonObj) {
	if (typeof jsonObj == "object") {
		if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
			appzillon.util.displayMessage("APZ-CNT-166", null);
		} else if (jsonObj.failureCallback == undefined || jsonObj.failureCallback.trim() == "") {
			appzillon.util.displayMessage("APZ-CNT-167", null);
		} else {
			Android.getDeviceInformation(JSON.stringify(jsonObj));
		}
	} else {
		appzillon.util.displayMessage("APZ-CNT-077", null);
	}
}


//*****Download*******

/*for file download*/
appzillon.plugin.downloadFile = function(jsonObject) {
	//Abhishek 04 September 2015, For differentiating between without session START
	jsonObject.isSession = "Y";
	//Abhishek 04 September 2015, For differentiating between without session END
	downloadBody = {
		"appzillonBody" : {
			"appzillonFilePushServiceRequest" : {
				"appId" : "",
				"fileName" : "",
				"userId" : "",
				"screenId" : ""
			}
		}
	};
	var jsonfilepath = jsonObject.filePath;
	temp = jsonfilepath.split("/");
	var lScreenId;
	var lUserId;
	if (temp.length == 1) {
		lScreenId = temp[0];
	} else if (temp.length == 2) {
		lScreenId = temp[0];
		lUserId = temp[1];
	}
	downloadBody.appzillonBody.appzillonFilePushServiceRequest.appId = appzillon.data.appid;
//	downloadBody.appzillonBody.appzillonFilePushServiceRequest.fileName = jsonObject.fileName;
	downloadBody.appzillonBody.appzillonFilePushServiceRequest.fileName = jsonObject.filePath+"/"+jsonObject.fileName;
	downloadBody.appzillonBody.appzillonFilePushServiceRequest.userId = lUserId;
	downloadBody.appzillonBody.appzillonFilePushServiceRequest.screenId = lScreenId;
	header = appzillon.server.getHeader("appzillonFilePushService",appzillon.data.scrid);
	jsonReq = '{"appzillonHeader":' + JSON.stringify(header)
			+ ',"appzillonBody":' + JSON.stringify(downloadBody.appzillonBody)
			+ '}';
	Android.download(jsonReq, JSON.stringify(jsonObject));
}

//Abhishek 04 September 2015, For downloading without session START
appzillon.plugin.getFileFromServer = function(jsonObject) {
	jsonObject.isSession = "N";
	downloadBody = {
		"appzillonBody" : {
			"appzillonFilePushServiceWSRequest" : {
				"appId" : "",
				"fileName" : "",
				"userId" : "",
				"screenId" : ""
			}
		}
	};
	var jsonfilepath = jsonObject.filePath;
	temp = jsonfilepath.split("/");
	var lScreenId;
	var lUserId;
	if (temp.length == 1) {
		lScreenId = temp[0];
	} else if (temp.length == 2) {
		lScreenId = temp[0];
		lUserId = temp[1];
	}
	downloadBody.appzillonBody.appzillonFilePushServiceWSRequest.appId = appzillon.data.appid;
//	downloadBody.appzillonBody.appzillonFilePushServiceWSRequest.fileName = jsonObject.fileName;
//	downloadBody.appzillonBody.appzillonFilePushServiceWSRequest.relativePath = jsonObject.relativePath;
	downloadBody.appzillonBody.appzillonFilePushServiceWSRequest.fileName = jsonObject.filePath+"/"+jsonObject.fileName;
	downloadBody.appzillonBody.appzillonFilePushServiceWSRequest.userId = lUserId;
	downloadBody.appzillonBody.appzillonFilePushServiceWSRequest.screenId = lScreenId;
	header = appzillon.server.getHeader("appzillonFilePushServiceWS",appzillon.data.scrid);
	jsonReq = '{"appzillonHeader":' + JSON.stringify(header)
			+ ',"appzillonBody":' + JSON.stringify(downloadBody.appzillonBody)
			+ '}';
	Android.download(jsonReq, JSON.stringify(jsonObject));
}
//Abhishek 04 September 2015, For downloading without session END

/*...................... E ............................*/

/*Events*/
appzillon.plugin.controlEvent = function(jsonObject) {
	Android.overrideEventsDefault(JSON.stringify(jsonObject));
}


/*used for mail plugin*/
function executeCallback(functionName, arguments) {
	var errorFunction = window[functionName];
	if (typeof errorFunction !== 'function') {
		return;
	}
	errorFunction.apply(window, arguments);
}
/*CallBack*/
function mailcallback(pcallid, lifaceid, pstatus, lerrorCode, lbodyobj) {
	var lmailjson = mailjsonObj;
	if (pstatus == 'success') {
		var successString = {
			"successMessage" : "Mail Sent",
			"id" : lmailjson.id
		};
		executeCallback(lmailjson.successCallback, [ successString ]);
	} else {
		var errorString = {
			"errorCode" : "",
			"errorDescription" : "Unable to send mail",
			"id" : lmailjson.id
		};
		executeCallback(lmailjson.failureCallback, [ errorString ]);
	}
}

/*...................... F ............................*/

/*File Access*/
/*opens file browser*/
appzillon.plugin.filebrowser = function(jsonObject) {
	Android.browserPlugin(JSON.stringify(jsonObject));
}

/*File Operation*/

/*Content*/
appzillon.plugin.fileContent = function(json) {
	if (json.successCallback == undefined || json.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
	Android.fileContent(JSON.stringify(json));
	}
}

/*Create*/
appzillon.plugin.fileCreate = function(json) {
	if (json.successCallback == undefined || json.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
	Android.fileCreate(JSON.stringify(json));
	}
}

/*Delete*/
appzillon.plugin.fileDelete = function(json) {
	if (json.successCallback == undefined || json.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
	Android.fileDelete(JSON.stringify(json));
	}
}

/*Open*/
/*to open file from sandbox with given directory and filename*/
appzillon.plugin.openFile = function(json) {
	if (json.successCallback == undefined || json.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
	Android.fileOpen(JSON.stringify(json));
	}
}

/*File Upload/Download*/
/*File upload service*/
appzillon.plugin.uploadFile = function(jsonObject) {
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
	header = appzillon.server.getHeader("appzillonUploadFile",appzillon.data.scrid);
	//Check if file path is null then get value for field ID 
//Abhishek 16 September 2015, Bug id 6309 START
//	if (appzillon.util.isNull(jsonObject.filepath)) {
	if (appzillon.util.isNull(jsonObject.filePath)) {
//Abhishek 16 September 2015, Bug id 6309 END
		jsonObject.fieldID = appzillon.data.getElemValue(jsonObject.fieldID);
	} else {
//Abhishek 16 September 2015, Bug id 6309 START
//		jsonObject.fieldID = jsonObject.filepath;
		jsonObject.fieldID = jsonObject.filePath;
//Abhishek 16 September 2015, Bug id 6309 END
	}
	//Abhishek 04 September 2015, For differentiating between without session START
	jsonObject.isSession = "Y";
	//Abhishek 04 September 2015, For differentiating between without session END
	Android.uploadToServer(JSON.stringify(jsonObject), JSON.stringify(header));
	}
	
}

//Abhishek 04 September 2015, For uploading without session START
appzillon.plugin.sendFileToServer = function(jsonObject) {
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
	header = appzillon.server.getHeader("appzillonUploadFileWS",appzillon.data.scrid);
	//Check if file path is null then get value for field ID 
//Abhishek 16 September 2015, Bug id 6309 START
//	if (appzillon.util.isNull(jsonObject.filepath)) {
	if (appzillon.util.isNull(jsonObject.filePath)) {
//Abhishek 16 September 2015, Bug id 6309 END
		jsonObject.fieldID = appzillon.data.getElemValue(jsonObject.fieldID);
	} else {
//Abhishek 16 September 2015, Bug id 6309 END
//		jsonObject.fieldID = jsonObject.filepath;
		jsonObject.fieldID = jsonObject.filePath;
//Abhishek 16 September 2015, Bug id 6309 END
	}
	jsonObject.isSession = "N";
	Android.uploadToServer(JSON.stringify(jsonObject), JSON.stringify(header));
	}
}
//Abhishek 04 September 2015, For uploading without session END

/*...................... G ............................*/

/*Geo Fencing*/

appzillon.plugin.geofencing = function(json) {
	
	if(appzillon.plugin.validategeofencing(json)){
		Android.geoFencing(JSON.stringify(json));
	}
}

/*Gesture Support*/
/*Start*/
appzillon.plugin.startGesture = function(json) {
	Android.gestureSupportStart(JSON.stringify(json));
}

/*Stop*/
appzillon.plugin.stopGesture = function(json) {
	Android.gestureSupportStop(JSON.stringify(json));
}

/*......................I ............................*/

//Image
appzillon.plugin.image = function(image) {
	var themeVal = appzillon.plugin.getCurrentTheme();
	if (themeVal == null || themeVal == "") {
		themeVal = Android.getSettingsValue('THEME');
	}
	var l_image = "styles/" + themeVal + "/img/" + image;
	return l_image;
}

// Initialize any container specific variables
appzillon.plugin.init = function(){
	
}

appzillon.plugin.getSettings = function(){   // 3.2 changes
	var fileoutput = appzillon.util.getFile("appconfig/settings.json");
	var all = Android.getAllSettingsValue(fileoutput);
	appzillon.data.loadUserSettingsCB(JSON.parse(all));
}

appzillon.plugin.getDeviceInfo = function(){
	var deviceInfoObj = Android.getDeviceRunTimeInfo();
	appzillon.data.getDeviceInfoCB(JSON.parse(deviceInfoObj)); 
}


/*...................... L ............................*/

/*Locale*/

/*Returns current locale of the device*/
appzillon.plugin.currentLocale = function(jsonObj) {
	if (typeof jsonObj == "object") {
		if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
			appzillon.util.displayMessage("APZ-CNT-166", null);
		} else if (jsonObj.failureCallback == undefined || jsonObj.failureCallback.trim() == "") {
			appzillon.util.displayMessage("APZ-CNT-167", null);
		} else {
			Android.getDeviceLocale(JSON.stringify(jsonObj));
		}
	} else {
		appzillon.util.displayMessage("APZ-CNT-077", "", "");
	}
}

/*Location*/

/*Start location service to retrive device currentLocation*/
appzillon.plugin.startUpdatingLocation = function(jsonObj) {
	if (typeof jsonObj == "object") {
		if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
			appzillon.util.displayMessage("APZ-CNT-166", "", "");
		} else if (jsonObj.failureCallback == undefined || jsonObj.failureCallback.trim() == "") {
			appzillon.util.displayMessage("APZ-CNT-167", "", "");
		} else if (jsonObj.periodicity == undefined || jsonObj.periodicity == "none" || jsonObj.periodicity.trim() == "") {
			Android.startGpsLocationListener(JSON.stringify(jsonObj));
		} else if (jsonObj.periodicity == "intervalBased") {

			if (jsonObj.distanceInterval == undefined && jsonObj.timeInterval == undefined) {
				appzillon.util.displayMessage("APZ-CNT-125", "", "");
			} else {
				Android.startGpsLocationListener(JSON.stringify(jsonObj));
			}
		} else if (jsonObj.periodicity == "onChange") {
			Android.startGpsLocationListener(JSON.stringify(jsonObj));
		} else {
			appzillon.util.displayMessage("APZ-CNT-099", "", "");
		}
	} else {
		appzillon.util.displayMessage("APZ-CNT-077", "", "");
	}
}

/*Stops location service to retrive device currentLocation*/
appzillon.plugin.stopUpdatingLocation = function(jsonObj) {
	if (typeof jsonObj == "object") {
		if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
			appzillon.util.displayMessage("APZ-CNT-166", "", "");
		} else if (jsonObj.failureCallback == undefined || jsonObj.failureCallback.trim() == "") {
			appzillon.util.displayMessage("APZ-CNT-167", "", "");
		} else {
			Android.stoptGpsLocationListener(JSON.stringify(jsonObj));
		}
	} else {
		appzillon.util.displayMessage("APZ-CNT-077", "", "");
	}
}

/*LOG*/

/*Logs messages to console*/
appzillon.plugin.log = function(jsonObj) {
	Android.logRuntimeDebugMsg(JSON.stringify(jsonObj));
}

//Remote Debug
appzillon.plugin.remoteDebug = function(json) {
	var jsonstring = JSON.stringify(json);
//Abhishek 17 April 2015 Changed DEBUGREQUIRED to APPZILLONSENDLOG, check Shrish mail START
//	appzillon.plugin.store("DEBUGREQUIRED", json.debug);
//	appzillon.plugin.store("SENDLOG", json.debug);
//Abhishek 17 April 2015 Changed DEBUGREQUIRED to APPZILLONSENDLOG, check Shrish mail END
	// appzillon.plugin.notify(gRemoteDebug+'~'+jsonstring);

//Abhishek , bug id 5238, setting updated value into persitance storage START
	
	//Abhishek , Bug id 5324, START	
//	Android.setAppPersistanceValue("APPZILLONSENDLOG", json.debug);
	Android.setRemoteDebug(JSON.stringify(json));
	//Abhishek , Bug id 5324, END
//Abhishek , bug id 5238, setting updated value into persitance storage END
}
/*...................... M ............................*/

/*MAIL*/

/*Launches mail client */
appzillon.plugin.mail = function(jsonObj) {
	 if(appzillon.plugin.validatemail(jsonObj)){
		 mailjsonObj = jsonObj;
		 	var linterface = jsonObj.interfaceID;
		 	if ((linterface == "") || (linterface == "undefined") || (linterface == undefined) || (linterface == null)) {
		 		linterface = "appzillonMailRequest";
		 	}
		 	var requestBody = {
		 		"appzillonMailRequest" : {
		 			"emailid" : jsonObj.recipientMailId,
		 			"subject" : jsonObj.subject,
		 			"CC" : jsonObj.ccIdList,
		 			"body" : jsonObj.body
		 		}
		 	};
		 	if (jsonObj.internal == undefined || jsonObj.internal == "") {
		 		appzillon.util.displayMessage("APZ-CNT-082", "", "");
		 	} else if (jsonObj.internal.toUpperCase() == "Y") {
		 		appzillon.server.callServer(linterface, '', 'N', JSON.stringify(requestBody), 'N', '', false, mailcallback);
		 	} else if (jsonObj.internal.toUpperCase() == "N") {
		 		Android.launchEmail(JSON.stringify(jsonObj));
		 	}
	 }
 	
 }

/*MAPS*/

//Driving Direction
appzillon.plugin.drivingDirection = function(jsonObj) {
	
	 if(appzillon.plugin.validaterouteplugin(jsonObj)){
		  Android.routeMap(JSON.stringify(jsonObj));
	  }
}

/*Launches map */
appzillon.plugin.loadMap = function(jsonObject) {
	Android.launchMap(JSON.stringify(jsonObject));
}

//Selection Map
appzillon.plugin.locationSelector = function(jsonObj) {
	
	if(appzillon.plugin.validateareaselector(jsonObj)){
		 Android.selectArea(JSON.stringify(jsonObj)); 
	 }
}

/*Multi View*/

/*Opens multiview*/
appzillon.plugin.multiviewOpen = function(jsonObject) {
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
        if (appzillon.plugin.validateWhiteList(jsonObject.launchPage)) {
            Android.multiviewOpen(JSON.stringify(jsonObject));
        } else {
            appzillon.util.displayMessage("APZ-CNT-131", null);
        }
    }
}

/*Closes Multiview*/
appzillon.plugin.multiviewClose = function(jsonObject) {
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
	Android.multiviewClose(JSON.stringify(jsonObject));
	}
}	

/*...................... N ............................*/

/*NFC*/	

appzillon.plugin.sendNFC = function(json) {
	Android.sendNFC(JSON.stringify(json));
}

appzillon.plugin.receiveNFC = function(json) {
	Android.receiveNFC(JSON.stringify(json));
}

appzillon.plugin.stopNFC = function(json) {
	Android.stopNFC(JSON.stringify(json));
}

/*appzillon.plugin.startNFC = function(json) {
	//Android.startNFC(JSON.stringify(json));
}*/

/*Native Extensibility*/

appzillon.plugin.nativeServiceExt = function(jsonObject) {
	NSService.nativeServiceExt(JSON.stringify(jsonObject));
}

/*...................... O ............................*/

/*OTA*/

//Abhishek 29 April 2015, Commented, not used any where START
//Download OTA
//appzillon.plugin.downloadOTAFile = function(jsonObject) {
//	downloadBody = {
//		"appzillonBody" : {
//			"appzillonFilePushServiceRequest" : {
//				"appId" : appzillon.plugin.appId(),
//				"fileName" : jsonObject.fileName,
//				"userId" : appzillon.plugin.retrieve('USERID'),
//				"screenId" : appzillon.data.scrid
//			}
//		}
//	};
//	header = appzillon.server.getHeader("appzillonFilePushService",
//			appzillon.data.scrid);
//	jsonReq = '{"appzillonHeader":' + JSON.stringify(header)
//			+ ',"appzillonBody":' + JSON.stringify(downloadBody.appzillonBody)
//			+ '}';
//	Android.download(jsonReq, JSON.stringify(jsonObject));
//}
//Abhishek 29 April 2015, Commented, not used any where END

//Instructions
appzillon.plugin.getInstructions = function(jsonobj) {
	if(appzillon.plugin.validategetInstructions(jsonobj)){
		Android.appInstructions(JSON.stringify(jsonobj));
	}
	
}

//Launch App
appzillon.plugin.launchApp = function(projectname) {

	if (appzillon.plugin.validatelaunchApp(projectname)) {
		Android.loadPage(projectname);
	}

}

//Sub App Delete
appzillon.plugin.subappDelete = function(appName) {	
	if(appzillon.plugin.validatesubappDelete(appName)){
		Android.deleteSubApp(appName);
	}
	
}

//UpdradeApp
appzillon.plugin.upgradeApp = function(appName) {	
	if(appzillon.plugin.validateupgradeApp(appName)){
		Android.upgradeApp(appName);
	}
	
}

//Upgrade Required
appzillon.plugin.upgradeRequired = function() {
//	return appzillon.plugin.retrieve("UPDATEREQUEST");
	if(appzillon.plugin.retrieve("UPDATEREQUEST") == "Y"){
		return true;
	}else{
		return false;
	}
}

//Current Version
appzillon.plugin.getCurrentVersion = function(appName) {
	var version = "";
	if(appzillon.plugin.validategetCurrentVersion(appName)){
		version =  Android.currentVersion(appName);
	}
	return version;
}

/*...................... P ............................*/

/*Push Notification*/

/*Deletes notification corresponding to rowid from table and returns boolean value */
appzillon.plugin.deletenotification = function(jsonObj) {
	Android.deletePushMessages(JSON.stringify(jsonObj));
}

//Show Notification
/*Returns all push messages in json format as string*/
appzillon.plugin.shownotification = function(jsonObj) {
	Android.showPushMessages(JSON.stringify(jsonObj));
}

appzillon.plugin.getNotification = function(jsonObj) {
	var query = "SELECT * FROM tb_notifications;";
	jsonObj.id = sqlId + 1; // Declare �sqlId� as a global variable
	jsonObj.databaseName = "APPSDB"; // In case of iOS
	jsonObj.executeQuery = query;
	appzillon.plugin.executeSql(jsonObj);
}
//Delete Notification
appzillon.plugin.deleteNotification = function(jsonObj) {
	var ID = jsonObj.notificationID;
	var query = "DELETE FROM tb_notifications WHERE id = " + ID + ";";
	jsonObj.id = ID;
	jsonObj.databaseName = "APPSDB";
	jsonObj.executeQuery = query;
	appzillon.plugin.executeSql(jsonObj);
}
//Update Notification
appzillon.plugin.updateNotification = function(jsonObj) {
	var ID = jsonObj.notificationID;
	var readFlag = jsonObj.readFlag;
	readFlag = "'" + readFlag + "'";
	var query = "UPDATE tb_notifications SET readFlag = " + readFlag
			+ " WHERE id = " + ID + ";";
	jsonObj.id = ID;
	jsonObj.databaseName = "notificationDB";
	jsonObj.executeQuery = query;
	appzillon.plugin.executeSql(jsonObj);
}

/*...................... R ............................*/

/*Remote Wipe-out*/
appzillon.plugin.wipeOut = function(jsonObj) {
	localStorage.clear();
	Android.wipeOut(JSON.stringify(jsonObj));
}

//*****Run Time Logger*******

appzillon.plugin.runTimeDebug = function(debugMsg) {
	var json = {};
	json.errorType = "W";
	json.errorSeverity = "1";
	json.errorMessage = debugMsg;
	json.errorSource = "debug";
	appzillon.plugin.log(json);
}

/*Rotation Control*/

/*Lock the screen orientation*/
appzillon.plugin.lockRotation = function(jsonObject) {
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
	Android.lockScreenRotation(JSON.stringify(jsonObject));
	}
}

/*Unlock the screen orientation*/
appzillon.plugin.unlockRotation = function(jsonObject) {
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
	Android.unLockScreenRotation(JSON.stringify(jsonObject));
	}
}

/*...................... S ............................*/


/*Signatutre Pad*/
appzillon.plugin.signaturePad = function(json) {
	
	 if(appzillon.plugin.validateSignature(json)){
		 Android.captureSignature(JSON.stringify(json)); 
	 }
 	
 }

/*SMS*/

/*Sends sms to corresponding phNumber */
appzillon.plugin.smsSend = function(jsonobj) {
	Android.sendSms(JSON.stringify(jsonobj));
}

appzillon.plugin.startSMSListener = function() {
	Android.startSMSReceiveListener();
}

appzillon.plugin.stopSMSListener = function() {
	Android.stopSMSListener();
}

/*Social Media*/

appzillon.plugin.facebookLogin = function(jsonObj){
	Android.authFacebook(JSON.stringify(jsonObj));
}

appzillon.plugin.googleLogin = function(jsonObj){
	Android.authGooglePlus(JSON.stringify(jsonObj));
}

appzillon.plugin.linkedinLogin = function(jsonObj){
	Android.authLinkedin(JSON.stringify(jsonObj));
}

/*Settings*/

/*Sets setting screen data to preference file*/
appzillon.plugin.saveSettings = function() {
	var settingScreenData = appzillon.data.buildScreen();
	var settingData = JSON.stringify(settingScreenData.Settings)
	Android.setSettingsValue(settingData);
}

appzillon.plugin.setSettings = function(json) { // 3.2 changes
	var settingData = JSON.stringify(json);
	Android.setSettingsValue(settingData);
}

/*Loads setting screen data*/
appzillon.plugin.loadSettings = function() {
	var fileoutput = appzillon.util.getFile("appconfig/settings.json");
	var all = Android.getAllSettingsValue(fileoutput);
	var screenresponse = JSON.parse(all);
	appzillon.server.storeResponse(screenresponse);
	appzillon.server.loadResponse();
}

//Language
/*Returns current language in application*/
//appzillon.plugin.language = function() {
//	var langVal = Android.getSettingsValue('DEFAULTLANGUAGE');
//	return langVal;
//}

//NumberMask
/*Returns numberMask*/
//appzillon.plugin.numberMask = function() {
//	var maskVal = Android.getSettingsValue('NUMBERMASK');
//	return maskVal;
//}

/*Returns userDateFormat*/
//appzillon.plugin.userDateFormat = function() {
//	var dateFormatVal = Android.getSettingsValue('DATEFORMAT');
//	return dateFormatVal;
//}

/*Returns decimalSeperator*/
//appzillon.plugin.decimalSeparator = function() {
//	var decvalue = Android.getSettingsValue('DECIMALSEPARATOR');
//	return decvalue;
//}

/* Returns userTimeFormat */
//appzillon.plugin.userTimeFormat = function() {
//	var timestampFormatVal = Android.getSettingsValue('TIMEFORMAT');
//	return timestampFormatVal;
//}

/*user date time format*/
//appzillon.plugin.userDateTimeFormat = function() {
//	var value = appzillon.plugin.retrieve("DATETIMEFORMAT");
//	if (value == null || value == "" || value == 'null' || value == 'default')
//		value = "dd-MMM-yyyy HH-mm-ss";
//	return value;
//}

/*SQL Storage*/

/*Executes given sql query */
appzillon.plugin.executeSql = function(jsonObj) {
	if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
	Android.storagePlugin(JSON.stringify(jsonObj));
	}
}

/*Splash Screen*/
//TODO check weather showSplash needed or not
//Splash show
appzillon.plugin.showSplash = function() {
	Android.showSplash();
}

//Splash hide
appzillon.plugin.hideSplash = function() {
	Android.hideSplash();
}

/*Set Orientation*/ 
appzillon.plugin.setOrientation = function(mode) {
	Android.setOrientation(mode);
}
/*...................... V ............................*/

/*Vibrate Support*/

appzillon.plugin.vibrate = function(json) {
	
	if(appzillon.plugin.validatevibrate(json)){
		 Android.Vibrate(JSON.stringify(json)); 
	 }
}

/*Voice Support*/
appzillon.plugin.voice = function(jsonObj) {
	Android.speechToText(JSON.stringify(jsonObj));
}

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@ END Plugins calls (alphabetical order )@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/


/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@ START  UI Utils  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

//Current Theme
/*Returns  current Theme used in the application*/
appzillon.plugin.getCurrentTheme = function() {
	//Abhishke 13 August 2015, for getting the theme stored in preferences START
	// var themeVal = appzillon.plugin.retrieve('THEME');
	var themeVal = Android.getSettingsValue('THEME');
	//if (themeVal == null || themeVal == "") {
		// themeVal = appzillon.plugin.retrieve('THEME');
		//Abhishke 13 August 2015, for getting the theme stored in preferences END
		//themeVal = appzillon.plugin.retrieve('THEME');
	//}
	return themeVal;
}

//FirstPage
//appzillon.plugin.firstPage = function() {
//	var firstPage = Android.getFirstPage();
//	return firstPage;
//}


//Orientation to set, received from launch of app
//Abhishek bud id 4911 START
appzillon.plugin.setDeviceOrientation = function(orientation) {
//Abhishek bud id 4911 END	
	appzillon.data.orientation = orientation;
}

//Settings
appzillon.plugin.setSetting = function(key, value) {
//Abhishek 11 June 2015, Setting single value START
//	var jsonobj = {};
//	jsonobj["Settings"] = {}
//	jsonobj["Settings"][key] = value;
//	jsonobj = JSON.stringify(jsonobj);
//	Android.setSettingsValue(jsonobj);
	Android.setSetting(key, value);
}



/*Calls to set layout information to Preference file */
appzillon.plugin.setScreenMapping = function(scrJson) {
	Android.setScreenMapping(JSON.stringify(scrJson));
}

appzillon.plugin.sysDateTime = function() {
	var dObj = new Date();
	var day = dObj.getDate();
	var month = dObj.getMonth() + 1;
	var year = dObj.getFullYear();
	var hr = dObj.getHours();
	var sec = dObj.getSeconds();
	var min = dObj.getMinutes();
	var msec = dObj.getMilliseconds();

	if (day < 10) {
		day = "0" + day;
	}
	if (month < 10) {
		month = "0" + month;
	}
	if (min < 10) {
		min = "0" + min;
	}
	if (sec < 10) {
		sec = "0" + sec;
	}
	if (hr < 10) {
		hr = "0" + hr;
	}
	if (msec < 10) {
		msec = "00" + msec;
	} else if (msec < 100) {
		msec = "0" + msec;
	}

	var dateval = year + "-" + month + "-" + day + " " + hr + ":" + min + ":"
			+ sec + "." + msec;
	return dateval;
};




/* Returns thousandSeperator */
/*appzillon.plugin.thousandSeparator = function() {
	value = appzillon.plugin.decimalSeparator();
	if (value == ".") {
		value = ",";
	} else {
		value = ".";
	}
	return value;
}*/



/*Returns uniqueDeviceId*/
/*appzillon.plugin.uniqueDeviceId = function uniqueDeviceId () {
    return appzillon.plugin.retrieve('HASHKEY1');
}*/

//Abhishek bug id 4549 getting animation support START
appzillon.plugin.isAnimationSupported = function(){
	
	return Android.isAnimationSupported();
}
//Abhishek bug id 4549 getting animation support END

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@ END UI Utils  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/


/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@ START  INTERNAL PLUGINS  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

/*Calls to save the message with corresponding refNo in table*/
appzillon.plugin.captureNotes = function(refNo) {
	Android.startNotePad(refNo);
}

/*Starts scheduler available in device */
appzillon.plugin.scheduleCalendarEvent = function(l_event, eventDescription, eventTime) {
	Android.startCalendar(l_event, eventDescription, eventTime);
}

/*Calls to record video*/
appzillon.plugin.videoRecording = function(jsonObj) {
	Android.recordVideo(JSON.stringify(jsonObj));
}


/*Returns OTP generated with given userId and password*/
appzillon.plugin.generateOTP = function(userId, pin ,logintimestamp) {
	var otpflag = appzillon.user.authenticationtype;
	if (otpflag == '#DeviceId') {
		var OTP = Android.getOTP(userId, pin ,logintimestamp);
		return OTP;
	} else {
		return pin;
	}
}

/*Returns device type "ANDROID"*/
//appzillon.plugin.deviceType = function() {
//	return Android.getDeviceType();
//}

/*Returns IMEI no associated with the device*/
//appzillon.plugin.hashKey1 = function() {
//////deviceidchanges starts
//	hashkey1 = "ANDROID"; 
//	hashkey1 = appzillon.plugin.retrieve('HASHKEY1');
//////deviceidchanges ends
//	return hashkey1;
//}

/*Returns IMSI no associated with the device*/
//appzillon.plugin.hashKey2 = function() {
//////deviceidchanges starts
//	hashkey2 = "ANDROID"; 
//	hashkey2 = appzillon.plugin.retrieve('HASHKEY2');
//////deviceidchanges ends
//	return hashkey2;
//}

/*Returns value associated with key stored in Preference file*/
appzillon.plugin.getPreference = function(key) {
	var keyValue = Android.getSettingsValue(key);
	return keyValue;
}


/*Returns base64 code of picture associated with filename */
/*appzillon.plugin.getBase64Code = function(filename) {
	var imageCode = appzillon.plugin.retrieve(filename + "_image");
	return imageCode;
}*/

/*To call SSL url*/
appzillon.plugin.loadSSL = function(url) {
	$.ajax({
		url : url,
		cache : false,
		timeout : 10000,
		success : function(data) {
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert("Server not available.");
		}

	});
}

/*Returns encrypted text as string */
appzillon.plugin.hashSHA256 = function(ptext, psalt) {
	encryPswd = Android.hashSHA256(ptext, psalt);
	return encryPswd;
}

/*Returns serverToken as string */
//appzillon.plugin.serverToken = function() {
//	serverToken = Android.getServerToken();
//	//alert("serverToken : "+serverToken);
//	return serverToken;
//}



/*parsse exception*/
function jsonParseExceptionCallBack(excep) {
	appzillon.util.displayMessage(excep.errorCode, "", "");
}

//*********LOGS START*********//
/*Upload Logfile*/
appzillon.plugin.uploadLog = function(fileName) {
	var newPath = 'upload';
	var serverAddr = appzillon.data.serverurl;
	var segements = serverAddr.split("/");
	segements[segements.length - 1] = "" + newPath;
	var finalurl = segements.join("/");
	var uploadDetails = {
		"fieldID" : "",
		"destination" : "",
		"filepath" : fileName,
		"successCallback" : "appzillon.plugin.uploadLogSuccess",
		"failureCallback" : "appzillon.plugin.uploadLogFailure"
	};
	uploadDetails.fieldID = fileName;
	appzillon.plugin.uploadFile(uploadDetails);
}

appzillon.plugin.uploadLogSuccess = function(json) {
	Android.deleteLogFile();
}

appzillon.plugin.uploadLogFailure = function(json) {

}

appzillon.plugin.appendLog = function(logs){
	appzillon.data.logmessages += logs + " ~ ";
}

//*********LOGS END*********//


/* Function for executing a offline sql query */
appzillon.plugin.offlineData = function(query) {
	jsonObj = {
		"id" : "offlineID",
		"successCallback" : "appzillon.plugin.offlineSuccess",
		"failureCallback" : "appzillon.plugin.offlineFailure",
		"databaseName" : "APPSDB",
		"executeQuery" : query
	}
	appzillon.plugin.executeSql(jsonObj);
}

appzillon.plugin.offlineSuccess = function(json) {
	if (json.sqlResult == "success") {
		appzillon.offline.executeSqlCallBack('S');
	} else {
		var res = JSON.stringify(json.sqlResult);
		res = res.replace(/\\\"/g, '\"');
		appzillon.offline.executeSqlCallBack(res);
	}
}

appzillon.plugin.offlineFailure = function(json) {
	appzillon.offline.executeSqlCallBack('F');
}

//appzillon.plugin.serverUrl = function(serverurl) {
//	appzillon.plugin.store('SERVERURL', serverurl);
//	Android.setServerURL(serverurl);
//}

//appzillon.plugin.otpRequired = function() {
//	var otpflag = appzillon.plugin.retrieve("OTPFLAG");
//	return otpflag;
//}

appzillon.plugin.saveReport = function(jsonobj) {
	Android.savereport(JSON.stringify(jsonobj));
}

/*appzillon.plugin.getDevice = function() {
	var devtype = Android.getDevice();
	return devtype;
}*/

/*appzillon.plugin.userId = function() {
	var userid;
	try {
		userid = appzillon.plugin.retrieve('USERID');
	} catch (e) {
		userid = "";
	}

	if (userid == null) {
		userid = "";
	}

	return userid;
}*/



appzillon.plugin.widgetDateFormat = function() {
	var lformat = "yyyy-MM-dd";
	return lformat;
}
appzillon.plugin.getAndroidversion = function() {
	var version = Android.getBuildversion();
	return version;
}

appzillon.plugin.smsReceive = function(jsonObj) {
	var phoneno = jsonObj.number;
	var message = jsonObj.message;
	appzillon.app.smsReceive(phoneno, message);
}

//Abhishek 14 April 2015 For bug id 4769 START
//Told by Ashotosh to comment out as it is been used in server.js 
//appzillon.server.postRequest = function(preqinterfaceid, prespinterfaceid,preqstr, ppaintresp, pcallid, pasync, pcallback) {
//	///// checkfor version and call the respective funtion
//	var linternalserverurl = "";
//	linternalserverurl = appzillon.plugin.retrieve("APPZILLONSERVERURL");
//	try {
//		var lleninferfaceID = preqinterfaceid.length;
//		var interfaceID = preqinterfaceid.substring(lleninferfaceID - 4);
//		var intfaceID = preqinterfaceid;
//		if (interfaceID == "_Req" || interfaceID == "_Res") {
//			intfaceID = intfaceID.substr(0, lleninferfaceID - 4);
//		}
//		isOffline = appzillon.data.interfacesmap[intfaceID].offlinesupport;
//	} catch (err) {
//		isOffline = "N";
//	}
//	appzillon.server.serverCallCounter = appzillon.server.serverCallCounter + 1;
//	var xhr = new XMLHttpRequest();
//	xhr.open("POST", linternalserverurl, pasync);
//	xhr.onreadystatechange = function() {
//		if (xhr.readyState === XMLHttpRequest.DONE) {
//			if (xhr.status === 200) {
//				var presp = JSON.parse(xhr.responseText);
//				var lcallid = presp.appzillonHeader.requestID;
//				var lstatus = presp.appzillonHeader.status;
//				appzillon.server.postRequestCallBack(lstatus, presp,preqinterfaceid, prespinterfaceid, preqstr, ppaintresp,	lcallid, pasync, pcallback);
//				if (isOffline == "Y") {
//					var lscreenid = appzillon.plugin.retrieve("SERVER.OFFLINESCRNAME");
//					var lrefno = appzillon.plugin.retrieve("SERVER.OFFLINEREFNO");
//
//					if (lstatus == "failure" || (lscreenid == appzillon.data.scrid && !appzillon.util.isNull(lrefno))) {
//						var lactualStatus = appzillon.offline.uploadsuccess;
//						if (lstatus == "failure") {
//							lactualStatus = appzillon.offline.uploadfailure;
//						}
//						appzillon.offline.persistOfflineData(intfaceID,	preqstr, presp, lactualStatus);
//					}
//				}
//			} else {
//				appzillon.server.postRequestCallBack("servererror", "",	preqinterfaceid, prespinterfaceid, preqstr, ppaintresp,	pcallid, pasync, pcallback);
//			}
//		}
//	}
//	xhr.send(preqstr);
//}
//Abhishek 14 April 2015 For bug id 4769 END

/*Clear LocalStorage*/
appzillon.plugin.clearStorage = function() {
	localStorage.clear();
	appzillon.plugin.initializeStorage();
}

appzillon.plugin.initializeStorage = function() {
	//var fileOutput = appzillon.util.getFile("appconfig/settings.json");
	//var fileVal = JSON.parse(fileOutput);
	//Abhishek,29 April 2015, Bug id 5218,5183, Saving settings json from Javascript only START
	//var tJson = fileVal; // 3.2 changes
	//for (var key in tJson) {
	//	  if (tJson.hasOwnProperty(key)) {
	//	    localStorage.setItem(key, tJson[key]);
	//	  }
	//	}
	//it was saving value, but on retrive it was not giving value after logout so commented on 29 April 2015	
	//	var settingsData = JSON.stringify(fileVal);
	//	Android.setSettingsDefaultVal(settingsData);
	
	//Abhishek,29 April 2015, Bug id 5218,5183, Saving settings json from Javascript only END	
	/*Settings end*/
	//lang = appzillon.plugin.language();
	Android.initLocalStorage();
	//Android.setDisplayInfo(); //nagaraj 03 September 2015
	//localStorage.setItem("DEFAULTLANGUAGE", lang);
	//Abhishek , bug id 5342, saving again into local storage once it is cleared START
	//localStorage.setItem("DEVICETYPE", "ANDROID");
	//Abhishek , bug id 5342, saving again into local storage once it is cleared END
	//Abhishek , bug id 5238 , setting sendlog value to localstorage START
	//var sendLog = Android.getSettingsValue('SENDLOG');
	//console.log("sendLog : "+sendLog);
	//localStorage.setItem("SENDLOG", sendLog);
	//Abhishek , bug id 5238 , setting sendlog value to localstorage START
	
	//var audit = Android.getSettingsValue('AUDITLOGREQUIRED');
	//localStorage.setItem("AUDITLOGREQUIRED", audit);
	//var otpFlag = Android.getOtpflag();
	//localStorage.setItem("OTPFLAG", otpFlag);
	//var theme = appzillon.plugin.getCurrentTheme();
	//localStorage.setItem("THEME", theme);
	//appzillon.data.devicetype = appzillon.plugin.retrieve('DEVICETYPE');
	var scrnMap = Android.getScreenMap();
	if (scrnMap != "") {
		var parent = scrnMap.split('%');
		for (i = 0; i < parent.length - 1; i++) {
			var child = parent[i].split(":");
			localStorage.setItem(child[0], child[1]);
		}
	} else {
		//appzillon.util.getLayoutMapping();
		//Abhishek, 06 July 2015 , Updated getDevice Logic, Now get device and lock Roation are independent on each other START
 		Android.lockRotationByDefault();
 		//Abhishek, 06 July 2015 , Updated getDevice Logic, Now get device and lock Roation are independent on each other END
	}
	/*work flow initialization*/
	//appzillon.wf.buildWFArray();
	//appzillon.data.loadProjectDetails();
	Android.loadFirstPage();
}

appzillon.plugin.deviceStatus = function(jsonObj) {
	appzillonappsuccess = jsonObj.successCallback;
	appzillonappfailure = jsonObj.failureCallback;
	var reqobj = {};
	reqobj.deviceStatusRequest = {};
	reqobj.deviceStatusRequest.appId = appzillon.data.appid;
	reqobj.deviceStatusRequest.deviceId = appzillon.plugin.getUUID();
	var req = JSON.stringify(reqobj);
	appzillon.server.callServer("deviceStatus_Req", "deviceStatus_Res", "N",req, "N", "checkDevice", "true", appzillon.app.checkDevice);
}

appzillon.app.checkDevice = function(pcallid, lifaceid, pstatus, lerrorCode,lbodyobj) {
	if (pstatus) {
		var status = lbodyobj.findDeviceStatus_Res.activeStatus;
		executeCallback(appzillonappsuccess, status);
	} else {
		executeCallback(appzillonappfailure, "");
	}
}



appzillon.plugin.notify = function(pushmessage, appstatus, param) {
	push_message = pushmessage;
	notification_param = param;
	if (appstatus) {
		//Abhishek Bug id 5184, updated code START
//		appzillon.util.displayMessage("LIT_NOTIFY", fn_notify);
		appzillon.util.displayMessage("APZ-CNT-225", fn_notify);
		//Abhishek Bug id 5184, updated code END
	} else {
		appzillon.app.onNotification(push_message,notification_param);
	}

}
function fn_notify(param) {
//Abhishek 21 April 2015 , check result.id == ‘popup_ok’ in appzillon.plugin.notify for actionable notification START
//	if (param) {

//Abhishek 08 May 2015, bug id 5332, Updated as per changes in infra side START
//	if (param.id=='popup_ok') {
	if (param==true) {
//Abhishek 08 May 2015, bug id 5332, Updated as per changes in infra side END
		
//Abhishek 21 April 2015 , check result.id == ‘popup_ok’ in appzillon.plugin.notify for actionable notification END
		try {
			appzillon.app.onNotification(push_message,notification_param);
		} catch (err) {
		}
	} else {
	}
}

appzillon.plugin.getUUID = function() {
	var device_id = Android.getUUID();
	return device_id;
}



//appzillon.plugin.getUpdateRequest = function() {
//	if(appzillon.plugin.retrieve("UPDATEREQUEST") == "Y"){
//		return true;
//	}else{
//		return false;
//	}
//	
//}


//Remote Debug

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@ END INTERNAL PLUGINS  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@ START  Validation  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

//Validate Success call back
appzillon.plugin.successcallvalidate = function successcallvalidate (jsonobject) {
   var lreturn = false;
   if (!appzillon.util.isNull(jsonobject.successCallback)) {
          lreturn = true;
   }
   return lreturn;
};

//Validate Failure call back
appzillon.plugin.failurecallvalidate = function failurecallvalidate (jsonobject) {
   var lreturn = false;
   if (!appzillon.util.isNull(jsonobject.failureCallback)) {
          lreturn = true;
   }
   return lreturn;
};

//Email
appzillon.plugin.validatemail = function validatemail (jsonobject) {
   var lcheck = false;
   var lsuccesscallpresent = appzillon.util.containsKey (jsonobject, "successCallback");
   var lfailurecallpresent = appzillon.util.containsKey (jsonobject, "failureCallback");

   if (!lsuccesscallpresent) {
	   appzillon.util.displayMessage("APZ-CNT-166", null);
   } else if (!lfailurecallpresent) {
	   appzillon.util.displayMessage("APZ-CNT-167", null);
   } else {
   
          var lsuccesscall = appzillon.plugin.successcallvalidate(jsonobject);
          var lfailurecall = appzillon.plugin.failurecallvalidate(jsonobject);
          var lbodycheck = false; 
          if (!appzillon.util.isNull(jsonobject.body)) {
                 lbodycheck = true;
          } 
          var lrecipientmail = false;
          if (!appzillon.util.isNull(jsonobject.recipientMailId)) {
                 lrecipientmail = true;
          }
          var lsubjectcheck = false;
          if (!appzillon.util.isNull(jsonobject.subject)) {
                 lsubjectcheck = true;
          }
          var lidcheck = false;
          if (!appzillon.util.isNull(jsonobject.id)) {
                 lidcheck = true;
          }
          if (!lsuccesscall) {
        	  appzillon.util.displayMessage("APZ-CNT-166", null);
          } else if (!lfailurecall) {
        	  appzillon.util.displayMessage("APZ-CNT-167", null);
          } else if (!lbodycheck) {
        	  appzillon.util.displayMessage("APZ-CNT-172", null);
          } else if (!lrecipientmail) {
        	  appzillon.util.displayMessage("APZ-CNT-173", null);
          } else if (!lsubjectcheck) {
        	  appzillon.util.displayMessage("APZ-CNT-174", null);
          } else if (!lidcheck) {
        	  appzillon.util.displayMessage("APZ-CNT-175", null);
          } else {
                 lcheck = true;
          }
   }
   return lcheck;
};

//Geo Fencing
appzillon.plugin.validategeofencing = function validategeofencing(jsonobject) {
	var lcheck = false;
	var lsuccesscallpresent = appzillon.util.containsKey(jsonobject,"successCallback");
	var lfailurecallpresent = appzillon.util.containsKey(jsonobject,"failureCallback");
	var lregioncheck = appzillon.util.containsKey(jsonobject, "region");

	if (!lsuccesscallpresent) {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else if (!lfailurecallpresent) {
		appzillon.util.displayMessage("APZ-CNT-167", null);
	} else if (!lregioncheck) {
		appzillon.util.displayMessage("APZ-CNT-176", null);
	} else {
		var lsuccesscall = appzillon.plugin.successcallvalidate(jsonobject);
		var lfailurecall = appzillon.plugin.failurecallvalidate(jsonobject);
		var lregionchecknull = false;
		var lregioncheckval = false;
		var lcountylistpresent = false;
		var lcountylistnull = false;
		var lcountylistval = false;
		var lradiuspresent = false;
		var lradiusnull = false;
		var lradiusval = false;
		var lcoordpresent = false;
		var lcoordnull = false;
		var lcoordval = false;

		if (!appzillon.util.isNull(jsonobject.region)) {
			lregionchecknull = true;
			if (jsonobject.region == "COUNTRY") {
				lregioncheckval = true;
				lradiuspresent = true;
				lradiusnull = true;
				lradiusval = true;
				lcoordpresent = true;
				lcoordnull = true;
				lcoordval = true;
				lcountylistpresent = appzillon.util.containsKey(jsonobject,	"CountryList");
				if (lcountylistpresent) {
					if (!appzillon.util.isNull(jsonobject.CountryList)) {
						lcountylistnull = true;
						if (jsonobject.CountryList.constructor === Array) {
							if (jsonobject.CountryList.length > 0) {
								lcountylistval = true;
							}
						}
					}
				}
			}
			if (jsonobject.region == "LATLONG") {
				lregioncheckval = true;
				lcountylistpresent = true;
				lcountylistnull = true;
				lcountylistval = true;
				lradiuspresent = appzillon.util.containsKey(jsonobject,	"radius");
				if (lradiuspresent) {
					if (!appzillon.util.isNull(jsonobject.radius)) {
						lradiusnull = true;
						if (!(isNaN(jsonobject.radius) || jsonobject.radius === Infinity || jsonobject.radius === "Infinity" || jsonobject.radius <= 0)) {
							lradiusval = true;
						}
					}
				}
				lcoordpresent = appzillon.util.containsKey(jsonobject,"coordinates");
				if (lcoordpresent) {
					if (!appzillon.util.isNull(jsonobject.coordinates)) {
						lcoordnull = true;
						var coordinates = jsonobject.coordinates.split(",");
						if (coordinates.length == 2) {
							if (!appzillon.util.isNull(coordinates[0]) && !appzillon.util.isNull(coordinates[1])) {
								if (appzillon.plugin.validateinrange(-90,coordinates[0], 90) && appzillon.plugin.validateinrange(-180, coordinates[1], 180)) {
									lcoordval = true;
								}
							}
						}
					}
				}
			}
		}

		if (!lcountylistpresent) {
			appzillon.util.displayMessage("APZ-CNT-178", null);
		} else if (!lradiuspresent) {
			appzillon.util.displayMessage("APZ-CNT-179", null);
		} else if (!lcoordpresent) {
			appzillon.util.displayMessage("APZ-CNT-181", null);
		} else if (!lsuccesscall) {
			appzillon.util.displayMessage("APZ-CNT-166", null);
		} else if (!lfailurecall) {
			appzillon.util.displayMessage("APZ-CNT-167", null);
		} else if (!lregionchecknull) {
			appzillon.util.displayMessage("APZ-CNT-176", null);
		} else if (!lcountylistnull) {
			appzillon.util.displayMessage("APZ-CNT-178", null);
		} else if (!lradiusnull) {
			appzillon.util.displayMessage("APZ-CNT-179", null);
		} else if (!lcoordnull) {
			appzillon.util.displayMessage("APZ-CNT-181", null);
		} else if (!lregioncheckval) {
			appzillon.util.displayMessage("APZ-CNT-177", null);
		} else if (!lcountylistval) {
			appzillon.util.displayMessage("APZ-CNT-178", null);
		} else if (!lradiusval) {
			appzillon.util.displayMessage("APZ-CNT-180", null);
		} else if (!lcoordval) {
			appzillon.util.displayMessage("APZ-CNT-182", null);
		} else {
			lcheck = true;
		}
	}
	return lcheck;
};

// Current version
appzillon.plugin.validategetCurrentVersion = function validategetCurrentVersion (appname) {
   var lcheck = false;
   if (appzillon.util.isNull(appname)) {
	   appzillon.util.displayMessage("APZ-CNT-183", null);
   } else {
          lcheck = true;
   }
   return lcheck;
};

//Instructions
appzillon.plugin.validategetInstructions = function validategetInstructions(jsonobject) {
	var lcheck = false;
	var lsuccesscallpresent = appzillon.util.containsKey(jsonobject,"successCallback");
	var lfailurecallpresent = appzillon.util.containsKey(jsonobject,"failureCallback");
	var lappnamepresent = appzillon.util.containsKey(jsonobject, "appId");
	if (!lsuccesscallpresent) {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else if (!lfailurecallpresent) {
		appzillon.util.displayMessage("APZ-CNT-167", null);
	} else if (!lappnamepresent) {
		appzillon.util.displayMessage("APZ-CNT-183", null);
	} else {
		var lsuccesscall = appzillon.plugin.successcallvalidate(jsonobject);
		var lfailurecall = appzillon.plugin.failurecallvalidate(jsonobject);
		var lappname = false;
		if (!appzillon.util.isNull(jsonobject.appId)) {
			lappname = true;
		}

		if (!lsuccesscall) {
			appzillon.util.displayMessage("APZ-CNT-166", null);
		} else if (!lfailurecall) {
			appzillon.util.displayMessage("APZ-CNT-167", null);
		} else if (!lappname) {
			appzillon.util.displayMessage("APZ-CNT-183", null);
		} else {
			lcheck = true;
		}
	}
	return lcheck;
};

// Upgrade App
appzillon.plugin.validateupgradeApp = function validateupgradeApp (appname) {
   var lcheck = false;
   if (appzillon.util.isNull(appname)) {
	   appzillon.util.displayMessage("APZ-CNT-183", null);
   } else {
          lcheck = true;
   }
   return lcheck;
};

//Sub App Delete
appzillon.plugin.validatesubappDelete = function validatesubappDelete (appname) {
   var lcheck = false;
   if (appzillon.util.isNull(appname)) {
	   appzillon.util.displayMessage("APZ-CNT-183", null);
   } else {
          lcheck = true;
   }
   return lcheck;
};



//Signature 
appzillon.plugin.validateSignature = function validateSignature(jsonobject){
	   var lcheck = false;
	   var lsuccesscallpresent = appzillon.util.containsKey (jsonobject, "successCallback");
	   var lfailurecallpresent = appzillon.util.containsKey (jsonobject, "failureCallback");
	   if (!lsuccesscallpresent) {
		   appzillon.util.displayMessage("APZ-CNT-166", null);
	   } else if (!lfailurecallpresent) {
		   appzillon.util.displayMessage("APZ-CNT-167", null);
	   } else {
	          var lsuccesscall = appzillon.plugin.successcallvalidate(jsonobject);
	          var lfailurecall = appzillon.plugin.failurecallvalidate(jsonobject);
	                 
	          if (!lsuccesscall) {
	        	  appzillon.util.displayMessage("APZ-CNT-166", null);
	          } else if (!lfailurecall) {
	        	  appzillon.util.displayMessage("APZ-CNT-167", null);
	          } else {
	                 lcheck = true;
	          }
	   }      
	   return lcheck;
	
};

//Voice 
appzillon.plugin.validateVoice = function validateVoice(jsonobject){
	   var lcheck = false;
	   var lsuccesscallpresent = appzillon.util.containsKey (jsonobject, "successCallback");
	   var lfailurecallpresent = appzillon.util.containsKey (jsonobject, "failureCallback");
	   if (!lsuccesscallpresent) {
		   appzillon.util.displayMessage("APZ-CNT-166", null);
	   } else if (!lfailurecallpresent) {
		   appzillon.util.displayMessage("APZ-CNT-167", null);
	   } else {
	          var lsuccesscall = appzillon.plugin.successcallvalidate(jsonobject);
	          var lfailurecall = appzillon.plugin.failurecallvalidate(jsonobject);
	                 
	          if (!lsuccesscall) {
	        	  appzillon.util.displayMessage("APZ-CNT-166", null);
	          } else if (!lfailurecall) {
	        	  appzillon.util.displayMessage("APZ-CNT-167", null);
	          } else {
	                 lcheck = true;
	          }
	   }      
	   return lcheck;
	
};

//File Encryption-Decryption 
appzillon.plugin.validateFileEncDesc = function validateFileEncDesc(jsonobject) {
	var lcheck = false;

	var lKeyPresent = appzillon.util.containsKey(jsonobject, "key");
	var sourcepathPresent = appzillon.util.containsKey(jsonobject,"srcFilePath");
	var destpathPresent = appzillon.util.containsKey(jsonobject, "destFilePath");

	if (!lKeyPresent) {
		appzillon.util.displayMessage("APZ-CNT-184", null);
	} else if (!sourcepathPresent) {
		appzillon.util.displayMessage("APZ-CNT-190", null);
	} else if (!destpathPresent) {
		appzillon.util.displayMessage("APZ-CNT-191", null);
	} else {
		var lkeycheck = false;
		if (!appzillon.util.isNull(jsonobject.key)) {
			lkeycheck = true;
		}
		var lsrcFilePathcheck = false;
		if (!appzillon.util.isNull(jsonobject.srcFilePath)) {
			lsrcFilePathcheck = true;
		}
		var ldestFilePathcheck = false;
		if (!appzillon.util.isNull(jsonobject.destFilePath)) {
			ldestFilePathcheck = true;
		}
		if (!lkeycheck) {
			appzillon.util.displayMessage("APZ-CNT-184", null);
		} else if (!lsrcFilePathcheck) {
			appzillon.util.displayMessage("APZ-CNT-190", null);
		} else if (!ldestFilePathcheck) {
			appzillon.util.displayMessage("APZ-CNT-191", null);
		} else {
			lcheck = true;
		}
	}
	return lcheck;

};


// Vibrate
appzillon.plugin.validatevibrate = function validatevibrate (jsonobject) {
	var lcheck= false;
    var ltimepresent = appzillon.util.containsKey (jsonobject, "time");
   
   if (!ltimepresent) {
	   appzillon.util.displayMessage("APZ-CNT-185", null);
   } else {

          var ltimechecknull = false;
		  var ltimecheckval = false;
	      if (!appzillon.util.isNull(jsonobject.time)) {
			ltimechecknull = true;
			if( !(isNaN(jsonobject.time) || jsonobject.time=== Infinity || jsonobject.time=== "Infinity" || jsonobject.time<=0)){
				ltimecheckval = true;
			}
          }

          if (!ltimechecknull) {
        	  appzillon.util.displayMessage("APZ-CNT-185", null);
          } else if (!ltimecheckval) {
        	  appzillon.util.displayMessage("APZ-CNT-132", null);
          } else {
                 lcheck = true;
          }
   }

   return lcheck;
};

//Location Selector
appzillon.plugin.validateareaselector = function validateareaselector (jsonobject) {
var lcheck = false;
   var lradiuspresent = appzillon.util.containsKey (jsonobject, "radius");
   var lnearbyplacespresent = appzillon.util.containsKey (jsonobject, "nearbyplaces");
   
   if (!lradiuspresent) {
	   appzillon.util.displayMessage("APZ-CNT-179", null);
   } else if (!lnearbyplacespresent) {
	   appzillon.util.displayMessage("APZ-CNT-192", null);
   } else {

          var lradiuschecknull = false;
		  var lradiuscheckval = false;
	      if (!appzillon.util.isNull(jsonobject.radius)) {
			lradiuschecknull = true;
			if( !(isNaN(jsonobject.radius) || jsonobject.radius=== Infinity || jsonobject.radius=== "Infinity" || jsonobject.radius<=0)){
				lradiuscheckval = true;
			}
          }
		  var lnearbyplaceschecknull = false;
		  var lnearbyplacescheckval = false;
		  if (!appzillon.util.isNull(jsonobject.nearbyplaces)) {
			lnearbyplaceschecknull = true;
			if (jsonobject.nearbyplaces.constructor === Array){
				var count = 0;
				for (var i = 0; i<jsonobject.nearbyplaces.length; i++){
					if (!appzillon.util.isNull(jsonobject.nearbyplaces[i].locationLatitude) && !appzillon.util.isNull(jsonobject.nearbyplaces[i].locationLongitude)){
						if (appzillon.plugin.validateinrange(-90,jsonobject.nearbyplaces[i].locationLatitude,90) && appzillon.plugin.validateinrange(-180,jsonobject.nearbyplaces[i].locationLongitude,180)) {
							count++;
						}
					}
				}
				if (count == jsonobject.nearbyplaces.length)
					lnearbyplacescheckval = true;
			}
          }          
          
          if (!lradiuschecknull) {
        	  appzillon.util.displayMessage("APZ-CNT-179", null);
          } else if (!lnearbyplaceschecknull) {
        	  appzillon.util.displayMessage("APZ-CNT-192", null);
          } else if (!lradiuscheckval) {
        	  appzillon.util.displayMessage("APZ-CNT-180", null);
          } else if (!lnearbyplacescheckval) {
        	  appzillon.util.displayMessage("APZ-CNT-092", null);
          } else {
                 lcheck = true;
          }
   }
   return lcheck;
};

//Route to get Directions on Map
appzillon.plugin.validaterouteplugin = function(jsonobject) {
    var lcheck = false;
    var ltoLocationpresent = appzillon.util.containsKey (jsonobject, "toLocation");
    if (!ltoLocationpresent) {
           appzillon.util.displayMessage("APZ-CNT-188", null);
    } else {
                  
           var lfromLocationval = false;
           var ltoLocationnull = false;
           var ltoLocationval = false;

           var lfromLocationpresent = appzillon.util.containsKey (jsonobject, "fromLocation");
           if (!lfromLocationpresent){
                  lfromLocationval = true;
           }
           else{
                  if (appzillon.util.isNull(jsonobject.fromLocation)) {
                        lfromLocationval = true;
                  }
                  else{
                        var coordinates = jsonobject.fromLocation.split(",");
                        if (coordinates.length == 2){
                               if (!appzillon.util.isNull(coordinates[0]) && !appzillon.util.isNull(coordinates[1])){
                                      if (appzillon.plugin.validateinrange(-90,coordinates[0],90) && appzillon.plugin.validateinrange(-180,coordinates[1],180)) {
                                             lfromLocationval = true;                                             
                                      }
                               }
                        }      
                  }
           }
           
                  
           if (!appzillon.util.isNull(jsonobject.toLocation)) {
                  ltoLocationnull = true;
                  var coordinates = jsonobject.toLocation.split(",");
                  if (coordinates.length == 2){
                        if (!appzillon.util.isNull(coordinates[0]) && !appzillon.util.isNull(coordinates[1])){
                               if (appzillon.plugin.validateinrange(-90,coordinates[0],90) && appzillon.plugin.validateinrange(-180,coordinates[1],180)) {
                                       ltoLocationval = true;
                               }
                               
                        }
                  }      
           }
           if (!ltoLocationnull) {
                  appzillon.util.displayMessage("APZ-CNT-188", null);
           } else if (!lfromLocationval) {
                  appzillon.util.displayMessage("APZ-CNT-187", null);
           } else if (!ltoLocationval) {
                  appzillon.util.displayMessage("APZ-CNT-189", null);
           } else {
                  lcheck = true
           }
    }
    
    return lcheck;
};


appzillon.plugin.validateinrange = function(min, number, max) {
	if (!isNaN(number) && !(number === "") && !(number === Infinity) && !(number === "Infinity") && (number >= min) && (number <= max)) { // Shirish 31mar
		return true;
	} else {
		return false;
	}
};

//Shirish 28jan start
//Launch App
appzillon.plugin.validatelaunchApp = function validatelaunchApp(appname) {
	var lcheck = false;
	if (appzillon.util.isNull(appname)) {
		appzillon.util.displayMessage("APZ-CNT-183", null);
	} else {
		lcheck = true;
	}
	return lcheck;
};

//Validate Remote Debug
appzillon.plugin.validateremoteDebug = function validateremoteDebug(jsonobject) {
	var lcheck = false;
	var ldebugpresent = appzillon.util.containsKey(jsonobject, "debug");

	if (!ldebugpresent) {
		appzillon.util.displayMessage("APZ-CNT-194", null);
	} else {
		var ldebugnull = false;
		if (!appzillon.util.isNull(jsonobject.debug)) {
			ldebugnull = true;
		}
		if (!ldebugnull) {
			appzillon.util.displayMessage("APZ-CNT-194", null);
		} else {
			lcheck = true;
		}
	}

	return lcheck;
};

//Validate appstatus 
appzillon.plugin.validateAppStatus = function validateAppStatus(jsonobject) {
	var lcheck = false;
	var lsuccesscallpresent = appzillon.util.containsKey(jsonobject,"successCallback");
	var lfailurecallpresent = appzillon.util.containsKey(jsonobject,"failureCallback");
	if (!lsuccesscallpresent) {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else if (!lfailurecallpresent) {
		appzillon.util.displayMessage("APZ-CNT-167", null);
	} else {
		var lsuccesscall = appzillon.plugin.successcallvalidate(jsonobject);
		var lfailurecall = appzillon.plugin.failurecallvalidate(jsonobject);

		if (!lsuccesscall) {
			appzillon.util.displayMessage("APZ-CNT-166", null);
		} else if (!lfailurecall) {
			appzillon.util.displayMessage("APZ-CNT-167", null);
		} else {
			lcheck = true;
		}
	}
	return lcheck;

};
//Shirish 28jan end

// validation for whitelist 3.2 Siddu

appzillon.plugin.validateWhiteList = function validateWhiteList(url) {
		var lproceed = false;
    	var whiteListCheck = appzillon.plugin.retrieve("URLWHITELIST");
    
    	if (whiteListCheck) {
        var check = JSON.parse(whiteListCheck);
        for (var i = 0; i < check.length; i++) {
            var counter = check[i].url;
            if (counter == url) {
                lproceed = true;
                break;
            }
        }       
	}else{
		lproceed = true; 
	}
	return lproceed;
};	
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@ END Validation  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

//appzillon.plugin.getPPI=function(){
//	var ppi = appzillon.plugin.retrieve("SCREENPPI");
//	if(appzillon.util.isNull(ppi)){
//		ppi = Android.getPPI();
//	}
//	return ppi;
//}

//appzillon.plugin.getScreenSize=function(){
//	var scrsize = appzillon.plugin.retrieve("SCREENSIZE");
//	if(appzillon.util.isNull(scrsize)){
//		scrsize = Android.getScreenSize();
//	}
//	return scrsize;
//}

appzillon.plugin.getLocation = function(jsonObject){
//	var latLong = Android.getPresentLatLong();
//	appzillon.server.locationCallback(latLong);
	Android.getPresentLatLong(JSON.stringify(jsonObject))
}

appzillon.plugin.setRingtone = function(jsonObject){
	Android.setRingtone(JSON.stringify(jsonObject));
}

appzillon.plugin.getInboxSms = function(jsonObj){
	Android.getInboxSms(JSON.stringify(jsonObj));
}
appzillon.plugin.getMissedCalls = function(jsonObj){
	Android.getMissedCalls(JSON.stringify(jsonObj));
}

appzillon.plugin.launchWebview = function(jsonObject){	
	if (jsonObject.successCallback == undefined || jsonObject.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	}else{
        if (appzillon.plugin.validateWhiteList(jsonObject.URL)) {
            Android.launchWebview(JSON.stringify(jsonObject));
        } else {
            appzillon.util.displayMessage("APZ-CNT-131", null);
        }
    }
}

appzillon.plugin.closeWebview = function(){
	Android.closeWebview();
}
appzillon.plugin.fileToBase64 = function(jsonobj) {
//	Example JSON 
//	var jsonobj = {
//	        "successCallback": "appzillon.app.fileToBase64Success",
//	        "failureCallback": "appzillon.app.fileToBase64Fail",
//	        "filePath": path
//	        
//	    };

	Android.fileToBase64(JSON.stringify(jsonobj));
}

//3.1.2 Plugins

appzillon.plugin.openFile = function(jsonObj){   // plugin name need to change
	Android.loadFile(JSON.stringify(jsonObj));
}

appzillon.plugin.startBatteryMonitor = function(jsonObj){
	if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
        appzillon.util.displayMessage("APZ-CNT-166",null);
	  } else if (jsonObj.failureCallback == undefined   || jsonObj.failureCallback.trim() == "") {
        appzillon.util.displayMessage("APZ-CNT-167", null);
	  } else if (jsonObj.level == undefined   || jsonObj.level.trim() == "") {
        appzillon.util.displayMessage("APZ-CNT-237", null);
	  } else if (jsonObj.state == undefined   || jsonObj.state.trim() == "") {
        appzillon.util.displayMessage("APZ-CNT-238", null);
	  } else if(!appzillon.plugin.validateinrange(0,jsonObj.threshold, 100)){
		  appzillon.util.displayMessage("APZ-CNT-240", null);
	  }else if(!appzillon.plugin.validateinrange(0,jsonObj.time, 100)){
		  appzillon.util.displayMessage("APZ-CNT-241", null);
	  }else{
		  Android.registorBatteryListener(JSON.stringify(jsonObj),"Yes");
	  } 	
}

appzillon.plugin.stopBatteryMonitor = function(jsonObj){
	if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
        appzillon.util.displayMessage("APZ-CNT-166",null);
	  } else if (jsonObj.failureCallback == undefined   || jsonObj.failureCallback.trim() == "") {
        appzillon.util.displayMessage("APZ-CNT-167", null);
	  } else if (jsonObj.level == undefined   || jsonObj.level.trim() == "") {
        appzillon.util.displayMessage("APZ-CNT-237", null);
	  } else if (jsonObj.state == undefined   || jsonObj.state.trim() == "") {
        appzillon.util.displayMessage("APZ-CNT-238", null);
	  }else if (jsonObj.threshold == undefined   || jsonObj.level.trim() == "") {
        appzillon.util.displayMessage("APZ-CNT-237", null);
	  } else if (jsonObj.time == undefined   || jsonObj.state.trim() == "") {
        appzillon.util.displayMessage("APZ-CNT-238", null);
	  } else{
		  Android.registorBatteryListener(JSON.stringify(jsonObj),"No");
	  } 	
}

appzillon.plugin.zip = function(jsonObj){
	  if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
          appzillon.util.displayMessage("APZ-CNT-166",null);
	  } else if (jsonObj.failureCallback == undefined   || jsonObj.failureCallback.trim() == "") {
          appzillon.util.displayMessage("APZ-CNT-167", null);
	  } else{
          Android.zip(JSON.stringify(jsonObj));
	  }    
}

appzillon.plugin.unzip = function(jsonObj) {
	if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else if (jsonObj.failureCallback == undefined	|| jsonObj.failureCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-167", null);
	} else {
		Android.unzip(JSON.stringify(jsonObj));
	}
}

appzillon.plugin.print = function(jsonObj){
	Android.printDoc(JSON.stringify(jsonObj));
}

appzillon.plugin.enablePullDown = function(jsonObj){
	if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
	appzillon.util.displayMessage("APZ-CNT-166", null);
	} else if (jsonObj.failureCallback == undefined	|| jsonObj.failureCallback.trim() == "") {
	appzillon.util.displayMessage("APZ-CNT-167", null);
	} else {
	Android.enableSwipeLayout(JSON.stringify(jsonObj));
	}
}

appzillon.plugin.disablePullDown = function(jsonObj){
	if (jsonObj.successCallback == undefined || jsonObj.successCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-166", null);
	} else if (jsonObj.failureCallback == undefined	|| jsonObj.failureCallback.trim() == "") {
		appzillon.util.displayMessage("APZ-CNT-167", null);
	} else {
		Android.disableSwipeLayout(JSON.stringify(jsonObj));
	}
}

//TODO Can be renamed
appzillon.plugin.hideRefresh = function(){
	Android.hideRefreshIcon();
}

appzillon.plugin.getIP = function(json){
	 Android.getIP(JSON.stringify(json));
}

appzillon.plugin.fetchContact = function(json){
	Android.fetchContact(JSON.stringify(json));
}

appzillon.plugin.twitterLogin = function(json){
	Android.twitterAuth(JSON.stringify(json));
}

appzillon.plugin.youtube = function(json){
	Android.openYoutubeApp(JSON.stringify(json));
}

appzillon.plugin.openUrl = function(json){
	Android.openBrowser(JSON.stringify(json));
}

appzillon.plugin.startCallListener = function(){
	Android.enableListener();
}

appzillon.plugin.testConnection = function(){
	Android.testConnection();
}

appzillon.plugin.scanFinger = function(json){
	Android.scanFinger(JSON.stringify(json));
}

appzillon.plugin.getfileSize = function(json){
	Android.getfileSize(JSON.stringify(json));
}

appzillon.plugin.closeApplicaiton = function(){
	Android.closeApplication();
}
appzillon.plugin.saveBase64ToPdf = function(jsonobj){
	Android.saveBase64ToPdf(JSON.stringify(jsonobj));
}

/*appzillon.plugin.getSpeed = function() {
	Android.getTheSpeed(); // plugin for testing. to be removed, Sid
}*/

appzillon.plugin.updateWhiteList = function(json){
        var jsonStr = {
            "successCallback": json.successCallback,
            "failureCallback": json.failureCallback,
            "id": "12345",
            "updateWhitelist": "Y",
            "databaseName": "APPSDB"            
            };
      if(json.action=="delete"){
        jsonStr.executeQuery="DELETE from TB_URL_WHITELIST where URL='"+json.URL+"'";
        }
      else if(json.action=="insert"){
        jsonStr.executeQuery="INSERT into TB_URL_WHITELIST values('"+json.URL+"')";
      }
    appzillon.plugin.executeSql(jsonStr);
}

appzillon.plugin.callServer = function(preqinterfaceid,prespinterfaceid,preqstr, ppaintresp, pcallid, pasync, pcallback,linternalserverurl){
    appzillon.plugin.preqinterfaceid=preqinterfaceid;
    appzillon.plugin.prespinterfaceid=prespinterfaceid;
    appzillon.plugin.preqstr=preqstr;
    appzillon.plugin.ppaintresp=ppaintresp;
    appzillon.plugin.pcallid=pcallid;
    appzillon.plugin.pasync=pasync;
    appzillon.plugin.pcallback=pcallback;
    var json={};
    json.request=preqstr;
    json.serverurl=linternalserverurl;
    var serverResponse = Android.callServer(JSON.stringify(json));
    if(appzillon.util.isNull(serverResponse)){
    	appzillon.plugin.callServerFailureCallBack();
    }else{
    	appzillon.plugin.callServerSuccessCallBack(JSON.parse(serverResponse));
    }
}
appzillon.plugin.callServerFailureCallBack = function(){
    appzillon.server.postRequestFailure(appzillon.plugin.preqinterfaceid,appzillon.plugin.prespinterfaceid,appzillon.plugin.preqstr,appzillon.plugin.ppaintresp,appzillon.plugin.pcallid,appzillon.plugin.pasync,appzillon.plugin.pcallback);
}

appzillon.plugin.callServerSuccessCallBack = function(responseObj){
    appzillon.server.postRequestSuccess(responseObj,appzillon.plugin.preqinterfaceid,appzillon.plugin.prespinterfaceid,appzillon.plugin.preqstr,appzillon.plugin.ppaintresp,appzillon.plugin.pcallid,appzillon.plugin.pasync,appzillon.plugin.pcallback);
}