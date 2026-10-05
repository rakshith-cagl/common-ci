package com.iexceed.appzillon.securityutils;

import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;


public class AuthTokenUtil {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getLogger("com.iexceed.appzillon.rest");

    private AuthTokenUtil() {

    }

    public static String getAuthTokenKeyVault(String appId) {
        return getAccessToken(new HttpPost(PropertyUtils.getCloudSetting(appId, ServerConstants.AUTH_URL_KEY_VAULT)), getNameValuePairs(ServerConstants.AZURE_KEY_VAULT, appId));
    }

    public static String getAuthTokenAppConfig(String appId) {
        return getAccessToken(new HttpPost(PropertyUtils.getCloudSetting(appId, ServerConstants.AUTH_URL_APP_CONFIG)), getNameValuePairs(ServerConstants.AZURE_APP_CONFIG, appId));
    }

    private static List<NameValuePair> getNameValuePairs(String type, String appId) {
        List<NameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair(ServerConstants.AZURE_CLIENT_ID, PropertyUtils.getCloudSetting(appId, ServerConstants.CLIENT_ID)));
        formData.add(new BasicNameValuePair(ServerConstants.AZURE_CLIENT_SECRET, PropertyUtils.getCloudSetting(appId, ServerConstants.CLIENT_SECRET)));

        formData.add(
                new BasicNameValuePair(ServerConstants.AZURE_GRANT_TYPE, ServerConstants.AZURE_GRANT_TYPE_CLIENT_CREDENTIALS));
        if (type.equalsIgnoreCase(ServerConstants.AZURE_KEY_VAULT)) {
            formData.add(new BasicNameValuePair(ServerConstants.AZURE_SCOPE, ServerConstants.AZURE_SCOPE_URL));
        } else if (type.equalsIgnoreCase(ServerConstants.AZURE_APP_CONFIG)) {
            formData.add(new BasicNameValuePair(ServerConstants.AZURE_RESOURCE, PropertyUtils.getCloudSetting(appId, ServerConstants.APP_CONFIG_URL)));
        }
        return formData;
    }

    private static String getAccessToken(HttpPost postRequest, List<NameValuePair> formData) {
        String accessToken = "";
        try {
            postRequest.setEntity(new UrlEncodedFormEntity(formData));
            JSONObject output = callServer(postRequest);
            accessToken = output != null ? (String) output.get(ServerConstants.ACCESS_TOKEN) : "";
        } catch (Exception e) {
            LOG.error("error while doing getAccessToken", e);
        }
        return accessToken;
    }

    public static String getSecretValue(String url, String token) {
        HttpGet getRequest = new HttpGet(url + ServerConstants.KEY_VAULT_API_VERSION);
        setAuthHeader(getRequest, token);
        getRequest.addHeader(ServerConstants.CONTENT_TYPE, ServerConstants.CONTENT_TYPE_VALUE);
        JSONObject output = callServer(getRequest);
        if (output != null && output.get(ServerConstants.AZURE_VALUE) != null) {
            return output.get(ServerConstants.AZURE_VALUE).toString();
        } else if (output != null && output.has(ServerConstants.ERROR)) {
            //if token is expired it will return null based on our requirement need to get access token again
            LOG.error("Error while getting the secret value from vault :: " + output.get(ServerConstants.ERROR));
            return null;
        }
        return null;
    }

    public static String getAppConfigValue(String key, String token, String appId) {
        HttpGet getRequest = new HttpGet(PropertyUtils.getCloudSetting(appId, ServerConstants.APP_CONFIG_URL) + "/kv/" + key + ServerConstants.APP_CONFIG_API_VERSION);
        setAuthHeader(getRequest, token);
        getRequest.addHeader(ServerConstants.CONTENT_TYPE, ServerConstants.CONTENT_TYPE_VALUE);
        JSONObject output = callServer(getRequest);
        if (output != null && output.get(ServerConstants.AZURE_VALUE) != null) {
            return output.get(ServerConstants.AZURE_VALUE).toString();
        } else if (output != null && output.has(ServerConstants.ERROR)) {
            //if token is expired it will return null based on our requirement need to get access token again
            LOG.error("fail to fetch appconfig value for key: " + key + "   with response:" + output);
            return null;
        }
        return null;
    }

    public static String getAppConfigValueList(String appId) {
        HttpGet getRequest = new HttpGet(PropertyUtils.getCloudSetting(appId, ServerConstants.APP_CONFIG_URL) + "/kv" + ServerConstants.APP_CONFIG_API_VERSION);
        setAuthHeader(getRequest, AuthTokenUtil.getAuthTokenAppConfig(appId));
        getRequest.addHeader(ServerConstants.CONTENT_TYPE, ServerConstants.CONTENT_TYPE_VALUE);
        JSONObject output = callServer(getRequest);
        if (output == null) {
            return null;
        }
        if (output.has(ServerConstants.ERROR)) {
            LOG.error("Failed to get the configurations list from App Config :: {}", output);
        }
        return output.toString();
    }


    public static HttpClient createClient() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.setProtocol(ServerConstants.HTTP_PROTOCOL_TLS);
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
            HttpClientBuilder hcBuilder = HttpClients.custom();
            return hcBuilder.setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            LOG.error("error while creating http client", e);
            return null;
        }

    }

    public static JSONObject callServer(HttpUriRequest request) {
        HttpClient httpClient = createClient();
        JSONObject response = null;
        try {
            if (httpClient != null) {
                HttpResponse httpClientResponse = httpClient.execute(request);
                String responseBody = EntityUtils.toString(httpClientResponse.getEntity(), StandardCharsets.UTF_8);
                response = new JSONObject(responseBody);
            }
        } catch (JSONException | IOException e) {
            LOG.error("error while doing callServer", e);
        }
        return response;
    }

    private static void setAuthHeader(HttpUriRequest request, String token) {
        request.addHeader(ServerConstants.AUTHORIZATION, "Bearer " + token);
    }

    public static String getVaultUrl(String propValue) {
        JSONObject value = new JSONObject(propValue);
        return value.getString(ServerConstants.URI);
    }

}

