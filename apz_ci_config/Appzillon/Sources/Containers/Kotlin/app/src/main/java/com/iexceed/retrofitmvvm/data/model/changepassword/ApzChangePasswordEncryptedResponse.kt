package com.iexceed.retrofitmvvm.data.model.changepassword

import com.iexceed.retrofitmvvm.data.ApzEncryptedHeader

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/


data class ApzChangePasswordEncryptedResponse(
    override var appzillonBody: String,
    override var appzillonQop: String?,
    override var appzillonHeader: String,
    override var appzillonErrors: String?,
    override var appzillonSafe: String?
) : ApzEncryptedHeader()