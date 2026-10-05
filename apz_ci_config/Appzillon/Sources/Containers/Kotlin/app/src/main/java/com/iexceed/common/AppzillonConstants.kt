package com.iexceed.common

import android.util.Log
import kotlin.properties.Delegates
/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
object AppzillonConstants {

   // const val ABC
   var IS_SERVER: Boolean = false

    var IS_TRACK_LOCATION = false
    var SNONCE = ""

    var SESSIONTOKEN = ""

    var APPZILLONSAFE = ""
    var IS_APP_DEBUGABLE = false

    const val sDeviceId = "deviceId"


    //JSON fields....
    const val sDeviceGroups = "deviceGroups"
    const val sDeviceWidth = "width"
    const val sDeviceHeight = "height"
    const val sDeviceOrientation = "orientation"
    const val sDeviceName = "name"

   const val APP_ID = "appId"

   const val SESSION_ID = "sessionId"

   const val INTERFACE_ID = "interfaceId"

    const val SCREEN_ID = "screenId"

    const val  USER_ID_FOR_OTA = "Android"


    const val SAFE_TOKEN = "safeToken"

    const val DEVICE_NAME = "deviceName"

    const val REQUEST_KEY = "requestKey"

    const val USER_ID = "userId"

    const val REQ_STATUS = "status"

    const val APP_VERSION = "appVersion"

    const val OS = "os"

    const val ASYNC = "async"

    const val FILENAME = "fileName"

    const val FILEPATH = "filepath"

    const val OS_ID = "osId"

    const val REQUEST_ID = "requestID"

    const val OS_VERSION = "osVersion"
    const val APPZILLON_HEADER = "appzillonHeader"

    const val APPZILLON_BODY = "appzillonBody"

    const val APPZILLON_ERRORS = "appzillonErrors"


    const val CONTAINER_APP = "containerApp"
    var OTAREQUIRED = ""
    var UPDATE_REQUEST = "N"
    const val EXPIRY_DATE = "expiryDate"

    const val WIPE_OUT = "wipeout"
    var UPDATE_ACTION = ""
    const val REMOTE_DEBUG = "remoteDebug"
    /**
     * Intent's extra that contains the message to be displayed.
     */
    const val EXTRA_MESSAGE = "message"
    const val ANDROID_OS = "ANDROID"

    const val ASSETS_MAIN_FOLDER = "apps"

    const val ASSETS_APZ_FOLDER = "appzillon"

    const val PRE_LOGIN = "preLogin"

    const val DEVICE_ID = "deviceId"

    const val APP_STORE_VERSION = "appVersion" //playstore version

    const val UPDATE_APP_STORE_VERSION = "updateAppVersion" //update playstore version -Y/N

    const val MOBILE_ONE = "mobile1"

    const val MOBILE_TWO = "mobile2"

    const val DEVICE_MODEL = "model"

    const val DEVICE_MAKE = "make"

    const val SCREEN_RESOLUTION = "screenResolution"

    const val APP_TYPE = "appType"

    const val PARENT_APP_ID = "parentAppId"

    const val APZ_SIGNATURE = "signature"
    const val IS_CONTAINER_LOADED = "isContainerLoaded"
    const val IS_APP_TOKEN_STATUS = "appTokenStatus"

    const val CRYPTO_ALGORITHM = "AES/GCM/NoPadding"
    const val RSA_CRYPTO_ALGORITHM = "RSA/NONE/OAEPPadding"
    const val IV_LENGTH = 12

    const val SANDBOX_CREATED = 0
    const val APPZILLON_BROADCAST_PERMISSION = "com.appzillon.permission"

    var isNetworkConnected: Boolean by Delegates.observable(false) { _, _, newValue ->
        Log.i("Network connectivity", "$newValue")
    }

}
