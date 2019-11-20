package com.cas.musicplayer.base

import androidx.lifecycle.ViewModel
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

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}