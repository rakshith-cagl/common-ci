package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.iface.ITenantCreation;
import com.iexceed.appzillon.utils.ServerConstants;

import java.io.File;

public class TenantCreationImpl implements ITenantCreation {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
            AuditLogImpl.class.getName());

    @Override
    public void createTenant(Message pMessage) {
        LOG.debug("{} Creation of new app by generation of properties files and xmls", ServerConstants.LOGGER_PREFIX_SMS);
        JSONObject request = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_CREATE_TENANT_REQUEST);
        JSONArray oldAppIds = request.getJSONArray("appIds");
        JSONArray newAppIds = request.getJSONArray("newAppIds");

        for (int i = 0; i < oldAppIds.length(); i++) {
            createFolderStructure(oldAppIds.getString(i), newAppIds.getString(i));
        }

        JSONObject response = new JSONObject();
        response.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
        pMessage.getResponseObject()
                .setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_ROOT_CREATE_TENANT_RESPONSE, response));
    }

    public void createFolderStructure(String oldAppId, String newAppId) {
        String configurationFilePath = TenantCreationImpl.class.getClassLoader().getResource(Logger.propertiesPath)
                .getFile();
        String oldAppFilepath = configurationFilePath + File.separator + oldAppId;
        String newAppFilePath = configurationFilePath + File.separator + newAppId;

        if (new File(oldAppFilepath).exists()) {
            LOG.debug("{} Tenant app {} will be created for an existing app {}", ServerConstants.LOGGER_PREFIX_SMS, newAppId, oldAppId);
        } else {
            LOG.error("{} Invalid appId in tenant creation request.", ServerConstants.LOGGER_PREFIX_SMS);
            SmsException lSmsException = SmsException.getSMSExceptionInstance();
            lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_018));
            lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_018.toString());
            lSmsException.setPriority("1");
            throw lSmsException;
        }


        // Copying of whole app folder contents to new.
        Utils.copySourceFolder(oldAppFilepath, newAppFilePath);

        // Replacing appId in the newly copied files.
        String[] files = new String[]{ServerConstants.CAMEL_CONTEXT_XML, ServerConstants.SMS_SPRING_XML,
                ServerConstants.NOTIFICATION_XML};
        for (String s : files) {
            String filePath = newAppFilePath + File.separator + ServerConstants.META_INF_SPRING + s;
            Utils.searchAndReplaceString(oldAppId + "_", newAppId + "_", filePath);
        }

        // Renaming and modification of properties file.
        String oldPropsName = newAppFilePath + "/" + "appzillon-server" + "_" + oldAppId + ".properties";
        String newpropsName = newAppFilePath + "/" + "appzillon-server" + "_" + newAppId + ".properties";
        Utils.fileRename(oldPropsName, newpropsName);
        Utils.searchAndReplaceString(oldAppId, newAppId, newpropsName);

        // ISO xml files name changes and Generation and execution of sql script files.
        Utils.renameAndModifyFileContents(oldAppId, newAppId, newAppFilePath);

    }

}
