package com.cas.musicplayer.data.remote.retrofit

import okhttp3.Interceptor
import okhttp3.Response

/**
 **********************************
 * Created by Abdelhadi on 4/19/19.
 **********************************
 */

class AddKeyInterceptor : Interceptor {

    private val keys = mutableListOf(
        "AIzaSyC1kFDwpC9FTJfHolxXHd_2Lo9cD3Yd2WQ",
        "AIzaSyAzLo5mV0ciK_Rhn5uzlsDouDieJA8FYNM",
        "AIzaSyCRvpp4fPTwFbLRLf9D9Z8K85tu8Dj9NCE"
    )

    private var currentKey = keys[0]

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url().newBuilder()
            .addQueryParameter("key", currentKey)
            .build()
        val builder = request.newBuilder()
        request = builder.url(url).build()
        val response = chain.proceed(request)
        if (response.code() == 403 || response.code() == 400) {
            return retryRequestWithAnotherKey(chain)
        }
        return response
    }

    private fun retryRequestWithAnotherKey(chain: Interceptor.Chain): Response {
        var retryCount = 0
        var response: Response
        do {
            retryCount++
            val request = chain.request()
            currentKey = nexKey()
            val urlWithNewKey = request.url().newBuilder()
                .addQueryParameter("key", currentKey)
                .build()
            response = chain.proceed(request.newBuilder().url(urlWithNewKey).build())
        } while ((response.code() == 403 || response.code() == 400) && retryCount < 4)
        return response
    }

    private fun nexKey(): String {
        val indexOfCurrent = keys.indexOf(currentKey)
        if (indexOfCurrent < 0 || indexOfCurrent >= keys.size - 1) {
            return keys[0]
        }
        return keys[indexOfCurrent + 1]
    }
}