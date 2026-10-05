/**
 *
 */
package com.iexceed.appzillon.utils;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.ITranslationBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import org.apache.camel.spring.SpringCamelContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author ripu.pandey
 *
 */
public class ServicesUtil {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    ServicesUtil.class.getName());
    private static Map<String, ITranslationBean> itTranslationBeanMap = new HashMap<>();

    private ServicesUtil() {

    }

    /**
     * Below method written by ripu on 14-April-2016
     * For replacing element value in under any jsonobject node
     * @param pNodeElementStruc
     * @param pReplacingValue
     * @param prequestPayLoad
     * @return
     */
    private static String replaceMaskedValueFromNodeStruc(String pNodeElementStruc, String pReplacingValue, JSONObject prequestPayLoad) {
        LOG.debug("inside replaceMaskedValueFromNodeStruc()..");
        LOG.debug("Node Structure : {} and Value To Be Replaced : {}", pNodeElementStruc, pReplacingValue);
        LOG.debug("Payload : {}", prequestPayLoad);
        JSONObject tempPayload = prequestPayLoad;
        JSONObject responsePayload = null;
        try {
            JSONObject lTempJSON = prequestPayLoad;
            String[] node = pNodeElementStruc.split(".");
            String[] jsonObject = new String[node.length];
            int i = 0;
            for (; i < node.length - 1; i++) {
                jsonObject[i] = lTempJSON + "";
                lTempJSON = lTempJSON.getJSONObject(node[i]);
            }
            lTempJSON.put(node[i], pReplacingValue);

            int j = node.length - 2;
            for (; j >= 0; j--) {
                JSONObject internal = new JSONObject(jsonObject[j]);
                internal.put(node[j], lTempJSON);
                lTempJSON = internal;
            }
            responsePayload = lTempJSON;
        } catch (JSONException jsonex) {
            jsonex.printStackTrace();
        }
        if (responsePayload == null) {
            responsePayload = tempPayload;
        }
        LOG.debug("Response Payload : {}", responsePayload);
        return responsePayload + "";
    }

    /**
     * Below method written by ripu for masking source messageId on 14-April-2016.
     * @param pMaskFormat
     * @return
     */
    private static String getMasked(Message pMessage, String pMaskFormat) {
        LOG.debug("inside getMasked..");
        String bkpServiceType = pMessage.getHeader().getServiceType();
        JSONObject bkpRequestJson = pMessage.getRequestObject().getRequestJson();

        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_FETCH_SEQUENCE_VALUE);
        JSONObject sourceMsgSeqIdJson = new JSONObject();
        sourceMsgSeqIdJson.put("sequenceName", "SOURCE_MSG_ID");
        pMessage.getRequestObject().setRequestJson(new JSONObject().put("sequenceDtlsRequest", sourceMsgSeqIdJson));
        DomainStartup.getInstance().processRequest(pMessage);

        sourceMsgSeqIdJson = pMessage.getResponseObject().getResponseJson();
        String sequeceValue = sourceMsgSeqIdJson.getJSONObject("sequenceDtlsResponse").get("sequenceValue") + "";
        LOG.debug("Sequence Value from Domain : {}", sequeceValue);
        pMessage.getHeader().setServiceType(bkpServiceType);
        pMessage.getRequestObject().setRequestJson(bkpRequestJson);

        StringBuilder stringBuilder = new StringBuilder(pMaskFormat);
        if (pMaskFormat.contains("N")) {
            int index = pMaskFormat.indexOf("N");
            int lastIndex = pMaskFormat.lastIndexOf("N");
            int length = lastIndex - index;
            String paddedSequenceValue = Utils.getPaddedString(sequeceValue, length + 1, '0', true);
            stringBuilder.replace(index, lastIndex + 1, paddedSequenceValue);
        }
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        String paddedYear = Utils.getPaddedString(year + "", 4, '0', true);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        String paddedMonth = Utils.getPaddedString(month + "", 2, '0', true);

        int day = now.get(Calendar.DAY_OF_MONTH);
        String paddedDay = Utils.getPaddedString(day + "", 2, '0', true);
        int hour24Formate = now.get(Calendar.HOUR_OF_DAY);
        String paddedHour = Utils.getPaddedString(hour24Formate + "", 2, '0', true);
        int minute = now.get(Calendar.MINUTE);
        String paddedMinute = Utils.getPaddedString(minute + "", 2, '0', true);
        int second = now.get(Calendar.SECOND);
        String paddedSecond = Utils.getPaddedString(second + "", 2, '0', true);
        int millis = now.get(Calendar.MILLISECOND);

        pMaskFormat = stringBuilder + "";
        if (pMaskFormat.contains("HH")) { //Hour in 24 hours format
            pMaskFormat = pMaskFormat.replace("HH", paddedHour);
        }
        if (pMaskFormat.contains("hh")) { // Hour in 12 hours format
            String hour12Formate = now.get(Calendar.HOUR) + "";
            hour12Formate = Utils.getPaddedString(hour12Formate, 2, '0', true);
            pMaskFormat = pMaskFormat.replace("hh", hour12Formate);
        }
        if (pMaskFormat.contains("mm")) { // Minutes
            pMaskFormat = pMaskFormat.replace("mm", paddedMinute);
        }
        if (pMaskFormat.contains("ss")) { // Seconds
            pMaskFormat = pMaskFormat.replace("ss", paddedSecond);
        }
        if (pMaskFormat.contains("SSS")) { // Milli Seconds
            String msss = millis + "";
            pMaskFormat = getMaskFormatSSS(pMaskFormat, msss);
        }
        if (pMaskFormat.contains("SS")) { // Milli Seconds
            String mss = millis + "";
            pMaskFormat = getMaskFormatSS(pMaskFormat, mss);
        }
        if (pMaskFormat.contains("S")) { // Milli Seconds
            String ms = millis + "";
            pMaskFormat = pMaskFormat.replace("S", ms.substring(0, 1));
        }
        if (pMaskFormat.contains("DD")) { // Day in year
            pMaskFormat = pMaskFormat.replace("DD", paddedDay);
        }
        if (pMaskFormat.contains("MM")) { // Month in year
            pMaskFormat = pMaskFormat.replace("MM", paddedMonth);
        }
        if (pMaskFormat.contains("YYYY")) { // Year in 4 digits
            pMaskFormat = pMaskFormat.replace("YYYY", paddedYear);
        }
        if (pMaskFormat.contains("YY")) { // Year in two digits
            String yr = year + "";
            pMaskFormat = pMaskFormat.replace("YY", yr.substring(2));
        }

        return pMaskFormat;
    }

    private static String getMaskFormatSS(String pMaskFormat, String mss) {
        String paddedMilliSS;
        if (mss.length() < 2) {
            paddedMilliSS = Utils.getPaddedString(mss, 2, '0', true);
            pMaskFormat = pMaskFormat.replace("SS", paddedMilliSS);
        } else {
            pMaskFormat = pMaskFormat.replace("SS", mss.substring(0, 2));
        }
        return pMaskFormat;
    }

    private static String getMaskFormatSSS(String pMaskFormat, String msss) {
        String paddedMilliSS;
        if (msss.length() < 3) {
            paddedMilliSS = Utils.getPaddedString(msss, 3, '0', true);
            pMaskFormat = pMaskFormat.replace("SSS", paddedMilliSS);
        } else {
            if (msss.length() == 4) {
                pMaskFormat = pMaskFormat.replace("SSS", msss.substring(0, 4));
            } else {
                pMaskFormat = pMaskFormat.replace("SSS", msss.substring(0, 3));
            }
        }
        return pMaskFormat;
    }

    // to read the constant param values from the user defined impl class
    private static String getTranslationValue(com.iexceed.appzillon.message.Message pMessage, String queryParamField) {
        LOG.debug("Getting Translation value for : {}", queryParamField);
        SpringCamelContext context = ExternalServicesRouter.getCamelContext();
        ITranslationBean tranBean = getTransVal(pMessage, context);
        return tranBean.getParamValue(queryParamField);
    }

    private static ITranslationBean getTransVal(com.iexceed.appzillon.message.Message pMessage, SpringCamelContext pContext) {
        String beanId = pMessage.getHeader().getAppId() + "_paramImpl";
        ITranslationBean itTransBean = null;
        if (!itTranslationBeanMap.containsKey(beanId)) {
            LOG.debug("Does Not contain beanId in the map, so its loading in the map : {}", beanId);
            itTransBean = (ITranslationBean) ExternalServicesRouter.injectBeanFromSpringContext(beanId, pContext);
            itTranslationBeanMap.put(beanId, itTransBean);
        } else {
            itTransBean = itTranslationBeanMap.get(beanId);
        }
        return itTransBean;
    }

    /**
     * Below method written by ripu for adding extra element in the payload as maskedMessageId and translation bean
     * @param pMessage
     * @param pRequestPayLoad
     * @param autoGenElementMap
     * @param translationElementMap
     * @return
     */
    public static Object getModifiedPayloadWithMaskedValue(com.iexceed.appzillon.message.Message pMessage, Object pRequestPayLoad,
                                                           Map<String, String> autoGenElementMap, Map<String, String> translationElementMap) {
        LOG.debug("inside getModifiedPayloadWithMaskedValue()..");
        JSONObject payLoadJson = new JSONObject(pRequestPayLoad + "");
        pRequestPayLoad = getRequestPayLoadAutoGen(pMessage, pRequestPayLoad, autoGenElementMap, payLoadJson);
        pRequestPayLoad = getRequestPayLoadTranslation(pMessage, pRequestPayLoad, translationElementMap, payLoadJson);

        LOG.debug("New Payload with masked value : {}", pRequestPayLoad);
        return pRequestPayLoad;
    }

    private static Object getRequestPayLoadTranslation(Message pMessage, Object pRequestPayLoad, Map<String, String> translationElementMap, JSONObject payLoadJson) {
        if (translationElementMap != null && !translationElementMap.isEmpty()) {
            LOG.debug("Translation Element Map in the camel context : {}", translationElementMap);
            for (Map.Entry<String, String> translationEntry : translationElementMap.entrySet()) {
                LOG.debug("Translation Node Structure = {}, Translation Node Value = {}", translationEntry.getKey(), translationEntry.getValue());
                String[] node = translationEntry.getKey().split("\\.");
                LOG.debug("Checking Node : {} available in the payload", node[0]);
                if (payLoadJson.has(node[0]) && (!translationEntry.getValue().isEmpty())) {
                    String translationVal = ServicesUtil.getTranslationValue(pMessage, translationEntry.getValue());
                    LOG.debug("{} Translation value : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, translationVal);
                    pRequestPayLoad = ServicesUtil.replaceMaskedValueFromNodeStruc(translationEntry.getKey(), translationVal, payLoadJson);
                } else {
                    LOG.debug("payload doesn't have node : {}, OR value of key - {} is empty in camel-context.", node[0], translationEntry.getKey());
                }
            }
        }
        return pRequestPayLoad;
    }

    private static Object getRequestPayLoadAutoGen(Message pMessage, Object pRequestPayLoad, Map<String, String> autoGenElementMap, JSONObject payLoadJson) {
        if (autoGenElementMap != null && !autoGenElementMap.isEmpty()) {
            LOG.debug("Auto Gen Element Map in the camel-context : {}", autoGenElementMap);
            for (Map.Entry<String, String> entry : autoGenElementMap.entrySet()) {
                LOG.debug("Auto Gen Node.Element : {}, Auto Gen Element Value : {}", entry.getKey(), entry.getValue());
                String[] node = entry.getKey().split("\\.");
                LOG.debug("Checking Node : {} available in the payload", node[0]);
                if (payLoadJson.has(node[0]) && (!entry.getValue().isEmpty())) {
                    String lMaskedSrcMsgId = ServicesUtil.getMasked(pMessage, entry.getValue());
                    LOG.debug("Masked Source Message Id Mask : {}", lMaskedSrcMsgId);
                    pRequestPayLoad = ServicesUtil.replaceMaskedValueFromNodeStruc(entry.getKey(), lMaskedSrcMsgId, payLoadJson);
                } else {
                    LOG.debug("payload doesn't have node : {}, OR value of key - {} is empty in camel-context.", node[0], entry.getKey());
                }
            }
        }
        return pRequestPayLoad;
    }

    public static void processFmwTxnDetails(com.iexceed.appzillon.message.Message pMessage, String payloadKey, Object responsePayload, Object requestPayload) {
        LOG.debug("{} Inside processFmwTxnDetails().. ", ServerConstants.FMW_LOG_TRANSACTION);

        if (((pMessage.getSecurityParams().getLogTxn() != null) && (ServerConstants.YES.equalsIgnoreCase(pMessage.getSecurityParams().getLogTxn()))) &&
                (pMessage.getSecurityParams().getFmwTxnReq() != null) && (ServerConstants.YES.equalsIgnoreCase(pMessage.getSecurityParams().getFmwTxnReq()))) {
            pMessage.getHeader().setServiceType(ServerConstants.FMW_LOG_TRANSACTION);
            JSONObject prevReq = pMessage.getRequestObject().getRequestJson();
            JSONObject prevRes = pMessage.getResponseObject().getResponseJson();
            JSONObject jsonRequestObject = new JSONObject();
            jsonRequestObject.put(ServerConstants.REQUEST_PAYLOAD, requestPayload.toString());
            pMessage.getRequestObject().setRequestJson(jsonRequestObject);
            JSONObject jsonResponseObject = new JSONObject();
            jsonResponseObject.put(payloadKey, responsePayload.toString());
            pMessage.getResponseObject().setResponseJson(jsonResponseObject);

            DomainStartup.getInstance().processRequest(pMessage);

            pMessage.getRequestObject().setRequestJson(prevReq);
            pMessage.getResponseObject().setResponseJson(prevRes);
        }

    }


    public static void processMailTxn(com.iexceed.appzillon.message.Message pMessage, String payloadKey,
                                      Object responsePayload, Object requestPayload) {
        LOG.debug("{} Inside processMailTxn().. ", ServerConstants.FMW_LOG_TRANSACTION);
        pMessage.getHeader().setServiceType(ServerConstants.FMW_LOG_TRANSACTION);
        JSONObject jsonRequestObject = new JSONObject();
        jsonRequestObject.put(ServerConstants.REQUEST_PAYLOAD, requestPayload.toString());
        pMessage.getRequestObject().setRequestJson(jsonRequestObject);
        JSONObject jsonResponseObject = new JSONObject();
        jsonResponseObject.put(payloadKey, responsePayload.toString());
        pMessage.getResponseObject().setResponseJson(jsonResponseObject);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public static String enrichWithProperties(String appId, String text) {
        Properties props = new Properties();
        String envFileName = PropertyUtils.getPropValue(appId, ServerConstants.ENV_FILE_NAME);
        try {

            if (Utils.isNotNullOrEmpty(Logger.propertiesPath)) {
                envFileName = String.format("%s/%s/%s", Logger.propertiesPath, appId, envFileName);
            }

            try (InputStream is = ServicesUtil.class.getClassLoader().getResourceAsStream(envFileName)) {
                LOG.debug(ServerConstants.LOGGER_FRAMEWORKS + "Properties file name found : "
                        + is);
                props.load(is);
            }

        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(ServerConstants.LOGGER_FRAMEWORKS, e);
        }
        text = URLDecoder.decode(text);
        String response = text;

        // APZ variables
        response = replacePropertyPlaceHolder(ServerConstants.APZ_VARIABLE, text, props);
        // Env variable
        response = replacePropertyPlaceHolder(ServerConstants.ENV_VARIABLE, response, null);
        // SYS variables
        response = replacePropertyPlaceHolder(ServerConstants.SYSTEM_VARIABLE, response, null);

        return response;
    }

    private static String replacePropertyPlaceHolder(String type, String text, Properties props) {
        while (text.contains(type)) {
            int start = text.indexOf(type);
            int end = text.indexOf("}");
            String propertyKey = text.substring(start + type.length(), end);
            String property = text.substring(start, end + 1);
            String propertyValue = "";
            if (ServerConstants.APZ_VARIABLE.equals(type)) {
                propertyValue = props.getProperty(propertyKey);
            } else if (ServerConstants.ENV_VARIABLE.equals(type)) {
                propertyValue = System.getenv(propertyKey);
            } else if (ServerConstants.SYSTEM_VARIABLE.equals(type)) {
                propertyValue = System.getProperty(propertyKey);
            }
            text = text.replace(property, propertyValue);
        }
        return text;
    }

}
