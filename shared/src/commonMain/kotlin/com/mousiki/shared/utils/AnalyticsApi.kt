package com.mousiki.shared.utils

interface AnalyticsApi {
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
    fun logScreenView(screenName: String)
}

fun AnalyticsApi.logEvent(name: String, vararg params: Pair<String, Any>) {
    logEvent(name, params.toMap())
}