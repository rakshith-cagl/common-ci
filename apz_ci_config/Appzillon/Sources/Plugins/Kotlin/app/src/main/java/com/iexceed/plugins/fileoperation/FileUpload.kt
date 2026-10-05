package com.iexceed.plugins.fileoperation

import android.net.Uri
import android.webkit.MimeTypeMap
import android.webkit.WebView
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import com.iexceed.common.FileUtils.getFile


/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class FileUpload(webview: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil) {
    private val mActivity: ApzActivity<*> = activity
    private val mWebView: WebView = webview
    private var callerId: String? = null
    private var mApzPluginUtil = apzPluginUtil

    private var lfileName: String? = null
    private var isSession: String? = null
    private val TAG = "UPLOAD_TO_SERVER"
    private var customInterfaceId :String? = null

    fun uploadFile(uploadDetails: JSONObject) {
        ApzLogger.w(TAG, " Inside doFileUpload uploadDetails : $uploadDetails")
        val destination: String
        val fileName: String
        val override: String
        val fileURI: String
//        val customInterfaceId :String
        try {
            callerId = uploadDetails.getString("id")
            fileURI = uploadDetails.optString("fileURI")
            destination = uploadDetails.getString("destination")
            isSession = uploadDetails.getString("sessionReq")
            fileName = uploadDetails.getString("fieldID")
            override = if (uploadDetails.has("fileOverride")) {
                uploadDetails.getString("fileOverride")
            } else {
                "N"
            }
            customInterfaceId = if (uploadDetails.has("customInterfaceId")) {
                uploadDetails.getString("customInterfaceId")
            } else {
                ""
            }

            lfileName = fileName.substring(fileName.lastIndexOf('/') + 1)
        } catch (e1: JSONException) {
            ApzLogger.e(TAG, e1.toString())
            val json = JSONObject()
            try {
                json.put("errorCode", e1.toString())
            } catch (e11: JSONException) {
                ApzLogger.e(TAG, e11.toString())
            }
            mApzPluginUtil.sendError(
                callerId, "APZ-CNT-077", json, mActivity,
                mWebView, true
            )
            return
        }
        
        val sourceFile : File
            if(fileURI.equals("")){
            
                sourceFile = File(fileName)
            } else {
                sourceFile = getFile(mActivity, Uri.parse(fileURI))!!
            }
        
      
        val extension: String =
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(sourceFile).toString())
        val appzillonBody = JSONObject()
        val fileDetails = JSONObject()
        val fileDetailsArray = JSONArray()
        val jsonRequest = JSONObject()
        try {
            appzillonBody.put("destination", destination)
            appzillonBody.put("overWrite", override)
            customInterfaceId?.let {
                if(it.isNotEmpty()) {
                    appzillonBody.put("interfaceId", it)
                }
            }
            fileDetails.put("fileName", lfileName)
            fileDetails.put("fileType", extension)
            fileDetails.put("fileNo", "1")
            fileDetails.put("fileSize", sourceFile.length())
            fileDetailsArray.put(fileDetails)
            appzillonBody.put("fileDetails", fileDetailsArray)
            jsonRequest.put("appzillonBody", appzillonBody)
            jsonRequest.put("appzillonHeader", uploadDetails.getJSONObject("appzillonHeader").also {
                it.put("serverNonce", AppzillonConstants.SNONCE)
                it.put("clientNonce", System.currentTimeMillis())
                it.put("sessionToken", AppzillonConstants.SESSIONTOKEN)
            })
            doFileUpload(jsonRequest, sourceFile)
        } catch (e1: JSONException) {
            ApzLogger.e(TAG, e1.toString())
        }
        if (!sourceFile.isFile) {
            mApzPluginUtil.sendError(callerId, "APZ-CNT-002", null, mActivity, mWebView, true)
        }
    }

    private fun uploadSuccess(successMsg: String) {
        val json = JSONObject()
        try {
            json.put("successMessage", successMsg)
        } catch (e1: JSONException) {
            ApzLogger.e(TAG, e1.toString())
        }
        mApzPluginUtil.sendSuccess(callerId, json, false, mActivity, mWebView, true)
    }

    private fun uploadFailure(errorMsg: String) {
        val json = JSONObject()
        try {
            json.put("errorCode", errorMsg)
        } catch (e1: JSONException) {
            ApzLogger.e(TAG, e1.toString())
        }
        mApzPluginUtil.sendError(callerId, "APZ-CNT-007", json, mActivity, mWebView, true)
    }

    private fun doFileUpload(jsonReq: JSONObject, file: File) {
        ApzLogger.w(TAG, " Inside doFileUpload ")
        val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val lMultipartBody: MultipartBody.Part =
            MultipartBody.Part.createFormData(file.name, file.name, requestFile)

        // Change this approach after DI integration
        //val apiInterface = RetrofitBuilder.apiService
        val apiInterface = RetrofitBuilder.getRetrofit(mActivity).create(ApiService::class.java)
        val call = apiInterface.uploadFile(jsonReq.toString().toRequestBody(), lMultipartBody)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val lRes = response
                val lApzResponse = lRes.body()?.string()
                ApzLogger.w(TAG, " Upload File response Obj : $lApzResponse")

                if (lRes.isSuccessful && lApzResponse != null) {
                    handleDoFileUploadResponse(lApzResponse, isSession, lfileName)
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                ApzLogger.w(TAG, "Upload File onFailure ")
                uploadFailure(t.message!!)
            }
        })
    }

    private fun handleDoFileUploadResponse(
        responseJsonString: String,
        isSession: String?,
        fileName: String?
    ) {

        val uploadServerResponse = JSONObject(responseJsonString)
        ApzLogger.w(TAG, " uploadServerResponse : $uploadServerResponse")

        val appzillonHeader = uploadServerResponse.getJSONObject("appzillonHeader")
        val status = appzillonHeader.getBoolean("status")
        if (status) {
            val appzillonBody = uploadServerResponse.getJSONObject("appzillonBody")

            if(!customInterfaceId.isNullOrEmpty()) {
                sendCustomUploadSuccess(appzillonBody)

            } else {
                val appzillonUploadResponse: JSONObject =
                    if (isSession.equals("Y", ignoreCase = true)) {
                        appzillonBody.getJSONObject("appzillonUploadFileResponse")
                    } else {
                        appzillonBody.getJSONObject("appzillonUploadFileWSResponse")
                    }
                val uploadStatus = appzillonUploadResponse.getString(fileName!!)
                if (uploadStatus.equals("success", ignoreCase = true)) {
                    ApzLogger.d(TAG, "upload Success")
                    uploadSuccess("Upload Success")
                } else {
                    ApzLogger.d(TAG, "upload already exists")
                    uploadSuccess("File Already Exists")
                }
            }
        } else {
            ApzLogger.e(TAG, "upload failed")
            uploadFailure("Upload Failed")
        }
    }

    private fun sendCustomUploadSuccess(res: JSONObject) {
        val json = JSONObject()
        try {
            json.put("successMessage", "Upload Success")
            json.put("uploadResponse",res)
        } catch (e1: JSONException) {
            ApzLogger.e(TAG, e1.toString())
        }
        mApzPluginUtil.sendSuccess(callerId, json, false, mActivity, mWebView, true)
    }

}
