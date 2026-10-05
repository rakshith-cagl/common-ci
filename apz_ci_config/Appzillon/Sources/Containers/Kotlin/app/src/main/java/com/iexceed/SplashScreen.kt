package com.iexceed


import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.iexceed.unifiedApp.AppzillonMainScreen
import com.iexceed.unifiedApp.BuildConfig
import com.iexceed.unifiedApp.R
import com.iexceed.common.AppzillonUtils
import com.scottyab.rootbeer.RootBeer

class SplashScreen : Activity() {
    private val mSplashTimeOut = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if(BuildConfig.BUILD_TYPE=="release") {
            if (AppzillonUtils.isRunningOnEmulator()) {
                Toast.makeText(this, resources.getString(R.string.app_not_support_emulator), Toast.LENGTH_SHORT).show()
                AppzillonUtils.closeApplication(this)
            } else if (RootBeer(this).isRooted) {
                Toast.makeText(this, resources.getString(R.string.rooted_device_no_applunch), Toast.LENGTH_SHORT).show()
                AppzillonUtils.closeApplication(this)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val i = Intent(this, AppzillonMainScreen::class.java)
                i.putExtra("type", "com.iexceed.shortcut")
                i.putExtra("action", getIntent().action)
                startActivity(i)
                finish()
            }, mSplashTimeOut)
        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }
}
