package com.mousiki.shared.preference

import com.russhwolf.settings.Settings

//import com.russhwolf.settings.AppleSettings

actual class SettingsProvider {
    actual fun providesSettings(): Settings {
        TODO()
    }

    actual fun providesOldSettings(): Settings {
        // Not needed for iOS, just return default
        return providesSettings()
    }
}