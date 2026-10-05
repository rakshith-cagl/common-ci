package com.iexceed.webcontainer.utils;

import javax.servlet.ServletContext;

/**
 * 
 * @author arthanarisamy
 *
 */
public class WebProperties {
	private static String KEY_STORE_PATH;
	private static String KEY_STORE_PASSWORD;
	private static String TRUST_STORE_PATH;
	private static String TRUST_STORE_PASSWORD;
	private static String loadDefaultSettings = "";	
	private static String serverContextPath = "";
	private static ServletContext servletContext;
	private static String settingsPath = "";
	private static String serverURL;
	private static String userSettingsPath;
	private static String logPath;
	private static String AppId;
	private static String expiryDate;
	private static String serverToken;
	private static String authenticationType;
	private static String jndiSource;
	private static String overrideOTP;
	private static String otpClassName;
	private static String sessionTimeOut;
	private static String signedIn;
	private static String twitterOauthConsumerKey;
	private static String twitterOauthConsumerSecret;
	private static String appProps;
	private static String dataIntegrity;
	private static String csrfPrevention;
	private static String payloadEncryptionReq;
	private static String encryptionKeyFileName;
	private static String cookieAge;
	private static String keepMeSignedInEnabled;
	private static String secureCookie;
	private static String IfaceIdInUri;
	private static String readPEMFromAzure;
	private static String vaultName;
	private static String azureClientId;
	private static String azureTenantId;
	private static String azureClientSecret;
	private static String azureVaultUrl;
	private static String azureLoggingReq;
	private static String azureInstrumentationKey;

	private static String aadClientId;
	private static String aadClientSecret;
	private static String aadRedirectUri;
	private static String aadTenantId;

	private static String aadB2cDomainName;
	private static String aadB2cSignInPolicy;
	private static String aadB2cPwdResetPolicy;
	private static String aadB2cClientId;
	private static String aadB2cClientSecret;
	private static String aadB2cRedirectUri;

	private static String safeBit;

	private static String isThirdPartyAuthEnabled;
	private static String authProvider;
	private static String eidpRedirectUri;
	
	private static String urlEncryptionReq;
	private static String urlSafeKey;

	public static String getEidpRedirectUri() {
		return eidpRedirectUri;
	}

	public static void setEidpRedirectUri(String eidpRedirectUri) {
		WebProperties.eidpRedirectUri = eidpRedirectUri;
	}

	public static String getIsThirdPartyAuthEnabled() {
		return isThirdPartyAuthEnabled;
	}

	public static void setIsThirdPartyAuthEnabled(String isThirdPartyAuthEnabled) {
		WebProperties.isThirdPartyAuthEnabled = isThirdPartyAuthEnabled;
	}

	public static String getAuthProvider() {
		return authProvider;
	}

	public static void setAuthProvider(String authProvider) {
		WebProperties.authProvider = authProvider;
	}

	public static String getAppProps() {
		return appProps;
	}
	public static void setAppProps(String props) {
		WebProperties.appProps = props;
	}
	
	public static String getLoadDefaultSettings() {
		return loadDefaultSettings;
	}
	public static void setLoadDefaultSettings(String loadDefaultSettings) {
		WebProperties.loadDefaultSettings = loadDefaultSettings;
	}
	public static String getServerContextPath() {
		return serverContextPath;
	}
	public static void setServerContextPath(String serverContextPath) {
		WebProperties.serverContextPath = serverContextPath;
	}
	
	public static ServletContext getServletContext() {
		return servletContext;
	}
	public static void setServletContext(ServletContext servletContext) {
		WebProperties.servletContext = servletContext;
	}
		
	public static String getSettingsPath() {
		return settingsPath;
	}
	public static void setSettingsPath(String settingsPath) {
		WebProperties.settingsPath = settingsPath;
	}

	public static String getServerURL() {
		if ("Y".equals(WebProperties.getUrlEncryptionReq())) {
			return AppzillonAESUtils.getDecryptedServerUrl(WebProperties.getUrlSafeKey(), serverURL);
		} else {
			return serverURL;
		}
	}
	
	public static void setServerURL(String serverURL) {
		WebProperties.serverURL = serverURL;
	}
	public static String getUserSettingsPath() {
		return userSettingsPath;
	}
	public static void setUserSettingsPath(String userSettingsPath) {
		WebProperties.userSettingsPath = userSettingsPath;
	}
	public static String getAppId() {
		return AppId;
	}
	public static void setAppId(String appId) {
		AppId = appId;
	}
	public static String getExpiryDate() {
		return expiryDate;
	}
	public static void setExpiryDate(String expiryDate) {
		WebProperties.expiryDate = expiryDate;
	}
	public static String getServerToken() {
		return serverToken;
	}
	public static void setServerToken(String serverToken) {
		WebProperties.serverToken = serverToken;
	}
	public static String getJndiSource() {
		return jndiSource;
	}
	public static void setJndiSource(String jndiSource) {
		WebProperties.jndiSource = jndiSource;
	}
	public static String getOverrideOTP() {
		return overrideOTP;
	}
	public static void setOverrideOTP(String overrideOTP) {
		WebProperties.overrideOTP = overrideOTP;
	}
	public static String getOtpClassName() {
		return otpClassName;
	}
	public static void setOtpClassName(String otpClassName) {
		WebProperties.otpClassName = otpClassName;
	}
	public static String getSessionTimeOut() {
		return sessionTimeOut;
	}
	public static void setSessionTimeOut(String sessionTimeOut) {
		WebProperties.sessionTimeOut = sessionTimeOut;
	}
	public static String getSignedIn() {
		return signedIn;
	}
	public static void setSignedIn(String signedIn) {
		WebProperties.signedIn = signedIn;
	}
	public static String getTwitterOauthConsumerKey() {
		return twitterOauthConsumerKey;
	}
	public static void setTwitterOauthConsumerKey(String twitterOauthConsumerKey) {
		WebProperties.twitterOauthConsumerKey = twitterOauthConsumerKey;
	}
	public static String getTwitterOauthConsumerSecret() {
		return twitterOauthConsumerSecret;
	}
	public static void setTwitterOauthConsumerSecret(String twitterOauthConsumerSecret) {
		WebProperties.twitterOauthConsumerSecret = twitterOauthConsumerSecret;
	}
	public static void setKeyStorePath(String KEY_STORE_PATH) {
		WebProperties.KEY_STORE_PATH = KEY_STORE_PATH;
	}
	public static String getKeyStorePath() {
		return KEY_STORE_PATH;
	}
	public static void setKeyStorePassword(String KEY_STORE_PASSWORD) {
		WebProperties.KEY_STORE_PASSWORD = KEY_STORE_PASSWORD;
	}
	public static String getKeyStorePassword() {
		return KEY_STORE_PASSWORD;
	}
	public static void setTrustStorePath(String TRUST_STORE_PATH) {
		WebProperties.TRUST_STORE_PATH = TRUST_STORE_PATH;
	}
	public static String getTrustStorePath() {
		return TRUST_STORE_PATH;
	}
	public static void setTrustStorePassword(String TRUST_STORE_PASSWORD) {
		WebProperties.TRUST_STORE_PASSWORD = TRUST_STORE_PASSWORD;
	}
	public static String getTrustStorePassword() {
		return TRUST_STORE_PASSWORD;
	}
	public static String getAuthenticationType() {
		return authenticationType;
	}
	public static void setAuthenticationType(String authenticationType) {
		WebProperties.authenticationType = authenticationType;
	}
	public static String getLogPath() {
		return logPath;
	}
	public static void setLogPath(String logPath) {
		WebProperties.logPath = logPath;
	}
	
	public static String getDataIntegrity() {
		return dataIntegrity;
	}
	public static String getCsrfPrevention() {	return csrfPrevention;	}
	public static void setCsrfPrevention(String csrfPrevention) {	WebProperties.csrfPrevention = csrfPrevention;	}

	public static void setDataIntegrity(String dataIntegrity) {
		WebProperties.dataIntegrity = dataIntegrity;
	}
	
	public static String getPayloadEncryptionReq() {
		return payloadEncryptionReq;
	}
	public static void setPayloadEncryptionReq(String payloadEncryptionReq) {
		WebProperties.payloadEncryptionReq = payloadEncryptionReq;
	}
	public static String getEncryptionKeyFileName() {
		return encryptionKeyFileName;
	}
	public static void setEncryptionKeyFileName(String encryptionKeyFileName) {
		WebProperties.encryptionKeyFileName = encryptionKeyFileName;
	}
	public static String getCookieAge() {
		return cookieAge;
	}
	public static void setCookieAge(String cookieAge) {
		WebProperties.cookieAge = cookieAge;
	}
	public static String getKeepMeSignedInEnabled() {
		return keepMeSignedInEnabled;
	}
	public static void setKeepMeSignedInEnabled(String keepMeSignedInEnabled) {
		WebProperties.keepMeSignedInEnabled = keepMeSignedInEnabled;
	}

	public static String getIfaceIdInUri() {
		return IfaceIdInUri;
	}

	public static void setIfaceIdInUri(String ifaceIdInUri) {

		WebProperties.IfaceIdInUri = ifaceIdInUri;
	}

	public static String getReadPEMFromAzure() {
		return readPEMFromAzure;
	}

	public static void setReadPEMFromAzure(String readPEMFromAzure) {
		WebProperties.readPEMFromAzure = readPEMFromAzure;
	}

	public static String getVaultName() {
		return vaultName;
	}

	public static void setVaultName(String vaultName) {
		WebProperties.vaultName = vaultName;
	}

	public static String getAzureClientId() {
		return azureClientId;
	}

	public static void setAzureClientId(String azureClientId) {
		WebProperties.azureClientId = azureClientId;
	}

	public static String getAzureTenantId() {
		return azureTenantId;
	}

	public static void setAzureTenantId(String azureTenantId) {
		WebProperties.azureTenantId = azureTenantId;
	}

	public static String getAzureClientSecret() {
		return azureClientSecret;
	}

	public static void setAzureClientSecret(String azureClientSecret) {
		WebProperties.azureClientSecret = azureClientSecret;
	}

	public static String getAzureVaultUrl() {
		return azureVaultUrl;
	}

	public static void setAzureVaultUrl(String azureVaultUrl) {
		WebProperties.azureVaultUrl = azureVaultUrl;
	}

	public static String getSecureCookie() {
		return secureCookie;
	}
	public static void setSecureCookie(String secureCookie) {
		WebProperties.secureCookie = secureCookie;
	}

	public static String getAzureLoggingReq() {
		return azureLoggingReq;
	}

	public static void setAzureLoggingReq(String azureLoggingReq) {
		WebProperties.azureLoggingReq = azureLoggingReq;
	}

	public static String getAzureInstrumentationKey() {
		return azureInstrumentationKey;
	}

	public static void setAzureInstrumentationKey(String azureInstrumentationKey) {
		WebProperties.azureInstrumentationKey = azureInstrumentationKey;
	}
	public static String getAadClientId() {
		return aadClientId;
	}
	public static void setAadClientId(String aadClientId) {
		WebProperties.aadClientId = aadClientId;
	}
	public static String getAadClientSecret() {
		return aadClientSecret;
	}
	public static void setAadClientSecret(String aadClientSecret) {
		WebProperties.aadClientSecret = aadClientSecret;
	}
	public static String getAadRedirectUri() {
		return aadRedirectUri;
	}
	public static void setAadRedirectUri(String aadRedirectUri) {
		WebProperties.aadRedirectUri = aadRedirectUri;
	}
        public static String getSafeBit() {
		return safeBit;
	}

	public static void setSafeBit(String safeBit) {
		WebProperties.safeBit = safeBit;
	}

	public static String getAadB2cDomainName() {
		return aadB2cDomainName;
	}

	public static void setAadB2cDomainName(String aadB2cDomainName) {
		WebProperties.aadB2cDomainName = aadB2cDomainName;
	}

	public static String getAadB2cSignInPolicy() {
		return aadB2cSignInPolicy;
	}

	public static void setAadB2cSignInPolicy(String aadB2cSignInPolicy) {
		WebProperties.aadB2cSignInPolicy = aadB2cSignInPolicy;
	}

	public static String getAadB2cPwdResetPolicy() {
		return aadB2cPwdResetPolicy;
	}

	public static void setAadB2cPwdResetPolicy(String aadB2cPwdResetPolicy) {
		WebProperties.aadB2cPwdResetPolicy = aadB2cPwdResetPolicy;
	}

	public static String getAadB2cClientId() {
		return aadB2cClientId;
	}

	public static void setAadB2cClientId(String aadB2cClientId) {
		WebProperties.aadB2cClientId = aadB2cClientId;
	}

	public static String getAadB2cClientSecret() {
		return aadB2cClientSecret;
	}

	public static void setAadB2cClientSecret(String aadB2cClientSecret) {
		WebProperties.aadB2cClientSecret = aadB2cClientSecret;
	}

	public static String getAadB2cRedirectUri() {
		return aadB2cRedirectUri;
	}

	public static void setAadB2cRedirectUri(String aadB2cRedirectUri) {
		WebProperties.aadB2cRedirectUri = aadB2cRedirectUri;
	}

	public static String getAadTenantId() {
		return aadTenantId;
	}

	public static void setAadTenantId(String aadTenantId) {
		WebProperties.aadTenantId = aadTenantId;
	}

	public static String getUrlEncryptionReq() {
		return urlEncryptionReq;
	}

	public static void setUrlEncryptionReq(String urlEncryptionReq) {
		WebProperties.urlEncryptionReq = urlEncryptionReq;
	}

	public static String getUrlSafeKey() {
		return urlSafeKey;
	}

	public static void setUrlSafeKey(String urlSafeKey) {
		WebProperties.urlSafeKey = urlSafeKey;
	}
}
