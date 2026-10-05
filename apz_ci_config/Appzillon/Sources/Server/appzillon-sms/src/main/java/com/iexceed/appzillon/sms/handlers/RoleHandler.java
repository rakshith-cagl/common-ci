package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.IRoleProfile;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Vinod Rawat
 */
public class RoleHandler implements IHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getSmsLogger(ServerConstants.LOGGER_SMS,
                    RoleHandler.class.getName());
    private IRoleProfile cRoleprofile;

    public IRoleProfile getCRoleprofile() {
        return cRoleprofile;
    }

    public void setCRoleprofile(IRoleProfile cRoleprofile) {
        this.cRoleprofile = cRoleprofile;
    }

    @Override
    public void handleRequest(Message pMessage) {

        String pRequestIntfID = pMessage.getHeader().getInterfaceId();
        LOG.info("{} Routing to Role Master Implementation class", ServerConstants.LOGGER_PREFIX_SMS);
        if (ServerConstants.INTERFACE_ID_CREATE_ROLE_MASTER.equalsIgnoreCase(pRequestIntfID)) {
            cRoleprofile.create(pMessage);

        } else if (ServerConstants.INTERFACE_ID_UPDATE_ROLE_MASTER.equalsIgnoreCase(pRequestIntfID)) {
            cRoleprofile.update(pMessage);

        } else if (ServerConstants.INTERFACE_ID_DELETE_ROLE_MASTER.equalsIgnoreCase(pRequestIntfID)) {
            cRoleprofile.delete(pMessage);

        } else if (ServerConstants.INTERFACE_ID_GET_ROLE_MASTER.equalsIgnoreCase(pRequestIntfID)) {
            cRoleprofile.search(pMessage);

        } else if (ServerConstants.INTERFACE_ID_GET_SCREENS_INTF_APPID.equalsIgnoreCase(pRequestIntfID)) {
            cRoleprofile.getScreensIntfByAppID(pMessage);

        } else if (ServerConstants.INTERFACE_ID_GET_SCREENS_INTF_APPID_ROLEID.equalsIgnoreCase(pRequestIntfID)) {
            cRoleprofile.getIntfScrByAppIDRoleID(pMessage);
        }

    }

}
