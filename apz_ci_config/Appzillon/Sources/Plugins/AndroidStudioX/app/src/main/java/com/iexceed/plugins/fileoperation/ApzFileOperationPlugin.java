package com.iexceed.plugins.fileoperation;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.ExternalActivityResultHandler;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.BuildConfig;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.auditlog.AuditLog;
import com.iexceed.plugins.errorlog.ApzLogger;

import java.io.File;

public class ApzFileOperationPlugin extends ApzPlugin {

    private static ApzPlugin pluginObj;
    public static int RESULT_LOAD_IMAGE = 1;
    public static int RESULT_LOAD_VIDEO = 2;
    public static int RESULT_LOAD_AUDIO = 3;
    public static final int BROWSE_FILE = 102;
    String isOpenable = null;
    JSONObject result = null;
    private String TAG = "ApzFileOperationPlugin";
    private String mAction;
    private JSONObject mParams;
    private String mCallbackId;
    private String[] permissions=new String[]{};


    private ApzFileOperationPlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzFileOperationPlugin(webView, activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {

        try {
            mParams = params;
            callbackId = params.getString("id");
            mCallbackId = callbackId;
            mAction = params.getString("action");
            boolean conditionCheck = false;
            if (!mAction.equalsIgnoreCase("FILECREATE")
                    && params.has("filePath")
                    && !params.getString("filePath").equalsIgnoreCase("")
                    && !params.getString("filePath").contains(BuildConfig.APPLICATION_ID)) {
                File file = new File(params.getString("filePath"));
                if(file.isDirectory() || file.isFile()){
                    conditionCheck = true;
                }

            } else if (params.has("fileCategory") && params.optString("fileCategory").equals("EXTERNAL")) {
                conditionCheck = true;
            }
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && (("N".equalsIgnoreCase(activity.getResources().getString(R.string.INTERNALSANDBOX)))
                    || conditionCheck))) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ) {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestForPermission();
                    } else {
                        proceedFileOperation();
                    }
                } else {
                    if(conditionCheck){
                        switch (mParams.optString("fileCategory","EXTERNAL")){
                            case "PHOTO" : {
                                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
                                }
                                break;
                            }
                            case "AUDIO" : {
                                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    permissions = new String[]{Manifest.permission.READ_MEDIA_AUDIO};
                                }
                                break;
                            }
                            case "VIDEO" : {
                                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    permissions = new String[]{Manifest.permission.READ_MEDIA_VIDEO};
                                }
                                break;
                            }
                            case "EXTERNAL":{

                                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO)
                                        != PackageManager.PERMISSION_GRANTED||
                                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
                                        != PackageManager.PERMISSION_GRANTED||
                                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                                                != PackageManager.PERMISSION_GRANTED) {
                                    permissions=new String[]{Manifest.permission.READ_MEDIA_VIDEO,
                                            Manifest.permission.READ_MEDIA_IMAGES,
                                            Manifest.permission.READ_MEDIA_AUDIO };

                                }
                                break;
                            }
                        }
                        if (permissions.length > 0) {
                            requestForPermission();
                        } else {
                            proceedFileOperation();
                        }
                    } else {
                        proceedFileOperation();
                    }
                }
            } else {
                proceedFileOperation();
            }
        } catch (Exception e1) {
            ApzLogger.i(TAG, e1.toString());
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
                                proceedFileOperation();
                            }
                        } else {
                            PermissionDeniedCallback();
                        }

                    }
                }
        );
    }
    private void displayReconfirmationMessage() {
        String message = "To access files,allow app to access by granting requested permissions";
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
        ApzPluginUtil.sendPermissionDenied("File access",callbackId, this.activity,this.webView);
    }

    private void proceedFileOperation() {
        if (mAction.equalsIgnoreCase("BROWSER")) {
            String fileCategory = null;

            try {
                fileCategory = mParams.getString("fileCategory");
                if (fileCategory.equals("AUDIO")) {
                    isOpenable = mParams.getString("openFile");
                    Intent audio = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    this.activity.startActivityForResult(audio,
                            RESULT_LOAD_AUDIO,
                            new ExternalActivityResultHandler() {

                                @Override
                                public void handleActivityResult(
                                        int resultCode, Intent data) {
                                    if (resultCode == Activity.RESULT_OK) {
                                        Uri selectedImage = data.getData();
                                        String[] filePathColumn = {MediaStore.Audio.Media.DATA};
                                        Cursor cursor = activity
                                                .getContentResolver().query(
                                                        selectedImage,
                                                        filePathColumn, null,
                                                        null, null);
                                        if (cursor.moveToFirst()) {
                                            int columnIndex = cursor
                                                    .getColumnIndex(filePathColumn[0]);
                                            String audioPath = cursor
                                                    .getString(columnIndex);
                                            cursor.close();
                                            try {
                                                if (isOpenable.equals("Y")) {
                                                    openFile(audioPath);
                                                } else {
                                                    result = new JSONObject();
                                                    result.put("filePath",
                                                            audioPath);
                                                    ApzPluginUtil.sendSuccess(
                                                            mCallbackId, result,
                                                            false, activity,
                                                            webView, false);
                                                }
                                            } catch (ActivityNotFoundException anf) {
                                                ApzLogger.i(TAG, anf.toString());
                                            } catch (Exception e) {
                                                ApzLogger.i(TAG, e.toString());
                                            }
                                        }
                                        if (cursor != null) {
                                            cursor.close();
                                        }
                                    } else {
                                        ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-328", null, activity, webView, true);
                                    }
                                }
                            });

                } else if (fileCategory.equals("VIDEO")) {
                    isOpenable = mParams.getString("openFile");
                    Intent video = new Intent(Intent.ACTION_PICK, null);
                    video.setType("video/*");
                    this.activity.startActivityForResult(video,
                            RESULT_LOAD_VIDEO,
                            new ExternalActivityResultHandler() {

                                @Override
                                public void handleActivityResult(
                                        int resultCode, Intent data) {
                                    if (resultCode == Activity.RESULT_OK) {
                                        Uri selectedImage = data.getData();
                                        String[] filePathColumn = {MediaStore.Video.Media.DATA};
                                        Cursor cursor = activity
                                                .getApplicationContext()
                                                .getContentResolver()
                                                .query(selectedImage,
                                                        filePathColumn, null,
                                                        null, null);
                                        if (cursor.moveToFirst()) {
                                            int columnIndex = cursor
                                                    .getColumnIndex(filePathColumn[0]);
                                            String videoPath = cursor
                                                    .getString(columnIndex);
                                            cursor.close();
                                            try {
                                                if (isOpenable
                                                        .equals("Y")) {
                                                    openFile(videoPath);
                                                } else {
                                                    AuditLog.sendToJSON();
                                                    result = new JSONObject();
                                                    result.put("filePath",
                                                            videoPath);
                                                    ApzPluginUtil.sendSuccess(
                                                            mCallbackId, result,
                                                            false, activity,
                                                            webView, false);
                                                }
                                            } catch (ActivityNotFoundException anf) {
                                                ApzLogger.i(TAG, anf.toString());
                                            } catch (Exception e) {
                                                ApzLogger.i(TAG, e.toString());
                                            }
                                        }
                                        if (cursor != null) {
                                            cursor.close();
                                        }
                                    } else {
                                        ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-328", null, activity, webView, true);
                                    }
                                }
                            });
                } else if (fileCategory.equals("PHOTO")) {
                    isOpenable = mParams.getString("openFile");
                    Intent photo = new Intent(Intent.ACTION_PICK, null);
                    photo.setType("image/*");
                    this.activity.startActivityForResult(photo,
                            RESULT_LOAD_IMAGE,
                            new ExternalActivityResultHandler() {

                                @Override
                                public void handleActivityResult(
                                        int resultCode, Intent data) {
                                    if (resultCode == Activity.RESULT_OK) {
                                        try {
                                            String picturePath = null;
                                            Cursor cursor = null;
                                            Uri selectedImage = data.getData();
                                            if (selectedImage.toString()
                                                    .contains("file:///")) {
                                                picturePath = selectedImage
                                                        .toString()
                                                        .substring(7);
                                            } else {
                                                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                                                cursor = activity
                                                        .getApplicationContext()
                                                        .getContentResolver()
                                                        .query(selectedImage,
                                                                filePathColumn,
                                                                null, null,
                                                                null);
                                                if (cursor.moveToFirst()) {
                                                    int columnIndex = cursor
                                                            .getColumnIndex(filePathColumn[0]);
                                                    picturePath = cursor
                                                            .getString(columnIndex);
                                                    cursor.close();
                                                }
                                            }
                                            if (isOpenable
                                                    .equals("Y")) {
                                                openFile(picturePath);
                                            } else {
                                                AuditLog.sendToJSON();
                                                result = new JSONObject();
                                                result.put("filePath",
                                                        picturePath);
                                                ApzPluginUtil.sendSuccess(
                                                        mCallbackId, result,
                                                        false, activity,
                                                        webView, false);
                                            }

                                            if (cursor != null) {
                                                cursor.close();
                                            }
                                        } catch (ActivityNotFoundException anf) {
                                            ApzLogger.e(TAG, anf.toString());
                                        } catch (Exception e) {
                                            ApzLogger.e(TAG, e.toString());
                                        }

                                    } else {
                                        ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-328", null, activity, webView, true);
                                    }

                                }
                            });
                } else if (fileCategory.equals("DEFAULT")) {
                    Intent browserIntent = new Intent(
                            activity.getApplicationContext(),
                            DirectoryBrowser.class);
                    browserIntent.putExtra("location", "");
                    browserIntent.putExtra("root", "DEFAULT");
                    browserIntent.putExtra("filter", "");
                    browserIntent.putExtra("openFile",
                            mParams.getString("openFile"));
                    //activity.startActivityForResult(browserIntent, BROWSE_FILE);

                    this.activity.startActivityForResult(browserIntent,
                            BROWSE_FILE, new ExternalActivityResultHandler() {

                                @Override
                                public void handleActivityResult(
                                        int resultCode, Intent data) {

                                    if (resultCode == Activity.RESULT_OK) {
                                        String filePath = data
                                                .getStringExtra("filePath");
                                        AuditLog.sendToJSON();
                                        try {
                                            result = new JSONObject();
                                            result.put("filePath", filePath);
                                        } catch (JSONException e) {
                                            ApzLogger.i(TAG, e.toString());
                                        }
                                        ApzPluginUtil.sendSuccess(mCallbackId,
                                                result, false, activity,
                                                webView, false);

                                    } else {
                                        if (data != null) {
                                            String error = data
                                                    .getStringExtra("error");
                                            if (error.equalsIgnoreCase("ANF")) {
                                                ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-275", null, activity, webView, true);
                                            }
                                        } else
                                            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-328", null, activity, webView, true);
                                    }

                                }

                            });

                } else if (fileCategory.equals("EXTERNAL")) {
                    Intent browserIntent = new Intent(
                            activity.getApplicationContext(),
                            DirectoryBrowser.class);
                    browserIntent.putExtra("location", "EXTERNAL");
                    browserIntent.putExtra("root", "DEFAULT");
                    browserIntent.putExtra("filter", "");
                    browserIntent.putExtra("openFile",
                            mParams.getString("openFile"));
                    //activity.startActivityForResult(browserIntent, BROWSE_FILE);
                    this.activity.startActivityForResult(browserIntent,
                            BROWSE_FILE, new ExternalActivityResultHandler() {

                                @Override
                                public void handleActivityResult(
                                        int resultCode, Intent data) {

                                    if (resultCode == Activity.RESULT_OK) {
                                        String filePath = data
                                                .getStringExtra("filePath");
                                        AuditLog.sendToJSON();
                                        try {
                                            result = new JSONObject();
                                            result.put("filePath", filePath);
                                        } catch (JSONException e) {

                                        }
                                        ApzPluginUtil.sendSuccess(mCallbackId,
                                                result, false, activity,
                                                webView, false);
                                    } else {
                                        ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-328", null, activity, webView, true);
                                    }
                                }
                            });
                } else {
                    Intent browserIntent = new Intent(
                            activity.getApplicationContext(),
                            DirectoryBrowser.class);
                    browserIntent.putExtra("location",
                            mParams.getString("location"));
                    browserIntent
                            .putExtra("filter", mParams.getString("filter"));
                    browserIntent.putExtra("openFile",
                            mParams.getString("openFile"));
                    //activity.startActivityForResult(browserIntent, BROWSE_FILE);

                    this.activity.startActivityForResult(browserIntent,
                            BROWSE_FILE, new ExternalActivityResultHandler() {

                                @Override
                                public void handleActivityResult(
                                        int resultCode, Intent data) {

                                    if (resultCode == Activity.RESULT_OK) {
                                        String filePath = data
                                                .getStringExtra("filePath");
                                        AuditLog.sendToJSON();
                                        try {
                                            result = new JSONObject();
                                            result.put("filePath", filePath);
                                        } catch (JSONException e) {
                                            ApzLogger.i(TAG, e.toString());
                                        }
                                        ApzPluginUtil.sendSuccess(mCallbackId,
                                                result, false, activity,
                                                webView, false);
                                    } else {
                                        ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-328", null, activity, webView, true);
                                    }
                                }
                            });
                }
            } catch (JSONException e) {
                ApzLogger.i(TAG, e.toString());
                JSONObject json = new JSONObject();
                try {
                    json.put("errorCode", e.toString());
                } catch (JSONException e1) {
                    ApzLogger.i(TAG, e1.toString());
                }
                ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-227", json, activity, webView, true);
            } catch (Exception e) {
                ApzLogger.i(TAG, e.toString());
            }
        } else if (mAction.equalsIgnoreCase("FILECONTENT")) {
            FileOperation fileOp = new FileOperation(activity, webView);
            fileOp.getFileContent(mParams);
        } else if (mAction.equalsIgnoreCase("FILECREATE")) {
            FileOperation fileOp = new FileOperation(activity, webView);
            fileOp.createFile(mParams);
        } else if (mAction.equalsIgnoreCase("FILEDELETE")) {
            FileOperation fileOp = new FileOperation(activity, webView);
            fileOp.deleteFile(mParams);
        } else if (mAction.equalsIgnoreCase("OPENFILE")) {
            FileOperation fileOp = new FileOperation(activity, webView);
            fileOp.openFile(mParams);
        } else if (mAction.equalsIgnoreCase("GETFILESIZE")) {
            GetFileSize.getFileSize(webView, activity, mParams);
        } else if (mAction.equalsIgnoreCase("FILEUPLOAD")) {
            FileUpload mFileUpload = new FileUpload(webView, activity);
            mFileUpload.uploadFile(mParams);
        } else if (mAction.equalsIgnoreCase("FILEDOWNLOAD")) {
            FileDownload mFileUpload = new FileDownload(activity.getApplicationContext(), activity, webView);
            mFileUpload.downloadFile(mParams);
        }else if (mAction.equalsIgnoreCase("FILEDOWNLOAD_MANAGER")) {
            FileDownloadManager mFileDownload = new FileDownloadManager(activity.getApplicationContext(), activity, webView);
            mFileDownload.downloadFile(mParams);
        }
    }

    public void openFile(String path) {

        File file = new File(path);
        if (file.exists()) {

            try {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID, file));
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(i);
            } catch (ActivityNotFoundException act) {
                ApzLogger.e(TAG, "Activity not found");
                ApzPluginUtil.sendError(callbackId, "APZ-CNT-275", null, activity, webView, true);
            }

        } else {
            ApzLogger.e(TAG, "File not found");
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-002", null, activity, webView, true);//File Not found
        }
    }

    public static boolean isFileOperation() {
        return true;
    }
}

