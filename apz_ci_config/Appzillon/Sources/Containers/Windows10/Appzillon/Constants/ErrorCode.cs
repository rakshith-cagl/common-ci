using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Constants
{
    class ErrorCode
    {

        internal const string INVALID_JSON_REQUEST = "APZ-CNT-077";
        #region AcceleroMeter 
#if ACCELEROMETER
        internal const string ACCELERO_DEVICE_NOT_FOUND = "APZ-CNT-060";
        internal const string ACCELEROMETER_ALREADY_RUNNING = "APZ-CNT-015";
        internal const string ACCELERO_RUNTIME_EXCEPTION = "APZ-CNT-269";
#endif
        #endregion

        #region Audio & Video
#if MEDIA
        internal const string VIDEO_PLAY_OPERATION_FAILED = "APZ-CNT-002";
        internal const string OPERATION_NOT_POSSIBLE_IN_CURRENT_STATE = "APZ-CNT-033";
        internal const string CANT_PALY_WHILE_RECORDING = "APZ-CNT-029";
        internal const string COULD_NOT_SAVE = "APZ-CNT-095";
        internal const string RECORDING_FAIL = "APZ-CNT-031";
        internal const string PAUSE_FAIL = "APZ-CNT-094";
        internal const string AUDIO_FAIL = "APZ-CNT-097";
        internal const string PLAY_FAIL = "APZ-CNT-030";
        internal const string LIMIT_EXCEEDED = "APZ-CNT-117";
        internal const string UNABLE_TO_SAVE_RECORD = "APZ-CNT-095";
#endif
        #endregion

        #region Battery
        internal const string REGISTERING_TO_TIMED_MONITORING = "APZ-CNT-241";
        internal const string UNREGISTERING_MONITORING = "APZ-CNT-260";
        #endregion

        #region BioMetric Auth
        #if BIOMETRIC
        internal const string FINGERPRINT_AUTHENTICATION_AVAILABILITY_CHECK_FAIL = "APZ-CNT-142";
        internal const string BIOMETRIC_DEVICE_IS_BUSY = "APZ-CNT-219";
        internal const string NO_BIOMETRIC_DEVICE_FOUND = "APZ-CNT-215";
        internal const string BIOMETRIC_VERIFICATION_IS_DISABLED_BY_POLICY = "APZ-CNT-216";
        internal const string NO_FINGERPRINTS_REGISTERED = "APZ-CNT-218";
        internal const string FINGERPRINTS_VERIFICATION_IS_CURRENTLY_UNAVAILABLE = "APZ-CNT-219";
        internal const string TOO_MANY_FAILED_ATTEMPTS = "APZ-CNT-217";
        internal const string AUTHENTICATION_CANCELLED = "APZ-CNT-305";
        internal const string AUTHENTICATION_FAILED = "APZ-CNT-306";
        #endif
        #endregion

        #region Beacon
        #if BEACON
        internal const string BEACON_START_FAIL = "APZ-CNT-252";
        internal const string BEACON_STOP_FAIL = "APZ-CNT-254";
        #endif
        #endregion

        #region Camera
#if CAMERA
        internal const string VIDEO_RECORDING_FAIL = "APZ-CNT-264";
        internal const string PHOTO_CONVERTION_TO_BASE64_FAIL = "APZ-CNT-036";
        internal const string PHOTO_CAPTURE_FAIL = "APZ-CNT-005";
        internal const string PHOTO_LOAD_FAIL = "APZ-CNT-003";
#endif
        #endregion

        #region BarCode
        //#if BARCODE
        internal const string BARCODE_READ_FAIL = "APZ-CNT-013";
        internal const string CAMERA_ACCESS_DENIED = "APZ-CNT-211";
        //#endif
        #endregion

        #region Calendar
#if CALENDAR
        internal const string UNABLE_TO_CREATE_EVENT = "APZ-CNT-084";
        internal const string UNABLE_TO_DELETE_EVENT = "APZ-CNT-038";
        internal const string UNABLE_TO_EDIT_EVENT = "APZ-CNT-020";
#endif
        #endregion

        #region Comapss
#if COMPASS
        internal const string COMPASS_NOT_FOUND = "APZ-CNT-062";
        internal const string FAILED_TO_STOP_COMPASS = "APZ-CNT-063";
        internal const string COMPASS_ALREADY_RUNNING = "APZ-CNT-016";
#endif
        #endregion

        #region Crypto file
#if CRYPTO
        internal const string FILE_ENCRYPTION_FAIL = "APZ-CNT-209";
        internal const string FILE_DECRYPTION_FAIL = "APZ-CNT-210";
#endif
        #endregion


        #region Contacts 
#if CONTACTS
        internal const string CANNOT_ADD_CONTACT = "APZ-CNT-041";
        internal const string CONTACT_NOT_FOUND = "APZ-CNT-044";
        internal const string CONTACT_SELECTION_FAIL = "APZ-CNT-045";
        internal const string CAN_NOT_EDIT_CONTACT = "APZ-CNT-042";
#endif
        #endregion

        #region Common
        internal const string FILE_ALREADY_EXISTS = "APZ-CNT-228";
        internal const string FILE_DOES_NOT_EXISTS = "APZ-CNT-002";
        internal const string FILE_CONTENT_EMPTY_ERROR = "APZ-CNT-229";
        internal const string FILE_GET_FAIL = "APZ-CNT-002";
        internal const string FILE_NOT_SELECTED = "APZ-CNT-009";
        #endregion

        #region Device
        internal const string COULD_NOT_OBTAIN_SYSTEM_STATUS = "APZ-CNT-103";
        internal const string NOT_CONNECTED_TO_INTERNET = "APZ-CNT-059";
        #endregion

        #region Deeplink
        internal const string APP_OPEN_FAIL = "APZ-CNT-309";
        #endregion

        #region Events
        internal const string REGISTER_TO_EVT_FAIL = "UNDEFINED";
        #endregion

        #region Social
#if (FACEBOOK || GOOGLE || TWITTER || LINKEDIN)
        internal const string COULD_NOT_LOGIN = "APZ-CNT-247";
        internal const string HTTP_FAIL = "APZ-CNT-059";
#endif
        #endregion

        #region File Operation
#if FILEOPERATION

        internal const string SERVER_ERROR = "APZ-CNT-304";
        internal const string FILE_ACCESS_DENIED = "APZ-CNT-008";
        internal const string UPLOARDING_FILE = "APZ-CNT-007";
        internal const string UNABLE_TO_GET_FILE_SIZE = "APZ-CNT-289";
        internal const string DOWNLOADING_FILE = "APZ-CNT-079";
        internal const string FILE_COULD_NOT_BE_CREATED = "APZ-CNT-011";
        internal const string BASE64_TO_PDF_FAIL = "APZ-CNT-271";
        internal const string BASE64_TO_FILE_FAIL = "APZ-CNT-271";
        internal const string FILE_DELETE = "APZ-CNT-075";
        internal const string FILE_READ = "APZ-CNT-010";
        internal const string FILE_ENCODING = "APZ-CNT-270";
        internal const string ZIP_FAIL = "APZ-CNT-011";
        internal const string UNZIP_FAIL = "APZ-CNT-072";
        #endif
        #endregion

        #region Geofencing
//#if GEOFENCING
        internal const string MAP_LOCATION_NOT_FOUND = "APZ-CNT-092";
        internal const string GEOFENCING_FAIL = "APZ-CNT-206";

//#endif
        #endregion

        #region Map
#if MAP
        internal const string MAP_LOAD_FAIL = "APZ-CNT-107";
        internal const string DRIVINING_DIRECTIONS_LOAD_FAIL = "APZ-CNT-223";
        internal const string LOCATION_ACCESS_UNAVAILABLE = "APZ-CNT-051";
        internal const string GPS_FAIL = "APZ-CNT-059";
        internal const string GPS_ALREADY_RUNNING = "APZ-CNT-017";
        internal const string GPS_STOP_FAIL = "APZ-CNT-091";
#endif
        #endregion

        #region Get Insructions
        internal const string GET_INSTRUCTIONS_FAIL = "APZ-CNT-205";
        #endregion

        #region Mail
        #if MAIL
        internal const string MAIL_SENDING_FAILED = "APZ-CNT-012";
        #endif
        #endregion

        #region Multiview
        internal const string MULTIVIEW_LOAD_FAIL = "APZ-CNT-001";
        internal const string MULTIVIEW_CLOSE_FAIL = "APZ-CNT-012";
        #endregion

        #region NFC
        #if NFC
        internal const string NFC_NOT_SUPPORTED_BY_THE_DEVICE = "APZ-CNT-214";
        internal const string NFC_SEND_FAIL = "APZ-CNT-214";
        internal const string NFC_STOP_FAIL = "APZ-CNT-214";
        #endif
        #endregion

        #region Orientation
        internal const string ORIENATATION_LOCK_FAIL = "APZ-CNT-082";
        #endregion

        #region OTA
        internal static string CHILD_APP_VERSION_GET_FAIL= "UNDEFINED";
        internal static string CHILD_APP_DELETE_FAIL = "UNDEFINED";
        internal static string CHILD_APP_LAUNCH_FAIL = "APZ-CNT-221";
        internal static string CHILD_APP_DOES_NOT_EXISTS = "APZ-CNT-221";
        internal static string ERROR_WHILE_UPGRADING_APP = "APZ-CNT-221";
        internal static string SERVER_ERROR_WHILE_UPGRADING_APP = "APZ-CNT-221";
        internal const string SUB_APP_DEL_RUN_TIME_EXCEPTION = "APZ-CNT-221";
        #endregion

        #region Phone Call
        internal const string COULD_NOT_PLACE_THE_CALL_REQUESTED = "APZ-CNT-262";
        #endregion

        #region Phone SMS
        internal const string COULD_NOT_SEND_SMS = "APZ-CNT-077";
        internal const string NO_SUPPORT_FOR_BG_MSG = "APZ-CNT-245";
        #endregion

        #region Signature pad
#if SIGNATUREPAD
        internal const string FAILED_TO_LOAD_SIGNATURE_PAD = "APZ-CNT-136";
        internal const string FAILED_TO_ACCEPT_SIGNATURE = "APZ-CNT-212";
#endif
        #endregion

        #region Skype
        internal const string SKYPE_FAIL = "APZ-CNT-265";
        #endregion

        #region Storage
        internal const string SQL_QUERY_FAIL = "APZ-CNT-056";
        internal const string WHITELIST_UPDATE_FAIL = "APZ-CNT-054";
        #endregion

        #region Vibration
#if VIBRATE
        internal const string VIBRATION_REQUEST_FAIL = "APZ-CNT-213";
#endif
        #endregion

        #region Voice
#if VOICE
        internal const string SPEECH_RECOGNITION_FAILED = "APZ-CNT-203";
        internal const string MICROPHONE_ACCESS_DENIED = "UNDEFINED";
#endif
        #endregion

        #region Url
        internal const string URL_OPEN_FAIL = "APZ-CNT-131";
        internal const string VALIDATE_URL_FAIL = "APZ-CNT-131";
        #endregion

        #region Utils
        public const string GET_APPVERSION_FAIL = "UNDEFINED";
        #endregion

        #region Webview
        #if WEBVIEW
        internal static string WEBVIEW_LAUNCH_FAIL = "APZ-CNT-199";
        #endif
        #endregion

        #region Whatsapp
        internal const string WHATSAPP_OPEN_FAIL = "APZ-CNT-077";
        #endregion

    }
}
