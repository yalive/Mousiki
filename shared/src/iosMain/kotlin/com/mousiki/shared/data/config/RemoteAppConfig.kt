package com.mousiki.shared.data.config

actual class RemoteAppConfig {

    actual suspend fun awaitActivation() {
    }

    actual fun getYoutubeApiKeys(): List<String> {
        return listOf(
            "AIzaSyBqhHieeAMC79qmGm67df6vm8kMQoTpnog",
            "AIzaSyC40U4MYSqEjNnNp8c1389vU3g7kJ1WGCo",
            "AIzaSyCnNXLH_W3I8pe0PIsNrDZWb5S9OONZ9vQ",
            "AIzaSyAyV0mk3Gdx-LHG7np_1kSFLcPd62YWIqA",
        )
    }

    actual fun getApiConfig(): ApiConfig {
        val search = SearchConfig(
            apis = listOf("https://theta-topic-205615.uc.r.appspot.com"),
            maxApi = 3,
            retryCount = 3
        )
        return ApiConfig(
            search = search,
            artistSongs = search,
            playlists = search,
            home = search,
        )
    }

    actual fun searchConfig(): SearchConfig = getApiConfig().search

    actual fun playlistApiConfig(): SearchConfig = getApiConfig().playlists

    actual fun artistSongsApiConfig(): SearchConfig = getApiConfig().artistSongs

    actual fun homeApiConfig(): SearchConfig = getApiConfig().home

    actual fun loadChartSongsFromFirebase(): Boolean {
        return false
    }

    actual fun loadPlaylistSongsFromApi(): Boolean {
        return true
    }

    actual fun loadGenreSongsFromFirebase(): Boolean {
        return false
    }

    actual fun getOffsetListAds(): Int {
        return 10
    }

    actual fun getFrequencyRateAppPopup(): Int {
        return 3
    }

    actual fun rewardAdOn(): Boolean {
        return true
    }

    actual fun newHomeEnabled(): Boolean {
        return true
    }

    actual fun bannerAdOn(): Boolean {
        return true
    }

    actual fun searchArtistTracksFromMousikiApi(): Boolean {
        return true
    }

    actual fun showNativeAdTrackOptions(): Boolean {
        return true
    }

    actual fun getClickCountToShowReward(): Int {
        return 10
    }

    /* Duration in hours */
    actual fun getHomeCacheDuration(): Int {
        return 2
    }
}