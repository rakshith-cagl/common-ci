package com.iexceed.webcontainer.utils;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.servlet.ServletContext;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.startup.WebContextListener;
import com.iexceed.webcontainer.utils.hash.Utility;
import com.iexceed.webcontainer.utils.json.JSONException;
import com.iexceed.webcontainer.utils.json.JSONObject;
import com.iexceed.webcontainer.vault.AuthTokenUtil;

public class PropertyUtils {

	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(PropertyUtils.class.getName());

	private static Map<String, Properties> mapContainerProp = new HashMap<String, Properties>();

	private static Map<String, String> mapAppProp = new HashMap<String, String>();
	private static boolean isInstrumentKeyLoaded = false;

	public Properties loadContainerProperties(String propFileName) {
		Properties propfile = new Properties();
		LOG.debug("Loading properties file : " + propFileName);
		String fName;
		if (WebContextListener.propertiesPath != null && !"".equals(WebContextListener.propertiesPath)) {
			fName = WebContextListener.propertiesPath + "/" + propFileName + PROPERTIES_EXTN;
		} else {
			fName = propFileName + PROPERTIES_EXTN;
		}

		try(InputStream is = PropertyUtils.class.getClassLoader()
				.getResourceAsStream(fName)){
			propfile.load(is);
			mapContainerProp.put(propFileName, propfile);
			// update with appConfig
			String cloudProvider = mapContainerProp.get(propFileName).getProperty(CLOUD_PROVIDER);
			if (cloudProvider.equalsIgnoreCase(CLOUD_AZURE)) {
				Properties properties = mapContainerProp.get(propFileName);
				Set<String> stringEntry = properties.stringPropertyNames();
				String authToken = "";
				for (String key : stringEntry) {
					if (properties.getProperty(key).startsWith("azure_")) {
						LOG.info("key:  " + key);
						if (!Utility.isNotNullOrEmpty(authToken)) {
							authToken = AuthTokenUtil.getAuthTokenAppConfig();
						}
						loadFromAppConfig(propFileName, properties.getProperty(key), key, authToken);
					}
				}
			}
		} catch (IOException e) {
			LOG.error("IOException ", e);
		}
		return mapContainerProp.get(propFileName);
	}

	public Map<String, String> loadAppProperties(String propFileName, ServletContext servletContext) {
		if (!mapAppProp.containsKey(propFileName)) {
			LOG.debug("Loading properties file : " + propFileName);
			InputStream inStrm = servletContext.getResourceAsStream(APPS_PATH + WebProperties.getAppId() + CONFIG_PATH + APPPROPSJSON);
			String appProperty = Utility.getStringFromInputStream(inStrm);
			try {
				JSONObject appObj = new JSONObject(appProperty);
				Iterator<?> keys = appObj.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					String value = appObj.getString(key);
					mapAppProp.put(key, value);
				}
				WebProperties.setAppProps(appObj.toString());
			} catch (JSONException e) {
				LOG.error("JSONException ", e);
			}
		} else {
			LOG.debug("Property utils for this " + propFileName + " app id is already intialized");
		}
		return mapAppProp;
	}

	/**
	 * Initializes Webcontainer and App properties along with Default Settings.
	 *
	 * @param servletContext
	 */
	public static void initializeProperties(ServletContext servletContext) {
		//Loading Container Properties
		String cloudName = PropertyUtils.getPropertyValue(CLOUD_LOGGING);

		if (!isInstrumentKeyLoaded && (Utility.isNotNullOrEmpty(cloudName) && cloudName.equalsIgnoreCase(CLOUD_AZURE))) {
			loadInstrumentationKey();
		}

		LOG.info("Loading Container Properties....");
		initContainerProperties(CONTAINER_PROPERTIES, servletContext);
		//Loading Application Properties		
		LOG.info("Loading Application Properties....");
		//initAppProperties(APP_PROPERTIES, servletContext);
		// Loading Default Settings
		LOG.info("Initializing Default Settings....");
		PropertyUtils.loadSettings(servletContext);
		//setAppzillonContextpath(servletContext);
	}

	/**
	 * Initializing Application Properties
	 *
	 * @param appProp
	 * @param servletContext
	 */
	public static void initAppProperties(String appProp, ServletContext servletContext) {
		PropertyUtils propUtils = new PropertyUtils();
		//Setting app properties
		Map<String, String> appProperties = propUtils.loadAppProperties(appProp, servletContext);
		WebProperties.setServerURL(appProperties.get(SERVER_URL));
		WebProperties.setExpiryDate(appProperties.get(APP_EXPIRY_DATE));
		WebProperties.setServerToken(appProperties.get(SERVER_TOKEN));
		WebProperties.setAuthenticationType(appProperties.get(AUTHENTICATION_TYPE));
	}

	/**
	 * Initializing Container Properties
	 *
	 * @param containerProp
	 * @param servletContext
	 */
	private static void initContainerProperties(String containerProp, ServletContext servletContext) {
		PropertyUtils propUtils = new PropertyUtils();
		Properties containerProps = propUtils.loadContainerProperties(containerProp);
		//Setting required WebContainer properties
		WebProperties.setServerURL(containerProps.getProperty(SERVER_URL));
		WebProperties.setExpiryDate(containerProps.getProperty(APP_EXPIRY_DATE));
		WebProperties.setServerToken(containerProps.getProperty(SERVER_TOKEN));
		WebProperties.setAuthenticationType(containerProps.getProperty(AUTHENTICATION_TYPE));
		WebProperties.setUserSettingsPath(containerProps.getProperty(USER_SETTINGS_PATH));
		WebProperties.setLogPath(containerProps.getProperty(LOG_PATH));
		WebProperties.setCookieAge(containerProps.getProperty(COOKIE_AGE));
		WebProperties.setKeepMeSignedInEnabled(containerProps.getProperty(KEEP_ME_SIGNED_IN_ENABLED));
		WebProperties.setSecureCookie(containerProps.getProperty(SECURE_COOKIE));
		WebProperties.setAppId(containerProps.getProperty(MAIN_APP_ID));
		WebProperties.setOverrideOTP(containerProps.getProperty(OVERRIDE_OTP));
		WebProperties.setSessionTimeOut(containerProps.getProperty(SESSION_TIMEOUT));
		WebProperties.setSignedIn(containerProps.getProperty(SIGNEDIN));
		WebProperties.setKeyStorePath(containerProps.getProperty(KEY_STORE_PATH));
		WebProperties.setKeyStorePassword(containerProps.getProperty(KEY_STORE_PASSWORD));
		WebProperties.setTrustStorePath(containerProps.getProperty(TRUST_STORE_PATH));
		WebProperties.setTrustStorePassword(containerProps.getProperty(TRUST_STORE_PASSWORD));
		WebProperties.setDataIntegrity(containerProps.getProperty(DATA_INTEGRITY));
		WebProperties.setPayloadEncryptionReq(containerProps.getProperty(ENCRYPTION_FLAG));
		WebProperties.setEncryptionKeyFileName(containerProps.getProperty(ENCRYPTION_KEY_FILENAME));
		WebProperties.setIfaceIdInUri(containerProps.getProperty(IFACE_ID_IN_URI));
		WebProperties.setReadPEMFromAzure(containerProps.getProperty(AppzillonConstants.READ_PEM_FROM_VAULT));
		WebProperties.setAzureClientId(containerProps.getProperty(CLIENT_ID));
		WebProperties.setAzureClientSecret(containerProps.getProperty(CLIENT_SECRET));
		WebProperties.setAzureVaultUrl(containerProps.getProperty(VAULT_URL));
		WebProperties.setAzureTenantId(containerProps.getProperty(AUTH_URL_KEY_VAULT));
		WebProperties.setAzureLoggingReq(containerProps.getProperty(CLOUD_LOGGING));
		WebProperties.setAzureInstrumentationKey(containerProps.getProperty(INSTRUMENTATION_KEY));
		WebProperties.setAadRedirectUri(containerProps.getProperty(AAD_REDIRECT_URI));
		WebProperties.setAadB2cSignInPolicy(containerProps.getProperty(AAD_B2C_SIGN_IN_POLICY));
		WebProperties.setAadB2cPwdResetPolicy(containerProps.getProperty(AAD_B2C_PWD_RESET_POLICY));
		WebProperties.setAadB2cRedirectUri(containerProps.getProperty(AAD_B2C_REDIRECTURI));
		WebProperties.setIsThirdPartyAuthEnabled(containerProps.getProperty(THIRD_PARTY_AUTH_ENABLED));
		WebProperties.setAuthProvider(containerProps.getProperty(AUTH_PROVIDER));
		WebProperties.setSafeBit(containerProps.getProperty(SAFE_BIT));
		WebProperties.setUrlEncryptionReq(containerProps.getProperty(URL_ENCRYPTION_REQ));
		WebProperties.setUrlSafeKey(containerProps.getProperty(URL_KEY_SAFE));
		WebProperties.setCsrfPrevention(containerProps.getProperty(CSRF_PREVENTION));
		if(Utility.isExternalAuthEnabled()){
			//Load third party auth provider details from vault
			loadThirdPartyAuthPropsFromKeyVault();
			if(WebProperties.getAuthProvider().equalsIgnoreCase(AUTH_PROVIDER_KEYCLOAK)){
				WebProperties.setEidpRedirectUri(containerProps.getProperty(EDIP_REDIRECT_URI));
			}
		}

	}

	public static void setLoadDefaultSettings(String loadDefaultSettings) {
		WebProperties.setLoadDefaultSettings(loadDefaultSettings);
	}

	public static void loadSettings(ServletContext pSrvletCtx) {
		LOG.info("Setting Default/User Setting Path ...");
		setSettingsPath(pSrvletCtx);
		try {
			PropertyUtils.setLoadDefaultSettings((FileUtils.readUserSettings(DEFAULT_SETTINGS,
					pSrvletCtx)).toString());
		} catch (IOException e) {
			LOG.error("IOException", e);
		}
	}

	private static void setSettingsPath(ServletContext pSrvletCtx) {
		LOG.debug("Setting Appzillon web context path");
		String lSettingsPath = WebProperties.getUserSettingsPath();
		if (!(lSettingsPath == null) && !("".equals(lSettingsPath))) {
			WebProperties.setSettingsPath(lSettingsPath);
		} else {
			String ctxtName = pSrvletCtx.getContextPath();
			WebProperties.setSettingsPath(pSrvletCtx.getRealPath(ctxtName));
		}
		if ((!WebProperties.getSettingsPath().endsWith(FWDSLASH)) && (!WebProperties.getSettingsPath()
				.endsWith(BWDSLASH))) {
			WebProperties.setSettingsPath(WebProperties.getSettingsPath() + File.separator);
		}
	}

	public static String getPropertyValue(String propertyKey) {
		PropertyUtils propUtils = new PropertyUtils();
		if (!mapContainerProp.containsKey(CONTAINER_PROPERTIES))
			propUtils.loadContainerProperties(CONTAINER_PROPERTIES);
		Properties lProperties = mapContainerProp.get(CONTAINER_PROPERTIES);
		LOG.debug("Property key : " + propertyKey + " and value : " + lProperties.getProperty(propertyKey));
		return lProperties.getProperty(propertyKey);
	}

	private static void loadInstrumentationKey() {

		String secret = "";
		try {
			secret = AuthTokenUtil.getValueFromVault(INSTRUMENTATION_KEY);
		} catch (Exception e) {
			LOG.error("Exception while getting instrumentation key from azure", e);
		}
		System.setProperty(INSTRUMENTATION_KEY, secret);
		isInstrumentKeyLoaded = true;
	}

	private void loadFromAppConfig(String appId, String propertyValue, String propertyKey, String authToken) {
		String propertyFromAppConfig = AuthTokenUtil.getAppConfigValue(propertyValue, authToken);
		mapContainerProp.get(appId).put(propertyKey, propertyFromAppConfig);
	}

	public static void loadThirdPartyAuthPropsFromKeyVault() {
		String  cloudProvider = PropertyUtils.getPropertyValue(CLOUD_PROVIDER);
		if (Utility.isNotNullOrEmpty(cloudProvider) && CLOUD_AZURE.equalsIgnoreCase(cloudProvider)) {
			LOG.info("Third party auth is enabled, loading auth provider details...");
			String authProvider = PropertyUtils.getPropertyValue(AUTH_PROVIDER);
			if (AUTH_PROVIDER_AAD.equalsIgnoreCase(authProvider)) {
				WebProperties.setAadClientId(AuthTokenUtil.getValueFromVault(AAD_CLIENT_ID));
				WebProperties.setAadClientSecret(AuthTokenUtil.getValueFromVault(AAD_CLIENT_SECRETE));
				WebProperties.setAadTenantId(AuthTokenUtil.getValueFromVault(AAD_TENANT_ID));
			} else if (AUTH_PROVIDER_AAD_B2C.equalsIgnoreCase(authProvider)) {
				WebProperties.setAadB2cClientId(AuthTokenUtil.getValueFromVault(AAD_B2C_CLIENT_ID));
				WebProperties.setAadB2cClientSecret(AuthTokenUtil.getValueFromVault(AAD_B2C_SECRET));
				WebProperties.setAadB2cDomainName(AuthTokenUtil.getValueFromVault(AAD_B2C_DOMAIN_NAME));
			} else {
				LOG.error("No auth provider found");
			}
		}
	}

	public static String getFusionChartKey(){
		LOG.debug("Loading fusion chart key");
		String  cloudProvider = PropertyUtils.getPropertyValue(CLOUD_PROVIDER);
		String  fusionChartKey = PropertyUtils.getPropertyValue(FUSION_CHART_KEY);
		if(CLOUD_AZURE.equalsIgnoreCase(cloudProvider) && Utility.isNotNullOrEmpty(fusionChartKey)){
			LOG.debug("Loading fusion chart key from vault");
			return AuthTokenUtil.getValueFromVault(FUSION_CHART_KEY);
		}
		return fusionChartKey;
	}

}
