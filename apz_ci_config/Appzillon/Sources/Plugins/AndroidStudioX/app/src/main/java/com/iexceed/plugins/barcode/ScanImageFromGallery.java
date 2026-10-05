package com.iexceed.plugins.barcode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.SparseArray;
import android.webkit.WebView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by siddaiahswamy.patil on 04-Jun-18.
 */

public class ScanImageFromGallery {

    private Activity mActivity;
    private Context context;
    private WebView webView;
    private String mJson;
    private String mCallbackId;
    private String filePath;

    public ScanImageFromGallery(Activity activity, WebView wv, String json) {
        super();
        mActivity = activity;
        webView = wv;
        mJson = json;
        context = activity.getApplicationContext();
        try {
            JSONObject mJson = new JSONObject(json);
            mCallbackId = mJson.getString("id");
            filePath = mJson.getString("filePath");
        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

    public void scanImage() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    InputStream imageStream = null;
                    try {
                        final Uri imageUri = Uri.fromFile(new File(filePath));
                        imageStream = mActivity.getContentResolver().openInputStream(imageUri);
                    } catch (FileNotFoundException e) {
                        ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-070", null, mActivity,
                                webView, true);
                        return;
                    }

                    final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    BarcodeDetector detector = new BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE).build();
                    if (!detector.isOperational()) {
                        failureCallBack("Failed to scan the image.");
                        return;
                    }
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();

                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    Barcode thisCode = barcodes.valueAt(0);
                    JSONObject decodedData = null;
                    try {
                        decodedData = new JSONObject();
                        decodedData.put("text", thisCode.rawValue);
                    } catch (Exception e) {
                    }
                    sendSuccess(decodedData);
                } catch (Exception e){
                    failureCallBack("Failed to scan the image.");
                }
            }
        });

    }

    public void failureCallBack(String errMessage) {
        JSONObject error = null;
        try {
            error = new JSONObject();
            error.put("errorMessage", errMessage);
        } catch (Exception e) {

        }
        ApzPluginUtil.sendError(mCallbackId, "", error, mActivity,
                webView, true);
    }

    private void sendSuccess(JSONObject jsonObject) {
        ApzPluginUtil.sendSuccess(mCallbackId, jsonObject, false, mActivity, webView, true);
    }
}
