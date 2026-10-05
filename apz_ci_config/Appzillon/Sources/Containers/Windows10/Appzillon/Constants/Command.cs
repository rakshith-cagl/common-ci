using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Constants
{
    class Command
    {
        #region AcceleroMeter
        public const string ACCELERO_START = "PLGN_ACC_START";
        public const string ACCELERO_STOP = "PLGN_ACC_STOP";
        #endregion
        #region App idle time out
        public const string APP_IDLE_TIME_OUT = "PLGN_IDLE_TMR_START";
        #endregion   
        #region Augment Reality
        public const string AUGMENT_START = "PLGN_AUG_START";
        public const string AUGMENT_STOP = "PLGN_AUG_RELOAD";
        #endregion

        #region Barcode
        public const string BARCODE = "PLGN_SCN_BARCODE";
        #endregion

        #region Common
        public const string TERMINATE_APP = "PLGN_CLS_APPCTN";
        public const string WIPE_OUT = "PLGN_WIPEOUT";
        public const string SET_SCREEN_MAP = "SET_SCREEN_MAP";
        #endregion

        #region Battery
        public const string BATTERY_START = "PLGN_BT_MTR_START";
        public const string BATTERY_STOP = "PLGN_BT_MTR_STOP";
        #endregion
        #region Beacon
        public const string BEACON_START = "PLGN_BCON_START";
        public const string BEACON_STOP = "PLGN_BCON_STOP";
        #endregion

        #region Biometric Authentication
        public const string FINGERPRINT_VERIFY = "PLGN_BIO_AUTH";
        #endregion

        #region Calendar
        public const string CALENDAR = "PLGN_CRT_CAL_EVT";
        public const string CALENDAR_DELETE = "PLGN_DLT_CAL_EVT";
        public const string CALENDAR_EDIT = "PLGN_EDT_CAL_EVT"; 
        #endregion

        #region Call
        public const string CALL = "PLGN_CALL_NMB";
        #endregion

        #region Camera
        public const string CAMERA = "PLGN_OPN_CAMERA";
        public const string VIDEO_RECORD = "PLGN_RECD_VIDEO";
        #endregion

        #region Compass
        public const string COMPASS_START = "PLGN_CMP_START";
        public const string COMPASS_STOP = "PLGN_CMP_STOP";
        #endregion

        #region Contacts
        public const string CONTACT_CREATE = "PLGN_ADD_CONT";
        public const string CONTACT_DELETE = "PLGN_DEL_CONT";
        public const string CONTACT_SEARCH = "PLGN_SRCH_CONT";
        public const string CONTACT_EDIT = "PLGN_EDIT_CONT";
        public const string CONTACT_FETCH = "PLGN_FETCH_CONT";
        #endregion

        #region Device
        public const string GET_IP = "PLGN_GET_IP";
        public const string DEVICE = "PLGN_DEV_DETAILS";
        public const string DEVICE_INFO = "PLGN_DEV_INFO";
        #endregion

        #region DeepLinking
        public const string DEEP_LINK = "PLGN_DEEP_LNK";
        #endregion

        #region Email
        public const string EMAIL = "PLGN_SEND_MAIL";
        #endregion

        #region Encryption and Decryption
        public const string ENCRYPT_FILE = "PLGN_ENCRPT_FILE";
        public const string DECRYPT_FILE = "PLGN_DECRPT_FILE";
        public const string ENCRYPT_STRING = "PLGN_ENCRPT_DATA";
        public const string DECRYPT_STRING = "PLGN_DECRPT_DATA";
        #endregion

        #region Control Events
        public const string EVENTS = "PLGN_DET_EVE";
        #endregion

        #region File Operation
        public const string BASE64_TO_FILE = "PLGN_B64_TO_FILE";
        public const string BASE64_TO_PDF = "PLGN_B64_TO_PDF";
        public const string FILE_BROWSER = "PLGN_BRWS_FILE";
        public const string FILE_TO_BASE64 = "PLGN_FILE_TO_B64";
        public const string FILE_UPLOAD = "PLGN_UPLD_FILE";
        public const string FILE_DOWNLOAD = "PLGN_DWLD_FILE";
        public const string FILE_UPLOAD_WS = "FILE_UPLOAD_WS"; // WS - Without Session
        public const string FILE_DOWNLOAD_WS = "FILE_DOWNLOAD_WS";
        public const string CREATE_FILE = "PLGN_CRT_FILE";
        public const string DELETE_FILE = "PLGN_DEL_FILE";
        public const string FILE_CONTENT = "PLGN_FILE_CONTENT";
        public const string GET_FILE_SIZE = "PLGN_FILE_SIZE";
        public const string PLGN_OPN_FILE = "PLGN_OPN_FILE";
        public const string PLGN_READ_FILE = "PLGN_READ_FILE";
        #endregion

        #region Get Instructions
        public const string GET_INSTRUCTION = "PLGN_GET_INST";
        #endregion

        #region GPS
        public const string GPS_START = "PLGN_LOC_TRCK_START";
        public const string GPS_STOP = "PLGN_LOC_TRCK_STOP";
        #endregion
        public const string PLGN_GET_LOCATION = "PLGN_GET_LOCATION";

        #region Keyboard visibility
        //#if KEYBOARD
        public const string PLGN_KEYBD_LISTR_STP = "PLGN_KEYBD_LISTR_STOP";
        public const string PLGN_LTN_KEY = "PLGN_KEYBD_LISTR_STRT";
        public const string SHOW_KEYBOARD = "PLGN_SHW_KEY";
        public const string HIDE_KEYBOARD = "PLGN_HID_KEY";
        //#endif
        #endregion
        #region Map
        public const string MAP_LOCATE = "PLGN_LOAD_MAP";
        public const string MAP_DRIVE_DIRECTION = "PLGN_DRVNG_DIRCTN";
        public const string MAP_AREA_SELECTOR = "PLGN_LOC_SELECTR";
        public const string GEO_FENCE = "PLGN_CALL_GEOFNCING";
        #endregion

        #region Media
        public const string AUDIO = "PLGN_AUDIO";
        public const string VIDEO_PLAY = "VIDEO_PLAY";
        #endregion

        #region Native service
        public const string NATIVE_SERVICE_EXT = "PLGN_NTV_EXT";
        #endregion

        #region NFC
        //#if NFC
        public const string SEND_NFC = "PLGN_SND_NFC";
        public const string RECEIVE_NFC = "PLGN_RCV_NFC";
        public const string STOP_NFC = "PLGN_STOP_NFC";
        //#endif
        #endregion

        #region Notification
        //#if NFC
        public const string UPDATE_NOTIFICATION = "PLGN_UPDT_NOTIF";
        public const string DELETE_NOTIFICATION = "PLGN_DLT_NOTIF";
        public const string SHOW_NOTIFICATION = "PLGN_SHW_NOTIF";
        public const string GET_NOTIFICATION = "PLGN_GET_NOTIF";
        public const string PLGN_LTN_NTF = "PLGN_NOT_LISTR_STRT";
        public const string PLGN_LTN_NTF_STP = "PLGN_NOT_LISTR_STOP";
        //#endif
        #endregion

        #region Orientation and Rotation
        public const string ORIENTATION_SET = "PLGN_SET_ORTN";
        public const string ROTATION_LOCK = "PLGN_LCK_ROTN";
        public const string ROTATION_UNLOCK = "PLGN_UNLCK_ROTN";
        public const string PLGN_SET_ORTN_LSTR = "PLGN_ORTN_LISTR";

        #endregion

        #region OTA
        public const string UPGRADE_REQ = "PLGN_UPGRD_REQ";
        public const string UPGRADE_APP = "PLGN_UPGRD_APP";
        public const string LAUNCH_CHILD_APP = "PLGN_LAUNCH_APP";
        public const string GET_CHILD_APP_VERSION = "PLGN_CURR_VERSN";
        public const string DELETE_CHILD_APP = "PLGN_SUBAPP_DEL";
        #endregion

        #region OTP
        public const string GENERATE_OTP = "PLGN_HASH_PWD";
        #endregion

        #region Pulldown
        public const string STOP_REFRESH = "STOP_REFRESH";
        public const string APP_REFRESH = "APP_REFRESH";
        #endregion

        #region Setttings
        public const string GET_SETTING = "PLGN_GET_PREF";
        public const string GET_SETTING_S = "PLGN_GET_USER_PREF";
        public const string SET_SETTING = "PLGN_SET_PREF";
        public const string SET_SETTING_S = "PLGN_SET_STNGS";

        public const string LOAD_SETTING_S = "PLGN_LOAD_STNGS";
        public const string SAVE_SETTING_S = "PLGN_SAVE_STNGS";
        #endregion

        #region Signature Pad
        public const string SIGNATURE_PAD = "PLGN_SIGN_PAD";
        #endregion

        #region SMS
        public const string SMS_SEND = "PLGN_SMS_SEND";
        #endregion

        #region Social Media
        public const string GOOGLE_LOGIN = "PLGN_GOOGLE_LOGIN";
        public const string FACEBOOK_LOGIN = "PLGN_FACEBK_LOGIN";
        public const string LINKEDIN_LOGIN = "PLGN_LINKDIN_LOGIN";
        public const string TWITTER_LOGIN = "PLGN_TWITTER_LOGIN";
        public const string PLGN_OP_WP = "PLGN_OP_WP";
        #endregion

        #region Skype
       public const string SKYPE = "PLGN_SKYP_CL";
        #endregion

        #region Splash
        public const string SHOW_SPLASH = "PLGN_SPLASH_SHOW";
        public const string HIDE_SPLASH = "PLGN_SPLASH_HIDE";
        #endregion

        #region Sqlite
        public const string EXECUTE_SQL = "PLGN_EXE_SQL";
        #endregion

        #region Vibrate
        public const string VIBRATE_DEVICE = "PLGN_VIBRATE";
        #endregion
        #region View
        public const string WEBVIEW_LAUNCH = "PLGN_LNCH_WEBVW";
        public const string WEBVIEW_CLOSE = "PLGN_CLS_WEBVW";
        public const string MULTIVIEW_LAUNCH = "PLGN_MLTVW_OPEN";
        public const string MULTIVIEW_CLOSE = "PLGN_MLTVW_CLOSE";
        #endregion

        #region Voice Support
        public const string VOICE = "PLGN_VOICE";
        #endregion

        #region Url
        //#if
        internal const string UPDATE_WHITELIST = "PLGN_UPDT_WLIST";
        internal const string VALIDATE_WHITELIST = "VALIDATE_WHITELIST";
        internal const string UPDATE_WHITELIST_TO_LOCAL = "UPDATE_WHITELIST_TO_LOCAL";
        internal const string URL_OPEN = "PLGN_OPEN_URL";
        //#endif
        #endregion

        #region Utils
        public const string GET_APPVERSION = "PLGN_APP_VERSION";
        #endregion

        #region ZIP & UnZip
        public const string ZIP = "PLGN_ZIP";
        public const string UNZIP = "PLGN_UNZIP";
        #endregion

        //        public const string SCREENID = "SCREENID"; //back pressed
        #region Native Extended Support
        public const string DEBUG = "DEBUG";
        public const string ALERT = "ALERT";
        #endregion
    }
}
