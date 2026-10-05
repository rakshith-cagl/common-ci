/**
 * @namespace
 */
Apz.Ns = function(apz) {
   ////Core Instance
   this.apz = apz;
   this.safeToken = "";
   this.sessionToken = "";
   this.serverNonce = "";
   this.counter = 0;
   this.publicKey = "";
   ///Add Random Number
   require.config({
		urlArgs: "bust=" + (new Date()).getTime()
   });

};
///////////////////Prototype Definition///////////////////////
Apz.Ns.prototype = {
   callNative : function(req) {
        try {
         var res = {};
            res.id = req.id;
            var params = {"message":"Plugin not supported for Simulator"};
            params.callBack = req.callBack;
            params.callBackObj = req.callBackObj;
            this.apz.dispMsg(params);
            //this.apz.initNativeService(res);
            //Apz.nativeServiceCB(res);
        } catch (e) {
            console.log(e);
        }
   }, changePassword : function(req) {
      var params = req.params;
      var changePwdReq = params.req.changePasswordRequest;
      changePwdReq.newPassword = apzIde.encryptData(changePwdReq.newPassword);
      if (this.apz.authenticationType == "#DeviceId") {
         try {
            changePwdReq.pwd = apzIde.hashPwd(changePwdReq.userId, changePwdReq.pwd, this.apz.serverToken, this.apz.deviceId, changePwdReq.sysDate);
         } catch (e) {
            alert("Hashing Failed in Change Password");
         }
      }
      this.sendReq(req);
   }, log : function(msg,type) {
      if ( typeof apzIde !== "undefined") {
         apzIde.log(msg);
      } else {
         console.log(msg);
      }
   }, login : function(req) {
      if(this.apz.isNull(req.params.bioMetAuth) && req.params.bioMetAuth === "Y"){
         this.callNative(req);
      } else {
         if (this.apz.authenticationType == "#DeviceId") {
            var params = req.params;
            var hash = params.pwd;
            try {
               hash = apzIde.hashPwd(params.userId, params.pwd, this.apz.serverToken, this.apz.deviceId, params.req.loginRequest.sysDate);
            } catch (e) {
               alert("Login Hashing Failed");
            }
            params.pwdOrig = params.pwd;
            params.req.loginRequest.pwd = hash;
          }

          this.sendReq(req);
      }
   }, 
   
   /**
    * This plugin helps to get the device related information in the callback JSON parameter.
    * @param {object} req req includes id, callBack
    * @returns {object} object id, status, errorCode, deviceOs, deviceType, screenSize, screenPpi, deviceGroup, orientation, lockRotation
    * @example 
    * var json = {};
 	 * json.id = "DEVICEINFOID";
    * json.callBack = getdeviceInfoCallback;
    * apz.ns.getDeviceInfo (json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)  
	*/
   getDeviceInfo : function(req) {
      
      ////Init
      this.apz.initNativeService(req);
      var ideSim = false;
      if ( typeof apzIde !== "undefined") {
         ideSim = true;
      }
      var res = {};
      res.id = req.id;
      res.status = true;
      res.status = true;
      res.deviceType = "SIMULATOR";
      res.deviceOs = "SIMULATOR";
      res.deviceId = "SIMULATOR";
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
   /**
    * This API is used to obtain the user settings from device persistence storage.
    * @param {object} req req includes id, callback.
    * @returns {object} object includes id, status, errorcode, userprefs.
    * @example
    * var json = {};
    * json.id = "getUserPrefs"
    * json.callBack = getPrefCallback;
    * apz.ns.getUserPrefs(json)
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)  
	*/
   getUserPrefs : function(req) {
      ////Init
      this.apz.initNativeService(req);
      var res = {};
      res.id = req.id;
      res.userPrefs = null;
      ///Would have read it from projdef
      Apz.nativeServiceCB(JSON.stringify(res));
   }, 
   
   /**
    * This API returns the Latitude and Longitude of the current Location in JSON parameter of the callback.
    * @param {object} req req includes id, callBack
    * @returns {object} object includes id, status, errorCode, latitude, longitude
    * @example
    * gps.id = "LOCATEUSER";
    * gps.callBack = getLocCallBack;
    * apz.ns.getLocation(gps);
    * getLocCallBack = function(params){
    * alert(params.latitude+","+params.longitude);
    * }
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)  
	*/

   getLocation : function(req) {
	this.apz.initNativeService(req);
	var res = {};
	res.id = req.id;
	res.status = true;
	try{
		navigator.geolocation.getCurrentPosition(
		    function(position) {
				res.latitude = position.coords.latitude.toString();
				res.longitude = position.coords.longitude.toString();
				Apz.nativeServiceCB(JSON.stringify(res));
		    },
		    function(error){
				res.latitude = "0";
				res.longitude = "0";
				Apz.nativeServiceCB(JSON.stringify(res));
		    }
		);
	} catch(e){
		this.callNative(res);
	}
   }, 
   
   /**
    * This API encrypts the string based on the device.
    * @param {object} req req includes id, callBack, encryptionId, stringToEncrypt, key
    * @returns {object} includes id, status, errorCode, text
    * @example
    * var json = {
    *    "id": "123",
    *    "stringToEncrypt": "Test string",
    *    "key": "Key123"
    *  };
    *  json.encryptionId="xyz";
    *  json.id = "ENCRYPTSTRING_ID";
    *  json.callBack = encryptCallback;
    *  apz.ns.encryptData(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)  
	*/
	
   encryptData : function(req) {
      ////Init
      this.apz.initNativeService(req);
      var res = {};
      res.id = req.id;
      res.status = true;
      var encryptedStr = req.stringToEncrypt;
      try{
      	encryptedStr = apzIde.encryptData(req.stringToEncrypt);
      } catch (e){
      	res.status = false;
      }
      res.text = encryptedStr;
      ////CAll BAck..
      Apz.nativeServiceCB(JSON.stringify(res));
   }, 
   
   /**
    * This API decrypts a string using a particular key.
    * @param {object} req req includes id, callback, decryptionId, stringToDecrypt, key
    * @returns {object} object includes id, errorCode, status, text
    * @example
    * var json = {
    *   "id": "123",
    *   "stringToDecrypt": "tCURj02QJzdvZuktaDlHLA==",
    *   "key": "Key123"
    * };
    * json.id = "DECRYPTSTRING_ID";
    * json.decryptionId="XYZ";
    * json.callBack = decryptStringCallback;
    * apz.ns.decryptData(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)  
	*/
   decryptData : function(req) {
      ////Init
      this.apz.initNativeService(req);
      var res = {};
      res.id = req.id;
      res.status = true;
      var decryptedStr = req.stringToDecrypt;
      try{
      	decryptedStr = apzIde.decryptData(req.stringToDecrypt);
      } catch (e){
      	res.status = false;
      }
      res.text = decryptedStr;
      ////CAll BAck..
      Apz.nativeServiceCB(JSON.stringify(res));
   }, 
   /**
    * This API is used to reset the security token obtained from the server.
    * @param {object} params params includes id, callback.
    * @returns {object} object includes status, id.
    * 	var params = {};
    *		params.id = "RESETSERVERNONCE";
    *		params.callBack = nonceCallback;
    *		apz.ns.refreshServerNonce(params);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)  
	*/
   refreshServerNonce : function(params) {
      params.id = "CSNONCE";
      params.internal = true;
      params.ifaceName = "appzillonGetAppSecTokens";
      params.ifaceId = "appzillonGetAppSecTokens";
      if(!params.callBack){
    	  params.callBack = this.refreshServerNonceCB;
    	  params.callBackObj = this;
      }
      this.apz.initNativeService(params);
      var internalErr = false;
      if (!this.apz.isNull(typeof apzIde) && !apzIde.isMockEnabled()) {
         //// Construct Request
         var reqFull = {};
         if(this.apz.isNull(params.id)){
            params.id = "CSNONCE";
         }
         reqFull.appzillonHeader = this.apz.server.getHeader(params);
         var request = {};
         request.appzillonGetAppSecTokensRequest = {};
         request.appzillonGetAppSecTokensRequest.appId = this.apz.appId;
         request.appzillonGetAppSecTokensRequest.deviceId = "SIMULATOR";
         reqFull.appzillonBody = request;
         params.reqFull = reqFull;
         var url = apzIde.getServerUrl();
         var encReqd = apzIde.isEncryptionReqd();
         url = url.replace("https://", "http://");
         if (encReqd == "Y") {
        	var safeKey = Math.random().toString(36).substr(2,8) + Math.random().toString(36).substr(2,8);
            this.publicKey = this.apz.getFile("apps/" + this.apz.appId + "/rsakeys/" + this.apz.appId + "_Public.key");
            reqFull.appzillonSafe = apzIde.encryptWithPublicKey(safeKey, this.apz.encryptionKeyFileName);
            reqFull.appzillonHeader = apzIde.encryptWithKey(JSON.stringify(reqFull.appzillonHeader), safeKey);
            reqFull.appzillonBody = apzIde.encryptWithKey(JSON.stringify(reqFull.appzillonBody), safeKey);
            if(this.apz.isNull(reqFull.appzillonHeader) || this.apz.isNull(reqFull.appzillonBody)){
               internalErr = true;
            }
         }
         if(!internalErr){
            var reqStr = JSON.stringify(reqFull);
            var nsObj = this;
            $.ajax({
               url: url,
               type: 'POST',
               cache: false,
               data: reqStr,
               contentType: 'application/json',
               dataType: 'json',
               async: false,
               success: function(res) {
                  params.status = true;
                  nsObj.counter = 0;
                  if (encReqd == "Y") {
                	 var safeKey = apzIde.decryptWithPublicKey(res.appzillonSafe, nsObj.publicKey);
                     var decrypHeader = apzIde.decryptWithKey(res.appzillonHeader, safeKey);
                     var decrypBody = apzIde.decryptWithKey(res.appzillonBody, safeKey);
                     if (res.appzillonErrors) {
                        var decrypErrors = apzIde.decryptWithKey(res.appzillonErrors, safeKey);
                        if(nsObj.apz.isNull(decrypErrors)){
                           internalErr = true;
                        } else {
                           res.appzillonErrors = JSON.parse(decrypErrors);
                        }
                     }
                     if(nsObj.apz.isNull(decrypHeader) || nsObj.apz.isNull(decrypBody)){
                        internalErr = true;
                     } else {
                        res.appzillonHeader = JSON.parse(decrypHeader);
                        res.appzillonBody = JSON.parse(decrypBody);
                     }
                  }
                  var appTokn = res.appzillonBody.appzillonGetAppSecTokensResponse;
                  if (appTokn) {
                     nsObj.safeToken = res.appzillonBody.appzillonGetAppSecTokensResponse.safeToken;
                     nsObj.sessionToken = res.appzillonBody.appzillonGetAppSecTokensResponse.sessionToken;
                     nsObj.serverNonce = res.appzillonBody.appzillonGetAppSecTokensResponse.serverNonce;
                  }
                  if(internalErr){
                     params.status = false;
                     res.appzillonErrors = [{"errorCode":"APZ-CNT-330"}];
                  }
                  params.resFull = res;
                  Apz.nativeServiceCB(JSON.stringify(params));
               },
               error: function(res) {
                  params.status = false;
                  Apz.nativeServiceCB(JSON.stringify(params));
               }
            });
         } else {
            params.status = false;
            params.resFull = {"appzillonErrors":[{"errorCode":"APZ-CNT-330"}]};
            params.errorCode = "APZ-CNT-330";
            Apz.nativeServiceCB(params);
         }
      } else {
         params.status = true;
         Apz.nativeServiceCB(params);
      }
   }, refreshServerNonceCB : function(params) {
	   if(!params.status){
		   this.apz.dispMsg({message:"Failed to get Server Nonce"});
	   }
   }, hashSHA256 : function(req) {
      ////Init
      this.apz.initNativeService(req);
      var hash = '';
      try {
         hash = apzIde.hashSHA256(req.body.text, req.body.salt);
      } catch (e) {
      }
      var res = {};
      res.id = req.id;
      res.status = true;
      res.text = hash;
      ////Call BAck..
      Apz.nativeServiceCB(JSON.stringify(res));
   }, hashPwd : function() {
      
   },
   
   /**
    * This API helps to show the splash screen.Splash screen is the image that appears while launching application.
    * @example
    * apz.ns.showSplash(
    * @description
	* <b>OS Support</b><br>
	* ![iOS](../iOS.png) 
	*/
   showSplash : function(req) {
       //this.callNative(req);  
   }, 
   /**
    * This API helps to hide splash screen.Splash screen is the image that appears while launching application. 
    * @example
    * apz.ns.hideSplash();
    * @description
	* <b>OS Support</b><br>
	* ![iOS](../iOS.png) 
	*/
   hideSplash : function(req) {
	   //Nothing To Do
   }, startOrientationListener: function(req) {      
      this.apz.initNativeService(req);         
   }, 
   
   /**
    * @param {object} req req includes id, callBack
    * @example 
    * apz.ns.nativeServiceExt();
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)  
	*/
   nativeServiceExt : function(req) {
      this.callNative(req);
   }, 
   /**
    * A camera plugin.
    * @param {object} req req includes zoomLevel, targetWidth, targetHeight, crop, flash, action, fileName, encodingType, sourceType, frontCamera, quality.
    * @returns {object} object includes 	id, errorCode,	status, encodedImage, path.
    * @example
    * var jsonobj = {
    * "zoomLevel": "20", 
    * "targetWidth": "200",
    * "targetHeight":"200",
    * "crop": "Y",//Y or N 
    * "flash": "N",
    * "action": "base64_Save", // save,base64
    * "fileName": “Sample”,
    * "quality" : "50",
    * "encodingType" : "JPG",
	* "unCompressed" : "Y/N"
    * "sourceType":"camera"   // photo
    * };
    * jsonobj.id = "CAMERA_ID";
    *	jsonobj.callBack = cameraCallback;
    *	apz.ns.openCamera(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   openCamera : function(req) {
      this.callNative(req);
   }, 
   /**
    * 
    * @param {object} req req includes id, callback, uuid
    * @returns {object} object includes id, errorcode, status,event,text
    * @example
    *    var beaconJson = {
    *        "uuid": "E1C56DB5-DFFB-48D2-B060-D0F5A71096E0"
    *    };
    *     beaconJson.id = "BEACON";
    *     beaconJson.callBack = beaconCallback;
    *     apz.ns.startBeacon(beaconJson);
    */
   startBeacon : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API stops the beacon listener.
    * @param {object} req req includes id & callback
    * @returns {object} object includes id, errorCode, status
    * @example
    *     var json = {};
    *     json.id = "BEACON";
    *     json.callBack = beaconStopCallback;
    *     apz.ns.stopBeacon(json);
    * <b>Note:</b> The ID for apz.ns.startBeacon and apz.ns.stopBeacon should be same.
    * @description <b>OS specific Limitations</b>
    * <br>Beacon functionalities are supported in iOS 7 and above, in android 4.4 and above devices  and Windows 10 phone.
    * 
    */
   stopBeacon : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API calls a particular number using native dial facility.
    * @param {object} req req includes id, callback, phoneNo.
    * @example
    * var json = {};
    * json.phoneNo = "1234567890";
    * json.id = "CALL_ID";
    * json.callBack = phoneCallback;
    * apz.ns.callNumber(json); 
    * <b>Note:</b> The ID for apz.ns.startBeacon and apz.ns.stopBeacon should be same.
    * @description <b>OS Specific Limitations</b>    
    * <br>This plugin can be used provided the device includes a SIM.
    * 
    */
   callNumber : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API helps convert a base64 to file and save in the device.
    * @param {object} req req includes id, callBack, base64, fileName, filePath
    * @returns {object} object includes id, status, errorCode, filePath
    * @example
    * var json = {};
    *  json.id = "BASE64TOFILE";
    *  json.callBack = base64tofilecallBack;
    *  json.base64 = base64OfFile
    *  json.fileName = "file_name");
    *  json.filePath = "docs";
    *  apz.ns.base64ToFile(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)  
	*/
   base64ToFile : function(req) {
      this.callNative(req);
   }, scanFinger : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * @param {object} req req includes id, callBack, url
    * returns {object} object includes id, status, errorCode
    * @example 
    * var json = {
	 * 	 	"url" : "http://i-exceed.com/appzillon/"
    *      	 }   
    *  json.id = "BROWSERID";
    *  json.callBack = browserCallback;
    *  apz.ns.openUrl(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   openUrl : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API returns the base64 string of any file in callback JSON parameter.
    * @param {object} req req includes id, callBack, filePath
    * @returns {object} id, status, errorCode, text
    * @example
    * var json = {};
    *  json.id = "FILETOBASE64";
    *  json.callBack = filetobase64callBack;
    *  json.filePath = "docs/temp.pdf";
    *  apz.ns.fileToBase64(json)
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png) 
	*/
   fileToBase64 : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API returns the size of any file
    * @param {object} req req includes id, callBack, filePath
    * @returns {object} id, status, errorCode, fileSize
    * @example
    * var json = {};
    *  json.id = "FILETOBASE64";
    *  json.callBack = fileSizeCallBack;
    *  json.filePath = "docs/temp.pdf";
    *  apz.ns.getFileSize(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   getFileSize : function(req) {
      this.callNative(req);
   }, 
   /**
    * The plugin helps to do the following

    * <br> Lauch a webview within the application with control on the URL
    * <br> Close the webview
    * @param {object} req req includes URL, callback, id.
    * @returns {object} object includes id, errorCode, status, URL.
    * @example
    * var json = {
    * "URL": "http://www.google.co.in/"
    * }
    * json.id = "LAUNCH_ID";
    * json.callBack = this.launchwebviewCallback;
    * apz.ns.launchWebview(json);
    */
   launchWebview : function(req) {
      this.callNative(req);
   }, 
   /**
    * 
    * @param {object} req req includes id, callback.
    * @returns {object} object includes id, errorcode, status.
    * @example
    * var json = {
    * "URL": "http://www.google.co.in/"
    * }
    * json.id = "CLOSELAUNCH_ID";
    * json.callBack = this.launchwebviewCallback;
    * apz.ns.closeWebview(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   closeWebview : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API helps to get list of messages from the Inbox
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, status, errorcode, smsList{[{body,address,timestamp},……]}
    * @example
    *  json = {};
    *  json.id = "INBOXSMS";
    *  json.callBack = inboxSMSCallback;
    *  apz.ns.getInboxSMS(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png)
	*/
   getInboxSMS : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API helps to set Ringtone of the device.
    * @param {object} req req includes id, callback, filepath, title.
    * @returns {object} object includes id, errorCode, status
    * @example
    * var json = {};
    * json.id = "RINGTONE";
    * json.callBack = ringtoneCallback;
    * json.title = "AppzillonRingtone";
    * json.filePath = "temp.mp3";
    * apz.ns.setRingtone(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png)
	*/
   setRingtone : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API helps to register a callback function to handle the missed call. The plugin calls the callback when it detect the missed call. The application should be in active to receive this callback.
    * @param {object} req req includes Id, callback.
    * @returns {object} object includes id, status, errorcode,Number.
    * @example
    * var json = {};
    * json.id = "MISSEDCALL";
    * json.callBack =startList;
    * apz.ns.startCallListener (json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png)
	*/
   startCallListener : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API helps to get the list of missed calls in the callback JSON parameter.
    * @param {object} req req includes Id, callback.
    * @returns {object} object includes id, status, errorCode, missedCallList :, [{number ,timestamp},…].
    * @example
    * var json = {};
    * json.id = "MISSEDCALL";
    * json.callBack = missedcallCallback;
    * apz.ns.getMissedCalls(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png)
	*/
   getMissedCalls : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API zips the file or folder.
    * @param {object} req req includes srcFilePath, destFilePath, id, callBack, overwrite.
    * @returns {object} object id, status, errorcode, filePath.
    * @example
    *    var json = {
    *    "destFilePath": "docs/Zipped",
    *    "overwrite": "Y"
    * };
    * json.id = "ZIP_ID";
    * json.srcFilePath = apz.getElmValue (“file_path");
    * json.callBack = zipCallback;
    * apz.ns.zip(json);
    */
   zip : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API unzips the files or folders
    * @param {object} req req includes id, callback, srcFilepath, destFilepath.
    * @returns {object} object includes id, status, errorcode, filepath.
    * @example
    *     var json = {
    *    "destFilePath": "docs",
    *    "overwrite": "Y"
    * };
    * json.srcFilePath = apz.getElmValue (“file_path");
    * json.id = "UNZIP_ID";
    * json.callBack = unzipCallback;
    * apz.ns.unzip(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   unzip :function(req) {
      this.callNative(req);
   },
   /**
    * The accelerometer plugin enables the developer to do the following:
    * Start accelerometer plugin
    * Retrieve acceleration details of the device.
    * Stop the plugin.
    * @param {object} req req includes CallBack, id, periodicity, interval
    * <br>periodicity: This parameter can have following values.
    * <br><b>onChange</b> If the value is onChange, then the callback function is called with the current acceleration details whenever there is a change in the acceleration details of the device. 
    * <br><b>timed</b> If the value is timed, then the callback function is called with the current acceleration details based on the time interval (in milliseconds) set in the field interval.
    * <br><b>none</b> If the value is none, then the callback function is called with the current acceleration details at only once.
    * <br>  interval: This parameter takes the time (in milliseconds) after which the acceleration update is to be sent to the callback function. This needs to be set only if the periodicity is timed.
    * @example
    * var jsonobj = {
    *    "periodicity": "none",
    *    "interval": ""
    * };
    * jsonobj.periodicity = apz.getElmValue ("element_dropdown_1");
    * jsonobj.interval = apz.getElmValue ("element_inputbox_2");
    * jsonobj.id = "STARTACC_ID";
    * jsonobj.callBack = accelerometerstartCallback;
    * apz.ns.startAccelerometer(jsonobj);
    * @returns {object} object includes - Status, Id, ErrorCode, xCord, yCord, zCord.
    */
   startAccelerometer : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API is to stop the accelerometer plugin if it’s running already.
    * @param {object} req req includes Id and Callback.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "STOPACC_ID";
    * jsonobj.callBack = accelerometerstopCallback;
    * apz.ns.stopAccelerometer(jsonobj);
    * <b>Note:</b> The ID for apz.ns.startAccelerometer and apz.ns.stopAccelerometer should be same.
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   stopAccelerometer : function(req) {
      this.callNative(req);
   }, 

   scanBarcode : function(req) {
      this.callNative(req);
   }, 
   /**
    * The calendar plugin helps to do the following
    * 
    * <br>• Create a calendar event.
    * <br>• Edit a calendar event.
    * <br>• Delete an event.
    * @param {object} req req includes title, alarm, startDate, endDate, startTime, endTime, priority, summary, recurrence, recurrenceEndDate, location, callBack, id.
    * @returns {object} object includes Id, status, Errorcode.
    * @example
    *  var json = {
    *    "title": "event1",
    *    "alarm": "on",
    *    "startDate": "01-JAN-2017",
    *    "endDate": "01-JAN-2017",
    *    "startTime": "02:37:10",
    *    "endTime": "03:37:10",
    *    "priority": "N",
    *    "summary": "event 1 is edited",
    *    "recurrence": "",
    *    "recurrenceEndDate": "",
    *    "location": "Bangalore"
    * };
    * json.id = "CALENDARADD_ID";
    * json.callBack = calendarAddCallback;
    * apz.ns.createCalendarEvent(json);
    */
   createCalendarEvent : function(req) {
      this.callNative(req);
   }, 
   /**
    * It will edit a specific calendar event with details as specified in the remaining fields of the parameter JSON.
    * @param {object} req req includes title, alarm, startDate, endDate , startTime, endTime, newStartDate                       , newStartTime, newEndTime, newEndDate, priority, summary, recurrence, recurrenceEndDate, location, newStartDate, callBack, id.
    * @return {object} Id, status, errorcode.
    * @example
    * var json = {
    *    "title": "event1",
    *    "alarm": "on",
    *    "startDate": "01-JAN-2017",
    *    "endDate": "01-JAN-2017",
    *    "startTime": "02:37:10",
    *    "endTime": "03:37:10",
    *    "newStartDate": "04-JAN-2017",
    *    "newEndDate": "04-JAN-2017",
    *    "newStartTime": "10:37:10",
    *    "newEndTime": "11:37:10",
    *    "priority": "N",
    *    "summary": "event 1 is edited",
    *    "recurrence": "",
    *    "recurrenceEndDate": "",
    *    "location": "Mumbai"
    * };
    * json.id = "CALENDAREDIT_ID";
    * json.callBack = calendarEditCallback;
    * apz.ns.editCalendarEvent(json);
    */
   editCalendarEvent : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API deletes the specified event in Calendar.
    * @param {object} req req includes Title, startDate, endDate, startTime, endTime, callBack, id.
    * @returns {object} object includes Id, Status, errorCode.
    * @example
    *     var json = {
    *    "title": "event1",
    *    "startDate": "04-JAN-2017",
    *    "endDate": "04-JAN-2017",
    *    "startTime": "10:37:10",
    *    "endTime": "11:37:10"
    * };
    * json.id = "CALENDARDELETE_ID";
    * json.callBack = calendarDeleteCallback;
    * apz.ns.deleteCalendarEvent(json);
    * @description <b>OS specific Limitations</b>
    * <br>Windows 8.1 can only create event but cannot edit  the calendar event.<br>Windows has a mandatory field summary.<br>Windows 8.1 allows creation of multiple events.<br>Not supported in Web.<br>Windows  10 ignores seconds from time field.
    * 
    */
   deleteCalendarEvent : function(req) {
      this.callNative(req);
   },
   /**
    * The compass plugin helps to retrieve current geological orientation details.
    * @param {object} req req includes id, callback, periodicity, interval
    * @returns {object} object includes id, errorCode, status, magneticNorth
    * @example
    *        var compass = {
    *           "periodicity": "onChange"//timed,none
    *        };
    *        compass.id = "COMPASSSTART_ID";
    *        compass.callBack = compassStartCallback;
    *        compass.periodicity = apz.getElmValue (“element_dropdown_2");
    *        compass.interval = apz.getElmValue (“interval_com");
    *        apz.ns.startCompass(compass);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   startCompass : function(req) {
      this.callNative(req);
   }, 
   /**
    * The API stops retrieving current geological orientation details.
    * @param {object} req req includes id, callback, periodicity, interval
    * @returns {object} object includes id, errorCode, status
    * @example 
    *  var compass = {};
    *  compass.id = "COMPASSSTOP_ID";
    *  compass.callBack = compassStopCallback;
    *  apz.ns.stopCompass(compass); 
    * <b>Note:</b> The ID for apz.ns.startompass and apz.ns.stopCompass should be same.
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   stopCompass : function(req) {
      this.callNative(req);
   }, 
   /**
    * The storage plugin helps to do the following in the SQLite database available on the mobile device:
    * <br>Create a database
    * <br>Create tables in the database created
    * <br>Insert data into the tables in the database
    * <br>Select data from database
    * <br>Delete data from database.
    * @param {object} req req includes id, callBack, queryId , databaseName, executeQuery.
    * @returns {object} object includes id, errorcode, status.
    * @example
    * var jsonStr = {
    * "queryId": "XYZ78V",
    * "databaseName": "OfflineDb",
    * "executeQuery": "CREATE TABLE DEPARTMENT(ID INT PRIMARY KEY NOT NULL,DEPT CHAR(50) NOT NULL,EMP_ID INT NOT NULL);"
    * };	
    * jsonStr.id = "CREATE_ID";
    * jsonStr.callBack = SQLCallback;
    * apz.ns.executeSql(jsonStr);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)  
	*/
   executeSql : function(req) {
      this.callNative(req);
   }, 
   /**
    * 
    * @param {object} req req includes id, callback.
    * @returns {object} object includes id, status, errorcode, osName, osversion, devtype, isEmulator, screenResolution, connectionType, batterystatus, simDetails.
    * @example
    * var json = {};
 	 * json.id = "DEVICE_ID";
    * json.callBack = deviceInfoCallback;
    * apz.ns.deviceDetails(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)  
	*/
   deviceDetails :function(req) {
      this.callNative(req);
   }, 
   /**
    * This plugin helps to monitor the battery status.
    * @param {object} req req includes Id, callback, state, time, level, threshold.
    * @return {object} object includes  state, level, event, id, status, errorCode .
    * @example
    * jsonobj = {
    * "state": "Y",
    * "level": "Y",
    * "threshold": "",
    * "time": "",
    * }
    * jsonobj.state = inputState;
    * jsonobj.level = inputLevel;
    * jsonobj.threshold = inputhreshold;
    * jsonobj.time = inputtime;
    * jsonobj.id = "BAT_ID";
    * jsonobj.callBack = startbatteryMonitorCallback;
    * apz.ns.startBatteryMonitor(jsonobj);
    */
   startBatteryMonitor : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API stops battery monitoring.
    * @param {object} req req includes Id, callback.
    * @returns {object} object includes Id, Errorcode, status.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "BAT_ID";
    * jsonobj.callBack = stopbatteryMonitorCallback;
    * apz.ns.stopBatteryMonitor(jsonobj);
    * <b>Note: </b>The ID for apz.ns.startBatteryMonitor and apz.ns.stopBatteryMonitor should be same.
    * @description <b>OS Specific Limitation</b>
    * <br>In Android the battery level depends on the hardware and in case of battery monitoring based on level may not be 100% for all the devices.
    * <br>Windows 8.1 does not support Battery monitoring based on STATE
    */
   stopBatteryMonitor : function(req) {
      this.callNative(req);
   }, 
   /**
    * It helps to encrypt files stored in the device application sandbox.
    * @param {object} req req includes id, callback, key, srcFilePath, destFilePath.
    * @returns {object} object includes Id, errorcode, status, filePath.
    * @example
    *  var jsonobj = {
    *   "key": "APPZ9@#$",
    *   "srcFilePath":"/storage/0/com.exceed.appzillonapp/apps/com.iexced.appzillon	app/'docs/Temp.docx",
 	 *   "destFilePath": "docs/Encryptedfile/TempEncrypted.docx",
    *  }
    *  jsonobj.id = "ENCRYPT_ID";
    *  jsonobj.callBack = fileEncryptionSuccess;
    *  apz.ns.encryptFile(jsonobj);
    * @description <b>OS specific Limitations</b><br>Web does not support
    * 
    */
   encryptFile : function(req) {
      this.callNative(req);
   },
   /**
    * It helps to decrypt the encrypted file stored in sandbox.
    * @param {object} req req includes key, srcFilePath, destFilePath, id, callback.
    * @returns {object} object includes Id, errorcode, status, filePath.
    * @example
    *     var jsonobj = {
    *  	"key": "APPZ9@#$",
    * 	"srcFilePath":"/storage/0/com.exceed.appzillonapp/apps/com.iexced.appzillon		app/'docs/Temp.docx",
    *    "destFilePath": "docs/Decryptedfile/Temp.docx",
    *   };
    *  jsonobj.callBack = filedecrtpyCallback;
    *  apz.ns.decryptFile(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   decryptFile : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * It can be implemented in 2 ways. 1. Using Country: 2. Using Location Coordinates:
    * @param {object} req req includes id, callBack, region, countryList, coordinates, radius 
    * @returns {object} object includes id, errorCode, status
    * @example
    * var json = {
    *  "region": "LATLONG",
    *  "countryList": "[]",
    *  "coordinates": "12.9259,77.6229",
    *  "radius": "5000"
    *  };
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   geofencing : function(req) {
      this.callNative(req);
   },

   /**
    * This API retrieves current location data.
    * @param {object} req req includes id, callBack, periodicity, distanceInterval, timeInterval
    * @returns {object} object includes id, status, errorCode, latitude, longitude
    * @example 
    * var gps =	{	
    *  "periodicity": "onChange",//onChange,timed,none
    *  "distanceInterval":"200",
    *  "timeInterval":"1000"
    *  };
    *  gps.id = "LOCATION";
    *  gps.callBack = startgpsCallback;
    *  apz.ns.startLocationTracking(gps);
    *  startgpsCallback = function(gpsRes){
    *     var curr_latitude = gpsRes.latitude;
    *     var curr_longitude = gpsRes.longitude; 
    *  }
    */
   startLocationTracking : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API stops the location manager from retrieving location updates.
    * @param {object} req req includes id, callBack
    * @returns {object} object includes id, errorCode, status
    * @example
    * gps.id = "LOCATION";
    *  gps.callBack = callbackfunc;
    *  apz.ns.stopLocationTracking(gps);
    *  callbackfunc = function(gpsRes){
    *  }
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   stopLocationTracking : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API starts the gesture support based on the input.
    * @param {object} req req includes singletap, doubletap, tripletap, swipe, longpress, pinch, id, callBack
    * @returns {object} object includes id, status, errorCode, event
    * @example
    * var json = {
    *  "singletap": "Y",
    *  "doubletap": "Y",
    *  "tripletap": "N",
    *  "pinch": "N",
    *  "longpress": "N",
    *  "swipe": "N"
    *  }
    *  json.id = "GESTURE_ID";//should be same as stopGesture
    *  json.callBack = gestureCallback;
    *  apz.ns.startGesture(json);
    */
   startGesture : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API stops all the gesture started.
    * @param {object} req req includes id, callBack
    * @returns {object} object includes id, status, errorCode
    * @example 
    * var json = {};
    *  json.id = "GESTURE_ID ";// should be same as startGesture
    *  json.callBack = stopGestureCallback;
    *  apz.ns.stopGesture(json);
    * <b>Note:</b> The ID for apz.ns.startGesture and apz.ns.stopGesture should be same.
    * @description <b>OS Specific Limitations</b>
    * <br>HTML ids are not captured as part of gesture.
    * <br>Not supported in Web.
    * <br>In Android, gestures cannot be started individually based on user preferences. If start gesture is called, it will activate recognition for all gestures supported.
    * <br>Below are the gestures and availability in each container. 
    * <br>Not Supported  in Windows 10 non touch screens.
    */
   stopGesture : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This plugin helps to browse system files with or without filters
    * @param {object} req req includes id, callBack, filter, fileCategory, location, openFile
    * @returns {object} object includes id, errorCode, status, filePath
    * @example
    * var json = {
    *  "filter": "",
    *  "fileCategory": "DEFAULT",
    *  "location": "",
    *  "openFile": "N"
    *  };
    * @description <b>OS specific Limitations</b>
    * <br>File browser is not supported in Web Container.
    * <br>For Windows Surface and desktop, if “Default” filter category is chosen then “Desktop” is opened instead of Root Sandbox, because of this “Filter” field will also not work.
    */
   fileBrowser : function(req) {
      this.callNative(req);
   }, 
   /**
    * The file content plugin helps to read the content of the file.
    * @param {object} req req includes id, callback, filepath.
    * @returns {object} object includes id, errorcode, status, content.
    * @example
    * var json = {};
    * json.filePath = "File Path";
    * json.id = "FILEREAD_ID";
    * json.callBack = fileReadCallback;
    * apz.ns.getFileContent(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   getFileContent : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * The file create plugin helps to create a file in the sandbox. 
    * @param {object} req req includes id, callBack, filePath, fileName, fileContent
    * @returns {object} object includes id, errorCode, status
    * @example
    * var json = {
    *    "filePath": "docs",
    *    "fileName": "SampleFile.txt",
    *    "fileContent": " //This is a sample text file"
    *  };
    *   json.id = "CREATE_FILE";;
    *  json.callBack = createfileCallback;
    *  apz.ns.createFile(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   createFile : function(req) {
      this.callNative(req);
   }, 
   /**
    * The file delete plugin helps to delete a file from the sandbox.
    * @param {object} req req includes  filePath, id, callBack.
    * @returns {object} object includes id, errorcode, status.
    * @example
    * var json = {};
    * json.filePath = "file_path";
    * json.id = "FILEDELETE_ID";
    * json.callBack = fileDeleteCallback;
    * apz.ns.deleteFile(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   deleteFile : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * The open file plugin helps to open the file in any of the supporting applications present in the device.
    * @param {object} req req includes filePath, id, callBack
    * @returns {object} object includes id, errorCode, status
    * @example
    * var json = {
    *  "filePath": "docs/ SampleFile.js "
    *  };
    * json.id = "OPENFILE_ID";
    * json.callBack = openFileCallback;
    * apz.ns.openFile(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   openFile : function(req) {
      this.callNative(req);         
   },   

   /**
    * The Mail plugin enables the developer to do the following. 
    * <br>1. Open mail editor with values filled in its fields <br>2. Send mail using internal server.
    * @param {object} req req includes id, callBack, emailId, recipientMailId, senderMailId, ccIdList, internal, subject, body, filepaths, maxAttachmentSize
    * @returns {object} object includes emailId, status, errorCode, id
    * @example 
    * var email = { 
    *  "mailId": "mail001", 
    *  "recipientMailId": "communications@i-exceed.com", 
    *  "senderMailId": "support@gmail.com", 
    *  "ccIdList": "", 
    *  "internal": "N", 
    *  "subject": "Appzillon Enquiry", 
    *  "body": "Hi,This is a test Mail" 
    * };
    * email.internal=document.getElementById('element_dropdown_1').value; 
    * email.id = "MAIL_ID"; 
    * email.callBack = mailCallback; 
    * apz.ns.sendMail(email); 
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   sendMail : function(req) {
      this.callNative(req);
   }, 

   /**
    * The Contacts addition plugin helps to add a contact to the device memory.
    * @param {object} req req includes id, callback, failureCallback,details  
    * @returns {object} object includes id, errorCode, status
    * @example
    *     var ldetailsjson = {
    *     "firstName": "abc",
    *     "lastName": "xyz",
    *     "phoneMobile": "9876543221",
    *     "phoneWork": "080 23456789",
    *     "phoneHome": "011 12345678",
    *     "mail": "myemail@i-exceed.com",
    *     "website": "www.i-exceed.com",
    *     "address": "Bangalore"
    *     };
    *     lcontactJson.details = ldetailsjson;
    *     lcontactJson.id = "ADDCONTACT_ID";
    *     lcontactJson.callBack = addContactCallback;
    *     apz.ns.addContact (lcontactJson);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   addContact : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API helps to delete a contact from the device memory .
    * @param {object} req req includes id, callBack, firstName, lastName, phoneMobile, phoneWork, phoneHome
    * @example  
    *     var deleteCriteria = {
    *    "firstName": "abc",
    *     "lastName": "xyz",
    *     "phoneMobile": "9800000011",
    *     "phoneWork": "080 23456789",
    *     "phoneHome": "011 12345678"
    *     };
    *     var lcontactJson = {};
    *     lcontactJson. deleteCriteria = deleteCriteria;
    *     lcontactJson.id = "DELETECONTACT_ID";
    *     lcontactJson.callBack = deleteContactCallback;
    *     apz.ns.deleteContact(lcontactJson);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   deleteContact : function(req) {
      this.callNative(req);
   },
   
   /**
    * The Contacts helps to search a contact to the device.
    * @param {object} req req includes id, callback, firstName, lastname, phoneMobile, phoneWork, phoneHome
    * @returns {object} objects includes id, callback 
    * @example 
    *     var lsearchCriteria = {
    *        "firstName": "abc",
    *        "lastName": "xyz",
    *        "phoneMobile": "9800000011",
    *        "phoneWork": "080 23456789",
    *        "phoneHome": "011 12345678"
    *        };
    *        var lcontactJson = {
    *        "searchCriteria": lsearchCriteria
    *        };
    *        lcontactJson.id = "SEARCHCONTACT_ID";
    *        lcontactJson.callBack = searchContactCallback;
    *        apz.ns.searchContact(lcontactJson);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   searchContact : function(req) {
      this.callNative(req);
   },

   /**
    * The Contacts addition plugin helps to update/modify a contact to the device memory     
    * @param {object} req req includes id, callback, details
    * @returns {object} object includes id, callback
    * @example
    * var ldetailsjson = {
    *    "firstName": "abc",
    *    "lastName": "xyz",
    *    "phoneMobile": "9800000011",
    *    "phoneWork": "080 23456789",
    *    "phoneHome": "011 12345678",
    *    "mail": "myemail@i-exceed.com",
    *    "website": "www.i-exceed.com",
    *    "address": "Bangalore"
    *    };
    *    var lsearchCriteria = {
    *    "firstName": "abc",
    *    "lastName": "xyz",
    *    "phoneMobile": "800000011",
    *    "phoneWork": "080 23456789",
    *    "phoneHome": "011 12345678"
    *  };
    *   var lcontactJson = {};
    *   lcontactJson.details = ldetailsjson;
    *   lcontactJson.searchCriteria = lsearchCriteria;
    *    lcontactJson.id = "EDITCONTACT_ID";
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
    editContact : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API helps open the Native Contact UI and get the name, phone number and email of the chosen contact.
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, name, phoneno, email 
    * @example
    * json.id = "SEARCHCONTACT_ID";
    * json.callBack = fetchContactCallback;
    * apz.ns.fetchContact(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   fetchContact : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API loads the map and displays the driving route between 2 points, both of which are specified or the user, or 1 of which is specified by the user(in which case current location is taken as from location). 
    * @param {object} req req includes fromLocation, toLocation, id, callBack ,mapKey
    * @returns {object} object includes id, status, errorCode
    * @example
    * var json = {
    *  "mapKey":"google_maps_key",
    *  "fromLocation": "12.9255693, 77.61964749999993",
    *  "toLocation": "12.9718915, 77.64115449999997"
    *  };
    *  json.id = "MAP2_ID";
    *  json.callBack = drivdirecCallback;
    *  apz.ns.drivingDirection(json);
    * @description <b>OS specific Limitations</b>
    * <br>In Windows Phone dragging location points is not supported.Instead, Windows phone allows the user to tap anywhere on the map and re-draws the route. When the user taps on a new location, he/she is asked which location to change,  To or From. Based on user input, the new route is re-drawn.
    * <br>For iOS, there is a specific list of countries for which driving directions will work. For the list, please refer to iOS_countrylist.
    */
   drivingDirection : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API loads the map with certain user defined points, along with the current location of the device, displayed in the map.
    * @param {object} req req includes markerInfo, id, callback ,mapKey
    * @returns {object} object includes id, status, errorCode
    * @example 
    * var json = {
    *  "mapKey":"google_maps_key",
    *  "markerInfo": [{
    *  "locationLatitude": "12.9255339",
    *  "locationLongitude": "77.61951599999999",
    *  "locationName": "Me",
    *  "locationDescription": "My Location"
    *  }]
    *  }
    *  json.id = "MAP3_ID";
    *  json.callBack = locCallback;
    *  apz.ns.loadMap(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   loadMap : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API loads the map and focuses on a certain area defined using current location and corresponding radius. Also, a set of points are passed in the API, and the ones among them lying in the above defined area will be displayed. The user can change the central location at any time by tapping anywhere on the map.
    * @param {object} req req includes radius, nearbyplaces, id, callback ,mapKey
    * @returns {object} object includes id, status, errorCode
    * @example
    * var json = {
    *     "radius": 500,
    *     "mapKey":"google_maps_key",
    *     "nearbyplaces": [{
    *     "locationLatitude": "12.928276105253065",
    *     "locationLongitude": "77.61171340942383"
    *     }, {
    *     "locationLatitude": "12.920203271315359",
    *     "locationLongitude": "77.62982368469238"
    *     }]
    *     }	
    *     json.id = "MAP1_ID";
    *     json.callBack = selcapCallback;
    *     apz.ns.locationSelector(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   locationSelector : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * @param {object} req req includes id, callBack
    * @returns {object} object includes id, status, errorCode, locale
    * @example
    * var locale = {};
    *  locale.id = "LOCALE_ID";
    *  locale.callBack = localeCallback;
    *  apz.ns.currentLocale(locale); 
	* @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   currentLocale : function(req) {
      this.callNative(req);
   }, saveReport : function(req) {
      this.callNative(req);
   }, sendReq : function(req) {
      this.apz.initNativeService(req);
      var params = req.params;
      var reqFull = params.reqFull;
      this.counter = this.counter + 1;
      reqFull.appzillonHeader.clientNonce = this.counter;
      reqFull.appzillonHeader.sessionToken = this.sessionToken;
      reqFull.appzillonHeader.serverNonce = this.serverNonce;
      var url = params.url.replace("https://", "http://");
      var encReq = null;
      var internalErr = false;
      var resObj = req;
      resObj.params = {id: params.id, reqId: params.reqId};
      if (this.apz.encryption == "Y") {
         encReq = {};
         encReq.appzillonSafe = apzIde.encryptWithPublicKey(this.safeToken, this.publicKey);
         encReq.appzillonHeader = apzIde.encryptWithKey(JSON.stringify(reqFull.appzillonHeader), this.safeToken);
         encReq.appzillonBody = apzIde.encryptWithKey(JSON.stringify(reqFull.appzillonBody), this.safeToken);
         if(this.apz.isNull(encReq.appzillonSafe) || this.apz.isNull(encReq.appzillonHeader) || this.apz.isNull(encReq.appzillonBody)){
            internalErr = true;
         }
      }
      if (this.apz.dataIntegrity == "Y") {
         var apzQopStr = apzIde.getQualityOfPayload(JSON.stringify(params.reqFull), this.counter, this.serverNonce);
         var apzQOP = {
            "appzillonQop": apzQopStr
         };
         encReq = $.extend({}, apzQOP, (encReq || params.reqFull));
         if(this.apz.isNull(apzQopStr)){
            internalErr = true;
         }
      }
      if(!internalErr){
         var reqStr = encReq ? JSON.stringify(encReq) : JSON.stringify(params.reqFull);
         var nsObj = this;
         $.ajax({
            url: url,
            type: params.method,
            cache: false,
            data: reqStr,
            contentType: 'application/json',
            dataType: 'json',
            async: params.async,
            success: function(res) {
               resObj.params.status = true;
               req.status = true;
               var resFull = null;
               var encStr = "";
               if (nsObj.apz.encryption == "Y") {
                  resFull = {};
                  var decrypSafeToken = apzIde.decryptWithPublicKey(res.appzillonSafe, nsObj.publicKey);
                  var decrypHeader = apzIde.decryptWithKey(res.appzillonHeader, decrypSafeToken);
                  var decrypBody = apzIde.decryptWithKey(res.appzillonBody, decrypSafeToken);
                  var decrErrors = "";
                  if (res.appzillonErrors) {
                     decrErrors = apzIde.decryptWithKey(res.appzillonErrors, decrypSafeToken);
                     if(nsObj.apz.isNull(decrErrors)){
                        internalErr = true;
                     } else {
                        resFull.appzillonErrors = JSON.parse(decrErrors);
                     }
                  }
                  nsObj.safeToken = decrypSafeToken;
                  if(nsObj.apz.isNull(decrypSafeToken) || nsObj.apz.isNull(decrypHeader) || nsObj.apz.isNull(decrypBody)){
                     internalErr = true;
                  } else {
                	 try{
	                     resFull.appzillonHeader = JSON.parse(decrypHeader);
	                     resFull.appzillonBody = JSON.parse(decrypBody);
                	 } catch (e){
                		 resFull.appzillonBody = {};
                	 }
                  }
                  if(!internalErr){
                	  if(!nsObj.apz.isNull(decrErrors)){ 
                	   encStr = '{"appzillonHeader":"'+decrypHeader +'","appzillonBody":"'+ decrypBody+'","appzillonErrors":"'+ decrErrors+'"}';
                	  }else{
                	   encStr = '{"appzillonHeader":'+decrypHeader +',"appzillonBody":'+ decrypBody+'}';  
                	  }
                	}
               }
               if (nsObj.apz.dataIntegrity == "Y") {
                  var sQop = res["appzillonQop"];
                  delete res["appzillonQop"];
                  var cQop = "";
                  if(!nsObj.apz.isNull(encStr)){
                	  cQop = apzIde.getQualityOfPayload(encStr, nsObj.counter, nsObj.serverNonce);
                  } else { 
                	  //cQop = apzIde.getQualityOfPayload(JSON.stringify(res), nsObj.counter, nsObj.serverNonce);
                	  //// TBC - Commenting above and adding below as temp fix suggested for ISO service sorting issue.
                	  cQop = sQop; 
                  }
                  if (cQop != sQop) {
                     internalErr = true;
                  }
               }
               resObj.params.resFull = resFull || res;
               if(internalErr){
                  req.status = false;
                  resObj.params.resFull.appzillonErrors = [{"errorCode":"APZ-CNT-330"}];
               }
               Apz.nativeServiceCB(req);
            },
            error: function(res) {
               resObj.status = true;
               resObj.params.status = false;
               Apz.nativeServiceCB(req);
            }
         });
      } else {
    	 resObj.status = false;
         resObj.params.resFull = {"appzillonErrors":[{"errorCode":"APZ-CNT-330"}]};
         Apz.nativeServiceCB(req);
      }
   }, 
   
   /**
    * A native UI will be launched with signature pad and base64 string will be returned after capturing the signature. Native UI will have options to save, cancel and reset.
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, status, errorCode, encodedImage
    * @example 
    * var params = {};
    *  params.id = "SIGNATUREPAD_ID";
    *  params.callBack =signaturepadCallback;
    *  apz.ns.signaturePad(params);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   signaturePad : function(req) {
      this.callNative(req);
   },

   /**
    * This API helps to authenticate users via Facebook.
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, status, errorCode, fbId, email, name, pictureURL, firstName, lastName, gender, locale
    * @example
    * var params = {};
    * params.id = "FACEBOOK_ID";
    * params.callBack = facebookCallback;
    * apz.ns.facebookLogin(params);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
    facebookLogin : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API helps to authenticate user via Google+.
    * @param {object} req req includes id, callback, clientId
    * @returns {object} object includes id, status, errorCode, gplusId, email, name, pictureURL, firstName, lastName, gender, locale
    * @example
    * var params = {};
    * params.id = "GOOGLEPLUS_ID";
    * clientId = "CLIENT_ID_FROM_GOOGLEPLUS_CONSOLE"
    * params.callBack = googlePlusInCallback;
    * apz.ns.googleLogin(params);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   googleLogin : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API helps to authenticate the user using Linked In.
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, status, errorCode, linId, email, name, pictureURL, firstName, lastName, gender, locale
    * @example
    * var params = {};
    * params.id = "LINKEDIN_ID";
    * params.callBack = linkedInCallback;
    * apz.ns.linkedinLogin(params);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   linkedinLogin : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API helps to authenticate user using Twitter.
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, status, errorCode, twitId, screenName, name, pictureURL
    * @example
    * var params = {};
    * params.id = "TWITTER_ID";
    * params.callBack = twitterCallback;
    * apz.ns.twitterLogin(params);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   twitterLogin : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * The API enables to open the youtube link in the youtube application/browser.
    * @param {object} req req includes id, callback, url
    * @returns {object} object includes id, status, errorCode 
    * @example
    * var json ={
    * "url":"https://www.youtube.com/watch?v=z4P4lmW4dSo",
    *  }
    * json.id = "YOUTUBE";
    * json.callBack = youtubeCallback
    * apz.ns.youtube(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   youtube : function(req) {
      this.callNative(req);
   }, 
   /**
    * This function is used to enable the timer for screen inactivity and calls the callback function when timeout occurs. You can use this function after login to track when to logout the session after inactivity.
    * @param {object} req req includes Id and Callback
    * @example
    * var params = {};
    * params.id = "EXPIRE_ID";
    * params.callBack = timerCallback;
    * apz.ns.startIdleTimer(params);
    * @returns {object} object includes Id, event, status, ErrorCode.
    * Event: This returns “timerExceeds” when the timeout occurs.
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   startIdleTimer : function(req) {
      this.callNative(req);
   }, 
   /**
    * This helps to lock the screen in the current orientation, which can be portrait or landscape.
    * @param {object} req req includes id, callBack
    * @returns {object} object includes id, errorcode, status.
    * @example
    * json.callBack = lockRotationCallback;
    * json.id = "LOCKROTATION";
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   lockRotation : function(req) {
      this.callNative(req);
   }, 
   /** 
    * @param {object} req req includes id, callback.
    * @returns {object} object includes id, errorcode, status.
    * @example
    * var json = {}
    * json.callBack = unlockRotationCallback;
    * json.id = "LOCKROTATION";
    * apz.ns.unlockRotation (json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   unlockRotation : function(req) {
      this.callNative(req);
   }, 

   /**
    * @param {object} req id, callBack, orientation .
    * @returns {object} object includes id, errorCode, status.
    * @example
    * var json = {};
    * json.id = "ORIENTATION";
    * json.callBack = orienCallback;
    * json.orientation  = " PORTRAIT ";
    * apz.ns.setOrientation (json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   setOrientation : function(req) {
      this.callNative(req);
   }, 
   /**
    * The plugin helps to activate device vibration which can be implemented by apps based on business requirements.
    * @param {object} req req includes time, id, callback.
    * @returns {object} object includes id, errorcode, status.
    * @example
    * var jsonobj = {
 	 * "time":"600",
    * };
    * jsonobj.id = "VIBRATE";
    * jsonobj.callBack=vibrateCallBack;
    * apz.ns.vibrate (jsonobj);
    * @description <b>OS Specific Limitations</b>
    * <br>Vibrate plugin is not supported in Windows 8.1 Surface and Web.
    * <br>Max and min vibrate duration for different OS’s are as follows
    * <br>Windows phone   	:  	Max 5 Seconds
    * <br>Android		:	No restriction
    * <br>iOS			: 	Always 2 Seconds
    * <br>Windows 8.1 Phone	:	Max 5 seconds
    * 
    */
   vibrate : function(req) {
      this.callNative(req);
   },

   /**
	* This plugin will convert speech to text.
    * @param {object} req req includes id, callback, languageCode, action,pauseRecognize,supportsOnDeviceRecognition.
    * @returns {object} object includes id, errorcode, status, text , action.
	* <br><b>action</b> action can take value as follows.
	* <br><b>VALUE</b> when action is VALUE, result will be returned in text.
	* <br><b>STARTED</b> indicates voice capture is started.
	* <br><b>STOPPED</b> indicates voice capture is stopped.
	* <br><b>PAUSED</b> if pauseRecognize is Y, then action will be PAUSED with response in text.
	* <br><b>RESUMED</b> indicates voice capture is resumed.
    * @example
    * var jsonobj = {}; 
    * jsonobj.id = "VOICE";
	* jsonobj.languageCode = "en-IN";
	* jsonobj.action = "START"; // RESUME/STOP. 
	* jsonobj.pauseRecognize = "Y"; // or N
	* jsonobj.supportsOnDeviceRecognition = "Y"; // or N
    * jsonobj.callBack = callbackVoice;
    * apz.ns.voiceToText(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png)
	*/
   voiceToText : function(req) {
      this.callNative(req);
   },

   /**
    * The events plugin helps to trap a supported event. The call back function is called whenever the an event is triggered.
    * @param {object} req req includes allEvents, backButtonEvent, menuButtonEvent, volUpButtonEvent, volDownButtonEvent, appPausedEvent, appResumedEvent, appSearchButtonEvent, appCallStartEvent, appCallEndEvent
    * @returns {object} object includes event, status, id, errorcode.
    * @example
    * var json ={
    * "allEvents":"on"                                                                    
    * };
    * json.callBack = eventCallBack;
    * json.id = "EventID";
    * apz.ns.detectEvents(json);
    * @description <b>OS specific Limitations</b>
    * </br>Volume button press event is not supported by iOS, Windows Surface and Windows phone container.</br>Windows Surfaces does not support any event other than orientation event.
    */
   detectEvents : function(req) {
      this.callNative(req);
   }, wipeOut : function(req) {
      this.callNative(req);
   },
   /**
    * This API returns the IP address of the device.
    * @param {object} req req includes id, callback.
    * @returns {object} object includes id, status, errorCode, ip.
    * @example
    * var json = {};
    * json.id = "IP";
    * json.callBack = ipCallback;
    * apz.ns.getIP(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   getIP : function(req) {
      this.callNative(req);
   }, 
   /**
    * @param {object} req req includes appId, id, callback.
    * @returns {object} object includes id, errorCode, status, appVersion.
    * @example
    * json.id = "APPVERSION";
    * json.callBack = versionCallback;
    * json.appId = "com.iexceed.containerapp";
	 * apz.ns.getAppVersion(json)
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   getAppVersion : function(req) {
      this.callNative(req);
   }, closeApplication : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API opens the external pages in a new view.
    * @param {object} req req includes viewId, location, percentage, targetView, launchPage, callback, id	.
    * @returns {object} object includes id, errorcode, status.
    * @example
    *  var json = {
    *    "viewId": "Appzillon",
    *    "location": "horizontal",
    *    "percentage": 50,
    *    "targetView": "main",
    *    "launchPage": "https://www.google.co.in"
    * };
    * json.id = "CLOSEMULVIE_ID";
    * json.callBack = openMulviewCallback;
    * apz.ns.multiviewOpen(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   multiviewOpen : function(req) {
      this.callNative(req);
   }, 
   /**
    * This enables the developer to close the multiview which was opened using multiview.
    * @param {object} req req includes viewId, callback, id.
    * @returns {object} object includes id, errorcode, status.
    * @example
    *     var json = {
    *    "viewId": "Appzillon"
    * };
    * json.id = "CLOSEMULVIE_ID";
    * json.callBack = closeMulviewCallback;
    * apz.ns.multiviewClose(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   multiviewClose : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * @param {object} req req includes id, callBack, refNo
    * @returns {object} object includes id, status, errorCode
    * @example 
    * var jsonobj = {};
    *  jsonobj.id = "NOTES";
    *  jsonobj.callBack = captureNotesCallback;
    *  jsonobj.refNo = "1";
    *  apz.ns.captureNotes (jsonobj);
    */
   captureNotes : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This plugin helps to refresh the screen data or do some activity when user is pulling down from the top of the screen in the device.
    * @param {object} req req includes screenId, callId, id, callBack
    * @returns {object} object includes id, status, errorCode, event
    * @example
    * var json = {
    *    "screenId": "FirstPage",
    *     "callId": "123"
    *  };
    *  json.id = "PULLDOWN_ID";
    *  json.callBack = enablepulldownCallback;
    *  apz.ns.enablePullDown(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   enablePullDown : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API disables the pulldown feature of the application
    * @param {object} req req includes id, callBack
    * @returns {object} object includes id, status, errorCode
    * @example
    * json.id = "DISPULLDOWN_ID";
    * json.callBack = disablepulldownCallback;
    * apz.ns.disablePullDown(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   disablePullDown : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This plugin hides the loader hence stops refresh
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, status, errorCode
    * @example
    * var json = {};
    *  json.id = "HIDE_REFRESH";
    *  json.callBack = hideCallback;
    *  apz.ns.hideRefresh(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   hideRefresh : function(req) {
      this.callNative(req);
   }, 
   /**
    * The Augmented Reality pluginhelps to show nearby places using camera view.
    * @param {object} req req includes
    * <br> thresholdDistance, maxPlaces, Places :[{image,title,description,additionalInfo,latitude,longitude,category,regionCode,id,appId}], id, callBack.
    * <br><b>maxPlaces:</b> Number of places to be shown in the camera.
    * <br><b>Places:</b>Details of places in a JSON array.
    * 
    *• image: Name of the image file
    *• title: Title of the place
    *• description: Description about the place
    *• additionalInfo : Additional info if any , for example offers and promotions.
    *• latitude: The latitude of the place
    *• longitude: the longitude of the place
    *• regionCode: Name of the city e.g. Bengaluru.
    *• appId: The application ID from which the call to the API is made.
    * <br><b>thresholdDistance:</b> The distance in meters (callBack with event in the JSON parameter  will be called if the device is moving beyond given threshold distance)thresholdCallBack : A callback function called on reaching the threshold distance. The Callback will contain the following JSON parameter:
    * @returns {object} object includes Id, status, Errorcode, event, latitude, longitude.
    * <br><b>event:</b>  After crossing the threshold distance the callback is called with event as “threshold”.
    * <br><b>latitude:</b> After reaching the threshold distance the callback returns the latitude of the current location in this parameter.
    * <br><b>longitude:</b> After reaching the threshold distance the callback returns the longitude of the current location in this parameter.
    * @example
    *     var pjson = {
    *        "thresholdDistance": "200",
    *        "maxPlaces": "3",
    *        "Places": [{
    *            "image": "bitcoin.png",
    *            "title": "Sanjeevnam",
    *            "description": "Authentic Kerala food",
    *            "additionalInfo": "10% Off",
    *            "latitude": "12.925335",
    *            "longitude":"77.619375",
    *            "category":"Restaraunt"
    *        }, {
    *            "image": "drive.png",
    *            "title": "Fab India",
    *            "description": "Welcome to Fab India",
    *            "additionalInfo": "",
    *            "latitude": "12.924886095784546",
    *            "longitude":"77.61911798268557",
    *            "category":"Restaraunt"
    *        }, {
    *            "image": "steam.png",
    *            "title": "Toshiba Software",
    *            "description": "Software",
    *            "additionalInfo": "Java Opening",
    *            "latitude": "12.924932498769708",
    *            "longitude":"77.61957630515099",
    *            "category":"IT"
    *        }, {
    *            "image": "image_8.jpg",
    *            "title": "Athappar",
    *            "description": "Non Veg",
    *            "additionalInfo": "food",
    *            "latitude": "12.925923298479596",
    *            "longitude":"77.61824056506157",
    *            "category":"Restaraunt"
    *        }, {
    *            "image": "group.png",
    *            "title": "Masjid-e-noor",
    *            "description": "Pray to god",
    *            "additionalInfo": "Discount to heaven",
    *            "latitude": "12.924908970496634",
    *            "longitude":"77.61847794055939",
    *            "category":"Pray"
    *        }],
    *    };
    *    pjson.id = "AUGMENTEDREALITY_ID";
    *    pjson.callBack = ARCallback;
    *    apz.ns.startAugmentation(pjson);
    * @description <b>OS Specific Limitation</b>
    * <br> In Android , the distance between the device and required place should be in between 30-100 meters.The device should be held absolutely straight i.e. perpendicular to the ground. The movement of the device should be slow for precise results and the result may vary based on the sensitivity of the accelerometer and compass of the device.
    * <br> Windows does not support Augmented Reality.
    * 
    */
   startAugmentation : function(req) {
      this.callNative(req);
   }, reloadAugmentation : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API helps to capture/record videos in  application.
    * @param {object} req req includes Id, Callback, overwrite, fileName.
    * <br><b>fileName:</b> The video file will be saved with this name.
    * <br><b>fileOverwrite:</b> It specifies whether the file with the particular name will be overwritten or not. If the value is N, then timestamp is added along with the file name before saving. 
    * @returns {object} object includes Id, status, Errorcode, filepath
    * <br><b>filePath:</b> The path where the video file is saved.
    * @example
    * var json = {};
    * json.callBack = videocallback;
    * json.id = "VIDEO";
    * json.fileOverwrite = "Y";
    * json.fileName = "temp";
    * apz.ns.videoRecording(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   videoRecording : function(req) {
      this.callNative(req);
   }, 
   /**
    * The audio plugin enables the developer to record/play/save/pause any audio file created by app.
    * @param {object} req req includes
    * <br>id, callBack, location, action, fileName, base64, channel, samplingRate, bitRate , wavFileFormat, timeDuration.
    * <br><b>action:</b> The value for this parameter can be Record, Play, Save or Pause.
    * <br><b>base64:</b>This parameter should contain Y if you want Base64 of the recorded audio file else should return N.
    * <br><b>location:</b>If the action is Play , then this parameter is used as the location where the audio item is present. If the action is Record then the same location is where the file will be saved. Possible values are as below.
    * <br> ◦ default: Default location to store recorded files.
    * <br> ◦ external: App Sandbox. 
    * <br><b>fileName:</b> Name of the file to be recorded/played.
	* <br><b>channel:</b> channels can be mono/stereo -optional.
	* <br><b>samplingRate:</b> sampling rate -valid value (in hertz) -optional.
	* <br><b>bitRate:</b> bit rate -optional.
	* <br><b>wavFileFormat:</b> mandatory -  plays .wav audio file if "Y",else default audio format.
	* <br><b>timeDuration:</b> max duration of audio recording (in ms)//saves automatically if mentioned.
    * @returns {object} object includes Id, status, Errorcode, base64, event.
    * <br><b>text:</b>This returns the base64 string of the file recorded.
    * <br><b>event:</b>This returns the event based on which the callback is called. This  can contain “Paused”,”Playing” and”Audio Stopped and saved”.
    * @example
    * var audio = {
    * "location": "internal",//internal,external
    * "action": "record",
    * "fileName": "recordtest"
    * };
	* audio.id = "AUDIORECORD_ID";
	* audio.channel = "stereo";
	* audio.samplingRate = "44100";
	* audio.bitRate = "16";
	* audio.wavFileFormat = "N";
    * audio.timeDuration = "10000";
    * audio.callBack = audiorecordCallback;
    * apz.ns.audio(audio);
    * @description <b>OS specific Limitations</b>
    * <br>Not Supported in Web
    * <br>Windows doesn’t support pause and resume during recording.
    * <br>Android doesn’t support pause and resume during recording.
    */
   audio : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This plugin helps to retrieve all the notifications received by the Application from the persistence.
    * @param {object} req req includes id, callBack
    * @returns {object} object includes notfication[{ID, MSG, Timestamp, read}]
    * @example 
    * var json = {};
    *  json.id = "GET_NOTI";
    *  json.callBack = getnotiCallback;
    *  apz.ns.getNotification(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   getNotification : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API deletes a notification received by the Application from the persistence.
    * @param {object} req req includes id, callback, notificationID
    * @returns {object} object includes id, status, errorCode
    * @example
    * var json = {
    *     "notificationID": "1"
    *  };
    *  json.id = "DELNOTIF";
    *  json.callBack = deleteNotifCallBack;
    *  apz.ns.deleteNotification(json)
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   deleteNotification : function(req) {
      this.callNative(req);
   }, updateNotification : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API helps to launch an app from any other app in case of Multiple apps.
    * @param {object} req req includes id, appId, callBack
    * @returns {object} object includes id, status, errorCode
    */
   launchApp : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * The API enables the developer to delete a child app.
    * @param {object} req req includes appId, id, callback
    * @returns {object} req req includes id, callback, status, errorCode
    * @description <b>OS Specific Limitations</b>
    * <br>OTA is not supported in Windows 8.1.
    */
   subappDelete : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API helps to get app details from the server for any app(main or child).
    * @param {object} req req includes appId, id, callBack	
    * @returns {object} object includes appId, appVersion, containerApp, expired, expiryDate, otaReq, parentAppId, wipeout, upgradeRequired, id, callBack
    * 
    */
   getInstructions : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * The API is implemented to check whether container (main) app is to be upgraded.
    * @param {object} req req includes appId, id, callBack
    * @returns {object} object includes id, status, errorCode, ugradeRequired
    */
   upgradeRequired : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * The API enables the developer to pull the changes (new files, modified files and files to be deleted) for an app(child apps or main app) from the server using OTA functionality, thus upgrade the app.
    * @param {object} req req includes appId, id, callBack
    * @returns {object} object includes id, status, errorCode
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp;
	*/
   upgradeApp : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API is used to store user settings in the device persistence storage.
    * 
    * @param {object} req req includes id, callback, userprefs.
    * @returns {object} object includes id, status, errorcode.
    * @example
    * var json = {};
    * json.id = "setUserPrefs";
    * json.callBack = setPrefCallback;
    * json.userprefs = {"THEME":"Appzillon"};
    * apz.ns.setUserPrefs(json)
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   setUserPrefs : function(req) {
      this.callNative(req);      
   }, offlineData : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * @param {object} req req includes phoneNo, message, type, id, callback
    * @returns {object} object includes id, status, errorCode, encodedImage
    * @example
    * var JsonObject = {
    *   "phoneNo": "999999999",
    *   "message": "Hi sms Testing ",
    *   "type": "UI"
    *  }
    *  JsonObject.id = "BEACONSTART_ID";
    *  JsonObject.callBack = sendSMSCallback;
    *  apz.ns.smsSend(JsonObject);
    * @description <b>OS Specific Limitations</b>
    * <br>This API can be used provided the device contains a SIM.
    * <br>Background send SMS will work only in Android
    */
   smsSend : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This plugin helps to start Listener to SMS received by the device.
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, status, errorCode, number, message
    * @example
    * json = {};
    *  json.id = "SMSLIST_ID";
    *  json.callBack = startSMSCallback;
    *  apz.ns.startSMSListener(json);
    */
   startSMSListener : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * The API helps to stop SMS Listener.
    * @param {object} req req includes id, callBack
    * @returns {object} object includes id, status, errorcode
    * @example
    *  json = {};
    *  json.id = "SMSLIST_ID ";
    *  json.callBack = stopSMSCallback;
    *  apz.ns.stopSMSListener(json);
    * <b>Note:</b> The ID for apz.ns.startSMSListener and apz.ns.stopSMSListener should be same.
    * @description <b>OS Specific Limitation</b>
    * <br>This plugin can be used provided the device includes a SIM.
    * <br>SMS not supported in Web and Windows 8.1Surface/Desktop
    */
   stopSMSListener : function(req) {
      this.callNative(req);
   }, store : function(req) {
      this.callNative(req);
   }, retrieve : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API starts listening to the soft keyboard.
    * @param {object} req req includes id, callBack
    * @returns {object} object includes id, status, errorCode, event
    * @example 
    * var json = {};
    *  json.id = "KEYBOARDLISTENER";
    *  json.callBack = keyboardstartCall;
    *  apz.ns.startKeyboardListener(json);
    */
   startKeyboardListener : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API stops listening to the soft keyboard.
    * @param {object} req req includes id, callBack
    * @returns {object} object includes id, status, errorCode
    * @example 
    * var json = {};
    * json.id = "KEYBOARDLISTENER";
    * json.callBack = keyboardstopCall;
    * apz.ns.stopKeyboardListener(json);
    * <b>Note:</b> The ID for apz.ns.startKeyboardListener and apz.ns.stopKeyboardListener should be same.
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   stopKeyboardListener : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API enables the application to listen to the notification received.
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, params, text, status, errorCode
    * @example
    * var json = {};
    *  json.id = "NOTIFICATIONSTRTLIST_ID";
    *  json.callBack = noficationstartListenerCallback;
    *  apz.ns.startNotificationListener(json);
    */
   startNotificationListener : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API disables the application to listen to the notification received.
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, status, errorCode
    * @example
    * var json = {};
    *  json.id = "NOTIFICATIONSTOLIST_ID";
    *  json.callBack = noficationstopListenerCallback;
    *  apz.ns.stopNotificationListener(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   stopNotificationListener : function(req) {
      this.callNative(req);
   }, 

   /**
    * This API enables the application to listen to app shortcuts /3D touch.The App shortcut ID and labels shall be initialized in Appzillon ->Preferences ->Shortcut Items.The selected App shortcut ID is returned to the callback to perform further tasks.
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, params, event ,shortcutID, status, errorCode
    * @example
    * var json = {};
    *  json.id = "SHORTCUTSTRTLIST_ID";
    *  json.callBack = shortcutstartListenerCallback;
    *  apz.ns.startShortcutListener(json);
    */
   startShortcutListener : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API disables the application to listen to app shortcuts /3D touch.
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, status, event, errorCode
    * @example
    * var json = {};
    *  json.id = "SHORTCUTSTRTLIST_ID";
    *  json.callBack = shortcutstartListenerCallback;
    *  apz.ns.stopShortcutListener(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   stopShortcutListener : function(req) {
      this.callNative(req);
   },

   /**
    * This API is used to get the value based on the key from device persistence storage.
    * @param {object} req req includes id, callback, key.
    * @returns {object} object includes id, status, errorcode, value.
    * @example
    * var json = {};
    * json.id = "GET_PREF";
    * json.callBack = getprefCallback;
    * json.key = "KEY";
    * apz.ns.getPref(json);
    * 
    */
   getPref : function(req) {
      this.callNative(req);
   }, 
   /**
    *  This API is used to store key-value pair in the device persistence storage.
    * @param {object} req req includes id, callBack, key, value.
    * @returns {object} object includes id, status, errorcode.
    * @example
    * var json = {};
    * json.id = "SET_PREF";
    * json.callBack = setprefCallback;
    * json.key = "Name";
    * json.value = "IEXCEED";
    * apz.ns.setPref(json);
    */
   setPref : function(req) {
      this.callNative(req);
   }, callNativeCBwithErrorCode : function(req) {
      this.callNative(req);
   }, 
   /**
    * The biometric authentication plugin helps to validate the user fingerprints with registered fingerprints.
    * @param {object} req req includes Id, callback.
    * @returns {object} object includes Id, status, errorcode.
    * @example
    * var params = {};
    * params.id = "BIOMETRIC_ID
    * params.callBack = biometricCallback;

    * apz.ns.biometricAuth(params);
    * @description <b>OS specific Limitations</b>
    * <br>Windows 8.1 doesn’t support this plugin.
    * <br>In iOS a native UI will be opened on invoking the API and fingerprints will be scanned and validated with the registered fingerprints.
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   biometricAuth : function(req) {
      this.callNative(req);
   }, 
   /**
    * This plugin helps to send message via whatsapp application.
    * @param {object} req req includes id, callback, message.
    * @returns {object} object includes id, status, errorcode.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "WHATSAPP";
    * jsonobj.callBack = whatsAppCallback;
    * jsonobj.message = "your_message";
    * apz.ns.whatsApp(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   whatsApp : function(req) {
      this.callNative(req);
   }, getSimInfo : function(req) {
      this.callNative(req);
   }, sendSmsBySID : function(req) {
      this.callNative(req);
   }, 
   /**
    * 
    * @param {object} req req includes id, callBack, crop, targetWidth, targetHeight, quality, OCR, fileName.
    * @returns {object} object includes id, status, errorcode, encodedImage
    * @example
    * var json ={ };
    * json.id = "DOCSCAN_ID";
    * json.callBack = docScannerCallback;
    * json.crop = "Y";
    * json.targetHeight = "100";
    * json.targetWidth = "100";
    * json.quality= "50";
    * json.fileName = "Temp";
    * apz.ns.documentScanner(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   documentScanner : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * The file upload plugin allows a user to upload a file present in his device to the Appzillon server.
    * @param {object} req req includes id, callBack, fieldID, sessionReq, destination, overWrite 
    * @returns {object} object includes id, errorCode, status
    * @example
    * var json = {
    *    "fieldID": "",
    *    "destination": "Upload/user1",
    *    "overWrite": "Y"
    * };
    *  json.filePath = "file Path";
    *  json.sessionReq = "Y"; //Y or N
    *  json.id = "UPLOADFILE_ID";
    *  json.callBack = uploadFileCallback;
    *  apz.ns.uploadFile(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   uploadFile : function(req) {
      this.callNative(req);
   },
   /**
    * The file download plugin allows a user to download a file present in the Appzillon server. 
    * @param {object} req req includes  id, callBack, fileName, base64, destinationPath, filePath, sessionReq.
    * @returns {object} object includes id, errorCode, status, filePath, base64.
    * @example
    *     var json = {
    *    "destinationPath": "docs",
    *    "filePath": "Upload/user1",
    *    "sessionReq": "Y" //Y or N
    * };
    * json.destinationPath = "docs";
    * json.fileName = "fileName";
    * json.base64 = "N";
    * json.filePath = "filePath";
    * json.id = "DOWNLOADFILE_ID";
    * json.callBack = downloadFileCallback;
    * apz.ns.downloadFile(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   downloadFile : function(req) {
      this.callNative(req);
   },  

   /**
    * Send NFC plugin helps to do the following
    * <br> Write message to another NFC enabled device or tag.
    * <br> Write URLs to another NFC enabled device or tag.
    * <br>Until NFC stop API is called, it continues to send messages.
    * @param {object} req req includes id, callBack, type, action, content 
    * @returns {object} object includes id, status, errorCode
    * @example
    *  var jsonobj = {
    *  "type": "URL",
    *  "content": "http://www.google.com",
    *  "action":"DEVICE"
    *  };
    *  jsonobj.id = "NFCID";
    *  jsonobj.callBack = sendNFCCallback;
    *  apz.ns.sendNFC(jsonobj);
    * 
    * OR
    * 
    * var jsonobj = {
    *  "type": "MSG",
    *  "content": "NFC Message",
    *  "action":"DEVICE"
    *  };
    *  jsonobj.id = "NFCID";
    *  jsonobj.callBack = sendNFCCallback;
    *  apz.ns.sendNFC(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   sendNFC : function(req) {
      this.callNative(req);
   },
   
   /**
    * Receive NFC plugin helps to do the following.
    * <br>Receive message from another NFC enabled device or tag
    * <br>Until NFC stop API is called, it continues to receive messages.
    * @param {object} req req includes id, CallBack, successMessage
    * @returns {object} object includes id, status, errorCode, successMessage
    * @example
    * var jsonobj = {};
    *  jsonobj .id="RECMFC";
    *  jsonobj.callBack=this.NFCCallback;
    *  apz.ns.receiveNFC(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   receiveNFC : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * Stop NFC plugin enables the developer to do the following
    * Stops the NFC instance.
    * Once NFC is stopped, it will no longer send or receive messages until those APIs are called again
    * @param {object} req req includes id, callBack, successMessage
    * @returns {object} object includes id, status, errorCode
    * @example
    * var jsonobj = {};
    *  jsonobj.id = "StopNFC";
    *  jsonobj.callBack = stopNFCCallbak
    *  apz.ns.stopNFC (jsonobj);
    * <b>Note:</b> The ID for apz.ns.startKeyboardListener and apz.ns.stopKeyboardListener should be same.
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   stopNFC : function(req) {
      this.callNative(req);
   }, 
   /**
    * This API helps to open file within the app.
    * @param {object} req req includes id, callback, filepath.
    * @returns {object} object includes id, status, errorcode.
    * @example
    * var json = {};
    * json.id = "READFILE";
    * json.callBack = readFileCallBack;
    * json.filePath = "file_path";
    * apz.ns.readFile(json);
    * @description <b>OS Specific Limitation</b>
    * <br> Android supports pdf, txt, xml.
    * <br> iOS supports pdf, txt, xlsx, docx, jpg.
    * <br> Windows 10 supports pdf.
    */
   readFile : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API prints the file passed in the filePath.
    * @param {object} req req includes id, callBack, filePath
    * @returns {object} object includes id, status, errorcode
    * @example
    * var json = {
	 * "filePath" : "docs/SampleFile.txt"
    * }   
    *  json.id = "";
    *  apz.ns.printFile(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   printFile : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * This API prints the current screen of the application.
    * @param {object} req req includes id, callback
    * @returns {object} object includes id, status, errorcode
    * @example
    * var json = {}   
    *  json.id = "";
    *  json.callBack = printCallback;
    *  apz.ns.printScreen(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   printScreen : function(req) {
      this.callNative(req);
   }, 
   
   /**
    * The API enables to make video call ,audio call and chatting throug skype.
    * @param {object} req req includes id, callback, type, userId
    * @returns {object} object includes id, status, errorCode
    * @example
    * var params = {};
	 * params.type = "call";//chat/call/video,
	 * params.userId = ["skypeId1","skypeId2","skypeId3"];
	 * params.callBack = skpeCallback;
	 * params.id = "SKYPECALLBACK"
	 * apz.ns.makeSkypeCall(params);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   makeSkypeCall : function(req) {
      this.callNative(req);
   },

   /**
    * This API is used for deep linking third party applications.
    * @param {object} req req includes id, callback, packageName.
    * @returns {object} object includes id, status, errorcode.
    * @example
    * var json ={ };
    * json.id = "DEEPLINK_ID";
    * json. packageName = "com.iexceed.testapp";
    * json.callBack = deepLinkinCallback;
    * apz.ns.deepLinking(json);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   deepLinking : function(req) {
      this.callNative(req);
   },
   
   /**
	* This plugin helps in auto capturing documents and performs OCR for the same.The camera UI is configurable for various documents and applications.Both auto capture and manual capture are available.
    * @param {object} req req includes id, callback, source, type,fileName,encodingType,quality,documentWHRatio,holdTimeForCapture,defaultCaptureMode,timeOutForCapture,nativePreviewScreen,typeOfDetection,documentType,textToDetect(object),UIParams(objcet).
	* <br><b>source</b> "camera/photo" -opens camera or photo gallery(only for iOS)
	* <br><b>type</b> can be file or base64, default is file.
	* <br><b>fileName</b> should be provided if 'type' is 'file'.
	* <br><b>encodingType</b> "JPEG/PNG/JPG", default is JPEG encoding.
	* <br><b>documentWHRatio</b> "width:height" ratio of the document to be captured.
	* <br><b>holdTimeForCapture</b> "in seconds",Time duration to hold the camera before capture.Used for devices which requires more time to focus. 
	* <br><b>defaultCaptureMode</b> "auto/manual", default mode to be launched by the camera.Default is auto.
	* <br><b>typeOfDetection</b> "new/AppleVision", "Applevision" scans document with edge detection (suggested for td1 and other documents)."new" scans document without  edge detection (suggested for td3 format documents) - only for iOS.
	* <br><b>documentType</b> "td1/td3", Default is empty.Td1,td3 scans and verifies the ISO code of TD1,TD3 format cards.Example -Passport(td3),Oman National ID Card(td1),Cambodian Natioanl ID card(td1).
	* <br><b>timeOutForCapture</b> "in seconds",  default is 0.Auto capture timeout in seconds.After timeout,callback errorCode -APZ-CNT-340 returned.
	* <br><b>nativePreviewScreen</b> "Y/N", To show or hide image preview screen from native after every capture.
	* <br><b>textToDetect</b> textToDetect object contains following.
	* <br><b>type</b> "all/any",'Any' auto captures if any of the given text(array) is present in the document and 'All' captures only if all the texts are present
	* <br><b>text</b> ["" ,""],array of static texts from the document.Detects OCR if passed.
	* <br><b>UIParams</b> UIParams object contains following.
	* <br><b>toggleButton</b> "Y/N", auto/manual toggle button show(Y) or hide(N) at the top right of the camera screen.
	* <br><b>portraitMarginPercent</b> 0-50 ,Margin percent of the frame from left/right in portrait screen.Default is 10.
	* <br><b>landscapeMarginPercent</b> 0-50 ,Margin percent of the frame from left/right in landscape screen.Default is 30.
	* <br><b>topMarginPercent</b> 0-100 ,Margin percent of the frame from top in camera screen.Default is 20.
	* <br><b>pageTitle</b>"any text", Title for the camera screen,displayed above the frame.Default is “” displays no title.
	* <br><b>messageTitle</b>"any text", Message title for the camera screen,displayed below the frame.Default is “” displays no title. 
	* <br><b>message</b>"", Message for the camera screen,displayed below the message title.Default is “” displays no title.
	* <br><b>scanStatus1</b>"any text", Status message at the bottom for capturing.Default is "Scanning.."
	* <br><b>scanStatus2</b>"any text", Status message at the bottom for verifying the image.Default is "Verifying.."
	* <br><b>scanStatus3</b>"any text", Status message at the bottom for holding the camera.Default is "Hold steady.."
	* <br><b>fontColor</b>"hex value six digit", font color of the messages in camera screen.Default is black.
	* <br><b>overlayColor</b>"hex value six digit", font color of the messages in camera screen.Default is grey.
    * @example
    * var jsonobj = {}; 
     *jsonobj.source ="camera";           //-option only for iOS 
     *jsonobj.type="base64" ;   
     *jsonobj.fileName ="filename";  
     *jsonobj.encodingType ="PNG";   
     *jsonobj.quality="100" ;   
     *jsonobj.documentWHRatio ="3:2";       //"width:height" ratio 
     *jsonobj.holdTimeForCapture ="3";      // in seconds
     *jsonobj.defaultCaptureMode="auto" ;  //auto/manual mode 
     *jsonobj.nativePreviewScreen ="Y";  
     *jsonobj.timeOutForCapture ="20" ;      // in seconds
     *jsonobj.typeOfDetection = "new";       //new or AppleVision
     *jsonobj.documentType = "td3";          // td3 or td1
     *jsonobj.textToDetect ={};       //object with keys "type" and "text"
     *jsonobj.textToDetect.type="all" ;   // -all/any   
     *jsonobj.textToDetect.text= ["statictext1","statictext2"] ; // array of static texts from the document 
     *jsonobj.UIParams={} ;       // object with keys mentioned below
     *jsonobj.UIParams.toggleButton ="Y";  // -Y/N
     *jsonobj.UIParams.portraitMarginPercent ="20";   // 0-50
     *jsonobj.UIParams.landscapeMarginPercent ="20";   // 0-50
     *jsonobj.UIParams.topMarginPercent ="20";   // 0-100 
     *jsonobj.UIParams.pageTitle=" Identity Card" ;   
     *jsonobj.UIParams.messageTitle="Front Side" ;   
     *jsonobj.UIParams.message=" Position your card inside the frame" ;   
     *jsonobj.UIParams.fontColor="#FFFFFF" ;   
     *jsonobj.UIParams.overlayColor="#000000" ;   
     *jsonobj.UIParams.scanStatus1="Looking for card.." ;   
     *jsonobj.UIParams.scanStatus2="Ready to capture.." ;   
     *jsonobj.UIParams.scanStatus3="Hold steady..."" ; 
     *jsonobj.id = "AUTO_CAPTURE"; 
     *jsonobj.callBack = autoCaptureCallback; 
     *apz.ns.detectDocument(jsonobj);
     * @returns {object} object includes id, errorcode, status, outputFile.
     * <br><b>outputFile</b> outputFile contains following.
	 * <br><b>type</b> "base64/file".
	 * <br><b>data</b> "base64String/filePath".
	 * <br><b>ocrText</b> [].  //array with blocks of texts and  position points.Returned when input "textToDetect" obj passed
	 * <br><b>ocrWholeText</b> "whole ocr text".   //Returned when input "textToDetect" obj passed
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   detectDocument : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API helps to live capture face using  face and  blink detection.
    * @param {object} req req includes callBack, type,fileName, encodingType ,quality ,blinkEyeDetection ,blinkInstruction,pageTitle,faceInstruction1,faceInstruction2,faceInstruction3,scanStatus,fontColor,overlayColor,holdTimeInstruction,instructionPosition,holdTimeForCapture,nativePreviewScreen,targetWidth,targetHeight.
    * <br><b>type</b> values can be base64 or file. default is file.
	* <br><b>fileName</b> mandatory if type is "file".
	* <br><b>encodingType</b> can be JPEG or PNG, default is JPEG encoding.
	* <br><b>quality</b> ranges from 0 to 100, default is 100.
	* <br><b>blinkEyeDetection</b> Y if blink eye detection required, -default is N.
	* <br><b>blinkInstruction</b> blink instruction (to be shown when blink detection is enabled)-default  is "". 
	* <br><b>pageTitle</b> title of the camera screen  -default  is "" 
	* <br><b>faceInstruction1</b> face capturing  instruction 1 (Shown in all generic cases) -default  is "".
	* <br><b>faceInstruction2</b> face capturing  instruction  2(to be shown when face is too far) -default  is "". 
	* <br><b>faceInstruction3</b> face capturing  instruction 2 (to be shown when face is too close) -default  is "".
	* <br><b>scanStatus</b> scan status after -default  is "" .
	* <br><b>fontColor</b> color hex value of the texts (titles and instructions) -default is black(#000000).
	* <br><b>overlayColor</b> color hex value of the overlay outside the oval frame -default is grey (#FFFFFF).
	* <br><b>holdTimeInstruction</b> hold time instruction (to be shown for capturing a steady image)-default  is "".
	* <br><b>instructionPosition</b> instruction occuring position- "1"(at the top below title) ,"2" (at the middle inside the oval frame)),"3" (at the bottom-default)).
	* <br><b>holdTimeForCapture</b> value in seconds -holding time before capture (default is 2 seconds).
	* <br><b>nativePreviewScreen</b> display captured image preview screen from native Y/N    -default N .
	* <br><b>targetWidth</b> Expected width of the result image   -default "".
	* <br><b>targetHeight</b> Expected height of the result image  -default "" .
    * @example
    * var jsonobj = {};
    * jsonobj.id = "CALLLOGS";
	* jsonobj.type = "base64";
	* jsonobj.fileName = "FILE_PATH"; //mandatory if type is "file"
	* jsonobj.encodingType = "PNG"; // default is JPEG encoding
	* jsonobj.quality = 100; //default is 100
	* jsonobj.blinkEyeDetection = "Y";
	* jsonobj.blinkInstruction = "Blink your eyes slowly.";
	* jsonobj.pageTitle = "Capture Face";
	* jsonobj.faceInstruction1 = "Position face inside the oval frame.";
	* jsonobj.faceInstruction2 = "Face is too far.Move closer.";
	* jsonobj.faceInstruction3 = "Face is too close.Move far.";
	* jsonobj.scanStatus = "Capturing.....";
	* jsonobj.fontColor = "#000000";
	* jsonobj.overlayColor = "#FFFFFF";
	* jsonobj.holdTimeInstruction = "Hold steady";
	* jsonobj.instructionPosition = "1";
	* jsonobj.holdTimeForCapture = "4";
	* jsonobj.nativePreviewScreen = "Y";
        * jsonobj.targetWidth = "", //width of the result image.
        * jsonobj.targetHeight = "", //height of the result image. 
    * jsonobj.callBack = getCallLogsCallback;
    * apz.ns.selfieCapture(jsonobj);
    * @returns {object} object includes - Status, id, ErrorCode, outputFile.
	* <br><b>outputFile</b> outputFile contains following.
	* <br><b>type</b> "base64/file".
	* <br><b>data</b> "baseString/filePath".
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   selfieCapture : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API helps to process the image or modify its properties.
    * @param {object} req req includes callBack, id, inputFile, outputFile, imageAction.
	* <br><b>imageAction</b> can be BW or grayscale, default is grayscale.
    * <br><b>inputFile</b> inputFile contains type and data values,
	* <br><b>type</b> can be file or base64,
	* <br><b>data</b> can be filePath or base64string correspondingly.
	* <br><b>outputFile</b> outputFile contains type, fileName, threshold, encodingType and quality values,
	* <br><b>type</b> can be file or base64, default is file.
	* <br><b>fileName</b> should be provided if 'type' is 'file'.
	* <br><b>threshold</b> varied between 0 to 255.
	* <br><b>encodingType</b> "JPEG/PNG/JPG", default is JPEG encoding.
	* <br><b>quality</b> ranges from 0 to 100, default is 100.
    * @example
    *     var json = {
    *    "inputFile": {
    *    "type": "file/base64",
	*	 "data": "filePath/base64string"
    * 	};
	*   "outputFile": {
    *   "type": "file/base64", //default file
		"fileName": "filename", //mandatory if type is "file"
        "threshold": "thresholdvalue"  //this varies between 0 to 255 
		"encodingType": "JPEG/PNG/JPG", // default is JPEG encoding
		"quality": "0--100" //default is 100
    * }};
    * jsonobj.imageAction = "BW/grayscale", //default is grayscale.
    * jsonobj.id = "PROCESSIMAGE";
    * jsonobj.callBack = processImageCallback;
    * apz.ns.processImage(jsonobj);
    * @returns {object} object includes - Status, id, ErrorCode, outputFile, imageAction.
	* <br><b>outputFile</b> outputFile contains following.
	* <br><b>type</b> "base64/file".
	* <br><b>data</b> "base64String/filePath".
	* <br><b>imageAction</b> "BW/grayscale".
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/

   
   processImage : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API helps to get the list of missed/incoming/outgoing calls in the callback JSON parameter. This plugin returns the array of call list from the call Logs.
    * @param {object} req req includes callBack, id, callType.
    * <br><b>callType</b> It is type of for which we want to get Logs, the values can be either of MISSED, INCOMING, OUTGOING or ALL, if no parameter is passed then it will consider it as ALL and all logs will be returned.
    * @example
    * var jsonobj = {};
	* jsonobj.callType = "MISSED";// OUTGOING, INCOMING, ALL
    * jsonobj.id = "CALLLOGS";
    * jsonobj.callBack = getCallLogsCallback;
    * apz.ns.getCallLogs(jsonobj);
    * @returns {object} object includes - Status, id, ErrorCode.
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png)
	*/
   
   getCallLogs : function(req) {
      this.callNative(req);
   },
   
   /**
    * This plugin scans barcode or QR code using the camera and returns the decoded result.
    * @param {object} req req includes id, callback.
    * @returns {object} object includes id, status, errorcode, text.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "BARCODE_ID";
    * jsonobj.callBack = scanBarcodeCallback;
    * apz.ns.startBarcodeScan(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   
   startBarcodeScan : function(req) {
      this.callNative(req);
   },
   
   /**
    * This plugin stops barcode or QR code scanning.
    * @param {object} req req includes id, callback.
    * @returns {object} object includes id, status, errorcode.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "BARCODE_ID";
    * jsonobj.callBack = scanBarcodeCallback;
    * apz.ns.stopBarcodeScan(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
	
   stopBarcodeScan : function(req) {
      this.callNative(req);
   },
   
   /**
    * This plugin scans barcode or QR code of the image from gallery and returns the decoded result.
    * @param {object} req req includes id, callback ,filePath.
    * @returns {object} object includes id, status, errorcode, text.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "BARCODE_ID";
    * jsonobj.callBack = scanBarcodeCallback;
    * apz.ns.barcodeFromGallery(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png)
	*/
   
   barcodeFromGallery : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API resizes the view which was previously opened using multiviewOpen plugin.
    * @param {object} req req includes id, callback ,percentage.
    * @returns {object} object includes id, status, errorcode.
    * @example
    * var jsonobj = {};
	* jsonobj.percentage = 80;
    * jsonobj.id = "RESIZE_MV";
    * jsonobj.callBack = resizeMulviewCallback;
    * apz.ns.multiviewResize(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp;
	*/
   
   multiviewResize : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API tracks the real time location of the device, if the app is running in foreground or background.Based on the distance or time interval location will be sent to Server.
    * @param {object} req req includes callBack, id, isIntervalBased , interval ,displacement.
    * <br><b>isIntervalBased</b> It should contain ’Y’ if the location needs to be tracked based on the time interval else if the location needs to be tracked based on displacement it will contain ‘N’. 
    * <br><b>interval</b> Time interval in seconds, incase ‘isIntervalBased’ is ‘Y’, after which location needs to be tracked.
    * <br><b>displacement</b> The distance after which the location needs to tracked.
    * @example
    * var jsonobj = {};
    * jsonobj.isIntervalBased  = "Y";
    * jsonobj.interval = 30 * 1000; // 30 seconds;
	* jsonobj.displacement = 1 ;// 1 meter
    * jsonobj.id = "REAL_TIME_TRACK_LOCATION";
    * jsonobj.callBack = trackLocationCallback;
    * apz.ns.startRealTimeTrackLocation(jsonobj);
    * @returns {object} object includes - Status, id, ErrorCode.
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png)
	*/
   
   startRealTimeTrackLocation : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API stops tracking real time location if started by plugin startRealTimeTrackLocation.
    * @param {object} req req includes id, callback.
    * @returns {object} object includes id, status, errorcode.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "REAL_TIME_TRACK_LOCATION";
    * jsonobj.callBack = trackLocationCallback;
    * apz.ns.stopRealTimeTrackLocation(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png)
	*/
   
   stopRealTimeTrackLocation : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API converts text to speech.
	* This feature uses Google Text-to-speech engine and supports English and Hindi. User has to install “Google TTS Voice data” for English and Hindi languages in their Android devices.
    * @param {object} req req includes id, callback , language, actualText.
	* <br><b>language</b> The language in which the text needs to be read. Currently it supports English and Hindi.
    * <br><b>actualText</b> The text that is supposed to be read.The text should be in the required language script.
    * @returns {object} object includes id, status, errorcode.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "TEXT_TO_SPEECH";
	* jsonobj.language = "English";
	* jsonobj.actualText = "Hello How are you ?";
    * jsonobj.callBack = ttsCallback;
    * apz.ns.textToSpeech(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png)
	*/
   
   textToSpeech : function(req) {
      this.callNative(req);
   },
   
    /**
    * This API generates PDF file of the above content and store in the App Sandbox or the base64 of the PDF.	
    * @param {object} req req includes id, callback , filePath, base64.
	* <br><b>filePath</b> The file path where the file needs to be stored and this should contain the file name along with extension “.pdf”.If no value is given then the file would be stored in the root folder with timpastamp as the name of the file.
    * <br><b>base64</b> This contains “Y” if only the base 64 of the generated file is required otherwise “N” if file needs to be generated and stored in the sandbox.
    * @returns {object} object includes id, status, errorcode, text.
	* <br><b>text</b> If base64 in the input is “Y” then this contains the base64 of the PDF generated otherwise, this contains the file path of the file.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "PDFGENERATOR";
	* jsonobj.filePath="storage/docs/PDFFile.pdf";
	* jsonobj.base64="Y";//or N
    * jsonobj.callBack = GeneratePDFFileCB;
    * apz.ns.generatePDF(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   generatePDF : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API initializes the PDF generator preparing it to accept the contents.	
    * @param {object} req req includes id, callback.
    * @returns {object} object includes id, status, errorcode.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "INITPDFGEN";
    * jsonobj.callBack = IntializePDFGeneratorCB;
    * apz.ns.initializePDF(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   initializePDF : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API appends the content together to write in the PDF.
    * @param {object} req req includes id,callBack,contentType,imagePath,imageWidth,imageHeight,text,fontType,fontSize,fontColor.
	* <br><b>contentType</b> This should contain “Text” incase the PDF needs to contain Text and “Image” if the PDF needs to contain Images.
    * <br><b>imagePath</b> The path of the image to be painted in the PDF.
	* <br><b>imageWith</b> The width of the image.
	* <br><b>imageHeight</b> The height image.
	* <br><b>Text</b> The text to be written in the PDF.
	* <br><b>fontType</b> The font Type of the text like Ariel.
	* <br><b>fontSize</b> The size of the text.
	* <br><b>fontColor</b> The color of the text, must be hexadecimal value.
    * @returns {object} object includes id, status, errorcode.
    * @example
    * var jsonobj = {};
	* jsonobj.contentType= "Text "//or Image;
    * jsonobj.imagePath="docs/images/temp.jpeg";
    * jsonobj.imageHeight="100";
    * jsonobj.imageWidth="100";
    * jsonobj.text="Paste your content here";
    * jsonobj.fontType="Calibri";
    * jsonobj.fontSize="12";
    * jsonobj.fontColor="#000000";
    * jsonobj.callBack=AppendContentToPDFGeneratorCB;
    * apz.ns.addPDFContent(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   addPDFContent : function(req) {
      this.callNative(req);
   },
   
   /**
    * The Barcode generator is used to generate QRCode jpeg file or base 64 of QR code.	
    * @param {object} req req includes id, callback , inputString, base64, destinationPath, fileName.
	* <br><b>inputString</b> The String whose QR code needs to be obtained.
    * <br><b>base64</b> This should contain the value ”Y” in case base 64 string is needed otherwise “N” if jpeg is required.
    * <br><b>destinationPath</b> This should contain the file path of the folder where the jpeg file needs to be stored.If no value is passed then it will be stored in a folder named “TempFolderQRCode” in the sandbox.
	* <br><b>fileName</b> The name with which the file needs to be stored.
	* @returns {object} object includes id, status, errorcode, text.
	* <br><b>text</b> In case the input has base64 as “Y” then this will contain the base64 string of the QRcode generated and if the base64 is “N” then this will contain the absolute file path of the jpeg file stored in the sandbox.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "BARCODE_GEN";
	* jsonobj.inputString="Sample";
	* jsonobj.base64="Y";//or N
	* jsonobj.destinationPath="docs";
	* jsonobj.fileName = "QRCode";
    * jsonobj.callBack = GeneratePDFFileCB;
    * apz.ns.genBarcode(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   genBarcode : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API is used to store data securely. It uses key chain in iOS and in Android it uses keystore.
    * @param {object} req req includes id, callback , key, value, promptBiometric.
	* <br><b>key</b> The key whose values need to be stored.
    * <br><b>value</b> The value to be stored Securely.
    * <br><b>promptBiometric</b> This contains “Y” if the data should be stored with biometric authentication otherwise contains “N”. Data stored with biometric will  be retrieved with biometric only.
	* @returns {object} object includes id, status, errorcode.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "BIOMETRIC_STORE_ID";
	* jsonobj.key = "KEY";
	* jsonobj.value = "VALUE";
	* jsonobj.promptBiometric = "Y";//N
    * jsonobj.callBack = biometricEncryptCallback;
    * apz.ns.storeSecurely(jsonobj);
    */
   
   storeSecurely : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API is used to retrieve data securely.
    * @param {object} req req includes id, callback , key.
	* <br><b>key</b> The key whose value need to be retrieved.
	* @returns {object} object includes id, status, errorcode , text.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "BIOMETRIC_STORE_ID";
	* jsonobj.key = "KEY";
    * jsonobj.callBack = biometricEncryptCallback;
    * apz.ns.retrieveSecurely(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   
   retrieveSecurely : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API is used to store the user credentials securely in the device so that biometric login can be used.
    * @param {object} req req includes id, callback , userId, password.
	* <br><b>userId</b> The user id to be stored in the device.
    * <br><b>password</b> The password to be stored in the device.
	* @returns {object} object includes id, status, errorcode.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "BIOMETRIC_STORE_ID";
	* jsonobj.userId = "admin";
	* jsonobj.password = "admin";
    * jsonobj.callBack = biometricEncryptCallback;
    * apz.ns.storeCredentialSecurely(jsonobj);
    */
   
   storeCredentialSecurely : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API returns the biometric hardware/biometric configuration state of the device.
    * @param {object} req req includes id, callback.
	* @returns {object} object includes id, status, biometricStatus.
	* <br><b>biometricStatus</b> This parameter contains the following:
    * <br><b>NOTSUPPORTED</b> If the device does not support biometric authentication or biometric hardware is not availabale then the plugin returns then the plugin returns “NOTSUPPORTED”.
	* <br><b>NOTCONFIGURED</b> If the fingerprint is not configured then the plugin returns “NOTCONFIGURED”.
	* <br><b>TOUCHID</b> If the biometric is properly configured in the device then the plugin returns “TOUCHID”.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "BIOMETRIC_ID";
    * jsonobj.callBack = biometricAvailabilityCB;
    * apz.ns.biometricAvailability(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png) &nbsp;&nbsp;&nbsp; ![windows](../windows.png) &nbsp;&nbsp;&nbsp; ![web](../web.png)
	*/
   
   biometricAvailability : function(req) {
      this.callNative(req);
   },
   
   /**
    * This API helps to share the Text and files using OS native share option.
    * @param {object} req req includes id, callback , action, textToShare, filePath.
	* <br><b>action</b> action can be text or file.
	* <br><b>textToShare</b> when action is text, this will contain text to be shared.
	* <br><b>filePath</b> when action is file, this would container filePath of the file to be shared.
	* @returns {object} object includes id, status.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "SHARE";
	* jsonobj.textToShare = "TEXT_TO_BE_SHARED"; // or jsonobj.filePath = "FILE_TO_BE_SHARED";
    * jsonobj.callBack = nativeShareCallback;
    * apz.ns.nativeShare(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   nativeShare : function(req) {
      this.callNative(req);
   },
   
   /**
    * Network monitoring plugin keeps track of network status and sends back the same in response.
    * @param {object} req req includes id, callback.
	* @returns {object} object includes id, status, event.
	* <br><b>event</b> this parameter in output will consist,
	* <br><b>started</b> – if plugin was successfully invoked.
	* <br><b>on</b> if device is connected to network
	* <br><b>off</b> if device is not connected to network.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "NW_LISTENER";
    * jsonobj.callBack = nwListenerCallback;
    * apz.ns.startNetworkListener(jsonobj);
    */
   
   startNetworkListener : function(req) {
      this.callNative(req);
   },
   
   /**
    * Stops tracking the network status.
    * @param {object} req req includes id, callback.
	* @returns {object} object includes id, status, event.
	* <br><b>event</b> this parameter in output will retrun “stopped”  on successful stoppage of plugin.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "NW_LISTENER";
    * jsonobj.callBack = nwListenerCallback;
    * apz.ns.stopNetworkListener(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   stopNetworkListener : function(req) {
      this.callNative(req);
   },
   
   /**
    * This plugin is to show download notification in the system notification tray.
    * @param {object} req req includes id, callback, title, content.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "NTWK_ID";
	* jsonobj.title = "Download";
	* jsonobj.content = "Downloading in progress";
    * jsonobj.callBack = downloadNotifierCB;
    * apz.ns.startDownloadNotifier(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   startDownloadNotifier : function(req) {
      this.callNative(req);
   },
   
   /**
    * This plugin is to close download notification in the system notification tray.
    * @param {object} req req includes id, callback, title, content,filePath, mimeType.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "NTWK_ID";
	* jsonobj.title = "Downloaded";
	* jsonobj.content = "Downloading complete";
	* jsonobj.filePath = "/storage/emulated/0/Download/narcos.mp3";
	* jsonobj.mimeType = "audio/mpeg";
    * jsonobj.callBack = downloadNotifierCB;
    * apz.ns.stopDownloadNotifier(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   stopDownloadNotifier : function(req) {
      this.callNative(req);
   },
   
   /**
    * This plugin is to perform custom http requests other than Appzillon server.
    * @param {object} req req includes id, callback, isAppzillon, url,httpHeaders, request.
	* @returns {object} object includes id, status, httpCode, httpHeaders, response.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "customHTTPReq_ID";
	* jsonobj.isAppzillon = "Y";// or N
	* jsonobj.url = "http://echo.jsontest.com/title/ipsum/content/blah";
	* jsonobj.httpHeaders = "header_json";
	* jsonobj.request = "request_json";
    * jsonobj.callBack = customHTTPReqCB;
    * apz.ns.customHTTPReq(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   customHTTPReq : function(req) {
      this.callNative(req);
   },
   
   /**
    * This plugin is to send result or data to native app in case of appzillon lib.
    * @param {object} req req includes id, callback, data.
	* @returns {object} object includes id, status.
    * @example
    * var jsonobj = {};
    * jsonobj.id = "customHTTPReq_ID";
	* jsonobj.data = "data_json";
    * jsonobj.callBack = dataToMainAppCB;
    * apz.ns.sendDataToMainApp(jsonobj);
    * @description
	* <b>OS Support</b><br>
	* ![Android](../android.png) &nbsp;&nbsp;&nbsp; ![iOS](../iOS.png)
	*/
   
   sendDataToMainApp : function(req) {
      this.callNative(req);
   },
    
	
};
getMCABootstrapData = function(){
    var path = apz.getConfigPath() + "/mcaprocess/" + "process.json";
    var params = { "context" : {}};
	params.config={};
	params.config.rootContext="";
	var content = apz.getFile(path);
	if(content){
		params.context.processJson = JSON.parse(content);
	}
	return params;
}
window.onerror = function(msg, url, lineNo, columnNo, error){
   var string = msg.toLowerCase();
    var substring = "script error";
    if (string.indexOf(substring) > -1){
        console.log('Script Error Occured');
    } else {
        var message = [
            'Message: ' + msg,
            'URL: ' + url,
            'Line: ' + lineNo,
            'Column: ' + columnNo,
            'Error object: ' + JSON.stringify(error)
        ].join(' -- ');
        console.log(message);
    }
}
