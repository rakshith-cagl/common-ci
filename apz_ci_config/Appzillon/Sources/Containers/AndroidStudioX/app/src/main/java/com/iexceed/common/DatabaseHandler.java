package com.iexceed.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.iexceed.plugins.createnote.Notes;

import java.io.File;
import java.util.ArrayList;

/**
 * This class is used for managing database operation.
 *
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private final Context myContext;

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "APPSDB";

    private static String DB_PATH ;

    private static final String TABLE_NAME_NOTES= "Notes";

    private static final String KEY_TXN_NO = "txn_no";

    private static final String KEY_NOTE = "note";

    public static final String TABLE_NAME_PUSHNOTES= "tb_notifications";

    public static final String PUSH_ROW_ID = "id";

    public static final String PUSH_TIME = "timeStamp";

    public static final String PUSH_MSG = "message";

    public static final String PUSH_MSG_READ_STS = "readFlag";

    private final String TAG = "";

    SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        File filePath = myContext.getDatabasePath(DATABASE_NAME);
        DB_PATH = filePath.getPath();
        //Log.i(TAG, "Database Path : "+DB_PATH);
        //db = getWritableDatabase();	// to read from a sqlite3 file comment these two lines
        //db.close();					// to read from a sqlite3 file comment these two lines
    }

    @Override
    public synchronized void close() {
        if(db != null)
            db.close();
        super.close();
    }

    @Override
    public final void onCreate(SQLiteDatabase db) {

    }

    @Override
    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String dropTable = "DROP TABLE IF EXISTS";
        db.execSQL(dropTable + TABLE_NAME_NOTES);
        db.execSQL(dropTable + TABLE_NAME_PUSHNOTES);
        onCreate(db);
    }
    /*litcodes*/
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase(){

//Abhishek 17 April 2015 Commented out because it was creating tables if DB was not copied, old approach used by Renuka START
//	    	Log.i(TAG, "Importing DataBase ");
//	    	boolean dbExist = checkDataBase();

//	    	if(dbExist){
//	    		//do nothing - database already exist
//	    		Log.i(TAG, "Database Already Exist");
//	    	}else{
//	    		Log.i(TAG, "Database Does Not Exist");
//	    		//By calling this method an empty database will be created into the default system path
//	            //of your application so we are gonna be able to overwrite that database with our database.
//	        	db=this.getReadableDatabase();
//	        	try {
//	    			//copyDataBase();
//	        		Log.i(TAG, "Database copied successfully");
//	    		} catch (Error e) {
//	    			
//	    			Log.i(TAG, "Error copying database");
//	        	}
//	    		finally{
//
//	    			final String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NAME_NOTES + "("
//	    				+ KEY_TXN_NO + " TEXT PRIMARY KEY," + KEY_NOTE + " TEXT)";
//
//
//	    			final String CREATE_PUSH_NOTES_TABLE = "CREATE TABLE " + TABLE_NAME_PUSHNOTES + "("
//	    		    + PUSH_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//	    		    + PUSH_TIME + " TEXT," + PUSH_MSG + " TEXT,"+ PUSH_MSG_READ_STS + " TEXT)";
//
//
//	    			db.execSQL(CREATE_NOTES_TABLE);
//	    			db.execSQL(CREATE_PUSH_NOTES_TABLE);
//
//	    			Log.i(TAG, "TABLE CREATED");
//
//	    			// to read from a sqlite3 file comment this function
//	    			db.close();
//	    		}
//	    	}
//Abhishek 17 April 2015 Commented out because it was creating tables if DB was not copied, old approach used by Renuka END

//Abhishek 17 April 2015 bug id 4952 Creating new tables in DB START
        db = this.getReadableDatabase();

        final String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NAME_NOTES + "(" + KEY_TXN_NO + " TEXT PRIMARY KEY," + KEY_NOTE + " TEXT)";

        final String CREATE_PUSH_NOTES_TABLE = "CREATE TABLE " + TABLE_NAME_PUSHNOTES + "(" + PUSH_ROW_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + PUSH_TIME + " TEXT," + PUSH_MSG + " TEXT," + PUSH_MSG_READ_STS + " TEXT)";

        db.execSQL(CREATE_NOTES_TABLE);
        //Log.i(TAG, "TABLE "+CREATE_NOTES_TABLE+" CREATED.");
        db.execSQL(CREATE_PUSH_NOTES_TABLE);
//        Log.i(TAG, "TABLE "+CREATE_PUSH_NOTES_TABLE+" CREATED.");
        // to read from a sqlite3 file comment this function
        db.close();

//Abhishek 17 April 2015 bug id 4952 Creating new tables in DB END
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
    */
    private boolean checkDataBase(){

        File dbFile = new File(DB_PATH);
        return dbFile.exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
//	    private void copyDataBase() throws IOException{
//
//	    	System.out.println("Copying Database......");
//	    	//Open your local db as the input stream
//	    	InputStream myInput = myContext.getAssets().open(AppzillonMainScreen.ASSET_APP_LOC+"sqlite/"+DATABASE_NAME+".sqlite");
//
//	    	// Path to the just created empty db
//	    	String outFileName = DB_PATH ;//+ DATABASE_NAME_IMPORT;
//
//	    	//Open the empty db as the output stream
//	    	OutputStream myOutput =new FileOutputStream(outFileName,false);
//
//	    	//transfer bytes from the inputfile to the outputfile
//	    	byte[] buffer = new byte[1024];
//	    	int length;
//	    	while ((length = myInput.read(buffer))>0){
//	    		myOutput.write(buffer, 0, length);
//	    	}
//
//	    	//Close the streams
//	    	myOutput.flush();
//	    	myOutput.close();
//	    	myInput.close();
//
//	    }
    /*litcodes*/


    /**
     * This method is used to get notes if exists for a given reference number.
     * @param refNO as String
     * @return  note as String
     */
    public final String getNote(String refNO){

        String l_note="";
//        Log.i(TAG, "getNote TXN : "+refNO);
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cur = db.rawQuery("Select "+KEY_NOTE+" FROM "+TABLE_NAME_NOTES+" WHERE "+KEY_TXN_NO+" = ?",new String []{refNO});
        if(cur.moveToFirst()){
            if(!cur.isAfterLast()){
                l_note=cur.getString(0);

            }
        }

        cur.close();
        db.close();
//        Log.i(TAG, "getNote NOTE : "+l_note);
        return l_note;
    }

    /**
     * This method used to add notes.
     * @param Notes class object
    */
    public final void addNotes(Notes note){
        try{
            db = this.getWritableDatabase();
            final ContentValues cv = new ContentValues();
            cv.put(KEY_TXN_NO,note.getTime());
            cv.put(KEY_NOTE, note.getNote());
//            Log.i(TAG, "addNotes getTime : "+note.getTime());
//            Log.i(TAG, "addNotes getNote : "+note.getNote());
            db.insert(TABLE_NAME_NOTES, null, cv);
            //Log.i(TAG, "addNotes Note added successfully ");

        }catch(SQLiteException e){
            //Log.e(TAG, "Error is "+e.getMessage());
        }
        finally{
            db.close();
        }
    }

    /**
     * This method retrieves all notes and return.
     * @return Cursor
    */
    public final Cursor getAllNotes() {
        db = this.getReadableDatabase();

        final Cursor cur = db.rawQuery("Select "+KEY_TXN_NO+" ,"+KEY_NOTE+" FROM "+TABLE_NAME_NOTES , new String [] {});
        cur.close();
        db.close();
        return cur;

    }

    public final void updateNote(Notes note,String refNO){
        try{
            final String whereClause = KEY_TXN_NO+"=?";
            db = this.getWritableDatabase();
            final ContentValues cv = new ContentValues();
            cv.put(KEY_NOTE, note.getNote());

            final int y=db.update(TABLE_NAME_NOTES, cv, whereClause, new String[] {String.valueOf(refNO)});
            //Log.i(TAG, "updateNote Note updated successfully ");

        }catch(SQLiteException e){
           
            //Log.e(TAG, "updateNote Note Error is "+e.getMessage());
        }finally{
            db.close();
        }
    }

    public final boolean deleteNoteRow(String refNO){

        int x=0;
        try{
            final String whereClause = KEY_TXN_NO+"=?";
            db = this.getWritableDatabase();
            x= db.delete(TABLE_NAME_NOTES, whereClause, new String[]{String.valueOf(refNO)});
            //Log.i(TAG, "deleteNoteRow Note deleted successfully "+x);

        }catch(SQLiteException e){
          
            //Log.e(TAG, "deleteNoteRow Note Error is "+e.getMessage());
        }finally{
            db.close();
        }
        return x>0;

    }

    /**
     * This method is used to add push message to local DB
     * @param p_notes
     */
    public final void addPushMsg(Notes p_notes){
        try{
            db = this.getWritableDatabase();
            final ContentValues cv = new ContentValues();
            cv.put(PUSH_TIME,p_notes.getTime());
            cv.put(PUSH_MSG, p_notes.getNote());
            cv.put(PUSH_MSG_READ_STS, p_notes.getReadSts());
//            Log.i(TAG, "addPushMsg Time "+p_notes.getTime());
//            Log.i(TAG, "addPushMsg Note "+p_notes.getNote());
            db.insert(TABLE_NAME_PUSHNOTES, null, cv);
            //Log.i(TAG, "PUSH Msg added successfully");

        }catch(SQLiteException e){
            //Log.e(TAG, "addPushMsg Note Error is "+e.getMessage());
        }
        finally{
            db.close();
        }
    }
    /**
     * This method will return all push messages stored in local DB
     * @return ArrayList<Notification>
     */
    public final ArrayList<Notification> getAllPushMsg() {
        final ArrayList<Notification> result = new ArrayList<Notification>();
        try{
            db = this.getWritableDatabase();
            final Cursor cur = db.rawQuery("Select "+PUSH_ROW_ID+","+PUSH_TIME+","+PUSH_MSG+ ","+ PUSH_MSG_READ_STS +" FROM "+TABLE_NAME_PUSHNOTES , null);
            if(cur != null){
                if( cur.moveToFirst()){
                    do{
                        final String id = cur.getString(cur.getColumnIndex(PUSH_ROW_ID));
                        final String time = cur.getString(cur.getColumnIndex(PUSH_TIME));
                        final String message = cur.getString(cur.getColumnIndex(PUSH_MSG));
                        final String readSts = cur.getString(cur.getColumnIndex(PUSH_MSG_READ_STS));
                        result.add(new Notification(id, time, message, readSts));
                    }while(cur.moveToNext());
                    cur.close();
                }
            }
        }catch(SQLiteException s){
           
        }finally{
            db.close();
        }
        return result;

    }

    /**
     * This method is used to delete push message.Passing null will delete all rows
     * @param id
     * @return boolean
     */
    public final boolean deletePushMsg(String id) throws SQLiteException{
        boolean flag=false;
            final String whereClause = PUSH_ROW_ID+"=?";
            db = this.getWritableDatabase();
            flag = db.delete(TABLE_NAME_PUSHNOTES, whereClause, new String[]{id})>0;

        return flag;

    }

    public final int deleteAllPushMsg(){
        int flag = 0;
        try{
            db = this.getWritableDatabase();
            flag = db.delete(TABLE_NAME_PUSHNOTES, "1", null);
        }catch(SQLiteException e){
            
        }finally{
            db.close();
        }
        return flag;
    }

}
   
