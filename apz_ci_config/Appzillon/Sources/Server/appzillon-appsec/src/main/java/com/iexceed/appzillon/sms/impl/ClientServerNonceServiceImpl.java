package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.service.ClientServerNonceService;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IClientServerNonce;
import com.iexceed.appzillon.utils.ServerConstants;

public class ClientServerNonceServiceImpl implements IClientServerNonce {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_PREFIX_NONCE, ClientServerNonceServiceImpl.class.getName());
    private static final String CLIENT_SERVER_NONCE_SERVICE = "ClientServerNonceServcie";

    @Override
    public void generateNonce(Message pMessage) {
        LOG.debug("{} Going to generate Nonce", ServerConstants.LOGGER_PREFIX_NONCE);
        ((ClientServerNonceService) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
                .getBean(CLIENT_SERVER_NONCE_SERVICE)).generateNonce(pMessage);
    }

    @Override
    public void clientNonceVerification(Message pMessage) {
        LOG.debug("{} Going to validate nonce", ServerConstants.LOGGER_PREFIX_NONCE);
        ((ClientServerNonceService) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
                .getBean(CLIENT_SERVER_NONCE_SERVICE)).validateClientServerNonce(pMessage);
    }

    @Override
    public void purgeNonce(Message pMessage) {
        ((ClientServerNonceService) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
                .getBean(CLIENT_SERVER_NONCE_SERVICE)).purgeNonce(pMessage);
    }

    @Override
    public void clearExpiredRecords(Message pMessage) {
        LOG.debug("{} Going to clear expired serverNonces", ServerConstants.LOGGER_PREFIX_NONCE);
        ((ClientServerNonceService) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
                .getBean(CLIENT_SERVER_NONCE_SERVICE)).clearExpiredRecords(pMessage);
    }

}
