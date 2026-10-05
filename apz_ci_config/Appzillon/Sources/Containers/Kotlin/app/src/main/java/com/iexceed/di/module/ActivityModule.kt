package com.iexceed.di.module

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
import android.webkit.WebView
import android.widget.RelativeLayout
import androidx.lifecycle.ViewModelProvider
import com.iexceed.appzillonapp.ApzViewModel
import com.iexceed.appzillonapp.R
import com.iexceed.common.ApzActivity
import com.iexceed.common.ApzPluginBridge
import com.iexceed.common.LaunchMergedInterface
import com.iexceed.common.MultifactorRegister
import com.iexceed.di.ActivityScope
import com.iexceed.plugins.ApzPluginUtil
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.repository.ApzRepository
import com.iexceed.retrofitmvvm.utils.network.NetworkHelper
import com.iexceed.ui.ViewModelProviderFactory
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import com.iexceed.utils.webview.ApzWebChromeClient
import com.iexceed.utils.webview.ApzWebViewClient
import dagger.Module
import dagger.Provides

/**
 * Kotlin Generics Reference: https://kotlinlang.org/docs/reference/generics.html
 * Basically it means that we can pass any class that extends BaseActivity which take
 * BaseViewModel subclass as parameter
 */
@Module
class ActivityModule(private val activity: ApzActivity<*>) {

    @ActivityScope
    @Provides
    fun getContext(): Context = activity

    @ActivityScope
    @Provides
    fun getActivity(): Activity = activity

    @Provides
    fun provideApzViewModel(
        networkHelper: NetworkHelper,
        apzRepository: ApzRepository
    ): ApzViewModel = ViewModelProvider(
        activity, ViewModelProviderFactory(ApzViewModel::class) {
            ApzViewModel(networkHelper, apzRepository)
        })[ApzViewModel::class.java]

    @ActivityScope
    @Provides
    fun provideWebView(): WebView {

        val relativeLayout = activity.findViewById<View>(R.id.mainWebViewLayout) as RelativeLayout
        val webView: WebView = relativeLayout.findViewById(R.id.main)
        webView.apply {
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            isScrollbarFadingEnabled = true
            isFocusableInTouchMode = true
            filterTouchesWhenObscured = true
            setOnLongClickListener { true }
            isLongClickable = false
        }

        return webView
    }

    @SuppressWarnings("SonarLint:Ignore", "SonarQube:Ignore")
    @Provides
    fun provideWebSettings(webView: WebView): WebSettings = webView.settings.apply {
        allowUniversalAccessFromFileURLs = true
        allowFileAccess = true
        javaScriptEnabled = true
        useWideViewPort = true
        builtInZoomControls = false
        domStorageEnabled = true
        loadsImagesAutomatically = true
        setGeolocationEnabled(true)
        javaScriptCanOpenWindowsAutomatically = true
        setGeolocationDatabasePath("")
        saveFormData = false
        mixedContentMode = MIXED_CONTENT_NEVER_ALLOW
    }

    @Provides
    fun provideApzPluginBridge(webView: WebView): ApzPluginBridge = ApzPluginBridge(
        webView, activity, activity.applicationContext, ApzPluginUtil()
    )

    @Provides
    fun provideApzWebViewClient(activity: Activity, webView: WebView) =
        ApzWebViewClient(activity, webView)

    @ActivityScope
    @Provides
    fun provideApzWebChromeClient(activity: Activity) =
        ApzWebChromeClient(activity)

    @ActivityScope
    @Provides
    fun provideEncryptedPrefHelper(activity: Activity): SharedPreferences =
        EncryptedPrefHelper.init(activity.applicationContext)

    @Provides
    fun provideMultifactorRegister(): MultifactorRegister = MultifactorRegister(activity)

    @Provides
    fun provideLaunchMergedInterface(webView: WebView): LaunchMergedInterface =
        LaunchMergedInterface(activity, webView)

    @Provides
    @ActivityScope
    fun provideApzRepository(
        apiService: ApiService,
        multifactorRegister: MultifactorRegister,
        launchMergedInterface: LaunchMergedInterface
    ): ApzRepository = ApzRepository(apiService, multifactorRegister, launchMergedInterface)

}
