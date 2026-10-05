package com.iexceed.common;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringEscapeUtils;

import static com.iexceed.common.CommonUtilities.displayMessage;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

//	private static SharedPreferences settings;

    final static String properties = "USER_PREFS";

    static BluetoothAdapter mBluetoothAdapter;

    public static String safeBit ="0";

    static String devicename;

    static String TAG = "ServerUtilities";

    static int TIME_OUT = 300 * 1000; // changed from 5 to 60 seconds

    static SSLContext sc = null;

    final static String app_props = "APP_PREFS";

    private final static char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8','9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static String DATAINTEGRITY;

    private static String PAYLOAD_ENCRYPTION = "";

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    public static boolean register(final Context context, final String regId) {

        final String SERVER_URL = StringUtils.getString(StringUtils.SERVER_URL);

//		Log.i(TAG, "registering device regId : " + regId);

        final String device_id = AppzillonUtils.getDeviceId(context);
        displayMessage(context, context.getString(R.string.server_registering));
        if (SERVER_URL != null) {
            boolean serverflag = post(SERVER_URL, device_id, regId, context);
            if (serverflag == true) {
                final String message = context.getString(R.string.server_registered);
                CommonUtilities.displayMessage(context, message);

				/*Natasha Dawra 5/6/2017
                Setting the Shared preference key as true once the Notification
				registration is done
				 */

                SharedPreferences apps = context.getSharedPreferences(app_props, 0);
                UserSettings.setIsNotificationRegistered(StringUtils.getString(StringUtils.APP_ID), "true", apps);
                return true;
            } else {
                post(SERVER_URL, device_id, regId, context);
                final String message = context.getString(R.string.server_register_error);
                CommonUtilities.displayMessage(context, message);
                return false;
            }
        } else {
            return false;
        }


    }

    /**
     * Unregister this account/device pair within the server.
     */
    public static void unregister(final Context context, final String regId) {
//		Log.i(TAG, "unregistering device regId = " + regId);

        // final String SERVER_URL = context.getResources().getString(R.string.server_url);
        final String SERVER_URL = StringUtils.getString(StringUtils.SERVER_URL);

//		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		final String device_id = mTelephonyMgr.getDeviceId();

//		DeviceInfo df = new DeviceInfo(context, AppzillonMainScreen.getInstance());		
        final String device_id = AppzillonUtils.getDeviceId(context);

        boolean serverflag;

        serverflag = post(SERVER_URL, device_id, regId, context);
        if (serverflag == true) {
            //	GCMRegistrar.setRegisteredOnServer(context, false);
            final String message = context.getString(R.string.server_unregistered);
            CommonUtilities.displayMessage(context, message);
        } else {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            final String message = context.getString(R.string.server_unregister_error);
            CommonUtilities.displayMessage(context, message);
        }
    }

    private static boolean post(String endpoint, String deviceid, String regid, Context c) {
        String bluetoothName = getBluetoothName();
        String osVersion = android.os.Build.VERSION.RELEASE;
        String ip = AppzillonUtils.ipAddress(c);
//		String jsonData = "{'appzillonHeader': {'appId':'"
//				+ c.getPackageName()
//				+ "'"
//				+ "            , 'sessionId': 'sessionId','interfaceId': 'appzillonDeviceRegistration','screenId': 'login','deviceId': '"
//				+ deviceid + "','requestKey': '000NEW','requestID': '','async': 'false'"
//				+ "            , 'userId': '','status': 'success'}"
//				+ "            , 'appzillonBody': {'deviceId':'" + deviceid
//				+ "','deviceName':'" + bluetoothName
//				+ "','osId':'Android','regId':'" + regid + "'"
//				+ "            , 'osVersion':'"+osVersion+"','appId': '"
//				+ c.getPackageName() + "'}}";

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject header = new JSONObject();

            header.put(AppzillonMainScreen.APP_ID, StringUtils.getString(StringUtils.APP_ID));
            header.put(AppzillonMainScreen.SESSION_ID, "sessionId");
//			header.put(AppzillonMainScreen.INTERFACE_ID, "appzillonDeviceRegistration");
            header.put(AppzillonMainScreen.INTERFACE_ID, "appzillonNotificationRegistration");
            header.put(AppzillonMainScreen.SCREEN_ID, "login");
            header.put(AppzillonMainScreen.DEVICE_ID, deviceid);
            header.put(AppzillonMainScreen.REQUEST_KEY, "000NEW");
            header.put(AppzillonMainScreen.REQUEST_ID, "");
            header.put(AppzillonMainScreen.ASYNC, "false");
            header.put(AppzillonMainScreen.USER_ID, AppzillonMainScreen.USER_ID_FOR_OTA);
            header.put(AppzillonMainScreen.REQ_STATUS, true); // sid, 3.2 server changes
            header.put("origination", ip);
            header.put("source", "APPZILLON");

            JSONObject appFileReq = new JSONObject();

            appFileReq.put(AppzillonMainScreen.DEVICE_ID, deviceid);
            appFileReq.put(AppzillonMainScreen.DEVICE_NAME, bluetoothName);
            appFileReq.put(AppzillonMainScreen.OS_ID, AppzillonMainScreen.ANDROID_OS);
            appFileReq.put("regId", regid);
            appFileReq.put(AppzillonMainScreen.OS_VERSION, osVersion);
            appFileReq.put(AppzillonMainScreen.APP_ID, StringUtils.getString(StringUtils.APP_ID));

            jsonObject.put(AppzillonMainScreen.APPZILLON_HEADER, header);
            jsonObject.put(AppzillonMainScreen.APPZILLON_BODY, appFileReq);

        } catch (Exception e) {

        }

//		Log.v(TAG, "Posting  to " + endpoint + "Data : " + jsonObject);
//Abhishek, Bug id 6079, commented default client START
//		final HttpClient httpclient = new DefaultHttpClient();
//		final HttpPost httppost = new HttpPost(endpoint);
//		httppost.setHeader("Accept", "text/plain");
//Abhishek, Bug id 6079, commented default client END
//		StringEntity reqentity = null;
//		HttpResponse response = null;
        //String statusbody;     // 3.2 changes
        //String statusheader;   // 3.2 changes
        boolean statusbody;
        boolean statusheader;

        try {
            JSONObject obj = sendRequestToServer(endpoint, jsonObject.toString());
            if (obj != null) {
                JSONObject body = obj.getJSONObject(AppzillonMainScreen.APPZILLON_BODY);
                //statusbody = body.getString("status");  3.2 changes
                //statusbody = body.getBoolean("status");//changes natasha
                statusbody = body.getString("status").equalsIgnoreCase("success");
                JSONObject header = obj.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
                //statusheader = header.getString("status");	3.2 changes
                statusheader = header.getBoolean("status");
                if (statusbody && statusheader) {  // 3.2 changes statusbody.equals("success")&& statusheader.equals("success")
//	   				Log.v("Registration successful", "on Server");
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
          
        }

////Abhishek, Bug id 6079, Added new code to get http Client for HTTPS START
//		HttpClient httpclient = getHttpsClient(new DefaultHttpClient());
//		HttpParams params = new BasicHttpParams();
//        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//		try {
//			HttpPost httppost = new HttpPost(endpoint);
//			httppost.setParams(params);
//			reqentity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
//
////		try {
////			reqentity = new StringEntity(jsonObject.toString(),"UTF-8");
////			reqentity.setContentType("application/json");
//
////Abhishek, Bug id 6079, Added new code to get http Client for HTTPS END
//			httppost.setEntity(reqentity);
//			response = httpclient.execute(httppost);
//			if( response.getStatusLine().getStatusCode()==200){
//	        	HttpEntity entity = response.getEntity();
//	   			InputStream inpStream = entity.getContent();
//	   			String lResponse = getStringFromInputStream(inpStream);
//	   			Log.v("response", lResponse);
//	   			JSONObject obj = new JSONObject(lResponse);		 
//	   			JSONObject body = obj.getJSONObject(AppzillonMainScreen.APPZILLON_BODY);
//	   			statusbody = body.getString("status");
//	   			JSONObject header = obj.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
//	   			statusheader = header.getString("status");	   			
//	   			// handle the response
//	   			if(statusbody.equals("success")&& statusheader.equals("success")){
//	   				Log.v("Registration successful", "on Server");
//	   				return true;
//	   			}else{
//	   				return false;
//	   			}
//	           }else{
//	        	   Log.e(TAG, "response : "+response.getStatusLine().getStatusCode());
//	   				return false;
//	   			}
//			} 
//			catch (Exception e) {
//				
//
//			}

        return false;

    }

////Abhishek, Bug id 6079, method to return http Client for HTTPS START	
//		@SuppressWarnings("deprecation")
//		public static HttpClient getHttpsClient(HttpClient client) {
//		     try{
//				   X509TrustManager x509TrustManager = new X509TrustManager() { 	           
//						@Override
//						public void checkClientTrusted(X509Certificate[] chain,
//								String authType) throws CertificateException {
//						}
//		 
//						@Override
//						public void checkServerTrusted(X509Certificate[] chain,
//								String authType) throws CertificateException {
//						}
//		 
//						@Override
//						public X509Certificate[] getAcceptedIssuers() {
//							return null;
//						}
//			        };
//			        
//			        SSLContext sslContext = SSLContext.getInstance("TLS");
//			        sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
//			        SSLSocketFactory sslSocketFactory = new ExSSLSocketFactory(sslContext);
//			        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//			        ClientConnectionManager clientConnectionManager = client.getConnectionManager();
//			        SchemeRegistry schemeRegistry = clientConnectionManager.getSchemeRegistry();
//			        schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
//			        return new DefaultHttpClient(clientConnectionManager, client.getParams());
//			    } catch (Exception ex) {
//			        return null;
//			    }
//			}
////Abhishek, Bug id 6079, method to return http Client for HTTPS END

    public static SSLContext pinning() {
        InputStream caInput = null;
        KeyStore keyStore = null;
        try {
            AssetManager man = AppzillonMainScreen.activity.getAssets();
            String[] files = man.list("apps/" + StringUtils.getString(StringUtils.APP_ID) + "/sslCertificates");
            if (files.length > 0) {
                sc = SSLContext.getInstance("TLS");
                String keyStoreType = KeyStore.getDefaultType();
                keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                // randomCA.crt should be in the Assets directory
                for (int j = 0; j < files.length; j++) {
 		    try {
                    caInput = null;
                    // Load CAs from an InputStream
                    caInput = new BufferedInputStream(man.open("apps/" + StringUtils.getString(StringUtils.APP_ID) + "/sslCertificates/" + files[j]));
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    Certificate ca = cf.generateCertificate(caInput);
                  
                    // Create a KeyStore containing our trusted CAs
                    keyStore.setCertificateEntry("cert" + j, ca);
		    }catch (Exception ex){
                        
                    }
                }
                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);
                // Create an SSLContext that uses our TrustManager
                sc.init(null, tmf.getTrustManagers(), null);
                return sc;
            } else {
                return null;
            }

        } catch (NoSuchAlgorithmException e) {
          
        } catch (CertificateException e) {
           
        } catch (KeyStoreException e) {
          
        } catch (IOException e) {
          
        } catch (KeyManagementException e) {
         
        } catch(NullPointerException e){

        }finally {
            try {
                if (caInput != null) {
                    caInput.close();
                }
            } catch (IOException e) {
               
            }
        }
        return null;
    }

    public static JSONObject sendRequestToServer(String endpoint, String string) {
        JSONObject obj = null;
        //Security Changes 3.5.3 Natasha
        PAYLOAD_ENCRYPTION = StringUtils.getString("payloadEncryption");
        JSONObject requestJson = null;
        JSONObject appzillonheader = null;
        JSONObject appzillonbody = null;
        String cNonce = "";
        boolean isDownload;
        String currentInterfaceId = "";

        try {
            if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
                safeBit="0";
            }else{
                safeBit="1";
            }
            //Request without dataIntegrity and encryption
            requestJson = new JSONObject(string);



            appzillonheader = requestJson.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
            appzillonbody = requestJson.getJSONObject(AppzillonMainScreen.APPZILLON_BODY);
            currentInterfaceId = appzillonheader.getString(AppzillonMainScreen.INTERFACE_ID);

            String headerStr = appzillonheader.toString();
            String bodyStr = appzillonbody.toString();

            if(StringUtils.getString(StringUtils.IFACE_ID_IN_SEVER_URL).equalsIgnoreCase("Y")){
                endpoint =endpoint +"/services/"+currentInterfaceId;
            }
            //Checking if the request is for Download
            if("appzillonFilePushService".equals(currentInterfaceId) || "appzillonFilePushServiceWS".equals(currentInterfaceId)){
                isDownload = true;
            }else{
                isDownload = false;
            }

            if (!("appzillonOnAppLaunch".equalsIgnoreCase(currentInterfaceId)||"appzillonGetAppSecTokens".equalsIgnoreCase(currentInterfaceId))) {
                appzillonheader.put("serverNonce", AppzillonMainScreen.SNONCE);
                appzillonheader.put("clientNonce", System.currentTimeMillis() + "");
                appzillonheader.put("sessionToken", AppzillonMainScreen.SESSIONTOKEN);
                if(currentInterfaceId.equalsIgnoreCase("appzillonChangePassword")&&"N".equalsIgnoreCase(PAYLOAD_ENCRYPTION)){
                    appzillonheader.put("appzillonSafeBit", safeBit);
                }
                string = requestJson.toString();

                //Converting JsonObject to string for encryption and QOP value

                    headerStr = requestJson.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER).toString();
                    bodyStr = requestJson.getJSONObject(AppzillonMainScreen.APPZILLON_BODY).toString();


                //String paylod
                String payloadStr = "{\"" + AppzillonMainScreen.APPZILLON_HEADER + "\":" + headerStr + ","+"\"" + AppzillonMainScreen.APPZILLON_BODY + "\":" + bodyStr + "}" ;




                if(!isDownload){
                    if("Y".equalsIgnoreCase(StringUtils.getString("dataIntegrity"))){
                        JSONObject json = null;
                        JSONObject header = null;

                        try {
                            json = new JSONObject(string);
                            header = json.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
                            cNonce = header.getString("clientNonce");

                        } catch (JSONException e) {
                           
                        }
                       // String finalpayload = requestJson.toString();



                       /* //Unescape String
                        String unescapedString = StringEscapeUtils.unescapeJava(payloadStr);
                        String rString = removeSpaceFromElements(unescapedString);*/

//                        String rString = payloadStr;
                        String rString = StringEscapeUtils.escapeJava(payloadStr);
                        
                        String qop = finalPayloadAfterHashing(rString,cNonce);
                        //requestJson = new JSONObject(rString);

                        //Data Integrity with encrytion
                        if("Y".equalsIgnoreCase(PAYLOAD_ENCRYPTION)){

                            JSONObject safeResponse = completeEncryptedPayload(headerStr, bodyStr, false,AppzillonMainScreen.APPZILLONSAFE);
                            String strSafeReq = safeResponse.toString();
                            string = "{\"appzillonQop\":\""+qop+"\","+strSafeReq.substring(1);
                        }else{
                            //Data Integrity without encryption
                           // string = "{\"appzillonQop\":\""+qop+"\","+finalpayload.substring(1);
                            string = "{\"appzillonQop\":\""+qop+"\","+payloadStr.substring(1);
                        }

                        //  string = "{\"appzillonQop\":\""+hashedPayload+"\","+finalpayload.substring(1);
                   /* if("Y".equalsIgnoreCase(PAYLOAD_ENCRYPTION)){
                        String safeToken = PayloadEncryptionDecryption.getEncryptePayload();
                        JSONObject safeResponse =  completeEncryptedPayload(requestJson,false).put("appzillonSafe",safeToken);
                        string = safeResponse.toString();

                    }*/
                    }else{//DataIntegrity not enabled

                        //Encryption without DataIntegrity
                        if("Y".equalsIgnoreCase(PAYLOAD_ENCRYPTION)) {
                            JSONObject safeResponse = completeEncryptedPayload(headerStr,bodyStr, false,AppzillonMainScreen.APPZILLONSAFE);
                            string = safeResponse.toString();
                        }
                    }
                }

               /* if("Y".equalsIgnoreCase(PAYLOAD_ENCRYPTION) && !isDownload) {
                    JSONObject safeResponse = completeEncryptedPayload(requestJson, false);
                    string = safeResponse.toString();
                }*/
            }else{
                //Encryption Payload for first Request
                //requestJson = new JSONObject(string);
                if("Y".equalsIgnoreCase(PAYLOAD_ENCRYPTION)){

                    //string = completeEncryptedPayload(requestJson,false, UUID.randomUUID().toString()).toString();
                    string = completeEncryptedPayload(headerStr, bodyStr ,false, UUID.randomUUID().toString()).toString();
                }
            }

            if (endpoint.contains("https:")) {
               /*vapt change -replace with code starts */
                TRUSTALL_REQ
               /*vapt change -replace with code ends */
            } else {
                obj = sendRequestToHTTPServer(endpoint, string, cNonce, currentInterfaceId);
            }

        }catch (Exception ex){
            //Log.e(TAG, ex.getMessage() );
		obj = null;
        }
        return obj;

    }

    // changes for javax.net.ssl.SSLHandshakeException (< 5.0) START P1
    public static void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(AppzillonMainScreen.activity);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            //Log.e("SecurityException", "Google Play Services not available.");
        }
    }
    // changes for javax.net.ssl.SSLHandshakeException (< 5.0) END P1

    private static JSONObject sendRequestToHTTPSServer(String URL, String request,String cNonce,String mCurrentInterfaceId) {
        JSONObject responseJson = null;
        HttpsURLConnection urlConnection = null;
        try {
            // changes for javax.net.ssl.SSLHandshakeException (< 5.0) START P2
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                updateAndroidSecurityProvider(AppzillonMainScreen.activity);
            }
            // changes for javax.net.ssl.SSLHandshakeException (< 5.0) END P2

			/*X509TrustManager x509TrustManager = new X509TrustManager() { 	           
				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
 
				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
 
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
	        };*/


            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            // Create the SSL connection
		    /*SSLContext sc = SSLContext.getInstance("TLS");
		    sc.init(null, new TrustManager[]{x509TrustManager}, new java.security.SecureRandom());
		    
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());*/
//		    urlConnection.setSSLSocketFactory(sc.getSocketFactory());

//			boolean pinning_success = false;
//			if(StringUtils.getString(StringUtils.CERTIFICATE_PINNING).equals("Y")){
//			 pinning_success = pinning();
//		    }
            URL url = new URL(URL);
            urlConnection = (HttpsURLConnection) url.openConnection();
//			if(pinning_success){
//			urlConnection.setSSLSocketFactory(sc.getSocketFactory());
//			}
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(TIME_OUT);
            urlConnection.setReadTimeout(TIME_OUT);
            if (StringUtils.getString(StringUtils.SSL_PINNING).equalsIgnoreCase("Y")) {
                SSLContext keystore = pinning();
                if (keystore != null) {
                    urlConnection.setSSLSocketFactory(keystore.getSocketFactory());
                }
            }
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept-Encoding","gzip");
            urlConnection.connect();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            //Changes for 3.5.0
            // /out.write(request.toString());
            //out.write(request.toString());
            out.write(request);
            out.close();

            int HttpResult = urlConnection.getResponseCode();
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
                  //  sb.append(line + "\n");
                    sb.append(line);
                }
                br.close();

                //Security: Data Integrity changes Natasha

                String respString = sb.toString();
                if("appzillonFilePushService".equals(mCurrentInterfaceId) || "appzillonFilePushServiceWS".equals(mCurrentInterfaceId)){
                    return new JSONObject(respString);
                }else{
                    responseJson = finalResponseString(respString, cNonce,mCurrentInterfaceId);
                }
            } else {
//			    	Log.e(TAG, ""+urlConnection.getResponseMessage());
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
          
        } catch (IOException e) {
            // TODO Auto-generated catch block

            if (e.getMessage().equals("java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.") || e.getMessage().equals("Handshake failed")) {
                AppzillonMainScreen.activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AppzillonMainScreen.activity);
                        builder.setMessage("Untrusted Connection,request cannot be processed");
                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                });
            }
           
        }catch (Exception e) {

        }

        return responseJson;

    }

    private static JSONObject sendRequestToHTTPServer(String URL, String request,String cNonce,String mCurrentInterfaceId) {
        HttpURLConnection urlConnection = null;
        JSONObject responseJson = null;
        try {

            URL url = new URL(URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(TIME_OUT);
            urlConnection.setReadTimeout(TIME_OUT);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept-Encoding","gzip");
            urlConnection.connect();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
          //  out.write(request.toString());

            out.write(request);
            out.close();

            int HttpResult = urlConnection.getResponseCode();
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
                    //sb.append(line + "\n");
                    sb.append(line);
                }
                br.close();
                String respString = sb.toString();

                //Security: Data Integrity changes Natasha
                if("appzillonFilePushService".equals(mCurrentInterfaceId) || "appzillonFilePushServiceWS".equals(mCurrentInterfaceId)){
                    return new JSONObject(respString);
                }else{
                    responseJson = finalResponseString(respString, cNonce, mCurrentInterfaceId);
                }
            } else {
//			    	Log.e(TAG, ""+urlConnection.getResponseMessage());
            }
        }catch (Exception e) {
          
        }
        return responseJson;

    }

    public static String getBluetoothName() {
        //Abhishek 10 April 2015 To handle crash observed in lower end devices on re-launch of app START
        devicename = "ANDROID";
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            devicename = mBluetoothAdapter.getName();
            //Abhishek Bug ID 4869 START
            if (devicename == null) {
                devicename = mBluetoothAdapter.getAddress();
            }
            //Abhishek Bug ID 4869 END
            return devicename;

        } catch (Exception e) {
            
            return devicename;
        }
        //Abhishek 10 April 2015 To handle crash observed in lower end devices on re-launch of app END
    }

//	private static String getStringFromInputStream(InputStream is) {
//
//		BufferedReader br = null;
//		StringBuilder sb = new StringBuilder();
//
//		String line;
//		try {
//
//			br = new BufferedReader(new InputStreamReader(is));
//			while ((line = br.readLine()) != null) {
//				sb.append(line);
//			}
//
//		} catch (IOException e) {
//			
//		} finally {
//			if (br != null) {
//				try {
//					br.close();
//				} catch (IOException e) {
//					
//				}
//			}
//		}
//
//		return sb.toString();
//
//	}
    

    /*vapt change -replace with code starts */

    TRUSTALL_METHOD 
  

    /*vapt change -replace with code ends */

    private static String finalPayloadAfterHashing(String req,String cNonce){

        String hashedCNONCE = hashPayload(cNonce,AppzillonMainScreen.SNONCE+StringUtils.getString(StringUtils.SERVER_TOKEN));


        //Converting the payload to base64
            String base64req = Base64.encodeToString(req.getBytes(),Base64.NO_WRAP);

        //Hashing the base64 payload
        String hashPayload = hashPayload(base64req,hashedCNONCE);

        return  hashPayload;
    }

    private static String hashPayload(String textToHash, String salt) {
        String hashPin = null;
        try {
            hashPin = hashSHA256(textToHash, salt);
        } catch (NoSuchAlgorithmException e) {
          
        }
        return hashPin;
    }


    private static String hashSHA256(String ptext, String psalt) throws NoSuchAlgorithmException {
        String pTextSalt = ptext + psalt;
        String pHashedText = "";
        byte[] ptextSaltbyte = new byte[200];
        byte[] hashbyte = new byte[200];
        MessageDigest msgdigest = MessageDigest.getInstance("SHA-256");
        try {
            ptextSaltbyte = pTextSalt.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            ApzLogger.e(TAG,"Unsupported character set");
        }
        msgdigest.reset();
        msgdigest.update(ptextSaltbyte);
        hashbyte = msgdigest.digest();
        pHashedText = toHexString(hashbyte);
//		ApzLogger.i(TAG,"EncryptHash : "+ pHashedText);
        return pHashedText;
    }

    private static String toHexString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            int c = ((b[i]) >>> 4) & 0xf;
            sb.append(hex[c]);
            c = (b[i] & 0xf);
            sb.append(hex[c]);
        }
        return sb.toString();
    }

    private static boolean validateHash(String request, String cNonce) {
        String reqwithoutQop = "{" + request.substring(request.indexOf(',') + 1);
        String hashedCNONCE = hashPayload(cNonce, AppzillonMainScreen.SNONCE + StringUtils.getString(StringUtils.SERVER_TOKEN));
//Unescape String
// String unescapedString = StringEscapeUtils.unescapeJava(reqwithoutQop); // String rString = removeSpaceFromElements(unescapedString); //Saqib-56228
        String addEscape = StringEscapeUtils.escapeJava(reqwithoutQop);
//Base64 conversion of the payload
        String base64Req = Base64.encodeToString(addEscape.getBytes(), Base64.NO_WRAP);
        String hashPayload = hashPayload(base64Req, hashedCNONCE);
// String hashPayload = hashPayload(reqwithoutQop, hashedCNONCE);
        String appzillonQop = "";
        try {
            JSONObject json = new JSONObject(request);
            appzillonQop = json.getString("appzillonQop");
        } catch (Exception exp) {
        }
        if (appzillonQop.equals(hashPayload)) {
            return true;
        }
        return false;
    }

    private static JSONObject finalResponseString(String string,String cNonce, String currentInterfaceId){
        JSONObject responseJson = null;
        boolean isFirstResponse = false;
        //ResponseStr for QOP

        String responseStr = "";
        try {
           // responseJson = new JSONObject(string);

            //Finding whether first response
          /*  if("Y".equalsIgnoreCase(PAYLOAD_ENCRYPTION)){
                if(!responseJson.has("appzillonSafe")){
                    isFirstResponse = true;
                }
            }else{
                JSONObject appzillonHeader = responseJson.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
                String currentInterfaceId = appzillonHeader.getString(AppzillonMainScreen.INTERFACE_ID);
                if("appzillonOnAppLaunch".equalsIgnoreCase(currentInterfaceId)){
                    isFirstResponse = true;
                }
            }*/

            //Finding whether first response
            if("appzillonOnAppLaunch".equalsIgnoreCase(currentInterfaceId)||"appzillonGetAppSecTokens".equalsIgnoreCase(currentInterfaceId)){
                isFirstResponse = true;
            }

          /*  if(isFirstResponse){
                if("Y".equalsIgnoreCase(PAYLOAD_ENCRYPTION)) {
                    JSONObject finalResponseJSON = new JSONObject();
                    finalResponseJSON.put(AppzillonMainScreen.APPZILLON_HEADER,new JSONObject(PayloadEncryptionDecryption.getDecryptedPayloadWithRSA(responseJson.getString(AppzillonMainScreen.APPZILLON_HEADER))));
                    finalResponseJSON.put(AppzillonMainScreen.APPZILLON_BODY,new JSONObject(PayloadEncryptionDecryption.getDecryptedPayloadWithRSA(responseJson.getString(AppzillonMainScreen.APPZILLON_BODY))));
                    responseJson = finalResponseJSON;
                }
            }

            //If not first Response
            else{*/


            if("N".equalsIgnoreCase(PAYLOAD_ENCRYPTION) && isFirstResponse){
                responseJson = new JSONObject(string);
            }


                if("Y".equalsIgnoreCase(PAYLOAD_ENCRYPTION)) {

                    responseJson = new JSONObject(string);

                    //Decrypt the Payload
                    JSONObject finalResponseJSON = new JSONObject();

                    //Copy qop if present
                    if(responseJson.has("appzillonQop")){
                        String qop = responseJson.getString("appzillonQop");
                        finalResponseJSON.put("appzillonQop",qop);
                        responseStr = "{\"appzillonQop\":\""+qop+"\",";
                    }

                    String appzillonSafe = responseJson.optString("appzillonSafe");
                    String decryptedAppzillonSafe = PayloadEncryptionDecryption.decryptDataWithRSAPublicKey(appzillonSafe,PayloadEncryptionDecryption.getKey());
                    AppzillonMainScreen.APPZILLONSAFE = decryptedAppzillonSafe;

                    //AppzillonErrors
                    if(responseJson.has(AppzillonMainScreen.APPZILLON_ERRORS)) {
                        String decryptedError = PayloadEncryptionDecryption.getDecryptedPayloadWithAES(responseJson.getString(AppzillonMainScreen.APPZILLON_ERRORS), decryptedAppzillonSafe);
                        responseStr += "\""+AppzillonMainScreen.APPZILLON_ERRORS+"\":"+decryptedError+",";
                        finalResponseJSON.put(AppzillonMainScreen.APPZILLON_ERRORS, new JSONArray(decryptedError));

                        //finalResponseJSON.put(AppzillonMainScreen.APPZILLON_ERRORS, new JSONArray(PayloadEncryptionDecryption.getDecryptedPayloadWithAES(responseJson.getString(AppzillonMainScreen.APPZILLON_ERRORS), decryptedAppzillonSafe)));
                    }



                    //AppzillonHeader
                    String decryptedHeader = PayloadEncryptionDecryption.getDecryptedPayloadWithAES(responseJson.getString(AppzillonMainScreen.APPZILLON_HEADER),decryptedAppzillonSafe);

                        responseStr += "\"" + AppzillonMainScreen.APPZILLON_HEADER + "\":" + decryptedHeader + ",";

                    finalResponseJSON.put(AppzillonMainScreen.APPZILLON_HEADER,new JSONObject(decryptedHeader));



                    //AppzillonBody
                    String decryptedBody = PayloadEncryptionDecryption.getDecryptedPayloadWithAES(responseJson.getString(AppzillonMainScreen.APPZILLON_BODY),decryptedAppzillonSafe);

                    //Checking whether the body is json array or json object
                    if(decryptedBody.startsWith("[") && decryptedBody.endsWith("]")){
                        responseStr += "\""+ AppzillonMainScreen.APPZILLON_BODY+"\":"+decryptedBody+"}";
                        finalResponseJSON.put(AppzillonMainScreen.APPZILLON_BODY,new JSONArray(decryptedBody));
                    }else{
                        responseStr += "\""+ AppzillonMainScreen.APPZILLON_BODY+"\":"+decryptedBody+"}";
                        finalResponseJSON.put(AppzillonMainScreen.APPZILLON_BODY,new JSONObject(decryptedBody));
                    }

                    //Response JSON
                    responseJson = finalResponseJSON;

                }

                //Data Integrity check
                if ("Y".equalsIgnoreCase(StringUtils.getString("dataIntegrity")) && !isFirstResponse) {
                  // if(validateHash(responseJson.toString(0),cNonce)){
                    if("Y".equalsIgnoreCase(PAYLOAD_ENCRYPTION)) {
                        if(validateHash(responseStr,cNonce)){
                            responseJson.remove("appzillonQop");

                        }else{
                            responseJson.put("errorCode", "APZ-CNT-230");
                        }
                    }else{
                        //String escapedResponse = StringEscapeUtils.escapeJava(string);
                        if(validateHash(string,cNonce)){
                           // responseJson.remove("appzillonQop");
                            responseJson = new JSONObject(string);
                        }else{
                            responseJson = new JSONObject(string);
                            responseJson.put("errorCode", "APZ-CNT-230");
                        }
                    }

                   // Log.d("finalResponseString", responseJson.toString());

                }

            if("N".equalsIgnoreCase(PAYLOAD_ENCRYPTION) && "N".equalsIgnoreCase(StringUtils.getString("dataIntegrity")) && !isFirstResponse){
                responseJson = new JSONObject(string);
            }

            // }

        }catch (Exception ex){
            //Log.e(TAG, ex.getMessage() );
        }
        return responseJson;
        }

        public static JSONObject completeEncryptedPayload(String header, String body, boolean isRSA,String key){
        JSONObject encryptedResponce = null;
        try{
            String appzillonBody = body;//payload.getJSONObject(AppzillonMainScreen.APPZILLON_BODY).toString();
          /*  if("Y".equalsIgnoreCase(StringUtils.getString("dataIntegrity"))){
                //Unescape String
                String unescapedString = StringEscapeUtils.unescapeJava(appzillonBody);
                appzillonBody = removeSpaceFromElements(unescapedString);
            }*/


            String appzillonHeader = header;//payload.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER).toString();

           /* if("Y".equalsIgnoreCase(StringUtils.getString("dataIntegrity"))){
                //Unescape String
                String unescapedString = StringEscapeUtils.unescapeJava(appzillonHeader);
                appzillonHeader = removeSpaceFromElements(unescapedString);
            }*/

            encryptedResponce = new JSONObject();


            encryptedResponce.put("appzillonSafeBit",safeBit);
            encryptedResponce.put("encMode",1);
            if(isRSA) {
                encryptedResponce.put(AppzillonMainScreen.APPZILLON_HEADER, PayloadEncryptionDecryption.getEncryptePayloadwithRSA(appzillonHeader));
                encryptedResponce.put(AppzillonMainScreen.APPZILLON_BODY, PayloadEncryptionDecryption.getEncryptePayloadwithRSA(appzillonBody));
            }else{
                encryptedResponce.put(AppzillonMainScreen.APPZILLON_HEADER, PayloadEncryptionDecryption.getEncryptePayloadwithAES(appzillonHeader, key));
                encryptedResponce.put(AppzillonMainScreen.APPZILLON_BODY, PayloadEncryptionDecryption.getEncryptePayloadwithAES(appzillonBody, key));
            }
            if(!isRSA){
                encryptedResponce.put("appzillonSafe",PayloadEncryptionDecryption.encryptDataWithRSAPublicKey(key,PayloadEncryptionDecryption.getKey()));
            }
            return encryptedResponce;
        }catch (Exception e) {

        }
            return encryptedResponce;
        }
/*
    public static String removeSpaceFromElements(String payloadJson) {
        Pattern p = Pattern.compile("\\s*[\"\"]");
        Matcher m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("\"");

        p = Pattern.compile("[\"\"]\\s*");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("\"");

        p = Pattern.compile("[}]\\s*");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("}");

        p = Pattern.compile("\\s*[{]");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("{");

        p = Pattern.compile("[]]\\s*");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("]");

        p = Pattern.compile("[\\[]\\s*");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("[");

        p = Pattern.compile("[:]\\s*");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll(":");

        // added by sasidhar for new lines
        p = Pattern.compile("[\n]");
        m = p.matcher(payloadJson);
        payloadJson = m.replaceAll("");

        return payloadJson;
    }*/

}






