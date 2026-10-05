/**
 *
 */
package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IDragDrop;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 *
 */
public class DragDropImpl implements IDragDrop {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
            ServerConstants.LOGGER_SMS, DragDropImpl.class.getName());

    @Override
    public void insert(Message pMessage) {
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DRAG_DROP);
        LOG.info("{} Routing to Domain to insert the Record.", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void delete(Message pMessage) {
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DRAG_DROP);
        LOG.info("{} Routing to Domain to Delete the Record.", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void search(Message pMessage) {
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DRAG_DROP);
        LOG.info("{} Routing to Domain to fetch the Record.", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void update(Message pMessage) {
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DRAG_DROP);
        LOG.info("{} Routing to Domain to update the existing Record.", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

}
