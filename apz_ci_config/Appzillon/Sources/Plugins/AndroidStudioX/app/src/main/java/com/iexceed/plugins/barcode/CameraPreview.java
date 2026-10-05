/*
 * Barebones implementation of displaying camera preview.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.iexceed.plugins.barcode;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.appzillonapp.AppzillonMainScreen;

import java.io.IOException;
/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private PreviewCallback previewCallback;
    private AutoFocusCallback autoFocusCallback;
    private Context mContext;
   
    public CameraPreview(Context context, Camera camera,
                         PreviewCallback previewCb,
                         AutoFocusCallback autoFocusCb) {
        super(context);
        mContext = context;
        mCamera = camera;
        previewCallback = previewCb;
        autoFocusCallback = autoFocusCb;
       
        /* 
         * Set camera to continuous focus if supported, otherwise use
         * software auto-focus. Only works for API level >=9.
         */
        /*
        Camera.Parameters parameters = camera.getParameters();
        for (String f : parameters.getSupportedFocusModes()) {
            if (f == Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
                mCamera.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                autoFocusCallback = null;
                break;
            }
        }
        */

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {

            mCamera.setPreviewDisplay(holder);
          //  mCamera.startPreview();
        } catch (IOException e) {
            //Log.d("DBG", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Camera preview released in activity
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        /*
         * If your preview can change or rotate, take care of those events here.
         * Make sure to stop the preview before resizing or reformatting it.
         */
        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }
        
        boolean isSamsungTablet = AppzillonUtils.getDeviceMake().equalsIgnoreCase("SAMSUNG") && AppzillonUtils.isTablet(AppzillonMainScreen.activity);
		if(isSamsungTablet){
			mCamera.startPreview();
			mCamera.setDisplayOrientation(90);
            mCamera.autoFocus(autoFocusCallback);
            mCamera.setPreviewCallback(previewCallback);
		}
        try {
        	
            // Hard code camera surface rotation 90 degs to match Activity view in portrait
            //mCamera.setDisplayOrientation(90);
    
        	setCameraDisplayOrientation((Activity)mContext,1,mCamera);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(previewCallback);
          
//            flashbutton=(Button) findViewById(R.id.);
           
        	
            /*if(hasFlash){
           
            if(JavaScriptInterface.Flash.equalsIgnoreCase("Y")){
            params.setFlashMode(Parameters.FLASH_MODE_ON);
            }
            else if(JavaScriptInterface.Flash.equalsIgnoreCase("N")){
            	 params.setFlashMode(Parameters.FLASH_MODE_OFF);
            }*/
           
            
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e){
            //Log.d("DBG", "Error starting camera preview: " + e.getMessage());
        }
    }
   
    public static void setCameraDisplayOrientation(Activity activity,
            int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}

