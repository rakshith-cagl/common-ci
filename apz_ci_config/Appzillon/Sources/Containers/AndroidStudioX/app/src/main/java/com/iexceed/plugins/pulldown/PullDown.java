package com.iexceed.plugins.pulldown;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class PullDown {
	public static Boolean isDummy = false;

	public static void enablePullDown(final String callerId,
			final Activity activity, final WebView webView, String jsonobj) {
		JSONObject json = null;
		String callId = null;
		String screenId = null;

		try {
			json = new JSONObject(jsonobj);
			callId = json.getString("callId");
			screenId = json.getString("screenId");
		} catch (JSONException e) {
			ApzLogger.e("PullDown",e.toString());
		}
		final String cId = callId;
		final String sId = screenId;

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					ApzActivity.swipeLayout.setEnabled(true);
					ApzActivity.swipeLayout.setColorScheme(
							android.R.color.holo_blue_bright,
							android.R.color.holo_green_light,
							android.R.color.holo_orange_light,
							android.R.color.holo_red_light);
					ApzActivity.swipeLayout
							.setOnRefreshListener(new OnRefreshListener() {

								@Override
								public void onRefresh() {
									JSONObject cBackObj = new JSONObject();
									try {
										cBackObj.put("callId", cId);
										cBackObj.put("screeId", sId);
										cBackObj.put("event", "pullDown"); // Tiwari
																			// making
																			// xcel
									} catch (JSONException e) {
										ApzLogger.e("PullDown",e.toString());
									}
									ApzPluginUtil.sendSuccess(callerId,
											cBackObj, true, activity, webView,
											true);

								}
							});

					JSONObject cBackObj = new JSONObject();
					try {
						cBackObj.put("event", "started");
					} catch (JSONException e) {
					}
					ApzPluginUtil.sendSuccess(callerId, cBackObj, true,
							activity, webView, true);

				} catch (Exception e) {
					ApzPluginUtil.sendError(callerId, "", new JSONObject(),
							activity, webView, true);
				}

			}
		});
	}

	public static void disablePullDown(final String callerId,
			final Activity activity, final WebView webView, String jsonobj) {
		JSONObject json = null;
		try {
			json = new JSONObject(jsonobj);
		} catch (JSONException e) {
			ApzLogger.e("PullDown",e.toString());
		}

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					ApzActivity.swipeLayout.setEnabled(false);
					ApzActivity.swipeLayout.setRefreshing(false);
					ApzActivity.swipeLayout.setOnRefreshListener(null);
					
					JSONObject cBackObj = new JSONObject();
					try {
						cBackObj.put("event", "stopped");
					} catch (JSONException e) {
					}

					ApzPluginUtil.sendSuccess(callerId, cBackObj,
							false, activity, webView, true);
				} catch (Exception e) {
					ApzPluginUtil.sendError(callerId, "", new JSONObject(),
							activity, webView, true);
				}
			}
		});
	}

	public static void hideRefreshIcon(Activity activity) {

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (ApzActivity.swipeLayout.isRefreshing())
					ApzActivity.swipeLayout.setRefreshing(false);
			}
		});		
	}
}
