package com.mousiki.shared.utils

import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getCurrentLocale(): String {
    val code = NSLocale.currentLocale.countryCode?.toUpperCase()
    if (code?.length == 2) return code
    return "US"
}

actual fun getLanguage(): String {
    val language = NSLocale.currentLocale.languageCode.toLowerCase()
    if (language.isNotEmpty()) return language
    return "en"
}