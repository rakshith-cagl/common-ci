package com.iexceed.appzillon.notification.impl;

import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.notification.exception.NotificationException;
import com.iexceed.appzillon.notification.iface.ITypeSender;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.owasp.encoder.Encode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebNotificationSenderImpl implements ITypeSender {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
                    WebNotificationSenderImpl.class.getName());

    public WebNotificationSenderImpl() {
        // default constructor
    }


    @Override
    public Map<String, String> sendNotification(Map<String, String> deviceDetails, JSONObject notificationJSON,
                                                String appId, JSONObject pParams) {
        Map<String, String> notificationstatus = new HashMap<>();
        LOG.debug("{} Sending Notification to web", ServerConstants.LOGGER_NOTIFICATION);
        URL url;
        HttpURLConnection conn = null;
        OutputStream os = null;

        JSONObject firebaseOutputjs = null;
        JSONObject json = new JSONObject();
        JSONObject requestJson = new JSONObject();
        List<String> deviceList = new ArrayList<>();
        for (Map.Entry<String, String> entry : deviceDetails.entrySet()) {
            deviceList.add(entry.getValue());
        }
        JSONArray jsdeviceArray = new JSONArray(deviceList);
        json.put(ServerConstants.NOTIFICATION_TITLE, notificationJSON.get(ServerConstants.NOTIFICATION_TITLE));
        json.put(ServerConstants.WEB_NOTIFICATION_BODY, notificationJSON.get(ServerConstants.NOTIFICATION_MESSAGEDATA));
        if (notificationJSON.has(ServerConstants.WEB_NOTIFICATION_ICON))
            json.put(ServerConstants.WEB_NOTIFICATION_ICON, notificationJSON.get(ServerConstants.WEB_NOTIFICATION_ICON));
        if (notificationJSON.has(ServerConstants.WEB_NOTIFICATION_CLICK_ACTION))
            json.put("click_action", notificationJSON.get(ServerConstants.WEB_NOTIFICATION_CLICK_ACTION));
        requestJson.put(ServerConstants.NOTIFICATION, json);
        requestJson.put(ServerConstants.WEB_NOTIFICATION_REGISTRATION_ID, jsdeviceArray);
        LOG.debug("{} Request JSON for firebase : {}", ServerConstants.LOGGER_NOTIFICATION, requestJson);

        try {
            url = new URL(PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.WEB_NOTIFICATION_URL).trim());
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.WEB_NOTIFICATION_METHOD).trim());
            conn.setRequestProperty(ServerConstants.CONTENT_TYPE, PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.WEB_NOTIFICATION_CONTENT_TYPE).trim());
            conn.setRequestProperty(ServerConstants.AUTHORIZATION, "key=" + PropertyUtils.getPropValue(appId, ServerConstants.WEB_NOTIFICATION_SERVER_KEY).trim());
            os = conn.getOutputStream();
            os.write(Encode.forHtmlContent(requestJson.toString()).getBytes(StandardCharsets.UTF_8));
            os.flush();
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                LOG.error("Failed : HTTP error code : {}", status);
                NotificationException notificationException = NotificationException.getNotificationExceptionInstance();
                notificationException.setMessage("Failed to send notification");
                throw notificationException;
            }
            setMotificationStatus(notificationstatus, conn, firebaseOutputjs, requestJson);
        } catch (Exception e) {
            LOG.error(ServerConstants.LOGGER_NOTIFICATION + ServerConstants.EXCEPTION, e);
            NotificationException lNotificationException = NotificationException.getNotificationExceptionInstance();
            lNotificationException.setMessage(lNotificationException.getNotificationExceptionMessage(NotificationException.Code.APZ_NT_002));
            lNotificationException.setCode(NotificationException.Code.APZ_NT_002.toString());
            lNotificationException.setPriority("1");
            throw lNotificationException;
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (IOException e) {

                LOG.error(ServerConstants.LOGGER_NOTIFICATION + ServerConstants.IOEXCEPTION, e);
            }
            if (conn != null)
                conn.disconnect();
        }
        LOG.debug("{} Notification status : {}", ServerConstants.LOGGER_NOTIFICATION, notificationstatus);
        return notificationstatus;
    }

    private void setMotificationStatus(Map<String, String> notificationstatus, HttpURLConnection conn, JSONObject firebaseOutputjs, JSONObject requestJson) throws IOException {
        String output;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())))) {
            int i = 0;
            while ((output = br.readLine()) != null) {
                firebaseOutputjs = new JSONObject(output);
            }
            LOG.debug("{} firebase response : {}", ServerConstants.LOGGER_NOTIFICATION, firebaseOutputjs);
            if (firebaseOutputjs != null) {
                while ((firebaseOutputjs.getJSONArray(ServerConstants.WEB_NOTIFICATION_RESULTS).length()) != i) {
                    JSONObject resultjs = (JSONObject) firebaseOutputjs.getJSONArray(ServerConstants.WEB_NOTIFICATION_RESULTS).get(i);
                    if (resultjs.has(ServerConstants.ERROR)) {
                        notificationstatus.put(requestJson.getJSONArray(ServerConstants.WEB_NOTIFICATION_REGISTRATION_ID).get(i).toString(), resultjs.getString(ServerConstants.ERROR));
                    } else {
                        notificationstatus.put(requestJson.getJSONArray(ServerConstants.WEB_NOTIFICATION_REGISTRATION_ID).get(i).toString(), ServerConstants.SUCCESS);
                    }
                    i++;
                }
            }
        }
    }

    @Override
    public Map<String, String> sendNotification(Map<String, String> deviceDetails, String messageData, String appId,
                                                JSONObject pParams) {
        return Collections.emptyMap();
    }

}
