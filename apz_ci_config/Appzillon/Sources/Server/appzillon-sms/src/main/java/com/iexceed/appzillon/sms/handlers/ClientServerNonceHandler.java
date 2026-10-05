package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.domain.handler.IHandler;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IClientServerNonce;
import com.iexceed.appzillon.utils.ServerConstants;

public class ClientServerNonceHandler implements IHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
            ClientServerNonceHandler.class.getName());

    private IClientServerNonce cClientServerNonce;

    public IClientServerNonce getcClientServerNonce() {
        return cClientServerNonce;
    }

    public void setcClientServerNonce(IClientServerNonce cClientServerNonce) {
        this.cClientServerNonce = cClientServerNonce;
    }

    @Override
    public void handleRequest(Message pMessage) {
        String mRequesttype = pMessage.getHeader().getInterfaceId();
        LOG.info("{} Routing to Appzillon Nonce Impl", ServerConstants.LOGGER_PREFIX_SMS);
        if (mRequesttype.equals(ServerConstants.INTERFACE_ID_GET_APP_SEC_TOKENS)) {
            cClientServerNonce.generateNonce(pMessage);
        } else if (mRequesttype.equals(ServerConstants.INTERFACE_ID_LOGOUT)
                && ServerConstants.SERVICE_PURGE_NONCE.equals(pMessage.getHeader().getServiceType())) {
            cClientServerNonce.purgeNonce(pMessage);
        } else if (ServerConstants.CLEAR_SERVER_NONCES.equals(pMessage.getHeader().getServiceType())) {
            cClientServerNonce.clearExpiredRecords(pMessage);
        } else {
            cClientServerNonce.clientNonceVerification(pMessage);
        }
    }
}
