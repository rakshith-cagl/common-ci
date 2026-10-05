package com.iexceed.di.component

import android.app.Application
import android.content.Context
import com.iexceed.AppzillonApplication
import com.iexceed.common.StringUtils
import com.iexceed.di.ApplicationContext
import com.iexceed.di.module.AppzillonModule
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.utils.network.NetworkHelper
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppzillonModule::class])
interface AppzillonComponent {

    fun inject(app: AppzillonApplication)

    fun getApplication(): Application

    @ApplicationContext
    fun getContext(): Context

    fun getNetworkService(): ApiService

    fun getNetworkHelper(): NetworkHelper

    fun getStringUtils(): StringUtils
}