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
        "AIzaSyC1kFDwpC9FTJfHolxXHd_2Lo9cD3Yd2WQ",//mousiki project api key
        "AIzaSyBbEnTCM6ZQiyZIiYO98usu3gjhFIPSrmA",//appodeal project api key
        "AIzaSyC_yUoi7wGn3tGH3oXjTszPXOI-jQWsipg",//CAStudio project api key
        "AIzaSyBqhHieeAMC79qmGm67df6vm8kMQoTpnog",//chatApp project api key
        "AIzaSyAKBQe0h6RcLTERmnFsuBTnkR85q96afMg",//chatApplication project api key
        "AIzaSyD6zBfCgaiLihDNkN3qqjR6jMBgVmEcXcE",//FireBaseExample project api key
        "AIzaSyC0EY97Ub0plnZICfJ7D-7X9oNTyo-pHXU",//pushNotificationDemo project api key
        "AIzaSyDgsObSiocd0oY-EMKQakzPeHy6QU9hjsU",//quickstart project api key
        "AIzaSyC40U4MYSqEjNnNp8c1389vU3g7kJ1WGCo",//RingtoneApp project api key
        "AIzaSyC4bPPdqk3VL4Gy78hYFU-dnFgtbfa77Fc"//RingtoneApp project api key
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