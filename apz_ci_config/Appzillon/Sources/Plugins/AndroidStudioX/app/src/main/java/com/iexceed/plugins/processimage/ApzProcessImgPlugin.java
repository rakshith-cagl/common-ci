package com.iexceed.plugins.processimage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.util.Base64;
import android.webkit.WebView;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.MediaUtils;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

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

public class ApzProcessImgPlugin extends ApzPlugin {
    private static ApzPlugin pluginObj;
    private final String TAG = "ApzProcessImgPlugin";
    private JSONObject mJsonObj;
    private String mAction;
    private String mInputFileType = "";
    private String mInputData = "";
    private String mEncodingType = "";
    public Bitmap.CompressFormat mCompressFormat;
    private String mEncodingFormat;
    private String FILENAME;
    private static int mThreshold;
    File pictureFile;
    private int cmpLevel ;
    private String[] permissions;

    public ApzProcessImgPlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzProcessImgPlugin(webView, activity);
        }
        return pluginObj;
    }


    @Override
    public void execute(JSONObject params) {
        try {
           // mAction = mJsonObj.getString("action");
            mJsonObj=params;
            this.callbackId = params.getString("id");
            boolean isSourceTypeExternal = false;
            mInputFileType = mJsonObj.optJSONObject("inputFile").optString("type");
            FILENAME = mJsonObj.optJSONObject("outputFile").optString("fileName");
            String threshold = mJsonObj.optJSONObject("outputFile").optString("threshold");
            mInputData = mJsonObj.optJSONObject("inputFile").optString("data");
            mEncodingType = mJsonObj.optString("encodingType");
            String compressionLevel = mJsonObj.optString("quality");
            if (!compressionLevel.equals("")) {
                cmpLevel = Integer.parseInt(compressionLevel);
            } else {
                cmpLevel = 100;
            }
            if(!threshold.equalsIgnoreCase("")){
                mThreshold=Integer.parseInt(threshold);
            }else{
                mThreshold=128;
            }
            setCompressFormat(mEncodingType);
            if(mInputFileType.equalsIgnoreCase("file")){
                isSourceTypeExternal=true;
            }

            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && "N".equalsIgnoreCase(activity.getResources().getString(R.string.INTERNALSANDBOX)))
                    ||isSourceTypeExternal){
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ) {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestForPermission();
                    } else {
                        callProccessor();
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                            != PackageManager.PERMISSION_GRANTED) {
                        permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
                        requestForPermission();
                    } else {
                        callProccessor();
                    }
                }

            } else {
                callProccessor();
            }

        } catch (Exception e) {
            failureCallback("Exception occured");
        }
    }

    private void requestForPermission() {
        this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_WRITE_STORAGE, new OnPermissionsResultHandler() {
                    @Override
                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == ApzPlugin.APZ_REQ_WRITE_STORAGE) {
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
                                callProccessor();
                            }
                        } else {
                            PermissionDeniedCallback();
                        }

                    }
                }
        );
    }

    private void displayReconfirmationMessage() {
        String message = "To access image from gallery, allow app to access by requested permissions";

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
        ApzPluginUtil.sendPermissionDenied("Image Storage", this.callbackId, this.activity, this.webView);
    }

    public void callProccessor() {
        try {
            String mOutputFileType = mJsonObj.optJSONObject("outputFile").optString("type","file");
            String mAction=mJsonObj.optString("imageAction","grayscale");
            String outputData="";
            if(mAction.equalsIgnoreCase("")){
                mAction="grayscale";
            }

            JSONObject resultBody = new JSONObject();
            JSONObject internalres = new JSONObject();
            if(!mInputData.equalsIgnoreCase("")) {
                Bitmap inputBitmap = getBitmap(mInputFileType, mInputData);
                if (inputBitmap != null) {
                    inputBitmap = convertToBlackWhite(inputBitmap, mAction);
                    if (mOutputFileType.equalsIgnoreCase("base64")) {
                        outputData = getBase64Image(inputBitmap);
                    }else{
                        outputData = saveImage(inputBitmap);
                    }
                    inputBitmap=null;
                    internalres.put("type", mOutputFileType);
                    internalres.put("data", outputData);
                    resultBody.put("outputFile", internalres);
                    resultBody.put("imageAction", mAction);
                    ApzPluginUtil.sendSuccess(
                            this.callbackId,
                            resultBody, false,
                            this.activity,
                            this.webView, true);
 pluginObj = null;
                }
            }else{
                failureCallback("Image data is invalid");
            }
        } catch (Exception e) {
            failureCallback("Exception occured");
        }
    }
    private void setCompressFormat(String mEncodingType2) {
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
    public Bitmap getBitmap(String fileType, String fileData){
        Bitmap finalBitmap=null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        if(fileType.equalsIgnoreCase("file")){
            finalBitmap= BitmapFactory.decodeFile(fileData,bmOptions);
        }else if(fileType.equalsIgnoreCase("base64")){
            byte[] decodedString = Base64.decode(
                    fileData, Base64.NO_WRAP);
            finalBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }


        return finalBitmap;
    }  public static Bitmap convertToBlackWhite(Bitmap colorBmp,String mAction) {
        Bitmap bmpMonochrome = Bitmap.createBitmap(colorBmp.getWidth(), colorBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpMonochrome);
        //set contrast
        ColorMatrix contrastMatrix = new ColorMatrix();
//change contrast
        if(mAction.equalsIgnoreCase("grayscale")) {
            contrastMatrix.setSaturation(0);
        }else if(mAction.equalsIgnoreCase("BW")){
            contrastMatrix.setSaturation(0);
            contrastMatrix.set(new float[]{
                    128, 128, 128, 0, -(mThreshold) * 255,
                    128, 128, 128, 0, -(mThreshold) * 255,
                    128, 128, 128, 0, -(mThreshold) * 255,
                    0, 0, 0, 1, 0});
        }
//apply contrast
        Paint contrastPaint = new Paint();
        contrastPaint.setColorFilter(new ColorMatrixColorFilter(contrastMatrix));
        canvas.drawBitmap(colorBmp, 0, 0, contrastPaint);
        return bmpMonochrome;
    }
    private String getBase64Image(Bitmap bitmap) {
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
    public void failureCallback(String mMessage){
        JSONObject json = new JSONObject();
        try {
            json.put("text", "Operation Cancelled");
        } catch (JSONException e) {
        }
        ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-065", json, this.activity, this.webView, true);
    }
    private String saveImage(Bitmap bitmp) {

        String photoFile;
        String photoFilepath = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmyyhhmmss");
        String date = dateFormat.format(new Date());
        photoFile = FILENAME + date + mEncodingFormat;
        try {

            File pictureFileDir = getDir("photo");
            if (pictureFileDir != null) {
                String filenameWithPath = pictureFileDir.getPath()
                        + File.separator + photoFile;
//                pictureFile = new File("" + filenameWithPath );
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
                            activity.sendBroadcast(mediaScanIntent);
                        } else {
                            activity.sendBroadcast(new Intent(
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

                MediaScannerConnection.scanFile(activity.getApplicationContext(),
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
    private File getDir(String loc) {
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
    public static boolean IsImgProcessing() {
        return true;
    }
}
