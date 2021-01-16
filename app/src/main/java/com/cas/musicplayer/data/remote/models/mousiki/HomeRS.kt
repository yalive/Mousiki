package com.cas.musicplayer.data.remote.models.mousiki

import androidx.annotation.Keep
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.toYoutubeDuration
import com.google.gson.annotations.SerializedName

/**
 ************************************
 * Created by Abdelhadi on 11/22/20.
 * Copyright © 2020 Mousiki
 ************************************
 */
@Keep
data class HomeRS(
    @SerializedName("promos")
    val promos: List<VideoInfo>,

    @SerializedName("compactPlaylists")
    val compactPlaylists: List<CompactPlaylistSection>,

    @SerializedName("simplePlaylists")
    val simplePlaylists: List<SimplePlaylistSection>,

    @SerializedName("videoLists")
    val videoLists: List<VideoListSection>
)

@Keep
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
    @SerializedName("id")
    val videoId: String?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("channelId")
    val channelId: String?,
    @SerializedName("channelName")
    val channelName: String?
)

@Keep
data class CompactPlaylistSection(
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("index")
    val index: Int,
    @SerializedName("playlists")
    val playlists: List<CompactPlaylist>
)

@Keep
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

@Keep
data class SimplePlaylistSection(
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("index")
    val index: Int,
    @SerializedName("playlists")
    val playlists: List<SimplePlaylist>
)

@Keep
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

@Keep
data class VideoListSection(
    @SerializedName("title")
    val title: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("index")
    val index: Int,
    @SerializedName("videos")
    val videos: List<VideoInfo>
)

@Keep
data class VideoInfo(
    @SerializedName("video")
    val video: MousikiVideoRS,
    @SerializedName("owner")
    val owner: VideoOwner
)

@Keep
data class VideoOwner(
    @SerializedName("channelId")
    val channelId: String,
    @SerializedName("title")
    val title: String
)

@Keep
fun MousikiVideoRS.toTrack(): MusicTrack {
    return MusicTrack(
        youtubeId = videoId.orEmpty(),
        title = title.orEmpty(),
        duration = MusicTrack.toYoutubeDuration(duration.orEmpty())
    )
}