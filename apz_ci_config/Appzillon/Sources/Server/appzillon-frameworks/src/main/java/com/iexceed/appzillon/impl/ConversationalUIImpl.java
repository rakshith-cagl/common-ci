package com.iexceed.appzillon.impl;


import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.services.ConversationalUIService;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;

public class ConversationalUIImpl extends ExternalServicesRouter {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_FRAMEWORKS,
            ConversationalUIImpl.class.getName());

    public void serviceRequestDispatcher(Message pMessage,
                                         SpringCamelContext context) throws ExternalServicesRouterException,
            InvalidPayloadException, ClassNotFoundException, JSONException {
        String lInterfaceId = pMessage.getHeader().getInterfaceId();
        LOG.debug("{} Routing to ConversationalUIService", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        if (ServerConstants.INTERFACE_ID_GET_FIRST_CNVUI_DLG.equals(lInterfaceId)) {
            ConversationalUIService cnvUIService = new ConversationalUIService();
            cnvUIService.getFirstCnvUIDlg(pMessage);
        } else if (ServerConstants.INTERFACE_ID_GET_CNVUI_DLG.equals(lInterfaceId)) {
            ConversationalUIService cnvUIService = new ConversationalUIService();
            cnvUIService.getNextCnvUIDlg(pMessage);
        }
    }
}
