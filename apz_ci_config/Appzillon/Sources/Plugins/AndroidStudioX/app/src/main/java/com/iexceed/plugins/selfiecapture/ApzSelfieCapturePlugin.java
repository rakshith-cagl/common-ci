package com.iexceed.plugins.selfiecapture;

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
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.CameraUtils;
import com.iexceed.common.MediaUtils;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.selfiecapture.LivePreviewActivity;
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
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import androidx.exifinterface.media.ExifInterface;
import java.io.OutputStream;
import static android.content.Context.MODE_PRIVATE;

import static com.iexceed.appzillonapp.AppzillonMainScreen.ASSET_APP_LOC;
import static com.iexceed.appzillonapp.AppzillonMainScreen.SANDBOX_LOC;
import static com.iexceed.plugins.selfiecapture.LivePreviewActivity.fInstruction1;
import static com.iexceed.plugins.selfiecapture.LivePreviewActivity.fInstruction2;
import static com.iexceed.plugins.selfiecapture.LivePreviewActivity.fInstruction3;

public class ApzSelfieCapturePlugin extends ApzPlugin {
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
    public static String blinkEyeDetection = "N";
    public static String facePageTitle = "";
    public static String faceInstruction1 = "";
    public static String faceInstruction2 = "";
    public static String faceInstruction3 = "";
    public static String blinkInstruction = "";
    public static String faceScanningMsg = "";
    public static String mOutputFileType = "";
    public static String faceFontColor = "";
    public static String faceOverlayColor = "";
    public static String nativePreviewScreen = "";
    public static int instructionPosition ;
    public static int holdTimeForCapture ;
    public static String holdTimeInstruction = "";



    static File pictureFile;
    private static int cmpLevel ;

    private static int HTMLWIDTH = 0;
    private static int HTMLHEIGHT = 0;
    private ApzSelfieCapturePlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
        mWebview = webView;
        mActivity = activity;
    }

    @Override
    public void execute(JSONObject params) {
        try {
            this.callbackId = params.getString("id");
            mCallbackId=this.callbackId;
            this.mJsonObj = params;
            fileName = mJsonObj.optString("fileName");
            mEncodingType = mJsonObj.optString("encodingType").trim();
            blinkEyeDetection = mJsonObj.optString("blinkEyeDetection").trim();
            facePageTitle = mJsonObj.optString("pageTitle");
            faceInstruction1 = mJsonObj.optString("faceInstruction1");
            faceInstruction2 = mJsonObj.optString("faceInstruction2");
            faceInstruction3 = mJsonObj.optString("faceInstruction3");
            blinkInstruction = mJsonObj.optString("blinkInstruction");
            String captureTime = mJsonObj.optString("holdTimeForCapture").trim();
            holdTimeForCapture=(captureTime.equalsIgnoreCase(""))?2:Integer.parseInt(captureTime);
            holdTimeInstruction = mJsonObj.optString("holdTimeInstruction");
            nativePreviewScreen = mJsonObj.optString("nativePreviewScreen").trim();
            String position = mJsonObj.optString("instructionPosition").trim();
            instructionPosition=(position.equalsIgnoreCase(""))?3:Integer.parseInt(position);
            faceScanningMsg = mJsonObj.optString("scanStatus");
            faceFontColor = mJsonObj.optString("fontColor");
            faceOverlayColor = mJsonObj.optString("overlayColor");
            // color code validation
            String colorRegex="^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
            if (!(faceFontColor.matches(colorRegex) && faceOverlayColor.matches(colorRegex))) {
                faceFontColor = "#000000";
                faceOverlayColor = "#ffffff";
            }
            String compressionLevel = mJsonObj.optString("quality").trim();
            if (!compressionLevel.equals("")) {
                cmpLevel = Integer.parseInt(compressionLevel);
            } else {
                cmpLevel = 100;
            }
            setCompressFormat(mEncodingType);
            String targetWidth = mJsonObj.optString("targetWidth").trim();
            if (!targetWidth.isEmpty() && targetWidth != null) {
                HTMLWIDTH = Integer.parseInt(targetWidth);
            }
            String targetHeight = mJsonObj.optString("targetHeight").trim();
            if (!targetHeight.isEmpty() && targetHeight != null) {
                HTMLHEIGHT = Integer.parseInt(targetHeight);
            }
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                boolean isSourceTypeExternal=false;
                if("N".equalsIgnoreCase(activity.getResources().getString(R.string.INTERNALSANDBOX))){
                    isSourceTypeExternal=true;
                }

                // Android 13 storage change
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions = new String[]{
                            Manifest.permission.CAMERA};
                    requestForPermission();
                } else {
                    callSelfieCapture();
                }

            } else {
                callSelfieCapture();
            }
//			ApzLogger.e(TAG, "Plugin Payload "+ params);
        } catch (Exception e) {
            ApzLogger.e(TAG, e.getMessage());
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-211", null, activity, webView, true);
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
                            } else {
                                callSelfieCapture();
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

    private void PermissionDeniedCallback(){
        ApzPluginUtil.sendPermissionDenied("Camera or Storage ",callbackId, this.activity,this.webView);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzSelfieCapturePlugin(webView, activity);
        }
        return pluginObj;
    }
    private void callSelfieCapture() {

        try {
             mOutputFileType = mJsonObj.optString("type","file");
                   Intent capture = new Intent(activity, LivePreviewActivity.class);
            capture.putExtra("jsonStr", mJsonObj.toString());
            this.activity.startActivity(capture);
          /*  this.activity.startActivityForResult(capture, AUTO_CAPTURE_PIC, new ExternalActivityResultHandler() {

                @Override
                public void handleActivityResult(int resultCode, Intent data) {
                    // TODO Auto-generated method stub

                    try {
                        if (resultCode == Activity.RESULT_OK) {
                            Log.i(TAG, "handleActivityResult: "+resultCode);

                            byte[] outputByte;
                            if(data!=null) {
                                 String outputData="";
                                outputByte = data.getExtras().getByteArray("bitmap");
                                JSONObject resultBody = new JSONObject();
                                JSONObject internalres = new JSONObject();
                                if ((outputByte!=null)&&outputByte.length!=0) {
                                    Bitmap inputBitmap = BitmapFactory.decodeByteArray(outputByte, 0, outputByte.length);
                                    if (inputBitmap != null) {
                                        if (mOutputFileType.equalsIgnoreCase("base64")) {
                                            outputData = getBase64Image(inputBitmap);
                                        } else {
                                            outputData = saveImage(inputBitmap);
                                        }
                                        inputBitmap = null;
                                        internalres.put("type", mOutputFileType);
                                        internalres.put("data", outputData);
                                        resultBody.put("outputFile", internalres);
                                        ApzPluginUtil.sendSuccess(
                                                mCallbackId,
                                                resultBody, false,
                                                mActivity,
                                                mWebview, true);
                                    }
                                } else {
                                    failureCallback("Error occured");
                                }
                            }
                        }else if(resultCode==RESULT_CANCELED){
                             failureCallback("Operation cancelled");
                        }
                    }catch(Exception e){
                        failureCallback("Exception occured");
                    }
                }
            });*/

        }catch (Exception e){
            failureCallback("Operation failed");
        }


    }
    public static void fetchOutput(Bitmap bitmap){
        String outputData="";
        try {
            JSONObject resultBody = new JSONObject();
            JSONObject internalres = new JSONObject();
            if ((bitmap != null)) {
                //Bitmap inputBitmap = bitmap;
                Bitmap inputBitmap;
                if(HTMLHEIGHT >0 && HTMLWIDTH > 0){
                    inputBitmap = CameraUtils.compressSelfieBmpByHeightWeigth(bitmap,HTMLWIDTH,HTMLHEIGHT,mActivity);
                }else{
                    inputBitmap = bitmap;
                }
                if (mOutputFileType.equalsIgnoreCase("base64")) {
                    outputData = getBase64Image(inputBitmap);
                } else {
                    outputData = saveImage(inputBitmap);
                }
                inputBitmap = null;
                internalres.put("type", mOutputFileType);
                internalres.put("data", outputData);
                resultBody.put("outputFile", internalres);
                ApzPluginUtil.sendSuccess(
                        mCallbackId,
                        resultBody, false,
                        mActivity,
                        mWebview, true);
    pluginObj=null;
            }
        }catch (Exception e){

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
            failureCallback("Exception in base64 image");
        } catch (IOException e) {
            ApzLogger.e(TAG, "Exception in base64 image" + e);
            failureCallback("Exception in base64 image");
        }

        return returnBase64;
    }
    public static void failureCallback(String mMessage){
        JSONObject json = new JSONObject();
        try {
            json.put("text", ""+mMessage);
        } catch (JSONException e) {
        }
        ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-211", json, mActivity, mWebview, true);
        pluginObj=null;
    }
    private static String saveImage(Bitmap bitmp) {

        String photoFile;
        String photoFilepath = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyhhmmss");
        String date = dateFormat.format(new Date());
        photoFile = fileName + date + mEncodingFormat;
        try {

            File pictureFileDir = getDir("Selfies");
            if (pictureFileDir != null) {
                String filenameWithPath = pictureFileDir.getPath()
                        + File.separator + photoFile;
//                pictureFile = new File(filenameWithPath);
                pictureFile = AppzillonUtils.getApzFile(pictureFileDir.getPath() + File.separator + photoFile,null);
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
                            failureCallback(e.getMessage());
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
            failureCallback("Exception in saving image");
        }
        return photoFilepath;

    }
    private static File getDir(String loc) {
        if (MediaUtils.isSDCardPresent()) {
//            File sdDir = new File(SANDBOX_LOC
//                    + File.separator + ASSET_APP_LOC + loc);
            File sdDir = AppzillonUtils.getApzFile(SANDBOX_LOC + File.separator + ASSET_APP_LOC + loc,null);
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
            failureCallback("Operation cancelled");
        }

        return null;
    }
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
    public static void setInstructionTextView(final String message){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                   switch (instructionPosition){
                       case 1:
                           fInstruction1.setText(message);
                           break;
                           case 2:
                           fInstruction2.setText(message);
                           break;
                       default:
                           fInstruction3.setText(message);
                   }

                } catch (Exception e) {

                }
            }
        });
    }
    public static boolean isSelfieCapture() {
        return true;
    }

}
