package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.ITrackLocation;
import com.iexceed.appzillon.utils.ServerConstants;

public class TrackLocationImpl implements ITrackLocation {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
            TrackLocationImpl.class.getName());

    @Override
    public void saveOrUpdateLocationDetails(Message pMessage) {
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TRACK_LOCATION);
        LOG.info("{} Routing to Domain StartUp", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }
}
