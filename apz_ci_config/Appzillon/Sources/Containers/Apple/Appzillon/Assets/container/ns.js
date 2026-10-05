
Apz.Ns = function(apz) {
   this.apz = apz;
    this.cb_CbObjArr = {};
    this.mainAppParams = "";
};

///////////////////Prototype Definition///////////////////////
Apz.Ns.prototype = {
	regi_callNative : function(req){
		try{
            this.apz.initNativeService(req);
            var iframe = document.createElement("IFRAME");
            var source=encodeURIComponent(JSON.stringify(req));
            iframe.setAttribute("src", source);
            document.documentElement.appendChild(iframe);
            iframe.parentNode.removeChild(iframe);
            iframe = null;
		}
		catch(e){
			console.log(e);
		}
	},
   log : function(msg,type) {
      if ( typeof apzIde !== "undefined") {
         apzIde.log(msg);
      } else {
         console.log(msg);
      }
   }
    , startBarcodeScan : function(req) {
        req.command = "PLGN_SCN_BARCODE";
        this.regi_callNative(req);
    }, stopBarcodeScan : function(req) {
        req.actionType='stop';
        req.command = "PLGN_SCN_BARCODE";
        this.regi_callNative(req);
    }, voiceToText : function(req) {
        req.command = "PLGN_SPEECH_TO_TEXT";
        this.regi_callNative(req);
    }, barcodeFromGallery : function(req) {
        req.command = "PLGN_GALRY_SCN_BARCODE";
        this.regi_callNative(req);
    }, genBarcode : function(req) {
        req.command = "PLGN_GEN_BARCODE";
        this.regi_callNative(req);
    }, getSimDetails : function(req) {
        req.command = "PLGN_GET_SIM_DETAILS";
        this.regi_callNative(req);
    }, startBatteryMonitor : function(req) {
        req.actionType='start';
        req.command = "PLGN_BT_MTR_START";
        this.regi_callNative(req);
    }, stopBatteryMonitor : function(req) {
        req.actionType='stop';
        req.command = "PLGN_BT_MTR_STOP";
        this.regi_callNative(req);
    }, biometricAuth : function(req) {
        req.command = "PLGN_BIO_AUTH";
        this.regi_callNative(req);
    }, biometricAvailability : function(req) {
        req.command = "PLGN_BIOMET_AVAIL";
        this.regi_callNative(req);
    }, storeSecurely : function(req) {
        req.command = "PLGN_STR_SECURELY";
        this.regi_callNative(req);
    }, retrieveSecurely : function(req) {
        req.command = "PLGN_RTR_SECURELY";
        this.regi_callNative(req);
    }, storeCredentialSecurely : function(req) {
        req.command = "PLGN_STR_CRD_SECURELY";
        this.regi_callNative(req);
    }, login : function(req) {
        req.command = "PLGN_LOGIN";
        this.regi_callNative(req);
    },availableBiometric : function(req) {
        req.command = "PLGN_AVAIL_BIO";
        this.regi_callNative(req);
    },callNumber : function(req) {
        req.command = "PLGN_CALL_NMB";
        this.regi_callNative(req);
    }, startCallListener : function(req) {
       regi_callNativeCBwithErrorCode(req,"APZ-CNT-295",false);
    }, createCalendarEvent : function(req) {
        req.action="create";
        if(validate_calendar(req)){
        req.command = "PLGN_CRT_CAL_EVT";
        this.regi_callNative(req);
        }
    }, editCalendarEvent : function(req) {
        req.action="edit";
        if(validate_calendar(req)){
        req.command = "PLGN_EDT_CAL_EVT";
        this.regi_callNative(req);
        }
    },deleteCalendarEvent : function(req) {
        req.action="delete";
        if(validate_calendar(req)){
        req.command = "PLGN_DLT_CAL_EVT";
        this.regi_callNative(req);
         }
    }, openCamera : function(req) {
        if(validate_camera(req)){
        req.command = "PLGN_OPN_CAMERA";
        this.regi_callNative(req);
     }
    },processImage : function(req) {
        req.command = "PLGN_PROC_IMG";
        this.regi_callNative(req);
    },detectDocument : function(req) {
        req.command = "PLGN_DETECT_DOC";
        this.regi_callNative(req);
    },selfieCapture : function(req) {
        req.command = "PLGN_SLFI_CAP";
        this.regi_callNative(req);
    },addContact : function(req) {
        if(validate_contactAdd(req)){
        req.opnType = "add";
        req.command = "PLGN_ADD_CONT";
        this.regi_callNative(req);
        }
    }, deleteContact : function(req) {
        req.opnType = "delete";
        req.command = "PLGN_DEL_CONT";
        this.regi_callNative(req);
    }, editContact : function(req) {
        req.opnType = "edit";
        req.command = "PLGN_EDIT_CONT";
        this.regi_callNative(req);
    },searchContact : function(req) {
        req.opnType = "search";
        req.command = "PLGN_SRCH_CONT";
        this.regi_callNative(req);
    }, fetchContact : function(req) {
        req.opnType = "fetch";
        req.command = "PLGN_FETCH_CONT";
        this.regi_callNative(req);
    },fetchAllContacts : function(req) {
        req.opnType = "fetchAllContacts";
        req.command = "PLGN_FETCH_ALL_CONT";
        this.regi_callNative(req);
    }, decryptData : function(req) {
        req.command = "PLGN_DECRPT_DATA";
        this.regi_callNative(req);
    }, encryptData : function(req) {
        req.command = "PLGN_ENCRPT_DATA";
        this.regi_callNative(req);
    }, decryptFile : function(req) {
        if(validate_FileEncDesc(req)){
        req.command = "PLGN_DECRPT_FILE";
        this.regi_callNative(req);
        }
    }, encryptFile : function(req) {
        if(validate_FileEncDesc(req)){
        req.command = "PLGN_ENCRPT_FILE";
        this.regi_callNative(req);
        }
    }, deviceDetails : function(req) {
        req.command = "PLGN_DEV_DETAILS";
        this.regi_callNative(req);
    }, getDeviceInfo : function(req) {
        req.command = "PLGN_DEV_INFO";
        this.regi_callNative(req);
    }, uploadFile : function(req) {
        var screenId= req.screenId;
        var appzillonHeader;
        if(req.sessionReq=='Y'){
       // appzillonHeader=JSON.stringify(apz.server.getHeader('appzillonUploadFile',screenId,'uploadReqId',true));
       var params ={};
       params.ifaceName = 'appzillonUploadFile';
       params.scrName = screenId;
       params.async = true;
       params.id = 'uploadReqId';
       params.internal = true;
    appzillonHeader = apz.server.getHeader(params);


        }
        else{
        //appzillonHeader=JSON.stringify(apz.server.getHeader('appzillonUploadFileWS',screenId,'uploadReqId',true));
       var params ={};
       params.ifaceName = 'appzillonUploadFileWS';
       params.scrName = screenId;
       params.async = true;
      params.id = 'uploadReqId';
      params.internal = true;
    appzillonHeader = apz.server.getHeader(params);

        }
        req.appzillonHeader=appzillonHeader;
        if(this.apz.isNull(req.filePath)){
            req.fieldID = this.apz.getElmValue(req.fieldID);
        }else if(!this.apz.isNull(req.filePath)){
            req.fieldID=req.filePath;
        }else{
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-099",false);
        }
        req.command = "PLGN_UPLD_FILE";
        this.regi_callNative(req);
    }, downloadFile : function(req) {
        var screenId= req.screenId;
        var appzillonHeader;
        if(req.sessionReq=='Y'){
           // appzillonHeader=JSON.stringify(apz.server.getHeader('appzillonFilePushService',screenId,'downloadReqId',true));
       var params ={};
       params.ifaceName = 'appzillonFilePushService';
       params.scrName = screenId;
       params.async = true;
      params.id = 'downloadReqId';
      params.internal = true;
    appzillonHeader = apz.server.getHeader(params);
        }
        else{
          //  appzillonHeader=JSON.stringify(apz.server.getHeader('appzillonFilePushServiceWS',screenId,'downloadReqId',true));
      var params ={};
       params.ifaceName = 'appzillonFilePushServiceWS';
       params.scrName = screenId;
       params.async = true;
      params.id = 'downloadReqId';
      params.internal = true;
    appzillonHeader = apz.server.getHeader(params);
        }
        req.appzillonHeader=appzillonHeader;
        req.command = "PLGN_DWLD_FILE";
        this.regi_callNative(req);
    }, createFile : function(req) {
        req.opnType="create";
        req.command = "PLGN_CRT_FILE";
        this.regi_callNative(req);
    }, fileBrowser : function(req) {
//        if(validate_filebrowser(req)){
        req.command = "PLGN_BRWS_FILE";
        this.regi_callNative(req);
//        }
    }, getFileSize : function(req) {
        req.command = "PLGN_FILE_SIZE";
        this.regi_callNative(req);
    }, openFile : function(req) {
        req.command = "PLGN_OPN_FILE";
        this.regi_callNative(req);
    }, readFile : function(req) {
        req.command = "PLGN_OPN_FILE";
        this.regi_callNative(req);
    }, base64ToFile : function(req) {
        req.action='B64tofile';
        req.command = "PLGN_B64_TO_FILE";
        this.regi_callNative(req);
    }, fileToBase64 : function(req) {
        req.action='filetoB64';
        req.command = "PLGN_FILE_TO_B64";
        this.regi_callNative(req);
    }, startGesture : function(req) {
        req.command = "PLGN_GSTR_START";
        this.regi_callNative(req);
    }, stopGesture : function(req) {
        req.actionType='stop';
        req.command = "PLGN_GSTR_STOP";
        this.regi_callNative(req);
    }, checkNotificationStatus : function(req) {
        req.command = "PLGN_CHECK_NOTIFICATION_STATUS";
        this.regi_callNative(req);
    }, startLocationTracking : function(req) {
        if(validate_startUpdatingLocation(req)){
            if(apz.isNull(req.periodicity)){
                req.periodicity="none";
            }
        req.command = "PLGN_LOC_TRCK_START";
        this.regi_callNative(req);
        }
    }, stopLocationTracking : function(req) {
        req.command = "PLGN_LOC_TRCK_STOP";
        req.actionType='stop';
        this.regi_callNative(req);
    }, getLocation : function(req) {
        req.command = "PLGN_GET_LOCATION";
        this.regi_callNative(req);
    }, startIdleTimer : function(req) {
        req.singletap="Y";
        req.doubletap="Y";
        req.tripletap="Y";
        req.swipe="Y";
        req.longpress="Y";
        req.pinch="Y";
        req.command = "PLGN_IDLE_TMR_START";
        this.regi_callNative(req);
    }, currentLocale : function(req) {
        req.command = "PLGN_CRNT_LOCALE";
        this.regi_callNative(req);
    },
    sendMail : function(req) {
        if(validate_mail(req)){
        var internal = req.internal;
        if(internal.toUpperCase() == "Y")
         {
        var requestBody = {
         "appzillonMailRequest" : {
         "emailid" : req.recipientMailId,
         "subject" : req.subject,
         "CC" : req.ccIdList,
         "body" : req.body
          }
         };
         var interfaceId ="";
         if(req.interfaceID == undefined || req.interfaceID == ""){
         interfaceId = 'appzillonMailRequest';
         }
         else{
         interfaceId = req.interfaceID;
         }
             var params ={};
             params.ifaceName=interfaceId;
             params.internal=internal;
             params.buildReq="N";
             params.async=true;
             params.req=requestBody;
             params.callBack=req.callBack;
             this.apz.server.callServer(params);
         }else {
         req.opnType = "mail";
         req.command = "PLGN_SEND_MAIL";
         this.regi_callNative(req);
         }
        }
    }, loadMap : function(req) {
        if(validate_map(req)){
        req.command = "PLGN_LOAD_MAP";
        this.regi_callNative(req);
        }
    },audio : function(req) {
        if(validate_audio(req)){
        req.command = "PLGN_AUDIO";
        this.regi_callNative(req);
        }
    },deleteNotification : function(req) {
        if(validate_deleteNotification(req)){
        var ID = jsonObj.notificationID;
        var query = "DELETE FROM tb_notifications WHERE id = " + ID + ";";
        req.queryId = ID;
        req.databaseName = "APPSDB";
        req.executeQuery = query;
        req.command = "PLGN_DLT_NOTIF";
        this.regi_callNative(req);
        }
    },getNotification : function(req) {
        var query = "SELECT * FROM tb_notifications;";
        //        req.queryId =sqlId.toString();
        //        sqlId =sqlId + 1;
        req.queryId = req.notificationID;
        req.databaseName = "APPSDB";
        req.executeQuery = query;
        req.command = "PLGN_GET_NOTIF";
        this.regi_callNative(req);
    }, updateNotification : function(req) {
        if(validate_updateNotification(req)){
        var ID = jsonObj.notificationID;
        var readFlag = jsonObj.readFlag;
        readFlag = "'"+readFlag+"'";
        var query = "UPDATE tb_notifications SET readFlag = "+ readFlag +" WHERE id = " + ID + ";";
        req.queryId = ID;
        req.databaseName = "APPSDB";
        req.executeQuery = query;
        req.command = "PLGN_UPDT_NOTIF";
        this.regi_callNative(req);
        }
    }, startOrientationListener : function(req) {
        req.command = "PLGN_ORTN_START";
        this.regi_callNative(req);
    }, stopOrientationListener : function(req) {
        req.command = "PLGN_ORTN_STOP";
        this.regi_callNative(req);
    }, setOrientation : function(req) {
        req.command = "PLGN_SET_ORTN";
        this.regi_callNative(req);
    }, upgradeRequired : function(req) {
        req.command = "PLGN_UPGRD_REQ";
        this.regi_callNative(req);
    }, getUpdateAction : function(req) {
        req.command = "PLGN_GET_UPDATE_ACT";
        this.regi_callNative(req);
    }, upgradeApp : function(req) {
        req.command = "PLGN_UPGRD_APP";
        this.regi_callNative(req);
    },subappDelete : function(req) {
        if(validate_subappDelete(req)){
        req.command = "PLGN_SUBAPP_DEL";
        this.regi_callNative(req);
        }
    },
    hashPwd : function(req) {
        req.command = "PLGN_HASH_PWD";
        this.regi_callNative(req);
    },changePassword : function(req) {
        req.command = "PLGN_CHNG_PWD";
        this.regi_callNative(req);
    },lockRotation : function(req) {
        req.command = "PLGN_LCK_ROTN";
        this.regi_callNative(req);
    },unlockRotation : function(req) {
        req.command = "PLGN_UNLCK_ROTN";
        this.regi_callNative(req);
    },signaturePad : function(req) {
        req.command = "PLGN_SIGN_PAD";
        this.regi_callNative(req);
    },smsSend : function(req) {
        req.command = "PLGN_SMS_SEND";
        this.regi_callNative(req);
    },startSMSListener : function(req) {
      regi_callNativeCBwithErrorCode(req,"APZ-CNT-297",false);
    },stopSMSListener : function(req) {
      regi_callNativeCBwithErrorCode(req,"APZ-CNT-297",false);
    },smsReceive : function(req) {
      regi_callNativeCBwithErrorCode(req,"APZ-CNT-297",false);
    },getUserPrefs : function(req) {
        req.command = "PLGN_GET_USER_PREF";
        this.regi_callNative(req);
    },setUserPrefs : function(req) {
        req.command = "PLGN_SET_USER_PREF";
        this.regi_callNative(req);
    },getPref : function(req) {
        req.command = "PLGN_GET_PREF";
        this.regi_callNative(req);
    },setPref : function(req) {
        req.command = "PLGN_SET_PREF";
        this.regi_callNative(req);
    },
    executeSql : function(req) {
        if(validate_executeSql(req)){
        req.command = "PLGN_EXE_SQL";
        this.regi_callNative(req);
        }
    },wipeOut : function(req) {
        req.command = "PLGN_WIPEOUT";
        localStorage.clear();
        this.regi_callNative(req);
    },openUrl : function(req) {
        req.command = "PLGN_OPEN_URL";
        this.regi_callNative(req);
    },nativeServiceExt : function(req) {
        req.command = "PLGN_NTV_EXT";
        this.regi_callNative(req);
    },nativeShare : function(req) {
        req.command = "PLGN_NATVE_SHARE";
        this.regi_callNative(req);
    },showSplash : function(req) {
        req.command = "PLGN_SPLASH_SHOW";
        this.regi_callNative(req);
    }, hideSplash : function(req) {
        req.command = "PLGN_SPLASH_HIDE";
        this.regi_callNative(req);
    }, getIP : function(req) {
        req.command = "PLGN_GET_IP";
        this.regi_callNative(req);
    }, getAppVersion : function(req) {
        if(validate_getAppVersion(req)){
        req.command = "PLGN_APP_VERSION";
        this.regi_callNative(req);
        }
    }, hideRefresh : function(req) {
        req.refreshStatus = "HideRefresh";
        req.command = "PLGN_DIS_PLDN";
        this.regi_callNative(req);
    }, closeApplication : function(req) {
       // regi_callNativeCBwithErrorCode(req,"APZ-CNT-299",false);
       req.command = "PLGN_CLOSE_APP";
        this.regi_callNative(req);
    }, setRingtone : function(req) {
       regi_callNativeCBwithErrorCode(req,"APZ-CNT-300",false);
    },launchWebview : function(req) {
        req.command = "PLGN_LNCH_WEBVW";
        this.regi_callNative(req);
    }, closeWebview : function(req) {
        req.actionType='stop';
        req.command = "PLGN_CLS_WEBVW";
        this.regi_callNative(req);
    }, updateWhiteList : function(req) {
        //       
        //        req.command = "lockRotation";
        //        this.regi_callNative(req);
        //to be decieded what to do?
    }, updateWhiteListLocalStore : function(req) {
        //       
        //        req.command = "lockRotation";
        //        this.regi_callNative(req);
        //to be decieded what to do?
    }, voice : function(req) {
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-138",false);
    }, detectEvents : function(req) {
        req.command = "PLGN_DET_EVE";
        this.regi_callNative(req);
    }, getFileContent : function(req) {
         req.opnType = "read";
        req.command = "PLGN_FILE_CONTENT";
        this.regi_callNative(req);
    }, deleteFile : function(req) {
         req.opnType = "delete";
        req.command = "PLGN_DEL_FILE";
        this.regi_callNative(req);
    }, getInstructions : function(req) {
        if(validate_getInstructions(req)){
        req.command = "PLGN_GET_INST";
        this.regi_callNative(req);
        }
    }, sendReq : function(req) {
        req.command = "PLGN_N_SRVRCALL";
        this.regi_callNative(req);
    }, refreshServerNonce : function(req) {
        req.command = "PLGN_REFSVRNONCE";
        this.regi_callNative(req);
    },
    startKeyboardListener : function(req) {
        req.command = "PLGN_KEYBD_LISTR_STRT";
        this.regi_callNative(req);
    },
    stopKeyboardListener : function(req) {
        req.command = "PLGN_KEYBD_LISTR_STOP";
        this.regi_callNative(req);
    },
    updateWhitelist : function(req) {
        req.queryId="updateWhitelist";
        req.databaseName="APPSDB";
        req.updateWhitelist="Y";
        req.command = "PLGN_UPDT_WLIST";
        this.regi_callNative(req);
    },
    startNotificationListener : function(req) {
        req.command = "PLGN_NOT_LISTR_STRT";
        this.regi_callNative(req);
    },
    stopNotificationListener : function(req) {
        req.command = "PLGN_NOT_LISTR_STOP";
        this.regi_callNative(req);
    },
    startShortcutListener : function(req) {
        req.command = "PLGN_SHORTCUT_LISTR_STRT";
        this.regi_callNative(req);
    },
    stopShortcutListener : function(req) {
        req.command = "PLGN_SHORTCUT_LISTR_STOP";
        this.regi_callNative(req);
    },
    startUniversalLinkListener : function(req) {
        req.command = "PLGN_UNIVERSAL_LINK_LISTR_STRT";
        this.regi_callNative(req);
    },
    stopUniversalLinkListener : function(req) {
        req.command = "PLGN_UNIVERSAL_LINK_LISTR_STOP";
        this.regi_callNative(req);
    },
    unzip : function(req) {
        if(validate_zip_unZip(req)){
        req.actionType="unZip";
        req.command = "PLGN_UNZIP";
        this.regi_callNative(req);
        }
    }, zip : function(req) {
        if(validate_zip_unZip(req)){
        req.actionType="Zip";
        req.command = "PLGN_ZIP";
        this.regi_callNative(req);
        }
    }, disableUIBounce : function(req) {
        req.command = "PLGN_DIS_BOUNCE";
        this.regi_callNative(req);
    }, remoteDebug : function(req) {
        if(validate_remoteDebug(req)){
        req.command = "PLGN_RMT_DBUG";
        this.regi_callNative(req);
        }
    }, disablePullDown : function(req) {
        req.refreshStatus = "disable";
        req.command = "PLGN_DIS_PLDN";
        this.regi_callNative(req);
    }, enablePullDown : function(req) {
        req.refreshStatus = "enable";
        req.command = "PLGN_EN_PLDN";
        this.regi_callNative(req);
    },deepLinking : function(req) {
        req.command = "PLGN_DEEP_LNK";
        this.regi_callNative(req);
    }, vibrate : function(req) {
        req.command = "PLGN_VIBRATE";
        this.regi_callNative(req);
    }, initializePDF : function(req) {
        req.action='intialize';
        req.command = "PLGN_GEN_PDF";
        this.regi_callNative(req);
    }, addPDFContent : function(req){
        req.action='addContent';
        req.command="PLGN_GEN_PDF";
        this.regi_callNative(req);
    }, generatePDF : function(req){
        req.action='createPdf';
        req.command="PLGN_GEN_PDF";
        this.regi_callNative(req);
    }, startNetworkListener : function(req) {
        req.command = "PLGN_NW_STR_LIST";
        this.regi_callNative(req);
    }, stopNetworkListener : function(req) {
        req.command = "PLGN_NW_STP_LIST";
        this.regi_callNative(req);
    }, customHTTPReq : function(req) {
        req.command = "PLGN_N_CUST_HTTP_REQ";
        this.regi_callNative(req);
    }, setMainAppParams:function(mainAppParams){
        // internal apz sdk call
        apz.ns.mainAppParams = mainAppParams;
    }, getMainAppParams:function(req){
        if(apz.ns.mainAppParams != null){
            return apz.ns.mainAppParams;
        }else{
            return null;
        }
    }, sendDataToMainApp:function(req){
        req.command = "PLGN_SDK_CALLBACK";
        this.regi_callNative(req);
    }, isAppTokenSet:function(req){
        req.command = "PLGN_APP_TOKEN_SET";
        this.regi_callNative(req);
    }, inAppReview:function(req){
        req.command = "PLGN_IN_APP_REVIEW";
        this.regi_callNative(req);
    }, changeStatusBarColor:function(req){
        req.command = "PLGN_STATUS_BAR_COLOR";
        this.regi_callNative(req);
    }, checkGpsStatus:function(req){
        req.command = "PLGN_GPS_STATUS";
        req.action = "CHECK_LOCATION_AVAILABILITY";
        this.regi_callNative(req);
    }, checkAndRedirectGPS:function(req){
        req.command = "PLGN_GPS_STATUS";
        req.action = "REDIRECT_TO_SETTINGS";
        this.regi_callNative(req);
    }, clearWebviewCache:function(req){
        req.command = "PLGN_CLR_WEB_CACHE";
        this.regi_callNative(req);
    },redirectToSettings:function(req){
        req.command = "PLGN_REDIRECT_TO_SETTINGS";
        this.regi_callNative(req);
    },importDeviceFiles:function(req){
        req.command = "PLGN_IMPORT_DEVICE_FILES";
        this.regi_callNative(req);
    }, modifyShortCutItems:function(req){
        req.command = "PLGN_MODIFY_SHORTCUTITEMS";
        this.regi_callNative(req);
    }, checkNFCSupport:function(req){
        req.command = "PLGN_CHECK_NFCSUPPORT";
        this.regi_callNative(req);
    },loadLanguageStrings:function(req){
        req.command = "PLGN_LOAD_NATIVE_STRINGS";
        this.regi_callNative(req);
    },isNetworkAvailable:function(req){
        req.command = "PLGN_NTW_AVAILABILITY";
        this.regi_callNative(req);
    }
};
////////////////VALIDATION//////////////
validate_camera= function(req){
    var lcheck = false;
    var lactionpresent = apz.containsKey(req, "action");
//    var lcroppresent = apz.containsKey(req, "crop");
    if (!lactionpresent) {
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-168",false);
    } else {
        var lactionnull = false;
        lactionval = false;
        if (!apz.isNull(req.action)) {
            lactionnull = true;
            if (req.action=="srcBase64" || req.action=="srcUrl" ||req.action=="base64" || req.action=="save" ||req.action=="base64_Save"){
                lactionval = true;
            }
        }
        var lhtmlidpresent = false;
        var lhtmlidnull = false;
        var lfilenamepresent = false;
        var lfilenamenull = false;
        if (lactionval == true){
            if (req.action == 'srcBase64'){
                lfilenamepresent = true;
                lfilenamenull = true;
                var lhtmlidpresent = apz.containsKey(req, "elementId");
                if (lhtmlidpresent){
                    if (!apz.isNull(req.elementId)) {
                        lhtmlidnull = true;
                    }
                }
            }
            else if (req.action == 'srcUrl'){
                var lhtmlidpresent = apz.containsKey(req, "elementId");
                if (lhtmlidpresent){
                    if (!apz.isNull(req.elementId)) {
                        lhtmlidnull = true;
                    }
                }
                var lfilenamepresent = apz.containsKey(req, "fileName");
                if (lfilenamepresent){
                    if (!apz.isNull(req.fileName)) {
                        lfilenamenull = true;
                    }
                }
            }
            else if (req.action == 'base64_Save'){
                var lhtmlidpresent = true;
                var lhtmlidnull = true;
                var lfilenamepresent = apz.containsKey(req, "fileName");
                if (lfilenamepresent){
                    if (!apz.isNull(req.fileName)) {
                        lfilenamenull = true;
                    }
                }
            }
            else if (req.action == 'save'){
                lhtmlidpresent = true;
                lhtmlidnull = true;
                var lfilenamepresent = apz.containsKey(req, "fileName");
                if (lfilenamepresent){
                    if (!apz.isNull(req.fileName)) {
                        lfilenamenull = true;
                    }
                }
            }
            else{
                lhtmlidpresent = true;
                lhtmlidnull = true;
                lfilenamepresent = true;
                lfilenamenull = true;
            }
        }
        if (!lactionnull) {
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-168",false);
        } else if (!lactionval) {
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-193",false);
        } else if (!lhtmlidpresent) {
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-170",false);
        } else if (!lhtmlidnull) {
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-170",false);
        } else if (!lfilenamepresent) {
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-169",false);
        } else if (!lfilenamenull) {
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-169",false);
        }
//        else if (lcroppresent)
//        {
//            var cropString=req.crop;
//            var cropValueArray = cropString.split(",");
//            if(cropValueArray.length!=4 || apz.isNull(cropValueArray.length)){
//                regi_callNativeCBwithErrorCode(req,"APZ-CNT-195",false);
//            }else{
//                var firstValue=cropValueArray[0];
//                var secondValue=cropValueArray[1];
//                var thirdValue=cropValueArray[2];
//                var forthValue=cropValueArray[3];
//                firstValue = parseFloat(firstValue);
//                secondValue = parseFloat(secondValue);
//                thirdValue = parseFloat(thirdValue);
//                forthValue = parseFloat(forthValue);
//                if(isNaN(firstValue)||isNaN(secondValue)||isNaN(thirdValue)||isNaN(forthValue)){
//                    regi_callNativeCBwithErrorCode(req,"APZ-CNT-195",false);
//                    
//                }else{
//                    lcheck=true;
//                }
//            }
//        }
        else if (!apz.isNull(req.size))
        {
            var sizeString=req.size;
            var sizeValueArray = sizeString.split(",");
            if(sizeValueArray.length!=2 || apz.isNull(sizeValueArray.length)){
                regi_callNativeCBwithErrorCode(req,"APZ-CNT-196",false);
            }else{
                var firstValue=sizeValueArray[0];
                var secondValue=sizeValueArray[1];
                firstValue = parseFloat(firstValue);
                secondValue = parseFloat(secondValue);
                if(isNaN(firstValue)||isNaN(secondValue)){
                    regi_callNativeCBwithErrorCode(req,"APZ-CNT-196",false);
                }else{
                    lcheck=true;
                }
            }
        }
        else{
            lcheck=true;
        }
    }
    return lcheck;
    
};
validate_remoteDebug = function (req) {
    var lcheck= false;
    var ldebugpresent = apz.containsKey (req, "debug");
    if (!ldebugpresent) {
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-194",false);
    } else {
        var ldebugnull = false;
        if (!apz.isNull(req.debug)) {
            ldebugnull = true;
        }
        if (!ldebugnull) {
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-194",false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;
};

validate_deleteNotification = function(req) {
    var lcheck=true;
    if(apz.isNull(req.notificationID)) {
        lcheck=false;
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-276",false);
    }
    return lcheck;
}

validate_updateNotification = function(req) {
    var lcheck=true;
    if(apz.isNull(req.notificationID)) {
        lcheck=false;
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-276",false);
    } else if(apz.isNull(req.readFlag)) {
        lcheck=false;
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-277",false);
    }
    return lcheck;
}

validate_startUpdatingLocation = function(req) {
    var lcheck=true;
    if(req.periodicity=="intervalBased"){
    if((apz.isNull(req.timeInterval)) && (apz.isNull(req.distanceInterval)))
    {
        lcheck=false;
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-125",false);
    }
    }
 return lcheck;
 }
validate_audio = function(req) {
    var lcheck=true;
    if(apz.isNull(req.action)){
        lcheck=false;
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-168",false);
    }else if((req.action=="play") || (req.action=="record")){
        if(apz.isNull(req.location)){
            lcheck=false;
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-278",false);
        }else if(apz.isNull(req.fileName)){
            lcheck=false;
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-169",false);
        }
    }
    
    return lcheck;
}
validate_contactAdd = function(req) {
    var lcheck=true;
    if(apz.isNull(req.details.firstName)){
        lcheck=false;
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-283",false);
    }
    return lcheck;
}
validate_calendar = function(req) {
    var lcheck = true;
    if(req.action == "create"){
        var eventsArray = [];
        eventsArray = req.events;
        for (var i = 0; i < eventsArray.length; i++)
        {
            if (apz.isNull(eventsArray[i].title)) {
                lcheck = false;
                regi_callNativeCBwithErrorCode(req, "APZ-CNT-087", false);
            } else if (apz.isNull(eventsArray[i].startDate) || apz.isNull(eventsArray[i].startTime)) {
                lcheck = false;
                regi_callNativeCBwithErrorCode(req, "APZ-VAL-008", false);
            } else if (apz.isNull(eventsArray[i].endDate) || apz.isNull(eventsArray[i].endTime)) {
                lcheck = false;
                regi_callNativeCBwithErrorCode(req, "APZ-VAL-008", false);
            }
        }
    }else if((req.action == "edit") || (req.action == "delete")){
        if(req.events != null){
            var eventsArray = [];
            eventsArray = req.events;
            for (var i = 0; i < eventsArray.length; i++)
            {
                if (apz.isNull(eventsArray[i].title)){
                    lcheck=false;
                    regi_callNativeCBwithErrorCode(req,"APZ-CNT-087",false);
                }else if (apz.isNull(eventsArray[i].startDate)||apz.isNull(eventsArray[i].startTime)){
                    lcheck=false;
                    regi_callNativeCBwithErrorCode(req,"APZ-VAL-008",false);
                }else if (apz.isNull(eventsArray[i].endDate)||apz.isNull(eventsArray[i].endTime)){
                    lcheck=false;
                    regi_callNativeCBwithErrorCode(req,"APZ-VAL-008",false);
                }else if(req.action=="edit"){
                    if(apz.isNull(eventsArray[i].startDate) || apz.isNull(eventsArray[i].newStartTime)){
                        lcheck=false;
                        regi_callNativeCBwithErrorCode(req,"APZ-VAL-008",false);
                    }else if(apz.isNull(eventsArray[i].endDate) || apz.isNull(eventsArray[i].newEndTime)){
                        lcheck=false;
                        regi_callNativeCBwithErrorCode(req,"APZ-VAL-008",false);
                    }
                }
            }
        }else{
            if (apz.isNull(req.title)){
                lcheck=false;
                regi_callNativeCBwithErrorCode(req,"APZ-CNT-087",false);
            }else if (apz.isNull(req.startDate)||apz.isNull(req.startTime)){
                lcheck=false;
                regi_callNativeCBwithErrorCode(req,"APZ-VAL-008",false);
            }else if (apz.isNull(req.endDate)||apz.isNull(req.endTime)){
                lcheck=false;
                regi_callNativeCBwithErrorCode(req,"APZ-VAL-008",false);
            }else if(req.action=="edit"){
                if(apz.isNull(req.startDate) || apz.isNull(req.newStartTime)){
                    lcheck=false;
                    regi_callNativeCBwithErrorCode(req,"APZ-VAL-008",false);
                }else if(apz.isNull(req.endDate) || apz.isNull(req.newEndTime)){
                    lcheck=false;
                    regi_callNativeCBwithErrorCode(req,"APZ-VAL-008",false);
                }
            }
        }
        
    }

    
    return lcheck;
}

validate_map = function(req) {
    var lcheck=true;
    for(var i = 0; i < req.markerInfo.length; i++) {
        var lati = req.markerInfo[i].locationLatitude;
        var longi = req.markerInfo[i].locationLongitude;
        if(apz.isNull(lati)) {
            lcheck=false;
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-181",false);
        } else if(apz.isNull(longi)) {
            lcheck=false;
            regi_callNativeCBwithErrorCode(req,"APZ-CNT-181",false);
        }
    }
    return lcheck;
}

validate_filebrowser = function(req) {
    var lcheck=true;
    if(apz.isNull(req.filter)){
        lcheck=false;
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-283",false);
       }else if(apz.isNull(req.fileCategory)){
        lcheck=false;
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-284",false);
       }
    return lcheck;
}

validate_executeSql = function(req) {
    var lcheck=true;
    if(apz.isNull(req.queryId)){
        lcheck=false;
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-286",false);
    }else if(apz.isNull(req.databaseName)){
        lcheck=false;
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-287",false);
    }else if(apz.isNull(req.executeQuery)){
        lcheck=false;
        regi_callNativeCBwithErrorCode(req,"APZ-CNT-288",false);
    }
    return lcheck;
}


validate_mail = function(jsonobject) {
    var lcheck = false;
        var lbodycheck = false;
        if (!apz.isNull(jsonobject.body)) {
            lbodycheck = true;
        }
        var lrecipientmail = false;
        if (!apz.isNull(jsonobject.recipientMailId)) {
            lrecipientmail = true;
        }
        var lsubjectcheck = false;
        if (!apz.isNull(jsonobject.subject)) {
            lsubjectcheck = true;
        }
        var lidcheck = false;
        if (!apz.isNull(jsonobject.mailId)) {
            lidcheck = true;
        }
        if (!lbodycheck) {
            regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-172",false);
        } else if (!lrecipientmail) {
            regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-173",false);

        } else if (!lsubjectcheck) {
            regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-174",false);

        } else if (!lidcheck) {
            regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-175",false);

        } else {
            lcheck = true;
        }
//    }
    return lcheck;
};
validate_getAppVersion = function(jsonobject) {
    var lcheck = false;
    if (apz.isNull(jsonobject)) {
        regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-183",false);
    } else {
        lcheck = true;
    }
    return lcheck;
};
validate_getInstructions = function(jsonobject) {
    var lcheck = false;
    var lappnamepresent=apz.containsKey(jsonobject, "appId");
    if (!lappnamepresent) {
        regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-183",false);
     }
     else {
        var lappname = false;
        if (!apz.isNull(jsonobject.appId)) {
            lappname = true;
        }
        if (!lappname) {
            regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-183",false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;
};

validate_subappDelete = function (jsonobject) {
    var lcheck = false;
    if (apz.isNull(jsonobject.appId)) {
        regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-183",false);
    } else {
        lcheck = true;
    }
    return lcheck;
};

validate_FileEncDesc = function (jsonobject){
    var lcheck = false;
    var lKeyPresent=apz.containsKey(jsonobject, "key");
    var lsrcFilePathcheck=apz.containsKey(jsonobject, "srcFilePath");
    var ldestFilePathcheck=apz.containsKey(jsonobject, "destFilePath");
    if (!lKeyPresent) {
        regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-184",false);
    } else {
        var lkeycheck = false;
        if (!apz.isNull(jsonobject.key)) {
            lkeycheck = true;
        }
        if (!lkeycheck) {
            regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-184",false);
        } else if (!lsrcFilePathcheck) {
            regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-190",false);
        } else if (!ldestFilePathcheck) {
            regi_callNativeCBwithErrorCode(jsonobject,"APZ-CNT-191",false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;
};

validateinrange = function(min,number,max){
    if ( !isNaN(number) && !(number === Infinity) && !(number === "Infinity") && (number >= min) && (number <= max) ){
        return true;
    } else {
        return false;
    }
};
validateWhiteList = function validateWhiteList(url) {
    var lproceed = false;
    var urlWhiteList = apz.urlWhiteList;
    if (urlWhiteList.length > 0) {
        var check = JSON.parse(urlWhiteList);
        for (var i = 0; i < check.length; i++) {
            var counter = check[i].URL;
            if (counter == url) {
                lproceed = true;
                break;
            }
        }
    }
    return lproceed;
};
validate_zip_unZip = function (json){
    var lsrcFilePathcheck = apz.isNull(json.srcFilePath);
    var ldestFilePathcheck = apz.isNull(json.destFilePath);
    var lcheck=true;
    if(lsrcFilePathcheck){
        lcheck=false;
        regi_callNativeCBwithErrorCode(json,"APZ-CNT-190",false);
    }
    else if(ldestFilePathcheck){
        lcheck=false;
        regi_callNativeCBwithErrorCode(json,"APZ-CNT-191",false);
    }
    return lcheck;
}

regi_callNativeCBwithErrorCode= function(req,errorCode,keepAlive){
    this.apz.initNativeService(req);
    req.errorCode=errorCode;
    req.keepAlive=keepAlive;
    Apz.nativeServiceCB(req);
};
