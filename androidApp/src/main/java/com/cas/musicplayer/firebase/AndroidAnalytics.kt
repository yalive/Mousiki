package com.cas.musicplayer.firebase

import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.mousiki.shared.utils.AnalyticsApi

class AndroidAnalytics(
    private val analytics: FirebaseAnalytics,
) : AnalyticsApi {

    override fun logEvent(name: String, params: Map<String, Any>) {
        analytics.logEvent(name, bundleOf(*params.toList().toTypedArray()))
    }
}