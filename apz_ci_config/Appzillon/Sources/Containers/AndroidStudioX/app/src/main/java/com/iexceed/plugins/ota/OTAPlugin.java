package com.iexceed.plugins.ota;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.appzillonapp.R;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.MediaUtils;
import com.iexceed.common.ServerUtilities;
import com.iexceed.common.StringUtils;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import static com.iexceed.common.CameraUtils.deleteDir;


public class OTAPlugin {

    private static Context mContext;

    private static String TAG = "";

    private boolean STATUS = false;

    public int downloadCount = 0;

    public File otaTempFolder = null;
    //  public File otaTempFiles[] = null;

    String OTA_ACTION_DELETE = "delete";

    public String UPDATED_APP_VERSION;
    public String tempAppVer;
    public static JSONArray downloadArray;
    public static JSONArray deleteArray;


    public OTAPlugin(Context context) {
        mContext = context;

    }

    public static boolean IsOTAPlugin() {
        return true;
    }

    public boolean getDataForOTA(String appId, String appVersion) {

        UPDATED_APP_VERSION = appVersion;

        //Abhishek , bug id 5333, getting present APP ID START
        String presentApp = null;
        try {
            presentApp = StringUtils.getString(StringUtils.APP_ID);
        } catch (Exception e) {
            //presentApp = mContext.getResources().getString(R.string.app_id);
            ApzLogger.e(TAG, "" + e.getMessage());
        }
        //Abhishek , bug id 5333, getting present APP ID END

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject header = new JSONObject();
            header.put(AppzillonMainScreen.REQ_STATUS, true); // sid, 3.2 server changes
            header.put(AppzillonMainScreen.PRE_LOGIN, "true");

            //Abhishek , bug id 5333, sending app id as that of present APP START
//			header.put(AppzillonMainScreen.APP_ID, mContext.getResources().getString(R.string.app_id));
            header.put(AppzillonMainScreen.APP_ID, presentApp);
            //Abhishek , bug id 5333, sending app id as that of present APP END

            header.put(AppzillonMainScreen.SESSION_ID, "");
            header.put(AppzillonMainScreen.INTERFACE_ID, "appzillonGetAppFile");
            header.put(AppzillonMainScreen.SCREEN_ID, "login");
            header.put(AppzillonMainScreen.DEVICE_ID, AppzillonMainScreen.ANDROID_OS);
            header.put(AppzillonMainScreen.REQUEST_KEY, "");
            header.put(AppzillonMainScreen.USER_ID, AppzillonMainScreen.USER_ID_FOR_OTA);
            JSONObject reqBody = new JSONObject();
            JSONObject appFileReq = new JSONObject();
            appFileReq.put(AppzillonMainScreen.APP_VERSION, appVersion);
            appFileReq.put(AppzillonMainScreen.APP_ID, appId);
            appFileReq.put(AppzillonMainScreen.OS, AppzillonMainScreen.ANDROID_OS);
            reqBody.put("appzillonAppFilesRequest", appFileReq);
            jsonObject.put(AppzillonMainScreen.APPZILLON_HEADER, header);
            jsonObject.put(AppzillonMainScreen.APPZILLON_BODY, reqBody);
        } catch (JSONException e) {
            ApzLogger.e(TAG, e.toString());
        }


//		HttpResponse response = null;
//		String lResponse = null;
        String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
        JSONObject response = ServerUtilities.sendRequestToServer(serverUrl, jsonObject.toString());
        if (response != null) {
//			ApzLogger.i(TAG, "response : "+response);
            try {
                JSONObject body = response.getJSONObject(AppzillonMainScreen.APPZILLON_BODY);
                JSONObject header = response.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
                //String status = header.getString("status"); Sid , 3.2 server changes
                boolean status = header.getBoolean("status");
                if (status) {  //status.equalsIgnoreCase("success"
                    JSONArray array = body.getJSONArray(body.getString("appId"));
                    deleteArray = new JSONArray();
                    downloadArray = new JSONArray();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject fileData = array.getJSONObject(i);
                        String action = fileData.getString("action");
                        if (action.equalsIgnoreCase(OTA_ACTION_DELETE)) {
                            deleteArray.put(array.getJSONObject(i));
                        } else {
                            downloadArray.put(array.getJSONObject(i));
                        }
                    }
                    actionOnOTAFiles(body, appVersion);
                } else {
                    JSONArray error = response.getJSONArray(AppzillonMainScreen.APPZILLON_ERRORS);
                    JSONObject errObj = error.getJSONObject(0);
                    Toast.makeText(mContext, errObj.toString(), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                ApzLogger.e(TAG, e.toString());
            }


        } else {
            ApzLogger.e(TAG, "Server Connection Error.");
        }

        return STATUS;
    }

    private void actionOnOTAFiles(JSONObject body, String oldAppVersion) {
        try {
            JSONArray array = body.getJSONArray(body.getString("appId"));
            if (downloadArray.length() != 0) {
                for (int i = 0; i < downloadArray.length(); i++) {
                    JSONObject fileData = downloadArray.getJSONObject(i);
                    String filePath = fileData.getString("filepath");
                    // TODO Abhishek Get action to be taken is it download or delete

                    //Log.e(TAG, "i : " + i + " :: filePath : " + filePath);
                    String destinationPath = "";
                    if (filePath.contains("/")) {
                        int lastOccurence = filePath.lastIndexOf("/");
                        String appFolderLoc = filePath.substring(0, lastOccurence);
                        destinationPath = AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSETS_MAIN_FOLDER + File.separator + appFolderLoc;
                    } else {
                        // TODO Abhishek
                        destinationPath = AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSETS_MAIN_FOLDER + File.separator + filePath;
                    }
                    String action = fileData.getString("action");
                    FileOutputStream output = null;
                    FileInputStream input = null;

                    downloadFilesFromServer(body.getString("appId"), fileData.getString(AppzillonMainScreen.APP_VERSION), fileData.getString("filename"), destinationPath, filePath, fileData.getString(AppzillonMainScreen.OS));
                    // }
                    //	UPDATED_APP_VERSION = fileData.getString(AppzillonMainScreen.APP_VERSION);
                    tempAppVer = fileData.getString(AppzillonMainScreen.APP_VERSION);
                }

                if (downloadCount != 0 && (downloadCount == downloadArray.length())) {
                    if (deleteArray.length() != 0)
                        deleteOtaFiles(deleteArray);
                    boolean isRefreshed = refreshFiles(array);
                    deleteOtaTempFolder(getDir("appzillonOTATemp"));
                    if (isRefreshed) {
                        UPDATED_APP_VERSION = tempAppVer;
                        AppzillonMainScreen.UPDATE_REQUEST = "N";
                        STATUS = true;
                    } else {
                        UPDATED_APP_VERSION = oldAppVersion;
                        STATUS = false;
                    }
                } else {
                    UPDATED_APP_VERSION = oldAppVersion;
                    deleteOtaTempFolder(getDir("appzillonOTATemp"));
                    STATUS = false;
                }
            } else {
                if (deleteArray.length() != 0) {
                    deleteOtaFiles(deleteArray);
                    UPDATED_APP_VERSION = tempAppVer;
                    AppzillonMainScreen.UPDATE_REQUEST = "N";
                    STATUS = true;
                }
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
            UPDATED_APP_VERSION = oldAppVersion;
        } catch (JSONException e) {
            e.printStackTrace();
            UPDATED_APP_VERSION = oldAppVersion;
        }

    }

    private void deleteOtaFiles(JSONArray deleteArray) {
        try {
            for (int i = 0; i < deleteArray.length(); i++) {
                JSONObject fileData = deleteArray.getJSONObject(i);
                String filePath = fileData.getString("filepath");
                // TODO Abhishek Get action to be taken is it download or delete

                //Log.e(TAG, "i : " + i + " :: filePath : " + filePath);
                String deleteFileLocation = AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSETS_MAIN_FOLDER + File.separator + filePath;
                File file = new File(deleteFileLocation);


                String tFilePath = filePath;
                //   String tFileName = fileData.getString("fileName");
                int lastOccurence = tFilePath.lastIndexOf("/");
                String appFolderLoc = tFilePath.substring(0, lastOccurence);

                //OTA Delete file
                if (file.exists()) {
                    if (file.isDirectory()) {
                        String[] children = file.list();
                        for (int a = 0; a < children.length; a++) {
                            try {
                                new java.io.FileWriter(new File(file, children[a]).getAbsolutePath(), false).close();
                                new File(file, children[a]).delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (file.isFile()) {
                        try {
                            new java.io.FileWriter(file.getAbsolutePath(), false).close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        file.delete();
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void downloadFilesFromServer(String appId, String appVersion, String downloadFileName, String destinationPath, String appFilePath, String appOs) {
        String mServerStatus = null;

        JSONObject jsonObject = new JSONObject();
        try {

            JSONObject header = new JSONObject();

            header.put(AppzillonMainScreen.PRE_LOGIN, "true");
            header.put(AppzillonMainScreen.APP_ID, StringUtils.getString(StringUtils.APP_ID));
            header.put(AppzillonMainScreen.SESSION_ID, "");
            header.put(AppzillonMainScreen.INTERFACE_ID, "appzillonOTAFileDownloadReq");
            header.put(AppzillonMainScreen.DEVICE_ID, AppzillonMainScreen.ANDROID_OS);
            header.put(AppzillonMainScreen.REQUEST_KEY, "");
            header.put(AppzillonMainScreen.USER_ID, AppzillonMainScreen.USER_ID_FOR_OTA);
            header.put(AppzillonMainScreen.REQ_STATUS, true);
            header.put(AppzillonMainScreen.ASYNC, "true");

            JSONObject reqBody = new JSONObject();
            JSONObject appFileReq = new JSONObject();
            appFileReq.put(AppzillonMainScreen.APP_VERSION, appVersion);
            appFileReq.put(AppzillonMainScreen.APP_ID, appId);
            appFileReq.put(AppzillonMainScreen.OS, appOs);
            appFileReq.put(AppzillonMainScreen.FILENAME, downloadFileName);
            appFileReq.put(AppzillonMainScreen.FILEPATH, appFilePath);
            reqBody.put("appzillonOTAFileDownloadReq", appFileReq);
            jsonObject.put(AppzillonMainScreen.APPZILLON_HEADER, header);
            jsonObject.put(AppzillonMainScreen.APPZILLON_BODY, reqBody);

        } catch (JSONException e) {
            ApzLogger.e(TAG, e.toString());
        }
        try {
            if (MediaUtils.checkInternetConnection(mContext)) {
                if (!MediaUtils.isSDCardPresent()) {
                    ApzLogger.e(TAG, mContext.getResources().getString(R.string.sdcard_unavailable));
                    return;
                }

            } else {
                ApzLogger.e(TAG, mContext.getResources().getString(R.string.sdcard_unavailable));
                return;
            }

//			HttpResponse response = null;
            String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
            JSONObject response = ServerUtilities.sendRequestToServer(serverUrl, jsonObject.toString());
            if (response != null) {
                mServerStatus = response.toString();
            } else {
                mServerStatus = "error";
            }

        }


        finally {
            if ("error".equals(mServerStatus) || mServerStatus == null) {
                ApzLogger.e(TAG, mContext.getResources().getString(R.string.download_error));
            } else {
                // download Success
                JSONObject jsonResult = null;
                FileOutputStream output = null;
                try {
                    jsonResult = new JSONObject(mServerStatus);
                    final JSONObject resultBody = new JSONObject(jsonResult.getString(AppzillonMainScreen.APPZILLON_BODY));
                    final JSONObject resultHeader = new JSONObject(jsonResult.getString(AppzillonMainScreen.APPZILLON_HEADER));
                    if (resultHeader.getBoolean("status") == true) {

                        JSONObject jsonBodyResult = new JSONObject(resultBody.getString("appzillonOTAFileDownloadResponse"));
                        if (!"".equals(jsonBodyResult.getString("file"))) {
                            //create and download in temp folder
                            String tFilePath=jsonBodyResult.getString("filePath");
                            String tFileName = jsonBodyResult.getString("fileName");
                            int lastOccurence = tFilePath.lastIndexOf("/");
                            String appFolderLoc = tFilePath.substring(0, lastOccurence);
                            otaTempFolder = getDir("appzillonOTATemp"+File.separator+appFolderLoc);
                            if (otaTempFolder != null) {
                                String tFilenameWithPath = otaTempFolder.getPath();
                                File fileLoc = new File(tFilenameWithPath);
                                if (!fileLoc.exists()) {
                                    fileLoc.mkdirs();
                                }
                                output = new FileOutputStream(fileLoc + "/" + tFileName);
                                output.write(Base64.decode(jsonBodyResult.getString("file"), Base64.NO_WRAP));
                            }
                            //download in temp folder ends
                            downloadCount++;
                            STATUS = true;
                        }
                    } else {
                        STATUS = false;
                        ApzLogger.e(TAG, mContext.getResources().getString(R.string.download_error));
                    }
                } catch (JSONException jex) {
                    STATUS = false;
                    ApzLogger.e(TAG, mContext.getResources().getString(R.string.download_error));
                } catch (FileNotFoundException fnfe) {
                    STATUS = false;
                    ApzLogger.e(TAG, mContext.getResources().getString(R.string.file_notfound));
                } catch (Exception e) {
                    STATUS = false;
                    ApzLogger.e(TAG, mContext.getResources().getString(R.string.download_error));
                } finally {
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            ApzLogger.e(TAG, e.toString());
                        }
                    }
                }

            }
        }

    }

    //get ota temporary directory created
    private File getDir(String loc) {
        File sdDir;
        if ("N".equalsIgnoreCase(AppzillonMainScreen.activity.getResources().getString(R.string.INTERNALSANDBOX))) {
            sdDir = new File(mContext.getExternalFilesDir(null).getAbsolutePath() + File.separator + loc);
        } else {
            sdDir = new File(AppzillonMainScreen.SANDBOX_LOC + File.separator + loc);
        }

        try {
            if (sdDir.exists()) {
                return sdDir;
            } else {
                if (sdDir.mkdirs()) {
                    return sdDir;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {

            Log.e(TAG, "Create directory failed : " + e.getMessage());
        }


        return null;
    }
    //update files to the sandbox.Obtain from temp folders

    public boolean refreshFiles(JSONArray array) {
        boolean isRefreshed=false;
        for (int i = 0; i < array.length(); i++) {
            JSONObject fileData = null;
            String fileName = "";
            String destinationPath = "";
            String appFolderLoc="";
            String tempFolder="";
            try {
                fileData = array.getJSONObject(i);
                String filePath = fileData.getString("filepath");
                fileName = fileData.getString("filename");
                if (filePath.contains("/")) {
                    int lastOccurence = filePath.lastIndexOf("/");
                    appFolderLoc = filePath.substring(0, lastOccurence);
                    destinationPath = AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSETS_MAIN_FOLDER + File.separator + appFolderLoc;
                } else {
                    // TODO Abhishek
                    destinationPath = AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSETS_MAIN_FOLDER + File.separator + filePath;
                }

                String outFileName = "";
                if (destinationPath.contains(mContext.getExternalFilesDir(null).getAbsolutePath())) {
                    tempFolder=mContext.getExternalFilesDir(null).getAbsolutePath() + File.separator + "appzillonOTATemp"+ File.separator+appFolderLoc;
                    outFileName = destinationPath;
                } else {
                    //outFileName = mContext.getExternalFilesDir(null).getAbsolutePath() + File.separator + destinationPath;
                    if ("N".equalsIgnoreCase(AppzillonMainScreen.activity.getResources().getString(R.string.INTERNALSANDBOX))) {
                        tempFolder=mContext.getExternalFilesDir(null).getAbsolutePath() + File.separator + "appzillonOTATemp"+ File.separator+appFolderLoc;
                        outFileName = mContext.getExternalFilesDir(null).getAbsolutePath() + File.separator + destinationPath;
                    } else {
                        tempFolder=AppzillonMainScreen.SANDBOX_LOC + File.separator+"appzillonOTATemp"+ File.separator+appFolderLoc;
                        outFileName = destinationPath+ File.separator;
                    }
                }
                File fileLoc = new File(outFileName);
                File tempOTAFolder=new File(tempFolder);
                File  tempFiles[] = tempOTAFolder.listFiles();
                for (File file : tempFiles) {
                    if (file.getName().equalsIgnoreCase(fileName)) {
                        file.renameTo(new File(fileLoc + "/" + fileName));
                    }
                }
                isRefreshed=true;
            } catch (Exception e) {
                ApzLogger.e(TAG, e.getMessage());
                isRefreshed=false;
            }

        }
        return isRefreshed;
    }

    public static boolean deleteOtaTempFolder(File dir) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteOtaTempFolder(new File(dir, children[i]));
                    if (!success) {
                        // return false;
                    }
                }
            }
        } catch (Exception e) {
            ApzLogger.e(TAG, e.getMessage());
        }

        // The directory is now empty so delete it
//        new File(dir.getAbsolutePath()).mkdir();
        AppzillonUtils.getApzFile(dir.getAbsolutePath(),null).mkdir();
        return dir.delete();
    }


    public String getUpdatedAppversion() {
        return UPDATED_APP_VERSION;
    }
}


