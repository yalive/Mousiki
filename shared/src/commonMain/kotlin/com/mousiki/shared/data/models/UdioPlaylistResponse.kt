package com.mousiki.shared.data.models

import com.mousiki.shared.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Keep
@Serializable
data class UdioPlaylistResponse(

	@SerialName("playlists")
	val playlists: List<PlaylistsItem?>? = null
)

@Keep
@Serializable
data class PlaylistsItem(

	@SerialName("user_id")
	val userId: String? = null,

	@SerialName("song_list")
	val songList: List<String?>? = null,

	@SerialName("image_path")
	val imagePath: String? = null,

	@SerialName("name")
	val name: String? = null,

	@SerialName("id")
	val id: String? = null,

	@SerialName("published_at")
	val publishedAt: String? = null,

	@SerialName("followed")
	val followed: Boolean? = null,

	@SerialName("username")
	val username: String? = null
)
