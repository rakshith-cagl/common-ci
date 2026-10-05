package com.iexceed.plugins.fileoperation;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.JavaScriptInterface;
import com.iexceed.common.MediaUtils;
import com.iexceed.common.StringUtils;
import com.iexceed.common.UserSettings;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.BuildConfig;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.errorlog.ApzLogger;
import android.widget.TextView;
import com.iexceed.plugins.ApzPluginUtil;

public class DirectoryBrowser extends Activity {
	
	private ArrayList<File> mFileItems = null;
	
	private List<String> mFilePathItems = null;
	
	private Activity mActivity;
	
	private String prevDir;
	
	private Intent fileIntent;
	
	private boolean canGoBack;
	
	CustomArrayAdapter mFileList;
	
	private SharedPreferences settings;
	
	private String languageCode;
	
	final static String properties = "USER_PREFS";
	
	/* For EXT */
	private boolean mFilterEnabled;
	
	private static  String FILE_DIR ;//= Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	
	private String[] mFileExtGrp ;

	private String TAG = "FILE BROWSER";
	ListView list;
 
@Override
   public void onCreate(Bundle icicle) {
      super.onCreate(icicle);
      this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//    AuditLog.makeString("FILE BROWSER","on cerate");
      /* To change language setting */
      mActivity = this;
      FILE_DIR ="";
      if("Y".equalsIgnoreCase(AppzillonMainScreen.activity.getResources().getString(R.string.INTERNALSANDBOX))){
         FILE_DIR = AppzillonMainScreen.SANDBOX_LOC + "/";
      }else{
         try {
            FILE_DIR = mActivity.getExternalFilesDir(null).getAbsolutePath();
            
             FILE_DIR= FILE_DIR.substring(0, FILE_DIR.lastIndexOf("/Android"));
         }catch(Exception e){
         }
      }

		settings = getApplication().getSharedPreferences(properties, 0);
		languageCode = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,"DEFAULTLANGUAGE", StringUtils.getString(StringUtils.DEFAULT_LANG), settings);
		final Locale appLocale = new Locale(languageCode);
		Locale.setDefault(appLocale);
		final Configuration config2 = new Configuration();
		config2.locale = appLocale;
		getApplicationContext().getResources().updateConfiguration(config2,getBaseContext().getResources().getDisplayMetrics());
		/**/
		setContentView(R.layout.file_browser);
		TextView emptyText = findViewById(R.id.empty);
		list = (ListView) findViewById(R.id.list);
		list.setEmptyView(emptyText);
		// check for filter type
		mActivity = this;
		fileIntent=getIntent();
		if(fileIntent.getStringExtra("location").equals("EXTERNAL")){
            if (MediaUtils.isSDCardPresent()) {
                  File sandbox = new File(FILE_DIR);
                  if(!sandbox.exists()){
                         sandbox.mkdirs();
                  }
                  getFiles((sandbox).listFiles());
                  
            } 
		}else if (!(fileIntent.getStringExtra("location").equals(""))) {
			// check for SD Card availability
			if (MediaUtils.isSDCardPresent()) {
				//Abhishek 23Feb 2015 if location is not null then it will same for both the cases of filter START
				//Abhishek 08 April 2015 updated the path for new folder structure followed START
				FILE_DIR = AppzillonMainScreen.SANDBOX_LOC+ "/"+AppzillonMainScreen.ASSET_APP_LOC+fileIntent.getStringExtra("location")+"/";
				//Abhishek 08 April 2015 updated the path for new folder structure followed END
				//Abhishek 23Feb 2015 if location is not null then it will same for both the cases of filter END
				/*if filter empty, then filter all contents*/
				if(fileIntent.getStringExtra("filter").equals("")){
//					getFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+fileIntent.getStringExtra("location")+"/").listFiles());

					//Abhishek 23Feb 2015 update of get files START
					getFiles(new File(FILE_DIR).listFiles());
					//Abhishek 23Feb 2015 update of get files END
				}
				else{
					//Abhishek 23Feb 2015 Commented out as it is same START
					
//					FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+fileIntent.getStringExtra("location")+"/";
					
					//Abhishek 23Feb 2015 Commented out as it is same END
					mFileExtGrp = fileIntent.getStringExtra("filter").split(",");
					mFileItems = new ArrayList<File>();
					mFilePathItems = new ArrayList<String>();
					mFileList = new CustomArrayAdapter(this, R.layout.filebrowser_list_row, mFileItems);
					list.setAdapter(mFileList);
					filterByExtension(mFileExtGrp);
				}
			} else {
				new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.sdcard_unavailable))
				.setNeutralButton(getResources().getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int button) {
										finish();
									}
								}).show();

			}
		}
		// if it contains any file extension
		else if (!(fileIntent.getStringExtra("filter").equals(""))) {
			if (MediaUtils.isSDCardPresent()) {
				mFileExtGrp=fileIntent.getStringExtra("filter").split(",");
				mFilterEnabled = true;
				mFileItems = new ArrayList<File>();
				mFilePathItems = new ArrayList<String>();
				mFileList = new CustomArrayAdapter(this, R.layout.filebrowser_list_row, mFileItems);
				list.setAdapter(mFileList);
				filterByExtension(mFileExtGrp);
			} else {
				new AlertDialog.Builder(this)
						.setTitle(getResources().getString(R.string.sdcard_unavailable))
						.setNeutralButton(getResources().getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int button) {
										// do nothing
										finish();
									}
								}).show();

			}
		}
		else if("DEFAULT".equals(fileIntent.getStringExtra("root"))){
			if (MediaUtils.isSDCardPresent()) {
				/*if filecategory is default , then open app sandbox */
				//Abhishek 08 April 2015 updated the path for new folder structure followed START
//				File sandbox = new File(AppzillonMainScreen.SANDBOX_LOC+"/"+AppzillonMainScreen.ASSET_APP_LOC);
				File sandbox = AppzillonUtils.getApzFile(AppzillonMainScreen.SANDBOX_LOC+"/"+AppzillonMainScreen.ASSET_APP_LOC,null);
				//Abhishek 08 April 2015 updated the path for new folder structure followed END
				if(!sandbox.exists()){
					sandbox.mkdirs();
				}
				getFiles((sandbox).listFiles());
				
			} else {
				new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.sdcard_unavailable))
				.setNeutralButton(getResources().getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int button) {
										finish();
									}
								}).show();

			}
		} else{
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.invalid_search))
			.setNeutralButton(getResources().getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,	int button) {
									finish();
								}
							}).show();
		}
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		AuditLog.makeString("FILE BROWSER","List item click");
				int selectedRow = (int) id;
				ApzLogger.i(TAG, "File Browser:" + selectedRow);
				canGoBack = true;
				// File file = new File(filePathItems.get(selectedRow));
//				File file = new File(mFilePathItems.get(selectedRow));
				File file = AppzillonUtils.getApzFile(mFilePathItems.get(selectedRow),null);
				if (file.isDirectory()) {
					prevDir = file.toString();
//			ApzLogger.i(TAG,"File:Prev FILE:" + file.toString());
					getFiles(file.listFiles());
				} else {
//			ApzLogger.i(TAG,"File Opener:" + file.toString());
					try {
						if ((fileIntent.getStringExtra("openFile").equals("Y"))) {
							try {
								if (file.exists()) {

									Intent i = new Intent(Intent.ACTION_VIEW,
											FileProvider.getUriForFile(DirectoryBrowser.this, BuildConfig.APPLICATION_ID, file));

									i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

									startActivity(i);

								}
							} catch (ActivityNotFoundException act) {
								ApzLogger.e(TAG, act.toString());
								Intent in = new Intent();
								in.putExtra("error", "ANF");
								setResult(RESULT_CANCELED, in);
								finish();
							}
						} else {
							Intent in = new Intent();
							in.putExtra("filePath", file.getPath());
							setResult(RESULT_OK, in);
							finish();
						}
					} catch (ActivityNotFoundException anf) {
						ApzLogger.e(TAG, anf.toString());
						Intent in = new Intent();
						in.putExtra("error", "ANF");
						setResult(RESULT_CANCELED, in);
						finish();
					} catch (Exception e) {
						ApzLogger.e(TAG, e.toString());
					}

				}
			}
		});
}

	
	private void getFiles(File[] files) {
//		AuditLog.makeString("FILE BROWSER","get files");
		if (files != null) {
			mFileItems = new ArrayList<File>();
			mFilePathItems = new ArrayList<String>();
			for (File file : files) {

				if (!file.getName().equalsIgnoreCase("screens") && !file.getName().equalsIgnoreCase("scripts") && !file.getName().equalsIgnoreCase("sqlite")) {
					mFileItems.add(file);
					mFilePathItems.add(file.getPath());
				}
			}
			mFileList = new CustomArrayAdapter(this, R.layout.filebrowser_list_row, mFileItems);
			list.setAdapter(mFileList);
		}
	}

	@Override
	public void onBackPressed() {
//		AuditLog.makeString("FILE BROWSER","back pressed");
		if (mFilterEnabled) {
			finish();
		} else if (canGoBack && prevDir !=null) {
			String prevPath = (prevDir.substring(0, prevDir.lastIndexOf("/")));
			
			if (!prevPath.endsWith("/apps")){
				prevDir = (prevPath.substring(0, prevPath.lastIndexOf("/")) + "/");
//				ApzLogger.i(TAG, "File:prevPath"
//						+ prevPath
//						+ " File:prevDir:"
//						+ prevDir
//						+ Environment.getExternalStorageDirectory()
//								.getAbsolutePath());
				if (prevPath.equals(FILE_DIR)) {
					ApzLogger.i(TAG, "If Sd Card Directory");
//					getFiles(new File(""+prevPath + "/").listFiles());
					getFiles(AppzillonUtils.getApzFile(prevPath + "/",null).listFiles());
					canGoBack = false;
				} else {
//					getFiles(new File(""+prevPath + "/").listFiles());
					getFiles(AppzillonUtils.getApzFile(prevPath + "/",null).listFiles());
				}
			}else{
				finish();
			}
		} else {
			// if user in the root directory and presses the back key, then close the activity
			finish();
		}
	}

	/* Below codes for extension search */
	private class FilesAsyncTask extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (mActivity != null) {
				mFileList.notifyDataSetChanged();
			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
//			AuditLog.makeString("FILE BROWSER","File Async");
			for (int i = 0; i < params.length; i++) {
//				ApzLogger.i(TAG,"File Ext:"+params[i]);
				listFileWithExt(FILE_DIR, params[i].trim());
			}
			return null;
		}

		/**
		 * Used to list files with provided file extension type
		 *@param fileDir
		 *@param fileExt
		 */
		private void listFileWithExt(String fileDir, String fileExt) {
//			AuditLog.makeString("FILE BROWSER","listFileWithExt");
//			ApzLogger.i(TAG,"folder:" + fileDir + "  ext:" + fileExt);
			GenericExtFilter filter = new GenericExtFilter(fileExt);


			//processDirectory(new File(fileDir) ,fileExt);
			processDirectory(AppzillonUtils.getApzFile(fileDir,null),fileExt);
		}

		private void processDirectory(File dir,String fileExt) {
//			AuditLog.makeString("FILE BROWSER","process directory");

			if (dir.isFile()) {
				processFile(dir,fileExt);
			} else if (dir.isDirectory()) {
				File[] listOfFiles = dir.listFiles();
				if (listOfFiles != null) {
					for (int i = 0; i < listOfFiles.length; i++)
						processDirectory(listOfFiles[i],fileExt);
				} else {
					ApzLogger.d(TAG,"FILE LIST : [ACCESS DENIED]");
				}
			}

		}

		private void processFile(File file,String fileExt) {
//			AuditLog.makeString("FILE BROWSER","process file");
			try {
				if (!file.getName().endsWith(fileExt))
					return;
				// Add file path to another array of files
				mFileItems.add(file);
				mFilePathItems.add(file.getPath());
			} catch (Exception e) {
				ApzLogger.e(TAG,e.toString());
			}

		}

	}

	// inner class, generic extension filter
	public class GenericExtFilter implements FilenameFilter {

		private String ext;

		public GenericExtFilter(String ext) {
			this.ext = ext;
		}

		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		JavaScriptInterface.activityDestroyed(TAG);
	}

	public void filterByExtension(String[] ext) {

		if (mFileItems.size() > 0) {
			mFileItems.clear();
			mFilePathItems.clear();
		}
		new FilesAsyncTask().execute(ext);

	}

}
