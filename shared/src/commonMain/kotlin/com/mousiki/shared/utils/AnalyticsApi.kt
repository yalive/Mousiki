package com.mousiki.shared.utils

interface AnalyticsApi {
    fun logEvent(name: String, vararg params: Pair<String, Any>)
}