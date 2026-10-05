package com.iexceed.retrofitmvvm.data.api

import android.content.Context
import android.util.Log
import android.util.Log.VERBOSE
import com.google.gson.Gson
import com.iexceed.appzillonapp.BuildConfig
import com.iexceed.common.StringUtils
import com.iexceed.common.StringUtils.getString
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


object RetrofitBuilder {
    private val BASE_URL = getString(StringUtils.SERVER_URL) + "/"

    private lateinit var tmf :TrustManagerFactory
    fun getRetrofit(context: Context): Retrofit {

        if (BASE_URL.contains("https:")) {
                return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(newGenerateSecureOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create(Gson()))
                    .build()
        } else {
            val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(300, TimeUnit.SECONDS)
                .connectTimeout(300, TimeUnit.SECONDS)
                .connectionPool(ConnectionPool(0, 5, TimeUnit.MINUTES))
                .protocols(listOf(Protocol.HTTP_1_1))

            if (BuildConfig.BUILD_TYPE == "debug"){

                setLoggingInterceptor(httpClientBuilder)
            }

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()
        }
    }

    private fun newGenerateSecureOkHttpClient(context: Context): OkHttpClient
    {
        // Create a simple builder for our http client, this is only por example purposes
        val httpClientBuilder = OkHttpClient.Builder()
            .readTimeout(300, TimeUnit.SECONDS)
            .connectTimeout(300, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(0, 5, TimeUnit.MINUTES))
            .protocols(listOf(Protocol.HTTP_1_1))

        if (BuildConfig.BUILD_TYPE == "debug"){

            setLoggingInterceptor(httpClientBuilder)
        }

        if (getString(StringUtils.SSL_PINNING).equals("Y", ignoreCase = true)) {
            val sslContext = SSLContext.getInstance("TLS")
            try {
                // create self-signed server certificate
                    val keyStore = doPinning(context)
                    val tmfAlgo = TrustManagerFactory.getDefaultAlgorithm()
                    tmf = TrustManagerFactory.getInstance(tmfAlgo)
                    tmf.init(keyStore)
                    sslContext.init(null, tmf.trustManagers, null)

                if (sslContext != null) {
                    return httpClientBuilder
                        .sslSocketFactory(sslContext.socketFactory, tmf.trustManagers[0] as X509TrustManager)
                        .build()
                } else {
                    Log.d("RetrofitBuilder", "sslContext is NULL")
                }
            }catch (e:Exception){
                Log.d("RetrofitBuilder", "sslContext is NULL")
            }
        }
        return httpClientBuilder.build()
    }

    private fun doPinning(context: Context) : KeyStore? {
        var keyStore: KeyStore? = null
        var cert: InputStream? = null
        try {
            // create self-signed server certificate
            val certificateList = SSLCertificates.getCertificates(context)
            if (certificateList.isNotEmpty()) {
                val cf = CertificateFactory.getInstance("X.509")
                for (i in certificateList.indices){
                    try {
                        val fileId = context.resources.getIdentifier(certificateList[i], "raw", context.packageName)
                        cert = context.resources?.openRawResource(fileId)
                        val ca = cf.generateCertificate(cert)
                        val keyStoreType = KeyStore.getDefaultType()

                        keyStore = KeyStore.getInstance(keyStoreType)
                        keyStore.load(null, null)
                        keyStore.setCertificateEntry("ca", ca)

                    } catch (e:Exception){
                        Log.d("RetrofitBuilder", "sslContext is NULL")
                    } finally {
                        cert?.close()
                    }
                }
            }

        }catch (e:Exception){
            Log.d("RetrofitBuilder", "sslContext is NULL")
        }
        return keyStore
    }


    private fun setLoggingInterceptor(client: OkHttpClient.Builder){

        client.addInterceptor(
            LoggingInterceptor.Builder()
                .setLevel(if (BuildConfig.DEBUG) Level.BASIC else Level.NONE)
                .log(VERBOSE)
                .build())
    }

}
