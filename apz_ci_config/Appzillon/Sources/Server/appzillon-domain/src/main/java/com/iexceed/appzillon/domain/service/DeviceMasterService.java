/**
 *
 */
package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiDeviceMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserDevicesRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfDevicesMasterRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.iexceed.appzillon.domain.utils.Constants.DELETE_DEVICE_MASTER_REQUEST;
import static com.iexceed.appzillon.domain.utils.Constants.DOUBLE_BRACES;

/**
 * @author Ripu This class is written for handling all the operation for
 *         DeviceMaster
 */
@Named(ServerConstants.SERVICE_DEVICE_MASTER)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class DeviceMasterService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            DeviceMasterService.class.getName());
    @Inject
    private TbAsmiDeviceMasterRepository deviceMasterRepo;
    @Inject
    private TbAsmiUserDevicesRepository userDevicesRepo;
    @Inject
    private TbAsmiUserRepository userRepo;
    @Inject
    private TbAsnfDevicesMasterRepository asnfDeviceMasterRepo;

    public void registerDevice(Message pMessage) {
        LOG.debug("{} inside registerDevice()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject response = new JSONObject();
        JSONObject location = pMessage.getHeader().getLocation();
        String longitude = "";
        String latitude = "";
        if (location != null) {
            longitude = location.has(ServerConstants.LONGITUDE) ? location.getString(ServerConstants.LONGITUDE) : "";
            latitude = location.has(ServerConstants.LATITUDE) ? location.getString(ServerConstants.LATITUDE) : "";
        }
        try {
            JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
            JSONObject request = requestJson.getJSONObject(ServerConstants.DEVICE_REGISTER_REQUEST);
            TbAsmiDeviceMasterPK id = new TbAsmiDeviceMasterPK(request.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                    request.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));

            TbAsmiDeviceMaster deviceMaster;
            Optional<TbAsmiDeviceMaster> deviceMasterOpt = deviceMasterRepo.findById(id);
            if (deviceMasterOpt.isPresent()) {
                deviceMaster = deviceMasterOpt.get();
            } else {
                deviceMaster = new TbAsmiDeviceMaster();
                deviceMaster.setId(id);
            }
            deviceMaster.setOs(request.getString(ServerConstants.OS));
            deviceMaster.setOsVersion(request.getString(ServerConstants.NOTIFICATION_OS_VERSION));
            deviceMaster.setMobileNumber1(request.getString(ServerConstants.MOBILE_1));
            deviceMaster.setMobileNumber2(request.getString(ServerConstants.MOBILE_2));
            deviceMaster.setModel(request.getString(ServerConstants.MODEL));
            deviceMaster.setMake(request.getString(ServerConstants.MAKE));
            deviceMaster.setScreenResolution(request.getString(ServerConstants.SCREEN_RESOLUTION));
            deviceMaster.setWipedOut("N");
            deviceMaster.setCreateTs(new Date());
            deviceMaster.setDeviceName(request.getString(ServerConstants.NOTIFICATION_DEVICE_NAME));
            deviceMaster.setLongitude(longitude);
            deviceMaster.setLatitude(latitude);
            deviceMaster.setAppVersion(request.getString(ServerConstants.APP_VERSION));
            deviceMasterRepo.save(deviceMaster);
            response.put(ServerConstants.MESSAGE_HEADER_STATUS, true);
            pMessage.getResponseObject()
                    .setResponseJson(new JSONObject().put(ServerConstants.DEVICE_REGISTER_RESPONSE, response));
        } catch (JSONException jsonExp) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, jsonExp);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");

            throw dexp;
        }
    }

    /**
     * Below method written by ripu to update device master
     *
     * @param pMessage
     */
    public void updateDeviceMaster(Message pMessage) {
        LOG.debug("{} inside updateDeviceMaster()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject response = new JSONObject();
        JSONObject location = pMessage.getHeader().getLocation();
        String longitude = "";
        String latitude = "";
        if (location != null) {
            longitude = location.has(ServerConstants.LONGITUDE) ? location.getString(ServerConstants.LONGITUDE) : "";
            latitude = location.has(ServerConstants.LATITUDE) ? location.getString(ServerConstants.LATITUDE) : "";
        }
        try {
            JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
            JSONObject request = requestJson.getJSONObject("updateDeviceMasterRequest");

            TbAsmiDeviceMasterPK id = new TbAsmiDeviceMasterPK(request.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                    request.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));

            if (deviceMasterRepo.existsById(id)) {
                TbAsmiDeviceMaster deviceMaster = new TbAsmiDeviceMaster();
                deviceMaster.setId(id);
                deviceMaster.setOs(request.getString(ServerConstants.OS));
                deviceMaster.setOsVersion(request.getString(ServerConstants.NOTIFICATION_OS_VERSION));
                deviceMaster.setMobileNumber1(request.getString(ServerConstants.MOBILE_1));
                deviceMaster.setMobileNumber2(request.getString(ServerConstants.MOBILE_2));
                deviceMaster.setModel(request.getString(ServerConstants.MODEL));
                deviceMaster.setMake(request.getString(ServerConstants.MAKE));
                deviceMaster.setScreenResolution(request.getString(ServerConstants.SCREEN_RESOLUTION));
                deviceMaster.setWipedOut(request.getString(ServerConstants.WIPE_OUT));
                deviceMaster.setCreateTs(new Date());
                deviceMaster.setDeviceName(request.getString(ServerConstants.NOTIFICATION_DEVICE_NAME));
                deviceMaster.setLongitude(longitude);
                deviceMaster.setLatitude(latitude);
                deviceMasterRepo.save(deviceMaster);

                response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
                pMessage.getResponseObject()
                        .setResponseJson(new JSONObject().put("updateDeviceMasterResponse", response));
            } else {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                throw dexp;
            }
        } catch (JSONException jsonExp) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, jsonExp);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");

            throw dexp;
        }
    }

    /**
     * Below method written by ripu to Delete device master
     *
     * @param pMessage
     */
    public void deleteDeviceMaster(Message pMessage) {
        JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
        LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside deleteDeviceMaster() - Request :: " + mRequest);
        JSONObject jsonRequest = null;
        try {
            if (mRequest.get(DELETE_DEVICE_MASTER_REQUEST) instanceof JSONArray) {
                JSONArray jsonArray = mRequest.getJSONArray(DELETE_DEVICE_MASTER_REQUEST);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonRequest = jsonArray.getJSONObject(i);
                    deleteDeviceMasterRequest(jsonRequest);
                }
            } else if (mRequest.get(DELETE_DEVICE_MASTER_REQUEST) instanceof JSONObject) {
                jsonRequest = mRequest.getJSONObject(DELETE_DEVICE_MASTER_REQUEST);
                deleteDeviceMasterRequest(jsonRequest);
            }
            JSONObject response = new JSONObject();
            response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            pMessage.getResponseObject().setResponseJson(new JSONObject().put("deleteDeviceMasterResponse", response));
        } catch (JSONException json) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    public void deleteDeviceMasterRequest(JSONObject pRequest) {
        LOG.debug("{} inside deleteDeviceMasterRequest()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        try {
            TbAsmiDeviceMasterPK id = new TbAsmiDeviceMasterPK(
                    pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                    pRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
            if (deviceMasterRepo.existsById(id)) {
                deviceMasterRepo.deleteById(id);
            } else {
                LOG.error("{} No Record Found.", ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                throw dexp;
            }
        } catch (JSONException jsonExp) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, jsonExp);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    /**
     * Below method written by ripu to search device master
     *
     * @param pMessage
     */
    public void searchDeviceMaster(Message pMessage) {
        LOG.debug("{} inside searchDeviceMaster()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        try {
            JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
            JSONObject request = requestJson.getJSONObject("appzillonSearchDeviceMasterRequest");
            String lAppId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            String lDeviceId = request.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
            String lOs = request.getString(ServerConstants.OS);
            List<TbAsmiDeviceMaster> deviceMasterList = null;
            deviceMasterList = deviceMasters(lAppId, lDeviceId, lOs, deviceMasterList);

            if (deviceMasterList != null && !deviceMasterList.isEmpty()) {
                JSONArray jarray = new JSONArray();
                for (TbAsmiDeviceMaster deviceMaster : deviceMasterList) {
                    JSONObject json = new JSONObject();
                    json.put(ServerConstants.MESSAGE_HEADER_APP_ID, deviceMaster.getId().getAppId());
                    json.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, deviceMaster.getId().getDeviceId());
                    json.put(ServerConstants.OS, deviceMaster.getOs());
                    json.put(ServerConstants.NOTIFICATION_OS_VERSION, deviceMaster.getOsVersion());
                    json.put(ServerConstants.MOBILE_1, deviceMaster.getMobileNumber1());
                    json.put(ServerConstants.MOBILE_2, deviceMaster.getMobileNumber2());
                    json.put(ServerConstants.MODEL, deviceMaster.getModel());
                    json.put(ServerConstants.MAKE, deviceMaster.getMake());
                    json.put(ServerConstants.SCREEN_RESOLUTION, deviceMaster.getScreenResolution());
                    json.put(ServerConstants.WIPE_OUT, deviceMaster.getWipedOut());
                    jarray.put(json);
                }
                pMessage.getResponseObject()
                        .setResponseJson(new JSONObject().put("appzillonSearchDeviceMasterResponse", jarray));
            } else {
                LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Record Found.");
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                throw dexp;
            }
        } catch (JSONException jsonExp) {
            LOG.error(ServerConstants.JSON_EXCEPTION, jsonExp);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");

            throw dexp;
        }
    }

    private List<TbAsmiDeviceMaster> deviceMasters(String lAppId, String lDeviceId, String lOs, List<TbAsmiDeviceMaster> deviceMasterList) {
        if (Utils.isNotNullOrEmpty(lOs) && Utils.isNullOrEmpty(lAppId) && Utils.isNullOrEmpty(lDeviceId)) {
            deviceMasterList = getDeviceMasterList("ALL".equalsIgnoreCase(lOs),
                    deviceMasterRepo.findAll(), deviceMasterRepo.findDeviceMasterByOS(lOs));
        } else if (Utils.isNotNullOrEmpty(lAppId) && Utils.isNullOrEmpty(lDeviceId)
                && Utils.isNotNullOrEmpty(lOs)) {
            deviceMasterList = getDeviceMasterList("ALL".equalsIgnoreCase(lOs),
                    deviceMasterRepo.findDeviceMasterByAppID(lAppId), deviceMasterRepo.findDeviceMasterByAppIdAndOs(lAppId, lOs));
        } else if (Utils.isNullOrEmpty(lAppId) && Utils.isNotNullOrEmpty(lDeviceId)
                && Utils.isNotNullOrEmpty(lOs)) {
            deviceMasterList = getDeviceMasterList("ALL".equalsIgnoreCase(lOs),
                    deviceMasterRepo.findDeviceMasterByDeviceID(lDeviceId), deviceMasterRepo.findDeviceMasterByDeviceIdAndOs(lDeviceId, lOs));
        } else if (Utils.isNotNullOrEmpty(lAppId) && Utils.isNotNullOrEmpty(lDeviceId)
                && Utils.isNotNullOrEmpty(lOs)) {
            deviceMasterList = getDeviceMasterList("ALL".equalsIgnoreCase(lOs),
                    deviceMasterRepo.findDeviceMasterByAppIdDeviceId(lAppId, lDeviceId), deviceMasterRepo.findDeviceMasterByAppIdDeviceIdAndOs(lAppId, lDeviceId, lOs));
        }
        return deviceMasterList;
    }

    private List<TbAsmiDeviceMaster> getDeviceMasterList(boolean expression, List<TbAsmiDeviceMaster> result1, List<TbAsmiDeviceMaster> result2) {
        return expression ? result1
                : result2;
    }

    private void updateUserDevice(Message pMessage, TbAsmiDeviceMaster pAsmiDeviceMaster) {
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        JSONObject pRequest = requestJson.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
        LOG.debug("{} inside updateUserDevice()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        try {
            TbAsmiUserPK userPk = new TbAsmiUserPK(pRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                    pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            Optional<TbAsmiUser> userEntity = userRepo.findById(userPk);
            if (userEntity.isPresent() && ServerConstants.YES.equalsIgnoreCase(userEntity.get().getUserActive())) {
                TbAsmiUserDevicesPK id = new TbAsmiUserDevicesPK(pAsmiDeviceMaster.getId().getDeviceId(),
                        pRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                        pAsmiDeviceMaster.getId().getAppId());
                if (userDevicesRepo.existsById(id)) {
                    LOG.debug("{} Device already there in TB_ASMI_USER_DEVICES table. So Deleting existing",
                            ServerConstants.LOGGER_PREFIX_DOMAIN);
                    userDevicesRepo.deleteById(id);
                }

                // Added on 31/5/2019 to store the notiregId in TB_ASMI_USER_DEVICES
                TbAsmiUserDevices entity = new TbAsmiUserDevices();
                String notifId = asnfDeviceMasterRepo.findNotifIdByAppAndDeviceId(pAsmiDeviceMaster.getId().getAppId(),
                        pAsmiDeviceMaster.getId().getDeviceId());

                if (Utils.isNotNullOrEmpty(notifId)) {
                    entity.setNotifRegId(notifId);
                }
                // End

                entity.setTbAsmiUserDevicesPK(id);
                entity.setDeviceStatus(pRequest.getString(ServerConstants.MESSAGE_HEADER_STATUS));
                entity.setCreateUserId(pMessage.getHeader().getUserId());
                entity.setCreateTs(new Date());
                entity.setVersionNo(1);
                userDevicesRepo.save(entity);

            } else {
                LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "User Does Not Exist !!!");
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_001));
                dexp.setCode(DomainException.Code.APZ_DM_001.toString());
                dexp.setPriority("1");
            }
        } catch (JSONException jsonExp) {
            LOG.error(ServerConstants.JSON_EXCEPTION, jsonExp);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    public void checkDevice(Message pMessage) {
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        JSONObject request = requestJson.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
        LOG.debug("{} inside checkDevice()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        TbAsmiUserDevicesPK userDevicePk = new TbAsmiUserDevicesPK(
                request.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID),
                request.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                request.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        Optional<TbAsmiUserDevices> userDeviceEntity = userDevicesRepo.findById(userDevicePk);
        if (!userDeviceEntity.isPresent() || !"ACTIVE".equalsIgnoreCase(userDeviceEntity.get().getDeviceStatus())) {
            TbAsmiDeviceMasterPK id = new TbAsmiDeviceMasterPK(request.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                    request.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
            Optional<TbAsmiDeviceMaster> deviceMaster = deviceMasterRepo.findById(id);
            if (deviceMaster.isPresent()) {
                LOG.info("{} Device is Registered.", ServerConstants.LOGGER_PREFIX_DOMAIN);
                updateUserDevice(pMessage, deviceMaster.get());
            } else {
                LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Device is not Registered !!!");
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_040));
                dexp.setCode(DomainException.Code.APZ_DM_040.toString());
                dexp.setPriority("1");

                throw dexp;
            }
        } else {
            pMessage.getResponseObject().setResponseJson(
                    new JSONObject().put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS));
        }
    }

    /**
     * Validating user device is registered or not and adding new user device
     * details into user device table for User Device Registration, based on
     * AutoApprove yes/no with ACTIVE/PENDING resp.
     */
    public void doRegisterDevice(Message pMessage) {
        LOG.debug("{} inside doRegisterDevice", ServerConstants.LOGGER_PREFIX_DOMAIN);
        String autoApprove = pMessage.getSecurityParams().getAutoApprove();
        JSONObject response = new JSONObject();
        JSONObject lRequestJson = pMessage.getRequestObject().getRequestJson();
        lRequestJson = lRequestJson.getJSONObject(ServerConstants.USER_DEVICE_REGISTER_REQUEST);
        String deviceId = lRequestJson.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
        String appId = lRequestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String userId = lRequestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
        TbAsmiDeviceMasterPK masterPK = new TbAsmiDeviceMasterPK(appId, deviceId);
        Optional<TbAsmiDeviceMaster> deviceMaster = deviceMasterRepo.findById(masterPK);
        if (deviceMaster.isPresent()) {
            TbAsmiUserDevicesPK devicesPK = new TbAsmiUserDevicesPK(deviceMaster.get().getId().getDeviceId(), userId,
                    deviceMaster.get().getId().getAppId());
            Optional<TbAsmiUserDevices> devices = userDevicesRepo.findById(devicesPK);
            if (!devices.isPresent()) {
                TbAsmiUserDevices asmiUserDevices = new TbAsmiUserDevices();
                asmiUserDevices.setTbAsmiUserDevicesPK(devicesPK);
                asmiUserDevices.setCreateTs(new Date());
                asmiUserDevices.setCreateUserId(pMessage.getHeader().getUserId());
                asmiUserDevices.setVersionNo(1);
                if (ServerConstants.YES.equalsIgnoreCase(autoApprove)) {
                    asmiUserDevices.setDeviceStatus("ACTIVE");
                    response.put(ServerConstants.MESSAGE_HEADER_STATUS,
                            ServerConstants.USER_DEVICE_REGISTERED_SUCCESSFULLY);
                } else if (ServerConstants.NO.equalsIgnoreCase(autoApprove)) {
                    asmiUserDevices.setDeviceStatus("PENDING");
                    response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.ADMIN_APPROVAL_REQUIRED);
                }
                // Added on 31/5/2019 to store the notiregId in TB_ASMI_USER_DEVICES
                String notifId = asnfDeviceMasterRepo.findNotifIdByAppAndDeviceId(deviceMaster.get().getId().getAppId(),
                        deviceMaster.get().getId().getDeviceId());
                if (Utils.isNotNullOrEmpty(notifId)) {
                    asmiUserDevices.setNotifRegId(notifId);
                }
                // End
                userDevicesRepo.save(asmiUserDevices);
                pMessage.getResponseObject()
                        .setResponseJson(new JSONObject().put(ServerConstants.USER_DEVICE_REGISTER_RESPONSE, response));
            } else {
                LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "User Device Already Registered");
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                lDomainException
                        .setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_039));
                lDomainException.setCode(DomainException.Code.APZ_DM_039.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            }
        } else {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "User Device Not Registered");
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_040));
            lDomainException.setCode(DomainException.Code.APZ_DM_040.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }
}

