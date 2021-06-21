package com.mousiki.shared.preference

import com.mousiki.shared.utils.elapsedRealtime
import com.russhwolf.settings.set

class PreferencesHelper(
    val provider: SettingsProvider
) {

    private val setting by lazy { provider.providesSettings() }

    fun mostPopularNextPageToken(): String {
        return setting.getStringOrNull(EXTRAS_POPULAR_NEXT_PAGE).orEmpty()
    }

    fun setMostPopularNextPageToken(nextPageToken: String) {
        setting[EXTRAS_POPULAR_NEXT_PAGE] = nextPageToken
    }

    fun setMostPopularSongsUpdateDate() {
        setting[EXTRAS_POPULAR_UPDATE_DATE] = elapsedRealtime
    }

    fun getMostPopularSongsUpdateDate(): Long {
        return setting.getLong(EXTRAS_POPULAR_UPDATE_DATE, -1)
    }

    fun setChartsUpdateDate() {
        setting[EXTRAS_CHARTS_UPDATE_DATE] = elapsedRealtime
    }

    fun getChartsUpdateDate(): Long {
        return setting.getLong(EXTRAS_CHARTS_UPDATE_DATE, -1)
    }

    /* Home */
    fun setHomeResponseDate() {
        setting[EXTRAS_HOME_UPDATE_DATE] = elapsedRealtime
    }

    fun getHomeResponseDate(): Long {
        return setting.getLong(EXTRAS_HOME_UPDATE_DATE, -1)
    }

    /* Api key */
    fun setCurrentYtbApiKey(key: String) {
        setting[EXTRAS_CURRENT_API_YTB_KEY] = key
    }

    fun getCurrentYtbApiKey(): String {
        val currentKey = setting.getString(EXTRAS_CURRENT_API_YTB_KEY, "")
        if (currentKey.isEmpty()) {
            val key = getYtbApiKeys().firstOrNull().orEmpty()
            if (key.isNotEmpty()) {
                setCurrentYtbApiKey(key)
                return key
            }
        }
        return currentKey
    }

    fun setYtbApiKeys(keys: List<String>) {
        val keyList = keys.joinToString("#MSK#")
        setting[EXTRAS_YTB_KEYS] = keyList
    }

    fun getYtbApiKeys(): List<String> {
        return setting.getString(EXTRAS_YTB_KEYS, "")
            .split("#MSK#")
            .filter { it.isNotEmpty() }
    }

    companion object {
        private const val EXTRAS_POPULAR_NEXT_PAGE = "most-popular-next-page"
        private const val EXTRAS_POPULAR_UPDATE_DATE = "most-popular-songs-update-date"
        private const val EXTRAS_HOME_UPDATE_DATE = "home-response-update-date"
        private const val EXTRAS_CHARTS_UPDATE_DATE = "charts-update-date"
        private const val EXTRAS_CURRENT_API_YTB_KEY = "current-ytb-api-key"
        private const val EXTRAS_YTB_KEYS = "ytb-api-keys"
    }
}