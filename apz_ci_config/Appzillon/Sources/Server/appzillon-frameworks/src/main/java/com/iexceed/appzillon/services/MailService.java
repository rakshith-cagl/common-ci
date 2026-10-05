package com.iexceed.appzillon.services;

import com.iexceed.appzillon.dao.MailDetails;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;
import com.iexceed.appzillon.utils.mail.AsyncMailListener;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ResolveEndpointFailedException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.util.URISupport;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public class MailService implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    MailService.class.getName());
    protected MailDetails mailDetails = null;

    @Override
    public Object callService(Message pMessage, Object pRequestPayLoad,
                              SpringCamelContext pContext) {

        getMailDetails(pMessage, pContext);
        JSONObject json = (JSONObject) buildRequest(pMessage, pRequestPayLoad,
                pContext);
        String uri = createURIByParams(pMessage, json);
        Exchange exchange = null;

        final String lBodymsg = json
                .getString(ServerConstants.MAIL_CONSTANTS_BODY);
        final String appId = pMessage.getHeader().getAppId();
        final String userId = pMessage.getHeader().getUserId();
        final String reqRef = pMessage.getHeader().getReqRefId();
        final String sNonce = pMessage.getHeader().getServerNonce();
        final String sessionId = pMessage.getHeader().getSessionId();
        final String osType = pMessage.getHeader().getOs();

        LOG.info("{} direct start endpoint: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, uri);
        String type = pMessage.getIntfDtls().getType();
        String responseCode = ServerConstants.SUCCESS;
        Object requestPayload = null;
        Object responsepayload = null;
        pMessage.getIntfDtls().setType(ServerConstants.APPZILLON_ROOT_MAIL_TYPE);

        try {
            uri = URISupport.normalizeUri(uri);
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            LOG.error(ServerConstants.LOGGER_FRAMEWORKS, e);
        }

        ProducerTemplate producer = ExternalServicesRouter
                .createProducerTemplate();

        try {
            LOG.debug("{} Checking whether mail has to be sent asynchronously or not.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            Utils.setExtTime(pMessage, "S");
            if (pMessage.getHeader().isAsyncMail()) {
                LOG.debug("{} Sending mail asynchronously...", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                AsyncMailListener callback = new AsyncMailListener();
                producer.asyncCallback(uri, new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        exchange.setProperty(Exchange.CHARSET_NAME, "UTF-8");
                        exchange.getIn().getHeaders().put("Content-Type", "text/html");
                        exchange.getIn().setBody(lBodymsg);
                        exchange.setProperty("appId", appId);
                        exchange.setProperty("userId", userId);
                        exchange.setProperty("txnRef", reqRef);
                        exchange.setProperty("sNonce", sNonce);
                        exchange.setProperty("sessionId", sessionId);
                        exchange.setProperty("pMessage", pMessage);
                        exchange.setProperty("payload", lBodymsg);
                        exchange.setProperty("osType", osType);
                    }
                }, callback);
                LOG.debug("{} Calling asyncMailListener to log response/exception.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            } else {
                requestPayload = lBodymsg;
                Utils.setExtTime(pMessage, "S");
                exchange = producer.request(uri, new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        exchange.setProperty(Exchange.CHARSET_NAME, "UTF-8");
                        exchange.getIn().getHeaders().put("Content-Type", "text/html");
                        exchange.getIn().setBody(lBodymsg);
                    }
                });
                LOG.info("{} Exchange completed", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                Utils.setExtTime(pMessage, "E");
                responsepayload = "Mail has been delivered successfully";
                if (exchange.getException() == null) {
                    ServicesUtil.processFmwTxnDetails(pMessage, responseCode, responsepayload, requestPayload);
                }
                if (exchange.getException() != null) {
                    LOG.error("Exchange Exception", exchange.getException());
                    ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                    responseCode = ServerConstants.ERROR;
                    responsepayload = exchange.getException().getMessage();
                    ServicesUtil.processFmwTxnDetails(pMessage, responseCode, responsepayload, requestPayload);

                    // handled if authentication is failed
                    if (exchange.getException() instanceof javax.mail.AuthenticationFailedException) {
                        exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_035.toString());
                        exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_035));
                        exsrvcallexp.setPriority("1");
                        throw exsrvcallexp;
                    }

                    // handled if missing domain in mail address
                    if (exchange.getException() instanceof javax.mail.internet.AddressException) {
                        exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_036.toString());
                        exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_036));
                        exsrvcallexp.setPriority("1");
                        throw exsrvcallexp;
                    }

                    //10-7-2014  : below change is to handle exception when user enters invalid email address
                    if (exchange.getException() instanceof javax.mail.SendFailedException) {
                        exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_025.toString());
                        exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_025));
                        exsrvcallexp.setPriority("1");
                        throw exsrvcallexp;

                    } else {
                        exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_023.toString());
                        exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_023));
                    }
                    exsrvcallexp.setPriority("1");
                    throw exsrvcallexp;
                }
            }
        } catch (ResolveEndpointFailedException ex) { // 29-9-2015 handled if Mail details not found
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                    .getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_044.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_044));
            exsrvcallexp.setPriority("1");
            LOG.error("{} ResolveEndpointFailedException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
            throw exsrvcallexp;
        }
        pMessage.getIntfDtls().setType(type);
        JSONObject lRespJson = new JSONObject();
        lRespJson.put(ServerConstants.MESSAGE_HEADER_STATUS,
                ServerConstants.SUCCESS);
        String resobj = (String) processResponse(pMessage,
                lRespJson.toString(), pContext);
        LOG.debug("{} Response Object after processing response {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, resobj);
        return new JSONObject(resobj);
    }

    @Override
    public Object buildRequest(Message pMessage, Object pRequestPayLoad, SpringCamelContext pContext) {
        JSONObject lPayloadobj = (JSONObject) pRequestPayLoad;
        JSONObject lEmailobj = null;

        LOG.debug("{} Inside  buildRequest with payload {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lPayloadobj);
		/*if (lPayloadobj.has(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside password reset..");
			lEmailobj = lPayloadobj
					.getJSONObject(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES);
			lEmailobj.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, "password");

		} else if (lPayloadobj
				.has(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside createuser...");
			lEmailobj = lPayloadobj
					.getJSONObject(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES);
		}*/ //else {
        lEmailobj = lPayloadobj
                .getJSONObject(ServerConstants.INTERFACE_ID_MAIL_REQ);
        if (lEmailobj.has(ServerConstants.APPZILLON_ROOT_PIN_RESET_RES)) {
            lEmailobj = lEmailobj
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_PIN_RESET_RES);
        }

        //}
        if (lEmailobj.has(ServerConstants.MAIL_CONSTANTS_EMAIL_ID)) {
            LOG.debug("{} Mail to be sent to Email Id {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lEmailobj.getString(ServerConstants.MAIL_CONSTANTS_EMAIL_ID));
        } else {
            LOG.error("{} mailId is not found in request...", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_012.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_012));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        if (!lEmailobj.has(ServerConstants.MAIL_CONSTANTS_CC)) {
            lEmailobj.put(ServerConstants.MAIL_CONSTANTS_CC, "");
        }
        if (!lEmailobj.has(ServerConstants.MAIL_CONSTANTS_BCC)) {
            lEmailobj.put(ServerConstants.MAIL_CONSTANTS_BCC, "");
        }
        if (!lEmailobj.has(ServerConstants.MAIL_CONSTANTS_SUBJECT)) {
            lEmailobj.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, "");
        }
        if (!lEmailobj.has(ServerConstants.MAIL_CONSTANTS_BODY)) {
            lEmailobj.put(ServerConstants.MAIL_CONSTANTS_BODY, "");
        }
        String msg = "";
        if (Utils.isNullOrEmpty(lEmailobj.getString(ServerConstants.MAIL_CONSTANTS_BODY))
                && !lPayloadobj.has(ServerConstants.INTERFACE_ID_MAIL_REQ)) {
            lEmailobj.put(ServerConstants.MAIL_CONSTANTS_BODY, msg);
        }
        LOG.debug("{} Data build from build request {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lPayloadobj);
        return lEmailobj;

    }

    @Override
    public Object processResponse(Message pMessage, Object pResponse, SpringCamelContext pContext) {
        return pResponse;
    }

    public String createURIByParams(Message pMessage, JSONObject json) {
        LOG.debug("{} protocol used : {} port used : {} from user: {}",
                ServerConstants.LOGGER_PREFIX_FRAMEWORKS, mailDetails.getProtocol(), mailDetails.getPortNumber(), mailDetails.getFrom());
        String toEmailId = json.getString(ServerConstants.MAIL_CONSTANTS_EMAIL_ID);
        String str = mailDetails.getProtocol()
                + ServerConstants.MAIL_URL_SEPARATOR_COLON
                + ServerConstants.MAIL_URL_SEPARATOR_DOUBLE_SLASH
                + mailDetails.getHostName()
                + ServerConstants.MAIL_URL_SEPARATOR_COLON
                + mailDetails.getPortNumber()
                + ServerConstants.MAIL_URL_CONSTANTS_TO
                + "RAW(" + toEmailId + ")"
                + ServerConstants.MAIL_URL_CONSTANTS_SUBJECT
                + json.getString(ServerConstants.MAIL_CONSTANTS_SUBJECT)
                + ServerConstants.MAIL_URL_CONSTANTS_FROM
                + mailDetails.getFrom();
        StringBuilder defaultURI = new StringBuilder(str);
        String appendUserIDPassURI = ServerConstants.MAIL_URL_CONSTANTS_USER_NAME
                + mailDetails.getUserName()
                + ServerConstants.MAIL_URL_CONSTANTS_PIN
                + mailDetails.getPassword();
        if (Utils.isNotNullOrEmpty(json.getString(ServerConstants.MAIL_CONSTANTS_CC))) {
            LOG.info("{} CC is not null", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            defaultURI.append(ServerConstants.MAIL_URL_CONSTANTS_CC
                    + json.getString(ServerConstants.MAIL_CONSTANTS_CC));
        }
        if (Utils.isNotNullOrEmpty(json.getString(ServerConstants.MAIL_CONSTANTS_BCC))) {
            LOG.info("{} BCC is not null", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            defaultURI.append(ServerConstants.MAIL_URL_CONSTANTS_BCC
                    + json.getString(ServerConstants.MAIL_CONSTANTS_BCC));
        }
        if (Utils.isNotNullOrEmpty(mailDetails.getUserName()) || Utils.isNotNullOrEmpty(mailDetails.getPassword())) {
            defaultURI.append(appendUserIDPassURI);
        }
        String startTls = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.startTls);
        if (Utils.isNotNullOrEmpty(startTls) && ServerConstants.YES.equalsIgnoreCase(startTls)) {
            defaultURI.append("&mail.smtp.starttls.enable=true");
        }

        LOG.info("{} final URI formed : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, defaultURI);
        return defaultURI.toString();
    }

    public void getMailDetails(Message pMessage, SpringCamelContext pContext) {
        LOG.debug("{} Mail Details Id used {}_{}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getHeader().getAppId(), pMessage.getHeader().getInterfaceId());

        mailDetails = (MailDetails) ExternalServicesRouter
                .injectBeanFromSpringContext(pMessage.getHeader().getAppId()
                        + "_" + pMessage.getHeader().getInterfaceId(), pContext);

    }
}
