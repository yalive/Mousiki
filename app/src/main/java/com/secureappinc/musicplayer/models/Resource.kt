package com.secureappinc.musicplayer.models

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<ResultType>(var status: Status, var data: ResultType? = null, var message: String? = null) {

    companion object {
        /**
         * Creates [Resource] object with `SUCCESS` status and [data].
         */
        fun <ResultType> success(data: ResultType): Resource<ResultType> = Resource(Status.SUCCESS, data)

        /**
         * Creates [Resource] object with `LOADING` status to notify
         * the UI to showing loading.
         */
        fun <ResultType> loading(): Resource<ResultType> = Resource(Status.LOADING)

        /**
         * Creates [Resource] object with `ERROR` status and [message].
         */
        fun <ResultType> error(message: String?): Resource<ResultType> = Resource(Status.ERROR, message = message)
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