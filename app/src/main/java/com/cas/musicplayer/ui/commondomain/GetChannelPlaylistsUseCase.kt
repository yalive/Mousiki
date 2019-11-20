package com.cas.musicplayer.ui.commondomain

import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.net.Result
import com.cas.musicplayer.net.map
import com.cas.musicplayer.repository.PlaylistRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-19.
 ***************************************
 */
class GetChannelPlaylistsUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {

    suspend operator fun invoke(channelId: String): Result<List<Playlist>> {
        return repository.getPlaylists(channelId).map { playlists ->
            playlists.filter { it.itemCount > 0 }
        }
    }
}