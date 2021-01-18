package com.cas.common.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cas.common.event.Event
import com.cas.common.event.asEvent
import com.cas.common.result.AppMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 **********************************
 * Created by Abdelhadi on 2019-05-28.
 **********************************
 */
open class BaseViewModel : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Main

    private val _toast = MutableLiveData<Event<AppMessage.ResourceMessage>>()
    val toast: LiveData<Event<AppMessage.ResourceMessage>> = _toast

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun showToast(@StringRes messageResId: Int) {
        _toast.value = AppMessage.ResourceMessage(messageResId).asEvent()
    }
}