package com.iexceed.appzillon.impl;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.services.CacheService;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;

public class CacheServicesImpl extends ExternalServicesRouter {

    public void serviceRequestDispatcher(Message pMessage,
                                         SpringCamelContext context) throws ExternalServicesRouterException,
            InvalidPayloadException, ClassNotFoundException,
            JSONException {
        CacheService cacheservices = new CacheService();
        cacheservices.callExternalService(pMessage);

    }

}
