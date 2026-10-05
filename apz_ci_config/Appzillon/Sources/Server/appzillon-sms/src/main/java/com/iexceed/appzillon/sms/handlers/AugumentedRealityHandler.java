package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAuguementedReality;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;

public class AugumentedRealityHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, AugumentedRealityHandler.class.getName());
    private IAuguementedReality cARDetails;

    public IAuguementedReality getcARDetails() {
        return cARDetails;
    }

    public void setcARDetails(IAuguementedReality cARDetails) {
        this.cARDetails = cARDetails;
    }

    public void handleRequest(Message pMessage) {
        LOG.info("{} Routing to Appzillon Augmented Reality Impl to Fetch AR Details....", ServerConstants.LOGGER_PREFIX_SMS);
        cARDetails.fetchAugumentedRealityDetails(pMessage);
    }
}
