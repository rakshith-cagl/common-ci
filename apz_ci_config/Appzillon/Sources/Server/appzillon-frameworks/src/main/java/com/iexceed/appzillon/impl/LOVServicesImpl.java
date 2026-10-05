package com.iexceed.appzillon.impl;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.spring.SpringCamelContext;

public class LOVServicesImpl extends ExternalServicesRouter {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    LOVServicesImpl.class.getName());

    public void serviceRequestDispatcher(Message pMessage,
                                         SpringCamelContext context) throws ExternalServicesRouterException {
        LOG.info("{} LOVServices Implementation Dispatching request to Service Bean to process the request....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String lInterfaceId = pMessage.getHeader().getInterfaceId();
        String lAppId = pMessage.getHeader().getAppId();
        String lCamelID = ServerConstants.SERVICES_BEAN_LOV;
        LOG.debug("{} LOVServices Application Id -: {}, InterfaceId -: {}, Service BeanId -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lAppId, lInterfaceId, lCamelID);

        IServicesBean lovServices = (IServicesBean) context
                .getApplicationContext().getBean(lCamelID);
        LOG.debug("{} LOVServices Bean is injected -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lovServices);
        JSONObject lResponse = (JSONObject) lovServices.callService(pMessage,
                pMessage.getRequestObject().getRequestJson(), context);
        LOG.debug("{} LOVServices Response received from the service is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lResponse.toString());
        pMessage.getResponseObject().setResponseJson(lResponse);


    }

}
