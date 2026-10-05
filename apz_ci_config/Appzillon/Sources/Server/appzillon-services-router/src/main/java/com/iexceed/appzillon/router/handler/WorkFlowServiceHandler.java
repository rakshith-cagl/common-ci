/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.router.handler;

/**
 * @author Administrator
 */

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

public class WorkFlowServiceHandler implements IRequestHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES,
                    WorkFlowServiceHandler.class.getName());

    @Override
    public void handleRequest(Message pMessage) {

        LOG.info("{} Routing To Domain Processing", ServerConstants.LOGGER_PREFIX_RESTFULL);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_WORKFLOW);
        DomainStartup.getInstance().processRequest(pMessage);
        pMessage.getHeader().setServiceType("");

    }
}
