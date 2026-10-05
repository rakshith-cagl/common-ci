package com.iexceed.plugins.dataencryption

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class EncryptDecryptUtility {
    companion object {
        fun createPlugin(w: WebView, a: ApzActivity<*>): ApzPlugin? {
            return null
        }

        val isEncryptDecryptUtility: Boolean
            get() = true
    }
}
