package com.iexceed.plugins.fingerprintscan

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class DeviceFingerprintAccess {

    companion object {
        fun createPlugin(w: WebView, a: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            return null
        }

        val isPlugin: Boolean get() = false
    }
}
