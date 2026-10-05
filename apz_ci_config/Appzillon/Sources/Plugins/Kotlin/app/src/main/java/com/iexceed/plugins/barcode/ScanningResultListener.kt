package com.iexceed.plugins.barcode

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/

interface ScanningResultListener {
    fun onScanned(result: String)
    fun onFailure(error: String)
}