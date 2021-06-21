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

    override fun logScreenView(screenName: String) {
        val params = mapOf(
            FirebaseAnalytics.Param.SCREEN_NAME to screenName,
            /* FirebaseAnalytics.Param.SCREEN_CLASS to screenName,*/
        )
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }
}