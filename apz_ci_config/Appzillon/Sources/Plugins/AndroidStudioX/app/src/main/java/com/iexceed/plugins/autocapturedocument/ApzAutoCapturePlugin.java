package com.iexceed.plugins.autocapturedocument;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.text.TextRecognizer;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.MediaUtils;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.iexceed.appzillonapp.AppzillonMainScreen.ASSET_APP_LOC;
import static com.iexceed.appzillonapp.AppzillonMainScreen.SANDBOX_LOC;

public class ApzAutoCapturePlugin extends ApzPlugin {
    private static final String TAG = "ApzAutoCapture";
    private static ApzPlugin pluginObj;
    public static final int AUTO_CAPTURE_PIC = 16;
    private JSONObject mJsonObj;
    private String[] permissions;
    public static WebView mWebview;
    public static Activity mActivity;
    public static String mCallbackId;
    private static String mEncodingType = "";
    public static Bitmap.CompressFormat mCompressFormat;
    private static String mEncodingFormat;
    private static String fileName;
    private String mportraitMarginPercent = "";
    private String mLandscapeMarginPercent = "";
    private String mtopMarginPercent = "";
    public static float gMatchingThreshold ;
    public static String sTemplateFileName = "";
    public static String sTesseractCheckRequired = "";
    public static String mTextTypeToDetect = "";
    public static String isTextDetection = "";
    public static String isTemplateDetection = "";
    public static boolean isManualCapture = false;
    public static boolean deviceTextRecognition = true;
    private static String mOutputFileType = "";
    public static String documentAspectRatio = "";
    public static JSONArray mTextToDetect = null;
    public static int tPortMarginPercent;
    public static int tLandMarginPercent;
    public static int topMarginPercent;
    public static String mPageTitle = "";
    public static String mMessageTitle = "";
    public static String mMessage = "";
    public static String mFontColor = "";
    public static String mOverlayColor = "";
    public static String mScanStatus1 = "Scanning...";
    public static String mScanStatus2 = "Verifying...";
    public static String mScanStatus3 = "";
    public static String defaultCaptureMode = "";
    public static String istoggleButtonReq = "";
    public static int holdTimeForAutoCapture;
    public static int timeOutForAutoCapture;
    public static String nativePreviewScreen = "";
    public static String documentType = "";
    static File pictureFile;
    private static int cmpLevel;

    private ApzAutoCapturePlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
        mWebview = webView;
        mActivity = activity;
    }

    @Override
    public void execute(JSONObject params) {
        try {
            this.callbackId = params.getString("id");
            mCallbackId = this.callbackId;
            this.mJsonObj = params;
            fileName = mJsonObj.optString("fileName");
            mEncodingType = mJsonObj.optString("encodingType").trim();
            documentAspectRatio = mJsonObj.optString("documentWHRatio").trim();
            documentType = mJsonObj.optString("documentType").trim();
            defaultCaptureMode = mJsonObj.optString("defaultCaptureMode").trim();
            if ((documentAspectRatio.equalsIgnoreCase("")) || (!documentAspectRatio.contains(":") || documentAspectRatio.split(":").length == 0)) {
                documentAspectRatio = "3:2";
            }
            String compressionLevel = mJsonObj.optString("quality").trim();
            if (!compressionLevel.equals("")) {
                cmpLevel = Integer.parseInt(compressionLevel);
            } else {
                cmpLevel = 100;
            }
            setCompressFormat(mEncodingType);
            String captureTime = mJsonObj.optString("holdTimeForCapture").trim();
            String timeout = mJsonObj.optString("timeOutForCapture").trim();
            holdTimeForAutoCapture = (captureTime.equalsIgnoreCase("")) ? 2 : Integer.parseInt(captureTime);
            timeOutForAutoCapture = (timeout.equalsIgnoreCase("")) ? 0 : Integer.parseInt(timeout);
            final JSONObject objTextToDetect = mJsonObj.optJSONObject("UIParams");
            if (objTextToDetect != null) {
                mportraitMarginPercent = mJsonObj.optJSONObject("UIParams").optString("portraitMarginPercent").trim();
                mLandscapeMarginPercent = mJsonObj.optJSONObject("UIParams").optString("landscapeMarginPercent").trim();
                mtopMarginPercent = mJsonObj.optJSONObject("UIParams").optString("topMarginPercent").trim();
                istoggleButtonReq = mJsonObj.optJSONObject("UIParams").optString("toggleButton").trim();
                mPageTitle = mJsonObj.optJSONObject("UIParams").optString("pageTitle");
                mMessageTitle = mJsonObj.optJSONObject("UIParams").optString("messageTitle");
                mMessage = mJsonObj.optJSONObject("UIParams").optString("message");
                mFontColor = mJsonObj.optJSONObject("UIParams").optString("fontColor").trim();
                mOverlayColor = mJsonObj.optJSONObject("UIParams").optString("overlayColor").trim();
                mScanStatus1 = mJsonObj.optJSONObject("UIParams").optString("scanStatus1");
                mScanStatus2 = mJsonObj.optJSONObject("UIParams").optString("scanStatus2");
                mScanStatus3 = mJsonObj.optJSONObject("UIParams").optString("scanStatus3");
            }
            // color code validation
            String colorRegex="^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
            if (!(mFontColor.matches(colorRegex) && mOverlayColor.matches(colorRegex))) {
                mFontColor = "#000000";
                mOverlayColor = "#ffffff";
            }
            mScanStatus1 = (mScanStatus1.equalsIgnoreCase("")) ? "Scanning..." : mScanStatus1;
            mScanStatus2 = (mScanStatus2.equalsIgnoreCase("")) ? "Verifying..." : mScanStatus2;
            mScanStatus3 = (mScanStatus3.equalsIgnoreCase("")) ? "Hold Steady..." : mScanStatus3;
            nativePreviewScreen = mJsonObj.optString("nativePreviewScreen").trim();

            if (!mportraitMarginPercent.equalsIgnoreCase("")) {
                tPortMarginPercent = (Integer.parseInt(mportraitMarginPercent) == 100) ? 10 : Integer.parseInt(mportraitMarginPercent);
            } else {
                tPortMarginPercent = 10;
            }
            if (!mtopMarginPercent.equalsIgnoreCase("")) {
                topMarginPercent = (Integer.parseInt(mtopMarginPercent) == 0) ? 20 : Integer.parseInt(mtopMarginPercent);
            } else {
                topMarginPercent = 20;
            }
            if (!mLandscapeMarginPercent.equalsIgnoreCase("")) {
                tLandMarginPercent = (Integer.parseInt(mLandscapeMarginPercent) == 100) ? 30 : Integer.parseInt(mLandscapeMarginPercent);
            } else {
                tLandMarginPercent = 30;
            }
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                boolean isSourceTypeExternal=false;
                if("N".equalsIgnoreCase(activity.getResources().getString(R.string.INTERNALSANDBOX))){
                    isSourceTypeExternal=true;
                }
                /*if (isSourceTypeExternal  && (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
                    permissions = new String[]{
                            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestForPermission();
                } else */
                    // Android 13 storage change
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions = new String[]{
                            Manifest.permission.CAMERA};
                    requestForPermission();
                } else {
                    callAutoCapture();
                }

            } else {
                callAutoCapture();
            }
//			ApzLogger.e(TAG, "Plugin Payload "+ params);
        } catch (Exception e) {
            ApzLogger.e(TAG, e.getMessage());
            failureCallback("Error occured","");
        }
    }

    private void requestForPermission() {
        this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_CAMERA, new OnPermissionsResultHandler() {
                    @Override
                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == ApzPlugin.APZ_REQ_CAMERA) {
                            boolean denied = false;
                            boolean never_ask_again = false;
                            for (String permission : permissions) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                                    denied = true;
                                } else {
                                    if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                                        //callCamera();
                                    } else {
                                        never_ask_again = true;
                                    }
                                }
                            }
                            if (never_ask_again) {
                                PermissionDeniedCallback();
                            } else if (denied) {
                                displayReconfirmationMessage();
                                //PermissionDeniedCallback();
                            } else {
                                callAutoCapture();
                            }
                        } else {
                            PermissionDeniedCallback();
                        }

                    }
                }
        );
    }

    private void displayReconfirmationMessage() {
        String message = "To capture image/select image from gallery, allow app to access by requested permissions";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Permission Denied");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        requestForPermission();
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                PermissionDeniedCallback();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void PermissionDeniedCallback() {
        ApzPluginUtil.sendPermissionDenied("Camera or Storage ", callbackId, this.activity, this.webView);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzAutoCapturePlugin(webView, activity);
        }
        return pluginObj;
    }


    private void callAutoCapture() {

        try {
            mOutputFileType = mJsonObj.optString("type", "file");
            final JSONObject objTextToDetect = mJsonObj.optJSONObject("textToDetect");
            TextRecognizer textRecognizer = new TextRecognizer.Builder(mActivity.getApplicationContext()).build();
            if (objTextToDetect != null && objTextToDetect.has("type") && objTextToDetect.has("text")) {
                if (objTextToDetect.isNull("text")) {
                    isTextDetection = "Y";
                    //used if textTo detect is null put dummy value to scan card with "" string and not all objects
                    mTextTypeToDetect = "any";
                    mTextToDetect = new JSONArray();
                    mTextToDetect.put(0, "");
                } else {
                    isTextDetection = "Y";
                    mTextTypeToDetect = mJsonObj.optJSONObject("textToDetect").optString("type", "Any");
                    mTextToDetect = mJsonObj.optJSONObject("textToDetect").optJSONArray("text");

                }
            } else {
                  
                    isTextDetection = "Y";
                    mTextTypeToDetect = "any";
                    mTextToDetect = new JSONArray();
                    mTextToDetect.put(0, "");
            }

            Intent capture = new Intent(activity, LiveObjectDetectionActivity.class);
            capture.putExtra("jsonStr", mJsonObj.toString());
            if (!textRecognizer.isOperational()) {
                failureCallback("Text detection is not supported in this device.", "");
            } else {
                if (isTextDetection.equalsIgnoreCase("Y")) {
                    deviceTextRecognition = true;
                    } else {
                    failureCallback("Detection parameters not passed", "");
                    return;
                }
            }
            this.activity.startActivity(capture);

        } catch (Exception e) {
          //  Log.i(TAG, "callAutoCapture: " + e);
            failureCallback("Operation failed","");
            return;
        }


    }

    public static void fetchOutput(Bitmap bitmap, String textArr, String fullText) {
        String outputData = "";
        try {
            JSONObject resultBody = new JSONObject();
            JSONObject internalres = new JSONObject();
            if ((bitmap != null)) {
                Bitmap inputBitmap = bitmap;
                if (mOutputFileType.equalsIgnoreCase("base64")) {
                    outputData = getBase64Image(inputBitmap);
                } else {
                    outputData = saveImage(inputBitmap);
                }
                inputBitmap = null;
                internalres.put("type", mOutputFileType);
                internalres.put("data", outputData);
                if (deviceTextRecognition) {
                    JSONArray jsonArray = new JSONArray(textArr);
                    internalres.put("ocrText", jsonArray);
                    internalres.put("ocrWholeText", fullText);
                }
                resultBody.put("outputFile", internalres);
                ApzPluginUtil.sendSuccess(
                        mCallbackId,
                        resultBody, false,
                        mActivity,
                        mWebview, true);
                pluginObj = null;
            }
        } catch (Exception e) {

        }

    }

    private static String getBase64Image(Bitmap bitmap) {
        String returnBase64 = null;
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(mCompressFormat, cmpLevel, baos); // mBitmap is the
            // bitmap object
            byte[] b = baos.toByteArray();
            String base64Image = Base64.encodeToString(b, Base64.NO_WRAP);
            returnBase64 = base64Image;
            baos.close();
            baos = null;
            bitmap = null;

        } catch (OutOfMemoryError e) {
            ApzLogger.e(TAG, "Exception in base64 image" + e);
            failureCallback("Exception in base64 image","");
        } catch (IOException e) {
            ApzLogger.e(TAG, "Exception in base64 image" + e);
            failureCallback("Exception in base64 image","");
        }

        return returnBase64;
    }

    public static void failureCallback(String mMessage, String errorCode) {
        JSONObject json = new JSONObject();
        try {
            json.put("text", "" + mMessage);
            if (errorCode.equalsIgnoreCase("")) {
                errorCode = "APZ-CNT-341";
            }
        } catch (JSONException e) {
        }
        ApzPluginUtil.sendError(mCallbackId, errorCode, json, mActivity, mWebview, true);
        pluginObj = null;
    }

    private static String saveImage(Bitmap bitmp) {

        String photoFile;
        String photoFilepath = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyhhmmss");
        String date = dateFormat.format(new Date());
        photoFile = fileName + date + mEncodingFormat;
        try {

            File pictureFileDir = getDir("ScannedDocuments");
            if (pictureFileDir != null) {
                String filenameWithPath = pictureFileDir.getPath()
                        + File.separator + photoFile;
                pictureFile = new File(filenameWithPath);
                if (pictureFile.exists()) {
                    if (pictureFile.delete()) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent mediaScanIntent = new Intent(
                                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri
                                    .parse("file://"
                                            + Environment
                                            .getExternalStorageDirectory()); // out
                            mediaScanIntent.setData(contentUri);
                            mActivity.sendBroadcast(mediaScanIntent);
                        } else {
                            mActivity.sendBroadcast(new Intent(
                                    Intent.ACTION_MEDIA_MOUNTED,
                                    Uri.parse("file://"
                                            + Environment
                                            .getExternalStorageDirectory())));
                        }
                        // Abhishek 18 March 2015 to handle the crash while
                        // sending broadcast in KITKAT and above END
                        try {

                            boolean isCreated = pictureFile.createNewFile();

                        } catch (IOException e) {

                        } catch (OutOfMemoryError e) {
                            failureCallback("","");
                        }

                    }
                }
                FileOutputStream fos = new FileOutputStream(pictureFile);

                final BufferedOutputStream bos = new BufferedOutputStream(fos,
                        1024 * 8);
                bitmp.compress(mCompressFormat, cmpLevel, bos);
                bos.flush();
                bos.close();
                fos.close();
                photoFilepath = filenameWithPath;

                MediaScannerConnection.scanFile(mActivity.getApplicationContext(),
                        new String[]{pictureFile.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
//                                ApzLogger.i(TAG, "ExternalStorage Scanned "
//                                        + path + ":");
//                                ApzLogger.i(TAG, "ExternalStorage -> uri="
//                                        + uri);
                            }
                        });
            }

        } catch (FileNotFoundException e) {
            ApzLogger.e(TAG, e.getMessage());
        } catch (IOException e) {
            ApzLogger.e(TAG, e.getMessage());
        } catch (OutOfMemoryError e) {
            ApzLogger.e(TAG, "Exception in saving image" + e);
            failureCallback("Exception in saving image","");
        }
        return photoFilepath;

    }

    private static File getDir(String loc) {
        if (MediaUtils.isSDCardPresent()) {
            File sdDir = new File(SANDBOX_LOC
                    + File.separator + ASSET_APP_LOC + loc);
            try {
                if (sdDir.exists()) {
                    return sdDir;
                } else {
                    if (sdDir.mkdirs()) {
                        return sdDir;
                    } else {
                        return null;
                    }
                }
            } catch (Exception e) {

                ApzLogger.e(TAG, "Create directory failed : " + e.getMessage());
            }
        } else {
            failureCallback("Operation cancelled","");
        }

        return null;
    }
/*    public void copyTessData() {
        try {

          //  String fileList[] = activity.getApplicationContext().getAssets().list(TESSERACT_TRAINED_DATA_FOLDER);


                String pathToDataFile = TESSERACT_PATH + TESSERACT_TRAINED_DATA_FOLDER + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {

                    *//*File storageDir = new File(TESSERACT_PATH + TESSERACT_TRAINED_DATA_FOLDER + "/");
                    storageDir.mkdirs();*//*

                    InputStream in = activity.getApplicationContext().getAssets().open(TESSERACT_TRAINED_DATA_FOLDER + "/" + fileName);
                    if (is != null) {
                        br = new BufferedReader(new InputStreamReader(is));
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        br.close();
                    }




                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte[] buf = new byte[1024];
                    int length;
                    while ((length = in.read(buf)) > 0) {
                        out.write(buf, 0, length);
                    }
                    in.close();
                    out.close();

            }
        } catch (IOException e) {
            failureCallback("Tesseract data not found");
        }
    }*/
    private static void setCompressFormat(String mEncodingType2) {
        if (mEncodingType2.equalsIgnoreCase("JPG")
                || mEncodingType2.equalsIgnoreCase("JPEG")) {
            mCompressFormat = Bitmap.CompressFormat.JPEG;
            mEncodingFormat = ".jpg";
        } else if (mEncodingType2.equalsIgnoreCase("PNG")) {
            mCompressFormat = Bitmap.CompressFormat.PNG;
            mEncodingFormat = ".png";
        } else {
            mCompressFormat = Bitmap.CompressFormat.JPEG;
            mEncodingFormat = ".jpg";
        }

    }

    public static boolean isAutoCapture() {
        return true;
    }

}
