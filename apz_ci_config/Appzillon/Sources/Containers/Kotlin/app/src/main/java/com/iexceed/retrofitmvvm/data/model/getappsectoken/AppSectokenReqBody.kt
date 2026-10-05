package com.iexceed.retrofitmvvm.data.model.getappsectoken

import com.iexceed.retrofitmvvm.data.ApzEncryptedHeader

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/

data class AppSectokenReqBody(
    val appzillonBody: AppzillonBody,
    val appzillonHeader: AppzillonHeader
)


data class AppSectokenEncryptedReqBody(
    override var appzillonBody: String,
    override var appzillonQop: String?,
    override var appzillonHeader: String,
    override var appzillonErrors: String?,
    override var appzillonSafe: String?,
    val safeBit: Int,
    val encMode: Int
) : ApzEncryptedHeader()