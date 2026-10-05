package com.iexceed.appzillon.sms.processor;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.MessageFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.rest.AppzillonRestWS;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.services.SendSMSService;
import com.iexceed.appzillon.sms.utils.HashXor;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.XMLExternalEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.iexceed.appzillon.utils.ServerConstants.INTERFACE_ID_SMS_USER;
import static com.iexceed.utils.Constants.RESPONSE;


public class SMSRequestProcessorImpl implements ISMSProcessor {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, SMSRequestProcessorImpl.class.getName());
    private String defaultLang;

    public String getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    /**
     * @param checkresponse
     * @return
     */
    public String getErrorMessage(String checkresponse) {
        String lerrormsg = "";
        JSONArray ljsonarray = (JSONArray) new JSONObject(checkresponse).get(ServerConstants.MESSAGE_ERROR);
        for (int i = 0; i < ljsonarray.length(); i++) {
            JSONObject lobj = (JSONObject) ljsonarray.get(i);
            lerrormsg = lobj.getString(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE);
        }
        return lerrormsg;
    }

    /**
     * @param requestPayload
     * @param pmobilenumber
     * @return
     */
    public String processRequest(String requestPayload, String pmobilenumber, HttpServletRequest request) {

        JSONObject lfinalreq = new JSONObject();

        DocumentBuilder dBuilder;
        String luserId = "";
        try {
            String lservicetype = SMSProcessorUtils.serviceTypeIdentifier(requestPayload);
            InputStream isr = SMSRequestProcessorImpl.class.getClassLoader().getResourceAsStream(Utils.getSmsServiceXmlFile(lservicetype));
            if (isr != null) {
                dBuilder = XMLExternalEntity.getDocBuilder();
                Document doc = dBuilder.parse(isr);
                doc.getDocumentElement().normalize();

                String appId = getIdByType(doc, ServerConstants.MESSAGE_HEADER_APP_ID);

                String lresponse = validateMobileNumber(pmobilenumber, appId, request);
                JSONObject resjson = new JSONObject(lresponse);
                if (!resjson.has(ServerConstants.MESSAGE_ERROR)) {
                    luserId = resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject(INTERFACE_ID_SMS_USER + RESPONSE).getString(ServerConstants.MESSAGE_HEADER_USER_ID);
                    this.setDefaultLang(resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject(INTERFACE_ID_SMS_USER + RESPONSE).getString(ServerConstants.DEFAULTLANGUAGE));
                } else {
                    return lresponse;
                }

                String ljsonbody = getFinalResponse(doc, pmobilenumber, appId, luserId, request, requestPayload);

                JSONObject resjsonbody = new JSONObject(ljsonbody);
                if (resjsonbody.has(ServerConstants.MESSAGE_ERROR)) {
                    return ljsonbody;
                }
                lfinalreq.put(ServerConstants.MESSAGE_HEADER, getJsonReqHeadObj(doc, appId, luserId));
                lfinalreq.put(ServerConstants.MESSAGE_BODY, resjsonbody);
            } else {
                LOG.error("Error while processing sms service xml file...");
                return getErrorResponse("Unable to Process request");
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            LOG.error("ArrayIndexOutOfBoundsException: ", e);
            return getErrorResponse("Bad Request. Please check format");
        } catch (Exception e1) {
            LOG.error("Exception: ", e1);
            return getErrorResponse("Sorry , Unable to Process request");

        }

        LOG.debug("Final request payload {}", lfinalreq.toString());
        return lfinalreq.toString();
    }

    private String getFinalResponse(Document doc, String pmobilenumber, String appId, String luserId, HttpServletRequest request, String requestPayload) {
        String[] reqarray = SMSProcessorUtils.split(requestPayload, ServerConstants.SEPARATOR_SPACE);
        return getJSONbody(doc, reqarray, pmobilenumber, appId, luserId, request);
    }

    public String getIdByType(Document pdoc, String pnode) {
        NodeList nodeList = pdoc.getDocumentElement().getChildNodes();
        String result = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeName().equals(pnode) && node.getNodeType() == Node.ELEMENT_NODE) {
                result = node.getTextContent();
                break;
            }
        }

        return result;
    }

    public String getJSONbody(Document pdoc, String[] reqarray, String pmobilenumber, String pappId, String puserId, HttpServletRequest request) {

        pdoc.getDocumentElement().normalize();

        XPath xPath = XPathFactory.newInstance().newXPath();
        JSONObject jsonnodes = new JSONObject();
        String expression = "/INTERFACE/REQUEST/TAG";
        NodeList nodeList;
        String result = null;

        try {
            nodeList = (NodeList) xPath.compile(expression).evaluate(pdoc, XPathConstants.NODESET);
            List<Tag> taglist = SMSProcessorUtils.buildtagList(nodeList);
            for (Tag ltag : taglist) {
                if (ltag.getElementvalue().startsWith("$") && ltag.getElementvalue().contains("PIN") && ltag.getFrom().equals(ServerConstants.SMS)) {
                    String ltoken = ltag.getElementvalue();
                    int lidx = ltoken.indexOf("#");
                    String ltagnostr = ltoken.substring(lidx + 1, lidx + 4);
                    int ltagno = -1;
                    ltagno = Integer.parseInt(ltagnostr);
                    String ltagval = reqarray[ltagno];
                    if (!authenticatePin(ltagval, puserId, pappId, pmobilenumber, request)) {
                        return getErrorResponse("Pin entered is Incorrect");
                    }
                } else {
                    SMSProcessorUtils.buildRequestJson(jsonnodes, ltag, reqarray, pmobilenumber);
                    if (ltag.getFrom().equals(ServerConstants.SMS)) {
                        LOG.debug("ltag.getFrom is SMS");
                    }
                }
            }
            Tag ltag = taglist.get(0);
            if (ltag.getNode() != null && !ltag.getNode().equals("")) {
                jsonnodes = new JSONObject().put(ltag.getNode(), jsonnodes);
                result = jsonnodes.toString();
            } else {
                result = jsonnodes.toString();
            }
        } catch (Exception e) {
            LOG.error("Exception: ", e);
        }
        return result;
    }

    public String validateMobileNumber(String pmobilenumber, String appId, HttpServletRequest request) {
        String result = "";
        JSONObject lfinalreq = new JSONObject();
        JSONObject ljsonreqhead = new JSONObject();
        String linterfaceId = INTERFACE_ID_SMS_USER;
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, linterfaceId);
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_STATUS, true);
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_USER_ID, pmobilenumber);
        JSONObject ljsonreqbody = new JSONObject();
        ljsonreqbody.put(ServerConstants.MOBILE_NUMBER, pmobilenumber);
        ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
        ljsonreqbody.put(ServerConstants.FLAG, ServerConstants.SMS);
        JSONObject body = new JSONObject();
        body.put(linterfaceId + "Request", ljsonreqbody);
        lfinalreq.put(ServerConstants.MESSAGE_HEADER, ljsonreqhead);
        lfinalreq.put(ServerConstants.MESSAGE_BODY, body);
        LOG.debug("validateMobileNumber:  Final Request to appzillon Server {}", lfinalreq.toString());
        String lresponse = callServer(lfinalreq.toString(), request);
        JSONObject resjson = new JSONObject(lresponse);
        result = resjson.toString();
        LOG.debug("validateMobileNumber-result: {}", result);
        return result;
    }

    public boolean authenticatePin(String pin, String puserId, String pappId, String pmobileNumber, HttpServletRequest request) {
        boolean lstatus = false;
        String lresponse = "";
        JSONObject lfinalreq = new JSONObject();
        JSONObject ljsonreqhead = new JSONObject();
        String linterfaceId = ServerConstants.INTERFACE_ID_RE_LOGIN;
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_APP_ID, pappId);
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, linterfaceId);
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_STATUS, true);
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_USER_ID, puserId);
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.SMS);
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY, "000NEW");
        ljsonreqhead.put(ServerConstants.PIN, pin);
        JSONObject ljsonreqbody = new JSONObject();
        ljsonreqbody.put(ServerConstants.PIN, pin);
        ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_USER_ID, puserId);
        ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.SMS);
        ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY, "000NEW");
        ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_APP_ID, pappId);
        ljsonreqbody.put(ServerConstants.HASHKEY1, ServerConstants.SMS);
        ljsonreqbody.put(ServerConstants.HASHKEY2, pmobileNumber);
        ljsonreqbody.put(ServerConstants.SYSDATE, new Date().toString());
        ljsonreqbody.put(ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE, ServerConstants.USER_PRIVS_NOT_REQUIRED);
        ljsonreqbody.put(ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE, ServerConstants.USER_PRIVS_NOT_REQUIRED);
        ljsonreqbody.put(ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE, ServerConstants.USER_PRIVS_NOT_REQUIRED);
        JSONObject body = new JSONObject();
        body.put(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST, ljsonreqbody);
        lfinalreq.put(ServerConstants.MESSAGE_HEADER, ljsonreqhead);
        lfinalreq.put(ServerConstants.MESSAGE_BODY, body);
        String authenticationType = PropertyUtils.getPropValue(pappId, ServerConstants.AUTHENTICATION_TYPE);
        if (ServerConstants.HASH_DEVICE_ID.equalsIgnoreCase(authenticationType)) {
            LOG.debug("authenticatePin-> authenticationType: {}", authenticationType);
            Message lMessage = MessageFactory.getMessage(lfinalreq.toString(), null);
            lMessage.getHeader().setServiceType(ServerConstants.FETCH_SECURITY_PARAMS);
            DomainStartup.getInstance().processRequest(lMessage);
            LOG.debug("Security Parameter Details : {}", lMessage.getSecurityParams());
            String lServerToken = lMessage.getSecurityParams().getServerToken();
            String lHashedPin = HashUtils.hashSHA256(pin, puserId + lServerToken);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("E',' dd-MM-yyyy HH:mm:ss");
            String lFormattedDate = dateFormatter.format(new Date());
            String lOtp = new HashXor().hashValue("SMS", pmobileNumber, "", puserId, lHashedPin, lFormattedDate);
            LOG.debug("authenticatePin + lHashedPin= {}", lHashedPin);
            LOG.debug("authenticatePin + lFormattedDate= {}", lFormattedDate);
            ljsonreqbody.put(ServerConstants.PIN, lOtp);
            ljsonreqbody.put(ServerConstants.SYSDATE, lFormattedDate);
            lfinalreq = new JSONObject();
            body = new JSONObject();
            body.put(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST, ljsonreqbody);
            lfinalreq.put(ServerConstants.MESSAGE_HEADER, ljsonreqhead);
            lfinalreq.put(ServerConstants.MESSAGE_BODY, body);
        }
        LOG.debug("authenticatePin:  Final Request to appzillon Server {}", lfinalreq.toString());
        lresponse = callServer(lfinalreq.toString(), request);
        JSONObject resjson = new JSONObject(lresponse);
        JSONObject loginresponse = resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_RES);
        lstatus = loginresponse.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS);

        lresponse = resjson.toString();
        LOG.debug("authenticatePin:  result =  {}", lresponse);
        return lstatus;
    }

    public String getErrorResponse(String errorMessage) {
        String response = "";
        JSONArray jsonarray = new JSONArray();
        JSONObject error = new JSONObject();
        error.put(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE, errorMessage);
        jsonarray.put(error);
        response = new JSONObject().put(ServerConstants.MESSAGE_ERROR, jsonarray).toString();
        return response;
    }

    public String callServer(String request, HttpServletRequest httpServletRequest) {
        String result = "";
        httpServletRequest.setAttribute(ServerConstants.ENCRYPTION_FLAG, ServerConstants.NO);
        JSONObject jsonObject = new JSONObject(request).getJSONObject(ServerConstants.MESSAGE_HEADER);
        jsonObject.put("smsType", true);
        JSONObject jsonObject1 = new JSONObject(request);
        jsonObject1.put(ServerConstants.MESSAGE_HEADER, jsonObject);
        request = jsonObject1.toString(0);
        result = new AppzillonRestWS().processRequest(request, httpServletRequest, null);
        return result;
    }

    @Override
    public String process(String pmobilenumber, String requestPayload,
                          String messageId, HttpServletRequest request) {
        JSONObject lfinalreq = new JSONObject();
        DocumentBuilder dBuilder;
        String luserId = "";
        String appId = "";
        try {
            String lservicetype = SMSProcessorUtils.serviceTypeIdentifier(requestPayload);
            try (InputStream isr = SMSRequestProcessorImpl.class.getClassLoader().
                    getResourceAsStream(Utils.getSmsServiceXmlFile(lservicetype))) {
                /*Veracode fix: Improper Restriction of XML External Entity Reference (CWE ID 611)*/
                dBuilder = XMLExternalEntity.getDocBuilder();
                Document doc = dBuilder.parse(isr);
                doc.getDocumentElement().normalize();
                appId = getIdByType(doc, ServerConstants.MESSAGE_HEADER_APP_ID);
                String lresponse = validateMobileNumber(pmobilenumber, appId, request);
                JSONObject resjson = new JSONObject(lresponse);
                if (resjson.has(ServerConstants.MESSAGE_ERROR)) {
                    return lresponse;
                } else {
                    luserId = resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject(INTERFACE_ID_SMS_USER + RESPONSE).getString(ServerConstants.MESSAGE_HEADER_USER_ID);
                    this.setDefaultLang(resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject(INTERFACE_ID_SMS_USER + RESPONSE).getString(ServerConstants.DEFAULTLANGUAGE));
                }

                String[] reqarray = SMSProcessorUtils.split(requestPayload, ServerConstants.SEPARATOR_SPACE);
                String ljsonbody = getJSONbody(doc, reqarray, pmobilenumber, appId, luserId, request);
                JSONObject resjsonbody = new JSONObject(ljsonbody);
                if (resjsonbody.has(ServerConstants.MESSAGE_ERROR)) {
                    return ljsonbody;
                }
                JSONObject body = new JSONObject(ljsonbody);
                lfinalreq.put(ServerConstants.MESSAGE_HEADER, getJsonReqHeadObj(doc, appId, luserId));
                lfinalreq.put(ServerConstants.MESSAGE_BODY, body);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            return getErrorResponse("Bad Request. Please check format");
        } catch (Exception e1) {
            return getErrorResponse("Sorry , Unable to Process request");
        }

        LOG.debug("Final request payload {}", lfinalreq.toString());
        String result = "";
        JSONObject checkresponse = new JSONObject(lfinalreq);
        if (!checkresponse.has(ServerConstants.MESSAGE_ERROR)) {
            String lresponse = this.callServer(lfinalreq.toString(), request);
            SMSResponseProcessor lsmsResponseProcessor = new SMSResponseProcessor();
            lsmsResponseProcessor.setLsmsProcessor(this);
            result = lsmsResponseProcessor.getResponse(lresponse, requestPayload);
        } else {
            result = getErrorMessage(checkresponse.toString());
        }
        LOG.debug("inside method process Response {}", result);
        SendSMSService smsService = new SendSMSService();
        result = smsService.sendSMS(appId, pmobilenumber, result);
        return result;
    }

    private JSONObject getJsonReqHeadObj(Document doc, String appId, String luserId) {
        JSONObject ljsonreqhead = new JSONObject();
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, getIdByType(doc, ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_USER_ID, luserId);
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_STATUS, true);
        ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.SMS);
        return ljsonreqhead;
    }


}
