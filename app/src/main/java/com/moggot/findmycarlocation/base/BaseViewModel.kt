package com.moggot.findmycarlocation.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    private val msg = MutableLiveData<Int>()
    private val loading = MutableLiveData<Boolean>()

    // Post in background thread
    fun postMessage(@StringRes message: Int) {
        msg.postValue(message)
    }

    // Post in main thread
    fun setMessage(@StringRes message: Int) {
        msg.value = message
    }

    fun setLoading(show: Boolean) {
        loading.value = show
    }

    fun postLoading(show: Boolean) {
        loading.postValue(show)
    }

    fun getMsg(): LiveData<Int> = msg
    fun getLoading(): LiveData<Boolean> = loading
}
