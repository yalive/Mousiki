package com.mousiki.shared.di

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.preference.SettingsProvider
import com.mousiki.shared.utils.NetworkUtils
import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.koin.dsl.module

actual val platformModule = module {
    single {
        val driver = AndroidSqliteDriver(
            MousikiDb.Schema,
            get(),
            "music_track_database"
        )
        MousikiDb(driver)
    }

    single { PreferencesHelper(SettingsProvider(get())) }
    single { NetworkUtils(get()) }
}