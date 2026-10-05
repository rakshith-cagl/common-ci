package com.iexceed.appzillon.notification.impl;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.notification.iface.ITypeSender;
import com.iexceed.appzillon.notification.utils.hauwei.exception.HuaweiMesssagingException;
import com.iexceed.appzillon.notification.utils.hauwei.message.AndroidConfig;
import com.iexceed.appzillon.notification.utils.hauwei.message.Message;
import com.iexceed.appzillon.notification.utils.hauwei.messaging.HuaweiApp;
import com.iexceed.appzillon.notification.utils.hauwei.messaging.HuaweiMessaging;
import com.iexceed.appzillon.notification.utils.hauwei.model.Urgency;
import com.iexceed.appzillon.notification.utils.hauwei.reponse.SendResponse;
import com.iexceed.appzillon.notification.utils.hauwei.util.InitAppUtils;
import com.iexceed.appzillon.utils.ServerConstants;

import java.util.*;

public class HauweiNotificationSenderImpl implements ITypeSender {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getNotificationsLogger(
            ServerConstants.LOGGER_NOTIFICATION, AndroidNotificationSenderImpl.class.getName());

    public Map<String, String> sendNotification(Map<String, String> devicesDetails, JSONObject notificationJSON,
                                                String pAppId, JSONObject pParams) {
        LOG.debug("{} STARTING HAUWEI NOTIFICATION", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);

        HuaweiApp app = InitAppUtils.initializeApp();
        HuaweiMessaging huaweiMessaging = HuaweiMessaging.getInstance(app);
        SendResponse response = null;
        Map<String, String> newHash = new HashMap<>();

        String requestData = getPayload(notificationJSON, pParams);

        AndroidConfig androidConfig = AndroidConfig.builder().setCollapseKey(-1).setUrgency(Urgency.HIGH.getValue())
                .setTtl("1296000s").build();

        List<String> tokens = new ArrayList<>(devicesDetails.values());

        Message message = Message.builder().setData(requestData).setAndroidConfig(androidConfig)
                .addAllToken(tokens).build();

        try {
            response = huaweiMessaging.sendMessage(message);
        } catch (HuaweiMesssagingException e) {
            LOG.error("HuaweiMesssagingException", e);
        }

        if (response != null) {
            if (!ServerConstants.HUAWEI_SUCCESS_RESPONSE_CODE.equals(response.getCode())) {
                LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Hauwei Notification failed with Resposne code: "
                        + response.getCode() + " and Resposne message: " + response.getMsg());
            }
        } else {
            LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Sending notifications has failed");
        }

        // Setting status for devices
        Iterator<String> it = devicesDetails.keySet().iterator();
        while (it.hasNext()) {
            String deviceId = it.next();
            if (response == null) {
                newHash.put(deviceId, "Failed");
            } else {
                newHash.put(deviceId, response.getMsg());
            }

        }

        return newHash;
    }

    private String getPayload(JSONObject notificationJSON, JSONObject pCustomPayload) {
        JSONObject requestJson = new JSONObject();
        requestJson.put(ServerConstants.MESSAGE, notificationJSON.getString(ServerConstants.NOTIFICATION_MESSAGEDATA));
        requestJson.put(ServerConstants.NOTIFICATION_TITLE,
                notificationJSON.getString(ServerConstants.NOTIFICATION_TITLE));
        if (notificationJSON.has(ServerConstants.NOTIFICATION_CATEGORY)) {
            requestJson.put(ServerConstants.NOTIFICATION_CODE,
                    notificationJSON.getString(ServerConstants.NOTIFICATION_CATEGORY));
        }
        if (notificationJSON.has(ServerConstants.NOTIFICATION_IMAGE_URL)) {
            requestJson.put(ServerConstants.IMAGE_URL,
                    notificationJSON.getString(ServerConstants.NOTIFICATION_IMAGE_URL));
        }
        if (pCustomPayload != null) {
            Iterator<String> keys = pCustomPayload.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                requestJson.put(key, pCustomPayload.getString(key));
            }
        }
        LOG.debug("{} Notification to be sent to HMS servers : {}", ServerConstants.LOGGER_NOTIFICATION,
                requestJson.toString());
        return requestJson.toString();
    }

    @Override
    public Map<String, String> sendNotification(Map<String, String> deviceDetails, String messageData, String appId,
                                                JSONObject pParams) {
        return Collections.emptyMap();
    }

}
