package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.IUserProfile;
import com.iexceed.appzillon.utils.ServerConstants;

public class UserProfileHandler implements IHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getSmsLogger(ServerConstants.LOGGER_SMS,
                    UserProfileHandler.class.getName());
    private IUserProfile cUserprofile;

    public IUserProfile getCUserprofile() {
        return cUserprofile;
    }

    public void setCUserprofile(IUserProfile cUserprofile) {
        this.cUserprofile = cUserprofile;
    }

    @Override
    public void handleRequest(Message pMessage) {

        LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Message Details :: "
                + pMessage);

        String mRequesttype = pMessage.getHeader().getInterfaceId();

        LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Request Type :: "
                + mRequesttype);
        LOG.info(ServerConstants.LOGGER_PREFIX_SMS
                + "Routing to User Profile impl");

        switch (mRequesttype) {
            case ServerConstants.INTERFACE_ID_CREATE_USER:
                cUserprofile.createUserRequest(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_USER_REGISTER:
                cUserprofile.userRegisterRequest(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_USER_AUTHORIZATION:
                cUserprofile.authenticateUser(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_UPDATE_USER:
                cUserprofile.updateUser(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_DELETE_USER:
                cUserprofile.userDelete(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_SEARCH_USER:
                cUserprofile.searchUser(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_GET_ROLES_APPID_USERID:
                cUserprofile.getRolesByAppIDUserID(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_GET_ROLES_APPID:
                cUserprofile.getRolesByAppID(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_PIN_RESET:
                cUserprofile.passwordReset(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_FORGOT_PIN:
                cUserprofile.forgotPassword(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_UNLOCK_USER:
                cUserprofile.unlockUser(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_CREATE_PIN_RULES, ServerConstants.INTERFACE_ID_GET_PIN_RULES:
                cUserprofile.createPasswordRules(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_UPDATE_PIN_RULES:
                cUserprofile.updatePasswordRules(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_DELETE_PIN_RULES:
                cUserprofile.deletePasswordRules(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_DEVICE_STATUS_REQ:
                cUserprofile.checkDeviceStatus(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_GET_USER:
                cUserprofile.getUser(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_DASHBOARD:
                cUserprofile.getDashBoardDetails(pMessage);
                break;
            case ServerConstants.INTERFACE_ID_SAVE_APPACCESS:
                cUserprofile.saveOrUpdateUserAppAccess(pMessage);
                break;
            default:
                LOG.info("Do nothing");
        }
    }
}
