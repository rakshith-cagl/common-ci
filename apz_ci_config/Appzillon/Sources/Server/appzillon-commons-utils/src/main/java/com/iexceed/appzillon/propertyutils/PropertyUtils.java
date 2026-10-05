package com.iexceed.appzillon.propertyutils;


import com.iexceed.appzillon.exception.LoggerException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyUtils {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getLogger("com.iexceed.appzillon.rest");
    private static final String PROP_FILE_PREFIX = "appzillon-server";
    private static final String PROP_FILE_EXTENSION = ".properties";
    private static final String PROP_FILE_FOUND_MSG = "Properties file name found : ";
    private static Map<String, Properties> mapPropObj = new HashMap<>();
    private static Map<String, String> cloudPropMapObj = new HashMap<>();
    private static Map<String, String> cloudSettings = new HashMap<>();
    private static String cloudProvider;
    private static String ctxParamName;
    private static boolean isCloudConfigurationInitialized = false;

    private PropertyUtils() {

    }

    public static String getCtxParam() {
        return ctxParamName;
    }

    public static void setCtxParam(String ctxParamName) {
        PropertyUtils.ctxParamName = ctxParamName;
    }

    public static String getCloudProvider() {
        return cloudProvider;
    }

    public static void setCloudProvider(String cloudProvider) {
        PropertyUtils.cloudProvider = cloudProvider;
    }

    private static void loadProperties(String hAppId) {
        try {
            LOG.debug("[REST] => Loading appzillon-server" + "_" + hAppId + ".properties.");
            if (!mapPropObj.containsKey(hAppId)) {
                Properties propfile = new Properties();
                boolean isCloudProviderPresent = Utils.isNotNullOrEmpty(getCloudProvider()) && !"NONE".equalsIgnoreCase(getCloudProvider());
                String fPath;

                LOG.debug("Properties path in propertyUtils :" + Logger.propertiesPath);
                if ((Logger.propertiesPath != null && !"".equals(Logger.propertiesPath))) {
                    if (hAppId.equals("commons")) {
                        fPath = String.format("%s/%s/%s", Logger.propertiesPath, "META-INF", (PROP_FILE_PREFIX + "_" + hAppId + PROP_FILE_EXTENSION));
                    } else {
                        fPath = String.format("%s/%s/%s", Logger.propertiesPath, hAppId, (PROP_FILE_PREFIX + "_" + hAppId + PROP_FILE_EXTENSION));
                    }
                } else if (isCloudProviderPresent) {
                    fPath = String.format("%s/%s", hAppId, (PROP_FILE_PREFIX + "_" + hAppId + PROP_FILE_EXTENSION));
                    LOG.info("Cloud provider is present, loading externalize property file: {}", fPath);
                } else {
                    fPath = PROP_FILE_PREFIX + "_" + hAppId + PROP_FILE_EXTENSION;
                }
                try (InputStream is = PropertyUtils.class.getClassLoader().getResourceAsStream(fPath)) {
                    LOG.debug(PROP_FILE_FOUND_MSG + is);
                    propfile.load(is);
                }


                if (isCloudProviderPresent) {
                    if (hAppId.equals("commons")) {
                        LOG.debug("Loading cloud settings from prop file");
                        loadCloudSettings(propfile);
                    }
                    loadPropertiesFromCloudProvider(propfile);
                }
                mapPropObj.put(hAppId, propfile);
                LOG.debug("[REST] => properties utils initialized for app id : {} :: {}", hAppId, mapPropObj.get(hAppId));
            } else {
                LOG.debug("[REST] => Property utils for this " + hAppId + " app id is already initialized");
            }
        } catch (NullPointerException | IOException | IllegalArgumentException e) {
            LOG.error("Properties file not found");
            LoggerException logExp = LoggerException.getLoggerInstance();
            logExp.setMessage(logExp.getLogExceptionMessage(LoggerException.Code.APZ_LOG_000));
            logExp.setCode(LoggerException.Code.APZ_LOG_000.toString());
            logExp.setPriority("1");
            throw logExp;
        } catch (Exception e) {
            LOG.error("Exception occurred while loading properties :: ", e);
            throw e;
        }
    }

    private static void loadCloudSettings(Properties properties) {
        if (ServerConstants.CLOUD_AZURE.equalsIgnoreCase(getCloudProvider())) {
            //loading azure app config/vault details
            cloudSettings.put(ServerConstants.CLIENT_ID, properties.getProperty(ServerConstants.CLIENT_ID));
            cloudSettings.put(ServerConstants.CLIENT_SECRET, properties.getProperty(ServerConstants.CLIENT_SECRET));
            cloudSettings.put(ServerConstants.APP_CONFIG_URL, properties.getProperty(ServerConstants.APP_CONFIG_URL));
            cloudSettings.put(ServerConstants.AUTH_URL_KEY_VAULT, properties.getProperty(ServerConstants.AUTH_URL_KEY_VAULT));
            cloudSettings.put(ServerConstants.AUTH_URL_APP_CONFIG, properties.getProperty(ServerConstants.AUTH_URL_APP_CONFIG));

        }

    }

    private static void loadPropertiesFromCloudProvider(Properties properties) {
        //load configurations from cloud provider.
        LOG.debug("Loading property values from cloud provider");
        if (!isCloudConfigurationInitialized) {
            LOG.debug("Cloud properties is not initialized");
            CloudProviderService service = new CloudProviderService();
            if (ServerConstants.CLOUD_AZURE.equalsIgnoreCase(getCloudProvider())) {
                cloudPropMapObj = service.loadCloudProperties(new CloudProviderAzure());
            }
            isCloudConfigurationInitialized = true;
        } else {
            LOG.debug("Cloud properties already initialized");
        }

        properties.forEach((key, value) -> {
            if (cloudPropMapObj.containsKey(value.toString())) {
                properties.put(key.toString(), cloudPropMapObj.get(value.toString()));
            }
        });
    }

    public static String getPropValue(String appId, String key) {
        try {
            loadProperties(appId);
            return mapPropObj.get(appId).getProperty(key);
        } catch (NullPointerException npe) {
            LOG.warn("value is null for the key : {}", key);
            return "";
        }
    }

    public static String getCloudSetting(String appId, String key) {
        try {
            if (cloudSettings.isEmpty()) {
                loadProperties(appId);
            }
            return cloudSettings.get(key);
        } catch (NullPointerException npe) {
            LOG.warn("value is null for the key : {}", key);
            return "";
        }
    }

    public static void loadServerProperties() {
        LOG.info("Loading Server properties");
        //1.First load all the common properties 2.Load respective appId properties
        loadProperties(ServerConstants.SERVER_PROP_FILE_CONSTANT);
        String[] appIds = getCtxParam().split(",");
        for (String appId : appIds) {
            loadProperties(appId);
        }
        LOG.info("Server properties loaded successfully");
        cloudPropMapObj();
    }

    private static void cloudPropMapObj() {
        cloudPropMapObj.clear();
    }

    public static void resetPropertiesObjBeforeReload() {
        isCloudConfigurationInitialized = false;
        cloudSettings.clear();
        mapPropObj.clear();
        cloudPropMapObj();
    }

}
