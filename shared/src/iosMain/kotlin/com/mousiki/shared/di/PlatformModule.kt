package com.mousiki.shared.di

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.preference.SettingsProvider
import com.mousiki.shared.utils.NetworkUtils
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
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
    single { RemoteAppConfig() }
}