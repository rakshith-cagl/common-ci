package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiAppMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiDeviceMasterRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiAppIdVersionRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiAppOsVersionRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Named(ServerConstants.SERVICE_APP_MASTER)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class AppMasterService {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            AppMasterService.class.getName());

    @Inject
    private TbAsmiAppMasterRepository appMasterRepo;
    @Inject
    private TbAsmiAppOsVersionRepository appOsVersionRepo;
    @Inject
    private TbAsmiDeviceMasterRepository deviceMasterRepo;
    @Inject
    private TbAsmiAppIdVersionRepository appIdVersionRepo;

    public void getAppMasterDetails(Message pMessage) {
        LOG.debug("{} inside getAppMasterDetails", ServerConstants.LOGGER_PREFIX_DOMAIN);
        if (!pMessage.getHeader().getDeviceId().equals(ServerConstants.WEB)) {
            fetchDeviceMaster(pMessage);
        }
    }

    private void fetchDeviceMaster(Message pMessage) {
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        JSONObject request = requestJson.getJSONObject(ServerConstants.APPZILLON_APP_MASTER_REQUEST);
        String lAppId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String lOs = request.getString(ServerConstants.OS);
        String lDeviceId = request.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
        String appOsVersionId = "";
        if (request.has(ServerConstants.APP_VERSION)) {
            appOsVersionId = request.getString(ServerConstants.APP_VERSION);
        }
        if (request.has(ServerConstants.UPDATE_APP_VERSION)
                && Utils.isNotNullOrEmpty(request.getString(ServerConstants.UPDATE_APP_VERSION))
                && ServerConstants.YES.equals(request.getString(ServerConstants.UPDATE_APP_VERSION))) {
            updateAppVersion(lAppId, lDeviceId, appOsVersionId);
        }

        TbAsmiAppMaster appMaster = appMasterRepo.findAppMasterByAppId(lAppId);

        // Max Appversion from App id version table
        String appVersionId = appIdVersionRepo.findMaxAppIdVersionByAppIdAndOS(lAppId, lOs);

        // Update action from app os version table for given version.

        if (appMaster != null && Utils.isNotNullOrEmpty(appVersionId)) {
            buildAppMasterResp(pMessage, appMaster, appOsVersionId, appVersionId);
        } else {
            LOG.error("{} No Record found for appId : {} and os {} in table", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    lAppId, lOs);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            throw dexp;
        }

    }

    private void updateAppVersion(String lAppId, String lDeviceId, String appOsVersionId) {
        LOG.debug("{} Updating app version in device master table", ServerConstants.LOGGER_PREFIX_DOMAIN);
        Optional<TbAsmiDeviceMaster> deviceMasterRec = deviceMasterRepo.findById(new TbAsmiDeviceMasterPK(lAppId, lDeviceId));
        if (deviceMasterRec.isPresent()) {
            deviceMasterRec.get().setAppVersion(appOsVersionId);
            deviceMasterRepo.save(deviceMasterRec.get());
        }

    }

    private void buildAppMasterResp(Message pMessage, TbAsmiAppMaster appMaster, String appOsVersionId,
                                    String appVersionId) {
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        JSONObject request = requestJson.getJSONObject(ServerConstants.APPZILLON_APP_MASTER_REQUEST);
        JSONObject parentJson = new JSONObject();
        String updateAction = "";
        String lAppId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String lOs = request.getString(ServerConstants.OS);
        String lDeviceId = request.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
        Optional<TbAsmiAppOsVersion> asmiAppOsVersion = appOsVersionRepo
                .findById(new TbAsmiAppOsVersionPK(lAppId, lOs, appOsVersionId));
        if (asmiAppOsVersion.isPresent()) {
            updateAction = asmiAppOsVersion.get().getAppAction();
        }

        TbAsmiDeviceMaster deviceMaster = getDeviceMaster(lAppId, lDeviceId);
        if (deviceMaster == null) {
            LOG.error("{} No Record found in TbAsmiDeviceMaster table for this AppId : {} and DeviceId : ",
                    ServerConstants.LOGGER_PREFIX_DOMAIN, lAppId, lDeviceId);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            throw dexp;
        } else {
            parentJson.put(ServerConstants.WIPE_OUT, deviceMaster.getWipedOut());
        }

        Timestamp currentTime = new Timestamp(new Date().getTime());
        String expiryTimeFromDb = appMaster.getExpiryDate().toString();
        try {
            String currentTm = currentTime.toString();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long diff = formatter.parse(expiryTimeFromDb).getTime() - formatter.parse(currentTm).getTime();
            LOG.debug("{} Time Difference between current time and expiry time : {}",
                    ServerConstants.LOGGER_PREFIX_DOMAIN, diff);
            parentJson.put(ServerConstants.EXPIRED, (diff < 0) ? ServerConstants.YES : ServerConstants.NO);
        } catch (Exception e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.EXCEPTION, e);
        }
        parentJson.put(ServerConstants.MESSAGE_HEADER_APP_ID, appMaster.getTbAsmiAppMasterPK().getAppId());
        parentJson.put(ServerConstants.APPVERSION, appVersionId);
        parentJson.put(ServerConstants.UPDATE_ACTION, updateAction);
        parentJson.put(ServerConstants.CONTAINER_APP, appMaster.getContainerApp());
        parentJson.put(ServerConstants.OTA_REQUIRED, appMaster.getOtaReq());
        parentJson.put(ServerConstants.REMOTE_DEBUG, appMaster.getRemoteDebug());
        parentJson.put(ServerConstants.EXPIRY_DATE, expiryTimeFromDb.substring(0, 10));
        parentJson.put(ServerConstants.PARENT_APPID, appMaster.getParentAppId());

        pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.APP_INSTRUCTION, parentJson));

    }

    @Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
    public TbAsmiDeviceMaster getDeviceMaster(String pAppId, String pDeviceId) {
        LOG.debug("{} inside getDeviceMaster(), Device Master Details Will be fetched from Admin Meta.",
                ServerConstants.LOGGER_PREFIX_DOMAIN);
        TbAsmiDeviceMasterPK id = new TbAsmiDeviceMasterPK(pAppId, pDeviceId);
        Optional<TbAsmiDeviceMaster> deviceMaster = deviceMasterRepo.findById(id);
        if (deviceMaster.isPresent()) {
            return deviceMaster.get();
        }
        return null;
    }

}
