package com.iexceed.webcontainer.authentication.util;

import java.io.*;
import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpClientHelper implements Serializable {

    private static final long serialVersionUID = 3298244077163098656L;

    private HttpClientHelper() {
    }

    public static String getResponseStringFromConn(HttpURLConnection conn) throws IOException {

        InputStream is;
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            is = conn.getInputStream();
        } else {
            is = conn.getErrorStream();
        }
        StringBuilder stringBuilder= new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(is))){
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }

   public static JSONObject processResponse(int responseCode, String response) throws JSONException {

        JSONObject responseJson = new JSONObject();
        responseJson.put("responseCode", responseCode);

        if (response.equalsIgnoreCase("")) {
            responseJson.put("responseMsg", "");
        } else {
            responseJson.put("responseMsg", new JSONObject(response));
        }
        return responseJson;
    }
}
