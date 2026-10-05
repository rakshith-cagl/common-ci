package com.iexceed.appzillon.notification.impl;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.proxy.HttpProxyHandlerFactory;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.MDC;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.iexceed.appzillon.utils.ServerConstants.*;

public class IosPushNotification implements Callable<Map<String, String>> {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION, IosPushNotification.class.getName());
    static Map<String, String> iOSNotifRespFutureMap = new HashMap<>();
    private SimpleApnsPushNotification pushNotification;
    private String device;
    private String appId;
    private String path;
    private String password;
    private HttpProxyHandlerFactory proxyHandlerFactory;

    public IosPushNotification(String path, String password, HttpProxyHandlerFactory proxyHandlerFactory, String device, SimpleApnsPushNotification pushNotification, String pAppId) {
        this.path = path;
        this.password = password;
        this.proxyHandlerFactory = proxyHandlerFactory;
        this.device = device;
        this.pushNotification = pushNotification;
        this.appId = pAppId;
    }

    public Map<String, String> call() throws InterruptedException {
        MDC.put("logRouter", appId + "/Notification");
        MDC.put(APPID, APPID_VALUE);
        MDC.put(OSTYPE, OSTYPE_VALUE);
        MDC.put(TXNREF, TXNREF_VALUE);
        MDC.put(USERID, USERID_VALUE);
        ApnsClient apnsClient = null;
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
        try {
            apnsClient = new ApnsClientBuilder().setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST)
                    .setClientCredentials(new File(path), password).setConcurrentConnections(1)
                    .setEventLoopGroup(eventLoopGroup).setProxyHandlerFactory(proxyHandlerFactory).build();
            LOG.debug("{} Sending Notifications", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> sendNotificationFuture = apnsClient
                    .sendNotification(pushNotification);
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse = sendNotificationFuture
                    .get();
            if (pushNotificationResponse.isAccepted()) {
                LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Push notification accepted by APNs gateway.");
                iOSNotifRespFutureMap.put(device, "success");
            } else {
                LOG.info("{} Notification rejected by the APNs gateway for this token : {}",
                        ServerConstants.LOGGER_PREFIX_NOTIFICAITON, pushNotificationResponse.getRejectionReason());
                if (pushNotificationResponse.getTokenInvalidationTimestamp().isPresent()) {
                    LOG.debug("{} token {} is invalid as of {}", ServerConstants.LOGGER_PREFIX_NOTIFICAITON, device,
                            pushNotificationResponse.getTokenInvalidationTimestamp());
                    pushNotificationResponse.getPushNotification().getToken();
                    pushNotificationResponse.getTokenInvalidationTimestamp();
                    iOSNotifRespFutureMap.put(device, "Uninstall");
                } else {
                    LOG.debug("{} Invalid Registration.", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
                    iOSNotifRespFutureMap.put(device, "Invalid");
                }
            }
        } catch (ExecutionException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "ExecutionException", e);

        } catch (SSLException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "SSL Exception Occured", e);
        } catch (IOException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "IO Exception Occured", e);
        } catch (InterruptedException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "InterruptedException", e);
            Thread.currentThread().interrupt();
        } finally {
            if (apnsClient != null) {
                LOG.debug("Closing APNSClient");
                apnsClient.close();

            }
            LOG.debug("Closing NioEventLoopGroup");
            eventLoopGroup.shutdownGracefully();
        }
        return iOSNotifRespFutureMap;
    }

}
