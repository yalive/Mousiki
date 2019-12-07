package com.cas.musicplayer.data.remote.retrofit

import okhttp3.Interceptor
import okhttp3.Response

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-07.
 ***************************************
 */
class RotateKeyInterceptor  : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url().newBuilder()
            .addQueryParameter("key", "AIzaSyAzLo5mV0ciK_Rhn5uzlsDouDieJA8FYNM")
            .build()
        val builder = request.newBuilder()
        request = builder.url(url).build()
        return chain.proceed(request)
    }
}