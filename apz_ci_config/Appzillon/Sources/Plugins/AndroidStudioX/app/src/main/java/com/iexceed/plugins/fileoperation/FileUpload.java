package com.iexceed.plugins.fileoperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.ServerUtilities;
import com.iexceed.common.StringUtils;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class FileUpload {

//    private Context mContext;

    private Activity mActivity;

    private WebView mWebView;

    private String callerId;

//    HttpEntity resEntity;

    private String lfileName;

    private String isSession;

    private String TAG = "UPLOAD TO SERVER";

    public FileUpload(WebView webview, ApzActivity activity) {
        mActivity = activity;
        mWebView = webview;
    }

    public void uploadFile(JSONObject uploadDetails) {
        String destination;
        String fileName;
        String override;

        try {
            callerId = uploadDetails.getString("id");
            destination = uploadDetails.getString("destination");
            isSession = uploadDetails.getString("sessionReq");
            fileName = uploadDetails.getString("fieldID");
            if (uploadDetails.has("fileOverride")) {
                override = uploadDetails.getString("fileOverride");
            } else {
                override = "N";
            }

            lfileName = fileName.substring(fileName.lastIndexOf('/') + 1);

            String path;// = Environment.getExternalStorageDirectory() + "";

            //Changes for OTA for internal memory Natasha 10-7-2018
            /*if("Y".equalsIgnoreCase(AppzillonMainScreen.activity.getResources().getString(R.string.INTERNALSANDBOX))){
                path = AppzillonMainScreen.SANDBOX_LOC;
            }else{
                path = Environment.getExternalStorageDirectory() + "";
            }
            if (fileName.contains(path)) {
                fileName = fileName.replace(path, "");
            }*/

        } catch (JSONException e1) {
            ApzLogger.e(TAG, e1.toString());
            JSONObject json = new JSONObject();
            try {
                json.put("errorCode", e1.toString());
            } catch (JSONException e11) {
                ApzLogger.e(TAG, e11.toString());
            }
            ApzPluginUtil.sendError(callerId, "APZ-CNT-077", json, mActivity,
                    mWebView, true);
            return;
        }
        //Changes for OTA for internal memory Natasha 10-7-2018
        File sourceFile ;//= new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
        if("Y".equalsIgnoreCase(AppzillonMainScreen.OTAREQUIRED)){
            sourceFile = new File(fileName); // new File(AppzillonMainScreen.SANDBOX_LOC + File.separator + fileName);
        }else{
            sourceFile = new File(fileName);//new File(Environment.getExternalStorageDirectory() + File.separator + fileName);

        }
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(sourceFile).toString());

        JSONObject appzillonBody = new JSONObject();
        JSONObject fileDetails = new JSONObject();
        JSONArray fileDetailsArray = new JSONArray();
        JSONObject jsonRequest = new JSONObject();
        try {
            appzillonBody.put("destination", destination);
            appzillonBody.put("overWrite", override);

            fileDetails.put("fileName", lfileName);
            fileDetails.put("fileType", extension);
            fileDetails.put("fileNo", "1");
            fileDetails.put("fileSize", sourceFile.length());
            fileDetailsArray.put(fileDetails);
            appzillonBody.put("fileDetails", fileDetailsArray);
            jsonRequest.put("appzillonBody", appzillonBody);
            jsonRequest.put("appzillonHeader", uploadDetails.getJSONObject("appzillonHeader"));
            doFileUpload(jsonRequest, sourceFile);
        } catch (JSONException e1) {
            ApzLogger.e(TAG, e1.toString());
        }
        if (!sourceFile.isFile()) {
            ApzPluginUtil.sendError(callerId, "APZ-CNT-002", null, mActivity, mWebView, true);
        }
    }

    private void uploadSuccess(final String successMsg) {
        JSONObject json = new JSONObject();
        try {
            json.put("successMessage", successMsg);
        } catch (JSONException e1) {
            ApzLogger.e(TAG, e1.toString());
        }
        ApzPluginUtil.sendSuccess(callerId, json, false, mActivity, mWebView, true);
    }

    private void uploadFailure(final String errorMsg) {
        JSONObject json = new JSONObject();
        try {
            json.put("errorCode", errorMsg);
        } catch (JSONException e1) {
            ApzLogger.e(TAG, e1.toString());
        }
        ApzPluginUtil.sendError(callerId, "APZ-CNT-007", json, mActivity, mWebView, true);
    }

    private void doFileUpload(JSONObject jsonReq, File file) {
        int serverResponseCode = 0;
        InputStream iStream = null;
        String serverResponseMessage;


        String upLoadServerUri = StringUtils.getString(StringUtils.SERVER_URL);
        String internalServerURL = upLoadServerUri + "/upload";
        try {

            JSONObject appzillonheader = jsonReq.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
            appzillonheader.put("serverNonce", AppzillonMainScreen.SNONCE);
            appzillonheader.put("clientNonce", System.currentTimeMillis()+"");
            appzillonheader.put("sessionToken",AppzillonMainScreen.SESSIONTOKEN);

//            HttpClient client = new DefaultHttpClient();
//            HttpPost post = new HttpPost(internalServerURL);
//            FileBody bin1 = new FileBody(file);
//            MultipartEntity reqEntity = new MultipartEntity();
//            reqEntity.addPart(lfileName, bin1);
//            reqEntity.addPart("appzillonRequest", new StringBody(jsonReq.toString()));
//            post.setEntity(reqEntity);
//            HttpResponse response = client.execute(post);
//            resEntity = response.getEntity();

//            final String response_str = EntityUtils.toString(resEntity);
//            if (resEntity != null) {

            URL url = new URL(internalServerURL);

            FileBody fileBody = new FileBody(file);
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart(lfileName, fileBody);
                reqEntity.addPart("appzillonRequest", new StringBody(jsonReq.toString()));

  
            if(internalServerURL.contains("https")){
             /*vapt change -replace with code starts */
 TRUSTALL_FILEUPLOAD
            /*vapt change -replace with code ends */
            }

           else {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true); // Allow Inputs
		conn.setDoOutput(true); // Allow Outputs
		conn.setUseCaches(false); // Don't use a Cached Copy
		conn.setChunkedStreamingMode(1024);
		conn.setReadTimeout(15000);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.addRequestProperty("Content-length", reqEntity.getContentLength() + "");
		conn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());
		OutputStream os = conn.getOutputStream();
		reqEntity.writeTo(conn.getOutputStream());
		os.close();
		conn.connect();
		// Responses from the server (code and message)
		serverResponseCode = conn.getResponseCode();
		serverResponseMessage = conn.getResponseMessage();
		iStream = conn.getInputStream();
	}  

            if (serverResponseCode == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(iStream));
                StringBuilder responseString = new StringBuilder();
                String next = "";
                while ((next = bufferedReader.readLine()) != null) {
                    // do nothing
                    responseString.append(next);

                }
                if (responseString != null) {

                    JSONObject uploadServerResponse = new JSONObject(responseString.toString());
                    JSONObject appzillonHeader = uploadServerResponse.getJSONObject("appzillonHeader");
                    boolean status = appzillonHeader.getBoolean("status");
                    if (status) {
                        JSONObject appzillonBody = uploadServerResponse.getJSONObject("appzillonBody");
                        JSONObject appzillonUploadResponse;
                        if (isSession.equalsIgnoreCase("Y")) {
                            appzillonUploadResponse = appzillonBody.getJSONObject("appzillonUploadFileResponse");
                        } else {
                            appzillonUploadResponse = appzillonBody.getJSONObject("appzillonUploadFileWSResponse");
                        }
                        String uploadStatus = appzillonUploadResponse.getString(lfileName);
                        if (uploadStatus.equalsIgnoreCase("success")) {
                            ApzLogger.d(TAG, "upload Success");
                            uploadSuccess("Upload Success");
                        } else {
                            ApzLogger.d(TAG, "upload already exists");
                            uploadSuccess("File Already Exists");
                        }
                    } else {
                        ApzLogger.e(TAG, "upload failed");
                        uploadFailure("Upload Failed");
                    }
                }
            }else{
                uploadFailure("Upload Failed");
            }
        } catch (Exception ex) {
            ApzLogger.e(TAG, ex.toString());
            JSONObject json = new JSONObject();
            try {
                json.put("errorMessage", ex.toString());
            } catch (JSONException e) {
            }
            ApzPluginUtil.sendError(callerId, "APZ-CNT-002", json, mActivity, mWebView, true);
        }
    }
}

