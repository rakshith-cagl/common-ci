package com.iexceed.retrofitmvvm.utils.network

interface NetworkHelper {

    fun isNetworkConnected(): Boolean

    fun castToNetworkError(throwable: Throwable): NetworkError

}