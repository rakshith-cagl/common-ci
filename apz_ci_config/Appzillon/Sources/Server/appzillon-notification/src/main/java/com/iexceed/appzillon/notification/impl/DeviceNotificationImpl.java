package com.iexceed.appzillon.notification.impl;

/**
 * @author Vinod Rawat
 */

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.INotificationSender;
import com.iexceed.appzillon.notification.iface.ITypeSender;
import com.iexceed.appzillon.utils.ServerConstants;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DeviceNotificationImpl implements INotificationSender {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION, DeviceNotificationImpl.class.getName());
    private ITypeSender cAndroidSender;
    private ITypeSender cIosSender;
    private ITypeSender cIosSenderH2;
    private ITypeSender cBlackberrySender;
    private ITypeSender cWebSender;
    private ITypeSender cHauweiSender;

    public void notificationAppDetail(Message pMessage) {
        LOG.debug("{} Routing to Domain startup to get Details of App selected for Notification",
                ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_PUSH_NOTIFICATION);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getNotificationDetails(Message pMessage) {
        LOG.debug("{} Routing to Domain startup to get Device and Group Details Selected for Notification",
                ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_PUSH_NOTIFICATION);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void writeNotificationLogs(Message pMessage) {
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_PUSH_NOTIFICATION);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getGroupDetails(Message pMessage) {
        LOG.debug("{} Routing to Domain startup to get Group Details", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_PUSH_NOTIFICATION);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void sendNotificationtoAll(Message pMessage) {
        Map<String, String> androidsstatus = null;
        Map<String, String> iosstatus = null;
        Map<String, String> bbstatus = null;
        Map<String, String> webstatus = null;
        Map<String, String> hauweiStatus = null;
        Map<String, String> statusAll = new HashMap<>();
        Map<String, String> androidDeviceIds;
        Map<String, String> iosDeviceIds;
        Map<String, String> bbDeviceIds;
        Map<String, String> webDeviceIds;
        Map<String, String> hauweiDeviceIds;
        JSONObject mRequest;
        JSONObject mResponse;
        JSONObject notificationJSON = new JSONObject();
        String imageURL = null;
        String subtitle = null;
        String category = null;

        JSONObject mBody;
        try {
            getNotificationDetails(pMessage);
            mBody = pMessage.getRequestObject().getRequestJson();
            mRequest = mBody.getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ);
            String mMessageData = mRequest.getString(ServerConstants.NOTIFICATION);
            notificationJSON.put(ServerConstants.NOTIFICATION_MESSAGEDATA, mMessageData);
            String setContentAvailable = mRequest.getString(ServerConstants.IOS_CONTENT_AVAILABLE);
            notificationJSON.put(ServerConstants.IOS_CONTENT_AVAILABLE, setContentAvailable);
            String title = mRequest.getString(ServerConstants.NOTIFICATION_TITLE);
            notificationJSON.put(ServerConstants.NOTIFICATION_TITLE, title);
            setEmailParams(mRequest, ServerConstants.NOTIFICATION_IMAGE_URL, notificationJSON);
            setEmailParams(mRequest, ServerConstants.NOTIFICATION_SUBTITLE, notificationJSON);
            setEmailParams(mRequest, ServerConstants.NOTIFICATION_CATEGORY, notificationJSON);
            setEmailParams(mRequest, ServerConstants.WEB_NOTIFICATION_CLICK_ACTION, notificationJSON);
            setEmailParams(mRequest, ServerConstants.WEB_NOTIFICATION_ICON, notificationJSON);
            JSONObject lparams = null;
            if (mRequest.has(ServerConstants.NOTIFICATION_PARAMETERS)) {
                lparams = mRequest.getJSONObject(ServerConstants.NOTIFICATION_PARAMETERS);
            }

            String mAppId = mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            JSONArray groupIds = mRequest.getJSONArray(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE);
            androidDeviceIds = getDeviceIds("androidDevices", mRequest);
            iosDeviceIds = getDeviceIds("iosDevices", mRequest);
            bbDeviceIds = getDeviceIds("bbDevices", mRequest);
            webDeviceIds = getDeviceIds("webDevices", mRequest);
            hauweiDeviceIds = getDeviceIds("hauweiDevices", mRequest);

            androidsstatus = getAndroidsstatus(androidsstatus, androidDeviceIds, notificationJSON, lparams, mAppId);
            hauweiStatus = getHauweiStatus(hauweiStatus, hauweiDeviceIds, notificationJSON, lparams, mAppId);
            iosstatus = getIosstatus(iosstatus, iosDeviceIds, notificationJSON, lparams, mAppId);
            bbstatus = getBbstatus(bbstatus, bbDeviceIds, mMessageData, lparams, mAppId);
            webstatus = getWebstatus(webstatus, webDeviceIds, notificationJSON, lparams, mAppId);

            JSONObject entity;
            JSONObject response;
            JSONArray arr = new JSONArray();
            Timestamp t = new Timestamp(new Date().getTime());
            updateStatus(androidsstatus, iosstatus, bbstatus, webstatus, hauweiStatus, statusAll);

            int successCount = 0;
            int failureCount = 0;
            boolean writeLog = false;
            if (statusAll != null) {
                Iterator<String> it = statusAll.keySet().iterator();
                while (it.hasNext()) {

                    String deviceId = it.next();
                    String notifRegId = null;
                    notifRegId = getNotifRegId(androidDeviceIds, iosDeviceIds, bbDeviceIds, hauweiDeviceIds, deviceId, notifRegId);
                    entity = new JSONObject();
                    entity.put(ServerConstants.MESSAGE_HEADER_APP_ID, mAppId);
                    entity.put(ServerConstants.NOTIFICATION_REGISTRATION_ID, notifRegId);
                    entity.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, deviceId);
                    entity.put(ServerConstants.NOTIFICATION, mMessageData);
                    entity.put(ServerConstants.NOTIFICATION_TITLE, title);
                    entity.put(ServerConstants.NOTIFICATION_IMAGE_URL, imageURL);
                    entity.put(ServerConstants.NOTIFICATION_SUBTITLE, subtitle);
                    entity.put(ServerConstants.NOTIFICATION_CATEGORY, category);
                    entity.put(ServerConstants.TIME, t);
                    entity.put(ServerConstants.MESSAGE_HEADER_STATUS, statusAll.get(deviceId));
                    if (ServerConstants.SUCCESS.equalsIgnoreCase(statusAll.get(deviceId)))
                        successCount++;
                    else
                        failureCount++;

                    arr.put(entity);
                    writeLog = true;
                }

            }
            pMessage.getHeader().setNotifSuccessCount(pMessage.getHeader().getNotifSuccessCount() + successCount);
            pMessage.getHeader().setNotifFailureCount(pMessage.getHeader().getNotifFailureCount() + failureCount);

            pMessage.getRequestObject().getRequestJson().put(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE, groupIds);
            if (arr.length() > 0)
                pMessage.getRequestObject().getRequestJson().put(ServerConstants.APPZILLON_ROOT_NF_TXNARRAY, arr);

            response = new JSONObject();
            writeLogs(pMessage, response, writeLog);

            response.put(ServerConstants.SUCCESS, pMessage.getHeader().getNotifSuccessCount());
            response.put(ServerConstants.FAILURE, pMessage.getHeader().getNotifFailureCount());
            mResponse = new JSONObject();
            mResponse.put(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP, response);
            pMessage.getResponseObject().setResponseJson(mResponse);

        } catch (JSONException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "", e);
        }

    }

    private String getNotifRegId(Map<String, String> androidDeviceIds, Map<String, String> iosDeviceIds, Map<String, String> bbDeviceIds, Map<String, String> hauweiDeviceIds, String deviceId, String notifRegId) {
        if (notifRegId == null)
            notifRegId = androidDeviceIds.get(deviceId);
        if (notifRegId == null)
            notifRegId = hauweiDeviceIds.get(deviceId);
        if (notifRegId == null)
            notifRegId = iosDeviceIds.get(deviceId);
        if (notifRegId == null)
            notifRegId = bbDeviceIds.get(deviceId);
        if (notifRegId == null)
            notifRegId = deviceId;
        return notifRegId;
    }

    private Map<String, String> getWebstatus(Map<String, String> webstatus, Map<String, String> webDeviceIds, JSONObject notificationJSON, JSONObject lparams, String mAppId) {
        if (webDeviceIds != null && !webDeviceIds.isEmpty()) {
            LOG.debug("{} Routing to Web Notification Sender", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            webstatus = cWebSender.sendNotification(webDeviceIds, notificationJSON, mAppId, lparams);
        }
        return webstatus;
    }

    private Map<String, String> getBbstatus(Map<String, String> bbstatus, Map<String, String> bbDeviceIds, String mMessageData, JSONObject lparams, String mAppId) {
        if (bbDeviceIds != null && !bbDeviceIds.isEmpty()) {
            LOG.debug("{} Routing to BlackBerry Notification Sender", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            bbstatus = cBlackberrySender.sendNotification(bbDeviceIds, mMessageData, mAppId, lparams);
        }
        return bbstatus;
    }

    private Map<String, String> getIosstatus(Map<String, String> iosstatus, Map<String, String> iosDeviceIds, JSONObject notificationJSON, JSONObject lparams, String mAppId) {
        if (iosDeviceIds != null && !iosDeviceIds.isEmpty()) {
            LOG.debug("{} Routing to IOS Notification Sender using h2",
                    ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            iosstatus = cIosSenderH2.sendNotification(iosDeviceIds, notificationJSON, mAppId, lparams);
        }
        return iosstatus;
    }

    private Map<String, String> getHauweiStatus(Map<String, String> hauweiStatus, Map<String, String> hauweiDeviceIds, JSONObject notificationJSON, JSONObject lparams, String mAppId) {
        if (hauweiDeviceIds != null && !hauweiDeviceIds.isEmpty()) {
            LOG.debug("{} Routing to HAUWEI Notification Sender", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            hauweiStatus = cHauweiSender.sendNotification(hauweiDeviceIds, notificationJSON, mAppId, lparams);
        }
        return hauweiStatus;
    }

    private Map<String, String> getAndroidsstatus(Map<String, String> androidsstatus, Map<String, String> androidDeviceIds, JSONObject notificationJSON, JSONObject lparams, String mAppId) {
        if (androidDeviceIds != null && !androidDeviceIds.isEmpty()) {
            LOG.debug("{} Routing to Android Notification Sender", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            androidsstatus = cAndroidSender.sendNotification(androidDeviceIds, notificationJSON, mAppId, lparams);
        }
        return androidsstatus;
    }

    private void writeLogs(Message pMessage, JSONObject response, boolean writeLog) {
        if (writeLog) {
            this.writeNotificationLogs(pMessage);
            LOG.debug("{} Transaction table populated successfully", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            String nfTxnNo = pMessage.getResponseObject().getResponseJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP)
                    .getString(ServerConstants.REFNO);
            response.put(ServerConstants.REFNO, nfTxnNo);
        }
    }

    private void updateStatus(Map<String, String> androidsstatus, Map<String, String> iosstatus, Map<String, String> bbstatus, Map<String, String> webstatus, Map<String, String> hauweiStatus, Map<String, String> statusAll) {
        if (androidsstatus != null)
            statusAll.putAll(androidsstatus);
        if (bbstatus != null)
            statusAll.putAll(bbstatus);
        if (iosstatus != null)
            statusAll.putAll(iosstatus);
        if (webstatus != null)
            statusAll.putAll(webstatus);
        if (hauweiStatus != null)
            statusAll.putAll(hauweiStatus);
    }

    private Map<String, String> getDeviceIds(String key, JSONObject mRequest) {
        Map<String, String> deviceIds = null;
        if (mRequest.has(key))
            deviceIds = JSONUtils.getJsonHashMap(mRequest.get(key).toString());
        return deviceIds;
    }

    private String setEmailParams(JSONObject mRequest, String key, JSONObject notificationJSON) {
        String value = null;
        if (mRequest.has(key)) {
            value = mRequest.getString(key);
            notificationJSON.put(key, value);
        }
        return value;
    }

    public ITypeSender getCAndroidSender() {
        return cAndroidSender;
    }

    public void setCAndroidSender(ITypeSender cAndroidSender) {
        this.cAndroidSender = cAndroidSender;
    }

    public ITypeSender getCIosSender() {
        return cIosSender;
    }

    public void setCIosSender(ITypeSender cIosSender) {
        this.cIosSender = cIosSender;
    }

    public ITypeSender getCIosSenderH2() {
        return cIosSenderH2;
    }

    public void setCIosSenderH2(ITypeSender cIosSenderH2) {
        this.cIosSenderH2 = cIosSenderH2;
    }

    public ITypeSender getCBlackberrySender() {
        return cBlackberrySender;
    }

    public void setCBlackberrySender(ITypeSender cBlackberrySender) {
        this.cBlackberrySender = cBlackberrySender;
    }

    public ITypeSender getCWebSender() {
        return cWebSender;
    }

    public void setCWebSender(ITypeSender cWebSender) {
        this.cWebSender = cWebSender;
    }

    public ITypeSender getcHauweiSender() {
        return cHauweiSender;
    }

    public void setcHauweiSender(ITypeSender cHauweiSender) {
        this.cHauweiSender = cHauweiSender;
    }

}
