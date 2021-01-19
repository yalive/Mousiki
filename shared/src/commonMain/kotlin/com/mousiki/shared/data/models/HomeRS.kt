package com.mousiki.shared.data.models

import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.toYoutubeDuration
import com.mousiki.shared.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 ************************************
 * Created by Abdelhadi on 11/22/20.
 * Copyright © 2020 Mousiki
 ************************************
 */
@Keep
@Serializable
data class HomeRS(
    val promos: List<VideoInfo>,
    val compactPlaylists: List<CompactPlaylistSection>,
    val simplePlaylists: List<SimplePlaylistSection>,
    val videoLists: List<VideoListSection>
)

@Keep
@Serializable
data class MousikiVideoRS(
    val title: String? = null,
    val thumbnail: String? = null,
    val viewCount: String? = null,
    val viewCountShort: String? = null,
    val publishedTimeText: String? = null,
    @SerialName("id")
    val videoId: String? = null,
    val duration: String? = null,
    val channelId: String? = null,
    val channelName: String? = null
)

@Keep
@Serializable
data class CompactPlaylistSection(
    val title: String? = null,
    val thumbnail: String? = null,
    val index: Int,
    val playlists: List<CompactPlaylist>? = null
)

@Keep
@Serializable
data class CompactPlaylist(
    val title: String,
    val description: String,
    val videoCount: String,
    val playlistId: String,
    val featuredImage: String
)

@Keep
@Serializable
data class SimplePlaylistSection(
    val title: String? = null,
    val thumbnail: String? = null,
    val index: Int = 0,
    val playlists: List<SimplePlaylist>? = null
)

@Keep
@Serializable
data class SimplePlaylist(
    val title: String? = null,
    val description: String? = null,
    val videoCount: String? = null,
    val playlistId: String? = null,
    val featuredImage: String? = null,
    val updatedTime: String? = null,
    val channelId: String? = null,
    val channelName: String? = null
)

@Keep
@Serializable
data class VideoListSection(
    val title: String? = null,
    val thumbnail: String? = null,
    val index: Int = 0,
    val videos: List<VideoInfo>? = null
)

@Keep
@Serializable
data class VideoInfo(
    val video: MousikiVideoRS,
    val owner: VideoOwner? = null
)

@Keep
@Serializable
data class VideoOwner(
    val channelId: String? = null,
    val title: String? = null
)

@Keep
fun MousikiVideoRS.toTrack(): MusicTrack {
    return MusicTrack(
        youtubeId = videoId.orEmpty(),
        title = title.orEmpty(),
        duration = MusicTrack.toYoutubeDuration(duration.orEmpty())
    )
}