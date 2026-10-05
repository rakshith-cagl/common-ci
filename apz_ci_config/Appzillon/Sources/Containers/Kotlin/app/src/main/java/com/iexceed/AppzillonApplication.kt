package com.iexceed

import android.app.Application
import com.iexceed.common.StringUtils
import com.iexceed.di.component.AppzillonComponent
import com.iexceed.di.component.DaggerAppzillonComponent
import com.iexceed.di.module.AppzillonModule
import javax.inject.Inject

class AppzillonApplication : Application() {

    lateinit var applicationComponent: AppzillonComponent

    @Inject
    lateinit var stringUtils: StringUtils

    override fun onCreate() {
        super.onCreate()
        injectDependencies()
    }

    private fun injectDependencies() {
        applicationComponent = DaggerAppzillonComponent
            .builder()
            .appzillonModule(AppzillonModule(this))
            .build()
        applicationComponent.inject(this)
    }

    // For testing
    fun setComponent(testComponent: AppzillonComponent) {
        this.applicationComponent = testComponent
    }
}