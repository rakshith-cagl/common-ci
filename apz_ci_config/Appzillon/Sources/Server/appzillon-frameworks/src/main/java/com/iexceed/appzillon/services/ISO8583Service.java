package com.iexceed.appzillon.services;

import com.iexceed.appzillon.dao.ISO8583Details;
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import org.apache.camel.spring.SpringCamelContext;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.iexceed.appzillon.utils.Constants.*;

/**
 * @author arthanarisamy
 */
public class ISO8583Service implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    ISO8583Service.class.getName());
    static Socket socket = null;
    protected ISO8583Details lisoSocketDetails = null;
    private String responseExceptionPayload = null;
    private Object requestPayload;

    public static Iterator<String> sortedIterator(Iterator<String> it,
                                                  Comparator<String> comparator) {
        List<String> list = new ArrayList<String>();
        while (it.hasNext()) {
            list.add(it.next());
        }

        Collections.sort(list, comparator);
        return list.iterator();
    }

    public static Comparator<String> getStrComparator() {
        Comparator<String> comparator = new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
        return comparator;
    }

    public static int converttoHex(int n) {
        return Integer.valueOf(String.valueOf(n), 16);
    }

    public static void print(IsoMessage m) {
        LOG.debug("{} : {}", ServerConstants.MESSAGE, m);
        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "TYPE: %04x\n" + m.getType());
        for (int i = 2; i <= 128; i++) {
            if (m.hasField(i)) {
                LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "F %3d(%s): %s -> '%s'\n" + i
                        + m.getField(i).getType() + m.getObjectValue(i)
                        + m.getField(i).toString());
            }
        }
    }

    private static void replaceTag(StringBuffer pcontent, String ptag, String pval) {
        if (pval == null) {
            pval = "";
        }
        int lindex = pval.indexOf(ptag);
        if (lindex >= 0) {
            // //Infinite Loop Case..
        } else {
            int ltagindex = -1;
            int ltaglen = 0;
            ltaglen = ptag.length();
            if (pcontent != null) {
                ltagindex = pcontent.indexOf(ptag);
                while (ltagindex >= 0) {
                    pcontent.replace(ltagindex, ltagindex + ltaglen, pval);
                    ltagindex = pcontent.indexOf(ptag);
                }
            }
        }
    }

    private static String replaceTag(String pcontent, String ptag, String pval) {
        String lcontent = pcontent;
        if (pcontent != null) {
            StringBuffer lbuf = new StringBuffer(pcontent);
            replaceTag(lbuf, ptag, pval);
            lcontent = lbuf.toString();
        }
        return lcontent;
    }

    private static String padString(String pstr, String ppadchar, int plen) {
        String lstr = pstr;
        int lactlen = pstr.length();
        int ldesiredlen = plen;
        if ((ldesiredlen - lactlen) > 0) {
            for (int i = 0; i < (ldesiredlen - lactlen); i++) {
                lstr = ppadchar + lstr;
            }
        }
        return lstr;
    }

    private static byte[] copyBytes(byte[] src, byte[] copy) {
        copy = Arrays.copyOf(src, src.length);
        LOG.debug("{} After Copying bytes to a new bytes array -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, Arrays.toString(copy));
        return copy;
    }

    public byte[] buildRequest(Message pMessage,
                               Object pRequestPayLoad, SpringCamelContext pContext) {
        LOG.info("{} Building request for external ISO8583 service. Request Payload is: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pRequestPayLoad);
        String lpayLoad = (String) pRequestPayLoad;
        LOG.debug("{} Sending request payload to build ISO8583 Message...", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        //		String iSOReqMsg = iSOMessageBuilder(lpayLoad, pMessage.getHeader().getInterfaceId(), pMessage.getHeader().getAppId());
        byte[] iSOReqMsg = iSOMessageBuilder(lpayLoad, pMessage.getHeader().getInterfaceId(), pMessage.getHeader().getAppId(), pMessage);
        LOG.debug("{} After building ISO 8583 message -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, new String(iSOReqMsg));
        return iSOReqMsg;
    }

    public void getSocketDetails(Message pMessage,
                                 SpringCamelContext pContext) {
        String interfaceId = pMessage.getHeader().getInterfaceId();
        String appId = pMessage.getHeader().getAppId();
        lisoSocketDetails = (ISO8583Details) ExternalServicesRouter.injectBeanFromSpringContext(appId + "_" + interfaceId, pContext);
        LOG.debug("{} ISO8583  service details bean injected {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lisoSocketDetails);

        int timeOut = lisoSocketDetails.getTimeOut();
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
        lisoSocketDetails.setTimeOut(timeOut);
    }

    public Object callService(Message pMessage,
                              Object pRequestPayLoad, SpringCamelContext pContext) {
        Socket socket = null;
        OutputStream lOutPutStream = null;
        InputStream lInputStream = null;
        JSONObject lResponseJson = null;
        LOG.debug("{} Getting Socket Details ....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        getSocketDetails(pMessage, pContext);

        pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad, lisoSocketDetails.getAutoGenElementMap(), lisoSocketDetails.getTranslationElementMap());
        pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad + ""));
        LOG.debug("{} After Appending Request Json With MaskedId : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        byte[] payload = buildRequest(pMessage, pRequestPayLoad.toString(), pContext);
        requestPayload = new String(payload);

        try {
            LOG.debug("{} Establishing socket connection....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            Utils.setExtTime(pMessage, "S");
            socket = establishSocketConnection(lisoSocketDetails.getEndPointURL(), Integer.parseInt(lisoSocketDetails.getPortNo()), lisoSocketDetails.getKeepAlive());
            LOG.debug("{} Socket Connection Established, Setting Time out:{} to millisecond: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lisoSocketDetails.getTimeOut(), lisoSocketDetails.getTimeOut() * 1000);
            socket.setSoTimeout(lisoSocketDetails.getTimeOut() * 1000);
            LOG.debug("{} Getting socket's output stream....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            lOutPutStream = socket.getOutputStream();
            LOG.debug("{} Getting socket's input stream....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            lInputStream = socket.getInputStream();
            boolean signOnStatus = true;
            if (ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getSignonRequired())) {
                LOG.info("{} SignOn is required before sending the service request payload...", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                LOG.debug("{} Building SignOn request....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                String signonRequest = buildSignOnRequest(lisoSocketDetails);
                LOG.debug("{} Building Sign on ISO 8583 Request....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                //    			String iSOSignOnReqMsg = iSOMessageBuilder(signonRequest, ServerConstants.ISO_SIGN_ON,pMessage.getHeader().getAppId());
                byte[] iSOSignOnReqMsg = iSOMessageBuilder(signonRequest, ServerConstants.ISO_SIGN_ON, pMessage.getHeader().getAppId(), pMessage);
                LOG.debug("{} Sign on ISO8583 request is built -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, iSOSignOnReqMsg);
                LOG.info("{} Sending SignOn Request....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                sendRequest(socket, iSOSignOnReqMsg, lOutPutStream);
                LOG.debug("{} SignOn Request is Sent....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                byte[] signOnResp = getResponse(socket, lInputStream);
                LOG.debug("{} Response Received for Signon Request is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, signOnResp);
                LOG.debug("{} Parsing Signon Response....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                String signOnRespJson = parseISOMessage(lisoSocketDetails, signOnResp, ServerConstants.ISO_SIGN_ON, pMessage.getHeader().getAppId());
                LOG.debug("{} After parsing Signon Response -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, signOnRespJson);
                signOnStatus = processSignOnResponse(signOnRespJson);
                LOG.debug("{} Signon status from the response -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, signOnStatus);

            }
            if (signOnStatus) {
                LOG.debug("{} Building actual ISO 8583 request....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                byte[] iSOReqMsg = payload;
                LOG.debug("{} Built actual ISO 8583 request is-: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, new String(iSOReqMsg));
                LOG.debug("{} Sending actual service request....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                Utils.setExtTime(pMessage, "S");
                sendRequest(socket, iSOReqMsg, lOutPutStream);
                LOG.debug("{} Sent actual service request....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                byte[] iSOResp = getResponse(socket, lInputStream);
                Utils.setExtTime(pMessage, "E");
                LOG.debug("{} Response Received for actual Request is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, iSOResp);
                String lresponse = parseISOMessage(lisoSocketDetails, iSOResp, pMessage.getHeader().getInterfaceId(), pMessage.getHeader().getAppId());
                LOG.debug("{} After Parsing actual service response -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lresponse);
                lResponseJson = (JSONObject) processResponse(pMessage, lresponse, pContext);
                LOG.debug("{} Building actual service response -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lresponse);
                ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.SUCCESS, new String(iSOResp), new String(payload));

            }

        } catch (IOException ex) {
            LOG.error("{} IOException - ex: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
            responseExceptionPayload = ex.getMessage();
        } finally {

            try {

                if (!lisoSocketDetails.getKeepAlive().equalsIgnoreCase(ServerConstants.YES)) {
                    if (lOutPutStream != null) {
                        lOutPutStream.close();
                    }

                    if (lInputStream != null) {
                        lInputStream.close();
                    }

                    if (socket != null) {
                        socket.close();
                    }

                }
            } catch (IOException e) {
                LOG.error(IOEXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);

            }
            if (responseExceptionPayload != null) {
                Utils.setExtTime(pMessage, "E");
                ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, responseExceptionPayload, requestPayload);
            }

        }
        LOG.debug("{} Returning response -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lResponseJson);
        return lResponseJson;

    }

    public boolean processSignOnResponse(String pSignOnResponse) {
        boolean signedOn = false;
        try {
            JSONObject signOnJson = new JSONObject(pSignOnResponse);
            LOG.debug("{} After converting Sigon response to a JSON -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, signOnJson);
            String json0810Resp = signOnJson.getString(ServerConstants.TYPE);
            LOG.debug("{} SignOn Response is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, json0810Resp);
            if (json0810Resp != null && ISO_MSG_TYPE.ISO_0810.toString().equals(json0810Resp)) {
                signedOn = true;
            }
        } catch (JSONException e) {
            LOG.error(JSON_EXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_019
                    .toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_019));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        return signedOn;
    }

    public Object processResponse(Message pMessage, Object pResponse, SpringCamelContext pContext) {

        JSONObject lResponseJson = new JSONObject((String) pResponse);
        return lResponseJson;
    }

    /**
     * @param pIpAddress
     * @param pPortNo
     * @return
     */
    public Socket establishSocketConnection(String pIpAddress, int pPortNo, String keepAlive) {

        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket(pIpAddress, pPortNo);
                LOG.debug("{} Client connected to Server through PORT -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, socket.getLocalPort());
                if (keepAlive.equalsIgnoreCase(ServerConstants.YES)) {
                    socket.setKeepAlive(true);
                }
            }
        } catch (NumberFormatException e) {
            responseExceptionPayload = e.getMessage();
            LOG.error("{} NumberFormatException: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        } catch (UnknownHostException e) {
            responseExceptionPayload = e.getMessage();
            LOG.error("{} UnknownHostException: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        } catch (IOException e) {
            responseExceptionPayload = e.getMessage();
            LOG.error("{} callExternalService - IOException: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        }
        return socket;
    }

    public byte[] iSOMessageBuilder(String inputJSon, String interfaceId, String appId, Message pMessage) {
        byte[] lmsgbytes = null;
//		String lheader = "";

        try {
            JSONObject reqBody = new JSONObject(inputJSon);
            LOG.debug("{} request Body JSON: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, reqBody);
            String type = reqBody.getString("type");
            String lheader = reqBody.getString(HEADER);
            Iterator<String> fields;
            fields = reqBody.keys();
            StringBuilder bitMap = new StringBuilder();
            Map<String, String> elementMap = new TreeMap<String, String>();

            fields = sortedIterator(fields, getStrComparator());
            while (fields.hasNext()) {

                String field = (String) fields.next();
                if (!"type".equalsIgnoreCase(field)
                        && !ServerConstants.HEADER.equalsIgnoreCase(field)) {
                    bitMap.append(reqBody.getString(field));
                    LOG.debug("{} Position: {}, value: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, field, reqBody.getString(field));
                    elementMap.put(field, reqBody.getString(field));
                }

            }
            LOG.debug("{} bitMap: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, bitMap);
            LOG.debug("{} elementMap: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, elementMap);

            IsoMessage message = getISOMessage(elementMap, type, interfaceId, appId, pMessage);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            message.write(byteArrayOutputStream, 0);
            LOG.debug("{} Before Setting Header -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, byteArrayOutputStream.toString());
            /*
             * Below changes are made by Samy on 31/08/2015
             * To address header length and static string
             */
            lmsgbytes = byteArrayOutputStream.toByteArray();
            int lmsglen = lmsgbytes.length;
            String lnewln = "\n";
            byte[] lnewlnbytes = lnewln.getBytes();
            int lnewlinebytelen = lnewlnbytes.length;
            if (ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getNewLineReq())) {
                lmsglen = lmsglen + lnewlinebytelen;
            }
            if (lisoSocketDetails.getIsoReqHeaderMask() != null) {
                int lidx = lisoSocketDetails.getIsoReqHeaderMask().indexOf(ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH);
                if (lidx > -1) {
                    ////Length is Required in the Header..
                    int lmasklen = lisoSocketDetails.getIsoReqHeaderMask().length();
                    int lheaderlen = Integer.parseInt(lisoSocketDetails.getIsoReqHeaderLength());
                    int ldesiredlenoflen = lheaderlen - (lmasklen - (ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH.length()));
                    String lmsglenstr = Integer.toString(lmsglen);
                    lmsglenstr = padString(lmsglenstr, "0", ldesiredlenoflen);
                    lheader = replaceTag(lisoSocketDetails.getIsoReqHeaderMask(), ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH, lmsglenstr);
                }
            }

            // Header Length
            /**
             * Changes made by Samy on 19/12/2016 to append header lenght to the
             * actual message lenth.
             */
            byte[] lheaderBytes = lheader != null ? lheader.getBytes() : new byte[0];

            int lHeaderLength = lheaderBytes.length;
            LOG.debug("{} Header Length -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lHeaderLength);
            // Converting header length int to binary
            lmsglen = lmsglen + lHeaderLength;
            LOG.debug("{} Message length + Header Length -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lmsglen);
            LOG.debug("{} Header -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lheader);
            message.setIsoHeader(lheader);
            /////Convert the Message into Bytes
            byteArrayOutputStream = new ByteArrayOutputStream();
            message.write(byteArrayOutputStream, Integer.parseInt(lisoSocketDetails.getMsgLengthBytes()));
            lmsgbytes = byteArrayOutputStream.toByteArray();
            lmsglen = lmsgbytes.length;
            if (ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getNewLineReq())) {
                lmsglen = lmsglen + lnewlinebytelen;
                byte[] lnewmsgbytes = new byte[lmsglen];
                ////Copy Message -
                /*
                 * Use copyBytes(byte1[], byte2[]);
                 * Only if JDK/JRE used is 1.6
                 */
                System.arraycopy(lmsgbytes, 0, lnewmsgbytes, 0, lmsgbytes.length);
                ////Copy Newline bytes as well..
                System.arraycopy(lnewlnbytes, 0, lnewmsgbytes, lmsgbytes.length, lnewlnbytes.length);
                lmsgbytes = lnewmsgbytes;
            }

            LOG.debug("{} ISO Message To String -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, new String(lmsgbytes));
            LOG.debug("{} ISO Message Length -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lmsgbytes.length);

            LOG.debug("{} Header: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, message.getIsoHeader());
        } catch (JSONException ex) {
            LOG.error(JSON_EXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
        } catch (IOException ex) {
            LOG.error(IOEXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
        }
        return lmsgbytes;
    }

    public IsoMessage getISOMessage(Map<String, String> elementMap,
                                    String type, String interfaceId, String appId, Message pMessage) {
        IsoMessage m = null;
        MessageFactory<IsoMessage> mfact = null;
        try {
            String configFile = Utils.getIsoConfigFile(appId, interfaceId);
            LOG.debug("{} getISOMessage - configFile name: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, configFile);
            LOG.debug("{} Config File Loaded..Before", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            /**
             * Below changes are made to read the iso config file from class
             * path instead of a location in c drive. Changes made by Samy on
             * 14-04-2014
             */
            URL lconfigurl = ISO8583Service.class.getClassLoader().getResource(
                    configFile);
            LOG.debug("{} getISOMessage - lconfigurl: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lconfigurl);
            mfact = ConfigParser.createFromUrl(lconfigurl);


            /*
             * Commenting the below block of code to generate field 7 & 11
             * as the same is handled based on condition and configuration
             * Changes made by Samy on 31/08/2015
             */
            /*
             *//**
             * Commenting the trace number generation in memory and using the
             * sequence from DB. Changed made by Samy on 18-12-2013
             *//*
            mfact.setTraceNumberGenerator(new SimpleTraceGenerator(
                    (int) (System.currentTimeMillis() % 1000000)));

			  *//**
             * Setting field 7 with the date Changes made by samy on 19-12-2013
             *//*
            mfact.setAssignDate(true);*/
            /*
             * Below changes are made by
             * Samy on 09/09/2015
             * to set field 7 to the message factory
             */
            if (elementMap.containsKey("7")) {
                mfact.setAssignDate(true);
            }

            LOG.debug("{} Config File Loaded..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

            int msgType = Integer.valueOf(String.valueOf(type), 16);
            LOG.debug("{} getISOMessage - MsgType: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, msgType);
            m = mfact.newMessage(msgType);
            m.setIsoHeader("");

            ////Field 11
            if (ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getAutoGenerateField11())) {
                m.setValue(11, getField11(pMessage), m.getField(11).getType(), m.getField(11).getLength());
            }
            /*
             * Below changes to set binary bitmap
             * done by Samy on 31/08/2015
             */
            LOG.debug("{} Request Binary Bitmap -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lisoSocketDetails.getIsoReqBinaryBitmap());
            if (ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getIsoReqBinaryBitmap())) {
                mfact.setUseBinaryBitmap(true);
                m.setBinaryBitmap(true);
            }
            /*
             * Below changes to set message as binary
             * done by Samy on 31/08/2015
             */
            if (ServerConstants.ISO8583_REQUEST_TYPE_BINARY.equalsIgnoreCase(lisoSocketDetails.getIsoReqFormat())) {
                m.setBinary(true);
            }

            Set<String> elementKeys = elementMap.keySet();
            Iterator<String> position = elementKeys.iterator();
            position = sortedIterator(position, getStrComparator());
            while (position.hasNext()) {
                String lKey = (String) position.next();
                int field = 0;
                if ((!"type".equals(lKey)) && (!HEADER.equals(lKey))
                        && (!"11".equals(lKey)) && (!"37".equals(lKey))
                        && (!"7".equals(lKey))) {
                    field = Integer.parseInt(lKey);

                    LOG.debug("{} Adding Field : {} : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, field, elementMap.get(lKey));
                    m.setValue(field, elementMap.get(lKey), m.getField(field)
                            .getType(), m.getField(field).getLength());

                    LOG.debug("{} ISO Message - After : m.getField({}):{}, Length: {}, value: {}"
                            , ServerConstants.LOGGER_PREFIX_FRAMEWORKS, field, m.getField(field), m.getField(field).getLength(), m.getField(field).getValue());
                    LOG.debug("ISO Message - After : Type: {}", m.getField(field).getType());
                } else {
                    if ("11".equals(lKey)) {
                        if (!ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getAutoGenerateField11())) {
                            m.setValue(11, elementMap.get(lKey), m.getField(11).getType(), m
                                    .getField(11).getLength());
                        }
                    } else if ("37".equals(lKey)) {
                        if (elementMap.get(lKey) != null
                                && elementMap.get(lKey).length() > 0) {
                            m.setValue(37, elementMap.get(lKey), m.getField(37)
                                    .getType(), m.getField(37).getLength());
                        } else {
                            String lref = getRef37();
                            LOG.debug("{} Field 37 - lref: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lref);
                            m.setValue(37, lref, m.getField(37).getType(), m
                                    .getField(37).getLength());
                        }

                    }

                }

            }

        } catch (IOException ex) {
            LOG.error(IOEXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
        }
        return m;
    }

    public String getRef37() {
        String lref = "";
        try {
            Calendar cal = Calendar.getInstance();
            lref = new SimpleDateFormat("ddMMhhmmssSSS").format(cal.getTime());
            LOG.debug("{} Reference Time : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lref);

            if (lref.length() > 12) {
                lref = lref.substring(0, 12);
            }
        } catch (Exception ex) {
            lref = "000000000000";
        }
        return lref;
    }

    public String parseISOMessage(ISO8583Details pISODet, byte[] isoResponse,
                                  String pintfaceid, String appId) {
        String response = null;

        try {
            String configFile = Utils.getIsoConfigFile(appId, pintfaceid);
            IsoMessage message = null;
            MessageFactory<IsoMessage> mfact = null;

            /**
             * Below changes are made to read the iso config file from class
             * path instead of a location in c drive. Changes made by Samy on
             * 16-04-2014
             */
            URL lconfigurl = ISO8583Service.class.getClassLoader().getResource(
                    configFile);
            LOG.debug("{} getISOMessage - lconfigurl: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lconfigurl);
            mfact = ConfigParser.createFromUrl(lconfigurl);

            /*
             * Below changes are to set binary bitmap
             * done by Samy on 31/08/2015
             */
            if (ServerConstants.YES.equalsIgnoreCase(lisoSocketDetails.getIsoRespBinaryBitmap())) {
                mfact.setUseBinaryBitmap(true);
            }

            //            mfact.setAssignDate(true);
            LOG.debug("{} parseISOMessage - isoRespHeaderLength: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pISODet.getIsoRespHeaderLength());


            readBitMap(mfact, isoResponse, Integer.parseInt(pISODet.getIsoRespHeaderLength()));
            //LOG.debug("ISO Message : " + new String(isoResponse, 0, isoResponse.length));
            message = mfact.parseMessage(isoResponse,
                    Integer.parseInt(pISODet.getIsoRespHeaderLength()));
            /*
             * Below changes are to set message as binary
             * done by Samy on 31/08/2015
             */
            if (ServerConstants.ISO8583_REQUEST_TYPE_BINARY.equalsIgnoreCase(lisoSocketDetails.getIsoRespFormat())) {
                message.setBinary(true);
            }
            response = buildISORespJson(message);
            LOG.debug("{} parseISOMessage - response: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, response);
        } catch (IOException ex) {
            LOG.error(IOEXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
        } catch (ParseException ex) {
            LOG.error("{} ParseException: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        return response;

    }

    private void readBitMap(MessageFactory<IsoMessage> mfact, byte buff[], int length) {
        LOG.info("printing response bitMap");
        boolean useBinary = mfact.getUseBinaryMessages();
        boolean binBitMap = mfact.isUseBinaryBitmap();
        final BitSet bs = new BitSet(64);
        int pos = 0;
        final int minlength = 4 + (useBinary ? 2 : 4) + (useBinary || binBitMap ? 8 : 16);
        try {
            if (useBinary || binBitMap) {
                final int bitmapStart = 4 + (useBinary ? 2 : 4);
                for (int i = bitmapStart; i < 8 + bitmapStart; i++) {
                    int bit = 128;
                    for (int b = 0; b < 8; b++) {
                        bs.set(pos++, (buff[i] & bit) != 0);
                        bit >>= 1;
                    }
                }
                // Check for secondary bitmap and parse if necessary
                if (bs.get(0)) {
                    if (buff.length < minlength + 8) {
                        throw new ParseException("Insufficient length for secondary bitmap", minlength);
                    }
                    for (int i = 8 + bitmapStart; i < 16 + bitmapStart; i++) {
                        int bit = 128;
                        for (int b = 0; b < 8; b++) {
                            bs.set(pos++, (buff[i] & bit) != 0);
                            bit >>= 1;
                        }
                    }
                    pos = minlength + 8;
                } else {
                    pos = minlength;
                }
            } else {
                // ASCII parsing

                final byte[] bitmapBuffer;
                bitmapBuffer = buff;
                for (int i = length + 4; i < length + 20; i++) {
                    if (bitmapBuffer[i] >= '0' && bitmapBuffer[i] <= '9') {
                        bs.set(pos++, ((bitmapBuffer[i] - 48) & 8) > 0);
                        bs.set(pos++, ((bitmapBuffer[i] - 48) & 4) > 0);
                        bs.set(pos++, ((bitmapBuffer[i] - 48) & 2) > 0);
                        bs.set(pos++, ((bitmapBuffer[i] - 48) & 1) > 0);
                    } else if (bitmapBuffer[i] >= 'A' && bitmapBuffer[i] <= 'F') {
                        bs.set(pos++, ((bitmapBuffer[i] - 55) & 8) > 0);
                        bs.set(pos++, ((bitmapBuffer[i] - 55) & 4) > 0);
                        bs.set(pos++, ((bitmapBuffer[i] - 55) & 2) > 0);
                        bs.set(pos++, ((bitmapBuffer[i] - 5) & 1) > 0);
                    } else if (bitmapBuffer[i] >= 'a' && bitmapBuffer[i] <= 'f') {
                        bs.set(pos++, ((bitmapBuffer[i] - 87) & 8) > 0);
                        bs.set(pos++, ((bitmapBuffer[i] - 87) & 4) > 0);
                        bs.set(pos++, ((bitmapBuffer[i] - 87) & 2) > 0);
                        bs.set(pos++, ((bitmapBuffer[i] - 87) & 1) > 0);
                    }
                }
                // Check for secondary bitmap and parse it if necessary
                if (bs.get(0)) {
                    if (buff.length < length + 16) {
                        throw new ParseException("Insufficient length for secondary bitmap", length);
                    }
                    for (int i = length + 20; i < length + 36; i++) {
                        if (bitmapBuffer[i] >= '0' && bitmapBuffer[i] <= '9') {
                            bs.set(pos++, ((bitmapBuffer[i] - 48) & 8) > 0);
                            bs.set(pos++, ((bitmapBuffer[i] - 48) & 4) > 0);
                            bs.set(pos++, ((bitmapBuffer[i] - 48) & 2) > 0);
                            bs.set(pos++, ((bitmapBuffer[i] - 48) & 1) > 0);
                        } else if (bitmapBuffer[i] >= 'A' && bitmapBuffer[i] <= 'F') {
                            bs.set(pos++, ((bitmapBuffer[i] - 55) & 8) > 0);
                            bs.set(pos++, ((bitmapBuffer[i] - 55) & 4) > 0);
                            bs.set(pos++, ((bitmapBuffer[i] - 55) & 2) > 0);
                            bs.set(pos++, ((bitmapBuffer[i] - 5) & 1) > 0);
                        } else if (bitmapBuffer[i] >= 'a' && bitmapBuffer[i] <= 'f') {
                            bs.set(pos++, ((bitmapBuffer[i] - 87) & 8) > 0);
                            bs.set(pos++, ((bitmapBuffer[i] - 87) & 4) > 0);
                            bs.set(pos++, ((bitmapBuffer[i] - 87) & 2) > 0);
                            bs.set(pos++, ((bitmapBuffer[i] - 87) & 1) > 0);
                        }
                    }
                    pos = 16 + minlength;
                } else {
                    pos = minlength;
                }
            }
        } catch (Exception ex) {
            LOG.error("Invalid ISO8583 bitmap", ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_056
                    .toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_056));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        bs.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < bs.length(); i++) {
            if (bs.get(i))
                sb.append(i + 1 + " ");
        }
        LOG.debug("available fields in response : {}", sb.toString());

    }

    public String buildISORespJson(IsoMessage m) {
        LOG.debug("{} Response Message : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, m);
        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "TYPE: %04x\n" + m.getType());
        LOG.debug("{} Debug String: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, m.debugString());
        String result = null;
        try {
            JSONObject isoResp = new JSONObject();
            isoResp.put(HEADER, m.getIsoHeader());
            for (int i = 2; i <= 128; i++) {
                if (m.hasField(i)) {
                    LOG.info("{} Field: {}, Value: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, i, m.getObjectValue(i));
                    isoResp.put(Integer.toString(i), m.getObjectValue(i).toString());
                }

            }

            Formatter formatter = getFormatter().format("%04x", m.getType());
            String respType = formatter.toString();
            LOG.debug("{} strRespType.toString(): {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, respType);
            isoResp.put("type", respType);
            LOG.debug("{} JSON Response: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, isoResp);
            result = isoResp.toString();
        } catch (JSONException jsonex) {
            LOG.error(JSON_EXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, jsonex);
        }
        return result;
    }

    public Formatter getFormatter() {
        return new Formatter();
    }

    private String getInputJson(String inputJson) {
        String jsonBody = null;
        try {
            JSONObject jSONObject = new JSONObject(inputJson);
            Iterator<?> keyIterator = jSONObject.keys();
            while (keyIterator.hasNext()) {
                String key = (String) keyIterator.next();
                for (ISO_MSG_TYPE types : ISO_MSG_TYPE.values()) {
                    LOG.debug("{} getMsgType - types: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, types.toString());
                    LOG.debug("{} Request MsgType: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, key);
                    if (key.equals(types.toString())) {
                        jsonBody = jSONObject.getString(types.toString());
                        break;
                    }
                }
            }
        } catch (JSONException ex) {
            LOG.error(JSON_EXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
        }
        return jsonBody;

    }

    public String formatDate() {
        String lFormatteDate = null;
        SimpleDateFormat sf = new SimpleDateFormat("ddmmHHmmss");
        Date date = new Date();
        lFormatteDate = sf.format(date);
        return lFormatteDate;

    }

    public String buildSignOnRequest(ISO8583Details isoSocketDetails) {
        LOG.debug("{} ****Inside buildSignOnRequest....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        JSONObject lSignOnReq = new JSONObject();
        JSONObject lSignOnReqJson = new JSONObject();
        try {
            LOG.debug("{} buildSignOnRequest - sigonRequest: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, isoSocketDetails.getSigonRequest());
            lSignOnReq = new JSONObject(isoSocketDetails.getSigonRequest());
            lSignOnReqJson = new JSONObject(lSignOnReq.toString());
            LOG.debug("{} sendSignOnRequest - lSignOnReqJson toString {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lSignOnReqJson);

        } catch (JSONException jsex) {
            jsex.getStackTrace();
            LOG.error("{} buildSignOnRequest - JSONException: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS
                    , jsex);
        }
        return lSignOnReqJson.toString();
    }

    public void sendRequest(Socket socket, String iSORequest, OutputStream dout) {
        try {
            dout.write(iSORequest.getBytes(), 0, iSORequest.length());
            dout.flush();

        } catch (IOException e) {
            LOG.error("{} IOException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        }
    }

    public void sendRequest(Socket socket, byte[] iSORequest, OutputStream dout) {
        try {
            DataOutputStream out = new DataOutputStream(dout);
            out.write(iSORequest, 0, iSORequest.length);
            out.flush();
            dout.flush();

        } catch (IOException e) {
            responseExceptionPayload = e.getMessage();
            LOG.error("{} IOException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        }
    }

    /**
     * @param socket
     * @param in
     * @return
     */
    public byte[] getResponse(Socket socket, InputStream in) {
        String str = null;
        byte[] resbytes = null;
		/*
		Changes made by Samy on 06/11/2017
		To read response bytes from Socket.
		 */
        try (DataInputStream din = new DataInputStream(in);) {

            resbytes = new byte[16 * 1024];
            byte[] lreadbytes = new byte[16 * 1024];
            boolean read = true;
            int lresplen = 0;
            while (read) {
                int lreadlen = 0;
                try {
                    if (socket.isClosed()) {
                        lreadlen = 0;
                        LOG.debug("{} Socket is closed....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                    } else {
                        lreadlen = din.read(lreadbytes);
                        LOG.debug("{} Reading Bytes and read bytes length -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lreadlen);
                    }
                } catch (SocketTimeoutException stoex) {
                    LOG.error("{} Timeout Exception..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                    responseExceptionPayload = stoex.getMessage();
                    lreadlen = 0;
                }


                LOG.debug("{} Bytes Recevied lreadlen-: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lreadlen);
                if (lreadlen <= 0) {
                    read = false;
                } else {
                    LOG.debug("{} Total Bytes Recevied lreadbytes-: {} Total Bytes Recevied resbytes-: {} Total Bytes Recevied lresplen-: {} Total Bytes Recevied lreadbytes length-: {}"
                            , ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lreadbytes, resbytes, lresplen, lreadbytes.length);
                    System.arraycopy(lreadbytes, 0, resbytes, lresplen, lreadlen);
                    lresplen = lresplen + lreadlen;
					/*
					Changes made by Samy on 06/11/2017
					To read response bytes from Socket.
		 			*/
                    read = false;
                }
            }
            LOG.debug("{} Total Bytes Received -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lresplen);
            str = new String(resbytes, 0, lresplen);
            LOG.debug("{} getResponse - Response ISO from external Socket server {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, str);

            if (Utils.isNotNullOrEmpty(str) && str.length() > 1) {
                String lleadingspace = str.substring(0, 1);
                if (" ".equals(lleadingspace)) {
                    LOG.debug("{} getResponse - Space Removed..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                    str = str.substring(1);
                    LOG.debug("{} getResponse - Message After Removing Space : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, str);
                }
            }
			/*
			Changes made by Samy on 06/11/2017
			To read response bytes from Socket.
		 	*/
        } catch (IOException e) {
            responseExceptionPayload = e.getMessage();
            LOG.error("{} getResponse - e: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        }
        return resbytes;
    }

    /**
     * @param socket
     * @param in
     * @return
     */
    public byte[] getResponseBufferedReader(Socket socket, InputStream in) {
        String lrespstr = "";
        try {
            BufferedReader lbufferedreader = null;
            InputStream linputstream = null;
            linputstream = socket.getInputStream();
            lbufferedreader = new BufferedReader(new InputStreamReader(linputstream));
            long lwaittill = System.currentTimeMillis() + (lisoSocketDetails.getTimeOut() * 1000);
            LOG.debug("{} Buffer will wait till -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lwaittill);
            boolean lbufferedreaderready = false;
            while (System.currentTimeMillis() <= lwaittill) {
                if (lbufferedreader.ready()) {
                    lbufferedreaderready = true;
                    LOG.debug("{} Breaking at ...: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, System.currentTimeMillis());
                    break;
                }
            }
            if (lbufferedreaderready) {
                LOG.debug("{} Ready...", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                lrespstr = "";
                int avail = linputstream.available();
                LOG.debug("{} Availability : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, avail);
                if (linputstream.available() > 1) {
                    for (int i = 0; i < avail; i++) {
                        int lchar = lbufferedreader.read();
                        lrespstr = lrespstr + (char) lchar;
                    }
                }
            }
            LOG.debug("{} Response Received : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lrespstr);
        } catch (Exception ex) {
            LOG.error("{} Receive Error : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ex);
        }
        return lrespstr.getBytes();
    }

    private String getField7() {
        String lFormatteDate = null;
        SimpleDateFormat sf = new SimpleDateFormat("MMddhhmmss");
        Date date = new Date();
        lFormatteDate = sf.format(date);
        LOG.debug("{} lFormatteDate: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lFormatteDate);
        return lFormatteDate;
    }

    synchronized private String getField11(Message pMessage) {
        Header lHeader = pMessage.getHeader();
        JSONObject lRequetJSON = pMessage.getRequestObject().getRequestJson();
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_FETCH_SEQUENCE_VALUE);
        JSONObject sequenceValueJSON = new JSONObject();
        sequenceValueJSON.put("sequenceName", "ISO8583_F11");
        pMessage.getRequestObject().setRequestJson(new JSONObject().put("sequenceDtlsRequest", sequenceValueJSON));
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject lSequenceDetailsJSON = pMessage.getResponseObject().getResponseJson();
        JSONObject lSequenceRespJson = lSequenceDetailsJSON.getJSONObject("sequenceDtlsResponse");
        String lSequenceValue = lSequenceRespJson.getString("sequenceValue");
        int field11Length = Integer.parseInt(lisoSocketDetails.getField11Length());
        String lpaddedField11 = Utils.getPaddedString("" + lSequenceValue, field11Length, '0', true);
        LOG.debug("{} Generated field 11 after padding -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lpaddedField11);
        pMessage.setHeader(lHeader);
        pMessage.getRequestObject().setRequestJson(lRequetJSON);

        return lpaddedField11;
    }

    private String getISOHeader(ByteArrayOutputStream pisoMessageBytes) {
        String lHeader = "";

        if (lisoSocketDetails.getIsoReqHeaderMask() != null
                && !lisoSocketDetails.getIsoReqHeaderMask().equals("")
                && lisoSocketDetails.getIsoReqHeaderMask().contains(ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH)) {
            byte[] lmsgbytes = pisoMessageBytes.toByteArray();
            int lmsglen = lmsgbytes.length;
			/*int toPadLength = Integer.parseInt(lisoSocketDetails.getIsoReqHeaderLength());
    			String paddedMessageLength = Utils.getPaddedString(""+ lmsglen, 4 , '0', true);
    			lHeader = lisoSocketDetails.getIsoReqHeaderMask().replace(ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH, paddedMessageLength);*/

            ////Length is Required in the Header..
            int lmasklen = lisoSocketDetails.getIsoReqHeaderMask().length();
            int lHeaderLength = Integer.parseInt(lisoSocketDetails.getIsoReqHeaderLength());
            LOG.debug("{} Header Mask Length -: {}, Header Length : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lmasklen, lHeaderLength);
            int ldesiredlenoflen = lHeaderLength - (lmasklen - (ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH.length()));
            LOG.debug("{} Header Desired Length -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ldesiredlenoflen);
            String lmsglenstr = Integer.toString(lmsglen);
            LOG.debug("{} Message Length Before Padding -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lmsglenstr);
            lmsglenstr = Utils.getPaddedString(lmsglenstr, ldesiredlenoflen, '0', true);
            LOG.debug("{} Message Length After Padding -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lmsglenstr);
            LOG.debug("{} Request Header Mask -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lisoSocketDetails.getIsoReqHeaderMask());
            lHeader = replaceTag(lisoSocketDetails.getIsoReqHeaderMask(), ServerConstants.ISO8583_REQUEST_HEADER_MESSAGE_LENGTH, lmsglenstr);
            LOG.debug("{} Message Header -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lHeader);
        }
        return lHeader;
    }

    public enum ISO_MSG_TYPE {

        ISO_0100("0100"), ISO_0110("0110"), ISO_0120("0120"), ISO_0121("0121"), ISO_0130(
                "0130"), ISO_0200("0200"), ISO_0210("0210"), ISO_0220("0220"), ISO_0221(
                "0221"), ISO_0230("0230"), ISO_0400("0400"), ISO_0420("0420"), ISO_0421(
                "0421"), ISO_0430("0430"), ISO_0800("0800"), ISO_0810("0810"), ISO_0820(
                "0820"), ISO_1200("1200"), ISO_1210("1210"), ISO_1304("1304"), ISO_1314("1314");
        String isomsgtype;

        private ISO_MSG_TYPE(String type) {
            isomsgtype = type;
        }

        @Override
        public String toString() {
            return isomsgtype;
        }
    }
}
