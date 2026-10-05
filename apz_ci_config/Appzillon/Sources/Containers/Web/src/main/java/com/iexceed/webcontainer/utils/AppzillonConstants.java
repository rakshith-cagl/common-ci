package com.iexceed.webcontainer.utils;

public interface AppzillonConstants {
	String ACTION_ID="actionId";
	String STATUS="status";
	String SUCCESSCAPS="SUCCESS";
	String DEFAULT_SETTINGS="DEFAULT_SETTINGS";
	String FAILURE="failure";
	String SCREENID = "screenId";
	String FWDSLASH = "/";
	String BWDSLASH = "\\";
	String UNDERSCORE = "_";
	String RESULT = "result";
	String FAILURECAPS = "FAILURE";
	String UTF_8="UTF-8";
	String SETTINGSDATAJSON = "userprefs.json";
	String APPPROPSJSON = "appprops.json";
	String SUCCESS = "success";
	String SCREEN_ID = "SCREENID";
	String INVALID = "INVALID";
	String ULOAD_FAILURE = "APZ-FL001";
	String SUCCESS_MESSAGE="successMessage";
	String APPZILLON_ERROR_MSG = "errorMessage";
	String DEVICE_ID_WEB = "WEB";
	
	String APPS_PATH = "/apps/";
	String CONFIG_PATH = "/screens/config/";
	String PROPERTIES_EXTN = ".properties";
	String APPPROPERTIES = "AppProperties";
	
	String ID = "id";
	String KEY = "key";
	String STRING_TO_ENCRYPT="stringToEncrypt";
	String STRING_TO_DECRYPT="stringToDecrypt";
	String ENCRYPTED_STRING="encryptedString";
	String DECRYPTED_STRING="decryptedString";
	String ERRORCODE = "errorCode";	
	String ERRORDESC = "errorDescription";
	String ERRORMSG = "errorMessage";
	String FACTORY="PBKDF2WithHmacSHA1";
	String ALGORITHM="AES";
	String CIPHER="AES/CBC/PKCS5Padding";
	int ENCRYPT_REQ = 1;
	int DECRYPT_REQ = 2;
	String SALT = "Salt: ";
	String DOLLAR="$";
	
	String SQLRESULT = "sqlResult";
	String DATABASENAME ="databaseName";
	String EXECUTEQUERY="executeQuery";
	String SELECT="SELECT";
	String JBOSSJNDILOOKUPVALUE="java:";
	String TOMCATJNDILOOKUPVALUE="java:/comp/env";
	String WEBSPHEREJNDILOOKUPVALUE="java:/comp/env";
	
	//ServerDetector Constants
	String GERONIMO_ID = "geronimo";
	String GLASSFISH_ID = "glassfish";
	String JBOSS_ID = "jboss";
	String JETTY_ID = "jetty";
	String JONAS_ID = "jonas";
	String OC4J_ID = "oc4j";
	String RESIN_ID = "resin";
	String TOMCAT_ID = "tomcat";
	String WEBLOGIC_ID = "weblogic";
	String WEBSPHERE_ID = "websphere";
		
	//Session Management
	String SESSIONID = "appzillonSessionID";
	String REQUESTKEY = "appzillonRequestKey";
	
	String APPZILLON_SESSION_ID = "sessionId";
	String APPZILLON_REQUEST_KEY = "requestKey";
	String APPZILLON_INTERFACEID = "interfaceId";
	String APPZILLON_HEADER = "appzillonHeader";
	String APPZILLON_LOGIN_REQ = "appzillonAuthenticationRequest";
	String APPZILLON_LOGOUT_REQ ="appzillonLogoutRequest";
	String APPZILLON_RELOGIN_REQ = "appzillonReLoginRequest";
	String APPZILLON_BODY = "appzillonBody";
	String APPZILLON_ERRORS = "appzillonErrors";
	String APPZILLON_ERROR_CODE = "errorCode";
	String APPZILLON_ERROR_DESC = "errorDescription";
	String APPZILLON_REQUEST="appzillonRequest";
	String APPZILLON_UPLOADFILE_RES="appzillonUploadFileResponse";
	String APPZILLON_UPLOADWSFILE_RES="appzillonUploadFileWSResponse";
	String APPZILLON_INTERFACE_UPLOAD_FILE_WS = "appzillonUploadFileWS";
	String APPZILLON_FILEPUSH_RES = "appzillonFilePushServiceResponse";
	String APPZILLON_FILEPUSH_WS_RES = "appzillonFilePushServiceWSResponse";
	String APPZILLON_INTERFACE_FILE_PUSH = "appzillonFilePushService";
	String APPZILLON_INTERFACE_FILE_PUSH_AUTH = "appzillonFilePushServiceAuth";
	String APPZILLON_INTERFACE_FILEPUSH_WS = "appzillonFilePushServiceWS";
	String APPZILLON_FILEPUSH_REQ = "appzillonFilePushServiceRequest";
	String APPZILLON_FILEPUSH_WS_REQ = "appzillonFilePushServiceWSRequest";
	String APPZILLON_CHANGEPASSWORD = "appzillonChangePassword";
	String APPZILLON_SERVER_REQ = "appzillonServerRequest";
	String APPZILLON_FORGOTPSWD = "appzillonForgotPassword";
	String APPZILLON_FORGOTPSWD_REQ = "appzillonForgotPasswordRequest";
	String APPZILLON_UPLOAD_FILE_RES = "appzillonUploadFileResponse";
	String APPZILLON_UPLOAD_PATH = "/upload";
	String APPZILLON_MAP = "appzillonMaps";
	
	String FILE_OVERRIDE = "overWrite";
	String FILE_DETAILS = "fileDetails";
	String FILE_DESTINATION="destination";
	String FILE_NAME="fileName";
	String FILE_TYPE="fileType";
	String FILE_NO="fileNo";
	String FILE_CONTENTS="fileContents";
	String FILE_SIZE="fileSize";

	String CONTAINER_PROPERTIES = "containerprops";
	String APP_PROPERTIES = "appprops.json";
	String CACHE_CONTROL = "Cache-Control";
	String CACHE_CONTROL_VAL = "no-cache, no-store";
	String CACHE_CONTROL_VAL_2 = "no-cache, must-revalidate";
	String AUTHENTICATION_TYPE_DEVICE_ID = "#DeviceId";
	String YES = "Y";
	String NO = "N";
	String GENERATE="generate";
	String LOGIN_REQUEST = "loginRequest";
	String CHANGEPASSWORD_REQ = "changePasswordRequest";
	String HEADER_SYSDATE = "sysDate";
	String HEADER_USER_ID = "userId";
	String HEADER_PIN = "pwd";
	String CONTENT_TYPE="Content-Type";
	String CONTENT_TYPE_APP_JSON="application/json";
	String LOGIN_RESPONSE = "loginResponse";
	String USER_DET = "userDet";
	String USER_PREFS = "userPrefs";
	String SELECTOR = "selector";
	String TXN_REF_NO = "txnRefNo";
	
	String ACTION_ID_USERSETTINGS = "USERSETTINGS";
	String ACTION_ID_LOAD_SETTINGS = "LOADSETTINGS";
	String ACTION_ID_UPLOAD = "UPLOAD";
	String ACTION_ID_UPLOADAUTH = "UPLOADAUTH";
	String ACTION_ID_UPLOADWS = "UPLOADWS";
	String ACTION_ID_ENCRYPT = "ENCRYPT";
	String ACTION_ID_DECRYPT="DECRYPT";
	String ACTION_ID_SQL = "SQL";
	String ACTION_ID_DOWNLOAD = "DOWNLOADFILE";
	String ACTION_ID_DOWNLOADAUTH ="DOWNLOADAUTH";
	String ACTION_ID_DOWNLOADWS ="DOWNLOADWS";
	
	String DUMMYSESSION = "Ziojunbw197urtIujujUUU8768768768"; 
	String DUMMYREQUESTKEY = "1.8967678756544442";
	String USERID = "appzillonUserId";
	String SIGNEDIN = "SIGNEDIN";
	String TRUE = "true";
	String HEADER_ORIGINATION="origination";
	String HTTP = "http";
	String HTTPS = "https";
	String POST = "POST";
	String GET = "GET";
	String OK = "ok";
	String ERROR_TYPE="errorType";
	String ERROR_SOURCE="errorSource";
	String ERROR_SEVERITY="errorSeverity";
	String ERROR_MSG = "errorMessage";
	String LOG_TYPE = ".log";
	String ERROR_LOG_ROUTER = "errorLogRouter";
	String SPLITSTR = "[\n]";
	String UPLOADLOGFILE="uploadLogFile";
	String MULIPART ="multipart/form-data; boundary=***";
	String DATEFORMAT ="ddMMyyHHmmss";
	String QUES = "?";
	String CARRIAGE = "\r\n--";
	String NEWLINE = "\n";
	
	String SYSDATEFORMAT="EEE, dd MM yyyy HH:mm:ss";
	String EXPIRY_DATE_FORMAT="dd/MM/yyyy";
	String MESSAGE="Message";
	String WEB_ERROR_LOG="WebContainerErrorLogFile.log";
	
	String UPLOADFILE="UPLOADFILE";
	String CONTENT_TYPE_MULTIPART="multipart/form-data";

	//Azure details
	String READ_PEM_FROM_VAULT ="READ_PEM_FROM_VAULT";
	String CLIENT_ID ="CLIENT_ID";
	String CLIENT_SECRET ="CLIENT_SECRET";
	String VAULT_URL ="VAULT_URL";
	String AUTH_URL_KEY_VAULT ="AUTH_URL_KEY_VAULT";
	String CONTENT_TYPE_VALUE="application/json";
	String AZURE_VALUE = "value";
	String CLOUD_LOGGING="CLOUD_LOGGING";
	String AUTH_URL_APP_CONFIG ="AUTH_URL_APP_CONFIG";
	String INSTRUMENTATION_KEY = "INSTRUMENTATION_KEY";
	String AUTHORIZATION="Authorization";
	String CLOUD_AZURE = "Azure";
	String AZURE_CLIENT_ID ="client_id";
	String AZURE_CLIENT_SECRET = "client_secret";
	String AZURE_SCOPE="scope";
	String AZURE_SCOPE_URL ="https://vault.azure.net/.default";
	String AZURE_GRANT_TYPE = "grant_type";
	String AZURE_GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
	String HTTP_PROTOCOL_TLS = "TLSv1.2";
	String ACCESS_TOKEN="access_token";
	String CLOUD_PROVIDER="CLOUD_PROVIDER";
	String AZURE_KEY_VAULT ="KeyVault";
	String AZURE_APP_CONFIG="AppConfig";
	String AZURE_RESOURCE ="resource";
	String AZURE_RESOURCE_URL ="https://management.azure.com/";
	String APP_CONFIG_URL ="APP_CONFIG_URL";
	String HTTP_HEADER_ACCEPT="Accept";
	String AZURE_APP_CONFIG_KEY="Key";
	String ERROR = "error";
	String URI="uri";
	String KEY_VAULT_API_VERSION="?api-version=2016-10-01";
	String APP_CONFIG_API_VERSION ="?api-version=1.0";


	//file download
	String FILENAME = "fileName";
	String FILEPATH="filePath";
	String DOWNLOADS_FOLDER="/Downloads";
	String FWDSLASH_SPLITTER ="[/]";
	String FILELINK = "fileLink";
	String DWNLDS = "Downloads/";
	String DOWNLOAD_FILE_ERROR = "Download failed";
	String APPZILLON_DOWNLOAD_URL = "/downloadFile";
	String BASE64 = "base64";
	String FILE_NOT_FOUND = "File not found";
	String FILE_PATH = "/screens/AppMessage.jsp";
	String APP_EXPIRED = "Application has expired";
	
	//app properties
	String SERVER_URL = "SERVERURL";
	String APP_EXPIRY_DATE = "EXPIRYDATE";
	String SERVER_TOKEN = "SERVERTOKEN";
	String OTP_REQUIRED = "otpReqd";
	String AUTHENTICATION_TYPE = "AUTHENTICATIONTYPE";
	
	//container properties
	String USER_SETTINGS_PATH = "USERSETTINGSPATH";
	String LOG_PATH = "LOGPATH";
	String MAIN_APP_ID = "MAINAPPID";
	String APP_ID = "appId";
	String OVERRIDE_OTP = "OVERRIDEOTP";
	String SESSION_TIMEOUT = "SESSIONTIMEOUT";
	String KEY_STORE_PATH = "KEYSTOREPATH";
	String KEY_STORE_PASSWORD = "KEYSTOREPASSWORD";
	String TRUST_STORE_PATH = "TRUSTSTOREPATH";
	String TRUST_STORE_PASSWORD = "TRUSTSTOREPASSWORD";
	String COOKIE_AGE = "COOKIEAGE";
	String KEEP_ME_SIGNED_IN_ENABLED = "KEEPMESIGNEDIN";
	String SECURE_COOKIE = "SECURECOOKIE";
	String KEE_ME_SIGNED_IN_FLAG = "keepMeSignedIn";
	
	//System Properties 
	String SYSTEM_PROPERTY_SSL_KEY_STORE = "javax.net.ssl.keyStore";
	String SYSTEM_PROPERTY_SSL_KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
	String SYSTEM_PROPERTY_SSL_TRUST_STORE = "javax.net.ssl.trustStore";
	String SYSTEM_PROPERTY_SSL_TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
	String ENV_VARIABLE_PREFIX = "${env:";
	String SYS_VARIABLE_PREFIX = "${SYS:";
	String VARIABLE_SUFFIX = "}";
	
	//Request attributes
	String DEFAULT_SETTINGS_ATTR = "defaultSettings";
	String APP_PROP_ATTR = "appProps";
	String APZILLON_ARGS_ATTR = "appzillonArgs";
	
	//Bean ids
	String REQUEST_PROCESSOR_BEAN = "requestProcessorBean";
	String GENERATE_OTP = "generateOtp";
	
	//client-server Nonce
	String DEVICE_ID = "deviceId";
	String CLIENT_NONCE = "clientNonce";
	String DATA_INTEGRITY = "DATAINTEGRITY";
	String QOP = "appzillonQop";
	String SERVER_NONCE = "serverNonce";
	String APPZILLON_GET_APP_SEC_TOKENS_REQUEST = "appzillonGetAppSecTokensRequest";
	String APPZILLON_GET_APP_SEC_TOKENS_RESPONSE = "appzillonGetAppSecTokensResponse";
	String APPZILLON_GET_APP_SEC_TOKENS = "appzillonGetAppSecTokens";
	String SESSION_TOKEN = "sessionToken";
	String CSRF_PREVENTION="CSRFPREVENTION";
	String SAFE_TOKEN = "safeToken";
	String OWASP_CSRFTOKEN = "OWASP_CSRFTOKEN";
	String APPZILLON_SAFE = "appzillonSafe";
	String PAYLOAD_ENCRYPTION_REQ = "PAYLOADENCRYPTIONREQ";

	String ENCRYPTION_FLAG = "PAYLOADENCRYPTIONREQ";
	String APPZILLON_SAFE_KEY = "APPZILLONSAFEKEY";
	String EXCHANGE = "exchange";
	String PAYLOAD_ENCRYPTION_FLAG = "payloadEncryptionReq";
	String PAYLOAD_ENCRYPTION_ERROR_CODE = "APZ-CNT-330";
	String PAYLOAD_ENCRYPTION = "payloadEncryption";
	String TRANSITTED = "transitted";
	String ENCRYPTION_KEY_FILENAME = "ENCRYPTIONKEYFILENAME";
	String ALGORITHM_WITH_PADDING = "RSA/ECB/PKCS1Padding";
	String IFACE_ID_IN_URI="IFACEIDINURI";
	
	String FEDERATION_URL = "federationURL";
	String REPLAY_REQUEST_REQUIRED ="REPLAYREQ";

	String apzParseMetaJSON = "apzParseMetaJSON";
	String apzPersistHTMLInfo = "apzPersistHTMLInfo";
	String apzParseProductJSON = "apzParseProductJSON";
	String appzillonReloadLogger="appzillonReloadLogger";
	
	//Azure authentication  details
	String AUTHENTICATION_TYPE_AZURE_AD = "AzureAD";





	//Azure AD prop details
	String AAD_CLIENT_ID = "AAD_CLIENT_ID";
	String AAD_CLIENT_SECRETE = "AAD_SECRETE_KEY";
	String AAD_REDIRECT_URI = "AAD_REDIRECT_URI";
	String AAD_TENANT_ID = "AAD_TENANT_ID";
	String AAD_AUTHORITY="https://login.microsoftonline.com/{tenantId}/";
	String AAD_MS_GRAPH_ENDPOINT_HOST="https://graph.microsoft.com/";
	String AAD_ENDSESSION_ENDPOINT="https://login.microsoftonline.com/common/oauth2/v2.0/logout";

	//Azure AD B2C prop details
	String AAD_B2C_SIGN_IN_POLICY = "AAD_B2C_POLICY_SIGNIN";
	String AAD_B2C_PWD_RESET_POLICY = "AAD_B2C_POLICY_RESETPASSSWORD";
	String AAD_B2C_DOMAIN_NAME = "AAD_B2C_DOMAIN";
	String AAD_B2C_CLIENT_ID = "AAD_B2C_CLIENT_ID";
	String AAD_B2C_SECRET = "AAD_B2C_SECRET";
	String AAD_B2C_REDIRECTURI = "AAD_B2C_REDIRECTURI";
	String AAD_B2C_SCOPES="openid offline_access {clientId}";
	String AAD_B2C_AUTHORITY="https://{domain}.b2clogin.com/tfp/{domain}.onmicrosoft.com/";
	String AAD_B2C_SIGN_OUT_ENDPOINT="oauth2/v2.0/logout";
	String AAD_B2C_POST_SIGN_OUT_FRAGMENT="?post_logout_redirect_uri=";
	String AAD_B2C_FORGOT_PASSWORD_ERRCODE="AADB2C90118";
	String AAD_B2C_ERROR_DESCRIPTION_KEY="error_description";
	String AAD_B2C_PWD_RESET_CANCEL_ERRCODE="AADB2C90091";

	String AUTH_PROVIDER_AAD="azureAd";
	String AUTH_PROVIDER_AAD_B2C="azureAdB2c";
	String AUTH_PROVIDER = "AUTHPROVIDER";
	String THIRD_PARTY_AUTH_ENABLED = "THIRDPARTYAUTHENABLED";
	String AUTH_TOKEN="authToken";
	String AD_SIGN_OUT="logOutType=adlogout";
	String AD_PWD_RESET="pwdReset=adPwdReset";
	String IS_TOKEN_REFRESHED = "isTokenRefreshed";
	String PRINCIPAL_SESSION_NAME = "principal";
	String TOKEN_CACHE_SESSION_ATTRIBUTE = "token_cache";
	String IS_USER_AUTHENTICATED="isUserAuthenticated";

	String APPZILLON_SAFE_BIT="appzillonSafeBit";
	String SAFE_BIT="SAFE_BIT";

	String FUSION_CHART_KEY = "FUSION_CHART_KEY";
	String APZ_FUSION_CHART_KEY = "fusionChartKey";

	String POST_LOGOUT_URI = "?post_logout_redirect_uri=";
	String CONTENT_SECURITY_POLICY = "Content-Security-Policy";

	String ENC_MODE="encMode";
	String AES_CBC = "AES/CBC/PKCS5Padding";

	String AUTH_PROVIDER_KEYCLOAK = "keycloak";
	String EDIP_REDIRECT_URI = "EDIP_REDIRECT_URI";
	String AUTH_SERVER_URL = "AUTH_SERVER_URL";
	String AUTH_SERVER_REALM = "AUTH_SERVER_REALM";
	String ACTION_LOGOUT = "action=logout";
	String AUTH_TOKEN_EXP = "authTokenExp";
	
	String URL_ENCRYPTION_REQ = "URL_ENCRYPTION_REQ";
	String URL_KEY_SAFE = "URL_KEY_SAFE";

}
