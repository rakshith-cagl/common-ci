package com.iexceed.plugins.storage;

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin


/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
 
class StoragePlugin {
    companion object {
        fun createPlugin(w: WebView, a: ApzActivity<*>): ApzPlugin? {
            return null
        }

        fun isStoragePlugin(): Boolean {
            return false
        }
    }
}