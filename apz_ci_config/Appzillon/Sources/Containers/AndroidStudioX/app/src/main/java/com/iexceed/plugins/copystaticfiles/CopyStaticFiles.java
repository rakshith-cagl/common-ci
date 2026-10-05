package  com.iexceed.plugins.copystaticfiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;




public class CopyStaticFiles {

	private static final String TAG = "CopyStaticFiles";
	public Context mContext;


	public CopyStaticFiles(Context context) {
		mContext = context;
	}



	public void copystatic(JSONObject staticJson) {
		String filename ;
		String filepath;
		AssetManager assetManager = mContext.getAssets();
		InputStream in = null;
		OutputStream out = null;
		try {
			JSONObject staticobj = staticJson.getJSONObject("Files");
			Iterator<String> iterator = staticobj.keys();
			while (iterator.hasNext()) {
				filename = (String) iterator.next();
				filepath = staticobj.getString(filename);
				String basepath = "";
				if(filepath.equalsIgnoreCase("AppzillonRingtone")){
					basepath = mContext.getExternalFilesDir(null).getAbsolutePath()+File.separator+filepath;
				}else{
					basepath = AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC	+filepath;
				}
//				File clipartdir = new File(basepath);
				File clipartdir = AppzillonUtils.getApzFile(basepath,null);
				if (!clipartdir.exists()) {
					clipartdir.mkdirs();
				}
				String pathOut = basepath + "/" + filename;
				try {
					// Abhishek OTA
					String pathIn = null;
					//Abhishek 10 April 2015 Static files are not been copied into sandbox START
//					if (mContext.getResources().getString(R.string.is_OTA_enabled).equalsIgnoreCase("Y")) {
//						pathIn = AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC	+ "staticfiles" + "/" + filename;
//						File jsonFile = new File(pathIn);
//						in = new FileInputStream(jsonFile);
//					} else 
					//Abhishek 10 April 2015 Static files are not been copied into sandbox END
					{
						pathIn =  AppzillonMainScreen.ASSET_APP_LOC+ "staticfiles" + "/" +filename;
//						in = assetManager.open(pathIn);
						in = assetManager.open(AppzillonUtils.validatePath(pathIn,null));
					}

					out = new FileOutputStream(pathOut);
					copy(in, out);
					in.close();
					in = null;
					out.flush();
					out.close();

				} catch (IOException e) {
					ApzLogger.e(TAG,e.toString());
				}
			}
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}

	}



	private void copy(InputStream in, OutputStream out) throws IOException  {
		// TODO Auto-generated method stub
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		out.flush();
		out.close();
		in.close();
	}

}

