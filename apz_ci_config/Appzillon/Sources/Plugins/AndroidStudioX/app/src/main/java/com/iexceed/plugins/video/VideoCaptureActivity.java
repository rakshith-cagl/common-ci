package com.iexceed.plugins.video;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.MediaUtils;
import com.iexceed.common.StringUtils;
import com.iexceed.common.UserSettings;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
//import android.support.v7.appcompat.BuildConfig;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;



public class VideoCaptureActivity extends Activity implements SurfaceHolder.Callback {
		private static final String TAG = "VideoCaptureActivity";
		private SurfaceView mPreview;
	    private Button prStartBtn,prCancelBtn,deleteBtn,prBack,playBtn;
	    private boolean isRecording;
	    private SurfaceHolder mHolder;
	    private List<Size> mSupportedPreviewSizes;
	    private Camera mCamera;
		private String cVideoFilePath ;
		private boolean isSDCardPresent=false;
		private MediaRecorder mMediaRecorder;
		private Context prContext;
		private Intent fromJs;
		private SharedPreferences settings;
		private String languageCode;
		final static String properties = "USER_PREFS";
		private long startTime = 0L;
		private Handler customHandler = new Handler();
		long timeInMilliseconds = 0L;
	    long timeSwapBuff = 0L;
	    long updatedTime = 0L;
	    TextView timerValue = null;
	    TextView Recording = null;
	    VideoView video_view = null;
	    MediaController mc;
	    MediaPlayer mediaPlayer = null;
	    String overwrite;
	    String newFile;
	    String VIDEO_REC ="Video Recording";
	    String deleted = "";
	    String callbackId;
	   

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        prContext = this.getApplicationContext();
	        cVideoFilePath = AppzillonMainScreen.SANDBOX_LOC +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"video/";
	        settings=getApplication().getSharedPreferences(properties, 0);
			languageCode=UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,"LANGUAGE", StringUtils.getString(StringUtils.DEFAULT_LANG),settings);
			final Locale appLocale = new Locale(languageCode);
			Locale.setDefault(appLocale);
	        final Configuration config2 = new Configuration();
	        config2.locale = appLocale;
	        getApplicationContext().getResources()
			     .updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());
			/**/
	        setContentView(R.layout.video_recorder);
	        fromJs=getIntent();
	        callbackId = fromJs.getStringExtra("id");
	        overwrite = fromJs.getStringExtra("overwrite");
	        
	        if(MediaUtils.isSDCardPresent()){
//	        	Log.d(this.getClass().getName(), "SD>>>>>>>>>>>>>>>CARD PRESENT");
	        	isSDCardPresent=true;
	        }
	        MediaUtils.createDirIfNotExist(cVideoFilePath);
	        mPreview = (SurfaceView) findViewById(R.id.surface_camera);
	        prStartBtn = (Button) findViewById(R.id.startBtn);
	        prCancelBtn = (Button) findViewById(R.id.cancelBtn);
	        prBack = (Button)findViewById(R.id.backBtn);
	        deleteBtn = (Button) findViewById(R.id.deleteBtn);
	        timerValue = (TextView)findViewById(R.id.timerValue);
	        Recording = (TextView) findViewById(R.id.Recording);
	        playBtn = (Button)findViewById(R.id.playBtn);
	        video_view = (VideoView) findViewById(R.id.video_view);
	        mHolder = mPreview.getHolder();
	        mHolder.addCallback(this);
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mMediaRecorder = new MediaRecorder();
			mediaPlayer = new MediaPlayer();
			mc = new MediaController(this);
			
			mCamera = getCameraInstance();
			prBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					back(v);
					if(deleted.equals("Y")){
						videoRecSuccess("");
					}else{
						videoRecSuccess(cVideoFilePath+newFile);
					}
				}
			});
	        prStartBtn.setOnClickListener(new View.OnClickListener() {
	        	@Override
	            public void onClick(View v) {
	        		if(!isSDCardPresent){
	        			Toast.makeText(VideoCaptureActivity.this, prContext.getString(R.string.sdcard_unavailable), Toast.LENGTH_SHORT).show();
	        		}
	        		else if (isRecording) {
	        			timeSwapBuff += timeInMilliseconds;
                        customHandler.removeCallbacks(updateTimerThread);
                        Recording.setVisibility(View.GONE);
                        timerValue.setVisibility(View.GONE);
                        playBtn.setVisibility(View.VISIBLE);
	                    // stop recording and release camera
	                    mMediaRecorder.stop();  // stop the recording
	                    releaseMediaRecorder();
	                    
	                    // release the MediaRecorder object
	                    mCamera.lock();         // take camera access back from MediaRecorder

	                    // inform the user that recording has stopped
	                    //prStartBtn.setText(prContext.getString(R.string.video_rec_start));
	                    prStartBtn.setVisibility(View.GONE);
	                    deleteBtn.setVisibility(View.VISIBLE);
	                    prBack.setVisibility(View.VISIBLE);
	                  //  uploadBtn.setVisibility(View.VISIBLE);
	                    
	                    isRecording = false;
	                    Toast.makeText(prContext, prContext.getString(R.string.video_save_success), Toast.LENGTH_SHORT).show();
	                    //finish();
	                } else {
	                    // initialize video camera
	                    if (prepareVideoRecorder()) {
	                        // Camera is available and unlocked, MediaRecorder is prepared,
	                        // now you can start recording
	           
	                    	Recording.setVisibility(View.VISIBLE);
	                    	timerValue.setVisibility(View.VISIBLE);
	                    	playBtn.setVisibility(View.GONE);
	                    	Recording.setText("Recording...");
	                    	startTime = SystemClock.uptimeMillis();
	                    	customHandler.postDelayed(updateTimerThread, 0);
	                        mMediaRecorder.start();
	                        prCancelBtn.setVisibility(View.GONE);
	                        // inform the user that recording has started
	                        prStartBtn.setText(prContext.getString(R.string.video_rec_stop));
	                        isRecording = true;
	                        deleted="N";
	                    } else {
	                        // prepare didn't work, release the camera
	                        releaseMediaRecorder();
	                        // inform user
	                    }
	                }
	            }
	       
			});
	        
	       playBtn.setOnClickListener(new View.OnClickListener() {
	    	   @Override
			public void onClick(View v) {
	    		prCancelBtn.setVisibility(View.GONE);
	    		prBack.setVisibility(View.VISIBLE);
	    		playBtn.setVisibility(View.GONE);
	    		deleteBtn.setVisibility(View.GONE);
	    		releaseCamera();
	    		mPreview.setVisibility(View.GONE);
	    		Uri uri = Uri.parse(cVideoFilePath+newFile);
				   //Uri uri = FileProvider.getUriForFile(prContext,
					//	   BuildConfig.APPLICATION_ID + ".provider",
						//   new File(cVideoFilePath+newFile));
	    		video_view.setVideoURI(uri);
	    		
	    		mc.setAnchorView(video_view);
	    		video_view.setMediaController(mc);
	    		
	    		video_view.setOnPreparedListener(new
	                    MediaPlayer.OnPreparedListener()  {
	                         @Override
	                         public void onPrepared(MediaPlayer mp) {
//	                                  Log.i(TAG, "Duration = " + 
//	                                		  video_view.getDuration());
	                         }
	             });
	    		
	    		video_view.requestFocus();
	    		video_view.start();
	    		
			}
		} );
	    }
	    
		//@Override
		@SuppressWarnings("deprecation")
		public void surfaceChanged(SurfaceHolder _holder, int _format, int _width, int _height) {
	        // If your preview can change or rotate, take care of those events here.
	        // Make sure to stop the preview before resizing or reformatting it.

	        if (_holder.getSurface() == null){
	          // preview surface does not exist
	          return;
	        }

	        // stop preview before making changes
	        try {
	            mCamera.stopPreview();
	        } catch (Exception e){
	          // ignore: tried to stop a non-existent preview
	        }
	        
	        boolean isSamsungTab = AppzillonUtils.getDeviceMake().equalsIgnoreCase("SAMSUNG") && AppzillonUtils.isTablet(this);
			if(isSamsungTab){
				mCamera.startPreview();
			}
			
			try {
				mCamera = android.hardware.Camera.open();
			}catch (RuntimeException ex){} 
	      
	        // set preview size and make any resize, rotate or
	        // reformatting changes here
	        Camera.Parameters parameters = mCamera.getParameters();
	        DisplayMetrics displaymetrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	        int height = displaymetrics.heightPixels;
	        int width = displaymetrics.widthPixels;
	        Size si = getOptimalSize(mCamera.getParameters().getSupportedPreviewSizes(), width, height);
	       //mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
	       //Camera.Size mPreviewSize=mSupportedPreviewSizes.get(0);
			parameters.setPreviewSize(si.width, si.height);
			//parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			mCamera.setParameters(parameters);
	        // start preview with new settings
	        try {
	        	if(mCamera != null) {
	        		mCamera.setPreviewDisplay(_holder);
	                mCamera.startPreview();
	             } 
	        }catch (Exception e){
	        	videoRecFailure("APZ-CNT-308");
	            //Log.d(TAG, "Error starting camera preview: " + e.getMessage());
	        }
		}

		//@Override
		public void surfaceCreated(SurfaceHolder holder) {
			 try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				videoRecFailure("APZ-CNT-308");
				 //Log.d(TAG, "Error setting camera preview: " + e.getMessage());
			}
		}

        
		//@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			
		}

		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			finish();
		}
		private boolean prepareVideoRecorder(){
		    mMediaRecorder = new MediaRecorder();

		    // Step 1: Unlock and set camera to MediaRecorder
		    mCamera.stopPreview();
		    mCamera.unlock();
		    mMediaRecorder.setCamera(mCamera);

		    // Step 2: Set sources
		    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

		    // Step 4: Set output file
//		    File outputfile = new File(cVideoFilePath+fromJs.getStringExtra("fileName")+".mp4");
			File outputfile = AppzillonUtils.getApzFile(cVideoFilePath+fromJs.getStringExtra("fileName"),"mp4");
		    if(outputfile.exists()&& overwrite.equalsIgnoreCase("Y")){
//		    	  mMediaRecorder.setOutputFile(cVideoFilePath+fromJs.getStringExtra("fileName")+".mp4");
				mMediaRecorder.setOutputFile(AppzillonUtils.validatePath(cVideoFilePath+fromJs.getStringExtra("fileName"),"mp4"));
				  newFile = fromJs.getStringExtra("fileName")+".mp4";
		    }else if(outputfile.exists() && overwrite.equalsIgnoreCase("N")){
		    	SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmyyhhmmss");
				String date = dateFormat.format(new Date());
//		    	mMediaRecorder.setOutputFile(cVideoFilePath+fromJs.getStringExtra("fileName")+date+".mp4");
				mMediaRecorder.setOutputFile(AppzillonUtils.validatePath(cVideoFilePath+fromJs.getStringExtra("fileName")+date,"mp4"));
		    	newFile = fromJs.getStringExtra("fileName")+date+".mp4";
		    }else{
//		    	mMediaRecorder.setOutputFile(cVideoFilePath+fromJs.getStringExtra("fileName")+".mp4");
				mMediaRecorder.setOutputFile(AppzillonUtils.validatePath(cVideoFilePath+fromJs.getStringExtra("fileName"),"mp4"));
		    	newFile = fromJs.getStringExtra("fileName")+".mp4";
		    	
		    }
		    // Step 5: Set the preview output
		    mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
	        
		    // Step 6: Prepare configured MediaRecorder
		    try {
		    	// Sid changes to counter video tilt while playing the Video
//		    	if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.N){
//		    		mMediaRecorder.setOrientationHint(180);
//		    	}
		        mMediaRecorder.prepare();
		    } catch (IllegalStateException e) {
		        //Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
		        videoRecFailure("APZ-CNT-308");
		        releaseMediaRecorder();
		        return false;
		    } catch (IOException e) {
		        //Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
		        videoRecFailure("APZ-CNT-308");
		        releaseMediaRecorder();
		        return false;
		    }
		    return true;
		}
		 @Override
		    protected void onPause() {
		        super.onPause();
		        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
		        releaseCamera();         // release the camera immediately on pause event
		        finish();  
		 }
		 /** A safe way to get an instance of the Camera object. */
		 public static Camera getCameraInstance(){
		     Camera c = null;
		     try {
		         c = Camera.open(); // attempt to get a Camera instance
		     }
		     catch (Exception e){
				 ApzLogger.i(TAG,"Problem with e"+e.toString());
		     }
		     return c; // returns null if camera is unavailable
		 }
		    private void releaseMediaRecorder(){
		        if (mMediaRecorder != null) {
		            mMediaRecorder.reset();   // clear recorder configuration
		            mMediaRecorder.release(); // release the recorder object
		            mMediaRecorder = null;
		            mCamera.lock();           // lock camera for later use
		        }
		    }

		    private void releaseCamera(){
		        if (mCamera != null){
		        	mCamera.stopPreview();
		            mCamera.release();        // release the camera for other applications
		            mCamera = null;
		        }
		    }
		public void cancelVideoRec(View v) {
			    releaseMediaRecorder();       // if you are using MediaRecorder, release it first
		        releaseCamera();         // release the camera immediately on pause event
		        finish(); 
		}
		
		public void back(View v) {
		    releaseMediaRecorder();       // if you are using MediaRecorder, release it first
	        releaseCamera();         // release the camera immediately on pause event
	        finish(); 
		}
		/**
		 * uploads videos to server
		 * @param v
		 */
		/*public void uploadToServer(View v) {
			releaseMediaRecorder();       // if you are using MediaRecorder, release it first
	        releaseCamera();         // release the camera immediately on pause event
	        finish();
		}*/
		public void deleteVideo(View v) {
		   	deleteSavedVideo();
			 deleteBtn.setVisibility(View.GONE);
	        // uploadBtn.setVisibility(View.GONE);
	        prStartBtn.setText(prContext.getString(R.string.video_rec_start));
			prStartBtn.setVisibility(View.VISIBLE);
			prBack.setVisibility(View.VISIBLE);
			prCancelBtn.setVisibility(View.GONE);
			playBtn.setVisibility(View.GONE);
		}

		private void deleteSavedVideo() {
			// TODO Auto-generated method stub
//			File f=new File(cVideoFilePath+newFile);
			File f = AppzillonUtils.getApzFile(cVideoFilePath+newFile,null);
			try{
				if(f.exists()){
//			 new java.io.FileWriter(cVideoFilePath+newFile, false).close();
			new java.io.FileWriter(AppzillonUtils.validatePath(cVideoFilePath+newFile,null), false).close();
			f.delete();
	 		Toast.makeText(prContext, "Deleted", Toast.LENGTH_SHORT).show();
			 deleted = "Y";
			 }
			}
			catch(Exception e){
				
				Toast.makeText(prContext, "Error in Deletion", Toast.LENGTH_SHORT).show();
				videoRecFailure("APZ-CNT-307");
			}
		}

		@Override
		protected void onDestroy() {
			super.onDestroy();
		}
		
		 private Size getOptimalSize(List<Size> sizes, int w, int h) {

		        final double ASPECT_TOLERANCE = 0.2;        
		        double targetRatio = (double) w / h;         
		        if (sizes == null)             
		            return null;          
		        Size optimalSize = null;         
		        double minDiff = Double.MAX_VALUE;          
		        int targetHeight = h;              
		        for (Size size : sizes) 
		        {                      
		            double ratio = (double) size.width / size.height;            
		            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)                
		                continue;             
		            if (Math.abs(size.height - targetHeight) < minDiff) 
		            {                 
		                optimalSize = size;                 
		                minDiff = Math.abs(size.height - targetHeight);             
		            }         
		        }              

		        if (optimalSize == null)
		        {
		            minDiff = Double.MAX_VALUE;             
		            for (Size size : sizes) {
		                if (Math.abs(size.height - targetHeight) < minDiff)
		                {
		                    optimalSize = size;
		                    minDiff = Math.abs(size.height - targetHeight); 
		                }
		            }
		        }

		        SharedPreferences previewSizePref;
		            previewSizePref = getSharedPreferences("PREVIEW_PREF",MODE_PRIVATE);
		       /* } else {
		            previewSizePref = getSharedPreferences("FRONT_PREVIEW_PREF",MODE_PRIVATE);
		        }*/

		        SharedPreferences.Editor prefEditor = previewSizePref.edit();
		        prefEditor.putInt("width", optimalSize.width);
		        prefEditor.putInt("height", optimalSize.height);
		        prefEditor.commit();
		        return optimalSize;     
		    }
		 
		 
		 //Timer
		 
		 private Runnable updateTimerThread = new Runnable() {
			 public void run() {
				 timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
				 updatedTime = timeSwapBuff + timeInMilliseconds;
				 int secs = (int) (updatedTime / 1000);
				 int mins = secs / 60;
				 secs = secs % 60;
				 timerValue.setText("" + mins + ":" + String.format("%02d", secs));
				 customHandler.postDelayed(this, 0);
			 		}
			     };
			     
			     private void setText(final CharSequence text) {
			    	    runOnUiThread(new Runnable() {
			    	        @Override
			    	        public void run() {
			    	            Recording.setText(text);
			    	        }
			    	    });
			    	}
			     
			     public void videoRecSuccess(String path) {
			    	 JSONObject jsonRes = new JSONObject();
			    	 try {
						jsonRes.put("filePath", path);
					} catch (JSONException e) {
						
					}
			    	 ApzPluginUtil.sendSuccess(callbackId, jsonRes, false, ApzVideoPlugin.mActivity, ApzVideoPlugin.mWebview, true);
			 	}
			 	
			 	public void videoRecFailure(String error) {
			 		ApzPluginUtil.sendError(callbackId, error, null, ApzVideoPlugin.mActivity, ApzVideoPlugin.mWebview, true);
			 		
			 	}
}
