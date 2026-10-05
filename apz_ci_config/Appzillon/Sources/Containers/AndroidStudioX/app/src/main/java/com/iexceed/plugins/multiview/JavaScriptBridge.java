package com.iexceed.plugins.multiview;
import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class JavaScriptBridge {
	
	private Context context;
	
	private String mCallerId;
	
	private Activity activity;
	
	private WebView mainWebView;
	
	RelativeLayout mainWebViewContainer;
	
	private WebSettings webSettings;
	
	private Hashtable<String, Integer> mapIdToString; 
	
	/* list holds id and webview as key value pair */
	private HashMap<Integer, WebView> list;
	
	/*parentChild holds child WebView id and parent WebView id as key value pair	*/ 
	private HashMap<Integer, Integer> childParent;
	
	/* contains id of the mainWebViewLayout */
	private static int mainWebViewId;
	
	/*to check webview added vertical or horizontal split mode*/
	private static String childWebViewMode;
	
	/*to check multiview is enabled or not*/
	private static boolean isMultiviewEnabled;
	
	//private String successCallback;
	
//	private String failureCallback ;
	
	/*stores width or height in %*/
	private int dimension;
	
	private String TAG = "MULTIVIEW";

	private int mainWebViewfullHeight = 0;
	private int mainWebViewfullWidth = 0;
	
	androidx.swiperefreshlayout.widget.SwipeRefreshLayout SwipeRefreshLayout;

	private String location = null;

	private String viewID = null;

	public JavaScriptBridge(Context c, Activity a, WebView w, RelativeLayout mainWv) {
		context = c;
		activity = a;
		mainWebView = w;
		// this.mainViewContainer = webViewContainer;
		mainWebViewContainer = mainWv;
		list = new HashMap<Integer, WebView>();
		childParent = new HashMap<Integer, Integer>();
		mainWebViewId = mainWebView.getId();
		mapIdToString=new Hashtable<String, Integer>();
		
	}

	public void enableMultiView(JSONObject jsonObj , String callerId ) {
//		AuditLog.makeString("MULTIVIEW","enableMultiView");
		int id = 0;

		int width = 0;
		int height = 0;
		String targetView = null;
		String launchPage = null;
		
		try {
			//successCallback = jsonObj.getString("successCallback");
			//failureCallback = jsonObj.getString("failureCallback");
			location = jsonObj.getString("location");
			targetView = jsonObj.getString("targetView");
			launchPage = jsonObj.getString("launchPage");
			viewID = jsonObj.getString("viewId");
			mCallerId = callerId;
			SwipeRefreshLayout = (androidx.swiperefreshlayout.widget.SwipeRefreshLayout) activity.findViewById(R.id.swipe_container);
			if(mapIdToString.isEmpty()){
			 mapIdToString.put(viewID,1);
				mainWebViewfullHeight = SwipeRefreshLayout.getHeight();
				mainWebViewfullWidth = SwipeRefreshLayout.getWidth();
			}
			else if(mapIdToString.size()==1){
				callFailure("");
				return;
			}
			id = mapIdToString.get(viewID);
			
			if(location.equals("vertical")){
				width = Integer.parseInt(jsonObj.getString("percentage"));
			}
			else if(location.equals("horizontal")){				
				height = Integer.parseInt(jsonObj.getString("percentage"));
			}
			else
				return;
     		
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			callFailure("APZ-CNT-077");
			return;
		} catch (NumberFormatException nfe) {
			ApzLogger.e(TAG,nfe.toString());
			callFailure("");
			return;
		}
		if(!targetView.equalsIgnoreCase("main")){
		   callFailure("APZ-CNT-026");
			return;
		}
		if (location.equals("horizontal")) {
			addH(id, location, width, height, targetView, launchPage);
		} else if (location.equals("vertical")) {
			addV(id, location, width, height, targetView, launchPage);
		}
	}

	void resetSize(JSONObject jsonObj, String callerId){
		int width = 0;
		int height = 0;
		mCallerId = callerId;
		try {
			if(location.equals("vertical")){
				width = Integer.parseInt(jsonObj.getString("percentage"));
				dimension = width;
			}
			else if(location.equals("horizontal")){
				height = Integer.parseInt(jsonObj.getString("percentage"));
				dimension = height;
			}
			if(mapIdToString.isEmpty()){
				callFailure("");
				return;
			}
			else if(mapIdToString.get(viewID)==null){
				callFailure("");
				return;
			}
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			callFailure("APZ-CNT-077");
			return;
		} catch (Exception nfe) {
			ApzLogger.e(TAG,nfe.toString());
			callFailure("");
			return;
		}
		ApzLogger.i(TAG,"Close Multiview:"+list+" List size:"+list.size());

		if (location.equals("horizontal")) {
			int childWebViewH = (int) ((float) height * mainWebViewfullHeight / 100);
			resizeMainH(getScreenWidth(), childWebViewH, SwipeRefreshLayout);
		} else if (location.equals("vertical")) {
			int childWebViewW = (int) ((float) width * mainWebViewfullWidth / 100);
			resizeMainV(childWebViewW,getScreenHeight() ,SwipeRefreshLayout);
		}
	}
	public void killMultiView(JSONObject jsonObj , String callerId ) {
//		AuditLog.makeString("MULTIVIEW","killMultiView");
		int deletedWebViewW = 0;
		int deletedWebViewH = 0;
		int id = 0;
		androidx.swiperefreshlayout.widget.SwipeRefreshLayout parentWebView;
		mCallerId = callerId;
		String idToRemove=null;
		try {
			idToRemove=jsonObj.getString("viewId");
			if(mapIdToString.isEmpty()){
			callFailure("");
			return;
			}
			else if(mapIdToString.get(jsonObj.getString("viewId"))==null){
				callFailure("");
				return;
			}
			id = mapIdToString.get(idToRemove);
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			callFailure("APZ-CNT-077");
			return;
		} catch (NumberFormatException nfe) {
			ApzLogger.e(TAG,nfe.toString());
			callFailure("");
			return;
		}
		ApzLogger.i(TAG,"Close Multiview:"+list+" List size:"+list.size());
		WebView webView = null;
		if(list.size()==1){
			callFailure("APZ-CNT-026");
			return;
		}
		else if (list.size() > 0) {
			webView = list.get(id);
		}
		 
		ApzLogger.i(TAG,"List :webView =" + webView);
		if (webView != null) {
			/* get the wdth and height of the deleted webview */
			deletedWebViewW = webView.getWidth();
			deletedWebViewH = webView.getHeight();
			mainWebViewContainer.removeView(webView);
			/* get the parent Id to which the deleted view was attached */
			//parentWebView = (WebView) activity.findViewById(childParent.get(id));
			//Changing to swipeLayout Natasha
			parentWebView = (androidx.swiperefreshlayout.widget.SwipeRefreshLayout) activity.findViewById(SwipeRefreshLayout.getId());
			/* remove the deleted view id and WebView from list */
			list.remove(id);
			ApzLogger.i(TAG,"List :Removed Id=" + id);
			childParent.remove(id);
			/* if list don't have any views restore the main view */
			if (list.isEmpty()) {
				RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				parentWebView.setLayoutParams(params1);
				
			} else if (list.size() == 1) {
				isMultiviewEnabled=false;
				RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				parentWebView.setLayoutParams(params1);
				
			} else {
				ApzLogger.i(TAG,"List :parentWebView=" + parentWebView);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(parentWebView.getWidth() + deletedWebViewW,parentWebView.getHeight() + deletedWebViewH);
				parentWebView.setLayoutParams(params);
			}
		}
		if(!isMultiviewEnabled){
			mapIdToString.remove(idToRemove);
			callSuccess();
		}
		else{
			callFailure("APZ-CNT-026");
		}
		ApzLogger.i(TAG,"List Remove :" + list+"List Remove Sise:" + list.size());
		
	}

	public void addWebSetting(WebView webView) {
//		AuditLog.makeString("MULTIVIEW","addWebSetting");
		webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setDomStorageEnabled(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setGeolocationEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setGeolocationDatabasePath("");
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				String message = "";
				switch (error.getPrimaryError()) {
					case SslError.SSL_UNTRUSTED:
						message = "Certificate is untrusted.";
						break;
					case SslError.SSL_EXPIRED:
						message = "Certificate has expired.";
						break;
					case SslError.SSL_IDMISMATCH:
						message = "Certificate ID is mismatched.";
						break;
					case SslError.SSL_NOTYETVALID:
						message = "Certificate is not yet valid.";
						break;
				}
//				message += " Do you want to continue anyway?";
				builder.setTitle("SSL Certificate Error");
				builder.setMessage(message);
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.proceed();

					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.cancel();
						callFailure("User has cancelled the operation");//add suitable error code
					}
				});
				AlertDialog ad = builder.create();
				ad.show();//handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {

		});
	}

	public int getScreenWidth() {
//		AuditLog.makeString("MULTIVIEW","getScreenWidth");
		DisplayMetrics display = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(display);
		int width = display.widthPixels;
		return width;
	}

	public int getScreenHeight() {
//		AuditLog.makeString("MULTIVIEW","getScreenHeight");
		DisplayMetrics display = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(display);
		int height = display.heightPixels;
		return height;
	}

	public void addV(int id, String location, int width, int height, String targetViewId, String launchPage) {
//		AuditLog.makeString("MULTIVIEW","addV");
		RelativeLayout.LayoutParams params1=null;
		int childWebViewW = 0;
		WebView targetWebView = null;
		ApzLogger.i(TAG," Multiview ListSIZE:"+list.size());
        if(list.size()==2){
        	callFailure("");
			return;
		}
        dimension=width;
		if (targetWebView == null) {
			targetWebView = mainWebView;
			ApzLogger.i(TAG,"Multiview :TargetView W:" + SwipeRefreshLayout.getWidth() + " H:"+ SwipeRefreshLayout.getHeight());
			childWebViewW = (int) ((float) width * SwipeRefreshLayout.getWidth() / 100);
			params1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			ApzLogger.i(TAG,"Multiview :childWebViewW:" + childWebViewW );
			
		} 
		if (list.isEmpty()) {
			params1.addRule(RelativeLayout.RIGHT_OF, SwipeRefreshLayout.getId());
		} else {
			if (targetViewId.equalsIgnoreCase("main")) {
				params1.addRule(RelativeLayout.RIGHT_OF, SwipeRefreshLayout.getId());
			} else {
				return;
			}
		}
		WebView webView = new WebView(activity);
		webView.setLayoutParams(params1);
		webView.setId(id);
		webView.loadUrl(launchPage);
		webView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				 switch (event.getAction()) { 
                 case MotionEvent.ACTION_DOWN: 
                 case MotionEvent.ACTION_UP: 
                     if (!v.hasFocus()) { 
                         v.requestFocus(); 
                     } 
                     break; 
             } 
             return false; 
          }
		});
		list.put(Integer.valueOf(id), webView);
		ApzLogger.i(TAG,"Multiview List add V:" + list.size() + "==" + list);
		if (targetViewId.equalsIgnoreCase("MAIN")) {
			list.put(mainWebView.getId(), mainWebView);
			childParent.put(Integer.valueOf(id), mainWebView.getId());
			ApzLogger.i(TAG,"MultiView add H:targetWebView:" + targetWebView	+ " :-" + list.size() + "==" + list);
		} else {
			childParent.put(Integer.valueOf(id), Integer.valueOf(targetViewId));
			ApzLogger.i(TAG,"Multiview List add H:" + list.size() + "==" + list);
		}
		resizeMainV(childWebViewW,getScreenHeight() ,SwipeRefreshLayout);
		mainWebViewContainer.addView(webView);
		addWebSetting(webView);
	}

	public void addH(int id, String location, int width, int height,String targetViewId, String launchPage) {
//		AuditLog.makeString("MULTIVIEW","addH");
		WebView targetWebView = null;
		RelativeLayout.LayoutParams params1=null;
		int childWebViewH = 0;
		if(list.size()==2){
			callFailure("");
			return;
		}
		dimension=height;
		if (targetWebView == null) {
			targetWebView = mainWebView;
			childWebViewH = (int) ((float) height * SwipeRefreshLayout.getHeight() / 100);
			params1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		} 
		if (list.isEmpty()) {
			params1.addRule(RelativeLayout.BELOW, SwipeRefreshLayout.getId());
		} else {
			if (targetViewId.equalsIgnoreCase("main")) {
				params1.addRule(RelativeLayout.BELOW, SwipeRefreshLayout.getId());
			} else {
				return;
			}
		}
		WebView webView = new WebView(activity);
		webView.setLayoutParams(params1);
		webView.setId(id);
		webView.loadUrl(launchPage);
		webView.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				 switch (event.getAction()) { 
                 case MotionEvent.ACTION_DOWN: 
                 case MotionEvent.ACTION_UP: 
                     if (!v.hasFocus()) { 
                         v.requestFocus(); 
                     } 
                     break; 
             } 
             return false; 
          }
		});
		list.put(Integer.valueOf(id), webView);
		if (targetViewId.equalsIgnoreCase("MAIN")) {
			list.put(mainWebView.getId(), mainWebView);
			childParent.put(Integer.valueOf(id), mainWebView.getId());
		} else {
			childParent.put(Integer.valueOf(id), Integer.valueOf(targetViewId));
		}
		resizeMainH(getScreenWidth(), childWebViewH, SwipeRefreshLayout);
		addWebSetting(webView);
		mainWebViewContainer.addView(webView);
	}

	private void resizeMainV(int childWebViewWidth, int fullHeight, androidx.swiperefreshlayout.widget.SwipeRefreshLayout parentWebView) {
//		AuditLog.makeString("MULTIVIEW","resizeMainV");
		ApzLogger.i(TAG,"List :VResize W:" +childWebViewWidth  + " H:"+ fullHeight + parentWebView + " Parent:W-H"	+ parentWebView.getWidth() + "-" + parentWebView.getHeight());
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(mainWebViewfullWidth-childWebViewWidth, LayoutParams.MATCH_PARENT);
		parentWebView.setLayoutParams(params1);
		childWebViewMode="V";
		isMultiviewEnabled=true;
		callSuccess();
	}

	private void resizeMainH(int fullWidth, int childWebViewHeight,	androidx.swiperefreshlayout.widget.SwipeRefreshLayout parentWebView) {
//		AuditLog.makeString("MULTIVIEW","resizeMainH");
		ApzLogger.i(TAG,"List :HResize W:" + fullWidth + " H:"+ childWebViewHeight + parentWebView + " Parent:W-H"+ parentWebView.getWidth() + "-" + parentWebView.getHeight());
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mainWebViewfullHeight	- childWebViewHeight);
		parentWebView.setLayoutParams(params1);
		childWebViewMode="H";
		isMultiviewEnabled=true;
		callSuccess();
	}
	private void callSuccess() {
		ApzPluginUtil.sendSuccess(mCallerId, new JSONObject(), false,
				this.activity, mainWebView, true);
	}
	private void callFailure(String errorCode) {
		ApzPluginUtil.sendError(mCallerId, errorCode,new JSONObject(),
				this.activity, mainWebView, true);
		
	}

	/*will be called on orientation change to resize the mainwebview*/
	public void resizeMain() {
//		AuditLog.makeString("MULTIVIEW","resizeMain");
		ApzLogger.i(TAG,"Multiview OnOrientation change :dimension (W/H):" + dimension );
		if(!isMultiviewEnabled){
			return;
		}
		if(childWebViewMode.equals("V") && childParent.size()==1){
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(getScreenWidth()-(int) ((float) dimension * getScreenWidth() / 100),LayoutParams.MATCH_PARENT);
		SwipeRefreshLayout.setLayoutParams(params1);
		}
		if(childWebViewMode.equals("H") && childParent.size()==1){
			RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, getScreenHeight()-(int) ((float) dimension * getScreenHeight() / 100));
			SwipeRefreshLayout.setLayoutParams(params1);
		}
	}
}

