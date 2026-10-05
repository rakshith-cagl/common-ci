Apz.Ns = function(apz) {
	// //Core Instance
	this.apz = apz;
	this.servletUrl = "AppzillonWeb";
	this.sNonce = "";
	this.apz.deviceId = "WEB";
	this.apz.exchange = "";
	// //Initialize Paths for Web
};

function asyncGeneratorStep(gen, resolve, reject, _next, _throw, key, arg) {
	try {
	  var info = gen[key](arg);
	  var value = info.value;
	} catch (error) {
	  reject(error);
	  return;
	}
	if (info.done) {
	  resolve(value);
	} else {
	  Promise.resolve(value).then(_next, _throw);
	}
  }

  function _asyncToGenerator(fn) {
	return function () {
	  var self = this,
		args = arguments;
	  return new Promise(function (resolve, reject) {
		var gen = fn.apply(self, args);
		function _next(value) {
		  asyncGeneratorStep(gen, resolve, reject, _next, _throw, "next", value);
		}
		function _throw(err) {
		  asyncGeneratorStep(gen, resolve, reject, _next, _throw, "throw", err);
		}
		_next(undefined);
	  });
	};
  }
// /////////////////Prototype Definition///////////////////////
Apz.Ns.prototype = {
	/*
	 * log : function(msg,type) { if ( typeof apzIde !== "undefined") {
	 * apzIde.log(msg); } else { console.log(msg); } },
	 */
	getDeviceInfo : function(req) {
		// appzillon.data.getDeviceInfoCB(res);
		// //Init
		this.apz.initNativeService(req);
		var ideSim = false;
		if (typeof apzIde !== "undefined") {
			simType = true;
		}
		var res = {};
		res.id = req.id;
		res.status = true;
		res.status = true;
		res.deviceType = "WEB";
		res.deviceOs = "WEB";
		res.deviceId = "WEB";
		// /Get Screen Size
		if (ideSim) {
			res.screenPpi = apzIde.getScreenPpi();
			res.screenSize = apzIde.getScreenSize();
		} else {
			res.screenPpi = 160;
			res.screenSize = this.apz.getScreenWidth() + "X"
					+ this.apz.getScreenHeight();
		}
		// //Register Screen Size
		this.apz.deviceOs = res.deviceOs;
		this.apz.screenSize = res.screenSize;
		this.apz.screenPpi = res.screenPpi;
		// /Get Device Group
		res.deviceGroup = this.apz.getDeviceGroup();
		// //Populate Lock Rotation
		res.orientation = this.apz.deviceGroupDet.orientation;
		res.lockRotation = true;
		if (res.orientation == "ANY") {
			res.lockRotation = false;
		}
		// /Send Orientation to IDE
		if (ideSim) {
			apzIde.setDeviceGroup(res.deviceGroup);
			res.orientation = apzIde.getOrientation();
		} else {
			res.orientation = "PORTRAIT";
		}
		// /In Case of Appzillon Simulator Signal Show
		if (ideSim) {
			apzIde.showSimulator();
		}
		// //Get Orientation
		// //Call CB ( This will be done from Native layer for Mobile
		// Containers)
		// /Here we directly call the function
		// //CAll BAck..
		Apz.nativeServiceCB(JSON.stringify(res));
	},
	getUserPrefs : function(json) {
		if (this.apz.isNull(this.apz.userId)) {
			var respObj = apzargs.settings;
			json.userPrefs = respObj;
			json.callBack(json);
		} else {
			var params = {};
			params.id = json.id;
			params.callBack = json.callBack;
			params.callBackObj = json.callBackObj;
			params.req = {};
			params.req.actionId = "LOADSETTINGS";
			this.callServlet(params);
		}
	},
	setUserPrefs : function(json) {
		var params = {};
		params.id = json.id;
		params.callBack = json.callBack;
		params.callBackObj = json.callBackObj;
		params.req = {};
		params.req.userPrefs = json.userPrefs;
		params.req.actionId = "USERSETTINGS";
		this.callServlet(params);
	},
	getLocation : function(req) {
		this.apz.initNativeService(req);
		var res = {};
		res.id = req.id;
		res.status = true;
		navigator.geolocation.getCurrentPosition(function(position) {
			res.latitude = position.coords.latitude.toString();
			res.longitude = position.coords.longitude.toString();
			Apz.nativeServiceCB(JSON.stringify(res));
		}, function(error) {
			res.latitude = "0";
			res.longitude = "0";
			Apz.nativeServiceCB(JSON.stringify(res));
		});
	},
	apzGetKey : function() {
		var json = {};
		var request = {};
		request.actionId = "appzillonGetKey";
		json.req = request;
		json.ifaceId = "appzillonGetKey";
		json.callBack = this.apzGetKeyCB;
		json.callBackObj = this;
		json.async = false;
		this.callServlet(json);
	},
	apzGetKeyCB : function(response) {
		this.apz.exchange = response.params.resFull.exchange;
	},
	encryptData : function(json) {
		if (json.hasOwnProperty("fwdData")
				&& json.fwdData.hasOwnProperty("ifaceName")
				&& json.fwdData.ifaceName == "appzillonChangePassword") {
			var params = {};
			params.status = true;
			params.id = json.id;
			params.text = json.fwdData.newPassword;
			this.apz.initNativeService(json);
			Apz.nativeServiceCB(JSON.stringify(params));
		} else {
			this.callNative(json);
		}
	},
	encryptRequest : function(req) {
		var key = this.getKey(req.key);
		var encyptedKey = CryptoJS.enc.Utf8.parse(key);
		var plainText = req.plainText;
		var encrypted = CryptoJS.AES.encrypt(plainText, encyptedKey, {
			mode : CryptoJS.mode.CBC,
			padding : CryptoJS.pad.Pkcs7,
			iv : req.ive
		});
		return encrypted.toString();
	},

	encryptRequestAES_GCM: (function () {
		var _encryptRequestAES_GCM = _asyncToGenerator(
		  /*#__PURE__*/ regeneratorRuntime.mark(function _callee3(req) {
			var key,
			  pwUtf8,
			  imkey,
			  plainText,
			  ptutf8,
			  cipherText_buffer,
			  cipherText_Uint8,
			  cipherText_str,
			  cipherText_base64;
			return regeneratorRuntime.wrap(
			  function _callee3$(_context3) {
				while (1) {
				  switch ((_context3.prev = _context3.next)) {
					case 0:
					  key = this.getKey(req.key); //encode the key into utf-8
	
					  pwUtf8 = new TextEncoder().encode(key); //generate key using password
	
					  _context3.next = 4;
					  return crypto.subtle.importKey(
						"raw",
						pwUtf8,
						"AES-GCM",
						false,
						["encrypt"]
					  );
	
					case 4:
					  imkey = _context3.sent;
					  plainText = req.plainText; //encoding plaintext as utf8
	
					  ptutf8 = new TextEncoder().encode(plainText); //encryption using the key and iv
	
					  _context3.next = 9;
					  return crypto.subtle.encrypt(
						{
						  name: "AES-GCM",
						  iv: req.iv
						},
						imkey,
						ptutf8
					  );
	
					case 9:
					  cipherText_buffer = _context3.sent;
					  //cipherText as Uint8Array
					  cipherText_Uint8 = Array.from(
						new Uint8Array(cipherText_buffer)
					  ); //cipherText as string
	
					  cipherText_str = cipherText_Uint8
						.map(function (b) {
						  return String.fromCharCode(b);
						})
						.join(""); //encoding the cipherText as base64
	
					  cipherText_base64 = btoa(cipherText_str);
					  return _context3.abrupt("return", cipherText_base64);
	
					case 14:
					case "end":
					  return _context3.stop();
				  }
				}
			  },
			  _callee3,
			  this
			);
		  })
		);
	
		function encryptRequestAES_GCM(_x3) {
		  return _encryptRequestAES_GCM.apply(this, arguments);
		}
	
		return encryptRequestAES_GCM;
	  })(),

	  decryptResponseAES_GCM: (function () {
		var _decryptResponseAES_GCM = _asyncToGenerator(
		  /*#__PURE__*/ regeneratorRuntime.mark(function _callee4(req) {
			var key,
			  pwUtf8,
			  iv_hex,
			  iv,
			  encrypted,
			  encrypted_str,
			  encrypted_utf8,
			  imkey,
			  plainText_buffer,
			  plainText;
			return regeneratorRuntime.wrap(
			  function _callee4$(_context4) {
				while (1) {
				  switch ((_context4.prev = _context4.next)) {
					case 0:
					  key = this.getKey(req.key);
					  pwUtf8 = new TextEncoder().encode(key);
					  iv_hex = req.decryptedText.slice(0, 24);
					  iv = new Uint8Array(
						iv_hex.match(/.{2}/g).map(function (hex) {
						  return parseInt(hex, 16);
						})
					  );
					  encrypted = req.decryptedText.slice(24);
					  encrypted_str = atob(encrypted);
					  encrypted_utf8 = encrypted_str
						.match(/[\s\S]/g)
						.map(function (ch) {
						  return ch.charCodeAt(0);
						});
					  _context4.next = 9;
					  return crypto.subtle.importKey(
						"raw",
						pwUtf8,
						"AES-GCM",
						false,
						["decrypt"]
					  );
	
					case 9:
					  imkey = _context4.sent;
					  _context4.next = 12;
					  return crypto.subtle.decrypt(
						{
						  name: "AES-GCM",
						  iv: iv
						},
						imkey,
						new Uint8Array(encrypted_utf8)
					  );
	
					case 12:
					  plainText_buffer = _context4.sent;
					  plainText = new TextDecoder().decode(plainText_buffer);
					  return _context4.abrupt("return", plainText);
	
					case 15:
					case "end":
					  return _context4.stop();
				  }
				}
			  },
			  _callee4,
			  this
			);
		  })
		);
	
		function decryptResponseAES_GCM(_x4) {
		  return _decryptResponseAES_GCM.apply(this, arguments);
		}
	
		return decryptResponseAES_GCM;
	  })(),

	decryptData : function(params) {
		this.callNative(params);
	},
	decryptResponse : function(req) {
		var key = this.getKey(req.key);
		var keyt = CryptoJS.enc.Utf8.parse(key);
		var ivet = CryptoJS.enc.Base64.parse(req.ivetoTransit);
		var encrypted = req.decryptedText;
		var decrypted = CryptoJS.AES.decrypt(encrypted, keyt, {
			mode : CryptoJS.mode.CBC,
			padding : CryptoJS.pad.Pkcs7,
			iv : ivet
		});
		return decrypted.toString(CryptoJS.enc.Utf8);
	},
	getAppSecTokens : function(json) {
		var reqId = this.apz.getProcId();
		var request = {};
		request.appzillonGetAppSecTokensRequest = {};
		request.appzillonGetAppSecTokensRequest.appId = json.appId || apz.appId;
		if (json.id == undefined) {
			json.id = "CSNONCE";
		}
		json.req = request;
		json.reqId = reqId;
		json.ifaceId = "appzillonGetAppSecTokens";
		json.internal = true;
		json.async = false;
		if (json.callBack == undefined) {
			json.callBack = this.getAppSecTokensCB;
			json.callBackObj = this;
		}
		this.callServlet(json);
	},
	getAppSecTokensCB : function(params) {
		if (!params.status) {
			params.code = params.params.resFull.appzillonErrors[0].errorCode;
			params.callBack = ""; // revisit
			this.apz.dispMsg(params);
		} else {
			this.sNonce = params.params.resFull.appzillonBody.appzillonGetAppSecTokensResponse.serverNonce;
			//Changes for Citi
			  if(params.id == "startUp"){
          			this.apz.loadProject();
        		  	}
		}
	},
	rememberMe : function() {
		var json = {};
		var request = {};
		request.actionId = "appzillonCheckCookie";
		json.req = request;
		json.ifaceId = "appzillonReLoginRequest";
		json.callBack = this.rememberMeCB;
		json.callBackObj = this;
		this.callServlet(json);
	},
	rememberMeCB : function(json) {
		if (json.params.resFull.appzillonBody.loginResponse
				&& json.params.resFull.appzillonBody.loginResponse.status) {
			this.loginSuccess = true;
			var response = {};
			response.res = json.params.resFull.appzillonBody;
			apz.server.loginCB(response);
		}
		apz.loadProject();
	},
	hashSHA256 : function(req) {
		// //Init
		this.apz.initNativeService(req);
		var hash = text;
		try {
			hash = appzillonide.hashSHA256(req.body.text, req.body.salt);
		} catch (e) {
		}
		var res = {};
		res.id = req.id;
		res.status = true;
		res.text = hash;
		// //Call BAck..
		Apz.nativeServiceCB(JSON.stringify(res));
	},
	hashPwd : function(req) {
		this.apz.initNativeService(req);
		req.text = req.pwd;
		req.status = true;
		Apz.nativeServiceCB(JSON.stringify(req));
	},
	callServlet : function(json) {
		var request = {};
		var reqJson = {};
		var params = {};
		params.ifaceName = json.ifaceId;
		params.scrName = this.apz.currScr;
		params.async = false;
		params.id = json.id;
		params.internal = true;
		request.appzillonHeader = apz.server.getHeader(params);
		request.appzillonBody = json.req;
		json.params = params;
		json.params.method = "POST";
		json.params.req = json.req;
		json.params.reqFull = request;
		this.sendReq(json);
	},
	downloadFile : function(json) {
		var params = {};
		//params.ifaceName = "appzillonFilePushService";
		//Deep changes to support downloading file without session
		params.ifaceName= (json.sessionReq=="Y")?"appzillonFilePushService":"appzillonFilePushServiceWS";
		params.scrName = this.apz.currScr;
		params.async = false;
		params.id = "00NEW";
		params.internal = true;
		var header = apz.server.getHeader(params);
		var request = {};
		request.appzillonHeader = header;
		/////////////changes to pass customInterfaceId in appzillon body///////////////
		if(json.customInterfaceId !== undefined){
			json.interfaceId = json.customInterfaceId; 
			delete json.customInterfaceId;
		}
		request.appzillonBody = json;
		request.appzillonBody.actionId = "DOWNLOADFILE";
		reqjson = JSON.stringify(request);
		if (json.base64 != "Y") {
			var http = new XMLHttpRequest();
			var url = this.servletUrl;
			var data = reqjson;
			http.open("POST", url, true);
			http.setRequestHeader("Content-type", "application/json");
			http.responseType = "arraybuffer";
			http.onreadystatechange = function() {
				if (http.readyState == 4 && http.status == 200) {
					if (http.getResponseHeader("Content-Disposition") != null) {
						var arrayBuffer = http.response;
						var blob = new Blob([ arrayBuffer ], {
							type : http.getResponseHeader("Content-Type")
						});
						var url = URL.createObjectURL(blob);
						var downloadUrl = URL.createObjectURL(blob);
						var a = document.createElement("a");
						a.href = downloadUrl;
						a.download = http.getResponseHeader(
								"Content-Disposition").match(/fileName="(.+)"/)[1];
						$(document.body).append(a);
						a.click();
						if (json.callBack instanceof Function) {
							json.callBack({ status: true });
						}
					} else {
						var arrayBuffer = http.response;
						var respJSON = JSON.parse(atob(btoa(String.fromCharCode
								.apply(null, new Uint8Array(arrayBuffer)))));
						json.callBack(respJSON);
					}
				}
			}
			http.send(reqjson);
		} else {
			$.ajax({
				url : this.servletUrl,
				data : reqjson,
				cache : false,
				contentType : 'application/json',
				dataType : 'json',
				type : 'POST',
				success : function(presp, textStatus, jqXHR) {
					var responseJson = presp;
					if (responseJson.result == "success") {
						var successJSON = {};
						var base64 = responseJson.fileLink;
						successJSON.base64 = base64;
						json.callBack(successJSON);
					} else if (presp.result == "failure") {
						this.apz.dispMsg("APZ-SMS-EX-003");
					} else {
						json.callBack(presp);
					}
				},
				error : function(jqXHR, textStatus, errorMessage) {
					json.callBack(errorMessage);
				},
				async : true
			});
		}
	},

	uploadFile : function(json) {
		if (window.File && window.FileReader && window.FileList && window.Blob) {
			var params = {};
			//params.ifaceName = "appzillonUploadFile";
			//Deep changes to support upload file without session
			params.ifaceName= (json.sessionReq=="Y")?"appzillonUploadFile":"appzillonUploadFileWS";
			params.scrName = this.apz.currScr;
			params.async = false;
			params.id = "00NEW";
			params.internal = true;
			var header = apz.server.getHeader(params);
			header = JSON.stringify(header);
			var formData = new FormData();
			var querySelector = "input[type=file]";
			if ((json.fieldID != undefined) && (json.fieldID != "")) {
				var isInput = $("#" + json.fieldID).is("input");
				if (isInput) {
					querySelector = "#" + json.fieldID;
				} else {
					querySelector = "#" + json.fieldID + " input[type=file]";
				}
			}
			var filesLength = 0;
			filesLength = $(querySelector).bind('change', function() {
				this.files[0].size;
			});
			filesLength = $(querySelector).prop("files").length;
			for (var x = 0; x < filesLength; x++) {
				formData.append("uploadfiles[]",
						$(querySelector).prop("files")[x]);
			}
			if (!apz.isNull(json.interfaceId)) {
				formData.append("interfaceId", json.interfaceId);
			}
			if (!apz.isNull(json.request)) {
				formData.append("request", JSON.stringify(json.request));
			}
			formData.append("destination", json.destination);
			if (apz.isNull(json.overWrite)) {
				formData.append("overWrite", "N");
			} else {
				formData.append("overWrite", json.overWrite);
			}
			formData.append("appzillonHeader", header);
			////////////uploadfile extensibility support//////////////
			if(json.customInterfaceId !== undefined){
				let body = {};
				body.interfaceId = json.customInterfaceId;
                body = JSON.stringify(body);
				formData.append("appzillonBody", body);
			}
			$.ajax({
				url : this.servletUrl,
				data : formData,
				cache : false,
				contentType : false,
				processData : false,
				type : "POST",
				success : function(presp) {
					var responseJson = presp;
					if (responseJson.result == "success") {
						var resp = {};
						resp.successMessage = responseJson.successMessage;
						json.callBack(resp);
					} else {
						json.callBack(responseJson);
					}
				},
				error : function(jqXHR, textStatus, errorMessage) {
					json.callBack(errorMessage);
				},
				async : true

			});
		}
	},
	sendMail : function(json) {
		var requestBody = {
			"appzillonMailRequest" : {
				"emailid" : json.recipientMailId,
				"subject" : json.subject,
				"CC" : json.ccIdList,
				"body" : json.body
			}
		};
		var errorString = {
			"errorCode" : "",
			"errorDescription" : "Unable to send mail",
			"mailId" : json.mailId
		};

		var linterface = json.interfaceID;
		if ((linterface == "") || (linterface == "undefined")
				|| (linterface == undefined) || (linterface == null)) {
			linterface = "appzillonMailRequest";
		}
		var params = {};
		params.ifaceName = "appzillonMailRequest";
		params.scrName = this.apz.currScr;
		params.async = false;
		params.id = "00NEW";
		params.internal = true;
		var header = apz.server.getHeader(params);
		var request = {};
		request.appzillonHeader = header;
		request.appzillonBody = requestBody;
		request.appzillonHeader.clientNonce = this.makeId();
		reqjson = JSON.stringify(request);

		$.ajax({
			url : this.servletUrl,
			data : reqjson,
			cache : false,
			contentType : 'application/json',
			dataType : 'json',
			type : "POST",
			success : function(presp, textStatus, jqXHR) {
				if (presp != '{}') {
					if (presp.appzillonHeader.status) {
						if (presp.appzillonBody.status) {
							var successString = {
								"successMessage" : "Mail Sent",
								"mailId" : json.mailId
							};
							json.callBack(successString);
						} else {
							json.callBack(errorString);
						}
					} else if (presp.appzillonHeader.status == "failure") {
						json.callBack(errorString);
					} else if (presp.result == "failure") {
						this.apz.dispMsg("APZ-SMS-EX-003");
					} else {
						json.callBack(errorString);
					}
				}
			},
			error : function(jqXHR, textStatus, errorMessage) {
				json.callBack(errorString);
			},
			async : true
		});
	},
	loadMap : function(json) {
		this.apz.initNativeService(json);
		var thisVar = this;
		if (json.hasOwnProperty("mapDiv")) {
			requirejs([
					"appzillon/container/initializeMap.js",
					"https://maps.googleapis.com/maps/api/js?&key="
							+ apz.googleMapsKey ], function() {
				params = {};
				params.id = "LOADMAP";
				params.req = json;
				params.req.actionId = "appzillonMaps"
				params.callBackObj = thisVar;
				params.callBack = thisVar.loadMapCB;
				thisVar.callServlet(params);
			})
		} else {
			params = {};
			params.id = "LOADMAP";
			params.req = json;
			params.req.actionId = "appzillonMaps"
			params.callBackObj = thisVar;
			params.callBack = thisVar.loadMapCB;
			thisVar.callServlet(params);
		}

	},
	loadMapCB : function(params) {
		params = params.params;
		if (params.resFull.result == "SUCCESS") {
			try {
				if (params.req.markerInfo.length != 0) {
					if (params.req.hasOwnProperty("mapDiv")) {
						initializationDiv(params.req);
					} else {
						fileURL = window.location.href + "apps/" + apz.appId
								+ "/screens/Map.html";
						var win = window.open(fileURL, '_blank');
						win.mapDataObj = JSON.stringify(params.req);
						win.focus();
					}
					var resultJSON = {
						"successMessage" : "success"
					}
				} else {
					var resultJSON = {
						"errorCode" : "",
						"errorDescription" : "markerInfo array if empty"
					}
				}
			} catch (e) {
				var resultJSON = {
					"errorCode" : "",
					"errorDescription" : "Failed to load Maps"
				}
			}
		} else {
			var resultJSON = {
				"errorCode" : "",
				"errorDescription" : "Failed to load Maps"
			}
		}
		params.req.result = resultJSON;
		Apz.nativeServiceCB(JSON.stringify(params.req));
	},
	drivingDirection : function(json) {
		this.apz.initNativeService(json);
		var thisVar = this;
		if (json.hasOwnProperty("mapDiv")) {
			requirejs([
					"appzillon/container/initializeMap.js",
					"https://maps.googleapis.com/maps/api/js?&key="
							+ apz.googleMapsKey ], function() {
				params = {};
				params.id = "drivingDirection";
				params.req = json;
				params.req.actionId = "appzillonMaps"
				params.callBackObj = thisVar;
				params.callBack = thisVar.drivingDirectionCB;
				thisVar.callServlet(params);
			})
		} else {
			params = {};
			params.id = "drivingDirection";
			params.req = json;
			params.req.actionId = "appzillonMaps"
			params.callBackObj = thisVar;
			params.callBack = thisVar.drivingDirectionCB;
			thisVar.callServlet(params);
		}

	},
	drivingDirectionCB : function(params) {
		params = params.params;
		if (params.resFull.result == "SUCCESS") {
			try {
				if (params.req.hasOwnProperty("mapDiv")) {
					initializationDiv(params.req);
				} else {
					fileURL = window.location.href + "apps/" + apz.appId
							+ "/screens/Map.html";
					var win = window.open(fileURL, '_blank');
					win.mapDataObj = JSON.stringify(params.req);
					win.focus();
				}
				var resultJSON = {
					"successMessage" : "success"
				}
			} catch (e) {
				var resultJSON = {
					"errorCode" : "",
					"errorDescription" : "Failed to load Maps of drivingDirection"
				}
			}
		} else {
			var resultJSON = {
				"errorCode" : "",
				"errorDescription" : "Failed to load Maps"
			}
		}
		params.req.result = resultJSON;
		Apz.nativeServiceCB(JSON.stringify(params.req));
	},
	locationSelector : function(json) {
		this.apz.initNativeService(json);
		var thisVar = this;
		if (json.hasOwnProperty("mapDiv")) {
			requirejs([
					"appzillon/container/initializeMap.js",
					"https://maps.googleapis.com/maps/api/js?&key="
							+ apz.googleMapsKey ], function() {
				var jsonObjV1 = thisVar.validateAreaSelector(json);
				if (jsonObjV1) {
					params = {};
					params.id = "locationSelector";
					params.req = json;
					params.req.actionId = "appzillonMaps";
					params.callBackObj = thisVar;
					params.callBack = thisVar.locationSelectorCB;
					thisVar.callServlet(params);
				}
			})
		} else {
			var jsonObjV1 = thisVar.validateAreaSelector(json);
			if (jsonObjV1) {
				params = {};
				params.id = "locationSelector";
				params.req = json;
				params.req.actionId = "appzillonMaps";
				params.callBackObj = thisVar;
				params.callBack = thisVar.locationSelectorCB;
				thisVar.callServlet(params);
			}

		}
	},
	locationSelectorCB : function(params) {
		params = params.params;
		if (params.resFull.result == "SUCCESS") {
			try {
				if (params.req.hasOwnProperty("mapDiv")) {
					initializationDiv(params.req);
				} else {
					fileURL = window.location.href + "apps/" + apz.appId
							+ "/screens/Map.html";
					var win = window.open(fileURL, '_blank');
					win.mapDataObj = JSON.stringify(params.req);
					win.focus();
				}

				var resultJSON = {
					"successMessage" : "success"
				}
			} catch (e) {
				var resultJSON = {
					"errorCode" : "",
					"errorDescription" : "Failed to load Maps of nearByPlaces"
				}
			}

		} else {
			var resultJSON = {
				"errorCode" : "",
				"errorDescription" : "Failed to load Maps"
			}
		}
		params.req.result = resultJSON;
		Apz.nativeServiceCB(JSON.stringify(params.req));
	},
	validateAreaSelector : function(jsonobject) {
		var lcheck = false;
		var lradiuspresent = this.apz.containsKey(jsonobject, "radius");
		var lnearbyplacespresent = this.apz.containsKey(jsonobject,
				"nearbyplaces");

		if (!lradiuspresent) {
			this.apz.dispMsg("APZ-CNT-179", null);
		} else if (!lnearbyplacespresent) {
			this.apz.dispMsg("APZ-CNT-192", null);
		} else {

			var lradiuschecknull = false;
			var lradiuscheckval = false;
			if (!apz.isNull(jsonobject.radius)) {
				lradiuschecknull = true;
				if (!(isNaN(jsonobject.radius)
						|| jsonobject.radius === Infinity
						|| jsonobject.radius === "Infinity" || jsonobject.radius <= 0)) {
					lradiuscheckval = true;
				}
			}
			var lnearbyplaceschecknull = false;
			var lnearbyplacescheckval = false;
			if (!apz.isNull(jsonobject.nearbyplaces)) {
				lnearbyplaceschecknull = true;
				if (jsonobject.nearbyplaces.constructor === Array) {
					var count = 0;
					for (var i = 0; i < jsonobject.nearbyplaces.length; i++) {
						if (!this.apz
								.isNull(jsonobject.nearbyplaces[i].locationLatitude)
								&& !this.apz
										.isNull(jsonobject.nearbyplaces[i].locationLongitude)) {
							if (this
									.validateInRange(
											-90,
											jsonobject.nearbyplaces[i].locationLatitude,
											90)
									&& this
											.validateInRange(
													-180,
													jsonobject.nearbyplaces[i].locationLongitude,
													180)) {
								count++;
							}
						}
					}
					if (count == jsonobject.nearbyplaces.length)
						lnearbyplacescheckval = true;
				}
			}

			if (!lradiuschecknull) {
				this.apz.dispMsg("APZ-CNT-179", null);
			} else if (!lnearbyplaceschecknull) {
				this.apz.dispMsg("APZ-CNT-192", null);
			} else if (!lradiuscheckval) {
				this.apz.dispMsg("APZ-CNT-180", null);
			} else if (!lnearbyplacescheckval) {
				this.apz.dispMsg("APZ-CNT-092", null);
			} else {
				lcheck = true;
			}
		}
		return lcheck;

	},
	validateInRange : function(min, number, max) {
		if (!isNaN(number) && !(number === "") && !(number === Infinity)
				&& !(number === "Infinity") && (number >= min)
				&& (number <= max)) { // Shirish 31mar
			return true;
		} else {
			return false;
		}
	},
	refreshPageCallback : function() {
		window.location = window.location.origin + window.location.pathname;
	},
	refreshADCallback : function(){
		window.location = window.location.origin + window.location.pathname+"?logOutType=adlogout";
	},
	clearAuthProviderSession: function(){
        window.location = window.location.origin + window.location.pathname+"?action=logout";
    },
	callNative : function(req) {
		try {
			var res = {};
			res.id = req.id;
			var params = {
				"message" : "Plugin not supported for Web"
			};
			params.callBack = req.callBack;
			params.callBackObj = req.callBackObj;
			this.apz.dispMsg(params);
			// this.apz.initNativeService(res);
			// Apz.nativeServiceCB(res);
		} catch (e) {
			console.log(e);
		}
	},
	showSplash : function(params) {
		// this.callNative(params);
	},
	hideSplash : function(params) {
		// this.callNative(params);
	},
	nativeServiceExt : function(params) {
		this.callNative(params);
	},
	openCamera : function(params) {
		this.callNative(params);
	},
	startBeacon : function(params) {
		this.callNative(params);
	},
	stopBeacon : function(params) {
		this.callNative(params);
	},
	callNumber : function(params) {
		this.callNative(params);
	},
	base64ToFile : function(params) {
		this.callNative(params);
	},
	scanFinger : function(params) {
		this.callNative(params);
	},
	openUrl : function(params) {
		this.callNative(params);
	},
	fileToBase64 : function(params) {
		this.callNative(params);
	},
	getFileSize : function(params) {
		this.callNative(params);
	},
	launchWebview : function(params) {
		this.callNative(params);
	},
	closeWebview : function(params) {
		this.callNative(params);
	},
	getInboxSMS : function(params) {
		this.callNative(params);
	},
	setRingtone : function(params) {
		this.callNative(params);
	},
	startCallListener : function(params) {
		this.callNative(params);
	},
	getMissedCalls : function(params) {
		this.callNative(params);
	},
	zip : function(params) {
		this.callNative(params);
	},
	unzip : function(params) {
		this.callNative(params);
	},
	startAccelerometer : function(params) {
		this.callNative(params);
	},
	stopAccelerometer : function(params) {
		this.callNative(params);
	},
	scanBarcode : function(params) {
		this.callNative(params);
	},
	createCalendarEvent : function(params) {
		this.callNative(params);
	},
	editCalendarEvent : function(params) {
		this.callNative(params);
	},
	deleteCalendarEvent : function(params) {
		this.callNative(params);
	},
	startCompass : function(params) {
		this.callNative(params);
	},
	stopCompass : function(params) {
		this.callNative(params);
	},
	executeSql : function(params) {
		this.callNative(params);
	},
	deviceDetails : function(params) {
		this.callNative(params);
	},
	startBatteryMonitor : function(params) {
		this.callNative(params);
	},
	stopBatteryMonitor : function(params) {
		this.callNative(params);
	},
	encryptFile : function(params) {
		this.callNative(params);
	},
	decryptFile : function(params) {
		this.callNative(params);
	},
	geofencing : function(params) {
		this.callNative(params);
	},
	startLocationTracking : function(params) {
		this.callNative(params);
	},
	stopLocationTracking : function(params) {
		this.callNative(params);
	},
	startGesture : function(params) {
		this.callNative(params);
	},
	stopGesture : function(params) {
		this.callNative(params);
	},
	fileBrowser : function(params) {
		this.callNative(params);
	},
	getFileContent : function(params) {
		this.callNative(params);
	},
	createFile : function(params) {
		this.callNative(params);
	},
	deleteFile : function(params) {
		this.callNative(params);
	},
	openFile : function(params) {
		this.callNative(params);
	},
	addContact : function(params) {
		this.callNative(params);
	},
	deleteContact : function(params) {
		this.callNative(params);
	},
	searchContact : function(params) {
		this.callNative(params);
	},
	editContact : function(params) {
		this.callNative(params);
	},
	fetchContact : function(params) {
		this.callNative(params);
	},
	currentLocale : function(params) {
		this.callNative(params);
	},
	saveReport : function(params) {
		this.callNative(params);
	},
	signaturePad : function(params) {
		this.callNative(params);
	},
	facebookLogin : function(params) {
		this.callNative(params);
	},
	googleLogin : function(params) {
		this.callNative(params);
	},
	linkedinLogin : function(params) {
		this.callNative(params);
	},
	twitterLogin : function(params) {
		this.callNative(params);
	},
	youtube : function(params) {
		this.callNative(params);
	},
	startIdleTimer : function(params) {
		this.callNative(params);
	},
	lockRotation : function(params) {
		this.callNative(params);
	},
	unlockRotation : function(params) {
		this.callNative(params);
	},
	setOrientation : function(params) {
		this.callNative(params);
	},
	vibrate : function(params) {
		this.callNative(params);
	},
	voice : function(params) {
		this.callNative(params);
	},
	detectEvents : function(params) {
		this.callNative(params);
	},
	wipeOut : function(params) {
		this.callNative(params);
	},
	getIP : function(params) {
		this.callNative(params);
	},
	getAppVersion : function(params) {
		this.callNative(params);
	},
	closeApplication : function(params) {
		this.callNative(params);
	},
	multiviewOpen : function(params) {
		this.callNative(params);
	},
	multiviewClose : function(params) {
		this.callNative(params);
	},
	captureNotes : function(params) {
		this.callNative(params);
	},
	enablePullDown : function(params) {
		this.callNative(params);
	},
	disablePullDown : function(params) {
		this.callNative(params);
	},
	hideRefresh : function(params) {
		this.callNative(params);
	},
	startAugmentation : function(params) {
		this.callNative(params);
	},
	reloadAugmentation : function(params) {
		this.callNative(params);
	},
	videoRecording : function(params) {
		this.callNative(params);
	},
	getNotification : function(params) {
		this.callNative(params);
	},
	deleteNotification : function(params) {

		this.callNative(params);
	},
	updateNotification : function(params) {
		this.callNative(params);
	},
	launchApp : function(params) {
		this.callNative(params);
	},
	subappDelete : function(params) {
		this.callNative(params);
	},
	getInstructions : function(params) {
		this.callNative(params);
	},
	upgradeRequired : function(params) {
		this.callNative(params);
	},
	upgradeApp : function(params) {
		this.callNative(params);
	},
	offlineData : function(params) {
		this.callNative(params);
	},
	smsSend : function(params) {
		this.callNative(params);
	},
	startSMSListener : function(params) {
		this.callNative(params);
	},
	stopSMSListener : function(params) {
		this.callNative(params);
	},
	startKeyboardListener : function(params) {
		this.callNative(params);
	},
	stopKeyboardListener : function(params) {
		this.callNative(params);
	},
	startOrientationListener : function(params) {
		// this.callNative(params);
	},
	startNotificationListener : function(params) {
		this.callNative(params);
	},
	stopNotificationListener : function(params) {
		this.callNative(params);
	},
	getPref : function(params) {
		this.callNative(params);
	},
	setPref : function(params) {
		this.callNative(params);
	},
	callNativeCBwithErrorCode : function(params) {
		this.callNative(params);
	},
	biometricAuth : function(params) {
		this.callNative(params);
	},
	whatsApp : function(params) {
		this.callNative(params);
	},
	getSimInfo : function(params) {
		this.callNative(params);
	},
	sendSmsBySID : function(params) {
		this.callNative(params);
	},
	documentScanner : function(params) {
		this.callNative(params);
	},
	sendNFC : function(params) {
		this.callNative(params);
	},
	receiveNFC : function(params) {
		this.callNative(params);
	},
	stopNFC : function(params) {
		this.callNative(params);
	},
	readFile : function(params) {
		this.callNative(params);
	},
	printFile : function(params) {
		this.callNative(params);
	},
	printScreen : function(params) {
		this.callNative(params);
	},
	makeSkypeCall : function(params) {
		this.callNative(params);
	},
	deepLinking : function(params) {
		this.callNative(params);
	},
	getPayloadForQOP: function(apzHeader, apzBody){
		return '{'+
			'"appzillonHeader":'+ JSON.stringify(apzHeader)+','+
			'"appzillonBody":'+ JSON.stringify(apzBody)+
		'}';
	},
	getPayloadChecksum : function(str, salt) {
		if(salt){
			str = str+salt;
		}
		return CryptoJS.SHA256(str).toString(CryptoJS.enc.Hex)
	},
	//This sendReq is converted to es5 and placed below...
	// sendReq: async function (req) {
	// 	this.apz.initNativeService(req);
	// 	var params = req.params;
	// 	var interfaceId = params.reqFull.appzillonHeader.interfaceId;
	// 	if (interfaceId != "appzillonGetAppSecTokens") {
	// 		params.reqFull.appzillonHeader.clientNonce = this.makeId();
	// 		params.reqFull.appzillonHeader.serverNonce = this.sNonce;
	// 	}
	// 	var reqStr = params.reqFull;
	// 	if (payloadEncryptionReq == "Y" && interfaceId != "appzillonGetKey") {
	// 		// var ive = CryptoJS.lib.WordArray.random(128 / 8);
	// 		var iv = crypto.getRandomValues(new Uint8Array(12));
	// 		// var ivetoTransit = CryptoJS.enc.Base64.stringify(ive);
	// 		// converting iv to hex
	// 		var ivtoTransit = Array.from(iv).map(function (b) {
	// 			return ('00' + b.toString(16)).slice(-2)
	// 		}).join('');
	// 		var encData = {};
	// 		encData.key = apz.exchange;
	// 		// encData.ive = ive;
	// 		encData.iv = iv;
	// 		encData.plainText = JSON.stringify(reqStr.appzillonHeader);
	// 		// reqStr.appzillonHeader = this.encryptRequest(encData);
	// 		// var nobj=this;
	// 		var encryptedHeader = await this.encryptRequestAES_GCM(encData);
	// 		reqStr.appzillonHeader = ivtoTransit + encryptedHeader;
	// 		encData.plainText = JSON.stringify(reqStr.appzillonBody);
	// 		// reqStr.appzillonBody = this.encryptRequest(encData);
	// 		var encryptedBody = await this.encryptRequestAES_GCM(encData);
	// 		reqStr.appzillonBody = ivtoTransit + encryptedBody;

	// 	}

	// 	if (interfaceId !== "appzillonGetAppSecTokens" && this.apz.dataIntegrity === "Y" && payloadEncryptionReq === "N") {
	// 		var payloadForQOP = this.getPayloadForQOP(reqStr.appzillonHeader, reqStr.appzillonBody);
	// 		var checksum = this.getPayloadChecksum(payloadForQOP, reqStr.appzillonHeader.clientNonce);
	// 		reqStr["appzillonQop"] = checksum;
	// 	}

	// 	reqStr = JSON.stringify(reqStr);
	// 	var lurl = this.servletUrl;
	// 	var nsObj = this;
	// 	$
	// 		.ajax({
	// 			url: lurl,
	// 			type: params.method,
	// 			cache: false,
	// 			data: reqStr,
	// 			contentType: 'application/json',
	// 			dataType: 'json',
	// 			async: params.async,
	// 			success: async function (res) {
	// 				params.status = true;
	// 				if (payloadEncryptionReq == "Y") {
	// 					if (interfaceId != "appzillonGetKey") {
	// 						var resp = {};
	// 						var decData = {};
	// 						decData.key = apz.exchange;
	// 						// decData.ivetoTransit = res.appzillonSafe;
	// 						if (res.appzillonHeader != undefined) {
	// 							decData.decryptedText = res.appzillonHeader;
	// 							if (res.hasOwnProperty("exchange")) {
	// 								decData.key = res.exchange;
	// 							}
	// 							var result = await nsObj.decryptResponseAES_GCM(decData);
	// 							if (!apz.isNull(result)) {
	// 								resp.appzillonHeader = JSON
	// 									.parse(result);
	// 							}
	// 						}
	// 						if (res.appzillonBody != undefined) {
	// 							decData.decryptedText = res.appzillonBody;
	// 							if (res.hasOwnProperty("exchange")) {
	// 								decData.key = res.exchange;
	// 							}
	// 							// var result = nsObj.decryptResponse(decData);
	// 							var result = await nsObj.decryptResponseAES_GCM(decData);
	// 							if (!apz.isNull(result)) {
	// 								resp.appzillonBody = JSON.parse(result);
	// 							}
	// 						}
	// 						if (res.appzillonErrors != undefined) {
	// 							decData.decryptedText = res.appzillonErrors;
	// 							if (res.hasOwnProperty("exchange")) {
	// 								decData.key = res.exchange;
	// 							}
	// 							// var result = nsObj.decryptResponse(decData);
	// 							var result = await nsObj.decryptResponseAES_GCM(decData);
	// 							if (!apz.isNull(result)) {
	// 								resp.appzillonErrors = JSON
	// 									.parse(result);
	// 							}
	// 						}
	// 						if (resp.appzillonHeader == undefined) {
	// 							params.resFull = {};
	// 							params.resFull.appzillonErrors = [{
	// 								"errorCode": "APZ-CNT-330"
	// 							}];
	// 							req.status = false;
	// 						} else {
	// 							params.resFull = resp;
	// 						}

	// 					}
	// 					else {
	// 						params.resFull = res;
	// 					}
	// 				} else {
	// 					params.resFull = res;
	// 				}
	// 				if (params.resFull.appzillonErrors != undefined
	// 					&& params.resFull.appzillonErrors[0].errorCode == "APZ-CNT-330") {
	// 					req.status = false;
	// 				} else if (params.resFull.appzillonErrors != undefined
	// 					&& params.resFull.appzillonErrors[0].errorCode == "APZ-CSRF-EX") {
	// 					req.status = false;
	// 				} else {
	// 					req.status = true;
	// 				}
	// 				var resParams = {};
	// 				resParams.resFull = req.params.resFull;
	// 				resParams.status = req.params.status;
	// 				resParams.id = req.params.id;
	// 				req.params = resParams;
	// 				Apz.nativeServiceCB(JSON.stringify(req));
	// 			},
	// 			error: function () {
	// 				params.status = false;
	// 				Apz.nativeServiceCB(JSON.stringify(req));
	// 			}
	// 		});
	// },
	
	//This function is coverted to es5. It contains the AES_GCM Encryption logic...
	sendReq: (function () {
    	var _sendReq = _asyncToGenerator(
      /*#__PURE__*/ regeneratorRuntime.mark(function _callee2(req) {
        var params,
          interfaceId,
          reqStr,
          iv,
          ivtoTransit,
          encData,
          nobj,
          encryptedHeader,
          encryptedBody,
          payloadForQOP,
          checksum,
          lurl,
          nsObj;
        return regeneratorRuntime.wrap(
          function _callee2$(_context2) {
            while (1) {
              switch ((_context2.prev = _context2.next)) {
                case 0:
                  this.apz.initNativeService(req);
                  params = req.params;
                  interfaceId = params.reqFull.appzillonHeader.interfaceId;

                  if (interfaceId != "appzillonGetAppSecTokens") {
                    params.reqFull.appzillonHeader.clientNonce = this.makeId();
                    params.reqFull.appzillonHeader.serverNonce = this.sNonce;
                  }

                  reqStr = params.reqFull;

                  if (
                    !(
                      payloadEncryptionReq == "Y" &&
                      interfaceId != "appzillonGetKey"
                    )
                  ) {
                    _context2.next = 22;
                    break;
                  }

                  // var ive = CryptoJS.lib.WordArray.random(128 / 8);
                  iv = crypto.getRandomValues(new Uint8Array(12)); // var ivetoTransit = CryptoJS.enc.Base64.stringify(ive);
                  // converting iv to hex

                  ivtoTransit = Array.from(iv)
                    .map(function (b) {
                      return ("00" + b.toString(16)).slice(-2);
                    })
                    .join("");
                  encData = {};
                  encData.key = apz.exchange; // encData.ive = ive;

                  encData.iv = iv;
                  encData.plainText = JSON.stringify(reqStr.appzillonHeader); // reqStr.appzillonHeader = this.encryptRequest(encData);
                  _context2.next = 15;
                  return this.encryptRequestAES_GCM(encData);

                case 15:
                  encryptedHeader = _context2.sent;
                  reqStr.appzillonHeader = ivtoTransit + encryptedHeader; 
                  encData.plainText = JSON.stringify(reqStr.appzillonBody); // reqStr.appzillonBody = this.encryptRequest(encData);

                  _context2.next = 20;
                  return this.encryptRequestAES_GCM(encData);

                case 20:
                  encryptedBody = _context2.sent;
                  reqStr.appzillonBody = ivtoTransit + encryptedBody; 
                case 22:
                  if (
                    interfaceId !== "appzillonGetAppSecTokens" &&
                    this.apz.dataIntegrity === "Y" &&
                    payloadEncryptionReq === "N"
                  ) {
                    payloadForQOP = this.getPayloadForQOP(
                      reqStr.appzillonHeader,
                      reqStr.appzillonBody
                    );
                    checksum = this.getPayloadChecksum(
                      payloadForQOP,
                      reqStr.appzillonHeader.clientNonce
                    );
                    reqStr["appzillonQop"] = checksum;
                  }

                  reqStr = JSON.stringify(reqStr);
                  lurl = this.servletUrl;
                  nsObj = this;
                  const token = $("meta[name='_csrf']").attr("content");
                  const csrf = $("meta[name='_csrf_header']").attr("content");
                  if(this.apz.CSRF == "Y"){
                      $.ajaxSetup({
                          beforeSend: function(xhr) {
                              xhr.setRequestHeader(csrf,token);
                          }
                      });
                  }
                  $.ajax({
                    url: lurl,
                    type: params.method,
                    cache: false,
                    data: reqStr,
                    contentType: "application/json",
                    dataType: "json",
                    async: params.async,
                    success: (function () {
                      var _success = _asyncToGenerator(
                        /*#__PURE__*/ regeneratorRuntime.mark(function _callee(
                          res
                        ) {
                          var resp, decData, result, resParams;
                          return regeneratorRuntime.wrap(function _callee$(
                            _context
                          ) {
                            while (1) {
                              switch ((_context.prev = _context.next)) {
                                case 0:
                                  params.status = true;

                                  if (!(payloadEncryptionReq == "Y")) {
                                    _context.next = 36;
                                    break;
                                  }

                                  if (!(interfaceId != "appzillonGetKey")) {
                                    _context.next = 33;
                                    break;
                                  }

                                  resp = {};
                                  decData = {};
                                  decData.key = apz.exchange; // decData.ivetoTransit = res.appzillonSafe;

                                  if (!(res.appzillonHeader != undefined)) {
                                    _context.next = 14;
                                    break;
                                  }

                                  decData.decryptedText = res.appzillonHeader;

                                  if (res.hasOwnProperty("exchange")) {
                                    decData.key = res.exchange;
                                  }

                                  _context.next = 11;
                                  return nsObj.decryptResponseAES_GCM(decData);

                                case 11:
                                  result = _context.sent;
                                

                                  if (!apz.isNull(result)) {
                                    resp.appzillonHeader = JSON.parse(result);
                                  }

                                case 14:
                                  if (!(res.appzillonBody != undefined)) {
                                    _context.next = 22;
                                    break;
                                  }

                                  decData.decryptedText = res.appzillonBody;

                                  if (res.hasOwnProperty("exchange")) {
                                    decData.key = res.exchange;
                                  } // var result = nsObj.decryptResponse(decData);

                                  _context.next = 19;
                                  return nsObj.decryptResponseAES_GCM(decData);

                                case 19:
                                  result = _context.sent;
                                  

                                  if (!apz.isNull(result)) {
                                    resp.appzillonBody = JSON.parse(result);
                                  }

                                case 22:
                                  if (!(res.appzillonErrors != undefined)) {
                                    _context.next = 30;
                                    break;
                                  }

                                  decData.decryptedText = res.appzillonErrors;

                                  if (res.hasOwnProperty("exchange")) {
                                    decData.key = res.exchange;
                                  } // var result = nsObj.decryptResponse(decData);

                                  _context.next = 27;
                                  return nsObj.decryptResponseAES_GCM(decData);

                                case 27:
                                  result = _context.sent;
					

                                  if (!apz.isNull(result)) {
                                    resp.appzillonErrors = JSON.parse(result);
                                  }

                                case 30:
                                  if (resp.appzillonHeader == undefined) {
                                    params.resFull = {};
                                    params.resFull.appzillonErrors = [
                                      {
                                        errorCode: "APZ-CNT-330"
                                      }
                                    ];
                                    req.status = false;
                                  } else {
                                    params.resFull = resp;
                                  }

                                  _context.next = 34;
                                  break;

                                case 33:
                                  params.resFull = res;

                                case 34:
                                  _context.next = 37;
                                  break;

                                case 36:
                                  params.resFull = res;

                                case 37:
                                  if (
                                    params.resFull.appzillonErrors !=
                                      undefined &&
                                    params.resFull.appzillonErrors[0]
                                      .errorCode == "APZ-CNT-330"
                                  ) {
                                    req.status = false;
                                  } else if (
                                    params.resFull.appzillonErrors !=
                                      undefined &&
                                    params.resFull.appzillonErrors[0]
                                      .errorCode == "APZ-CSRF-EX"
                                  ) {
                                    req.status = false;
                                  } else {
                                    req.status = true;
                                  }

                                  resParams = {};
                                  resParams.resFull = req.params.resFull;
                                  resParams.status = req.params.status;
                                  resParams.id = req.params.id;
                                  req.params = resParams;
                                  Apz.nativeServiceCB(JSON.stringify(req));

                                case 44:
                                case "end":
                                  return _context.stop();
                              }
                            }
                          },
                          _callee);
                        })
                      );

                      function success(_x2) {
                        return _success.apply(this, arguments);
                      }

                      return success;
                    })(),
                    error: function error() {
                      params.status = false;
                      Apz.nativeServiceCB(JSON.stringify(req));
                    }
                  });

                case 27:
                case "end":
                  return _context2.stop();
              }
            }
          },
          _callee2,
          this
        );
      })
    );

    function sendReq(_x) {
      return _sendReq.apply(this, arguments);
    }

    return sendReq;
	  })(),
	  
	login : function(req) {
		// Implemented for biometric login changes in other containers
		this.sendReq(req);
	},
	changePassword : function(req) {
		this.sendReq(req);
	},
	refreshServerNonce : function(req) {
		this.getAppSecTokens(req);
	},
	getKey : function(key) {
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
	makeId : function() {
		var text = "";
		var length = 10;
		var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		for (var i = 0; i < length; i++)
			text += possible
					.charAt(Math.floor(Math.random() * possible.length));
		return text;
	},
	
	audio: function(json) {	 
		 if(json.action == "record")
				 {
				  this.startRecord();
				 }
			 else if(json.action == "save")
				 {
				 this.stopRecord(json);
				 }
			 else if(json.action == "play")
				 {
				   this.playRecord(json);
				 }		 
		},
		startRecord: function()
		{
			if (navigator.mediaDevices.getUserMedia) {
				const constraints = { audio: true };
				chunks = [];
				onSuccess = function(stream) {
					mediaRecorder = new MediaRecorder(stream);				
					mediaRecorder.start();	
					mediaRecorder.ondataavailable = function(e) {
						chunks.push(e.data);
					}
				}
				onError = function(err) {
					this.apz.dispMsg("The following error occured:"+ err, null);
				}
				navigator.mediaDevices.getUserMedia(constraints).then(onSuccess, onError);
			} else {
				this.apz.dispMsg("getUserMedia not supported on your browser!", null);
			}
		},
		
		stopRecord: function(json)
		{
			
			var params = {};
			params.ifaceName = "appzillonUploadFile";
			params.scrName = this.apz.currScr;
			params.async = false;
			params.id = "AUDIOSAVE_ID";
			params.internal = true;
			var header = apz.server.getHeader(params);
			header = JSON.stringify(header);
			mediaRecorder.stop();
			mediaRecorder.onstop = function(e) {
				
				var blob = new Blob(chunks, { 'type' : 'audio/ogg; codecs=opus' });
				chunks = [];	
                                var clipLabel;			  	
				if((!apz.isNull(json.base64))&& (json.base64 == "Y"))
				{
					var base64data;
					var response = {};		
					var reader = new FileReader();
					reader.readAsDataURL(blob); 
					reader.onloadend = function() {
						base64data = reader.result; 
						response.data = base64data;               					     
					}
									
					response.successMessage = "success";					
					json.callBack(response);
				}
				else
				{
					var today = new Date();
					var date = today.getFullYear()+'-'+(today.getMonth()+1)+'-'+today.getDate();
					  var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
					var clipName = prompt('Enter a name for your sound clip?','Audio'+date+time);
					 clipLabel = document.createElement('p');	
					if(clipName == null) {
						clipLabel.textContent = 'Audio'+date+time;
					} else {
						clipLabel.textContent = clipName;
					}	
					var formData=new FormData();
					if(json.wavFileFormat == "Y")
				    	formData.append("fileName", clipName+".wav");
					else
						formData.append("fileName", clipName+".mp3");
					formData.append("file", blob);		
					if (!apz.isNull(json.interfaceId)) {
						formData.append("interfaceId", json.interfaceId);
					}
					if (!apz.isNull(json.request)) {
						formData.append("request", JSON.stringify(json.request));
					}
					formData.append("destination", json.location);
					if (apz.isNull(json.overWrite)) {
						formData.append("overWrite", "N");
					} else {
						formData.append("overWrite", json.overWrite);
					}
					formData.append("appzillonHeader", header);
					$.ajax({url: this.servletUrl,data:formData,
						contentType: false,
						enctype: 'multipart/form-data',
						type:"POST",
						processData:false,
						success : function(presp) {
							// var responseJson = JSON.parse(presp);
							if (presp.result == "success") {
								var resp = {};
								resp.successMessage = presp.successMessage;
								json.callBack(resp);
							} else if (presp.result == "failure") {
								this.apz.dispMsg("APZ-SMS-EX-003");
							} else {
								json.callBack(presp);
							}
						},
						error : function(jqXHR, textStatus, errorMessage) {
							json.callBack(errorMessage);
						}
					});	
				}
				clipLabel.onclick = function() {
					var existingName = clipLabel.textContent;
					var newClipName = prompt('Enter a new name for your sound clip?');
					if(newClipName == null) {
						clipLabel.textContent = existingName;
					} else {
						clipLabel.textContent = newClipName;
					}
				}
			}
		},
		playRecord: function(json)
		{
			var req={};
			req.fileName=json.fileName;
			req.filePath=json.location;
			req.base64="N";
			this.downloadFile(req);
	},
	registerWebNotificationToken: function()
	{
		var params = {};
		var thisVar = this;
		params.ifaceId = "appzillonNotificationRegistration";
		params.id="00NEW";
		var messaging=registerFirebase();
		
		navigator.serviceWorker.register('Web-ServiceWorker.js')
		.then(function(registration) {
			messaging.useServiceWorker(registration);
			// Request permission and get token.....
			Notification.requestPermission().then(function(permission)  {
				if (permission === 'granted') {
					messaging.getToken().then(function(currentToken)  {
						if (currentToken) {
							var browserVersion = navigator.appVersion;
							var browserAgent = navigator.userAgent;
							var browserEngine  = navigator.appName;
                           var appzillonregisterWebDeviceTokenRequest={};
                           appzillonregisterWebDeviceTokenRequest.appId=apz.appId;
                            appzillonregisterWebDeviceTokenRequest.regId=currentToken;
                            appzillonregisterWebDeviceTokenRequest.deviceId=navigator.platform;
                            appzillonregisterWebDeviceTokenRequest.osId='WEB';
                            appzillonregisterWebDeviceTokenRequest.deviceName=browserAgent;
                            appzillonregisterWebDeviceTokenRequest.browserVersion=browserVersion;
                            appzillonregisterWebDeviceTokenRequest.browserEngine=browserEngine;
							params.req={};   
						    params.req=appzillonregisterWebDeviceTokenRequest;
						    apz.ns.callServlet(params);
							
						} 
					});
				} 
			});
			messaging.onMessage(function(payload) {
				// alert('Notification received!', payload);
				var myJSON = JSON.stringify(payload);
				var js=JSON.parse(myJSON);
				const title = js["notification"].title;
				const options = {
						body: js["notification"].body,
						icon:js["notification"].icon,
						action:js["notification"].click_action
				};

				navigator.serviceWorker.ready.then(function(serviceWorker) {
					serviceWorker.showNotification(title, options);
				});
			});
			  messaging.setBackgroundMessageHandler(function(payload) {
	                 var myJSON = JSON.stringify(payload);
					var js=JSON.parse(myJSON);
	               const notificationTitle = js["notification"].title;
	               const notificationOptions = {
	               body: js["notification"].body,
				icon:js["notification"].icon,
				action:js["notification"].click_action
	                         };

	             return self.registration.showNotification(notificationTitle,notificationOptions);
	                                                   });
		});
	}
};
