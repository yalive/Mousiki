package com.cas.musicplayer.utils

import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.mousiki.shared.utils.AnalyticsApi

class AndroidAnalytics(
    private val analytics: FirebaseAnalytics,
) : AnalyticsApi {

    override fun logEvent(name: String, vararg params: Pair<String, Any>) {
        analytics.logEvent(name, bundleOf(*params))
    }

}