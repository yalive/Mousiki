package com.mousiki.shared.di

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.preference.SettingsProvider
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.ConnectivityChecker
import com.mousiki.shared.utils.NetworkUtils
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    single {
        val driver = NativeSqliteDriver(
            MousikiDb.Schema,
            "mousiki_db"
        )
        MousikiDb(driver)
    }

    single { PreferencesHelper(SettingsProvider()) }
    single { NetworkUtils() }

    // Tempo
    single { IOSAnalytics() } bind AnalyticsApi::class
    single { IOSNetworkChecker() } bind ConnectivityChecker::class
}


// Tempo
class IOSAnalytics : AnalyticsApi {
    override fun logEvent(name: String, vararg params: Pair<String, Any>) {
        println("Log event $name with params: $params")
    }
}

class IOSNetworkChecker : ConnectivityChecker {
    override fun isConnected(): Boolean {
        return true
    }
}