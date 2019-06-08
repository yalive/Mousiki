package com.secureappinc.musicplayer.base

import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.net.YoutubeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 **********************************
 * Created by Abdelhadi on 2019-05-28.
 **********************************
 */
open class BaseViewModel : ViewModel(), CoroutineScope {
    val job = Job()
    override val coroutineContext = job + Dispatchers.Main

    fun api(): YoutubeApi = ApiManager.api

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}