package com.iexceed.appzillon.notification.impl;

/**
 * @author Vinod Rawat
 */

import com.google.android.gcm.server.*;
import com.google.android.gcm.server.Message.Builder;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.notification.iface.ITypeSender;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class AndroidNotificationSenderImpl implements ITypeSender {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION, AndroidNotificationSenderImpl.class.getName());

    public AndroidNotificationSenderImpl() {
        // default constructor
    }

    public Map<String, String> sendNotification(Map<String, String> devicesDetails, JSONObject notificationJSON, String pAppId, JSONObject pParams) {
        Sender sender;
        Map<String, String> hash = new HashMap<>();
        Map<String, String> newHash = new HashMap<>();
        List<String> devices = new ArrayList<>(devicesDetails.values());
        String key = null;
        MulticastResult res = null;
        Message message = getCustomPayLoad(notificationJSON, pParams).build();
        key = PropertyUtils.getPropValue(pAppId, ServerConstants.ANDROID_NOTIFICATION_KEY).trim();
        sender = new Sender(key, Endpoint.FCM);

        // NOTIFICATION Proxy Changes -- START
        String proxyHost = PropertyUtils.getPropValue(pAppId, ServerConstants.PROXY_HOST).trim();
        String proxyPort = PropertyUtils.getPropValue(pAppId, ServerConstants.PROXY_PORT).trim();
        String proxyType = PropertyUtils.getPropValue(pAppId, ServerConstants.PROXY_TYPE).trim();
        String proxyUser = PropertyUtils.getPropValue(pAppId, ServerConstants.PROXY_USER).trim();
        String proxyPassword = PropertyUtils.getPropValue(pAppId, ServerConstants.PROXY_PIN).trim();
        String nonProxyHosts = PropertyUtils.getPropValue(pAppId, ServerConstants.NONPROXYHOSTS).trim();
        if (Utils.isNotNullOrEmpty(nonProxyHosts)) {
            System.setProperty("http.nonProxyHosts", nonProxyHosts);
        }
        if (Utils.isNotNullOrEmpty(proxyHost) && Utils.isNotNullOrEmpty(proxyPort)) {
            LOG.debug("{} Setting Proxy Details.", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            System.setProperty(proxyType + ".proxyHost", proxyHost);
            System.setProperty(proxyType + ".proxyPort", proxyPort);
            if (Utils.isNotNullOrEmpty(proxyUser)) {
                System.setProperty(proxyType + ".proxyUser", proxyUser);
                System.setProperty(proxyType + ".proxyPassword", proxyPassword);
            }

        }
        LOG.debug("{} Proxy Details are set", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        // NOTIFICATION Proxy Changes -- END
        try {
            int retryCount = Integer.parseInt(PropertyUtils.getPropValue(pAppId, ServerConstants.NOTIF_RETRY_COUNT).trim());
            LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Sending message to FCM server");
            LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "message : "+message+ " devices : "+devices);
            res = sender.send(message, devices, retryCount);
            LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Response from FCM : "+res);
            List<Result> resultsList = res.getResults();
            for (int i = 0; i < devices.size(); i++) {
                Result result = resultsList.get(i);
                setDevices(hash, devices, i, result);
            }
        } catch (IOException e) {
            LOG.error("IOException", e);
        }

        Iterator<String> it = devicesDetails.keySet().iterator();
        while (it.hasNext()) {
            String deviceId = it.next();
            String regId = devicesDetails.get(deviceId);
            if (hash.containsKey(regId)) {
                newHash.put(deviceId, hash.get(regId));
            } else {
                newHash.put(deviceId, "Unknown");
            }
        }

        return newHash;
    }

    private void setDevices(Map<String, String> hash, List<String> devices, int i, Result result) {
        if (result.getMessageId() != null) {
            hash.put(devices.get(i), "Success");
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                LOG.warn("{} Registration Id {} changed for this device to {}", ServerConstants.LOGGER_PREFIX_NOTIFICAITON, devices.get(i), canonicalRegId);
                devices.set(i, canonicalRegId);
            }
        } else {
            String regId = devices.get(i);
            String error = result.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                // application has been removed from device - unregister it
                hash.put(regId, "Uninstall");
                LOG.warn("{} {} No longer registered with APNS", ServerConstants.LOGGER_PREFIX_NOTIFICAITON, regId);
            } else {
                LOG.warn("{} {} Invalid regId", ServerConstants.LOGGER_PREFIX_NOTIFICAITON, regId);
                hash.put(regId, "Invalid");
            }
        }
    }

    private Builder getCustomPayLoad(JSONObject notificationJSON, JSONObject pCustomPayload) {
        Builder message = null;
        String messageData = notificationJSON.getString(ServerConstants.NOTIFICATION_MESSAGEDATA);
        message = new Message.Builder().addData("message", messageData);
        String title = notificationJSON.getString(ServerConstants.NOTIFICATION_TITLE);
        message.addData(ServerConstants.NOTIFICATION_TITLE, title);
        if (notificationJSON.has(ServerConstants.NOTIFICATION_CATEGORY)) {
            message.addData("notification_code", notificationJSON.getString(ServerConstants.NOTIFICATION_CATEGORY));
        }
        if (notificationJSON.has(ServerConstants.NOTIFICATION_IMAGE_URL)) {
            message.addData("image_url", notificationJSON.getString(ServerConstants.NOTIFICATION_IMAGE_URL));
        }
        if (pCustomPayload != null) {
            Map<String, String> parametersMap = JSONUtils.getJsonHashMap(pCustomPayload.toString());
            LOG.debug("{} After converting json to map : {}", ServerConstants.LOGGER_PREFIX_NOTIFICAITON, parametersMap);
            Iterator<Entry<String, String>> parameters = parametersMap.entrySet().iterator();
            while (parameters.hasNext()) {
                Map.Entry<String, String> parameter = parameters.next();
                message.addData(parameter.getKey(), parameter.getValue());
            }
        }
        LOG.debug("{} Custom Payload : {}", ServerConstants.LOGGER_PREFIX_NOTIFICAITON, message.toString());
        return message;

    }

    @Override
    public Map<String, String> sendNotification(Map<String, String> deviceDetails, String messageData, String appId, JSONObject pParams) {
        return Collections.emptyMap();
    }
}
