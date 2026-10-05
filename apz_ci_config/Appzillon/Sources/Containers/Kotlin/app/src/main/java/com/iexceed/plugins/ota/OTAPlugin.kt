package com.iexceed.plugins.ota

import android.content.Context
import android.util.Base64
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.AppzillonConstants.ANDROID_OS
import com.iexceed.common.AppzillonConstants.ASSETS_MAIN_FOLDER
import com.iexceed.common.ServerUtilities
import com.iexceed.common.StringUtils
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder
import com.iexceed.utils.localstorage.FileAccessHelper
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class OTAPlugin(private var mContext: Context) {
    private var STATUS = false
    var downloadCount = 0
    var otaTempFolder: File? = null

    //  public File otaTempFiles[] = null;
    var OTA_ACTION_DELETE = "delete"
    var updatedAppversion: String? = null
    var tempAppVer: String? = null
    // Change this approach after DI integration
    //val apiInterface = RetrofitBuilder.apiService
    val apiInterface: ApiService = RetrofitBuilder.getRetrofit(mContext).create(ApiService::class.java)
    var presentApp: String? = null
    val fileData = null
    lateinit var file: File
    lateinit var sdDir: File
    
    fun getDataForOTA(appId: String?, appVersion: String?): Boolean {
        updatedAppversion = appVersion

        //Abhishek , bug id 5333, getting present APP ID START
        var presentApp: String? = null
        try {
            presentApp = StringUtils.getString(StringUtils.APP_ID)
        } catch (e: Exception) {
            //presentApp = mContext.getResources().getString(R.string.app_id);
            ApzLogger.e(TAG, "" + e.message)
        }
        //Abhishek , bug id 5333, getting present APP ID END
        val jsonObject = JSONObject()
        try {
            val header = JSONObject()
            header.put(AppzillonConstants.REQ_STATUS, true) // sid, 3.2 server changes
            header.put(AppzillonConstants.PRE_LOGIN, "true")

            //Abhishek , bug id 5333, sending app id as that of present APP START
//			header.put(AppzillonMainScreen.APP_ID, mContext.getResources().getString(R.string.app_id));
            header.put(AppzillonConstants.APP_ID, presentApp)
            //Abhishek , bug id 5333, sending app id as that of present APP END
            header.put(AppzillonConstants.SESSION_ID, "")
            header.put(AppzillonConstants.INTERFACE_ID, "appzillonGetAppFile")
            header.put(AppzillonConstants.SCREEN_ID, "login")
            header.put(AppzillonConstants.DEVICE_ID, ANDROID_OS)
            header.put(AppzillonConstants.REQUEST_KEY, "")
            header.put(AppzillonConstants.USER_ID, AppzillonConstants.USER_ID_FOR_OTA)
            val reqBody = JSONObject()
            val appFileReq = JSONObject()
            appFileReq.put(AppzillonConstants.APP_VERSION, appVersion)
            appFileReq.put(AppzillonConstants.APP_ID, appId)
            appFileReq.put(AppzillonConstants.OS, ANDROID_OS)
            reqBody.put("appzillonAppFilesRequest", appFileReq)
            jsonObject.put(AppzillonConstants.APPZILLON_HEADER, header)
            jsonObject.put(AppzillonConstants.APPZILLON_BODY, reqBody)
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }

        val lPayLoadEncryption = StringUtils.getString("payloadEncryption")
        val lReq: RequestBody = getRequestBody(lPayLoadEncryption, jsonObject)

        val clientNonce = System.currentTimeMillis().toString()
        val interfaceId = "appzillonGetAppFile"

        val call = apiInterface.getAppFileRequest(lReq)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val lRes = response
                val lApzResponse = lRes.body()?.string()

                if (lRes.isSuccessful && lApzResponse != null) {
                    val lResponseObj: JSONObject? = retrieveApzResponse(
                        lPayLoadEncryption,
                        lApzResponse,
                        clientNonce,
                        interfaceId
                    )
                    if (lResponseObj != null) {
                        handleWhenThereIsValidResponse(lResponseObj, appVersion)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                ApzLogger.e(TAG, "Server Connection Error : "+t.message)
            }
        })
        return STATUS
    }

    private fun handleWhenThereIsValidResponse(
        lResponseObj: JSONObject,
        appVersion: String?
    ) {
        val body = lResponseObj.getJSONObject(AppzillonConstants.APPZILLON_BODY)
        val header = lResponseObj.getJSONObject(AppzillonConstants.APPZILLON_HEADER)
        val status = header.getBoolean("status")

        if (status) {
            val array = body.getJSONArray(body.getString("appId"))
            deleteArray = JSONArray()
            downloadArray = JSONArray()
            for (i in 0 until array.length()) {
                val fileData = array.getJSONObject(i)
                val action = fileData.getString("action")
                if (action.equals(OTA_ACTION_DELETE, ignoreCase = true)) {
                    deleteArray!!.put(array.getJSONObject(i))
                } else {
                    downloadArray!!.put(array.getJSONObject(i))
                }
            }
            actionOnOTAFiles(body, appVersion)
        } else {
            val error = lResponseObj.getJSONArray(AppzillonConstants.APPZILLON_ERRORS)
            val errObj = error.getJSONObject(0)
            ApzLogger.e(TAG, "Error : $errObj")
        }
    }

    private fun actionOnOTAFiles(body: JSONObject, oldAppVersion: String?) {
        try {
            val array = body.getJSONArray(body.getString("appId"))
            if (downloadArray!!.length() != 0) {
                for (i in 0 until downloadArray!!.length()) {
                    val fileData = downloadArray!!.getJSONObject(i)
                    val filePath = fileData.getString("filepath")

                    downloadFilesFromServer(
                        body.getString("appId"),
                        fileData.getString(AppzillonConstants.APP_VERSION),
                        fileData.getString("filename"),
                        filePath,
                        fileData.getString(AppzillonConstants.OS)
                    )
                    tempAppVer = fileData.getString(AppzillonConstants.APP_VERSION)
                }
                validateDownloadCount(array, oldAppVersion)
            } else {
                handleDeleteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            updatedAppversion = oldAppVersion
        }
    }

    private fun validateDownloadCount(array: JSONArray, oldAppVersion: String?) {
        if (downloadCount != 0 && downloadCount == downloadArray!!.length()) {
            if (deleteArray!!.length() != 0) deleteOtaFiles(deleteArray)
            val isRefreshed = refreshFiles(array)
            deleteOtaTempFolder(getDir("appzillonOTATemp"))
            if (isRefreshed) {
                updatedAppversion = tempAppVer
                AppzillonConstants.UPDATE_REQUEST = "N"
                STATUS = true
            } else {
                updatedAppversion = oldAppVersion
                STATUS = false
            }
        } else {
            updatedAppversion = oldAppVersion
            deleteOtaTempFolder(getDir("appzillonOTATemp"))
            STATUS = false
        }
    }

    private fun handleDeleteArray() {
        if (deleteArray!!.length() != 0) {
            deleteOtaFiles(deleteArray)
            updatedAppversion = tempAppVer
            AppzillonConstants.UPDATE_REQUEST = "N"
            STATUS = true
        }
    }

    private fun deleteOtaFiles(deleteArray: JSONArray?) {
        try {
            for (i in 0 until deleteArray!!.length()) {
                val fileData = deleteArray.getJSONObject(i)
                val filePath = fileData.getString("filepath")

                val deleteFileLocation = AppzillonMainScreen.SANDBOX_LOC + File.separator + ASSETS_MAIN_FOLDER + File.separator + filePath
                val file = File(deleteFileLocation)
                val lastOccurence = filePath.lastIndexOf("/")
                filePath.substring(0, lastOccurence)

                //OTA Delete file
                if (file.exists()) {
                    if (file.isDirectory) {
                        file.deleteRecursively()
                    } else if (file.isFile) {
                        val isDeleted = file.delete()
                        if (!isDeleted){
                            //handle if not deleted
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun downloadFilesFromServer(
        appId: String,
        appVersion: String,
        downloadFileName: String,
        appFilePath: String,
        appOs: String
    ) {
        val jsonObject = JSONObject()
        try {
            val header = JSONObject()
            header.put(AppzillonConstants.PRE_LOGIN, "true")
            header.put(AppzillonConstants.APP_ID, StringUtils.getString(StringUtils.APP_ID))
            header.put(AppzillonConstants.SESSION_ID, "")
            header.put(AppzillonConstants.INTERFACE_ID, "appzillonOTAFileDownloadReq")
            header.put(AppzillonConstants.DEVICE_ID, ANDROID_OS)
            header.put(AppzillonConstants.REQUEST_KEY, "")
            header.put(AppzillonConstants.USER_ID, AppzillonConstants.USER_ID_FOR_OTA)
            header.put(AppzillonConstants.REQ_STATUS, true)
            header.put(AppzillonConstants.ASYNC, "true")
            val reqBody = JSONObject()
            val appFileReq = JSONObject()
            appFileReq.put(AppzillonConstants.APP_VERSION, appVersion)
            appFileReq.put(AppzillonConstants.APP_ID, appId)
            appFileReq.put(AppzillonConstants.OS, appOs)
            appFileReq.put(AppzillonConstants.FILENAME, downloadFileName)
            appFileReq.put(AppzillonConstants.FILEPATH, appFilePath)
            reqBody.put("appzillonOTAFileDownloadReq", appFileReq)
            jsonObject.put(AppzillonConstants.APPZILLON_HEADER, header)
            jsonObject.put(AppzillonConstants.APPZILLON_BODY, reqBody)
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }
        val lPayLoadEncryption = StringUtils.getString("payloadEncryption")
        val lReq: RequestBody = getRequestBody(lPayLoadEncryption, jsonObject)

        val clientNonce = System.currentTimeMillis().toString()
        val interfaceId = jsonObject.getJSONObject(AppzillonConstants.APPZILLON_HEADER).getString(AppzillonConstants.INTERFACE_ID)
        //val apiInterface = RetrofitBuilder.apiService
        val call = apiInterface.getAppFileRequest(lReq)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val lApzResponse = response.body()?.string()
                if (response.isSuccessful && lApzResponse != null) {
                    val lResponseObj: JSONObject? = retrieveApzResponse(
                        lPayLoadEncryption,
                        lApzResponse,
                        clientNonce,
                        interfaceId
                    )

                    //Download success
                    handleDownloadSuccess(lResponseObj)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                ApzLogger.e(TAG,"Error in Downloading : "+t.message)
            }
        })
    }

    private fun handleDownloadSuccess(lResponseObj: JSONObject?) {
        val jsonResult: JSONObject
        var output: FileOutputStream? = null
        if (lResponseObj != null) {
            jsonResult = lResponseObj

            try {
                val resultBody =
                    JSONObject(jsonResult.getString(AppzillonConstants.APPZILLON_BODY))
                val resultHeader =
                    JSONObject(jsonResult.getString(AppzillonConstants.APPZILLON_HEADER))
                if (resultHeader.getBoolean("status")) {
                    output = handleStatusTrue(resultBody, output)
                } else {
                    STATUS = false
                }
            } catch (e: Exception) {
                STATUS = false
            } finally {
                releaseResources(output)
            }
        }
    }

    private fun handleStatusTrue(
        resultBody: JSONObject,
        output: FileOutputStream?
    ): FileOutputStream? {
        var output1 = output
        val jsonBodyResult =
            JSONObject(resultBody.getString("appzillonOTAFileDownloadResponse"))
        if ("" != jsonBodyResult.getString("file")) {
            //create and download in temp folder
            val tFilePath = jsonBodyResult.getString("filePath")
            val tFileName = jsonBodyResult.getString("fileName")
            val lastOccurence = tFilePath.lastIndexOf("/")
            val appFolderLoc = tFilePath.substring(0, lastOccurence)
            otaTempFolder =
                getDir("appzillonOTATemp" + File.separator + appFolderLoc)
            if (otaTempFolder != null) {
                val tFilenameWithPath = otaTempFolder!!.path
                val fileLoc = File(tFilenameWithPath)
                if (!fileLoc.exists()) {
                    fileLoc.mkdirs()
                }
                val newFile = File(fileLoc, tFileName)
                val isCreated = newFile.createNewFile()
                if (!isCreated) {
                    //Sonar fix
                }
                output1 = FileAccessHelper.getFileOutPutStream(newFile)
                output1.write(
                    Base64.decode(
                        jsonBodyResult.getString("file"),
                        Base64.NO_WRAP
                    )
                )
            }
            //download in temp folder ends
            downloadCount++
            STATUS = true
        }
        return output1
    }

    private fun releaseResources(output: FileOutputStream?) {
        if (output != null) {
            try {
                output.close()
            } catch (e: IOException) {
                ApzLogger.e(TAG, e.toString())
            }
        }
    }

    private fun retrieveApzResponse(
        lPayLoadEncryption: String,
        lApzResponse: String?,
        clientNonce: String,
        interfaceId: String
    ) = if (lPayLoadEncryption == "Y") {
        ServerUtilities.getDecryptedResponseObj(
            mContext, lApzResponse!!, clientNonce, interfaceId
        )
    } else {
        JSONObject(lApzResponse!!)
    }

    private fun getRequestBody(
        lPayLoadEncryption: String,
        jsonObject: JSONObject
    ) = if ("Y".equals(lPayLoadEncryption, ignoreCase = true)) {
        val lJSONObject = ServerUtilities.getEncryptedRequest(mContext, jsonObject.toString())
        lJSONObject.toString().toRequestBody()
    } else {
        jsonObject.toString().toRequestBody()
    }

    //get ota temporary directory created
    private fun getDir(loc: String): File? {
        val sdDir: File
        if ("N".equals(mContext.resources.getString(R.string.INTERNALSANDBOX),ignoreCase = true)) {
            sdDir = File(FileAccessHelper.getExternalFileDirFilePath(mContext,loc))
        } else {
            sdDir = File(AppzillonMainScreen.SANDBOX_LOC + File.separator + loc)
        }
        try {
            return if (sdDir.exists()) {
                sdDir
            } else {
                if (sdDir.mkdirs()) {
                    sdDir
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            ApzLogger.e(TAG, "Create directory failed : " + e.message)
        }
        return null
    }

    //update files to the sandbox.Obtain from temp folders
    fun refreshFiles(array: JSONArray): Boolean {
        var isRefreshed = false
        for (i in 0 until array.length()) {
            var fileData: JSONObject? = null
            var fileName = ""
            var destinationPath = ""
            var appFolderLoc = ""
            var tempFolder = ""
            try {
                fileData = array.getJSONObject(i)
                val filePath = fileData.getString("filepath")
                fileName = fileData.getString("filename")
                if (filePath.contains("/")) {
                    val lastOccurence = filePath.lastIndexOf("/")
                    appFolderLoc = filePath.substring(0, lastOccurence)
                    destinationPath = AppzillonMainScreen.SANDBOX_LOC + File.separator + ASSETS_MAIN_FOLDER + File.separator + appFolderLoc
                } else {
                    destinationPath = AppzillonMainScreen.SANDBOX_LOC + File.separator + ASSETS_MAIN_FOLDER + File.separator + filePath
                }
                var outFileName = ""
                if (destinationPath.contains(
                        FileAccessHelper.getExternalFileDirFile(mContext)
                    )
                ) {
                    tempFolder = FileAccessHelper.getExternalFileDirFile(mContext) + File.separator + "appzillonOTATemp" + File.separator + appFolderLoc
                    outFileName = destinationPath
                } else {
                    //outFileName = mContext.getExternalFilesDir(null).getAbsolutePath() + File.separator + destinationPath;
                    if ("N".equals(
                            mContext.resources.getString(R.string.INTERNALSANDBOX),
                            ignoreCase = true
                        )
                    ) {
                        tempFolder = FileAccessHelper.getExternalFileDirFile(mContext) + File.separator + "appzillonOTATemp" + File.separator + appFolderLoc
                        outFileName = FileAccessHelper.getExternalFileDirFile(mContext) + File.separator + destinationPath
                    } else {
                        tempFolder = AppzillonMainScreen.SANDBOX_LOC + File.separator + "appzillonOTATemp" + File.separator + appFolderLoc
                        outFileName = destinationPath + File.separator
                    }
                }
                renameFiles(outFileName, tempFolder, fileName)
                isRefreshed = true
            } catch (e: Exception) {
                ApzLogger.e(TAG, e.message!!)
                isRefreshed = false
            }
        }
        return isRefreshed
    }

    private fun renameFiles(
        outFileName: String,
        tempFolder: String,
        fileName: String
    ) {
        val fileLoc = File(outFileName)
        val tempOTAFolder = File(tempFolder)
        val tempFiles = tempOTAFolder.listFiles()!!
        for (file in tempFiles) {
            if (file.name.equals(fileName, ignoreCase = true)) {
                val isDone = file.renameTo(File("$fileLoc/$fileName"))
                if (!isDone) {
                    //sonar fix
                }
            }
        }
    }

    fun getUpdatedAppVersion(): String? {
        return updatedAppversion
    }

    companion object {
        private const val TAG = ""
        var downloadArray: JSONArray? = null
        var deleteArray: JSONArray? = null
        fun IsOTAPlugin(): Boolean {
            return true
        }

        fun deleteOtaTempFolder(dir: File?): Boolean {
            try {
                if (dir!!.isDirectory) {
                    val children = dir.list()!!
                    for (i in children.indices) {
                        val success = deleteOtaTempFolder(
                            File(
                                dir,
                                children[i]
                            )
                        )
                        if (!success) {
                            // return false;
                        }
                    }
                }
            } catch (e: Exception) {
                ApzLogger.e(TAG, e.message!!)
            }

            // The directory is now empty so delete it
            val isDeleted = File(dir!!.absolutePath).mkdir()
            if (isDeleted){
                // Sonar fix
            }
            return dir.delete()
        }
    }
}