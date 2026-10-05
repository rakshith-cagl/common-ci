package com.iexceed.plugins.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.app.Activity;
import android.webkit.WebView;

public class ZipPlugin {

	private String TAG = "ZipPlugin";
	private static JSONObject obj;
	private static Activity mActivity;
	private static WebView mWebView;
	private static String TAGZIP = "ZIP";
	private static String TAGUNZIP = "UNZIP";
	private static String status;
	String files[];
	String filename;
	String folderStructure;
	BufferedInputStream origin = null;
	ZipOutputStream out = null;
	String appSandboxLoc = AppzillonMainScreen.SANDBOX_LOC + File.separator
			+ AppzillonMainScreen.ASSET_APP_LOC;
	String mCallerId;

	public ZipPlugin(String callerId, Activity activity, WebView webView) {
		mActivity = activity;
		mWebView = webView;
		mCallerId = callerId;
	}

	public void zip(String jsonObject) throws IOException {
		String zipFile = null;
		String file = null;
		try {
			obj = new JSONObject(jsonObject);
			file = obj.getString("srcFilePath");  //  extra / is removed
			
			
			
			
			if (obj.getString("destFilePath").equals("")
					|| obj.getString("destFilePath").equals(null)) {
				zipFile = file.substring(0, file.lastIndexOf("/"));
			} else {
				zipFile  = obj.getString("destFilePath");  // extra / is removed
			}
			if(zipFile.startsWith("/"))
				zipFile = zipFile.substring(0, 1);
		} catch (JSONException e) {
			ApzLogger.i(TAG,e.toString());
		}
		if (!file.contains(AppzillonMainScreen.SANDBOX_LOC)) {
			if(file.startsWith("/")){
				file = file.substring(1, file.length());
			}

			File fileCheck = new File(file);
			if(!fileCheck.exists()){
				file = appSandboxLoc + file;
			}
		}else{
			if(file.startsWith("/")){
				file = file.substring(1, file.length());
			}
			file = file;
		}
		if (!zipFile.contains(AppzillonMainScreen.SANDBOX_LOC)) {
			zipFile = appSandboxLoc + zipFile;
		}
		File mZipFile = new File(zipFile);
		if(!mZipFile.exists()){
			mZipFile.mkdirs();
		}
		// zipFile = AppzillonMainScreen.SANDBOX_LOC
		// +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"app"+zipFile;
		// file = AppzillonMainScreen.SANDBOX_LOC
		// +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"app"+file;
		File check = new File(file);
		if (check.exists()) {
			if (check.isDirectory()) {
				zipFile = zipFile
						+ file.substring(file.lastIndexOf("/"), file.length())
						+ ".zip";
			} else {
				zipFile = zipFile
						+ file.substring(file.lastIndexOf("/"),
								file.lastIndexOf(".")) + ".zip";
			}
			
			zipFileAtPath(file, zipFile);
			zipSuccess(zipFile);
		} else {
			zipFailure("File not found");
		}
	}

	public boolean zipFileAtPath(String sourcePath, String toLocation) {
		final int BUFFER = 2048;
		File sourceFile = new File(sourcePath);
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(toLocation);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			if (sourceFile.isDirectory()) {
				zipSubFolder(out, sourceFile, sourceFile.getParent().length());
			} else {
				byte data[] = new byte[BUFFER];
				FileInputStream fi = new FileInputStream(sourcePath);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
			}
			out.close();
		} catch (Exception e) {
			ApzLogger.i(TAG,e.toString());
			zipFailure(e.getMessage());
			return false;
		}
		return true;
	}

	private void zipSubFolder(ZipOutputStream out, File folder,
			int basePathLength) throws IOException {

		final int BUFFER = 2048;

		File[] fileList = folder.listFiles();
		BufferedInputStream origin = null;
		for (File file : fileList) {
			if (file.isDirectory()) {
				zipSubFolder(out, file, basePathLength);
			} else {
				byte data[] = new byte[BUFFER];
				String unmodifiedFilePath = file.getPath();
				String relativePath = unmodifiedFilePath
						.substring(basePathLength);
//				ApzLogger.i("ZIP SUBFOLDER", "Relative Path : " + relativePath);
				FileInputStream fi = new FileInputStream(unmodifiedFilePath);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(relativePath);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
		}
	}

	public String getLastPathComponent(String filePath) {
		String[] segments = filePath.split("/");
		String lastPathComponent = segments[segments.length - 1];
		return lastPathComponent;
	}

	public void unzip(String jsonObject) throws IOException {
		try {
			obj = new JSONObject(jsonObject);
			String zipFile = null;
			zipFile = obj.getString("srcFilePath");  // extra path is removed
			String location = null;
			
			if (obj.getString("destFilePath").equals("")
					|| obj.getString("destFilePath").equals(null)) {
				location = zipFile.substring(0, zipFile.lastIndexOf("/"));
			} else {
				location = obj.getString("destFilePath");
			}
			if(location.startsWith("/"))
				location = location.substring(0, 1);
			if (!zipFile.contains(AppzillonMainScreen.SANDBOX_LOC)) {
				if(zipFile.startsWith("/")){
					zipFile = zipFile.substring(1, zipFile.length());
				}
				zipFile = appSandboxLoc + zipFile;
			}else{
				if(zipFile.startsWith("/")){
					zipFile = zipFile.substring(1, zipFile.length());
				}
				zipFile = zipFile;
			}
			if (!location.contains(AppzillonMainScreen.SANDBOX_LOC)) {
				location = appSandboxLoc + location;
			}
			// zipFile = AppzillonMainScreen.SANDBOX_LOC
			// +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"app"+zipFile;
			// location = AppzillonMainScreen.SANDBOX_LOC
			// +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"app"+location;
			File check = new File(zipFile);
			if (check.exists()) {
				unzipFile(zipFile, location);
				unzipSuccess(location);
			} else {
				unzipFailure("File not found");
			}
		} catch (JSONException e) {
			ApzLogger.i(TAG,e.toString());
			status = e.getMessage();
			unzipFailure(status);
		}
	}

	public void unzipFile(String source, String destination) {
		File zipFile = new File(source);
		String directory = null;
		if (destination.equals("") || destination == null) {
			directory = zipFile.getParent();
			directory = directory + "/";
		} else {
			directory = destination + "/";
		}
		Thread workthread = new Thread(new UnZip(zipFile, directory));
		workthread.start();
	}

	public class UnZip implements Runnable {

		File archive;
		String outputDir;

		public UnZip(File ziparchive, String directory) {
			archive = ziparchive;
			outputDir = directory;
		}

		@SuppressWarnings("unchecked")
		public void run() {
			try {
				ZipFile zipfile = new ZipFile(archive);
				for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
					ZipEntry entry = (ZipEntry) e.nextElement();
					unzipEntry(zipfile, entry, outputDir);
				}
			} catch (Exception e) {
				ApzLogger.i(TAG,e.toString());
			}
		}

		@SuppressWarnings("unchecked")
		public void unzipArchive(File archive, String outputDir) {
			try {
				ZipFile zipfile = new ZipFile(archive);
				for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
					ZipEntry entry = (ZipEntry) e.nextElement();
					unzipEntry(zipfile, entry, outputDir);
				}
			} catch (Exception e) {
				ApzLogger.i(TAG,e.toString());
			}
		}

		private void unzipEntry(ZipFile zipfile, ZipEntry entry,
				String outputDir) throws IOException {

			if (entry.isDirectory()) {
//				createDir(new File(outputDir, entry.getName()));
				createDir(AppzillonUtils.getApzFile(outputDir+"/"+entry.getName(),null));
				return;
			}

//			File outputFile = new File(outputDir, entry.getName());
			File outputFile = AppzillonUtils.getApzFile(outputDir+"/"+entry.getName(),null);
			if (!outputFile.getParentFile().exists()) {
				createDir(outputFile.getParentFile());
			}
			BufferedInputStream inputStream = new BufferedInputStream(
					zipfile.getInputStream(entry));
			BufferedOutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(outputFile));
			try {
				copyStream(inputStream, outputStream);
			} finally {
				outputStream.close();
				inputStream.close();
			}
		}

		private void createDir(File dir) {
			if (!dir.mkdirs())
				throw new RuntimeException("Can not create dir " + dir);
		}

	}

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024]; // Adjust if you want
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}


	public void zipSuccess(final String path) {
//		ApzLogger.i(TAGZIP, "Success : " + path);

		try {
			JSONObject json = new JSONObject();
			json.put("filePath", path);
			ApzPluginUtil.sendSuccess(mCallerId, json, false, mActivity,
					mWebView, true);
		} catch (JSONException e) {
			ApzLogger.i(TAG,e.toString());
		}

	}

	public void unzipFailure(final String status) {
		try {
			JSONObject json = new JSONObject();
			json.put("text", status);
			ApzPluginUtil.sendError(mCallerId, "APZ-CNT-315", json, mActivity, mWebView,
					true);
		} catch (JSONException e) {
			ApzLogger.i(TAG,e.toString());
		}
	}

	public void unzipSuccess(final String path) {
		try {
			JSONObject json = new JSONObject();
			json.put("filePath", path);
			ApzPluginUtil.sendSuccess(mCallerId, json, false, mActivity,
					mWebView, true);
		} catch (JSONException e) {
			ApzLogger.i(TAG,e.toString());
		}

	}

	public void zipFailure(final String status) {
		try {
			JSONObject json = new JSONObject();
			json.put("text", status);
			ApzPluginUtil.sendError(mCallerId, "APZ-CNT-314", json, mActivity, mWebView,
					true);
		} catch (JSONException e) {
			ApzLogger.i(TAG,e.toString());
		}
	}
}

