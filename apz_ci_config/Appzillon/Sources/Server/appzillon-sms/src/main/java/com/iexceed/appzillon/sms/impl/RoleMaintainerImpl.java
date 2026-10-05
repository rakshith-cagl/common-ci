package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IRoleProfile;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Vinod Rawat
 */
public class RoleMaintainerImpl implements IRoleProfile {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
            ServerConstants.LOGGER_SMS, ScreenMaintainerImpl.class.getName());

    public void create(Message pMessage) {
        LOG.debug("{} Creating Role", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
        LOG.info("{} Routing to Domain Create ROLE MAINTENANCE SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void update(Message pMessage) {
        LOG.debug("{} Update Role", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
        LOG.info("{} Routing to Domain Update ROLE MAINTENANCE SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void delete(Message pMessage) {
        LOG.debug("{} Delete Role", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
        LOG.info("{} Routing to Domain Delete ROLE MAINTENANCE SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void search(Message pMessage) {
        LOG.debug("{} Search Role", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
        LOG.info("{} Routing to Domain Search ROLE MAINTENANCE SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getScreensIntfByAppID(Message pMessage) {
        LOG.debug("{} getScreensIntfByAppID", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
        LOG.info("{} Routing to Domain getScreensIntfByAppID ROLE MAINTENANCE SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getIntfScrByAppIDRoleID(Message pMessage) {
        LOG.debug("{} getIntfScrByAppIDRoleID", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
        LOG.info("{} Routing to Domain getIntfScrByAppIDRoleID ROLE MAINTENANCE SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getAllRoleMasterData(Message pMessage) {
        LOG.debug("{} getAllRoleMasterData", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
        LOG.info("{} Routing to Domain getAllRoleMasterData ROLE MAINTENANCE SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }
}
