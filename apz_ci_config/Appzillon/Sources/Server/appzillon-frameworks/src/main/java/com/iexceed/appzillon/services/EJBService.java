package com.iexceed.appzillon.services;

import com.iexceed.appzillon.dao.EJBDetails;
import com.iexceed.appzillon.dao.EJBParamDetails;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.XMLToJsonConverter;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;
import org.apache.camel.*;
import org.apache.camel.spring.SpringCamelContext;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EJBService implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    EJBService.class.getName());
    protected EJBDetails cEjbDetails = null;
    private Object responsePayload = null;

    /**
     * @param pMessage
     * @param pContext
     * @return
     */
    public Object buildRequest(com.iexceed.appzillon.message.Message pMessage, Object pRequestPayLoad,
                               SpringCamelContext pContext) {
        Object[] lParamArray = null;
        JSONObject lRequestJson = null;
        String lParamValue = null;
        LOG.info("{} Building request for external EJB service....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        List<EJBParamDetails> lparamList = cEjbDetails.getParamList();
        LOG.debug("{} EJB Service request's parameters list -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lparamList);
        if (lparamList != null) {
            LOG.debug("{} Initializing EJB Service request's Object Parameters array of size: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lparamList.size());
            lParamArray = new Object[lparamList.size()];

            for (EJBParamDetails fParamlist : lparamList) {
                int lParamArrayIndex = Integer.parseInt(fParamlist.getParamOrder());
                String paramType = fParamlist.getParamType();
                LOG.debug("{}  Parameter type for param index {}  is : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lParamArrayIndex, paramType);
                // Fetching the node structure value from the input JSON
                String[] nodestructure = fParamlist.getNodeStructureName()
                        .split(".");
                // Initializing j for looping
                int j = 0;
                // Assing value to JSONObject
                lRequestJson = new JSONObject(pRequestPayLoad.toString());
                lParamValue = null;
                for (; j < nodestructure.length - 1; j++) {
                    LOG.debug("{} Node at index [ {} ] -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, j, nodestructure[j]);
                    if (lRequestJson.get(nodestructure[j]) instanceof JSONObject) {
                        LOG.debug("{} Json Instance found and the value is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lRequestJson.getJSONObject(nodestructure[j]));
                        lRequestJson = lRequestJson.getJSONObject(nodestructure[j]);

                    } else if (lRequestJson.get(nodestructure[j]) instanceof String) {
                        lParamValue = lRequestJson.get(nodestructure[j]).toString();
                        LOG.debug("{} String Instance found and the value is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lParamValue);
                    }
                }
                lParamValue = getParamValue(lParamArray, lRequestJson, lParamValue, lParamArrayIndex, paramType, nodestructure, j);

                LOG.debug("{} Request built before checking the parameters type -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lParamValue);
                LOG.debug("{} Checking parameter type and the type is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fParamlist.getParamType());

                // Checking if the Parameter type is serializable object
                if (fParamlist.getParamType().equalsIgnoreCase(
                        ServerConstants.EJB_SERIALIZABLE_OBJECT_TYPE)) {
                    LOG.debug("{} Parameter Type is found to be Serializable java object, hence converting it into a XML....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                    // Getting XML from JSON
                    String lXML = ExternalServicesRouter.getJSONtoXML(lParamValue);
                    LOG.debug("{} Parameter Type is found to be Serializable java object,after converting to an XML -:", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lXML);
                    LOG.debug("{} Parameter type is serializable object hence unmarshalling the input xml to java object of class -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, fParamlist.getQualifiedClassName());
                    Object lReqObject = ExternalServicesRouter.getUnMarshalled(lXML, fParamlist.getQualifiedClassName());
                    LOG.debug("{} After unmarshalling the request object: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lReqObject);
                    lParamArray[lParamArrayIndex] = lReqObject;
                }
            }
        }
        LOG.debug("{} After adding request parameters, final request Parameter array is-: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lParamArray);

        return lParamArray;
    }

    private String getParamValue(Object[] lParamArray, JSONObject lRequestJson, String lParamValue, int lParamArrayIndex, String paramType, String[] nodestructure, int j) {
        if (lParamValue == null || lParamValue.isEmpty()) {
            if (paramType.equals("JSON") || paramType.equals("SERIALIZABLEJAVAOBJECT")) {
                lParamArray[lParamArrayIndex] = lRequestJson.getJSONObject(nodestructure[j]);
                // Storing node value to parameter value variable
                lParamValue = lRequestJson.getJSONObject(nodestructure[j]).toString();
            } else {
                lParamArray[lParamArrayIndex] = lRequestJson.getString(nodestructure[j]);
                // Storing node value to parameter value variable
                lParamValue = lRequestJson.getString(nodestructure[j]);
            }
            LOG.debug("{} After setting Parameters value -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lParamValue);
        } else {
            lParamArray[lParamArrayIndex] = lParamValue;
        }
        return lParamValue;
    }

    /**
     * @param pMessage
     * @param pContext
     */
    public void getEJBDetails(com.iexceed.appzillon.message.Message pMessage,
                              SpringCamelContext pContext) {
        LOG.debug("{} ApplicationId -: {}, InterfaceId -: {}, and Service Details BeanId -: {}"
                , ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getHeader().getAppId(), pMessage.getHeader().getInterfaceId()
                , pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getInterfaceId());
        cEjbDetails = (EJBDetails) ExternalServicesRouter
                .injectBeanFromSpringContext(pMessage.getHeader().getAppId()
                        + "_" + pMessage.getHeader().getInterfaceId(), pContext);
        LOG.debug("{} Service Details Bean is injected.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        int timeOut = cEjbDetails.getTimeOut();
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
        cEjbDetails.setTimeOut(timeOut);
    }

    /**
     * @param pMessage
     * @param pRequestPayLoad
     * @param pContext
     * @return
     */
    public Object callService(com.iexceed.appzillon.message.Message pMessage,
                              Object pRequestPayLoad, SpringCamelContext pContext) {
        LOG.debug("Inside SuperClass : callService ");
        String output = "";
        long startTime;
        Exchange exchange = null;
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        getEJBDetails(pMessage, pContext);
        LOG.debug("{} Getting producer Template....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        ProducerTemplate pProducerTemplate = FrameworksStartup.getProducerTemplate();
        LOG.debug("{} Built producer Template", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

        String camelID = (appId + "__" + interfaceId).replaceAll("[.]", "__");
        LOG.debug("{} EndPoint Id is built for the AppId -: {}, InterfaceId -: {}, and the Camel EndPoint Id -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, appId, interfaceId, camelID);
        pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad,
                cEjbDetails.getAutoGenElementMap(), cEjbDetails.getTranslationElementMap());
        pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad + ""));
        LOG.debug("{} After Appending Request Json With MaskedId : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        Object buildParam = buildRequest(pMessage, pRequestPayLoad.toString(), pContext);
        final Object[] lparamArray = (Object[]) buildParam;

        Message msg = null;
        LOG.debug("{} Getting EndPoint for the Camel Endpoint Id -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, camelID);
        Endpoint endPoint = pContext.getEndpoint(camelID);
        LOG.debug("{} EndPoint for the Camel Endpoint Id -: {} is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, camelID, endPoint);

        LOG.debug("{} Building Future<Exchange> object by setting request body parameter array....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        Utils.setExtTime(pMessage, "S");
        Future<Exchange> futurexchange = pProducerTemplate
                .asyncSend(endPoint, new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody(lparamArray);
                    }
                });

        startTime = System.currentTimeMillis();
        LOG.debug("{} Future Exchange request sent at : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, startTime);
        try {
            LOG.debug("{} Setting Future<Exchange> time out....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            exchange = futurexchange.get(cEjbDetails.getTimeOut(),
                    TimeUnit.SECONDS);
            LOG.debug("{} Future Exchange received", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        } catch (InterruptedException e) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            LOG.error("{} Future - InterruptedException: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } catch (ExecutionException e) {
            LOG.error("{} Future - ExecutionException: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } catch (TimeoutException e) {
            LOG.error("{} Future - TimeoutException: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } finally {
            futurexchange.cancel(true);
            LOG.debug("{} Stopping future exchange....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        }
        Utils.setExtTime(pMessage, "E");
        if (exchange.getException() == null) {
            LOG.debug("{} No exception hence proceeding to process response....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            long endTime = System.currentTimeMillis();
            LOG.debug("{} Response received at -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endTime);
            if ((endTime - startTime) > 1000) {
                long quotient = (endTime - startTime) / 1000;
                long remainder = (endTime - startTime) % 1000;
                LOG.debug("{} Time Taken for the service -: {} is -: {}.{} Seconds", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, interfaceId, quotient, remainder);
            } else {
                LOG.debug("{} Time Taken for the service -: {} is -: {} MilliSeconds", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, interfaceId, (endTime - startTime));
            }
            LOG.debug("{} Getting Camel Message from the exchange....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            msg = exchange.getMessage();


            LOG.debug("{} Camel Message received from the exchange is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, msg);
            LOG.debug("{} Requesting to processing the response....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            output = (String) processResponse(pMessage, msg, pContext);
            LOG.debug("{} After processing response, the response is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, output);
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.SUCCESS, responsePayload, lparamArray);
        } else {
            LOG.error("{} callExternalService - Exchange-exception : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, Utils.getStackTrace(exchange.getException()));
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();

            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, exchange.getException().getMessage(), lparamArray);

            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;

        }

        LOG.debug("{} Response returned after calling external service is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, output);
        return new JSONObject(output);
    }

    @SuppressWarnings("unchecked")
    public Object processResponse(com.iexceed.appzillon.message.Message pMessage, Object pResponse, SpringCamelContext pContext) {
        Message pMsg = (Message) pResponse;
        LOG.debug("{} Processing response and the response received is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMsg);
        String outputJSON = null;
        try {
            if (ServerConstants.XML.equalsIgnoreCase(cEjbDetails
                    .getResponseContentType())) {
                LOG.debug("{} Response content type is XML and the response is -: {} -> and will be converting it to a JSON....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMsg.getMandatoryBody(String.class));
                outputJSON = pMsg.getMandatoryBody(String.class);
                responsePayload = outputJSON;
                outputJSON = XMLToJsonConverter.xmlToJson(outputJSON);
                LOG.debug("{} Response content type is XML, After converting response XML to JSON String: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, outputJSON);
            } else if (ServerConstants.EJB_SERIALIZABLE_OBJECT_TYPE
                    .equalsIgnoreCase(cEjbDetails.getResponseContentType())) {
                LOG.debug("{} Response content type is serializable java object and the response is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMsg.getMandatoryBody(String.class));
                @SuppressWarnings("rawtypes")
                Class lClass = getaClass();
                Object lresponseObj;
                lresponseObj = pMsg.getMandatoryBody(lClass);
                responsePayload = lresponseObj;
                LOG.debug("{} Converting the serializable java object to XML to convert it to a JSON.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                // Marshalling the object to XML string
                outputJSON = ExternalServicesRouter.getMarshalled(lresponseObj,
                        cEjbDetails.getResponseFullyQualifiedClassName());
                LOG.debug("{} After Marshelling to XML from response Object -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, outputJSON);
                // Converting XML string to JSON String
                outputJSON = ExternalServicesRouter.getXMLToJSON(outputJSON);
                LOG.debug("{} After converting XML to a JSON String: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, outputJSON);
            } else {
                outputJSON = pMsg.getMandatoryBody(String.class);
                responsePayload = outputJSON;
                LOG.debug("{} Response content type is  string and the response is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, outputJSON);
            }
        } catch (InvalidPayloadException e) {
            LOG.error("{} InvalidPayloadException: Error parsing  external response {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS
                    , e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017)
            );
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        return outputJSON;
    }

    private Class getaClass() {
        @SuppressWarnings("rawtypes") Class lclass;
        try {
            lclass = Class.forName(cEjbDetails
                    .getResponseFullyQualifiedClassName());
            LOG.debug("{} Response content type is serializable java object and of class - {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lclass.getClass());
        } catch (ClassNotFoundException e) {
            LOG.error("{} Response content type is serializable java object Class is Not Found -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS
                    , e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017)
            );
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        return lclass;
    }

}
