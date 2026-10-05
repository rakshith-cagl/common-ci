package com.iexceed.appzillon.services;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

public class SessionStorageService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    SessionStorageService.class.getName());

    public void saveOrUpdateSessionStorage(Message pMessage, JSONArray jsonArray) {
        LOG.debug("{} inside saveOrUpdateSessionStorage()", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_SAVE_OR_UPDATE_SESSION_STORAGE);
        pMessage.getRequestObject().getRequestJson().put(ServerConstants.REQUEST_DATA, jsonArray);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public JSONArray getSessionStorage(Message pMessage, JSONArray sessionKeys) {
        LOG.debug("{} inside getUserJsonData()", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GET_USER_SESSION_STORAGE);
        pMessage.getRequestObject().getRequestJson().put(ServerConstants.USER_DATA_LOG_KEY, sessionKeys);
        DomainStartup.getInstance().processRequest(pMessage);
        return pMessage.getResponseObject().getResponseJson().getJSONArray(ServerConstants.SESSION_VALUES_ARRAY);
    }

    public JSONArray getValueFromSessionStorage(Message pMessage, JSONArray sessionKeys) {
        LOG.debug("{} get session value from DB with out Exception", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GET_USER_SESSION_STORAGE_ARRAY);
        pMessage.getRequestObject().getRequestJson().put(ServerConstants.USER_DATA_LOG_KEY, sessionKeys);
        DomainStartup.getInstance().processRequest(pMessage);
        return pMessage.getResponseObject().getResponseJson().getJSONArray(ServerConstants.SESSION_VALUES_ARRAY);
    }

}
