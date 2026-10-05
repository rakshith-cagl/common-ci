package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiAppMasterRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiAppFileRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiAppIdVersionRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiAppOsVersionRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiAppUserRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.iexceed.appzillon.domain.utils.Constants.*;

/**
 * @author Ripu This class is written for handling all the operation for OTA
 */
@Named(ServerConstants.SERVICE_OTA)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class OTAService {

    public static final String FILE_PATH_SEPERATOR = "/";
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            OTAService.class.getName());
    @Inject
    private TbAsmiAppUserRepository appUserRepo;
    @Inject
    private TbAsmiAppFileRepository appFileRepo;
    @Inject
    private TbAsmiAppMasterRepository appMasterRepo;
    @Inject
    private TbAsmiAppOsVersionRepository appOsVersionRepo;
    @Inject
    private TbAsmiAppIdVersionRepository appIdVersionRepo;

    @Autowired
    private FileService fileService;

    /**
     * Below method written by ripu for fetching child app details, which are
     * assigned to the particular user and master app
     *
     * @param pMessage
     */
    public void getChildAppDetails(Message pMessage) {
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        LOG.debug("{} inside getChildAppDetails", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject request = requestJson.getJSONObject("appzillonChildAppRequest");
        String lUserId = request.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
        String lParentAppId = request.getString(ServerConstants.PARENT_APPID);

        List<TbAsmiAppUser> appUserChildList = appUserRepo.findChildAppIdByUserIdandMasterAppId(lUserId, lParentAppId);

        if (appUserChildList.isEmpty()) {
            LOG.debug("{} No Child App Exist for user id : {} and parent app id : {}",
                    ServerConstants.LOGGER_PREFIX_DOMAIN, lUserId, lParentAppId);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            throw dexp;
        } else {
            JSONObject json = null;
            JSONArray mResponse = new JSONArray();
            for (TbAsmiAppUser tbAsmiAppUser : appUserChildList) {
                json = new JSONObject();
                json.put(ServerConstants.MESSAGE_HEADER_USER_ID, tbAsmiAppUser.getTbAsmiAppUserPK().getUserId());
                json.put(ServerConstants.PARENT_APPID, tbAsmiAppUser.getTbAsmiAppUserPK().getParentAppId());
                json.put(ServerConstants.CHILD_APP_ID, tbAsmiAppUser.getTbAsmiAppUserPK().getChildAppId());
                mResponse.put(json);
            }
            pMessage.getResponseObject().setResponseJson(new JSONObject().put("appzillonChildAppResponse", mResponse));
        }
    }

    public void getAppFileDetails(Message pMessage) {
        JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
        LOG.debug("{} inside getAppFileDetails()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject jsonRequest = mRequest.getJSONObject("appzillonAppFilesRequest");
        String lAppId = jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String lAppVersion = jsonRequest.getString(ServerConstants.APPVERSION);
        String lOs = jsonRequest.getString(ServerConstants.OS);
        String appVersionId = appIdVersionRepo.findMaxAppIdVersionByAppId(lAppId);
        LOG.debug("{} TbAsmiAppOsVersion : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, appVersionId);
        if (appVersionId != null && !appVersionId.isEmpty()) {
            List<TbAsmiAppFiles> otafileList = appFileRepo.findOTAFileBetweenVersion(lAppVersion, appVersionId, lAppId,
                    lOs);
            List<TbAsmiAppFiles> otafileAllList = appFileRepo.findOTAByAppIdAndOS(lAppId, "ALL");
            JSONArray mResponse = new JSONArray();
            processFileList(otafileList, otafileAllList, mResponse);
            JSONObject finalResponse = new JSONObject();
            finalResponse.put(lAppId, mResponse);
            finalResponse.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
            finalResponse.put(ServerConstants.APPVERSION, appVersionId);

            pMessage.getResponseObject().setResponseJson(finalResponse);
        } else {
            LOG.error("{} No Record found in TbAsmiAppOsVersion table for appId : {} and OS : {}",
                    ServerConstants.LOGGER_PREFIX_DOMAIN, lAppId, lOs);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    private void processFileList(List<TbAsmiAppFiles> otafileList, List<TbAsmiAppFiles> otafileAllList, JSONArray mResponse) {
        if (otafileList.isEmpty() && otafileAllList.isEmpty()) {
            LOG.debug("No OTA file details found.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage("No OTA file details found.");
            dexp.setCode("APZ-DM-048");
            dexp.setPriority("1");
            throw dexp;
        } else {
            if (!otafileAllList.isEmpty()) {
                setAppFileAttributeInResponse(otafileAllList, mResponse);
            }
            if (!otafileList.isEmpty()) {
                setAppFileAttributeInResponse(otafileList, mResponse);
            }
        }
    }

    private void setAppFileAttributeInResponse(List<TbAsmiAppFiles> otafileAllList, JSONArray mResponse) {
        JSONObject json;
        for (TbAsmiAppFiles tbAsmiAppFiles : otafileAllList) {
            json = new JSONObject();
            json.put(ServerConstants.APPVERSION, tbAsmiAppFiles.getTbAsmiAppFilesPK().getAppVersion());
            json.put(FILE_NAME, tbAsmiAppFiles.getTbAsmiAppFilesPK().getFileName());
            json.put(ServerConstants.FILE_PATH, tbAsmiAppFiles.getFilePath());
            json.put(ServerConstants.OS, tbAsmiAppFiles.getTbAsmiAppFilesPK().getOs());
            json.put(ServerConstants.ACTION, tbAsmiAppFiles.getAction());
            mResponse.put(json);
        }
    }

    public void downloadFile(Message pMessage) {
        try {
            JSONObject body = pMessage.getRequestObject().getRequestJson();
            JSONObject mRequest = body.getJSONObject(ServerConstants.INTERFACE_ID_OTAFILE_DOWNLOADREQ);
            LOG.debug("{} Request Received to Download the File for OTA", ServerConstants.LOGGER_PREFIX_DOMAIN);
            // otaSourceLocation
            String rootpath = PropertyUtils
                    .getPropValue(pMessage.getHeader().getAppId(), ServerConstants.OTA_SOURCE_LOCATION).toString()
                    .trim();
            if (rootpath.endsWith(FILE_PATH_SEPERATOR)) {
                rootpath = rootpath.substring(0, rootpath.lastIndexOf('/'));
            } else if (rootpath.endsWith("\\")) {
                rootpath = rootpath.substring(0, rootpath.lastIndexOf('\\'));
            }
            String appId = mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            String fileName = mRequest.getString(ServerConstants.REPORT_FILENAME);
            String osId = mRequest.getString(ServerConstants.OS);
            String appVersion = mRequest.getString(ServerConstants.APPVERSION);
            String lFilePath = mRequest.getString(ServerConstants.FILE_PATH);
            StringBuilder filePath = new StringBuilder(rootpath).append(FILE_PATH_SEPERATOR)
                    .append(osId).append(FILE_PATH_SEPERATOR).append(appId).append(FILE_PATH_SEPERATOR)
                    .append(appVersion).append(FILE_PATH_SEPERATOR).append(lFilePath);
            File file = fileService.getFile(filePath.toString());

            byte[] data = new byte[(int) file.length()];
            readFile(file, data);
            String base64 = Base64.encodeBase64String(data);
            JSONObject mResponse = new JSONObject();
            JSONObject obj = new JSONObject();
            obj.put(ServerConstants.REPORT_FILENAME, fileName);
            obj.put("file", base64);
            obj.put("filePath", lFilePath);
            mResponse.put(ServerConstants.OTAFILEDOWNLOAD_RESPONSE, obj);

            pMessage.getResponseObject().setResponseJson(mResponse);
        } catch (JSONException jsone) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }

    }

    private void readFile(File file, byte[] data) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(data);
            fileInputStream.close();
        } catch (IOException e) {
            LOG.error("{} File  not Found at Uploaded Location", ServerConstants.LOGGER_PREFIX_DOMAIN, e);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_028));
            dexp.setCode(DomainException.Code.APZ_DM_028.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    public void createAppFiles(Message pMessage) {
        JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
        LOG.debug("{} inside createAppFiles()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject jsonRequest = mRequest.getJSONObject("createAppFileRequest");
        String status = null;
        TbAsmiAppFilesPK id = new TbAsmiAppFilesPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                jsonRequest.getString(ServerConstants.APPVERSION), jsonRequest.getString(ServerConstants.OS),
                jsonRequest.getString(ServerConstants.REPORT_FILENAME));
        if (!appFileRepo.existsById(id)) {
            TbAsmiAppFiles asmiOTAfile = new TbAsmiAppFiles();
            asmiOTAfile.setTbAsmiAppFilesPK(id);
            asmiOTAfile.setFilePath(jsonRequest.getString(ServerConstants.FILE_PATH));
            asmiOTAfile.setAction(jsonRequest.getString(ServerConstants.ACTION));
            asmiOTAfile.setCreateUserId(pMessage.getHeader().getUserId());
            asmiOTAfile.setCreateTs(new Date());
            asmiOTAfile.setVersionNo(0);

            appFileRepo.save(asmiOTAfile);
            status = ServerConstants.SUCCESS;
        } else {
            LOG.error("{} Record already exists", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_015));
            dexp.setCode(DomainException.Code.APZ_DM_015.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        JSONObject response = new JSONObject();
        response.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
        pMessage.getResponseObject().setResponseJson(new JSONObject().put("createAppFileResponse", response));
    }

    /**
     * Below method written by ripu on 12-12-2014 for updating files
     *
     * @param pMessage
     */
    public void updateAppFiles(Message pMessage) {
        LOG.debug("{} inside updateAppFiles()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
        JSONObject jsonRequest = mRequest.getJSONObject("updateAppFileRequest");
        String status = null;
        TbAsmiAppFilesPK id = new TbAsmiAppFilesPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                jsonRequest.getString(ServerConstants.APPVERSION), jsonRequest.getString(ServerConstants.OS),
                jsonRequest.getString("fileName"));
        Optional<TbAsmiAppFiles> asmiOTAfile = appFileRepo.findById(id);

        if (asmiOTAfile.isPresent()) {
            appFileRepo.deleteById(id);
            LOG.debug("{} File Deleted", ServerConstants.LOGGER_PREFIX_DOMAIN);
            TbAsmiAppFilesPK pk = new TbAsmiAppFilesPK();
            pk.setAppId(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            pk.setAppVersion(jsonRequest.getString(ServerConstants.APPVERSION));
            pk.setOs(jsonRequest.getString(ServerConstants.OS));
            pk.setFileName(jsonRequest.getString("newFileName"));

            TbAsmiAppFiles obj = new TbAsmiAppFiles();
            obj.setTbAsmiAppFilesPK(pk);
            obj.setFilePath(jsonRequest.getString(ServerConstants.FILE_PATH));
            obj.setAction(jsonRequest.getString(ServerConstants.ACTION));
            obj.setCreateUserId(pMessage.getHeader().getUserId());
            obj.setCreateTs(new Date());
            obj.setVersionNo(asmiOTAfile.get().getVersionNo() + 1);

            appFileRepo.save(obj);
            status = ServerConstants.SUCCESS;
            LOG.debug("{} Record Updated in TbAsmiAppFiles table", ServerConstants.LOGGER_PREFIX_DOMAIN);
        } else {
            LOG.error(RECORD_NOT_EXIST, ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        JSONObject response = new JSONObject();
        response.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
        pMessage.getResponseObject().setResponseJson(new JSONObject().put("updateAppFileResponse", response));

    }

    /**
     * Below method written by ripu on 12-12-2014 for searching files
     *
     * @param pMessage
     */
    public void searchAppFiles(Message pMessage) {
        LOG.debug("{} inside searchAppFiles()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
        JSONObject jsonRequest = mRequest.getJSONObject("searchAppFileRequest");
        String lAppId = jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String lOs = jsonRequest.getString(ServerConstants.OS);

        List<TbAsmiAppFiles> appFileList = null;
        if (Utils.isNullOrEmpty(lAppId) && (Utils.isNullOrEmpty(lOs) || "ALL".equalsIgnoreCase(lOs))) {
            appFileList = appFileRepo.findAll();
        } else if (Utils.isNullOrEmpty(lAppId) && Utils.isNotNullOrEmpty(lOs)) {
            appFileList = appFileRepo.findOTAByOS(lOs);
        } else if (Utils.isNotNullOrEmpty(lAppId) && Utils.isNotNullOrEmpty(lOs)) {
            appFileList = appFileRepo.findOTAByAppIdAndOS(lAppId, lOs);
        }
        if (CollectionUtils.isEmpty(appFileList)) {
            LOG.error("{} No OTA file details found.",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage("No OTA file details found. ");
            dexp.setCode("APZ-DM-048");
            dexp.setPriority("1");
            throw dexp;
        } else {
            JSONArray mResponse = new JSONArray();
            JSONObject json;
            for (int a = 0; a < appFileList.size(); a++) {
                TbAsmiAppFiles otafileAll = (TbAsmiAppFiles) appFileList.get(a);
                json = new JSONObject();
                json.put(ServerConstants.MESSAGE_HEADER_APP_ID, otafileAll.getTbAsmiAppFilesPK().getAppId());
                json.put(ServerConstants.APPVERSION, otafileAll.getTbAsmiAppFilesPK().getAppVersion());
                json.put(ServerConstants.OS, otafileAll.getTbAsmiAppFilesPK().getOs());
                json.put(FILE_NAME, otafileAll.getTbAsmiAppFilesPK().getFileName());
                json.put(ServerConstants.FILE_PATH, otafileAll.getFilePath());
                json.put(ServerConstants.ACTION, otafileAll.getAction());

                mResponse.put(json);
            }
            JSONObject finalResponse = new JSONObject();
            finalResponse.put("searchAppFileResponse", mResponse);
            finalResponse.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
            pMessage.getResponseObject().setResponseJson(finalResponse);
        }
    }

    /**
     * Below method written by ripu on 12-12-2014 for deleting files
     *
     * @param pMessage
     */
    public void deleteAppFiles(Message pMessage) {
        JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
        LOG.debug("{} inside deleteAppFiles()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject jsonRequest = null;
        try {
            if (mRequest.get(DELETE_APP_FILE_REQUEST) instanceof JSONArray) {
                JSONArray jsonArray = mRequest.getJSONArray(DELETE_APP_FILE_REQUEST);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonRequest = jsonArray.getJSONObject(i);
                    deleteAppFilesRequest(jsonRequest);
                }
            } else if (mRequest.get(DELETE_APP_FILE_REQUEST) instanceof JSONObject) {
                jsonRequest = mRequest.getJSONObject(DELETE_APP_FILE_REQUEST);
                deleteAppFilesRequest(jsonRequest);
            }
            JSONObject response = new JSONObject();
            response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            pMessage.getResponseObject().setResponseJson(new JSONObject().put("deleteAppFileResponse", response));
        } catch (JSONException json) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    private boolean deleteAppFilesRequest(JSONObject pRequest) {
        LOG.debug("{} inside deleteAppFilesRequest()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        boolean status = false;
        TbAsmiAppFilesPK id = new TbAsmiAppFilesPK(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                pRequest.getString(ServerConstants.APPVERSION), pRequest.getString(ServerConstants.OS),
                pRequest.getString(ServerConstants.REPORT_FILENAME));
        if (appFileRepo.existsById(id)) {
            appFileRepo.deleteById(id);
            status = true;
        } else {
            LOG.error(RECORD_NOT_EXIST, ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        return status;
    }

    /**
     * Below service written by ripu on 11-12-2014 for creating app-master
     *
     * @param pMessage
     */
    @Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
    public void createAppMaster(Message pMessage) {
        LOG.debug("{} inside createAppMaster()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
        JSONObject jsonRequest = mRequest.getJSONObject("createAppMasterRequest");
        String status;
        String appName = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.APP_NAME);
        String appVersion = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.APPVERSION);
        String ideVersion = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.IDE_VERSION);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");// ("yyyy-MM-dd HH:mm:ss"); this code is
        // commented by ripu because date was coming in
        // (2015-12-30) format
        Date expiryDate = null;
        try {
            expiryDate = formatter.parse(jsonRequest.getString(ServerConstants.EXPIRY_DATE));
            LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Expiry Date : " + expiryDate);
        } catch (Exception e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Date Formating Error", e);
        }
        /**
         * Below changes done by ripu parentId is made nullable and not primary key on
         * 12-03-2015. Since the Appzillon IDE will send parentId value null if parentId
         * is not available.
         *
         * previous code was "TbAsmiAppMasterPK id = new
         * TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
         * jsonRequest.getString(ServerConstants.PARENT_APPID));" when parentId was
         * primaryKey
         *
         * Now it is changed to "TbAsmiAppMasterPK id = new
         * TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));"
         */
        TbAsmiAppMasterPK id = new TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        appName = (appName != null && !appName.isEmpty()) ? appName
                : jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        appVersion = (appVersion != null && !appVersion.isEmpty()) ? appVersion : appName;
        ideVersion = (ideVersion != null && !ideVersion.isEmpty()) ? ideVersion : appName;
        if (!appMasterRepo.existsById(id)) {
            TbAsmiAppMaster obj = new TbAsmiAppMaster();
            obj.setTbAsmiAppMasterPK(id);
            obj.setParentAppId(jsonRequest.getString(ServerConstants.PARENT_APPID));
            obj.setAppName(appName);
            obj.setContainerApp(jsonRequest.getString(ServerConstants.CONTAINER_APP));
            obj.setExpiryDate(expiryDate);
            obj.setOtaReq(jsonRequest.getString(ServerConstants.OTA_REQUIRED));
            obj.setRemoteDebug(jsonRequest.getString(ServerConstants.REMOTE_DEBUG));
            obj.setDefaultLanguage(jsonRequest.getString(ServerConstants.DEFAULTLANGUAGE));
            obj.setMicroAppType(JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.MICRO_APP_TYPE));
            obj.setAppVersion(appVersion);
            obj.setIdeVersion(ideVersion);
            obj.setCreateTs(new Date());
            obj.setCreateUserId(pMessage.getHeader().getUserId());
            appMasterRepo.save(obj);
            status = ServerConstants.SUCCESS;
        } else {
            LOG.error("{} Record already exists", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_015));
            dexp.setCode(DomainException.Code.APZ_DM_015.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        JSONObject response = new JSONObject();
        response.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
        pMessage.getResponseObject().setResponseJson(new JSONObject().put("createAppMasterResponse", response));
    }

    /**
     * Below service written by ripu on 15-12-2014 for updating app-master
     *
     * @param pMessage
     */
    @Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
    public void updateAppMaster(Message pMessage) {
        LOG.debug("{} inside updateAppMaster()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
        JSONObject jsonRequest = mRequest.getJSONObject("updateAppMasterRequest");
        String status = ServerConstants.FAILURE;
        String appName = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.APP_NAME);
        String appVersion = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.APPVERSION);
        String ideVersion = JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.IDE_VERSION);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date expiryDate = null;
        try {
            expiryDate = formatter.parse(jsonRequest.getString(ServerConstants.EXPIRY_DATE));
            LOG.debug("{} Expiry Date : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, expiryDate);
        } catch (Exception e) {
            LOG.error("{} Date Formating Error", ServerConstants.LOGGER_PREFIX_DOMAIN, e);
        }
        /**
         * Below changes done by ripu parentId is made nullable and not primary key on
         * 12-03-2015. Since the Appzillon IDE will send parentId value null if parentId
         * is not available.
         *
         * previous code was "TbAsmiAppMasterPK id = new
         * TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
         * jsonRequest.getString(ServerConstants.PARENT_APPID));" when parentId was
         * primaryKey
         *
         * Now it is changed to "TbAsmiAppMasterPK id = new
         * TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));"
         */
        TbAsmiAppMasterPK id = new TbAsmiAppMasterPK(jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        Optional<TbAsmiAppMaster> objOpt = appMasterRepo.findById(id);
        appName = (appName != null && !appName.isEmpty()) ? appName
                : jsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        appVersion = (appVersion != null && !appVersion.isEmpty()) ? appVersion : appName;
        ideVersion = (ideVersion != null && !ideVersion.isEmpty()) ? ideVersion : appName;
        if (objOpt.isPresent()) {
            TbAsmiAppMaster appMaster = new TbAsmiAppMaster();
            appMaster.setTbAsmiAppMasterPK(id);
            appMaster.setParentAppId(jsonRequest.getString(ServerConstants.PARENT_APPID));
            appMaster.setContainerApp(jsonRequest.getString(ServerConstants.CONTAINER_APP));
            appMaster.setAppName(appName);
            appMaster.setExpiryDate(expiryDate);
            appMaster.setOtaReq(jsonRequest.getString(ServerConstants.OTA_REQUIRED));
            appMaster.setRemoteDebug(jsonRequest.getString(ServerConstants.REMOTE_DEBUG));
            appMaster.setDefaultLanguage(jsonRequest.getString(ServerConstants.DEFAULTLANGUAGE));
            appMaster.setVersionNo(appMaster.getVersionNo() + 1);
            appMaster.setMicroAppType(JSONUtils.getJsonValueFromObject(jsonRequest, ServerConstants.MICRO_APP_TYPE));
            appMaster.setAppVersion(appVersion);
            appMaster.setIdeVersion(ideVersion);
            appMaster.setCreateTs(new Date());
            appMaster.setCreateUserId(pMessage.getHeader().getUserId());
            appMasterRepo.save(appMaster);
            status = ServerConstants.SUCCESS;
        } else {
            LOG.error(RECORD_NOT_EXIST, ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        JSONObject response = new JSONObject();
        response.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
        pMessage.getResponseObject().setResponseJson(new JSONObject().put("updateAppMasterResponse", response));
    }

    /**
     * Below service written by ripu on 08-12-2014 for searching app-master
     *
     * @param pMessage
     */
    public void searchAppMaster(Message pMessage) {
        LOG.debug("{} inside searchAppMaster()..", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        JSONObject request = requestJson.getJSONObject("appzillonSearchAppMasterRequest");
        String lAppId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        List<TbAsmiAppMaster> objList = null;
        if (Utils.isNullOrEmpty(lAppId)) {
            objList = appMasterRepo.findAll();
        } else {
            objList = appMasterRepo.findAppMasterByAppIdinList(lAppId);
        }
        JSONArray mResponse = new JSONArray();
        if (!objList.isEmpty()) {
            for (int i = 0; i < objList.size(); i++) {
                TbAsmiAppMaster appMaster = objList.get(i);
                JSONObject json = new JSONObject();
                json.put(ServerConstants.MESSAGE_HEADER_APP_ID, appMaster.getTbAsmiAppMasterPK().getAppId());
                json.put(ServerConstants.CONTAINER_APP, appMaster.getContainerApp());
                json.put(ServerConstants.OTA_REQUIRED, appMaster.getOtaReq());
                json.put(ServerConstants.REMOTE_DEBUG, appMaster.getRemoteDebug());
                json.put(ServerConstants.EXPIRY_DATE, appMaster.getExpiryDate());
                json.put(ServerConstants.PARENT_APPID, appMaster.getParentAppId());
                json.put(ServerConstants.DEFAULTLANGUAGE, appMaster.getDefaultLanguage());
                mResponse.put(json);
            }
            pMessage.getResponseObject()
                    .setResponseJson(new JSONObject().put("appzillonSearchAppMasterResponse", mResponse));
        } else {
            LOG.error("{} No Record found for appId : {} in db", ServerConstants.LOGGER_PREFIX_DOMAIN, lAppId);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    /**
     * Below service written by ripu on 15-12-2014 for updating app-master
     *
     * @param pMessage
     */
    @Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
    public void deleteAppMaster(Message pMessage) {
        JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
        LOG.debug("{} inside deleteAppMaster()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject jsonRequest = null;
        try {
            if (mRequest.get(DELETE_APP_MASTER_REQUEST) instanceof JSONArray) {
                JSONArray jsonArray = mRequest.getJSONArray(DELETE_APP_MASTER_REQUEST);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonRequest = jsonArray.getJSONObject(i);
                    deleteAppMasterRequest(jsonRequest);
                }
            } else if (mRequest.get(DELETE_APP_MASTER_REQUEST) instanceof JSONObject) {
                jsonRequest = mRequest.getJSONObject(DELETE_APP_MASTER_REQUEST);
                deleteAppMasterRequest(jsonRequest);
            }
            JSONObject response = new JSONObject();
            response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            pMessage.getResponseObject().setResponseJson(new JSONObject().put("deleteAppMasterResponse", response));
        } catch (JSONException json) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    private boolean deleteAppMasterRequest(JSONObject pJsonRequest) {
        LOG.debug("{} inside deleteAppMasterRequest()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        boolean status = false;
        TbAsmiAppMasterPK id = new TbAsmiAppMasterPK(pJsonRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        if (appMasterRepo.existsById(id)) {
            appMasterRepo.deleteById(id);
            status = true;
        } else {
            LOG.error(RECORD_NOT_EXIST, ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        return status;
    }

    public void fetchWelcomeMsg(Message pMessage) {
        LOG.debug("{} Inside fetchWelcomeMsg()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_GET_CNVUI_WELCOME_MSG_REQUEST);
        String appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        TbAsmiAppMaster tbAsmiAppMaster = appMasterRepo.findAppMasterByAppId(appId);
        String message = tbAsmiAppMaster.getWelcomeMsg();
        LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Welcome message : " + message);
        JSONObject responseJson = new JSONObject();
        responseJson.put(ServerConstants.MESSAGE, message);
        pMessage.getResponseObject().getResponseJson()
                .put(ServerConstants.APPZILLON_ROOT_GET_CNVUI_WELCOME_MSG_RESPONSE, responseJson);
    }

}
