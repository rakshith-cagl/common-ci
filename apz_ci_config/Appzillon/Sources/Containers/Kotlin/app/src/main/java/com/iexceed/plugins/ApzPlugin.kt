package com.iexceed.plugins

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import org.json.JSONObject

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/

 abstract class ApzPlugin {
  protected open var TAG = "ApzPlugin"
  protected var callbackId: String? = null
  protected open lateinit var aActivity: ApzActivity<*>
  protected lateinit var aWebview: WebView
  open val apzPluginUtil: IapzPluginUtil
   get() {
    return ApzPluginUtil()
   }

  companion object {
   var debugLevel = 0
   var checkPermissionFlag = false
  }

  abstract fun execute(params: JSONObject)
}

