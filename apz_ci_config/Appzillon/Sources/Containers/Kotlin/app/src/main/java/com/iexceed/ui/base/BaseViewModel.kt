package com.iexceed.ui.base

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iexceed.appzillonapp.R
import com.iexceed.retrofitmvvm.repository.ApzRepository
import com.iexceed.retrofitmvvm.utils.Resource
import com.iexceed.retrofitmvvm.utils.network.NetworkHelper
import javax.net.ssl.HttpsURLConnection


abstract class BaseViewModel(
    private val networkHelper: NetworkHelper,
    private val apzRepository: ApzRepository
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
    }

    val messageStringId: MutableLiveData<Resource<Int>> = MutableLiveData()
    val messageString: MutableLiveData<Resource<String>> = MutableLiveData()
    val orientationListenerLiveData  = MutableLiveData<String>()
    val notificationListenerLiveData  = MutableLiveData<Intent>()
    val shortcutListenerLiveData  = MutableLiveData<Intent>()

    protected fun checkInternetConnectionWithMessage(): Boolean =
        if (networkHelper.isNetworkConnected()) {
            true
        } else {
            messageStringId.postValue(Resource.error(R.string.network_connection_error))
            false
        }

    protected fun checkInternetConnection(): Boolean = networkHelper.isNetworkConnected()

    protected fun handleNetworkError(err: Throwable?) =
        err?.let {
            networkHelper.castToNetworkError(it).run {
                when (status) {
                    -1 -> messageStringId.postValue(Resource.error(R.string.network_default_error))
                    0 -> messageStringId.postValue(Resource.error(R.string.server_connection_error))
                    HttpsURLConnection.HTTP_UNAUTHORIZED -> {
                        forcedLogoutUser()
                        messageStringId.postValue(Resource.error(R.string.permission_denied))
                    }
                    HttpsURLConnection.HTTP_INTERNAL_ERROR ->
                        messageStringId.postValue(Resource.error(R.string.network_internal_error))
                    HttpsURLConnection.HTTP_UNAVAILABLE ->
                        messageStringId.postValue(Resource.error(R.string.network_server_not_available))
                    else -> messageString.postValue(Resource.error(message))
                }
            }
        }

    protected open fun forcedLogoutUser() {
        // do something
    }


    abstract fun onCreate()
}