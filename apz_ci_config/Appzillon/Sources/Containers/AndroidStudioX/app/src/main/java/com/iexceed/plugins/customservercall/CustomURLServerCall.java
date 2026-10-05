package com.iexceed.plugins.customservercall;

import android.app.Activity;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class CustomURLServerCall extends ApzPlugin{

    private static ApzPlugin pluginObj;
    private WebView mWebView;

    private Activity mActivity;

    private String serverURL;

    private String isAppzillon;

    private JSONObject httpHeader;

    private JSONObject request;

    static int TIME_OUT = 300 * 1000;

    public CustomURLServerCall(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new CustomURLServerCall(webView, activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {
        this.mWebView = webView;
        this.mActivity = activity;
        try {
            this.callbackId = params.getString("id");
            isAppzillon = params.getString("isAppzillon");
            serverURL = params.getString("url");
            httpHeader = params.getJSONObject("httpHeaders");
            request = params.getJSONObject("request");
                if (AppzillonUtils.isNetworkAvailable(this.mActivity)) {
                    new CustomServerCall(this.callbackId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,request.toString(), httpHeader.toString());
                } else {
                    final JSONObject json = new JSONObject();
                    ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-233", json, mActivity, mWebView, true);
                }
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
    }

    private class CustomServerCall extends AsyncTask<String, Void, JSONObject> {
        String callbackId = "";
        public CustomServerCall(String callbackId) {
            super();
            this.callbackId = callbackId;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject response = null;
            try {
                JSONObject request = new JSONObject(params[0]);
                JSONObject httpHeaders = new JSONObject(params[0]);
                response = sendRequestToCustomHTTPServer(serverURL, request.toString(), httpHeaders);
            } catch (Exception e) {
                return response;
            }
            return response;
        }

        @Override
        protected void onPostExecute(final JSONObject response) {
            super.onPostExecute(response);
            try {
                if(response != null && response.getString("status").equalsIgnoreCase("true")){
                    ApzPluginUtil.sendSuccess(this.callbackId, response, false, mActivity, mWebView, true);
                } else {
                    ApzPluginUtil.sendError(this.callbackId, "", response, mActivity, mWebView, true);
                }
            } catch (Exception e) {

            }

        }
    }

    private static JSONObject sendRequestToCustomHTTPServer(String URL, String request, JSONObject httpHeaders) {
        HttpURLConnection urlConnection = null;
        JSONObject responseJson = null;
        try {

            java.net.URL url = new URL(URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(TIME_OUT);
            urlConnection.setReadTimeout(TIME_OUT);

            if(null != httpHeaders){
                Iterator<String> iter = httpHeaders.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        urlConnection.setRequestProperty(key, httpHeaders.getString(key));
                    } catch (Exception e) {
                        // Something went wrong!
                    }
                }
            }

            urlConnection.connect();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());

            out.write(request);
            out.close();

            int HttpResult = urlConnection.getResponseCode();
            responseJson = new JSONObject();
            responseJson.put("httpCode",urlConnection.getResponseCode());

            JSONObject headerJson = new JSONObject();
            Map<String, List<String>> headerMap = urlConnection.getHeaderFields();
            for(String key:headerMap.keySet()){
                if (null != key) {
                    List<String> valuesList = headerMap.get(key);
                    if (valuesList != null) {
                        for(String value:valuesList){
                            headerJson.put(key,value);
                        }
                    }
                }
            }
            responseJson.put("httpHeaders",headerJson);
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                StringBuilder sb = new StringBuilder();
                BufferedReader br=null;
                if("gzip".equalsIgnoreCase(urlConnection.getContentEncoding())) {
                    br=new BufferedReader(new InputStreamReader(new GZIPInputStream((urlConnection.getInputStream()))));
                }else {
                    br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                }
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                String respString = sb.toString();
                responseJson.put("response",respString);
                responseJson.put("status","true");
            } else {
                responseJson.put("status","false");
            }
        }catch (Exception e) {
            try {
                responseJson.put("status","false");
            } catch (JSONException ex) {
            }
        }
        return responseJson;

    }

}
