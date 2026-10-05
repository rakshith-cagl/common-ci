package com.iexceed.appzillon.router.handler;

import com.appzillon.scheduler.AppzillonScheduler;
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.handler.IHandler;
import com.iexceed.appzillon.domain.service.ClientServerNonceService;
import com.iexceed.appzillon.domain.service.InterfaceMasterService;
import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.intf.ExternalInterfaceDtls;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Error;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.InterfaceDetails;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.router.exception.RouterException;
import com.iexceed.appzillon.securityutils.RequestSanitizer;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.handlers.CaptchaGenerateHandler;
import com.iexceed.appzillon.sms.handlers.SessionHandler;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.workflow.handler.WorkflowHandler;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;


/**
 * @author arthanarisamy
 */
public class RequestHandler implements IRequestHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, RequestHandler.class.getName());

    @Override
    public void handleRequest(Message pMessage) {
        LOG.info("{} ***************************** AppzillonRequestHandler.handleRequest * Start ******************************************", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
        LOG.debug("{} Header and PayLoad Details - Request Header -: {} and RequestBody -: {}",
                ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getHeader(),
                pMessage.getRequestObject().getRequestJson());
        Header lHeader = pMessage.getHeader();
        InterfaceDetails lInterfaceDtls = pMessage.getIntfDtls();
        Error lError = Error.getInstance();
        boolean updateInDB = true;
        JSONObject requestBody = null;
        SessionHandler lSessionHandler = null;
        try {

            requestBody = pMessage.getRequestObject().getRequestJson();
            sanitzeRequest(pMessage);
            // This flag is used to disable full security.
            String securityFlag = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.REPLAY_REQUEST_REQUIRED);
            //Replay attack Check
            String replayAttackCheck = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.REPLAY_ATTACK_CHECK);

            // Server status check
            if (lInterfaceDtls != null && ServerConstants.INTERFACE_ID_CHECK_SERVER.equals(lInterfaceDtls.getInterfaceId())) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
                pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_CHECK_SERVER_RESPONSE, jsonObject));
            } else {
                // changes for client server nonce
                String intf = pMessage.getHeader().getInterfaceId();
                if (!intf.equals(ServerConstants.INTERFACE_ID_GET_APP_SEC_TOKENS)
                        && !intf.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS)
                        && !intf.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE)
                        && !intf.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH)
                        && !intf.equals(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE)
                        && !intf.equals(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH)
                        && !intf.equals(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS)
                        && !intf.equals(ServerConstants.INTERACE_ID_GET_USER_APPACCESS_TOKEN)
                        && !intf.equals(ServerConstants.INTERACE_ID_RELOAD_LOGGER)
                        && !intf.equals(ServerConstants.INTERFACE_ID_ON_APP_LAUNCH)
                        && !pMessage.getHeader().isSmsType()
                        && pMessage.getRequestObject().getRequestJson().has(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)) {
                    // DataIntegrity check
                    if ((Utils.isNullOrEmpty(securityFlag) || !ServerConstants.NO.equalsIgnoreCase(securityFlag)) && ServerConstants.YES.equals(pMessage.getSecurityParams().getDataIntegrity()) && !ServerConstants.RICT.equalsIgnoreCase(pMessage.getHeader().getOs())
                            && Utils.isNullOrEmpty(pMessage.getHeader().getUserAppAccessToken())) {
                        boolean qopStatus = Utils.checkQualityOfPayload(pMessage);
                        if (!qopStatus) {
                            RouterException lRouterException = RouterException.getInstance();
                            lRouterException.setMessage(lRouterException
                                    .getRestExceptionMessage(RouterException.EXCEPTION_CODE.APZ_RS_010));
                            lRouterException.setCode(RouterException.EXCEPTION_CODE.APZ_RS_010.toString());
                            lRouterException.setPriority("1");
                            LOG.error("{} Invalid Request. {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER,
                                    lRouterException);
                            throw lRouterException;
                        }
                        LOG.debug("{} Payload is intact and data integrity check is successful", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                    }
                    if ((Utils.isNullOrEmpty(securityFlag) || !ServerConstants.NO.equalsIgnoreCase(securityFlag))
                            && (Utils.isNotNullOrEmpty(replayAttackCheck) && ServerConstants.YES.equalsIgnoreCase(replayAttackCheck))) {
                        // Replay Attack Check
                        LOG.info("{} Inside Replay Attack", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                        IHandler smsHandler = (IHandler) SmsStartup.getInstance().getSpringContext()
                                .getBean(pMessage.getHeader().getAppId() + "_nonceHandler");
                        smsHandler.handleRequest(pMessage);
                    }

                }
                if (lInterfaceDtls == null) {
                    LOG.info("{} Requested Interface is found not to be an internal category. Hence will be checking from Camel Context.", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                    SpringCamelContext context = ExternalServicesRouter.getCamelContext();
                    LOG.debug("{} Injecting interface bean from Camel context.", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                    ExternalInterfaceDtls lExtInterfaceDtls = (ExternalInterfaceDtls) context.getApplicationContext().getBean(pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getInterfaceId() + "_intf");
                    lInterfaceDtls = InterfaceDetails.getInstance();
                    lInterfaceDtls.setAppId(pMessage.getHeader().getAppId());
                    lInterfaceDtls.setInterfaceId(pMessage.getHeader().getInterfaceId());
                    lInterfaceDtls.setCategory(lExtInterfaceDtls.getCategory());
                    LOG.debug("{} External interface Details -: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, lExtInterfaceDtls);
                    lInterfaceDtls.setSessionRequired(lExtInterfaceDtls.getSessionReq());
                    lInterfaceDtls.setType(ServerConstants.INTERFACE_CATEGORY_EXTERNAL);
                    LOG.debug("{} AppzillonBody -: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getRequestObject().getRequestJson().getJSONObject("appzillonBody"));
                    pMessage.getRequestObject().setRequestJson(pMessage.getRequestObject().getRequestJson().getJSONObject("appzillonBody"));

                }
                LOG.debug("{} Updated interface details Request InterfaceID -: {} and SessionRequired is -: {} and Category -: {} and Type -: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER,
                        lInterfaceDtls.getInterfaceId(), lInterfaceDtls.getSessionRequired(), lInterfaceDtls.getCategory(), lInterfaceDtls.getType());
                LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Updated interface details Request InterfaceID -:" + lInterfaceDtls.getInterfaceId() + " and SessionRequired is -:" + lInterfaceDtls.getSessionRequired()
                        + " and Category -:" + lInterfaceDtls.getCategory() + " and Type -:" + lInterfaceDtls.getType());
                setInterfaceProp(lInterfaceDtls, pMessage);

                pMessage.setIntfDtls(lInterfaceDtls);
                // For Captcha Validation
                String captchaReq = lInterfaceDtls.getCaptchaReq();
                String osType = pMessage.getHeader().getOs();
                //checking for the source and captchaReq flag.platform(WEB/MOBILE) based captcha enabling
                if ((ServerConstants.WEB.equalsIgnoreCase(osType) && ServerConstants.WEB.equalsIgnoreCase(captchaReq))
                        || ((ServerConstants.IOS.equalsIgnoreCase(osType) || ServerConstants.ANDROID.equalsIgnoreCase(osType))
                        && ServerConstants.MOBILE.equalsIgnoreCase(captchaReq))
                        || ServerConstants.BOTH.equalsIgnoreCase(captchaReq)) {
                    validateCaptcha(pMessage);
                }


                if (ServerConstants.YES.equalsIgnoreCase(lInterfaceDtls.getSessionRequired())) {
                    //Validating User Session
                    LOG.debug("{} Requesting to validate user session", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                    lSessionHandler = (SessionHandler) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_HANDLER);
                    pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_VALIDATE_SESSION);
                    lSessionHandler.handleRequest(pMessage);
                    LOG.info("{} After validating session, Session ID valid.", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                } else {
                    if (lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)
                            || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN)
                            || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_UPLOAD_FILE)) {
                        LOG.debug("{} Session not Required but session update required", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                        updateInDB = true;

                    } else if (lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_LOV)) {
                        // 7-10-2015 : validating session for the interface id appzillonLOVReq based on lov query id
                        JSONObject lLOVPayLoad = pMessage.getRequestObject().getRequestJson();
                        JSONObject lLOVReq = lLOVPayLoad.getJSONObject("appzillonLOVReqRequest");
                        LOG.debug("{} getVariablesList- lLOVReq : {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, lLOVReq);
                        if (!lLOVReq.has(ServerConstants.LOV_TYPE)) {
                            String lQueryId = lLOVReq.getString(ServerConstants.LOV_QUERY_ID);
                            pMessage.getHeader().setServiceType(ServerConstants.APPZDBFETCHLOVREQUEST);
                            JSONObject pResqObject = new JSONObject();
                            pResqObject.put(ServerConstants.LOV_QUERY_ID, lQueryId);
                            LOG.debug("{} lQueryId: {} and request JSON - pResqObject: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, lQueryId, pResqObject);
                            pMessage.getRequestObject().setRequestJson(pResqObject);
                            DomainStartup.getInstance().processRequest(pMessage);
                            JSONObject lLovDetails = pMessage.getResponseObject().getResponseJson();

                            pMessage.getRequestObject().setRequestJson(lLOVPayLoad);
                            LOG.debug("{} LOV request : {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getRequestObject().getRequestJson());
                            LOG.debug("{} Session required for LOV query id : {} is {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, lQueryId, lLovDetails.getString(ServerConstants.LOV_SESSION_REQD));

                            if (ServerConstants.YES.equalsIgnoreCase(lLovDetails.getString(ServerConstants.LOV_SESSION_REQD)) || "".equalsIgnoreCase(lLovDetails.getString(ServerConstants.LOV_SESSION_REQD))) {
                                LOG.debug("{} Requesting to validate user session", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                                lSessionHandler = (SessionHandler) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_HANDLER);
                                pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_VALIDATE_SESSION);
                                lSessionHandler.handleRequest(pMessage);
                                LOG.info("{} After validating session, Session ID valid.", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                            }
                        }
                    } else {
                        LOG.debug("{} Session not Required", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                        updateInDB = false;
                        handleSession(pMessage, lHeader);
                    }
                }

                if (ServerConstants.YES.equalsIgnoreCase(pMessage.getSecurityParams().getDefaultAuthorization())) {
                    LOG.info("{} Checking whether the interface is authorized for the user or not.", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                    pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_DEFAULT_AUTHORIZATION);
                    DomainStartup.getInstance().processRequest(pMessage);
                }

                if (lInterfaceDtls.getCategory().equalsIgnoreCase(ServerConstants.INTERFACE_CATEGORY_INTERNAL)) {
                    if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_SMS)) {
                        LOG.info("{} Routing To SMS Request Handler", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                        IRequestHandler sMShandler = new SMSServiceHandler();
                        sMShandler.handleRequest(pMessage);
                        LOG.debug("{} Response From sms Request Handler {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                        //LOG.error("{} Response From sms Request Handler {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                        JSONObject lSmsResp = pMessage.getResponseObject().getResponseJson();

                        if ((lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)
                                || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN))
                                && lSmsResp != null) {
                            JSONObject lLoginResp = lSmsResp.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_RES);
                            if (!"true".equals(lLoginResp.getString(ServerConstants.MESSAGE_HEADER_STATUS))) { //CANPROCEED
                                LOG.debug("{} Authentication failed", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                                updateInDB = false;
                            } else {
                                LOG.debug("{} User Login response status : {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER,
                                        lLoginResp.getString(ServerConstants.MESSAGE_HEADER_STATUS));
                            }
                        }
                    } else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_NOTIFICATIONS)) {
                        LOG.debug("{} Routing to NotificationHandler", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                        NotificationRequestHandler nfrequestHandler = new NotificationRequestHandler();
                        nfrequestHandler.handleRequest(pMessage);
                        LOG.debug("{} Response From NotificationHandler :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                        LOG.error("{} Response From NotificationHandler :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                    } else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_WORKFLOW)) {
                        LOG.debug("{} Routing to WorkFlowServiceHandler", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                        WorkFlowServiceHandler workflowHandler = new WorkFlowServiceHandler();
                        workflowHandler.handleRequest(pMessage);
                        LOG.debug("{} Response From WorkFlowServiceHandler :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                        LOG.error("{} Response From WorkFlowServiceHandler :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                    } else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_APPZILLON_WORKFLOW)) {
                        LOG.debug("{} Routing to WorkFlowHandler", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                        WorkflowHandler workflowHandler = new WorkflowHandler();
                        workflowHandler.handleRequest(pMessage);
                        LOG.debug("{} Response From WorkFlowServiceHandler :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                        LOG.error("{} Response From WorkFlowServiceHandler :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                    } else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_LOV)
                            || lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_OTP)
                            || lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_CNVUI)
                            || lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_NLP)) {
                        LOG.debug("{} Routing to Frameworks....", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                        IRequestHandler externalServiceHandler = new ExternalServiceRequestHandler();
                        externalServiceHandler.handleRequest(pMessage);
                        LOG.debug("{} Response From LOVService :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                        LOG.error("{} Response From LOVService :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                    } else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_SCHEDULER)) {
                        LOG.debug("{} Routing To scheduler...", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                        AppzillonScheduler scheduler = AppzillonScheduler.getSchedulerInstance();
                        scheduler.processRequest(pMessage);
                        LOG.debug("{} Response From Scheduler Service :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                        LOG.error("{} Response From Scheduler Service :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                    } else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_MULTI)) {
                        loadMultipleIntfOnAppLaunch(pMessage);
                    } else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_INTERFACE)) {
                        LOG.debug("{} Routing To interface handler...", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                        InterfaceServiceHandler interfaceServiceHandler = new InterfaceServiceHandler();
                        interfaceServiceHandler.handleRequest(pMessage);
                        LOG.debug("{} Response From interface handler Service :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                        LOG.error("{} Response From interface handler Service :: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                    }
                } else if (lInterfaceDtls.getCategory().equalsIgnoreCase(ServerConstants.INTERFACE_CATEGORY_EXTERNAL)) {
                    LOG.info("{} Routing To External Service Request Handler", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                    IRequestHandler externalServiceHandler = new ExternalServiceRequestHandler();
                    externalServiceHandler.handleRequest(pMessage);
                    LOG.debug("{} Response From External Service Request Handler : {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
                }
            }
        } catch (AppzillonException i) {
            LOG.error("{} In Abstract Appzillon Exception Block, Error Code : {} and Error Message : {}",
                    ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, i.getCode(), i.getMessage());

            lHeader.setStatus(false);
            pMessage.setHeader(lHeader);
            LOG.debug("{} Initial Request Body {}, pMessage.getRequestObject() -: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, requestBody, pMessage.getRequestObject().getRequestJson());
            pMessage.getRequestObject().setRequestJson(requestBody);
            if (lInterfaceDtls != null && (lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)
                    || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN))) {
                LOG.error("{} Exception in Authentication", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                updateInDB = false;
            }

            if (i.getCode().equals(SmsException.EXCEPTION_CODE.APZ_SMS_EX_003.toString())
                    || "APZ-UT-002".equals(i.getCode())
                    || "APZ-UT-001".equals(i.getCode())
                    || "APZ-UT-000".equals(i.getCode())) {
                LOG.debug("{} Session Error Or Owasp Error ,Will not update Existing Valid Session", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                LOG.error("{} Session Error Or Owasp Error ,Will not update Existing Valid Session", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                updateInDB = false;
            }
            Error err = Error.getInstance();
            err.setErrorCode(i.getCode());
            err.setErrorDesc(i.getMessage());
            pMessage.getErrors().add(err);
        } catch (NoSuchBeanDefinitionException re) {
            LOG.error("{} In NoSuchBeanDefinitionException Block {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, re);
            lHeader.setStatus(false);
            lError.setErrorCode("NSBEX_0");
            lError.setErrorDesc("Interface Details Not Found.");
            pMessage.getErrors().add(lError);
            pMessage.getRequestObject().setRequestJson(requestBody);

        } catch (RuntimeException re) {
            LOG.error("{} RuntimeException : {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, re);
            lHeader.setStatus(false);
            lError.setErrorCode("REX_0");
            if (re instanceof NullPointerException) {
                lError.setErrorDesc("NullPointerException");
            } else {
                lError.setErrorDesc("Oops something went wrong. Please try again.");
            }
            pMessage.getErrors().add(lError);
            pMessage.getRequestObject().setRequestJson(requestBody);
            if (lInterfaceDtls != null && (lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)
                    || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN))) {
                updateInDB = false;
            }
        } catch (Exception e) {
            LOG.error("{} Exception {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, e);
            lHeader.setStatus(false);
            lError.setErrorCode("EX_0");
            lError.setErrorDesc("Oops something went wrong. Please try again.");
            pMessage.getErrors().add(lError);
            pMessage.getRequestObject().setRequestJson(requestBody);
            if (lInterfaceDtls != null && (lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)
                    || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN))) {
                updateInDB = false;
            }
        }
        if (lInterfaceDtls != null && !ServerConstants.INTERFACE_ID_CHECK_SERVER.equals(lInterfaceDtls.getInterfaceId()) && !ServerConstants.INTERFACE_ID_LOGOUT.equals(lInterfaceDtls.getInterfaceId()) && updateInDB) {
            LOG.debug("{} Requesting Session Handler to update User Session.", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
            try {
                lSessionHandler = (SessionHandler) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_HANDLER);
                pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_CREATE_UPDATE_SESSION);
                LOG.debug("{} Session Handler Used ", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, lSessionHandler);
                lSessionHandler.handleRequest(pMessage);
                LOG.debug("{} After Updating user Session and Request Key in DB , RequestKey -: {} and SessionID -: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getHeader().getRequestKey(), pMessage.getHeader().getSessionId());
            } catch (Exception e) {
                LOG.warn("{} Error while Updating the session", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, e);
            }

            lHeader.setServiceType("");
        }
        if (lInterfaceDtls != null
                && !(ServerConstants.INTERFACE_ID_CHECK_SERVER.equals(lInterfaceDtls.getInterfaceId())
                || ServerConstants.INTERFACE_ID_GET_CNVUI_DLG.equals(lInterfaceDtls.getInterfaceId()))
                && pMessage.getSecurityParams().getLogTxn().equalsIgnoreCase(ServerConstants.YES)
                && ServerConstants.YES.equalsIgnoreCase(lInterfaceDtls.getTxnLogReq())) {
            LOG.debug("{} Logging Transaction updateInDB", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
            pMessage.getHeader().setServiceType(ServerConstants.LOG_TRANSACTION);
            pMessage.getRequestObject().setRequestJson(requestBody);
            try {
                DomainStartup.getInstance().processRequest(pMessage);
            } catch (Exception e) {
                LOG.error("{} Could not LOG the response in Database : {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, e);
            }
        }
        pMessage.getHeader().setServiceType("");
        if (pMessage.getResponseObject().getResponseJson() == null) {
            pMessage.getResponseObject().setResponseJson(requestBody);
        }
        LOG.info("{} ***************************** AppzillonRequestHandler.handleRequest * END ******************************************", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);

    }

    private void loadMultipleIntfOnAppLaunch(Message pMessage) {
        String parentInterface = "";

        try {
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Routing To appzillon OnAppLaunchInterface...");
            parentInterface = pMessage.getHeader().getInterfaceId();
            JSONObject finalResponse = new JSONObject();
            JSONObject totalRequest = pMessage.getRequestObject().getRequestJson();
            JSONObject getAppSecTokenFullRequest = new JSONObject();
            getAppSecTokenFullRequest.put("appzillonGetAppSecTokensRequest", totalRequest.getJSONObject("appzillonGetAppSecTokensRequest"));
            pMessage.getRequestObject().setRequestJson(getAppSecTokenFullRequest);
            //get app sec tokens request processing on app launch
            LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "Going to generate Nonce");
            ((ClientServerNonceService) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
                    .getBean("ClientServerNonceServcie")).generateNonce(pMessage);
            JSONObject getAppSecTokenRes = pMessage.getResponseObject().getResponseJson();
            LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "getAppSecTokenRes" + getAppSecTokenRes);
            if (pMessage.getHeader().getAppLaunch().equalsIgnoreCase(ServerConstants.FIRST) || pMessage.getHeader().getAppLaunch().equalsIgnoreCase(ServerConstants.APP_LAUNCH_NOTIFY)) {
                LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "launching app for the first time");
                JSONObject deviceRegistrationFullRequest = new JSONObject();
                deviceRegistrationFullRequest.put("deviceRegisterRequest", totalRequest.getJSONObject("deviceRegisterRequest"));
                pMessage.getRequestObject().setRequestJson(deviceRegistrationFullRequest);
                pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_MULTIFACTOR_DEVICE_REGISTRATION);
                pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DEVICE_MASTER);
                DomainStartup.getInstance().processRequest(pMessage);
                JSONObject deviceRegistrationRes = pMessage.getResponseObject().getResponseJson();
                LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "deviceRegistrationRes" + deviceRegistrationRes);
                finalResponse.put("deviceRegisterResponse", deviceRegistrationRes.getJSONObject("deviceRegisterResponse"));
            }
            JSONObject appzillonAppMasterFullRequest = new JSONObject();
            appzillonAppMasterFullRequest.put("appzillonAppMasterRequest", totalRequest.getJSONObject("appzillonAppMasterRequest"));
            pMessage.getRequestObject().setRequestJson(appzillonAppMasterFullRequest);
            pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_OTA_GET_APP_MASTER_DETAILS);
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
            DomainStartup.getInstance().processRequest(pMessage);
            JSONObject appzillonAppMasterRes = pMessage.getResponseObject().getResponseJson();
            LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "appzillonAppMasterRes" + appzillonAppMasterRes);

            if (pMessage.getHeader().getAppLaunch().equalsIgnoreCase(ServerConstants.APP_LAUNCH_NOTIFY)) {
                LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "launching app for the first time");
                pMessage.getRequestObject().setRequestJson(totalRequest.getJSONObject("appzillonNotificationRegistrationRequest"));
                pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_DEVICE_REGISTRATION);
                pMessage.getHeader().setServiceType(ServerConstants.SERVICE_PUSH_NOTIFICATION);
                DomainStartup.getInstance().processRequest(pMessage);
                JSONObject appzillonNotificationRegistrationRes = pMessage.getResponseObject().getResponseJson();
                LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "appzillonNotificationRegistrationRes" + appzillonNotificationRegistrationRes);
                finalResponse.put("appzillonNotificationRegistrationResponse", appzillonNotificationRegistrationRes);
            }
            finalResponse.put("appzillonGetAppSecTokensResponse", getAppSecTokenRes.getJSONObject("appzillonGetAppSecTokensResponse"));
            finalResponse.put("appzillonAppMasterResponse", appzillonAppMasterRes);

            pMessage.getResponseObject().setResponseJson(finalResponse);
            pMessage.getHeader().setInterfaceId(parentInterface);
            pMessage.getRequestObject().setRequestJson(totalRequest);
        } catch (AppzillonException e) {
            pMessage.getHeader().setInterfaceId(parentInterface);
            pMessage.getHeader().setStatus(false);
            LOG.error("exception occurred while launching the app", e);
            throw e;
        }
    }

    private void handleSession(Message pMessage, Header lHeader) {
        if (Utils.isNotNullOrEmpty(pMessage.getHeader().getSessionId())) {
            try {
                SessionHandler lSessionHandler = (SessionHandler) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_HANDLER);
                pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_UPDATE_SESSION_IF_EXISTS);
                LOG.debug("{} Session Handler Used {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, lSessionHandler);
                lSessionHandler.handleRequest(pMessage);
                LOG.debug("{} After Updating user Session and Request Key in DB , RequestKey -: {} and SessionID -: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER,
                        pMessage.getHeader().getRequestKey(), pMessage.getHeader().getSessionId());
            } catch (Exception e) {
                LOG.error("{} Error while Updating the session", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, e);
            }
            lHeader.setServiceType("");
        }
    }

    public void setInterfaceProp(InterfaceDetails lInterfaceDtls, Message pMessage) {
        try {
            lInterfaceDtls.setCaptchaReq(InterfaceMasterService.getInterfaceMasterMap().get(pMessage.getHeader().getAppId()).get(pMessage.getHeader().getInterfaceId()).getCaptchaReq());
            lInterfaceDtls.setCaptchaType(InterfaceMasterService.getInterfaceMasterMap().get(pMessage.getHeader().getAppId()).get(pMessage.getHeader().getInterfaceId()).getCaptchaType());
            lInterfaceDtls.setDgTxnRequired(InterfaceMasterService.getInterfaceMasterMap().get(pMessage.getHeader().getAppId()).get(pMessage.getHeader().getInterfaceId()).getDgTxnLogRequired());
            lInterfaceDtls.setTxnLogReq(InterfaceMasterService.getInterfaceMasterMap().get(pMessage.getHeader().getAppId()).get(pMessage.getHeader().getInterfaceId()).getTxnLogReq());
            lInterfaceDtls.setTxnPayLoadLogReq(InterfaceMasterService.getInterfaceMasterMap().get(pMessage.getHeader().getAppId()).get(pMessage.getHeader().getInterfaceId()).getTxnLogPayLoadReq());
            lInterfaceDtls.setFmwTxnReq(InterfaceMasterService.getInterfaceMasterMap().get(pMessage.getHeader().getAppId()).get(pMessage.getHeader().getInterfaceId()).getFmwTxnReq());
            lInterfaceDtls.setFmwTxnPayloadReq(InterfaceMasterService.getInterfaceMasterMap().get(pMessage.getHeader().getAppId()).get(pMessage.getHeader().getInterfaceId()).getFmwTxnPayloadReq());
            lInterfaceDtls.setAuthorizationReq(InterfaceMasterService.getInterfaceMasterMap().get(pMessage.getHeader().getAppId()).get(pMessage.getHeader().getInterfaceId()).getAuthorizationReq());
        } catch (NullPointerException e) {
            RouterException lRouterException = RouterException.getInstance();
            lRouterException
                    .setMessage(lRouterException.getRestExceptionMessage(RouterException.EXCEPTION_CODE.APZ_RS_009));
            lRouterException.setCode(RouterException.EXCEPTION_CODE.APZ_RS_009.toString());
            lRouterException.setPriority("1");
            LOG.error("{} Interface Details not found in Interface Master Map {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, lRouterException);
            throw lRouterException;
        }
    }

    private void validateCaptcha(Message pMessage) {
        String captchaRef = pMessage.getHeader().getCaptchaRef();
        if (Utils.isNotNullOrEmpty(captchaRef)) {
            LOG.debug("{} Requesting to validate captcha", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
            CaptchaGenerateHandler lCaptchaGenerateHandler = (CaptchaGenerateHandler) SmsStartup.getInstance().getSpringContext()
                    .getBean(pMessage.getHeader().getAppId() + "_"
                            + ServerConstants.BEAN_SMS_CAPTCHA_GENERATE);
            pMessage.getHeader().setServiceType(ServerConstants.VALIDATE_CAPTCHA);
            lCaptchaGenerateHandler.handleRequest(pMessage);
        } else {
            RouterException lRouterException = RouterException.getInstance();
            lRouterException
                    .setMessage(lRouterException.getRestExceptionMessage(RouterException.EXCEPTION_CODE.APZ_RS_008));
            lRouterException.setCode(RouterException.EXCEPTION_CODE.APZ_RS_008.toString());
            lRouterException.setPriority("1");
            LOG.error("{} Captcha Validation Failed {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, lRouterException);
            throw lRouterException;
        }
    }

    private void sanitzeRequest(Message pMessage) {
        if (ServerConstants.YES.equalsIgnoreCase(PropertyUtils
                .getPropValue(pMessage.getHeader().getAppId(),
                        ServerConstants.OWASP_SANTIZER_REQ).trim())) {
            LOG.debug("OWASP sanitization  Required");
            RequestSanitizer owaspSanitizer = RequestSanitizer
                    .getInstance(pMessage.getHeader().getAppId());
            String newInputString = owaspSanitizer.encodeRequest(pMessage
                    .getHeader().getInputString());

            LOG.debug("OWASP Validation Passed and Input string after owasp sanitization  {}", newInputString);
        } else {
            LOG.debug("OWASP sanitization not Required");
        }
    }

}
