package com.mousiki.shared.data.remote.api

import com.mousiki.shared.data.models.*

interface YoutubeApi {

    suspend fun suggestions(url: String): String

    suspend fun videos(ids: String): Videos

    suspend fun trending(
        maxResults: Int,
        regionCode: String,
        pageToken: String = ""
    ): Videos

    suspend fun playlists(channelId: String): Playlists

    suspend fun channels(ids: String): Channels

    suspend fun channelPlaylists(channelId: String): Playlists

    suspend fun channelVideoIds(channelId: String): SearchResults

    suspend fun playlistVideoIds(playlistId: String, maxResults: Int): PlaylistItems

    suspend fun searchVideoIdsByQuery(query: String, maxResults: Int): SearchResults

    suspend fun searchItemIdsByQuery(query: String, type: String, maxResults: Int): SearchResults


    companion object {
        private const val SNIPPET_TITLE = "%2Csnippet%2Ftitle"
        private const val SNIPPET_THUMBNAILS = "%2Csnippet%2Fthumbnails"
        private const val SNIPPET_CHANNEL_ID = "%2Csnippet%2FchannelId"
        private const val SNIPPET_CHANNEL_TITLE = "%2Csnippet%2FchannelTitle"
        private const val CONTENTDETAILS_DURATION = "%2CcontentDetails%2Fduration"
        private const val CONTENTDETAILS_VIDEO_ID = "%2CcontentDetails%2FvideoId"

        const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

        private const val TRENDING_ITEMS =
            "&fields=items(id$CONTENTDETAILS_DURATION$SNIPPET_CHANNEL_ID$SNIPPET_TITLE$SNIPPET_THUMBNAILS$SNIPPET_CHANNEL_TITLE)%2CnextPageToken%2CprevPageToken%2CpageInfo"

        const val TRENDING =
            BASE_URL + "videos?part=snippet,contentDetails&chart=mostPopular&videoCategoryId=10${YoutubeApi.TRENDING_ITEMS}"

        const val PLAYLIST_ITEMS =
            "&fields=items(id%2CcontentDetails%2FitemCount$SNIPPET_TITLE$SNIPPET_THUMBNAILS$SNIPPET_CHANNEL_TITLE)"

        const val PLAYLISTS =
            BASE_URL + "playlists?part=snippet%2CcontentDetails&maxResults=50&videoCategoryId=10$PLAYLIST_ITEMS"

        const val VIDEOS =
            BASE_URL + "videos?part=contentDetails,snippet&videoCategoryId=10&fields=items(id$CONTENTDETAILS_DURATION$SNIPPET_CHANNEL_ID$SNIPPET_TITLE$SNIPPET_THUMBNAILS$SNIPPET_CHANNEL_TITLE)"

        // Get images
        const val CHANNELS =
            "https://www.googleapis.com/youtube/v3/channels?part=snippet&fields=items(id$SNIPPET_THUMBNAILS$SNIPPET_TITLE)"

    }
}