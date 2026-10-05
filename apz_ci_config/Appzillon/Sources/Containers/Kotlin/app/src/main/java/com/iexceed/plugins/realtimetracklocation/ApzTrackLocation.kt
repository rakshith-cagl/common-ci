package com.iexceed.plugins.realtimetracklocation

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzTrackLocation {
    companion object {
        fun createPlugin(w: WebView, a: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            return null
        }

        fun isPlugin(): Boolean {
            return false
        }
    }
}
