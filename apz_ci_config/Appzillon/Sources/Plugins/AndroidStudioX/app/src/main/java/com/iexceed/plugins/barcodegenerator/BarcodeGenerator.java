package com.iexceed.plugins.barcodegenerator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Base64;
import android.webkit.WebView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.iexceed.appzillonapp.R;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;
import com.iexceed.appzillonapp.AppzillonMainScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
/**
 * Created by mishra.abhishek on 31/8/17.
 */

public class BarcodeGenerator extends ApzPlugin{

    private String TAG = "BarcodeGenerator";

    private static ApzPlugin pluginObj;


    private String isLogo = "N";

    public BarcodeGenerator(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    private void generateFile(String data, String dest, String fileName){
        dest = AppzillonMainScreen.SANDBOX_LOC +File.separator+AppzillonMainScreen.ASSET_APP_LOC+File.separator+dest;
        try{
            Bitmap imageBitmap = encodeAsBitmap(data);
            if(imageBitmap != null){
                if(!new File(dest).exists())
                    new File(dest).mkdirs();
                File imageFile = new File(dest, fileName + ".jpg");
                OutputStream os;
                try {
                    os = new FileOutputStream(imageFile);
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();

                    JSONObject rJson = new JSONObject();
                    try {
                        rJson.put("text", imageFile.getAbsolutePath());
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                       
                    }
                    //Send Success
                    ApzPluginUtil.sendSuccess(callbackId,rJson,false,activity,webView,true);
                } catch (Exception e) {
                    ApzLogger.e(TAG, e.getMessage());
                    handleFailure("Error writing bitmap.");
                }
            }else{
                handleFailure("Unable to encode data.");
            }
        } catch (WriterException e) {
            // TODO Auto-generated catch block
          
            handleFailure("WriterException");
        }
    }

    public void generateBase64(String data) {
        try {
            Bitmap imageBitmap = encodeAsBitmap(data);
            if (imageBitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100	, baos);
                byte[] byteArray = baos.toByteArray();
                String str = Base64.encodeToString(byteArray, Base64.DEFAULT);
                JSONObject rJson =new JSONObject();
                try {
                    rJson.put("text", str);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    
                }
                //Send Success
                ApzPluginUtil.sendSuccess(callbackId,rJson,false,activity,webView,true);
            } else {
                handleFailure("Unable to encode data.");

            }

        } catch (WriterException e) {
            // TODO Auto-generated catch block
            
            handleFailure("WriterException");
        }
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
//            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 150, 150, null);
        	Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8"); 
			result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 150, 150, hintMap);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            handleFailure("Unsupported format");
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 150, 0, 0, w, h);

        if(isLogo.equalsIgnoreCase("Y")){
            int resID = activity.getResources().getIdentifier("qr_code_logo" , "drawable", activity.getPackageName());
            if(resID == 0){
                return bitmap;
            }else{
                Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), resID);
                return mergeBitmaps(icon, bitmap);
            }

        }else{
            return bitmap;
        }
    }

    private Bitmap mergeBitmaps(Bitmap logo, Bitmap qrcode) {

        Bitmap combined = Bitmap.createBitmap(qrcode.getWidth(), qrcode.getHeight(), qrcode.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        canvas.drawBitmap(qrcode, new Matrix(), null);

        Bitmap resizeLogo = Bitmap.createScaledBitmap(logo, canvasWidth / 5, canvasHeight / 5, true);
        int centreX = (canvasWidth - resizeLogo.getWidth()) /2;
        int centreY = (canvasHeight - resizeLogo.getHeight()) / 2;
        canvas.drawBitmap(resizeLogo, centreX, centreY, null);
        return combined;
    }


    private void handleFailure(String str){
        final JSONObject fJson =new JSONObject();
        try {
            fJson.put("error", str);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            
        }
        ApzPluginUtil.sendError(callbackId, "APZ-DM-006", fJson, activity, webView, true);
    }

    @Override
    public void execute(JSONObject params) {
        try{
            callbackId = params.getString("id");
            String inputString = params.getString("inputString");
            String base64 = params.getString("base64");
            isLogo = params.optString("isLogoImagePresent");
            if(base64.equalsIgnoreCase("N")){
                String filePath = params.optString("destinationPath");
                if(filePath == null || filePath.length()==0)
                    filePath = "TempFolderQRCode";
                String fileName = params.optString("fileName");
                if(fileName == null || fileName.length()==0){
                    Date date = new Date();
                    DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
                    fileName = dateFormatter.format(date);
                }
                generateFile(inputString,filePath,fileName);
            }else{
                generateBase64(inputString);
            }

        } catch (JSONException e) {
           
        }
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if(pluginObj == null){
            pluginObj = new BarcodeGenerator(webView, activity);
        }
        return pluginObj;
    }

  public static boolean isPlugin(){
        return true;
    }
}
