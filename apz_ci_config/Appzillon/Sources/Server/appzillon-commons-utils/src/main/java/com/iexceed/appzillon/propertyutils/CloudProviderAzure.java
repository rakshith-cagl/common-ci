package com.iexceed.appzillon.propertyutils;

import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.securityutils.AuthTokenUtil;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class CloudProviderAzure implements ICloudProvider {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getLogger("com.iexceed.appzillon.rest");

    @Override
    public Map<String, String> loadCloudProperties() {
        LOG.debug("Loading setting from azure app configuration");
        Map<String, String> configurationMap = new HashMap<>();
        String result = AuthTokenUtil.getAppConfigValueList(ServerConstants.SERVER_PROP_FILE_CONSTANT);

        Assert.isTrue(result != null, "Cloud properties not found");
        JSONObject settings = new JSONObject(result);
        if (settings.has(ServerConstants.AZURE_ITEMS)) {
            JSONArray settingsList = settings.getJSONArray(ServerConstants.AZURE_ITEMS);
            for (int i = 0; i < settingsList.length(); i++) {
                JSONObject objects = settingsList.getJSONObject(i);
                if (objects.has("key") && objects.has("value"))
                    configurationMap.put(objects.getString("key"), objects.getString("value"));
            }
        }
        LOG.debug("configurationMap: {}", configurationMap);
        return configurationMap;
    }
}
