package com.iexceed.appzillon.impl;

import com.iexceed.appzillon.iface.ISendSMS;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

public class TextMessagingServicesImpl implements ISendSMS {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    TextMessagingServicesImpl.class.getName());

    @Override
    public String sendSMS(String mobileNumber, String message) {

        LOG.info("{} Text Messaging services implementation has to be provided by the user....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        JSONObject json = new JSONObject();
        try {
            json.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
        } catch (JSONException e) {
            LOG.error("JSONException : ", e);
        }
        return json.toString();
    }

    @Override
    public String sendSMS(String mobileNumber, String message, String portNumber) {
        LOG.info("{} Text Messaging services implementation has to be provided by the user....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        return "{'todo':'Text Messaging services implementation has to be provided by the user'}";
    }

}
