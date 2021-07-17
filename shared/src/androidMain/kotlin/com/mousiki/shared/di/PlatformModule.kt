package com.mousiki.shared.di

import androidx.sqlite.db.SupportSQLiteDatabase
import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.downloader.extractor.DefaultExtractor
import com.mousiki.shared.downloader.extractor.Extractor
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.preference.SettingsProvider
import com.mousiki.shared.utils.NetworkUtils
import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.koin.dsl.bind
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

    single { DefaultExtractor() } bind Extractor::class
}