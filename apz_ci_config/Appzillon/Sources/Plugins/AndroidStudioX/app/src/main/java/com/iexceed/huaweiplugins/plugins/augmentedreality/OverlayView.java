package com.iexceed.plugins.augmentedreality;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;
import android.view.View;

public class OverlayView extends View implements SensorEventListener{
    
    private float[] mGravity = new float[3];
	
	private float[] mGeomagnetic = new float[3];
	
	private static String TAG = "OverlayView";
	
	private int mAzimuth = 0;
	
    Context mContext;
    
    boolean isShow = false;
    
    Sensor accelSensor;
    Sensor compassSensor;
    SensorManager sensors;
    
    static JSONArray places;
    
    Activity activity;
    
    int previousAngle;
    
    public static int dist;
    
    public static String category;
    
    public static String offer;
    
    public static String additionalInfo;
    
    public static String title;
    
    public OverlayView(Context context, Activity activity, JSONArray places) { 
        super(context);   
        mContext = context;
        this.activity = activity;
        this.places = places;
        registoreSensors();
    } 

	@Override
	public void onSensorChanged(SensorEvent event) {

		final float alpha = 0.97f;

		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				mGravity[0] = alpha * mGravity[0] + (1 - alpha)	* event.values[0];
				mGravity[1] = alpha * mGravity[1] + (1 - alpha)	* event.values[1];
				mGravity[2] = alpha * mGravity[2] + (1 - alpha)	* event.values[2];

			}

			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

				mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)	* event.values[0];
				mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)	* event.values[1];
				mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)	* event.values[2];

			}

			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I,	mGravity, mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				mAzimuth = (int) Math.toDegrees(orientation[0]); // orientation
				mAzimuth = (mAzimuth + 360) % 360;
				
				Location presentLoc =  ARActivity.presentLoc;
				boolean isAngleChanged = true;
				//Change in 2  degrees is only considered as angle change, to reduce iterations and flickring on screen
				if(mAzimuth > previousAngle-10 && mAzimuth < previousAngle+10){
					isAngleChanged = false;
				}else{
					if(previousAngle != mAzimuth){
						isAngleChanged = true;
						previousAngle = mAzimuth;
					}
					
				}
				if(presentLoc != null && isAngleChanged){
					//Log.i(TAG, "mAzimuth : "+mAzimuth);
					
					try {
						double destLat;
						double destLong;
						for(int i = 0; i < places.length();i++){
							JSONObject jsonObj = places.getJSONObject(i);
							/*String cord = (String) jsonObj.getString("coordinates");
							String[] parts = cord.split("\\~");
							String lat = parts[0];
							destLat = Double.valueOf(lat);
							String longi = parts[1];
							destLong = Double.valueOf(longi);*/
							String lat = jsonObj.getString("latitude");
							String longi = jsonObj.getString("longitude");
							destLong = Double.valueOf(longi);
							destLat = Double.valueOf(lat);
							Location lcn = new Location("temp");
							lcn.setLatitude(destLat);
							lcn.setLongitude(destLong);
							category = jsonObj.getString("category");
							offer = jsonObj.getString("description");
							additionalInfo = jsonObj.getString("additionalInfo");
							title = jsonObj.getString("title");
							dist = (int) lcn.distanceTo(presentLoc);
							
							int angle = (int) angleFromCoordinate(presentLoc.getLatitude(),presentLoc.getLongitude(),destLat,destLong);
							//If present angle lies with in the range of 30 degrees then only show 
							if(mAzimuth > angle-15 && mAzimuth < angle+15){
								int angleDiff = angle -mAzimuth;
//								Log.i(TAG, "Show : "+jsonObj.getString("title")+" angle :"+angle);
								isShow = true;
								ARActivity.showLayout(jsonObj,i,angleDiff);
						}else{
//							Log.e(TAG, "Hide : "+jsonObj.getString("title"));
							isShow = false;
							ARActivity.hideLayout(i);
						}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						
					}
				}else{
//					Log.i(TAG, "same angle");
				}
				
			}else{
				
			}
		}
	
	}

	public void unregistoreSensors() {

		sensors.unregisterListener(this);
		if (accelSensor != null)
			accelSensor = null;
		if (compassSensor != null)
			compassSensor = null;

	}
	
	public void registoreSensors() {
		sensors = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE); 
        accelSensor = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); 
        compassSensor = sensors.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//		boolean isAccelAvailable = sensors.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_FASTEST); 
//      boolean isCompassAvailable = sensors.registerListener(this, compassSensor, SensorManager.SENSOR_DELAY_FASTEST);
        boolean isAccelAvailable = sensors.registerListener(this, accelSensor, 0); 
        boolean isCompassAvailable = sensors.registerListener(this, compassSensor, 0); 
        
        if(isAccelAvailable && isCompassAvailable){
        	//Do Nothing
        }else{
        	String serviceNotSupported;
        	if(isAccelAvailable)
        		serviceNotSupported = "Compass";
        	else
        		serviceNotSupported = "Accelerometer";
        	unregistoreSensors();
        	ARActivity.showAlert(serviceNotSupported+" feature is not supported by this Device.");
        }
		
	}

	
	
	private double angleFromCoordinate(double lat1, double lon1, double lat2,double lon2) {

	   /* double dLon = (lon2 - lon1);

	    double y = Math.sin(dLon) * Math.cos(lat2);
	    double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
	            * Math.cos(lat2) * Math.cos(dLon);

	    double brng = Math.atan2(y, x);

	    brng = Math.toDegrees(brng);
	    brng = (brng + 360) % 360;
	    brng = Math.toDegrees(brng);

	    return brng;
        */
		//Natasha changes Bug Id 6352
		double lonDelta = (lon2 - lon1);
		double y = Math.sin(lonDelta)  * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2)* Math.cos(lonDelta);
		double angle = Math.atan2(y, x);
		double angleDeg = Math.toDegrees(angle);
		angle = Math.toRadians((angleDeg + 360)% 360); //normalize to 0 to 360 (instead of -180 to 180), then convert back to radians
		angleDeg = Math.toDegrees(angle);
		return angleDeg;
				
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	} 
	
	public static void refreshPlace(JSONArray Placesjson) {
		try {
			places = null;
			places = Placesjson;
		} catch (Exception ex) {
			
		}
	}
}
