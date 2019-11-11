package com.cas.musicplayer.base.common

import androidx.lifecycle.MutableLiveData

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class ResourceOld<ResultType>(var status: Status, var data: ResultType? = null, var message: String? = null) {

    companion object {
        /**
         * Creates [ResourceOld] object with `SUCCESS` status and [data].
         */
        fun <ResultType> success(data: ResultType): ResourceOld<ResultType> =
            ResourceOld(
                Status.SUCCESS,
                data
            )

        /**
         * Creates [ResourceOld] object with `LOADING` status to notify
         * the UI to showing loading.
         */
        fun <ResultType> loading(): ResourceOld<ResultType> =
            ResourceOld(Status.LOADING)

        /**
         * Creates [ResourceOld] object with `ERROR` status and [message].
         */
        fun <ResultType> error(message: String?): ResourceOld<ResultType> =
            ResourceOld(
                Status.ERROR,
                message = message
            )
    }
}

/**
 * Status of a resource that is provided to the UI.
 *
 *
 * These are usually created by the Repository classes where they return
 * `LiveData<Resource<T>>` to pass back the latest data to the UI with its fetch status.
 */
enum class Status {
    SUCCESS,
    ERROR,
    LOADING;

    /**
     * Returns `true` if the [Status] is loading else `false`.
     */
    fun isLoading() = this == LOADING
}

fun <T> MutableLiveData<ResourceOld<T>>.isSuccess(): Boolean {
    return value != null && value!!.status == Status.SUCCESS && value!!.data != null
}

fun <T> MutableLiveData<ResourceOld<List<T>>>.hasItems(): Boolean {
    return isSuccess() && value!!.data!!.isNotEmpty()
}

fun <T> MutableLiveData<ResourceOld<T>>.isLoading(): Boolean {
    return value != null && value!!.status == Status.LOADING
}

fun <T> MutableLiveData<ResourceOld<T>>.isError(): Boolean {
    return value != null && value!!.status == Status.ERROR
}

fun <T> MutableLiveData<ResourceOld<T>>.loading() {
    value = ResourceOld.loading()
}