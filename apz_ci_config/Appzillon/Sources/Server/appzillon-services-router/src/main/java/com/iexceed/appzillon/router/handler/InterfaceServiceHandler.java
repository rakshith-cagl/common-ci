package com.iexceed.appzillon.router.handler;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.InterfaceDetails;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.router.exception.RouterException;
import com.iexceed.appzillon.utils.ServerConstants;

public class InterfaceServiceHandler implements IRequestHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, InterfaceServiceHandler.class.getName());

    @Override
    public void handleRequest(Message pMessage) throws RouterException {
        LOG.info("{} Routing To Domain Processing... Inside handle Request", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
        Header lHeader = pMessage.getHeader();
        InterfaceDetails lInterfaceDetails = pMessage.getIntfDtls();
        String interfaceID = lInterfaceDetails.getInterfaceId();
        LOG.debug("{} Interface Id is : {}", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER, interfaceID);
        if (ServerConstants.INTERFACE_ID_JMSRESFETCHREQ.equals(interfaceID)) {
            lHeader.setServiceType(ServerConstants.INTERFACE_ID_JMSRESFETCHREQ);
            pMessage.setHeader(lHeader);
            DomainStartup.getInstance().processRequest(pMessage);
        } else {
            lHeader.setServiceType(ServerConstants.SERVICE_INTERFACE_MAINTENANCE);
            pMessage.setHeader(lHeader);
            DomainStartup.getInstance().processRequest(pMessage);
        }
    }
}
