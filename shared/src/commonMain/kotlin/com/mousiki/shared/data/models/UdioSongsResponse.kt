package com.mousiki.shared.data.models

/**
 * Created by Fayssel Yabahddou on 16/6/2024.
 */

import com.mousiki.shared.Parcelable
import com.mousiki.shared.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("song")
@Parcelize
data class UdioSong(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val artist: String,
    val title: String,
    val created_at: String,
    val generation_id: String,
    @SerialName("image_path")
    val imageUrl: String,
    val lyrics: String,
    val prompt: String,
    val likes: Long,
    val plays: Long,
    val published_at: String?,
    @SerialName("song_path")
    val songPath: String,
    val tags: List<String>,
    val duration: Double,
    val video_path: String? = "",
    val finished: Boolean,
    val liked: Boolean,
    val disliked: Boolean,
    val publishable: Boolean
) : Parcelable

@Serializable
data class SongsResponse(
    val songs: List<UdioSong>
)

@Serializable
data class SearchResponse(
    val data: List<UdioSong>,
    val cursor : Long,
    val nextCursor : Long,
)