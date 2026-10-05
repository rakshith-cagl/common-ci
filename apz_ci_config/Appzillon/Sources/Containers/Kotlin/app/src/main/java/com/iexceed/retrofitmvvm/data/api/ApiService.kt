package com.iexceed.retrofitmvvm.data.api

import com.iexceed.retrofitmvvm.data.model.mergeApi.MergeApiEncryptedReqBody
import com.iexceed.retrofitmvvm.data.model.mergeApi.MergeApiReqBody
import com.iexceed.retrofitmvvm.data.response.mergeApi.MergeApiEncryptedResponse
import com.iexceed.retrofitmvvm.data.response.mergeApi.MergeApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import javax.inject.Singleton

@Singleton
interface ApiService {
    // (".")  represents that base URL to be used as the full path.
    @POST(".")
    fun getAppSecToken(@Body aData: RequestBody): Call<ResponseBody>

    @POST(".")
    fun getMultiFactorReq(@Body adata: RequestBody): Call<ResponseBody>

    @POST(".")
    fun getAppInstructionReq(@Body aData: RequestBody): Call<ResponseBody>

    @POST(".")
    fun getMergeApiReq(@Body adata:MergeApiReqBody): Call<MergeApiResponse>

    @POST(".")
    fun getMergeApiReqEncrypted(@Body adata:MergeApiEncryptedReqBody): Call<MergeApiEncryptedResponse>

    @POST(".")
    fun requestLogin(@Body aData: RequestBody): Call<ResponseBody>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST(".")
    fun serverCallRequest(@Body aData: RequestBody): Call<ResponseBody>

    @POST(".")
    fun requestChangePassword(@Body aData: RequestBody): Call<ResponseBody>

    @POST(".")
    fun requestTrackLocation(@Body aReqBody: RequestBody): Call<ResponseBody>


    @Multipart
    @POST("upload")
    fun uploadFile(
        @Part("appzillonRequest") appzillonRequest: RequestBody,
        @Part file: MultipartBody.Part): Call<ResponseBody>

    @POST(".")
    fun downloadFile(@Body aData: RequestBody): Call<ResponseBody>

    @POST(".")
    fun notificationRegistration(@Body lReq: RequestBody): Call<ResponseBody>

    @POST(".")
    fun getAppFileRequest(lReq: RequestBody): Call<ResponseBody>
}
