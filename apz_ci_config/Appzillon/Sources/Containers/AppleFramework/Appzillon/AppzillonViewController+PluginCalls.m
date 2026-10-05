//
//  AppzillonViewController+PluginCalls.m
//  Appzillon
//
//  Created by Pradeep Tiwari on 29/07/16.
//
//

#import "AppzillonViewController+PluginCalls.h"

@implementation AppzillonViewController (PluginCalls)
#pragma mark - RUNTIME PLUGIN OBJECT CREATION
- (BOOL)execute:(NSString*)pluginName json:(NSDictionary *)jsonDict wbView:(WKWebView *)webView
{
    BOOL isExecuted = NO;
    if (self.activePluginList==NULL) {
        self.activePluginList=[[NSMutableDictionary alloc]init];
    }
    
    NSArray *activePluginClassNames=[self.activePluginList allKeys];
    BOOL pluginIsRunning=[activePluginClassNames containsObject:pluginName];
    if (pluginIsRunning) {
        self.plugin=[self.activePluginList objectForKey:pluginName];
        self.plugin.delegate=self;
        if ([[jsonDict objectForKey:@"actionType"]isEqualToString:@"stop"]) {
            [self.plugin stopPlugin:jsonDict];
        }else{
            [self.plugin executePlugin:jsonDict];
        }
        isExecuted = YES;
    } else {
        if ((!pluginIsRunning)&&[[jsonDict objectForKey:@"actionType"]isEqualToString:@"stop"]) {
            self.plugin=[[NSClassFromString(pluginName)alloc] initPlugin:webView :jsonDict];
            if (self.plugin) {
                [self.plugin stopPlugin:jsonDict];
            }else{
                [self pluginNotSelected:jsonDict];
            }
            
        }else{
            self.plugin=[[NSClassFromString(pluginName)alloc] initPlugin:webView :jsonDict];
            if (self.plugin) {
                [self.activePluginList setObject:self.plugin forKey:pluginName];
                self.plugin.delegate=self;
                [self.plugin executePlugin:jsonDict];
                isExecuted = YES;
            }else{
                [self pluginNotSelected:jsonDict];
            }
        }
    }
    return isExecuted;
}

-(void)pluginNotSelected:(NSDictionary*)jsonDict{
    NSArray *returnResultkeys=[NSArray arrayWithObjects:ERROR_CODE,nil];
    NSArray *returnResult=[NSArray arrayWithObjects:[NSString stringWithFormat:@"%@",PLUGIN_NOT_SUPPORTED],nil];
    [MiscellaneousMethods jsLayerCall:webView :JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[jsonDict objectForKey:PLUGINID] :false :false :returnResultkeys :returnResult]];
}

-(NSString*)getClassNameForPlugin:(NSString*)command{
    NSString* className;
    if([command isEqualToString:@"PLGN_ACC_START"]||[command isEqualToString:@"PLGN_ACC_STOP"]){
        className=@"APZAccelerometer";
    }
    else if([command isEqualToString:@"PLGN_GEN_PDF"]){
        className=@"APZPDFGenerator";
    }
    else if([command isEqualToString:@"PLGN_AUG_START"]){
        className=@"APZAGReality";
    }
    else if([command isEqualToString:@"PLGN_SPEECH_TO_TEXT"]){
        className=@"APZSpeechToText";
    }
    else if([command isEqualToString:@"PLGN_AUG_RELOAD"]){
        className=@"AGReloadCoordinates";
    }
    else if([command isEqualToString:@"PLGN_SCN_BARCODE"]){
        className=@"APZBarcode";
    }
    else if([command isEqualToString:@"PLGN_GALRY_SCN_BARCODE"]){
        className=@"APZBarcodeGallery";
    }
    else if([command isEqualToString:@"PLGN_GEN_BARCODE"]){
        className=@"APZBarcodeGenerator";
    }
    else if([command isEqualToString:@"PLGN_BCON_START"]){
        className=@"iBeaconstart";
    }
    else if([command isEqualToString:@"PLGN_BCON_STOP"]){
        className=@"iBeaconstop";
    }
    else if([command isEqualToString:@"PLGN_BIO_AUTH"]){
        className=@"APZBioAuthentication";
    }
    else if([command isEqualToString:@"PLGN_CALL_NMB"]){
        className=@"MakePhoneCall";
    }
    else if([command isEqualToString:@"PLGN_SKYP_CL"]){
        className=@"APZCallSkype";
    }
    else if([command isEqualToString:@"PLGN_CRT_CAL_EVT"]||[command isEqualToString:@"PLGN_DLT_CAL_EVT"]||[command isEqualToString:@"PLGN_EDT_CAL_EVT"]){
        className=@"APZCalendar";
    }
    else if([command isEqualToString:@"PLGN_OPN_CAMERA"]){
        className=@"APZCamera";
    }
    else if([command isEqualToString:@"PLGN_DOC_SCNR"]){
        className=@"APZOcrView";
    }
    else if([command isEqualToString:@"PLGN_NATVE_SHARE"]){
        className=@"APZActivityShare";
    }
    else if([command isEqualToString:@"PLGN_CMP_START"]||[command isEqualToString:@"PLGN_CMP_STOP"]){
        className=@"APZCompass";
    }else if([command isEqualToString:@"PLGN_NW_STR_LIST"]){
        className=@"nwListStart";
    }else if ([command isEqualToString:@"PLGN_NW_STP_LIST"]){
        className=@"nwListStop";
    }
    else if([command isEqualToString:@"PLGN_ADD_CONT"]||[command isEqualToString:@"PLGN_DEL_CONT"]||[command isEqualToString:@"PLGN_EDIT_CONT"]||[command isEqualToString:@"PLGN_SRCH_CONT"]||[command isEqualToString:@"PLGN_FETCH_CONT"] || [command isEqualToString:@"PLGN_FETCH_ALL_CONT"]){
        className=@"APZContact";
    }
    else if([command isEqualToString:@"PLGN_DECRPT_DATA"]){
        className=@"APZDecrypt";
    }
    else if([command isEqualToString:@"PLGN_ENCRPT_DATA"]){
        className=@"APZEncrypt";
    }
    else if([command isEqualToString:@"PLGN_STR_SECURELY"]||[command isEqualToString:@"PLGN_RTR_SECURELY"]||[command isEqualToString:@"PLGN_STR_CRD_SECURELY"]||[command isEqualToString:@"PLGN_LOGIN"]){
        className=@"APZSecureStorage";
    }
    else if([command isEqualToString:@"PLGN_DECRPT_FILE"]){
        className=@"APZFileDecryption";
    }
    else if([command isEqualToString:@"PLGN_ENCRPT_FILE"]){
        className=@"APZFileEncryption";
    }
    else if([command isEqualToString:@"PLGN_DEV_DETAILS"]){
        className=@"APZDevice";
    }
    else if([command isEqualToString:@"PLGN_DEV_INFO"]){
        className=@"getDeviceInfo";
    }
    else if([command isEqualToString:@"PLGN_UPLD_FILE"]){
        className=@"APZFileUpload";
    }
    else if([command isEqualToString:@"PLGN_DWLD_FILE"]){
        className=@"APZFileDownload";
    }
    else if([command isEqualToString:@"PLGN_CRT_FILE"]){
        className=@"APZFileOperation";
    }
    else if([command isEqualToString:@"PLGN_BRWS_FILE"]){
        className=@"APZFileBrowser";
    }
    else if([command isEqualToString:@"PLGN_FILE_SIZE"]){
        className=@"APZFileSize";
    }
    else if([command isEqualToString:@"PLGN_OPN_FILE"]||[command isEqualToString:@"PLGN_PRINT_FILE"]){
        className=@"APZOpenFile";
    }
    else if([command isEqualToString:@"PLGN_B64_TO_FILE"]){
        className=@"APZBase64_File";
    }
    else if([command isEqualToString:@"PLGN_FILE_TO_B64"]){
        className=@"APZBase64_File";
    }
    else if([command isEqualToString:@"PLGN_GSTR_START"]){
        className=@"GestureSupportStart";
    }
    else if([command isEqualToString:@"PLGN_GSTR_STOP"]){
        className=@"GestureSupportStop";
    }
    else if([command isEqualToString:@"PLGN_LOC_TRCK_START"]||[command isEqualToString:@"PLGN_LOC_TRCK_STOP"]){
        className=@"APZGps";
    }
    else if([command isEqualToString:@"PLGN_GET_LOCATION"]){
        className=@"APZUserLocation";
    }
    else if([command isEqualToString:@"PLGN_IDLE_TMR_START"]){
        className=@"appIdleTimeOut";
    }
    else if([command isEqualToString:@"PLGN_CRNT_LOCALE"]){
        className=@"APZLocale";
    }
    else if([command isEqualToString:@"PLGN_SEND_MAIL"]){
        className=@"APZMail";
    }
    else if([command isEqualToString:@"PLGN_CALL_GEOFNCING"]){
        className=@"APZGeofencing";
    }
    else if([command isEqualToString:@"PLGN_LOC_SELECTR"]){
        className=@"APZSelectiveMap";
    }
    else if([command isEqualToString:@"PLGN_DRVNG_DIRCTN"]){
        className=@"APZDrivingDirection";
    }
    else if([command isEqualToString:@"PLGN_LOAD_MAP"]){
        className=@"APZMap";
    }
    else if([command isEqualToString:@"PLGN_RECD_VIDEO"]){
        className=@"APZVideo";
    }
    else if([command isEqualToString:@"PLGN_AUDIO"]){
        className=@"APZAudio";
    }
    else if([command isEqualToString:@"PLGN_DLT_NOTIF"]||[command isEqualToString:@"PLGN_GET_NOTIF"]||[command isEqualToString:@"PLGN_UPDT_NOTIF"]||[command isEqualToString:@"PLGN_EXE_SQL"]||[command isEqualToString:@"PLGN_UPDT_WLIST"]){
        className=@"APZStorage";
    }
    else if([command isEqualToString:@"PLGN_SET_ORTN"]){
        className=@"setOrientation";
    }
    else if([command isEqualToString:@"PLGN_LAUNCH_APP"]){
        className=@"launchApp";
    }
    else if([command isEqualToString:@"PLGN_UPGRD_REQ"]){
        className=@"upgradeRequired";
    }
    else if([command isEqualToString:@"PLGN_GET_UPDATE_ACT"]){
        className=@"getUpdateActionRequired";
    }
    else if([command isEqualToString:@"PLGN_UPGRD_APP"]){
        className=@"APZOTARefresh";
    }
    else if([command isEqualToString:@"PLGN_SUBAPP_DEL"]){
        className=@"APZChildWipeout";
    }
    else if([command isEqualToString:@"PLGN_HASH_PWD"]){
        className=@"generateOTP";
    } else if([command isEqualToString:@"PLGN_CHNG_PWD"]){
        className=@"chgPassword";
    }
    else if([command isEqualToString:@"PLGN_LCK_ROTN"]){
        className=@"lockRotation";
    }
    else if([command isEqualToString:@"PLGN_UNLCK_ROTN"]){
        className=@"unlockRotation";
    }
    else if([command isEqualToString:@"PLGN_SIGN_PAD"]){
        className=@"APZSignaturePad";
    }
    else if([command isEqualToString:@"PLGN_SMS_SEND"]){
        className=@"sendSMS";
    }
    else if([command isEqualToString:@"PLGN_FACEBK_LOGIN"]){
        className=@"APZFacebook";
    }
    else if([command isEqualToString:@"PLGN_GOOGLE_LOGIN"]){
        className=@"APZGooglePlus";
    }
    else if([command isEqualToString:@"PLGN_LINKDIN_LOGIN"]){
        className=@"APZLinkedin";
    }
    else if([command isEqualToString:@"PLGN_TWITTER_LOGIN"]){
        className=@"APZTwitter";
    }
    else if([command isEqualToString:@"PLGN_YOUTUBE"]){
        className=@"APZYouTube";
    }
    else if([command isEqualToString:@"PLGN_GET_USER_PREF"]){
        className=@"GetUserPrefs";
    }
    else if([command isEqualToString:@"PLGN_SET_USER_PREF"]){
        className=@"SetUserPrefs";
    }
    else if([command isEqualToString:@"PLGN_GET_PREF"]){
        className=@"getPref";
    }
    else if([command isEqualToString:@"PLGN_SET_PREF"]){
        className=@"setPref";
    }
    else if([command isEqualToString:@"PLGN_WIPEOUT"]){
        className=@"clearAppData";
    }
    else if([command isEqualToString:@"PLGN_OPEN_URL"]){
        className=@"APZBrowser";
    }
    else if([command isEqualToString:@"PLGN_NTV_EXT"]){
        className=@"nativeService";
    }
    else if([command isEqualToString:@"PLGN_KEYBD_LISTR_STRT"]){
        className=@"KBListStart";
    }
    else if([command isEqualToString:@"PLGN_KEYBD_LISTR_STOP"]){
        className=@"KBListStop";
    }
    else if([command isEqualToString:@"PLGN_NOT_LISTR_STRT"]){
        className=@"notifListStart";
    }
    else if([command isEqualToString:@"PLGN_NOT_LISTR_STOP"]){
        className=@"notifListStop";
    }
    else if([command isEqualToString:@"PLGN_SHORTCUT_LISTR_STRT"]){
        className=@"shortcutListStart";
    }
    else if([command isEqualToString:@"PLGN_SHORTCUT_LISTR_STOP"]){
        className=@"shortcutListStop";
    }
    else if([command isEqualToString:@"PLGN_SPLASH_SHOW"]){
        className=@"showSplashScreen";
    }
    else if([command isEqualToString:@"PLGN_SPLASH_HIDE"]){
        className=@"hideSplashScreen";
    }
    else if([command isEqualToString:@"PLGN_GET_IP"]){
        className=@"getIP";
    }
    else if([command isEqualToString:@"PLGN_APP_VERSION"]){
        className=@"appVersion";
    }
    else if([command isEqualToString:@"PLGN_MLTVW_OPEN"]){
        className=@"multiViewOpen";
    }
    else if([command isEqualToString:@"PLGN_MLTVW_CLOSE"]){
        className=@"multiviewClose";
    }
    else if([command isEqualToString:@"PLGN_MLTVW_RESIZE"]){
        className=@"resizeMultiview";
    }
    else if([command isEqualToString:@"PLGN_LNCH_WEBVW"]){
        className=@"APZWebview";
    }
    else if([command isEqualToString:@"PLGN_CLS_WEBVW"]){
        //Impliment the close webView
        className=@"APZWebview";
    }
    
    else if([command isEqualToString:@"PLGN_DET_EVE"]){
        className=@"controlEvents";
    }
    else if([command isEqualToString:@"PLGN_FILE_CONTENT"]){
        className=@"APZFileOperation";
    }
    else if([command isEqualToString:@"PLGN_DEL_FILE"]){
        className=@"APZFileOperation";
    }
    else if([command isEqualToString:@"PLGN_GET_INST"]){
        className=@"APZInstruction";
    }
    //    else if([command isEqualToString:@"PLGN_PRINT_SCR"]){
    //        className=@"printScreen";
    //    }
    else if([command isEqualToString:@"PLGN_SAV_REPORT"]){
        className=@"APZSaveReport";
    }
    else if([command isEqualToString:@"PLGN_N_SRVRCALL"]){
        className=@"callServerforInfra";
    }
    else if([command isEqualToString:@"PLGN_UNZIP"]){
        className=@"APZZip";
    }
    else if([command isEqualToString:@"PLGN_ZIP"]){
        className=@"APZZip";
    }
    else if([command isEqualToString:@"PLGN_DIS_BOUNCE"]){
        className=@"disableBounce";
    }
    else if([command isEqualToString:@"PLGN_CPT_NOTES"]){
        //        needs to be checked once
        //        className=@"stopAccelerometer";
    }
    else if([command isEqualToString:@"PLGN_RMT_DBUG"]){
        className=@"remoteDebug";
    }
    else if([command isEqualToString:@"PLGN_DIS_PLDN"]||[command isEqualToString:@"PLGN_EN_PLDN"]){
        className=@"APZPullDown";
    }
    else if([command isEqualToString:@"PLGN_OP_WP"]){
        className=@"APZWhatsapp";
    }
    else if([command isEqualToString:@"PLGN_VIBRATE"]){
        className=@"APZVibrate";
    }
    else if([command isEqualToString:@"PLGN_ORTN_START"]){
        className=@"startOrientList";
    }
    else if([command isEqualToString:@"PLGN_ORTN_STOP"]){
        className=@"stopOrientList";
    }
    else if([command isEqualToString:@"PLGN_REFSVRNONCE"]){
        className=@"getServerNonce";
    }
    else if([command isEqualToString:@"PLGN_DEEP_LNK"]){
        className=@"APZDeepLinking";
    }else if([command isEqualToString:@"PLGN_BIOMET_AVAIL"]){
        className=@"APZBiometricAvailability";
    }
    else if ([command isEqualToString:@"PLGN_BT_MTR_START"]||[command isEqualToString:@"PLGN_BT_MTR_STOP"]){
        className=@"APZBattery";
    }else if([command isEqualToString:@"PLGN_DETECT_DOC"]){
        className=@"APZVision";
    }else if([command isEqualToString:@"PLGN_PROC_IMG"]){
        className=@"APZProcessImage";
    }else if([command isEqualToString:@"PLGN_SLFI_CAP"]){
        className=@"APZFaceDetector";
    }else if([command isEqualToString:@"PLGN_N_CUST_HTTP_REQ"]){
        className=@"customHTTPRequest";
    }else if ([command isEqualToString:@"PLGN_SDK_CALLBACK"]){
        className=@"sendCallbackFromSDK";
    }else if ([command isEqualToString:@"PLGN_CLOSE_APP"]){
        className=@"closeAppzillonSDK";
    }else{
        NSLog(@"Invalid Command Passed!");
    }
    return className;
}
//-(void)missingPluginErrorMessage:(NSString *)plugin :(NSDictionary *)jsonDict{
//    NSString *errorCode;
//    if ([plugin isEqualToString:@"APZCalendar"]) {
//        errorCode=CALENDAR;
//    }
//    else if ([plugin isEqualToString:@"APZCamera"]) {
//        errorCode=CAMERA;
//    }
//    else if ([plugin isEqualToString:@"APZContact"]) {
//        errorCode=CONTACT;
//    }
//    else if ([plugin isEqualToString:@"APZDevice"]) {
//        errorCode=DEVICEINFO;
//    }
//    else if ([plugin isEqualToString:@"APZEncrypt"] ||[plugin isEqualToString:@"APZDecrypt"]) {
//        errorCode=CRYPTOGRAPHY;
//    }
//    else if ([plugin isEqualToString:@"APZLocale"]) {
//        errorCode=LOCALE;
//    }
//    else if ([plugin isEqualToString:@"APZMail"]) {
//        errorCode=EMAIL;
//    }
//    else if ([plugin isEqualToString:@"APZAirdrop"]) {
//        errorCode=AIRDROP;
//    }
//    else if ([plugin isEqualToString:@"APZStorage"]) {
//        errorCode=STORAGE;
//    }else if ([plugin isEqualToString:@"APZFileOperation"]) {
//        errorCode=FILEOPERATION;
//    }
//    else if ([plugin isEqualToString:@"APZBarcode"]) {
//        errorCode=BARCODE;
//    }
//    else if ([plugin isEqualToString:@"APZMap"]) {
//        errorCode=MAP;
//    }else if ([plugin isEqualToString:@"startGPS"]||[plugin isEqualToString:@"stopGPS"]) {
//        errorCode=GPS;
//    }
//    else if ([plugin isEqualToString:@"startAccelerometer"]||[plugin isEqualToString:@"startAccelerometer"]) {
//        errorCode=ACCELEROMETER;
//    }
//    else if ([plugin isEqualToString:@"startCompass"]||[plugin isEqualToString:@"stopCompass"]) {
//        errorCode=COMPASS;
//    }
//    else if ([plugin isEqualToString:@"Audio"]) {
//        errorCode=MEDIA;
//    }
//    else if ([plugin isEqualToString:@"startGeoFencing"]) {
//        errorCode=GEOFENCING;
//    }
//    else if ([plugin isEqualToString:@"APZSelectiveMap"]) {
//        errorCode=SELECTIVE_MAP;
//    }
//    else if ([plugin isEqualToString:@"APZDrivingDirection"]) {
//        errorCode=DRIVING_DIRECTION;
//    }
//    else if ([plugin isEqualToString:@"APZSignaturePad"]) {
//        errorCode=SIGNATURE_PAD;
//    }
//    else if ([plugin isEqualToString:@"APZFileEncryption"]) {
//        errorCode=FILE_ENCRYPTION;
//    }
//    else if ([plugin isEqualToString:@"APZFileDecryption"]) {
//        errorCode=FILE_DECRYPTION;
//    }
//    else if ([plugin isEqualToString:@"APZVibrate"]) {
//        errorCode=VIBRATION;
//    }
//    else if ([plugin isEqualToString:@"APZBioAuthentication"]) {
//        errorCode=BIO_AUTHENTICATION;
//    }
//    else if ([plugin isEqualToString:@"APZOTARefresh"]||[plugin isEqualToString:@"APZInstruction"]||[plugin isEqualToString:@"APZChildWipeout"]) {
//        errorCode=OTA_REFRESH;
//    }
//    else if ([plugin isEqualToString:@"APZAGReality"]) {
//        errorCode=AUGMENTED_REALITY;
//    }
//    else if ([plugin isEqualToString:@"iBeaconstart"]||[plugin isEqualToString:@"iBeaconstop"]) {
//        errorCode=iBEACON;
//    }
//    else if ([plugin isEqualToString:@"APZGooglePlus"]) {
//        errorCode=GOOGLEPLUS_SUPPORT;
//    }
//    else if ([plugin isEqualToString:@"APZFacebook"]) {
//        errorCode=FACEBOOK_SUPPORT;
//    }
//    else if ([plugin isEqualToString:@"APZLinkedin"]) {
//        errorCode=LINKEDIN_SUPPORT;
//    }
//    else if ([plugin isEqualToString:@"APZZip"]) {
//        errorCode=ZIP_UNZIP;
//    }
//    else if ([plugin isEqualToString:@"APZPullDown"]) {
//        errorCode=PULLDOWN;
//    }
//    else if ([plugin isEqualToString:@"APZBattery"]) {
//        errorCode=BATTERY;
//    }
//    else if ([plugin isEqualToString:@"APZVideo"]) {
//      errorCode=VDORECORDING;
//    }
//    else if ([plugin isEqualToString:@"APZCallSkype"]) {
//        errorCode=SKYPE_FUN;
//    }
//    else if ([plugin isEqualToString:@"APZWhatsapp"]) {
//        errorCode=WHATSAPP_FUN;
//    }
//    else if ([plugin isEqualToString:@"APZYouTube"]) {
//        errorCode=YOUTUBE;
//    }
//    else if ([plugin isEqualToString:@"APZTwitter"]) {
//        errorCode=TWITTER;
//    }
//    NSArray *errorkeys=[[NSArray alloc]initWithObjects:ERROR_CODE, nil];
//    NSArray *errorMsg=[[NSArray alloc] initWithObjects:errorCode, nil];
//    [self jsLayerCall:JSCALLBACKMEHTOD parameter:[self createResponseJSONString:[jsonDict objectForKey:PLUGINID] :false :true :errorkeys :errorMsg]];
//}

@end


