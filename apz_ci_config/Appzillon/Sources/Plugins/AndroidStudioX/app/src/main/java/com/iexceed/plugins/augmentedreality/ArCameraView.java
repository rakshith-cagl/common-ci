package com.iexceed.plugins.augmentedreality;

import java.io.IOException;
import java.util.List;

import com.iexceed.plugins.errorlog.ApzLogger;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ArCameraView extends SurfaceView implements SurfaceHolder.Callback {
	
	public static final String DEBUG_TAG = "ArDisplayView Log"; 
    static Camera mCamera; 
    SurfaceHolder mHolder; 
    Activity mActivity;
    View view;
    static int setRotation;
    
    
    public ArCameraView(Context context,Activity activity) {  
        super(context); 
  
        mActivity = activity; 
        mHolder = getHolder(); 
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
        mHolder.addCallback(this); 
  
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open(); 
        
		   CameraInfo info = new CameraInfo();      
		   Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);           
		   int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();   
		   int degrees = 0;     
		   switch (rotation) {       
		      case Surface.ROTATION_0: degrees = 0; break;          
		      case Surface.ROTATION_90: degrees = 90; break;        
		      case Surface.ROTATION_180: degrees = 180; break;         
		      case Surface.ROTATION_270: degrees = 270; break;     
		   }  
		   setRotation = (info.orientation - degrees + 360) % 360;
		   mCamera.setDisplayOrientation(setRotation); 
		          
		   try { 
		       mCamera.setPreviewDisplay(mHolder); 
		   } catch (Exception e) { 
		      ApzLogger.e(DEBUG_TAG, "surfaceCreated exception: "+ e.toString());     
		   } 
		
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		Camera.Parameters params = mCamera.getParameters();      
		   List<Size> prevSizes = params.getSupportedPreviewSizes(); 
		   for (Size s : prevSizes) 
		   { 
		      if((s.height <= height) && (s.width <= width)) 
		      { 
		         params.setPreviewSize(s.width, s.height); 
		         break; 
		      }  
		   } 
		             
		   mCamera.setParameters(params); 
		   mCamera.startPreview();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview(); 
		mCamera.release();  
	}
	
	public static Camera getCameraInstance() {
		Camera c = null;
		int frontCameraId = 0;
		try {
			for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
				CameraInfo camInfo = new CameraInfo();
				Camera.getCameraInfo(camNo, camInfo);
				if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_BACK)) {
					frontCameraId = camNo;
				} else if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {
					frontCameraId = camNo;
				}
			}
			c = Camera.open();
			if (c == null) {
				c = Camera.open(frontCameraId);
			}
		} catch (Exception e) {
			
		}
		return c;
	}

}
