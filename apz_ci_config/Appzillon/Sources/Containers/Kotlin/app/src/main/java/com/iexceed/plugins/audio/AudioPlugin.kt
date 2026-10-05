package com.iexceed.plugins.audio

import android.webkit.WebView
import com.iexceed.common.*
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class AudioPlugin {
    companion object {
        fun createPlugin(w: WebView, a: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            return null
        }

        fun isAudioPlugin(): Boolean {
            return false
        }
    }
}
