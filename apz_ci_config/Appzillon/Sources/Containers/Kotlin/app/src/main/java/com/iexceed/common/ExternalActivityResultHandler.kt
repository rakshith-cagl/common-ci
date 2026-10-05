package com.iexceed.common

import android.content.Intent

abstract class ExternalActivityResultHandler {
    abstract fun handleActivityResult(resultCode: Int, data: Intent?)
}
