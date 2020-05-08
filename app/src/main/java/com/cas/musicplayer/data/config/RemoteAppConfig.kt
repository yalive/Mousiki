package com.cas.musicplayer.data.config

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import com.cas.common.connectivity.ConnectivityState
import com.cas.musicplayer.utils.getCurrentLocale
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private val connectivityState: ConnectivityState,
    private val gson: Gson,
    private val context: Context
) {

    init {
        val connectedBefore = connectivityState.isConnected()
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                val bundle = Bundle()
                bundle.putBoolean("isConnected", connectivityState.isConnected())
                bundle.putBoolean("isConnectedBeforeCall", connectedBefore)
                bundle.putString("local", getCurrentLocale())
                firebaseAnalytics.logEvent("error_fetch_remote_config", bundle)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun getYoutubeApiKeys(): List<String> {
        val jsonKeysByCountry = firebaseRemoteConfig.getString(YOUTUBE_API_KEYS_BY_COUNTRY)
        val typeTokenKeys = object : TypeToken<List<CountryKeys>>() {}.type
        val keys: List<CountryKeys>? = gson.fromJson(jsonKeysByCountry, typeTokenKeys)
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

    fun loadChartSongsFromFirebase(): Boolean {
        return firebaseRemoteConfig.getBoolean(LOAD_CHART_SONGS_FROM_FIREBASE)
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

    fun getClickCountToShowReward(): Int {
        val offset = firebaseRemoteConfig.getLong(SHOW_REWARD_AFTER_X_CLICK).toInt()
        if (offset <= 0) return DEF_CLICK_TO_SHOW_REWARD
        return offset
    }

    companion object {
        private const val DEF_ADS_LIST_OFFSET = 6
        private const val DEF_FREQ_POPUP_RATE = 3
        private const val DEF_CLICK_TO_SHOW_REWARD = 7
        const val YOUTUBE_API_KEYS = "youtube_api_keys"
        const val LOAD_CHART_SONGS_FROM_FIREBASE = "chart_songs_from_firebase"
        const val LOAD_GENRE_SONGS_FROM_FIREBASE = "genre_songs_from_firebase"
        const val LIST_ADS_OFFSET = "list_ads_offset"
        const val RATE_APP_DIALOG_FREQUENCY = "rate_app_dialog_frequency"
        const val YOUTUBE_API_KEYS_BY_COUNTRY = "youtube_api_keys_by_country"
        const val TURN_ON_REWARD_AD = "turn_on_reward_ad"
        const val SHOW_REWARD_AFTER_X_CLICK = "show_reward_after_x_click"
    }
}