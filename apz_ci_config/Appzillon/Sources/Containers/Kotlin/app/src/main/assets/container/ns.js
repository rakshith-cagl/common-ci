Apz.Ns = function(apz) {
    this.apz = apz;
	this.mainAppParams = {};
};
///////////////////Prototype Definition///////////////////////
Apz.Ns.prototype = {
    callNative: function(req) {
        try {
            NativeBridge.executePlugin(JSON.stringify(req));
        } catch (e) {
            console.log(e);
        }
    },
    log: function(msg, type) {
        if (typeof apzIde !== "undefined") {
            apzIde.log(msg);
        } else {
            console.log(msg);
        }
    },
    changeStatusBarColor:function(req){
         this.apz.initNativeService(req);
         req.command = "PLGN_STATUS_BAR_COLOR";
         this.callNative(req);
    },
    getDeviceInfo: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_DEV_INFO";
        this.callNative(req);
    },
    getUserPrefs: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_GET_USER_PREF";
        this.callNative(req);
    },

    createTable: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_DB_OPERATION";
        req.action = "CREATE_DB";
        this.callNative(req);
    },

    securityWifiStatus: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SECURITY_UTIL";
        req.action = "getWifiStatus";
        this.callNative(req);
    },

	checkGpsStatus:function(req){
         this.apz.initNativeService(req);
         req.command = "PLGN_CHECK_GPS_STATUS";
         req.action = "CHECK_LOCATION_AVAILABILITY";
         this.callNative(req);
    },
    
     redirectToSettings:function(req){
             this.apz.initNativeService(req);
             req.command = "PLGN_REDIRECT_TO_SETTINGS";
             this.callNative(req);
        },
    
	checkAndRedirectGPS:function(req){
         this.apz.initNativeService(req);
         req.command = "PLGN_CHECK_GPS_STATUS";
         req.action = "REDIRECT_TO_SETTINGS";
         this.callNative(req);
    },

    insertTable: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_DB_OPERATION";
        req.action = "INSERT_DB";
        this.callNative(req);
    },


     updateTable: function(req) {
         this.apz.initNativeService(req);
         req.command = "PLGN_DB_OPERATION";
         req.action = "UPDATE_DB";
         this.callNative(req);
    },

     deleteTable: function(req) {
         this.apz.initNativeService(req);
         req.command = "PLGN_DB_OPERATION";
         req.action = "DELETE_DB";
         this.callNative(req);
     },

      migrateTable: function(req) {
              this.apz.initNativeService(req);
              req.command = "PLGN_DB_OPERATION";
              req.action = "MIGRATE_DB";
              this.callNative(req);
      },

      getFromTable: function(req) {
              this.apz.initNativeService(req);
              req.command = "PLGN_DB_OPERATION";
              req.action = "GET_FROM_DB";
              this.callNative(req);
      },

    encryptData: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_ENCRPT_DATA";
        this.callNative(req);
    },
    decryptData: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_DECRPT_DATA";
        this.callNative(req);
    },
    hashPwd: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_HASH_PWD";
        this.callNative(req);
    },
    showSplash: function(req) {
        /*this.apz.initNativeService(req);
        req.command = "PLGN_SPLASH_SHOW";
        this.callNative(req);*/
    },
    hideSplash: function(req) {
        /*this.apz.initNativeService(req);
        req.command = "PLGN_SPLASH_HIDE";
        this.callNative(req);*/
    },
    nativeServiceExt: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_NTV_EXT";
        this.callNative(req);
    },
    openCamera: function(req) {
        if (validate_camera(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_OPN_CAMERA";
            this.callNative(req);
        }
    },
    detectDocument: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_OPN_AUTOCAPTURE";
        this.callNative(req)
    },
    selfieCapture: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_OPN_SELFIECAPTURE";
        this.callNative(req)
    },
    processImage: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_IMG_PROC";
        this.callNative(req)
    },
    callNumber: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_CALL_NMB";
        this.callNative(req);
    },
    base64ToFile: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_B64_TO_FILE";
        this.callNative(req);
    },
    openUrl: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_OPEN_URL";
        this.callNative(req);
    },
    fileToBase64: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_FILE_TO_B64";
        this.callNative(req);
    },
    getFileSize: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_FILE_SIZE";
        req.action = "GETFILESIZE";
        this.callNative(req);
    },
    downloadManager: function(req) {
        this.apz.initNativeService(req);
        req.command="PLGN_FILE_DOWNLOADMGR";
        req.action="FILEDOWNLOAD_MANAGER";
        this.callNative(req);
    },
    launchWebview: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_LNCH_WEBVW";
        req.action = "Open";
        this.callNative(req);
    },
    closeWebview: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_CLS_WEBVW";
        req.action = "Close";
        this.callNative(req);
    },
    closeApplication: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_CLS_APPCTN";
        this.callNative(req);
    },
    startShortcutListener: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SHORT_LISTR_STRT";
        req.action = "STARTLISTENER";
        this.callNative(req);
    },
    stopShortcutListener: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SHORT_LISTR_STOP";
        req.action = "STOPLISTENER";
        this.callNative(req);
    },
    getLocation: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_GET_LOCATION";
        this.callNative(req);
    },
    zip: function(req) {
        if (validate_zip_unZip(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_ZIP";
            req.action = "zip";
            this.callNative(req);
        }
    },
    unzip: function(req) {
            if (validate_zip_unZip(req)) {
                this.apz.initNativeService(req);

                req.command = "PLGN_UNZIP";
                req.action = "unzip";
                this.callNative(req);
            }
        },
    startBarcodeScan: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SCN_BARCODE";
        req.action = "START";
        this.callNative(req);
    },
    stopBarcodeScan: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SCN_BARCODE";
        req.action = "STOP";
        this.callNative(req);
    },
    barcodeFromGallery: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_GALRY_SCN_BARCODE";
        req.action = "SCAN_FROM_GALLERY";
        this.callNative(req);
    },
    createCalendarEvent: function(req) {
    req.action = "create";
        if (validate_calendar(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_CRT_CAL_EVT";
            //req.action = "create";
            req.dateFormat = this.apz.dateFormat;
            this.callNative(req);
        }
    },
    editCalendarEvent: function(req) {
    req.action = "edit";
        if (validate_calendar(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_EDT_CAL_EVT";
            //req.action = "edit";
            req.dateFormat = this.apz.dateFormat;
            this.callNative(req);
        }
    },
    deleteCalendarEvent: function(req) {
    req.action = "delete";
        if (validate_calendar(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_DLT_CAL_EVT";
            //req.action = "delete"; // calendar already has action param
            req.dateFormat = this.apz.dateFormat;
            this.callNative(req);
        }
    },
    executeSql: function(req) {
        if (validate_executeSql(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_EXE_SQL";
            this.callNative(req);
        }
    },
    deviceDetails: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_DEV_DETAILS";
        this.callNative(req);
    },
    startBatteryMonitor: function(req) {
           this.apz.initNativeService(req);
           req.command = "PLGN_BT_MTR_START";
           req.action = "Start";
           this.callNative(req);
       },
    stopBatteryMonitor: function(req) {
           this.apz.initNativeService(req);
           req.command = "PLGN_BT_MTR_STOP";
           req.action = "Stop";
           this.callNative(req);
       },
    encryptFile: function(req) {
        if (validate_FileEncDesc(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_ENCRPT_FILE";
            req.action = "ENCRYPT";
            this.callNative(req);
        }
    },
    decryptFile: function(req) {
        if (validate_FileEncDesc(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_DECRPT_FILE";
            req.action = "DECRYPT";
            this.callNative(req);
        }
    },
    startLocationTracking: function(req) {
        if (validate_startUpdatingLocation(req)) {
            this.apz.initNativeService(req);
            if (apz.isNull(req.periodicity)) {
                req.periodicity = "none";
            }
            req.command = "PLGN_LOC_TRCK_START";
            req.action = "START";
            this.callNative(req);
        }
    },
    stopLocationTracking: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_LOC_TRCK_STOP";
        req.action = "STOP";
        this.callNative(req);
    },
    startGesture: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_GSTR_START";
        req.action = "START";
        this.callNative(req);
    },
    stopGesture: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_GSTR_STOP";
        req.action = "STOP";
        this.callNative(req);
    },
    fileBrowser: function(req) {
    //    if (validate_filebrowser(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_BRWS_FILE";
            req.action = "BROWSER";
            this.callNative(req);
     //   }
    },
    getFileContent: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_FILE_CONTENT";
        req.action = "FILECONTENT";
        this.callNative(req);
    },
    createFile: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_CRT_FILE";
        req.action = "FILECREATE";
        this.callNative(req);
    },
    deleteFile: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_DEL_FILE";
        req.action = "FILEDELETE";
        this.callNative(req);
    },
    openFile: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_OPN_FILE";
        req.action = "OPENFILE";
        this.callNative(req);
    },
    sendMail: function(req) {
        if (validate_mail(req)) {
            if (req.internal.toUpperCase() == "Y") {
                var linterface = req.interfaceID;
                if ((linterface == "") || (linterface == "undefined") || (linterface == undefined) || (linterface == null)) {
                    linterface = "appzillonMailRequest";
                }
                var requestBody = {
                    "appzillonMailRequest": {
                        "emailid": req.recipientMailId,
                        "subject": req.subject,
                        "CC": req.ccIdList,
                        "body": req.body
                    }
                };
                var serverParams = {};
                serverParams.ifaceName = linterface;
                serverParams.buildReq = "N";
                serverParams.req = requestBody;
                serverParams.paintResp = "N";
                serverParams.id = "MAIL_ID"
                serverParams.async = false
		//Natasha's changes for Internal Mail 17-07-2017
		serverParams.internal = true;
                serverParams.callBackObj = this;
                serverParams.callBack = req.callBack;
                apz.server.callServer(serverParams);
            } else if (req.internal.toUpperCase() == "N") {
                ////Init
                this.apz.initNativeService(req);
                req.command = "PLGN_SEND_MAIL";
                this.callNative(req);
            }
        }
    },
    addContact: function(req) {
        if (validate_contactAdd(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_ADD_CONT";
            req.action = "ADDCONTACT";
            this.callNative(req);
        }
    },
    deleteContact: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_DEL_CONT";
        req.action = "DELETECONTACT";
        this.callNative(req);
    },
    searchContact: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SRCH_CONT";
        req.action = "SEARCHCONTACT";
        this.callNative(req);
    },
    editContact: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_EDIT_CONT";
        req.action = "EDITCONTACT";
        this.callNative(req);
    },
    fetchContact: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_FETCH_CONT";
        req.action = "FETCHCONTACT";
        this.callNative(req);
    },
    fetchAllContacts : function(req) {
       this.apz.initNativeService(req);
       req.command = "PLGN_FETCH_CONT";
       req.action = "FETCHALLCONTACTS";
       this.callNative(req);
   },
    currentLocale: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_CRNT_LOCALE";
        this.callNative(req);
    },
    signaturePad: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SIGN_PAD";
        this.callNative(req);
    },
    startIdleTimer: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_IDLE_TMR_START";
        this.callNative(req);
    },
    lockRotation: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_LCK_ROTN";
        req.action = "LOCKROTATION";
        this.callNative(req);
    },
    unlockRotation: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_UNLCK_ROTN";
        req.action = "UNLOCKROTATION";
        this.callNative(req);
    },
    setOrientation: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SET_ORTN";
        this.callNative(req);
    },
    vibrate: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_VIBRATE";
        req.action = "START_VIBRATE";
        this.callNative(req);
    },
    stopVibrate: function(req) {
       	this.apz.initNativeService(req);
        req.command = "PLGN_VIBRATE";
        req.action = "STOP_VIBRATE";
        this.callNative(req);
    },
    voiceToText: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_VOICE";
        this.callNative(req);
    },
    detectEvents: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_DET_EVE";
        this.callNative(req);
    },
    wipeOut: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_WIPEOUT";
        req.action = "unInstallApp";
        this.callNative(req);
    },
    isDeviceNFCSupported: function(req) {
            this.apz.initNativeService(req);
            req.command = "PLUGIN_IS_NFC_SUPPORTED";
            this.callNative(req);
        },
    getIP: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_GET_IP";
        this.callNative(req);
    },
    getAppVersion: function(req) {
        if (validate_getAppVersion(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_APP_VERSION";
            this.callNative(req);
        }
    },
    enablePullDown: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_EN_PLDN";
        req.action = "Enable";
        this.callNative(req);
    },
    disablePullDown: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_DIS_PLDN";
        req.action = "Disable";
        this.callNative(req);
    },
    hideRefresh: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_HIDE_REFRESH";
        req.action = "HideRefresh";
        this.callNative(req);
    },
    audio: function(req) {
          if (validate_audio(req)) {
              this.apz.initNativeService(req);
              req.command = "PLGN_AUDIO";
              this.callNative(req);
          }
      },
    getNotification: function(req) {
        var query = "SELECT * FROM tb_notifications;"
        req.queryId = req.notificationID;
        req.executeQuery = query;
        req.databaseName = "APPSDB"; // In case of iOS
        this.executeSql(req);
    },
    deleteNotification: function(req) {
        var ID = req.notificationID;
        var query = "DELETE FROM tb_notifications WHERE id = " + ID + ";";
        req.databaseName = "APPSDB";
        req.executeQuery = query;
        this.executeSql(req);
    },
    updateNotification: function(req) {
        var ID = req.notificationID;
        var readFlag = req.readFlag;
        readFlag = "'" + readFlag + "'";
        var query = "UPDATE tb_notifications SET readFlag = " + readFlag +
            " WHERE id = " + ID + ";";
        var params = {};
        req.databaseName = "notificationDB";
        req.executeQuery = query;
        this.executeSql(req);
    },
    subappDelete: function(req) {
        if (validate_subappDelete(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_SUBAPP_DEL";
            req.action = "APPDELETE";
            this.callNative(req);
        }
    },
    getInstructions: function(req) {
        if (validate_getInstructions(req)) {
            this.apz.initNativeService(req);
            req.command = "PLGN_GET_INST";
            req.action = "INSTRUCTIONS";
            this.callNative(req);
        }
    },
    upgradeRequired: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_UPGRD_REQ";
        req.action = "UPGRADEREQ";
        this.callNative(req);
    },
    upgradeApp: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_UPGRD_APP";
        req.action = "UPGRADEAPP";
        this.callNative(req);
    },
    getUpdateAction: function(req) {
            this.apz.initNativeService(req);
            req.command = "PLGN_UPDATE_REQ";
            req.action = "UPDATEACTION";
            this.callNative(req);
        },
    setUserPrefs: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SET_USER_PREF";
        req.action = "SETSETTINGS";
        this.callNative(req);
    },
    smsSend: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SMS_SEND";
        req.action = "SEND";
        this.callNative(req);
    },
    startSMSListener: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SMS_LSTN_START";
        req.action = "STARTLISTENER";
        this.callNative(req);
    },
    stopSMSListener: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SMS_LSTN_STOP";
        req.action = "STOPLISTENER";
        this.callNative(req);
    },
    startKeyboardListener: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_KEYBD_LISTR_STRT";
        req.action = "STARTLISTENER";
        this.callNative(req);
    },
    stopKeyboardListener: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_KEYBD_LISTR_STOP";
        req.action = "STOPLISTENER";
        this.callNative(req);
    },
    startOrientationListener: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_ORTN_LISTR";
        req.action = "STARTLISTENER";
        this.callNative(req);
    },
    stopOrientationListener: function(req) {
            this.apz.initNativeService(req);
            req.command = "PLGN_ORTN_LISTR";
            req.action = "STOPLISTENER";
            this.callNative(req);
    },
    startNotificationListener: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_NOT_LISTR_STRT";
        req.action = "STARTLISTENER";
        this.callNative(req);
    },
    stopNotificationListener: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_NOT_LISTR_STOP";
        req.action = "STOPLISTENER";
        this.callNative(req);
    },
    getPref: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_GET_PREF";
        this.callNative(req);
    },
    setPref: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_SET_PREF";
        this.callNative(req);
    },
    checkAppAvailability: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_APP_AVAILABILITY";
        this.callNative(req);
       
    },
    
    checkNotificationStatus: function(req) {
            this.apz.initNativeService(req);
            req.command = "PLGN_NOTIF_STATUS";
            this.callNative(req);
        },
    
    callNativeCBwithErrorCode: function(req, errorCode, keepAlive) {
        this.apz.initNativeService(req);
        req.errorCode = errorCode;
        req.keepAlive = keepAlive;
        Apz.nativeServiceCB(req);
    },
    biometricAuth: function(req) {
        req.command = "PLGN_BIO_AUTH";
        this.apz.initNativeService(req);
        this.callNative(req);
    },
     isAppTokenSet:function(req){
            this.apz.initNativeService(req);
            req.command = "PLGN_IS_APPTOKENSET";
            this.callNative(req);
    },
    uploadFile: function(req) {
        if (Apz.Server) {
            var screenId = req.screenId;
            var appzillonHeader;
            var params = {};
            if (req.sessionReq == 'Y') {
                params.ifaceName = 'appzillonUploadFile';
                params.scrName = screenId;
                params.async = true;
                params.id = 'uploadReqId';
                params.internal = true;
                appzillonHeader = apz.server.getHeader(params);
                //Natasha's changes 14-07-17 getheader changes in infra
               // appzillonHeader = apz.server.getHeader('appzillonUploadFile', screenId, 'uploadReqId', true);
            } else {
                 params.ifaceName = 'appzillonUploadFileWS';
                 params.scrName = screenId;
                 params.async = true;
                 params.id = 'uploadReqId';
                 params.internal = true;
                 appzillonHeader = apz.server.getHeader(params);
                 //Natasha's changes 14-07-17 getheader changes in infra
              //  appzillonHeader = apz.server.getHeader('appzillonUploadFileWS', screenId, 'uploadReqId', true);
            }
            req.appzillonHeader = appzillonHeader;
            this.apz.initNativeService(req);
            if (this.apz.isNull(req.filePath)) {
                req.fieldID = this.apz.getElmValue(req.fieldID);
            } else if (!this.apz.isNull(req.filePath)) {
                req.fieldID = req.filePath;
            } else {
                callNativeCBwithErrorCode(req, false, "APZ-CNT-099");
            }
            req.command = "PLGN_UPLD_FILE";
            req.action = "FILEUPLOAD";
            this.callNative(req);
        } else {
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-304", false);
        }
    },
    downloadFile: function(req) {
        if (Apz.Server) {
            var screenId = req.screenId;
            var appzillonHeader;
            var params = {};
            if (req.sessionReq == 'Y') {
                params.ifaceName = 'appzillonFilePushService';
                params.scrName = screenId;
                params.async = true;
                params.id = 'downloadReqId';
                params.internal = true;
                appzillonHeader = apz.server.getHeader(params);
                //Natasha's changes 14-07-17 getheader changes in infra
               // appzillonHeader = apz.server.getHeader('appzillonFilePushService', screenId, 'downloadReqId', true);
            } else {
                params.ifaceName = 'appzillonFilePushServiceWS';
                params.scrName = screenId;
                params.async = true;
                params.id = 'downloadReqId';
                params.internal = true;
                appzillonHeader = apz.server.getHeader(params);
                //Natasha's changes 14-07-17 getheader changes in infra
              //  appzillonHeader = apz.server.getHeader('appzillonFilePushServiceWS', screenId, 'downloadReqId', true);
            }

            var downloadBody = {
                "appzillonFilePushServiceRequest": {
                    "fileName": req.fileName,
                    "filePath": req.filePath,
                    "base64": "Y"// Server Changes
                }
            };

            var appzillonRequest = {
                "appzillonHeader": appzillonHeader,
                "appzillonBody": downloadBody
            }
            req.downloadReqDetails = appzillonRequest;
            this.apz.initNativeService(req);
            req.command = "PLGN_DWLD_FILE";
            req.action = "FILEDOWNLOAD";
            this.callNative(req);
        } else {
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-304", false);
        }
    },
    sendNFC: function(req) {
         this.apz.initNativeService(req);
         req.command = "PLGN_SND_NFC";
         req.executeAction = "SEND";
         this.callNative(req);
     },
     receiveNFC: function(req) {
         this.apz.initNativeService(req);
         req.executeAction = "RECEIVE";
         req.command = "PLGN_RCV_NFC";
         this.callNative(req);
     },
     stopNFC: function(req) {
         this.apz.initNativeService(req);
         req.executeAction = "STOP";
         req.command = "PLGN_STOP_NFC";
         this.callNative(req);
     },
    readFile: function(req) {
        this.apz.initNativeService(req);
        //  req.command = "PLGN_READ_FILE";
        req.command = "PLGN_OPN_FILE";
        req.action = "OPENFILE";
        this.callNative(req);
    },
   deepLinking: function(req) {
	this.apz.initNativeService(req);
	req.command = "PLGN_DEEP_LNK";
	this.callNative(req);  
	},
   startRealTimeTrackLocation: function(req) {
	this.apz.initNativeService(req);
	req.command = "PLGN_TRACK_LOC";
	req.action = "START";
	this.callNative(req);
	},
   stopRealTimeTrackLocation: function(req) {
	this.apz.initNativeService(req);
	req.command = "PLGN_TRACK_LOC";
	req.action = "STOP";
	this.callNative(req);
	},
   sendReq: function(req) {
    this.apz.initNativeService(req);
    req.command = "PLGN_N_SRVRCALL";
    this.callNative(req);
    },
    generatePDF: function(req){
     this.apz.initNativeService(req);
     req.command = "PLGN_GEN_PDF";
     req.action = "generate";
     this.callNative(req);
    },
    addPDFContent: function(req){
     this.apz.initNativeService(req);
     req.command = "PLGN_GEN_PDF";
     req.action = "append";
     this.callNative(req);
    },
    genBarcode: function(req){
     this.apz.initNativeService(req);
     req.command = "PLGN_GEN_BARCODE";
     this.callNative(req);
    },
    storeSecurely: function(req){
         this.apz.initNativeService(req);
         req.command = "PLGN_STORE_RETRIEVE_SECURE";
         req.action = "STORESECURELY";
         this.callNative(req);
    },
    retrieveSecurely: function(req){
         this.apz.initNativeService(req);
         req.action = "RETRIEVESECURELY";
         req.command = "PLGN_STORE_RETRIEVE_SECURE";
         this.callNative(req);
    },
    storeCredentialSecurely: function(req){
         this.apz.initNativeService(req);
         req.action = "STORECREDENTIALSSECURELY";
         req.command = "PLGN_STORE_RETRIEVE_SECURE";
         this.callNative(req);
    },
    login: function(req){
         this.apz.initNativeService(req);
         req.action = "RETRIEVECREDENTIALSSECURELY";
         req.command = "PLGN_STORE_RETRIEVE_SECURE";
         this.callNative(req);
     },
     refreshServerNonce: function(req){
        this.apz.initNativeService(req);
        req.command = "PLGN_REFSVRNONCE";
        this.callNative(req);
     },
     biometricAvailability:function(req){
      this.apz.initNativeService(req);
      req.command = "PLGN_BIOMET_AVAIL";
      this.callNative(req);
     },
     changePassword:function(req){
       this.apz.initNativeService(req);
       req.command = "PLGN_CHNG_PWD";
       this.callNative(req);
     },
     nativeShare:function(req){
       this.apz.initNativeService(req);
       req.command = "PLGN_NATVE_SHARE";
       this.callNative(req);
     },
	 startNetworkListener: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_NETWORK_MONITOR";
        req.action = "START";
        this.callNative(req);
    },
	stopNetworkListener: function(req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_NETWORK_MONITOR";
        req.action = "STOP";
        this.callNative(req);
    },
	startDownloadNotifier: function(req) {
             this.apz.initNativeService(req);
             req.command = "PLGN_NOTIFY_DOWNLOAD";
             req.action = "START";
             this.callNative(req);
    },
    stopDownloadNotifier: function(req) {
             this.apz.initNativeService(req);
             req.command = "PLGN_NOTIFY_DOWNLOAD";
             req.action = "STOP";
             this.callNative(req);
    },
    customHTTPReq: function(req) {
             this.apz.initNativeService(req);
             req.command = "PLGN_CUSTOM_SERVERCALL";
             this.callNative(req);
    },
    setMainAppParams:function(mainAppParams){
		// internal apz sdk call
        apz.ns.mainAppParams = mainAppParams;
    },
    getMainAppParams:function(){
        if(apz.ns.mainAppParams != null){
            return apz.ns.mainAppParams;
        }else{
            return null;
        }
    },
    sendDataToMainApp:function(req){
        this.apz.initNativeService(req);
        req.command = "PLGN_SDK_CALLBACK";
        this.callNative(req);
    },
    simdetails: function(req) {
         this.apz.initNativeService(req);
         req.command = "PLGN_SMS_GET_SIMDET";
         req.action = "SIMDET";
         this.callNative(req);
    },
	isDebuggingEnabled:function(req){
        this.apz.initNativeService(req);
        req.command = "PLGN_DEBUGGING_ENABLED";
        this.callNative(req);
	},
	isNetworkAvailable:function(req){
       this.apz.initNativeService(req);
       req.command = "PLGN_IS_NETWORK_AVAILABLE";
       this.callNative(req);
    }
};

////////////////VALIDATION//////////////
validate_camera = function(req) {
    var lcheck = false;
    var lactionpresent = apz.containsKey(req, "action");
    var lcroppresent = apz.containsKey(req, "crop");
    if (!lactionpresent) {
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-082", false);
    } else {
        var lactionnull = false;
        lactionval = false;
        if (!apz.isNull(req.action)) {
            lactionnull = true;
            if (req.action == "base64" || req.action == "save" || req.action == "base64_Save") {
                lactionval = true;
            }
        }
        var lhtmlidpresent = false;
        var lhtmlidnull = false;
        var lfilenamepresent = false;
        var lfilenamenull = false;
        if (lactionval == true) {
            if (req.action == 'base64_Save') {
                var lhtmlidpresent = true;
                var lhtmlidnull = true;
                var lfilenamepresent = apz.containsKey(req, "fileName");
                if (lfilenamepresent) {
                    if (!apz.isNull(req.fileName)) {
                        lfilenamenull = true;
                    }
                }
            } else if (req.action == 'save') {
                lhtmlidpresent = true;
                lhtmlidnull = true;
                var lfilenamepresent = apz.containsKey(req, "fileName");
                if (lfilenamepresent) {
                    if (!apz.isNull(req.fileName)) {
                        lfilenamenull = true;
                    }
                }
            } else {
                lhtmlidpresent = true;
                lhtmlidnull = true;
                lfilenamepresent = true;
                lfilenamenull = true;
            }
        }
        if (!lactionnull) {
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-082", false);
        } else if (!lactionval) {
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-082", false);
        } else if (!lhtmlidpresent) {
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-170", false);
        } else if (!lhtmlidnull) {
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-170", false);
        } else if (!lfilenamepresent) {
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-169", false);
        } else if (!lfilenamenull) {
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-169", false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;

};
/*validate_launchApp = function(req) {
    var lcheck = false;
    if (apz.isNull(req.appId)) {
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-183", false);
    } else {
        lcheck = true;
    }
    return lcheck;
};*/
validate_remoteDebug = function(req) {
    var lcheck = false;
    var ldebugpresent = apz.containsKey(req, "debug");
    if (!ldebugpresent) {
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-194", false);
    } else {
        var ldebugnull = false;
        if (!apz.isNull(req.debug)) {
            ldebugnull = true;
        }
        if (!ldebugnull) {
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-194", false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;
};

validate_deleteNotification = function(req) {
    var lcheck = true;
    if (apz.isNull(req.notificationID)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-276", false);
    }
    return lcheck;
}

validate_updateNotification = function(req) {
    var lcheck = true;
    if (apz.isNull(req.notificationID)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-276", false);
    } else if (apz.isNull(req.readFlag)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-277", false);
    }
    return json;
}

validate_acc_compass_start = function(req) {
    var lcheck = true;
    if (req.periodicity == "timed" && (apz.isNull(req.interval))) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-132", false);
    }
    return lcheck;
}

validate_startUpdatingLocation = function(req) {
    var lcheck = true;
    if (req.periodicity == "intervalBased") {
        if ((apz.isNull(req.timeInterval)) && (apz.isNull(req.distanceInterval))) {
            lcheck = false;
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-125", false);
        }
    }
    return lcheck;
}
validate_audio = function(req) {
    var lcheck = true;
    if (apz.isNull(req.action)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-082", false);
    } else if ((req.action == "play") || (req.action == "record")) {
        if (apz.isNull(req.location)) {
            lcheck = false;
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-278", false);
        } else if (apz.isNull(req.fileName)) {
            lcheck = false;
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-169", false);
        }
    }

    return lcheck;
}

validate_multiviewOpen = function(req) {
    var lcheck = true;
    if (apz.isNull(req.id)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-279", false);
    } else if (apz.isNull(req.location)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-278", false);
    } else if (apz.isNull(req.percentage)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-280", false);
    } else if (apz.isNull(req.targetView)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-281", false);
    } else if (apz.isNull(req.launchPage)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-282", false);
    }
    return lcheck;
}
validate_multiviewClose = function(req) {
    var lcheck = true;
    if (apz.isNull(req.id)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-279", false);
    }
    return lcheck;
}
validate_contactAdd = function(req) {
    var lcheck = true;
    if (apz.isNull(req.details.firstName)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-283", false);
    }
    return lcheck;
}

// changed on 08-Dec-2020 Manu
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
    } else {
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

    return lcheck;
}

/*validate_map = function(req) {
    var lcheck = true;
    for (var i = 0; i < req.markerInfo.length; i++) {
        var lati = req.markerInfo[i].locationLatitude;
        var longi = req.markerInfo[i].locationLongitude;
        if (apz.isNull(lati)) {
            lcheck = false;
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-181", false);
        } else if (apz.isNull(longi)) {
            lcheck = false;
            regi_callNativeCBwithErrorCode(req, "APZ-CNT-181", false);
        }
    }
    return lcheck;
}*/

validate_filebrowser = function(req) {
    var lcheck = true;
    if (apz.isNull(req.filter)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-284", false);
    } else if (apz.isNull(req.fileCategory)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-284", false);
    }
    return lcheck;
}

validate_executeSql = function(req) {
    var lcheck = true;
    if (apz.isNull(req.queryId)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-286", false);
    } else if (apz.isNull(req.databaseName)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-287", false);
    } else if (apz.isNull(req.executeQuery)) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(req, "APZ-CNT-288", false);
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
    if (!apz.isNull(jsonobject.id)) {
        lidcheck = true;
    }
    if (!lbodycheck) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-172", false);
    } else if (!lrecipientmail) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-173", false);

    } else if (!lsubjectcheck) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-174", false);

    } else if (!lidcheck) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-175", false);

    } else {
        lcheck = true;
    }
    //    }
    return lcheck;
};
validate_geofencing = function(jsonobject) {
    var lcheck = false;
    var lregioncheck = apz.containsKey(jsonobject, "region");
    if (!lregioncheck) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-176", false);
    } else {
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
        if (!apz.isNull(jsonobject.region)) {
            lregionchecknull = true;
            if (jsonobject.region == "COUNTRY") {
                lregioncheckval = true;
                lradiuspresent = true;
                lradiusnull = true;
                lradiusval = true;
                lcoordpresent = true;
                lcoordnull = true;
                lcoordval = true;
                lcountylistpresent = apz.containsKey(jsonobject, "CountryList");
                if (lcountylistpresent) {
                    if (!apz.isNull(jsonobject.CountryList)) {
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
                lradiuspresent = apz.containsKey(jsonobject, "radius");
                if (lradiuspresent) {
                    if (!apz.isNull(jsonobject.radius)) {
                        lradiusnull = true;
                        if (!(isNaN(jsonobject.radius) || jsonobject.radius === Infinity || jsonobject.radius === "Infinity" || jsonobject.radius <= 0)) {
                            lradiusval = true;
                        }
                    }
                }
                lcoordpresent = apz.containsKey(jsonobject, "coordinates");
                if (lcoordpresent) {
                    if (!apz.isNull(jsonobject.coordinates)) {
                        lcoordnull = true;
                        var coordinates = jsonobject.coordinates.split(",");
                        if (coordinates.length == 2) {
                            if (!apz.isNull(coordinates[0]) && !apz.isNull(coordinates[1])) {
                                if (validateinrange(-90, coordinates[0], 90) && validateinrange(-180, coordinates[1], 180)) {
                                    lcoordval = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!lcountylistpresent) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-178", false);
        } else if (!lradiuspresent) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-179", false);
        } else if (!lcoordpresent) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-181", false);
        } else if (!lregionchecknull) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-176", false);
        } else if (!lcountylistnull) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-178", false);
        } else if (!lradiusnull) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-179", false);
        } else if (!lcoordnull) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-181", false);
        } else if (!lregioncheckval) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-177", false);
        } else if (!lcountylistval) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-178", false);
        } else if (!lradiusval) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-180", false);
        } else if (!lcoordval) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-182", false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;
};
validate_getAppVersion = function(jsonobject) {
    var lcheck = false;
    if (apz.isNull(jsonobject)) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-183", false);
    } else {
        lcheck = true;
    }
    return lcheck;
};
validate_getInstructions = function(jsonobject) {
    var lcheck = false;
    var lappnamepresent = apz.containsKey(jsonobject, "appId");
    if (!lappnamepresent) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-183", false);
    } else {
        var lappname = false;
        if (!apz.isNull(jsonobject.appId)) {
            lappname = true;
        }
        if (!lappname) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-183", false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;
};

validate_subappDelete = function(jsonobject) {
    var lcheck = false;
    if (apz.isNull(jsonobject.appId)) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-183", false);
    } else {
        lcheck = true;
    }
    return lcheck;
};

validate_FileEncDesc = function(jsonobject) {
    var lcheck = false;
    var lKeyPresent = apz.containsKey(jsonobject, "key");
    var lsrcFilePathcheck = apz.containsKey(jsonobject, "srcFilePath");
    var ldestFilePathcheck = apz.containsKey(jsonobject, "destFilePath");
    if (!lKeyPresent) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-184", false);
    } else {
        var lkeycheck = false;
        if (!apz.isNull(jsonobject.key)) {
            lkeycheck = true;
        }
        if (!lkeycheck) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-184", false);
        } else if (!lsrcFilePathcheck) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-190", false);
        } else if (!ldestFilePathcheck) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-191", false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;
};
/*validate_areaSelector = function(jsonobject) {
    var lcheck = false;
    var lradiuspresent = apz.containsKey(jsonobject, "radius");
    var lnearbyplacespresent = apz.containsKey(jsonobject, "nearbyplaces");
    if (!lradiuspresent) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-179", false);
    } else if (!lnearbyplacespresent) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-192", false);
    } else {
        var lradiuschecknull = false;
        var lradiuscheckval = false;
        if (!apz.isNull(jsonobject.radius)) {
            lradiuschecknull = true;
            if (!(isNaN(jsonobject.radius) || jsonobject.radius === Infinity || jsonobject.radius === "Infinity" || jsonobject.radius <= 0)) {
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
                    if (!apz.isNull(jsonobject.nearbyplaces[i].locationLatitude) && !apz.isNull(jsonobject.nearbyplaces[i].locationLongitude)) {
                        var locationLatitude = jsonobject.nearbyplaces[i].locationLatitude;
                        var locationLongitude = jsonobject.nearbyplaces[i].locationLongitude;
                        if (validateinrange(-90, locationLatitude, 90) && validateinrange(-180, locationLongitude, 180)) {
                            count++;
                        }
                    }
                }
                if (count == jsonobject.nearbyplaces.length)
                    lnearbyplacescheckval = true;
            }
        }
        if (!lradiuschecknull) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-179", false);
        } else if (!lnearbyplaceschecknull) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-192", false);
        } else if (!lradiuscheckval) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-180", false);
        } else if (!lnearbyplacescheckval) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-092", false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;

};
validater_drivingDirection = function(jsonobject) {
    var lcheck = false;
    var lfromLocationpresent = apz.containsKey(jsonobject, "fromLocation");
    var ltoLocationpresent = apz.containsKey(jsonobject, "toLocation");
    if (!ltoLocationpresent) {
        regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-188", false);
    } else {
        var lfromLocationnull = false;
        var lfromLocationval = false;
        var ltoLocationnull = false;
        var ltoLocationval = false;
        if (!apz.isNull(jsonobject.fromLocation)) {
            lfromLocationnull = true;
            var coordinates = jsonobject.fromLocation.split(",");
            if (coordinates.length == 2) {
                if (!apz.isNull(coordinates[0]) && !apz.isNull(coordinates[1])) {
                    if (validateinrange(-90, coordinates[0], 90) && validateinrange(-180, coordinates[1], 180)) {
                        lfromLocationval = true;
                    }
                }
            }
        } else {
            lfromLocationval = true;
        }
        if (!apz.isNull(jsonobject.toLocation)) {
            ltoLocationnull = true;
            var coordinates = jsonobject.toLocation.split(",");
            if (coordinates.length == 2) {
                if (!apz.isNull(coordinates[0]) && !apz.isNull(coordinates[1])) {
                    if (validateinrange(-90, coordinates[0], 90) && validateinrange(-180, coordinates[1], 180)) {
                        ltoLocationval = true;
                    }
                }
            }
        }
        if (!lfromLocationval) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-187", false);
        } else if (!ltoLocationnull) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-188", false);
        } else if (!ltoLocationval) {
            regi_callNativeCBwithErrorCode(jsonobject, "APZ-CNT-189", false);
        } else {
            lcheck = true
        }
    }
    return lcheck;
};*/
validateinrange = function(min, number, max) {
    if (!isNaN(number) && !(number === Infinity) && !(number === "Infinity") && (number >= min) && (number <= max)) {
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
validate_zip_unZip = function(json) {
    var lsrcFilePathcheck = apz.isNull(json.srcFilePath);
    var ldestFilePathcheck = apz.isNull(json.destFilePath);
    var lcheck = true;
    if (lsrcFilePathcheck) {
        lcheck = false;
        this.callNativeCBwithErrorCode(json, "APZ-CNT-190", false);
    } else if (ldestFilePathcheck) {
        lcheck = false;
        regi_callNativeCBwithErrorCode(json, "APZ-CNT-191", false);
    }
    return lcheck;
}

regi_callNativeCBwithErrorCode = function(req, errorCode, keepAlive) {
    this.apz.initNativeService(req);
    req.errorCode = errorCode;
    req.keepAlive = keepAlive;
    Apz.nativeServiceCB(req);
};
