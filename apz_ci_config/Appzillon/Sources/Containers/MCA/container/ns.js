Apz.Ns = function(apz) {
   ////Core Instance
   this.apz = apz;
   ////Initialize Paths for Web
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
   }, log : function(msg,type) {
      if ( typeof apzIde !== "undefined") {
         apzIde.log(msg);
      } else {
         console.log(msg);
      }
   }, getDeviceInfo : function(req) {
      
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
      res.deviceType = "MCA";
      res.deviceOs = "MCA";
      res.deviceId = "MCA";
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
   }, getUserPrefs : function(req) {
      ////Init
      this.apz.initNativeService(req);
      var res = {};
      res.id = req.id;
      res.userPrefs = null;
      ///Would have read it from projdef
      Apz.nativeServiceCB(JSON.stringify(res));
   }, getLocation : function(req) {
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
   }, encryptData : function(req) {
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
   }, decryptData : function(req) {
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
   }, hashPwd : function(req) {
      ////Init
      this.apz.initNativeService(req);
      var hash = req.pwd;
      try {
         hash = apzIde.hashPwd(req.userId, req.pwd, req.serverToken, req.deviceId, req.date);
      } catch (e) {
      }
      var res = {};
      res.id = req.id;
      res.status = true;
      res.text = hash;
      ////Call Back..
      Apz.nativeServiceCB(JSON.stringify(res));
   }, showSplash : function(req) {
       //this.callNative(req);  
   }, hideSplash : function(req) {
	   //Nothing To Do
   }, startOrientationListener: function(req) {      
      this.apz.initNativeService(req);         
   }, nativeServiceExt : function(req) {
      this.callNative(req);
   }, openCamera : function(req) {
      this.callNative(req);
   }, startBeacon : function(req) {
      this.callNative(req);
   }, stopBeacon : function(req) {
      this.callNative(req);
   }, callNumber : function(req) {
      this.callNative(req);
   }, base64ToFile : function(req) {
      this.callNative(req);
   }, scanFinger : function(req) {
      this.callNative(req);
   }, openUrl : function(req) {
      this.callNative(req);
   }, fileToBase64 : function(req) {
      this.callNative(req);
   }, getFileSize : function(req) {
      this.callNative(req);
   }, launchWebview : function(req) {
      this.callNative(req);
   }, closeWebview : function(req) {
      this.callNative(req);
   }, getInboxSMS : function(req) {
      this.callNative(req);
   }, setRingtone : function(req) {
      this.callNative(req);
   }, startCallListener : function(req) {
      this.callNative(req);
   }, getMissedCalls : function(req) {
      this.callNative(req);
   }, zip : function(req) {
      this.callNative(req);
   }, unzip :function(req) {
      this.callNative(req);
   }, startAccelerometer : function(req) {
      this.callNative(req);
   }, stopAccelerometer : function(req) {
      this.callNative(req);
   }, scanBarcode : function(req) {
      this.callNative(req);
   }, createCalendarEvent : function(req) {
      this.callNative(req);
   }, editCalendarEvent : function(req) {
      this.callNative(req);
   }, deleteCalendarEvent : function(req) {
      this.callNative(req);
   }, startCompass : function(req) {
      this.callNative(req);
   }, stopCompass : function(req) {
      this.callNative(req);
   }, executeSql : function(req) {
      this.callNative(req);
   }, deviceDetails :function(req) {
      this.callNative(req);
   }, startBatteryMonitor : function(req) {
      this.callNative(req);
   }, stopBatteryMonitor : function(req) {
      this.callNative(req);
   }, encryptFile : function(req) {
      this.callNative(req);
   }, decryptFile : function(req) {
      this.callNative(req);
   }, geofencing : function(req) {
      this.callNative(req);
   }, startLocationTracking : function(req) {
      this.callNative(req);
   }, stopLocationTracking : function(req) {
      this.callNative(req);
   }, startGesture : function(req) {
      this.callNative(req);
   }, stopGesture : function(req) {
      this.callNative(req);
   }, fileBrowser : function(req) {
      this.callNative(req);
   }, getFileContent : function(req) {
      this.callNative(req);
   }, createFile : function(req) {
      this.callNative(req);
   }, deleteFile : function(req) {
      this.callNative(req);
   }, openFile : function(req) {
      this.callNative(req);         
   }, sendMail : function(req) {
      this.callNative(req);
   }, addContact : function(req) {
      this.callNative(req);
   }, deleteContact : function(req) {
      this.callNative(req);
   }, searchContact : function(req) {
      this.callNative(req);
   }, editContact : function(req) {
      this.callNative(req);
   }, fetchContact : function(req) {
      this.callNative(req);
   }, drivingDirection : function(req) {
      this.callNative(req);
   }, loadMap : function(req) {
      this.callNative(req);
   }, locationSelector : function(req) {
      this.callNative(req);
   }, currentLocale : function(req) {
      this.callNative(req);
   }, saveReport : function(req) {
      this.callNative(req);
   }, sendReq : function(req) {
	   this.apz.initNativeService(req);
	   var params = req.params;
	   var reqStr = JSON.stringify(params.reqFull);
	   $.ajax({
           url : params.url, type : params.method, cache : false, data : reqStr, contentType : 'application/json', dataType : 'json', async : params.async, success : function(res) {
        	  params.status = true;
              params.resFull = res;
              Apz.nativeServiceCB(JSON.stringify(req));
           }, error : function() {
              params.status = false;
              Apz.nativeServiceCB(JSON.stringify(req));
           }
        });
   }, signaturePad : function(req) {
      this.callNative(req);
   }, facebookLogin : function(req) {
      this.callNative(req);
   }, googleLogin : function(req) {
      this.callNative(req);
   }, linkedinLogin : function(req) {
      this.callNative(req);
   }, twitterLogin : function(req) {
      this.callNative(req);
   }, youtube : function(req) {
      this.callNative(req);
   }, startIdleTimer : function(req) {
      this.callNative(req);
   }, lockRotation : function(req) {
      this.callNative(req);
   }, unlockRotation : function(req) {
      this.callNative(req);
   }, setOrientation : function(req) {
      this.callNative(req);
   }, vibrate : function(req) {
      this.callNative(req);
   }, voice : function(req) {
      this.callNative(req);
   }, detectEvents : function(req) {
      this.callNative(req);
   }, wipeOut : function(req) {
      this.callNative(req);
   }, getIP : function(req) {
      this.callNative(req);
   }, getAppVersion : function(req) {
      this.callNative(req);
   }, closeApplication : function(req) {
      this.callNative(req);
   }, multiviewOpen : function(req) {
      this.callNative(req);
   }, multiviewClose : function(req) {
      this.callNative(req);
   }, captureNotes : function(req) {
      this.callNative(req);
   }, enablePullDown : function(req) {
      this.callNative(req);
   }, disablePullDown : function(req) {
      this.callNative(req);
   }, hideRefresh : function(req) {
      this.callNative(req);
   }, startAugmentation : function(req) {
      this.callNative(req);
   }, reloadAugmentation : function(req) {
      this.callNative(req);
   }, videoRecording : function(req) {
      this.callNative(req);
   }, audio : function(req) {
      this.callNative(req);
   }, getNotification : function(req) {
      this.callNative(req);
   }, deleteNotification : function(req) {
      this.callNative(req);
   }, updateNotification : function(req) {
      this.callNative(req);
   }, launchApp : function(req) {
      this.callNative(req);
   }, subappDelete : function(req) {
      this.callNative(req);
   }, getInstructions : function(req) {
      this.callNative(req);
   }, upgradeRequired : function(req) {
      this.callNative(req);
   }, upgradeApp : function(req) {
      this.callNative(req);
   }, setUserPrefs : function(req) {
      this.callNative(req);      
   }, offlineData : function(req) {
      this.callNative(req);
   }, smsSend : function(req) {
      this.callNative(req);
   }, startSMSListener : function(req) {
      this.callNative(req);
   }, stopSMSListener : function(req) {
      this.callNative(req);
   }, store : function(req) {
      this.callNative(req);
   }, retrieve : function(req) {
      this.callNative(req);
   }, startKeyboardListener : function(req) {
      this.callNative(req);
   }, stopKeyboardListener : function(req) {
      this.callNative(req);
   }, startNotificationListener : function(req) {
      this.callNative(req);
   }, stopNotificationListener : function(req) {
      this.callNative(req);
   }, getPref : function(req) {
      this.callNative(req);
   }, setPref : function(req) {
      this.callNative(req);
   }, callNativeCBwithErrorCode : function(req) {
      this.callNative(req);
   }, biometricAuth : function(req) {
      this.callNative(req);
   }, whatsApp : function(req) {
      this.callNative(req);
   }, getSimInfo : function(req) {
      this.callNative(req);
   }, sendSmsBySID : function(req) {
      this.callNative(req);
   }, documentScanner : function(req) {
      this.callNative(req);
   }, uploadFile : function(req) {
      this.callNative(req);
   }, downloadFile : function(req) {
      this.callNative(req);
   }, sendNFC : function(req) {
      this.callNative(req);
   }, receiveNFC : function(req) {
      this.callNative(req);
   }, stopNFC : function(req) {
      this.callNative(req);
   }, readFile : function(req) {
      this.callNative(req);
   }, printFile : function(req) {
      this.callNative(req);
   }, printScreen : function(req) {
      this.callNative(req);
   }, makeSkypeCall : function(req) {
      this.callNative(req);
   }, deepLinking : function(req) {
      this.callNative(req);
   }
};
