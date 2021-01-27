package com.cas.musicplayer.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.sqldelight.Query

fun <T : Any> Query<T>.asLiveData(): LiveData<Query<T>> {
    val liveData = MutableLiveData<Query<T>>()
    val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            liveData.postValue(this@asLiveData)
        }
    }
    addListener(listener)
    return liveData
}