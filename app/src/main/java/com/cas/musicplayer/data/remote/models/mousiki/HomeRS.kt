package com.cas.musicplayer.data.remote.models.mousiki

import com.cas.musicplayer.domain.model.MusicTrack
import com.google.gson.annotations.SerializedName

/**
 ************************************
 * Created by Abdelhadi on 11/22/20.
 * Copyright © 2020 Mousiki
 ************************************
 */
data class HomeRS(
    @SerializedName("promos")
    val promos: List<MousikiVideoRS>,

    @SerializedName("compactPlaylists")
    val compactPlaylists: List<CompactPlaylistSection>,

    @SerializedName("simplePlaylists")
    val simplePlaylists: List<SimplePlaylistSection>,

    @SerializedName("videoLists")
    val videoLists: List<VideoListSection>
)

data class MousikiVideoRS(
    @SerializedName("title")
    val title: String?,
    @SerializedName("thumbnail")
    val thumbnail: String?,
    @SerializedName("viewCount")
    val viewCount: String?,
    @SerializedName("viewCountShort")
    val viewCountShort: String?,
    @SerializedName("publishedTimeText")
    val publishedTimeText: String?,
    @SerializedName("videoId")
    val videoId: String?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("channelId")
    val channelId: String?,
    @SerializedName("channelName")
    val channelName: String?
)

data class CompactPlaylistSection(
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("index")
    val index: Int,
    @SerializedName("items")
    val playlists: List<CompactPlaylist>
)

data class CompactPlaylist(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("videoCount")
    val videoCount: String,
    @SerializedName("playlistId")
    val playlistId: String,
    @SerializedName("featuredImage")
    val featuredImage: String
)

data class SimplePlaylistSection(
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("index")
    val index: Int,
    @SerializedName("items")
    val playlists: List<SimplePlaylist>
)

data class SimplePlaylist(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("videoCount")
    val videoCount: String,
    @SerializedName("playlistId")
    val playlistId: String,
    @SerializedName("featuredImage")
    val featuredImage: String,
    @SerializedName("updatedTime")
    val updatedTime: String,
    @SerializedName("channelId")
    val channelId: String,
    @SerializedName("channelName")
    val channelName: String
)

data class VideoListSection(
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("index")
    val index: Int,
    @SerializedName("items")
    val videos: List<MousikiVideoRS>
)

fun MousikiVideoRS.toTrack(): MusicTrack {
    return MusicTrack(
        youtubeId = videoId.orEmpty(),
        title = title.orEmpty(),
        duration = duration.orEmpty()
    )
}