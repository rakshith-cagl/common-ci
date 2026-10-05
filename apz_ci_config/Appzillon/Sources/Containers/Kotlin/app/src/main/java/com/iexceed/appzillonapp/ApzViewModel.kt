package com.iexceed.appzillonapp

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.iexceed.retrofitmvvm.repository.ApzRepository
import com.iexceed.retrofitmvvm.utils.network.NetworkHelper
import com.iexceed.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ApzViewModel @Inject constructor(
    val networkHelper: NetworkHelper,
    val apzRepository: ApzRepository
) :
    BaseViewModel(networkHelper, apzRepository) {

    override fun onCreate() {
        Log.d("ApzViewModel", "@@@@@@@@@ NetworkHelper is $networkHelper")
        Log.d("ApzViewModel", "@@@@@@@@@ ApzRepository is $apzRepository")
    }

    fun doMultiFactorRequest() {
        viewModelScope.launch {
            try {
                if (networkHelper.isNetworkConnected()) {
                    apzRepository.doMultiFactorRegistration()
                }
            } catch (e: Exception) {
                Log.d("ApzViewModel", e.toString())
            }
        }
    }

    fun doLaunchMergedInterfaceRequest() {
        viewModelScope.launch {
            try {
                if (networkHelper.isNetworkConnected()) {
                    apzRepository.doAppzillonOnAppLaunch()
                }
            } catch (e: Exception) {
                Log.d("ApzViewModel", e.toString())
            }
        }
    }
}