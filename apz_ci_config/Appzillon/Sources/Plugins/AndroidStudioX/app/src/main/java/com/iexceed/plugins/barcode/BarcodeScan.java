package com.iexceed.plugins.barcode;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPluginUtil;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.hardware.Camera.getCameraInfo;
import static android.hardware.Camera.getNumberOfCameras;
import static android.hardware.Camera.open;


/**
 * Created by rema.krishnan on 16/5/18.
 */

public class BarcodeScan {


    private String mCallbackId;
    public static Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private ImageScanner scanner;
    boolean barcodeScanned = false;
    public static boolean previewing = true;
    public Activity activity;
    public Context context;
    public WebView webView;
    private int width = 0;
    private int boxWidth = 0;
    private int boxHeight = 0;
    int screenWidth = 0;
    boolean setflash = false;
    boolean hasFlash = false;
    public static String mJson;

    static {
        System.loadLibrary("iconv");
    }

    public BarcodeScan(Context c, Activity a, WebView wv, String json) {
        super();
        activity = a;
        context = c;
        webView = wv;
        mJson = json;
        try {
            JSONObject mJson = new JSONObject(json);
            mCallbackId = mJson.getString("id");
        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

    public int getScreenWidth() {
        DisplayMetrics display = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(display);
        screenWidth = display.widthPixels;
        return screenWidth;
    }

    public void createLayout() {


        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                hasFlash = activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                RelativeLayout linear = (RelativeLayout) activity.findViewById(R.id.mainWebViewLayout);
                int count = linear.getChildCount();
                if(mCamera==null) {
                    previewing=true;
                    //  activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    width = getScreenWidth();
                    boxWidth = width;
                    boxHeight = boxWidth;
                    autoFocusHandler = new Handler();
                    mCamera = getCameraInstance();

		/* Instance barcode scanner */
                    scanner = new ImageScanner();
                    scanner.setConfig(0, Config.X_DENSITY, 2);
                    scanner.setConfig(0, Config.Y_DENSITY, 2);
                    mPreview = new CameraPreview(activity, mCamera, previewCb, autoFocusCB);

                    FrameLayout frameLayout = new FrameLayout(context);
                    RelativeLayout.LayoutParams pParams = new RelativeLayout.LayoutParams(boxWidth, boxHeight);
                    pParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    frameLayout.setLayoutParams(pParams);
                    frameLayout.addView(mPreview);
                    linear.addView(frameLayout);

                    RelativeLayout parent = new RelativeLayout(context);
                                  /* parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                           LinearLayout.LayoutParams.MATCH_PARENT));*/
                    FrameLayout.LayoutParams pParams1 = new FrameLayout.LayoutParams(boxWidth, boxHeight);
                    pParams1.gravity = Gravity.CENTER;
                    parent.setLayoutParams(pParams1);

                    parent.setBackgroundResource(R.drawable.barcode_parent);

                    /*Flash Image*/
                    RelativeLayout imgParent = new RelativeLayout(context);
                                  /* parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                           LinearLayout.LayoutParams.MATCH_PARENT));*/
                    final float scale = activity.getResources().getDisplayMetrics().density;
                    int h = (int) (50 * scale + 0.5f);
                    RelativeLayout.LayoutParams imgpParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,h);
                    imgParent.setLayoutParams(imgpParams);
                    final ImageView flashImg=new ImageView(context);
                    flashImg.setImageResource(R.drawable.ic_flash_on_white_24dp);
                    flashImg.setClickable(true);
                    if (!hasFlash)
                        flashImg.setVisibility(View.INVISIBLE);
                    flashImg.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            final Camera.Parameters params = mCamera.getParameters();
                            List<String> flashModes = params.getSupportedFlashModes();
                            String flashMode = params.getFlashMode();
                            // Toast.makeText(getApplicationContext(),
                            // MessageFormat.format("{0}", params.getFlashMode()),
                            // Toast.LENGTH_SHORT).show();
                            if (hasFlash) {
                                if (setflash) {
                                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                    flashImg.setImageResource(R.drawable.ic_flash_on_white_24dp);
                                    setflash = false;
                                } else {
                                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                    flashImg.setImageResource(R.drawable.ic_flash_off_white_24dp);
                                    setflash = true;
                                }
                            }
                            mCamera.setParameters(params);
                        }
                    });
                    RelativeLayout.LayoutParams imgChildparams = new RelativeLayout.LayoutParams((boxWidth*10)/100 , (boxWidth*10)/100);
                    imgChildparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    imgChildparams.setMargins(0,15,0,0);
                    flashImg.setLayoutParams(imgChildparams);
 /*Flash Image*/
                    RelativeLayout child = new RelativeLayout(context);
                    RelativeLayout.LayoutParams childparams = new RelativeLayout.LayoutParams((boxWidth * 70) / 100, (boxWidth * 70) / 100);
                    childparams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    int cMargin = 80;
                    childparams.setMargins(cMargin, cMargin, cMargin, cMargin);
                    child.setLayoutParams(childparams);
                    child.setBackgroundResource(R.drawable.barcode_child);
/*Flash Image*/
                    imgParent.addView(flashImg);
                    parent.addView(imgParent);
/*Flash Image*/
                    parent.addView(child);
                    frameLayout.addView(parent);
                }
                else{
                    failureCallBack("Camera is already opened.");
                }
            }
        });
    }

    public void closeLayout() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    releaseCamera();
                    final RelativeLayout relative1 = (RelativeLayout) activity.findViewById(R.id.mainWebViewLayout);
                    int count = relative1.getChildCount();
                    View frame = relative1.getChildAt(1);
                    View frame1 = relative1.getChildAt(2);
                    ViewGroup view = (ViewGroup) frame.getParent();
                    view.removeView(frame);
                    view.removeView(frame1);
                    if(!ApzBarcodePlugin.restartBarcodeScan) {
                        JSONObject data = null;
                        try {
                            data = new JSONObject();
                            data.put("text", "Camera closed");
                        } catch (Exception e) {
                        }
                        ApzBarcodePlugin.barcodeScan = null;
                        ApzPluginUtil.sendSuccess(mCallbackId, data, false, activity, webView, true);
                    }
                } catch (Exception e) {
                    failureCallBack("Failed to close the camera.");
                }
            }
        });
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        int frontCameraId = 0;
        try {
            for (int camNo = 0; camNo < getNumberOfCameras(); camNo++) {
                Camera.CameraInfo camInfo = new Camera.CameraInfo();
                getCameraInfo(camNo, camInfo);
                if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_BACK)) {
                    frontCameraId = camNo;
                } else if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                    frontCameraId = camNo;
                }
            }
            c = open();
            if (c == null) {
                c = open(frontCameraId);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            int type = parameters.getPictureFormat();
            Camera.Size size = parameters.getPreviewSize();
            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);
            int result = scanner.scanImage(barcode);

            if (result != 0) {
                SymbolSet syms = scanner.getResults();
                
				for (Symbol sym : syms) {
					String qrdata = sym.getData();                 
                    
                    if (qrdata.length() != 16)
                    {
						barcodeScanned = true;
                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                        previewing = false;
                        mCamera.setPreviewCallback(null);
                        mCamera.stopPreview();
						JSONObject decodedData = null;
						try {
							decodedData = new JSONObject();
							decodedData.put("text", qrdata);
						} catch (JSONException e) { }
                        sendSuccess(decodedData);
                    }
                }
            }
        
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    public void failureCallBack(String errMessage) {
        JSONObject error = null;
        try {
            error = new JSONObject();
            error.put("errorMessage", errMessage);
        } catch (Exception e) {

        }
        ApzPluginUtil.sendError(mCallbackId, "", error, activity,
                webView, true);
    }

    private void sendSuccess(JSONObject jsonObject) {
        ApzPluginUtil.sendSuccess(mCallbackId, jsonObject, true, activity, webView, true);
    }
}
