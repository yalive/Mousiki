package com.mousiki.shared.preference

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings

actual class SettingsProvider(
    private val context: Context
) {
    actual fun providesSettings(): Settings {
        return AndroidSettings(
            context.getSharedPreferences("mousiki", Context.MODE_PRIVATE)
        )
    }
}