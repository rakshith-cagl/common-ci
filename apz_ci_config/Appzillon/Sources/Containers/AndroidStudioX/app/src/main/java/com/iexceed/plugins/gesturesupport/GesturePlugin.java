package com.iexceed.plugins.gesturesupport;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class GesturePlugin extends ApzPlugin{

	private Context context;
	
	private Activity mActivity;
	
	private WebView mWebView;
	
	private RectF _lastTapArea;
	
    private int _lastTapCount = 0;

    private static final long TAP_MAX_DELAY = 500L;
    
    private final static int RADIUS = 30;
    
    private TapCounter _tapCounter;
    
    private ScaleGestureDetector SGD;
    
    private float scale = 1f;
    
    private boolean IS_ACTION_POINTER_DOWN = false;
    
    private String TAG = "GESTURE";
    
    private static ApzPlugin pluginObj;
    
    private GesturePlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
		mActivity = activity;
		mWebView = webView;
	}
    
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new GesturePlugin(webView, activity);
		}
		return pluginObj;
	}
	
	private void startListener(JSONObject obj) {
		try {
			callbackId = obj.getString("id");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}
		//initialize gesture detector and scale detector	
		final GestureDetector gestureDetector = new GestureDetector(mActivity.getApplicationContext(), new GestureListener());
		SGD = new ScaleGestureDetector(mActivity.getApplicationContext(),new ScaleListener());
		
		// The ?active pointer? is the one currently moving our object.	
		 _tapCounter = new TapCounter(TAP_MAX_DELAY, TAP_MAX_DELAY);
		 
        webView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
            		IS_ACTION_POINTER_DOWN =true;
            	}  else{
            		IS_ACTION_POINTER_DOWN = false;
            	}

            	gestureDetector.onTouchEvent(event);
            	SGD.onTouchEvent(event);            	
                return false;                 

            }
        }); 

		
	}
	
	private void stopListener(JSONObject obj){
		webView.setOnTouchListener(null);
	}
	
	private void getGestutreCallBack(final String pactivity){
		JSONObject result = null;
		try{
			result = new JSONObject();
			result.put("event", pactivity);
		}catch(JSONException ex){
			ApzLogger.e(TAG,ex.toString());
		}
		ApzPluginUtil.sendSuccess(this.callbackId, result, true, this.activity, this.webView, true);
	}
	
	private void setTapCpont(int _lastTapCount) {
		String tapValue = "";
		switch(_lastTapCount){
		case 1:
			tapValue = "singleTap";
			break;
		case 2 :
			tapValue = "doubleTap";
			break;
		case 3 :
			tapValue = "tripleTap";
			break;
		}
		getGestutreCallBack(tapValue);
		ApzLogger.i(TAG, "Tap Count : "+tapValue);
		
	}
	
	private class TapCounter extends CountDownTimer {

        TapCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (_lastTapArea != null) {
                if (_lastTapCount == 2)
                	setTapCpont(_lastTapCount);               	

                _lastTapCount = 0;
                _lastTapArea = null;
            }
        }       

		@Override
        public void onTick(long millisUntilFinished) {
        }

        void resetCounter() {
            start();
        }
    }
	
	private class GestureListener extends SimpleOnGestureListener{	
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			
			_tapCounter.resetCounter();
			float x = e.getX();
            float y = e.getY();
            _lastTapCount = 2;
			_lastTapArea = new RectF(x - RADIUS, y - RADIUS,x + RADIUS, y + RADIUS);
		return super.onDoubleTap(e);
		}		

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2,float velocityX, float velocityY) {
			float flingMin = 100;
			float velocityMin = 20;
			// If we are using two fingers then it will not take this in consideration
			if (!IS_ACTION_POINTER_DOWN) {
				
				boolean leftward = false;				
				boolean rightward = false;
				boolean upward = false;
				boolean downward = false;

				// calculate the change in X position within the fling gesture
				float horizontalDiff = event2.getX() - event1.getX();
				// calculate the change in Y position within the fling gesture
				float verticalDiff = event2.getY() - event1.getY();

				float absHDiff = Math.abs(horizontalDiff);
				float absVDiff = Math.abs(verticalDiff);
				float absVelocityX = Math.abs(velocityX);
				float absVelocityY = Math.abs(velocityY);

				if (absHDiff > absVDiff && absHDiff > flingMin	&& absVelocityX > velocityMin) {
					if (horizontalDiff > 0)
						rightward = true;
					else
						leftward = true;
					
				} else if (absVDiff > flingMin && absVelocityY > velocityMin) {
					if (verticalDiff > 0)
						downward = true;
					else
						upward = true;
				}

				if (leftward) {
					getGestutreCallBack("swipeLeft");
					ApzLogger.i(TAG, "On swipeLeft");
				} else if (rightward) {
					getGestutreCallBack("swipeRight");
					ApzLogger.i(TAG, "On swipeRight");
				} else if (upward) {
					getGestutreCallBack("swipeUp");
					ApzLogger.i(TAG, "On swipeUp");
				} else if (downward) {
					getGestutreCallBack("swipeDown");
					ApzLogger.i(TAG, "On swipeDown");
				}

			}

			return super.onFling(event1, event2, velocityX, velocityY);
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			getGestutreCallBack("longPress");
			ApzLogger.i(TAG, "On Long Click");
		super.onLongPress(e);
		}
		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {

			if (_lastTapCount == 2 && (event.getAction() == MotionEvent.ACTION_DOWN)) {

				if (_lastTapArea != null) {
					if (_lastTapArea.contains(event.getX(), event.getY())) {
						_lastTapCount++;
					}
				}

			} else {
				_lastTapCount = 1;
			}
			setTapCpont(_lastTapCount);

			return true;
		}	
		
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {	 
		float startScale;
		@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
			startScale = detector.getScaleFactor();
				return super.onScaleBegin(detector);
			}
		
	   @Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			  float endScale = detector.getScaleFactor();
			  
			  if (startScale > endScale) {
		            ApzLogger.i(TAG, "Zoom out Dection");
		            getGestutreCallBack("zoomOut");
		            
		        } else if (startScale < endScale) {
		        	ApzLogger.i(TAG, "Zoom in Dection");
		        	getGestutreCallBack("zoomIn");
		        }
			super.onScaleEnd(detector);
		}
	}

	public static boolean isGesturePlugin() {
		return true;
	}

	@Override
	public void execute(JSONObject params) {
		String action = "";
		try {
			action = params.getString("action");
		} catch (JSONException e) {
			ApzLogger.i(TAG, e.toString());
		}
		if(action.equals("START")){
			startListener(params);		
			
		}else if(action.equals("STOP")){
			stopListener(params);
		}
		
	}

}
