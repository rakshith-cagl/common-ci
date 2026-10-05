package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfDevicesMasterRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfGroupDeviceRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfGroupMasterRepository;
import com.iexceed.appzillon.domain.spec.DeviceMasterSpecification;
import com.iexceed.appzillon.domain.spec.GroupSpecification;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.iexceed.appzillon.domain.utils.Constants.DOUBLE_BRACES;

@Named("NotificationMaintenanceService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class DeviceGroupMaintenanceService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            DeviceGroupMaintenanceService.class.getName());
    @Inject
    TbAsnfDevicesMasterRepository deviceMasterRepo;
    @Inject
    TbAsnfGroupDeviceRepository groupedDeviceRepo;
    @Inject
    TbAsnfGroupMasterRepository groupMasterRepo;

    public void createDevice(Message pMessage) {
        LOG.debug("{} Inserting Device Record", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.CREATE_DEVICE_REQUEST);
        if (Utils.isNullOrEmpty(lrequest.getString(ServerConstants.NOTIFICATION_OS_ID))
                || Utils.isNullOrEmpty(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID))
                || Utils.isNullOrEmpty(lrequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID))
                || Utils.isNullOrEmpty(lrequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID))) {

            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
            dexp.setCode(DomainException.Code.APZ_DM_009.toString());
            dexp.setPriority("1");
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN,
                    dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009), dexp);
            throw dexp;
        }
        TbAsnfDevicesMasterPK deviceMasterPk = new TbAsnfDevicesMasterPK(
                lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                lrequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID));
        TbAsnfDevicesMaster deviceMaster = null;
        Optional<TbAsnfDevicesMaster> deviceMasterOpt = deviceMasterRepo.findById(deviceMasterPk);
        if (deviceMasterOpt.isPresent()) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setCode(DomainException.Code.APZ_DM_015.toString());
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_015));
            dexp.setPriority("1");
            LOG.error("{} Device record already exists", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
            throw dexp;
        } else {
            deviceMaster = new TbAsnfDevicesMaster();
            deviceMaster.setId(deviceMasterPk);
            deviceMaster.setStatus("Y");
            if (lrequest.has(ServerConstants.NOTIFICATION_DEVICE_NAME)) {
                deviceMaster.setDeviceName(lrequest.getString(ServerConstants.NOTIFICATION_DEVICE_NAME));
            }

            if (lrequest.has(ServerConstants.NOTIFICATION_OS_VERSION)) {
                deviceMaster.setOsVersion(lrequest.getString(ServerConstants.NOTIFICATION_OS_VERSION));
            }
            if (lrequest.has(ServerConstants.NOTIF_MSG_SERVER)) {
                deviceMaster.setNotifMsgServer(lrequest.getString(ServerConstants.NOTIF_MSG_SERVER));
            }

            deviceMaster.setOsId(lrequest.getString(ServerConstants.NOTIFICATION_OS_ID).toUpperCase());
            deviceMaster.setDeviceId(lrequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
            deviceMaster.setCreatedBy(pMessage.getHeader().getUserId());
            deviceMaster.setCreatedTs(new Timestamp(System.currentTimeMillis()));
            deviceMaster.setVersionNo((long) 1);

            deviceMasterRepo.save(deviceMaster);

            JSONObject out = new JSONObject();
            out.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            JSONObject response = new JSONObject();
            response.put(ServerConstants.CREATE_DEVICE_RESPONSE, out);
            pMessage.getResponseObject().setResponseJson(response);

        }
    }

    public void deleteDevice(Message pMessage) {
        LOG.debug("{} Deleting Devices", ServerConstants.LOGGER_PREFIX_DOMAIN);

        JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.DELETE_DEVICE_REQUEST);
        Object input = lrequest.get(ServerConstants.DEVICE_ID_MULTIPLE);
        if (input instanceof JSONArray jsonArray) {
            JSONArray array = jsonArray;

            int i = 0;
            while (i < array.length()) {
                pMessage.getRequestObject().setRequestJson(array.getJSONObject(i));
                deleteDeviceByID(pMessage);
                i++;
            }
        } else if (input instanceof JSONObject jsonObject) {

            pMessage.getRequestObject().setRequestJson(jsonObject);
            deleteDeviceByID(pMessage);
        }
        JSONObject out = new JSONObject();
        out.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
        JSONObject response = new JSONObject();
        response.put(ServerConstants.DELETE_DEVICE_RESPONSE, out);
        pMessage.getResponseObject().setResponseJson(response);
    }

    private void deleteDeviceByID(Message pMessage) {
        JSONObject lrequest = pMessage.getRequestObject().getRequestJson();
        TbAsnfDevicesMasterPK deviceMasterPk = new TbAsnfDevicesMasterPK(
                lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                lrequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID));
        Optional<TbAsnfDevicesMaster> rec = deviceMasterRepo.findById(deviceMasterPk);
        if (rec.isPresent()) {
            rec.get().setStatus(ServerConstants.NO);
            deviceMasterRepo.save(rec.get());
        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
            dexp.setCode(DomainException.Code.APZ_DM_010.toString());
            dexp.setPriority("1");
            LOG.error("{} No record exists for primary key appId {} notifRegId {}",
                    ServerConstants.LOGGER_PREFIX_DOMAIN, deviceMasterPk.getAppId(), deviceMasterPk.getNotifRegId(),
                    dexp);
            throw dexp;
        }
    }

    public void updateDevice(Message pMessage) {
        LOG.debug("{} Updating Device Record ", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.UPDATE_DEVICE_REQUEST);
        TbAsnfDevicesMasterPK deviceMasterPk = new TbAsnfDevicesMasterPK(
                lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                lrequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID));
        Optional<TbAsnfDevicesMaster> deviceMaster = deviceMasterRepo.findById(deviceMasterPk);
        if (deviceMaster.isPresent()) {
            deviceMaster.get().setId(deviceMasterPk);
            deviceMaster.get().setStatus(ServerConstants.YES);
            if (lrequest.has(ServerConstants.NOTIFICATION_DEVICE_NAME)) {
                deviceMaster.get().setDeviceName(lrequest.getString(ServerConstants.NOTIFICATION_DEVICE_NAME));
            }
            if (lrequest.has(ServerConstants.NOTIFICATION_OS_VERSION)) {
                deviceMaster.get().setOsVersion(lrequest.getString(ServerConstants.NOTIFICATION_OS_VERSION));
            }
            if (lrequest.has(ServerConstants.NOTIF_MSG_SERVER)) {
                deviceMaster.get().setNotifMsgServer(lrequest.getString(ServerConstants.NOTIF_MSG_SERVER));
            }
            if (lrequest.has(ServerConstants.NOTIFICATION_OS_ID)) {
                deviceMaster.get().setOsId(lrequest.getString(ServerConstants.NOTIFICATION_OS_ID));
            }
            if (lrequest.has(ServerConstants.MESSAGE_HEADER_DEVICE_ID)) {
                deviceMaster.get().setDeviceId(lrequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
            }
            deviceMaster.get().setCreatedBy(pMessage.getHeader().getUserId());
            deviceMaster.get().setCreatedTs(new Timestamp(System.currentTimeMillis()));
            deviceMaster.get().setVersionNo(deviceMaster.get().getVersionNo() + 1);
            deviceMasterRepo.save(deviceMaster.get());
        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
            dexp.setCode(DomainException.Code.APZ_DM_010.toString());
            dexp.setPriority("1");
            LOG.error("{} No record exists for primary key", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
            throw dexp;
        }
        JSONObject out = new JSONObject();
        out.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
        JSONObject response = new JSONObject();
        response.put(ServerConstants.UPDATE_DEVICE_RESPONSE, out);
        pMessage.getResponseObject().setResponseJson(response);

    }

    public void searchDevice(Message pMessage) {
        LOG.debug("{} Searching for Devices records", ServerConstants.LOGGER_PREFIX_DOMAIN);
        String osId = "";
        String deviceName = "";
        JSONArray out = null;
        JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.SEARCH_DEVICE_REQUEST);
        if (lrequest.has(ServerConstants.NOTIFICATION_DEVICE_NAME)) {
            deviceName = lrequest.getString(ServerConstants.NOTIFICATION_DEVICE_NAME);
        }
        if (lrequest.has(ServerConstants.NOTIFICATION_OS_ID)) {
            osId = lrequest.getString(ServerConstants.NOTIFICATION_OS_ID);
        }
        if (Utils.isNullOrEmpty(osId)) {
            osId = ServerConstants.PERCENT;
        }
        if (Utils.isNullOrEmpty(deviceName)) {
            deviceName = ServerConstants.PERCENT;
        }
        final String fdeviceName = deviceName;
        final String fosId = osId;
        LOG.debug("{} Parameters to be  search with deviceName : {} and osId : {}",
                ServerConstants.LOGGER_PREFIX_DOMAIN, fdeviceName, fosId);
        List<TbAsnfDevicesMaster> deviceMasters;
        if (fosId.equalsIgnoreCase(ServerConstants.BLACKBERRY) || fosId.equals(ServerConstants.PERCENT)) {
            deviceMasters = deviceMasterRepo.findAll(Specification
                    .where(DeviceMasterSpecification.likeDeviceName(fdeviceName))
                    .and(DeviceMasterSpecification.likeOsId(fosId)).and(DeviceMasterSpecification.statusIsActive())
                    .or(DeviceMasterSpecification.deviceNameisNull()));
        } else {
            deviceMasters = deviceMasterRepo.findAll(Specification
                    .where(DeviceMasterSpecification.likeDeviceName(fdeviceName))
                    .and(DeviceMasterSpecification.likeOsId(fosId)).and(DeviceMasterSpecification.statusIsActive()));

        }
        if (deviceMasters.isEmpty()) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN,
                    dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008), dexp);
            throw dexp;
        } else {
            out = new JSONArray();
            LOG.info("{} No of Device Records found : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, deviceMasters.size());
            int i = 0;
            while (i < deviceMasters.size()) {
                JSONObject obj = new JSONObject();
                obj.put(ServerConstants.MESSAGE_HEADER_APP_ID, deviceMasters.get(i).getId().getAppId());
                obj.put(ServerConstants.NOTIFICATION_REGISTRATION_ID, deviceMasters.get(i).getId().getNotifRegId());
                obj.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, deviceMasters.get(i).getDeviceId());
                obj.put(ServerConstants.NOTIFICATION_DEVICE_NAME, deviceMasters.get(i).getDeviceName());
                obj.put(ServerConstants.NOTIFICATION_OS_ID, deviceMasters.get(i).getOsId());
                obj.put(ServerConstants.NOTIFICATION_OS_VERSION, deviceMasters.get(i).getOsVersion());
                obj.put(ServerConstants.CREATETS, deviceMasters.get(i).getCreatedTs());
                obj.put(ServerConstants.CREATEUSERID, deviceMasters.get(i).getCreatedBy());
                out.put(obj);
                i++;
            }
            JSONObject response = new JSONObject();
            response.put(ServerConstants.SEARCH_DEVICE_RESPONSE, out);
            pMessage.getResponseObject().setResponseJson(response);

        }

    }

    public void createGroup(Message pMessage) {
        LOG.debug("{} Creating Group Details", ServerConstants.LOGGER_PREFIX_DOMAIN);
        if (pMessage.getRequestObject().getRequestJson().has(ServerConstants.CREATE_GROUP_REQUEST)) {
            JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.CREATE_GROUP_REQUEST);
            if (Utils.isNullOrEmpty(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID))
                    || Utils.isNullOrEmpty(lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID))) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
                dexp.setCode(DomainException.Code.APZ_DM_009.toString());
                LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN,
                        dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009), dexp);
                dexp.setPriority("1");
                throw dexp;
            }
            TbAsnfGroupMasterPK groupMasterPk = new TbAsnfGroupMasterPK(
                    lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                    lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID));
            Optional<TbAsnfGroupMaster> groupMasterOpt = groupMasterRepo.findById(groupMasterPk);
            if (groupMasterOpt.isPresent()) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setCode(DomainException.Code.APZ_DM_015.toString());
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_015));
                dexp.setPriority("1");
                LOG.error("{} Group already exists", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            } else {
                JSONArray devicesDet = lrequest.getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE);
                LOG.debug("{} No of registration IDs to be mapped to group : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                        devicesDet.length());
                if (devicesDet.length() == 0) {
                    LOG.error("{} No Devices are mapped", ServerConstants.LOGGER_PREFIX_DOMAIN);
                    DomainException dexp = DomainException.getDomainExceptionInstance();
                    dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_037));
                    dexp.setCode(DomainException.Code.APZ_DM_037.toString());
                    dexp.setPriority("1");
                    throw dexp;
                }
                TbAsnfGroupMaster groupMaster = new TbAsnfGroupMaster();
                groupMaster.setId(groupMasterPk);
                groupMaster.setGroupDesc(lrequest.getString(ServerConstants.DESCRIPTION));
                groupMaster.setCreatedBy(pMessage.getHeader().getUserId());
                groupMaster.setCreatedTs(new Timestamp(System.currentTimeMillis()));
                groupMaster.setVersionNo((long) 1);

                List<String> notifRegIds = getNotifRegIdsFromDeviceIds(devicesDet,
                        lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
                int i = 0;
                LOG.debug("{} notifRegIds list size : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, notifRegIds.size());
                while (i < notifRegIds.size()) {

                    TbAsnfGroupDevice groupDevice = new TbAsnfGroupDevice();
                    TbAsnfGroupDevicePK groupDevicePk = new TbAsnfGroupDevicePK();
                    groupDevicePk.setAppId(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
                    groupDevicePk.setGroupId(lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID));
                    groupDevicePk.setNotifRegId(notifRegIds.get(i));
                    groupDevice.setId(groupDevicePk);
                    groupDevice.setCreatedBy(pMessage.getHeader().getUserId());
                    groupDevice.setCreatedTs(new Timestamp(System.currentTimeMillis()));
                    groupDevice.setVersionNo((long) 1);
                    groupedDeviceRepo.save(groupDevice);
                    i++;
                }

                groupMasterRepo.save(groupMaster);
                JSONObject out = new JSONObject();
                out.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
                JSONObject response = new JSONObject();
                response.put(ServerConstants.CREATE_GROUP_RESPONSE, out);
                pMessage.getResponseObject().setResponseJson(response);
                LOG.debug("{} Group created Successfully.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            }
        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_038));
            dexp.setCode(DomainException.Code.APZ_DM_038.toString());
            dexp.setPriority("1");
            LOG.error("{} Please select device to group", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
            throw dexp;
        }
    }

    public void updateGroup(Message pMessage) {
        LOG.debug("{} Updating Group Details", ServerConstants.LOGGER_PREFIX_DOMAIN);
        TbAsnfGroupMaster groupMaster = null;
        JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.UPDATE_GROUP_REQUEST);
        if (Utils.isNullOrEmpty(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID))
                || Utils.isNullOrEmpty(lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID))) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
            dexp.setCode(DomainException.Code.APZ_DM_009.toString());
            dexp.setPriority("1");
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN,
                    dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009), dexp);
            throw dexp;
        }
        TbAsnfGroupMasterPK groupMasterPk = new TbAsnfGroupMasterPK(
                lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID));
        Optional<TbAsnfGroupMaster> groupMasterOpt = groupMasterRepo.findById(groupMasterPk);
        if (groupMasterOpt.isPresent()) {

            groupMaster = groupMasterOpt.get();
            JSONArray devicesDet = lrequest.getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE);
            LOG.debug("{} Number of registration IDs to be mapped to group {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    devicesDet.length());
            groupMaster.setId(groupMasterPk);
            groupMaster.setGroupDesc(lrequest.getString(ServerConstants.DESCRIPTION));
            groupMaster.setCreatedBy(pMessage.getHeader().getUserId());
            groupMaster.setCreatedTs(new Timestamp(System.currentTimeMillis()));
            groupMaster.setVersionNo((long) 1);
            groupedDeviceRepo.deleteByAppIdAndGroupId(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                    lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID));
            List<String> notifRegIds = getNotifRegIdsFromDeviceIds(devicesDet,
                    lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            int i = 0;
            while (i < notifRegIds.size()) {
                TbAsnfGroupDevice groupDevice = new TbAsnfGroupDevice();
                TbAsnfGroupDevicePK groupDevicePk = new TbAsnfGroupDevicePK();
                groupDevicePk.setAppId(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
                groupDevicePk.setGroupId(lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID));
                groupDevicePk.setNotifRegId(notifRegIds.get(i));

                groupDevice.setId(groupDevicePk);
                groupDevice.setCreatedBy(pMessage.getHeader().getUserId());
                groupDevice.setCreatedTs(new Timestamp(System.currentTimeMillis()));
                groupDevice.setVersionNo((long) 1);
                groupedDeviceRepo.save(groupDevice);
                i++;
            }

            groupMasterRepo.save(groupMaster);
            JSONObject out = new JSONObject();
            out.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            JSONObject response = new JSONObject();
            response.put(ServerConstants.UPDATE_GROUP_RESPONSE, out);
            pMessage.getResponseObject().setResponseJson(response);
        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setCode(DomainException.Code.APZ_DM_010.toString());
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
            dexp.setPriority("1");
            LOG.error("{} Group does not exists", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
            throw dexp;
        }

    }

    public void deleteGroup(Message pMessage) {
        LOG.debug("{} Deleting groups started", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.DELETE_GROUP_REQUEST);
        Object input = lrequest.get(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE);
        if (input instanceof JSONArray jsonArray) {
            JSONArray array = jsonArray;

            int i = 0;
            while (i < array.length()) {
                pMessage.getRequestObject().setRequestJson(array.getJSONObject(i));
                deleteGroupByID(pMessage);
                i++;
            }
        } else if (input instanceof JSONObject jsonObject) {
            pMessage.getRequestObject().setRequestJson(jsonObject);
            deleteGroupByID(pMessage);
        }
        JSONObject out = new JSONObject();
        out.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
        JSONObject response = new JSONObject();
        response.put(ServerConstants.DELETE_GROUP_RESPONSE, out);
        pMessage.getResponseObject().setResponseJson(response);

    }

    private void deleteGroupByID(Message pMessage) {
        JSONObject lrequest = pMessage.getRequestObject().getRequestJson();

        String appId = lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String groupId = lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID);
        if (Utils.isNullOrEmpty(appId) || Utils.isNullOrEmpty(groupId)) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
            dexp.setCode(DomainException.Code.APZ_DM_009.toString());
            dexp.setPriority("1");
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009), dexp);
            throw dexp;
        }
        TbAsnfGroupMasterPK groupMasterPk = new TbAsnfGroupMasterPK();
        groupMasterPk.setAppId(appId);
        groupMasterPk.setGroupId(groupId);
        Optional<TbAsnfGroupMaster> groupMaster = groupMasterRepo.findById(groupMasterPk);
        if (groupMaster.isPresent()) {
            LOG.debug("{} Deleting groupMaster and groupedDevicesfor groupId : {} and appId : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, groupId, appId);
            groupMasterRepo.delete(groupMaster.get());
            groupedDeviceRepo.deleteByAppIdAndGroupId(appId, groupId);
        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setCode(DomainException.Code.APZ_DM_010.toString());
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
            dexp.setPriority("1");
            LOG.error("{} Group {} does not exists for appId : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, groupId, appId);
            throw dexp;
        }
    }

    public void searchGroup(Message pMessage) {
        LOG.debug("{} Search Group Started", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONArray out = null;
        JSONObject lReqObject = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.SEARCH_GROUP_REQUEST);
        String groupId = lReqObject.getString(ServerConstants.NOTIFICATION_GROUP_ID);
        String description = "";
        if (lReqObject.has(ServerConstants.DESCRIPTION))
            description = lReqObject.getString(ServerConstants.DESCRIPTION);
        if (Utils.isNullOrEmpty(groupId)) {
            groupId = ServerConstants.PERCENT;
        }
        if (Utils.isNullOrEmpty(description)) {
            description = ServerConstants.PERCENT;
        }
        final String fgroupId = groupId;
        final String fdescription = description;
        List<TbAsnfGroupMaster> groupList = groupMasterRepo
                .findAll(Specification.where(GroupSpecification.likeDesc(fdescription))
                        .or(GroupSpecification.descisNull()).and(GroupSpecification.likeGroupId(fgroupId)));
        if (groupList.isEmpty()) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008), dexp);
            throw dexp;
        } else {
            out = new JSONArray();
            int i = 0;
            while (i < groupList.size()) {
                JSONObject obj = new JSONObject();
                obj.put(ServerConstants.MESSAGE_HEADER_APP_ID, groupList.get(i).getId().getAppId());
                obj.put(ServerConstants.NOTIFICATION_GROUP_ID, groupList.get(i).getId().getGroupId());
                obj.put(ServerConstants.DESCRIPTION, groupList.get(i).getGroupDesc());
                out.put(obj);
                i++;
            }
            JSONObject response = new JSONObject();
            response.put(ServerConstants.SEARCH_GROUP_RESPONSE, out);
            pMessage.getResponseObject().setResponseJson(response);
        }
    }

    private List<String> getNotifRegIdsFromDeviceIds(JSONArray pdeviceList, String pappId) {
        List<String> devices = new ArrayList<>();
        List<String> notifRegIds = new ArrayList<>();
        int i = 0;
        while (i < pdeviceList.length()) {
            devices.add(pdeviceList.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
            i++;
        }
        if (pdeviceList.length() == 0) {
            return devices;
        } else {
            if (devices.size() > 1000) {
                int splitLen = 1000;
                for (int j = 0; j < devices.size(); j += splitLen) {
                    notifRegIds.addAll(deviceMasterRepo.findRegIdsByDeviceIdsListNAppId(
                            devices.subList(j, (j + splitLen > devices.size()) ? devices.size() : j + splitLen),
                            pappId));
                }
                return notifRegIds;
            }
            return deviceMasterRepo.findRegIdsByDeviceIdsListNAppId(devices, pappId);
        }
    }
}
