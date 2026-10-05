package com.iexceed.retrofitmvvm.data

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/


abstract class ApzEncryptedHeader{
    abstract var appzillonBody: String
    abstract var appzillonQop: String?
    abstract var appzillonHeader: String
    abstract var appzillonErrors: String?
    abstract var appzillonSafe: String?
}