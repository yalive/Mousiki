package com.mousiki.shared.preference

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

actual class SettingsProvider {
    actual fun providesSettings(): Settings {
        return AppleSettings(NSUserDefaults.standardUserDefaults)
    }

    actual fun providesOldSettings(): Settings {
        // Not needed for iOS, just return default
        return providesSettings()
    }
}