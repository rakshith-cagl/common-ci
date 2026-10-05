package com.iexceed.plugins.fileoperation

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzFileOperationPlugin {
    companion object {
        fun createPlugin(w: WebView, a: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            return null
        }

        val isFileOperation: Boolean
            get() = false
    }

}
