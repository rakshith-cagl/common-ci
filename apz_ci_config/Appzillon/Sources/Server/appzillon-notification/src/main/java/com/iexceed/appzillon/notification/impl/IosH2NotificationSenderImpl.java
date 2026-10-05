package com.iexceed.appzillon.notification.impl;

import com.eatthepath.pushy.apns.proxy.HttpProxyHandlerFactory;
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.notification.iface.ITypeSender;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IosH2NotificationSenderImpl implements ITypeSender {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION, IosH2NotificationSenderImpl.class.getName());
    private static final ExecutorService threadpool = Executors.newFixedThreadPool(20);

    public IosH2NotificationSenderImpl() {
        // default constructor
    }

    public Map<String, String> sendNotification(Map<String, String> devicesDetails, JSONObject notificationJSON,
                                                String pAppId, JSONObject pParams) {
        Map<String, String> newHash = new HashMap<>();
        Map<String, String> iOSNotifResponseMap = new HashMap<>();
        String password = PropertyUtils.getPropValue(pAppId, ServerConstants.IOS_P12_PIN).trim();
        String path = PropertyUtils.getPropValue(pAppId, ServerConstants.IOS_P12_PATH).trim();
        List<String> devices = new ArrayList<>(devicesDetails.values());

        HttpProxyHandlerFactory proxyHandlerFactory = null;
        String proxyHost = PropertyUtils.getPropValue(pAppId, ServerConstants.PROXY_HOST).trim();
        String proxyPort = PropertyUtils.getPropValue(pAppId, ServerConstants.PROXY_PORT).trim();
        String proxyUser = PropertyUtils.getPropValue(pAppId, ServerConstants.PROXY_USER).trim();
        String proxyPassword = PropertyUtils.getPropValue(pAppId, ServerConstants.PROXY_PIN).trim();
        String topic = PropertyUtils.getPropValue(pAppId, ServerConstants.IOS_BUNDLE_ID).trim();
        if (Utils.isNotNullOrEmpty(proxyHost) && Utils.isNotNullOrEmpty(proxyPort)) {
            LOG.debug("{} Setting Proxy Details", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            proxyHandlerFactory = new HttpProxyHandlerFactory(
                    new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)), proxyUser, proxyPassword);
        }
        LOG.debug("{} Getting Custom Payload", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        final ApnsPayloadBuilder payloadBuilder = getCustomPayLoad(notificationJSON, pParams);
        LOG.debug("{} Total number of devices : {}", ServerConstants.LOGGER_PREFIX_NOTIFICAITON, devices.size());
        final String payload = payloadBuilder.build();
        List<IosPushNotification> callable = new ArrayList<>();
        for (String device : devices) {
            String token = TokenUtil.sanitizeTokenString(device);
            String collapseId = Utils.generateRandomOfLength(7, ServerConstants.OTP_ALPHA_NUMERIC);
            Instant instant = null;
            String invalidationTimeStr = PropertyUtils.getPropValue(pAppId, ServerConstants.IOS_NOTIF_INVALIDATION_TIME)
                    .trim();
            if (!invalidationTimeStr.isEmpty()) {
                int invalidationTime = Integer.parseInt(invalidationTimeStr);
                TemporalAmount temporalAmount = Duration.ofHours(invalidationTime);
                instant = (Instant.now()).plus(temporalAmount);
                LOG.debug("{} notification invalidation time for ios ", instant.toString());
            }
            SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, topic, payload, instant,
                    null, collapseId);
            IosPushNotification task = new IosPushNotification(path, password, proxyHandlerFactory, device,
                    pushNotification, pAppId);
            callable.add(task);
        }
        try {
            List<Future<Map<String, String>>> future = threadpool.invokeAll(callable);
            for (Future<Map<String, String>> f : future) {
                iOSNotifResponseMap.putAll(f.get());
            }
        } catch (InterruptedException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "InterruptedException", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "ExecutionException", e);
        }

        Iterator<String> it = devicesDetails.keySet().iterator();
        while (it.hasNext()) {
            String deviceId = it.next();
            String regId = devicesDetails.get(deviceId);
            if (iOSNotifResponseMap.containsKey(regId)) {
                newHash.put(deviceId, iOSNotifResponseMap.get(regId));
            } else {
                newHash.put(deviceId, "Unknown");
            }

        }
        return newHash;
    }

    /**
     * @param notificationJSON
     * @param pCustomPayload
     * @return
     */
    private ApnsPayloadBuilder getCustomPayLoad(JSONObject notificationJSON, JSONObject pCustomPayload) {
        ApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();
        LOG.debug("{} Building Custom Payload", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        payloadBuilder.setAlertTitle(notificationJSON.getString(ServerConstants.NOTIFICATION_TITLE));
        if (notificationJSON.has(ServerConstants.NOTIFICATION_SUBTITLE)) {
            payloadBuilder.setAlertSubtitle(notificationJSON.getString(ServerConstants.NOTIFICATION_SUBTITLE));
        }
        payloadBuilder.setAlertBody(notificationJSON.getString(ServerConstants.NOTIFICATION_MESSAGEDATA));
        if (notificationJSON.has(ServerConstants.NOTIFICATION_CATEGORY)) {
            payloadBuilder.setCategoryName(notificationJSON.getString(ServerConstants.NOTIFICATION_CATEGORY));
        }
        if (notificationJSON.has(ServerConstants.NOTIFICATION_IMAGE_URL)) {
            payloadBuilder.addCustomProperty("image_url",
                    notificationJSON.getString(ServerConstants.NOTIFICATION_IMAGE_URL));
        }
        if (pCustomPayload != null) {
            Map<String, String> parametersMap = JSONUtils.getJsonHashMap(pCustomPayload.toString());
            LOG.debug("{} After converting json to map  : {}", ServerConstants.LOGGER_PREFIX_NOTIFICAITON,
                    parametersMap);
            Iterator<Entry<String, String>> parameters = parametersMap.entrySet().iterator();
            while (parameters.hasNext()) {
                Map.Entry<String, String> parameter = parameters.next();
                payloadBuilder.addCustomProperty(parameter.getKey(), parameter.getValue());
            }
        }
        payloadBuilder.setSound("default");
        // Condition to check redirection of app is required.
        if (notificationJSON.getBoolean(ServerConstants.IOS_CONTENT_AVAILABLE)) {
            payloadBuilder.setContentAvailable(notificationJSON.getBoolean(ServerConstants.IOS_CONTENT_AVAILABLE));
        }
        LOG.debug("{} Custom Payload : {}", ServerConstants.LOGGER_PREFIX_NOTIFICAITON, payloadBuilder.toString());
        return payloadBuilder;
    }

    @Override
    public Map<String, String> sendNotification(Map<String, String> deviceDetails, String messageData, String appId,
                                                JSONObject pParams) {
        return null;
    }
}
