package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAstpSessionStorage;
import com.iexceed.appzillon.domain.entity.TbAstpSessionStoragePK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.impl.TbAsTpSessionStorageRepositoryImpl;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.utils.ServerConstants;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named(ServerConstants.APPZILLON_SESSION_STORAGE_SERVICE)
public class SessionStorageService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS, SessionStorageService.class.getName());
    public static Map<String, JSONArray> sessionMap = new HashMap<String, JSONArray>();
    @Inject
    TbAsTpSessionStorageRepositoryImpl tbAstpSessionStorageRepo;

    public void saveOrUpdateSessionStorage(Message pMessage) {
        LOG.debug("{} Saving data in session storage", ServerConstants.LOGGER_PREFIX_DOMAIN);
        pMessage.getHeader().setServiceType("");
        String sessionId = pMessage.getHeader().getSessionId();
        String deviceId = pMessage.getHeader().getDeviceId();
        String appId = pMessage.getHeader().getAppId();
        String userId = pMessage.getHeader().getUserId();
        JSONArray reqArray = pMessage.getRequestObject().getRequestJson().getJSONArray(ServerConstants.REQUEST_DATA);
        int listSize = reqArray.length();
        List<TbAstpSessionStorage> lRecordList = new ArrayList<TbAstpSessionStorage>(listSize);
        pMessage.getRequestObject().getRequestJson().remove(ServerConstants.REQUEST_DATA);

        if ((pMessage.getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION) || pMessage.getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN)) && !pMessage.getHeader().isFlushSessionMap()) {
            LOG.debug("{} loading session details to map ", ServerConstants.LOGGER_PREFIX_DOMAIN);
            sessionMap.put(appId + "_" + userId, reqArray);
        } else {
            LOG.debug("{} flushing session details to DB", ServerConstants.LOGGER_PREFIX_DOMAIN);
            for (int i = 0; i < listSize; i++) {
                TbAstpSessionStorage lRecord = new TbAstpSessionStorage();
                TbAstpSessionStoragePK lRecordPk = new TbAstpSessionStoragePK();
                lRecordPk.setAppId(appId);
                lRecordPk.setUserId(userId);
                lRecordPk.setSessionId(sessionId);
                lRecordPk.setSessionKey(reqArray.getJSONObject(i).getString(ServerConstants.USER_DATA_KEY));
                lRecord.setId(lRecordPk);
                lRecord.setDeviceId(deviceId);
                lRecord.setSessionValue(AppzillonAESUtils.encryptString(userId + sessionId, reqArray.getJSONObject(i).getString(ServerConstants.USER_DATA_VALUE)));
                lRecord.setCreatedBy(userId);
                lRecord.setCreateTs(new Timestamp(System.currentTimeMillis()));
                lRecordList.add(lRecord);
            }
            tbAstpSessionStorageRepo.saveAll(lRecordList, pMessage.getHeader().getAppId());
        }

        JSONObject response = new JSONObject();
        response.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
        pMessage.getResponseObject().setResponseJson(response);
    }

    public void deleteSessionStorage(Message pMessage) {
        LOG.debug("{} Deleting existing data for the previous session", ServerConstants.LOGGER_PREFIX_DOMAIN);
        String deviceId = pMessage.getHeader().getDeviceId();
        String appId = pMessage.getHeader().getAppId();
        String userId = pMessage.getHeader().getUserId();
        tbAstpSessionStorageRepo.deleteAll(appId, userId, deviceId);
    }

    public void getSessionStorage(Message pMessage) {
        getSessionStorageValue(pMessage);
        if (pMessage.getResponseObject().getResponseJson().getJSONArray(ServerConstants.SESSION_VALUES_ARRAY) == null || pMessage.getResponseObject().getResponseJson().getJSONArray(ServerConstants.SESSION_VALUES_ARRAY).length() == 0) {
            LOG.debug("{} No key/val pair found.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException lException = DomainException.getDomainExceptionInstance();
            lException.setCode(DomainException.Code.APZ_DM_069.toString());
            lException.setMessage(lException.getDomainExceptionMessage(DomainException.Code.APZ_DM_069));
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + lException.getDomainExceptionMessage(DomainException.Code.APZ_DM_069), lException);
            throw lException;
        }
    }

    public void getSessionStorageArray(Message pMessage) {
        getSessionStorageValue(pMessage);
    }

    private void getSessionStorageValue(Message pMessage) {
        JSONObject lRequest = pMessage.getRequestObject().getRequestJson();
        pMessage.getHeader().setServiceType("");
        String userId = pMessage.getHeader().getUserId();
        String appId = pMessage.getHeader().getAppId();
        String sessionId = pMessage.getHeader().getSessionId();

        if (!(pMessage.getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION) || pMessage.getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN)) || pMessage.getHeader().isFlushSessionMap()) {

            JSONArray jsonKeys = lRequest.getJSONArray(ServerConstants.USER_DATA_LOG_KEY);
            LOG.debug("{} Fetching data from the session storage DB for key : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, jsonKeys);
            pMessage.getRequestObject().getRequestJson().remove(ServerConstants.USER_DATA_LOG_KEY);
            int listSize = jsonKeys.length();
            List<TbAstpSessionStoragePK> lRecordList = new ArrayList<TbAstpSessionStoragePK>(listSize);

            for (int i = 0; i < listSize; i++) {
                TbAstpSessionStoragePK lPk = new TbAstpSessionStoragePK();
                lPk.setAppId(appId);
                lPk.setUserId(userId);
                lPk.setSessionId(sessionId);
                lPk.setSessionKey(jsonKeys.getJSONObject(i).getString(ServerConstants.USER_DATA_KEY));
                lRecordList.add(lPk);
            }
            List<TbAstpSessionStorage> lRecords = new ArrayList<>();
            for (TbAstpSessionStoragePK sessionStoragePK : lRecordList) {
                lRecords.addAll(tbAstpSessionStorageRepo.findAllById(sessionStoragePK, pMessage.getHeader().getAppId()));
            }
            JSONArray array = new JSONArray();
            if (!lRecords.isEmpty()) {
                for (TbAstpSessionStorage sessionStorage : lRecords) {
                    String encryptedData = sessionStorage.getSessionValue();
                    String lKey = sessionStorage.getId().getSessionKey();
                    String decryptedData = AppzillonAESUtils.decryptString(userId + sessionId, encryptedData);
                    JSONObject lResponse = new JSONObject();
                    lResponse.put(ServerConstants.USER_DATA_KEY, lKey);
                    lResponse.put(ServerConstants.USER_DATA_VALUE, decryptedData);
                    array.put(lResponse);
                }

            }
            pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.SESSION_VALUES_ARRAY, array));

        } else {
            LOG.debug("{} Fetching data from the session storage from Map : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, sessionMap.get(appId + "_" + userId));
            pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.SESSION_VALUES_ARRAY, sessionMap.get(appId + "_" + userId)));
        }
    }
}
