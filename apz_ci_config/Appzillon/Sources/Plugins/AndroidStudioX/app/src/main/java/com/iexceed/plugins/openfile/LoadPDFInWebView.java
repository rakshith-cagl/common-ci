package com.iexceed.plugins.openfile;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoadPDFInWebView extends Activity {

	int pageToDisplay;
	TextView presentPage;
	TextView totalPageView;
	byte[] data;

	LinearLayout ln;

	String TAG = "LoadPDF";

	GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

	}

	public static boolean isPlugin() {
		return false;
	}

}
