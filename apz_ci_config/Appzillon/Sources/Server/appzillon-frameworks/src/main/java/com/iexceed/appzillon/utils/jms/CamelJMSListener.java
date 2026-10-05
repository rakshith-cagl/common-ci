package com.iexceed.appzillon.utils.jms;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Request;
import com.iexceed.appzillon.message.Response;
import com.iexceed.appzillon.utils.ServerConstants;
import org.slf4j.MDC;

import javax.jms.*;
import java.util.Enumeration;

import static com.iexceed.appzillon.utils.Constants.*;

public class CamelJMSListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    CamelJMSListener.class.getName());
    public static String lastUpdatedId = "";
    private static CamelJMSListener jmsListener = null;

    public static CamelJMSListener getInstance() {
        if (jmsListener == null) {
            jmsListener = new CamelJMSListener();
        }
        return jmsListener;
    }

    public void onMessage(Message message) {
        try {
            if (message.getJMSCorrelationID() != null) {
                String lresponseString = "";
                MDC.put("logRouter", "JMSListenerThread");
                LOG.debug("{} JMS Message received - correlation ID: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, message.getJMSCorrelationID());
                lresponseString = getResponseString(message);

                JSONObject lJSONObject = new JSONObject();
                lJSONObject.put(
                        ServerConstants.JMS_RESP_QUEUE,
                        message.getJMSDestination().toString());
                lJSONObject
                        .put(ServerConstants.JMS_MSG_CORRELATION_ID,
                                message.getJMSCorrelationID());
                lJSONObject.put(
                        ServerConstants.JMS_RESP_MSG,
                        lresponseString);
                com.iexceed.appzillon.message.Message msg = com.iexceed.appzillon.message.Message
                        .getInstance();
                msg.setHeader(Header.getInstance());
                msg.getHeader().setServiceType(
                        ServerConstants.APPZJMSRESPUPDATEREQUEST);
                msg.setRequestObject(Request.getInstance());
                msg.getRequestObject().setRequestJson(lJSONObject);
                msg.setResponseObject(Response.getInstance());
                DomainStartup.getInstance().processRequest(msg);

                String jmsResponse = msg.getResponseObject().getResponseJson()
                        .getString("jmsStatus");
                lastUpdatedId = message.getJMSCorrelationID();
                LOG.debug("{} JMS response after updating the DB - lJMSResp: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, jmsResponse);
            }
        } catch (com.iexceed.appzillon.json.JSONException ex) {
            LOG.error("{} JSONException: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
        } catch (JMSException e) {
            LOG.error("{} JMSException: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        }

    }

    private String getResponseString(Message message) throws JMSException {
        String lresponseString;
        if (message instanceof TextMessage textMessage) {
            LOG.debug("{} TextMessage found", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            lresponseString = (textMessage).getText();
            LOG.debug("{} JMS Message received - responseString: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lresponseString);
            if (lresponseString != null) {
                lresponseString = getResponseString(lresponseString);
            }

        } else if (message instanceof ObjectMessage objectMessage) {
            Object resobj = (objectMessage).getObject();
            String responseClass = "" + resobj.getClass();
            responseClass = responseClass.replace("class ", "");
            LOG.debug("{} Response class name {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, responseClass);

            String xmlRes = ExternalServicesRouter.getMarshalled(resobj, "" + responseClass);
            lresponseString = ExternalServicesRouter.getXMLToJSON(xmlRes);
        } else if (message instanceof MapMessage mapMessage) {
            LOG.debug("{} MapMessage found", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            Enumeration<?> mapNames = (mapMessage).getMapNames();
            JSONObject json = new JSONObject();
            iterateOverMapNames(mapMessage, mapNames, json);
            lresponseString = json.toString();

        } else if (message instanceof BytesMessage bytesMessage) {
            LOG.debug("{} BytesMessage found", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            int contentLength = (int) (bytesMessage)
                    .getBodyLength();
            byte[] body = new byte[contentLength];
            (bytesMessage).readBytes(body);
            lresponseString = new String(body);
            if (!lresponseString.isEmpty()) {
                lresponseString = getResponseString(lresponseString);
            }

        } else {
            LOG.debug("{} StreamMessage found", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            int contentLength = 1;
            while (((StreamMessage) message).readByte() != -1) {
                contentLength++;
            }
            byte[] body = new byte[contentLength];
            ((StreamMessage) message).readBytes(body);
            lresponseString = new String(body);
            if (!lresponseString.isEmpty()) {
                lresponseString = getResponseString(lresponseString);
            }

        }
        return lresponseString;
    }

    private void iterateOverMapNames(MapMessage mapMessage, Enumeration<?> mapNames, JSONObject json) throws JMSException {
        String eachName;
        Object eachValue;
        while (mapNames.hasMoreElements()) {

            eachName = (String) mapNames.nextElement();
            eachValue = mapMessage.getObject(eachName);
            LOG.debug("{} key found {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, eachName);
            json.put(eachName, "" + eachValue);

        }
    }

    private String getResponseString(String responseString) {
        if (responseString.charAt(0) == '<') {
            LOG.debug(XML_FOUND, ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            responseString = ExternalServicesRouter.getXMLToJSON(responseString);
        } else if (responseString.charAt(0) == '{') {
            LOG.debug(JSON_FOUND, ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

        } else {
            LOG.debug(NON_XML_JSON_VALUE_FOUND, ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            responseString = RESPONSE_KEY
                    + responseString + "\"}";
        }
        return responseString;
    }

}
