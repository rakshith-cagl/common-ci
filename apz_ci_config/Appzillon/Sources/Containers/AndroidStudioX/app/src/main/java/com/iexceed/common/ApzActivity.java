package com.iexceed.common;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.SparseArray;

import com.iexceed.appzillonapp.R;

public abstract class ApzActivity extends Activity {
	
	public static SharedPreferences settings;
	public static CustomProgressDialog mProgreesDialog;
	
	public static SwipeRefreshLayout swipeLayout;
	
	protected SparseArray<ExternalActivityResultHandler> mapActivityResultHandler;
	protected SparseArray<OnPermissionsResultHandler> mapPermissionResultHandler;
	protected Dialog splashDialog ;
	
	public ApzActivity() {
		
		super();
		this.mapActivityResultHandler = new SparseArray<ExternalActivityResultHandler>();
		this.mapPermissionResultHandler = new SparseArray<OnPermissionsResultHandler>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//this.splashDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		this.splashDialog = new Dialog(this, R.style.SplashDialogTheme);
		splashDialog.setContentView(R.layout.splash_dialog_layout);
	}
	

	public void addExternalActivityResultHandler(int requestCode, ExternalActivityResultHandler resultHandler){
		if(this.mapPermissionResultHandler != null){
			this.mapActivityResultHandler.put(requestCode, resultHandler);
		}
		
	}
	
	protected void removeExternalActivityResultHandler(int requestCode){
		if(this.mapActivityResultHandler != null){
			this.mapActivityResultHandler.delete(requestCode);
		}
		
	}

	// handling permission handlers
	protected void addOnPermissionsResultHandler(int requestCode, OnPermissionsResultHandler resultHandler){
		if(this.mapPermissionResultHandler != null){
			this.mapPermissionResultHandler.put(requestCode, resultHandler);
		}

	}

	protected void removeOnPermissionsResultHandler(int requestCode){
		if(this.mapPermissionResultHandler != null){
			this.mapPermissionResultHandler.delete(requestCode);
		}

	}
	
	public void showSplashScreen(){
		this.splashDialog.show();
	}
	
	public void hideSplashScreen(){
		this.splashDialog.dismiss();
	}


	public void startActivityForResult(Intent intent, int requestCode, ExternalActivityResultHandler resultHandler) {
		this.addExternalActivityResultHandler(requestCode, resultHandler);
		super.startActivityForResult(intent, requestCode);
	}

	public void startOnPermissionForResult(Activity activity, String[] permissions, int requestCode, OnPermissionsResultHandler resultHandler) {
		this.addOnPermissionsResultHandler(requestCode, resultHandler);
		ActivityCompat.requestPermissions(activity, permissions, requestCode);
	}
		
	
}
