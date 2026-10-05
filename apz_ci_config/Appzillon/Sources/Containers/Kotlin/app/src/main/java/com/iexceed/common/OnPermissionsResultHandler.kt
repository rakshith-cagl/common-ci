package com.iexceed.common

abstract class OnPermissionsResultHandler {

    abstract fun handlePermissionResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    )
}
