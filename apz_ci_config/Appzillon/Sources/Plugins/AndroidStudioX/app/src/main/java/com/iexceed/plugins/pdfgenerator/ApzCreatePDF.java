package com.iexceed.plugins.pdfgenerator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApzCreatePDF extends ApzPlugin{

    private static ApzPlugin pluginObj;

    private int pageHeight;

    private int pageWidth;

    private float scale;

    private PdfDocument pdfDoc;

    private String TAG = "ApzCreatePDF";

    private ApzCreatePDF(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    @Override
    public void execute(JSONObject params) {
        String action;

        try{
            callbackId = params.getString("id");
            action = params.getString("action");

//            if(action.equalsIgnoreCase("init")){
//                init();
//            }else
            if(action.equalsIgnoreCase("append")){
                if(pdfDoc == null ){
                    WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    pageHeight = (int) displaymetrics.heightPixels ;
                    pageWidth = (int) displaymetrics.widthPixels ;

                    scale = displaymetrics.density;

                    pdfDoc = new PdfDocument();
                }
                String type = params.getString("contentType");
                if(type.equalsIgnoreCase("Text")){
                    String text = params.getString("text");
                    String textColor = params.optString("fontColor");
                    if(textColor == null || textColor.length() == 0)
                        textColor = "#000000";// Default color as Black
                    int textSize = params.optInt("fontSize");
                    if(textSize == 0)
                        textSize = 13; // Default text Size
                    String font = params.optString("fontType");
                    if(font == null || font.length() == 0)
                        font = "Ariel";
                    int padding = params.optInt("padding");
                    if(padding == 0)
                        padding = 10; //Default Padding

                    appendText(text,textColor,textSize,font,padding);
                }else if(type.equalsIgnoreCase("Image")){
                    String path = params.optString("imagePath");
                    int imgWidht = params.optInt("imageWidth");
                    if(imgWidht == 0)
                        imgWidht = 200;  //Default Width
                    int imgHeight = params.optInt("imageHeight");
                    if(imgHeight == 0)
                        imgHeight = 200;    //Default Height
                    if(path == null || path.length()==0){
                        String base64 = params.optString("base64");
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        appendImage(path,bitmap,imgWidht,imgHeight);
                    }else{
                        appendImage(path,null,imgWidht,imgHeight);
                    }
                }else{
                    ApzPluginUtil.sendError(callbackId, "APZ-FM-EX-025", null, activity, webView, true);
                }

            }else if(action.equalsIgnoreCase("generate")){
                if(pdfDoc == null){
                    ApzLogger.e(TAG, "Generating PDF without Content.");
                    ApzPluginUtil.sendError(callbackId, "APZ-CNT-082", null, activity, webView, true);
                }else{
                    String base64 = params.optString("base64");
                    String filePath = params.optString("filePath");
                    if(filePath == null || filePath.length() == 0){
                        Date date = new Date();
                        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd_hhmmss");
                        String fileName = dateFormatter.format(date);
                        filePath = AppzillonMainScreen.SANDBOX_LOC +File.separator+AppzillonMainScreen.ASSET_APP_LOC+File.separator+fileName+".pdf";
                    }else{
                        filePath = AppzillonMainScreen.SANDBOX_LOC +File.separator+AppzillonMainScreen.ASSET_APP_LOC+File.separator+filePath;
                    }
                    if(base64.equalsIgnoreCase("Y")){
                        savePdf(filePath,true);
                    }else{
                        savePdf(filePath,false);
                    }
                }

            }


        } catch (JSONException e) {
            ApzLogger.e(TAG, e.getMessage());
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity, webView, true);
        }

    }

//    private void init(){
//        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        pageHeight = (int) displaymetrics.heightPixels ;
//        pageWidth = (int) displaymetrics.widthPixels ;
//
//        scale = displaymetrics.density;
//
//        pdfDoc = new PdfDocument();
//
//        JSONObject resultObj = new JSONObject();
//        try {
//            resultObj.put("event","pdfInitialized");
//        } catch (JSONException e) {
//            
//        }
//
//        ApzPluginUtil.sendSuccess(callbackId,resultObj,false,activity,webView,true);
//
//    }

    private void appendText(String val, String txtColor, int txtSize, String font, int padding){

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

        PdfDocument.Page page = pdfDoc.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        try{
            TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.parseColor(txtColor));
            paint.setTextSize(txtSize);
            Typeface typeface = Typeface.create(font, Typeface.NORMAL);
            paint.setTypeface(typeface);

            // set text width to canvas width minus 16dp padding
            int textWidth = canvas.getWidth() - (int) (16 * scale);

            // init StaticLayout for text
            StaticLayout textLayout = new StaticLayout(val, paint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

            canvas.save();
            // Set x and y asis padding
            canvas.translate(padding, padding);
            textLayout.draw(canvas);
            canvas.restore();

            pdfDoc.finishPage(page);

            JSONObject resultObj = new JSONObject();
            try {
                resultObj.put("event","contentAdded");
            } catch (JSONException e) {
                
            }

            ApzPluginUtil.sendSuccess(callbackId,resultObj,false,activity,webView,true);
        }catch(IllegalArgumentException iae){
            pdfDoc.finishPage(page);
            ApzLogger.e(TAG, iae.getMessage());
            ApzPluginUtil.sendError(callbackId, "APZ-DM-006", null, activity, webView, true);
        }catch(StringIndexOutOfBoundsException sobe){
            pdfDoc.finishPage(page);
            ApzLogger.e(TAG, sobe.getMessage());
            ApzPluginUtil.sendError(callbackId, "APZ-DM-006", null, activity, webView, true);
        }catch(IllegalStateException ise){
            pdfDoc.finishPage(page);
            ApzLogger.e(TAG, ise.getMessage());
            ApzPluginUtil.sendError(callbackId, "APZ-DM-006", null, activity, webView, true);
        }
    }

    private void appendImage(String path,Bitmap bmp, int imgWidth, int imgHeight){
        Bitmap bitmap = null;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

        PdfDocument.Page page = pdfDoc.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        if(bmp == null){
            bitmap = getBitmapfromPath(path, imgWidth, imgHeight);
        }else{
            bitmap = bitmap.createScaledBitmap(bmp, imgWidth, imgHeight, true);
        }

        if(bitmap != null){
            int left = (pageWidth - bitmap.getWidth()) /2;
            int top = (pageHeight - bitmap.getHeight())/2;
            canvas.drawBitmap(bitmap, left, top , null);

            JSONObject resultObj = new JSONObject();
            try {
                resultObj.put("event","contentAdded");
            } catch (JSONException e) {
                
            }

            ApzPluginUtil.sendSuccess(callbackId,resultObj,false,activity,webView,true);

        }else{
            ApzLogger.e(TAG, "Bitmap Is Null");
            ApzPluginUtil.sendError(callbackId, "APZ-FM-EX-030", null, activity, webView, true);
        }
        pdfDoc.finishPage(page);
    }

    private Bitmap getBitmapfromPath(String path, int width, int height){
        Bitmap bmp = null;
        try{
            File file = new File(path);
            if(file.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                bmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }
        }catch(Exception e){
            ApzLogger.e(TAG, e.getMessage());
        }
        return bmp;
    }

    private void savePdf(String filePath, boolean isBase64){

        try {
            File file = new File(filePath);
            String destinationPath;
            if(!file.exists()){
                File dFile = new File(filePath.replace(filePath.substring(filePath.lastIndexOf("/")),""));
                dFile.mkdirs();

            }
            pdfDoc.writeTo(new FileOutputStream(new File(filePath)));

            JSONObject resultObj = new JSONObject();
            // If return type is base 64
            if(isBase64){
                try{
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    FileInputStream fis = new FileInputStream(new File(filePath));
                    byte[] buf = new byte[1024];
                    int n;
                    while (-1 != (n = fis.read(buf)))
                        baos.write(buf, 0, n);
                    fis.close();
                    byte[] bytes = baos.toByteArray();
                    String str = Base64.encodeToString(bytes, Base64.DEFAULT);
                    resultObj.put("text", str);
                } catch (FileNotFoundException e) {
                    
                } catch (IOException e) {
                    
                } catch (JSONException e) {
                    
                }

            }else{
                try{
                    resultObj.put("text", filePath);
                } catch (JSONException e) {
                    
                }
            }
            //Send Success
            ApzPluginUtil.sendSuccess(callbackId,resultObj,false,activity,webView,true);
        } catch (IOException e) {
            ApzLogger.e(TAG, e.getMessage());
            ApzPluginUtil.sendError(callbackId, "APZ-FM-EX-030", null, activity, webView, true);
        }
        // close the document
        pdfDoc.close();
        pdfDoc = null;

    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if(pluginObj == null){
            pluginObj = new ApzCreatePDF(webView,activity);
        }
        return pluginObj;
    }

    public static boolean isPlugin() {
        return true;
    }


}

