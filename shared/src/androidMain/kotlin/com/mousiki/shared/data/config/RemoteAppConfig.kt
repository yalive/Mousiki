package com.mousiki.shared.data.config

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.mousiki.shared.preference.PreferencesHelper
import com.mousiki.shared.utils.getCurrentLocale
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/9/20.
 ***************************************
 */
actual class RemoteAppConfig(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    private val json: Json,
    //private val connectivityState: ConnectivityState,
    private val context: Context,
    private val preferencesHelper: PreferencesHelper
) {

    private var gotConfigResponse = false

    init {
        // val connectedBefore = connectivityState.isConnected()
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            gotConfigResponse = true
            if (!task.isSuccessful) {
                /* val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                 val bundle = Bundle()
                 bundle.putBoolean("isConnected", connectivityState.isConnected())
                 bundle.putBoolean("isConnectedBeforeCall", connectedBefore)
                 bundle.putString("local", getCurrentLocale())
                 firebaseAnalytics.logEvent("error_fetch_remote_config", bundle)*/
            } else {
                val ytbKeys = getYoutubeApiKeys()
                if (ytbKeys.isNotEmpty()) {
                    preferencesHelper.setYtbApiKeys(ytbKeys)
                }
            }
        }
    }

    actual suspend fun awaitActivation() {
        var count = 0
        while (!gotConfigResponse && count < MAX_CYCLES) {
            count++
            delay(WAIT_INTERVAL_MS)
        }
    }

    @SuppressLint("DefaultLocale")
    actual fun getYoutubeApiKeys(): List<String> {
        val jsonKeysByCountry =
            firebaseRemoteConfig.getString(ConfigCommon.YOUTUBE_API_KEYS_BY_COUNTRY)
        val keys: List<CountryKeys>? = try {
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
        return firebaseRemoteConfig.getString(ConfigCommon.YOUTUBE_API_KEYS).split("###")
    }

    actual fun getApiConfig(): ApiConfig {
        val jsonString = firebaseRemoteConfig.getString(ConfigCommon.API_URLS)
        return json.decodeFromString(jsonString)
    }

    actual fun searchConfig(): SearchConfig = getApiConfig().search

    actual fun playlistApiConfig(): SearchConfig = getApiConfig().playlists

    actual fun artistSongsApiConfig(): SearchConfig = getApiConfig().artistSongs

    actual fun homeApiConfig(): SearchConfig = getApiConfig().home

    actual fun loadChartSongsFromFirebase(): Boolean {
        return firebaseRemoteConfig.getBoolean(ConfigCommon.LOAD_CHART_SONGS_FROM_FIREBASE)
    }

    actual fun loadPlaylistSongsFromApi(): Boolean {
        return firebaseRemoteConfig.getBoolean(ConfigCommon.LOAD_PLAYLIST_SONGS_FROM_API)
    }

    actual fun loadGenreSongsFromFirebase(): Boolean {
        return firebaseRemoteConfig.getBoolean(ConfigCommon.LOAD_GENRE_SONGS_FROM_FIREBASE)
    }

    actual fun getOffsetListAds(): Int {
        val offset = firebaseRemoteConfig.getLong(ConfigCommon.LIST_ADS_OFFSET).toInt()
        if (offset <= 0) return ConfigCommon.DEF_ADS_LIST_OFFSET
        return offset
    }

    actual fun getFrequencyRateAppPopup(): Int {
        val rateFrequency =
            firebaseRemoteConfig.getLong(ConfigCommon.RATE_APP_DIALOG_FREQUENCY).toInt()
        if (rateFrequency <= 0) return ConfigCommon.DEF_FREQ_POPUP_RATE
        return rateFrequency
    }

    actual fun rewardAdOn(): Boolean {
        return firebaseRemoteConfig.getBoolean(ConfigCommon.TURN_ON_REWARD_AD)
    }

    actual fun newHomeEnabled(): Boolean {
        return firebaseRemoteConfig.getBoolean(ConfigCommon.ENABLE_NEW_HOME)
    }

    actual fun bannerAdOn(): Boolean {
        return firebaseRemoteConfig.getBoolean(ConfigCommon.BANNER_AD_TURNED_ON)
    }

    actual fun searchArtistTracksFromMousikiApi(): Boolean {
        return firebaseRemoteConfig.getBoolean(ConfigCommon.SEARCH_ARTIST_TRACKS_FROM_MOUSIKI_API)
    }

    actual fun showNativeAdTrackOptions(): Boolean {
        return firebaseRemoteConfig.getBoolean(ConfigCommon.SHOW_AD_FOR_TRACK_OPTIONS)
    }

    actual fun getClickCountToShowReward(): Int {
        val offset = firebaseRemoteConfig.getLong(ConfigCommon.SHOW_REWARD_AFTER_X_CLICK).toInt()
        if (offset <= 0) return ConfigCommon.DEF_CLICK_TO_SHOW_REWARD
        return offset
    }

    /* Duration in hours */
    actual fun getHomeCacheDuration(): Int {
        val cacheDuration = firebaseRemoteConfig.getLong(ConfigCommon.HOME_CACHE_DURATION).toInt()
        if (cacheDuration <= 0) return 1 // 1 hour
        return cacheDuration
    }

    companion object {
        private const val WAIT_INTERVAL_MS = 100L
        private const val MAX_CYCLES = 150
    }
}