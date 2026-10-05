package com.iexceed.appzillon.sms;


import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.service.InterfaceMasterService;
import com.iexceed.appzillon.domain.service.SessionStorageService;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.*;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.ISessionManager;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author arthanarisamy
 */
public final class SmsStartup {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
            ServerConstants.LOGGER_SMS, SmsStartup.class.getName());
    private static SmsStartup smsStartup = null;
    private static WebApplicationContext springContext;

    private SmsStartup() {
    }

    public static void init(WebApplicationContext wac) {
        springContext = wac;
        getInstance();
    }

    public static SmsStartup getInstance() {
        if (smsStartup == null) {
            smsStartup = new SmsStartup();
        }
        return smsStartup;
    }

    public void processRequest(Message pMessage) {
        LOG.info("{} ***************************** SmsStartup.processRequest * Start ******************************************", ServerConstants.LOGGER_PREFIX_SMS);
        Header lHeader = pMessage.getHeader();
        Request lRequest = pMessage.getRequestObject();
        InterfaceDetails lInterfaceDetails = pMessage.getIntfDtls();
        String linterfaceId = lInterfaceDetails.getInterfaceId();
        LOG.debug("{} processRequest - HeaderMap: {} , Request PayLoad: {}", ServerConstants.LOGGER_PREFIX_SMS, lHeader, lRequest.getRequestJson());

        switch (linterfaceId.toLowerCase()) {
            case "appzillonauthenticationrequest", "appzillonlogoutrequest", "appzillonreloginrequest",
                    "appzillonvalidateotp", "appzillonregenerateotp",
                    "appzillongetuserappaccesstoken":

                LOG.debug("{} Interface id is either AuthenticationRequest, LogoutRequest, ReloginRequest and interfaceId is {}", ServerConstants.LOGGER_PREFIX_SMS, linterfaceId);
                JSONObject request = pMessage.getRequestObject().getRequestJson();
                IHandler lAuthHandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_AUTHENTICATION_HANDLER);
                lAuthHandler.handleRequest(pMessage);
                JSONObject lSmsResp = pMessage.getResponseObject().getResponseJson();
                if (lSmsResp != null) {
                    processAuthenticationRequest(pMessage, lSmsResp, linterfaceId, request);
                }
                break;

            case "appzillonchangepassword":

                LOG.info("{} Routing To SmsChangePasswordRequestHandler", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler changepasswordhandler = (IHandler) getInstance()
                        .getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_CHANGE_PIN_HANDLER);
                changepasswordhandler.handleRequest(pMessage);
                break;

            case "appzillondecrypt":

                LOG.info("{} Routing To SmsDecryptHandler", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler lDecrypthandler = (IHandler) getInstance()
                        .getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_DECRYPT_HANDLER);
                lDecrypthandler.handleRequest(pMessage);
                break;

            case "appzilloninterfaceauthrequest", "appzillonscreenauthrequest", "appzillonfetchprivilegeservice":

                LOG.info("{} Routing To SmsAuthorizationHandler", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler authorizationhandler = (IHandler) getInstance()
                        .getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_AUTHORIZATION_HANDLER);
                authorizationhandler.handleRequest(pMessage);
                break;

            case "appzillonpasswordvalidate":

                LOG.info("{} Routing to SmsChangePasswordRequestHandler for password validate", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler passwordValidate = (IHandler) getInstance()
                        .getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_CHANGE_PIN_HANDLER);
                passwordValidate.handleRequest(pMessage);
                break;

            case "appzillongetintfscrbyappidroleid", "appzillongetscreensintfbyappid", "appzillongetrolemaster",
                    "appzillondeleterolemaster", "appzillonupdaterolemaster", "appzilloncreaterolemaster":

                LOG.info("{} Routing To SmsRoleProfileHandler ", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler roleprofilehandler = (IHandler) getInstance()
                        .getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_ROLE_PROFILE_HANDLER);
                roleprofilehandler.handleRequest(pMessage);
                break;

            case "appzillongetuser", "appzillongetpasswordrules", "appzillondeletepasswordrules",
                    "appzillonupdatepasswordrules", "appzilloncreatepasswordrules", "appzillonunlockuser",
                    "appzillonpasswordreset", "appzillongetrolesbyappid", "appzillongetrolesbyappiduserid",
                    "appzillonsearchuser", "appzillondeleteuser", "appzillonupdateuser", "appzilloncreateuser",
                    "devicestatus_req", "appzillonforgotpassword", "appzillonuserregistration",
                    "appzillonuserauthorization", "appzillondashboard", "appzillonsaveappaccess":

                LOG.info("{} Routing To SmsUserProfileHandler", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler userprofilehandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_USER_PROFILE_HANDLER);
                userprofilehandler.handleRequest(pMessage);
                break;

            case "appzillonsearchtxnlogging", "appzillonloginreport", "appzillonappusagereport",
                    "appzillongetreqresp", "appzillonmessagestatistics", "appzilloncustomer",
                    "appzilloncustomerlocation", "appzilloncustomerdetails":

                LOG.info("{} Routing To Report Handler in SMS", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler reporthandler = (IHandler) getInstance()
                        .getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.APPZILLON_ROOT_SMS_REPORT_HANDLER);
                reporthandler.handleRequest(pMessage);
                break;

            case "appzillonsearchscreen", "appzilloncreatescreen", "appzillonupdatescreen", "appzillondeletescreen":

                LOG.info("{} Routing To Screen Handler in SMS", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler screenhandler = (IHandler) getInstance()
                        .getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.APPZILLON_ROOT_SMS_SCREEN_HANDLER);
                screenhandler.handleRequest(pMessage);
                break;

            case "appzillonauditlog":

                LOG.info("{} Routing To SmsAccessLoggingHandler for Access Logging.", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler accesslogginghandler = (IHandler) getInstance()
                        .getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.APPZILLON_ROOT_SMS_AUDIT_LOG_HANDLER);
                accesslogginghandler.handleRequest(pMessage);
                break;

            case "appzillonerrorlogging", "appzillonreloadlogger":

                LOG.info("{} Routing To SmsLoggingHandler for Access Logging.", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler logginghandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.APPZILLON_ROOT_SMS_LOGGING_HANDLER);
                logginghandler.handleRequest(pMessage);
                break;

            case "appzillongeneratecaptcha":

                JSONObject requestJson = pMessage.getRequestObject().getRequestJson()
                        .getJSONObject(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_REQUEST);
                String appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
                String interfaceId = requestJson.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
                IntfMasterDtls captchaReq = InterfaceMasterService.getInterfaceMasterMap().get(appId).get(interfaceId);
                String isCaptchaEnabled = captchaReq != null ? captchaReq.getCaptchaReq() : "";
                if (ServerConstants.WEB.equalsIgnoreCase(isCaptchaEnabled) || ServerConstants.MOBILE.equalsIgnoreCase(isCaptchaEnabled) || ServerConstants.BOTH.equalsIgnoreCase(isCaptchaEnabled)) {
                    LOG.info("{} Routing To CaptchaGenerateHandler for handling captcha generation", ServerConstants.LOGGER_PREFIX_SMS);
                    IHandler captchaGenerateHandler = (IHandler) getInstance().getSpringContext()
                            .getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_CAPTCHA_GENERATE);
                    captchaGenerateHandler.handleRequest(pMessage);
                } else {
                    LOG.error("{} Captcha required is disabled for the interface ", ServerConstants.LOGGER_PREFIX_SMS);
                    SmsException lSmsException = SmsException.getSMSExceptionInstance();
                    lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_015));
                    lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_015.toString());
                    lSmsException.setPriority("1");
                    throw lSmsException;
                }
                break;

            case "appzillongetappfile", "appzillongetappmasterdetails", "appzillonotafiledownloadreq",
                    "appzilloncreateappmaster", "appzillonupdateappmaster", "appzillondeleteappmaster",
                    "appzillonsearchappmaster", "appzilloncreateappfile", "appzillonupdateappfile",
                    "appzillondeleteappfile", "appzillonsearchappfile", "appzillongetchildappdetails",
                    "getcnvuiwelcomemsg":

                LOG.debug("{} Routing To OTAHandler..", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler iHandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.APPZILLON_ROOT_SMS_OTA_HANDLER);
                iHandler.handleRequest(pMessage);
                break;

            case "appzillondeviceregistration", "appzillonsearchdevicemaster", "appzillondeletedevicemaster",
                    "appzillonupdatedevicemaster", "appzillonuserdeviceregistration":

                LOG.debug("{} Routing To DeviceMasterHandler..", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler handler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.APPZILLON_ROOT_SMS_DEVICE_MASTER_HANDLER);
                handler.handleRequest(pMessage);
                break;

            case "appzillonsmsuser":

                LOG.debug("{} Routing To SmsUserDetailHandler..", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler handler1 = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_smsUserDetail");
                handler1.handleRequest(pMessage);
                break;

            case "appzilloninsertdragdrop", "appzillondeletedragdrop", "appzillonsearchdragdrop",
                    "appzillonupdatedragdrop":

                LOG.debug("{} Routing To DragDropHandler..", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler handler2 = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_dragDropHandler");
                handler2.handleRequest(pMessage);
                break;

            case "appzilloninsertbeacon", "appzillonfetchbeacondetails", "appzillonupdatebeacondetails":

                LOG.debug("{} Routing To Beacon Handler...", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler handler3 = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_beaconHandler");
                handler3.handleRequest(pMessage);
                break;

            case "appzillonfetchardetails":

                LOG.debug("{} Routing To AugumentedReality Handler...", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler lARhandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_ARHandler");
                lARhandler.handleRequest(pMessage);
                break;

            case "appzillonsendsms":

                LOG.debug("{} Routing To Send SMS Handler...", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler smsHandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_sendSMSHandler");
                smsHandler.handleRequest(pMessage);
                break;

            case "appzillongetquerydesignerdata", "appzillondevicegrpquery", "appzillonappscreensquery",
                    "appzillonsavecustomizationdata", "appzillongetcustomizerdetails":

                LOG.debug("{} Routing To Customize Handler...", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler handler4 = (IHandler) getInstance().getSpringContext()
                        .getBean(pMessage.getHeader().getAppId() + "_customizeHandler");
                handler4.handleRequest(pMessage);
                break;

            case "appzillontracklocation":

                LOG.debug("{} Routing To TrackLocation Handler...", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler handler5 = (IHandler) getInstance().getSpringContext()
                        .getBean(pMessage.getHeader().getAppId() + "_trackLocation");
                handler5.handleRequest(pMessage);
                break;

            case "appzillongetappsectokens":

                LOG.debug("{} Routing To ClientSeverNonce Handler...", ServerConstants.LOGGER_PREFIX_SMS);
                com.iexceed.appzillon.domain.handler.IHandler handler6 = (com.iexceed.appzillon.domain.handler.IHandler) getInstance().getSpringContext()
                        .getBean(pMessage.getHeader().getAppId() + "_nonceHandler");
                handler6.handleRequest(pMessage);
                break;

            case "apzparsemetajson", "apzpersisthtmlinfo", "apzparseproductjson", "apzparsewidgetjson":

                com.iexceed.appzillon.domain.handler.IHandler handler7 = (com.iexceed.appzillon.domain.handler.IHandler) getInstance().getSpringContext()
                        .getBean(pMessage.getHeader().getAppId() + "_citiServiceImpl");
                handler7.handleRequest(pMessage);
                break;

            case "appzilloncreatetenant":

                LOG.debug("{} Routing To AppCreation Handler...", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler handler8 = (IHandler) getInstance().getSpringContext()
                        .getBean(pMessage.getHeader().getAppId() + "_tenantCreationHandler");
                handler8.handleRequest(pMessage);
                break;

            case "reloadserverproperties":

                pMessage.getHeader().setServiceType(ServerConstants.INTERACE_ID_RELOAD_SERVER_PROPERTIES);
                DomainStartup.getInstance().processRequest(pMessage);
                pMessage.getHeader().setServiceType("");
                break;

            default:
                LOG.warn("{} Interface id is not a SMS Type", ServerConstants.LOGGER_PREFIX_SMS);

        }
        LOG.info("{} ***************************** SmsStartup.processRequest * END ******************************************", ServerConstants.LOGGER_PREFIX_SMS);
    }

    private void processAuthenticationRequest(Message pMessage, JSONObject lSmsResp, String linterfaceId, JSONObject request) {
        if (!linterfaceId.equalsIgnoreCase(ServerConstants.INTERFACE_ID_LOGOUT)) {
            if (lSmsResp.has(ServerConstants.APPZILLON_ROOT_LOGIN_RES)) {
                JSONObject lLoginResp = lSmsResp.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_RES);
                if (lLoginResp.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS)) { //APPZILLON_ROOT_CANPROCEED

                    ISessionManager cSesssionManager = (ISessionManager) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_MANAGER);
                    cSesssionManager.createAuthtKey(pMessage);
                    cSesssionManager.createSessionID(pMessage);
                    if (lLoginResp.has(ServerConstants.USERDET)) {
                        pMessage.getRequestObject().setRequestJson(request);
                        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUTHORIZATION);
                        DomainStartup.getInstance().processRequest(pMessage);
                    }
                    pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_DELETE_SESSION_STORAGE);
                    DomainStartup.getInstance().processRequest(pMessage);
                    flushSessionStorage(pMessage);
                    pMessage.getHeader().setServiceType("");
                } else {
                    LOG.debug("{} Status found not true : Authentication Failed", ServerConstants.LOGGER_PREFIX_SMS);
                }
            }
        } else {
            boolean logOutResp = pMessage.getResponseObject().getResponseJson().getBoolean(ServerConstants.MESSAGE_HEADER_STATUS);
            if (logOutResp) {
                LOG.debug("{} Log out response is success and clearing session...", ServerConstants.LOGGER_PREFIX_SMS);
                IHandler cSesssionHandler = (IHandler) SmsStartup.getInstance()
                        .getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_HANDLER);
                pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_CLEAR_SESSION);
                cSesssionHandler.handleRequest(pMessage);
                LOG.debug("{} Session cleared successfully", ServerConstants.LOGGER_PREFIX_SMS);
            }
        }
    }

    private void flushSessionStorage(Message pMessage) {
        boolean mapContains = SessionStorageService.sessionMap.containsKey(pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getUserId());
        if ((pMessage.getIntfDtls().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN) || pMessage.getIntfDtls().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)) && mapContains) {
            LOG.debug("{} Flush the storage details", ServerConstants.LOGGER_PREFIX_SMS);
            JSONArray sessionStore = SessionStorageService.sessionMap.get(pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getUserId());
            SessionStorageService.sessionMap.remove(pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getUserId());
            JSONObject lLoginRequest = pMessage.getRequestObject().getRequestJson();
            JSONObject lLoginResponse = pMessage.getResponseObject().getResponseJson();
            pMessage.getHeader().setFlushSessionMap(true);
            pMessage.getRequestObject().setRequestJson(new JSONObject().put(ServerConstants.REQUEST_DATA, sessionStore));
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_SAVE_OR_UPDATE_SESSION_STORAGE);
            DomainStartup.getInstance().processRequest(pMessage);
            pMessage.getRequestObject().setRequestJson(lLoginRequest);
            pMessage.getResponseObject().setResponseJson(lLoginResponse);
        }
    }

    public WebApplicationContext getSpringContext() {
        return springContext;
    }
}
