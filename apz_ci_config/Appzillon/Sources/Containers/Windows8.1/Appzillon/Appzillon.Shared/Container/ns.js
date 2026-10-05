
Apz.Ns = function (apz) {
    this.apz = apz;
};
///////////////////Prototype Definition///////////////////////

Apz.Ns.prototype = {
    callNative: function (req) {
        try {
            this.apz.initNativeService(req);
            WinContainer.notify(req);
        }
        catch (e) {
            console.log(e);
        }
    },
    log: function (msg, type) {
        if (typeof apzIde !== "undefined") {
            apzIde.log(msg);
        } else {
            console.log(msg);
        }
    }, startAccelerometer: function (req) {
        if (validate_acc_compass_start(req)) {
            if (apz.isNull(req.periodicity)) {
                req.periodicity = "none";
            }
            req.command = "PLGN_ACC_START";
            this.callNative(req);
        }
    }, stopAccelerometer: function (req) {
        req.command = "PLGN_ACC_STOP";
        this.callNative(req);
    }, startAugmentation: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, reloadAugmentation: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, scanBarcode: function (req) {
        req.command = "PLGN_SCN_BARCODE";
        this.callNative(req);
    }, startBatteryMonitor: function (req) {
        req.command = "PLGN_BT_MTR_START";
        this.callNative(req);
    }, stopBatteryMonitor: function (req) {
        req.command = "PLGN_BT_MTR_STOP";
        this.callNative(req);
    }, startBeacon: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, stopBeacon: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, //Ruthvik 11- 04-17 Changed to latest Error Code 
	biometricAuth: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-135", false);
    }, scanFinger: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, callNumber: function (req) {
        req.command = "PLGN_CALL_NMB";
        this.callNative(req);
    }, getMissedCalls: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, startCallListener: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, makeSkypeCall: function (req) {
        req.command = "PLGN_SKYP_CL";
        this.callNative(req);
    },editCalendarEvent: function (req){
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, createCalendarEvent: function (req) {
        req.action = "create";
        if (validate_calendar(req)) {
            req.command = "PLGN_CRT_CAL_EVT";
            this.callNative(req);
        }
    }, deleteCalendarEvent: function (req) {
        req.action = "delete";
        if (validate_calendar(req)) {
            req.command = "PLGN_DLT_CAL_EVT";
            this.callNative(req);
        }
    }, deepLinking: function (req) {
        req.command = "PLGN_DEEP_LNK";
        this.callNative(req);
    }, openCamera: function (req) {
        if (validate_camera(req)) {
            req.command = "PLGN_OPN_CAMERA";
            this.callNative(req);
        }
    }, startCompass: function (req) {
        if (validate_acc_compass_start(req)) {
            if (apz.isNull(req.periodicity)) {
                req.periodicity = "none";
            }
            req.command = "PLGN_CMP_START";
            this.callNative(req);
        }
    }, stopCompass: function (req) {
        req.command = "PLGN_CMP_STOP";
        this.callNative(req);
    }, addContact: function (req) {
        if (validate_contactAdd(req)) {
            req.opnType = "add";
            req.command = "PLGN_ADD_CONT";
            this.callNative(req);
        }
    }, deleteContact: function (req) {
        req.command = "PLGN_DEL_CONT";
        this.callNative(req);
    }, editContact: function (req) {
        req.opnType = "edit";
        req.command = "PLGN_EDIT_CONT";
        this.callNative(req);
    }, searchContact: function (req) {
        req.opnType = "search";
        req.command = "PLGN_SRCH_CONT";
        this.callNative(req);
    }, fetchContact: function (req) {
        req.opnType = "fetch";
        req.command = "PLGN_FETCH_CONT";
        this.callNative(req);
    }, decryptData: function (req) {
        req.command = "PLGN_DECRPT_DATA";
        this.callNative(req);
    }, encryptData: function (req) {
        req.command = "PLGN_ENCRPT_DATA";
        this.callNative(req);
    }, decryptFile: function (req) {
        if (validate_FileEncDesc(req)) {
            req.command = "PLGN_DECRPT_FILE";
            this.callNative(req);
        }
    }, encryptFile: function (req) {
        if (validate_FileEncDesc(req)) {
            req.command = "PLGN_ENCRPT_FILE";
            this.callNative(req);
        }
    }, deviceDetails: function (req) {
        req.command = "PLGN_DEV_DETAILS";
        this.callNative(req);
    }, getDeviceInfo: function (req) {
        req.command = "PLGN_DEV_INFO";
        this.callNative(req);
    }, uploadFile: function (req) {
        //Needs to be updated according to new infra
        if (apz.isNull(req.filePath)) {
            req.fieldID = this.apz.getElmValue(req.fieldID);
        } else if (!apz.isNull(req.filePath)) {
            req.fieldID = req.filePath;
        } else {
            callNativeCBwithErrorCode(req,"APZ-CNT-099",false);
            return;
        }
        if (!Apz.Server) {
            callNativeCBwithErrorCode(req, "APZ-CNT-034", false);
            return;
        }
        req.command = "PLGN_UPLD_FILE";
        this.callNative(req);
    }, downloadFile: function (req) {
        // req.sessionReq = "Y";
        if (!Apz.Server) {
            callNativeCBwithErrorCode(req, "APZ-CNT-034", false);
            return;
        }
        req.command = "PLGN_DWLD_FILE";
        this.callNative(req);
    }, createFile: function (req) {
        req.sessionReq = "Y";
        req.command = "PLGN_CRT_FILE";
        this.callNative(req);
    }, fileBrowser: function (req) {
        if (validate_filebrowser(req)) {
            req.command = "PLGN_BRWS_FILE";
            this.callNative(req);
        }
    }, getFileSize: function (req) {
        req.command = "PLGN_FILE_SIZE";
        this.callNative(req);
    }, fileRead: function (req) {
        req.command = "PLGN_READ_FILE";
        this.callNative(req)
    }, readFile: function (req) {
        req.command = "PLGN_READ_FILE";
        this.callNative(req)
    }, openFile: function (req) {
        req.command = "PLGN_OPN_FILE";
        this.callNative(req);
    },base64ToFile: function (req) {
        req.command = "PLGN_B64_TO_FILE";
        this.callNative(req);
    }, fileToBase64: function (req) {
        req.command = "PLGN_FILE_TO_B64";
        this.callNative(req);
    }, startGesture: function (req) {
        req.command = "PLGN_GSTR_START";
        this.callNative(req);
    }, stopGesture: function (req) {
        req.command = "PLGN_GSTR_STOP";
        this.callNative(req);
    }, startLocationTracking: function (req) {
        if (validate_startUpdatingLocation(req)) {
            if (apz.isNull(req.periodicity)) {
                req.periodicity = "none";
            }
            req.command = "PLGN_LOC_TRCK_START";
            this.callNative(req);
        }
    }, stopLocationTracking: function (req) {
        req.command = "PLGN_LOC_TRCK_STOP";
        this.callNative(req);
    }, getLocation: function (req) {
        req.command = "PLGN_GET_LOCATION";
        this.callNative(req);
    }, startIdleTimer: function (req) {
        req.gestureJson = {
            "singletap": "Y",
            "doubletap": "Y",
            "tripletap": "Y",
            "swipe": "Y",
            "longpress": "Y",
            "pinch": "Y"
        }
        req.command = "PLGN_IDLE_TMR_START";
        this.callNative(req);
    }, currentLocale: function (req) {
        this.apz.initNativeService(req);
        req.command = "PLGN_CRNT_LOCALE";
        try {
            var language = navigator.language; //en-US
            req.locale = language;
			req.text = language;
            req.status = true;
            req.keepAlive = false;
            Apz.nativeServiceCB(req);
        }
        catch (ex) {
            callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
        }
    },
    sendMail: function (req) {
        if (validate_mail(req)) {
            var internal = req.internal;
            if (internal.toUpperCase() == "Y") {
                var requestBody = {
                    "appzillonMailRequest": {
                        "emailid": req.recipientMailId,
                        "subject": req.subject,
                        "CC": req.ccIdList,
                        "body": req.body
                    }
                };
                var interfaceId = "";
                if (req.interfaceID == undefined || req.interfaceID == "") {
                    interfaceId = 'appzillonMailRequest';
                }
                else {
                    interfaceId = req.interfaceID;
                }
                var params = {};
                params.ifaceName = interfaceId;
                params.internal = internal;
                params.buildReq = "N";
                params.async = true;
                params.req = requestBody;
                params.callBack = req.callBack;
                this.apz.server.callServer(params);

            } else {
                req.opnType = "mail";
                req.command = "PLGN_SEND_MAIL";
                this.callNative(req);
            }
        }
    }, geofencing: function (req) {
        if (validate_geofencing(req)) {
            req.command = "PLGN_CALL_GEOFNCING";
            this.callNative(req);
        }
    }, locationSelector: function (req) {
        if (validate_areaSelector(req)) {
            req.command = "PLGN_LOC_SELECTR";
            this.callNative(req);
        }
    }, drivingDirection: function (req) {
        if (validater_drivingDirection(req)) {
            req.command = "PLGN_DRVNG_DIRCTN";
            this.callNative(req);
        }
    }, loadMap: function (req) {
        if (validate_map(req)) {
            req.command = "PLGN_LOAD_MAP";
            this.callNative(req);
        }
    }, videoRecording: function (req) {
        req.command = "PLGN_RECD_VIDEO";
        this.callNative(req);
    }, audio: function (req) {
        if (validate_audio(req)) {
            req.command = "PLGN_AUDIO";
            this.callNative(req);
        }
    }, sendNFC: function (req) {
        req.command = "PLGN_SND_NFC";
        this.callNative(req);
    }, receiveNFC: function (req) {
        req.command = "PLGN_RCV_NFC";
        this.callNative(req);
    }, stopNFC: function (req) {
        req.command = "PLGN_STOP_NFC";
        this.callNative(req);
    }, deleteNotification: function (req) {
        if (validate_deleteNotification(req)) {
            var ID = req.notificationID;
            var query = "DELETE FROM tb_notifications WHERE id = " + ID + ";";
            req.databaseName = "APPSDB";
            req.executeQuery = query;
            req.command = "PLGN_EXE_SQL";
            this.callNative(req);
        }
    }, getNotification: function (req) {
        var query = "SELECT * FROM tb_notifications;";
        req.databaseName = "APPSDB";
        req.executeQuery = query;
        req.command = "PLGN_EXE_SQL";
        this.callNative(req);
    }, updateNotification: function (req) {
        if (validate_updateNotification(req)) {
            var ID = req.notificationID;
            var readFlag = req.readFlag;
            readFlag = "'" + readFlag + "'";
            var query = "UPDATE tb_notifications SET readFlag = " + readFlag + " WHERE id = " + ID + ";";
            req.databaseName = "APPSDB";
            req.executeQuery = query;
            req.command = "PLGN_EXE_SQL";
            this.callNative(req);
        }
    }, showNotification: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, startNotificationListener: function (req) {
        req.command = "PLGN_NOT_LISTR_STRT";
        this.callNative(req);
    }, stopNotificationListener: function (req) {
        req.command = "PLGN_NOT_LISTR_STOP";
        this.callNative(req);
    }, //Ruthvik 18-04-17 modified by adding Start and Stop Orientation Listener
	startOrientationListener: function (req) {
        req.command = "PLGN_ORTN_START";
        this.callNative(req);
    }, stopOrientationListener: function (req) {
        req.command = "PLGN_ORTN_STOP";
        this.callNative(req);
    }, setOrientation: function (req) {
        req.command = "PLGN_SET_ORTN";
        this.callNative(req);
    }, hashPwd: function (req) {
        req.command = "PLGN_HASH_PWD";
        this.callNative(req);

    }, lockRotation: function (req) {
        req.command = "PLGN_LCK_ROTN";
        this.callNative(req);
    }, unlockRotation: function (req) {
        req.command = "PLGN_UNLCK_ROTN";
        this.callNative(req);
    }, signaturePad: function (req) {
        req.command = "PLGN_SIGN_PAD";
        this.callNative(req);
    }, launchApp: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, upgradeRequired: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, upgradeApp: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, subappDelete: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, smsSend: function (req) {
        req.command = "PLGN_SMS_SEND";
        this.callNative(req);
    }, startSMSListener: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, stopSMSListener: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, smsReceive: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, getInboxSMS: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, facebookLogin: function (req) {
        req.command = "PLGN_FACEBK_LOGIN";
        this.callNative(req);
    }, googleLogin: function (req) {
        req.command = "PLGN_GOOGLE_LOGIN";
        this.callNative(req);
    }, linkedinLogin: function (req) {
        req.command = "PLGN_LINKDIN_LOGIN";
        this.callNative(req);
    }, twitterLogin: function (req) {
        req.command = "PLGN_TWITTER_LOGIN";
        this.callNative(req);
    }, youtube: function (req) {
        req.command = "PLGN_YOUTUBE";
        this.callNative(req);
    }, getUserPrefs: function (req) {
        req.command = "PLGN_GET_USER_PREF";
        this.callNative(req);
    }, setPref: function (req) {
        req.command = "PLGN_SET_PREF";
        this.callNative(req);
    }, getPref: function (req) {
        req.command = "PLGN_GET_PREF";
        this.callNative(req);
    }, setUserPrefs: function (req) {
        req.command = "PLGN_SET_USER_PREF";
        this.callNative(req);
    }, executeSql: function (req) {
        if (validate_executeSql(req)) {
            req.command = "PLGN_EXE_SQL";
            this.callNative(req);
        }
    }, wipeOut: function (req) {
        req.command = "PLGN_WIPEOUT";
        this.callNative(req);
    }, openUrl: function (req) {
        req.command = "PLGN_OPEN_URL";
        this.callNative(req);
    }, nativeServiceExt: function (req) {
        req.command = "PLGN_NTV_EXT";
        this.callNative(req);
    }, showSplash: function (req) {
        req.command = "PLGN_SPLASH_SHOW";
        this.callNative(req);
    }, hideSplash: function (req) {
        req.command = "PLGN_SPLASH_HIDE";
        this.callNative(req);
    }, getIP: function (req) {
        req.command = "PLGN_GET_IP";
        this.callNative(req);
    }, getAppVersion: function (req) {
        if (validate_getAppVersion(req)) {
            req.command = "PLGN_APP_VERSION";
            this.callNative(req);
        }
    }, closeApplication: function (req) {
        req.command = "PLGN_CLS_APPCTN";
        this.callNative(req);
    }, //Ruthvik 11-04-17 Changed to latest Error Code 
	setRingtone: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-300", false);
    }, hideRefresh: function (req) {
        req.command = "PLGN_HIDE_REFRESH";
        this.callNative(req);
    }, multiviewOpen: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, multiviewClose: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, launchWebview: function (req) {
        req.command = "PLGN_LNCH_WEBVW";
        this.callNative(req);
    }, closeWebview: function (req) {
        req.command = "PLGN_CLS_WEBVW";
        this.callNative(req);
    }, updateWhiteList: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, updateWhiteListLocalStore: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, voice: function (req) {
        req.command = "PLGN_VOICE";
        this.callNative(req);
    }, detectEvents: function (req) {
        req.command = "PLGN_DET_EVE";
        this.callNative(req);
    }, getFileContent: function (req) {
        req.command = "PLGN_FILE_CONTENT";
        this.callNative(req);
    }, deleteFile: function (req) {
        req.command = "PLGN_DEL_FILE";
        this.callNative(req);
    }, getInstructions: function (req) {
        if (validate_getInstructions(req)) {
            req.command = "PLGN_GET_INST";
            this.callNative(req);
        }
    }, //Ruthvik 11-04-17 Changed to latest Error Code 
	printFile: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-231", false);
    }, //Ruthvik 11-04-17 Changed to latest Error Code 
	printScreen: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-236", false);
    }, captureNotes: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, saveReport: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
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
    }, nativeServerCall: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, startKeyboardListener: function (req) {
        req.command = "PLGN_KEYBD_LISTR_STRT";
        this.callNative(req);
    }, stopKeyboardListener: function (req) {
        req.command = "PLGN_KEYBD_LISTR_STOP";
        this.callNative(req);
    }, unzip: function (req) {
        if (validate_zip_unZip(req)) {
            req.command = "PLGN_UNZIP";
            this.callNative(req);
        }
    }, zip: function (req) {
        if (validate_zip_unZip(req)) {
            req.command = "PLGN_ZIP";
            this.callNative(req);
        }
    }, disableUIBounce: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, captureNotes: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, remoteDebug: function (req) {
        callNativeCBwithErrorCode(req, "APZ-CNT-022", false);
    }, disablePullDown: function (req) {
        req.refreshStatus = "disable";
        req.command = "PLGN_DIS_PLDN";
        this.callNative(req);
    }, enablePullDown: function (req) {
        req.refreshStatus = "enable";
        req.command = "PLGN_EN_PLDN";
        this.callNative(req);
    }, whatsApp: function (req) {
        req.command = "PLGN_OP_WP";
        this.callNative(req);
    }, vibrate: function (req) {
        req.command = "PLGN_VIBRATE";
        this.callNative(req);
    }
};
callNativeCBwithErrorCode = function (req, errorCode, keepAlive) {
    this.apz.initNativeService(req);
    req.errorCode = errorCode;
    req.keepAlive = keepAlive;
    Apz.nativeServiceCB(req);
};
////////////////VALIDATION//////////////
validate_camera = function (req) {
    var lcheck = false;
    var lactionpresent = apz.containsKey(req, "action");
    var lcroppresent = apz.containsKey(req, "crop");
    if (!lactionpresent) {
        callNativeCBwithErrorCode(req, "APZ-CNT-168", false);
    } else {
        var lactionnull = false;
        lactionval = false;
        if (!apz.isNull(req.action)) {
            lactionnull = true;
            if (req.action == "srcBase64" || req.action == "srcUrl" || req.action == "base64" || req.action == "save" || req.action == "base64_Save") {
                lactionval = true;
            }
        }
        var lhtmlidpresent = false;
        var lhtmlidnull = false;
        var lfilenamepresent = false;
        var lfilenamenull = false;
        if (lactionval == true) {
            if (req.action == 'srcBase64') {
                lfilenamepresent = true;
                lfilenamenull = true;
                var lhtmlidpresent = apz.containsKey(req, "elementId");
                if (lhtmlidpresent) {
                    if (!apz.isNull(req.elementId)) {
                        lhtmlidnull = true;
                    }
                }
            }
            else if (req.action == 'srcUrl') {
                var lhtmlidpresent = apz.containsKey(req, "elementId");
                if (lhtmlidpresent) {
                    if (!apz.isNull(req.elementId)) {
                        lhtmlidnull = true;
                    }
                }
                var lfilenamepresent = apz.containsKey(req, "fileName");
                if (lfilenamepresent) {
                    if (!apz.isNull(req.fileName)) {
                        lfilenamenull = true;
                    }
                }
            }
            else if (req.action == 'base64_Save') {
                var lhtmlidpresent = true;
                var lhtmlidnull = true;
                var lfilenamepresent = apz.containsKey(req, "fileName");
                if (lfilenamepresent) {
                    if (!apz.isNull(req.fileName)) {
                        lfilenamenull = true;
                    }
                }
            }
            else if (req.action == 'save') {
                lhtmlidpresent = true;
                lhtmlidnull = true;
                var lfilenamepresent = apz.containsKey(req, "fileName");
                if (lfilenamepresent) {
                    if (!apz.isNull(req.fileName)) {
                        lfilenamenull = true;
                    }
                }
            }
            else {
                lhtmlidpresent = true;
                lhtmlidnull = true;
                lfilenamepresent = true;
                lfilenamenull = true;
            }
        }
        if (!lactionnull) {
            callNativeCBwithErrorCode(req, "APZ-CNT-168", false);
        } else if (!lactionval) {
            callNativeCBwithErrorCode(req, "APZ-CNT-193", false);
        } else if (!lhtmlidpresent) {
            callNativeCBwithErrorCode(req, "APZ-CNT-170", false);
        } else if (!lhtmlidnull) {
            callNativeCBwithErrorCode(req, "APZ-CNT-170", false);
        } else if (!lfilenamepresent) {
            callNativeCBwithErrorCode(req, "APZ-CNT-169", false);
        } else if (!lfilenamenull) {
            callNativeCBwithErrorCode(req, "APZ-CNT-169", false);
        }
        else if (lcroppresent) {
            var cropString = req.crop;
            if (cropString == "Y")
            {
                if (isNaN(req.targetHeight) || isNaN(req.targetWidth)) {
                    callNativeCBwithErrorCode(req, "APZ-CNT-195", false);
                }
                else {
                    lcheck = true;
                }
            }
            else
                lcheck = true;
        }
        else {
            lcheck = true;
        }
    }
    return lcheck;

};

validate_remoteDebug = function (req) {
    var lcheck = false;
    var ldebugpresent = apz.containsKey(req, "debug");
    if (!ldebugpresent) {
        callNativeCBwithErrorCode(req, "APZ-CNT-194", false);
    } else {
        var ldebugnull = false;
        if (!apz.isNull(req.debug)) {
            ldebugnull = true;
        }
        if (!ldebugnull) {
            callNativeCBwithErrorCode(req, "APZ-CNT-194", false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;
};

validate_deleteNotification = function (req) {
    var lcheck = true;
    if (apz.isNull(req.notificationID)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-276", false);
    }
    return lcheck;
}

validate_updateNotification = function (req) {
    var lcheck = true;
    if (apz.isNull(req.notificationID)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-276", false);
    } else if (apz.isNull(req.readFlag)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-277", false);
    }
    return lcheck;
}

validate_acc_compass_start = function (req) {
    var lcheck = true;
    if (req.periodicity == "timed" && (apz.isNull(req.interval))) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-132", false);
    }
    return lcheck;
}

validate_startUpdatingLocation = function (req) {
    var lcheck = true;
    if (req.periodicity == "intervalBased") {
        if ((apz.isNull(req.timeInterval)) && (apz.isNull(req.distanceInterval))) {
            lcheck = false;
            callNativeCBwithErrorCode(req, "APZ-CNT-125", false);
        }
    }
    return lcheck;
}
validate_audio = function (req) {
    var lcheck = true;
    if (apz.isNull(req.action)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-168", false);
    } else if ((req.action == "play") || (req.action == "record")) {
        if (apz.isNull(req.location)) {
            lcheck = false;
            callNativeCBwithErrorCode(req, "APZ-CNT-278", false);
        } else if (apz.isNull(req.fileName)) {
            lcheck = false;
            callNativeCBwithErrorCode(req, "APZ-CNT-169", false);
        }
    }

    return lcheck;
}
validate_multiviewOpen = function (req) {
    var lcheck = true;
    if (apz.isNull(req.id)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-279", false);
    } else if (apz.isNull(req.location)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-278", false);
    } else if (apz.isNull(req.percentage)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-280", false);
    } else if (apz.isNull(req.targetView)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-281", false);
    } else if (apz.isNull(req.launchPage)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-282", false);
    }
    return lcheck;
}
validate_multiviewClose = function (req) {
    var lcheck = true;
    if (apz.isNull(req.id)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-279", false);
    }
    return lcheck;
}
validate_contactAdd = function (req) {
    var lcheck = true;
    if (apz.isNull(req.details.firstName)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-283", false);
    }
    return lcheck;
}
validate_calendar = function (req) {
    var lcheck = true;
    if (apz.isNull(req.action)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-168", false);
    } else if (apz.isNull(req.title)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-087", false);
    } else if (apz.isNull(req.startDate) || apz.isNull(req.startTime)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-VAL-008", false);
    } else if (apz.isNull(req.endDate) || apz.isNull(req.endTime)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-VAL-008", false);
    } else if (req.action == "edit") {
        if (apz.isNull(req.startDate) || apz.isNull(req.newStartTime)) {
            lcheck = false;
            callNativeCBwithErrorCode(req, "APZ-VAL-008", false);
        } else if (apz.isNull(req.endDate) || apz.isNull(req.newEndTime)) {
            lcheck = false;
            callNativeCBwithErrorCode(req, "APZ-VAL-008", false);
        }
    }
    return lcheck;
}

validate_map = function (req) {
    var lcheck = true;
    for (var i = 0; i < req.markerInfo.length; i++) {
        var lati = req.markerInfo[i].locationLatitude;
        var longi = req.markerInfo[i].locationLongitude;
        if (apz.isNull(lati)) {
            lcheck = false;
            callNativeCBwithErrorCode(req, "APZ-CNT-181", false);
        } else if (apz.isNull(longi)) {
            lcheck = false;
            callNativeCBwithErrorCode(req, "APZ-CNT-181", false);
        }
    }
    return lcheck;
}

validate_filebrowser = function (req) {
    var lcheck = true;
    if (apz.isNull(req.fileCategory)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-284", false);
    }
    return lcheck;
}

validate_executeSql = function (req) {
    var lcheck = true;
    if (apz.isNull(req.queryId)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-286", false);
    } else if (apz.isNull(req.databaseName)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-287", false);
    } else if (apz.isNull(req.executeQuery)) {
        lcheck = false;
        callNativeCBwithErrorCode(req, "APZ-CNT-288", false);
    }
    return lcheck;
}


validate_mail = function (jsonobject) {
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
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-172", false);
    } else if (!lrecipientmail) {
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-173", false);

    } else if (!lsubjectcheck) {
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-174", false);

    } else if (!lidcheck) {
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-175", false);

    } else {
        lcheck = true;
    }
    //    }
    return lcheck;
};
validate_geofencing = function (jsonobject) {
    var lcheck = false;
    var lregioncheck = apz.containsKey(jsonobject, "region");
    if (!lregioncheck) {
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-176", false);
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
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-178", false);
        } else if (!lradiuspresent) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-179", false);
        } else if (!lcoordpresent) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-181", false);
        }
        else if (!lregionchecknull) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-176", false);
        } else if (!lcountylistnull) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-178", false);
        } else if (!lradiusnull) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-179", false);
        } else if (!lcoordnull) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-181", false);
        } else if (!lregioncheckval) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-177", false);
        } else if (!lcountylistval) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-178", false);
        } else if (!lradiusval) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-180", false);
        } else if (!lcoordval) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-182", false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;
};
validate_getAppVersion = function (jsonobject) {
    var lcheck = false;
    if (apz.isNull(jsonobject)) {
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-183", false);
    } else {
        lcheck = true;
    }
    return lcheck;
};
validate_getInstructions = function (jsonobject) {
    var lcheck = false;
    var lappnamepresent = apz.containsKey(jsonobject, "appId");
    if (!lappnamepresent) {
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-183", false);
    }
    else {
        var lappname = false;
        if (!apz.isNull(jsonobject.appId)) {
            lappname = true;
        }
        if (!lappname) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-183", false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;
};

validate_subappDelete = function (jsonobject) {
    var lcheck = false;
    if (apz.isNull(jsonobject.appId)) {
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-183", false);
    } else {
        lcheck = true;
    }
    return lcheck;
};

validate_FileEncDesc = function (jsonobject) {
    var lcheck = false;
    var lKeyPresent = apz.containsKey(jsonobject, "key");
    var lsrcFilePathcheck = apz.containsKey(jsonobject, "srcFilePath");
    var ldestFilePathcheck = apz.containsKey(jsonobject, "destFilePath");
    if (!lKeyPresent) {
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-184", false);
    } else {
        var lkeycheck = false;
        if (!apz.isNull(jsonobject.key)) {
            lkeycheck = true;
        }
        if (!lkeycheck) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-184", false);
        } else if (!lsrcFilePathcheck) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-190", false);
        } else if (!ldestFilePathcheck) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-191", false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;
};
validate_areaSelector = function (jsonobject) {
    var lcheck = false;
    var lradiuspresent = apz.containsKey(jsonobject, "radius");
    var lnearbyplacespresent = apz.containsKey(jsonobject, "nearbyplaces");
    if (!lradiuspresent) {
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-179", false);
    } else if (!lnearbyplacespresent) {
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-192", false);
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
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-179", false);
        } else if (!lnearbyplaceschecknull) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-192", false);
        } else if (!lradiuscheckval) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-180", false);
        } else if (!lnearbyplacescheckval) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-092", false);
        } else {
            lcheck = true;
        }
    }
    return lcheck;

};
validater_drivingDirection = function (jsonobject) {
    var lcheck = false;
    var lfromLocationpresent = apz.containsKey(jsonobject, "fromLocation");
    var ltoLocationpresent = apz.containsKey(jsonobject, "toLocation");
    if (!ltoLocationpresent) {
        callNativeCBwithErrorCode(jsonobject, "APZ-CNT-188", false);
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
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-187", false);
        } else if (!ltoLocationnull) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-188", false);
        } else if (!ltoLocationval) {
            callNativeCBwithErrorCode(jsonobject, "APZ-CNT-189", false);
        }
        else {
            lcheck = true
        }
    }
    return lcheck;
};
validateinrange = function (min, number, max) {
    if (!isNaN(number) && !(number === Infinity) && !(number === "Infinity") && (number >= min) && (number <= max)) {
        return true;
    } else {
        return false;
    }
};
validateWhiteList = function (url) {
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
validate_zip_unZip = function (json) {
    var lsrcFilePathcheck = apz.isNull(json.srcFilePath);
    var ldestFilePathcheck = apz.isNull(json.destFilePath);
    var lcheck = true;
    if (lsrcFilePathcheck) {
        lcheck = false;
        callNativeCBwithErrorCode(json, "APZ-CNT-190", false);
    }
    else if (ldestFilePathcheck) {
        lcheck = false;
        callNativeCBwithErrorCode(json, "APZ-CNT-191", false);
    }
    return lcheck;
}