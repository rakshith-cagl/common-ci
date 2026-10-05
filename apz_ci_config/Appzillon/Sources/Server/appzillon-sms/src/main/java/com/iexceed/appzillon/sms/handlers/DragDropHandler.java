/**
 *
 */
package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IDragDrop;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 *
 */
public class DragDropHandler implements IHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, DragDropHandler.class.getName());
    private IDragDrop cDragDrop;


    public IDragDrop getcDragDrop() {
        return cDragDrop;
    }

    public void setcDragDrop(IDragDrop cDragDrop) {
        this.cDragDrop = cDragDrop;
    }

    @Override
    public void handleRequest(Message pMessage) {
        String mRequesttype = pMessage.getHeader().getInterfaceId();
        LOG.debug("{} Interface id : {}", ServerConstants.LOGGER_PREFIX_SMS, mRequesttype);
        LOG.info("{} Routing to DragDropImpl", ServerConstants.LOGGER_PREFIX_SMS);
        if (ServerConstants.INTERFACE_ID_DRAG_DROP_INSERT.equals(mRequesttype)) {
            cDragDrop.insert(pMessage);
        } else if (ServerConstants.INTERFACE_ID_DRAG_DROP_DELETE.equals(mRequesttype)) {
            cDragDrop.delete(pMessage);
        } else if (ServerConstants.INTERFACE_ID_DRAG_DROP_SEARCH.equals(mRequesttype)) {
            cDragDrop.search(pMessage);
        } else if (ServerConstants.INTERFACE_ID_DRAG_DROP_UPDATE.equals(mRequesttype)) {
            cDragDrop.update(pMessage);
        }

    }

}
