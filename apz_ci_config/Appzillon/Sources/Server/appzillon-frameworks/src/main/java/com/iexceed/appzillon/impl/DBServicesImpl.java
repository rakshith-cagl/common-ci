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

/**
 * @author arthanarisamy
 * Created on 09/01/2015
 * Appzillon 3.1 - 61
 * <p>
 * Class acts as an handler for the Database services.
 * Injects the respective service class from camel-context beans and process the database service requests.
 */
public class DBServicesImpl extends ExternalServicesRouter {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    DBServicesImpl.class.getName());

    public void serviceRequestDispatcher(Message pMessage,
                                         SpringCamelContext pContext) throws ExternalServicesRouterException {

        LOG.info("{} DBService Implementation Dispatching request to Service Bean to process the request....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String lInterfaceId = pMessage.getHeader().getInterfaceId();
        String lAppId = pMessage.getHeader().getAppId();
        String lCamelID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;
        LOG.debug("{} DBService Application Id -: {}, InterfaceId -: {}, Service BeanId -: {}"
                , ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lAppId, lInterfaceId, lCamelID);
        IServicesBean dbService = (IServicesBean) pContext
                .getApplicationContext().getBean(lCamelID);
        LOG.debug("{} DBService Bean is injected -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, dbService);
        JSONObject lResponse = (JSONObject) dbService.callService(pMessage,
                pMessage.getRequestObject().getRequestJson(), pContext);
        pMessage.getResponseObject().setResponseJson(lResponse);
    }
}
