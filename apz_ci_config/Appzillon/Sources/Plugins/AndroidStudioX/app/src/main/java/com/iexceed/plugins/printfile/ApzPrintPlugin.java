package com.iexceed.plugins.printfile;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

public class ApzPrintPlugin extends ApzPlugin {

	private static ApzPlugin pluginObj;

	public ApzPrintPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new ApzPrintPlugin(webView, activity);
		}
		return pluginObj;
	}

	@Override
	public void execute(JSONObject params) {
		String action = null;
		try {
			callbackId = params.getString("id");
			action = params.getString("action");
		} catch (JSONException exp) {

		}
		if (action.equalsIgnoreCase("PRINTDOC")) {
			printDoc(params);
		} else if ("PRINTSCREEN".equalsIgnoreCase(action)) {
			createWebPrintJob();
		}
	}

	public void printDoc(JSONObject params) {

		String fLocation = "";
		try {
			fLocation = params.getString("filePath");

		} catch (JSONException e) {

		}

		// String filePath =
		// AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC+"app"+File.separator+fLocation;
		String filePath;
		if (fLocation.contains(AppzillonMainScreen.SANDBOX_LOC)) {
			filePath = fLocation;
		} else {
			filePath = AppzillonMainScreen.SANDBOX_LOC + File.separator
					+ AppzillonMainScreen.ASSET_APP_LOC + fLocation;
		}
		if (new File(filePath).exists()) {
			final Uri docUri = Uri.fromFile(new File(filePath));
			String docType = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(
					new File(filePath)).toString());
			String docMimeType = null;
			if (docType.equalsIgnoreCase("pdf")) {
				docMimeType = "application/pdf";
			} else if (docType.equalsIgnoreCase("txt")) {
				docMimeType = "text/plain";
			} else if (docType.equalsIgnoreCase("doc")) {
				docMimeType = "application/msword";
			} else if (docType.equalsIgnoreCase("xls")) {
				docMimeType = "application/vnd.ms-excel";
			} else if (docType.equalsIgnoreCase("xlsx")) {
				docMimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			} else if (docType.equalsIgnoreCase("xml")) {
				docMimeType = "application/xml";
			}
			String docTitle = fLocation.replace("." + docType, "");

			if (AppzillonUtils.isNetworkAvailable(activity) == false) {

				ApzPluginUtil.sendError(callbackId, "APZ-CNT-059", null, activity,
						webView, true);
			} else {

				if (android.os.Build.VERSION.SDK_INT < 19) {
					Intent printIntent = new Intent(activity,
							PrintDialogActivity.class);
					printIntent.setDataAndType(docUri, docMimeType);
					printIntent.putExtra("title", docTitle);
					activity.startActivity(printIntent);
				} else {
					if (docMimeType.contains("pdf")) {
						PrintManager printManager = (PrintManager) activity
								.getSystemService(activity.PRINT_SERVICE);
						String jobName = "Document";
						MyPrintDocumentAdapter pda = new MyPrintDocumentAdapter(
								filePath);
						PrintJob val = printManager.print(jobName, pda, null);
						ApzPluginUtil.sendSuccess(callbackId, null, false,
								activity, webView, true);
						if (val.isCompleted()) {
							ApzPluginUtil.sendSuccess(callbackId, null, false,
									activity, webView, true);
						} else if (val.isFailed()) {
							ApzPluginUtil.sendError(callbackId, "", null,
									activity, webView, true);
						}
					} else {
						ApzPluginUtil.sendError(callbackId, "APZ-CNT-275", null, activity,
								webView, true);
					}

				}
			}
		} else {
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-070", null, activity, webView,
					true);
		}
	}

	public void createWebPrintJob() {
		if (android.os.Build.VERSION.SDK_INT > 19) {
			try {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {

						PrintManager printManager = (PrintManager) activity
								.getSystemService(activity.PRINT_SERVICE);

						// Get a print adapter instance
						PrintDocumentAdapter printAdapter = webView
								.createPrintDocumentAdapter();

						// Create a print job with name and adapter instance
						String jobName = "Document";
						printManager.print(jobName, printAdapter,
								new PrintAttributes.Builder().build());

					}
				});

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		if (android.os.Build.VERSION.SDK_INT < 19) {
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-323", null, activity, webView,
					true);// Not Supported
		}
	}

	public static boolean isPlugin() {
		return true;
	}

}

