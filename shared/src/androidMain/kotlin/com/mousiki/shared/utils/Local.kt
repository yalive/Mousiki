package com.mousiki.shared.utils

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import com.mousiki.shared.BuildConfig

actual fun getCurrentLocale(): String {
    if (BuildConfig.DEBUG) {
        return "MX"
    }
    val tm = globalAppContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    val countryCodeValue: String? = tm?.networkCountryIso

    if (countryCodeValue != null && countryCodeValue.length == 2) {
        return countryCodeValue
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val locales = globalAppContext.resources.configuration.locales
        when {
            locales.isEmpty -> "US"
            locales.get(0)?.country != null && locales.get(0).country.length == 2 -> "US"
            else -> "US"
        }
    } else {
        val country = globalAppContext.resources.configuration?.locale?.country
        if (country != null && country.isNotEmpty() && country.length == 2) country else "US"
    }
}