package com.iexceed.appzillon.rest;

import com.iexceed.appzillon.dbutils.DBUtils;
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.intf.AppzillonInterface;
import com.iexceed.appzillon.intf.AppzillonInterfaceDetails;
import com.iexceed.appzillon.intf.ExternalInterfaceDtls;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.*;
import com.iexceed.appzillon.message.Error;
import com.iexceed.appzillon.message.InterfaceDetails;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.MessageFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.router.exception.RouterException;
import com.iexceed.appzillon.router.handler.ExternalServiceRequestHandler;
import com.iexceed.appzillon.router.handler.IRequestHandler;
import com.iexceed.appzillon.router.handler.RequestHandler;
import com.iexceed.appzillon.securityutils.RSACryptoUtils;
import com.iexceed.appzillon.services.AppIntialization;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.sms.iface.ISessionManager;
import com.iexceed.appzillon.sms.processor.ISMSProcessor;
import com.iexceed.appzillon.sms.processor.SMSProcessorUtils;
import com.iexceed.appzillon.sms.processor.SMSRequestProcessorImpl;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.XMLExternalEntity;
import org.apache.camel.spring.SpringCamelContext;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static com.iexceed.appzillon.utils.ServerConstants.*;

@Path("/Appzillon")
public class AppzillonRestWS {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, AppzillonRestWS.class.getName());

    @POST
    @Path("/services/{interfaceId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String processRequest(String inputString, @Context HttpServletRequest request,
                                 @Context HttpServletResponse response) {
        long st = System.currentTimeMillis();
        MDC.put(LOG_ROUTER, "Register/EncPayload");
        Message message = Message.getInstance();
        message.setIntfDtls(null);
        String outputString;
        String safeToken;
        JSONObject json = new JSONObject(inputString);
        int safeBit = 0;
        if (json.has(ServerConstants.APPZILLON_SAFE_BIT)) {
            safeBit = json.getInt(ServerConstants.APPZILLON_SAFE_BIT);
        }
        try {
            inputString = RSACryptoUtils.decryptRequestPayLoad(inputString, request, message);
            safeToken = message.getHeader().getServerToken();
        } catch (AppzillonException e) {
            if (e.getCode().equalsIgnoreCase("APZ_RS_011")) {
                LOG.error(APPZILLON_EXCEPTION, ServerConstants.LOGGER_PREFIX_RESTFULL,
                        "Error code for this request failure is " + e.getCode());
                JSONObject responseJson = new JSONObject(inputString);
                JSONObject errors = new JSONObject();
                errors.put("errorCode", e.getCode());
                errors.put("errorMessage", e.getMessage());
                responseJson.put("appzillonErrors", new JSONArray().put(errors));
                return responseJson.toString();
            }
            LOG.error(APPZILLON_EXCEPTION, ServerConstants.LOGGER_PREFIX_RESTFULL, e.getLocalizedMessage());
            message.getHeader().setStatus(false);
            message.getHeader().setSafeBit(safeBit);
            Error err = Error.getInstance();
            err.setErrorCode(e.getCode());
            err.setErrorDesc(e.getMessage());
            message.getErrors().add(err);
            outputString = MessageFactory.buildResponseJson(message);
            return outputString;
        }
        String header = JSONUtils.extractJsonString(inputString, ServerConstants.MESSAGE_HEADER);

        Map<String, String> reqHeaderMap = JSONUtils.getJsonHashMap(header);
        String userId = reqHeaderMap.get(ServerConstants.MESSAGE_HEADER_USER_ID);
        String appId = reqHeaderMap.get(ServerConstants.MESSAGE_HEADER_APP_ID);
        String osType = reqHeaderMap.get(ServerConstants.OS);

        userId = Utils.isNullOrEmpty(userId) ? "Register" : userId;

        MDC.put(LOG_ROUTER, appId + "/" + userId);
        //log pattern changes
        MDC.put(APPID, APPID_VALUE + appId);
        MDC.put(OSTYPE, OSTYPE_VALUE + osType);

        MDC.put(USERID, USERID_VALUE + userId);

        try {
            // Fetching Security Parameters
            message = MessageFactory.getMessage(inputString, null);
            MDC.put(TXNREF, TXNREF_VALUE + message.getHeader().getReqRefId());
            LOG.info("{} ###################################### Device Request Reached Server #######################################", ServerConstants.LOGGER_PREFIX_RESTFULL);
            LOG.info("{} Processing Rest request with request payload is -: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, inputString);
            LOG.debug("{} Request sent to build Appzillon Message Objects.", ServerConstants.LOGGER_PREFIX_RESTFULL);
            message.getHeader().setServerToken(safeToken);
            if (!message.getHeader().getDeviceId().equalsIgnoreCase(ServerConstants.WEB))
                message.getHeader().setOrigination(getIpAddress(request));
            initializeApp(message);
            setEncProperties(json, message);
            message.getHeader().setServiceType(ServerConstants.FETCH_SECURITY_PARAMS);
            DomainStartup.getInstance().processRequest(message);
            message.getHeader().setServiceType("");

            //AccessToken
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (Utils.isNotNullOrEmpty(authorizationHeader)) {
                if (authorizationHeader.contains(ServerConstants.BEARER_TOKEN)) {//For authorization.
                    authorizationHeader = authorizationHeader.substring(7);
                } else if (authorizationHeader.contains(ServerConstants.BASIC_AUTH)) {//For authentication
                    authorizationHeader = authorizationHeader.substring(6);
                    message.getHeader().setUserId(Utils.getUserIdFromAuthString(authorizationHeader));
                }
                message.getHeader().setUserAppAccessToken(authorizationHeader);
            }

            // Processing requests
            LOG.debug("{} Message built and processing request", ServerConstants.LOGGER_PREFIX_RESTFULL);
            IRequestHandler iHandler = new RequestHandler();
            LOG.info("{} Routing To Appzillon Request Handler..", ServerConstants.LOGGER_PREFIX_RESTFULL);
            message.getHeader().setInputString(inputString);
            iHandler.handleRequest(message);
            LOG.info("{} Request Handler Completed task and building response..", ServerConstants.LOGGER_PREFIX_RESTFULL);
            outputString = MessageFactory.buildResponseJson(message);
        } catch (AppzillonException e) {
            LOG.error(APPZILLON_EXCEPTION, ServerConstants.LOGGER_PREFIX_RESTFULL, e.getLocalizedMessage());
            message.getHeader().setStatus(false);
            Error err = Error.getInstance();
            err.setErrorCode(e.getCode());
            err.setErrorDesc(e.getMessage());
            message.getErrors().add(err);
            outputString = MessageFactory.buildResponseJson(message);
        }
        LOG.info("{} Response From Appzillon Server {}", ServerConstants.LOGGER_PREFIX_RESTFULL, outputString);
        LOG.info("{} ############################# Response sent From Appzillon and Processing time in ms {} ######################################################\n\n",
                ServerConstants.LOGGER_PREFIX_RESTFULL, (System.currentTimeMillis() - st));

        return outputString;

    }

    private void setEncProperties(JSONObject jsonPayload, Message message) {
        LOG.debug("Setting encryption properties");
        String encryptionFlag = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.ENCRYPTION_FLAG);
        if (ServerConstants.NO.equalsIgnoreCase(encryptionFlag)) {
            JSONObject requestPayLoad = jsonPayload.getJSONObject(ServerConstants.MESSAGE_HEADER);
            if (requestPayLoad.has(ServerConstants.APPZILLON_SAFE_BIT))
                message.getHeader().setSafeBit(Integer.parseInt(requestPayLoad.getString(ServerConstants.APPZILLON_SAFE_BIT)));
            if (requestPayLoad.has(ServerConstants.APPZILLON_KEY_LEN))
                message.getHeader().setEncyKeyLen(Integer.parseInt(requestPayLoad.getString(ServerConstants.APPZILLON_KEY_LEN)));
            if (requestPayLoad.has(ServerConstants.APPZILLON_ENC_MODE))
                message.getHeader().setEncMode(Integer.parseInt(requestPayLoad.getString(ServerConstants.APPZILLON_ENC_MODE)));
        } else if (ServerConstants.YES.equalsIgnoreCase(encryptionFlag)) {
            if (jsonPayload.has(ServerConstants.APPZILLON_SAFE_BIT))
                message.getHeader().setSafeBit(Integer.parseInt(jsonPayload.getString(ServerConstants.APPZILLON_SAFE_BIT)));
            if (jsonPayload.has(ServerConstants.APPZILLON_KEY_LEN))
                message.getHeader().setEncyKeyLen(Integer.parseInt(jsonPayload.getString(ServerConstants.APPZILLON_KEY_LEN)));
            if (jsonPayload.has(ServerConstants.APPZILLON_ENC_MODE))
                message.getHeader().setEncMode(Integer.parseInt(jsonPayload.getString(ServerConstants.APPZILLON_ENC_MODE)));
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String processRequestBack(String inputString, @Context HttpServletRequest request,
                                     @Context HttpServletResponse response) {
        return this.processRequest(inputString, request, response);
    }

    private void initializeApp(Message pMessage) {
        if (Utils.isNullOrEmpty(AppIntialization.getAppsIntializationStatus().get(pMessage.getHeader().getAppId()))) {
            AppIntialization.intializationOfAppAtRuntime(pMessage);
        }
    }

    @GET
    @Path("/SMSIn")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String processSMSIn(String inputString, @Context HttpServletRequest request,
                               @Context HttpServletResponse response/*
     * , @Context HttpHeaders
     * headers
     */, @QueryParam("mobileNumber") String mobileNumber,
                               @QueryParam("message") String message, @QueryParam("messageId") String messageId) throws JSONException {
        MDC.put(LOG_ROUTER, "SMSSupport/" + mobileNumber);
        //log pattern changes
        MDC.put(APPID, APPID_VALUE);
        MDC.put(OSTYPE, OSTYPE_VALUE);
        MDC.put(TXNREF, TXNREF_VALUE);
        MDC.put(USERID, USERID_VALUE);

        DocumentBuilder dBuilder;
        LOG.debug("Inside processSMSIn....");
        LOG.debug("{} SMSIn query parameters Mobile Number : {}, Message: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, mobileNumber, message);
        String ip = getIpAddress(request);
        LOG.debug("Remote_Addr : {}", ip);

        Message lMessage = getMessageObject(mobileNumber, message, messageId, ip);
        LOG.debug("Going to Log Txn For SMS after getting Message object..");
        DomainStartup.getInstance().processRequest(lMessage);
        String result = "";
        SMSRequestProcessorImpl lsmsProcessor = new SMSRequestProcessorImpl();
        try {
            String lservicetype = SMSProcessorUtils.serviceTypeIdentifier(message);
            String path = Utils.getSmsServiceXmlFile(lservicetype);
            try (InputStream isr = SMSRequestProcessorImpl.class.getClassLoader().getResourceAsStream(path)) {
                /*Veracode fix: Improper Restriction of XML External Entity Reference (CWE ID 611)*/
                dBuilder = XMLExternalEntity.getDocBuilder();
                if (isr == null)
                    throw new IOException("resource not found");
                Document doc = dBuilder.parse(isr);
                doc.getDocumentElement().normalize();
                String appId = lsmsProcessor.getIdByType(doc, ServerConstants.MESSAGE_HEADER_APP_ID);
                String iterfaceId = lsmsProcessor.getIdByType(doc, ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
                SpringCamelContext context = ExternalServicesRouter.getCamelContext();
                LOG.debug("{} Injecting interface bean from Camel context.", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
                ISMSProcessor smsProcessor = (ISMSProcessor) context.getApplicationContext()
                        .getBean(appId + "_" + iterfaceId + "_smsImpl");
                result = smsProcessor.process(mobileNumber, message, messageId, request);

                JSONObject body = new JSONObject();
                body.put(ServerConstants.MOBILENUMBER, mobileNumber);
                body.put(ServerConstants.MESSAGE, message);
                body.put(ServerConstants.JMS_MSG_ID, messageId);
                body.put(ServerConstants.RESPONSE, result);
                LOG.debug("Setting Response Body, and going to update response");
                lMessage.getRequestObject().setRequestJson(body);
                DomainStartup.getInstance().processRequest(lMessage);
            }
        } catch (Exception e) {
            LOG.error("Exception: ", e);
        }
        return result;
    }

    private Message getMessageObject(String pMobileNumber, String pMsg, String pMsgId, String pOrigination) {
        LOG.debug("inside getMessageObject()..");
        JSONObject body = new JSONObject();
        body.put(ServerConstants.MOBILENUMBER, pMobileNumber);
        body.put(ServerConstants.MESSAGE, pMsg);
        body.put(ServerConstants.JMS_MSG_ID, pMsgId);
        JSONObject messageJson = new JSONObject();
        messageJson.put(ServerConstants.MESSAGE_HEADER, new JSONObject());
        messageJson.put(ServerConstants.MESSAGE_BODY, new JSONObject());
        Message messageObj = MessageFactory.getMessage(messageJson.toString(), null);
        messageObj.getHeader().setUserId(pMobileNumber);
        messageObj.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_SMS_TXN);
        messageObj.getHeader().setServiceType(ServerConstants.LOG_TRANSACTION);
        messageObj.getHeader().setOrigination(pOrigination);
        messageObj.getRequestObject().setRequestJson(body);
        return messageObj;
    }

    @POST
    @Path("/downloadFile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public FormDataMultiPart downloadFile(String requestJSONString, @Context HttpServletRequest request,
                                          @Context HttpServletResponse response) throws JSONException {
        FormDataMultiPart multipart = null;
        JSONObject mRequest = null;
        JSONObject requestJSON = new JSONObject(requestJSONString);
        request.setAttribute(ServerConstants.ENCRYPTION_FLAG, ServerConstants.NO);
        //FileDownload customization
        String lHeaderIntfId = JSONUtils.getJsonValueFromObject(requestJSON.getJSONObject(MESSAGE_HEADER), MESSAGE_HEADER_INTERFACE_ID);
        String lBodyIntfId = JSONUtils.getJsonValueFromObject(requestJSON.getJSONObject(MESSAGE_BODY), MESSAGE_HEADER_INTERFACE_ID);
        if (Utils.isNotNullOrEmpty(lBodyIntfId)) {
            Message pMessage = MessageFactory.getMessage(requestJSONString, null);
//            pMessage.getRequestObject().setRequestJson(requestJSON.getJSONObject(MESSAGE_BODY));
            pMessage.getIntfDtls().setInterfaceId(lBodyIntfId);
            pMessage.getHeader().setInterfaceId(lBodyIntfId);
            loadExtensibilityFileDownloadInterFaceDtls(pMessage);
            validateSessionForMultipartFileUploadDownload(pMessage);
//            requestJSON.getJSONObject(ServerConstants.MESSAGE_HEADER).put(MESSAGE_HEADER_INTERFACE_ID, lBodyIntfId);
            LOG.info("{} Routing To External Service Request Handler", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
            IRequestHandler externalServiceHandler = new ExternalServiceRequestHandler();
            externalServiceHandler.handleRequest(pMessage);
            LOG.debug("{} Response From External Service Request Handler : {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getResponseObject().getResponseJson());
            pMessage.getIntfDtls().setInterfaceId(lHeaderIntfId);
            multipart = pMessage.getFormDataMultiPart();

        } else {
            String outputString = new AppzillonRestWS().processRequest(requestJSON.toString(), request, response);
            requestJSON = new JSONObject(outputString);
            JSONObject appzillonHeader = requestJSON.getJSONObject(ServerConstants.MESSAGE_HEADER);
            JSONObject appzillonBody = requestJSON.getJSONObject(ServerConstants.MESSAGE_BODY);
            String rootpath = PropertyUtils.getPropValue(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                    ServerConstants.FILE_UPLOAD_LOCATION);
            if (rootpath.endsWith("/")) {
                rootpath = rootpath.substring(0, rootpath.lastIndexOf('/'));
            }
            if (rootpath.endsWith("\\")) {
                rootpath = rootpath.substring(0, rootpath.lastIndexOf('\\'));
            }
            if (appzillonBody.has(ServerConstants.FILEPUSHSERVICERESPONSE)) {
                mRequest = appzillonBody.getJSONObject(ServerConstants.FILEPUSHSERVICERESPONSE);
            } else if (appzillonBody.has(ServerConstants.FILEPUSHSERVICEWSRESPONSE)) {
                mRequest = appzillonBody.getJSONObject(ServerConstants.FILEPUSHSERVICEWSRESPONSE);
            }
            String fileName = "";
            String filePath = "";
            if (mRequest != null) {
                fileName = mRequest.get(ServerConstants.REPORT_FILENAME).toString();
                filePath = mRequest.get(ServerConstants.FILEPATH).toString();
            }
            LOG.debug("Filename in request body :: {}", fileName);
            multipart = new FormDataMultiPart();
            try {
                String fPath = String.format("%s/%s/%s", rootpath, filePath, fileName);
                getFileContents(multipart, fPath, outputString);
                return multipart;

            } catch (AppzillonException ex) {
                LOG.error("{} Abstract Appzillon Exception caught", ServerConstants.LOGGER_PREFIX_RESTFULL);
                LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, ex);
                outputString = getErrorObj(ex, appzillonHeader, ex.getCode());

            } catch (Exception e) {
                LOG.error("{} Caught Exception from server {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e);
                outputString = getErrorObj(e, appzillonHeader, "EX_0");
            }
            multipart.field("responseJSON", outputString);
        }
        return multipart;
    }


    private void getFileContents(FormDataMultiPart multipart, String fPath, String outputString) {
        try {
            InputStream fis = new FileInputStream(fPath);
            multipart.field("responseJSON", outputString);
            multipart.field("fileContents", fis, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        } catch (IOException e) {
            LOG.error("File not Found at Uploaded Location: ", e);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_028));
            dexp.setCode(DomainException.Code.APZ_DM_028.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    // Multipart file upload
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadMultipartFile(FormDataMultiPart multiPart, @Context HttpServletRequest request,
                                        @Context HttpServletResponse response) throws JSONException {
        String outputString;
        Message lMessage;

        FormDataBodyPart requestJSONFormData = multiPart.getField(ServerConstants.UPLOAD_REQ);
        String appzillonRequestString = requestJSONFormData.getEntityAs(String.class);
        JSONObject appzillonRequest = new JSONObject(appzillonRequestString);
        JSONObject appzillonHeader = appzillonRequest.getJSONObject(ServerConstants.MESSAGE_HEADER);
        JSONObject appzillonBody = appzillonRequest.getJSONObject(ServerConstants.MESSAGE_BODY);
        String inputString = appzillonRequest.toString();
        String userId = "null";
        if (!appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)
                .equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS)) {
            userId = appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
        }
        appzillonHeader.put(ServerConstants.MESSAGE_HEADER_USER_ID, userId);
        MDC.put(LOG_ROUTER, appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_APP_ID).trim() + "/" + userId.trim());
        //log pattern changes
        MDC.put(APPID, APPID_VALUE);
        MDC.put(OSTYPE, OSTYPE_VALUE);
        MDC.put(TXNREF, TXNREF_VALUE);
        MDC.put(USERID, "USERID: " + userId.trim());

        String ifaceIdBody = JSONUtils.getJsonValueFromKey(appzillonBody.toString(), ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
        if (ifaceIdBody != null && !ifaceIdBody.isEmpty()) {
            LOG.debug("interfaceId in body is not null and is : {}", ifaceIdBody);
            return processRequestMultipart(inputString, multiPart);
        } else {
            String appzillonheader = appzillonHeader.toString();
            String appzillonbody = appzillonBody.toString();
            LOG.debug("{} appzillonHeader :: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, appzillonheader);
            LOG.debug("{} appzillonBody :: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, appzillonbody);

            try {
                if (!appzillonheader.isEmpty()) {
                    lMessage = prepareHeader(request, appzillonHeader);

                    LOG.debug("{} Message built -: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, lMessage);
                } else {
                    LOG.error("{} No AppzillonHeader Value Found ,No Proper Request Format for file upload.", ServerConstants.LOGGER_PREFIX_RESTFULL);
                    RouterException re = RouterException.getInstance();
                    re.setCode(RouterException.EXCEPTION_CODE.APZ_RS_006.toString());
                    re.setMessage(re.getRestExceptionMessage(RouterException.EXCEPTION_CODE.APZ_RS_006));
                    throw re;
                }

                String ifaceId = appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
                if (!ifaceId.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS)) {
                    LOG.debug("{} loading bean : {}_{}", ServerConstants.LOGGER_PREFIX_RESTFULL, lMessage.getHeader().getAppId(), ServerConstants.BEAN_SMS_SESSION_MANAGER);
                    ISessionManager sessionHandler = (ISessionManager) SmsStartup.getInstance().getSpringContext()
                            .getBean(lMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_MANAGER);
                    String prevReqKey = lMessage.getHeader().getRequestKey();
                    LOG.debug("{} requestKey object : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, prevReqKey);
                    sessionHandler.validateSession(lMessage);
                    LOG.debug("{} requestKey object validateSession:: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, lMessage.getHeader().getRequestKey());
                    LOG.debug("{} Setting previous request key", ServerConstants.LOGGER_PREFIX_RESTFULL);
                    lMessage.getHeader().setRequestKey(prevReqKey);
                    appzillonHeader.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY,
                            lMessage.getHeader().getRequestKey());
                }

                String filePath = UploadService.getFileUploadLocation(lMessage.getHeader().getAppId());

                JSONArray filedetailsarray = appzillonBody.getJSONArray("fileDetails");
                String overrideFlag = appzillonBody.getString(ServerConstants.OVERRIDE);
                String destination = appzillonBody.getString("destination");
                filePath = filePath + File.separator + destination;
                LOG.debug("{} overrideFlag  found to be {}", ServerConstants.LOGGER_PREFIX_RESTFULL, overrideFlag);
                int maxFileSize = UploadService.getMaxFileSize(lMessage.getHeader().getAppId());
                boolean uploadstatus = UploadService.writeToFile(multiPart, filePath, filedetailsarray,
                        overrideFlag, maxFileSize);
                LOG.debug("{} Upload status :: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, uploadstatus);
                JSONArray jsonfiles = UploadService.createServerRequest(appzillonHeader, filedetailsarray, destination,
                        overrideFlag);
                outputString = UploadService.createAndSendRequestJSON(appzillonHeader,
                        jsonfiles, request, response);

            } catch (AppzillonException ex) {
                LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, ex);
                outputString = getErrorObj(ex, appzillonHeader, ex.getCode());

            } catch (Exception e) {
                LOG.error("{} Caught Exception from server {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e);
                outputString = getErrorObj(e, appzillonHeader, "EX_0");
            }
            return Response.status(200).entity(outputString).build();
        }
    }

    private Message prepareHeader(HttpServletRequest request, JSONObject appzillonHeader) {
        Message lMessage;
        lMessage = Message.getInstance();
        lMessage.getHeader().setAppId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        lMessage.getHeader()
                .setInterfaceId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));

        if (!appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)
                .equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS)) {
            lMessage.getHeader()
                    .setSessionId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_SESSION_ID));
        }
        lMessage.getHeader().setUserId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
        lMessage.getHeader()
                .setDeviceId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
        lMessage.getIntfDtls()
                .setInterfaceId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
        lMessage.getIntfDtls().setCategory(ServerConstants.INTERFACE_CATEGORY_INTERNAL);
        if (!lMessage.getHeader().getDeviceId().equalsIgnoreCase(ServerConstants.WEB)) {
            String origination = getIpAddress(request);
            lMessage.getHeader().setOrigination(origination);
        } else {
            lMessage.getHeader()
                    .setOrigination(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_ORIGINATION));
        }
        return lMessage;
    }

    private String getErrorObj(Object ex, JSONObject appzillonHeader, String code) {
        JSONArray errorArray = new JSONArray();
        JSONObject error = new JSONObject();
        error.put(ServerConstants.MESSAGE_HEADER_ERROR_CODE, code);
        if (ex instanceof AppzillonException appzillonException) {
            error.put(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE, appzillonException.getMessage());
        } else if (ex instanceof Exception exception) {
            error.put(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE, exception.getMessage());
        }

        errorArray.put(error);
        appzillonHeader.put(ServerConstants.MESSAGE_HEADER_STATUS, false);
        JSONObject resObject = new JSONObject();
        resObject.put(ServerConstants.MESSAGE_HEADER, appzillonHeader);
        resObject.put(ServerConstants.MESSAGE_ERROR, errorArray);
        return resObject.toString();
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response serverHealth(String requestJSONString, @Context HttpServletRequest request,
                                 @Context HttpServletResponse response) {
        MDC.put(LOG_ROUTER, "health/health");
        MDC.put(APPID, APPID_VALUE);
        MDC.put(OSTYPE, OSTYPE_VALUE);
        MDC.put(TXNREF, TXNREF_VALUE);
        MDC.put(USERID, "USERID: health");
        LOG.debug("{} inside server health service", ServerConstants.LOGGER_PREFIX_RESTFULL);
        int statusCode = 200;
        String message = "Success";
        Connection lConnection = null;
        try {
            lConnection = DBUtils.getConnectionFromDataSource(PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.JNDI_DATA_SOURCE));
            if (lConnection == null || !lConnection.isValid(1)) {
                statusCode = 421;
                message = "Failed to connect to database";
            }

        } catch (Exception exception) {
            statusCode = 500;
            LOG.error("{} Exception: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, exception);
        } finally {
            try {
                if (lConnection != null) {
                    lConnection.close();
                }
            } catch (SQLException e) {
                LOG.error("{} SQLException: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e);
                try {
                    lConnection.close();
                } catch (SQLException e1) {
                    LOG.error("{} SQLException -: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e1);
                }
            }
        }

        return Response.status(statusCode).entity(message).build();
    }

    @GET
    @Path("/reload")
    public Response reloadServerProps() {
        MDC.put(LOG_ROUTER, "AppStartUp");
        LOG.debug("{} reloading server properties", ServerConstants.LOGGER_PREFIX_RESTFULL);
        String message = "Success";
        PropertyUtils.resetPropertiesObjBeforeReload();
        PropertyUtils.loadServerProperties();
        return Response.ok().entity(message).build();
    }


    public Response processRequestMultipart(String inputString, FormDataMultiPart multiPart) {
        long st = System.currentTimeMillis();
        String outputString;
        String header = JSONUtils.extractJsonString(inputString, ServerConstants.MESSAGE_HEADER);

        Map<String, String> reqHeaderMap = JSONUtils.getJsonHashMap(header);
        String userId = reqHeaderMap.get(ServerConstants.MESSAGE_HEADER_USER_ID);
        String appId = reqHeaderMap.get(ServerConstants.MESSAGE_HEADER_APP_ID);
        String osType = reqHeaderMap.get(ServerConstants.OS);
        if ("".equals(userId)) {
            userId = "Register";
        }

        MDC.put(LOG_ROUTER, appId + "/" + userId);
        MDC.put(APPID, APPID_VALUE + appId);
        MDC.put(OSTYPE, OSTYPE_VALUE + osType);
        MDC.put(TXNREF, TXNREF_VALUE);
        MDC.put(USERID, USERID_VALUE + userId);

        LOG.info("{} ***************************** Device Request Reached Server *******************************************", ServerConstants.LOGGER_PREFIX_RESTFULL);
        LOG.info("{} Processing Rest request with request payload is -: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, inputString);
        LOG.debug("{} Request send to build Appzillon Message Objects.", ServerConstants.LOGGER_PREFIX_RESTFULL);
        Message message = MessageFactory.getMessage(inputString, multiPart);
        message.getHeader().setServiceType(ServerConstants.FETCH_SECURITY_PARAMS);
        try {
            DomainStartup.getInstance().processRequest(message);
            message.getHeader().setServiceType("");
            //Processing requests
            IRequestHandler iHandler = new RequestHandler();

            String headerIfaceId = message.getHeader().getInterfaceId();
            //TODO
            String lBodyInterfaceId = message.getRequestObject().getRequestJson().getString(MESSAGE_HEADER_INTERFACE_ID);
            message.getHeader().setInterfaceId(lBodyInterfaceId);
            JSONObject appzillonBody=new JSONObject();
            appzillonBody.put("appzillonBody",message.getRequestObject().getRequestJson());
            message.getRequestObject().setRequestJson(appzillonBody);
            loadExtensibilityFileDownloadInterFaceDtls(message);
            JSONObject jSONObject = new JSONObject(inputString);
            jSONObject.getJSONObject(ServerConstants.MESSAGE_BODY).put(INTERFACE_TYPE, FILE_UPLOAD);
            LOG.debug("Before message.getRequestObject().getRequestJson() -:" + message.getRequestObject().getRequestJson());
            message.getRequestObject().setRequestJson(new JSONObject().put(MESSAGE_BODY, jSONObject.getJSONObject(ServerConstants.MESSAGE_BODY)));
            LOG.debug("After message.getRequestObject().getRequestJson() -:" + message.getRequestObject().getRequestJson());
            message.setIntfDtls(null);

            //* License agreement validation is removed by Samy on 18/05/2015
            LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Routing To Appzillon Request Handler");
            iHandler.handleRequest(message);
            LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Request Handler Completed task");
            message.getHeader().setInterfaceId(headerIfaceId);

            outputString = MessageFactory.buildResponseJson(message);
        } catch (AppzillonException e) {
            LOG.error(APPZILLON_EXCEPTION, ServerConstants.LOGGER_PREFIX_RESTFULL, e);
            message.getHeader().setStatus(false);
            Error err = Error.getInstance();
            err.setErrorCode(e.getCode());
            err.setErrorDesc(e.getMessage());
            message.getErrors().add(err);
            outputString = MessageFactory.buildResponseJson(message);
        }

        LOG.info("{} Response From Appzillon Server {}", ServerConstants.LOGGER_PREFIX_RESTFULL, outputString);
        LOG.info("{} ***************************** Response sent From Appzillon and Processing time in ms {} *******************************************\n\n"
                , ServerConstants.LOGGER_PREFIX_RESTFULL, (System.currentTimeMillis() - st));
        return Response.status(200).entity(outputString).header("X-Frame-Options", "DENY")
                .header("X-XSS-Protection", "1; mode=block").header("X-Content-Type-Options", "nosniff")
                .header("Strict-Transport-Security", "max-age=31536000; includeSubDomains").build();

    }

    private String getIpAddress(HttpServletRequest request) {
        LOG.debug("{} Inside getIpAddress.", ServerConstants.LOGGER_PREFIX_RESTFULL);

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-real-ip");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        LOG.debug("{} Return IP Address : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, ip);
        return ip;
    }


    void validateSessionForMultipartFileUploadDownload(Message lMessage) {
//        Header appzillonHeader = lMessage.getHeader();
        LOG.debug("{} loading bean : {}_{}", ServerConstants.LOGGER_PREFIX_RESTFULL, lMessage.getHeader().getAppId(), ServerConstants.BEAN_SMS_SESSION_MANAGER);
        ISessionManager sessionHandler = (ISessionManager) SmsStartup.getInstance().getSpringContext()
                .getBean(lMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_MANAGER);
        String prevReqKey = lMessage.getHeader().getRequestKey();
        LOG.debug("{} requestKey object : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, prevReqKey);
        sessionHandler.validateSession(lMessage);
        LOG.debug("{} requestKey object validateSession:: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, lMessage.getHeader().getRequestKey());
        LOG.debug("{} Setting previous request key", ServerConstants.LOGGER_PREFIX_RESTFULL);
        lMessage.getHeader().setRequestKey(prevReqKey);
/*        appzillonHeader.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY,
                lMessage.getHeader().getRequestKey());*/
    }

    void loadExtensibilityFileDownloadInterFaceDtls(Message pMessage) {
        InterfaceDetails lInterfaceDtls = pMessage.getIntfDtls();
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
        LOG.debug("{} AppzillonBody -: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage.getRequestObject().getRequestJson().toString());
        pMessage.getRequestObject().setRequestJson(pMessage.getRequestObject().getRequestJson());

        LOG.debug("{} Updated interface details Request InterfaceID -: {} and SessionRequired is -: {} and Category -: {} and Type -: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER,
                lInterfaceDtls.getInterfaceId(), lInterfaceDtls.getSessionRequired(), lInterfaceDtls.getCategory(), lInterfaceDtls.getType());
        LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Updated interface details Request InterfaceID -:" + lInterfaceDtls.getInterfaceId() + " and SessionRequired is -:" + lInterfaceDtls.getSessionRequired()
                + " and Category -:" + lInterfaceDtls.getCategory() + " and Type -:" + lInterfaceDtls.getType());
        //Setting additional Interface Properties
        new RequestHandler().setInterfaceProp(lInterfaceDtls, pMessage);
        //Setting inteface details to the header
        pMessage.setIntfDtls(lInterfaceDtls);
    }

}
