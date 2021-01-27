package com.mousiki.shared.preference

import com.russhwolf.settings.Settings

expect class SettingsProvider {
    fun providesSettings(): Settings
}