package com.mousiki.shared.preference

import com.russhwolf.settings.Settings

expect class SettingsProvider {
    fun providesSettings(): Settings
    fun providesOldSettings(): Settings // Needed for android due to the fact we have two shared prefs
}