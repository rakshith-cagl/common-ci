package com.iexceed.webcontainer.vault;


import com.iexceed.webcontainer.utils.AppzillonConstants;
import static com.iexceed.webcontainer.utils.AppzillonConstants.*;
import com.iexceed.webcontainer.utils.PropertyUtils;
import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.WebProperties;
import com.iexceed.webcontainer.utils.json.JSONException;
import com.iexceed.webcontainer.utils.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
	 static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(AuthTokenUtil.class.getName());

    public static String getAuthTokenKeyVault()  {
        return  getAccessToken(new HttpPost(PropertyUtils.getPropertyValue(AUTH_URL_KEY_VAULT)), getNameValuePairs(AZURE_KEY_VAULT));
    }
    public static String getAuthTokenAppConfig()  {
        return getAccessToken(new HttpPost(PropertyUtils.getPropertyValue(AUTH_URL_APP_CONFIG)) , getNameValuePairs(AZURE_APP_CONFIG));
    }

    private static List<NameValuePair> getNameValuePairs(String type) {
        List<NameValuePair> formData = new ArrayList<NameValuePair>();
        formData.add(new BasicNameValuePair(AZURE_CLIENT_ID, PropertyUtils.getPropertyValue(CLIENT_ID)));
        formData.add(new BasicNameValuePair(AZURE_CLIENT_SECRET, PropertyUtils.getPropertyValue(CLIENT_SECRET)));

        formData.add(
                new BasicNameValuePair(AZURE_GRANT_TYPE, AZURE_GRANT_TYPE_CLIENT_CREDENTIALS));
        if(type.equalsIgnoreCase(AZURE_KEY_VAULT)){
            formData.add(new BasicNameValuePair(AZURE_SCOPE,AZURE_SCOPE_URL));
        }else if(type.equalsIgnoreCase(AZURE_APP_CONFIG)){
        	formData.add(new BasicNameValuePair(AZURE_RESOURCE,PropertyUtils.getPropertyValue(APP_CONFIG_URL)));
        }
        return formData;
    }

    private static String getAccessToken(HttpPost postRequest, List<NameValuePair> formData) {
        String accessToken = "";
        try {
            postRequest.setEntity(new UrlEncodedFormEntity(formData));
            JSONObject output = callServer(postRequest);
            accessToken = (String) output.get(AppzillonConstants.ACCESS_TOKEN);
        } catch (Exception e) {
            LOG.error("exception",e);
        }
        return accessToken;
    }

    public static String getSecretValue(String url,String token)  {
        HttpGet getRequest = new HttpGet(url+KEY_VAULT_API_VERSION);
        setAuthHeader(getRequest,token);
        getRequest.addHeader(AppzillonConstants.CONTENT_TYPE, AppzillonConstants.CONTENT_TYPE_VALUE);
       JSONObject output= callServer(getRequest);
       String value ="";
        if(output != null && output.has(AppzillonConstants.AZURE_VALUE)) {
            try{
                value= output.get(AppzillonConstants.AZURE_VALUE).toString();
            }catch (Exception e){
                e.printStackTrace();
            }
            return value ;
        }else if(output != null && output.has("error")) {
            return null;
        }
        return null;
    }

    public static HttpClient createClient() {
        try {
            org.apache.http.ssl.SSLContextBuilder builder = new SSLContextBuilder();
            builder.useProtocol(AppzillonConstants.HTTP_PROTOCOL_TLS);
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
            HttpClientBuilder hcBuilder = HttpClients.custom();
            return  hcBuilder.setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            LOG.error("exception",e);
        }
        return null;
    }

    public static String getAppConfigValue(String key,String token)  {
        HttpGet getRequest = new HttpGet(PropertyUtils.getPropertyValue(APP_CONFIG_URL)+"/kv/"+key+APP_CONFIG_API_VERSION);
        setAuthHeader(getRequest,token );
        getRequest.addHeader(AppzillonConstants.CONTENT_TYPE, AppzillonConstants.CONTENT_TYPE_VALUE);
        JSONObject output= callServer(getRequest);
        try {
            if(output!= null && output.get(AZURE_VALUE) !=null) {
                return output.get(AZURE_VALUE).toString();
            }else if(output != null && output.has(ERROR)) {
                //if token is expired it will return null based on our requirement need to get access token again
                LOG.error("fail to fetch appconfig value for key: "+key+"   with response:"+output);
                return null;
            }
        } catch (JSONException e) {
            LOG.error("exception",e);
        }
        return null;
    }


    public static JSONObject callServer(HttpUriRequest request) {
        HttpClient httpClient =  createClient();
        JSONObject response =null;
        try {
            HttpResponse httpClientResponse = httpClient.execute(request);
            String responseBody = EntityUtils.toString(httpClientResponse.getEntity(), StandardCharsets.UTF_8);
             response = new JSONObject(responseBody);
        } catch (ClientProtocolException e) {
            LOG.error("exception",e);
        } catch (IOException e) {
            LOG.error("exception",e);
        } catch (JSONException e) {
            LOG.error("exception",e);
        }
        return response;
    }



    public static void setAuthHeader(HttpUriRequest request, String token) {
        request.addHeader(AppzillonConstants.AUTHORIZATION, "Bearer " + token);
    }
    public static String getVaultUrl(String propValue){
        JSONObject value = null;
        String uri="";
        try {
            value = new JSONObject(propValue);
            uri =value.getString(URI);
        } catch (JSONException e) {
            LOG.error("exception",e);
        }
        return uri;
    }

    public static String getValueFromVault(String propName){
        return getSecretValue(getVaultUrl(PropertyUtils.getPropertyValue(propName)), getAuthTokenKeyVault());
    }

}

