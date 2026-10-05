package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.ILogging;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 * This Class written for Handling with logging Errors
 */
public class LoggingHandler implements IHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, LoggingHandler.class.getName());
    private ILogging cLogging;

    public ILogging getcLogging() {
        return cLogging;
    }

    public void setcLogging(ILogging cLogging) {
        this.cLogging = cLogging;
    }

    @Override
    public void handleRequest(Message pMessage) {
        String lRequestIntfID = pMessage.getHeader().getInterfaceId();
        LOG.info("Routing to AppzillonLoggingImpl");
        if (ServerConstants.INTERFACE_ID_ERROR_LOGGING.equals(lRequestIntfID)) {
            cLogging.loggingRequest(pMessage);
        } else if (ServerConstants.INTERACE_ID_RELOAD_LOGGER.equals(lRequestIntfID)) {
            cLogging.reloadLoggerConfig(pMessage);
        }
    }

}
