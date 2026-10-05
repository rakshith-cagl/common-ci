// Copyright (c) 2021 Appzillon. All rights reserved.
// swiftlint:disable all
import Foundation
extension AppzillonViewController {
    
    func execute(_ pluginName: String, jsonDict: Dictionary<AnyHashable,Any>, wbView: WKWebView) -> Bool {
        var isExecuted = false
        var pluginName = pluginName
        let swiftPluginsArray = ["APZCalendar","APZDeepLinking","APZContact","APZZip","APZLocale","APZCamera","APZSignaturePad",
                                 "APZFileOperation","APZBarcode","APZBarcodeGallery","APZUserLocation","APZBarcodeGenerator",
                                 "APZVibrate","APZActivityShare","APZDeleteSubApp","APZPullDown","APZAudio", "APZBiometricAvailability",
                                 "APZBioAuthentication","APZDevice","APZWebview","APZAppstoreReview","APZMail","APZFileSize","APZOpenFile",
                                 "APZFileEncryption","APZFileDecryption","APZDecrypt","APZEncrypt","APZBrowser",
                                 "APZBase64File","APZProcessImage","APZSecureStorage","APZFileBrowser",
                                 "APZStatusBarColor","APZSpeechToText","APZGps","APZGpsStatus","APZSpeechToText",
                                 "APZPDFGenerator","APZMap","APZBattery","APZStorage","APZAccessDeviceFiles", "APZClearWebCache", "APZModifyShortCutItems", "APZCheckNFCSupport", "APZCheckNotificationStatus", "APZVision", "APZFaceDetector", "APZGetSimDetails", "APZFileUpload", "APZFileDownload", "APZInstruction","APZRedirectToSettings"]
        if self.activePluginList == nil {
            self.activePluginList = NSMutableDictionary.init()
        }
        if swiftPluginsArray.contains(pluginName) {
            let strModuleName = Bundle.main.infoDictionary?["CFBundleName"] as? String ?? StringConstants.Generic.emptyString
            pluginName = strModuleName + "." + pluginName
        }
        let activePluginClassNames = self.activePluginList.allKeys as? [String] ?? []
        let pluginIsRunning = activePluginClassNames.contains(where: { $0 == pluginName })
        if pluginIsRunning, let plugin = self.activePluginList[pluginName] as? APZPlugin {
            self.plugin = plugin
            self.plugin.delegate = self
            if let action = jsonDict["actionType"] as? String, action == "stop" {
                self.plugin.stop(jsonDict)
            } else {
                self.plugin.execute(jsonDict)
            }
            isExecuted = true
        } else {
            if !pluginIsRunning, let action = jsonDict["actionType"] as? String, action == "stop" {
                let pluginClass = NSClassFromString(pluginName) as? APZPlugin.Type
                self.plugin = pluginClass?.init(plugin: webView, jsonDict)
                if self.plugin != nil {
                    self.plugin.stop(jsonDict)
                } else {
                    pluginNotSelected(jsonDict: jsonDict)
                }
            } else {
                let pluginClass = NSClassFromString(pluginName) as? APZPlugin.Type
                self.plugin = pluginClass?.init(plugin: webView , jsonDict)
                if self.plugin != nil, let plugin = self.plugin {
                    self.activePluginList.setObject(plugin, forKey: pluginName as NSCopying)
                    self.plugin.delegate = self
                    self.plugin.execute(jsonDict)
                    isExecuted = true
                } else {
                    pluginNotSelected(jsonDict: jsonDict)
                }
            }
        }
        return isExecuted
    }
    
    func pluginNotSelected(jsonDict: Dictionary<AnyHashable,Any>) {
        let pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: [StringConstants.Generic.errorCode],
                                                                    responseValues: ["APZ-CNT-022"])
        MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        
    }
    
   func getClassNameForPlugin(_ command: String) -> String {
        var className: String = StringConstants.Generic.emptyString
        
        if command == "PLGN_GEN_PDF" {
            className = "APZPDFGenerator";
        }
        else if command == "PLGN_SPEECH_TO_TEXT" {
            className = "APZSpeechToText";
        }
        else if command == "PLGN_SCN_BARCODE" {
            className = "APZBarcode";
        }
        else if command == "PLGN_GALRY_SCN_BARCODE" {
            className = "APZBarcodeGallery";
        }
        else if command == "PLGN_GEN_BARCODE" {
            className = "APZBarcodeGenerator";
        }
        else if command == "PLGN_BIO_AUTH" {
            className = "APZBioAuthentication";
        }
        else if command == "PLGN_CALL_NMB" {
            className = "MakePhoneCall";
        }
        else if(command == "PLGN_CRT_CAL_EVT" || command == "PLGN_DLT_CAL_EVT" || command == "PLGN_EDT_CAL_EVT") {
            className = "APZCalendar";
        }
        else if command == "PLGN_OPN_CAMERA" {
            className = "APZCamera";
        }
        else if command == "PLGN_NATVE_SHARE" {
            className = "APZActivityShare";
        }
        else if command == "PLGN_NW_STR_LIST" {
            className = "nwListStart";
        }
        else if command == "PLGN_NW_STP_LIST" {
            className = "nwListStop";
        }
       else if command == "PLGN_CHECK_NOTIFICATION_STATUS" {
           className = "APZCheckNotificationStatus";
       }
        else if(command == "PLGN_ADD_CONT" || command == "PLGN_DEL_CONT" || command == "PLGN_EDIT_CONT" ||
                command == "PLGN_SRCH_CONT" || command == "PLGN_FETCH_CONT" || command == "PLGN_FETCH_ALL_CONT") {
            className = "APZContact";
        }
        else if command == "PLGN_DECRPT_DATA" {
            className = "APZDecrypt";
        }
        else if command == "PLGN_ENCRPT_DATA" {
            className = "APZEncrypt";
        }
       else if command == "PLGN_GET_SIM_DETAILS" {
           className = "APZGetSimDetails";
       }
        else if(command == "PLGN_STR_SECURELY" || command == "PLGN_RTR_SECURELY"
                || command == "PLGN_STR_CRD_SECURELY" || command == "PLGN_LOGIN") {
            className = "APZSecureStorage";
        }
        else if command == "PLGN_DECRPT_FILE" {
            className = "APZFileDecryption";
        }
        else if command == "PLGN_ENCRPT_FILE" {
            className = "APZFileEncryption";
        }
        else if command == "PLGN_DEV_DETAILS" {
            className = "APZDevice";
        }
        else if command == "PLGN_DEV_INFO" {
            className = "getDeviceInfo";
        }
        else if command == "PLGN_UPLD_FILE" {
            className = "APZFileUpload";
        }
        else if command == "PLGN_DWLD_FILE" {
            className = "APZFileDownload";
        }
        else if command == "PLGN_CRT_FILE" {
            className = "APZFileOperation";
        }
        else if command == "PLGN_BRWS_FILE" {
            className = "APZFileBrowser";
        }
        else if command == "PLGN_FILE_SIZE" {
            className = "APZFileSize";
        }
        else if command == "PLGN_OPN_FILE" {
            className = "APZOpenFile";
        }
        else if command == "PLGN_B64_TO_FILE" {
            className = "APZBase64File";
        }
        else if command == "PLGN_FILE_TO_B64" {
            className = "APZBase64File";
        }
        else if command == "PLGN_GSTR_START" {
            className = "GestureSupportStart";
        }
        else if command == "PLGN_GSTR_STOP" {
            className = "GestureSupportStop";
        }
        else if(command == "PLGN_LOC_TRCK_START" || command == "PLGN_LOC_TRCK_STOP") {
            className = "APZGps";
        }
       else if command == "PLGN_REDIRECT_TO_SETTINGS" {
            className = "APZRedirectToSettings";
        }
        else if command == "PLGN_GET_LOCATION" {
            className = "APZUserLocation";
        }
        else if command == "PLGN_IDLE_TMR_START" {
            className = "appIdleTimeOut";
        }
        else if command == "PLGN_CRNT_LOCALE" {
            className = "APZLocale";
        }
        else if command == "PLGN_SEND_MAIL" {
            className = "APZMail";
        }
        else if command == "PLGN_LOAD_MAP" {
            className = "APZMap";
        }
        else if command == "PLGN_AUDIO" {
            className = "APZAudio";
        }
        else if(command == "PLGN_DLT_NOTIF" || command == "PLGN_GET_NOTIF" ||
                command == "PLGN_UPDT_NOTIF" || command == "PLGN_EXE_SQL" || command == "PLGN_UPDT_WLIST") {
            className = "APZStorage";
        }
        else if command == "PLGN_SET_ORTN" {
            className = "setOrientation";
        }
        else if command == "PLGN_UPGRD_REQ" {
            className = "upgradeRequired";
        }
        else if command == "PLGN_GET_UPDATE_ACT" {
            className = "getUpdateActionRequired";
        }
        else if command == "PLGN_UPGRD_APP" {
            className = "APZOTARefresh";
        }
        else if command == "PLGN_SUBAPP_DEL" {
            className = "APZDeleteSubApp";
        }
        else if command == "PLGN_HASH_PWD" {
            className = "generateOTP";
        } else if command == "PLGN_CHNG_PWD" {
            className = "chgPassword";
        }
        else if command == "PLGN_LCK_ROTN" {
            className = "lockRotation";
        }
        else if command == "PLGN_UNLCK_ROTN" {
            className = "unlockRotation";
        }
        else if command == "PLGN_SIGN_PAD" {
            className = "APZSignaturePad";
        }
        else if command == "PLGN_SMS_SEND" {
            className = "sendSMS";
        }
        else if command == "PLGN_GET_USER_PREF" {
            className = "GetUserPrefs";
        }
        else if command == "PLGN_SET_USER_PREF" {
            className = "SetUserPrefs";
        }
        else if command == "PLGN_GET_PREF" {
            className = "getPref";
        }
        else if command == "PLGN_SET_PREF" {
            className = "setPref";
        }
        else if command == "PLGN_WIPEOUT" {
            className = "clearAppData";
        }
        else if command == "PLGN_OPEN_URL" {
            className = "APZBrowser";
        }
        else if command == "PLGN_NTV_EXT" {
            className = "nativeService";
        }
        else if command == "PLGN_KEYBD_LISTR_STRT" {
            className = "KBListStart";
        }
        else if command == "PLGN_KEYBD_LISTR_STOP" {
            className = "KBListStop";
        }
        else if command == "PLGN_NOT_LISTR_STRT" {
            className = "notifListStart";
        }
        else if command == "PLGN_NOT_LISTR_STOP" {
            className = "notifListStop";
        }
        else if command == "PLGN_SHORTCUT_LISTR_STRT" {
            className = "shortcutListStart";
        }
        else if command == "PLGN_SHORTCUT_LISTR_STOP" {
            className = "shortcutListStop";
        }
       else if command == "PLGN_UNIVERSAL_LINK_LISTR_STRT" {
           className = "universalLinkListnerStart";
       }
       else if command == "PLGN_UNIVERSAL_LINK_LISTR_STOP" {
           className = "universalLinkListnerStop";
       }
        else if command == "PLGN_SPLASH_SHOW" {
            className = "showSplashScreen";
        }
        else if command == "PLGN_SPLASH_HIDE" {
            className = "hideSplashScreen";
        }
        else if command == "PLGN_GET_IP" {
            className = "getIP";
        }
        else if command == "PLGN_APP_VERSION" {
            className = "appVersion";
        }
        else if command == "PLGN_LNCH_WEBVW" {
            className = "APZWebview";
        }
        else if command == "PLGN_CLS_WEBVW" {
            //Impliment the close webView
            className = "APZWebview";
        }
        
        else if command == "PLGN_DET_EVE" {
            className = "controlEvents";
        }
        else if command == "PLGN_FILE_CONTENT" {
            className = "APZFileOperation";
        }
        else if command == "PLGN_DEL_FILE" {
            className = "APZFileOperation";
        }
        else if command == "PLGN_GET_INST" {
            className = "APZInstruction";
        }
        else if command == "PLGN_N_SRVRCALL" {
            className = "callServerforInfra";
        }
        else if command == "PLGN_UNZIP" {
            className = "APZZip";
        }
        else if command == "PLGN_ZIP" {
            className = "APZZip";
        }
        else if command == "PLGN_DIS_BOUNCE" {
            className = "disableBounce";
        }
        else if command == "PLGN_RMT_DBUG" {
            className = "remoteDebug";
        }
        else if command == "PLGN_DIS_PLDN" || command == "PLGN_EN_PLDN" {
            className = "APZPullDown";
        }
        else if command == "PLGN_VIBRATE" {
            className = "APZVibrate";
        }
        else if command == "PLGN_ORTN_START" {
            className = "startOrientList";
        }
        else if command == "PLGN_ORTN_STOP" {
            className = "stopOrientList";
        }
        else if command == "PLGN_REFSVRNONCE" {
            className = "getServerNonce";
        }
        else if command == "PLGN_DEEP_LNK" {
            className = "APZDeepLinking";
        }
        else if command == "PLGN_BIOMET_AVAIL" {
            className = "APZBiometricAvailability";
        }
        else if(command == "PLGN_BT_MTR_START" || command == "PLGN_BT_MTR_STOP") {
            className = "APZBattery";
        }
        else if command == "PLGN_DETECT_DOC" {
            className = "APZVision";
        }
        else if command == "PLGN_PROC_IMG" {
            className = "APZProcessImage";
        }
        else if command == "PLGN_SLFI_CAP" {
            className = "APZFaceDetector";
        }
        else if command == "PLGN_N_CUST_HTTP_REQ" {
            className = "customHTTPRequest";
        }
        else if command == "PLGN_CLOSE_APP" {
            className = "closeAppzillonSDK";
        }
        else if command == "PLGN_APP_TOKEN_SET" {
            className = "checkAppTokenSet";
        }
        else if command == "PLGN_IN_APP_REVIEW" {
            className = "APZAppstoreReview";
        }
        else if command == "PLGN_STATUS_BAR_COLOR" {
            className = "APZStatusBarColor";
        }
        else if command == "PLGN_GPS_STATUS" {
            className = "APZGpsStatus";
        }
        else if command == "PLGN_CLR_WEB_CACHE" {
           className = "APZClearWebCache";
       }
       else if command == "PLGN_IMPORT_DEVICE_FILES" {
          className = "APZAccessDeviceFiles";
       }
       else if command == "PLGN_MODIFY_SHORTCUTITEMS" {
           className = "APZModifyShortCutItems";
       }
       else if command == "PLGN_CHECK_NFCSUPPORT" {
           className = "APZCheckNFCSupport";
       }
       else if command == "PLGN_LOAD_NATIVE_STRINGS" {
           className = "loadLanguageStrings"
       }
       else if command == "PLGN_NTW_AVAILABILITY" {
           className = "checkNetworkAvailability"
       }
       else {
           print("Invalid Command Passed!");
       }
        return className
    }
}
// swiftlint:enable all
