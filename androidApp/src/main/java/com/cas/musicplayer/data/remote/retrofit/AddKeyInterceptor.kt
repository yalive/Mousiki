package com.cas.musicplayer.data.remote.retrofit

import android.content.Context
import android.os.Bundle
import com.cas.common.connectivity.ConnectivityState
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.cas.musicplayer.utils.getCurrentLocale
import com.google.firebase.analytics.FirebaseAnalytics
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 **********************************
 * Created by Abdelhadi on 4/19/19.
 **********************************
 */

// AIzaSyBG7HtgbENiSehKP7O5vKLbW7lnA6Ip_QY   -----> 1
// AIzaSyALB5-Y7FlvLbJGWgZ7lu8GViyHrbezPk4   -----> 2
// AIzaSyDl2cO4YdaIHQHtumnE4UljiLT266sj-uw   -----> 3
@Singleton
class AddKeyInterceptor @Inject constructor(
    private val context: Context,
    private val remoteConfig: RemoteAppConfig,
    private val connectivityState: ConnectivityState
) : Interceptor {

    private var currentKey = ""

    init {
        currentKey = nexKey()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this) {
            checkApiKeys()
        }
        var request = chain.request()
        val url = request.url().newBuilder()
            .addQueryParameter(QUERY_KEY, currentKey)
            .build()
        val builder = request.newBuilder()
        request = builder.url(url).build()
        val response = chain.proceed(request)
        if (response.code() == 403 || response.code() == 400) {
            return retryRequestWithAnotherKey(chain)
        }
        return response
    }

    /**
     * wait for api keys
     */
    private fun checkApiKeys() {
        var count = 0
        while (currentKey.isEmpty() && count < MAX_RETRY_GET_KEYS) {
            count++
            try {
                Thread.sleep(500)
            } catch (e: Exception) {
            }
            val youtubeApiKeys = remoteConfig.getYoutubeApiKeys()
            currentKey = youtubeApiKeys.firstOrNull().orEmpty()
        }
        if (count >= MAX_RETRY_GET_KEYS && currentKey.isEmpty()) {
            val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
            val bundle = Bundle()
            bundle.putBoolean("isConnected", connectivityState.isConnected())
            bundle.putString("local", getCurrentLocale())
            firebaseAnalytics.logEvent("cannot_get_ytb_api_keys", bundle)
        }
    }

    private fun retryRequestWithAnotherKey(chain: Interceptor.Chain): Response {
        var retryCount = 0
        var response: Response
        do {
            retryCount++
            val request = chain.request()
            currentKey = nexKey()
            val urlWithNewKey = request.url().newBuilder()
                .addQueryParameter(QUERY_KEY, currentKey)
                .build()
            response = chain.proceed(request.newBuilder().url(urlWithNewKey).build())
        } while ((response.code() == 403 || response.code() == 400) && retryCount < 10)
        return response
    }

    private fun nexKey(): String {
        val keys = remoteConfig.getYoutubeApiKeys()
        val indexOfCurrent = keys.indexOf(currentKey)
        if (indexOfCurrent < 0 || indexOfCurrent >= keys.size - 1) {
            return keys[0]
        }
        return keys[indexOfCurrent + 1]
    }

    companion object {
        private const val QUERY_KEY = "key"
        private const val MAX_RETRY_GET_KEYS = 20
    }
}