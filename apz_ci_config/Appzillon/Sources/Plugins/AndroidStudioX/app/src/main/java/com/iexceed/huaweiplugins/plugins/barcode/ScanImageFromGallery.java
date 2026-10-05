package com.iexceed.plugins.barcode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseArray;
import android.webkit.WebView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
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
                    String outputData = "";
                    boolean flag = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == com.google.android.gms.common.ConnectionResult.SUCCESS;
                    if (flag) {

                        BarcodeDetector detector = new BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE).build();
                        if (!detector.isOperational()) {
                            failureCallBack("Failed to scan the image.");
                            return;
                        }
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

                        SparseArray<Barcode> barcodes = detector.detect(frame);
                        Barcode thisCode = barcodes.valueAt(0);
                        outputData = thisCode.rawValue;
                    } else {
                        // Call the decodeWithBitmap method to pass the bitmap.
                        HmsScan[] result1 = ScanUtil.decodeWithBitmap(context, bitmap, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(0).setPhotoMode(false).create());
                        // Obtain the scanning result.
                        if (result1 != null && result1.length > 0) {
                            outputData = result1[0].getOriginalValue();
                        }
                    }

                    JSONObject decodedData = null;
                    try {
                        decodedData = new JSONObject();
                        decodedData.put("text", outputData);
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
