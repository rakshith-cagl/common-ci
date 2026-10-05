package com.iexceed.appzillon.sms.citi.handlers;

import com.iexceed.appzillon.domain.handler.IHandler;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.citi.iface.ICitiService;
import com.iexceed.appzillon.utils.ServerConstants;

public class CitiServiceHandler implements IHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
            CitiServiceHandler.class.toString());
    private ICitiService citiService;


    public ICitiService getCitiService() {
        return citiService;
    }


    public void setCitiService(ICitiService citiService) {
        this.citiService = citiService;
    }


    @Override
    public void handleRequest(Message pMessage) {

        String pMessageRequestType = pMessage.getHeader().getInterfaceId();
        if (pMessageRequestType.equals(ServerConstants.INTERFACE_ID_PARSE_META_JSON)) {
            LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Appzillon Citi Impl");
            citiService.generateAppzillonJson(pMessage);
        } else if (pMessageRequestType.equals(ServerConstants.INTERFACE_ID_INSERT_HTML_JSON)) {
            citiService.persistAppScreenJson(pMessage);
        } else if (pMessageRequestType.equals(ServerConstants.INTERFACE_ID_PARSE_PRODUCT_JSON)) {
            citiService.parseProductJson(pMessage);
        } else if (pMessageRequestType.equals(ServerConstants.INTERFACE_ID_PARSE_WIDGET_JSON)) {
            citiService.parseWidgetJson(pMessage);
        }

    }


}