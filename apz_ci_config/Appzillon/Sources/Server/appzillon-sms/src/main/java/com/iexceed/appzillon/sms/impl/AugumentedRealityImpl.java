package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAuguementedReality;
import com.iexceed.appzillon.utils.ServerConstants;

public class AugumentedRealityImpl implements IAuguementedReality {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, AugumentedRealityImpl.class.getName());

    @Override
    public void fetchAugumentedRealityDetails(Message pMessage) {
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_FETCH_AUGUMENTED_REALITY);
        LOG.info("{} Routing to Domain StartUp to Fetch AugmentedReality details", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);

    }

}
