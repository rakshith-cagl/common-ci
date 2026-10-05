package com.iexceed.appzillon.rest;

import com.iexceed.appzillon.domain.service.FileService;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

public class UploadService {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, UploadService.class.getName());

    private UploadService() {

    }

    static JSONArray createServerRequest(JSONObject appzillonHeader, JSONArray filedetailsarray, String destination,
                                         String overrideFlag) {
        JSONArray jsonfiles = new JSONArray();
        JSONObject jsonfile = null;

        String appId = null;
        String screenId = "";
        String userId = "";

        appId = appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        screenId = appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
        userId = appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_USER_ID);

        for (int i = 0; i < filedetailsarray.length(); i++) {
            jsonfile = new JSONObject();
            JSONObject filedetails = filedetailsarray.getJSONObject(i);

            String desfilePath = destination + File.separator + filedetails.get(ServerConstants.REPORT_FILENAME).toString();
            String fileType = "";
            if (desfilePath.contains(".")) {
                fileType = desfilePath.substring(desfilePath.lastIndexOf("."));
            }
            try {
                jsonfile.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
                jsonfile.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, screenId);
                jsonfile.put(ServerConstants.MESSAGE_HEADER_USER_ID, userId);
                jsonfile.put(ServerConstants.REPORT_FILENAME, desfilePath);
                jsonfile.put(ServerConstants.REPORT_FILE_TYPE, fileType);
                LOG.debug("{} overWrite Flag found to be {}", ServerConstants.LOGGER_PREFIX_RESTFULL, overrideFlag);
                jsonfile.put(ServerConstants.OVERRIDEFLAG, overrideFlag);
            } catch (JSONException e1) {
                LOG.error("{}{} : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, ServerConstants.JSON_EXCEPTION, e1);
            }
            jsonfiles.put(jsonfile);
        }
        return jsonfiles;
    }

    static boolean writeToFile(FormDataMultiPart multiPart, String filePath,
                               JSONArray filedetailsarray, String overrideFlag, int maxFileSize) throws IOException {
        boolean uploadstatus = false;

        ArrayList<String> filenames = new ArrayList<>();
        ArrayList<FormDataBodyPart> contentslist = new ArrayList<>();
        LOG.debug("filedetailsarray.length() : {}", filedetailsarray.length());
        getContentList(contentslist, filedetailsarray, filenames, multiPart);

        Iterator<String> i = filenames.iterator();
        Iterator<FormDataBodyPart> j = contentslist.iterator();

        while (i.hasNext() && j.hasNext()) {
            String filename = i.next();

            if (!filename.isEmpty()) {
                File file = new FileService().getFile(filePath + File.separator + filename);
                writeFile(file, overrideFlag, maxFileSize, j);
            }
            uploadstatus = true;
        }
        return uploadstatus;
    }

    private static void writeFile(File file, String overrideFlag, int maxFileSize, Iterator<FormDataBodyPart> j) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        if (!(file.isFile() && file.exists()) || ServerConstants.YES.equalsIgnoreCase(overrideFlag)) {

            FormDataBodyPart tempfile = j.next();
            File tempfilecontent = tempfile.getEntityAs(File.class);
            LOG.debug("temp file content :: {}", tempfilecontent);

            try (InputStream uploadinputstream = new FileInputStream(tempfilecontent)) {
                int fileSize = uploadinputstream.available();
                if (fileSize <= maxFileSize) {
                    try (OutputStream out = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int c = 0;
                        while ((c = uploadinputstream.read(buffer)) != -1) {
                            out.write(buffer, 0, c);
                        }
                        out.flush();
                    }

                }
            }
            Files.delete(tempfilecontent.toPath());
        }
    }

    private static void getContentList(ArrayList<FormDataBodyPart> contentslist, JSONArray filedetailsarray, ArrayList<String> filenames, FormDataMultiPart multiPart) {
        for (int i = 1; i <= filedetailsarray.length(); i++) {
            JSONObject filedetails = filedetailsarray.getJSONObject(i - 1);
            filenames.add(filedetails.get(ServerConstants.REPORT_FILENAME).toString());
            FormDataBodyPart contentsform = multiPart.getField(filenames.get(i - 1));
            contentslist.add(contentsform);
            LOG.debug("contentslist : {}", contentslist.size());
        }
    }

    static String getFileUploadLocation(String pAppId) {
        String filePath = PropertyUtils.getPropValue(pAppId, ServerConstants.FILE_UPLOAD_LOCATION);
        if (Utils.isNullOrEmpty(filePath)) {
            LOG.info("{} file Upload location not set ,File will be uploaded in root dir", ServerConstants.LOGGER_PREFIX_RESTFULL);
        }

        if (filePath.endsWith("\\")) {
            filePath = filePath.substring(0, filePath.lastIndexOf('\\'));
        }

        if (filePath.endsWith("/")) {
            filePath = filePath.substring(0, filePath.lastIndexOf('/'));
        }

        return filePath;
    }

    static int getMaxFileSize(String appId) {
        int maxFileSize;
        try {
            maxFileSize = Integer.parseInt(
                    PropertyUtils.getPropValue(appId, ServerConstants.MAX_UPLOAD_FILE_SIZE).trim());
            LOG.debug("{} Maximum file size configured in MB : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, (maxFileSize / (1024 * 1024)));
        } catch (Exception e) {
            maxFileSize = 2097152;
            LOG.error("{} Max file size not found in property file so setting to 2MB {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e);

        }
        return maxFileSize;
    }

    static String createAndSendRequestJSON(JSONObject jsonHeader,
                                           JSONArray jsonfiles, HttpServletRequest request, HttpServletResponse response) {
        String outputString = "";
        LOG.debug("{} appzillonHeader found in upload REST service {}", ServerConstants.LOGGER_PREFIX_RESTFULL, jsonHeader);
        String content = null;
        try {
            if ((!ServerConstants.INTERFACE_ID_UPLOAD_FILE
                    .equals(jsonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID))
                    && !ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS
                    .equals(jsonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID))
                    && !ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH
                    .equals(jsonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)))) {
                LOG.debug("{} Creating content for External Service", ServerConstants.LOGGER_PREFIX_RESTFULL);
                content = "{\"" + ServerConstants.MESSAGE_HEADER + "\":" + jsonHeader.toString() + "            ,\""
                        + ServerConstants.MESSAGE_BODY + "\": " + "" + "}";
            } else {
                content = "{\"" + ServerConstants.MESSAGE_HEADER + "\":" + jsonHeader.toString() + ",\""
                        + ServerConstants.MESSAGE_BODY + "\": {\"" + ServerConstants.INTERFACE_ID_UPLOAD_FILE
                        + "Request\":{\"Files\":" + jsonfiles.toString() + "}}}";
            }
            LOG.debug("{} Final Body Content Built : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, content);
            request.setAttribute(ServerConstants.ENCRYPTION_FLAG, ServerConstants.NO);
            outputString = new AppzillonRestWS().processRequest(content, request, response);

            JSONObject internalResponseobj = new JSONObject(outputString);
            JSONObject lappHeader = internalResponseobj.getJSONObject(ServerConstants.MESSAGE_HEADER);
            LOG.info("{} appzillonHeader in Response from internal server {}", ServerConstants.LOGGER_PREFIX_RESTFULL, lappHeader.toString());

        } catch (Exception e) {
            LOG.error("{} Exception: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e);
        }
        return outputString;
    }
}
