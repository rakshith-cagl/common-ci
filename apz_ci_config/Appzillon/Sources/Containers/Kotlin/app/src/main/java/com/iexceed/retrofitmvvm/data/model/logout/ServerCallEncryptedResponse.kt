package com.iexceed.retrofitmvvm.data.model.logout

import com.iexceed.retrofitmvvm.data.ApzEncryptedHeader

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/

data class ServerCallEncryptedResponse(
    override var appzillonBody: String,
    override var appzillonQop: String?,
    override var appzillonHeader: String,
    override var appzillonErrors: String?,
    override var appzillonSafe: String?
) : ApzEncryptedHeader()