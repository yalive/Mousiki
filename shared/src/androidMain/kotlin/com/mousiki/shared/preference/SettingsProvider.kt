package com.mousiki.shared.preference

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings

actual class SettingsProvider(
    private val context: Context
) {

    actual fun providesSettings(): Settings {
        return AndroidSettings(
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        )
    }

    actual fun providesOldSettings(): Settings {
        return AndroidSettings(
            context.getSharedPreferences(OLD_PREF_NAME, Context.MODE_PRIVATE)
        )
    }

    companion object {
        val OLD_PREF_NAME = "music-app-pref"
        val PREF_NAME = "mousiki"
    }
}