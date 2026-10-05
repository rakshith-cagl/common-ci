package com.iexceed.plugins.augmentedreality;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.currentlocation.CurrentLocation;

public class ARActivity extends Activity {
	
	private static final String TAG = "ARActivity";
	
	static boolean isViewAdded = false;
	static int childCount;	
	
	static FrameLayout arViewPane;
	
	static LinearLayout layoutView;
	
	static ArCameraView arDisplay;
	
	LinearLayout preveiewLayout;
	
	Bitmap receivedBitmap;
	
	public static Activity activity;
	
	OverlayView arContent;
	
	static JSONArray places;
	
	static Location presentLoc;
	
	static ImageView overLayImage;
	
	static String theme;
	
	boolean isUnRegisteredFromARActivity = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ar_view);

		arViewPane = (FrameLayout) findViewById(R.id.ar_view_pane);

		arDisplay = new ArCameraView(getApplicationContext(),this);
		arViewPane.addView(arDisplay);
		
		Intent in = getIntent();
		theme = in.getStringExtra("theme");
		
		activity = this;
		
		if(places != null){
			arContent = new OverlayView(getApplicationContext(),activity,places); 
			arViewPane.addView(arContent);
		}else{
			showAlert("No Place found to display.");
		}
	
		
	}

	

	public static void showAlert(String message) {
		
		AlertDialog dialog = new AlertDialog.Builder(activity).setTitle("Sorry !!!").setMessage(message).create();
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				
			}
		});
		dialog.show();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		isUnRegisteredFromARActivity = true;
		if(arContent != null)
		arContent.unregistoreSensors();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Register it from Activity if it is unregistered from activity 
		if(isUnRegisteredFromARActivity && arContent != null)
		arContent.registoreSensors();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		CurrentLocation.stopLocationListener();
	}
	

	public static void showLayout(JSONObject jsonObj, int i, int angleDiff)	throws JSONException {

		layoutView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.info_window_view, null);
		layoutView.setTag(jsonObj);
		layoutView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final JSONObject json =(JSONObject) v.getTag();
				ApzPluginUtil.sendSuccess(ApzARPlugin.callerId, json, false,ApzARPlugin.mActivity, ApzARPlugin.mWebview, true);
				activity.finish();
				
			}
		});
		layoutView.setId(999 + i);
		TextView title = (TextView) layoutView.findViewById(R.id.title);
		title.setText(jsonObj.getString("title"));
		TextView distance = (TextView) layoutView.findViewById(R.id.distance);
		//distance.setText(jsonObj.getString("distance") + "mts");
		distance.setText(OverlayView.dist+"m");
		TextView category = (TextView) layoutView.findViewById(R.id.category);
		category.setText(OverlayView.category);
		TextView offer = (TextView) layoutView.findViewById(R.id.offer);
		offer.setText(OverlayView.offer);
		TextView addInfo = (TextView) layoutView.findViewById(R.id.add_info);
		addInfo.setText(jsonObj.getString("additionalInfo"));
		ImageView imgView = (ImageView) layoutView.findViewById(R.id.img);

		try {
			InputStream ims;
			// For OTA refresh

			if ((AppzillonMainScreen.OTAREQUIRED).equalsIgnoreCase("Y")) {
//				File filesJson = new File(AppzillonMainScreen.SANDBOX_LOC + "/"	+ AppzillonMainScreen.ASSET_APP_LOC	+ "styles/"+ theme + "/img/" + jsonObj.getString("image"));
				File filesJson = AppzillonUtils.getApzFile(AppzillonMainScreen.SANDBOX_LOC + "/"	+ AppzillonMainScreen.ASSET_APP_LOC	+ "styles/"+ theme + "/img/" + jsonObj.getString("image"),null);
				ims = new FileInputStream(filesJson);
			} else {
				ims = activity.getAssets().open(AppzillonUtils.validatePath(AppzillonMainScreen.ASSET_APP_LOC+ "styles/"+ theme + "/img/"+ jsonObj.getString("image"),null));
			}
			// load image as Drawable
			Drawable d = Drawable.createFromStream(ims, null);
			// set image to ImageView
			imgView.setImageDrawable(d);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}

		android.widget.FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(440, 150);
		layoutParams.topMargin = 100 + i * 65;
		if (angleDiff > 0)
			layoutParams.leftMargin = 150 + angleDiff * 8;
		else
			layoutParams.rightMargin = 150 - (angleDiff * 8);
		layoutView.setLayoutParams(layoutParams);

		View view = arViewPane.findViewById(999 + i);
		if (view != null)
			arViewPane.removeView(view);
		arViewPane.addView(layoutView);
		isViewAdded = true;

	}

	public static void hideLayout(int i) {
			View view = arViewPane.findViewById(999+i);
			if(view != null)
			arViewPane.removeView(view);
			isViewAdded = false;
	}
	


	public static void setJsonArray(JSONArray mPlaces) {
		places = mPlaces;

	}

	public static void setCurrentLocation(Location mpresentLoc) {
		presentLoc = mpresentLoc;

	}


}

