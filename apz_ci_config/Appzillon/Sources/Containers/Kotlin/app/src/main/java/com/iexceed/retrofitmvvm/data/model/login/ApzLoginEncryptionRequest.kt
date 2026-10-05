package com.iexceed.retrofitmvvm.data.model.login

import com.iexceed.retrofitmvvm.data.ApzEncryptedHeader

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/

data class ApzLoginEncryptionRequest(
    override var appzillonBody: String,
    override var appzillonQop: String?,
    override var appzillonHeader: String,
    override var appzillonErrors: String?,
    override var appzillonSafe: String?,
    val encMode: Int,
    val safeBit: Int) : ApzEncryptedHeader()