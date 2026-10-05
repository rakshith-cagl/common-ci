using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Constants
{
    class JsonKey
    {
        //Response Json
        #region Request & Response Json Key
        internal const string ID = "id";
        internal const string TEXT = "text";
        internal const string COMMAND = "command";
        internal const string STATUS = "status";
        internal const string KEEP_ALIVE = "keepAlive";
        //internal const string BODY = "body";
        internal const string ERROR_CODE = "errorCode";
        internal const string SUCCESS_MESSAGE = "successMessage";
        internal const string SUCCESS = "success";
        internal const int Timeout = 5;
        #endregion

        #region AcceleroMeter constant
#if ACCELEROMETER
        internal const string X_CORD = "xCord";
        internal const string Y_CORD = "yCord";
        internal const string Z_CORD = "zCord";
#endif
        #endregion

        #region Audio
#if MEDIA

#endif
        #endregion

        #region BarCode
#if BARCODE
        internal const string DECODED_DATA = "decodedData";
#endif
        #endregion

        #region Battery Monitor
        internal const string STATE = "state";
        internal const string LEVEL = "level";
        internal const string THRESHOLD = "threshold";
        #endregion

        #region Beacon
#if BEACON
internal const string UU_ID = "uuid";
#endif
        #endregion

        #region BioMetric Auth
#if BIOMETRIC
        internal const string FINGERPRINT_VERIFICATION_IS_AVAILABLE="Fingerprint verification is available.";
        internal const string USER_MESSAGE = "Please provide fingerprint verification.";
        internal const string FINGERPRINT_VERIFIED = "Fingerprint verified.";
#endif
        #endregion

        #region Camera
#if CAMERA
        internal const string SAVE_BASE64 = "base64_Save";
        internal const string ENCODING = "encoding";
        internal const string JPEG = "jpeg";
        internal const string PNG = "png";
        internal const string COMPRESSION_LEVEL = "compressionLevel";
#endif
        #endregion

        #region Crypto
#if CRYPTO
        internal const string STRING_TO_ENCRYPT = "stringToEncrypt";
        internal const string ENCRYPTED_STRING = "encryptedString";
        internal const string STRING_TO_DECRYPT = "stringToDecrypt";
        internal const string DECRYPTED_STRING = "decryptedString";
#endif
        #endregion

        #region File browser
        internal const string FILE_CATEGORY = "fileCategory";
        internal const string FILTER = "filter";
        internal const string OPEN_FILE = "openFile";
        #endregion

        #region File Operation
#if FILEOPERATION
        internal const string WINDOWS10_FILE_UPLOAD = "Windows10FileUpload";
        internal const string APPZILLON_UPLOAD_FILE_RESPONSE = "appzillonUploadFileResponse";
        internal const string APPZILLON_WS_RESPONSE = "appzillonUploadFileWSResponse";
        internal const string WS = "WS";
        internal const string FILE = "file";
        //upload
        internal const string DESTINATION = "destination";
        internal const string FILE_OVERRIDE = "fileOverride";
        internal const string OVER_WRITE = "overWrite";
        //download
        internal const string DATA_STRING = "dataString";
        internal const string DESTINATION_PATH = "destinationPath";
        internal const string FILE_TYPE = "fileType";
        //Create
        internal const string FILE_CONTENT = "fileContent";
        //base64
        internal const string ENCODED_FILE = "encodedFile";


#endif
        #endregion

        #region Device information

        internal const string DEVICES = "devices";
        internal const string OS_NAME = "osName";
        internal const string OS_VERSION = "osVersion";
        internal const string LOCK_ROTATION = "lockRotation";
        internal const string DEV_TYPE = "deviceType";
        internal const string DEVICE_GROUP = "deviceGroup";
        internal const string SCREEN_PPI = "screenPpi";
        internal const string SCREEN_RESOLUTION = "screenresolution";
        internal const string CONNECTION_TYPE = "connectionType";
        internal const string BATTERY_STATUS = "batteryStatus";
        internal const string DEVICE_ID = "DEVICEID";
        internal const string PLUGIN_OS = "PLUGIN.OS";
        internal const string SCREEN_SIZE = "screenSize";
        internal const string DEVICE_TYPE = "DEVICETYPE";
        internal const string OTA_REQUIRED = "OTAREQUIRED";
        internal const string HASHKEY_1 = "HASHKEY1";
        internal const string HASHKEY_2 = "HASHKEY2";
        #endregion

        #region Common
#if (FACEBOOK || TWITTER || GOOGLE || LINKEDIN)
        internal const string PICTURE_URL = "pictureURL";
        internal const string GENDER = "gender";
        internal const string LOCALE = "locale";
        internal const string IS_VERIFIED = "isVerified";
#endif
        internal const string TIME = "time";
        internal const string EVENT = "event";
        internal const string CONTENT = "content";
        internal const string APP_VERSION = "appversion";
        internal const string ORIENTATION = "orientation";
        internal const string OS = "os";
        internal const string IP = "ip";
        internal const string WIDTH = "width";
        internal const string HEIGHT = "height";
        internal const string FILE_NAME = "fileName";
        internal const string FILE_EXT = "extension";
        internal const string TYPE = "type";
        internal const string OVERWRITE = "overwrite";
        internal const string WINDOWS_10 = "windows10";
        internal const string LOGIN = "login";
        internal const string NULL = "null";
        internal const string ENCODED_IMAGE = "encodedImage";
        internal const string SCREEN_ID = "screenId";
        internal const string REQUEST_KEY = "requestKey";
        internal const string SESSION_ID = "sessionId";
        internal const string APP_ID = "appId";
        internal const string USER_ID = "userId";
        internal const string SERVER_TOKEN = "SERVERTOKEN";
        internal const string SERVER_URL = "SERVERURL";
        internal const string APPZILLON_HEADER = "appzillonHeader";
        internal const string APPZILLON_BODY = "appzillonBody";
        internal const string APPZILLON_ERROR = "appzillonErrors";
        internal const string APPZILLON_SAFE = "appzillonSafe";
        internal const string APPZILLON_QOP = "appzillonQop";
        internal const string POST = "POST";
        internal const string EMAIL = "email";
        internal const string NAME = "name";
        internal const string BASE64 = "base64";
        internal const string FILE_PATH = "filePath";
        internal const string SAVE = "save";
        internal const string FIRST_NAME = "firstName";
        internal const string LAST_NAME = "lastName";
        internal const string SRC_FILEPATH = "srcFilePath";
        internal const string DEST_FILEPATH = "destFilePath";
#if (CRYPTO || CRYPTOFILE)
        internal const string KEY = "key";
#endif
        internal const string LOCATION = "location";
        //#if (AUDIO || CAMERA || NFC)
        internal const string ACTION = "action";
        //#endif
#if (AUDIO || FILEOPERATION)
      
#endif
#if (ACCELEROMETER || COMPASS || GPS)
        internal const string PERIODICITY = "periodicity";
        internal const string INTERVAL = "interval";
        internal const string NONE = "none";
        internal const string ON_CHANGE = "onChange";
        internal const string TIMED = "timed";
#endif
        #endregion

        #region Comapss
#if COMPASS
        internal const string MAGNETIC_NORTH = "magneticNorth";
        internal const string TRUE_NORTH = "trueNorth";
        internal const string COMPASS_RESULT = "compRes";
#endif
        #endregion

        #region Calendar constant
#if CALENDAR
        internal const string CALENDAR_ACTION = "action";
        internal const string TITLE = "title";
        internal const string ALARM = "alarm";
        internal const string START_DATE = "startDate";
        internal const string END_DATE = "endDate";
        internal const string START_TIME = "startTime";
        internal const string END_TIME = "endTime";
        internal const string PRIORITY = "priority";
        internal const string SUMMERY = "summary";
        internal const string RECCURENCE = "recurrence";
        internal const string RECCURENCE_END_DATE = "recurrenceEndDate";
        internal const string VENUE = "location";
#endif
        #endregion

        #region Contacts constant
        //#if CONTACTS
        internal const string DETAILS = "details";
        internal const string PHONE_HOME = "phoneHome";
        internal const string PHONE_WORK = "phoneWork";
        internal const string PHONE_MOBILE = "phoneMobile";
        internal const string ADDRESS = "address";
        internal const string WEBSITE = "website";
        internal const string MAIL = "mail";
        internal const string SEARCH_CRITERIA = "searchCriteria";
        internal const string DELETE_CRITERIA = "deleteCriteria";
        //#endif
        #endregion
        /*
   #region Device information
    internal const string DEVICES = "devices";
    internal const string OS_NAME = "osName";
    internal const string OS_VERSION = "osVersion";
    internal const string DEV_TYPE = "devType";
    internal const string SCREEN_RESOLUTION = "screenResolution";
    internal const string CONNECTION_TYPE = "connectionType";
    internal const string BATTERY_STATUS = "batteryStatus";
    internal const string DEVICE_ID = "DEVICEID";
    internal const string PLUGIN_OS = "PLUGIN.OS";
    internal const string SCREEN_SIZE = "SCREENSIZE";
    internal const string DEVICE_TYPE = "DEVICETYPE";
    internal const string OTA_REQUIRED = "OTAREQUIRED";
    internal const string HASHKEY_1 = "HASHKEY1";
    internal const string HASHKEY_2 = "HASHKEY2";
    #endregion
*/
        #region Events
        internal const string ON = "on";
        internal const string OFF = "off";
        internal const string BATTERY_EVENT = "batteryEvent";
        internal const string BACK_BUTTON_EVENT = "backButtonEvent";
        internal const string APP_PAUSED_EVENT = "appPausedEvent";
        internal const string APP_RESUMED_EVENT = "appResumedEvent";
        internal const string APP_CALLSTART_EVENT = "appCallStartEvent";
        internal const string APP_CALLEND_EVENT = "appCallEndEvent";
        internal const string ALL_EVENTS = "allEvents";
        #endregion

        #region Geofencing constants
#if MAP
        internal const string REGION = "region";
        internal const string IN_REGION = "inRegion";
        internal const string COUNTRY = "COUNTRY"; 
        internal const string COUNTRY_LIST = "CountryList";
        internal const string COORDINATES = "coordinates";
        internal const string RADIUS = "radius";
#endif
        #endregion

        #region Get Insructions
        internal const string APPZILLON_GET_APP_MASTER_DETAILS = "appzillonGetAppMasterDetails";
        internal const string PRELOGIN = "preLogin";
        internal const string DEVICE = "deviceId";
        internal const string INTERFACE_ID = "interfaceId";
        internal const string APPZILLON_APP_MASTER_REQUEST = "appzillonAppMasterRequest";
        #endregion

        #region Gps
#if GPS
        internal const string INTERVAL_BASED = "intervalBased";
        internal const string TIME_INTERVAL = "timeInterval";
        internal const string GPS_RES = "gpsRes";
        internal const string LATITUDE = "latitude";
        internal const string LONGITUDE = "longitude";
        internal const string ALTITUDE = "altitude";
        internal const string ACCURACY = "accuracy";
        internal const string ALTITUDE_ACCURACY = "altitudeAccuracy";
        internal const string HEADING = "heading";
        internal const string SPEED = "speed";
#endif
        #endregion

        #region Mail Constant 
#if MAIL
        internal const string RECIPIENT_MAIL_ID= "recipientMailId";
        internal const string CCID_LIST= "ccIdList";
        internal const string INTERNAL= "internal";
        internal const string SUBJECT = "subject";
        internal const string BODY = "body";
#endif
        #endregion

        #region Map
#if MAP
        internal const string MARKER_INFO = "markerInfo";
        internal const string LOCATION_LATITUDE = "locationLatitude";
        internal const string LOCATION_NAME = "locationName";
        internal const string LOCATION_DESCRIPTION = "locationDescription";
        internal const string LOCATION_LONGITUDE = "locationLongitude"; 
        internal const string FROM_LOCATION = "fromLocation";
        internal const string TO_LOCATION = "toLocation";
#endif
        #endregion

        #region Multiview
        internal const string PERCENTAGE = "percentage";
        internal const string TARGET_VIEW = "targetView";
        internal const string LAUNCH_PAGE = "launchPage";
        internal const string VERTICAL = "vertical";
        #endregion

        #region NFC
#if NFC
        internal const string DEV = "DEVICE";
        internal const string TAG = "TAG";
        internal const string MSG = "MSG";
        internal const string NFC_URL = "URL";
        internal const string PsERCENTAGE = "percentage";
#endif
        #endregion

        #region Orientation
        internal const string CURRENT_ORIENTATION = "currentOrientation";
        #endregion

        #region OTP
        internal const string OTP_CB = "NativeContainer.OTPCallBack";
        internal const string PIN = "pin";
        internal const string USER = "user";
        internal const string TIME_STAMP = "timeStamp";
        #endregion

        #region Phone Call
        internal const string NUMBER = "number";
        #endregion

        #region Phone SMS
        internal const string PHONE_NUMBER = "phoneNo";
        internal const string MESSAGE = "message";
        #endregion

        #region Notification
        internal const string APPZILLON_NOTIFICATION_REGISTRATION = "appzillonNotificationRegistration";
        internal const string OS_ID = "osId";
        internal const string NOTIFICATION = "NOTIFICATION";
        internal const string DEVICE_NAME = "deviceName";
        internal const string REG_ID = "regId";
        #endregion

        #region OTA
        internal const string CHILD_APP_VERSION = "APPVERSION";
        #endregion

        #region Storage
        internal const string EXECUTE_QUERY = "executeQuery";
        internal const string DATABASE_NAME = "databaseName";
        internal const string SQL_RESULT = "sqlResult";
        #endregion


        #region Twitter
#if TWITTER
        internal const string TWITTER_NAME = "twitterName";
        internal const string SCREEN_NAME = "screen_name"; 
        internal const string PROFILE_IMAGE_URL = "profile_image_url";
#endif
        #endregion

        #region URL & WEBVIEW
        internal const string URL = "url";
        internal const string URL_WEBVIEW = "URL";
        #endregion

        #region AppsicToken
        internal static string SNONCE = string.Empty;
        internal const string REQ_STATUS = "status";
        internal const string APPZLION_GET_APP_SEC_TOKEN_REQUEST = "appzillonGetAppSecTokensRequest";
        internal const string APPZLION_GET_APP_SEC_TOKEN_RESPONSE = "appzillonGetAppSecTokensResponse";
        internal const string APPZLION_GET_APP_SEC_TOKEN = "appzillonGetAppSecTokens";
        internal const string CSNONCE = "CSNONCE";
        internal const string REQUESTID = "requestId";
        internal const string SAFETOKEN = "safeToken";
        internal const string SESSIONTOKEN = "sessionToken";
        internal const string SERVERNONCE = "serverNonce";
        internal static string Payload = string.Empty;
        internal static string DataIntegrity = string.Empty;


        #endregion
    }
}
