package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAsfsFileDetails;
import com.iexceed.appzillon.domain.entity.TbAsfsFileDetailsId;
import com.iexceed.appzillon.domain.entity.TbAsmiUserRole;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiRoleIntfRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRoleRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsfsFileDetailsRepository;
import com.iexceed.appzillon.domain.spec.FileSpecification;
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
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static com.iexceed.appzillon.domain.utils.Constants.*;

@Named("appzillonFileService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class FileService {
    @Inject
    TbAsmiUserRepository cAsmiUserDetRepository;
    @Inject
    TbAsmiUserRoleRepository cAsmiUserRoleRepository;
    @Inject
    TbAsmiRoleIntfRepository cAsmiRoleIntfRepository;
    private Logger log = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            FileService.class.getName());
    @Inject
    private TbAsfsFileDetailsRepository fileDetailsRepo;

    public void createFile(Message pMessage) {
        log.debug("{} inside createFile()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        boolean userAuthStatus;
        if (ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH.equals(pMessage.getHeader().getInterfaceId())) {
            userAuthStatus = checkForAuthorization(pMessage);
            log.debug("{} User Auth Status : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, userAuthStatus);
        }
        JSONObject mRequest = null;
        JSONObject mResponse = null;
        try {
            String path = PropertyUtils
                    .getPropValue(pMessage.getHeader().getAppId(), ServerConstants.FILE_UPLOAD_LOCATION)
                    .trim();
            path = getPath(path);
            JSONObject pBody = pMessage.getRequestObject().getRequestJson();
            mRequest = pBody.getJSONObject(ServerConstants.UPLOADFILEREQUEST);
            JSONArray fileArray = mRequest.getJSONArray(FILES);
            String createUserId = pMessage.getHeader().getUserId();
            JSONObject response = new JSONObject();
            String iface = pMessage.getHeader().getInterfaceId();
            for (int i = 0; i < fileArray.length(); i++) {

                String appId = fileArray.getJSONObject(i).get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();
                String screenId = fileArray.getJSONObject(i).get(ServerConstants.MESSAGE_HEADER_SCREEN_ID).toString();
                String userId = fileArray.getJSONObject(i).get(ServerConstants.MESSAGE_HEADER_USER_ID).toString();
                String fileName = fileArray.getJSONObject(i).get(ServerConstants.REPORT_FILENAME).toString();
                String fileExtn = fileArray.getJSONObject(i).get(ServerConstants.REPORT_FILE_TYPE).toString();
                String overrideFlag = fileArray.getJSONObject(i).get(ServerConstants.OVERRIDEFLAG).toString();
                File f = getFile(String.format("%s%s%s", path, File.separator, fileName));
                fileName = getFileName(fileName);

                Map<String, String> inputParam = new HashMap<>();
                inputParam.put("createUserId", createUserId);
                inputParam.put("iface", iface);
                inputParam.put("appId", appId);
                inputParam.put("screenId", screenId);
                inputParam.put("userId", userId);
                inputParam.put("fileName", fileName);
                inputParam.put("fileExtn", fileExtn);
                inputParam.put("overrideFlag", overrideFlag);
                processFile(fileArray, response, i, f, inputParam);
            }
            mResponse = new JSONObject();
            if (!iface.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS)) {
                mResponse.put(ServerConstants.UPLOADFILERESPONSE, response);
            } else {
                mResponse.put(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS + RESPONSE, response);
            }
            pMessage.getResponseObject().setResponseJson(mResponse);
        } catch (JSONException jsone) {
            log.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }

    }

    private void processFile(JSONArray fileArray, JSONObject response, int i, File f, Map<String, String> inputParam) {

        String createUserId = inputParam.get("createUserId");
        String iface = inputParam.get("iface");
        String appId = inputParam.get("appId");
        String screenId = inputParam.get("screenId");
        String userId = inputParam.get("userId");
        String fileName = inputParam.get("fileName");
        String fileExtn = inputParam.get("fileExtn");
        String overrideFlag = inputParam.get("overrideFlag");

        if (f.exists()) {
            log.info("File exist at location");

            if (screenId.isEmpty())
                screenId = ServerConstants.NULL;

            if (userId.isEmpty())
                userId = ServerConstants.NULL;
            TbAsfsFileDetails filedetail = this.getFile(appId, fileName);
            TbAsfsFileDetailsId recordId;
            TbAsfsFileDetails oRecord;
            Timestamp t = new Timestamp(new Date().getTime());
            if (filedetail == null) {
                log.info("file details not found so creating new record");
                recordId = new TbAsfsFileDetailsId();
                oRecord = new TbAsfsFileDetails();
                recordId.setAppId(appId);
                recordId.setFileName(fileName);
                oRecord.setId(recordId);
                oRecord.setUserId(userId);
                oRecord.setScreenId(screenId);
                oRecord.setFileType(fileExtn);
                oRecord.setInterfaceId(iface);
                oRecord.setLastUploadedDate(t);
                oRecord.setRecentUploadedDate(t);
                oRecord.setCreateUserId(createUserId);

                fileDetailsRepo.save(oRecord);
                response.put(fileName, ServerConstants.SUCCESS);
            } else if (ServerConstants.NO.equalsIgnoreCase(overrideFlag)) {
                response.put(fileName, "Already Exists");
            } else {
                log.info("file details found will update in db");
                filedetail.setUserId(userId);
                filedetail.setScreenId(screenId);
                filedetail.setFileType(fileExtn);
                filedetail.setInterfaceId(iface);
                filedetail.setLastUploadedDate(t);
                filedetail.setRecentUploadedDate(t);
                filedetail.setCreateUserId(createUserId);
                fileDetailsRepo.save(filedetail);
                response.put(fileName, ServerConstants.SUCCESS);
            }
        } else {

            if (!fileArray.getJSONObject(i).has(ServerConstants.MESSAGE_HEADER_STATUS))
                response.put(fileName, ServerConstants.FAILURE);
            else
                response.put(fileName,
                        fileArray.getJSONObject(i).getString(ServerConstants.MESSAGE_HEADER_STATUS));
        }
    }

    private String getFileName(String fileName) {
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        } else if (fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        }
        return fileName;
    }

    private String getPath(String path) {
        if (path.endsWith("\\"))
            path = path.substring(0, path.lastIndexOf('\\'));
        if (path.endsWith("/"))
            path = path.substring(0, path.lastIndexOf('/'));
        return path;
    }

    public String deleteFile(Message pMessage) {
        JSONObject response = null;
        try {
            log.info("Deleting file");
            JSONObject body = pMessage.getRequestObject().getRequestJson();
            response = new JSONObject();

            String path = PropertyUtils
                    .getPropValue(pMessage.getHeader().getAppId(), ServerConstants.FILE_UPLOAD_LOCATION).trim();
            if (path.endsWith("/"))
                path = path.substring(0, path.lastIndexOf('/'));
            if (path.endsWith("\\"))
                path = path.substring(0, path.lastIndexOf('\\'));
            String userId = body.get(ServerConstants.MESSAGE_HEADER_USER_ID).toString();
            String appId = body.get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();
            String screenId = body.get(ServerConstants.MESSAGE_HEADER_SCREEN_ID).toString();
            String fileName = body.get(ServerConstants.REPORT_FILENAME).toString();

            String appendPath = "";
            String pathappID = appId.replace('.', '_');
            if (Utils.isNotNullOrEmpty(pathappID))
                appendPath += pathappID + "/";
            String pathscreenID = screenId.replace('.', '_');
            if (Utils.isNotNullOrEmpty(pathscreenID))
                appendPath += pathscreenID + "/";
            String pathuserID = userId.replace('.', '_');
            if (Utils.isNotNullOrEmpty(pathuserID))
                appendPath += pathuserID + "/";


            TbAsfsFileDetails filedetail = this.getFile(appId, fileName);
            if (filedetail != null) {
                fileDetailsRepo.delete(filedetail);

                deleteFile(path, fileName, appendPath);

                response.put(appendPath + fileName, ServerConstants.SUCCESS);
            } else {
                response.put(appendPath + fileName, "file record not found");
            }
            return response.toString();
        } catch (JSONException jsone) {
            log.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    private void deleteFile(String path, String fileName, String appendPath) {
        try {
            String fPath = String.format("%s/%s%s", path, appendPath, fileName);
            File folder = getFile(fPath);
            this.delete(folder);
        } catch (IOException e) {
            log.error("IOException", e);
        }
    }

    public void searchFile(Message pMessage) {
        JSONObject mResponse = null;
        JSONObject mRequest = null;
        try {
            log.info("Searching file");
            JSONObject body = pMessage.getRequestObject().getRequestJson();
            mRequest = body.getJSONObject(ServerConstants.SEARCHFILEREQUEST);
            String appId = mRequest.get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();
            if (Utils.isNullOrEmpty(appId)) {
                log.warn("Search columns values not found");
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
                dexp.setCode(DomainException.Code.APZ_DM_009.toString());
                dexp.setPriority("1");
                throw dexp;
            }
            final String fappId;
            if (Utils.isNullOrEmpty(appId))
                fappId = "%";
            else
                fappId = appId;

            List<TbAsfsFileDetails> reslist;
            reslist = fileDetailsRepo.findAll(FileSpecification.likeAppId(fappId),
                    Sort.by(Sort.Direction.DESC, "recentUploadedDate"));

            if (reslist.isEmpty()) {
                log.error("{}No record found ", ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                throw dexp;
            }
            JSONArray arr = new JSONArray();
            int i = 0;
            while (i < reslist.size()) {
                JSONObject obj = new JSONObject();
                obj.put(ServerConstants.MESSAGE_HEADER_APP_ID, reslist.get(i).getId().getAppId());
                String screenId = reslist.get(i).getScreenId();
                if (ServerConstants.NULL.equals(screenId))
                    screenId = "";
                obj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, screenId);
                String userId = reslist.get(i).getUserId();
                if (ServerConstants.NULL.equals(userId))
                    userId = "";
                obj.put(ServerConstants.MESSAGE_HEADER_USER_ID, userId);

                obj.put(ServerConstants.REPORT_FILENAME, reslist.get(i).getId().getFileName());
                obj.put(ServerConstants.REPORT_FILE_TYPE, reslist.get(i).getFileType());
                obj.put("lastUpload", reslist.get(i).getLastUploadedDate());
                obj.put("recentUpload", reslist.get(i).getRecentUploadedDate());
                arr.put(obj);
                i++;
            }
            JSONObject res = new JSONObject();
            res.put(FILES, arr);
            mResponse = new JSONObject();
            mResponse.put(ServerConstants.SEARCHFILERESPONSE, res);
            pMessage.getResponseObject().setResponseJson(mResponse);
        } catch (JSONException jsone) {
            log.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    public TbAsfsFileDetails getFile(String appId, String fileName) {
        TbAsfsFileDetailsId id = new TbAsfsFileDetailsId();
        id.setAppId(appId);
        id.setFileName(fileName);
        Optional<TbAsfsFileDetails> oRecord = fileDetailsRepo.findById(id);
        if (oRecord.isPresent())
            return oRecord.get();
        return null;
    }

    public void deleteFileRequest(Message pMessage) {
        JSONObject mRequest = null;
        JSONObject mResponse = null;
        try {
            log.info("Files Delete Request recieved");
            JSONObject body = pMessage.getRequestObject().getRequestJson();
            mRequest = body.getJSONObject(ServerConstants.DELETEFILEREQUEST);
        } catch (JSONException jsone) {
            log.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        String colstring = JSONUtils.getColStringFromBody(mRequest.toString(), FILES);
        JSONArray finalStatus = null;
        String status = "";
        JSONObject jsonobj = null;
        JSONArray jsonarr = null;
        try {
            finalStatus = new JSONArray();
            if (colstring.substring(0, 1).equals("{")) {
                jsonobj = new JSONObject(colstring);
            } else if (colstring.substring(0, 1).equals("[")) {
                jsonarr = new JSONArray(colstring);
            }
            if (jsonobj != null) {
                status = this.deleteFile(pMessage);
                finalStatus.put(new JSONObject(status));
            } else if (jsonarr != null) {
                for (int i = 0; i < jsonarr.length(); i++) {
                    pMessage.getRequestObject().setRequestJson(jsonarr.getJSONObject(i));
                    status = this.deleteFile(pMessage);
                    finalStatus.put(new JSONObject(status));
                }
            }
            mResponse = new JSONObject();
            mResponse.put(ServerConstants.DELETEFILERESPONSE, finalStatus);
            pMessage.getResponseObject().setResponseJson(mResponse);
        } catch (JSONException jsone) {
            log.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    public void delete(File file) throws IOException {
        if (file.isDirectory()) {
            // directory is empty, then delete it
            if (Objects.requireNonNull(file.list()).length == 0) {
                deleteFile(file);
            } else {
                // list all the directory contents
                String[] files = file.list();
                if (files != null) {
                    for (String temp : files) {
                        // construct the file structure
                        File fileDelete = new File(file, temp);
                        // recursive delete
                        this.delete(fileDelete);
                    }
                }
                // check the directory again, if empty then delete it
                if (Objects.requireNonNull(file.list()).length == 0) {
                    deleteFile(file);
                }
            }
        } else {
            // if file, then delete it
            deleteFile(file);
        }
    }

    private void deleteFile(File file) {
        try {
            if (!file.delete()) {
                log.debug("Exception in deleting file");
            }
        } catch (Exception e) {
            DomainException ds = DomainException.getDomainExceptionInstance();
            ds.setMessage(ds.getDomainExceptionMessage(e.getMessage()));
            ds.setCode(e.getMessage());
            ds.setPriority("1");
            throw ds;
        }
    }

    public void pushFile(Message pMessage) {
        log.debug("{} inside pushFile", ServerConstants.LOGGER_PREFIX_DOMAIN);
        boolean userAuthStatus;
        if (ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH.equals(pMessage.getHeader().getInterfaceId())) {
            userAuthStatus = checkForAuthorization(pMessage);
            log.debug("{} User Auth Status : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, userAuthStatus);
        }
        JSONObject mRequest = null;
        JSONObject mResponse = null;
        JSONObject body = pMessage.getRequestObject().getRequestJson();
        mRequest = getRequestJsonObject(mRequest, body);
        String fileName = null;
        String filePath = null;
        boolean validFilePath = false;
        if (mRequest != null) {
            fileName = mRequest.get(ServerConstants.REPORT_FILENAME).toString();
            filePath = mRequest.get(ServerConstants.FILEPATH).toString();
            validFilePath = isFilePathValid(filePath, fileName, pMessage.getHeader().getAppId());
        }
        if (validFilePath) {
            if (mRequest.getString(ServerConstants.BASE64STATUS).equalsIgnoreCase(ServerConstants.NO)) {
                log.debug("{} Multipart file download", ServerConstants.LOGGER_PREFIX_DOMAIN);
                log.debug("{} Filename in request body : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, fileName);

                String fileType = fileName.substring(fileName.lastIndexOf("."));
                mResponse = new JSONObject();
                JSONObject obj = new JSONObject();
                obj.put(ServerConstants.REPORT_FILENAME, fileName);
                obj.put(ServerConstants.FILEPATH, filePath);
                obj.put(ServerConstants.REPORT_FILE_TYPE, fileType);
                if (body.has(ServerConstants.FILEPUSHSERVICEREQUEST)) {
                    mResponse.put(ServerConstants.FILEPUSHSERVICERESPONSE, obj);
                } else {
                    mResponse.put(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS + RESPONSE, obj);
                }
                pMessage.getResponseObject().setResponseJson(mResponse);
            } else {
                pushFileAsBase64(pMessage, fileName, filePath);
            }
        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_085));
            dexp.setCode(DomainException.Code.APZ_DM_085.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    private void pushFileAsBase64(Message pMessage, String fileName, String filePath) {
        JSONObject mResponse;
        String base64;
        try {
            log.debug("{} Request recieved to push file as base 64 String",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            String rootpath = PropertyUtils
                    .getPropValue(pMessage.getHeader().getAppId(), ServerConstants.FILE_UPLOAD_LOCATION).trim();
            if (rootpath.endsWith("/"))
                rootpath = rootpath.substring(0, rootpath.lastIndexOf('/'));
            if (rootpath.endsWith("\\"))
                rootpath = rootpath.substring(0, rootpath.lastIndexOf('\\'));
            String iface = pMessage.getHeader().getInterfaceId();

            String lfileName = "";
            if (fileName.contains("/")) {
                lfileName = fileName.substring(fileName.lastIndexOf("/"));
            } else {
                lfileName = fileName;
            }
            String fileType = fileName.substring(fileName.lastIndexOf("."));

            base64 = readFile(fileName, filePath, rootpath);

            mResponse = new JSONObject();
            JSONObject obj = new JSONObject();
            obj.put(ServerConstants.REPORT_FILENAME, lfileName);
            obj.put(ServerConstants.REPORT_FILE_TYPE, fileType);
            obj.put("file", base64);

            if (!iface.equals(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS)) {
                mResponse.put(ServerConstants.FILEPUSHSERVICERESPONSE, obj);
            } else {
                mResponse.put(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS + RESPONSE, obj);
            }

            pMessage.getResponseObject().setResponseJson(mResponse);
        } catch (JSONException jsone) {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    private JSONObject getRequestJsonObject(JSONObject mRequest, JSONObject body) {
        if (body.has(ServerConstants.FILEPUSHSERVICEREQUEST)) {
            mRequest = body.getJSONObject(ServerConstants.FILEPUSHSERVICEREQUEST);
        } else if (body.has(ServerConstants.FILEPUSHSERVICEWSREQUEST)) {
            mRequest = body.getJSONObject(ServerConstants.FILEPUSHSERVICEWSREQUEST);
        }
        return mRequest;
    }

    private String readFile(String fileName, String filePath, String rootpath) {
        String base64;
        try {
            String fPath = String.format("%s/%s/%s", rootpath, filePath, fileName);
            File file = getFile(fPath);
            byte[] data = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(data);
            fileInputStream.close();
            base64 = Base64.encodeBase64String(data);
        } catch (IOException e) {
            log.error("{} File  not Found at Uploaded Location", ServerConstants.LOGGER_PREFIX_DOMAIN, e);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_028));
            dexp.setCode(DomainException.Code.APZ_DM_028.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        return base64;
    }

    private boolean isFilePathValid(String filePath, String fileName, String appId) {
        boolean isValid = true;
        String rootpath = PropertyUtils.getPropValue(appId, ServerConstants.FILE_UPLOAD_LOCATION).trim();
        if (rootpath.endsWith("/"))
            rootpath = rootpath.substring(0, rootpath.lastIndexOf('/'));
        if (rootpath.endsWith("\\"))
            rootpath = rootpath.substring(0, rootpath.lastIndexOf('\\'));

        String fPath = String.format("%s/%s/%s", rootpath, filePath, fileName);
        log.debug("{} The filePath from which we are going to download is: {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                fPath);
        try {
            File file = getFile(fPath);
            log.debug("{} File absolute path {}", ServerConstants.LOGGER_PREFIX_DOMAIN, file.getAbsolutePath());
            log.debug("{} File canonical path: {}", ServerConstants.LOGGER_PREFIX_DOMAIN, file.getCanonicalPath());
            if (file.getAbsolutePath().equalsIgnoreCase(file.getCanonicalPath())) {
                log.debug("{} FilePath and filename are valid. Going to process the request.",
                        ServerConstants.LOGGER_PREFIX_DOMAIN);
            } else {
                log.debug("{} Trying to path traversal the system files. Invalidating the request",
                        ServerConstants.LOGGER_PREFIX_DOMAIN);
                isValid = false;
            }
        } catch (IOException e) {
            log.error("{} IOException:", ServerConstants.LOGGER_PREFIX_DOMAIN, e);
        }
        return isValid;
    }

    private boolean checkForAuthorization(Message pMessage) {
        boolean status = false;
        log.debug("{} inside checkForAuthorization", ServerConstants.LOGGER_PREFIX_DOMAIN);

        List<TbAsmiUserRole> result = cAsmiUserRoleRepository
                .findRolesByAppIdUserId(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId());
        List<String> lRoleslist = null;
        if (!result.isEmpty()) {
            lRoleslist = new ArrayList<>();
            for (TbAsmiUserRole tbAsmiUserRole : result) {
                lRoleslist.add(tbAsmiUserRole.getTbAsmiUserRolePK().getRoleId());
            }
            log.debug("{} Fetching List of interfaces authorized for the roleId",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            String[] interfaceIds = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIds(lRoleslist,
                    pMessage.getHeader().getAppId());
            List<String> responseInterfaceIdList = Arrays.asList(interfaceIds);
            if (interfaceIds != null && interfaceIds.length > 0) {

                if (responseInterfaceIdList.contains(pMessage.getHeader().getInterfaceId())) {
                    status = true;
                } else {
                    log.error("{} This InterfaceId Not Authorized.", ServerConstants.LOGGER_PREFIX_DOMAIN);
                    DomainException dexp = DomainException.getDomainExceptionInstance();
                    dexp.setMessage("This InterfaceId Not Authorized.");
                    dexp.setCode(DomainException.Code.APZ_DM_025.toString());
                    dexp.setPriority("1");
                    throw dexp;
                }
            } else {
                log.error("{} No InterfaceIds Exists in TbAsmiRoleIntf to Validate with request interfaceId.",
                        ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage("No InterfaceIds Exists");
                dexp.setCode(DomainException.Code.APZ_DM_025.toString());
                dexp.setPriority("1");
                throw dexp;
            }
        } else {
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_018));
            dexp.setCode(DomainException.Code.APZ_DM_018.toString());
            dexp.setPriority("1");
            log.error("{} No role exists for this userId : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    pMessage.getHeader().getUserId(), dexp);
            throw dexp;
        }
        return status;
    }

    public File getFile(String path) {
        return new File(path);
    }

}
