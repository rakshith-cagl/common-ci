package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserDevicesRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAstpLastLoginRepository;
import com.iexceed.appzillon.domain.repository.meta.*;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.*;

import static com.iexceed.appzillon.domain.utils.Constants.*;

@Named("NotificationService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class NotificationService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            NotificationService.class.toString());
    @Inject
    TbAsnfDevicesMasterRepository deviceMasterRepo;
    @Inject
    TbAsnfGroupDeviceRepository groupedDeviceRepo;
    @Inject
    TbAsnfGroupMasterRepository groupMasterRepo;
    @Inject
    TbAsnfTxnLogRepository nfTxnLogRepo;
    @Inject
    TbAsnfTxnMasterRepository nfTxnmasterRepo;
    @Inject
    TbAsnfTxnDevicesRepository nfTxnDevicesRepo;
    @Inject
    TbAsnfTxnGroupsRepository nfTxnGroupsRepo;
    @Inject
    TbAstpSeqGenRepository seqGenRepo;
    @Inject
    TbAsmiUserDevicesRepository userDevicesRepo;
    @Inject
    TbAstpLastLoginRepository userLastLoginRepo;

    public void registerDevice(Message pMessage) {
        LOG.debug("{} Registering Device ", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lRequest = pMessage.getRequestObject().getRequestJson();
        if (Utils.isNullOrEmpty(lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID))
                || Utils.isNullOrEmpty(lRequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID))
                || Utils.isNullOrEmpty(lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID))
                || Utils.isNullOrEmpty(lRequest.getString(ServerConstants.NOTIFICATION_OS_ID))) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
            dexp.setCode(DomainException.Code.APZ_DM_009.toString());
            dexp.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Column Value can not be null", dexp);
            throw dexp;
        }
        // update status for old regId of android devices in case of
        // reinstalling app
        if (lRequest.getString(ServerConstants.NOTIFICATION_OS_ID).equalsIgnoreCase("ANDROID")) {
            updateUninstalledDevices(lRequest);
        }

        TbAsnfDevicesMasterPK deviceMasterPk = new TbAsnfDevicesMasterPK();
        deviceMasterPk.setAppId(lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        deviceMasterPk.setNotifRegId(lRequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID));

        TbAsnfDevicesMaster deviceMaster = new TbAsnfDevicesMaster();
        deviceMaster.setId(deviceMasterPk);
        deviceMaster.setStatus(ServerConstants.YES);
        if (lRequest.has(ServerConstants.NOTIFICATION_DEVICE_NAME)) {
            deviceMaster.setDeviceName(lRequest.getString(ServerConstants.NOTIFICATION_DEVICE_NAME));
        }

        if (lRequest.has(ServerConstants.NOTIFICATION_OS_VERSION)) {
            deviceMaster.setOsVersion(lRequest.getString(ServerConstants.NOTIFICATION_OS_VERSION));
        }
        if (lRequest.has(ServerConstants.NOTIF_MSG_SERVER)) {
            deviceMaster.setNotifMsgServer(lRequest.getString(ServerConstants.NOTIF_MSG_SERVER));
        }

        deviceMaster.setOsId(lRequest.getString(ServerConstants.NOTIFICATION_OS_ID).toUpperCase());
        deviceMaster.setDeviceId(lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
        deviceMaster.setCreatedBy(pMessage.getHeader().getUserId());
        deviceMaster.setCreatedTs(new Timestamp(System.currentTimeMillis()));
        deviceMaster.setVersionNo((long) 1);

        deviceMasterRepo.save(deviceMaster);

        JSONObject response = new JSONObject();
        response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
        pMessage.getResponseObject().setResponseJson(response);
    }

    private void updateUninstalledDevices(JSONObject lRequest) {
        String appId = lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String deviceId = lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
        List<TbAsnfDevicesMaster> reinstallingDeviceId = deviceMasterRepo.findByAppIdAndDeviceId(appId, deviceId);
        if (!reinstallingDeviceId.isEmpty()) {
            LOG.debug("{} User is reinstalling app, changing status for the old reg id",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            reinstallingDeviceId.get(0).setStatus(ServerConstants.NO);
            deviceMasterRepo.save(reinstallingDeviceId.get(0));
        }
    }

    public void getMappedNAvailableDeviceForGroup(Message pMessage) {
        LOG.debug("{} Getting Mapped and available devices for a given group", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.GET_GROUP_DETAIL_REQUEST);
        String appId = lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String groupId = lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID);
        List<String> regIdsUnderGroup = groupedDeviceRepo.findRegIdsByAppIdAndGroupId(appId, groupId);
        List<TbAsnfDevicesMaster> underGroupDetails = null;
        List<TbAsnfDevicesMaster> notUnderGroupDetails = null;
        JSONArray members = new JSONArray();
        JSONArray nonMembers = new JSONArray();
        if (!regIdsUnderGroup.isEmpty()) {
            underGroupDetails = deviceMasterRepo.findByInRegIdListNAppIdGprID(appId, groupId);
            notUnderGroupDetails = deviceMasterRepo.findByNotInRegIdListNAppIdGprID(appId, groupId);
            LOG.debug("{} Number of devices available to be mapped to group {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    notUnderGroupDetails.size());
            int i = 0;
            while (i < underGroupDetails.size()) {
                JSONObject json = new JSONObject();
                json.put(ServerConstants.NOTIFICATION_OS_ID, underGroupDetails.get(i).getOsId());
                json.put(ServerConstants.NOTIFICATION_DEVICE_NAME, underGroupDetails.get(i).getDeviceName());
                json.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, underGroupDetails.get(i).getDeviceId());
                members.put(json);
                i++;
            }

        } else {
            notUnderGroupDetails = deviceMasterRepo.findByAppIdNStatus(appId);
            LOG.debug("{} No of devices available for mapping to group {} ", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    notUnderGroupDetails.size());
            // changes for bug 4718
            if (notUnderGroupDetails.size() == 0) {
                pMessage.getHeader().setStatus(false);
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_037));
                dexp.setCode(DomainException.Code.APZ_DM_037.toString());
                dexp.setPriority("1");
                LOG.error("{} No Available Device for this Application", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;

            }
        }
        int i = 0;
        while (i < notUnderGroupDetails.size()) {
            JSONObject json = new JSONObject();
            json.put(ServerConstants.NOTIFICATION_OS_ID, notUnderGroupDetails.get(i).getOsId());
            json.put(ServerConstants.NOTIFICATION_DEVICE_NAME, notUnderGroupDetails.get(i).getDeviceName());
            json.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, notUnderGroupDetails.get(i).getDeviceId());
            nonMembers.put(json);
            i++;
        }
        JSONObject resobj = new JSONObject();
        JSONObject responseJson = new JSONObject();
        resobj.put(ServerConstants.MEMBER, members);
        resobj.put(ServerConstants.NONMEMBER, nonMembers);
        responseJson.put(ServerConstants.GET_GROUP_DETAIL_RESPONSE, resobj);
        pMessage.getResponseObject().setResponseJson(responseJson);
    }

    public void getGroupNDevicesForApplication(Message pMessage) {
        LOG.debug("{} Getting Group and Devices for application", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.NOTIFICATION_APP_DETAIL_REQUEST);
        String appId = lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        List<TbAsnfDevicesMaster> devicesList = deviceMasterRepo.findByAppIdNStatus(appId);
        LOG.debug("{} {} devices found Registered for Application With Status Y ", ServerConstants.LOGGER_PREFIX_DOMAIN,
                devicesList.size());
        List<TbAsnfGroupMaster> groupList = groupMasterRepo.findByAppId(appId);
        LOG.debug("{} {} Number of Groups found for Application ", ServerConstants.LOGGER_PREFIX_DOMAIN,
                groupList.size());
        if (devicesList.isEmpty() && groupList.isEmpty()) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_002));
            dexp.setCode(DomainException.Code.APZ_DM_002.toString());
            dexp.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No Devices and Groups found for this Application", dexp);
            throw dexp;
        } else {
            int i = 0;
            JSONArray deviceArray = new JSONArray();
            while (i < devicesList.size()) {
                JSONObject obj = new JSONObject();
                obj.put(ServerConstants.NOTIFICATION_OS_ID, devicesList.get(i).getOsId());
                obj.put(ServerConstants.NOTIFICATION_DEVICE_NAME, devicesList.get(i).getDeviceName());
                obj.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, devicesList.get(i).getDeviceId());
                obj.put(ServerConstants.MESSAGE_HEADER_APP_ID, devicesList.get(i).getId().getAppId());
                i++;
                deviceArray.put(obj);
            }
            i = 0;
            JSONArray groupArray = new JSONArray();
            while (i < groupList.size()) {
                JSONObject obj = new JSONObject();
                obj.put(ServerConstants.NOTIFICATION_GROUP_ID, groupList.get(i).getId().getGroupId());
                obj.put(ServerConstants.DESCRIPTION, groupList.get(i).getGroupDesc());
                obj.put(ServerConstants.MESSAGE_HEADER_APP_ID, groupList.get(i).getId().getAppId());
                i++;
                groupArray.put(obj);
            }
            JSONObject out = new JSONObject();
            out.put(ServerConstants.DEVICE_ID_MULTIPLE, deviceArray);
            out.put(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE, groupArray);
            JSONObject response = new JSONObject();
            response.put(ServerConstants.NOTIFICATION_APP_DETAIL_RESPONSE, out);
            pMessage.getResponseObject().setResponseJson(response);
        }
    }

    public void getNotifRegIdsForDevNGroup(Message pMessage) {
        LOG.debug("{} Getting device and Grouped devices NotificationRegIds", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ);
        String lappId = lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String lmessageData = lrequest.getString(ServerConstants.NOTIFICATION);
        String title = lrequest.getString("title");
        String imageURL = null;
        String subtitle = null;
        String category = null;
        String icon = null;
        String clickAction = null;
        if (lrequest.has(IMAGE_URL) && !lrequest.getString(IMAGE_URL).isEmpty())
            imageURL = lrequest.getString(IMAGE_URL);
        if (lrequest.has(SUB_TITLE) && !lrequest.getString(SUB_TITLE).isEmpty())
            subtitle = lrequest.getString(SUB_TITLE);
        if (lrequest.has(CATEGORY) && !lrequest.getString(CATEGORY).isEmpty())
            category = lrequest.getString(CATEGORY);
        if (lrequest.has(ServerConstants.WEB_NOTIFICATION_ICON)
                && !lrequest.getString(ServerConstants.WEB_NOTIFICATION_ICON).isEmpty())
            icon = lrequest.getString(ServerConstants.WEB_NOTIFICATION_ICON);
        if (lrequest.has(ServerConstants.WEB_NOTIFICATION_CLICK_ACTION)
                && !lrequest.getString(ServerConstants.WEB_NOTIFICATION_CLICK_ACTION).isEmpty())
            clickAction = lrequest.getString(ServerConstants.WEB_NOTIFICATION_CLICK_ACTION);
        boolean setContentAvailable = false;
        if (lrequest.has(ServerConstants.IOS_CONTENT_AVAILABLE)
                && Utils.isNotNullOrEmpty(lrequest.getString(ServerConstants.IOS_CONTENT_AVAILABLE))) {
            setContentAvailable = lrequest.getBoolean(ServerConstants.IOS_CONTENT_AVAILABLE);
        }
        JSONObject params;
        params = null;
        if (lrequest.has("params")) {
            params = lrequest.getJSONObject(ServerConstants.NOTIFICATION_PARAMETERS);
        }
        JSONArray deviceIds = null;
        if (lrequest.has(ServerConstants.DEVICE_ID_MULTIPLE)) {
            deviceIds = lrequest.getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE);
        }
        JSONArray groupIds = null;
        if (lrequest.has(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE)) {
            groupIds = lrequest.getJSONArray(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE);
        }
        JSONArray lOsIds = null;
        if (lrequest.has(ServerConstants.NOTIFICATION_OS_ID_MULTIPLE)) {
            lOsIds = lrequest.getJSONArray(ServerConstants.NOTIFICATION_OS_ID_MULTIPLE);
        }
        List<String> osList = null;
        if (lOsIds != null && lOsIds.length() > 0) {
            osList = new ArrayList<>();
            for (int i = 0; i < lOsIds.length(); i++) {
                osList.add(lOsIds.getString(i));
            }
        }
        if ((deviceIds == null || deviceIds.length() == 0) && (groupIds == null || groupIds.length() == 0)
                && (osList == null || osList.isEmpty())) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
            dexp.setCode(DomainException.Code.APZ_DM_009.toString());
            dexp.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No Devices and GroupIds Selected for Notifications",
                    dexp);
            throw dexp;
        } else {
            List<String> finalGroup = new ArrayList<>();
            List<String> groupedDevicesRegIds = null;
            if (groupIds != null && groupIds.length() > 0 && pMessage.getHeader().isnotifGroupFlag()
                    && !pMessage.getHeader().isnotifOsFlag()) {
                List<String> groups = new ArrayList<>();
                int i = 0;
                while (i < groupIds.length()) {
                    groups.add(groupIds.getJSONObject(i).getString(ServerConstants.NOTIFICATION_GROUP_ID));
                    i++;
                }
                if (osList == null || osList.isEmpty()) {
                    osList = new ArrayList<>();
                    osList.add(" ");
                }

                groupedDevicesRegIds = deviceMasterRepo.findRegIdsByAppIdAndGroupIdList(osList, lappId, groups,
                        PageRequest.of(pMessage.getHeader().getNotifOffset(), 1000));
                if (groupedDevicesRegIds == null || groupedDevicesRegIds.isEmpty()) {
                    pMessage.getHeader().setnotifGroupFlag(false);
                }

                LOG.debug("{} Found Grouped RegIds : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                        groupedDevicesRegIds.size());
                if (groupedDevicesRegIds != null && !groupedDevicesRegIds.isEmpty()) {
                    finalGroup.addAll(groupedDevicesRegIds);
                }
            }

            LOG.debug("{} Getting Regids of UnGrouped devices", ServerConstants.LOGGER_PREFIX_DOMAIN);
            List<String> deviceRegIds = null;
            if (deviceIds != null && deviceIds.length() > 0 && pMessage.getHeader().isnotifDeviceFlag()
                    && !pMessage.getHeader().isnotifGroupFlag() && !pMessage.getHeader().isnotifOsFlag()) {
                deviceRegIds = getRegIdsFromDeviceIds(deviceIds, lappId);
                LOG.debug("{} Found Devices RegIds : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, deviceRegIds.size());
                if (deviceRegIds != null && !deviceRegIds.isEmpty()) {
                    finalGroup.addAll(deviceRegIds);
                    pMessage.getHeader().setnotifDeviceFlag(false);
                }
            }

            Map<String, String> androidMap = new HashMap<>();
            Map<String, String> iosMap = new HashMap<>();
            Map<String, String> bbMap = new HashMap<>();
            Map<String, String> webMap = new HashMap<>();
            Map<String, String> hauweiMsgServerMap = new HashMap<>();
            List<TbAsnfDevicesMaster> deviceList = null;
            if (finalGroup != null && !finalGroup.isEmpty()) {
                deviceList = deviceMasterRepo.findByInRegIdListNAppId(finalGroup, lappId);
            }
            if (osList != null && !osList.isEmpty() && pMessage.getHeader().isnotifOsFlag()) {
                List<TbAsnfDevicesMaster> deviceListBasedOnOsIDs = deviceMasterRepo.findByInOsIdListAndAppId(osList,
                        lappId, PageRequest.of(pMessage.getHeader().getNotifOffset(), 1000));
                // List<TbAsnfDevicesMaster> deviceListBasedOnOsIDs =
                if (deviceListBasedOnOsIDs.isEmpty()) {
                    pMessage.getHeader().setnotifOsFlag(false);
                    if (groupIds != null && groupIds.length() > 0) {
                        pMessage.getHeader().setnotifGroupFlag(true);
                        pMessage.getHeader().setNotifOffset(-1);
                    }
                }
                deviceList = new ArrayList<>();
                deviceList.addAll(deviceListBasedOnOsIDs);
            }

            // LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Notification to

            LOG.debug("{} Final No OF Devices : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, deviceList.size());
            if (deviceList != null) {
                int i = 0;
                while (i < deviceList.size()) {
                    if ("IOS".equalsIgnoreCase(deviceList.get(i).getOsId())) {
                        if (iosMap.containsKey(deviceList.get(i).getDeviceId())) {
                            // LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN + " Duplicate deviceId found
                            iosMap.put(deviceList.get(i).getDeviceId() + i, deviceList.get(i).getId().getNotifRegId());
                        } else {
                            iosMap.put(deviceList.get(i).getDeviceId(), deviceList.get(i).getId().getNotifRegId());
                        }
                    } else if ("ANDROID".equalsIgnoreCase(deviceList.get(i).getOsId())) {
                        if (ServerConstants.HAUWEI_MSG_SERVER.equals(deviceList.get(i).getNotifMsgServer())) {
                            hauweiMsgServerMap.put(deviceList.get(i).getDeviceId(),
                                    deviceList.get(i).getId().getNotifRegId());
                        } else {
                            androidMap.put(deviceList.get(i).getDeviceId(), deviceList.get(i).getId().getNotifRegId());
                        }

                    } else if ("BLACKBERRY".equalsIgnoreCase(deviceList.get(i).getOsId())
                            || "BLACKBERRY10".equalsIgnoreCase(deviceList.get(i).getOsId())) {
                        /*
                         * Below changes is made by Samy on 20/01/2016 To Send notifications to BB7/BB10
                         * with different set of properties
                         */
                        if ("BLACKBERRY10".equalsIgnoreCase(deviceList.get(i).getOsId())) {
                            bbMap.put(deviceList.get(i).getDeviceId(), "T" + deviceList.get(i).getId().getNotifRegId());
                        } else {
                            bbMap.put(deviceList.get(i).getDeviceId(), deviceList.get(i).getId().getNotifRegId());
                        }

                    } else if ("WEB".equalsIgnoreCase(deviceList.get(i).getOsId())) {
                        try {

                            webMap.put("" + i, URLDecoder.decode(deviceList.get(i).getId().getNotifRegId(), "UTF-8"));

                        } catch (UnsupportedEncodingException e) {
                            LOG.error("{} {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                                    ServerConstants.UNSUPPORTED_ENCODING_EXCEPTION, e);
                            DomainException lDomainException = DomainException.getDomainExceptionInstance();
                            lDomainException.setMessage(
                                    lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_084));
                            lDomainException.setCode(DomainException.Code.APZ_DM_084.toString());
                            lDomainException.setPriority("1");
                            throw lDomainException;
                        }
                    }
                    i++;
                }
            }
            LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Creating new Request");
            lrequest.put(ServerConstants.MESSAGE_HEADER_APP_ID, lappId);
            lrequest.put(ServerConstants.NOTIFICATION, lmessageData);
            lrequest.put("androidDevices", androidMap);
            lrequest.put("hauweiDevices", hauweiMsgServerMap);
            lrequest.put("iosDevices", iosMap);
            lrequest.put("webDevices", webMap);
            lrequest.put("title", title);
            lrequest.put(SUB_TITLE, subtitle);
            lrequest.put(IMAGE_URL, imageURL);
            lrequest.put(CATEGORY, category);
            lrequest.put("icon", icon);
            lrequest.put("clickAction", clickAction);
            lrequest.put("contentAvailable", setContentAvailable);
            if (params != null) {
                lrequest.put("params", params);
            }
            lrequest.put("bbDevices", bbMap);
            lrequest.put(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE, groupIds);
            JSONObject newRequest = new JSONObject();
            newRequest.put(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ, lrequest);
            pMessage.getRequestObject().setRequestJson(newRequest);
        }

    }

    private List<String> getRegIdsFromDeviceIds(JSONArray pdeviceIds, String pappId) {
        List<String> devices = new ArrayList<>();
        int i = 0;
        while (i < pdeviceIds.length()) {
            devices.add(pdeviceIds.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
            i++;
        }
        if (pdeviceIds.length() == 0) {
            return devices;
        } else {
            return deviceMasterRepo.findRegIdsByDeviceIdsListNAppId(devices, pappId);
        }
    }

    public void writeNotificationLogs(Message pMessage) {
        LOG.debug("{} Populating Notification transactions table", ServerConstants.LOGGER_PREFIX_DOMAIN);
        long notifIdAll = pMessage.getHeader().getNotifSeqNo();
        if (notifIdAll == -1) {
            TbAstpSeqGen astpSeq = seqGenRepo.getloggingsequencenumber("NOTIFICATION_NO");
            notifIdAll = astpSeq.getSequenceValue();
            pMessage.getHeader().setNotifSeqNo(notifIdAll);
            astpSeq.setSequenceValue((int) notifIdAll + 1);
            seqGenRepo.save(astpSeq);
        }

        LOG.debug("{} Logging txn under Notfication Ref No : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, notifIdAll);
        JSONArray lrequest = pMessage.getRequestObject().getRequestJson()
                .getJSONArray(ServerConstants.APPZILLON_ROOT_NF_TXNARRAY);
        JSONArray lgroupIds = pMessage.getRequestObject().getRequestJson()
                .getJSONArray(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE);
        Map<String, String> userDeviceIdMap = fetchUsersWithAppIDAndDeviceId(lrequest);
        int i = 0;
        while (i < lrequest.length()) {
            TbAsnfTxnDevicePK id = new TbAsnfTxnDevicePK();
            id.setNotifId(notifIdAll);
            id.setAppId(lrequest.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            id.setNotifRegId(lrequest.getJSONObject(i).getString(ServerConstants.NOTIFICATION_REGISTRATION_ID));
            TbAsnfTxnDevice txnDevice = new TbAsnfTxnDevice();
            txnDevice.setId(id);
            nfTxnDevicesRepo.save(txnDevice);

            TbAsnfTxnLogPK txLogPk = new TbAsnfTxnLogPK();
            txLogPk.setAppId(lrequest.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            txLogPk.setNotifRegId(lrequest.getJSONObject(i).getString(ServerConstants.NOTIFICATION_REGISTRATION_ID));
            txLogPk.setNotifId(notifIdAll);
            txLogPk.setDeviceId(lrequest.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));// added
            // on
            // may30-2019

            TbAsnfTxnLog txLog = new TbAsnfTxnLog();
            txLog.setId(txLogPk);
            txLog.setCreatedTs(new Timestamp(System.currentTimeMillis()));
            txLog.setVersionNo((long) 1);
            txLog.setCreatedBy(pMessage.getHeader().getUserId());
            txLog.setStatus(lrequest.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_STATUS));
            txLog.setUserId(
                    userDeviceIdMap.get(lrequest.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID)));
            nfTxnLogRepo.save(txLog);

            TbAsnfTxnMasterPK txnMasterPK = new TbAsnfTxnMasterPK();
            txnMasterPK.setNotifId(notifIdAll);
            txnMasterPK.setAppId(lrequest.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            txnMasterPK.setNotifMsg(lrequest.getJSONObject(i).getString(ServerConstants.NOTIFICATION));
            TbAsnfTxnMaster txnMaster = new TbAsnfTxnMaster();
            txnMaster.setId(txnMasterPK);
            if ((lrequest.getJSONObject(i).has(ServerConstants.NOTIFICATION_CATEGORY))) {
                txnMaster.setCategory((lrequest.getJSONObject(i).getString(ServerConstants.NOTIFICATION_CATEGORY)));
            }
            if ((lrequest.getJSONObject(i).has(ServerConstants.NOTIFICATION_SUBTITLE))) {
                txnMaster.setSubtitle((lrequest.getJSONObject(i).getString(ServerConstants.NOTIFICATION_SUBTITLE)));
            }
            if ((lrequest.getJSONObject(i).has(ServerConstants.NOTIFICATION_IMAGE_URL))) {
                txnMaster.setImageURL((lrequest.getJSONObject(i).getString(ServerConstants.NOTIFICATION_IMAGE_URL)));
            }
            if ((lrequest.getJSONObject(i).has(ServerConstants.NOTIFICATION_TITLE))) {
                txnMaster.setTitle((lrequest.getJSONObject(i).getString(ServerConstants.NOTIFICATION_TITLE)));
            }

            txnMaster.setCreateTS(new Date());
            nfTxnmasterRepo.save(txnMaster);
            i++;
        }
        i = 0;
        while (i < lgroupIds.length() && pMessage.getHeader().isnotifGroupFlag()
                && !pMessage.getHeader().isnotifOsFlag()) {
            TbAsnfTxnGroupPK txnGroupPk = new TbAsnfTxnGroupPK();
            txnGroupPk.setNotifId(notifIdAll);
            txnGroupPk.setAppId(lgroupIds.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            txnGroupPk.setGroupId(lgroupIds.getJSONObject(i).getString(ServerConstants.NOTIFICATION_GROUP_ID));
            TbAsnfTxnGroup txnGroup = new TbAsnfTxnGroup();
            txnGroup.setId(txnGroupPk);
            nfTxnGroupsRepo.save(txnGroup);
            i++;
        }
        JSONObject out = new JSONObject();
        out.put(ServerConstants.REFNO, "" + notifIdAll);
        JSONObject response = new JSONObject();
        response.put(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP, out);
        pMessage.getResponseObject().setResponseJson(response);
        LOG.debug("{} Populated Notification transactions table ", ServerConstants.LOGGER_PREFIX_DOMAIN);
        pMessage.getRequestObject().getRequestJson().remove(ServerConstants.APPZILLON_ROOT_NF_TXNARRAY);
        pMessage.getRequestObject().getRequestJson().remove(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE);

    }

    private Map<String, String> fetchUsersWithAppIDAndDeviceId(JSONArray lrequest) {
        LOG.debug("{} Inside fetchUsersWithAppIDAndDeviceId", ServerConstants.LOGGER_PREFIX_DOMAIN);
        String appId = lrequest.getJSONObject(0).getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        Map<String, String> usersDeviceIds = new HashMap<>();
        List<String> deviceIdList = new ArrayList<>();
        int i = 0;
        while (i < lrequest.length()) {
            String deviceId = lrequest.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
            deviceIdList.add(deviceId);
            i++;
        }
        LOG.debug("{} No of deviceIds from request are : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                deviceIdList.size());
        List<TbAstpLastLogin> listOfUsers = userLastLoginRepo.findUsersByAppIdAndDeviceID(appId, deviceIdList);
        i = 0;
        LOG.debug("{} No of user records from db : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, listOfUsers.size());
        while (i < listOfUsers.size()) {
            TbAstpLastLogin tb = listOfUsers.get(i);
            usersDeviceIds.put(tb.getTbAstpLastLoginPK().getDeviceId(), tb.getTbAstpLastLoginPK().getUserId());
            i++;
        }

        return usersDeviceIds;
    }

}
