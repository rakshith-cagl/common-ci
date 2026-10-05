package com.iexceed.common

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.Window
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.iexceed.AppzillonApplication
import com.iexceed.di.component.ActivityComponent
import com.iexceed.di.component.DaggerActivityComponent
import com.iexceed.di.module.ActivityModule
import com.iexceed.plugins.ApzPluginUtil
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.ui.base.BaseViewModel
import javax.inject.Inject


abstract class ApzActivity<VM : BaseViewModel> : AppCompatActivity() {

    @Inject
    lateinit var viewModel: VM

    var progressDialog: CustomProgressDialog? = null
    var swipeLayout: SwipeRefreshLayout? = null

    var mapActivityResultHandler: SparseArray<ExternalActivityResultHandler>? = null
    var mapPermissionResultHandler: SparseArray<OnPermissionsResultHandler>? = null
    protected var splashDialog: Dialog? = null
    var apzPluginUtil: IapzPluginUtil = ApzPluginUtil()
    var requestCode : Int? = null

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    init {
        if (mapActivityResultHandler == null) {
            mapActivityResultHandler = SparseArray()
        }

        if (mapPermissionResultHandler == null) {
            mapPermissionResultHandler = SparseArray()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupFeatures(savedInstanceState)
        setContentView(provideLayoutId())
        injectDependencies(buildActivityComponent())
        super.onCreate(savedInstanceState)
        setupObservers()
        setupView(savedInstanceState)
        viewModel.onCreate()

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (mapActivityResultHandler != null) {
                    val resultHandler = mapActivityResultHandler!![requestCode!!]
                    if (resultHandler != null) {
                        removeExternalActivityResultHandler(requestCode!!)
                        resultHandler.handleActivityResult(result.resultCode, result.data)
                    }
                }
            }
    }

    private fun setupFeatures(savedInstanceState: Bundle?) {
        // to be discussed and removed
        val titleBarString = "NO"
        if (savedInstanceState == null) {
            val enableNavigation = "YES".equals(titleBarString, ignoreCase = true)
            if (enableNavigation) {
                requestWindowFeature(Window.FEATURE_CUSTOM_TITLE)
            } else {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
            }
        }
    }

    private fun buildActivityComponent() =
        DaggerActivityComponent
            .builder()
            .appzillonComponent((application as AppzillonApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()

    protected open fun setupObservers() {
        viewModel.messageString.observe(this, Observer {
            //Sonar fix
        })

        viewModel.messageStringId.observe(this, Observer {
            //Sonar fix
        })
    }

    open fun goBack() = onBackPressedDispatcher.onBackPressed()

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    protected abstract fun injectDependencies(activityComponent: ActivityComponent)

    protected abstract fun setupView(savedInstanceState: Bundle?)


    fun addExternalActivityResultHandler(
        requestCode: Int,
        resultHandler: ExternalActivityResultHandler
    ) {
        if (mapActivityResultHandler != null) {
            mapActivityResultHandler!!.put(requestCode, resultHandler)
        }
    }

    protected fun removeExternalActivityResultHandler(requestCode: Int) {
        if (mapActivityResultHandler != null) {
            mapActivityResultHandler!!.delete(requestCode)
        }
    }

    // handling permission handlers
    protected fun addOnPermissionsResultHandler(
        requestCode: Int,
        resultHandler: OnPermissionsResultHandler
    ) {
        if (mapPermissionResultHandler != null) {
            mapPermissionResultHandler!!.put(requestCode, resultHandler)
        }
    }

    protected fun removeOnPermissionsResultHandler(requestCode: Int) {
        if (mapPermissionResultHandler != null) {
            mapPermissionResultHandler!!.delete(requestCode)
        }
    }

    fun startActivityForResult(
        intent: Intent?,
        requestCode: Int,
        resultHandler: ExternalActivityResultHandler
    ) {
        this.requestCode = requestCode
        addExternalActivityResultHandler(requestCode, resultHandler)
        activityResultLauncher.launch(intent)
    }

    fun startOnPermissionForResult(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int,
        resultHandler: OnPermissionsResultHandler
    ) {
        addOnPermissionsResultHandler(requestCode, resultHandler)
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            requestCode
        )
    }

}
