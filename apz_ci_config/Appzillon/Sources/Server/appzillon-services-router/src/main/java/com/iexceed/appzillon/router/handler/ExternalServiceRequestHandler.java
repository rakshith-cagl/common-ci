package com.iexceed.appzillon.router.handler;

import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.router.exception.RouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.InvalidPayloadException;

public class ExternalServiceRequestHandler implements IRequestHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES,
                    ExternalServiceRequestHandler.class.getName());

    @Override
    public void handleRequest(Message pMessage) throws RouterException {
        LOG.debug("{} Routing To External Services Processing.. Inside handleRequest", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
        try {
            FrameworksStartup.getInstance().processRequest(pMessage);
        } catch (ExternalServicesRouterException externalServicesRouterException) {
            LOG.error("{} externalServicesRouterException {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, externalServicesRouterException);
            throw externalServicesRouterException;
        } catch (JSONException | ClassNotFoundException | InvalidPayloadException ex) {
            LOG.error("{} Exception: {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, ex);
            RouterException dexp = RouterException.getInstance();
            dexp.setMessage(ex.getMessage());
            dexp.setCode(RouterException.EXCEPTION_CODE.APZ_RS_003.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        LOG.debug("{} Response from Service RequestHandler : {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, pMessage);
    }

}
