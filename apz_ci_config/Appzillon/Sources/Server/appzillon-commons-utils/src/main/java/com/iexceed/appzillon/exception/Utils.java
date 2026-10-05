/**
 *
 */
package com.iexceed.appzillon.exception;

import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.Response;
import com.iexceed.appzillon.message.SecurityParams;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.LargeData;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static com.iexceed.appzillon.utils.Constants.PROPERTIES;


/**
 * @author arthanarisamy Created on 10-07-2013
 *
 */
public class Utils {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, Utils.class.getName());

    private static String cStackTrace = null;

    private Utils() {

    }

    /*
     * @author arthanarisamy
     *
     * @param pEx
     *
     * @return String
     *
     * getStackTrace() method takes Exception as its parameter and returns the
     * stack trace of the exception as a string which helps in logging and
     * better debugging Created on 10-07-2013
     */
    // Server Appzillon �RS Ref� Changes (Server Appzillon 2.1 ) - Start
    // New utility method added
    public static String getStackTrace(Exception pEx) {
        StringWriter lSw = null;
        PrintWriter lPw = null;
        try {
            // Creating String writer Object
            lSw = new StringWriter();
            // Creating print writer object
            lPw = new PrintWriter(lSw);
            // Getting stack trace and storing it in print writer obj
            pEx.printStackTrace(lPw);
            // Storing the stack trace string to the string object
            cStackTrace = lSw.toString(); // StackTrace as a string
        } catch (Exception ex) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, ex);

        }
        // returning stack trace string
        return cStackTrace;
    }

    /*
     * @author arthanarisamy
     *
     * @param pInputXMLStr
     *
     * @param pQualifiedClassName
     *
     * @return object
     *
     * getUnMarshalled() returns Java Object after unmarshalling the input
     * request XML string.
     *
     * Firstly loads the Class at run time from the fully qualified class name.
     * Create JAXBContext taking dynamically created class. Create JAXB
     * UnMarshaller from the JAXB Context Create StringReader passing the input
     * xml string. UnMarshall to Java object passing the String reader. Returns
     * UnMarshalled Object
     */
    // Converting XML to Object using JAXB
    public static Object getUnMarshalled(String pInputXMLStr, String pQualifiedClassName) {
        Object lRespObject = null;

        try {
            // Loading Class at Run time from the fully qualified class name
            @SuppressWarnings("rawtypes") Class lC = Class.forName(pQualifiedClassName);

            // Creating JAXBContext instance
            JAXBContext lJaxbContext = JAXBContext.newInstance(lC);
            XMLInputFactory factory = XMLInputFactory.newInstance();
            //Veracode Issue fix: Improper Restriction of XML External Entity Reference (CWE ID 611)
            // to be compliant, completely disable DOCTYPE declaration:
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            // This causes XMLStreamException to be thrown if external DTDs are accessed.
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            // disable external entities
            factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);

            XMLStreamReader xsr = factory.createXMLStreamReader(new StreamSource(pInputXMLStr));

            // Creating JAXB UnMarshaller Instance
            Unmarshaller lJaxbUnmarshaller = lJaxbContext.createUnmarshaller();
            // Passing String reader Object for UnMarshalling
            lRespObject = lJaxbUnmarshaller.unmarshal(xsr);
        } catch (ClassNotFoundException | JAXBException | XMLStreamException pCnfex) {
            // Class not found Exception
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, pCnfex);
        } // JAXB Exception


        // Returning UnMarshalled Object
        return lRespObject;
    }

    /*
     * @author arthanarisamy
     *
     * @param pInputXMLStr
     *
     * @param pQualifiedClassName
     *
     * @return object
     *
     * getMarshalled() returns String after Marshalling the input response
     * Object.
     *
     * Firstly loads the Class at run time from the fully qualified class name.
     * Create JAXBContext taking dynamically created class. Create JAXB
     * Marshaller from the JAXB Context Create StringWriter Marshall the Java
     * object to XML String Returns Marshalled XML String
     */
    // Converting Object to XML using JAXB

    public static String getMarshalled(Object pResponseObj, String pQualifiedClassName) {

        String lResponse = null;
        try {

            // Loading Class at Run time from the fully qualified class name
            @SuppressWarnings("rawtypes") Class lC = Class.forName(pQualifiedClassName);

            // Creating JAXBContext instance
            JAXBContext lJaxbContext = JAXBContext.newInstance(lC);

            StringWriter stringWriter = new StringWriter();

            // Creating JAXB Marshaller Instance
            Marshaller lJaxbmarshaller = lJaxbContext.createMarshaller();
            // Setting property to format the xml
            lJaxbmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Passing the response object and Stringwriter to marshall
            lJaxbmarshaller.marshal(pResponseObj, stringWriter);
            // assigning the marshelled xml string to string object
            lResponse = stringWriter.toString();
        } catch (JAXBException pJaxex) {
            // JAXB Exception
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, pJaxex);
        } catch (ClassNotFoundException pCnfex) {
            // Class not found Exception
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, pCnfex);
        }
        // Returning response XML
        return lResponse;
    }

    public static String[] split(String original, String separator) {
        ArrayList<String> nodes = new ArrayList<>();
        String[] result = null;
        String lOriginal = original;
        try {
            int index = lOriginal.indexOf(separator);
            while (index >= 0) {
                nodes.add(lOriginal.substring(0, index));
                lOriginal = lOriginal.substring(index + separator.length());
                index = lOriginal.indexOf(separator);
            }
            if (isNotNullOrEmpty(lOriginal.trim())) {
                nodes.add(lOriginal);
            }
            result = new String[nodes.size()];
            if (!nodes.isEmpty()) {
                for (int loop = 0; loop < nodes.size(); loop++) {
                    result[loop] = nodes.get(loop);
                }
            }
        } catch (Exception e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, e);
        }
        return result;
    }

    public static Object[] splitToObj(String original, String separator) {
        ArrayList<String> nodes = new ArrayList<>();
        Object[] result = null;
        try {
            int index = original.indexOf(separator);
            while (index >= 0) {
                nodes.add(original.substring(0, index));
                original = original.substring(index + separator.length());
                index = original.indexOf(separator);
            }
            if (isNotNullOrEmpty(original.trim())) {
                nodes.add(original);
            }
            result = new String[nodes.size()];
            if (!nodes.isEmpty()) {
                for (int loop = 0; loop < nodes.size(); loop++) {
                    result[loop] = nodes.get(loop);
                }
            }
        } catch (Exception e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, e);
        }
        return result;
    }
    // Server Appzillon �RS Ref� Changes (Server Appzillon 2.1 ) - End

    public static String getPaddedString(String data, int len, char padder, boolean isPrefix) {
        String toReturn = "";
        try {
            if (data.length() < len) {
                while (data.length() < len) {
                    if (isPrefix) {
                        data = "" + padder + data;
                    } else {
                        data = data + padder;
                    }
                }
            } else if (data.length() > len) {
                data = data.substring(data.length() - len, data.length());
            }
            toReturn = data;
        } catch (Exception e) {
            toReturn = data;
        }
        return toReturn;
    }

    public static String generateRandomofLength(int length) {
        SecureRandom random = new SecureRandom();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return new String(digits);
    }

    public static String generateRandomNo() {
        SecureRandom random = new SecureRandom();
        return System.currentTimeMillis() + "" + random.nextInt(1000000000);
    }

    /**
     *
     * @param length
     * @param type
     * @return
     */


    public static String generateRandomOfLength(int length, String type) {
        StringBuilder result = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        if (type.equalsIgnoreCase(ServerConstants.OTP_ALPHA_NUMERIC)) {
            String alphanumeric = ServerConstants.ALL_UPPER_LOWER_ALPHABETS_NUMBERS;
            int size = alphanumeric.length();

            for (int i = 0; i < length; i++) {
                result.append(alphanumeric.charAt(secureRandom.nextInt(size)));
            }
        } else if (type.equalsIgnoreCase(ServerConstants.OTP_ALPHA)) {
            String alphanumeric = ServerConstants.ALL_UPPER_LOWER_ALPHABETS;
            int size = alphanumeric.length();
            for (int i = 0; i < length; i++) {
                result.append(alphanumeric.charAt(secureRandom.nextInt(size)));
            }
        } else if (type.equalsIgnoreCase(ServerConstants.OTP_NUMERIC)) {
            String alphanumeric = ServerConstants.ZERO_TO_NINE;
            int size = alphanumeric.length();
            for (int i = 0; i < length; i++) {
                result.append(alphanumeric.charAt(secureRandom.nextInt(size)));
            }
        } else if (type.equalsIgnoreCase(ServerConstants.OTP_DUMMY)) {
            StringBuilder dummyOTP = new StringBuilder();
            for (int i = 1; i <= length; i++) {
                dummyOTP.append(i);
            }
            result = dummyOTP;
        }
        LOG.debug("{} type {} OTP generated is : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, type, result);
        return result.toString();
    }

    /**
     * returns true if the string is not null and not empty
     * otherwise returns false
     * @param pValue
     * @return
     */

    public static boolean isNotNullOrEmpty(String pValue) {
        return pValue != null && !pValue.isEmpty();
    }

    public static boolean existsAndNotNullOrEmpty(JSONObject pJson, String pValue) {
        return pJson.has(pValue) && isNotNullOrEmpty(pJson.getString(pValue));
    }

    /**
     *returns true if the string is null or empty
     * otherwise returns false
     * @param pValue
     * @return
     */

    public static boolean isNullOrEmpty(String pValue) {

        return pValue == null || pValue.isEmpty();
    }


    public static void setExtTime(Message message, String flag) {
        if (flag.equalsIgnoreCase("S")) {
            message.getHeader().setExtStartTime(new Timestamp(new Date().getTime()));
        } else if (flag.equalsIgnoreCase("E")) {
            message.getHeader().setExtEndTime(new Timestamp(new Date().getTime()));
        }
    }

    public static String getTxnRefNum(String pUserId) {
        SecureRandom random = new SecureRandom();
        int randomNo = random.nextInt(1000000);
        if (isNullOrEmpty(pUserId)) return System.currentTimeMillis() + "" + randomNo;
        else return (pUserId + System.currentTimeMillis() + "" + randomNo);
    }

    public static boolean checkQualityOfPayload(Message pMessage) {
        String inputString = pMessage.getHeader().getInputString();
        LOG.trace("Raw Request payload to be hashed : {}", inputString);
        int i = inputString.indexOf(ServerConstants.QOP);
        String qop = inputString.substring(i + 15, i + 79);
        LOG.trace("QOP from Request Payload : {}", qop);
        inputString = inputString.replaceAll(inputString.substring(i - 1, i + 81), "");
        String lHashedCnonce = HashUtils.hashSHA256(pMessage.getHeader().getClientNonce(), pMessage.getHeader().getServerNonce() + pMessage.getSecurityParams().getServerToken());
        LOG.trace("Request payload After removing QOP : {}", inputString);
        LOG.trace("Request payload After removing QOP length : {}", inputString.length());
        inputString = StringEscapeUtils.escapeJava(inputString);
        LOG.debug("Escaped java request string for qop calculation {}", inputString);
        LOG.debug("Escaped java request string length {}", inputString.length());
        inputString = Base64.encodeBase64String(inputString.getBytes());
        LOG.trace("Request payload Base64 encoded : ", inputString);
        String hashedPayLoad = HashUtils.hashSHA256(inputString, lHashedCnonce);
        LOG.trace("Request payload hashed value : {}", hashedPayLoad);
        return qop.equals(hashedPayLoad);
    }

    public static String getFileNameForMailSMSTemplate(String appId, String pFileName) throws IOException {
        String fp;
        if (isNotNullOrEmpty(Logger.propertiesPath)) {
            fp = Logger.propertiesPath + "/" + appId + "/" + appId + "_" + pFileName;
            try (InputStream is = PropertyUtils.class.getClassLoader().getResourceAsStream(fp)) {
                if (is == null) {
                    return Logger.propertiesPath + "/" + appId + "/" + pFileName;
                }
                return fp;
            }


        } else {
            fp = appId + "_" + pFileName;
            try (InputStream is = PropertyUtils.class.getClassLoader().getResourceAsStream(fp)) {
                if (is == null) {
                    return pFileName;
                }
                return fp;
            }

        }

    }

    public static String getConstructedBody(String template, JSONObject fillersNValue) {

        Iterator<?> fillers = fillersNValue.keys();
        while (fillers.hasNext()) {
            String key = (String) fillers.next();
            template = template.replace(key, fillersNValue.getString(key));
        }

        return template;

    }

    public static Timestamp modifyEndDate(Timestamp enDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(enDate.getTime());
        // Adding one day to date.
        cal.add(Calendar.DAY_OF_MONTH, 1);
        enDate = new Timestamp(cal.getTime().getTime());
        return enDate;
    }


    public static String getPayLoadForQop(String apzHeader, String apzBody, String apzErrors) {
        StringBuilder br = new StringBuilder();
        if (Utils.isNotNullOrEmpty(apzErrors)) {
            br.append("{\"").append(ServerConstants.MESSAGE_ERROR).append("\":").append(apzErrors).append(",\"");
        } else {
            br.append("{\"");
        }
        br.append(ServerConstants.MESSAGE_HEADER).append("\":").append(apzHeader).append(",\"").append(ServerConstants.MESSAGE_BODY).append("\":").append(apzBody).append("}");
        return br.toString();
    }

    public static String appendQopWithPayload(String lresposne, String qop) {
        return "{\"" + ServerConstants.QOP + "\":\"" + qop + "\"," + lresposne.substring(1);
    }

    //generating OTP

    public static String getOTP(SecurityParams lSecurityParams) {
        LOG.debug("{} OTP Length : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lSecurityParams.getOtpLength());

        int lOTPLength = 8;
        LOG.debug("otpType from securityparams :" + lSecurityParams.getOtpFormat());
        try {
            lOTPLength = lSecurityParams.getOtpLength();
        } catch (NumberFormatException nfex) {
            LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " NumberFormatException", nfex);
        } catch (Exception e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Exception", e);
        }
        return Utils.generateRandomOfLength(lOTPLength, lSecurityParams.getOtpFormat());
    }

    public static List<LargeData> getPayloadList(String payload, Message pMessage) {
        LOG.debug(" Inside getPayloadList()");
        long startTime = System.currentTimeMillis();
        List<LargeData> ldRecsList = new ArrayList<>();
        LargeData ldrec = null;
        int i = 0;
        int maxTxnLogLen = 0;
        int seqNo = 0;
        int payloadLen = payload.length();
        String txnLogLen = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.MAX_TRANSACTION_LOG_LENGTH);

        if (Utils.isNotNullOrEmpty(txnLogLen)) {
            maxTxnLogLen = Integer.parseInt(txnLogLen);
        } else {
            maxTxnLogLen = 255;
        }
        LOG.debug(" Max transaction log length in each column :::: " + maxTxnLogLen);
        String refNo = Utils.getTxnRefNum(pMessage.getHeader().getUserId());
        while (i < payloadLen) {
            ldrec = new LargeData();
            ldrec.setRefNo(refNo);
            ldrec.setSeqNo(++seqNo);
            ldrec.setDataChunk1(getPayload(i, maxTxnLogLen, payloadLen, payload));
            i += maxTxnLogLen;
            ldrec.setDataChunk2(getPayload(i, maxTxnLogLen, payloadLen, payload));
            i += maxTxnLogLen;
            ldrec.setDataChunk3(getPayload(i, maxTxnLogLen, payloadLen, payload));
            i += maxTxnLogLen;
            ldrec.setDataChunk4(getPayload(i, maxTxnLogLen, payloadLen, payload));
            i += maxTxnLogLen;
            ldrec.setDataChunk5(getPayload(i, maxTxnLogLen, payloadLen, payload));
            i += maxTxnLogLen;
            ldRecsList.add(ldrec);
        }
        LOG.debug(" Time taken to generate ld Recs : {}", (System.currentTimeMillis() - startTime));
        return ldRecsList;
    }

    public static String getPayload(int stindex, int maxTxnLoglen, int payloadlen, String payload) {
        return (stindex > payloadlen) ? "" : payload.substring(stindex, Math.min(payloadlen, stindex + maxTxnLoglen));
    }

    public static boolean deleteFile(String fileName) {
        boolean status = false;
        LOG.debug("{} File to delete : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, fileName);
        File file = new File(fileName);
        if (file.exists()) {
            LOG.debug("{} file exists : ", ServerConstants.LOGGER_PREFIX_RESTFULL);
            try {
                Files.delete(file.toPath());
                status = true;
                LOG.debug("{} file deleted : ", ServerConstants.LOGGER_PREFIX_RESTFULL);
            } catch (IOException e) {
                LOG.error("{} failed to delete file : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, fileName);
            }
        } else {
            LOG.debug("{} file doesn't exists : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, fileName);
        }
        return status;
    }

    /* Exposing Appzillon API to Third Party Start*/
    public static String getUserIdFromAuthString(String authString) {
        byte[] lAuthString = Base64.decodeBase64(authString);
        String decodedString = new String(lAuthString);
        return decodedString.substring(0, decodedString.indexOf(':'));
    }

    public static String getPasswordFromAuthString(String authString) {
        byte[] lAuthString = Base64.decodeBase64(authString);
        String decodedString = new String(lAuthString);
        int indexOfColon = decodedString.indexOf(':');
        return decodedString.substring(indexOfColon + 1);
    }
    /* Exposing Appzillon API to Third Party End*/

    //For checking file path
    public static String filePathCheck(String filePath) {
        if (!(filePath.endsWith("/") || (filePath.endsWith("\\")))) {
            filePath = filePath.concat("/");
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Final destination file Path :: " + filePath);
        }
        return filePath;
    }

    public static String getPdfReportFileName(String appId) {
        if (isNotNullOrEmpty(Logger.propertiesPath)) {
            return Logger.propertiesPath + "/" + appId + "/" + ServerConstants.PDFREPORT_CONSTANTS_FILE_NAME + PROPERTIES;
        }
        return ServerConstants.PDFREPORT_CONSTANTS_FILE_NAME + PROPERTIES;
    }

    public static String getOtpSendFileName(String appId, String language, String channel) {
        if (isNotNullOrEmpty(Logger.propertiesPath)) {
            return Logger.propertiesPath + "/" + appId + "/" + ServerConstants.OTPSEND_FILE_TEMPLATE + language + channel + PROPERTIES;
        }
        return ServerConstants.OTPSEND_FILE_TEMPLATE + language + channel + PROPERTIES;
    }


    public static String getSmsServiceXmlFile(String lServiceType) {
        if (isNotNullOrEmpty(Logger.propertiesPath)) {
            return Logger.propertiesPath + "/" + ServerConstants.SMS_SERVICES + "/" + lServiceType + ".xml";
        }
        return ServerConstants.META_INF_SMS + lServiceType + ".xml";
    }

    public static String getIsoConfigFile(String appId, String interfaceId) {
        if (isNotNullOrEmpty(Logger.propertiesPath)) {
            return Logger.propertiesPath + "/" + appId + "/" + ServerConstants.ISO + "/" + interfaceId + ServerConstants.ISO_CONFIG_XML;
        }
        return ServerConstants.META_INF_SPRING + appId + "/" + interfaceId + ServerConstants.ISO_CONFIG_XML;
    }

    public static void copySourceFolder(String source, String destination) {
        try {
            FileUtils.copyDirectory(new File(source), new File(destination));
        } catch (IOException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, e);
        }
    }

    public static void searchAndReplaceString(String searchValue, String replaceValue, String path) {
        File f = new File(path);
        String content;
        try {
            content = FileUtils.readFileToString(new File(path), Charset.defaultCharset());
            FileUtils.writeStringToFile(f, content.replaceAll(searchValue, replaceValue), Charset.defaultCharset(), false);
        } catch (IOException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, e);
        }
    }

    public static void fileRename(String oldFileName, String newFileName) {
        File old = new File(oldFileName);
        File new1 = new File(newFileName);
        boolean isRenamed = old.renameTo(new1);
        if (!isRenamed) {
            LOG.error("not able to rename the file");
        }
    }

    public static void generateScriptFiles(String oldAppId, String newAppId, String newAppFilePath) {
        for (File file : FileUtils.listFiles(new File(newAppFilePath), null, true)) {
            String filePath = file.getPath();
            if (filePath.contains(ServerConstants.STATIC_SQL_DATA)) {
                searchAndReplaceString(oldAppId, newAppId, filePath);
            }
        }
    }

    public static void renameAndModifyFileContents(String oldAppId, String newAppId, String newAppFilePath) {
        for (File file : FileUtils.listFiles(new File(newAppFilePath), null, true)) {
            String filePath = file.getPath();
            if (filePath.contains(ServerConstants.STATIC_SQL_DATA)) {
                searchAndReplaceString(oldAppId, newAppId, filePath);
                executeInsertScript(newAppId, filePath);
            } else if (filePath.contains(ServerConstants.ISO_CONFIG_XML)) {
                String newFilePath = filePath.replaceAll(oldAppId + "_", newAppId + "_");
                fileRename(filePath, newFilePath);
            }
        }
    }

    public static void executeInsertScript(String appId, String filePath) {
        InitialContext context = null;
        DataSource dataSource = null;
        Connection conn = null;
        try {
            context = new InitialContext();
            dataSource = (DataSource) context.lookup(PropertyUtils.getPropValue(appId, ServerConstants.JNDI_DATA_SOURCE));
            conn = dataSource.getConnection();
            LOG.debug("{} script execution started", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            ScriptUtils.executeSqlScript(conn, new EncodedResource(new FileSystemResource(filePath), StandardCharsets.UTF_8));
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "script execution finished");
        } catch (Exception e) {
            LOG.error("Failed to execute script. ", e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (context != null) {
                    context.close();
                }
            } catch (NamingException | SQLException e) {
                LOG.error(ServerConstants.LOGGER_FRAMEWORKS, e);
            }
        }
    }

    public static int generateSecureRandom(int maximumValue) {
        SecureRandom ranGen = new SecureRandom();
        return ranGen.nextInt(maximumValue);
    }
    /* End*/
    public static boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

}
