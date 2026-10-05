package com.iexceed.codecoverage

import java.lang.annotation.ElementType.METHOD
import java.lang.annotation.ElementType.TYPE
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.RUNTIME
import java.lang.annotation.Target


/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/



//This Annotation is used to Exclude class/methods from codecoverage...

@MustBeDocumented
@Retention(RUNTIME)
@Target(*[TYPE, METHOD])
annotation class Generated
