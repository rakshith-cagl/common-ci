package com.iexceed.appzillon.services;

import com.iexceed.appzillon.dao.SOAPDetails;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
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
import org.apache.camel.component.spring.ws.SpringWebserviceConstants;
import org.apache.camel.spring.SpringCamelContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static com.iexceed.appzillon.utils.Constants.ENV;
import static com.iexceed.appzillon.utils.Constants.SYS;
import static javax.xml.transform.TransformerFactory.newInstance;

public class SOAPService implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    SOAPService.class.getName());
    protected SOAPDetails soapDtls = null;
    String tempHeader = "";
    Object reqPayLoad;
    private String xmlnsColon = "xmlns:";

    @Override
    public Object callService(com.iexceed.appzillon.message.Message pMessage,
                              Object pRequestPayLoad, SpringCamelContext pContext) {
        String output = "";
        LOG.debug("{} After getting the context {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pContext);
        getSOAPDetails(pMessage, pContext);
        // get soapXmlHeader from payload and remove soapXmlheader from payload
        LOG.debug("{} Soap Details : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, soapDtls.toString());
        String soapHeaderXmlNode = soapDtls.getHeaderXmlNode();

        pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad, soapDtls.getAutoGenElementMap(), soapDtls.getTranslationElementMap());
        pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad + ""));
        LOG.debug("{} After Appending Request Json With MaskedId : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        LOG.debug("{} Soap Header Xml Node: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, soapHeaderXmlNode);
        if (Utils.isNotNullOrEmpty(soapHeaderXmlNode)) {//soapHeaderXmlNode.length() > 0
            prepareHeaderFromNodeStructure(soapHeaderXmlNode, pRequestPayLoad);
            pRequestPayLoad = reqPayLoad;
        }

        final String payload = (String) buildRequest(pMessage,
                pRequestPayLoad.toString(), pContext);
        LOG.debug("{} target name space {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, soapDtls.getTargetNamespace());
        LOG.debug("{} Input to callExternalService --- serviceName: {} requestPayload : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getHeader().getInterfaceId(), payload);
        ProducerTemplate lProducerTemplate = ExternalServicesRouter
                .createProducerTemplate();
        LOG.debug("{} got the producer {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lProducerTemplate);
        //this method is previously commented
        applySSl(pMessage);
        String endpoint = createEndpointURIbyParam(pMessage, pContext);
        final Map<String, Object> headermap = soapDtls.getHeaderAttributesMap();
        final String customHeader = tempHeader;
        LOG.debug("{} Endpoint URL after appending options {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpoint);
        Utils.setExtTime(pMessage, "S");
        Exchange exchangeRequest = lProducerTemplate.request(endpoint,
                new Processor() {
                    public void process(Exchange exchange) throws Exception {

                        exchange.getIn().setBody(payload);
                        if (Utils.isNotNullOrEmpty(customHeader)) {
                            exchange.getIn()
                                    .setHeader(
                                            SpringWebserviceConstants.SPRING_WS_SOAP_HEADER,
                                            customHeader);
                        }
                        if (headermap != null && headermap.size() > 0) {
                            exchange.getIn().setHeaders(headermap);
                        }
                    }
                });
        Utils.setExtTime(pMessage, "E");
        if (exchangeRequest.getException() == null) {
            Message outMessage = exchangeRequest.getOut();
            Integer statusHeader = (Integer) outMessage
                    .getHeader(Exchange.HTTP_RESPONSE_CODE);
            LOG.debug("{} HTTP Response code {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, statusHeader);
            if (exchangeRequest.getException() != null) {
                LOG.error("{} The exception in exchange {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exchangeRequest.getException());
            }

            try {
                output = outMessage.getMandatoryBody(String.class);
            } catch (InvalidPayloadException e1) {
                LOG.error("{} callExternalService - Exchange - exception: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exchangeRequest.getException());
                ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, e1.getMessage(), payload);
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                        .getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
                exsrvcallexp.setMessage(exchangeRequest.getException()
                        .getMessage());
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            }

            LOG.info("{} response content received {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, output);
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.SUCCESS, output, payload);

            output = (String) processResponse(pMessage, output, pContext);

            return new JSONObject(output);
        } else {
            LOG.error("{} Exception from external system hence will be throwing an exception....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            LOG.error("{} callExternalService - Exchange-exception : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                    exchangeRequest.getException());
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                    .getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp
                    .setMessage(exsrvcallexp
                            .getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
            exsrvcallexp.setPriority("1");
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, exsrvcallexp.getMessage(), payload);
            throw exsrvcallexp;
        }

    }

    @Override
    public Object buildRequest(com.iexceed.appzillon.message.Message pMessage,
                               Object pRequestPayLoad, SpringCamelContext pContext) {
        String lXML = null;
        LOG.debug("Inside SuperClass buildRequest with payload {}", pRequestPayLoad);
        lXML = ExternalServicesRouter.getJSONtoXML((String) pRequestPayLoad);
        lXML = addNamespace(soapDtls, lXML);
        LOG.info("final Request Build in SuperClass -: {}", lXML);
        return lXML;
    }

    @Override
    public Object processResponse(
            com.iexceed.appzillon.message.Message pMessage, Object pResponse,
            SpringCamelContext pContext) {
        LOG.debug("{} Processing response in superclass with payload {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pResponse);
        return ExternalServicesRouter.getXMLToJSON((String) pResponse);
    }

    public void getSOAPDetails(com.iexceed.appzillon.message.Message pMessage,
                               SpringCamelContext pContext) {
        soapDtls = (SOAPDetails) ExternalServicesRouter
                .injectBeanFromSpringContext(pMessage.getHeader().getAppId()
                        + "_" + pMessage.getHeader().getInterfaceId(), pContext);
        int timeOut = soapDtls.getTimeOut();
        LOG.debug("{} getSOAPDetails - timeOut -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, timeOut);
        /**
         * Below changes are made by Vinod as part of At app level, service time
         * out should be configurable. Appzillon 3.1 - 63 -- Start
         */
        if (timeOut == 0) {
            LOG.warn("{} Timeout value not configured will use default timeOut", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            timeOut = Integer.parseInt(PropertyUtils.getPropValue(
                    pMessage.getHeader().getAppId(),
                    ServerConstants.DEFAULT_TIMEOUT).trim());
            LOG.debug("{} After setting default timeout value -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, timeOut);
        }
        /** Appzillon 3.1 - 63 -- END */
        soapDtls.setTimeOut(timeOut * 1000);
    }

    public void applySSl(
            com.iexceed.appzillon.message.Message pMessage) {
        if (ServerConstants.YES.equalsIgnoreCase(soapDtls.getSSLRequired())) {
            Object keyStorePath = null;
            Object keyStoreCred = null;
            Object trustStorePath = null;
            Object trustStoreCred = null;

            String trustStrPath = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.TRUST_STORE_PATH);
            String trustStrPwd = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.TRUST_STORE_PIN);
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

                trustStorePath = getValue(trustStrPath, "Reading truststore path from env variable : {}", "Reading truststore path from system property : {}");
                trustStoreCred = getValue(trustStrPwd, "Reading truststore password from env variable : {}", "Reading truststore password from system property : {}");

            }

            String keyStrPath = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.KEY_STORE_PATH);
            String keyStrPwd = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.KEY_STORE_PIN);
            LOG.debug("Reading keyStrPath : {}", keyStrPath);
            LOG.debug("Reading keyStrPwd : {}", keyStrPwd);

            if (Utils.isNotNullOrEmpty(cloudName) && cloudName.equalsIgnoreCase(ServerConstants.CLOUD_AZURE)) {
                LOG.debug("Reading keystorePath and password from vault ");
                String authToken = AuthTokenUtil.getAuthTokenKeyVault(ServerConstants.SERVER_PROP_FILE_CONSTANT);
                keyStoreCred = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(keyStrPwd), authToken);
                keyStorePath = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(keyStrPath), authToken);

            } else {
                keyStorePath = getValue(keyStrPath, "Reading keystore path from env variable : {}", "Reading keystore path from system property : {}");
                keyStoreCred = getValue(keyStrPwd, "Reading keystore password from env variable : {}", "Reading keystore password from system property : {}");
            }
            LOG.debug("Actual Keystore Path : {}", keyStorePath);
            setStoreProperty(keyStorePath, ServerConstants.SYSTEM_PROPERTY_SSL_KEY_STORE, ServerConstants.SYSTEM_PROPERTY_SSL_KEY_STORE_PIN, keyStoreCred);
            LOG.debug("Actual truststore Path : {}", trustStorePath);
            setStoreProperty(trustStorePath, ServerConstants.SYSTEM_PROPERTY_SSL_TRUST_STORE, ServerConstants.SYSTEM_PROPERTY_SSL_TRUST_STORE_PIN, trustStoreCred);
        }
    }

    private void setStoreProperty(Object storePath, String systemPropertySslKeyStore, String systemPropertySslKeyStorePin, Object storeCred) {
        if (storePath != null) {
            System.setProperty(
                    systemPropertySslKeyStore,
                    storePath.toString().trim());
            System.setProperty(
                    systemPropertySslKeyStorePin, storeCred != null ?
                            storeCred.toString().trim() : "");

        }
    }

    private Object getValue(String inputString, String message, String message1) {
        Object outputObj;
        if (inputString != null && inputString.startsWith(ENV)) {
            // reading from environment variable
            LOG.debug(message, inputString);
            outputObj = System.getenv(inputString.replace(ENV, "").replace("}", ""));
        } else if (inputString != null && inputString.startsWith(SYS)) {
            // reading from system property
            LOG.debug(message1, inputString);
            outputObj = System.getProperty(inputString.replace(SYS, "").replace("}", ""));
        } else {
            outputObj = inputString;
        }
        return outputObj;
    }

    public String createEndpointURIbyParam(
            com.iexceed.appzillon.message.Message pMessage,
            SpringCamelContext pContext) {
        String camelID = (pMessage.getHeader().getAppId() + "__" + pMessage
                .getHeader().getInterfaceId()).replaceAll("[\\.]", "__");
        LOG.info("{} Getting endpoint with id {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, camelID);
        String endpointURI = pContext.getEndpoint(camelID).getEndpointUri();
        LOG.debug("{} URI endpoint after replacing properties : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpointURI);
        StringBuilder appendOption = new StringBuilder("?");
        if (ServerConstants.YES.equalsIgnoreCase(soapDtls.getSSLRequired())) {
            appendOption.append(ServerConstants.SSL_CONTEXT_PARAMETERS);
            LOG.debug("{} SSL Required and setting SSLContextParameters -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, appendOption);
        }

        String soapAction = soapDtls.getAction();

        if (Utils.isNotNullOrEmpty(soapAction)) {
            if ("?".equals(appendOption.toString())) {
                appendOption.append(ServerConstants.SOAP_URL_SOAP_ACTION + soapAction);
            } else {
                appendOption.append(ServerConstants.AMD + ServerConstants.SOAP_URL_SOAP_ACTION + soapAction);
            }

        }

        if (ServerConstants.SOAP_VERSION_SOAP12.equalsIgnoreCase(soapDtls
                .getVersion())) {
            LOG.debug("{} SOAP Version is SOAP1.2 and appending timeout parameters. SOAP1.2 timeout -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, soapDtls.getTimeOut());

            if ("?".equals(appendOption.toString())) {
                appendOption
                        .append(ServerConstants.SOAP_URL_MSG_FACTORY2_TIMEOUT
                                + soapDtls.getTimeOut());
            } else {
                appendOption.append(ServerConstants.AMD
                        + ServerConstants.SOAP_URL_MSG_FACTORY2_TIMEOUT
                        + soapDtls.getTimeOut());

            }

        } else {
            LOG.debug("{} SOAP Version is SOAP1.1 and appending timeout parameters. SOAP1.1 timeout -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, soapDtls.getTimeOut());

            if ("?".equals(appendOption.toString())) {
                appendOption
                        .append(ServerConstants.SOAP_URL_MSG_FACTORY1_TIMEOUT
                                + soapDtls.getTimeOut());
            } else {
                appendOption.append(ServerConstants.AMD
                        + ServerConstants.SOAP_URL_MSG_FACTORY1_TIMEOUT
                        + soapDtls.getTimeOut());
            }
        }
        LOG.debug("Final URI formed {}{}", endpointURI, appendOption);
        return endpointURI + appendOption;
    }

    public String addNamespace(SOAPDetails soapDtls, String xmlData) {
        LOG.debug("{} XmlData:: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, xmlData);
        String output = null;
        try {
            /*Veracode fix: Improper Restriction of XML External Entity Reference (CWE ID 611)*/
            DocumentBuilder builder = XMLExternalEntity.getDocBuilder();
            Document document = builder.parse(new InputSource(new StringReader(
                    xmlData)));
            Element originalDocumentElement = document.getDocumentElement();
            LOG.debug("{} Root Node:: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, originalDocumentElement.getNodeName());

            // uncommented below line to get child node
            Node nextNode = originalDocumentElement.getChildNodes().item(0);
            if (nextNode != null && Utils.isNotNullOrEmpty(nextNode.getNodeName())) {
                LOG.debug("{} Next Node:: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, nextNode.getNodeName());
            }
            /**
             * Creating Element with Namespace is commented as the namespace
             * will be added as attributes Changes made by Samy on 04/06/2015
             */
            Element newDocumentElement = document
                    .createElement(originalDocumentElement.getNodeName());
            LOG.debug("{} New Node:: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, newDocumentElement.getNodeName());
            LOG.debug("{} getIgnoreNamespaces-: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, soapDtls.getIgnoreNamespaces());
            if (ServerConstants.NO.equalsIgnoreCase(soapDtls
                    .getIgnoreNamespaces())) {
                LOG.debug("{} getNameSpaces-: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, soapDtls.getNameSpaces());
                Map<String, String> lNameSpacesUriAttributesMap = getNameSpaceUriMap(soapDtls
                        .getNameSpaces());
                for (Map.Entry<String, String> entry : lNameSpacesUriAttributesMap
                        .entrySet()) {
                    newDocumentElement.setAttribute(entry.getKey(),
                            entry.getValue());
                }
            }

            NodeList list = originalDocumentElement.getChildNodes();
            while (list.getLength() != 0) {
                LOG.debug("{} Element at Zeroth Position:: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, list.item(0).getNodeName());
                newDocumentElement.appendChild(list.item(0));
                LOG.debug("{} After Adding Child:: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, newDocumentElement.getNodeName());
            }

            document.removeChild(originalDocumentElement);
            document.appendChild(newDocumentElement);

            Source src = new DOMSource(document);
            TransformerFactory tranFactory = newInstance();
            setAttribute(tranFactory);
            Transformer aTransformer = tranFactory.newTransformer();
            StringWriter writer = new StringWriter();
            Result dest = new StreamResult(writer);
            aTransformer.transform(src, dest);
            writer.flush();
            output = writer.toString();
            LOG.debug("{} output:: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, output);
        } catch (TransformerException ex) {
            LOG.error("TransformerException", ex);
        } catch (SAXException ex) {
            LOG.error("SAXException", ex);
        } catch (IOException ex) {
            LOG.error("IOException", ex);
        } catch (ParserConfigurationException ex) {
            LOG.error("ParserConfigurationException", ex);
        }
        return output;
    }

    private void setAttribute(TransformerFactory tranFactory) {
        try {
            tranFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tranFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (IllegalArgumentException e) {
            LOG.error("jaxp 1.5 feature not supported: ", e.getMessage());
        }
    }

    private Map<String, String> getNameSpaceUriMap(String nameSpaces) {
        String[] nameSpacesSplit = Utils.split(nameSpaces, xmlnsColon);
        Map<String, String> attributes = new HashMap<>();
        for (int i = 1; i < nameSpacesSplit.length; i++) {
            String[] nameSpace = Utils.split(nameSpacesSplit[i], "=");
            String nameSpaceUri = nameSpace[1].substring(1,
                    nameSpace[1].lastIndexOf("\"")).trim();
            LOG.debug("{} Prefix -: {}, TargetURL -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, nameSpace[0], nameSpaceUri);
            if (nameSpaceUri.endsWith("\"")) {
                if (nameSpaceUri.startsWith("\"")) {
                    nameSpaceUri = nameSpaceUri.substring(1,
                            nameSpaceUri.length()).trim();
                }
                nameSpaceUri = nameSpaceUri.substring(0,
                        nameSpaceUri.lastIndexOf("\"")).trim();
            } else {
                if (nameSpaceUri.startsWith("\"")) {
                    nameSpaceUri = nameSpaceUri.substring(1,
                            nameSpaceUri.length()).trim();
                }
                nameSpaceUri = nameSpaceUri.trim();
            }

            attributes.put(xmlnsColon + nameSpace[0], nameSpaceUri);
        }
        LOG.debug("{} TargetNameSpacesURI Map -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, attributes);
        return attributes;
    }

    private String getValueFromNodeStruc(String pNodeStruc,
                                         JSONObject prequestPayLoad) {
        LOG.debug("{} getValueFromNodeStruc, Building query Parameters, from request -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, prequestPayLoad);
        String lvalue = "";
        try {
            JSONObject lTempJSON = prequestPayLoad;
            LOG.debug("{} pNodeStruc: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pNodeStruc);
            String[] nodestructure = Utils.split(pNodeStruc, ".");
            LOG.debug("{} node structure.length: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, nodestructure.length);
            int j = 0;
            for (; j < nodestructure.length - 1; j++) {
                LOG.debug("{} node structure[j]: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, nodestructure[j]);
                lTempJSON = lTempJSON.getJSONObject(nodestructure[j]);
                LOG.debug("{} lTempJSON: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lTempJSON);
            }


            lvalue = new JSONObject().put(nodestructure[j], lTempJSON.getJSONObject(nodestructure[j])).toString();
            LOG.debug("lvalue : {}", lvalue);

            LOG.debug("{} lTempJSON: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lTempJSON.toString());
        } catch (JSONException jsonex) {
            LOG.error("{} JSONException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, jsonex);
        }
        return lvalue;

    }

    private void prepareHeaderFromNodeStructure(String headerNodeStructure, Object payLoadReq) {
        JSONObject jsonObject = new JSONObject(payLoadReq.toString());
        String customHeaderJson = getValueFromNodeStruc(headerNodeStructure,
                jsonObject);
        LOG.debug("{} Soap Header Xml value in JSON {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, customHeaderJson);
        // convert customHeader JSON to XML
        tempHeader = ExternalServicesRouter
                .getJSONtoXML((String) customHeaderJson);
        tempHeader = addNamespace(soapDtls, tempHeader);
        LOG.debug("{} After converting Soap Header Xml JSON to XML {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, tempHeader);

        // remove soapXmlHeader from payload
        jsonObject.remove(Utils.split(headerNodeStructure, ".")[0]);
        reqPayLoad = jsonObject;
        LOG.debug("{} After Removing Soap Header Xml from Payload {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, reqPayLoad);
    }
}
