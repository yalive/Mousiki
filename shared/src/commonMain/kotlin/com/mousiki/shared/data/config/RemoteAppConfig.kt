package com.mousiki.shared.data.config

import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.ConnectivityChecker
import com.mousiki.shared.utils.getCurrentLocale
import com.mousiki.shared.utils.logEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/9/20.
 ***************************************
 */
class RemoteAppConfig(
    private val delegate: RemoteConfigDelegate,
    private val json: Json,
    private val preferencesHelper: PreferencesHelper,
    private val analytics: AnalyticsApi,
    private val connectivityChecker: ConnectivityChecker
) {

    private var gotConfigResponse = false

    init {
        val connectedBefore = connectivityChecker.isConnected()
        delegate.fetchAndActivate { success ->
            gotConfigResponse = true
            if (!success) {
                analytics.logEvent(
                    "error_fetch_remote_config",
                    "isConnected" to connectivityChecker.isConnected(),
                    "isConnectedBeforeCall" to connectedBefore,
                    "local" to getCurrentLocale(),

                    )
            } else {
                val ytbKeys = getYoutubeApiKeys()
                if (ytbKeys.isNotEmpty()) {
                    preferencesHelper.setYtbApiKeys(ytbKeys)
                }
            }
        }
    }

    // withContext(Dispatchers.Main) needed for iOS
    suspend fun awaitActivation() = withContext(Dispatchers.Main) {
        var count = 0
        while (!gotConfigResponse && count < MAX_CYCLES) {
            count++
            delay(WAIT_INTERVAL_MS)
        }
    }

    fun getYoutubeApiKeys(): List<String> {
        val jsonKeysByCountry =
            delegate.getString(YOUTUBE_API_KEYS_BY_COUNTRY)
        val keys: List<YtbCountryKeys>? = try {
            json.decodeFromString(jsonKeysByCountry)
        } catch (e: Exception) {
            null
        }

        if (keys != null && keys.isNotEmpty()) {
            val currentLocale = getCurrentLocale()
            val countryKeys = keys.find {
                it.countries.contains(currentLocale.toUpperCase())
            }?.keys
            if (countryKeys != null && countryKeys.isNotEmpty()) return countryKeys

            // Others
            val others = keys.find { it.countries == "others" }?.keys
            if (others != null && others.isNotEmpty()) return others
        }
        // Old
        return delegate.getString(YOUTUBE_API_KEYS).split("###")
    }

    fun getApiConfig(): ApiConfig {
        val jsonString = delegate.getString(API_URLS)
        return json.decodeFromString(jsonString)
    }

    fun searchConfig(): SearchConfig = getApiConfig().search

    fun playlistApiConfig(): SearchConfig = getApiConfig().playlists

    fun artistSongsApiConfig(): SearchConfig = getApiConfig().artistSongs

    fun homeApiConfig(): SearchConfig = getApiConfig().home

    fun loadChartSongsFromFirebase(): Boolean {
        return delegate.getBoolean(LOAD_CHART_SONGS_FROM_FIREBASE)
    }

    fun loadPlaylistSongsFromApi(): Boolean {
        return delegate.getBoolean(LOAD_PLAYLIST_SONGS_FROM_API)
    }

    fun loadGenreSongsFromFirebase(): Boolean {
        return delegate.getBoolean(LOAD_GENRE_SONGS_FROM_FIREBASE)
    }

    fun getOffsetListAds(): Int {
        val offset = delegate.getInt(LIST_ADS_OFFSET)
        if (offset <= 0) return DEF_ADS_LIST_OFFSET
        return offset
    }

    fun getFrequencyRateAppPopup(): Int {
        val rateFrequency =
            delegate.getInt(RATE_APP_DIALOG_FREQUENCY)
        if (rateFrequency <= 0) return DEF_FREQ_POPUP_RATE
        return rateFrequency
    }

    fun rewardAdOn(): Boolean {
        return delegate.getBoolean(TURN_ON_REWARD_AD)
    }

    fun newHomeEnabled(): Boolean {
        return delegate.getBoolean(ENABLE_NEW_HOME)
    }

    fun bannerAdOn(): Boolean {
        return delegate.getBoolean(BANNER_AD_TURNED_ON)
    }

    fun libraryBannerAdOn(): Boolean {
        return delegate.getBoolean(LIBRARY_BANNER_AD_TURNED_ON)
    }

    fun searchArtistTracksFromMousikiApi(): Boolean {
        return delegate.getBoolean(SEARCH_ARTIST_TRACKS_FROM_MOUSIKI_API)
    }

    fun showNativeAdTrackOptions(): Boolean {
        return delegate.getBoolean(SHOW_AD_FOR_TRACK_OPTIONS)
    }

    fun getClickCountToShowReward(): Int {
        val offset = delegate.getInt(SHOW_REWARD_AFTER_X_CLICK)
        if (offset <= 0) return DEF_CLICK_TO_SHOW_REWARD
        return offset
    }

    /* Duration in hours */
    fun getHomeCacheDuration(): Int {
        val cacheDuration = delegate.getInt(HOME_CACHE_DURATION)
        if (cacheDuration <= 0) return 1 // 1 hour
        return cacheDuration
    }

    fun autoRefreshAds(): Boolean {
        return delegate.getBoolean(AUTO_REFRESH_ADS)
    }

    fun autoRefreshAdsDuration(): Int {
        return delegate.getInt(AUTO_REFRESH_ADS_DURATION)
    }

    fun appOpenAdEnabled(): Boolean {
        return delegate.getBoolean(ENABLE_APP_OPEN_AD)
    }

    fun appOpenAdFrequency(): Int {
        return delegate.getInt(APP_OPEN_AD_FREQUENCY)
    }

    companion object {

        private const val YOUTUBE_API_KEYS = "youtube_api_keys"
        private const val API_URLS = "api_urls"
        private const val LOAD_CHART_SONGS_FROM_FIREBASE = "chart_songs_from_firebase"
        private const val LOAD_PLAYLIST_SONGS_FROM_API = "playlist_songs_from_api"
        private const val LOAD_GENRE_SONGS_FROM_FIREBASE = "genre_songs_from_firebase"
        private const val LIST_ADS_OFFSET = "list_ads_offset"
        private const val RATE_APP_DIALOG_FREQUENCY = "rate_app_dialog_frequency"
        private const val YOUTUBE_API_KEYS_BY_COUNTRY = "youtube_api_keys_by_country"
        private const val TURN_ON_REWARD_AD = "turn_on_reward_ad"
        private const val BANNER_AD_TURNED_ON = "turn_on_banner_ad"
        private const val LIBRARY_BANNER_AD_TURNED_ON = "turn_on_library_banner_ad"
        private const val SHOW_REWARD_AFTER_X_CLICK = "show_reward_after_x_click"
        private const val SHOW_AD_FOR_TRACK_OPTIONS = "show_ad_for_track_options"
        private const val SEARCH_ARTIST_TRACKS_FROM_MOUSIKI_API =
            "search_artist_tracks_from_mousiki_api"
        private const val HOME_CACHE_DURATION = "home_cache_duration_hours"
        private const val ENABLE_NEW_HOME = "enable_new_home"
        private const val AUTO_REFRESH_ADS = "auto_refresh_ads"
        private const val AUTO_REFRESH_ADS_DURATION = "auto_refresh_ads_duration_min"
        private const val ENABLE_APP_OPEN_AD = "enable_app_open_ad"
        private const val APP_OPEN_AD_FREQUENCY = "app_open_ad_frequency"

        private const val DEF_ADS_LIST_OFFSET = 6
        private const val DEF_FREQ_POPUP_RATE = 3
        private const val DEF_CLICK_TO_SHOW_REWARD = 7

        private const val WAIT_INTERVAL_MS = 100L
        private const val MAX_CYCLES = 150

        fun defaultConfig(): Map<String, String> {
            val configMap = mutableMapOf<String, String>()
            configMap[YOUTUBE_API_KEYS] =
                "AIzaSyAlHwrVuiEV7s1yo_qQW2Ka3zIT9psnSB8###AIzaSyD9PTSV4tM4A_D4lrGlmBW71VgW6PWdsVU"
            configMap[LOAD_CHART_SONGS_FROM_FIREBASE] = "true"
            configMap[LIST_ADS_OFFSET] = "5"
            configMap[RATE_APP_DIALOG_FREQUENCY] = "3"
            configMap[LOAD_GENRE_SONGS_FROM_FIREBASE] = "true"
            configMap[LOAD_PLAYLIST_SONGS_FROM_API] = "true"
            configMap[TURN_ON_REWARD_AD] = "true"
            configMap[BANNER_AD_TURNED_ON] = "true"
            configMap[LIBRARY_BANNER_AD_TURNED_ON] = "false"
            configMap[SEARCH_ARTIST_TRACKS_FROM_MOUSIKI_API] = "false"
            configMap[SHOW_REWARD_AFTER_X_CLICK] = "7"
            configMap[SHOW_AD_FOR_TRACK_OPTIONS] = "true"
            configMap[API_URLS] =
                """{ "search": { "apis": ["https://mousikiapp.herokuapp.com", "https://mousikiapp2.herokuapp.com"], "maxApiToTry": 3, "retryCount": 1 }, "artistSongs": { "apis": ["https://mousikiapp.herokuapp.com", "https://mousikiapp2.herokuapp.com"], "maxApiToTry": 3, "retryCount": 1 }, "playlists": { "apis": ["https://mousikiapp.herokuapp.com", "https://mousikiapp2.herokuapp.com"], "maxApiToTry": 3, "retryCount": 1 }, "home": { "apis": ["https://mousikiapp.herokuapp.com"], "maxApiToTry": 3, "retryCount": 1 } }"""
            configMap[HOME_CACHE_DURATION] = "1"
            configMap[AUTO_REFRESH_ADS] = "true"
            configMap[ENABLE_NEW_HOME] = "false"
            configMap[AUTO_REFRESH_ADS_DURATION] = "10"
            configMap[ENABLE_APP_OPEN_AD] = "true"
            configMap[APP_OPEN_AD_FREQUENCY] = "3"
            return configMap
        }
    }
}
