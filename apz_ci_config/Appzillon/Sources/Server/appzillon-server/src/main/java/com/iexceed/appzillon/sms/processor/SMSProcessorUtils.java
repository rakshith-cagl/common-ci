package com.iexceed.appzillon.sms.processor;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Author Abhishek
 */
public class SMSProcessorUtils {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, SMSProcessorUtils.class.getName());

    private SMSProcessorUtils() {

    }

    /**
     * @param nodeList
     * @return
     */
    public static List<Tag> buildtagList(NodeList nodeList) {
        List<Tag> tags = new ArrayList<>();
        Tag tag;
        for (int i = 0; i < nodeList.getLength(); i++) {
            tag = new Tag();
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                NodeList cnodelist = eElement.getChildNodes();
                getTagByNodeName(cnodelist, tag, tags, i);
            }
        }

        return tags;
    }

    private static void getTagByNodeName(NodeList cnodelist, Tag tag, List<Tag> tags, int i) {
        for (int j = 0; j < cnodelist.getLength(); j++) {
            Node cNode = cnodelist.item(j);
            if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.NODE)) {
                tag.setNode(cNode.getTextContent());
            }
            if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.ELEMENT)) {
                tag.setElement(cNode.getTextContent());
            }
            if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.NAME_CAPS)) {
                tag.setName(cNode.getTextContent());
            }
            if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.FROM)) {
                tag.setFrom(cNode.getTextContent());
            }
            if (cNode.getNodeName().equalsIgnoreCase(ServerConstants.ELEMENTVALUE)) {
                tag.setElementvalue(cNode.getTextContent());
            }
        }
        tags.add(i, tag);
    }

    /**
     * @param previousJSON
     * @param tag
     * @param requestPayLad
     * @param pmobilenumber
     * @return
     */
    public static void buildRequestJson(JSONObject previousJSON, Tag tag, String[] requestPayLad, String pmobilenumber) {
        String[] larry = split(tag.getNode(), ".");

        JSONObject ljsonobject = previousJSON;
        JSONObject lchildobj = null;
        String value = "";
        String ltoken = tag.getElementvalue();
        if (tag.getFrom().equals(ServerConstants.APPZILLON)) {
            if (ltoken.startsWith("$DATE")) {
                value = getDate(ltoken);
            } else if (ltoken.equals("$MOBILENUMBER")) {
                value = pmobilenumber;
            }
        } else {
            value = ltoken;
            int lidx = value.indexOf("#");
            while (lidx >= 0) {
                String ltagnostr = value.substring(lidx + 1, lidx + 4);
                int ltagno = -1;
                try {
                    ltagno = Integer.parseInt(ltagnostr);
                } catch (Exception ex) {
                    ltagno = -1;
                }
                if (ltagno >= 0) {
                    String ltagval = requestPayLad[ltagno];
                    value = value.substring(0, lidx) + ltagval + value.substring(lidx + 4);
                }
                lidx = value.indexOf("#");
            }
        }


        getNodeObject(larry, previousJSON, ljsonobject, tag, value, lchildobj);
    }

    private static void getNodeObject(String[] larry, JSONObject previousJSON, JSONObject ljsonobject, Tag tag, String value, JSONObject lchildobj) {
        if (larry != null) {
            for (int i = 0; i < larry.length; i++) {
                if (i == 0) {
                    getJsonObjWhenInitValIsZero(larry, previousJSON, ljsonobject, tag, value);
                } else {
                    if (lchildobj == null) {
                        lchildobj = getJsonObjectWhenChileObjIsNull(larry, ljsonobject, tag, value, i);
                    } else {
                        lchildobj = getJsonObjectWhenChileObjIsNotNull(larry, tag, value, i, lchildobj);
                    }
                }
            }
        }
    }

    private static JSONObject getJsonObjectWhenChileObjIsNotNull(String[] larry, Tag tag, String value, int i, JSONObject lchildobj) {
        if (i == larry.length - 1) {

            lchildobj = createChild(
                    larry[i], (JSONObject)
                            lchildobj.get(larry[i - 1]), tag.getElement(),
                    value);

        } else {

            lchildobj = createChildWhenNull(
                    larry[i], (JSONObject)
                            lchildobj.get(larry[i - 1]), tag.getElement(), value);

        }
        return lchildobj;
    }

    private static JSONObject getJsonObjectWhenChileObjIsNull(String[] larry, JSONObject ljsonobject, Tag tag, String value, int i) {
        JSONObject lchildobj;
        if (i == larry.length - 1) {

            lchildobj = createChild(
                    larry[i], (JSONObject)
                            ljsonobject.get(larry[i - 1]), tag.getElement(),
                    value);

        } else {

            lchildobj = createChildWhenNull(
                    larry[i], (JSONObject)
                            ljsonobject.get(larry[i - 1]), tag.getElement(),
                    value);


        }
        return lchildobj;
    }

    private static void getJsonObjWhenInitValIsZero(String[] larry, JSONObject previousJSON, JSONObject ljsonobject, Tag tag, String value) {
        if (!previousJSON.toString().equals("{}")) {
            ljsonobject = previousJSON;
        }
        if (larry.length == 1) {

            ljsonobject.put(tag.getElement(), value);

        } else {
            ljsonobject.accumulate(larry[0], new JSONObject());
        }
    }

    /**
     * @param pNode
     * @param pjsonobj
     * @param element
     * @param value
     * @return
     */
    public static JSONObject createChildWhenNull(String pNode, JSONObject pjsonobj, String element, String value) {
        JSONObject lobj = new JSONObject();
        try {
            if (pjsonobj.has(pNode)) {
                JSONObject tempJSON = pjsonobj.getJSONObject(pNode);
                tempJSON.put(element, value);
                pjsonobj.put(pNode, tempJSON);
            } else {
                pjsonobj.accumulate(pNode, lobj);
            }

        } catch (JSONException jx) {
            LOG.debug("JSONException createChildWhenNull :: ", jx);
        }

        return pjsonobj;

    }

    /**
     * @param p
     * @param pjsonobj
     * @param element
     * @param value
     * @return
     */
    public static JSONObject createChild(String p, JSONObject pjsonobj, String element, String value) {
        JSONObject lobj = new JSONObject();
        try {
            if (pjsonobj.has(p)) {
                JSONObject tempJSON = pjsonobj.getJSONObject(p);
                tempJSON.put(element, value);
                pjsonobj.put(p, tempJSON);
            } else {
                pjsonobj.accumulate(p, lobj.put(element, value));
            }

        } catch (JSONException jx) {
            LOG.debug("JSONException :: ", jx);
        }

        return pjsonobj;

    }

    /**
     * @param pOriginalText
     * @param pSeparator
     * @return
     */
    public static String[] split(String pOriginalText, String pSeparator) {
        List<String> nodes = new ArrayList<>();
        String[] result = null;
        try {
            int index = pOriginalText.indexOf(pSeparator);
            while (index >= 0) {
                nodes.add(pOriginalText.substring(0, index));
                pOriginalText = pOriginalText.substring(index + pSeparator.length());
                index = pOriginalText.indexOf(pSeparator);
            }
            if (Utils.isNotNullOrEmpty(pOriginalText.trim())) {
                nodes.add(pOriginalText);
            }
            result = new String[nodes.size()];
            if (!nodes.isEmpty()) {
                for (int loop = 0; loop < nodes.size(); loop++) {
                    result[loop] = nodes.get(loop);
                }
            }
        } catch (Exception e) {
            LOG.error("Exception occurred during split: ", e);
        }
        return result;
    }

    /**
     * @param actualRequest
     * @return
     */
    public static String serviceTypeIdentifier(String actualRequest) {
        String serviceType = "";
        String[] splitResult = split(actualRequest, ServerConstants.SEPARATOR_SPACE);
        serviceType = splitResult != null ? splitResult[0] : serviceType;
        LOG.debug("After identifying service request, service type is -: {}", serviceType);

        return serviceType.toUpperCase();
    }

    public static String getDate(String token) {
        String date = token;
        try {
            int start = token.indexOf('(');
            int last = token.indexOf(')');
            if (start > -1 && last > -1) {
                String format = token.substring(start + 1, last - 1);
                SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
                date = dateFormatter.format(new Date());
            }
        } catch (Exception e) {
            LOG.error("In getDate exception is ", e.getMessage());
        }
        return date;
    }

}
