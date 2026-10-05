package com.iexceed.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;

public class CustomProgressDialog extends ProgressDialog{
	
	private String TAG = "CustomProgressDialog";

	public CustomProgressDialog(Context context) {
		super(context);
		setOnKeyListener(new MyOnKeyListener());
	}
	
	class MyOnKeyListener implements OnKeyListener {
		
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			 if (keyCode == KeyEvent.KEYCODE_SEARCH){
				 //Log.i(TAG, "MyOnKeyListener : ignore search pressed");
                 return true;
             } 
			return false;
		}
	} 
	
}
