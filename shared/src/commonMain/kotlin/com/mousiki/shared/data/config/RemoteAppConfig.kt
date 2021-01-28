package com.mousiki.shared.data.config

expect class RemoteAppConfig {

    suspend fun awaitActivation()

    fun getYoutubeApiKeys(): List<String>

    fun getApiConfig(): ApiConfig

    fun searchConfig(): SearchConfig

    fun playlistApiConfig(): SearchConfig

    fun artistSongsApiConfig(): SearchConfig

    fun homeApiConfig(): SearchConfig

    fun loadChartSongsFromFirebase(): Boolean

    fun loadPlaylistSongsFromApi(): Boolean

    fun loadGenreSongsFromFirebase(): Boolean

    fun getOffsetListAds(): Int

    fun getFrequencyRateAppPopup(): Int

    fun rewardAdOn(): Boolean

    fun newHomeEnabled(): Boolean

    fun bannerAdOn(): Boolean

    fun searchArtistTracksFromMousikiApi(): Boolean

    fun showNativeAdTrackOptions(): Boolean

    fun getClickCountToShowReward(): Int

    /* Duration in hours */
    fun getHomeCacheDuration(): Int
}

object ConfigCommon {
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

    const val DEF_ADS_LIST_OFFSET = 6
    const val DEF_FREQ_POPUP_RATE = 3
    const val DEF_CLICK_TO_SHOW_REWARD = 7
}