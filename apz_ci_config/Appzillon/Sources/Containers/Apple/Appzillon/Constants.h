#define PLUGIN_NOT_SUPPORTED @"APZ-CNT-022"
#define TARGETVIEW_NOT_FOUND @"APZ-CNT-026"
#define INVALIDAPPID @"APZ-CNT-183"
//Constants
#define ERROR_CODE @"errorCode"
#define BASE64_Fail @"1113"
#define JSCALLBACKMEHTOD @"Apz.nativeServiceCB"
#define CBEVENT @"event"
#define CBTEXT @"text"
#define PLUGINID @"id"
#define MESSAGE @"message"


//Camera PLUGIN
#define CAMERA_ACTION @"action"
#define CAMERA_FILENAME @"fileName"
#define CROPBOXSHAPE @"cropBox"
#define CAMERA_HTMLID @"elementId"
#define CAMERA_FILEOVERWRITE @"fileOverwrite"
#define CAMERA_SRCURL @"srcUrl"
#define CAMERA_SRCBASE64 @"srcBase64"
#define CAMERA_BASE64 @"base64"
#define CAMERA_SAVE @"save"
#define FRONT_CAMERA @"frontCamera"
#define CAMERA_BASE64_SAVE @"base64_Save"


#define DATEFORMAT @"ddMMYYHHmmss"
#define CAMERA_RESULT_ENCODEDIMAGE @"encodedImage"
#define CAMERA_RESULT_PATH @"path"
//#define CAMERA_SUCCESS_MESSAGEKEY @"successMessage"
#define CAMERA_JSON_PATH @"path"
#define CAMERA_JSON_ENCODEDIMAGE @"encodedImage"
#define CAMERA_DIRNAME @"photo"
#define CAMERA_IMAGE_EXTENSION @".jpg"
//CameraError Code...
#define CAMERA_NOTFOUND @"APZ-CNT-035"
#define CAMERA_ENCODEFAILBASE64 @"APZ-CNT-036"
#define CAMERA_IMAGE_CONVERSION_ERR @"APZ-CNT-118"
#define SACNNER_CANCELLED @"APZ-CNT-73"

//E-mail PLUGIN
#define EMAILSUBJECT @"subject"
#define EMAILRECIPIENTID @"recipientMailId"
#define EMAILBODY @"body"
#define EMAIL_CC_IDLIST @"ccIdList"
#define EMAIL_CLIENT_ERROR @"APZ-CNT-012"
#define EMAIL_CLIENT_ERROR_MSG @"Configure your mail client"


//DEVICE JSON keys
#define DEVICE_JSON_OSNAME @"osName"
#define DEVICE_JSON_OSVERSION @"osVersion"
#define DEVICE_JSON_DEVTYPE @"devType"
#define DEVICE_JSON_SCREENRESOLUTION @"screenResolution"
#define DEVICE_JSON_BATTERYSTATUS @"batteryStatus"
#define DEVICE_JSON_CONNECECTIONTYPE @"connectionType"
#define DEVICE_CARRIER_NAME @"displayName"
#define DEVICE_CARRIER_SUBCRIPTIONID @"subscriptionId"
#define DEVICE_CARRIER_CCID @"icc_id"
#define DEVICE_CARRIER_SLOT @"slot"
#define DEVICE_CARRIER_SIM_DETAILS @"simDetails"

//AUDIO
#define AUDIO_SUCCESSCALLBACK @"successCallback"
#define AUDIO_FAILCALLBACK @"failureCallback"
#define AUDIO_SAVEFORMAT @"saveFormat"
#define AUDIO_ACTION @"action"
#define AUDIO_FILENAME @"fileName"
#define AUDIO_DIRNAME @"audio"
#define AUDIO_EXTENSION @".caf"
#define AUDIO_LOCATION @"location"
#define AUDIO_LO_DEFAULT @"DEFAULT"
#define AUDIO_LO_EXTERNAL @"EXTERNAL"

//AUDIO FUNCTIONS
#define AUDIO_RECORD @"record"
#define AUDIO_PLAY @"play"
#define AUDIO_SAVE @"save"
#define AUDIO_PAUSE @"pause"

//AUDIO RECORD
#define AUDIO_ENCODER_QUALITYKEY 16
#define AUDIO_ENCODER_BITRATEKEY 2
#define AUDIO_ENCODER_NOOFCHANNELKEY 44100.0

//#define AUDIO_EXCEPTION_UNABLETORECORD @"APZ-CNT-031"
#define AUDIO_EXCEPTION_RECORDFAIL @"APZ-CNT-031"
#define AUDIO_EXCEPTION_ENCODEFAIL @"APZ-CNT-121"
#define AUDIO_EXCEPTION_DECODEFAIL @"APZ-CNT-122"
#define AUDIO_EXCEPTION_RECBUSY @"APZ-CNT-029"
#define AUDIO_EXCEPTION_PLAYFAIL @"APZ-CNT-030"
#define AUDIO_EXCEPTION_NO_REC_PLAY @"APZ-CNT-033"

//BATTERY PLUGIN

#define BATTERY_LEVEL @"batteryLevel"
#define BATTERY_STATE @"batteryState"

//Locale JSON
#define LOCALE_JSON @"locale"
#define FAILED_GET_LOCALE @"APZ-CNT-049"

//GPS
#define GPS_LATITUDE @"latitude"
#define GPS_LONGITUDE @"longitude"
#define GPS_LOCATION_ADDRESS @"address"
#define GPS_ALTITUDE @"altitude"
#define GPS_ACCURACY @"accuracy"
#define GPS_ALTITUDEACCURACY @"altitudeaccuracy"
#define GPS_HEADING @"heading"
#define GPS_SPEED @"speed"
//29-Jul-14::GPS Input Validation:: Shyam Bahadur Singh
#define GPS_Distance_Accuracy @"distanceInterval"
#define GPS_PERIODICITY @"periodicity"
#define GPS_ONCHANGE @"onChange"
#define GPS_DISTANCE_BASED @"distanceBased"
#define GPS_NONE @"none"
#define JSON_OPTIONALFIELD_EXCEPTION @"APZ_OP01" //has to be replaced
//25-Jul-2014::GPS Time Based::Shyam Bahadur Singh::Start
#define GPS_FREQ @"intervalBased"
#define GPS_Time_Accuracy @"timeInterval"
//25-Jul-2014::GPS Time Based::Shyam Bahadur Singh::End
#define INVALID_TIME_INTERVAL @"APZ-VAL-006"
#define GPS_SERVICES_OFF_ERROR @"APZ-CNT-057"
#define GPS_IS_RUNNING_ERROR @"APZ-CNT-017"
#define GPS_IS_STOPPED_ERROR @"APZ-CNT-058"
#define GPS_NETWORK_OFF @"APZ-CNT-059"
//JSON PARSING ERROR
#define JSON_EXCEPTION_CALLBACK @"jsonParseExceptionCallBack"
#define JSON_MANDATORYFIELD_EXCEPTION @"APZ_MD01"
#define JSON_OPTIONALFIELD_EXCEPTION  @"APZ_OP01"
#define HARDWARE_NOT_FOUND_CODE @"APZ_DN01"



//Calendar
#define CALENDAR_ACTION @"action"
#define CALENDAR_ACTION_CREATE @"create"
#define CALENDAR_ACTION_EDIT @"edit"
#define CALENDAR_ACTION_DELETE @"delete"
#define CALENDAR_TITLE @"title"
#define CALENDAR_ALARM @"alarm"
#define CALENDAR_FUTUREEVENTS @"futureEvents"
#define CALENDAR_ALARM_NONE @"none"
#define CALENDAR_ALARM_5_MINUTES @"5M"
#define CALENDAR_ALARM_15_MINUTES @"15M"
#define CALENDAR_ALARM_1_HOUR @"1H"
#define CALENDAR_ALARM_1_DAY @"1D"
#define CALENDAR_START_DATE @"startDate"
#define CALENDAR_END_DATE @"endDate"
#define CALENDAR_RECURRENCE_END @"recurrenceEndDate"
#define CALENDAR_LOCATION @"location"
#define CALENDAR_STARTTIME @"startTime"
#define CALENDAR_ENDTIME @"endTime"
#define CALENDAR_NEW_STARTTIME @"newStartTime"
#define CALENDAR_NEW_ENDTIME @"newEndTime"
#define CALENDAR_NEW_STARTDATE @"newStartDate"
#define CALENDAR_NEW_ENDDATE @"newEndDate"
#define CALENDAR_PRIORITY @"priority"
#define CALENDAR_PRIORITY_HIGH @"high"
#define CALENDAR_PRIORITY_NORMAL @"normal"
#define CALENDAR_PRIORITY_LOW @"low"
#define CALENDAR_SUMMARY @"summary"
#define CALENDAR_FREQUENCY @"recurrence"
#define CALENDAR_FREQUENCY_DAILY @"daily"
#define CALENDAR_FREQUENCY_WEEKLY @"weekly"
#define CALENDAR_FREQUENCY_MONTHLY @"monthly"
#define CALENDAR_FREQUENCY_NONE @"none"

//Calendar Error-Codes
#define CALENDAR_ACCESS_FAILED_CODE @"APZ-CNT-037"
#define CALENDAR_EVENT_ADD_FAILED_CODE @"APZ-CNT-084"
#define CALENDAR_EVENT_DELETE_FAILED_CODE @"APZ-CNT-038"
#define CALENDAR_EVENT_EDIT_FAILED_CODE @"APZ-CNT-020"
#define CALENDAR_EVENT_SAVED_FAILED_CODE @"APZ-CAL05"
#define CALENDAR_EVENT_NOTFOUND_FAILED_CODE @"APZ-CNT-039"
#define CALENDER_TITLE_MISSING @"APZ-CNT-087"
#define  CALENDER_DATE_FORMATE @"APZ-VAL-008"

//Contacts
#define CONTACT_OPERATION @"opn"
#define CONTACT_OPERATION_CREATE @"create"
#define CONTACT_OPERATION_EDIT @"edit"
#define CONTACT_OPERATION_DELETE @"delete"
#define CONTACT_DETAILS @"details"
#define CONTACT_DETAILS_FIRSTNAME @"firstName"
#define CONTACT_DETAILS_LASTNAME @"lastName"
#define CONTACT_DETAILS_PHONEMOBILE @"phoneMobile"
#define CONTACT_DETAILS_PHONEWORK @"phoneWork"
#define CONTACT_DETAILS_PHONEHOME @"phoneHome"
#define CONTACT_DETAILS_MAIL @"mail"
#define CONTACT_DETAILS_ADDRESS @"address"
#define CONTACT_DETAILS_WEBSITE @"website"
#define CONTACT_PHONE_SPECIAL_CHARACTERS @"/.()-+ "
#define CONTACT_IMAGE_PATH @"imagePath"
#define CONTACT_DETAILS_NICKNAME @"nickName"
#define CONTACT_DETAILS_BIRTHDAY @"birthday"

//Contacts Error-Codes
#define CONTACT_ACCESS_FAILED_CODE @"APZ-CNT-040"
#define CONTACT_CREATION_FAILED_CODE @"APZ-CNT-041"
#define CONTACT_DELETE_FAILED_CODE @"APZ-CNT-043"
#define CONTACT_EDIT_FAILED_CODE @"APZ-CNT-042"
#define CONTACT_NOTFOUND_FAILED_CODE @"APZ-CNT-044"
#define CONTACT_SEARCH_MULTIPLECONTACTS_FAILED_CODE @"APZ-CNT-045"
//Contacts Search
#define CONTACT_SEARCHCRITERIA @"searchCriteria"
#define CONTACT_SEARCH_JSON_ROOT @"contacts"

//Contacts Delete
#define CONTACTS_DELETECRITERIA @"deleteCriteria"





//RUN-TIME DEBUG
#define RUNTIME_DEBUG_TYPE @"errorType"
#define RUNTIME_DEBUG_SOURCE @"errorSource"
#define RUNTIME_DEBUG_MESSAGE @"errorMessage"
#define RUNTIME_DEBUG_SEVERITY @"errorSeverity"

#define RUNTIME_DEBUG_SEVERITY_FATAL @"F"
#define RUNTIME_DEBUG_SEVERITY_ERROR @"E"
#define RUNTIME_DEBUG_SEVERITY_WARN @"W"
#define RUNTIME_DEBUG_SEVERITY_INFO @"I"
#define RUNTIME_DEBUG_SEVERITY_DEBUG @"D"
#define RUNTIME_DEBUG_MAXLOG_SIZE 50
#define RUNTIME_DEBUG_LOGDIR @"Logs"
#define RUNTIME_DEBUG_LOGFILENAME @"Log"
#define RUNTIME_DATEFORMAT @"ddMMYYHHmmss"

#define RUNTIME_LOGFILE_EXTENSION @".txt"

//Cryptography Related Constants
#define STORAGE_AES_D_ERROR @"APZ-CNT-046"
#define STORAGE_AES_K_D_ERROR @"APZ-CNT-047"
#define STORAGE_AES_E_ERROR @"APZ-CNT-048"
#define STORAGE_AES_KEY @"key"
#define STORAGE_AES_CTEXT @"stringToDecrypt"
#define STORAGE_AES_SALT @"salt"
#define STORAGE_AES_IV @"iv"
#define STORAGE_AES_PTEXT @"stringToEncrypt"
#define ENCRYPTID @"encryptionId"
#define DECRYPTID @"decryptionId"

//Storage
//#define STORAGE_PREPARE_ERROR @"APZ-CNT-124"
//#define STORAGE_DB_NOT_FOUND @"APZ-CNT-123"
#define STORAGE_DB_EXISTS_ERROR @"APZ-CNT-120"
#define STORAGE_DB_NAME @"databaseName"
#define STORAGE_QUERY_EXECUTEQUERY @"executeQuery"
#define STORAGE_QUERY_JSONKEY_RESULT @"sqlResult"
#define DELETE_QUERY_FAILED @"APZ-CNT-055"
#define CREATE_QUERY_FAILED @"APZ-CNT-052"
#define SYSTEM_QUERY_ERROR @"APZ-CNT-056"
#define DATABASE_OPEN_ERROR @"APZ-CNT-051"
#define INSERT_QUERY_FAILED @"APZ-CNT-053"
#define UPDATE_QUERY_FAILED @"APZ-CNT-054"
#define QUERY_ID @"queryId"


//Maps
#define MAP_MARKERINFO @"markerInfo"
#define MAP_LOCATIONNAME @"locationName"
#define MAP_LOCATIONDESCRIPTION @"locationDescription"
#define MAP_LOCATION_LATITUDE @"locationLatitude"
#define MAP_LOCATION_LONGITUDE @"locationLongitude"




//Events
#define CONTROLEVENTS_ON @"on"
#define CONTROLEVENTS_OFF @"off"
#define CONTROLEVENTS_ALLEVENTS @"allEvents"
#define CONTROLEVENTS_BATTERYEVENT @"batteryEvent"
#define CONTROLEVENTS_APPPAUSEDEVENT @"appPausedEvent"
#define CONTROLEVENTS_APPRESUMEDEVENT @"appResumedEvent"
#define CONTROLEVENTS_APPCALLSTARTEVENT @"appCallStartEvent"
#define CONTROLEVENTS_APPCALLENDEVENT @"appCallEndEvent"

//Event Callbacks
#define EVENT_BATTERY_CALLBACK @"batteryStateChange"
#define EVENT_BATTERY_STATUS @"Status"
#define EVENT_APP_IDLETIMEOUT 60.0
#define EVENT_APP_IDLETIMEOUT_CALLBACK @"appIdletimeout"
#define EVENT_APP_BACKGROUND_CALLBACK @"applicationPaused"
#define EVENT_APP_FOREGROUND_CALLBACK @"applicationResumed"
#define EVENT_CALLSTART_CALLBACK @"applicationCallStart"
#define EVENT_CALLEND_CALLBACK @"applicationCallEnd"
#define EVENT_VOLUMEUP_CALLBACK @"volumeUpPressed"
#define EVENT_VOLUMEDOWN_CALLBACK @"volumeDownPressed"
#define EVENT_VOLUMELEVEL @"volumeLevel"
#define EVENT_ORIENTATION_PORTRAIT @"PORTRAIT"
#define EVENT_ORIENTATION_LANDSCAPE @"LANDSCAPE"
#define ORIENTATION_KEY @"orientation"


//File Operation related Constants....

//FILE-BROWSER
#define FILEBROWSER_FILTER @"filter"
#define FILEBROWSER_FILECATEGORY @"fileCategory"
#define FILEBROWSER_FILECATEGORY_IMAGE @"PHOTO"
#define FILEBROWSER_FILECATEGORY_VIDEO @"VIDEO"
#define FILEBROWSER_FILECATEGORY_AUDIO @"AUDIO"
#define FILEBROWSER_FILECATEGORY_DEFAULT @"DEFAULT"
#define FILEBROWSER_IMAGEGALLERY_DIRNAME @"photo"
#define FILEBROWSER_IMAGEGALLERY_FILENAME @"image_"
#define FILEBROWSER_IMAGEGALLERY_EXTENSION @".jpg"
#define FILEBROWSER_VIDEOGALLERY_DIRNAME @"video"
#define FILEBROWSER_VIDEOGALLERY_FILENAME @"video_"
#define FILEBROWSER_VIDEOGALLERY_EXTENSION @".mp4"
#define FILEBROWSER_LOCATION @"location"
#define FILEBROWSER_OPENFILE @"openFile"
#define FILEBROWSER_DOCS_LOCATION @"docs"
#define FILEBROWSER_PATH @"filePath"


//Supported File Extensions
#define FILEBROWSER_AUDIO_EXTENSION_MP3 @".mp3"
#define FILEBROWSER_AUDIO_EXTENSION_AMR @".amr"
#define FILEBROWSER_AUDIO_EXTENSION_CAF @".caf"


#define FILEBROWSER_VIDEO_EXTENSION_MP4 @".mp4"
#define FILEBROWSER_VIDEO_EXTENSION_MPG @".mpg"
#define FILEBROWSER_VIDEO_EXTENSION_MPEG @".mpeg"
#define FILEBROWSER_VIDEO_EXTENSION_AMR @".amr"

#define FILEBROWSER_IMAGE_EXTENSION_JPG @".jpg"
#define FILEBROWSER_IMAGE_EXTENSION_PNG @".png"

#define FILEBROWSER_DOCUMENTS_EXTENSION_DOCX @".docx"
#define FILEBROWSER_DOCUMENTS_EXTENSION_XLSX @".xlsx"
#define FILEBROWSER_DOCUMENTS_EXTENSION_PPT @".ppt"
#define FILEBROWSER_DOCUMENTS_EXTENSION_PDF @".pdf"

#define FILEBROWSER_FILTER_SEPERATOR @","

#define FILEBROWSER_LOCATION_SEPERATOR @"~"
#define FILEBROWSER_LOCATION_IOS @"ios-downloads"
#define FILEBROWSER_ICON_FOLDER @"fileBrowserIcons"

//File Upload
//here filedID contains the filePath,as we extracted this in plugin.js
#define FILEUPLOAD_FILENAME @"fieldID"
#define FILEUPLOAD_FILESERVER @"fileServer"
#define FILEUPLOAD_DESTINATION_DIR @"destination"
#define FILE_UPLOAD_SERVER @"/upload"

#define FILEUPLOAD_JSON_BODY @"appzillonBody"
#define FILEUPLOAD_JSON_ROOT @"appzillonFileUploadRequest"
#define FILEUPLOAD_JSON_APPID @"appId"
#define FILEUPLOAD_JSON_FILEID @"fileId"
#define FILEUPLOAD_JSON_FILENAME @"fileName"
#define FILEUPLOAD_JSON_FILETYPE @"fileType"
#define FILEUPLOAD_DATEFORMAT @"DDMMYYHHmmssSSS"

#define FILEUPLOAD_REQ_INTERFACEID @"appzillonUploadFile"
#define FILEUPLOAD_FAILURE_CODE @"APZ-FL001"
#define FILEUPLOAD_SUCCESS_MESSAGE @"File uploaded successfully"
#define FILEUPLOAD_FAILURE_MESSAGE @"File upload failed"
#define FILEUPLOAD_HEADER @"Header"
#define FILEUPLOAD_RES_ERROR_MESSAGE @"errorMessage"
#define FILEUPLOAD_RES_HEADER @"appzillonHeader"
#define FILEUPLOAD_RES_STATUS @"status"
#define FILEUPLOAD_RES_REQUESTKEY @"requestKey"
#define FILEUPLOAD_RES_SESSIONID @"sessionId"
#define FILEUPLOAD_RES_SYSDATE @"sysDate"
#define FILEUPLOAD_RES_BODY @"appzillonBody"
#define FILEUPLOAD_RES_ERROR_CODE @"errorCode"
#define FILEUPLOAD_RES_F_RESPONSE @"appzillonUploadFileResponse"
#define FILEUPLOAD_RES_F_RESPONSEWS @"appzillonUploadFileWSResponse"

//File Download
#define FILEDOWNLOAD_SERVERURL @"serverURL"
#define FILEDOWNLOAD_FILEID @"fileId"
#define FILEDOWNLOAD_RES_HEADER @"appzillonHeader"
#define FILEDOWNLOAD_RES_ERROR @"appzillonErrors"
#define FILEDOWNLOAD_RES_STATUS @"status"
#define FILEDOWNLOAD_RES_REQUESTKEY @"requestKey"
#define FILEDOWNLOAD_RES_SESSIONID @"sessionId"
#define FILEDOWNLOAD_RES_SYSDATE @"sysDate"
#define FILEDOWNLOAD_RES_SUCCESS @"success"
#define FILEDOWNLOAD_RES_BODY @"appzillonBody"
#define FILEDOWNLOAD_RES_FILE @"file"
#define FILEDOWNLOAD_RES_FILENAME @"fileName"
#define FILEDOWNLOAD_RES_FILEPATH @"filePath"
#define FILEDOWNLOAD_KET_PUSH @"appzillonFilePushServiceResponse"
#define FILEDOWNLOAD_KET_PUSHWS @"appzillonFilePushServiceWSResponse"
#define FILEDOWNLOAD_FAILURE_CODE @"APZ-FL002"
#define FILEDOWNLOAD_ERRORMESSAGE @"errorMessage"
#define FILEDOWNLOAD_ERRORCODE @"errorCode"
#define FILEDOWNLOAD_DESTINATION_DIR @"destinationPath"


//Open File
#define OPENFILE_DIR @"directory"
#define OPENFILE_FILENAME @"fileName"
#define OPENFILE_FILETYPE @"fileType"

#define OPENFILE_FILETYPE_PPT @"ppt"
#define OPENFILE_FILETYPE_DOC @"doc"
#define OPENFILE_FILETYPE_XLSX @"xlsx"
#define OPENFILE_FILETYPE_PDF @"pdf"
#define OPENFILE_FILETYPE_MOV @"mov"

#define OPENFILE_DIR_DOCS @"docs"
#define OPENFILE_DIR_VIDEOS @"video"


//File-Operation
#define FILEOPN_FILENAME @"fileName"
#define FILEOPN_FILECONTENT @"fileContent"
#define FILEOPN_DIRNAME @"Words"
#define FILEOPN_EXTENSION @".json"
#define FILECONTENT_PATH_JSONKEY @"filePath"
#define FILECONTENT_CONTENT_JSONKEY @"content"
#define OPENFILE_FILEPATH @"filePath"


//File-Related Error-Codes
#define INVALID_FILE_CATAGORY @"APZ-CNT-064"
#define FILEBROWSER_APP_FILES_EMPTY @"APZ-CNT-069"
#define FILEBROWSER_AUDIO_APP_FILES_EMPTY @"APZ-FBAUDIO_EMPTY"
#define FILEBROWSER_DOCUMENT_APP_FILES_EMPTY @"APZ-FB02"
//#define INVALID_FILE_LOCATION @"APZ-CNT-092"
#define FILE_NOT_FOUND_ERROR @"APZ-CNT-002"
#define FILE_COULD_NOT_CREATED @"APZ-CNT-011"
#define FILEOPERATION_INVALID @"APZ-CNT-082"
#define INVALIDFILEEXT @"APZ-CNT-327"
#define FILEOP_MODIFY_FAIL @"APZ-CNT-074"
#define FILEOP_DELETE_FAIL @"APZ-CNT-075"
//#define DIRECTORY_NOT_SUPPORTED @"APZ-CNT-076"
#define FILE_DOWNLOAD_ERROR @"APZ-CNT-079"
#define IMAGE_ACCESS_FAILED @"APZ-CNT-065"
#define IMAGE_ACCESS_DENIED @"APZ-CNT-066"
#define MUSIC_PALYER_MISSING @"APZ-CNT-067"
#define VIDEO_ACCESS_DENIED @"APZ-CNT-114"
#define DIRECTORY_CREATION_FAILED @"APZ-CNT-072"
//Screenshot
#define SCREENSHOT_FILENAME @"fileName"
#define SCREENSHOT_FOLDER @"folder"
#define SCREENSHOT_MEMORY @"memory"
#define SCREENSHOT_MEMORY_PHONE @"phone"
#define SCREENSHOT_MEMORY_SDCARD @"sdcard"
#define SCREENSHOT_SAVED_FAILED_CODE @"APZ-SCPDF01"
#define SCREENSHOT_DEFAULT_FOLDER @"Screenshots"

//Splash Screen
#define SPLASHSCREEN_FILEEXTENSION @"png"
#define SPLASHSCREEN_DIRECTORY @"/screens/styles"
#define SPLASHSCREEN_EMPTYPATH @"/screens/styles/"
#define SPLASHSCREEN_IPAD_PORTRAIT @"-Portrait@2x~ipad.png"
#define SPLASHSCREEN_IPAD_LANDSCAPE @"-Landscape@2x~ipad.png"
#define SPLASHSCREEN_PORTAIT_IPHONE5 @"-568h@2x.png"
#define SPLASHSCREEN_LANDSCAPE_IPHONE5 @"-Landscape-568h@2x.png"
#define SPLASHSCREEN_IPHONE_PORTRAIT @"@2x~iphone.png"
#define SPLASHSCREEN_IPHONE_LANDSCAPE @"-Landscape@2x~iphone.png"

//Biometric Authentication
#define BIOAUTH_ERROR @"APZ-CNT-193"
#define BIOAUTH_NOT_ENROLLED @"APZ-CNT-324"
//Vibration
#define VIBRATE_DEVICE_NOT_SUPPORT @"APZ-CNT-134"

//WebView

#define WEBVIEW_URL @"URL"

#define SERVER_ERROR @"APZ_RS_002"
#define INVALID_SRC @"APZ-CNT-190"
#define INVALID_DEST @"APZ-CNT-191"
#define INVALID_FILE_FORMAT @"APZ-CNT-275"
#define FILE_SIZE_ERROR @"APZ-CNT-289"
//#define APP_NOT_FOUND @"APZ-CNT-290"
#define APP_WIPE_OUT_FAILURE @"APZ-CNT-290"

#define INVALID_URL @"APZ-CNT-131"
#define TWITTERSDK_ERR @"APZ-CNT-321"

#define SERVERTOKENDECRYPTKEY @"APPZILLONDECRYPT"
#define PLISTENCRYPTKEY @"PLISTENCRYPTKEY"
#define NETWORKERRORCODE @"APZ-CNT-233"
#define CONTAINERTIMEOUT 180
