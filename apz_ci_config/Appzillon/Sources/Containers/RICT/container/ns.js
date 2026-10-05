Apz.Ns = function (apz) {
	////Core Instance - RICT
	this.apz = apz;
	this.clientNonce = 0;
	this.appzillonSafe = "";
	this.sessionToken = "";
	this.serverNonce = "";
	this.safeToken = APZSAFEKEYTOKEN;
	this.scanneddata = "";
};
///////////////////Prototype Definition///////////////////////
Apz.Ns.prototype = {
	userCB: function (params) {
		if (typeof params.callBack == "function") {
			if (params.callBackObj) {
				params.callBack.call(params.callBackObj, params);
			} else {
				params.callBack(params);
			}
		}
	},
	callNative: function (req) {
		try {
			var res = {};
			res.id = req.id;
			var params = {
				"message": "Plugin not supported for Simulator"
			};
			params.callBack = req.callBack;
			params.callBackObj = req.callBackObj;
			this.apz.dispMsg(params);
			//this.apz.initNativeService(res);
			//Apz.nativeServiceCB(res);
		} catch (e) {
			console.log(e);
		}
	},
	log: function () {
		Bridge.log("logged successfully");
	},
	getDeviceInfo: function (req) {

		////Init
		this.apz.initNativeService(req);
		var ideSim = false;
		if (typeof apzIde !== "undefined") {
			ideSim = true;
		}
		var res = {};
		res.id = req.id;
		res.status = true;
		res.status = true;
		res.deviceType = "RICT";
		res.deviceOs = "RICT";
		res.deviceId = "RICT";
		///Get Screen Size
		if (ideSim) {
			res.screenPpi = apzIde.getScreenPpi();
			res.screenSize = apzIde.getScreenSize();
		} else {
			res.screenPpi = 160;
			res.screenSize = this.apz.getScreenWidth() + "X" + this.apz.getScreenHeight();
		}

		////Register Screen Size
		this.apz.deviceOs = res.deviceOs;
		this.apz.screenSize = res.screenSize;
		this.apz.screenPpi = res.screenPpi;
		///Get Device Group
		res.deviceGroup = this.apz.getDeviceGroup();
		////Populate Lock Rotation
		res.orientation = this.apz.deviceGroupDet.orientation;
		res.lockRotation = true;
		if (res.orientation == "ANY") {
			res.lockRotation = false;
		}
		///Send Orientation to IDE
		if (ideSim) {
			apzIde.setDeviceGroup(res.deviceGroup);
			res.orientation = apzIde.getOrientation();
		} else {
			res.orientation = "PORTRAIT";
		}
		///In Case of Appzillon Simulator Signal Show
		if (ideSim) {
			apzIde.showSimulator();
		}
		////Get Orientation
		////Call CB ( This will be done from Native layer for Mobile Containers)
		///Here we directly call the function
		////CAll BAck..
		Apz.nativeServiceCB(JSON.stringify(res));
	},
	getUserPrefs: function (req) {
		////Init
		this.apz.initNativeService(req);
		var res = {};
		res.id = req.id;
		res.userPrefs = null;
		///Would have read it from projdef
		Apz.nativeServiceCB(JSON.stringify(res));
	},
	getLocation: function (req) {
		this.apz.initNativeService(req);
		var res = {};
		res.id = req.id;
		res.status = true;
		try {
			navigator.geolocation.getCurrentPosition(
				function (position) {
				res.latitude = position.coords.latitude.toString();
				res.longitude = position.coords.longitude.toString();
				Apz.nativeServiceCB(JSON.stringify(res));
			},
				function (error) {
				res.latitude = "0";
				res.longitude = "0";
				Apz.nativeServiceCB(JSON.stringify(res));
			});
		} catch (e) {
			this.callNative(res);
		}
	},
	encryptData: function (req) {
		var key = this.getKey(req.key);
		var encyptedKey = CryptoJS.enc.Utf8.parse(key);
		var plainText = req.plainText;
		var encrypted = CryptoJS.AES.encrypt(plainText, encyptedKey, {
				mode: CryptoJS.mode.CBC,
				padding: CryptoJS.pad.Pkcs7,
				iv: req.ive
			});
		return encrypted.toString();
	},
	decryptData: function (req) {
		var key = this.getKey(req.key);
		var keyt = CryptoJS.enc.Utf8.parse(key);
		var ivet = CryptoJS.enc.Base64.parse(req.ivetoTransit);
		var encrypted = req.plainText;
		var decrypted = CryptoJS.AES.decrypt(encrypted, keyt, {
				mode: CryptoJS.mode.CBC,
				padding: CryptoJS.pad.Pkcs7,
				iv: ivet
			});
		return decrypted.toString(CryptoJS.enc.Utf8);
	},
	getKey: function (key) {
		var updatedKey = key;
		var finalKey;
		if (key.length < 16) {
			for (var i = key.length; i < 16; i++) {
				updatedKey = updatedKey.concat("$");
			}
			finalKey = updatedKey;
		} else if (key.length > 16) {
			var temp = key.substr(0, 16);
			finalKey = temp;
		} else {
			finalKey = key;
		}
		return finalKey;
	},
	refreshServerNonce: function (params) {
		this.clientNonce = 0;
		var request = {};
		request.appzillonGetAppSecTokensRequest = {};
		request.appzillonGetAppSecTokensRequest.appId = this.apz.appId;
		var params = {};
		params.id = "CSNONCE";
		params.req = request;
		params.reqId = this.apz.getProcId();
		params.ifaceName = "appzillonGetAppSecTokens";
		params.internal = true;
		if (!params.callBack) {
			params.callBack = this.refreshServerNonceCB;
			params.callBackObj = this;
		}
		this.apz.server.sendReq(params);
	},
	refreshServerNonceCB: function (params) {
		if (!params.status) {
			params.code = params.errorCode;
			params.callBack = "";
			this.apz.dispMsg(params);
		} else {
			if (params.res.status === "success") {
				this.appzillonSafe = params.res.safeToken;
				this.sessionToken = params.res.sessionToken;
				this.serverNonce = params.res.serverNonce;
			}
			/////Call User Callback..
			if (this.apz.isFunction(params.userCallBack)) {
				if (params.userCallBackObj) {
					params.userCallBack.call(params.userCallBackObj, params);
				} else {
					params.userCallBack(params);
				}
			}
		}
	},
	hashSHA256: function (req) {
		////Init
		this.apz.initNativeService(req);
		var hash = '';
		try {
			hash = apzIde.hashSHA256(req.body.text, req.body.salt);
		} catch (e) {}
		var res = {};
		res.id = req.id;
		res.status = true;
		res.text = hash;
		////Call BAck..
		Apz.nativeServiceCB(JSON.stringify(res));
	},
	hashPwd: function (req) {
		////Init
		this.apz.initNativeService(req);
		var hash = req.pwd;
		try {
			hash = apzIde.hashPwd(req.userId, req.pwd, req.serverToken, req.deviceId, req.date);
		} catch (e) {}
		var res = {};
		res.id = req.id;
		res.status = true;
		res.text = hash;
		////Call Back..
		Apz.nativeServiceCB(JSON.stringify(res));
	},
	showSplash: function (req) {
		//this.callNative(req);
	},
	hideSplash: function (req) {
		//Nothing To Do
	},
	startOrientationListener: function (req) {
		this.apz.initNativeService(req);
	},
	nativeServiceExt: function (req) {
		this.callNative(req);
	},
	openCamera: function (params) {
		/// Expects callBack, callBackObj
		var apzObj = this;
		var requestParam = {}
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		navigator.camera.getPicture(successCB, errorCB, requestParam);
	},
	startBeacon: function (req) {
		this.callNative(req);
	},
	stopBeacon: function (req) {
		this.callNative(req);
	},
	callNumber: function (req) {
		this.callNative(req);
	},
	base64ToFile: function (req) {
		this.callNative(req);
	},
	scanFinger: function (req) {
		this.callNative(req);
	},
	openUrl: function (req) {
		this.callNative(req);
	},
	fileToBase64: function (req) {
		this.callNative(req);
	},
	getFileSize: function (req) {
		this.callNative(req);
	},
	launchWebview: function (req) {
		this.callNative(req);
	},
	closeWebview: function (req) {
		this.callNative(req);
	},
	getInboxSMS: function (req) {
		this.callNative(req);
	},
	setRingtone: function (req) {
		this.callNative(req);
	},
	startCallListener: function (req) {
		this.callNative(req);
	},
	getMissedCalls: function (req) {
		this.callNative(req);
	},
	zip: function (req) {
		this.callNative(req);
	},
	unzip: function (req) {
		this.callNative(req);
	},
	startAccelerometer: function (req) {
		this.callNative(req);
	},
	stopAccelerometer: function (req) {
		this.callNative(req);
	},
	scanBarcode: function (params) {
		// Expects callBack, callBackObj
		var apzObj = this;
		var requestParam = {}
		var successCB = function (response) {
			this.scanneddata = response.data;
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		BarcodePlugin.scan(successCB, errorCB, requestParam);
	},
	printBarcode: function (params) {
		// Expects callBack, callBackObj
		var apzObj = this;
		var requestParam = {};
		requestParam.scannedData = this.scanneddata;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		BarcodePlugin.printBarcode(successCB, errorCB, requestParam);
	},
	scanA4: function (params) {
		// Expects callBack, callBackObj
		var apzObj = this;
		var requestParam = {}
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		BarcodePlugin.scanA4(successCB, errorCB, requestParam);
	},
	magneticSwipe: function (params) {
		// Expects callBack, callBackObj
		var apzObj = this;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var jargs = {}
		MagneticcardPlugin.swipe(successCB, errorCB, jargs);
	},
	eSignature: function (params) {
		/// Expectes path, callBack, callBackObj
		var apzObj = this;
		var successcallback = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		}
		var failurecallback = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		}
		var jargsForEsign = {
			filePath: params.path
		};
		EsignaturePlugin.captureSign(successcallback, failurecallback, jargsForEsign);
	},
	loadMasterKey: function (params) {
		//// Expects masterKey , masterKeyLength, masterKeyIndex, callBack, callBackObj
		var apzObj = this;
		var success = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var failure = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var jargs = {
			"master_key": params.masterKey,
			"master_key_length": params.masterKeyLength,
			"pin_mk_index": params.masterKeyIndex
		}
		PinPadPlugin.loadMasterKey(success, failure, jargs);
	},
	loadSessionKey: function (params) {
		//// Expects sessionKey , sessionKeyLen, sessionKeyIndex , masterKeyIndex, callBack, callBackObj
		var apzObj = this;
		var success = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var failure = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var jargs = {
			"p_sk_key": params.sessionKey,
			"sk_length": params.sessionKeyLen,
			"session_key_index": params.sessionKeyIndex,
			"master_key_index": params.masterKeyIndex
		}
		PinPadPlugin.loadSessionKey(success, failure, jargs);
	},
	getEncPinBlock: function (params) {
		/// Expects  cardNum , cardNumLen, sessionKeyIndex, amount, callBack, callBackObj
		var apzObj = this;
		var success = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var failure = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var jargs = {
			"card_number": params.cardNum,
			"card_length": params.cardNumLen,
			"session_key_index": params.sessionKeyIndex,
			"amount": params.amount
		}
		PinPadPlugin.getEncPinBlock(success, failure, jargs);
	},
	printtext: function (params) {
		/// Expects printText, callBack, callBackObj
		var apzObj = this;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var requestParams = {
			"text": params.printText
		}
		PrinterPlugin.printtext(successCB, errorCB, requestParams);
	},
	printbmppathimg: function (params) {
		/// Expects path, callBack, callBackObj
		var apzObj = this;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var requestParams = {
			"path": params.path
		}
		PrinterPlugin.printbmppathimg(successCB, errorCB, requestParams);
	},
	printImage: function (params) {
		//// Expects  imgText (in base 64 format) , width, height, path, callBack, callBackObj
		var apzObj = this;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var jargs = {
			"text": params.imgText,
			"width": params.width,
			"height": params.height,
			"path": params.path
		};
		PrinterPlugin.printimg(successCB, errorCB, jargs);
	},
	createCalendarEvent: function (req) {
		this.callNative(req);
	},
	editCalendarEvent: function (req) {
		this.callNative(req);
	},
	deleteCalendarEvent: function (req) {
		this.callNative(req);
	},
	startCompass: function (req) {
		this.callNative(req);
	},
	stopCompass: function (req) {
		this.callNative(req);
	},
	executeSql: function (req) {
		this.callNative(req);
	},
	deviceDetails: function (req) {
		this.callNative(req);
	},
	startBatteryMonitor: function (req) {
		this.callNative(req);
	},
	stopBatteryMonitor: function (req) {
		this.callNative(req);
	},
	encryptFile: function (params) {
		/// Expects fileName , inpFilePath , outFilePath, callBack, callBackObj
		var apzObj = this;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var jargsForEncrypt = {
			fileName: params.fileName,
			inputFilePath: params.inpFilePath,
			outputFilePath: params.outFilePath
		}
		FileOperationPlugin.encryptFile(successCB, errorCB, jargsForEncrypt);
	},
	decryptFile: function (params) {
		/// Expects fileName , inpFilePath , outFilePath, callBack, callBackObj
		var apzObj = this;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var jargsForDecrypt = {
			fileName: params.fileName,
			filePath: params.inpFilePath,
			outputFilePath: params.outFilePath
		}
		FileOperationPlugin.decryptFile(successCB, errorCB, jargsForDecrypt);
	},
	compress: function (params) {
		/// Expects operationType , inpFilesWithDelimeter , outputFileName, callBack, callBackObj
		var apzObj = this;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var jargsForCompress = {
			operationType: params.operationType,
			outputFileName: params.outputFileName,
			inputFilesWithDelimeter: params.inpFilesWithDelimeter
		}
		FileOperationPlugin.compress(successCB, errorCB, jargsForCompress);
	},
	extract: function (params) {
		/// Expects operationType , inputFileName, callBack, callBackObj
		var apzObj = this;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var jargsForExtract = {
			operationType: params.operationType,
			inputFileName: params.inputFileName,
			inputFilesWithDelimeter: "dummy"
		}
		FileOperationPlugin.extract(successCB, errorCB, jargsForExtract);
	},
	sessionKey: function () {
		return Bridge.sessionKey;
	},
	deviceId: function () {
		return Bridge.deviceId;
	},
	getUserDetails: function (params) {
		/// Expects callBack, callBackObj
		var apzObj = this;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var jargs = {}
		Bridge.getUserDetails(successCB, errorCB, jargs);
	},
	geofencing: function (req) {
		this.callNative(req);
	},
	startLocationTracking: function (req) {
		this.callNative(req);
	},
	stopLocationTracking: function (req) {
		this.callNative(req);
	},
	startGesture: function (req) {
		this.callNative(req);
	},
	stopGesture: function (req) {
		this.callNative(req);
	},
	fileBrowser: function (req) {
		this.callNative(req);
	},
	getFileContent: function (req) {
		this.callNative(req);
	},
	createFile: function (req) {
		this.callNative(req);
	},
	deleteFile: function (req) {
		this.callNative(req);
	},
	openFile: function (req) {
		this.callNative(req);
	},
	sendMail: function (req) {
		this.callNative(req);
	},
	addContact: function (req) {
		this.callNative(req);
	},
	deleteContact: function (req) {
		this.callNative(req);
	},
	searchContact: function (req) {
		this.callNative(req);
	},
	editContact: function (req) {
		this.callNative(req);
	},
	fetchContact: function (req) {
		this.callNative(req);
	},
	drivingDirection: function (req) {
		this.callNative(req);
	},
	loadMap: function (req) {
		this.callNative(req);
	},
	locationSelector: function (req) {
		this.callNative(req);
	},
	currentLocale: function (req) {
		this.callNative(req);
	},
	saveReport: function (req) {
		this.callNative(req);
	},
	sendReq: function (req) {
		var params = req.params;
		var reqFull = params.reqFull;
		var gAccessToken = this.getAccessToken(reqFull);
		this.apz.initNativeService(req);
		if (params.reqFull.appzillonHeader.interfaceId != "appzillonGetAppSecTokens") {
			params.reqFull.appzillonHeader.clientNonce = this.clientNonce;
			this.clientNonce += 1;
		}
		params.reqFull.appzillonHeader.serverNonce = this.serverNonce;
		params.reqFull.appzillonHeader.sessionToken = this.sessionToken;
		if (this.apz.encryption === "Y") {
			var encData = {};
			encData.key = this.safeToken;
			encData.ive = CryptoJS.lib.WordArray.random(128 / 8);
			encData.plainText = JSON.stringify(params.reqFull.appzillonHeader);
			params.reqFull.appzillonHeader = this.encryptData(encData);
			encData.plainText = JSON.stringify(params.reqFull.appzillonBody);
			params.reqFull.appzillonBody = this.encryptData(encData);
			params.reqFull.appzillonSafe = CryptoJS.enc.Base64.stringify(encData.ive);
		}
		var agrs = {
			url: HttpPlugin.serviceUrl,
			type: 1,
			headers: {
				"Content-Type": "application/json",
				"Accept": "application/json",
				"token": gAccessToken,
				"device_id": Bridge.deviceId,
				"randomValue": Bridge.getRandomNum(),
				"service": "Appzillon",
				"shiroAuthId": HttpPlugin.shiroAuthId,
				"shiroPassword": HttpPlugin.shiroPassword
			},
			attachments: [{
					header: {
						"Content-Type": "application/json",
						"Content-Transfer-Encoding": "binary",
						"Content-ID": "admin",
						"Key": "APZCNTR",
						"Value": "APZRICT"
					},
					payload: reqFull
				}
			]
		}
		var successCB = function (resp) {
			params.status = true;
			params.resFull = resp.attachments;
			try{
				if (this.apz.encryption === "Y") {
					var decData = {};
					decData.key = this.safeToken;
					decData.ivetoTransit = params.resFull.appzillonSafe;
					decData.plainText = params.resFull.appzillonHeader;
					params.resFull.appzillonHeader = JSON.parse(this.decryptData(decData));
					decData.plainText = params.resFull.appzillonBody;
					params.resFull.appzillonBody = JSON.parse(this.decryptData(decData));
					if(params.resFull.appzillonErrors){
						decData.plainText = params.resFull.appzillonErrors;
						params.resFull.appzillonErrors = JSON.parse(this.decryptData(decData));
					}
				}
			} catch(e){
				req.errorCode = "APZ-CNT-330";
				req.status = false;
			}
			Apz.nativeServiceCB(req);
		}
		var failureCB = function (resp) {
			params.status = false;
			params.resFull = resp;
			Apz.nativeServiceCB(req);
		}
		HttpPlugin.syncPost(successCB, failureCB, agrs);
	},
	getAccessToken: function (req) {
		/// Expects username, password
		var accessToken = "";
		var successCB = function (resp) {
			accessToken = resp.attachments.access_token;
		}
		var failureCB = function (resp) {
			alert(JSON.stringify(resp));
		}
		HttpPlugin.getOauthAccessToken(successCB, failureCB, req);
		return accessToken;
	},
	signaturePad: function (req) {
		this.callNative(req);
	},
	facebookLogin: function (req) {
		this.callNative(req);
	},
	googleLogin: function (req) {
		this.callNative(req);
	},
	linkedinLogin: function (req) {
		this.callNative(req);
	},
	twitterLogin: function (req) {
		this.callNative(req);
	},
	youtube: function (req) {
		this.callNative(req);
	},
	startIdleTimer: function (req) {
		this.callNative(req);
	},
	lockRotation: function (req) {
		this.callNative(req);
	},
	unlockRotation: function (req) {
		this.callNative(req);
	},
	setOrientation: function (req) {
		this.callNative(req);
	},
	vibrate: function (req) {
		this.callNative(req);
	},
	voice: function (req) {
		this.callNative(req);
	},
	detectEvents: function (req) {
		this.callNative(req);
	},
	wipeOut: function (req) {
		this.callNative(req);
	},
	getIP: function (req) {
		this.callNative(req);
	},
	getAppVersion: function (req) {
		this.callNative(req);
	},
	closeApplication: function (params) {
		// Expects callBack, callBackObj
		var apzObj = this;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var jargs = {};
		navigator.app.exitApp(successCB, errorCB, jargs);
	},
	multiviewOpen: function (req) {
		this.callNative(req);
	},
	multiviewClose: function (req) {
		this.callNative(req);
	},
	captureNotes: function (req) {
		this.callNative(req);
	},
	enablePullDown: function (req) {
		this.callNative(req);
	},
	disablePullDown: function (req) {
		this.callNative(req);
	},
	hideRefresh: function (req) {
		this.callNative(req);
	},
	startAugmentation: function (req) {
		this.callNative(req);
	},
	reloadAugmentation: function (req) {
		this.callNative(req);
	},
	videoRecording: function (req) {
		this.callNative(req);
	},
	audio: function (params) {
		/// Expectes path, callBack, callBackObj
		var apzObj = this;
		var successCB = function (response) {
			params.status = true;
			params.res = response;
			apzObj.userCB(params);
		};
		var errorCB = function (response) {
			params.status = false;
			params.res = response;
			apzObj.userCB(params);
		};
		var requestParams = {
			"path": params.path
		}
		PlayaudioPlugin.play(successCB, errorCB, requestParams);
	},
	getNotification: function (req) {
		this.callNative(req);
	},
	deleteNotification: function (req) {
		this.callNative(req);
	},
	updateNotification: function (req) {
		this.callNative(req);
	},
	launchApp: function (req) {
		this.callNative(req);
	},
	subappDelete: function (req) {
		this.callNative(req);
	},
	getInstructions: function (req) {
		this.callNative(req);
	},
	upgradeRequired: function (req) {
		this.callNative(req);
	},
	upgradeApp: function (req) {
		this.callNative(req);
	},
	setUserPrefs: function (req) {
		this.callNative(req);
	},
	offlineData: function (req) {
		this.callNative(req);
	},
	smsSend: function (req) {
		this.callNative(req);
	},
	startSMSListener: function (req) {
		this.callNative(req);
	},
	stopSMSListener: function (req) {
		this.callNative(req);
	},
	store: function (req) {
		this.callNative(req);
	},
	retrieve: function (req) {
		this.callNative(req);
	},
	startKeyboardListener: function (req) {
		this.callNative(req);
	},
	stopKeyboardListener: function (req) {
		this.callNative(req);
	},
	startNotificationListener: function (req) {
		this.callNative(req);
	},
	stopNotificationListener: function (req) {
		this.callNative(req);
	},
	getPref: function (req) {
		this.callNative(req);
	},
	setPref: function (req) {
		this.callNative(req);
	},
	callNativeCBwithErrorCode: function (req) {
		this.callNative(req);
	},
	biometricAuth: function (req) {
		this.callNative(req);
	},
	whatsApp: function (req) {
		this.callNative(req);
	},
	getSimInfo: function (req) {
		this.callNative(req);
	},
	sendSmsBySID: function (req) {
		this.callNative(req);
	},
	documentScanner: function (req) {
		this.callNative(req);
	},
	uploadFile: function (req) {
		this.callNative(req);
	},
	downloadFile: function (req) {
		this.callNative(req);
	},
	sendNFC: function (req) {
		this.callNative(req);
	},
	receiveNFC: function (req) {
		this.callNative(req);
	},
	stopNFC: function (req) {
		this.callNative(req);
	},
	readFile: function (req) {
		this.callNative(req);
	},
	printFile: function (req) {
		this.callNative(req);
	},
	printScreen: function (req) {
		this.callNative(req);
	},
	makeSkypeCall: function (req) {
		this.callNative(req);
	},
	deepLinking: function (req) {
		this.callNative(req);
	}
};
