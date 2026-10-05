package com.iexceed.appzillon.services;

import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.spring.SpringCamelContext;

import java.util.HashMap;
import java.util.Map;

import static com.iexceed.appzillon.utils.Constants.STATUS;
import static com.iexceed.appzillon.utils.Constants.SUCCESS;

public class CacheService implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    CacheService.class.getName());

    public void callExternalService(Message pMessage) {
        // Default constructor
    }

    @Override
    public Object buildRequest(Message pMessage, Object pRequestPayLoad,
                               SpringCamelContext pContext) {
        return pRequestPayLoad;
    }

    @Override
    public Object processResponse(Message pMessage, Object pResponse,
                                  SpringCamelContext pContext) {
        return pResponse;
    }

    @Override
    public Object callService(Message pMessage, Object pRequestPayLoad,
                              SpringCamelContext pContext) {
        JSONObject loutputString = null;

        String payLoad = (String) buildRequest(pMessage, pRequestPayLoad, pContext);
        LOG.debug("{} callExternalService.appID -: {}, interfaceID -: {}, payload : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getHeader().getAppId(), pMessage.getHeader().getInterfaceId(), payLoad);

        try {
            JSONObject lJonPayload = new JSONObject(payLoad);
            String lMethodType = lJonPayload.getString(ServerConstants.METHOD_TYPE);
            if ("PUT".equalsIgnoreCase(lMethodType) || "DELETE".equalsIgnoreCase(lMethodType)
                    || "FLUSH".equalsIgnoreCase(lMethodType)) {
                lJonPayload.remove(ServerConstants.METHOD_TYPE);
                loutputString = new JSONObject().put(STATUS, SUCCESS);
            } else if ("GET".equalsIgnoreCase(lMethodType)) {
                lJonPayload.remove(ServerConstants.METHOD_TYPE);
                Map<String, String> responseMap = new HashMap<>();
                loutputString = JSONUtils.getJsonStringFromMap(responseMap);
            } else {
                LOG.warn("Exception {} not Supported", lMethodType);
            }
            LOG.debug("{} json payload after removing methodType : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lJonPayload);

            loutputString = (JSONObject) processResponse(pMessage, loutputString, pContext);


        } catch (JSONException e) {
            LOG.error(ServerConstants.JSON_EXCEPTION, e);
        }
        return new JSONObject(loutputString);

    }

}
