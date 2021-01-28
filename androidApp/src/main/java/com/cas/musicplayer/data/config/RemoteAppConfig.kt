package com.cas.musicplayer.data.config

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import com.cas.common.connectivity.ConnectivityState
import com.cas.musicplayer.utils.getCurrentLocale
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.mousiki.shared.data.config.ApiConfig
import com.mousiki.shared.data.config.CountryKeys
import com.mousiki.shared.data.config.SearchConfig
import com.mousiki.shared.preference.PreferencesHelper
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/9/20.
 ***************************************
 */
@Singleton
class RemoteAppConfig @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    private val json: Json,
    private val connectivityState: ConnectivityState,
    private val context: Context,
    private val preferencesHelper: PreferencesHelper
) {

    private var gotConfigResponse = false

    init {
        val connectedBefore = connectivityState.isConnected()
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            gotConfigResponse = true
            if (!task.isSuccessful) {
                val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                val bundle = Bundle()
                bundle.putBoolean("isConnected", connectivityState.isConnected())
                bundle.putBoolean("isConnectedBeforeCall", connectedBefore)
                bundle.putString("local", getCurrentLocale())
                firebaseAnalytics.logEvent("error_fetch_remote_config", bundle)
            } else {
                val ytbKeys = getYoutubeApiKeys()
                if (ytbKeys.isNotEmpty()) {
                    preferencesHelper.setYtbApiKeys(ytbKeys)
                }
            }
        }
    }

    suspend fun awaitActivation() {
        var count = 0
        while (!gotConfigResponse && count < MAX_CYCLES) {
            count++
            delay(WAIT_INTERVAL_MS)
        }
    }

    @SuppressLint("DefaultLocale")
    fun getYoutubeApiKeys(): List<String> {
        val jsonKeysByCountry = firebaseRemoteConfig.getString(YOUTUBE_API_KEYS_BY_COUNTRY)
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
        return firebaseRemoteConfig.getString(YOUTUBE_API_KEYS).split("###")
    }

    private fun getApiConfig(): ApiConfig {
        val jsonString = firebaseRemoteConfig.getString(API_URLS)
        return json.decodeFromString(jsonString)
    }

    fun searchConfig(): SearchConfig = getApiConfig().search

    fun playlistApiConfig(): SearchConfig = getApiConfig().playlists

    fun artistSongsApiConfig(): SearchConfig = getApiConfig().artistSongs

    fun homeApiConfig(): SearchConfig = getApiConfig().home

    fun loadChartSongsFromFirebase(): Boolean {
        return firebaseRemoteConfig.getBoolean(LOAD_CHART_SONGS_FROM_FIREBASE)
    }

    fun loadPlaylistSongsFromApi(): Boolean {
        return firebaseRemoteConfig.getBoolean(LOAD_PLAYLIST_SONGS_FROM_API)
    }

    fun loadGenreSongsFromFirebase(): Boolean {
        return firebaseRemoteConfig.getBoolean(LOAD_GENRE_SONGS_FROM_FIREBASE)
    }

    fun getOffsetListAds(): Int {
        val offset = firebaseRemoteConfig.getLong(LIST_ADS_OFFSET).toInt()
        if (offset <= 0) return DEF_ADS_LIST_OFFSET
        return offset
    }

    fun getFrequencyRateAppPopup(): Int {
        val rateFrequency = firebaseRemoteConfig.getLong(RATE_APP_DIALOG_FREQUENCY).toInt()
        if (rateFrequency <= 0) return DEF_FREQ_POPUP_RATE
        return rateFrequency
    }

    fun rewardAdOn(): Boolean {
        return firebaseRemoteConfig.getBoolean(TURN_ON_REWARD_AD)
    }

    fun newHomeEnabled(): Boolean {
        return firebaseRemoteConfig.getBoolean(ENABLE_NEW_HOME)
    }

    fun bannerAdOn(): Boolean {
        return firebaseRemoteConfig.getBoolean(BANNER_AD_TURNED_ON)
    }

    fun searchArtistTracksFromMousikiApi(): Boolean {
        return firebaseRemoteConfig.getBoolean(SEARCH_ARTIST_TRACKS_FROM_MOUSIKI_API)
    }

    fun showNativeAdTrackOptions(): Boolean {
        return firebaseRemoteConfig.getBoolean(SHOW_AD_FOR_TRACK_OPTIONS)
    }

    fun getClickCountToShowReward(): Int {
        val offset = firebaseRemoteConfig.getLong(SHOW_REWARD_AFTER_X_CLICK).toInt()
        if (offset <= 0) return DEF_CLICK_TO_SHOW_REWARD
        return offset
    }

    /* Duration in hours */
    fun getHomeCacheDuration(): Int {
        val cacheDuration = firebaseRemoteConfig.getLong(HOME_CACHE_DURATION).toInt()
        if (cacheDuration <= 0) return 1 // 1 hour
        return cacheDuration
    }

    companion object {
        private const val DEF_ADS_LIST_OFFSET = 6
        private const val DEF_FREQ_POPUP_RATE = 3
        private const val DEF_CLICK_TO_SHOW_REWARD = 7
        const val YOUTUBE_API_KEYS = "youtube_api_keys"
        const val API_URLS = "api_urls"
        const val LOAD_CHART_SONGS_FROM_FIREBASE = "chart_songs_from_firebase"
        const val LOAD_PLAYLIST_SONGS_FROM_API = "playlist_songs_from_api"
        const val LOAD_GENRE_SONGS_FROM_FIREBASE = "genre_songs_from_firebase"
        const val LIST_ADS_OFFSET = "list_ads_offset"
        const val RATE_APP_DIALOG_FREQUENCY = "rate_app_dialog_frequency"
        const val YOUTUBE_API_KEYS_BY_COUNTRY = "youtube_api_keys_by_country"
        const val TURN_ON_REWARD_AD = "turn_on_reward_ad"
        const val BANNER_AD_TURNED_ON = "turn_on_banner_ad"
        const val SHOW_REWARD_AFTER_X_CLICK = "show_reward_after_x_click"
        const val SHOW_AD_FOR_TRACK_OPTIONS = "show_ad_for_track_options"
        const val SEARCH_ARTIST_TRACKS_FROM_MOUSIKI_API = "search_artist_tracks_from_mousiki_api"
        const val HOME_CACHE_DURATION = "home_cache_duration_hours"
        const val ENABLE_NEW_HOME = "enable_new_home"

        private const val WAIT_INTERVAL_MS = 100L
        private const val MAX_CYCLES = 150
    }
}