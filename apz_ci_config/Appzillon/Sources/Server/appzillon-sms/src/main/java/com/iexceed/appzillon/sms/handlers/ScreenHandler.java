package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.IScreenMaintainer;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Vinod Rawat
 */
public class ScreenHandler implements IHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
            ServerConstants.LOGGER_SMS,
            ScreenHandler.class.getName());

    private IScreenMaintainer cScreen;

    @Override
    public void handleRequest(Message pMessage) {

        String pRequestIntfID = pMessage.getHeader().getInterfaceId();
        LOG.info("{} Routing to Screen Implementation class", ServerConstants.LOGGER_PREFIX_SMS);
        if (ServerConstants.INTERFACE_ID_SEARCH_SCREEN.equals(pRequestIntfID)) {
            cScreen.search(pMessage);

        } else if (ServerConstants.INTERFACE_ID_CREATE_SCREEN.equals(pRequestIntfID)) {
            cScreen.create(pMessage);

        } else if (ServerConstants.INTERFACE_ID_UPDATE_SCREEN.equals(pRequestIntfID)) {
            cScreen.update(pMessage);

        } else if (ServerConstants.INTERFACE_ID_DELETE_SCREEN.equals(pRequestIntfID)) {
            cScreen.delete(pMessage);
        }

    }

    public IScreenMaintainer getCScreen() {
        return cScreen;
    }

    public void setCScreen(IScreenMaintainer cScreen) {
        this.cScreen = cScreen;
    }

}
