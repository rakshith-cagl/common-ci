package com.iexceed.plugins.copydatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.appzillonapp.AppzillonMainScreen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;




public class CopyDatabase extends SQLiteOpenHelper {
	
	private final Context myContext;
	private static final int DATABASE_VERSION = 1;
	private static String DATABASE_NAME ;
	private static String DB_PATH;
	String [] list;
	String file;

	public CopyDatabase(Context context, String DATABASE_NAME) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.DATABASE_NAME = DATABASE_NAME;
		this.myContext = context;
		File filePath = myContext.getDatabasePath(DATABASE_NAME);
		DB_PATH = filePath.getPath();
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}
	public void copyDataBase(String DATABASE_NAME) throws IOException {
		//getReadableDatabase();
		//Abhishek For OTA
		InputStream myInput;
		//Abhishek ,13 August 2015, Bug id 6147, Reverted back changes of 10 April 2015 START
				if((AppzillonMainScreen.OTAREQUIRED).equalsIgnoreCase("Y")){
//					File sqliteFile = new File(AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC+"sqlite/"+DATABASE_NAME);
					File sqliteFile = AppzillonUtils.getApzFile(AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC+"sqlite/"+DATABASE_NAME,null);
					myInput = new FileInputStream(sqliteFile);
				}else
		//Abhishek ,13 August 2015, Bug id 6147, Reverted back changes of 10 April 2015 END
		{
//			myInput = myContext.getAssets().open(AppzillonMainScreen.ASSET_APP_LOC+"sqlite/"+DATABASE_NAME);
			myInput = myContext.getAssets().open(AppzillonUtils.validatePath(AppzillonMainScreen.ASSET_APP_LOC+"sqlite/"+DATABASE_NAME,null));
		}
		
		String outFileName = DB_PATH;
		//Abhishek 14 April 2015 To make only one DB , in-place of APPSDB and APPSDB.sqlite two separate DB's START
		//Since there are two DB one was created in DatabaseHelper class and another was copied from asset folder
		if(outFileName.contains(".sqlite")){
			
			outFileName = outFileName.substring(0, outFileName.lastIndexOf("."));
		}
		//Abhishek 14 April 2015 To make only one DB , in-place of APPSDB and APPSDB.sqlite two separate DB's END
		OutputStream myOutput = new FileOutputStream(outFileName, false);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
		
	}


}
