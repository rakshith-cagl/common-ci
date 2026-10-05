package com.iexceed.appzillon.notification.handlers;

/**
 * @author Vinod Rawat
 */

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.IFileService;
import com.iexceed.appzillon.utils.ServerConstants;

public class FileRequestHandler {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
                    FileRequestHandler.class.getName());
    private IFileService cFileService;

    public IFileService getCFileService() {
        return cFileService;
    }

    public void setCFileService(IFileService cFileService) {
        this.cFileService = cFileService;
    }

    public void processRequest(Message pMessage) {

        String requesttype = pMessage.getIntfDtls().getInterfaceId();

        if (ServerConstants.INTERFACE_ID_SEARCH_FILE.equals(requesttype)) {
            LOG.debug("{} Routing to  File to Search File", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cFileService.search(pMessage);

        } else if (ServerConstants.INTERFACE_ID_UPLOAD_FILE.equals(requesttype) || ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS.equals(requesttype)
                || ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH.equals(requesttype)) {
            LOG.info("{} Routing to File to create record for uploaded file ", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cFileService.create(pMessage);
        } else if (ServerConstants.INTERFACE_ID_DELETE_FILE.equals(requesttype)) {
            LOG.info("{} Routing to File Impl to delete File", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cFileService.delete(pMessage);

        } else if (ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE.equals(requesttype) ||
                ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS.equals(requesttype) ||
                ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH.equals(requesttype)) {
            LOG.debug("[NOTIFICATIONS] Routing to File Impl to get file as Base64", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cFileService.download(pMessage);

        }

    }
}