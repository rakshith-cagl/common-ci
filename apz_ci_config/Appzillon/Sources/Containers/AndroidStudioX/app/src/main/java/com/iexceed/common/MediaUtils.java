package com.iexceed.common;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;

import com.iexceed.appzillonapp.AppzillonMainScreen;

import java.io.File;

public class MediaUtils {
	private static final String TAG="MediaUtils";
	/**
	 * To check externalStorage(SD Card) availability
	 * @return
	 */
	public static boolean isSDCardPresent(){
	boolean mExternalStorageAvailable = false;
	String state = Environment.getExternalStorageState();
	 if (Environment.MEDIA_MOUNTED.equals(state)) {
	    mExternalStorageAvailable =  true;
	   } 
	  else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		mExternalStorageAvailable =  false;
	   }
	 else {
		mExternalStorageAvailable =  false;
	 }
	return mExternalStorageAvailable;
	}
	public static void createDirIfNotExist(String _path) {
		File lf = new File(_path);
		try {
			if (lf.exists()) {
				//directory already exists
			} else {
				if (lf.mkdirs()) {
//					Log.v(TAG, "createDirIfNotExist created " + _path);
				} else {
//					Log.v(TAG, "createDirIfNotExist failed to create " + _path);
				}
			}
		} catch (Exception e) {
			//create directory failed
//			Log.v(TAG, "createDirIfNotExist failed to create " + _path);
		}
	}
	public static void deleteExternalStoragePublicPicture(String dirName,Context c) {
	    // Create a path where we will place our picture in the user's
	    // public pictures directory and delete the file.  If external
	    // storage is not currently mounted this will fail.
		if(hasExternalStoragePublicPicture(dirName,c)){
			File sdDir=new File(AppzillonMainScreen.SANDBOX_LOC+"/"+dirName);
	    if(deleteDirectory(sdDir)){
	    	c.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
	    			 Uri.parse("file://" +  Environment.getExternalStorageDirectory())));
	    }
		}
	}
	public static boolean deleteDirectory(File path) {
		try {
        if( path.exists() ) {
          File[] files = path.listFiles();
          if (files == null) {
              return true;
          }
          for(int i=0; i<files.length; i++) {
             if(files[i].isDirectory()) {
               deleteDirectory(files[i]);
             }
             else {
					 new java.io.FileWriter(files[i].getAbsolutePath(), false).close();
					 files[i].delete();
                 }
          }
        }
		} catch (java.io.IOException e) {

		}
        return( path.delete() );
      }
	static boolean hasExternalStoragePublicPicture(String dirName,Context ctx) {
	    // Create a path where we will place our picture in the user's
	    // public pictures directory and check if the file exists.  If
	    // external storage is not currently mounted this will think the
	    // picture doesn't exist.
		File sdDir = new File(AppzillonMainScreen.SANDBOX_LOC+"/"+dirName);
	    System.out.println("FILE hasExternalStoragePublicPicture"+dirName+" == "+sdDir.exists());
	    return sdDir.exists();
	}
	/**
	 * To check whether network connection is available on device or not
	 * 
	 * @return boolean(true if network connection available otherwise false)
	 */
	public static boolean checkInternetConnection(Context c) {
	 ConnectivityManager mConMgr;
		mConMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (mConMgr.getActiveNetworkInfo() != null && mConMgr.getActiveNetworkInfo().isAvailable()	&& mConMgr.getActiveNetworkInfo().isConnected())
			return true;
		else
			return false;
	}
}
