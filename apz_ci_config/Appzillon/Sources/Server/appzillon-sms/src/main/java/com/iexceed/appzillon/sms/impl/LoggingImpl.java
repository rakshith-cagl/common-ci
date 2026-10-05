package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.ILogging;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 * This Class written for Handling with logging Errors
 */
public class LoggingImpl implements ILogging {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
            ServerConstants.LOGGER_SMS, LoggingImpl.class.getName());

    @Override
    public void loggingRequest(Message pMessage) {
        LOG.debug("{} loggingRequest", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ERROR_LOGGING);
        LOG.info("{} Routing to Domain Create logging Request..", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void reloadLoggerConfig(Message pMessage) {
        LOG.debug("{} reloadLoggerRequest", ServerConstants.LOGGER_PREFIX_SMS);
        LoggerFactory.getLoggerFactory().reloadLogger(pMessage);
        JSONObject reloadLoggerResponse = new JSONObject();
        reloadLoggerResponse.put("appzillonReloadLoggerResponse", new JSONObject().put("Loggers", pMessage.getResponseObject().getResponseJson()));
        pMessage.getResponseObject().setResponseJson(reloadLoggerResponse);
        LOG.debug("{} re-loaded external Logger file and response from reloadLoggerConfig {}", ServerConstants.LOGGER_PREFIX_SMS, reloadLoggerResponse.toString());

    }

}
