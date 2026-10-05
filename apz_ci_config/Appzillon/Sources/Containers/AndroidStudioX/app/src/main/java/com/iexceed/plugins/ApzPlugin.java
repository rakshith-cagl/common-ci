package com.iexceed.plugins;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.multiview.JavaScriptBridge;

import org.json.JSONObject;


public abstract class ApzPlugin {
	
	public static final String APZ_PLUGIN_CAMERA = "PLGN_OPN_CAMERA";
        public static final String APZ_PLUGIN_AUTOCAPTURE = "PLGN_OPN_AUTOCAPTURE";
        public static final String APZ_PLUGIN_SELFIECAPTURE = "PLGN_OPN_SELFIECAPTURE";
        public static final String APZ_PLUGIN_IMG_PROC = "PLGN_IMG_PROC";
	public static final String APZ_PLUGIN_BEACON_START = "PLGN_BCON_START";
	public static final String APZ_PLUGIN_BEACON_STOP = "PLGN_BCON_STOP";
	public static final String APZ_PLUGIN_CALL = "PLGN_CALL_NMB";
	public static final String APZ_PLUGIN_BASE64TOFILE = "PLGN_B64_TO_FILE";
	public static final String APZ_PLUGIN_SCANFINGER = "PLGN_SCN_FINGER";
	//public static final String APZ_PLUGIN_MISSEDCALLS = "PLGN_MSD_CALLS";
	public static final String APZ_PLUGIN_GETCALLLOGS ="PLGN_GET_CALLLOGS";
	public static final String APZ_PLUGIN_CALLLISTENER = "PLGN_CALL_LISTNR";
	public static final String APZ_PLUGIN_OPENURL = "PLGN_OPEN_URL";
	public static final String APZ_PLUGIN_FILETOBASE64 = "PLGN_FILE_TO_B64";
	public static final String APZ_PLUGIN_GETFILESIZE = "PLGN_FILE_SIZE";
	public static final String APZ_PLUGIN_FILEDOWNLOADMGR = "PLGN_FILE_DOWNLOADMGR";
	public static final String APZ_PLUGIN_LAUNCHWEBVIEW = "PLGN_LNCH_WEBVW";
	public static final String APZ_PLUGIN_CLOSEWEBVIEW = "PLGN_CLS_WEBVW";
//	public static final String APZ_PLUGIN_INBOXSMS = "PLGN_GET_INBX_SMS";
	public static final String APZ_PLUGIN_RINGTONE = "PLGN_SET_RNGTN";
	public static final String APZ_PLUGIN_NOTIF_SHOW = "PLGN_SHW_NOTIF";
	public static final String APZ_PLUGIN_NOTIF_DELETE = "PLGN_DLT_NOTIF";
	public static final String APZ_PLUGIN_SEND_NFC = "PLGN_SND_NFC";
	public static final String APZ_PLUGIN_REC_NFC = "PLGN_RCV_NFC";
	public static final String APZ_PLUGIN_STOP_NFC = "PLGN_STOP_NFC";
	public static final String APZ_PLUGIN_CURRENTLOCATION = "PLGN_GET_LOCATION";
	public static final String APZ_PLUGIN_ZIP = "PLGN_ZIP";
	public static final String APZ_PLUGIN_UNZIP = "PLGN_UNZIP";
	
	public static final String PLGN_DEV_INFO = "PLGN_DEV_INFO";
	public static final String PLGN_GET_USER_PREF = "PLGN_GET_USER_PREF";
	public static final String PLGN_ENCRPT_DATA = "PLGN_ENCRPT_DATA";
	public static final String PLGN_DECRPT_DATA = "PLGN_DECRPT_DATA";
	public static final String PLGN_HASH_PWD = "PLGN_HASH_PWD";
	public static final String APZ_PLUGIN_HASH_SHA256 = "APZ_PLUGIN_HASH_SHA256";
	
	public static final String PLGN_SPLASH_SHOW = "PLGN_SPLASH_SHOW";
	public static final String PLGN_SPLASH_HIDE = "PLGN_SPLASH_HIDE";
	
	public static final String PLGN_NTV_EXT = "PLGN_NTV_EXT";
	
	// Natasha 3.2 plugins 
	public static final String APZ_PLUGIN_ACCE_START = "PLGN_ACC_START";
	public static final String APZ_PLUGIN_ACCE_STOP = "PLGN_ACC_STOP";
	public static final String APZ_PLUGIN_CAL_CREATE = "PLGN_CRT_CAL_EVT";
	public static final String APZ_PLUGIN_CAL_DELETE = "PLGN_DLT_CAL_EVT";
	public static final String APZ_PLUGIN_CAL_EDIT = "PLGN_EDT_CAL_EVT";
	public static final String APZ_PLUGIN_COMP_START = "PLGN_CMP_START";
	public static final String APZ_PLUGIN_COMP_STOP = "PLGN_CMP_STOP";
	public static final String APZ_PLUGIN_ENCRYPT_DATA = "PLGN_ENCRPT_DATA";
	public static final String APZ_PLUGIN_DECRYPT_DATA = "PLGN_DECRPT_DATA";
	public static final String APZ_PLUGIN_SQL = "PLGN_EXE_SQL";
	public static final String APZ_PLUGIN_DEVICEINFO = "PLGN_DEV_DETAILS";
	public static final String APZ_PLUGIN_BATRY_START = "PLGN_BT_MTR_START";
	public static final String APZ_PLUGIN_BATRY_STOP = "PLGN_BT_MTR_STOP";
	public static final String APZ_PLUGIN_ENCRPT_FILE = "PLGN_ENCRPT_FILE";
	public static final String APZ_PLUGIN_DECRPT_FILE = "PLGN_DECRPT_FILE";
	public static final String APZ_PLUGIN_GEOFENCING = "PLGN_CALL_GEOFNCING";
	public static final String APZ_PLUGIN_GPS_START = "PLGN_LOC_TRCK_START";
	public static final String APZ_PLUGIN_GPS_STOP = "PLGN_LOC_TRCK_STOP";
	public static final String APZ_PLUGIN_GESTURE_START = "PLGN_GSTR_START";
	public static final String APZ_PLUGIN_GESTURE_STOP = "PLGN_GSTR_STOP";
	public static final String APZ_PLUGIN_FILE_BROWSER = "PLGN_BRWS_FILE";
	public static final String APZ_PLUGIN_FILE_CONTENT = "PLGN_FILE_CONTENT";
	public static final String APZ_PLUGIN_FILE_CREATE = "PLGN_CRT_FILE";
	public static final String APZ_PLUGIN_FILE_DELETE = "PLGN_DEL_FILE";
	public static final String APZ_PLUGIN_FILE_OPEN = "PLGN_OPN_FILE";
	public static final String APZ_PLUGIN_MAIL = "PLGN_SEND_MAIL";
	public static final String APZ_PLUGIN_CONTACT_ADD = "PLGN_ADD_CONT";
	public static final String APZ_PLUGIN_CONTACT_DELETE = "PLGN_DEL_CONT";
	public static final String APZ_PLUGIN_CONTACT_SEARCH = "PLGN_SRCH_CONT";
	public static final String APZ_PLUGIN_CONTACT_EDIT = "PLGN_EDIT_CONT";
	public static final String APZ_PLUGIN_CONTACT_FETCH = "PLGN_FETCH_CONT";
	public static final String APZ_PLUGIN_DRVNG_DIRCTN = "PLGN_DRVNG_DIRCTN";
	public static final String APZ_PLUGIN_LOAD_MAP = "PLGN_LOAD_MAP";
	public static final String APZ_PLUGIN_LOCATN_SELECTR = "PLGN_LOC_SELECTR";
	public static final String APZ_PLUGIN_LOCALE = "PLGN_CRNT_LOCALE";
	public static final String APZ_PLUGIN_REPORT = "PLGN_SAV_REPORT";
	public static final String APZ_PLUGIN_SIGNATUREPAD = "PLGN_SIGN_PAD";
	public static final String APZ_PLUGIN_FACEBOOK = "PLGN_FACEBK_LOGIN";
	public static final String APZ_PLUGIN_TWITTER = "PLGN_TWITTER_LOGIN";
	public static final String APZ_PLUGIN_LINKEDIN = "PLGN_LINKDIN_LOGIN";
	public static final String APZ_PLUGIN_YOUTUBE = "PLGN_YOUTUBE";
	public static final String APZ_PLUGIN_GOOGLEPLUS = "PLGN_GOOGLE_LOGIN";
	public static final String APZ_PLUGIN_IDLETIMEOUT = "PLGN_IDLE_TMR_START";
	public static final String APZ_PLUGIN_LCK_ROTN = "PLGN_LCK_ROTN";
	public static final String APZ_PLUGIN_UNLCK_ROTN = "PLGN_UNLCK_ROTN";
	public static final String APZ_PLUGIN_ORIENTATION = "PLGN_SET_ORTN";
	public static final String APZ_PLUGIN_VIBRATE = "PLGN_VIBRATE";
	public static final String APZ_PLUGIN_VOICE = "PLGN_VOICE";
	public static final String APZ_PLUGIN_CONTROLEVENTS = "PLGN_DET_EVE";
	public static final String APZ_PLUGIN_WIPEOUT = "PLGN_WIPEOUT";
	public static final String APZ_PLUGIN_GET_IP = "PLGN_GET_IP";
	public static final String APZ_PLUGIN_APP_VERSION = "PLGN_APP_VERSION";
	public static final String APZ_PLUGIN_CLS_APPCTN = "PLGN_CLS_APPCTN";
	public static final String APZ_PLUGIN_MULTIVW_OPEN = "PLGN_MLTVW_OPEN";
	public static final String APZ_PLUGIN_MULTIVW_CLOSE = "PLGN_MLTVW_CLOSE";
	public static final String APZ_PLUGIN_MULTIVW_RESIZE = "PLGN_MLTVW_RESIZE";
	public static final String APZ_PLUGIN_CREATENOTE = "PLGN_CPT_NOTES";
	public static final String APZ_PLUGIN_PULDWN_ENABLE = "PLGN_EN_PLDN";
	public static final String APZ_PLUGIN_PULDWN_DISABLE = "PLGN_DIS_PLDN";
	public static final String APZ_PLUGIN_STARTAR = "PLGN_AUG_START";
	public static final String APZ_PLUGIN_RELOADAR = "PLGN_AUG_RELOAD";
	public static final String APZ_PLUGIN_AUTH = "PLGN_BIO_AUTH";
	public static final String APZ_PLUGIN_VIDEO = "PLGN_RECD_VIDEO";
	public static final String APZ_PLUGIN_AUDIO = "PLGN_AUDIO";
	public static final String APZ_PLUGIN_LAUNCHAPP = "PLGN_LAUNCH_APP";
	public static final String APZ_PLUGIN_DELETESUBAPP = "PLGN_SUBAPP_DEL";
	public static final String APZ_PLUGIN_GETINSTRUCTIONS = "PLGN_GET_INST";
	public static final String APZ_PLUGIN_UPGRADEREQUIRED = "PLGN_UPGRD_REQ";
	public static final String APZ_PLUGIN_UPGRD_APP = "PLGN_UPGRD_APP";
	public static final String APZ_PLUGIN_UPDATE_ACTION = "PLGN_UPDATE_REQ";
	public static final String APZ_PLUGIN_SETSETTINGS = "PLGN_SET_USER_PREF";
	public static final String APZ_PLUGIN_LOADSETTINGS = "PLGN_LOAD_STNGS";
	public static final String APZ_PLUGIN_SENDSMS = "PLGN_SMS_SEND";
	public static final String APZ_PLUGIN_SMS_LSTN_STRT = "PLGN_SMS_LSTN_START";
	public static final String APZ_PLUGIN_SMS_LSTN_STOP = "PLGN_SMS_LSTN_STOP";	
	public static final String APZ_PLUGIN_HIDEREFRESH = "PLGN_HIDE_REFRESH";
	public static final String APZ_PLUGIN_KEYBD_LSTN_STRT = "PLGN_KEYBD_LISTR_STRT";
	public static final String APZ_PLUGIN_KEYBD_LSTN_STOP = "PLGN_KEYBD_LISTR_STOP";
	public static final String APZ_PLUGIN_ORIENTATION_LISTN = "PLGN_ORTN_LISTR";
	public static final String APZ_PLUGIN_NOTIF_LSTN_STRT = "PLGN_NOT_LISTR_STRT";
	public static final String APZ_PLUGIN_NOTIF_LSTN_STOP = "PLGN_NOT_LISTR_STOP";
	public static final String APZ_PLUGIN_SET_PREF = "PLGN_SET_PREF";
	public static final String APZ_PLUGIN_GET_PREF = "PLGN_GET_PREF";
	public static final String APZ_APP_AVAILABILITY = "PLGN_APP_AVAILABILITY";
	public static final String APZ_PLUGIN_FILEUPLOAD = "PLGN_UPLD_FILE";
	public static final String APZ_PLUGIN_FILEDOWNLOAD = "PLGN_DWLD_FILE";
	public static final String APZ_PLUGIN_WHATSAPP = "PLGN_OP_WP";
	public static final String APZ_PLUGIN_FINGERPRINT = "PLGN_ENB_FINGERPRINT";

	
	//Abhishek 3.2 Changes
	public static final String APZ_PLUGIN_GET_SIM_INFO = "PLGN_GET_SIM_INFO";
//	public static final String APZ_PLUGIN_SENDSMS_BY_SID = "PLGN_SEND_SMS_BY_SID";
	public static final String APZ_PLUGIN_DOCUMENT_SCANNER = "PLGN_DOC_SCANNER";
	public static final String APZ_PLUGIN_READ_FILE = "PLGN_READ_FILE";
	public static final String APZ_PLUGIN_PRINT_SCR = "PLGN_PRINT_SCR";
	public static final String APZ_PLUGIN_PRINT_FILE = "PLGN_PRINT_FILE";
	public static final String APZ_PLUGIN_SKYPE = "PLGN_SKYP_CL";
	public static final String APZ_PLUGIN_DEEPLNKNG = "PLGN_DEEP_LNK";
	
	//Abhishek 3.4 Changes
	public static final String APZ_PLUGIN_TRACK_LOC = "PLGN_TRACK_LOC";
	public static final String APZ_PLUGIN_TEXT_TO_SPEECH = "PLGN_TEXT_TO_SPEECH";
	public static final String APZ_PLUGIN_NATIVE_SERVER_CALL = "PLGN_N_SRVRCALL";

	//Natasha 3.5 Changes
	public static final String APZ_PLUGIN_CREATE_PDF = "PLGN_GEN_PDF";
	public static final String APZ_PLUGIN_GEN_BARCODE = "PLGN_GEN_BARCODE";

	//Secure Data 3.5.1
	public static final String APZ_PLUGIN_SECURE = "PLGN_STORE_RETRIEVE_SECURE";


	//PreventReplayAttack and DataIntegrity changes in Android
	public static final String APZ_PLUGIN_RESETNONCE = "PLGN_REFSVRNONCE";

	public static final String APZ_BIOMET_AVAIL = "PLGN_BIOMET_AVAIL";
	public static final String APZ_CHANG_PCHANGE = "APZ_CHANG_PCHANGE";
	public static final String APZ_NATIVE_SHARE = "PLGN_NATVE_SHARE";
	
	// recent barcode and gallery developments
	public static final String PLGN_SCN_BARCODE = "PLGN_SCN_BARCODE";
	public static final String PLGN_GALRY__SCN_BARCODE = "PLGN_GALRY_SCN_BARCODE";

	public static final String APZ_NETWORK_MONITOR = "PLGN_NETWORK_MONITOR";
	public static final String APZ_NOTIFY_DOWNLOAD = "PLGN_NOTIFY_DOWNLOAD";
    public static final String APZ_CUSTOM_URL = "PLGN_CUSTOM_SERVERCALL";

    public static final String PLGN_SDK_CALLBACK = "PLGN_SDK_CALLBACK";
	public static final String APZ_PLUGIN_SHORT_LSTN_STRT = "PLGN_SHORT_LISTR_STRT";
	public static final String APZ_PLUGIN_SHORT_LSTN_STOP = "PLGN_SHORT_LISTR_STOP";
	public static final String PLGN_IS_APPTOKENSET = "PLGN_IS_APPTOKENSET";
	public static final String PLGN_SECURITY_UTIL = "PLGN_SECURITY_UTIL";
	public static final String PLGN_INAPP_REVIEW = "PLGN_INAPP_REVIEW";
	public static final String APZ_PLUGIN_STATUS_BAR_COLOR = "PLGN_STATUS_BAR_COLOR";
	public static final String PLGN_CHECK_GPS_STATUS = "PLGN_CHECK_GPS_STATUS";


	// request codes
	public static int APZ_REQ_DEFAULT_PERMISSIONS = 101;
	public static int APZ_REQ_CAMERA = 102;
	public static int APZ_REQ_CALL_PHONE = 103;
	public static int APZ_REQ_CALENDAR = 104;
	public static int APZ_REQ_LOCATION = 105;
	public static int APZ_REQ_FINGERPRINT = 106;
	public static int APZ_REQ_FINE_LOCATION = 107;
	public static final int GOOGLE_PLUS_SIGN_IN = 108;
	public static final int APZ_REQ_WRITE_STORAGE = 109;
	public static final int APZ_REQ_RECORD_AUDIO = 110;
    public static final int APZ_REQ_CONTACTS = 111;
	public static final int APZ_REQ_READ_SMS = 112;
    public static final int APZ_REQ_WRITE_SETTINGS = 113;
	public static final int APZ_REQ_BLUETOOTH = 114;

	
	
	public final static String properties = "USER_PREFS";
	
	/*For multiview*/
	public static JavaScriptBridge jsBridge;
	
	
	public static String BEACON_ID;
	
	
	protected String TAG = "ApzPlugin";

	protected String callbackId;
	protected WebView webView;
	protected ApzActivity activity;
	
	public static int debugLevel;

	public static boolean checkPermissionFlag = false;
	
	//Natasha Dawra 9/6/2017 Changes for Notification Registration
	//public static String refreshedToken = "";
		
	public ApzPlugin( WebView webView,ApzActivity activity) {
		this.webView = webView;
		this.activity = activity;
	}
	
	
	public abstract void execute(JSONObject params);
}


