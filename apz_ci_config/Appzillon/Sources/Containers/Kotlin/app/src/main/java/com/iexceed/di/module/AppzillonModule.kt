package com.iexceed.di.module

import android.app.Application
import android.content.Context
import com.iexceed.AppzillonApplication
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonConstants.ASSETS_MAIN_FOLDER
import com.iexceed.common.StringUtils
import com.iexceed.di.ApplicationContext
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder
import com.iexceed.retrofitmvvm.utils.network.NetworkHelper
import com.iexceed.retrofitmvvm.utils.network.NetworkHelperImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppzillonModule(private val application: AppzillonApplication) {

    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext(): Context = application

    @Singleton
    @Provides
    fun provideNetworkHelper(): NetworkHelper = NetworkHelperImpl(application)

    @Singleton
    @Provides
    fun provideStringUtils() : StringUtils = StringUtils.StringUtils(
        application,
        "${ASSETS_MAIN_FOLDER}/${application.resources.getString(R.string.MAINAPPID)}")

    @Provides
    @Singleton
    fun provideNetworkService(): ApiService = RetrofitBuilder.getRetrofit(application)
        .create(ApiService::class.java)



}