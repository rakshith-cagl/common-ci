package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IOta;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author ripu
 * This class is written for handling all the operation for OTA
 */
public class OTAImpl implements IOta {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
            ServerConstants.LOGGER_SMS, OTAImpl.class.getName());


    public void getAppFileDetails(Message pMessage) {
        LOG.debug("{} inside getAppFileDetails()..", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
        LOG.info("{} Routing to Domain getLatestRequestDetails GENERATE LATEST SOURCE DETAILS SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void getAppMasterDetail(Message pMessage) {
        LOG.debug("{} getAppMasterDetail", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
        LOG.info("{} Routing to Domain to getAppMaster Detail.", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void otaDownloadFile(Message pMessage) {
        LOG.debug("{} inside otaDownloadFile()..", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
        LOG.info("{} Routing to Domain to do OTA file download", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void create(Message pMessage) {
        LOG.debug("{} inside create().", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
        LOG.info("{} Routing to Domain to createAppMaster.", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void update(Message pMessage) {
        LOG.debug("{} inside update().", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
        LOG.info("{} Routing to Domain to update.", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void search(Message pMessage) {
        LOG.debug("{} inside search().", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
        LOG.info("{} Routing to Domain to search.", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void delete(Message pMessage) {
        LOG.debug("{} inside delete().", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
        LOG.info("{} Routing to Domain to delete.", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);

    }

    @Override
    public void getChildAppDetails(Message pMessage) {
        LOG.debug("{} inside getChildAppDetails()..", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
        LOG.info("{} Routing to Domain to get child app details", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);

    }

    public void getCnvUIWelcomeMsg(Message pMessage) {
        LOG.debug("{} inside getCNVUIWelcomeMsg()..");
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
        LOG.info("{} Routing to Domain to get Welcome Message", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

}



