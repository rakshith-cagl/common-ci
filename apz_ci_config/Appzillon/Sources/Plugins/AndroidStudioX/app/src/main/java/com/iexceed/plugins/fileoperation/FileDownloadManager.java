package com.iexceed.plugins.fileoperation;

import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.webkit.WebView;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import static android.content.Context.DOWNLOAD_SERVICE;

public class FileDownloadManager {

    private Context mContext;

    private Activity mActivity;

    private WebView mWebView;

    private String TAG = "DOWNLOADMANAGER ";

    private String base64;

    private String downloadExternalPath;

    private String callerId;


    private DownloadManager downloadManager;
    private String fileName=null;
    private long downLoadId;
    private String fileURL;


    public FileDownloadManager(Context context, Activity activity, WebView webview) {
        mContext = context;
        mActivity = activity;
        mWebView = webview;
    }

    public void downloadFile(JSONObject jsonObj) {


        try {
            callerId = jsonObj.getString("id");
             fileURL = jsonObj.optString("fileURL");
             fileName = jsonObj.optString("fileName");

                // DownloadManager Changes

                downloadManager= (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
                mContext.registerReceiver(onComplete,
                        new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(fileURL));

                request.setTitle("Download")
                        .setDescription("File is downloading...")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                downLoadId=downloadManager.enqueue(request);




        } catch (Exception e1) {
            ApzLogger.e(TAG,e1.toString());
            downloadFailure(mContext.getResources().getString(R.string.download_json_error));
        }


    }



    private void downloadSuccess(final String path) {
        final JSONObject fileDownloadRes = new JSONObject();
        try {
                fileDownloadRes.put("filePath", path);


        } catch (final JSONException ex) {
            ApzLogger.e(TAG,ex.toString());
            return;
        }

        ApzPluginUtil.sendSuccess(callerId, fileDownloadRes, false, mActivity, mWebView,
                true);
    }

    private void downloadFailure(final String errorMsg) {
        JSONObject json = new JSONObject();
        try {
            json.put("errorMessage", errorMsg);
        } catch (JSONException e) {
        }
        ApzPluginUtil.sendError(callerId, "APZ-CNT-079", json, mActivity,
                mWebView, true);
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if the broadcast message is for our Enqueued download
            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (referenceId == downLoadId) {
                    try {
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(downLoadId);
                        Cursor c = downloadManager.query(query);

                        if (c.moveToFirst()) {
                            int columnIndex = c
                                    .getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c
                                    .getInt(columnIndex)) {
                                String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                String subString = uriString.substring(7);
                                downloadSuccess(new File(subString).getAbsolutePath());
                            }else {
                                downloadFailure("Download unsuccessful");
                            }
                        } else {
                            downloadFailure("Download cancelled");
                        }
                    } catch (Exception e) {
                        downloadFailure("Download unsuccessful "+e.getMessage());
                    }

                }
            } else {
                downloadFailure("Download unsuccessful");
            }
        }
    };
    public static boolean isFileDownload() {
        return true;
    }
}
