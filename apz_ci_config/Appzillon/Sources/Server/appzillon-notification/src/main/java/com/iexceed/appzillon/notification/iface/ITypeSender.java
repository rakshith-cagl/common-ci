package com.iexceed.appzillon.notification.iface;

import com.iexceed.appzillon.json.JSONObject;

import java.util.Map;

public interface ITypeSender {
    public Map<String, String> sendNotification(Map<String, String> deviceDetails, String messageData, String appId, JSONObject pParams);

    public Map<String, String> sendNotification(Map<String, String> deviceDetails, JSONObject notificationJSON, String appId, JSONObject pParams);

}
