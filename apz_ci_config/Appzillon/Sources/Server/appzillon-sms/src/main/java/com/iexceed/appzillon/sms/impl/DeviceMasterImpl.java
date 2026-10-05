/**
 *
 */
package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.TbAsmiUserPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.sms.iface.IDeviceMasterHandler;
import com.iexceed.appzillon.sms.utils.HashXor;
import com.iexceed.appzillon.utils.ServerConstants;

import javax.inject.Inject;
import java.util.Optional;

/**
 * @author ripu This class is written for handling all the operation for
 *         DeviceMaster for Multifactor
 */
public class DeviceMasterImpl implements IDeviceMasterHandler {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
            DeviceMasterImpl.class.getName());
    @Inject
    private TbAsmiUserRepository userRepo;

    @Override
    public void searchDeviceMaster(Message pMessage) {
        LOG.debug("{} inside searchDeviceMaster()..", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DEVICE_MASTER);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void createDeviceMaster(Message pMessage) {
        LOG.debug("{} inside createDeviceMaster()..", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DEVICE_MASTER);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void updateDeviceMaster(Message pMessage) {
        LOG.debug("{} inside updateDeviceMaster()..", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DEVICE_MASTER);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void deleteDeviceMaster(Message pMessage) {
        LOG.debug("{} inside deleteDeviceMaster()..", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DEVICE_MASTER);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    /* Registering User device method added */
    @Override
    public void registerUserDevice(Message pMessage) {
        LOG.debug("{} inside registerUserDevice()..", ServerConstants.LOGGER_PREFIX_SMS);
        // Validating User Credentials
        boolean flag = false;
        LOG.debug("{} Security Parameter ServerToken : {}", ServerConstants.LOGGER_PREFIX_SMS,
                pMessage.getSecurityParams().getServerToken());
        flag = validateUserCredential(pMessage, pMessage.getSecurityParams().getServerToken());
        if (flag) {
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DEVICE_MASTER);
            DomainStartup.getInstance().processRequest(pMessage);
        } else {
            LOG.error("{} UserId/Password Incorrect for otp authentication", ServerConstants.LOGGER_PREFIX_SMS);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage("UserId/Password incorrect for otp authentication");
            lDomainException.setCode(DomainException.Code.APZ_DM_046.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }

    /* Validating User Details for UserDeviceRegistration */
    private boolean validateUserCredential(Message pMessage, String serverToken) {
        LOG.info("{} Validating the user credential", ServerConstants.LOGGER_PREFIX_SMS);
        JSONObject lRequestJson = pMessage.getRequestObject().getRequestJson();
        lRequestJson = lRequestJson.getJSONObject(ServerConstants.USER_DEVICE_REGISTER_REQUEST);
        String lHashvalue = "";
        LOG.debug("{} Request Body : {}", ServerConstants.LOGGER_PREFIX_SMS, lRequestJson.toString());
        String userId = lRequestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
        String appId = lRequestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String encryptedPwd = HashUtils.hashSHA256(lRequestJson.getString(ServerConstants.PIN), userId + serverToken);
        TbAsmiUserPK lAsmiUserId = new TbAsmiUserPK(userId, appId);
        Optional<TbAsmiUser> lAsmiUserDet = userRepo.findById(lAsmiUserId);
        if (lAsmiUserDet.isPresent()) {
            LOG.info("{} User Exist In Database", ServerConstants.LOGGER_PREFIX_SMS);
            String authenticationType = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(),
                    ServerConstants.AUTHENTICATION_TYPE);
            if (authenticationType == null || authenticationType.isEmpty()) {
                authenticationType = ServerConstants.HASH_DEVICE_ID;
            }
            LOG.info("{} Authentication Type : {}", ServerConstants.LOGGER_PREFIX_SMS, authenticationType);
            if (authenticationType.equalsIgnoreCase(ServerConstants.HASH_DEVICE_ID)) {
                lHashvalue = new HashXor().hashValue(lRequestJson.getString(ServerConstants.HASHKEY1),
                        lRequestJson.getString(ServerConstants.HASHKEY2), "",
                        lRequestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID), lAsmiUserDet.get().getPin(),
                        lRequestJson.getString(ServerConstants.SYSDATE));
                LOG.info("{} Request PIN is  : {}, Generated PIN is  : {}", ServerConstants.LOGGER_PREFIX_SMS,
                        lRequestJson.getString(ServerConstants.PIN), lHashvalue);
                return lRequestJson.getString(ServerConstants.PIN).equals(lHashvalue.replaceAll("[\n\r]", ""));
            } else {
                LOG.debug("{} Verifying for Plain Text for authentication", ServerConstants.LOGGER_PREFIX_SMS);
                return lAsmiUserDet.get().getPin().equals(encryptedPwd);
            }
        } else {
            LOG.error("{} User Does not Exist In Database", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_001));
            lDomainException.setCode(DomainException.Code.APZ_DM_001.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }
}
