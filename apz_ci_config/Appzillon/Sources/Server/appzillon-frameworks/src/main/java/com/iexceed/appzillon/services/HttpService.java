package com.iexceed.appzillon.services;

import com.iexceed.appzillon.dao.HttpDetails;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AuthTokenUtil;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;
import com.iexceed.appzillon.utils.XMLExternalEntity;
import org.apache.camel.*;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import static com.iexceed.appzillon.utils.Constants.*;
import static javax.xml.transform.TransformerFactory.newInstance;

/**
 * @author arthanarisamy
 */
public class HttpService implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS, HttpService.class.getName());

    protected HttpDetails httpDtls;

    private Object responsePayLoad;

    private Object requestPayload;

    private Map<String, String> pathParameters = new HashMap<>();

    private static JSONObject buildJsonwithHeaderParams(JSONObject previousJSON, String nodeStruc, String key,
                                                        String value) {
        String[] larry = Utils.split(nodeStruc, ".");
        JSONObject ljsonobject = new JSONObject();
        JSONObject lchildobj = null;
        LOG.debug("newJSON Loop starts larry.length -: {}", larry.length);
        for (int i = 0; i < larry.length; i++) {
            LOG.debug(
                    "newJSON Loop starts i -: {} newJSON Loop starts ljsonobject -: {} checking json has element - {} : {} checking json has element -: {}",
                    i, ljsonobject, i, larry[i], previousJSON.has(larry[i]));
            if (i == 0) {
                LOG.debug("previousJSON.has(larry[i]) -: {}", previousJSON.has(larry[i]));
                if (!previousJSON.has(larry[i])) {
                    ljsonobject = accumulateJSON(previousJSON, key, value, larry, ljsonobject, i);
                    LOG.debug("ljsonobject : {}", ljsonobject.toString());

                } else {
                    LOG.debug("ELSE VAlue of i-: {} and node is present previousJSON - : {}", i, previousJSON);
                    ljsonobject = getPreviousJSON(previousJSON, key, value, larry, ljsonobject, i);

                }
            } else {
                lchildobj = getChildobj(value, larry, ljsonobject, lchildobj, i);
            }
        }
        return ljsonobject;
    }

    private static JSONObject getChildobj(String value, String[] larry, JSONObject ljsonobject, JSONObject lchildobj, int i) {
        if (lchildobj == null) {
            LOG.debug("Child Object is null. Array length - {}, i-: {}", larry.length, i);
            LOG.debug("**** Child node I length -: {} node name -: {} - ljsonobject -: {}", i, larry[i],
                    ljsonobject);
            if (i == larry.length - 1) {
                LOG.debug("First Child node I length -: {} node name -: {} - ljsonobject -: {}", i, larry[i],
                        ljsonobject);
                lchildobj = getJSONObject(larry[i],
                        (JSONObject) ljsonobject.get(larry[i - 1]), larry[i], value);
                LOG.debug("After adding first child -: {}", lchildobj);
            } else {
                lchildobj = (JSONObject) ljsonobject.get(larry[i - 1]);
            }

        } else {
            if (i == larry.length - 1) {
                LOG.debug("&&&&&&&&&&&&&&&&&&& I length -: {}, node name -: {}", i, larry[i]);
                lchildobj = getJSONObject(larry[i],
                        (JSONObject) lchildobj.get(larry[i - 1]), larry[i], value);
            } else {
                LOG.debug("lchildobj-: {}", lchildobj);
                lchildobj = getNewJSONObject(larry[i], (JSONObject) lchildobj.get(larry[i - 1]),
                        larry[i], value);
            }
        }
        return lchildobj;
    }

    private static JSONObject getPreviousJSON(JSONObject previousJSON, String key, String value, String[] larry, JSONObject ljsonobject, int i) {
        if (i == larry.length) {
            JSONObject tempJson = previousJSON.getJSONObject(larry[i]);
            tempJson.put(key, value);
            ljsonobject.put(larry[i], tempJson);
        } else {
            ljsonobject = previousJSON;
            if (!ljsonobject.has(key)) {
                JSONObject tempJson = previousJSON.getJSONObject(larry[i]);
                LOG.debug("tag.getElement(): {}", key);
                tempJson.put(key, value);
                ljsonobject.put(larry[i], tempJson);
            }
        }
        return ljsonobject;
    }

    private static JSONObject accumulateJSON(JSONObject previousJSON, String key, String value, String[] larry, JSONObject ljsonobject, int i) {
        if (!previousJSON.toString().equals("{}")) {
            ljsonobject = previousJSON;
        }
        if (larry.length == 1) {

            JSONObject tmp = new JSONObject();
            tmp.put(key, value);
            LOG.debug("tmp.get(key)-: {}", tmp.get(key));
            ljsonobject.accumulate(larry[i], tmp.get(key));
        } else {
            JSONObject tmp = new JSONObject();
            tmp.put(key, value);
            LOG.debug("tmp.get(key)-: {}", tmp.get(key));
            ljsonobject.accumulate(larry[i], new JSONObject());
        }
        return ljsonobject;
    }

    private static JSONObject getNewJSONObject(String p, JSONObject pjsonobj, String element, String value) {
        JSONObject lobj = new JSONObject();
        try {
            LOG.debug(" Checkign - {}", (pjsonobj.has(p)));
            LOG.debug("pjsonobj -: {}", pjsonobj);
            if (pjsonobj.has(p)) {
                JSONObject tempJSON = pjsonobj.getJSONObject(p);
                LOG.debug("Temp JSON -: {}", tempJSON);
                tempJSON.put(element, value);
                pjsonobj.put(p, tempJSON);
            } else {
                pjsonobj.accumulate(p, lobj);
            }

        } catch (JSONException jx) {
            LOG.error("Exception : ", jx);
        }
        return pjsonobj;

    }

    private static JSONObject getJSONObject(String p, JSONObject pjsonobj, String element, String value) {
        try {
            LOG.debug("pjsonobj -: {}", pjsonobj);
            LOG.debug("getJSONObject -: {} has - {}", p, pjsonobj.has(p));
            if (pjsonobj.has(p)) {
                JSONObject tempJSON = pjsonobj.getJSONObject(p);
                LOG.debug("Temp JSON -: {}", tempJSON);
                tempJSON.put(element, value);
                pjsonobj.put(p, tempJSON);
            } else {
                pjsonobj.accumulate(p, value);
            }

        } catch (JSONException jx) {
            LOG.error("Exception : ", jx);
        }
        return pjsonobj;
    }

    public Object buildRequest(com.iexceed.appzillon.message.Message pMessage, Object pRequestPayLoad,
                               SpringCamelContext pContext) {
        LOG.info("{} Building request for external HTTP service....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String lRequestPayLoad = null;
        LOG.debug("{} HTTP service call type is : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                httpDtls.getCallType());
        if (ServerConstants.HTTP_CALL_TYPE_GET.equalsIgnoreCase(httpDtls.getCallType())) {

            final String queryString = getQueryString(pRequestPayLoad.toString());
            try {
                lRequestPayLoad = new org.apache.http.client.utils.URIBuilder().setPath(queryString).toString();
                LOG.debug("{} After encoded the request for HTTP Service GET method -: {}",
                        ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lRequestPayLoad);
            } catch (Exception e) {
                LOG.error("Exception", e);
            }
        } else {
            lRequestPayLoad = pRequestPayLoad.toString();

            // Removing Header Ref elements from actual payLoad
            lRequestPayLoad = removeHeaderRef(lRequestPayLoad);
            LOG.debug("{} After removing header param from payload : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                    lRequestPayLoad);

            // Removing Query Ref elements from actual payLoad
            lRequestPayLoad = removeQueryRef(lRequestPayLoad);
            LOG.debug("{} After removing query param from payload : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                    lRequestPayLoad);
            lRequestPayLoad = removePathRef(lRequestPayLoad);
            LOG.debug("{} After removing path param from payload : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                    lRequestPayLoad);
            if (ServerConstants.XML.equals(httpDtls.getRequestType())) {
                LOG.info("{} Request type excepted is XML, hence converting JSON to XML....",
                        ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                /** checking for queryString in POST Method */
                Map<String, Object> queryparams = httpDtls.getQueryParameters();
                if (queryparams != null && !queryparams.isEmpty()
                        && queryparams.containsKey(ServerConstants.QUERY_STRING)
                        && queryparams.get(ServerConstants.QUERY_STRING) != null) {
                    LOG.debug("Checking for Query String in POST Method.");
                    String queryStringNodeElement = queryparams.get(ServerConstants.QUERY_STRING) + "";
                    String valueFromPayLoad = getValueFromNodeStruc(queryStringNodeElement,
                            new JSONObject(lRequestPayLoad));
                    LOG.debug("New payLoad in POST Method : {}", valueFromPayLoad);
                    lRequestPayLoad = valueFromPayLoad;
                }

                /** checking for queryString in POST Method END here */
                lRequestPayLoad = ExternalServicesRouter.getJSONtoXML(lRequestPayLoad);
                lRequestPayLoad = appendAttributes(lRequestPayLoad);
            }
        }
        LOG.debug("{} Returning request for HTTP Service -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                lRequestPayLoad);

        return lRequestPayLoad;
    }

    /**
     * @param lRequestPayLoad
     * @return
     * @throws FactoryConfigurationError
     */
    private String appendAttributes(String lRequestPayLoad) throws FactoryConfigurationError {
        try {
            DocumentBuilder docBuilder = XMLExternalEntity.getDocBuilder();
            /*
             * Converting requestPayLoad string to document object for XML parsing
             */
            // updated to handle java.net.MalformedURLException and
            // org.w3c.dom.DOMException: INVALID_CHARACTER_ERR
            Document doc = docBuilder.parse(new InputSource(new StringReader(lRequestPayLoad)));

            /* Obtaining childnodes of the root node */
            Node rootNode = (Node) doc.getFirstChild(); // updated on 19-10-2015
            // to handle header
            // attribute

            /* Obtaining rootnode attributes to the nodeMap */
            NamedNodeMap nodeMap = rootNode.getAttributes();
            /*
             * Adding headerNode attribute and value to attributes
             */

            if (Utils.isNotNullOrEmpty(httpDtls.getHeaderNode())
                    && Utils.isNotNullOrEmpty(httpDtls.getHeaderAttributes())) {
                Attr attributes = doc.createAttribute(httpDtls.getHeaderNode());
                attributes.setNodeValue(httpDtls.getHeaderAttributes());
                /* Adding attributes to the nodeMap */
                nodeMap.setNamedItem(attributes);
            }

            /*
             * Converting document to DOMSource for XML conversion
             */
            DOMSource domSource = new DOMSource(doc);
            /* Converting DOMSource to attributesAddedXML String */
            String attributesAddedXML = getStringFromDoc(domSource);

            lRequestPayLoad = attributesAddedXML;

        } catch (Exception e) {
            LOG.error("Exception while appending attributes to root node : ", e);
        }
        LOG.debug("{} Returning XML after adding attributes -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                lRequestPayLoad);
        return lRequestPayLoad;
    }

    public String getQueryString(String requestPayLoad) {
        LOG.debug("{} Building query Parameters, from request -: {} and the parameters are -: {}",
                ServerConstants.LOGGER_PREFIX_FRAMEWORKS, requestPayLoad, httpDtls.getQueryParameters());
        if (httpDtls.getQueryParameters() == null || httpDtls.getQueryParameters().size() <= 0) {
            return "";
        } else {
            Map<String, Object> dbqueryParam = new HashMap<>();
            try {
                JSONObject screenObj = new JSONObject(requestPayLoad);
                JSONObject screenObj2;
                Map<String, Object> queryparams = httpDtls.getQueryParameters();
                if (queryparams != null && !queryparams.isEmpty()) {
                    // changes for QueryString Start
                    if (queryparams.containsKey(ServerConstants.QUERY_STRING)
                            && queryparams.get(ServerConstants.QUERY_STRING) != null) {
                        String queryStringNodeElement = queryparams.get(ServerConstants.QUERY_STRING) + "";
                        return getValueFromNodeStruc(queryStringNodeElement,
                                new JSONObject(requestPayLoad));
                    }
                    // changes for QueryString End here

                    for (Map.Entry<String, Object> entry : queryparams.entrySet()) {
                        iterateQueryParam(dbqueryParam, screenObj, entry);
                    }
                    LOG.debug("{} the hashmap with query parameter values {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                            dbqueryParam.toString());
                }
            } catch (JSONException jsonex) {
                LOG.error("JSONException : ", jsonex);
            }
            return converttoQueryString(dbqueryParam);
        }

    }

    private void iterateQueryParam(Map<String, Object> dbqueryParam, JSONObject screenObj, Entry<String, Object> entry) {
        JSONObject screenObj2;
        String parameterKey = entry.getKey();
        if (!parameterKey.equals(ServerConstants.QUERY_STRING)) {
            String queryParamFields = (String) entry.getValue();
            String[] nodestructure = queryParamFields.split(SPLIT_BY_BACKWARD_SLASH);
            screenObj2 = screenObj;
            int j = 0;
            for (; j < nodestructure.length - 1; j++) {
                LOG.debug("{} nodestructure[j]: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                        nodestructure[j]);
                screenObj2 = getScreenObj2(screenObj2, nodestructure[j]);
                LOG.debug("{} screenObj2: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, screenObj2);
            }
            LOG.debug("{} screenObj2: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                    screenObj2.toString());
            setParamKey(dbqueryParam, screenObj2, parameterKey, queryParamFields, nodestructure[j]);
        }
    }

    private void setParamKey(Map<String, Object> dbqueryParam, JSONObject screenObj2, String parameterKey, String queryParamFields, String nodestructure) {
        try {
            dbqueryParam.put(parameterKey, screenObj2.get(nodestructure));
        } catch (JSONException e) {
            if (e.getMessage().contains("not found")) {
                LOG.debug("{} Json Object not found, {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                        e.getMessage());
                dbqueryParam.put(parameterKey, queryParamFields);
            } else {
                LOG.error("WARNING !!! parameterKey ", e);
            }
        }
    }

    private JSONObject getScreenObj2(JSONObject screenObj2, String nodeStructure) {
        try {
            screenObj2 = screenObj2.getJSONObject(nodeStructure);
        } catch (JSONException e) {
            LOG.error("WARNING !!! Node Structure ", e);
        }
        return screenObj2;
    }

    public String converttoQueryString(Map<String, Object> dbqueryParamValues) {
        StringBuilder queryString = new StringBuilder();
        if (dbqueryParamValues != null && !dbqueryParamValues.isEmpty()) {
            Iterator<Entry<String, Object>> it = dbqueryParamValues.entrySet().iterator();
            int count = 0;
            while (it.hasNext()) {
                if (count > 0) {
                    queryString = queryString.append(ServerConstants.AMD);
                }
                Map.Entry<String, Object> pairs = it.next();
                queryString = queryString.append(pairs.getKey()).append("=").append(pairs.getValue());
                count++;
            }
        }
        return queryString.toString();
    }

    public Object callService(com.iexceed.appzillon.message.Message pMessage, Object pRequestPayLoad,
                              SpringCamelContext pContext) {
        Exchange exchange = null;
        String jsonContent = "application/json;charset=UTF-8";
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        ProducerTemplate producer = FrameworksStartup.getInstance().getProducerTemplate();
        getHttpDetails(interfaceId, appId, pContext);
        pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad,
                httpDtls.getAutoGenElementMap(), httpDtls.getTranslationElementMap());
        pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad + ""));
        LOG.debug("{} After Appending Request Json With MaskedId : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                pMessage.getRequestObject().getRequestJson());
        String endpointURI = createEndpointURI(pMessage, pContext);
        LOG.debug("{} QueryParameters: {}, call Type: {}, requestType: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                httpDtls.getQueryParameters(), httpDtls.getCallType(), httpDtls.getRequestType());
        int timeOut = httpDtls.getTimeOut() * 1000;
        /**
         * Below changes are made by Samy on 04/03/2016 To enhance support for Path
         * Parameters to consume RESTFull services using HTTPInterface.
         */
        endpointURI = appendPathParameters(endpointURI, pMessage);

        /**
         * Changes End
         */
        applySSL(pContext, timeOut);
        HttpComponent httpComponent = (HttpComponent) pContext.getComponent(ServerConstants.CAMEL_COMPONENT_HTTP);
        httpComponent.setConnectionRequestTimeout(timeOut);
        httpComponent.setSocketTimeout(timeOut);
        httpComponent.setConnectTimeout(timeOut);

        String useCustomHTTP = "Y";//PropertyUtils.getPropValue(appId, "USE_CUSTOM_HTTP");
        LOG.debug("{} CustomHttp useCustomHTTP flag = {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, useCustomHTTP);
		//LOG.error("{} CustomHttp useCustomHTTP flag = {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, useCustomHTTP);
        final String contentType = httpDtls.getPayLoadType().equalsIgnoreCase(ServerConstants.CONTENT_TYPE_VALUE) ? jsonContent : httpDtls.getPayLoadType();
        final Map<String, Object> lHeaderAttributesMap = buildHeaderParamMap(new JSONObject(pRequestPayLoad + ""), pMessage);
        LOG.debug("{} After Building HeaderMap -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lHeaderAttributesMap);
        try {
            if (ServerConstants.HTTP_CALL_TYPE_GET.equalsIgnoreCase(httpDtls.getCallType())) {
                String queryString = (String) buildRequest(pMessage, pRequestPayLoad, pContext);
                final String encqueryString = queryString;
                String lQueryParameters = queryString;
                if (Utils.isNotNullOrEmpty(queryString) && queryString.trim().length() > 0) {
                    if (lQueryParameters.startsWith("/")) {
                        lQueryParameters = lQueryParameters.replaceFirst("/", "");
                    }
                    endpointURI = endpointURI + "&" + lQueryParameters;
                    LOG.debug("{} endpointURI -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpointURI);
                }
                Utils.setExtTime(pMessage, "S");
                if ("Y".equals(useCustomHTTP)) {
                    JSONObject lResponse = callCustomHttp(endpointURI, httpDtls, contentType, lHeaderAttributesMap, null);
                    Utils.setExtTime(pMessage, "E");
                    return lResponse;
                } else {
                    exchange = producer.request(endpointURI, new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            LOG.debug("{} Query Parameter is set -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                                    encqueryString);
                            CaseInsensitiveMap caseInsensitiveMap = new CaseInsensitiveMap(lHeaderAttributesMap);

                            if (!caseInsensitiveMap.containsKey(Exchange.HTTP_METHOD.toLowerCase())) {
                                caseInsensitiveMap.put(Exchange.HTTP_METHOD, httpDtls.getCallType());
                            }
                            if (!caseInsensitiveMap.containsKey(Exchange.CONTENT_TYPE.toLowerCase())) {
                                caseInsensitiveMap.put(Exchange.CONTENT_TYPE, contentType);
                            }
                            if (!caseInsensitiveMap.containsKey(ACCEPT.toLowerCase())) {
                                caseInsensitiveMap.put(ACCEPT, contentType);
                            }
                            if (!caseInsensitiveMap.containsKey(CONNECTION.toLowerCase())) {
                                caseInsensitiveMap.put(CONNECTION, "close");
                            }

                            LOG.debug("{} Check Setting final HeaderMap 1 -: {}",
                                    ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exchange.getMessage().getHeaders());
                            if (!caseInsensitiveMap.isEmpty()) {
                                LOG.debug("{} Setting final HeaderMap -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                                        caseInsensitiveMap);
                                exchange.getMessage().setHeaders(caseInsensitiveMap);
                            }
                            LOG.debug("{} Header Attributes Map is set -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                                    caseInsensitiveMap);
                        }
                    });
                }
                Utils.setExtTime(pMessage, "E");
                requestPayload = encqueryString;
            } else if (ServerConstants.HTTP_CALL_TYPE_POST.equalsIgnoreCase(httpDtls.getCallType())
                    || ServerConstants.HTTP_CALL_TYPE_DELETE.equalsIgnoreCase(httpDtls.getCallType())
                    || ServerConstants.HTTP_CALL_TYPE_PUT.equalsIgnoreCase(httpDtls.getCallType())) {

                LOG.debug("{} IN POST/PUT/DELETE Type", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                String lPayload = (String) buildRequest(pMessage, pRequestPayLoad, pContext);
                /*
                 * Checking if the request payload built is empty JSON. if empty, no payload is
                 * set to the HTTP body. Changes made by Samy on 06/07/2017 -- Start
                 */
                /*
                 */
                final String payload;
                if (ServerConstants.XML.equals(httpDtls.getRequestType())) {
                    payload = lPayload;
                } else {
                    payload = new JSONObject(lPayload).toString(0);
                }

                LOG.debug("{} Payload: {}, ContentType: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, payload,
                        contentType);
                final String pQueryString = pRequestPayLoad.toString();
                String lQueryParameters = null;
                try {
                    String queryString = getQueryString(pQueryString);
                    lQueryParameters = new org.apache.http.client.utils.URIBuilder().setPath(queryString).toString();
                    LOG.debug(
                            "{} After encoded the request for HTTP Service POST/PUT/DELETE method -: {} parameters length -: {}",
                            ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lQueryParameters, lQueryParameters.length());

                    if (Utils.isNotNullOrEmpty(lQueryParameters) && lQueryParameters.trim().length() > 0) {
                        LOG.debug("{} setting query paramters....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                        if (lQueryParameters.startsWith("/")) {
                            lQueryParameters = lQueryParameters.replaceFirst("/", "");
                        }
                        endpointURI = endpointURI + "&" + lQueryParameters;
                    }
                    LOG.debug("{} endpointURI : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpointURI);
                } catch (Exception e) {
                    LOG.error("Exception", e);

                }
                Utils.setExtTime(pMessage, "S");
                if ("Y".equals(useCustomHTTP)) {
                    JSONObject lResponse = callCustomHttp(endpointURI, httpDtls, contentType, lHeaderAttributesMap,
                            payload);
                    Utils.setExtTime(pMessage, "E");
                    return lResponse;
                } else {
                    exchange = producer.request(endpointURI, new Processor() {

                        public void process(Exchange exchange) throws Exception {
                            CaseInsensitiveMap caseInsensitiveMap = new CaseInsensitiveMap(lHeaderAttributesMap);

                            if (!caseInsensitiveMap.containsKey(Exchange.HTTP_METHOD.toLowerCase())) {
                                caseInsensitiveMap.put(Exchange.HTTP_METHOD, httpDtls.getCallType());
                            }
                            if (!caseInsensitiveMap.containsKey(Exchange.CONTENT_TYPE.toLowerCase())) {
                                caseInsensitiveMap.put(Exchange.CONTENT_TYPE, contentType);
                            }
                            if (!caseInsensitiveMap.containsKey(ACCEPT.toLowerCase())) {
                                caseInsensitiveMap.put(ACCEPT, contentType);
                            }
                            if (!caseInsensitiveMap.containsKey(CONNECTION.toLowerCase())) {
                                caseInsensitiveMap.put(CONNECTION, "close");
                            }

                            LOG.debug("{} Check Setting final HeaderMap 1 -: {}",
                                    ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exchange.getMessage().getHeaders());
                            if (!caseInsensitiveMap.isEmpty()) {
                                LOG.debug("{} Setting final HeaderMap -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                                        caseInsensitiveMap);
                                exchange.getMessage().setHeaders(caseInsensitiveMap);
                            }
                            if (!ServerConstants.HTTP_CALL_TYPE_DELETE.equalsIgnoreCase(httpDtls.getCallType())) {
                                exchange.getMessage().setBody(payload);
                            }

                            LOG.debug("{} Check Setting final HeaderMap 2 -: {}",
                                    ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exchange.getMessage().getHeaders());
                        }
                    });
                }
                Utils.setExtTime(pMessage, "E");
                requestPayload = payload;
            } else {
                LOG.error("{} Only POST, GET, PUT and DELETE methods are allowed : {}",
                        ServerConstants.LOGGER_PREFIX_FRAMEWORKS, httpDtls.getCallType());
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                        .getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_026.toString());
                exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_026));
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            }
        } finally {
            /* Patch Added for HTTPS  1/2 Start*/
            LOG.debug("{} Closing idle connections.....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            try{
                if (ServerConstants.YES.equalsIgnoreCase(httpDtls.getSSLRequired())) {
                     httpComponent = (HttpComponent) pContext
                            .getComponent(ServerConstants.CAMEL_COMPONENT_HTTPS);
                    LOG.debug("Connection Timeout:{},Socket Timeout:{},Connection Request Timeout:{}", httpComponent.getConnectTimeout(), httpComponent.getSocketTimeout(), httpComponent.getConnectionRequestTimeout());
                }else{
                     httpComponent = (HttpComponent) pContext
                            .getComponent(ServerConstants.CAMEL_COMPONENT_HTTP);
                    LOG.debug("Connection Timeout:{},Socket Timeout:{},Connection Request Timeout:{}", httpComponent.getConnectTimeout(), httpComponent.getSocketTimeout(), httpComponent.getConnectionRequestTimeout());
                }

                 PoolingHttpClientConnectionManager connectionManager = (PoolingHttpClientConnectionManager)httpComponent.getClientConnectionManager();
                 //time in milliseconds
                 if(connectionManager != null){
                     LOG.debug("Max Connections: {}, Available Connections:{}, " +
                                     "Leased Connections:{}, Pending Connections:{}",
                             connectionManager.getTotalStats().getMax(),
                             connectionManager.getTotalStats().getAvailable(),
                             connectionManager.getTotalStats().getLeased(),
                             connectionManager.getTotalStats().getPending());
                
                    connectionManager.closeExpiredConnections();
                    connectionManager.closeIdleConnections(10, TimeUnit.NANOSECONDS);
                }
                LOG.debug( "{} Closed idle connections.....",ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
             } catch (Exception ex){
                 LOG.error( "{} PoolingHttpClientConnectionManager Exception :{} ", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
             }
            /* Patch Added for HTTPS  1/2 end*/
        }

        JSONObject lResponse = null;
        Object lOutput = null;
        if (exchange.getException() == null) {
            Message out = exchange.getMessage();
            try {

                lOutput = processResponse(pMessage, out, pContext);
                lResponse = new JSONObject(lOutput.toString());

                ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.SUCCESS, responsePayLoad, requestPayload);
            } catch (Exception ex) {
                LOG.error("{} Class -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex.getClass());
                if (lOutput == null) {
                    return new JSONObject().put("response", "");
                }
                String response = lOutput.toString();
                if (ex instanceof ClassCastException || ex instanceof JSONException) {
                    try {
                        lResponse = new JSONObject(response);
                        LOG.debug("{} Response put in JSONObject : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                                lResponse);
                    } catch (JSONException csex) {
                        LOG.error(
                                "{} Exception while putting in JSONObject, since external response is not a JSONObject.",
                                ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                        LOG.debug("{} Now Putting response in JSONArray", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                        try {
                            lResponse = new JSONArray(response);
                            LOG.debug("{} JSONArray Casting -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                                    lResponse);
                        } catch (JSONException jsonEx) {
                            LOG.error("{} Exception while putting in JSONArray, since response is not an array.",
                                    ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                            lResponse = new JSONObject().put("response", response.trim());
                            LOG.debug("{} After Putting in the response : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                                    lResponse);
                        }
                    }
                }
            }
        } else {
            LOG.error("Exception from external system hence will be throwing an exception....",
                    exchange.getException());
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, exchange.getException().getMessage(),
                    requestPayload);
            if (exchange.getException() instanceof HttpOperationFailedException) {
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                        .getExternalServicesRouterExceptionInstance();
                HttpOperationFailedException exp = exchange
                        .getException(HttpOperationFailedException.class);
                exsrvcallexp.setCode(String.valueOf(exp.getStatusCode()));
                exsrvcallexp.setMessage(exp.getStatusText());
                JSONObject jsonOut = new JSONObject();
                if (exp.getResponseHeaders() != null) {
                    jsonOut = buildJsonwithHeaderParams((Map) exp.getResponseHeaders(), jsonOut);
                }
                if (exp.getResponseBody() != null) {
                    pMessage.getResponseObject()
                            .setResponseJson(jsonOut.put(ServerConstants.HTTP_ERROR_BODY, exp.getResponseBody()));
                }
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            } else {
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                        .getExternalServicesRouterExceptionInstance();

                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
                exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;

            }
        }
        return lResponse;
    }

    private JSONObject callCustomHttp(String endpointURI, HttpDetails httpDtls, final String contentType,
            final Map<String, Object> lHeaderAttributesMap, final String payload) {

        int timeOut = httpDtls.getTimeOut() * 1000;
        String methodType = httpDtls.getCallType();
        CaseInsensitiveMap caseInsensitiveMap = new CaseInsensitiveMap(lHeaderAttributesMap);
        if (!caseInsensitiveMap.containsKey(Exchange.HTTP_METHOD.toLowerCase())) {
            caseInsensitiveMap.put(Exchange.HTTP_METHOD, methodType);
        }
        if (!caseInsensitiveMap.containsKey(Exchange.CONTENT_TYPE.toLowerCase())) {
            caseInsensitiveMap.put(Exchange.CONTENT_TYPE, contentType);
        }
        if (!caseInsensitiveMap.containsKey(ACCEPT.toLowerCase())) {
            caseInsensitiveMap.put(ACCEPT, contentType);
        }
        if (!caseInsensitiveMap.containsKey(CONNECTION.toLowerCase())) {
            caseInsensitiveMap.put(CONNECTION, "close");
        }
        try {
            LOG.debug("{} CustomHttp  with method {} and endpointURI -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                    httpDtls.getCallType(), endpointURI);
            LOG.debug("{} callCustomHttp Headers: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, caseInsensitiveMap);
            LOG.debug("{} callCustomHttp payload: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, payload);
            if (ServerConstants.HTTP_CALL_TYPE_GET.equalsIgnoreCase(methodType)) {
                HttpGet reqGet = new HttpGet(endpointURI);
                caseInsensitiveMap.forEach((key, value) -> reqGet.setHeader(key.toString(), value.toString()));
                return httpExecuteService(reqGet, timeOut);
            } else if (ServerConstants.HTTP_CALL_TYPE_DELETE.equalsIgnoreCase(methodType)) {
                HttpDelete reqDelete = new HttpDelete(endpointURI);
                caseInsensitiveMap.forEach((key, value) -> reqDelete.setHeader(key.toString(), value.toString()));
                return httpExecuteService(reqDelete, timeOut);
            } else if (ServerConstants.HTTP_CALL_TYPE_POST.equalsIgnoreCase(methodType)) {
                HttpPost reqPost = new HttpPost(endpointURI);
                caseInsensitiveMap.forEach((key, value) -> reqPost.setHeader(key.toString(), value.toString()));
                StringEntity reqBody = new StringEntity(payload);
                reqPost.setEntity(reqBody);
                return httpExecuteService(reqPost, timeOut);
            } else if (ServerConstants.HTTP_CALL_TYPE_PUT.equalsIgnoreCase(methodType)) {
                HttpPut reqPut = new HttpPut(endpointURI);
                caseInsensitiveMap.forEach((key, value) -> reqPut.setHeader(key.toString(), value.toString()));
                StringEntity reqBody = new StringEntity(payload);
                reqPut.setEntity(reqBody);
                return httpExecuteService(reqPut, timeOut);
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (Exception ex) {
            LOG.error("Error in request {}", ex);
            LOG.debug("{} Setting as empty json string", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        }
        return new JSONObject();
    }

    public void applySSL(SpringCamelContext pContext, int timeOut) {

        Object keyStorePath = null;
        Object keyStoreCred = null;
        Object trustStorePath = null;
        Object trustStoreCred = null;

        String trustStrPath = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
                ServerConstants.TRUST_STORE_PATH);
        String trustStrPwd = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
                ServerConstants.TRUST_STORE_PIN);
        LOG.debug("Reading trustStrPath : {}", trustStrPath);
        LOG.debug("Reading trustStrPwd : {}", trustStrPwd);
        String cloudName = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
                ServerConstants.READ_PEM_FROM_VAULT);
        if (Utils.isNotNullOrEmpty(cloudName) && cloudName.equalsIgnoreCase(ServerConstants.CLOUD_AZURE)) {
            LOG.debug("Reading trustStrPath  and password from vault ");
            String authToken = AuthTokenUtil.getAuthTokenKeyVault(ServerConstants.SERVER_PROP_FILE_CONSTANT);
            trustStoreCred = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(trustStrPwd), authToken);
            trustStorePath = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(trustStrPath), authToken);

        } else {
            if (trustStrPath != null && trustStrPath.startsWith(ENV)) {
                // reading from environment variable
                LOG.debug("Reading truststore path from env variable : {}", trustStrPath);
                trustStorePath = System.getenv(trustStrPath.replace(ENV, "").replace("}", ""));
            } else if (trustStrPath != null && trustStrPath.startsWith(SYS)) {
                // reading from system property
                LOG.debug("Reading truststore path from system property : {}", trustStrPath);
                trustStorePath = System.getProperty(trustStrPath.replace(SYS, "").replace("}", ""));
            } else {
                trustStorePath = trustStrPath;
            }

            if (trustStrPwd != null && trustStrPwd.startsWith(ENV)) {
                // reading from environment variable
                LOG.debug("Reading truststore password from env variable : {}", trustStrPwd);
                trustStoreCred = System.getenv(trustStrPwd.replace(ENV, "").replace("}", ""));
            } else if (trustStrPwd != null && trustStrPwd.startsWith(SYS)) {
                // reading from system property
                LOG.debug("Reading truststore password from system property : {}", trustStrPwd);
                trustStoreCred = System.getProperty(trustStrPwd.replace(SYS, "").replace("}", ""));
            } else {
                trustStoreCred = trustStrPwd;
            }
        }

        String keyStrPath = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
                ServerConstants.KEY_STORE_PATH);
        String keyStrPwd = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
                ServerConstants.KEY_STORE_PIN);
        LOG.debug("Reading keyStrPath : {}", keyStrPath);
        LOG.debug("Reading keyStrPwd : {}", keyStrPwd);

        if (Utils.isNotNullOrEmpty(cloudName) && cloudName.equalsIgnoreCase(ServerConstants.CLOUD_AZURE)) {
            LOG.debug("Reading trustStrPath  and password from vault");
            String authToken = AuthTokenUtil.getAuthTokenKeyVault(ServerConstants.SERVER_PROP_FILE_CONSTANT);
            keyStoreCred = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(keyStrPwd), authToken);
            keyStorePath = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(keyStrPath), authToken);

        } else {
            if (keyStrPath != null && keyStrPath.startsWith(ENV)) {
                // reading from environment variable
                LOG.debug("Reading keystore path from env variable : {}", keyStrPath);
                keyStorePath = System.getenv(keyStrPath.replace(ENV, "").replace("}", ""));
            } else if (keyStrPath != null && keyStrPath.startsWith(SYS)) {
                // reading from system property
                LOG.debug("Reading keystore path from system property : {}", keyStrPath);
                keyStorePath = System.getProperty(keyStrPath.replace(SYS, "").replace("}", ""));
            } else {
                keyStorePath = keyStrPath;
            }

            if (keyStrPwd != null && keyStrPwd.startsWith(ENV)) {
                // reading from environment variable
                LOG.debug("Reading keystore password from env variable : {}", keyStrPwd);
                keyStoreCred = System.getenv(keyStrPwd.replace(ENV, "").replace("}", ""));
            } else if (keyStrPwd != null && keyStrPwd.startsWith(SYS)) {
                // reading from system property
                LOG.debug("Reading keystore password from system property : {}", keyStrPwd);
                keyStoreCred = System.getProperty(keyStrPwd.replace(SYS, "").replace("}", ""));
            } else {
                keyStoreCred = keyStrPwd;
            }
        }

        LOG.debug("Actual Keystore Path : {}", keyStorePath);
        if (keyStorePath != null) {
            System.setProperty(ServerConstants.SYSTEM_PROPERTY_SSL_KEY_STORE, keyStorePath.toString().trim());
            System.setProperty(ServerConstants.SYSTEM_PROPERTY_SSL_KEY_STORE_PIN,
                    keyStoreCred != null ? keyStoreCred.toString().trim() : "");

        }
        LOG.debug("Actual truststore Path : {}", trustStorePath);
        if (trustStorePath != null) {
            System.setProperty(ServerConstants.SYSTEM_PROPERTY_SSL_TRUST_STORE, trustStorePath.toString().trim());
            System.setProperty(ServerConstants.SYSTEM_PROPERTY_SSL_TRUST_STORE_PIN,
                    trustStoreCred != null ? trustStoreCred.toString().trim() : "");
        }
        /* Patch Added for HTTPS  2/2 Start*/
        HttpComponent httpComponent = (HttpComponent) pContext
                .getComponent(ServerConstants.CAMEL_COMPONENT_HTTPS);
        httpComponent.setConnectionRequestTimeout(timeOut);
        httpComponent.setSocketTimeout(timeOut);
        httpComponent.setConnectTimeout(timeOut);
        /* Patch Added for HTTPS  2/2 End*/
    }

    public String createEndpointURI(com.iexceed.appzillon.message.Message pMessage, SpringCamelContext pContext) {
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        String camelID = (appId + "__" + interfaceId).replaceAll(SPLIT_BY_BACKWARD_SLASH, "__");
        LOG.debug("{} Getting endpoint with id {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, camelID);
        String endpoint = pContext.getEndpoint(camelID).getEndpointUri();
        endpoint = ServicesUtil.enrichWithProperties(pMessage.getHeader().getAppId(), endpoint);
        LOG.debug("URI enpoint after replacing properties : {}", endpoint);
        String appendOption = "?headerFilterStrategy=appzillonHttpHeaderFilterStrategy&useSystemProperties=true";
        LOG.debug("{} Option appended {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, appendOption);
        endpoint = endpoint.concat(appendOption);
        LOG.debug("{} Endpoint used after adding params {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpoint);
        return endpoint;
    }

    public Object processResponse(com.iexceed.appzillon.message.Message pMessage, Object pResponse,
                                  SpringCamelContext pContext) {
        Message out = (Message) pResponse;
        String output = null;
        Integer statusHeader = (Integer) out.getHeader(Exchange.HTTP_RESPONSE_CODE);

        LOG.debug("{} status header is {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, statusHeader);
        if (statusHeader != null) {
            try {
                output = out.getMandatoryBody(String.class);
                responsePayLoad = output;
                if (Utils.isNullOrEmpty(output)) {
                    output = "{}";
                    LOG.debug("{} out.getMandatoryBody(String.class) is set as empty json string",
                            ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                }

                if (ServerConstants.XML.equalsIgnoreCase(httpDtls.getResponseType())) {
                    output = ExternalServicesRouter.getXMLToJSON(output);
                }
                JSONObject jsonOut = new JSONObject(output);
                output = buildJsonwithHeaderParams(out.getHeaders(), jsonOut).toString();
            } catch (InvalidPayloadException e) {
                LOG.error("{} callExternalService - Error parsing external response: {}",
                        ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                        .getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
                exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            } catch (JSONException e) {
                LOG.error("{} callExternalService - Error parsing external response: {}",
                        ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            }
            LOG.debug("{} Super Class processResponse: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, output);
        }
        return output;
    }

    public String getStringFromDoc(DOMSource dsource) {
        try {
            if (dsource.getNode().getPrefix() != null) {
                LOG.debug("{} prefix: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, dsource.getNode().getPrefix());
                dsource.getNode().setPrefix(null);
            }
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            /*
             * Veracode fix: Improper Restriction of XML External Entity Reference (CWE ID
             * 611)
             */
            TransformerFactory tranFactory = newInstance();

            setAttribute(tranFactory);

            Transformer transformer = tranFactory.newTransformer();
            transformer.transform(dsource, result);
            writer.flush();
            return writer.toString();
        } catch (TransformerException ex) {
            LOG.error("{} TransformerException : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
            return null;
        }
    }

    private void setAttribute(TransformerFactory tranFactory) {
        try {
            tranFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tranFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (IllegalArgumentException e) {
            LOG.error("jaxp 1.5 feature not supported: ", e.getMessage());
        }
    }

    public void getHttpDetails(String pInterfaceID, String pAppId, SpringCamelContext pContext) {
        LOG.debug("{} getTargetNameSpace ifID: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pInterfaceID);

        httpDtls = (HttpDetails) ExternalServicesRouter.injectBeanFromSpringContext(pAppId + "_" + pInterfaceID,
                pContext);
        LOG.debug("{} QueryParameters: {}, call Type: {}, requestType: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                httpDtls.getQueryParameters(), httpDtls.getCallType(), httpDtls.getRequestType());
        int timeOut = httpDtls.getTimeOut();
        /**
         * Below changes are made by Vinod as part of At app level, service time out
         * should be configurable. Appzillon 3.1 - 63 -- Start
         */
        if (timeOut == 0) {

            LOG.warn("{} Timeout value not configured will use default timeOut",
                    ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            timeOut = Integer.parseInt(PropertyUtils.getPropValue(pAppId, ServerConstants.DEFAULT_TIMEOUT).trim());
        }
        /** Appzillon 3.1 - 63 -- END */
        httpDtls.setTimeOut(timeOut);
    }

    /*
     * Below methods are added by Samy on 11/08/2105 to address passing Parameters
     * in request header
     */
    private Map<String, Object> buildHeaderParamMap(JSONObject prequestPayLoad,
                                                    com.iexceed.appzillon.message.Message pMessage) {

        Map<String, Object> lheaderParamMap = new HashMap<>();
        if (httpDtls.getRequestHeaderParams() != null && httpDtls.getRequestHeaderParams().size() > 0) {
            Map<String, String> lRequestParamMap = httpDtls.getRequestHeaderParams();
            for (Entry<String, String> nodeStruc : lRequestParamMap.entrySet()) {
                String headerValue = getValueFromNodeStruc(nodeStruc.getValue(), prequestPayLoad);
                lheaderParamMap.put(nodeStruc.getKey(), headerValue);
            }
        }

        return lheaderParamMap;
    }

    private String getValueFromNodeStruc(String pNodeStruc, JSONObject prequestPayLoad) {
        LOG.debug(
                "{} getValueFromNodeStruc, Building query Parameters, from request -: {} and the parameters are -: {}",
                ServerConstants.LOGGER_PREFIX_FRAMEWORKS, prequestPayLoad, httpDtls.getQueryParameters());
        String lvalue = "";
        try {
            JSONObject lTempJSON = prequestPayLoad;
            LOG.debug("{} **** pNodeStruc: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pNodeStruc);
            String[] nodestructure = pNodeStruc.split(SPLIT_BY_BACKWARD_SLASH);
            LOG.debug("{} **** nodestructure.length: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                    nodestructure.length);
            int j = 0;
            for (; j < nodestructure.length - 1; j++) {
                LOG.debug("{} **** nodestructure[j]: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, nodestructure[j]);
                lTempJSON = lTempJSON.getJSONObject(nodestructure[j]);
                LOG.debug("{} $$$$ lTempJSON: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lTempJSON);
            }
            /**
             * Below condition put for taking JSONObject if JSONObject coming as element
             * value, in the case of POST Method XML comes as JSONObject, and we build XML
             * from JSONObject, added by ripu 28-03-2016
             */
            try {
                lvalue = lTempJSON.get(nodestructure[j]) + "";
            } catch (JSONException jsonex) {
                if (jsonex.getMessage().contains("not found")) {
                    LOG.error("{} JSON object Not found, {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                            jsonex.getMessage());
                    lvalue = pNodeStruc;
                } else {
                    LOG.error("Not String Value, So Its Fetching JSON Object ", jsonex);
                    lvalue = lTempJSON.getJSONObject(nodestructure[j]) + "";
                }
                LOG.debug("lvalue : {}", lvalue);
            }
            LOG.debug("{} **** lTempJSON: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lTempJSON.toString());
        } catch (JSONException jsonex) {
            LOG.error("{} JSONException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, jsonex);
        }
        return lvalue;
    }

    private JSONObject buildJsonwithHeaderParams(Map<String, Object> lheaderParamMap, JSONObject pHeaderParamJson) {
        Map<String, String> lResponseParamMap = httpDtls.getResponseHeaderParams();
        if (httpDtls.getResponseHeaderParams() != null && httpDtls.getResponseHeaderParams().size() > 0) {
            Iterator<Entry<String, String>> lattributeNodeStrucIterator = lResponseParamMap.entrySet().iterator();
            while (lattributeNodeStrucIterator.hasNext()) {
                Entry<String, String> headerEntry = lattributeNodeStrucIterator.next();
                String key = headerEntry.getKey();
                if (lheaderParamMap.get(key) != null) {
                    pHeaderParamJson = buildJsonwithHeaderParams(pHeaderParamJson, headerEntry.getValue(), key,
                            lheaderParamMap.get(key).toString());
                }
            }
        }
        return pHeaderParamJson;
    }

    private String removeHeaderRef(String payLoad) {
        JSONObject requestPayLoad = new JSONObject(payLoad);
        if (httpDtls.getRequestHeaderParams() != null && httpDtls.getRequestHeaderParams().size() > 0) {
            Map<String, String> lHeaderParams = httpDtls.getRequestHeaderParams();
            int i = 0;
            for (Map.Entry<String, String> entry : lHeaderParams.entrySet()) {
                LOG.debug(KEY_VALUE, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, entry.getKey(), entry.getValue());
                String nodes = entry.getValue();
                LOG.debug(NODE_KEY, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, nodes);
                String trimmedNodes = Utils.split(nodes, ".")[i];
                LOG.debug("{} trimmedNode : {} and requestPayLoad - {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                        trimmedNodes, requestPayLoad);
                if (requestPayLoad.has(trimmedNodes)) {
                    requestPayLoad.remove(Utils.split(nodes, ".")[i]).toString();
                }
            }
        }
        return requestPayLoad.toString();
    }

    private String removeQueryRef(String payLoad) {
        JSONObject requestPayLoad = new JSONObject(payLoad);
        if (httpDtls.getQueryParameters() != null && httpDtls.getQueryParameters().size() > 0) {
            Map<String, Object> lqueryParams = httpDtls.getQueryParameters();
            int i = 0;
            for (Map.Entry<String, Object> entry : lqueryParams.entrySet()) {
                LOG.debug(KEY_VALUE, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, entry.getKey(), entry.getValue());
                String nodes = entry.getValue().toString();
                LOG.debug(NODE_KEY, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, nodes);
                String[] nodesToRemove = Utils.split(nodes, ".");
                if (nodesToRemove.length != 0) {
                    String trimmedNodes = nodesToRemove[i];
                    LOG.debug("{} trimmedNode : {} and requestPayLoad - {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                            trimmedNodes, requestPayLoad);
                    if (requestPayLoad.has(trimmedNodes)) {
                        requestPayLoad.remove(nodesToRemove[i]).toString();
                    }
                }
            }
        }
        return requestPayLoad.toString();
    }

    private String removePathRef(String payLoad) {
        JSONObject requestPayLoad = new JSONObject(payLoad);
        if (pathParameters != null && pathParameters.size() > 0) {
            int i = 0;
            for (Map.Entry<String, String> entry : pathParameters.entrySet()) {
                LOG.debug(KEY_VALUE, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, entry.getKey(), entry.getValue());
                String nodes = entry.getValue();
                LOG.debug(NODE_KEY, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, nodes);
                String[] nodesToRemove = Utils.split(nodes, ".");
                if (nodesToRemove.length != 0) {
                    String trimmedNodes = nodesToRemove[i];
                    LOG.debug("{} trimmedNode : {} and requestPayLoad: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                            trimmedNodes, requestPayLoad);
                    if (requestPayLoad.has(trimmedNodes)) {
                        requestPayLoad.remove(nodesToRemove[i]).toString();
                    }
                }
            }
        }
        return requestPayLoad.toString();
    }

    /**
     * Below changes are made by Samy on 04/03/2016 To enhance support for Path
     * Parameters to consume RESTFull services using HTTPInterface.
     *
     * @param pURL
     * @param pMessage
     * @return
     */
    private String appendPathParameters(String pURL, com.iexceed.appzillon.message.Message pMessage) {
        LOG.debug("{} appendPathParameters Encoded URL -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pURL);
        String lURL = "";
        try {
            lURL = URLDecoder.decode(pURL, ServerConstants.CHARACTER_ENCODING_UTF_8);
            LOG.debug("{} appendPathParameters Decoded URL -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lURL);
            if (lURL.contains(ServerConstants.PATH_PARAMETERS_LEFT_FILLER)
                    && lURL.contains(ServerConstants.PATH_PARAMETERS_RIGHT_FILLER)) {
                while ((lURL.contains(ServerConstants.PATH_PARAMETERS_LEFT_FILLER)
                        && lURL.contains(ServerConstants.PATH_PARAMETERS_RIGHT_FILLER))) {
                    LOG.debug("{} appendPathParameters lURL -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lURL);
                    int lfirstIndex = lURL.indexOf(ServerConstants.PATH_PARAMETERS_LEFT_FILLER);
                    int rfirstIndex = lURL.indexOf(ServerConstants.PATH_PARAMETERS_RIGHT_FILLER);
                    LOG.debug("{} appendPathParameters lfirstIndex -: {} and rfirstIndex -: {}",
                            ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lfirstIndex, rfirstIndex);
                    String nodeStructure = lURL.substring(lfirstIndex + 1, rfirstIndex);
                    LOG.debug("{} appendPathParameters nodeStructure -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                            nodeStructure);
                    LOG.debug("{} appendPathParameters RequestPayLoad -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                            pMessage.getRequestObject().getRequestJson());
                    String lValueFromNodeStruc = getValueFromNodeStruc(nodeStructure,
                            pMessage.getRequestObject().getRequestJson());
                    LOG.debug("{} appendPathParameters lValueFromNodeStruc -: {}",
                            ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lValueFromNodeStruc);
                    lURL = lURL.replace(ServerConstants.PATH_PARAMETERS_LEFT_FILLER + nodeStructure
                            + ServerConstants.PATH_PARAMETERS_RIGHT_FILLER, lValueFromNodeStruc);
                    LOG.debug("{} After appending Path Parameter -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                            lURL);
                    pathParameters.put(ServerConstants.PATH_PARAMETERS_LEFT_FILLER + lValueFromNodeStruc
                            + ServerConstants.PATH_PARAMETERS_RIGHT_FILLER, nodeStructure);
                }
            } else if ((lURL.contains(ServerConstants.NEW_PATH_PARAMETERS_LEFT_FILLER)
                    && lURL.contains(ServerConstants.NEW_PATH_PARAMETERS_RIGHT_FILLER))) {
                while (lURL.contains(ServerConstants.NEW_PATH_PARAMETERS_LEFT_FILLER)
                        && lURL.contains(ServerConstants.NEW_PATH_PARAMETERS_RIGHT_FILLER)) {

                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters  lURL -:" + lURL);
                    int lfirstIndex = lURL.indexOf(ServerConstants.NEW_PATH_PARAMETERS_LEFT_FILLER);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters  lfirstIndex -:"
                            + lfirstIndex);
                    int rfirstIndex = lURL.indexOf(ServerConstants.NEW_PATH_PARAMETERS_RIGHT_FILLER);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters rfirstIndex -:"
                            + rfirstIndex);
                    String nodeStructure = lURL.substring(lfirstIndex + 4, rfirstIndex);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters nodeStructure -:"
                            + nodeStructure);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters RequestPayLoad -:"
                            + pMessage.getRequestObject().getRequestJson());
                    String lValueFromNodeStruc = getValueFromNodeStruc(nodeStructure,
                            pMessage.getRequestObject().getRequestJson());
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "appendPathParameters lValueFromNodeStruc -:"
                            + lValueFromNodeStruc);
                    lURL = lURL.replace(ServerConstants.NEW_PATH_PARAMETERS_LEFT_FILLER + nodeStructure
                            + ServerConstants.NEW_PATH_PARAMETERS_RIGHT_FILLER, lValueFromNodeStruc);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After appending Path Parameter -:" + lURL);
                    pathParameters.put(ServerConstants.PATH_PARAMETERS_LEFT_FILLER + lValueFromNodeStruc
                            + ServerConstants.PATH_PARAMETERS_RIGHT_FILLER, nodeStructure);
                }
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error("{} UnsupportedEncodingException -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            lURL = pURL;
        }
        LOG.debug("{} Final URL after replacing path parameters -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lURL);
        return lURL;
    }

    private JSONObject httpExecuteService(HttpUriRequest request, int timeout) {

        LOG.info("{} httpExecuteService {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, httpDtls.getCallType());
        JSONObject lResponse = new JSONObject();
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()) {
            HttpResponse response = client.execute(request);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                LOG.error("{} No response entity found for the request", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                return lResponse;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity.getContent()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                lResponse = parseResponse(sb.toString());
            } catch (IOException ioEx) {
                LOG.error("{} IOException while reading the response entity: {}",
                        ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ioEx.getMessage());
            }
        } catch (SocketTimeoutException timeoutEx) {
            LOG.error("{} Request timed out after {} ms for URI: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, timeout,
                    request.getURI());
        } catch (ClientProtocolException protocolEx) {
            LOG.error("{} Protocol exception occurred while executing the request: {}",
                    ServerConstants.LOGGER_PREFIX_FRAMEWORKS, protocolEx.getMessage());
        } catch (IOException ioEx) {
            LOG.error("{} IOException while executing the HTTP request: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                    ioEx.getMessage());
        } catch (Exception ex) {
            LOG.error("{} Exception while executing CustomHttp :{} ", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
        }
        return lResponse;
    }

    private JSONObject parseResponse(String data) {

        JSONObject lResponse = new JSONObject();
        try {
            Object lResObj = data; // To avoid casting exception
            LOG.debug("lResObj is: " + data);
            if (lResObj instanceof JSONObject) {
                lResponse = new JSONObject(data);
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Final Response JSONObject :: "
                        + lResponse.toString());
            } else {
                lResponse = new JSONArray(data);
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Final Response JSONArray :: "
                        + lResponse.toString());
            }
        } catch (Exception ex) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Class -:" + ex.getClass());
            if (ex instanceof ClassCastException || ex instanceof JSONException) {
                try {
                    lResponse = new JSONObject(data);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Response put in JSONObject : " + lResponse);
                } catch (JSONException csex) {
                    LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
                            + "Exception while putting in JSONObject, since external response is not a JSONObject.");
                    LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Now Putting response in JSONArray");
                    try {
                        lResponse = new JSONArray(data);
                        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSONArray Casting -:" + lResponse);
                    } catch (JSONException jsonEx) {
                        LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
                                + "Exception while putting in JSONArray, since response is not an array.");
                        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
                                + "Now Putting response string from external service in JSONObject.");
                        lResponse = new JSONObject().put("response", data.trim());
                        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "After Putting in the response : "
                                + lResponse);
                    }
                }
            }
        }
        return lResponse;
    }
}
