package com.iexceed.appzillon.services;

import com.iexceed.appzillon.dao.JMSDetails;
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;
import com.iexceed.appzillon.utils.jms.CamelJMSListener;
import com.iexceed.appzillon.utils.jms.CamelJMSQueueResolver;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.jndi.JndiTemplate;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JMSService implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    JMSService.class.getName());
    public static Map<String, String> isConnected = new HashMap<>();
    protected JMSDetails cJMSDetails = null;

    public void getJMSDetails(com.iexceed.appzillon.message.Message pMessage,
                              SpringCamelContext pContext) {
        LOG.debug("{} ApplicationId -: {}, InterfaceId -: {}, and Service Details BeanId -: {}_{}"
                , ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getHeader().getAppId()
                , pMessage.getHeader().getInterfaceId(), pMessage.getHeader().getAppId(), pMessage.getHeader().getInterfaceId());
        cJMSDetails = (JMSDetails) ExternalServicesRouter
                .injectBeanFromSpringContext(pMessage.getHeader().getAppId()
                        + "_" + pMessage.getHeader().getInterfaceId(), pContext);
        int timeOut = cJMSDetails.getTimeOut();
        /**
         * Below changes are made by Vinod as part of 
         * At app level, service time out should be configurable.
         * Appzillon 3.1 - 63 -- Start
         */
        if (timeOut == 0) {
            LOG.warn("{} Timeout value not configured will use default timeOut", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            timeOut = Integer.parseInt(PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.DEFAULT_TIMEOUT).trim());
        }
        /** Appzillon 3.1 - 63 -- END */
        cJMSDetails.setTimeOut(timeOut);
    }

    @Override
    public Object buildRequest(Message pMessage, Object pRequestPayLoad,
                               SpringCamelContext pContext) {
        Object reqpayload = null;
        LOG.debug("{} Building Request Using Superclass implementation ", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String lpayLoad = getPayLoadtoDeliver((String) pRequestPayLoad);

        if (ServerConstants.JMS_MSG_TYPE_OBJECT.equals(cJMSDetails
                .getJmsMessageType())) {
            String lInputXMLStr = ExternalServicesRouter.getJSONtoXML(lpayLoad);
            String lReqQualifiedClassName = cJMSDetails
                    .getRequestQualifiedClassName();
            LOG.debug("{} Qualified class name {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lReqQualifiedClassName);
            reqpayload = ExternalServicesRouter.getUnMarshalled(lInputXMLStr,
                    lReqQualifiedClassName);
        } else if (ServerConstants.JMS_MSG_TYPE_MAP.equals(cJMSDetails
                .getJmsMessageType())) {
            reqpayload = JSONUtils.getJsonHashMap(lpayLoad);
        } else if (ServerConstants.JMS_MSG_TYPE_STREAM.equals(cJMSDetails
                .getJmsMessageType())) {
            StringWriter s = new StringWriter();
            s.write(lpayLoad);
            reqpayload = s;
            try {
                s.close();
            } catch (IOException e) {
                LOG.error(ServerConstants.IOEXCEPTION, e);
            }
        } else if (ServerConstants.JMS_MSG_TYPE_BYTES.equals(cJMSDetails
                .getJmsMessageType())) {

            reqpayload = lpayLoad.getBytes(StandardCharsets.UTF_8);
        } else {
            LOG.debug("{} JMSmessage Type Text found", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            if (ServerConstants.XML.equalsIgnoreCase(cJMSDetails
                    .getRequestContentType())) {
                LOG.debug("{} requestContentType XML", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                reqpayload = ExternalServicesRouter.getJSONtoXML(lpayLoad);

            } else {
                LOG.debug("request contentType NON-XML");
                reqpayload = lpayLoad;
            }

        }
        return reqpayload;
    }

    @Override
    public Object processResponse(Message pMessage, Object pResponse,
                                  SpringCamelContext pContext) {
        return pResponse;
    }

    @Override
    public Object callService(Message pMessage, Object pRequestPayLoad,
                              SpringCamelContext pContext) {
        String responseFromQueue;
        String ouputString = null;
        LOG.debug("{} Getting JMSDetail bean from context", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        getJMSDetails(pMessage, pContext);

        pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad, cJMSDetails.getAutoGenElementMap(), cJMSDetails.getTranslationElementMap());
        pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad + ""));
        LOG.debug("{} After Appending Request Json With MaskedId : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());

        final Object fObject = buildRequest(pMessage, pRequestPayLoad.toString(), pContext);
        LOG.debug("{} Request created or edited successfully {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fObject);

        LOG.info("{} ********Sending request to Request Queue   Start ***********", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        ProducerTemplate pProducer = ExternalServicesRouter
                .createProducerTemplate();
        CamelJMSQueueResolver ljmsQueueResolver = (CamelJMSQueueResolver) ExternalServicesRouter
                .injectBeanFromSpringContext(pMessage.getHeader().getAppId()
                        + "_" + pMessage.getHeader().getInterfaceId() + "_"
                        + ServerConstants.JMS_CAMEL_QUEUE_RESOLVER, pContext);
        String endpoint = createEndpointURIByAppendingParam(pMessage, pContext);
        LOG.debug("{} Endpoint URI used after appending params {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpoint);
        final String fcorrelationId = createJMSTransactionInDomain(pMessage,
                ljmsQueueResolver,
                getPayLoadtoDeliver(pRequestPayLoad.toString()));
        LOG.debug("{} Jms endpoint used after appending params {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpoint);
        Utils.setExtTime(pMessage, "S");
        Exchange exchange = pProducer.request(endpoint, new Processor() {
            public void process(Exchange exchng) throws Exception {
                exchng.getIn().setBody(fObject);
                exchng.getIn().setHeader("JMSCorrelationID", fcorrelationId);

            }
        });

        Utils.setExtTime(pMessage, "E");
        if (exchange.getException() != null) {
            LOG.error("Exchange exception: ", exchange.getException());
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_011.toString());
            String emsg = exsrvcallexp
                    .getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_011);
            exsrvcallexp.setMessage(emsg);
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, exchange.getException().getMessage(), fObject);
            throw exsrvcallexp;
        } else {
            LOG.debug("{} No Exception, So starting listener on all response queues", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            responseFromQueue = getMessageFromResponseQueue(fcorrelationId,
                    ljmsQueueResolver, pMessage, pContext, pProducer);
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.SUCCESS, responseFromQueue, fObject);

        }
        ouputString = (String) processResponse(pMessage, responseFromQueue,
                pContext);
        LOG.debug("{} Response from JMSService {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ouputString);
        return new JSONObject(ouputString);
    }

    public String getMessageFromResponseQueue(String fcorrelationId,
                                              CamelJMSQueueResolver ljmsQueueResolver, Message pMessage,
                                              SpringCamelContext pContext, ProducerTemplate pProducer) {

        String lInterfaceId = pMessage.getHeader().getInterfaceId();
        String lAppId = pMessage.getHeader().getAppId();
        String lresponseFromQueue = null;
        Context lnamingContext = null;
        Properties lprop = null;
        QueueSession lqueueSession = null;
        QueueConnection lconnection = null;
        QueueConnectionFactory lconnectionFactory = null;
        long lstartTime = 0;
        String lconStatus = isConnected.get(lInterfaceId);
        LOG.debug("{} connection status of this interfaceID {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lconStatus);
        if (!ServerConstants.CONNECTED.equals(lconStatus)) {

            try {
                lprop = ((JndiTemplate) pContext.getRegistry().lookupByName(
                        lAppId + "_" + lInterfaceId + ServerConstants.JNDI_TEMPLATE))
                        .getEnvironment();
                lnamingContext = new InitialContext(lprop);

                lconnectionFactory = (QueueConnectionFactory) lnamingContext
                        .lookup(cJMSDetails.getConnectionFactory());

                lconnection = lconnectionFactory
                        .createQueueConnection(
                                (String) lprop
                                        .get(ServerConstants.JMS_JAVA_NAMING_SEC_PRINCIPAL),
                                (String) lprop
                                        .get(ServerConstants.JMS_JAVA_NAMING_SEC_CREDENTIALS));
                lconnection
                        .setExceptionListener(new com.iexceed.appzillon.utils.jms.JMSConExceptionListener(
                                lconnection, lnamingContext, lInterfaceId));
                LOG.debug("{} connectionFactory: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lconnectionFactory);
                LOG.debug("{} user : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, (String) lprop
                        .get(ServerConstants.JMS_JAVA_NAMING_SEC_PRINCIPAL));

                lqueueSession = lconnection.createQueueSession(false,
                        QueueSession.AUTO_ACKNOWLEDGE);

                CamelJMSListener lcamelJMSListener = CamelJMSListener
                        .getInstance();

                int i = 0;
                while (i < ljmsQueueResolver.getResponseQueues().size()) {
                    Queue queue = (Queue) lnamingContext
                            .lookup(ljmsQueueResolver.getResponseQueues()
                                    .get(i));

                    QueueReceiver queueReceiver = lqueueSession
                            .createReceiver(queue);
                    LOG.debug("{} queueReceiver: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, queueReceiver);
                    queueReceiver.setMessageListener(lcamelJMSListener);

                    i++;
                }
                lconnection.start();

                isConnected.put(lInterfaceId, ServerConstants.CONNECTED);

            } catch (NamingException e) {
                LOG.error("NamingException: ", e);
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_011.toString());
                String emsg = exsrvcallexp
                        .getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_011);
                exsrvcallexp.setMessage(emsg);
                exsrvcallexp.setPriority("1");

                throw exsrvcallexp;
            } catch (JMSException e) {
                LOG.error("JMSException: ", e);
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_011.toString());
                String emsg = exsrvcallexp
                        .getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_011);
                exsrvcallexp.setMessage(emsg);
                exsrvcallexp.setPriority("1");

                throw exsrvcallexp;
            } finally {
                try {
                    lqueueSession.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }

        LOG.debug("{} Waiting for correlation iD : {} to get Updated in atmost: {} sec", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                fcorrelationId, cJMSDetails.getTimeOut());
        int timeOut = cJMSDetails.getTimeOut() * 1000;
        boolean updatedBefTimeOut = false;
        lstartTime = new Date().getTime();
        String updateCor = CamelJMSListener.lastUpdatedId;
        while (System.currentTimeMillis() - lstartTime < timeOut) {
            if (updateCor.equals(fcorrelationId)) {
                LOG.debug("{} Updated corelation id in {} millisecs after connection", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, (System.currentTimeMillis() - lstartTime));
                updatedBefTimeOut = true;
                break;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    LOG.error("InterruptedException: ", e);
                    Thread.currentThread().interrupt();
                }
                updateCor = CamelJMSListener.lastUpdatedId;
            }
        }
        if (!updatedBefTimeOut) {
            LOG.debug("{} Didn't get the response within {} ms", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, timeOut);
        }
        JSONObject lobject = new JSONObject();
        try {
            lobject.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
            lobject.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID,
                    lInterfaceId);
            lobject.put(ServerConstants.JMS_MSG_CORRELATION_ID, fcorrelationId);

            pMessage.getHeader().setServiceType(
                    ServerConstants.APPZGETJMSSTATUS);
            LOG.debug("{} setting service type to {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getHeader().getServiceType());
            pMessage.getRequestObject().setRequestJson(lobject);
            DomainStartup.getInstance().processRequest(pMessage);
            JSONObject response = pMessage.getResponseObject()
                    .getResponseJson();
            lresponseFromQueue = response.getString("JmsResponseJSON");
            LOG.debug("{} getMessageFromResponseQueue - database response: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lresponseFromQueue);
        } catch (JSONException e) {
            LOG.error("JSONException: ", e);
        }
        if (lresponseFromQueue == null) {
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_010.toString());
            String emsg = exsrvcallexp
                    .getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_010);
            exsrvcallexp.setMessage(emsg);
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;

        }
        return lresponseFromQueue;

    }

    public String createEndpointURIByAppendingParam(Message pMessage,
                                                    SpringCamelContext pContext) {

        String camelID = (pMessage.getHeader().getAppId() + "__" + pMessage
                .getHeader().getInterfaceId()).replaceAll("[\\.]", "__");
        String endpoint = pContext.getEndpoint(camelID).getEndpointUri();
        LOG.debug("{} Jms endpoint used before appending params {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpoint);
        endpoint = ServicesUtil.enrichWithProperties(pMessage.getHeader().getAppId(), endpoint);
        LOG.debug("{} URI endpoint after replacing properties : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpoint);
        StringBuilder appendOption = new StringBuilder(ServerConstants.JMS_DEFAULT_APPENDER);
        int timeOut = cJMSDetails.getTimeOut();
        timeOut = timeOut * 1000;
        if (timeOut == 0) {
            LOG.debug("{} TimeOut value is not set by the user hence setting it to 20 seconds....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            timeOut = 20000;
        }
        appendOption.append(ServerConstants.JMS_APPEND_MSG_TYPE + cJMSDetails.getJmsMessageType());
        Properties prop = ((JndiTemplate) pContext.getRegistry().lookupByName(
                pMessage.getHeader().getAppId() + "_"
                        + pMessage.getHeader().getInterfaceId()
                        + ServerConstants.JNDI_TEMPLATE)).getEnvironment();
        String username = null;
        String password = null;
        if (prop != null) {
            username = (String) prop.get(ServerConstants.JMS_JAVA_NAMING_SEC_PRINCIPAL);
            password = (String) prop.get(ServerConstants.JMS_JAVA_NAMING_SEC_CREDENTIALS);
        }
        if (!(username == null || password == null)) {
            appendOption.append(ServerConstants.JMS_APPEND_AND_USER + username + ServerConstants.JMS_APPEND_AND_PIN + password);
        }

        endpoint += appendOption;
        return endpoint;
    }

    public String createJMSTransactionInDomain(Message pMessage,
                                               CamelJMSQueueResolver pjmsQueueRes, String lpayLoad) {
        String lcorelationRefName = cJMSDetails.getJmsCorelationName();
        String wholepayLoad = pMessage.getRequestObject().getRequestJson()
                .toString();
        if (lcorelationRefName == null) {
            lcorelationRefName = "";
        }
        String lcorrelationId = JSONUtils.getKeyValue(wholepayLoad,
                lcorelationRefName);
        JSONObject lJSONObject = new JSONObject();
        lJSONObject.put(ServerConstants.JMS_REQ_PAYLOAD, lpayLoad);
        lJSONObject.put(ServerConstants.JMS_RESP_MSG, "");

        lJSONObject.put(ServerConstants.JMS_REQ_TYPE,
                cJMSDetails.getRequestContentType());
        lJSONObject.put(ServerConstants.JMS_REQ_QUEUE,
                pjmsQueueRes.getRequestQueue());
        lJSONObject.put(ServerConstants.JMS_RESP_TYPE,
                cJMSDetails.getResponseContentType());
        lJSONObject.put(ServerConstants.JMS_MSG_ID, lcorrelationId);
        LOG.debug("reqQueue {}", cJMSDetails);

        pMessage.getHeader().setServiceType(
                ServerConstants.APPZJMSREQINSERTREQUEST);
        pMessage.getRequestObject().setRequestJson(lJSONObject);
        DomainStartup.getInstance().processRequest(pMessage);
        String jmsTransactionId = pMessage.getResponseObject()
                .getResponseJson().getString("jmsTransactionId");
        LOG.debug("{} createJMSTransactionInDomain -jmsTranactionId: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, jmsTransactionId);
        if (Utils.isNullOrEmpty(lcorrelationId)) {
            lcorrelationId = "JMS_" + jmsTransactionId;
        }
        LOG.debug("{} createJMSTransactionInDomain -lcorrelationId: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lcorrelationId);
        return lcorrelationId;

    }

    public String getPayLoadtoDeliver(String appzillonBodyContent) {
        String lpayLoad = appzillonBodyContent;
        if (Utils.isNotNullOrEmpty(cJMSDetails.getTextMsgNode())) {
            LOG.debug("{} Message node configured in detail bean is : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, cJMSDetails.getTextMsgNode());
            String nodeValue = JSONUtils.getKeyValue(appzillonBodyContent,
                    cJMSDetails.getTextMsgNode());
            if (Utils.isNullOrEmpty(nodeValue)) {
                LOG.error("{} No Node found with name {} in appzillonBody", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, cJMSDetails.getTextMsgNode());
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_009.toString());
                String emsg = exsrvcallexp
                        .getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_009);
                exsrvcallexp.setMessage(emsg);
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;

            } else {
                LOG.debug("{} Node value found in appzillonBody", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                lpayLoad = nodeValue;
            }
        } else {
            LOG.warn("{} No Specific Node configured with  in appzillonBody: Will Use appzillonBody", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

        }
        return lpayLoad;

    }
}
