package com.iexceed.appzillon.sms.processor;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.XMLExternalEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
/*
 * Author Abhishek
 */

public class SMSResponseProcessor {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, SMSResponseProcessor.class.getName());
    SMSRequestProcessorImpl lsmsProcessor = null;

    public String getResponse(String lresponse, String lrequest) {
        String lservicetype = SMSProcessorUtils.serviceTypeIdentifier(lrequest);

        String result = "";
        DocumentBuilder dBuilder;
        try (InputStream isr = SMSResponseProcessor.class.getClassLoader().
                getResourceAsStream(Utils.getSmsServiceXmlFile(lservicetype))) {
            /*Veracode fix: Improper Restriction of XML External Entity Reference (CWE ID 611)*/
            dBuilder = XMLExternalEntity.getDocBuilder();
            Document doc = dBuilder.parse(isr);
            result = buildResponseString(doc, lresponse);
        } catch (Exception e1) {
            LOG.error("{} Exception: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e1);
        }

        return result;
    }

    public String buildResponseString(Document pdoc, String presponse) {
        StringBuilder result = new StringBuilder();
        JSONObject resheader = new JSONObject(presponse).getJSONObject(ServerConstants.MESSAGE_HEADER);
        JSONObject bodyjson = new JSONObject(presponse).getJSONObject(ServerConstants.MESSAGE_BODY);
        boolean hstatus = resheader.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS);
        LOG.debug("{} in buildResponseString response body {}", ServerConstants.LOGGER_PREFIX_RESTFULL, bodyjson.toString());
        String messsageSep = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.MESSAGE_SEPERATOR);
        if (hstatus) {
            presponse = bodyjson.toString();
        } else {
            return "Sorry Unable to Process your Request";
        }
        JSONObject jsres = new JSONObject(presponse);
        String lrequestlevel = getNodeName(pdoc);
        String[] lrequestlarr = lrequestlevel.split("\\.");
        String node = "";
        int count = 0;
        for (String resnode : lrequestlarr) {

            if (jsres.has(resnode) && count < lrequestlarr.length - 1) {
                jsres = (JSONObject) jsres.get(resnode);

            }
            node = resnode;
            count++;
        }
        if (jsres.get(node) instanceof JSONArray) {
            LOG.debug("{} in buildResponseString Response node is JSONArray", ServerConstants.LOGGER_PREFIX_RESTFULL);
            JSONArray jsarray = (JSONArray) jsres.get(node);
            for (int i = 0; i < jsarray.length(); i++) {
                JSONObject jobj = (JSONObject) jsarray.get(i);
                result.append(buildResponse(pdoc, jobj.toString())).append(messsageSep);
            }
            result.substring(0, result.length());
        } else if (jsres.get(node) instanceof JSONObject) {
            JSONObject jobj = (JSONObject) jsres.get(node);
            presponse = jobj.toString();
            LOG.debug("{} in buildResponseString Response node is JSONObject", ServerConstants.LOGGER_PREFIX_RESTFULL);
            result = new StringBuilder(buildResponse(pdoc, presponse));
        }

        return result.toString();
    }

    public String buildResponse(Document pdoc, String presponse) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "/INTERFACE/RESPONSE/TAG";
        NodeList nodeList;
        String name = null;

        JSONObject jsonres = null;
        HashMap<String, Tag> responsetagmap = new HashMap<>();
        LinkedList<String> taglist = new LinkedList<>();
        StringBuilder result = new StringBuilder();

        try {
            nodeList = (NodeList) xPath.compile(expression).evaluate(pdoc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Tag responseTag = new Tag();
                jsonres = new JSONObject(presponse);
                Node node = nodeList.item(i);
                NodeList cnodelist = node.getChildNodes();
                for (int x = 0; x < cnodelist.getLength(); x++) {
                    Node cnode = cnodelist.item(x);
                    if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.NAME_CAPS)) {
                        name = cnode.getTextContent();
                        responseTag.setName(name);
                        taglist.add(name);
                    }
                    getResponseTagData(cnode, responseTag, jsonres);
                }
                responsetagmap.put(name, responseTag);
            }
            LOG.debug("{} Response Tags Map : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, responsetagmap);
            result = getResult(taglist, responsetagmap, pdoc);
        } catch (XPathExpressionException e) {
            LOG.error("{} XPathExpressionException {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e);
        }
        return result.toString();
    }

    private StringBuilder getResult(LinkedList<String> taglist, HashMap<String, Tag> responsetagmap, Document pdoc) {
        StringBuilder result = new StringBuilder();
        for (String tagname : taglist) {
            Tag ltag = responsetagmap.get(tagname);
            String lcondition = ltag.getCondition();
            String lconditiontype = ltag.getConditionType();
            String lconditionvalue = ltag.getConditionValue();
            LOG.debug("{} tagname : {}; lcondition: {}; lconditiontype: {}; lconditionvalue: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, tagname, lcondition, lconditiontype, lconditionvalue);

            if (isAppendRequired(lcondition, ltag, lconditionvalue, lconditiontype, responsetagmap)) {
                result.append(appendResult(ltag, pdoc));
            }
            LOG.debug("{} buildResponseTag: {}{}", ServerConstants.LOGGER_PREFIX_RESTFULL, tagname, result);
        }
        return result;
    }

    private boolean isAppendRequired(String lcondition, Tag ltag, String lconditionvalue, String lconditiontype, HashMap<String, Tag> responsetagmap) {
        boolean append = false;
        if (lcondition != null && !lcondition.equals("")) {
            if (lconditiontype.equals(ServerConstants.EQ)) {
                if (responsetagmap.get(lcondition).getElementvalue().equals(lconditionvalue)
                        && ServerConstants.YES.equalsIgnoreCase(ltag.getAppender())) {
                    append = true;
                }
            } else if (lconditiontype.equals(ServerConstants.NEQ)
                    && !responsetagmap.get(lcondition).getElementvalue().equals(lconditionvalue)
                    && ServerConstants.YES.equalsIgnoreCase(ltag.getAppender())) {
                append = true;
            }
        } else {
            if (ServerConstants.YES.equalsIgnoreCase(ltag.getAppender())) {
                append = true;
            }
        }
        return append;
    }

    private void getResponseTagData(Node cnode, Tag responseTag, JSONObject jsonres) {

        responseTagDataWhenNodeNameIsNode(cnode, responseTag);
        responseTagDataWhenNodeNameIsElement(cnode, responseTag, jsonres);

        if (cnode.getNodeType() == Node.ELEMENT_NODE
                && cnode.getNodeName().equals(ServerConstants.CONDITIONTAG)
                && !cnode.getTextContent().equals("")) {
            responseTag.setCondition(cnode.getTextContent());
        }
        if (cnode.getNodeType() == Node.ELEMENT_NODE
                && cnode.getNodeName().equals(ServerConstants.CONDITIONTYPE)
                && !cnode.getTextContent().equals("")) {
            responseTag.setConditionType(cnode.getTextContent());
        }
        if (cnode.getNodeType() == Node.ELEMENT_NODE
                && cnode.getNodeName().equals(ServerConstants.CONDITIONVALUE)
                && !cnode.getTextContent().equals("")) {
            responseTag.setConditionValue(cnode.getTextContent());
        }
        if (cnode.getNodeType() == Node.ELEMENT_NODE
                && cnode.getNodeName().equals(ServerConstants.MESSAGES)
                && !cnode.getTextContent().equals("")) {
            responseTag.setMessageMap(getMessageMap(cnode));
        }
        if (cnode.getNodeType() == Node.ELEMENT_NODE
                && cnode.getNodeName().equals(ServerConstants.APPEND)
                && !cnode.getTextContent().equals("")) {
            responseTag.setAppender(cnode.getTextContent());
        }
    }

    private void responseTagDataWhenNodeNameIsNode(Node cnode, Tag responseTag) {
        if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.NODE)) {
            String lrequestlevel = cnode.getTextContent();
            responseTag.setNode(lrequestlevel);
        }
    }

    private void responseTagDataWhenNodeNameIsElement(Node cnode, Tag responseTag, JSONObject jsonres) {
        String element;
        if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.ELEMENT)
                && !cnode.getTextContent().equals("")) {
            element = cnode.getTextContent();
            if (jsonres.has(element)) {
                responseTag.setElementvalue("" + jsonres.get(element));
                responseTag.setElement(element);
            }
        }
    }

    public Map<String, String> getMessageMap(Node pnode) {
        HashMap<String, String> messageMap = new HashMap<>();
        NodeList nodeList = pnode.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            getMessageMapByNodeTypeAndName(node, messageMap);
        }
        LOG.debug("{} getMessageMap Message Map is {}", ServerConstants.LOGGER_PREFIX_RESTFULL, messageMap);
        return messageMap;
    }

    private void getMessageMapByNodeTypeAndName(Node node, HashMap<String, String> messageMap) {
        String lang = "";
        String desc = "";
        if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(ServerConstants.MESSAGENODE)) {
            Element eElement = (Element) node;
            NodeList cnodelist = eElement.getChildNodes();
            for (int j = 0; j < cnodelist.getLength(); j++) {
                Node cNode = cnodelist.item(j);
                if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.LANGUAGE)) {
                    lang = cNode.getTextContent();
                }
                if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.DESCRIPTIONNODE)) {
                    desc = cNode.getTextContent();
                }
            }
            if (!lang.equals("") && !desc.equals("")) {
                messageMap.put(lang, desc);
            }
        }
    }

    public String appendResult(Tag ptag, Document pdoc) {
        String result = "";
        String ldefaultlang = this.getLsmsProcessor().getDefaultLang();
        if (ptag.getMessageMap().get(ldefaultlang) != null && !ptag.getMessageMap().get(ldefaultlang).equals("")) {
            result += ptag.getMessageMap().get(ldefaultlang);
        }
        if (ptag.getAppender().equalsIgnoreCase(ServerConstants.YES)
                && ptag.getElement() != null && !ptag.getElement().equals("")) {
            if (!ptag.getMessageMap().get(ldefaultlang).contains("$")) {
                if (!getTranslatedValue(ptag, pdoc).equals("")) {
                    result += getTranslatedValue(ptag, pdoc);
                } else {
                    result += ptag.getElementvalue();
                }

            } else {
                result = result.replace("$", getTranslatedValue(ptag, pdoc));
            }
        }

        return result;
    }

    public String getTranslatedValue(Tag ptag, Document pdoc) {
        String result = "";
        NodeList nodeList;
        String ldefaultlang = this.getLsmsProcessor().getDefaultLang();
        XPath xPath = XPathFactory.newInstance().newXPath();
        String xpathexpr = "INTERFACE/TRANSLATIONS/TRANSLATION";
        String lkey = "";
        String ldesc = "";
        boolean code = false;
        boolean lnode = false;
        boolean lelement = false;
        LOG.debug("{} getTranslatedValue : xpathexpr = {}", ServerConstants.LOGGER_PREFIX_RESTFULL, xpathexpr);
        try {
            nodeList = (NodeList) xPath.compile(xpathexpr).evaluate(pdoc, XPathConstants.NODESET);
            LOG.debug("{} getTranslatedValue : nodeList length : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, nodeList.getLength());
            if (nodeList.getLength() == 0) {
                return ptag.getElementvalue();
            }
            for (int x = 0; x < nodeList.getLength(); x++) {
                Node node = nodeList.item(x);
                NodeList cnodelist = node.getChildNodes();
                for (int i = 0; i < cnodelist.getLength(); i++) {
                    Node cnode = cnodelist.item(i);
                    lnode = isNode(cnode, ptag);
                    lelement = isNodeNameElement(cnode, ptag);

                    if (cnode.getNodeType() == Node.ELEMENT_NODE
                            && cnode.getNodeName().equals(ServerConstants.LANGUAGE)) {
                        Element eElement = (Element) cnode;
                        NodeList langlist = eElement.getChildNodes();
                        for (int j = 0; j < langlist.getLength(); j++) {
                            Node lang = langlist.item(j);
                            code = isNodeNameCode(lang, ldefaultlang);

                            Map<String, String> keyAndDesc = getKeyAndDesc(lang, ptag);
                            lkey = keyAndDesc.get("lkey");
                            ldesc = keyAndDesc.get("ldesc");
                        }
                    }
                }
            }

            result = (!lkey.equals("") && lnode && lelement && code) ? ldesc : ptag.getElementvalue();

        } catch (XPathExpressionException e) {
            result = ptag.getElementvalue();
        }

        LOG.debug("{} getTranslatedValue : result = {}", ServerConstants.LOGGER_PREFIX_RESTFULL, result);
        return result;
    }

    private Map<String, String> getKeyAndDesc(Node lang, Tag ptag) {
        Map<String, String> map = new HashMap<>();
        String lkey = "";
        if (lang.getNodeType() == Node.ELEMENT_NODE
                && lang.getNodeName().equals(ServerConstants.VALUES)) {

            Element eElement = (Element) lang;
            NodeList values = eElement.getChildNodes();
            for (int z = 0; z < values.getLength(); z++) {
                Node value = values.item(z);
                if (value.getNodeType() == Node.ELEMENT_NODE && value.getNodeName().equals(ServerConstants.VALUE)) {
                    NodeList gcnodelist = value.getChildNodes();
                    for (int y = 0; y < gcnodelist.getLength(); y++) {
                        Node gcnode = gcnodelist.item(y);
                        lkey = getlKey(gcnode);

                        map.put("lkey", lkey);
                        map.put("ldesc", getlDesc(gcnode, lkey, ptag));
                    }
                }
            }
        }
        return map;
    }

    private String getlDesc(Node gcnode, String lkey, Tag ptag) {
        if (gcnode.getNodeType() == Node.ELEMENT_NODE
                && gcnode.getNodeName().equals(ServerConstants.DESCRIPTIONNODE)
                && lkey.equals(ptag.getElementvalue())
                && !gcnode.getTextContent().equals("")) {
            return gcnode.getTextContent();
        }
        return "";
    }

    private String getlKey(Node gcnode) {
        if (gcnode.getNodeType() == Node.ELEMENT_NODE && gcnode.getNodeName().equals(ServerConstants.KEY)) {
            return gcnode.getTextContent();
        }
        return "";
    }

    private boolean isNodeNameCode(Node lang, String ldefaultlang) {
        return lang.getNodeType() == Node.ELEMENT_NODE
                && lang.getNodeName().equals(ServerConstants.CODE)
                && lang.getTextContent().equals(ldefaultlang);
    }

    private boolean isNodeNameElement(Node cnode, Tag ptag) {
        return cnode.getNodeType() == Node.ELEMENT_NODE
                && cnode.getNodeName().equals(ServerConstants.ELEMENT)
                && ptag.getElement().equals(cnode.getTextContent());
    }

    private boolean isNode(Node cnode, Tag ptag) {
        return cnode.getNodeType() == Node.ELEMENT_NODE
                && cnode.getNodeName().equals(ServerConstants.NODE)
                && ptag.getNode().equals(cnode.getTextContent());
    }

    public String getNodeName(Document pdoc) {
        String result = "";
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "/INTERFACE/RESPONSE/TAG";
        NodeList nodeList;
        String name = null;
        LinkedList<String> taglist = new LinkedList<>();
        try {
            nodeList = (NodeList) xPath.compile(expression).evaluate(pdoc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Tag responseTag = new Tag();
                Node node = nodeList.item(i);
                NodeList cnodelist = node.getChildNodes();
                for (int x = 0; x < cnodelist.getLength(); x++) {
                    Node cnode = cnodelist.item(x);
                    if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.NAME_CAPS)) {
                        name = cnode.getTextContent();
                        responseTag.setName(name);
                        taglist.add(name);
                    }
                    if (cnode.getNodeType() == Node.ELEMENT_NODE && cnode.getNodeName().equals(ServerConstants.NODE)) {
                        return cnode.getTextContent();
                    }
                }
            }
        } catch (XPathExpressionException e) {
            LOG.error("{} XPathExpressionException {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e);
        }
        return result;
    }

    public SMSRequestProcessorImpl getLsmsProcessor() {
        return lsmsProcessor;
    }

    public void setLsmsProcessor(SMSRequestProcessorImpl lsmsProcessor) {
        this.lsmsProcessor = lsmsProcessor;
    }

}
